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

# @Template@
# $Id: mmxsl.tpl,v 1.4 2006/10/18 13:15:07 rassy Exp $

export MMXSL_HOME=${MMXSL_HOME:-@basedir@}

export CLASSPATH=\
$MMXSL_HOME/lib/mumie-xsl.jar:\
$MMXSL_HOME/lib/mumie-util.jar:\
$CLASSPATH

trusted_certs_store=$MMXSL_HOME/certs/trusted_certs

java \
  -Djavax.net.ssl.trustStore=$trusted_certs_store \
  net.mumie.xslt.MmXSL \
  --name=mmxsl "$@"
