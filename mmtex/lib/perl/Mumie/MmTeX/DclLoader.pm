package Mumie::MmTeX::DclLoader;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: DclLoader.pm,v 1.5 2007/07/11 15:56:15 grudzin Exp $

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

# ------------------------------------------------------------------------------------------
#1 Description
# ------------------------------------------------------------------------------------------
#
#

use Mumie::Boolconst;
use Mumie::Logger qw(/.+/);
use Mumie::File qw(/.+/);
use Mumie::List qw(/.+/);
use Mumie::Scanner qw($scan_proc);
use Mumie::Hooks qw(/.+/);

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL = qw
    (
     $cml_dcl_options
     $dcl_option_table
     $dcl_table
     $dcl_path
     init_dcl
     load_document_class
    );
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
    );
}

sub init
  {
    $dcl_path = FALSE;
    # Colon-separated list of directories in which the document class files are searched.

    $dcl_option_table = {};
    # The document class option table. Contains the options in key-value pair form.

    $cml_dcl_options = "";
    # Variable to store the document class options set via the command line

    # Setting "init done" flag
    $init_done = 1;

    # The document class table (see main documentation).
    if ( $dcl_table )
      {
	# Cleanup dcl_table, just keep the initializers.
	my $_dcl_table = {};
	foreach $dcl_name (keys %{$dcl_table})
	  {
	    $_dcl_table->{$dcl_name}->{initializer} = $dcl_table->{$dcl_name}->{initializer};
	  }
	$dcl_table = $_dcl_table;
	undef($_dcl_table);
      }
    else
      {
	$dcl_table = {};
      }
  }

sub reset_init
  {
    $init_done = 0;
  }

# ------------------------------------------------------------------------------------------
#  The document class loading
# ------------------------------------------------------------------------------------------

sub load_document_class
  {
    my ($dcl_name, $error_handler) = @_;
    $error_handler ||= sub { die(@_) };

    my $dcl_filename = $dcl_name;
    $dcl_filename =~ s/\./\//g;
    $dcl_filename = "$dcl_filename.dcl.pl";
    my @dcl_path_list = split(/:/, $dcl_path);

    my $dcl_full_filename = find_in_path($dcl_filename, \@dcl_path_list);

    if (! $dcl_full_filename )
      {
	&{$error_handler}("Failed to resolve documentclass: $dcl_name\n",
			  "File not found in dcl search path: $dcl_filename\n");
      }

    log_message("\nload_document_class 1/2\n");
    log_data("Class name", $dcl_name, "Filename", $dcl_full_filename);

    {
      package Mumie::MmTeX::Converter;
      require($dcl_full_filename)
	or &{$error_handler}("Failed to load documentclass: $dcl_name\n$!\n");
    }

    init_dcl($dcl_name, $error_handler);
    # Possible place for the init functions ??? CHR

    log_message("\nload_document_class 2/2\n");
  }

sub init_dcl
  #a ($name, $error_handler)
  # Initializes dcl $name by calling its initializer, i.e., $dcl_table->{$name}->{initializer}.
  # The latter is called with one argument, $error_handler, which is the error handler the
  # initializer should use.
  {
    my ($name, $error_handler) = @_;
    log_message("\ninit_dcl 1/2\n");
    my $initialized = $dcl_table->{$name}->{initialized};
    log_data('Dcl', $name, 'Initialized', $initialized);
    unless ( $initialized )
       {
	 run_hook($dcl_table->{$name}->{initializer}, $error_handler);
	 $dcl_table->{$name}->{initialized} = TRUE;
       }
    log_message("\ninit_dcl 2/2\n");
  }

init();
return(1);
