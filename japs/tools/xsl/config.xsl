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
   Authors:  Tilman Rassy        <rassy@math.tu-berlin.de>
             Fritz Lehman-Grube  <lehmannf@math.tu-berlin.de>

   $Id: config.xsl,v 1.97 2008/02/14 09:51:51 rassy Exp $

   Templates to access the data in config/config.xml
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-strutil="xalan://net.mumie.util.StringUtil"
                xmlns:x-xalan="http://xml.apache.org/xalan"
                version="1.0">

<!-- <xsl:output method="text"/> -->
<!-- <xsl:strip-space elements="*"/> -->

<!--
   ==========================================================================================
   Global parameters and variables
   ==========================================================================================
-->

<!--
   The name of this file
-->

<xsl:param name="this-filename">config.xsl</xsl:param>

<!--
   The cvs id of this file
-->

<xsl:param name="this-cvs-id">$Id: config.xsl,v 1.97 2008/02/14 09:51:51 rassy Exp $</xsl:param>

<!--
   Default string to start a new line
-->

<xsl:param name="default-newline" select="'&br;'"/>

<!--
   The root of the source tree.
-->

<xsl:variable name="config.src" select="/"/>

<!--
-->

<xsl:param name="config.checkin-master-filename-suffix" select="'.meta.xml'"/>

<!--
-->

<xsl:param name="config.checkin-content-filename-suffix" select="'.content'"/>

<!--
  Name of the plpgsql procedure that updates the last modified value.
-->

<xsl:variable name="config.db-procedure-name.set-last-modified">set_last_modified</xsl:variable>

<!--
   ==========================================================================================
   General templates
   ==========================================================================================
-->

<!--
   Prints an error message and terminates the transformation. Expects one parameter,
   $message, which is the explanation of what went wrong.
-->

<xsl:template name="config.error">
  <xsl:param name="message"/>
  <xsl:message terminate="yes">
    <xsl:text>&br;&sp;************************************************************</xsl:text>
    <xsl:text>&br;&sp;&sp;Error while accessing configuration data:&br;&sp;&sp;</xsl:text>
    <xsl:value-of select="$message"/>
    <xsl:text>&br;&sp;************************************************************</xsl:text>
      <xsl:text>&br;&br;</xsl:text>
  </xsl:message>
</xsl:template>

<!--
   Returns $string in upper case. $string must be passed as a parameter.
-->

<xsl:template name="config.upcase">
  <xsl:param name="string"/>
  <xsl:value-of select="translate($string,
                                  'abcdefghijklmnopqrstuvwxyz',
                                  'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
</xsl:template>

<!--
   Returns $string in upper case and '-' replaced by '_'.
   $string must be passed as a parameter.
-->

<xsl:template name="config.upcase-and-dashes-to-underscores">
  <xsl:param name="string"/>
  <xsl:value-of select="translate($string,
                                  'abcdefghijklmnopqrstuvwxyz-',
                                  'ABCDEFGHIJKLMNOPQRSTUVWXYZ_')"/>
</xsl:template>

<!--
   Returns $string with the first letter upcased. $string must be passed as a parameter.
-->

<xsl:template name="config.upcase-first-char">
  <xsl:param name="string"/>
  <xsl:call-template name="config.upcase">
    <xsl:with-param name="string" select="substring($string,1,1)"/>
  </xsl:call-template>
  <xsl:value-of select="substring($string,2)"/>
</xsl:template>

<!--
   Returns the string "CVS" followed by the cvs keyword $value with the $'s removed. $value
   must by passed as a parameter. 
-->

<xsl:template name="config.cvs-keyword">
  <xsl:param name="value"/>
  <xsl:text>CVS </xsl:text>
  <xsl:value-of select="substring-before(substring-after($value,'$'),'$')"/>
</xsl:template>

<!--
   Returns a string to start a new line. The string may be passed as the parameter
   $newline. The default is $default-newline.
-->

<xsl:template match="br">
  <xsl:param name="newline" select="$default-newline"/>
  <xsl:value-of select="$newline"/>
</xsl:template>

<!--
   Returns the description supplied with a node. Expects up to two parameters:

     node     The node to look up for the description. Default is the
              current node.

     newline  String to separate lines in the description. Default is
              $default-newline.

  Works as follows: If $node has a "description" attribute, this attribute's value is
  returned. If $node has a "description" child element, the description is composed from the
  "line" and/or "l" child elements of the "description" element. Each "line" or "l" element
  represents a line of the description.

  Examples:

  If applied to the element

    <foo description="An example"/>

  the template returns

    An example

  If applied to 

    <foo>
      <description>
        <l>This is a description</l>
        <l>that goes over</l>
        <l>multiple lines.</l>
      </description>
    <foo>

  the template returnes

    This is a description
    that goes over
    multiple lines.
-->

<xsl:template name="config.description">
  <xsl:param name="node" select="."/>
  <xsl:param name="newline" select="$default-newline"/>
  <xsl:choose>
    <xsl:when test="$node/@description and $node/description">
      <xsl:message terminate="yes">
        <xsl:text>"description" child element not allowed when a</xsl:text>
        <xsl:text>"description" attribute is present</xsl:text>
      </xsl:message>
    </xsl:when>
    <xsl:when test="$node/@description">
      <xsl:value-of select="$node/@description"/>
    </xsl:when>
    <xsl:when test="$node/description">
      <xsl:for-each select="$node/description/line|$node/description/l">
        <xsl:if test="position()&gt;1">
          <xsl:value-of select="$newline"/>
        </xsl:if>
        <xsl:apply-templates/>
      </xsl:for-each>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!--
   Writes a Javadoc description for a Java class which is automatically created by XSL
   transformation. Expects six parameters:

     xml-source  Name od the XML source of the XSL transformation.

     xsl_stylesheet
                 Name of the XSL stylesheet

     xml-source-cvs-id
                 CVS id of the XML source. May be omitted.

     xsl-source-cvs-id
                 CVS id of the XSL stylesheet. May be omitted.

     parameters  A description of the parameters passed to the XSL stylesheet, if any.
                 May be omitted.

     content     The actual description of the class.
-->

<xsl:template name="config.class-description">
  <xsl:param name="xml-source" select="@filename"/>
  <xsl:param name="xsl-stylesheet"/>
  <xsl:param name="xml-source-cvs-id" select="@cvs-id"/>
  <xsl:param name="xsl-stylesheet-cvs-id"/>
  <xsl:param name="content"/>
  <xsl:param name="parameters"/>
  <xsl:text>/**&br;</xsl:text>
  <xsl:value-of select="$content"/>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Java source automatically created by XSLT transformation.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;dl class="genuine"&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;dt&gt;XML Source:&lt;/dt&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;dd class="file"&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;</xsl:text>
  <xsl:value-of select="$xml-source"/>
  <xsl:if test="$xml-source-cvs-id">
    <xsl:text>&lt;br&gt;</xsl:text>
    <xsl:text>&br; * &sp;&sp;(</xsl:text>
    <xsl:call-template name="config.cvs-keyword">
      <xsl:with-param name="value" select="$xml-source-cvs-id"/>
    </xsl:call-template>
    <xsl:text>)&br;</xsl:text>
  </xsl:if>
  <xsl:text> * &sp;&lt;/dd&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;dt&gt;XSL Stylesheet:&lt;/dt&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;dd class="file"&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;</xsl:text>
  <xsl:value-of select="$xsl-stylesheet"/>
  <xsl:if test="$xsl-stylesheet-cvs-id">
    <xsl:text>&lt;br&gt;</xsl:text>
    <xsl:text>&br; * &sp;&sp;(</xsl:text>
    <xsl:call-template name="config.cvs-keyword">
      <xsl:with-param name="value" select="$xsl-stylesheet-cvs-id"/>
    </xsl:call-template>
    <xsl:text>)</xsl:text>
  </xsl:if>
  <xsl:text>&br;</xsl:text>
  <xsl:text> * &sp;&lt;/dd&gt;&br;</xsl:text>
  <xsl:if test="$parameters">
    <xsl:text> * &sp;&lt;dt&gt;Stylesheet parameters:&lt;/dt&gt;&br;</xsl:text>
    <xsl:text> * &sp;&lt;dd class="code"&gt;</xsl:text>
    <xsl:value-of select="$parameters"/>
    <xsl:text>&lt;/dd&gt;&br;</xsl:text>
  </xsl:if>
  <xsl:text> * &lt;/dl&gt;&br;</xsl:text>
  <xsl:text> */&br;</xsl:text>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the value corresponding to the
   piece of data represented by $node. $node may be passed as a parameter. Default is the
   current node. Works as follows: If $node has a "constant" attribute, that's value is
   returned. Otherwise, The value of the "name" attribute of $node, turned to upper case
   and dashes replaced by underscores, is returned.
-->

<xsl:template name="config.constant">
  <xsl:param name="node" select="."/>
  <xsl:variable name="name" select="$node/@name"/>
  <xsl:variable name="constant" select="$node/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase-and-dashes-to-underscores">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns "hint" for the component represented by $node. $node may be passed as a parameter.
   Default is the current node. Works as follows: If $node has a "hint" attribute, that's
   value is returned. Otherwise, The value of the "name" attribute of $node is returned.
-->

<xsl:template name="config.hint">
  <xsl:param name="node" select="."/>
  <xsl:variable name="name" select="$node/@name"/>
  <xsl:variable name="hint" select="$node/@hint"/>
  <xsl:choose>
    <xsl:when test="$hint">
      <xsl:value-of select="$hint"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Writes a description comment for an XML file which is automatically created by XSL
   transformation. Expects six parameters:

     xml-source  Name or the XML source of the XSL transformation.

     xsl_stylesheet
                 Name of the XSL stylesheet

     xml-source-cvs-id
                 CVS id of the XML source. May be omitted.

     xsl-source-cvs-id
                 CVS id of the XSL stylesheet. May be omitted.

     parameters  A description of the parameters passed to the XSL stylesheet, if any.
                 May be omitted.

     content     The actual description of the XML file.
-->

<xsl:template name="config.xml-description">
  <xsl:param name="xml-source" select="@filename"/>
  <xsl:param name="xsl-stylesheet"/>
  <xsl:param name="xml-source-cvs-id" select="@cvs-id"/>
  <xsl:param name="xsl-stylesheet-cvs-id"/>
  <xsl:param name="content"/>
  <xsl:param name="parameters"/>
  <xsl:comment>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;Automatically created by XSLT transformation.&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;XML Source     : </xsl:text>
    <xsl:value-of select="$xml-source"/>
    <xsl:text>&br;</xsl:text>
    <xsl:if test="$xml-source-cvs-id">
      <xsl:text>&sp;                 (</xsl:text>
      <xsl:call-template name="config.cvs-keyword">
        <xsl:with-param name="value" select="$xml-source-cvs-id"/>
      </xsl:call-template>
      <xsl:text>)&br;</xsl:text>
    </xsl:if>
    <xsl:text>&sp;XSL Stylesheet : </xsl:text>
    <xsl:value-of select="$xsl-stylesheet"/>
    <xsl:text>&br;</xsl:text>
    <xsl:if test="$xsl-stylesheet-cvs-id">
      <xsl:text>&sp;                 (</xsl:text>
      <xsl:call-template name="config.cvs-keyword">
        <xsl:with-param name="value" select="$xsl-stylesheet-cvs-id"/>
      </xsl:call-template>
      <xsl:text>)&br;</xsl:text>
    </xsl:if>
    <xsl:if test="$parameters">
      <xsl:text>&sp;Stylesheet parameters:&br;</xsl:text>
      <xsl:text>&sp;               </xsl:text>
      <xsl:value-of select="$parameters"/>
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>&br;&sp;</xsl:text>
    <xsl:value-of select="$content"/>
    <xsl:text>&br;</xsl:text>
  </xsl:comment>
</xsl:template>

<!--
   Writes a description comment for an SQL file which is automatically created by XSL
   transformation. Expects six parameters:

     xml-source  Name or the XML source of the XSL transformation.

     xsl_stylesheet
                 Name of the XSL stylesheet

     xml-source-cvs-id
                 CVS id of the XML source. May be omitted.

     xsl-source-cvs-id
                 CVS id of the XSL stylesheet. May be omitted.

     parameters  A description of the parameters passed to the XSL stylesheet, if any.
                 May be omitted.

     content     The actual description of the XML file.
-->

<xsl:template name="config.sql-description">
  <xsl:param name="xml-source" select="@filename"/>
  <xsl:param name="xsl-stylesheet"/>
  <xsl:param name="xml-source-cvs-id" select="@cvs-id"/>
  <xsl:param name="xsl-stylesheet-cvs-id"/>
  <xsl:param name="content"/>
  <xsl:param name="parameters"/>
    <xsl:text>--&br;--</xsl:text>
    <xsl:text>&sp;SQL source automatically created by XSLT transformation.&br;--</xsl:text>
    <xsl:text>&br;--</xsl:text>
    <xsl:text>&sp;XML Source     : </xsl:text>
    <xsl:value-of select="$xml-source"/>
    <xsl:text>&br;--</xsl:text>
    <xsl:if test="$xml-source-cvs-id">
      <xsl:text>&sp;                 (</xsl:text>
      <xsl:call-template name="config.cvs-keyword">
        <xsl:with-param name="value" select="$xml-source-cvs-id"/>
      </xsl:call-template>
      <xsl:text>)&br;--</xsl:text>
    </xsl:if>
    <xsl:text>&sp;XSL Stylesheet : </xsl:text>
    <xsl:value-of select="$xsl-stylesheet"/>
    <xsl:text>&br;--</xsl:text>
    <xsl:if test="$xsl-stylesheet-cvs-id">
      <xsl:text>&sp;                 (</xsl:text>
      <xsl:call-template name="config.cvs-keyword">
        <xsl:with-param name="value" select="$xsl-stylesheet-cvs-id"/>
      </xsl:call-template>
      <xsl:text>)&br;--</xsl:text>
    </xsl:if>
    <xsl:if test="$parameters">
      <xsl:text>&sp;Stylesheet parameters:&br;--</xsl:text>
      <xsl:text>&sp;               </xsl:text>
      <xsl:value-of select="$parameters"/>
      <xsl:text>&br;--</xsl:text>
    </xsl:if>
    <xsl:text>&br;/**&br;</xsl:text>
    <xsl:value-of select="$content"/>
    <xsl:text>&br;*/&br;--</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning document types
   ==========================================================================================
-->

<!--
   Checks whether a document type with name $name exists. If not, terminates with an error
   message.
-->

<xsl:template name="config.document-type.check-existence">
  <xsl:param name="name" select="@name"/>
  <xsl:if test="not($config.src/*/document-types/document-type[@name=$name])">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message" select="concat('Cannot find document-type for name: ',$name)"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<!--
   Returns "yes" or "no", depending on whether document type $name is generic or not. $name
   can be passed as a parameter; default is the "name" attribute of the current node.
-->

<xsl:template name="config.document-type.is-generic">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$config.src/*/document-types/document-type[@name=$name]/@is-generic-of">
      <xsl:value-of select="'yes'"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the document type (as string name) of which $name is the corresponding generic
   counterpart. $name can be passed as aparameter; default is the "name" attribute of the
   current node.
 --> 

<xsl:template name="config.document-type.is-generic-of">
  <xsl:param name="name" select="@name"/>
  <xsl:value-of select="$config.src/*/document-types/document-type[@name=$name]/@is-generic-of"/>
</xsl:template>

<!--
   Returns the generic counterpart of document type $name. $name can be passed as
   aparameter; default is the "name" attribute of the current node. 
-->

<xsl:template name="config.document-type.generic-counterpart">
  <xsl:param name="name" select="@name"/>
  <xsl:value-of select="$config.src/*/document-types/document-type[@is-generic-of=$name]/@name"/>
</xsl:template>

<!--
   Returns "yes" or "no", depending on whether document type $name has a generic counterpart
   or not. $name can be passed as a parameter; default is the "name" attribute of the current
   node. 
-->

<xsl:template name="config.document-type.has-generic-counterpart">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$config.src/*/document-types/document-type[@is-generic-of=$name]">
      <xsl:value-of select="'yes'"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns either the name of document type $name - i.e., $name itself -, or, if $name is a
   generic document type, the the name of the corresponding "real" document type;
-->

<xsl:template name="config.document-type.name-of-self-or-real">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="is-generic">
    <xsl:call-template name="config.document-type.is-generic">
      <xsl:with-param name="name" select="$name"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="$is-generic='yes'">
      <xsl:value-of
        select="$config.src/*/document-types/document-type[@name=$name]/@is-generic-of"/>  
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns "yes" or "no", depending on whether documents of type $name recursivly list their
   components if in use mode "component". $name can be passed as a parameter; default is the
   "name" attribute of the current node. 
-->

<xsl:template name="config.document-type.is-recursive-component">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$config.src/*/document-types/document-type[@name=$name]/@is-recursive-component='yes'">
      <xsl:value-of select="'yes'"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the numerical code for the document type with name $name. $name can be passed as
   a parameter; default is the "name" attribute of the current node. Calculates the position
   of the "document-type" element whose "name" attribute equals $name, where positions are
   counted from 0, and returns that.
-->

<xsl:template name="config.document-type.code">
  <xsl:param name="name" select="@name"/>
  <xsl:for-each select="$config.src/*/document-types/document-type">
    <xsl:if test="@name=$name">
      <xsl:value-of select="position() - 1"/>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<!--
   Returns the smallest number used as a numerical code for a document type. Simply returns
   0. 
-->

<xsl:template name="config.document-type.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<!--
   Returns the largest number used as a numerical code for a document type. Computes the
   number of "document-type" elements, where counting starts with 0, and returnes that.
-->

<xsl:template name="config.document-type.code-max">
  <xsl:value-of select="count($config.src/*/document-types/document-type) - 1"/>
</xsl:template>

<!--
   Returns the Java constant that wraps document type $name. $name can be passed as a
   parameter; default is the "name" attribute of the current node. Works as follows: If the
   "document-type" element whose "name" attribute equals $name has a "constant" attribute,
   that is returned; otherwise, $name turned to upper case is retuned.
-->

<xsl:template name="config.document-type.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="constant"
                select="$config.src/*/document-types/document-type[@name=$name]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the data format of document type $name. $name can be passed as a parameter;
   default is the "name" attribute of the current node. Works as follows: First, checks if
   document type $name is generic. If, so, sets an auxiliary variable $lookup-name to the
   name of the corresponding "real" document type, otherwise, $lookup-name is set to
   $name. If the "document-type" element whose "name" attribute equals $lookup-name has a
   "format" attribute, returns its value; otherwise, returns "text".
-->

<xsl:template name="config.document-type.format">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="lookup-name">
    <xsl:call-template name="config.document-type.name-of-self-or-real">
      <xsl:with-param name="name" select="$name"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="format"
                select="$config.src/*/document-types/document-type[@name=$lookup-name]/@format"/>
  <xsl:choose>
    <xsl:when test="$format">
      <xsl:value-of select="$format"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'text'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the hint for document type $name. $name can be passed as a parameter; default is
   the "name" attribute of the current node. Works as follows: If the "document-type"
   element whose "name" attribute equals $name has a "hint" attribute, that is returned;
   otherwise, $name is returned. 
-->

<xsl:template name="config.document-type.hint">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="hint"
                select="$config.src/*/document-types/document-type[@name=$name]/@hint"/>
  <xsl:choose>
    <xsl:when test="$hint">
      <xsl:value-of select="$hint"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the role of document type $name. $name can be passed as a parameter; default is
   the "name" attribute of the current node. Simply returnes the @role attribute of the
   corresponding "document-type" element.
-->

<xsl:template name="config.document-type.role">
  <xsl:param name="name" select="@name"/>
  <xsl:value-of select="$config.src/*/document-types/document-type[@name=$name]/@role"/>
</xsl:template>

<!--
   Returns the name of the document type whose role is 'element'.
-->

<xsl:template name="config.document-type.role-element">
  <xsl:value-of select="$config.src/*/document-types/document-type[@role='element']/@name"/>
</xsl:template>

<!--
   Returns the name of the document type whose role is 'subelement'.
-->

<xsl:template name="config.document-type.role-subelement">
  <xsl:value-of select="$config.src/*/document-types/document-type[@role='subelement']/@name"/>
</xsl:template>

<!--
   Returns the name of the document type whose role is 'page'.
-->

<xsl:template name="config.document-type.role-page">
  <xsl:value-of select="$config.src/*/document-types/document-type[@role='page']/@name"/>
</xsl:template>

<!--
   Returns "yes" or "no" depending on whether document type $name has category or not. $name
   can be passed as a parameter; default is the "name" attribute of the current node. Works
   as follows: If the document type has the attribute "has-category" with the value of "yes",
   returns "yes"; otherwise, returns "no".
--> 

<xsl:template name="config.document-type.has-category">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="config.document-type.has-category"
                select="$config.src/*/document-types/document-type[@name=$name]/@has-category"/>
  <xsl:choose>
    <xsl:when test="$config.document-type.has-category='yes'">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>  
</xsl:template>

<!--
   Returns "yes" or "no" depending on whether document type $name has width and height or
   not. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: If the "document-type" element whose "name" attribute equals
   $name has a "has-width-and-height" attribute whose value is "yes", "yes" is returned;
   otherwise, "no" is returned.
-->

<xsl:template name="config.document-type.has-width-and-height">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when
      test="$config.src/*/document-types/document-type[@name=$name]/@has-width-and-height='yes'">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns "yes" or "no" depending on whether document type $name has a qualified name or
   not. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: If the "document-type" element whose "name" attribute equals
   $name has a "has-qualified-name" attribute whose value is "yes", "yes" is returned;
   otherwise, "no" is returned.
-->

<xsl:template name="config.document-type.has-qualified-name">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when
      test="$config.src/*/document-types/document-type[@name=$name]/@has-qualified-name='yes'">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns "yes" or "no" depending on whether document type $name has duration or not. $name
   can be passed as a parameter; default is the "name" attribute of the current node. Works
   as follows: If the "document-type" element whose "name" attribute equals $name has a
   "has-duration" attribute whose value is "yes", "yes" is returned; otherwise, "no" is
   returned.
-->

<xsl:template name="config.document-type.has-duration">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$config.src/*/document-types/document-type[@name=$name]/@has-duration='yes'">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns "yes" or "no" depending on whether document type $name has timeframe or not. $name
   can be passed as a parameter; default is the "name" attribute of the current node. Works
   as follows: If the "document-type" element whose "name" attribute equals $name has a
   "has-timeframe" attribute whose value is "yes", "yes" is returned; otherwise, "no" is
   returned.
-->

<xsl:template name="config.document-type.has-timeframe">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$config.src/*/document-types/document-type[@name=$name]/@has-timeframe='yes'">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns "yes" or "no" depending on whether document type $name has members or not. $name
   can be passed as a parameter; default is the "name" attribute of the current node. Works
   as follows: If the "document-type" element whose "name" attribute equals $name has a
   "has-members" attribute whose value is "yes", "yes" is returned; otherwise, "no" is
   returned.
-->

<xsl:template name="config.document-type.has-members">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$config.src/*/document-types/document-type[@name=$name]/@has-class='yes'">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns "yes" or "no" depending on whether document type $name has a corrector or not. $name
   can be passed as a parameter; default is the "name" attribute of the current node. Works
   as follows: If the "document-type" element whose "name" attribute equals $name has a
   "has-corrector" attribute whose value is "yes", "yes" is returned; otherwise, "no" is
   returned.
-->

<xsl:template name="config.document-type.has-corrector">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$config.src/*/document-types/document-type[@name=$name]/@has-corrector='yes'">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:when test="$config.src/*/document-types/document-type[@name=$name]/has-corrector">
      <xsl:value-of select="'yes'"/>      
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the XML element corresponding to the  document type with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "document-type" element whose "name" attribute
   equals $name. If that element has a "xml-element" attribute, that is returned; otherwise,
   $name is returned.
-->

<xsl:template name="config.document-type.xml-element-name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="xml-element"
                select="$config.src/*/document-types/document-type[@name=$name]/@xml-element"/>
  <xsl:choose>
    <xsl:when test="$xml-element">
      <xsl:value-of select="$xml-element"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns if documents of the type $name have text- or binary-sources. $name can be passed
   as a parameter; default is the "name" attribute of the current node. Works as follows:
   Looks up the "document-type" element whose "name" attribute equals $name. If that element
   has a "has-$format-source" attribute, that is returned, otherwise, the "no" is returned.

   The template checks the value of the "text-source" attribute (if any). The allowed values and
   their meanings are:

     no        Documents of this type do not have texts as sources.

     yes       Each document of this type can can have any number of sources in the given format,
               including 0.
-->

<xsl:template name="config.document-type.has-source">
  <xsl:param name="name" select="@name"/>
  <xsl:param name="format"/>
  <xsl:variable name="has-source">
  <xsl:choose>
    <xsl:when test="$format='text'">
      <xsl:value-of select="$config.src/*/document-types/document-type[@name=$name]/@has-text-source"/>
    </xsl:when>
    <xsl:when test="$format='binary'">
      <xsl:value-of select="$config.src/*/document-types/document-type[@name=$name]/@has-binary-source"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.error">
        <xsl:with-param name="message" select="concat('DEBUG config.document-type.has-source / No such source format ',$format)"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="not($has-source) or $has-source='no' or $has-source='false'">
      <xsl:value-of select="'no'"/>
    </xsl:when>
    <xsl:when test="$has-source='yes' or $has-source='true'">
      <xsl:value-of select="$has-source"/>
    </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns if and how documents of the type $name are filed in sections. $name can be passed
   as a parameter; default is the "name" attribute of the current node. Works as follows:
   Looks up the "document-type" element whose "name" attribute equals $name. If that element
   has a "filed" attribute, that is returned, otherwise, the "no" is returned.

   The template checks the value of the "filed" attribute (if any). The allowed values and
   their meanings are:

     no        Documents of this type are not filed.

     unique    Each document of this type is contained in exactly one section.

     multiple  Each document of this type can be contained in any number of sections,
               including 0.
-->

<xsl:template name="config.document-type.filed">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="filed"
                select="$config.src/*/document-types/document-type[@name=$name]/@filed"/>
  <xsl:choose>
    <xsl:when test="not($filed) or $filed='no'">
      <xsl:value-of select="'no'"/>
    </xsl:when>
    <xsl:when test="$filed='unique' or $filed='multiple'">
      <xsl:value-of select="$filed"/>
    </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
  The name of the document type that is used for thumbnails.
-->

<xsl:variable name="config.thumbnail.document-type.name">
  <xsl:variable name="thumbnail.document-type.name" select="/*/document-types/@thumbnail"/>
  <xsl:choose>
    <xsl:when test="$thumbnail.document-type.name">
      <xsl:call-template name="config.document-type.check-existence">
        <xsl:with-param name="name" select="$thumbnail.document-type.name"/>
      </xsl:call-template>
      <xsl:value-of select="$thumbnail.document-type.name"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.error">
        <xsl:with-param name="message" select="'No thumbnail document type specified'"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<!--
  The name of the document type that is used for correctors.
-->

<xsl:variable name="config.corrector.document-type.name">
  <xsl:variable name="corrector.document-type.name" select="/*/document-types/@corrector"/>
  <xsl:choose>
    <xsl:when test="$corrector.document-type.name">
      <xsl:call-template name="config.document-type.check-existence">
        <xsl:with-param name="name" select="$corrector.document-type.name"/>
      </xsl:call-template>
      <xsl:value-of select="$corrector.document-type.name"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.error">
        <xsl:with-param name="message" select="'No corrector document type specified'"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:template name="config.document-type.is-corrector">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="$name = $config.corrector.document-type.name">yes</xsl:when>
    <xsl:otherwise>no</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
  Decides whether a reference table on a given pair of document types shall exist.
  Takes as parameters
   1. a <document-type> node from config.xml as $from-doctype-node
   2. the name of a document type as $to-doctype
  Returns 'yes' or 'no' or fails with an error message depending on the xml-attributes
  'refs-to' and 'no-refs-to' of the given from-doc-node.
  Works such:

           \ refs-to  #  not                #   '*'               #  list of             #
  no-refs-to\         # existent            #                     # doctype names        #
  ########################################################################################
                      # returns             # returns             # 'yes', if to-doctype #
  not existent        #  'yes'              #  'yes'              #  is in the list      #
                      #                     #                     # 'no' otherwise       #
  ########################################################################################
                      # returns             #                     # 'yes', if to-doctype #
    '*'               #   'no'              #  fail               #  is in the list      #
                      #                     #                     # 'no' otherwise       #
  ########################################################################################
  list of             # 'no', if to-doctype # 'no', if to-doctype # fails if to-doctype  #
  doctype names       # is in the list      # is in the list      #  is in both lists    #
                      # 'yes' otherwise     # 'yes' otherwise     # 'yes' if to-doctype  #
  #################################################################  is in refs-to list  #
                                                                  # 'no' otherwise       #
                                                                  ########################
-->

<xsl:template name="config.document-type.pair.refs-to">
  <xsl:param name="from-doctype-node"/>
  <xsl:param name="to-doctype"/>
  <xsl:choose><!--generic-->
    <xsl:when test="$from-doctype-node/@is-generic-of">no</xsl:when>
    <xsl:otherwise>
      <xsl:variable name="refs-to-attr" select="$from-doctype-node/@refs-to"/>
      <xsl:variable name="no-refs-to-attr" select="$from-doctype-node/@no-refs-to"/>
      <xsl:choose>
        <xsl:when test="not($refs-to-attr)"><!--refs-to attribute is not given-->
          <xsl:choose><!--no-refs-to-->
            <xsl:when test="not($no-refs-to-attr)">yes</xsl:when>
            <xsl:when test="$no-refs-to-attr and $no-refs-to-attr='*'">no</xsl:when>
            <xsl:when test="$no-refs-to-attr and x-strutil:listContains(string($no-refs-to-attr), string($to-doctype))">no</xsl:when>
            <xsl:otherwise>yes</xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="$refs-to-attr and $refs-to-attr='*'"><!--refs-to attribute is given and says 'all'-->
          <xsl:choose><!--no-refs-to-->
            <xsl:when test="not($no-refs-to-attr)">yes</xsl:when>
            <xsl:when test="$no-refs-to-attr and $no-refs-to-attr='*'">
              <xsl:call-template name="config.error">
                <xsl:with-param name="message" select="'ambiguous: refs-to=* and no-refs-to=*'"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$no-refs-to-attr and x-strutil:listContains(string($no-refs-to-attr), string($to-doctype))">no</xsl:when>
            <xsl:otherwise>yes</xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise><!--refs-to attribute is a list of doctypes-->
          <xsl:choose><!--no-refs-to-->
            <xsl:when test="not($no-refs-to-attr) and x-strutil:listContains(string($refs-to-attr), string($to-doctype))">yes</xsl:when>
            <xsl:when test="$no-refs-to-attr and $no-refs-to-attr='*' and x-strutil:listContains(string($refs-to-attr), string($to-doctype))">yes</xsl:when>
            <xsl:when test="$no-refs-to-attr and x-strutil:listContains(string($refs-to-attr), string($to-doctype))">
              <xsl:choose>
                <xsl:when test="x-strutil:listContains(string($no-refs-to-attr), string($to-doctype))">
                  <xsl:call-template name="config.error">
                    <xsl:with-param name="message" select="'ambiguous: refs-to and no-refs-to both contain the same doctype'"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>yes</xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:otherwise>no</xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose><!--refs-to-->
    </xsl:otherwise>
  </xsl:choose><!--generic-->
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning pseudo-document types
   ==========================================================================================
-->

<xsl:template name="config.pseudodoc-type.code">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/pseudodoc-types/pseudodoc-type">
      <xsl:if test="@name=$name">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of
    select="count($config.src/*/pseudodoc-types/pseudodoc-type[position()&lt;$position])"/>
</xsl:template>

<xsl:template name="config.pseudodoc-type.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.pseudodoc-type.code-max">
  <xsl:value-of select="count($config.src/*/pseudodoc-types/pseudodoc-type) - 1"/>
</xsl:template>

<!--
   Returns the name of the database table corresponding to pseudo-document type
   $pseudodoc-type. $pseudodoc-type can be passed as a parameter; default is the "name"
   attribute of the current node (so this template is intended to be applied to the
   "pseudodoc-type" elements"). Returns $pseudodoc-type with an "s" appended.
-->

<xsl:template name="config.db-table-name.pseudo-document">
  <xsl:param name="pseudodoc-type" select="@name"/>
  <xsl:value-of select="$pseudodoc-type"/>
  <xsl:choose>
    <xsl:when test="substring($pseudodoc-type,string-length($pseudodoc-type))='s'">es</xsl:when>
    <xsl:otherwise>s</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning use modes
   ==========================================================================================
-->

<xsl:template name="config.use-mode.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.use-mode.code-max">
  <xsl:value-of select="count($config.src/*/use-modes/use-mode) - 1"/>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning categories
   ==========================================================================================
-->

<!--
   Checks whether a document type with name $name exists. If not, terminates with an error
   message.
-->

<xsl:template name="config.category.check-existence">
  <xsl:param name="name" select="@name"/>
  <xsl:if test="not($config.src/*/categories/category[@name=$name])">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message" select="concat('Cannot find category for name: ',$name)"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<!--
  Returns the category id for a category name. The latter is specified as a parameter "name"
  which defaults to the "name" attribute of the current node.
-->

<xsl:template name="config.category.code">
  <xsl:param name="name" select="@name"/>
  <xsl:for-each select="$config.src/*/categories/category">
    <xsl:if test="@name=$name">
      <xsl:value-of select="position() - 1"/>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<!--
  Returns the category name for a category id. The latter is specified as a parameter "id"
  which defaults to the "id" attribute of the current node.
-->

<xsl:template name="config.category.name">
  <xsl:param name="id" select="@id"/>
  <xsl:variable name="thisid" select="$id"></xsl:variable>
  <xsl:for-each select="$config.src/*/categories/category">
    <xsl:if test="$thisid=position()-1">
      <xsl:value-of select="@name"/>
    </xsl:if>
  </xsl:for-each>
</xsl:template>


<xsl:template name="config.category.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.category.code-max">
  <xsl:value-of select="count($config.src/*/categories/category[position()&lt;last()])"/>
</xsl:template>

<!--
  The name of the category of correctors.
-->

<xsl:variable name="config.corrector.category.name">
  <xsl:variable name="corrector.category.name" select="/*/categories/@corrector"/>
  <xsl:choose>
    <xsl:when test="$corrector.category.name">
      <xsl:call-template name="config.category.check-existence">
        <xsl:with-param name="name" select="$corrector.category.name"/>
      </xsl:call-template>
      <xsl:value-of select="$corrector.category.name"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.error">
        <xsl:with-param name="message" select="'No corrector category specified'"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<!--
   ==========================================================================================
   Templates concerning semesteres
   ==========================================================================================

DEPRECATED:

<xsl:template name="config.semester.code">
  <xsl:param name="name" select="@name"/>
  <xsl:for-each select="$config.src/*/semesters/semester">
    <xsl:if test="@name=$name">
      <xsl:value-of select="position() - 1"/>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template name="config.semester.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.semester.code-max">
  <xsl:value-of select="count($config.src/*/semesters/semester[position()&lt;last()])"/>
</xsl:template>

-->

<!--
   ==========================================================================================
   Templates concerning database tables
   ==========================================================================================
-->

<!--
   Checks if a database table with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.db-table.name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/db-tables/db-table[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find database table for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the database table with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "db-table" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.db-table.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/db-tables/db-table[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find database table for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the database view with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "db-view" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.db-view.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/db-views/db-view[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find database view for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the database table corresponding to document type $doctype. $doctype
   can be passed as a parameter; default is the "name" attribute of the current node (so
   this template is intended to be applied to the "document-type" elements"). Returns
   $doctype with an "s" appended.
-->

<xsl:template name="config.db-table-name.document">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="$doctype"/>
  <xsl:choose>
    <xsl:when test="substring($doctype,string-length($doctype))='s'">es</xsl:when>
    <xsl:otherwise>s</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the database table of vc_threads corresponding to document type 
   $doctype. $doctype
   can be passed as a parameter; default is the "name" attribute of the current node (so
   this template is intended to be applied to the "document-type" elements"). Returns
   'vc_threads_' with $doctype appended.
-->

<xsl:template name="config.db-table-name.vc_threads">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="'vc_threads_'"/>
  <xsl:value-of select="$doctype"/>
</xsl:template>

<!--
   Returns the name of the database view corresponding to document type $doctype. $doctype
   can be passed as a parameter; default is the "name" attribute of the current node (so
   this template is intended to be applied to the "document-type" elements"). Returns
   "latest_" with $doctype appended with an "s" appended.
-->

<xsl:template name="config.db-view-name.latest-document">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="'latest_'"/>
  <xsl:value-of select="$doctype"/>
  <xsl:choose>
    <xsl:when test="substring($doctype,string-length($doctype))='s'">es</xsl:when>
    <xsl:otherwise>s</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the database table that describes the references from $from-doctype
   to $to-doctype (two document type names). $from-doctype must be passed as a parameter;
   $to-doctype can be passed as a parameter, but also has a default: the "name" attribute of
   the current node. The table name is composed as follows:

     refs_$from-doctype_$to-doctype
-->

<xsl:template name="config.db-table-name.refs-document-document">
  <xsl:param name="from-doctype"/>
  <xsl:param name="to-doctype" select="@name"/>
  <xsl:value-of select="'refs_'"/>
  <xsl:value-of select="$from-doctype"/>
  <xsl:value-of select="'_'"/>
  <xsl:value-of select="$to-doctype"/>
</xsl:template>

<!--
   Returns the name of the database table that stores annotations to documents of type 
   $doctype (a document type name).
   noted by $originator ('user' or 'user_group'). 
   $doctype must be passed as a parameter.
   $originator can be passed as a parameter, default is 'user'.
   The table name is composed as follows:

     anns_$originator_$doctype
-->

<xsl:template name="config.db-table-name.anns-single-document">
  <xsl:param name="originator" select="'user'"/>
  <xsl:param name="doctype"/>
  <xsl:value-of select="'anns_'"/>
  <xsl:value-of select="$originator"/>
  <xsl:value-of select="'_'"/>
  <xsl:value-of select="$doctype"/>
</xsl:template>

<!--
   Returns the name of the database table that stores annotations to the references from 
   $from-doctype to $to-doctype (two document type names). 
   noted by $originator ('user' or 'user_group'). 
   $from-doctype must be passed as a parameter; 
   $to-doctype must be passed as a parameter. 
   $originator can be passed as a parameter, default is 'user'.
   The table name is composed as follows:

     anns_$originator_$from-doctype_$to-doctype
-->

<xsl:template name="config.db-table-name.anns-document-document">
  <xsl:param name="originator" select="'user'"/>
  <xsl:param name="from-doctype"/>
  <xsl:param name="to-doctype"/>
  <xsl:value-of select="'anns_'"/>
  <xsl:value-of select="$originator"/>
  <xsl:value-of select="'_'"/>
  <xsl:value-of select="$from-doctype"/>
  <xsl:value-of select="'_'"/>
  <xsl:value-of select="$to-doctype"/>
</xsl:template>

<!--
   Returns the name of the database table that describes which authors the documents of type
   $doctype have. $doctype can be passed as a parameter; default is the "name" attribute of
   the current node. The table name is composed as follos:

     ${doctype}_authors
-->

<xsl:template name="config.db-table-name.document-authors">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="$doctype"/>
  <xsl:value-of select="'_authors'"/>
</xsl:template>

<!--
   Returns the name of the database view that describes which members the documents of type
   $doctype have. $doctype can be passed as a parameter; default is the "name" attribute of
   the current node. The view name is composed as follos:

     ${doctype}_members
-->

<xsl:template name="config.db-view-name.document-members">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="$doctype"/>
  <xsl:value-of select="'_members'"/>
</xsl:template>

<!--
   Returns the name of the database table that records answers to documents of type $doctype
   ((generic_)(sub)element) in courses of type course_subsection.
   $doctype can be passed as a parameter; default is the "name" attribute of
   the current node. The table name is composed as follos:

     $doctype_answers
-->

<xsl:template name="config.db-table-name.document-answers">
  <xsl:param name="doctype"/>
  <xsl:choose>
    <xsl:when test="$doctype">
      <xsl:value-of select="$doctype"/>
      <xsl:value-of select="'_answers'"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.error">
        <xsl:with-param name="message" select="'parameter doctype missing'"/>
      </xsl:call-template>
   </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the database table that links documents of type $doctype 
   to their sources of format $format. $doctype can be passed as a parameter;
   default is the "name" attribute of the current node. The table name is composed as follos:

     $doctype_$format_sources
-->

<xsl:template name="config.db-table-name.refs-document-source">
  <xsl:param name="doctype" select="@name"/>
  <xsl:param name="format"/>
  <xsl:value-of select="$doctype"/>
  <xsl:value-of select="'_'"/>
  <xsl:value-of select="$format"/>
  <xsl:value-of select="'_sources'"/>
</xsl:template>

<!--
   Returns the name of the database table that describes which user groups may read the 
   documents of type $doctype. $doctype can be passed as a parameter; default is the 
   "name" attribute of the current node. The table name is composed as follos:

     read_{$doctype}s
-->

<xsl:template name="config.db-table-name.read-permissions">
  <xsl:param name="type" select="@name"/>
  <xsl:value-of select="'read_'"/>
  <xsl:value-of select="$type"/>
  <xsl:choose>
    <xsl:when test="substring($type,string-length($type))='s'">es</xsl:when>
    <xsl:otherwise>s</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the database table that describes which user groups may write the 
   vc_threads of type $doctype have. $doctype can be passed as a parameter; default is the 
   "name" attribute of the current node. The table name is composed as follos:

     write_{$doctype}s
-->

<xsl:template name="config.db-table-name.write-permissions">
  <xsl:param name="type" select="@name"/>
  <xsl:value-of select="'write_'"/>
  <xsl:value-of select="$type"/>
  <xsl:choose>
    <xsl:when test="substring($type,string-length($type))='s'">es</xsl:when>
    <xsl:otherwise>s</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the theme map database table of document type $doctype. $doctype can
   be passed as a parameter; default is the "name" attribute of the current node. The table
   name is composed as follows:

     theme_map_$doctype
-->

<xsl:template name="config.db-table-name.theme-map-document">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="'gdim_'"/>
  <xsl:value-of select="$doctype"/>
</xsl:template>

<!--
   Returns the name of the gdim database table of document type $doctype. $doctype can
   be passed as a parameter; default is the "name" attribute of the current node. The table
   name is composed as follows:

     gdim_$doctype
-->

<xsl:template name="config.db-table-name.gdim-document">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="'gdim_'"/>
  <xsl:value-of select="$doctype"/>
</xsl:template>

<!--
   Returns the name of the database table sources in $format (text or binary). The table
   name is composed as follows:

     $format_sources
-->

<xsl:template name="config.db-table-name.sources">
  <xsl:param name="format"/>
  <xsl:value-of select="$format"/>
  <xsl:value-of select="'_sources'"/>
</xsl:template>

<!--
   Returns the name of the database rule that protects defaults for gdim criteria instances from being deleted of updated. The rule
   name is composed as follows:

     "default-"$criterion"-"$action"-rule"
-->

<xsl:template name="config.db-rule-name.gdim-default">
  <xsl:param name="criterion"/>
  <xsl:param name="action"/>
  <xsl:value-of select="'base_'"/>
  <xsl:value-of select="$criterion"/>
  <xsl:value-of select="'_'"/>
  <xsl:value-of select="$action"/>
  <xsl:value-of select="'_rule'"/>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning database columns
   ==========================================================================================
-->

<!--
   Checks if a database column with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.db-column.name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/db-columns/db-column[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find database column for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the database column with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "db-column" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.db-column.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/db-columns/db-column[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find database column for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of the database column that stores the id of the document of the type
   $container.doctype.name which contains this document. $container.doctype.name can be
   passed as a parameter; default is the "document-type" attribute of the current node.
   The name is composed as follows:

     contained_in_$container.doctype.name
-->

<xsl:template name="config.db-column.contained-in-document.name">
  <xsl:param name="container.doctype.name" select="@document-type"/>
  <xsl:value-of select="concat('contained_in_',$container.doctype.name)"/>
</xsl:template>

<!--
   Returns the name of the database column that specifies whether user_group has 
   the permission to create a new vc_thread with an initial document of type $doctype;
   $doctype must be passed as a parameter;

     may_write_$doctype
-->

<xsl:template name="config.db-column-name.may-create-document">
  <xsl:param name="doctype-name" select="@name"/>
  <xsl:value-of select="'may_write_'"/>
  <xsl:value-of select="$doctype-name"/>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning database constants
   ==========================================================================================
-->

<!--
   Returns the value of the db constant with a given name. Expects one parameter:

     name  The name of the db constant. Defaults to the "name" attribute of
           the current node.
-->

<xsl:template name="config.db-constant.value">
  <xsl:param name="name" select="@name"/>
  <xsl:choose>
    <xsl:when test="not($config.src/*/db-constants/db-constant[@name=$name])">
      <xsl:call-template name="config.error">
        <xsl:with-param name="message"
                        select="concat('Can not find db constant for name: ',$name)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="value"
                    select="$config.src/*/db-constants/db-constant[@name=$name]/@value"/>
      <xsl:choose>
        <xsl:when test="not($value)">
          <xsl:call-template name="config.error">
            <xsl:with-param name="message"
                            select="concat('No value for db constant: ',$name)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$value"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates to compose names of database functions, rules, triggers, etc.
   ==========================================================================================
-->

<xsl:template name="config.db-rule-name.new-vc-thread">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="'new_'"/>
  <xsl:value-of select="$doctype"/>
  <xsl:value-of select="'_vc_thread'"/>
</xsl:template>

<xsl:template name="config.db-sequence-name.for-serial">
  <xsl:param name="table-name"/>
  <xsl:param name="column-name"/>
  <xsl:value-of select="$table-name"/>
  <xsl:text>_</xsl:text>
  <xsl:value-of select="$column-name"/>
  <xsl:text>_seq</xsl:text>
</xsl:template>

<xsl:template name="config.db-constraint-name.ref-attribute">
  <xsl:param name="table-name"/>
  <xsl:param name="column-name"/>
  <xsl:value-of select="$table-name"/>
  <xsl:text>_</xsl:text>
  <xsl:value-of select="$column-name"/>
  <xsl:text>_constraint</xsl:text>
</xsl:template>

<xsl:template name="config.db-trigger-name.set-last-modified">
  <xsl:param name="type-name" select="@name"/>
  <xsl:value-of select="concat('set_', $type-name, '_last_modified')"/>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning XML elements
   ==========================================================================================
-->

<!--
   Returns the name of the XML element corresponding to the the database column with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the 
   current node. Works as follows: Looks up the "db-column" element whose "name" attribute
   equals $name. If that element has a "xml-element" attribute, that is returned; otherwise,
   $name is returned. 
-->

<xsl:template name="config.db-column.xml-element-name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/db-columns/db-column[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find database column for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:variable name="xml-element" select="$node/@xml-element"/>
  <xsl:choose>
    <xsl:when test="$xml-element">
      <xsl:value-of select="$xml-element"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name of Java constant wrapping the name of the XML element corresponding to
   the database column with name $name. $name can be passed as a parameter; default is the
   "name" attribute of the current node. Works as follows: Looks up the "db-column" element
   whose "name" attribute equals $name. If that element has a "xml-element-constanr"
   attribute, that is returned; otherwise, $name turned to upper case is returned. 
-->

<xsl:template name="config.db-column.xml-element-constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/db-columns/db-column[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find database column for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:variable name="xml-element-constant" select="$node/@xml-element-constant"/>
  <xsl:choose>
    <xsl:when test="$xml-element-constant">
      <xsl:value-of select="$xml-element-constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Checks if an XML element with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.xml-element.name">
  <xsl:param name="name" select="@name"/>
  <xsl:if test="not($config.src/*/xml-elements/xml-element[@name=$name])">
    <!-- Name is not declared in "xml-elements" section: -->
    <xsl:variable name="db-column-check">
      <xsl:for-each select="$config.src/*/db-columns/db-column">
        <xsl:variable name="name-declared-by-db-column">
          <xsl:call-template name="config.db-column.xml-element-name"/>
        </xsl:variable>
        <xsl:if test="$name-declared-by-db-column=$name">
          <xsl:value-of select="'PASSED'"/>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:if test="not($db-column-check='PASSED')">
      <!-- Name is not indirectly declared in "db-columns" section, either: -->
      <xsl:call-template name="config.error">
        <xsl:with-param name="message"
                        select="concat('Cannot find XML element for name: ', $name)"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the XML element with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "xml-element" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.xml-element.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/xml-elements/xml-element[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find XML element for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning XML attributes
   ==========================================================================================
-->

<!--
   Checks if an XML attribute with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.xml-attribute.name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/xml-attributes/xml-attribute[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find XML attribute for name: ',$name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the XML attribute with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "xml-attribute" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.xml-attribute.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/xml-attributes/xml-attribute[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find XML attribute for name: ',$name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning media types
   ==========================================================================================
-->

<!--
  Checks if a given media type is declared in the config.xml file. If not, aborts with an
  error message. Expects up to two parameters:

    type     The major part of the media type. Defaults to the "type" attribute of
             the current node.

    subtype  The minor part of the media type. Defaults to the "subtype" attribute of
             the current node.
-->

<xsl:template name="config.media-type.check">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:if test="not($config.src/*/media-types/media-type[@type=$type and @subtype=$subtype])">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
                      select="concat('Cannot find media type: ',$type,'/',$subtype)"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<!--
   Returns the numerical code for a given media type. Expects up to two parameters:

     type     The major part of the media type. Defaults to the "type" attribute of
              the current node.

     subtype  The minor part of the media type. Defaults to the "subtype" attribute of
              the current node.
-->

<xsl:template name="config.media-type.code">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:call-template name="config.media-type.check">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/media-types/media-type">
      <xsl:if test="@type=$type and @subtype=$subtype">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of select="count($config.src/*/media-types/media-type[position()&lt;$position])"/>
</xsl:template>

<!--
   Returns the smallest number used as a numerical code for a media type.
-->

<xsl:template name="config.media-type.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<!--
   Returns the largest number used as a numerical code for a media type.
-->

<xsl:template name="config.media-type.code-max">
  <xsl:value-of select="count($config.src/*/media-types/media-type[position()&lt;last()])"/>
</xsl:template>

<!--
   Returns the Java constant used for a given media type. Expects up to two parameters:

     type     The major part of the media type. Defaults to the "type" attribute of
              the current node.

     subtype  The minor part of the media type. Defaults to the "subtype" attribute of
              the current node.

   Returns the value of the "constant" attribute of the media types's node in the config file,
   or, if that attribute does not exist, the string

     $TYPE_$SUBTYPE

   where $TYPE and $SUBTYPE denote the "type" and "subtype" parameters converted to upper case
   and "-"'s replaced by "_"'s.
-->

<xsl:template name="config.media-type.constant">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:call-template name="config.media-type.check">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:variable name="constant"
    select="$config.src/*/media-types/media-type[@type=$type and @subtype=$subtype]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string">
          <xsl:value-of select="translate($type,'-','_')"/>
          <xsl:value-of select="'_'"/>
          <xsl:value-of select="translate($subtype,'-','_')"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name for a given media type. Expects up to two parameters:

     type     The major part of the media type. Defaults to the "type" attribute of
              the current node.

     subtype  The minor part of the media type. Defaults to the "subtype" attribute of
              the current node.

   Returns the string

     $type/$subtype

   where $type and $subtype denote the values of the "type" and "subtype" parameters.
-->

<xsl:template name="config.media-type.name">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:call-template name="config.media-type.check">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:value-of select="$type"/>
  <xsl:value-of select="'/'"/>
  <xsl:value-of select="$subtype"/>
</xsl:template>

<!--
   Returns the suffix for a given media type. Expects up to two parameters:

     type     The major part of the media type. Defaults to the "type" attribute of
              the current node.

     subtype  The minor part of the media type. Defaults to the "subtype" attribute of
              the current node.

   Returns the value of the "suffix" attribute of the media types's node in the config file,
   or, if that attribute does not exist, the value of the "subtype" attribute.
-->

<xsl:template name="config.media-type.suffix">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="subtype" select="@subtype"/>
  <xsl:call-template name="config.media-type.check">
    <xsl:with-param name="type" select="$type"/>
    <xsl:with-param name="subtype" select="$subtype"/>
  </xsl:call-template>
  <xsl:variable name="suffix"
    select="$config.src/*/media-types/media-type[@type=$type and @subtype=$subtype]/@suffix"/>
  <xsl:choose>
    <xsl:when test="$suffix">
      <xsl:value-of select="$suffix"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$subtype"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning user roles
   ==========================================================================================
-->

<!--
  Checks if a given user role is declared in the config.xml file. If not, aborts with an
  error message. Expects up to one parameter:

    name   The name of the user role. Defaults to the "name" attribute of the current
           node.
-->

<xsl:template name="config.user-role.check">
  <xsl:param name="name" select="@name"/>
  <xsl:if test="not($config.src/*/user-roles/user-role[@name=$name])">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
                      select="concat('Cannot find user role: ',$name)"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<!--
   Returns the numerical code for a given user role. Expects up to one parameter:

    name   The name of the user role. Defaults to the "name" attribute of the current
           node.
-->

<xsl:template name="config.user-role.code">
  <xsl:param name="name" select="@name"/>
  <xsl:call-template name="config.user-role.check">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/user-roles/user-role">
      <xsl:if test="@name=$name">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of select="$position - 1"/>
</xsl:template>

<!--
   Returns the smallest number used as a numerical code for a user role.
-->

<xsl:template name="config.user-role.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<!--
   Returns the largest number used as a numerical code for a user role.
-->

<xsl:template name="config.user-role.code-max">
  <xsl:value-of select="count($config.src/*/user-roles/user-role[position()&lt;last()])"/>
</xsl:template>

<!--
   Returns the Java constant used for a given user role. Expects up to one parameter:

    name   The name of the user role. Defaults to the "name" attribute of the current
           node.

   Returns the value of the "constant" attribute of the user roles's node in the config file,
   or, if that attribute does not exist, the value of the "name" parameter converted to upper
   case and "-"'s replaced by "_"'s.
-->

<xsl:template name="config.user-role.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:call-template name="config.user-role.check">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:variable name="constant"
    select="$config.src/*/user-roles/user-role[@name=$name]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string">
          <xsl:value-of select="translate($name,'-','_')"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name for a given user role. Expects up to one parameter:

    name   The name of the user role. Defaults to the "name" attribute of the current
           node.

   Returns the value of that parameter after performing the usual check whether this user
   role is defined.
-->

<xsl:template name="config.user-role.name">
  <xsl:param name="name" select="@name"/>
  <xsl:call-template name="config.user-role.check">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning file roles
   ==========================================================================================
-->

<!--
  Checks if a given file role is declared in the config.xml file. If not, aborts with an
  error message. Expects up to one parameter:

    name   The name of the file role. Defaults to the "name" attribute of the current
           node.
-->

<xsl:template name="config.file-role.check">
  <xsl:param name="name" select="@name"/>
  <xsl:if test="not($config.src/*/file-roles/file-role[@name=$name])">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
                      select="concat('Cannot find file role: ',$name)"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<!--
   Returns the numerical code for a given file role. Expects up to one parameter:

    name   The name of the file role. Defaults to the "name" attribute of the current
           node.
-->

<xsl:template name="config.file-role.code">
  <xsl:param name="name" select="@name"/>
  <xsl:call-template name="config.file-role.check">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/file-roles/file-role">
      <xsl:if test="@name=$name">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of select="$position - 1"/>
</xsl:template>

<!--
   Returns the smallest number used as a numerical code for a file role.
-->

<xsl:template name="config.file-role.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<!--
   Returns the largest number used as a numerical code for a file role.
-->

<xsl:template name="config.file-role.code-max">
  <xsl:value-of select="count($config.src/*/file-roles/file-role[position()&lt;last()])"/>
</xsl:template>

<!--
   Returns the Java constant used for a given file role. Expects up to one parameter:

    name   The name of the file role. Defaults to the "name" attribute of the current
           node.

   Returns the value of the "constant" attribute of the file roles's node in the config file,
   or, if that attribute does not exist, the value of the "name" parameter converted to upper
   case and "-"'s replaced by "_"'s.
-->

<xsl:template name="config.file-role.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:call-template name="config.file-role.check">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:variable name="constant"
    select="$config.src/*/file-roles/file-role[@name=$name]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string">
          <xsl:value-of select="translate($name,'-','_')"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the name for a given file role. Expects up to one parameter:

    name   The name of the file role. Defaults to the "name" attribute of the current
           node.

   Returns the value of that parameter after performing the usual check whether this file
   role is defined.
-->

<xsl:template name="config.file-role.name">
  <xsl:param name="name" select="@name"/>
  <xsl:call-template name="config.file-role.check">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the suffix for a given file role. Expects up to one parameter:

    name   The name of the file role. Defaults to the "name" attribute of the current
           node.

   Returns the value of the "suffix" attribute of the file roles's node in the config file,
   or, if that attribute does not exist, the value of the "name" attribute.
-->

<xsl:template name="config.file-role.suffix">
  <xsl:param name="name" select="@name"/>
  <xsl:call-template name="config.file-role.check">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  <xsl:variable name="suffix"
    select="$config.src/*/file-roles/file-role[@name=$name]/@suffix"/>
  <xsl:choose>
    <xsl:when test="$suffix">
      <xsl:value-of select="$suffix"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning data formats
   ==========================================================================================
-->

<xsl:template name="config.data-format.code">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/data-formats/data-format">
      <xsl:if test="@name=$name">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of select="count($config.src/*/data-formats/data-format[position()&lt;$position])"/>
</xsl:template>

<xsl:template name="config.data-format.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.data-format.code-max">
  <xsl:value-of select="count($config.src/*/data-formats/data-format) - 1"/>
</xsl:template>

<xsl:template name="config.data-format.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="constant"
                select="$config.src/*/data-formats/data-format[@name=$name]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning reference types
   ==========================================================================================
-->

<xsl:template name="config.reference-type.code">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/reference-types/reference-type">
      <xsl:if test="@name=$name">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of
    select="count($config.src/*/reference-types/reference-type[position()&lt;$position])"/>
</xsl:template>

<xsl:template name="config.reference-type.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.reference-type.code-max">
  <xsl:value-of select="count($config.src/*/reference-types/reference-type) - 1"/>
</xsl:template>

<xsl:template name="config.reference-type.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="constant"
                select="$config.src/*/reference-types/reference-type[@name=$name]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning annotation types
   ==========================================================================================
-->

<xsl:template name="config.annotation-type.code">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/annotation-types/annotation-type">
      <xsl:if test="@name=$name">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of
    select="count($config.src/*/annotation-types/annotation-type[position()&lt;$position])"/>
</xsl:template>

<xsl:template name="config.annotation-type.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.annotation-type.code-max">
  <xsl:value-of select="count($config.src/*/annotation-types/annotation-type) - 1"/>
</xsl:template>

<xsl:template name="config.annotation-type.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="constant"
                select="$config.src/*/annotation-types/annotation-type[@name=$name]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning time formats
   ==========================================================================================
-->

<xsl:template name="config.time-format.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="constant"
                select="$config.src/*/time-formats/time-format[@name=$name]/@constant"/>
  <xsl:choose>
    <xsl:when test="$constant">
      <xsl:value-of select="$constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning XML namespaces
   ==========================================================================================
-->

<!--
   Checks if an XML namespace with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.xml-namespace.name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/xml-namespaces/xml-namespace[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find XML namespace for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<xsl:template name="config.xml-namespace.uri.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/xml-namespaces/xml-namespace[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find XML namespace for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:variable name="uri-constant" select="$node/@uri-constant"/>
  <xsl:choose>
    <xsl:when test="$uri-constant">
      <xsl:value-of select="$uri-constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'URI_'"/>
      <xsl:call-template name="config.upcase-and-dashes-to-underscores">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="config.xml-namespace.prefix.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/xml-namespaces/xml-namespace[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find XML namespace for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:variable name="prefix-constant" select="$node/@prefix-constant"/>
  <xsl:choose>
    <xsl:when test="$prefix-constant">
      <xsl:value-of select="$prefix-constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'PREFIX_'"/>
      <xsl:call-template name="config.upcase-and-dashes-to-underscores">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning request parameters
   ==========================================================================================
-->

<!--
   Checks if a request parameter with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.request-param.name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/request-params/request-param[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find request parameter for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the request parameter with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "request-param" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.request-param.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/request-params/request-param[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find request parameter for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase-and-dashes-to-underscores">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning default user groups
   ==========================================================================================
-->

<!--
   Checks if a default user group with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.default-user-group.name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/default-user-groups/default-user-group[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find default user group for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the default user group with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "default-user-group" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.default-user-group.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/default-user-groups/default-user-group[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find default user group for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase-and-dashes-to-underscores">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning sync commands
   ==========================================================================================
-->

<!--
   Checks if a sync command with name $name is declared. If so, returns $name, if not,
   terminates transformation with an error message.
-->

<xsl:template name="config.sync-command.name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/sync-commands/sync-command[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find sync command for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:value-of select="$name"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the name of the sync command with name
   $name. $name can be passed as a parameter; default is the "name" attribute of the current
   node. Works as follows: Looks up the "sync-command" element whose "name" attribute equals
   $name. If that element has a "constant" attribute, that is returned; otherwise, $name
   turned to upper case is returned. 
-->

<xsl:template name="config.sync-command.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/sync-commands/sync-command[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find sync command for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase-and-dashes-to-underscores">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Returns the hint for synchronization command $name. $name can be passed as a parameter;
   default is the "name" attribute of the current node. Works as follows: If the "sync-command"
   element whose "name" attribute equals $name has a "hint" attribute, that is returned;
   otherwise, $name is returned. 
-->

<xsl:template name="config.sync-command.hint">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="hint"
                select="$config.src/*/sync-commands/sync-command[@name=$name]/@hint"/>
  <xsl:choose>
    <xsl:when test="$hint">
      <xsl:value-of select="$hint"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning reference attributes
   ==========================================================================================
-->

<xsl:template name="config.ref-attribute.db-column-name">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="db-column-name"
                select="$config.src/*/ref-attributes/ref-attribute[@name=$name]/@db-column-name"/>
  <xsl:choose>
    <xsl:when test="$db-column-name">
      <xsl:value-of select="$db-column-name"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$name"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="config.ref-attribute.sql-datatype">
  <xsl:param name="name" select="@name"/>
  <xsl:value-of select="$config.src/*/ref-attributes/ref-attribute[@name=$name]/@sql-datatype"/>
</xsl:template>

<xsl:template name="config.ref-attribute.java-type">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="java-type"
                select="$config.src/*/ref-attributes/ref-attribute[@name=$name]/@java-type"/>
  <xsl:choose>
    <xsl:when test="$java-type and $java-type!=''">
      <xsl:value-of select="$java-type"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.java-type-for-sql-type">
        <xsl:with-param name="sql-type">
          <xsl:call-template name="config.ref-attribute.sql-datatype">
            <xsl:with-param name="name" select="$name"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="config.ref-attribute.code">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="position">
    <xsl:for-each select="$config.src/*/ref-attributes/ref-attribute">
      <xsl:if test="@name=$name">
        <xsl:value-of select="position()"/>
      </xsl:if>      
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of
    select="count($config.src/*/ref-attributes/ref-attribute[position()&lt;$position])"/>
</xsl:template>

<xsl:template name="config.ref-attribute.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<xsl:template name="config.ref-attribute.code-max">
  <xsl:value-of select="count($config.src/*/ref-attributes/ref-attribute) - 1"/>
</xsl:template>

<!--
   Returns the name of the Java constant wrapping the numerical code of the reference
   attribute with name $name. $name can be passed as a parameter; default is the "name"
   attribute of the current node. Works as follows: Looks up the "ref-attribute" element
   whose "name" attribute equals $name. If that element has a "constant" attribute, that is
   returned; otherwise, $name turned to upper case is returned. 
-->

<xsl:template name="config.ref-attribute.constant">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="node" select="$config.src/*/ref-attributes/ref-attribute[@name=$name]"/>
  <xsl:if test="not($node)">
    <xsl:call-template name="config.error">
      <xsl:with-param name="message"
        select="concat('Cannot find reference attribute for name: ', $name)"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$node/@constant">
      <xsl:value-of select="$node/@constant"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.upcase-and-dashes-to-underscores">
        <xsl:with-param name="string" select="$name"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning worksheet states
   ==========================================================================================
-->

<!--
   Returns the smallest number used as a numerical code for a worksheet state. Simply returns
   0. 
-->

<xsl:template name="config.worksheet-state.code-min">
  <xsl:value-of select="0"/>
</xsl:template>

<!--
   Returns the largest number used as a numerical code for a worksheet state. Computes the
   number of "worksheet-state" elements, where counting starts with 0, and returnes that.
-->

<xsl:template name="config.worksheet-state.code-max">
  <xsl:value-of select="count($config.src/*/worksheet-states/worksheet-state) - 1"/>
</xsl:template>

<!--
   ==========================================================================================
   Templates to compose urls
   ==========================================================================================
-->

<!--
   Returns the relative URL of a document. Expects the following parameters:

    context
         The context of the URL. Optional. Defaults to "view"

    type
         The type of the document; specified as string name.

    id    The database id of the document. Either this or qualified-name must
          be given.

    qualified-name
          The qualified name of the document. Either this od id must be given.

    mode  DEPRECATED. Same as context.

   No checks wether $type is a valid document type are made. The URL returned is

     protected/document/$mode/type-name/$type/id/$id

   if $id is given and

     protected/document/$mode/type-name/$type/qname/$qualified-name

   if qualified-name is given. If both are given, $id takes precedence.
-->

<xsl:template name="config.url.document.unchecked">
  <xsl:param name="type"/>
  <xsl:param name="id"/>
  <xsl:param name="qualified-name"/>
  <xsl:param name="context" select="'view'"/>
  <xsl:param name="mode"/>
  <!-- Part 1: "protected" -->
  <xsl:text>protected/</xsl:text>
  <!-- Part 2: "$context" -->
  <xsl:choose>
    <xsl:when test="$mode">
      <xsl:value-of select="$mode"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$context"/>
    </xsl:otherwise>
  </xsl:choose>
  <!-- Part 3 and 4: "document/type-name" -->
  <xsl:text>/document/type-name/</xsl:text>
  <!-- Part 5: "$type" -->
  <xsl:value-of select="$type"/>
  <xsl:choose>
    <xsl:when test="$id">
      <!-- Part 6 and 7: "id/$id" -->
      <xsl:text>/id/</xsl:text>
      <xsl:value-of select="$id"/>
    </xsl:when>
    <xsl:when test="$qualified-name">
      <!-- Part 6 and 7: "qname/$qualified-name" -->
      <xsl:text>qname/</xsl:text>
      <xsl:value-of select="$qualified-name"/>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template name="config.url.document.unchecked.OLD">
  <xsl:param name="type"/>
  <xsl:param name="id"/>
  <xsl:param name="qualified-name"/>
  <xsl:param name="context" select="'view'"/>
  <xsl:param name="mode"/>
  <xsl:text>protected/document/</xsl:text>
  <xsl:choose>
    <xsl:when test="$mode">
      <xsl:value-of select="$mode"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$context"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>/type-name/</xsl:text>
  <xsl:value-of select="$type"/>
  <xsl:choose>
    <xsl:when test="$id">
      <xsl:text>/id/</xsl:text>
      <xsl:value-of select="$id"/>
    </xsl:when>
    <xsl:when test="$qualified-name">
      <xsl:text>/qname/</xsl:text>
      <xsl:value-of select="$qualified-name"/>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates to compose java  names
   ==========================================================================================
-->

<xsl:template name="config.java-name">
  <xsl:param name="string"/>
  <xsl:param name="upcase-first-char" select="'no'"/>

  <xsl:for-each select="x-xalan:tokenize($string,'_-')">
    <xsl:variable name="token" select="."/>
    <xsl:choose>
      <xsl:when test="position()&gt;1 or $upcase-first-char='yes'">
        <xsl:call-template name="config.upcase-first-char">
          <xsl:with-param name="string" select="$token"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$token"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template name="config.java-class-name.document">
  <xsl:param name="doctype" select="@name"/>
  <xsl:for-each select="x-xalan:tokenize($doctype,'_')">
    <xsl:variable name="token" select="."/>
    <!-- TODO: Use template config.upcase-first-char -->
    <xsl:call-template name="config.upcase">
      <xsl:with-param name="string" select="substring($token,1,1)"/>
    </xsl:call-template>
    <xsl:value-of select="substring($token,2)"/>
  </xsl:for-each>
  <xsl:value-of select="'Document'"/>
</xsl:template>

<xsl:template name="config.java-class-name.generic-document">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="'Generic'"/>
  <xsl:call-template name="config.java-class-name.document">
    <xsl:with-param name="doctype" select="$doctype"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="config.java-class-name.document-to-checkin">
  <xsl:param name="doctype" select="@name"/>
  <xsl:call-template name="config.java-name">
    <xsl:with-param name="string" select="$doctype"/>
    <xsl:with-param name="upcase-first-char">yes</xsl:with-param>
  </xsl:call-template>
  <xsl:text>ToCheckin</xsl:text>
</xsl:template>

<!--
<xsl:template name="config.java-class-name.checked-in-document">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="'CheckedIn'"/>
  <xsl:for-each select="x-xalan:tokenize($doctype,'_')">
    <xsl:variable name="token" select="."/>
    <xsl:call-template name="config.upcase">
      <xsl:with-param name="string" select="substring($token,1,1)"/>
    </xsl:call-template>
    <xsl:value-of select="substring($token,2)"/>
  </xsl:for-each>
</xsl:template>
-->

<xsl:template name="config.java-class-name.pseudodoc">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="java-class"
                select="/*/pseudodoc-types/pseudodoc-type[@name=$name]/@java-class"/>
  <xsl:choose>
    <xsl:when test="$java-class">
      <xsl:value-of select="$java-class"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>Default</xsl:text>
      <xsl:for-each select="x-xalan:tokenize($name,'_')">
        <xsl:call-template name="config.upcase-first-char">
          <xsl:with-param name="string" select="."/>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="config.java-class-name.pseudodoc-to-checkin">
  <xsl:param name="type" select="@name"/>
  <xsl:call-template name="config.java-name">
    <xsl:with-param name="string" select="$type"/>
    <xsl:with-param name="upcase-first-char">yes</xsl:with-param>
  </xsl:call-template>
  <xsl:text>ToCheckin</xsl:text>
</xsl:template>

<xsl:template name="config.java-class-name.sync-command">
  <xsl:param name="name" select="@name"/>
  <xsl:for-each select="x-xalan:tokenize($name,'-')">
    <xsl:call-template name="config.upcase-first-char">
      <xsl:with-param name="string" select="."/>
    </xsl:call-template>
  </xsl:for-each>
  <xsl:text>SyncCommand</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Templates for SQL
   ==========================================================================================
-->

<!--
  Returns a suitable Java type for a given SQL data type.
-->

<xsl:template name="config.java-type-for-sql-type">
  <xsl:param name="sql-type"/>
  <xsl:choose>
    <xsl:when test="$sql-type='text'">java.lang.String</xsl:when>
    <xsl:when test="starts-with($sql-type,'character varying(')">java.lang.String</xsl:when>
    <xsl:when test="starts-with($sql-type,'varchar(')">java.lang.String</xsl:when>
    <xsl:when test="starts-with($sql-type,'character(')">java.lang.String</xsl:when>
    <xsl:when test="starts-with($sql-type,'char(')">java.lang.String</xsl:when>
    <xsl:when test="$sql-type='integer'">java.lang.Integer</xsl:when>
    <xsl:when test="$sql-type='int'">java.lang.Integer</xsl:when>
    <xsl:when test="$sql-type='int4'">java.lang.Integer</xsl:when>
    <xsl:when test="$sql-type='smallint'">java.lang.Integer</xsl:when>
    <xsl:when test="$sql-type='int2'">java.lang.Integer</xsl:when>
    <xsl:when test="$sql-type='bigint'">java.lang.Long</xsl:when>
    <xsl:when test="$sql-type='int8'">java.lang.Long</xsl:when>
    <xsl:when test="$sql-type='real'">java.lang.Real</xsl:when>
    <xsl:when test="$sql-type='float4'">java.lang.Real</xsl:when>
    <xsl:when test="$sql-type='double precision'">java.lang.Double</xsl:when>
    <xsl:when test="$sql-type='float8'">java.lang.Double</xsl:when>
    <xsl:when test="$sql-type='boolean'">java.lang.Boolean</xsl:when>
    <xsl:when test="$sql-type='bool'">java.lang.Boolean</xsl:when>
    <xsl:when test="$sql-type='timestamp'">java.sql.Timestamp</xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.error">
        <xsl:with-param name="message">
          <xsl:text>config.java-type-for-sql-type: Unknown SQL type: </xsl:text>
          <xsl:value-of select="$sql-type"/>
        </xsl:with-param>
      </xsl:call-template>      
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   Templates concerning the check-in of database content
   ==========================================================================================
-->

<xsl:template name="config.default-checkin-content-filename">
  <xsl:param name="master-filename"/>
  <xsl:variable name="core-name-length"
    select="string-length($master-filename)
            - string-length($config.checkin-master-filename-suffix)"/>
  <xsl:value-of select="concat(substring($master-filename,1,$core-name-length),
                               $config.checkin-content-filename-suffix)"/>
</xsl:template>

</xsl:stylesheet>
