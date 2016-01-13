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

<!DOCTYPE stylesheet
  [
   <!ENTITY br  "&#xA;">
   <!ENTITY sp  "  ">
  ]
>

<!--
   Author:  Tilman Rassy

   $Id: XMLNamespace.xsl,v 1.9 2007/07/11 15:38:58 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-strutil="xalan://net.mumie.util.StringUtil"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">XMLNamespace.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: XMLNamespace.xsl,v 1.9 2007/07/11 15:38:58 grudzin Exp $</xsl:param>
<xsl:param name="XMLNamespace.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>
<xsl:param name="XMLNamespace.mumie-xml-namespace">http://www.mumie.net/xml-namespace</xsl:param>
<xsl:param name="XMLNamespace.mumie-xml-namespace-shortcut">$MUMIE</xsl:param>

<!--
   ==========================================================================================
   Templates for target "XMLNamespace"
   ==========================================================================================
-->

<xsl:template name="XMLNamespace.namespaces">
  <xsl:for-each select="/*/xml-namespaces/xml-namespace">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:variable name="uri-constant">
      <xsl:call-template name="config.xml-namespace.uri.constant"/>
    </xsl:variable>
    <xsl:variable name="prefix-constant">
      <xsl:call-template name="config.xml-namespace.prefix.constant"/>
    </xsl:variable>
    <xsl:text>&sp;/**&br;</xsl:text>
    <xsl:text>&sp; * &lt;p&gt;&br;</xsl:text>
    <xsl:text>&sp; * &sp;</xsl:text>
    <xsl:call-template name="config.description">
      <xsl:with-param name="newline">&br;&sp; * &sp;</xsl:with-param>
    </xsl:call-template>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp; * &lt;/p&gt;&br;</xsl:text>
    <xsl:text>&sp; * &lt;p&gt;&br;</xsl:text>
    <xsl:text>&sp; * &sp;Value: &lt;span class="string"&gt;"</xsl:text>
    <xsl:value-of select="@uri"/> 
    <xsl:text>"&lt;/span&gt;&br;</xsl:text>
    <xsl:text>&sp; * &lt;/p&gt;&br;</xsl:text>
    <xsl:text>&sp; */&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;public final static String </xsl:text>
    <xsl:value-of select="$uri-constant"/>
    <xsl:text> = "</xsl:text>
    <xsl:value-of select="@uri"/> 
    <xsl:text>";&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;/**&br;</xsl:text>
    <xsl:text>&sp; * &lt;p&gt;&br;</xsl:text>
    <xsl:text>&sp; * &sp;Default prefix for the namespace {@link #</xsl:text>
    <xsl:value-of select="$uri-constant"/>
    <xsl:text>}.&br;</xsl:text>
    <xsl:text>&sp; * &lt;/p&gt;&br;</xsl:text>
    <xsl:text>&sp; * &lt;p&gt;&br;</xsl:text>
    <xsl:text>&sp; * &sp;Value: &lt;span class="string"&gt;"</xsl:text>
    <xsl:value-of select="@prefix"/> 
    <xsl:text>"&lt;/span&gt;&br;</xsl:text>
    <xsl:text>&sp; * &lt;/p&gt;&br;</xsl:text>
    <xsl:text>&sp; */&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;public final static String </xsl:text>
    <xsl:value-of select="$prefix-constant"/>
    <xsl:text> = "</xsl:text>
    <xsl:value-of select="@prefix"/> 
    <xsl:text>";&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template name="XMLNamespace">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$XMLNamespace.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text> * &lt;p&gt;&br;</xsl:text>
      <xsl:text> * &sp;Defines static constants wrapping XML namespace URI's</xsl:text>
      <xsl:text> and prefixes.&br;</xsl:text>
      <xsl:text> * &lt;/p&gt;&br;</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class XMLNamespace&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="XMLNamespace.namespaces"/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>


<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="XMLNamespace"/>
</xsl:template>

</xsl:stylesheet>
