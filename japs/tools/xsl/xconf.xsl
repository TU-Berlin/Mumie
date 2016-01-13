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

   $Id: xconf.xsl,v 1.26 2009/09/08 22:39:13 rassy Exp $

   ======================================================================================

   In a transformation with this XSL stylesheet, there are two sources:

   1. The global Japs configuration file, i.e., $JAPS_HOME/config/config.xml.
      Its root node is stored in the parameter "xconf.config".

   2. The XML source of the cocoon.xconf file. Usually, this is
      $JAPS_HOME/config/cocoon.xconf.src. Its root node is stored in the
      parameter "xconf.source".

   The first source is read as the usual (primary) source of the stylesheet. The
   second source is read by means of the document() function.

   ======================================================================================

-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mumie="http://www.mumie.net/xml-namespace/xconf"
                xmlns:x-util="xalan://net.mumie.util.Util"
                exclude-result-prefixes="mumie x-util">

<xsl:import href="config.xsl"/>

<xsl:output method="xml" indent="yes"/>
<xsl:preserve-space elements="*"/>

<!--
   ======================================================================================
   Global paramteres and variables
   ======================================================================================
-->

<!--
   Root node of the first source, i.e., the global configuration (see above)
-->

<xsl:param name="xconf.config" select="/"/>

<!--
  Filename of the second source, i.e., the XML source of cocoon.xconf (see above)
-->

<xsl:param name="xconf.source.filename"/>

<!--
  Root node of the second source, i.e., the XML source of cocoon.xconf (see above)
-->

<xsl:param name="xconf.source" select="document($xconf.source.filename)"/>

<!--
   The namespace URI of the Mumie XML used in the source document
-->

<xsl:param name="xconf.mumie-namespace"
           select="'http://www.mumie.net/xml-namespace/xconf'"/>

<!--
   The XSL namespace URI
-->

<xsl:param name="xsl-namespace"
           select="'http://www.w3.org/1999/XSL/Transform'"/>

<!--
   Database settings
-->

<xsl:param name="xconf.db.host"/>
<xsl:param name="xconf.db.port"/>
<xsl:param name="xconf.db.name"/>
<xsl:param name="xconf.db.user.name"/>
<xsl:param name="xconf.db.user.password"/>
<xsl:variable name="xconf.db.url">
  <xsl:value-of select="concat('jdbc:postgresql://',$xconf.db.host,':',$xconf.db.port,'/')"/>
</xsl:variable>

<!--
  Pool settings
-->

<xsl:param name="xconf.document.pool-max">128</xsl:param>
<xsl:param name="xconf.document.pool-min">8</xsl:param>
<xsl:param name="xconf.document.pool-grow">4</xsl:param>
<xsl:param name="xconf.document-to-checkin.pool-max">128</xsl:param>
<xsl:param name="xconf.document-to-checkin.pool-min">8</xsl:param>
<xsl:param name="xconf.document-to-checkin.pool-grow">4</xsl:param>
<xsl:param name="xconf.pseudodoc-to-checkin.pool-max">128</xsl:param>
<xsl:param name="xconf.pseudodoc-to-checkin.pool-min">8</xsl:param>
<xsl:param name="xconf.pseudodoc-to-checkin.pool-grow">4</xsl:param>

<!--
   Mail helper settings
-->

<xsl:param name="xconf.mail.smtp.host"/>
<xsl:param name="xconf.mail.from.address"/>
<xsl:param name="xconf.mail.from.name"/>
<xsl:param name="xconf.mail.reply.address"/>
<xsl:param name="xconf.mail.reply.name"/>

<!--
   Upload helper settings
-->

<xsl:param name="xconf.upload.dir"/>

<!--
   Checkin helper settings
-->

<xsl:param name="xconf.checkin.defaults.file"/>

<!--
   Sign helper settings
-->

<xsl:param name="xconf.sign.keystore.filename"/>
<xsl:param name="xconf.sign.keystore.type"/>
<xsl:param name="xconf.sign.keystore.password"/>
<xsl:param name="xconf.sign.key.alias"/>
<xsl:param name="xconf.sign.key.password"/>

<!--
   Receipt settings
-->

<xsl:param name="xconf.receipt.dir"/>

<!--
   Settings for UserProblemData
-->

<xsl:param name="xconf.correction.tmp.dir"/>

<!--
   Message destination table settings
-->

<xsl:param name="xconf.message.destination.table.filename"/>

<!--
   ======================================================================================
   Templates
   =======================================================================================
-->

<!--
   Default template for non-mumie elements. Simply copies node and applies templates to
   children.
-->

<xsl:template name="default">
  <xsl:copy>
    <xsl:apply-templates select="@*[namespace-uri()=$xconf.mumie-namespace]"/>
    <xsl:copy-of select="@*[namespace-uri()!=$xconf.mumie-namespace]"/>
    <xsl:apply-templates select="node()"/>
  </xsl:copy>
</xsl:template>

<!--
   The default template as a matching template.
-->

<xsl:template match="*[namespace-uri()!=$xconf.mumie-namespace]" priority="0">
  <xsl:call-template name="default"/>
</xsl:template>

<!--
  Processes comments. They are simply copied.
-->

<xsl:template match="comment()">
  <xsl:copy/>
</xsl:template>

<!--
   Creates the db-helper entry for the Japs database.
-->

<xsl:template match="mumie:db-helper-config">
  <connection-name><xsl:value-of select="$xconf.db.name"/></connection-name>
  <connection-url><xsl:value-of select="$xconf.db.url"/></connection-url>
  <user-name><xsl:value-of select="$xconf.db.user.name"/></user-name>
  <user-password><xsl:value-of select="$xconf.db.user.password"/></user-password>
</xsl:template>

<!--
   Creates the "documents" element.
-->

<xsl:template match="mumie:documents">
  <xsl:variable name="node" select="."/>
  <xsl:variable name="element.name" select="local-name()"/>
  <xsl:element name="{$element.name}">
    <xsl:for-each select="$xconf.config/*/document-types/document-type">
      <xsl:variable name="child-element.name" select="@name"/>
      <xsl:variable name="child-element" select="$node/*[name()=$child-element.name]"/>
      <xsl:choose>
        <xsl:when test="$child-element">
          <xsl:copy-of select="$child-element"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="hint">
            <xsl:call-template name="config.document-type.hint"/>
          </xsl:variable>
          <xsl:element name="{$child-element.name}">
            <xsl:attribute name="name">
              <xsl:value-of select="$hint"/>
            </xsl:attribute>
            <xsl:attribute name="pool-max">
              <xsl:value-of select="$xconf.document.pool-max"/>
            </xsl:attribute>
            <xsl:attribute name="pool-min">
              <xsl:value-of select="$xconf.document.pool-min"/>
            </xsl:attribute>
            <xsl:attribute name="pool-grow">
              <xsl:value-of select="$xconf.document.pool-grow"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:element>
</xsl:template>

<!--
   Creates the "content-objects-to-checkin" element.
-->

<xsl:template match="mumie:content-objects-to-checkin">
  <xsl:variable name="node" select="."/>
  <xsl:variable name="element.name" select="local-name()"/>
  <xsl:element name="{$element.name}">
    <xsl:for-each select="$xconf.config/*/document-types/document-type">
      <xsl:variable name="child-element.name" select="@name"/>
      <xsl:variable name="child-element" select="$node/*[name()=$child-element.name]"/>
      <xsl:choose>
        <xsl:when test="$child-element">
          <xsl:copy-of select="$child-element"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="hint">
            <xsl:call-template name="config.hint"/>
          </xsl:variable>
          <xsl:element name="{$child-element.name}">
            <xsl:attribute name="name">
              <xsl:value-of select="$hint"/>
            </xsl:attribute>
            <xsl:attribute name="pool-max">
              <xsl:value-of select="$xconf.document-to-checkin.pool-max"/>
            </xsl:attribute>
            <xsl:attribute name="pool-min">
              <xsl:value-of select="$xconf.document-to-checkin.pool-min"/>
            </xsl:attribute>
            <xsl:attribute name="pool-grow">
              <xsl:value-of select="$xconf.document-to-checkin.pool-grow"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:for-each select="$xconf.config/*/pseudodoc-types/pseudodoc-type">
      <xsl:variable name="child-element.name" select="@name"/>
      <xsl:variable name="child-element" select="$node/*[name()=$child-element.name]"/>
      <xsl:choose>
        <xsl:when test="$child-element">
          <xsl:copy-of select="$child-element"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="hint">
            <xsl:call-template name="config.hint"/>
          </xsl:variable>
          <xsl:element name="{$child-element.name}">
            <xsl:attribute name="name">
              <xsl:value-of select="$hint"/>
            </xsl:attribute>
            <xsl:attribute name="pool-max">
              <xsl:value-of select="$xconf.pseudodoc-to-checkin.pool-max"/>
            </xsl:attribute>
            <xsl:attribute name="pool-min">
              <xsl:value-of select="$xconf.pseudodoc-to-checkin.pool-min"/>
            </xsl:attribute>
            <xsl:attribute name="pool-grow">
              <xsl:value-of select="$xconf.pseudodoc-to-checkin.pool-grow"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:element>
</xsl:template>

<!--
   Creates the "pseudo-documents" element.
-->

<xsl:template match="mumie:pseudo-documents">
  <xsl:variable name="element.name" select="local-name()"/>
  <xsl:element name="{$element.name}">
    <xsl:for-each select="$xconf.config/*/pseudodoc-types/pseudodoc-type">
      <xsl:variable name="hint">
        <xsl:call-template name="config.hint"/>
      </xsl:variable>
      <xsl:variable name="child-element.name" select="@name"/>
      <xsl:element name="{$child-element.name}">
        <xsl:attribute name="name">
          <xsl:value-of select="$hint"/>
        </xsl:attribute>
      </xsl:element>
    </xsl:for-each>
  </xsl:element>
</xsl:template>

<!--
   Creates the "sync-commands" element.
-->

<xsl:template match="mumie:sync-commands">
  <xsl:variable name="element.name" select="local-name()"/>
  <xsl:element name="{$element.name}">
    <xsl:for-each select="$xconf.config/*/sync-commands/sync-command">
      <xsl:variable name="hint">
        <xsl:call-template name="config.sync-command.hint"/>
      </xsl:variable>
      <xsl:variable name="child-element.name" select="@name"/>
      <xsl:element name="{$child-element.name}">
        <xsl:attribute name="name">
          <xsl:value-of select="$hint"/>
        </xsl:attribute>
      </xsl:element>
    </xsl:for-each>
  </xsl:element>
</xsl:template>

<!--
   Creates the "event-handlers" element.
-->

<xsl:template match="mumie:event-handlers">
  <xsl:variable name="element.name" select="local-name()"/>
  <xsl:element name="{$element.name}">
    <xsl:for-each select="$xconf.config/*/events/event">
      <xsl:variable name="hint">
        <xsl:call-template name="config.hint"/>
      </xsl:variable>
      <xsl:variable name="child-element.name" select="@name"/>
      <xsl:element name="{$child-element.name}">
        <xsl:attribute name="name">
          <xsl:value-of select="$hint"/>
        </xsl:attribute>
      </xsl:element>
    </xsl:for-each>
  </xsl:element>
</xsl:template>

<!--
  Mail helper configuration
-->

<xsl:template match="mumie:mail-helper-config">
  <smtp-host><xsl:value-of select="$xconf.mail.smtp.host"/></smtp-host>
  <from-name><xsl:value-of select="$xconf.mail.from.name"/></from-name>
  <from-address><xsl:value-of select="$xconf.mail.from.address"/></from-address>
  <reply-name><xsl:value-of select="$xconf.mail.reply.name"/></reply-name>
  <reply-address><xsl:value-of select="$xconf.mail.reply.address"/></reply-address>
</xsl:template>

<!--
  Upload helper configuration
-->

<xsl:template match="mumie:upload-helper-config">
  <upload-dir><xsl:value-of select="$xconf.upload.dir"/></upload-dir>
</xsl:template>

<!--
  Upload helper configuration
-->

<xsl:template match="mumie:checkin-helper-config">
  <defaults-file><xsl:value-of select="$xconf.checkin.defaults.file"/></defaults-file>
</xsl:template>

<!--
  Sign helper configuration
-->

<xsl:template match="mumie:sign-helper-config">
  <keystore-filename><xsl:value-of select="$xconf.sign.keystore.filename"/></keystore-filename>
  <keystore-type><xsl:value-of select="$xconf.sign.keystore.type"/></keystore-type>
  <keystore-password><xsl:value-of select="$xconf.sign.keystore.password"/></keystore-password>
  <key-alias><xsl:value-of select="$xconf.sign.key.alias"/></key-alias>
  <key-password><xsl:value-of select="$xconf.sign.key.password"/></key-password>
</xsl:template>

<!--
  Receipt helper configuration
-->

<xsl:template match="mumie:receipt-helper-config">
  <receipt-dir><xsl:value-of select="$xconf.receipt.dir"/></receipt-dir>
</xsl:template>

<!--
  User problem data configuration
-->

<xsl:template match="mumie:user-problem-data-config">
  <correction-tmp-dir><xsl:value-of select="$xconf.correction.tmp.dir"/></correction-tmp-dir>
</xsl:template>

<!--
   Message destination table configuration
-->

<xsl:template match="mumie:message-destination-table-config">
  <filename><xsl:value-of select="$xconf.message.destination.table.filename"/></filename>
</xsl:template>

<!--
   =======================================================================================
   Top level template
   =======================================================================================
-->

<xsl:template match="/">
  <xsl:apply-templates select="$xconf.source/*"/>
</xsl:template>

</xsl:stylesheet>
