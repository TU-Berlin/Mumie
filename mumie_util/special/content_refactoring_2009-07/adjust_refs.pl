#!/usr/bin/perl -w

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

sub read_file
  {
    my $filename = $_[0];
    open(INPUT, $filename) or die("Can't open file $filename for reading: $!\n");
    my $input = join('', <INPUT>);
    close(INPUT);
    return($input);
  }

sub write_file
  {
    my ($output, $filename) = @_;
    open(OUTPUT, ">$filename") or die("Can't open file $filename for writing: $!\n");
    print(OUTPUT $output);
    close(OUTPUT);
  }

sub get_new_path
  {
    my ($old_path) = @_;
    # TODO: Implement real mapping
    return("NEW_" . $old_path);
  }

sub process_tex_source
  {
    my ($filename) = @_;
    my $source = read_file($filename);
     $source =~ s/\\component{([^}]*)}{([^}]*)}{([^}]*)}/"\\component{$1}{" . get_new_path($2) . "}{$3}";/meg;
     $source =~ s/\\link{([^}]*)}{([^}]*)}{([^}]*)}/"\\link{$1}{" . get_new_path($2) . "}{$3}";/meg;
     $source =~ s/\\corrector{([^}]*)}/"\\corrector{" . get_new_path($1) . "}";/meg;
     $source =~ s/\\generic{([^}]*)}/"\\generic{" . get_new_path($1) . "}";/meg;
     $source =~ s/\\creategeneric\[([^}]*)\]/"\\creategeneric[" . get_new_path($1) . "]";/meg;
     $source =~ s/\\class{([^}]*)}/"\\class{" . get_new_path($1) . "}";/meg;
     $source =~ s/\\genericsummary{([^}]*)}/"\\genericsummary{" . get_new_path($1) . "}";/meg;
     $source =~ s/\\compcsc{([^}]*)}{([^}]*)}{([^}]*)}/"\\compcsc{" . get_new_path($1) . "}{$2}{$3}";/meg;
     $source =~ s/\\compwks{([^}]*)}{([^}]*)}{([^}]*)}/"\\compwks{" . get_new_path($1) . "}{$2}{$3}";/meg;
     $source =~ s/\\compgelm{([^}]*)}{([^}]*)}{([^}]*)}/"\\compgelm{" . get_new_path($1) . "}{$2}{$3}";/meg;
     $source =~ s/\\compgsel{([^}]*)}{([^}]*)}{([^}]*)}/"\\compgsel{" . get_new_path($1) . "}{$2}{$3}";/meg;
     $source =~ s/\\compgprb{([^}]*)}{([^}]*)}{([^}]*)}/"\\compgprb{" . get_new_path($1) . "}{$2}{$3}";/meg;
     write_file($source, $filename);
  }

sub process_java_source
  {
    my ($filename) = @_;
    my $source = read_file($filename);
    $source =~ s/\@mm\.(section|infoPage|thumbnail|requireApplet|requireJar)\s*(\S+)/
      "\@mm.$1 " . get_new_path($2)/meg;
    write_file($source, $filename);
  }

sub filter_filenames
  {
    my @filenames = ();
    foreach my $token (@_)
      {
	$token =~ s/^\s+|\s+$//g;
	push(@filenames, $token) if ( $token );
      }
    return(@filenames);
  }

$| = 1;

# Get files to process:
@filenames = @ARGV;
if ( ! @filenames )
  {
    print("Reading filenames from stdin ... ");
    @filenames = <STDIN>;
    @filenames = filter_filenames(@filenames);
    print("done\n");
  }

foreach $filename (@filenames)
  {
    print("$filename\n");
    my $status = 1;
    print("$filename ... ");
    if ( $filename =~ m/\.src\.tex$/ )
      { process_tex_source($filename) }
    elsif ( $filename =~ m/\.java$/ )
      { process_java_source($filename) }
    else
      { $status = 0 }
    print($status ? "ok\n" : "skipped\n");
  }
