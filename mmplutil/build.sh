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
# $Id: build.sh,v 1.10 2008/01/14 11:00:20 rassy Exp $

# Build script for the mmplutil package

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.10 $'
readonly user_config_file=build.conf

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions prefix:,force,targets,ignore-deps,release:,cvsroot:,help,version,vars \
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
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Set the variables if not set already:
prefix=${prefix:-${MM_BUILD_PREFIX:-/usr/local}}
perl_lib_dir=$prefix/lib/perl
etc_dir=$prefix/etc/mmplutil
version_file=VERSION
task=${task:-process_targets}

# Store the current directory:
base_dir=`pwd`

# Executables to install/uninstall:
execs_install_files="
  mmtex
  mmxalan
"

# Perl modules to install/uninstall:
perlmod_install_files="
Mumie/Balanced.pm
Mumie/Boolconst.pm
Mumie/ExecFuncCalls.pm
Mumie/File.pm
Mumie/Hooks.pm
Mumie/List.pm
Mumie/Logger.pm
Mumie/ProgressBar.pm
Mumie/Scanner.pm
Mumie/Text.pm
Mumie/XML/Characters.pm
Mumie/XML/Writer.pm
"

# --------------------------------------------------------------------------------
# Utility functions
# --------------------------------------------------------------------------------
# (Pasted from build_tools/lib/bash/build.inc, revision 1.2)

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
      [ "`needs_build $source_file $target_file`" ] && echo $source_file
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

# Quotes the character '/' with a backslash. Used in sed input.
function quote
  {
    echo "$@" | sed 's/\//\\\//g'
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Copies the Perl modules files to their installation location
function install_perlmods
  {
    echo
    echo "======================================================================"
    echo "Installing Perl modules"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/perl
    echo "$program_name: changed into lib/perl"
    run_cmd mkdir -pv $perl_lib_dir
    cp_to_dir $perl_lib_dir $perlmod_install_files
    run_cmd cd $base_dir
  }

# Removes the Perl modules files from their installation location
function uninstall_perlmods
  {
    echo
    echo "======================================================================"
    echo "Uninstalling Perl modules"
    echo "======================================================================"
    echo
    if [ -e $perl_lib_dir ] ; then
      run_cmd cd $perl_lib_dir
      echo "$program_name:: changed into $perl_lib_dir"
      run_cmd rm -vf $perlmod_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Copies the version file to its installation location
function install_version_file
  {
    echo
    echo "======================================================================"
    echo "Installing version file"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir
    if [ -e "$version_file" ] ; then
        run_cmd mkdir -pv $etc_dir
      run_cmd rm -vf $etc_dir/$version_file
      run_cmd cp -v $version_file $etc_dir
    else
      echo "$program_name: Version file does not exist"
    fi
    run_cmd cd $base_dir
  }

# Removes the version file from its installation location
function uninstall_version_file
  {
    echo
    echo "======================================================================"
    echo "Uninstalling version file"
    echo "======================================================================"
    echo
    if [ -e $etc_dir ] ; then
      run_cmd rm -vf $etc_dir/$version_file
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
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
    local dist_name="mmplutil_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag mmplutil
    run_cmd mv -v mmplutil $dist_name
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
    for target in $targets ; do
      case $target in
        all)
          echo "$program_name: nothing to build" ;;
        install-perlmods)
          install_perlmods ;;
        install-verfile)
          install_version_file ;;
        install)
          install_perlmods; install_version_file ;;
        uninstall-perlmods)
          uninstall_perlmods ;;
        uninstall-verfile)
          uninstall_version_file ;;
        uninstall)
          uninstall_perlmods; uninstall_version_file ;;
        dist)
          create_dist ;;
        print-vars)
          print_variables ;;
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
install-perlmods    Installs the Perl modules
install-verfile     Installs the version file
install             Installs all
uninstall-perlmods  Uninstalls the Perl modules
uninstall-verfile   Uninstalls the version file
uninstall           Uninstalls all
dist                Creates a distribution

EOF
  }

# Prints all variables to stdout
function print_variables
  {
    cat <<EOF
cvsroot       = $cvsroot
force         = $force
ignore_deps   = $ignore_deps
prefix        = $prefix
release       = $release
targets       = $targets
task          = $task
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the mmplutil package, or parts of it. What is
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
  --release=VERSION_NUMBER
      Set the release for the distribution to build. In effect only with
      the "dist" target, otherwise ignored.
  --cvsroot=CVSROOT
      Set the cvs root for retrieving the distribution to build. In effect
      only with the "dist" target, otherwise ignored. If not set, the
      environment variable \$CVSROOT is used
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
