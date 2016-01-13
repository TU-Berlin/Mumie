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

# Exit on error unless --no-abort option is specified:
[ "$1" == --no-abort  ] || set -e

# Init customizable variables with void values:
test_dir=''
log_dir=''
status_file=''
sys_file=''
time_file=''
pid_file=''
cvs_root=''
signjar_alias=''
signjar_storepass=''
date_format=''
mail_cmd=''
gcc_options=''
tomcat_heap_space=''
japs_url_prefix=''
auto_commit=''

# Source config file:
readonly config_file=mmtest.conf
[ -e "$config_file" ] && source "$config_file"

# Current directory:
base_dir=`pwd`

# Set customizable variables if not set already:
test_dir=${test_dir:-$base_dir/work}
log_dir=${log_dir:-$base_dir/log}
status_file=${status_file:-$log_dir/mmtest.status}
sys_file=${sys_file:-$log_dir/mmtest.sys}
time_file=${time_file:-$log_dir/mmtest.time}
pid_file=${pid_file:-$base_dir/mmtest.pid}
auto_commit=${auto_commit:-yes}
cvs_root=${cvs_root:-/net/mumie/cvs}
signjar_alias=${signjar_alias:-mumie}
signjar_storepass=${signjar_storepass:-japsen}
date_format=${date_format:-'%Y-%m-%d %H:%M:%S'}
mail_cmd=${mail_cmd:-mail}

# Set non-customizable variables:

# Set environment variables:
export MM_BUILD_PREFIX=$test_dir
export MM_CHECKIN_ROOT=$test_dir/checkin
export PATH=$test_dir/bin:$PATH
export MMJVMD_DIR=$test_dir/mmjvmd
export MMCDK_DIR=$test_dir/mmcdk

# Aborts with an error message
function error
  {
    echo "ERROR: $*" 1>&2
    echo 1>&2
    exit 1
  } 

# function to print the header
function print_header
  {
    local package=$1
    echo
    echo "================================================================================"
    echo "$package"
    echo "================================================================================"
    echo
  }

# "Normalizes" a boolean value. Usage:
#
#   normalize_boolean VALUE
#
# If VALUE is "true" or "yes", outputs "enabled". Otherwise, if VALUE is "false" or "no",
# outputs the empty string. Thus, if a variable 'foo' is given having one of these values,
# then
#
#   foo=`normalize_boolean $foo`
#
# turns foo into a variable which can be used in boolean tests like
#
#   if [ "$foo" ] ; then ...

function normalize_boolean
  {
    case "$1" in
      true|yes)
        echo enabled ;;
      false|no)
        echo '' ;;
      *)
        echo "$1" ;;
    esac
  }

# "Normalize" auto_commit flag:
auto_commit=`normalize_boolean $auto_commit`
