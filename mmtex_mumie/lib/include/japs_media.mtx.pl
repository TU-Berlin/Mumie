# Author: Tilman Rassy  <rassy@math.tu-berlin.de>
# $Id: japs_media.mtx.pl,v 1.5 2008/12/04 11:56:05 rassy Exp $

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

log_message("\nLibrary japs_media ", '$Revision: 1.5 $ ', "\n");

# ---------------------------------------------------------------
#1 Auxiliaries
# ---------------------------------------------------------------

sub japs_get_applet_attribs
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_get_applet_attribs 1/3\n");
    my $lid = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    my $width = ( $opt_args->[0]
                  ? get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD')
                  : undef() );
    my $height = ( $opt_args->[1]
                   ? get_data_from_arg(1, $opt_args, $pos_opt_args, 'CMD')
                   : undef() );
    log_message("\njaps_get_applet_attribs 2/3\n");
    log_data('lid', $lid, 'width', $width, 'height', $height);
    my $attribs = {};
    japs_check_lid($lid, 'applet');
    $attribs->{lid} = $lid;
    push(@{$global_data->{used_lids}}, $lid);
    if ( $width )
      {
        japs_check_length($width);
        $attribs->{width} = $width;
      }
    if ( $height )
      {
        japs_check_length($height);
        $attribs->{height} = $height;
      }
    log_message("\njaps_get_applet_attribs 3/3\n");
    return($attribs);
  }

sub japs_get_flash_attribs
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_get_flash_attribs 1/3\n");
    my $lid = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    my $width = ( $opt_args->[0]
                  ? get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD')
                  : undef() );
    my $height = ( $opt_args->[1]
                   ? get_data_from_arg(1, $opt_args, $pos_opt_args, 'CMD')
                   : undef() );
    log_message("\njaps_get_flash_attribs 2/3\n");
    log_data('lid', $lid, 'width', $width, 'height', $height);
    my $attribs = {};
    japs_check_lid($lid, 'flash');
    $attribs->{lid} = $lid;
    push(@{$global_data->{used_lids}}, $lid);
    if ( $width )
      {
        japs_check_length($width);
        $attribs->{width} = $width;
      }
    if ( $height )
      {
        japs_check_length($height);
        $attribs->{height} = $height;
      }
    log_message("\njaps_get_flash_attribs 3/3\n");
    return($attribs);
  }

sub japs_check_length
  {
    my ($length) = @_;
    if ( $length !~ m/^[0-9]+$/ )
      {
        $scan_proc->{error_handler}("invalid length value: $length.");
      }
  }

sub japs_set_lang_param
  {
    if ( $scan_proc->{lang} && !$scan_proc->{lang_param_set} )
      {
        log_message("\njaps_set_lang_param 1/2\n");
        log_data('Lang', $scan_proc->{lang});
        empty_xml_element('param', {'name' => 'lang', 'value' => $scan_proc->{lang}}, 'DISPLAY');
        log_message("\njaps_set_lang_param 2/2\n");
      }
    else
      {
        log_message("\njaps_set_lang_param 1/1: No language specified\n");
      }
  }

# ---------------------------------------------------------------
# Command \image
# ---------------------------------------------------------------

sub japs_execute_image
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_image 1/3\n");
    my $lid = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    log_message("\njaps_execute_image 2/3\n");
    log_data('lid', $lid);
    japs_check_lid($lid, 'image');
    push(@{$global_data->{used_lids}}, $lid);
    empty_xml_element('image', {'lid' => $lid}, 'DISPLAY');
    log_message("\njaps_execute_image 3/3\n");
  }

# ---------------------------------------------------------------
#1 Command \applet
# ---------------------------------------------------------------

sub japs_execute_applet
  {
    log_message("\njaps_execute_applet 1/2\n");
    start_xml_element('applet', japs_get_applet_attribs(@_), 'DISPLAY');
    japs_set_lang_param();
    close_xml_element('applet', 'DISPLAY');
    log_message("\njaps_execute_applet 2/2\n");
  }

# ---------------------------------------------------------------
#1 Environment applet
# ---------------------------------------------------------------

sub japs_begin_applet
  {
    log_message("\njaps_begin_applet 1/2\n");
    start_xml_element('applet', japs_get_applet_attribs(@_), 'DISPLAY');
    allow_tokens(['ign_whitesp'], 'PROTECT', 'PREPEND');
    lock_output_list();
    $scan_proc->{locked_output_list_error_msg}
      = 'No text allowed in environment "applet".';
    $scan_proc->{scan_failed_handler} = sub
      {
        &{$scan_proc->{error_handler}}
          ("Invalid input. Most likely, you placed text directly in the \"applet\"\n" .
           "environment. This is not allowed.");
      };
    install_cmds(['param'], 'japs_media');
  }

sub japs_end_applet
  {
    log_message("\njaps_end_applet 1/2\n");
    unlock_output_list();
    japs_set_lang_param();
    close_xml_element('applet', 'DISPLAY');
    log_message("\njaps_end_applet 2/2\n");
  }

# ---------------------------------------------------------------
#1 Command \flash
# ---------------------------------------------------------------

sub japs_execute_flash
  {
    log_message("\njaps_execute_flash 1/2\n");
    empty_xml_element('flash', japs_get_flash_attribs(@_), 'DISPLAY');
    log_message("\njaps_execute_flash 2/2\n");
  }

# ---------------------------------------------------------------
#1 Environment flash
# ---------------------------------------------------------------

sub japs_begin_flash
  {
    log_message("\njaps_begin_flash 1/2\n");
    start_xml_element('flash', japs_get_flash_attribs(@_), 'DISPLAY');
    allow_tokens(['ign_whitesp'], 'PROTECT', 'PREPEND');
    lock_output_list();
    $scan_proc->{locked_output_list_error_msg}
      = 'No text allowed in environment "flash".';
    install_cmds(['param'], 'japs_media');
  }

sub japs_end_flash
  {
    log_message("\njaps_end_flash 1/2\n");
    unlock_output_list();
    close_xml_element('flash', 'DISPLAY');
    log_message("\njaps_end_flash 2/2\n");
  }

# ---------------------------------------------------------------
#1 Command \param
# ---------------------------------------------------------------

sub japs_execute_param
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_param 1/3\n");
    unlock_output_list();
    disallow_tokens(['ign_whitesp']);
    my $name = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    my $value = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD');
    log_message("\njaps_execute_param 2/3\n");
    log_data('name', $name, 'value', $value);
    empty_xml_element('param', {'name' => $name, 'value' => $value}, 'DISPLAY');
    lock_output_list();
    allow_tokens(['ign_whitesp'], 'MODIFY', 'PREPEND');
    if ( $name eq 'lang' )
      { $scan_proc->{lang_param_set} = TRUE; }
    log_message("\njaps_execute_param 3/3\n");
  }

# --------------------------------------------------------------------------------
#1 Initializing
# --------------------------------------------------------------------------------

$lib_table->{japs_media}->{initializer} = sub
  {
    my $japs_media_cmd_table =
      {
       'image' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 1,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_image,
          'doc' =>
            {
             'man_args' => [['lid', 'The local id of the image.']],
             'description' =>
               'Includes the image with the local id %{1}.',
            },
         },
       'applet' =>
         {
          'num_opt_args' => 2,
          'num_man_args' => 1,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_applet,
          'doc' =>
            {
             'man_args' => [['lid', 'The local id of the applet.']],
             'opt_args' => [['width', 'Width of the applet, in pixels.'],
                            ['height', 'Height of the applet, in pixels.']],
             'description' =>
               'Includes the applet with the local id %{1}. If %[1] and/or %[2] is given, the ' .
               'width and/or height of the applet is set to the respective value. Otherwise, ' .
               'the default values from the applet metadata are used. If you want to pass ' .
               'parameters to the applet, use the applet environment instead.',
             'see' => {'envs' => ['applet']},
            },
         },
       'flash' =>
         {
          'num_opt_args' => 2,
          'num_man_args' => 1,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_flash,
          'doc' =>
            {
             'man_args' => [['lid', 'The local id of the flash.']],
             'opt_args' => [['width', 'Width of the flash, in pixels.'],
                            ['height', 'Height of the flash, in pixels.']],
             'description' =>
               'Includes the flash with the local id %{1}. If %[1] and/or %[2] is given, the ' .
               'width and/or height of the flash is set to the respective value. Otherwise, ' .
               'the default values from the flash metadata are used. If you want to pass ' .
               'parameters to the flash, use the flash environment instead.',
             'see' => {'envs' => ['flash']},
            },
         },
       'param' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 2,
          'is_par_starter' => FALSE,
          'execute_function' => \&japs_execute_param,
          'doc' =>
            {
             'man_args' => [['name', 'Width of the applet, in pixels.'],
                            ['value', 'Height of the applet, in pixels.']],
             'description' =>
               'Includes the applet with the local id %{1}. If %[1] and/or %[2] is given, the ' .
               'width and/or height of the applet is set to the respective value. Otherwise, ' .
               'the default values from the applet metadata are used. If you want to pass ' .
               'parameters to the applet, use the applet environment instead.',
             'see' => {'envs' => ['applet']},
            },
         }
      };

    my $japs_media_env_table =
      {
       'applet' =>
         {
          'num_opt_args' => 2,
          'num_man_args' => 1,
          "begin_function" => \&japs_begin_applet,
          "end_function" => \&japs_end_applet,
          'doc' =>
            {
             'man_args' => [['lid', 'The local id of the applet.']],
             'opt_args' => [['width', 'Width of the applet, in pixels.'],
                            ['height', 'Height of the applet, in pixels.']],
             'description' =>
               'Includes the applet with the local id %{1}. If %[1] and/or %[2] is given, the ' .
               'width and/or height of the applet is set to the respective value. Otherwise, ' .
               'the default values from the applet metadata are used. Parameters can be set ' .
               'by nested \param commands.',
               'see' => {'cmds' => ['param']},
            },
         },
       'flash' =>
         {
          'num_opt_args' => 2,
          'num_man_args' => 1,
          "begin_function" => \&japs_begin_flash,
          "end_function" => \&japs_end_flash,
          'doc' =>
            {
             'man_args' => [['lid', 'The local id of the flash.']],
             'opt_args' => [['width', 'Width of the flash, in pixels.'],
                            ['height', 'Height of the flash, in pixels.']],
             'description' =>
               'Includes the flash with the local id %{1}. If %[1] and/or %[2] is given, the ' .
               'width and/or height of the flash is set to the respective value. Otherwise, ' .
               'the default values from the flash metadata are used. Parameters can be set ' .
               'by nested \param commands.',
               'see' => {'cmds' => ['param']},
            },
         },
      };

    my @all_cmds = keys(%{$japs_media_cmd_table});
    my @toplevel_cmds = @all_cmds;
    rm_string_from_array(\@toplevel_cmds, ['param']);
    my @all_envs = keys(%{$japs_media_env_table});
    my @toplevel_envs = @all_envs;

    deploy_lib
      (
       'japs_media',
       $japs_media_cmd_table,
       $japs_media_env_table,
       {'JAPS_MEDIA_TOPLEVEL' => \@toplevel_cmds, 'JAPS_MEDIA' => \@all_cmds},
       {'JAPS_MEDIA_TOPLEVEL' => \@toplevel_envs, 'JAPS_MEDIA' => \@all_envs}
      );
  };


return(TRUE);
