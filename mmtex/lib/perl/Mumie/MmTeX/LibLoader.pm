package Mumie::MmTeX::LibLoader;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: LibLoader.pm,v 1.10 2007/07/11 15:56:15 grudzin Exp $

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
# Loads mmtex libraries, and manages loaded libraries.
#
# ------------------------------------------------------------------------------------------
#1 Library table
# ------------------------------------------------------------------------------------------
#
# To handle loaded libraries, a so-called *library table* is installed. This is a
# hash mapping library names to the corresponding library data. Each value is again a
# (reference to a) hash of the following form:
#H
#  cmd_table   & The commands this library can export (in command table
#                format). \\
#  env_table   & The environments this library can export (in environment
#                table format). \\
#  cmd_tags    & Reference to a hash specifying the command tags of this library. The
#                keys are the tags. The value corresponding to the a key "foo" is a
#                (references to a) list of command names. \\
#  env_tags    & Reference to a hash specifying the environment tags of this library.
#                keys are the tags. The value corresponding to the a key "foo" is a
#                (references to a) list of environment names. \\
#  initializer & Reference to a function called to initialize the library. \\
#  activator   & Reference to a function; should be called by the document class
#                to "activate" the library. An appropriate place to do that may be at
#                the beginning of the "document" environment. \\
#  deactivator & Reference to a function; should be called by the document class
#                to "deactivate" the library. An appropriate place to do that may be at
#                the end of the "document" environment. \\
#/H
# There is always a single, global, library table, a reference of which is stored in the
# variable $lib_table


use Mumie::Boolconst;
use Mumie::Logger qw(/.+/);
use Mumie::File qw(/.+/);
use Mumie::List qw(/.+/);
use Mumie::Hooks qw(/.+/);
use Mumie::Scanner qw($scan_proc);

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL = qw
    (
     $autoload_libs
     $inc_path
     $lib_table
     activate_libs
     deploy_lib
     disable_cmds
     disable_cmds_from_all_libs
     disable_envs
     disable_envs_from_all_libs
     find_all_libs
     get_cmds_for_tag
     init_lib
     install_cmds
     install_cmds_from_all_libs
     install_envs
     install_envs_from_all_libs
     require_lib
    );
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
    );
}

sub init
  {
    $inc_path = FALSE;
    # Colon-separated list of directories in which libraries are searched.

    $lib_options_buffer = "";
    # Transient global copy of a library options string. Used to pass options to a library.

    $autoload_libs = FALSE;
    # Whether libraries are loaded automatically when needed.

    # The library table (see main documentation).
    if ( $lib_table )
      {
	# Cleanup lib_table, just keep the initializers.
	my $_lib_table = {};
	foreach $lib_name (keys %{$lib_table})
	  {
	    $_lib_table->{$lib_name}->{initializer} = $lib_table->{$lib_name}->{initializer};
	  }
	$lib_table = $_lib_table;
	undef($_lib_table);
      }
    else
      {
	$lib_table = {};
      }

    # Setting "init done" flag
    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub notify_error
  {
    die('Error: LibLoader: ', @_, "\n");
  }

sub deploy_lib
  #a ($name, $cmd_table, $env_table, $cmd_tags_spec, $env_tags_spec)
  # Deploys a library in the system. This means that the necessary entries in
  # $lib_table are made. The arguments have the following meaning:
  #H
  #  $name       & Name of the library. \\
  #  $cmd_table  & (Reference to the) command table of the library. \\
  #  $env_table  & (Reference to the) environment table of the library. \\
  #  $cmd_tags_spec
  #              & The command tags of the library. This may be a string
  #                or a reference to a hash. In the first case, the string
  #                is set as a tag for all commands of this library. In the
  #                second case, the hash must map tags to (references to)
  #                lists of commands. \\
  #  $env_tags_spec
  #              & The environment tags of the library. See $cmd_tags_spec
  #                for the format of the value.
  #/H
  # Except $name, all arguments are optional and default to (references to)
  # empty hashes.
  {
    my ($name, $cmd_table, $env_table, $cmd_tags_spec, $env_tags_spec) = @_;
    $cmd_table ||= {};
    $env_table ||= {};
    my ($cmd_tags, $env_tags) = ({}, {});

    if ( $cmd_tags_spec )
      {
	if ( ref($cmd_tags_spec) )
	  {
	    $cmd_tags = $cmd_tags_spec;
	  }
	else
	  {
	    my @all_cmds = keys(%{$cmd_table});
	    $cmd_tags = {$cmd_tags_spec => \@all_cmds};
	  }
      }

    if ( $env_tags_spec )
      {
	if ( ref($env_tags_spec) )
	  {
	    $env_tags = $env_tags_spec;
	  }
	else
	  {
	    my @all_envs = keys(%{$env_table});
	    $env_tags = {$env_tags_spec => \@all_envs};
	  }
      }

    log_message("\ndeploy_lib\n");
    log_data('Name', $name, 'Cmd table', $cmd_table, 'Env table', $env_table,
	     'Cmd tags', $cmd_tags_spec, 'Env tags', $env_tags_spec);

    $lib_table->{$name}->{cmd_table} = ($cmd_table ? $cmd_table : {});
    $lib_table->{$name}->{env_table} = ($env_table ? $env_table : {});
    $lib_table->{$name}->{cmd_tags} = ($cmd_tags ? $cmd_tags : {});
    $lib_table->{$name}->{env_tags} = ($env_tags ? $env_tags : {});
  }

sub init_lib
  #a ($name, $error_handler)
  # Initializes library $name by calling its initializer, i.e., $lib_table->{$name}->{initializer}.
  # The latter is called with one argument, $error_handler, which is the error handler the
  # initializer should use.
  {
    my $name = $_[0];
    log_message("\ninit_lib 1/2\n");
    my $initialized = $lib_table->{$name}->{initialized};
    log_data('Lib', $name, 'Initialized', $initialized);
    unless ( $initialized )
      {
        notify_error("No initializer for library: $name") unless ( $lib_table->{$name}->{initializer} );
        run_hook($lib_table->{$name}->{initializer}, $error_handler);
        $lib_table->{$name}->{initialized} = TRUE;
      }
    log_message("\ninit_lib 2/2\n");
  }

sub activate_libs
  {
    my @libs = ( @_ || keys(%{$lib_table}) );
    log_message("\nactivate_libs 1/2\n");
    log_data('Libs', join(' ', @libs));
    foreach my $name (@libs)
      {
	&{$scan_proc->{error_handler}}("Library not loaded: $name") unless ( $lib_table->{$name} );
	run_hook($lib_table->{$name}->{activator});
      }
  }

sub install_cmds
  #a ($cmd_names_or_tag, $lib_name, $mode, $state, $cmd_table)
  # Imports commands specified by $cmd_names_or_tag from the library $lib_name into the
  # command table $cmd_table. The latter defaults to the current command table, i.e.,
  # $scan_proc->{cmd_table}. $cmd_names_or_tag may be a reference or a string. In the first
  # case, it is assumed that $cmd_names_or_tag points to a list of command names. These are
  # taken as the commands to import. In the latter case, i.e., if $cmd_names_or_tag is a
  # string, it is assumed that this string is a tag defined by the library $lib_name, and
  # the names corresponding to this tag are taken as the commands to import. $mode may be
  # "OVERWRITE" (the default) or "PROTECT". In the latter case, commands are imported only
  # if they are not already included in $cmd_table. Otherwise, they are imported
  # uncondicionally. $state may be "DISABLED", "ACTIVE", or "INHERIT" (the default). In the
  # first two cases, the imported commands will be disabled resp. activated after import by
  # setting the "disabled" flag correspondingly. In the latter case, the "disabled" flag
  # will be left unchanged.
  {
    log_message("\ninstall_cmds 1/3\n");

    my ($cmd_names_or_tag, $lib_name, $mode, $state, $cmd_table) = @_;
    $mode ||= "OVERWRITE";
    $state ||= "INHERIT";
    $cmd_table ||= $scan_proc->{cmd_table};

    if ( ! $lib_table->{$lib_name} )
      {
	if ( $autoload_libs )
	  {
	    require_lib($lib_name);
	  }
	else
	  {
            notify_error("install_cmds: Library not loaded: $lib_name");
	  }
      }

    my $lib_cmd_table = $lib_table->{$lib_name}->{cmd_table};
    my $cmd_names;
    my $tag = FALSE;
    if ( ref($cmd_names_or_tag) )
      {
	$cmd_names = $cmd_names_or_tag;
      }
    else
      {
	$tag = $cmd_names_or_tag;
	if ( $tag eq "ALL" )
	  {
	    @{$cmd_names} = keys(%{$lib_cmd_table});
	  }
	elsif ( $lib_table->{$lib_name}->{cmd_tags}->{$tag} )
	  {
	    $cmd_names = $lib_table->{$lib_name}->{cmd_tags}->{$tag}
	  }
	else
	  {
            notify_error("install_cmds: Library \"$lib_name\" does not define tag: $tag");
	  }
      }

    log_message("\ninstall_cmds 2/3\n");
    log_data("Commands", join(" ", @{$cmd_names}),
	     "Tag", ( $tag || "-" ),
	     "Lib", $lib_name,
	     "Mode", $mode,
	     "State", $state);

    foreach my $cmd_name (@{$cmd_names})
      {
	if ( ! $lib_cmd_table->{$cmd_name} )
	  {
            notify_error("install_cmds: Library \"$lib_name\" does not export command: $cmd_name");
	  }
	unless ( ( $cmd_table->{$cmd_name} ) && ( $mode ne "OVERWRITE" ) )
	  {
	    $cmd_table->{$cmd_name} = copy_hash($lib_cmd_table->{$cmd_name});
	  }
	if ( $state eq "DISABLED" )
	  {
	    $cmd_table->{$cmd_name}->{disabled} = TRUE;
	  }
	elsif ( $state eq "ACTIVE" )
	  {
	    $cmd_table->{$cmd_name}->{disabled} = FALSE;
	  }
      }

    log_message("\ninstall_cmds 3/3\n");
  }

sub disable_cmds
  #a ($cmd_names_or_tag, $lib_name, $cmd_table)
  #  Simply disables a command
  {
    log_message("\ndisable_cmds 1/3\n");

    my ($cmd_names_or_tag, $lib_name, $cmd_table) = @_;
    $cmd_table ||= $scan_proc->{cmd_table};

    if ( ! $lib_table->{$lib_name} )
      {
	notify_error("install_cmds: Library not loaded: $lib_name");
      }

    my $lib_cmd_table = $lib_table->{$lib_name}->{cmd_table};
    my $cmd_names;
    my $tag = FALSE;
    if ( ref($cmd_names_or_tag) )
      {
	$cmd_names = $cmd_names_or_tag;
      }
    else
      {
	$tag = $cmd_names_or_tag;
	if ( $tag eq "ALL" )
	  {
	    @{$cmd_names} = keys(%{$lib_cmd_table});
	  }
	elsif ( $lib_table->{$lib_name}->{cmd_tags}->{$tag} )
	  {
	    $cmd_names = $lib_table->{$lib_name}->{cmd_tags}->{$tag}
	  }
	else
	  {
            notify_error("install_cmds: Library \"$lib_name\" does not define tag: $tag");
	  }
      }

    log_message("\ndisable_cmds 2/3\n");
    log_data("Commands", join(" ", @{$cmd_names}),
	     "Tag", ( $tag || "-" ),
	     "Lib", $lib_name,
	     "State", "SET TO DISABLED");

    foreach my $cmd_name (@{$cmd_names})
      {
	if ( ! $lib_cmd_table->{$cmd_name} )
	  {
            notify_error("disable_cmds: Library \"$lib_name\" does not export command: $cmd_name");
	  }
	$cmd_table->{$cmd_name}->{disabled} = TRUE;
      }

    log_message("\ndisable_cmds 3/3\n");
  }

sub install_envs
  # ($env_names_or_tag, $lib_name, [$mode, [$state_begin, [$state_end, [$env_table]]]])
  # Imports commands specified by $env_names_or_tag from the library $lib_name into the
  # command table $env_table. The latter defaults to the current command table, i.e.,
  # $scan_proc->{env_table}. $env_names_or_tag may be a reference or a string. In the first
  # case, it is assumed that $env_names_or_tag points to a list of command names. These are
  # taken as the commands to import. In the latter case, i.e., if $env_names_or_tag is a
  # string, it is assumed that this string is a tag defined by the library $lib_name, and
  # the names corresponding to this tag are taken as the commands to import. $mode my by
  # "OVERWRITE" (the default) or "PROTECT". In the latter case, commands are imported only
  # if they are not already included in $env_table. Otherwise, they are imported
  # uncondicionally. $state_begin may be "DISABLED", "ACTIVE", or "INHERIT" (the
  # default). In the first two cases, the start of the imported environments will be
  # disabled resp. activated after import by setting the "begin_disabled" flag
  # correspondingly. In the latter case, the "begin_disabled" flag will be left
  # unchanged. Analogous holds true for $state_end.
  {
    log_message("\ninstall_envs 1/3\n");

    my ($env_names_or_tag, $lib_name, $mode, $state_begin, $state_end, $env_table) = @_;
    $mode ||= "OVERWRITE";
    $state_begin ||= "INHERIT";
    $state_end ||= "INHERIT";
    $env_table ||= $scan_proc->{env_table};

    if ( ! $lib_table->{$lib_name} )
      {
	if ( $autoload_libs )
	  {
	    require_lib($lib_name);
	  }
	else
	  {
            notify_error("install_envs: Library not loaded: $lib_name");
	  }
      }

    my $lib_env_table = $lib_table->{$lib_name}->{env_table};
    my $env_names;
    my $tag = FALSE;
    if ( ref($env_names_or_tag) )
      {
	$env_names = $env_names_or_tag;
      }
    else
      {
	$tag = $env_names_or_tag;
	if ( $tag eq "ALL" )
	  {
	    @{$env_names} = keys(%{$lib_env_table});
	  }
	elsif ( $lib_table->{$lib_name}->{env_tags}->{$tag} )
	  {
	    $env_names = $lib_table->{$lib_name}->{env_tags}->{$tag};
	  }
	else
	  {
            notify_error("install_envs: Library \"$lib_name\" does not define tag: $tag");
	  }
      }

    log_message("\ninstall_envs 2/3\n");
    log_data("Environments", join(" ", @{$env_names}),
	     "Tag", ( $tag || "-" ),
	     "Lib", $lib_name,
	     "Mode", $mode,
	     "State begin", $state_begin,
	     "State end", $state_end);

    foreach my $env_name (@{$env_names})
      {
	if ( ! $lib_env_table->{$env_name} )
	  {
            notify_error("install_envs: Library \"$lib_name\" does not export environment: $env_name");
	  }
	unless ( ( $env_table->{$env_name} ) && ( $mode ne "OVERWRITE" ) )
	  {
	    $env_table->{$env_name} = copy_hash($lib_env_table->{$env_name});
	  }
	if ( $state_begin eq "DISABLED" )
	  {
	    $env_table->{$env_name}->{begin_disabled} = TRUE;
	  }
	elsif( $state_begin eq "ACTIVE" )
	  {
	    $env_table->{$env_name}->{begin_disabled} = FALSE;
	  }
	if ( $state_end eq "DISABLED" )
	  {
	    $env_table->{$env_name}->{end_disabled} = TRUE;
	  }
	elsif( $state_end eq "ACTIVE" )
	  {
	    $env_table->{$env_name}->{end_disabled} = FALSE;
	  }
      }

    log_message("\ninstall_envs 3/3\n");
  }

sub disable_envs
  # ($env_names_or_tag, $lib_name)
  # Simply set the begin_disabled to true for every env
  {
    log_message("\ndisable_envs 1/3\n");
    my ($env_names_or_tag, $lib_name, $env_table) = @_;
    $env_table ||= $scan_proc->{env_table};

    if ( ! $lib_table->{$lib_name} )
      {
	notify_error("install_envs: Library not loaded: $lib_name");
      }

    my $lib_env_table = $lib_table->{$lib_name}->{env_table};
    my $env_names;

    my $tag = FALSE;
    if ( ref($env_names_or_tag) )
      {
	$env_names = $env_names_or_tag;
      }
    else
      {
	$tag = $env_names_or_tag;
	if ( $tag eq "ALL" )
	  {
	    @{$env_names} = keys(%{$lib_env_table});
	  }
	elsif ( $lib_table->{$lib_name}->{env_tags}->{$tag} )
	  {
	    $env_names = $lib_table->{$lib_name}->{env_tags}->{$tag};
	  }
	else
	  {
            notify_error("disable_envs: Library \"$lib_name\" does not define tag: $tag");
	  }
      }

    log_message("\ndisable_envs 2/3\n");
    log_data("Environments", join(" ", @{$env_names}),
	     "Tag", ( $tag || "-" ),
	     "Lib", $lib_name,
	     "State begin", "SET TO DISABLED");

    foreach my $env_name (@{$env_names})
      {
	if ( ! $lib_env_table->{$env_name} )
	  {
            notify_error("install_envs: Library \"$lib_name\" does not export environment: $env_name");
	  }
	$env_table->{$env_name}->{begin_disabled} = TRUE;
      }

    log_message("\ndisable_envs 3/3\n");
  }

sub install_cmds_from_all_libs
  #a ($tag, $mode, $state, $cmd_table)
  {
    my ($tag, $mode, $state, $cmd_table) = @_;

    if ( ref($tag) )
      {
        notify_error("install_cmds_from_all_libs: First arg must be a tag name: $tag");
      }

    foreach my $lib_name (keys(%{$lib_table}))
      {
	if ( $lib_table->{$lib_name}->{cmd_tags}->{$tag} )
	  {
	    install_cmds($tag, $lib_name, $mode, $state, $cmd_table);
	  }
      }
  }

sub install_envs_from_all_libs
  #a ($tag, $mode, $state, $env_table)
  {
    my ($tag, $mode, $state, $env_table) = @_;

    if ( ref($tag) )
      {
        notify_error("install_envs_from_all_libs: First arg must be a tag name: $tag");
      }

    foreach my $lib_name (keys(%{$lib_table}))
      {
	if ( $lib_table->{$lib_name}->{env_tags}->{$tag} )
	  {
	    install_envs($tag, $lib_name, $mode, $state, $env_table);
	  }
      }
  }

sub disable_cmds_from_all_libs
  #a ($tag)
  {
    my ($tag, $mode, $state, $cmd_table) = @_;

    if ( ref($tag) )
      {
        notify_error("disable_cmds_from_all_libs: First arg must be a tag name: $tag");
      }

    foreach my $lib_name (keys(%{$lib_table}))
      {
	if ( $lib_table->{$lib_name}->{cmd_tags}->{$tag} )
	  {
	    disable_cmds($tag, $lib_name, $mode, $state, $cmd_table);
	  }
      }
  }

sub disable_envs_from_all_libs
  #a ($tag)
  {
    my ($tag, $mode, $state, $env_table) = @_;

    if ( ref($tag) )
      {
        notify_error("disable_envs_from_all_libs: First arg must be a tag name: $tag");
      }

    foreach my $lib_name (keys(%{$lib_table}))
      {
	if ( $lib_table->{$lib_name}->{env_tags}->{$tag} )
	  {
	    disable_envs($tag, $lib_name, $mode, $state, $env_table);
	  }
      }
  }

sub get_cmds_for_tag
  #a ($tag)
  {
    my ($tag) = @_;
    my @cmd_names = ();
    foreach my $lib_name (keys(%{$lib_table}))
      {
        my $lib_cmd_names = $lib_table->{$lib_name}->{cmd_tags}->{$tag};
        if ( $lib_cmd_names )
          {
            foreach my $cmd_name (@{$lib_cmd_names})
              {
                push(@cmd_names, $cmd_name)
                  unless ( grep($cmd_name eq $_, @cmd_names) );
              }
          }
      }
    return(@cmd_names);
  }

sub require_lib
  # ($lib_name, $lib_options, $error_handler)
  {
    my ($lib_name, $lib_options, $error_handler) = @_;
    $error_handler ||= sub { die(@_, "\n") };
    my $lib_filename = "$lib_name.mtx.pl";
    my @inc_path_list = split(/:/, $inc_path);

    my $lib_full_filename
      = find_in_path($lib_filename, \@inc_path_list)
          or &{$error_handler}("Failed to load library: $lib_name\n",
			       "File not found in library search path: $lib_filename");
    log_message("\nrequire_lib (1/2)\n");
    log_data("Library name", $lib_name,
	     "Filename", $lib_full_filename,
	     "Options", $lib_options);

    $lib_options_buffer = $lib_options;

    {
      package Mumie::MmTeX::Converter;
      require($lib_full_filename)
	or &{$error_handler}("Failed to load library: $lib_name\n$!\n");
    }

    init_lib($lib_name, $error_handler);

    log_message("\nrequire_lib (2/2)\n");
  }

sub find_all_libs
  {
    my @dirs = split(/:/, $inc_path);
    my @libs = ();
    foreach my $dir (@dirs)
      {
	push(@libs, map({$_ =~ s/\.mtx\.pl$//; $_} grep(/\.mtx\.pl$/, get_dir_contents($dir))));
      }
    return(@libs);
  }

init();
return(1);
