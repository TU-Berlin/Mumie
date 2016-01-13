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

   $Id: Category.xsl,v 1.6 2009/02/18 15:55:34 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:import href="config.xsl"/>
<xsl:import href="java_utils.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">Category.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: Category.xsl,v 1.6 2009/02/18 15:55:34 rassy Exp $</xsl:param>
<xsl:param name="Category.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<xsl:template name="Category.class-description">
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> *   Each element or subelement document belongs to a certain</xsl:text>
  <xsl:text> &lt;em&gt;category&lt;/em&gt;. Categories&br;</xsl:text>
  <xsl:text> *   are specified by numerical codes, or, alternatively, string names. The computer almost&br;</xsl:text>
  <xsl:text> *   only uses the numerical codes. The string names exist to have a more human-readable&br;</xsl:text>
  <xsl:text> *  category specification as well.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Currenty, the following categories are defined:&br;</xsl:text>
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
  <xsl:for-each select="/*/categories/category">
    <xsl:call-template name="Category.description-of"/>    
  </xsl:for-each>
  <xsl:text> * &sp;&lt;/tbody&gt;&br;</xsl:text>
  <xsl:text> * &lt;/table&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;This class defines static constants with self-explanatory names&br;</xsl:text>
  <xsl:text> * &sp;wrapping the numerical codes, and some auxilliary stuff.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
</xsl:template>

<xsl:template name="Category.description-of">
  <xsl:param name="name" select="@name"/>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.category.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.description">
    <xsl:with-param name="node" select="/*/categories/category[@name=$name]"/>
    <xsl:with-param name="newline">&br; * &sp;&sp;&sp;&sp;</xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
</xsl:template>

<xsl:template name="Category.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code of the &lt;span class="string"&gt;"</xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>"&lt;/span&gt; category.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int </xsl:text>
  <xsl:call-template name="config.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.category.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<xsl:template name="Category.constant-range">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Smallest number used as numerical code of a category.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int first = </xsl:text>
  <xsl:call-template name="config.category.code-min"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Largest number used as numerical code of a category.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int last = </xsl:text>
  <xsl:call-template name="config.category.code-max"/>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<xsl:template name="Category.nameFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping numerical codes to string names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] nameFor = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/categories/category">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="Category.UNDEFINED">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the category is undefined.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int UNDEFINED = -1;&br;</xsl:text>
</xsl:template>

<xsl:template name="Category">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$Category.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:call-template name="Category.class-description"/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class Category&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="Category.UNDEFINED"/>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/categories/category">
    <xsl:call-template name="Category.constant"/>
    <xsl:text>&br;</xsl:text>
  </xsl:for-each>
  <xsl:call-template name="Category.constant-range"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="Category.nameFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.exists">
    <xsl:with-param name="description">a category</xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.codeFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'nameFor'"/>
    <xsl:with-param name="type" select="'String'"/>
  </xsl:call-template>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="Category"/>
</xsl:template>

</xsl:stylesheet>
