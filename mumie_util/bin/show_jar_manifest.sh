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

# Autor: Tilman Rassy <rassy@math.tu-berlin.de>
# $id$

# Set fixed variabes (constants):
readonly program_name=show_jar_manifest

# Aborts with an error message
function error
  {
    echo "ERROR: $*"
    echo
    [ "$temp_dir" ] && rm -rf $temp_dir
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

function make_abs
  {
    loacl filename=$1
  }

function list_manifest
  {
    local jar_file=$1

    local current_dir=`pwd`

    # Make filename absolute if necessary:
    [ "${jar_file:0:1}" == '/' ] || jar_file="`pwd`/$jar_file"

    # Create temp dir:
    temp_dir=`mktemp -d /tmp/${program_name}.XXXXXXXXXX`
    check_exit_code

    cd $temp_dir
    run_cmd jar xf $jar_file META-INF/MANIFEST.MF
    cd $current_dir
    cat $temp_dir/META-INF/MANIFEST.MF
    rm -rf $temp_dir
  }

if [ "$1" ] ; then
  list_manifest "$1"
else
  cat <<EOF
Usage: $program_name JAR_FILE
EOF
fi
