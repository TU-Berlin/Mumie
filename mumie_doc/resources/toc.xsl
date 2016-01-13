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

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>


<!-- Name of the documentation root directory: -->
<xsl:param name="doc-dir">doc</xsl:param>

<!-- Prefix to add to URLs -->
<xsl:param name="url-prefix" select="$doc-dir"/>

<!-- Language (as language code) -->
<xsl:param name="lang">de</xsl:param>

<!-- Name and id of the main frame -->
<xsl:param name="main-frame">main</xsl:param>

<!-- Styles for the table of contents -->
<xsl:param name="styles">
  body
    {
      font-family: Verdana, Arial, Helvetica, sans-serif;
      font-style: normal;
      font-variant: normal;
      font-size: 12pt;
      font-weight: normal;
      padding-left: 10px;
      padding-top: 10px;
    }

  div.head
    {
      margin: 1ex 0px 1ex 1em;
      /* text-align: center; */
    }

  h1
    { 
      font-weight: bold; 
      font-size: 22pt;
      margin: 2px 0px 2px 0px;
      padding: 0px 0px 0px 0px;
    }

  h2
    { 
      font-weight: bold; 
      font-size: 18pt;
      margin: 2px 0px 2px 0px;
      padding: 0px 0px 0px 0px;
    }

  h3
    { 
      font-weight: bold; 
      font-size: 16pt;
      margin: 0.75ex 0px 0.75ex 0px;
      padding: 0px 0px 0px 0px;
    }

  h4
    { 
      font-weight: bold; 
      font-size: 14pt;
      margin: 0.5ex 0px 0.75ex 0px;
      padding: 0px 0px 0px 0px;
    }

  a:link, a:visited
    { 
      color: #0000ff;
    }

  a:hover
    { 
      color: #00008b;
      background-color: #c1ffc1;
    }

  a:active
    { 
      color: #ff0000;
      background-color: #9aff9a;
    }

  ul.toc
    { 
      list-style-type: none;
      margin: 1ex 0px 1ex 1em;
      padding: 0px 0px 0px 0px;
    }

  li.part > ul.toc, li.subpart > ul.toc
    {
      margin-top: 2px;
    }

  li.part
    {
      margin: 3ex 0px 3ex 0px;
    }

  li.subpart
    {
      margin: 1.5ex 0px 1ex 0px;
    }

  li.entry
    {
      margin: 2px 0px 2px 0px;
    }
</xsl:param>

<xsl:template match="/">
  <html>
    <head>
      <style type="text/css">
        <xsl:value-of select="$styles"/>
      </style>
    </head>
    <body>
      <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<xsl:template match="toc">
  <h1>
    <xsl:apply-templates select="name"/>
  </h1>
  <ul class="toc">
    <xsl:apply-templates select="entries"/>
  </ul>
</xsl:template>

<xsl:template match="name[@lang=$lang]">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="name[@lang!=$lang]">
  <!-- Ignored -->
</xsl:template>

<xsl:template match="part">
  <li class="part">
    <h2>
      <xsl:apply-templates select="name"/>
    </h2>
    <ul class="toc">
      <xsl:apply-templates select="entries"/>
    </ul>
  </li>
</xsl:template>

<xsl:template match="subpart">
  <li class="subpart">
    <h3>
      <xsl:apply-templates select="name"/>
    </h3>
    <ul class="toc">
      <xsl:apply-templates select="entries"/>
    </ul>
  </li>
</xsl:template>

<xsl:template match="entry">
  <li class="entry">
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="concat($url-prefix, '/', @src, '_', $lang, '.xhtml')"/>
      </xsl:attribute>
      <xsl:attribute name="target">
        <xsl:value-of select="$main-frame"/>
      </xsl:attribute>
      <xsl:variable name="xml-src" select="concat($doc-dir, '/', @src, '_', $lang, '.xml')"/>
      <xsl:apply-templates select="document($xml-src)/generic/document/title"/>
    </a>
  </li>
</xsl:template>

</xsl:stylesheet>