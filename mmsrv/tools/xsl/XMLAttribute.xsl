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
   <!ENTITY dash10  "----------">
   <!ENTITY dash20  "&dash10;&dash10;">
   <!ENTITY dash80  "&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;">
  ]
>

<!--

   Author:  Tilman Rassy

   $Id: XMLAttribute.xsl,v 1.1 2008/07/29 23:02:28 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.db classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dc="http://www.mumie.net/xml-namespace/declarations"
                xmlns:x-lib="xalan://net.mumie.srv.build.XSLExtLib"
                xmlns:x-strutil="xalan://net.mumie.util.StringUtil"
                xmlns:x-ioutil="xalan://net.mumie.util.io.IOUtil"
                version="1.0">

<xsl:output method="text"/>

<xsl:strip-space elements="*"/>

<xsl:param name="skeleton-filename"/>

<xsl:variable name="xml-attributes" select="/*/dc:xml-attributes/dc:xml-attribute"/>

<xsl:template name="xml-attribute-constants">
  <xsl:for-each select="$xml-attributes">
    <xsl:text>&br;</xsl:text>
    <xsl:value-of select="x-lib:javadocComment()"/>
    <xsl:text>&br;&br;</xsl:text>
    <xsl:text>&sp;public static final String </xsl:text>
    <xsl:value-of select="x-lib:itemKey()"/>
    <xsl:text> = "</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>";&br;</xsl:text>
  </xsl:for-each>  
</xsl:template>

<xsl:template match="/">
  <xsl:variable name="skeleton" select="x-ioutil:readFile($skeleton-filename)"/>
  <xsl:variable name="autocoded">//#AUTOCODED</xsl:variable>
  <xsl:value-of select="substring-before($skeleton,$autocoded)"/>
  <xsl:call-template name="xml-attribute-constants"/>
  <xsl:value-of select="substring-after($skeleton,$autocoded)"/>
</xsl:template>

</xsl:stylesheet>
