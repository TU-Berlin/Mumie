package Mumie::MmTeX::Converter;

# Author: Tilman Rassy <rassy@math.tu-berlin.de>

# $Id: specialchar.mtx.pl,v 1.18 2007/07/11 15:56:15 grudzin Exp $

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
#1                                  Description
# --------------------------------------------------------------------------------
#
# Implements commands to insert special characters

log_message("\nLibrary specialchar, ",
	    '$Id: specialchar.mtx.pl,v 1.18 2007/07/11 15:56:15 grudzin Exp $ ', "\n");


# --------------------------------------------------------------------------------
# German umlaut characters
# --------------------------------------------------------------------------------

sub exec_umlaut_cmd
  # ($opt_args, $man_args, $pos_opt_args, $pos_man_args)
  {
    my ($opt_args, $man_args, $pos_opt_args, $pos_man_args) = @_;
    my $char = $man_args->[0];

    log_message("\nexec_umlaut_cmd\n");
    log_data("Character", $char);

    if ( $char =~ m/^[aouAOUs3]$/ )
      {
	my $entity;
	if ( grep($char eq $_, "s", "3") )
	  {
	    $entity = "szlig";
	  }
	else
	  {
	    $entity = $char . "uml";
	  }
	xml_special_char($entity);
      }
    else
      {
	\&{$scan_proc->{error_handler}}("No umlaut for character: $char");
      }
  }

# ------------------------------------------------------------------------------------------
# Initializing
# ------------------------------------------------------------------------------------------

# $import_cmd_tables->{specialchar} = $specialchar_cmd_table;

$lib_table->{specialchar}->{initializer} = sub
  {
    # Command table for export
    my $specialchar_cmd_table
      = {
         "\$" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("\$") },
            "doc" =>
              {
              "description" => 'Dollar sign ($).',
              }
           },
         "\&" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("amp") },
            "doc" =>
              {
              "description" => 'Ampersand sign (&amp;).',
              }
           },
         "%" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("%") },
            "doc" =>
              {
              "description" => 'Percent sign (%).',
              }
           },
         "~" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("~") },
            "doc" =>
              {
              "description" => 'Tilde (~).',
              }
           },
         "#" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("#") },
            "doc" =>
              {
              "description" => '# character.',
              }
           },
         "_" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("_") },
            "doc" =>
              {
              "description" => 'Underscore character (_).',
              }
           },
         "^" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("^") },
            "doc" =>
              {
              "description" => 'Hat character (^).',
              }
           },
         "{" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("{") },
            "doc" =>
              {
              "description" => 'Left curly brace ({).',
              }
           },
         "}" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("}") },
            "doc" =>
              {
              "description" => 'Right curly brace (}).',
              }
           },
         "S" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("sect") },
            "doc" =>
              {
              "description" => 'Section sign (&sect;).',
              }
           },
         "dag" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("dagger") },
            "doc" =>
              {
              "description" => 'Dagger sign (&dagger;).',
              }
           },
         "ddag" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("Dagger") },
            "doc" =>
              {
              "description" => 'Double dagger sign (&Dagger;).',
              }
           },
         "P" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("para") },
            "doc" =>
              {
              "description" => 'Paragraph sign (&para;).',
              }
           },
         "copyright" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("copy") },
            "doc" =>
              {
              "description" => 'Copyright sign (&copy;).',
              }
           },
         "euro" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("euro") },
            "doc" =>
              {
              "description" => 'Euro sign (&euro;)',
              }
           },
         "pound" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("pound") },
            "doc" =>
              {
              "description" => 'Pound sign (&pound;).',
              }
           },
         "AE" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("AElig") },
            "doc" =>
              {
              "description" => 'A-E compound sign (&AElig;).',
              }
           },
         "AA" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("Aring") },
            "doc" =>
              {
              "description" => 'A with ring (&Aring;).',
              }
           },
         "ae" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("aelig") },
            "doc" =>
              {
              "description" => 'a-e compound sign (&aelig;).',
              }
           },
         "O" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("Oslash") },
            "doc" =>
              {
              "description" => 'Capital O with a / (&Oslash;).',
              }
           },
         "o" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("oslash") },
            "doc" =>
              {
              "description" => 'Small o with a / (&oslash;).',
              }
           },
         "?" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("iquest") },
            "doc" =>
              {
              "description" => 'Upside down question mark (&iquest;).',
              }
           },
         "!" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("iexcl") },
            "doc" =>
              {
              "description" => 'Upside down exclamation mark (&iexcl;).',
              }
           },
         "ss" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("szlig") },
            "doc" =>
              {
              "description" => 'German sharp s (&szlig;).',
              }
           },
         "SS" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("SS") },
            "doc" =>
              {
              "description" => 'German capital sharp s (SS).',
              }
           },
         "\"" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 1,
            "is_par_starter" => TRUE,
            "execute_function" => \&exec_umlaut_cmd,
            "doc" =>
              {
                'man_args' => [['char', 'Character to turn into umlaut']],
                'description' => 'German umlaut. Prints the umlaut of %{1}.',
              }
           },
         "ldots" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("hellip") },
            "doc" =>
              {
              "description" => 'Three dots (&hellip;).',
              }
           },
         "vdots" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("vellip") },
            "doc" =>
              {
              "description" => 'Three vertical dots (&vellip;).',
              }
           },
         "cdots" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("ctdot") },
            "doc" =>
              {
              "description" => 'Three dots (&ctdot;).',
              }
           },
         "ddots" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_special_char("dtdot") },
            "doc" =>
              {
              "description" => 'Three diagonal dots (&dtdot;).',
              }
           },
         "backslash" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => TRUE,
            "execute_function" => sub { xml_pcdata("\\") },
            "doc" =>
              {
              "description" => 'A backslash (\).',
              }
           },
         "/" =>
           {
            "num_opt_args" => 0,
            "num_man_args" => 0,
            "is_par_starter" => FALSE,
            "execute_function" => sub { },
            "doc" =>
              {
              "description" => 'Nothing - can be used as invisable separator.',
              }
           },
        };

    $specialchar_cmd_table->{euros} = $specialchar_cmd_table->{euro};
    $specialchar_cmd_table->{pounds} = $specialchar_cmd_table->{pound};

    deploy_lib('specialchar', $specialchar_cmd_table, {}, 'TOPLEVEL', FALSE);
  };

return(TRUE);


