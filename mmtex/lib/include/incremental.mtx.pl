package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: incremental.mtx.pl,v 1.1 2007/07/29 23:16:44 rassy Exp $

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

log_message("\nLibrary \"incremental\" ", '$Revision: 1.1 $ ', "\n");

# --------------------------------------------------------------------------------
# The 'incremental' environment
# --------------------------------------------------------------------------------

sub begin_incremental
  {
    log_message("\nbegin_incremental 1/2");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    # Initialize alignment arrays:
    $scan_proc->{incr_cell_aligns} = [];
    $scan_proc->{incr_cell_valigns} = [];

    # Set alignments if ncessary:
    if ( $opt_args->[0] )
      {
        convert_arg
          (0, $opt_args, $pos_opt_args, 'ENV',
           sub { install_cmds('INCREMENTAL_STYLE', 'incremental') });
      }

    # Start XML element:
    start_xml_element('incremental', {}, 'DISPLAY');

    # Install commands specific to this environent:
    install_cmds('INCREMENTAL', 'incremental');

    # Install tokens specific to this environent:
    allow_tokens(["ign_whitesp"], "PROTECT", "PREPEND");

    # Initialize increment counter:
    $scan_proc->{incr_number} = 0;

    # Lock output list:
    lock_output_list();
    $scan_proc->{locked_output_list_error_msg}
      = "No text allowed before first \\increment.";

    log_message("\nbegin_incremental 2/2\n");
  }

sub close_everything_in_incr
  {
    log_message("\nclose_everything_in_incr 1/2\n");
    close_par_if_in_par();
    close_incr_cell();
    close_increment();
    log_message("\nclose_everything_in_incr 2/2\n");
  }

sub end_incremental
  {
    log_message("\nend_incremental 1/2\n");
    unlock_output_list();
    close_xml_element('incremental', 'DISPLAY');
    log_message("\nend_incremental 2/2\n");
  }

# --------------------------------------------------------------------------------
# Increments
# --------------------------------------------------------------------------------

sub start_increment
  {
    my $visible = ($_[0] || FALSE);

    log_message("\nstart_increment 1/2\n");

    $scan_proc->{incr_number}++;

    new_scan_proc("COPY", "OWN_NAMESPACE");
    $scan_proc->{current_env} = "_increment";
    $scan_proc->{incr_col_num} = -1;

    # Enable '&' token:
    $scan_proc->{allowed_tokens} = ['incr_sep', @{$scan_proc->{allowed_tokens}}];

    my $attribs = {};
    $attribs->{visible} = 'true' if ( $visible );
    start_xml_element("increment", $attribs, "DISPLAY");

    log_message("\nstart_increment 2/2\n");
  }

sub close_increment
  {
    log_message("\nclose_increment 1/2\n");
    close_xml_element("increment", "DISPLAY");
    reset_scan_proc();
    log_message("\nclose_increment 2/2\n");
  }

sub execute_increment
  {
    log_message("\nexecute_increment 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $visible) = @_;

    if ( $scan_proc->{incr_number} == 0 )
      {
	# Allow output:
	unlock_output_list();
	disallow_tokens(["ign_whitesp"]);
      }
    elsif ( $scan_proc->{incr_number} > 0 )
      {
        close_par_if_in_par();
        close_incr_cell();
        close_increment();
      }
    else
      {
	notify_programming_error
	  ('execute_increment', ' Invalid incr_number value: ',
           $scan_proc->{incr_number});
      }

    start_increment($visible);
    start_incr_cell();

    log_message("\nexecute_increment 2/2\n");
  }

# --------------------------------------------------------------------------------
# Increment cells
# --------------------------------------------------------------------------------

sub start_incr_cell
  {
    log_message("\nstart_incr_cell 1/2\n");

    my $col_num = ++$scan_proc->{incr_col_num};

    new_scan_proc("COPY", "OWN_NAMESPACE");
    $scan_proc->{current_env} = "_increment cell";

    # Install increment cell specific commands:
    install_cmds_from_all_libs('INCREMENT_CELL');

    # Start XML Element
    my $attribs = {};
    my $align = $scan_proc->{incr_cell_aligns}->[$col_num];
    my $valign = $scan_proc->{incr_cell_valigns}->[$col_num];
    $attribs->{align} = $align if ( $align );
    $attribs->{valign} = $valign if ( $valign );
    start_xml_element("incrcell", $attribs, "DISPLAY");

    # Save attributes hash to set attributes later if necessary:
    $scan_proc->{incr_cell_attribs} = $attribs;

    log_message("\nstart_incr_cell 2/2\n");
  }

sub close_incr_cell
  {
    log_message("\nclose_incr_cell 1/2\n");

    # Check nesting:
    if ( $scan_proc->{current_env} ne "_increment cell" )
      {
	&{$scan_proc->{error_handler}}
	  (appr_env_notation($scan_proc->{current_env}),
	   ' must be opened and closed within the same increment cell.');
      }

    close_xml_element("incrcell", "DISPLAY");
    reset_scan_proc();

    log_message("\nclose_incr_cell 2/2\n");
  }

sub handle_incr_cell_separator
  {
    log_message("\nhandle_incr_cell_separator 1/2\n");
    close_par_if_in_par();
    close_incr_cell();
    start_incr_cell();
    log_message("\nhandle_incr_cell_separator 2/2\n");
  }

# --------------------------------------------------------------------------------
# The \cellaligns and \cellvaligns commands
# --------------------------------------------------------------------------------

sub execute_incr_cellaligns
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_incr_cellaligns 1/2\n");
    my $cell_align_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "cellaligns", "arg");
    my @align_chars = split(//, $cell_align_descr);
    for (my $i = 0; $i <= $#align_chars; $i++)
      {
        $scan_proc->{incr_cell_aligns}->[$i] = align_char_to_name
          ($align_chars[$i], {l => 'left', c => 'center', r => 'right'});
      }
    log_message("\nexecute_incr_cellaligns 2/2\n");
    log_data('Aligns', $cell_align_descr);
  }

sub execute_incr_cellvaligns
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_incr_cellvaligns 1/2\n");
    my $cell_valign_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "cellvaligns", "arg");
    my @valign_chars = split(//, $cell_valign_descr);
    for (my $i = 0; $i <= $#valign_chars; $i++)
      {
        $scan_proc->{incr_cell_valigns}->[$i] = valign_char_to_name
          ($valign_chars[$i], {t => 'top', m => 'middle', b => 'bottom'});
      }
    log_message("\nexecute_incr_cellvaligns 2/2\n");
    log_data('Vertical aligns', $cell_valign_descr);
  }

# --------------------------------------------------------------------------------
# The \colspan command
# --------------------------------------------------------------------------------

sub execute_incr_cell_colspan
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_incr_cell_colspan\n");

    my $colspan = get_data_from_arg(0, $man_args, $pos_man_args, "CMD");
    $colspan =~ s/^\s*|\s*$//g;

    if ( ( $colspan !~ m/^[0-9]+$/ ) || ( $colspan <= 0 ) )
      {
	&{$scan_proc->{error_handler}}("Invalid colspan value: $colspan");
      }

    $scan_proc->{incr_cell_attribs}->{colspan} = $colspan;
    $scan_proc->{incr_col_num} += ($colspan -1);
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{incremental}->{initializer} = sub
  {
    # Extra tokens
    $default_token_table->{incr_sep}
      = {
         'tester' => sub { test_regexp('&') },
         'handler' => \&handle_incr_cell_separator,
        };

    # Command table to export
    my $incremental_cmd_table =
      {
       'increment' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => sub { execute_increment(@_, FALSE) },
          'doc' =>
            {
             'description' => 'Starts a new increment in an "incremental" environment',
             'see' => {'envs' => ['incremental']},
            },
         },
       'increment*' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' =>  sub { execute_increment(@_, TRUE) },
          'doc' =>
            {
             'description' => 'Starts a new increment in an "incremental" environment ' .
                              'which is initially visible',
             'see' => {'envs' => ['incremental']},
            },
         },
       "cellaligns" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_incr_cellaligns,
          "doc" =>
            {
             'man_args' =>
               [
                ['aligns', 'Horizontal alignments of the increment cells'],
               ],
             'description' =>
               'Specifies the horizontal alignments of increment cells. ' .
               '%{1} must be a sequence of one-character alignment descriptors, ' .
               'one for each cell, e.g., "ccrl" for "center" "center" ' .
               '"right" "left".',
            }
         },
       "cellvaligns" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_incr_cellvaligns,
          "doc" =>
            {
             'man_args' =>
               [
                ['valigns', 'Vertical alignments of the increment cells'],
               ],
             'description' =>
               'Specifies the vertical alignments of increment cells. ' .
               '%{1} must be a sequence of one-character alignment descriptors, ' .
               'one for each cell, e.g., "tmmb" for "top" "middle" ' .
               '"middle" "bottom".',
            }
         },
       'colspan' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'is_par_starter' => FALSE,
          'execute_function' => \&execute_incr_cell_colspan,
          'doc' =>
            {
             "man_args" => [["COLSPAN", "Number of cells to span"]],
             'description' => 'Lets the current increment cell span over %{1} columns',
             'see' => {'envs' => ['incremental']},
            }
         },
      };

    # Environment table to export
    my $incremental_env_table =
      {
       'incremental' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_closer" => TRUE,
          "begin_function" => \&begin_incremental,
          "end_function" => \&end_incremental,
          "early_pre_end_hook" => \&close_everything_in_incr,
          "doc" =>
            {
             "description" => "Lets text become visible step by step",
            }
         },
      };

    deploy_lib
      ('incremental', $incremental_cmd_table, $incremental_env_table,
       {
        'INCREMENTAL' => ['increment', 'increment*'],
        'INCREMENTAL_STYLE' => ['cellaligns', 'cellvaligns'],
        'INCREMENT_CELL' => ['colspan'],
       },
       {
        'TOPLEVEL' => ['incremental'],
       });
  };

return(TRUE);
