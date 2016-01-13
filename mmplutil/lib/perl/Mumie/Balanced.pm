package Mumie::Balanced;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Balanced.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

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
   scan_until_balanced
   scan_balanced
   $balanced_begin
   $balanced_end
   $balanced_exp
   $balanced_found
   $open_exp
   $close_exp
   $close_exp_end
  );


# --------------------------------------------------------------------------------
# The init function
# --------------------------------------------------------------------------------

sub init
  {
    return() if ( $init_done );

    $balanced_begin = 0;
    # Stores the beginning of the expression found by the last scan_until_balanced()
    # or scan_balanced() call
	
    $balanced_end = 0;
    # Stores the end of the expression found by the last scan_until_balanced() or
    # scan_balanced() call

    $balanced_exp = 0;
    # Stores the expression found by the last scan_until_balanced() or 
    # scan_balanced() call

    $close_exp = 0;
    # Stores the terminating expression found by the last
    # scan_until_balanced() or scan_balanced() call

    $open_exp = 0;
    # Stores the starting expression found by the last
    # scan_until_balanced() or scan_balanced() call

    $close_exp_end = 0;
    # Stores the end of the terminating expression found by the last
    # scan_until_balanced() or scan_balanced() call

    $balanced_found = FALSE;
    # TRUE or FALSE depending on whether the last balanced expression search was
    # successfull, i.e., an balanced expression was found, or not.

    $default_comments = [];
    # Reference to a list specifying the comments that should be skipped. Must be of the
    # form
    #c
    #   [
    #     [$start_regexp_1, $end_regexp_1],
    #     [$start_regexp_2, $end_regexp_2],
    #                    .
    #                    .
    #                    .
    #   ]
    #/c
    # where $start_regexp_n and $end_regexp_n are regular expressions describing the
    # beginning resp. end of a comment. For example, for the Java programming language
    # this would be
    #c
    #   [
    #     ["\\/\\*", "\\*\\/"],
    #     ["\\/\\*\\*", "\\*\\/"],
    #     ["\\/\\/", "\\n"]
    #   ]
    #/c

    $default_strings = [];
    # Reference to a list specifying the quoted expressions that should be skipped  (it is
    # called "default_strings" because in programming languages, these quoted expressions
    # are often strings literals). Must be of the form
    #c
    #   [
    #     [$start_regexp_1, $end_regexp_1],
    #     [$start_regexp_2, $end_regexp_2],
    #                    .
    #                    .
    #                    .
    #   ]
    #/c
    # where $start_regexp_n and $end_regexp_n are regular expressions describing the
    # beginning resp. end of a quoted expression. For example, for the Java programming
    # language this would be
    #c
    #   [
    #     ["(?<!\\\\)\\\"", "(?<!\\\\)\\\""]
    #   ]
    #/c

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub scan_until_balanced
  #a ($open_regex, $close_regex, $strref, $comments, $strings)
  # Scans the text in the string given by the reference $strref from the actual position
  # (see below) up to the next match of the regular expression $close_regex, where balanced
  # pairs of $open_regex and $close_regex matches are skipped. $close_regex is another  regular
  # expression. Balanced means that both matches belong to the same nesting depth. Comments
  # and quoted expressions are skipped provided they are specified in $comments and $strings,
  # respectively.
  #
  # The actual position is the one pos(${$strref}) would yield.  Returns the scanned text
  # without the terminating $close_regex match if the search succeds. Afterwards, the scanned
  # text, its beginning and end position in the string, and the end position of the
  # terminating $close_regex match in the string are_contained in $balanced_exp,
  # $balanced_begin, $balanced_end, and $close_regex_end, repectively, and
  # $balanced_found is TRUE. If the search fails, FALSE is returned, the values of
  # $balanced_exp, $balanced_begin, $balanced_end, and $close_regex_end, are not
  # altered, and $balanced_found is FALSE.
  {
    my $open_regex = $_[0];
    my $close_regex = $_[1];
    my $strref = $_[2];
    my $comments = ( $_[3] || $default_comments );
    my $strings = ( $_[4] || $default_strings );

    my $start = ( pos($ {$strref}) || 0 );
    my $num_comments = scalar(@{$comments});
    my $num_strings = scalar(@{$strings});
    my $regexp = "($open_regex)|($close_regex)";
    for (my $i = 0; $i < $num_comments; $i++)
      {
	$regexp .= "|(" . $comments->[$i]->[0] . ")";
      }
    for (my $i = 0; $i < $num_strings; $i++)
      {
	$regexp .= "|(" . $strings->[$i]->[0] . ")";
      }

    my $depth = 1;
    my $runaway_comment = FALSE;

    while ( ( $depth > 0 ) && ( $ {$strref} =~ m/$regexp/gc ) && ( ! $runaway_comment ) )
      {
	if ( $1 )
	  {
	    $depth++;
	  }
	elsif ( $2 )
	  {
	    $depth--;
	  }
	else
	  {
	    # Proceeding to the subexpression that matched
	    my $i = 2;
	    do { $i++ } until ( $$i );

	    my $k;
	    my $comments_or_strings;

	    # Checking whether it is a comment or a string
	    if ( $i <= 2 + $num_comments )
	      {
		# It's a comment
		$comments_or_strings = $comments;
		$k = $i - 3;
	      }
	    else
	      {
		# It's a string
		$comments_or_strings = $strings;
		$k = $i - $num_comments - 3;
	      }

	    ( $ {$strref} =~ m/$comments_or_strings->[$k]->[1]/gc )
	      or ( $runaway_comment = TRUE );

	  }
      }
    if ( ( $depth == 0 ) && ( ! $runaway_comment ) )
      {
	substr(${$strref}, $start, pos(${$strref}) - $start) =~ m/$close_regex$/;
	$balanced_exp = $`;
 	$balanced_begin = $start;
	$close_exp = $&;
	$close_exp_end = pos(${$strref});
	$balanced_end = $balanced_begin + length($balanced_exp);
	$balanced_found = TRUE;
	return($balanced_exp);
      }
    else
      {
	$balanced_found = FALSE;
	return(FALSE);
      }
  }

sub scan_balanced
  #a ($open_regex, $close_regex, $strref, $comments, $strings)
  {
    my $open_regex = $_[0];
    my $close_regex = $_[1];
    my $strref = $_[2];
    my $comments = ( $_[3] || $default_comments );
    my $strings = ( $_[4] || $default_strings );

    if ( $ {$strref} =~ m/\G$open_regex/gc )
      {
	my $maybe_open_exp = $&;
	scan_until_balanced($open_regex, $close_regex, $strref);
	if ( $balanced_found )
	  {
	    $open_exp = $maybe_open_exp;
	    return($balanced_exp);
	  }
	else
	  {
	    return(FALSE);
	  }
      }
    else
      {
	$balanced_found = FALSE;
	return(FALSE);
      }
  }

sub scan_until_match
  # ($close_regex, $strref, $comments, $strings)
  # Scans the text in the string given by the reference $strref from the actual position
  # (see below) up to the next match of the regular expression $close_regex. $close_regex is
  # another regular expression. Comments and quoted expressions are skipped provided they
  # are specified in $comments and $strings, respectively.
  #
  # The actual position is the one pos(${$strref}) would yield.  Returns the scanned text
  # without the terminating $close_regex match if the search succeds. Afterwards, the scanned
  # text, its beginning and end position in the string, and the end position of the
  # terminating $close_regex match in the string are_contained in $balanced_exp,
  # $balanced_begin, $balanced_end, and $close_regex_end, repectively, and
  # $balanced_found is TRUE. If the search fails, FALSE is returned, the values of
  # $balanced_exp, $balanced_begin, $balanced_end, and $close_regex_end, are not
  # altered, and $balanced_found is FALSE.
  {
    my $close_regex = $_[0];
    my $strref = $_[1];
    my $comments = ( $_[2] || $default_comments );
    my $strings = ( $_[3] || $default_strings );
    my $start = ( pos($ {$strref}) || 0 );
    my $num_comments = scalar(@{$comments});
    my $num_strings = scalar(@{$strings});
    my $regexp = "($close_regex)";
    for (my $i = 0; $i < $num_comments; $i++)
      {
	$regexp .= "|(" . $comments->[$i]->[0] . ")";
      }
    for (my $i = 0; $i < $num_strings; $i++)
      {
	$regexp .= "|(" . $strings->[$i]->[0] . ")";
      }

    my $finished = FALSE;
    my $runaway_comment = FALSE;

    while ( (! $finished ) && ( ! $runaway_comment ) && ( $ {$strref} =~ m/$regexp/gc ) )
      {
	if ( $1 )
	  {
	    $finished = TRUE;
	  }
	else
	  {
	    # Proceeding to the subexpression that matched
	    my $i = 1;
	    do { $i++ } until ( $$i );

	    my $k;
	    my $comments_or_strings;

	    # Checking whether it is a comment or a string
	    if ( $i <= 1 + $num_comments )
	      {
		# It's a comment
		$comments_or_strings = $comments;
		$k = $i - 2;
	      }
	    else
	      {
		# It's a string
		$comments_or_strings = $strings;
		$k = $i - $num_comments - 2;
	      }

	    ($ {$strref} =~ m/$comments_or_strings->[$k]->[1]/gc)
	      or ( $runaway_comment = TRUE );
	  }
      }
    if ( $finished )
      {
	substr(${$strref}, $start, pos(${$strref}) - $start) =~ m/$close_regex$/;
	$balanced_exp = $`;
	$close_exp = $&;
 	$balanced_begin = $start;
	$close_exp_end = pos(${$strref});
	$balanced_end = $balanced_begin + length($balanced_exp);
	$balanced_found = TRUE;
	return($balanced_exp);
      }
    else
      {
	$balanced_found = FALSE;
	return(FALSE);
      }
  }


init();
return(1);
