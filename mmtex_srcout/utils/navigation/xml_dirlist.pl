#!/usr/bin/perl

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

#------------------------------------------------------------------------#
#   Make sure there is the XML:Directory modul in the path entered below
#------------------------------------------------------------------------#
use lib "/net/multimedia/Perl/";
use XML::Directory::String;
use Getopt::Long;


my $directory_to_parse = '';
my $target_file = '';
my $overwrite = '';


GetOptions
  (
   'overwrite' => \$overwrite,
   'start-directory=s' => \&check_start_directory,
   'target-file=s' => \$target_file,
  );


sub check_start_directory
  {
    my ($key, $string) = @_;

    if (not $string) {die ("You have to provide a start-directory in the following way: --start-directory=foo/bar");}
    if (-r $string)
      {
	if (-d $string)	 {$directory_to_parse = $string;}
	else	         {die("Given string for start-directory is not a directory: $string \n");}
      }
    else
      {
	die("Given directory name is not readable: $string");
      }
  }



# Check the $target_file value..

if ($target_file !~ m/\.xml$/) 
  {
    die ("You have to provide a target-file in the following way: --target-file=xxx/yyy/zzz.xml");
  }

if (-e $target_file and not $overwrite)
  {
    die("File already exists, please use the '--overwrite' option to skip this error");
  }
else
  {
    open (TEST, ">$target_file") || die("Could not open target for writing: $target_file");
    close TEST;
    system("rm $target_file");
  }


# Die if one of them not set..
if (not $directory_to_parse)
  {
    die("\nNo starting directory specified, please use the '--start-directory=foo/bar' option.\n\n");
  }
if (not $target_file)
  {
    die("\nNo target_file specified, please use the '--target-file=foo/bar/abc.xml' option.\n\n");
  }


#------------------------------------------------------------------------#
#   Parse the directory and create a xml file..
#------------------------------------------------------------------------#
print "\n\nCreating XML Index of $directory_to_parse, please be patient...\n";
open (OUT, ">$target_file") || die ("Unknown error when writing to $target_file");

$dir = XML::Directory::String->new("$directory_to_parse",2,99);
$dir->parse_dir;
@res = $dir->get_array;
foreach (@res)
  {
    print OUT $_ . "\n";
  }
close OUT;
print "Ready, file stored in $target_file\n";


