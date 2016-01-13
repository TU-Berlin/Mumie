package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: box.mtx.pl,v 1.10 2008/01/20 22:34:32 rassy Exp $

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
# Boxes

log_message("\nLibrary box ", '$Revision: 1.10 $', "\n");

require_lib("length");

# ------------------------------------------------------------------------------------------
# Environment `block`
# ------------------------------------------------------------------------------------------

sub begin_block
  {
    log_message("\nbegin_block 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $attribs = {};
    if ( $opt_args->[0] )
      {
	my $class = get_data_from_arg(0, $opt_args, $pos_opt_args, 'ENV');
        check_class($class, 'ENV', 'block');
        $attribs->{class} = $class;
      }

    if ( $opt_args->[1] )
      {
	my $width =
	  get_xml_length_spec(get_length_from_arg(1, $opt_args, $pos_opt_args, 'ENV'));
	$attribs->{width} = $width;
      }

    if ( $opt_args->[2] )
      {
	my $height =
	  get_xml_length_spec(get_length_from_arg(2, $opt_args, $pos_opt_args, 'ENV'));
	$attribs->{height} = $height;
      }

    if ( $opt_args->[3] )
      {
	my $content_align_spec = get_data_from_arg(3, $opt_args, $pos_opt_args, 'ENV');
	my $content_align = align_char_to_name($content_align_spec, $horizontal_aligns);
	$attribs->{'content-align'} = $content_align;
      }

    if ( $opt_args->[4] )
      {
	my $content_valign_spec = get_data_from_arg(4, $opt_args, $pos_opt_args, 'ENV');
	my $content_valign = align_char_to_name($content_valign_spec, $vertical_aligns);
	$attribs->{'content-valign'} = $content_valign;
      }

    start_xml_element('block', $attribs, 'DISPLAY');

    log_message("\nbegin_block 2/2\n");
  }

sub end_block
  {
    log_message("\nend_block\n");
    close_xml_element('block', 'DISPLAY');
  }

# ------------------------------------------------------------------------------------------
# Command \box
# ------------------------------------------------------------------------------------------

sub execute_box
  {
    log_message("\nexecute_box 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $attribs = {};
    if ( $opt_args->[0] )
      {
	my $class = get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD');
        check_class($class, 'CMD', 'box');
        $attribs->{class} = $class;
      }

    start_xml_element('box', $attribs, 'INLINE');

    convert_arg(0, $man_args, $pos_man_args, 'CMD', 'box', 'content_arg', FALSE, FALSE,
		'SHARED_OUTPUT_LIST');

    close_xml_element('box', 'INLINE');

    log_message("\nexecute_box 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Command \mbox
# ------------------------------------------------------------------------------------------

sub execute_mbox
  {
    log_message("\nexecute_mbox 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    start_xml_element('box', {}, 'INLINE');

    convert_arg(0, $man_args, $pos_man_args, 'CMD', 'box', 'content_arg', FALSE, FALSE,
		'SHARED_OUTPUT_LIST');

    close_xml_element('box', 'INLINE');

    log_message("\nexecute_mbox 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Command \fbox
# ------------------------------------------------------------------------------------------

sub execute_fbox
  {
    log_message("\nexecute_fbox 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    start_xml_element('box', {'class' => 'frame'}, 'INLINE');

    convert_arg(0, $man_args, $pos_man_args, 'CMD', 'box', 'content_arg', FALSE, FALSE,
		'SHARED_OUTPUT_LIST');

    close_xml_element('box', 'INLINE');

    log_message("\nexecute_fbox 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Commands \makebox and \framebox
# ------------------------------------------------------------------------------------------

sub execute_makebox_or_framebox
  {
    log_message("\nexecute_makebox_or_framebox 1/2\n");

    my ($cmd_name, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $attribs = {};

    $attribs->{class} = 'frame' if ( $cmd_name eq 'framebox' );

    if ( $opt_args->[0] )
      {
	my $width =
	  get_xml_length_spec(get_length_from_arg(0, $opt_args, $pos_opt_args, 'CMD'));
	$attribs->{width} = $width;
      }

    if ( $opt_args->[1] )
      {
	my $content_align_spec = get_data_from_arg(1, $opt_args, $pos_opt_args, 'CMD');
	my $content_align = align_char_to_name($content_align_spec, $horizontal_aligns);
	$attribs->{'content-align'} = $content_align;
      }

    start_xml_element('box', $attribs, 'INLINE');

    convert_arg(0, $man_args, $pos_man_args, 'CMD', $cmd_name, 'content_arg', FALSE, FALSE,
		'SHARED_OUTPUT_LIST');

    close_xml_element('box', 'INLINE');

    log_message("\nexecute_makebox_or_framebox 2/2\n");
  }

# --------------------------------------------------------------------------------=
# Environment and command table for export
# --------------------------------------------------------------------------------



# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{box}->{initializer} = sub
  {
    my $box_env_table =
      {
       'block' =>
         {
          'num_opt_args' => 5,
          'num_man_args' => 0,
          'begin_function' => \&begin_block,
          'end_function' => \&end_block,
          'early_pre_end_hook' => \&close_par_if_in_par,
          'classes' => ['frame', 'note', 'important', 'remark', 'warning'],
          'is_par_closer' => TRUE,
          'is_par_starter' => FALSE,
          'doc' =>
             {
              'opt_args' =>
                [
                 ['class', 'Class of the block'],
                 ['width', 'Width of the block'],
                 ['height', 'Height of the block'],
                 ['align', 'Horizontal align of the content'],
                 ['valign', 'Vertical align of the content']
                ],
              'description' => 
                'A distinct block of content. Usually rendered as a box. ' .
                '%[2] must be a one-character horizontal alignment specifier, e.g., ' .
                'l for left, r for right, etc. %[3] must be a one-character vertical ' .
                'alignment specifier, e.g., t for top, b for bottom, etc.',
             }
         },
      };

    my $box_cmd_table =
      {
       'box' =>
         {
          'num_opt_args' => 1,
          'num_man_args' => 1,
          'execute_function' => \&execute_box,
          'post_content_arg_hook' => \&close_par_if_in_par,
          'is_par_starter' => TRUE,
          'doc' =>
             {
              'opt_args' =>
                [
                 ['class', 'Class of the box'],
                ],
              'man_args' =>
                [
                 ['content', 'Content of the box'],
                ],
              'description' =>
                'A box. Usually renderd in inline layout.',
             }
         },
       'mbox' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'execute_function' => \&execute_mbox,
          'post_content_arg_hook' => \&close_par_if_in_par,
          'is_par_starter' => TRUE,
          'doc' =>
             {
              'man_args' =>
                [
                 ['content', 'Content of the box'],
                ],
              'description' =>
                'A box. Usually renderd in inline layout.',
             }
         },
       'fbox' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'execute_function' => \&execute_fbox,
          'post_content_arg_hook' => \&close_par_if_in_par,
          'is_par_starter' => TRUE,
          'doc' =>
             {
              'man_args' =>
                [
                 ['content', 'Content of the box'],
                ],
              'description' =>
                'A box with a frame. Usually renderd in inline layout.',
             }
         },
       'makebox' =>
         {
          'num_opt_args' => 2,
          'num_man_args' => 1,
          'execute_function' => sub { execute_makebox_or_framebox('makebox', @_) },
          'post_content_arg_hook' => \&close_par_if_in_par,
          'is_par_starter' => TRUE,
          'doc' =>
             {
              'opt_args' =>
                [
                 ['width', 'Width of the box'],
                 ['align', 'Horizontal align of the content'],
                ],
              'man_args' =>
                [
                 ['content', 'Content of the box'],
                ],
              'description' =>
                'A box. Usually renderd in inline layout.',
             }
         },
       'framebox' =>
         {
          'num_opt_args' => 2,
          'num_man_args' => 1,
          'execute_function' => sub { execute_makebox_or_framebox('framebox', @_) },
          'post_content_arg_hook' => \&close_par_if_in_par,
          'is_par_starter' => TRUE,
          'doc' =>
             {
              'opt_args' =>
                [
                 ['width', 'Width of the box'],
                 ['align', 'Horizontal align of the content'],
                ],
              'man_args' =>
                [
                 ['content', 'Content of the box'],
                ],
              'description' =>
                'A box with a frame. Usually renderd in inline layout.',
             }
         },
      };

    deploy_lib('box', $box_cmd_table, $box_env_table, 'TOPLEVEL', 'TOPLEVEL');
  };

return(TRUE);
