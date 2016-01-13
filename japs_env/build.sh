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

# Authors: Tilman Rassy <rassy@math.tu-berlin.de>
#          Marek Grudzinski <grudzin@math.tu-berlin.de>
# (Based on the Perl script japs_environment_install from  Christian Ruppert
# <ruppert@math.tu-berlin.de> and Helmut Vieritz <vieritz@math.tu-berlin.de>)
# $Id: build.sh,v 1.34 2008/02/06 16:07:25 rassy Exp $

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.34 $'
readonly user_config_file=build.conf
#readonly log_file=${program_name}.log

# Start logging:
# rm -f $log_file
# exec 2>&1 | tee $log_file

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Default task:
task=process_targets

# Process command line parameters:
params=`getopt \
  --longoptions targets,src-dir:,build-dir:,inst-dir:,bin-dir:,vars,release:,cvsroot:,help,version \
  --options t,s:,p:,b:,i:,x:,h,v \
  -- \
  "$@"`
if [ $? -ne 0 ] ; then exit 1 ; fi
eval set -- "$params"
while true ; do
  case "$1" in
    --targets|-t) task=show_targets ; shift ;;
    --release) release="$2" ; shift 2 ;;
    --cvsroot) cvsroot="$2" ; shift 2 ;;
    --src-dir|-s) src_dir="$2" ; shift 2 ;;
    --build-dir|-b) build_dir="$2" ; shift 2 ;;
    --inst-dir|-i) inst_dir="$2" ; shift 2 ;;
    --bin-dir|-x) bin_dir="$2" ; shift 2 ;;
    --vars) task=print_variables ; shift ;;
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Current dir:
base_dir=`pwd`

# User:
user=`whoami`

# Initialize customizable variables:
if [ "$user" == root ] ; then
  build_dir=${build_dir:-/usr/local/src}
  inst_dir=${inst_dir:-/srv}
  bin_dir=${bin_dir:-/usr/local/bin}
  apache_port=${apache_port:-80}
else
  build_dir=${build_dir:-$HOME/src}
  inst_dir=${inst_dir:-$HOME/srv}
  bin_dir=${bin_dir:-$HOME/bin}
  apache_port=${apache_port:-8080}
fi
tomcat_heap_space=${tomcat_heap_space:-256}
mumie_user=${mumie_user:-mumie}
mumie_group=${mumie_group:-mumie}

# Setup variables:
src_dir=$base_dir/src
# Tar archives (absolute paths):
apache_tgz=$src_dir/apache/httpd-2.2.4.tar.gz
mod_jk_tgz=$src_dir/mod_jk/tomcat-connectors-1.2.20-src.tar.gz
postgres_tgz=$src_dir/postgresql/postgresql-8.2.3.tar.gz
postgres_jar=$src_dir/postgresql/postgresql-8.2-504.jdbc4.jar
tomcat_tgz=$src_dir/tomcat/apache-tomcat-5.5.23.tar.gz
cocoon_tgz=$src_dir/cocoon/cocoon-2.1.8-src.tar.gz
# Build directories (relative to $build_dir):
apache_build_dir=httpd-2.2.4
mod_jk_build_dir=mod_jk-1.2.20
postgres_build_dir=postgresql-8.2.3
cocoon_build_dir=cocoon-2.1.8
# Shorter name for the mod_jk directory:
mod_jk_old_dir=tomcat-connectors-1.2.20-src
mod_jk_renamed_dir=mod_jk-1.2.20
# Shorter name for the tomcat directory:
tomcat_old_dir=apache-tomcat-5.5.23
tomcat_renamed_dir=tomcat-5.5.23
# Install directories (absolute paths):
apache_home=$inst_dir/apache-2.2.4
mod_jk_home=$apache_home/modules
postgres_home=$inst_dir/pgsql-8.2.3
tomcat_home=$inst_dir/$tomcat_renamed_dir
# mod_jk2:
mod_jk=mod_jk.so
mod_jk_store=$build_dir/$mod_jk_build_dir/native/apache-2.0/$mod_jk
# Bash and C shell config files:
apache_bashrc=conf/bash/apache_bashrc
apache_cshrc=conf/csh/apache_cshrc
postgres_bashrc=conf/bash/postgres_bashrc
postgres_cshrc=conf/csh/postgres_cshrc
tomcat_bashrc=conf/bash/tomcat_bashrc
tomcat_cshrc=conf/csh/tomcat_cshrc
# Others:
version_file=VERSION

# Runs a command, checks the exit code, terminates with an error message if exit
# code is not 0
function run_cmd
  {
    "$@"
    local exit_code=$?
    if [ "$exit_code" -ne 0 ] ; then
      echo "ERROR: Last command returned with code $exit_code"
      exit 1
    fi
  }

# Creates a backup of an original file. If the backup already exists, does nothing.
# The backup is named FOO.orig, where FOO is the original filename.
function backup_orig
  {
   local file=$1
   local backup_file=${file}.orig
   echo "$program_name: Making backup of $file"
    if [ -e $backup_file ] ; then
      echo "$program_name: Backup already exists"
    else
      run_cmd cp -v $file $backup_file
    fi
  }

# Creates $build_dir if necessary
function ensure_build_dir
  {
    run_cmd mkdir -pv $build_dir
  }

# Creates $inst_dir if necessary
function ensure_inst_dir
  {
    run_cmd mkdir -pv $inst_dir
  }

# Creates $bin_dir if necessary
function ensure_bin_dir
  {
    run_cmd mkdir -pv $bin_dir
  }

# Checks the exit code of the last command, terminates with an error message if the
# exit code is not 0
function check_exit_code
  {
    local exit_code=$?
    [ "$exit_code" -eq 0 ] || error "Last command returned with code $exit_code"
  }

# Prints all variables to stdout
function print_variables
  {
    cat <<EOF
apache_build_dir   = $apache_build_dir
apache_home        = $apache_home
apache_port        = $apache_port
apache_tgz         = $apache_tgz
bin_dir            = $bin_dir
build_dir          = $build_dir
cocoon_build_dir   = $cocoon_build_dir
cocoon_tgz         = $cocoon_tgz
cvsroot            = $cvsroot
inst_dir           = $inst_dir
mod_jk             = $mod_jk
mod_jk_store       = $mod_jk_store
mumie_user         = $mumie_user
mumie_group        = $mumie_group
postgres_build_dir = $postgres_build_dir
postgres_home      = $postgres_home
postgres_tgz       = $postgres_tgz
release            = $release
src_dir            = $src_dir
targets            = $targets
task               = $task
tomcat_heap_space  = $tomcat_heap_space
tomcat_home        = $tomcat_home
tomcat_old_dir     = $tomcat_old_dir
tomcat_renamed_dir = $tomcat_renamed_dir
tomcat_tgz         = $tomcat_tgz
EOF
  }

function install_apache
  {
    echo
    echo "======================================================================"
    echo "Installing Apache"
    echo "======================================================================"
    echo
    ensure_build_dir
    run_cmd cd $build_dir
    echo "$program_name: Extracting tar archive"
    run_cmd tar xzf $apache_tgz
    run_cmd cd $apache_build_dir
    echo "$program_name: Running configure"
    run_cmd ./configure \
      --prefix=$apache_home \
      --enable-so \
      --enable-ssl \
      --enable-rewrite \
      --with-port=$apache_port
    echo "$program_name: Running make"
    run_cmd make
    ensure_inst_dir
    echo "$program_name: Running make install"
    run_cmd make install
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function install_mod_jk
  {
    echo
    echo "======================================================================"
    echo "Installing Tomcat Connector mod_jk"
    echo "======================================================================"
    echo
    ensure_build_dir
    run_cmd cd $build_dir
    echo "$program_name: Extracting tar archive"
    run_cmd tar xzf $mod_jk_tgz
    run_cmd mv $mod_jk_old_dir $mod_jk_renamed_dir
    run_cmd cd $mod_jk_build_dir/native
    echo "$program_name: Running configure"
    run_cmd ./configure --with-apxs=$apache_home/bin/apxs --with-java-home=${JAVA_HOME}
    echo "$program_name: Running make"
    run_cmd make
    echo "$program_name: Done"
  }

function install_postgres
  {
    echo
    echo "======================================================================"
    echo "Installing Postgres"
    echo "======================================================================"
    echo
    ensure_build_dir
    run_cmd cd $build_dir
    echo "$program_name: Extracting tar archive"
    run_cmd tar xzf $postgres_tgz
    run_cmd cd $postgres_build_dir
    echo "$program_name: Running configure"
    run_cmd ./configure --prefix=$postgres_home
    echo "$program_name: Running make"
    run_cmd make
    ensure_inst_dir
    echo "$program_name: Running make install"
    run_cmd make install
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function setup_postgres
  {
    echo
    echo "======================================================================"
    echo "Setting up Postgres"
    echo "======================================================================"
    echo

    echo "$program_name: Creating .bashrc settings"
    run_cmd rm -fv $postgres_bashrc
    echo "
# PostgreSQL:
export PG_HOME=$postgres_home
export LD_LIBRARY_PATH=\${PG_HOME}/lib:\${LD_LIBRARY_PATH}
export MANPATH=\${PG_HOME}/man:\${MANPATH}
export PGDATA=\${PG_HOME}/data
" > $postgres_bashrc

    echo "$program_name: Creating .cshrc settings"
    run_cmd rm -fv $postgres_cshrc
    echo "
# PostgreSQL:
setenv PG_HOME $postgres_home
if ( \$?LD_LIBRARY_PATH ) then
  setenv LD_LIBRARY_PATH \${PG_HOME}/lib:\${LD_LIBRARY_PATH}
else
  setenv LD_LIBRARY_PATH \${PG_HOME}/lib
endif
setenv MANPATH \${PG_HOME}/man:\${MANPATH}
setenv PGDATA \${PG_HOME}/data
" > $postgres_cshrc

    if [ "$user" == root ] ; then
      echo "$program_name: Setting user and group for $postgres_home"
      run_cmd chown -R $mumie_user $postgres_home
      run_cmd chgrp -R $mumie_group $postgres_home
      echo "$program_name: Setting user and group for $postgres_bashrc"
      run_cmd chown $mumie_user $postgres_bashrc
      run_cmd chgrp $mumie_group $postgres_bashrc
    fi

    local params="$postgres_bashrc $postgres_home $postgres_jar $tomcat_home"
    local cmd="/bin/bash $base_dir/tools/setup_postgres.sh"
    if [ "$user" == root ] ; then
      echo "$program_name: Switching user to $mumie_user"
      run_cmd su $mumie_user -c "$cmd $params \"$initdb_options\""
      echo "$program_name: User is $user again"
    else
      run_cmd $cmd $params "$initdb_options"
    fi
    
    echo "$program_name: Creating entries in bin dir"
    ensure_bin_dir
    run_cmd cd $bin_dir
    run_cmd rm -fv pg_ctl psql postgres
    run_cmd ln -s $postgres_home/bin/pg_ctl
    run_cmd ln -s $postgres_home/bin/psql
    run_cmd echo "#!/bin/bash

if [ \"\`whoami\`\" = root ] ; then
  cd \~$mumie_user
  su -c \"postgres \$@\" $mumie_user
  exit \$?
fi

pg_ctl=$postgres_home/bin/pg_ctl
log_file=$postgres_home/data/pg.log
cmd=\$1
shift
if [ \"\$cmd\" == start ] ; then
  \$pg_ctl start -w -o -i -l \$log_file \"\$@\"
else
  \$pg_ctl \$cmd \"\$@\"
fi
" > postgres
    run_cmd chmod u+x postgres

    echo "$program_name: Done"
  }

function install_tomcat
  {
    echo
    echo "======================================================================"
    echo "Installing Tomcat"
    echo "======================================================================"
    echo
    ensure_inst_dir
    run_cmd cd $inst_dir
    echo "$program_name: Extracting tar archive"
    run_cmd tar xzf $tomcat_tgz
    echo "$program_name: Renaming directory"
    run_cmd mv -v $tomcat_old_dir $tomcat_renamed_dir
    run_cmd cd $base_dir
    run_cmd rm -fv $tomcat_bashrc
    echo "$program_name: Creating $tomcat_bashrc"
    echo "
# Tomcat:
export CATALINA_HOME=$tomcat_home
export TOMCAT_HOME=\$CATALINA_HOME
" > $tomcat_bashrc
    run_cmd rm -fv $tomcat_cshrc
    echo "$program_name: Creating $tomcat_cshrc"
    echo "
# Tomcat:
setenv CATALINA_HOME $tomcat_home
setenv TOMCAT_HOME \$CATALINA_HOME
" > $tomcat_cshrc
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function build_cocoon
  {
    echo
    echo "======================================================================"
    echo "Building Cocoon"
    echo "======================================================================"
    echo
    ensure_build_dir
    run_cmd cd $build_dir
    echo "$program_name: Extracting tar archive"
    run_cmd tar xzf $cocoon_tgz
    run_cmd cd $cocoon_build_dir
    echo "$program_name: Copying local.blocks.properties"
    run_cmd cp -v $base_dir/conf/cocoon/local.blocks.properties .
    echo "$program_name: Running build.sh"
    run_cmd ./build.sh war
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function install_cocoon
  {
    echo
    echo "======================================================================"
    echo "Installing Cocoon"
    echo "======================================================================"
    echo
    echo "$program_name: Extracting cocoon.war"
    run_cmd mkdir -v $tomcat_home/webapps/cocoon
    run_cmd cd $tomcat_home/webapps/cocoon
    run_cmd jar xf $build_dir/$cocoon_build_dir/build/cocoon/cocoon.war
    echo "$program_name: Copying Xalan libs to endorsed dir"
    local lib
    for lib in xalan-2.7.0.jar xercesImpl-2.7.1.jar xml-apis-1.3.02.jar ; do
      run_cmd cp -v WEB-INF/lib/$lib $tomcat_home/common/endorsed
    done
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function setup_apache
  {
    echo
    echo "======================================================================"
    echo "Setting up Apache"
    echo "======================================================================"
    echo

    echo "$program_name: Copying $mod_jk"
    run_cmd cp -v $mod_jk_store $mod_jk_home

    run_cmd cd $apache_home/conf

    backup_orig httpd.conf

    echo "$program_name: Adding $mod_jk to httpd.conf"
    local line="LoadModule jk_module modules/$mod_jk"
    if [ "`grep "^$line" httpd.conf`" ] ; then
      echo "$program_name: Is already added"
    else
      run_cmd echo "`cat httpd.conf`

# Load the mod_jk module:
$line
# Where to put jk shared memory
JkShmFile $apache_home/conf/mod_jk.shm

# Where to put jk logs
JkLogFile $apache_home/logs/mod_jk.log

# Configure a worker
JkWorkerProperty worker.list=worker1
JkWorkerProperty worker.worker1.type=ajp13
JkWorkerProperty worker.worker1.host=localhost
JkWorkerProperty worker.worker1.port=8009

# Send everything for context /cocoon to worker named worker1 (ajp13)
JkMount  /cocoon/* worker1
" > httpd.conf
    fi

    run_cmd cd $base_dir

    run_cmd rm -fv $apache_bashrc
    echo "$program_name: Creating $apache_bashrc"
    echo "
# Apache:
export APACHE_HOME=$apache_home
export MANPATH=\${APACHE_HOME}/man:\${MANPATH}
" > $apache_bashrc

    run_cmd rm -fv $apache_cshrc
    echo "$program_name: Creating $apache_cshrc"
    echo "
# Apache:
setenv APACHE_HOME $apache_home
setenv MANPATH \${APACHE_HOME}/man:\${MANPATH}
" > $apache_cshrc

    echo "$program_name: Creating executables in bin dir"
    ensure_bin_dir
    run_cmd cd $bin_dir
    run_cmd rm -fv apachectl apache
    run_cmd ln -s $apache_home/bin/apachectl
    cat $base_dir/bin/apache.tpl \
      | sed "s:@apache-home@:$apache_home:g" \
      | sed "s:@apache-port@:$apache_port:g" \
      > apache
    check_exit_code
    run_cmd chmod u+x apache

#     if [ "$user" == root ] ; then
#       echo "$program_name: Setting user and group for $apache_home"
#       run_cmd chown -R $mumie_user $apache_home
#       run_cmd chgrp -R $mumie_group $apache_home
#     fi

    run_cmd cd $base_dir

    echo "$program_name: Done"
  }

function setup_tomcat
  {
    echo
    echo "======================================================================"
    echo "Setting up Tomcat"
    echo "======================================================================"
    echo
    run_cmd cd $tomcat_home/conf
    backup_orig server.xml
    echo "$program_name: Copying new server.xml"
    run_cmd cp $base_dir/conf/tomcat/server.xml .
    echo "$program_name: Creating executables in bin dir"
    ensure_bin_dir
    run_cmd cd $bin_dir
    run_cmd rm -fv catalina.sh tomcat
    cat $base_dir/bin/tomcat.tpl \
      | sed "s:@tomcat-home@:$tomcat_home:g" \
      | sed "s:@tomcat-heap-space@:$tomcat_heap_space:g" \
      | sed "s:@mumie-user@:$mumie_user:g" \
      > tomcat
    check_exit_code
    run_cmd chmod u+x tomcat
    if [ "$user" == root ] ; then
      echo "$program_name: Setting user and group for $tomcat_home"
      run_cmd chown -R $mumie_user $tomcat_home
      run_cmd chgrp -R $mumie_group $tomcat_home
    fi
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function setup_postgres_jar
  {
    echo
    echo "======================================================================"
    echo "Setting up Postgres Jar"
    echo "======================================================================"
    echo
    echo "$program_name: Copying postgres jar to cocoon webapp dir"
    local target=$tomcat_home/webapps/cocoon/WEB-INF/lib
    run_cmd cp -v $postgres_jar $target
    if [ "$user" == root ] ; then
      echo "$program_name: Setting user and group for $target"
      run_cmd chown $mumie_user $target
      run_cmd chgrp $mumie_group $target
    fi
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function clear_build_dirs
  {
    echo
    echo "======================================================================"
    echo "Deleting build directories"
    echo "======================================================================"
    echo
    local dir
    cd $build_dir
    for dir in $apache_build_dir $mod_jk_build_dir $postgres_build_dir $cocoon_build_dir ; do
      run_cmd rm -rfv $dir
    done
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

function clear_install_dirs
  {
    echo
    echo "======================================================================"
    echo "Deleting install directories"
    echo "======================================================================"
    echo
    run_cmd rm -rfv $apache_home
    run_cmd rm -rfv $tomcat_home
    run_cmd rm -rfv $postgres_home
    run_cmd cd $base_dir
    echo "$program_name: Done"
  }

# Creates the distribution
function create_dist
  {
    echo
    echo "======================================================================"
    echo "Creating distribution"
    echo "======================================================================"
    echo
    [ "$release" ] || error "No release specified"
    run_cmd cd $base_dir
    run_cmd mkdir -pv dist
    run_cmd cd dist
    echo "$program_name: Changed into dist/"
    echo "$program_name: Checking-out release"
    local dist_name="japs_env_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag japs_env
    run_cmd mv -v japs_env $dist_name
    echo "$program_name: Creating version file"
    run_cmd echo $release > $dist_name/$version_file
    echo "$program_name: Creating tgz"
    run_cmd tar czf $archive $dist_name
    run_cmd cd $base_dir
  }

# Processes the targets
function process_targets
  {
    for target in $targets ; do
      case $target in 
        apache)
          install_apache ;;
        mod_jk)
          install_mod_jk ;;
        tomcat)
          install_tomcat ;;
        postgres)
          install_postgres ;;
        cocoon)
          build_cocoon
          install_cocoon ;;
        cocoon-build)
          build_cocoon ;;
        cocoon-install)
          install_cocoon ;;
        apache-setup)
          setup_apache ;;
        tomcat-setup)
          setup_tomcat ;;
        postgres-setup)
          setup_postgres ;;
        postgres-jar)
          setup_postgres_jar ;;
        all)
          install_apache
          install_mod_jk
          install_tomcat
          install_postgres
          build_cocoon
          install_cocoon
          setup_apache
          setup_tomcat
          setup_postgres
          setup_postgres_jar ;;
        vars)
          print_variables ;;
        clear-build)
          clear_build_dirs ;;
        clear-install)
          clear_install_dirs ;;
        dist)
          create_dist ;;
        *)
          echo "ERROR: Unknown target: $target"
          exit 3 ;;
      esac
    done
  }

function show_targets
  {
cat <<EOF
apache           Builds and installs Apache
mod_jk           Builds and installs mod_jk
tomcat           Installs Tomcat
postgres         Builds and installs Postgres
cocoon           Build and install Cocoon. Same as the sequence of targets:
                 cocoon-build cocoon-install.
cocoon-build     Builds Cocoon
cocoon-install   Installs Cocoon (as a webapp in Tomcat).
apache-setup     Sets-up Apache
tomcat-setup     Sets-up Tomcat
postgres-setup   Sets-up Postgres
postgres-jar     Copies the postgres jar to the Cocoon webapp directory
all              Same as the sequence of targets: apache mod_jk tomcat postgres
                 cocoon-build cocoon-install apache-setup tomcat-setup
                 postgres-setup postgres-jar
clear-build      Removes the build directories
clear-install    Removes the install directories
EOF
  }

function show_help
  {
     cat <<EOF
Usage:
  $program_name [OPTIONS] [TARGETS]
Description:
  Installs Apache, Tomcat, Postgres, and Cocoon und sets them up for Japs.
  It is also possible to install or setup only parts of the software listed
  above. What is actually done is controlled by TARGETS, which is a list of
  keywords called "targets". Type ./build.sh -t to get a list of all targets.
  The default target is "all"; it is assumed if no targets are specified.
Targets:
Options:
  --targets, -t
      List all targets
  --src-dir=DIR | -s DIR
      Set the source root directory.
  --build-dir=DIR | -b DIR
      Set the build directory
  --inst-dir=DIR | -i DIR
      Set the installation directory
  --bin-dir=DIR | -x DIR
      Set the bin directory
  --vars
      Prints the build variables to stdout
  --help | -h
      Print this help text and exit
  --version | -v
      Print version information and exit
EOF
  }

function show_version
  {
    echo $program_version
  }

$task


