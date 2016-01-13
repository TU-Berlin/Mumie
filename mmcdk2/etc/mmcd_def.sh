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

function mmcd
  {
    # Old directory:
    local old_dir=`pwd`

    # The shortcut:
    local shortcut=$1

    # Split shortcut into parts:
    if echo $shortcut | egrep '[._,]' > /dev/null ; then
      local parts=`echo $shortcut | tr ._, '   '`
    else
     local parts=`echo $shortcut | sed -n 's/\(.\)/\1 /gp'`
    fi

    # Get directory:
    local checkin_root=${MM_CHECKIN_ROOT:-$HOME/mumie/checkin}
    local start_path=${MMCD_START_PATH:-content/lineare_algebra}
    cd $checkin_root/$start_path
    local path_abs=`pwd`
    local number=''
    local part
    for part in $parts ; do
      number="${number}${part}_"
      local dir=`ls | egrep "^${number}"`
      local count=`echo $dir | wc -w`
      if [ $count -eq 0 ] ; then
        echo "No directory found for shortcut" 1>&2
        cd $old_dir
        exit 1
      elif [ $count -gt 1 ] ; then
        echo "Ambiguous shortcut" 1>&2
        cd $old_dir
        exit 2
      fi
      cd $dir
      path_abs="${path_abs}/${dir}"
    done
  }