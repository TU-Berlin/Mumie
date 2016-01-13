package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: webpage.mtx.pl,v 1.13 2007/07/11 15:56:15 grudzin Exp $

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
# ...


log_message("\nLibrary webpage ", '$Revision: 1.13 $', "\n");

sub start_webpage
  {
    log_message("\nstart_webpage 1/2\n");
    log_data("XSL Stylesheet", $xsl_stylesheet);

    xml_decl("1.0", "ISO_8859-2", undef(), "DISPLAY");
    # xml_doctype(["webpage", "SYSTEM", "\"$doctype\""], "DISPLAY");
    if ( $xsl_stylesheet )
      {
	xml_pr_instr("xml-stylesheet",
		     ["type=\"text/xsl\"", "href=\"$xsl_stylesheet\""],
		     "DISPLAY");
      }
    start_xml_element("webpage", {}, "DISPLAY");
    new_css_styles();
    log_message("\nstart_webpage 2/2\n");
  }

sub close_webpage
  {
    log_message("\nclose_webpage 1/2\n");
    reset_css_styles();
    close_xml_element("webpage", "DISPLAY");
    log_message("\nclose_webpage 2/2\n");
  }

sub start_webpage_document
  {
    start_xml_element("document", {}, "DISPLAY");
  }

sub pre_close_webpage_document
  {
    close_par_if_in_par();
    close_strucs();
  }

sub close_webpage_document
  {
    close_xml_element("document", "DISPLAY");
  }

$lib_table->{webpage}->{initializer} = sub
  {
    require_lib('struc');
  };

return(1);	
