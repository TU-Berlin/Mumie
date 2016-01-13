package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: list.mtx.pl,v 1.22 2007/08/27 23:09:13 rassy Exp $

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
# h2: Description
# --------------------------------------------------------------------------------
#
# Implements the list environments.

log_message("\nLibrary \"list\" ", '$Revision: 1.22 $', "\n");

require_lib('counter');

# --------------------------------------------------------------------------------
#1 List counters
# --------------------------------------------------------------------------------

sub get_successor_of_list_item_counter
  #a ($counter)
  # Returns the enumeration counter which is next to $counter in @list_item_counters.
  {
    my $counter = $_[0];
    my ($i, $length) = (0, scalar(@list_item_counters));
    while ( ( $i < $length ) && ( $list_item_counters[$i] ne $counter ) )
      {
        $i++;
      }
    if ( $i == $length )
      {
        &{$scan_proc->{error_handler}}
          ("Not a enumeration counter: $counter");
      }
    if ( $i == $length-1 )
      {
        &{$scan_proc->{error_handler}}
          ("No successor defined for enumeration counter: $counter\n");
      }
    return($list_item_counters[$i+1]);
  }

sub get_next_list_item_counter
  {
    return
      ($scan_proc->{list_item_counter}
       ? get_successor_of_list_item_counter($scan_proc->{list_item_counter})
       : $list_item_counters[0]);
  }

# --------------------------------------------------------------------------------
#1 List items
# --------------------------------------------------------------------------------

sub start_list_item
  # $label_output_list
  {
    my $label_output_list = $_[0];

    $scan_proc->{list_item_number}++;

    log_message("\nstart_list_item 1/3\n");
    log_data("Number", $scan_proc->{list_item_number}, "Label", $label_output_list);

    my $item_attribs = {};
    if ( $scan_proc->{list_item_counter} )
      {
        my $counter = $scan_proc->{list_item_counter};
        add_to_counter($counter, 1);
        my $value = get_counter_value($counter);
        $item_attribs->{no} = $value;
        log_message("\nstart_list_item 2/3\n");
        log_data('Counter', $counter, 'Value', $value);
      }
    else
      {
        log_message("\nstart_list_item 2/3: No counter\n");
      }

    new_scan_proc("COPY", "OWN_NAMESPACE");
    $scan_proc->{current_env} = "_list item";
    $scan_proc->{inside_list_item} = TRUE;
    start_xml_element("item", $item_attribs, "DISPLAY");
    if ( $label_output_list )
      {
	start_xml_element("label", {}, "INLINE");
	append_to_output(@{$label_output_list});
	close_xml_element("label", "INLINE");
      }
    start_xml_element("content", {}, "DISPLAY");
    log_message("\nstart_list_item 2/2\n");
  }

sub close_list_item
  {
    log_message("\nclose_list_item 1/2\n");
    if ( $scan_proc->{current_env} ne "_list item" )
      {
	&{$scan_proc->{error_handler}}
	  (appr_env_notation($scan_proc->{current_env}),
	   ' must be opened and closed within same list item.');
      }
    else
      {
	close_xml_element("content", "DISPLAY");
	close_xml_element("item", "DISPLAY");
	reset_scan_proc();
      }
    log_message("\nclose_list_item 2/2\n");
  }

sub execute_item
  {
    log_message("\nexecute_item 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    if ( $scan_proc->{list_item_number} == 0 )
      {
	# Allowing output
	unlock_output_list();
	disallow_tokens(["ign_whitesp"]);
      }
    elsif ( $scan_proc->{list_item_number} > 0 )
      {
	# Closing paragraph if necessary
	if ( $scan_proc->{current_env} eq "_paragraph" )
	  {
	    close_par();
	  }

	# Closing last item
	close_list_item();
      }
    else
      {
	notify_programming_error
	  ('execute_item', ' Invalid item number value: ', $scan_proc->{list_item_number});
      }

    # Getting the label
    my $label_output_list;
    if ( $opt_args->[0] )
      {
	$label_output_list
	  = convert_arg(0, $opt_args, $pos_opt_args, "CMD", "item", "label_arg");
      }
    else
      {
	$label_output_list = FALSE;
      }

    # Starting list item
    start_list_item($label_output_list);

    log_message("\nexecute_item 2/2\n");
  }

# --------------------------------------------------------------------------------
#1 List construction functions
# --------------------------------------------------------------------------------

sub start_list
  # ($env_name, $element_name, $class, $numbering, $attribs)
  {
    log_message("\nstart_list 1/2\n");

    my ($env_name, $element_name, $class, $numbering, $attribs) = @_;

    if ( ( $class ne "normal" )
	 && ( ! grep($class eq $_,
		     @{$scan_proc->{env_table}->{$env_name}->{allowed_classes}}) ) )
      {
	&{$scan_proc->{error_handler}}("Invalid $env_name class: ", $class);
      }

    install_cmds(["item"], "list");

    $attribs->{numbering} = $numbering;
    if ( $class ne "normal" )
      {
	$attribs->{class} = $class;
      }

    start_xml_element($element_name, $attribs, "DISPLAY");

    allow_tokens(["ign_whitesp"], "PROTECT", "PREPEND");
    $scan_proc->{list_item_number} = 0;
    lock_output_list();
    $scan_proc->{locked_output_list_error_msg}
      = "No text allowed before the first \\item.";

    log_message("\nstart_list 2/2\n");
  }

sub close_everything_in_list
  {
    log_message("\nclose_everything_in_list 1/2\n");

    if ( output_list_locked() )
      {
	unlock_output_list();
      }

    if ( $scan_proc->{current_env} eq "_paragraph" )
      {
	close_par();
      }

    if ( $scan_proc->{current_env} eq "_list item" )
      {
	close_list_item();
      }

    log_message("\nclose_everything_in_list 2/2\n");
  }

sub close_list
  {
    my ($element_name) = @_;
    log_message("\nclose_list 1/2\n");
    close_xml_element($element_name, "DISPLAY");
    log_message("\nclose_list 2/2\n");
  }

# --------------------------------------------------------------------------------
#1 The 'itemize' environment
# --------------------------------------------------------------------------------

sub begin_itemize
  {
    log_message("\nbegin_itemize 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $class
      = ( $opt_args->[0] ? get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD") : "normal" );

    start_list("itemize", "list", $class, "no", {});

    log_message("\nbegin_itemize 2/2\n");
  }

sub end_itemize
  {
    log_message("\nend_itemize 1/2\n");
    close_list("list");
    log_message("\nend_itemize 2/2\n");
  }

# --------------------------------------------------------------------------------
#1 The 'enumerate' environment
# --------------------------------------------------------------------------------

sub begin_enumerate
  {
    log_message("\nbegin_enumerate 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $class
      = ( $opt_args->[0] ? get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD") : "normal" );

    start_list("enumerate", "list", $class, "yes", {});

    $scan_proc->{list_item_counter} = get_next_list_item_counter();
    set_counter($scan_proc->{list_item_counter}, 0);

    log_message("\nbegin_enumerate 2/2\n");
    log_data('Counter', $scan_proc->{list_item_counter});
  }

sub end_enumerate
  {
    log_message("\nend_enumerate 1/2\n");
    close_list("list");
    log_message("\nend_enumerate 2/2\n");
  }

# --------------------------------------------------------------------------------
#1 The 'description' environment
# --------------------------------------------------------------------------------

sub begin_description
  {
    log_message("\nbegin_description 1/2\n");

    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;

    my $class
      = ( $opt_args->[0] ? get_data_from_arg(0, $opt_args, $pos_opt_args, "CMD") : "normal" );

    start_list("description", "description-list", $class, "no", {});

    log_message("\nbegin_description 2/2\n");
  }

sub end_description
  {
    log_message("\nend_description 1/2\n");
    close_list("description-list");
    log_message("\nend_description 2/2\n");
  }



# --------------------------------------------------------------------------------=
# Environment and command tables for export
# --------------------------------------------------------------------------------




# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

$lib_table->{list}->{initializer} = sub
  {
    # Enumeration counters:
    @list_item_counters =
      (
       'enumi',
       'enumii',
       'enumiii',
       'enumiv',
       'enumv',
       'enumvi',
       'enumvii',
       'enumiix',
      );

    # Environment table to export
    my $list_env_table =
      {
       'itemize' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_closer" => TRUE,
          "begin_function" => \&begin_itemize,
          "end_function" => \&end_itemize,
          "early_pre_end_hook" => \&close_everything_in_list,
          "allowed_classes" => ["normal", "bullet", "circle", "square"],
          "doc" =>
            {
             "opt_args" => [["class", "class"]],
             "description" => "Unordered list. Items are marked by a graphical symbol. " .
                              "Default is a circle. This may be changed via the %[1] argument.",
             "see" => {"cmds" => ["item"]},
            }
           },
       'enumerate' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_closer" => TRUE,
          "begin_function" => \&begin_enumerate,
          "end_function" => \&end_enumerate,
          "early_pre_end_hook" => \&close_everything_in_list,
          "allowed_classes" => ["arabic", "roman", "Roman", "alph", "Alph", "greek"],
          "doc" =>
            {
             "opt_args" => [["class", "class"]],
             "description" => "Ordered list. Items are numerated in some way. Default is " .
	                      "decimal numbers. This may be changed via the %[1] argument.",

             "see" => {"cmds" => ["item"]},
            }
         },
       'description' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_closer" => TRUE,
          "begin_function" => \&begin_description,
          "end_function" => \&end_description,
          "early_pre_end_hook" => \&close_everything_in_list,
          "allowed_classes" => ["code-doc"],
          "doc" =>
            {
             "opt_args" => [["class", "class"]],
             "description" => "Unordered list. Items are marked by labels. These are " .
                              "specified by the \\item commands inside the list.",

             "see" => {"cmds" => ["item"]},
            }
         },
      };

    # Command table to export
    my $list_cmd_table =
      {
       'item' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 0,
          "is_par_starter" => FALSE,
          "execute_function" => \&execute_item,
          "pre_label_arg_hook" => sub { $scan_proc->{par_enabled} = FALSE },
          "no_tbl_elements_autostart" => TRUE,
          "doc" =>
            {
             "opt_args" => [["LABEL", "Label of the list item"]],
             "description" => "Starts a new list item. If %[1] is given, it  becomes the label " . 
	                      "of the item.",
             "see" => {"envs" => ["itemize", "enumerate", "description"]},
            }
         }
      };

    my @list_envs = keys(%{$list_env_table});
    my @list_cmds = keys(%{$list_cmd_table});

    deploy_lib('list', $list_cmd_table, $list_env_table,
               {'LIST' => \@list_cmds, 'TOPLEVEL' => \@list_cmds,},
               {'LIST' => \@list_envs, 'TOPLEVEL' => \@list_envs,});

    foreach my $counter (@list_item_counters)
      {
        new_counter($counter);
      }
  };

return(TRUE);
