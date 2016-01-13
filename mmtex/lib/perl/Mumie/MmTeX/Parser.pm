package Mumie::MmTeX::Parser;

# Authors:  Tilman Rassy <rassy@math.tu-berlin.de>,
#           Christian Ruppert <ruppert@math.tu-berlin.de>

# $Id: Parser.pm,v 1.129 2007/07/11 15:56:15 grudzin Exp $

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
#1                                  Description
# ------------------------------------------------------------------------------------------
#
# LaTeX Parser build on top of Mumie::Scanner.



# ------------------------------------------------------------------------------------------
#1 Additional fields in $scan_proc
# ------------------------------------------------------------------------------------------
#
# The latex parser adds some additional fields to `Mumie::MmTeX::Scanner::$scan_proc`.
# They are:
#
#H
#  additinal_parse_logger &
#                      Reference to a function called by `log_parse_data` to log
#                      additional data (optional).
#  cmd_table         & Reference to a hash containing data to handle LaTeX commands.
#                      This hash is called the \emph{command table}. \\
#  env_table         & Reference to a hash containing data to handle LaTeX
#                      environments. This hash is called the \emph{environment
#                      table}. \\
#  current_env       & Name of the environment the actual scan position is in.
#  inside_par        & 'TRUE' or 'FALSE' depending on whether
#                      the actual scan position is inside a paragraph or not. \\
#  man_args          & Reference to a list containing the mandatory arguments of a
#                      command or environment. \\
#  mode              & String denoting the \emph{LaTeX mode} at the actual scan
#                      position (see below). \\
#  opt_args          & Reference to a list containing the optional arguments of a
#                      command or environment. \\
#  output_functions  & Reference to a hash mapping output types (see below) to
#                      references of functions which generate output. \\
#  par_enabled       & Whether the text is grouped in paragraphes at the actual scan
#                      position or not. 'TRUE' or 'FALSE'.\\
#  parsing_data      & Whether data is parsed at the currend scan position. `Data' may
#                      be counter numbers, key words, etc. \\
#  pos_man_args      & Reference to a list containing the positions of the mandatory
#                      arguments of a command or environment. \\
#  pos_opt_args      & Reference to a list containing the positions of the optional
#                      arguments of a command or environment. \\
#  post_block_begin_hook &
#                      Reference to a function. This is run by `start_block`
#                      as the very last thing it does. \\
#  post_block_end_hook &
#                      Reference to a function. This is run by `close_block`
#                      as the very last thing it does. \\
#  post_exec_cmd_hook & Reference to a function to run imediately after the execute
#                      function of a command has been run. Called with arguments.
#                      The first of them is the command name, the remaining ones are
#                      the arguments which are passed to the execute function. \\
#  post_exec_env_begin_hook &
#                      Reference to a function to run imediately after the begin
#                      function of an environment has been run. Called with arguments. The
#                      first of them is the command name and the remaining ones are
#                      the arguments which are passed to the begin function. \\
#  post_exec_env_end_hook &
#                      Reference to a function to run imediately after the end
#                      function of an environment has been run. Called with one argument,
#                      the environment name. \\
#  post_token_handler & Reference to a function which is called by
#                      `call_token_handler()` after the handler of
#                      the token has been called.
#  pre_block_begin_hook &
#                      Reference to a function. This is run by `start_block`
#                      as the very first thing it does.
#  pre_block_end_hook &
#                      Reference to a function. This is run by `close_block`
#                      as the very first thing it does.
#  pre_cmd_hook      & Reference to a function run by `process_cmd()` as the
#                      very first thing it does. Called with one argument, the command
#                      name. \\
#  pre_env_begin_hook & Reference to a function run by `process_env_begin()`
#                      after the environment name has been identified. Called with one
#                      argument, the environment name. \\
#  pre_exec_cmd_hook & Reference to a function to run imediately before the execute
#                      function of a command is run. This hook is called with arguments.
#                      The first one is the command name, the remaining ones are
#                      the arguments which are passed to the execute function. \\
#  pre_exec_env_begin_hook &
#                      Reference to a function to run imediately before the begin
#                      function of an environment is run. Called with arguments. The
#                      first of them is the command name and the remaining ones are
#                      the arguments which are passed to the begin function. \\
#  pre_exec_env_end_hook &
#                      Reference to a function to run imediately before the end
#                      function of an environment is run. Called with one
#                      argument, the environment name. \\
#  pre_process_env_begin_hook &
#                      Reference to a function run by `process_env_begin()`
#                      as very the first thing it does. Called with no arguments. \\
#  pre_process_env_end_hook &
#                      Reference to a function run by `process_env_end()`
#                      as very the first thing it does. Called with no argument. \\
#  pre_env_end_hook  & Reference to a function run by `process_env_end()`
#                      after the environment name has been identified. Called with one
#                      argument, the environment name. \\
#  pre_token_handler & Reference to a function which is called by
#                      `call_token_handler()` before the handler of
#                      the token is called. \\
#/H


# ------------------------------------------------------------------------------------------
#1 The command table
# ------------------------------------------------------------------------------------------
#
# The command table should map each command name to a reference to a hash of the
# following form
#
#H
#  execute_function  & Reference to a function which generates the output. \\
#  disabled          & Flag to indicate that the command is not allowed at the
#                      current position (boolean, optional). \\
#  is_alias_for      & Name of a second command for which this one is an alias. If
#                      this is set, all other entries of the hash will be ignored.
#  is_par_closer     & Whether this command closes a paragraph if it occurs
#                      inside a paragraph (boolean). \\
#  is_par_starter    & Whether this command starts a new paragraph if it occurs
#                      between two paragraphs (boolean). \\
#  num_opt_args      & Number of optional arguments. \\
#  num_man_args      & Number of mandatory arguments. \\
#  post_hook         & Reference to a function to run imediately after the execute
#                      function has been run. Called with the same arguments as the
#                      execute function. (Optional)\\
#  pre_hook          & Reference to a function to run imediately before the execute
#                      function is run. Called with the same arguments as the
#                      execute function. (Optional)\\
#  pref_man_arg      & Whether this command has a preferred mandatory argument.
#/H


# ------------------------------------------------------------------------------------------
#1 The environment table
# ------------------------------------------------------------------------------------------
#
# The environment table should map each environment name to a reference of a hash of the
# following form
#
#H
#  begin_disabled    & Optional flag to indicate that the environment must not be
#                      opened at the current position (boolean).
#  end_disabled      & Optional flag to indicate that the environment must not be
#                      closed at the current position (boolean). \\
#  begin_function    & Reference to a function which is called when the environent
#                      starts.  \\
#  end_function      & Reference to a function which is called when the environent
#                      ends.  \\
#  early_pre_begin_hook &
#                      Reference to a function to run immediately after
#                      $scan_proc->{pre_env_begin_hook} has been run. \\
#  early_pre_end_hook &
#                      Reference to a function to run immediately before
#                      $scan_proc->{pre_env_end_hook} has been run. \\
#  is_alias_for      & Name of a second environment for which this one is an alias.
#                      If this is set, all other entries of the hash will be ignored.
#  is_par_closer     & Whether this environment closes a paragraph if it occurs
#                      inside a paragraph (boolean). \\
#  is_par_starter    & Whether this environment starts a new paragraph if it occurs
#                      between two paragraphs (boolean). \\
#  late_post_end_hook & Reference to a function to run after the end function has been
#                      run and after the scan process has been reset. Called with the
#                      same arguments as the execute function. (Optional)\\
#  num_opt_args      & Number of optional arguments. \\
#  num_man_args      & Number of mandatory arguments. \\
#  post_begin_hook   & Reference to a function to run imediately after the begin
#                      function has been run. Called with the same arguments as the
#                      execute function. (Optional)\\
#  post_end_hook     & Reference to a function to run imediately after the end
#                      function has been run, in particular before the scab process is
#                      reset. Called with the same arguments as the execute function.
#                      (Optional)\\
#  pre_begin_hook    & Reference to a function to run imediately before the begin
#                      function is run. Called with the same arguments as the
#                      execute function. (Optional)\\
#  pre_end_hook      & Reference to a function to run imediately before the end
#                      function is run. Called with the same arguments as the
#                      execute function. (Optional)\\
#/H
#

# ------------------------------------------------------------------------------------------
#1 Output Types
# ------------------------------------------------------------------------------------------
#
# The final output is generated by so called \emph{output functions} which
# are called from the parser on appropriate occasions. They must be implemented
# by the program which uses this module. Each such function refers to one of the
# following \emph{output types}:
#
#H
#  ign_whitesp       & Whitespaces that can be ignored \\
#  plain_text        & Plain text \\
#  begin_par         & Beginning of a paragraph \\
#  end_par           & End of a paragraph \\
#  special_char      & Special character, coded literally, like ¹º»¼½¾¿ÀÁÂÃ... \\
#  umlaut            & German umlaut (including sharp "s"), coded as "a, "A, ... \\
#  tbl_sep           & Table cell separator \\
#  comment           & Comment \\
#/H

# ------------------------------------------------------------------------------------------
#1 LaTeX modes
# ------------------------------------------------------------------------------------------
#
# When going throw the source, the parser is always in one of the following
# so-called \emph{LaTeX modes}:
#
#H
#  PREAMBLE & Mode when inside the LaTeX preamble, i.e., before the
#             'document' environment. \\
#  NORMAL   & Mode when inside the <code>document</em> environment but not
#             inside a mathematical environment (including $ ... $). \\
#  MATH     & Mode when inside a mathematical environment (including $ ... $). \\
#/H

# ------------------------------------------------------------------------------------------
#1 Tokens
# ------------------------------------------------------------------------------------------
#
#2 Basic token types
#
#H
#  ign_whitesp       & Whitespaces that can be ignored \\
#  plain_text        & Plain text \\
#  nonpar_whitesp    & Whitespaces that doesn't indicate a paragraph boundry \\
#  par               & Paragraph boundary \\
#  cmd               & Command \\
#  one_char_cmd      & Command consisting of one character \\
#  begin_block       & Beginning of a {...}-Block
#  end_block         & End of a {...}-Block
#  special_char      & Special character, coded literally, like ¹º»¼½¾¿ÀÁÂÃ...
#  xml_special_char  & One of the characters &,<,>
#  double_quote      & Double quote, i.e., '"' \\
#  umlaut            & German umlaut (including sharp "s"), coded as "a, "A, ... \\
#  tbl_sep           & Table column delimiter \\
#  comment           & Comment
#/H
#
#2 Compound token types
#
#H
#   opt_arg          & Optional argument \\
#   man_arg          & Mandatory argument
#/H


# ------------------------------------------------------------------------------------------
#1 The property table
# ------------------------------------------------------------------------------------------
#
# The LaTeX parser adds an additional field called '"handler"' to the property
# table of each token (see {-->mumie::util::Scanner::$scan_proc
# '$scan_proc'}). This field should contain a reference to a function that
# handles tokens of the respective type. 
#
# Furthermore, the LaTeX parser adds a field called 'args' to the property table
# of the 'opt_arg' and 'man_arg' tokens which should contain a
# reference to a list. This list is used to store the arguments found in the last optional
# resp. mandatory argument parse process.
#
# So, the property table may be specified as
#
#H
#  tester  & Reference to the function testing for a token of the respective
#            type \\
#  handler & Reference to the function handling a token of the respective
#            type \\
#/H
#
# for tokens other than 'opt_arg' and 'man_arg', and
#
#H
#  tester   & Reference to the function testing for a token of the respective
#             type \\
#  handler  & Reference to the function handling a token of the respective
#             type \\
#  args     & Reference to a list containing the arguments found in the
#             last argument parse process \\
#  pos_args & Reference to a list containing the positions of the arguments
#             found in the last argument parse process \\
#/H
#
# for the 'opt_arg' and 'man_arg' tokens.


use constant VERSION => '$Revision: 1.129 $';

use Mumie::Boolconst;
use Mumie::Logger qw(/^.+$/);
use Mumie::Text qw(/^.+$/);
use Mumie::List qw(/^.+$/);
use Mumie::Balanced qw(/^.+$/);
use Mumie::Hooks qw(/^.+$/);
use Mumie::Scanner qw(/^.+$/ !new_scan_proc !reset_scan_proc);
use Mumie::MmTeX::Util qw(:ALL);

require Exporter;

@ISA = qw(Exporter);

{
  my @_LOG =
    (
     '$log_include_cmds',
     '$log_include_envs',
     '$log_include_tokens',
     '$log_include_allowed_tokens',
     '$log_include_output_functions',
     'log_parse_data',
    );

  my @_SCAN_PROC =
    (
     'new_scan_proc',
     'reset_scan_proc',
    );

  my @_WARNINGS =
    (
     'notify_warning',
     '$print_warnings',
     '$default_warning_handler',
    );

  my @_CMDS =
    (
     'remove_cmds',
     'own_cmd_table',
    );

  my @_ENVS =
    (
     '$env_name_provider',
     'remove_envs',
     'own_env_table',
     'appr_env_notation',
    );

  my @_TOKENS =
    (
     'own_allowed_tokens',
     'allow_tokens',
     'disallow_tokens',
     'get_vital_tokens',
    );

  my @_TOKEN_TYPES =
    (
     '@preamble_token_types',
     '@basic_token_types',
     '@data_token_types',
     '@container_token_types',
    );

  my @_PARS =
    (
     'start_par',
     'close_par',
     'start_par_if_necessary',
     'close_par_if_necessary',
     'close_par_if_in_par',
     'check_par_before_token',
     'check_par_before_cmd',
     'check_par_before_env',
    );

  my @_BLOCKS =
    (
     'start_block',
     'close_block',
    );

  my @_UTILS =
    (
     '%other_paren',
     '$class_regexp',
     '$nonnewline_whitesp_chars',
     '$nonnewline_nonwhitesp_chars',
     'plain_text_regexp',
    );

  my @_ALL =
    (
     @_LOG,
     @_SCAN_PROC,
     @_WARNINGS,
     @_CMDS,
     @_ENVS,
     @_TOKENS,
     @_TOKEN_TYPES,
     @_PARS,
     @_BLOCKS,
     @_UTILS,
     'look_ahead',
     '$default_token_table',
     'call_token_handler',
     '$ignore_unknown_controls',
    );

  @EXPORT_OK = @_ALL;

  %EXPORT_TAGS
    = (
       'ALL' => \@_ALL,
       'LOG' => \@_LOG,
       'SCAN_PROC' => \@_SCAN_PROC,
       'WARNINGS' => \@_WARNINGS,
       'CMDS' => \@_CMDS,
       'ENVS' => \@_ENVS,
       'TOKENS' => \@_TOKENS,
       'TOKEN_TYPES' => \@_TOKEN_TYPES,
       'PARS' => \@_PARS,
       'BLOCKS' => \@_BLOCKS,
       'UTILS' => \@_UTILS,
    );
}

# ------------------------------------------------------------------------------------------
#1 Initializing the default token table
# ------------------------------------------------------------------------------------------

sub plain_text_regexp
  #a ($characters)
  # Auxiliary function to compose certain regular expressions from character sets.
  {
    my $characters = $_[0];

    # Regular expression fragments:

    # Whitespace character except newline:
    my $_s = "[$nonnewline_whitesp_chars]";

    # Non-whitespace plain text character:
    my $_a = "[$characters]";

    # Plain text character except newline:
    my $_x ="[$characters \\t]";

    #  Sequence of plain text characters with at least one non-whitespace character
    my $_xax = "$_x*$_a$_x*";

    #  The plain text regular expression.
    #  It has the following structure, where rest-par denotes the regular
    #  expression that makes a \n a paragraph boundry, i.e., rest-par is $_s*\n.
    #
    #    $_xax (\n $_xax)* (\n, no rest-par follows)?
    #  /                                              \
    # (                    ($_xax               )      )
    #  \                  /                       \   /
    #    \n ($_xax \n)*  (                         )
    #                     \                       /
    #                      (no rest-par follows)

    my $regular_expression
      = "(?:$_xax(?:\\n$_xax)*(?:\\n(?!$_s*\\n))?|\\n(?:$_xax\\n)*(?:(?:$_xax)|(?!$_s*\\n)))";

    return($regular_expression);
  }

sub init_default_token_table
  {
    # -------------------------------------------------------------------------
    # Auxiliary regular expressions
    # -------------------------------------------------------------------------

    my $_s = "[$nonnewline_whitesp_chars]";
    # Whitespace character except newline

    my $_plain_text = plain_text_regexp($nonnewline_nonwhitesp_chars);
    # Plain text

    my $_plain_text_extended = plain_text_regexp("A-Za-z0-9\\`\\'.,;:?!@()\\[\\]\\*\\/\\|+-=<>_\\\$\&");
    # Plain text including characters that have a special meaning in math mode.

    my $_nonpar_whitesp = "(?:$_s+\\n?$_s*|$_s*\\n?$_s+)(?!\\s)";
    # Whitespaces that don't indicate a paragraph boundry.
    # The regular expression has the following structure (see init_regexps()):
    #
    #    $_s+ \n? $_s*
    #  /               \
    # (                 )
    #  \               /
    #    $_s* \n? $_s+

    my $_par = "$_s*\\n(?:$_s*\\n)+$_s*";
    # Paragraph boundry

    my $_cmd_name = "[A-Za-z]+\\*?";
    # Command name

    my $_one_char_cmd = "[^a-zA-Z]";
    # Command consisting of one character

    my $_plain_arg = "\\S";
    # A one-character argument

    my $_unbr_arg = '\\S+';
    #

    my $_special_char = "[¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ]";
    # Special characters

    my $_xml_special_char = "[<>]";
    # The XML special characters < and >.

    # -------------------------------------------------------------------------
    # The default token table
    # -------------------------------------------------------------------------

    $default_token_table->{ign_whitesp}
      = {
	 "tester" => sub { test_regexp("\\s+") },
	 "handler" => sub { &{$scan_proc->{output_functions}->{ign_whitesp}}() },
	};

    $default_token_table->{plain_text}
      = {
	 "tester"  => sub { test_regexp("$_plain_text") },
	 "handler" => sub
	 {
	   my $text = $scan_proc->{last_token};
	   unless ( $text =~ m/^\s*$/ )
	     {
	       start_par_if_necessary();
	     }
	   &{$scan_proc->{output_functions}->{plain_text}}($text);
	 }
	};

    $default_token_table->{plain_text_extended}
      = {
	 "tester"  => sub { test_regexp("$_plain_text_extended") },
	 "handler" => sub
	 {
	   &{$scan_proc->{output_functions}->{plain_text}}($scan_proc->{last_token});
	 }
	};

    $default_token_table->{nonpar_whitesp}
      = {
	 "tester"  => sub { test_regexp("$_nonpar_whitesp") },
	 "handler" => sub
	 {
	   if (! between_pars() ) {
	     &{$scan_proc->{output_functions}->{plain_text}}($scan_proc->{last_token});
	   }
	 }
	};

    $default_token_table->{par}
      = {
	 "tester" => sub { test_regexp("$_par") },
	 "handler" => sub
	 {
	   if ( $scan_proc->{par_enabled} )
	     {
	       if ( $scan_proc->{inside_par} )
		 {
		   close_par();
		 }
	     }
	   else
	     {
	       &{$scan_proc->{output_functions}->{plain_text}}
		 ($scan_proc->{last_token});
	     }
	 }
	};

    $default_token_table->{cmd}
      = {
	 "tester" => sub { test_regexp("\\\\($_cmd_name)", 1) },
	 "handler" => sub
	 {
	   my $cmd_name = $scan_proc->{last_token};
	   if ( $cmd_name eq "begin")
	     {
	       process_env_begin();
	     }
	   elsif ( $cmd_name eq "end" )
	     {
	       process_env_end();
	     }
	   else
	     {
	       process_cmd($cmd_name);
	     }
	 }
	};

    $default_token_table->{one_char_cmd}
      = {
	 "tester" => sub { test_regexp("\\\\($_one_char_cmd)", 1) },
	 "handler" => sub
	 {
	   my $cmd_name = $scan_proc->{last_token};
	   process_cmd($cmd_name, 'ALLOW_PLAIN_ARGS');
	 }
	};

    $default_token_table->{special_char}
      = {
	 "tester" => sub { test_regexp($_special_char, 0) },
	 "handler" => sub
	 {
	   &{$scan_proc->{output_functions}->{special_char}}($scan_proc->{last_token});
	 }
	};

    $default_token_table->{xml_special_char}
      = {
	 "tester" => sub { test_regexp($_xml_special_char, 0) },
	 "handler" => sub
	 {
	   &{$scan_proc->{output_functions}->{special_char}}($scan_proc->{last_token});
	 }
	};


    $default_token_table->{double_quote}
      = {
	 "tester" => sub { test_regexp("\\\"", 0) },
	 "handler" => sub
	 {
	   &{$scan_proc->{output_functions}->{double_quote}}($scan_proc->{last_token});
	 }
	};

    $default_token_table->{tbl_sep}
      = {
	 "tester" => sub { test_regexp("&") },
	 "handler" => sub
	 {
	   &{$scan_proc->{output_functions}->{tbl_sep}}()
	 }
	};

    $default_token_table->{comment}
      = {
	 "tester" => sub { test_regexp("%(.*(?:\\n(?!\\s*\\n))?)") },
	 "handler" => sub
	 {
	   &{$scan_proc->{output_functions}->{comment}}($scan_proc->{last_token})
	 }
	};

    $default_token_table->{opt_arg}
      = {
	 "tester" => sub { skip_whitesp(); test_balanced("\\[", "\\]") },
	 "handler" => \&handle_opt_arg,
	};

    $default_token_table->{man_arg}
      = {
	 "tester" => sub { skip_whitesp(); test_balanced("(?<!\\\\){", "(?<!\\\\)}") },
	 "handler" => \&handle_man_arg,
	};

    $default_token_table->{arg_sep}
      = {
	 "tester" => sub { test_regexp("\\s+") },
	 "handler" => sub { },
	};

    $default_token_table->{plain_opt_arg}
      = {
	 "tester" => sub { test_regexp($_plain_arg) },
	 "handler" => \&handle_opt_arg,
	};

    $default_token_table->{plain_man_arg}
      = {
	 "tester" => sub { test_regexp($_plain_arg) },
	 "handler" => \&handle_man_arg,
	};

    $default_token_table->{unbr_opt_arg}
      = {
	 "tester" => sub { test_regexp($_unbr_arg) },
	 "handler" => \&handle_opt_arg,
	};

    $default_token_table->{unbr_man_arg}
      = {
	 "tester" => sub { test_regexp($_unbr_arg) },
	 "handler" => \&handle_man_arg,
	};

    $default_token_table->{begin_block}
      = {
	 "tester" => sub { test_regexp("(?<!\\\\){") },
	 "handler" => \&start_block
	};

    $default_token_table->{end_block}
      = {
	 "tester" => sub { test_regexp("(?<!\\\\)}") },
	 "handler" => \&close_block
	};

    foreach my $token_type ("double_quote", "special_char", "xml_special_char") {
      $default_token_table->{$token_type}->{is_par_starter} = TRUE;
    }
  };

# --------------------------------------------------------------------------------
# The init function
# --------------------------------------------------------------------------------

sub init
  {
    return() if ( $init_done );

    # -------------------------------------------------------------------------
    # Misc. global variables
    # -------------------------------------------------------------------------

    $log_include_cmds = FALSE;
    $log_include_envs = FALSE;
    $log_include_tokens = FALSE;
    $log_include_allowed_tokens = FALSE;
    $log_include_output_functions = FALSE;

    $print_warnings = FALSE;
    # Whether warnings are to be printed or not.

    $warning_message_margin_left = undef();
    $warning_message_headline = 'WARNING: ';

    $default_warning_handler = \&notify_warning;
    # The default warning handler.

    $default_scan_failed_handler = \&handle_scan_failure;

    $env_name_provider = sub { return($_[0]) };
    $ignore_unknown_controls = FALSE;

    # Telling the balanced expression scan functions which are the comments in LaTeX:
    $Mumie::Balanced::default_comments
      = [
	 ["(?<!\\\\)%", "\\n"]
	];

    $nonnewline_whitesp_chars =  ' \t';
    # Characters that a whitespaces but not newlines.

    $nonnewline_nonwhitesp_chars = '-+=A-Za-z0-9`\'.,;:?!@()\\[\\]\\*\\/\\|_';
    # Characters that are not whitespaces


    $class_regexp = "^[a-zA-Z][a-zA-Z-]*\$";
    # Regular expression a string must match to be accepted as a class name.

    %other_paren =
      (
       "(" => ")",
       ")" => "(",
       "{" => "}",
       "}" => "{",
       "[" => "]",
       "]" => "[",
       "<" => ">",
       ">" => "<"
      );
    # Maps opening parenthesis to the corresponding closing parenthesis.

    # -------------------------------------------------------------------------
    # Some predefined lists of token types
    # -------------------------------------------------------------------------

    @preamble_token_types =
      (
       "ign_whitesp",
       "cmd",
       "one_char_cmd",
       "comment"
      );

    @basic_token_types = 
      (
       "nonpar_whitesp",
       "plain_text",
       "xml_special_char",
       "par",
       "cmd",
       "one_char_cmd",
       "double_quote",
       "comment",
       "begin_block",
       "end_block",
       "special_char"
      );

    @data_token_types =
      (
       "nonpar_whitesp",
       "plain_text_extended",
       "par",
       "cmd",
       "one_char_cmd",
       "comment",
       "begin_block",
       "end_block",
       "special_char"
      );

    @container_token_types =
      (
       "ign_whitesp",
       "cmd",
       "one_char_cmd",
       "comment",
      );

    # -------------------------------------------------------------------------
    # The default token table
    # -------------------------------------------------------------------------

    init_default_token_table();

    # -------------------------------------------------------------------------
    # Setting the "done" flag
    # -------------------------------------------------------------------------

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

# ------------------------------------------------------------------------------------------
#1 Auxiliaries
# ------------------------------------------------------------------------------------------

sub look_ahead
  #a ($regexp)
  {
    my ($regexp) = @_;
    return(${$scan_proc->{source}} =~ m/\G(?=$regexp)/);
  }

# ------------------------------------------------------------------------------------------
#1 Writing log messages
# ------------------------------------------------------------------------------------------

sub log_parse_data
  # ()
  # Logs information about the state of the parsing process
  {
    log_message("\nParse_data:\n");
    log_data
      (
       "Pos", pos($ {$scan_proc->{source}} ),
       "Depth", scalar(@Mumie::Scanner::scan_proc_list),
       "Current env.", $scan_proc->{current_env},
       "Par enabled", $scan_proc->{par_enabled},
       "Inside par", $scan_proc->{inside_par},
       "Parsing data", $scan_proc->{parsing_data},
       "Mode", $scan_proc->{mode},
       "Match", "-->" . $scan_proc->{last_match} . "<--",
       "Token type", $scan_proc->{last_token_type},
       "Token value", "-->" . $scan_proc->{last_token} . "<--",
      );
    log_data("Cmds", $scan_proc->{cmd_table}) if ( $log_include_cmds );
    log_data("Envs", $scan_proc->{env_table}) if ( $log_include_envs );
    log_data("Tokens", $scan_proc->{token_table}) if ( $log_include_tokens );
    log_data("Allowed tokens", $scan_proc->{allowed_tokens}) if ( $log_include_allowed_tokens );
    log_data("Outp funcions", $scan_proc->{output_functions}) if ( $log_include_output_functions );
    &{$scan_proc->{additional_parse_logger}}() if ( $scan_proc->{additional_parse_logger} );
  }

sub log_cmd_or_env_begin
  #a ($prefix, $name, $alias, $arg_mode, $opt_args, $man_args, $pos_opt_args, $pos_man_args)
  {
    if ( $write_log )
      {
	my ($prefix, $name, $alias, $arg_mode, $opt_args, $man_args, $pos_opt_args, $pos_man_args)
	  = @_;
	my $pos_opt_args_str = join(', ', @{$pos_opt_args});
	my $pos_man_args_str = join(', ', @{$pos_man_args});
	
	my $opt_args_str = join('] [', @{$opt_args});
	if ( $opt_args_str )
	  {
	    $opt_args_str = "[$opt_args_str]";
	  }

	my $man_args_str = join('} {', @{$man_args});
	if ( $man_args_str )
	  {
	    $man_args_str = "{$man_args_str}";
	  }

	my $depth = scalar(@Mumie::Scanner::scan_proc_list);

	log_message("\n$prefix\n");
	log_data
	  (
	   'Name', $name,
	   'Alias', $alias,
	   'Arg mode', $arg_mode,
	   'Pos opt args', $pos_opt_args_str,
	   'Pos man args', $pos_man_args_str,
	   'Opt args', $opt_args_str,
	   'Man args', $man_args_str,
	   'Depth', $depth
	   );
      }
  }

# ------------------------------------------------------------------------------------------
#1 Writing warnings
# ------------------------------------------------------------------------------------------

sub notify_warning
  #a (@message_list)
  {
    my $message = join("", @_);
    my $line_number
      = get_line_number_at(pos(${$scan_proc->{source}}), ${$scan_proc->{source}});
    my ($before, $after)
      = get_text_at(pos(${$scan_proc->{source}}), ${$scan_proc->{source}});
    my $column_number = length($before);
    my $text = "Line $line_number, column $column_number: $message";
    $text = $warning_message_headline . $text if ( $warning_message_headline );
    $text =~ s/^/$warning_message_margin_left/mg if ( $warning_message_margin_left );
    log_message("\n$text\n");
    print(STDERR "$text\n") if ( $print_warnings );
  }

# ------------------------------------------------------------------------------------------
# Handling scan failure
# ------------------------------------------------------------------------------------------

sub handle_scan_failure
  {
    if ( $ignore_unknown_controls )
      {
	test_regexp('.');
	&{$scan_proc->{warning_handler}}
	  ("Can't identify token: ", $scan_proc->{last_token});
      }
    else
      {
	&{$scan_proc->{error_handler}}("Can't identify token.");
      }
  }

# ------------------------------------------------------------------------------------------
#1 Processing command and environment calls
# ------------------------------------------------------------------------------------------

sub process_cmd
  #a ($cmd_name, $arg_mode)
  {
    log_message("\nprocess_cmd 1/2\n");

    my ($cmd_name, $arg_mode) = @_;
    $arg_mode ||= 'NORMAL';
    my $alias = FALSE;

    # Checking if command exists in the command table:
    if ( ! $scan_proc->{cmd_table}->{$cmd_name} )
      {
	if ( $ignore_unknown_controls )
	  {
	    &{$scan_proc->{warning_handler}}
	      ("Command unknown or not allowed here: \\$cmd_name");
	    log_message("\nprocess_cmd 2/2: Terminating\n");
	    return();
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}
	      ("Command unknown or not allowed here: \\$cmd_name");
	  }
      }

    # Resolving alias (if $cmd_name is an alias):
    if ( $scan_proc->{cmd_table}->{$cmd_name}->{is_alias_for} )
      {
	$alias = $cmd_name;
	$cmd_name = $scan_proc->{cmd_table}->{$cmd_name}->{is_alias_for};
      }

    # Running pre_cmd_hook:
    run_hook($scan_proc->{pre_cmd_hook}, $cmd_name);

    # Checking if command is disabled:
    if ( $scan_proc->{cmd_table}->{$cmd_name}->{disabled} )
      {
	&{$scan_proc->{error_handler}}("Command not allowed here: $cmd_name");
      }

    # Initializing argument auxiliaries:
    clear_opt_args();
    clear_man_args();

    # Retrieving number of arguments:
    my $num_opt_args = get_value($scan_proc->{cmd_table}->{$cmd_name}->{num_opt_args});
    my $num_man_args = get_value($scan_proc->{cmd_table}->{$cmd_name}->{num_man_args});

    # Retrieving preferred argument, if any:
    if ( $scan_proc->{cmd_table}->{$cmd_name}->{pref_man_arg} )
      {
	get_man_args(1, $arg_mode);
	$num_man_args--;
      }

    # Retrieving optional arguments:
    get_opt_args($num_opt_args);
    my $opt_args = $scan_proc->{opt_args};
    my $pos_opt_args = $scan_proc->{pos_opt_args};
	
    # Retrieving mandatory arguments:
    get_man_args($num_man_args, $arg_mode);
    my $man_args = $scan_proc->{man_args};
    my $pos_man_args = $scan_proc->{pos_man_args};
	
    # Resetting argument auxiliaries:
    clear_opt_args();
    clear_man_args();

    # Logging:
    log_cmd_or_env_begin('process_cmd 2/2', $cmd_name, $alias, $arg_mode, $opt_args, $man_args,
			 $pos_opt_args, $pos_man_args);

    # Running pre_exec_cmd_hook:
    run_hook($scan_proc->{pre_exec_cmd_hook}, $cmd_name,
	     $opt_args, $man_args, $pos_opt_args, $pos_man_args);
	
    # Running the command's pre_hook:
    run_hook($scan_proc->{cmd_table}->{$cmd_name}->{pre_hook},
	     $opt_args, $man_args, $pos_opt_args, $pos_man_args);

    # Storing the command's post_hook in a local variable (for the case the command
    # removes itself from the command table):
    my $post_hook = $scan_proc->{cmd_table}->{$cmd_name}->{post_hook};
	

    # Running the command's execute function:
    &{$scan_proc->{cmd_table}->{$cmd_name}->{execute_function}}
      ($opt_args, $man_args, $pos_opt_args, $pos_man_args);

    # Running the command's post_hook:
    run_hook($post_hook, $opt_args, $man_args, $pos_opt_args, $pos_man_args);

    # Running the post_exec_cmd_hook:
    run_hook($scan_proc->{post_exec_cmd_hook}, $cmd_name,
	     $opt_args, $man_args, $pos_opt_args, $pos_man_args);
  }

sub process_env_begin
  #a ()
  {
    my $env_name = $_[0];
    my $alias = FALSE;
    my $env_name_code;
    my $pos_env_name_code;

    log_message("\nprocess_env_begin 1/2\n");
    log_data('Name arg', $env_name);

    # Running the pre_process_env_begin_hook:
    run_hook($scan_proc->{pre_process_env_begin_hook});

    # Getting the environment name if necessary
    unless ( $env_name )
      {
	clear_man_args();
	get_man_args(1, 'NORMAL');
	$env_name_code = $scan_proc->{man_args}->[0];
	$pos_env_name_code = $scan_proc->{pos_man_args}->[0];
	$env_name = &{$env_name_provider}($env_name_code, $pos_env_name_code);
	clear_man_args();
      }

    # Checking if the environment exists in the environment table:
    if ( ! $scan_proc->{env_table}->{$env_name} )
      {
	if ( $ignore_unknown_controls )
	  {
	    &{$scan_proc->{warning_handler}}
	      ("Environment unknown or not allowed here: $env_name");
	    log_message("\nprocess_env_begin 2/2: Terminating\n");
	    return();
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}
	      ("Environment unknown or not allowed here: $env_name");
	  }
      }

    # Resolving alias (if $cmd_name is an alias):
    if ( $scan_proc->{env_table}->{$env_name}->{is_alias_for} )
      {
	$alias = $env_name;
	$env_name = $scan_proc->{env_table}->{$env_name}->{is_alias_for};
      }

    # Running the pre_env_begin_hook:
    run_hook($scan_proc->{pre_env_begin_hook}, $env_name);

    # Running the environment's early_pre_begin_hook:
    run_hook($scan_proc->{env_table}->{$env_name}->{early_pre_begin_hook});

    # Checking if the environment is disabled:
    if ( $scan_proc->{env_table}->{$env_name}->{begin_disabled} )
      {
	&{$scan_proc->{error_handler}}("Environment begin not allowed here: $env_name");
      }

    # Initializing argument auxiliaries:
    clear_opt_args();
    clear_man_args();

    # Retrieving number of arguments:
    my $num_opt_args = get_value($scan_proc->{env_table}->{$env_name}->{num_opt_args});
    my $num_man_args = get_value($scan_proc->{env_table}->{$env_name}->{num_man_args});

    # Retrieving optional arguments:
    get_opt_args($num_opt_args);
    my $opt_args = $scan_proc->{opt_args};
    my $pos_opt_args = $scan_proc->{pos_opt_args};

    # Retrieving mandatory arguments:
    get_man_args($num_man_args, 'NORMAL');
    my $man_args = $scan_proc->{man_args};
    my $pos_man_args = $scan_proc->{pos_man_args};

    # Resetting argument auxiliaries:
    clear_opt_args();
    clear_man_args();

    # Logging:
    log_cmd_or_env_begin('process_env_begin 2/2', $env_name, $alias, 'NORMAL', $opt_args,
			 $man_args, $pos_opt_args, $pos_man_args);

    # Starting and setting up new scan process:
    new_scan_proc('COPY', 'OWN_NAMESPACE');
    $scan_proc->{current_env} = $env_name;

    # Running the pre_exec_env_begin_hook:
    run_hook($scan_proc->{pre_exec_env_begin_hook}, $env_name,
	     $opt_args, $man_args, $pos_opt_args, $pos_man_args);

    # Running the environment's pre_begin_hook:
    run_hook($scan_proc->{env_table}->{$env_name}->{pre_begin_hook},
	     $opt_args, $man_args, $pos_opt_args, $pos_man_args);

    # Storing the environment's post_begin_hook in a local variable (for the case the
    # environment removes itself from the environment table):
    my $post_begin_hook = $scan_proc->{env_table}->{$env_name}->{post_begin_hook};
	

    # Running the environment's begin function
    &{$scan_proc->{env_table}->{$env_name}->{begin_function}}
      ($opt_args, $man_args, $pos_opt_args, $pos_man_args);

    # Running the environment's post_begin_hook
    run_hook($post_begin_hook, $opt_args, $man_args, $pos_opt_args, $pos_man_args);
	
    # Running the post_exec_env_begin_hook
    run_hook($scan_proc->{post_exec_env_begin_hook}, $env_name,
	     $opt_args, $man_args, $pos_opt_args, $pos_man_args);
  }

sub process_env_end
  # ()
  {
    my $env_name = $_[0];

    log_message("\nprocess_env_end 1/4\n");

    run_hook($scan_proc->{pre_process_env_end_hook});

    unless ( $env_name )
      {
	clear_man_args();
	get_man_args(1, 'NORMAL');
	my $env_name_code = $scan_proc->{man_args}->[0];
	$env_name = &{$env_name_provider}($env_name_code);
      }

    log_message("\nprocess_env_end 2/4\n");
    log_data('Name arg',    $env_name,
	     'Current env', $scan_proc->{current_env},);


    if ( $scan_proc->{env_table}->{$env_name} )
      {
	run_hook($scan_proc->{env_table}->{$env_name}->{early_pre_end_hook});
	run_hook($scan_proc->{pre_env_end_hook}, $env_name);


	log_message("\nprocess_env_end 3/4\n");
	log_data('Name arg',    $env_name,
		 'Current env', $scan_proc->{current_env},);

	my $current_env = $scan_proc->{current_env};
	if ( $env_name eq $current_env )
	  {
	    if ( $scan_proc->{env_table}->{$env_name}->{end_disabled} )
	      {
		&{$scan_proc->{error_handler}}
		  ("Environment end not allowed here: $env_name");
	      }
	    else
	      {

		run_hook($scan_proc->{env_table}->{$env_name}->{pre_end_hook});
		run_hook($scan_proc->{pre_exec_env_end_hook}, $env_name);
		&{$scan_proc->{env_table}->{$env_name}->{end_function}}();
		run_hook($scan_proc->{post_exec_env_end_hook}, $env_name);
		run_hook($scan_proc->{env_table}->{$env_name}->{post_end_hook});
		reset_scan_proc();
		run_hook($scan_proc->{env_table}->{$env_name}->{late_post_end_hook});
	      }
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}
	      ("Improper nesting: ", appr_env_notation($current_env),
	       " must be closed before environment\n\`$env_name\' can be closed.");
	  }
      }
    else
      {
	if ( $ignore_unknown_controls )
	  {
	    # do nothing
	  }
	else
	  {
	    &{$scan_proc->{error_handler}}("Environment unknown: $env_name");
	  }
      }

    log_message("\nprocess_env_end 4/4\n");
    log_data('Current env', $scan_proc->{current_env},);


  }

sub clear_opt_args
  {
    $scan_proc->{opt_args} = [];
    $scan_proc->{pos_opt_args} = [];
  }

sub get_opt_args
  # ($req_num)
  {
    my $req_num = $_[0];
    $req_num ||= 0;

    log_message("\nget_opt_args 1/2\n");
    log_data("number", $req_num);

    my $count = 0;

    new_scan_proc("COPY", "SHARED_NAMESPACE");
    $scan_proc->{allowed_tokens} = ["opt_arg"];

    my $no_more_args = FALSE;
    $scan_proc->{scan_failed_handler} = sub { $no_more_args = TRUE };

    while ( ( $count < $req_num ) && !( $no_more_args ) )
      {
	scan_next_token();
	$count++ unless ( $no_more_args );
      }

    reset_scan_proc();

    log_message("\nget_opt_args 2/2\n");
  }

sub clear_man_args
  {
    $scan_proc->{man_args} = [];
    $scan_proc->{pos_man_args} = [];
  }

sub get_man_args
  #a ($req_number, $mode)
  {
    my ($req_num, $mode) = @_;
    $req_num ||= 0;
    $mode ||= 'NORMAL';

    log_message("\nget_man_args 1/2\n");
    log_data('number', $req_num, 'mode', $mode);

    new_scan_proc('COPY', 'SHARED_NAMESPACE');

    if ( $mode eq 'NORMAL' )
      {
	$scan_proc->{allowed_tokens} = ['man_arg'];
      }
    elsif ( $mode eq 'ALLOW_PLAIN_ARGS' )
      {
	$scan_proc->{allowed_tokens} = ['man_arg', 'plain_man_arg'];
      }
    elsif ( $mode eq 'ALLOW_UNBR_ARGS' )
      {
	$scan_proc->{allowed_tokens} = ['man_arg', 'unbr_man_arg'];
      }

    $scan_proc->{scan_failed_handler}
      = sub { &{$scan_proc->{error_handler}}('Expected mandatory command argument') };

    my $count = 0;
    while ( $count < $req_num )
      {
        scan_next_token();
        $count++;
      }

    reset_scan_proc();

    log_message("\nget_man_args 2/2\n");
  }

sub handle_opt_arg
  #a ()
  # Intended to be used as token handler for tokens that are optional arguments.
  {
    push(@{$scan_proc->{opt_args}}, $scan_proc->{last_token});
    push(@{$scan_proc->{pos_opt_args}},
	 pos(${$scan_proc->{source}}) - length($scan_proc->{last_token}) - 2);
  }

sub handle_man_arg
  #a ()
  # Intended to be used as token handler for tokens that are mandatory arguments.
  {
    push(@{$scan_proc->{man_args}}, $scan_proc->{last_token});
    push(@{$scan_proc->{pos_man_args}},
	 pos(${$scan_proc->{source}}) - 1 - length($scan_proc->{last_token}));
  }

sub appr_env_notation
  # ($env_name)
  {
    my $env_name = $_[0];
    my $notation;

    if ( $env_name =~ m/^_(.*)$/ ) # i.e., it is a pseudo environment
      {
	$notation = $1;
      }
    else # i.e., it is a genuine environment
      {
	$notation = "environment \`$env_name\'";
      }
  }

# ------------------------------------------------------------------------------------------
#1 Dealing with paragraphs and blocks
# ------------------------------------------------------------------------------------------

sub start_par
  # ()
  # Starts a new paragraph. In particular, creates a new scan process, configures it for
  # the paragraph, and calls the "begin_par" output function. In addition, runs some hooks,
  # i.e.,
  #DL
  #  $scan_proc->{early_pre_start_par_hook} &
  #    At the very beginning (but after the first log message), especially before the new
  #    scan process is created. \\
  #  $scan_proc->{pre_start_par_hook} &
  #    Immediately before the output function is called,  especially after the new scan
  #    process has been created. \\
  #  $scan_proc->{post_start_par_hook} &
  #    As the very last.
  #\DL
  {
    # Logging:
    log_message("\nstart_par\n");

    # Running the early_pre_start_par_hook:
    run_hook($scan_proc->{early_pre_start_par_hook});

    # Creating and setting up a new scan process:
    new_scan_proc('COPY', 'SHARED_NAMESPACE');
    $scan_proc->{current_env} = '_paragraph';
    $scan_proc->{inside_par} = TRUE;

    # Running the pre_start_par_hook:
    run_hook($scan_proc->{pre_start_par_hook});

    # Calling the begin_par output function:
    &{$scan_proc->{output_functions}->{begin_par}}();

    # Running the post_start_par_hook:
    run_hook($scan_proc->{post_start_par_hook});
  }

sub close_par
  # ()
  # Closes a paragraph. In particular, checks for correct nesting, calls the "end_par"
  # output function and resets the scan process. In addition, runs some hooks,
  # i.e.,
  #DL
  #  $scan_proc->{early_pre_close_par_hook} &
  #    At the very beginning (but after the first log message), especially before the
  #    nesting cjeck. \\
  #  $scan_proc->{pre_close_par_hook} &
  #    Immediately before the output function is called, especially after the nesting
  #    check. \\
  #  $scan_proc->{post_close_par_hook} &
  #    At the very last, especially after the scan process has been reset.
  #\DL
  {
    # Logging:
    log_message("\nclose_par\n");

    # Running the early_pre_close_par_hook:
    run_hook($scan_proc->{early_pre_close_par_hook});

    # Checking for correct nesting:
    if ( $scan_proc->{current_env} ne '_paragraph' )
      {
	&{$scan_proc->{error_handler}}
	  ('Improper nesting: ', appr_env_notation($scan_proc->{current_env}),
	   ' must be closed within paragraph.');
      }

    # Running the pre_close_par_hook:
    run_hook($scan_proc->{pre_close_par_hook});

    # Calling the end_par output function:
    &{$scan_proc->{output_functions}->{end_par}}();

    # Resetting the scan process:
    reset_scan_proc();

    # Running the post_close_par_hook:
    run_hook($scan_proc->{post_close_par_hook});
  }

sub start_par_if_necessary
  #a ()
  # Starts a paragraph unless one of the following conditions hold true:
  #L
  #  1. & Paragraphs are disabled, i.e., $scan_proc->{par_enabled} is false. \\
  #  2. & Data are parsed, i.e., $scan_proc->{parsing_data} is true. \\
  #  3. & We are already in a paragraph, i.e., $scan_proc->{inside_par} is true.
  #/L
  {
    log_message("\nstart_par_if_necessary 1/2\n");

    if ( ( $scan_proc->{par_enabled} )
	 && ( ! $scan_proc->{parsing_data} )
	 && ( ! $scan_proc->{inside_par} ) )
      {
	start_par();
      }

    log_message("\nstart_par_if_necessary 2/2\n");
  }

sub close_par_if_necessary
  #a ()
  # Closes a paragraph unless one of the following conditions hold true:
  #L
  #  1. & Paragraphs are disabled, i.e., $scan_proc->{par_enabled} is false. \\
  #  2. & Data are parsed, i.e., $scan_proc->{parsing_data} is true. \\
  #  3. & We are not in a paragraph, i.e., $scan_proc->{inside_par} is false.
  #/L
  # Note that this function does not check if the current environment is "_paragraph". It
  # assumes that this is fullfilled if none of the above conditons apply. If not, an error
  # is signaled. This is ok because the error is probably caused by an environment the user
  # has opened but not closed in the paragraph.
  #
  # Use `close_par_if_in_par` if you need a function that checks if current environment is
  # "_paragraph" before attempting to close a paragraph.
  {
    log_message("\nclose_par_if_necessary 1/2\n");

    if ( ( $scan_proc->{par_enabled} )
	 && ( ! $scan_proc->{parsing_data} )
	 && ( $scan_proc->{inside_par} ) )
      {
	close_par();
      }

    log_message("\nclose_par_if_necessary 2/2\n");
  }

sub close_par_if_in_par
  #a ()
  # Closes the current paragraph, if any. Checks whether current environment is "_paragraph";
  # if so, calls `close_par_if_necessary`.
  {
    close_par_if_necessary() if ( $scan_proc->{current_env} eq '_paragraph' );
  }

sub check_par_before_token
  {
    log_message("\ncheck_par_before_token 1/2\n");
    my $token_type = ( $_[0] || $scan_proc->{last_token_type} );
    if ( $scan_proc->{token_table}->{$token_type}->{is_par_closer} )
      {
	close_par_if_necessary();
      }
    if ( $scan_proc->{token_table}->{$token_type}->{is_par_starter} )
      {
	start_par_if_necessary();
      }
    log_message("\ncheck_par_before_token 2/2\n");
  }

sub check_par_before_cmd
  {
    log_message("\ncheck_par_before_cmd 1/2\n");
    my $cmd_name = $_[0];
    if ( $scan_proc->{cmd_table}->{$cmd_name}->{is_par_closer} )
      {
	close_par_if_necessary();
      }
    if ( $scan_proc->{cmd_table}->{$cmd_name}->{is_par_starter} )
      {
	start_par_if_necessary();
      }
    log_message("\ncheck_par_before_cmd 2/2\n");
  }

sub check_par_before_env
  {
    log_message("\ncheck_par_before_env 1/2\n");
    my $env_name = $_[0];
    if ( $scan_proc->{env_table}->{$env_name}->{is_par_closer} )
      {
	close_par_if_necessary();
      }
    if ( $scan_proc->{env_table}->{$env_name}->{is_par_starter} )
      {
	start_par_if_necessary();
      }
    log_message("\ncheck_par_before_env 2/2\n");
  }

sub start_block
  {
    log_message("\nstart_block\n");
    run_hook($scan_proc->{pre_block_begin_hook});
    new_scan_proc("COPY", "OWN_NAMESPACE");
    allow_tokens(['end_block'], 'PROTECT', 'PREPEND');
    $scan_proc->{current_env} = "_block";
    run_hook($scan_proc->{post_block_begin_hook});
  }


sub close_block
  {
    log_message("\nclose_block\n");
    run_hook($scan_proc->{pre_block_end_hook});
    if ( $scan_proc->{current_env} ne "_block" )
      {
	&{$scan_proc->{error_handler}}
	  ("Improper nesting: ", appr_env_notation($scan_proc->{current_env}),
	   " must be closed within current {...}-block.");
      }
    else
      {
	reset_scan_proc();
	run_hook($scan_proc->{post_block_end_hook});
      }
  }

sub between_pars
  # ()
  {
    return( ( $scan_proc->{par_enabled} ) && (! $scan_proc->{inside_par} ) );
  }

# ------------------------------------------------------------------------------------------
#1 Default scan result handler
# ------------------------------------------------------------------------------------------

sub call_token_handler
  #a ()
  # Calls the handler of the last token. Before and after that, the hooks
  # $scan_proc->{pre_token_handler} resp. $scan_proc->{post_token_handler} are run.
  {
    my $token_type = $scan_proc->{last_token_type};

    # Logging:
    log_message("\ncall_token_handler 1/2\n");
    log_parse_data() if ( $write_log );

    # Running the pre_token_handler hook:
    run_hook($scan_proc->{pre_token_handler});

    # Running the token's pre_handler_hook:
    run_hook($scan_proc->{token_table}->{$token_type}->{pre_handler_hook});

    # Calling the token's handler:
    &{$scan_proc->{token_table}->{$token_type}->{handler}}();

    # Running the token's post_handler_hook:
    run_hook($scan_proc->{token_table}->{$token_type}->{post_handler_hook});

    # Running the post_token_handler hook:
    run_hook($scan_proc->{post_token_handler});

    # Logging:
    log_message("\ncall_token_handler 2/2\n");
  }

# ------------------------------------------------------------------------------------------
#1 Dealing with the command and environment tables
# ------------------------------------------------------------------------------------------

sub own_cmd_table
  # [$option]
  # Makes a copy of the target of $scan_proc->{cmd_table}, and lets
  # $scan_proc->{cmd_table} point to it. If $option is "DEREFERENCE" (the default),
  # references in the copy are turned into references to copys recursively (see
  # function 'copy_hash'). If $option is "NO_DEREFERENCE", the references are
  # left unchanged, so that both the copy and the original point to the same things.
  {
    my $option = ( $_[0] || "DEREFERENCE" );

    log_message("\nown_cmd_table\n",
		"  Option         :  $option\n");

    if ( $option eq "DEREFERENCE" )
      {
	$scan_proc->{cmd_table} = copy_hash($scan_proc->{cmd_table});
      }
    elsif ( $option eq "NO_DEREFERENCE" )
      {
	my %copy = %{$scan_proc->{cmd_table}};
	$scan_proc->{cmd_table} = \%copy;
      }
  }

sub own_env_table
  # [$option]
  # Makes a copy of the target of $scan_proc->{env_table}, and lets
  # $scan_proc->{env_table} point to it. If $option is "DEREFERENCE" (the default),
  # references in the copy are turned into references to copys recursively (see
  # function 'copy_hash'). If $option is "NO_DEREFERENCE", the references are
  # left unchanged, so that both the copy and the original point to the same things.
  {
    my $option = ( $_[0] || "DEREFERENCE" );

    log_message("\nown_env_table\n",
		"  Option         :  $option\n");

    if ( $option eq "DEREFERENCE" )
      {
	$scan_proc->{env_table} = copy_hash($scan_proc->{env_table});
      }
    elsif ( $option eq "NO_DEREFERENCE" )
      {
	my %copy = %{$scan_proc->{env_table}};
	$scan_proc->{env_table} = \%copy;
      }
  }

sub own_allowed_tokens
  # ()
  # Makes a copy of the target of $scan_proc->{allowed_tokens}, and lets
  # $scan_proc->{allowed_tokens} point to it.
  {
    log_message("\nown_allowed_tokens\n");
    my @copy = @{$scan_proc->{allowed_tokens}};
    $scan_proc->{allowed_tokens} = \@copy;
  }

sub allow_tokens
  # ($token_types_ref, [$modify_mode, [$add_mode]])
  # Ensures that all tokens in @{$token_types_ref} are allowed. If all these tokens are
  # already included in @{$scan_proc->{allowed_tokens}}, does nothing. Otherwise adds the
  # lacking tokens to @{$scan_proc->{allowed_tokens}}. $modify_mode may be "MODIFY" (the
  # default) or "PROTECT". In the latter case, 'own_allowed_tokens()' is called before
  # adding the tokens. $add_mode may be "APPEND" (the default) or "PREPEND". In the first
  # case, the lacking tokens are added at the end of @{$scan_proc->{allowed_tokens}},
  # otherwise at the beginning. Note that the position of a token mentioned in
  # @{$token_types_ref} that is already included in @{$scan_proc->{allowed_tokens}} is not
  # altered by this function.
  {
    my @token_types = @{$_[0]};
    my $modify_mode = ( $_[1] || 'MODIFY' );
    my $add_mode = ( $_[2] || 'APPEND' );

    log_message("\nallow_tokens 1/2\n");
    log_data('Modify mode', \$modify_mode,
	     'Add mode', \$add_mode,
	     'Token types', \@token_types);

    my @token_types_to_add = ();
    foreach my $token_type (@token_types)
      {
	unless ( grep($_ eq $token_type, @{$scan_proc->{allowed_tokens}}) )
	  {
	    push(@token_types_to_add, $token_type);
	  }
      }

    if ( @token_types_to_add )
      {
	if ( $modify_mode eq 'PROTECT' )
	  {
	    own_allowed_tokens();
	  }

	if ( $add_mode eq 'APPEND' )
	  {
	    push(@{$scan_proc->{allowed_tokens}}, @token_types_to_add);
	  }
	else # i.e., ( $modify_mode eq 'PREPEND' )
	  {
            unshift(@{$scan_proc->{allowed_tokens}}, @token_types_to_add);
# 	    $scan_proc->{allowed_tokens}
# 	      = [@token_types_to_add, @{$scan_proc->{allowed_tokens}}];
	  }
      }

    log_message("\nallow_tokens 2/2\n");
  }

sub disallow_tokens
  #a ($token_types_ref, [$modify_mode])
  # Disallows all tokens in @{$token_types_ref}, i.e., removes them from
  # @{$scan_proc->{allowed_tokens}}. $modify_mode may be "MODIFY" (the default) or
  # "PROTECT". In the latter case, `own_allowed_tokens` is called before removing the
  # tokens.
  {
    my @token_types = @{$_[0]};
    my $modify_mode = ( $_[1] || "MODIFY" );

    log_message("\ndisallow_tokens 1/2\n");
    log_data("Modify mode", $modify_mode,
	     "Token types", \@token_types);

    my $contained = FALSE;
    for (my $i = 0; ( $i < scalar(@token_types) ) && ( ! $contained ); $i++)
      {
	if ( grep($_ eq $token_types[$i], @{$scan_proc->{allowed_tokens}}) )
	  {
	    $contained = TRUE;
	  }
      }

    if ( ( $modify_mode eq "PROTECT" ) && ( $contained ) )
      {
	own_allowed_tokens();
      }

    foreach my $token_type (@token_types)
      {
	rm_string_from_array($token_type, $scan_proc->{allowed_tokens});
      }

    log_message("\ndisallow_tokens 2/2\n");
  }

sub get_vital_tokens
  {
    log_message("\nget_vital_tokens 1/2\n");
    my @vital_tokens =
      grep({$scan_proc->{token_table}->{$_}->{vital}} keys(%{$scan_proc->{token_table}}));
    log_message("\nget_vital_tokens 2/2\n");
    log_data("Vital tokens", \@vital_tokens);
    return @vital_tokens;
  }

sub remove_cmds
  # ($forbidden_cmd_list)
  {
    my $forbidden_cmd_list = $_[0];
    my $forbidden_cmd;
    foreach $forbidden_cmd (@{$forbidden_cmd_list})
      {
	delete($scan_proc->{cmd_table}->{$forbidden_cmd});
      }
  }

sub remove_envs
  # ($forbidden_env_list)
  {
    my $forbidden_env_list = $_[0];
    my $frobidden_env;
    foreach $frobidden_env (@{$forbidden_env_list})
      {
	delete($scan_proc->{env_table}->{$frobidden_env});
      }
  }

# ------------------------------------------------------------------------------------------
#1 Creating and resetting scan processes
# ------------------------------------------------------------------------------------------

sub new_scan_proc
  # ($how_to_init)
  {
    my $how_to_init = ( shift() or 'DEFAULT' );

    if ( $write_log )
      {
	my $current_depth = get_scan_proc_depth();
	my $next_depth = $current_depth + 1;
	log_message("\nnew_scan_proc (Mumie::MmTeX::Parser)\n");
	log_data('Init type', $how_to_init,
		 'Depth', "$current_depth->$next_depth");
      }

    if ( $how_to_init eq 'DEFAULT' )
      {
	Mumie::Scanner::new_scan_proc('DEFAULT');

	$scan_proc->{token_table} = $default_token_table;
	$scan_proc->{scan_result_handler} = \&call_token_handler;
	$scan_proc->{warning_handler} = $default_warning_handler;

	$scan_proc->{cmd_table} = {};
	$scan_proc->{env_table} = {};
	$scan_proc->{current_env} = '';
	$scan_proc->{output_functions} = {};
	$scan_proc->{inside_par} = FALSE;
	$scan_proc->{par_enabled} = FALSE;
	$scan_proc->{parsing_data} = FALSE;
	$scan_proc->{mode} = '';
	$scan_proc->{pre_token_handler} = \&check_par_before_token;
	$scan_proc->{pre_cmd_hook} = \&check_par_before_cmd;
	$scan_proc->{pre_env_begin_hook} = \&check_par_before_env;
#	$scan_proc->{log_function} = \&log_parse_data;
      }
    elsif ( $how_to_init eq 'COPY' )
      {
	Mumie::Scanner::new_scan_proc('COPY');

	my $namespace_opt = shift();
	if ( $namespace_opt eq 'OWN_NAMESPACE' )
	  {
	    own_cmd_table();
	    own_env_table();
	  }
      }
    elsif ( $how_to_init eq 'EMPTY' )
      {
	# nothing to do in this case (Tilman)
	# Shoudn't it be
	#
        # Mumie::Scanner::new_scan_proc('EMPTY');
        #
	# ? (Tilman)
      }
  }

sub reset_scan_proc
  {
    if ( $write_log )
      {
	my $current_depth = get_scan_proc_depth();
	my $next_depth = $current_depth - 1;
	log_message("\nreset_scan_proc (Mumie::MmTeX::Parser)\n");
	log_data("Depth", "$current_depth->$next_depth");
      }

    Mumie::Scanner::reset_scan_proc();
  }

init();
return(1);
