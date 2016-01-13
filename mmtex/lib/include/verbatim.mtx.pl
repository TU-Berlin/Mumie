package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: verbatim.mtx.pl,v 1.23 2008/11/10 11:53:14 rassy Exp $

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


log_message("\nLibrary verbatim ", '$Revision: 1.23 $', "\n");

$verbatim_end_name_regexp = "^[a-zA-Z]+\$";

sub get_verbatim
  {
    my ($end_mark, $trim_verbatim_text) = @_;

    log_message("\nget_verbatim 1/2\n");
    log_data("End mark", $end_mark, "Trim", $trim_verbatim_text);

    # Quoting chars which might have a special meaning in regular expressions
    my $end_mark_regexp = $end_mark;
    $end_mark_regexp =~ s/[.+*?!<>|(){}\[\]\$]/\\$&/g;

    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{prim_output_list} = [];
    $scan_proc->{output_list} = $scan_proc->{prim_output_list};
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{forced_layout} = "STRICTLY_VERBATIM";
    $scan_proc->{token_table}
      = {
	 "verbatim"
           => {
	       "tester"
	         => sub { test_regexp("([\\s\\S]*?)$end_mark_regexp", 1) },
	       "handler"
	         => sub
	              {
			my $verbatim_text = $scan_proc->{last_token};

			if ( $xml_special_char_output_mode eq 'AS_NUMCODES' )
			  {
			    quote_xml(\$verbatim_text, 'BY_NUMCODES');
			  }
			elsif ( $xml_special_char_output_mode =~ m/^(?:AS_ENTITIES|LITERAL)$/ )
			  {
			    quote_xml(\$verbatim_text, 'BY_ENTITIES');
			  }

			if ( $trim_verbatim_text )
			  {
			    $verbatim_text =~ s/^\s*\n|\n\s*$//g;
			  }
			&{$scan_proc->{output_functions}->{plain_text}}($verbatim_text);
		      }
	      }
	};
    $scan_proc->{allowed_tokens} = ["verbatim"];
    $scan_proc->{scan_failed_handler}
      = sub { &{$scan_proc->{error_handler}}("Verbatim end mark ($end_mark) missing") };

    Mumie::Scanner::scan_next_token();

    my $output_list = $scan_proc->{output_list};
    reset_scan_proc();

    log_message("\nget_verbatim 2/2\n");

    return($output_list);
  }

sub execute_verbatim
  {
    log_message("\nexecute_verbatim 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    my $class = "normal";
    if ( $opt_args->[0] )
      {
	$class = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD", "verbatim", "class_arg");
      }
    if ( ! grep($class eq $_, @{$scan_proc->{cmd_table}->{verbatim}->{classes}}) )
      {
	&{$scan_proc->{error_handler}}("Invalid \\verbatim class: $class");
      }

    my $end_name = "endverbatim";
    if ( $opt_args->[1] )
      {
	$end_name
	  = get_data_from_arg(1, $opt_args, $pos_opt_args, "CMD", "verbatim", "end_name_arg");
      }
    if ( $end_name !~ m/$verbatim_end_name_regexp/ )
      {
	&{$scan_proc->{error_handler}}("Invalid \\verbatim end name: $end_name");
      }

    my $verbatim_output_list
      = get_verbatim("\\\\$end_name", $scan_proc->{trim_verbatim_text});

    my $attribs = {};
    unless ( $class eq "normal" )
      {
	$attribs->{class} = $class;
      }
    start_xml_element("verbatim", $attribs, "STRICTLY_VERBATIM");
    append_to_output(@{$verbatim_output_list});
    close_xml_element("verbatim", "STRICTLY_VERBATIM");

    log_message("\nexecute_verbatim 2/2\n");
  }

sub execute_verb
  {
    log_message("\nexecute_verb 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $class = "normal";
    if ( $ {$opt_args}[0] )
      {
	$class = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD", "verb", "class_arg");
      }
    if ( ! grep($class eq $_, @{$scan_proc->{cmd_table}->{verb}->{classes}}) )
      {
	&{$scan_proc->{error_handler}}("Invalid \\verb class: $class");
      }

    my $end_mark;
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{prim_output_list} = [];
    $scan_proc->{output_list} = $scan_proc->{prim_output_list};
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{token_table}
      = {
	 "verb_mark"
           => {
	       "tester"
	         => sub { test_regexp(".", 0) },
	       "handler"
	         => sub
	              {
			$end_mark = $scan_proc->{last_token};
		      }
	      }
	};
    $scan_proc->{allowed_tokens} = ["verb_mark"];
    Mumie::Scanner::scan_next_token();
    reset_scan_proc();

    my $verbatim_output_list = get_verbatim($end_mark, FALSE);

    my $attribs = {};
    unless ( $class eq "normal" )
      {
	$attribs->{class} = $class;
      }
    start_xml_element("verb", $attribs, "INLINE");
    append_to_output(@{$verbatim_output_list});
    close_xml_element("verb", "STRICTLY_VERBATIM");

    log_message("\nexecute_verb 2/2\n");
  }

sub start_verbatim_body
  {
    my ($env, $trim) = @_;
    $trim ||= 'NO_TRIM';
    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{forced_layout} = "STRICTLY_VERBATIM";
    $scan_proc->{token_table} =
      {
       "verbatim" =>
         {
          "tester" => sub { test_regexp("([\\s\\S]*?)(?=\\\\end{$env})") },
          "handler" => sub
            {
              my $text = $scan_proc->{last_token};
              $text =~ s/^\n|\n$//g if ( $trim eq 'TRIM' );
              chars_to_numcodes(\$text); # (<- Workaround)
              xml_pcdata($text);
              reset_scan_proc();
            }
         }
      };
    $scan_proc->{allowed_tokens} = ["verbatim"];
  }

sub begin_verbatim
  {
    log_message("\nbegin_verbatim\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $attribs = {};

    if ( $ {$opt_args}[0] )
      {
	my $class_code = $ {$opt_args}[0];
	my $pos_class_code = $ {$pos_opt_args}[0];

	my $class = get_data_from_subexp(\$class_code, $pos_class_code);

	if ( ! grep($_ eq $class, "normal", @{$scan_proc->{env_table}->{verbatim}->{classes}}) )
	  {
	    &{$scan_proc->{error_handler}}("Invalid class for \`verbatim\': ", $class);
	  }

        $attribs->{class} = $class;
      }

    start_xml_element("verbatim", $attribs, "SEMI_DISPLAY");
    start_verbatim_body('verbatim');
  }

sub end_verbatim
  {
    log_message("\nend_verbatim\n");
    close_xml_element("verbatim", "SEMI_DISPLAY");
  }


# --------------------------------------------------------------------------------=
# Command- and environment tables for export
# --------------------------------------------------------------------------------



# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

# $import_cmd_tables->{verbatim} = $verbatim_cmd_table;
# $import_env_tables->{verbatim} = $verbatim_env_table;

# $scan_proc->{trim_verbatim_text} = TRUE;


$lib_table->{verbatim}->{initializer} = sub
  {


    my $verbatim_cmd_table =
      {
       'verbatim' =>
         {
          "num_opt_args" => 2,
          "num_man_args" => 0,
          "execute_function" => \&execute_verbatim,
          "is_par_closer" => TRUE,
          "classes" => ["normal", "code"],
          "doc" =>
            {
             "opt_args" =>
               [
                ["class", "The class of this verbatim text"],
                ["endname", "Name of the (pseudo-)command that ends the verbatim text"]
               ],
             "description" =>
               'Prints all input literately until the string "\%[2]" is found. ' .
               '%[2] should be a command name without the leading backslash. ' .
               'It defaults to "endverbatim".'
            }
         },
       'verb' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "execute_function" => \&execute_verb,
          "classes" => ["normal", "code"],
          "is_par_starter" => TRUE,
         }
      };

    my $verbatim_env_table =
      {
       'verbatim' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "begin_function" => \&begin_verbatim,
          "end_function" => \&end_verbatim,
          "classes" => []
         }
      };

    deploy_lib('verbatim', $verbatim_cmd_table, $verbatim_env_table, 'TOPLEVEL', 'TOPLEVEL');
    $scan_proc->{trim_verbatim_text} = TRUE;
  };

return(TRUE);
