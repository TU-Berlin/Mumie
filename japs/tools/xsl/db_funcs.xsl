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
   Authors:  Tilman Rassy        <rassy@math.tu-berlin.de>
             Fritz Lehman-Grube  <lehmannf@math.tu-berlin.de>

   $Id: db_funcs.xsl,v 1.3 2007/07/11 15:38:59 grudzin Exp $
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                xmlns:x-sql="xalan://net.mumie.xslt.ext.SQLUtil"
                version="1.0">

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:include href="config.xsl"/>
<xsl:include href="db_notions.xsl"/>

<!--
   =====================================================================================
   Parameters and variables
   =====================================================================================
-->

<!--
   =====================================================================================
   Creating maxVersionSet(int) function 
   =====================================================================================
-->

<xsl:template name="db-funcs.maxVersionSet">
  <xsl:text>CREATE OR REPLACE FUNCTION maxVersionSet(int)&br;</xsl:text>
  <xsl:text>RETURNS setof int AS '&br;</xsl:text>
  <xsl:text>SELECT max FROM (&br;</xsl:text>
  <xsl:text>&sp;&sp;SELECT 0 AS "max"&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="name">
      <xsl:call-template name="config.db-table-name.document"/>
    </xsl:variable>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="$is-generic='no'">
      <xsl:text>&sp;&sp;UNION&br;</xsl:text>
      <xsl:text>&sp;&sp;SELECT max(</xsl:text>
      <xsl:value-of select="$db-notions.column-name.version"/>
      <xsl:text>) FROM </xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text> WHERE </xsl:text>
      <xsl:value-of select="$db-notions.column-name.vc_thread"/>
      <xsl:text> = $1&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
  <xsl:text>) AS foo;&br;</xsl:text>
  <xsl:text>'&br;</xsl:text>
  <xsl:text>LANGUAGE 'SQL';&br;</xsl:text>
</xsl:template>


<!--
   =====================================================================================
   Main template
   =====================================================================================
-->

<xsl:template name="db-core.separator">
  <xsl:text>-- ==============================================================</xsl:text>
  <xsl:text>=============&br;</xsl:text>
</xsl:template>

<xsl:template match="/">

  <xsl:call-template name="db-funcs.maxVersionSet"/>

</xsl:template>

</xsl:stylesheet>
