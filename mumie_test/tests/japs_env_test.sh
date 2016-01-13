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

print_header "japs_env"

test_conf_file=$base_dir/japs_env_build.conf

cd $test_dir/packages
rm -rf japs_env
cvs -d $cvs_root checkout $cvs_checkout_opts $cvs_checkout_opts_japs_env japs_env
[ -e  $test_conf_file ] && cp $test_conf_file japs_env/build.conf
cd japs_env

echo "
build_dir=$test_dir/src
inst_dir=$test_dir/srv
bin_dir=$test_dir/bin
" > build.conf

[ "$tomcat_heap_space" ] && echo "
tomcat_heap_space=$tomcat_heap_space
" >> build.conf

./build.sh clear-install
./build.sh clear-build
./build.sh
