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

   $Id: create_db_table_names.xsl,v 1.4 2007/12/13 15:08:44 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.db classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dc="http://www.mumie.net/xml-namespace/declarations"
                xmlns:x-lib="xalan://net.mumie.japs.build.XSLExtLib"
                version="1.0">

<xsl:output method="xml" version="1.0" indent="yes" encoding="ASCII"/>

<xsl:template match="/">

  <xsl:comment> Autogenerated file. Contains database table names as XSL variables </xsl:comment>

  <xsl:element name="xsl:stylesheet" namespace="http://www.w3.org/1999/XSL/Transform">
    <xsl:attribute name="version">1.0</xsl:attribute>

    <!-- Explicitly defined table names: -->
    <xsl:for-each select="/*/dc:db-tables/dc:db-table">
      <xsl:element name="xsl:variable">
        <xsl:attribute name="name">
          <xsl:value-of select="concat('db-table.', @name)"/>
        </xsl:attribute>
        <xsl:value-of select="@name"/>
      </xsl:element>
    </xsl:for-each>

    <!-- Table names of (pseudo-)documents: -->
    <xsl:for-each select="/*/dc:data-entity-types/*">
      <xsl:variable name="name" select="x-lib:dbTable()"/>
      <xsl:element name="xsl:variable">
        <xsl:attribute name="name">
          <xsl:value-of select="concat('db-table.', $name)"/>
        </xsl:attribute>
        <xsl:value-of select="$name"/>
      </xsl:element>
    </xsl:for-each>

  </xsl:element>

</xsl:template>

</xsl:stylesheet>
