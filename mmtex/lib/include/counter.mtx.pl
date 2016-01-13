package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: counter.mtx.pl,v 1.16 2007/07/11 15:56:13 grudzin Exp $

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

# --------------------------------------------------------------------------------
#1 Additional fields in $scan_proc
# --------------------------------------------------------------------------------
#
# This library introduces an additional field of
# \link{Mumie::Scanner::$scan_proc}:
#
#T[code,]
#  cnt_table      & Reference to the \link{counter table} (see below). \\
#/T
#
# --------------------------------------------------------------------------------
#1 The counter table
# --------------------------------------------------------------------------------
#
# This is a hash mapping each counter name to a reference to a hash containing
# the following entries
#
#T[code,]
#  value    & Current value of the counter \\
#  dep_cnts & Reference to a list of counters, the so-called \emph{dependent
#             counters} of this one. Each of these counters is reset to zero
#             if this counter is incremented by \code{stepcounter}.
#/T

log_message("\nLibrary counter ", '$Revision: 1.16 $ ', "\n");

$cnt_name_regexp = "^[a-zA-Z]+\$";

sub log_counter
  # ($counter, [$value, $action])
  {
    my ($counter, $value, $action) = @_;

    log_data("Counter", $counter,
	     "Value", $scan_proc->{cnt_table}->{$counter}->{value});

    if ( $action eq "SET" )
      {
	log_data("New value", $value);
      }
    elsif ( $action eq "ADD" )
      {
	log_data("Increment", $value);
      }
  }

sub get_counter_value
  # ($counter)
  {
    my ($counter) = @_;
    my $value = 0;

    if ( exists($scan_proc->{cnt_table}->{$counter}) )
      {
	$value = $scan_proc->{cnt_table}->{$counter}->{value};
      }
    else
      {
	&{$scan_proc->{error_handler}}("Unknown counter: \`$counter\'.");
      }

    log_message("\nget_counter_value\n");
    log_data("Counter", $counter, "Value", $value);

    return($value);
  }

sub set_counter
  # ($counter, $value)
  {
    log_message("\nset_counter\n");

    my ($counter, $value) = @_;
    $value = trim_str($value);

    if ( exists($scan_proc->{cnt_table}->{$counter}) )
      {
	if ( $value =~ m/[0-9]+/ )
	  {
	    log_counter($counter, $value, "SET");
	    $scan_proc->{cnt_table}->{$counter}->{value} = $value;
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}("Invalid counter value: \`$value\'.");
	  }
      }
    else
      {
	&{$scan_proc->{error_handler}}("Unknown counter: \`$counter\'.");
      }
  }

sub add_to_counter
  # ($counter, $value)
  {
    log_message("\nadd_to_counter\n");

    my ($counter, $value) = @_;
    $value = trim_str($value);

    if ( exists($scan_proc->{cnt_table}->{$counter}) )
      {
	if ( $value =~ m/-?[0-9]+/ )
	  {
	    log_counter($counter, $value, "ADD");
	    $scan_proc->{cnt_table}->{$counter}->{value} += $value;
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}("Invalid counter increment: \`$value\'.");
	  }
      }
    else
      {
	&{$scan_proc->{error_handler}}("Unknown counter: \`$counter\'.");
      }
  }

sub execute_value
  # ($counter)
  {
    log_message("\nexecute_value\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $counter
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "value", "counter_arg");

    xml_pcdata(get_counter_value($counter));
  }

sub execute_setcounter
  {
    log_message("\nexecute_setcounter\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $counter
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "setcounter", "counter_arg");
    my $value
      = get_data_from_arg(1, $man_args, $pos_man_args, "CMD", "setcounter", "value_arg");

    set_counter($counter, $value);
  }

sub execute_addtocounter
  {
    log_message("\nexecute_addtocounter\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $counter
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "addtocounter", "counter_arg");
    my $value
      = get_data_from_arg(1, $man_args, $pos_man_args, "CMD", "addtocounter", "value_arg");

    add_to_counter($counter, $value);
  }

sub execute_stepcounter
  {
    log_message("\nexecute_stepcounter\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $counter
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "stepcounter", "counter_arg");

    add_to_counter($counter, 1);

    foreach my $dep_counter (@{$scan_proc->{cnt_table}->{$counter}->{dep_cnts}})
      {
	set_counter($dep_counter, 0);
      }
  }

sub new_counter
  # ($counter, [$dep_from_spec])
  {
    my ($counter, $dep_from_spec) = @_;

    log_message("\nnew_counter 1/2\n");
    log_data("Name", $counter);

    if ( $counter !~ m/$cnt_name_regexp/ )
      {
	&{$scan_proc->{error_handler}}("Invalid counter name: \`$counter\'.");
      }
	$scan_proc->{cnt_table}->{$counter}->{value} = 0;


    if ( $dep_from_spec )
      {
	my $dep_from_list = ( ref($dep_from_spec) ? $dep_from_spec : [$dep_from_spec] );

	log_message("\nnew_counter 2/2\n");
	log_data("Dep. spec.", $dep_from_spec, "Dep.", join(', ', @{$dep_from_list}));

	foreach my $dep_from (@{$dep_from_list})
	  {
	    if ( ! exists($scan_proc->{cnt_table}->{$dep_from}) )
	      {
		&{$scan_proc->{error_handler}}("Unknown counter: $dep_from");
	      }
	    push(@{$scan_proc->{cnt_table}->{$dep_from}->{dep_cnts}}, $counter);
	  }
      }
    else
      {
	log_message("\nnew_counter 2/2\n");
      }
  }

sub execute_newcounter
  {
    log_message("\nexecute_newcounter\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $counter
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "newcounter", "counter_arg");

    my $dep_from = FALSE;
    if ( $ {$opt_args}[0] )
      {
	$dep_from
	  = get_data_from_arg(0, $opt_args, $pos_opt_args,
			      "CMD", "newcounter", "dep_from_arg");
      }

    new_counter($counter, $dep_from);
  }

sub number_to_alph
  # ($number)
  {
    my $number = $_[0];
    my $alph;
    if ( ( $number >= 0 ) && ( $number <= 26 ) )
      {
	$alph
	  = [" ", "a",  "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
	     "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"]->[$number];
      }
    else
      {
	$alph = "";
      }
    return($alph);
  }

sub execute_alph
  {
    log_message("\nexecute_alph\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $counter
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "alph", "counter_arg");

    if ( exists($scan_proc->{cnt_table}->{$counter}) )
      {
	my $value = $scan_proc->{cnt_table}->{$counter}->{value};
	if ( ( $value >= 0 ) && ( $value <= 26 ) )
	  {
	    xml_pcdata(number_to_alph($value));
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}("Can't convert number to alph: \`$value\'.");
	  }
      }
    else
      {
	&{$scan_proc->{error_handler}}("Unknown counter: $counter");
      }

  }

sub execute_counter_output
  {
    log_message("\nexecute_counter_output\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $format, $range) = @_;

    my $counter
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "alph", "counter_arg");

    if (! exists($scan_proc->{cnt_table}->{$counter}) )
      {
	&{$scan_proc->{error_handler}}("Unknown counter: $counter");
      }
    else
      {
	my $value = $scan_proc->{cnt_table}->{$counter}->{value};

	if ( ( $range ) && ( ( $value < $range->[0] ) || ( $value > $range->[1] ) ) )
	  {
	    &{$scan_proc->{error_handler}}
	      ("Counter value not printable this way: $value (out of printable range)");
	  }
	else
	  {
	    empty_xml_element("number", {"value" => $value, "format" => $format}, "INLINE");
	  }
      }
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{counter}->{initializer} = sub
  {
    my $counter_cmd_table
      = {
         "addtocounter" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 2,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_addtocounter,
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter'],
                              ['number', 'Number to add to %{1}']],
               "description" => 'Adds %{2} to the value of %{1}.',
              }
           },
         "setcounter" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 2,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_setcounter,
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter'],
                              ['value', 'New value of %{1}']],
               "description" => 'Sets the value of %{1} to %{2}.',
              }
           },
         "stepcounter" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_stepcounter,
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Increases the value of %{1} by 1.',
              }
           },
         "newcounter" =>
           {
            "num_opt_args" => 1,
            "num_man_args" => 1,
            "pref_man_arg" => TRUE,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_newcounter,
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the new counter']],
               "opt_args" => [['master', 'Counter that resets %{1}']],
               "description" =>
                 'Defines %{1} as a new counter. If %[1] is set, it must be the name ' .
                 'of another counter. Each time %[1] is incremented, %{1} is reset to 0.',
              }
           },
         "value" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_value,
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Returns the value of %{1}.',
              }
           },
         "arabic" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => sub { execute_counter_output(@_, "arabic") },
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Prints the value of %{1} as an arabic number. ' .
                                'E.g., the values 1,2,3,... are printed as 1,2,3,...',
              }
           },
         "alph" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => sub { execute_counter_output(@_, "alph") },
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Prints the value of %{1} as a lower case latin character. ' .
                                'E.g., the values 1,2,3,... are printed as a,b,c,...',
              }
           },
         "Alph" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => sub { execute_counter_output(@_, "Alph") },
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Prints the value of %{1} as a upper case latin character. ' .
                                'E.g., the values 1,2,3,... are printed as A,B,C,...',
              }
           },
         "roman" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => sub { execute_counter_output(@_, "roman") },
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Prints the value of %{1} as a lower case roman number. ' .
                                'E.g., the values 1,2,3,... are printed as i,ii,iii,...',
              }
           },
         "Roman" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => sub { execute_counter_output(@_, "Roman") },
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Prints the value of %{1} as a upper case roman number. ' .
                                'E.g., the values 1,2,3,... are printed as I,II,III,...',
              }
           },
         "fnsymbol" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => sub { execute_counter_output(@_, "footnote-symbol", [0, 9]) },
            "doc" =>
              {
               "man_args" => [['counter', 'Name of the counter']],
               "description" => 'Prints the value of %{1} as a footnote symbol. E.g., the ' .
                                'values 1,2,3,... are printed as &lowast;,&dagger;,&Dagger;,...' .
                                'The value of %{1} must not exceed 9, since there are only 9 ' .
                                'footnote symbols.',
              }
           },
        };

    my @all_cmds = keys(%{$counter_cmd_table});
    my @preamble_cmds = ('value', 'setcounter', 'addtocounter', 'stepcounter', 'newcounter');
    deploy_lib('counter', $counter_cmd_table, {},
               {'TOPLEVEL' => \@all_cmds, 'PREAMBLE' => \@preamble_cmds},
               FALSE);
  };

return(TRUE);
