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
                xmlns:data="http://www.mumie.net/xml-namespace/mmtex/styles/xsl/data"
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex">

<xsl:param name="left-quote">
  <xsl:choose>
    <xsl:when test="$language='en'">"</xsl:when>
    <xsl:when test="$language='de'">\glqq </xsl:when>
    <xsl:otherwise>"</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<xsl:param name="right-quote">
  <xsl:choose>
    <xsl:when test="$language='en'">"</xsl:when>
    <xsl:when test="$language='de'">\grqq </xsl:when>
    <xsl:otherwise>"</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<xsl:template match="mtx:emph">
  <xsl:text>\mtxEmph{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="mtx:code">
  <xsl:text>\mtxCode{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="mtx:keyb">
  <xsl:text>\mtxKeyb{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="mtx:var">
  <xsl:text>\mtxVar{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="mtx:file">
  <xsl:text>\mtxFile{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="mtx:meta">
  <xsl:text>\mtxMeta{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="mtx:optional">
  <xsl:text>\mtxMeta{[}</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>\mtxMeta{]}</xsl:text>
</xsl:template>

<xsl:template match="mtx:alts">
  <xsl:variable name="suppress-parenthesis"
                select="local-name(..)='optional' and count(../*)=1 and count(../text())=0"/>
  <xsl:if test="not($suppress-parenthesis)">
    <xsl:text>\mtxMeta{(}</xsl:text>
  </xsl:if>
  <xsl:for-each select="mtx:alt">
    <xsl:if test="position()!=1">
      <xsl:text>\mtxMeta{|}</xsl:text>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:for-each>
  <xsl:if test="not($suppress-parenthesis)">
    <xsl:text>\mtxMeta{)}</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="mtx:mark">
  <!-- TODO -->
</xsl:template>

<xsl:template match="mtx:break">
  <xsl:text>\\</xsl:text>
  <xsl:if test="@space">
    <xsl:text>\vspace{</xsl:text>
    <xsl:value-of select="@space"/>
    <xsl:text>}</xsl:text>
  </xsl:if>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<xsl:template match="mtx:quoted">
  <xsl:value-of select="$left-quote"/>
  <xsl:apply-templates/>
  <xsl:value-of select="$right-quote"/>
</xsl:template>

</xsl:stylesheet>