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

   $Id: java_utils.xsl,v 1.12 2007/07/11 15:38:59 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://www.mumie.net Java Document"
                version="1.0">

<xsl:template name="java-utils.array-to-method">
  <!-- Parameters -->
  <xsl:param name="array"/>
  <xsl:param name="type"/>
  <xsl:param name="class"/>
  <xsl:param name="name-resolver" select="'codeFor'"/>
  <xsl:param name="first" select="'first'"/>
  <xsl:param name="last" select="'last'"/>
  <xsl:param name="method" select="$array"/>
  <xsl:param name="per-string-name">
    <xsl:choose>
      <xsl:when test="$array='nameFor'">
        <xsl:value-of select="'no'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'yes'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>
  <!-- Variables -->
  <xsl:variable name="class.name-resolver">
    <xsl:if test="$class">
      <xsl:value-of select="concat($class, '.')"/>
    </xsl:if>
    <xsl:value-of select="$name-resolver"/>
  </xsl:variable>
  <xsl:variable name="class.first">
    <xsl:if test="$class">
      <xsl:value-of select="concat($class, '.')"/>
    </xsl:if>
    <xsl:value-of select="$first"/>
  </xsl:variable>
  <xsl:variable name="class.last">
    <xsl:if test="$class">
      <xsl:value-of select="concat($class, '.')"/>
    </xsl:if>
    <xsl:value-of select="$last"/>
  </xsl:variable>
  <!-- code vs. name method -->
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Provides the same infomation as the array {@link #</xsl:text>
  <xsl:value-of select="$array"/>
  <xsl:text>}.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static </xsl:text>
  <xsl:value-of select="$type"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$method"/>
  <xsl:text> (int code)&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;&sp;if ( ( code &lt; </xsl:text>
  <xsl:value-of select="$class.first"/>
  <xsl:text> ) || ( code &gt; </xsl:text>
  <xsl:value-of select="$class.last"/>
  <xsl:text> ) )&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;throw new IllegalArgumentException("</xsl:text>
  <xsl:value-of select="$method"/>
  <xsl:text>(int): Code out of range " + code);&br;</xsl:text>
  <xsl:text>&sp;&sp;return </xsl:text>
  <xsl:value-of select="$array"/>
  <xsl:text>[code];&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
  <xsl:if test="$per-string-name='yes'">
    <!-- name1 vs. name2 method -->
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;/**&br;</xsl:text>
    <xsl:text>&sp; * Provides the same infomation as the array {@link #</xsl:text>
    <xsl:value-of select="$array"/>
    <xsl:text>}, but instead of numerical&br;</xsl:text>
    <xsl:text>&sp; * codes, the string names are used.&br;</xsl:text>
    <xsl:text>&sp; */&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;public static </xsl:text>
    <xsl:value-of select="$type"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="$method"/>
    <xsl:text> (String name)&br;</xsl:text>
    <xsl:text>&sp;{&br;</xsl:text>
    <xsl:text>&sp;&sp;int code = </xsl:text>
    <xsl:value-of select="$class.name-resolver"/>
    <xsl:text>(name);&br;</xsl:text>
    <xsl:text>&sp;&sp;if ( code == UNDEFINED )&br;</xsl:text>
    <xsl:text>&sp;&sp;&sp;throw new IllegalArgumentException("</xsl:text>
    <xsl:value-of select="$method"/>
    <xsl:text>(String): Unknown name " + name);&br;</xsl:text>
    <xsl:text>&sp;&sp;return </xsl:text>
    <xsl:value-of select="$array"/>
    <xsl:text>[code];&br;</xsl:text>
    <xsl:text>&sp;}&br;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template name="java-utils.codeFor">
  <xsl:param name="code-vs-name-array" select="'nameFor'"/>
  <xsl:param name="class"/>
  <xsl:param name="string" select="'name'"/>
  <xsl:param name="string-notation" select="'string name'"/>
  <xsl:param name="first" select="'first'"/>
  <xsl:param name="last" select="'last'"/>
  <xsl:param name="method" select="'codeFor'"/>
  <xsl:param name="undef-value" select="'UNDEFINED'"/>
  <!-- Variables -->
  <xsl:variable name="class.code-vs-name-array">
    <xsl:if test="$class">
      <xsl:value-of select="concat($class, '.')"/>
    </xsl:if>
    <xsl:value-of select="$code-vs-name-array"/>
  </xsl:variable>
  <xsl:variable name="class.first">
    <xsl:if test="$class">
      <xsl:value-of select="concat($class, '.')"/>
    </xsl:if>
    <xsl:value-of select="$first"/>
  </xsl:variable>
  <xsl:variable name="class.last">
    <xsl:if test="$class">
      <xsl:value-of select="concat($class, '.')"/>
    </xsl:if>
    <xsl:value-of select="$last"/>
  </xsl:variable>
  <!-- name vs. code method -->
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns the numerical code for the </xsl:text>
  <xsl:value-of select="$string-notation"/>
  <xsl:text> &lt;code&gt;</xsl:text>
  <xsl:value-of select="$string"/>
  <xsl:text>&lt;/code&gt;.&br;</xsl:text>
  <xsl:text>&sp; * Can also return &lt;code&gt;</xsl:text>
  <xsl:value-of select="$undef-value"/>
  <xsl:text>&lt;/code&gt; if</xsl:text>
  <xsl:text> &lt;code&gt;</xsl:text>
  <xsl:value-of select="$string"/>
  <xsl:text>&lt;/code&gt; is not a valid </xsl:text>
  <xsl:value-of select="$string-notation"/>
  <xsl:text>.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static int </xsl:text>
  <xsl:value-of select="$method"/>
  <xsl:text> (String </xsl:text>
  <xsl:value-of select="$string"/>
  <xsl:text>)&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;&sp;int code = </xsl:text>
  <xsl:value-of select="$class.first"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&sp;&sp;while ( ( code &lt;= </xsl:text>
  <xsl:value-of select="$class.last"/>
  <xsl:text> )</xsl:text>
  <xsl:text> &amp;&amp; (! </xsl:text>
  <xsl:value-of select="$class.code-vs-name-array"/>
  <xsl:text>[code].equals(</xsl:text>
  <xsl:value-of select="$string"/>
  <xsl:text>) ) )</xsl:text>
  <xsl:text> code++;&br;</xsl:text>
  <xsl:text>&sp;&sp;if ( code &gt; </xsl:text>
  <xsl:value-of select="$class.last"/>
  <xsl:text> ) code = </xsl:text>
  <xsl:value-of select="$undef-value"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&sp;&sp;return code;&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
</xsl:template>

<xsl:template name="java-utils.exists">
  <xsl:param name="method" select="'exists'"/>
  <xsl:param name="class"/>
  <xsl:param name="first" select="'first'"/>
  <xsl:param name="last" select="'last'"/>
  <xsl:param name="description"/>
  <xsl:param name="codeFor.method" select="'codeFor'"/>
  <!-- Method exists (int type) -->
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns &lt;code&gt;true&lt;/code&gt; if &lt;code&gt;type&lt;/code&gt; is the numerical code&br;</xsl:text>
  <xsl:text>&sp; * of </xsl:text>
  <xsl:value-of select="$description"/>
  <xsl:text>, otherwise &lt;code&gt;false&lt;/code&gt;.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static boolean </xsl:text>
  <xsl:value-of select="$method"/>
  <xsl:text> (int type)&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;&sp;return ( type &gt;= </xsl:text>
  <xsl:if test="$class">
    <xsl:value-of select="concat($class, '.')"/>
  </xsl:if>
  <xsl:value-of select="$first"/>
  <xsl:text> &amp;&amp; type &lt;= </xsl:text>
  <xsl:if test="$class">
    <xsl:value-of select="concat($class, '.')"/>
  </xsl:if>
  <xsl:value-of select="$last"/>
  <xsl:text> );&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <!-- Method exists (String name) -->
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns &lt;code&gt;true&lt;/code&gt; if &lt;code&gt;name&lt;/code&gt; is the string name&br;</xsl:text>
  <xsl:text>&sp; * of </xsl:text>
  <xsl:value-of select="$description"/>
  <xsl:text>, otherwise &lt;code&gt;false&lt;/code&gt;.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static boolean </xsl:text>
  <xsl:value-of select="$method"/>
  <xsl:text> (String name)&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;&sp;return </xsl:text>
  <xsl:value-of select="$method"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$codeFor.method"/>
  <xsl:text>(name));&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
</xsl:template>

<xsl:template name="java-utils.disabled-constructor">
  <xsl:param name="class"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Throws an IllegalStateException to indicate that this class must not be instanciated.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private </xsl:text>
  <xsl:value-of select="$class"/>
  <xsl:text> ()&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;&sp;throw new IllegalStateException&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;(</xsl:text>
  <xsl:value-of select="$class"/>
  <xsl:text>.class.getName() + " must not be instanciated.");&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
</xsl:template>

</xsl:stylesheet>
