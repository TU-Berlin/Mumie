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
# $Id: build.sh,v 1.7 2008/09/23 11:47:16 rassy Exp $

# Build script for mmckxml

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.7 $'
readonly user_config_file=build.conf

unset prefix java_home jdk_apidocs_url task

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions force,targets,ignore-deps,vars,help,version \
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
    --vars) task=print_variables ; shift ;;
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --force|-f) force=force ; shift ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Set the variables if not set already:
prefix=${prefix:-${MM_BUILD_PREFIX:-/usr/local}}
exec_dir=$prefix/bin
java_home=${java_home:-$JAVA_HOME}
java_lib_dir=$prefix/lib/java
doc_dir=$prefix/share/doc/mmckxml
jdk_apidocs_url=${jdk_apidocs_url:-$java_home/docs/api}
version_file=VERSION
task=${task:-process_targets}

# Store the current directory:
base_dir=`pwd`

# Executables to install/uninstall:
execs_install_files="
  mmckxml
  mmvalxml"

# Java class files to install/uninstall:
java_lib_install_files='
  mumie-ckxml.jar'

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
          -Dmmckxml.install.lib.java.dir=$java_lib_dir \
          -Dmmckxml.jdk.apidocs.url=$jdk_apidocs_url " 
    [ "$force" ] && ant_cmd="$ant_cmd -Dmmckxml.force=yes"
    [ "$javac_verbose" ] && ant_cmd="$ant_cmd -Dmmckxml.javac.verbose=yes"
    [ "$javac_deprecation" ] && ant_cmd="$ant_cmd -Dmmckxml.javac.deprecation=yes"
    [ "$javac_debug" ] && ant_cmd="$ant_cmd -Dmmckxml.javac.debug=yes"
    [ -e "$base_dir/$version_file" ] &&
      ant_cmd="$ant_cmd -Dmmckxml.version=`cat $base_dir/$version_file`"
    run_cmd $ant_cmd "$@"
  }

# Returns all Java classes which are below a certain package path and must be rebuild
function get_java_source_files
  {
    local package_path=$1
    local src_dir=$base_dir/src/java
    local target_dir=$base_dir/lib/java/classes
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

# Compares last modification times and returns changed sources. If $force is
# set, all sources are returned regardless if they have changed or not.
# Usage: get_source_files SOURCE_SUFFIX TARGET_SUFFIX TARGET_DIR SOURCE_FILES
function get_source_files
  {
    local source_suffix=$1
    local target_suffix=$2
    local target_dir=$3
    shift; shift; shift;
    local source_file
    for source_file in "$@"
    do
      local target_file=${target_dir}${source_file%$source_suffix}${target_suffix}
      if [ "$force" ] || \
         [ ! -e "$target_file" ] || \
         [ `stat -c %Y "$source_file"` -gt `stat -c %Y "$target_file"` ]
      then
        echo $source_file
      fi
    done
  }

# Returns all target files for a given list of source files.
# Usage: get_target_files SOURCE_SUFFIX TARGET_SUFFIX TARGET_DIR SOURCE_FILES
function get_target_files
  {
    local source_suffix=$1
    local target_suffix=$2
    local target_dir=$3
    shift; shift; shift;
    local source_file
    for source_file in "$@"
    do
      echo ${target_dir}${source_file%$source_suffix}${target_suffix}
    done
  }

# Copies files to a directory, preserves subdirectory struture
# Usage: cp_to_dir DIR FILE1 FILE2 ...
# Copies FILE1 FILE2 ... to DIR, with their relative paths. Subdirectories are created
# if necessary
function cp_to_dir
  {
    local dir=$1
    shift
    local file
    for file in "$@" ; do
      local path=`dirname $file | sed s/^\\.\\\\///`
      if [ "$path" != '.' ] ; then
        run_cmd mkdir -vp $dir/$path
        run_cmd cp -v $file $dir/$path
      else
        run_cmd mkdir -vp $dir
        run_cmd cp -v $file $dir
      fi
    done
  }

# Quotes the character / with a slash. Used in sed input.
function quote
  {
    echo "$@" | sed 's/\//\\\//g'
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Creates the java libraries
function create_java_libs
  {
    echo
    echo "======================================================================"
    echo "Creating Java libraries"
    echo "======================================================================"
    echo
    if [ "`get_java_source_files net/mumie/ckxml`" ] ; then
      run_ant -buildfile tools/build.xml
    fi
    run_cmd cd $base_dir
    echo "$program_name: creating java libs done"
    create_java_libs_done=done
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
    run_ant -buildfile tools/build.xml apidocs

    run_cmd cd $base_dir
    echo "$program_name: creating doc done"
    create_doc_done=done
  }

# Installs the Java libraries
function install_java_libs
  {
    echo
    echo "======================================================================"
    echo "Installing Java libraries"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/java
    echo "$program_name: changed into lib/java"
    run_cmd mkdir -pv $java_lib_dir
    run_cmd cp -v $java_lib_install_files $java_lib_dir
    run_cmd cd $base_dir
    echo "$program_name: installing java libs done"
    install_java_libs_done=done
  }

# Uninstalls the Java libraries
function uninstall_java_libs
  {
    echo
    echo "======================================================================"
    echo "Uninstalling Java libraries"
    echo "======================================================================"
    echo
    if [ -e $java_lib_dir ] ; then
      run_cmd cd $java_lib_dir
      echo "$program_name: changed into $java_lib_dir"
      run_cmd rm -vf $java_lib_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
    echo "$program_name: uninstalling java libs done"
    uninstall_java_libs_done=done
  }

# Creates executables
function create_executables
  {
    echo
    echo "======================================================================"
    echo "Creating executables"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/bin
    local templates=`get_source_files .tpl "" "" *.tpl`
    if [ "$templates" ] ; then
      for template in $templates ; do
        target=${template%.tpl}
        echo "$program_name: Creating $target"
        run_cmd sed s/\@java-lib-dir\@/`quote $java_lib_dir`/g $template > $target
        run_cmd chmod u+x $target
      done
    else
      echo "$program_name: all targets up-to-date"
    fi
    create_executables_done=done
    run_cmd cd $base_dir
  }

# Deletes the executables created by create_executables
function clear_executables
  {
    echo
    echo "======================================================================"
    echo "Removing created executables"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/bin
    run_cmd rm -vf `get_target_files .tpl "" "" *.tpl`
    run_cmd cd $base_dir
  }

# Copies the executables to their installation location
function install_executables
  {
    [ "$create_executables_done" ] || [ "$ignore_deps" ] || create_executables
    echo
    echo "======================================================================"
    echo "Installing executables"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/bin
    echo "$program_name: changed into bin/"
    run_cmd mkdir -pv $exec_dir
    cp_to_dir $exec_dir $execs_install_files
    run_cmd cd $base_dir
  }

# Removes the executables from their installation location
function uninstall_executables
  {
    echo
    echo "======================================================================"
    echo "Uninstalling executables"
    echo "======================================================================"
    echo
    if [ -e $exec_dir ] ; then
      run_cmd cd $exec_dir
      echo "$program_name: changed into $exec_dir"
      run_cmd rm -vf $execs_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Prints all variables to stdout
function print_variables
  {
    cat <<EOF
exec_dir          = $exec_dir
force             = $force
ignore_deps       = $ignore_deps
java_home         = $java_home
java_lib_dir      = $java_lib_dir
javac_verbose     = $javac_verbose
javac_deprecation = $javac_deprecation
javac_debug       = $javac_debug
prefix            = $prefix
targets           = $targets
task              = $task
EOF
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

# Processes the targets
function process_targets
  {
    for target in $targets ; do
      case $target in 
        java)
          create_java_libs ;;
        execs)
          create_executables ;;
        all)
          create_java_libs; create_executables ;;
        doc)
          create_doc ;;
        clear-execs)
          clear_executables ;;
        clear)
          clear_executables ;;
        install-java)
          install_java_libs;;
        install-execs)
          install_executables ;;
        install)
           install_java_libs; install_executables ;;
        uninstall-execs)
          uninstall_executables ;;
        uninstall-java)
          uninstall_java_libs ;;
        uninstall)
          uninstall_executables; uninstall_java_libs;;
        *)
          echo "ERROR: Unknown target: $target"
          exit 3 ;;
      esac
    done
    echo
    echo "$program_name: Done"
    echo
  }

function show_targets
  {
cat <<EOF

java                Creates the Java libraries
execs               Creates the executables
all                 Same as "java execs"
install-java        Installs the Java libraries
install-execs       Installs the executables
install             Same as "install-java install-execs"
uninstall-java      Uninstalls the Java libraries
uninstall-execs     Uninstalls the executables
uninstall           Same as "uninstall-java uninstall-execs"
clear-java          Deletes the Java libraries created by "java"
clear-execs         Deletes the executables created "execs"
clear               Same as "clear-java clear-execs"
print-vars          Prints the variables to stdout

Default: all

EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the mmckxml package, or parts of it. What is
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
