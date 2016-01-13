#!/usr/bin/perl -w
# -*- perl -*-

# Authors:  Tilman Rassy <rassy@math.tu-berlin.de>,
#           Christian Ruppert <ruppert@math.tu-berlin.de>

# $Id: mmtex.tpl,v 1.7 2008/02/07 13:59:37 rassy Exp $

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

use constant PROGRAM => "mmtex";
use constant VERSION => '$Revision: 1.7 $ ';
use Env qw(HOME PATH MMTEX_PREFIX MMTEXRC USER_MMTEXRC);
use Getopt::Long;

BEGIN
  {
    $MMTEX_PREFIX ||= '@mmtex-prefix@';
  }

use lib "$MMTEX_PREFIX/lib/perl";
use Cwd;
use File::Basename qw(basename);
use Mumie::Boolconst;
use Mumie::Logger qw(/.+/);
use Mumie::ExecFuncCalls;
use Mumie::Scanner qw($scan_proc);
use Mumie::MmTeX::Util qw(/.+/);
use Mumie::MmTeX::Parser qw(:LOG :WARNINGS $ignore_unknown_controls);
use Mumie::MmTeX::Converter qw(/.+/);
use Mumie::MmTeX::DclLoader qw($dcl_path $cml_dcl_options);
use Mumie::MmTeX::LibLoader qw($inc_path);
use Mumie::MmTeX::Serializer;
use Mumie::MmTeX::IOHelper qw(:ALL);
use Mumie::MmTeX::SelfDoc qw(:ALL);
use Mumie::XML::Writer qw(/.+/);
use Mumie::File qw(/.+/);

$SIG{__WARN__} = sub
  {
    print(STDERR @_);
    log_message("\nPERL WARNING: ", @_, "\n") if ( logging_is_ready() );
  };

my @_cmdline_params = @ARGV;

# --------------------------------------------------------------------------------
# Processing the command line
# --------------------------------------------------------------------------------

sub process_cmdline_config_file_options
  #a ()
  # Processes only the config file options, other parameters are passed through.
  {
    Getopt::Long::Configure("posix_default", "pass_through");
    # We use "posix_default" here to prevent the options from being abbreviatable,
    # which could interfere with the second option parsing.
    GetOptions
      (
       'config-file:s' =>  \$config_file,
       'user-config-file:s' =>  \$user_config_file,
       'no-config-file' => sub { $config_file = FALSE },
       'no-user-config-file' => sub { $user_config_file = FALSE },
      );
  }

sub process_cmdline
  #a ()
  # Processes all but the config file options, which should by processed before
  # by process_cmdline_config_file_options().
  {
    Getopt::Long::Configure("default", "bundling");
    GetOptions
      (
       # XML formatting options:
       'columns=s' => \&get_xml_max_column,
       'indent=s' => \&get_xml_indent_step,

       # Convert options:
       'dcl-opts=s' => \$cml_dcl_options,
       'dcl-path|D=s' => \$dcl_path,
       'inc-path|I=s' => \$inc_path,
       'force|f' => sub { $with_up_to_date_check = FALSE },
       'unless-up-to-date|F|u' => sub { $with_up_to_date_check = TRUE },
       'status|s' => \$print_status,
       'no-status|S' => sub { $print_status = FALSE },
       'strict' => sub { $ignore_unknown_controls = FALSE },
       'tolerant' => sub { $ignore_unknown_controls = TRUE },
       'target-root=s' => \$target_root_dir,
       'output|o=s' => \$output_file,
       'param=s' => \%params,
       'recursive|r' => sub { $recursive = TRUE },
       'non-recursive|R' => sub { $recursive = FALSE },
       'source-root=s' => \$source_root_dir,
       'special-char-mode=s' => \&get_xml_special_char_output_mode,
       'src-root=s' => \$source_root_dir,
       'trg-root=s' => \$target_root_dir,
       'abort-on-error' => sub { $abort_on_error = TRUE },
       'continue-on-error' => sub { $abort_on_error = FALSE },
       'warnings|w' => \$print_warnings,
       'xml-root=s' => \$xml_root_dir,
       'xsl-stylesheet=s' => \$xsl_stylesheet,

       # Logging options:
       'log|g' => \$write_log,
       'log-exclude=s' => sub { get_log_settings(@_, FALSE) },
       'log-expand-depth=s' => \&check_max_expand_depth_value,
       'log-include=s' => sub { get_log_settings(@_, TRUE) },
       'log-file=s' => \$log_file,
       'log-root=s'=> \$log_root_dir,
       'no-log|nolog|G' => sub { $write_log = FALSE },

       # Query options:
       'describe-cmd|help-cmd|c=s' => \&setup_describe_cmd,
       'describe-env|help-env|e=s' => \&setup_describe_env,
       'list-cmd-libs' => sub { $task = \&list_cmd_libs },
       'list-env-libs' => sub { $task = \&list_env_libs },
       'list-cmds' => sub { $task = \&list_cmds },
       'list-envs' => sub { $task = \&list_envs },
       'list-libs' => sub { $task = \&list_libs },
       'query-dcl|dcl|d=s' => \$dcl_to_query,
       'query-lib|lib|l=s' => \$lib_to_query,
       'xdoc-libs' => sub { $task = \&xdoc_libs },
       'xdoc' => sub { $task = \&xdoc },

       # Other options:
       'help|h' => sub { $task = \&show_help },
       'version|v' => sub { $task = \&show_version },
       'settings' => sub { $task = \&show_settings },
       'time|t' => \$print_elapsed_time,
       'no-time|notime|T' => \$print_elapsed_time,

      )
	or exit($!);
    if ( ( $task != \&perform_conversion ) && ( @ARGV ) )
      {
	die("Invalid parameter(s): ", join(' ', @ARGV), "\n");
      }
  }

sub get_xml_max_column
  # ($name, $value)
  {
    my ($name, $value) = @_;
    if ( ( $value =~ m/^[0-9]+$/ ) && ( $value > 0 ) )
      {
	$xml_max_column = $value - 1;
      }
    else
      {
	die("Illegal value for --$name: $value\n");
      }
  }

sub get_xml_indent_step
  # ($name, $value)
  {
    my ($name, $value) = @_;
    if ( $value =~ m/^[0-9]+$/ )
      {
	$xml_indent_step = $value;
      }
    else
      {
	die("Illegal value for --$name: $value\n");
      }
  }

sub get_xml_special_char_output_mode
  # ($name, $value)
  {
    my ($name, $value) = @_;

    my $mode
      = {
	 "as-entities" => "AS_ENTITIES",
	 "as-numcodes" => "AS_NUMCODES",
	 "literal" => "LITERAL",
	 "strictly-literal" => "STRICTLY_LITERAL",
	}
	->{$value};

    if ( $mode )
      {
	$xml_special_char_output_mode = $mode;
	$doc_special_char_output_mode = $mode;
      }
    else
      {
	die("Illegal value for --$name: $value\n");
      }
  }

sub get_log_settings
  # ($name, $value, $state)
  {
    my ($name, $value, $state) = @_;
    my @keys = split(',', $value);
    foreach my $key (@keys)
      {
	if ( $key eq 'cmds' )
	  {
	    $log_include_cmds = $state;
	  }
	elsif ( $key eq 'envs' )
	  {
	    $log_include_envs = $state;
	  }
	elsif ( $key eq 'tokens' )
	  {
	    $log_include_tokens = $state;
	  }
	elsif ( $key eq 'allowed-tokens' )
	  {
	    $log_include_allowed_tokens = $state;
	  }
	elsif ( $key eq 'output-functions' )
	  {
	    $log_include_output_functions = $state;
	  }
	else
	  {
	    die("Illegal key in --$name value: $key");
	  }
      }
  }

sub check_max_expand_depth_value
  {
    my ($key, $value) = @_;
    if (($value >= 0) && ($value <= 99))
      {
	$max_log_expand_depth = $value;
      }
    else
      {
	die("Illegal value for log-expand-depth: $value (please use 0 to 99)\n");
      }
  }

sub setup_describe_cmd
  {
    my ($name, $value) = @_;
    $cmd_to_describe = $_[1];
    $task = \&describe_cmd;
  }

sub setup_describe_env
  {
    my ($name, $value) = @_;
    $env_to_describe = $_[1];
    $task = \&describe_env;
  }

# --------------------------------------------------------------------------------
# Processing the config files
# --------------------------------------------------------------------------------

{
  my $config_code;
  my $user_config_code;

  sub process_config_files
    #a ()
    # Processes the global and user specific config files if available.
    {
      if ( ( $config_file ) && ( -r $config_file ) )
	{
	  $config_code = ${read_file($config_file)}
	    unless ( defined($config_code) );
	  eval($config_code);
	  die("$@\n") if ( $@ );
	}
      if ( ( $user_config_file ) && ( -r $user_config_file ) )
	{
	  $user_config_code = ${read_file($user_config_file)}
	    unless ( defined($user_config_code) );
	  eval($user_config_code);
	  die("$@\n") if ( $@ );
	}
    }
}

# --------------------------------------------------------------------------------
# Initializing
# --------------------------------------------------------------------------------

sub autoflush_on
  {
    my $current_filehandle = select(STDOUT);
    $| = 1;
    select($current_filehandle);
  }

sub init_global_variables
  {
    $log_file = FALSE;

    $xml_max_column = 80;
    $xml_indent_step = 2;

    $dcl_path = "$MMTEX_PREFIX/lib/mmtex/dcl";
    $inc_path = "$MMTEX_PREFIX/lib/mmtex/include";
    $xsl_dir = "$MMTEX_PREFIX/lib/mmtex/xsl";
    $dtd_dir = "$MMTEX_PREFIX/etc/mmtex/dtd";
    $css_dir = "$MMTEX_PREFIX/lib/mmtex/css";

    $config_file = ( $MMTEXRC || "$MMTEX_PREFIX/etc/mmtex/mmtexrc" );
    $user_config_file = ( $USER_MMTEXRC || "$HOME/.mmtexrc" );
    $output_file = "";
    $xsl_stylesheet = FALSE;
    $task = \&perform_conversion;
    $with_up_to_date_check = FALSE;
    $recursive = FALSE;
    $abort_on_error = TRUE;
    $print_elapsed_time = FALSE;
    $query_mode = FALSE;
    $cmd_to_describe = FALSE;
    $env_to_describe = FALSE;
    $dcl_to_query = FALSE;
    $lib_to_query = FALSE;
    $xdoc_xsl_stylesheet = "$xsl_dir/xdoc.2xhtml.xsl";
    %params = ();
    $source_file_regexp = '\.tex$';
    $include_regexp = undef();
    $exclude_regexp = undef();

    $exit_value = 0;
  }

sub init
  {
    autoflush_on();
    init_global_variables();
    process_cmdline_config_file_options();
    process_config_files();
    process_cmdline();
  }

sub init_all_modules
  {
    reset_init_all_modules();
    Mumie::Boolconst::init();
    Mumie::Text::init();
    Mumie::Logger::init();
    Mumie::ExecFuncCalls::init();
    Mumie::Balanced::init();
    Mumie::Scanner::init();
    Mumie::List::init();
    Mumie::MmTeX::Parser::init();
    Mumie::Hooks::init();
    Mumie::File::init();
    Mumie::XML::Characters::init();
    Mumie::XML::Writer::init();
    Mumie::MmTeX::Util::init();
    Mumie::MmTeX::Converter::init();
    Mumie::MmTeX::Serializer::init();
    Mumie::MmTeX::DclLoader::init();
    Mumie::MmTeX::LibLoader::init();
    Mumie::MmTeX::IOHelper::init();
    Mumie::MmTeX::SelfDoc::init();
  }

sub reset_init_all_modules
  {
    Mumie::Boolconst::reset_init();
    Mumie::Text::reset_init();
    Mumie::Logger::reset_init();
    Mumie::ExecFuncCalls::reset_init();
    Mumie::Balanced::reset_init();
    Mumie::Scanner::reset_init();
    Mumie::List::reset_init();
    Mumie::MmTeX::Parser::reset_init();
    Mumie::Hooks::reset_init();
    Mumie::File::reset_init();
    Mumie::XML::Characters::reset_init();
    Mumie::XML::Writer::reset_init();
    Mumie::MmTeX::Util::reset_init();
    Mumie::MmTeX::Converter::reset_init();
    Mumie::MmTeX::Serializer::reset_init();
    Mumie::MmTeX::DclLoader::reset_init();
    Mumie::MmTeX::LibLoader::reset_init();
    Mumie::MmTeX::IOHelper::reset_init();
    Mumie::MmTeX::SelfDoc::reset_init();
  }

sub reinit_for_new_conversion
  {
    @ARGV = @_cmdline_params;
    init_all_modules();
    init();
  }

# --------------------------------------------------------------------------------
# Converting sources
# --------------------------------------------------------------------------------

sub accept_source_file
  {
    my $file = $_[0];
    return ( $file =~ m/$source_file_regexp/ &&
	     ( !$include_regexp || $file =~ m/$include_regexp/ ) &&
	     ( !$exclude_regexp || $file !~ m/$exclude_regexp/ ) );
  }

sub process_files
  #a ($files, $depth, $reinit_needed)
  {
    my @files = @{$_[0]};
    my $depth = ( $_[1] || 0 );
    my $reinit_needed = ( $_[1] || FALSE );

    foreach my $file (@files)
      {
	$file =~ s/\/$//;
	if ( -d $file )
	  {
	    if ( $recursive || ( $depth == 0 ) )
	      {
		opendir(DIR, $file);
		my @dir_files = grep
		  (
		   { ( $_ ne '.' ) && ( $_ ne '..' ) &&
		     ( accept_source_file($_) || ( -d "$file/$_" ) ) }
		   readdir(DIR)
		  );
		foreach my $dir_file (@dir_files)
		  {
		    $dir_file = $file . '/' . $dir_file;
		  }
		closedir(DIR);
		process_files(\@dir_files, $depth+1, $reinit_needed);
	      }
	  }
	else
	  {
	    reinit_for_new_conversion() if ( $reinit_needed );
	    eval { convert_file($file) };
	    $reinit_needed = TRUE;
	    if ( $@ )
	      {
                $exit_value = 1;
		($abort_on_error ? die("$@\n") : print("$@\n"));
	      }
	  }
      }
  }

sub perform_conversion
  {
    my @files = @ARGV;
    @files || (@files = (get_working_dir()));
    process_files(\@files);
  }

# --------------------------------------------------------------------------------
# Query tasks
# --------------------------------------------------------------------------------

sub describe_cmd_or_env
  {
    my ($type, $to_describe, $dcl_to_query, $lib_to_query) = @_;

    $query_mode = TRUE;
    if ( ( $dcl_to_query ) && ( $lib_to_query ) )
      {
	die("Please specify either a document class or a library, not both\n");
      }
    elsif ( $dcl_to_query )
      {
	dump_dcl_cmd_or_env_doc($type, $to_describe, $dcl_to_query);
      }
    elsif ( $lib_to_query )
      {
	dump_lib_cmd_or_env_doc($type, $to_describe, $lib_to_query);
      }
    else
      {
        dump_all_libs_cmd_or_env_doc($type, $to_describe);
      }
  }

sub list_cmds_or_envs
  {
    my ($type, $dcl_to_query, $lib_to_query) = @_;

    $query_mode = TRUE;
    if ( ( $dcl_to_query ) && ( $lib_to_query ) )
      {
	die("Please specify either a document class or a library, not both\n");
      }
    elsif ( $dcl_to_query )
      {
	dump_cmd_or_env_list($type, undef(), $dcl_to_query);
      }
    elsif ( $lib_to_query )
      {
	dump_cmd_or_env_list($type, [$lib_to_query]);
      }
    else
      {
	dump_cmd_or_env_list($type);
      }
  }

sub list_cmd_or_env_exporting_libs
  {
    my ($type, $dcl_to_query, $lib_to_query) = @_;

    $query_mode = TRUE;
    if ( ( $dcl_to_query ) && ( $lib_to_query ) )
      {
	die("Please specify only a document class, no library\n");
      }
    elsif ( $dcl_to_query )
      {
	dump_cmd_or_env_exporting_lib_list($type, $dcl_to_query, TRUE);
      }
    elsif ( $lib_to_query )
      {
	die("Please specify a document class, no library\n");
      }
    else
      {
	die("No document class specified\n");
      }
  }

sub list_cmds
  {
    list_cmds_or_envs('CMD', $dcl_to_query, $lib_to_query);
  }

sub describe_cmd
  {
    describe_cmd_or_env('CMD', $cmd_to_describe, $dcl_to_query, $lib_to_query);
  }

sub list_envs
  {
    list_cmds_or_envs('ENV', $dcl_to_query, $lib_to_query);
  }

sub list_cmd_libs
  {
    list_cmd_or_env_exporting_libs('CMD', $dcl_to_query, $lib_to_query);
  }

sub list_env_libs
  {
    list_cmd_or_env_exporting_libs('ENV', $dcl_to_query, $lib_to_query);
  }

sub describe_env
  {
    describe_cmd_or_env('ENV', $env_to_describe, $dcl_to_query, $lib_to_query);
  }

sub list_libs
  {
    dump_lib_list();
  }

sub xdoc
  {
    if (not $output_file)
      {
	$output_file = 'XDOC_';
	$output_file .= $lib_to_query . '_LIB' if $lib_to_query;
	$output_file .= $dcl_to_query . '_DCL' if $dcl_to_query;
	$output_file .= '.xml';
      }

    $query_mode = TRUE;
    if ( ( $dcl_to_query ) && ( $lib_to_query ) )
      {
	die("Please specify either a document class or a library, not both\n");
      }
    elsif ( $dcl_to_query )
      {
	write_dcl_xdoc($dcl_to_query, $output_file);
      }
    elsif ( $lib_to_query )
      {
	write_lib_xdoc($lib_to_query, $output_file);
      }
    else
      {
	die("No document class or library specified\n");
      }
  }

sub xdoc_libs
  {
    if (not $output_file)
      {
	$output_file = 'XDOC_';
	$output_file .= $dcl_to_query . '_DCL_';
	$output_file .= "libs.xml";
      }

    $query_mode = TRUE;
    if ( ( $dcl_to_query ) && ( $lib_to_query ) )
      {
	die("Please only a document class, no library\n");
      }
    elsif ( $dcl_to_query )
      {
	write_lib_list_xdoc($dcl_to_query, $output_file);
      }
    elsif ( $lib_to_query )
      {
	die("Please specify a document class, no library\n");
      }
    else
      {
	die("No document class specified\n");
      }
  }

# ------------------------------------------------------------------------------------------
# Help, version and settings information
# ------------------------------------------------------------------------------------------

sub show_help
  {
    print("
Usage:
  " . PROGRAM . " [OPTIONS] [SOURCES]
Convert options:
   --output=FILENAME, -o FILENAME   Output file
   --dcl-opts=OPTIONS               Document class options
   --dcl-path=PATH, -D PATH         Path to document classes
   --special-char-mode=MODE         Set special character mode (see below)
   --xsl-stylesheet=URI             Default xsl stylesheet
   --tolerant                       Ignore unknown tokens, commands, and
                                    environments
   --strict                         Fail on unknown tokens, commands, or
                                    environments
   --recursive, -r                  Recursively parse subdirectories
   --unless-up-to-date, -u, -F      Convert target only if it is not up to date
   --force, -f                      Convert even if targets are up to date
   --abort-on-error                 Abort multiple conversion on error
   --continue-on-error              Continue multiple conversion on error
   --param NAME=VALUE               Set parameter NAME to VALUE
XML output options:
   --indent=NUMBER                  Indent each level by NUMBER columns
   --columns=NUMBER                 Set text width to NUMBER columns
Path options:
   --inc-path=PATH, -I PATH         Path to libraries
   --source-root=PATH               Root directory of the sources
   --log-root=PATH                  Root directory of log files
   --xml-root=PATH                  Root directory of the XML output files
   --target-root=PATH               Root directory of the final targets
Logging options:
   --log, -g                        Print log messages
   --no-log, --nolog, -G            Don't print log messages
   --log-file=FILENAME              Log file
   --log-exclude=GROUP1,GROUP2,...  Don't log GROUP1, GROUP2, ... (s.b.)
   --log-expand-depth=NUMBER        Depth of expanding refs in logged data
   --log-include=GROUP1,GROUP2,...  Log extra data GROUP1, GROUP2, ... (s.b.)
Warning options:
   --warnings, -w                   Print warnings
   --no-warnings, --nowarnings, -W  Don't print warnings
Status message options:
   --status, -s                     Print status messages
   --no-status, --nostatus, -S      Don't print status messages
Query options:
   --dcl=CLASS, -d CLASS            Set documentclass for query options
   --describe-cmd=CMD, -c CMD       Describe command CMD
   --describe-env=ENV, -e ENV       Describe environment ENV
   --lib=LIBRARY, -l LIBRARY        Set library  for query options
   --list-cmd-libs                  List command exporting libraries
   --list-env-libs                  List environment exporting libraries
   --list-cmds                      List commands
   --list-envs                      List environments
   --list-libs                      List all libraries in the library path
   --output=FILENAME, -o FILENAME   Output file
   --query-dcl=DCL                  Same as --dcl=CLASS
   --query-lib=LIBRARY              Same as --lib=LIBRARY
   --xdoc-libs                      Write library list for selected dcl in XML
   --xdoc                           Write commmand and evironment documentation
                                    in XML
Config file options:
   --config-file=FILENAME           Set the global configuration file
   --user-config-file=FILENAME      Set the user configuration file.
   --no-config-file                 Don't load the global configuration file.
   --no-user-config-file            Don't load the user configuration file
Other options:
   --settings                       Print settings and exit
   --time, -t                       Print elapsed CPU time
   --no-time, --notime, -T                    Don't print elapsed CPU time
   --help, -h                       Print this message and quit
   --version, -v                    Print version information and quit
Special character modes:
   as-entities                      As entities (e.g., &uuml;)
   as-numcodes                      As numerical codes (e.g., &#252;)
   literal                          Literally (e.g., ü), except <,>, and &
   strictly-literal                 Literally including <,>, and &
Log include/exclude groups:
   cmds                             Log command table
   envs                             Log environment table
   tokens                           Log token table
   allowed-tokens                   Log list of alowed tokens
   output-functions                 Log output function table
   output-list                      Log output list

");
   }

sub show_version
  {
    print
      (
       VERSION, "\n",
       "using\n",
       "  Mumie::Scanner " , Mumie::Scanner::VERSION , "\n",
       "  Mumie::MmTeX::Parser " , Mumie::MmTeX::Parser::VERSION , "\n",
       "  Mumie::MmTeX::Converter " , Mumie::MmTeX::Converter::VERSION , "\n",
      );
  }

sub show_settings
  {
    print("
\$config_file = $config_file
\$dcl_path = $dcl_path
\$inc_path = $inc_path
\$ignore_unknown_controls = $ignore_unknown_controls
\$log_include_allowed_tokens = $log_include_allowed_tokens
\$log_include_cmds = $log_include_cmds
\$log_include_envs = $log_include_envs
\$log_include_output_functions = $log_include_output_functions
\$log_include_tokens = $log_include_tokens
\$log_file = $log_file
\$log_root_dir =$log_root_dir
\$max_log_expand_depth = $max_log_expand_depth
\$MMTEX_PREFIX = $MMTEX_PREFIX
\$MMTEXRC = ", ( $MMTEXRC || '-undef-' ), "
\$USER_MMTEXRC = ", ( $USER_MMTEXRC || '-undef-' ), "
\$output_file = $output_file
\$print_elapsed_time = $print_elapsed_time
\$print_status = $print_status
\$print_warnings = $print_warnings
\$source_root_dir = $source_root_dir
\$target_root_dir = $target_root_dir
\$user_config_file = $user_config_file
VERSION = ", VERSION, "
\$with_up_to_date_check = $with_up_to_date_check
\$xml_indent_step = $xml_indent_step
\$xml_max_column = $xml_max_column
\$xml_root_dir = $xml_root_dir
\$xml_special_char_output_mode = $xml_special_char_output_mode
\$xsl_stylesheet = $xsl_stylesheet
\$write_log = $write_log
");
  }

# --------------------------------------------------------------------------------
# Main function
# --------------------------------------------------------------------------------

sub main
  {
    init();
    my $start_time = (times())[0];
    &{$task}();
    my $elapsed_time = (times())[0] - $start_time;
    print("Elapsed time: $elapsed_time CPU seconds\n") if ( $print_elapsed_time );
    exit($exit_value);
  }

main();
