package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: japs_course.mtx.pl,v 1.3 2008/06/17 11:28:30 rassy Exp $

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

log_message("\nLibrary \"japs_course\" ", '$Revision: 1.3 $ ', "\n");

# ------------------------------------------------------------------------------------------
# Description
# ------------------------------------------------------------------------------------------
#
# Code for creating the content of courses, course sections, and worksheets.

# --------------------------------------------------------------------------------
# Code to be executed at beginning/end of a course
# --------------------------------------------------------------------------------

sub japs_start_course
  {
    log_message("\njaps_start_course\n");
    if ( $global_data->{arrange} eq 'list' )
      {
        # Start "list' element:
        unlock_output_list();
        japs_start_content_xml_element('list', {}, "DISPLAY");
        lock_output_list();
      }
    elsif( $global_data->{arrange} eq 'tree' )
      {
        # Start "tree' element:
        unlock_output_list();
        japs_start_content_xml_element('tree', {}, "DISPLAY");
        lock_output_list();
      }
  }

sub japs_close_course
  {
    log_message("\njaps_close_course\n");
    if ( $global_data->{arrange} eq 'list' )
      {
        # Close "list' element:
        unlock_output_list();
        japs_close_content_xml_element('list', "DISPLAY");
        lock_output_list();
      }
    elsif( $global_data->{arrange} eq 'tree' )
      {
        # Close "tree' element:
        unlock_output_list();
        japs_close_content_xml_element('tree', "DISPLAY");
        lock_output_list();
      }
  }

# --------------------------------------------------------------------------------
# Environments to insert components
# --------------------------------------------------------------------------------

sub japs_begin_course_component
  {
    my ($env, $type, $element, $cmds, $forbidden_cmds,
        $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_begin_course_component\n");
    my $lid = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_check_lid($lid, $type);
    unlock_output_list();
    japs_start_content_xml_element($element, {'lid' => $lid}, "DISPLAY");
    install_cmds($cmds, 'japs_course');
    disable_cmds($forbidden_cmds, 'japs_course');
    lock_output_list();
  }

sub japs_end_course_component
  {
    my $element = $_[0];
    log_message("\njaps_end_course_component\n");
    unlock_output_list();
    japs_close_content_xml_element($element, "DISPLAY");
    lock_output_list();
  }

# --------------------------------------------------------------------------------
# Commands to insert components
# --------------------------------------------------------------------------------

sub japs_execute_course_component
  {
    my ($cmd, $type, $element, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_course_component\n");
    my $lid = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_check_lid($lid, $type);
    unlock_output_list();
    japs_empty_content_xml_element($element, {'lid' => $lid}, 'DISPLAY');
    lock_output_list();
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{japs_course}->{initializer} = sub
  {
    my $cmd_table =
      {
       'crssection' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => sub
            {
              japs_execute_course_component
                ('crssection', 'course_section', 'course_section', @_);
            },
         },
       'worksheet' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => sub
            {
              japs_execute_course_component
                ('worksheet', 'worksheet', 'worksheet', @_);
            },
         },
       'gelement' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => sub
            {
              japs_execute_course_component
                ('gelement', 'generic_element', 'generic_element', @_);
            },
         },
       'gsubelement' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => sub
            {
              japs_execute_course_component
                ('gsubelement', 'generic_subelement', 'generic_subelement', @_);
            },
         },
       'gproblem' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => sub
            {
              japs_execute_course_component
                ('gproblem', 'generic_problem', 'generic_problem', @_);
            },
         },
      };

    my $env_table =
      {
       'crssection' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'begin_function' => sub
            {
              japs_begin_course_component
                ('crssection', 'course_section', 'course_section',
                 ['worksheet'], ['crssection'], @_);
            },
          'end_function' => sub
            {
              japs_end_course_component('course_section');
            },
         },
       'gelement' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'begin_function' => sub
            {
              japs_begin_course_component
                ('gelement', 'generic_element', 'generic_element',
                 ['gsubelement'], ['gelement'], @_);
            },
          'end_function' => sub
            {
              japs_end_course_component('generic_element');
            },
         },
      };

    my @toplevel_cmds_course = ('crssection', 'worksheet');
    my @toplevel_cmds_coursesection = ('gelement', 'gsubelement');
    my @toplevel_cmds_worksheet = ('gproblem');

    my @toplevel_envs_course = ('crssection');
    my @toplevel_envs_coursesection = ('gelement');

    deploy_lib
      ('japs_course',
       $cmd_table,
       $env_table,
       {
        'JAPS_COURSE_TOPLEVEL' => \@toplevel_cmds_course,
        'JAPS_COURSESECTION_TOPLEVEL' => \@toplevel_cmds_coursesection,
        'JAPS_WORKSHEET_TOPLEVEL' => \@toplevel_cmds_worksheet
       },
       {
        'JAPS_COURSE_TOPLEVEL' => \@toplevel_envs_course,
        'JAPS_COURSESECTION_TOPLEVEL' => \@toplevel_envs_coursesection,
        'JAPS_WORKSHEET_TOPLEVEL' => []
       },
     );
  };

return(TRUE);
