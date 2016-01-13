# Author: Tilman Rassy  <rassy@math.tu-berlin.de>
# $Id: japs_link.mtx.pl,v 1.3 2007/12/19 14:02:25 rassy Exp $

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

log_message("\nLibrary japs_link ", '$Revision: 1.3 $ ', "\n");

# ---------------------------------------------------------------
# Command \ehref
# ---------------------------------------------------------------

sub japs_execute_ehref
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_ehref 1/2\n");
    my $attribs = {};
    $attribs->{href} = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    $attribs->{target} = get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD')
      if $opt_args->[0];
    start_xml_element('elink', $attribs, "INLINE");
    convert_arg
      (1, $man_args, $pos_man_args, 'CMD', 'ehref', 'arg', FALSE, FALSE, 'SHARED_OUTPUT_LIST');
    close_xml_element('elink', 'INLINE');
    log_message("\njaps_execute_ehref 2/2\n");
  }

# ---------------------------------------------------------------
# Command \href
# ---------------------------------------------------------------

sub japs_execute_href
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_href 1/3\n");
    my $lid = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    log_message("\njaps_execute_href 2/3\n");
    log_data('lid', $lid);
    japs_check_lid($lid);
    push(@{$global_data->{used_lids}}, $lid);
    my $attribs = {};
    $attribs->{lid} = $lid;
    $attribs->{target} = get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD')
      if $opt_args->[0];
    start_xml_element('link', $attribs, "INLINE");
    convert_arg
      (1, $man_args, $pos_man_args, 'CMD', 'href', 'arg', FALSE, FALSE, 'SHARED_OUTPUT_LIST');
    close_xml_element('link', 'INLINE');
    log_message("\njaps_execute_href 3/3\n");
  }

# --------------------------------------------------------------------------------
#1 Initializing
# --------------------------------------------------------------------------------

$lib_table->{japs_link}->{initializer} = sub
  {
    my $japs_link_cmd_table =
      {
       'ehref' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 2,
          'is_par_starter' => TRUE,
          'execute_function' => \&japs_execute_ehref,
          'doc' =>
            {
             'man_args' => [['href', 'The URL of the href'],
                            ['text', 'The text of the href']],
             'opt_args' => [['target', 'Target keyword (_blank, _parent,...)']],
             'description' =>
               'Creates a href to an external page.',
            },
         },
       'href' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 2,
          'is_par_starter' => TRUE,
          'execute_function' => \&japs_execute_href,
          'doc' =>
            {
             'man_args' => [['lid', 'The local id of the linked document'],
                            ['text', 'The text of the link']],
             'opt_args' => [['target', 'Target keyword (_blank, _parent,...)']],
             'description' =>
               'Creates a link to a Mumie document.',
            },
         },
      };

    my @all_cmds = keys(%{$japs_link_cmd_table});
    my @toplevel_cmds = @all_cmds;

    deploy_lib
      (
       'japs_link',
       $japs_link_cmd_table,
       {},
       {'JAPS_LINK_TOPLEVEL' => \@toplevel_cmds, 'JAPS_LINK' => \@all_cmds},
       FALSE
      );
  };


return(TRUE);
