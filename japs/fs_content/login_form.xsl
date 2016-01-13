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
   <!ENTITY lf-namespace  "http://www.mumie.net/xml-namespace/login-form">
  ]
>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lf="&lf-namespace;"
                xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"
            encoding="ASCII"/>

<!--
  ================================================================================
  Global parameters and variables.
  ================================================================================
-->

<!-- The URL prefix -->
<xsl:param name="url-prefix">https://japs.mumie.net/cocoon</xsl:param>

<!-- The requested resource -->
<xsl:param name="resource"/>

<!-- Default for the requested resource -->
<xsl:param name="default-resource" select="concat($url-prefix, '/protected/alias/start')"/>

<!-- The language -->
<xsl:param name="lang">de</xsl:param>

<!-- Whether an error occurred -->
<xsl:param name="error">no</xsl:param>

<!--
  ================================================================================
  Non-extension elements and attributes
  ================================================================================
-->
 
<xsl:template match="*[namespace-uri()!='&lf-namespace;']" priority="0">
  <xsl:copy>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*[namespace-uri()!='&lf-namespace;']" priority="0">
  <xsl:copy-of select="."/>
</xsl:template>

<!--
  ================================================================================
  Internationalization: lf:i18n-xxx elements
  ================================================================================
-->

<xsl:template match="lf:i18n-defs">
  <!-- Ignored when accessed directly -->
</xsl:template>

<xsl:template match="lf:i18n-def">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="lf:i18n-text">
  <xsl:apply-templates select="//lf:i18n-defs[@lang=$lang]/lf:i18n-def[@name=current()/@ref]"/>
</xsl:template>

<!--
  ================================================================================
  Control structures: lf:if-error, lf:then, lf:else elements
  ================================================================================
-->

<xsl:template match="lf:if-error">
  <xsl:choose>
    <xsl:when test="$error='true'">
      <xsl:apply-templates select="lf:then"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="lf:else"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="lf:then|lf:else">
  <xsl:apply-templates/>
</xsl:template>

<!--
  ================================================================================
  Resolution of the %{resource} keyword
  ================================================================================
-->

<xsl:template match="@value[.='%{resource}']">
  <xsl:attribute name="value">
    <xsl:choose>
      <xsl:when test="$resource and $resource != ''">
        <xsl:value-of select="$resource"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$default-resource"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
</xsl:template>

</xsl:stylesheet>
