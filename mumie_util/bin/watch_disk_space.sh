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

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: watch_disk_space.sh,v 1.1.1.1 2009/07/03 11:48:43 rassy Exp $

threshold=90
recipients="
  rassy@math.tu-berlin.de
  seiler@math.tu-berlin.de
"
watch_interval=60
notify_interval=3600 # one hour
last_notified=0

function get_max_disk_space
  {
    local max=0
    local entry
    local in_body
    for entry in `df -l | awk '{ print substr($5, 0, index($5,"%")-1) }'` ; do
      [ "$in_body" ] && [ "$entry" -ge "$max" ] && max=$entry 
      in_body=enabled
    done
    echo $max
  }

while true ; do
  now=`date +%s`
  max=`get_max_disk_space`
  if [ "$max" -ge "$threshold" ] \
     && [ "$(( $now - $last_notified ))" -ge "$notify_interval" ] ; then
    for recipient in $recipients ; do
      echo "

WARNING: Disk space allocation on $HOST exceeds ${threshold}%
         for at least one partition.

df -l output on $HOST:

`df -l`

" | nail -s "WARNING: $HOST: Used disk space exceeds ${threshold}%" $recipient
    done
    last_notified=$now
  fi
  sleep $watch_interval
done

