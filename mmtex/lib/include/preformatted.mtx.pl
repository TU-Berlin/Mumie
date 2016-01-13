package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: preformatted.mtx.pl,v 1.13 2008/08/20 12:16:13 rassy Exp $

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
# Defines the \code{preformatted} environment.


log_message("\nLibrary preformatted ", '$Revision: 1.13 $', "\n");

# ------------------------------------------------------------------------------------------
# Auxiliaries
# ------------------------------------------------------------------------------------------

sub start_preformatted_line
  {
    my ($line_start) = @_;
    my $offset = $scan_proc->{preformatted_offset};

    log_message("\nstart_preformatted_line\n");
    log_data('Line start', "-->$line_start<--", 'Offset', $offset);

    my ($newline, $length);
    if ( $line_start =~ m/^\n/ )
      {
	$newline = "\n";
	$length = length($line_start) - 1;
      }
    else
      {
	$newline = '';
	$length = length($line_start);
      }

    log_message("\nDEBUG\n");
    log_data('$newline', "-->$newline<--", '$length', $length);

    if ( $offset )
      {
	my $new_length = $length + $offset;
	if ( $new_length > 0 )
	  {
	    $line_start = $newline . (' ' x $new_length);
	  }
      }

    xml_pcdata($line_start);
  }

# ------------------------------------------------------------------------------------------
# Environment `preformatted'
# ------------------------------------------------------------------------------------------

sub begin_preformatted
  {
    log_message("\nbegin_preformatted\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $attribs = {};

    if ( $ {$opt_args}[0] )
      {
	my $class = get_data_from_arg(0, $opt_args, $pos_opt_args, "ENV");

	if ( ! grep(/^$class$/,
		    "normal", @{$scan_proc->{env_table}->{preformatted}->{classes}}) )
	  {
	    &{$scan_proc->{error_handler}}("Invalid class of \`preformatted\': ", $class);
	  }

        $attribs->{class} = $class;
      }

    start_xml_element("preformatted", $attribs, "SEMI_DISPLAY");
    own_allowed_tokens();
    disallow_tokens(['par', 'plain_text']);
    allow_tokens(['begin_pref_line', 'pref_plain_text'], 'MODIFY', 'PREPEND');
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{forced_layout} = "STRICTLY_VERBATIM";
    install_cmds(['blanks', 'currentindent'], 'preformatted');
    install_envs(['shift'], 'preformatted');
  }


sub end_preformatted
  {
    log_message("\nend_preformatted\n");
    close_xml_element("preformatted", "SEMI_DISPLAY");
  }

# ------------------------------------------------------------------------------------------
# Environment `shift`
# ------------------------------------------------------------------------------------------

sub begin_shift
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $offset = get_data_from_arg(0, $man_args, $pos_man_args, 'ENV');
    $offset =~ s/^\s+|\s+$//g;
    if ( $offset !~ m/^-?[0-9]+$/ )
      {
	&{$scan_proc->{error_handler}}("Invalid shift number: $offset");
      }

    log_message("\nbegin_shift\n");
    log_data('Offset', $offset);

    $scan_proc->{preformatted_offset} = $offset;
  }

sub end_shift
  {
    log_message("\nend_shift\n");
  }

# --------------------------------------------------------------------------------
# Commands
# --------------------------------------------------------------------------------

sub execute_blanks
  #a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
  # Execute function of the '\blanks' command.
  {
    log_message("\nexecute_blanks\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $num = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    $num =~ s/^\s+|\s+$//g;
    if ( $num !~ m/^[0-9]+$/ )
      {
	&{$scan_proc->{error_handler}}("Invalid number of blanks: $num");
      }
    xml_pcdata(' ' x $num);
  }

sub execute_currentindent
  #a ()
  # Execute function of the '\currentindent' command.
  {
    my $current_indent =
      ( ${$scan_proc->{prim_source}} =~ m/^( +).*\G/m ? length($1) : 0 );

    log_message("\nexecute_currentindent\n");
    log_data('Indent', $current_indent);

    xml_pcdata($current_indent);
  }

# --------------------------------------------------------------------------------
# Extra tokens
# --------------------------------------------------------------------------------

$preformatted_token_table ||= $default_token_table;

$preformatted_token_table->{begin_pref_line}
  = {
     'tester' => sub { test_regexp('(?:\n[ \t]*|^[ \t]+)') },
     'handler' => sub { start_preformatted_line($scan_proc->{last_token}) },
    };

my $pref_plain_text_regexp =
  '[' . $nonnewline_nonwhitesp_chars . $nonnewline_whitesp_chars . ']+';

$preformatted_token_table->{pref_plain_text}
  = {
     'tester' => sub { test_regexp($pref_plain_text_regexp) },
     'handler' =>
       sub
         {
	   my $text = $scan_proc->{last_token};
	   &{$scan_proc->{output_functions}->{plain_text}}($text);
	 },
    };

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{preformatted}->{initializer} = sub
  {
    my $preformatted_env_table =
      {
       'preformatted' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 0,
          'begin_function' => \&begin_preformatted,
          'end_function' => \&end_preformatted,
          'classes' => ['code', 'output'],
          'doc' =>
             {
              'opt_args' => [['class', 'Class']],
              'description' => 'Preformatted output, preserves whitespaces and linebreakes. ' .
                               'Commands and environments are executed.',
             }
         },
       'shift' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'begin_function' => \&begin_shift,
          'end_function' => \&end_shift,
          'doc' =>
             {
              'man_args' => [['num', 'Number of characters to shift']],
              'description' => 'Shifts the content of the environment by %{1} characters. ' .
                               '%{1} may be negative.',
             }
         },
      };

    my $preformatted_cmd_table =
      {
       'blanks' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'execute_function' => \&execute_blanks,
          'doc' =>
            {
             'man_args' => [['num', 'Number of blanks to insert']],
             'description' => 'Inserts %{1} blanks.',
            }
         },
       'currentindent' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'execute_function' => \&execute_currentindent,
          'doc' =>
            {
             'description' => 'Prints the indent of the current line.',
            }
         },
      };

    deploy_lib('preformatted', $preformatted_cmd_table, $preformatted_env_table,
               'TOPLEVEL', 'TOPLEVEL');
  };

return(TRUE);

