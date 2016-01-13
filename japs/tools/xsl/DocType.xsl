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

   $Id: DocType.xsl,v 1.22 2007/07/11 15:38:56 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:import href="config.xsl"/>
<xsl:import href="java_utils.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">DocType.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: DocType.xsl,v 1.22 2007/07/11 15:38:56 grudzin Exp $</xsl:param>
<xsl:param name="DocType.mumie-xml-namespace">http://www.mumie.net/xml-namespace</xsl:param>
<xsl:param name="DocType.mumie-xml-namespace-shortcut">$MUMIE</xsl:param>
<xsl:param name="DocType.java-src-dir">java/src</xsl:param>
<xsl:param name="DocType.package">net.mumie.cocoon.notions</xsl:param>

<!--
   ==========================================================================================
   Templates for target "DocType"
   ==========================================================================================
-->

<xsl:template name="DocType.document-type.description">
  <xsl:param name="name" select="@name"/>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.document-type.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.document-type.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.description">
    <xsl:with-param name="node" select="/*/document-types/document-type[@name=$name]"/>
    <xsl:with-param name="newline">&br; * &sp;&sp;&sp;&sp;</xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.class-description">
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Each Mumie document has a &lt;em&gt;type&lt;/em&gt;.</xsl:text>
  <xsl:text> Document types are specified by numerical&br;</xsl:text>
  <xsl:text> * &sp;codes, or, alternatively, string names.</xsl:text>
  <xsl:text> The computer almost only uses the numerical&br;</xsl:text>
  <xsl:text> * &sp;codes. The string names exist to have a more human-readable</xsl:text>
  <xsl:text> document type specification&br;</xsl:text>
  <xsl:text> * &sp;as well.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Currenty, the following document types are defined:&br;</xsl:text>
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
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:call-template name="DocType.document-type.description"/>    
  </xsl:for-each>
  <xsl:text> * &sp;&lt;/tbody&gt;&br;</xsl:text>
  <xsl:text> * &lt;/table&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;This class defines static constants with self-explanatory names&br;</xsl:text>
  <xsl:text> * &sp;wrapping the numerical codes, and some auxilliary stuff.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.UNDEFINED">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the document type is undefined.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int UNDEFINED = -1;&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.corrector">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating the document type that functions as corrector.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int corrector = </xsl:text>
  <xsl:call-template name="config.document-type.code">
    <xsl:with-param name="name" select="$config.corrector.document-type.name"/>
  </xsl:call-template>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.document-type.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code of the &lt;span class="string"&gt;"</xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>"&lt;/span&gt; document type.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int </xsl:text>
  <xsl:call-template name="config.document-type.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.document-type.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.constant-range">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Smallest number used as numerical code of a document type.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int first = </xsl:text>
  <xsl:call-template name="config.document-type.code-min"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Largest number used as numerical code of a document type.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int last = </xsl:text>
  <xsl:call-template name="config.document-type.code-max"/>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.nameFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping numerical codes to string names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] nameFor = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.codeFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns the numerical code for the string name</xsl:text>
  <xsl:text> &lt;code&gt;name&lt;/code&gt;.&br;</xsl:text>
  <xsl:text>&sp; * Can also return &lt;code&gt;UNDEFINED&lt;/code&gt; if</xsl:text>
  <xsl:text> &lt;code&gt;name&lt;/code&gt; is not a valid name&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static int codeFor ( String name )&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;&sp;int code = first;&br;</xsl:text>
  <xsl:text>&sp;&sp;while ( ( code &lt;= last )</xsl:text>
  <xsl:text> &amp;&amp; (! nameFor[code].equals(name) ) )</xsl:text>
  <xsl:text> code++;&br;</xsl:text>
  <xsl:text>&sp;&sp;if ( code &gt; last ) code = UNDEFINED;&br;</xsl:text>
  <xsl:text>&sp;&sp;return code;&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.hintFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping document types to hints.</xsl:text>
  <xsl:text> The hints are designated to be used with a&br;</xsl:text>
  <xsl:text>&sp; * {@link org.apache.avalon.framework.service.ServiceSelector</xsl:text>
  <xsl:text> ServiceSelector}.&br;</xsl:text>
  <xsl:text>&sp; * The document types are given as numerical codes.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] hintFor = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:call-template name="config.document-type.hint"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.descriptionFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping document types to their descriptions.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] descriptionFor = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:if test="$is-generic='yes'">
      <xsl:text>Generic </xsl:text>
    </xsl:if>
    <xsl:value-of select="@description"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.isGeneric">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array specifying for each document type whether it</xsl:text>
  <xsl:text> is generic or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final boolean[] isGeneric = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:text>&sp;&sp;false,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$is-generic='yes'">
        <xsl:text>&sp;&sp;true,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.hasGeneric">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array specifying for each document type whether it</xsl:text>
  <xsl:text> has a generic counterpart or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final boolean[] hasGeneric = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-generic">
      <xsl:call-template name="config.document-type.has-generic-counterpart"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-generic='no'">
        <xsl:text>&sp;&sp;false,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$has-generic='yes'">
        <xsl:text>&sp;&sp;true,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.genericOf">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping each document type to its generic counterpart.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int[] genericOf = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-generic">
      <xsl:call-template name="config.document-type.has-generic-counterpart"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-generic='yes'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:call-template name="config.document-type.constant">
          <xsl:with-param name="name">
            <xsl:call-template name="config.document-type.generic-counterpart"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$has-generic='no'">
        <xsl:text>&sp;&sp;UNDEFINED,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.realOf">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping each document type to its "real" counterpart.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int[] realOf = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$is-generic='yes'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:call-template name="config.document-type.constant">
          <xsl:with-param name="name">
            <xsl:call-template name="config.document-type.is-generic-of"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$is-generic='no'">
        <xsl:text>&sp;&sp;UNDEFINED,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.hasQualifiedName">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array specifying for each document type whether it has</xsl:text>
  <xsl:text> a qualified name or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final boolean[] hasQualifiedName = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-qualified-name">
      <xsl:call-template name="config.document-type.has-qualified-name"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-qualified-name='no'">
        <xsl:text>&sp;&sp;false,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$has-qualified-name='yes'">
        <xsl:text>&sp;&sp;true,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.hasCategory">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array specifying for each document type whether it has</xsl:text>
  <xsl:text> a category or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final boolean[] hasCategory = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-category">
      <xsl:call-template name="config.document-type.has-category"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-category='yes'">
        <xsl:text>&sp;&sp;true,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$has-category='no'">
        <xsl:text>&sp;&sp;false,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.hasCorrector">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array specifying for each document type whether it has</xsl:text>
  <xsl:text> a corrector or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final boolean[] hasCorrector = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-corrector">
      <xsl:call-template name="config.document-type.has-corrector"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-corrector='yes'">
        <xsl:text>&sp;&sp;true,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$has-corrector='no'">
        <xsl:text>&sp;&sp;false,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.hasWidthAndHeight">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array specifying for each document type whether it has</xsl:text>
  <xsl:text> width and height or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final boolean[] hasWidthAndHeight = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-width-and-height">
      <xsl:call-template name="config.document-type.has-width-and-height"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-width-and-height='no'">
        <xsl:text>&sp;&sp;false,&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$has-width-and-height='yes'">
        <xsl:text>&sp;&sp;true,&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.has">
  <xsl:param name="property"/>
  <xsl:param name="property.notion" select="$property"/>
  <xsl:param name="method.name">
    <xsl:call-template name="config.java-name">
      <xsl:with-param name="string" select="$property"/>
    </xsl:call-template>
  </xsl:param>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array specifying for each document type whether it </xsl:text>
  <xsl:value-of select="$property.notion"/>
  <xsl:text> or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final boolean[] </xsl:text>
  <xsl:value-of select="$method.name"/>
  <xsl:text> = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:choose>
      <xsl:when test="@*[local-name()=$property]='yes'">
        <xsl:text>&sp;&sp;true,&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&sp;&sp;false,&br;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DocType.exists">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns &lt;code&gt;true&lt;/code&gt; if</xsl:text>
  <xsl:text> &lt;code&gt;type&lt;/code&gt; is the numerical code of a document&br;</xsl:text>
  <xsl:text>&sp; * type, &lt;code&gt;false&lt;/code&gt; otherwise.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static boolean exists (int type)&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;  return ( ( type &gt;= DocType.first ) &amp;&amp; </xsl:text>
  <xsl:text> ( type &lt;= DocType.last ) );&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns &lt;code&gt;true&lt;/code&gt; if</xsl:text>
  <xsl:text> &lt;code&gt;name&lt;/code&gt; is the string name of a document&br;</xsl:text>
  <xsl:text>&sp; * type, &lt;code&gt;false&lt;/code&gt; otherwise.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static boolean exists ( String name )&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;&sp;int code = first;&br;</xsl:text>
  <xsl:text>&sp;&sp;while ( ( code &lt;= last )</xsl:text>
  <xsl:text> &amp;&amp; (! nameFor[code].equals(name) ) )</xsl:text>
  <xsl:text> code++;&br;</xsl:text>
  <xsl:text>&sp;&sp;return ( code &lt;= last );&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>

</xsl:template>

<xsl:template name="DocType">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$DocType.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:call-template name="DocType.class-description"/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class DocType&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="DocType.UNDEFINED"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.corrector"/>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:call-template name="DocType.document-type.constant"/>
    <xsl:text>&br;</xsl:text>
  </xsl:for-each>
  <xsl:call-template name="DocType.constant-range"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.nameFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.codeFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.hintFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.descriptionFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.isGeneric"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.hasGeneric"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.genericOf"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.realOf"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.hasQualifiedName"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.hasCategory"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.hasCorrector"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.hasWidthAndHeight"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.has">
    <xsl:with-param name="property" select="'has-alt-image'"/>
    <xsl:with-param name="property.notion" select="'has an alternative image'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.has">
    <xsl:with-param name="property" select="'has-thumbnail'"/>
    <xsl:with-param name="property.notion" select="'has a thumbnail image'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.has">
    <xsl:with-param name="property" select="'has-members'"/>
    <xsl:with-param name="property.notion" select="'has members'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.has">
    <xsl:with-param name="property" select="'has-timeframe'"/>
    <xsl:with-param name="property.notion" select="'has a timeframe'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DocType.exists"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'nameFor'"/>
    <xsl:with-param name="type" select="'String'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'hintFor'"/>
    <xsl:with-param name="type" select="'String'"/>
  </xsl:call-template>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'descriptionFor'"/>
    <xsl:with-param name="type" select="'String'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'isGeneric'"/>
    <xsl:with-param name="type" select="'boolean'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'hasGeneric'"/>
    <xsl:with-param name="type" select="'boolean'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'genericOf'"/>
    <xsl:with-param name="type" select="'int'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'realOf'"/>
    <xsl:with-param name="type" select="'int'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'hasQualifiedName'"/>
    <xsl:with-param name="type" select="'boolean'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'hasAltImage'"/>
    <xsl:with-param name="type" select="'boolean'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'hasThumbnail'"/>
    <xsl:with-param name="type" select="'boolean'"/>
  </xsl:call-template>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="DocType"/>
</xsl:template>

</xsl:stylesheet>
