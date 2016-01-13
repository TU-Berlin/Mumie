#!/bin/bash

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: build.sh,v 1.51 2009/05/15 23:15:11 rassy Exp $

# Build script for mmcdk

# Set fixed variabes (constants):
readonly program_name=build.sh
readonly program_version='$Revision: 1.51 $'
readonly user_config_file=build.conf

# Clear some variables:
unset prefix checkin_root java_home signjar_alias signjar_storepass \
      jdk_apidocs_url japs_apidocs_url lang_codes task

# Source user config file:
[ -e "$user_config_file" ] && source "$user_config_file"

# Process command line parameters:
params=`getopt \
  --longoptions force,targets,ignore-deps,help,version,vars,release:,cvsroot:,javac-verbose,javac-deprecation,javac-debug \
  --options f,t,D,h,v \
  -- \
  "$@"`
if [ $? -ne 0 ] ; then exit 1 ; fi
eval set -- "$params"
while true ; do
  case "$1" in
    --targets|-t) task=show_targets ; shift ;;
    --ignore-deps|-D) ignore_deps=ignore_deps ; shift ;;
    --help|-h) task=show_help ; shift ;;
    --version|-v) task=show_version ; shift ;;
    --vars) task=print_variables ; shift ;;
    --force|-f) force=force ; shift ;;
    --release) release="$2" ; shift 2 ;;
    --cvsroot) cvsroot="$2" ; shift 2 ;;
    --javac-verbose) javac_verbose=enabled ; shift ;;
    --javac-deprecation) javac_deprecation=enabled ; shift ;;
    --javac-debug) javac_debug=enabled ; shift ;;
    --) shift ; break ;;
  esac
done
targets=${*:-'all'}

# Set the variables if not set already:
prefix=${prefix:-${MM_BUILD_PREFIX:-/usr/local}}
checkin_root=${checkin_root:-${MM_CHECKIN_ROOT:-$HOME/mumie/checkin}}
java_home=${java_home:-$JAVA_HOME}
lib_dir=$prefix/lib
java_lib_dir=$lib_dir/java
signjar_alias=${signjar_alias:-mumie}
signjar_storepass=${signjar_storepass:-japsen}
xsl_lib_dir=$lib_dir/mmcdk/xsl
exec_dir=$prefix/bin
etc_dir=$prefix/etc/mmcdk
var_dir=$prefix/var/mmcdk
doc_dir=$prefix/share/doc/mmcdk
jdk_apidocs_url=${jdk_apidocs_url:-$JAVA_HOME/docs/api}
japs_apidocs_url=${japs_apidocs_url:-${prefix}/share/doc/japs/apidocs}
lang_codes=${lang_codes:-en,de}
version_file=VERSION
task=${task:-process_targets}

# Store the current directory:
base_dir=`pwd`

# Java libraries to install/uninstall:
java_lib_install_files="
  mumie-cdk.jar
  mumie-japs-for-mmcdk.jar
"

# Third-party Java libraries to install/uninstall:
third_party_java_lib_install_files="
  avalon-framework-api-4.3.jar
  avalon-framework-impl-4.3.jar
  excalibur-pool-api-2.1.jar
  excalibur-pool-impl-2.1.jar
  excalibur-xmlutil-2.1.jar
"

# Japs library to install/uninstall:
japs_lib_install_file="mumie-japs-for-mmcdk.jar"

# XSL stylesheets to install/uninstall:
xsl_install_files="
  rootxsl_mmcdk.xsl
  master2tmp.xsl
"

# Commands to wrap by shell skripts:
wrapped_cmds="
  mmprev
  mmckrefs
  mmjava
  mmjmtag
  mmroot
  mmalias
  mmsrv
  mmckin
  mmlsrefs
  mmgendoc
  mmr2gdoc
  mmlsprev
  mmgdimc
  mmdatasheet
  mmencrypt
  mmsec
  mmimg
  mmckgdim
  mmcksecmaster
"

# Executables to install/uninstall (except mmcntmod, which is treated separately):
execs_install_files="
  $wrapped_cmds
  mmlnjava
  mmstorepass
  mmclearpass
"

# Config files to install/uninstall (except the template files for mmcntmod, which are
# treated separately):
config_install_files="
  mmcdk.conf
  mmcdk.init
  mmcd_def.sh
  mmcd.csh
  mmcd_setup.sh
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

# Compares a target and a source file and prints "needs_build" to stdout if the
# target file needs to be (re)build; otherwise, prints the empty string.
# Usage: needs_build SOURCE_FILE TARGET_FILE
function needs_build
  {
    local source_file=$1
    local target_file=$2
    if [ "$force" ] || \
       [ ! -e "$target_file" ] || \
       [ `stat -c %Y "$source_file"` -gt `stat -c %Y "$target_file"` ]
    then
      echo "needs_build"
    else
      echo ""
    fi
  }

# Compares last modification times and returns changed sources. If $force is
# set, all sources are returned regardless if they have changed or not.
# Usage: get_source_files SOURCE_SUFFIX TARGET_SUFFIX TARGET_DIR SOURCE_FILES
function get_source_files
  {
    local source_suffix=$1
    local target_suffix=$2
    local target_dir=$3
    shift; shift; shift;
    local source_file
    for source_file in "$@"
    do
      local target_file=${target_dir}${source_file%$source_suffix}${target_suffix}
      [ "`needs_build $source_file $target_file`" ] && echo $source_file
    done
  }

# Aborts with an error message
function error
  {
    echo "ERROR: $*"
    echo
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

# Runs ant
function run_ant
  {
    local ant_cmd="\
      ant -e \
          -Dmmcdk.install.lib.java.dir=$java_lib_dir \
          -Dmmcdk.jdk.apidocs.url=$jdk_apidocs_url \
          -Dmmcdk.japs.apidocs.url=$japs_apidocs_url " 
    [ "$force" ] && ant_cmd="$ant_cmd -Dmmcdk.force=yes"
    [ "$javac_verbose" ] && ant_cmd="$ant_cmd -Dmmcdk.javac.verbose=yes"
    [ "$javac_deprecation" ] && ant_cmd="$ant_cmd -Dmmcdk.javac.deprecation=yes"
    [ "$javac_debug" ] && ant_cmd="$ant_cmd -Dmmcdk.javac.debug=yes"
    [ -e "$base_dir/$version_file" ] &&
      ant_cmd="$ant_cmd -Dmmcdk.version=`cat $base_dir/$version_file`"
    run_cmd $ant_cmd "$@"
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

# Quotes sed metacharacters with a backslash. Used in sed input.
function quote
  {
    echo "$@" | sed 's:\([/$.*&{}^]\):\\\1:g' | sed 's:\[:\\[:g' | sed 's:\]:\\]:g'
  }

# --------------------------------------------------------------------------------
# Functions implementing targets
# --------------------------------------------------------------------------------

# Creates the java libraries
function create_java_libs
  {
    echo
    echo "======================================================================"
    echo "Creating Java libraries"
    echo "======================================================================"
    echo
    if [ "`get_java_source_files net/mumie/cdk`" ] ; then
      run_ant -buildfile tools/build.xml
    fi
    run_cmd cd $base_dir
    echo "$program_name: creating java libs done"
    create_java_libs_done=done
  }

# Creates the configuration files
function create_config
  {
    echo
    echo "======================================================================"
    echo "Creating config"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/etc

    # Process *.tpl files:
    local templates=`get_source_files .tpl "" "" *.tpl`
    if [ "$templates" ] ; then
      local template
      for template in $templates ; do
        local target=${template%.tpl}
        echo "$program_name: Creating $target"
        cat $template \
          | sed s/\@prefix\@/`quote $prefix`/g \
          | sed s/\@checkin-root\@/`quote $checkin_root`/g \
          | sed s/\@signjar-storepass\@/`quote $signjar_storepass`/g \
          | sed s/\@signjar-alias\@/`quote $signjar_alias`/g \
          | sed s/\@lang-codes\@/`quote $lang_codes`/g \
          > $target
        check_exit_code
      done
    fi

    # Make mmcd_setup.sh executable:
    chmod -v a+rx mmcd_setup.sh

    run_cmd cd $base_dir
    echo "$program_name: creating config done"
    create_config_done=done
  }

# Creates the command wrappers
function create_cmd_wrappers
  {
    echo
    echo "======================================================================"
    echo "Creating command wrappers"
    echo "======================================================================"
    echo

    run_cmd cd $base_dir/bin
    echo "$program_name: changed into bin/"
    local cmd_name
    for cmd_name in $wrapped_cmds ; do
      echo "$program_name: creating $cmd_name"
      run_cmd echo "\
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

[ \"\`mmjvmd status\`\" == 'Jvmd is not running' ] && mmjvmd start
mmjvmc --dir-file=.dir $cmd_name \"\$@\"
" > $cmd_name
    done

    run_cmd cd $base_dir
    echo "$program_name: creating command wrappers done"
    create_command_wrappers_done=done
  }

# Create the mmcntmod command
function create_mmcntmod
  {
    echo
    echo "======================================================================"
    echo "Creating mmcntmod"
    echo "======================================================================"
    echo
    run_cmd cd bin
    echo "$program_name: changed into bin/"
    if [ "`needs_build mmcntmod.tpl mmcntmod`" ] ; then
      echo "$program_name: creating mmcntmod"
      run_cmd sed s/\@prefix\@/`quote $prefix`/g mmcntmod.tpl > mmcntmod
      run_cmd chmod a+x mmcntmod
    fi
    run_cmd cd $base_dir
    echo "$program_name: creating mmcntmod done"
    create_mmcntmod_done=done
  }

# Creates the documentation
function create_apidocs
  {
    echo
    echo "======================================================================"
    echo "Creating apidocs"
    echo "======================================================================"
    echo

    # Call Ant to create the apidocs:
    run_ant -buildfile tools/build.xml apidocs

    run_cmd cd $base_dir
    echo "$program_name: creating apidocs done"
    create_apidocs_done=done
  }

# Creates the manual
function create_manual
  {
    echo
    echo "======================================================================"
    echo "Creating manual"
    echo "======================================================================"
    echo
    run_cmd cd doc/manual
    echo "$program_name: changed into doc/manual"
    [ "`needs_build manual.tex manual.xml`" ] && mmtex -s manual.tex
    [ "`needs_build manual.xml manual.xhtml`" ] && mmxtr -b manual.xml
    run_cmd cd $base_dir
    echo "$program_name: creating manual done"
    create_manual_done=done
  }

# Installs the Java libraries
function install_java_libs
  {
    echo
    echo "======================================================================"
    echo "Installing Java libraries"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/java
    echo "$program_name: changed into lib/java"
    run_cmd mkdir -pv $java_lib_dir
    run_cmd cp -v $java_lib_install_files $java_lib_dir
    run_cmd cd $base_dir
    echo "$program_name: installing java libs done"
    install_java_libs_done=done
  }

# Installs the Avalon and Excalibur libraries
function install_third_party_java_libs
  {
    echo
    echo "======================================================================"
    echo "Installing third-party Java libraries"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/java
    echo "$program_name: changed into lib/java"
    run_cmd mkdir -pv $java_lib_dir
    run_cmd cp -v $third_party_java_lib_install_files $java_lib_dir
    run_cmd cd $base_dir
    echo "$program_name: installing third-party Java libs done"
    install_third_party_java_libs_done=done
  }

# Copies the executables to their installation location
function install_executables
  {
    echo
    echo "======================================================================"
    echo "Installing executables"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/bin
    echo "$program_name: changed into bin/"
    run_cmd mkdir -pv $exec_dir
    run_cmd chmod a+x $execs_install_files
    run_cmd cp -v $execs_install_files $exec_dir
    run_cmd cd $base_dir
    echo "$program_name: installing executables done"
    install_executables_done=done
  }

# Copies the mmcntmod command and its auxiliary files to their installation locations
function install_mmcntmod
  {
    echo
    echo "======================================================================"
    echo "Installing mmcntmod"
    echo "======================================================================"
    echo

    # Mmcntmod executable:
    run_cmd cd $base_dir/bin
    echo "$program_name: changed into bin/"
    run_cmd mkdir -pv $exec_dir
    run_cmd cp -v mmcntmod $exec_dir

    # Auxiliary files:
    run_cmd mkdir -pv $etc_dir/mmcntmod
    run_cmd cd $base_dir/etc/mmcntmod
    echo "$program_name: changed into etc/mmcntmod"
    run_cmd cp -v build.sh.tpl.prot $etc_dir/mmcntmod/build.sh.tpl
    run_cmd cp -v section.meta.xml.tpl.prot $etc_dir/mmcntmod/section.meta.xml.tpl

    run_cmd cd $base_dir
    echo "$program_name: installing mmcntmod done"
    install_mmcntmod_done=done
  }

# Copies the config files to their installation location
function install_config
  {
    echo
    echo "======================================================================"
    echo "Installing config files"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/etc
    echo "$program_name: changed into etc/"
    run_cmd mkdir -pv $etc_dir
    run_cmd cp -v $config_install_files $etc_dir
    run_cmd cd $base_dir
    echo "$program_name: installing config files done"
    install_config_done=done
  }

# Copies the xsl files to their installation location
function install_xsl
  {
    echo
    echo "======================================================================"
    echo "Installing xsl files"
    echo "======================================================================"
    echo
    run_cmd cd $base_dir/lib/xsl
    echo "$program_name: changed into lib/xsl/"
    run_cmd mkdir -pv $xsl_lib_dir
    run_cmd cp -v $xsl_install_files $xsl_lib_dir
    run_cmd cd $base_dir
    echo "$program_name: installing xsl files done"
    install_xsl_done=done
  }

# Uninstalls the Java libraries
function uninstall_java_libs
  {
    echo
    echo "======================================================================"
    echo "Uninstalling Java libraries"
    echo "======================================================================"
    echo
    if [ -e $java_lib_dir ] ; then
      run_cmd cd $java_lib_dir
      echo "$program_name: changed into $java_lib_dir"
      run_cmd rm -vf $java_lib_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
    echo "$program_name: uninstalling java libs done"
    uninstall_java_libs_done=done
  }

# Installs the Avalon and Excalibur libraries
function uninstall_third_party_java_libs
  {
    echo
    echo "======================================================================"
    echo "Uninstalling third-party Java libraries"
    echo "======================================================================"
    echo
    if [ -e $java_lib_dir ] ; then
      run_cmd cd $java_lib_dir
      echo "$program_name: changed into $java_lib_dir"
      run_cmd rm -vf $third_party_java_lib_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
    echo "$program_name: uninstalling third-party java libs done"
    uninstall_third_party_java_libs_done=done
  }

# Removes the executables from their installation location
function uninstall_executables
  {
    echo
    echo "======================================================================"
    echo "Uninstalling executables"
    echo "======================================================================"
    echo
    if [ -e $exec_dir ] ; then
      run_cmd cd $exec_dir
      echo "$program_name:: changed into $exec_dir"
      run_cmd rm -vf $execs_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Removes the config files from their installation location
function uninstall_config
  {
    echo
    echo "======================================================================"
    echo "Uninstalling config files"
    echo "======================================================================"
    echo
    if [ -e $etc_dir ] ; then
      run_cmd cd $etc_dir
      echo "$program_name:: changed into $etc_dir"
      run_cmd rm -vf $config_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
  }

# Removes the mmcntmod command and its auxiliary files from their installation locations
function uninstall_mmcntmod
  {
    echo
    echo "======================================================================"
    echo "Uninstalling mmcntmod"
    echo "======================================================================"
    echo

    if [ -e $exec_dir ] ; then
      run_cmd cd $exec_dir
      echo "$program_name:: changed into $exec_dir"
      run_cmd rm -vf mmcntmod
    else
      echo "$program_name: installation dir of mmcntmod command does not exist"
    fi

    if [ -e $etc_dir/mmcntmod ] ; then
      run_cmd cd $etc_dir/mmcntmod
      echo "$program_name:: changed into $etc_dir/mmcntmod"
      run_cmd rm -vf build.sh.tpl section.meta.xml.tpl
    else
      echo "$program_name: installation dir mmcntmod auxiliary files does not exist"
    fi

    run_cmd cd $base_dir
  }

# Removes the xsl files from their installation location
function uninstall_xsl
  {
    echo
    echo "======================================================================"
    echo "Uninstalling xsl files"
    echo "======================================================================"
    echo
    if [ -e $xsl_lib_dir ] ; then
      run_cmd cd $xsl_lib_dir
      echo "$program_name:: changed into $xsl_lib_dir"
      run_cmd rm -vf $xsl_install_files
    else
      echo "$program_name: installation dir does not exist"
    fi
    run_cmd cd $base_dir
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
    local dist_name="mmcdk_${release}"
    local archive="${dist_name}.tgz"
    local tag="ver-`echo $release | tr '.' '-'`"
    run_cmd rm -rfv $dist_name
    run_cmd rm -fv $archive
    local cvscmd=cvs
    [ "$cvsroot" ] && cvscmd="cvs -d $cvsroot"
    run_cmd $cvscmd export -r $tag mmcdk2
    run_cmd mv -v mmcdk2 $dist_name
    echo "$program_name: Creating version file"
    run_cmd echo $release > $dist_name/$version_file
    echo "$program_name: Creating tgz"
    run_cmd tar czf $archive $dist_name
    run_cmd cd $base_dir
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
        java)
          create_java_libs ;;
        config)
          create_config ;;
        cmd-wrappers)
          create_cmd_wrappers ;;
        mmcntmod)
          create_mmcntmod ;;
        all)
          create_java_libs ; create_config ; create_cmd_wrappers ; create_mmcntmod ;;
        apidocs)
          create_apidocs ;;
        manual)
          create_manual ;;
        install-java)
          install_java_libs ;;
        install-third-party-java)
          install_third_party_java_libs ;;
        install-execs)
          install_executables ;;
        install-mmcntmod)
          install_mmcntmod ;;
        install-config)
          install_config ;;
        install-xsl)
          install_xsl ;;
        install)
          install_java_libs ; install_third_party_java_libs ; install_executables ;
          install_mmcntmod ; install_config ; install_xsl ;;
        uninstall-java)
          uninstall_java_libs ;;
        uninstall-third-party-java)
          uninstall_third_party_java_libs ;;
        uninstall-execs)
          uninstall_executables ;;
        uninstall-mmcntmod)
          uninstall_mmcntmod ;;
        uninstall-config)
          uninstall_config ;;
        uninstall-xsl)
          uninstall_xsl ;;
        uninstall)
          uninstall_java_libs ; uninstall_third_party_java_libs ; uninstall_executables ;
          uninstall_mmcntmod ; uninstall_config ; uninstall_xsl ;;
        dist)
          create_dist ;;
        *)
          echo "ERROR: Unknown target: $target"
          exit 3 ;;
      esac
    done
    echo
    echo "$program_name: BUILD DONE"
    echo
  }

function print_variables
  {
    cat <<EOF
checkin_root      = $checkin_root
cvsroot           = $cvsroot
doc_dir           = $doc_dir
etc_dir           = $etc_dir
force             = $force
ignore_deps       = $ignore_deps
java_home         = $java_home
java_lib_dir      = $java_lib_dir
javac_debug       = $javac_debug
javac_deprecation = $javac_deprecation
javac_verbose     = $javac_verbose
jdk_apidocs_url   = $jdk_apidocs_url
japs_apidocs_url  = $japs_apidocs_url
lang_codes        = $lang_codes
prefix            = $prefix
release           = $release
targets           = $targets
task              = $task
var_dir           = $var_dir
EOF
  }

function show_targets
  {
cat <<EOF
java               Creates the Java libraries
config             Creates the configuration files
cmd-wrappers       Creates the command wrappers
all                Creates the Java libraries and the executables
apidocs            Creates the Java API documentation
manual             Creates the manual
mmcntmod           Creates the mmcntmod command
install-java       Installs the Java libraries
install-third-party-java
                   Installs the third-party Java libraries
install-execs      Installs the executables (except mmcntmod)
install-mmcntmod   Installs mmcntmod and auxiliary files
install-config     Installs the configuration files
install-xsl        Installs the XSL files
install            Installs all
uninstall-java     Uninstalls the Java libraries
uninstall-third-party-java
                   Uninstalls the third-party Java libraries
uninstall-execs    Uninstalls the executables (except mmcntmod)
uninstall-mmcntmod Uninstalls mmcntmod and auxiliary files
uninstall-config   Uninstalls the configuration files
uninstall-xsl      Uninstalls the XSL files
uninstall          Uninstalls all
dist               Creates a distribution
EOF
  }

function show_help
  {
    cat <<EOF
Usage:
  ./build.sh [OPTIONS] [TARGETS]
Description:
  Builds and/or installs the mmcdk package, or parts of it. What is
  actually done is controlled by TARGETS, which is a list of keywords called
  targets. Type ./build.sh -t to get a list of all targets. The default target
  is "all"; it is assumed if no targets are specified.
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
