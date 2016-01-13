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

<!DOCTYPE commands
  [
    <!ENTITY br "&#xA;">
    <!ENTITY sp "    ">
  ]
>
  
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mumie="http://www.mumie.net/xml-namespace/mmtex-xsl"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mumie">


<xsl:import href="config.xsl"/>


<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>






<xsl:strip-space elements="*"/>

<!-- helper templates -->
<xsl:template name="main" match="library">
<table id="top">
  <thead class="title">
    <tr>
      <td>Library:&#160;&#160;&#160;<xsl:value-of select="@name"/></td>
    </tr>
   </thead>
   <thead class="content">
    <tr>
      <td>Content</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td class="title">1. Commands</td>
    </tr>   
    <tr>
      <td class="content"><xsl:call-template name="com-view"/></td>
    </tr>
    <tr>
      <td class="title">2. Environments</td>
    </tr>
    <tr>
      <td class="content"><xsl:call-template name="env-view"/></td>
    </tr>
    <tr>
      <td class="list">
        <xsl:call-template name="com-content"/>
        <xsl:call-template name="env-content"/>
      </td>
    </tr>
   </tbody>
</table>
</xsl:template>

<!-- generate a list of all commands with anchors -->
<xsl:template name="com-view">
  <xsl:for-each select="command">
  <xsl:sort select="@name" order="ascending"/>
    <xsl:text>1.</xsl:text><xsl:number value="position()" format="01"/>&#160;&#160;&#160;
    <a>
      <xsl:attribute name="href">
        <xsl:text>#1.</xsl:text><xsl:number value="position()" format="1"/>
      </xsl:attribute>
      <xsl:value-of select="@name"/>
    </a><br/>
  </xsl:for-each>
</xsl:template>

<xsl:template name="env-view">
  <xsl:for-each select="environment">
  <xsl:sort select="@name" order="ascending"/>
    <xsl:text>2.</xsl:text><xsl:number value="position()" format="01"/>&#160;&#160;&#160;
    <a>
      <xsl:attribute name="href">
        <xsl:text>#2.</xsl:text><xsl:number value="position()" format="1"/>
      </xsl:attribute>
      <xsl:value-of select="@name"/>
    </a><br/>
  </xsl:for-each>
</xsl:template>

<!-- generate a list of all commands with arguments and description -->
<xsl:template name="com-content">
  <xsl:for-each select="command">
  <xsl:sort select="@name" order="ascending"/>
    <tr>
      <td class="command">
        <a href="#top">top</a><br/> 
        <a>
          <xsl:attribute name="id">
            <xsl:text>1.</xsl:text><xsl:number value="position()" format="1"/>
          </xsl:attribute>
          <xsl:text>1.</xsl:text><xsl:number value="position()" format="1"/><xsl:text>      </xsl:text>
          <xsl:value-of select="@name"/><br/>
        </a>
      </td>
    </tr>
    <tr>
      <td>
        <xsl:text>\</xsl:text><code><xsl:value-of select="@name"/></code>
        <xsl:if test="./descendant::*[local-name(.)='opt-args']">
          <xsl:text>  [</xsl:text><var class="command"><xsl:value-of select="opt-args/arg/@name"/></var><xsl:text>]</xsl:text>
        </xsl:if>
        <xsl:if test="./descendant::*[local-name(.)='man-args']">
          <xsl:text>  {</xsl:text><var class="command"><xsl:value-of select="man-args/arg/@name"/></var><xsl:text>}</xsl:text>
        </xsl:if>
      </td>
    </tr>
    <tr>
      <td class="param"> 
        <xsl:text>Parameters:</xsl:text>
      </td>
    </tr>
    <tr>
      <td>
        <xsl:choose>
          <xsl:when test="./descendant::*[local-name(.)='opt-args']">
            <var class="command"><xsl:value-of select="opt-args/arg/@name"/></var><xsl:text> - </xsl:text><xsl:value-of select="opt-args/arg[@name]"/><br/>
          </xsl:when>
          <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="./descendant::*[local-name(.)='man-args']">
            <var class="command"><xsl:value-of select="man-args/arg/@name"/></var><xsl:text> - </xsl:text><xsl:value-of select="man-args/arg[@name]"/>
          </xsl:when>
          <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
    <tr>
      <td> 
        <p><xsl:value-of select="description"/></p>
      </td>
    </tr>
  </xsl:for-each>
</xsl:template>

<!-- generate a list of all environments with arguments and description -->
<xsl:template name="env-content">
  <xsl:for-each select="environment">
  <xsl:sort select="@name" order="ascending"/>
    <tr>
      <td class="command">
        <a href="#top">top</a><br/> 
        <a>
          <xsl:attribute name="id">
            <xsl:text>2.</xsl:text><xsl:number value="position()" format="1"/>
          </xsl:attribute>
          <xsl:text>2.</xsl:text><xsl:number value="position()" format="1"/><xsl:text>      </xsl:text>
          <xsl:value-of select="@name"/><br/>
        </a>
      </td>
    </tr>
    <tr>
      <td>
        <xsl:text>\begin{</xsl:text><code><xsl:value-of select="@name"/></code><xsl:text>}...\end{</xsl:text><code><xsl:value-of select="@name"/></code><xsl:text>}</xsl:text>
        <xsl:if test="./descendant::*[local-name(.)='opt-args']">
          <xsl:text>  [</xsl:text><var class="command"><xsl:value-of select="opt-args/arg/@name"/></var><xsl:text>]</xsl:text>
        </xsl:if>
        <xsl:if test="./descendant::*[local-name(.)='man-args']">
          <xsl:text>  {</xsl:text><var class="command"><xsl:value-of select="man-args/arg/@name"/></var><xsl:text>}</xsl:text>
        </xsl:if>
      </td>
    </tr>
    <tr>
      <td class="param"> 
        <xsl:text>Parameters:</xsl:text>
      </td>
    </tr>
    <tr>
      <td>
        <xsl:choose>
          <xsl:when test="./descendant::*[local-name(.)='opt-args']">
            <var class="command"><xsl:value-of select="opt-args/arg/@name"/></var><xsl:text> - </xsl:text><xsl:value-of select="opt-args/arg[@name]"/><br/>
          </xsl:when>
          <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="./descendant::*[local-name(.)='man-args']">
            <var class="command"><xsl:value-of select="man-args/arg/@name"/></var><xsl:text> - </xsl:text><xsl:value-of select="man-args/arg[@name]"/>
          </xsl:when>
          <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
    <tr>
      <td> 
        <p><xsl:value-of select="description"/></p>
      </td>
    </tr>
  </xsl:for-each>
</xsl:template>


<xsl:template match="/">
  <html>
    <head>
      <title>XDOC - Library</title>
      <link rel="stylesheet" type="text/css">
        <xsl:attribute name="href"><xsl:value-of select="'../css/xdoc.css'"/></xsl:attribute>
      </link>
    </head>
    <body>
      <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>




</xsl:stylesheet>