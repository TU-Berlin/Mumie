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

readonly prefix=@prefix@

task=setup_mmcd
mmcd_def_bash=$prefix/etc/mmcdk/mmcd_def.sh
mmcd_csh=$prefix/etc/mmcdk/mmcd.csh

# --------------------------------------------------------------------------------
# Tasks
# --------------------------------------------------------------------------------

function setup_mmcd
  {
    if [ -e ~/.bashrc ] && \
      egrep "^[[:space:]]*source[[:space:]]+[\"']?$mmcd_def_bash[\"']?" ~/.bashrc > /dev/null
    then
      echo "$mmcd_def_bash is already sourced in ~/.bashrc"
    else
      echo >> ~/.bashrc
      echo "# Define mmcd function:" >> ~/.bashrc
      echo "source $mmcd_def_bash" >> ~/.bashrc
    fi

    if [ -e ~/.cshrc ] && \
      egrep "^[[:space:]]*alias[[:space:]]+mmcd[[:space:]]+[\"']source[[:space:]]+$mmcd_csh[\"']" \
       ~/.cshrc > /dev/null
    then
      echo "Alias mmcd already defined defined in ~/.cshrc"
    else
      echo >> ~/.cshrc
      echo "# Define mmcd as alias:" >> ~/.cshrc
      echo "alias mmcd 'source $mmcd_csh'" >> ~/.cshrc
    fi

    echo "Done"
  }

function show_help
  {
    cat <<EOF
Usage:
  $prefix/etc/mmcdk/mmcd_setup.sh
  $prefix/etc/mmcdk/mmcd_setup.sh --help | -h | --version | -v
Description:
  Sets-up the mmcd command.
Options:
  --help, -h
      Prints this help text and exits
  --version, -v
      Prints version information and exits
EOF
  }

function show_version
  {
    echo "$Revision: 1.1 $"
  }

# --------------------------------------------------------------------------------
# Main program
# --------------------------------------------------------------------------------

# Process command line parameters:
params=`getopt \
  --longoptions help,version \
  --options h,v \
  -- \
  "$@"`
eval set -- "$params"
while true ; do
  case "$1" in
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --) shift ; break ;;
  esac
done

$task "$@"

