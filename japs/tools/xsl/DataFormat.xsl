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

<!DOCTYPE stylesheet
  [
   <!ENTITY br  "&#xA;">
   <!ENTITY sp  "  ">
  ]
>

<!--
   Author:  Tilman Rassy

   $Id: DataFormat.xsl,v 1.8 2007/07/11 15:38:56 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:import href="config.xsl"/>
<xsl:import href="java_utils.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">DataFormat.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: DataFormat.xsl,v 1.8 2007/07/11 15:38:56 grudzin Exp $</xsl:param>
<xsl:param name="DataFormat.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<xsl:template name="DataFormat.class-description">
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;The &lt;em&gt;format&lt;/em&gt; of a document indicates</xsl:text>
  <xsl:text> how the document's data is stored&br;</xsl:text>
  <xsl:text> * &sp;in the database. Formats are specified by numerical</xsl:text>
  <xsl:text> codes, or, alternatively,&br;</xsl:text>
  <xsl:text> * &sp;string names. The computer almost only uses the</xsl:text>
  <xsl:text> numerical codes. The string&br;</xsl:text>
  <xsl:text> * &sp; names exist to have a more human-readable</xsl:text>
  <xsl:text> format specification as well.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Currenty, the following formats are defined:&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;table class="genuine indented"&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;thead&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Name&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Code&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Constant&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Description&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;/thead&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;tbody&gt;&br;</xsl:text>
  <xsl:for-each select="/*/data-formats/data-format">
    <xsl:call-template name="DataFormat.description-of"/>    
  </xsl:for-each>
  <xsl:text> * &sp;&lt;/tbody&gt;&br;</xsl:text>
  <xsl:text> * &lt;/table&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;This class defines static constants with self-explanatory names&br;</xsl:text>
  <xsl:text> * &sp;wrapping the numerical codes, and some auxilliary stuff.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
</xsl:template>

<xsl:template name="DataFormat.description-of">
  <xsl:param name="name" select="@name"/>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.data-format.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.data-format.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.description">
    <xsl:with-param name="node" select="/*/data-formats/data-format[@name=$name]"/>
    <xsl:with-param name="newline">&br; * &sp;&sp;&sp;&sp;</xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
</xsl:template>

<xsl:template name="DataFormat.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code of the &lt;span class="string"&gt;"</xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>"&lt;/span&gt; data format.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int </xsl:text>
  <xsl:call-template name="config.data-format.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.data-format.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<xsl:template name="DataFormat.constant-range">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Smallest number used as numerical code of a data format.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int first = </xsl:text>
  <xsl:call-template name="config.data-format.code-min"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Largest number used as numerical code of a data format.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int last = </xsl:text>
  <xsl:call-template name="config.data-format.code-max"/>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<xsl:template name="DataFormat.UNDEFINED">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the data format is undefined.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int UNDEFINED = -1;&br;</xsl:text>
</xsl:template>

<xsl:template name="DataFormat.ofDocType">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding formats.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int[] ofDocType = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="config.data-format.constant">
      <xsl:with-param name="name">
        <xsl:call-template name="config.document-type.format"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DataFormat">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$DataFormat.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:call-template name="DataFormat.class-description"/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class DataFormat&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="DataFormat.UNDEFINED"/>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/data-formats/data-format">
    <xsl:call-template name="DataFormat.constant"/>
    <xsl:text>&br;</xsl:text>
  </xsl:for-each>
  <xsl:call-template name="DataFormat.constant-range"/>
  <xsl:text>&br;</xsl:text>
  <xsl:if test="/*/data-formats/data-formats-of-documents">
    <xsl:call-template name="DataFormat.ofDocType"/>
    <xsl:text>&br;</xsl:text>
    <xsl:call-template name="java-utils.array-to-method">
      <xsl:with-param name="array" select="'ofDocType'"/>
      <xsl:with-param name="type" select="'int'"/>
      <xsl:with-param name="class" select="'DocType'"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="DataFormat"/>
</xsl:template>

</xsl:stylesheet>
