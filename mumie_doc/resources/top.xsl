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

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"
            encoding="ASCII"/>


<!-- Language (as language code -->
<xsl:param name="lang">de</xsl:param>

<!-- Writes the page: -->
<xsl:template match="/top">
<html>
  <head>
    <title><xsl:value-of select="title[@lang=$lang]"/></title>
    <style type="text/css">
      html
        {
          background-image: url("bg_top.png");
          background-color: #27408b;
          color: #FFFFFF;
          padding: 0px 0px 0px 0px;
          margin: 0px 0px 0px 0px;
          font-family: Verdana, Arial, Helvetica, sans-serif;
          font-size: 10pt;
        }
      body
        {
          margin: 0px 0px 0px 0px;
          padding: 10px 0px 0px 8px;
          height: 20px;
        }
      table
        {
          border-collapse: collapse;
          border-style: none;
          margin: 0px 0px 0px 0px;
          padding: 0px 0px 0px 0px;
        }
      table tr, table td
        {
          margin: 0px 0px 0px 0px;
          padding: 0px 0px 0px 0px;
        }
      td.left
        {
          width: 100px;
          text-align: left;
          vertical-align: middle;
        }
      td.right
        {
          width: 100px;
          text-align: right;
          vertical-align: middle;
        }
      td.middle
        {
          width: 90%;
          text-align: center;
          font-size: 100%;
        }
       div.top-links
        {
          margin: 0px 0px 0px 0px;
          padding: 2px 2px 2px 2px;
          font-size: 75%;
        }
      div.top-links a 
        {
          color: #FFFFFF;
          border-style: none;
          margin: 1px 1px 1px 1px;
          padding: 1px 1px 1px 1px;
        }
     div.top-links a:hover
       {
         border-style: solid;
         margin: 0px 0px 0px 0px;
         border-width: 1px;
         border-color: #FFFFFF;
         text-decoration: none;
         background-color: inherit;
       }
     img
       {
         margin: 0px 4px 0px 4px;
         padding: 0px 0px 0px 0px;
       }
     span.mumie
       {
         font-size: 110%;
         font-weight: bold;
       }
    </style>
  </head>
  <body>
    <strong>
      <xsl:apply-templates select="headline[@lang=$lang]"/>
    </strong>
  </body>
</html>
</xsl:template>

</xsl:stylesheet>

