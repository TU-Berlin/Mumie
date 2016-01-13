<?xml version="1.0" encoding="ASCII"?>

<!DOCTYPE stylesheet
  [
   <!ENTITY br  "&#xA;">
   <!ENTITY sp  "  ">
  ]
>


<!--
  The MIT License (MIT)
  
  Copyright (c) 2010 Technische Universitaet Berlin
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex">

<xsl:import href="struct.2latex.xsl"/>
<xsl:import href="list.2latex.xsl"/>
<xsl:import href="preformatted.2latex.xsl"/>
<xsl:import href="simple_markup.2latex.xsl"/>
<xsl:import href="hyperlink.2latex.xsl"/>
<xsl:import href="table.2latex.xsl"/>
<xsl:import href="/usr/local/lib/xsltml/mmltex.xsl"/>

<xsl:output method="text" encoding="ASCII"/>

<xsl:strip-space elements="*"/>

<!--
   ==========================================================================================
   Top level parameters and variables
   ==========================================================================================
-->

<!--
   The language the source is written in.
-->

<xsl:param name="language" select="'en'"/>

<!--
   Prefix to prepend to URI's in references. - Currently unused.
-->

<xsl:param name="uri-prefix"/>

<!--
   Suffix to append to referenced filenames.
-->

<xsl:param name="output-suffix" select="'xhtml'"/>

<!--
   ==========================================================================================
   Auxiliary templates
   ==========================================================================================
-->

<!--
   Outputs an error message and terminates transformation. The message is passed as the
   parameter $message.
-->

<xsl:template name="error">
  <xsl:param name="message"/>
  <xsl:message terminate="yes">
    <xsl:text>Error:&br;&br;&sp;</xsl:text>
    <xsl:value-of select="$message"/>
    <xsl:text>&br;</xsl:text>
  </xsl:message>
</xsl:template>

<!--
   ==========================================================================================
   Main
   ==========================================================================================
-->

<xsl:template match="/">
  <xsl:text>\documentclass{mtxprint}&br;</xsl:text>
  <xsl:text>&br;\begin{document}&br;</xsl:text>
  <xsl:apply-templates select="/mtx:generic/mtx:document"/>
  <xsl:text>&br;\end{document}&br;</xsl:text>
</xsl:template>

<xsl:template match="/mtx:generic/mtx:document">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

