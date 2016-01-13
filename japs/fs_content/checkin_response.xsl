<?xml version="1.0" encoding="ASCII"?>


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

<!DOCTYPE stylesheet
  [
   <!ENTITY br  "&#xA;">
   <!ENTITY sp  "  ">
  ]
>

<!--
   Authors:  Marek Grudzinski <grudzin@math.tu-berlin.de>
             Tilman Rassy < rassy@math.tu-berlin.de>

   $Id: checkin_response.xsl,v 1.2 2007/07/11 15:38:37 grudzin Exp $
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mumie="http://www.mumie.net/xml-namespace/document/metainfo"
                version="1.0">

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<!-- 
<xsl:template match="*/*" priority="1">
  <xsl:text>&br;Checked-in: </xsl:text>
  <xsl:text>&br;&sp;path      = </xsl:text>
  <xsl:value-of select="@path"/>
  <xsl:text>&br;&sp;type      = </xsl:text>
  <xsl:value-of select="local-name(.)"/>
  <xsl:text>&br;&sp;id        = </xsl:text>
  <xsl:value-of select="@id"/>
  <xsl:if test="mumie:vc_thread">
    <xsl:text>&br;&sp;vc_thread = </xsl:text>
    <xsl:value-of select="mumie:vc_thread/@id"/>
  </xsl:if>
  <xsl:if test="mumie:version">
    <xsl:text>&br;&sp;version   = </xsl:text>
    <xsl:value-of select="mumie:version/@value"/>
  </xsl:if>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<xsl:template match="*/mumie:error" priority="2">
  <xsl:text>ERROR&br;</xsl:text>
  <xsl:value-of select="."/>
</xsl:template>
 -->

<xsl:template match="*">
  <xsl:choose>
    <xsl:when test="mumie:error">
      <xsl:text>ERROR&br;</xsl:text>
      <xsl:value-of select="mumie:error"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="total" select="count(*)"/>
      <xsl:for-each select="*">
        <xsl:text>&br;content object </xsl:text>
        <xsl:value-of select="concat(position(), '/', $total)"/>
        <xsl:text>&br;&sp;path      = </xsl:text>
        <xsl:value-of select="@path"/>
        <xsl:text>&br;&sp;type      = </xsl:text>
        <xsl:value-of select="local-name(.)"/>
        <xsl:text>&br;&sp;id        = </xsl:text>
        <xsl:value-of select="@id"/>
        <xsl:if test="mumie:vc_thread">
          <xsl:text>&br;&sp;vc_thread = </xsl:text>
          <xsl:value-of select="mumie:vc_thread/@id"/>
        </xsl:if>
        <xsl:if test="mumie:version">
          <xsl:text>&br;&sp;version   = </xsl:text>
          <xsl:value-of select="mumie:version/@value"/>
        </xsl:if>
        <xsl:text>&br;</xsl:text>
      </xsl:for-each>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>