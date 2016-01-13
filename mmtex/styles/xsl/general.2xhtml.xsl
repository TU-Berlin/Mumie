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
                xmlns="http://www.w3.org/1999/xhtml">


<!--
  Language
-->

<xsl:param name="lang">
  <xsl:choose>
    <xsl:when test="/*/language/@value">
      <xsl:value-of select="/*/language/@value"/>
    </xsl:when>
    <xsl:otherwise>en</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<!--
   Sets a "class" attribute if necessary. Expects up to two parameters:

     class       The class. Defaults to the "class" attribute of the current node.

     base-class  An additional class (see below). No default.

  If both $class and $base-class are present, the value of the attribute is
  "$base-class $class". If only $class or $base-class are present, the value of the
  attribute is "$class" or "$base-class", repsectively. If neither $class nor $base-class is
  present, the attribute is not set.
-->

<xsl:template name="class">
  <xsl:param name="base-class"/>
  <xsl:param name="class" select="@class"/>
  <xsl:attribute name="class">
    <xsl:choose>
      <xsl:when test="$base-class and $class">
        <xsl:value-of select="concat($base-class,' ',$class)"/>
      </xsl:when>
      <xsl:when test="$class">
        <xsl:value-of select="$class"/>
      </xsl:when>
      <xsl:when test="$base-class">
        <xsl:value-of select="$base-class"/>
      </xsl:when>
    </xsl:choose>
  </xsl:attribute>
</xsl:template>

<xsl:template match="@class">
  <xsl:copy/>
</xsl:template>

<!--
   Handles the event attributes "onclick", "ondblclick", etc. Simply copies them.
-->

<xsl:template match="@onclick|@ondblclick|@onmousedown|@onmouseup|@onmouseover|
                     @onmousemove|@onmouseout|@onkeypress|@onkeydown|@onkeyup">
  <xsl:copy/>
</xsl:template>

<!--
   Handles the "id" attribute. Simply copies it.
-->

<xsl:template match="@id">
  <xsl:copy/>
</xsl:template>

<!--
   Handles the "ballon" attribute. Transforms it into a "title" attribute.
-->

<xsl:template match="@balloon">
  <xsl:attribute name="title">
    <xsl:value-of select="."/>
  </xsl:attribute>
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
</xsl:template>

<!--
   Sets an "href" attribute of necessary. Recognizes one parameter:

     href  The URI this href attribute should point to. Defaults to the
           "href" attribute of the current node.

-->

<xsl:template name="href">
  <xsl:param name="href" select="@href"/> 
  <xsl:if test="$href">
    <xsl:attribute name="href">
      <xsl:value-of select="$href"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!--
  Inserts vertical space.
-->

<xsl:template name="vertical-space">
  <xsl:param name="space" select="@space"/>
  <div>
    <xsl:attribute name="style">
      <xsl:value-of select="concat('height:',$space)"/>
    </xsl:attribute>
  </div>
</xsl:template>

<!--
   Ignores attributes that should not be handled by a direct match. Such attributes may
   be processed in a different way.
-->

<xsl:template match="@*" priority="-1">
  <!-- Ignored -->
</xsl:template>

</xsl:stylesheet>