package Mumie::MmTeX::Converter;

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

#
#################################################################

$dcl_table->{'japs.subelement.deduction'}->{initializer} =  sub
  {
    my ($error_handler) = @_;

    # ---------------------------------------------------------------
    # Global variables
    # ---------------------------------------------------------------

    $DOCUMENT_TYPE = 'subelement';
    $CATEGORY = 'deduction';

    # ---------------------------------------------------------------
    # Required Libraries
    # ---------------------------------------------------------------

    require_lib("metainfo");
    require_lib("general");
    require_lib("lang");

    $scan_proc->{cmd_table} = {};
    $scan_proc->{env_table} = {};

    install_cmds(["title", "step"], "general");
    install_envs(["content", "steps", "suppositions"], "general");

    install_cmds("ALL", "lang");
    install_cmds("ALL", "metainfo");
    install_envs("ALL", "metainfo");

    # ---------------------------------------------------------------
    # Initializing
    # ---------------------------------------------------------------

    $scan_proc->{post_documentclass_hook} = \&mumie_start_subelement;
    $scan_proc->{post_scan_hook} = \&mumie_stop_subelement;

    $scan_proc->{metainfo_general}->{subtype} = "deduction";
  };
return(1);
