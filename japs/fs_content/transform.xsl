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
   <!ENTITY fs-ext    "http://www.mumie.net/xml-namespace/fs-content">
   <!ENTITY emb-xhtml "http://www.mumie.net/xml-namespace/fs-content-i18n-embedded-xhtml">
  ]
>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fs="&fs-ext;"
                xmlns:i18n="http://www.mumie.net/xml-namespace/fs-content-i18n"
                xmlns:h="&emb-xhtml;"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl fs i18n h">

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"
            encoding="ASCII"/>

<!-- ================================================================================ -->
<!-- h1: Global parameters and variables.                                             -->
<!-- ================================================================================ -->

<!-- The URL prefix -->
<xsl:param name="url-prefix"/>

<!-- The language code -->
<xsl:param name="lang">de</xsl:param>

<!-- Error message, if any -->
<xsl:param name="error"/>

<!-- "Positional" parameters -->
<xsl:param name="param0"/>
<xsl:param name="param1"/>
<xsl:param name="param2"/>
<xsl:param name="param3"/>
<xsl:param name="param4"/>
<xsl:param name="param5"/>
<xsl:param name="param6"/>
<xsl:param name="param7"/>
<xsl:param name="param8"/>
<xsl:param name="param9"/>

<xsl:variable name="i18n-defs-file" select="concat('resources/i18n_', $lang, '.xml')"/>

<!-- Root element of the i18n definitions -->
<!-- <xsl:variable name="i18n-defs" select="document('resources/i18n_de.xml')/i18n:defs"/> -->
<xsl:variable name="i18n-defs" select="document($i18n-defs-file)/i18n:defs"/>

<!-- ================================================================================ -->
<!-- h1: Non-extension elements and attributes                                        -->
<!-- ================================================================================ -->
 
<xsl:template match="*[namespace-uri()!='&fs-ext;']" priority="0">
  <xsl:copy>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*[namespace-uri()!='&fs-ext;']" priority="0">
  <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="@fs:id" priority="0">
  <!-- Ignore -->
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Positional parameters                                                        -->
<!-- ================================================================================ -->

<xsl:template name="param-value">
  <xsl:param name="pos" select="@pos"/>
  <xsl:choose>
    <xsl:when test="$pos=0"><xsl:value-of select="$param0"/></xsl:when>
    <xsl:when test="$pos=1"><xsl:value-of select="$param1"/></xsl:when>
    <xsl:when test="$pos=2"><xsl:value-of select="$param2"/></xsl:when>
    <xsl:when test="$pos=3"><xsl:value-of select="$param3"/></xsl:when>
    <xsl:when test="$pos=4"><xsl:value-of select="$param4"/></xsl:when>
    <xsl:when test="$pos=5"><xsl:value-of select="$param5"/></xsl:when>
    <xsl:when test="$pos=6"><xsl:value-of select="$param6"/></xsl:when>
    <xsl:when test="$pos=7"><xsl:value-of select="$param7"/></xsl:when>
    <xsl:when test="$pos=8"><xsl:value-of select="$param8"/></xsl:when>
    <xsl:when test="$pos=9"><xsl:value-of select="$param9"/></xsl:when>
  </xsl:choose>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Internationalization                                                         -->
<!-- ================================================================================ -->

<xsl:template name="i18n-text">
  <xsl:param name="ref" select="@ref"/>
  <xsl:apply-templates select="$i18n-defs/i18n:def[@id=$ref]/node()"/>
</xsl:template>

<xsl:template match="fs:i18n-text">
  <xsl:call-template name="i18n-text"/>
</xsl:template>

<xsl:template name="i18n-error-text">
  <xsl:call-template name="i18n-text">
    <xsl:with-param name="ref" select="$error"/>
  </xsl:call-template>
</xsl:template>

<xsl:template match="fs:i18n-error-text">
  <xsl:call-template name="i18n-error-text"/>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: XHTML embedded in internationalization XML                                   -->
<!-- ================================================================================ -->
 
<xsl:template match="*[namespace-uri()='&emb-xhtml;']" priority="1">
  <xsl:element name="{local-name()}">
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Control structures                                                           -->
<!-- ================================================================================ -->

<xsl:template match="fs:if-param">
  <xsl:variable name="value">
    <xsl:call-template name="param-value"/>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="$value = @value">
      <xsl:call-template name="then-clause"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="fs:else"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="fs:switch-param">
  <xsl:variable name="value">
    <xsl:call-template name="param-value"/>
  </xsl:variable>
  <xsl:apply-templates select="fs:case-param[@value=$value][1]"/>
</xsl:template>

<xsl:template name="then-clause">
  <xsl:choose>
    <xsl:when test="fs:then">
      <xsl:apply-templates select="fs:then"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="fs:then|fs:else|fs:case-param">
  <xsl:apply-templates/>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Error handling                                                               -->
<!-- ================================================================================ -->

<xsl:template match="fs:if-error">
  <xsl:choose>
    <xsl:when test="$error and $error!=''">
      <xsl:call-template name="then-clause"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="fs:else"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="fs:switch-error">
  <xsl:apply-templates select="fs:case-error[@value=$error][1]"/>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Resolution of the %{prefix} keyword                                        -->
<!-- ================================================================================ -->

<!-- Handles attributes which may start with the %{prefix} keyword-->
<xsl:template match="@action|@href|@src|@value" priority="1">
  <xsl:variable name="url" select="normalize-space(.)"/>
  <xsl:attribute name="{local-name()}">
    <xsl:choose>
      <xsl:when test="starts-with($url, '%{prefix}')">
        <xsl:variable name="rel-url" select="substring-after($url, '%{prefix}')"/>
        <xsl:value-of select="$url-prefix"/>
        <xsl:if test="not(starts-with($rel-url, '/'))">/</xsl:if>
        <xsl:value-of select="$rel-url"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$url"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Inserting referenced elements                                                -->
<!-- ================================================================================ -->

<xsl:template match="fs:insert">
  <xsl:apply-templates select="//*[@fs:id=current()/@ref]"/>
</xsl:template>


<!-- ================================================================================ -->
<!-- h1: Attribute values                                                             -->
<!-- ================================================================================ -->

<xsl:template match="@fs:value|@fs:href">
  <xsl:attribute name="{local-name()}">
    <xsl:call-template name="param-value">
      <xsl:with-param name="pos" select="substring-after(string(.), '$param')"/>
    </xsl:call-template>
  </xsl:attribute>
</xsl:template>


<!-- ================================================================================ -->
<!-- h1: Top bar                                                                      -->
<!-- ================================================================================ -->

<xsl:template match="fs:top-bar">
  <div class="top">
    <table class="layout">
      <tr>
        <td class="left" rowspan="2">
          <img src="{$url-prefix}/public/resources/mumie_logo.png" alt="Mumie-Logo"/>
        </td>
        <td class="middle" colspan="3">
          <span class="title">
            <xsl:call-template name="i18n-text">
              <xsl:with-param name="ref">top-bar-title</xsl:with-param>
            </xsl:call-template>
          </span><br/>
          <xsl:call-template name="i18n-text">
            <xsl:with-param name="ref">top-bar-subtitle</xsl:with-param>
          </xsl:call-template>
        </td>
        <td class="right" rowspan="2">
          <img src="{$url-prefix}/public/resources/logo.png" alt="Logo"/>
        </td>
      </tr>
    </table>
  </div>
</xsl:template>

</xsl:stylesheet>
