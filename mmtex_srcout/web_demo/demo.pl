#!/usr/site-local/bin/perl -w

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

# -w => ausfuehrlichere Warnungen/Fehlermeldungen
# -T => Sicherheitseinstellung: Tainted Mode

# Script zur Web-Demonstration des mmtex-Konverters
#Author: Helmut Vieritz 11/2002
#email: vieritz@math.tu-berlin.de

# Folgende ToDos sind noch zu leisten:
# - Das Script kann momentan nicht von mehreren Clienten gleichzeitig aufgerufen werden,
# da es unkontrolliert immer die gleichen Dateien zum Speichern und Einlesen fuer den Konverter
# und die XSL Transformation verwendet. Moeglicher Ansatzpunkt ist den Konverter und Transformer
# nur noch ueber stdin stdout und stderr anzusprechen. Dazu sind Aenderungen am Konverter und
# Transformer noetig. Quick & Dirty: der Apache beggrenzt die Anzahl gleichzeitiger Zugriffe
#  auf 1. Die Vergabe von der Prozess-ID abhaengiger Dateinamen waere ebenso eine moegliche
# Loesung, wuerde jedoch Schreibrechte im Verzeichnis erfordern statt Schreibrechte fuer eine Datei
# - die Sicherheitsbelange des Scripts insgesamt sind mir noch reichlich unklar. Ich gehe davon aus, dass
# relevante Sicherheitsluecken existieren. Eine waere, dass das mmtex-Verzeichnis Schreibrechte
# fuer die ganze Welt hat, da ich mit dem Apache-Prozess keine Gruppe teile und das Script Dateien
# anlegen koennen muss. Loesung: Verwendung von SuExec fuer den Apache oder obige bereits angesprochene.
# - perl mit -T Parameter ansprechen. (geht evtl. gar nicht wegen externem mmtex und mmxsl)
# - automatisierte Erstellung des Beispielmenues

require 5.004;

# Perl-Pragma um u.a. Variablendeklaration zu erzwingen
# Insbesondere in Hinblick auf Apache-httpd mit mod_perl
# zu empfehlen.
use strict;
# Fehlermeldungen werden dem Browser mitgeteilt, sollte nach Fertigstellung wieder entfernt werden
use CGI::Carp qw(fatalsToBrowser);

# open (LOG,'>demo.log');
# Variablendeklaration
my ($daten, @formulardaten, $formfeld, $name, $wert, %feld);
my ($MMTEX_PATH,$AREAHEIGHT,$AREAWIDTH,$file,%samples,$line,$warn,$anker,$res,$res1,$printwarn);

# Liste der Dateinamen der Beispiele (*.tex Dateien)
# Muss zusaetzlich unten im Formularfeld eingetragen werden.
%samples = (
	'1' => 'welcome',
	'2' => 'scratch',
);
# width and height of the text fields
$AREAWIDTH = 90;
$AREAHEIGHT = 12;

sub selectDefaultTex
{
	$feld{'s'} = '1';
	$file = $samples{$feld{'s'}}.'.tex';
	open (FILE, "<$file") or die "Cannot open LaTeX file for reading: $!\n";
	$feld{'tx'} = '';
	while (defined($line = <FILE>)){$feld{'tx'} .= $line;}
	$warn = "<p class=\"warn\">You've forgotten to type in or select some L<sp>A</sp>T<sb>E</sb>X code.
	Therefore I have chosen a sample for you.</p>\n";
return $warn;
} # selectDefaultTex

sub echoWarnArea
{
my $em = $_[0];
print <<"EndHTML";
<table class="bor" id="warn1" width="10%">
<tr><td><p class="capital">Something went wrong. I hope the message can help you:</p></td></tr>
<tr><td><textarea name="em" rows="6" cols="$AREAWIDTH" readonly="readonly">$em</textarea></td></tr>
</table>
EndHTML
return 0;
} # echoWarnArea

# formular used POST-method
$ENV{'CONTENT_LENGTH'}< 500000 or die "POST variables size is bigger then 500KByte! ";
read(STDIN, $daten, $ENV{'CONTENT_LENGTH'} );
# splitting of formular datas into separated fields
@formulardaten = split(/&/, $daten);
# Jedes Formularfeld einzeln bearbeiten und Ergebnis
# im Hash $feld ablegen.
foreach $formfeld (@formulardaten)
{
	# Aufspaltung eines Felds in Feldname und -wert
	($name, $wert) = split(/=/, $formfeld);
	# MIME-Kodierung der Leerzeichen aufheben
	$wert =~ tr/+/ /;
	# MIME-Kodierung von Sonderzeichen aufheben<
	$wert =~ s/%([a-fA-F0-9].)/pack("C", hex($1))/eg;
	# XSSI-Anweisungen entfernen!  Sicherheit!!!
	$wert =~ s/<!--(.|\n)*-->//g;
	# Neue Hashzeile mit Feldname und gefiltertem Feldwert
	$feld{$name}=$wert;
}

# nicht gesetzte Variablen auf definierte Werte setzen
$feld{'sb1'}  ||= '';
$feld{'sb2'}  ||= '';
$feld{'sb3'}  ||= '';
$feld{'sb4'}  ||= '';
$feld{'sb5'}  ||= '';
$feld{'sb6'}  ||= '';
$feld{'tx'}   ||= '';
$feld{'xml'}  ||= '';
$feld{'xhtml'}||= '';
$feld{'s'}    ||= '';
$warn = '';
$anker = '';
$printwarn ||= '';

#Beginn der Formularauswertung
if ($feld{'sb5'} eq 'Select')
{
	if ($samples{$feld{'s'}})
	{
		$file = $samples{$feld{'s'}}.'.tex';
		open (FILE, "<$file") or die "Cannot open LaTeX file for reading: $!\n";
		$feld{'tx'} = '';
		while (defined($line = <FILE>)){$feld{'tx'} .= $line;}
	} else
	{
		$feld{'tx'} = '';
	}
}

if ($feld{'sb1'} eq 'Submit')
{
	selectDefaultTex() if ($feld{'tx'} eq '');
	$feld{'tx'} =~ s/\r//g;
	open (FILE, "> xxx.tex") or die "Cannot open LaTeX file for writing: $!\n";
	print FILE $feld{'tx'}."\n";
	close FILE;
	# print LOG 'xxx.stderr wird geloescht...';
	system('rm xxx.stderr');
	# print LOG "okay\n";
	# print LOG 'MMTEX wird aufgrufen...';
	$res = system("bash -c '../htdocs/mmtex/bin/mmtex xxx.tex 2> xxx.stderr'");
	# ($res == 0) or die "Cannot execute command (Line:$.): $! \n";
	# print LOG "okay\n";	
	if ($res == 0)
	{
		open (FILE, "<xxx.xml") or die "Cannot open XML file for reading: $!\n";
		$feld{'xml'} = '';
		while (defined($line = <FILE>)){$feld{'xml'} .= $line;}
		$anker = 'xml';
	} else
	{
		open (FILE, "<xxx.stderr") or die "Cannot open xxx.stderr file for reading: $!\n";
		$feld{'xml'} = 'Exit status: '.$res."\n";
		while (defined($line = <FILE>)){$feld{'xml'} .= $line;}
		$feld{'sb1'} = '';
		$feld{'sb2'} = '';
		$printwarn = '1';
	}
}

if ($feld{'sb2'} eq 'XHTML')
{
	selectDefaultTex() if ($feld{'tx'} eq '');
	$feld{'tx'} =~ s/\r//g;
	open (FILE, "> xxx.tex") or die "Cannot open LaTeX file for writing: $!\n";
	print FILE $feld{'tx'}."\n";
	close FILE;
	# print LOG 'xxx.stderr wird geloescht...';
	system('rm xxx.stderr');
	# print LOG "okay\n";
	# print LOG 'MMTEX wird aufgrufen...';
	$res = system("bash -c '../htdocs/mmtex/bin/mmtex xxx.tex 2> xxx.stderr'");
	# print LOG "okay\n";
	if ($res != 0)
	{
	 	open (FILE, "<xxx.stderr") or die "Cannot open xxx.stderr file for reading: $!\n";
		$feld{'xml'} = 'Exit status: '.$res."\n";
		while (defined($line = <FILE>)){$feld{'xml'} .= $line;}
		$feld{'sb1'} = '';
		$feld{'sb2'} = '';
		$printwarn = '1';
	} else
	{
	  # print LOG 'xxx.stderr wird geloescht...';
	  system('rm xxx.stderr');
	  # print LOG "okay\n";
	  # print LOG 'MMXSL wird aufgrufen...';
	  $res1 = system("bash -c '../htdocs/mmtex/bin/mmxsl xxx.xml --output=../htdocs/xxx.xhtml 2> xxx.stderr'");
	  # print LOG "okay\n";
	  if ($res1 == 0)
	  {
	    open (FILE, "<../htdocs/xxx.xhtml") or die "Cannot open XHTML file for reading: $!\n";
	    $feld{'xhtml'} = '';
	    while (defined($line = <FILE>)){$feld{'xhtml'} .= $line;}
	    $anker = 'xhtml';
	  } else
	  {
	    open (FILE, "<xxx.stderr") or die "Cannot open xxx.stderr file for reading: $!\n";
	    $feld{'xml'} = 'Exit status: '.$res."\n";
	    while (defined($line = <FILE>)){$feld{'xml'} .= $line;}
	    $feld{'sb1'} = '';
	    $feld{'sb2'} = '';
	    $feld{'sb3'} = '';
	    $printwarn = '1';
	  }
	}
}

if ($feld{'sb3'} eq 'Submit')
{
	if ($feld{'xml'} eq '') {die "No XML code exists: $!\n";}
	else
	{
	  # print LOG 'xxx.stderr wird geloescht...';
	  system('rm xxx.stderr');
	  # print LOG "okay\n";
	  # print LOG 'MMXSL wird aufgrufen...';
	  $res1 = system("bash -c '../htdocs/mmtex/bin/mmxsl xxx.xml --output=../htdocs/xxx.xhtml  2> xxx.stderr'");
	  # print LOG "okay\n";
	  if ($res1 == 0)
	  {
	    open (FILE, "<../htdocs/xxx.xhtml") or die "Cannot open XHTML file for reading: $!\n";
	    $feld{'xhtml'} = '';
	    while (defined($line = <FILE>)){$feld{'xhtml'} .= $line;}
	    $anker = 'xhtml';
	  } else
	  {
	    open (FILE, "<xxx.stderr") or die "Cannot open xxx.stderr file for reading: $!\n";
	    $feld{'xml'} = 'Exit status: '.$res."\n";
	    while (defined($line = <FILE>)){$feld{'xml'} .= $line;}
	    $feld{'sb1'} = '';
	    $feld{'sb2'} = '';
	    $feld{'sb3'} = '';
	    $printwarn = '1';
	  }
	}
}

if ($feld{'sb6'} eq 'New')
{
  $feld{'sb1'}   = '';
  $feld{'sb2'}   = '';
  $feld{'sb3'}   = '';
  $feld{'sb4'}   = '';
  $feld{'sb5'}   = '';
  $feld{'sb6'}   = '';
  $feld{'tx'}    = '';
  $feld{'s'}     = '0';
  $feld{'xml'}   = '';
  $feld{'xhtml'} = '';
  $warn          = '';
  $anker         = '';
}

#Ab hier erfolgt der Aufbau der HTML-Seite
print "Content-type: text/html\n\n";
print <<"EndHTML";
<html>
<head>
<title>MUMIE: mmtex Demo</title>
<style rel="stylesheet" type="text/css">
	a:link {color: #800000;}
	a:active {}
	a:hover {color:#000080; text-decoration:none; font-weight:bold;}
	a:visited {color: #800000;}
	a:focus {}
	body {background-color: #AACCFF;}
	h2 {text-align: center;}
	p.capital {font-weight: bold; background-color: black; color: white; padding: 2px;}
	input {background-color: navy; color: white;}
	table.bor {border: black 1px solid; padding: 5px; margin: 5px;}
	#bor1 {background-color:#FF2222;}
	#bor2 {background-color:gold;}
	#bor3 {background-color:#00CC00; height: 90%;}
	#warn1 {background: orange url(../warn.png);}
	table.bor td {vertical-align: top;}
	table.bor td.dc {vertical-align: bottom; text-align: center;}
	table.bor td.tc {text-align: center;}
	table.bor td.tr {text-align: right;}
	.ifr {width: 100%; height: 400px; background-color: white;}
	.warn {color: maroon; font-weight: bold;}
	sb {vertical-align: bottom;font-size: smaller;}
	sp {vertical-align: text-top; font-size: smaller;}
</style>
</head>
EndHTML
if ($anker ne '') {print "<body onload='javascript: location.href=\"#$anker\"'>";}
else {print '<body>';}
print <<"EndHTML";
<h2>mmtex tool</h2>
<h3>Online Demo</h3>
<form method="post" action="http://falbala.math.tu-berlin.de:8080/cgi-bin/demo.pl">
<p><a name="latex">Y</a>ou can evaluate mmtex by using this site. Select a L<sp>A</sp>T<sb>E</sb>X script demo or
type your own L<sp>A</sp>T<sb>E</sb>X code in the field below. Then submit to see the XML result.</p>
<table class="bor" id="bor1" width="10%">
<tr>
<td><p>Type your own code:</p></td>
<td class="tr">select a demo script here:&nbsp;&nbsp;
<select name="s">
EndHTML
	print "<option value=\"0\" ";
	if ($feld{'s'} eq '0') {print "selected=\"selected\"";}
	print ">none</option>";
	print "<option value=\"1\" ";
	if ($feld{'s'} eq '1') {print "selected=\"selected\"";}
	print ">welcome</option>";
	print "<option value=\"2\" ";
	if ($feld{'s'} eq '2') {print "selected=\"selected\"";}
	print ">scratch</option>";
print <<"EndHTML";
</select>
<input type="submit" name="sb5" value="Select" />
</td>
</tr>
<tr>
EndHTML

if ($feld{'sb1'} eq 'Submit' || $feld{'sb2'} eq 'XHTML' || $feld{'sb3'} eq 'Submit')
{print '<td colspan="2"><textarea id="tx" name="tx" rows="12" cols="90" readonly="readonly">';}
else {print "<td colspan=\"2\"><textarea id=\"tx\" name=\"tx\" rows=\"$AREAHEIGHT\" cols=\"$AREAWIDTH\">";}

print <<"EndHTML";
$feld{'tx'}</textarea></td>
</tr>
<tr>
<td>
<!-- May be you prefer MS Windows-like systems and<br/>you aren't interested in seeing the details.<br/> -->
You can see the XHTML result immediately by clicking the XHTML button:&nbsp;&nbsp;
<input type="submit" name="sb2" value="XHTML" /></td>
<td class="tr">
<input type="submit" name="sb1" value="Submit" />
<input type="reset" name="rs" value="Reset" />
<input type="submit" name="sb6" value="New" />
</td></tr>
</table>
EndHTML
print $warn if ($warn ne '');
echoWarnArea($feld{'xml'}) if $printwarn eq '1';
if ($feld{'sb1'} eq "Submit" || $feld{'sb3'} eq "Submit")
{
print <<"EndHTML";
<p>Next you can see the XML file generated by mmtex. XML datas are suitable to store complex
informations for easy handling. Broadly spoken the content is separated from the layout because
with XML we can store the science information and with XSLT we handle the appearance. Click on the submit
button below to see the XHTML result created from the XML file using XSLT with Xalan (a XSLT processor from apache.org).</p>
<table class="bor" id="bor2" width="10%">
<tr><td>
<p>You can see the XML result here:</p>
<textarea id="tx" name="xml" rows="$AREAHEIGHT" cols="$AREAWIDTH" readonly="readonly">$feld{'xml'}</textarea>
</td></tr>
<tr><td class="tr">
<p><a name="xml">a</a>nd you can create the XHTML result here:&nbsp;&nbsp;
<input type="submit" name="sb3" value="Submit" /></p>
</td></tr>
</table>
EndHTML
}

if ($feld{'sb2'} eq "XHTML" || $feld{'sb3'} eq "Submit")
{
print <<"EndHTML";
<p>Last but not least you see the XHTML output of the XSL transformation. On one hand there is the XHTML code
but this isn't made for human eyes. Therefore (if your browser displays MathML and iframes like Mozilla) you see
the graphical result of the complete transformation at the bottom.</p>
<table class="bor" id="bor3" width="10%">
<tr><td>
<textarea id="tx" name="xhtml" rows="$AREAHEIGHT" cols="$AREAWIDTH" readonly="readonly">$feld{'xhtml'}</textarea>
</td></tr>
<tr><td><iframe src="../xxx.xhtml" class="ifr">
<p>Sorry, your Browser doesn't support so called iframes. You should use either the MS Internet Explorer
with a plug-in for MathML or Browsers attaching the Mozilla engine Gecko.</p>
</iframe></td></tr>
<tr><td class="tr">
<p><a name="xhtml">C</a>reate a new example:&nbsp;&nbsp;
<input type="submit" name="sb4" value="Transform new example" />
</p>
</td></tr>
</table>
EndHTML
}

print "
</form>
</body>
</html>
";
close LOG;


