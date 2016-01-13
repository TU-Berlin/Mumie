#!/bin/bash

source util/setup.sh
source util/mmjvmd_util.sh

print_header applets_java4

check_mmjvmd_not_running

[ "$java4_home" ] || error "java4_home not set"

# Create scripts javac4.sh, jar4.sh, jarsigner4.sh:
echo "Creating javac4.sh, jar4.sh, jarsigner4.sh"

cd $test_dir/bin
echo "Current directory: `pwd`"

echo "
#!/bin/bash

export JAVA_HOME=$java4_home
export JAVA_BINDIR=\${JAVA_HOME}/bin
export JAVA_ROOT=''

\${JAVA_HOME}/bin/javac \"\$@\"
" > javac4.sh

echo "
#!/bin/bash

export JAVA_HOME=$java4_home
export JAVA_BINDIR=\${JAVA_HOME}/bin
export JAVA_ROOT=''

\${JAVA_HOME}/bin/jar \"\$@\"
" > jar4.sh

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

export JAVA_HOME=$java4_home
export JAVA_BINDIR=\${JAVA_HOME}/bin
export JAVA_ROOT=''

\${JAVA_HOME}/bin/jarsigner \"\$@\"
" > jarsigner4.sh

chmod a+x javac4.sh jar4.sh jarsigner4.sh

# Start mmjvmd:
mmjvmd --vars
mmjvmd start

# Set properties to use javac4.sh, jar4.sh, jarsigner4.sh:
echo "Setting properties to use javac4.sh, jar4.sh, jarsigner4.sh"
mmjvmc setprop net.mumie.cdk.javacCmd=$test_dir/bin/javac4.sh
mmjvmc setprop net.mumie.cdk.jarCmd=$test_dir/bin/jar4.sh
mmjvmc setprop net.mumie.cdk.jarsignerCmd=$test_dir/bin/jarsigner4.sh

# Build applets:
for field in analysis applied lineare_algebra ; do
  cd $MM_CHECKIN_ROOT/content/$field/media/applets
  echo "Current directory: `pwd`"
  mmjava -f `find -name *.src.java`
done

mmjvmd stop-retry

check_mmjvmd_not_running
