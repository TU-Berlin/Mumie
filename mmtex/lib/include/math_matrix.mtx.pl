package Mumie::MmTeX::Converter;

# Author: Christian Ruppert <ruppert@math.tu-berlin.de>
#
# $Id: math_matrix.mtx.pl,v 1.17 2007/07/11 15:56:14 grudzin Exp $

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

log_message("\nLibrary math_matrix ", 'new', "\n");

# ------------------------------------------------------------------------------------------
#  Auxiliaries
# ------------------------------------------------------------------------------------------

my @mmatrix_scan_proc_keys =
  (
   'start_mmatrix_env',
   'current_mmatrix_env',
   'mmatrix_row_num',
   'mmatrix_col_num',
   'mmatrix_max_col_num',
   'mmatrix_autostart_disabled',
  );

# ------------------------------------------------------------------------------------------
#  Logging
# ------------------------------------------------------------------------------------------

sub log_math_matrix_data
  {
    log_message("\n\n\n" . "-" x 50 . "\n");
    log_message("Matrix data:");
    log_message("\n" . "-" x 50 . "\n");
    log_data
      (
       "Nesting", $math_matrix_nesting_depth,
       "Class", $scan_proc->{matrix_class},
       "Start env", $scan_proc->{start_mmatrix_env},
       "Current mmatrix env", $scan_proc->{current_mmatrix_env},
       "Current env", $scan_proc->{current_env},
       "Row", $scan_proc->{mmatrix_row_num},
       "Column", $scan_proc->{mmatrix_col_num},
       "Max column", $scan_proc->{mmatrix_max_col_num},
       "Matrix type", $scan_proc->{matrix_type},
      );



    log_message("-" x 50 . "\n\n\n");
  }







# ******************************************************************************************
#
#  The begin_matrix function
#
# ******************************************************************************************


sub begin_math_matrix
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nbegin_math_matrix 1/2\n");
    log_math_matrix_data();



    # Increase nesting
    #-----------------------------------------------------------------------------
    $math_matrix_nesting_depth++;


    # Setting the max_col_num
    #-------------------------------------------------------------------------------------------------------
    $scan_proc->{mmatrix_max_col_num} =   $scan_proc->{cnt_table}->{MaxMatrixCols}->{value};



    # Removing matrix data from scan process, if any
    #-----------------------------------------------------------------------------
    foreach my $key (@math_matrix_scan_proc_keys)
      {
	delete($scan_proc->{$key});
      }


    # Setting the default class
    #-----------------------------------------------------------------------------
    $scan_proc->{matrix_class} =  $special_matrix_class || "math-matrix";


    # It is convienient to start another scan process
    #-----------------------------------------------------------------------------
    new_scan_proc('COPY', 'SHARED_NAMESPACE');



    # New, empty math node list
    #-----------------------------------------------------------------------------
    $scan_proc->{math_node_list} = [];



    # Storing the matrix environment name
    #-----------------------------------------------------------------------------
    $scan_proc->{start_mmatrix_env} = $scan_proc->{current_env};



    # Setting the current matrix environment
    #-----------------------------------------------------------------------------
    $scan_proc->{current_mmatrix_env} = $scan_proc->{start_mmatrix_env};



    # Setting up tokens. Note that a copy of list of allowed tokens is made.
    #-----------------------------------------------------------------------------
    $scan_proc->{allowed_tokens}
      = ["ign_whitesp",  "math_mtx_sep", @{$scan_proc->{allowed_tokens}}];


    # Adding the mtx seperator for ematrix (|)
    #-----------------------------------------------------------------------------
    if (defined $scan_proc->{token_table}->{math_emtx_sep})
      {
	$scan_proc->{allowed_tokens}
	  = ["math_emtx_sep", @{$scan_proc->{allowed_tokens}}];
      }


    # Setting up paragraph handling
    #-----------------------------------------------------------------------------
    $scan_proc->{par_enabled} = FALSE;



    # Ensuring element autostart
    #-----------------------------------------------------------------------------
    $scan_proc->{pre_token_handler}        = sub
                                                  {
						    check_mmatrix_elements_before_token();
						    check_par_before_token();
						  };
    $scan_proc->{pre_cmd_hook}             = sub
                                                  {
						    my $cmd_name = $_[0];
						    check_mmatrix_elements_before_cmd($cmd_name);
						    check_par_before_cmd($cmd_name);
						  };
    $scan_proc->{pre_env_begin_hook}      = sub
                                                  {
						    my $env_name = $_[0];
						    check_mmatrix_elements_before_env($env_name);
						    check_par_before_env($env_name);
						  };



    # Importing special commands valid only within the matrix environment
    #-----------------------------------------------------------------------------
    install_cmds_from_all_libs('MATH_MATRIX');


    # Allowing '\end{math_matrix}'
    #-----------------------------------------------------------------------------
    $scan_proc->{env_table}->{math_matrix}->{end_disabled} = FALSE;

    log_message("\nstart_math_matrix 2/2\n");
  }











# ******************************************************************************************
#
#  Closing the math_matrix
#
# ******************************************************************************************



sub close_all_mmatrix_strucs
  {
    log_message("\nclose_all_mtx_strucs 1/2\n");

    # Closing matrix structures if necessary
    #-----------------------------------------------------------------------------
    close_mmatrix_cell_if_in_cell();
    close_mmatrix_row_if_in_row();

    log_message("\nclose_all_mtx_strucs 2/2\n");
  }


sub end_math_matrix
  {
    log_message("\nend_math_matrix 1/2\n");

    # Math node
    #-----------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'mtable';
    $node->{class} = $scan_proc->{matrix_class};
    $node->{align} = $scan_proc->{align} if  $scan_proc->{align};
    $node->{valign} = $scan_proc->{valign} if $scan_proc->{valign};
    $node->{value} = $scan_proc->{math_node_list};

    # Resetting scan proc
    #-----------------------------------------------------------------------------
    reset_scan_proc();

    # Appending math node
    #-----------------------------------------------------------------------------
    append_math_node($node);

    # Updating matrix nesting depth variable
    #-----------------------------------------------------------------------------
    $math_matrix_nesting_depth--;

    log_message("\nend_math_matrix 2/2\n");
    log_math_matrix_data();
  }






# ******************************************************************************************
#
#  Start and close a matrix row
#
# ******************************************************************************************



sub start_mmatrix_row
  #a ()
  # Starts a matrix row.
  {
    log_message("\nstart_mmatrix_row 1/2\n");
    log_math_matrix_data();

    # Checking nesting
    #----------------------------------------------------------------------------
    my $start_env = $scan_proc->{start_mmatrix_env} || '';
    if ( $scan_proc->{current_env} ne  $scan_proc->{start_mmatrix_env} )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a matrix row can be opened.");
      }


    # Updating row number (note that this is done before new_scan_proc())
    #----------------------------------------------------------------------------
    $scan_proc->{mmatrix_row_num}++;



    # Starting and setting up new scan process
    #----------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_math_matrix_row';
    $scan_proc->{current_mmatrix_env} = '_math_matrix_row';

    # Initializing column number
    #----------------------------------------------------------------------------
    $scan_proc->{mmatrix_col_num} = -1;


    log_message("\nstart_mmatrix_row 2/2\n");
    log_math_matrix_data();
  }





sub close_mmatrix_row
  {
    log_message("\nclose_mmatrix_row 1/2\n");
    log_math_matrix_data();

    # Checking nesting
    #----------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_math_matrix_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}("$env must be closed within matrix row.");
      }

    # Checking number of cells in the row
    #-----------------------------------------------------------------------------
    my $last_col_num = $scan_proc->{mmatrix_col_num};
    my $max_col_num = $scan_proc->{mmatrix_max_col_num};
    if ( ( $max_col_num ) &&  ( $max_col_num > 0 ) && ($last_col_num > $max_col_num ) )
      {
	&{$scan_proc->{warning_handler}}("\n\nInvalid number of matrix cells.".
					 "\n Expected maximal:  " . $max_col_num .
					 "\n Found:             ". ($last_col_num + 1).
					 "\n\n   You should set the MaxMatrixCols to a higher value".
					 "\n   using the setcounter{MaxMatrixCols}{15} command.".
					 "\n   This is just for compability to latex, MmTex will work without".
					 "\n   setting this value, but it will warn like now..\n");
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

    log_message("\nclose_mmatrix_row 2/2\n");
    log_math_matrix_data();
  }






# ******************************************************************************************
#
#  Start and close a matrix cell
#
# ******************************************************************************************





sub start_mmatrix_cell
  #a ()
  # Starts a matrix cell.
  {
    log_message("\nstart_mmatrix_cell 1/2\n");
    log_math_matrix_data();

    # Checking nesting
    #-------------------------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne '_math_matrix_row' )
      {
	my $env = appr_env_notation($scan_proc->{current_env});
	&{$scan_proc->{error_handler}}
	  ("$env must be closed before a matrix cell can be opened.");
      }

    # Updating column number
    #-------------------------------------------------------------------------------------------------------
    $scan_proc->{mmatrix_col_num}++;



    # Setting up the new scan process
    #-------------------------------------------------------------------------------------------------------
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    disallow_tokens(['ign_whitesp'], 'PROTECT');
    $scan_proc->{math_node_list} = [];
    $scan_proc->{current_env} = '_math_matrix_cell';
    $scan_proc->{current_mmatrix_env} = '_math_matrix_cell';


    log_message("\nstart_mmatrix_cell 2/2\n");
    log_math_matrix_data();
  }





sub close_mmatrix_cell
  {
    log_message("\nclose_mmatrix_cell 1/2\n");
    log_math_matrix_data();

    # Checking correct nesting
    #-------------------------------------------------------------------------------------------------------
    if ( $scan_proc->{current_env} ne "_math_matrix_cell" )
      {
	&{$scan_proc->{error_handler}}(appr_env_notation($scan_proc->{current_env}),
				       " must be closed within matrix cell.");
      }

    # Math node
    #-------------------------------------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'table_cell';

    # Setting class, align, and valign to the defaults
    #-------------------------------------------------------------------------------------------------------
    $node->{align} = "center";
    if (defined $scan_proc->{matrix_cell_class})
      {
	$node->{class} = $scan_proc->{matrix_cell_class};
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


    delete($scan_proc->{mmatrix_cell_align});

    log_message("\nclose_mmatrix_cell 2/2\n");
    log_math_matrix_data();
  }






# ******************************************************************************************
#
#  Cell sperator and row end handler
#
# ******************************************************************************************







sub handle_mtx_cell_sep
  {
    log_message("\nhandle_math_mtx_cell_sep 1/2\n");

    # Ensuring we are inside a matrix cell
    start_mmatrix_row_if_in_math_matrix();
    start_mmatrix_cell_if_in_row();

    # Closing current matrix cell
    close_mmatrix_cell();

    log_message("\nhandle_math_mtx_cell_sep 2/2\n");
    log_math_matrix_data();
  }


sub handle_emtx_cell_sep
  {
    log_message("\nhandle_math_emtx_cell_sep 1/2\n");

    # Ensuring we are inside a matrix cell
    start_mmatrix_row_if_in_math_matrix();
    start_mmatrix_cell_if_in_row();

    # Setting a class for the current cell
    $scan_proc->{matrix_cell_class} = "pre-vertical-line";

    # Closing current matrix cell
    close_mmatrix_cell();

    log_message("\nhandle_math_emtx_cell_sep 2/2\n");
    log_math_matrix_data();
  }






sub execute_mmatrix_row_end
  {
    log_message("\nexecute_mmatrix_row\n");

    # Ensuring we are inside a matrix cell
    start_mmatrix_row_if_in_math_matrix();
    start_mmatrix_cell_if_in_row();

    # Closing the matrix cell
    close_mmatrix_cell();

    # Closing the matrix row
    close_mmatrix_row();
  }





sub execute_hdotsfor
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("execute_hdotsfor 1/2\n");

    my $col_number = $man_args->[0] || "1";
    if ($col_number =~ m/[^0-9]+/g)
      {
	&{$scan_proc->{error_handler}}
	  ("Mandatory argument scope: 1 - 9999");
      }



    my $i = 0;

    while ($i+1 < $col_number)
      {
	log_message("execute_hdotsfor Nr. $i \n");
	start_mmatrix_elements_if_necessary();

	# Math node
	#-----------------------------------------------------------------------------
	my $node = new_math_node();
	$node->{type} = 'identifier';
	$node->{value} = '&hellip;';

	# Appending math node
	#-----------------------------------------------------------------------------
	append_math_node($node);

	close_mmatrix_cell();
	$i++;
      }

    # Special handling for the last dots, NO CLOSING
    #-----------------------------------------------------------------------------
    log_message("execute_hdotsfor Nr. $i \n");
    start_mmatrix_elements_if_necessary();
    # Math node
    #-----------------------------------------------------------------------------
    my $node = new_math_node();
    $node->{type} = 'identifier';
    $node->{value} = '&hellip;';

    # Appending math node
    #-----------------------------------------------------------------------------
    append_math_node($node);





}









# ******************************************************************************************
#
#  The CHECK_.._BEFORE functions
#
# ******************************************************************************************




sub check_mmatrix_elements_before_token
  {
    my $token_type = $scan_proc->{last_token_type};
    unless ( $scan_proc->{token_table}->{$token_type}->{no_mmatrix_elements_autostart} )
      {
	start_mmatrix_elements_if_necessary();
      }
  }

sub check_mmatrix_elements_before_cmd
  {
    my $cmd_name = $_[0];
    unless ( $scan_proc->{cmd_table}->{$cmd_name}->{no_mmatrix_elements_autostart} )
      {
	start_mmatrix_elements_if_necessary();
      }
  }

sub check_mmatrix_elements_before_env
  {
    my $env_name = $_[0];
    unless ( $scan_proc->{env_table}->{$env_name}->{no_mmatrix_elements_autostart} )
      {
	start_mmatrix_elements_if_necessary();
      }
  }







# ******************************************************************************************
#
#  The START_.._IF   and   CLOSE_.._IF   functions
#
# ******************************************************************************************


sub start_mmatrix_elements_if_necessary
  {
    log_message("\nstart_mmatrix_elements_if_necessary 1/2");

    if (  ( ! $scan_proc->{mmatrix_autostart_disabled} )
	  && ( ! $scan_proc->{parsing_data} ) )
      {
	my $start_env   = $scan_proc->{start_mmatrix_env};
	my $current_env = $scan_proc->{current_env};

	if ($current_env eq $start_env)
	  {
	    log_message("  --> YES (Row & Cell)\n");
	    start_mmatrix_row();
	    start_mmatrix_cell();
	  }
	elsif ( $current_env eq "_math_matrix_row" )
	  {
	    log_message("  --> YES  (Cell)\n");
	    start_mmatrix_cell();
	  }
	else  {log_message("  --> NO \n");  }
      }
    log_message("\nstart_mmatrix_elements_if_necessary 2/2\n");
  }


sub start_mmatrix_cell_if_in_row
  {
    log_message("\nstart_mmatrix_cell_if_in_row");

    if ( $scan_proc->{current_mmatrix_env} eq '_math_matrix_row' )
      {
	log_message(" --->  YES \n");	start_mmatrix_cell();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub close_mmatrix_cell_if_in_cell
  {
    log_message("\nclose_mmatrix_cell_if_in_cell");

    if ( $scan_proc->{current_mmatrix_env} eq '_math_matrix_cell' )
      {
	log_message(" --->  YES \n");	close_mmatrix_cell();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub start_mmatrix_row_if_in_math_matrix
  {
    log_message("\nstart_mmatrix_row_if_in_math_matrix");
    if ( $scan_proc->{current_mmatrix_env} eq $scan_proc->{start_mmatrix_env} )
      {
	log_message(" --->  YES \n");	start_mmatrix_row();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }


sub close_mmatrix_row_if_in_row
  {
    log_message("\nclose_mmatrix_row_if_in_row");

    if ( $scan_proc->{current_mmatrix_env} eq '_math_matrix_row' )
      {
	log_message(" --->  YES \n");	close_mmatrix_row();
      }
    else
      {
	log_message(" --->  NO \n");
      }
  }






# ******************************************************************************************
#
#  execute_matrix
#
# ******************************************************************************************

sub execute_matrix
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_matrix (1/2)\n");

    my $man = new_math_node();
    $man_matrix->{type} = 'mtable';
    $man_matrix->{class} = 'math-matrix';

    my $m_row = new_math_node();
    $m_row->{type}= 'table_row';

    my $m_cell = new_math_node();
    $m_cell->{type}= 'table_cell';
    $m_cell->{value} = convert_math_subexp(\ ($man_args->[0]), $pos_man_args->[0]);

    $m_row->{value} = [$m_cell];
    $man_matrix->{value} = [$m_row];


    if (defined $opt_args->[0])
      {

	my $opt_matrix = new_math_node();
	$opt_matrix->{type} = 'mtable';
	$opt_matrix->{class} = 'math-matrix';
	$opt_matrix->{left_paren} = '[';
	$opt_matrix->{right_paren} = ']';
	
	my $o_row = new_math_node();
	$o_row->{type}= 'table_row';
	
	my $o_cell = new_math_node();
	$o_cell->{type}= 'table_cell';
	$o_cell->{value} = convert_math_subexp(\ ($opt_args->[0]), $pos_opt_args->[0]);

	$o_row->{value} = [$o_cell];
	$opt_matrix->{value} = [$o_row];

	my $block = new_math_node();
	$block->{type}= 'block';
	$block->{value} = [$opt_matrix, $man_matrix];
	append_math_node($block);
      }
    else
      {
	append_math_node($man_matrix);
      }

    log_message("\nexecute_matrix (2/2)\n");
  }












# ******************************************************************************************
#
#  Command and environment tables
#
# ******************************************************************************************

$math_matrix_env_table->{matrix}
  = {
     'num_opt_args' => 0,
     'num_man_args' => 0,
     'begin_function' => sub { $scan_proc->{matrix_type} = 'default';
			       begin_math_matrix();		  },

     'end_function' => \&end_math_matrix,
     'early_pre_end_hook' => \&close_all_mmatrix_strucs,
     'doc' =>
       {
	'description' => '',
       }
    };



$math_matrix_cmd_table
  = {
     "\\"
       => {
	   "num_opt_args" => 0,
	   "num_man_args" => 0,
	   "is_par_starter" => FALSE,
	   "execute_function" => sub { execute_mmatrix_row_end(); },
	   "doc" =>
	     {
	      'description' =>
	        'Ends a matrix row'
	     }
	  },
     "matrix"
       => {
	   "num_opt_args" => 1,
	   "num_man_args" => 1,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_matrix,
	   "doc" =>
	     {
	      'description' =>
	        'Ends a matrix row'
	     }
	  },
     "hdotsfor"
        => {
	   "num_opt_args" => 0,
	   "num_man_args" => 1,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_hdotsfor,
	   "doc" =>
	     {
	      'description' =>
	        'Adds multiple cells with dots in it..'
	     }
	  },


    };



# --------------------------------------------------------------------------------
#  Other similar environments
# --------------------------------------------------------------------------------

$math_matrix_env_table->{pmatrix} = copy_value($math_matrix_env_table->{matrix});
$math_matrix_env_table->{bmatrix} = copy_value($math_matrix_env_table->{matrix});
$math_matrix_env_table->{vmatrix} = copy_value($math_matrix_env_table->{matrix});
$math_matrix_env_table->{Vmatrix} = copy_value($math_matrix_env_table->{matrix})
;
$math_matrix_env_table->{ematrix} = copy_value($math_matrix_env_table->{vmatrix});

$math_matrix_env_table->{smallmatrix} = copy_value($math_matrix_env_table->{matrix});
$math_matrix_env_table->{cases} = copy_value($math_matrix_env_table->{matrix});
$math_matrix_env_table->{Determinant} = copy_value($math_matrix_env_table->{vmatrix});


$math_matrix_env_table->{pmatrix}->{begin_function} = sub
  {
    $special_matrix_class = 'pmatrix';
    begin_math_matrix();
  };

$math_matrix_env_table->{bmatrix}->{begin_function} = sub
  {
    $special_matrix_class = 'bmatrix';
    begin_math_matrix();
  };

$math_matrix_env_table->{Vmatrix}->{begin_function} = sub
  {
    $special_matrix_class = 'Vmatrix';
    begin_math_matrix();
  };

$math_matrix_env_table->{vmatrix}->{begin_function} = sub
  {
    $special_matrix_class = 'vmatrix';
    begin_math_matrix();
  };

$math_matrix_env_table->{smallmatrix}->{begin_function} = sub
  {
    $special_matrix_class = "math-small-matrix";
    begin_math_matrix();
  };

$math_matrix_env_table->{ematrix}->{begin_function}  = sub
  {
    $special_matrix_class = "math-ematrix";
    $scan_proc->{token_table}->{math_emtx_sep}->{handler} = \&handle_emtx_cell_sep;
    $scan_proc->{token_table}->{math_emtx_sep}->{tester} = sub {test_regexp("[|]")};
    begin_math_matrix();
  };


$math_matrix_env_table->{cases}->{begin_function} = sub 
{
  $special_matrix_class = "math-cases-matrix";
  begin_math_matrix();
};







# ******************************************************************************************
#
# Initializing
#
# ******************************************************************************************

$lib_table->{math_matrix}->{initializer} = sub
  {
    deploy_lib('math_matrix', $math_matrix_cmd_table, $math_matrix_env_table,
	       {'MATH' => ['matrix'], 'MATH_MATRIX' => ['\\', 'hdotsfor']},
	       {'MATH' => [keys(%{$math_matrix_env_table})]});
    # May be set or changed like a normal counter
    $scan_proc->{cnt_table}->{MaxMatrixCols}->{value} = 10;
  };




$math_token_table->{math_mtx_sep}->{handler} = \&handle_mtx_cell_sep;
$math_token_table->{math_mtx_sep}->{tester} = sub {test_regexp("[&]")};




foreach my $token_type ('cmd', 'one_char_cmd',  'comment', 'man_arg', 'opt_arg')
  {
    $math_token_table->{$token_type}->{no_mmatrix_elements_autostart} = TRUE;
  }







return(TRUE);
