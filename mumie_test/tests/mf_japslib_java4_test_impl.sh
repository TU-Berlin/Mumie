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
source util/java_util.sh

japs_env_conf_dir=$test_dir/packages/japs_env/conf/bash
source $japs_env_conf_dir/tomcat_bashrc # for TOMCAT_HOME

print_header mf_japslib_java4

[ "$java4_home" ] || error "java4_home not set"

setjava $java4_home

# Recompile cocoon-2.1.8.jar with Java 4 and install it:
cd $test_dir/src/cocoon-2.1.8
./build.sh clean webapp
cp build/webapp/WEB-INF/lib/cocoon-2.1.8.jar $TOMCAT_HOME/webapps/cocoon/WEB-INF/lib

cd $test_dir/packages/japs

./build.sh -f japs-lib-for-mf
