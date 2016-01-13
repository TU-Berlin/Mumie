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
  </head>
 <frameset rows="38,*">
   <frame src="resources/top_{$lang}.xhtml" name="top" id="top" noresize="noresize"/>
   <frameset cols="300,*">
     <frame src="resources/toc_{$lang}.xhtml" name="toc" id="toc"/>
     <frame src="resources/void.xhtml" name="main" id="main"/>
   </frameset>
 </frameset>

</html></xsl:template>

</xsl:stylesheet>

