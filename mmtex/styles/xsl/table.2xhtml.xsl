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

<xsl:template match="mtx:table">
  <table>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">genuine</xsl:with-param>
    </xsl:call-template>
    <xsl:choose>
      <xsl:when test="@align='left'">
        <xsl:attribute name="style">margin-right:auto</xsl:attribute>
      </xsl:when>
      <xsl:when test="@align='center'">
        <xsl:attribute name="style">margin-left:auto;margin-right:auto</xsl:attribute>
      </xsl:when>
      <xsl:when test="@align='right'">
        <xsl:attribute name="style">margin-left:auto</xsl:attribute>
      </xsl:when>
    </xsl:choose>
    <xsl:copy-of select="@valign"/>
    <xsl:apply-templates select="./mtx:thead"/>
    <xsl:apply-templates select="./mtx:tfoot"/>
    <xsl:apply-templates select="./mtx:tbody"/>
  </table>
</xsl:template>

<xsl:template match="mtx:table/mtx:thead">
  <thead>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </thead>
</xsl:template>

<xsl:template match="mtx:table/mtx:tbody">
  <tbody>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </tbody>
</xsl:template>
  
<xsl:template match="mtx:table/mtx:tfoot">
  <tfoot>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </tfoot>
</xsl:template>

<xsl:template match="mtx:trow">
  <tr>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="mtx:tcell/@rowspan|mtx:tcell/@colspan|mtx:tcell/@align|mtx:tcell/@valign">
  <xsl:copy/>
</xsl:template>

<xsl:template match="mtx:tcell">
  <td>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </td>
</xsl:template>

</xsl:stylesheet>