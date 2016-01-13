#! /bin/bash

# The MIT License (MIT)
# 
# Copyright (c) 2010 Technische Universitaet Berlin
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

# Abort on error:
set -e

# Fixed variables (constants):
readonly base_dir=`pwd`
readonly user=`whoami`
readonly config_file=create_test_server.conf

# Customizable variables:
if [ "$user" = root ] ; then
  prefix=/usr/local
  checkin_root=/srv/mumie/checkin
  default_url_prefix=http://${HOST}.math.tu-berlin.de/cocoon
else
  prefix=$HOME
  checkin_root=$HOME/mumie/checkin
  default_url_prefix=http://${HOST}.math.tu-berlin.de:8080/cocoon
fi
src_dir=$prefix/src
target=build_server

# Source config file:
[ -e "$config_file" ] && source "$config_file"

# Process command line parameters:
params=`getopt \
  --longoptions help,version,vars,release: \
  --options h,v \
  -- \
  "$@"`
if [ $? -ne 0 ] ; then exit 1 ; fi
eval set -- "$params"
while true ; do
  case "$1" in
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --vars) task=print_variables ; shift ;;
    --release) release="$2" ; shift 2 ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Environment variables:
export MM_BUILD_PREFIX=$prefix
export MM_CHECKIN_ROOT=$checkin_root
export PATH=$prefix/bin:$PATH

# Aborts with an error message.
function error
  {
    echo "ERROR: $*" 1>&1
    echo 1>&1
    exit 1
  } 

# Prints a warning to stderr, but does not abort the program.
function warning
  {
    echo "WARNING: $*" 1>&1
  } 

# Creates the package directory from the package name and prints it to stdout. Synopsis:
#
#  get_package_dir <package_name>
#
# where <package_name> is the name of the package. If a release is specified, i.e,
# if the variable 'release' is non-void, the directory is
#
#  <src_dir>/<package_name>_<relelase>
#
# where <relelase> is the value of 'release'. If no release is specified, the directory
# is
#
#  <src_dir>/<package_name>
#
# In both cases, <src_dir> is the value of the variable 'src_dir'.

function get_package_dir
  {
    local package_dir=$src_dir/$1
    [ "$release" ] && package_dir="${package_dir}_${release}"
    echo $package_dir
  }

# Builds a package. The package name must be specified as a parameter. Note that this
# function is not suitable for building the 'japs' package. There is a special function
# for the 'japs' package called 'build_japs'.

function build_package
  {
    echo "# **********************************************************************"
    echo "# $package"
    echo "# **********************************************************************"
    echo

    # Package to build:
    local package=$1
    shift

    # Targets:
    local targets=$@
    [ "$targets" ] || targets='all install'

    # Config file
    local conf_file=$base_dir/${package}_build.conf

    # Build:
    cd `get_package_dir $package`
    [ -e $conf_file ] && cp $conf_file build.conf
    echo "Variables:"
    ./build.sh --vars
    echo
    ./build.sh --vars
    ./build.sh $targets
    cd $base_dir
  }

# Builds the 'japs' package
function build_japs
  {
    echo "# **********************************************************************"
    echo "# japs"
    echo "# **********************************************************************"
    echo

    # Config file
    local conf_file=$base_dir/japs_build.conf

    # Source Bash settings for servers:
    local japs_env_conf_dir=$src_dir/japs_env/conf/bash
    source $japs_env_conf_dir/apache_bashrc
    source $japs_env_conf_dir/postgres_bashrc
    source $japs_env_conf_dir/tomcat_bashrc

    # Build:
    cd `get_package_dir japs`
    if [ -e $conf_file ] ; then
      cp $conf_file build.conf
    else
      echo "url_prefi=$default_url_prefix" > build.conf
    fi
    echo "Variables:"
    ./build.sh --vars
    echo
    postgres start
    ./build.sh all-step1
    apache start
    tomcat start
    ./build.sh all-step2
    ./build.sh all-step3
    tomcat stop
    apache stop
    postgres stop
    cd $base_dir
  }

# Builds the entire server
function build_server
  {
    build_package japs_env           all
    build_package mmjutil
    build_package mmjsql
    build_package mmjaps_client      all install install-jcookie mount-checkin
    build_package mmjaps_datasheet   all install mount-checkin
    build_package mmjaps_checkin     all install mount-checkin
    build_package mathletfactory_lib all install mount-checkin
    build_japs
  }

# Prints all variables to stdout
function print_variables
  {
    cat <<EOF
prefix             = $prefix
checkin_root       = $checkin_root
default_url_prefix = $default_url_prefix
src_dir            = $src_dir
release            = $release
EOF
  }

# Prints the version of this script to stdout
function show_version
  {
    echo '$version'
  }

$task
