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

   $Id: DbColumn.xsl,v 1.6 2007/07/11 15:38:56 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">DbColumn.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: DbColumn.xsl,v 1.6 2007/07/11 15:38:56 grudzin Exp $</xsl:param>
<xsl:param name="DbColumn.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<!--
   ==========================================================================================
   Templates for target "DbColumn"
   ==========================================================================================
-->

<xsl:template name="DbColumn.columns">
  <xsl:for-each select="/*/db-columns/db-column">
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
    <xsl:call-template name="config.db-column.constant"/>
    <xsl:text> = "</xsl:text>
    <xsl:call-template name="config.db-column.name"/>
    <xsl:text>";&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template name="DbColumn.column-lists">
  <xsl:for-each select="/*/db-columns/db-column-list">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>&sp;/**&br;</xsl:text>
    <xsl:text>&sp; * </xsl:text>
    <xsl:call-template name="config.description"/>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp; */&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;final public static String[] </xsl:text>
    <xsl:value-of select="@constant"/>
    <xsl:text> =&br;</xsl:text>
    <xsl:text>&sp;{&br;</xsl:text>
    <xsl:for-each select="item">
      <xsl:text>&sp;&sp;</xsl:text>
      <xsl:call-template name="config.db-column.constant"/>
      <xsl:text>,&br;</xsl:text>
    </xsl:for-each>
    <xsl:text>&sp;};&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template name="DbColumn.mayCreate">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping each document type to the corresponding </xsl:text>
  <xsl:text>&lt;code&gt;"may_create_...&lt;/code&gt; column.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;final public static String[] mayCreate =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:call-template name="config.db-column-name.may-create-document"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbColumn">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$DbColumn.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text> * &lt;p&gt;&br;</xsl:text>
      <xsl:text> * &sp;Defines static constants wrapping db column names.&br;</xsl:text>
      <xsl:text> * &lt;/p&gt;&br;</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class DbColumn&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="DbColumn.columns"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbColumn.column-lists"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbColumn.mayCreate"/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="DbColumn"/>
</xsl:template>

</xsl:stylesheet>
