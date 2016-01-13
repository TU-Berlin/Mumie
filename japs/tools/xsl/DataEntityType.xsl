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
   <!ENTITY dash10  "----------">
   <!ENTITY dash20  "&dash10;&dash10;">
   <!ENTITY dash80  "&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;">
  ]
>

<!--

   Author:  Tilman Rassy

   $Id: DataEntityType.xsl,v 1.3 2007/11/28 15:11:24 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.db classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dc="http://www.mumie.net/xml-namespace/declarations"
                xmlns:x-lib="xalan://net.mumie.japs.build.XSLExtLib"
                xmlns:x-strutil="xalan://net.mumie.util.StringUtil"
                xmlns:x-ioutil="xalan://net.mumie.util.io.IOUtil"
                version="1.0">

<xsl:output method="text"/>

<xsl:strip-space elements="*"/>

<xsl:param name="skeleton-filename"/>

<xsl:variable name="detypes" select="/*/*/dc:document-type|/*/*/dc:pseudo-document-type"/>

<xsl:variable name="doctypes" select="/*/*/dc:document-type"/>

<xsl:variable name="psdoctypes" select="/*/*/dc:pseudo-document-type"/>

<xsl:template name="type-constants">
  <xsl:for-each select="$detypes">
    <xsl:text>&br;</xsl:text>
    <xsl:value-of select="x-lib:javadocComment()"/>
    <xsl:text>&br;&br;</xsl:text>
    <xsl:text>&sp;public static final int </xsl:text>
    <xsl:value-of select="x-lib:itemKey()"/>
    <xsl:text> = </xsl:text>
    <xsl:value-of select="@code"/>
    <xsl:text>;&br;</xsl:text>
  </xsl:for-each>  
</xsl:template>

<xsl:template name="types">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing all (pseudo-)document type codes.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final int[] types =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="@code"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing all document type codes.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final int[] docTypes =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$doctypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="@code"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing all pseudo-document type codes.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final int[] pseudoDocTypes =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$psdoctypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="@code"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="names">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing all (pseudo-)document type names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final String[] names =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:value-of select="@name"/>    
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="hints">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing the selector hints for all (pseudo-)document types.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final String[] hints =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:value-of select="x-lib:hint()"/>    
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="dbTables">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing the db table names for all (pseudo-)document types.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final String[] dbTables =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:value-of select="x-lib:dbTable()"/>    
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="dbRefTables">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing the names for all db reference tables.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final String[][] dbRefTables =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:variable name="origin" select="."/>
    <xsl:text>&sp;&sp;// </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;&sp;{&br;</xsl:text>
    <xsl:for-each select="$detypes">
      <xsl:text>&sp;&sp;&sp;</xsl:text>
      <xsl:choose>
        <xsl:when test="local-name($origin) = 'pseudo-document-type'">null,</xsl:when>
        <xsl:when test="local-name() = 'pseudo-document-type'">null,</xsl:when>
        <xsl:when test="x-lib:isGeneric($origin)">null,</xsl:when>
        <xsl:when test="x-lib:enabled(@name, x-lib:refsTo($origin), x-lib:noRefsTo($origin))">
          <xsl:text>"</xsl:text>
          <xsl:value-of select="x-lib:dbRefTable($origin, .)"/>
          <xsl:text>",</xsl:text>
        </xsl:when>
        <xsl:otherwise>null,</xsl:otherwise>
      </xsl:choose>
      <xsl:text>&br;</xsl:text>
    </xsl:for-each>
    <xsl:text>&sp;&sp;},&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="natures">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing the natures of all (pseudo-)document types.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final int[] natures =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:choose>
      <xsl:when test="local-name() = 'document-type'">Nature.DOCUMENT</xsl:when>
      <xsl:when test="local-name() = 'pseudo-document-type'">Nature.PSEUDO_DOCUMENT</xsl:when>
    </xsl:choose>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="isGeneric">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing for each (pseudo-)document type whether it<!-- 
               --> is generic or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final boolean[] isGeneric =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="x-lib:isGeneric()"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="hasGeneric">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing for each (pseudo-)document type whether it<!-- 
               --> has a generic counterpart or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final boolean[] hasGeneric =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:choose>
      <xsl:when test="/*/*/dc:document-type[@is-generic-of=current()/@name]">true</xsl:when>
      <xsl:otherwise>false</xsl:otherwise>
    </xsl:choose>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="genericOf">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing, for each content object type, its generic&br;</xsl:text>
  <xsl:text>&sp; * counterpart, or {@link "UNDEFINED UNDEFINED} if the content object&br;</xsl:text>
  <xsl:text>&sp; * type has no generic counterpart.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final int[] genericOf =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:variable name="generic" select="$doctypes[@is-generic-of=current()/@name]/@code"/>
    <xsl:choose>
      <xsl:when test="$generic and $generic != ''">
        <xsl:value-of select="$generic"/>
      </xsl:when>
      <xsl:otherwise>UNDEFINED</xsl:otherwise>
    </xsl:choose>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="realOf">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing, for each content object type, its "real"&br;</xsl:text>
  <xsl:text>&sp; * counterpart, or {@link "UNDEFINED UNDEFINED} if the content object&br;</xsl:text>
  <xsl:text>&sp; * type has no real counterpart.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final int[] realOf =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:choose>
      <xsl:when test="@is-generic-of">
        <xsl:value-of select="$doctypes[@name=current()/@is-generic-of]/@code"/>
      </xsl:when>
      <xsl:otherwise>UNDEFINED</xsl:otherwise>
    </xsl:choose>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="hasCategory">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing for each content object type whether it<!-- 
               --> has a category or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final boolean[] hasCategory =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="x-lib:hasCategory()"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="hasWidthAndHeight">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing for each content object type whether it<!-- 
               --> has width and height or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final boolean[] hasWidthAndHeight =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="x-lib:hasWidthAndHeight()"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="hasCorrector">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing for each content object type whether it<!-- 
               --> has a corrector or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final boolean[] hasCorrector =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="x-lib:hasCorrector()"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="hasMembers">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing for each content object type whether it<!-- 
               --> has members or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final boolean[] hasMembers =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="x-lib:hasMembers()"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="hasTimeframe">
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * An array containing for each content object type whether it<!-- 
               --> has a timeframe or not.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;private static final boolean[] hasTimeframe =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="$detypes">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="x-lib:hasTimeframe()"/>    
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>  
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template match="/">
  <xsl:variable name="skeleton" select="x-ioutil:readFile($skeleton-filename)"/>
  <xsl:variable name="autocoded">//#AUTOCODED</xsl:variable>
  <xsl:value-of select="substring-before($skeleton,$autocoded)"/>
  <xsl:call-template name="type-constants"/>
  <xsl:call-template name="types"/>
  <xsl:call-template name="names"/>
  <xsl:call-template name="dbTables"/>
  <xsl:call-template name="dbRefTables"/>
  <xsl:call-template name="hints"/>
  <xsl:call-template name="natures"/>
  <xsl:call-template name="isGeneric"/>
  <xsl:call-template name="hasGeneric"/>
  <xsl:call-template name="genericOf"/>
  <xsl:call-template name="realOf"/>
  <xsl:call-template name="hasCategory"/>
  <xsl:call-template name="hasWidthAndHeight"/>
  <xsl:call-template name="hasCorrector"/>
  <xsl:call-template name="hasMembers"/>
  <xsl:call-template name="hasTimeframe"/>
  <xsl:value-of select="substring-after($skeleton,$autocoded)"/>
</xsl:template>

</xsl:stylesheet>
