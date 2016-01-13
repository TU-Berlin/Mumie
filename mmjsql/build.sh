#!/bin/bash

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

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: build.sh,v 1.3 2008/01/14 11:14:15 rassy Exp $

# Build script for mmjsql

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.3 $'
readonly user_config_file=build.conf

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions prefix:,force,targets,ignore-deps,help,version,vars,release:,cvsroot:,javac-verbose,javac-deprecation,javac-debug \
  --options f,t,D,h,v \
  -- \
  "$@"`
if [ $? -ne 0 ] ; then exit 1 ; fi
eval set -- "$params"
while true ; do
  case "$1" in
    --prefix) prefix="$2" ; shift 2 ;;
    --targets|-t) task=show_targets ; shift ;;
    --ignore-deps|-D) ignore_deps=ignore_deps ; shift ;;
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --vars) task=print_variables ; shift ;;
    --force|-f) force=force ; shift ;;
    --release) release="$2" ; shift 2 ;;
    --cvsroot) cvsroot="$2" ; shift 2 ;;
    --javac-verbose) javac_verbose=enabled ; shift ;;
    --javac-deprecation) javac_deprecation=enabled ; shift ;;
    --javac-debug) javac_debug=enabled ; shift ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Set the variables if not set already:
prefix=${prefix:-${MM_BUILD_PREFIX:-/usr/local}}
java_lib_dir=$prefix/lib/java
doc_dir=$prefix/share/doc/mmjsql
version_file=VERSION
task=${task:-process_targets}

# Store the current directory:
base_dir=`pwd`

# Java libraries to install/uninstall:
java_lib_install_files="
mume-sql.jar"

# --------------------------------------------------------------------------------
# Utility functions
# --------------------------------------------------------------------------------

# Pasted from build_tools/lib/bash/build.inc, revision 1.2:

# Aborts with an error message
function error
  {
    echo "ERROR: $*"
    echo
    exit 1
  } 

# Checks the exit code of the last command, terminates with an error message if the
# exit code is not 0
function check_exit_code
  {
    local exit_code=$?
    [ "$exit_code" -eq 0 ] || error "Last command returned with code $exit_code"
  }

# Runs a command, checks the exit code, terminates with an error message if the exit
# code is not 0
function run_cmd
  {
    "$@"
    check_exit_code
  }

# Non included in build_tools/lib/bash/build.inc, revision 1.2:

function run_ant
  {
    local ant_cmd="\
      ant -e \
          -Dmmjsql.install.prefix=$prefix \
          -Dmmjsql.install.lib.java.dir=$java_lib_dir \
          -Dmmjsql.install.doc.mmjsql.dir=$doc_dir"
    [ "$force" ] && ant_cmd="$ant_cmd -Dmmjsql.force=yes"
    [ "$javac_verbose" ] && ant_cmd="$ant_cmd -Dmmjsql.javac.verbose=yes"
    [ "$javac_deprecation" ] && ant_cmd="$ant_cmd -Dmmjsql.javac.deprecation=yes"
    [ "$javac_debug" ] && ant_cmd="$ant_cmd -Dmmjsql.javac.debug=yes"
    [ -e "$base_dir/$version_file" ] &&
      ant_cmd="$ant_cmd -Dmmjsql.version=`cat $base_dir/$version_file`"
    run_cmd $ant_cmd "$@"
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Creates the distribution
function create_dist
  {
    echo
    echo "======================================================================"
    echo "Creating distribution"
    echo "======================================================================"
    echo
    [ "$release" ] || error "No release specified"
    run_cmd cd $base_dir
    run_cmd mkdir -pv dist
    run_cmd cd dist
    echo "$program_name: Changed into dist/"
    echo "$program_name: Checking-out release"
    local dist_name="mmjsql_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag mmjsql
    run_cmd mv -v mmjsql $dist_name
    echo "$program_name: Creating version file"
    run_cmd echo $release > $dist_name/$version_file
    echo "$program_name: Creating tgz"
    run_cmd tar czf $archive $dist_name
    run_cmd cd $base_dir
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

# Processes the targets
function process_targets
  {
    if [ "$targets" == dist ] ; then
      create_dist
    elif [ "`echo $targets | fgrep dist`" ] ; then
      error "You can not call 'dist' together with other targets"
    else
      echo
      echo "======================================================================"
      echo "Calling ant"
      echo "======================================================================"
      echo
      run_ant $targets
    fi
    echo
    echo "$program_name: Done"
    echo
  }

function print_variables
  {
    cat <<EOF
cvsroot           = $cvsroot
doc_dir           = $doc_dir
force             = $force
ignore_deps       = $ignore_deps
java_lib_dir      = $java_lib_dir
prefix            = $prefix
release           = $release
targets           = $targets
task              = $task
javac_verbose     = $javac_verbose
javac_deprecation = $javac_deprecation
javac_debug       = $javac_debug
EOF
  }

function show_targets
  {
cat <<EOF
all                Compiles and jars the classes
classes            Compiles the classes
jar                Jars the classes
apidocs            Creates the API docs
clear-classes      Deletes the classes
clear-apidocs      Deletes the API docs
install-jar        Copies the Jar archive to its installation location
install-apidocs    Copies the API documentation to their installation location
uninstall-apidocs  Deletes the API documentation from their installation
                   location
uninstall-jar      Deletes the Jar archive from its installation location
uninstall          Uninstalls all
dist               Creates a distribution
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the mmjsql package, or parts of it. What is
  actually done is controlled by TARGETS, which is a list of keywords called
  targets. Type ./build.sh -t to get a list of all targets. The default target
  is "all"; it is assumed if no targets are specified.
Options:
  --prefix=PREFIX
      The root of the installation directory. Default is /usr/local.
  --targets, -t
      List all targets
  --force, -f
      Create files even if they are up-to-date.
  --ignore-deps, -D
      Ignore target dependencies. If a target is build with this option,
      then targets required by this target are not build automatically.
  --javac-verbose
      Turns the "verbose" flag on when compiling the java sources.
  --javac-debug
      Turns the "debug" flag on when compiling the java sources.
  --javac-deprecation
      Turns the "deprecation" flag on when compiling the java sources.
  --release=VERSION_NUMBER
      Set the release for the distribution to build. In effect only with
      the "dist" target, otherwise ignored.
  --cvsroot=CVSROOT
      Set the cvs root for retrieving the distribution to build. In effect
      only with the "dist" target, otherwise ignored. If not set, the
      environment variable \$CVSROOT is used
  --vars
      Prints the build variables to stdout
  --help, -h
      Print this help text and exit.
  --version, -v
      Print version information and exit.
EOF
  }

function show_version
  {
    echo $program_version
  }

$task
