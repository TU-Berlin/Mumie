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
   $Id: config_to.db_notions.xsl.xsl,v 1.8 2007/07/11 15:38:58 grudzin Exp $

   Stylesheet to create the db_notions.xsl stylesheet from the config.xml file.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="xml" version="1.0" indent="yes"/>

<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">config_to.db_notions.xsl.xsl</xsl:param>

<xsl:param name="this.cvs-id">$Id: config_to.db_notions.xsl.xsl,v 1.8 2007/07/11 15:38:58 grudzin Exp $</xsl:param>
  
<xsl:param name="db-notions.prefix" select="'db-notions'"/>

<xsl:template match="/*/db-tables/db-table">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($db-notions.prefix,'.table-name.',@name)"/>
    </xsl:attribute>
    <xsl:value-of select="@name"/>
  </xsl:element>
</xsl:template>

<xsl:template match="/*/db-columns/db-column">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($db-notions.prefix,'.column-name.',@name)"/>
    </xsl:attribute>
    <xsl:value-of select="@name"/>
  </xsl:element>
</xsl:template>

<xsl:template match="/*/document-types/document-type">
  <xsl:variable name="table-name">
    <xsl:call-template name="config.db-table-name.document"/>
  </xsl:variable>
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($db-notions.prefix,'.table-name.',$table-name)"/>
    </xsl:attribute>
    <xsl:value-of select="$table-name"/>
  </xsl:element>
</xsl:template>

<xsl:template name="db-notions.table-name.thumbnails">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($db-notions.prefix,'.table-name.thumbnails')"/>
    </xsl:attribute>
    <xsl:call-template name="config.db-table-name.document">
      <xsl:with-param name="doctype" select="$config.thumbnail.document-type.name"/>
    </xsl:call-template>
  </xsl:element>
</xsl:template>

<xsl:template name="db-notions.table-name.correctors">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($db-notions.prefix,'.table-name.correctors')"/>
    </xsl:attribute>
    <xsl:call-template name="config.db-table-name.document">
      <xsl:with-param name="doctype" select="$config.corrector.document-type.name"/>
    </xsl:call-template>
  </xsl:element>
</xsl:template>

<xsl:template match="/*/db-views/db-view">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($db-notions.prefix,'.view-name.',@name)"/>
    </xsl:attribute>
    <xsl:value-of select="@name"/>
  </xsl:element>
</xsl:template>

<xsl:template match="/*/db-constants/db-constant">
  <xsl:element name="xsl:variable">
    <xsl:attribute name="name">
      <xsl:value-of select="concat($db-notions.prefix,'.constant-value.',@name)"/>
    </xsl:attribute>
    <xsl:value-of select="@value"/>
  </xsl:element>
</xsl:template>

<xsl:template match="/*">
  <xsl:call-template name="config.xml-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text>DB table names, column names, and constants as XSL variables</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:element name="xsl:stylesheet" namespace="http://www.w3.org/1999/XSL/Transform">
    <xsl:attribute name="version">1.0</xsl:attribute>
    <xsl:text>&br;</xsl:text>
    <xsl:comment>
      <xsl:text>DB table names</xsl:text>
    </xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:for-each select="/*/db-tables">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
    <xsl:for-each select="/*/document-types">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
    <xsl:call-template name="db-notions.table-name.thumbnails"/>
    <xsl:text>&br;</xsl:text>
    <xsl:call-template name="db-notions.table-name.correctors"/>
    <xsl:text>&br;</xsl:text>
    <xsl:comment>
      <xsl:text>DB view names</xsl:text>
    </xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:for-each select="/*/db-views">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>

    <xsl:text>&br;</xsl:text>
    <xsl:comment>
      <xsl:text>DB column names</xsl:text>
    </xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:for-each select="/*/db-columns">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
    <xsl:comment>
      <xsl:text>DB constant values</xsl:text>
    </xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:for-each select="/*/db-constants">
      <xsl:apply-templates/>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>
