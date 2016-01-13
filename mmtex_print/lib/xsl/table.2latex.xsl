<?xml version="1.0" encoding="utf-8"?>
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
                xmlns:x-latex="xalan://LaTeXUtil"
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex">

<xsl:template match="mtx:table">
  <xsl:text>&br;\begin{mtxTable}{</xsl:text>
  <xsl:value-of select="x-latex:generateTableFormat(number(@cols))"/>
  <xsl:text>}&br;</xsl:text>
  <xsl:text>\hline&br;</xsl:text>
  <xsl:apply-templates select="mtx:thead/*"/>
  <xsl:text>\hline&br;</xsl:text>
  <xsl:apply-templates select="mtx:tbody/*"/>
  <xsl:if test="mtx:tfoot">
    <xsl:text>\hline&br;</xsl:text>
    <xsl:apply-templates select="./mtx:tfoot"/>
  </xsl:if>
  <xsl:text>\end{mtxTable}&br;</xsl:text>
</xsl:template>

<xsl:template match="mtx:trow">
  <xsl:for-each select="mtx:tcell">
    <xsl:apply-templates select="."/>
    <xsl:if test="position() != last()"> &amp; </xsl:if>
  </xsl:for-each>
  <xsl:text>\\ \hline&br;</xsl:text>
</xsl:template>

<xsl:template match="mtx:tcell">
  <xsl:text>\multicolumn{</xsl:text>
  <!-- Number of columns ("colspan"): -->
  <xsl:choose>
    <xsl:when test="@colspan">
      <xsl:value-of select="@colspan"/>
    </xsl:when>
    <xsl:otherwise>1</xsl:otherwise>
  </xsl:choose>
  <xsl:text>}{|</xsl:text>
  <!-- Align: -->
  <xsl:choose>
    <xsl:when test="@align='left'">l</xsl:when>
    <xsl:when test="@align='center'">c</xsl:when>
    <xsl:when test="@align='right'">r</xsl:when>
    <xsl:otherwise>c</xsl:otherwise>
  </xsl:choose>
  <xsl:text>|}{</xsl:text>
  <!-- Content: -->
  <xsl:apply-templates/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

</xsl:stylesheet>