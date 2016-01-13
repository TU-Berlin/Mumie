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

# Authors Tilman Rassy <rassy@math.tu-berlin.de>

# This is the part of the setup_postgres function in the main script (build.sh)
# which must be run as a different user if installation is done as root.
#
# Usage:
#
#   tools/setup_postgres.sh POSTGRES_BASHRC POSTGRES_HOME POSTGRES_JAR TOMCAT_HOME
#
# This script is called from the main script. It should not be run by hand.

# Program name (name of the main script):
program_name=build.sh

# Variables set in the command line:
postgres_bashrc="$1"
postgres_home="$2"
postgres_jar="$3"
tomcat_home="$4"
initdb_options="$5"

# A slightly different version of run_cmd, compared to that in the main script.
# Does not print an error message (this is done in the main script); and the exit
# code in the case of an error is that of the command (not 1).
function run_cmd
  {
    "$@"
    local exit_code=$?
    if [ "$exit_code" -ne 0 ] ; then
      local pg_ctl=$postgres_home/bin/pg_ctl
      [ -e "$pg_ctl" ] && $pg_ctl status && $pg_ctl stop fast
      exit $exit_code
    fi
  }

echo "$program_name: Sourcing .bashrc settings"
run_cmd source $postgres_bashrc

echo "$program_name: Prepending postgres bin dir to path"
run_cmd export PATH=${PG_HOME}/bin:${PATH}

echo "$program_name: Running initdb"
echo "$program_name: Check: which initdb: `which initdb`"
run_cmd initdb $initdb_options

echo "$program_name: Adding entries to postgresql.conf"
run_cmd echo "

#---------------------------------------------------------------------------
# Added by japs_env/build.sh
#---------------------------------------------------------------------------

default_with_oids = on
" >> $postgres_home/data/postgresql.conf

echo "$program_name: Starting db server"
echo "$program_name: Check: which pg_ctl: `which pg_ctl`"
run_cmd pg_ctl start
echo "$program_name: Waiting 5 secongs for db server to initialize"
sleep 5
echo "$program_name: Running createlang"
echo "Check: which createlang: `which createlang`"
run_cmd createlang -d template1 plpgsql
echo "$program_name: Shutting down db server"
run_cmd pg_ctl stop
