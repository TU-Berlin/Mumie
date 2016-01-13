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
                xmlns:x-latex="xalan://net.mumie.xslt.ext.LaTeXUtil">

<!--
  Replaces any sequence of whitespace by a single space (' ') character, quotes the
  special characters {}$&#_^~\".
-->

<xsl:template match="text()">
  <xsl:value-of select="x-latex:processText(.)"/>
</xsl:template>

<!--
   Composes an URI. Expects one parameter:

     spec  A description of the URI in a certain format; see below. Defaults to the "href"
           attribute of the current node.

   The format of the description is:

     $mode:[$uri][#$anchor]

   $mode is keyword that specifies how $spec must be processed, $uri the name of the file
   to where the URI points, and $anchor an anchor within this file. "$uri" and  "#$anchor"
   are both optional, as indicated by the square brackets. $mode may take the following
   values:

     normal 
           The part of $spec after "$mode:" is taken as the URI.

     mmtex
           If $uri is not lacking, the URI is composed of $uri, a dot, the value of the
           global parameter $output-suffix, and, optinally, a "#" and $anchor; i.e.,  

             $uri.$output-suffix[#$anchor]

           If $uri is lacking but $anchor is present, the URI is

             #$anchor

           If neither $uri nor $anchor is present, nothing is returned for the URI.

     mailto
           Indicates that the part of $spec after "mailto:" is an email address. Entire
           $spec is returned for the URI.
-->

<xsl:template name="uri">
  <xsl:param name="spec" select="@href"/>
  <xsl:variable name="mode" select="substring-before($spec,':')"/>
  <xsl:variable name="uri-plus-anchor" select="substring-after($spec,':')"/>
  <xsl:variable name="uri">
    <xsl:choose>
      <xsl:when test="contains($uri-plus-anchor,'#')">
        <xsl:value-of select="substring-before($uri-plus-anchor,'#')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$uri-plus-anchor"/>
      </xsl:otherwise>    
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="anchor" select="substring-after($uri-plus-anchor,'#')"/>
  <xsl:variable name="uri-text">
    <xsl:choose>
      <xsl:when test="$mode='normal'">
        <xsl:value-of select="$uri-plus-anchor"/>
      </xsl:when>
      <xsl:when test="$mode='mmtex'">
        <xsl:choose>
          <xsl:when test="string-length($uri)&gt;0 and string-length($anchor)&gt;0">
            <xsl:value-of select="concat($uri,'.',$output-suffix,'#',$anchor)"/>
          </xsl:when>
          <xsl:when test="string-length($uri)&gt;0">
            <xsl:value-of select="concat($uri,'.',$output-suffix)"/>
          </xsl:when>
          <xsl:when test="string-length($anchor)&gt;0">
            <xsl:value-of select="concat('#',$anchor)"/>
          </xsl:when>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$mode='mailto'">
        <xsl:value-of select="$spec"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="error">
          <xsl:with-param name="message"
                          select="concat('Unknown uri processing mode: ',$mode)"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:text>\mmtexUri{</xsl:text>
  <xsl:value-of select="x-latex:processText($uri-text)"/>
  <xsl:text>}</xsl:text>
</xsl:template>

</xsl:stylesheet>