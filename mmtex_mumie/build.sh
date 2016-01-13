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
# $Id: build.sh,v 1.16 2009/11/10 13:02:44 linges Exp $

# Build script for mmtex4japs

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.16 $'
readonly user_config_file=build.conf

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Init customizable variables:
prefix=${MM_BUILD_PREFIX:-/usr/local}
checkin_root=${MM_CHECKIN_ROOT:-$HOME/mumie/checkin}
mmtex_cmd=mmtex
mmtex_opts="-w"

# Process command line parameters:
params=`getopt \
  --longoptions prefix:,force,targets,ignore-deps,help,version,vars,release:,cvsroot: \
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
inc_lib_dir=$prefix/lib/mmtex/include
dcl_lib_dir=$prefix/lib/mmtex/dcl
etc_dir=$prefix/etc/mmplutil
version_file=VERSION
task=${task:-process_targets}

# Store the current directory:
base_dir=`pwd`

# section names of this content repository
section_names="samples"

#mount path constant (normally: content repository sections are mounted under <checkin root>/content/)
mount_path=

# Mmtex libraries to intsall/uninstall:
inc_install_files="
japs_core.mtx.pl
japs_metainfo.mtx.pl
japs_content.mtx.pl
japs_problem.mtx.pl
japs_summary.mtx.pl
japs_media.mtx.pl
japs_link.mtx.pl
japs_course.mtx.pl
"

# Mmtex document classes to install/uninstall:
dcl_install_files="
japs/element/algorithm.dcl.pl
japs/element/application.dcl.pl
japs/element/definition.dcl.pl
japs/element/lemma.dcl.pl
japs/element/motivation.dcl.pl
japs/element/theorem.dcl.pl
japs/problem/applet.dcl.pl
japs/problem/mchoice.dcl.pl
japs/problem/traditional.dcl.pl
japs/problem/program.dcl.pl
japs/subelement/deduction.dcl.pl
japs/subelement/example.dcl.pl
japs/subelement/history.dcl.pl
japs/subelement/motivation.dcl.pl
japs/subelement/proof.dcl.pl
japs/subelement/remark.dcl.pl
japs/subelement/table.dcl.pl
japs/subelement/test.dcl.pl
japs/subelement/visualization.dcl.pl
japs/summary.dcl.pl
japs/course.dcl.pl
japs/coursesection.dcl.pl
japs/worksheet/homework.dcl.pl
japs/worksheet/selftest.dcl.pl
japs/worksheet/training.dcl.pl
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

# Recursivly creates .dir files. Usage:
#
#   create_dot_dir_files PATH NAME
#
# PATH is the path relative to the checkin root of the directory where to start. NAME
# is the local name of that directory. It is assumed that the parent of that directory
# is the current working directory when the function is entered.

function create_dot_dir_files
  {
    local path=$1
    local name=$2
    run_cmd cd $name
    echo "$program_name: changed into $name"
    echo "$program_name: creating .dir"
    echo -n $checkin_root/$path > .dir
    check_exit_code
    local subdirs=`ls $base_dir/checkin/$path`

    if [ "$subdirs" ] ; then
      local subdir
      for subdir in $subdirs ; do
        local dir=$base_dir/checkin/$path/$subdir
        [ -d "$dir" ] && [ "$subdir" != 'CVS' ] && ! [ -h "$subdir" ] && \
          create_dot_dir_files $path/$subdir $subdir
      done
    fi
    run_cmd cd ..
    echo "$program_name: left $name"
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

# Creates a directory in the global checkin tree provided it does not exist already.
# Parent directories are created if necessary. The directory and its parents are
# equipped with .meta.xml files if the latter do not exist already (this is done even
# if the directories itselfs existed before).

function create_checkin_dir
  {
    local path=$1
    run_cmd mkdir -pv $checkin_root/$path

    while [ "$path" ] && [ "$path" != "." ] ; do
      [ -e "$checkin_root/$path/.meta.xml" ] || \
        run_cmd cp -v $base_dir/checkin/$path/.meta.xml $checkin_root/$path
      path=`dirname $path`
    done
  }

# Starts mmjvmd if necessary. The old mmjvmd status (running or not running)
# is saved in the gloabl variable old_mmjvmd_status.
function begin_mmjvmd
  {
    old_mmjvmd_status=`mmjvmd status`
    run_cmd mmjvmd start
  }

# Stops mmjvmd provided its old status is "Jvmd is not running"
function end_mmjvmd
  {
    [ "$old_mmjvmd_status" = 'Jvmd is not running' ] && run_cmd mmjvmd stop
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

#Compiles the Java sources and creates Mumie documents ready for checkin
function apply_mmjava
  {
    echo
    echo "======================================================================"
    echo "Compiling java sources"
    echo "======================================================================"
    echo
    local section_name
    begin_mmjvmd
    for section_name in $section_names ; do
      run_cmd cd $checkin_root/$mount_path/$section_name
      for source_file in `find -L -name "*.java"` ; do
        [ "$force" == 'force' ] && local force_flag="-f "
        run_cmd mmjava $force_flag $source_file
      done
    done
    end_mmjvmd
  }

# Converts all tex sources
function apply_mmtex
  {
    echo
    echo "======================================================================"
    echo "Applying mmtex"
    echo "======================================================================"
    echo

    # Compose mmtex command:
    local mmtex_call="$mmtex_cmd -rwF $mmtex_opts"
    [ "$force" ] && mmtex_call="$mmtex_call -f"
 
    local section_name
    for section_name in $section_names ; do
      # Change into top-level local checkin dir:
      run_cmd cd $base_dir/checkin/$mount_path/$section_name
      echo "$program_name: changed into checkin/$mount_path/$section_name"

      # Execute mmtex:
      echo "$program_name: calling mmtex:"
      run_cmd $mmtex_call
      run_cmd cd $base_dir
    done

    apply_mmtex_done=done
    echo "$program_name: applying mmtex done"
  }

# "Mounts" the contents to the global checkin tree
function mount_checkin
  {
    echo
    echo "======================================================================"
    echo "Mounting checkin"
    echo "======================================================================"
    echo

    create_checkin_dir $mount_path
    local section_name
    for section_name in $section_names ; do
      if ! [ -e $checkin_root/$mount_path/$section_name ] ; then
        run_cmd ln -vs $base_dir/checkin/$mount_path/$section_name $checkin_root/$mount_path 
      fi
      run_cmd cd $checkin_root/$mount_path
      create_dot_dir_files $mount_path/$section_name $section_name 
      run_cmd cd $base_dir
    done

    mount_checkin_done=done
    echo "$program_name: mounting checkin done"
  }

# "Unmounts" the contents to the global checkin tree
function unmount_checkin
  {
    echo
    echo "======================================================================"
    echo "Unmounting checkin"
    echo "======================================================================"
    echo

    local section_name
    for section_name in $section_names ; do
      if [ -h $checkin_root/$mount_path/$section_name ] ; then
        run_cmd rm  $checkin_root/$mount_path/$section_name
        echo "$program_name: unmounting checkin done"
      else
        echo "Package ${section_name} not mounted yet"
      fi
    done
    unmount_checkin_done=done
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Copies the mmtex document classes to their installation location
function install_dcls
  {
    echo
    echo "======================================================================"
    echo "Installing document classes"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/dcl
    echo "$program_name: changed into lib/dcl"
    cp_to_dir $dcl_lib_dir $dcl_install_files
    run_cmd cd $base_dir
  }

# Removes the mmtex document classes from their installation location
function uninstall_dcls
  {
    echo
    echo "======================================================================"
    echo "Uninstalling document classes"
    echo "======================================================================"
    echo
    if [ -e $dcl_lib_dir ] ; then
      run_cmd cd $dcl_lib_dir
      echo "$program_name:: changed into $dcl_lib_dir"
      run_cmd rm -vf $dcl_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Copies the mmtex libraries to their installation location
function install_libs
  {
    echo
    echo "======================================================================"
    echo "Installing libraries"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/include
    echo "$program_name: changed into lib/include"
    cp_to_dir $inc_lib_dir $inc_install_files
    run_cmd cd $base_dir
  }

# Removes the mmtex libraries files from their installation location
function uninstall_libs
  {
    echo
    echo "======================================================================"
    echo "Uninstalling libraries"
    echo "======================================================================"
    echo
    if [ -e $inc_lib_dir ] ; then
      run_cmd cd $inc_lib_dir
      echo "$program_name:: changed into $inc_lib_dir"
      run_cmd rm -vf $inc_install_files
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
    local dist_name="mmtex_mumie_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag mmtex_mumie
    run_cmd mv -v mmtex_mumie $dist_name
    echo "$program_name: Creating version file"
    run_cmd echo $release > $dist_name/$version_file
    echo "$program_name: Creating tgz"
    run_cmd tar czf $archive $dist_name
    run_cmd cd $base_dir
  }



# Processes the targets
function process_targets
  {
    for target in $targets ; do
      case $target in
        conf-xsl)
          create_conf_xsl ;;
        all)
          echo "Nothing to build" ;;
        clear)
          echo "Nothing to clear" ;;
        install-dcls)
          install_dcls ;;
        install-libs)
          install_libs ;;
        install-verfile)
          install_version_file ;;
        install)
          install_dcls; install_libs; install_version_file ;;
        uninstall-dcls)
          uninstall_dcls ;;
        uninstall-libs)
          uninstall_libs ;;
        uninstall-verfile)
          uninstall_version_file ;;
        uninstall)
          uninstall_libs; uninstall_dcls; uninstall_version_file ;;
        dist)
          create_dist ;;
        mount-checkin)
          mount_checkin ;;
        unmount-checkin)
          unmount_checkin ;;
        mmjava)
          apply_mmjava ;;
        mmtex)
          apply_mmtex ;;
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
all                 Does nothing
clear               Does nothing
install-dcls        Installs the mmtex document classes
install-libs        Installs the mmtex libraries
install-verfile     Installs the version file
install             Installs all
uninstall-dcls      Uninstalls the mmtex document classes
uninstall-libs      Uninstalls the mmtex libraries
uninstall-verfile   Uninstalls the version file
uninstall           Uninstalls all
mount-checkin       Adds the contents to the global checkin tree
unmount-checkin     Removes the contents from the global checkin tree
mmtex               Applies mmtex to all tex sources
mmjava              Applies mmjava to all java sources
dist                Creates a distribution

EOF
  }

# Prints all variables to stdout
function print_variables
  {
    cat <<EOF
cvsroot           = $cvsroot
force             = $force
ignore_deps       = $ignore_deps
prefix            = $prefix
release           = $release
targets           = $targets
task              = $task
java_source_path  = $java_source_path
section_names     = $section_names
mount_path        = $mount_path
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the mmtex_mumie package, or parts of it. What is
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
