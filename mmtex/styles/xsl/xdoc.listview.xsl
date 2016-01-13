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
                xmlns:mumie="http://www.mumie.net/xml-namespace/mmtex-xsl"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mumie">


<xsl:import href="xdoc.xsl"/>


<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>


<xsl:strip-space elements="*"/>

<xsl:param name="css-stylesheet"><xsl:value-of select="$css_dir"/>/xdoc.css</xsl:param>




<!--====================================================-->
<!--                                                    -->
<!-- List view of XDOCS, common templates are located   -->
<!-- in xdoc.xsl                                        -->
<!--                                                    -->
<!--====================================================-->



<!-- List of commands either for a library or a dcl -->

<xsl:template match="/">
  <html>
    <head>
      <title>XDOC</title>
      <link rel="stylesheet" type="text/css">
        <xsl:attribute name="href"><xsl:copy-of select="$css-stylesheet"/></xsl:attribute>
      </link>
    </head>
    <body>
      <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>

<xsl:template match="documentclass">
<table>
  <tr>
    <th>
      Documentclass: <xsl:value-of select="@name"/>
    </th>
  </tr>
  <xsl:for-each select="library">
    <tr>
      <td>
        <xsl:apply-templates select="."/>
      </td>
    </tr>
  </xsl:for-each>
</table>
  
</xsl:template>




<xsl:template match="library">

<table class="library">
  <tr>
    <th>
      Library: <xsl:value-of select="@name"/>
    </th>
  </tr>

  <tr>
    <td>
      <xsl:call-template name="add_lib_info"/>      
    </td>
  </tr>

  <tr>
    <td>
      <xsl:apply-templates select="." mode='list'/>      
    </td>
  </tr>
</table>
</xsl:template>




<xsl:template match="library" mode="list">
  <table class="short-list">
    <!-- ============ ENVS ================ -->
    <xsl:if test="environment">
      <tr class='env-header'>
        <td colspan="3">
          Environments
        </td>     
      </tr> 
    </xsl:if>

    <xsl:for-each select="environment">
    <xsl:sort select="@name"/>
      <tr>
        <td class="command-line">
          <xsl:call-template name="process-env-query"/>
        </td>
        <td class="args">
          <xsl:apply-templates  select="opt-args|man-args"/>     
        </td>
        <td class="description">
          <xsl:apply-templates select="description"/>
        </td>
      </tr>
    </xsl:for-each>


    <!-- ============ CMDS ================ -->
    <xsl:if test="command">
      <tr class='cmd-header'>
        <td colspan="3">
          Commands
        </td>     
      </tr> 
    </xsl:if>

    <xsl:for-each select="command">
    <xsl:sort select="@name"/>
      <tr>
        <td class="command-line">
          <xsl:call-template name="process-cmd-query"/>
        </td>
        <td class="args">
          <xsl:apply-templates  select="opt-args|man-args"/>     
        </td>
        <td class="description">
          <xsl:apply-templates select="description"/>
        </td>
      </tr>
    </xsl:for-each>
  </table>
</xsl:template>

</xsl:stylesheet>
