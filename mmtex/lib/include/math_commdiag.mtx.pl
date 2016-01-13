package Mumie::MmTeX::Converter;

# Author: Christian Ruppert <ruppert@math.tu-berlin.de>
#
# $Id: math_commdiag.mtx.pl,v 1.8 2007/07/11 15:56:14 grudzin Exp $

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

log_message("\nLibrary math_commdiag ", 'new', "\n");

# ------------------------------------------------------------------------------------------
#  Auxiliaries
# ------------------------------------------------------------------------------------------

my @commdiag_scan_proc_keys =
  (
   "commdiag"
  );

# ------------------------------------------------------------------------------------------
#  Logging
# ------------------------------------------------------------------------------------------

sub log_math_commdiag_data
  {
    log_message("\n\n\n" . "-" x 50 . "\n");
    log_message("Commdiag data:");
    log_message("\n" . "-" x 50 . "\n");
    log_data
      (
       "Nesting", $math_commdiag_nesting_depth,
       "Class", $scan_proc->{commdiag}->{class},
       "Start env", $scan_proc->{commdiag}->{start_env},
       "Current env", $scan_proc->{current_env},
       "Row", $scan_proc->{commdiag}->{row_num},
       "Column", $scan_proc->{commdiag}->{col_num},
      );

    log_data
      (
       "Left paren", $scan_proc->{commdiag_left_paren},
       "Rigth paren", $scan_proc->{commdiag_right_paren},
      ) if ($scan_proc->{commdiag_left_paren} || $scan_proc->{commdiag_right_paren});



    log_message("-" x 50 . "\n\n\n");
  }







# ******************************************************************************************
#
#  The begin_commdiag function
#
# ******************************************************************************************


sub begin_math_commdiag
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nbegin_math_commdiag 1/2\n");
    log_math_commdiag_data();


    # Increase nesting
    #-----------------------------------------------------------------------------
    $math_commdiag_nesting_depth++;




    # Removing commdiag data from scan process, if any
    #-----------------------------------------------------------------------------
    foreach my $key (@math_commdiag_scan_proc_keys)
      {
	delete($scan_proc->{$key});
      }


    # Setting the default class
    #-----------------------------------------------------------------------------
    $scan_proc->{commdiag}->{class} =  $special_commdiag_class || "math-commdiag";


    # It is convienient to start another scan process
    #-----------------------------------------------------------------------------
    new_scan_proc('COPY', 'SHARED_NAMESPACE');


    # New, empty math node list
    #-----------------------------------------------------------------------------
    $scan_proc->{math_node_list} = [];


    # Storing the commdiag environment name
    #-----------------------------------------------------------------------------
    $scan_proc->{commdiag}->{start_env} = $scan_proc->{current_env};




    # Setting up tokens. Note that a copy of list of allowed tokens is made.
    #-----------------------------------------------------------------------------
    $scan_proc->{allowed_tokens}
      = ["ign_whitesp",  "math_mcd_sep", @{$scan_proc->{allowed_tokens}}];



    # Remove extensible arrow as own token, will be used in 'math_cd_sep'
    #-----------------------------------------------------------------------------
    $scan_proc->{allowed_tokens} =
      [ grep {not ($_ eq "math_extensible_arrow")}  @{$scan_proc->{allowed_tokens}}];



    # Setting up paragraph handling
    #-----------------------------------------------------------------------------
    $scan_proc->{par_enabled} = FALSE;



    # Ensuring element autostart
    #-----------------------------------------------------------------------------
    $scan_proc->{pre_token_handler}        = sub
                                                  {
						    check_commdiag_elements_before_token();
						    check_par_before_token();
						  };
    $scan_proc->{pre_cmd_hook}             = sub
                                                  {
						    my $cmd_name = $_[0];
						    check_commdiag_elements_before_cmd($cmd_name);
						    check_par_before_cmd($cmd_name);
						  };
    $scan_proc->{pre_env_begin_hook}      = sub
                                                  {
						    my $env_name = $_[0];
						    check_commdiag_elements_before_env($env_name);
						    check_par_before_env($env_name);
						  };



    # Importing special commands valid only within the commdiag environment
    #-----------------------------------------------------------------------------
    install_cmds_from_all_libs('MATH_COMMDIAG');


    # Allow the up_down mode for extensible arrows (will change behavior of tester)
    #-----------------------------------------------------------------------------
    $scan_proc->{extensible_arrow}->{allow_up_down} = TRUE;


    # Allowing '\end{math_commdiag}'
    #-----------------------------------------------------------------------------
    $scan_proc->{env_table}->{math_commdiag}->{end_disabled} = FALSE;

    log_message("\nstart_math_commdiag 2/2\n");
  }











# ******************************************************************************************
#
#  Closing the math_commdiag
#
# ******************************************************************************************



sub close_all_commdiag_strucs
  {
    log_message("\nclose_all_mcd_strucs 1/2\n");

    # Closing commdiag structures if necessary
    #-----------------------------------------------------------------------------
    close_commdiag_cell_if_in_cell();
    close_commdiag_row_if_in_row();

    log_message("\nclose_all_mcd_strucs 2/2\n");
  }


sub end_math_commdiag
  {
    log_message("\nend_math_commdiag 1/2\n");

    # Math node
    #-----------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'mtable';
    $node->{class} = $scan_proc->{commdiag}->{class};
    $node->{align} = $scan_proc->{align} if $scan_proc->{align};
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan proc
    #-----------------------------------------------------------------------------
    reset_scan_proc();

    # Appending math node
    #-----------------------------------------------------------------------------
    append_math_node($node);

    # Updating commdiag nesting depth variable
    #-----------------------------------------------------------------------------
    $math_commdiag_nesting_depth--;

    log_message("\nend_math_commdiag 2/2\n");
    log_math_commdiag_data();
  }






# ******************************************************************************************
#
#  Start and close a commdiag row
#
# ******************************************************************************************



sub start_commdiag_row
  #a ()
  # Starts a commdiag row.
  {
    log_message("\nstart_commdiag_row 1/2\n");
    log_math_commdiag_data();

    # Checking nesting
    #----------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne  $scan_proc->{commdiag}->{start_env} )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a commdiag row can be opened.");
      }


    # Updating row number (note that this is done before new_scan_proc())
    #----------------------------------------------------------------------------
    $scan_proc->{commdiag}->{row_num}++;


    # Starting and setting up new scan process
    #----------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_math_commdiag_row';


    # Initializing column number
    #----------------------------------------------------------------------------
    $scan_proc->{commdiag}->{col_num} = -1;


    log_message("\nstart_commdiag_row 2/2\n");
    log_math_commdiag_data();
  }





sub close_commdiag_row
  {
    log_message("\nclose_commdiag_row 1/2\n");
    log_math_commdiag_data();

    # Checking nesting
    #----------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_math_commdiag_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within commdiag row.");
      }


    # Math node
    #-----------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'table_row';
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan process
    #-----------------------------------------------------------------------------
    reset_scan_proc();

    # Appending math node
    #-----------------------------------------------------------------------------
    append_math_node($node);

    log_message("\nclose_commdiag_row 2/2\n");
    log_math_commdiag_data();
  }






# ******************************************************************************************
#
#  Start and close a commdiag cell
#
# ******************************************************************************************





sub start_commdiag_cell
  #a ()
  # Starts a commdiag cell.
  {
    log_message("\nstart_commdiag_cell 1/2\n");
    log_math_commdiag_data();

    # Checking nesting
    #-------------------------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_math_commdiag_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a commdiag cell can be opened.");
      }

    # Updating column number
    #-------------------------------------------------------------------------------------------------------
    $scan_proc->{commdiag}->{col_num}++;



    # Setting up the new scan process
    #-------------------------------------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    disallow_tokens(['ign_whitesp'], 'PROTECT');
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_math_commdiag_cell';



    # Allow the \\ command
    #-------------------------------------------------------------------------------------------------------
    $scan_proc->{cmd_table}->{'\\'}->{disabled} = FALSE;


    log_message("\nstart_commdiag_cell 2/2\n");
    log_math_commdiag_data();
  }





sub close_commdiag_cell
  {
    log_message("\nclose_commdiag_cell 1/2\n");
    log_math_commdiag_data();

    # Checking correct nesting
    #-------------------------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne "_math_commdiag_cell" )
      {
	&{$scan_proc->{error_handler}}(appr_env_notation($scan_proc->{current_env}),
				       " must be closed within commdiag cell.");
      }

    # Math node
    #-------------------------------------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'table_cell';


    # Setting class, align, and valign to the defaults
    #-------------------------------------------------------------------------------------------------------
    $node->{align} = "center";


    # Setting the value
    #-------------------------------------------------------------------------------------------------------
    $node->{value} = $scan_proc->{math_node_list};


    # Resetting scan process
    #-------------------------------------------------------------------------------------------------------
    reset_scan_proc();


    # Appending math node to math node list
    #-------------------------------------------------------------------------------------------------------
    append_math_node($node);


    delete($scan_proc->{commdiag_cell_align});

    log_message("\nclose_commdiag_cell 2/2\n");
    log_math_commdiag_data();
  }






# ******************************************************************************************
#
#  Cell seperator (a little bit longer then normal)
#
# ******************************************************************************************

$math_token_table->{math_mcd_sep}->{handler} = \&handle_mcd_cell_sep;
$math_token_table->{math_mcd_sep}->{tester} = $math_token_table->{math_extensible_arrow}->{tester};




sub handle_mcd_cell_sep
  {
    log_message("\nhandle_math_mcd_cell_sep 1/2\n");
    log_message("Call the default ext. arrow handler: \n");


    # Disable autostart for subexpression handling (not nice)
    #-----------------------------------------------------------
    my $old_1 =    $scan_proc->{pre_token_handler}     ;
    my $old_2 =    $scan_proc->{pre_cmd_hook}          ;
    my $old_3 =    $scan_proc->{pre_env_begin_hook}    ;

    $scan_proc->{pre_token_handler}             = sub {};
    $scan_proc->{pre_cmd_hook}                  = sub {};
    $scan_proc->{pre_env_begin_hook}            = sub {};


    # Generate the arrow-node
    #---------------------------------------------
    &{$math_token_table->{math_extensible_arrow}->{handler}};


    # Get the generated math arrow node
    #---------------------------------------------
    my $arrow_node = pop(@{$scan_proc->{math_node_list}});


    # Re-enable autostart after subexpression handling
    #---------------------------------------------
    $scan_proc->{pre_token_handler}      = $old_1;
    $scan_proc->{pre_cmd_hook}           = $old_2;
    $scan_proc->{pre_env_begin_hook}     = $old_3;


    # Start and close CONTENT cell (like always)
    #---------------------------------------------
    start_commdiag_row_if_in_math_commdiag();

    my $updown_first = ($scan_proc->{extensible_arrow}->{up_down} &&
			($scan_proc->{commdiag}->{col_num} eq "-1"));

    if (not $updown_first)
      {
	start_commdiag_cell_if_in_row();
	close_commdiag_cell();
      }
    else
      {
	log_message("\nSkipping first cell in up_down mode\n");
      }

    # Start and close ARROW cell (yet empty)
    #---------------------------------------------
    start_commdiag_cell();
    close_commdiag_cell();

    # Add the content to the last node (cell node)
    #---------------------------------------------
    my $cell_node = pop(@{$scan_proc->{math_node_list}});
    $cell_node->{value} = [$arrow_node];	
    push(@{$scan_proc->{math_node_list}}, $cell_node);


    log_message("\nhandle_math_mcd_cell_sep 2/2\n");
    log_math_commdiag_data();
  }












sub execute_commdiag_row_end
  {
    log_message("\nexecute_commdiag_row\n");

    # Ensuring we are inside a commdiag cell
    start_commdiag_row_if_in_math_commdiag();
    start_commdiag_cell_if_in_row();

    # Closing the commdiag cell
    close_commdiag_cell();

    # Closing the commdiag row
    close_commdiag_row();
  }












# ******************************************************************************************
#
#  The CHECK_.._BEFORE functions
#
# ******************************************************************************************




sub check_commdiag_elements_before_token
  {
    my $token_type = $scan_proc->{last_token_type};
    unless ( $scan_proc->{token_table}->{$token_type}->{no_commdiag_elements_autostart} )
      {
	start_commdiag_elements_if_necessary();
      }
  }

sub check_commdiag_elements_before_cmd
  {
    my $cmd_name = $_[0];
    unless ( $scan_proc->{cmd_table}->{$cmd_name}->{no_commdiag_elements_autostart} )
      {
	start_commdiag_elements_if_necessary();
      }
  }

sub check_commdiag_elements_before_env
  {
    my $env_name = $_[0];
    unless ( $scan_proc->{env_table}->{$env_name}->{no_commdiag_elements_autostart} )
      {
	start_commdiag_elements_if_necessary();
      }
  }







# ******************************************************************************************
#
#  The START_.._IF   and   CLOSE_.._IF   functions
#
# ******************************************************************************************


sub start_commdiag_elements_if_necessary
  {
    log_message("\nstart_commdiag_elements_if_necessary 1/2");

    if (  ( ! $scan_proc->{commdiag_autostart_disabled} )
	  && ( ! $scan_proc->{parsing_data} ) )
      {
	my $current_env = $scan_proc->{current_env};
	if ( $current_env =~ 'CD' )
	  {
	    log_message("  --> YES (Row & Cell)\n");
	    start_commdiag_row();
	    start_commdiag_cell();
	  }
	elsif ( $current_env eq "_math_commdiag_row" )
	  {
	    log_message("  --> YES  (Cell)\n");
	    start_commdiag_cell();
	  }
	else  {log_message("  --> NO \n");  }
      }
    log_message("\nstart_commdiag_elements_if_necessary 2/2\n");
  }


sub start_commdiag_cell_if_in_row
  {
    log_message("\nstart_commdiag_cell_if_in_row");
    if ( $scan_proc->{current_env} eq '_math_commdiag_row' )
      {
	log_message(" --->  YES \n");	start_commdiag_cell();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub close_commdiag_cell_if_in_cell
  {
    log_message("\nclose_commdiag_cell_if_in_cell");

    if ( $scan_proc->{current_env} eq '_math_commdiag_cell' )
      {
	log_message(" --->  YES \n");	close_commdiag_cell();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub start_commdiag_row_if_in_math_commdiag
  {
    log_message("\nstart_commdiag_row_if_in_math_commdiag");
    if ( $scan_proc->{current_env} eq $scan_proc->{commdiag}->{start_env} )
      {
	log_message(" --->  YES \n");	start_commdiag_row();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub close_commdiag_row_if_in_row
  {
    log_message("\nclose_commdiag_row_if_in_row");

    if ( $scan_proc->{current_env} eq '_math_commdiag_row' )
      {
	log_message(" --->  YES \n");	close_commdiag_row();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }







# ******************************************************************************************
#
#  Command and environment tables
#
# ******************************************************************************************

$math_commdiag_env_table->{CD}
  = {
     'num_opt_args' => 0,
     'num_man_args' => 0,
     'begin_function' => \&begin_math_commdiag,
     'end_function' => \&end_math_commdiag,
     'early_pre_end_hook' => \&close_all_commdiag_strucs,
     'doc' =>
       {
	'description' => '',
       }
    };



$math_commdiag_cmd_table
  = {
     "\\"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 0,
	   "is_par_starter" => FALSE,
	   "disabled" => TRUE,
	   "execute_function" => \&execute_commdiag_row_end,
	   "doc" =>
	     {
	      'description' =>
	        'Ends a commdiag row'
	     }
	  },
    };




# ******************************************************************************************
#
# Initializing
#
# ******************************************************************************************

$lib_table->{math_commdiag}->{initializer} = sub
  {
    deploy_lib('math_commdiag', $math_commdiag_cmd_table, $math_commdiag_env_table,
	       {'MATH' => [keys(%{$math_commdiag_cmd_table})], 'MATH_COMMDIAG' => ['\\']},
	       {'MATH' => [keys(%{$math_commdiag_env_table})]});
  };

foreach my $token_type ('math_mcd_sep',
			'cmd',
			'one_char_cmd',
			'comment',
			'ign_whitesp',
			'man_arg',
			'opt_arg')
  {
    $math_token_table->{$token_type}->{no_commdiag_elements_autostart} = TRUE;
  }





return(TRUE);
