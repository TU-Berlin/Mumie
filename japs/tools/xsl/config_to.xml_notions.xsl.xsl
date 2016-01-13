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
   Author:  Tilman Rassy <rassy@math.tu-berlin.de>
   $Id: config_to.xml_notions.xsl.xsl,v 1.4 2007/07/11 15:38:58 grudzin Exp $

   Stylesheet to create the db_notions.xsl stylesheet from the config.xml file.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="xml" version="1.0" indent="yes"/>

<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">config_to.xml_notions.xsl.xsl</xsl:param>

<xsl:param name="this.cvs-id">$Id: config_to.xml_notions.xsl.xsl,v 1.4 2007/07/11 15:38:58 grudzin Exp $</xsl:param>
  
<xsl:param name="xml-notions.prefix" select="'xml-notions'"/>

<xsl:template match="/*/xml-elements/xml-element">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($xml-notions.prefix,'.element-name.',@name)"/>
    </xsl:attribute>
    <xsl:value-of select="@name"/>
  </xsl:element>
</xsl:template>

<xsl:template match="/*/db-columns/db-column">
  <xsl:variable name="element-name">
    <xsl:call-template name="config.db-column.xml-element-name"/>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="/*/xml-elements/xml-element[@name=$element-name]">
      <xsl:comment>
        <xsl:text>&sp;[Already directly declared: </xsl:text>
        <xsl:value-of select="$element-name"/>
        <xsl:text>]&sp;</xsl:text>
      </xsl:comment>
    </xsl:when>
    <xsl:otherwise>
      <xsl:element name="xsl:variable">
        <xsl:attribute name="name">
          <xsl:value-of select="concat($xml-notions.prefix,'.element-name.',$element-name)"/>
        </xsl:attribute>
        <xsl:value-of select="$element-name"/>
      </xsl:element>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="/*/xml-attributes/xml-attribute">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($xml-notions.prefix,'.attribute-name.',@name)"/>
    </xsl:attribute>
    <xsl:value-of select="@name"/>
  </xsl:element>
</xsl:template>

<xsl:template match="/*">
  <xsl:call-template name="config.xml-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text>&sp;XML element and attribute names as XSL variables&sp;</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:element name="xsl:stylesheet" namespace="http://www.w3.org/1999/XSL/Transform">
    <xsl:attribute name="version">1.0</xsl:attribute>
    <xsl:text>&br;</xsl:text>
    <xsl:comment>
      <xsl:text>&sp;XML element names&sp;</xsl:text>
    </xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:for-each select="/*/xml-elements">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
    <xsl:comment>
      <xsl:text>&sp;XML element names, indirectly declared by db columns&sp;</xsl:text>
    </xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:for-each select="/*/db-columns">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
    <xsl:comment>
      <xsl:text>&sp;XML attribute names&sp;</xsl:text>
    </xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:for-each select="/*/xml-attributes">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>
