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
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex"
                xmlns:x-latex="xalan://LaTeXUtil">

<!--
  Numbered lists
-->

<xsl:template match="mtx:list[@numbering='no']">
  <xsl:text>&br;\begin{mtxList}&br;</xsl:text>
  <xsl:for-each select="item">
    <xsl:text>&br;\item[</xsl:text>
    <xsl:choose>
      <xsl:when test="mtx:label">
        <xsl:apply-templates select="mtx:label/node()"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="depth" select="count(ancestor::mtx:list[@numbering='no'])"/>
        <xsl:choose>
          <xsl:when test="$depth = 1">
            <xsl:number value="@no" format="1."/>
          </xsl:when>
          <xsl:when test="$depth = 2">
            <xsl:number value="@no" format="(a)"/>
          </xsl:when>
          <xsl:when test="$depth = 3">
            <xsl:number value="@no" format="i."/>
          </xsl:when>
          <xsl:when test="$depth = 4">
            <xsl:number value="@no" format="A."/>
          </xsl:when>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>]</xsl:text>
    <xsl:apply-templates select="mtx:content"/>
  </xsl:for-each>
  <xsl:text>\end{mtxList}&br;</xsl:text>
</xsl:template>

<!--
  Unnumbered lists
-->

<xsl:template match="mtx:list[@numbering='no']">
  <xsl:text>&br;\begin{mtxList}&br;</xsl:text>
  <xsl:for-each select="mtx:item">
    <xsl:text>&br;\item[</xsl:text>
    <xsl:choose>
      <xsl:when test="mtx:label">
        <xsl:apply-templates select="mtx:label/node()"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="depth" select="count(ancestor::mtx:list[@numbering='no'])"/>
        <xsl:choose>
          <xsl:when test="$depth = 1">\listItemMarkI</xsl:when>
          <xsl:when test="$depth = 2">\listItemMarkII</xsl:when>
          <xsl:when test="$depth = 3">\listItemMarkIII</xsl:when>
          <xsl:when test="$depth = 4">\listItemMarkIV</xsl:when>
          <xsl:otherwise>\listItemMarkOther</xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>]</xsl:text>
    <xsl:apply-templates select="mtx:content"/>
  </xsl:for-each>
  <xsl:text>\end{mtxList}&br;</xsl:text>
</xsl:template>

</xsl:stylesheet>