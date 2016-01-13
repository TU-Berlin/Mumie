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

print_header update_japs_checkin_compl

mathletfactory_lib_files="
  jar_jreality.content.jar
  jar_jreality.meta.xml
  jar_mathletfactory_base.content.jar
  jar_mathletfactory_base.meta.xml
  jar_mathlet_factory.content.jar
  jar_mathletfactory_graphics2d.content.jar
  jar_mathletfactory_graphics2d.meta.xml
  jar_mathletfactory_java3d.content.jar
  jar_mathletfactory_java3d.meta.xml
  jar_mathletfactory_jreality.content.jar
  jar_mathletfactory_jreality.meta.xml
  jar_mathlet_factory.meta.xml
"

japs_client_files="
  jar_japs_client.content.jar
  jar_japs_client.meta.xml
"

japs_datasheet_files="
  jar_datasheet.content.jar
  jar_datasheet.meta.xml
"

lib_path=system/libraries

cd $test_dir/packages

for f in $mathletfactory_lib_files ; do
  cp -v mathletfactory_lib/checkin/$lib_path/$f japs_checkin/checkin_compl/$lib_path
done

for f in $japs_client_files ; do
  cp -v japs_client/checkin/$lib_path/$f japs_checkin/checkin_compl/$lib_path
done

for f in $japs_datasheet_files ; do
  cp -v japs_datasheet/checkin/$lib_path/$f japs_checkin/checkin_compl/$lib_path
done

if [ "$auto_commit" ] ; then
  cd japs_checkin/checkin_compl/$lib_path
  cvs commit -m 'Updated by automated test' $mathletfactory_lib_files $japs_client_files $japs_datasheet_files
fi





