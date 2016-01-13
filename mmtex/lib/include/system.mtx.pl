package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: system.mtx.pl,v 1.8 2007/07/11 15:56:15 grudzin Exp $

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

log_message("\nLibrary \"system\" ", '$Revision: 1.8 $ ', "\n");

# --------------------------------------------------------------------------------
# Writing messages
# --------------------------------------------------------------------------------

sub execute_dump
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    my $data =
      get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', 'dump', 'data_arg');

    my $streams_descriptor =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD', 'dump', 'streams_arg')
       : 'stdout');

    my @stream_list = split(/\s*,\s*/, $streams_descriptor);

    foreach my $stream (@stream_list)
      {
	if ( $stream eq 'stdout' )
	  {
	    print(STDOUT $data);
	  }
	elsif ( $stream eq 'stderr' )
	  {
	    print(STDERR $data);
	  }
	elsif ( $stream eq 'log' )
	  {
	    log_message($data);
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}("Unknown output stream: $stream");
	  }
      }
  }

sub execute_message
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    my $message = $man_args->[0];

    $message =~ s/\\n/\n/g;
    $message =~ s/\\t/\t/g;

    my $streams_descriptor =
      ($opt_args->[0]
       ? get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD', 'message', 'message_arg')
       : 'stdout');

    my @stream_list = split(/\s*,\s*/, $streams_descriptor);

    foreach my $stream (@stream_list)
      {
	if ( $stream eq 'stdout' )
	  {
	    print(STDOUT $message);
	  }
	elsif ( $stream eq 'stderr' )
	  {
	    print(STDERR $message);
	  }
	elsif ( $stream eq 'log' )
	  {
	    log_message($message);
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}("Unknown output stream: $stream");
	  }
      }
  }

# --------------------------------------------------------------------------------
# Loading a library
# --------------------------------------------------------------------------------

sub execute_requirelib
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $lib_name =
      get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "requirelib", "name_arg");

    my $lib_otpions =
      ( $opt_args->[0]
	? get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD", "requirelib", "opt_arg")
	: "" );

    require_lib($lib_name, $lib_otpions, $scan_proc->{error_handler});
  }

# --------------------------------------------------------------------------------
# Installing commands and environments
# --------------------------------------------------------------------------------

sub execute_installcmds
  {
    log_message("\nexecute_installcmds 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    # Get commands or tag:
    my $cmds_arg =
      get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', 'installcmds', 'cmds_arg');
    my $prefix;
    if ( $cmds_arg =~ m/^(tag|cmds)=(.*)$/ )
      {
        $prefix = $1;
        $cmds_arg = $2;
      }
    else
      {
        $prefix = $1;
      }
    my $cmds_or_tag =
      ($prefix eq 'cmds' ? [split(/\s*,\s*/, $cmds_arg)] : $cmds_arg);

    # Get library name:
    my $lib =
      get_data_from_arg(1, $man_args, $pos_man_args, 'CMD', 'installcmds', 'lib_arg');

    # Get mode:
    my $mode = undef();
    if ( $opt_args->[0] )
      {
        my $mode_flag =
          get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD', 'installcmds', 'mode_arg');
        if ( ! ( $mode_flag eq 'o' || $mode_flag eq 'p' ) )
          {
            &{$scan_proc->{error_handler}}("Illegal mode flag: $mode_flag");
          }
        my $mode = {'o' => 'OVERWRITE', 'p' => 'PROTECT'}->{$mode_flag};
      }

    install_cmds($cmds_or_tag, $lib, $mode);

    log_message("\nexecute_installcmds 2/2\n");
  }


# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

# $import_cmd_tables->{system} = $system_cmd_table;


$lib_table->{system}->{initializer} = sub
  {
    my $system_cmd_table
      = {
         "message" =>
           {
            'num_opt_args' => 1,
            'num_man_args' => 1,
            'is_par_starter' => FALSE,
            'execute_function' => \&execute_message,
            'doc' =>
              {
               'man_args' => [['message', 'Text to output']],
               'opt_args' => [['streams', 'Streams to write to']],
               'description' =>
                 'Prints %{1} to the streams specified by %[1]. %[1] must be a comma-separated ' .
                 'list of "stdout", "stderr", or "log"; meaning the standard output stream, the ' .
                 'standard error stream, or the log file, respectively. Default is "stdout". ' .
                 '%{1} is not evaluated.' ,
               'see' => {'cmds' => ['dump']},
              }
           },
         "dump" =>
           {
            'num_opt_args' => 1,
            'num_man_args' => 1,
            'is_par_starter' => FALSE,
            'execute_function' => \&execute_dump,
            'doc' =>
              {
               'man_args' => [['data', 'Data to dump']],
               'opt_args' => [['streams', 'Streams to write to']],
               'description' =>
                 'Prints %{1} to the streams specified by %[1]. %[1] must be a comma-separated ' .
                 'list of "stdout", "stderr", or "log";  meaning the standard output stream, the ' .
                 'standard error stream, or the log file, respectively. Default is "stdout". ' .
                 '%{1} is evaluated before printed.' ,
               'see' => {'cmds' => ['message']},
              }
           },
         "requirelib" =>
           {
            'num_opt_args' => 1,
            'num_man_args' => 1,
            'is_par_starter' => FALSE,
            'execute_function' => \&execute_requirelib,
            'doc' =>
              {
               'man_args' => [['library', 'Name of the library']],
               'opt_args' => [['options', 'Options to path to the library']],
               'description' =>
                 'Loads library %{1} with options %[1]',
              }
           },
         "execute_installcmds" =>
           {
            'num_opt_args' => 1,
            'num_man_args' => 2,
            'is_par_starter' => FALSE,
            'execute_function' => \&execute_installcmds,
           },
         "usepackage" =>
           {
            'is_alias_for' => 'requirelib',
           },
        };

    my @system_cmds = keys(%{$system_cmd_table});

    deploy_lib
      (
       'system',
       $system_cmd_table,
       {},
       {
        'SYSTEM' => \@system_cmds,
        'TOPLEVEL' => \@system_cmds,
        'PREAMBLE' => ['dump', 'message', 'requirelib'],
       },
       FALSE
      );
  };

return(1);
