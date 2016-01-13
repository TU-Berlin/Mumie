package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: simple_markup.mtx.pl,v 1.26 2007/07/11 15:56:14 grudzin Exp $

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
# Defines the markup commands '\emph', '\code', '\var', '\file', and '\keyb'.


log_message("\nLibrary \"simple_markup\" ", '$Revision: 1.26 $ ', "\n");


# --------------------------------------------------------------------------------
# Execute function for basic markup commands
# --------------------------------------------------------------------------------

sub execute_basic_markup_cmd
  {
    log_message("\nexecute_basic_markup_cmd\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args,
	$cmd_name, $xml_element_name) = @_;

    # Get the class:
    my $class = undef();
    if ( $opt_args->[0] )
      {
	$class = get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD', $cmd_name, 'class_arg');

        # Check the class if necessary:
        my $allowed_classes = $scan_proc->{cmd_table}->{$cmd_name}->{classes};
        if ( $allowed_classes && !grep($class eq $_, @{$allowed_classes}) )
          {
            &{$scan_proc->{error_handler}}("Invalid $cmd_name class: $class");
          }
      }

    # Set attributes:
    my $attribs = {};
    if ( $class  )
      {
	$attribs->{class} = $class;
      }

    # Start XML element:
    start_xml_element($xml_element_name, $attribs, 'INLINE');

    # Convert argumnent:
    convert_arg
      (0, $man_args, $pos_man_args, 'CMD', $cmd_name, 'arg', FALSE, FALSE, 'SHARED_OUTPUT_LIST');

    # Close XML element:
    close_xml_element($xml_element_name, 'INLINE');
  }


# --------------------------------------------------------------------------------
# The "\mark" command (see math.mtx.pl, too)
# --------------------------------------------------------------------------------


sub execute_mark_cmd
  {
    log_message("\nexecute_mark\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    # Getting the class
    my $class = 'Mark-0-Style';
    if ( $opt_args->[0] )
      {
	$class = 'Mark-' . get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD', 'mark', 'class_arg') . '-Style';
      }

    # Setting up attributes
    my $attribs = {};
    $attribs->{class} = $class;

    # Opening XML element
    start_xml_element('mark', $attribs, 'INLINE');

    # Converting argumnent
    convert_arg
      (0, $man_args, $pos_man_args, 'CMD', 'mark', 'arg', FALSE, FALSE, 'SHARED_OUTPUT_LIST');

    # Closing XML element
    close_xml_element('mark', 'INLINE');
  }

# --------------------------------------------------------------------------------
# Line breaks ("\\" command)
# --------------------------------------------------------------------------------

sub execute_line_break
  {
    log_message("\nexecute_line_break\n");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $attribs = {};
    if ( $opt_args->[0] )
      {
        $attribs->{space} =
          get_xml_length_spec(get_length_from_arg(0, $opt_args, $pos_opt_args, 'CMD'));
      }
    empty_xml_element('break', $attribs, 'DISPLAY');
  }

# --------------------------------------------------------------------------------
# Constructs to express alternatives
# --------------------------------------------------------------------------------

sub start_alt
  #a ()
  # Starts an alternative. Default handler of the `begin_alt` token type.
  {
    log_message("\nstart_alt\n");
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{allowed_tokens} = $scan_proc->{previous_tokens};
    own_allowed_tokens();
    disallow_tokens(['begin_alt']);
    allow_tokens(['begin_block', 'end_alt'], 'MODIFY', 'PREPEND');
    $scan_proc->{current_env} = '_alternative';
    start_xml_element('alt', {}, 'INLINE');
  }

sub close_alt
  #a ()
  # Closes an alternative. Default handler of the `end_alt` token type.
  {
    log_message("\nclose_alt\n");
    close_xml_element('alt', 'INLINE');
    reset_scan_proc();
  }

sub execute_alt
  #a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
  # Execute function of the '\alt' command.
  {
    log_message("\nexecute_alt\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    start_xml_element('alts', {}, 'INLINE');

    new_scan_proc('EXCURSION', \$man_args->[0], $pos_man_args->[0], 'SHARED_NAMESPACE',
		  'SHARED_OUTPUT_LIST');
    $scan_proc->{previous_tokens} = $scan_proc->{allowed_tokens};
    $scan_proc->{allowed_tokens} = ['begin_alt', 'ign_whitesp'];
    scan();
    reset_scan_proc();

    close_xml_element('alts', 'INLINE');
  }

# --------------------------------------------------------------------------------
# Generic attributes
# --------------------------------------------------------------------------------

sub execute_generic_attrib_cmd
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args,
	$cmd_name, $attrib_name, $notation,) = @_;

    # Setting $target, the target specifier
    my $target;
    if ( $scan_proc->{generic_attrib_target}->{$attrib_name} )
      {
	$target = $scan_proc->{generic_attrib_target}->{$attrib_name};
      }
    elsif ( $scan_proc->{parsing_data} )
      {
	$target = 'XML_AUTO_ATTRIBS';
      }
    else
      {
	$target = 'CURRENT_XML_ELEMENT';
      }

    log_message("\nexecute_generic_attrib_cmd 1/3\n");
    log_data('Target', $target);

    # Getting attribute value
    my $attrib_value =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", $cmd_name, "arg");

    log_message("\nexecute_generic_attrib_cmd 2/3\n");
    log_data('Value', $attrib_value);

    # Writing the attribute value to the target
    if ( $target eq 'CURRENT_XML_ELEMENT' )
      {
	my $current_xml_element_start = get_last_xml_element_start();
	if ( ! $current_xml_element_start )
	  {
	    &{$scan_proc->{error_handler}}('Nothing to apply $notation to');
	  }
	my $current_xml_element_name = $current_xml_element_start->[1];
	my $current_xml_element_attribs = $current_xml_element_start->[2];

	log_message("\nexecute_generic_attrib_cmd 2/3\n");
	log_data('Element', $current_xml_element_name);

	my $existing_value = $current_xml_element_attribs->{$attrib_name};
	if ( $existing_value )
	  {
	    &{$scan_proc->{error_handler}}
	      (ucfirst("$notation already set (\"$existing_value\")"));
	  }
	$current_xml_element_attribs->{$attrib_name} = $attrib_value;
      }
    elsif ( $target eq 'XML_AUTO_ATTRIBS' )
      {
	my $existing_value = $scan_proc->{xml_auto_attribs}->{$attrib_name};
	if ( $existing_value )
	  {
	    &{$scan_proc->{error_handler}}
	      (ucfirst("$notation already set"
		       . ( ! ref($existing_value) ? " (\"$existing_value\")" : '' )));
	  }
	$scan_proc->{xml_auto_attribs}->{$attrib_name} = $attrib_value;
      }

    log_message("\nexecute_generic_attrib_cmd 3/3\n");
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{simple_markup}->{initializer} = sub
  {
    require_lib('length');

    # Extra tokens
    $default_token_table->{begin_alt}
      = {
         'tester' => sub { test_regexp('(?<!\\\\){') },
         'handler' => \&start_alt,
        };
    $default_token_table->{end_alt}
      = {
         'tester' => sub { test_regexp('(?<!\\\\)}') },
         'handler' => \&close_alt,
        };

    # Command table for export
    my $simple_markup_cmd_table =
      {
       'mark' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => \&execute_mark_cmd,
          'doc' =>
            {
             'man_args' => [['text', 'Text to emphasize']],
             'opt_args' => [['mark style', 'Number from 0 - 9']],
             'description' => 'Emphasizes a piece of text. Also possible in math mode, ' .
             'see math library for more information.',
            }
         },
       'emph' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'emph', 'emph') },
          'doc' =>
            {
             'man_args' => [['text', 'Text to emphasize']],
             'opt_args' => [['class', 'Class']],
             'description' => 'Emphasizes a piece of text.',
            }
         },
       'code' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'code', 'code'); },
          'doc' =>
            {
             'man_args' => [['text', 'Text to identify as code']],
             'opt_args' => [['class', 'Class']],
             'description' => 'Identifies a piece of text as (computer) code.',
            }
         },
       'var' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'var', 'var'); },
          'doc' =>
            {
             'man_args' => [['text', 'Text to identify as a variable']],
             'opt_args' => [['class', 'Class']],
             'description' => 'Identifies a piece of text as a variable.',
            }
         },
       'notion' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'notion', 'notion'); },
          'doc' =>
            {
             'man_args' => [['text', 'Text to identify as a notion']],
             'opt_args' => [['class', 'Class']],
             'description' => 'Identifies a piece of text as a technical term.',
            }
         },
       'file' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'file', 'file'); },
          'doc' =>
          {
           'man_args' => [['text', 'Text to identify as a filename']],
           'opt_args' => [['class', 'Class']],
           'description' => 'Identifies a piece of text as a filename.',
          }
         },
       'keyb' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'keyb', 'keyb'); },
          'doc' =>
            {
             'man_args' => [['text', 'Text to identify as keyboard input']],
             'opt_args' => [['class', 'Class']],
             'description' => 'Identifies a piece of text as keyboard input.',
            }
         },
       'plhld' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'plhld', 'plhld'); },
          'doc' =>
          {
           'man_args' => [['text', 'Text to identify as a placeholder']],
           'opt_args' => [['class', 'Class']],
           'description' => 'Identifies a piece of text as a placeholder.',
          }
         },
       'quoted' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'quoted', 'quoted'); },
          'doc' =>
          {
           'man_args' => [['text', 'Text to set in quotes']],
           'opt_args' => [['class', 'Class']],
           'description' => 'Sets a piece of text in quotes.',
          }
         },
       'optional' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'optional', 'optional'); },
          'doc' =>
          {
           'man_args' => [['text', 'Expression that is optional']],
           'description' => 'Identifies %{1} as an optional expression. ' .
                            'Usually, will be rendered as [%{1}].',
          }
         },
       'meta' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'meta', 'meta'); },
          'doc' =>
          {
           'man_args' => [['text', 'Meta information']],
           'description' => 'Identifies %{1} as a meta information, i.e., a piece of ' .
    	                    'information concerning the surrounding text.',
          }
         },
       'span' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => sub { execute_basic_markup_cmd(@_, 'span', 'span'); },
          'doc' =>
          {
           'man_args' => [['text', 'Text to apply [0]] to']],
           'opt_args' => [['class', 'Class']],
           'description' => 'Generic command to apply a class to a piece of text.',
          }
         },
       'alt' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'is_par_starter' => TRUE,
          'execute_function' => \&execute_alt,
          'doc' =>
          {
           'man_args' => [['alts', 'List of alternatives']],
           'description' =>
    	     'Identifies %{1} as a list of alternatives. %{1} must be composed of {}-blocks, ' .
    	     'each of which specifying one alternative, e.g, {one}{two}{three}. ' .
    	     'Will be renderd as something like (one|two|three).'
          }
         },
       'id' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'is_par_starter' => FALSE,
          'execute_function' => sub { execute_generic_attrib_cmd(@_, 'id', 'id', 'id'); },
          'doc' =>
          {
           'man_args' => [['id', 'The id']],
           'description' => 'Assignes an id to the current structure.',
          }
         },
       "balloon" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" =>
              sub { execute_generic_attrib_cmd(@_, 'balloon', 'balloon', 'balloon help'); },
            "doc" =>
            {
             "man_args" => [['text','Content of the balloon help']],
             "description" => 'Creates a ballon help.'
            }
           },
       '\\' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => \&execute_line_break,
          'doc' =>
          {
           'opt_args' => [['space', 'Extra space to add after the line break']],
           'description' => 'Causes a line break.',
          }
         },
       'newline' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'is_par_starter' => FALSE,
          'execute_function' => \&execute_line_break,
          'doc' =>
          {
           'description' => 'Causes a line break.',
          }
         },
        };

    my @simple_markup_cmds = keys(%{$simple_markup_cmd_table});
    deploy_lib
      ('simple_markup',
       $simple_markup_cmd_table,
       {},
       {
        'TOPLEVEL' => \@simple_markup_cmds,
        'NOT_IN_DATA' => \@simple_markup_cmds,
       },
       FALSE);
  };

return(TRUE);
