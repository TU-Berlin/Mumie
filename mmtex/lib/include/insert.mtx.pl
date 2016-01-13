package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: insert.mtx.pl,v 1.12 2007/07/11 15:56:14 grudzin Exp $

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
# Inserts and converts another MmTeX source file
# DEPRECATED: Use library "imput" instead

log_message("\nLibrary insert ", '$Revision: 1.12 $ ', "\n");

sub insert_source_file
  {
    my ($raw_source_filename, $pre_scan_hook, $post_scan_hook) = @_;

    my $source_filename = compose_filename($raw_source_filename, get_source_root_dir());

    log_message("\ninsert_source_file\n");
    log_data('Filename raw', $raw_source_filename,
	     'Filename', $source_filename);
    open(SOURCE, $source_filename) or
      &{$scan_proc->{error_handler}}("Can't open file $source_file: $!\n");
    my $source = join("", <SOURCE>);
    close(SOURCE);
    convert_insert(\$source, $source_filename, $pre_scan_hook, $post_scan_hook);
  }

sub execute_insert
  {
    log_message("\nexecute_insert\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $source_file
      = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "insert", "source_file_arg");
    insert_source_file($source_file);
  }


# ------------------------------------------------------------------------------------------
# Commmand table
# ------------------------------------------------------------------------------------------

$insert_cmd_table
  = {
     "insert" =>
       {
	"num_opt_args" => 0,
	"num_man_args" => 1,
	"is_par_starter" => FALSE,
	"execute_function" => \&execute_insert,
	"doc" =>
          {
	   "man_args" => [['filename','The name of the file to read']],
	   "description" => 'Reads and processes another MmTeX source file.'
	  }
       }
    };

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{insert}->{initializer} = sub
  {
    deploy_lib('insert', $insert_cmd_table, {}, 'INSERT', FALSE);
  };

return(TRUE);


