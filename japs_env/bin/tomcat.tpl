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

# If running as root, change to unprivileged user:
if [ "`whoami`" = root ] ; then
  su -c "tomcat $@" @mumie-user@
  exit $?
fi

# Set fixed variabes (constants):
readonly program_name=tomcat
readonly program_version='$Revision: 1.6 $'

# Set variables:
ajp_port=8009
tomcat_home=${TOMCAT_HOME:-@tomcat-home@}
tomcat_heap_space=@tomcat-heap-space@
timeout=20

# Source user config file:
user_config_file=$tomcat_home/conf/local/${program_name}.conf
[ -e $user_config_file ] && source $user_config_file

# Non-customizable variables:
tomcat_log_dir=$tomcat_home/logs
export CATALINA_PID=$tomcat_log_dir/catalina.pid

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

# Checks if tomcat is running
function is_running
  {
    run_cmd netstat -ant | fgrep $ajp_port | fgrep -i listen
  }

# Prints status information to stdout
function show_tomcat_status
  {
    if [ "`is_running`" ] ; then
      echo "Tomcat is running"
    else
      echo "Tomcat is not running"
    fi
  }

# Starts tomcat
function start_tomcat
  {
    if [ "`is_running`" ] ; then
      echo "Tomcat is already running"
      return
    fi 

    [ "$tomcat_home" ] || error "tomcat_home not set"

    export CATALINA_OPTS="$CATALINA_OPTS -Xmx${tomcat_heap_space}m"

    run_cmd $tomcat_home/bin/catalina.sh start > /dev/null

    echo "Started Tomcat process"
    echo -n "Waiting ..."

    local time=0;
    while [ "$time" -le "$timeout" ] && [ ! "`is_running`" ] ; do
      sleep 1
      echo -n "."
      let "time+=1"
    done

    if [ "`is_running`" ] ; then
      echo " ok"
      echo "Tomcat is running"
    else
      echo " failed"
      error "Timeout while waiting for Tomcat to start"
    fi
  }

# Stopps tomcat
function stop_tomcat
  {
    if [ ! "`is_running`" ] ; then
      echo "Tomcat is not running"
      return
    fi 

    [ "$tomcat_home" ] || error "tomcat_home not set"
    run_cmd $tomcat_home/bin/catalina.sh stop > /dev/null
    echo "Requested Tomcat to stop"
    echo -n "Waiting ..."

    local time=0;
    while [ "$time" -le "$timeout" ] && [ "`is_running`" ] ; do
      sleep 1
      echo -n "."
      let "time+=1"
    done

    if [ ! "`is_running`" ] ; then
      echo " ok"
      echo "Tomcat is stopped"
    else
      echo " failed"
      error "Timeout while waiting for Tomcat to stop"
    fi
  }

# Removes the temporary Cocoon files
function clear_cocoon_tmp
  {
    [ !"`is_running`" ] || error "Tomcat is still running"
    [ "$tomcat_home" ] || error "tomcat_home not set"
    run_cmd rm -rvf $tomcat_home/work/Catalina/localhost/cocoon
  }

# Removes the Cocoon log files
function clear_cocoon_logs
  {
    [ !"`is_running`" ] || error "Tomcat is still running"
    [ "$tomcat_home" ] || error "tomcat_home not set"
    run_cmd rm -vf $tomcat_home/webapps/cocoon/WEB-INF/logs/*
  }

# Prints a help text to stdout and exits.
function show_help
  {
    cat <<EOF
Usage:
  $program_name start | stop | status | clear-tmp | clear-logs
  $program_name --help | -h | --version | -v
Description:
  Performs a certain operation with Tomcat (e.g., starting or stopping).
Operations:
  start
      Starts Tomcat. The program waits until Tomcat is ready.
  stop
      Stops Tomcat. The program waits until Tomcat is shutdown.
  status
      Prints on stdout whether Tomcat is running or not.
  clear-tmp
      Deletes the temporary Cocoon files.
  clear-logs
      Deletes the Cocoon log files.
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
    start) task=start_tomcat ;;
    stop) task=stop_tomcat  ;;
    status) task=show_tomcat_status ;;
    clear-tmp) task=clear_cocoon_tmp ;;
    clear-logs) task=clear_cocoon_logs ;;
    "" ) error "Missing task" ;;
    *) error "Unknown task: $1" ;;
  esac
fi
shift
[ "$*" ] && error "Extra parameter(s): $*"

# Execute task:
$task
