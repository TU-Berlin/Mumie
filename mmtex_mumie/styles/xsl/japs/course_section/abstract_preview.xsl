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
   Authors:  Tilman Rassy <rassy@math.tu-berlin.de>,
             Christian Ruppert <ruppert@math.tu-berlin.de>

   $Id: abstract_preview.xsl,v 1.4 2007/07/11 15:53:11 grudzin Exp $

-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mumie="http://www.mumie.net/xml-namespace/mmtex-xsl"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mumie">

<xsl:import href="../../mmtex4japs_config.xsl"/>
<xsl:import href="../../math.2xhtml.xsl"/>

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>

<xsl:strip-space elements="a h1 h2 h3 h4 h5 h6"/>

<!--
   ==========================================================================================
   Top level parameters and variables
   ==========================================================================================
-->

<!--
   The language the source is written in.
-->

<xsl:param name="language" select="'de'"/>

<!--
   The CSS stylesheet
-->

<xsl:param name="css-stylesheet" select="concat($mmtex4japs.css-dir,'/abstract_preview.css')"/>

<!--
   ==========================================================================================
   Data
   ==========================================================================================
-->

<!--
   The data block itself.
-->

<mumie:data>
  <mumie:footnote-symbols>
    <mumie:footnote-symbol>&#8727;</mumie:footnote-symbol>        <!-- asterisk -->
    <mumie:footnote-symbol>&#8224;</mumie:footnote-symbol>        <!-- dagger -->
    <mumie:footnote-symbol>&#8225;</mumie:footnote-symbol>        <!-- double dagger -->
    <mumie:footnote-symbol>&#167;</mumie:footnote-symbol>         <!-- section -->
    <mumie:footnote-symbol>&#182;</mumie:footnote-symbol>         <!-- paragraph -->
    <mumie:footnote-symbol>&#x02225;</mumie:footnote-symbol>      <!-- double vertical bar -->
    <mumie:footnote-symbol>&#8727;&#8727;</mumie:footnote-symbol> <!-- two  asterisks -->
    <mumie:footnote-symbol>&#8224;&#8224;</mumie:footnote-symbol> <!-- two daggers -->
    <mumie:footnote-symbol>&#8225;&#8225;</mumie:footnote-symbol> <!-- two double daggers -->
  </mumie:footnote-symbols>
</mumie:data>

<!--
   Root node of the data block
-->

<xsl:variable name="data" select="document('')/xsl:stylesheet/mumie:data"/>

<!--
   ==========================================================================================
   General structure
   ==========================================================================================
-->

<xsl:template match="/">
  <html>
    <head>
      <title>Course Section Abstract Preview</title>
      <link rel="stylesheet" type="text/css" href="{$css-stylesheet}"/>
    </head>
    <body>
      <xsl:apply-templates select="/abstract"/>
      <!-- For online version: select="/csection/abstract"/> -->
    </body>
  </html>
</xsl:template>

<!--
   ==========================================================================================
   Structuring (incl. headlines)
   ==========================================================================================
-->

<xsl:template match="/abstract">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="par">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="section">
  <div class="section">
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="section/headline">
  <h2>
    <xsl:apply-templates/>
  </h2>
</xsl:template>

<!--
   ==========================================================================================
                                      Simple markup
   ==========================================================================================
-->

<xsl:template match="emph">
  <em>
    <xsl:apply-templates/>
  </em>
</xsl:template>

<xsl:template match="code">
  <code>
  </code>
</xsl:template>

<xsl:template match="keyb">
  <kbd>
    <xsl:apply-templates/>
  </kbd>
</xsl:template>

<xsl:template match="var">
  <var>
    <xsl:apply-templates/>
  </var>
</xsl:template>

<xsl:template match="file">
  <span class="file">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="plhld">
  <span class="plhld">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="quoted">
  <xsl:text>&#8222;</xsl:text>
  <xsl:apply-templates/>
  <xsl:text>&#8220;</xsl:text>
</xsl:template>

<xsl:template match="meta">
  <span class="meta">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="optional">
  <span class="meta">[</span>
  <xsl:apply-templates/>
  <span class="meta">]</span>
</xsl:template>

<xsl:template match="alts">
  <xsl:variable name="suppress-parenthesis"
                select="local-name(..)='optional' and count(../*)=1 and count(../text())=0"/>
  <xsl:if test="not($suppress-parenthesis)">
    <span class="meta">(</span>
  </xsl:if>
  <xsl:for-each select="alt">
    <xsl:if test="position()!=1">
      <span class="meta">|</span>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:for-each>
  <xsl:if test="not($suppress-parenthesis)">
    <span class="meta">)</span>
  </xsl:if>
</xsl:template>

<!--
   ==========================================================================================
   Preformatted and verbatim Text                    
   ==========================================================================================
-->

<xsl:template match="preformatted|verbatim">
  <pre class="block">
    <xsl:apply-templates/>
  </pre>
</xsl:template>

<xsl:template match="pref">
  <span class="pref">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="verb">
  <span class="verb">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!--
   ==========================================================================================
   Lists
   ==========================================================================================
-->

<xsl:template match="list">
  <xsl:choose>
    <xsl:when test="@numbering='no'">
      <ul>
        <xsl:copy-of select="@class"/>
        <xsl:apply-templates/>
      </ul>
    </xsl:when>
    <xsl:when test="@numbering='yes'">
      <ol>
        <xsl:copy-of select="@class"/>
        <xsl:apply-templates/>
      </ol>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="list/item">
  <li>
    <xsl:if test="label">
      <xsl:attribute name="style">
        <xsl:value-of select="'list-style-type:none'"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </li>
</xsl:template>

<xsl:template match="list/item[label]/content/par[position()=1]">
  <p>
    <xsl:apply-templates select="../../label" mode="item-label"/>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="list/item/label">
  <!--
    Only processed in mode "item-label"
  -->
</xsl:template>

<xsl:template match="list/item/label" mode="item-label">
  <span class="label">
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="list/item/content">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="description-list">
  <dl>
    <xsl:apply-templates/>
  </dl>
</xsl:template>

<xsl:template match="description-list/item">
  <dt>
    <xsl:if test="label">
      <xsl:apply-templates select="label"/>    
    </xsl:if>
  </dt>
  <dd>
    <xsl:apply-templates select="content"/>    
  </dd>
</xsl:template>

<!--
   ==========================================================================================
   Tables
   ==========================================================================================
-->

<xsl:template match="table">
  <table class="genuine">
    <xsl:copy-of select="@align|@valign"/>
    <xsl:apply-templates select="./thead"/>
    <xsl:apply-templates select="./tfoot"/>
    <xsl:apply-templates select="./tbody"/>
  </table>
</xsl:template>

<xsl:template match="table/thead">
  <thead>
    <xsl:apply-templates/>
  </thead>
</xsl:template>

<xsl:template match="table/tbody">
  <tbody>
    <xsl:apply-templates/>
  </tbody>
</xsl:template>
  
<xsl:template match="table/tfoot">
  <tfoot>
    <xsl:apply-templates/>
  </tfoot>
</xsl:template>

<xsl:template match="trow">
  <tr>
    <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="tcell">
  <td>
    <xsl:copy-of select="@rowspan|@colspan|@align|@valign"/>
    <xsl:apply-templates/>
  </td>
</xsl:template>

<!--
   ==========================================================================================
   Numbers
   ==========================================================================================
-->

<xsl:template match="number">
  <xsl:choose>
    <xsl:when test="@format='arabic'">
      <xsl:number value="@value" format="1"/>
    </xsl:when>
    <xsl:when test="@format='roman'">
      <xsl:number value="@value" format="i"/>
    </xsl:when>
    <xsl:when test="@format='Roman'">
      <xsl:number value="@value" format="I"/>
    </xsl:when>
    <xsl:when test="@format='alph'">
      <xsl:number value="@value" format="a"/>
    </xsl:when>
    <xsl:when test="@format='Alph'">
      <xsl:number value="@value" format="A"/>
    </xsl:when>
    <xsl:when test="@format='footnote-symbol'">
      <xsl:value-of select="$data/mumie:footnote-symbols/mumie:footnote-symbol[position()=current()/@value]/text()"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:number value="@value" format="@format"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>

