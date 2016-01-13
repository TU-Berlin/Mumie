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

   $Id: roles.xsl,v 1.8 2008/10/07 23:33:54 rassy Exp $
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mumie="http://www.mumie.net/xml-namespace/roles"
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
   Root node of the config file.   
-->

<xsl:param name="roles.config" select="/"/>

<!--
   Filename of the roles source
-->

<xsl:param name="roles.source.filename"/>

<!--
   The roles source (root node).
-->

<xsl:param name="roles.source" select="document($roles.source.filename)"/>

<!--
   The namespace uri of the Mumie XML used in the source document
-->

<xsl:param name="roles.mumie-namespace"
           select="'http://www.mumie.net/xml-namespace/roles'"/>

<!--
   The XSL namespace uri
-->

<xsl:param name="xsl-namespace"
           select="'http://www.w3.org/1999/XSL/Transform'"/>

<!--
   Package names
-->

<xsl:param name="roles.package.document" select="'net.mumie.cocoon.documents'"/>
<xsl:param name="roles.package.checkin" select="'net.mumie.cocoon.checkin'"/>
<xsl:param name="roles.package.checkin.documents"
           select="concat($roles.package.checkin,'.documents')"/>
<xsl:param name="roles.package.checkin.pseudodocs"
           select="concat($roles.package.checkin,'.pseudodocs')"/>
<xsl:param name="roles.package.pseudodoc" select="'net.mumie.cocoon.pseudodocs'"/>
<xsl:param name="roles.package.sync" select="'net.mumie.cocoon.sync'"/>
<xsl:param name="roles.package.event" select="'net.mumie.cocoon.event'"/>

<!--
   ======================================================================================
   Templates
   =======================================================================================
-->

<!--
   Default template for non-mumie elements. Simply copies node and applies templates
   to children.
-->

<xsl:template match="*[namespace-uri()!=$roles.mumie-namespace]" priority="0">
  <xsl:copy>
    <xsl:apply-templates select="@*[namespace-uri()=$roles.mumie-namespace]"/>
    <xsl:copy-of select="@*[namespace-uri()!=$roles.mumie-namespace]"/>
    <xsl:apply-templates select="node()"/>
  </xsl:copy>
</xsl:template>

<!--
  Processes comments. They are simply copied.
-->

<xsl:template match="comment()">
  <xsl:copy/>
</xsl:template>

<!--
  Inserts the hints for the DocumentSelector
-->

<xsl:template match="role[@name='net.mumie.cocoon.documents.DocumentSelector']/mumie:hints">
  <xsl:for-each select="$roles.config/*/document-types/document-type">
    <hint>
      <xsl:attribute name="shorthand">
        <xsl:call-template name="config.document-type.hint"/>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:value-of select="concat($roles.package.document,'.')"/>
        <xsl:call-template name="config.java-class-name.document"/>
      </xsl:attribute>
    </hint>
  </xsl:for-each>
</xsl:template>

<!--
  Inserts the hints for the PseudoDocumentSelector
-->

<xsl:template match="role[@name='net.mumie.cocoon.pseudodocs.PseudoDocumentSelector']/mumie:hints">
  <xsl:for-each select="$roles.config/*/pseudodoc-types/pseudodoc-type">
    <hint>
      <xsl:attribute name="shorthand">
        <xsl:call-template name="config.hint"/>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:value-of select="concat($roles.package.pseudodoc,'.')"/>
        <xsl:call-template name="config.java-class-name.pseudodoc"/>
      </xsl:attribute>
    </hint>
  </xsl:for-each>
</xsl:template>

<!--
  Inserts the hints for the ContentObjectToCheckinSelector
-->

<xsl:template match="role[@name='net.mumie.cocoon.checkin.ContentObjectToCheckinSelector']/mumie:hints">
  <xsl:for-each select="$roles.config/*/document-types/document-type">
    <hint>
      <xsl:attribute name="shorthand">
        <xsl:call-template name="config.hint"/>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:value-of select="concat($roles.package.checkin.documents,'.')"/>
        <xsl:call-template name="config.java-class-name.document-to-checkin"/>
      </xsl:attribute>
    </hint>
  </xsl:for-each>
  <xsl:for-each select="$roles.config/*/pseudodoc-types/pseudodoc-type">
    <hint>
      <xsl:attribute name="shorthand">
        <xsl:call-template name="config.hint"/>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:value-of select="concat($roles.package.checkin.pseudodocs,'.')"/>
        <xsl:call-template name="config.java-class-name.pseudodoc-to-checkin"/>
      </xsl:attribute>
    </hint>
  </xsl:for-each>
</xsl:template>

<!--
  Inserts the hints for the SyncCommandSelector
-->

<xsl:template match="role[@name='net.mumie.cocoon.sync.SyncCommandSelector']/mumie:hints">
  <xsl:for-each select="$roles.config/*/sync-commands/sync-command">
    <hint>
      <xsl:attribute name="shorthand">
        <xsl:call-template name="config.sync-command.hint"/>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:value-of select="concat($roles.package.sync,'.')"/>
        <xsl:call-template name="config.java-class-name.sync-command"/>
      </xsl:attribute>
    </hint>
  </xsl:for-each>
</xsl:template>

<!--
  Inserts the hints for the EventHandlerSelector
-->

<xsl:template match="role[@name='net.mumie.cocoon.event.EventHandlerSelector']/mumie:hints">
  <xsl:for-each select="$roles.config/*/events/event">
    <hint>
      <xsl:attribute name="shorthand">
        <xsl:call-template name="config.hint"/>
      </xsl:attribute>
      <xsl:attribute name="class">
        <xsl:value-of select="concat($roles.package.event, '.NullEventHandler')"/>
      </xsl:attribute>
    </hint>
  </xsl:for-each>
</xsl:template>

<!--
   =======================================================================================
   Top level template
   =======================================================================================
-->

<xsl:template match="/">
  <xsl:apply-templates select="$roles.source/*"/>
</xsl:template>

</xsl:stylesheet>
