package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: table.mtx.pl,v 1.51 2008/07/03 11:55:02 rassy Exp $

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
# Defines the table environment and table-related commands.
# Syntax:
#
#   \begin{table}[CLASS][CELLCLASSES][ALIGN][CELLALIGNS][VALIGN][CELLVALIGNS]
#
# Examples:
#
#   \begin{table}
#
#   \begin{table}[plain]
#
#   \begin{table}[plain][code,normal,normal]
#
#   \begin{table}[plain][code,normal,normal][c][lrj][m][tbB]
#
#   \begin{table}[][][c]


# --------------------------------------------------------------------------------
#1 Additional fields in $scan_proc
# --------------------------------------------------------------------------------
#
#H
#  tbl_class         & The table class. \\
#  current_tbl_env   & The current table environment. \\
#  tbl_row_num       & The actual row number. The first row has number '0'. \\
#  tbl_col_num       & The actual column number. The first column has number '0'. \\
#  tbl_max_col_num   & The maximal column number of the previous row. \\
#  tbl_cell_classes  & Reference to a list of strings. The nth string in the a list
#                      specifies the class of the nth column. \\
#  tbl_cell_aligns   & Reference to a list of strings. The nth string in the a list
#                      specifies the horizontal align of the nth column. \\
#  tbl_cell_valigns  & Reference to a list of strings. The nth string in the a list
#                      specifies the vertical align of the nth column. \\
#  tbl_rowspan       & Rowspan value for the next table cell. \\
#  tbl_colspan       & Colspan value for the next table cell. \\
#  tbl_excess_rowspan_list &
#                      Reference to a list containing auxiliary data to handle cells
#                      that extent over several rows. \\
#  tbl_autostart_disabled &
#                      If 'true', automatic start of table elements is suppressed.
#/H

log_message("\nLibrary \"table\" ", '$Revision: 1.51 $ ', "\n");

# ------------------------------------------------------------------------------------------
#  Auxiliaries
# ------------------------------------------------------------------------------------------

my @table_scan_proc_keys =
  (
   'tbl_class',
   'current_tbl_env',
   'tbl_row_num',
   'tbl_col_num',
   'tbl_max_col_num',
   'tbl_attribs',
   'tbl_cell_classes',
   'tbl_cell_valigns',
   'tbl_rowspan',
   'tbl_colspan',
   'tbl_excess_rowspan_list',
   'tbl_autostart_disabled',
  );

# ------------------------------------------------------------------------------------------
#  Logging
# ------------------------------------------------------------------------------------------

sub log_table_data
  {
    log_message("\nTable data:\n");
    log_data
      (
       "Nesting", $table_nesting_depth,
       "Current tbl env", $scan_proc->{current_tbl_env},
       "Current env", $scan_proc->{current_env},
       "Row", $scan_proc->{tbl_row_num},
       "Column", $scan_proc->{tbl_col_num},
       "Max column", $scan_proc->{tbl_max_col_num},
       "Cell classes", deref_log_list($scan_proc->{tbl_cell_classes}),
       "Cell aligns", deref_log_list($scan_proc->{tbl_cell_aligns}),
       "Cell valigns", deref_log_list($scan_proc->{tbl_cell_valigns}),
       "Cell class", deref_log_list($scan_proc->{tbl_cell_class}),
       "Cell align", deref_log_list($scan_proc->{tbl_cell_align}),
       "Cell valign", deref_log_list($scan_proc->{tbl_cell_valign}),
       "Rowspan", $scan_proc->{tbl_rowspan},
       "Colspan", $scan_proc->{tbl_colspan},
       "Exc rowspan", deref_log_list($scan_proc->{tbl_excess_rowspan_list}),
      );
  }


# ------------------------------------------------------------------------------------------
# Automatic start of elements
# ------------------------------------------------------------------------------------------

sub start_tbl_elements_if_necessary
  #a ()
  # Starts all table elements necessary to output content at the current position. Recall
  # that content is allowed only in table cells. If the parser finds a token that would
  # produce content (e.g., a plain text token), but the current environment is not
  # '_table_cell', but '_table_head' for example, a row and a cell must be opened. This is
  # what this function does.
  {
    log_message("\nstart_tbl_elements_if_necessary 1/2\n");

    if (  ( ! $scan_proc->{tbl_autostart_disabled} )
	  && ( ! $scan_proc->{parsing_data} ) )
      {
	my $current_env = $scan_proc->{current_env};
	if ( $current_env eq "table" )
	  {
	    start_tbl_body();
	    start_tbl_row();
	    start_tbl_cell();
	  }
	elsif ( grep($_ eq $current_env, "_table_body", "_table_head", "_table_foot") )
	  {
	    start_tbl_row();
	    start_tbl_cell();
	  }
	elsif ( $current_env eq "_table_row" )
	  {
	    start_tbl_cell();
	  }
      }

    log_message("\nstart_tbl_elements_if_necessary 2/2\n");
  }

sub check_tbl_elements_before_token
  #a ($token_type)
  # Assumes $token_type is the type of the token that is about to be processed. Checks
  # whether before tokens of this type table elements are to be started automatically if
  # necessary. If so, calls `start_tbl_elements_if_necessary` perform automatic table
  # element start.
  {
    my $token_type = $scan_proc->{last_token_type};
    my $no_autostart = $scan_proc->{token_table}->{$token_type}->{no_tbl_elements_autostart};

    log_message("\ncheck_tbl_elements_before_token\n");
    log_data("Token type", $token_type, "No autostart", $no_autostart);

    unless ( $no_autostart )
      {
	start_tbl_elements_if_necessary();
      }
  }

sub check_tbl_elements_before_cmd
  {
    my $cmd_name = $_[0];

    log_message("\ncheck_tbl_elements_before_cmd\n");
    log_data("Cmd", $cmd_name);

    unless ( $scan_proc->{cmd_table}->{$cmd_name}->{no_tbl_elements_autostart} )
      {
	start_tbl_elements_if_necessary();
      }
  }

sub check_tbl_elements_before_env
  {
    my $env_name = $_[0];

    log_message("\ncheck_tbl_elements_before_env\n");
    log_data("Env", $env_name);

    unless ( $scan_proc->{env_table}->{$env_name}->{no_tbl_elements_autostart} )
      {
	start_tbl_elements_if_necessary();
      }
  }

# --------------------------------------------------------------------------------
#  Setting cell classes, aligns, valigns
# --------------------------------------------------------------------------------

sub tbl_cell_align_char_to_name
  # ($align_char)
  {
    my $align_char = $_[0];
    return
      (align_char_to_name
       ($align_char, {l => 'left', r => 'right', c => 'center', j => 'justify'}));
  }

sub tbl_cell_valign_char_to_name
  # ($valign_char)
  {
    my $valign_char = $_[0];
    return
      (align_char_to_name
       ($valign_char, {t => 'top', m => 'middle', b => 'bottom', B => 'baseline'}));
  }

#Ds
#a ()
# Initializes the arrays holding style informations for each column. These arrays
# are stored in the scan process entries "tbl_cell_classes", "tbl_cell_aligns",
# and "tbl_cell_valigns". This function is used in begin_table(), start_tbl_head(),
# start_tbl_body(), and start_tbl_foot().
#
# The function does the following: If the scan process entry does not exist yet,
# it is initialized with an empty array. Otherwise, it is set to a new copy of
# the array. The latter guarantees that the current scan process has its own style
# information not shared with the parent namespace. This is necessary because the
# style information may be different in the head, body, and foot of the table.

sub init_tbl_cell_styles ()
  {
    foreach my $style
      (
       'tbl_cell_classes',
       'tbl_cell_aligns',
       'tbl_cell_valigns')
      {
        $scan_proc->{$style} =
          ($scan_proc->{$style} ? [@{$scan_proc->{$style}}] : []);
      }
  }

# --------------------------------------------------------------------------------
#  Cells and the `&' token (tbl_sep)
# --------------------------------------------------------------------------------

sub start_tbl_cell
  #a ()
  # Starts a table cell.
  {
    log_message("\nstart_tbl_cell 1/2\n");
    log_table_data();

    # Checking nesting
    if ( $scan_proc->{current_env} ne '_table_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a table cell can be opened.");
      }

    # Updating column number and excess rowspan list
    my $col_num = $scan_proc->{tbl_col_num};
    $col_num++;
    my $excess_rowspan_list = $scan_proc->{tbl_excess_rowspan_list};
    while ( ( $excess_rowspan_list->[$col_num] )
            && ( $excess_rowspan_list->[$col_num] > 0 ) )
      {
	$excess_rowspan_list->[$col_num]--;
	$col_num++;
      }
    $scan_proc->{tbl_col_num} = $col_num;
    $excess_rowspan_list->[$col_num] = $scan_proc->{tbl_rowspan} - 1;

    # Setting up the new scan process
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    disallow_tokens(['ign_whitesp'], 'PROTECT');
    $scan_proc->{current_env} = '_table_cell';
    $scan_proc->{current_tbl_env} = '_table_cell';
    $scan_proc->{cmd_table}->{rowspan}->{disabled} = TRUE;
    $scan_proc->{cmd_table}->{colspan}->{disabled} = TRUE;

    # Attribute hash
    my $attribs = {};

    # First, setting class, align, and valign attributes to the defaults
    my $class = $scan_proc->{tbl_cell_classes}->[$scan_proc->{tbl_col_num}];
    if ( $class )
      {
	$attribs->{class} = $class;
      }
    my $align = $scan_proc->{tbl_cell_aligns}->[$scan_proc->{tbl_col_num}];
    if ( $align )
      {
	$attribs->{align} = $align;
      }
    my $valign = $scan_proc->{tbl_cell_valigns}->[$scan_proc->{tbl_col_num}];
    if ( $valign )
      {
	$attribs->{valign} = $valign;
      }

    # Then, setting class, align, and valign attributs to the values explicitely specified
    # for them, if such values exist
    if ( $scan_proc->{tbl_cell_class} )
      {
	$attribs->{class} = $scan_proc->{tbl_cell_class};
      }
    if ( $scan_proc->{tbl_cell_align} )
      {
	$attribs->{align} = $scan_proc->{tbl_cell_align};
      }
    if ( $scan_proc->{tbl_cell_valign} )
      {
	$attribs->{valign} = $scan_proc->{tbl_cell_valign};
      }

    # Setting rowspan and colspan if > 1
    if ( $scan_proc->{tbl_rowspan} > 1 )
      {
	$attribs->{rowspan} = $scan_proc->{tbl_rowspan};
      }
    if ( $scan_proc->{tbl_colspan} > 1 )
      {
	$attribs->{colspan} = $scan_proc->{tbl_colspan};
      }

    # Starting element
    start_xml_element("tcell", $attribs, "INLINE");

    log_message("\nstart_tbl_cell 2/2\n");
    log_table_data();
  }

sub start_tbl_cell_if_in_row
  #a ()
  # Starts a table cell if inside a table row. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nstart_tbl_cell_if_in_row\n");

    if ( $scan_proc->{current_tbl_env} eq '_table_row' )
      {
	start_tbl_cell();
      }
  }

sub close_tbl_cell
  {
    log_message("\nclose_tbl_cell 1/2\n");
    log_table_data();

    # Checking correct nesting
    if ( $scan_proc->{current_env} ne "_table_cell" )
      {
	&{$scan_proc->{error_handler}}(appr_env_notation($scan_proc->{current_env}),
				       " must be closed within table cell.");
      }

    # Resetting scan process
    reset_scan_proc();

    # Closing element
    close_xml_element("tcell", "INLINE");

    # Allowing \rowspan and \colspan command
    $scan_proc->{cmd_table}->{rowspan}->{disabled} = FALSE;
    $scan_proc->{cmd_table}->{colspan}->{disabled} = FALSE;

    # Updating column number
    $scan_proc->{tbl_col_num} += ($scan_proc->{tbl_colspan} - 1);

    # Resetting rowspan and colspan
    $scan_proc->{tbl_rowspan} = 1;
    $scan_proc->{tbl_colspan} = 1;

    # Resetting cell class and cell align
    delete($scan_proc->{tbl_cell_class});
    delete($scan_proc->{tbl_cell_align});

    log_message("\nclose_tbl_cell 2/2\n");
    log_table_data();
  }

sub close_tbl_cell_if_in_cell
  #a ()
  # Closes a table cell if inside a table cell. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nclose_tbl_cell_if_in_cell\n");

    if ( $scan_proc->{current_tbl_env} eq '_table_cell' )
      {
	close_tbl_cell();
      }
  }

sub handle_tbl_cell_sep
  #a ()
  # Handles the table cell separator, &. The philosophy is that the cell separator closes
  # cells but does not start them. Cells are started automatically when the parser
  # encounters tokens that produce content that is to be placed in a cell but no cell has
  # been opened yet.
  {
    log_message("\nhandle_tbl_cell_sep\n");

    # Ensuring we are inside a table cell
    start_tbl_body_if_in_table();
    start_tbl_row_if_in_head_body_foot();
    start_tbl_cell_if_in_row();

    # Closing current table cell
    close_tbl_cell();
  }

# --------------------------------------------------------------------------------
#  Rows and the command \\
# --------------------------------------------------------------------------------

sub start_tbl_row
  #a ()
  # Starts a table row.
  {
    log_message("\nstart_tbl_row 1/2\n");
    log_table_data();

    # Checking nesting
    if ( $scan_proc->{current_env} !~ m/^_table_(?:head|body|foot)$/ )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a table row can be opened.");
      }

    # Updating row number (note that this is done before new_scan_proc())
    $scan_proc->{tbl_row_num}++;

    # Starting and setting up new scan process
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{current_env} = '_table_row';
    $scan_proc->{current_tbl_env} = '_table_row';

    # Initializing column number
    $scan_proc->{tbl_col_num} = -1;

    # Initializing rowspan and colspan
    $scan_proc->{tbl_rowspan} = 1;
    $scan_proc->{tbl_colspan} = 1;

    # Starting XML element
    start_xml_element("trow", {}, "DISPLAY");

    log_message("\nstart_tbl_row 2/2\n");
    log_table_data();
  }

sub start_tbl_row_if_in_head_body_foot
  #a ()
  # Starts a table row if inside a table head, body or foot. - This is simply a shortcut for
  # code that occurres several times.
  {
    log_message("\nstart_tbl_row_if_in_head_body_foot\n");

    if ( $scan_proc->{current_tbl_env} =~ m/^_table_(?:head|body|foot)$/ )
      {
	start_tbl_row();
      }
  }

sub close_tbl_row
  #a ()
  # Closes a table row
  {
    log_message("\nclose_tbl_row 1/2\n");
    log_table_data();

    # Checking nesting
    if ( $scan_proc->{current_env} ne '_table_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within table row.");
      }

    # Checking number of cells in the row
    my $last_col_num = $scan_proc->{tbl_col_num};
    my $max_col_num = $scan_proc->{tbl_max_col_num};
    if ( ( $max_col_num ) &&  ( $max_col_num >= 0 ) && ($last_col_num != $max_col_num ) )
      {
	&{$scan_proc->{error_handler}}("Invalid number of table cells.",
				       " Expected: ", $max_col_num + 1, ",",
				       " found: ", $last_col_num + 1, ".");
      }

    # Resetting scan process
    reset_scan_proc();

    # Setting the number of cells per row
    $scan_proc->{tbl_max_col_num} = $last_col_num;

    # Set the "cols" attribute of the "table" element if necessary:
    if ( $scan_proc->{tbl_attribs} )
      {
        $scan_proc->{tbl_attribs}->{cols} = $last_col_num + 1;
        delete($scan_proc->{tbl_attribs});
      }

    # Closing XML element
    close_xml_element("trow", "DISPLAY");

    log_message("\nclose_tbl_row 2/2\n");
    log_table_data();
  }

sub close_tbl_row_if_in_row
  #a ()
  # Closes a table row if inside a table row. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nclose_tbl_row_if_in_row\n");

    if ( $scan_proc->{current_tbl_env} eq '_table_row' )
      {
	close_tbl_row();
      }
  }

sub execute_tbl_row_end
  #a ()
  # Executes the \\ command. The philosophy is that \\ closes rows but does not start
  # them. Rows are started automatically when the parser encounters tokens that produce
  # content that is to be placed in a table cell but no cell nor row has been opened yet.
  {
    log_message("\nexecute_tbl_row\n");

    # Ensuring we are inside a table cell
    start_tbl_body_if_in_table();
    start_tbl_row_if_in_head_body_foot();
    start_tbl_cell_if_in_row();

    # Closing the table cell
    close_tbl_cell();

    # Closing the table row
    close_tbl_row();
  }

# --------------------------------------------------------------------------------
#  Head and the \head command
# --------------------------------------------------------------------------------

sub start_tbl_head
  {
    log_message("\nstart_tbl_head 1/2\n");
    log_table_data();

    # Check nesting:
    if ( $scan_proc->{current_env} ne 'table' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a table head can be opened.");
      }

    # Remove the \head command from the command table:
    delete($scan_proc->{cmd_table}->{head});

    # Start and set up a new scan process:
    new_scan_proc('COPY', 'SHARED_NAMESPACE');
    $scan_proc->{current_env} = '_table_head';
    $scan_proc->{current_tbl_env} = '_table_head';

    # Initialize row number
    $scan_proc->{tbl_row_num} = -1;

    # Initialize excess rowspan list:
    $scan_proc->{tbl_excess_rowspan_list} = [];

    # Init cell styles:
    init_tbl_cell_styles();

    # Start XML element:
    start_xml_element("thead", {}, "DISPLAY");

    log_message("\nstart_tbl_head 2/2\n");
  }

sub close_tbl_head
  #a ()
  # Closes a table head
  {
    log_message("\nclose_tbl_head\n");

    # Checking nesting
    if ( $scan_proc->{current_env} ne '_table_head' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within table head.");
      }

    # Resetting scan process
    reset_scan_proc('ADOPT_VALUES', ['tbl_max_col_num']);

    # Closing XML element
    close_xml_element("thead", "DISPLAY");
  }

sub close_tbl_head_if_in_head
  #a ()
  # Closes a table head if inside a table head. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nclose_tbl_head_if_in_head\n");

    if ( $scan_proc->{current_tbl_env} eq '_table_head' )
      {
	close_tbl_head();
      }
  }

sub execute_tbl_head
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_tbl_head 1/2\n");

    # Start the table head:
    start_tbl_head();

    # Process additional cell style settings, if any:
    if ( $opt_args->[0] )
      {
        convert_arg
          (0, $opt_args, $pos_opt_args, 'ENV',
           sub { install_cmds('TABLE_CELL_STYLE', 'table') });
      }

    log_message("\nexecute_tbl_head 2/2\n");
  }

# --------------------------------------------------------------------------------
#  Table body and the \body command
# --------------------------------------------------------------------------------

sub start_tbl_body
  #a ()
  # Starts a table body.
  {
    log_message("\nstart_tbl_body 1/2\n");
    log_table_data();

    # Check nesting:
    if ( $scan_proc->{current_env} ne 'table' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a table body can be opened.");
      }

    # Remove the \head and \body commands from the command list:
    delete($scan_proc->{cmd_table}->{head});
    delete($scan_proc->{cmd_table}->{body});

    # Start and set up a new scan process:
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{current_env} = '_table_body';
    $scan_proc->{current_tbl_env} = '_table_body';

    # Initialize row number:
    $scan_proc->{tbl_row_num} = -1;

    # Initialize excess rowspan list:
    $scan_proc->{tbl_excess_rowspan_list} = [];

    # Init cell styles:
    init_tbl_cell_styles();

    # Start XML element:
    start_xml_element("tbody", {}, "DISPLAY");

    log_message("\nstart_tbl_body 2/2\n");
  }

sub start_tbl_body_if_in_table
  #a ()
  # Starts a table body if inside a table. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nstart_tbl_body_if_in_table\n");

    if ( $scan_proc->{current_tbl_env} eq 'table' )
      {
	start_tbl_body();
      }
  }

sub close_tbl_body
  #a ()
  # Closes a table body
  {
    log_message("\nclose_tbl_body\n");

    # Checking nesting
    if ( $scan_proc->{current_env} ne '_table_body' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within table body.");
      }

    # Resetting scan process
    reset_scan_proc();

    # Closing XML element
    close_xml_element("tbody", "DISPLAY");
  }

sub close_tbl_body_if_in_body
  #a ()
  # Closes a table body if inside a table body. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nclose_tbl_body_if_in_body\n");

    if ( $scan_proc->{current_tbl_env} eq '_table_body' )
      {
	close_tbl_body();
      }
  }

sub execute_tbl_body
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_tbl_body 1/2\n");

    # Close other table structures if necessary:
    close_tbl_cell_if_in_cell();
    close_tbl_row_if_in_row();
    close_tbl_head_if_in_head();

    # Start the table body:
    start_tbl_body();

    # Process additional cell style settings, if any:
    if ( $opt_args->[0] )
      {
        convert_arg
          (0, $opt_args, $pos_opt_args, 'ENV',
           sub { install_cmds('TABLE_CELL_STYLE', 'table') });
      }

    log_message("\nexecute_tbl_body 2/2\n");
  }

# --------------------------------------------------------------------------------
#  Table foot and the \foot command
# --------------------------------------------------------------------------------

sub start_tbl_foot
  #a ()
  # Starts a table foot.
  {
    log_message("\nstart_tbl_foot\n");
    log_table_data();

    # Check nesting:
    if ( $scan_proc->{current_env} ne 'table' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a table foot can be opened.");
      }

    # Remove \head, \body, and \foot from the command list:
    delete($scan_proc->{cmd_table}->{head});
    delete($scan_proc->{cmd_table}->{body});
    delete($scan_proc->{cmd_table}->{foot});

    # Starting and setting up new scan process
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{current_env} = '_table_foot';
    $scan_proc->{current_tbl_env} = '_table_foot';

    # Initializing row number
    $scan_proc->{tbl_row_num} = -1;

    # Initializing excess rowspan list
    $scan_proc->{tbl_excess_rowspan_list} = [];

    # Init cell styles:
    init_tbl_cell_styles();

    # Start XML element:
    start_xml_element("tfoot", {}, "DISPLAY");
  }

sub close_tbl_foot
  #a ()
  # Closes a table foot.
  {
    log_message("\nclose_tbl_foot\n");

    # Checking nesting
    if ( $scan_proc->{current_env} ne '_table_foot' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within table foot.");
      }

    # Resetting scan process
    reset_scan_proc();

    # Closing XML element
    close_xml_element("tfoot", "DISPLAY");
  }

sub close_tbl_foot_if_in_foot
  #a ()
  # Closes a table foot if inside a table foot.
  {
    if ( $scan_proc->{current_tbl_env} eq '_table_foot' )
      {
	close_tbl_foot();
      }
  }

sub execute_tbl_foot
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_tbl_foot 1/2\n");

    # Close other table structures if necessary:
    close_tbl_cell_if_in_cell();
    close_tbl_row_if_in_row();
    close_tbl_head_if_in_head();
    close_tbl_body_if_in_body();

    # Start the table foot:
    start_tbl_foot();

    # Process additional cell style settings, if any:
    if ( $opt_args->[0] )
      {
        convert_arg
          (0, $opt_args, $pos_opt_args, 'ENV',
           sub { install_cmds('TABLE_CELL_STYLE', 'table') });
      }

    log_message("\nexecute_tbl_foot 2/2\n");
  }

# ------------------------------------------------------------------------------------------
#  Commands to change table style
# ------------------------------------------------------------------------------------------

sub execute_tbl_class
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $disable_base_class) = @_;
    log_message("\nexecute_tbl_class 1/2\n");
    my $class = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "class", "arg");
    $scan_proc->{tbl_attribs}->{class} = $class;
    if ( defined($disable_base_class) && $disable_base_class eq 'yes' )
      { $scan_proc->{tbl_attribs}->{'disable-base-class'} = 'yes'; }
    log_message("\nexecute_tbl_class 2/2\n");
    log_data('Class', $class);
  }

sub execute_tbl_align
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_tbl_align 1/2\n");
    my $align_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "align", "arg");
    $scan_proc->{tbl_attribs}->{align} =
      align_char_to_name($align_descr, {l => 'left', r => 'right', c => 'center'});
    log_message("\nexecute_tbl_align 2/2\n");
    log_data('Align', $align_descr);
  }

sub execute_tbl_valign
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_tbl_valign 1/2\n");
    my $valign_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "valign", "arg");
    $scan_proc->{tbl_attribs}->{valign} =
      align_char_to_name
        ($valign_descr, {t => 'top', m => 'middle', b => 'bottom', B => 'baseline'});
    log_message("\nexecute_tbl_valign 2/2\n");
    log_data('Valign', $valign_descr);
  }

sub execute_cellaligns
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_cellaligns 1/2\n");
    my $cell_align_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "cellaligns", "arg");
    my @cell_align_char = split(//, $cell_align_descr);
    for (my $i = 0; $i <= $#cell_align_char; $i++)
      {
        $scan_proc->{tbl_cell_aligns}->[$i] =
          tbl_cell_align_char_to_name($cell_align_char[$i]);
      }
    log_message("\nexecute_cellaligns 2/2\n");
    log_data('Aligns', $cell_align_descr);
  }

sub execute_cellvaligns
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_cellvaligns 1/2\n");
    my $cell_valign_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "cellvaligns", "arg");
    my @cell_valign_char = split(//, $cell_valign_descr);
    for (my $i = 0; $i <= $#cell_valign_char; $i++)
      {
        $scan_proc->{tbl_cell_valigns}->[$i] =
          tbl_cell_valign_char_to_name($cell_valign_char[$i]);
      }
    log_message("\nexecute_cellvaligns 2/2\n");
    log_data('Valigns', $cell_valign_descr);
  }

sub execute_cellclasses
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\nexecute_cellclasses 1/2\n");
    my $cell_classes_descr =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "cellclasses", "arg");
    my @cell_classes = split(/\s*,\s*/, $cell_classes_descr);
    for (my $i = 0; $i <= $#cell_classes; $i++)
      {
        $scan_proc->{tbl_cell_classes}->[$i] = $cell_classes[$i];
      }
    log_message("\nexecute_cellclasses 2/2\n");
    log_data('Classes', $cell_classes_descr);
  }

# ------------------------------------------------------------------------------------------
#  Tables and table environment
# ------------------------------------------------------------------------------------------

sub begin_table
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nbegin_table 1/3\n");

    # Update nesting counter:
    $table_nesting_depth++;

    # Remove table data from scan process, if any:
    foreach my $key (@table_scan_proc_keys)
      {
	delete($scan_proc->{$key});
      }

    # Init table attributes:
    $scan_proc->{tbl_attribs} = {};

    # Init cell styles:
    init_tbl_cell_styles();

    # Process table settings, if any:
    if ( $opt_args->[0] )
      {
        convert_arg
          (0, $opt_args, $pos_opt_args, 'ENV',
           sub { install_cmds('TABLE_STYLE', 'table') });
      }

    # Set the current environment:
    $scan_proc->{current_tbl_env} = 'table';

    # Setup tokens (note that a copy of the list of allowed tokens is made):
    $scan_proc->{allowed_tokens}
      = ["ign_whitesp", @{$scan_proc->{allowed_tokens}}, "tbl_sep"];

    # Setup paragraph handling:
    $scan_proc->{par_enabled} = FALSE;

    # Ensure table elements (trow, tcell, etc.) are started automatically before certain
    # tokens are handled
    $scan_proc->{pre_token_handler}
      = sub
          {
	    check_tbl_elements_before_token();
	    check_par_before_token();
	  };

    # Ensure table elements (trow, tcell, etc.) are started automatically before certain
    # commands are handled
    $scan_proc->{pre_cmd_hook}
      = sub
          {
	    my $cmd_name = $_[0];
	    check_tbl_elements_before_cmd($cmd_name);
	    check_par_before_cmd($cmd_name);
	  };

    # Ensure table elements (trow, tcell, etc.) are started automatically before certain
    # environment begins are handled
    $scan_proc->{pre_env_begin_hook}
      = sub
          {
	    my $env_name = $_[0];
	    check_tbl_elements_before_env($env_name);
	    check_par_before_env($env_name);
	  };

    # Import commands which are valid only within the table environment:
    install_cmds_from_all_libs('TABLE');

    # Allowing '\end{table}'
    $scan_proc->{env_table}->{table}->{end_disabled} = FALSE;

    # Enable table separator token ("&"):
    $scan_proc->{token_table}->{tbl_sep}->{handler} = \&handle_tbl_cell_sep;

    # Start XML element:
    start_xml_element("table", $scan_proc->{tbl_attribs}, "DISPLAY");

    log_message("\nbegin_table 3/3\n");
    log_table_data();
  }

sub close_all_tbl_strucs
  {
    log_message("\nclose_all_tbl_strucs 1/2\n");

    # Closing table structures if necessary
    close_tbl_cell_if_in_cell();
    close_tbl_row_if_in_row();
    close_tbl_head_if_in_head();
    close_tbl_body_if_in_body();
    close_tbl_foot_if_in_foot();

    log_message("\nclose_all_tbl_strucs 2/2\n");
  }

sub end_table
  {
    log_table_data();
    log_message("\nend_table 1/2\n");

    # Close XML element:
    close_xml_element("table", "DISPLAY");

    # Update table nesting depth variable:
    $table_nesting_depth--;

    log_message("\nend_table 2/2\n");
    log_table_data();
  }

# --------------------------------------------------------------------------------
#  The \rowspan command
# --------------------------------------------------------------------------------

sub execute_rowspan
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_rowspan\n");

    # Making shure we are in a row
    start_tbl_body_if_in_table();
    start_tbl_row_if_in_head_body_foot();

    my $rowspan = get_data_from_arg(0, $man_args, $pos_man_args, "CMD");
    $rowspan =~ s/^\s*|\s*$//g;

    if ( ( $rowspan !~ m/^[0-9]+$/ ) || ( $rowspan <= 0 ) )
      {
	&{$scan_proc->{error_handler}}("Invalid rowspan value: $rowspan");
      }

    $scan_proc->{tbl_rowspan} = $rowspan;

    if ( $opt_args->[0] )
      {
	my $class = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD");
	$scan_proc->{tbl_cell_class} = $class;
      }

    if ( $opt_args->[1] )
      {
	my $align_char = get_data_from_arg(1, $opt_args, $pos_opt_args, "CMD");
	my $align = tbl_cell_align_char_to_name($align_char);
	$scan_proc->{tbl_cell_align} = $align;
      }

    if ( $opt_args->[2] )
      {
	my $valign_char = get_data_from_arg(2, $opt_args, $pos_opt_args, "CMD");
	my $valign = tbl_cell_valign_char_to_name($valign_char);
	$scan_proc->{tbl_cell_valign} = $valign;
      }
  }

# --------------------------------------------------------------------------------
# The \colspan command
# --------------------------------------------------------------------------------

sub execute_colspan
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_colspan\n");

    # Making shure we are in a row
    start_tbl_body_if_in_table();
    start_tbl_row_if_in_head_body_foot();

    my $colspan = get_data_from_arg(0, $man_args, $pos_man_args, "CMD");
    $colspan =~ s/^\s*|\s*$//g;

    if ( ( $colspan !~ m/^[0-9]+$/ ) || ( $colspan <= 0 ) )
      {
	&{$scan_proc->{error_handler}}("Invalid colspan value: $colspan");
      }

    $scan_proc->{tbl_colspan} = $colspan;
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

@no_tbl_elements_autostart_token_types =
  (
   'tbl_sep',
   'opt_arg',
   'man_arg',
   'math_tbl_sep',
   'ign_whitesp',
   'nonpar_whitesp',
   'cmd',
   'one_char_cmd',
   'comment',
   'math_ign_whitesp',
  );

$lib_table->{table}->{initializer} = sub
  {
    # Comand and environment tables
    my $table_env_table =
      {
       'table' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 0,
          'begin_function' => \&begin_table,
          'end_function' => \&end_table,
          'early_pre_end_hook' => \&close_all_tbl_strucs,
          'is_par_starter' => TRUE,
          'doc' =>
            {
             'opt_args' =>
               [
                ['style', 'Table style settings'],
               ],
             'description' =>
               'A table. %[1] may contain any of the style commands \\align, \\valign, ' .
               '\\cellclasses, \\cellaligns, and \\cellvaligns.',
             'see' =>
               {'cmds' => ['head', 'body', 'foot', '\\', 'rowspan', 'colspan', 'align',
                           'valign', 'cellclasses', 'cellaligns', 'cellvaligns']},
            }
         }
      };

    my $table_cmd_table =
      {
       "class" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_tbl_class,
          "doc" =>
            {
             'man_args' =>
               [
                ['class', 'Class of the table'],
               ],
             'description' =>
               'Specifies the class of the table. If an XHTML page is made from the document, ' .
               'this will usually become the CSS class. The base class "genuine" is ' .
               'prepended to the class. If you want to suppress this, use the \\class* ' .
               'command instead.',
            },
         },
       "class*" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => sub { execute_tbl_class(@_, 'yes') },
          "doc" =>
            {
             'man_args' =>
               [
                ['class', 'Class of the table'],
               ],
             'description' =>
               'Specifies the class of the table. If an XHTML page is made from the document, ' .
               'this will usually become the CSS class.',
            },
         },
       "align" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_tbl_align,
          "doc" =>
            {
             'man_args' =>
               [
                ['align', 'Horizontal alignment of the table'],
               ],
             'description' =>
               'Specifies the horizontal alignment of the table. ' .
               '%{1} must be a one-character alignment descriptor, i.e., ' .
               '"l" for "left", "c" for "center", "r" for "right"',
            },
         },
       "valign" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_tbl_valign,
          "doc" =>
            {
             'man_args' =>
               [
                ['valign', 'Vertical alignment of the table'],
               ],
             'description' =>
               'Specifies the vertical alignment of the table. ' .
               '%{1} must be a one-character alignment descriptor, i.e., ' .
               ' "t" for "top", "m" for "middle", "b" for "bottom", "B" for "baseline".',
            },
         },
       "cellclasses" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_cellclasses,
         },
       "cellaligns" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_cellaligns,
          "doc" =>
            {
             'man_args' =>
               [
                ['aligns', 'Horizontal alignments of the table cells'],
               ],
             'description' =>
               'Specifies the horizontal alignments of table cells. ' .
               '%{1} must be a sequence of one-character alignment descriptors, ' .
               'one for each table cell, e.g., "ccrl" for "center" "center" ' .
               '"right" "left".',
            }
         },
       "cellvaligns" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_cellvaligns,
          "doc" =>
            {
             'man_args' =>
               [
                ['valigns', 'Vertical alignments of the table cells'],
               ],
             'description' =>
               'Specifies the vertical alignments of table cells. ' .
               '%{1} must be a sequence of one-character alignment descriptors, ' .
               'one for each table cell, e.g., "tmmb" for "top" "middle" ' .
               '"middle" "bottom".',
            }
         },
       "head" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_tbl_head,
          "no_tbl_elements_autostart" => TRUE,
          'doc' =>
            {
             'opt_args' =>
               [
                ['style', 'Cell style settings'],
               ],
             'description' =>
               'Starts the head part of a table. ' .
               '%[1] may contain any of the cell style commands ' .
               '\\cellclasses, \\cellaligns, and \\cellvaligns.',
             'see' =>
               {'cmds' => ['table', 'body', 'foot', 'cellclasses',
                           'cellaligns', 'cellvaligns']},
            }
         },
       "body" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_tbl_body,
          "no_tbl_elements_autostart" => TRUE,
          'doc' =>
            {
             'opt_args' =>
               [
                ['style', 'Cell style settings'],
               ],
             'description' =>
               'Starts the body part of a table. ' .
               '%[1] may contain any of the cell style commands ' .
               '\\cellclasses, \\cellaligns, and \\cellvaligns.',
             'see' =>
               {'cmds' => ['table', 'head', 'foot', 'cellclasses',
                           'cellaligns', 'cellvaligns']},
            }
         },
       "foot" =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_tbl_foot,
          "no_tbl_elements_autostart" => TRUE,
          'doc' =>
            {
             'opt_args' =>
               [
                ['style', 'Cell style settings'],
               ],
             'description' =>
               'Starts the foot part of a table. ' .
               '%[1] may contain any of the cell style commands ' .
               '\\cellclasses, \\cellaligns, and \\cellvaligns.',
             'see' =>
               {'cmds' => ['table', 'head', 'body', 'cellclasses',
                           'cellaligns', 'cellvaligns']},
            }
         },
       "\\" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 0,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_tbl_row_end,
          "doc" =>
            {
             'description' => 'Ends a table row'
            }
         },
       "rowspan" =>
         {
          "num_opt_args" => 3,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_rowspan,
          "no_tbl_elements_autostart" => TRUE,
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
       "colspan" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_colspan,
          "no_tbl_elements_autostart" => TRUE,
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

    my @table_cmds = 
      (
       'head',
       'body',
       'foot',
       "\\",
       'rowspan',
       'colspan',
      );

    my @table_cell_style_cmds =
      (
       'cellclasses',
       'cellaligns',
       'cellvaligns',
      );

    my @table_style_cmds =
      (
       'class',
       'class*',
       'align',
       'valign',
       @table_cell_style_cmds
      );

    my @table_envs = keys(%{$table_env_table});

    deploy_lib
      (
       'table',
       $table_cmd_table,
       $table_env_table,
       {
        'TABLE' => \@table_cmds,
        'TABLE_STYLE' => \@table_style_cmds,
        'TABLE_CELL_STYLE' => \@table_cell_style_cmds,
        'NOT_IN_DATA' => \@table_cmds,
       },
       {
        'TOPLEVEL' => \@table_envs,
        'TABLE_TOPLEVEL' => \@table_envs,
        'NOT_IN_DATA' => \@table_envs,
       }
      );

    foreach my $token_type (@no_tbl_elements_autostart_token_types)
      {
        $scan_proc->{token_table}->{$token_type}->{no_tbl_elements_autostart} = TRUE;
      }
  };

return(TRUE);
