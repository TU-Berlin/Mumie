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
   Author:  Tilman Rassy

   $Id: create_rootxsl.xsl,v 1.4 2007/07/11 15:38:58 grudzin Exp $
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rootxsl="http://www.mumie.net/xml-namespace/rootxsl"
                xmlns:xsl-lib="http://www.mumie.net/xml-namespace/xsl-lib"
                exclude-result-prefixes="rootxsl xsl-lib">

<xsl:output method="xml" encoding="ASCII"/>

<xsl:param name="url-prefix" select="'www.mumie.net/cocoon'"/>


<xsl:template match="*|@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="*|@*|node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="rootxsl:param[@name='xsl.url-prefix']" priority="1">
  <xsl:element name="xsl:param">
    <xsl:attribute name="name">
      <xsl:value-of select="@name"/>
    </xsl:attribute>
    <xsl:if test="@xsl-lib:included">
      <xsl:attribute name="xsl-lib:included">
        <xsl:value-of select="@xsl-lib:included"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:value-of select="$url-prefix"/>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>
