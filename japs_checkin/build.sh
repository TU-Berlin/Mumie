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
# $Id: build.sh,v 1.23 2009/11/16 11:26:02 rassy Exp $

# Build script for japs_checkin

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.23 $'
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
version_file=VERSION
task=${task:-process_targets}

# Store the current directory:
base_dir=`pwd`

# Subdirectories of the 'system' checkin directory:
system_dirs="
  account
  admin
  common
  course
  element
  languages
  manage
  misc
  problem
  start
  document
  pseudodoc
  grades
  mmbrowser
  auth
"

libraries_files="
  jsl_mmajax.meta.xml
  jsl_mmajax.content.xml
  jsl_datasheet.meta.xml
  jsl_datasheet.content.xml
  jsl_problem_answers.meta.xml
  jsl_problem_answers.content.xml
  jsl_mchoice_problem_answers.meta.xml
  jsl_mchoice_problem_answers.content.xml
  jsl_textinput_problem_answers.content.xml
  jsl_textinput_problem_answers.meta.xml
"

compl_libraries_files="
  jar_jreality.content.jar
  jar_jreality.meta.xml
  jar_mathletfactory_base.content.jar
  jar_mathletfactory_base.meta.xml
  jar_mathlet_factory.content.jar
  jar_mathletfactory_graphics2d.content.jar
  jar_mathletfactory_graphics2d.meta.xml
  jar_mathletfactory_java3d.content.jar
  jar_mathletfactory_java3d.meta.xml
  jar_mathletfactory_jreality.content.jar
  jar_mathletfactory_jreality.meta.xml
  jar_mathlet_factory.meta.xml
  jar_japs_client.content.jar
  jar_japs_client.meta.xml
  jar_datasheet.content.jar
  jar_datasheet.meta.xml
"

# Subdirectories of the 'org' checkin directory:
org_dirs="
  user_groups
  users"

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
    echo -e "ERROR: $*" 1>&2
    echo
    [ "$mmjvmd_status" == 'Jvmd is not running' ] && run_cmd mmjvmd stop;
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

# Creates links in the global checkin tree. Usage:
#
#   link_checkin_dirs PATH SUBDIRS
#
# PATH is a path relative to the checkin root. It must point to a directory in the local
# checkin tree of this package (thus, to a descendent of $basedir/checkin). In the
# corresponding directory of the global checkin tree, softlinks are created to each subdirectory
# listed in SUBDIRS. SUBDIRS must contain the subdirectories as a whitespace-separated list.
# Directories with the name "CVS" are skipped. For each linked directory, .dir files are created
# recursively. This is done by calling the 'create_dot_dir_files' function.

function link_checkin_dirs
  {
    local path=$1
    local subdirs=$2
    local old_dir=`pwd`
    run_cmd cd $path
    echo "$program_name: Changed into $path"
    local subdir
    for subdir in $subdirs ; do
      local dir=$base_dir/checkin/$path/$subdir
      if [ -d "$dir" ] && [ "$subdir" != 'CVS' ] ; then
        run_cmd ln -vfs $dir
        create_dot_dir_files $path/$subdir $subdir
      fi
    done
    run_cmd cd $old_dir
    echo "$program_name: returned into $old_dir"
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
    [ "$subdirs" ] || return
    local subdir
    for subdir in $subdirs ; do
      local dir=$base_dir/checkin/$path/$subdir
      [ -d "$dir" ] && [ "$subdir" != 'CVS' ] && \
        create_dot_dir_files $path/$subdir $subdir
    done
    run_cmd cd ..
    echo "$program_name: left $name"
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# "Mounts" the (pseudo-)documents to the global checkin tree.
function mount_checkin
  {
    echo
    echo "======================================================================"
    echo "Mounting checkin"
    echo "======================================================================"
    echo
    mkdir -pv $checkin_root
    run_cmd cd $checkin_root
    echo "$program_name: changed into $checkin_root"

    # Mount system and org, except system/libraries:
    run_cmd mkdir -pv system org
    link_checkin_dirs system "$system_dirs"
    link_checkin_dirs org "$org_dirs"

    # Mount system/libraries:
    local library_checkin_dir="system/libraries"
    [ -e "$library_checkin_dir" ] || run_cmd mkdir -vp $library_checkin_dir
    run_cmd cd $library_checkin_dir
    echo "$program_name: changed into $library_checkin_dir"
    local build_library_checkin_dir=$base_dir/checkin/$library_checkin_dir
    [ -e .meta.xml ] || run_cmd cp -v $build_library_checkin_dir/.meta.xml .
    echo -n $checkin_root/$library_checkin_dir > .dir
    local f
    for f in $libraries_files ; do
      run_cmd ln -sfv $build_library_checkin_dir/$f
    done

    run_cmd cd $base_dir
    echo "$program_name: mounting checkin done"
  }

# "Mounts" the (pseudo-)documents from the global checkin tree.
function unmount_checkin
  {
    echo
    echo "======================================================================"
    echo "Unmounting checkin"
    echo "======================================================================"
    echo
    run_cmd cd $checkin_root/system
    run_cmd rm -vf $system_dirs
    run_cmd cd libraries
    run_cmd rm -vf $libraries_files .dir
    run_cmd cd $checkin_root/org
    run_cmd rm -vf $org_dirs
    run_cmd cd $base_dir/checkin
    run_cmd rm -v `find -name .dir`
    run_cmd cd $base_dir
    echo "$program_name: unmounting checkin done"
  }

# Adds the documents in the checkin_compl folder to the checkin tree
function mount_checkin_compl
  {
    echo
    echo "======================================================================"
    echo "Mounting checkin compl"
    echo "======================================================================"
    echo
    local library_checkin_dir="system/libraries"
    [ -e "$checkin_root/$library_checkin_dir" ] ||
      error "$library_checkin_dir does not exist in checkin tree\n" \
            "(Note: You must run mount-checkin before mount-checkin-compl)"
    run_cmd cd $checkin_root/$library_checkin_dir
    echo "$program_name: changed into `pwd`"
    local build_library_checkin_dir=$base_dir/checkin_compl/$library_checkin_dir
    local f
    for f in $compl_libraries_files ; do
      run_cmd ln -sfv $build_library_checkin_dir/$f
    done
    run_cmd cd $base_dir
    echo "$program_name: mounting checkin compl done"
  }

# Removes the documents in the checkin_compl folder from the checkin tree
function unmount_checkin_compl
  {
    echo
    echo "======================================================================"
    echo "Unmounting checkin compl"
    echo "======================================================================"
    echo
    run_cmd cd $checkin_root/system/libraries
    run_cmd rm -vf $compl_libraries_files
    run_cmd cd $base_dir
    echo "$program_name: unmounting checkin compl done"
  }

# Creates the previews of all XSL stylesheets
function create_xsl_previews
  {
    echo
    echo "======================================================================"
    echo "Creating XSL stylesheet previews"
    echo "======================================================================"
    echo

    # Store current mmjvmd status:
    mmjvmd_status=`mmjvmd status`

    run_cmd cd $checkin_root
    echo "$program_name: changed into $checkin_root"

    # Create previews:
    run_cmd mmprev -f `find -L -name "xsl_*.meta.xml"`

    # Stop mmjvmd if it was not running before:
    [ "$mmjvmd_status" == 'Jvmd is not running' ] && run_cmd mmjvmd stop;

    run_cmd cd $base_dir
    echo "$program_name: creating XSL stylesheet previews done"
  }

# Creates the previews of all CSS stylesheets
function create_css_previews
  {
    echo
    echo "======================================================================"
    echo "Creating CSS stylesheet previews"
    echo "======================================================================"
    echo

    # Store current mmjvmd status:
    local mmjvmd_status=`mmjvmd status`

    run_cmd cd $checkin_root
    echo "$program_name: changed into $checkin_root"

    # Create previews:
    run_cmd mmprev -f `find -L -name "css_*.meta.xml"`

    # Stop mmjvmd if it was not running before:
    [ "$mmjvmd_status" == 'Jvmd is not running' ] && run_cmd mmjvmd stop;

    run_cmd cd $base_dir
    echo "$program_name: creating CSS stylesheet previews done"
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
    local dist_name="japs_checkin_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag japs_checkin
    run_cmd mv -v japs_checkin $dist_name
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
        all)
          echo "$program_name: Nothing to build" ;;
        install)
          echo "$program_name: Nothing to install" ;;
        mount-checkin)
          mount_checkin ;;
        unmount-checkin)
          unmount_checkin ;;
        mount-checkin-compl)
          mount_checkin_compl ;;
        unmount-checkin-compl)
          unmount_checkin_compl ;;
        previews-xsl)
          create_xsl_previews ;;
        previews-css)
          create_css_previews ;;
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
checkin_root      = $checkin_root
force             = $force
ignore_deps       = $ignore_deps
prefix            = $prefix
release           = $release
targets           = $targets
task              = $task
EOF
  }

function show_targets
  {
cat <<EOF
all                    Does nothing, present only for complience reasons
install                Does nothing, present only for complience reasons
mount-checkin          Adds the (pseudo-)documents to the global checkin tree
unmount-checkin        Removes the (pseudo-)documents from the global checkin
                       tree
mount-checkin-compl    Adds the (pseudo-)documents in the checkin_compl folder
                       to the global checkin tree
unmount-checkin-compl  Removes the (pseudo-)documents in the checkin_compl
                       folder from the global checkin tree
previews-xsl           Creates the previews of all XSL stylesheets
previews-css           Creates the previews of all CSS stylesheets
dist                   Creates a distribution
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the japs_checkin package, or parts of it. What is
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
