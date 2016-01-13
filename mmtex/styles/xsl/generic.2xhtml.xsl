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
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mtx">

<xsl:import href="config.xsl"/>

<xsl:import href="struct.2xhtml.xsl"/>
<xsl:import href="box.2xhtml.xsl"/>
<xsl:import href="misc.2xhtml.xsl"/>
<xsl:import href="list.2xhtml.xsl"/>
<xsl:import href="math.2xhtml.xsl"/>
<xsl:import href="multimedia.2xhtml.xsl"/>
<xsl:import href="numbers.2xhtml.xsl"/>
<xsl:import href="preformatted.2xhtml.xsl"/>
<xsl:import href="simple_markup.2xhtml.xsl"/>
<xsl:import href="table.2xhtml.xsl"/>
<xsl:import href="hyperlink.2xhtml.xsl"/>
<xsl:import href="horizontal_float.2xhtml.xsl"/>
<xsl:import href="tree.2xhtml.xsl"/>

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"
            encoding="ASCII"/>

<xsl:strip-space elements="a h1 h2 h3 h4 h5 h6"/>

<!--
   ==========================================================================================
   Top level parameters and variables
   ==========================================================================================
-->

<!--
   The CSS stylesheets. Comma-separated list of URLs.
-->

<xsl:param name="css-stylesheets"/>

<!--
   The default CSS stylesheets. Comma-separated list of URLs.
-->

<xsl:param name="default-css-stylesheets" select="concat($css-dir, '/generic.css')"/>

<!--
   The strut image
-->

<xsl:param name="strut-image" select="concat($img-dir, '/strut.png')"/>

<!--
   The MathML namespace
-->

<xsl:param name="mathml-namespace">http://www.w3.org/1998/Math/MathML</xsl:param>

<!--
   The XHTML namespace
-->

<xsl:param name="xhtml-namespace">http://www.w3.org/1999/xhtml</xsl:param>

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
  Inserts vertical space.
-->

<xsl:template name="vertical-space">
  <xsl:param name="space" select="@space"/>
  <div>
    <xsl:attribute name="style">
      <xsl:value-of select="concat('height:',$space)"/>
    </xsl:attribute>
  </div>
</xsl:template>

<!--
   ==========================================================================================
   Styles
   ==========================================================================================
-->

<xsl:template match="/mtx:generic/mtx:styles/mtx:css-stylesheet">
  <link rel="stylesheet" type="text/css">
    <xsl:call-template name="href"/>
  </link>
</xsl:template>

<xsl:template name="css-stylesheet-link">
  <xsl:param name="href"/>
  <link rel="stylesheet" type="text/css" href="{$href}"/>
</xsl:template>

<xsl:template name="process-css-stylesheet-list">
  <xsl:param name="list"/>
  <xsl:choose>
    <xsl:when test="contains($list, ',')">
      <xsl:call-template name="css-stylesheet-link">
        <xsl:with-param name="href" select="normalize-space(substring-before($list, ','))"/>
      </xsl:call-template>
      <xsl:call-template name="process-css-stylesheet-list">
        <xsl:with-param name="list" select="substring-after($list, ',')"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="css-stylesheet-link">
        <xsl:with-param name="href" select="normalize-space($list)"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>  
</xsl:template>

<xsl:template name="styles">
  <xsl:choose>
    <xsl:when test="$css-stylesheets">
      <xsl:call-template name="process-css-stylesheet-list">
        <xsl:with-param name="list" select="$css-stylesheets"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="/generic/styles/css-stylesheet">
      <xsl:apply-templates select="/generic/styles/css-stylesheet"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="process-css-stylesheet-list">
        <xsl:with-param name="list" select="$default-css-stylesheets"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Main
   ==========================================================================================
-->

<xsl:template match="/">
  <html>
    <head>
      <xsl:copy-of select="/mtx:generic/mtx:title"/>
      <xsl:call-template name="styles"/>
    </head>
    <body>
      <a id="{$top-anchor}"/>
      <xsl:apply-templates select="/mtx:generic/mtx:document"/>
    </body>
  </html>
</xsl:template>

<xsl:template match="/mtx:generic/mtx:document">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

