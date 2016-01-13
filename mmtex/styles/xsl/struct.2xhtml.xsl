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

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mtx">

<!--
  Table of contents headline
-->

<xsl:param name="toc-title">
  <xsl:choose>
    <xsl:when test="$lang='en'">Contents</xsl:when>
    <xsl:when test="$lang='de'">Inhalt</xsl:when>
    <xsl:otherwise>Contents</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<!--
  Top anchor id
-->

<xsl:param name="top-anchor">TOP</xsl:param>

<!--
  Top links
-->
<xsl:param name="with-top-links">false</xsl:param>

<xsl:param name="top-link">
  <xsl:choose>
    <xsl:when test="$lang='en'">[top]</xsl:when>
    <xsl:when test="$lang='de'">[Seitenanfang]</xsl:when>
    <xsl:otherwise>[top]</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<!--
  Table of contents
-->

<xsl:template match="mtx:toc">
  <div class="toc">
    <h2>
      <xsl:value-of select="$toc-title"/>
    </h2>
    <ul>
      <xsl:apply-templates/>
    </ul>
  </div>
</xsl:template>

<xsl:template match="mtx:toc-item[@class='section']">
  <xsl:choose>
    <xsl:when test="./mtx:toc-item">
      <li class="section">
        <xsl:apply-templates select="./mtx:toc-entry"/>
        <ul>
          <xsl:apply-templates select="./mtx:toc-item"/>
        </ul>
      </li>
    </xsl:when>
    <xsl:otherwise>
      <li class="section">
        <xsl:apply-templates select="./mtx:toc-entry"/>
      </li>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="mtx:toc-item/mtx:toc-entry[../@class='section']">
  <xsl:if test="../@no">
    <span class="number"><xsl:number value="../@no" format="1 "/></span>
  </xsl:if>
  <a>
    <xsl:call-template name="href">
      <xsl:with-param name="href" select="../@href"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="mtx:toc-item[@class='subsection']">
  <xsl:choose>
    <xsl:when test="./mtx:toc-item">
      <li class="subsection">
        <xsl:apply-templates select="./mtx:toc-entry"/>
          <ul>
            <xsl:apply-templates select="./mtx:toc-item"/>
          </ul>
      </li>
    </xsl:when>
    <xsl:otherwise>
      <li class="subsection">
        <xsl:apply-templates select="./mtx:toc-entry"/>
      </li>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="mtx:toc-item/mtx:toc-entry[../@class='subsection']">
  <xsl:if test="../../@no and ../@no">
    <span class="number">
      <xsl:number value="../../@no" format="1."/>
      <xsl:number value="../@no" format="1 "/>
    </span>
  </xsl:if>
  <a>
    <xsl:call-template name="href">
      <xsl:with-param name="href" select="../@href"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="mtx:toc-item[@class='subsubsection']">
  <xsl:choose>
    <xsl:when test="./mtx:toc-item">
      <li class="subsubsection">
        <xsl:apply-templates select="./mtx:toc-entry"/>
          <ul>
            <xsl:apply-templates select="./mtx:toc-item"/>
          </ul>
      </li>
    </xsl:when>
    <xsl:otherwise>
      <li class="subsubsection">
        <xsl:apply-templates select="./mtx:toc-entry"/>
      </li>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="mtx:toc-item/mtx:toc-entry[../@class='subsubsection']">
  <xsl:if test="../../../@no and ../../@no and ../@no">
    <span class="number">
      <xsl:number value="../../../@no" format="1."/>
      <xsl:number value="../../@no" format="1."/>
      <xsl:number value="../@no" format="1 "/>
    </span>
  </xsl:if>
  <a>
    <xsl:call-template name="href">
      <xsl:with-param name="href" select="../@href"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<!--
  Title and subtitle
-->

<xsl:template match="/*/mtx:document/mtx:title">
  <div class="title">
    <h1>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </h1>
    <xsl:apply-templates select="/*/document/subtitle" mode="title"/>
  </div>
</xsl:template>

<xsl:template match="/*/mtx:document/mtx:subtitle" mode="title">
  <h1 class="sub">
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </h1>
</xsl:template>

<xsl:template match="/*/mtx:document/mtx:subtitle">
  <!-- Ignore in normal mode -->
</xsl:template>

<xsl:template match="mtx:section/@no|mtx:subsection/@no|mtx:subsubsection/@no">
  <!-- Ignore in normal mode -->
</xsl:template>

<!--
  Author, version, etc.
-->

<xsl:template match="mtx:authors">
  <div class="authors">
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="mtx:author">
  <div class="author">
    <xsl:apply-templates/>
    <xsl:if test="@email">
      <xsl:text> </xsl:text>
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="concat('mailto:',@email)"/>
        </xsl:attribute>
        <xsl:value-of select="@email"/>
      </a>
    </xsl:if>
  </div>
</xsl:template>

<xsl:template match="mtx:version">
  <div class="version">
    <xsl:apply-templates/>
  </div>
</xsl:template>

<!--
  Paragraphs
-->

<xsl:template match="mtx:par">
  <p>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<!--
  Structuring units (section, subsection, subsubsection)
-->

<xsl:template name="top-link">
  <xsl:if test="$with-top-links='true' or $with-top-links='yes'">
    <a class="top-link" href="#{top-anchor}">
      <xsl:value-of select="$top-link"/>
    </a>
  </xsl:if>
</xsl:template>

<xsl:template match="mtx:section|mtx:subsection|mtx:subsubsection">
  <div>
    <xsl:attribute name="class">
      <xsl:value-of select="local-name()"/>
    </xsl:attribute>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="mtx:section/mtx:headline">
  <h2>
    <xsl:apply-templates select="@*"/>
    <xsl:if test="../@no">
      <span class="section-no"><xsl:number value="../@no" format="1 "/></span>
    </xsl:if>
    <xsl:apply-templates/>
    <xsl:call-template name="top-link"/>
  </h2>
</xsl:template>

<xsl:template match="mtx:subsection/mtx:headline">
  <h3>
    <xsl:apply-templates select="@*"/>
    <xsl:if test="../../@no and ../@no">
      <span class="subsection-no">
        <xsl:number value="../../@no" format="1."/>
        <xsl:number value="../@no" format="1 "/>
      </span>
    </xsl:if>
    <xsl:apply-templates/>
    <xsl:call-template name="top-link"/>
  </h3>
</xsl:template>

<xsl:template match="mtx:subsubsection/mtx:headline">
  <h4>
    <xsl:apply-templates select="@*"/>
    <xsl:if test="../../../@no and ../../@no and ../@no">
      <span class="subsubsection-no">
        <xsl:number value="../../../@no" format="1."/>
        <xsl:number value="../../@no" format="1."/>
        <xsl:number value="../@no" format="1 "/>
      </span>
    </xsl:if>
    <xsl:apply-templates/>
    <xsl:call-template name="top-link"/>
  </h4>
</xsl:template>

</xsl:stylesheet>