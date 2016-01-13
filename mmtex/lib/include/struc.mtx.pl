package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: struc.mtx.pl,v 1.35 2008/01/20 22:43:40 rassy Exp $

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

# ----------------------------------------------------------------------------------------
#1 Description
# ----------------------------------------------------------------------------------------
#
# Provides commands
#L
#   - to structure the document, as '\section', '\subsection', and so on;
#   - to create title and subtitle;
#   - to create a table of contents.
#/L

# --------------------------------------------------------------------------------
#1 Additional fields in '$scan_proc'
# --------------------------------------------------------------------------------
#
# This library adds additional fields to @{'Mumie::Scanner::$scan_proc'}
# They are:
#
#H
#  struc_table      & Reference to the \emph{structure table}, i.e., a hash
#                     describing all possible structures (see below). \\
#  current_struc    & Name of the current structure (only if currently in a
#                     structure).\\
#  toc_output_list  & Output list for the table of contents. \\
#/H

# --------------------------------------------------------------------------------
#1 The structure table
# --------------------------------------------------------------------------------
#
# A hash that mapps each structure name to a (reference to a) hash comprising all
# information necessary to deal with that structure. Each of the latter hashes has
# the following entries:
#
#H
#  level          & The level of the structure. Must be a positive integer.
#                   (NOTE: Zero is not allowed).\\
#  start_cmd      & The name of the command to start the structure (E.g.,
#                   '\section'). \\
#  env            & The pseudo-ennvironment name of the structure (usually the
#                   structure name preceeded by a "_"). \\
#  xml_element    & Name of the xml element corresponding to the structure. \\
#  counter        & Name of the counter associated with the structure. (Only if
#                 & the structure should be numbered.) \\
#  include_in_toc & Whether the structure should be included in the table of
#                   contents or not (boolean).
#/H

log_message("\nLibrary \"struc\" ", '$Revision: 1.35 $ ', "\n");

sub init_default_struc_table
  {
    $default_struc_table =
      {
       "part" =>
         {
          "level" => 1,
          "start_cmd" => "part",
          "env" => "_part",
          "xml_element" => "part",
          "counter" => "part",
          "include_in_toc" => TRUE,
          "doc" =>
            {
             "description" => 'Starts a part',
            }
	  },
       "part*" =>
         {
          "level" => 1,
          "start_cmd" => "part*",
          "env" => "_part*",
          "xml_element" => "part",
          "counter" => FALSE,
          "include_in_toc" => FALSE,
          "doc" =>
            {
             "description" =>
                  'Starts a part without numbering it, and without adding it ' .
                  'to the table of contents',
            }
	  },
       "chapter" =>
         {
          "level" => 2,
          "start_cmd" => "chapter",
          "env" => "_chapter",
          "xml_element" => "chapter",
          "counter" => "chapter",
          "include_in_toc" => TRUE,
          "doc" =>
            {
             "description" => 'Starts a chapter.',
            }
	  },
       "chapter*" =>
         {
          "level" => 2,
          "start_cmd" => "chapter*",
          "env" => "_chapter*",
          "xml_element" => "chapter",
          "counter" => FALSE,
          "include_in_toc" => FALSE,
          "doc" =>
            {
             "description" =>
                  'Starts a chapter without numbering it, and without adding it ' .
               'to the table of contents',
            }
	  },
       "section" =>
         {
          "level" => 3,
          "start_cmd" => "section",
          "env" => "_section",
          "xml_element" => "section",
          "counter" => "section",
          "include_in_toc" => TRUE,
          "doc" =>
            {
             "description" => 'Starts a section',
            }
	  },
       "section*" =>
         {
          "level" => 3,
          "start_cmd" => "section*",
          "env" => "_section*",
          "xml_element" => "section",
          "counter" => FALSE,
          "include_in_toc" => FALSE,
          "doc" =>
            {
             "description" =>
                  'Starts a section without numbering it, and without adding it ' .
               'to the table of contents',
            }
	  },
       "subsection" =>
         {
	  "level" => 4,
          "start_cmd" => "subsection",
          "env" => "_subsection",
          "xml_element" => "subsection",
          "counter" => "subsection",
          "include_in_toc" => TRUE,
          "doc" =>
            {
             "description" => 'Starts a subsection',
            }
	  },
       "subsection*" =>
         {
          "level" => 4,
          "start_cmd" => "subsection*",
          "env" => "_subsection*",
          "xml_element" => "subsection",
          "counter" => FALSE,
          "include_in_toc" => FALSE,
          "doc" =>
            {
             "description" =>
                  'Starts a subsection without numbering it, and without adding it ' .
                  'to the table of contents',
            }
	  },
       "subsubsection" =>
         {
          "level" => 5,
          "start_cmd" => "subsubsection",
          "env" => "_subsubsection",
          "xml_element" => "subsubsection",
          "counter" => "subsubsection",
          "include_in_toc" => TRUE,
          "doc" =>
            {
             "description" => 'Starts a subsubsection',
            }
	  },
       "subsubsection*" =>
         {
          "level" => 5,
          "start_cmd" => "subsubsection*",
          "env" => "_subsubsection*",
          "xml_element" => "subsubsection",
          "counter" => FALSE,
          "include_in_toc" => FALSE,
          "doc" =>
            {
             "description" =>
                  'Starts a subsubsection without numbering it, and without adding it ' .
                  'to the table of contents',
            }
	  },
    };
  }

# --------------------------------------------------------------------------------
#1 Auxiliaries
# --------------------------------------------------------------------------------

sub get_deepest_struc_level
  {
    my $struc_table = ( $_[0] || $scan_proc->{struc_table} );
    my $deepest_level = -1;

    foreach my $struc (values(%{$struc_table}))
      {
	if ( $struc->{level} > $deepest_level )
	  {
	    $deepest_level = $struc->{level};
	  }
      }

    log_message("\nget_deepest_struc_level\n");
    log_data("Struc. table", $struc_table, "Deepest level", $deepest_level);

    return($deepest_level);
  }

sub get_strucs_with_level
  {
    my $level = $_[0];
    my $struc_table = ( $_[1] || $scan_proc->{struc_table} );
    my @strucs = ();

    log_message("\nget_strucs_with_level\n");
    log_data("Level", $level);

    foreach my $struc (values(%{$struc_table}))
      {
	if ( $struc->{level} == $level )
	  {
	    push(@strucs, $struc);
	  }
      }

    return(@strucs);
  }

sub get_struc_names_with_level
  {
    my $level = $_[0];
    my $struc_table = ( $_[1] || $scan_proc->{struc_table} );
    my @struc_names = ();

    log_message("\nget_struc_names_with_level 1/2\n");
    log_data("Level", $level);

    foreach my $struc_name (keys(%{$struc_table}))
      {
	if ( $struc_table->{$struc_name}->{level} == $level )
	  {
	    push(@struc_names, $struc_name);
	  }
      }

    log_message("\nget_struc_names_with_level 2/2\n");
    log_data("Struc. names", join(' ', @struc_names));

    return(@struc_names);
  }

sub inside_struc
  # ($struc_name)
  # Returns \code{TRUE} if the current scan position is inside a \code{$struc_name}, otherwise
  # \code{FALSE}. \code{$struc_name} must be the name of a structuring unit, e.g. \code{section},
  # \code{subsection}.
  {
    my $struc_name = $_[0];
    if ( ( exists($scan_proc->{current_struc}) )
	 && ( $scan_proc->{current_struc} eq $struc_name ) )
      {
	return(TRUE);
      }
    else
      {
	return(FALSE);
      }
  }

sub check_env_struc_nesting
  {
    my $current_struc_name = $scan_proc->{current_struc};
    my $current_struc_env = $scan_proc->{struc_table}->{$current_struc_name}->{env};
    if ( $scan_proc->{current_env} eq $current_struc_env )
      {
	return(TRUE);
      }
    else
      {
	&{$scan_proc->{error_handler}}
	  ("Improper nesting: ", appr_env_notation($scan_proc->{current_env}),
	   " must be closed within current $current_struc_name.");
	return(FALSE);
      }
  }

# --------------------------------------------------------------------------------
#1 Starting / closing structures
# --------------------------------------------------------------------------------

sub start_struc
  {
    my ($struc_name, $name_output_list, $id) = @_;

    log_message("\nstart_struc 1/2\n");
    log_data('Structure', $struc_name);

    my $no = FALSE;
    my $struc = $scan_proc->{struc_table}->{$struc_name};
    my $attribs = {};

    if ( $struc->{include_in_toc} )
      {
	$id ||= next_default_id();
	$attribs->{id} = $id;
      }

    if ( $struc->{counter} )
      {
	add_to_counter($struc->{counter}, 1);
	foreach my $nested_struc (get_strucs_with_level($struc->{level} + 1))
	  {
	    if ( $nested_struc->{counter} )
	      {
		set_counter($nested_struc->{counter}, 0);
	      }
	  }
	$no = get_counter_value($struc->{counter});
	$attribs->{no} = $no;
      }

    start_xml_element($struc->{xml_element}, $attribs, 'DISPLAY');
    start_xml_element('headline', {}, 'INLINE');
    append_to_output(@{$name_output_list});
    close_xml_element('headline', 'INLINE');

    if ( $struc->{include_in_toc} )
      {
	my $href = ($scan_proc->{toc_target_name} or '') . '#' . $id;
	temporarily_other_output_list
	  (
	   $scan_proc->{toc_output_list},
	   sub
	     {
	       my $attribs
		 = {
		    'class' => $struc_name,
		    'no' => $no,
		    'href' => $href,
		   };
	       start_xml_element('toc-item', $attribs, 'DISPLAY');
	       start_xml_element('toc-entry', {}, 'INLINE');
	       append_to_output(@{$name_output_list});
	       close_xml_element('toc-entry', 'INLINE');
	     }
	  );
      }

    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_struc} = $struc_name;
    $scan_proc->{current_env} = $struc->{env};

    my @child_struc_cmds = ();
    foreach my $child_struc (get_strucs_with_level($struc->{level} + 1))
      {
	push(@child_struc_cmds, $child_struc->{start_cmd});
      }
    install_cmds(\@child_struc_cmds, 'struc');

    log_message("\nstart_struc 2/2\n");
  }

sub close_struc
  # ($struc_name)
  {
    my $struc_name = $_[0];
    log_message("\nclose_struc 1/2\n");
    log_data("Structure", $struc_name);
    if ( check_env_struc_nesting() )
      {
	my $struc = $scan_proc->{struc_table}->{$struc_name};
	close_xml_element($struc->{xml_element}, "DISPLAY");
	reset_scan_proc();
	if ( $struc->{include_in_toc} )
	  {
	    temporarily_other_output_list
	      (
	       $scan_proc->{toc_output_list},
	       sub
	         {
		  close_xml_element("toc-item", "DISPLAY");
	         }
	      );
	  }
      }
    log_message("\nclose_struc 2/2\n");
  }

sub get_current_struc_level
  # ()
  {
    my $current_struc_level =
      ( exists($scan_proc->{current_struc})
	? $scan_proc->{struc_table}->{$scan_proc->{current_struc}}->{level}
	: FALSE );

    return($current_struc_level);
  }

sub close_strucs
  # ([$level])
  {
    my $level = ($_[0] || 1);

    log_message("\nclose_strucs 1/2\n");
    log_data('Level', $level,
	     'Current level', get_current_struc_level(),
	     'Current struc', $scan_proc->{current_struc},
	     'Current env',  $scan_proc->{current_env});

    if ( $scan_proc->{current_env} eq '_paragraph' )
      {
	close_par();
      }

    while ( get_current_struc_level() >= $level )
      {
	close_struc($scan_proc->{current_struc});
      }

    log_message("\nclose_strucs 2/2\n");
  }

# --------------------------------------------------------------------------------
#1 Structure commands (\section, \subsection, etc.)
# --------------------------------------------------------------------------------

sub execute_struc_cmd
  # ($struc_name, $opt_args, $man_args, $pos_opt_args, $pos_man_args)
  {
    my ($struc_name, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $struc = $scan_proc->{struc_table}->{$struc_name};

    log_message("\nexecute_struc_cmd 1/2\n");
    log_data("Struc", $struc_name);

    my $name_output_list
      = convert_arg(0, $man_args, $pos_man_args, "CMD", $struc->{start_cmd}, "headline_arg");

    my $id = FALSE;
    if ( $opt_args->[0] )
      {
	$id = get_data_from_arg
	  (0, $opt_args, $pos_opt_args, "CMD", $struc->{start_cmd}, "id_arg");
      }

    close_strucs($struc->{level});
    start_struc($struc_name, $name_output_list, $id);

    log_message("\nexecute_struc_cmd 2/2\n");
  }

sub enter_cmd_for_struc
  {
    my ($struc_name, $cmd_table) = @_;

    log_message("\nenter_cmd_for_struc 1/2\n");
    log_data("Structure", $struc_name, "Cmd. table", $cmd_table);

    if ( ! exists($scan_proc->{struc_table}->{$struc_name}) )
      {
	notify_programming_error("No such entry in structure table: $struc_name");
      }

    my $struc = $scan_proc->{struc_table}->{$struc_name};

    $cmd_table->{$struc->{start_cmd}}
      = {
	 "num_opt_args" => 1,
	 "num_man_args" => 1,
	 "is_par_starter" => FALSE,
	 "execute_function" => sub { execute_struc_cmd($struc_name, @_) },
	 "pre_headline_arg_hook" => sub { $scan_proc->{par_enabled} = FALSE },
	 "is_par_closer" => TRUE,
	};

    log_message("\nenter_cmd_for_struc 2/2\n");
  }

sub create_counter_for_struc
  {
    my ($struc_name) = @_;

    log_message("\ncreate_counter_for_struc 1/2\n");
    log_data("Structure", $struc_name);

    if ( ! exists($scan_proc->{struc_table}->{$struc_name}) )
      {
	notify_programming_error("No such entry in structure table: $struc_name");
      }

    my $struc = $scan_proc->{struc_table}->{$struc_name};

    @dep_from = ();
    foreach my $parent_struc (get_strucs_with_level($struc->{level} - 1))
      {
	push(@dep_from, $parent_struc->{counter}) if ( $parent_struc->{counter} );
      }

    new_counter($struc->{counter}, \@dep_from);

    log_message("\ncreate_counter_for_struc 2/2\n");
  }

# --------------------------------------------------------------------------------
# Command `\title'
# --------------------------------------------------------------------------------

sub execute_title
  # ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_title\n");

    if ( $scan_proc->{cmd_table}->{title}->{already_called} )
      {
	&{$scan_proc->{error_handler}}
	  ("Command allowed only once and already called: \\title");
      }
    else
      {
	my $name_output_list
	  = convert_arg(0, $man_args, $pos_man_args, "CMD",
			sub { $scan_proc->{par_enabled} = FALSE; }, undef());

	start_xml_element("title", {}, "SEMI_DISPLAY");
	append_to_output(@{$name_output_list});
	close_xml_element("title", "INLINE");

	$scan_proc->{cmd_table}->{title}->{already_called} = TRUE;
      }
  }

# --------------------------------------------------------------------------------
# Command `\subtitle'
# --------------------------------------------------------------------------------

sub execute_subtitle
  # ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_subtitle\n");

    if ( $scan_proc->{cmd_table}->{subtitle}->{already_called} )
      {
	&{$scan_proc->{error_handler}}
	  ("Command allowed only once and already called: \\subtitle");
      }
    else
      {
	my $name_output_list
	  = convert_arg(0, $man_args, $pos_man_args, "CMD",
			sub { $scan_proc->{par_enabled} = FALSE; }, undef());

	start_xml_element("subtitle", {}, "SEMI_DISPLAY");
	append_to_output(@{$name_output_list});
	close_xml_element("subtitle", "INLINE");

	$scan_proc->{cmd_table}->{subtitle}->{already_called} = TRUE;
      }
  }

# ------------------------------------------------------------------------------------------
# Environment `authors'
# ------------------------------------------------------------------------------------------

sub begin_authors
  {
    log_message("\nbegin_authors\n");
    start_xml_element("authors", {}, "DISPLAY");
    install_cmds(['author'], 'struc');
  }

sub end_authors
  {
    log_message("\nend_authors\n");
    close_xml_element("authors", "DISPLAY");
  }

# --------------------------------------------------------------------------------
# Command `\author'
# --------------------------------------------------------------------------------

sub execute_author
  {
    log_message("\nexecute_author\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args, $xml_element_name) = @_;

    # Retrieving email if necessary
    my $email = FALSE;
    if ( $opt_args->[0] )
      {
	$email = get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD", "author", "target_arg");
      }

    # Setting up attributes
    my $attribs = {};
    if ( $email )
      {
	$attribs->{email} = $email;
      }

    # Opening XML element
    start_xml_element("author", $attribs, "DISPLAY");

    # Getting author
    my $author_output_list
      = convert_arg(0, $man_args, $pos_man_args, "CMD",
                    sub { $scan_proc->{par_enabled} = FALSE; }, undef());

    # Appending author to XML
    append_to_output(@{$author_output_list});

    #Closing XML element
    close_xml_element("author", "DISPLAY");
  }

# --------------------------------------------------------------------------------
# Command `\version'
# --------------------------------------------------------------------------------

sub execute_version
  # ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    log_message("\nexecute_version\n");

    if ( $scan_proc->{cmd_table}->{version}->{already_called} )
      {
	&{$scan_proc->{error_handler}}
	  ("Command allowed only once and already called: \\version");
      }
    else
      {
	my $version = get_data_from_arg
	  (0, $man_args, $pos_man_args, "CMD", "version", "arg");

	start_xml_element("version", {}, "SEMI_DISPLAY");
        xml_pcdata($version);
	close_xml_element("version", "INLINE");

	$scan_proc->{cmd_table}->{version}->{already_called} = TRUE;
      }
  }

# --------------------------------------------------------------------------------
# Command `\tableofcontents'
# --------------------------------------------------------------------------------

sub execute_tableofcontents
  {
    log_message("\nexecute_tableofcontents\n");

    if ( $scan_proc->{cmd_table}->{tableofcontents}->{already_called} )
      {
	&{$scan_proc->{error_handler}}
	  ("Command allowed only once and already called: \\tableofcontents");
      }
    else
      {
	append_to_output
	  (
	   [ sub
	     {
	       if ( @{$scan_proc->{toc_output_list}} )
		 {
		   exec_func_calls
		     (
		      [\&write_xml_start_tag, "toc", {}, "DISPLAY"],
		      @{$scan_proc->{toc_output_list}},
		      [\&write_xml_end_tag, "toc", "DISPLAY"]
		     );
		 }
	     }
	   ]
	  );
	$scan_proc->{cmd_table}->{tableofcontents}->{already_called} = TRUE;
      }
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{struc}->{initializer} = sub
  {
    require_lib("counter");

    init_default_struc_table();
    $scan_proc->{struc_table} ||= $default_struc_table;

    my $struc_cmd_table
      = {
	 "title" =>
	   {
	    "num_opt_args" => 0,
	    "num_man_args" => 1,
	    "is_par_starter" => FALSE,
	    "execute_function" => \&execute_title,
	    "already_called" => FALSE,
	    "doc" =>
	      {
	       "man-args" => [['title','Text to be displayed as title']],
	       "description" => "Adds a title, command is only allowed once"
	      }
	   },
	 "subtitle" =>
	   {
	    "num_opt_args" => 0,
	    "num_man_args" => 1,
	    "is_par_starter" => FALSE,
	    "execute_function" => \&execute_subtitle,
	    "already_called" => FALSE,
	    "doc" =>
	      {
	       "man-args" => [['subtitle','Text to be displayed as subtitle']],
	       "description" => "Adds a subtitle, command is only allowed once"
	      }
	   },
	 "author" =>
	   {
	    "num_opt_args" => 1,
	    "num_man_args" => 1,
	    "is_par_starter" => FALSE,
	    "execute_function" => \&execute_author,
	    "doc" =>
	      {
	       "man_args" => [['author','The author\'s name']],
	       "opt_args" => [['email','The author\'s email']],
	       "description" => "Adds an author"
	      }
	   },
	 "version" =>
	   {
	    "num_opt_args" => 0,
	    "num_man_args" => 1,
	    "is_par_starter" => FALSE,
	    "execute_function" => \&execute_version,
	    "already_called" => FALSE,
	    "doc" =>
	      {
	       "man_args" => [['version','The version of this document']],
	       "description" => "Adds the version. Command is only allowed once"
	      }
	   },
	 "tableofcontents" =>
          {
	   "num_opt_args" => 0,
	   "num_man_args" => 0,
	   "is_par_starter" => FALSE,
	   "execute_function" => \&execute_tableofcontents,
	   "already_called" => FALSE,
	   "doc" =>
	     {
	      "description" => "Adds a table of contents, command is only allowed once"
	     }
	  }
	};

    my $struc_env_table =
      {
       'authors' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'begin_function' => \&begin_authors,
          'end_function' => \&end_authors,
          'doc' =>
             {
              'description' => 'List of authors of the document. ' .
                               'Each author is declared by the \\author command.',
             }
         }
      };

    my $deepest_level = get_deepest_struc_level();
    for (my $level = 0; $level <= $deepest_level; $level++)
      {
	foreach my $struc_name (get_struc_names_with_level($level))
	  {
	    enter_cmd_for_struc($struc_name, $struc_cmd_table);
	    if ( $scan_proc->{struc_table}->{$struc_name}->{counter} )
	      {
		create_counter_for_struc($struc_name);
	      }
	  }
      }


    my @struc_cmds = keys(%{$struc_cmd_table});
    my @toplevel_struc_cmds = grep($_ ne 'author', @struc_cmds);
    my @struc_envs = keys(%{$struc_env_table});
    deploy_lib
      ('struc',
       $struc_cmd_table,
       $struc_env_table,
       {
        'STRUC' => \@struc_cmds,
        'TOPLEVEL' => \@toplevel_struc_cmds,
        'NOT_IN_DATA' => \@struc_cmds,
       },
       {
        'STRUC' => \@struc_envs,
        'TOPLEVEL' => \@struc_envs,
        'NOT_IN_DATA' => \@struc_envs,
       },
      );

    $scan_proc->{toc_target_name} = FALSE;
    $scan_proc->{toc_output_list} = [];
  };

return(TRUE);
