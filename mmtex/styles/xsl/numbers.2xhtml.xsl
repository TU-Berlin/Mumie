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
  <footnote-symbol no="1">&#8727;</footnote-symbol>        <!-- asterisk -->
  <footnote-symbol no="2">&#8224;</footnote-symbol>        <!-- dagger -->
  <footnote-symbol no="3">&#8225;</footnote-symbol>        <!-- double dagger -->
  <footnote-symbol no="4">&#167;</footnote-symbol>         <!-- section -->
  <footnote-symbol no="5">&#182;</footnote-symbol>         <!-- paragraph -->
  <footnote-symbol no="6">&#x02225;</footnote-symbol>      <!-- double vertical bar -->
  <footnote-symbol no="7">&#8727;&#8727;</footnote-symbol> <!-- two  asterisks -->
  <footnote-symbol no="8">&#8224;&#8224;</footnote-symbol> <!-- two daggers -->
  <footnote-symbol no="9">&#8225;&#8225;</footnote-symbol> <!-- two double daggers -->
</data>

<!--
   Root node of the data block
-->

<xsl:variable name="numbers-data" select="document('')/xsl:stylesheet/data:data"/>

<!--
  Formats a number.
-->

<xsl:template match="mtx:number">
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
      <xsl:value-of select="$numbers-data/data:footnote-symbol[@no=current()/@value]"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:number value="@value" format="@format"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>