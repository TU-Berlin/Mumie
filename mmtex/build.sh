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
# $Id: build.sh,v 1.17 2008/01/14 11:03:54 rassy Exp $

# Build script for mmtex

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.17 $'
readonly user_config_file=build.conf

# Set variable defaults:
prefix=${MM_BUILD_PREFIX:-/usr/local}
task=process_targets

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions prefix:,force,targets,ignore-deps,help,version,release:,cvsroot:,vars \
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
    --force|-f) force=force ; shift ;;
    --release) release="$2" ; shift 2 ;;
    --cvsroot) cvsroot="$2" ; shift 2 ;;
    --vars) task=print_variables ; shift ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Set the variables if not set already:
perl_lib_dir=$prefix/lib/perl
exec_dir=$prefix/bin
inc_lib_dir=$prefix/lib/mmtex/include
dcl_lib_dir=$prefix/lib/mmtex/dcl
xsl_dir=$prefix/lib/mmtex/xsl
css_dir=$prefix/lib/mmtex/css
img_dir=$prefix/lib/mmtex/images
etc_dir=$prefix/etc/mmplutil
version_file=VERSION

# Store the current directory:
base_dir=`pwd`

# Executables to install/uninstall:
execs_install_files="
  mmtex
  mmxalan"

# Perl modules to install/uninstall:
perlmod_install_files="
  Mumie/MmTeX/Converter.pm
  Mumie/MmTeX/DclLoader.pm
  Mumie/MmTeX/IOHelper.pm
  Mumie/MmTeX/LibLoader.pm
  Mumie/MmTeX/Parser.pm
  Mumie/MmTeX/Serializer.pm
  Mumie/MmTeX/SelfDoc.pm
  Mumie/MmTeX/Util.pm"

# XSL stylesheets to install/uninstall:
xsl_install_files="
  config.xsl
  box.2xhtml.xsl
  misc.2xhtml.xsl
  horizontal_float.2xhtml.xsl
  hyperlink.2xhtml.xsl
  list.2xhtml.xsl
  math.2xhtml.xsl
  multimedia.2xhtml.xsl
  numbers.2xhtml.xsl
  preformatted.2xhtml.xsl
  simple_markup.2xhtml.xsl
  struct.2xhtml.xsl
  table.2xhtml.xsl
  xdoc.2xhtml.xsl
  generic.2xhtml.xsl
  tree.2xhtml.xsl"

# CSS stylesheets to install/uninstall:
css_install_files="
  generic.css"

# Mmtex libraries to intsall/uninstall:
inc_install_files="
  box.mtx.pl
  counter.mtx.pl
  css.mtx.pl
  german.mtx.pl
  horizontal_float.mtx.pl
  hyperlink.mtx.pl
  input.mtx.pl
  lang.mtx.pl
  length.mtx.pl
  list.mtx.pl
  math_align.mtx.pl
  math_commdiag.mtx.pl
  math_matrix.mtx.pl
  math.mtx.pl
  math_table.mtx.pl
  multimedia.mtx.pl
  preformatted.mtx.pl
  simple_markup.mtx.pl
  specialchar.mtx.pl
  struc.mtx.pl
  system.mtx.pl
  table.mtx.pl
  tabular.mtx.pl
  usercmd.mtx.pl
  verbatim.mtx.pl
  incremental.mtx.pl
  math_incremental.mtx.pl
  tree.mtx.pl
"

# Mmtex document classes to install/uninstall:
dcl_install_files="
  generic.dcl.pl
"

# Images to install/uninstall:
img_install_files="
  strut.png
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
        run_cmd sed s/\@mmtex-prefix\@/`quote $prefix`/g $template > $target
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

# Creates the configuration xsl
function create_conf_xsl
  {
    echo
    echo "======================================================================"
    echo "Creating config xsl"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/styles/xsl
    rm -vf config.xsl
    run_cmd echo '<?xml version="1.0" encoding="ASCII"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:param name="css-dir">'$css_dir'</xsl:param>
<xsl:param name="img-dir">'$img_dir'</xsl:param>

</xsl:stylesheet>
' > config.xsl
    run_cmd cd $base_dir
  }

# Deletes the configuration xsl created by create_conf_xsl
function clear_conf_xsl
  {
    echo
    echo "======================================================================"
    echo "Removing config xsl"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/styles/css
    rm -vf config.xsl
    run_cmd cd $base_dir
  }

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
      echo "$program_name: changed into $perl_lib_dir"
      run_cmd rm -vf $perlmod_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

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
      echo "$program_name: changed into $dcl_lib_dir"
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
      echo "$program_name: changed into $inc_lib_dir"
      run_cmd rm -vf $inc_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Copies the images to their installation location
function install_images
  {
    echo
    echo "======================================================================"
    echo "Installing images"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/images
    echo "$program_name: changed into images/"
    cp_to_dir $img_dir $img_install_files
    run_cmd cd $base_dir
  }

# Removes the images from their installation location
function uninstall_images
  {
    echo
    echo "======================================================================"
    echo "Uninstalling images"
    echo "======================================================================"
    echo
    if [ -e $img_dir ] ; then
      run_cmd cd $img_dir
      echo "$program_name: changed into $img_dir"
      run_cmd rm -vf $img_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Copies the XSL stylesheets to their installation location
function install_xsl
  {
    echo
    echo "======================================================================"
    echo "Installing XSL stylesheets"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/styles/xsl
    echo "$program_name: changed into styles/xsl"
    run_cmd mkdir -pv $xsl_dir
    cp_to_dir $xsl_dir $xsl_install_files
    run_cmd cd $base_dir
  }

# Removes the XSL stylesheets from their installation location
function uninstall_xsl
  {
    echo
    echo "======================================================================"
    echo "Uninstalling XSL stylesheets"
    echo "======================================================================"
    echo
    if [ -e $xsl_dir ] ; then
      run_cmd cd $xsl_dir
      echo "$program_name: changed into $xsl_dir"
      run_cmd rm -vf $xsl_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Copies the CSS stylesheets to their installation location
function install_css
  {
    echo
    echo "======================================================================"
    echo "Installing CSS stylesheets"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/styles/css
    echo "$program_name: changed into styles/css"
    run_cmd mkdir -pv $css_dir
    cp_to_dir $css_dir $css_install_files
    run_cmd cd $base_dir
  }

# Removes the CSS stylesheets from their installation location
function uninstall_css
  {
    echo
    echo "======================================================================"
    echo "Uninstalling CSS stylesheets"
    echo "======================================================================"
    echo
    if [ -e $css_dir ] ; then
      run_cmd cd $css_dir
      echo "$program_name: changed into $css_dir"
      run_cmd rm -vf $css_install_files
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
    local dist_name="mmtex_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag mmtex
    run_cmd mv -v mmtex $dist_name
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
        execs)
          create_executables ;;
        conf-xsl)
          create_conf_xsl ;;
        all)
          create_executables; create_conf_xsl ;;
        clear-execs)
          clear_executables ;;
        clear-conf-xsl)
          clear_conf_xsl ;;
        clear)
          clear_executables; clear_conf_xsl ;;
        install-perlmods)
          install_perlmods ;;
        install-dcls)
          install_dcls ;;
        install-libs)
          install_libs ;;
        install-xsl)
          install_xsl ;;
        install-css)
          install_css ;;
        install-img)
          install_images ;;
        install-execs)
          install_executables ;;
        install-verfile)
          install_version_file ;;
        install)
          install_perlmods; install_dcls; install_libs; install_xsl; install_css;
          install_images; install_executables; install_version_file ;;
        uninstall-perlmods)
          uninstall_perlmods ;;
        uninstall-dcls)
          uninstall_dcls ;;
        uninstall-libs)
          uninstall_libs ;;
        uninstall-xsl)
          uninstall_xsl ;;
        uninstall-css)
          uninstall_css ;;
        uninstall-img)
          uninstall_images ;;
        uninstall-execs)
          uninstall_executables ;;
        uninstall-verfile)
          uninstall_version_file ;;
        uninstall)
          uninstall_perlmods; uninstall_libs; uninstall_dcls; uninstall_xsl; uninstall_css;
          uninstall_images; uninstall_executables; uninstall_version_file ;;
        dist)
          create_dist ;;
        *)
          echo "ERROR: Unknown target: $target"
          exit 3 ;;
      esac
    done
    echo
    echo "$program_name: Done"
    echo
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

function show_targets
  {
cat <<EOF
execs               Creates the executables
conf-xsl            Creates the configuration XSL stylesheet
all                 Same as "execs conf-xsl"
install-perlmods    Installs the Perl modules
install-dcls        Installs the mmtex document classes
install-libs        Installs the mmtex libraries
install-xsl         Installs the XSL stylesheets
install-css         Installs the CSS stylesheets
install-img         Installs the images
install-execs       Installs the executables
install-verfile     Installs the version file
install             Installs all
clear-execs         Deletes the created executables
clear-conf-xsl      Deletes the configuration XSL stylesheet
clear               Delets all created files in the build tree (installed
                    files are not deleted)
uninstall-perlmods  Uninstalls the Perl modules
uninstall-dcls      Uninstalls the mmtex document classes
uninstall-libs      Uninstalls the mmtex libraries
uninstall-xsl       Uninstalls the XSL stylesheets
uninstall-css       Uninstalls the CSS stylesheets
uninstall-img       Uninstalls the images
uninstall-execs     Uninstalls the executables
uninstall-verfile   Uninstalls the version file
uninstall           Uninstalls all
dist                Creates a distribution

EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the MmTeX package, or parts of it. What is actually
  done is controlled by TARGETS, which is a list of keywords called targets.
  Type ./build.sh -t to get a list of all targets. The default target is "all";
  it is assumed if no targets are specified.
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
