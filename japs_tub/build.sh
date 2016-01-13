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
# $Id: build.sh,v 1.3 2009/10/13 22:13:35 rassy Exp $

# Build script for japs

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.3 $'
readonly user_config_file=build.conf

# Set variable defaults:
prefix=${MM_BUILD_PREFIX:-/usr/local}
task=process_targets
tomcat_home=$TOMCAT_HOME
webapp_name=cocoon
japs_dir=''
jdk_apidocs_url=$JAVA_HOME/docs/api
cocoon_apidocs_url=http://xml.apache.org/cocoon/apidocs
avalon_framework_apidocs_url=''
excalibur_xmlutil_apidocs_url=''

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions force,targets,ignore-deps,help,version,vars,check,release:,cvsroot:,javac-verbose,javac-deprecation,javac-debug \
  --options f,t,D,h,v \
  -- \
  "$@"`
if [ $? -ne 0 ] ; then exit 1 ; fi
eval set -- "$params"
while true ; do
  case "$1" in
    --targets|-t) task=show_targets ; shift ;;
    --ignore-deps|-D) ignore_deps=ignore_deps ; shift ;;
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --vars) task=print_variables ; shift ;;
    --check) task=check ; shift ;;
    --force|-f) force=force ; shift ;;
    --release) release="$2" ; shift 2 ;;
    --cvsroot) cvsroot="$2" ; shift 2 ;;
    --javac-verbose) javac_verbose=enabled ; shift ;;
    --javac-deprecation) javac_deprecation=enabled ; shift ;;
    --javac-debug) javac_debug=enabled ; shift ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'japs-lib'}

# Store the current directory:
base_dir=`pwd`

# Store the pid of this process
pid=$$

# Set the variables if not set already:
java_lib_dir=$prefix/lib/java
doc_dir=$prefix/share/doc/japs
webapp_root=$tomcat_home/webapps/$webapp_name
webapp_webinf=$webapp_root/WEB-INF
webapp_lib_dir=$webapp_webinf/lib
japs_dir=${japs_dir:-$base_dir/../japs}
version_file=VERSION
avalon_framework_api_jar=avalon-framework-api-4.3.jar
avalon_framework_impl_jar=avalon-framework-impl-4.3.jar
datasheet_apidocs_url=${datasheet_apidocs_url:-$prefix/doc/japs_client/apidocs}

# Japs Java library:
japs_tub_jar="mumie-japs-tub.jar"

# Libraries to install/uninstall. This variable contains libraries which are
# included in this packege or are created by the build.
lib_install_files="
  $japs_tub_jar
"

# --------------------------------------------------------------------------------
# Utility functions
# --------------------------------------------------------------------------------

# Prints the time of last modification of a file to stdout. The time is expressed
# as seconds since Epoch
function mtime
  {
    run_cmd stat -c %Y $1
  }

# Returns all Java classes which are below a certain package path and must be rebuild
function get_java_source_files
  {
    local package_path=$1
    local src_dir=$base_dir/java/src
    local target_dir=$base_dir/java/classes
    local saved_dir=`pwd`
    run_cmd cd $src_dir/$package_path
    local source_file
    for source_file in `find -name "*.java"` ; do
      local target_file=$target_dir/$package_path/${source_file%java}class
      if [ "$force" ] || \
         [ ! -e "$target_file" ] || \
         [ `mtime $source_file` -gt `mtime $target_file` ]
      then
        echo $source_file
      fi
    done
    run_cmd cd $saved_dir
  }

# Aborts with an error message
function error
  {
    echo "$program_name: ERROR: $*"
    echo
    kill -9 $pid
    # exit 1
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

# Calls ant:
function run_ant
  {
    local ant_cmd="\
      ant -e \
          -Djaps-tub.install.prefix=$prefix \
          -Djaps-tub.install.lib.java.dir=$java_lib_dir \
          -Djaps-tub.japs.dir=$japs_dir \
          -Djaps-tub.tomcat.dir=$tomcat_home \
          -Djaps-tub.webapp.dir=$webapp_root \
          -Djaps-tub.apidocs.jdk.url=$jdk_apidocs_url \
          -Djaps-tub.apidocs.japs-datasheet.url=$datasheet_apidocs_url \
          -Djaps-tub.apidocs.cocoon.url=$cocoon_apidocs_url \
          -Djaps-tub.apidocs.avalon-framework.url=$avalon_framework_apidocs_url \
          -Djaps-tub.apidocs.excalibur-xmlutil.url=$excalibur_xmlutil_apidocs_url"
    [ "$force" ] && ant_cmd="$ant_cmd -Djaps-tub.force=yes"
    [ "$javac_verbose" ] && ant_cmd="$ant_cmd -Djaps-tub.javac.verbose=yes"
    [ "$javac_deprecation" ] && ant_cmd="$ant_cmd -Djaps-tub.javac.deprecation=yes"
    [ "$javac_debug" ] && ant_cmd="$ant_cmd -Djaps-tub.javac.debug=yes"
    [ -e "$base_dir/$version_file" ] &&
      ant_cmd="$ant_cmd -Djaps-tub.version=`cat $base_dir/$version_file`"
    run_cmd $ant_cmd "$@"
  }

# Sets the classpath:
function set_classpath
  {
    local part
    local classpath
    for part in $* ; do
      if [ "$classpath" ] ; then
        classpath="$classpath:$part"
      else
       classpath=$part
      fi
    done
    run_cmd export CLASSPATH=$classpath
  }

# Quotes the character '/' with a backslash. Used in sed input.
function quote
  {
    echo "$@" | sed 's/\//\\\//g'
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Creates the Japs Java library
function create_japs_tub_lib
  {
    echo
    echo "======================================================================"
    echo "Creating Japs-TUB lib"
    echo "======================================================================"
    echo
    local jar_file=$base_dir/java/lib/$japs_tub_jar
    [ "$tomcat_home" ] || error "tomcat_home not set"
    if [ "`get_java_source_files net/mumie/cocoon`" ] ; then
      run_ant -buildfile tools/build.xml
    fi
    echo "$program_name: japs-tub lib done"
    create_japs_tub_lib_done=done
  }

# Copies the Java libraries to the WEB-INF/lib directory of the Cocoon webapp in Tomcat.
# Some of the libraries are copied from the java/lib directory of the build tree, some
# are copied from its usual installation location, i.e., $prefix/lib/java

function install_libs
  {
    echo
    echo "======================================================================"
    echo "Installing libraries"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/java/lib
    echo "$program_name: changed into java/lib"
    run_cmd cp -v $lib_install_files $webapp_lib_dir
    run_cmd cd $base_dir
    echo "$program_name: installing libs done"
    install_libs_done=done
  }

# Removes the Japs Java library (mumie-japs.jar) and the Mumie SQL Java library
# (mumie-sql.jar) from the WEB-INF/lib directory of the Cocoon webapp in Tomcat.

function uninstall_libs
  {
    echo
    echo "======================================================================"
    echo "Uninstalling libraries"
    echo "======================================================================"
    echo
    run_cmd cd $webapp_lib_dir
    echo "$program_name: changed into $webapp_lib_dir"
    run_cmd rm -fv $lib_install_files
    run_cmd cd $base_dir
    echo "$program_name: uninstalling libs done"
    uninstall_libs_done=done
  }

# Creates the API documentation for the Japs-TUB Java library and the build tools
function create_java_apidocs
  {
    echo
    echo "======================================================================"
    echo "Creating Java apidocs"
    echo "======================================================================"
    echo
    run_ant -buildfile tools/ant/build.xml apidocs
    echo "$program_name: java apidocs done"
    create_java_apidocs_done=done
  }

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
    local dist_name="japs_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag japs
    run_cmd mv -v japs $dist_name
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
    local target
    for target in $targets ; do
      case $target in
        japs-lib)
          create_japs_tub_lib ;;
        install-libs)
          install_libs ;;
        uninstall-libs)
          uninstall_libs ;;
        all)
          create_japs_tub_lib ;;
        apidocs)
          create_java_apidocs ;;
        dist)
          create_dist ;;
        *)
          echo "ERROR: Unknown target: $target"
          exit 3 ;;
      esac
    done
    echo
    echo "$program_name: BUILD DONE"
    echo
  }

function print_variables
  {
    cat <<EOF
cocoon_apidocs_url       = $cocoon_apidocs_url
cvsroot                  = $cvsroot
datasheet_apidocs_url    = $datasheet_apidocs_url
doc_dir                  = $doc_dir
excalibur_xmlutil_apidocs_url = $excalibur_xmlutil_apidocs_url
force                    = $force
ignore_deps              = $ignore_deps
japs_dir                 = $japs_dir
java_lib_dir             = $java_lib_dir
javac_debug              = $javac_debug
javac_deprecation        = $javac_deprecation
javac_verbose            = $javac_verbose
jdk_apidocs_url          = $jdk_apidocs_url
prefix                   = $prefix
release                  = $release
targets                  = $targets
task                     = $task
tomcat_home              = $tomcat_home
webapp_lib_dir           = $webapp_lib_dir
webapp_root              = $webapp_root
webapp_webinf            = $webapp_webinf
EOF
  }

function show_targets
  {
cat <<EOF
japs-lib            - Creates the Japs-TUB Java library
install-libs        - Installs the Japs-TUB Java library in the WEB-INF/lib
                      directory
uninstall-libs      - Uninstalls the Japs-TUB Java library
all                 - Same as 'japs-lib'
dist                - Creates a distribution
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the japs package, or parts of it. What is
  actually done is controlled by TARGETS, which is a list of keywords called
  targets. Type ./build.sh -t to get a list of all targets. The default target
  is "japs-lib"; it is assumed if no targets are specified.
Options:
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
  --check
      Performs some checks
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
