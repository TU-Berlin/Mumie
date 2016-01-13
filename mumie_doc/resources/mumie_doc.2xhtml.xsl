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
   <!ENTITY br  "&#xA;">
   <!ENTITY sp  "  ">
  ]
>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">

<xsl:import href="config.xsl"/>

<xsl:import href="generic.2xhtml.xsl"/>

<xsl:import href="box.2xhtml.xsl"/>
<xsl:import href="misc.2xhtml.xsl"/>
<xsl:import href="list.2xhtml.xsl"/>
<xsl:import href="math.2xhtml.xsl"/>
<xsl:import href="multimedia.2xhtml.xsl"/>
<xsl:import href="numbers.2xhtml.xsl"/>
<xsl:import href="preformatted.2xhtml.xsl"/>
<xsl:import href="struct.2xhtml.xsl"/>
<xsl:import href="simple_markup.2xhtml.xsl"/>
<xsl:import href="table.2xhtml.xsl"/>
<xsl:import href="hyperlink.2xhtml.xsl"/>
<xsl:import href="horizontal_float.2xhtml.xsl"/>

<!--
   ==========================================================================================
   Lists
   ==========================================================================================
-->

<xsl:template name="numbered-list">
  <xsl:param name="prefix"/>
  <table class="list">
    <xsl:for-each select="item">
      <xsl:variable name="full-number" select="concat($prefix,'.',@no)"/>
      <tr>
        <td class="label">
          <xsl:choose>
            <xsl:when test="label">
              <xsl:apply-templates select="label"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$full-number"/>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <td>
          <xsl:for-each select="content/*">
            <xsl:choose>
              <xsl:when test="local-name()='list' and @numbering='yes'">
                <xsl:call-template name="numbered-list">
                  <xsl:with-param name="prefix" select="$full-number"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </td>
      </tr>
    </xsl:for-each>
  </table>
</xsl:template>

<xsl:template match="section/list[@numbering='yes']">
  <xsl:call-template name="numbered-list">
    <xsl:with-param name="prefix" select="../@no"/>
  </xsl:call-template>
</xsl:template>

<xsl:template match="subsection/list[@numbering='yes']">
  <xsl:call-template name="numbered-list">
    <xsl:with-param name="prefix" select="concat(ancestor::section/@no,'.',../@no)"/>
  </xsl:call-template>
</xsl:template>

<!--
 <xsl:template match="list/item/content/par[position()=1]">
  <p style="margin-top:0px">
    <xsl:apply-templates/>
  </p>
</xsl:template>
-->

</xsl:stylesheet>

