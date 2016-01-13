#! /usr/bin/perl -w

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

$input_file = $ARGV[0];

open(IN, $input_file) or die("Can not open file: $input_file: $!\n");
@input = <IN>;
close(IN);

for $line (@input)
  {
    $line =~ s/^\s+|\s+$//g;
    if ( $line !~ m/^#/ && $line ne "" )
      {
        ($id, $name) = split(/;/, $line);
	$sql_code = "SELECT sync_id,first_name,surname FROM users WHERE id IN (SELECT member FROM tutorial_members WHERE tutorial = $id);";
        $output_file = $name . '.csv';
	$output_file =~ s/\s/_/g;
	print("$output_file\n");
        system
          ('psql',
           '-U', 'japs',
           'mdb01',
           '--tuples-only',
           '--no-align',
           '--field-separator', ';',
           '--record-separator', "\n",
           '--command', $sql_code,
           '--output', $output_file);
      }
  }
