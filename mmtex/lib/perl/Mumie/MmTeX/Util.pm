package Mumie::MmTeX::Util;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Util.pm,v 1.2 2007/07/11 15:56:15 grudzin Exp $

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
# General mmtex utilities.

use constant VERSION => '$Revision: 1.2 $';
use Mumie::Boolconst;

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL = qw
    (
     get_value
     parse_options
    );
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
    );
}

sub init
  {
    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub get_value
  #a ($spec)
  # If $spec is not a reference, returns $spec. Otherwise, assumes $spec points to a
  # function, and returns the result of the function call.
  {
    my ($spec) = @_;
    return(ref($spec) ? &{$spec}() : $spec);
  }

sub parse_options
  #a ($option_string, $option_table)
  # Parses $option_string, stores the result in $option_table, and returns
  # $option_table. $option_string must be of the form
  #c
  #  OPTION1, OPTION2, ... OPTIONn
  #/c
  # where `OPTION1` ... `OPTIONn` is of the form
  #c
  #  NAME=VALUE
  #/c
  # or simply
  #c
  #  NAME
  #/c
  # In the latter case, i.e., if no VALUE is given, VALUE is set to `TRUE`. $option_table
  # must be a refernce to a hash. It can also be omitted or false, in which case it is set
  # to a reference to a newly created hash. Each NAME/VALUE pair is entered in the hash
  # $option_table points to.
  {
    my ($option_string, $option_table) = @_;
    $option_string ||= '';
    $option_table ||= {};
    my @option_list = split(/\s*,\s*/, $option_string);
    foreach my $option (@option_list)
      {
	$option =~ m/^([^=]+)(?:=(.*))?$/;
        my $name = ( $1 || "" );
        my $value = ( $2 || 1 );
	$option_table->{$name} = $value;
      }
    return $option_table;
  }


init();
return(1);
