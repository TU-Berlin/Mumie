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
   <!ENTITY br             "&#xA;">
   <!ENTITY branch         "&#x251C;&#x2500;">
   <!ENTITY final-branch   "&#x2514;&#x2500;">
   <!ENTITY cont           "&#x2502; ">
   <!ENTITY void           "  ">
   <!ENTITY left-space     "  ">
   <!ENTITY offset         " ">
  ]
>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex"
                xmlns:data="http://www.mumie.net/xml-namespace/mmtex/styles/xsl/data"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mtx data">

<xsl:template match="mtx:tree">
  <pre class="tree">
    <xsl:apply-templates select="mtx:node">
      <xsl:with-param name="indent" select="'&left-space;&void;'"/>
      <xsl:with-param name="child-indent" select="'&left-space;&void;&offset;'"/>
    </xsl:apply-templates>
  </pre>
</xsl:template>

<xsl:template match="mtx:node">
  <xsl:param name="indent"/>
  <xsl:param name="child-indent"/>
  <div class="line">
    <span class="indent"><xsl:value-of select="$indent"/></span>
    <xsl:apply-templates select="mtx:label"/>
  </div>
  <xsl:for-each select="mtx:children/*">
    <xsl:choose>
      <xsl:when test="position() = 1">
        <div class="sep"><xsl:value-of select="$child-indent"/></div>
      </xsl:when>
      <xsl:otherwise>
        <div class="sep"><xsl:value-of select="concat($child-indent, '&cont;')"/></div>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="position() = last()">
        <xsl:apply-templates select=".">
          <xsl:with-param name="indent" select="concat($child-indent, '&final-branch;')"/>
          <xsl:with-param name="child-indent" select="concat($child-indent, '&void;&offset;')"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select=".">
          <xsl:with-param name="indent" select="concat($child-indent, '&branch;')"/>
          <xsl:with-param name="child-indent" select="concat($child-indent, '&cont;&offset;')"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template match="mtx:node/mtx:label">
  <span class="label"><xsl:apply-templates/></span>
</xsl:template>

</xsl:stylesheet>
