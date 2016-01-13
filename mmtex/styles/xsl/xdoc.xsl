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


<xsl:import href="config.xsl"/>


<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>






<xsl:strip-space elements="*"/>

<xsl:param name="css-stylesheet">
  <xsl:value-of select="$css_dir"/> 
  <xsl:value-of select="'/xdoc_manpage.css'"/>
</xsl:param>





<!-- List of commands either for a library or a dcl -->

<xsl:template match="/">
  <html>
    <head>
      <title>XDOC</title>
      <link rel="stylesheet" type="text/css" href="{$css-stylesheet}"/>
    </head>
    <body>
      <xsl:apply-templates/>
    </body>
  </html>
</xsl:template>




<xsl:template match="library">

<table>
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

  <tr>
    <td>
      <xsl:for-each select="command">
        <xsl:sort order="ascending" select="@name"/>
        <xsl:apply-templates select="." mode='detail'/>      
      </xsl:for-each>
    </td>
  </tr>

</table>
</xsl:template>
















<xsl:template match="opt-args|man-args">
  <table class='arg-description'>
    <xsl:for-each select="arg">
      <tr>
        <td class='arg'>
          <span>
            <xsl:attribute name="class">
              <xsl:value-of select="name(..)"/>          
            </xsl:attribute>
            <xsl:value-of select="@name"/>
          </span>
        </td>
        <td class='des'>
          <xsl:apply-templates/>
        </td>
      </tr>
    </xsl:for-each>
</table>
</xsl:template>






<xsl:template name="add_lib_info">
  <xsl:variable name="add_lib_info" select="document(concat($MMTEX_HOME, '/lib/include/lib_info.xml'))"/>  
  <xsl:variable name="lib_name" select="@name"/>  
  <xsl:if test="$add_lib_info//library[@name=$lib_name]">
    <table class='lib-info'>
      <tr>
        <th>
          Additional library information:          
        </th>
      </tr>
      <tr>
        <td>
          <xsl:value-of select="$add_lib_info//library[@name=$lib_name]"/> 
        </td>
      </tr>
    </table>    
  </xsl:if>  
</xsl:template>









<xsl:template name="process-cmd-query">

    <!-- Print the name of the command e.g \counter -->    
    <span class="code">
    <xsl:value-of select="concat('\',@name)" />
    </span>
    
    <!-- Check if there is a preceding mandatory argument -->
    <xsl:if test="@pref-man-arg='yes'">
      <span class = "man-arg">
        <xsl:value-of select="concat('{',(man-args/arg[1]/@name),'}')"/>
      </span>
    </xsl:if>

    <!-- Process all optional arguments -->
    <xsl:for-each select="opt-args/arg">
      <span class = "opt-arg">
        <xsl:value-of select="concat('[',@name,']')"/>
      </span>
    </xsl:for-each>

    <!-- Start first with the position of the not already processed elements -->
    <xsl:choose>
      <!-- Start with the second one -->
      <xsl:when test="@pref-man-arg='yes'" >
        <xsl:for-each select="man-args/arg[position()&gt;=2]">
          <span class = "man-arg">
            <xsl:value-of select="concat('{',@name,'}')"/>
          </span>
        </xsl:for-each>
      </xsl:when>
      <!-- Start with the first one -->
      <xsl:otherwise>  
        <xsl:for-each select="man-args/arg[position()&gt;=1]">
          <span class = "man-arg">
            <xsl:value-of select="concat('{',@name,'}')"/>
          </span>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
</xsl:template>





<xsl:template name="process-env-query">

    <!-- Print the name of the command e.g \counter -->    
    <span class="code">
    <xsl:value-of select="concat('\begin{',@name , '}')" />
    </span>

    <!-- Check if there is a preceding mandatory argument -->
    <xsl:if test="@pref-man-arg='yes'">
      <span class = "man-arg">
        <xsl:value-of select="concat('{',(man-args/arg[1]/@name),'}')"/>
      </span>
    </xsl:if>

    <!-- Process all optional arguments -->
    <xsl:for-each select="opt-args/arg">
      <span class = "opt-arg">
        <xsl:value-of select="concat('[',@name,']')"/>
      </span>
    </xsl:for-each>

    <!-- Start first with the position of the not already processed elements -->
    <xsl:choose>
      <!-- Start with the second one -->
      <xsl:when test="@pref-man-arg='yes'" >
        <xsl:for-each select="man-args/arg[position()&gt;=2]">
          <span class = "man-arg">
            <xsl:value-of select="concat('{',@name,'}')"/>
          </span>
        </xsl:for-each>
      </xsl:when>
      <!-- Start with the first one -->
      <xsl:otherwise>  
        <xsl:for-each select="man-args/arg[position()&gt;=1]">
          <span class = "man-arg">
            <xsl:value-of select="concat('{',@name,'}')"/>
          </span>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
</xsl:template>






<xsl:template match="description">
  <span class='description'>
    <xsl:apply-templates/>
  </span>
</xsl:template>



<xsl:template match="opt-arg-ref">
  <xsl:variable name="number" select="@number"/>
  <var>
    <xsl:value-of select="ancestor::*/opt-args/arg[$number]/@name"/>  
  </var>
</xsl:template>

<xsl:template match="man-arg-ref">
  <xsl:variable name="number" select="@number"/>
  <var>
    <xsl:value-of select="ancestor::*/man-args/arg[$number]/@name"/>  
  </var>
</xsl:template>



</xsl:stylesheet>
