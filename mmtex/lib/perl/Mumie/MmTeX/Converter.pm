package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Converter.pm,v 1.148 2008/08/16 23:19:02 rassy Exp $

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
# Provides functions to convert LaTeX into other languages. Build on top of
# Mumie::Scanner and Mumie::MmTeX::Parser.



# ------------------------------------------------------------------------------------------
#1 Primary and secondary source
# ------------------------------------------------------------------------------------------
#
# The entire LaTeX code to be converted is called the \emph{primary source}.
# During parsing, pieces of the primary source may be copied and parsed separately.
# Those pieces of code are called a \emph{secondary sources}.
#
# ------------------------------------------------------------------------------------------
#1 Additional fields in $scan_proc
# ------------------------------------------------------------------------------------------
#
# The latex converter adds some additional fields to $scan_proc. They are:
#
#H
#  prim_source        & Reference to the primary source \\
#  prim_source_offset & Position in the primary source where the current source
#                       starts. Zero if the latter is the primary source itself. \\
#  prim_source_name   & Name of the primary source currently converted. Usually
#                       the file name. \\
#  saved_prim_source_data_list &
#                       Reference to a list of data concerning previous primary
#                       sources (see below). \\
#  output_list        & Reference to the current output list (see below). \\
#  prim_output_list   & The primary output list (see below). This is the list
#                       where the output goes to by default. \\
#                       form. \\
#  pre_scan_hook      & Reference to a function to run imediately before the
#                       source is scanned. \\
#  post_scan_hook     & Reference to a function to run imediately after the
#                       source was scanned. \\
#  post_documentclass_hook
#                     & Reference to a function to run imediately after the
#                       \code{documentclass} command was executed. \\
#  forced_layout      & If set, the layout to use when writing XML code. \\
#  xml_auto_attribs   & Reference to the *auto attbribute table*, a hash
#                       specifying attributes that are set automatically when
#                       the next XML element is started (see below).
#
#/H
#
# ------------------------------------------------------------------------------------------
#1 Output lists
# ------------------------------------------------------------------------------------------
#
# An *output list* is a list of the following form:
#c
#  [funcref1, arg11, arg12, ... ]
#  [funcref2, arg21, arg22, ... ]
#      .        .      .
#      .        .      .
#      .        .      .
#/c
# `funcref1, ...` are references to the XML-writing functions in theMumie::XML:Writre
# module, e.g., `write_xml_pcdata`, `write_xml_start_tag`, etc.; and `arg11`, ... are
# arguments for these functions. If `exec_func_calls`is applied to an output list, it will
# write formatted XML.
#
# ------------------------------------------------------------------------------------------
#1 Auto attribute table
# ------------------------------------------------------------------------------------------
#
# A hash specifying attributes that are set automatically when the next XML element is
# started. The keys are the attribute names; the values are attribute values or references
# to functions that return the attribute values.
#
# ------------------------------------------------------------------------------------------
#1 Saved primary source data
# ------------------------------------------------------------------------------------------
#
# If mmtex temporarily switches to another primary source (e.g., by the `\input` command),
# some data of the old primary source must be saved for later restore. To this end, the
# entry "saved_prim_source_data_list" exists in $scan_proc. It is a stack (list) of data
# sets concerning previous primary sources. Each element of the list is a (refernce to a)
# hash with the following keys:
#H
#  scan_proc  & Reference to a hash containing saved scan proc entries. Keys are:
#               "source", "prim_source", "prim_source_name", "prim_source_offset".
#  scan_proc_depth &
#             The depth of the scan proc.
#/H


use constant VERSION => '$Revision: 1.148 $ ';

use Cwd;
use File::Basename qw(dirname);
use File::Path qw(mkpath);
use Mumie::Boolconst;
use Mumie::Logger qw(/.+/);
use Mumie::File qw(/.+/);
use Mumie::Text qw(/.+/);
use Mumie::List qw(/.+/);
use Mumie::Balanced qw(/.+/);
use Mumie::Hooks qw(/.+/);
use Mumie::ExecFuncCalls;
use Mumie::XML::Characters qw(/.+/);
use Mumie::Scanner qw
  (
   $scan_proc
   get_scan_proc_depth
   get_scan_proc
   test_regexp
   test_inside_balanced
   test_balanced
   $default_error_handler
   $default_scan_failed_handler
   scan
   to_parent_scanproc
  );
use Mumie::MmTeX::Util qw(:ALL);
use Mumie::MmTeX::Parser  qw(:ALL !new_scan_proc !reset_scan_proc !notify_warning);
use Mumie::XML::Writer qw(/.+/);
use Mumie::MmTeX::DclLoader qw(:ALL);
use Mumie::MmTeX::LibLoader qw(:ALL);
use Mumie::MmTeX::Serializer qw(:ALL);
use Mumie::MmTeX::IOHelper qw(:ALL);

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL =
    (
     '$css_dir',
     '$dtd_dir',
     '$global_data',
     '$horizontal_aligns',
     '$import_cmd_tables',
     '$mark_string',
     '$print_progress_bar',
     '$print_status',
     '$status_message',
     '$status_message_margin_left',
     '$user_id_regexp',
     '$vertical_aligns',
     '$with_up_to_date_check',
     '$xdoc_xsl_stylesheet',
     '$xsl_dir',
     '$xsl_stylesheet',
     '%params',
     '@css_stylesheets',
     '@href_prefixes',
     '@href_protocols',
     'add_to_global_data',
     'align_char_to_name',
     'check_class',
     'check_after_conversion',
     'convert',
     'convert_arg',
     'convert_arg_run_hooks',
     'convert_as_pseudo_env',
     'convert_file',
     'convert_subexp',
     'get_data_from_arg',
     'new_scan_proc',
     'notify_error',
     'notify_programming_error',
     'notify_scan_failed_error',
     'parse_options',
     'reset_scan_proc',
     'set_dcl_options',
     'write_dcl_xdoc',
     'write_lib_list_xdoc',
     'write_lib_xdoc',
    );
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
    );
}

# --------------------------------------------------------------------------------
# The init function
# --------------------------------------------------------------------------------

sub init
  {
    return() if ( $init_done );

    # -----------------------------------------------------------------------
    # Error Handling
    # -----------------------------------------------------------------------

    $mark_string = '<*POS*>';
    $default_error_handler = \&notify_error;
    $default_programming_error_handler = \&notify_programming_error; # Yet unused

    # -----------------------------------------------------------------------
    # Warnings
    # -----------------------------------------------------------------------

    $default_warning_handler = \&notify_warning;

    # -----------------------------------------------------------------------
    # Status messages
    # -----------------------------------------------------------------------

    $print_status = FALSE;
    # Whether status messages should be printed or not.

    $status_message_margin_left = "";
    # String inserted on the left of each (new) status message. Empty by
    # default, meant for customization.

    $status_message =
      {
       'CONVERSION_START' => sub
         {
	   my $prim_source_name = $_[0];
	   return($status_message_margin_left . $prim_source_name . " ... ");
	 },
       'SOURCE_UP_TO_DATE' => sub
         {
	  return("up-to-date\n");
	 },
       'CONVERSION_DONE' => sub
         {
	   return("done\n");
	 },
      };
    # The status message table (see main documentation).

    # -----------------------------------------------------------------------
    # Finding document classes, include files, etc.
    # -----------------------------------------------------------------------

    $dtd_dir = FALSE;
    # Directory where the dtd's are kept

    $xsl_dir = FALSE;
    # If non-FALSE, the directory where the default xsl stylesheet (see below)
    # resides.

    $xsl_stylesheet = FALSE;
    # If non-FALSE, this is the URI of the default xsl stylesheet, i.e., the
    # stylesheet specified by the <?xml-stylesheet ...?> processing instruction.
    # Otherwise, no such processing instruction is inserted into the xml document.

    $css_dir = FALSE;
    # If non-FALSE, the root directory for CSS stylesheets.

    # @css_stylesheets = ();
    # List of default CSS stylesheets for the document. It is the job of the document
    # class to handle this.

    # -----------------------------------------------------------------------
    # Parameters, global parse data
    # -----------------------------------------------------------------------

    %params = ();
    # A hash containing name-value pairs of user defined parameters.

    $global_data = {};
    # (Reference to a) hash containing "global" data; i.e., data which are similar to those in
    # $scan_proc, but can not be stored there properly since they are independent of the
    # $scan_proc depth.

    # -----------------------------------------------------------------------
    # The default output functions
    # -----------------------------------------------------------------------

    $default_output_functions = {};

    # -----------------------------------------------------------------------
    # The initial command table
    # -----------------------------------------------------------------------

    $initial_cmd_table = {};
    $initial_cmd_table->{documentclass}
      = {
	 "num_opt_args" => 1,
	 "num_man_args" => 1,
	 "is_par_starter" => FALSE,
	 "execute_function" => \&documentclass,
	 "documentation" => ""
	};

    # -----------------------------------------------------------------------
    # Auxiliaries for automatic id generation
    # -----------------------------------------------------------------------

    $last_default_id = -1;
    # $default_id_prefix = "DEFAULT_ID_";
    $default_id_prefix = "_DEFAULT_ID_";
    $user_id_regexp = "^[a-zA-Z0-9][a-zA-Z0-9-_.]+\$";

    # -----------------------------------------------------------------------
    # Handling the primary source
    # -----------------------------------------------------------------------

    @SAVED_PRIM_SOURCE_KEYS = ('source', 'prim_source', 'prim_source_name', 'prim_source_offset');
    # The keys of the entries in $scan_proc that are saved for later restore
    # when switching to a new primary source.

    # -----------------------------------------------------------------------
    # Converting
    # -----------------------------------------------------------------------

    $horizontal_aligns =
      {
       'l' => 'left',
       'r' => 'right',
       'c' => 'center',
       'j' => 'justify',
       's' => 'justify',
      };

    $vertical_aligns =
      {
       't' => 'top',
       'm' => 'middle',
       'b' => 'bottom',
       'B' => 'baseline',
      };

    $with_up_to_date_check = FALSE;

    # -----------------------------------------------------------------------
    # Setting "done" flag
    # -----------------------------------------------------------------------

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

# ------------------------------------------------------------------------------------------
#  Error messages
# ------------------------------------------------------------------------------------------

sub notify_error
  # (@explanation_list)
  {
    my $explanation = join("", @_);

    my $message = compose_error_message
      (
       $explanation,
       $scan_proc->{prim_source},
       $scan_proc->{prim_source_name},
       $scan_proc->{prim_source_offset} + pos(${$scan_proc->{source}}),
       $mark_string
      );

    log_message("\n$message\n");
    print(STDERR "\n") if ( $print_status );
    die("\n$message\n");
  }

sub notify_programming_error
  # ($sub_name, @messages)
  {
    my ($sub_name, @messages) = @_;
    ( $! = 2 ) if ( $! == 0 );
    die("\nProgramming error: $sub_name:", @messages, "\n\n");
  }

# ------------------------------------------------------------------------------------------
# Warnings
# ------------------------------------------------------------------------------------------

sub notify_warning
  {
    if ( $print_warnings )
      {
	Mumie::MmTeX::Parser::notify_warning(@_);
      }
  }

# ------------------------------------------------------------------------------------------
# Status messages
# ------------------------------------------------------------------------------------------

sub notify_status
  {
    my ($event, @params) = @_;
    print(&{$status_message->{$event}}(@params)) if ( $print_status );
  }

# ------------------------------------------------------------------------------------------
#  Parameters and global parse data
# ------------------------------------------------------------------------------------------

sub add_to_global_data
  #a ($data, $mode)
  # Adds $data to $global_data. $data must be a reference to a hash. If $mode is "OVERWRITE"
  # (the default), each key-value pair of $data is added to $global data; if $mode is
  # "PROVIDED_NOT_EXIST", a key-value pair of $data is added to $global data only if the
  # key does not exist already in $global_data.
  {
    my ($data, $mode) = (@_);
    $mode ||= 'OVERWRITE';
    foreach my $key (keys(%{$data}))
      {
	if ( ( $mode eq 'OVERWRITE' ) || ( ! exists($global_data->{$key}) ) )
	  {
	    $global_data->{$key} = $data->{$key};
	  }
      }
  }

# ------------------------------------------------------------------------------------------
#  The \documentclass command
# ------------------------------------------------------------------------------------------

sub documentclass
  {
    log_message("\ndocumentclass 1/3\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $dcl_options = ( $opt_args->[0] || "" );
    my $dcl_name = $man_args->[0];

    parse_options($dcl_options, $dcl_option_table);
    parse_options($cml_dcl_options, $dcl_option_table);

    my $option_log_text = "";
    foreach my $option_name (keys(%{$dcl_option_table}))
      {
	$option_log_text .= "    "
                         . flush_string_right($option_name, 10)
	                 . " = "
			 . $dcl_option_table->{$option_name}
			 . "\n";
      }

    log_message("\ndocumentclass 2/3\n");
    log_data("Options", $option_log_text);

    load_document_class($dcl_name);

    run_hook($scan_proc->{post_documentclass_hook});

    log_message("\ndocumentclass 3/3\n");
  }

# --------------------------------------------------------------------------------
# The default output functions
# --------------------------------------------------------------------------------

sub log_output
  #a ()
  # Writes log information about the token with type $type and value $value.
  {
    my ($type, $value) = @_;
    log_message("\nOutput\n");
    log_data("Type", $type, "Value", "-->$value<--");
  }

sub create_default_output_functions
  {
    return
      ({
       'ign_whitesp' => sub
         {
	   log_output("ign_whitesp", "");
	 },
       'plain_text' => sub
         {
	   log_output("plain_text", $_[0]);
	   my $text = $_[0];
	   $text =~ s/&|<|>/{'&' => '&amp;', '<' => '&lt;', '>' => '&gt;'}->{$&}/ge;
	   # $text =~ s/</&lt\;/g;
	   # $text =~ s/>/&gt\;/g;
	   xml_pcdata($text);
	 },
       'begin_par' => sub
         {
	   log_output("begin_par", "");
	   start_xml_element("par", {}, "DISPLAY");
	 },
       'end_par' => sub
         {
	   log_output("end_par", "");
	   close_xml_element("par", "DISPLAY");
	 },
       'comment' => sub
         {
	   log_output("comment", $_[0]);
	 },

       'double_quote' => sub
         {
	   log_output("double_quote", $_[0]);
	   xml_pcdata('"');
	 },

       'special_char' => sub
         {
	   log_output("special_char", $_[0]);
	   my $char = $_[0];
	   my $entity = $chars_vs_entities{$char};
	   xml_special_char($entity);
	 },
      });
  }

# ------------------------------------------------------------------------------------------
# Handling the primary source
# ------------------------------------------------------------------------------------------

sub get_prim_source_depth
  #a ()
  # Returnes the nesting depth of primary sources.
  {
    return(1+scalar(@{$scan_proc->{saved_prim_source_data_list}}));
  }

sub switch_prim_source
  {
    my ($prim_source, $prim_source_name) = @_;
    my $prim_source_depth = get_prim_source_depth();

    log_message("\nswitch_prim_source 1/2\n");
    log_data('New prim. source', $prim_source,
             'New prim. source name', $prim_source_name,
             'Prim. source depth', $prim_source_depth . '->' . ($prim_source_depth+1));

    my $saved_data = {};
    foreach my $key (@SAVED_PRIM_SOURCE_KEYS)
      {
	$saved_data->{scan_proc}->{$key} = $scan_proc->{$key};
      }
    $saved_data->{scan_proc_depth} = get_scan_proc_depth();

    push(@{$scan_proc->{saved_prim_source_data_list}}, $saved_data);

    $scan_proc->{source} = $prim_source;
    pos(${$scan_proc->{source}}) = 0;
    $scan_proc->{prim_source} = $prim_source;
    $scan_proc->{prim_source_name} = $prim_source_name;
    $scan_proc->{prim_source_offset} = 0;

    log_message("\nswitch_prim_source 2/2\n");
  }

sub reset_prim_source
  {
    my $prim_source_depth = get_prim_source_depth();

    log_message("\nreset_prim_source 1/3\n");
    log_data('Prim. source depth', $prim_source_depth . '->' . ($prim_source_depth-1));

    my $saved_data = pop(@{$scan_proc->{saved_prim_source_data_list}});

    my ($depth, $last_depth) = ($saved_data->{scan_proc_depth}, get_scan_proc_depth());
    log_message("\nreset_prim_source 2/3\n");
    log_data('Scan procs', "$depth ... $last_depth");
    while ( $depth <= $last_depth )
      {
        foreach my $key (@SAVED_PRIM_SOURCE_KEYS)
          {
            get_scan_proc($depth)->{$key} = $saved_data->{scan_proc}->{$key};
          }
        $depth++;
      }

    log_message("\nreset_prim_source 2/3\n");
  }

# ------------------------------------------------------------------------------------------
#  Creating scan processes
# ------------------------------------------------------------------------------------------

sub new_scan_proc
  # (DEFAULT
  #  | COPY, $namespace_opt
  #  | INITIAL, [$source, [$prim_source_name]]
  #  | EXCURSION, $source, $prim_source_offset, $namespace_opt
  #  | INSERT, $source, $prim_source_name, $namespace_opt)
  {
    my $how_to_init = ( shift() or 'DEFAULT' );

    log_message("\nnew_scan_proc 1/2 (Mumie::MmTeX::Converter)\n");
    log_data('Init type', $how_to_init);

    if ( $how_to_init eq 'DEFAULT' )
      {
	Mumie::MmTeX::Parser::new_scan_proc('DEFAULT');

	$scan_proc->{prim_source_name} = '*unnamed*';
	$scan_proc->{prim_source_offset} = 0;
        $scan_proc->{saved_prim_source_data_list} = [];
	$scan_proc->{message} = '';
	$scan_proc->{prim_output_list} = [];
	$scan_proc->{output_list} = $scan_proc->{prim_output_list};
      }
    elsif ( $how_to_init eq 'COPY' )
      {
	my $namespace_opt = shift();
 	Mumie::MmTeX::Parser::new_scan_proc('COPY', $namespace_opt);
      }
    elsif ( $how_to_init eq 'INITIAL' )
      {
	my $source = ( shift() or \ '' );
	my $prim_source_name = ( shift() or '' );

	Mumie::MmTeX::Parser::new_scan_proc('DEFAULT');

	$scan_proc->{source} = $source;
	pos(${$scan_proc->{source}}) = 0;
	$scan_proc->{prim_source} = $source;
	$scan_proc->{prim_source_name} = $prim_source_name;
	$scan_proc->{prim_source_offset} = 0;
        $scan_proc->{saved_prim_source_data_list} = [];
	$scan_proc->{allowed_tokens} = \@preamble_token_types;
	$scan_proc->{mode} = 'PREAMBLE';
	$scan_proc->{cmd_table} = $initial_cmd_table;
	$scan_proc->{inside_par} = FALSE;
	$scan_proc->{par_enabled} = FALSE;
	$scan_proc->{prim_output_list} = [];
	$scan_proc->{output_list} = $scan_proc->{prim_output_list};
	$scan_proc->{output_functions} = create_default_output_functions();
	$scan_proc->{xml_auto_attribs} = {};
	$scan_proc->{lang} = ( $params{lang} || 'en' );
        $scan_proc->{multilang} = TRUE
          if ( $params{multilang} && $params{multilang} =~ m/^yes|true$/ );
      }
    elsif ( $how_to_init eq 'EXCURSION' )
      {
        my ($source, $prim_source_offset, $namespace_opt, $output_list_opt) = @_;
	$namespace_opt ||= 'SHARED_NAMESPACE';
	$output_list_opt ||= 'OWN_OUTPUT_LIST';

	Mumie::MmTeX::Parser::new_scan_proc('COPY', $namespace_opt);

        $scan_proc->{source} = $source;

	pos($ {$scan_proc->{source}}) = 0;
	$scan_proc->{prim_source_offset} += $prim_source_offset;

	if ( $output_list_opt eq 'OWN_OUTPUT_LIST' )
	  {
	    $scan_proc->{prim_output_list} = [];
	    $scan_proc->{output_list} = $scan_proc->{prim_output_list};
	  }

        if ( $scan_proc->{mode} eq 'MATH' )
	  {
	    $scan_proc->{math_node_list} = [];
	  }
      }
    elsif ( $how_to_init eq 'INSERT' )
      {
	my ($source, $prim_source_name, $namespace_opt)  = @_;
	$namespace_opt ||= 'SHARED_NAMESPACE';

 	Mumie::MmTeX::Parser::new_scan_proc('COPY', $namespace_opt);
	$scan_proc->{source} = $source;
	pos(${$scan_proc->{source}}) = 0;
	$scan_proc->{prim_source} = $source;
	$scan_proc->{prim_source_name} = $prim_source_name;
	$scan_proc->{prim_source_offset} = 0;
      }
    elsif ( $how_to_init eq 'SUB' )
      {
        my ($type, $name, $source, $prim_source, $prim_source_name, $prim_source_offset,
	    $namespace_opt) = @_;
	my $calling_scan_proc = $scan_proc;

	Mumie::MmTeX::Parser::new_scan_proc('COPY', $namespace_opt);

	$scan_proc->{type_of_sub} = $type;
	$scan_proc->{name_of_sub} = $name;
	$scan_proc->{calling_scan_proc} = $calling_scan_proc;
        $scan_proc->{source} = $source;
	pos($ {$scan_proc->{source}}) = 0;
	$scan_proc->{prim_source} = $prim_source;
	$scan_proc->{prim_source_name} = $prim_source_name;
	$scan_proc->{prim_source_offset} = $prim_source_offset;
      }
    elsif ( $how_to_init eq 'EMPTY' )
      {
	# nothing to do in this case
      }
    log_message("\nnew_scan_proc 2/2 (Mumie::MmTeX::Converter)\n");
  }

sub reset_scan_proc
  # ($how_to_reset, @params)
  {
    my ($how_to_reset, @params) = @_;
    $how_to_reset ||= "PLAIN";

    log_message("\nreset_scan_proc (Mumie::MmTeX::Converter)\n");
    my $depth = get_scan_proc_depth();
    log_data("How", $how_to_reset,
	     "Depth", "$depth -> ".($depth-1),
	     "Params", join(' ', @params));

    my $previous_scan_proc = $scan_proc;

    Mumie::MmTeX::Parser::reset_scan_proc();

    # Adopt values
    if ( $how_to_reset eq "ADOPT_VALUES" )
      {
	my @keys = (@params ? @{$params[0]} : ('inside_par', 'current_env'));
	log_data('Keys', join(' ', @keys));
	foreach $key (@keys)
	  {
	    $scan_proc->{$key} = $previous_scan_proc->{$key};
	  }
      }

    # Check for paragraph nesting
    my $inside_par_before = $previous_scan_proc->{inside_par};
    my $inside_par_after = $scan_proc->{inside_par};
    if ( $inside_par_before !=  $inside_par_after )
      {
	&{$scan_proc->{error_handler}}
	  ("Nesting error after scan process reset:\n",
	   "Before reset: ",
	   ($inside_par_before ? "inside paragraph" : "not inside paragraph"), "\n",
	   "After reset: ",
	   ($inside_par_after ? "inside paragraph" : "not inside paragraph"));
      }
  }


# ------------------------------------------------------------------------------------------
# Converting
# ------------------------------------------------------------------------------------------

sub convert
  #a ($source, $prim_source_name)
  # Converts $source. $source is a reference to a string. $prim_source_name is the name of
  # the primary source (see `Primary and secondary source`). Returns the output list
  # emerging from the conversion process.
  {
    my ($source, $prim_source_name) = @_;

    log_message("\nconvert 1/2\n");
    log_data("Source", $source, "Prim name", $prim_source_name);

    new_scan_proc("INITIAL", $source, $prim_source_name);
    run_hook($scan_proc->{pre_scan_hook});
    scan();
    run_hook($scan_proc->{post_scan_hook});

    log_message("\nconvert 2/2\n");

    return($scan_proc->{output_list});
  }

sub convert_subexp
  #a ($source, $prim_source_offset, $pre_scan_hook, $post_scan_hook, $namespace_opt,
  #   $output_list_opt)
  # Meant to convert a subexpression of the primary source (see
  # `Primary and secondary source`). The subexpression is given by $source, a reference to a
  # string. $prim_source_offset is the position in the primary source where the
  # subexpression starts. $pre_scan_hook and $post_scan_hook are (references to) functions;
  # they are called immediately before resp. after the conversion. They may also be
  # omitted. Returns the output list emerging from the conversion process.
  {
    my ($source, $prim_source_offset, $pre_scan_hook, $post_scan_hook, $namespace_opt,
	$output_list_opt) = @_;

    $namespace_opt ||= 'SHARED_NAMESPACE';
    $output_list_opt ||= 'OWN_OUTPUT_LIST';

    log_message("\nconvert_subexp 1/2\n");
    log_data('source', $source,
	     'Prim offset', $prim_source_offset,
	     'Pre hook', $pre_scan_hook,
	     'Post hook', $post_scan_hook,
	     'Namespace', $namespace_opt,
	     'Output list', $output_list_opt);

    new_scan_proc('EXCURSION', $source, $prim_source_offset, $namespace_opt, $output_list_opt);
    run_hook($pre_scan_hook);
    scan();
    my $output_list = $scan_proc->{output_list};
    run_hook($post_scan_hook);
    reset_scan_proc();

    log_message("\nconvert_subexp 2/2\n");
    log_data('Output', $output_list);

    return($output_list);
  }

sub convert_math_subexp
  #a ($source, $prim_source_offset, $pre_scan_hook, $post_scan_hook)
  # Meant to convert a subexpression of the primary source in mathematical mode (see
  # `Primary and secondary source`). The subexpression is given by $source, a reference to a
  # string. $prim_source_offset is the position in the primary source where the
  # subexpression starts. $pre_scan_hook and $post_scan_hook are (references to) functions;
  # they are called immediately before resp. after the conversion. They may also be
  # omitted. Returns the math node list emerging from the conversion process.
  {
    my ($source, $prim_source_offset, $pre_scan_hook, $post_scan_hook) = @_;

    log_message("\nconvert_math_subexp 1/2\n");
    log_data("Source", $source,
	     "Prim offset", $prim_source_offset,
	     "Pre hook", $pre_scan_hook,
	     "Post hook", $post_scan_hook);

    new_scan_proc("EXCURSION", $source, $prim_source_offset, "SHARED_NAMESPACE");
    run_hook($pre_scan_hook);
    scan();

    run_hook($post_scan_hook);

    my $math_node_list = $scan_proc->{math_node_list};


    reset_scan_proc();

    log_message("\nconvert_math_subexp 2/2\n");

    return($math_node_list);
  }

sub convert_math_as_block
  #a ($source, $prim_source_offset, $pre_scan_hook, $post_scan_hook)
  # Meant to convert a subexpression of the primary source in mathematical mode (see
  # `Primary and secondary source`). The subexpression is given by $source, a reference to a
  # string. $prim_source_offset is the position in the primary source where the
  # subexpression starts. $pre_scan_hook and $post_scan_hook are (references to) functions;
  # they are called immediately before resp. after the conversion. They may also be
  # omitted. Returns a block node if there are more than one result, the resulting node
  # if only one is created..
  {
    my ($source, $prim_source_offset, $pre_scan_hook, $post_scan_hook) = @_;

    log_message("\nconvert_math_as_block 1/2\n");
    log_data("Source", $source,
	     "Prim offset", $prim_source_offset,
	     "Pre hook", $pre_scan_hook,
	     "Post hook", $post_scan_hook);

    new_scan_proc("EXCURSION", $source, $prim_source_offset, "SHARED_NAMESPACE");

    run_hook($pre_scan_hook);
    scan();

    run_hook($post_scan_hook);


    my $block = new_math_node();
    $block->{type} = "block";
    $block->{value} = $scan_proc->{math_node_list};

    reset_scan_proc();

    log_message("\nconvert_math_as_block 2/2\n");

    return($block);
  }

sub convert_insert
  #a ($source, $prim_source_name, $pre_scan_hook, $post_scan_hook, $namespace_opt)
  # Meant to convert an additional source that is read from a file during the main
  # conversion. The source is given by $source, a reference to a string. $prim_source_name
  # is the the name of the source (which is the primary source in this case; see
  # `Primary and secondary source`). $namespace_opt may be "SHARED_NAMESPACE" or
  # "OWN_NAMESPACE". In the first case, this conversion uses the same command and
  # environment tables as the current scan process; in the latter case, a copy of that
  # tables is made. $pre_scan_hook and $post_scan_hook are references to functions called
  # immediately before resp. after the conversion. They may be omitted. Returns the output
  # list emerging from the conversion.
  {
    my ($source, $prim_source_name, $pre_scan_hook, $post_scan_hook, $namespace_opt) = @_;
    $namespace_opt ||= 'SHARED_NAMESPACE';

    log_message("\nconvert_insert 1/2\n");
    log_data('Source', $source,
	     'Prim name', $prim_source_name,
	     'Namespace', $namespace_opt,
	     'Pre hook', $pre_scan_hook,
	     'Post hook', $post_scan_hook);

    # Storing current scan process for later check
    my $previous_scan_proc = $scan_proc;

    # New scan process with pseudo environment
    new_scan_proc('INSERT', $source, $prim_source_name, $namespace_opt);
    $scan_proc->{current_env} = '_inserted source';

    # Scanning
    run_hook($pre_scan_hook);
    scan();
    my $output_list = $scan_proc->{output_list};
    run_hook($post_scan_hook);

    # Closing paragraph if inside one
    if ( $scan_proc->{current_env} eq '_paragraph' )
      {
	close_par();
      }

    # Nesting check
    if ( $scan_proc->{current_env} ne '_inserted source' )
      {
	&{$scan_proc->{error_handler}}
	  (appr_env_notation($scan_proc->{current_env}),
	   ' must be closed within inserted source.');
      }

    # Resetting scan process
    reset_scan_proc();

    # Checking if we have the same scan process as before
    if ( $scan_proc != $previous_scan_proc )
      {
	notify_programming_error
	  ('convert_insert',
	   "\nDid not return to same scan process after insert (nesting problem)");
      }

    log_message("\nconvert_insert 2/2\n");

    return($output_list);
  }

sub convert_as_pseudo_env
  #a ($env_name, $source, $prim_source_offset, $pre_scan_hook, $post_scan_hook, $namespace_opt,
  #   $output_list_opt)
  # Internal help function; you probably don't need it directly. Meant to convert a
  # subexpression as a pseudo environment. $env_name is the environment name, $source the
  # source to convert, given as a reference to a string, $prim_source_offset the position
  # in the primary source where the subexpression starts (see
  # `Primary and secondary source`). $pre_scan_hook and $post_scan_hook are references to
  # functions called immediately before resp. after the conversion. They may be
  # omitted. Returns the output list emerging from the conversion.
  {
    my ($env_name, $source, $prim_source_offset, $pre_scan_hook, $post_scan_hook, $namespace_opt,
	$output_list_opt) = @_;

    log_message("\nconvert_as_pseudo_env\n");
    log_data("Env", $env_name,
	     "Source", $source,
	     "Prim offset", $prim_source_offset,
	     "Pre hook", $pre_scan_hook,
	     "Post hook", $post_scan_hook,
	     'Namespace', $namespace_opt,
	     'Output list', $output_list_opt);

    my $pre_hook
      = sub
	  {
	    $scan_proc->{current_env} = $env_name;
	    run_hook($pre_scan_hook);
	  };

    my $post_hook
      = sub
	  {
	    run_hook($post_scan_hook);
	    if ( $scan_proc->{current_env} ne $env_name )
	      {
		&{$scan_proc->{error_handler}}
		  ("Improper nesting: ", appr_env_notation($scan_proc->{current_env}),
		   " must be closed before\n", appr_env_notation($env_name),
		   " can be closed.");
	      }
	  };

    return(convert_subexp($source, $prim_source_offset, $pre_hook, $post_hook, $namespace_opt,
			  $output_list_opt));
  }

sub convert_arg_run_hooks
  #a ($num, $args, $pos_args, $ctr_type, $pre_scan_hook, $post_scan_hook, $mode, $namespace_opt,
  #   $output_list_opt)
  # Internal help function; you probably don't need it directly. Meant to convert a command
  # or environment argument. $num is the number of the argument, $args a reference to the
  # argument list, $pos_args a reference to the list of the positions of the arguments in
  # the primary source (see `Primary and secondary source`). The latter is only for error
  # messages. $ctr_type may be "CMD" or "ENV", meaning the argument is from a command or an
  # environment, respectively. $pre_scan_hook and $post_scan_hook are references to
  # functions called immediately before resp. after the conversion. $mode may be "NORMAL" or
  # "DATA", meaning that the result of the conversion is XML markup or data,
  # respectively. The last three arguments may be omitted. The default of $mode is
  # "NORMAL". Returns the output list emerging from the conversion.
  #
  # See `convert_arg` for additional information.
  {
    my ($num, $args, $pos_args, $ctr_type, $pre_scan_hook, $post_scan_hook, $mode, $namespace_opt,
	$output_list_opt) = @_;
    $mode or ( $mode = "NORMAL" );

    log_message("\nconvert_arg_run_hooks\n");
    log_data("Number", $num,
	     "Arguments", $args,
	     "Pos args", $pos_args,
	     "Ctr type", $ctr_type,
	     "Pre hook", $pre_scan_hook,
	     "Post hook", $post_scan_hook,
	     "Mode", $mode,
	     'Namespace', $namespace_opt,
	     'Output list', $output_list_opt);

    my $arg_code = $args->[$num];
    my $pos_arg_code = $pos_args->[$num];

    my $pseudo_env_name
      = {
	 "CMD" => "_command argument",
	 "ENV" => "_environment argument"
	}
	->{$ctr_type};

    my $pre_hook
      = {
	 "NORMAL" => $pre_scan_hook,
	 "DATA"   => sub
	               {
			 $scan_proc->{parsing_data} = TRUE ;
			 $scan_proc->{allowed_tokens} = \@data_token_types;
                         allow_tokens([get_vital_tokens()]);
                         # own_cmd_table();
                         # remove_cmds(get_cmds_for_tag('NOT_IN_DATA'));
			 run_hook($pre_scan_hook);
		       }
	}
	->{$mode};

    return(convert_as_pseudo_env($pseudo_env_name, \$arg_code, $pos_arg_code,
				 $pre_hook, $post_scan_hook, $namespace_opt, $output_list_opt));
  }

sub convert_arg
  #a ($num, $args, $pos_args, $ctr_type, $hook_descr_1, $hook_descr_2, $mode, $namespace_opt,
  #   $output_list_opt)
  # High-level function to convert a command or environment argument. Returns the output
  # list emering from conversion. The function parameters have the following meanings:
  #DL
  #  $num      & number of the argument \\
  #  $args     & reference to the argument list \\
  #  $pos_args & reference to the list of the positions of the arguments in the primary
  #              source \\
  #  $ctr_type & "CMD" or "ENV", meaning the argument is from a command or an
  #              environment, respectively \\
  #  $hook_descr_1 &
  #              See below. \\
  #  $hook_descr_2 &
  #              See below. \\
  #  $mode     & "NORMAL" or "DATA", meaning that the result of the conversion is XML
  #              markup or data, respectively \\
  #  $namespace_opt &
  #              "SHARED_NAMESPACE" or "OWN_NAMESPACE", meaning the current command and
  #              environment tables are used or copies are made, respectively. \\
  #  $output_list_opt &
  #              "SHARED_OUTPUT_LIST" or "OWN_OUTPUT_LIST", meaning the current or an
  #              newly created output list is used.
  #/DL
  # $hook_descr_1 and $hook_descr_2 specify the hooks to run before and after conversion.
  # They do this in the following way:
  #
  # If $hook_descr_1 and $hook_descr_2 are both strings, then $hook_descr_1 is regarded
  # as a command resp. environment name. In the command resp. environment table is searched
  # for entries
  #c
  #  $ctr_table->{$hook_descr_1}->{"pre_" . $hook_descr_2 . "_hook"}
  #  $ctr_table->{$hook_descr_1}->{"post_" . $hook_descr_2 . "_hook"}
  #/c
  # where $ctr_table stands for the command resp. environment table reference. These two
  # entries are regarded as (references to) the hooks to run before resp. after conversion.
  #
  # If $hook_descr_1 and $hook_descr_2 are both references, these are regarded the hooks to
  # run before resp. after conversion.
  {
    my ($num, $args, $pos_args, $ctr_type, $hook_descr_1, $hook_descr_2, $mode, $namespace_opt,
	$output_list_opt) = @_;

    log_message("\nconvert_arg\n");
    log_data("Number", $num,
	     "Arguments", $args,
	     "Pos args", $pos_args,
	     "Ctr type", $ctr_type,
	     "Hook 1", $hook_descr_1,
	     "Hook 2", $hook_descr_2,
	     "Mode", $mode,
	     'Namespace', $namespace_opt,
	     'Output list', $output_list_opt);

    my $pre_scan_hook;
    my $post_scan_hook;

    # Checking whether $hook_descr_1 and $hook_descr_2 are the command resp.
    # environment name and the hook shortcut, respectively.

    if ( ( $hook_descr_1 ) && ( ! ref($hook_descr_1) ) )
      {
	my $ctr_name = $hook_descr_1;
	my $hook_shortcut = $hook_descr_2;
	
	my $table
	  = {
	     "CMD" => "cmd_table",
	     "ENV" => "env_table"
	    }
	    ->{$ctr_type};
	
	$pre_scan_hook
	  = $scan_proc->{$table}->{$ctr_name}->{"pre_" . $hook_shortcut . "_hook"};
	$post_scan_hook
	  = $scan_proc->{$table}->{$ctr_name}->{"post_" . $hook_shortcut . "_hook"};
      }
    else
      {
	$pre_scan_hook = $hook_descr_1;
	$post_scan_hook = $hook_descr_2;
      }

    return(convert_arg_run_hooks($num, $args, $pos_args,
				 $ctr_type, $pre_scan_hook, $post_scan_hook, $mode,
				 $namespace_opt, $output_list_opt));
  }

sub get_data_from_arg
  {
    my ($num, $args, $pos_args, $ctr_type, $hook_descr_1, $hook_descr_2, $namespace_opt) = @_;

    log_message("\nget_data_from_arg\n");
    log_data('Number', $num,
	     'Arguments', $args,
	     'Pos args', $pos_args,
	     'Ctr type', $ctr_type,
	     'Hook 1', $hook_descr_1,
	     'Hook 2', $hook_descr_2);

    my $data_output_list
      = convert_arg($num, $args, $pos_args, $ctr_type,
		    $hook_descr_1, $hook_descr_2, 'DATA',
		    $namespace_opt);

    reset_xml_writer();
    exec_func_calls(@{$data_output_list});
    my $result = join('', @xml_output_list);

    return($result)
  }

sub align_char_to_name
  # ($align_char, $align_table)
  # Converts an one-character align specifier (e.g., `l') to an align name (e.g., `left')
  # and returns that name. $align_char is the align specifier. $align_table is a refernce
  # to a hash mapping align specifiers to align names. The name returned is the value of
  # key $align_char in that hash. If there is no such key in the hash, the error handler
  # is called with suitable error message.
  {
    my ($align_char, $align_table) = @_;

    if ( grep($_ eq $align_char, keys(%{$align_table})) )
      {
	return($align_table->{$align_char});
      }
    else
      {
	&{$scan_proc->{error_handler}}
	  ("Invalid align specifier: \`", $align_char, "\'.");
	retun(FALSE);
      }
  }

sub check_after_conversion
  # ()
  # Meant to be called after a source has been converted to check if the converting has been
  # finished properly, i.e., if no environments are still open.
  {
    log_message("\ncheck_after_conversion\n");
    log_data
      (
       "Pos", pos($ {$scan_proc->{source}} ),
       "Depth", scalar(@Mumie::Scanner::scan_proc_list),
       "Current env.", $scan_proc->{current_env},
       "Par enabled", $scan_proc->{par_enabled},
       "Inside par", $scan_proc->{inside_par},
       "Parsing data", $scan_proc->{inside_par},
       "Mode", $scan_proc->{mode},
      );

    if ( $scan_proc->{current_env} )
      {
	&{$scan_proc->{error_handler}}
	  ('Source ended before ', appr_env_notation($scan_proc->{current_env}), ' was closed');
      }
  }

sub convert_file
  #a ($source_file);
  {
    my $source_file = $_[0];
    notify_status('CONVERSION_START', $source_file);
    if ( $with_up_to_date_check && &{$up_to_date_tester}($source_file) )
      {
	notify_status('SOURCE_UP_TO_DATE', $source_file);
	return();
      }
    start_logging($source_file);
    convert(read_source($source_file), $source_file);
    check_after_conversion();
    &{$output_writer}($source_file);
    stop_logging();
    notify_status('CONVERSION_DONE', $source_file);
  }

# ------------------------------------------------------------------------------------------
# Misc. utilities
# ------------------------------------------------------------------------------------------

sub next_default_id
  {
    return($default_id_prefix . ++$last_default_id);
  }

sub check_class
  {
    my ($class, $ctr_type, $ctr_name) = @_;
    my $ctr;
    if ( $ctr_type eq 'CMD' )
      { $ctr = $scan_proc->{cmd_table}->{$ctr_name}; }
    elsif ( $ctr_type eq 'ENV' )
      { $ctr = $scan_proc->{env_table}->{$ctr_name}; }
    else
      { notify_programming_error('check_class', "Invalid control type specifier: $ctr_type"); }
    if ( defined($ctr->{classes}) && !grep($_ eq $class, @{$ctr->{classes}}) )
      {
        if ( $ctr_type eq 'CMD' )
          { &{$scan_proc->{error_handler}}
              ("Invalid class for command '\\$ctr_name': $class"); }
        else
          { &{$scan_proc->{error_handler}}
              ("Invalid class for environment '$ctr_name': $class"); }
      }
  }

init();
return(1);
