#!/usr/bin/perl -w
# -*- perl -*-

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: mmxalan.tpl,v 1.1 2006/06/13 21:52:42 rassy Exp $

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

use constant PROGRAM => "mmxalan";
use constant VERSION => '$Revision: 1.1 $ (cvs version)';
use Env qw(HOME PATH MMTEX_HOME CLASSPATH XALAN_HOME);
use Getopt::Long;

BEGIN
  {
    $MMTEX_PREFIX ||= '@mmtex-prefix@';
  }

use lib "$MMTEX_PREFIX/lib/perl";
use Mumie::Boolconst;


# ------------------------------------------------------------------------------------------
# Auxilliary Functions
# ------------------------------------------------------------------------------------------

sub get_output_file
  # ($input_file)
  # Transforms $input_file into an output filename by replacing the suffix ".xml" by
  # $output_suffix. If $input_file doesn't ends with ".xml", the program exits with an
  # error message.
  {
    my $input_file = $_[0];
    if ( $input_file =~ m/\.xml$/ )
      {
	my $output_file = $input_file;
	$output_file =~ s/\.xml$/.$output_suffix/;
	return($output_file);
      }
    else
      {
	die("Can't create output filename from \"$input_file\"\n");
      }
  }

sub get_xalan_cmdline
  # ()
  # Returns the command line to call Xalan as a list of strings.
  {
    my @cmdline;
    push(@cmdline, "java");
    push(@cmdline, "-classpath", $classpath) if ( $classpath );
    push(@cmdline, "org.apache.xalan.xslt.Process", "-in", $input_file, "-out", $output_file);
    push(@cmdline, "-xsl", $xsl_stylesheet) if ( $xsl_stylesheet );
    foreach my $name (keys(%xsl_params))
      {
	my $value = $xsl_params{$name};
	push(@cmdline, "-param", $name, $value);
      }
    return(@cmdline);
  }

# ------------------------------------------------------------------------------------------
# Tasks
# ------------------------------------------------------------------------------------------

sub transform
  {
    # Reading the input filename from the command line
    if ( scalar(@ARGV) > 0 )
      {
	$input_file = shift(@ARGV);
	if ( scalar(@ARGV) > 0 )
	  {
	    die("Extra arguments: ", join(' ', @ARGV), "\n");
	  }
      }
    else
      {
	die("Missing input file");
      }

    # Constructing the output filename if not already specified
    unless ( $output_file )
      {
	$output_file = get_output_file($input_file);
      }

    # Perform the xalan call
    exec(get_xalan_cmdline());
  }

sub show_help
  {
    print(
"Usage:
  " . PROGRAM ." [OPTIONS] SOURCE
Options:
  --append-classpath=PATH      Append PATH to the classpath
  --help                       Print this message and quit
  --output=TARGET              Write output to file TARGET
  --output-suffix=SUFFIX       Set output filename suffix to SUFFIX
  --param NAME=VALUE           Set stylesheet parameter NAME to VALUE
  --prepend-classpath=PATH     Prepend PATH to the classpath
  --show-classpath             Print classpath and exit
  --stylesheet=STYLESHEET      Use xsl stylesheet STYLESHEET
  --version                    Print version information and quit
  --xsl-stylesheet=STYLESHEET  Same as --stylesheet=STYLESHEET
");
  }

sub show_version
  {
    print(PROGRAM . " " . VERSION . "\n");
  }

sub show_classpath
  {
    print($classpath, "\n");
  }



# ------------------------------------------------------------------------------------------
# Global variabes
# ------------------------------------------------------------------------------------------

$config_file = "$MMTEX_PREFIX/" . PROGRAM . "rc";
$user_config_file = "$HOME/." . PROGRAM . "rc";
if ( $XALAN_HOME )
  {
    $classpath
      = join(
	 ":",
	 "$XALAN_HOME/bin/xalan.jar",
	 "$XALAN_HOME/bin/xml-apis.jar",
	 "$XALAN_HOME/bin/xerces.jar"
	);
  }
else
  { $classpath = ""; }
$prepend_classpath = FALSE;
$append_classpath = FALSE;
$input_file = FALSE;
$output_file = FALSE;
$output_suffix = "xhtml";
$xsl_stylesheet = FALSE;
%xsl_params = ();
$task = \&transform;
$show_version = FALSE;
$show_help = FALSE;



# ------------------------------------------------------------------------------------------
# Main program
# ------------------------------------------------------------------------------------------

if ( -r $config_file )
  {
    require($config_file);
  }

if ( -r $user_config_file )
  {
    require($user_config_file);
  }

GetOptions
  (
   'output|o=s' => \$output_file,
   'output-suffix|x=s' => \$output_suffix,
   'stylesheet|xsl-stylesheet|s=s' => \$xsl_stylesheet,
   'version|v' => sub { $task = \&show_version },
   'help|h' => sub { $task = \&show_help },
   'show-classpath' => sub { $task = \&show_classpath },
   'param|p=s' => \%xsl_params,
   'prepend-classpath=s' => \$prepend_classpath,
   'append-classpath=s' => \$append_classpath,
  )
  or exit($!);

if ( $prepend_classpath )
  {
    $classpath = ":$classpath" if ( $classpath );
    $classpath = $prepend_classpath . $classpath;
  }

if ( $append_classpath )
  {
    $classpath = "$classpath:" if ( $classpath );
    $classpath = $classpath . ":" . $append_classpath;
  }

&{$task}();
