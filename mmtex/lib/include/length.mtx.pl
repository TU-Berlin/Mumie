package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: length.mtx.pl,v 1.6 2007/07/11 15:56:14 grudzin Exp $

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
# Defines utilities to handle lengths


log_message("\nLibrary \"length\" ", '$Revision: 1.6 $ ', "\n");

sub init_length_units
  {
    $length_units =
      {
       'em' =>
         {
          'type' => 'relative',
          'factor' => 10.000,
         },
       'ex' =>
         {
          'type' => 'relative',
          'factor' => 8.000,
         },
       'px',
         {
          'type' => 'relative',
          'factor' => 1.000,
         },
       'mm',
         {
          'type' => 'absolute',
          'factor' => 2.835,
         },
       'cm',
         {
          'type' => 'absolute',
          'factor' => 28.347,
         },
       'in',
         {
          'type' => 'absolute',
          'factor' => 72.000,
         },
       'pt',
         {
          'type' => 'absolute',
          'factor' => 1.000,
         },
       'pc',
         {
          'type' => 'absolute',
          'factor' => 12.000,
         },
       'prc',
         {
          'type' => 'percent',
          'xml_notation' => '%',
         },
      };

    $default_length_unit = 'px';
  }

sub check_length_unit
  {
    my ($unit) = @_;
    if ( grep($unit eq $_, (keys(%{$length_units}))) )
      {
	return(TRUE);
      }
    else
      {
	&{$scan_proc->{error_handler}}("Unknown length unit: $unit");
	return(FALSE);
      }
  }

sub process_length
  # ($length)
  # Splits length specificator $length into value and unit and returns the latter
  # as a list.
  {
    my ($length, $default_unit_or_length) = @_;
    if ( $length =~ m/^(-?(?:[0-9]+(?:\.[0-9]*)?|\.[0-9]+))?([^0-9]*)?$/ )
      {
        my $value = ($1 || '1');
        my $unit_or_other_length = $2;
	my $unit = undef();
        if ( $unit_or_other_length )
          {
            if ( grep($unit_or_other_length eq $_, (keys(%{$length_units}))) )
              {
                $unit = $unit_or_other_length;
              }
            elsif ( $scan_proc->{length_table}->{$unit_or_other_length} )
              {
                my $other_length = $unit_or_other_length;
                $unit = $scan_proc->{length_table}->{$other_length}->{unit};
                $value *= $scan_proc->{length_table}->{$other_length}->{value};
              }
            else
              {
                &{$scan_proc->{error_handler}}("Unknown unit or length: $unit_or_other_length");
              }
          }
	return($value, $unit);
      }
    else
      {
	&{$scan_proc->{error_handler}}("Invalid length: $length\n");
      }
  }

sub set_length
  {
    my ($name, $length) = @_;

    log_message("\nset_length\n");
    log_data('Name', $name, 'Length', $length);

    if ( ! $scan_proc->{cmd_table}->{$name} )
      {
	&{$scan_proc->{error_handler}}("Length command unknown: \\$name\n");
      }

    if ( ! $scan_proc->{length_table}->{$name} )
      {
	&{$scan_proc->{error_handler}}("Not a length command: \\$name\n");
      }

    my ($value, $unit) = (ref($length) ? @{$length} : process_length($length, 'CHECK_UNIT'));

    $scan_proc->{length_table}->{$name} =
      {
       'value' => $value,
       'unit' => $unit,
      }
  }

sub convert_length
  {
    my ($value, $unit, $new_unit) = @_;

    log_message("\nconvert_length\n");
    log_data('Value', $value, 'Unit', $unit, 'New unit', $new_unit);

    if ( $unit ne $new_unit )
      {
	if ( ! $length_units->{$unit}->{factor} )
	  {
	    &{$scan_proc->{error_handler}}("Can't convert length unit: $unit");
	  }

	if ( ! $length_units->{$new_unit}->{factor} )
	  {
	    &{$scan_proc->{error_handler}}("Can't convert into length unit: $new_unit");
	  }

	$value *= $length_units->{$unit}->{factor} / $length_units->{$new_unit}->{factor};
      }
    return($value);
  }

sub get_xml_length_spec
  #a ($value, $unit)
  {
    my ($value, $unit) = @_;
    my $xml_unit_notation = ( $length_units->{$unit}->{xml_notation} || $unit );
    return("$value$xml_unit_notation");
  }

my $last_length = undef();
  # If defined, stores the last length.

sub set_last_length
  #a ($name)
  {
    my ($name) = @_;

    if ( $last_length )
      {
	&{$scan_proc->{error_handler}}
	  ('Length already set (', $last_length->{name}, ')');
      }

    $last_length =
      {
       'name' => $name,
       'value' => $scan_proc->{length_table}->{$name}->{value},
       'unit' => $scan_proc->{length_table}->{$name}->{unit},
      };
  }

sub get_last_length
  #a ()
  # Returns $last_length (i.e., the last length) and sets
  # $last_length to the undefined value afterwards.
  {
    my $text = $last_length;
    $last_length = undef();
    return($text);
  }

sub reset_last_length
  #a ()
  # Sets $last_length to the undefined value.
  {
    $last_length = undef();
  }

sub get_length_from_arg
  {
    my ($num, $args, $pos_args, $ctr_type) = @_;

    log_message("\nget_length_from_arg\n");

    reset_last_length();
    my $length = get_data_from_arg
      ($num, $args, $pos_args, $ctr_type, sub {$scan_proc->{parsing_length} = TRUE});
    my ($value, $unit) = process_length($length);
    if ( $unit )
      {
	if ( $last_length )
	  {
	    &{$scan_proc->{error_handler}}
	      ('Expression before length command must be a bare number');
	  }
      }
    elsif ( $last_length )
      {
	$value *= $last_length->{value};
	$unit =  $last_length->{unit};
      }
    else
      {
	$unit = $default_length_unit;
      }

    check_length_unit($unit);
    return($value, $unit);
  }

sub execute_length_cmd
  {
    my ($name, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_length_cmd\n");
    log_data('Name', $name);

    if ( $scan_proc->{parsing_length} )
      {
	set_last_length($name);
      }
    else
      {
	my ($value, $unit) = get_length_from_arg(0, $man_args, $pos_man_args, 'CMD');
	$scan_proc->{length_table}->{$name} =
	  {
	   'value' => $value,
	   'unit' => $unit,
	  };
      }
  }

sub get_length_cmd_num_man_args
  {
    return($scan_proc->{parsing_length} ? 0 : 1);
  }

sub execute_newlength
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_newlength 1/3\n");

    my $name = $man_args->[0];
    $name =~ s/^\\//;

    my ($value, $unit) =
      ( $opt_args->[0] ? get_length_from_arg(0, $opt_args, $pos_opt_args, 'CMD')
	               : (0, $default_length_unit) );

    log_message("\nexecute_newlength 2/3\n");
    log_data('Name', $name, 'Value', $value, 'Unit', $unit);

    $scan_proc->{length_table}->{$name} =
      {
       'value' => $value,
       'unit' => $unit,
      };

    $scan_proc->{cmd_table}->{$name} =
      {
       'num_opt_args' => 0,
       'num_man_args' => \&get_length_cmd_num_man_args,
       'is_par_starter' => FALSE,
       'execute_function' => sub { execute_length_cmd($name, @_) },
      };

    log_message("\nexecute_newlength 3/3\n");
  }

sub execute_writelength
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_writelength 1/3\n");

    my $name = $man_args->[0];
    $name =~ s/^\\//;

    if ( ! $scan_proc->{length_table}->{$name} )
      {
	&{$scan_proc->{error_handler}}("Unknown length command: \\$name");
      }

    my ($value, $unit) =
      (
       $scan_proc->{length_table}->{$name}->{value},
       $scan_proc->{length_table}->{$name}->{unit},
      );

    if ( $opt_args->[0] )
      {
	my $old_unit = $unit;
	my $new_unit = get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD');
	check_length_unit($new_unit);
	$value = convert_length($value, $old_unit, $new_unit);
	$unit = $new_unit;
      }

    my $format = '';
    if ( $opt_args->[1] )
      {
	$format = get_data_from_arg(1, $opt_args, $pos_opt_args, 'CMD');
	if ( $format !~ m/^(?:[0-9]+\.?[0-9]*|[0-9]*\.?[0-9]+)$/ )
	  {
	    &{$scan_proc->{error_handler}}("Invalid format: $format");
	  }
      }

    log_message("\nexecute_writelength 2/3\n");
    log_data('Name', $name, 'Value', $value, 'Unit', $unit);

    xml_pcdata(($format ? sprintf("%${format}f", $value) : $value) . $unit);

    log_message("\nexecute_writelength 3/3\n");
  }

sub execute_setlength
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_setlength 1/3\n");

    my $name = $man_args->[0];
    $name =~ s/^\\//;

    my ($value, $unit) = get_length_from_arg(1, $man_args, $pos_man_args, 'CMD');

    log_message("\nexecute_setlength 2/3\n");
    log_message('Name', $name, 'Value', $value, 'Unit', $unit);

    set_length($name, [$value, $unit]);

    log_message("\nexecute_setlength 3/3\n");
  }



# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------


$lib_table->{length}->{initializer} = sub
  {
    init_length_units();

    my $length_cmd_table =
      {
       'newlength' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'pref_man_arg' => TRUE,
          'is_par_starter' => FALSE,
          'execute_function' => \&execute_newlength,
          'doc' =>
            {
    	 'man_args' => [['length', 'Name of the length']],
    	 'opt_args' => [['value', 'Initial value of the length']],
    	 'description' =>
    	   'Defines %{1} as a new length command. If %[1] is set, the initial value of %{1} ' .
    	   'is %[1]; otherwise, the initial value is 0.',
    	}
         },
       'writelength' =>
         {
          'num_opt_args' => 2,
          'num_man_args' => 1,
          'execute_function' => \&execute_writelength,
          'doc' =>
            {
    	 'man_args' => [['length', 'Name of the length']],
    	 'opt_args' =>
    	   [
    	    ['unit', 'Unit to print the value in'],
    	    ['format', 'Format to print the value in'],
    	   ],
    	 'description' =>
    	   'Prints out %{1}. The output is composed of value and length, e.g., "12em". ' .
    	   'By %[1] a length unit other than the one of %{1} can be specified for ' .
    	   'the output. %[2], if set, must be of the form "m.n"; where m is the total ' .
    	   'number of digits and n the number of digits after the decimal point. Either ' .
    	   'm or n, but not both, may be omitted.',
    	}
         },
       'setlength' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 2,
          'execute_function' => \&execute_setlength,
          'is_par_starter' => FALSE,
          'doc' =>
            {
    	 'man_args' =>
    	   [
    	    ['length', 'Name of the length'],
    	    ['value', 'New value of the length'],
    	   ],
    	 'description' =>
    	   'Sets the value of %{1} to %{2}.',
    	}
         },
      };

    $scan_proc->{length_table} = {};

    my @all_cmds = keys(%{$length_cmd_table});
    my @preamble_cmds = ('newlength', 'setlength');
    deploy_lib('length', $length_cmd_table, {},
	       {'TOPLEVEL' => \@all_cmds, 'PREAMBLE' => \@preamble_cmds},
	       FALSE);
  };

return(TRUE);
