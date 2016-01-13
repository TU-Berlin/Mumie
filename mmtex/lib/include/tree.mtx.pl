package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: tree.mtx.pl,v 1.1 2007/08/27 22:55:24 rassy Exp $

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
# h1: Description
# --------------------------------------------------------------------------------
#
# Implements trees like the following:
#
# ROOT
#  |- org
#  |   |- users
#  |   |- user_groups
#  |   '- uni
#  |      '- sum_2007
#  |          |- classes
#  |          '- courses
#  |- system
#  |   |- common
#  |   |- libraries
#  |   |- element
#  |   |- problem
#  |   '- course
#  |       '- images
#  '- content
#
# This library provides the following control structures:
#
#   Environment: 'tree'
#
#     Top-level container for trees. Synopsis:
#
#       \begin{tree}[CLASS]{LABEL}
#         % 'branch' environments and/or 'leaf' commands
#       \end{tree}
#
#     Starts a tree and creates the root node with the label LABEL. The label
#     of a node is the text representing it. LABEL may contain other commands
#     and/or environments. By the optional argument CLASS, a class can be
#     assigned to the tree. The content of the 'tree' environment consists of
#     'branch' environments and/or 'leaf' commands (see below). They represent
#     the children of the root node.
#
#   Environment: 'branch'
#
#     Intruduces a branch of the tree. Synopsis:
#
#       \begin{branch}{LABEL}
#         % 'branch' environments and/or 'leaf' commands
#       \end{tree}
#
#     Starts a branch of the tree (or of another branch of the tree). A branch
#     is simply a node which can have (and usually has) child nodes. LABEL is
#     the label of the node. LABEL may contain other commands and/or
#     environments. The content of the 'branch' environment consists of other
#     'branch' environments and/or 'leaf' commands (see below). They represent
#     the children of the node.
#
#  Command: 'leaf'
#
#    Creates a leaf node. Synopsis:
#
#      \leaf{LABEL}.
#
#    LABEL is the label of the node. LABEL may contain other commands
#    and/or environments.
#
#  Here is an examples:
#
#   \begin{tree}{\var{ROOT}}
#     \begin{branch}{org}
#      \leaf{{users}
#      \leaf{{user_groups}
#      \begin{branch}{uni}
#        \leaf{wisem_2007_2008}
#        \leaf{sosem_2008}
#      \end{branch}
#     \end{branch}
#     \begin{branch}{system}
#       \leaf{common}
#       \leaf{problem}
#       \leaf{course}
#     \end{branch}
#   \end{tree}
#

log_message("\nLibrary \"tree\" ", '$Revision: 1.1 $', "\n");

# --------------------------------------------------------------------------------
# h1: Functions
# --------------------------------------------------------------------------------

sub start_tree_node
  {
    log_message("\nstart_node 1/2\n");
    my ($ctr_type, $env, $args, $pos_args) = @_;
    start_xml_element('node', {}, 'DISPLAY');
    start_xml_element('label', {}, 'INLINE');
    convert_arg
      (0, $args, $pos_args, $ctr_type, $env, 'label_arg',
       FALSE, FALSE, 'SHARED_OUTPUT_LIST');
    close_xml_element('label', 'INLINE');
    start_xml_element('children', {}, 'DISPLAY');
    log_message("\nstart_node 2/2\n");
  }

sub close_tree_node
  {
    log_message("\nclose_node 1/2\n");
    close_xml_element('children', 'DISPLAY');
    close_xml_element('node', 'DISPLAY');
    log_message("\nclose_node 2/2\n");
  }

sub begin_tree
  {
    log_message("\nbegin_tree 1/2\n");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    $scan_proc->{par_enabled} = FALSE;
    my $attribs = {};
    if ( $opt_args->[0] )
      {
        $attribs->{class} =
           get_data_from_arg(0, $opt_args, $pos_opt_args, 'ENV', 'tree', 'class_arg');
      }
    start_xml_element('tree', $attribs. 'DISPLAY');
    start_tree_node('ENV', 'tree', $man_args, $pos_man_args);
    install_envs('TREE', 'tree');
    install_cmds('TREE', 'tree');
    log_message("\nbegin_tree 2/2\n");
  }

sub end_tree
  {
    log_message("\nend_tree 1/2\n");
    close_tree_node();
    close_xml_element('tree', 'DISPLAY');
    log_message("\nend_tree 2/2\n");
  }


sub begin_branch
  {
    log_message("\nbegin_branch 1/2\n");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    start_tree_node('ENV', 'branch', $man_args, $pos_man_args);
    log_message("\nbegin_branch 2/2\n");
  }

sub end_branch
  {
    log_message("\nend_branch 1/2\n");
    close_tree_node();
    log_message("\nend_branch 2/2\n");
  }

sub execute_leaf
  {
    log_message("\nexecute_leaf 1/2\n");
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    start_tree_node('CMD', 'leaf', $man_args, $pos_man_args);
    close_tree_node();
    log_message("\nexecute_leaf 2/2\n");
  }

# --------------------------------------------------------------------------------
# h1: Initializer
# --------------------------------------------------------------------------------

$lib_table->{tree}->{initializer} = sub
  {

    # Environment table to export
    my $tree_env_table =
      {
       'tree' =>
         {
          "num_opt_args" => 1,
          "num_man_args" => 1,
          "is_par_closer" => TRUE,
          "begin_function" => \&begin_tree,
          "end_function" => \&end_tree,
          "doc" =>
            {
             "man_args" => [["label", "Label of the root node"]],
             "opt_args" => [["class", "Class of the tree"]],
             "description" => "A tree, similar to a directory tree.",
             "see" => {"envs" => ["branch"]},
             "see" => {"cmds" => ["leaf"]},
            }
           },
       'branch' =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "begin_function" => \&begin_branch,
          "end_function" => \&end_branch,
          "doc" =>
            {
             "man_args" => [["label", "Label of the branch node"]],
             "description" => "Branch of a tree. This a tree node which may have children.",
             "see" => {"envs" => ["tree"]},
             "see" => {"cmds" => ["leaf"]},
            }
           },
      };

    # Command table to export
    my $tree_cmd_table =
      {
       'leaf' =>
         {
          "num_opt_args" => 0,
          "num_man_args" => 1,
          "execute_function" => \&execute_leaf,
          "doc" =>
            {
             "man_args" => [["label", "Label of the tree node"]],
             "description" => "A tree node which has no children.",
             "see" => {"envs" => ['tree', 'branch']},
            }
         }
      };

    deploy_lib
      ('tree', $tree_cmd_table, $tree_env_table,
       {'TREE' => ['leaf']},
       {'TREE' => ['branch'], 'TOPLEVEL' => ['tree']});
  };

return(TRUE);
