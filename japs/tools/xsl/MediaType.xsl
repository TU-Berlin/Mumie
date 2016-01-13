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

   $Id: MediaType.xsl,v 1.8 2007/07/11 15:38:57 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the
   net.mumie.cocoon.notion.MediaType class
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>
<xsl:import href="java_utils.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">MediaType.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: MediaType.xsl,v 1.8 2007/07/11 15:38:57 grudzin Exp $</xsl:param>
<xsl:param name="MediaType.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<!--
   The Javadoc comment for the class. Includes a table of all media types.
-->

<xsl:template name="MediaType.class-description">
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * Media types used in Japs.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Media types are almost the same as mime types.&br;</xsl:text>
  <xsl:text> * See &sp;&lt;a href="http://www.graphcomp.com/info/specs/mime.html"&gt;</xsl:text>
  <xsl:text>here&lt;/a&gt; for more information,&br;</xsl:text>
  <xsl:text> * &sp;including a list of commonly used media types.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;The content of each Mumie document has a</xsl:text>
  <xsl:text> media type. Do not confuse the type of&br;</xsl:text>
  <xsl:text> * &sp;a document and the media type of its content. See&br;</xsl:text>
  <xsl:text> * &sp;{@link net.mumie.cocoon.notions.DocType DocType}</xsl:text>
  <xsl:text> for document types. The media type&br;</xsl:text>
  <xsl:text> * &sp;describes the kind of data the content is made of on a</xsl:text>
  <xsl:text> lower level.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Besides some auxiliary stuff, this class defines&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;ul&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;li&gt;a numerical code for each media type needed,&lt;/li&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;li&gt;a static constant with a self-explanatory name for each numerical code,&lt;/li&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;li&gt;the suffix of each media type.&lt;/li&gt;&br;</xsl:text>
  <xsl:text> * &lt;/ul&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;The computer almost always uses the numerical codes instead of the string names which are&br;</xsl:text>
  <xsl:text> * &sp;specified by the media type standard.  &br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Currently, the following media types are defined within Japs:&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;table class="genuine indented"&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;thead&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Name&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Code&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Constant&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Suffix&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Description&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;/thead&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;tbody&gt;&br;</xsl:text>
  <xsl:for-each select="/*/media-types/media-type">
    <xsl:call-template name="MediaType.description-of"/>    
  </xsl:for-each>
  <xsl:text> * &sp;&lt;/tbody&gt;&br;</xsl:text>
  <xsl:text> * &lt;/table&gt;&br;</xsl:text>
</xsl:template>

<!--
   Creates a row of the table of media types in the Javadoc class documentation. The row
   corresponds to a certain media type and contains all its data.
-->

<xsl:template name="MediaType.description-of">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:call-template name="config.media-type.name">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.media-type.code">
    <xsl:with-param name="type" select="@type"/>
    <xsl:with-param name="subtype" select="@subtype"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.media-type.constant">
    <xsl:with-param name="type" select="@type"/>
    <xsl:with-param name="subtype" select="@subtype"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:call-template name="config.media-type.suffix">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.description">
    <xsl:with-param name="node"
                    select="/*/media-types/media-type[@type=$type and @subtype=$subtype]"/>
    <xsl:with-param name="newline">&br; * &sp;&sp;&sp;&sp;</xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for a media type constant.
-->

<xsl:template name="MediaType.constant">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code of the &lt;span class="string"&gt;"</xsl:text>
  <xsl:call-template name="config.media-type.name">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:text>"&lt;/span&gt; media type.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int </xsl:text>
  <xsl:call-template name="config.media-type.constant">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.media-type.code">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for two constants that contain the smallest resp. largest media
   type code.
-->

<xsl:template name="MediaType.constant-range">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Smallest number used as numerical code of a media type.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int first = </xsl:text>
  <xsl:call-template name="config.media-type.code-min"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Largest number used as numerical code of a media type.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int last = </xsl:text>
  <xsl:call-template name="config.media-type.code-max"/>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the UNDEFINED constant.
-->

<xsl:template name="MediaType.UNDEFINED">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the media type is undefined.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int UNDEFINED = -1;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the nameFor array.
-->

<xsl:template name="MediaType.nameFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping media type codes to string names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] nameFor = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/media-types/media-type">
    <xsl:call-template name="MediaType.name-for"/>    
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for one entry in the nameFor array. This entry belongs to a certain
   media type.
-->

<xsl:template name="MediaType.name-for">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:text>&sp;&sp;"</xsl:text>
  <xsl:call-template name="config.media-type.name">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:text>",&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the suffixOf array.
-->

<xsl:template name="MediaType.suffixOf">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping media type codes to suffixes.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] suffixOf = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/media-types/media-type">
    <xsl:call-template name="MediaType.suffix-of"/>    
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for one entry in the suffixOf array. The entry belongs to a certain
   media type.
-->

<xsl:template name="MediaType.suffix-of">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:text>&sp;&sp;"</xsl:text>
  <xsl:call-template name="config.media-type.suffix">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:text>",&br;</xsl:text>
</xsl:template>

<!--
  Creates a constant for the suffix of text/xml files
-->

<xsl:template name="MediaType.TEXT_XML_SUFFIX">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Suffix of text/xml files.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String TEXT_XML_SUFFIX = "</xsl:text>
  <xsl:call-template name="config.media-type.suffix">
    <xsl:with-param name="type">text</xsl:with-param>
    <xsl:with-param name="subtype">xml</xsl:with-param>
  </xsl:call-template>
  <xsl:text>";&br;</xsl:text>
</xsl:template>

<!--
   Creates the entire Java source.
-->

<xsl:template name="MediaType">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$MediaType.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:call-template name="MediaType.class-description"/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class MediaType&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="MediaType.UNDEFINED"/>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/media-types/media-type">
    <xsl:call-template name="MediaType.constant"/>
    <xsl:text>&br;</xsl:text>
  </xsl:for-each>
  <xsl:call-template name="MediaType.constant-range"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="MediaType.nameFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="MediaType.suffixOf"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="MediaType.TEXT_XML_SUFFIX"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.codeFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'nameFor'"/>
    <xsl:with-param name="type" select="'String'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'suffixOf'"/>
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
  <xsl:call-template name="MediaType"/>
</xsl:template>

</xsl:stylesheet>
