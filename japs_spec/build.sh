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
# $Id: build.sh,v 1.14 2008/08/11 12:10:01 rassy Exp $

# Build script for japs_spec

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.14 $'
readonly user_config_file=build.conf

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions force,targets,ignore-deps,help,version,vars \
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
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Set the variables if not set already:
prefix=${prefix:-${MM_BUILD_PREFIX:-/usr/local}}
xsl_dir=$prefix/lib/mmtex/xsl
xsl_stylesheet=japs_spec.2xhtml.xsl
task=${task:-process_targets}

# Store the current directory:
base_dir=`pwd`

# --------------------------------------------------------------------------------
# Utility funcions
# --------------------------------------------------------------------------------

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

# Quotes the character '/' with a backslash. Used in sed input.
function quote
  {
    echo "$@" | sed 's/\//\\\//g'
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Copies the XSL stylesheet to its installation location
function install_xsl
  {
    echo
    echo "======================================================================"
    echo "Installing XSL stylesheet"
    echo "======================================================================"
    echo
    local source_file=$base_dir/resources/$xsl_stylesheet
    local target_file=$xsl_dir/$xsl_stylesheet
    [ "`needs_build $source_file $target_file`" ] && \
      run_cmd cp -v $source_file $target_file
    echo "$program_name: installing XSL stylesheet done"
    install_xsl_done=done
  }

# Creates the *.xml files from the *.tex sources by applying mmtex
function create_xml
  {
    echo
    echo "======================================================================"
    echo "Creating XML"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/specs
    echo "$program_name: changed into specs/"
    local tex_files=`find -name "*.tex"`
    tex_files=`get_source_files .tex .xml " " $tex_files`
    if [ "$tex_files" ] ; then
      run_cmd mmtex \
        --param lang=de \
        --recursive \
        --status \
        --warnings \
        $tex_files
    fi
    run_cmd cd $base_dir
    echo "$program_name: creating XML done"
    create_xml_done=done
  }    

# Removes all *.xml files for which there is a *.tex source
function clear_xml
  {
    echo
    echo "======================================================================"
    echo "Removing XML"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/specs
    echo "$program_name: changed into specs/"
    local xml_files=`find -name "*.xml"`
    if [ "$xml_files" ] ; then
      local xml_file
      for xml_file in $xml_files ; do
        local tex_file=${xml_file%xml}tex
        if [ -e "$tex_file" ] ; then
          run_cmd rm -v $xml_file
        fi
      done
    fi
    run_cmd cd $base_dir
    echo "$program_name: removing XML done"
    delete_xml_done=done
  }

# Creates the xhtml pages from the xml files by XSL transformation
function create_xhtml
  {
    [ "$ignore_deps" ] || [ "$install_xsl_done" ] || install_xsl
    echo
    echo "======================================================================"
    echo "Creating XHTML"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/specs
    echo "$program_name: changed into specs/"
    local xml_files=`find -name "*.xml"`
    local xml_files=`get_source_files .xml .xhtml " " $xml_files`
    if [ "$xml_files" ] ; then
      run_cmd mmxtr \
        --stylesheet=$prefix/lib/mmtex/xsl/$xsl_stylesheet \
        --param css-stylesheets=$base_dir/resources/spec.css \
        $xml_files
    fi
    run_cmd cd $base_dir
    echo "$program_name: creating XHTML done"
    create_xhtml_done=done
  }

# Removes all xhtml files for which there is a tex source
function clear_xhtml
  {
    echo
    echo "======================================================================"
    echo "Removing XHTML"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/specs
    echo "$program_name: changed into specs/"
    local xhtml_files=`find -name "*.xhtml"`
    if [ "$xhtml_files" ] ; then
      local xhtml_file
      for xhtml_file in $xhtml_files ; do
        local tex_file=${xhtml_file%xhtml}tex
        if [ -e "$tex_file" ] ; then
          run_cmd rm -v $xhtml_file
        fi
      done
    fi
    run_cmd cd $base_dir
    echo "$program_name: removing XHTML done"
    clear_xhtml_done=done
  }

# Removes all backup (*~) files
function clear_backups
  {
    echo
    echo "======================================================================"
    echo "Removing backups"
    echo "======================================================================"
    echo
    local backup_files=`find -name "*~"`
    if [ "$backup_files" ] ; then
      run_cmd rm -v $backup_files
    fi
    echo "$program_name: removing backups done"
    clear_beckups_done=done
  }

# Creates the toc.xhtml file (table of contents)
function create_toc
  {
    echo
    echo "======================================================================"
    echo "Creating toc"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/resources
    echo "$program_name: changed into resources/"
    [ "`needs_build toc.xml toc.xhtml`" ] && \
      run_cmd mmxtr \
        --stylesheet=toc.xsl \
        --param "specs-dir=$base_dir/specs" \
        --param "url-prefix=../specs" \
        toc.xml
    run_cmd cd $base_dir
    echo "$program_name: creating toc done"
    create_toc_done=done
  }

# Removes the toc.xhtml file
function clear_toc
  {
    echo
    echo "======================================================================"
    echo "Removing toc"
    echo "======================================================================"
    echo
    rm -vf resources/toc.xhtml
    echo "$program_name: removing toc done"
    remove_toc_done=done
  }


# Installs the specifications in $install_dir
function install
  {
    echo
    echo "======================================================================"
    echo "Installing"
    echo "======================================================================"
    echo
    [ "$install_dir" ] || error "No installation directory specified"
    local dir
    for dir in japs_spec japs_spec/specs japs_spec/resources ; do
      [ -e $install_dir/$dir ] || run_cmd mkdir -vp $install_dir/$dir
    done
    local resource
    for resource in toc.xhtml top.xhtml home.xhtml spec.css bg_top.png ; do
      run_cmd cp -v resources/$resource $install_dir/japs_spec/resources
    done
    run_cmd cd $base_dir/specs
    echo "$program_name: changed into specs/"
    local suffix
    local specs
    local spec
    for suffix in xhtml txt png jpg ; do
      specs=`find -name "*.$suffix"`
      if [ "$specs" ] ; then
        for spec in $specs ; do
          run_cmd cp -v $spec $install_dir/japs_spec/specs
        done
      fi
    done
    run_cmd cd $base_dir
    echo "$program_name: changed into ./"
    run_cmd cp -v index.xhtml README $install_dir/japs_spec
    echo "$program_name: installing done"
    install_done=done
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

# Processes the targets
function process_targets
  {
    for target in $targets ; do
      case $target in 
        xml)
          create_xml ;;
        xhtml)
          create_xhtml ;;
        toc)
          create_toc ;;
        clear-xml)
          clear_xml ;;
        clear-xhtml)
          clear_xhtml ;;
        clear-toc)
          clear_toc ;;
        clear-backups)
          clear_backups ;;
        all)
          create_xml; create_xhtml; create_toc ;;
        clear)
          clear_xml; clear_xhtml; clear_toc; clear_backups ;;
        install)
          install ;;
        *)
          echo "ERROR: Unknown target: $target"
          exit 3 ;;
      esac
    done
    echo
    echo "$program_name: BUILD DONE"
    echo
  }

function show_targets
  {
cat <<EOF
all            Builds all. This is the default target
xml            Creates the XML files from the TeX sources
xhtml          Creates the XHTML files from the XML sources
toc            Creates the table of contents XHTML file
clear-xml      Removes XML files for which there is a TeX source
clear-xhtml    Removes XHTML files for which there is a TeX source
clear-toc      Removes the table of contents XHTML file
clear-backups  Removes all backup files (i.e., *~ files)
clear          Removes all generated and backup files
install        Installs the specifications
EOF
  }

# Prints all variables to stdout
function print_variables
  {
    cat <<EOF
force          = $force
ignore_deps    = $ignore_deps
install_dir    = $install_dir
prefix         = $prefix
targets        = $targets
task           = $task
xsl_dir        = $xsl_lib_dir
xsl_stylesheet = $xsl_stylesheet
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the Mumie Japs specifications, or parts of it. What is
  actually done is controlled by TARGETS, which is a list of keywords called
  targets. Type ./build.sh -t to get a list of all targets. The default target
  is "all"; it is assumed if no targets are specified.
Options:
  --targets, -t
      List all targets
  --vars
      Prints the build variables to stdout
  --force, -f
      Create files even if they are up-to-date.
  --ignore-deps, -D
      Ignore target dependencies. If a target is build with this option,
      then targets required by this target are not build automatically.
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
