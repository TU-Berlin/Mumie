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
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex">

<xsl:template match="mtx:anchor" mode="anchor-full-number">
  <xsl:for-each select="ancestor::*[@no]">
    <xsl:if test="position()!=1">
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:value-of select="@no"/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="mtx:anchor">
  <!-- Ignored -->
</xsl:template>

<xsl:template match="mtx:ref">
  <xsl:variable name="name" select="@name"/>
  <xsl:text>\mtxRef{</xsl:text>
  <xsl:apply-templates select="//mtx:anchor[@name=$name]" mode="anchor-full-number"/>
  <xsl:text>}</xsl:text>
</xsl:template>

<xsl:template match="mtx:link">
  <xsl:text>\mtxRef{</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>}</xsl:text>
</xsl:template>

</xsl:stylesheet>