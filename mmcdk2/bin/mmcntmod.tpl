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

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: mmcntmod.tpl,v 1.4 2008/09/23 11:04:12 rassy Exp $

# Set fixed variabes (constants):
readonly program_name=mmcntmod
readonly program_version='$Revision: 1.4 $'

# Variables:
prefix=${MM_BUILD_PREFIX:-@prefix@}
build_sh_tpl=$HOME/.mmcdk/mmcntmod/build.sh.tpl
[ -e "$build_sh_tpl" ] || build_sh_tpl=$prefix/etc/mmcdk/mmcntmod/build.sh.tpl
section_meta_tpl=$HOME/.mmcdk/mmcntmod/section.meta.xml.tpl
[ -e "$section_meta_tpl" ] || section_meta_tpl=$prefix/etc/mmcdk/mmcntmod/section.meta.xml.tpl
extra_dirs="
  media
  problems"
task=create_project

# Process command line parameters:
params=`getopt \
  --longoptions extra-dirs:,help,version,vars \
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
    --extra-dirs) extra_dirs="$2" ; shift 2 ;;
    --) shift ; break ;;
  esac
done
name=$1
mount_path=$2

# --------------------------------------------------------------------------------
# Utility functions
# --------------------------------------------------------------------------------

# Aborts with an error message
function error
  {
    echo "ERROR: $*" 1>&2
    echo  1>&2
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

# Quotes sed metacharacters with a backslash. Used in sed input.
function quote
  {
    echo "$@" | sed 's:\([/$.*&{}^]\):\\\1:g' | sed 's:\[:\\[:g' | sed 's:\]:\\]:g'
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

function create_project
  {
    # Checks:
    [ "$name" ] || error "No project name specified"
    [ "$mount_path" ] || error "No mount path specified"
    echo "$name" | egrep '^[a-zA-Z0-9_-]+' > /dev/null || error "Invalid project name"
    [ -e "$build_sh_tpl" ] || error "No build script template found"
    [ -e "$section_meta_tpl" ] || error "No section master file template found"

    run_cmd mkdir -p $name/checkin
    run_cmd cd $name

    local project_dir=`pwd`

    # Create build.sh file:
    cat $build_sh_tpl \
      | sed s/@package-name@/"`quote $name`"/g \
      | sed s/@mount-path@/"`quote $mount_path`"/g \
      | sed s/@extra-dirs@/"`quote $extra_dirs`"/g \
      > build.sh
    run_cmd chmod a+x build.sh

    # Create sections dirs in mount path:
    run_cmd cd checkin
    local item
    for item in `echo $mount_path | sed 's:/: :g'` ; do
      run_cmd mkdir $item
      run_cmd cd $item
      run_cmd sed s/@name@/"`quote $item`"/g $section_meta_tpl > .meta.xml
    done

    local extra_dir
    for extra_dir in $extra_dirs ; do
      run_cmd mkdir -p $extra_dir
      local name=`basename $extra_dir`
      run_cmd sed s/@name@/"`quote $name`"/g $section_meta_tpl > $extra_dir/.meta.xml
    done

    # Create 'var' dir:
    run_cmd cd $project_dir
    run_cmd mkdir var
    echo \
'$Id: mmcntmod.tpl,v 1.4 2008/09/23 11:04:12 rassy Exp $

This is simply a dummy file to prevent CVS from removing this
directory.
' > var/.KEEP_ME
  }

function show_help
  {
    cat <<EOF
Usage:
  $program_name NAME MOUNT_PATH
  $program_name --vars | --help | -h | --version | -v
Description:
  Creates a new content project with the specified name and mount path. This is
  what happens:
    o  A directory NAME is created (in the current directory).
    o  In NAME, a subdirectory 'checkin' is created.
    o  Therein, the directories of the path MOUNT_PATH are created and equipped
       with '.meta.xml' files
Options:
  --vars
      Prints some variables controlling this command
  --help, -h
      Prints this help text
  --version, -v
      Prints version information
EOF
  }

function show_version
  {
    echo "$program_version"
  }

function print_variables
  {
    cat <<EOF
build_sh_tpl     = $build_sh_tpl
section_meta_tpl = $section_meta_tpl
EOF
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

# Execute task:
$task
