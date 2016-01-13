package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: lang.mtx.pl,v 1.3 2007/07/11 15:56:14 grudzin Exp $

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

# --------------------------------------------------------------------------------
#1                                  Description
# --------------------------------------------------------------------------------
#
# Commands/environments for multi-language support.

log_message("\nLibrary \"lang\" ", '$Revision: 1.3 $', "\n");

sub execute_lang_cmd
  {
    log_message("\nexecute_lang_cmd 1/3\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $lang = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', 'lang', 'lang_arg');

    log_message("\nexecute_lang_cmd 2/3\n");
    log_data("Req. Lang.", $lang, "Set Lang.", $scan_proc->{lang});

    if ( $scan_proc->{lang} && ( $scan_proc->{lang} eq $lang ) )
      {
        convert_arg
          (1, $man_args, $pos_man_args, 'CMD', 'lang', 'block_arg',
           FALSE, FALSE, 'SHARED_OUTPUT_LIST');
      }

    log_message("\nexecute_lang_cmd 3/3\n");
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{lang}->{initializer} = sub
  {
    # Command table for export
    my $lang_cmd_table =
      {
       'lang' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 2,
          'is_par_starter' => FALSE,
          'execute_function' => \&execute_lang_cmd,
          'post_block_arg_hook' => \&close_par_if_in_par,
          'doc' =>
            {
             'man_args' => [['lang', 'Language shortcut'], ['block', 'Block of MmTeX code']],
             'description' =>
               'Language-specific processing. ' .
               '%{2} is processed only if %{1} equals the currently set language. ' .
               'The latter can be set from the command line by means of the ' .
               '"--param" option. Some document classes allow to set it as an option, too.' .
               'The parameter value should be one of the usual language specifiers, ' .
               'i.e., "en" for English or "de" for German.'
            }
         }
      };

    my @lang_cmds = keys(%{$lang_cmd_table});
    deploy_lib
      ('lang',
       $lang_cmd_table,
       {},
       {
        'TOPLEVEL' => \@lang_cmds,
       },
       FALSE);
  };

return(TRUE);
