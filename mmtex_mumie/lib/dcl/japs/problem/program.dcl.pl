package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: program.dcl.pl,v 1.2 2008/11/07 14:15:45 rassy Exp $

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

sub japs_setup_prb_program
  {
    log_message("\njaps_setup_prb_program 1/2\n");

    install_cmds(['title'], 'japs_content');
    install_cmds('JAPS_PPD', 'japs_problem');
    install_cmds('JAPS_DS', 'japs_problem');
    install_cmds(['prggrading', 'prgclassname'], 'japs_problem');
    install_envs
      (['prganswer', 'hidden', 'prgwrapper', 'prgevaluator', 'prgsolution'],
       'japs_problem');
    install_envs('JAPS_PPD', 'japs_problem');
    install_envs('JAPS_DS', 'japs_problem');

    log_message("\njaps_setup_prb_program 2/2\n");
  };

$dcl_table->{'japs.problem.program'}->{initializer} =  sub
  {
    log_message("\ndcl japs.problem.program initializer 1/2\n");

    require_lib('japs_core');
    require_lib('japs_metainfo');
    require_lib('japs_content');
    require_lib('japs_problem');
    require_lib('japs_media');
    require_lib('japs_link');
    japs_init_conversion('problem', 'program', \&japs_setup_prb_program);
    log_message("\ndcl japs.problem.program initializer 2/2\n");
  };

return(1);
