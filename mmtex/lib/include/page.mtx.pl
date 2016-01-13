package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: page.mtx.pl,v 1.13 2007/07/11 15:56:14 grudzin Exp $

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
# ...

log_message("\nLibrary page ", '$Revision: 1.13 $ ', "\n");

sub begin_page
  {
    log_message("\nbegin_page 1/3\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $target_name
      = get_data_from_arg(0, $man_args, $pos_man_args, "ENV", "page", "target_name_arg");

    my $toc_target_name;
    if ( $opt_args->[0] )
      {
	$toc_target_name
	  = get_data_from_arg(0, $opt_args, $pos_opt_args, "ENV", "page", "toc_target_name_arg");
      }
    elsif ( $target_name =~ m/^(.+)\.[^\.]+$/ )
      {
	$toc_target_name = $1;
      }
    else
      {
	$toc_target_name = $target_name;
      }

    log_message("\nbegin_page 2/3\n");
    log_data('Target', $target_name, 'TOC Target', $toc_target_name);

    $scan_proc->{target_name} = $target_name;
    $scan_proc->{toc_target_name} = $toc_target_name;
    $scan_proc->{prim_output_list} = [];
    $scan_proc->{output_list} = $scan_proc->{prim_output_list};

    &{$scan_proc->{start_page}}();
    &{$scan_proc->{start_document}}();

    log_message("\nbegin_page 3/3\n");
  }

sub end_page
  {
    log_message("\nend_page 1/2\n");

    &{$scan_proc->{close_document}}();
    &{$scan_proc->{close_page}}();

    # Add the directory of the source to the 'extern' ouput.
    my $target_root_dir =  get_target_root_dir();
    $scan_proc->{target_name} = compose_filename($scan_proc->{target_name}, $target_root_dir);

    write_output_to_file(compose_filename($scan_proc->{target_name}, $xml_root_dir));
    log_message("\nend_page 2/2\n");
  }


# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------


$lib_table->{page}->{initializer} = sub
  {
    my $page_env_table =
      {
       'page' =>
       {
        "num_opt_args" => 0,
        "num_man_args" => 1,
        "begin_function" => \&begin_page,
        "end_function" => \&end_page,
        "early_pre_begin_hook" => \&close_strucs,
        "early_pre_end_hook" => \&close_strucs,
       }
      };
    deploy_lib('page', {}, $page_env_table, FALSE, 'TOPLEVEL');
  };

return(TRUE);



