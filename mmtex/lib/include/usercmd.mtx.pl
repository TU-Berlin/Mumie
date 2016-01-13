package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: usercmd.mtx.pl,v 1.20 2007/07/11 15:56:15 grudzin Exp $

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
# Defines \newcommand, which allows the user to define own
# commands. Its syntax is
#
# \newcommand{NAME}[NUM_ARGS][NUM_OPT_ARGS][DEFAULTS][PAR_STARTER_SPEC]{CODE}
#
#
# --------------------------------------------------------------------------------
#1 Additional fields in $scan_proc
# --------------------------------------------------------------------------------
#
# This library introduces an additional field of
# Mumie::Scanner::$scan_proc:
#
#T{code}{}
#  arg_table      & Reference to the argument table (see below). \\
#/T
#
# Furthermore, this library uses fields of Mumie::Scanner::$scan_proc
# introduced by Mumie::MmTeX::new_scan_proc when initialized with
# the "SUB". They are:
#
#T{code}{}
#  type_of_sub       & Type of the subprocedure: user command or user command
#                      argument \\
#  name_of_sub       & Name of the subprocedure. For user commands: the command
#                      name, for user command arguments: #number, or
#                      ##number, ... \\
#  calling_scan_proc & Scan process from which this subprocedure was called.
#/T
#

use Mumie::Scanner qw
  (
   scan_next_token
  );

log_message("\nLibrary usercmd ", '$Revision: 1.20 $ ', "\n");


sub get_call_trace
  # ()
  # Returns a string describing the calls made while processing the last user command or
  # user argument.
  {
    my $text = "";
    my $this_scan_proc = $scan_proc;
    my $calling_scan_proc;

    while ( $this_scan_proc->{calling_scan_proc} )
      {
	$calling_scan_proc = $this_scan_proc->{calling_scan_proc};
	# For performance reasons, it may be better to declare the following variables
	# outside the while loop, but the code looks ugly then.
	my $name_of_sub = $this_scan_proc->{name_of_sub};
	$name_of_sub = "\\$name_of_sub" if ( $this_scan_proc->{type_of_sub} eq "USER_CMD" );
	my $prim_source_name = $calling_scan_proc->{prim_source_name};
	my $prim_source_pos
	  = $calling_scan_proc->{prim_source_offset} + pos($ {$calling_scan_proc->{source}} );
	my $line_number
	  = get_line_number_at($prim_source_pos, $ {$calling_scan_proc->{prim_source}});
	my ($before, $after)
	  = get_text_at($prim_source_pos, $ {$calling_scan_proc->{prim_source}});
	my $column_number = length($before);

	$text .= " - $name_of_sub was called from source $prim_source_name, "
               . "line $line_number, column $column_number";

	if ( $calling_scan_proc->{calling_scan_proc} )
	  {
	    my $name_of_calling_sub = $calling_scan_proc->{name_of_sub};
	    my $type_of_calling_sub = $calling_scan_proc->{type_of_sub};
	    if ( $type_of_calling_sub eq "USER_CMD" )
	      {
		$text .= ",\n   while executing user command $name_of_calling_sub.\n";
	      }
	    elsif ( $type_of_calling_sub eq "USER_CMD_ARG" )
	      {
		$text .= ",\n   while expanding user argument $name_of_calling_sub.\n";
	      }
	  }
	else
	  {
	    $text .= ".";
	  }

	$this_scan_proc = $calling_scan_proc;
      }

    return($text);
  }
sub notify_usr_cmd_error
  # (@explanation_list)
  # Notifies the user about an error occurred while processing a user command or user
  # argument. Then exists.
  {
    my $explanation = join("", @_);

    my $message = compose_error_message
      (
       $explanation,
       $scan_proc->{prim_source},
       $scan_proc->{prim_source_name},
       $scan_proc->{prim_source_offset} + pos(${$scan_proc->{source}}),
       $mark_string
      );

    if ( $scan_proc->{type_of_sub} eq "USER_CMD" )
      {
	$message .= "\nError occurred while executing user command \\"
	         . $scan_proc->{name_of_sub} . ".\n";
      }
    else
      {
	$message .= "\nError occurred while expanding user command argument "
	         . $scan_proc->{name_of_sub} . ".\n";
      }

    $message .= "Call trace:\n" . get_call_trace() . "\n";

    log_message("\n$message\n");

    ( $! = 1 ) if ( $! == 0 );
    die("\n$message\n");
  }

sub get_arg_defaults
  # ($arg_defaults_arg, $num_opt_args)
  # Returns the defaults for the optional arguments, as an array of strings.
  # \code{$arg_defaults_arg} is the argument of \code\verbatim{\newcommand} that
  # contains the defaults, and \code{$num_opt_args} is the number of optional arguments.
  {
    my ($arg_defaults_arg, $pos_arg_defaults_arg, $num_opt_args) = @_;
    log_message("\nget_arg_defaults 1/2\n");

    my @arg_defaults = ();

    new_scan_proc("EXCURSION", \$arg_defaults_arg, $pos_arg_defaults_arg, "SHARED_NAMESPACE");
    $scan_proc->{token_table}
      = {
	 "arg_default"
	   => {
	       "tester" => sub { test_balanced("(?<!\\\\){", "(?<!\\\\)}") },
	       "handler" => sub { push(@arg_defaults, $scan_proc->{last_token}) }
	      }
	};
    $scan_proc->{allowed_tokens} = ["arg_default"];

    scan();

    if ( scalar(@arg_defaults) > $num_opt_args )
      {
	&{$scan_proc->{error_handler}}("Too many argument defaults");
      }

    reset_scan_proc();

    log_message("\nget_arg_defaults 2/2\n");
    log_data('Defaults', "[", join('][', @arg_defaults), "]\n");

    return(\@arg_defaults);
  }

sub exec_newcommand
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexec_newcommand 1/2 \n");

    my $cmd_name_arg = $man_args->[0];
    if ( $cmd_name_arg !~ m/^\\/ )
      {
	&{$scan_proc->{error_handler}}
	  ("Command name must begin with a backslash: $cmd_name_arg");
      }
    my $cmd_name = substr($cmd_name_arg, 1);
    my $num_args = ( $opt_args->[0]  || 0 );
    my $num_opt_args = ( $opt_args->[1]  || 0 );
    if ( $num_opt_args > $opt_args )
      {
	&{$scan_proc->{error_handler}}
	  ("Number of optional arguments exceeds total number of arguments.");
      }
    my $num_man_args = $num_args - $num_opt_args;
    my $is_par_starter;
    my $execute_function =  sub { exec_user_cmd($cmd_name, @_) };
    my $cmd_source = $man_args->[1];
    my $prim_source_offset
      = $scan_proc->{prim_source_offset} + pos($ {$scan_proc->{source}}) - length($cmd_source);
    my $def_nesting
      = ( $scan_proc->{user_def_nesting} ? 1 + $scan_proc->{user_def_nesting} : 0 );
    my $arg_defaults = [];
    if ( $opt_args->[2] )
      {
	$arg_defaults = get_arg_defaults($opt_args->[2], $pos_opt_args->[2], $num_opt_args);
      }
    my $par_starter_specifier = ( $opt_args->[3] || "x" );
    if ( $par_starter_specifier eq "x" )
      {
	$is_par_starter = TRUE;
      }
    elsif ( $par_starter_specifier eq "-" )
            {
	$is_par_starter = FALSE;
      }
    else
      {
	&{$scan_proc->{error_handler}}
	  ("Invalid paragraph starter specifier: `$par_starter_specifier'");
      }

    $scan_proc->{cmd_table}->{$cmd_name}
      = {
	 "num_opt_args" => $num_opt_args,
	 "num_man_args" => $num_man_args,
	 "is_par_starter" => $is_par_starter,
	 "execute_function" => $execute_function,
	 "source" => $cmd_source,
	 "def_nesting" => $def_nesting,
	 "prim_source" => $scan_proc->{prim_source},
	 "prim_source_name" => $scan_proc->{prim_source_name},
	 "prim_source_offset" => $prim_source_offset
	};

    log_message("\nexec_newcommand 2/2\n");
    log_data("Name", $cmd_name,
	     "Num opt args", $num_opt_args,
	     "Num man args", $num_man_args,
	     "Par starter", $is_par_starter,
	     "Exec funct", $execute_function,
	     "Source", $cmd_source,
	     "Def nesting", $def_nesting,
	     "Prim source", $scan_proc->{prim_source},
	     "Prim name", $scan_proc->{prim_source_name},
	     "Prim offset", $prim_source_offset);

    my $arg_prefix = "#" x (1 + $def_nesting);
    for (my $arg_count = 1; $arg_count <= $num_opt_args; $arg_count++)
      {
	my $arg_name = $arg_prefix . $arg_count;
	my $arg_default = ( $arg_defaults->[$arg_count - 1] || "" );

	$scan_proc->{cmd_table}->{$cmd_name}->{arg_table}->{$arg_name}
	  = {
	     "source" => \$arg_default,
	     "prim_source" => $scan_proc->{prim_source},
	     "prim_source_name" => $scan_proc->{prim_source_name},
	     "prim_source_offset" => $prim_source_offset,
	    };

	log_data("Arg $arg_name default", $arg_default);
      }
  }

sub own_arg_table
  {
    log_message("\nown_arg_table\n");
    if ( $scan_proc->{arg_table} )
      {
	my %copy = %{$scan_proc->{arg_table}};
	$scan_proc->{arg_table} = \%copy;
      }
    else
      {
	$scan_proc->{arg_table} = {};
      }
  }

sub exec_user_cmd
  # ($cmd_name, $opt_args, $man_args, $pos_opt_args, $pos_man_args)
  # Executes the user defined command $cmd_name. The remaining args, i.e., $opt_args,
  # $man_args, $pos_opt_args, $pos_man_args, play the same role as with the usual command
  # executing functions.
  {
    log_message("\nexec_usr_cmd 1/3\n");

    my ($cmd_name, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $source = $scan_proc->{cmd_table}->{$cmd_name}->{source};
    my $def_nesting = $scan_proc->{cmd_table}->{$cmd_name}->{def_nesting};
    my $arg_prefix = "#" x (1 + $def_nesting);

    new_scan_proc
      (
       "SUB",                                                         # how_to_init
       "USER_CMD",                                                    # type
       $cmd_name,                                                     # name
       \$source,                                                      # source
       $scan_proc->{cmd_table}->{$cmd_name}->{prim_source},           # prim_source
       $scan_proc->{cmd_table}->{$cmd_name}->{prim_source_name},      # prim_source_name
       $scan_proc->{cmd_table}->{$cmd_name}->{prim_source_offset},    # prim_source_offset
       "OWN_NAMESPACE"                                                # namespace_opt
      );
    own_arg_table();
    $scan_proc->{error_handler} = \&notify_usr_cmd_error;

    my $num_opt_args = $scan_proc->{cmd_table}->{$cmd_name}->{num_opt_args};
    my $actual_num_opt_args = scalar(@{$opt_args});
    my $num_man_args = $scan_proc->{cmd_table}->{$cmd_name}->{num_man_args};
    my $num_args = $num_opt_args + $num_man_args;

    log_message("\nexec_usr_cmd 2/3\n");
    log_data("Name", $cmd_name,
	     "Num opt args", $num_opt_args,
	     "Act opt args", $actual_num_opt_args,
	     "Num man args", $num_man_args,
	     "Source", $source,
	     "Def nesting", $def_nesting,
	     "Prim source", $scan_proc->{prim_source},
             "Prim name", $scan_proc->{prim_source_name},
	     "Prim offset", $scan_proc->{prim_source_offset});

    for (my $arg_count = 1; $arg_count <= $num_args; $arg_count++)
      {
	my $arg_name = $arg_prefix . $arg_count;
	
	# If the default must be used:
	if ( ( $actual_num_opt_args < $arg_count ) && ( $arg_count <= $num_opt_args ) )
	  {
	    $scan_proc->{arg_table}->{$arg_name}
	      = $scan_proc->{cmd_table}->{$cmd_name}->{arg_table}->{$arg_name};
	  }
	# If the value is explicitely given:
	else
	  {
	    my $arg_value
	      = ( $arg_count <= $actual_num_opt_args ) ?
		($opt_args->[$arg_count - 1]) :
		($man_args->[$arg_count - $num_opt_args - 1]);
	    my $arg_pos
	      = ( $arg_count <= $actual_num_opt_args ) ?
		($pos_opt_args->[$arg_count - 1]) :
		($pos_man_args->[$arg_count - $num_opt_args - 1]);
	    $scan_proc->{arg_table}->{$arg_name}
	      = {
		 "source" => \$arg_value,
		 "prim_source" => $scan_proc->{calling_scan_proc}->{prim_source},
		 "prim_source_name" => $scan_proc->{calling_scan_proc}->{prim_source_name},
		 "prim_source_offset" => $scan_proc->{calling_scan_proc}->{prim_source_offset}
		                         + $arg_pos,
		};
	  }

	log_message("Arg $arg_name\n");
	log_data
	  (
	   "Value", ${$scan_proc->{arg_table}->{$arg_name}->{source}},
	   "Prim source", $scan_proc->{arg_table}->{$arg_name}->{prim_source},
	   "Prim name", $scan_proc->{arg_table}->{$arg_name}->{prim_source_name},
	   "Prim offset", $scan_proc->{arg_table}->{$arg_name}->{prim_source_offset}
	  );
      }

    $scan_proc->{token_table}->{usr_cmd_arg}
      = {
	 "tester" => sub { test_regexp("#+[1-9][0-9]*") },
	 "handler" => sub { exec_user_cmd_arg($scan_proc->{last_token}) },
         "vital" => TRUE,
	};
    $scan_proc->{allowed_tokens} = ["usr_cmd_arg", @{$scan_proc->{allowed_tokens}}];

    $scan_proc->{user_def_nesting} = $scan_proc->{cmd_table}->{$cmd_name}->{def_nesting};
	
    scan();

    my $output_list = $scan_proc->{output_list};
    reset_scan_proc("ADOPT_VALUES");

    log_message("\nexec_usr_cmd 3/3 (end)\n");

    return($output_list);
  }

sub exec_user_cmd_arg
  # ($arg_name)
  # "Executes" an argument placeholder in the body of a user defined command, e.g., "#1",
  # "#2", etc. Execution means processing the latex code the placeholder stands for.
  {
    log_message("\nexec_usr_cmd_arg 1/3\n");

    my $arg_name = $_[0];

    my $source = $scan_proc->{arg_table}->{$arg_name}->{source};

    log_message("\nexec_usr_cmd_arg 2/3\n",
		"  Name         : $arg_name\n",
		"  Source       : ", $ {$source}, "\n");

    new_scan_proc
      (
       "SUB",                                                      # how_to_init
       "USER_CMD_ARG",                                             # type
       $arg_name,                                                  # name
       $source,                                                    # source
       $scan_proc->{arg_table}->{$arg_name}->{prim_source},        # prim_source
       $scan_proc->{arg_table}->{$arg_name}->{prim_source_name},   # prim_source_name
       $scan_proc->{arg_table}->{$arg_name}->{prim_source_offset}, # prim_source_offset
       "OWN_NAMESPACE"                                             # namespace_opt
      );
    own_arg_table();
    $scan_proc->{error_handler} = \&notify_usr_cmd_error;

    scan();

    my $output_list = $scan_proc->{output_list};
    reset_scan_proc();

    log_message("\nexec_usr_cmd_arg 3/3 (end)\n");

    return($output_list);
  }




# --------------------------------------------------------------------------------=
# Command table for export
# --------------------------------------------------------------------------------




# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

# $import_cmd_tables->{usercmd} = $usercmd_cmd_table;

$lib_table->{usercmd}->{initializer} = sub
  {

    my $usercmd_cmd_table =
      {
       'newcommand' =>
         {
          'num_opt_args' => 4,
          'num_man_args' => 2,
          'pref_man_arg' => TRUE,
          'is_par_starter' => FALSE,
          'execute_function' => \&exec_newcommand,
          'doc' =>
            {
             'man_args' =>
               [
                ['name', 'Command name'],
                ['code', 'The code defining the command']
               ],
             'opt_args' =>
               [
                ['num_args', 'Total number of arguments'],
                ['num_opt_args', 'Number of optional arguments'],
                ['defaults', 'Default values for the optional arguments'],
                ['is_par_starter', 'Whether the command is a paragraph starter']
               ],
             'description' =>
               'Defines %{1} as a new command with %[1] arguments, from which %[2] are ' .
               'optional. Both %[1] and %[2] default to 0. When %{1} is called, %{2} is ' .
               'processed. %[3] must be a list of values, each enclosed in {} parenthesis. ' .
               '%[4] must be x or -, meaning "is paragrap starter" or "is no paragraph ' .
               'starter", respectively. Default is x.'
            }
         }
      };

    my @usercmd_cmds = keys(%{$usercmd_cmd_table});

    deploy_lib('usercmd', $usercmd_cmd_table, {},
               {
                'PREAMBLE' => \@usercmd_cmds,
                'TOPLEVEL' => \@usercmd_cmds,
                'USERCMD' => \@usercmd_cmds,
               },
               FALSE);
  };

return(1);
