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

source util/setup.sh
source util/mmjvmd_util.sh

# Common prefix of all sync ids:
sync_id_prefix=synctest

# Sync ids:
class_1_sync_id=${sync_id_prefix}-class-1
tutor_1_sync_id=${sync_id_prefix}-user-tut1
tutorial_1_sync_id=${sync_id_prefix}-tutorial-1
semester_2_sync_id=${sync_id_prefix}-semester-2
class_2_sync_id=${sync_id_prefix}-class-2
tutor_2_sync_id=${sync_id_prefix}-user-tut2
tutorial_2_sync_id=${sync_id_prefix}-tutorial-2
tutor_3_sync_id=${sync_id_prefix}-user-tut3
tutorial_3_sync_id=${sync_id_prefix}-tutorial-3

# Number of students per tutorial:
num_stud=10

# Counts the number of students created so far:
count_stud=0

# Number of lecturers per class (part 2 only):
num_lect=3

# Counts the number of lecturers created so far:
count_lect=0

function get_lecturers
  {
    local first=$1
    local num=$2
    local last=$[$first + $num - 1]
    local lecturers=''
    local i=$first
    while [ $i -le $last ] ; do
      local sync_id="${sync_id_prefix}-user-lect${i}"
      if [ $lecturers ] ; then
        lecturers="${lecturers},${sync_id}"
      else
        lecturers=$sync_id
      fi
      i=$[$i + 1]
    done
    echo $lecturers
  }

function run_sync_cmd
  {
    local cmd=$1
    shift
    local result=`mmsrv -a sync protected/sync/$cmd "$@"`
    echo $result
    if echo $result | fgrep ERROR > /dev/null ; then
      return 1
    fi
  }

function new_user
  {
    echo "new-user $1"
    run_sync_cmd new-user \
      -p sync-id="$1" \
      -p login-name="$2" \
      -p password-encrypted="`mmencrypt $3`" \
      -p first-name="$4" \
      -p surname="$5"
  }

function new_tutorial
  {
    echo "new-tutorial $1"
    run_sync_cmd new-tutorial \
      -p sync-id="$1" \
      -p name="$2" \
      -p tutor="$3" \
      -p class="$4"
  }

function add_user_to_tutorial
  {
    echo "add-user-to-tutorial $1 $2"
    run_sync_cmd add-user-to-tutorial \
      -p tutorial="$1" \
      -p user="$2"
  }

function remove_user_from_tutorial
  {
    echo "remove-user-from-tutorial $1 $2"
    run_sync_cmd remove-user-from-tutorial \
      -p tutorial="$1" \
      -p user="$2"
  }

function change_user_tutorial
  {
    echo "change-user-tutorial $1 $2 $3"
    run_sync_cmd change-user-tutorial \
      -p old-tutorial="$1" \
      -p new-tutorial="$2" \
      -p user="$3"
  }

function new_semester
  {
    echo "new-semester $1"
    run_sync_cmd new-semester \
      -p sync-id="$1" \
      -p name="$2"
  }

function new_class
  {
    echo "new-class $1"
    run_sync_cmd new-class \
      -p sync-id="$1" \
      -p name="$2" \
      -p description="Test" \
      -p semester="$3" \
      -p lecturers="$4"
  }

function change_class_data
  {
    echo "change-class-data $1"
    local sync_id=$1
    shift
    run_sync_cmd change-class-data -p sync-id="$sync_id" "$@"
  }

function create_lecturers
  {
    local num=$1
    local last=$[$count_lect + $num]
    local i=$[$count_lect + 1]
    while [ $i -le $last ] ; do
      new_user ${sync_id_prefix}-user-lect${i} lecturer${i} lecturer${i} Test${i} Lecturer${i}
      i=$[$i + 1]
      count_lect=$[$count_lect + 1]
    done
  }

function create_students
  {
    local num=$1
    local tutorial_sync_id=$2
    local last=$[$count_stud + $num]
    local i=$[$count_stud + 1]
    while [ $i -le $last ] ; do
      new_user ${sync_id_prefix}-user-stud${i} student${i} student${i} Test${i} Student${i}
      count_stud=$[$count_stud + 1]
      add_user_to_tutorial $tutorial_sync_id ${sync_id_prefix}-user-stud${i}
      i=$[$i + 1]
    done
  }

function unset_lecturer
  {
    echo "unset-lecturer-for-class $1 $2"
    run_sync_cmd unset-lecturer-for-class \
      -p user="$1" \
      -p class="$2"
  }

function set_lecturer
  {
    echo "set-lecturer-for-class $1 $2"
    run_sync_cmd set-lecturer-for-class \
      -p user="$1" \
      -p class="$2"
  }

function set_tutor
  {
    echo "set-tutor-for-tutorial $1 $2"
    run_sync_cmd set-tutor-for-tutorial \
      -p user="$1" \
      -p tutorial="$2"
  }

function update_semester
  {
    echo "update-semester $1 $2 $3"
    run_sync_cmd update-semester \
      -p sync-id="$1" \
      -p name="$2"\
      -p description="$3"
  }

function update_tutorial
  {
    echo "update-tutorial $1 $2 $3 $4"
    run_sync_cmd update-tutorial  \
      -p sync-id="$1" \
      -p name="$2"\
      -p description="$3" \
      -p tutor="$4" 
  }

function update_user
  {
    echo "update-user $1 $2 $3 $4"
    run_sync_cmd update-user \
      -p sync-id="$1" \
      -p name="$2"\
      -p loginName="$3" \
      -p surname="$4" 
  }

function update_class
  {
    echo "update-class $1 $2 $3"
    run_sync_cmd update-class \
      -p sync-id="$1" \
      -p name="$2"\
      -p description="$3" 

  }
print_header sync

echo "Sourcing bash settings"
bashrc_dir=$test_dir/packages/japs_env/conf/bash
source $bashrc_dir/postgres_bashrc
source $bashrc_dir/apache_bashrc
source $bashrc_dir/tomcat_bashrc

cat <<EOF
PG_HOME         = $PG_HOME
LD_LIBRARY_PATH = $LD_LIBRARY_PATH
PGDATA          = $PGDATA
APACHE_HOME     = $APACHE_HOME
CATALINA_HOME   = $CATALINA_HOME
TOMCAT_HOME     = $TOMCAT_HOME
EOF

postgres start
apache start
tomcat start

check_mmjvmd_not_running

echo "mmjvmd --vars:"
mmjvmd --vars
mmjvmd start
mmalias default $japs_url_prefix admin
echo mumie | mmstorepass

echo "mmencrypt --encryptor-class:"
mmencrypt --encryptor-class

# Create a sync user:
cd $MM_CHECKIN_ROOT/org/users
echo "Changed into `pwd`"
echo "Creating sync user"
echo \
"<?xml version=\"1.0\" encoding=\"ASCII\"?>
<mumie:user xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">
  <mumie:login_name>sync</mumie:login_name>
  <mumie:password>sync</mumie:password>
  <mumie:first_name>Sync</mumie:first_name>
  <mumie:surname>Sync</mumie:surname>
  <mumie:description>
    Sunchronisation
  </mumie:description>
  <mumie:user_groups>
    <mumie:user_group path=\"org/user_groups/ugr_syncs.meta.xml\"/>
  </mumie:user_groups>
</mumie:user>" \
> usr_sync.meta.xml
mmckin usr_sync.meta.xml
cd $base_dir
echo "Changed into `pwd` again"

# Set the sync home of the sync user [this is a hack; it should be possible to set the
# sync home in the master file]:
echo "Setting sync home for sync user"
psql -U japs mdb01 -c \
"UPDATE users
SET sync_home = section_id_for_path(0, 'org/tub')
WHERE login_name='sync';"

# Define alias for sync account:
echo "Defining alias for sync account"
mmalias sync $japs_url_prefix sync
echo sync | mmstorepass -a sync

# ----------------------------------------------------------------------
# Part 1
# ----------------------------------------------------------------------

# Set a sync id for the class checked-in with tub_teaching:
echo "Setting sync id for class:"
psql -U japs mdb01 -c \
"UPDATE classes
SET sync_id = '$class_1_sync_id'
WHERE pure_name = 'cls_lineare_algebra_fuer_ingenieure'
AND contained_in = section_id_for_path(0, 'org/tub/ss_09/classes');"

create_lecturers $num_lect
change_class_data $class_1_sync_id -p lecturers=`get_lecturers 1 $num_lect`
new_user $tutor_1_sync_id tutor1 tutor1 Test1 Tutor1
new_tutorial $tutorial_1_sync_id "Mo 10-12 MA 100" $tutor_1_sync_id $class_1_sync_id
create_students $num_stud $tutorial_1_sync_id

# ----------------------------------------------------------------------
# Part 2
# ----------------------------------------------------------------------

new_semester $semester_2_sync_id "fs_2008"
create_lecturers $num_lect
new_class $class_2_sync_id "Analysis I" $semester_2_sync_id `get_lecturers 4 $num_lect`
new_user $tutor_2_sync_id tutor2 tutor2 Test2 Tutor2
new_user $tutor_3_sync_id tutor3 tutor3 Test3 Tutor3
new_tutorial $tutorial_2_sync_id "Mo 12-14 MA 100" $tutor_2_sync_id $class_2_sync_id
new_tutorial $tutorial_3_sync_id "Mo 14-16 MA 100" $tutor_3_sync_id $class_2_sync_id
create_students $num_stud $tutorial_2_sync_id
create_students $num_stud $tutorial_3_sync_id
remove_user_from_tutorial $tutorial_2_sync_id ${sync_id_prefix}-user-stud20
change_user_tutorial $tutorial_2_sync_id $tutorial_3_sync_id ${sync_id_prefix}-user-stud19

set_tutor $tutor_2_sync_id $tutorial_3_sync_id

update_semester $semester_2_sync_id "fs_2008" "Test Test"
update_tutorial $tutorial_3_sync_id "Mo 14-16 MA 100" "Test Tut" $tutor_3_sync_id
update_user $tutor_2_sync_id tutor2 tutor2 tutor2
update_class $class_2_sync_id "Analysis I Test" "Test"

set_lecturer ${sync_id_prefix}-user-lect1 $class_2_sync_id
unset_lecturer ${sync_id_prefix}-user-lect1 $class_2_sync_id




mmjvmd stop-retry

check_mmjvmd_not_running

tomcat stop
apache stop
postgres stop
