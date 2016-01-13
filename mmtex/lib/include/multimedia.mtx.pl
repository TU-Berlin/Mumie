package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: multimedia.mtx.pl,v 1.18 2007/07/11 15:56:14 grudzin Exp $

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

log_message("\nLibrary multimedia ", '$Revision: 1.18 $ ', "\n");

require_lib('simple_markup');

# --------------------------------------------------------------------------------
# Auxiliaries
# --------------------------------------------------------------------------------

sub image_align_char_to_name
  # ($align_char, $class)
  {
    my ($align_char, $class) = @_;
    my $align_table
      = $scan_proc->{cmd_table}->{image}->{classes}->{$class}->{aligns};
    return(align_char_to_name($align_char, $align_table));
  }

# --------------------------------------------------------------------------------
# Images
# --------------------------------------------------------------------------------

sub execute_image
  {
    log_message("\nexecute_image 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    my $attribs = {};

    my $src = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', 'image', 'src_arg');
    $attribs->{src} = ( $scan_proc->{image_dir} ? $scan_proc->{image_dir} . $src : $src );

    my $class = "normal";
    if ( $opt_args->[0] )
      {
	$class
	  = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD", "image", "class_arg");
      }
    $attribs->{class} = $class unless ( $class eq "normal" );

    my $align_char = "d";
    if ( $opt_args->[1] )
      {
	$align_char
	  = get_data_from_arg(1, $opt_args, $pos_opt_args, "CMD", "image", "align_arg");
      }
    $attribs->{align} = image_align_char_to_name($align_char, $class)
      unless ( $align_char eq "d" );


    my $fallback = "";
    if ( $opt_args->[2] )
      {
	$fallback
	  = get_data_from_arg(2, $opt_args, $pos_opt_args, "CMD", "image", "fallback_arg");
      }
    $attribs->{alt} = $fallback unless ( $fallback eq "");

    empty_xml_element("image", $attribs, "DISPLAY");

    log_message("\nexecute_image 2/2\n");
    log_data('Src', $src,
	     'Class', $class,
	     'Align', $align,
	     'Fallback', $fallback);
  }

sub execute_setimagedir
  {
    log_message("\nexecute_setimagedir\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    my $dir
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "setimagedir", "dir_arg");

    ( $dir .= "/" ) if ( $dir !~ m/\/$/ );

    $scan_proc->{image_dir} = $dir;
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{multimedia}->{initializer} = sub
  {
    # Command table for export
    my $multimedia_cmd_table
      = {
         "image" =>
           {
            "num_opt_args" => 3,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_image,
            "classes" =>
              {
               "normal" =>
                  {
                   "aligns" =>
                     {
                      "d" => "",
                      "l" => "left",
                      "r" => "right",
                      "c" => "center"
                     }
                  }
              },
            "pre_src_arg_hook" => sub
              {
                log_message("\npre_src_arg_hook (command \\image)\n");
                my @allowed_token_value = @{$scan_proc->{allowed_tokens}};
                subst_string_in_array("plain_text", "plain_text_extended", \@allowed_token_value);
                $scan_proc->{allowed_tokens} = \@allowed_token_value;
              },
            "doc" =>
              {
               "man_args" => [['filename','Name of the image file']],
               "opt_args" =>
                 [
                  ['class','Class'],
                  ['align','Horizontal alignment'],
                  ['fallback','Alternative (fallback) text'],
                 ],
               "description" =>
                 'Inserts an image. %[2] must be a one-character alignment descriptor ' .
                 '(e.g., l for left, c for center, ...).'
              }
           },
         "setimagedir" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_setimagedir,
            "doc" =>
              {
               "man_args" => [['image_dir','Image directory']],
               "description" => 'Sets the image directory '
              }
           },
        };

    my @_generic_events =
      (
       'onclick',
       'ondblclick',
       'onmousedown',
       'onmouseup',
       'onmouseover',
       'onmousemove',
       'onmouseout',
       'onkeypress',
       'onkeydown',
       'onkeyup',
      );

    foreach my $event (@_generic_events)
      {
        $multimedia_cmd_table->{$event} =
          {
           'num_opt_args' => 0,
           'num_man_args' => 1,
           'is_par_starter' => FALSE,
           'execute_function' =>
             sub { execute_generic_attrib_cmd(@_, $event, $event, $event); },
           'doc' =>
             {
              'man_args' => [['action', 'Action to perform when the event occurs']],
              'description' => "Sets an action for the $event event.",
             }
          }
        }

    my @multimedia_cmds = keys(%{$multimedia_cmd_table});
    deploy_lib('multimedia', $multimedia_cmd_table, {},
               {'TOPLEVEL' => \@multimedia_cmds, 'MULTIMEDIA' => \@multimedia_cmds},
               FALSE);
  };

return(TRUE);
