package Mumie::MmTeX::Converter;

# Author: Christian Ruppert <ruppert@math.tu-berlin.de>
#
# $Id: tabular.mtx.pl,v 1.16 2007/07/11 15:56:15 grudzin Exp $

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

# use Mumie::Scanner qw(/access_scan_proc_list/);

# --------------------------------------------------------------------------------
#1                                  Description
# --------------------------------------------------------------------------------
#
# Defines the table environment and tabular-related commands.
# Syntax:
#
#   \begin{tabular}[POS]{SP_FORMS}
#
# Examples:
#
#   \begin{tabular}[10]{|l|r|c|}
#
#   \begin{tabular}[10]{||rr|r@{:}c|}
#


# --------------------------------------------------------------------------------
#1 Additional fields in $scan_proc
# --------------------------------------------------------------------------------
#
#H
#  tab_class         & The tabular class. \\
#  tab_row_num       & The actual row number. The first row has number '0'. \\
#  tab_col_num       & The actual column number. The first column has number '0'. \\
#  tab_max_col_num   & The maximal column number of the tabular, defined by the SP_FORM \\
#  tab_cell_aligns   & Reference to a list of strings. The nth string in the a list
#                      specifies the horizontal align of the nth column. \\
#  tab_cell_align    & Current cell align \\
#  tab_pre_cell      & Reference to a list of arrays. The nth array in the a list
#                      specifies the pre_n cells (borders or @{} cells)
#                      The entries are strings, used to create the output function \\
#/H

log_message("\nLibrary tabular ", '$Revision: 1.16 $ ', "\n");


# ------------------------------------------------------------------------------------------
#  Auxiliaries
# ------------------------------------------------------------------------------------------

my @tabular_scan_proc_keys =
  (
   'tab_class',
   'tab_valign',
   'tab_row_num',
   'tab_col_num',
   'tab_max_col_num',
   'tab_cell_aligns',
   'tab_cell_align',
   'tab_pre_cell',
  );


# ------------------------------------------------------------------------------------------
#  Logging
# ------------------------------------------------------------------------------------------

sub log_tabular_data
  {
    log_message("\n\n\n" . "-" x 50 . "\nTabular data:\n" . "-" x 50 . "\n");
    log_data
      (
       "Nesting", $tabular_nesting_depth,
       "Current tab env", $scan_proc->{current_tab_env},
       "Current env", $scan_proc->{current_env},
       "Row", $scan_proc->{tab_row_num},
       "Column", $scan_proc->{tab_col_num},
       "Max column", $scan_proc->{tab_max_col_num},
       "Cell aligns", deref_log_list($scan_proc->{tab_cell_aligns}),
       "Cell align", deref_log_list($scan_proc->{tab_cell_align}),
      );



    log_message("-" x 50 . "\n\n\n");

  }













# ******************************************************************************************
#
#  The begin_tabular function
#
# ******************************************************************************************



sub begin_tabular
  #
  # Will do the following:
  #
  #  1) Install all cmds of tabular
  #  2) Delete all tabular scan_proc_keys
  #  3) Parsing the horizontal align information
  #  4) Setting the tab_max_col_num by counting the align chars
  #  5) Getting the vertical align of the tabular
  #  6) Increase nesting
  #  7) Change environment
  #  8) Setting up tokens. Note that a copy of list of allowed tokens is made.
  #  9) Setting up paragraph handling
  # 10) Ensuring tabular elements autostart (trow, tcell, etc.)
  # 11) Importing special commands valid only within the tabular environment
  # 12) Allowing '\end{tabular}'
  # 13) Allowing '\\'
  # 14) Initializing col and row with 0;
  # 15) Starting XML element
  #--------------------------------------------------------------------------------------
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nbegin_tabular 1/3 \n");


    # 1) Install all cmds
    #----------------------------------------------------------------------------------
    install_cmds("ALL", 'tabular');


    # 2) Removing possible deprecated tabular data from scan process, if any
    #----------------------------------------------------------------------------------
    foreach my $key (@tabular_scan_proc_keys)
      {
	delete($scan_proc->{$key});
      }


    # 3) Horizontal align  ->  new_scan_proc
    #----------------------------------------------------------------------------------
    my $align_and_border = FALSE;
    if ( $man_args->[0] )
      {

	# PRE_SCAN_HOOK
	#----------------------------------------------------------------------------------
	my $pre_scan_hook  = sub
	  {
	    $scan_proc->{token_table}->{tabular_align_char}->{tester} = sub { test_regexp('[lrc|@]{1}') };
	    $scan_proc->{token_table}->{tabular_align_char}->{handler}= \&tabular_align_char_handler;

	    $scan_proc->{token_table}->{tabular_at_cell_balanced}->{tester} = sub {test_balanced("(?<!\\\\){", "(?<!\\\\)}");};
	    $scan_proc->{token_table}->{tabular_at_cell_balanced}->{handler}= \&tabular_at_cell_handler;

	    @old_allowed_tokens = $scan_proc->{allowed_tokens};
	    $scan_proc->{allowed_tokens} = ["tabular_align_char", "tabular_at_cell_balanced", "man_arg"];
	  };


	# POST_HOOK Collecting the last remaining border cells (if any)
	# --------------------------------------------------------------------------------
	my $post_scan_hook  = sub
	  {
	    log_message("\nPOST_SCAN_HOOK (tabular opt_arg align)\n");

	    # Present cell + 1
	    # --------------------------------------------------------------------------------
	    my $present_cell = $#{$scan_proc->{tab_cell_aligns}} + 1;



	    #  Concat tab_pre_cell to create ONE border function
	    # --------------------------------------------------------------------------------
	    $scan_proc->{tab_pre_cell}->[$present_cell] =  \&concat_raw_borders;


	    log_message("\nCollected the last cell border .. ");
	    log_message("\nLifting data one scanproc up ..\n");

	    to_parent_scanproc("->{tab_cell_aligns}");
	    to_parent_scanproc("->{tab_pre_cell}");

	    undef (@old_allowed_tokens);
	  };

	# Create a NEW_SCAN_PROC
	#----------------------------------------------------------------------------------
	convert_arg(0, $man_args, $pos_man_args, 'ENV', $pre_scan_hook , $post_scan_hook, FALSE, FALSE, 'SHARED_OUTPUT_LIST');
      }
    else
      {
	&{$scan_proc->{error_handler}}("No mandatory argument found..");
      }




    # 4) Max numbers of cells = number of align informations
    #----------------------------------------------------------------------------------
    $scan_proc->{tab_max_col_num} =  $#{($scan_proc->{tab_cell_aligns})};




    # 5) Vertical align
    #----------------------------------------------------------------------------------
    if ( $opt_args->[0] )
      {

    	my $vertical_descr = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD", "tabular", "vertical_align");
	my $valign = $scan_proc->{env_table}->{tabular}->{valigns}->{$valign_descr} || '';
	if ($vertical_descr && $valign)
	  {
	    $scan_proc->{tab_valign} = $valign;
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}("Unknown vertical align character '$vertical_descr'\n");
	  }
      }



    # 6) Increase nesting
    #----------------------------------------------------------------------------------
    $tabular_nesting_depth++;


    # 7) Change environment
    #----------------------------------------------------------------------------------
    $scan_proc->{current_tab_env} = 'tabular';


    # 8) Setting up tokens. Note that a copy of list of allowed tokens is made.
    #----------------------------------------------------------------------------------
    $scan_proc->{allowed_tokens}
      = ["tab_sep", "ign_whitesp", @{$scan_proc->{allowed_tokens}}];


    # 9) Setting up paragraph handling
    #----------------------------------------------------------------------------------
    $scan_proc->{par_enabled} = FALSE;



    # 10) Ensuring tabular elements (trow, tcell, etc.) are started
    #     automatically before certain tokens|envs|cmds are handled
    #----------------------------------------------------------------------------------
    $scan_proc->{pre_token_handler}     = sub
                                           {
					     check_tab_elements_before_token();
					   };
    $scan_proc->{pre_cmd_hook}          = sub
                                           {
					     my $cmd_name = $_[0];
					     check_tab_elements_before_cmd($cmd_name);
					   };
    $scan_proc->{pre_env_begin_hook}    = sub
                                           {
					     my $env_name = $_[0];
					     check_tab_elements_before_env($env_name);
					   };


    # 11) Importing special commands valid only within the tabular environment
    #----------------------------------------------------------------------------------
    install_cmds_from_all_libs('tabular');


    # 12) Allowing '\end{tabular}'
    #----------------------------------------------------------------------------------
    $scan_proc->{env_table}->{tabular}->{end_disabled} = FALSE;


    # 13) Allowing '\\'
    #----------------------------------------------------------------------------------
    $scan_proc->{cmd_table}->{'\\'}->{disabled} = FALSE;


    # 14) Initializing col and row with 0;
    #----------------------------------------------------------------------------------
    $scan_proc->{tab_col_num} = 0;
    $scan_proc->{tab_row_num} = 0;


    # 15) Starting XML element
    #----------------------------------------------------------------------------------
    start_xml_element("tabular", FALSE, "DISPLAY");

    log_message("\nbegin_tabular 2/2\n");
  }








# ******************************************************************************************
#
#  Closing the tabular
#
# ******************************************************************************************


sub close_all_tab_strucs
  {
    log_message("\nclose_all_tab_strucs 1/2\n");

    # Closing tabular structures if necessary
    #----------------------------------------------------------------------------------
    close_tab_cell_if_in_cell();
    close_tab_row_if_in_row();

    log_message("\nclose_all_tab_strucs 2/2\n");
  }


sub close_tabular
  {
    log_tabular_data();
    log_message("\nclose_tabular 1/2\n");

    # Closing XML element
    #----------------------------------------------------------------------------------
    close_xml_element("tabular", "DISPLAY");

    # Decresing tabular nesting depth
    #----------------------------------------------------------------------------------
    $tabular_nesting_depth--;

    log_message("\nclose_tabular 2/2\n");
    log_tabular_data();
  }








# ******************************************************************************************
#
#  Start and close a tabular row
#
# ******************************************************************************************


sub start_tab_row
  #a ()
  # Starts a tabular row...
  #
  # 1) Check nesting
  # 2) Updating row number
  # 3) Starting new scan_proc
  # 4) Init col number with -1
  # 5) Start XML Element
  {
    log_message("\nstart_tab_row 1/2\n");
    log_tabular_data();


    # 1) Checking nesting
    #-------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} !~ m/^tabular$/ )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a tabular row can be opened.");
      }


    # 2) Updating row number (note that this is done before new_scan_proc())
    #-------------------------------------------------------------------------------------
    my $temp = $scan_proc->{tab_row_num}; $temp++; $scan_proc->{tab_row_num} = $temp;
    undef $temp;


    # 3) Starting and setting up new scan process
    #-------------------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{current_env} = '_tabular_row';
    $scan_proc->{current_tab_env} = '_tabular_row';


    # 4) Initializing column number
    #-------------------------------------------------------------------------------------
    $scan_proc->{tab_col_num} = -1;


    # 5) Starting XML element
    #-------------------------------------------------------------------------------------
    start_xml_element("tabrow", FALSE, "DISPLAY");

    log_message("\nstart_tab_row 2/2\n");
    log_tabular_data();
  }






sub close_tab_row
  #a ()
  # Closes a tabular row
  #
  # 1) Check nesting
  # 2) Check number of cells
  # 3) Check for remaining border cells
  # 4) Reset scan proc
  # 5) Close XML Element
  {
    log_message("\nclose_tab_row 1/2\n");
    log_tabular_data();

    # 1) Checking nesting
    #-------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_tabular_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within tabular row.");
      }

    # 2) Checking number of cells in the row
    #-------------------------------------------------------------------------------------
    my $last_col_num = $scan_proc->{tab_col_num};
    my $max_col_num = $scan_proc->{tab_max_col_num};
    if ( ( $max_col_num ) &&  ( $max_col_num >= 0 ) && ($last_col_num != $max_col_num ) )
      {
	&{$scan_proc->{error_handler}}("Invalid number of tabular cells.",
				       " Expected: ", $max_col_num + 1, ",",
				       " found: ", $last_col_num + 1, ".");
      }


    # 3) Checking for remaining border cells
    #-------------------------------------------------------------------------------------
    check_for_remaining_border_cells();

    # 4) Resetting scan process
    #-------------------------------------------------------------------------------------
    reset_scan_proc();

    # 5) Closing XML element
    #-------------------------------------------------------------------------------------
    close_xml_element("tabrow", "DISPLAY");


    log_message("\nclose_tab_row 2/2\n");
    log_tabular_data();
  }











# ******************************************************************************************
#
#  Start and close a tabular cell
#
# ******************************************************************************************



sub start_tab_cell
  #a ()
  # Starts a tabular cell
  #
  # 1)   Check nesting
  # 2)   Update col number
  # 3.1) Multicolumn border if defined
  # 3.2) PreCellBorder otherwise
  # 4)   Start XML Element
  # 5)   New scan proc
  {
    log_message("\nstart_tab_cell 1/2\n");
    log_tabular_data();


    # 1) Checking nesting
    #-------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_tabular_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a tabular cell can be opened.");
      }

    # 2) Updating column number
    #-------------------------------------------------------------------------------------
    my $colspan = $scan_proc->{tab_multicolumn}->{span} || '';
    if ($colspan)
      {
	log_message("Adding colspan value ($colspan) to col number (".$scan_proc->{tab_col_num}.")\n");
	$scan_proc->{tab_col_num} = $scan_proc->{tab_col_num} + $scan_proc->{tab_multicolumn}->{span};
	undef $scan_proc->{tab_multicolumn}->{span};
      }
    else
      {
	$scan_proc->{tab_col_num}++;
      }


    # 3.1) Pre Cell border -> Multicolumn defined
    #-------------------------------------------------------------------------------------
    my $multicol_func = $scan_proc->{tab_multicolumn}->{bordercell} || "";
    if ( ref($multicol_func) eq "CODE" )
      {
	&{$multicol_func}();
	undef $scan_proc->{tab_multicolumn}->{bordercell};
      }

    # 3.2) Pre Cell border -> Standard cell
    #-------------------------------------------------------------------------------------
    else
      {
	my $pre_cell_func = $scan_proc->{tab_pre_cell}->[$scan_proc->{tab_col_num}];
	if (ref $pre_cell_func eq "CODE")
	  {
	    &{$pre_cell_func}();
	  }
      }


    # 4) Start XML element  (multicolumn_align is dominant)
    #-------------------------------------------------------------------------------------
    my $align = $scan_proc->{tab_cell_aligns}->[$scan_proc->{tab_col_num}];
    my $multicolumn_align = $scan_proc->{tab_multicolumn}->{cell_align};
    my $attribs = {};

    $attribs->{align} = $align                  if ( not $multicolumn_align && $align);
    $attribs->{align} = $multicolumn_align      if ( $multicolumn_align );

    undef $scan_proc->{tab_multicolumn}->{cell_align};

    start_xml_element("tabcell", $attribs, "SEMI_DISPLAY");


    # 5) Setting up the new scan process
    #-------------------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");

    $scan_proc->{current_env} = '_tabular_cell';
    $scan_proc->{current_tab_env} = '_tabular_cell';

    log_message("\nstart_tab_cell 2/2\n");
    log_tabular_data();

  }




sub close_tab_cell
  # Closes a tabular cell
  #
  # 1) Check nesting
  # 2) Reset scan proc
  # 3) Close XML Element
  # 4) Allow multicolumn command
  # 5) Reset cell align
  {
    log_message("\nclose_tab_cell 1/2\n");
    log_tabular_data();

    # Checking correct nesting
    #-------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne "_tabular_cell" )
      {
	&{$scan_proc->{error_handler}}(appr_env_notation($scan_proc->{current_env}),
				       " must be closed within tabular cell.");
      }

    # Resetting scan process
    #-------------------------------------------------------------------------------------
    reset_scan_proc();


    # Closing element
    #-------------------------------------------------------------------------------------
    close_xml_element("tabcell", "INLINE");


    # Allowing \multicolumn command
    #-------------------------------------------------------------------------------------
    #    $scan_proc->{cmd_table}->{multicolumn}->{disabled} = FALSE;


    # Resetting cell align
    #-------------------------------------------------------------------------------------
    delete($scan_proc->{tab_cell_align});

    log_message("\nclose_tab_cell 2/2\n");
    log_tabular_data();
  }


















# ******************************************************************************************
#
#  Cell sperator and row end handler
#
# ******************************************************************************************



sub handle_tab_cell_sep
  #a ()
  {
    log_message("\nhandle_tab_cell_sep:\n");

    # Ensuring we are inside a tabular cell and row
    #-------------------------------------------------------------------------------------
    start_tabular_elements_if_necessary();

    # Closing current tabular cell
    #-------------------------------------------------------------------------------------
    close_tab_cell();
  }


sub execute_tab_row_end
  #a ()
  {
    log_message("\nexecute_tab_row\n");

    # Ensuring we are inside a tabular cell
    #-------------------------------------------------------------------------------------
    start_tab_row_if_in_tabular();
    start_tab_cell_if_in_row();

    # Closing the tabular cell
    #-------------------------------------------------------------------------------------
    close_tab_cell();

    # Closing the tabular row
    #-------------------------------------------------------------------------------------
    close_tab_row();
  }













# ******************************************************************************************
#
#  The CHECK_.._BEFORE functions
#
# ******************************************************************************************


sub  check_tab_elements_before_token
  {
    my $token_type = $scan_proc->{last_token_type};
    my $no_autostart = $scan_proc->{token_table}->{$token_type}->{no_tab_elements_autostart};

    log_message("\ncheck_tab_elements_before_token\n");
    log_data("Token type", $token_type, "No autostart", $no_autostart);

    start_tabular_elements_if_necessary()     unless ( $no_autostart  || $scan_proc->{parsing_data});
  }

sub check_tab_elements_before_cmd
  {
    my $cmd_name = $_[0];
    my $no_autostart = $scan_proc->{cmd_table}->{$cmd_name}->{no_tab_elements_autostart};

    log_message("\ncheck_tab_elements_before_cmd\n");
    log_data("Cmd", $cmd_name, "No autostart", $no_autostart);

    start_tabular_elements_if_necessary()   unless ( $no_autostart  || $scan_proc->{parsing_data} );
  }

sub check_tab_elements_before_env
  {
    my $env_name = $_[0];
    my $no_autostart = $scan_proc->{env_table}->{$env_name}->{no_tab_elements_autostart};

    log_message("\ncheck_tab_elements_before_env\n");
    log_data("Env", $env_name, "No Autostart",$no_autostart);

    start_tabular_elements_if_necessary() unless ( $no_autostart  || $scan_proc->{parsing_data});
  }











# ******************************************************************************************
#
#  The START_.._IF   and   CLOSE_.._IF   functions
#
# ******************************************************************************************


sub start_tabular_elements_if_necessary
  {
    start_tab_row_if_in_tabular();
    start_tab_cell_if_in_row();
  }

sub start_tab_cell_if_in_row
  #a ()
  # Starts a tabular cell if inside a tabular row. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nstart_tab_cell_if_in_row --> ");

    if ( $scan_proc->{current_tab_env} eq '_tabular_row' )
      {
	log_message("YES\n");
	start_tab_cell();
      }
    else
      {
	log_message("NO\n");
      }
  }


sub close_tab_cell_if_in_cell
  #a ()
  # Closes a tabular cell if inside a tabular cell. - This is simply a shortcut for code that
  # occurres several times.
  {
    log_message("\nclose_tab_cell_if_in_cell  --> ");

    if ( $scan_proc->{current_tab_env} eq '_tabular_cell' )
      {
	log_message("YES\n");
	close_tab_cell();
      }
    else
      {
	log_message("NO\n");
      }
  }

sub start_tab_row_if_in_tabular
  #a ()
  {
    log_message("\nstart_tab_row_if_in_tabular  --> ");
    if ( $scan_proc->{current_tab_env} =~ m/^tabular$/ )
      {
	log_message("YES\n");
	start_tab_row();
      }
    else
      {
	log_message("NO\n");
      }
  }

sub close_tab_row_if_in_row
  #a ()
  {
    log_message("\nclose_tab_row_if_in_row --> ");

    if ( $scan_proc->{current_tab_env} eq '_tabular_row' )
      {
	log_message("YES\n");
	close_tab_row();
      }
    else
      {
	log_message("NO\n");
      }
  }









# ******************************************************************************************
#
#  Command and environment tables
#
# ******************************************************************************************



$tabular_env_table->{tabular}
  = {
     'num_opt_args' => 1,
     'num_man_args' => 1,
     'begin_function' => \&begin_tabular,
     'end_function' => \&close_tabular,
     'early_pre_end_hook' => \&close_all_tab_strucs,
     'valigns' => {'b' => 'bottom', 't' => 'top'},
     'doc' =>
       {
	'man_args' =>
	  [
	   ['col format', 'Format of every column in the LATEX tabular form "l|r|@{entry}|r" '],
	  ],
	'description' =>
	  'A tabular ... more later',
       }
    };



$tabular_cmd_table
  = {
     "\\"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 0,
	   "is_par_starter" => FALSE,
	   "disabled" => TRUE,
	   "execute_function" => \&execute_tab_row_end,
	   "doc" =>
	     {
	      'description' =>
	        'Ends a tabular row'
	     }
	  },
     "multicolumn"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 3,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_multicolumn,
	   "no_tab_elements_autostart" => TRUE,
	   "doc" =>
	     {
	      'man_args' =>
	        [
	         ['number', 'Number of cols to span'],
	         ['c_format', 'Format of the resultig cell i.e. |l| '],
	         ['content', 'Content of the cell'],
	        ],
	      'description' =>
                'Spans current tabular cell over %{1} cols. ',
	     }
	  },
   "hline"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 0,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_hline,
	   "no_tab_elements_autostart" => TRUE,
	   "doc" =>
	     {
	      'description' => 'Creates a horizontal line',
	     }
	  },
    };









# ******************************************************************************************
#
# Initializing
#
# ******************************************************************************************

$lib_table->{tabular}->{initializer} = sub
  {
    deploy_lib
      (
       'tabular', $tabular_cmd_table, $tabular_env_table,
       FALSE,
       {
        'TOPLEVEL' => ['tabular'],
        'TABULAR_TOPLEVEL' => ['tabular'],
       }
      );

    # tab_sep token
    $scan_proc->{token_table}->{tab_sep}->{handler} = \&handle_tab_cell_sep;
    $scan_proc->{token_table}->{tab_sep}->{tester} = sub { test_regexp("&") };

    # Prevent tabular from autostart rows or cells when scanning one of
    # the following tab_elements
    my @no_tab_elements_autostart_token_types =
      (
       'tab_sep',
       'opt_arg',
       'man_arg',
       'math_tab_sep',
       'ign_whitesp',
       'nonpar_whitesp',
       'cmd',
       'one_char_cmd',
       'comment',
       'math_ign_whitesp',
       'tab_newline'
      );
    foreach my $token_type (@no_tab_elements_autostart_token_types)
      {
	$scan_proc->{token_table}->{$token_type}->{no_tab_elements_autostart} = TRUE;
      }
  };

























# ******************************************************************************************
#
#  Special tabular function, implementation different to all other table environments
#
# ******************************************************************************************


# Overview:
#
# tabular_align_char_handler    |   concat_raw_border   |  check_for_remaining_border_cells
#
# These three function parse the align information, the first collects border information until it
# finds a align cell, then it concats the raw borders before the align char into one field
#
# The last check for remaining border cells needs to be done when a row is closed, to get the right
# border of the cell




sub tabular_at_cell_handler
  {
    my $last_at_cell = $scan_proc->{last_token};
    log_message("\nHandling @-cell content.\n");
    log_data("Content:", $last_at_cell);

    my $present_cell = $#{$scan_proc->{tab_cell_aligns}} + 1;
    my $position   = $scan_proc->{prim_source_offset} + pos(${$scan_proc->{source}});

    log_message("\nAdding @-cell content to tab_pre_cell_raw.\n");

	if (not defined $scan_proc->{tab_pre_cell_raw}->{content})
	  {
	    $scan_proc->{tab_pre_cell_raw}->{content} = [];
	  }

    push (@{$scan_proc->{tab_pre_cell_raw}->{content}->[$present_cell]}  , $scan_proc->{last_token});
    push (@{$scan_proc->{tab_pre_cell_raw}->{position}->[$present_cell]} , $position);

  }




# --------------------------------------------------------------------------------
#  Handler for align_chars, used in pre_scan_hook in begin_tabular
# --------------------------------------------------------------------------------

sub tabular_align_char_handler
  # Handler called from inside the new_scan_proc when parsing the align arg
  {


    # INIT                           (...STRUCTURE...)
    #       $scan_proc->{tab_pre_cell_raw}->{content}->[$cellnumber]->[$bordernumber] = "|"
    # -------------------------------------------------------------------------------------------------
    $scan_proc->{tab_pre_cell_raw}->{content}  = [] if not defined  $scan_proc->{tab_pre_cell_raw}->{content};
    $scan_proc->{tab_pre_cell_raw}->{position} = [] if not defined  $scan_proc->{tab_pre_cell_raw}->{position};



    #  Collect borders or {..} into raw_array
    # --------------------------------------------------------------------------------
    if ($scan_proc->{last_token} eq '|')
      {
	my $present_cell = $#{$scan_proc->{tab_cell_aligns}} + 1;
	my $position   = $scan_proc->{prim_source_offset} + pos(${$scan_proc->{source}});
	if (not defined $scan_proc->{tab_pre_cell_raw}->{content})
	  {
	    $scan_proc->{tab_pre_cell_raw}->{content} = [];
	  }
	push (@{$scan_proc->{tab_pre_cell_raw}->{content}->[$present_cell]}  , $scan_proc->{last_token});
	push (@{$scan_proc->{tab_pre_cell_raw}->{position}->[$present_cell]} , $position);

	log_message("\nFound border... -> ".$scan_proc->{last_token}." \n");
      }


    #  Found a cell
    # --------------------------------------------------------------------------------
    if ($scan_proc->{last_token} =~ m/[lrc]+/)
      {
	log_message("\nFound cell - Align:". $scan_proc->{last_token} . "\n");

	my $present_cell = $#{$scan_proc->{tab_cell_aligns}} + 1;

	#  Safe align value
	# --------------------------------------------------------------------------------
	push (@{$scan_proc->{tab_cell_aligns}}, $scan_proc->{last_token});


	#  Concat tab_pre_cell to create ONE border function
	# --------------------------------------------------------------------------------
	$scan_proc->{tab_pre_cell}->[$present_cell] = \&concat_raw_borders;
      }
  }



sub concat_raw_borders
  {
    my $present_cell = $#{$scan_proc->{tab_cell_aligns}} + 1;
    if (defined @{$scan_proc->{tab_pre_cell_raw}->{content}->[$present_cell]})
      {
	log_message("\nCreating border cells: (1/2)\n");
	
	start_xml_element("tabular_border_table",FALSE,"SEMI_DISPLAY");
	my $border_count = 0;

	foreach  my $pres_brd_content (@{$scan_proc->{tab_pre_cell_raw}->{content}->[$present_cell]})
	  {
	    # Simple border
	    # --------------------------------------------------------------------------------
	    if ($pres_brd_content eq "|")
	      {
		log_message("\nFound | border (Nr. $border_count)\n");
		empty_xml_element('t_b');
	      }

	    # @-Cell Content {....}
	    # --------------------------------------------------------------------------------
	    else
	      {
		my $at_cell_content = $pres_brd_content;
		log_message("\nFound @-Cell border (Nr. $border_count) = $at_cell_content \n");
		my $prim_source_offset = ${$scan_proc->{tab_pre_cell_raw}->{position}}[$border_count];
		start_xml_element('at_cell');
		convert_subexp (\$at_cell_content,
				$prim_source_offset,
				FALSE,
				sub {
                            				   log_message("\nNo autostart in @-cell  !!\n");
    				                           $scan_proc->{pre_token_handler}     = sub {};
							   $scan_proc->{pre_cmd_hook}          = sub {};
							   $scan_proc->{pre_env_begin_hook}    = sub {};
				    },
				sub{},
				$namespace_opt,
				"SHARED_OUTPUT_LIST");
		close_xml_element('at_cell');
	      }
	    $border_count++;
	  }
	close_xml_element("tabular_border_table");
      }
  }




sub check_for_remaining_border_cells
  #a ()
  {
    log_message("\nCheck_for_remaining_border_cells (1/2)\n");

    # Get current col number
    #-------------------------------------------------------------------------------------
    my $current_col =  $scan_proc->{tab_col_num};

    # Test for remaining border cells..
    #-------------------------------------------------------------------------------------
    my $remaining_cell_func = @{$scan_proc->{tab_pre_cell}}[$current_col + 1];

    # Execute
    #-------------------------------------------------------------------------------------
    if (ref $remaining_cell_func eq "CODE")
      {
	&{$remaining_cell_func}();
      }

    log_message("Check_for_remaining_border_cells (2/2)\n");
  }








# --------------------------------------------------------------------------------
#  The \multicolumn command
# --------------------------------------------------------------------------------

sub execute_multicolumn
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_multicolumn\n");


    # Get the numeric rowspan value (number of cells to span)
    #------------------------------------------------------------------------------
    my $colspan = get_data_from_arg(0, $man_args, $pos_man_args, "CMD");
    $colspan =~ s/^\s*|\s*$//g;
    if ( ( $colspan !~ m/^[0-9]+$/ ) || ( $colspan <= 0 ) )
      {
	&{$scan_proc->{error_handler}}("Invalid multicolumn (colspan) value: $colspan");
      }
    $scan_proc->{tab_multicolumn}->{span} = $colspan;




    # Parse align information (1/5)  -> INIT
    #------------------------------------------------------------------------------
    $scan_proc->{tab_multicolumn}->{pre_border}  = [];
    $scan_proc->{tab_multicolumn}->{cell_align}  = "";
    $scan_proc->{tab_multicolumn}->{post_border} = [];
    $scan_proc->{tab_multicolumn}->{at_cell_pos} = [];



    # Parse align information (2/5)  -> Setup tokens and pre/post scan hooks
    # Put results in PRE while align not set, then in POST, remember pos of @-cells
    #------------------------------------------------------------------------------


    # @ Sign, ignore it
    #------------------------------------------------------------------------------
    $scan_proc->{token_table}->{tab_multicol_at_sign}->{tester}  = sub {test_regexp("[@]");};
    $scan_proc->{token_table}->{tab_multicol_at_sign}->{handler} = sub {};


    # | or @{cell}
    #------------------------------------------------------------------------------
    $scan_proc->{token_table}->{tab_multicol_border}->{tester}  = sub
      {
	test_regexp("[|]") or test_balanced("(?<!\\\\){", "(?<!\\\\)}");
      };

    $scan_proc->{token_table}->{tab_multicol_border}->{handler} = sub
      {
	my $last_token = $scan_proc->{last_token};

	push(@{$scan_proc->{tab_multicolumn}->{pre_border}},  $last_token) if not $scan_proc->{tab_multicolumn}->{cell_align};
	push(@{$scan_proc->{tab_multicolumn}->{post_border}}, $last_token) if     $scan_proc->{tab_multicolumn}->{cell_align};

	if ($last_token ne "|") # last_token = {..}
	  {
	    my $position   = $scan_proc->{prim_source_offset} + pos(${$scan_proc->{source}});
	    push(@{$scan_proc->{tab_multicolumn}->{at_cell_pos}}, $position);
	    $scan_proc->{allowed_tokens} = ["tab_multicol_at_sign", @{$scan_proc->{allowed_tokens}}];
	  }
      };

    # Align char
    #------------------------------------------------------------------------------
    $scan_proc->{token_table}->{tab_multicol_align}->{tester}  = sub { test_regexp("[rlc]") };
    $scan_proc->{token_table}->{tab_multicol_align}->{handler} = sub
     {
	$scan_proc->{tab_multicolumn}->{cell_align}  =  $scan_proc->{last_token};
     };

    my $pre_scan_hook   = sub {
                                log_message("\nNo autostart in aling-info  !!\n");
				$scan_proc->{pre_token_handler}     = sub {};
				$scan_proc->{pre_cmd_hook}          = sub {};
				$scan_proc->{pre_env_begin_hook}    = sub {};
                                $scan_proc->{allowed_tokens} = ["tab_multicol_border",
								"tab_multicol_align",
								"tab_multicol_at_sign" ]; };

    my $post_scan_hook  = sub { to_parent_scanproc("->{tab_multicolumn}");     };


    # Parse align information (3/5)  -> Scan
    #------------------------------------------------------------------------------
    my $align_and_borders =  convert_arg(1,
					 $man_args,
					 $pos_man_args,
					 'CMD',
					 $pre_scan_hook,
					 $post_scan_hook,
					 FALSE,
					 FALSE,
					 'SHARED_OUTPUT_LIST');



    # Parse align information (4/5)  -> Handle PRE part
    #------------------------------------------------------------------------------
    my $at_cell_count = 0;
    $scan_proc->{tab_multicolumn}->{bordercell} = sub
      {
	if (defined @{$scan_proc->{tab_multicolumn}->{pre_border}})
	  {
	    log_message("\nCreating multicolumn border cells: (1/2)\n");
	    start_xml_element("tabular_border_table",FALSE,"SEMI_DISPLAY");
	    foreach  my $multicol_border (@{$scan_proc->{tab_multicolumn}->{pre_border}})
	      {
		if ($multicol_border eq "|")
		  {
		    log_message("\nFound | border\n");
		    empty_xml_element('t_b');
		  }
		else
		  {
		    my $at_cell_content = $multicol_border;
		    my $prim_source_offset = ${$scan_proc->{tab_multicolumn}->{at_cell_pos}}[$at_cell_count++];
		    log_message("\nFound @-Cell border = $at_cell_content \n");
		    start_xml_element('at_cell');
		    convert_subexp (\$at_cell_content,
				    $prim_source_offset,
				    sub {
   				                               log_message("\nNo autostart in @-cell  !!\n");
				                               $scan_proc->{pre_token_handler}     = sub {};
							       $scan_proc->{pre_cmd_hook}          = sub {};
							       $scan_proc->{pre_env_begin_hook}    = sub {};
					},
				    sub{},                #  $post_scan_hook
				    $namespace_opt,
				    "SHARED_OUTPUT_LIST");
		    close_xml_element('at_cell');
		  }
	      }
	    close_xml_element("tabular_border_table");
	    log_message("\nCreating multicolumn border cells: (2/2)\n");
	  }
      };



    # Get the present cell number
    #------------------------------------------------------------------------------
    my $present_cell = $scan_proc->{tab_col_num};

    start_tab_row() if ($scan_proc->{current_env} ne "_tabular_row");
    start_tab_cell();




    # Parse align information (5/5)  -> Handle POST part (will be executed by next cell)
    #------------------------------------------------------------------------------
    $at_cell_count = 0;
    $scan_proc->{tab_multicolumn}->{bordercell_info} = join("",@{$scan_proc->{tab_multicolumn}->{post_border}}) || "";
    $scan_proc->{tab_multicolumn}->{bordercell} = sub
      {
	if (defined @{$scan_proc->{tab_multicolumn}->{post_border}})
	  {
	    start_xml_element("tabular_border_table",FALSE,"SEMI_DISPLAY");
	    foreach  my $multicol_border (@{$scan_proc->{tab_multicolumn}->{post_border}})
	      {
		if ($multicol_border eq "|")
		  {
		    empty_xml_element('t_b');
		  }
		else
		  {
		    my $at_cell_content = $multicol_border;
		    my $prim_source_offset = ${$scan_proc->{tab_multicolumn}->{at_cell_pos}}[$at_cell_count++];
		    start_xml_element('at_cell');
		    convert_subexp (\$at_cell_content,
				    $prim_source_offset,
				    sub {
                                 			       log_message("\nNo autostart in @-cell  !!\n");
				                               $scan_proc->{pre_token_handler}     = sub {};
							       $scan_proc->{pre_cmd_hook}          = sub {};
							       $scan_proc->{pre_env_begin_hook}    = sub {};
				        },
				    FALSE,                #  $post_scan_hook
				    $namespace_opt,
				    "SHARED_OUTPUT_LIST");
		    close_xml_element('at_cell');
		  }
	      }
	    close_xml_element("tabular_border_table");
	  }
      };

  }




# --------------------------------------------------------------------------------
# The \colspan command
# --------------------------------------------------------------------------------

sub execute_hline
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    start_tab_row_if_in_tabular();

    if ( $scan_proc->{current_env} ne '_tabular_row' )
      {
	&{$scan_proc->{error_handler}}
	  ("The \\hline command should only appear immediatly after the beginning of a new line");
      }

    empty_xml_element('h_line');


    # Closing tabrow (normally with execute_tab_row_end)
    #-------------------------------------------------------------------------------------
    reset_scan_proc();
    close_xml_element("tabrow", "DISPLAY");



  }



return(TRUE);
