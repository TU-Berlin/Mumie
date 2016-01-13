package Mumie::ProgressBar;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: ProgressBar.pm,v 1.2 2007/07/11 15:58:55 grudzin Exp $

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

require Exporter;

@ISA = qw(Exporter);

@EXPORT_OK = qw
  (
   $progress_bar_size
   $progress_bar_done_char
   $progress_bar_remain_char
   $progress_bar_left_delimiter
   $progress_bar_right_delimiter
   $progress_threshold
   $progress_bar_displayed
   progress_bar
   reset_progress_bar
   display_progress_bar
   display_progress_bar_in_emacs
   get_progress_bar_eraser
   erase_progress_bar
  );

sub init
  {
    return() if ( $init_done );

    $progress_bar_size = 50;
    $progress_bar_done_char = ".";
    $progress_bar_remain_char = " ";
    $progress_bar_left_delimiter = "[";
    $progress_bar_right_delimiter = "]";
    $last_progress = 0;
    $last_prefix = undef();
    $progress_threshold = 0.01;
    $progress_bar_displayed = 0;

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub progress_bar
  {
    my $progress = $_[0];
    my $done = int($progress * $progress_bar_size);
    my $remain = $progress_bar_size - $done;
    my $bar = ($progress_bar_done_char x $done)
              . ($progress_bar_remain_char x $remain);
    return $bar
  }

sub reset_progress_bar
  {
    my $start_progress = ($_[0] || 0);
    $last_progress = $start_progress;
  }

sub display_progress_bar
  {
    my ($progress, $prefix, $display_percent) = @_;
    if ( ( ! $last_progress ) || ( ($progress - $last_progress) >= $progress_threshold ) )
      {
	my $bar = ( $prefix ? $prefix : "")
	        . $progress_bar_left_delimiter
		. progress_bar($progress)
                . $progress_bar_right_delimiter
	        . ( $display_percent ? (" " . int($progress * 100) . "%") : "");
	print("\r$bar");
	$progress_bar_displayed = 1;
      }
    $last_progress = $progress;
    $last_prefix = $prefix;
  }

sub display_progress_bar_in_emacs
  {
    my ($progress, $prefix) = @_;
    if ( ( ! $last_progress ) || ( ($progress - $last_progress) >= $progress_threshold ) )
      {
	system('gnuclient', '-eval', "(display-progress-bar $progress $prefix)");
      }
    $last_progress = $progress;
    $last_prefix = $prefix;
  }

sub get_progress_bar_eraser
  {
    return("\r" .
	   (' ' x (($last_prefix ? length($last_prefix) : 0) +
		   length($progress_bar_left_delimiter) + $progress_bar_size +
		   length($progress_bar_right_delimiter))));
  }

sub erase_progress_bar
  {
    print(get_progress_bar_eraser());
    $progress_bar_displayed = 0;
  }

$| = 1;

init();
return(1);

