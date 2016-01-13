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

source util/setup.sh
source util/mmjvmd_util.sh

print_header sample_documents

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

cd $test_dir/packages/mmtex_mumie/samples
echo "Changed into `pwd`"

echo "Mounting"
./mount_checkin.sh

cd $MM_CHECKIN_ROOT/samples
echo "Changed into `pwd`"

echo "Converting tex sources I: file by file"
for tex_src in `find -name "*.tex"` ; do
  mmtex -sw $tex_src
done

echo "Converting tex sources II: all files at once"
mmtex -rsw

echo "Checking references"
mmckrefs -rq

echo "Creating previews (in top-level directory only)"
mmprev

echo "Checkin"
mmckin `find -name "*.meta.xml"`

mmjvmd stop-retry

check_mmjvmd_not_running

tomcat stop
apache stop
postgres stop


