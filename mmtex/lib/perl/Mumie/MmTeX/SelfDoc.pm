package Mumie::MmTeX::SelfDoc;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: SelfDoc.pm,v 1.7 2007/07/11 15:56:15 grudzin Exp $

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
# Self-documentation of mmtex.

use constant VERSION => '$Revision: 1.7 $';
use Mumie::Boolconst;
use Mumie::Text qw(/.+/);
use Mumie::XML::Writer qw(/.+/);
use Mumie::XML::Characters qw(/.+/);
use Mumie::MmTeX::Serializer qw(:ALL);
use Mumie::MmTeX::DclLoader qw(:ALL);
use Mumie::MmTeX::LibLoader qw(:ALL);

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL = qw
    (
     $doc_arg_field_size
     $doc_indent
     $doc_max_col
     $doc_special_char_output_mode
     $xdoc_xsl_stylesheet
     dump_all_libs_cmd_or_env_doc
     dump_cmd_or_env_list
     dump_dcl_cmd_or_env_doc
     dump_lib_cmd_or_env_doc
     dump_lib_cmd_or_env_list
     dump_lib_list
     get_cmd_or_env_doc
     get_doc_list_entry
     process_doc_text
     process_doc_text_special_chars
     process_xdoc_text_special_chars
     write_cmd_or_env_xdoc
     write_dcl_xdoc
     write_lib_list_xdoc
     write_lib_xdoc
     write_xdoc_text
    );
  my @_CONVERTER = @_ALL;
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
     'CONVERTER' => \@_CONVERTER,
    );
}

sub init
  {
    $doc_indent = 2;
    $doc_max_col = 79;
    $doc_arg_field_size = 9;
    $doc_special_char_output_mode = "STRICTLY_LITERAL";
    $xdoc_xsl_stylesheet = undef();
    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub process_doc_text_special_chars
  #a ($text)
  # Processes special characters in a text. The latter is passed as $text, which must be a
  # reference to a string. The special characters are processed according to
  # $doc_special_char_output_mode. This function is used in the plain text version of the
  # documentation.
  {
    my ($text) = @_;
    if ( $doc_special_char_output_mode eq 'STRICTLY_LITERAL' )
      {
	entities_to_chars(\$text);
	numcodes_to_chars(\$text);
      }
    elsif ( $doc_special_char_output_mode eq 'LITERAL' )
      {
	entities_to_chars(\$text, ['lt', 'gt', 'amp']);
	numcodes_to_chars(\$text, ['&#60;', '&#62;', '&#38;']);
      }
    elsif ( $doc_special_char_output_mode eq 'AS_NUMCODES' )
      {
	chars_to_numcodes(\$text, FALSE, 'RESPECT_QUOTING');
	entities_to_numcodes(\$text);
      }
    elsif ( $doc_special_char_output_mode eq 'AS_ENTITIES' )
      {
	chars_to_entities(\$text, FALSE, 'RESPECT_QUOTING');
	numcodes_to_entities(\$text);
      }
    return($text);
  }

sub process_doc_text
  #a ($text, $data)
  # Formats a documentation string for the plain text output. The string is passed as a
  # reference $text, the command or environment information as a reference $data. The latter
  # is a reference to the entry of the command or environment in the command
  # resp. environment table.
  {
    my ($text, $data) = @_;

    for (my $i = 1; $i <= $data->{num_man_args}; $i++)
      {
	$placeholder = uc($data->{doc}->{man_args}->[$i - 1]->[0]);
	$text =~ s/\%\{$i\}/$placeholder/g;
      }

    for (my $i = 1; $i <= $data->{num_opt_args}; $i++)
      {
	$placeholder = uc($data->{doc}->{opt_args}->[$i - 1]->[0]);
	$text =~ s/\%\[$i\]/$placeholder/g;
      }

    $text = process_doc_text_special_chars($text);

    return($text);
  }

sub process_xdoc_text_special_chars
  {
    my ($text) = @_;
    if ( $xml_special_char_output_mode eq 'STRICTLY_LITERAL' )
      {
	entities_to_chars(\$text);
	numcodes_to_chars(\$text);
      }
    elsif ( $xml_special_char_output_mode eq 'LITERAL' )
      {
	entities_to_chars(\$text, ['lt', 'gt', 'amp']);
	numcodes_to_chars(\$text, ['&#60;', '&#62;', '&#38;']);
      }
    elsif ( $xml_special_char_output_mode eq 'AS_NUMCODES' )
      {
	chars_to_numcodes(\$text, FALSE, 'RESPECT_QUOTING');
	entities_to_numcodes(\$text);
      }
    elsif ( $xml_special_char_output_mode eq 'AS_ENTITIES' )
      {
	chars_to_entities(\$text, FALSE, 'RESPECT_QUOTING');
	numcodes_to_entities(\$text);
      }
    return($text);
  }

sub write_xdoc_text
  {
    my ($text, $data) = @_;

    while ( $text =~ m/([^\%]+)|\%\[([0-9]+)\]|\%\{([0-9]+)\}/gc )
      {
	if ( $1 )
	  {
	    write_xml_pcdata(process_xdoc_text_special_chars($1));
	  }
	elsif ( $2 )
	  {
	    write_empty_xml_element("opt-arg-ref", {"number" => $2}, "INLINE");
	  }
	elsif ( $3 )
	  {
	    write_empty_xml_element("man-arg-ref", {"number" => $3}, "INLINE");
	  }
      }
  }

sub get_doc_list_entry
  {
    my ($data) = @_;
    my $description;
    if ( $data->{doc} )
      {
	$description = process_doc_text($data->{doc}->{description}, $data);
	$description =~ m/^[^.]*/;
	$description = $&;
      }
    else
      {
	$description = '- not documented -';
      }
    return($description);
  }

sub dump_cmd_or_env_list
  {
    my ($type, $lib_names_ref, $dcl_name) = @_;

    my $ctrl_table = {'CMD' => 'cmd_table', 'ENV' => 'env_table'}->{$type};
    my $doc_table = {};
    my @lib_names;

    # Setup libraries:
    if ( $dcl_name )
      {
	load_document_class($dcl_name);
	@lib_names = keys(%{$lib_table})
      }
    else
      {
	@lib_names = ($lib_names_ref ? @{$lib_names_ref} : find_all_libs());
	foreach my $lib_name (@lib_names)
	  {
	    require_lib($lib_name);
	  }
      }

    # Compose the doc table:
    foreach my $lib_name (@lib_names)
      {
	foreach my $name (keys(%{$lib_table->{$lib_name}->{$ctrl_table}}))
	  {
	    my $data = $lib_table->{$lib_name}->{$ctrl_table}->{$name};
	    $doc_table->{$name} =
	      {
	       "lib" => $lib_name,
	       "description" => get_doc_list_entry($data),
	      };
	  }
      }

    # Output the doc table:
    foreach my $name (sort(keys(%{$doc_table})))
      {
	print(flush_string_right(($type eq 'CMD' ? '\\' : '') . $name, 20), ' ');
	print(flush_string_right($doc_table->{$name}->{lib}, 14), ' ');
	print($doc_table->{$name}->{description}, "\n");
      }
  }

sub get_cmd_or_env_doc
  #a ($type, $name, $data, $lib_name, $indent, $max_col)
  # Returns the full plain text documentation for a command or environment. $type specifies
  # the type of control structure: "CMD" means command, "ENV" environment. $name is the name
  # of the control structure (in case of a command, the leading backslash must be
  # stripped). $data must be a reference to the entry for $name in the command
  # resp. environment table. $lib_name must be the library that defines the command
  # resp. environment $name. $indent and $max_col are formatting parameters. $indent
  # specifies the left margin of the description (except the first line, which is not
  # indented), $max_col its width.
  {
    my ($type, $name, $data, $lib_name, $indent, $max_col) = @_;

    if ( $data->{doc} )
      {
	my $doc_str;

	my $append_opt_arg =
	  sub
	    {
	      my $arg_number = $_[0];
	      $doc_str .= '[' . uc($data->{doc}->{opt_args}->[$arg_number]->[0]) . ']';
	    };

	my $append_man_arg =
	  sub
	    {
	      my $arg_number = $_[0];
	      $doc_str .= '{' . uc($data->{doc}->{man_args}->[$arg_number]->[0]) . '}';
	    };

	my $append_opt_arg_description =
	  sub
            {
	      my $arg_number = $_[0];
	      my $arg_data = $data->{doc}->{opt_args}->[$arg_number];
	      $doc_str .= (' ' x $indent)
		       . flush_string_right(uc($arg_data->[0]), $doc_arg_field_size)
		       . ' '
		       . process_doc_text($arg_data->[1], $data)
		       . "\n";
	    };

	my $append_man_arg_description =
	  sub
            {
	      my $arg_number = $_[0];
	      my $arg_data = $data->{doc}->{man_args}->[$arg_number];
	      $doc_str .= (' ' x $indent)
		       . flush_string_right(uc($arg_data->[0]), $doc_arg_field_size)
		       . ' '
		       . process_doc_text($arg_data->[1], $data)
		       . "\n";
	    };

	my $man_arg_start = ($data->{pref_man_arg} ? 1 : 0);
	my $num_man_args =
	  ($data->{doc}->{man_args} ? scalar(@{$data->{doc}->{man_args}}) : 0);
	my $num_opt_args =
	  ($data->{doc}->{opt_args} ? scalar(@{$data->{doc}->{opt_args}}) : 0);

	if ( $type eq 'CMD' )
	  {
	    $doc_str = "\\$name";
	  }
	elsif ( $type eq 'ENV' )
	  {
	    $doc_str = "\\begin{$name}";
	  }
	if ( $data->{pref_man_arg} )
	  {
	    &{$append_man_arg}(0);
	  }
	for (my $i = 0; $i < $num_opt_args; $i++)
	  {
	    &{$append_opt_arg}($i);
	  }
	for (my $i = $man_arg_start; $i < $num_man_args; $i++)
	  {
	    &{$append_man_arg}($i);
	  }

	$doc_str .= "\n\n";

	my @description_words
	  = split(/\s+/, process_doc_text($data->{doc}->{description}, $data));
	@description_words
	  = @{format_words_as_block(\@description_words, $max_col, $indent)};
	$doc_str .= join('', @description_words) . "\n\n";

	if ( $data->{pref_man_arg} )
	  {
	    &{$append_man_arg_description}(0);
	    $doc_str .= "\n";
	  }

	if ( $num_opt_args > 0 )
	  {
	    for (my $i = 0; $i < $data->{num_opt_args}; $i++)
	      {
		&{$append_opt_arg_description}($i);
	      }
	    $doc_str .= "\n";
	  }

	if ( $num_man_args > $man_arg_start )
	  {
	    for (my $i = $man_arg_start; $i < $num_man_args; $i++)
	      {
		&{$append_man_arg_description}($i);
	      }
	    $doc_str .= "\n";
	  }

	my @see_list = ();
	my $see_cmds = $data->{doc}->{see}->{cmds};
	if ( $see_cmds )
	  {
	    foreach my $entry (@{$see_cmds})
	      {
		if ( ref($entry) )
		  {
		    my ($cmd, $lib) = @{$entry};
		    push(@see_list, "\\$cmd ($lib)");
		  }
		else
		  {
		    push(@see_list, "\\$entry");
		  }
	      }
	  }
	my $see_envs = $data->{doc}->{see}->{envs};
	if ( $see_envs )
	  {
	    foreach my $entry (@{$see_envs})
	      {
		if ( ref($entry) )
		  {
		    my ($env, $lib) = @{$entry};
		    push(@see_list, "$env ($lib)");
		  }
		else
		  {
		    push(@see_list, $entry);
		  }
	      }
	  }
	if ( @see_list )
	  {
	    @see_list = ("See also:", split(/\s+/, join(', ', @see_list)));
	    @see_list = @{format_words_as_block(\@see_list, $max_col, $indent)};
	    $doc_str .= join('', @see_list);
	    $doc_str .= "\n\n";
	  }

	$doc_str .= (' ' x $indent) . "Defined in: $lib_name" . "\n";

	return($doc_str);
      }
    else
      {
	return("- not documented -\n");
      }
  }

sub dump_all_libs_cmd_or_env_doc
  #a ($type, $name)

  # Prints the plain text documentation for a control structure (i.e., command or
  # environment). $type specifies the type of control structure:"CMD" means command, "ENV"
  # environment. $name is the name of the control structure (in case of a command, the
  # leading backslash must be stripped). Searches in all libraries returned by
  # `find_all_libs`. If more then one commands resp. environments with name $name is found,
  # the documentations of all of them are printed.
  {
    my ($type, $name) = @_;

    my $ctrl_table = {'CMD' => 'cmd_table', 'ENV' => 'env_table'}->{$type};
    my $found = FALSE;

    foreach my $lib_name (find_all_libs())
      {
        require_lib($lib_name);
	my $data = $lib_table->{$lib_name}->{$ctrl_table}->{$name};
	if ( $data )
	  {
	    $found = TRUE;
	    print("\n", get_cmd_or_env_doc($type, $name, $data, $lib_name,
					   $doc_indent, $doc_max_col), "\n");
	  }
      }

    unless ( $found )
      {
	print($type eq 'CMD'
	      ? "No such command: \\$name\n"
	      : "No such environment: $name\n");
      }
  }

sub dump_dcl_cmd_or_env_doc
  #a ($type, $name, $dcl_name)
  # Prints the plain text documentation for a control structure (i.e., command or
  # environment) in document class $dcl_name. $type specifies the type of control structure:
  # "CMD" means command, "ENV" environment. $name is the name of the control structure (in
  # case of a command, the leading backslash must be stripped). If more then one commands
  # resp. environments with name $name is found, the documentations of all of them are printed.
  {
    my ($type, $name, $dcl_name) = @_;

    load_document_class($dcl_name);

    my $ctrl_table = {'CMD' => 'cmd_table', 'ENV' => 'env_table'}->{$type};
    my $found = FALSE;

    foreach my $lib_name (keys(%{$lib_table}))
      {
	my $data = $lib_table->{$lib_name}->{$ctrl_table}->{$name};
	if ( $data )
	  {
	    $found = TRUE;
	    print("\n", get_cmd_or_env_doc('CMD', $name, $data, $lib_name,
					   $doc_indent, $doc_max_col), "\n");
	  }
      }

    unless ( $found )
      {
	print($type eq 'CMD'
	      ? "No such command in documentclass $dcl_name: \\$name\n"
	      : "No such environment in documentclass $dcl_name: $name\n");
      }
  }

sub dump_lib_cmd_or_env_doc
  #a ($type, $name, $lib_name)
  # Prints the plain text documentation for a control structure (i.e., command or
  # environment) defined in library $lib_name. $type specifies the type of control
  # structure: "CMD" means command, "ENV" environment. $name is the name of the control
  # structure (in case of a command, the leading backslash must be stripped).
  {
    my ($type, $name, $lib_name) = @_;

    require_lib($lib_name);

    my $ctrl_table = {'CMD' => 'cmd_table', 'ENV' => 'env_table'}->{$type};
    my $data = $lib_table->{$lib_name}->{$ctrl_table}->{$name};
    if ( $data )
      {
	print("\n", get_cmd_or_env_doc($type, $name, $data, $lib_name,
				       $doc_indent, $doc_max_col), "\n");
      }
    else
      {
	print($type eq 'CMD'
	      ? "No such command in library $lib_name: \\$name\n"
	      : "No such environment in library $lib_name: $name\n");
      }
  }

sub dump_lib_list
  #a ()
  # Prints a list of all libraries.
  {
    my @libs = find_all_libs();
    print(join("\n", sort(@libs)), "\n");
  }


sub write_cmd_or_env_xdoc
  #a ($type, $name, $data, $lib_name)
  # Creates the XML documentation for a command or environment. $type specifies the type of
  # control structure: "CMD" means command, "ENV" environment. $name is the name of the
  # control structure (in case of a command, the leading backslash must be stripped). $data
  # must be a reference to the entry for $name in the command resp. environment
  # table. $lib_name must be the library that defines the command resp. environment
  # $name. The XML created is send to the output list of the Mumie::XML::Writer.
  {
    my ($type, $name, $data, $lib_name) = @_;
    my $element = {"CMD" => "command", "ENV" => "environment"}->{$type};

    my $arg_doc_writer =
      sub
	{
	  my ($args, $num,) = @_;
	  my $name = "";
	  my $description = "";

	  if (defined $data->{doc}->{$args}->[$num]->[0])
	    {
	      $name = $data->{doc}->{$args}->[$num]->[0];
	    }
	  if (defined $data->{doc}->{$args}->[$num]->[1])
	    {
	      $description = $data->{doc}->{$args}->[$num]->[1];
	    }
	  write_xml_start_tag("arg", {"name" => $name}, "SEMI_DISPLAY");
	  write_xdoc_text($description, $data);
	  write_xml_end_tag("arg", "INLINE");
	};

    my $attribs = {"name" => process_xdoc_text_special_chars($name)};
    ($attribs->{lib} = $lib_name) if ( $lib_name );
    ($attribs->{'pref-man-arg'} = 'yes') if ( $data->{pref_man_arg} );

    if ( $data->{doc} )
      {
	write_xml_start_tag($element, $attribs, "DISPLAY");

	if ( $data->{num_opt_args} )
	  {
	    write_xml_start_tag("opt-args", {}, "DISPLAY");
	    for (my $i = 0; $i < $data->{num_opt_args}; $i++)
	      {
		&{$arg_doc_writer}("opt_args", $i);
	      }
	    write_xml_end_tag("opt-args", "DISPLAY");
	  }

	if ( $data->{num_man_args} )
	  {
	    write_xml_start_tag("man-args", {}, "DISPLAY");
	    for (my $i = 0; $i < $data->{num_man_args}; $i++)
	      {
		&{$arg_doc_writer}("man_args", $i);
	      }
	    write_xml_end_tag("man-args", "DISPLAY");
	  }

	write_xml_start_tag("description", {}, "SEMI_DISPLAY");
	write_xdoc_text($data->{doc}->{description}, $data);
	write_xml_end_tag("description", "INLINE");

	if ( $data->{doc}->{see} )
	  {
	    write_xml_start_tag("see", {}, "DISPLAY");
	    if ( my $see_cmds = $data->{doc}->{see}->{cmds} )
	      {
		foreach my $entry (@{$see_cmds})
		  {
		    my $attribs = {};
		    if ( ref($entry) )
		      {
			my ($cmd, $lib) = @{$entry};
			$attribs->{name} = $cmd;
			$attribs->{lib} = $lib;
		      }
		    else
		      {
			$attribs->{name} = $entry;
		      }
		    write_empty_xml_element("command-ref", $attribs, "DISPLAY");
		  }
	      }
	    if ( my $see_envs = $data->{doc}->{see}->{envs} )
	      {
		foreach my $entry (@{$see_envs})
		  {
		    my $attribs = {};
		    if ( ref($entry) )
		      {
			my ($env, $lib) = @{$entry};
			$attribs->{name} = $env;
			$attribs->{lib} = $lib;
		      }
		    else
		      {
			$attribs->{name} = $entry;
		      }
		    write_empty_xml_element("environment-ref", $attribs, "DISPLAY");
		  }
	      }
	    write_xml_end_tag("see", "DISPLAY");
	  }

	write_xml_end_tag($element, "DISPLAY");
      }
    else
      {
	write_empty_xml_element($element, $attribs, "DISPLAY");
      }
  }

sub write_dcl_xdoc
  #a ($dcl_name, $filename)
  # Writes the XML documentation for all command resp. environments in document class
  # $dcl_name to a file. $filename is the name of the file.
  {
    my ($dcl_name, $filename) = @_;

    load_document_class($dcl_name);

    reset_xml_writer();

    write_xml_decl();
    write_xml_pr_instr("xml-stylesheet",
		 ["type=\"text/xsl\"", "href=\"$xdoc_xsl_stylesheet\""],
		 "DISPLAY");

    write_xml_start_tag('documentclass',{"name" => $dcl_name}, 'DISPLAY');


    foreach my $lib_name (keys(%{$lib_table}))
      {
	write_xml_start_tag('library', {'name' => $lib_name}, 'DISPLAY');
	foreach $ctrl_table (('cmd_table', 'env_table'))
	  {
	    $type = {'cmd_table' => 'CMD' ,'env_table' =>'ENV'}->{$ctrl_table};
	    foreach my $name (keys(%{$lib_table->{$lib_name}->{$ctrl_table}}))
	      {
		my $data = $lib_table->{$lib_name}->{$ctrl_table}->{$name};
		write_cmd_or_env_xdoc($type, $name, $data);
	      }
	  }
	write_xml_end_tag('library', 'DISPLAY');
      }

    write_xml_end_tag('documentclass','DISPLAY');

    open(XDOC, ">$filename") || die("Can't open file for writing: $filename:\n$!\n");
    print(XDOC @xml_output_list);
    close(XDOC);
  }

sub write_lib_xdoc
  #a ($lib_name, $filename)
  # Writes the XML documentation for all command resp. environments defined in library
  # $lib_name to a file.  $filename is the name of the file.
  {
    my ($lib_name, $filename) = @_;

    require_lib($lib_name);

    reset_xml_writer();

    write_xml_decl();
    if ( $xdoc_xsl_stylesheet )
      {
        write_xml_pr_instr("xml-stylesheet",
                           ["type=\"text/xsl\"", "href=\"$xdoc_xsl_stylesheet\""],
                           "DISPLAY");
      }

    write_xml_start_tag('library',{"name" => $lib_name}, 'DISPLAY');

    foreach $ctrl_table (('cmd_table', 'env_table'))
      {
	$type = {'cmd_table' => 'CMD' ,'env_table' =>'ENV'}->{$ctrl_table};
	foreach my $name (keys(%{$lib_table->{$lib_name}->{$ctrl_table}}))
	  {
	    my $data = $lib_table->{$lib_name}->{$ctrl_table}->{$name};
	    write_cmd_or_env_xdoc($type, $name, $data);
	  }
      }
    write_xml_end_tag('library','DISPLAY');

    open(XDOC, ">$filename") || die("Can't open file for writing: $filename:\n$!\n");
    print(XDOC @xml_output_list);
    close(XDOC);
  }


sub write_lib_list_xdoc
  #a ($dcl_name, $filename)
  # Writes an XML description of a list of the libraries loaded by document class $dcl_name
  # to the file $filename.
  {
    my ($dcl_name, $filename) = @_;

    load_document_class($dcl_name);

    reset_xml_writer();

    write_xml_decl();
    write_xml_pr_instr("xml-stylesheet",
		 ["type=\"text/xsl\"", "href=\"$xdoc_xsl_stylesheet\""],
		 "DISPLAY");
    write_xml_start_tag('libraries', {'documentclass' => $dcl_name}, 'DISPLAY');

    foreach my $lib_name (keys(%{$lib_table}))
      {
	write_empty_xml_element('lib', {'name' => $lib_name}, 'DISPLAY');
      }

    write_xml_end_tag('libraries', 'DISPLAY');

    open(XDOC, ">$filename") || die("Can't open file for writing: $filename:\n$!\n");
    print(XDOC @xml_output_list);
    close(XDOC);
  }

init();
return(1);
