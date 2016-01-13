package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: selftest.dcl.pl,v 1.3 2008/06/25 11:08:14 rassy Exp $

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

sub japs_setup_worksheet_selftest
  {
    log_message("\njaps_setup_worksheet_selftest 1/2\n");

    # Default arrangement:
    $global_data->{arrange} = 'list';

    # Process document class options:
    for my $dcl_option (keys(%{$dcl_option_table}))
      {
        if ( $dcl_option eq 'list' )
          { $global_data->{arrange} = 'list'; }
        elsif ( $dcl_option eq 'tree' )
          { $global_data->{arrange} = 'tree'; }
        elsif ( $dcl_option eq 'net' )
          { $global_data->{arrange} = 'net'; }
        else
          { &{$scan_proc->{error_handler}}("Invalid documentclass option: $dcl_option"); }
      }

    install_envs('JAPS_WORKSHEET_TOPLEVEL', 'japs_course');
    install_cmds('JAPS_WORKSHEET_TOPLEVEL', 'japs_course');

    log_message("\njaps_setup_worksheet_selftest 2/2\n");
  };

$dcl_table->{'japs.worksheet.selftest'}->{initializer} =  sub
  {
    require_lib('japs_core');
    require_lib('japs_metainfo');
    require_lib('japs_content');
    require_lib('japs_course');
    japs_init_conversion
      ('worksheet', 'selftest', \&japs_setup_worksheet_selftest, undef(), TRUE);
    $scan_proc->{env_table}->{content}->{post_begin_hook} = \&japs_start_course;
    $scan_proc->{env_table}->{content}->{early_pre_end_hook} = \&japs_close_course;
  };

return(1);
