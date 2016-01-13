<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE stylesheet
  [
   <!ENTITY br  "&#xA;">
   <!ENTITY sp  "  ">
   <!ENTITY par "&#xA;&#xA;">
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
                xmlns:x-latex="xalan://LaTeXUtil"
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex">

<!--
  Table of contents headline
-->

<xsl:param name="toc-title">
  <xsl:choose>
    <xsl:when test="$language='en'">Contents</xsl:when>
    <xsl:when test="$language='de'">Inhalt</xsl:when>
    <xsl:otherwise>Contents</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<!--
  Title and subtitle
-->

<xsl:template match="/*/mtx:document/mtx:title">
  <xsl:text>&br;\begin{titleAndSubtitle}&br;</xsl:text>
  <xsl:text>&sp;\title{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}&br;</xsl:text>
  <xsl:if test="/*/mtx:document/mtx:subtitle">
    <xsl:text>&sp;\subtitle{</xsl:text>
    <xsl:apply-templates select="/*/mtx:document/mtx:subtitle" mode="title"/>
    <xsl:text>}&br;</xsl:text>
  </xsl:if>
  <xsl:text>\end{titleAndSubtitle}&br;</xsl:text>
</xsl:template>

<xsl:template match="/*/mtx:document/mtx:subtitle" mode="title">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="/*/mtx:document/mtx:subtitle">
  <!-- Ignore in normal mode -->
</xsl:template>

<!--
  Authors
-->

<xsl:template match="mtx:authors">
  <xsl:text>&br;\begin{authors}&br;</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>\end{authors}&br;</xsl:text>
</xsl:template>

<xsl:template match="mtx:author">
  <xsl:choose>
    <xsl:when test="@email">
      <xsl:text>&sp;\authorWithEmail{</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>}{</xsl:text>
      <xsl:value-of select="x-latex:processText(@email)"/>
      <xsl:text>}&br;</xsl:text>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>&sp;\author{</xsl:text>
      <xsl:apply-templates/>
      <xsl:text>}&br;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
  Version
-->

<xsl:template match="mtx:version">
  <xsl:text>&br;\version{</xsl:text>
  <xsl:variable name="version" select="normalize-space()"/>
  <xsl:choose>
    <xsl:when test="starts-with($version, '$Id')">
      <xsl:text>\texttt{</xsl:text>
      <xsl:value-of select="x-latex:processText($version)"/>
      <xsl:text>}</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="x-latex:processText($version)"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
  Table of contents
-->

<xsl:template match="mtx:toc">
  <xsl:text>&br;\tocHeadline{</xsl:text>
  <xsl:value-of select="x-latex:processText($toc-title)"/>
  <xsl:text>}&br;</xsl:text>
  <xsl:text>&br;\begin{tocItems}&br;</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>\end{tocItems}&br;</xsl:text>
</xsl:template>

<xsl:template match="mtx:toc-item[@class='section']">
  <xsl:text>&sp;\item[</xsl:text>
  <xsl:value-of select="@no"/>
  <xsl:text>] </xsl:text>
  <xsl:apply-templates select="mtx:toc-entry"/>
  <xsl:text>\dotfill \pageref{</xsl:text>
  <xsl:value-of select="substring-after(@href,'mmtex:#')"/>
  <xsl:text>}&br;</xsl:text>
  <xsl:if test="mtx:toc-item">
    <xsl:text>&sp;\begin{tocItems}&br;</xsl:text>
    <xsl:apply-templates select="mtx:toc-item"/>
    <xsl:text>&sp;\end{tocItems}&br;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="mtx:toc-item[@class='subsection']">
  <xsl:text>&sp;&sp;\item[</xsl:text>
  <xsl:value-of select="concat(../@no, '.', @no)"/>
  <xsl:text>] </xsl:text>
  <xsl:apply-templates select="mtx:toc-entry"/>
  <xsl:text>\dotfill \pageref{</xsl:text>
  <xsl:value-of select="substring-after(@href,'mmtex:#')"/>
  <xsl:text>}&br;</xsl:text>
  <xsl:if test="mtx:toc-item">
    <xsl:text>&sp;&sp;\begin{tocItems}&br;</xsl:text>
    <xsl:apply-templates select="mtx:toc-item"/>
    <xsl:text>&sp;&sp;\end{tocItems}&br;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="mtx:toc-item[@class='subsubsection']">
  <xsl:text>&sp;&sp;&sp;\tocItem{</xsl:text>
  <xsl:value-of select="concat(../../@no, '.', ../@no, '.', @no)"/>
  <xsl:text>}{</xsl:text>
  <xsl:apply-templates select="mtx:toc-entry"/>
  <xsl:text>}{</xsl:text>
  <xsl:value-of select="substring-after(@href,'mmtex:#')"/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
  Paragraphs
-->

<xsl:template match="mtx:par">
  <xsl:text>&par;</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>&par;</xsl:text>
</xsl:template>

<xsl:template match="text()">
  <xsl:value-of select="x-latex:processText(string(.))"/>
</xsl:template>

<!--
  Structuring units (section, subsection, subsubsection)
-->

<xsl:template match="mtx:section/mtx:headline">
  <xsl:choose>
    <xsl:when test="../@no">
      <xsl:text>&br;\secNumAndHeadline{</xsl:text>
      <xsl:value-of select="../@no"/>
      <xsl:text>}{</xsl:text>
      <xsl:value-of select="x-latex:processText(string(.))"/>
      <xsl:text>}\label{</xsl:text>
      <xsl:value-of select="../@id"/>
      <xsl:text>}&br;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>&br;\secHeadline{</xsl:text>
      <xsl:value-of select="x-latex:processText(string(.))"/>
      <xsl:text>}&br;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="mtx:subsection/mtx:headline">
  <xsl:choose>
    <xsl:when test="../@no">
      <xsl:text>&br;\subsecNumAndHeadline{</xsl:text>
      <xsl:value-of select="concat(../../@no, '.', ../@no)"/>
      <xsl:text>}{</xsl:text>
      <xsl:value-of select="x-latex:processText(string(.))"/>
      <xsl:text>}\label{</xsl:text>
      <xsl:value-of select="../@id"/>
      <xsl:text>}&br;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>&br;\secHeadline{</xsl:text>
      <xsl:value-of select="x-latex:processText(string(.))"/>
      <xsl:text>}&br;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="mtx:subsubsection/mtx:headline">
  <xsl:choose>
    <xsl:when test="../@no">
      <xsl:text>&br;\subsecNumAndHeadline{</xsl:text>
      <xsl:value-of select="concat(../../../@no, '.', ../../@no, '.', ../@no)"/>
      <xsl:text>}{</xsl:text>
      <xsl:value-of select="x-latex:processText(string(.))"/>
      <xsl:text>}\label{</xsl:text>
      <xsl:value-of select="../@id"/>
      <xsl:text>}&br;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>&br;\secHeadline{</xsl:text>
      <xsl:value-of select="x-latex:processText(string(.))"/>
      <xsl:text>}&br;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>