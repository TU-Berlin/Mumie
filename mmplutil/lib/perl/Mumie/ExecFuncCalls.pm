package Mumie::ExecFuncCalls;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: ExecFuncCalls.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

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

require Exporter;
@ISA = qw(Exporter);
@EXPORT = qw (exec_func_calls);


sub init
  {
    # Nothing to do
    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub exec_func_calls
  # (@func_call_list)
  # Calls each function in \code{@func_call_list}, with the arguments specified in
  # \code{@func_call_list}. Each element of \code{@func_call_list} must be a reference to a
  # list of the form \code{($func, @args)} where \code{$func} is a reference to the function
  # to be called, and \code{@args} are the arguments to be passed to the function.
  {
    my @func_call_list = @_;
    my $func;
    my @args;
    while ( @func_call_list )
      {
	($func, @args) = @{shift(@func_call_list)};
	&{$func}(@args);
      }
  }

init();
return(1);


