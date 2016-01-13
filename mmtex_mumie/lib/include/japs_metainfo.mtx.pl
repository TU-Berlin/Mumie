# Author: Tilman Rassy <rassy@math.tu-berlin.de>
# $Id: japs_metainfo.mtx.pl,v 1.38 2009/09/22 11:57:34 rassy Exp $

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

# ------------------------------------------------------------------------------------------
#1 Description
# ------------------------------------------------------------------------------------------
#
# Implements commands and environments to set metainfos of Japs  documents. Provides the
# following:
#L
# - Initialization of the metainfos
# - Top-level metainfo environment
# - Metainfo commands and environments
# - Output of the metainfos as XML
#/L

# ------------------------------------------------------------------------------------------
#1 Internal representation of metainfos
# ------------------------------------------------------------------------------------------
#
# Metainformations are represented as a hash of the following form:
#H
#  type          & (Reference to a) hash containing at least one of the keys "id" or
#                  "name", meaning the numerical code resp. string name of the document
#                  type (actually, this is not a metainfo).\\
#  path          & Path of the document (if any). \\
#  lid           & Local is of the document (only if a reference). \\
#  category      & (Reference to a) hash containing at least one of the keys "id" or
#                  "name", meaning the numerical code resp. string name of the category.\\
#  use_mode      & Use mode of the document. Optional. \\
#  name          & Name of the document. \\
#  description   & Description of the document. \\
#  changelog     & Changelog of the document. \\
#  copyright     & Copyright information about the document. \\
#  components    & Reference to a list of documents represented in a way described below. \\
#  links         & Reference to a list of documents represented in a way described below. \\
#  attachable    & Reference to a list of documents represented in a way described below. \\
#/H
#
# The documents in the components, links, and attachable lists are again (references to)
# hashes of the above form, except that they contain only the type, lid (optional), and
# path entries.
#
# The variable $global_data->{metainfo} holds a reference to that hash.

log_message("\nLibrary \"japs_metainfo\" ", '$Revision: 1.38 $ ', "\n");

# --------------------------------------------------------------------------------
# Initializing the metainfos
# --------------------------------------------------------------------------------

#Ds
#a ()
# Initializes the metainfo hash, i.e., $global_data->{metainfo}, by attaching
# default values to some (but not all) keys.

sub japs_initialize_metainfo
  {
    log_message("\njaps_initialize_metainfo 1/2\n");

    $global_data->{metainfo} = {};
    my $metainfo = $global_data->{metainfo};

    $metainfo->{type}->{name} = $global_data->{document_type_name};
    $metainfo->{category}->{name} = $global_data->{category};
    $metainfo->{copyright} = $params{copyright};
    $metainfo->{content_type} = {'type' => 'text', 'subtype' => 'xml'};
    if ( $global_data->{document_type_name} eq 'problem' )
      {
        if ( $global_data->{category} eq 'mchoice' )
          {
            $metainfo->{corrector} = {path => $params{mchoice_corrector_path}}
          }
        elsif ( $global_data->{category} eq 'traditional' )
          {
            $metainfo->{corrector} = {path => $params{traditional_corrector_path}}
          }
      }

    log_message("\njaps_initialize_metainfo 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "metainfo" environment
# --------------------------------------------------------------------------------

#Ds
#a ()
# Begin function of the 'metainfo' environment.

sub japs_begin_metainfo
  {
    log_message("\njaps_begin_metainfo 1/2\n");

    japs_register_occurrence('metainfo');
    japs_check_occurrence('metainfo', 'metainfo', 1, 1);

    $scan_proc->{mode} = 'NORMAL';

    my @metainfo_token_types
      = (
	 "ign_whitesp",
	 "cmd",
	 "one_char_cmd",
	 "comment"
	);
    $scan_proc->{allowed_tokens} = \@metainfo_token_types;

    japs_initialize_metainfo();

    install_cmds('METAINFO_TOPLEVEL', 'japs_metainfo');
    install_envs('METAINFO_TOPLEVEL', 'japs_metainfo');
    install_cmds(['corrector'], 'japs_metainfo') if ( $global_data->{has_corrector} );
    install_cmds(['timeframe'], 'japs_metainfo') if ( $global_data->{has_timeframe} );
    install_cmds(['class'], 'japs_metainfo') if ( $global_data->{has_class} );
    install_cmds(['genericsummary'], 'japs_metainfo') if ( $global_data->{has_summary} );

    log_message("\njaps_begin_metainfo 2/2\n");
  }

#Ds
#a ()
# End function of the 'metainfo' environment.

sub japs_end_metainfo
  {
    log_message("\njaps_end_metainfo 1/2\n");

    # Check if the mandatory metainfos have been set:
    japs_check_occurrence('name', '\name', 1, 1);

    japs_metainfo_to_xml();
    log_message("\njaps_end_metainfo 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\name" and "\copyrightinfo" commands
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\name' command. The command has the form
#c
#  \name{NAME}
#/c

sub japs_execute_name
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $value = get_data_from_arg
      (0, $man_args, $pos_man_args, 'CMD',
       sub { $scan_proc->{allowed_tokens} = [@basic_token_types, 'unparsed_math_boundry'] },
       sub {});

    log_message("\njaps_execute_name 1/2\n");
    log_data('Value', $value);
    japs_check_screen_width($value);
    $global_data->{metainfo}->{name} = $value;
    japs_register_occurrence('name');
    japs_check_occurrence('name', '\name', 1, 1);
    log_message("\njaps_execute_name 2/2\n");
  }

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\copyrightinfo' command. The command has the form
#c
#  \copyrightinfo{COPYRIGHTINFO}
#/c

sub japs_execute_copyrightinfo
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $value = $man_args->[0];

    # Workaround:
    chars_to_numcodes(\$value);

    log_message("\njaps_execute_copyrightinfo 1/2\n");
    log_data('Value', $value);
    japs_check_screen_width($value);
    $global_data->{metainfo}->{copyright} = $value;
    japs_register_occurrence('copyrightinfo');
    japs_check_occurrence('copyrightinfo', '\copyrightinfo', 0, 1);
    log_message("\njaps_execute_copyrightinfo 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\status" command
# --------------------------------------------------------------------------------

#Ds
#a ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Execute function of the '\status' command. Since the command is deprecated, only
# a warning is issued.

sub japs_execute_status
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_status 1/2\n");
    &{$scan_proc->{warning_handler}}('Command \\status is obsolete. Will be ignored.');
    log_message("\njaps_execute_status 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\corrector" command
# --------------------------------------------------------------------------------

sub japs_execute_corrector
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_corrector 1/2\n");
    japs_register_occurrence('corrector');
    japs_check_occurrence('corrector', '\corrector', 0, 1);
    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', 'corrector', 'path_arg');
    japs_check_master_filename($path);
    $global_data->{metainfo}->{corrector} = {path => $path};
    log_message("\njaps_execute_corrector 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "description" environment
# --------------------------------------------------------------------------------

#Ds
#a ()
# Starts the 'description' environment. Establishes a new output list to collect
# the description text and sets-up the scan process appropriately. The collected
# text is added to metainfo hash ('$global_data->{metainfo}') in the corresponding
# close environment function, `#func:japs_end_description japs_end_description`.
#
# The content of the description is expected to be plain text without XML markup.
# To this end, markup is disabled. Paragraphs are disabled as well. The allowed
# token types are the basic token types
# (`lib/perl/Mumie/MmTeX/Parser.pm#var:@basic_token_types @basic_token_types`)
# plus the 'unparsed_math_boundry' token type. The latter is for adding '$...$'
# literally to the description text.

sub japs_begin_description
  {
    log_message("\njaps_begin_description 1/2\n");

    # Setup output list:
    $scan_proc->{prim_output_list} = [];
    $scan_proc->{output_list} = $scan_proc->{prim_output_list};

    # Setup tokens:
    $scan_proc->{allowed_tokens} = [@basic_token_types, 'unparsed_math_boundry'];

    # Disable nested 'description' environments:
    $scan_proc->{env_table}->{description}->{begin_disabled} = TRUE;

    # Disable paragraphs and markup:
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{markup_disabled} = TRUE;

    log_message("\njaps_begin_description 2/2\n");
  }

#Ds
#a ()
# Obtains the description text as a string by evaluating the output list and
# adds it to metainfo hash ('$global_data->{metainfo}'). Registers and checks
# the ocurrence of the 'description' environment.

sub japs_end_description
  {
    log_message("\njaps_end_description 1/2\n");
    reset_xml_writer();
    exec_func_calls(@{$scan_proc->{output_list}});
    my $value = join('', @xml_output_list);
    japs_check_screen_width($value);
    $global_data->{metainfo}->{description} = $value;
    japs_register_occurrence('description');
    japs_check_occurrence('description' , 'description', 0, 1);
    log_message("\njaps_end_description 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "changelog" environment
# --------------------------------------------------------------------------------

#Ds
#a ()
# Starts a 'changelog' environment. Establishes a new output list to collect the
# changelog text and sets-up the scan process appropriately. The collected text
# is added to metainfo hash ('$global_data->{metainfo}') in the corresponding close
# environment function, `#func:japs_end_changelog japs_end_changelog`.
#
# The 'changelog' environment is similar to the 'verbatim' environment. The literal
# code until the next "\end{changelog}" token is the description text. To obtain
# it, a new token type "changelog_value" is defined (locally), which becomes the
# only allowed token type. The token matches arbitray text up to the next
# occurrence of "\end{changelog}".

sub japs_begin_changelog
  {
    log_message("\njaps_begin_changelog 1/2\n");

    $scan_proc->{prim_output_list} = [];
    $scan_proc->{output_list} = $scan_proc->{prim_output_list};
    $scan_proc->{env_table}->{changelog}->{begin_disabled} = TRUE;

    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{forced_layout} = "STRICTLY_VERBATIM";
    $scan_proc->{token_table} =
      {
       "changelog_value" =>
         {
	  "tester" => sub { test_regexp("([\\s\\S]*?)(?=\\\\end{changelog})") },
	  "handler" => sub
	    {
	      &{$scan_proc->{output_functions}->{plain_text}}($scan_proc->{last_token});
	      reset_scan_proc();
	    }
	 }
      };
    $scan_proc->{allowed_tokens} = ["changelog_value"];
    $scan_proc->{scan_failed_handler} = sub
      {
	&{$scan_proc->{error_handler}}("Can't find end of environment: changelog");
      };

    log_message("\njaps_begin_changelog 2/2\n");
  }

#Ds
#a ()
# Obtains the changelog text as a string by evaluating the output list and
# adds it to metainfo hash ('$global_data->{metainfo}'). Registers and checks
# the ocurrence of the 'changelog' environment.

sub japs_end_changelog
  {
    log_message("\njaps_end_changelog 1/2\n");
    reset_xml_writer();
    exec_func_calls(@{$scan_proc->{output_list}});
    my $value = join('', @xml_output_list);

    # Workaround:
    chars_to_numcodes(\$value);

    $global_data->{metainfo}->{changelog} = $value;
    japs_register_occurrence('changelog');
    japs_check_occurrence('changelog' , 'changelog', 0, 1);
    log_message("\njaps_end_changelog 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "components", "links", "attachable", and "authors" environments
# --------------------------------------------------------------------------------

#Ds
#a ($env, $element, $cmds)
# Common part of the implementations of the begin of the environments 'components',
# 'links', 'attachable', and 'authors'. $env is the environment name and $element the
# corresponding XML element name. $cmds specifies commands specific to the environment
# (e.g., the '\component' in case of the 'components' environment). It must be a
# reference to a list of command names. These commands are installed by this function.

sub japs_start_ref_env
  {
    log_message("\njaps_start_ref_env 1/2\n");

    my ($env, $element, $cmds) = @_;

    $scan_proc->{cmd_table} = {};
    $scan_proc->{env_table} = {};
    install_cmds($cmds, 'japs_metainfo');
    install_envs([$env], 'japs_metainfo');
    $scan_proc->{env_table}->{$env}->{begin_disabled} = TRUE;
    $scan_proc->{current_metainfo_element} = $element;

    log_message("\njaps_start_ref_env 2/2\n");
  }

#Ds
#a ($env, $element, $cmds)
# Common part of the implementations of the end of the environments 'components',
# 'links', 'attachable', and 'authors'. Currently, this function does nothing except
# writing a log message.

sub japs_close_ref_env
  {
    log_message("\njaps_close_ref_env\n");
  }

#Ds
#a ()
# Begin function of the 'components' environment.

sub japs_begin_components
  {
    japs_start_ref_env
      ('components', 'components',
       ['component', 'compcsc', 'compwks', 'compgelm', 'compgsel', 'compgprb']);
  }

#Ds
#a ()
# End function of the 'components' environment.

sub japs_end_components
  {
    japs_close_ref_env();
    japs_register_occurrence('components');
    japs_check_occurrence('components', 'components', 0, 1);
  }

#Ds
#a ()
# Begin function of the 'links' environment.

sub japs_begin_links
  {
    japs_start_ref_env('links', 'links', ['link']);
  }

#Ds
#a ()
# End function of the 'links' environment.

sub japs_end_links
  {
    japs_close_ref_env();
    japs_register_occurrence('links');
    japs_check_occurrence('links', 'links', 0, 1);
  }

#Ds
#a ()
# Begin function of the 'attachable' environment.

sub japs_begin_attachable
  {
    japs_start_ref_env('attachable', 'attachable', ['attachableto']);
  }

#Ds
#a ()
# End function of the 'attachable' environment.

sub japs_end_attachable
  {
    japs_close_ref_env();
    japs_register_occurrence('attachable');
    japs_check_occurrence('attachable', 'attachable', 0, 1);
  }

#Ds
#a ()
# Begin function of the 'authors' environment.

sub japs_begin_authors
  {
    japs_start_ref_env('authors', 'authors', ['author']);
  }

#Ds
#a ()
# End function of the 'authors' environment.

sub japs_end_authors
  {
    japs_close_ref_env();
    japs_register_occurrence('authors');
    japs_check_occurrence('authors', 'authors', 0, 1);
  }

# --------------------------------------------------------------------------------
# The "\component", "\compxxx", \link", and "\attachableto" commands
# --------------------------------------------------------------------------------

#Ds
#a ($cmd, $opt_args, $man_args, $pos_opt_args, $pos_man_args)
# Implements the commands '\component', '\link', and '\attachableto'. The command
# name is the first parameter ($cmd). The commands have the form
#c
#  \component{TYPE}{PATH}{LID}
#  \link{TYPE}{PATH}{LID}
#  \attachableto{TYPE}{PATH}
#/c
# Creates a representation of the referenced document as described in "Internal
# representation of metainfos" (see above). Thus, the representation is a hash
# containing the entries 'type', 'path', and possibly 'lid'. The latter entry is
# added only of the command has three mandatory arguments. The hash is then added
# (as a reference) to @{$global_data->{metainfo}->{$element}}, where $element is
# set to $scan_proc->{current_metainfo_element}.

sub japs_execute_ref_cmd
  {
    log_message("\njaps_execute_ref_cmd 1/2\n");

    my ($cmd, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $element = $scan_proc->{current_metainfo_element};
    my $child = {};

    my $type = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', $cmd, 'type_arg');
    if ( ! grep($_ eq $type, @{$scan_proc->{cmd_table}->{$cmd}->{types}}) )
      {
        my $message = "Invalid type: $type";
        my $special_cmd = $japs_types_vs_comp_cmds{$type};
        if ( $special_cmd )
          { $message .=  " (use \\$special_cmd to include documents of that type)"; }
        &{$scan_proc->{error_handler}}($message);
      }
    $child->{type} = $type;

    my $path = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD', $cmd, 'path_arg');
    japs_check_master_filename($path);
    $child->{path} = $path;

    my $lid = undef();
    if ( $scan_proc->{cmd_table}->{$cmd}->{num_man_args} == 3 )
      {
        $lid = get_data_from_arg(2, $man_args, $pos_man_args, 'CMD', $cmd, 'lid_arg');
        if ( grep($lid eq $_, japs_get_all_lids()) )
          { &{$scan_proc->{error_handler}}("Local id already in use: $lid"); }
        $child->{lid} = $lid;
      }

    push(@{$global_data->{metainfo}->{$element}}, $child);

    log_message("\njaps_execute_ref_cmd 2/2\n");
    log_data('Element', $element, 'Type', $type, 'LID', $lid, 'Child', $child);
  }

sub japs_execute_compxxx
  {
    log_message("\njaps_execute_compxxx 1/2\n");

    my ($cmd, $opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $element = $scan_proc->{current_metainfo_element};
    my $child = {};

    my $doctype = $global_data->{document_type_name};
    my @allowed_doctypes = @{$scan_proc->{cmd_table}->{$cmd}->{doctypes}};
    grep($doctype eq $_, @allowed_doctypes) ||
      &{$scan_proc->{error_handler}}("\\$cmd only allowed in ", join(', ', @allowed_doctypes));

    my $type = $japs_comp_cmds_vs_types{$cmd};
    $type || notify_programming_error('japs_execute_compxxx', " Invalid command: $cmd");
    $child->{type} = $type;

    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', $cmd, 'path_arg');
    japs_check_master_filename($path);
    $child->{path} = $path;

    my $lid = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD', $cmd, 'lid_arg');
    if ( grep($lid eq $_, japs_get_all_lids()) )
      { &{$scan_proc->{error_handler}}("Local id already in use: $lid"); }
    $child->{lid} = $lid;

    my $label = get_data_from_arg(2, $man_args, $pos_man_args, 'CMD', $cmd, 'label_arg');
    $child->{label} = $label;

    if ( $cmd eq 'compgprb' )
      {
        my $points = get_data_from_arg(3, $man_args, $pos_man_args, 'CMD', $cmd, 'points_arg');
        $points =~ m/^[0-9]+(?:\.[0-9]+)?$/ ||
          &{$scan_proc->{error_handler}}("Invalid point number: $points");
        $child->{points} = $points;
      }

    push(@{$global_data->{metainfo}->{$element}}, $child);

    log_message("\njaps_execute_compxxx 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\generic" and "\creategeneric" commands
# --------------------------------------------------------------------------------

sub japs_execute_generic
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_generic 1/2\n");
    japs_register_occurrence('generic');
    japs_check_occurrence('generic', '\generic', 0, 1);
    if ( japs_get_occurrence('creategeneric') > 0 )
      {
        &{$scan_proc->{error_handler}}
          ('\generic is not allowed if \creategeneric was already used');
      }
    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD', 'generic', 'path_arg');
    japs_check_master_filename($path);
    $global_data->{metainfo}->{gdim_entries} =
      [{generic_doc_path => $path, lang_code => $scan_proc->{lang}}];
    log_message("\njaps_execute_generic 2/2\n");
  }

sub japs_execute_creategeneric
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_creategeneric 1/2\n");

    # Check ocurrence of \creategeneric:
    japs_register_occurrence('creategeneric');
    japs_check_occurrence('creategeneric', '\creategeneric', 0, 1);
    if ( japs_get_occurrence('generic') > 0 )
      {
        &{$scan_proc->{error_handler}}
          ('\creategeneric is not allowed if \generic was already used');
      }

    # Get directory and pure name of source file:
    my ($dir, $pure_name_without_lang) = japs_parse_filename($scan_proc->{prim_source_name});

    # Get path of generic master file:
    my $path;
    if ( $opt_args->[0] )
      {
        $path = get_data_from_arg(0, $opt_args, $pos_opt_args, 'CMD', 'creategeneric', 'path_arg');
        japs_check_master_filename($path);
      }
    else
      { $path = japs_get_checkin_path_of_generic($dir, $pure_name_without_lang); }

    # Set 'create_generic' flag in global data:
    $global_data->{create_generic} = TRUE;

    # Save path of generic in global data:
    $global_data->{path_of_generic} = $path;

    # Save pure name without lang code in global data (needed in japs_create_generic_document,
    # library japs_core, to resolve %p placeholders in name and description):
    $global_data->{pure_name_without_lang} = $pure_name_without_lang;

    # Create gdim entries in metainfos:
    $global_data->{metainfo}->{gdim_entries} =
      [{generic_doc_path => $path, lang_code => $scan_proc->{lang}}];

    log_message("\njaps_execute_creategeneric 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\author" command
# --------------------------------------------------------------------------------

sub japs_execute_author
  {
    log_message("\njaps_execute_author 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $element = $scan_proc->{current_metainfo_element};
    my $child = {};

    $child->{type} = 'author';

    my $path = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD', $cmd, 'path_arg');
    japs_check_master_filename($path);
    $child->{path} = $path;

    push(@{$global_data->{metainfo}->{$element}}, $child);

    log_message("\njaps_execute_author 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\timeframe" command
# --------------------------------------------------------------------------------

sub japs_execute_timeframe
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $start = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    my $end = get_data_from_arg(1, $man_args, $pos_man_args, 'CMD');
    log_message("\njaps_execute_timeframe 1/2\n");
    log_data('Start', $start, 'End', $end);
    japs_check_time_format($start, $end);
    $global_data->{metainfo}->{timeframe_start} =
      {'value' => $start, 'format' => $japs_time_format};
    $global_data->{metainfo}->{timeframe_end} =
      {'value' => $end, 'format' => $japs_time_format};
    japs_register_occurrence('timeframe');
    japs_check_occurrence('timeframe', '\timeframe', 1, 1);
    log_message("\njaps_execute_timeframe 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\class" command
# --------------------------------------------------------------------------------

sub japs_execute_class
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_class 1/2\n");
    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_check_master_filename($path);
    $global_data->{metainfo}->{class}->{path} = $path;
    japs_register_occurrence('class');
    japs_check_occurrence('class', '\class', 1, 1);
    log_message("\njaps_execute_class 2/2\n");
  }

# --------------------------------------------------------------------------------
# The "\genericsummary" command
# --------------------------------------------------------------------------------

sub japs_execute_genericsummary
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    log_message("\njaps_execute_genericsummary 1/2\n");
    my $path = get_data_from_arg(0, $man_args, $pos_man_args, 'CMD');
    japs_check_master_filename($path);
    $global_data->{metainfo}->{generic_summary}->{path} = $path;
    japs_register_occurrence('generic_summary');
    japs_check_occurrence('generic_summary', '\genericsummary', 1, 1);
    log_message("\njaps_execute_genericsummary 2/2\n");
  }

# --------------------------------------------------------------------------------
# Low-level to-XML functions
# --------------------------------------------------------------------------------

sub japs_metainfo_to_xml_text
  {
    my ($element, $layout_start, $layout_value, $layout_end, $required) = @_;
    my $value = $global_data->{metainfo}->{$element};
    if ( $value )
      {
        start_xml_element($element, {}, $layout_start);
        xml_pcdata($value, $layout_value);
        close_xml_element($element, $layout_end);
      }
    elsif ( $required )
      {
        &{$scan_proc->{error_handler}}("Missing or empty metainfo: $element ");
      }
  }

sub japs_metainfo_to_xml_attribs
  {
    my ($element, $attrib_or_ref, $required) = @_;
    my @attribs = (ref($attrib_or_ref) ? @{$attrib_or_ref} : ($attrib_or_ref));
    if ( $global_data->{metainfo}->{$element} )
      {
        my $attrib_map = {};
        foreach my $attrib (@attribs)
          {
            $value = $global_data->{metainfo}->{$element}->{$attrib};
            &{$scan_proc->{error_handler}}("Missing or empty metainfo: $element/$attrib")
              if ( ! $value );
            $attrib_map->{$attrib} = $value;
          }
        empty_xml_element($element, $attrib_map, 'DISPLAY');
      }
    elsif ( $required )
      {
        &{$scan_proc->{error_handler}}("Missing or empty metainfo: $element ");
      }
  }

sub japs_refs_to_xml
  {
    my ($element) = @_;
    log_message("\njaps_refs_to_xml 1/2\n");
    log_data('Element', $element);

    my $refs = $global_data->{metainfo}->{$element};
    if ( $refs )
      {
        start_xml_element($element, {}, 'DISPLAY');
        foreach my $ref (@{$refs})
          {
            japs_ref_to_xml($ref);
          }
        close_xml_element($element, 'DISPLAY');
      }
    log_message("\njaps_refs_to_xml 2/2\n");
  }

sub japs_ref_to_xml
  {
    my $ref = $_[0];
    log_message("\njaps_ref_to_xml 1/2\n");
    log_data('Ref', $ref);

    my $element = $ref->{type};
    &{$scan_proc->{error_handler}}("Missing (pseudo-)document type name") if ( ! $element );
    my $path = $ref->{path};
    &{$scan_proc->{error_handler}}("Missing (pseudo-)document path") if ( ! $path );
    my $lid = $ref->{lid};
    my $label = $ref->{label};
    my $points = $ref->{points};


    my $attribs = {};
    $attribs->{path} = $path;
    $attribs->{lid} = $lid if ( $lid );

    if ( $label || $points )
      {
        start_xml_element($element, $attribs, 'DISPLAY');
        if ( $label )
          { empty_xml_element
              ('ref_attribute', {'name' => 'label', 'value' => $label}, 'DISPLAY'); }
        if ( $points )
          { empty_xml_element
              ('ref_attribute', {'name' => 'points', 'value' => $points}, 'DISPLAY'); }
        close_xml_element($element, 'DISPLAY');
      }
    else
      {
        empty_xml_element($element, $attribs, 'DISPLAY');
      }

    log_message("\njaps_ref_to_xml 2/2\n");
  }

# --------------------------------------------------------------------------------
# To-XML functions for particular metainfos
# --------------------------------------------------------------------------------

sub japs_name_to_xml
  {
    japs_metainfo_to_xml_text('name', 'SEMI_DISPLAY', 'CONTINUOUS', 'INLINE', TRUE);
  }

sub japs_description_to_xml
  {
    japs_metainfo_to_xml_text('description', 'DISPLAY', 'STRICTLY_VERBATIM', 'DISPLAY', TRUE);
  }

sub japs_changelog_to_xml
  {
    japs_metainfo_to_xml_text('changelog', 'DISPLAY', 'STRICTLY_VERBATIM', 'DISPLAY', FALSE);
  }

sub japs_copyright_to_xml
  {
    japs_metainfo_to_xml_text('copyright', 'DISPLAY', 'CONTINUOUS', 'DISPLAY', FALSE);
  }

sub japs_category_to_xml
  {
    japs_metainfo_to_xml_attribs('category', 'name', TRUE);
  }

sub japs_corrector_to_xml
  {
    japs_metainfo_to_xml_attribs('corrector', 'path', FALSE);
  }

sub japs_timeframe_to_xml
  {
    japs_metainfo_to_xml_attribs('timeframe_start', ['value', 'format'], TRUE);
    japs_metainfo_to_xml_attribs('timeframe_end', ['value', 'format'], TRUE);
  }

sub japs_class_to_xml
  {
    japs_metainfo_to_xml_attribs('class', 'path', FALSE);
  }

sub japs_genericsummary_to_xml
  {
    japs_metainfo_to_xml_attribs('generic_summary', 'path', FALSE);
  }

sub japs_content_type_to_xml
  {
    japs_metainfo_to_xml_attribs('content_type', ['type', 'subtype'], TRUE);
  }

sub japs_components_to_xml
  {
    japs_refs_to_xml('components');
  }

sub japs_links_to_xml
  {
    japs_refs_to_xml('links');
  }

sub japs_authors_to_xml
  {
    japs_refs_to_xml('authors');
  }

sub japs_attachable_to_xml
  {
    japs_refs_to_xml('attachable');
  }

sub japs_gdim_entries_to_xml
  {
    log_message("\njaps_gdim_entries_to_xml 1/2\n");
    start_xml_element('gdim_entries', {}, 'DISPLAY');
    for my $entry (@{$global_data->{metainfo}->{gdim_entries}})
      {
        my $attribs = {generic_doc_path => $entry->{generic_doc_path}};
        $attribs->{lang_code} = $entry->{lang_code} if ( $entry->{lang_code} );
        empty_xml_element('gdim_entry', $attribs, 'DISPLAY');
      }
    close_xml_element('gdim_entries', 'DISPLAY');
    log_message("\njaps_gdim_entries_to_xml 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# To-XML function for the complete metainfos
# ------------------------------------------------------------------------------------------

sub japs_metainfo_to_xml
  {
    log_message("\njaps_metainfo_to_xml 1/2\n");

    my $metainfo = $_[0];
    my $root_element = $global_data->{document_type_name};

    # Switch to own output list:
    my $saved_prim_output_list = $scan_proc->{prim_output_list};
    my $saved_output_list = $scan_proc->{output_list};
    $global_data->{metainfo_output_list} = [];

    $scan_proc->{prim_output_list} = $global_data->{metainfo_output_list};
    $scan_proc->{output_list} = $scan_proc->{prim_output_list};

    # Setting the namespace:
    new_xml_namespace
      ($params{metainfo_xml_namespace}, $params{metainfo_xml_namespace_prefix});

    # XML declaration:
    xml_decl("1.0", "ASCII", undef(), "DISPLAY");

    # Starting the root element:
    start_xml_element($root_element, {}, 'DISPLAY');

    japs_category_to_xml() if ( $global_data->{has_category} );
    japs_name_to_xml();
    japs_description_to_xml();
    japs_copyright_to_xml();
    japs_changelog_to_xml();
    japs_content_type_to_xml();
    japs_corrector_to_xml() if ( $global_data->{has_corrector} );
    japs_class_to_xml() if ( $global_data->{has_class} );
    japs_genericsummary_to_xml() if ( $global_data->{has_summary} );
    japs_timeframe_to_xml() if ( $global_data->{has_timeframe} );
    japs_authors_to_xml();
    japs_components_to_xml();
    japs_links_to_xml();
    japs_attachable_to_xml();
    japs_gdim_entries_to_xml();

    # Closing root element:
    close_xml_element($root_element, 'DISPLAY');

    # Reset output lists:
    $scan_proc->{prim_output_list} = $saved_prim_output_list;
    $scan_proc->{output_list} = $saved_output_list;

    log_message("\njaps_metainfo_to_xml 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# Auxiliaries
# ------------------------------------------------------------------------------------------

sub japs_check_metainfo
  {
    my ($metainfo, $elememt, $attrib) = @_;
    if ( !$metainfo->{$element} ||
         ( $attrib && ( !$metainfo->{$element}->{$attrib} ) ) )
      {
        &{$scan_proc->{error_handler}}("Missing or empty metainfo: $element")
      }
  }

sub japs_allow_text_token
  {
    allow_tokens(['plain_text'], 'MODIFY', 'APPEND');
  }

#Ds
#a ()
# Handler for the 'unparsed_math_boundry' token. This token is simply a "$" character.
# The handler scans the code until the next (non-escaped) "$" character, and adds the
# "$...$" sequence literally to the output.

sub japs_handle_unparsed_math
  {
    log_message("\njaps_handle_unparsed_math 1/2\n");

    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{prim_output_list} = [];
    $scan_proc->{output_list} = $scan_proc->{prim_output_list};
    $scan_proc->{par_enabled} = FALSE;
    $scan_proc->{forced_layout} = "STRICTLY_VERBATIM";
    $scan_proc->{token_table} =
      {
       'unparsed_math' =>
         {
          'tester' => sub { test_regexp("([\\s\\S]*?)(?<!\\\\)\\\$", 1) },
          'handler' => sub
            {
              my $code = '$' . $scan_proc->{last_token} . '$';
              if ( $xml_special_char_output_mode eq 'AS_NUMCODES' )
                {
                  quote_xml(\$code, 'BY_NUMCODES');
                }
              elsif ( $xml_special_char_output_mode =~ m/^(?:AS_ENTITIES|LITERAL)$/ )
                {
                  quote_xml(\$code, 'BY_ENTITIES');
                }
              &{$scan_proc->{output_functions}->{plain_text}}($code);
            },
         }
      };
    $scan_proc->{allowed_tokens} = ["unparsed_math"];
    $scan_proc->{scan_failed_handler}
      = sub { &{$scan_proc->{error_handler}}("Missing closing \"\$\"") };
    Mumie::Scanner::scan_next_token();
    reset_xml_writer();
    exec_func_calls(@{$scan_proc->{output_list}});
    my $unparsed_math = join('', @xml_output_list);
    reset_scan_proc();

    &{$scan_proc->{output_functions}->{plain_text}}($unparsed_math);

    log_message("\njaps_handle_unparsed_math 2/2\n");
  }

sub japs_check_time_format
  {
    my $year = '[0-9][0-9][0-9][0-9]';
    my $month = '[0-9][0-9]';
    my $day = '[0-9][0-9]';
    my $hour = '[0-9][0-9]';
    my $minute = '[0-9][0-9]';

    for my $time (@_)
      {
        if ( $time !~ m/^$year-$month-$day $hour:$minute$/ )
          { &{$scan_proc->{error_handler}}("Invalid time fomat: $time"); }
      }
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{japs_metainfo}->{initializer} = sub
  {
    $default_token_table->{unparsed_math_boundry} =
      {
       'tester' => sub { test_regexp("(?<!\\\\)\\\$", 0) },
       'handler' => \&japs_handle_unparsed_math,
      };

    %japs_comp_cmds_vs_types =
      (
       'compcsc' => 'course_section',
       'compwks' => 'worksheet',
       'compgelm' => 'generic_element',
       'compgsel' => 'generic_subelement',
       'compgprb' => 'generic_problem',
      );

    %japs_types_vs_comp_cmds =
      (
       'course_section' => 'compcsc',
       'worksheet' => 'compwks',
       'generic_problem' => 'compgprb',
      );

    $japs_time_format = 'yyyy-MM-dd HH:mm';

    # The command table:
    my $cmd_table =
      {
       'name' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 1,
	  'execute_function' => \&japs_execute_name,
         },
       'copyrightinfo' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 1,
	  'execute_function' => \&japs_execute_copyrightinfo,
         },
       'status' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 1,
	  'execute_function' => \&japs_execute_status,
          'status_names' => $global_data->{status_names},
	  'doc' =>
	    {
	     'man_args' => [['status','Document status']],
	     'description' => 'Deprecated. The command is ignored.',
	    },
         },
       'timeframe' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 2,
	  'execute_function' => \&japs_execute_timeframe,
	  'doc' =>
	    {
	     'man_args' => [['start','Beginning of timeframe'],
                            ['end','End of timeframe']],
	     'description' => 'Sets the timeframe. Start and end times must be specified ' .
                              'in the format YYYY-MM-DD hh:mm:ss',
	    },
         },
       'corrector' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_corrector,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)']],
             'description' =>
               'Declares the corrector of this problem. %{1} must be the checkin path ' .
               'pointing to the master file of the corrector.',
            },
         },
       'class' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_class,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)']],
             'description' =>
               'Declares the class of this problem. %{1} must be the checkin path ' .
               'pointing to the master file of the class.',
            },
         },
       'genericsummary' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_genericsummary,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)']],
             'description' =>
               'Declares the summary of this problem. %{1} must be the checkin path ' .
               'pointing to the master file of the generic summary.',
            },
         },
       'component' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_ref_cmd('component', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'pre_lid_arg_hook' => \&japs_allow_text_token,
          'types' => $global_data->{component_doctype_names},
          'doc' =>
            {
             'man_args' =>
               [['type', 'Document type name'],
                ['src', 'Source (master file)'],
                ['lid', 'Local id']],
             'description' =>
               'Declares a document as a component. %{1} is the type of the document , ' .
               'as a string name. %{2} must be the checkin path pointing to the master file ' .
               'of the document. %{3}, the local id of the document, can be choosen ' .
               'arbitrarily.',
            },
         },
       'compcsc' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_compxxx('compcsc', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'pre_lid_arg_hook' => \&japs_allow_text_token,
          'doctypes' => ['course'],
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)'],
                ['lid', 'Local id'],
                ['label', 'Label in the course'],
                ],
             'description' =>
               'Declares a course section as a component of a course. %{1} must be the ' .
               'checkin path pointing to the master file of the document. %{2}, the ' .
               'local id of the document, can be choosen arbitrarily.',
            },
         },
       'compwks' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_compxxx('compwks', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'pre_lid_arg_hook' => \&japs_allow_text_token,
          'doctypes' => ['course'],
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)'],
                ['lid', 'Local id'],
                ['label', 'Label in the course'],
                ],
             'description' =>
               'Declares a worksheet as a component of a course. %{1} must be the ' .
               'checkin path pointing to the master file of the document. %{2}, the ' .
               'local id of the document, can be choosen arbitrarily.',
            },
         },
       'compgelm' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_compxxx('compgelm', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'pre_lid_arg_hook' => \&japs_allow_text_token,
          'doctypes' => ['course_section'],
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)'],
                ['lid', 'Local id'],
                ['label', 'Label in the course section'],
                ],
             'description' =>
               'Declares a generic element as a component of a course section. %{1} must ' .
               'be the checkin path pointing to the master file of the document. %{2}, the ' .
               'local id of the document, can be choosen arbitrarily.',
            },
         },
       'compgsel' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_compxxx('compgsel', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'pre_lid_arg_hook' => \&japs_allow_text_token,
          'doctypes' => ['course_section'],
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)'],
                ['lid', 'Local id'],
                ['label', 'Label in the course section'],
                ],
             'description' =>
               'Declares a generic subelement as a component of a course section. %{1} ' .
               'must be the checkin path pointing to the master file of the document. %{2}, ' .
               'the local id of the document, can be choosen arbitrarily.',
            },
         },
       'compgprb' =>
         {
          'num_man_args' => 4,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_compxxx('compgprb', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'pre_lid_arg_hook' => \&japs_allow_text_token,
          'doctypes' => ['worksheet'],
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)'],
                ['lid', 'Local id'],
                ['label', 'Label in the worksheet'],
                ['points', 'Points for this problem'],
                ],
             'description' =>
               'Declares a generic problem as a component of a worksheet. %{1} must be the ' .
               'checkin path pointing to the master file of the document. %{2}, the ' .
               'local id of the document, can be choosen arbitrarily.',
            },
         },
       'link' =>
         {
          'num_man_args' => 3,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_ref_cmd('link', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'pre_lid_arg_hook' => \&japs_allow_text_token,
          'types' => $global_data->{link_doctype_names},
          'doc' =>
            {
             'man_args' =>
               [['type', 'Document type name'],
                ['src', 'Source (master file)'],
                ['lid', 'Local id']],
             'description' =>
               'Declares a link to a document. %{1} is the type of the document, ' .
               'as a string name. %{2} must be the checkin path pointing to the master file ' .
               'of the document. %{3}, the local id of the document, can be choosen ' .
               'arbitrarily.',
            },
         },
       'attachableto' =>
         {
          'num_man_args' => 2,
          'num_opt_args' => 0,
          'execute_function' => sub { japs_execute_ref_cmd('attachableto', @_) },
          'pre_type_arg_hook' => \&japs_allow_text_token,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'types' => $global_data->{attachableto_doctype_names},
          'doc' =>
            {
             'man_args' =>
               [['type', 'Document type name'],
                ['src', 'Source (master file)']],
             'description' =>
               'Declares a document to which this document can be attached. %{1} is the ' .
               'type of the document, as a string name. %{2} must be the checkin path ' .
               'pointing to the master file of the document.',
            },
         },
       'generic' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_generic,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'doc' =>
            {
             'man_args' =>
               [['path', 'Path (master file)']],
             'description' =>
               'Declares a generic document implemented by this document. %{1} must be ' .
               'the checkin path pointing to the master file of the generic document.',
            },
         },
       'creategeneric' =>
         {
          'num_man_args' => 0,
          'num_opt_args' => 1,
          'execute_function' => \&japs_execute_creategeneric,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'doc' =>
            {
             'opt_args' =>
               [['path', 'Path (master file)']],
             'description' =>
               'Causes mmtex to automatically create a generic document. If %[1] is not ' .
               'specified, the path is composed automatically by adding "g_" to the pure name ' .
               'of the source file. If specified, %{1} becomes the the path.',
            },
         },
       'author' =>
         {
          'num_man_args' => 1,
          'num_opt_args' => 0,
          'execute_function' => \&japs_execute_author,
          'pre_path_arg_hook' => \&japs_allow_text_token,
          'doc' =>
            {
             'man_args' =>
               [['src', 'Source (master file)']],
             'description' =>
               'Declares am author of this document. %{1} must be the checkin path ' .
               'pointing to the master file of the author.',
            },
         },
      };

    # The environment table:
    my $env_table =
      {
       'metainfo' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'begin_function' => \&japs_begin_metainfo,
          'end_function' => \&japs_end_metainfo,
         },
       'description' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'begin_function' => \&japs_begin_description,
          'end_function' => \&japs_end_description,
         },
       'changelog' =>
         {
          'num_opt_args' => 0,
          'num_man_args' => 0,
          'begin_function' => \&japs_begin_changelog,
          'end_function' => \&japs_end_changelog,
         },
       'components' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 0,
	  'begin_function' => \&japs_begin_components,
	  'end_function' => \&japs_end_components,
         },
       'links' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 0,
	  'begin_function' => \&japs_begin_links,
	  'end_function' => \&japs_end_links,
         },
       'attachable' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 0,
	  'begin_function' => \&japs_begin_attachable,
	  'end_function' => \&japs_end_attachable,
         },
       'authors' =>
         {
	  'num_opt_args' => 0,
	  'num_man_args' => 0,
	  'begin_function' => \&japs_begin_authors,
	  'end_function' => \&japs_end_authors,
         },
      };

    deploy_lib
      (
       'japs_metainfo',
       $cmd_table,
       $env_table,
       {
	'METAINFO_TOPLEVEL' =>
          [
           'name',
           'status',
           'copyrightinfo',
           'generic',
           'creategeneric',
          ],
       },
       {
	'METAINFO_TOPLEVEL' =>
          [
           'description',
           'changelog',
           'components',
           'links',
           'attachable',
           'authors'
          ],
       },
      );
  };

return(1);
