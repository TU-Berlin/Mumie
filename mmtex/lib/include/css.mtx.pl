package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: css.mtx.pl,v 1.17 2008/01/20 22:36:34 rassy Exp $

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
#1                                    Description
# ----------------------------------------------------------------------------------------
#
# Defines commands to handle the css settings of a document

log_message("\nLibrary css ", '$Revision: 1.17 $ ', "\n");

sub add_css_styles
  {
    my @stylesheets_to_add = @_;
    log_message("\nadd_css_styles\n");
    log_data("Styles", join(", ", @stylesheets_to_add));
    foreach my $stylesheet_to_add (@stylesheets_to_add)
      {
        unless ( grep($stylesheet_to_add eq $_, @css_stylesheets) )
          {
            push(@css_stylesheets, $stylesheet_to_add);
          }
      }
  }

sub rm_css_styles
  {
    my @stylesheets_to_remove = @_;
    log_message("\nrm_css_styles\n");
    log_data("Styles", join(", ", @stylesheets_to_remove));
    foreach my $stylesheet_to_remove (@stylesheets_to_remove)
      {
        rm_string_from_array($stylesheet_to_remove, \@css_stylesheets);
      }
  }

sub execute_addstyles
  {
    log_message("\nexecute_addstyles 1/2\n");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $stylesheets_to_add_string
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "addstyles", "styles_arg");
    my @stylesheets_to_add
      = split(/\s*,\s*/, $stylesheets_to_add_string);
    add_css_styles(@stylesheets_to_add);
    log_message("\nexecute_addstyles 2/2\n");
  }

sub execute_rmstyles
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $stylesheets_to_remove_spec
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "rmstyles", "styles_arg");
    log_message("\nexecute_rmstyles 1/2\n");
    log_data("Spec", $stylesheets_to_remove_spec);
    if ( $stylesheets_to_remove_spec =~ m/^\s*\*\s*$/ )
      {
        @css_stylesheets = ();
      }
    else
      {
        my @stylesheets_to_remove
          = split(/\s*,\s*/, $stylesheets_to_remove_spec);
        rm_css_styles(@stylesheets_to_remove);
      }
    log_message("\nexecute_rmstyles 2/2\n");
  }

sub execute_setstyles
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $stylesheets_to_set_string
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "setstyles", "styles_arg");
    my @stylesheets_to_set
      = split(/\s*,\s*/, $stylesheets_to_set_string);
    log_message("\nexecute_setstyles\n");
    log_data("Styles", join(", ", @stylesheets_to_set));
    @css_stylesheets = @stylesheets_to_set;
  }

sub default_css_style_settings_writer
  {
    log_message("\ndefault_css_style_settings_writer 1/2\n");
    if ( @css_stylesheets )
      {
        start_xml_element("styles", {}, "DISPLAY");
        foreach my $stylesheet (@css_stylesheets)
          {
            empty_xml_element("css-stylesheet", {"href" => $stylesheet}, "DISPLAY");
          }
        close_xml_element("styles", "DISPLAY");
      }
    log_message("\ndefault_css_style_settings_writer 2/2\n");
  }

sub allow_css_classes
  {
    my ($ctr_type, $ctr_name, $new_classes) = @_;

    my $ctr_table;
    my $ctr_type_notation;
    my $ctr_notation;
    if ( $ctr_type eq 'CMD' )
      {
        $ctr_table = $scan_proc->{cmd_table};
        $ctr_type_notation = 'command';
        $ctr_notation = "\\$ctr_name";
      }
    elsif  ( $ctr_type eq 'ENV' )
      {
        $ctr_table = $scan_proc->{env_table};
        $ctr_type_notation = 'environment';
        $ctr_notation = "$ctr_name";
      }

    if ( ! $ctr_table->{$ctr_name} )
      {
        &{$scan_proc->{error_handler}}("Unknown $ctr_type_notation: $ctr_notation");
      }

    my $class_adder = ( $ctr_table->{$ctr_name}->{class_adder} || FALSE );

    log_message("\nallow_css_classes\n");
    log_data('Ctr type', $ctr_type,
             'Ctr name', $ctr_name,
             'Classes',  deref_log_list($new_classes),
             'Adder', $class_adder);

    if ( $class_adder )
      {
        &{$class_adder}($new_classes);
      }
    else
      {
        my $classes = $ctr_table->{$ctr_name}->{classes};
        if ( ! $classes )
          {
            &{$scan_proc->{error_handler}}
              (ucfirst($ctr_type_notation), " seems to have no class argument: $ctr_notation");
          }
        if ( ref($classes) ne 'ARRAY' )
          {
            notify_programming_error
              ("allow_css_classes: \"classes\" entry is not ARRAY: $ctr_notation");
          }
        foreach my $new_class (@{$new_classes})
          {
            unless ( grep(/$new_class/, @{$classes}) )
              {
                if ( $new_class !~ m/$class_regexp/ )
                  {
                    &{$scan_proc->{error_handler}}
                      ("Invalid class name: $new_class");
                  }
                push(@{$classes}, $new_class);
              }
          }
      }
  }

sub execute_addcmdclasses
  {
    log_message("\naddcmdclasses\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $cmd_name = $man_args->[0];
    $cmd_name =~ s/^\\//;
    my $classes_str =
      get_data_from_arg(1, $man_args, $pos_man_args, 'CMD', 'addcmdclasses', 'classes_arg');

    my @new_classes = split(/\s*,\s*/, $classes_str);

    allow_css_classes('CMD', $cmd_name, \@new_classes);
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------


$lib_table->{css}->{initializer} = sub
  {

    my $css_cmd_table =
      {
       "addstyles" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_addstyles,
          "pre_styles_arg_hook"
            => sub { allow_tokens(["plain_text_extended"], "MODIFY", "APPEND") },
          "doc" =>
            {
             "man_args" => [['stylesheets','Comma separated list of stylesheets to add']],
             "description" => 'Adds one or more CSS stylesheets',
             'see' =>
               {'cmds' => ['setstyles', 'rmstyles']},
            }
         },
       "rmstyles" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_rmstyles,
          "pre_styles_arg_hook"
            => sub { allow_tokens(["plain_text_extended"], "MODIFY", "APPEND") },
          "doc" =>
            {
             "man_args" => [['stylesheets','Comma separated list of stylesheets to remove']],
             "description" => 'Removes one or more CSS stylesheets',
             'see' =>
               {'cmds' => ['addstyles', 'setstyles']},

            }
         },
       "setstyles" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_setstyles,
          "pre_styles_arg_hook"
            => sub { allow_tokens(["plain_text_extended"], "MODIFY", "APPEND") },
          "doc" =>
            {
             "man_args" => [['stylesheet','name of the stylesheet']],
             "description" => 'Sets one or more CSS stylesheets.',
             'see' =>
               {'cmds' => ['addstyles', 'rmstyles']},
            }
         },
       "addcmdclasses" =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 2,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_addcmdclasses,
          "pre_classes_arg_hook"
            => sub { allow_tokens(["plain_text_extended"], "MODIFY", "APPEND") },
          "doc" =>
            {
             "man_args" => 
               [
                ['cmd','Name of the command'],
                ['classes','Comma separated list of classes'],
               ],
             "description" =>
               'Allows a single or a list of (comma separated) classes for command %{1}',
            }
         },
      };

    @css_stylesheets = ();
    $css_style_settings_writer = \&default_css_style_settings_writer;

    deploy_lib('css', $css_cmd_table, {}, 'PREAMBLE', FALSE);
  };

return(TRUE);
