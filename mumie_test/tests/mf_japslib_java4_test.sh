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

source util/setup.sh --no-abort

source $test_dir/packages/japs_env/conf/bash/tomcat_bashrc

cocoon_lib_dir=$TOMCAT_HOME/webapps/cocoon/WEB-INF/lib
japs_lib_dir=$test_dir/packages/japs/java/lib

# Save cocoon-2.1.8.jar and mumie-japs-for-mf.jar:
cp -v $cocoon_lib_dir/cocoon-2.1.8.jar $test_dir/tmp
cp -v $japs_lib_dir/mumie-japs-for-mf.jar $test_dir/tmp

/bin/bash tests/mf_japslib_java4_test_impl.sh
exit_value=$?

# Restore cocoon-2.1.8.jar:
mv -v $test_dir/tmp/cocoon-2.1.8.jar $cocoon_lib_dir

# If test failed, restore mumie-japs-for-mf.jar:
[ "$exit_value" -eq 0 ] || mv -v $test_dir/tmp/mumie-japs-for-mf.jar $japs_lib_dir

exit $exit_value
