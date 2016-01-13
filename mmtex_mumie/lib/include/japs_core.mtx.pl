package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>
#
# $Id: japs_core.mtx.pl,v 1.44 2009/11/16 14:24:44 gronau Exp $

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

log_message("\nLibrary \"japs_core\" ", '$Revision: 1.44 $ ', "\n");

# ------------------------------------------------------------------------------------------
#1 Description
# ------------------------------------------------------------------------------------------
#
# Basic utilities for Japs documents. Provides the following features:
#L
# - Filename utilities: Functions to parse and compose Japs filenames
# - Occurrence control: Controlling how often a command or environment is called
# - Lid control: Controlling which local ids are set and used
# - Screen width of text: Functions to estimate the screen width of text
# - Output: Implements and sets a new ouptput writer
# - Initializing: Utilities to initialize the conversion
#/L

# --------------------------------------------------------------------------------
# Filenames
# --------------------------------------------------------------------------------

#Ds
#a ($filename)
# Returns the parts of a filename in the Japs standard for checkin files. The parts
# are returned as a list with the following items:
#DL
#  directory & The directory part of $filename, or the empty string if $filename
#              does not contain a directory. If not the empty string, this part
#              always ends with a slash. \\
#  pure name & The pure name of $filename. \\
#  role suffix &
#              The role suffix of $filename, e.g., "meta", "content", etc. \\
#  media type suffix &
#              The media type suffix of $filename, e.g., "xml", "tex", etc.
#/DL
# If $filename is not conform to the standard, an error is norified.

sub japs_parse_filename
  {
    my $filename = $_[0];
    if ( $filename =~ m/^(.*\/)?([^\/]+)\.([^.\/]+)\.([^.\/]+)$/ )
      {
        return(($1 ? $1 : ''), $2, $3, $4);
      }
    elsif ( $filename =~ m/^(.*\/)?([^\/]+)\.([^.\/]+)$/ )
      {
        return(($1 ? $1 : ''), $2, '', $3);
      }
    else
      {
        &{$scan_proc->{error_handler}}("Not a Japs filename: $filename");
      }
  }

#Ds
#a ([$filename])
# Returns the checkin path of the specified filename, or the current working directory if
# no filename is specified. This is what happens:
#l
# - $filename is made absolute if necessary
# - The checkin root is stripped from the beginning of $filename
# - Leading and trailing slashes are removed from the remaining part if $filename
# - The remaining part is returned
#/l
# If $filename does not start with the checkin root, an error is signalled.

sub japs_get_checkin_path
  {
    my $filename = ($_[0] || get_working_dir());
    if($^O eq "MSWin32" || $^O eq "MSWin64") {
    	# workaround for windows paths not starting with "/"
    	if (get_working_dir() ne $filename) {
    		$filename = get_working_dir() . '/' . $filename if ( $filename !~ m/^\// );
		}
    } else {
    	$filename = get_working_dir() . '/' . $filename if ( $filename !~ m/^\// );
	}
    my $checkin_root = $params{checkin_root};
    &{$scan_proc->{error_handler}}("Checkin root not set") unless ( $checkin_root );
    my $regex = quotemeta($checkin_root);
    $filename =~ m/^$regex\/?(.*)$/ ||
      &{$scan_proc->{error_handler}}
        ("Not under the checkin root: $filename (checkin root is $checkin_root)");
    my $path = $1;
    $path =~ s/^\/|\/$//g;
    return($path);
  }

sub get_lang_indicator
  {
    my $enabled =
      ( $params{add_lang_to_filenames} &&
        grep($global_data->{document_type_name} eq $_, @doctypes_with_generic) );
    return($enabled ? '_' . $scan_proc->{lang} : '');
  }

#Ds
#a ($filename)
# Returns the name of the master file corresponding to $filename. The latter must
# be conform to the Japs standard for checkin files.

sub japs_get_master_filename
  {
    my $filename = ( $_[0] || $scan_proc->{prim_source_name} );
    my ($dir, $body) = japs_parse_filename($filename);
    return
      $dir .
      $body .
      get_lang_indicator() . '.' .
      $params{master_role_suffix} . '.' .
      $params{xml_type_suffix}
  }

#Ds
#a ($filename)
# Returns the name of the content file corresponding to $filename. The latter must
# be conform to the Japs standard for checkin files.

sub japs_get_content_filename
  {
    my $filename = ( $_[0] || $scan_proc->{prim_source_name} );
    my ($dir, $body) = japs_parse_filename($filename);
    return
      $dir .
      $body .
      get_lang_indicator() . '.' .
      $params{content_role_suffix} . '.' .
      $params{xml_type_suffix}
  }

#Ds
#a ($dir, $body)
# Returns the checkin path of the generic document corresponding to the specified directory
# and body (pure name).

sub japs_get_checkin_path_of_generic
  {
    my ($dir, $body) = @_;
    return
      japs_get_checkin_path($dir) .
      '/' .
      'g_' .
      $body . '.' .
      $params{master_role_suffix} . '.' .
      $params{xml_type_suffix}
  }

#Ds
#a ($filename)
# Checks if $filename is a master filename, and notifies an error if it is not.

sub japs_check_master_filename
  {
    my $filename = $_[0];
    if ( $filename !~ m/\.$params{master_role_suffix}\.$params{xml_type_suffix}$/ )
      {
        &{$scan_proc->{error_handler}}("Not a master filename: $filename");
      }
  }

# --------------------------------------------------------------------------------
# Occurrence control
# --------------------------------------------------------------------------------
#
# Some commands and environments have certain contraints on their occurrence in the source.
# For instance, a command may be required to occur at least once, or exactly once, or not
# more than once. To handle this, the occurences are tracked in the so-called occurrence
# table. It is implemented as a hash. The keys are keywords representing a command or
# environment (usually, the key for a command is its name without the leading backslash, and
# the key for an environment is its name). The values are numbers indicating how often the
# command or environment has been called. The occurrence table is accessible by the variable
# $global_data->{occurrence}, which holds a reference to the occurrence table.

sub japs_register_occurrence
  {
    my $key = $_[0];
    if ( $global_data->{occurrence}->{$key} )
      {
        $global_data->{occurrence}->{$key}++;
      }
    else
      {
        $global_data->{occurrence}->{$key} = 1;
      }
  }

sub japs_get_occurrence
  {
    my $key = $_[0];
    return($global_data->{occurrence}->{$key} ? $global_data->{occurrence}->{$key} : 0);
  }

sub japs_check_occurrence
  {
    my ($key, $name, $min, $max, $mode) = @_;
    defined($max) || ($max = 'UNLIMITED');
    $mode ||= 'NORMAL';
    my $num = japs_get_occurrence($key);
    $num++ if ( $mode eq 'FORWARD' );

    if ( ( $max ne 'UNLIMITED' ) && ( $num > $max ) )
      {
        &{$scan_proc->{error_handler}}
          (($name =~ m/^\\/ ? "Command" : "Environment") .
           " $name allowed only " .
           ($max == 1 ? "once" : "$max times"));
      }
    elsif ( $min && $num < $min )
      {
        my $message =
          ('Missing ' . ($name =~ m/^\\/ ? "command" : "environment") . ": $name");
        if ( $min > 1 )
          {
            $message .= "\n(Must be called at least $min times, called only $num times)";
          }
        &{$scan_proc->{error_handler}}($message);
      }
  }

# --------------------------------------------------------------------------------
# Local ids
# --------------------------------------------------------------------------------

sub japs_get_all_lids
  {
    my $metainfo = $global_data->{metainfo};
    my @lids = ();
    foreach my $document (@{$metainfo->{components}}, @{$metainfo->{links}})
      {
	push(@lids, $document->{lid});
      }
    return(@lids);
  }

sub japs_find_by_lid
  {
    my ($lid) = @_;
    my $metainfo = $global_data->{metainfo};
    my @documents = ();
    foreach my $document (@{$metainfo->{components}}, @{$metainfo->{links}})
      {
	push(@documents, $document) if ( $document->{lid} eq $lid );
      }
    return(@documents);
  }

sub japs_check_lid
  {
    my ($lid, $expected, $metainfo) = @_;
    $metainfo ||= $global_data->{metainfo};
    my @documents = japs_find_by_lid($lid, $metainfo);
    unless ( @documents )
      {
	&{$scan_proc->{error_handler}}("Undefined local id: $lid");
      }
    if ( scalar(@documents) > 1 )
      {
	&{$scan_proc->{error_handler}}("Multiple defined local id: $lid");
      }
    if ( $expected )
      {
        my @expected_document_types = (ref($expected) ? @{$expected} : ($expected));
	my $document_type = $documents[0]->{type};
	unless ( grep($document_type eq $_, @expected_document_types) )
	  {
	    &{$scan_proc->{error_handler}}
	      ("Wrong document type for local id: $lid. Expected: " .
	       join(', ', @expected_document_types) . '. ' .
	       "Found: $document_type.");
	  }
      }
  }

sub japs_check_lid_balance
  {
    foreach my $lid (japs_get_all_lids())
      {
	unless ( grep($lid eq $_, @{$global_data->{used_lids}}) )
	  {
	    &{$scan_proc->{warning_handler}}("Unused local id: $lid");
	  }
      }
  }

# --------------------------------------------------------------------------------
# Text width
# --------------------------------------------------------------------------------

sub japs_estimate_screen_width
  {
    my $string = $_[0];
    my $width = 0;
    foreach my $char (split(//, $string))
      {
        $width +=
          ( $global_data->{char_width}->{$char} || $global_data->{char_width}->{X} );
      }
    return $width;
  }

sub japs_check_screen_width
  {
    my ($text, $max_width) = @_;
    $max_width ||= $params{max_text_screen_width};
    foreach my $token (split(/\s+/, $text))
      {
        my $width = japs_estimate_screen_width($token);
        if ( $width > $max_width )
          {
            &{$scan_proc->{warning_handler}}
              ("Token may be too long when rendered on screen: $token ($width/$max_width)");
          }
      }
  }

# --------------------------------------------------------------------------------
# Initialize conversion
# --------------------------------------------------------------------------------

#Ds
#a ($type, $category, $setup, $cleanup, $free)
# Initializes the conversion of a document source. The parameters are the type and
# category of the document, the category setup and cleanup hooks, and a boolean
# indicating that the content can be arranged freely ("free flag"). The function
# does the following:
#L
#   - Sets the global data 'document_type_name', 'category', 'setup',
#     'content_xml_namespace', and 'content_xml_namespace_prefix',
#   - Sets the 'free' flag if necessary,
#   - Loads libraries,
#   - Locks the output list,
#   - Installs the environments 'metainfo' and 'content'.
#/L
# The language and 'free' flag settings are taken from the init options. If the
# latter do not contain the one and/or the other, the respective setting is not
# made.

sub japs_init_conversion
  {
    my ($type, $category, $setup, $cleanup, $free) = @_;

    log_message("\njaps_init_conversion 1/2\n");
    log_data('Type', $type, 'Category', $category);

    # Set type and category:
    $global_data->{document_type_name} = $type;
    $global_data->{category} = $category;

    # Set 'has_category' flag if necessary:
    $global_data->{has_category} = TRUE if ( grep($type eq $_, @doctypes_with_category) );

    # Set 'has_generic' flag if necessary:
    $global_data->{has_generic} = TRUE if ( grep($type eq $_, @doctypes_with_generic) );

    # Set 'has_corrector' flag if necessary:
    $global_data->{has_corrector} = TRUE if ( $type eq 'problem' );

    # Set 'has_class' flag if necessary:
    $global_data->{has_class} = TRUE if ( $type eq 'course' );

    # Set 'has_summary' flag if necessary:
    $global_data->{has_summary} = TRUE if ( grep($type eq $_, @doctypes_with_summary) );

    # Set 'has_timeframe' flag if necessary:
    $global_data->{has_timeframe} = TRUE if ( $type eq 'worksheet' );

    # Set the setup and cleanup hooks:
    $global_data->{setup} = $setup if ( $setup );
    $global_data->{cleanup} = $cleanup if ( $cleanup );

    # Set content namespace:
    if ( $type eq 'element' || $type eq 'subelement' )
      {
        $global_data->{content_xml_namespace} = $params{element_xml_namespace};
        $global_data->{content_xml_namespace_prefix} = $params{element_xml_namespace_prefix};
      }
    elsif ( $type eq 'problem' )
      {
        $global_data->{content_xml_namespace} = $params{problem_xml_namespace};
        $global_data->{content_xml_namespace_prefix} = $params{problem_xml_namespace_prefix};
      }
    elsif ( $type eq 'summary' )
      {
        $global_data->{content_xml_namespace} = $params{summary_xml_namespace};
        $global_data->{content_xml_namespace_prefix} = $params{summary_xml_namespace_prefix};
      }
    elsif ( grep($type eq $_, 'course', 'course_section', 'worksheet') )
      {
        $global_data->{content_xml_namespace} = $params{course_xml_namespace};
        $global_data->{content_xml_namespace_prefix} = $params{course_xml_namespace_prefix};
      }
    else
      {
        notify_programming_error('japs_init_conversion', " Invalid document type: $type");
      }

    # Set "free" and "arrange" flags if necessary:
    if ( $free ||
         $dcl_option_table->{free} || $dcl_option_table->{freeenv} ||
         grep($type eq $_, @doctypes_with_free_arrangement) )
      {
        $global_data->{free} = TRUE;
        $global_data->{arrange} = 'free';
      }

    # Load libraries:
    require_lib("simple_markup");
    require_lib("struc");
    require_lib("list");
    require_lib("table");
    require_lib("hyperlink");
    require_lib("specialchar");
    require_lib("verbatim");
    require_lib("preformatted");
    require_lib("math");
    require_lib("lang");

    # Enable mathml namespace:
    $set_mathml_namespace = TRUE;

    # Lock the output list:
    lock_output_list();

    # Enable "metainfo" and "content" environments:
    install_envs(['metainfo'], 'japs_metainfo');
    install_envs(['content'], 'japs_content');

    # Enable "lang" command:
    install_cmds(['lang'], 'lang');

    log_message("\njaps_init_conversion 2/2\n");
  }

# ------------------------------------------------------------------------------------------
# "Text" commands and environments
# ------------------------------------------------------------------------------------------

sub japs_install_text_cmds_and_envs
  {
    log_message("\njaps_install_text_cmds_and_envs 1/2\n");

    install_cmds(['emph', 'mark', 'notion', 'code', '\\'], "simple_markup");
    install_cmds("ALL", "list");
    # install_cmds("ALL", "hyperlink");
    install_cmds("ALL", "specialchar");
    install_cmds("ALL", "verbatim");
    install_cmds("ALL", "preformatted");
    install_cmds("ALL", "lang");
    install_envs("ALL", "list");
    install_envs("ALL", "verbatim");
    install_envs("ALL", "preformatted");
    install_envs("ALL", "table");
    install_cmds_from_all_libs('MATH_TOPLEVEL');
    install_envs_from_all_libs('MATH_TOPLEVEL');
    install_cmds_from_all_libs('JAPS_MEDIA_TOPLEVEL');
    install_envs_from_all_libs('JAPS_MEDIA_TOPLEVEL');
    install_cmds_from_all_libs('JAPS_LINK_TOPLEVEL');

    # Disable optional space after line break:
    $scan_proc->{cmd_table}->{'\\'}->{num_opt_args} = 0;

    log_message("\njaps_install_text_cmds_and_envs 2/2\n");
  }

# --------------------------------------------------------------------------------
# Creating the generic document
# --------------------------------------------------------------------------------

sub japs_create_generic_document
  {
    my $generic_file = $params{checkin_root} . '/' . $global_data->{path_of_generic};
    my $root_element = 'generic_' . $global_data->{document_type_name};
    my $category = $global_data->{metainfo}->{category}->{name};
    my $pure_name_without_lang = $global_data->{pure_name_without_lang};
    my $name = $params{name_of_generic};
    my $description = $params{description_of_generic};
    my $name_of_real = $global_data->{metainfo}->{name};
    my $description_of_real = $global_data->{metainfo}->{description};
    for my $datum ($name, $description)
      {
        $datum = chars_to_numcodes($datum);
        $datum =~ s/\%p/$pure_name_without_lang/g;
        $datum =~ s/\%n/$name_of_real/g;
        $datum =~ s/\%d/$description_of_real/g;
      }
    # start windows workaround
  	if ($^O eq "MSWin32" || $^O eq "MSWin64") {
      my $filename = File::Basename::fileparse($generic_file);
      my $dir = Mumie::File::get_working_dir();
      $dir = Win32::GetShortPathName($dir);
      $generic_file = $dir . "/" . $filename;
    }
    # end windows workaround
    open(OUTPUT, ">$generic_file") or
      &{$scan_proc->{error_handler}}("Can't open file \`$generic_file\': $!\n");
    print(OUTPUT
      "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n" .
      "<mumie:${root_element} xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">\n");
    print(OUTPUT
      "  <mumie:category name=\"${category}\"/>\n") if ( $global_data->{has_category} );
    print(OUTPUT
      "  <mumie:name>${name}</mumie:name>\n" .
      "  <mumie:description>\n" .
      "    ${description}\n" .
      "  </mumie:description>\n" .
      "</mumie:${root_element}>\n");
    close(OUTPUT);
  }

# --------------------------------------------------------------------------------
# Output writer
# --------------------------------------------------------------------------------

sub japs_output_writer
  {
    my ($source_file) = @_;
    my $master_file = japs_get_master_filename($source_file);
    my $content_file = japs_get_content_filename($source_file);
    write_output_to_file($master_file, $global_data->{metainfo_output_list});
    write_output_to_file($content_file, $global_data->{content_output_list});
    japs_create_generic_document() if ( $global_data->{create_generic} )
  }

# --------------------------------------------------------------------------------
# Up-to-date tester
# --------------------------------------------------------------------------------

sub japs_up_to_date_tester
  {
    my ($source_file) = @_;
    my $master_file = japs_get_master_filename($source_file);
    my $content_file = japs_get_content_filename($source_file);
    my $source_mtime = mtime($source_file);
    if ( -e $master_file && mtime($master_file) > $source_mtime &&
         -e $content_file && mtime($content_file) > $source_mtime )
      { return TRUE; }
    else
      { return FALSE; }
  }

# --------------------------------------------------------------------------------
# Initializing
# --------------------------------------------------------------------------------

$lib_table->{japs_core}->{initializer} = sub
  {
    # Document types having a category
    @doctypes_with_category = qw(element subelement problem worksheet);

    # Document types having a summary
    @doctypes_with_summary = qw(course course_section worksheet);

    # Document types for which a generic counterpart exists. The information provided by
    # this array is important inn two ways: For adding the language specifier to the
    # filenames, and for the \creategeneric and \generic commands.
    @doctypes_with_generic = qw(element subelement problem summary);

    # Document types with free arrangement by default
    @doctypes_with_free_arrangement = qw(problem summary);

    # Setting up parameters
    # Here, parameters that have not been set in the command line or the mmtexrc
    # file get their values. The assignment is done with ||= to protect earlier
    # settings (i.e., in the command line or the mmtexrc file). Note that parameters
    # may also have earlier settings if mmtex converts several source files at
    # once.

    $params{master_role_suffix} ||= 'meta';
    $params{content_role_suffix} ||= 'content';
    $params{xml_type_suffix} ||= 'xml';
    $params{metainfo_xml_namespace} ||= 'http://www.mumie.net/xml-namespace/document/metainfo';
    $params{metainfo_xml_namespace_prefix} ||= 'mumie';
    $params{element_xml_namespace} = 'http://www.mumie.net/xml-namespace/document/element';
    $params{element_xml_namespace_prefix} = 'elm';
    $params{problem_xml_namespace} = 'http://www.mumie.net/xml-namespace/document/problem';
    $params{problem_xml_namespace_prefix} = 'prb';
    $params{summary_xml_namespace} = 'http://www.mumie.net/xml-namespace/document/summary';
    $params{summary_xml_namespace_prefix} = 'sum';
    $params{course_xml_namespace} = 'http://www.mumie.net/xml-namespace/document/content/course';
    $params{course_xml_namespace_prefix} = 'crs';
    $params{dsx_xml_namespace} ||= 'http://www.mumie.net/xml-namespace/data-sheet/extract';
    $params{dsx_xml_namespace_prefix} ||= 'dsx';
    $params{mtx_xml_namespace} ||= 'http://www.mumie.net/xml-namespace/mmtex';
    $params{mtx_xml_namespace_prefix} ||= 'mtx';
    $params{ppd_xml_namespace} ||= 'http://www.mumie.net/xml-namespace/personalized-problem-data';
    $params{ppd_xml_namespace_prefix} ||= 'ppd';
    $params{mchoice_corrector_path} ||= 'system/problem/GenericMchoiceProblemCorrector.meta.xml';
    $params{traditional_corrector_path} ||= 'system/problem/GenericTraditionalProblemCorrector.meta.xml';
    $params{checkin_root} ||= $ENV{MM_CHECKIN_ROOT} || "$ENV{HOME}/mumie/checkin";
    $params{name_of_generic} ||= "Generic of %p_xx";
    $params{description_of_generic} ||=
      "Generic document of all real documents %p_xx where xx is a language code";

    # Whether the language code (e.g., "en", "de") is added to the ouput filenames.
    $params{add_lang_to_filenames} ||= 'yes';

    # Maximum screen width of a word
    # This is compared to the width of a word computed with the aid of
    # $global_data->{char_width}. See there for more information.

    $params{max_text_screen_width} ||= 10;

    $params{copyright} ||= "";

    # Setting up global data
    # Here, the items of the global data get their (default) values. Some of them
    # will be overwritten later. This is especially true for those items that are
    # document type and/or category specific. Some items are set to 'undef()'
    # because their values can not be established here.

    $global_data->{document_type_name} = undef();
    $global_data->{metainfo} = {};
    $global_data->{category} = undef();
    $global_data->{occurrence} ||= {};
    $global_data->{free} = FALSE;
    $global_data->{arrange} = undef();
    $gloabl_data->{setup} ||= sub {};
    $global_data->{cleanup} ||= sub {};
    $global_data->{content_xml_namespace} = undef();
    $global_data->{content_xml_namespace_prefix} = undef();
    $global_data->{metainfo_output_list} = undef();
    $global_data->{content_output_list} = undef();

    $global_data->{status_names} =
      [
       'pre',
       'devel_ok',
       'content_ok',
       'content_complete',
       'ok_for_publication',
       'final'
      ];

    $global_data->{component_doctype_names} =
      [
       'applet',
       'image',
       'sound',
       'flash',
      ];

    $global_data->{link_doctype_names} =
      [
       'element',
       'subelement',
       'problem',
       'generic_element',
       'generic_subelement',
       'generic_problem',
      ];

    $global_data->{attachableto_doctype_names} =
      [
       'element',
       'problem',
      ];

    # Screen widths of characters
    # Contains the screen widths for the most frequent characters, in a relative unit
    # (intended to be em, approximately). Used to estimate the screen width of strings
    # This is not exact, of corse, but can help the author to avoid content that
    # corrupts the layout when rendered on screen.

    $global_data->{char_width} =
      {
       'A' => 0.675,
       'B' => 0.675,
       'C' => 0.675,
       'D' => 0.675,
       'E' => 0.600,
       'F' => 0.600,
       'G' => 0.750,
       'H' => 0.675,
       'I' => 0.225,
       'J' => 0.525,
       'K' => 0.600,
       'L' => 0.525,
       'M' => 0.825,
       'N' => 0.675,
       'O' => 0.825,
       'P' => 0.675,
       'Q' => 0.825,
       'R' => 0.675,
       'S' => 0.675,
       'T' => 0.525,
       'U' => 0.675,
       'V' => 0.675,
       'W' => 0.900,
       'X' => 0.675,
       'Y' => 0.675,
       'Z' => 0.525,
       'a' => 0.600,
       'b' => 0.600,
       'c' => 0.525,
       'd' => 0.600,
       'e' => 0.600,
       'f' => 0.225,
       'g' => 0.600,
       'h' => 0.525,
       'i' => 0.225,
       'j' => 0.225,
       'k' => 0.525,
       'l' => 0.225,
       'm' => 0.825,
       'n' => 0.525,
       'o' => 0.525,
       'p' => 0.600,
       'q' => 0.600,
       'r' => 0.300,
       's' => 0.450,
       't' => 0.225,
       'u' => 0.525,
       'v' => 0.525,
       'w' => 0.675,
       'x' => 0.525,
       'y' => 0.525,
       'z' => 0.450,
       '.' => 0.225,
       ',' => 0.300,
       '-' => 0.300,
      };

    # Set language:
    # if the 'lang' parameter was specified on the command line, Mumie::MmTeX::Converter
    # has set the language according to that parameter. If the 'lang' paramter was not
    # specified, Mumie::MmTeX::Converter has set the language to 'en'. In the latter case,
    # we overwrite the language here with the value found in the document class options
    # or the value found in the MM_LANG environment variable, provided one of the both
    # exists.
    #
    # If mmtex was called with the -c or -e option (query option), Mumie::MmTeX::Converter
    # has not set the language. Thus, if the language is unset, we first set it to 'en'.

    $scan_proc->{lang} ||= 'en';

    unless ( $params{lang} )
      {
        if ( $dcl_option_table->{lang} )
          {
            $scan_proc->{lang} = $dcl_option_table->{lang};
          }
        elsif ( $ENV{MM_LANG} )
          {
            $scan_proc->{lang} = $ENV{MM_LANG};
          }
      }

    require_lib("german") if ( $scan_proc->{lang} eq 'de' );

    # Set the output writer:
    $output_writer = \&japs_output_writer;

    # Set up-to-date tester:
    $up_to_date_tester = \&japs_up_to_date_tester;
  };

return(TRUE);
