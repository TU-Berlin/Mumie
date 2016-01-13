package Mumie::XML::Writer;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Writer.pm,v 1.5 2007/07/11 15:58:55 grudzin Exp $

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
# Description
# --------------------------------------------------------------------------------
#
# A module to pretty-print XML code

use Mumie::Boolconst;
use Mumie::Text qw(/.+/);

require Exporter;

@ISA = qw(Exporter);
@EXPORT_OK = qw
  (
   write_xml_pcdata
   write_xml_start_tag
   write_xml_end_tag
   write_empty_xml_element
   write_xml_decl
   write_xml_doctype_tag
   write_xml_pr_instr
   write_xml_comment
   write_xml_start_comment
   write_xml_end_comment
   write_xml
   set_xml_namespace
   reset_xml_writer
   @xml_output_list
   $xml_last_termination
   $xml_column
   $xml_max_column
   $xml_indent
   $xml_indent_step
   $xml_writer_error_handler
   $xml_namespace
   $xml_namespace_prefix
   $xml_namespace_initialized
  );


# --------------------------------------------------------------------------------
# Description
# --------------------------------------------------------------------------------
#
# Provides functions for prettily formatted XML output.


# --------------------------------------------------------------------------------
#1 The init function
# --------------------------------------------------------------------------------
sub init
  {
    return () if ( $init_done );

    @xml_output_list = ();
    # List of strings containing the output;

    $xml_last_termination = "";
    # Stores the information how the last text terminated. Possible values are
    # \code{"WHITESPACE"}, \code{"NON_WHITESPACE"}, and \code{"CLOSED"}.

    $xml_max_column = 70;
    # The maximal column number.


    $xml_column = 0;
    # The current column number.

    $xml_indent = 0;
    # Stores the current indentation.

    $xml_indent_step = 2;
    # Number of colums by which the indentation increases at each new indentation level.

    $xml_namespace = FALSE;
    # The namespace URI. May also be a false value, which indicates that no nameapace
    # should be used.

    $xml_namespace_prefix = FALSE;
    # The namespace prefix. May also be a false value, which indicates that no nameapace
    # prefix should be used.

    $xml_namespace_initialized = FALSE;
    # Whether the namespace has already been set via an 'xmlns' or 'xmlms:foo' attribute.

    $xml_writer_error_handler = sub { die(@_, "\n"); };
    # Reference to the function to call in the case of errors.

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub reset_xml_writer
  # ()
  # Prepares the writer for a new run. Clears the output list, @xml_output_list},
  # and sets $xml_last_termination, $xml_columns, and $xml_indent to 0.
  {
    @xml_output_list = ();
    $xml_last_termination = "";
    $xml_column = 0;
    $xml_indent = 0;
  }


# --------------------------------------------------------------------------------
# Auxiliaries
# --------------------------------------------------------------------------------

sub set_xml_namespace
  {
    ($xml_namespace, $xml_namespace_prefix, $xml_namespace_initialized) = @_;
  }

sub process_raw_string
  {
    my $raw_str = $_[0];
    my $start;
    my $end;
    my $new_str;

    if ( $raw_str =~ m/(^\s*)(.*\S)(\s*$)/s )
      {
	if ( $1 )
	  {
	    $start = "WHITESPACE";
	  }
	else
	  {
	    $start = "NON_WHITESPACE";
	  }
	
	if ( $3 )
	  {
	    $end = "WHITESPACE";
	  }
	else
	  {
	    $end = "NON_WHITESPACE";
	  }
	
	$new_str = $2;
      }
    elsif ( $raw_str =~ m/^\s+$/ )
      {
	$start = "WHITESPACE";
	$end = "WHITESPACE";
	$new_str = "";
      }
    else
      {
	$start = "NON_WHITESPACE";
	$end = "NON_WHITESPACE";
	$new_str = "";
      }

    return($new_str, $start, $end);
  }

sub get_attrib_list
  # ($attribs)
  # $attribs must be a reference to a hash whose keys and values are interpreted as
  # attribute names and values, respectively. The function returns a reference to a
  # list of strings, each of which having the form
  #c
  #       \plhld{attrib_name}="\plhld{attrib_value}" ,
  #/c
  # where \plhld{attrib_name} runs through all attribute names given in $attribs (and
  # \plhld{attrib_value} through the corresponding values, of course). The values in
  # \code{%{$attribs}} must not be quoted. The function quotes them. Quoting sign is
  # `"' unless the value contains this sign, in which case ``' is used. If the value
  # contains both `"' and ``' signs, the latter ones are replaced by \code{$apos;} and
  # ``' is used to quote.
  {
    my $attribs = $_[0];
    my @attrib_list = ();
    my $attrib_name;
    my $attrib_value;
    my $attrib;
    my $quote;

    foreach $attrib_name (keys(%{$attribs}))
      {
	if ( ! defined($attribs->{$attrib_name}) )
	  {
	    &{$xml_writer_error_handler}
              ("Mumie::XML::Writer::get_attrib_list: No value for attribute: $attrib_name");
	  }

	$attrib_value = $attribs->{$attrib_name};

	if ( $attrib_value =~ m/\"/g )
	  {
	    $attrib_value =~ s/\'/&apos;/g;
	    $quote = "\'";
	  }
	else
	  {
	    $quote = "\"";
	  }

	$attrib = "$attrib_name=$quote$attrib_value$quote";
	push(@attrib_list, "$attrib_name=$quote$attrib_value$quote");
      }

    return(\@attrib_list);
  }

sub get_max_attrib_len
  {
    my $attrib_list = $_[0];
    my $max_attrib_len = 0;

    foreach $attrib (@{$attrib_list})
      {
	if ( length($attrib) > $max_attrib_len )
	  {
	    $max_attrib_len = length($attrib);
	  }
      }

    return($max_attrib_len);
  }

sub get_attrib_area_len
  {
    my $attrib_list = $_[0];
    my $attrib_area_len = 0;

    foreach $attrib (@{$attrib_list})
      {
	$attrib_area_len++;
	$attrib_area_len += length($attrib);
      }

    return($attrib_area_len);
  }

sub pop_last_output
  {
    my $last_word = pop(@xml_output_list);

    $xml_column -= text_len($last_word);
    if ( ( @xml_output_list ) && ( $xml_column > 0 ) )
      {
	my $last_sep = pop(@xml_output_list);
	if ( $last_sep !~ m/^[ \n]+$/ )
	  {
	    &{$xml_writer_error_handler}
              ("Mumie::XML::Writer::pop_last_output():",
               "  expected : separating whitespaces\n",
               "  found    : $last_sep");
	  }
	else
	  {
	    $xml_column -= text_len($last_sep);
	  }
      }

    return($last_word);
  }

sub count_nls_at_end
  # ()
  {
    my $last_entry = $xml_output_list[$#xml_output_list];
    if ( $last_entry )
      {
	return(count_subexp("\\n", $last_entry));
      }
    else
      {
	return(0);
      }
  }

sub xml_newlines
  # ($num)
  {
    my $num = $_[0];
    if ( $num )
      {
	push(@xml_output_list, "\n" x $num);
	$xml_column = 0;
	$xml_last_termination = "CLOSED";
      }
  }

sub xml_continuous_text
  # ($raw_text)
  {
    my $raw_text = $_[0];

    return() if ( $raw_text eq '' );

    my ($text, $start, $end) = process_raw_string($raw_text);

    my @text_words = split(/\s+/, $text);

    if ( ($xml_last_termination eq "NON_WHITESPACE" ) && ( $start eq "NON_WHITESPACE" ) )
      {
	$text_words[0] = pop_last_output() . $text_words[0];
      }
    my $formatted_text
      = format_words_as_block(\@text_words, $xml_max_column, $xml_indent, $xml_column);

    push(@xml_output_list, @{$formatted_text});
    $xml_column = $last_format_col;
    $xml_last_termination = $end;
  }

sub xml_verbatim_text
  # ($text)
  {
    my $text = $_[0];

    # Correction for final newlines, which otherwise will be lost
    $text =~ m/(\n*)$/;
    $text = $`;
    my $final_nls = $1;

    my @lines = split("\n", $text);
    my $current_line;

    if ( $xml_column + text_len($lines[0]) <= $xml_max_column )
      {
	$current_line = shift(@lines);
	if ( ( $xml_column == 0 ) && ( $xml_indent ) )
	  {
	    push(@xml_output_list, " " x $xml_indent);
	  }
	push(@xml_output_list, $current_line);
      }

    while ( @lines )
      {
	push(@xml_output_list, "\n");
	if ( $xml_indent )
	  {
	    push(@xml_output_list, " " x $xml_indent);
	  }
	$current_line = shift(@lines);
	push(@xml_output_list, $current_line);
      }

    $xml_column = $xml_indent + text_len($current_line);

    if ( $final_nls )
      {
	push(@xml_output_list, $final_nls);
	$xml_column = 0;
      }

    $xml_last_termination = "CLOSED";
  }

sub xml_strictly_verbatim_text
  # ($text)
  {
    my $text = $_[0];

    push(@xml_output_list, $text);

    if ( $text =~ m/\n([^\n]+)$/ )
      {
	my $last_line = $1;
	$xml_column = length($last_line) - 1;
      }
    else
      {
	$xml_column += length($text);
      }
    $xml_last_termination = 'CLOSED';
  }

sub xml_tag_with_attribs_non_inline
  # ($name, $attrib_list, $tag_start, $tag_end, $newlines_before, $newlines_after,
  #  $indent_opt)
  # Low-level function to write tags (or tag-like expressions) with contain attributes and
  # which are inserted non-inline into the code. Typical applications are:
  #L
  #-   XML start tags
  #-   Processing instruction tags
  #-   XML declaration tag
  #/L
  # End tags won't usually be written with this function, since they don't contain
  # attributes. \code{name} is the name of the tag. \code{attrib_list} is a list as returned
  # by \link{\code{get_attrib_list}} which specifies the attributes. \code{tag_start} and
  # \code{tag_end} are the strings by which the tag is opened resp. closed (in the case of a
  # normal XML start tag, this would be `<' and `>'). \code{$newlines_before} and
  # \code{$newlines_after} are the number of newlines to insert before resp. after the
  # tag. Note: if \code{$newlines_before} is set to zero, the function assumes that the current
  # column number, i.e., \code{xml_column}, is zero. So, always give \code{$newlines_before}
  # a positive value, except you are shure you are at the beginning of a line.
  {
    my ($name, $attrib_list, $tag_start, $tag_end, $newlines_before, $newlines_after, $indent_opt)
      = @_;

    my $max_attrib_len = get_max_attrib_len($attrib_list);
    my $attrib_area_len = get_attrib_area_len($attrib_list);

    my $new_last_col = $xml_indent + length($tag_start) + length($name)
                       + $attrib_area_len + length($tag_end);

    if ( ! @xml_output_list ) # i.e., if the tag to write is the first content of the document
      {
	$newlines_before = 0; # $xml_column is necessarily 0, so no problem
      }

    if ( $newlines_before )
      {
	my $newlines_diff = $newlines_before - count_nls_at_end();
	if ( $newlines_diff > 0 )
	  {
	    push(@xml_output_list, "\n" x $newlines_diff);
	  }
	$xml_column = 0;
	if ( $indent_opt eq "RESET_INDENT" )
	  {
	    ( ($xml_indent -= $xml_indent_step) >= 0 or ($xml_indent = 0) );
	  }
      }

    $attrib_list->[$#{$attrib_list}] = $attrib_list->[$#{$attrib_list}] . $tag_end;

    if ( $new_last_col <= $xml_max_column )
      {
	push(@xml_output_list, " " x $xml_indent, "$tag_start$name");
	while ( @{$attrib_list} )
	  {
	    push(@xml_output_list, " ", shift(@{$attrib_list}));
	  }
	$xml_column = $new_last_col;
      }
    else
      {
	my $attrib_indent = $xml_indent + length($tag_start) + length($name) + 1;
	my $attrib = shift(@{$attrib_list});
	push(@xml_output_list, " " x $xml_indent, "$tag_start$name", " ", $attrib);
	while ( @{$attrib_list} )
	  {
	    $attrib = shift(@{$attrib_list});
	    push(@xml_output_list, "\n", " " x $attrib_indent, $attrib);
	  }
	$xml_column = $attrib_indent + length($attrib);
      }

    $xml_last_termination = "NON_WHITESPACE";

    if ( $newlines_after )
      {
	push(@xml_output_list, "\n" x $newlines_after);
	$xml_column = 0;
	$xml_last_termination = "CLOSED";
	if ( $indent_opt eq "INDENT" )
	  {
	    $xml_indent += $xml_indent_step;
	  }
      }
  }

sub xml_tag_with_attribs_inline
  {
    my ($name, $attrib_list, $tag_start, $tag_end) = @_;

    my $max_attrib_len = get_max_attrib_len($attrib_list);
    my $attrib_area_len = get_attrib_area_len($attrib_list);

    my @to_append = ("$tag_start$name", @{$attrib_list});

    $to_append[$#to_append] = $to_append[$#to_append] . $tag_end;

    if ( $xml_last_termination eq "NON_WHITESPACE" )
      {
	$to_append[0] = pop_last_output() . $to_append[0];
      }

    my $formatted_tag
      = format_words_as_block(\@to_append, $xml_max_column, $xml_indent, $xml_column);

    push(@xml_output_list, @{$formatted_tag});
    $xml_column = $last_format_col;
    $xml_last_termination = "NON_WHITESPACE";
  }

sub xml_tag_with_attribs_verbatim
  {
    my ($name, $attrib_list, $tag_start, $tag_end) = @_;
    my $tag = $tag_start . $name . " " . join(' ', @{$attrib_list}) . $tag_end;
    xml_verbatim_text($tag);
  }

sub xml_tag_with_attribs_strictly_verbatim
  {
    my ($name, $attrib_list, $tag_start, $tag_end) = @_;
    my $tag = $tag_start . $name . " " . join(' ', @{$attrib_list}) . $tag_end;
    xml_strictly_verbatim_text($tag);
  }

sub xml_tag_without_attribs_non_inline
  #a ($name, $tag_start, $tag_end, $newlines_before, $newlines_after, $indent_opt)
  # Low-level function to write tags (or tag-like expressions) with contain no attributes
  # and which are inserted non-inline into the code. Typical applications are:
  #L
  #-   XML start tags
  #-   XML end tags
  #/L
  # \code{name} is the name of the tag. \code{tag_start} and \code{tag_end} are the strings
  # by which the tag is opened resp. closed (in the case of a normal XML start tag, this
  # would be `<' and `>'). \code{$newlines_before} and \code{$newlines_after} are the number
  # of newlines to insert before resp. after the tag.

  {
    my ($name, $tag_start, $tag_end, $newlines_before, $newlines_after, $indent_opt) = @_;

    my $tag = "$tag_start$name$tag_end";

    if ( ! @xml_output_list )
      {
	$newlines_before = 0;
      }

    if ( $newlines_before )
      {
	my $newlines_diff = $newlines_before - count_nls_at_end();
	if ( $newlines_diff > 0 )
	  {
	    push(@xml_output_list, "\n" x $newlines_diff);
	  }
	$xml_column = 0;

	if ( $indent_opt eq "RESET_INDENT" )
	  {
	    ( ($xml_indent -= $xml_indent_step) >= 0 or ($xml_indent = 0) );
	  }
	if ( $xml_indent > 0 )
	  {
	    push(@xml_output_list, " " x $xml_indent);
	    $xml_column += $xml_indent;
	  }
	push(@xml_output_list, $tag);
	$xml_column += length($tag);
	$xml_last_termination = "NON_WHITESPACE";
      }
    else
      {
	xml_continuous_text($tag);
      }

    if ( $newlines_after )
      {
	push(@xml_output_list, "\n" x $newlines_after);
	$xml_column = 0;
	$xml_last_termination = "CLOSED";
	if ( $indent_opt eq "INDENT" )
	  {
	    $xml_indent += $xml_indent_step;
	  }
      }
  }

sub xml_tag_without_attribs_inline
  {
    my ($name, $tag_start, $tag_end) = @_;

    my $to_append = "$tag_start$name$tag_end";

    if ( $xml_last_termination eq "NON_WHITESPACE" )
      {
	$to_append = pop_last_output() . $to_append;
      }

    my $formatted_tag
      = format_words_as_block([$to_append], $xml_max_column, $xml_indent, $xml_column);

    push(@xml_output_list, @{$formatted_tag});

    $xml_column = $last_format_col;
    $xml_last_termination = "NON_WHITESPACE";
  }

sub xml_tag_without_attribs_verbatim
  {
    my ($name, $tag_start, $tag_end) = @_;
    my $tag = $tag_start . $name . $tag_end;
    xml_verbatim_text($tag);
  }

sub xml_tag_without_attribs_strictly_verbatim
  {
    my ($name, $tag_start, $tag_end) = @_;
    my $tag = $tag_start . $name . $tag_end;
    xml_strictly_verbatim_text($tag);
  }

sub xml_comment_display
  # ($comment, $newlines_before, $newlines_after)
  {
    my ($comment, $newlines_before, $newlines_after) = @_;

    ( ($newlines_before -= count_nls_at_end()) > 0 or ($newlines_before = 0) );
    push(@xml_output_list, "\n" x $newlines_before);
    $xml_column = 0;

    xml_continuous_text("<!-- $comment -->");

    push(@xml_output_list, "\n" x $newlines_after);
    $xml_column = 0;
    $xml_last_termination = "NON_WHITESPACE";
  }

sub xml_comment_inline
  # ($comment)
  {
    my $comment = $_[0];
    xml_continuous_text("<!-- $comment -->");
  }


# --------------------------------------------------------------------------------
# Functions to write the XML code
# --------------------------------------------------------------------------------

sub write_xml_pcdata
  # ($pcdata, $layout)
  {
    my ($pcdata, $layout) = @_;
    $layout or ($layout = "CONTINUOUS");

    if ( $layout eq "CONTINUOUS" )
      {
	xml_continuous_text($pcdata);
      }
    elsif ( $layout eq "VERBATIM" )
      {
	xml_verbatim_text($pcdata);
      }
    elsif ( $layout eq "STRICTLY_VERBATIM" )
      {
	xml_strictly_verbatim_text($pcdata);
      }
  }

sub write_xml_start_tag
  # ($name, $attribs, $layout)
  # Writes a start tag corresponding to the XML element $name. The attributes must be given
  # as a reference $attribs to a hash whose keys and values are the attributes' names and
  # values, respectively. $attribs is processed by \link{\code{get_attrib_list}}.  See there
  # for more information. $layout must be one of the keywords \code{"DISPLAY"},
  # \code{"SEMI_DISPLAY"}, \code{"VERBATIM"},\code{"STRICTLY_VERBATIM"}, or
  # \code{"INLINE"}. In the case of \code{"DISPLAY"}, one newline is inserted before the tag
  # and another one after the tag. In the case of \code{"SEMI_DISPLAY"}, only the newline
  # before the tag is inserted. In the case of \code{"INLINE"}, the tag is imbedded into the
  # current line.  In the case of \code{"VERBATIM"}, the tag is inserted without word wrap,
  # but with indentation. In the case of \code{"STRICTLY_VERBATIM"}, the tag is inserted
  # without word wrap and without indentation.
  {
    my ($name, $attribs, $layout) = @_;
    $layout or ($layout = "INLINE");

    if ( ( $xml_namespace ) && ( $name !~ m/:/ ) && ( ! $attribs->{xmlns} ) )
      {
	$name = "$xml_namespace_prefix:$name" if ( $xml_namespace_prefix );
	if ( ! $xml_namespace_initialized )
	  {
	    my $attrib_name = 'xmlns';
	    $attrib_name .= ":$xml_namespace_prefix" if ( $xml_namespace_prefix );
	    $attribs->{$attrib_name} = $xml_namespace;
	    $xml_namespace_initialized = TRUE;
	  }
      }

    if ( %{$attribs} )
      {
	my $attrib_list = get_attrib_list($attribs);
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_with_attribs_inline($name, $attrib_list, "<", ">");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline($name, $attrib_list, "<", ">", 1, 0, "INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline($name, $attrib_list, "<", ">", 1, 1, "INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_with_attribs_verbatim($name, $attrib_list, "<", ">");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_with_attribs_strictly_verbatim($name, $attrib_list, "<", ">");
	  }
      }
    else # i.e., no attributes
      {
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_without_attribs_inline($name, "<", ">");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline($name, "<", ">", 1, 0, "INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline($name, "<", ">", 1, 1, "INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_without_attribs_verbatim($name, "<", ">");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_without_attribs_strictly_verbatim($name, "<", ">");
	  }
      }
  }

sub write_xml_end_tag
  # ($name, $layout)
  # Writes an end tag corresponding to the XML element $name. $layout has the same meaning
  # as with \link{\code{write_xml_start_tag}}.
  {
    my ($name, $layout) = @_;
    $layout or ($layout = "INLINE");

    if ( ( $xml_namespace ) && ( $xml_namespace_prefix ) && ( $name !~ m/:/ ) &&
	 ( ! $attribs->{xmlns} ) )
      {
	$name = "$xml_namespace_prefix:$name";
      }

    if ( $layout eq "INLINE" )
      {
	xml_tag_without_attribs_inline($name, "</", ">");
      }
    elsif ( $layout eq "SEMI_DISPLAY" )
      {
	xml_tag_without_attribs_non_inline($name, "</", ">", 1, 0, "RESET_INDENT");
      }
    elsif ( $layout eq "DISPLAY" )
      {
	xml_tag_without_attribs_non_inline($name, "</", ">", 1, 1, "RESET_INDENT");
      }
    elsif ( $layout eq "VERBATIM" )
      {
	xml_tag_without_attribs_verbatim($name, "</", ">");
      }
    elsif ( $layout eq "STRICTLY_VERBATIM" )
      {
	xml_tag_without_attribs_strictly_verbatim($name, "</", ">");
      }
  }

sub write_empty_xml_element
  # ($name, $attribs, $layout)
  # Writes a tag corresponding to the empty XML element $name. The attributes must be given
  # as a reference $attribs to a hash whose keys and values are the attributes' names and
  # values, respectively. $attribs is processed by \link{\code{get_attrib_list}}. See there
  # for more information. $layout has the same meaning as with
  # \link{\code{write_xml_start_tag}}.
  {
    my ($name, $attribs, $layout) = @_;

    $layout or ($layout = "INLINE");

    if ( ( $xml_namespace ) && ( $name !~ m/:/ ) && ( ! $attribs->{xmlns} ) )
      {
	$name = "$xml_namespace_prefix:$name" if ( $xml_namespace_prefix );
	if ( ! $xml_namespace_initialized )
	  {
	    my $attrib_name = 'xmlns';
	    $attrib_name .= ":$xml_namespace_prefix" if ( $xml_namespace_prefix );
	    $attribs->{$attrib_name} = $xml_namespace;
	    $xml_namespace_initialized = TRUE;
	  }
      }

    if ( %{$attribs} )
      {
	my $attrib_list = get_attrib_list($attribs);
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_with_attribs_inline($name, $attrib_list, "<", "/>");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline($name, $attrib_list, "<", "/>", 1, 0, "NO_INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline($name, $attrib_list, "<", "/>", 1, 1, "NO_INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_with_attribs_verbatim($name, $attrib_list, "<", "/>");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_with_attribs_strictly_verbatim($name, $attrib_list, "<", "/>");
	  }
        else
	  {
	    &{$xml_writer_error_handler}
              ("Mumie::XML::Writer::write_empty_xml_element: Unknown layout keyword: $layout");
	  }
      }
    else # i.e., no attributes
      {
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_without_attribs_inline($name, "<", "/>");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline($name, "<", "/>", 1, 0, "NO_INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline($name, "<", "/>", 1, 1, "NO_INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_without_attribs_verbatim($name, "<", "/>");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_without_attribs_strictly_verbatim($name, "<", "/>");
	  }
        else
	  {
	    &{$xml_writer_error_handler}
              ("Mumie::XML::Writer::write_empty_xml_element: Unknown layout keyword: $layout");
	  }
      }
  }

sub write_xml_decl
  # ($version, $encoding, $standalone, $layout)
  # Writes an XML declaration tag, i.e.,
  #
  #     <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
  #
  {
    my ($version, $encoding, $standalone, $layout) = @_;
    $version ||= '1.0';
    $layout ||= 'DISPLAY';
    my @attrib_list = ("version=\"$version\"");
    push(@attrib_list, "encoding=\"$encoding\"") if ( $encoding );
    push(@attrib_list, "standalone=\"$standalone\"") if ( $standalone );
    if ( $layout eq "INLINE" )
      {
	xml_tag_with_attribs_inline("xml", \@attrib_list, "<?", "?>");
      }
    elsif ( $layout eq "SEMI_DISPLAY" )
      {
	xml_tag_with_attribs_non_inline("xml", \@attrib_list, "<?", "?>", 1, 0, "NO_INDENT");
      }
    elsif ( $layout eq "DISPLAY" )
      {
	xml_tag_with_attribs_non_inline("xml", \@attrib_list, "<?", "?>", 1, 1, "NO_INDENT");
      }
    elsif ( $layout eq "VERBATIM" )
      {
	xml_tag_with_attribs_verbatim("xml", \@attrib_list, "<?", "?>");
      }
    elsif ( $layout eq "STRICTLY_VERBATIM" )
      {
	xml_tag_with_attribs_strictly_verbatim("xml", \@attrib_list, "<?", "?>");
      }
  }

sub write_xml_pr_instr
  # ($target, $attrib_list, $layout)
  {
    my ($target, $attrib_list, $layout) = @_;
    $layout or ($layout = "DISPLAY");

    if ( @{$attrib_list} )
      {
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_with_attribs_inline($target, $attrib_list, "<?", "?>");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline($target, $attrib_list, "<?", "?>", 1, 0, "NO_INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline($target, $attrib_list, "<?", "?>", 1, 1, "NO_INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_with_attribs_verbatim($target, $attrib_list, "<?", "?>");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_with_attribs_strictly_verbatim($target, $attrib_list, "<?", "?>");
	  }
      }
    else # i.e., no attributes
      {
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_without_attribs_inline($target, "<?", "?>");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline($target, "<?", "?>", 1, 0, "NO_INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline($target, "<?", "?>", 1, 1, "NO_INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_without_attribs_verbatim($target, "<?", "?>");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_without_attribs_strictly_verbatim($target, "<?", "?>");
	  }
      }
  }

sub write_xml_doctype_tag
  # ($attrib_list, $layout)
  {
    my ($attrib_list, $layout) = @_;
    $layout or ($layout = "DISPLAY");

    if ( @{$attrib_list} )
      {
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_with_attribs_inline("DOCTYPE", $attrib_list, "<!", ">");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline("DOCTYPE", $attrib_list, "<!", ">",
					    1, 0, "NO_INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_with_attribs_non_inline("DOCTYPE", $attrib_list, "<!", ">",
					    1, 1, "NO_INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_with_attribs_verbatim("DOCTYPE", $attrib_list, "<!", ">");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_with_attribs_strictly_verbatim("DOCTYPE", $attrib_list, "<!", "?>");
	  }
      }
    else # i.e., no attributes
      {
	if ( $layout eq "INLINE" )
	  {
	    xml_tag_without_attribs_inline("DOCTYPE", "<!", ">");
	  }
	elsif ( $layout eq "SEMI_DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline("DOCTYPE", "<!", ">", 1, 0, "NO_INDENT");
	  }
	elsif ( $layout eq "DISPLAY" )
	  {
	    xml_tag_without_attribs_non_inline("DOCTYPE", "<!", ">", 1, 1, "NO_INDENT");
	  }
	elsif ( $layout eq "VERBATIM" )
	  {
	    xml_tag_without_attribs_verbatim("DOCTYPE", "<!", ">");
	  }
	elsif ( $layout eq "STRICTLY_VERBATIM" )
	  {
	    xml_tag_without_attribs_strictly_verbatim("DOCTYPE", "<!", ">");
	  }
      }
  }

sub write_xml_comment
  {
    my ($comment_list, $layout) = @_;
    $layout or ($layout = "INLINE");
    my $comment = join('', @{$comment_list});

    if ( $layout eq "DISPLAY" )
      {
	xml_comment_display($comment, 1, 1);
      }
    elsif ( $layout eq "INLINE" )
      {
	xml_comment_inline($comment);
      }
  }

sub write_xml_start_comment
  {
    my ($layout) = @_;
    $layout or ($layout = "INLINE");

    if ( $layout eq "INLINE" )
      {
	xml_tag_without_attribs_inline("", "<!--", "");
	$xml_last_termination = "CLOSED";
      }
    elsif ( $layout eq "SEMI_DISPLAY" )
      {
	xml_tag_without_attribs_non_inline("", "<!--", "", 1, 0, "NO_INDENT");
	$xml_last_termination = "CLOSED";
      }
    elsif ( $layout eq "DISPLAY" )
      {
	xml_tag_without_attribs_non_inline("", "<!--", "", 1, 1, "INDENT");
      }
    elsif ( $layout eq "VERBATIM" )
      {
	xml_tag_without_attribs_verbatim("", "<!--", "");
      }
    elsif ( $layout eq "STRICTLY_VERBATIM" )
      {
	xml_tag_without_attribs_strictly_verbatim("", "<!--", "");
      }
  }

sub write_xml_end_comment
  {
    my ($layout) = @_;
    $layout or ($layout = "INLINE");

    if ( $layout eq "INLINE" )
      {
	$xml_last_termination = "CLOSED";
	xml_tag_without_attribs_inline("", "", "-->");
      }
    elsif ( $layout eq "SEMI_DISPLAY" )
      {
	$xml_last_termination = "CLOSED";
	xml_tag_without_attribs_non_inline("", "", "-->", 0, 1, "NO_INDENT");
      }
    elsif ( $layout eq "DISPLAY" )
      {
	xml_tag_without_attribs_non_inline($name, "", "-->", 1, 1, "RESET_INDENT");
      }
    elsif ( $layout eq "VERBATIM" )
      {
	xml_tag_without_attribs_verbatim("", "", "-->");
      }
    elsif ( $layout eq "STRICTLY_VERBATIM" )
      {
	xml_tag_without_attribs_strictly_verbatim("", "", "-->");
      }
  }

sub write_xml
  # ($text)
  {
    my ($text, $layout) = @_;
    $layout or ($layout = "CONTINUOUS");

    if ( $layout eq "CONTINUOUS" )
      {
	xml_continuous_text($text);
      }
    elsif ( $layout eq "VERBATIM" )
      {
	xml_verbatim_text($text);
      }
    elsif ( $layout eq "STRICTLY_VERBATIM" )
      {
	xml_strictly_verbatim_text($text);
      }
  }

init();
return(1);
