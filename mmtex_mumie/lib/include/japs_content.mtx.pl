package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: japs_content.mtx.pl,v 1.23 2008/06/06 15:32:07 rassy Exp $

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

log_message("\nLibrary \"japs_content\" ", '$Revision: 1.23 $ ', "\n");

# ------------------------------------------------------------------------------------------
#1 Description
# ------------------------------------------------------------------------------------------
#
# Code for creating the content of elements and subelements. Provides the following
# features:
#L
# - Content environment: Implementation of the top-level 'content' environment
# - Content XML elements: Functions to create XML elements in the content namespace
# - Implementation of content commands and environments
#/L

# --------------------------------------------------------------------------------
# Utilities
# --------------------------------------------------------------------------------

sub japs_enable_output
  {
    log_message("\njaps_enable_output 1/1\n");

    # Unlock output list:
    unlock_output_list() unless ( $global_data->{free} );

    # Set token types:
    $scan_proc->{allowed_tokens} = \@japs_token_types;

    # Enable paragraphs:
    $scan_proc->{par_enabled} = TRUE;
  }

# --------------------------------------------------------------------------------
# The "content" environment
# --------------------------------------------------------------------------------

sub japs_begin_content
  {
    log_message("\njaps_begin_content 1/2\n");

    # Check if "metainfo" environment occurred before:
    if ( japs_get_occurrence('metainfo') == 0 )
      {
        &{$scan_proc->{error_handler}}
          ("Environment \"metainfo\" must occur before \"content\"");
      }

    # Specific setup:
    &{$global_data->{setup}}();

    unlock_output_list();

    my $type = $global_data->{document_type_name};

    # Output XML declaration:
    xml_decl('1.0', 'ASCII', undef(), 'DISPLAY');

    # Setup "content" element attributes:
    my $attribs = {};
    # - Content namespace:
    $attribs->{'xmlns:' . $global_data->{content_xml_namespace_prefix}} =
      $global_data->{content_xml_namespace};
    # - Mmtex namespace:
    $attribs->{'xmlns:' . $params{mtx_xml_namespace_prefix}} =
      $params{mtx_xml_namespace};
    if ( $type eq 'problem' )
      {
        # - Datasheet extract namespace:
        $attribs->{'xmlns:' . $params{dsx_xml_namespace_prefix}} =
          $params{dsx_xml_namespace};
        # - Personalized problem data namespace:
        $attribs->{'xmlns:' . $params{ppd_xml_namespace_prefix}} =
          $params{ppd_xml_namespace};
      }
    # - Category:
    $attribs->{category} = $global_data->{category} if ( $global_data->{has_category} );
    # - Arrangement option:
    $attribs->{arrange} = $global_data->{arrange} if ( $global_data->{arrange} );

    # Root element name:
    my $root_element = $global_data->{document_type_name};

    # Start "content" element:
    japs_start_content_xml_element($root_element, $attribs, 'DISPLAY');

    # Set mmtex namespace as default:
    new_xml_namespace
      ($params{mtx_xml_namespace}, $params{mtx_xml_namespace_prefix}, TRUE);

    $scan_proc->{mode} = "NORMAL";

    if ( $global_data->{free} )
      {
        japs_enable_output();
      }
    else
      {
        lock_output_list();
        $scan_proc->{allowed_tokens} = \@japs_toplevel_token_types;
        $scan_proc->{scan_failed_handler} = sub
          {
            &{$scan_proc->{error_handler}}
              ("Invalid input. Most likely, you placed text directly in the content\n" .
               "environment. This is not allowed unless you set the \"free\" option.");
          };
      }

    # Setup math:
    $scan_proc->{start_inline_math_end_hook} = sub
      {
        install_cmds("ALL", "math");
        install_envs("ALL", "math");
      };

    # Provide the mmtex commands/environments:
    japs_install_text_cmds_and_envs();

    log_message("\njaps_begin_content 2/2\n");
  }

sub japs_end_content
  {
    log_message("\njaps_end_content 1/2\n");
    &{$global_data->{cleanup}}();
    unlock_output_list();
    reset_xml_namespace();
    my $root_element = $global_data->{document_type_name};
    japs_close_content_xml_element($root_element, 'DISPLAY');
    $global_data->{content_output_list} = $scan_proc->{output_list};
    lock_output_list();
    log_message("\njaps_end_content 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Creating content XML elements
# ------------------------------------------------------------------------------------------

sub japs_start_content_xml_element
  {
    my ($name, $attribs, $layout) = @_;
    $name = $global_data->{content_xml_namespace_prefix} . ':' . $name;
    start_xml_element($name, $attribs, $layout);
  }

sub japs_close_content_xml_element
  {
    my ($name, $layout) = @_;
    $name = $global_data->{content_xml_namespace_prefix} . ':' . $name;
    close_xml_element($name, $layout);
  }

sub japs_empty_content_xml_element
  {
    my ($name, $attribs, $layout) = @_;
    $name = $global_data->{content_xml_namespace_prefix} . ':' . $name;
    empty_xml_element($name, $attribs, $layout);
  }

# --------------------------------------------------------------------------------
# The "\title" command
# --------------------------------------------------------------------------------

sub japs_execute_title
  {
    log_message("\njaps_execute_title 1/2\n");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    japs_check_occurrence('title', '\title', 1, 1, 'FORWARD');
    unlock_output_list() unless ( $global_data->{free} );
    japs_start_content_xml_element('title', {}, 'SEMI_DISPLAY');
    convert_arg
      (0,
       $man_args,
       $pos_man_args,
       'CMD',
       sub { $scan_proc->{allowed_tokens} = \@japs_token_types;
             $scan_proc->{par_enabled} = FALSE },
       sub {},
       FALSE,
       FALSE,
       'SHARED_OUTPUT_LIST');
    japs_close_content_xml_element('title', 'INLINE');
    japs_register_occurrence('title');
    lock_output_list() unless ( $global_data->{free} );
    log_message("\njaps_execute_title 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\defnotion" command
# --------------------------------------------------------------------------------

sub japs_start_defnotions
  {
    $scan_proc->{token_table}->{notion_sep} =
      {
       'tester' => sub { test_regexp('\\s*,\\s*') },
       'handler' => sub
         {
	   japs_close_content_xml_element('defnotion', 'INLINE');
	   japs_start_content_xml_element('defnotion', {}, 'SEMI_DISPLAY');
	 },
      };
    my $notion_plain_text_chars = $nonnewline_nonwhitesp_chars;
    $notion_plain_text_chars =~ s/,//g;
    my $notion_plain_text_regexp = plain_text_regexp($notion_plain_text_chars);
    $scan_proc->{token_table}->{notion_plain_text} =
      {
       'tester' => sub { test_regexp($notion_plain_text_regexp) },
       'handler' => $scan_proc->{token_table}->{plain_text}->{handler},
      };
    $scan_proc->{allowed_tokens} =
      [
       'notion_sep',
       'notion_plain_text',
       'inline_math_boundry',
       @basic_token_types
      ];
    $scan_proc->{par_enabled} = FALSE;
    japs_start_content_xml_element('defnotions', {}, 'DISPLAY');
    japs_start_content_xml_element('defnotion', {}, 'SEMI_DISPLAY');
  }

sub japs_close_defnotions
  {
    japs_close_content_xml_element('defnotion', 'INLINE');
    japs_close_content_xml_element('defnotions', 'DISPLAY');
  }

sub japs_execute_defnotion
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_defnotion\n");
    japs_check_occurrence('defnotion', '\defnotion', 1, 1, 'FORWARD');
    unlock_output_list() unless ( $global_data->{free} );
    convert_arg
      (0, $man_args, $pos_man_args, "CMD",
       \&japs_start_defnotions, \&japs_close_defnotions,
       'NORMAL',
       'SHARED_NAMESPACE',
       'SHARED_OUTPUT_LIST');
    lock_output_list() unless ( $global_data->{free} );
    japs_register_occurrence('defnotion');
  }

# ------------------------------------------------------------------------------------------
# The "suppositions" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_suppositions
  {
    log_message("\njaps_begin_suppositions\n");

    # Check if "suppositions" is allowed here:
    japs_check_occurrence('suppositions', 'suppositions', 0, 1, 'FORWARD');

    # Enable output:
    japs_enable_output();

    # Start "suppositions"  element:
    japs_start_content_xml_element("suppositions", {}, "DISPLAY");

    # Disable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');
  }

sub japs_end_suppositions
  {
    log_message("\njaps_end_suppositions\n");

    # Close "suppositions"  element:
    japs_close_content_xml_element("suppositions", "DISPLAY");

    # Register this "suppositions" occurrence:
    japs_register_occurrence('suppositions');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );
  }

# ------------------------------------------------------------------------------------------
# The "background" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_background
  {
    log_message("\njaps_begin_background\n");

    # Check if "background" is allowed here:
    japs_check_occurrence('background', 'background', 0, 1, 'FORWARD');

    # Enable output:
    japs_enable_output();

    # Start "background"  element:
    japs_start_content_xml_element("background", {}, "DISPLAY");

    # Diable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');
  }

sub japs_end_background
  {
    log_message("\njaps_end_background\n");

    # Close "background"  element:
    japs_close_content_xml_element("background", "DISPLAY");

    # Register this "background" occurrence:
    japs_register_occurrence('background');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );
  }

# ------------------------------------------------------------------------------------------
# The "input" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_input
  {
    log_message("\njaps_begin_input\n");

    # Check if "input" is allowed here:
    japs_check_occurrence('input', 'input', 0, 1, 'FORWARD');

    # Enable output:
    japs_enable_output();

    # Start "input"  element:
    japs_start_content_xml_element("input", {}, "DISPLAY");

    # Diable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');
  }

sub japs_end_input
  {
    log_message("\njaps_end_input\n");

    # Close "input"  element:
    japs_close_content_xml_element("input", "DISPLAY");

    # Register this "input" occurrence:
    japs_register_occurrence('input');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );
  }

# ------------------------------------------------------------------------------------------
# The "output" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_output
  {
    log_message("\njaps_begin_output\n");

    # Check if "output" is allowed here:
    japs_check_occurrence('output', 'output', 0, 1, 'FORWARD');

    # Enable output:
    japs_enable_output();

    # Start "output"  element:
    japs_start_content_xml_element("output", {}, "DISPLAY");

    # Diable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');
  }

sub japs_end_output
  {
    log_message("\njaps_end_output\n");

    # Close "output"  element:
    japs_close_content_xml_element("output", "DISPLAY");

    # Register this "output" occurrence:
    japs_register_occurrence('output');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );
  }

# ------------------------------------------------------------------------------------------
# The "remarks" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_remark
  {
    log_message("\njaps_begin_remark\n");

    # Check if "remark" is allowed here:
    japs_check_occurrence('remark', 'remark', 0, 1, 'FORWARD');

    # Enable output:
    japs_enable_output();

    # Start "remark"  element:
    japs_start_content_xml_element("remark", {}, "DISPLAY");

    # Diable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');
  }

sub japs_end_remark
  {
    log_message("\njaps_end_remark\n");

    # Close "remark"  element:
    japs_close_content_xml_element("remark", "DISPLAY");

    # Register this "remark" occurrence:
    japs_register_occurrence('remark');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );
  }

sub japs_begin_remarks
  {
    log_message("\njaps_begin_remarks\n");

    # Check if "remarks" is allowed here:
    japs_check_occurrence('remarks', 'remarks', 0, 1, 'FORWARD');

    # Enable output:
    japs_enable_output();

    # Start "remarks"  element:
    japs_start_content_xml_element("remarks", {}, "DISPLAY");

    # Diable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');
  }

sub japs_end_remarks
  {
    log_message("\njaps_end_remarks\n");

    # Close "remarks"  element:
    japs_close_content_xml_element("remarks", "DISPLAY");

    # Register this "remarks" occurrence:
    japs_register_occurrence('remarks');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );
  }

# ------------------------------------------------------------------------------------------
# The "statement" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_statement
  {
    log_message("\njaps_begin_statement 1/2\n");

    japs_check_occurrence('statement', 'statement', 1, 1, 'FORWARD');

    # Enable output:
    japs_enable_output();

    # Create "statement" start tag:
    japs_start_content_xml_element("statement", {}, "DISPLAY");

    # Diable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');

    log_message("\njaps_begin_statement 2/2\n");
  }

sub japs_end_statement
  {
    log_message("\njaps_end_statement 1/2\n");

    # Create "statement" end tag:
    japs_close_content_xml_element("statement", "DISPLAY");

    # Register this "statement" occurrence:
    japs_register_occurrence('statement');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );

    log_message("\njaps_end_statement 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "_statement" pseudo-environment
# ------------------------------------------------------------------------------------------

sub japs_start_statement
  {
    log_message("\njaps_start_statement 1/2\n");

    # Start and set-up new scan process:
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = '_statement';

    # Set token types:
    $scan_proc->{allowed_tokens} = \@japs_token_types;

    # Enable paragraphs:
    $scan_proc->{par_enabled} = TRUE;

    # Create "statement" start tag:
    japs_start_content_xml_element("statement", {}, "DISPLAY");

    # Disable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');

    log_message("\njaps_start_statement 2/2\n");
  }

sub japs_close_statement
  {
    log_message("\njaps_close_statement 1/2\n");

    # Close paragraph if any:
    close_par_if_in_par();

    # Check nesting:
    if ( $scan_proc->{current_env} ne '_statement' )
      {
        &{$scan_proc->{error_handler}}
          ('Improper nesting: ', appr_env_notation($scan_proc->{current_env}),
           ' must be closed before statement can be closed');
      }

    # Reset scan process:
    reset_scan_proc();

    # Create "statement" end tag:
    japs_close_content_xml_element("statement", "DISPLAY");

    log_message("\njaps_close_statement 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "_proposition" pseudo-environment
# ------------------------------------------------------------------------------------------

sub japs_start_proposition
  {
    log_message("\njaps_start_proposition 1/2\n");

    # Start and set-up new scan process:
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = '_proposition';

    # Set token types:
    $scan_proc->{allowed_tokens} = \@japs_token_types;

    # Enable paragraphs:
    $scan_proc->{par_enabled} = TRUE;

    # Create "proposition" start tag:
    japs_start_content_xml_element("proposition", {}, "DISPLAY");

    # Disable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');

    log_message("\njaps_start_proposition 2/2\n");
  }

sub japs_close_proposition
  {
    log_message("\njaps_close_proposition 1/2\n");

    # Close paragraph if any:
    close_par_if_in_par();

    # Check nesting:
    if ( $scan_proc->{current_env} ne '_proposition' )
      {
        &{$scan_proc->{error_handler}}
          ('Improper nesting: ', appr_env_notation($scan_proc->{current_env}),
           ' must be closed before proposition can be closed');
      }

    # Reset scan process:
    reset_scan_proc();

    # Create "proposition" end tag:
    japs_close_content_xml_element("proposition", "DISPLAY");

    log_message("\njaps_close_proposition 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "_algstep" pseudo-environment
# ------------------------------------------------------------------------------------------

sub japs_start_algstep
  {
    log_message("\njaps_start_algstep 1/2\n");

    # Start and set-up new scan process:
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = '_algstep';

    # Set token types:
    $scan_proc->{allowed_tokens} = \@japs_token_types;

    # Enable paragraphs:
    $scan_proc->{par_enabled} = TRUE;

    # Create "algstep" start tag:
    japs_start_content_xml_element("algstep", {}, "DISPLAY");

    # Disable toplevel mumie cmds/envs:
    disable_cmds('JAPS_CONTENT_TOPLEVEL', 'japs_content');
    disable_envs('JAPS_CONTENT_TOPLEVEL', 'japs_content');

    log_message("\njaps_start_algstep 2/2\n");
  }

sub japs_close_algstep
  {
    log_message("\njaps_close_algstep 1/2\n");

    # Close paragraph if any:
    close_par_if_in_par();

    # Check nesting:
    if ( $scan_proc->{current_env} ne '_algstep' )
      {
        &{$scan_proc->{error_handler}}
          ('Improper nesting: ', appr_env_notation($scan_proc->{current_env}),
           ' must be closed before algstep can be closed');
      }

    # Reset scan process:
    reset_scan_proc();

    # Create "algstep" end tag:
    japs_close_content_xml_element("algstep", "DISPLAY");

    log_message("\njaps_close_algstep 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "defequivalence" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_defequivalence
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_defequivalence 1/3\n");

    # Enable output:
    unlock_output_list() unless ( $global_data->{free} );

    # Get the "arrange" attribute:
    my $arrange =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, "ENV", "defequivalence", "arrange_arg")
       : 'side-by-side');
    if ( ! grep($_ eq $arrange, @{$scan_proc->{env_table}->{defequivalence}->{arrange}}) )
      {
        &{$scan_proc->{error_handler}}("Invalid arrange value: $arrange");
      }
    log_message("\njaps_begin_defequivalence 2/3\n");
    log_data('Arrange', $arrange);

    # Start "defequivalence"  element:
    japs_start_content_xml_element("defequivalence", {'arrange' => $arrange}, "DISPLAY");

    # Start statement:
    japs_start_statement();

    # Enable "\isdefinedas" command:
    install_cmds(['isdefinedas'], 'japs_content');

    log_message("\njaps_begin_defequivalence 3/3\n");
  }

sub japs_execute_isdefinedas
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_isdefinedas\n");

    # Check if already called:
    if ( $scan_proc->{isdefinedas_called} )
      {
        &{$scan_proc->{error_handler}}
          ('\\isdefinedas called twice in same "defequivalence" environment');
      }

    # Close current statement:
    japs_close_statement();

    # Mark that \isdefinedas has been called:
    $scan_proc->{isdefinedas_called} = TRUE;

    # Start new statement:
    japs_start_statement();
  }

sub japs_end_defequivalence
  {
    log_message("\njaps_end_defequivalence 1/2\n");

    # Check if \isdefinedas was called:
    if ( ! $scan_proc->{isdefinedas_called} )
      {
        &{$scan_proc->{error_handler}}
          ('Missing \\isdefinedas in "defequivalence" environment');
      }

    # Close "defequivalence"  element:
    japs_close_content_xml_element("defequivalence", "DISPLAY");

    # Register this "defequivalence" occurrence:
    japs_register_occurrence('defequivalence');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );

    log_message("\njaps_end_defequivalence 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "implication" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_implication
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_implication 1/3\n");

    # Enable output:
    unlock_output_list() unless ( $global_data->{free} );

    # Get the "arrange" attribute:
    my $arrange =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, "ENV", "implication", "arrange_arg")
       : 'side-by-side');
    if ( ! grep($_ eq $arrange, @{$scan_proc->{env_table}->{implication}->{arrange}}) )
      {
        &{$scan_proc->{error_handler}}("Invalid arrange value: $arrange");
      }
    log_message("\njaps_begin_implication 2/3\n");
    log_data('Arrange', $arrange);

    # Start "implication"  element:
    japs_start_content_xml_element("implication", {'arrange' => $arrange}, "DISPLAY");

    # Start statement:
    japs_start_statement();

    # Enable "\implies" command:
    install_cmds(['implies'], 'japs_content');

    log_message("\njaps_begin_implication 3/3\n");
  }

sub japs_execute_implies
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_implies\n");

    # Check if already called:
    if ( $scan_proc->{implies_called} )
      {
        &{$scan_proc->{error_handler}}
          ('\\implies called twice in same "defequivalence" environment');
      }

    # Close current statement:
    japs_close_statement();

    # Mark that \implies has been called:
    $scan_proc->{implies_called} = TRUE;

    # Start new statement:
    japs_start_statement();
  }

sub japs_end_implication
  {
    log_message("\njaps_end_implication 1/2\n");

    # Check if \isdefinedas was called:
    if ( ! $scan_proc->{implies_called} )
      {
        &{$scan_proc->{error_handler}}
          ('Missing \\implies in "implication" environment');
      }

    # Close "implication"  element:
    japs_close_content_xml_element("implication", "DISPLAY");

    # Register this "implication" occurrence:
    japs_register_occurrence('implication');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );

    log_message("\njaps_end_implication 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "equivalence" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_equivalence
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_equivalence 1/3\n");

    # Enable output:
    unlock_output_list() unless ( $global_data->{free} );

    # Get the "arrange" attribute:
    my $arrange =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, "ENV", "equivalence", "arrange_arg")
       : 'side-by-side');
    if ( ! grep($_ eq $arrange, @{$scan_proc->{env_table}->{equivalence}->{arrange}}) )
      {
        &{$scan_proc->{error_handler}}("Invalid arrange value: $arrange");
      }
    log_message("\njaps_begin_equivalence 2/3\n");
    log_data('Arrange', $arrange);

    # Start "equivalence"  element:
    japs_start_content_xml_element("equivalence", {'arrange' => $arrange}, "DISPLAY");

    # Start statement:
    japs_start_statement();

    # Enable "\isequivalentto" command:
    install_cmds(['isequivalentto'], 'japs_content');

    log_message("\njaps_begin_equivalence 3/3\n");
  }

sub japs_execute_isequivalentto
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_isequivalentto\n");

    # Check if already called:
    if ( $scan_proc->{isequivalentto_called} )
      {
        &{$scan_proc->{error_handler}}
          ('\\isequivalentto called twice in same "defequivalence" environment');
      }

    # Close current statement:
    japs_close_statement();

    # Mark that \isequivalentto has been called:
    $scan_proc->{isequivalentto_called} = TRUE;

    # Start new statement:
    japs_start_statement();
  }

sub japs_end_equivalence
  {
    log_message("\njaps_end_equivalence 1/2\n");

    # Check if \isequivalentto was called:
    if ( ! $scan_proc->{isequivalentto_called} )
      {
        &{$scan_proc->{error_handler}}
          ('Missing \\isequivalentto in "equivalence" environment');
      }

    # Close "equivalence"  element:
    japs_close_content_xml_element("equivalence", "DISPLAY");

    # Register this "equivalence" occurrence:
    japs_register_occurrence('equivalence');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );

    log_message("\njaps_end_equivalence 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "propositionlist" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_propositionlist
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_propositionlist 1/3\n");

    # Enable output:
    unlock_output_list() unless ( $global_data->{free} );

    # Get the "variant" attribute:
    my $variant =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, "ENV", "propositionlist", "variant_arg")
       : 'plain');
    if ( ! grep($_ eq $variant, @{$scan_proc->{env_table}->{propositionlist}->{variant}}) )
      {
        &{$scan_proc->{error_handler}}("Invalid variant value: $variant");
      }
    log_message("\njaps_begin_propositionlist 2/3\n");
    log_data('Variant', $variant);

    # Start "propositionlist"  element:
    japs_start_content_xml_element("propositionlist", {'variant' => $variant}, "DISPLAY");

    # Enable "\proposition" command:
    install_cmds(['proposition'], 'japs_content');

    # Init proposition counter:
    $scan_proc->{proposition_count} = 0;

    log_message("\njaps_begin_propositionlist 3/3\n");
  }

sub japs_execute_proposition
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_proposition\n");

    # Close current proposition if necessary:
    japs_close_proposition() if ( $scan_proc->{proposition_count} > 0 );

    # Increase proposition counter:
    $scan_proc->{proposition_count}++;

    # Start new proposition:
    japs_start_proposition();
  }

sub japs_end_propositionlist
  {
    log_message("\njaps_end_propositionlist 1/2\n");

    # Close "propositionlist"  element:
    japs_close_content_xml_element("propositionlist", "DISPLAY");

    # Register this "propositionlist" occurrence:
    japs_register_occurrence('propositionlist');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );

    log_message("\njaps_end_propositionlist 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# The "algsteps" environment
# ------------------------------------------------------------------------------------------

sub japs_begin_algsteps
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_algsteps 1/3\n");

    # Enable output:
    unlock_output_list() unless ( $global_data->{free} );

    # Start "algsteps"  element:
    japs_start_content_xml_element("algsteps", {}, "DISPLAY");

    # Enable "\algstep" command:
    install_cmds(['algstep'], 'japs_content');

    # Init algstep counter:
    $scan_proc->{algstep_count} = 0;

    log_message("\njaps_begin_algsteps 3/3\n");
  }

sub japs_execute_algstep
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_algstep\n");

    # Close current algstep if necessary:
    japs_close_algstep() if ( $scan_proc->{algstep_count} > 0 );

    # Increase algstep counter:
    $scan_proc->{algstep_count}++;

    # Start new algstep:
    japs_start_algstep();
  }

sub japs_end_algsteps
  {
    log_message("\njaps_end_algsteps 1/2\n");

    # Close "algsteps"  element:
    japs_close_content_xml_element("algsteps", "DISPLAY");

    # Register this "algsteps" occurrence:
    japs_register_occurrence('algsteps');

    # Disable output:
    lock_output_list() unless ( $global_data->{free} );

    log_message("\njaps_end_algsteps 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{japs_content}->{initializer} = sub
  {
    @japs_toplevel_token_types =
      (
       "ign_whitesp",
       "cmd",
       "one_char_cmd",
       "comment",
      );

    @japs_token_types =
      (
       @basic_token_types,
       "inline_math_boundry"
      );

    my $cmd_table =
      {
       'title' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_title,
         },
       'defnotion' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_defnotion,
         },
       'isdefinedas' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_isdefinedas,
         },
       'implies' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_implies,
         },
       'isequivalentto' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_isequivalentto,
         },
       'proposition' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_proposition,
         },
       'algstep' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_algstep,
         },
      };

    my $env_table =
      {
       'content' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_content,
          'end_function' => \&japs_end_content,
          'early_pre_end_hook' =>  sub
            {
	      close_par_if_in_par();
	      close_strucs();
	    },
         },
       'suppositions' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_suppositions,
          'end_function' => \&japs_end_suppositions,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'defequivalence' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 1,
          'begin_function' => \&japs_begin_defequivalence,
          'end_function' => \&japs_end_defequivalence,
          'early_pre_end_hook' => \&japs_close_statement,
          'arrange' => ['side-by-side', 'top-bottom'],
         },
       'implication' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 1,
          'begin_function' => \&japs_begin_implication,
          'end_function' => \&japs_end_implication,
          'early_pre_end_hook' => \&japs_close_statement,
          'arrange' => ['side-by-side', 'top-bottom'],
         },
       'equivalence' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 1,
          'begin_function' => \&japs_begin_equivalence,
          'end_function' => \&japs_end_equivalence,
          'early_pre_end_hook' => \&japs_close_statement,
          'arrange' => ['side-by-side', 'top-bottom'],
         },
       'propositionlist' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 1,
          'begin_function' => \&japs_begin_propositionlist,
          'end_function' => \&japs_end_propositionlist,
          'early_pre_end_hook' => \&japs_close_proposition,
          'variant' => ['plain', 'equivalent'],
         },
       'statement' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_statement,
          'end_function' => \&japs_end_statement,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'background' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_background,
          'end_function' => \&japs_end_background,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'input' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_input,
          'end_function' => \&japs_end_input,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'output' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_output,
          'end_function' => \&japs_end_output,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'algsteps' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 1,
          'begin_function' => \&japs_begin_algsteps,
          'end_function' => \&japs_end_algsteps,
          'early_pre_end_hook' => \&japs_close_algstep,
         },
       'remark' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_remark,
          'end_function' => \&japs_end_remark,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
       'remarks' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 0,
          'begin_function' => \&japs_begin_remarks,
          'end_function' => \&japs_end_remarks,
          'early_pre_end_hook' => \&close_par_if_in_par,
         },
      };

    my @toplevel_cmds =
      ('title',
       'defnotion');

    my @toplevel_envs =
      ('suppositions',
       'defequivalence',
       'statement'); # TODO: continue

    deploy_lib
      ('japs_content',
       $cmd_table,
       $env_table,
       {'JAPS_CONTENT_TOPLEVEL' => \@toplevel_cmds},
       {'JAPS_CONTENT_TOPLEVEL' => \@toplevel_envs},
     );
  };

return(TRUE);

