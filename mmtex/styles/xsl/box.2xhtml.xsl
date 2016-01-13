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
                xmlns:data="http://www.mumie.net/xml-namespace/mmtex/styles/xsl/data"
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mtx data">

<xsl:template name="setup-box-element">
  <xsl:call-template name="class">
    <xsl:with-param name="base-class">box</xsl:with-param>
  </xsl:call-template>
  <xsl:variable name="style">
    <xsl:if test="@width">
      <xsl:value-of select="concat('width:',@width,';')"/>
    </xsl:if>
    <xsl:if test="@height">
      <xsl:value-of select="concat('height:',@height,';')"/>
    </xsl:if>
    <xsl:if test="@content-align">
      <xsl:value-of select="concat('text-align:',@content-align)"/>
    </xsl:if>
    <xsl:if test="@content-valign">
      <xsl:value-of select="concat('vertical-align:',@content-valign)"/>
    </xsl:if>
  </xsl:variable>
  <xsl:if test="$style!=''">
    <xsl:attribute name="style">
      <xsl:value-of select="$style"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="mtx:box">
  <span>
    <xsl:call-template name="setup-box-element"/>
  </span>
</xsl:template>

<xsl:template match="mtx:block">
  <div>
    <xsl:call-template name="setup-box-element"/>
  </div>
</xsl:template>

</xsl:stylesheet>