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
# $Id: mmconmon.tpl,v 1.5 2008/05/02 09:26:04 rassy Exp $

# Set fixed variabes (constants):
readonly program_name=mmconmon
readonly program_version='$Revision: 1.5 $'
readonly conf_file=${program_name}.conf

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

# Sets the classpath:
function set_classpath
  {
    local part
    local classpath
    for part in $* ; do
      if [ "$classpath" ] ; then
        classpath="$classpath:$part"
      else
       classpath=$part
      fi
    done
    run_cmd export CLASSPATH=$classpath
  }

# Checks if an URL prefix file exists, and aborts with an error message if not
function check_url_prefix_file
  {
    [ -e "$url_prefix_file" ] || \
      error "No $url_prefix_file file - Not a $program_name working directory?"
  }

# --------------------------------------------------------------------------------
# Functions implementing commands
# --------------------------------------------------------------------------------

function start_monitor
  {
    check_url_prefix_file
    local url_prefix=`cat $url_prefix_file`

    set_classpath \
      $java_lib_dir/mumie-util.jar \
      $java_lib_dir/mumie-japs-client.jar \
      $java_lib_dir/mumie-server-check.jar \
      $java_lib_dir/jcookie-0.8c.jar \

    local java_opts=''
    [ "$trust_store" ] && java_opts="-Djavax.net.ssl.trustStore=$trust_store"

    java $java_opts net.mumie.servercheck.ConnectionMonitor \
      $url_prefix \
      $path \
      $account \
      $password \
      $interval \
      $log_file \
      $max_log_files \
      $max_log_records \
      "$date_format" \
      > /dev/null \
      2> $err_file \
      &

    echo $! > $pid_file
  }

function stop_monitor
  {
    check_url_prefix_file

    if [ ! -e "$pid_file" ] ; then
      echo "$program_name is not running"
      return
    fi

    kill `cat $pid_file`
    run_cmd rm $pid_file
  }

function show_status
  {
    check_url_prefix_file

    if [ -e "${log_file}.01" ] ; then
      local total_errors=`fgrep ERROR ${log_file}.* | wc -l`
      check_exit_code
    fi

    if [ -e "$pid_file" ] ; then
      echo "$program_name is running (pid = `cat $pid_file`)"
      [ "$total_errors" ] && echo "$total_errors error(s)"
    else
      echo "$program_name is not running"
      [ "$total_errors" ] && echo "$total_errors error(s) in last run"
    fi
  }

function clear_files
  {
    check_url_prefix_file

    run_cmd rm -fv ${log_file}.*
    local msg_file
    for msg_file in $err_file $out_file ; do
      [ "$msg_file" != /dev/null ] && rm -vf $msg_file
    done
  }

function show_logs
  {
    check_url_prefix_file
    if [ -e "${log_file}.01" ] ; then
      cat ${log_file}.* | less
    else
      echo "No log files found"
    fi
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

function execute_command
  {
    [ "$cmd" ] || error "No command specified"

    case "$cmd" in
      start) start_monitor ;;
      stop) stop_monitor ;;
      status) show_status ;;
      clear) clear_files ;;
      log) show_logs ;;
      *) error "Unknown commmand: $cmd"
    esac
  }

function print_variables
  {
    cat <<EOF
account         = $account
date_format     = $date_format
err_file        = $err_file
interval        = $interval
java_lib_dir    = $java_lib_dir
log_file        = $log_file
max_log_files   = $max_log_files
max_log_records = $max_log_records
out_file        = $out_file
password        = $password
path            = $path
pid_file        = $pid_file
prefix          = $prefix
trust_store     = $trust_store
EOF
  }

# --------------------------------------------------------------------------------
# Main program
# --------------------------------------------------------------------------------

# Customizable variable defaults:
unset url_prefix
path='protected/alias/start'
account=guest
password=guest
interval=60
unset trust_store
log_file=${program_name}.log
out_file=/dev/null
err_file=${program_name}.err
pid_file=${program_name}.pid
max_log_files=10
max_log_records=10000
date_format='yyyy-MM-dd HH:mm:ss'

# Source config file if exists:
[ -e $conf_file ] && source $conf_file

# Default task:
task=execute_command

# Process command line parameters:
params=`getopt \
  --longoptions help,version,vars \
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
    --) shift ; break ;;
  esac
done
cmd=$1
shift
[ "$1" ] && error "Extra parameter(s): $*"

# Non-customizable varaibles:
prefix=${MM_BUILD_PREFIX:-@prefix@}
java_lib_dir=$prefix/lib/java
url_prefix_file=url_prefix

# Execute task:
$task