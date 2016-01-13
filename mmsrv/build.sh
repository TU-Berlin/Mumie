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

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: build.sh,v 1.20 2009/12/01 00:48:06 rassy Exp $

# Build script for mmsrv

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.20 $'
readonly user_config_file=build.conf

# Set variable defaults:
prefix=${MM_BUILD_PREFIX:-/usr/local}
task=process_targets
tomcat_home=$TOMCAT_HOME
webapp_name=cocoon
db_host=localhost
db_port=5432
db_name=mumie
db_encoding=DEFAULT
db_user_name=mumie
db_user_password=mumie
db_admin_name=$USER
db_admin_password=foo
admin_name=admin
admin_password=mumie
password_encryptor_class=net.mumie.srv.encrypt.MD5PasswordEncryptor
mail_domain=localhost
mail_smtp_host=localhost
mail_from_name=Admin
mail_from_address=''
mail_reply_name=''
mail_reply_address=''
qf_applet_mail_to_name='QF Applet'
qf_applet_mail_to_address=''
default_lang_name=Deutsch
default_lang_code=de
receipt_dir=''
sign_keystore_file=''
sign_keystore_type=JKS
sign_keystore_password=japsen
sign_key_alias=mumie
sign_key_dn=''
sign_key_validity=''
upload_dir=''
correction_tmp_dir=''
msg_dest_table_filename=''
checkin_root=${MM_CHECKIN_ROOT:-$HOME/mumie/checkin}
jdk_apidocs_url=http://java.sun.com/j2se/1.4.2/docs/api
datasheet_apidocs_url=''
cocoon_apidocs_url=http://xml.apache.org/cocoon/apidocs
avalon_framework_apidocs_url=''
excalibur_xmlutil_apidocs_url=''

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions force,targets,ignore-deps,help,version,vars,check,release:,cvsroot:,javac-verbose,javac-deprecation,javac-debug \
  --options f,t,D,h,v \
  -- \
  "$@"` || exit 1
eval set -- "$params"
while true ; do
  case "$1" in
    --targets|-t) task=show_targets ; shift ;;
    --ignore-deps|-D) ignore_deps=ignore_deps ; shift ;;
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --vars) task=print_variables ; shift ;;
    --check) task=check ; shift ;;
    --force|-f) force=force ; shift ;;
    --release) release="$2" ; shift 2 ;;
    --cvsroot) cvsroot="$2" ; shift 2 ;;
    --javac-verbose) javac_verbose=enabled ; shift ;;
    --javac-deprecation) javac_deprecation=enabled ; shift ;;
    --javac-debug) javac_debug=enabled ; shift ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'mmsrv-lib'}

# Store the current directory:
base_dir=`pwd`

# Store the pid of this process
pid=$$

# Counter for warnings:
declare -i numwarn=0

# Set the variables if not set already:
java_lib_dir=$prefix/lib/java
doc_dir=$prefix/share/doc/japs
webapp_root=$tomcat_home/webapps/$webapp_name
webapp_webinf=$webapp_root/WEB-INF
webapp_lib_dir=$webapp_webinf/lib
java_cmd="java $java_opts"
version_file=VERSION
build_tools_jar=$base_dir/lib/java/mumie-japs-build.jar
avalon_framework_api_jar=avalon-framework-api-4.3.jar
avalon_framework_impl_jar=avalon-framework-impl-4.3.jar
mail_from_address=${mail_from_address:-admin@${mail_domain}}
mail_reply_name=${mail_reply_name:-$mail_from_name}
mail_reply_address=${mail_reply_address:-$mail_from_address}
qf_applet_mail_to_address=${qf_applet_mail_to_address:-qf-applet@$mail_domain}
receipt_dir=${receipt_dir:-$webapp_root/receipts}
sign_keystore_file=${sign_keystore_file:-$tomcat_home/conf/keystore}
sign_key_password=${sign_key_password:-$sign_keystore_password}
sign_key_validity=${sign_key_validity:-365}
upload_dir=${upload_dir:-$webapp_webinf/upload}
correction_tmp_dir=${correction_tmp_dir:-$webapp_webinf/correction}
msg_dest_table_filename=${msg_dest_table_filename:-$webapp_webinf/message_destination_table.xml}
checkin_root=${MM_CHECKIN_ROOT:-$HOME/mumie/checkin}
datasheet_apidocs_url=${datasheet_apidocs_url:-$prefix/doc/japs_client/apidocs}

# Mmsrv Java library:
mmsrv_jar="mumie-srv.jar"

# Notion class source files (except LangCode.java, which is treated specially):
notion_class_sources="
  Category.java
  ContentFormat.java
  DbColumn.java
  DbFunction.java
  DbTable.java
  EntityType.java
  EventName.java
  FileRole.java
  MediaType.java
  ProblemDataType.java
  RefType.java
  RequestParam.java
  ResponseHeader.java
  SessionAttrib.java
  SyncCmdName.java
  TimeFormat.java
  UseMode.java
  UserGroupName.java
  WorksheetState.java
  XMLAttribute.java
  XMLElement.java
  XMLNamespace.java
  XMLNamespacePrefix.java
"

# Config files to install/uninstall:
config_install_files="
  cocoon.xconf
  mumie.roles
  web.xml
  logkit.xconf
  checkin_defaults.xml"

# Original Cocoon config files to save:
orig_config_files="
  cocoon.xconf
  web.xml
  logkit.xconf"

# fs-content files to install/uninstall:
fsc_install_files="
  login_form.xml
  login_form.xsl
  checkin_response.xsl
  not_logged_in.xhtml
  logout_failed.xhtml
  transform.xsl
  create_account_form.xml
  resources/i18n_de.xml
  resources/styles.css
  resources/mumie_logo.png
  resources/logo.png
  resources/top_bar_bg.png
"

# Libraries to install/uninstall. This variable contains libraries which are
# included in this packege or are created by the build.
lib_install_files_1="
  $mmsrv_jar
  mail-1.3.3_01.jar
  activation-1.0.2.jar
  mumie-mathlet-factory.jar
"

# Libraries to install/uninstall. This variable contains libraries which are
# in $prefix/lib/java.
lib_install_files_2="
  mumie-sql.jar
  mumie-japs-datasheet.jar
  mumie-japs-client.jar
"

# --------------------------------------------------------------------------------
# Utility functions
# --------------------------------------------------------------------------------

# Prints the time of last modification of a file to stdout. The time is expressed
# as seconds since Epoch
function mtime
  {
    run_cmd stat -c %Y $1
  }

# Returns all Java classes which are below a certain package path and must be rebuild
function get_java_source_files
  {
    local package_path=$1
    local src_dir=$base_dir/src/java
    local target_dir=$base_dir/lib/java/classes
    local saved_dir=`pwd`
    run_cmd cd $src_dir/$package_path
    local source_file
    for source_file in `find -name "*.java"` ; do
      local target_file=$target_dir/$package_path/${source_file%java}class
      if [ "$force" ] || \
         [ ! -e "$target_file" ] || \
         [ `mtime $source_file` -gt `mtime $target_file` ]
      then
        echo $source_file
      fi
    done
    run_cmd cd $saved_dir
  }

# Aborts with an error message
function error
  {
    echo "$program_name: ERROR: $*"
    echo
    kill -9 $pid
    # exit 1
  } 

# Prints a warning
function warning
  {
    echo "$program_name: WARNING: $*"
    let numwarn++
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

# Creates a Java class name from a string. Usage:
#
#   to_class_name STR
#
# STR should be a string which is made of lower case characters, digits and underscores.
# STR is first split into parts, where the underscores are treated as part separators. The
# underscores itselfs are not contained in the parts. The first characters of the parts are
# capitalized, and the parts are concatenated again. the result is printed to stdout.
# Example:
#
#   Input: foo_bar_bazz   Output: FooBarBazz

function to_class_name
  {
    local name=$1
    local parts=`echo "$name" | tr '_' ' '`
    [ "$parts" ] || return
    local class_name=''
    local part
    for part in $parts ; do
      local first_char=`echo $part | awk '{print substr($1, 1, 1)}'`
      local rest=`echo $part | awk '{print substr($1, 2)}'`
      first_char=`echo $first_char | tr abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ`
      class_name="${class_name}${first_char}${rest}"
    done
    echo $class_name
  }

# Gets all document types. The types are extracted from config/declarations.xml by means
# of an XSL stylesheet and printed to stdout.

function get_doctypes
  {
     run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
       --input=$base_dir/config/declarations.xml \
       --stylesheet=$base_dir/tools/xsl/doctypes.xsl
  }

# Copies files to a directory, preserves subdirectory struture
# Usage: cp_to_dir DIR FILE1 FILE2 ...
# Copies FILE1 FILE2 ... to DIR, with their relative paths. Subdirectories are created
# if necessary

function cp_to_dir
  {
    local dir=$1
    shift
    local file
    for file in "$@" ; do
      local path=`dirname $file | sed s/^\\.\\\\///`
      if [ "$path" != '.' ] ; then
        run_cmd mkdir -vp $dir/$path
        run_cmd cp -v $file $dir/$path
      else
        run_cmd mkdir -vp $dir
        run_cmd cp -v $file $dir
      fi
    done
  }

# Calls ant:
function run_ant
  {
    local ant_cmd="\
      ant -e \
          -Dmmsrv.install.prefix=$prefix \
          -Dmmsrv.install.lib.java.dir=$java_lib_dir \
          -Dmmsrv.apidocs.jdk.url=$jdk_apidocs_url \
          -Dmmsrv.apidocs.japs-datasheet.url=$datasheet_apidocs_url \
          -Dmmsrv.apidocs.cocoon.url=$cocoon_apidocs_url \
          -Dmmsrv.apidocs.avalon-framework.url=$avalon_framework_apidocs_url \
          -Dmmsrv.apidocs.excalibur-xmlutil.url=$excalibur_xmlutil_apidocs_url"
    [ "$force" ] && ant_cmd="$ant_cmd -Dmmsrv.force=yes"
    [ "$javac_verbose" ] && ant_cmd="$ant_cmd -Dmmsrv.javac.verbose=yes"
    [ "$javac_deprecation" ] && ant_cmd="$ant_cmd -Dmmsrv.javac.deprecation=yes"
    [ "$javac_debug" ] && ant_cmd="$ant_cmd -Dmmsrv.javac.debug=yes"
    [ -e "$base_dir/$version_file" ] &&
      ant_cmd="$ant_cmd -Dmmsrv.version=`cat $base_dir/$version_file`"
    run_cmd $ant_cmd "$@"
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

# Runs psql as db admin
function psql_admin
  {
    export PGPASSWD=$db_admin_password
    export PGPASSWORD=$db_admin_password
    run_cmd psql \
      --host $db_host \
      --port $db_port \
      --username $db_admin_name \
      --dbname template1 \
      "$@"
  }

# Runs psql as db user
function psql_user
  {
    export PGPASSWD=$db_user_password
    export PGPASSWORD=$db_user_password
    run_cmd psql \
      --host $db_host \
      --port $db_port \
      --username $db_user_name \
      --dbname $db_name \
      "$@"
  }

# Tries to find the db user $db_user_name in the database. Returns the user name if yes,
# the empty string if no.
function find_db_user
  {
    psql_admin \
      --tuples-only \
      --command "SELECT usename FROM pg_user WHERE usename='$db_user_name';"
  }

# Tries to find the database $db_user_name in the DNMS. Returns the database name
# if yes, the empty string if no.
function find_db
  {
    psql_admin \
      --tuples-only \
      --command "SELECT datname FROM pg_database WHERE datname='$db_name';"
  }

# Makes a copy of the (original) sitemap.
function backup_sitemap
  {
    run_cmd mkdir -pv $base_dir/uninstall
    local uninstall_dir=$base_dir/uninstall
    # If necessary, save original:
    [ -e "$webapp_root/sitemap.xmap" ] && [ ! -e "$uninstall_dir/sitemap.xmap" ] && \
      run_cmd cp -v "$webapp_root/sitemap.xmap" $uninstall_dir
  }

# Quotes the character '/' with a backslash. Used in sed input.
function quote
  {
    echo "$@" | sed 's/\//\\\//g'
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Creates the build tools. I.e., compiles the corresponding Java classes and jars
# them. Calls an Ant process.

function create_build_tools
  {
    echo
    echo "======================================================================"
    echo "Creating build tools"
    echo "======================================================================"
    echo
    if [ "`get_java_source_files net/mumie/srv/build`" ] ; then
      run_ant -buildfile tools/ant/build_tools.xml
    fi
    echo "$program_name: build tools done"
    create_build_tools_done=done
  }

# Creates the offline tools. I.e., compiles the corresponding Java classes and jars
# them. Calls an Ant process.

function create_offline_tools
  {
    echo
    echo "======================================================================"
    echo "Creating offline tools"
    echo "======================================================================"
    echo
    if [ "`get_java_source_files net/mumie/srv/offline`" ] ; then
      run_ant -buildfile tools/ant/offline_tools.xml
    fi
    echo "$program_name: offline tools done"
    create_offline_tools_done=done
  }

# Creates the notion class sources.

function create_notion_class_sources
  {
    [ "$create_build_tools_done" ] || [ "$ignore_deps" ] || create_build_tools
    echo
    echo "======================================================================"
    echo "Creating notion class sources"
    echo "======================================================================"
    echo

    local result_dir=$base_dir/src/java/net/mumie/srv/notions

    # Create Java sources from skel files:
    local xsl_dir=$base_dir/tools/xsl
    local decl_file=$base_dir/config/declarations.xml
    local mtime_decl=`mtime $decl_file`
    local name
    set_classpath \
      $java_lib_dir/mumie-util.jar \
      $base_dir/lib/java/mumie-srv-build.jar
    for name in $notion_class_sources ; do
      local skel_file=$result_dir/${name}.skel
      local result_file=$result_dir/$name
      local xsl_file=$xsl_dir/${name%java}xsl
      if [ "$force" ] || \
         [ ! -e "$result_file" ] || \
         [ `mtime $skel_file` -gt `mtime $result_file` ] || \
         [ `mtime $xsl_file` -gt `mtime $result_file` ] || \
         [  $mtime_decl -gt `mtime $result_file` ]
      then
        run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
          --input=$decl_file \
          --output=$result_file \
          --stylesheet=$xsl_file \
          --param skeleton-filename=$skel_file
      fi
    done

    # Create LangCode.java from tpl file:
    local target_file=$result_dir/LangCode.java
    local tpl_file=$result_dir/LangCode.java.tpl
    if [ "$force" ] ||
       [ ! -e "$target_file" ] || \
       [ `mtime $tpl_file` -gt `mtime $target_file` ]
    then
      echo "Creating LangCode.java from tpl file"
      run_cmd sed "s:@default-lang-code@:`quote $default_lang_code`:g" $tpl_file > $target_file
    fi

    create_notion_class_sources_done=done  
    echo "$program_name: notion class sources done"
  }

# Creates the db helper sources. This is done by three XSL transformations
# producing the three sources DbHelper.java, AbstractDbHelper.java, and
# PostgreSQLDbHelper.java. The function calls a Java class from the build
# tools performing all three XSL transformations.

function create_db_helper_sources
  {
    [ "$create_build_tools_done" ] || [ "$ignore_deps" ] || create_build_tools
    echo
    echo "======================================================================"
    echo "Creating db helper sources"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir

    # Check if targets must be (re)build:
    local needs_build=''
    if [ "$force" ] ; then
      # Force flag enabled, thus (re)build required:
      needs_build=enabled
    else
      # Check if target files exist:
      local dbh_src_dir=java/src/net/mumie/srv/db
      local dbh_java=$dbh_src_dir/DbHelper.java
      local abs_dbh_java=$dbh_src_dir/AbstractDbHelper.java
      local pg_dbh_java=$dbh_src_dir/PostgreSQLDbHelper.java
      if [ ! -e "$dbh_java" ] || [ ! -e "$abs_dbh_java" ] || [ ! -e "$pg_dbh_java" ] ; then
        # Not all target files exist, thus (re)build required:
        needs_build=enabled
      else
        # Target files exist, check modification times:
        local mtime_dbh_java=`mtime $dbh_java`
        local mtime_abs_dbh_java=`mtime $abs_dbh_java`
        local mtime_pg_dbh_java=`mtime $pg_dbh_java`
        local mtime_dbh_xsl=`mtime tools/xsl/DbHelper.xsl`
        local mtime_dbh_xml=`mtime $dbh_src_dir/DbHelper.xml`
        local mtime_dbh_skel=`mtime $dbh_src_dir/DbHelper.java.skel`
        local mtime_abs_dbh_skel=`mtime $dbh_src_dir/AbstractDbHelper.java.skel`
        local mtime_pg_dbh_skel=`mtime $dbh_src_dir/PostgreSQLDbHelper.java.skel`
        local mtime_build_tools=`mtime $build_tools_jar`
        if [ $mtime_dbh_java -le $mtime_dbh_xml ] || \
           [ $mtime_dbh_java -le $mtime_dbh_skel ] || \
           [ $mtime_dbh_java -le $mtime_dbh_xsl ] || \
           [ $mtime_abs_dbh_java -le $mtime_dbh_xml ] || \
           [ $mtime_abs_dbh_java -le $mtime_abs_dbh_skel ] || \
           [ $mtime_abs_dbh_java -le $mtime_dbh_xsl ] || \
           [ $mtime_pg_dbh_java -le $mtime_dbh_xml ] || \
           [ $mtime_pg_dbh_java -le $mtime_pg_dbh_skel ] || \
           [ $mtime_pg_dbh_java -le $mtime_dbh_xsl ] ; then
          # At least one target file is outdated, thus (re)build required:
          needs_build=enabled
        fi
      fi
    fi

    # (Re)build targets if necessary:
    if [ "$needs_build" ] ; then
      echo "$program_name: calling DbHelperSourcesCreator"
      set_classpath \
        $java_lib_dir/mumie-util.jar \
        $java_lib_dir/mumie-sql.jar \
        $base_dir/lib/java/mumie-srv-build.jar
      run_cmd $java_cmd net.mumie.srv.build.DbHelperSourcesCreator \
        --xsl-dir=$base_dir/tools/xsl \
        --db-helper-dir=$base_dir/src/java/net/mumie/srv/db
    fi

    echo "$program_name: db helper sources done"
    create_db_helper_sources_done=done
    run_cmd cd $base_dir
  }

# Creates the document class sources. This is done by applying the stylesheet
# Document.xsl to the file declarations.xml for each source file to be created.
# The respective document type and class name is passed to the stylesheet in each
# transformation.

function create_document_class_sources
  {
    [ "$create_build_tools_done" ] || [ "$ignore_deps" ] || create_build_tools
    echo
    echo "======================================================================"
    echo "Creating document class sources"
    echo "======================================================================"
    echo

    run_cmd cd $base_dir

    set_classpath \
      $java_lib_dir/mumie-util.jar \
      $base_dir/lib/java/mumie-srv-build.jar

    local xsl_file=tools/xsl/Document.xsl
    local decl_file=config/declarations.xml
    local mtime_xsl=`mtime $xsl_file`
    local mtime_decl=`mtime $decl_file`
    local doctype

    for doctype in `get_doctypes` ; do

      local class_name="`to_class_name $doctype`Document"
      local src_file=src/java/net/mumie/srv/entities/documents/${class_name}.java

      # Check if target must be (re)build:
      local needs_build=''
      if [ "$force" ] ; then
        # Force flag enabled, thus (re)build required:
        needs_build=enabled
      elif [ ! -e "$src_file" ] ; then
        # Source file does not exist, thus build required:
        needs_build=enabled
      else
        local mtime_src=`mtime $src_file`
        if [ $mtime_src -le $mtime_xsl ] || [ $mtime_src -le $mtime_decl ] ; then
          needs_build=enabled
        fi
      fi

      if [ "$needs_build" ] ; then
        run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
          --input=$decl_file \
          --output=$src_file \
          --stylesheet=$xsl_file \
          --param type=$doctype \
          --param class-name=$class_name
      fi

    done

    create_document_class_sources_done=done
    echo "$program_name: document class sources done"
  }

# Creates the rootxsl stylesheet. This is done by substituting the placeholder
# "@url-prefix@" in rootxsl.xsl.tpl.

function create_rootxsl
  {
    echo
    echo "======================================================================"
    echo "Creating rootxsl"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/fs_content
    echo "$program_name: changed into fs_content/"
    if [ "$force" ] || \
       [ ! -e rootxsl.xsl ] || \
       [ "`mtime rootxsl.xsl`" -le "`mtime rootxsl.xsl.tpl`" ] ; then
      sed "s/\@url-prefix\@/`quote $url_prefix`/g" rootxsl.xsl.tpl > rootxsl.xsl
      check_exit_code
    fi
    echo "$program_name: rootxsl done"
    create_rootxsl=done
    run_cmd cd $base_dir
  }

# Creates the Mmsrv Java library

function create_mmsrv_lib
  {
    [ "$create_db_helper_sources_done" ] || [ "$ignore_deps" ] || \
      create_db_helper_sources
    [ "$create_notion_class_sources_done" ] || [ "$ignore_deps" ] || \
      create_notion_class_sources
    [ "$create_document_class_sources_done" ] || [ "$ignore_deps" ] || \
      create_document_class_sources
    echo
    echo "======================================================================"
    echo "Creating Mmsrv lib"
    echo "======================================================================"
    echo
    local jar_file=$base_dir/lib/java/mumie-srv.jar
    [ "$tomcat_home" ] || error "tomcat_home not set"
    [ "`find $webapp_lib_dir -name 'postgresql-*.jar'`" ] || error "Missing postgres jar in $webapp_lib_dir"
    if [ "`get_java_source_files net/mumie/srv`" ] ; then
      run_ant -buildfile tools/ant/main.xml
    fi
    echo "$program_name: mmsrv lib done"
    create_mmsrv_lib_done=done
  }

# Copies the Java libraries to the WEB-INF/lib directory of the Cocoon webapp in Tomcat.
# Some of the libraries are copied from the lib/java directory of the build tree, some
# are copied from its usual installation location, i.e., $prefix/lib/java

function install_libs
  {
    echo
    echo "======================================================================"
    echo "Installing libraries"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/java
    echo "$program_name: changed into lib/java"
    run_cmd cp -v $lib_install_files_1 $webapp_lib_dir
    run_cmd cd $java_lib_dir
    echo "$program_name: changed into $java_lib_dir"
    run_cmd cp -v $lib_install_files_2 $webapp_lib_dir
    run_cmd cd $base_dir
    echo "$program_name: installing libs done"
    install_libs_done=done
  }

# Removes the Japs Java library (mumie-japs.jar) and the Mumie SQL Java library
# (mumie-sql.jar) from the WEB-INF/lib directory of the Cocoon webapp in Tomcat.

function uninstall_libs
  {
    echo
    echo "======================================================================"
    echo "Uninstalling libraries"
    echo "======================================================================"
    echo
    run_cmd cd $webapp_lib_dir
    echo "$program_name: chenged into $webapp_lib_dir"
    run_cmd rm -fv $lib_install_files_1 $lib_install_files_2
    run_cmd cd $base_dir
    echo "$program_name: uninstalling libs done"
    uninstall_libs_done=done
  }

# Creates db_table_names.xsl and db_column_names.xsl
function create_tools_xsl
  {
    [ "$create_build_tools_done" ] || [ "$ignore_deps" ] || create_build_tools

    echo
    echo "======================================================================"
    echo "Creating tools XSLs"
    echo "======================================================================"
    echo

    local xsl_dir=$base_dir/tools/xsl
    local decl_file=$base_dir/config/declarations.xml
    local db_table_names_xsl=$xsl_dir/db_table_names.xsl
    local db_table_names_stl=$xsl_dir/create_db_table_names.xsl
    local db_column_names_xsl=$xsl_dir/db_column_names.xsl
    local db_column_names_stl=$xsl_dir/create_db_column_names.xsl
    local db_function_names_xsl=$xsl_dir/db_function_names.xsl
    local db_function_names_stl=$xsl_dir/create_db_function_names.xsl

    set_classpath \
      $java_lib_dir/mumie-util.jar \
      $base_dir/lib/java/mumie-srv-build.jar

    # (re)create db_table_names.xsl if necessary:
    if [ "$force" ] || \
       [ ! -e "$db_table_names_xsl" ] || \
       [ "`mtime $db_table_names_xsl`" -le "`mtime $decl_file`" ] || \
       [ "`mtime $db_table_names_xsl`" -le "`mtime $db_table_names_stl`" ]
    then
      run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
        --stylesheet=$db_table_names_stl \
        --input=$decl_file \
        --output=$db_table_names_xsl
    fi

    # (re)create db_column_names.xsl if necessary:
    if [ "$force" ] || \
       [ ! -e "$db_column_names_xsl" ] || \
       [ "`mtime $db_column_names_xsl`" -le "`mtime $decl_file`" ] || \
       [ "`mtime $db_column_names_xsl`" -le "`mtime $db_column_names_stl`" ]
    then
      run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
        --stylesheet=$db_column_names_stl \
        --input=$decl_file \
        --output=$db_column_names_xsl
    fi

    # (re)create db_function_names.xsl if necessary:
    if [ "$force" ] || \
       [ ! -e "$db_function_names_xsl" ] || \
       [ "`mtime $db_function_names_xsl`" -le "`mtime $decl_file`" ] || \
       [ "`mtime $db_function_names_xsl`" -le "`mtime $db_function_names_stl`" ]
    then
      run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
        --stylesheet=$db_function_names_stl \
        --input=$decl_file \
        --output=$db_function_names_xsl
    fi

    echo "$program_name: tools XSLs done"
    create_tools_xsl_done=done
  }

# [Auxiliary target]
# Checks if the db user $db_user_name exists

function check_db_user
  {
    echo
    echo "======================================================================"
    echo "Checking db user"
    echo "======================================================================"
    echo
    if [ "`find_db_user`" ] ; then
      echo "$program_name: user \"$db_user_name\" exists"
      db_user_exists=exists
    else
      echo "$program_name: user \"$db_user_name\" does not exist"
    fi
    echo "$program_name: checking db user done"
    check_db_user=done
  }

# [Auxiliary target]
# Creates the database user provided the user does not exist already. This target
# depends on check_db_user, which sets the flag $db_user_exists if the user exists.
# If the flag is not set, this target (create_db_user) is executed; otherwise, it
# is skipped.

function create_db_user
  {
    [ "$check_db_user_done" ] || [ "$ignore_deps" ] || check_db_user
    echo
    echo "======================================================================"
    echo "Creating db user"
    echo "======================================================================"
    echo
    if [ ! "$db_user_exists" ] ; then
      echo "\
        BEGIN TRANSACTION;
        CREATE USER $db_user_name NOCREATEDB NOCREATEUSER;
        ALTER USER $db_user_name WITH PASSWORD '$db_user_password'; 
        COMMIT TRANSACTION;
        " \
        | psql_admin
      echo "$program_name: creating db user done"
    else
      echo "$program_name: skipped (user already exists)"
    fi

    # Update check_db_user flags:
    db_user_exists=exists
    check_db_user=done

    create_db_user_done=done
  }

# Deletes the database user provided the user exists. This target depends on
# check_db_user, which sets the flag $db_user_exists if the user exists. If
# the flag is set, this target (drop_db_user) is executed, otherwise it is
# skipped.

function drop_db_user
  {
    [ "$check_db_user_done" ] || [ "$ignore_deps" ] || check_db_user
    echo
    echo "======================================================================"
    echo "Dropping db user"
    echo "======================================================================"
    echo
    if [ "$db_user_exists" ] ; then
      psql_admin --command "DROP USER $db_user_name;"
      echo "$program_name: dropping db user done"
    else
      echo "$program_name: skipped (user does not exist)"
    fi

    # Update check_db_user flags:
    db_user_exists=''
    check_db_user=done

    drop_db_user_done=done
  }

# [Auxiliary target]
# Checks if the database $db_name exists

function check_db
  {
    echo
    echo "======================================================================"
    echo "Checking db"
    echo "======================================================================"
    echo
    if [ "`find_db`" ] ; then
      echo "$program_name: database \"$db_name\" exists"
      db_exists=exists
    else
      echo "$program_name: database \"$db_name\" does not exist"
    fi
    echo "$program_name: checking db done"
    check_db=done
  }

# [Auxiliary target]
# Creates the database provided it does not exist already. This target depends on
# check_db, which sets the flag $db_exists if the database exists. If the flag is
# not set, this target (create_db) is executed; otherwise, it is skipped.

function create_db
  {
    [ "$check_db_done" ] || [ "$ignore_deps" ] || check_db
    echo
    echo "======================================================================"
    echo "Creating db"
    echo "======================================================================"
    echo
    if [ ! "$db_exists" ] ; then
      psql_admin \
        -c "CREATE DATABASE $db_name OWNER=$db_user_name ENCODING=$db_encoding;"
      echo "$program_name: creating db done"
    else
      echo "$program_name: skipped (database already exists)"
    fi

    # Update check_db flags:
    db_exists=exists
    check_db=done

    create_db_done=done
  }

# Deletes the database provided it exists. This target depends on  check_db, which
# sets the flag $db_exists if the database exists. If the flag is set, this target
# (drop_db) is executed; otherwise, it is skipped.

function drop_db
  {
    [ "$check_db_done" ] || [ "$ignore_deps" ] || check_db
    echo
    echo "======================================================================"
    echo "Dropping db"
    echo "======================================================================"
    echo
    if [ "$db_exists" ] ; then
      psql_admin --command "DROP DATABASE $db_name;"
      echo "$program_name: dropping db done"
    else
      echo "$program_name: skipped (database does not exist)"
    fi

    # Update check_db flags:
    db_exists=''
    check_db=done

    drop_db_done=done
  }

# [Auxiliary target]
# Creates the SQL code that defines the core database structure. This is done by applying
# create_db_core_sql.xsl to the config file (config/config.xml). The function does this by means of
# the XSLTransformer class from the build tools. This target requires the build tools and
# the Japs library; however, it does not call create_build_tools or create_japs_lib
# automatically. This is for more efficiency in the build process.

function create_db_main_sql
  {
    [ "$create_tools_xsl_done" ] || [ "$ignore_deps" ] || create_tools_xsl
    # [ "$create_offline_tools_done" ] || [ "$ignore_deps" ] || create_offline_tools
    echo
    echo "======================================================================"
    echo "Creating db main SQL"
    echo "======================================================================"
    echo
    local decl_file=$base_dir/config/declarations.xml
    local db_main_sql_trg=$base_dir/src/sql/db_main.sql
    local db_main_sql_stl=$base_dir/tools/xsl/db_main.xsl
    # Check if target must be (re)build:
    if [ "$force" ] || [ ! -e "$db_main_sql_trg" ] || \
       [ "`mtime $db_main_sql_trg`" -le "`mtime $decl_file`" ] || \
       [ "`mtime $db_main_sql_trg`" -le "`mtime $db_main_sql_stl`" ] ; then
      # (Re)build target:
      echo "$program_name: calling XSLTransformer"
      set_classpath \
        $java_lib_dir/mumie-util.jar \
        $java_lib_dir/mumie-sql.jar \
        $base_dir/lib/java/mumie-srv-build.jar \
        $base_dir/lib/java/mumie-srv-offline.jar \
        $base_dir/lib/java/mumie-srv.jar \
        $webapp_lib_dir/$avalon_framework_api_jar \
        $webapp_lib_dir/$avalon_framework_impl_jar
      run_cmd $java_cmd \
        net.mumie.srv.build.XSLTransformer \
        --stylesheet=$db_main_sql_stl \
        --input=$decl_file \
        --output=$db_main_sql_trg \
        --param admin-user.login_name="$admin_name" \
        --param admin-user.password="$admin_password" \
        --param password-encryptor.class-name="$password_encryptor_class" \
        --param default-language.code="$default_lang_code"
    fi
    echo "$program_name: db core sql done"
    create_db_main_sql_done=done
    run_cmd cd $base_dir
  }

# [Auxiliary target]
# Creates the database tables (but does not fill them with content). This is done
# by executing the SQL code created by create_db_main_sql in psql. This target
# requires that the database user and the database exists and that the database is
# empty. These requirements are not checked automatically, and the corresponding
# targets are not called automatically if needed. This is for more simplicity in
# the build process. However, the dependency on create_db_main_sql is implemented.

function create_db_main
  {
    [ "$create_db_main_sql_done" ] || [ "$ignore_deps" ] || create_db_main_sql
    echo
    echo "======================================================================"
    echo "Creating db main"
    echo "======================================================================"
    echo
    echo "\
      \\set ON_ERROR_STOP
      \\i $base_dir/db/src/db_main.sql" \
      | psql_user
    echo "$program_name: creating db main done"
    create_db_main_done=done
  }

# Creates the database, the database tables, functions, and views. Essentially,
# this is an aggregation of the targets create_db_user, create_db, create_db_main
# and create_db_functions. But before the targets are executed, it
# is checked if the dababase exists already. If this is the case, an error is
# signaled. This prevents the user from accidentally deleting an existing datatbase.

function build_db
  {
    check_db
    [ "$db_exists" ] && error "Database already exists (use drop-db to remove it)"
    create_db_user
    create_db
    create_db_main
  }

# Creates the cocoon.xconf file. The file is created from an XML source,
# cocoon.xconf.src. The creation is done by applying an XSL stylesheet to
# the global config file, config.xml. The source (cocoon.xconf.src) is read
# via the document() XPath function. 

function create_xconf
  {
    [ "$create_build_tools_done" ] || [ "$ignore_deps" ] || create_build_tools
    echo
    echo "======================================================================"
    echo "Creating xconf"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir

    local target_file=$base_dir/config/cocoon.xconf
    local source_file=$base_dir/config/cocoon.xconf.src
    local config_file=$base_dir/config/config.xml
    local xsl_file=$base_dir/tools/xsl/xconf.xsl

    # Check if target must be (re)build:
    local needs_build=''
    if [ "$force" ] ; then
      # Force flag enabled, thus (re)build required:
      needs_build=enabled
    else
      # Check if target file exists:
      if [ ! -e "$target_file" ] ;then
        # Does not exist, (re)build required:
        needs_build=enabled
      else
        # Target file exists, check modification times:
        local target_mtime=`mtime $target_file`
        if [ $target_mtime -le "`mtime $source_file`" ] || \
           [ $target_mtime -le "`mtime $config_file`" ] || \
           [ $target_mtime -le "`mtime $user_config_file`" ] || \
           [ $target_mtime -le "`mtime $xsl_file`" ] ; then
          # Target is outdated, thus (re)build required:
          needs_build=enabled
        fi
      fi
    fi
        
    # (Re)build target if necessary:
    if [ "$needs_build" ] ; then
      set_classpath \
        $java_lib_dir/mumie-util.jar \
        $base_dir/lib/java/mumie-japs-build.jar
      echo "$program_name: calling XSLTransformer"
      run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
        --stylesheet=$xsl_file \
        --input=$config_file \
        --output=$target_file \
        --param xconf.source.filename=$source_file \
        --param xconf.db.host="$db_host" \
        --param xconf.db.port="$db_port" \
        --param xconf.db.name="$db_name" \
        --param xconf.db.user.name="$db_user_name" \
        --param xconf.db.user.password="$db_user_password" \
        --param xconf.mail.smtp.host="$mail_smtp_host" \
        --param xconf.mail.from.name="$mail_from_name" \
        --param xconf.mail.from.address="$mail_from_address" \
        --param xconf.mail.reply.name="$mail_reply_name" \
        --param xconf.mail.reply.address="$mail_reply_address" \
        --param xconf.sign.keystore.filename="$sign_keystore_file" \
        --param xconf.sign.keystore.type="$sign_keystore_type" \
        --param xconf.sign.keystore.password="$sign_keystore_password" \
        --param xconf.sign.key.alias="$sign_key_alias" \
        --param xconf.sign.key.password="$sign_key_password" \
        --param xconf.upload.dir="$upload_dir" \
        --param xconf.checkin.defaults.file="$webapp_webinf/checkin_defaults.xml"
      fi

    echo "$program_name: creating xconf done"
    create_xconf_done=done
    run_cmd cd $base_dir
  }

# Creates the mumie.roles file. The file is created from an XML source,
# mumie.roles.src. The creation is done by applying an XSL stylesheet to
# that file. The declarations file (declarations.xml) is read via the document()
# XPath function. 
# [temp: adapted]

function create_roles
  {
    [ "$create_build_tools_done" ] || [ "$ignore_deps" ] || create_build_tools
    echo
    echo "======================================================================"
    echo "Creating roles"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir

    local target_file=$base_dir/config/mumie.roles
    local source_file=$base_dir/config/mumie.roles.src
    local decl_file=$base_dir/config/declarations.xml
    local xsl_file=$base_dir/tools/xsl/roles.xsl

    # Check if target must be (re)build:
    local needs_build=''
    if [ "$force" ] ; then
      # Force flag enabled, thus (re)build required:
      needs_build=enabled
    else
      # Check if target file exists:
      if [ ! -e "$target_file" ] ;then
        # Does not exist, (re)build required:
        needs_build=enabled
      else
        # Target file exists, check modification times:
        local target_mtime=`mtime $target_file`
        if [ $target_mtime -le "`mtime $source_file`" ] || \
           [ $target_mtime -le "`mtime $decl_file`" ] || \
           [ $target_mtime -le "`mtime $xsl_file`" ] ; then
          # Target is outdated, thus (re)build required:
          needs_build=enabled
        fi
      fi
    fi
        
    # (Re)build target if necessary:
    if [ "$needs_build" ] ; then
      set_classpath \
        $java_lib_dir/mumie-util.jar \
        $base_dir/lib/java/mumie-srv-build.jar
      echo "$program_name: calling XSLTransformer"
      run_cmd $java_cmd net.mumie.srv.build.XSLTransformer \
        --input=$source_file \
        --output=$target_file \
        --stylesheet=$xsl_file \
        --param decl-file=$decl_file
      fi

    echo "$program_name: creating roles done"
    create_roles_done=done
    run_cmd cd $base_dir
  }

# Installs the configuration files except sitemap.xmap
function install_config
  {
    [ "$create_xconf_done" ] || [ "$ignore_deps" ] || create_xconf
    [ "$create_roles_done" ] || [ "$ignore_deps" ] || create_roles
    echo
    echo "======================================================================"
    echo "Installing config files"
    echo "======================================================================"
    echo
    run_cmd mkdir -pv $base_dir/uninstall
    local config_file
    local uninstall_dir=$base_dir/uninstall
    local config_dir=$base_dir/config
    for config_file in $config_install_files ; do
      # If necessary, save original:
      [ -e "$webapp_webinf/$config_file" ] && [ ! -e "$uninstall_dir/$config_file" ] && \
        run_cmd cp -v "$webapp_webinf/$config_file" $uninstall_dir
      # Copy config file:
      run_cmd cp -v "$config_dir/$config_file" $webapp_webinf
    done
    run_cmd cd $base_dir
    echo "$program_name: installing config files done"
    install_config_done=done
  }

# Uninstalls the configuration files except sitemap.xmap
function uninstall_config
  {
    echo
    echo "======================================================================"
    echo "Uninstalling config files"
    echo "======================================================================"
    echo
    local config_file
    local uninstall_dir=$base_dir/uninstall
    local config_dir=$base_dir/config
    for config_file in $config_install_files ; do
      run_cmd rm -vf $webapp_webinf/$config_file
      [ -e "$uninstall_dir/$config_file" ] && \
        run_cmd mv -v $uninstall_dir/$config_file $webapp_webinf
    done    
    run_cmd cd $base_dir
    echo "$program_name: uninstalling config files done"
    uninstall_config_done=done
  }

# Installs the "file system content" files
function install_fs_content
  {
    echo
    echo "======================================================================"
    echo "Installing fs content"
    echo "======================================================================"
    echo
    run_cmd mkdir -pv $webapp_root/fs_content
    run_cmd cd $base_dir/fs_content
    echo "$program_name: changed into fs_content/"
    cp_to_dir $webapp_root/fs_content $fsc_install_files
    echo "program_name: installing fs content done"
    install_fs_content_done=done
    run_cmd cd $base_dir
  }

# Uninstalls the "file system content" files
function uninstall_fs_content
  {
    echo
    echo "======================================================================"
    echo "Uninstalling fs content"
    echo "======================================================================"
    echo
    if [ -e $webapp_root/fs_content ] ; then
      run_cmd cd $webapp_root/fs_content
      echo "program_name: changed into $webapp_root/fs_content"
      rm -vf $fsc_install_files
    fi
    echo "program_name: uninstalling fs content done"
    uninstall_fs_content_done=done
    run_cmd cd $base_dir
  }

# Creates the checkin.zip archive
function create_checkin_zip
  {
    echo
    echo "======================================================================"
    echo "Creating checkin.zip"
    echo "======================================================================"
    echo
    run_cmd rm -vf $base_dir/checkin/checkin.zip
    run_cmd cd $checkin_root
    echo "$program_name: changed into $checkin_root"
    run_cmd find -L -name "*.*" \
      | egrep '\.meta\.xml$|\.content\.[^.~]+$|\.src\.[^.~]+$' \
      | zip -@ $base_dir/checkin/checkin.zip
    echo "$program_name: creating checkin.zip done"
    create_checkin_zip_done=done
    run_cmd cd $base_dir
  }

#
#
function prepare_sitemaps
  {
    echo
    echo "======================================================================"
    echo "Preparing sitemaps"
    echo "======================================================================"
    echo
    [ "$url_prefix" ] || error "url_prefix not set"
    run_cmd cd $base_dir/config
    echo "$program_name: changed into config/"
    local template
    for template in build_sitemap.xmap.tpl sitemap.xmap.src.tpl ; do
      local target=${template%.tpl}
      echo "$program_name: Creating $target"
      run_cmd cat $template \
        | run_cmd sed "s/\@url-prefix\@/`quote $url_prefix`/g" \
        | run_cmd sed "s/\@qf-applet-mail-to-name\@/`quote $qf_applet_mail_to_name`/g" \
        | run_cmd sed "s/\@qf-applet-mail-to-address\@/`quote $qf_applet_mail_to_address`/g" \
        > $target
    done
    run_cmd cd $base_dir
    echo "$program_name: preparing sitemaps done"
    prepare_sitemaps_done=done
  }

# Installs the sitemap and checkin defaults which are used temporarily during
# the build
function tmp_settings
  {
    [ "$prepare_sitemaps_done" ] || [ "$ignore_deps" ] || prepare_sitemaps
    echo
    echo "======================================================================"
    echo "Temorary server settings"
    echo "======================================================================"
    echo
    # Backup original sitemap:
    backup_sitemap
    run_cmd cd $base_dir/config
    echo "$program_name: changed into config/"
    # Install build sitemap:
    run_cmd cp -v build_sitemap.xmap $webapp_root/sitemap.xmap
    run_cmd cd $base_dir
    echo "$program_name: temporary server settings done"
    tmp_settings_done=done
  }

# Checks-in initial documents and (pseudo-)documents
function checkin
  {
    [ "$create_checkin_zip_done" ] || [ "$ignore_deps" ] || create_checkin_zip
    echo
    echo "======================================================================"
    echo "Checkin"
    echo "======================================================================"
    echo
    set_classpath \
      $java_lib_dir/mumie-japs-client.jar \
      $java_lib_dir/jcookie-0.8c.jar \
      $java_lib_dir/mumie-util.jar \
      $base_dir/lib/java/mumie-japs-build.jar
    run_cmd $java_cmd \
      net.mumie.srv.build.JapsUpload \
      --url-prefix=$url_prefix \
      --path=protected/checkin/checkin \
      --account=$admin_name \
      --password=$admin_password \
      --file=$base_dir/checkin/checkin.zip
    run_cmd cd $base_dir
    echo "$program_name: checkin done"
    checkin_done=done
  }

# Creates the sitemap
function create_sitemap
  {
    [ "$create_build_tools_done" ] || [ "$ignore_deps" ] || create_build_tools
    [ "$install_fs_content_done" ] || [ "$ignore_deps" ] || install_fs_content
    [ "$prepare_sitemaps_done" ] || [ "$ignore_deps" ] || prepare_sitemaps
    echo
    echo "======================================================================"
    echo "Creating sitemap"
    echo "======================================================================"
    echo
    local target_file=$base_dir/config/sitemap.xmap
    local source_file=$base_dir/config/sitemap.xmap.src
    if [ "$force" ] || \
       [ ! -e $target_file ] || \
       [ "`mtime $target_file`" -le "`mtime $source_file`" ]
    then
      echo "$program_name: calling JapsUpload"
      set_classpath \
        $java_lib_dir/mumie-japs-client.jar \
        $java_lib_dir/jcookie-0.8c.jar \
        $java_lib_dir/mumie-util.jar \
        $base_dir/lib/java/mumie-japs-build.jar
      run_cmd $java_cmd \
        net.mumie.srv.build.JapsUpload \
        --url-prefix=$url_prefix \
        --path=protected/admin/create-sitemap \
        --account=$admin_name \
        --password=$admin_password \
        --file=$source_file \
        --output=$target_file
    fi
    run_cmd cd $base_dir
    echo "$program_name: creating sitemap done"
    create_sitemap_done=done
  }

# Installs the sitemap
function install_sitemap
  {
    echo
    echo "======================================================================"
    echo "Installing sitemap"
    echo "======================================================================"
    echo
    backup_sitemap
    run_cmd cp -v $base_dir/config/sitemap.xmap $webapp_root
    run_cmd cd $base_dir
    echo "$program_name: installing sitemap done"
    install_sitemap_done=done
  }

# Uninstalls the sitemap
function uninstall_sitemap
  {
    echo
    echo "======================================================================"
    echo "Uninstalling sitemap"
    echo "======================================================================"
    echo
    local config_file
    local uninstall_dir=$base_dir/uninstall
    [ -e "$uninstall_dir/sitemap.xmap" ] && \
        run_cmd mv -v "$uninstall_dir/sitemap.xmap" $webapp_root
    run_cmd cd $base_dir
    echo "$program_name: uninstalling sitemap done"
    uninstall_sitemap_done=done
  }

# Installs the checkin_defaults.xml file
function install_checkin_defaults
  {
    echo
    echo "======================================================================"
    echo "Installing checkin defaults"
    echo "======================================================================"
    echo
    run_cmd cp -v $base_dir/config/checkin_defaults.xml $webapp_webinf
    run_cmd cd $base_dir
    echo "$program_name: installing checkin defaults done"
    install_checkin_defaults_done=done
  }

# Uninstalls the checkin_defaults.xml file
function uninstall_checkin_defaults
  {
    echo
    echo "======================================================================"
    echo "Uninstalling checkin defaults"
    echo "======================================================================"
    echo
    run_cmd rm -fv $webapp_webinf/checkin_defaults.xml
    run_cmd cd $base_dir
    echo "$program_name: uninstalling checkin defaults done"
    uninstall_checkin_defaults_done=done
  }

# Creates the API documentation for the Japs Java library and the build tools
function create_java_apidocs
  {
    echo
    echo "======================================================================"
    echo "Creating Java apidocs"
    echo "======================================================================"
    echo
    run_ant -buildfile tools/ant/apidocs.xml
    echo "$program_name: java apidocs done"
    create_java_apidocs_done=done
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
    local dist_name="japs_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag japs
    run_cmd mv -v japs $dist_name
    echo "$program_name: Creating version file"
    run_cmd echo $release > $dist_name/$version_file
    echo "$program_name: Creating tgz"
    run_cmd tar czf $archive $dist_name
    run_cmd cd $base_dir
  }

# Creates the Japs Java library
function create_japs_lib_for_mf
  {
    echo
    echo "======================================================================"
    echo "Creating Japs lib for matheletfactory"
    echo "======================================================================"
    echo
    run_ant -buildfile tools/ant/buildfiles/japs_lib_for_mf.xml
    echo "$program_name: japs lib done"
    create_japs_lib_for_mf_done=done
  }

# --------------------------------------------------------------------------------
# Functions implementing tasks
# --------------------------------------------------------------------------------

# Processes the targets
function process_targets
  {
    local target
    for target in $targets ; do
      case $target in
        build-tools)
          create_build_tools ;;
        offline-tools)
          create_offline_tools ;;
        notion-class-sources)
          create_notion_class_sources ;;
        db-helper-sources)
          create_db_helper_sources ;;
        doc-class-sources)
          create_document_class_sources ;;
        mmsrv-lib)
          create_mmsrv_lib ;;
        install-libs)
          install_libs ;;
        rootxsl)
          create_rootxsl ;;
        tools-xsl)
          create_tools_xsl ;;
        db-user)
          create_db_user ;;
        drop-db-user)
          drop_db_user ;;
        create-db)
          create_db ;;
        drop-db)
          drop_db ;;
        db-main-sql)
          create_db_main_sql ;;
        db-main)
          create_db_main ;;
        db)
          build_db ;;
        xconf)
          create_xconf ;;
        roles)
          create_roles ;;
        install-config)
          install_config ;;
        install-fs-content)
          install_fs_content ;;
        checkin-zip)
          create_checkin_zip ;;
        prepare-sitemaps)
          prepare_sitemaps ;;
        tmp-settings)
          tmp_settings ;;
        checkin)
          checkin ;;
        sitemap)
          create_sitemap ;;
        install-sitemap)
          install_sitemap ;;
        uninstall-libs)
          uninstall_libs ;;
        uninstall-config)
          uninstall_config ;;
        uninstall-fs-content)
          uninstall_fs_content ;;
        uninstall-sitemap)
          uninstall_sitemap ;;
        all-step1)
          create_mmsrv_lib
          install_libs
          build_db
          install_config
          install_fs_content
          tmp_settings ;;
        all-step2)
          checkin
          create_sitemap ;;
        all-step3)
          install_sitemap ;;
        apidocs)
          create_java_apidocs ;;
        dist)
          create_dist ;;
        mmsrv-lib-for-mf)
          create_mmsrv_lib_for_mf ;;
        *)
          echo "ERROR: Unknown target: $target"
          exit 3 ;;
      esac
    done
    echo
    echo "$program_name: BUILD DONE"
    echo
  }

function check
  {
    [ -e "$java_lib_dir/mumie-util.jar" ] || \
      warning "mumie-util.jar not found in $java_lib_dir"
    [ -e "$java_lib_dir/mumie-sql.jar" ] || \
      warning "mumie-sql.jar not found in $java_lib_dir"
    [ -e "$tomcat_home" ] || \
      warning "$tomcat_home not found";
    [ -e "$webapp_root" ] || \
      warning "$webapp_root not found";
    [ -e "$webapp_lib_dir/postgresql.jar" ] || \
      warning "postgresql.jar not found in $webapp_lib_dir"
    which psql > /dev/null 2>&1
    [ $? -eq 0 ] ||
      warning "psql not found in PATH"
    if [ $numwarn -gt 0 ] ; then
      echo "$program_name: $numwarn warning(s)"
    else
      echo "No warnings"
    fi
  }

function print_variables
  {
    cat <<EOF
admin_name               = $admin_name
admin_password           = $admin_password
avalon_framework_apidocs_url = $avalon_framework_apidocs_url
checkin_root             = $checkin_root
webapp_apidocs_url       = $webapp_apidocs_url
webapp_lib_dir           = $webapp_lib_dir
webapp_root              = $webapp_root
webapp_webinf            = $webapp_webinf
cvsroot                  = $cvsroot
datasheet_apidocs_url    = $datasheet_apidocs_url
db_admin_name            = $db_admin_name
db_admin_password        = $db_admin_password
db_encoding              = $db_encoding
db_host                  = $db_host
db_name                  = $db_name
db_port                  = $db_port
db_user_name             = $db_user_name
db_user_password         = $db_user_password
doc_dir                  = $doc_dir
excalibur_xmlutil_apidocs_url = $excalibur_xmlutil_apidocs_url
force                    = $force
ignore_deps              = $ignore_deps
java_cmd                 = $java_cmd
java_lib_dir             = $java_lib_dir
javac_debug              = $javac_debug
javac_deprecation        = $javac_deprecation
javac_verbose            = $javac_verbose
jdk_apidocs_url          = $jdk_apidocs_url
mail_domain              = $mail_domain
mail_from_address        = $mail_from_address
mail_from_name           = $mail_from_name
mail_reply_address       = $mail_reply_address
mail_reply_name          = $mail_reply_name
mail_smtp_host           = $mail_smtp_host
password_encryptor_class = $password_encryptor_class
prefix                   = $prefix
qf_applet_mail_to_address = $qf_applet_mail_to_address
qf_applet_mail_to_name   = $qf_applet_mail_to_name
release                  = $release
sign_key_alias           = $sign_key_alias
sign_key_password        = $sign_key_password
sign_keystore_file       = $sign_keystore_file
sign_keystore_password   = $sign_keystore_password
sign_keystore_type       = $sign_keystore_type
targets                  = $targets
task                     = $task
tomcat_home              = $tomcat_home
upload_dir               = $upload_dir
url_prefix               = $url_prefix
EOF
  }

function show_targets
  {
cat <<EOF
all-step1           - Complete build, step 1: creates and installs the Japs Java
                      library; creates the db with tables, functions, and views,
                      creates and installs the configuration files except
                      sitemap.xmap; installs the "file system content" files,
                      installs the checkin defaults file
all-step2           - Complete build, step 2: checks-in initial documents and
                      pseudo-documents, creates the sitemap
all-step3           - Complete build, step 3: installs the sitemap
apidocs             - Creates the API documentation for the Japs libraries and
                      the build tools
build-tools         - Creates the build tools
checkin             - Checks-in initial documents and pseudo-documents
checkin-zip         - Creates the checkin.zip archive
create-db           - Creates the db (if not exists already)
db                  - Creates the db, db tables, functions, and views
db-functions        - Creates the db functions
db-helper-sources   - Creates the db helper sources
db-main-sql         - Creates the SQL code for the main db structures
db-main             - Creates the db tables
db-user             - Creates the db user (if not exists already)
dist                - Creates a distribution
doc-class-sources   - Creates the document class sources
drop-db             - Drops the db (if exists)
drop-db-user        - Drops the db user (if exists)
install-config      - Installs the configuration files except sitemap.xmap
install-fs-content  - Installs the "file system content" files
install-libs        - Installs the Java libraries in the WEB-INF/lib directory
install-sitemap     - Installs the sitemap
mmsrv-lib           - Creates the Japs Java library
mmsrv-lib-for-mf    - Creates a special version of the Java library for the
                      Mathelt Factory
notion-class-sources - Creates the notion class sources
offline-tools       - Creates the offline tools
prepare-sitemaps    - Creates the build sitemap and the sitemap XML source from
                      *.tpl templates
roles               - Creates the mumie.roles file
rootxsl             - Creates the rootxsl stylesheet
sitemap             - Creates the sitemap
tmp-settings        - Installs a temporary sitemap and checkin defaults file for
                      use during the build
tools-xsl           - Creates the "notion" XSL stylesheets db_notions.xsl and
                      xml_notions.xsl
uninstall-config    - Uninstalls the configuration files except sitemap.xmap
uninstall-fs-content  - Uninstalls the "file system content" files
uninstall-libs      - Uninstalls the Java libraries
uninstall-sitemap   - Uninstalls the sitemap
xconf               - Creates the cocoon.xconf file
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the japs package, or parts of it. What is
  actually done is controlled by TARGETS, which is a list of keywords called
  targets. Type ./build.sh -t to get a list of all targets. The default target
  is "mmsrv-lib"; it is assumed if no targets are specified.
Options:
  --targets, -t
      List all targets
  --force, -f
      Create files even if they are up-to-date.
  --ignore-deps, -D
      Ignore target dependencies. If a target is build with this option,
      then targets required by this target are not build automatically.
  --javac-verbose
      Turns the "verbose" flag on when compiling the java sources.
  --javac-debug
      Turns the "debug" flag on when compiling the java sources.
  --javac-deprecation
      Turns the "deprecation" flag on when compiling the java sources.
  --release=VERSION_NUMBER
      Set the release for the distribution to build. In effect only with
      the "dist" target, otherwise ignored.
  --cvsroot=CVSROOT
      Set the cvs root for retrieving the distribution to build. In effect
      only with the "dist" target, otherwise ignored. If not set, the
      environment variable \$CVSROOT is used
  --vars
      Prints the build variables to stdout
  --check
      Performs some checks
  --help, -h
      Print this help text and exit.
  --version, -v
      Print version information and exit.
EOF
  }

function show_version
  {
    echo $program_version
  }

$task
