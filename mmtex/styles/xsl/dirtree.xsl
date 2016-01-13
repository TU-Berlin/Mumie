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


<xsl:import href="config.xsl"/>

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>








<!-- ====================================================================== -->
<!--                                 Basic elements                         -->
<!-- ====================================================================== -->

<xsl:template match="/dirtree">
  <html>
    <head >
      <style type="text/css">
        <xsl:call-template name="css-defs"/>
      </style>
      <xsl:copy-of select="/dirtree/head/path"/>
    </head>
    <body>
      <xsl:apply-templates select="/dirtree/directory"/>
    </body>
  </html>
</xsl:template>


<xsl:template match="directory[@name!='CVS']">
  <table>
    <tr>
      <td>       
        <table class="inv">
          <tr>
            <td class='head'>
              <xsl:value-of select="@name"/>
            </td>
            <xsl:call-template name="additional-info"/>
          </tr>
        </table>
      </td>

    </tr>

    <xsl:for-each select="file">
      <tr>
        <td>
          <table class="inv">
            <tr>
              <td>
                <xsl:apply-templates select="."/>
              </td>
              <xsl:call-template name="additional-info"/>
            </tr>
          </table>
        </td>
      </tr>
    </xsl:for-each>

    <xsl:for-each select="directory[@name!='CVS']">
      <tr>
        <td>
          <xsl:apply-templates select="."/>
        </td>
      </tr>
    </xsl:for-each>

  </table>    
</xsl:template>


<xsl:template match="text()|@*">
  
</xsl:template>


<xsl:template match="file">
  <a>
    <xsl:attribute name="href">
     .<xsl:value-of select="parent::directory/path"/>/<xsl:value-of select="@name"/>
    </xsl:attribute>
     <xsl:value-of select="@name"/> 
  </a>
</xsl:template>






<xsl:template name="additional-info">
  <xsl:variable name="dir_name" select="concat(parent::directory/path, '/',@name)"/>  
  <xsl:variable name="add_dir_info" 
              select="document(concat($MMTEX_HOME, '_srcout/docs/additional_dir_info.xml'))"/>  
  <xsl:if test="$add_dir_info//info[@name=$dir_name]">
    <td class="add-info">
      <xsl:value-of select="$add_dir_info//info[@name=$dir_name]"/>
    </td>
  </xsl:if>
</xsl:template>







<xsl:template name="css-defs">
  &lt;!--



  table.inv
  {
  width: 100%;
  margin: 0px;
  spacing: 0px;
  border-collapse: collapse;
  }

  table.inv td {
  text-align: left;
  padding-right: 20px;
  border-style: none ;
  }

  table.inv td.head {
  background-color: rgb(255,255,200);
  text-align: left;
  }

  table.inv td.add-info {
  background-color: #D1FFB3;
  padding: 2px;
  border-style: dotted;
  border-width: 1px;
  font-size: small;
  }

  



  table {
  background-color: rgb(200,255,255);
  border-collapse: collapse;
  cell-spacing: 0px;
  margin: 0px;
  }


  table td {
  padding-left: 25px;
  }





  
  a:hover
  { 
    color: #00008b;
    background-color: #c1ffc1;
  }

  a
  { 
    text-decoration: none;
  }




  --&gt;
</xsl:template>


</xsl:stylesheet>