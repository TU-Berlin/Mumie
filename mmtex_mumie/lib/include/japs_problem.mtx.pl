package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: japs_problem.mtx.pl,v 1.31 2009/10/16 14:38:27 rassy Exp $

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

log_message("\nLibrary \"japs_problem\" ", '$Revision: 1.31 $ ', "\n");

# ------------------------------------------------------------------------------------------
#1 Description
# ------------------------------------------------------------------------------------------
#
# Code for creating the content of problems.

# ------------------------------------------------------------------------------------------
# Creating DSX elements
# ------------------------------------------------------------------------------------------

sub japs_start_dsx_element
  {
    my ($name, $attribs, $layout) = @_;
    $name = $params{dsx_xml_namespace_prefix} . ':' . $name;
    start_xml_element($name, $attribs, $layout);
  }

sub japs_close_dsx_element
  {
    my ($name, $layout) = @_;
    $name = $params{dsx_xml_namespace_prefix} . ':' . $name;
    close_xml_element($name, $layout);
  }

sub japs_empty_dsx_element
  {
    my ($name, $attribs, $layout) = @_;
    $name = $params{dsx_xml_namespace_prefix} . ':' . $name;
    empty_xml_element($name, $attribs, $layout);
  }

# ------------------------------------------------------------------------------------------
# Creating PPD elements
# ------------------------------------------------------------------------------------------

sub japs_start_ppd_element
  {
    my ($name, $attribs, $layout) = @_;
    $name = $params{ppd_xml_namespace_prefix} . ':' . $name;
    start_xml_element($name, $attribs, $layout);
  }

sub japs_close_ppd_element
  {
    my ($name, $layout) = @_;
    $name = $params{ppd_xml_namespace_prefix} . ':' . $name;
    close_xml_element($name, $layout);
  }

sub japs_empty_ppd_element
  {
    my ($name, $attribs, $layout) = @_;
    $name = $params{ppd_xml_namespace_prefix} . ':' . $name;
    empty_xml_element($name, $attribs, $layout);
  }

# ------------------------------------------------------------------------------------------
#1 The "data" and "data*" environments and commands
# ------------------------------------------------------------------------------------------

#Ds
#a ($path)
# Auxiliary function. Checks if $path meets the specification of datasheet paths.

sub japs_check_ds_path
  {
    my $path = $_[0];
    foreach my $name (split('/', $path))
      {
        if ( $name !~ m/^[a-zA-Z][a-zA-Z0-9_.-]*$/ )
          {
            &{$scan_proc->{error_handler}}("Invalid datasheet path: $path");
          }
      }
  }

#Ds
#a ($path)
# Auxiliary function. $path should be a datasheet path. Does the following:
#L
# - Checks if $path meets the specification of datasheet paths
# - Checks if $path was not used before
# - Registers $path as a used path
#/L

sub japs_register_ds_path
  {
    my $path = $_[0];

    japs_check_ds_path($path);

    if ( grep($_ eq $path, @{$global_data->{ds_paths}}) )
      {
        &{$scan_proc->{error_handler}}("Datasheet path used multiple times: $path");
      }
    push(@{$global_data->{ds_paths}}, $path);
  }

#Ds
#a ($path)
# Auxiliary function. $path should be a datasheet path. Does the following:
#L
# - Checks if $path meets the specification of datasheet paths
# - Registeds $path as a referenced path
#/L

sub japs_register_ds_path_ref
  {
    my $path = $_[0];
    japs_check_ds_path($path);
    push(@{$global_data->{ds_path_refs}}, $path);
  }

#Ds
#a ()
# Checks if all referenced datasheet paths occur in the document. Prints a warning if
# not.

sub japs_check_ds_path_refs
  {
    foreach my $path (@{$global_data->{ds_path_refs}})
      {
        if ( !grep($path eq $_, @{$global_data->{ds_paths}}) )
          {
            &{$scan_proc->{warning_handler}}
              ("Datasheet path referenced but not used: $path");
          }
      }
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $clickable)
# Execute function for the '\data' and '\data*' commands. For the latter, $clickable has
# to be set to a true value. For the former, $clickable has to be set to a false value.

sub japs_execute_data
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $clickable) = @_;
    log_message("\njaps_execute_data 1/3\n");

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    log_message("\njaps_execute_data 2/3\n");
    log_data('Path', $path);
    japs_register_ds_path($path);

    if ( $scan_proc->{mode} eq "MATH" )
      {
        my $node = new_math_node();
        $node->{type} = 'ds_data';
        $node->{path} = $path;
        $node->{clickable} = $clickable;
        $node->{value} = [convert_math_as_block(\($man_args->[1]), $pos_man_args->[1])];
        append_math_node($node);
      }
    else
      {
        my $attribs = {path => $path};
        $attribs->{clickable} = 'yes' if ( $clickable );
        japs_start_dsx_element('data', $attribs, 'DISPLAY');
        convert_arg
          (1, $man_args, $pos_man_args, 'CMD',
           sub { $scan_proc->{par_enabled} = FALSE },
           undef(),
           FALSE, FALSE, 'SHARED_OUTPUT_LIST');
        japs_close_dsx_element('data', 'DISPLAY');
      }

    log_message("\njaps_execute_data 3/3\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $clickable)
# Begin function of the 'data' and 'data*' environments. For the latter, $clickable has
# to be set to a true value. For the former, $clickable has to be set to a false value.

sub japs_begin_data
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $clickable) = @_;
    log_message("\njaps_begin_data 1/3\n");

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    log_message("\njaps_begin_data 2/3\n");
    log_data('Path', $path);
    japs_register_ds_path($path);

    if ($scan_proc->{mode} eq "MATH")
      {
        new_scan_proc('COPY', "SHARED_NAMESPACE");
	$scan_proc->{ds_path} = $path;
        $scan_proc->{ds_clickable} = $clickable;
        $scan_proc->{math_node_list} = [];
      }
    else
      {
        my $attribs = {path => $path};
        $attribs->{clickable} = 'yes' if ( $clickable );
        japs_start_dsx_element('data', $attribs, 'DISPLAY');
      }

    log_message("begin_data 3/3\n");
  }

#Ds
#a ()
# End function of the 'data' and 'data*' environments.

sub japs_end_data
  {
    log_message("\njaps_end_data 1/2\n");

    if ($scan_proc->{mode} eq "MATH")
      {
        my $node = new_math_node();
        $node->{type} = 'ds_data';
        $node->{path} = $scan_proc->{ds_path};
        $node->{clickable} = $scan_proc->{ds_clickable};
        $node->{value} = $scan_proc->{math_node_list};
        reset_scan_proc();
        append_math_node($node);
      }
    else
      {
        japs_close_dsx_element('data', 'DISPLAY');
      }

    log_message("\njaps_end_data 2/2\n");
  }

sub japs_begin_verbdata
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nbegin_verbdata 1/3\n");

    ($scan_proc->{mode} eq "MATH") &&
      &{$scan_proc->{error_handler}}('Environment "verbdata" cannot be used in math mode');

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    log_message("\njaps_begin_verbdata 2/3\n");
    log_data('Path', $path);
    japs_register_ds_path($path);

    japs_start_dsx_element('data', {path => $path}, 'SEMI_DISPLAY');
    start_verbatim_body('verbdata');

    log_message("begin_verbdata 3/3\n");
  }

sub japs_end_verbdata
  {
    log_message("\nend_verbdata\n");
    japs_close_dsx_element('data', 'SEMI_DISPLAY');
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "ds_data".

sub japs_handle_ds_data_value
  {
    log_message("\njaps_handle_ds_data_value (1/2)\n");
    my $node = $_[0];
    my $attribs = {path => $node->{path}};
    $attribs->{clickable} = 'yes' if ( $node->{clickable} );
    japs_start_dsx_element('data', $attribs, 'DISPLAY');
    handle_math_node_list($node->{value});
    japs_close_dsx_element('data', 'DISPLAY');
  }

# ------------------------------------------------------------------------------------------
#1 The "\ppdxxx" commands
# ------------------------------------------------------------------------------------------

#Ds
#a ($arg)
# Auxiliary method for PPD random number generating mands. Processes the optional argument
# which specifies whether the number must be non-zero. Returns either "yes" or "no".

sub japs_process_nonzero_arg
  {
    my $arg = $_[0];
    if ( !grep($arg eq $_, 'z', 'Z') )
      {
        &{$scan_proc->{error_handler}}("Invalid zero/nonzero flag: $arg");
      }
    return($arg eq 'Z' ? 'yes' : 'no');
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\ppdrandint' command.

sub japs_execute_ppdrandint
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("japs_execute_ppdrandint 1/2\n");

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_register_ds_path($path);

    my $min = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD');
    my $max = get_data_from_arg(2, $man_args, $pos_man_args, 'CMD');

    my $non_zero = 'no';
    if ( $opt_args->[0] )
      {
        $non_zero =
          japs_process_nonzero_arg(get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD'));
      }

    if ($scan_proc->{mode} eq "MATH")
      {
        my $node = new_math_node();
        $node->{type} = 'ppd_randint';
        $node->{value} =
          {
           path => $path,
           min => $min,
           max => $max,
           non_zero => $non_zero,
          };
        append_math_node($node);
      }
    else
      {
	&{$scan_proc->{error_handler}}("Command only allowed in math mode");
      }

    log_message("japs_execute_ppdrandint 2/2\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "ppd_randint".

sub japs_handle_ppd_randint_value
  {
    log_message("\njaps_handle_ppd_randint_value (1/2)\n");
    my $node = $_[0];
    japs_empty_ppd_element('random_integer', $node->{value}, 'INLINE');
    log_message("\njaps_handle_ppd_randint_value (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\ppdrandrat' command.

sub japs_execute_ppdrandrat
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("japs_execute_ppdrandrat 1/2\n");

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_register_ds_path($path);

    my $num_min = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD');
    my $num_max = get_data_from_arg(2, $man_args, $pos_man_args, 'CMD');
    my $den_min = get_data_from_arg(3, $man_args, $pos_man_args, 'CMD');
    my $den_max = get_data_from_arg(4, $man_args, $pos_man_args, 'CMD');

    my $non_zero = 'no';
    if ( $opt_args->[0] )
      {
        $non_zero =
          japs_process_nonzero_arg(get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD'));
      }

    my $reduce = 'yes';
    if ( $opt_args->[1] )
      {
        my $arg = get_data_from_arg(1, $opt_args, $pos_opt_args, 'CMD');
        if ( !grep($arg eq $_, 'r', 'R') )
          {
            &{$scan_proc->{error_handler}}("Invalid reduce flag: $arg");
          }
        $reduce = ($arg eq 'r' ? 'yes' : 'no');
      }

    if ($scan_proc->{mode} eq "MATH")
      {
        my $node = new_math_node();
        $node->{type} = 'ppd_randrat';
        $node->{value} =
          {
           path => $path,
           numerator_min => $num_min,
           numerator_max => $num_max,
           denominator_min => $den_min,
           denominator_max => $den_max,
           non_zero => $non_zero,
           reduce => $reduce,
          };
        append_math_node($node);
      }
    else
      {
	&{$scan_proc->{error_handler}}("Command only allowed in math mode");
      }

    log_message("japs_execute_ppdrandrat 2/2\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "ppd_randrat".

sub japs_handle_ppd_randrat_value
  {
    log_message("\njaps_handle_ppd_randrat_value (1/2)\n");
    my $node = $_[0];
    japs_empty_ppd_element('random_rational', $node->{value}, 'INLINE');
    log_message("\njaps_handle_ppd_randrat_value (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\ppdrandreal' command.

sub japs_execute_ppdrandreal
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("japs_execute_ppdrandreal 1/2\n");

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_register_ds_path($path);

    my $min = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD');
    my $max = get_data_from_arg(2, $man_args, $pos_man_args, 'CMD');
    my $non_zero = 'no';
    if ( $opt_args->[0] )
      {
        $non_zero =
          japs_process_nonzero_arg(get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD'));
      }

    if ($scan_proc->{mode} eq "MATH")
      {
        my $node = new_math_node();
        $node->{type} = 'ppd_randreal';
        $node->{value} =
          {
           path => $path,
           min => $min,
           max => $max,
           non_zero => $non_zero,
          };
        append_math_node($node);
      }
    else
      {
	&{$scan_proc->{error_handler}}("Command only allowed in math mode");
      }

    log_message("japs_execute_ppdrandreal 2/2\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "ppd_randreal".

sub japs_handle_ppd_randreal_value
  {
    log_message("\njaps_handle_ppd_randreal_value (1/2)\n");
    my $node = $_[0];
    japs_empty_ppd_element('random_real', $node->{value}, 'INLINE');
    log_message("\njaps_handle_ppd_randreal_value (2/2)\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\ppdcopy' command.

sub japs_execute_ppdcopy
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("japs_execute_ppdcopy 1/2\n");

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_register_ds_path_ref($path);

    if ($scan_proc->{mode} eq "MATH")
      {
        my $node = new_math_node();
        $node->{type} = 'ppd_copy';
        $node->{value} =
          {
           path => $path,
          };
        append_math_node($node);
      }
    else
      {
	&{$scan_proc->{error_handler}}("Command only allowed in math mode");
      }

    log_message("japs_execute_ppdcopy 2/2\n");
  }

#Ds
#a ($node)
# Handler for values of math nodes of type "ppd_copy".

sub japs_handle_ppd_copy_value
  {
    log_message("\njaps_handle_ppd_copy_value (1/2)\n");
    my $node = $_[0];
    japs_empty_ppd_element('copy', $node->{value}, 'INLINE');
    log_message("\njaps_handle_ppd_copy_value (2/2)\n");
  }

# ------------------------------------------------------------------------------------------
#1 The "ppdrandsel" environment
# ------------------------------------------------------------------------------------------

sub japs_start_ppdoption
  {
    my $key = ++$scan_proc->{ppdoption_key};
    log_message("\njaps_start_ppdoption 1/2\n");
    log_data('Key', $key);
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = '_ppdoption';
    japs_start_ppd_element('option', {key => $key}, 'DISPLAY');
    log_message("\njaps_start_ppdoption 2/2\n");
  }

sub japs_close_ppdoption_if_necessary
  {
    log_message("\njaps_close_ppdoption_if_necessary 1/2\n");
    if ( $scan_proc->{ppdoption_key} == 0 )
      {
	# Allow output:
	unlock_output_list();
	disallow_tokens(["ign_whitesp"]);
      }
    elsif ( $scan_proc->{ppdoption_key} > 0 )
      {
	close_par_if_in_par();
        if ( $scan_proc->{current_env} ne "_ppdoption" )
          {
            &{$scan_proc->{error_handler}}
              (appr_env_notation($scan_proc->{current_env}),
               ' must be opened and closed within same ppdoption.');
          }
        japs_close_ppd_element("option", "DISPLAY");
        reset_scan_proc();
      }
    else
      {
	notify_programming_error
	  ('japs_close_ppdoption_if_necessary', ' Invalid ppdoption key value: ',
           $scan_proc->{ppdoption_key});
      }
    log_message("\njaps_close_ppdoption_if_necessary 2/2\n");
  }

sub japs_execute_ppdoption
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_ppdoption 1/2\n");
    japs_close_ppdoption_if_necessary();
    japs_start_ppdoption();
    log_message("\njaps_execute_ppdoption 2/2\n");
  }

sub japs_init_ppdrandsel_key_if_necessary
  {
    log_message("\njaps_init_ppdrandsel_key_if_necessary\n");
    $scan_proc->{ppdrandsel_key} = 0 unless ( defined($scan_proc->{ppdrandsel_key}) );
  }

sub japs_increase_ppdrandsel_key
  {
    log_message("\njaps_increase_ppdrandsel_key\n");
    $scan_proc->{ppdrandsel_key}++;
  }

sub japs_begin_ppdrandsel
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $key = $scan_proc->{ppdrandsel_key};
    log_message("\njaps_begin_ppdrandsel 1/2\n");
    log_data('Key', $key);
    my $num = 1;
    if ( $opt_args->[0] )
      {
        $num = get_data_from_arg(0, $opt_args, $pos_opt_args, 'ENV');
        $num =~ m/[0-9]+/ ||
          &{$scan_proc->{error_handler}}("Invalid number of selections: $num");
      }
    $scan_proc->{ppdrandsel_num} = $num;
    install_cmds(['ppdoption'], 'japs_problem');
    allow_tokens(['ign_whitesp'], 'PROTECT', 'PREPEND');
    $scan_proc->{ppdoption_key} = 0;
    japs_start_ppd_element('random_select', {key => $key, number => $num}, 'DISPLAY');
    lock_output_list();
    $scan_proc->{locked_output_list_error_msg}
      = "No text allowed before the first \\ppdoption.";
    log_message("\njaps_begin_ppdrandsel 2/2\n");
  }

sub japs_end_ppdrandsel
  {
    my ($num, $total) = ($scan_proc->{ppdrandsel_num}, $scan_proc->{ppdoption_key});
    if ( $num > $total )
      {
        &{$scan_proc->{error_handler}}
          ("Number of options to select is greater than total number of options: $num vs. $total");
      }
    japs_close_ppd_element('random_select', 'DISPLAY');
    log_message("\njaps_end_ppdrandsel 1/1\n");
  }

# ------------------------------------------------------------------------------------------
#1 Writing DSX elements for mchoice
# ------------------------------------------------------------------------------------------

sub japs_mchoice_to_dsx
  {
    log_message("\njaps_mchoice_to_dsx 1/2\n");

    japs_start_content_xml_element('hidden', {}, 'DISPLAY');

    foreach my $choices_key (keys(%{$global_data->{choices}}))
      {
        my $choices = $global_data->{choices}->{$choices_key};

        # Type:
        my $type_path = "common/problem/choices-${choices_key}/type";
        japs_start_dsx_element('data', {path => $type_path}, 'SEMI_DISPLAY');
        xml_pcdata($choices->{type});
        japs_close_dsx_element('data', 'INLINE');

        # Solutions:
        foreach my $choice_key (keys(%{$choices->{choice}}))
          {
            my $solution_path = "common/solution/choices-${choices_key}/choice-${choice_key}";
            japs_start_dsx_element('data', {path => $solution_path}, 'SEMI_DISPLAY');
            xml_pcdata($choices->{choice}->{$choice_key}->{solution});
            japs_close_dsx_element('data', 'INLINE');
          }
      }

    japs_close_content_xml_element('hidden', 'DISPLAY');

    log_message("\njaps_mchoice_to_dsx 2/2\n");
  }

# ------------------------------------------------------------------------------------------
#1 The "subtasks" and "subtasks*" environments
# ------------------------------------------------------------------------------------------

sub japs_begin_subtasks
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $clickable) = @_;
    log_message("\njaps_begin_subtasks 1/2\n");

    # Start "subtasks"  element:
    japs_start_content_xml_element("subtasks", {}, "DISPLAY");

    # Set "clickable" flag if necessary:
    $scan_proc->{subtasks_clickable} = $clickable if ( $clickable );

    # Enable "\subtask" and "\subtask*" commands:
    install_cmds(['subtask', 'subtask*'], 'japs_problem');

    # Disable "subtasks" environment:
    $scan_proc->{env_table}->{subtasks}->{begin_disabled} = TRUE;

    # Init subtask counter:
    $scan_proc->{subtask_count} = 0;

    log_message("\njaps_begin_subtasks 2/2\n");
  }

sub japs_execute_subtask
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $clickable) = @_;
    log_message("\njaps_execute_subtask 1/2\n");

    # Close current subtask if necessary:
    japs_close_subtask() if ( $scan_proc->{subtask_count} > 0 );

    # Increase subtask counter:
    $scan_proc->{subtask_count}++;

    # Start new subtask:
    japs_start_subtask();

    log_message("\njaps_execute_subtask 2/2\n");
  }

sub japs_end_subtasks
  {
    log_message("\njaps_end_subtasks 1/2\n");

    # Close "subtasks"  element:
    japs_close_content_xml_element("subtasks", "DISPLAY");

    # Register this "subtasks" occurrence:
    japs_register_occurrence('subtasks');

    log_message("\njaps_end_subtasks 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "_subtask" pseudo-environment
# ------------------------------------------------------------------------------------------

sub japs_start_subtask
  {
    my ($clickable) = $_[0];
    $clickable = TRUE if ( $scan_proc->{subtasks_clickable} );
    my $number = ++$global_data->{current_subtask_number};
    log_message("\njaps_start_subtask 1/2\n");
    log_data('Number', $number, 'Clickable', $clickable);

    # Start and set-up new scan process:
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = '_subtask';

    # Create "subtask" start tag:
    my $attribs = {num => $number};
    $attribs->{clickable} = 'yes' if ( $clickable );
    japs_start_content_xml_element("subtask", $attribs, "DISPLAY");

    log_message("\njaps_start_subtask 2/2\n");
  }

sub japs_close_subtask
  {
    log_message("\njaps_close_subtask 1/2\n");

    # Close paragraph if any:
    close_par_if_in_par();

    # Check nesting:
    if ( $scan_proc->{current_env} ne '_subtask' )
      {
        &{$scan_proc->{error_handler}}
          ('Improper nesting: ', appr_env_notation($scan_proc->{current_env}),
           ' must be closed before subtask can be closed');
      }

    # Reset scan process:
    reset_scan_proc();

    # Create "subtask" end tag:
    japs_close_content_xml_element("subtask", "DISPLAY");

    log_message("\njaps_close_subtask 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "execute" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_execute
  {
    log_message("\njaps_begin_execute 1/2\n");

    # Start "execute"  element:
    japs_start_content_xml_element("execute", {}, "DISPLAY");

    log_message("\njaps_begin_execute 2/2\n");
  }

sub japs_end_execute
  {
    log_message("\njaps_end_execute 1/2\n");

    unlock_output_list();

    # Close "execute"  element:
    japs_close_content_xml_element("execute", "DISPLAY");

    log_message("\njaps_end_execute 2/2\n");
  }

# ------------------------------------------------------------------------------------------
#1 The "choices" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_choices
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_choices 1/3\n");

    # Get type:
    my $type =
      get_data_from_arg(0, $man_args, $pos_man_args, "ENV", "choices", "type_arg");
    if ( ! grep($_ eq $type, @{$scan_proc->{env_table}->{choices}->{types}}) )
      {
        &{$scan_proc->{error_handler}}("Invalid choices type: $type");
      }

    # Choices key, choice key;
    my $key = ++$global_data->{current_choices_key};
    $global_data->{current_choice_key} = 0;

    # Store type in global data:
    $global_data->{choices}->{$key}->{type} = $type;

    log_message("\njaps_begin_choices 2/3\n");
    log_data('Type', $type, 'Key', $key);

    # Start "choices"  element:
    japs_start_content_xml_element
      ("choices", {type => $type, key => $key}, "DISPLAY");

    # Enable specific commands and environments:
    install_cmds(['choice', 'commonexpl'], 'japs_problem');
    install_envs(['commonexpl'], 'japs_problem');

    # Disable "choices" environment:
    $scan_proc->{env_table}->{choices}->{begin_disabled} = TRUE;

    # Init choice counter:
    $scan_proc->{choice_count} = 0;

    # Init "commonexpl occurred" flag:
    $scan_proc->{commonexpl_occurred} = FALSE;

    log_message("\njaps_begin_choices 3/3\n");
  }

sub japs_execute_choice
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_choice 1/2\n");

    # Check if "\choice" is allowed here:
    if ( $scan_proc->{commonexpl_occurred} )
      {
        &{$scan_proc->{error_handler}}
          ('No \\choice allowed after commonexpl');
      }

    # Close current choice if necessary:
    japs_close_choice() if ( $scan_proc->{choice_count} > 0 );

    # Increase choice counter:
    $scan_proc->{choice_count}++;

    # Start new choice:
    japs_start_choice();

    # Enable specific commands/environments:
    install_cmds(['assertion', 'solution', 'explanation'], 'japs_problem');
    install_envs(['assertion', 'explanation'], 'japs_problem');

    log_message("\njaps_execute_choice 2/2\n");
  }

sub japs_end_choices
  {
    log_message("\njaps_end_choices 1/2\n");

    # Close "choices"  element:
    japs_close_content_xml_element("choices", "DISPLAY");

    # Register this "choices" occurrence:
    japs_register_occurrence('choices');

    log_message("\njaps_end_choices 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "_choice" pseudo-environment
# ------------------------------------------------------------------------------------------

sub japs_start_choice
  {
    # Choice key;
    my $key = ++$global_data->{current_choice_key};

    log_message("\njaps_start_choice 1/2\n");

    # Start and set-up new scan process:
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = '_choice';

    # Create "choice" start tag:
    japs_start_content_xml_element("choice", {key => $key}, "DISPLAY");

    # Provide specific commands:
    install_cmds(['assertion', 'solution', 'explanation'], 'japs_problem');

    # Init "assertion occurred" and "explanation occurred" flags:
    $scan_proc->{assertion_occurred} = FALSE;
    $scan_proc->{explanation_occurred} = FALSE;

    log_message("\njaps_start_choice 2/2\n");
  }

sub japs_close_choice
  {
    log_message("\njaps_close_choice 1/2\n");

    # Close paragraph if any:
    close_par_if_in_par();

    # Check nesting:
    if ( $scan_proc->{current_env} ne '_choice' )
      {
        &{$scan_proc->{error_handler}}
          ('Improper nesting: ', appr_env_notation($scan_proc->{current_env}),
           ' must be closed before choice can be closed');
      }

    # Check assertion occurrence:
    if ( ! $scan_proc->{assertion_occurred} )
      {
        &{$scan_proc->{error_handler}}('No assertion specified');
      }

    # Check solution:
    my $choices_key = $global_data->{current_choices_key};
    my $choice_key = $global_data->{current_choice_key};
    if ( $global_data->{choices}->{$choices_key}->{type} ne 'unique' &&
         !$global_data->{choices}->{$choices_key}->{choice}->{$choice_key}->{solution} )
      {
        &{$scan_proc->{error_handler}}('No solution specified');
      }

    # Reset scan process:
    reset_scan_proc();

    # Create "choice" end tag:
    japs_close_content_xml_element("choice", "DISPLAY");

    log_message("\njaps_close_choice 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "assertion" environment and "\assertion" command
# ------------------------------------------------------------------------------------------

sub japs_start_assertion
  {
    log_message("\njaps_start_assertion 1/2\n");

    # Check if "assertion" is allowed here:
    if ( $scan_proc->{assertion_occurred} )
      {
        &{$scan_proc->{error_handler}}
          ("Only one assertion allowed in each choice environment");
      }

    # Create "assertion" start tag:
    japs_start_content_xml_element("assertion", {}, "DISPLAY");

    log_message("\njaps_start_assertion 2/2\n");
  }

sub japs_close_assertion
  {
    log_message("\njaps_close_assertion 1/2\n");

    # Create "assertion" end tag:
    japs_close_content_xml_element("assertion", "DISPLAY");

    # Set flag to remember assertion already occurrenced:
    $scan_proc->{assertion_occurred} = TRUE;

    log_message("\njaps_close_assertion 2/2\n");
  }

sub japs_begin_assertion
  {
    log_message("\njaps_begin_assertion\n");
    japs_start_assertion();
  }

sub japs_end_assertion
  {
    log_message("\njaps_end_assertion\n");
    japs_close_assertion();
  }

sub japs_execute_assertion
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_assertion\n");
    japs_start_assertion();
    convert_arg
      (0, $man_args, $pos_man_args, 'CMD',
       undef(),
       \&close_par_if_in_par,
       FALSE, FALSE,
       'SHARED_OUTPUT_LIST');
    japs_close_assertion();
  }

# ------------------------------------------------------------------------------------------
# The "\solution" command
# ------------------------------------------------------------------------------------------

sub japs_execute_solution
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_solution 1/3\n");

    # Get solution value:
    my $solution = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "solution", "arg");
    my $solution_map = $scan_proc->{cmd_table}->{solution}->{value_map};
    if ( !grep($_ eq $solution, keys(%{$solution_map})) )
      {
        &{$scan_proc->{error_handler}}("Invalid solution value: $solution");
      }
    $solution = $solution_map->{$solution};

    log_message("\njaps_execute_solution 2/3\n");
    log_data('Solution', $solution);

    # Save solution in global data:
    my $choices_key = $global_data->{current_choices_key};
    my $choice_key = $global_data->{current_choice_key};
    $global_data->{choices}->{$choices_key}->{choice}->{$choice_key}->{solution} = $solution;

    # Check solution occurrence:
    if ( $global_data->{choices}->{$choices_key}->{type} eq 'unique' )
      {
        my @choice = values(%{$global_data->{choices}->{$choices_key}->{choice}});
        if ( grep($_->{solution} && $_->{solution} eq 'true', @choice) > 1 )
          {
            &{$scan_proc->{error_handler}}
              ('Multiple true solutions in unique choices');
          }
      }

    # Create XML element:
    japs_empty_content_xml_element('solution', {value => $solution}, 'DISPLAY');

    log_message("\njaps_execute_solution 3/3\n");
  }

# ------------------------------------------------------------------------------------------
# The "explanation" environment and "\explanation" command
# ------------------------------------------------------------------------------------------

sub japs_start_explanation
  {
    log_message("\njaps_start_explanation\n");

    # Check if "explanation" is allowed here:
    if ( $scan_proc->{explanation_occurred} )
      {
        &{$scan_proc->{error_handler}}
          ("Only one explanation allowed in each choice environment");
      }

    # Create "explanation" start tag:
    japs_start_content_xml_element("explanation", {}, "DISPLAY");
  }

sub japs_close_explanation
  {
    log_message("\njaps_end_explanation\n");

    # Create "explanation" end tag:
    japs_close_content_xml_element("explanation", "DISPLAY");

    # Set flag to remember explanation already occurrenced:
    $scan_proc->{explanation_occurred} = TRUE;
  }

sub japs_execute_explanation
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_explanation 1/2\n");

    japs_start_explanation();

    # Process arg:
    convert_arg
      (0, $man_args, $pos_man_args, 'CMD',
       undef(),
       \&close_par_if_in_par,
       FALSE, FALSE, 'SHARED_OUTPUT_LIST');

    japs_close_explanation();

    log_message("\njaps_close_explanation 2/2\n");
  }

sub japs_begin_explanation
  {
    log_message("\njaps_begin_explanation\n");
    japs_start_explanation();
  }

sub japs_end_explanation
  {
    log_message("\njaps_end_explanation\n");
    japs_close_explanation();
  }

# ------------------------------------------------------------------------------------------
# The "commonexpl" environment and "\commonexpl" command
# ------------------------------------------------------------------------------------------

sub japs_start_commonexpl
  {
    log_message("\njaps_start_commonexpl\n");

    # Check if "commonexpl" is allowed here:
    if ( $scan_proc->{commonexpl_occurred} )
      {
        &{$scan_proc->{error_handler}}
          ('Multiple commonexpl environments in one choices environment');
      }

    # Close current choice if necessary:
    japs_close_choice() if ( $scan_proc->{choice_count} > 0 );

    # Create "commonexpl" start tag:
    japs_start_content_xml_element("commonexpl", {}, "DISPLAY");
  }

sub japs_close_commonexpl
  {
    log_message("\njaps_end_commonexpl\n");

    # Create "commonexpl" end tag:
    japs_close_content_xml_element("commonexpl", "DISPLAY");

    # Set flag to remember commonexpl already occurrenced:
    $scan_proc->{commonexpl_occurred} = TRUE;
  }

sub japs_execute_commonexpl
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_commonexpl 1/2\n");

    japs_start_commonexpl();

    # Process arg:
    convert_arg
      (0, $man_args, $pos_man_args, 'CMD',
       undef(),
       \&close_par_if_in_par,
       FALSE, FALSE,
       'SHARED_OUTPUT_LIST');

    japs_close_commonexpl();

    log_message("\njaps_close_commonexpl 2/2\n");
  }

sub japs_begin_commonexpl
  {
    log_message("\njaps_begin_commonexpl\n");
    japs_start_commonexpl();
  }

sub japs_end_commonexpl
  {
    log_message("\njaps_end_commonexpl\n");
    japs_close_commonexpl();
  }

# ------------------------------------------------------------------------------------------
# The "hidden" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_hidden
  {
    log_message("\njaps_begin_hidden 1/2\n");
    japs_start_content_xml_element("hidden", {}, "DISPLAY");
    $scan_proc->{prb_hidden} = TRUE;
    log_message("\njaps_begin_hidden 2/2\n");
  }

sub japs_end_hidden
  {
    log_message("\njaps_end_hidden 1/2\n");

    # Close "hidden"  element:
    japs_close_content_xml_element("hidden", "DISPLAY");

    log_message("\njaps_end_hidden 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "prganswer" environment
# ------------------------------------------------------------------------------------------

sub japs_check_integer
  {
    for my $value (@_)
      {
        ( $value =~ m/^[0-9]+$/ ) ||
          &{$scan_proc->{error_handler}}("Not an integral number: $value");
      }
  }

sub japs_begin_prganswer
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nbegin_prganswer 1/2\n");

    japs_check_occurrence('prganswer', 'prganswer', 1, 1, 'FORWARD');

    ( $scan_proc->{mode} eq "MATH" ) &&
      &{$scan_proc->{error_handler}}('Environment "prganswer" cannot be used in math mode');

    my $rows = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    my $cols = get_data_from_arg(1, $man_args, $pos_man_args, 'ENV');
    japs_check_integer($rows, $cols);

    japs_register_ds_path('user/answer/code');

    japs_start_content_xml_element('prganswer', {}, 'SEMI_DISPLAY');

    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{forced_layout} = "STRICTLY_VERBATIM";
    $scan_proc->{token_table} =
      {
       "prganswer" =>
         {
          "tester" => sub { test_regexp("([\\s\\S]*)(?=\\\\end{prganswer})") },
          "handler" => sub
            {
              my $code = $scan_proc->{last_token};
              ( $code =~ m/\@USER_ANSWER\@/ ) ||
                 &{$scan_proc->{error_handler}}('Missing @USER_ANSWER@ keyword');
              my ($before, $after) = ($`, $');
              $before =~ s/^\n//; # Remove leading newline if any
              $after =~ s/\n$//; # Remove trailing newline if any
              chars_to_numcodes(\$before); # (<- Workaround)
              chars_to_numcodes(\$after); # (<- Workaround)
              xml_pcdata($before);
              japs_empty_content_xml_element('prginput', {rows => $rows, cols => $cols});
              xml_pcdata($after);
              reset_scan_proc();
            }
         }
      };
    $scan_proc->{allowed_tokens} = ["prganswer"];

    log_message("begin_prganswer 3/3\n");
  }

sub japs_end_prganswer
  {
    log_message("\nend_prganswer\n");
    japs_close_content_xml_element('prganswer', 'SEMI_DISPLAY');
    japs_register_occurrence('prganswer');
  }

# ------------------------------------------------------------------------------------------
# The "prgwrapper" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_prgwrapper
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nbegin_prgwrapper 1/3\n");

    japs_check_occurrence('prgwrapper', 'prgwrapper', 1, 1, 'FORWARD');

    ( $scan_proc->{prb_hidden} ) ||
      &{$scan_proc->{error_handler}}
        ('Environment "prgwrapper" only allowed in "hidden" environment');

    ( $scan_proc->{mode} eq "MATH" ) &&
      &{$scan_proc->{error_handler}}('Environment "prgwrapper" cannot be used in math mode');

    my $path = 'common/problem/wrapper';
    log_message("\njaps_begin_prgwrapper 2/3\n");
    log_data('Path', $path);
    japs_register_ds_path($path);

    japs_start_dsx_element('data', {path => $path}, 'SEMI_DISPLAY');

    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{forced_layout} = "STRICTLY_VERBATIM";
    $scan_proc->{token_table} =
      {
       "prgwrapper" =>
         {
          "tester" => sub { test_regexp("([\\s\\S]*)(?=\\\\end{prgwrapper})") },
          "handler" => sub
            {
              my $code = $scan_proc->{last_token};
              # ( $code =~ m/\@USER_ANSWER\@/ ) ||
              #   &{$scan_proc->{error_handler}}('Missing @USER_ANSWER@ keyword');
              chars_to_numcodes(\$code); # (<- Workaround)
              xml_pcdata($code);
              reset_scan_proc();
            }
         }
      };
    $scan_proc->{allowed_tokens} = ["prgwrapper"];

    log_message("begin_prgwrapper 3/3\n");
  }

sub japs_end_prgwrapper
  {
    log_message("\nend_prgwrapper\n");
    japs_close_dsx_element('data', 'SEMI_DISPLAY');
    japs_register_occurrence('prgwrapper');
  }

# ------------------------------------------------------------------------------------------
# The "prgevaluator" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_prgevaluator
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nbegin_prgevaluator 1/3\n");

    japs_check_occurrence('prgevaluator', 'prgevaluator', 1, 1, 'FORWARD');

    ( $scan_proc->{prb_hidden} ) ||
      &{$scan_proc->{error_handler}}
        ('Environment "prgevaluator" only allowed in "hidden" environment');

    ( $scan_proc->{mode} eq "MATH" ) &&
      &{$scan_proc->{error_handler}}('Environment "prgevaluator" cannot be used in math mode');

    my $path = 'common/problem/evaluator';
    log_message("\njaps_begin_prgevaluator 2/3\n");
    log_data('Path', $path);
    japs_register_ds_path($path);

    japs_start_dsx_element('data', {path => $path}, 'SEMI_DISPLAY');
    start_verbatim_body('prgevaluator');

    log_message("begin_prgevaluator 3/3\n");
  }

sub japs_end_prgevaluator
  {
    log_message("\nend_prgevaluator\n");
    japs_close_dsx_element('data', 'SEMI_DISPLAY');
    japs_register_occurrence('prgevaluator');
  }

# ------------------------------------------------------------------------------------------
# The "prgsolution" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_prgsolution
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nbegin_prgsolution 1/3\n");

    japs_check_occurrence('prgsolution', 'prgsolution', 1, 1, 'FORWARD');

    ( $scan_proc->{prb_hidden} ) ||
      &{$scan_proc->{error_handler}}
        ('Environment "prgsolution" only allowed in "hidden" environment');

    ( $scan_proc->{mode} eq "MATH" ) &&
      &{$scan_proc->{error_handler}}('Environment "prgsolution" cannot be used in math mode');

    my $path = 'common/problem/solution';
    log_message("\njaps_begin_prgsolution 2/3\n");
    log_data('Path', $path);
    japs_register_ds_path($path);

    japs_start_dsx_element('data', {path => $path}, 'SEMI_DISPLAY');
    start_verbatim_body('prgsolution', 'TRIM');

    log_message("begin_prgsolution 3/3\n");
  }

sub japs_end_prgsolution
  {
    log_message("\nend_prgsolution\n");
    japs_close_dsx_element('data', 'SEMI_DISPLAY');
    japs_register_occurrence('prgsolution');
  }

# ------------------------------------------------------------------------------------------
# The "\prggrading" command
# ------------------------------------------------------------------------------------------

sub japs_execute_prggrading
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_prggrading 1/3\n");

    my $status = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    my $score = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD');
    my $explanation = get_data_from_arg(2, $man_args, $pos_man_args, 'CMD');

    log_message("\njaps_execute_prggrading 2/3\n");
    log_data('Status', $status, 'Score', $score, 'Explanation', $explanation);

    my $score_path = "common/problem/score/status-$status";
    my $expl_path = "common/problem/explanation/status-$status";

    japs_register_ds_path($score_path);
    japs_register_ds_path($expl_path);

    japs_start_dsx_element('data', {path => $score_path}, 'SEMI_DISPLAY');
    xml_pcdata($score);
    japs_close_dsx_element('data', 'INLINE');

    japs_start_dsx_element('data', {path => $expl_path}, 'SEMI_DISPLAY');
    xml_pcdata($explanation);
    japs_close_dsx_element('data', 'INLINE');

    log_message("\njaps_execute_prggrading 3/3\n");
  }

# ------------------------------------------------------------------------------------------
# The "\prgclassname" command
# ------------------------------------------------------------------------------------------

sub japs_execute_prgclassname
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_prgclassname 1/3\n");

    my $class_name = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');

    ( $class_name =~ m/^[A-Za-z0-9_]+$/ ) ||
      &{$scan_proc->{error_handler}}("Invalid Java class name: $class_name");

    log_message("\njaps_execute_prgclassname 2/3\n");
    log_data('Class name', $class_name);

    my $path = "common/problem/class-name";

    japs_register_ds_path($path);

    japs_start_dsx_element('data', {path => $path}, 'SEMI_DISPLAY');
    xml_pcdata($class_name);
    japs_close_dsx_element('data', 'INLINE');

    log_message("\njaps_execute_prgclassname 3/3\n");
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{japs_problem}->{initializer} = sub
  {
    require_lib('math');

    # Additional math node type "ds_data":
    $default_math_nodes->{ds_data} =
      {
       'value_type' => 'NODE_LIST',
       'value_handler' => \&japs_handle_ds_data_value,
      };

    # Additional math node type "ppd_randint":
    $default_math_nodes->{ppd_randint} =
      {
       'value_type' => 'SPECIAL',
       'value_handler' => \&japs_handle_ppd_randint_value,
      };

    # Additional math node type "ppd_randrat":
    $default_math_nodes->{ppd_randrat} =
      {
       'value_type' => 'SPECIAL',
       'value_handler' => \&japs_handle_ppd_randrat_value,
      };

    # Additional math node type "ppd_randreal":
    $default_math_nodes->{ppd_randreal} =
      {
       'value_type' => 'SPECIAL',
       'value_handler' => \&japs_handle_ppd_randreal_value,
      };

    # Additional math node type "ppd_copy":
    $default_math_nodes->{ppd_copy} =
      {
       'value_type' => 'SPECIAL',
       'value_handler' => \&japs_handle_ppd_copy_value,
      };

    # Command table for export:
    my $cmd_table =
      {
       'data' =>
         {
          'num_man_args' => 2,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_data,
         },
       'data*' =>
         {
          'num_man_args' => 2,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => sub { japs_execute_data(@_, TRUE) },
         },
       'ppdrandint' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 1,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_ppdrandint,
         },
       'ppdrandrat' =>
         {
          'num_man_args' => 5,
          'num_opt_args' => 2,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_ppdrandrat,
         },
       'ppdrandreal' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 1,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_ppdrandreal,
         },
       'ppdcopy' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_ppdcopy,
         },
       'ppdoption' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_ppdoption,
         },
       'subtask' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_subtask,
         },
       'subtask*' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_subtask(@_, TRUE) },
         },
       'choice' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_choice,
         },
       'assertion' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_assertion,
         },
       'solution' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_solution,
          'value_map' =>
            {
             'true' => 'true',
             'yes' => 'true',
             'false' => 'false',
             'no' => 'false',
             'compute' => 'compute',
            },
         },
       'explanation' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_explanation,
         },
       'commonexpl' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_commonexpl,
         },
       'prggrading' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_prggrading,
          'is_par_starter' => FALSE,
         },
       'prgclassname' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_prgclassname,
          'is_par_starter' => FALSE,
         },
      };

    my $env_table =
      {
       'data' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'is_par_starter' => TRUE,
          'begin_function' => \&japs_begin_data,
          'end_function' => \&japs_end_data,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'data*' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'is_par_starter' => TRUE,
          'begin_function' => sub {japs_begin_data(@_, TRUE) },
          'end_function' => \&japs_end_data,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'verbdata' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'begin_function' => \&japs_begin_verbdata,
          'end_function' => \&japs_end_verbdata,
         },
       'subtasks' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_subtasks,
          'end_function' => \&japs_end_subtasks,
          'early_pre_end_hook' => \&japs_close_subtask,
          'is_par_closer' => TRUE,
         },
       'subtasks*' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => sub { japs_begin_subtasks(@_, TRUE) },
          'end_function' => \&japs_end_subtasks,
          'early_pre_end_hook' => \&japs_close_subtask,
          'is_par_closer' => TRUE,
         },
       'execute' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_execute,
          'end_function' => \&japs_end_execute,
         },
       'choices' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_choices,
          'end_function' => \&japs_end_choices,
          'early_pre_end_hook' =>
            sub { japs_close_choice() if ( $scan_proc->{current_env} eq '_choice' ) },
          'types' => ['unique', 'multiple', 'yesno'],
          'is_par_closer' => TRUE,
         },
       'assertion' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_assertion,
          'end_function' => \&japs_end_assertion,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'explanation' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_explanation,
          'end_function' => \&japs_end_explanation,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'commonexpl' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_commonexpl,
          'end_function' => \&japs_end_commonexpl,
          'early_pre_begin_hook' => \&japs_close_choice,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'hidden' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_hidden,
          'end_function' => \&japs_end_hidden,
          'is_par_starter' => FALSE,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'prganswer' =>
         {
          'num_man_args' => 2,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_prganswer,
          'end_function' => \&japs_end_prganswer,
          'is_par_starter' => FALSE,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'prgwrapper' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_prgwrapper,
          'end_function' => \&japs_end_prgwrapper,
          'is_par_starter' => FALSE,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'prgevaluator' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_prgevaluator,
          'end_function' => \&japs_end_prgevaluator,
          'is_par_starter' => FALSE,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'prgsolution' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_prgsolution,
          'end_function' => \&japs_end_prgsolution,
          'is_par_starter' => FALSE,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'ppdrandsel' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 1,
          'is_par_starter' => FALSE,
          'begin_function' => \&japs_begin_ppdrandsel,
          'end_function' => \&japs_end_ppdrandsel,
	  'early_pre_begin_hook' => \&japs_increase_ppdrandsel_key,
          'early_pre_end_hook' => \&japs_close_ppdoption_if_necessary,
         },
      };

    # Init global data:
    $global_data->{ds_paths} = [];
    $global_data->{ds_path_refs} = [];
    $global_data->{current_subtask_number} = 0;
    $global_data->{choices} = {};
    $global_data->{current_choices_key} = 0;
    $global_data->{current_choice_key} = 0;

    my @ppd_cmds = ('ppdrandint', 'ppdrandrat', 'ppdrandreal', 'ppdcopy');
    my @ppd_envs = ('ppdrandsel');
    my @ds_cmds = ('data', 'data*');
    my @ds_envs = ('data', 'data*', 'verbdata');

    deploy_lib
      ('japs_problem',
       $cmd_table,
       $env_table,
       {
        'JAPS_PPD' => \@ppd_cmds,
        'JAPS_DS' => \@ds_cmds,
       },
       {
        'JAPS_PPD' => \@ppd_envs,
        'JAPS_DS' => \@ds_envs,
       },
     );
  };

return(TRUE);
