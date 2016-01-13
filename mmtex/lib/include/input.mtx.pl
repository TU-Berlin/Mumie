package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: input.mtx.pl,v 1.4 2007/07/11 15:56:14 grudzin Exp $

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
# Provides the `\input` command as known from standard LaTeX.

log_message("\nLibrary \"input\" ", '$Revision: 1.4 $ ', "\n");

sub execute_input
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $raw_source_filename
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "input", "source_file_arg");
    my $source_filename = compose_filename($raw_source_filename, get_source_root_dir());

    log_message("\nexecute_input 1/2\n");
    log_data('Raw filename', $raw_source_filename,
             'Filename', $source_filename);

    my $source = read_file($source_filename);
    switch_prim_source($source, $raw_source_filename);
    scan();
    reset_prim_source();

    log_message("\nexecute_input 2/2\n");
  }

$input_cmd_table
  = {
     "input" =>
       {
	"num_opt_args" => 0,
	"num_man_args" => 1,
	"is_par_starter" => FALSE,
	"execute_function" => \&execute_input,
	"doc" =>
          {
	   "man_args" => [['filename','The name of the file to read']],
	   "description" => 'Reads and processes another MmTeX source file.'
	  }
       }
    };

$lib_table->{input}->{initializer} = sub
  {
    deploy_lib('input', $input_cmd_table, {},
               {'TOPLEVEL' => ['input'], 'PREAMBLE' => ['input']},
               FALSE);
  };

return(TRUE);
