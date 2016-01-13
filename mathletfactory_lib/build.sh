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
# $Id: build.sh,v 1.11 2009/07/17 12:55:26 linges Exp $

# Build script for the mathlet factory

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.11 $'
readonly user_config_file=build.conf

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions force,targets,ignore-deps,help,version,vars,release:,cvsroot:,javac-verbose,javac-deprecation,javac-debug \
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
checkin_root=${checkin_root:-${MM_CHECKIN_ROOT:-$HOME/mumie/checkin}}
java_home=${java_home:-$JAVA_HOME}
lib_dir=$prefix/lib
java_lib_dir=$lib_dir/java
signjar_alias=${signjar_alias:-mumie}
signjar_storepass=${signjar_storepass:-japsen}
signjar_keystore=${signjar_keystore:-~/.keystore}
doc_dir=$prefix/share/doc/mathlet_factory
jdk_apidocs_url=${jdk_apidocs_url:-http://java.sun.com/javase/6/docs/api}
version_file=VERSION
section_path=system/libraries
task=${task:-process_targets}

# Names of the libraries:
lib_names="
mathletfactory_base
mathletfactory_graphics2d
mathletfactory_java3d
mathletfactory_jreality
"

# Store the current directory:
base_dir=`pwd`

# --------------------------------------------------------------------------------
# Utility functions
# --------------------------------------------------------------------------------

# Prints the time of last modification of a file to stdout. The time is expressed
# as seconds since Epoch
function mtime
  {
    run_cmd stat -c %Y $1
  }

# Compares a target and a source file and prints "needs_build" to stdout if the
# target file needs to be (re)build; otherwise, prints the empty string.
# Usage: needs_build SOURCE_FILE TARGET_FILE
function needs_build
  {
    local source_file=$1
    local target_file=$2
    if [ "$force" ] || \
       [ ! -e "$target_file" ] || \
       [ `stat -c %Y "$source_file"` -gt `stat -c %Y "$target_file"` ]
    then
      echo "needs_build"
    else
      echo ""
    fi
  }

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

# Runs ant
function run_ant
  {
    local ant_cmd="\
      ant -e \
          -Dsignjar.alias=$signjar_alias \
          -Dsignjar.storepass=$signjar_storepass \
          -Dsignjar.keystore=$signjar_keystore \
          -Djdk.apidocs.url=$jdk_apidocs_url " 
    [ "$javac_verbose" ] && ant_cmd="$ant_cmd -Djavac.verbose=yes"
    [ "$javac_deprecation" ] && ant_cmd="$ant_cmd -Djavac.deprecation=yes"
    [ "$javac_debug" ] && ant_cmd="$ant_cmd -Djavac.debug=yes"
    [ -e "$base_dir/$version_file" ] &&
      ant_cmd="$ant_cmd -Dversion=`cat $base_dir/$version_file`"
    run_cmd $ant_cmd "$@"
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Creates the mathlet factory
function create_libs
  {
    echo
    echo "======================================================================"
    echo "Creating libraries"
    echo "======================================================================"
    echo

    local module_checkin_dir=$base_dir/checkin/$section_path
    local lib_name

    if [ "$force" ] ; then
      run_cmd rm -rf build/class
      run_cmd rm -rf build/lib
      for lib_name in $lib_names ; do
        run_cmd rm -f $module_checkin_dir/jar_${lib_name}.content.jar
      done
      rm -f $module_checkin_dir/jar_jreality.content.jar
    fi

    # Call Ant:
    run_ant -buildfile tools/build.xml build.all

    run_cmd cd $module_checkin_dir
    echo "$program_name: changed into checkin/$section_path"

    # Create content file links in local checkin dir if necessary:
    for lib_name in $lib_names ; do
      local content_file=jar_${lib_name}.content.jar
      if [ ! -h "jar_${lib_name}.content.jar" ] ; then
        [ -e $content_file ] ||
          run_cmd ln -vs ../../../build/lib/mumie_${lib_name}.jar $content_file
      fi
    done

    # JReality:
    [ -e jar_jreality.content.jar ] || \
      run_cmd ln -vs ../../../util/lib/jReality.jar jar_jreality.content.jar

    run_cmd cd $base_dir
    echo "$program_name: creating lib done"
    create_libs_done=done
  }

# Creates the documentation
function create_doc
  {
    echo
    echo "======================================================================"
    echo "Creating doc"
    echo "======================================================================"
    echo

    # Call Ant to create the apidocs:
    run_ant -buildfile tools/build.xml do.apidoc

    run_cmd cd $base_dir
    echo "$program_name: creating doc done"
    create_doc_done=done
  }

# Installs the libraries
function install_libs
  {
    echo
    echo "======================================================================"
    echo "Installing libraries"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/build/lib
    echo "$program_name: changed into build/lib"
    run_cmd mkdir -pv $java_lib_dir
    local lib_name
    for lib_name in $lib_names ; do
      run_cmd cp -v mumie_${lib_name}.jar $java_lib_dir
    done
    run_cmd cd $base_dir/util/lib
    echo "$program_name: changed into util/lib"
    run_cmd cp -v jReality.jar $java_lib_dir
    run_cmd cd $base_dir
    echo "$program_name: installing libraries done"
    install_libs_done=done
  }

# Uninstalls the libraries
function uninstall_libs
  {
    echo
    echo "======================================================================"
    echo "Uninstalling libraries"
    echo "======================================================================"
    echo
    if [ -e $java_lib_dir ] ; then
      run_cmd cd $java_lib_dir
      echo "$program_name: changed into $java_lib_dir"
      for lib_name in $lib_names ; do
        run_cmd rm -vf mumie_${lib_name}.jar
      done
      run_cmd rm -vf jReality.jar
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
    echo "$program_name: uninstalling libs done"
    uninstall_libs_done=done
  }

# "Mounts" the meta and content files of the libraries to the global checkin tree
function mount_checkin
  {
    echo
    echo "======================================================================"
    echo "Mounting checkin"
    echo "======================================================================"
    echo
    local checkin_dir=$checkin_root/$section_path
    [ -e "$checkin_dir" ] || run_cmd mkdir -vp $checkin_dir
    run_cmd cd $checkin_dir
    echo "$program_name: changed into $checkin_dir"
    local build_checkin_dir=$base_dir/checkin/$section_path
    [ -e .meta.xml ] || run_cmd cp -v $build_checkin_dir/.meta.xml .
    local lib_name
    local lib_file
    for lib_name in $lib_names jreality mathlet_factory; do
      for lib_file in jar_${lib_name}.meta.xml jar_${lib_name}.content.jar ; do
        [ "$force" ] && run_cmd rm -vf $lib_file
        [ -e $lib_file ] || [ -h $lib_file ] || run_cmd ln -vs $build_checkin_dir/$lib_file
      done
    done
    run_cmd cd $base_dir
    echo "$program_name: mounting checkin done"
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
    local dist_name="mathletfactory_lib_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag mathletfactory_lib
    run_cmd mv -v mathletfactory_lib $dist_name
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
        libs)
          create_libs ;;
        all)
          create_libs ;;
        doc)
          create_doc ;;
        install-libs)
          install_libs ;;
        install)
          install_libs ;;
        uninstall-libs)
          uninstall_libs ;;
        uninstall)
          uninstall_libs ;;
        mount-checkin)
          mount_checkin ;;
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
cvsroot           = $cvsroot
doc_dir           = $doc_dir
checkin_root      = $checkin_root
doc_dir           = $doc_dir
force             = $force
gcc_cmd           = $gcc_cmd
gcc_options       = $gcc_options
ignore_deps       = $ignore_deps
java_home         = $java_home
java_lib_dir      = $java_lib_dir
jdk_apidocs_url   = $jdk_apidocs_url
lib_dir           = $lib_dir
prefix            = $prefix
release           = $release
targets           = $targets
task              = $task
javac_verbose     = $javac_verbose
javac_deprecation = $javac_deprecation
javac_debug       = $javac_debug
signjar_alias     = $signjar_alias
signjar_storepass = $signjar_storepass
EOF
  }

function show_targets
  {
cat <<EOF
libs               Creates the mathlet factory libraries
all                Same as 'libs'
doc                Creates the documentation
install-libs       Installs the mathlet factory libraries
install            Same as 'install-libs'
uninstall-libs     Uninstalls the mathlet factory libraries
uninstall          Same as 'uninstall-libs'
mount-checkin      Adds the mathlet factory libraries to the checkin files
dist               Creates a distribution
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs mathletfactory_lib, or parts of it. What is
  actually done is controlled by TARGETS, which is a list of keywords called
  targets. Type ./build.sh -t to get a list of all targets. The default target
  is "all"; it is assumed if no targets are specified.
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
