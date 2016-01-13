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

# Set fixed variabes (constants):
readonly program_name=apache
readonly program_version='$Revision: 1.2 $'

# Set variables:
port=@apache-port@
apache_home=${APACHE_HOME:-@apache-home@}
pid_file=$apache_home/logs/httpd.pid
timeout=20

# Aborts with an error message
function error
  {
    echo "ERROR: $*"
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

# Checks if apache is running
function is_running
  {
    [ -e "$pid_file" ] && echo "running"
  }

# Starts apache
function start_apache
  {
    if [ "`is_running`" ] ; then
      echo "Apache is already running"
      return
    fi 

    [ "$apache_home" ] || error "apache_home not set"
    run_cmd $apache_home/bin/httpd -k start > /dev/null
    echo "Started Apache process"
    echo -n "Waiting ..."

    local time=0;
    while [ "$time" -le "$timeout" ] && [ ! "`is_running`" ] ; do
      sleep 1
      echo -n "."
      let "time+=1"
    done

    if [ "`is_running`" ] ; then
      echo " ok"
      echo "Apache is running"
    else
      echo " failed"
      error "Timeout while waiting for Apache to start"
    fi
  }

# Stopps apache
function stop_apache
  {
    if [ ! "`is_running`" ] ; then
      echo "Apache is not running"
      return
    fi 

    [ "$apache_home" ] || error "apache_home not set"
    run_cmd $apache_home/bin/httpd -k stop > /dev/null
    echo "Requested Apache to stop"
    echo -n "Waiting ..."

    local time=0;
    while [ "$time" -le "$timeout" ] && [ "`is_running`" ] ; do
      sleep 1
      echo -n "."
      let "time+=1"
    done

    if [ ! "`is_running`" ] ; then
      echo " ok"
      echo "Apache is stopped"
    else
      echo " failed"
      error "Timeout while waiting for Apache to stop"
    fi
  }

# Prints status information to stdout
function show_apache_status
  {
    if [ "`is_running`" ] ; then
      echo "Apache is running"
    else
      echo "Apache is not running"
    fi
  }

# Prints a help text to stdout and exits.
function show_help
  {
    cat <<EOF
Usage:
  $program_name start | stop | status
  $program_name --help | -h | --version | -v
Description:
  Performs a certain operation with Apache (e.g., starting or stopping).
Operations:
  start
      Starts Apache. The program waits until Apache is ready.
  stop
      Stops Apache. The program waits until Apache is shutdown.
  status
      Prints on stdout whether Apache is running or not.
Options:
  --help, -h
      Print this help text and exit.
  --version, -v
      Print version information and exit.
EOF
  }

# Prints version information to stdout and exits.
function show_version
  {
    echo $program_version
  }

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
    --) shift ; break ;;
  esac
done
if [ ! "$task" ] ; then
  case "$1" in
    start) task=start_apache ;;
    stop) task=stop_apache  ;;
    status) task=show_apache_status ;;
    "" ) error "Missing task" ;;
    *) error "Unknown task: $1" ;;
  esac
fi
shift
[ "$*" ] && error "Extra parameter(s): $*"

# Execute task:
$task
