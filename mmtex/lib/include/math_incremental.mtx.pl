package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: math_incremental.mtx.pl,v 1.1 2007/07/29 23:16:44 rassy Exp $

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

log_message("\nLibrary \"math_incremental\" ", '$Revision: 1.1 $ ', "\n");

# --------------------------------------------------------------------------------
# The 'incremental' environment
# --------------------------------------------------------------------------------

sub begin_mathincremental
  {
    log_message("\nbegin_mathincremental 1/2");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    # Start math mode:
    start_display_math();

    # It is convienient to start another scan process:
    new_scan_proc('COPY', 'SHARED_NAMESPACE');

    # New, empty math node list:
    $scan_proc->{math_node_list} = [];

    # Initialize alignment arrays:
    $scan_proc->{mincr_cell_aligns} = [];
    $scan_proc->{mincr_cell_valigns} = [];

    # Set alignments if ncessary:
    if ( $opt_args->[0] )
      {
        convert_arg
          (0, $opt_args, $pos_opt_args, 'ENV',
           sub { install_cmds('MATH_INCREMENTAL_STYLE', 'math_incremental') });
      }

    # Install commands specific to this environent:
    install_cmds('MATH_INCREMENTAL', 'math_incremental');

    # Install tokens specific to this environent:
    allow_tokens(["ign_whitesp"], "PROTECT", "PREPEND");

    # Initialize increment counter:
    $scan_proc->{mincr_number} = 0;

    log_message("\nbegin_mathincremental 2/2\n");
  }

sub close_everything_in_math_incr
  {
    log_message("\nclose_everything_in_math_incr 1/2\n");
    close_math_incr_cell();
    close_math_increment();
    log_message("\nclose_everything_in_math_incr 2/2\n");
  }

sub end_mathincremental
  {
    log_message("\nend_incremental 1/3\n");

    # Math node
    my $node = new_math_node();
    $node->{type} = 'incremental';
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan proc
    reset_scan_proc();

    # Appending math node
    append_math_node($node);

    # Exit math mode:
    close_display_math();

    log_message("\nend_incremental 1/3\n");
    log_math_data();

    # Formula to XML:
    handle_display_math();

    log_message("\nend_incremental 2/2\n");
  }

# --------------------------------------------------------------------------------
# Increments
# --------------------------------------------------------------------------------

sub start_math_increment
  {
    my $visible = ($_[0] || FALSE);

    log_message("\nstart_math_increment 1/2\n");

    $scan_proc->{mincr_number}++;

    new_scan_proc("COPY", "OWN_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = "_increment";
    $scan_proc->{mincr_col_num} = -1;
    $scan_proc->{mincr_colspan} = 1;

    # Enable '&' token:
    $scan_proc->{allowed_tokens} = ['mincr_sep', @{$scan_proc->{allowed_tokens}}];

    log_message("\nstart_math_increment 2/2\n");
  }

sub close_math_increment
  {
    log_message("\nclose_math_increment 1/2\n");

    # Math node
    my $node = new_math_node();
    $node->{type} = 'increment';
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan process
    reset_scan_proc();

    # Appending math node
    append_math_node($node);

    log_message("\nclose_math_increment 2/2\n");
  }

sub execute_math_increment
  {
    log_message("\nexecute_math_increment 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $visible) = @_;

    if ( $scan_proc->{mincr_number} == 0 )
      {
	disallow_tokens(["ign_whitesp"]);
      }
    elsif ( $scan_proc->{mincr_number} > 0 )
      {
        close_math_incr_cell();
        close_math_increment();
      }
    else
      {
	notify_programming_error
	  ('execute_math_increment', ' Invalid mincr_number value: ',
           $scan_proc->{mincr_number});
      }

    start_math_increment($visible);
    start_math_incr_cell();

    log_message("\nexecute_math_increment 2/2\n");
  }

# --------------------------------------------------------------------------------
# Increment cells
# --------------------------------------------------------------------------------

sub start_math_incr_cell
  {
    log_message("\nstart_math_incr_cell 1/2\n");

    $scan_proc->{mincr_col_num}++;

    new_scan_proc("COPY", "OWN_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = "_math increment cell";

    # Install increment cell specific commands:
    install_cmds_from_all_libs('MATH_INCREMENT_CELL');

    log_message("\nstart_math_incr_cell 2/2\n");
  }

sub close_math_incr_cell
  {
    log_message("\nclose_math_incr_cell 1/2\n");

    # Check nesting:
    if ( $scan_proc->{current_env} ne "_math increment cell" )
      {
	&{$scan_proc->{error_handler}}
	  (appr_env_notation($scan_proc->{current_env}),
	   ' must be opened and closed within the same increment cell.');
      }

    # Math node
    my $node = new_math_node();
    $node->{type} = 'incr_cell';
    my $col_num = $scan_proc->{mincr_col_num};
    my $align = $scan_proc->{mincr_cell_aligns}->[$col_num];
    my $valign = $scan_proc->{mincr_cell_valigns}->[$col_num];
    $node->{align} = $align if ( $align );
    $node->{valign} = $valign if ( $valign );
    $node->{value} = $scan_proc->{math_node_list};
    $node->{colspan} = $scan_proc->{mincr_colspan};

    # Reset scan proces:
    reset_scan_proc();

    # Append math node to math node list:
    append_math_node($node);

    # Update column number:
    $scan_proc->{mincr_col_num} += ($scan_proc->{mincr_colspan} - 1);

    # Reset colspan:
    $scan_proc->{mincr_colspan} = 1;

    log_message("\nclose_math_incr_cell 2/2\n");
  }

sub handle_math_incr_cell_separator
  {
    log_message("\nhandle_math_incr_cell_separator 1/2\n");
    close_math_incr_cell();
    start_math_incr_cell();
    log_message("\nhandle_math_incr_cell_separator 2/2\n");
  }

# --------------------------------------------------------------------------------
# The \cellaligns and \cellvaligns commands
# --------------------------------------------------------------------------------

sub execute_math_incr_cellaligns
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_incr_cellaligns 1/2\n");
    my $cell_align_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "cellaligns", "arg");
    my @align_chars = split(//, $cell_align_descr);
    for (my $i = 0; $i <= $#align_chars; $i++)
      {
        $scan_proc->{mincr_cell_aligns}->[$i] = align_char_to_name
          ($align_chars[$i], {l => 'left', c => 'center', r => 'right'});
      }
    log_message("\nexecute_math_incr_cellaligns 2/2\n");
    log_data('Aligns', $cell_align_descr);
  }

sub execute_math_incr_cellvaligns
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_math_incr_cellvaligns 1/2\n");
    my $cell_valign_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "cellvaligns", "arg");
    my @valign_chars = split(//, $cell_valign_descr);
    for (my $i = 0; $i <= $#valign_chars; $i++)
      {
        $scan_proc->{mincr_cell_valigns}->[$i] = valign_char_to_name
          ($valign_chars[$i], {t => 'top', m => 'middle', b => 'bottom'});
      }
    log_message("\nexecute_math_incr_cellvaligns 2/2\n");
    log_data('Vertical aligns', $cell_valign_descr);
  }

# --------------------------------------------------------------------------------
# The \colspan command
# --------------------------------------------------------------------------------

sub execute_math_incr_cell_colspan
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_math_incr_cell_colspan\n");

    my $colspan = get_data_from_arg(0, $man_args, $pos_man_args, "CMD");
    $colspan =~ s/^\s*|\s*$//g;

    if ( ( $colspan !~ m/^[0-9]+$/ ) || ( $colspan <= 0 ) )
      {
	&{$scan_proc->{error_handler}}("Invalid colspan value: $colspan");
      }

    $scan_proc->{mincr_colspan} = $colspan;
  }

# ------------------------------------------------------------------------------------------
# Math node handler
# ------------------------------------------------------------------------------------------

sub handle_math_incremental
  {
    log_message("\nhandle_math_incremental 1/2\n");

    my $node = $_[0];

    # Start "incremental" element:
    start_mathml_ext_namespace();
    start_xml_element("incremental", {}, "DISPLAY");

    # Handle children:
    handle_math_node_list($node->{value});

    # Close "incremental" element:
    close_xml_element("incremental", "DISPLAY");
    reset_xml_namespace();

    log_message("\nhandle_math_incremental 2/2\n");
  }

sub handle_math_increment
  {
    log_message("\nhandle_math_increment 1/2\n");

    my $node = $_[0];

    # Start "increment" element:
    my $attribs = {};
    $attribs->{visible} = 'true' if ( $node->{visible} );
    start_xml_element("increment", $attribs, "DISPLAY");

    # Handle children:
    handle_math_node_list($node->{value});

    # Close "increment" element:
    close_xml_element("increment", "DISPLAY");

    log_message("\nhandle_math_increment 2/2\n");
  }

sub handle_math_incr_cell
  {
    log_message("\nhandle_math_incr_cell 1/2\n");

    my $node = $_[0];
    my $value = $node->{value};

    # Start "incrcell" element:
    my $attribs = {};
    $attribs->{align} = $node->{align} if ( $node->{align} );
    $attribs->{valign} = $node->{valign} if ( $node->{valign} );
    $attribs->{colspan} = $node->{colspan} if ( $node->{colspan} );
    start_xml_element("incrcell", $attribs, "DISPLAY");

    # Handle children:
    start_mathml_namespace();
    if ($#{$value} > 0)
      {
	start_mathml_element("mrow", {}, "DISPLAY");
	handle_math_node_list($value);
	close_mathml_element("mrow");
      }
    else
      {
	handle_math_node_list($value);
      }
    reset_xml_namespace();

    # Close "incrcell" element:
    close_xml_element("incrcell", "DISPLAY");

    log_message("\nhandle_math_incr_cell 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{math_incremental}->{initializer} = sub
  {
    require_lib('math');

    # Extra tokens:
    $default_token_table->{mincr_sep} =
      {
       'tester' => sub { test_regexp('&') },
       'handler' => \&handle_math_incr_cell_separator,
      };

    # Extra math nodes:
    $default_math_nodes->{incremental} =
      {
       'value_type' => 'NODE_LIST',
       'value_handler' => \&handle_math_incremental,
      };
    $default_math_nodes->{increment} =
      {
       'value_type' => 'NODE_LIST',
       'value_handler' => \&handle_math_increment,
      };
    $default_math_nodes->{incr_cell} =
      {
       'value_type' => 'NODE_LIST',
       'value_handler' => \&handle_math_incr_cell,
      };

    # Command table to export
    my $math_incremental_cmd_table =
      {
       'increment' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => sub { execute_math_increment(@_, FALSE) },
          'doc' =>
            {
             'description' => 'Starts a new increment in an "incremental" environment',
             'see' => {'envs' => ['mathincremental']},
            },
         },
       'increment*' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' =>  sub { execute_math_increment(@_, TRUE) },
          'doc' =>
            {
             'description' => 'Starts a new increment in an "incremental" environment ' .
                              'which is initially visible',
             'see' => {'envs' => ['mathincremental']},
            },
         },
       "cellaligns" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_math_incr_cellaligns,
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
          "execute_function" => \&execute_math_incr_cellvaligns,
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
          'execute_function' => \&execute_math_incr_cell_colspan,
          'doc' =>
            {
             "man_args" => [["COLSPAN", "Number of cells to span"]],
             'description' => 'Lets the current increment cell span over %{1} columns',
             'see' => {'envs' => ['mathincremental']},
            }
         },
      };

    # Environment table to export
    my $math_incremental_env_table =
      {
       'mathincremental' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_closer" => TRUE,
          "begin_function" => \&begin_mathincremental,
          "end_function" => \&end_mathincremental,
          "early_pre_end_hook" => \&close_everything_in_math_incr,
          "doc" =>
            {
             "description" => "Lets text become visible step by step",
            }
         },
      };

    deploy_lib
      ('math_incremental', $math_incremental_cmd_table, $math_incremental_env_table,
       {
        'MATH_INCREMENTAL' => ['increment', 'increment*'],
        'MATH_INCREMENTAL_STYLE' => ['cellaligns', 'cellvaligns'],
        'MATH_INCREMENT_CELL' => ['colspan'],
       },
       {
        'TOPLEVEL' => ['mathincremental'],
        'MATH_TOPLEVEL' => ['mathincremental'],
       });
  };

return(TRUE);
