package Mumie::List;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: List.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

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

use Mumie::Boolconst;

require Exporter;

@ISA = qw(Exporter);

@EXPORT_OK = qw
  (
   remove_from_array
   insert_in_array
   copy_array
   copy_hash
   copy_value
   $copy_other_type_handler
  );


# --------------------------------------------------------------------------------
# The init function
# --------------------------------------------------------------------------------

sub init
  {
    return() if ( $init_done );

    $copy_other_type_handler = sub {};
    # Reference to a function which is called by \code{copy_value} when processing a value
    # that is a reference but not to an array or hash.

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }


# --------------------------------------------------------------------------------
# List functions
# --------------------------------------------------------------------------------

sub remove_from_array
  # ($element, $array_ref)
  # Removes all occurrences of $element from the array to which the reference $array_ref
  # points. Returns a reference to the altered array. $element is compared to the elements
  # of the array using `==', so this function doesn't work with strings. Use
  # rm_string_from_array in module mumie::util::text to remove a string from an array of
  # strings.
  {
    my ($element, $array_ref) = @_;
    for ( my $i = 0; $i < scalar(@{$array_ref}); $i++ )
      {
	if ( $array_ref->[$i] == $element )
	  {
	    splice(@{$array_ref}, $i, 1);
	  }
      }
    return($array_ref);
  }

sub insert_in_array
  #a ($array_ref, $pos_or_element, $new_elements_ref, $mode, $rel)
  #
  # Inserts new elemens into an array. The array is given by the reference $array_ref, the
  # position where the new elements are to be inserted by $pos_or_element, and the new
  # elements by $new_elements_ref, which must be a reference to an array.
  #
  # $mode controls how $pos_or_element is interpreted: if "POS" (the default),
  # $pos_or_element is interpreted as a numerical position; if "ELEMENT" or
  # "STRING_ELEMENT", $pos is considered the element of the array where the insert should
  # take place. The difference between the last two possibilities is that with "ELEMENT",
  # the comparision of $pos_or_element with the list elements is done by '==', while with
  # "STRING_ELEMENT" the comparision is done by 'eq' (actually, the corresponding negated
  # operators, i.e., '!=' and 'ne', are used in the code, but that does not matter).
  #
  # $rel controls whether the insertion is done before or after the specified position:
  # If "AFTER" (the default), it is done after; if "BEFORE", it is done before.
  {
    my ($array_ref, $pos_or_element, $new_elements_ref, $mode, $rel) = @_;
    $mode ||= 'POS';
    $rel ||= 'AFTER';
    my $checker =
      {
       'POS' => sub { $pos_or_element < $#{$array_ref} },
       'ELEMENT' => sub { $pos_or_element != $array_ref->[$#{$array_ref}] },
       'STRING_ELEMENT' => sub { $pos_or_element ne $array_ref->[$#{$array_ref}] },
      };

    my @buffer =  ();
    while ( &{$checker->{$mode}}() )
      {
	unshift(@buffer, pop(@{$array_ref}));
      }
    unshift(@buffer, pop(@{$array_ref})) if ( $rel eq 'BEFORE' );
    $array_ref = \ push(@{$array_ref}, @{$new_elements_ref}, @buffer);
    return($array_ref);
  }




sub copy_array
  # ($array_ref)
  # Copies recursively the array to which the reference $array_ref points, and returns a
  # reference to the copy. "Recursively" means: if an element is itself a reference to an
  # array or hash, it is replaced by a reference to a copy of the referenced object; if
  # that copy contains references to arrays or hashes, they are treated the same way (see
  # also \code{copy_hash}).
  {
    my $array_ref = $_[0];
    my @copy = @{$array_ref};
    foreach my $element (@copy)
      {
	$element = copy_value($element);
      }
    return(\@copy);
  }

sub copy_hash
  # ($hash_ref)
  # Copies recursively the hash to which the reference $hash_ref points, and returns a
  # reference to the copy. "Recursively" means: if a hash value is itself a reference to
  # a hash or array, it is replaced by a reference to a copy of the referenced object; if
  # that copy contains references to hashes or arrays, they are treated the same way (see
  # also \code{copy_array}).
  {
    my $hash_ref = $_[0];
    my %copy = %{$hash_ref};
    foreach my $value (values(%copy))
      {
	$value = copy_value($value);
      }
    return(\%copy);
  }

sub copy_value
  # ($value)
  # If $value is a reference to an array or hash, returns a reference to a copy of that
  # array or hash, where copying is done recursively (see \code{copy_array} and
  # \code{copy_hash} for what the latter means). Otherweise, returns $value. If $value
  # is a reference, but not to an array or hash, the function to which
  # $copy_other_type_handler points is run before returning.
  {
    my $value = $_[0];
    my $type = ref($value);

    if ( $type eq "ARRAY" )
      {
	return(copy_array($value));
      }
    elsif ( $type eq "HASH" )
      {
	return(copy_hash($value));
      }
    else
      {
	if ( $type )
	  {
	    &{$copy_other_type_handler}("copy_value: reference to ", $type);
	  }
	return($value);
      }
  }

init();
return(TRUE);
