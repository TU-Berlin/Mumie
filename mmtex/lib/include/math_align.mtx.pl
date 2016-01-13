package Mumie::MmTeX::Converter;

# Author: Christian Ruppert <ruppert@math.tu-berlin.de>
#
# $Id: math_align.mtx.pl,v 1.15 2007/07/11 15:56:14 grudzin Exp $

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
#  Provides several environments, like
#
#  - align
#  - alignat
#  - multiline
#  - split
#
# They only differ slightly in their behaviour, so they all use the same basic
# functions, just with some align or numbering infos added


log_message("\nLibrary math_align ", 'new', "\n");

# ------------------------------------------------------------------------------------------
#  Auxiliaries
# ------------------------------------------------------------------------------------------

my @align_scan_proc_keys =
  (
   'start_align_env',
   'align_row_num',
   'align_col_num',
   'align_max_col_num',
   'align_autostart_disabled',
  );

# ------------------------------------------------------------------------------------------
#  Logging
# ------------------------------------------------------------------------------------------

sub log_math_align_data
  {
    log_message("\n\n\n" . "-" x 50 . "\n");
    log_message("Align data:");
    log_message("\n" . "-" x 50 . "\n");
    log_data
      (
       "Nesting", $math_align_nesting_depth,
       "Class", $scan_proc->{align_class},
       "Start env", $scan_proc->{start_align_env},
       "Current env", $scan_proc->{current_env},
       "Row", $scan_proc->{align_row_num},
       "Column", $scan_proc->{align_col_num},
       "Max column", $scan_proc->{align_max_col_num},
       "Aligns", join(',', @{$scan_proc->{align_environment}->{align}}),
      );
    log_message("-" x 50 . "\n\n\n");
  }







# ******************************************************************************************
#
#  The begin_align function
#
# ******************************************************************************************


sub begin_math_align
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nbegin_math_align 1/2\n");
    log_math_align_data();


    # Increase nesting
    #-----------------------------------------------------------------------------
    $math_align_nesting_depth++;



    # Removing align data from scan process, if any
    #-----------------------------------------------------------------------------
    foreach my $key (@math_align_scan_proc_keys)
      {
	delete($scan_proc->{$key});
      }



    # It is convienient to start another scan process
    #-----------------------------------------------------------------------------
    new_scan_proc('COPY', 'SHARED_NAMESPACE');


    # Setting the default class
    #-----------------------------------------------------------------------------
    $scan_proc->{align_class} =  $special_align_class || "math-align-environment";


    # New, empty math node list
    #-----------------------------------------------------------------------------
    $scan_proc->{math_node_list} = [];


    # Storing the align environment name
    #-----------------------------------------------------------------------------
    $scan_proc->{start_align_env} = $scan_proc->{current_env};


    # Setting up tokens. Note that a copy of list of allowed tokens is made.
    #-----------------------------------------------------------------------------
    $scan_proc->{allowed_tokens}
      = ["ign_whitesp",  "math_align_sep", @{$scan_proc->{allowed_tokens}}];



    # Setting up paragraph handling
    #-----------------------------------------------------------------------------
    $scan_proc->{par_enabled} = FALSE;


    # Ensuring element autostart
    #-----------------------------------------------------------------------------
    $scan_proc->{pre_token_handler}        = sub
                                                  {
						    check_align_elements_before_token();
						    check_par_before_token();
						  };
    $scan_proc->{pre_cmd_hook}             = sub
                                                  {
						    my $cmd_name = $_[0];
						    check_align_elements_before_cmd($cmd_name);
						    check_par_before_cmd($cmd_name);
						  };
    $scan_proc->{pre_env_begin_hook}      = sub
                                                  {
						    my $env_name = $_[0];
						    check_align_elements_before_env($env_name);
						    check_par_before_env($env_name);
						  };



    # Importing special commands valid only within the align environment
    #-----------------------------------------------------------------------------
    install_cmds_from_all_libs('MATH_ALIGN');


    # Allowing '\end{math_align}'
    #-----------------------------------------------------------------------------
    $scan_proc->{env_table}->{math_align}->{end_disabled} = FALSE;

    log_message("\nstart_math_align 2/2\n");
  }











# ******************************************************************************************
#
#  Closing the math_align
#
# ******************************************************************************************



sub close_all_align_strucs
  {
    log_message("\nclose_all_align_strucs 1/2\n");

    # Closing align structures if necessary
    #-----------------------------------------------------------------------------
    close_align_cell_if_in_cell();
    close_align_row_if_in_row();

    log_message("\nclose_all_align_strucs 2/2\n");
  }


sub end_math_align
  {
    log_message("\nend_math_align 1/2\n");

    # Math node
    #-----------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'mtable';
    $node->{class} = $scan_proc->{align_class};
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan proc
    #-----------------------------------------------------------------------------
    reset_scan_proc();

    # Appending math node
    #-----------------------------------------------------------------------------
    append_math_node($node);

    # Updating align nesting depth variable
    #-----------------------------------------------------------------------------
    $math_align_nesting_depth--;

    log_message("\nend_math_align 2/2\n");
    log_math_align_data();
  }






# ******************************************************************************************
#
#  Start and close a align row
#
# ******************************************************************************************



sub start_align_row
  #a ()
  # Starts a align row.
  {
    log_message("\nstart_align_row 1/2\n");
    log_math_align_data();

    # Checking nesting
    #----------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne  $scan_proc->{start_align_env} )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a align row can be opened.");
      }


    # Updating row number (note that this is done before new_scan_proc())
    #----------------------------------------------------------------------------
    $scan_proc->{align_row_num}++;


    # Increase equation number if in 'align' environment (not in split)
    # Split environment will be counted by the enclosing equation
    #----------------------------------------------------------------------------
    add_to_counter("equation", 1) if not $scan_proc->{start_align_env} eq "split";


    # Starting and setting up new scan process
    #----------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_math_align_row';



    # Initializing column number
    #----------------------------------------------------------------------------
    $scan_proc->{align_col_num} = -1;


    log_message("\nstart_align_row 2/2\n");
    log_math_align_data();
  }





sub close_align_row
  {
    log_message("\nclose_align_row 1/2\n");
    log_math_align_data();

    # Checking nesting
    #----------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_math_align_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within align row.");
      }

    # Checking number of cells in the row
    #-----------------------------------------------------------------------------
    my $last_col_num = $scan_proc->{align_col_num};
    my $max_col_num = $scan_proc->{align_max_col_num};
    if ( ( $max_col_num ) &&  ( $max_col_num > 0 ) && ($last_col_num > $max_col_num ) )
      {
	&{$scan_proc->{warning_handler}}("\n\nInvalid number of align cells.".
					 "\n Expected maximal:  " . $max_col_num .
					 "\n Found:             ". ($last_col_num + 1).
					 "\n\n   You should use the alignat environment if more cells needed \n");
      }


    # Math node
    #-----------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'table_row';
    $node->{value} = $scan_proc->{math_node_list};



    # Adding the equation number as attribute to mrow
    #----------------------------------------------------------------------------
    if  (not $scan_proc->{start_align_env} eq "split")
      {
	$node->{'equation-no'} = get_counter_value("equation");
      }


    # Resetting scan process
    #-----------------------------------------------------------------------------
    reset_scan_proc();


    # Appending math node
    #-----------------------------------------------------------------------------
    append_math_node($node);

    log_message("\nclose_align_row 2/2\n");
    log_math_align_data();
  }






# ******************************************************************************************
#
#  Start and close a align cell
#
# ******************************************************************************************





sub start_align_cell
  #a ()
  # Starts a align cell.
  {
    log_message("\nstart_align_cell 1/2\n");
    log_math_align_data();

    # Checking nesting
    #-------------------------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_math_align_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a align cell can be opened.");
      }

    # Updating column number
    #-------------------------------------------------------------------------------------------------------
    $scan_proc->{align_col_num}++;


    # Setting up the new scan process
    #-------------------------------------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    disallow_tokens(['ign_whitesp'], 'PROTECT');
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_math_align_cell';



    # Allow the \\ command
    #-------------------------------------------------------------------------------------------------------
    $scan_proc->{cmd_table}->{'\\'}->{disabled} = FALSE;


    log_message("\nstart_align_cell 2/2\n");
    log_math_align_data();
  }





sub close_align_cell
  {
    log_message("\nclose_align_cell 1/2\n");
    log_math_align_data();

    # Checking correct nesting
    #-------------------------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne "_math_align_cell" )
      {
	&{$scan_proc->{error_handler}}(appr_env_notation($scan_proc->{current_env}),
				       " must be closed within align cell.");
      }

    # Math node
    #-------------------------------------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'table_cell';



    # Setting class, align, and valign to the defaults
    #-------------------------------------------------------------------------------------------------------
    my $current_cell = $scan_proc->{align_col_num};
    if ($scan_proc->{start_align_env} ne "multline")
      {
	if (defined $scan_proc->{align_environment}->{align}->[$current_cell])
	  {
	    $node->{columnalign} = $scan_proc->{align_environment}->{align}->[$current_cell];
	  }
      }
    else
      {
	# Align in the multline environment
	#
	# This is more easy to set via the stylesheet, because the first row should be left align,
	# the middle ones should be centered, and the last one should be right align
	# (To find if it's the last is easy in the XSL, but hard as an MmTex 'look in front' job)
      }


    # Setting the value
    #-------------------------------------------------------------------------------------------------------
    $node->{value} = $scan_proc->{math_node_list};


    # Resetting scan process
    #-------------------------------------------------------------------------------------------------------
    reset_scan_proc();


    # Appending math node to math node list
    #-------------------------------------------------------------------------------------------------------
    append_math_node($node);


    log_message("\nclose_align_cell 2/2\n");
    log_math_align_data();
  }






# ******************************************************************************************
#
#  Cell sperator and row end handler
#
# ******************************************************************************************







sub handle_align_cell_sep
  {
    log_message("\nhandle_math_align_cell_sep 1/2\n");

    # Ensuring we are inside a align cell
    start_align_row_if_in_math_align();
    start_align_cell_if_in_row();

    # Closing current align cell
    close_align_cell();

    log_message("\nhandle_math_align_cell_sep 2/2\n");
    log_math_align_data();
  }






sub execute_align_row_end
  {
    log_message("\nexecute_align_row_end\n");
    # Ensuring we are inside a align cell
    start_align_row_if_in_math_align();
    start_align_cell_if_in_row();

    # Closing the align cell
    close_align_cell();

    # Closing the align row
    close_align_row();
  }












# ******************************************************************************************
#
#  The CHECK_.._BEFORE functions
#
# ******************************************************************************************




sub check_align_elements_before_token
  {
    my $token_type = $scan_proc->{last_token_type};
    unless ( $scan_proc->{token_table}->{$token_type}->{no_align_elements_autostart} )
      {
	start_align_elements_if_necessary();
      }
  }

sub check_align_elements_before_cmd
  {
    my $cmd_name = $_[0];
    unless ( $scan_proc->{cmd_table}->{$cmd_name}->{no_align_elements_autostart} )
      {
	start_align_elements_if_necessary();
      }
  }

sub check_align_elements_before_env
  {
    my $env_name = $_[0];
    unless ( $scan_proc->{env_table}->{$env_name}->{no_align_elements_autostart} )
      {
	start_align_elements_if_necessary();
      }
  }







# ******************************************************************************************
#
#  The START_.._IF   and   CLOSE_.._IF   functions
#
# ******************************************************************************************


sub start_align_elements_if_necessary
  {
    log_message("\nstart_align_elements_if_necessary 1/2");

    start_align_row_if_in_math_align();
    start_align_cell_if_in_row();

    log_message("\nstart_align_elements_if_necessary 2/2\n");
  }


sub start_align_cell_if_in_row
  {
    log_message("\nstart_align_cell_if_in_row");

    if ( $scan_proc->{current_env} eq '_math_align_row' )
      {
	log_message(" --->  YES \n");	start_align_cell();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub close_align_cell_if_in_cell
  {
    log_message("\nclose_align_cell_if_in_cell");

    if ( $scan_proc->{current_env} eq '_math_align_cell' )
      {
	log_message(" --->  YES \n");	close_align_cell();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub start_align_row_if_in_math_align
  {
    log_message("\nstart_align_row_if_in_math_align");
    if ( $scan_proc->{current_env} eq $scan_proc->{start_align_env} )
      {
	log_message(" --->  YES \n");	start_align_row();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub close_align_row_if_in_row
  {
    log_message("\nclose_align_row_if_in_row");

    if ( $scan_proc->{current_env} eq '_math_align_row' )
      {
	log_message(" --->  YES \n");	close_align_row();
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

$math_align_env_table->{align}
  = {
     'num_opt_args' => 0,
     'num_man_args' => 0,
     'begin_function' => sub { start_math_align();
                               $scan_proc->{align_environment}->{align} = ['right','left'];
			       $scan_proc->{align_max_col_num} = 2;
			       begin_math_align();
			     },

     'end_function' => sub {end_math_align(); close_math_align();},
     'early_pre_end_hook' => \&close_all_align_strucs,
     'doc' =>
       {
	'description' => '',
       }
    };

$math_align_env_table->{eqnarray}
  = {
     'num_opt_args' => 0,
     'num_man_args' => 0,
     'begin_function' => sub { start_math_align();
                               $scan_proc->{align_environment}->{align} = ['right','left'];
			       $scan_proc->{align_max_col_num} = 2;
			       begin_math_align();
			     },

     'end_function' => sub {end_math_align(); close_math_align();},
     'early_pre_end_hook' => \&close_all_align_strucs,
     'doc' =>
       {
	'description' => '',
       }
    };


$math_align_env_table->{alignat}
  = {
     'num_opt_args' => 0,
     'num_man_args' => 1,
     'begin_function' => sub {
                               my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

			       my $align_pairs = $man_args->[0];

                               $scan_proc->{align_environment}->{align} = [];

			       for ( my $i = 0; $i < $align_pairs; $i++)
				 {
				   push(@{$scan_proc->{align_environment}->{align}}, ('right','left'));
				 }


			       $special_align_class = "math-alignat-environment";

			       start_math_align();

			       $scan_proc->{align_max_col_num} = (2 * $align_pairs);

			       begin_math_align();
			     },

     'end_function' => sub {end_math_align(); close_math_align();},
     'early_pre_end_hook' => \&close_all_align_strucs,
     'doc' =>
       {
	'description' => '',
       }
    };





$math_align_env_table->{multline}
  = {
     'num_opt_args' => 0,
     'num_man_args' => 0,
     'begin_function' => sub {
                               start_math_align();
			       $special_align_class = "math-multline-environment";
                               $scan_proc->{align_environment}->{align} =[];   # different behavior,
			       $scan_proc->{align_max_col_num} = 1;            # see in cell function
			       begin_math_align();
			     },

     'end_function' => sub{end_math_align(); close_math_align();},
     'early_pre_end_hook' => \&close_all_align_strucs,
     'doc' =>
       {
	'description' => '',
       }
    };


$math_align_env_table->{split}
  = {
     'num_opt_args' => 0,
     'num_man_args' => 0,
     'begin_function' => sub {
                               # NEEDS a \begin{equation}, will not work without one (like the others)
			       $special_align_class = "math-split-environment";
                               $scan_proc->{align_environment}->{align} = ['right','left'];
			       $scan_proc->{align_max_col_num} = 2;
			       begin_math_align();
			     },

     'end_function' => \&end_math_align,
     'early_pre_end_hook' => \&close_all_align_strucs,
     'doc' =>
       {
	'description' => '',
       }
    };







$math_align_cmd_table
  = {
     "\\"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 0,
	   "is_par_starter" => FALSE,
	   "disabled" => TRUE,
	   "execute_function" => \&execute_align_row_end,
	   "doc" =>
	     {
	      'description' =>
	        'Ends a align row'
	     }
	  },
    };



# --------------------------------------------------------------------------------
#  Other similar environments
# --------------------------------------------------------------------------------





# ------------------------------------------------------------------------------------------
#
# Initializing
#
# --------------------------------------------------------------------------------

$lib_table->{math_align}->{initializer} = sub
  {
    my @math_cmds = keys(%{$math_align_cmd_table});
    my @math_toplevel_envs = ('eqnarray', 'align', 'alignat', 'multline');
    my @math_envs = keys(%{$math_align_env_table});
    rm_string_from_array(\@math_toplevel_envs, \@math_envs);

    deploy_lib
      (
       'math_align',
       $math_align_cmd_table,
       $math_align_env_table,
       {
        'MATH' => \@math_cmds,
        'MATH_ALIGN' => ['\\']
       },
       {
        'MATH' => \@math_envs,
        'MATH_TOPLEVEL' => \@math_toplevel_envs,
        'TOPLEVEL' => \@math_toplevel_envs
       }
      );
  };

$math_token_table->{math_align_sep}->{handler} = \&handle_align_cell_sep;
$math_token_table->{math_align_sep}->{tester} = sub {test_regexp("[&]")};

foreach my $token_type ('cmd', 'one_char_cmd',  'comment', 'man_arg','opt_arg', 'ign_whitesp', 'math_ign_whitesp')
  {
    $math_token_table->{$token_type}->{no_align_elements_autostart} = TRUE;
  }

















# --------------------------------------------------------------------------------
#
# Environment start functions
#
# --------------------------------------------------------------------------------

#  Explanation:
#  If you use \begin{align} or \begin{alignat}{3} there is no need to start an equation before,
#  so these function will do all the work, they are just nearly a copy of the start_display_math
#  function from math.mtx.pl




sub start_math_align
  {
    log_message("\nstart_display_math (caused by \\begin{align})\n");
    $scan_proc->{env_table}->{align}->{begin_disabled} = TRUE;
    start_math_mode("BLOCK", undef);
  }


sub close_math_align
  {
    log_message("\nclose_display_math (started by \\begin{align} or \\begin{alignat}\n");
    log_math_data();

    end_math_mode();
    handle_display_math();

  }

return TRUE;
