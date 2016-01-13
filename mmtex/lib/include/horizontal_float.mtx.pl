package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: horizontal_float.mtx.pl,v 1.5 2007/07/11 15:56:14 grudzin Exp $

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


log_message("\nLibrary \"horizontal_float\" ", '$Revision: 1.5 $ ', "\n");


# --------------------------------------------------------------------------------
# Execute functions
# --------------------------------------------------------------------------------

sub execute_horizontal_float_cmd
  {
    log_message("\nexecute_horizontal_float_cmd\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args,
	$cmd_name, $xml_element_name) = @_;

    # Opening XML element
    start_xml_element($xml_element_name, {}, 'DISPLAY');

    # Converting argumnent

    convert_arg
      (0, $man_args, $pos_man_args, 'CMD', $cmd_name, 'arg', FALSE, FALSE, 'SHARED_OUTPUT_LIST');

    # Closing XML element
    close_xml_element($xml_element_name, 'DISPLAY');
  }


# --------------------------------------------------------------------------------
# Command table for export
# --------------------------------------------------------------------------------


$horizontal_float_cmd_table =
  {
   'floatleft' =>
     {
      'num_opt_args' => 0,
      'num_man_args' => 1,
      'is_par_starter' => TRUE,
      'execute_function' => sub { execute_horizontal_float_cmd(@_, 'floatleft', 'float-left') },
      'doc' =>
        {
	 'man_args' => [['content', 'Content to float']],
	 'description' => 'Floats %{1} to the left.',
	}
     },
   'floatright' =>
     {
      'num_opt_args' => 0,
      'num_man_args' => 1,
      'is_par_starter' => TRUE,
      'execute_function' => sub { execute_horizontal_float_cmd(@_, 'floatright', 'float-right') },
      'doc' =>
        {
	 'man_args' => [['content', 'Content to float']],
	 'description' => 'Floats %{1} to the right.',
	}
     },
    };



# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{horizontal_float}->{initializer} = sub
  {
    deploy_lib('horizontal_float', $horizontal_float_cmd_table);
  };

return(TRUE);



