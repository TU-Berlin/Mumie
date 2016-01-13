package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: math_table.mtx.pl,v 1.16 2007/07/11 15:56:14 grudzin Exp $

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

log_message("\nLibrary math_table ", 'new', "\n");

# ------------------------------------------------------------------------------------------
#  Auxiliaries
# ------------------------------------------------------------------------------------------

my @mtable_scan_proc_keys =
  (
   'mtbl_class',
   'start_mtbl_env',
   'current_mtbl_env',
   'mtbl_row_num',
   'mtbl_col_num',
   'mtbl_max_col_num',
   'mtbl_cell_classes',
   'mtbl_cell_valigns',
   'mtbl_rowspan',
   'mtbl_colspan',
   'mtbl_excess_rowspan_list',
   'mtbl_autostart_disabled',
  );

# ------------------------------------------------------------------------------------------
#  Logging
# ------------------------------------------------------------------------------------------

sub log_mtable_data
  {
    log_message("\nMtable data:\n");
    log_data
      (
       "Nesting", $mtable_nesting_depth,
       "Start env", $scan_proc->{start_mtbl_env},
       "Current mtbl env", $scan_proc->{current_mtbl_env},
       "Current env", $scan_proc->{current_env},
       "Row", $scan_proc->{mtbl_row_num},
       "Column", $scan_proc->{mtbl_col_num},
       "Max column", $scan_proc->{mtbl_max_col_num},
       "Cell classes", deref_log_list($scan_proc->{mtbl_cell_classes}),
       "Cell aligns", deref_log_list($scan_proc->{mtbl_cell_aligns}),
       "Cell valigns", deref_log_list($scan_proc->{mtbl_cell_valigns}),
       "Cell class", deref_log_list($scan_proc->{mtbl_cell_class}),
       "Cell align", deref_log_list($scan_proc->{mtbl_cell_align}),
       "Cell valign", deref_log_list($scan_proc->{mtbl_cell_valign}),
       "Rowspan", $scan_proc->{mtbl_rowspan},
       "Colspan", $scan_proc->{mtbl_colspan},
       "Exc rowspan", deref_log_list($scan_proc->{mtbl_excess_rowspan_list}),
      );
  }


# ------------------------------------------------------------------------------------------
# Automatic start of elements
# ------------------------------------------------------------------------------------------

sub start_mtbl_elements_if_necessary
  #a ()
  # Starts all table elements necessary to output content at the current position. Recall
  # that content is allowed only in table cells. If the parser finds a token that would
  # produce content (e.g., a plain text token), but the current environment is not
  # '_table_cell', but '_table_head' for example, a row and a cell must be opened. This is
  # waht this function does.
  {
    log_message("\nstart_mtbl_elements_if_necessary 1/2\n");

    if (  ( ! $scan_proc->{mtbl_autostart_disabled} )
	  && ( ! $scan_proc->{parsing_data} ) )
      {
	my $current_env = $scan_proc->{current_env};
	if ( $current_env eq "mtable" )
	  {
	    start_mtbl_row();
	    start_mtbl_cell();
	  }
	elsif ( $current_env eq "_mtable_row" )
	  {
	    start_mtbl_cell();
	  }
      }

    log_message("\nstart_mtbl_elements_if_necessary 2/2\n");
  }

sub check_mtbl_elements_before_token
  #a ($token_type)
  # Assumes $token_type is the type of the token that is about to be processed. Checks
  # whether before tokens of this type table elements are to be started automatically if
  # necessary. If so, calls `start_mtbl_elements_if_necessary` perform automatic table
  # element start.
  {
    my $token_type = $scan_proc->{last_token_type};
    unless ( $scan_proc->{token_table}->{$token_type}->{no_mtbl_elements_autostart} )
      {
	start_mtbl_elements_if_necessary();
      }
  }

sub check_mtbl_elements_before_cmd
  {
    my $cmd_name = $_[0];
    unless ( $scan_proc->{cmd_table}->{$cmd_name}->{no_mtbl_elements_autostart} )
      {
	start_mtbl_elements_if_necessary();
      }
  }

sub check_mtbl_elements_before_env
  {
    my $env_name = $_[0];
    unless ( $scan_proc->{env_table}->{$env_name}->{no_mtbl_elements_autostart} )
      {
	start_mtbl_elements_if_necessary();
      }
  }



# --------------------------------------------------------------------------------
#  Setting cell classes, aligns, valigns
# --------------------------------------------------------------------------------

sub mtbl_cell_class_name_to_name
  # ($class)
  {
    my $cell_class = $_[0];
    my $mtbl_class = $scan_proc->{mtbl_class};

    my $allowed_cell_classes
      = $scan_proc->{env_table}->{mtable}->{classes}->{$mtbl_class}->{cell_classes};

    if ( grep($_ eq $cell_class, @{$allowed_cell_classes}) )
      {
	if ( $cell_class eq "normal" )
	  {
	    $cell_class = "";
	  }
	return($cell_class);
      }
    else
      {
	&{$scan_proc->{error_handler}}("Invalid cell class: ", $cell_class);
	return(FALSE);
      }
  }

sub set_mtbl_cell_classes
  # ()
  {
    my $cell_class_descr = $_[0];

    log_message("\nset_mtbl_cell_classes\n");
    log_data("Descr", $cell_class_descr);

    if ( $cell_class_descr )
      {
	my @cell_class = split(/\s*,\s*/, $cell_class_descr);
	for (my $i = 0; $i <= $#cell_class; $i++)
	  {
	    $scan_proc->{mtbl_cell_classes}->[$i]
	      = mtbl_cell_class_name_to_name($cell_class[$i]);
	  }
      }
  }

sub mtbl_cell_align_char_to_name
  # ($align_char)
  {
    my $align_char = $_[0];
    my $mtbl_class = $scan_proc->{mtbl_class};
    my $align_table
      = $scan_proc->{env_table}->{mtable}->{cell_aligns};
    return(align_char_to_name($align_char, $align_table));
  }

sub set_mtbl_cell_aligns
  # ()
  {
    my $cell_align_descr = $_[0];

    log_message("\nset_mtbl_cell_aligns\n");
    log_data("Descr", $cell_align_descr);

    if ( $cell_align_descr )
      {
	my @cell_align_char = split(//, $cell_align_descr);
	for (my $i = 0; $i <= $#cell_align_char; $i++)
	  {
	    $scan_proc->{mtbl_cell_aligns}->[$i]
	      = mtbl_cell_align_char_to_name($cell_align_char[$i]);
	  }
      }
  }

sub mtbl_cell_valign_char_to_name
  # ($valign_char)
  {
    my $valign_char = $_[0];
    my $mtbl_class = $scan_proc->{mtbl_class};
    my $valign_table
      = $scan_proc->{env_table}->{mtable}->{cell_valigns};
    return(align_char_to_name($valign_char, $valign_table));
  }

sub set_mtbl_cell_valigns
  #a
  {
    my $cell_valign_descr = $_[0];

    log_message("\nset_mtbl_cell_valigns\n");
    log_data("Descr", $cell_valign_descr);

    if ( $cell_valign_descr )
      {
	my @cell_valign_char = split(//, $cell_valign_descr);
	for (my $i = 0; $i <= $#cell_valign_char; $i++)
	  {
	    $scan_proc->{mtbl_cell_valigns}->[$i]
	      = mtbl_cell_valign_char_to_name($cell_valign_char[$i]);
	  }
      }
  }

# --------------------------------------------------------------------------------
#  Cells and the `&' token (math_tbl_sep)
# --------------------------------------------------------------------------------

sub start_mtbl_cell
  #a ()
  # Starts a table cell.
  {
    log_message("\nstart_mtbl_cell 1/2\n");
    log_mtable_data();

    # Checking nesting
    if ( $scan_proc->{current_env} ne '_mtable_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a table cell can be opened.");
      }

    # Updating column number and excess rowspan list
    my $col_num = $scan_proc->{mtbl_col_num};
    $col_num++;
    my $excess_rowspan_list = $scan_proc->{mtbl_excess_rowspan_list};
    while ( ( $excess_rowspan_list->[$col_num] )
            && ( $excess_rowspan_list->[$col_num] > 0 ) )
      {
	$excess_rowspan_list->[$col_num]--;
	$col_num++;
      }
    $scan_proc->{mtbl_col_num} = $col_num;
    $excess_rowspan_list->[$col_num] = $scan_proc->{mtbl_rowspan} - 1;

    # Setting up the new scan process
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    disallow_tokens(['ign_whitesp'], 'PROTECT');
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_mtable_cell';
    $scan_proc->{current_mtbl_env} = '_mtable_cell';
    $scan_proc->{cmd_table}->{rowspan}->{disabled} = TRUE;
    $scan_proc->{cmd_table}->{colspan}->{disabled} = TRUE;

    log_message("\nstart_mtbl_cell 2/2\n");
    log_mtable_data();
  }

sub start_mtbl_cell_if_in_row
  #a ()
  # Starts a table cell if inside a table row. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nstart_mtbl_cell_if_in_row\n");

    if ( $scan_proc->{current_mtbl_env} eq '_mtable_row' )
      {
	start_mtbl_cell();
      }
  }

sub close_mtbl_cell
  {
    log_message("\nclose_mtbl_cell 1/2\n");
    log_mtable_data();

    # Checking correct nesting
    if ( $scan_proc->{current_env} ne "_mtable_cell" )
      {
	&{$scan_proc->{error_handler}}(appr_env_notation($scan_proc->{current_env}),
				       " must be closed within table cell.");
      }

    # Math node
    my $node = new_math_node();
    $node->{type} = 'table_cell';

    # Setting class, align, and valign to the defaults (if any)
    $node->{class} = $scan_proc->{mtbl_cell_classes}->[$scan_proc->{mtbl_col_num}]
      if ( $scan_proc->{mtbl_cell_classes} );
    $node->{align} = $scan_proc->{mtbl_cell_aligns}->[$scan_proc->{mtbl_col_num}]
      if ( $scan_proc->{mtbl_cell_aligns} );
    $node->{valign} = $scan_proc->{mtbl_cell_valigns}->[$scan_proc->{mtbl_col_num}]
      if ($scan_proc->{mtbl_cell_valigns} );

    # Setting class, align, and valign to the values explicitely specified
    # for them, if such values exist
    if ( $scan_proc->{mtbl_cell_class} )
      {
	$node->{class} = $scan_proc->{mtbl_cell_class};
      }
    if ( $scan_proc->{mtbl_cell_align} )
      {
	$node->{align} = $scan_proc->{mtbl_cell_align};
      }
    if ( $scan_proc->{mtbl_cell_valign} )
      {
	$node->{valign} = $scan_proc->{mtbl_cell_valign};
      }

    # Setting rowspan and colspan
    $node->{rowspan} =
      ($scan_proc->{mtbl_rowspan} > 1 ? $scan_proc->{mtbl_rowspan} :  FALSE);
    $node->{colpan} =
      ($scan_proc->{mtbl_colspan} > 1 ? $scan_proc->{mtbl_colspan} :  FALSE);

    # Setting the value
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan process
    reset_scan_proc();

    # Appending math node to math node list
    append_math_node($node);

    # Allowing \rowspan and \colspan command
    $scan_proc->{cmd_table}->{rowspan}->{disabled} = FALSE;
    $scan_proc->{cmd_table}->{colspan}->{disabled} = FALSE;

    # Updating column number
    $scan_proc->{mtbl_col_num} += ($scan_proc->{mtbl_colspan} - 1);

    # Resetting rowspan and colspan
    $scan_proc->{mtbl_rowspan} = 1;
    $scan_proc->{mtbl_colspan} = 1;

    # Resetting cell class and cell align
    delete($scan_proc->{mtbl_cell_class});
    delete($scan_proc->{mtbl_cell_align});

    log_message("\nclose_mtbl_cell 2/2\n");
    log_mtable_data();
  }

sub close_mtbl_cell_if_in_cell
  #a ()
  # Closes a table cell if inside a table cell. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nclose_mtbl_cell_if_in_cell\n");

    if ( $scan_proc->{current_mtbl_env} eq '_mtable_cell' )
      {
	close_mtbl_cell();
      }
  }

sub handle_math_tbl_cell_sep
  #a ()
  # Handles the table cell separator, &. The philosophy is that the cell separator closes
  # cells but does not start them. Cells are started automatically when the parser
  # encounters tokens that produce content that is to be placed in a cell but no cell has
  # been opened yet.
  {
    log_message("\nhandle_math_tbl_cell_sep 1/2\n");

    # Ensuring we are inside a table cell
    start_mtbl_row_if_in_mtable();
    start_mtbl_cell_if_in_row();

    # Closing current table cell
    close_mtbl_cell();

    log_message("\nhandle_math_tbl_cell_sep 2/2\n");
    log_mtable_data();
  }

# --------------------------------------------------------------------------------
#  Rows and the command \\
# --------------------------------------------------------------------------------

sub start_mtbl_row
  #a ()
  # Starts a table row.
  {
    log_message("\nstart_mtbl_row 1/2\n");
    log_mtable_data();

    # Checking nesting
    if ( $scan_proc->{current_env} ne  $scan_proc->{start_mtbl_env} )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a table row can be opened.");
      }

    # Updating row number (note that this is done before new_scan_proc())
    $scan_proc->{mtbl_row_num}++;

    # Starting and setting up new scan process
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_mtable_row';
    $scan_proc->{current_mtbl_env} = '_mtable_row';

    # Initializing column number
    $scan_proc->{mtbl_col_num} = -1;

    # Initializing rowspan and colspan
    $scan_proc->{mtbl_rowspan} = 1;
    $scan_proc->{mtbl_colspan} = 1;

    log_message("\nstart_mtbl_row 2/2\n");
    log_mtable_data();
  }

sub start_mtbl_row_if_in_mtable
  #a ()
  # Starts a table row if inside a table. - This is simply a shortcut for
  # code that occurres several times.
  {
    log_message("\nstart_mtbl_row_if_in_mtable\n");

    if ( $scan_proc->{current_mtbl_env} eq $scan_proc->{start_mtbl_env} )
      {
	start_mtbl_row();
      }
  }

sub close_mtbl_row
  #a ()
  # Closes a table row
  {
    log_message("\nclose_mtbl_row 1/2\n");
    log_mtable_data();

    # Checking nesting
    if ( $scan_proc->{current_env} ne '_mtable_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within table row.");
      }

    # Checking number of cells in the row
    my $last_col_num = $scan_proc->{mtbl_col_num};
    my $max_col_num = $scan_proc->{mtbl_max_col_num};
    if ( ( $max_col_num ) &&  ( $max_col_num >= 0 ) && ($last_col_num != $max_col_num ) )
      {
	&{$scan_proc->{error_handler}}("Invalid number of table cells.",
				       " Expected: ", $max_col_num + 1, ",",
				       " found: ", $last_col_num + 1, ".");
      }


    # Math node
    my $node = new_math_node();
    $node->{type} = 'table_row';
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan process
    reset_scan_proc();

    # Appending math node
    append_math_node($node);

    # Setting the number of cells per row
    $scan_proc->{mtbl_max_col_num} = $last_col_num;

    log_message("\nclose_mtbl_row 2/2\n");
    log_mtable_data();
  }

sub close_mtbl_row_if_in_row
  #a ()
  # Closes a table row if inside a table row. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nclose_mtbl_row_if_in_row\n");

    if ( $scan_proc->{current_mtbl_env} eq '_mtable_row' )
      {
	close_mtbl_row();
      }
  }

sub execute_mtbl_row_end
  #a ()
  # Executes the \\ command. The philosophy is that \\ closes rows but does not start
  # them. Rows are started automatically when the parser encounters tokens that produce
  # content that is to be placed in a table cell but no cell nor row has been opened yet.
  {
    log_message("\nexecute_mtbl_row\n");

    # Ensuring we are inside a table cell
    start_mtbl_row_if_in_mtable();
    start_mtbl_cell_if_in_row();

    # Closing the table cell
    close_mtbl_cell();

    # Closing the table row
    close_mtbl_row();
  }

# ------------------------------------------------------------------------------------------
#  Tables and mtable environment
# ------------------------------------------------------------------------------------------

sub start_mtable
  {
    my ($class, $align, $valign) = @_;

    $mtable_nesting_depth++;

    log_message("\nstart_mtable 1/2\n");

    # Removing table data from scan process, if any
    foreach my $key (@mtable_scan_proc_keys)
      {
	delete($scan_proc->{$key});
      }

    # It is convienient to start another scan process
    new_scan_proc('COPY', 'SHARED_NAMESPACE');

    # New, empty math node list
    $scan_proc->{math_node_list} = [];

    # Storing the table environment name
    $scan_proc->{start_mtbl_env} = $scan_proc->{current_env};

    # Setting the current table environment
    $scan_proc->{current_mtbl_env} = $scan_proc->{start_mtbl_env};

    # Setting up tokens. Note that a copy of list of allowed tokens is made.
    $scan_proc->{allowed_tokens}
      = ["ign_whitesp", @{$scan_proc->{allowed_tokens}}, "math_tbl_sep"];

    # Setting up paragraph handling
    $scan_proc->{par_enabled} = FALSE;

    # Ensuring table elements (trow, tcell, etc.) are started automatically before certain
    # tokens are handled
    $scan_proc->{pre_token_handler}
      = sub
          {
	    check_mtbl_elements_before_token();
	    check_par_before_token();
	  };

    # Ensuring table elements (trow, tcell, etc.) are started automatically before certain
    # commands are handled
    $scan_proc->{pre_cmd_hook}
      = sub
          {
	    my $cmd_name = $_[0];
	    check_mtbl_elements_before_cmd($cmd_name);
	    check_par_before_cmd($cmd_name);
	  };

    # Ensuring table elements (trow, tcell, etc.) are started automatically before certain
    # environment begins are handled
    $scan_proc->{pre_env_begin_hook}
      = sub
          {
	    my $env_name = $_[0];
	    check_mtbl_elements_before_env($env_name);
	    check_par_before_env($env_name);
	  };

    # Storing table class, align, and vertical align
    $scan_proc->{mtbl_class} = $class;
    $scan_proc->{align} = $align if ( $align );
    $scan_proc->{valign} = $valign if ( $valign );

    # Importing special commands valid only within the table environment
    install_cmds_from_all_libs('MTABLE');

    # Allowing '\end{mtable}'
    $scan_proc->{env_table}->{mtable}->{end_disabled} = FALSE;

    log_message("\nstart_mtable 2/2\n");
  }

sub begin_mtable
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nbegin_mtable 1/3\n");

    # Getting the class of this table
    my $class = "normal";
    if ( $opt_args->[0] )
      {
	$class = get_data_from_arg(0, $opt_args, $pos_opt_args, "ENV", "mtable", "class_arg");
      }

    # Checking class
    if ( ( $class ne "normal" )
	 && ( ! exists($scan_proc->{env_table}->{mtable}->{classes}->{$class}) ) )
      {
	&{$scan_proc->{error_handler}}("Invalid table class: ", $class);
      }

    # Getting the horizontical align of this table
    my $align = FALSE;
    if ( $opt_args->[2] )
      {
	my $align_descr
	  = get_data_from_arg(2, $opt_args, $pos_opt_args, "ENV", "mtable", "align_arg");
	$align = align_char_to_name($align_descr, $scan_proc->{env_table}->{mtable}->{aligns});
      }

    # Getting the vertical align of this table
    my $valign = FALSE;
    if ( $opt_args->[4] )
      {
	my $valign_descr
	  = get_data_from_arg(4, $opt_args, $pos_opt_args, "ENV", "mtable", "valign_arg");
	$valign = align_char_to_name($valign_descr, $scan_proc->{env_table}->{mtable}->{valigns});
      }

    # Getting the cell class descriptor
    my $cell_class_descr = FALSE;
    if ( $opt_args->[1] )
      {
	$cell_class_descr
	  = get_data_from_arg(1, $opt_args, $pos_opt_args, "ENV", "mtable", "cell_classes_arg");
      }
	
    # Getting the cell align descriptor
    my $cell_align_descr = FALSE;
    if ( $opt_args->[3] )
      {
	$cell_align_descr
	  = get_data_from_arg(3, $opt_args, $pos_opt_args, "ENV", "mtable", "cell_aligns_arg");
      }

    # Getting the cell valign descriptor
    my $cell_valign_descr = FALSE;
    if ( $opt_args->[5] )
      {
	$cell_valign_descr
	  = get_data_from_arg(5, $opt_args, $pos_opt_args, "ENV", "mtable", "cell_valigns_arg");
      }

    log_message("\nbegin_mtable 2/3\n");
    log_data
      (
       'Class', $class,
       'Align', $align_descr,
       'Valign', $valign_descr,
       'Cell classes', $cell_class_descr,
       'Cell aligns', $cell_align_descr,
       'Cell valigns', $cell_valign_descr,
      );


    # Starting table
    start_mtable($class, $align, $valign);

    # Setting cell classes, aligns, valigns
    set_mtbl_cell_classes($cell_class_descr);
    set_mtbl_cell_aligns($cell_align_descr);
    set_mtbl_cell_valigns($cell_valign_descr);

    log_message("\nbegin_mtable 3/3\n");
    log_mtable_data();
  }


sub close_all_mtbl_strucs
  {
    log_message("\nclose_all_tbl_strucs 1/2\n");

    # Closing table structures if necessary
    close_mtbl_cell_if_in_cell();
    close_mtbl_row_if_in_row();

    log_message("\nclose_all_tbl_strucs 2/2\n");
  }

sub close_mtable
  {
    log_message("\nclose_mtable 1/2\n");

    # Math node
    my $node = new_math_node();
    $node->{type} = 'mtable';
    $node->{class} = $scan_proc->{mtbl_class};
#    $node->{class} .= 'script' if ( $scan_proc->{math_mode} ne 'BASE' );
    $node->{align} = $scan_proc->{align} if ( $scan_proc->{align} );
    $node->{valign} = $scan_proc->{valign} if ( $scan_proc->{valign} );
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan proc
    reset_scan_proc();

    # Appending math node
    append_math_node($node);

    # Updating table nesting depth variable
    $mtable_nesting_depth--;

    log_message("\nclose_mtable 2/2\n");
  }

sub end_mtable
  {
    log_message("\nend_mtable\n");
    log_mtable_data();

    close_mtable();
  }

# --------------------------------------------------------------------------------
#  The \rowspan command
# --------------------------------------------------------------------------------

sub execute_math_rowspan
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_rowspan\n");

    # Making shure we are in a row
    start_mtbl_row_if_in_mtable();

    my $rowspan = get_data_from_arg(0, $man_args, $pos_man_args, "CMD");
    $rowspan =~ s/^\s*|\s*$//g;

    if ( ( $rowspan !~ m/^[0-9]+$/ ) || ( $rowspan <= 0 ) )
      {
	&{$scan_proc->{error_handler}}("Invalid rowspan value: $rowspan");
      }

    $scan_proc->{mtbl_rowspan} = $rowspan;

    if ( $opt_args->[0] )
      {
	my $class = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD");
	$class = mtbl_cell_class_name_to_name($class);
	$scan_proc->{mtbl_cell_class} = $class;
      }

    if ( $opt_args->[1] )
      {
	my $align_char = get_data_from_arg(1, $opt_args, $pos_opt_args, "CMD");
	my $align = mtbl_cell_align_char_to_name($align_char);
	$scan_proc->{mtbl_cell_align} = $align;
      }

    if ( $opt_args->[2] )
      {
	my $valign_char = get_data_from_arg(2, $opt_args, $pos_opt_args, "CMD");
	my $valign = mtbl_cell_valign_char_to_name($valign_char);
	$scan_proc->{mtbl_cell_valign} = $valign;
      }
  }

# --------------------------------------------------------------------------------
# The \colspan command
# --------------------------------------------------------------------------------

sub execute_math_colspan
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_colspan\n");

    # Making shure we are in a row
    start_mtbl_row_if_in_mtable();

    my $colspan = get_data_from_arg(0, $man_args, $pos_man_args, "CMD");
    $colspan =~ s/^\s*|\s*$//g;

    if ( ( $colspan !~ m/^[0-9]+$/ ) || ( $colspan <= 0 ) )
      {
	&{$scan_proc->{error_handler}}("Invalid colspan value: $colspan");
      }

    $scan_proc->{mtbl_colspan} = $colspan;
  }

# --------------------------------------------------------------------------------
# Comand and environment tables
# --------------------------------------------------------------------------------


$math_table_env_table->{mtable}
  = {
     'num_opt_args' => 6,
     'num_man_args' => 0,
     'begin_function' => \&begin_mtable,
     'end_function' => \&end_mtable,
     'early_pre_end_hook' => \&close_all_mtbl_strucs,
     'classes' =>
       {
	'normal' =>
	  {
	   'cell_classes' =>
	     [
	      'normal',
	      'emph',
	      'code',
	      'var',
	      'file',
	      'keyb',
	      'plhld'
	     ],
	  },
	'plain' =>
	  {
	   'cell_classes' =>
	     [
	      'normal',
	      'emph',
	      'code',
	      'var',
	      'file',
	      'keyb',
	      'plhld'
	     ],
	  },
       },
     'aligns' =>
       {
	'd' => '',
	'c' => 'center',
	'l' => 'left',
	'r' => 'right',
       },
     'valigns' =>
       {
	'd' => '',
	't' => 'top',
	'm' => 'middle',
	'b' => 'bottom',
	'B' => 'baseline'
       },
     'cell_aligns' =>
       {
	'd' => '',
	'c' => 'center',
	'l' => 'left',
	'r' => 'right',
	'j' => 'justify'
       },
     'cell_valigns' =>
       {
	'd' => '',
	't' => 'top',
	'm' => 'middle',
	'b' => 'bottom',
	'B' => 'baseline'
       },
     'doc' =>
       {
	'opt_args' =>
	  [
	   ['class', 'Table class'],
	   ['cclasses', 'Cell classes'],
	   ['align', 'Horizontical align of the table'],
	   ['caligns', 'Horizontical aligns of the table cells'],
	   ['valign', 'Vertical align of the table '],
	   ['cvaligns', 'Vertical aligns of the table cells'],
	  ],
	'description' =>
	  'A table. %[2] must be a comma-separated list of classes, one for each table cell. ' .
          '%[3] must be an one-character alignment descriptor (e.g., l for left, c for ' .
	  'center, ...), %[5] a similar descriptor for the vertical alignment (e.g. t for ' .
	  'top, m for middle, ...). ' .
	  '%[4] must be a sequence of one-character alignment  descriptors, one for each ' .
          'table cell (e.g., ccrl for center center right left), %[6] a similar ' .
	  'sequence of vertical alignment  descriptors (e.g., tmbB for top middle bottom ' .
	  ' baseline).',
	'see' =>
	  {'cmds' => ['head', 'body', 'foot', '\\', 'rowspan', 'colspan']},
       }
    };

$math_table_cmd_table
  = {
     "\\"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 0,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_mtbl_row_end,
	   "doc" =>
	     {
	      'description' =>
	        'Ends a table row'
	     }
	  },
     "rowspan"
       => {
	   "num_opt_args" => 3,
	   "num_man_args" => 1,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_math_rowspan,
	   "no_mtbl_elements_autostart" => TRUE,
	   "doc" =>
	     {
	      'man_args' =>
	        [
	         ['number', 'Number of rows to span']
	        ],
	      'opt_args' =>
	        [
	         ['class', 'Class'],
	         ['align', 'Horizontal alignment of the cell'],
	         ['valing', 'Vertical alignment of the cell'],
	        ],
	      'description' =>
                'Spans current table cell over %{1} rows. ' .
                '%[1] must be a one-character alignment descriptor (e.g., l for left, ' .
                'c for center, ...), %[2] a similar descriptor for the vertical alinment ' .
	        '(e.g., t for top, m for middle, ...).'
	     }
	  },
     "colspan"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 1,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_math_colspan,
	   "no_mtbl_elements_autostart" => TRUE,
	    "doc" =>
	     {
	      'man_args' =>
	        [
	         ['number', 'Number of cols to span']
	        ],
	      'description' =>
	        'Spans current table cell over %{1} columns.'
	     }
	  }
    };

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{math_table}->{initializer} = sub
  {
    $math_token_table->{math_tbl_sep}->{handler} = \&handle_math_tbl_cell_sep;

    foreach my $token_type ('cmd', 'one_char_cmd',  'comment', 'man_arg', 'opt_arg')
      {
	$math_token_table->{$token_type}->{no_mtbl_elements_autostart} = TRUE;
      }

    deploy_lib('math_table', $math_table_cmd_table, $math_table_env_table,
	       {'MTABLE' => ['\\', 'rowspan', 'colspan']},
	       {'MATH' => [keys(%{$math_table_env_table})]});
  };



return(TRUE);
