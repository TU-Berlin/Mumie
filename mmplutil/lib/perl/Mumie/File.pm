package Mumie::File;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: File.pm,v 1.5 2009/11/16 14:10:13 gronau Exp $

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

use File::Find;
use File::Copy;
use File::Basename;
use File::Path;
use File::Spec;
use Mumie::Boolconst;

require Exporter;

@ISA = qw(Exporter);

{
  my @_READ_WRITE =
    (
     'read_file',
     'write_file',
    );
  my @_MODIFY =
    (
     'replace_in_file',
    );
  my @_COPY =
    (
     'copy_with_path_to_dir',
    );
  my @_SELECT =
    (
     'select_files',
     'find_in_path',
    );
  my @_DIR =
    (
     'get_dir_contents',
     'get_working_dir',
    );
  my @_MISC =
    (
     'mtime',
     'get_rel_filename',
     'compact_filename',
     'create_file_dir_if_necessary',
    );

  my @_ALL =
    (
     @_READ_WRITE,
     @_MODIFY,
     @_COPY,
     @_SELECT,
     @_DIR,
     @_MISC,
     '$default_dir_creation_mask',
    );

  @EXPORT_OK = @_ALL;

  %EXPORT_TAGS
    = (
       'ALL' => \@_ALL,
       'READ_WRITE' => \@READ_WRITE,
       'MODIFY' => \@_MODIFY,
       'COPY' => \@_COPY,
       'SELECT' => \@_SELECT,
       'DIR' => \@_DIR,
       'MISC' => \@_MISC,
       'MAIN' => \@_MAIN,
      );
}

$_THIS_NAME = 'Mumie::File';
  # Name of this module.


# --------------------------------------------------------------------------------
#1 The init function
# --------------------------------------------------------------------------------

sub init
  {
    return() if ( $init_done );

    $default_dir_creation_mask = 0777;
    # Default mode mask for newly created directories. Defaults to 0777.

    $use_dot_dir = TRUE;
    # Use .dir to find current working directory.

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }


# --------------------------------------------------------------------------------
#1 Reading and writing file contents
# --------------------------------------------------------------------------------

sub read_file
  #a ($filename)
  # Reads file $filename and returns a reference to its content.
  {
    my $filename = $_[0];
    open(SOURCE, $filename) or die("Can't open file $filename for reading: $!\n");
    my $source = join('', <SOURCE>);
    close(SOURCE);
    return(\$source);
  }

sub write_file
  #a ($str_or_ref, $filename)
  # Writes a string to a file. The string is given by $str_or_ref, which may be the sting
  # itself or a reference to it. The file is given by $filename.
  {
    my ($str_or_ref, $filename) = @_;
    my $content = (ref($str_or_ref) ? ${$str_or_ref} : $str_or_ref);
    open(TARGET, ">$filename") or die("Can't open file $filename for writing: $!\n");
    print(TARGET $content);
    close(TARGET);
  }

# --------------------------------------------------------------------------------
#1 Modifying file contents
# --------------------------------------------------------------------------------

sub replace_in_file
  #a ($source_filename, $replacements, $target_filename)
  # Performs regular expression search-and-replace operations in file $source_filename, and
  # writes the result to file $target_filename. $target_filename defaults to
  # $source_filename. The search-and-replace operations are specified by $replacements,
  # which must be a reference to a hash mapping regular expressions to the replacement texts.
  {
    my ($source_filename, $replacements, $target_filename) = @_;
    $target_filename ||= $source_filename;
    my $source = read_file($source_filename);
    foreach my $pattern (keys(%{$replacements}))
      {
	my $replacement = $replacements->{$pattern};
	${$source} =~ s/$pattern/$replacement/gm;
      }

    write_file($source, $target_filename);
  }

# --------------------------------------------------------------------------------
#1 Copying and moving files
# --------------------------------------------------------------------------------

sub copy_with_path_to_dir
  #a ($filename_with_path, $dir)
  # Copies $filename_with_path including path to directory $dir. For example,
  #c
  #   copy_with_path_to_dir('foo/bar/baz.txt', 'bak');
  #/c
  # copies 'foo/bar/baz.txt' to 'bak/foo/bar/baz.txt'.
  {
    my ($filename_with_path, $dir) = @_;
    my $error_prefix = $_THIS_NAME . '::copy_with_path_to_dir:';
    if ( ! -e $filename_with_path )
      { die("$error_prefix File does not exist: $filename_with_path\n") };
    if ( ! -d $dir )
      { die("$error_prefix Not a directory: $dir\n") };
    my ($filename, $path) = fileparse($filename_with_path);
    my $dest_path = File::Spec->catdir($dir, $path);
    mkpath($dest_path) unless ( -e $dest_path );
    copy($filename_with_path, $dest_path) or error("$error_prefix $!\n");
  }

# --------------------------------------------------------------------------------
#1 Creating the directory of a file
# --------------------------------------------------------------------------------

#Ds
#a ($file)
# Creates the directory of $file if it does not exist.

sub create_file_dir_if_necessary
  {
    my $file = $_[0];
    my $dir = (fileparse($file))[1];
    mkpath($dir) if ( $dir && ( ! -e $dir ) );
  }

# --------------------------------------------------------------------------------
#1 Selecting files
# --------------------------------------------------------------------------------

sub select_files
  #a ($from, $selector_or_regexp)
  # Selects files recursivly from one or more root directories. The directories are given by
  # $from, which may be a single directory name, or a reference to a list of directory
  # names. These directories are searched recursivly for files matching a condition
  # specified by $selector_or_regexp. $selector_or_regexp may be either a regular expression
  # the filename must match, or a reference to a function. In the latter case, the function
  # must return a true value for the file to be selected. The function is called with no
  # argument, but it can get the filename from the variable $File::Find::name.
  {
    my ($from, $selector_or_regexp) = @_;
    my @root_dirs = ( ref($from) ? @{$from} : ($from) );
    my $selector;
    if ( $selector_or_regexp )
      {
	if ( ref($selector_or_regexp) )
	  {
	    $selector = $selector_or_regexp;
	  }
	else
	  {
	    my $regexp = $selector_or_regexp;
	    $selector = sub { return($File::Find::name =~ m/$regexp/) };
	  }
      }
    else
      {
	$selector = sub { return(TRUE) };
      }
    my @files = ();
    find( sub { push(@files, $File::Find::name) if ( &{$selector}() ) }, @root_dirs);
    return(@files);
  }

sub find_in_path
  # ($file_name, $path)
  # Searches in $path, which must be a reference to a list of directories, for file $filename.
  # If succeeds, returns the full name of the file, otherwise, returns the undefined value.
  {
    my $filename = shift();
    my @path = @{shift()};
    my $dir;
    my $full_name;
    my $found = FALSE;
    while ( @path && !$found )
      {
	$dir = shift(@path);
	$dir =~ s/\/\s*$//;
	$full_name = $dir . '/' . $filename;
	$found = -e $full_name;
      }
    return($found ? $full_name : undef());
  }

# --------------------------------------------------------------------------------
#1 Directories
# --------------------------------------------------------------------------------

sub get_dir_contents
  #a ($dir_name)
  # Returns the contents of the directory $dir.
  {
    my $dir = $_[0];
    opendir(GET_DIR_CONTENTS, $dir) or die("Mumie::File::get_dir_contents: $!\n");
    my @contents = readdir(GET_DIR_CONTENTS);
    closedir(GET_DIR_CONTENTS);
    return(@contents);
  }

sub get_working_dir
  #a ()
  # Returns the current working directory.
  {
  	my $dir;
    # windows workaround
  	if ($^O eq "MSWin32" || $^O eq "MSWin64") {
    	$dir = ( $use_dot_dir && -e '.dir' ? `cat .dir` : `chdir`);
    	$dir =~ s/\\/\//g;
    	# $dir = "//?/".$dir
  	}
  	# all other operating systems
  	else {
    	$dir = ( $use_dot_dir && -e '.dir' ? `cat .dir` : `pwd`);
  	}
    $dir =~ s/^\s+|\s+$//g;
    return($dir);
  }

# --------------------------------------------------------------------------------
#1 Misc.
# --------------------------------------------------------------------------------

sub mtime
  #a ($filename)
  # Returns the last modification time of $filename.
  {
    my ($filename) = @_;
    return((stat($filename))[9]);
  }

sub get_rel_filename
  # ($filename, $path)
  # Returns the part of $filename relative to $path. $path defaults to the current
  # directory.
  {
    my ($filename, $path) = @_;
    $path ||= `pwd`;
    $path =~ s/^\s+|\s+$//;
    $filename =~ s/^$path\/?//;
    return($filename);
  }

sub compact_filename
  # ($filename)

  # Resolves ".." and "." parts in $filename and returns the result. Example:
  #c
  #  compact_filename("foo/bar/../bazz")
  #/c
  # returns
  #c
  #  "foo/bazz"
  #/c
  {
    my ($filename) = @_;
    my @old_parts = split(/\//, $filename);
    my @new_parts = ();
    foreach my $part (@old_parts)
      {
        if ( $part eq '..' )
          {
            die("Mailformed filename: $filename\n") if ( scalar(@new_parts) == 0 );
            pop(@new_parts);
          }
        elsif ( $part eq '.' )
          {
            # do nothing
          }
        else
          {
            push(@new_parts, $part);
          }
      }
    return(join('/', @new_parts));
  }


init();
return(1);
