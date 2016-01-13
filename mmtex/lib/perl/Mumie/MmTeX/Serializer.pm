package Mumie::MmTeX::Serializer;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: Serializer.pm,v 1.10 2009/11/16 14:08:22 gronau Exp $

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
# Provides functions to output the created XML.


use File::Basename qw(dirname);
use File::Path qw(mkpath);
use Mumie::Boolconst;
use Mumie::Logger qw(/.+/);
use Mumie::Text qw(:FORMAT);
use Mumie::List qw(/.+/);
use Mumie::Scanner qw($scan_proc);
use Mumie::XML::Writer qw(/.+/);
use Mumie::XML::Characters qw(/.+/);
use Mumie::MmTeX::Util qw(/.+/);
use Mumie::ExecFuncCalls;

require Exporter;
@ISA = qw(Exporter);
{
  my @_ALL = qw
    (
     $locked_output_list_default_error_msg
     $xml_special_char_output_mode
     @xml_namespace_list
     add_xml_auto_attribs
     append_to_output
     close_xml_element
     empty_xml_element
     get_last_xml_element_start
     get_xml_namespace
     lock_output_list
     log_attribs_str
     log_output_list
     new_xml_namespace
     output_list_locked
     output_list_to_string
     reset_xml_auto_attribs
     reset_xml_namespace
     resolve_xml_entity
     start_xml_element
     temporarily_other_output_list
     unlock_output_list
     write_output_to_file
     xml_decl
     xml_doctype
     xml_entity
     xml_pcdata
     xml_pr_instr
     xml_special_char
    );
  @EXPORT_OK = @_ALL;
  %EXPORT_TAGS =
    (
     'ALL' => \@_ALL,
    );
}

sub init
  {
    @xml_namespace_list =
      ([$xml_namespace, $xml_namespace_prefix, $xml_namespace_initialized]);
    # Stack of XML namespaces.

    $xml_special_char_output_mode = "AS_NUMCODES";
    # How special characters should be passed to the output.

    $locked_output_list_default_error_msg
      = "Current output list is locked.\n"
	. "(Probably you put code here which tries to write output, but output is not\n"
	  . "allowed in this place.)";

    $init_done = 1;
  }

sub reset_init
  {
    $init_done = 0;
  }

sub output_list_locked
  # $output_list
  # Returns TRUE when $output_list is locked, otherwise FALSE. Default for $output_list is
  # $scan_proc->{output_list}.
  {
    my $output_list = ( $_[0] or $scan_proc->{output_list} );
    if ( grep($_ == $output_list, @{$scan_proc->{locked_output_lists}}) )
      {
	return TRUE;
      }
    else
      {
	return FALSE;
      }
  }

sub lock_output_list
  # $output_list
  # Locks $output_list. After this has been called, \code{append_to_output} will cause an
  # error upon an attempt to write to $output_list. The default for $output_list is
  # $scan_proc->{output_list}.
  {
    my $output_list = ( $_[0] or $scan_proc->{output_list} );
    log_message("\nlock_output_list\n");
    log_data('Output list', $output_list);
    unless ( output_list_locked($output_list) )
      {
	push(@{$scan_proc->{locked_output_lists}}, $output_list);
      }
  }

sub unlock_output_list
  # $output_list
  # Unlocks $output_list. After this has been called, \code{append_to_output} will no longer
  # refuse write operations to $output_list. The default for $output_list is
  # $scan_proc->{output_list}.
  {
    my $output_list = ( $_[0] or $scan_proc->{output_list} );
    log_message("\nunlock_output_list\n");
    log_data('Output list', $output_list);
    if ( output_list_locked($output_list) )
      {
	remove_from_array($output_list, $scan_proc->{locked_output_lists});
      }
  }

sub append_to_output
  # (@new_entries)
  {
    if ( output_list_locked() )
      {
	my $error_msg
	  = ( exists($scan_proc->{locked_output_list_error_msg}) ?
              $scan_proc->{locked_output_list_error_msg}
	      : $locked_output_list_default_error_msg );

	&{$scan_proc->{error_handler}}($error_msg);
      }
    else
      {
	my @new_entries = @_;
	for (my $i = 0; $i <= $#new_entries; $i++)
	  {
	    if ( $new_entries[$i] )
	      {
		push(@{$scan_proc->{output_list}}, $new_entries[$i]);
	      }
	  }
      }
  }

sub temporarily_other_output_list
  # ($output_list, $code)
  # Saves the value of \code{$scan_proc->{output_list}} in a local variable,
  # sets \code{$scan_proc->{output_list}} to \code{output_list}, executes
  # \code{$code}, and sets \code{$scan_proc->{output_list}} back to the saved
  # value. \code{$code} must be a reference to a function.
  {
    my ($output_list, $code) = @_;
    my $saved_output_list = $scan_proc->{output_list};
    $scan_proc->{output_list} = $output_list;
    &{$code};
    $scan_proc->{output_list} = $saved_output_list;
  }

sub log_attribs_str
  #a ($attribs, $label)
  {
    my ($attribs, $label) = @_;
    $label ||= 'Attributes';

    my $log_str = ' ' x $log_data_indent
                . flush_string_right($label, $log_data_size_name)
                . ":\n";

    foreach my $name (keys(%{$attribs}))
      {
	my $value_spec = $attribs->{$name};

	my $value_notation;
	if ( ! defined($value_spec) )
	  {
	    $value_notation = 'UNDEF';
	  }
	elsif ( ! ref($value_spec) )
	  {
	    $value_notation = $value_spec;
	  }
	elsif ( ref($value_spec) eq 'CODE' )
	  {
	    $value_notation = "$value_spec -> " . &{$value_spec};
	  }
	else
	  {
	    $value_notation = "$value_spec";
	  }

	$log_str .= ' ' x ($log_data_indent * 2)
	         . flush_string_right($name, $log_data_size_name)
		 . "=" .  " " x $log_data_space_before_value
                 . $value_notation
                 . "\n";
      }

    return($log_str);
  }

sub add_xml_auto_attribs
  #a ($attribs)
  # Adds the auto attributes to $attribs.
  {
    my ($attribs) = @_;
   log_message("\nadd_xml_auto_attribs\n",
	       log_attribs_str($scan_proc->{xml_auto_attribs}));
    foreach my $name (keys(%{$scan_proc->{xml_auto_attribs}}))
      {
	unless ( $attribs->{$name} )
	  {
	    my $value = get_value($scan_proc->{xml_auto_attribs}->{$name});
	    $attribs->{$name} = $value 	    if ( defined($value) )
	  }
      }

    return($attribs);
  }

sub reset_xml_auto_attribs
  #a ()
  # Resets the auto attributes.
  {
    my $log_it = 0;
    foreach my $name (keys(%{$scan_proc->{xml_auto_attribs}}))
      {
	$scan_proc->{xml_auto_attribs}->{$name} = undef();
	$log_it = 1;
      }
    log_message("\nreset_xml_auto_attribs\n") if $log_it;  # Don't pollute the log
  }

sub start_xml_element
  # ($name, $attribs, $layout, $auto_attribs_mode)
  {
    my ($name, $attribs, $layout, $auto_attribs_mode) = @_;
    $auto_attribs_mode ||= 'NORMAL';

    log_message("\nstart_xml_element 1/2\n");

    log_data('Name', $name,
             'Layout', $layout,
             'Forced layout', $scan_proc->{forced_layout},
             'Auto attribs', $auto_attribs_mode);

    log_message(log_attribs_str($attribs));

    if ( $scan_proc->{markup_disabled} )
      {
        &{$scan_proc->{error_handler}}
          ("Markup not allowd. You used code which produces XML markup, which is not\n" .
           "allowed here.");
      }

    $layout = $scan_proc->{forced_layout} if ( $scan_proc->{forced_layout} );
    add_xml_auto_attribs($attribs) if ( $auto_attribs_mode eq 'NORMAL' );
    reset_xml_auto_attribs();

    my $output_entry = [\&write_xml_start_tag, $name, $attribs, $layout];
    $scan_proc->{last_element_start} = $output_entry;

    append_to_output($output_entry);

    log_message("\nstart_xml_element 2/2\n");
  }

sub close_xml_element
  # $name, $layout
  {
    my ($name, $layout) = @_;

    log_message("\nclose_xml_element 1/2\n");

    log_data('Name', $name,
             'Layout', $layout,
             'Forced layout', $scan_proc->{forced_layout});

    if ( $scan_proc->{forced_layout} )
      {
	$layout = $scan_proc->{forced_layout};
      }
    append_to_output([\&write_xml_end_tag, $name, $layout]);

    log_message("close_xml_element 2/2\n");
  }

sub empty_xml_element
  #a ($name, $attribs, $layout, $auto_attribs_mode)
  {
    my ($name, $attribs, $layout, $auto_attribs_mode) = @_;
    $auto_attribs_mode ||= 'NORMAL';

    log_message("\nempty_xml_element 1/2\n");

    log_data('Name', $name,
             'Layout', $layout,
             'Forced layout', $scan_proc->{forced_layout},
             'Auto attribs', $auto_attribs_mode);

    log_message(log_attribs_str($attribs));

    $layout = $scan_proc->{forced_layout} if ( $scan_proc->{forced_layout} );
    add_xml_auto_attribs($attribs) if ( $auto_attribs_mode eq 'NORMAL' );
    reset_xml_auto_attribs();

    append_to_output([\&write_empty_xml_element, $name, $attribs, $layout]);

    log_message("\nempty_xml_element 2/2\n");
  }

sub xml_pcdata
  # ($pcdata)
  {
    my ($pcdata, $layout) = @_;

    if ( $scan_proc->{forced_layout} )
      {
	$layout = $scan_proc->{forced_layout};
      }

    log_message("\nxml_pcdata \n");
    log_data("Text", $pcdata, "Layout", ( $layout || "" ));

    append_to_output([\&write_xml_pcdata, $pcdata, $layout]);
  }


sub xml_entity
  # ($name)
  {
    my ($name, $layout) = @_;

    if ( $scan_proc->{forced_layout} )
      {
	$layout = $scan_proc->{forced_layout};
      }

    log_message("\nxml_entity\n");
    log_data('Name', $name, 'Layout', $layout);

    append_to_output([\&write_xml_pcdata, "&$name;", $layout]);
  }

sub xml_decl
  # ($version, $encoding, $standalone, $layout)
  {
    my ($version, $encoding, $standalone, $layout) = @_;

    if ( $scan_proc->{forced_layout} )
      {
	$layout = $scan_proc->{forced_layout};
      }

    log_message("\nxml_decl\n");
    log_data('Version', $version, 'Encoding', $encoding, 'Standalone', $standalone,
	     'Layout', $layout);

    append_to_output([\&write_xml_decl, $version, $encoding, $standalone,, $layout]);
  }

sub xml_doctype
  # ($attrib_list, $layout)
  {
    my ($attrib_list, $layout) = @_;

    if ( $scan_proc->{forced_layout} )
      {
	$layout = $scan_proc->{forced_layout};
      }

    log_message("\nxml_doctype\n");
    log_data('Attributes', join(' ', @{$attrib_list}), 'Layout', $layout);

    append_to_output([\&write_xml_doctype_tag, $attrib_list, $layout]);
  }

sub xml_pr_instr
  # ($target, $attrib_list, $layout)
  {
    my ($target, $attrib_list, $layout) = @_;

    if ( $scan_proc->{forced_layout} )
      {
	$layout = $scan_proc->{forced_layout};
      }

    log_message("\nxml_pr_instr\n");
    log_data('Target', $target, 'Attributes', join(' ', @{$attrib_list}), 'Layout', $layout);

    append_to_output([\&write_xml_pr_instr, $target, $attrib_list, $layout]);
  }

sub resolve_xml_entity
  # ($entity)
  {
    my $entity = $_[0];

    log_message("\nresolve_xml_entity 1/2\n");
    log_data("Entity", $entity, "Output", $xml_special_char_output_mode);

    my $result;

    if ( $xml_special_char_output_mode eq "AS_ENTITIES" )
      {
	$result = "&" . $entity . ";";
      }
    elsif ( $xml_special_char_output_mode eq "AS_NUMCODES" )
      {
	$result =  $entities_vs_numcodes{$entity};
      }
    elsif ( $xml_special_char_output_mode eq "LITERAL" )
      {
	if ( ( $entities_vs_chars{$entity} ) && ( $entity !~ m/^amp|lt|gt$/ ) )
	  {
	    $result = $entities_vs_chars{$entity};
	  }
	else
	  {
	    $result = "&" . $entity . ";";
	  }
      }
    elsif ( $xml_special_char_output_mode eq "STRICTLY_LITERAL" )
      {
	if ( $entities_vs_chars{$entity} )
	  {
	    $result = $entities_vs_chars{$entity};
	  }
	else
	  {
	    $result = "&" . $entity . ";";
	  }
      }

    log_message("\nresolve_xml_entity 2/2\n");
    log_data("Entity", $entity, "Result", $result);

    return $result;
  }

sub xml_special_char
  # ($entity)
  {
    my $entity = $_[0];

    log_message("\nxml_special_char\n");
    log_data("Entity", $entity, "Output", $xml_special_char_output_mode);

    xml_pcdata(resolve_xml_entity($entity));

#     if ( $xml_special_char_output_mode eq "AS_ENTITIES" )
#       {
# 	xml_pcdata("&" . $entity . ";");
#       }
#     elsif ( $xml_special_char_output_mode eq "AS_NUMCODES" )
#       {
# 	xml_pcdata($entities_vs_numcodes{$entity});
#       }
#     elsif ( $xml_special_char_output_mode eq "LITERAL" )
#       {
# 	if ( ( $entities_vs_chars{$entity} ) && ( $entity !~ m/^amp|lt|gt$/ ) )
# 	  {
# 	    xml_pcdata($entities_vs_chars{$entity});
# 	  }
# 	else
# 	  {
# 	    xml_pcdata("&" . $entity . ";");
# 	  }
#       }
#     elsif ( $xml_special_char_output_mode eq "STRICTLY_LITERAL" )
#       {
# 	if ( $entities_vs_chars{$entity} )
# 	  {
# 	    xml_pcdata($entities_vs_chars{$entity});
# 	  }
# 	else
# 	  {
# 	    xml_pcdata("&" . $entity . ";");
# 	  }
#       }
  }

sub new_xml_namespace
  #a ($new_namespace, $new_namespace_prefix, $already_declared)
  # Switches to a new XML namespace. The arguments have the following meaning:
  #DL
  #  $new_namespace  &
  #              The new namespace (URI) \\
  #  $new_namespace_prefix
  #              The new namespace prefix \\
  #  $already_declared &
  #              Whether the namespace is declared elsewhere \\
  #/DL
  # After a call to this function, all XML elements emitted by the serializer are assumed to
  # belong to namespace $new_namespace. If $already_declared is false, the namespace will be
  # declared in the next element (by a xmlns or xmlns:foo attribute). Otherwise, it is assumed
  # that the namespace has already been declared. If $new_namespace_prefix has a true value,
  # all elements are given $new_namespace_prefix as namespace prefix.
  {
    my ($new_namespace, $new_namespace_prefix, $already_declared) = @_;
    append_to_output
      ([\&set_xml_namespace, $new_namespace, $new_namespace_prefix, $already_declared]);
    push(@xml_namespace_list, [$new_namespace, $new_namespace_prefix]);
  }

sub reset_xml_namespace
  {
    pop(@xml_namespace_list);
    if ( @xml_namespace_list )
      {
        my ($old_namespace, $old_namespace_prefix) = @{$xml_namespace_list[$#xml_namespace_list]};
        append_to_output([\&set_xml_namespace, $old_namespace, $old_namespace_prefix, TRUE]);
      }
    else
      {
        append_to_output([\&set_xml_namespace, undef()]);
      }
  }

sub get_xml_namespace
  {
    return
      (@xml_namespace_list
       ? @{$xml_namespace_list[$#xml_namespace_list]}
       : (undef(), undef()));
  }

sub get_last_xml_element_start
  #a ()
  # Returns the entry in the current output list that starts the last XML element that is
  # not written in the abbreviated form for empty elements (i.e, `<foo .../>), or `FALSE` if
  # no such element exists. Thus, the return value, if not `FALSE`, is a reference to a list
  # of the form
  #c
  #  (\&write_xml_start_tag, $name, $attribs, $layout)
  #/c
  # where $name, $attribs, $layout are the name, attributes (as a reference to a hash of
  # name-value pairs), and layout (as a keyword conforming to Mumie::XML::Writer) of the
  # start tag.
  {
    my $i = scalar(@{$scan_proc->{output_list}});
    my $depth = 0;
    my $xml_element_start = FALSE;
    while ( ( $depth < 1 ) && ( $i > 0 ) )
      {
	$i--;
	if ( $scan_proc->{output_list}->[$i]->[0] == \&write_xml_start_tag )
	  {
	    $depth++;
	  }
	elsif  ( $scan_proc->{output_list}->[$i]->[0] == \&write_xml_end_tag )
	  {
	    $depth--;
	  }
      }
    if ( $depth == 1 )
      {
	$xml_element_start = $scan_proc->{output_list}->[$i];
      }
    return($xml_element_start);
  }

sub log_output_list
  {
    log_message("\nXML writer functions:",
	  "\n---------------------\n",
	  "\nwrite_xml_pcdata        ", \&write_xml_pcdata,
	  "\nwrite_xml_start_tag     ", \&write_xml_start_tag,
	  "\nwrite_xml_end_tag       ", \&write_xml_end_tag,
	  "\nwrite_empty_xml_element ", \&write_empty_xml_element,
	  "\n\n",
	  "\nOutput list:",
	  "\n-----------\n");
    for (my $i = 0; $i < scalar(@{$scan_proc->{output_list}}); $i++)
      {
	log_message("\n", join( ' ', @{$ {$scan_proc->{output_list}}[$i]}));
      }
    log_message("\n\n");
  }

sub output_list_to_string
  # ([$output_list])
  {
    my $output_list = ($_[0] or $scan_proc->{output_list});
    reset_xml_writer();
    exec_func_calls(@{$output_list});
    return(join('', @xml_output_list));
  }

sub write_output_to_file
  # ($output_file, [$output_list])
  {
    my $output_file = $_[0];
    my $output_list = ($_[1] or $scan_proc->{output_list});
    log_message("\nwrite_output_to_file\n");
    log_data("Destination", $output_file);
    mkpath(dirname($output_file));
    # start windows workaround
  	if ($^O eq "MSWin32" || $^O eq "MSWin64") {
      my $dir = Mumie::File::get_working_dir();
      $dir = Win32::GetShortPathName($dir);
      $output_file = $dir . "/" . $output_file;
    }
    # end windows workaround
    open(OUTPUT, ">$output_file") or
      &{$scan_proc->{error_handler}}("Can't open file \`$output_file\': $!\n");
    print(OUTPUT output_list_to_string($output_list));
    close(OUTPUT);
  }

init();
return(1);
