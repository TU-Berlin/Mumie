package Mumie::Logger;

# Authors:  Tilman Rassy <rassy@math.tu-berlin.de>,
#           Christian Ruppert<ruppert@math.tu-berlin.de>

# $Id: Logger.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

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

# ------------------------------------------------------------------------------------------
#1 Description
# ------------------------------------------------------------------------------------------
#
# Utilities to write log messages.
#
# This is a very simple logger. It allows you to write log messages to a file. Its main
# purpose is to provide convenience functions to format the messages and to open and close
# the log file.
#
# There are some gloabl variables that control the behaviour of the logger:
#cd
#  $log_file    & Name of the log file. Default is `"$0.$$.log"`. \\
#  $write_log   & Boolean. Whether to write log messages or not. Default is `FALSE`. \\
#  $max_log_expand_depth &
#                 Integer. Specifies the maximal depth whrn following array and
#                 hashes references. Default is 0. \\
#  $log_data_indent &
#                 Indent of logged data. Default is 2. \\
#  $log_data_size_name &
#                 Size of the name field when logging data. Default is 22. \\
#  $log_data_space_before_value &
#                 Number of blanks before the value of the logged data. Default is 2. \\
#  $log_max_data_value_length &
#                 Maximal length if scalar log data. If a scalar to be logged exeeds
#                 this value, it is truncated at $log_max_data_value_length characters
#                 and the string " [...]" is appended. To switch off this feature,
#                 set $log_max_data_value_length to 0 or any other false value. Default
#                 is 80.
#/cd



use constant VERSION => '$Revision: 1.2 $';

use Mumie::Boolconst;
use Mumie::Text qw(/^.+$/);

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL =
    (
     '$log_file',
     'open_log_file',
     'close_log_file',
     '$write_log',
     '$max_log_expand_depth',
     '$log_data_indent',
     '$log_data_size_name',
     '$log_data_space_before_value',
     'deref_log_list',
     'expand_log_value',
     'logging_is_ready',
     'log_message',
     'log_data',
    );
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
    );
}

my $_logging_is_ready;
  # Boolean. Whether the logging system is ready to recieve messages.

sub init
  #a ()
  {
    return() if ( $init_done );

    $write_log = FALSE;
    $max_log_expand_depth = 0;
    $log_data_indent = 2;
    $log_data_size_name = 22;
    $log_data_space_before_value = 2;
    $log_max_data_value_length = 80;
    $log_file ||= "$0.$$.log";
    $_logging_is_ready = FALSE;   # <- PLEASE INIT HERE
    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub logging_is_ready
  #a ()
  # Returns whether the logging system is ready; i.e., can recieve messages.
  {
    return($_logging_is_ready);
  }

sub open_log_file
  #a ()
  # Opens the log file
  {
    $log_file = $_[0] if ( $_[0] );
    open(LOG, ">$log_file");
    $_logging_is_ready = TRUE;
  }

sub close_log_file
  #a ()
  # Closes the log file
  {
    close(LOG);
    $_logging_is_ready = FALSE;
  }

sub log_message
  #a (@messages)
  # Writes @messages to the log file provided $write_log is true.
  {
    if ( $write_log )
      {
	print(LOG @_);
      }
  }

sub log_data
  #a (@data_description)
  # Pretty-prints name-value pairs of data to the log file. The data is
  # given by @data_description, which is an array of the form ($name1, $value1, $name2,
  # $value2, ...). $name1, $name2, ... and $value1, $value2, ... should be all strings.
  {
    if ( $write_log )
      {
	my @data_description = @_;
	while ( @data_description )
	  {
	    my $name = shift(@data_description);
	    my $value = ( @data_description ? shift(@data_description) : "" );

	    my $first_part
	      = (" " x $log_data_indent)
	      . flush_string_right($name, $log_data_size_name)
	      . ":"
	      . (" " x $log_data_space_before_value);

	    print(LOG $first_part);
	    print(LOG expand_log_value($value, length($first_part)));
	    print(LOG "\n");
	  }
      }
  }

sub deref_log_list
  #a ($list_ref, $seperator)
  {
    my ($list_ref, $seperator) = @_;
    $separator ||= ', ';
    if ( defined($list_ref) )
      {
	return(join($separator, @{$list_ref}));
      }
    else
      {
	return("UNDEF");
      }
  }

sub expand_log_value
  # ($value, $indent, $depth)
  # If $value is no reference it simply returns the value,
  # otherwise it expands arrays and hashes to a maximum depth of $depth
  # Runs recursive !!
  {
    my $value = $_[0];
    my $indent = ( $_[1] || 0 );
    my $depth = ( $_[2] ||  0 );

    my $result;

    if ( (! ref($value) ) || ( $depth >= $max_log_expand_depth ) )
      {
	if ( ( $value ) || ( defined($value) ) )
	  {
	    if ( ( $log_max_data_value_length ) &&
		 ( length("$value") > $log_max_data_value_length ) )
	      {
		$result = substr("$value", 0, $log_max_data_value_length) . ' [...]';
	      }
	    else
	      {
		$result = $value;
	      }
	  }
	else
	  {
	    $result = "UNDEF";
	  }
      }
    elsif ( ref($value) eq "REF" )
      {
	$result = expand_log_value(${$value}, $indent, $depth);
      }
    elsif ( ref($value) eq "CODE" )
      {
	$result = $value;
      }
    elsif ( ref($value) eq "HASH" )
      {
	$result = "$value:\n";
	foreach my $key (keys %{$value})
	  {
	    my $prefix = "  $key => ";
	    $result .= (" " x $indent) . $prefix
	            . expand_log_value($value->{$key},
				       length((" " x $indent) . $prefix),
				       $depth+1)
		    . "\n";
	  }
      }
    elsif ( ref($value) eq "ARRAY" )
      {
	$result = "$value:\n";
	my $length = scalar(@{$value});
	for (my $i = 0; $i < $length; $i++)
	  {
	    my $prefix = "  $i : ";
	    $result .= (" " x $indent) . $prefix
                    . expand_log_value($value->[$i],
				       length((" " x $indent) . $prefix),
				       $depth+1)
		    . "\n";
	  }
      }
    elsif ( ref($value) eq "SCALAR" )
      {
	$result .= "$value:\n"
	        . expand_log_value(${$value},
				   length((" " x $indent)),
				   $depth+1);
      }

    return $result;
  }

init();
return 1;
