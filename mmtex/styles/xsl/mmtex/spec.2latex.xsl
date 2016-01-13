<?xml version="1.0" encoding="utf-8"?>


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
                xmlns="http://www.w3.org/1999/xhtml">

<xsl:import href="../config.xsl"/>

<!-- <xsl:import href="../box.2latex.xsl"/> -->
<xsl:import href="../general.2latex.xsl"/>
<!-- <xsl:import href="../list.2latex.xsl"/> -->
<!-- <xsl:import href="../math.2latex.xsl"/> -->
<!-- <xsl:import href="../multimedia.2latex.xsl"/> -->
<!-- <xsl:import href="../numbers.2latex.xsl"/> -->
<!-- <xsl:import href="../preformatted.2latex.xsl"/> -->
<xsl:import href="../struct.2latex.xsl"/>
<xsl:import href="../simple_markup.2latex.xsl"/>
<!-- <xsl:import href="../table.2latex.xsl"/> -->
<!-- <xsl:import href="../hyperlink.2latex.xsl"/> -->
<!-- <xsl:import href="../horizontal_float.2latex.xsl"/> -->

<xsl:output method="text"/>


<!--
   ==========================================================================================
   Top level parameters and variables
   ==========================================================================================
-->

<!--
   The language the source is written in.
-->

<xsl:param name="language" select="'de'"/>

<!--
   The MathML namespace
-->

<xsl:param name="mathml-namespace">http://www.w3.org/1998/Math/MathML</xsl:param>

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
   Lists
   ==========================================================================================
-->

<xsl:template name="numbered-list">
  <xsl:param name="prefix"/>
  <xsl:text>&par;\begin{itemize}&br;</xsl:text>
    <xsl:for-each select="item">
      <xsl:variable name="full-number" select="concat($prefix,'.',@no)"/>
      <xsl:text>\item[</xsl:text>
      <xsl:choose>
        <xsl:when test="label">
          <xsl:apply-templates select="label"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$full-number"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>]&br;</xsl:text>
      <xsl:for-each select="content/*">
        <xsl:choose>
          <xsl:when test="local-name()='list' and @numbering='yes'">
            <xsl:call-template name="numbered-list">
              <xsl:with-param name="prefix" select="$full-number"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:for-each>
  <xsl:text>\end{itemize}&par;</xsl:text>
</xsl:template>

<xsl:template match="section/list[@numbering='yes']">
  <xsl:call-template name="numbered-list">
    <xsl:with-param name="prefix" select="../@no"/>
  </xsl:call-template>
</xsl:template>

<xsl:template match="description-list">
  <xsl:text>&par;\begin{description}&br;</xsl:text>
    <xsl:apply-templates select="@*[name()!='numbering']"/>
    <xsl:apply-templates/>
  <xsl:text>\end{description}&par;</xsl:text>
</xsl:template>

<xsl:template match="description-list/item">
  <xsl:apply-templates select="label"/>    
  <xsl:apply-templates select="content"/>
</xsl:template>

<xsl:template match="description-list/item/label">
  <xsl:text>\item[</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>]&br;</xsl:text>
</xsl:template>

<xsl:template match="description-list/item/content">
  <xsl:apply-templates/>
</xsl:template>

<!--
   ==========================================================================================
   Main
   ==========================================================================================
-->

<xsl:template match="/">
<xsl:text>\documentclass[11pt,a4paper]{article}
\usepackage{ngerman}
\parindent0em
\parskip1.00\baselineskip
\textwidth16cm
\textheight24cm
\headheight0cm
\oddsidemargin0pt
\evensidemargin0pt
\topmargin0cm
\headsep0cm

\begin{document}
</xsl:text>
<xsl:apply-templates select="/spec/document"/>
<xsl:text>\end{document}
</xsl:text>
</xsl:template>

<xsl:template match="/spec/document">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

