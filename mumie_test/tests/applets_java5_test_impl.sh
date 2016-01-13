#!/bin/bash

source util/setup.sh
source util/mmjvmd_util.sh

print_header applets_java5

check_mmjvmd_not_running

[ "$java5_home" ] || error "java5_home not set"

# Create scripts javac5.sh, jar5.sh, jarsigner5.sh:
echo "Creating javac5.sh, jar5.sh, jarsigner5.sh"

cd $test_dir/bin
echo "Current directory: `pwd`"

echo "
#!/bin/bash

export JAVA_HOME=$java5_home
export JAVA_BINDIR=\${JAVA_HOME}/bin
export JAVA_ROOT=''

\${JAVA_HOME}/bin/javac \"\$@\"
" > javac5.sh

echo "
#!/bin/bash

export JAVA_HOME=$java5_home
export JAVA_BINDIR=\${JAVA_HOME}/bin
export JAVA_ROOT=''

\${JAVA_HOME}/bin/jar \"\$@\"
" > jar5.sh

echo "
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

export JAVA_HOME=$java5_home
export JAVA_BINDIR=\${JAVA_HOME}/bin
export JAVA_ROOT=''

\${JAVA_HOME}/bin/jarsigner \"\$@\"
" > jarsigner5.sh

chmod a+x javac5.sh jar5.sh jarsigner5.sh

# Start mmjvmd:
mmjvmd --vars
mmjvmd start

# Set properties to use javac5.sh, jar5.sh, jarsigner5.sh:
echo "Setting properties to use javac5.sh, jar5.sh, jarsigner5.sh"
mmjvmc setprop net.mumie.cdk.javacCmd=$test_dir/bin/javac5.sh
mmjvmc setprop net.mumie.cdk.jarCmd=$test_dir/bin/jar5.sh
mmjvmc setprop net.mumie.cdk.jarsignerCmd=$test_dir/bin/jarsigner5.sh

cd $MM_CHECKIN_ROOT/content

fields="*"

if [ "$fields" ] ; then
  for field in $fields ; do
    applet_dir=$MM_CHECKIN_ROOT/content/$field/media/applets
    if [ -e "$applet_dir" ] ; then
      cd "$applet_dir"
      echo "Current directory: `pwd`"
      mmjava -f `find -name *.src.java`
    fi
  done
fi

mmjvmd stop-retry

check_mmjvmd_not_running
