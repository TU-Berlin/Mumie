package Mumie::Text;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Text.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

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

{
  my @_QUERY =
    (
     'count_subexp',
     'get_line_number_at',
     'get_column_number_at',
     'get_text_at',
     'text_len',
    );
  my @_MODIFY =
    (
     'trim_str',
    );
  my @_FORMAT =
    (
     'flush_string_right',
     'format_words_as_block',
     '$last_format_col',
     'underline',
     'bar',
     'box',
     'get_notation',
    );
  my @_ERROR =
    (
     'compose_error_message',
    );
  my @_ARRAY =
    (
     'rm_string_from_array',
     'subst_string_in_array',
    );

  my @_ALL =
    (
     @_QUERY,
     @_MODIFY,
     @_FORMAT,
     @_ERROR,
     @_ARRAY,
     '$error_message_margin_left',
     '$error_message_headline',
    );

  @EXPORT_OK = @_ALL;

  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
     'QUERY' => \@_QUERY,
     'MODIFY' => \@_MODIFY,
     'FORMAT' => \@_FORMAT,
     'ERROR' => \@_ERROR,
     'ARRAY' => \@_ARRAY,
    );
}


# --------------------------------------------------------------------------------
#1 The init function
# --------------------------------------------------------------------------------

sub init
  {
    return() if ( $init_done );

    $last_format_col = 0;
    # Contains the final column of a text formatting process performed by a
    # format_words_as_block() call.

    $error_message_margin_left = undef();
    $error_message_headline = undef();

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }


# --------------------------------------------------------------------------------
#1 Investigating text
# --------------------------------------------------------------------------------

sub count_subexp
  #a ($subexp, $string)
  # Returns the number of maches of regular expression $subexp in string $string.
  {
    my ($subexp, $string) = @_;
    my $count = 0;
    while ( $string =~ m/$subexp/g )
      {
	$count++;
      }
    return $count;
  }

sub get_line_number_at
  #a ($pos, $string)
  # Returns the line number at position $pos in string $string.
  {
    my ($pos, $string) = @_;
    return(1 + count_subexp("\\n", substr($string, 0, $pos)));
  }

sub get_column_number_at
  #a ($pos, $string)
  # Returns the column number at position $pos in string $string. I.e., if $pos is
  # 'n' characters from the last newline, then the column number is 'n'.
  {
    my ($pos, $string) = @_;
    substr($string, 0, $pos) =~ m/^.*\Z/m;
    return(length($&));
  }

sub get_text_at
  #a ($pos, $string)
  # Returns a list of two substrings of $string which are the parts left resp. right from
  # $pos of the line $pos is in. $pos must be a position in $string.
  {
    my ($pos, $string) = @_;
    substr($string, 0, $pos) =~ m/^.*\Z/m;
    my $before = $&;
    substr($string, $pos) =~ m/\A.*$/m;
    my $after = $&;
    return(($before, $after));
  }

sub text_len
  #a ($str)
  # Returns the length of string $str without newlines.
  {
    my $str = $_[0];
    $str =~ s/\n//g;
    return(length($str));
  }

# ------------------------------------------------------------------------------------------
#1 Modifying text
# --------------------------------------------------------------------------------

sub trim_str
  #a ($str)
  # Removes whitespaces at the beginning and end of string $str and returns the
  # resulting string.
  {
    my $str = $_[0];
    $str =~ s/^\s*|\s*$//g;
    return($str);
  }

# --------------------------------------------------------------------------------
#1 Formatting text
# --------------------------------------------------------------------------------

sub flush_string_right
  #a ($string, $length, $char)
  # Flushes string $string from the right with characters $char up to the length $length,
  # and returns the flushed string. If the length of the original string is not less then
  # $length, the original string is returned.
  {
    my ($string, $length, $char) = @_;
    ( $char ) || ( $char = " " );
    my $count = $length - length($string);
    if ( $count > 0 )
      {
	$string .= ($char x $count);
      }
    return($string);
  }


sub format_words_as_block
  #a ($words_ref, $max_col, $indent_col, $col)
  # Formats a list of words as a block of text. The list of words is given as a reference,
  # $words_ref, which should point to a list of strings. These are converted into a new list
  # of strings which are formatted as a block of text. Each line is wrapped at $max_col
  # columns, and indented by $indent_col columns, except the first line in the case $col is
  # > 0. In that case, $col is regarded as the column of the end of the last line of a
  # preceeding piece of text which should be continued by $source. Within the strings of the
  # original list, no newlines are inserted. Returns the formatted text as a reference to a
  # list of strings. Each string is either an element of the original list, or a separator,
  # i.e., a blank or newline character. The last column value will be found in the variable
  # $last_format_col.
  {
    my $words_ref = $_[0];
    my $max_col = $_[1];
    my $indent_col = ( $_[2] or 0 );
    my $col = ( $_[3] or 0 );
    my $indent = (" " x $indent_col);

    my @target_list = ();

    for (my $i = 0; $i <= $#{$words_ref}; $i++)
      {
	if ( ( $col > 0 ) && ( $col + 1 + length(${$words_ref}[$i]) <= $max_col ) )
	  {
	    push(@target_list, " ", ${$words_ref}[$i]);
	    $col = $col + length(${$words_ref}[$i]) + 1;
	  }
	else
	  {
	    unless ( ( $i == 0 ) && ( $col == 0 ) )
	      {
		push(@target_list, "\n");
	      }
	    if ( $indent )
	      {
		push(@target_list, $indent);
	      }
	    push(@target_list, ${$words_ref}[$i]);
	    $col = length($indent) + length(${$words_ref}[$i]);
	  }
      }

    $last_format_col = $col;
    return(\@target_list);
  }

sub underline
  #a ($text, $underline_char)
  # Underlines $text by repetitions of $underline_char, which defaults to "-".
  {
    my ($text, $underline_char) = @_;
    $underline_char ||= '-';
    return($text . "\n" . ($underline_char x length($text)));
  }

sub bar
  #a ($text, $width, $char, $min, $dist)
  # Puts $text in a bar of characters $char. The latter defaults to "=". $text is centered.
  # The other arguments have the following meaning:
  #D
  #  $width & Total width of the bar (default: 79). \\
  #  $min   & Minimum size of the parts of the bar left and right from $text
  #           (default: 4).\\
  #  $dist  & Distance between $text and the parts of the bar left and right from
  #           $text (default: 1).
  #/D
  {
    my ($text, $width, $char, $min, $dist) = @_;
    $width ||= 79;
    $char ||= '=';
    $min ||= 4;
    $dist ||= 1;
    my $fill_length = $width - length($text) - 2*$dist;
    my $fill_length_left = int($fill_length/2);
    my $fill_length_right = $fill_length - $fill_length_left;
    ( $fill_length_left >= $min ) || ( $fill_length_left = $min );
    ( $fill_length_right >= $min ) || ( $fill_length_right = $min );
    return(($char x $fill_length_left) .
	   (' ' x $dist) . $text . (' ' x $dist) .
	   ($char x $fill_length_right));
  }

sub box
  #a ($text, $symbol, $width, $border_size, $padding_size)
  # Puts $text in a box with borders made of characters $symbol. $symbol defaults to
  # "*". $text is centered. $width is the total horizontal size of the box (default: 79),
  # and $border_size the left and right borders.
  {
    my ($text, $width, $symbol, $border_size) = @_;
    $symbol ||= '*';
    $width ||= 79;
    $border_size ||= 10;
    my $fill_length = $width - length($text) - 2*$border_size;
    my $fill_length_left = int($fill_length/2);
    my $fill_length_right = $fill_length - $fill_length_left;
    my $outline = $symbol x $width ;
    my $frame = $symbol x $border_size .  ' ' x ($width - 2 * $border_size) .
                $symbol x $border_size;
    my $inline  = $symbol x $border_size . ' ' x $fill_length_left . $text .
                  ' ' x $fill_length_right . $symbol x $border_size;
    return("\n" . $outline. "\n" . $frame . "\n" . $inline. "\n" . $frame ."\n" .
           $outline . "\n");
  }

sub get_notation
  #a ($name_or_ref)

  # Return an appropriate string notation for the value of the variable specified by
  # $name_or_ref. The latter may be
  #l
  #  - the variable itself,
  #  - the qualified name of the variable (as a string),
  #  - a reference to the variable.
  #/l
  {
    my ($name_or_ref) = @_;
    my $undef_notation = '-undefined-';

    if ( ! ref($name_or_ref) )
      {
	if ( $name_or_ref =~ m/^\$(.*)$/ )
	  {
	    return(defined(${$1}) ? get_notation(\${$1}) : $undef_notation);
	  }
	elsif ( $name_or_ref =~ m/^\@(.*)$/ )
	  {
	    return(defined(@{$1}) ? get_notation(\@{$1}) :  $undef_notation);
	  }
	elsif ( $name_or_ref =~ m/^\%(.*)$/ )
	  {
	    return(defined(%{$1}) ? get_notation(\%{$1}) :  $undef_notation);
	  }
	else
	  {
	    return($name_or_ref);
	  }
      }
    elsif ( ref($name_or_ref) eq 'SCALAR' )
      {
	return(${$name_or_ref});
      }
    elsif ( ref($name_or_ref) eq 'ARRAY' )
      {
	my @values = @{$name_or_ref};
	foreach my $value (@values)
	  {
	    $value = get_notation(\$value);
	  }
	return('(' . join(', ', @values) . ')');
      }
    elsif ( ref($name_or_ref) eq 'HASH' )
      {
	my @key_value_pairs = ();
	foreach my $key (keys(%{$name_or_ref}))
	  {
	    push(@key_value_pairs, "'$key' => " . get_notation(\ ($name_or_ref->$key)));
	  }
	return('(' . join(', ', @key_value_pairs) . ')');
      }
    elsif ( ref($name_or_ref) eq 'REF' )
      {
	return('\\' . get_notation(${$name_or_ref}));
      }
    else
      {
	return($name_or_ref);
      }
  }

# --------------------------------------------------------------------------------
#1 Composing error messages
# --------------------------------------------------------------------------------

sub compose_error_message
  # ($explanation, $source, $source_name, $pos, $mark)
  {
    my ($explanation, $source, $source_name, $pos, $mark) = @_;
    $pos ||= pos(${$source});
    $mark ||= '<*pos*>';

    my $line_number = get_line_number_at($pos, ${$source});
    my ($code_before, $code_after) = get_text_at($pos, ${$source});
    my $column_number = length($code_before);

    my $message
      = "Error in source $source_name, line $line_number, column $column_number"
      . "\n(Position marked by \"$mark\"):"
      . "\n"
      . "\n$code_before$mark$code_after"
      . "\n"
      . "\n$explanation"
      . "\n";

    $message = $error_message_headline . $message if ( $error_message_headline );
    $message =~ s/^/$error_message_margin_left/mg if ( $error_message_margin_left );

    return($message);
  }

# --------------------------------------------------------------------------------
#1 String array utils
# --------------------------------------------------------------------------------

sub rm_string_from_array
  #a ($element_or_ref, $array_ref)
  # Removes all occurrences of specified elements from the array to which the reference
  # $array_ref points. Returns a reference to the altered array. The elements to remove are
  # specified by $element_or_ref, which my be a reference to a list of strings or a
  # string. In the first case, the elements of the list are removed, in the second case, the
  # string is removed.
  {
    my ($element_or_ref, $array_ref) = @_;
    my @elements = (ref($element_or_ref) ? @{$element_or_ref} : ($element_or_ref));
    foreach my $element (@elements)
      {
	for ( my $i = 0; $i < scalar(@{$array_ref}); $i++ )
	  {
	    if ( $array_ref->[$i] eq $element )
	      {
		splice(@{$array_ref}, $i, 1);
	      }
	  }
      }
    return($array_ref);
  }

sub subst_string_in_array
  #a ($str, $subst, $array_ref)
  # Substitutes all occurrences of string $str by string $ubst in the array to which the
  # reference $array_ref points. This array should contain only strings.
  {
    my ($str, $subst, $array_ref) = @_;
    for ( my $i = 0; $i < scalar(@{$array_ref}); $i++ )
      {
	if ( $array_ref->[$i] eq $str )
	  {
	    $array_ref->[$i] = $subst;
	  }
      }
  }

init();
return(1);
