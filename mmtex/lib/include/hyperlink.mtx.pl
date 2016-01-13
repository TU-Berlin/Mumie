package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: hyperlink.mtx.pl,v 1.19 2008/01/20 22:37:49 rassy Exp $

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
#1                                  Description
# --------------------------------------------------------------------------------
#

log_message("\nLibrary hyperlink ", '$Revision: 1.19 $ ', "\n");

# $anchor_name_regexp = "^[a-zA-Z0-9-_.]+\$";
# # Use $user_id_regexp instead


# --------------------------------------------------------------------------------
# Execute functions
# --------------------------------------------------------------------------------

sub execute_anchor
  {
    log_message("\nexecute_anchor\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    # Retrieving anchor name:
    my $name = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "anchor", "name_arg");

    # Checking anchor name:
    $name =~ m/$user_id_regexp/ or
      &{$scan_proc->{error_handler}}("Invalid anchor name: $name");

    # PROBLEM: May or may not start a paragraph depending on whether the optional
    # argument is present or not. WORKAROUND: Calling start_par_if_necessary as
    # soon as we know there is an optional argument.

    if ( $opt_args->[0] )
      {
        # Workaround (cf. above). Start paragraph if necessary:
        start_par_if_necessary();

	# Opening XML element
	start_xml_element("anchor", {"name" => $name}, "INLINE");

	# Getting anchor text
	my $text_output_list
	  = convert_arg(0, $opt_args, $pos_opt_args, "CMD", "anchor", "text_arg");

	# Appending anchor text to XML
	append_to_output(@{$text_output_list});

	# Closing XML element
	close_xml_element("anchor", "INLINE");
      }
    else
      {
	# Inserting an empty XML element
	empty_xml_element("anchor", {"name" => $name}, "INLINE");
      }
  }

sub execute_label
  {
    log_message("\nexecute_label\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    # Retrieving label name
    my $name = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "label", "name_arg");

    # Checking anchor name:
    $name =~ m/$user_id_regexp/ or
      &{$scan_proc->{error_handler}}("Invalid anchor name: $name");

    # Inserting an empty "anchor" element
    empty_xml_element("anchor", {"name" => $name}, "INLINE");
  }

sub execute_link
  {
    log_message("\nexecute_link\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    # Retrieving URI
    my $href = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "link", "uri_arg");

    # Retrieving target
    my $target = FALSE;
    if ( $opt_args->[0] )
      {
	$target = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD", "link", "target_arg");
      }

    # Setting up attributes
    my $attribs;
    $attribs->{href} = $href;
    if ( $target )
      {
	$attribs->{target} = $target;
      }

    # Opening XML element
    start_xml_element("link", $attribs, "INLINE");

    # Getting link text
    my $text_output_list
          = convert_arg(1, $man_args, $pos_man_args, "CMD", "link", "text_arg");

    # Appending link text to XML
    append_to_output(@{$text_output_list});

    #Closing XML element
    close_xml_element("link", "INLINE");
  }

sub execute_ref
  {
    log_message("\nexecute_ref\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    # Retrieving anchor name
    my $name = get_data_from_arg(0, $man_args, $pos_man_args, "CMD", "ref", "name_arg");
    $name =~ s/^#//;

    empty_xml_element("ref", {name => $name}, "INLINE");
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------


$lib_table->{hyperlink}->{initializer} = sub
  {
    my $hyperlink_cmd_table
      = {
         "anchor" =>
           {
            "num_opt_args" => 1,
            "num_man_args" => 1,
            "is_par_starter" => FALSE, # PROBLEM: May or may not start a paragraph
                                       # depending on whether the optional argument
                                       # is present or not. WORKAROUND: See code of
                                       # execute_anchor.
            "execute_function" => \&execute_anchor,
            "doc" =>
              {
               "opt_args" => [['text','Text to wrap by anchor']],
               "man_args" => [['name','Name of the anchor']],
               "description" => 'Adds an anchor at the current position'
              }
           },
         "label" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => FALSE,
            "execute_function" => \&execute_label,
            "doc" =>
              {
               "man_args" => [['name','Name of the anchor']],
               "description" => 'Adds an anchor at the current position'
              }
           },
         "link" =>
           {
            "num_opt_args" => 1,
            "num_man_args" => 2,
            "is_par_starter" => TRUE,
            "execute_function" => \&execute_link,
            "doc" =>
              {
               "man_args" => 
               [
                ['uri','The URI to be linked to'],
                ['text','The text to be linked']
               ],
               "opt_args" => [['target','The target (_blank, _parent,...)']],
               "description" => 'Makes a link at the current position.'
              }
           },
         "ref" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => TRUE,
            "execute_function" => \&execute_ref,
            "doc" =>
              {
               "man_args" => 
               [
                ['name','Name of the anchor to link to'],
               ],
               "description" => 'Makes a link at the current position.'
              }
           },
         "href" =>
           {
            'is_alias_for' => 'link',
           },
        };
    deploy_lib('hyperlink', $hyperlink_cmd_table, {}, 'TOPLEVEL', FALSE);
  };

return(TRUE);



