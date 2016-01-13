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

# Constants:
readonly prefix=@prefix@
readonly conf_dir=$prefix/etc/mmjvmd
readonly conf_file=$conf_dir/mmjvmw.conf
readonly user_jvmd_dir=$HOME/.mmjvmd
readonly user_conf_file=$user_jvmd_dir/mmjvmw.conf
readonly log_file=$user_jvmd_dir/mmjvmw.log
readonly pid_file=$user_jvmd_dir/mmjvmw.pid

# Variables customizable in the config files:
jvmc_cmd=$prefix/bin/mmjvmc
jvmd_cmd=$prefix/bin/mmjvmd
jvmw_cmd=$prefix/bin/mmjvmw
daemon_cmd=$prefix/bin/mmdaemon
check_intervall=10

# Source config file if exists:
[ -e $conf_file ] && . $conf_file

# Source user config file if exists:
[ -e $user_conf_file ] && . $user_conf_file

user=`whoami`

# Prints a message to stderr and exits
function error
  {
    echo "ERROR: $*" >&2
    exit 1
  }

# Prints a message to stderr, but does not exit
function warn
  {
    echo "WARNING: $*" >&2
  }

# Auxiliary function, checks if a process with a given pid exists
function process_exists
  {
    ps -p $1 > /dev/null 2>&1
  }

# Auxiliary function, removes the pid file if existing and exits.
function terminate
  {
    rm -f $pid_file
    exit 0
  }

# Runs the watch process
function run
  {
    # Check if mmjvmw is already running or an old pid file exists:
    if [ -e "$pid_file" ] ; then
      local pid=`cat $pid_file`
      if process_exists $pid ; then
        echo "Jvmw is already running"
        exit 0
      else
        warn "Old pid file exists; will be removed"
      fi
    fi

    # Save pid in flie:
    echo $$ > $pid_file

    # Register signal handlers:
    trap terminate SIGTERM
    trap terminate SIGINT

    while true ; do
      sleep $check_intervall
      if ! users | fgrep $user > /dev/null ; then
        if $jvmc_cmd --ping > /dev/null 2>&1 ; then
          $jvmd_cmd stop-force
          echo "`date +'%Y-%m-%d %H:%M:%S %N'`" > $log_file
          echo "Stopped mmjvmd because user is no longer logged-in" >> $log_file
        fi
        terminate
      fi
    done
  }

# Starts the watch process
function start
  {
    $daemon_cmd $jvmw_cmd run
  }

# Stops the watch process
function stop
  {
    if [ -e $pid_file ] ; then
      local pid=`cat $pid_file`
      if process_exists $pid ; then
        echo "Sending SIGTERM to mmjvmw process ... "
        if kill `cat $pid_file` ; then
          echo "Done"
        else
          echo "Failed"
        fi
      else
        echo "Jvmw pid file exists (pid = $pid), but process is not running"
      fi
    else
      echo "Jvmw is not running"
    fi      
  }

# Checks if a watch process is runing
function status
  {
    if [ -e "$pid_file" ] ; then
      local pid=`cat $pid_file`
      if process_exists $pid ; then
        echo "Jvmw is running"
      else
        echo "Jvmw pid file exists (pid = $pid), but process is not running"
      fi
    else
      echo "Jvmw is not running"
    fi
  }

function show_help
  {
    cat <<EOF
Usage:
  mmjvmw start | stop | run | status
  mmjvmd --help | -h | --version | -v
Arguments:
  start
      Starts the watch process in the background. The command returns
      immediately.
  stop
      Stopps the watch process
  run
      Runs the watch process in the foreground. This is used internally when
      'mmjvmw start' is called. You should not use this directly.
  status
      Prints whether the watch process is running or not.
Options:
  --help, -h
      Prints this help message and exits
  --version, -v
      Prints version information and exits
EOF
  }

function show_usage
  {
    echo "Usage: mmjvmw start | stop | run | status | --help | -h | --version | -v"
  }

function show_version
  {
    echo '$Revision: 1.2 $'
  }

# Main program:
if [ ! "$1" ] ; then
  task=show_usage
else
  case "$1" in
    start) task=start ;;
    stop) task=stop ;;
    run) task=run ;;
    status) task=status ;;
    --help|-h) task=show_help ;;
    --version|-v) task=show_version ;;
    *) error "Unknown parameter: $1"
  esac
  shift
  [ "$1" ] && error "Extra parameters: $*"
fi

$task
