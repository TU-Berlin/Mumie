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

# Constants:
readonly prefix=@prefix@
readonly lib_dir=$prefix/lib
readonly java_lib_dir=$lib_dir/java
readonly conf_dir=$prefix/etc/mmjvmd
readonly conf_file=$conf_dir/mmjvmd.conf
readonly init_file=$conf_dir/mmjvmd.init
readonly user_jvmd_dir=${MMJVMD_DIR:-$HOME/.mmjvmd}
readonly user_conf_file=$user_jvmd_dir/mmjvmd.conf
readonly user_init_file=$user_jvmd_dir/mmjvmd.init
readonly log_file=$user_jvmd_dir/mmjvmd.log
readonly socket_file=$user_jvmd_dir/mmjvmd.socket

# Variables customizable in the config files:
log_max_files=10
log_max_records=10000
log_date_format="yyyy-MM-dd HH:mm:ss S"
jvmc_cmd=$prefix/bin/mmjvmc
start_timeout=30
classpath=\
$java_lib_dir/mumie-util.jar\
:$java_lib_dir/mumie-ipc.jar\
:$java_lib_dir/mumie-jvmd.jar

# Source config file if exists:
[ -e $conf_file ] && . $conf_file

# Source user config file if exists:
[ -e $user_conf_file ] && . $user_conf_file

# Library path (for native library libmmjipc):
export LD_LIBRARY_PATH=$lib_dir

# Classpath:
export CLASSPATH=$classpath

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

# Checks if Jvmd is reachable
function check_running
  {
    $jvmc_cmd --ping > /dev/null 2>&1
  }

# Tries to stop the server; if the server refuses to stop because of active clients,
# waits one second and tries again. This is repeated until either the server accepts
# the stop request or the timeout is reached.
function try_stop_mmjvmd
  {
    echo "Waiting for Jvmd to accept stop request ..."
    local count=0
    local accepted
    local output
    while [ $count -lt "$start_timeout" ] ; do
      output=`$jvmc_cmd --stop-server 2>&1` && accepted=set
      [ "$accepted" ] && break
      echo "$output" | fgrep "Stop refused by server: Active clients exist" || error "$output"
      sleep 1
      let count++
    done

    # Throw an error if server did not not accept stop request:
    [ "$accepted" ] || error "Timeout. Jvmd refused stopping because of active clients"

    echo "Jvmd accepted stop request"
  }

# Starts the server
function start_server
  {
    # Check if Jvmd is already running:
    if check_running ; then
      echo "Jvmd is already running"
      return
    fi

    # Check if socket file exists (should not):
    if [ -e $socket_file ] ; then
      warn "Old socket file exists; will be removed"
      rm -f $socket_file || error "Failed to remove old socket file"
    fi

    # Create Jvmd dir if necessary:
    if [ ! -e $user_jvmd_dir ] ; then
      mkdir -p $user_jvmd_dir
      if [ $? -eq 0 ] ; then
        echo "Created Jvmd directory"
      else
        error "Failed to create Jvmd directory"
      fi
    fi

    # Compose -Xms and/or -Xmx parameters if initial_mem and/or max_mem is set:
    local initial_mem_param=''
    [ "$initial_mem" ] && initial_mem_param="-Xms${initial_mem}"
    local max_mem_param=''
    [ "$max_mem" ] && max_mem_param="-Xmx${max_mem}"

    local endorsed_dirs_param=''
    [ "$endorsed_dirs" ] && endorsed_dirs_param="-Djava.endorsed.dirs=$endorsed_dirs"

    # Start Java application:
    java \
      $initial_mem_param \
      $max_mem_param \
      $endorsed_dirs_param \
      -Dnet.mumie.jvmd.socketFile="$socket_file" \
      -Dnet.mumie.util.logFile="$log_file" \
      -Dnet.mumie.util.logMaxFiles="$log_max_files" \
      -Dnet.mumie.util.logMaxRecords="$log_max_records" \
      -Dnet.mumie.util.logDateFormat="$log_date_format" \
      net.mumie.jvmd.Jvmd &
    local pid=$!
    echo "Started Java"

    # Wait until the server is running:
    echo "Waiting for Jvmd ..."
    local count=0
    local running=''

    while [ $count -lt "$start_timeout" ] ; do
      # Check if the Java process is still running:
      ps -p $pid > /dev/null 2>&1 || error "Server died unexpectedly"
      # Check if the server already accepts connections:
      check_running && running=set
      [ "$running" ] && break
      sleep 1
      let count++
    done

    # Throw an error if server is not running:
    if [ ! "$running" ] ; then
      kill -9 $pid
      error "Timeout while waiting for server to start"
    fi

    # Source init file if exists:
    [ -e $init_file ] && . $init_file

    # Source user init file if exists:
    [ -e $user_init_file ] && . $user_init_file

    echo "Jvmd is running"
  }

function stop_server
  {
    if check_running ; then

      # Send stop request:
      $jvmc_cmd --stop-server || exit 1
      echo "Sent stop request to Jvmd"

      # Wait until the server is shut down:
      echo "Waiting for Jvmd to exit ..."
      local count=0
      local stopped
      while [ $count -lt "$start_timeout" ] ; do
        check_running || stopped=set
        [ "$stopped" ] && break
        sleep 1
        let count++
      done

      # Throw an error if server is not stopped:
      [ "$stopped" ] || error "Timeout while waiting for server to shutdown"

      echo "Jvmd is shut down"

    else
      echo "Jvmd is not running"
    fi
  }

function stop_server_force
  {
    if check_running ; then

      # Send stop-force request:
      $jvmc_cmd --stop-server-force || exit 1
      echo "Sent forced stop request to Jvmd"

      # Wait until the server is shut down:
      echo "Waiting for Jvmd to exit ..."
      local count=0
      local stopped
      while [ $count -lt "$start_timeout" ] ; do
        check_running || stopped=set
        [ "$stopped" ] && break
        sleep 1
        let count++
      done

      # Throw an error if server is not stopped:
      [ "$stopped" ] || error "Timeout while waiting for server to shutdown"

      echo "Jvmd is shut down"

    else
      echo "Jvmd is not running"
    fi
  }

function stop_server_retry
  {
    if check_running ; then

      # Send stop request, retry several times id not accepted:
      try_stop_mmjvmd

      # Wait until the server is shut down:
      echo "Waiting for Jvmd to exit ..."
      local count=0
      local stopped
      while [ $count -lt "$start_timeout" ] ; do
        check_running || stopped=set
        [ "$stopped" ] && break
        sleep 1
        let count++
      done

      # Throw an error if server is not stopped:
      [ "$stopped" ] || error "Timeout while waiting for server to shutdown"

      echo "Jvmd is shut down"

    else
      echo "Jvmd is not running"
    fi
  }

function server_status
  {
    if check_running ; then
      echo "Jvmd is running"
    else
      echo "Jvmd is not running"
    fi
  }

function show_help
  {
    cat <<EOF
Usage:
  mmjvmd start | stop | stop-force | status
  mmjvmd --help | -h | --version | -v
Arguments:
  start
      Starts the server
  stop
      Stops the server provided no active clients exist
  stop-force
      Stops the server and all active clients
  stop-retry
      Waits until no active clients exist and stops the server. If after
      a certain timeout (${start_timeout}s) still active clients exist,
      the server is not stopped.
  status
      Prints whether jvmd is running or not
Options:
  --help, -h
      Prints this help message and exits
  --version, -v
      Prints version information and exits
EOF
  }

function show_variables
  {
     cat <<EOF
classpath       = $classpath
conf_dir        = $conf_dir
conf_file       = $conf_file
endorsed_dirs   = $endorsed_dirs
init_file       = $init_file
initial_mem     = $initial_mem
java_lib_dir    = $java_lib_dir
jvmc_cmd        = $jvmc_cmd
lib_dir         = $lib_dir
log_date_format = $log_date_format
log_file        = $log_file
log_max_files   = $log_max_files
log_max_records = $log_max_records
max_mem         = $max_mem
prefix          = $prefix
socket_file     = $socket_file
start_timeout   = $start_timeout
user_conf_file  = $user_conf_file
user_init_file  = $user_init_file
user_jvmd_dir   = $user_jvmd_dir
EOF
  }

function show_usage
  {
    cat <<EOF
Usage:
  mmjvmd start | stop | stop-force | status
  mmjvmd --help | -h | --version | -v | --vars
EOF
  }

function show_version
  {
    echo '$Revision: 1.16 $'
  }

# Main program:
if [ ! "$1" ] ; then
  task=show_usage
else
  case "$1" in
    start) task=start_server ;;
    stop) task=stop_server ;;
    stop-force) task=stop_server_force ;;
    stop-retry) task=stop_server_retry ;;
    status) task=server_status ;;
    --help|-h) task=show_help ;;
    --version|-v) task=show_version ;;
    --vars) task=show_variables ;;
    *) error "Unknown parameter: $1"
  esac
  shift
  [ "$1" ] && error "Extra parameters: $*"
fi

$task
