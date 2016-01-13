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

   $Id: XMLElement.xsl,v 1.6 2007/07/11 15:38:58 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">XMLElement.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: XMLElement.xsl,v 1.6 2007/07/11 15:38:58 grudzin Exp $</xsl:param>
<xsl:param name="XMLElement.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<!--
   ==========================================================================================
   Templates for target "XMLElement"
   ==========================================================================================
-->

<xsl:template name="XMLElement.DOC">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding XML element names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] DOC =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:call-template name="config.document-type.xml-element-name"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="XMLElement.xml-elements.db-column">
  <xsl:for-each select="/*/db-columns/db-column">
    <xsl:variable name="name">
      <xsl:call-template name="config.db-column.xml-element-name"/>
    </xsl:variable>
    <xsl:if test="not(/*/xml-elements/xml-element[@name=$name])">
      <xsl:if test="position()&gt;1">
        <xsl:text>&br;</xsl:text>
      </xsl:if>
      <xsl:text>&sp;/**&br;</xsl:text>
      <xsl:text>&sp; * </xsl:text>
      <xsl:call-template name="config.description"/>
      <xsl:text>&br;</xsl:text>
      <xsl:text>&sp; */&br;</xsl:text>
      <xsl:text>&br;</xsl:text>
      <xsl:text>&sp;public final static String </xsl:text>
      <xsl:call-template name="config.db-column.xml-element-constant"/>
      <xsl:text> = "</xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>";&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template name="XMLElement.xml-elements">
  <xsl:for-each select="/*/xml-elements/xml-element">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>&sp;/**&br;</xsl:text>
    <xsl:text>&sp; * </xsl:text>
    <xsl:call-template name="config.description"/>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp; */&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;public final static String </xsl:text>
    <xsl:call-template name="config.xml-element.constant"/>
    <xsl:text> = "</xsl:text>
    <xsl:call-template name="config.xml-element.name"/>
    <xsl:text>";&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template name="XMLElement">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$XMLElement.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text> * &lt;p&gt;&br;</xsl:text>
      <xsl:text> * &sp;Defines static constants wrapping XML element names.&br;</xsl:text>
      <xsl:text> * &lt;/p&gt;&br;</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class XMLElement&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="XMLElement.DOC"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="XMLElement.xml-elements.db-column"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="XMLElement.xml-elements"/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="XMLElement"/>
</xsl:template>

</xsl:stylesheet>
