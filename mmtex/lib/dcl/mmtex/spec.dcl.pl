# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: spec.dcl.pl,v 1.9 2007/07/11 15:56:13 grudzin Exp $

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
# A generic document class.


# ------------------------------------------------------------------------------------------
# Environment `document'
# ------------------------------------------------------------------------------------------

sub begin_document
  {
    log_message("\nbegin_document\n");

    $scan_proc->{mode} = "NORMAL";
    $scan_proc->{allowed_tokens} = [@basic_token_types, "inline_math_boundry"];
    $scan_proc->{inside_par} = FALSE;
    $scan_proc->{par_enabled} = TRUE;

    # Installing commands and environments

    install_cmds
      (["title",
        "subtitle",
        "version",
        "tableofcontents",
        "section",
        "section*"],
       "struc");
    install_envs(["authors"], "struc");
    install_cmds("ALL", "simple_markup");
    install_cmds_from_all_libs('MATH_TOPLEVEL');
    install_envs_from_all_libs('MATH_TOPLEVEL');
    install_cmds("ALL", "counter");
    $scan_proc->{env_table}->{table}->{begin_disabled} = FALSE;
    $scan_proc->{env_table}->{tabular}->{begin_disabled} = FALSE;
    install_cmds("ALL", "list");
    install_envs("ALL", "list");
    install_envs(["preformatted"], "preformatted");
    install_cmds("ALL", "verbatim");
    install_cmds("ALL", "hyperlink");
    install_cmds("ALL", "multimedia");
    install_cmds("ALL", "input");
    install_cmds("ALL", "specialchar");
    install_cmds("ALL", "length");
    install_cmds("ALL", "box");
    install_envs("ALL", "box");
    install_cmds("ALL", "lang");

    # Setting up relationships between commads and/or environments

    $scan_proc->{start_inline_math_end_hook}
      = sub
	  {
	    install_cmds_from_all_libs('MATH');
	    install_envs_from_all_libs('MATH');
	  };

    $scan_proc->{cmd_table}->{section}->{pre_hook}
      = sub
	  {
	    remove_cmds(["title", "subtitle", "tableofcontents"]);
	  };

    my @markup_cmds = keys(%{$import_cmd_tables->{simple_markup}});
    my @struc_cmds = ("section", "subsection", "subsubsection",
                      "section*", "subsection*", "subsubsection*");
    my @table_cmds = ("head", "body", "foot", "rowspan", "colspan");

    my $pre_markup_arg_hook
      = sub
	  {
	    own_cmd_table();
	    remove_cmds(\@struc_cmds);
	  };

    my $cmd;
    foreach $cmd (@markup_cmds)
      {
	$scan_proc->{cmd_table}->{$cmd}->{pre_arg_hook} = $pre_markup_arg_hook;
      }

    my $pre_data_arg_hook
      = sub
	  {
	    own_cmd_table();
	    remove_cmds(\@markup_cmds);
	    remove_cmds(\@struc_cmds);
	    remove_cmds(\@table_cmds);
	  };

    $scan_proc->{cmd_table}->{anchor}->{pre_name_arg_hook} = $pre_data_arg_hook;
    $scan_proc->{cmd_table}->{link}->{pre_uri_arg_hook} = $pre_data_arg_hook;
    $scan_proc->{cmd_table}->{link}->{pre_target_arg_hook} = $pre_data_arg_hook;

    $scan_proc->{env_table}->{table}->{no_strucs_allowed} = TRUE;

    $scan_proc->{post_exec_env_begin_hook}
      = sub
	  {
	    my $env_name = $_[0];
	    if ( $scan_proc->{env_table}->{$env_name}->{no_strucs_allowed} )
	      {
		remove_cmds(\@struc_cmds);
	      }
	  };

    &{$scan_proc->{start_document}}();
  }

sub end_document
  {
    &{$scan_proc->{close_document}}();
  }


# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$dcl_table->{'mmtex.spec'}->{initializer} =  sub
  {
    log_message("<dcl japs.spec initalizer> 1/2");

    # Set language:
    if ( $dcl_option_table->{lang} && ! $params{lang} )
      {
        $scan_proc->{lang} = $dcl_option_table->{lang};
      }

    $xsl_stylesheet ||= "file://$xsl_dir/mmtex/spec.2xhtml.xsl";

    require_lib("math");
    $set_mathml_namespace = TRUE;
    require_lib("struc");
    require_lib("counter");
    require_lib("simple_markup");
    require_lib("table");
    require_lib("preformatted");
    require_lib("verbatim");
    require_lib("hyperlink");
    require_lib("insert");
    require_lib("multimedia");
    require_lib("usercmd");
    require_lib("specialchar");
    require_lib("list");
    require_lib("system");
    require_lib("input");
    require_lib("length");
    require_lib("box");
    require_lib("tabular");
    require_lib("lang");
    require_lib("german") if ( $scan_proc->{lang} eq 'de' );

    $lib_table->{simple_markup}->{cmd_table}->{code}->{classes} =
      [
       'normal',
       'xml-element',
       'xml-attrib',
       'xml-value',
       'db-table',
       'db-column',
       'tex-cmd',
       'tex-env',
       'value',
       'param',
       'sql'
      ];

    $lib_table->{simple_markup}->{cmd_table}->{emph}->{classes} =
      [
       'normal',
       'important',
       'new',
       'deprecated',
       'not-impl',
      ];

    $scan_proc->{cmd_table} = {};

    install_cmds_from_all_libs('PREAMBLE');
    install_cmds(["insert"], "insert");
    install_cmds(["newcommand"], "usercmd");

    $scan_proc->{env_table}
      = {
	 "document" =>
	 {
	  "num_opt_args" => 0,
	  "num_man_args" => 0,
	  "begin_function" => \&begin_document,
	  "end_function" => \&end_document,
	  "early_pre_end_hook" => sub
	    {
	      close_par_if_in_par();
	      close_strucs();
	    },
	 }
	};

    install_envs(["table"], "table");
    install_envs(["tabular"], "tabular");

    $scan_proc->{env_table}->{table}->{begin_disabled} = TRUE;
    $scan_proc->{env_table}->{tabular}->{begin_disabled} = TRUE;

    $scan_proc->{start_page} = sub
      {
        # new_xml_namespace("http://www.mumie.net/xml-namespace/mmtex/spec");
	xml_decl("1.0", "ISO_8859-2", undef(), "DISPLAY");
	if ( $xsl_stylesheet )
	  {
	    xml_pr_instr("xml-stylesheet",
			 ["type=\"text/xsl\"", "href=\"$xsl_stylesheet\""],
			 "DISPLAY");
	  }
	start_xml_element("spec", {}, "DISPLAY");
        if ( $scan_proc->{lang} )
          {
            empty_xml_element('language', {'value' => $scan_proc->{lang}});
          }
      };

    $scan_proc->{start_document} = sub
      {
	start_xml_element("document", {}, "DISPLAY");
      };

    $scan_proc->{close_document} = sub
      {
	close_xml_element("document", "DISPLAY");
      };

    $scan_proc->{close_page} = sub
      {
	close_xml_element("spec", "DISPLAY");
      };

    $scan_proc->{post_documentclass_hook} = \&{$scan_proc->{start_page}};

    $scan_proc->{post_scan_hook} = \&{$scan_proc->{close_page}};

    log_message("<dcl help initalizer> 2/2");
};

return(1);	
