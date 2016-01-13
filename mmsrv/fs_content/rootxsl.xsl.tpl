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
   <!ENTITY xmlns-mumie    "http://www.mumie.net/xml-namespace/document/metainfo">
  ]
>

<!--
   Author:  Tilman Rassy

   $Id: rootxsl.xsl.tpl,v 1.1 2009/01/01 21:35:11 rassy Exp $
-->

<xsl:stylesheet version="1.0"
                xmlns:mumie="http://www.mumie.net/xml-namespace/document/metainfo"
                xmlns:rootxsl="http://www.mumie.net/xml-namespace/rootxsl"
                xmlns:xsl-ext="&xmlns-xsl-ext;"
                xmlns:xsl-lib="http://www.mumie.net/xml-namespace/xsl-lib"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="mumie rootxsl xsl-lib xsl-ext">

<xsl:output method="xml" encoding="ASCII"/>

<xsl:strip-space elements="xsl:attribute"/>

<!-- ================================================================================ -->
<!-- h1: General parameters and variables                                             -->
<!-- ================================================================================ -->

<!--
   The source of the resources. This should be the filename of an XML document or "''"
   to indicate that this document is the source of the resources. Defaults to  "''".
-->

<xsl:param name="xsl.resources.source" select="''"/>

<!--
   Root node from where to look for resources. Defaults to the root node of
   $xsl.resources.source.
-->

<xsl:param name="xsl.resources" select="document($xsl.resources.source)"/>

<!-- ================================================================================ -->
<!-- h1: Japs XSL library parameters                                                  -->
<!-- ================================================================================ -->

<!--
   Prefix of Japs URLs.
-->

<xsl:param name="xsl.url-prefix" xsl-lib:included="yes">@url-prefix@</xsl:param>

<!--
   The URL prefix for internal Cocoon processing.
-->

<xsl:param name="xsl.url-prefix.cocoon-internal" xsl-lib:included="yes">cocoon:</xsl:param>

<!--
  Indicates that online mode is enabled (not preview mode)
-->

<xsl:param name="xsl.mode" xsl-lib:included="yes">online</xsl:param>

<!-- ================================================================================ -->
<!-- h1: Japs XSL library templates                                                   -->
<!-- ================================================================================ -->

<!--
   Returns the default url of a document. Recognizes the following parameters:

     context
           The context. Default is the "context" attribute of the current node if
           that attribute exists, and 'view' if it does not exist.

     document-type.name
           The string name of the document type. Defaults to the local name of
           the current node.

     id    The database id of the document. Defaults to the "id" attribut of the
           current node.

     internal
           If "yes", the URL is prepended by the "cocoon:" pseudo-protocol instead of the
           server prefix $xsl.url-prefix. Default is the value of the 'internal' attribute
           of the current node, or "no" if no such attribute exists.
-->

<xsl:template name="xsl.document-url" xsl-lib:included="yes">

  <!-- 'id' parameter: -->
  <xsl:param name="id" select="@id"/>

  <!-- 'qualified-name' parameter: -->
  <xsl:param name="qualified-name" select="@qualified-name"/>

  <!-- 'document-type.name' parameter: -->
  <xsl:param name="document-type.name" select="local-name()"/>

  <!-- 'context' parameter: -->
  <xsl:param name="context">
    <xsl:call-template name="xsl.param-value.context"/>
  </xsl:param>

  <!-- 'internal' parameter: -->
  <xsl:param name="internal">
    <xsl:call-template name="xsl.param-value.internal"/>
  </xsl:param>

  <!-- URL prefix: -->
  <xsl:variable name="prefix">
    <xsl:choose>
      <xsl:when test="$internal='yes'">
        <xsl:value-of select="$xsl.url-prefix.cocoon-internal"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$xsl.url-prefix"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!-- Compose URL: -->
  <xsl:value-of select="$prefix"/>
  <xsl:value-of select="'/protected/'"/>
  <xsl:value-of select="$context"/>
  <xsl:value-of select="'/document/type-name/'"/>
  <xsl:value-of select="$document-type.name"/>
  <xsl:value-of select="'/id/'"/>
  <xsl:value-of select="$id"/>

  <!-- Workaround for Java 6 Bug: -->
  <xsl:if test="$document-type.name = 'applet' or $document-type.name = 'jar'">
    <xsl:value-of select="'.jar'"/>
  </xsl:if>

</xsl:template>

<!--
  Returns the url of a pseudo-document.

     context
           The context. Default is the "context" attribute of the current node if
           that attribute exists, and 'info' if it does not exist.

     type-name
           The string name of the pseudo-document type. Defaults to the local name of
           the current node.

     id    The database id of the pseud0-document. Either this or qualified-name must
           be given. Defaults to the "id" attribut of the current node.
-->

<xsl:template name="xsl.pseudodoc-url" xsl-lib:included="yes">

  <!-- 'context' parameter: -->
  <xsl:param name="context">
    <xsl:call-template name="xsl.param-value.context">
      <xsl:with-param name="default">info</xsl:with-param>
    </xsl:call-template>
  </xsl:param>

  <!-- 'type-name' parameter: -->
  <xsl:param name="type-name" select="local-name()"/>
  
  <!-- 'id' parameter:  -->
  <xsl:param name="id" select="@id"/>
  
  <!-- Compose URL: -->
  <xsl:value-of select="$xsl.url-prefix"/>
  <xsl:value-of select="'/protected/'"/>
  <xsl:value-of select="$context"/>
  <xsl:value-of select="'/pseudo-document/type-name/'"/>
  <xsl:value-of select="$type-name"/>
  <xsl:value-of select="'/id/'"/>
  <xsl:value-of select="$id"/>

</xsl:template>

<!--
  Returns the url of a document index.

     context
           The context. Default is the "context" attribute of the current node if
           that attribute exists, and 'view' if it does not exist.

     type-name
           The string name of the document type. Defaults to the local name of
           the current node.
-->

<xsl:template name="xsl.document-index-url" xsl-lib:included="yes">

  <!-- 'context' parameter: -->
  <xsl:param name="context">
    <xsl:call-template name="xsl.param-value.context"/>
  </xsl:param>

  <!-- 'type-name' parameter: -->
  <xsl:param name="type-name" select="local-name()"/>
  
  <!-- Compose URL: -->
  <xsl:value-of select="$xsl.url-prefix"/>
  <xsl:value-of select="'/protected/'"/>
  <xsl:value-of select="$context"/>
  <xsl:value-of select="'/document-index/type-name/'"/>
  <xsl:value-of select="$type-name"/>

</xsl:template>

<!--
  Returns the url of a pseudo-document index.

     context
           The context. Default is the "context" attribute of the current node if
           that attribute exists, and 'view' if it does not exist.

     type-name
           The string name of the pseudo-document type. Defaults to the local name of
           the current node.
-->

<xsl:template name="xsl.pseudodoc-index-url" xsl-lib:included="yes">

  <!-- 'context' parameter: -->
  <xsl:param name="context">
    <xsl:call-template name="xsl.param-value.context"/>
  </xsl:param>

  <!-- 'type-name' parameter: -->
  <xsl:param name="type-name" select="local-name()"/>
  
  <!-- Compose URL: -->
  <xsl:value-of select="$xsl.url-prefix"/>
  <xsl:value-of select="'/protected/'"/>
  <xsl:value-of select="$context"/>
  <xsl:value-of select="'/pseudo-document-index/type-name/'"/>
  <xsl:value-of select="$type-name"/>

</xsl:template>

<!--
   Prints an error message and terminates the transformation.
-->

<xsl:template name="xsl.error" xsl-lib:included="yes">
  <xsl:param name="message"/>
  <xsl:message terminate="yes">
    <xsl:text>&br;&sp;****************************************************************</xsl:text>
    <xsl:text>&br;&sp;&sp;Error:&br;&sp;&sp;</xsl:text>
    <xsl:value-of select="$message"/>
    <xsl:text>&br;&sp;****************************************************************</xsl:text>
      <xsl:text>&br;&br;</xsl:text>
  </xsl:message>
</xsl:template>

<!--
  Converts a boolean value to the string "true" or "false"
-->

<xsl:template name="xsl.boolean-to-true-or-false" xsl-lib:included="yes">
  <xsl:param name="boolean"/>
  <xsl:choose>
    <xsl:when test="$boolean='yes'">true</xsl:when>
    <xsl:when test="$boolean='true'">true</xsl:when>
    <xsl:when test="$boolean='on'">true</xsl:when>
    <xsl:when test="$boolean='1'">true</xsl:when>
    <xsl:when test="$boolean='no'">false</xsl:when>
    <xsl:when test="$boolean='false'">false</xsl:when>
    <xsl:when test="$boolean='off'">false</xsl:when>
    <xsl:when test="$boolean='0'">false</xsl:when>
    <xsl:when test="not($boolean) or $boolean=''">false</xsl:when>
  </xsl:choose>
</xsl:template>

<!--
  Converts a boolean value to the string "yes" or "no"
-->

<xsl:template name="xsl.boolean-to-yes-or-no" xsl-lib:included="yes">
  <xsl:param name="boolean"/>
  <xsl:choose>
    <xsl:when test="$boolean='yes'">yes</xsl:when>
    <xsl:when test="$boolean='true'">yes</xsl:when>
    <xsl:when test="$boolean='on'">yes</xsl:when>
    <xsl:when test="$boolean='1'">yes</xsl:when>
    <xsl:when test="$boolean='no'">no</xsl:when>
    <xsl:when test="$boolean='false'">no</xsl:when>
    <xsl:when test="$boolean='off'">no</xsl:when>
    <xsl:when test="$boolean='0'">no</xsl:when>
    <xsl:when test="not($boolean) or $boolean=''">no</xsl:when>
  </xsl:choose>
</xsl:template>

<!--
  Returns the value of a parameter in the dynamic data section.
-->

<xsl:template name="xsl.get-param-from-dynamic-data" xsl-lib:included="yes">
  <xsl:param name="name" select="@name"/>
  <xsl:param name="default"/>
  <xsl:variable name="value">
    <xsl:for-each select="/*/mumie:dynamic_data/mumie:param[@name=$name]">
      <xsl:if test="position()=last()">
        <xsl:value-of select="@value"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="$value!=''">
      <xsl:value-of select="$value"/>
    </xsl:when>
    <xsl:when test="$default">
      <xsl:value-of select="$default"/>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!--
   Checks if a parameter with a certain prefix exists in the dynamic data section.
   Returns "yes" if the parameter was found, "no" otherwise. Expects one template
   parameter:

     prefix   The prefix of the dynamic data parameter. A dot is added
              automatically
-->

<xsl:template name="xsl.check-dynamic-data-param" xsl-lib:included="yes">
  <xsl:param name="prefix"/>
  <xsl:variable name="value"
                select="/*/mumie:dynamic_data/mumie:param[starts-with(@name,concat($prefix,'.'))]/@value"/>
  <xsl:choose>
    <xsl:when test="$value and $value!=''">yes</xsl:when>
    <xsl:otherwise>no</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Resolves a local id and returns an URL for a document. Recognizes the following parameters:

     lid   The local id of the document. Default is @lid, i.e., the 'lid' attribute of the
           current node.

     context
           The context. Default is the "context" attribute of the current node if
           that attribute exists, and 'view' if it does not exist.

     internal
           If "yes", the URL is prepended by the "cocoon:" pseudo-protocol instead of the
           server prefix $xsl.url-prefix. Default is the value of the 'internal' attribute
           of the current node, or "no" if no such attribute exists.

     parameters
           Request parameters, as a string of key-value pairs in URL syntax,
           i.e., '?KEY1=VALUE1&KEY2=VALUE2...'.

   Example:

     <components>
       ...
       <page lid="Help start page" id="56"/>
       ...
     </components>

     <content>
       ...
       <link lid="Help start page">Help</link>
       ...
     </content>

   If you call the template with the <link/> element as the current node, it would return
   "$prefix/protected/document/page/id/56" (for $prefix, see tmeplate "xsl.document-url").
-->

<xsl:template name="xsl.resolve-lid" xsl-lib:included="yes">

  <!-- 'lid' parameter: -->
  <xsl:param name="lid">
    <xsl:call-template name="xsl.param-value.lid"/>
  </xsl:param>

  <!-- 'context' parameter: -->
  <xsl:param name="context">
    <xsl:call-template name="xsl.param-value.context"/>
  </xsl:param>

  <!-- 'internal' parameter: -->
  <xsl:param name="internal">
    <xsl:call-template name="xsl.param-value.internal"/>
  </xsl:param>

  <!-- 'parameters' parameter: -->
  <xsl:param name="parameters"/>

  <!-- Metainfo nodes to search for the given lid: -->
  <xsl:variable name="refs" select="/*/mumie:components/*|/*/mumie:links/*"/>

  <!-- Metainfo node for the given lid: -->
  <xsl:variable name="data" select="$refs[@lid=$lid]"/>

  <!-- Get the id: -->
  <xsl:variable name="id">
    <xsl:choose>
      <xsl:when test="$data/@id">
        <xsl:value-of select="$data/@id"/>
      </xsl:when>
      <xsl:when test="$data/@param">
        <xsl:call-template name="xsl.get-param-from-dynamic-data">
          <xsl:with-param name="name" select="$data/@param"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="xsl.error">
          <xsl:with-param name="message" select="concat('Cannot resolve local id: ',$lid)"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!-- Compose URL: -->
  <xsl:call-template name="xsl.document-url">
    <xsl:with-param name="id" select="$data/@id"/>
    <xsl:with-param name="document-type.name" select="local-name($data)"/>
    <xsl:with-param name="context" select="$context"/>
    <xsl:with-param name="internal" select="$internal"/>
  </xsl:call-template>
  <xsl:value-of select="$parameters"/>

</xsl:template>

<!--
  Returns the DB id for a given local id. Expects one parameter:

    lid  The local id. Default is @lid, i.e., the 'lid' attribute of the
         current node.
-->

<xsl:template name="xsl.get-id" xsl-lib:included="yes">
  <xsl:param name="lid">
    <xsl:call-template name="xsl.param-value.lid"/>
  </xsl:param>
  <xsl:variable name="refs" select="/*/mumie:components/*|/*/mumie:links/*"/>
  <xsl:value-of select="$refs[@lid=$lid]/@id"/>
</xsl:template>

<!--
   Auxiliary template to set the value of the 'lid' parameter. Used in templates
   'xsl.resolve-lid' and 'xsl.get-id'.
-->

<xsl:template name="xsl.param-value.lid" xsl-lib:included="yes">
  <xsl:value-of select="@lid"/>
</xsl:template>

<!--
   Auxiliary template to set the value of the 'context' parameter. Used in template
   'xsl.resolve-lid'.
-->

<xsl:template name="xsl.param-value.context" xsl-lib:included="yes">
  <xsl:param name="default">view</xsl:param>
  <xsl:choose>
    <xsl:when test="@context">
      <xsl:value-of select="@context"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$default"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Auxiliary template to set the value of the 'internal' parameter. Used in template
   'xsl.resolve-lid'.
-->

<xsl:template name="xsl.param-value.internal" xsl-lib:included="yes">
  <xsl:choose>
    <xsl:when test="@internal">
      <xsl:value-of select="@internal"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'no'"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Sets the attributes of an "object" element inserting a Java applet. Recognizes
   four parameters:

     lid     The lid if the applet. Defaults to '@lid', i.e., the 'lid'
             attribute of the current node.

     applet  The 'mumie:applet' metainfo element of the applet. Defaults to
             '/*/mumie:components/*[@lid=$lid]'.

     width   The width of the applet. Defaults to '@width', i.e., the 'width'
             attribute of the current node. If the current node has no such
             attribute the width is taken from '$applet/mumie:width/@value'.

     height  The height of the applet. Defaults to '@height', i.e., the 'height'
             attribute of the current node. If the current node has no such
             attribute the height is taken from '$applet/mumie:height/@value'.

   The template sets the 'type', 'classid', 'archive', 'width', and 'height'
   attributes of the 'object' element. The latter must be created outside this
   template.
-->

<xsl:template name="xsl.set-applet-object-src-attributes" xsl-lib:included="yes">

  <!-- Parameters: -->
  <xsl:param name="lid" select="@lid"/>
  <xsl:param name="applet" select="/*/mumie:components/*[@lid=$lid]"/>
  <xsl:param name="width" select="@width"/>
  <xsl:param name="height" select="@height"/>

  <!-- Attribute 'type': -->
  <xsl:attribute name="type">application/x-java-applet</xsl:attribute>

  <!-- Attribute 'classid': -->
  <xsl:attribute name="classid">
    <xsl:value-of select="concat('java:',$applet/mumie:qualified_name)"/>
  </xsl:attribute>

  <!-- Attribute 'archive': -->
  <xsl:attribute name="archive">
    <!-- First item: the applet itself: -->
    <xsl:call-template name="xsl.document-url">
      <xsl:with-param name="id" select="$applet/@id"/>
      <xsl:with-param name="document-type.name" select="local-name($applet)"/>
    </xsl:call-template>
    <!-- Remaining items: needed jars and other applets: -->
    <xsl:for-each select="$applet//
                            mumie:components/*[local-name()='jar' or local-name()='applet']">
      <xsl:value-of select="', '"/>
      <xsl:call-template name="xsl.document-url"/>
    </xsl:for-each>
  </xsl:attribute>

  <!-- Attribute 'width': -->
  <xsl:attribute name="width">
    <xsl:choose>
      <xsl:when test="$width">
        <xsl:value-of select="$width"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$applet/mumie:width/@value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>

  <!-- Attribute 'height': -->
  <xsl:attribute name="height">
    <xsl:choose>
      <xsl:when test="$height">
        <xsl:value-of select="$height"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$applet/mumie:height/@value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>

</xsl:template>

<!--
   Sets the attributes of an "applet" element. Recognizes four parameters:

     lid     The lid if the applet. Defaults to '@lid', i.e., the 'lid'
             attribute of the current node.

     applet  The 'mumie:applet' metainfo element of the applet. Defaults to
             '/*/mumie:components/*[@lid=$lid]'.

     width   The width of the applet. Defaults to '@width', i.e., the 'width'
             attribute of the current node. If the current node has no such
             attribute the width is taken from '$applet/mumie:width/@value'.

     height  The height of the applet. Defaults to '@height', i.e., the 'height'
             attribute of the current node. If the current node has no such
             attribute the height is taken from '$applet/mumie:height/@value'.

   The template sets the 'code', 'codebase', 'archive', 'width', and 'height'
   attributes of the 'applet' element. The latter must be created outside this
   template.
-->

<xsl:template name="xsl.set-applet-src-attributes" xsl-lib:included="yes">

  <!-- Parameters: -->
  <xsl:param name="lid" select="@lid"/>
  <xsl:param name="applet" select="/*/mumie:components/*[@lid=$lid]"/>
  <xsl:param name="width" select="@width"/>
  <xsl:param name="height" select="@height"/>

  <!-- Attribute 'code': -->
  <xsl:attribute name="code">
    <xsl:value-of select="$applet/mumie:qualified_name"/>
  </xsl:attribute>

  <!-- Attribute 'codebase': -->
  <xsl:attribute name="codebase">
    <xsl:value-of select="''"/>
  </xsl:attribute>

  <!-- Attribute 'archive': -->
  <xsl:attribute name="archive">
    <!-- First item: the applet itself: -->
    <xsl:call-template name="xsl.document-url">
      <xsl:with-param name="id" select="$applet/@id"/>
      <xsl:with-param name="document-type.name" select="local-name($applet)"/>
    </xsl:call-template>
    <!-- Remaining items: needed jars and other applets: -->
    <xsl:for-each select="$applet//
                            mumie:components/*[local-name()='jar' or local-name()='applet']">
      <xsl:value-of select="', '"/>
      <xsl:call-template name="xsl.document-url"/>
    </xsl:for-each>
  </xsl:attribute>

  <!-- Attribute 'width': -->
  <xsl:attribute name="width">
    <xsl:choose>
      <xsl:when test="$width">
        <xsl:value-of select="$width"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$applet/mumie:width/@value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>

  <!-- Attribute 'height': -->
  <xsl:attribute name="height">
    <xsl:choose>
      <xsl:when test="$height">
        <xsl:value-of select="$height"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$applet/mumie:height/@value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:attribute>

</xsl:template>

<!--
   Creates the attributes of the "object" element to include a Flash movie.
   Recognizes the following parameters:

     lid   The local id of the Flash movie

     data  The URL of the Flash movie. The default is obtained by
           resolving $lid.
 
   This template is slightly different from xsl.set-applet-object-src-attributes,
   the corresponding template for applets, in the respect that you can pass the
   URL of the Flash movie in addition of the lid. This is because the URL is needed
   twice: for the "data" attribute and the "movie" parameter; so it might be better
   to compose it once outside this template, save it in a variable, and use the saved
   value for both the attribute and the parameter. The variable should be declared in
   the way

     <xsl:variable name="data">
       <xsl:call-template name="xsl.resolve-lid">
         <xsl:with-param name="lid" select="@lid"/>
       </xsl:call-template>
     </xsl:variable>

   where @lid is the local id of the Flash movie.
-->

<xsl:template name="xsl.set-flash-object-src-attributes" xsl-lib:included="yes">
  <xsl:param name="lid" select="@lid"/>
  <xsl:param name="data">
    <xsl:call-template name="xsl.resolve-lid">
      <xsl:with-param name="lid" select="@lid"/>
    </xsl:call-template>
  </xsl:param>

  <!-- Attribute "type": -->
  <xsl:attribute name="type">application/x-shockwave-flash</xsl:attribute>

  <!-- Attribute "data": -->
  <xsl:attribute name="data">
    <xsl:value-of select="$data"/>
  </xsl:attribute>

  <!-- Attribute "width": -->
  <xsl:attribute name="width">
    <xsl:value-of select="/*/mumie:components/*[@lid=$lid]/mumie:width/@value"/>
  </xsl:attribute>

  <!-- Attribute "height": -->
  <xsl:attribute name="height">
    <xsl:value-of select="/*/mumie:components/*[@lid=$lid]/mumie:height/@value"/>
  </xsl:attribute>
  
</xsl:template>

<!--
   Sets the attributes of an "img" element.
-->

<xsl:template name="xsl.set-image-src-attributes" xsl-lib:included="yes">
  <xsl:param name="request-param-element-name">request-param</xsl:param>
  <!-- src attribute: -->
  <xsl:attribute name="src">
    <xsl:call-template name="xsl.resolve-lid"/>
    <xsl:for-each select="*[local-name=$request-param-element-name]">
      <xsl:call-template name="xsl.set-request-param"/>
    </xsl:for-each>        
  </xsl:attribute>
  <!-- width and height values: -->
  <xsl:variable name="width"
                select="/*/mumie:components/*[@lid=current()/@lid]/mumie:width/@value"/>
  <xsl:variable name="height"
                select="/*/mumie:components/*[@lid=current()/@lid]/mumie:height/@value"/>
  <xsl:if test="$width">
    <!-- width attribute: -->
    <xsl:attribute name="width">
      <xsl:value-of select="$width"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$height">
    <!-- height attribute: -->
    <xsl:attribute name="height">
      <xsl:value-of select="$height"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!--
   Sets a hyper reference attribute.
-->

<xsl:template name="xsl.set-ref-attribute" xsl-lib:included="yes">

  <xsl:param name="name">href</xsl:param>
  <xsl:param name="default"/>
  <xsl:param name="request-param-element-name">request-param</xsl:param>
  <xsl:param name="params-prefix-attrib-allowed">no</xsl:param>

  <!-- Decide if to use parameters: -->
  <xsl:variable name="use-params">
    <xsl:choose>
      <xsl:when test="$params-prefix-attrib-allowed='yes' and @params-prefix">
        <xsl:call-template name="xsl.check-dynamic-data-param">
          <xsl:with-param name="prefix" select="@params-prefix"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!-- Generate the ref attribute: -->
  <xsl:attribute name="{$name}">
    <!-- URL: -->
    <xsl:choose>
      <xsl:when test="$use-params='yes'">
        <xsl:call-template name="xsl.url-from-params"/>
      </xsl:when>
      <xsl:when test="@lid">
        <xsl:call-template name="xsl.resolve-lid"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$default"/>
      </xsl:otherwise>
    </xsl:choose>
    <!-- Request parameters: -->
    <xsl:for-each select="*[local-name=$request-param-element-name]">
      <xsl:call-template name="xsl.set-request-param"/>
    </xsl:for-each>        
  </xsl:attribute>

</xsl:template>

<!--
   Returns a string that sets a request parameter in the url.
-->

<xsl:template name="xsl.set-request-param" xsl-lib:included="yes">
  <xsl:param name="name" select="@name"/>
  <xsl:param name="value">
    <xsl:choose>
      <xsl:when test="@value">
        <xsl:value-of select="@value"/>
      </xsl:when>
      <xsl:when test="@value-of-param">
        <xsl:call-template name="xsl.get-param-from-dynamic-data">
          <xsl:with-param name="name" select="@value-of-param"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="@value-of-lid">
        <xsl:call-template name="xsl.resolve-lid">
          <xsl:with-param name="lid" select="@value-of-param"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:param>
  <xsl:param name="separator">
    <xsl:choose>
      <xsl:when test="position()=1">
        <xsl:value-of select="'?'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'&amp;'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>
  <xsl:value-of select="$separator"/>
  <xsl:value-of select="$name"/>
  <xsl:value-of select="'='"/>
  <xsl:value-of select="$value"/>
</xsl:template>

<!--
  Composes a document url from parameters in the dynamic data section. Recocnizes
  one parameter:

    prefix  The prefix of the parameters. Defaults to the value of the
            "params-prefix" attribute of the current node.

-->

<xsl:template name="xsl.url-from-params" xsl-lib:included="yes">
  <xsl:param name="prefix" select="@params-prefix"/>

  <!-- Get request parmaters: -->

  <!-- id: -->
  <xsl:variable name="id">
    <xsl:call-template name="xsl.get-param-from-dynamic-data">
      <xsl:with-param name="name" select="concat($prefix,'.id')"/>
    </xsl:call-template>
  </xsl:variable>

  <!-- document type: -->
  <xsl:variable name="type-name">
    <xsl:call-template name="xsl.get-param-from-dynamic-data">
      <xsl:with-param name="name" select="concat($prefix,'.type-name')"/>
    </xsl:call-template>
  </xsl:variable>

  <!-- context: -->
  <xsl:variable name="context">
    <xsl:call-template name="xsl.get-param-from-dynamic-data">
      <xsl:with-param name="name" select="concat($prefix,'.context')"/>
      <xsl:with-param name="default">view</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>

  <!-- subject: -->
  <xsl:variable name="subject">
    <xsl:call-template name="xsl.get-param-from-dynamic-data">
      <xsl:with-param name="name" select="concat($prefix,'.subject')"/>
      <xsl:with-param name="default">document</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>

  <!-- internal: -->
  <xsl:variable name="internal">
    <xsl:call-template name="xsl.get-param-from-dynamic-data">
      <xsl:with-param name="name" select="concat($prefix,'.internal')"/>
      <xsl:with-param name="default">no</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>

  <!-- params: -->
  <xsl:variable name="params">
    <xsl:call-template name="xsl.get-param-from-dynamic-data">
      <xsl:with-param name="name" select="concat($prefix,'.params')"/>
    </xsl:call-template>
  </xsl:variable>

  <!-- Delegate to template according to subject, or "redirect" to void if id and/or
       type are not specified: -->
  <xsl:choose>

    <!-- If id or type could not be found, return URL of void response: -->
    <xsl:when test="$id='' or $type-name=''">
      <xsl:value-of select="concat($xsl.url-prefix, '/protected/alias/void')"/>
    </xsl:when>

    <!-- Document: -->
    <xsl:when test="$subject='document'">
      <xsl:call-template name="xsl.document-url">
        <xsl:with-param name="context" select="$context"/>
        <xsl:with-param name="document-type.name" select="$type-name"/>
        <xsl:with-param name="id" select="$id"/>
        <xsl:with-param name="internal" select="internal"/>
      </xsl:call-template>
    </xsl:when>

    <!-- Pseudo-document: -->
    <xsl:when test="$subject='pseudo-document'">
      <xsl:call-template name="xsl.pseudodoc-url">
        <xsl:with-param name="context" select="$context"/>
        <xsl:with-param name="type-name" select="$type-name"/>
        <xsl:with-param name="id" select="$id"/>
      </xsl:call-template>
    </xsl:when>

    <!-- Document index: -->
    <xsl:when test="$subject='document-index'">
      <xsl:call-template name="xsl.document-index-url">
        <xsl:with-param name="context" select="$context"/>
        <xsl:with-param name="type-name" select="$type-name"/>
      </xsl:call-template>
    </xsl:when>

    <!-- Pseudo-document index: -->
    <xsl:when test="$subject='pseudo-document-index'">
      <xsl:call-template name="xsl.pseudodoc-index-url">
        <xsl:with-param name="context" select="$context"/>
        <xsl:with-param name="type-name" select="$type-name"/>
      </xsl:call-template>
    </xsl:when>

  </xsl:choose>

  <!-- Append request parameters if necessary -->
  <xsl:if test="$params!=''">
    <xsl:if test="not(starts-with($params, '?'))">
      <xsl:text>?</xsl:text>
    </xsl:if>
    <xsl:value-of select="$params"/>
  </xsl:if>

</xsl:template>

<!--
   Returns the URL of the current source document. Recognizes the following parameters:

     context
           The context. Default is the "context" attribute of the current node if
           that attribute exists, and 'view' if it does not exist.

     internal
           If "yes", the URL is prepended by the "cocoon:" pseudo-protocol instead of the
           server prefix $xsl.url-prefix. Default is the value of the 'internal' attribute
           of the current node, or "no" if no such attribute exists.
-->

<xsl:template name="xsl.url-of-self" xsl-lib:included="yes">

  <!-- 'context' parameter: -->
  <xsl:param name="context">
    <xsl:call-template name="xsl.param-value.context"/>
  </xsl:param>

  <!-- 'internal' parameter: -->
  <xsl:param name="internal">
    <xsl:call-template name="xsl.param-value.internal"/>
  </xsl:param>

  <!-- Compose URL: -->
  <xsl:call-template name="xsl.document-url">
    <xsl:with-param name="id" select="/*/@id"/>
    <xsl:with-param name="document-type.name" select="local-name(/*)"/>
    <xsl:with-param name="context" select="$context"/>
    <xsl:with-param name="internal" select="$internal"/>
  </xsl:call-template>
  
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Templates to process a stylesheet                                            -->
<!-- ================================================================================ -->

<!--
  Inserts a Japs URL. Prototypes:

    <xsl-ext:url lid="$LID"/>

    <xsl-ext:url rel-url="$REL_URL"/>
  
  If the first form is used, the inserted URL is that of the document with the local id 
  given by $LID. If the second form is used, the inserted URL is the concatenation of
  $xsl.url-prefix and the relative Japs URL given by $REL_URL. A separating slash is
  inserted if necessary.
-->

<xsl:template match="xsl-ext:url">
  <xsl:choose>
    <xsl:when test="@lid">
      <xsl:call-template name="xsl.resolve-lid"/>
    </xsl:when>
    <xsl:when test="@rel-url">
      <xsl:value-of select="$xsl.url-prefix"/>
      <xsl:if test="not(starts-with(@rel-url, '/'))">/</xsl:if>
      <xsl:value-of select="@rel-url"/>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!--
  Inserts the id of a (pseudo-)document. Prototype:

    <xsl-ext:id lid="$LID"/>
-->

<xsl:template match="xsl-ext:id">
  <xsl:call-template name="xsl.get-id"/>
</xsl:template>

<!--
   Inserts an xsl:include element. Replaces

     <xsl-ext:include lid=$LID/>

   by

     <xsl:include href=$URL/>

   where $URL is the url of the xsl stylesheet referenced by $LID.
-->

<xsl:template match="xsl-ext:include">
  <xsl:element name="xsl:include">
   <xsl:call-template name="xsl.set-ref-attribute"/>
  </xsl:element>
</xsl:template>

<!--
   Inserts an xsl:import element. Replaces

     <xsl-ext:import lid=$LID/>

   by

     <xsl:import href=$URL/>

   where $URL is the url of the xsl stylesheet referenced by $LID.
-->

<xsl:template match="xsl-ext:import">
  <xsl:element name="xsl:import">
   <xsl:call-template name="xsl.set-ref-attribute"/>
  </xsl:element>
</xsl:template>

<!--
   Inserts a 'select' attribute that accesses a secondary source by means of the
   'document()' function. Replaces the attribute

      xsl-ext:select="$PREFIX:$PATH"

    by the attribute

      select="document('$URL')$PATH

    $URL is determined by $PREFIX in the following way:

      - If $PREFIX is "self", then $URL is the URL of the current (i.e.,
        primary) source docment.

      - If $PREFIX is of the form "lid=$LID", then $URL is the URL of the
        document specified by the local id $LID

-->

<xsl:template match="@xsl-ext:select">
  <xsl:variable name="prefix" select="substring-before(.,':')"/>
  <xsl:variable name="path" select="substring-after(.,':')"/>
  <xsl:attribute name="select">
    <xsl:text>document('</xsl:text>
    <xsl:choose>
      <xsl:when test="$prefix='self'">
        <xsl:call-template name="xsl.url-of-self">
          <xsl:with-param name="internal">yes</xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="starts-with($prefix,'lid=')">
        <xsl:call-template name="xsl.resolve-lid">
          <xsl:with-param name="lid" select="substring-after($prefix,'lid=')"/>
          <xsl:with-param name="internal">yes</xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="xsl.error">
          <xsl:with-param name="message" select="concat('Unknown prefix: ',$prefix)"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>')</xsl:text>
    <xsl:value-of select="$path"/>
  </xsl:attribute>

</xsl:template>

<!--
   Template to access the root node of another document by means of the the
   document() function when the document is specified by  a lid. Converts

     <xsl-ext:value-of lid="$LID"/>

   to

     <xsl:value-of select="document($URL)"/>

   where $URL is the URL that emerged from resolving $LID, the local id.

   The xsl-ext:value-of element may also have a 'path' attribute. In that case,

     <xsl-ext:value-of lid="$LID" path="$PATH"/>

   is converted to

     <xsl:value-of select="document($URL)$PATH"/>
-->

<xsl:template match="xsl-ext:value-of">
  <xsl:element name="xsl:value-of">
    <xsl:attribute name="select">
      <xsl:text>document(</xsl:text>
      <xsl:call-template name="xsl.resolve-lid"/>
      <xsl:text>)</xsl:text>
      <xsl:value-of select="@path"/>
    </xsl:attribute>
  </xsl:element>
</xsl:template>

<!--
   Inserts a "href" attribute. Replaces the attribute

     xsl-ext:href="$LID"

   by the attribute

     href="$URL"

   where $URL is the URL that emerged from looking up the local id $LID.
-->

<xsl:template match="@xsl-ext:href">
  <xsl:attribute name="href">
    <xsl:call-template name="xsl.resolve-lid">
      <xsl:with-param name="lid" select="."/>
    </xsl:call-template>
  </xsl:attribute>
</xsl:template>

<!--
   Inserts a "src" attribute. Replaces the attribute

     xsl-ext:src="$LID"

   by the attribute

     src="$URL"

   where $URL is the URL that emerged from looking up the local id $LID.
-->

<xsl:template match="@xsl-ext:src">
  <xsl:attribute name="src">
    <xsl:call-template name="xsl.resolve-lid">
      <xsl:with-param name="lid" select="."/>
    </xsl:call-template>
  </xsl:attribute>
</xsl:template>

<!--
   Inserts an XHTML "img" element.
-->

<xsl:template match="xsl-ext:image">
  <img xmlns="http://www.w3.org/1999/xhtml">
    <xsl:call-template name="xsl.set-image-src-attributes"/>
    <xsl:apply-templates select="@*"/>
  </img>
</xsl:template>

<!--
   Inserts an XHTML "applet" element.
-->

<xsl:template match="xsl-ext:applet">
  <applet xmlns="http://www.w3.org/1999/xhtml">
    <xsl:call-template name="xsl.set-applet-src-attributes"/>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </applet>
</xsl:template>

<!--
   Inserts an CSS "url(...)" construct.
-->

<xsl:template match="xsl-ext:image[local-name(..)='style']" priority="2">
  <xsl:text>url("</xsl:text>
  <xsl:call-template name="xsl.resolve-lid"/>
  <xsl:text>")</xsl:text>
</xsl:template>

<!--
   Copies all parameters and templates in $xsl.resources that have the
   xsl-lib:included="yes" flag into the result tree.
-->

<xsl:template match="xsl-ext:insert-japs-xsl-lib|xsl-ext:require-lid-resolving">
  <xsl:variable name="root" select="/"/>
  <xsl:text>&br;</xsl:text>
  <xsl:comment> japs xsl lib start </xsl:comment>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="$xsl.resources/*/xsl:param[@xsl-lib:included='yes']">
    <xsl:if test="not($root/*/xsl:param[@name=current()/@name])">
      <xsl:text>&br;</xsl:text>
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:for-each>
  <xsl:for-each select="$xsl.resources/*/xsl:variable[@xsl-lib:included='yes']">
    <xsl:if test="not($root/*/xsl:variable[@name=current()/@name])">
      <xsl:text>&br;</xsl:text>
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:for-each>
  <xsl:for-each select="$xsl.resources/*/xsl:template[@xsl-lib:included='yes']">
    <xsl:if test="not($root/*/xsl:template[@name=current()/@name])">
      <xsl:text>&br;</xsl:text>
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:for-each>
  <xsl:text>&br;</xsl:text>
  <xsl:comment> japs xsl lib end </xsl:comment>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
  Processes elements not in the XSL extension namespace, e.g., XSL elements. They are
  copied, and the templates of this stylesheet are applied to the children.
-->

<xsl:template match="*[not(namespace-uri()='&xmlns-xsl-ext;') and not(namespace-uri()='&xmlns-mumie;')]">
  <xsl:copy>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>

<!--
  Processes attributes not in the XSL extension namespace, e.g., XSL attributs. They
  are copied.
-->

<xsl:template match="@*[not(namespace-uri()='&xmlns-xsl-ext;') and not(namespace-uri()='&xmlns-mumie;')]">
  <xsl:copy-of select="."/>
</xsl:template>

<!--
  Processes comments. They are copied.
-->

<xsl:template match="comment()">
  <xsl:copy-of select="."/>
</xsl:template>

<!--
   Top-level template
-->

<xsl:template match="/">
  <xsl:apply-templates select="/*/mumie:content/*"/>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Support for legacy use of the "mumie" namespace                              -->
<!-- ================================================================================ -->

<xsl:template match="mumie:url">
  <xsl:call-template name="xsl.resolve-lid"/>
</xsl:template>

<xsl:template match="mumie:include">
  <xsl:element name="xsl:include">
   <xsl:call-template name="xsl.set-ref-attribute"/>
  </xsl:element>
</xsl:template>

<xsl:template match="mumie:import">
  <xsl:element name="xsl:import">
   <xsl:call-template name="xsl.set-ref-attribute"/>
  </xsl:element>
</xsl:template>

<xsl:template match="@mumie:select">
  <xsl:variable name="prefix" select="substring-before(.,':')"/>
  <xsl:variable name="path" select="substring-after(.,':')"/>
  <xsl:attribute name="select">
    <xsl:text>document('</xsl:text>
    <xsl:choose>
      <xsl:when test="$prefix='self'">
        <xsl:call-template name="xsl.url-of-self">
          <xsl:with-param name="internal">yes</xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="starts-with($prefix,'lid=')">
        <xsl:call-template name="xsl.resolve-lid">
          <xsl:with-param name="lid" select="substring-after($prefix,'lid=')"/>
          <xsl:with-param name="internal">yes</xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="xsl.error">
          <xsl:with-param name="message" select="concat('Unknown prefix: ',$prefix)"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>')</xsl:text>
    <xsl:value-of select="$path"/>
  </xsl:attribute>

</xsl:template>

<xsl:template match="mumie:value-of">
  <xsl:element name="xsl:value-of">
    <xsl:attribute name="select">
      <xsl:text>document(</xsl:text>
      <xsl:call-template name="xsl.resolve-lid"/>
      <xsl:text>)</xsl:text>
      <xsl:value-of select="@path"/>
    </xsl:attribute>
  </xsl:element>
</xsl:template>

<xsl:template match="@mumie:href">
  <xsl:attribute name="href">
    <xsl:call-template name="xsl.resolve-lid">
      <xsl:with-param name="lid" select="."/>
    </xsl:call-template>
  </xsl:attribute>
</xsl:template>

<xsl:template match="@mumie:src">
  <xsl:attribute name="src">
    <xsl:call-template name="xsl.resolve-lid">
      <xsl:with-param name="lid" select="."/>
    </xsl:call-template>
  </xsl:attribute>
</xsl:template>

<xsl:template match="mumie:image">
  <img xmlns="http://www.w3.org/1999/xhtml">
    <xsl:call-template name="xsl.set-image-src-attributes"/>
    <xsl:apply-templates select="@*"/>
  </img>
</xsl:template>

<xsl:template match="mumie:applet">
  <applet xmlns="http://www.w3.org/1999/xhtml">
    <xsl:call-template name="xsl.set-applet-src-attributes"/>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
  </applet>
</xsl:template>

<xsl:template match="mumie:image[local-name(..)='style']" priority="2">
  <xsl:text>url("</xsl:text>
  <xsl:call-template name="xsl.resolve-lid"/>
  <xsl:text>")</xsl:text>
</xsl:template>

<xsl:template match="mumie:insert-japs-xsl-lib|mumie:require-lid-resolving">
  <xsl:variable name="root" select="/"/>
  <xsl:text>&br;</xsl:text>
  <xsl:comment> japs xsl lib start </xsl:comment>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="$xsl.resources/*/xsl:param[@xsl-lib:included='yes']">
    <xsl:if test="not($root/*/xsl:param[@name=current()/@name])">
      <xsl:text>&br;</xsl:text>
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:for-each>
  <xsl:for-each select="$xsl.resources/*/xsl:variable[@xsl-lib:included='yes']">
    <xsl:if test="not($root/*/xsl:variable[@name=current()/@name])">
      <xsl:text>&br;</xsl:text>
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:for-each>
  <xsl:for-each select="$xsl.resources/*/xsl:template[@xsl-lib:included='yes']">
    <xsl:if test="not($root/*/xsl:template[@name=current()/@name])">
      <xsl:text>&br;</xsl:text>
      <xsl:copy-of select="."/>
    </xsl:if>
  </xsl:for-each>
  <xsl:text>&br;</xsl:text>
  <xsl:comment> japs xsl lib end </xsl:comment>
  <xsl:text>&br;</xsl:text>
</xsl:template>

</xsl:stylesheet>
