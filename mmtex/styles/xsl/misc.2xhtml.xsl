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
  Language
-->

<xsl:param name="lang">
  <xsl:choose>
    <xsl:when test="/*/mtx:language/@value">
      <xsl:value-of select="/*/mtx:language/@value"/>
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
  <xsl:param name="disable-base-class" select="@disable-base-class"/>
  <xsl:param name="class" select="@class"/>
  <xsl:attribute name="class">
    <xsl:choose>
      <xsl:when test="$disable-base-class='yes'">
        <xsl:if test="$class">
          <xsl:value-of select="$class"/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
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
      </xsl:otherwise>
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