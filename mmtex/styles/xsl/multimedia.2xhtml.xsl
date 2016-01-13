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
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex"
                xmlns:data="http://www.mumie.net/xml-namespace/mmtex/styles/xsl/data"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mtx data">

<xsl:template match="mtx:image/@src|mtx:image/@align|mtx:image/@alt">
  <xsl:copy/>
</xsl:template>

<xsl:template name="image">
  <img>
    <xsl:apply-templates select="@*"/>
  </img>
</xsl:template>

<xsl:template match="mtx:image">
  <xsl:choose>
    <xsl:when test="@align='left' or @align='right' or @align='center'">
      <div>
        <xsl:copy-of select="@align"/>
        <xsl:call-template name="class">
          <xsl:with-param name="base-class">image</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="image"/>
      </div>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="image"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>