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

   $Id: FileRole.xsl,v 1.4 2007/07/11 15:38:57 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the
   net.mumie.cocoon.notion.FileRole class
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>
<xsl:import href="java_utils.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">FileRole.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: FileRole.xsl,v 1.4 2007/07/11 15:38:57 grudzin Exp $</xsl:param>
<xsl:param name="FileRole.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<!--
   The Javadoc comment for the class. Includes a table of all file roles.
-->

<xsl:template name="FileRole.class-description">
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;To represent (pseudo-)documents at the client side, Japs uses files with&br;</xsl:text>
  <xsl:text> * &sp;different &lt;em&gt;roles&lt;/em&gt; (see table below). Roles are specified by string&br;</xsl:text>
  <xsl:text> * &sp;names or numerical codes. The computer almost always uses the numerical codes&br;</xsl:text>
  <xsl:text> * &sp;instead of the string names.&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Besides some auxiliary stuff, this class defines&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;ul&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;li&gt;the numerical code for each file role,&lt;/li&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;li&gt;a static constant with a self-explanatory name for each numerical code,&lt;/li&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;li&gt;a suffix of each file role.&lt;/li&gt;&br;</xsl:text>
  <xsl:text> * &lt;/ul&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
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
  <xsl:for-each select="/*/file-roles/file-role">
    <xsl:call-template name="FileRole.description-of"/>    
  </xsl:for-each>
  <xsl:text> * &sp;&lt;/tbody&gt;&br;</xsl:text>
  <xsl:text> * &lt;/table&gt;&br;</xsl:text>
</xsl:template>

<!--
   Creates a row of the table of file roles in the Javadoc class documentation. The row
   corresponds to a certain file role and contains all its data.
-->

<xsl:template name="FileRole.description-of">
  <xsl:param name="name" select="@name"/>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:call-template name="config.file-role.name">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.file-role.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.file-role.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:call-template name="config.file-role.suffix">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.description">
    <xsl:with-param name="node"
                    select="/*/file-roles/file-role[@name=$name]"/>
    <xsl:with-param name="newline">&br; * &sp;&sp;&sp;&sp;</xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for a file role constant.
-->

<xsl:template name="FileRole.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code of the &lt;span class="string"&gt;"</xsl:text>
  <xsl:call-template name="config.file-role.name">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>"&lt;/span&gt; file role.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int </xsl:text>
  <xsl:call-template name="config.file-role.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.file-role.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for two constants that contain the smallest resp. largest media
   type code.
-->

<xsl:template name="FileRole.constant-range">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Smallest number used as numerical code of a file role.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int first = </xsl:text>
  <xsl:call-template name="config.file-role.code-min"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Largest number used as numerical code of a file role.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int last = </xsl:text>
  <xsl:call-template name="config.file-role.code-max"/>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the UNDEFINED constant.
-->

<xsl:template name="FileRole.UNDEFINED">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the file role is undefined.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int UNDEFINED = -1;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the nameFor array.
-->

<xsl:template name="FileRole.nameFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping file role codes to string names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] nameFor = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/file-roles/file-role">
    <xsl:call-template name="FileRole.name-for"/>    
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for one entry in the nameFor array. This entry belongs to a certain
   file role.
-->

<xsl:template name="FileRole.name-for">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;&sp;"</xsl:text>
  <xsl:call-template name="config.file-role.name">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>",&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the suffixOf array.
-->

<xsl:template name="FileRole.suffixOf">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping file role codes to suffixes.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] suffixOf = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/file-roles/file-role">
    <xsl:call-template name="FileRole.suffix-of"/>    
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for one entry in the suffixOf array. The entry belongs to a certain
   file role.
-->

<xsl:template name="FileRole.suffix-of">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;&sp;"</xsl:text>
  <xsl:call-template name="config.file-role.suffix">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>",&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the MASTER_SUFFIX constant.
-->

<xsl:template name="FileRole.MASTER_SUFFIX">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Role suffix of master files&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String MASTER_SUFFIX = "</xsl:text>
  <xsl:call-template name="config.file-role.suffix">
    <xsl:with-param name="name">master</xsl:with-param>
  </xsl:call-template>
  <xsl:text>";&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the CONTENT_SUFFIX constant.
-->

<xsl:template name="FileRole.CONTENT_SUFFIX">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Role suffix of content files&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String CONTENT_SUFFIX = "</xsl:text>
  <xsl:call-template name="config.file-role.suffix">
    <xsl:with-param name="name">content</xsl:with-param>
  </xsl:call-template>
  <xsl:text>";&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the SOURCE_SUFFIX constant.
-->

<xsl:template name="FileRole.SOURCE_SUFFIX">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Role suffix of source files&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String SOURCE_SUFFIX = "</xsl:text>
  <xsl:call-template name="config.file-role.suffix">
    <xsl:with-param name="name">source</xsl:with-param>
  </xsl:call-template>
  <xsl:text>";&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the PREVIEW_SUFFIX constant.
-->

<xsl:template name="FileRole.PREVIEW_SUFFIX">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Role suffix of preview files&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String PREVIEW_SUFFIX = "</xsl:text>
  <xsl:call-template name="config.file-role.suffix">
    <xsl:with-param name="name">preview</xsl:with-param>
  </xsl:call-template>
  <xsl:text>";&br;</xsl:text>
</xsl:template>

<!--
   Creates the entire Java source.
-->

<xsl:template name="FileRole">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$FileRole.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:call-template name="FileRole.class-description"/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class FileRole&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="FileRole.UNDEFINED"/>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/file-roles/file-role">
    <xsl:call-template name="FileRole.constant"/>
    <xsl:text>&br;</xsl:text>
  </xsl:for-each>
  <xsl:call-template name="FileRole.constant-range"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="FileRole.nameFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="FileRole.suffixOf"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="FileRole.MASTER_SUFFIX"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="FileRole.CONTENT_SUFFIX"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="FileRole.SOURCE_SUFFIX"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="FileRole.PREVIEW_SUFFIX"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.codeFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'nameFor'"/>
    <xsl:with-param name="type" select="'String'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.codeFor">
    <xsl:with-param name="code-vs-name-array" select="'suffixOf'"/>
    <xsl:with-param name="method" select="'codeForSuffix'"/>
    <xsl:with-param name="string" select="'suffix'"/>
    <xsl:with-param name="string-notation" select="'suffix'"/>
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
  <xsl:call-template name="FileRole"/>
</xsl:template>

</xsl:stylesheet>
