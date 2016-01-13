package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: japs_summary.mtx.pl,v 1.6 2008/03/17 16:39:25 rassy Exp $

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

log_message("\nLibrary \"japs_summary\" ", '$Revision: 1.6 $ ', "\n");

# ------------------------------------------------------------------------------------------
# Description
# ------------------------------------------------------------------------------------------
#
# Code for creating the content of summaries.

# ------------------------------------------------------------------------------------------
# Environments 'ifpoints' and 'ifscore'
# ------------------------------------------------------------------------------------------

sub japs_begin_ifpoints
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_ifpoints 1/1\n");
    my $range = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    ( $range =~ m/^\s*([0-9]+(?:\.[0-9])?)\s*,?\s*([0-9]+(?:\.[0-9])?|max)\s*$/ )
      or &{$scan_proc->{error_handler}}("Invalid point range: $range");
    my $attribs = {min => $1, max => $2};
    japs_start_content_xml_element("ifpoints", $attribs, "DISPLAY");
    log_message("\njaps_begin_ifpoints 2/2\n");
    log_data('Min', $attribs->{min}, 'Max', $attribs->{max});
  }

sub japs_end_ifpoints
  {
    log_message("\njaps_end_ifpoints 1/2\n");
    japs_close_content_xml_element("ifpoints", "DISPLAY");
    log_message("\njaps_end_ifpoints 2/2\n");
  }

sub japs_begin_ifscore
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_ifscore 1/2\n");
    my $range = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    my $num = '[0-9]+\\.[0-9]+|[0-9]+|[0-9]+\\.|\\.[0-9]+';
    ( $range =~ m/^\s*($num)\s*,?\s*($num)\s*$/ )
      or &{$scan_proc->{error_handler}}("Invalid score range: $range");
    my ($max, $min) = ($1, $2);
    ( 0 <= $min && $min <= 1 )
      or &{$scan_proc->{error_handler}}("Invalid score value: $min (must be between 0 and 1");
    ( 0 <= $max && $max <= 1 )
      or &{$scan_proc->{error_handler}}("Invalid score value: $max (must be between 0 and 1");
    my $attribs = {min => $min, max => $max};
    japs_start_content_xml_element("ifscore", $attribs, "DISPLAY");
    log_message("\njaps_begin_ifscore 2/2\n");
    log_data('Min', $min, 'Max', $max);
  }

sub japs_end_ifscore
  {
    log_message("\njaps_end_ifscore 1/2\n");
    # Close "ifscore"  element:
    japs_close_content_xml_element("ifscore", "DISPLAY");
    log_message("\njaps_end_ifscore 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Environment 'casepoints' and the '\uptopoints' command
# ------------------------------------------------------------------------------------------

sub japs_start_uptopoints
  {
    log_message("\njaps_start_uptopoints 1/2\n");
    my ($max) = @_;
    my $min = $scan_proc->{casepoints_min};
    log_data('Min', $min, 'Max', $max);
    $scan_proc->{casepoints_min} = ($max eq 'max' ? 'max' : $max + 0.1);
    $scan_proc->{casepoints_item_number}++;
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = '_uptopoints';
    my $attribs = {min => $min, max => $max};
    japs_start_content_xml_element("ifpoints", $attribs, "DISPLAY");
    log_message("\njaps_start_uptopoints 2/2\n");
  }

sub japs_close_uptopoints_if_necessary
  {
    log_message("\njaps_close_uptopoints_if_necessary 1/2\n");
    if ( $scan_proc->{casepoints_item_number} == 0 )
      {
	# Allow output:
	unlock_output_list();
	disallow_tokens(["ign_whitesp"]);
      }
    elsif ( $scan_proc->{casepoints_item_number} > 0 )
      {
	close_par_if_in_par();
        if ( $scan_proc->{current_env} ne "_uptopoints" )
          {
            &{$scan_proc->{error_handler}}
              (appr_env_notation($scan_proc->{current_env}),
               ' must be opened and closed within same casepoints item.');
          }
        japs_close_content_xml_element("ifpoints", "DISPLAY");
        reset_scan_proc();
      }
    else
      {
	notify_programming_error
	  ('japs_close_uptopoints_if_necessary', ' Invalid item number value: ',
           $scan_proc->{casepoints_item_number});
      }
    log_message("\njaps_close_uptopoints_if_necessary 2/2\n");
  }

sub japs_execute_uptopoints
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_uptopoints 1/2\n");
    japs_close_uptopoints_if_necessary();
    my $max = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    ( $max =~ m/^\s*([0-9]+(?:\.[0-9])?|max)\s*$/ )
      or &{$scan_proc->{error_handler}}("Invalid point value: $max");
    japs_start_uptopoints($max);
    log_message("\njaps_execute_uptopoints 2/2\n");
  }

sub japs_begin_casepoints
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_casepoints 1/2\n");
    install_cmds(['uptopoints'], 'japs_summary');
    allow_tokens(['ign_whitesp'], 'PROTECT', 'PREPEND');
    $scan_proc->{casepoints_item_number} = 0;
    $scan_proc->{casepoints_min} = 0;
    lock_output_list();
    $scan_proc->{locked_output_list_error_msg}
      = "No text allowed before the first \\uptopoints.";
    log_message("\njaps_begin_casepoints 2/2\n");
  }

sub japs_end_casepoints
  {
    log_message("\njaps_end_casepoints 1/1\n");
  }

# ------------------------------------------------------------------------------------------
# Environment 'iftimeframe'
# ------------------------------------------------------------------------------------------

sub japs_begin_iftimeframe
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_iftimeframe 1/1\n");
    my $value = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    grep($value eq $_, 'before', 'inside', 'after')
      or &{$scan_proc->{error_handler}}("Invalid timeframe relation: $value");
    my $attribs = {relation => $value};
    japs_start_content_xml_element("iftimeframe", $attribs, "DISPLAY");
    log_message("\njaps_begin_iftimeframe 2/2\n");
  }

sub japs_end_iftimeframe
  {
    log_message("\njaps_end_iftimeframe 1/2\n");
    japs_close_content_xml_element("iftimeframe", "DISPLAY");
    log_message("\njaps_end_iftimeframe 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Environment 'ifstate'
# ------------------------------------------------------------------------------------------

sub japs_begin_ifstate
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_ifstate 1/1\n");
    my $value = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    grep($value eq $_, 'work', 'feedback')
      or &{$scan_proc->{error_handler}}("Invalid state: $value");
    my $attribs = {state => $value};
    japs_start_content_xml_element("ifstate", $attribs, "DISPLAY");
    log_message("\njaps_begin_ifstate 2/2\n");
  }

sub japs_end_ifstate
  {
    log_message("\njaps_end_ifstate 1/2\n");
    japs_close_content_xml_element("ifstate", "DISPLAY");
    log_message("\njaps_end_ifstate 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{japs_summary}->{initializer} = sub
  {
    my $cmd_table =
      {
       'uptopoints' =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&japs_execute_uptopoints,
         }
      };

    my $env_table =
      {
       'ifpoints' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'begin_function' => \&japs_begin_ifpoints,
          'end_function' => \&japs_end_ifpoints,
          'early_pre_end_hook' => \&close_par_if_in_par,
          'doc' =>
            {
             'man_args' =>
               [
                ['range', 'Point range'],
               ],
             'description' =>
               'The body of the environment is displayed if, and only if, the ' .
               'achieved number of points is in the specified range. %{1} must be ' .
               'of the form \'P1..P2\' where P1 and P2 are integral numbers',
             'see' =>
               {'envs' => ['ifscore']},
            }
         },
       'ifscore' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'begin_function' => \&japs_begin_ifscore,
          'end_function' => \&japs_end_ifscore,
          'early_pre_end_hook' => \&close_par_if_in_par,
          'doc' =>
            {
             'man_args' =>
               [
                ['range', 'Point range'],
               ],
             'description' =>
               'The body of the environment is displayed if, and only if, the ' .
               'achieved score is in the specified range. %{1} must be ' .
               'of the form \'X1..X2\' where X1 and X2 are real numbers between 0 and 1.',
             'see' =>
               {'envs' => ['ifpoints']},
            }
         },
       'casepoints' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'begin_function' => \&japs_begin_casepoints,
          'end_function' => \&japs_end_casepoints,
          'early_pre_end_hook' => \&japs_close_uptopoints_if_necessary,
         },
       'iftimeframe' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'begin_function' => \&japs_begin_iftimeframe,
          'end_function' => \&japs_end_iftimeframe,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'ifstate' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'begin_function' => \&japs_begin_ifstate,
          'end_function' => \&japs_end_ifstate,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
      };

    my @toplevel_cmds =
      ();

    my @toplevel_envs =
      (
       'ifpoints',
       'ifscore',
       'casepoints',
       'iftimeframe',
       'ifstate'
      );

    deploy_lib
      ('japs_summary',
       $cmd_table,
       $env_table,
       {'JAPS_SUMMARY_TOPLEVEL' => \@toplevel_cmds},
       {'JAPS_SUMMARY_TOPLEVEL' => \@toplevel_envs},
     );
  };

return(TRUE);
