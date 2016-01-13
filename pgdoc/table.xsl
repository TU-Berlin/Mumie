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

<!--
  Author:  Nina Dahlmann 
  -->
  
  <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                  version="1.0">
    
    <xsl:output method="html"/>
    
    <xsl:template match="/">
      <html>
        <head>
          <title>The Tables and References of our Database</title>
        </head>
        <p>
          <h2 align="center">TABLE OF CONTENTS (alphabetically)</h2>
        </p>
	<table border="0" align="center">
    	  <tr>
            <xsl:call-template name="table_of_content"/>
          </tr>
        </table>
        <xsl:apply-templates select="dbtables/dbcontent"/>
      </html>
    </xsl:template>
    
    <xsl:template match="dbcontent">
      <body>
        <xsl:apply-templates/>
      </body>
    </xsl:template>

    <!--==========================The templates=====================-->
    <!--==========================Table of content==================-->

    <xsl:template name="table_of_content">
      <xsl:for-each select="dbtables/dbcontent/table">
        <xsl:sort select="@name" order="ascending" data-type="text"/>
        <tr>
        <td>
          <a>
            <xsl:attribute name="href">#<xsl:value-of select="@name"/></xsl:attribute>
            <xsl:value-of select="@name"/></a>
        </td>
        </tr>
      </xsl:for-each>
    </xsl:template>

    <!--============================= TABLES=============================-->
    <xsl:template name="general_table">
      <xsl:choose>      
        <xsl:when test="@type='Table'">
          <xsl:call-template name="table_with_type_table"/>
        </xsl:when>
        <xsl:when test="@type='View'">
          <xsl:call-template name="table_with_type_view"/>
        </xsl:when>
      </xsl:choose>
      </xsl:template>

      <xsl:template name="name_set">
        <a><xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute></a>
      </xsl:template>
      
      <xsl:template name="table_with_type_table">
        <table border="1" bgcolor="#efb3b3" >
          <xsl:call-template name="name_set"/>
          <p>Name of the Table: <xsl:value-of select="@name"/></p>
          <xsl:apply-templates/>
        </table>
        <hr/>
      </xsl:template>

      <xsl:template name="table_with_type_view">
      <table border="1" bgcolor="#b3c9ef" >
        <xsl:call-template name="name_set"/>
        <p>Name of the View: <xsl:value-of select="@name"/></p>
        <xsl:apply-templates/>
      </table>
    </xsl:template>

    <!--=============================Columns============================-->

    <xsl:template name="reference">
      <tr>
        <td>Column:
        <xsl:value-of select="@fromcolumn"/>
        <br/>
        references to table:<br/>
        <a><xsl:attribute name="href">#<xsl:value-of select="@totable"/></xsl:attribute>
        <xsl:value-of select="@totable"/></a>
        and there in column:<xsl:value-of select="@tocolumn"/>
        </td>
      </tr>
    </xsl:template>

    <xsl:template name="referenced">
      <tr>
        <td>From table:<br/>
        <a><xsl:attribute name="href">#<xsl:value-of select="@fromtable"/></xsl:attribute>
        <xsl:value-of select="@fromtable"/></a><br/>
        there is a reference from column:<xsl:value-of select="@fromcolumn"/><br/> 
        to column:<xsl:value-of select="@tocolumn"/>
        </td>
      </tr>
    </xsl:template>

    <xsl:template name="normal_columns">
      <td>
      Name of the column=<xsl:value-of select="@name"/><br/>
      Datatype=<xsl:value-of select="@type"/><br/>
      <xsl:if test="@null!=''"> 
        Null=<xsl:value-of select="@null"/><br/>
      </xsl:if>
      <xsl:if test="@default!=''">
        Defaultvalue=<xsl:value-of select="@default"/>
      </xsl:if>
      <xsl:apply-templates/>
    </td>
    </xsl:template>

    <!--==========================The matches============================-->

    <xsl:template match="table">
      <xsl:call-template name="general_table"/>
    </xsl:template>

    <xsl:template match="column">
      <xsl:call-template name="normal_columns"/>
    </xsl:template>
    
    <xsl:template match="reference">
      <xsl:call-template name="reference"/>
    </xsl:template> 

    <xsl:template match="referenced">
      <xsl:call-template name="referenced"/>
    </xsl:template>

  </xsl:stylesheet>