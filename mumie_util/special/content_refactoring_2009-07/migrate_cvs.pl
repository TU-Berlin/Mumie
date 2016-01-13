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

use Env qw(HOME);
use File::Basename;

sub find_tex_files
  {
    open(INPUT, "| find -name '*.src.tex'") or die("Failed to find MmTeX sources: $!\n");
    my @tokes = <INPUT>;
    close(INPUT);
    my $filename_table = {};
    foreach my $token (@_) {
      $token =~ s/^\s+|\s+$//g;
      if ( $token ) {
        my $basename = fileparse($token);
        $table->{$basename} = [] unless ( exists($table->{$basename}) );
        push(@{$table->{$basename}}, $token);
      }
    }
    return($filename_table);
  }

sub check_unique_basenames
  {
    my ($label, $filename_table) = @_;
    foreach my $basename (sort(keys(%{$filename_table)))) {
      my $count = scalar(@{$filename_table->{$basename}});
      if ( $count > 1 ) {
        print("WARNING: $basename occurs multiple times in $label:\n  ");
        print(join("\n  ", @{$filename_table->{$basename}}), "\n" );
      }
    }
  }

sub check_complete
  {
    my ($table1, $label1, $table2, $label2) = @_;
    foreach my $basename1 (sort(keys(%{$table1})))
      {
        if ( ! grep($basename1 eq $_, keys(%{$table2})) )
          { print("WARNING: $basename1 exists in $label1 but not in $label2\n"); }
      }
  }

sub check
  {
    chdir("$HOME/projects/$source_repository/checkin");
    my $source_tex_file_table = find_tex_files();

    chdir("$HOME/projects/$target_repository/checkin");
    my $target_tex_file_table = find_tex_files();

    check_unique_basenames("source", $source_tex_file_table);
    check_unique_basenames("target", $target_tex_file_table);

    check_complete($source_tex_file_table, "source", $target_tex_file_table, "target");
    check_complete($target_tex_file_table, "target", $source_tex_file_table, "source");
  }

sub migrate
  {
    chdir("$HOME/projects/$source_repository/checkin");
    my $source_tex_file_table = find_tex_files();

    chdir("$HOME/projects/$target_repository/checkin");
    my $target_tex_file_table = find_tex_files();

    chdir($cvs_root);
    foreach my $basename (sort(keys(%{$source_tex_file_table})))
      {
        print("$basename: ");
        my @sources = @{$source_tex_file_table->{$basename}};
        my @targets = @{$target_tex_file_table->{$basename}};
        if ( scalar(@sources) > 1 )
          {
            print("ignored (multiple source files)\n");
          }
        elsif ( scalar(@targets) > 1 )
          {
            print("ignored (multiple targets files)\n");
          }
        elsif ( scalar(@targets) == 0 )
          {
            print("ignored (no target file)\n");
          }
        else
          {
            my $source = @sources[0] . ",v"
            my $target = @targets[0] . ",v";

            system
              ('cp', '-v',
               $source_repository . '/checkin/' . @sources[0] . ",v",
               $target_repository . '/checkin/' . @targets[0] . ",v");
          }
      }
  }

$cvs_root = "/net/mumie/cvs";
$source_repository = "linear_algebra_content";
$target_repository = "content_tub_linear_algebra";
$checkout_root = "$HOME/projects"

$task = "migrate";
if ( @ARGV && $ARGV[0] eq "--check" )
  {
    $task = "check";
    shift;
  }
( @ARGV ) && die("Illegal paramter(s): ", join(" ", @ARGV), "\n");

if ( $task eq "migrate" )
  {
    migrate();
  }
elsif ( $task eq "check" )
  {
    check();
  }
