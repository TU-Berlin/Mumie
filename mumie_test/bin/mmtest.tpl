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

# --------------------------------------------------------------------------------
# Auxiliary functions
# --------------------------------------------------------------------------------

# Function 'error' is defined in util/setup.sh

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

# Checks if a given test has been successfully executed. If this is the case, returns
# 0, otherwise non-0.
function check_status
  {
    local package=$1
    egrep "^$package"'[[:space:]].*[[:space:]]passed' $status_file > /dev/null 2>&1
  }

# Checks if certain tests have been successfully executed. This function is used to check
# if the tests a given test depends on have already been done and where successful.
function check_deps
  {
    [ "$1" ] || return 0
    local package
    for package in "$@" ; do
      if ! check_status $package ; then
        echo "Dependency not fullfilled: $package"
        return 1
      fi
    done
  }

# Writes the status of a package to the status file. Usage:
#
#  set_status PACKAGE STRAT_TIME STATUS ELAPSED_TIME
#
# (the arguments should be self-explanatory). If the status file already contains an entry
# for PACKAGE, it is removed before.

function set_status
  {
    local package=$1
    local start_time=$2
    local status=$3
    local elapsed_time=$4
    local format='%-28s %-22s %-10s %s\n'

    # Remove old entry for $package, if any:
    if [ -e $status_file ] ; then
      run_cmd sed "/^$package\>/d" $status_file > ${status_file}.tmp
      run_cmd mv ${status_file}.tmp $status_file
    fi

    # Write new entry:
    run_cmd printf \
      "$format" $package "$start_time" $status "$elapsed_time" >> $status_file
  }

# Checks if a mmtest process started with 'mmtest run [...]' exists. The possible return
# values of this function have the following meanings:
#
#  0   A pid file and a process with the corresponding pid exist
#  1   No pid file exists
#  2   A pid file exists, but no process with the corresponding pid

function check_running
  {
    if [ -e $pid_file ] ; then
      local pid=`cat $pid_file`
      if [ "`ps -p $pid --no-headers`" ] ; then
        return 0
      else
        return 2
      fi
    else
      return 1
    fi
  }

# Executes the test for a package. Usage:
#
#   run_test PACKAGE [DEP1 DEP2 ...]
#
# where DEP1, DEP2, ... denote packages the tests of which are required for the test
# of PACKAGE. If the requirements are not fullfilled, the test is skipped.
#
# The status file is updated when the test starts and ends, or when the test is
# skipped.

function run_test
  {
    local package=$1
    shift

    echo "Running test $package"

    local pkg_test=tests/${package}_test.sh
    local pkg_test_cleanup=tests/${package}_test_cleanup.sh
    local pkg_log=log/${package}_test.log
    local format='%-28s %-22s %-10s %s\n'
    local start_time=`date +"$date_format"`
    check_exit_code
    
    if check_deps "$@" ; then
    
      # Set status to 'started':
      set_status $package "$start_time" started

      # Current time in seconds since 00:00:00 Jan 1 1970:
      local start_time_sec=`date +%s`
      check_exit_code

      # Run the test:
      /bin/bash $pkg_test > $pkg_log 2>&1
      local exit_val=$?

      # Set status according to exit value:
      case $exit_val in
        0) local status=passed ;;
        *) local status=failed ;;
      esac

      # Get elapsed time:
      local elapsed_time=`$base_dir/util/difftime $start_time_sec`
      check_exit_code

      # Set status
      set_status $package "$start_time" $status "$elapsed_time"

    else
      set_status $package "$start_time" skipped
    fi
  }

# Stores information about the host, platform, processor, etc. in the 
# property file.

function store_sys_info
  {
    local host="`uname -n`"
    local kernel="`uname -s` `uname -r`"
    local machine="`uname -m`"
    local processor="`uname -p`"
    local platform="`uname -i`"
    local os_dist
    if [ -e /etc/SuSE-release ] ; then
      os_dist=`head -1 /etc/SuSE-release`
    else
      os_dist=unknown
    fi
    
    echo "\
System information:
-------------------
Host      = $host
Kernel    = $kernel
Machine   = $machine
Processor = $processor
Platform  = $platform
OS dist.  = $os_dist
" >> $sys_file 
  }

# Finds all descendents of a certain  process and prints their pids to stdout.
# Usage: get_proc_desc PID
# PID is the pid of the process.

function get_proc_desc
  {
    local pid=$1
    local desc=`ps -eo ppid,pid | awk '/^[ \t]*'$pid'/{print $2}'`
    if [ "$desc" ] ; then
      local dpid
      for dpid in $desc ; do
        echo $dpid
        get_proc_desc $dpid
      done
    fi
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

# Runs the tests specified in the test sequence file
function run_tests
  {
    local test_seq_file=$base_dir/test_sequences/${test_seq}.seq
    [ -e "$test_seq_file" ] || error "No such test sequence: $test_seq"

    local packages=`sed '/^#/d' $test_seq_file`
    check_exit_code

    check_running && error "Another mmtest process is already running"

    # Save pid in pid file:
    echo $$ > $pid_file

    # Start time in seconds since 00:00:00 Jan 1 1970:
    local total_start_time_sec=`date +%s`
    check_exit_code

    # Create test directory and unless existing:
    run_cmd mkdir -p $test_dir

    # Create 'packages' and 'tmp' subdirectories unless existing:
    run_cmd mkdir -p $test_dir/packages
    run_cmd mkdir -p $test_dir/tmp

    # Create log directory unless existing::
    run_cmd mkdir -p $log_dir

    # Save system information:
    store_sys_info

    # Load test_deps file:
    local test_deps=`sed '/^#/d' test_deps`
    check_exit_code

    # Do the tests:
    for package in $packages ; do
      local args=`echo "$test_deps" | egrep "^$package\b"`
      [ "$args" ] || error "Unknown test: $package"
      run_test $args
    done

    # Get elapsed time:
    local total_elapsed_time=`$base_dir/util/difftime $total_start_time_sec`
    check_exit_code

    # Save elapsed time in time file:
    echo "$total_elapsed_time" > $time_file

    # Remove pid file:
    rm -f $pid_file
  }

# Runs the tests as a daemon.
function start_tests
  {
    mmdaemon util/start_tests.sh $test_seq
  }

# Removes the test dir and the log dir.
function clear_tests
  {

    local bashrc_dir=$test_dir/packages/japs_env/conf/bash
    # Stop tomcat if installed and running:
    if [ -e "$bashrc_dir/tomcat_bashrc" ] ; then
      source $bashrc_dir/tomcat_bashrc
      tomcat stop
    fi

    # Stop apache if installed and running:
    if [ -e "$bashrc_dir/apache_bashrc" ] ; then
      source $bashrc_dir/apache_bashrc
      apache stop
    fi

    # Stop postgres if installed and running:
    if [ -e "$bashrc_dir/postgres_bashrc" ] ; then
      source $bashrc_dir/postgres_bashrc
      postgres stop
    fi

    # Stop mmjvmd if installed and running:
    which mmjvmd && [ "`mmjvmd status`" == 'Jvmd is running' ] && mmjvmd stop-force


    run_cmd rm -rf $test_dir
    rm -rf $log_dir
  }

# Prints the status file to stdout. If the 'use_colors' flag is set (the default),
# the output is colorized.
function print_status
  {
    if [ ! -e "$status_file" ] ; then
      echo "Status file does not exist"
    elif [ "$use_colors" ] ; then
      local red=`echo -en '\033[31m'`
      local green=`echo -en '\033[32m'`
      local magenta=`echo -en '\033[35m'`
      local cyan=`echo -en '\033[36m'`
      local reset=`echo -en '\033[00m'`
      echo "$status_table_head"
      cat $status_file \
        | sed "s/passed/${green}passed${reset}/g" \
        | sed "s/failed/${red}failed${reset}/g" \
        | sed "s/skipped/${cyan}skipped${reset}/g" \
        | sed "s/started/${magenta}started${reset}/g"
      if [ -e $time_file ] ; then
        echo "$status_table_footer `cat $time_file`"
      fi
    else
      echo "$status_table_head"
      cat $status_file
    fi
  }

function watch_status
  {
    while true ; do
      clear
      print_status
      sleep $watch_interval
    done
  }

function report_status
  {
    if [ ! -e "$status_file" ] ; then
      echo "Status file does not exist"
    elif [ ! -e "$sys_file" ] ; then
      echo "System info file does not exist"
    elif [ ! -e "$time_file" ] ; then
      echo "Tests are not completed yet"
    elif [ ! "$recipients" ] ; then
      echo "No recipients specified"
    else
      run_cmd tar czf log.tgz $log_dir
      run_cmd echo "
MUMIE TEST RESULTS
==================

The following table shows the results of the Mumie software tests carried out
by the mmtest command (package mumie_test). Detailed log messages for each test
are contained in the attached log.tgz file.

$status_table_head
`cat $status_file`
$status_table_footer `cat $time_file`

`cat $sys_file`

This mail was autogenerated by mmtest.

" | run_cmd $mail_cmd -s "Mumie test" -a log.tgz $recipients
      run_cmd rm -f log.tgz
    fi
  }

function abort_tests
  {
    if check_running ; then

      # Abort all descendent processes:
      local root_pid=`cat $pid_file`
      local pids="$root_pid `get_proc_desc $root_pid`"
      local pid
      for pid in $pids ; do
        echo "Stopping process $pid"
        kill -9 $pid || true
        [ "`ps -p $pid --no-headers`" ] && sleep 1
        if [ "`ps -p $pid --no-headers`" ] ; then
          echo "WARNING: process $pid could not be stopped" 1>&2
        fi
      done

      local bashrc_dir=$test_dir/packages/japs_env/conf/bash

      # Stop tomcat if installed and running:
      if [ -e "$bashrc_dir/tomcat_bashrc" ] ; then
        source $bashrc_dir/tomcat_bashrc
        tomcat stop
      fi

      # Stop apache if installed and running:
      if [ -e "$bashrc_dir/apache_bashrc" ] ; then
        source $bashrc_dir/apache_bashrc
        apache stop
      fi

      # Stop postgres if installed and running:
      if [ -e "$bashrc_dir/postgres_bashrc" ] ; then
        source $bashrc_dir/postgres_bashrc
        postgres stop
      fi

      # Stop mmjvmd if installed and running:
      which mmjvmd && [ "`mmjvmd status`" == 'Jvmd is running' ] && mmjvmd stop-force

      echo "Mmtest aborted"

    else
      echo "Mmtest is not running"
    fi
  }

function cvs_tag
  {
    local package
    run_cmd cd $test_dir/packages
    for package in * ; do
      echo "Tagging $package"
      run_cmd cd $package
      run_cmd cvs tag $tag . > $log_dir/${package}_tag.log 2>&1
      run_cmd cd ..
    done
  }  

# Prints the variables to stdout
function print_vars
  {
    cat <<EOF
auto_commit       = $auto_commit
base_dir          = $base_dir
cvs_checkout_opts = $cvs_checkout_opts
cvs_root          = $cvs_root
date_format       = $date_format
gcc_options       = $gcc_options
japs_url_prefix   = $japs_url_prefix
java4_home        = $java4_home
java5_home        = $java5_home
log_dir           = $log_dir
mail_cmd          = $mail_cmd
pid_file          = $pid_file
recipients        = $recipients
signjar_alias     = $signjar_alias
signjar_storepass = $signjar_storepass
status_file       = $status_file
sys_file          = $sys_file
test_dir          = $test_dir
time_file         = $time_file
tomcat_heap_space = $tomcat_heap_space
use_colors        = $use_colors
EOF
  }

# Prints a help text
function print_help
  {
    cat <<EOF
General usage:
  $program_name CMD [CMD_SPECIFIC_PARAMS]
  $program_name --vars | --help | -h | --version | -v
Description:
  In the first form, CMD stands for a command and CMD_SPECIFIC_PARAMS for a
  list of parameters passed to the command. Possible commands are: run, clear,
  status, watch-status, and report.
Usage and description for each command:
  $program_name run  [TEST_SEQUENCE]
      Runs the tests of the specified test sequence, or, if no test sequence is
      specified, the tests in the test sequence "default_tests".
  $program_name start  [TEST1 TEST2 ...]
      Same as $program_name run  [TEST1 TEST2 ...], but the tests are executed
      in the background and the command immediatly returns.
  $program_name clear
      Removes the test directory and the log directory.
  $program_name status [ --no-colors | -C ]
      Prints the current status of the tests to stdout. If the --no-colors or -C
      option is provided, color is suppressed in the output.
  $program_name abort
      Aborts the current test.
  $program_name tag TAG
      Sets TAG as a cvs tag for all packages of the last test.
  $program_name watch-status [ --no-colors | -C ] [ --intervall N | i- N ]
      Similar to $program_name status, but refreshs the output every N seconds.
      N defaults to 2. Runs until terminated by Ctrl-C.
  $program_name report
      Sends a status report to the e-mail addresses specified in the
      'recipients' variable which may be set in the config file.
Options in the second form of the general usage:
  --vars
      Prints the variables which control the tests
  --help, -h
      Prints this help text and exits
  --version, -v
      Prints version information and exits
EOF
  }

# --------------------------------------------------------------------------------
# Main program
# --------------------------------------------------------------------------------

# Set prgram name:
readonly program_name=mmtest

# Change to mmtest directory:
run_cmd cd @mmtest-dir@

# Customizable variable defaults:
use_colors=yes
watch_interval=2

# Source global setup file
source util/setup.sh --no-abort

# Other variable defaults:
unset task
test_seq=default_tests

# Process command line parameters:
params=`getopt \
  --longoptions no-colors,intervall:,vars,help,version \
  --options i:,C,h,v \
  -- \
  "$@"` || exit 1
eval set -- "$params"
while true ; do
  case "$1" in
    --help|-h) task=print_help ; shift ;;
    --version|-v) task=print_version ; shift ;;
    --vars) task=print_vars ; shift ;;
    --no-colors|-C) use_colors=no ; shift ;;
    --interval|-i) watch_interval="$2" ; shift 2 ;;
    --) shift ; break ;;
  esac
done

# "Normalize" use_colors flag:
use_colors=`normalize_boolean $use_colors`

# Set task by command if necessary:
if [ ! "$task" ] ; then
  [ "$1" ] || error "No command specified"
  echo "$1" | egrep '^(run|start|clear|status|abort|tag|watch-status|report)$' > /dev/null \
    || error "Unknown command: $1"
  task=$1
  shift
fi

# Process remaining parameters:
if [ "$@" ] ; then
  if [ $task = 'run' ] || [ $task = 'start' ] ; then
    test_seq="$1"
    shift
    [ "$@" ] && error "Extra paramters: $@"
  elif [ $task = 'tag' ] ; then
    tag="$1"
    shift
    [ "$@" ] && error "Extra paramters: $@"
  elif [ "$@" ] ; then
    error "Extra paramters: $@"
  fi
fi

# Status table head:
status_table_head="\
Test                         Started                Status     Elapsed time
---------------------------------------------------------------------------"

# Status table footer:
status_table_footer="\
                                                               ------------
                                                        Total:"

case $task in
  run) run_tests ;;
  start) start_tests ;;
  clear) clear_tests ;;
  status) print_status ;;
  abort) abort_tests ;;
  watch-status) watch_status ;;
  abort) abort_tests ;;
  report) report_status ;;
  tag) cvs_tag ;;
  print_vars) print_vars ;;
  print_help) print_help ;;
esac
