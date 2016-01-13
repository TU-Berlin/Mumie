package Mumie::MmTeX::IOHelper;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: IOHelper.pm,v 1.8 2009/11/16 14:08:01 gronau Exp $

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
# Helper funtions for input and output of mmttex.

use constant VERSION => '$Revision: 1.8 $';
use Cwd;
use File::Basename;
use File::Path;
use Mumie::Boolconst;
use Mumie::Scanner qw($scan_proc);
use Mumie::Logger qw(:ALL);
use Mumie::MmTeX::Serializer qw(write_output_to_file);

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL = qw
    (
     $log_root_dir
     $output_file
     $output_writer
     $source_root_dir
     $target_root_dir
     $up_to_date_message_text
     $up_to_date_tester
     $xml_root_dir
     compose_filename
     default_output_writer
     default_up_to_date_tester
     get_source_root_dir
     get_target_root_dir
     read_source
     start_logging
     stop_logging
    );
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
    );
}

sub init
  {
    $source_root_dir = FALSE;
    # If given (i.e., if true) source filenames are interpreted as relative paths to this
    # location.

    $xml_root_dir = FALSE;
    # If given (i.e., if true) xml output filenames are interpreted as relative paths to this
    # location.

    $output_file = FALSE;
    # If given (i.e., if true) the filename of the XML output. Usually set by the --output
    # command line option.

    $target_root_dir = FALSE;
    # If given (i.e., if true) final output filenames are interpreted as relative paths to
    # this location. Final output files are the files created from the xml documents, usually
    # HTML or XHTML files.

    $log_root_dir = FALSE;
    # If given (i.e., if true) log filenames are interpreted as relative paths to this
    # location.

    @href_prefixes = ('normal:', 'mmtex:', 'css:', 'mailto:');
    # Hyper reference prefixes used internally by mmtex.

    @href_protocols = ('http://', 'file://');
    # Hyper reference prefixes that represent protocols

    $output_writer = \&default_output_writer;
    # Global reference to the output writer to use.

    $up_to_date_tester = \&default_up_to_date_tester;
    # Global reference to the up-to-date tester to use

    $up_to_date_message_text = "Target is up to date.\n";
    # *** DEPRECATED ***

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub compose_filename
  # ($filename, $base)
  # Constructs a filename from $base, a root directory, and $filename, a path to a file. If
  # $base is true and $filename does not start with a slash, the filename returned is
  # $base/$filename, otherwise, $filename is returned unchanged. Whitespaces at the
  # beginning or end of $base and $filename are removed. $base may or may not end with a
  # slash.
  {
    my ($filename, $base) = @_;
    $filename =~ s/^\s+|\s+$//g;
    $base =~ s/^\s+|\/\s*$//g;
    my $composed_filename;
    if ( ( $filename !~ m/^\/|[a-zA-Z]+:\/\// ) && ( $base ) )
      {
	$composed_filename = "$base/$filename";
      }
    else
      {
	$composed_filename = $filename
      }
    return($composed_filename);
  }

sub get_source_root_dir
  #a ()
  # Returns the root directory of the source. If $source_root_dir is true, $source_root_dir
  # is returned; otherwise, the directory part of $scan_proc->{prim_source_name} (i.e., the
  # part up to the last "/") is returned.
  {
    my $_source_root_dir;
    if ( $source_root_dir )
      {
	$_source_root_dir = $source_root_dir;
      }
    else
      {
	$scan_proc->{prim_source_name} =~ m/[^\/]*$/;
	$_source_root_dir = $`;
      }
    return($_source_root_dir);
  }

sub get_target_root_dir
  #a ()
  # Returns the root directory of the target. If $output_filename is true, the directory
  # part of $output_filename is returned, otherwise, the directory part of 
  # $scan_proc->{prim_source_name} (i.e., the part up to the last "/") is returned.
  {
    my $_target_root_dir;

    if ( $output_file )
      {
	$output_file =~ m/[^\/]*$/;
	$_target_root_dir = $`;
      }
    else
      {
	$scan_proc->{prim_source_name} =~ m/[^\/]*$/;
	$_target_root_dir = $`;
      }
    return($_target_root_dir);
  }

sub compose_output_location
  #a ($source_file, , $_output_file, [$output_suffix])
  # Composes the "output location", which is the absloute filename of the output, taking into
  # account settings like $source_root_dir and $xml_root_dir. Works as follows:
  #
  # If $xml_root_dir is set and $_output_file is relative, returns the concatenation of the
  # both. If $_output_file is not set, it is set to the global variable $output_file before.
  # If the latter does not exist either, $_output_file is constructed from $source_file in the
  # following steps:
  #
  # 1. A variable $_source_root_dir is constructed as follows: If the global variable
  # $source_root_dir is set, $_source_root_dir is set to $source_root_dir; otherwise,
  # $_source_root_dir is set to the current working direktory. If $_source_root_dir is
  # not absolute (i.e., does not start with a slash), the current working directory is
  # prepended.
  #
  # 2. If $source_file starts with $_source_root_dir, the latter is removed from the beginning
  # of $source_file. If that results in a filename beginning with a slash, the slash is removed.
  #
  # 3. If the global variable $xml_root_dir is set and $source_file is relative (i.e., does not
  # start with a slash), $xml_root_dir is prepended to $source_file, separated by exactly one
  # slash.
  #
  # 4. The suffix of $source_file is replaced by $output_suffix
  #
  # REMARK/TODO: Currently, "is set" means "evaluates to a true value" (with $source_root_dir and
  # $xml_root_dir). This should be changed in the future).
  {
    my ($source_file, $_output_file, $output_suffix) = @_;
    $output_suffix ||= 'xml';
    $output_file ||= $output_file;

    # Construct $_output_file from $source_file if necessary:
    unless ( $_output_file )
      {
        $_output_file = $source_file;

        # Construct the variable $_source_root_dir:
        my $_source_root_dir = ( $source_root_dir || cwd() );
        # Prepend current working dir if not absolute:
        if ( $_source_root_dir !~ m/^\// )
          {
            my $current_dir = cwd();
            $current_dir =~ s/\/$//; # (Remove trailing slash, if any)
            $_source_root_dir = $current_dir . '/' . $_source_root_dir;
          }
        $_source_root_dir =~ s/\/$//; # (Remove trailing slash, if any)

        # If $_output_file starts with $_source_root_dir, remove it from $_output_file:
        if ( $_output_file =~ m/^$_source_root_dir\/(.*)$/ )
          {
            $_output_file = $1;
            $_output_file =~ s/^\///;
          }

        # Remove suffix:
        $_output_file =~ s/\.[^.]+$//;

        # If the 'multilang' flag is set, add the language code:
        $_output_file .= '_' . $scan_proc->{lang} if ( $scan_proc->{multilang} );

        # Add suffix:
        $_output_file .= ".$output_suffix";
      }

    # If $xml_root_dir is set and $_output_file is relative, prepend $_output_file with
    # $xml_root_dir:
    if ( $xml_root_dir && $_output_file !~ m/^\// )
      {
        my $_xml_root_dir = $xml_root_dir;
        $_xml_root_dir =~ s/\/$//; # (Remove trailing slash, if any)
        $_output_file = $_xml_root_dir . '/' . $_output_file;
      }

    return($_output_file);
  }

sub default_output_writer
  #a ($source_file, $_output_file)
  # Writes output. The output location is constructed by applying compose_output_location()
  # to the arguments.
  {
    my ($source_file, $_output_file) = @_;

    # Get output location:
    my $output_location = compose_output_location($source_file, $_output_file);

    # Create output dir if necessary:
    my $output_dir = (fileparse($output_location))[1];
    mkpath($output_dir) unless ( -e $output_dir );

    # Write output:
    write_output_to_file($output_location);
  }

sub default_up_to_date_tester
  #a ($source_file, $_output_file)
  # Returns TRUE if the target of $source_file exists and is newer then $source_file, otherwise
  # FALSE. The target filename is contructed by compose_output_location() to the arguments.
  {
    my ($source_file, $_output_file) = @_;
    my $source_location = compose_filename($source_file, $source_root_dir);
    my $output_location = compose_output_location($source_file, $_output_file);
    my $up_to_date = FALSE;
    if ( -e $output_location )
      {
	my $source_atime = (stat($source_location))[9];
	my $output_atime = (stat($output_location))[9];
 	$up_to_date = TRUE if ( $output_atime > $source_atime );
      }
    return($up_to_date);
  }

sub read_source
  {
    my $source_file = $_[0];
    my $source_location = compose_filename($source_file, $source_root_dir);
    # start windows workaround
  	if ($^O eq "MSWin32" || $^O eq "MSWin64") {
      my $dir = Mumie::File::get_working_dir();
      $dir = Win32::GetShortPathName($dir);
      $source_location = compose_filename($source_file, $dir);
    }
    # end windows workaround
    open(SOURCE, $source_location) or die("Can't open file $source_location\n");
    $source = join("", <SOURCE>);
    close(SOURCE);
    return(\$source);
  }

sub start_logging
  #a ($source_file, $_log_file)
  {
    return() unless ( $write_log );
    my ($source_file, $_log_file) = @_;
    $_log_file ||= $log_file;
    if (not $log_file )
      {
	$log_file = $source_file;
	$log_file =~ s/\..+$/.log/;
      }
    $log_location = compose_filename($log_file, $log_root_dir);
    open_log_file($log_location);
  }

sub stop_logging
  #a ($message)
  {
    return() unless ( $write_log );
    log_message($message) if ( $message );
    close_log_file();
  }

init();
return(1);
