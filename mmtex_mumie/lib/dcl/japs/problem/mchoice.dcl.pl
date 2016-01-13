package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: mchoice.dcl.pl,v 1.10 2008/03/17 16:39:25 rassy Exp $

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

sub japs_setup_prb_mchoice
  {
    log_message("\njaps_setup_prb_mchoice 1/2\n");

    install_cmds(['title'], 'japs_content');
    install_cmds('JAPS_PPD', 'japs_problem');
    install_cmds('JAPS_DS', 'japs_problem');
    install_envs(['choices', 'hidden'], 'japs_problem');
    install_envs('JAPS_PPD', 'japs_problem');
    install_envs('JAPS_DS', 'japs_problem');

    log_message("\njaps_setup_prb_mchoice 2/2\n");
  };

sub japs_cleanup_prb_mchoice
  {
    log_message("\njaps_cleanup_prb_mchoice 1/2\n");
    japs_mchoice_to_dsx();
    log_message("\njaps_cleanup_prb_mchoice 2/2\n");
  }

$dcl_table->{'japs.problem.mchoice'}->{initializer} =  sub
  {
    log_message("\ndcl japs.problem.mchoice initializer 1/2\n");

    require_lib('japs_core');
    require_lib('japs_metainfo');
    require_lib('japs_content');
    require_lib('japs_problem');
    require_lib('japs_media');
    require_lib('japs_link');
    japs_init_conversion
      ('problem', 'mchoice', \&japs_setup_prb_mchoice, \&japs_cleanup_prb_mchoice);
    log_message("\ndcl japs.problem.mchoice initializer 2/2\n");
  };

return(1);
