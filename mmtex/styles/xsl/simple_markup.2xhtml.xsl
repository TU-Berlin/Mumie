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
                xmlns:data="http://www.mumie.net/xml-namespace/mmtex/styles/xsl/data"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mtx data">
<!--
   Data block.
-->

<data xmlns="http://www.mumie.net/xml-namespace/mmtex/styles/xsl/data">
  <left-quote lang="en">"</left-quote>
  <right-quote lang="en">"</right-quote>
  <left-quote lang="de">&#8222;</left-quote>
  <right-quote lang="de">&#8220;</right-quote>
</data>

<!--
   Root node of the data block
-->

<xsl:variable name="simple-markup-data" select="document('')/xsl:stylesheet/data:data"/>


<xsl:template match="mtx:emph">
  <em>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </em>
</xsl:template>

<xsl:template match="mtx:code">
  <code>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </code>
</xsl:template>

<xsl:template match="mtx:keyb">
  <kbd>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </kbd>
</xsl:template>

<xsl:template match="mtx:var">
  <var>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </var>
</xsl:template>

<xsl:template match="mtx:file">
  <span>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">file</xsl:with-param>
    </xsl:call-template> 
    <xsl:apply-templates select="@*[local-name()!='class']"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="mtx:meta">
  <span>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">meta</xsl:with-param>
    </xsl:call-template> 
    <xsl:apply-templates select="@*[local-name()!='class']"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="mtx:optional">
  <span class="meta">[</span>
  <xsl:apply-templates/>
  <span class="meta">]</span>
</xsl:template>

<xsl:template match="mtx:alts">
  <xsl:variable name="suppress-parenthesis"
                select="local-name(..)='optional' and count(../*)=1 and count(../text())=0"/>
  <xsl:if test="not($suppress-parenthesis)">
    <span class="meta">(</span>
  </xsl:if>
  <xsl:for-each select="mtx:alt">
    <xsl:if test="position()!=1">
      <span class="meta">|</span>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:for-each>
  <xsl:if test="not($suppress-parenthesis)">
    <span class="meta">)</span>
  </xsl:if>
</xsl:template>

<xsl:template match="mtx:mark">
  <span>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">mark</xsl:with-param>
    </xsl:call-template> 
    <xsl:apply-templates select="@*[local-name()!='class']"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="mtx:break">
  <br/>
  <xsl:if test="@space">
    <xsl:call-template name="vertical-space"/>
  </xsl:if>
</xsl:template>

<xsl:template name="left-quote">
  <xsl:variable name="quote" select="$simple-markup-data/data:left-quote[@lang=$lang]"/>
  <xsl:choose>
    <xsl:when test="$quote">
      <xsl:value-of select="$quote"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'&quot;'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="right-quote">
  <xsl:variable name="quote" select="$simple-markup-data/data:right-quote[@lang=$lang]"/>
  <xsl:choose>
    <xsl:when test="$quote">
      <xsl:value-of select="$quote"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'&quot;'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="mtx:quoted">
  <xsl:call-template name="left-quote"/>
  <xsl:apply-templates/>
  <xsl:call-template name="right-quote"/>
</xsl:template>


</xsl:stylesheet>