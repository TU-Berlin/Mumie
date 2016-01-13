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

print_header recheckin

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

mmjvmd --vars
mmjvmd start
mmalias default $japs_url_prefix admin
echo mumie | mmstorepass

cd $MM_CHECKIN_ROOT/org/users
echo "Current directory: `pwd`"
echo \
"<?xml version=\"1.0\" encoding=\"ASCII\"?>
<mumie:user xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">
  <mumie:login_name>guest</mumie:login_name>
  <mumie:password>guest</mumie:password>
  <mumie:first_name>Gast</mumie:first_name>
  <mumie:surname>Gast</mumie:surname>
  <mumie:email>guest@guest.org</mumie:email>
  <mumie:description>
    &#214;ffentlicher Zugang zur MUMIE. [RECHECKIN]
  </mumie:description>
  <mumie:user_groups>
    <mumie:user_group path=\"org/user_groups/ugr_guests.meta.xml\"/>
  </mumie:user_groups>
</mumie:user>
" > usr_guest.meta.xml
mmckin usr_guest.meta.xml

cd $MM_CHECKIN_ROOT/org/user_groups
echo "Current directory: `pwd`"
echo \
"<?xml version=\"1.0\" encoding=\"ASCII\"?>
<mumie:user_group xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">
  <mumie:name>guests</mumie:name>
  <mumie:description>
    &#214;ffentlicher Zugang zur MUMIE. [RECHECKIN]
  </mumie:description>
</mumie:user_group>
" > ugr_guests.meta.xml
mmckin ugr_guests.meta.xml

cd $MM_CHECKIN_ROOT/org/tub/ws_07_08
echo "Current directory: `pwd`"
echo \
"<?xml version=\"1.0\" encoding=\"ASCII\"?>
<mumie:semester xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">
  <mumie:name>Wintersemester 2007/2008</mumie:name>
  <mumie:description>
    Wintersemester 2007/2008 [RECHECKIN]
  </mumie:description>
</mumie:semester>
" > sem_ws_07_08.meta.xml
mmckin sem_ws_07_08.meta.xml

cd $MM_CHECKIN_ROOT/org/tub/ws_07_08/classes
echo "Current directory: `pwd`"
echo \
"<?xml version=\"1.0\" encoding=\"ASCII\"?>
<mumie:class xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">
  <mumie:name>0230 L 002 Lineare Algebra f&#252;r Ingenieure</mumie:name>
  <mumie:description>
    Vektoren, Lineare Abbildungen, Lineare Gleichungen, Vektorgeometrie, Matrizenrechnung,
    Lineare Differentialgleichungen, Theorie und Anwendungen auf Probleme der
    Ingenieurwissenschaften. [RECHECKIN]
  </mumie:description>
  <mumie:semester path=\"org/tub/ws_07_08/sem_ws_07_08.meta.xml\">
    <mumie:name>Wintersemester 2007/2008</mumie:name>
  </mumie:semester>
</mumie:class>
" > cls_lineare_algebra_fuer_ingenieure.meta.xml
mmckin cls_lineare_algebra_fuer_ingenieure.meta.xml

cd $MM_CHECKIN_ROOT/system/common
echo "Current directory: `pwd`"
echo \
"<?xml version=\"1.0\" encoding=\"ASCII\"?>
<mumie:generic_css_stylesheet xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">
  <mumie:name>Basis-Styles</mumie:name>
  <mumie:description>
    Basis-Styles f&#252;r alle Seiten des Default-Themes [RECHECKIN]
  </mumie:description>
</mumie:generic_css_stylesheet>
" > g_css_base.meta.xml
mmckin g_css_base.meta.xml

mmjvmd stop-retry

check_mmjvmd_not_running

tomcat stop
apache stop
postgres stop
