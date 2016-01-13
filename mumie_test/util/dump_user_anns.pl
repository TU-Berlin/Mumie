#!/usr/bin/perl

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

#-w

my $dbname = "mdb01";
my $dbuser = "japs";
my $dump_file = "anns_dump.txt";
my $modus = "";
my $omit_ann_type = "";


my @tables = ("users", "sections");
my %users = ();
my %sections = ();
my %refs = ();
my %worksheets = ();
my %gproblems = ();

if ( $ARGV[0] eq "--help" )
{
  help();
  exit();
}
elsif ( $ARGV[0] ne "" )
{
  $ARGV[0] =~ m/--(.*)/;
  $modus = $1;
}
else
{
  die "Please use --help to see right usage of this script!\n";
}

if ( $modus eq "online" )
{
  if( $ARGV[1] ne "" )
  {
    
    ($param, $arg) = split('=', $ARGV[1]);
    if ( $arg eq "0" or $arg eq "1" or $arg eq "2" )
    {
      $omit_ann_type = $arg;
    }
  }
}

sub performDbQuery
{
  $cmd = $_[0];
  $query = "psql -U $dbuser -d $dbname -c \"$cmd\"";
  return `$query`;
}

sub getSectionPathForId
{
  $id = $_[0];
  $dbRes = performDbQuery("SELECT path_for_section_id($id)");
  $dbRes =~ m/.*?-*-(.*)\(.*/s;

  return trim($1);
}

sub getUsersFromDb
{
  $plain_users = performDbQuery("SELECT id, pure_name FROM users;");

  while ( $plain_users =~ m/^.*?(\d.*?)\|(.*)/mig )
  {
    $id = trim($1);
    $pure_name = trim($2);
    if ( $modus eq "offline" )
    {
      $users{$id} = $pure_name;
    }
    elsif ( $modus eq "online" )
    {
      $users{$pure_name} = $id;
    }
  }
}

sub getRefsWorksheetGenericProblemFromDb
{
  $plain_refs = performDbQuery("SELECT id, from_doc, to_doc FROM refs_worksheet_generic_problem;");

  while ( $plain_refs =~ m/^.*?(\d.*?)\|(.*)\|(.*)/mig )
  {
    $id = trim($1);
    $from_doc = trim($2);
    $to_doc = trim($3);
    if ( $modus eq "offline" )
    {
      $refs{$id} = [$from_doc, $to_doc];
    }
    elsif ( $modus eq "online" )
    {
      $refs{$from_doc.':'.$to_doc} = $id;
    }
  }
}

sub getWorksheetsFromDb
{
  $plain_worksheets = performDbQuery("SELECT id, pure_name, contained_in FROM worksheets;");

  while ( $plain_worksheets =~ m/^.*?(\d.*?)\|(.*)\|(.*)/mig )
  {
    $id = trim($1);
    $pure_name = trim($2);
    $contained_in = trim($3);
    $worksheets{$id} = [$pure_name, $contained_in];
  }
}

sub getGenericProblemsFromDb
{
  $plain_gproblems = performDbQuery("SELECT id, pure_name, contained_in FROM generic_problems;");

  while ( $plain_gproblems =~ m/^.*?(\d.*?)\|(.*)\|(.*)/mig )
  {
    $id = trim($1);
    $pure_name = trim($2);
    $contained_in = trim($3);
    $gproblems{$id} = [$pure_name, $contained_in];
  }
}

sub getAnnsFromDb
{
  print "Creating dump from DB...";
  $anns = `pg_dump -ia -F p -t anns_user_worksheet_generic_problem -d $dbname`;
  open(FH, ">$dump_file") || die "Writing file failed!".$_."\n";
  print FH $anns;
  close(FH);
  print "Done.\n";
}

sub updateAnnsToOffline
{
  my $anns = "";
  open(FH, "<$dump_file") || die "Required file not found!".$_."\n";
  $anns = join('', <FH>);
  close(FH);

  my $offline_sql = "";

  print "Processing dump from DB to offline modus...";
  $i = 0;
  while ($anns =~ m/(INSERT INTO anns_user_worksheet_generic_problem VALUES \()(.*?),(.*?),(.*?<\/data_sheet>'),(.*?),(.*?),(.*?),(.*?;)/mgs)
  {
    $the_user = trim($2);
    $ref = trim($3);
    $ann_type = trim($5);
    $doc1 = trim($6);
    $doc2 = trim($7); 

    if ( $users{$the_user} ne "" )
    {
      $i++;
      $worksheet = getSectionPathForId($worksheets{$refs{$ref}[0]}[1]).'/'.$worksheets{$refs{$ref}[0]}[0];
      $gproblem = getSectionPathForId($gproblems{$refs{$ref}[1]}[1]).'/'.$gproblems{$refs{$ref}[1]}[0];

      $offline_sql .= $1.$users{$the_user}.', '.$worksheet.':'.$gproblem.','.$4.', '.$ann_type.', '.$worksheet.', '.$gproblem.','.$8."\n";
    }
  }

  open(FH, ">$dump_file") || die "Datei konnte nicht geoeffnet werden!".$_."\n";
  print FH $offline_sql;
  close(FH);

  print "Done.\n";
}

sub updateAnnsToOnline
{
  my $anns = "";
  open(FH, "<$dump_file") || die "Required file not found!".$_."\n";
  $anns = join('', <FH>);
  close(FH);

  my $offline_sql = "
SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;
SET search_path = public, pg_catalog;

";

  print "Processing offline dump to online modus...";
  $i = 0;
  while ($anns =~ m/(INSERT INTO anns_user_worksheet_generic_problem VALUES \()(.*?),(.*?),(.*?<\/data_sheet>'),(.*?),(.*?),(.*?),(.*?;)/mgs)
  {
    $insert = $1;
    $the_user = trim($2);
    $ref = trim($3);
    $content = $4;
    $ann_type = trim($5);
    $doc1 = trim($6);
    $doc2 = trim($7);
    $last = $8;

    if ( $omit_ann_type ne "" and $omit_ann_type eq $ann_type )
    {
      #do nothing;
    }
    else
    {
      if ( $users{$the_user} ne "" )
      {
        $i++;
        ($plain_worksheet, $plain_gproblem) = split(':', $ref);
        
        $plain_worksheet =~ m/(.*)\/(.*)/;
        $worksheet_path = $1;
        $worksheet_name = $2;

        $plain_gproblem =~ m/(.*)\/(.*)/;
        $gproblem_path = $1;
        $gproblem_name = $2;

        $dbRes = performDbQuery("SELECT id FROM worksheets WHERE pure_name = \'$worksheet_name\' AND contained_in = section_id_for_path(0, \'$worksheet_path\');");
        $dbRes =~ m/.*?-*-(.*)\(.*/s;
        $worksheet_id = trim($1);

        $dbRes = performDbQuery("SELECT id FROM generic_problems WHERE pure_name = \'$gproblem_name\' AND contained_in = section_id_for_path(0, \'$gproblem_path\');");
        $dbRes =~ m/.*?-*-(.*)\(.*/s;
        $gproblem_id = trim($1);

        $ref_id = $refs{$worksheet_id.':'.$gproblem_id};

        $offline_sql .= $insert.$users{$the_user}.', '.$ref_id.','.$content.', '.$ann_type.', '.$worksheet_id.', '.$gproblem_id.','.$last."\n";
      }
    }
  }
  open(FH, ">$dump_file") || die "Required file not found!".$_."\n";
  print FH $offline_sql;
  close(FH);

  $res = `psql -U $dbuser -d $dbname -f anns_dump.txt`;

  print "Done.\n";
}

sub trim
{
  my $str = $_[0];
  if ( $str ne "" )
  {
    $str =~ s/^\s+|\s+$//g;
    return $str;
  }
}

sub help
{
  print "\n\nUsage:\n  dump_user_anns.pl --offline|--online [OPTIONS]\n\n";
  print "Required option:
  --offline     Create offline dump of table anns_user_worksheet_generic_problem
  --online      Create insert script of the offline dump and perform an INSERT into the db\n";
  print "\nAdditional options:
  -ann_type=NUM Only applicable with --online. Indicates which ann_type should be omitted while
                inserting data into db.
                NUM could be 0,1 or 2. Default is nothing, so all data would be inserted.\n";
  print "\nOther options:
  --help        Print this message and quit\n";
}

sub main
{
  if ( $modus eq "offline" )
  {
    print "Processing $modus started.\n";
      getAnnsFromDb();
    print "Processing nesseccary informations...";
      getUsersFromDb();
      getRefsWorksheetGenericProblemFromDb();
      getWorksheetsFromDb();
      getGenericProblemsFromDb();
    print "Done.\n";
      updateAnnsToOffline();
    print "Finished.\n";
  }
  if ( $modus eq "online" )
  {
    print "Processing $modus started.\n";
    print "Processing nesseccary informations...";
      getUsersFromDb();
      getRefsWorksheetGenericProblemFromDb();
      getWorksheetsFromDb();
      getGenericProblemsFromDb();
    print "Done.\n";
      updateAnnsToOnline();
    print "Finished.\n";
  }
}

main();
