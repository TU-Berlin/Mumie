#!/usr/bin/perl -w

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

use File::Listing;
use Env qw(MMTEX_HOME);
use Getopt::Long;
my $CMD_line_HOME = '';

GetOptions
  (
   'HOME=s' => \&check_home,
  );


if ( ! $MMTEX_HOME )
  {
    die("WARNING: \$MMTEX_HOME not set\nPlease set or use command line option --HOME=foo \n");
  }


# Needed variables and conditions for this script..
#
# $parse_starting_directory = The root perl directory with all sources to parse
#

# Set directory
$parse_starting_directory = "$MMTEX_HOME/docs/source_docs/perl";


# Set the frames/noframes option (default = frames)
$frames_option = 1;

# Set the frames index file
$frames_index_pure_name = "sub_index.html";
$frames_index = "$parse_starting_directory/$frames_index_pure_name";

$frameset = "$parse_starting_directory/frameset.html";

# Set the index.dat file location
$index_file = "$parse_starting_directory/sub_index.dat";




# Default values for multiple link target files
#
# Normally the program tries to find the right target, if a function is
# used by the Package::Filename::function syntax, the default value is
# set to Filename
# But in some cases the relations are not so easy to reconstruct, so if
# you already know that a function is overwritten by another one, and
# 90% of the references point to the second location, you may use this
# as default

%default_link_target = ( new_scan_proc   => "Converter",
			 reset_scan_proc => "Converter" );







#####
#####  Begin of source code, nothing user relevant below
#####
########################################################################
########################################################################
########################################################################
########################################################################


# Answer initialization for global answer
my $global_answer = 0;

# Empty the index file..
open (SUB_INDEX, ">$index_file") ||
  die "Can't open sub index file located: $index_file";
close SUB_INDEX;

open (FRAME_INDEX, ">$frames_index") ||
  die "Can't open frame index file located: $frames_index";
close FRAME_INDEX;


# Run (step 1) -> Collecting, setting anchors, creating sub_index.dat
print  "\n\nStart parsing files, collecting subs and adding them to list file ";
$parse_mode      = "get_complete_sublist";
recursive_dir_parse($parse_starting_directory);
print "  .... done";
print  "\nIndex file located at ($index_file)";
#-----------------------------------------------------------------------#



# Run (step 2) -> Parsing 'sub_index.dat', filtering doubles, sorting, creating sub_index.html
print "\n\nStart parsing index file, filtering doubles and sorting ";
order_sub_index("$parse_starting_directory/sub_index.dat");
print "   ....   done";
#-----------------------------------------------------------------------#


# Run (step 3) -> Adding links to files, placing javascript at top (if frames on)
$parse_mode      = "add_links_to_files";
recursive_dir_parse($parse_starting_directory);
#-----------------------------------------------------------------------#



# Run (step 4) -> Creating Frameset and Start-Page (right side)
if ($frames_option)
  {
    create_frameset($frameset, "sub_index.htm", "start.htm");
  }
#-----------------------------------------------------------------------#


















# --------------------------------------------------------------------------------
#1                               Recursive call functions
# --------------------------------------------------------------------------------


sub recursive_dir_parse
# A recursive function to work on all directories
# It will call itself in case of a dir entry, and the function
# work_file_entry otherwise..
  {
    (my $dirname) = @_;

    # Enter directory
    push @current_path , $dirname;
    chdir($dirname);

    #Parse directory
    for (parse_dir(`ls -l`))
      {
	($name, $type) = @$_;
	if ($type eq "f") { work_file_entry($name);}
	if ($type eq "d") { recursive_dir_parse($name);}
      }

    #Leave directory
    pop @current_path;
    chdir("..");
  }




sub work_file_entry
# Restrict handling to html-files, decide what mode and prevent the
# sub_index.html from being parsed ...
  {
    (my $name) = @_;

    if ($name =~ m/.html$/)     # Limit to html files (there shouldn't be others)
      {
	if ($parse_mode eq "get_complete_sublist")
	  {
	    search_file_for_subs_place_anchor($name);
	  }
	elsif ($parse_mode eq "add_links_to_files")
	  {
	    # Skip the frames_index even thought it's a html_file
	    if ($frames_index ne ((join ("/" , @current_path)) ."/" .$name))
	      {
		print "\nworking  : ".((join (@current_path ,"/")) . $name);
		print " " x (30 - length((join (@current_path ,"/")) . $name));
		check_for_sublinks($name);
	      }
	  }
      }
  }

















# --------------------------------------------------------------------------------
#1                               Get a list of sublinks - functions
# --------------------------------------------------------------------------------

sub search_file_for_subs_place_anchor
  {
    (my $name) = @_;
    open (SOURCE_FILE, "$name") || die "Can't open $name" ;
    @source_file = <SOURCE_FILE>;
    close SOURCE_FILE;
    my $changed = 0;
    my @result_file;

    # remove the 'home' directory from the path and use it to store the sub_index.dat
    my @temp_path = @current_path;
    my $root_dir = shift @temp_path;
    my $sub_path = join "/" , @temp_path;
    my $already_parsed_warning = 0;

    open (SUB_INDEX, ">>$root_dir/sub_index.dat") ||
      die "Can't open sub index file located: $root_dir";


    while ($source_line = shift(@source_file))
      {
	##### SEARCH
	if ($source_line =~ m/(<span class="function-name">)(\w*)/)
	  {
	    $sub_name = $2;

	    #### FILTER use, require and package
	    if (($` =~ m/(<span class="keyword">use<\/span>)(\s*)$/) or
		($` =~ m/(<span class="keyword">require<\/span>)(\s*)$/) or
		($` =~ m/(<span class="keyword">package<\/span>)(\s*)$/) or
		(not $sub_name))
	      {
		#### Rescue existing anchors and warn
		if ((not $sub_name) and ($' =~ m/(<a name=")([\w]+)/))
		  {
		    print SUB_INDEX fill_up_to_space(($sub_path . "/" . $name),60) ."; $2\n";
		    $already_parsed_warning = 1;
		  }
		#### Skip keyword because of preceding use, require or package
	      }

	    #### HIT - add to sub_index and place anchor at position
	    else
	      {
		print SUB_INDEX fill_up_to_space(($sub_path . "/" . $name),60) ."; $sub_name\n";
		$source_line =~ s/(<span class="function-name">)(\w*)/$1<a name="$2">$2<\/a>/;
		$changed = 1;
	      }
	  }
	push (@result_file, $source_line);
      }
    close SUB_INDEX;

    # In case of added anchors, write new html-file
    if ($changed)
      {
	open (SOURCE_FILE, ">$name") || die "Can't open $name" ;
	print SOURCE_FILE (join "", @result_file);
	close SOURCE_FILE;
      }	

  }





# --------------------------------------------------------------------------------
#1                  Filter the list of sublinks and find doubles
# --------------------------------------------------------------------------------


sub order_sub_index
  {
    (my $filename) = @_;

    open (SUB_INDEX_UNORDERED, "<$filename");
    while (<SUB_INDEX_UNORDERED>)
      {
	$current_line = $_;
	chomp $current_line;
	(my $location, my $name) = split(";", $current_line);
	
	# Remove whitespaces..
	$location =~ s/^\s*(.*?)\s*$/$1/;
	$name     =~ s/^\s*(.*?)\s*$/$1/;

	
	# Collect for frame_index
	if ($frames_option)
	  {
	    push_hash(\%html_sub_index_source, $location, $name);
	  }


	# Filter double entries and create an own area for them
	if ($function_names{$name} or $double_functions{$name})
	  {
	    # Initialize with empty array if not existing
	    if (not $double_functions{$name})
	      {
		$double_functions{$name} = [];
	      }

	    # Add and remove old entry, in case there is one
	    if ($function_names{$name})
	      {
		push(@{$double_functions{$name}},$function_names{$name});
		delete $function_names{$name};
	      }

	    push(@{$double_functions{$name}},$location);
	  }
	else
	  {
	    $function_names{$name}=$location;
	  }

      }
    close SUB_INDEX_UNORDERED;


    # Create the HTML Index File...
    if ($frames_option)
      {
	open (HTML_SUB_INDEX, ">$frames_index");
	print HTML_SUB_INDEX '<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"><html><head></head><body>';
	foreach my $link_target (keys %html_sub_index_source)
	  {
	    $link_target =~ m/([\w\.]*\.html)/g;
	    my $pure_filename = $1;
	    sort_hash(\%html_sub_index_source, $link_target);

	    print HTML_SUB_INDEX "\n<br/><br/><br/><br/><br/><br/><br/>\n<h3><a name=\"$pure_filename\">
                                    <a href=\"$link_target\" target=\"right_frame\"> $pure_filename</a></h3>\n";

	    print HTML_SUB_INDEX "<ul>\n";
	    while ($val = shift_hash(\%html_sub_index_source, $link_target))
	      {
		print HTML_SUB_INDEX "<li><a href=\"$link_target#$val\" target=\"right_frame\">$val</a></li>\n";
	      }
	    print HTML_SUB_INDEX "</ul> \n\n";
	  }
	print HTML_SUB_INDEX "\n<br/>" x 20;	
	print HTML_SUB_INDEX "\nThe End..\n</body>";
	close HTML_SUB_INDEX;
      }


}
















# --------------------------------------------------------------------------------
#1           Search and replace functions, the heart of this file..
# --------------------------------------------------------------------------------

sub check_for_sublinks
  {
    (my $filename) = @_;
    my $file_content = read_file($filename);
    add_javascript(\$file_content, $filename);

	foreach (keys %double_functions)
	  {
	    create_multi_target_link($filename, \$file_content, $_);
	  }
	foreach (keys %function_names)
	  {
	    create_single_target_link($filename, \$file_content, $_);
	  }

    print  "... done ";
    write_file(\$file_content, $filename);
}




sub create_multi_target_link
  {
    my ($file_name, $file_content, $sub_name) = @_;


    while (${$file_content} =~ m/(\(|::|\s|amp;|')($sub_name)(?!">)([\s\n]*?)([("']+)/g)
      {
	my $i = 0;
	my $suggest = 0;
	my $choice = 0;
	my $type_of_choice = 0;
	
	$pre_match = $`;
	
	if ($1 eq "::" and $pre_match =~ m/([\w]*$)/)                # suggest package name, if existing
	  {
	    if ((join "\n", @{$double_functions{$sub_name}}) =~ m/($1\.html)/g)
	      {
		$suggest = $1;
		$type_of_choice = 'package notation';
	      }
	  }
	elsif ($default_link_target{$sub_name})	                     # try default table defined at top of this file
	  {
	    my $def_target = $default_link_target{$sub_name};
	    # check if one possible target in double_functions is the default target
	    if ((join "\n", @{$double_functions{$sub_name}}) =~ m/($def_target)(\.html)/g)
	      {
		$suggest = $1;
		$type_of_choice = 'default list';
	      }
	    else
	      {
		print "\n\nERROR: \n" .
		  "Suggested target for '$sub_name' -> " .
		    "'$def_target' is not a valid entry ".
		      "in the double_function list. Entries are :\n".
			(join "\n", @{$double_functions{$sub_name}}) . "\n It will be ignored...\n";
	      }
	  }
	elsif ((join "\n", @{$double_functions{$sub_name}}) =~ m/($file_name)/g)  # try own filename
	  {
	    $suggest = $1;
	    $type_of_choice = 'own filename';
	  }
	
	while($i <= $#{$double_functions{$sub_name}})
	  {
	    if ($suggest and ${$double_functions{$sub_name}}[$i] =~ m/$suggest/)
	      {
		$choice = $i+1; # +1 just because of the if a few lines later (0 is an array index but false)
	      }
	    $i++;
	  }
	
	if (not $choice)
	  {
	    print "\n\nWARNING, I was unable to determine a target for the multiple target sub: $sub_name";
	    print "\nNeither was there a package notation, nor a default target. And the filename itself was not";
	    print "\na possible target. So I will skip that entry, not linking it at all..";
	  }
	
	if ($choice)
	  {
	    $choice -= 1; # Array Index ...

	    my @work_path = @current_path;
	    my @target_path = split("/", ${$double_functions{$sub_name}}[$choice]);
	    my $ready_path = construct_path(@target_path);	

	    $sub_link = $ready_path . "#$sub_name";
	    $sub_link = "<blink><a href=\"$sub_link\"><span style='color:#FF1493'><b>$sub_name</b></span></a></blink>";
		
	    my $old_pos = pos(${$file_content});
	    replace_subname($file_content, $sub_name, $sub_link, $old_pos);
	    pos(${$file_content}) = $old_pos;
	  }
      }
  }






sub create_single_target_link
  {
    my ($file_name, $file_content, $sub_name) = @_;

    if ($sub_name ne "")
      {
	
    while (${$file_content} =~ m/(\(|::|\s|'|amp;)($sub_name)(?!">)([\s\n]*?)([("']+)/g)
      {
	my @target_path = split("/", $function_names{$sub_name});
	$ready_path = construct_path(@target_path);	

	my $sub_link = $ready_path . "#$sub_name";
	$sub_link = "<a href=\"$sub_link\"><span style='color:#FF1493'><b>$sub_name</b></span></a>";

	my $old_pos = pos(${$file_content});
	replace_subname($file_content, $sub_name, $sub_link, $old_pos);
	pos(${$file_content}) = $old_pos;
      }
      }
  }




sub replace_subname
  {
    my ($file_content, $sub_name, $replace_string, $posis) = @_;

    my $test_cont = substr(${$file_content},$posis - (10 + length($sub_name)),10) ;
    if ( $test_cont =~ m/p;/)
      {
	print "\n --- $test_cont";
      }


    return;


#    if (not $replace_string) { die()}
#    if (not $sub_name) { die()}

#    if (${$file_content} =~ m/(?<![>$\w].)($sub_name)(?!">)([\s\n]*?)([,\("']+)/g)
#      {

#	my $pres_pos = pos ${$file_content};
#	print "\n" . "-" x 80;

#	print "\n0) ". substr(${$file_content}, $pres_pos - length($sub_name) - 21, 20);
#	print "\n1) $1";
#	print "\n2) $2";
#	print "\n3) $3";
#	print "\n4) $4";
#	print "\n5) $5";
#	print "\n" . "-" x 80;
	
##	print "\n\n". "#" x 60 . "\n\n\n\n\nReplaced:  " . substr(${$file_content}, $pres_pos - 40, 70) . "\n\n\n\n\n" . "#" x 60;
#      }


##${$file_content} =~ s/(?<![>$\w].)($sub_name)(?!">)([\s\n]*?) /$replace_string$2$3/;




##    ${$file_content} =~ s/(?<![>$\w].)     # not some of the following before the name: > $ letter
##			  ($sub_name)      # name of the sub
##			  (?!">)           # not "> behind the name
##			  ([\s\n]*?)       # ignore newlines and whitespaces
##			  ([,\("']+)        # but there have to be one of these ( , " '
##			  /$replace_string$2$3/gsx  # replace with the word


   }






















































# --------------------------------------------------------------------------------
#1           Help functions
# --------------------------------------------------------------------------------






sub add_javascript
    # Adding the javascript header to every file..
    # This will cause the navigation on the left to jump to the right
    # position
  {
    my ($file_content, $filename) = @_;
    if ($frames_option)
      {
	  my $path_to_subindex;
	  my @work_path = @current_path;

	  shift(@work_path);
	  while ($work_path[0])
	    {
	      $path_to_subindex .= "../";
	      shift @work_path;
	    }
	  $path_to_subindex .= "$frames_index_pure_name#" . $filename;

	  $head_plus_javascript = "<head><script>setTimeout(\"top.left_frame.location = '$path_to_subindex'\", 0);</script>";
	  ${$file_content} =~ s/(<script>)([^<]*)(<\/script>)//g;
	  ${$file_content} =~ s/<head>/$head_plus_javascript/g;

	}
  }








sub check_home
  {
    shift(@_);
    $CMD_line_HOME = shift(@_);
    $CMD_line_HOME =~ s{
			^ ~             # find a leading tilde
			(               # save this in $1
			[^/]        # a non-slash character
			*     # repeated 0 or more times (0 means me)
		       )
		      }{
			$1
			  ? (getpwnam($1))[7]
			    : ( $ENV{HOME} || $ENV{LOGDIR} )
			  }ex;

    if (-e $CMD_line_HOME and -d $CMD_line_HOME)
      {
	$MMTEX_HOME = $CMD_line_HOME;
      }
    else
      {
	die("\nGiven name is not a directory: $CMD_line_HOME \n Please use option this way --HOME=foo/bar..\n");
      }
  }


sub create_frameset
  #a(filename, left_side_filename, right_side_filename)
  {
    my ($filename, $left, $right) = @_;
    open (FRAMESET , ">$frameset") || die("Could not open frameset");
print FRAMESET '<HEAD>

<FRAMESET COLS="30%,70%">
<FRAME SRC="'.$left.'" NAME="left_frame">
<FRAME SRC="'.$right.'" NAME="right_frame">
</FRAMESET>

</HEAD>';
close FRAMESET
  }


sub read_file
  {
    my $filename = shift;
    open (PLCF, $filename) || die ("Can't open $filename");
    my $file_content = join "", <PLCF>;
    close PLCF;
    return $file_content;
  }


sub write_file
  {
    my ($file_content, $filename) = @_;
    open (LFOT, ">$filename") || die ("Can't open $filename");
    print LFOT ${$file_content};
    close LFOT;
  }







##################################################################
# Array in Hash - Help-functions
#
#  push_hash(\%hash, $key, "val");
#  sort_hash(\%hash, $key       );
# shift_hash(\%hash, $key       );
#   pop_hash(\%hash, $key       );
##################################################################

sub push_hash
  {
# Adds the value to the array in the hash at the given key
    (my $target_hash, my $target_key, my $value) = @_;
    if ((not $target_key) or (not $value))
      {
	die ("There have to be a key and a value \n");
      }
    # Create empty array ref
    if (not ${$target_hash}{$target_key})
      {
	${$target_hash}{$target_key} = [];
      }
    # Add value to array
    push(@{${$target_hash}{$target_key}},$value);
  }

sub pop_hash
# Pops the value of the array in the hash at the given key
  {
    (my $target_hash, my $target_key) = @_;

    if ((not %{$target_hash}) or (not $target_key))
      {
	die ("There have to be a target_hash, and a key \n");
      }

    # Add value to array
    return pop(@{${$target_hash}{$target_key}});
  }

sub shift_hash
# Shifts the value of the array in the hash at the given key
  {
    (my $target_hash, my $target_key) = @_;

    if ((not %{$target_hash}) or (not $target_key))
      {
	die ("There have to be a target_hash, and a key \n");
      }

    # Add value to array
    return shift(@{${$target_hash}{$target_key}});
  }

sub sort_hash
# Sort the array in the hash at the given key
  {
    (my $target_hash, my $target_key) = @_;
    if ((not %{$target_hash}) or (not $target_key))
      {
	die ("There have to be a target_hash, and a key \n");
      }

    # Sort it
    @{${$target_hash}{$target_key}} =  sort @{${$target_hash}{$target_key}};
  }
##################################################################
##################################################################
##################################################################





sub construct_path
# Constructs a link to the given target path, depending on the
# current position in the directory structure.
  {
    my @target_path = @_;
    my $ready_path;
    my @work_path = @current_path;
    shift(@work_path);

    while ((exists $target_path[0] && exists $work_path[0])  && $target_path[0] eq $work_path[0])
      {
	    shift @target_path;
	    shift @work_path;
      }
	
    while (exists $work_path[0])
      {
	$ready_path .= "../";
	shift @work_path;
      }
    $ready_path .= join("/", @target_path);
    return $ready_path;
  }





















			








sub fill_up_to_space
  {
    (my $content, my $len) = @_;

    if (length($content) > $len) {return $content;}
      else
	{
	  return $content . (" " x ($len - length($content)));
	}
}










