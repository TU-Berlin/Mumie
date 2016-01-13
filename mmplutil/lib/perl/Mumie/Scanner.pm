package Mumie::Scanner;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Scanner.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

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
# Generic text scanner. Generic means: not specific to a special language.

# --------------------------------------------------------------------------------
# Scan process data (the $scan_proc variable)
# --------------------------------------------------------------------------------
#
# $scan_proc is the most important variable defined by this module is is a
# reference to a hash holding the scan process data. This hash should contain at
# least the following entries (keys vs. values):
#
#H
#  source              & Reference to the scanned string \\
#  token_table         & Reference to a hash mapping each token type to a reference to
#                        The \emph{property table} of that token type (see below).
#                        a hash containing data specific to that token type \\
#  allowed_tokens      & Reference to a list of token types allowed in the next test \\
#  scan_result_handler & Reference to the function which handels the scan result \\
#  scan_failed_handler & Reference to the function called when no token could be scanned \\
#  error_handler       & Reference to the function which handels errors \\
#  last_token          & Token found in the last test \\
#  last_token_type     & Type of the token found in the last test \\
#  message             & Used for inter-function communication. Can contain message strings
#                        or any data.\\
#/H
#
# The *property table* of a token type is a hash that contains data and functions
# specific to that token type. It should contain at least one entry, i.e.,
#
#H
#  tester              & reference to the function testing for a token of the respective
#                        type \\
#/H

# The variable @scan_proc_list holds a stack of scan processes, i.e., a list of references of
# the same form as $scan_proc. Each reference points to a hash which represents a scan process.
# The last scan process in the list is the current one, is is the one $scan_proc points to. The
# other elements in the list represent previous scan processes.


use constant VERSION => '$Revision: 1.2 $ ';

use Mumie::Boolconst;
use Mumie::Text qw(/.+/);
use Mumie::Balanced qw(/.+/);
use Mumie::List qw(/copy_value/);

require Exporter;

@ISA = qw(Exporter);
@EXPORT_OK = qw
  (
   $default_error_handler
   $default_scan_failed_handler
   $mark_string
   $scan_proc
   DEFAULT_MARK_STRING
   access_scan_proc_list
   exists_allowed_token
   get_scan_proc
   get_scan_proc_depth
   new_scan_proc
   notify_scan_error
   reset_scan_proc
   scan
   scan_allowed_token
   scan_next_token
   scan_token
   skip_whitesp
   test_balanced
   test_inside_balanced
   test_regexp
   to_parent_scanproc
  );

# --------------------------------------------------------------------------------
# The init function
# --------------------------------------------------------------------------------

sub init
  {
    return() if ( $init_done );

    $scan_proc = {};
    @scan_proc_list = ();

    $default_error_handler = \&notify_scan_error;
    $default_scan_failed_handler = sub { notify_scan_error("Can't identify token") };

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

# --------------------------------------------------------------------------------
# Auxiliaries
# --------------------------------------------------------------------------------

sub get_scan_proc_depth
  #a ()
  # Returnes the size of the scan process stack, i.e., the length of @scan_proc_list.
  {
    return(scalar(@scan_proc_list));
  }

sub access_scan_proc_list
  #a (number)
  # Returnes a reference to the scan_proc_list with the given number
  {
    my $number = shift();
    return($scan_proc_list[$number]);
  }

sub get_scan_proc
  #a ($depth)
  # Returnes a (reference to the) scan proc with the given depth.
  {
    my $depth = shift();
    return($scan_proc_list[$depth-1]);
  }

sub to_parent_scanproc
  # (textsring)
  # This function copies the value determinated by the textstring from scan_proc
  # one level upwards, to make it available to the parent scan_process
  {
    my $textstring = shift();
    my $src = eval('$scan_proc'.$textstring);

    if ($#scan_proc_list)
      {
	$dst_ptr = $scan_proc_list[$#scan_proc_list - 1];
	eval('$dst_ptr' . $textstring . ' = copy_value($src); ');
     }
    else
      {
	&{$default_programming_error_handler}('to_parent_scanproc',"No parent scanproc exists");
      }
  }

# ------------------------------------------------------------------------------------------
# Error handler
# ------------------------------------------------------------------------------------------

sub notify_scan_error
  # (@message_list)
  {
    my $message = join("", @_);
    my $line_number
      = get_line_number_at(pos(${$scan_proc->{source}}), ${$scan_proc->{source}});
    my ($before, $after)
      = get_text_at(pos(${$scan_proc->{source}}), ${$scan_proc->{source}});
    my $column_number = length($before);
    my $text = "Error in line $line_number, column $column_number: $message";
    die("$text\n");
  }

# ------------------------------------------------------------------------------------------
# Creating and resetting scan processes
# ------------------------------------------------------------------------------------------

sub new_scan_proc
  # ($how_to_init)
  {
    my $how_to_init = ( shift() or "DEFAULT" );
    my $new_scan_proc = {};
    my $old_scan_proc = $scan_proc;

    if ( $how_to_init eq "DEFAULT" )
      {
	$new_scan_proc->{source} = \ "";
	$new_scan_proc->{token_table} = {};
	$new_scan_proc->{allowed_tokens} = [];
	$new_scan_proc->{scan_result_handler} = sub {};
	$new_scan_proc->{scan_failed_handler} = $default_scan_failed_handler;
	$new_scan_proc->{error_handler} = $default_error_handler;
	$new_scan_proc->{last_token} = "";
	$new_scan_proc->{last_token_type} = "";
	$new_scan_proc->{message} = "";
	pos(${$new_scan_proc->{source}}) = 0;
      }
    elsif ( $how_to_init eq "COPY" )
      {
	my $proc_to_copy = $scan_proc;
	%{$new_scan_proc} = %{$proc_to_copy};
      }
    elsif ( $how_to_init eq "EMPTY" )
      {
	# nothing to do in this case
      }

    push(@scan_proc_list, $new_scan_proc);
    $scan_proc = $new_scan_proc;
  }

sub reset_scan_proc
  # ()
  {
    pop(@scan_proc_list);
    $scan_proc = $scan_proc_list[$#scan_proc_list];
  }

# --------------------------------------------------------------------------------
# Test functions
# --------------------------------------------------------------------------------

sub test_regexp
  # ($regexp, $count)
  # Checks at the actual position in $scan_proc->{source} for $regexp. If succeeds,
  # sets $_proc->{last_token} to the $count-th parenthesized subexpression of
  # $regexp and returns TRUE. If $count is zero, which is its default value,
  # $scan_proc->{token} is set to the whole match. If the check fails, returns FALSE.
  {
    my ($regexp, $count) = @_;
    $count ||= 0;
    if ( ${$scan_proc->{source}} =~ m/\G$regexp/gmc )
      {
	$scan_proc->{last_token} = ($count == 0 ? $& : $$count);
	$scan_proc->{last_match} = $&;
	return(TRUE);
      }
    else
      {
	return(FALSE);
      }
  }

sub test_inside_balanced
  {
    my ($open_regex, $close_regex, $comments, $strings) = @_;
    scan_until_balanced($open_regex, $close_regex, $scan_proc->{source}, $comments, $strings);
    if ( $balanced_found )
      {
	$scan_proc->{last_token} = $balanced_exp;
	$scan_proc->{last_match} = $balanced_exp . $close_exp;
	return(TRUE);
      }
    else
      {
	return(FALSE);
      }
  }

sub test_balanced
  {
    my ($open_regex, $close_regex) = @_;
    scan_balanced($open_regex, $close_regex, $scan_proc->{source});
    if ( $balanced_found )
      {
	$scan_proc->{last_token} = $balanced_exp;
	$scan_proc->{last_match} = $open_exp . $balanced_exp . $close_exp;
	return(TRUE);
      }
    else
      {
	return(FALSE);
      }
  }

sub skip_whitesp
  # ()
  # Skips whitespaces.
  {
    ${$scan_proc->{source}} =~ m/\G\s*/gmc
  }

# --------------------------------------------------------------------------------
# Scanning
# --------------------------------------------------------------------------------

sub scan_token
  # ($TYPE)
  {
    my $type = $_[0];

    if ( &{$scan_proc->{token_table}->{$type}->{tester}} )
      {
	$scan_proc->{last_token_type} = $type;
	return(TRUE);
      }
    else
      {
	return(FALSE);
      }
  }

sub scan_allowed_token
  # ($INDEX)
  {
    my $index = $_[0];
    my $type = ${$scan_proc->{allowed_tokens}}[$index];
    return(scan_token($type));
  }

sub exists_allowed_token
  # ($INDEX)
  {
    my $index = $_[0];
    return(@{$scan_proc->{allowed_tokens}} > $index);
  }

sub scan_next_token
  {
    my $index = 0;
    my $token_found = FALSE;
    while ( (! $token_found) && ( exists_allowed_token($index) ) )
      {
	$token_found = scan_allowed_token($index);
	$index++;
      }

    if ( $token_found )
      {
	&{$scan_proc->{scan_result_handler}};
      }
    else
      {
	&{$scan_proc->{scan_failed_handler}};
      }
  }

sub scan
  {
    while ( ( pos(${$scan_proc->{source}}) < length(${$scan_proc->{source}}) )
            && ( $scan_proc->{message} ne "QUIT" ) )

      {
	scan_next_token();
      }
  }

init();
return(1);
