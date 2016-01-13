package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: german.mtx.pl,v 1.10 2007/07/11 15:56:14 grudzin Exp $

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

# ----------------------------------------------------------------------------------------
#1 Description
# ----------------------------------------------------------------------------------------
#
# Easy input of german special characters.

log_message("\nLibrary german ", '$Revision: 1.10 $ ', "\n");


sub handle_german_umlaut
  #a ($char)
  # Handles a "german_umlaut" token. $char is the character from which the umlaut is to be
  # created. Calls `xml_special_char` with the appropriate entity name.
  {
    my $char = $_[0];
    my $entity = ($char eq "s" ? "szlig" : $char . "uml");
    log_message("\nhandle_german_umlaut 1/2\n");
    log_data('Character', $char, 'Entity', $entity);
    xml_special_char($entity);
    log_message("\nhandle_german_umlaut 2/2\n");
  }

sub handle_german_left_quotes
  #a ()
  # Handles a "german_left_quotes" token. Calls `xml_special_char` with
  # $scan_proc->{german_left_quotes} as entity name.
  {
    xml_special_char($scan_proc->{german_left_quotes});
  }

sub handle_german_right_quotes
  #a ()
  # Handles a "german_right_quotes" token. Calls `xml_special_char` with
  # $scan_proc->{german_right_quotes} as entity name.
  {
    xml_special_char($scan_proc->{german_right_quotes});
  }

sub init_german_tokens
  #a ()
  # Defines tokens for german special characters.
  {
    $german_token_table ||= $default_token_table;

    $german_token_table->{german_umlaut} =
      {
       "tester" => sub { test_regexp("\\\"([aouAOUs])", 1) },
       "handler" => sub { handle_german_umlaut($scan_proc->{last_token}) },
       "is_par_starter" => TRUE,
      };

    $german_token_table->{german_left_quotes} =
      {
       "tester" => sub { test_regexp('"`') },
       "handler" => \&handle_german_left_quotes,
       "is_par_starter" => TRUE,
      };

    $german_token_table->{german_right_quotes} =
      {
       "tester" => sub { test_regexp('"\'') },
       "handler" => \&handle_german_right_quotes,
       "is_par_starter" => TRUE,
      };
  }

$lib_table->{german}->{initializer} = sub
  {
    init_german_tokens();
    $scan_proc->{german_left_quotes} = 'bdquo';
    $scan_proc->{german_right_quotes} = 'rdquo';
    @basic_token_types =
      ('german_umlaut', 'german_left_quotes', 'german_right_quotes', @basic_token_types);
  };

return(1);
