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

   $Id: UserRole.xsl,v 1.4 2007/07/11 15:38:58 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the
   net.mumie.cocoon.notion.UserRole class
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>
<xsl:import href="java_utils.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">UserRole.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: UserRole.xsl,v 1.4 2007/07/11 15:38:58 grudzin Exp $</xsl:param>
<xsl:param name="UserRole.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<!--
   The Javadoc comment for the class. Includes a table of all user roles.
-->

<xsl:template name="UserRole.class-description">
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Static utilities to deal with &lt;em&gt;user roles&lt;/em&gt;.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Eeach author or member of a document (if the document can have members) has a&br;</xsl:text>
  <xsl:text> * &sp;certain &lt;em&gt;role&lt;/em&gt; which specifies what the user is to the&br;</xsl:text>
  <xsl:text> * &sp;document. User roles are specified by string names or numerical codes. The computer&br;</xsl:text>
  <xsl:text> * &sp;almost always uses the numerical codes. The string names exist to have a human-readable&br;</xsl:text>
  <xsl:text> * &sp;way to address the user roles as well.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Currently, the following user roles are defined in Japs:&br;</xsl:text>
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
  <xsl:for-each select="/*/user-roles/user-role">
    <xsl:call-template name="UserRole.description-of"/>    
  </xsl:for-each>
  <xsl:text> * &sp;&lt;/tbody&gt;&br;</xsl:text>
  <xsl:text> * &lt;/table&gt;&br;</xsl:text>
</xsl:template>

<!--
   Creates a row of the table of user roles in the Javadoc class documentation. The row
   corresponds to a certain user role and contains all its data.
-->

<xsl:template name="UserRole.description-of">
  <xsl:param name="name" select="@name"/>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
  <xsl:call-template name="config.user-role.name">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.user-role.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
  <xsl:call-template name="config.user-role.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.description">
    <xsl:with-param name="node"
                    select="/*/user-roles/user-role[@name=$name]"/>
    <xsl:with-param name="newline">&br; * &sp;&sp;&sp;&sp;</xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for a user role constant.
-->

<xsl:template name="UserRole.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code of the &lt;span class="string"&gt;"</xsl:text>
  <xsl:call-template name="config.user-role.name">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>"&lt;/span&gt; user role.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int </xsl:text>
  <xsl:call-template name="config.user-role.constant">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.user-role.code">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for two constants that contain the smallest resp. largest media
   type code.
-->

<xsl:template name="UserRole.constant-range">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Smallest number used as numerical code of a user role.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int first = </xsl:text>
  <xsl:call-template name="config.user-role.code-min"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Largest number used as numerical code of a user role.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int last = </xsl:text>
  <xsl:call-template name="config.user-role.code-max"/>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the UNDEFINED constant.
-->

<xsl:template name="UserRole.UNDEFINED">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the user role is undefined.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int UNDEFINED = -1;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the AUTO constant.
-->

<xsl:template name="UserRole.AUTO">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the user role should be set automatically.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int AUTO = -3;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the ANY constant.
-->

<xsl:template name="UserRole.ANY">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Numerical code indicating that the user role comprises any roles.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final int ANY = -4;&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for the nameFor array.
-->

<xsl:template name="UserRole.nameFor">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping user role codes to string names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] nameFor = &br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/user-roles/user-role">
    <xsl:call-template name="UserRole.name-for"/>    
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<!--
   Creates the Java code for one entry in the nameFor array. This entry belongs to a certain
   user role.
-->

<xsl:template name="UserRole.name-for">
  <xsl:param name="name" select="@name"/>
  <xsl:text>&sp;&sp;"</xsl:text>
  <xsl:call-template name="config.user-role.name">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:text>",&br;</xsl:text>
</xsl:template>

<!--
   Creates the 'exists' methods.
-->

<xsl:template name="UserRole.exists">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns &lt;code&gt;true&lt;/code&gt; if</xsl:text>
  <xsl:text> &lt;code&gt;code&lt;/code&gt; is the numerical code of a user role&br;</xsl:text>
  <xsl:text>&sp; * code, &lt;code&gt;false&lt;/code&gt; otherwise.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static boolean exists (int code)&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:text>&sp;  return ( ( code &gt;= UserRole.first ) &amp;&amp; </xsl:text>
  <xsl:text> ( code &lt;= UserRole.last ) );&br;</xsl:text>
  <xsl:text>&sp;}&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Returns &lt;code&gt;true&lt;/code&gt; if</xsl:text>
  <xsl:text> &lt;code&gt;name&lt;/code&gt; is the string name of a user role&br;</xsl:text>
  <xsl:text>&sp; * code, &lt;code&gt;false&lt;/code&gt; otherwise.&br;</xsl:text>
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

<!--
   Creates the entire Java source.
-->

<xsl:template name="UserRole">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$UserRole.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:call-template name="UserRole.class-description"/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class UserRole&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="UserRole.UNDEFINED"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="UserRole.AUTO"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="UserRole.ANY"/>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/user-roles/user-role">
    <xsl:call-template name="UserRole.constant"/>
    <xsl:text>&br;</xsl:text>
  </xsl:for-each>
  <xsl:call-template name="UserRole.constant-range"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="UserRole.nameFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.codeFor"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="java-utils.array-to-method">
    <xsl:with-param name="array" select="'nameFor'"/>
    <xsl:with-param name="type" select="'String'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="UserRole.exists"/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>


<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="UserRole"/>
</xsl:template>

</xsl:stylesheet>
