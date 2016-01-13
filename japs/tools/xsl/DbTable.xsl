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

   $Id: DbTable.xsl,v 1.31 2007/08/23 12:07:25 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-strutil="xalan://net.mumie.util.StringUtil"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">DbTable.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: DbTable.xsl,v 1.31 2007/08/23 12:07:25 rassy Exp $</xsl:param>
<xsl:param name="DbTable.package">net.mumie.cocoon.notions</xsl:param>

<!--
   ==========================================================================================
   Templates for target "DbTable"
   ==========================================================================================
-->

<xsl:template name="DbTable.DOC">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding db table names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] DOC =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:call-template name="config.db-table-name.document"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.LATEST_DOC">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding db View names holding only latest versions.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] LATEST_DOC =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:call-template name="config.db-view-name.latest-document"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.PSEUDO_DOC">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie pseudo-document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.PseudoDocType}</xsl:text>
  <xsl:text> to the corresponding db table names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] PSEUDO_DOC =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/pseudodoc-types/pseudodoc-type">
    <xsl:text>&sp;&sp;"</xsl:text>
    <xsl:call-template name="config.db-table-name.pseudo-document"/>
    <xsl:text>",&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.REF">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping pairs of Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding db junction table names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[][] REF =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="from-doctype-node" select="."/>
    <xsl:variable name="from-doctype" select="@name"/>
    <xsl:text>&sp;&sp;{&br;</xsl:text>
    <xsl:for-each select="/*/document-types/document-type">
      <xsl:variable name="ref-exists">
        <xsl:call-template name="config.document-type.pair.refs-to">
          <xsl:with-param name="from-doctype-node" select="$from-doctype-node"/>
          <xsl:with-param name="to-doctype" select="@name"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:text>&sp;&sp;&sp;</xsl:text>
      <xsl:choose>
        <xsl:when test="$ref-exists='yes'">
          <xsl:text>"</xsl:text>
          <xsl:call-template name="config.db-table-name.refs-document-document">
            <xsl:with-param name="from-doctype" select="$from-doctype"/>
          </xsl:call-template>
          <xsl:text>"</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>null</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>,&br;</xsl:text>
    </xsl:for-each>
    <xsl:text>&sp;&sp;},&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.ANNS_PAIRS">
  <xsl:for-each select="/*/multidoc-annotations/multidoc-annotation">

    <!-- catching attribute "originator" -->
    <xsl:variable name="originator" select="@originator"/>
    <xsl:choose>
      <xsl:when test="$originator='user' or $originator='user_group'">
        
        <!-- catching pair of document types -->
        <xsl:variable name="order-max" select="count(document-types/document-type)"/>
        
        <xsl:choose>
          <xsl:when test="$order-max=2">
            <xsl:variable name="first-doctype"
                          select="document-types/document-type[@order='1']/@name"/>
            <xsl:variable name="second-doctype"
                          select="document-types/document-type[@order='2']/@name"/>
            <xsl:variable name="name">
              <xsl:call-template name="config.db-table-name.anns-document-document">
                <xsl:with-param name="originator" select="$originator"/>
                <xsl:with-param name="from-doctype" select="$first-doctype"/>
                <xsl:with-param name="to-doctype" select="$second-doctype"/>
              </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="constant">
              <xsl:call-template name="DbTable.constant.anns-document-document">
                <xsl:with-param name="originator" select="$originator"/>
                <xsl:with-param name="first-doctype" select="$first-doctype"/>
                <xsl:with-param name="second-doctype" select="$second-doctype"/>
              </xsl:call-template>
            </xsl:variable>
            <xsl:text>&sp;/**&br;</xsl:text>
            <xsl:text>&sp; * Constant String holding the table name of annotations by&br;</xsl:text>
            <xsl:text>&sp; * originators as defined in </xsl:text>
            <xsl:choose>
              <xsl:when test="$originator='user'">
                <xsl:text>{@link net.mumie.cocoon.pseudodocs.User}&br;</xsl:text>
              </xsl:when>
              <xsl:when test="$originator='user_group'">
                <xsl:text>{@link net.mumie.cocoon.pseudodocs.UserGroup}&br;</xsl:text>
              </xsl:when>
            </xsl:choose>
            <xsl:text>&sp; * to pairs of Mumie document types as defined in&br;</xsl:text>
            <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DbTable#REF} &br;</xsl:text>
            <xsl:text>&sp; */&br;</xsl:text>
            <xsl:text>&br;</xsl:text>
            <xsl:text>&sp;public static final String </xsl:text>
            <xsl:value-of select="$constant"/>
            <xsl:text> = "</xsl:text>
            <xsl:value-of select="$name"/>
            <xsl:text>";&br;</xsl:text>
            <xsl:text>&br;</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="no">
              <xsl:text>Creation of java annotations table constant skipped.&br;</xsl:text>
              <xsl:text>Annotations to more than two document types are not yet implemented&br;</xsl:text>
              <xsl:text>order-max is: </xsl:text>
              <xsl:value-of select="$order-max"/>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          <xsl:text>Invalid or missing attribute "originator" in multidoc-annotation.&br;</xsl:text>
          <xsl:text>Must be "user" or "user_group". Is: </xsl:text>
          <xsl:value-of select="$originator"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template name="DbTable.constant.anns-document-document">
  <xsl:param name="originator" select="'user'"/>
  <xsl:param name="first-doctype"/>
  <xsl:param name="second-doctype"/>
  <xsl:text>ANNS_</xsl:text>
  <xsl:choose>
    <xsl:when test="$originator='user'">
      <xsl:text>USER</xsl:text>
    </xsl:when>
    <xsl:when test="$originator='user_group'">
      <xsl:text>USER_GROUP</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        <xsl:text>Invalid or missing attribute "originator" in multidoc-annotation.&br;</xsl:text>
        <xsl:text>Must be "user" or "user_group". Is: </xsl:text>
        <xsl:value-of select="$originator"/>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>_</xsl:text>
  <xsl:call-template name="config.upcase">
    <xsl:with-param name="string" select="$first-doctype"/>
  </xsl:call-template>
  <xsl:text>_</xsl:text>
  <xsl:call-template name="config.upcase">
    <xsl:with-param name="string" select="$second-doctype"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="DbTable.ANNS_SINGLE">
  <xsl:for-each select="/*/singledoc-annotations/singledoc-annotation">
    <xsl:variable name="originator" select="@originator"/>
    <xsl:choose>
      <xsl:when test="$originator='user' or $originator='user_group'">
        <xsl:variable name="doctype" select="document-type/@name"/>
        <xsl:variable name="name">
          <xsl:call-template name="config.db-table-name.anns-single-document">
            <xsl:with-param name="originator" select="@originator"/>
            <xsl:with-param name="doctype" select="$doctype"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="constant">
          <xsl:call-template name="DbTable.constant.anns-document">
            <xsl:with-param name="originator" select="$originator"/>
            <xsl:with-param name="doctype" select="$doctype"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:text>&sp;/**&br;</xsl:text>
        <xsl:text>&sp; * Constant String holding the table name of annotations by&br;</xsl:text>
        <xsl:text>&sp; * originators as defined in </xsl:text>
        <xsl:choose>
          <xsl:when test="$originator='user'">
            <xsl:text>{@link net.mumie.cocoon.pseudodocs.User}&br;</xsl:text>
          </xsl:when>
          <xsl:when test="$originator='user_group'">
            <xsl:text>{@link net.mumie.cocoon.pseudodocs.UserGroup}&br;</xsl:text>
          </xsl:when>
        </xsl:choose>
        <xsl:text>&sp; * to Mumie document types as defined in&br;</xsl:text>
        <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType} &br;</xsl:text>
        <xsl:text>&sp; */&br;</xsl:text>
        <xsl:text>&br;</xsl:text>
        <xsl:text>&sp;public static final String </xsl:text>
        <xsl:value-of select="$constant"/>
        <xsl:text> = "</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>";&br;</xsl:text>
        <xsl:text>&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          <xsl:text>Invalid or missing attribute "originator" in singledoc-annotation.&br;</xsl:text>
          <xsl:text>Must be "user" or "user_group". Is: </xsl:text>
          <xsl:value-of select="$originator"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template name="DbTable.constant.anns-document">
  <xsl:param name="originator" select="'user'"/>
  <xsl:param name="doctype"/>
  <xsl:text>ANNS_</xsl:text>
  <xsl:choose>
    <xsl:when test="$originator='user'">
      <xsl:text>USER</xsl:text>
    </xsl:when>
    <xsl:when test="$originator='user_group'">
      <xsl:text>USER_GROUP</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        <xsl:text>Invalid or missing attribute "originator" in singledoc-annotation.&br;</xsl:text>
        <xsl:text>Must be "user" or "user_group". Is: </xsl:text>
        <xsl:value-of select="$originator"/>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>_</xsl:text>
  <xsl:call-template name="config.upcase">
    <xsl:with-param name="string" select="$doctype"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="DbTable.REF_XXX_SOURCE">
  <xsl:param name="format"/>
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding </xsl:text>
  <xsl:value-of select="$format"/>
  <xsl:text>source db junction table names.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] REF_</xsl:text>
  <xsl:call-template name="config.upcase">
    <xsl:with-param name="string" select="$format"/>
  </xsl:call-template>
  <xsl:text>_SOURCE =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:choose>
      <xsl:when test="$is-generic='yes'">
        <xsl:text>null</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-table-name.refs-document-source">
          <xsl:with-param name="format" select="$format"/>
        </xsl:call-template>
        <xsl:text>"</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.THEME_MAP">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding theme map table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] THEME_MAP =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-generic-counterpart">
      <xsl:call-template name="config.document-type.has-generic-counterpart"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-generic-counterpart='yes'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-table-name.theme-map-document"/>
        <xsl:text>"</xsl:text>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&sp;&sp;null,&br;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.GDIM">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding gdim table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] GDIM =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-generic-counterpart">
      <xsl:call-template name="config.document-type.has-generic-counterpart"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-generic-counterpart='yes'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-table-name.gdim-document"/>
        <xsl:text>"</xsl:text>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&sp;&sp;null,&br;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.VC_THREADS">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding vc_threads table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] VC_THREADS =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-table-name.vc_threads"/>
        <xsl:text>"</xsl:text>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&sp;&sp;null,&br;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.DOCUMENT_READ_PERMISSIONS">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding read access table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] DOCUMENT_READ_PERMISSIONS =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-table-name.read-permissions"/>
        <xsl:text>"</xsl:text>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&sp;&sp;null,&br;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.PSEUDODOC_READ_PERMISSIONS">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie pseudo-document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.PseudoDocType}</xsl:text>
  <xsl:text> to the corresponding read access table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] PSEUDODOC_READ_PERMISSIONS =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/pseudodoc-types/pseudodoc-type">
    <xsl:choose>
      <xsl:when test="'true'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-table-name.read-permissions"/>
        <xsl:text>"</xsl:text>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&sp;&sp;null,&br;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.WRITE_PERMISSIONS">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping non-generic Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding write access table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] WRITE_PERMISSIONS =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-table-name.write-permissions"/>
        <xsl:text>"</xsl:text>
        <xsl:text>,&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&sp;&sp;null,&br;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.PSEUDODOC_WRITE_PERMISSIONS">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping pseudo-document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.PseudoDocType}</xsl:text>
  <xsl:text> to the corresponding write access table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] PSEUDODOC_WRITE_PERMISSIONS =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/pseudodoc-types/pseudodoc-type">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:text>"</xsl:text>
    <xsl:call-template name="config.db-table-name.write-permissions"/>
    <xsl:text>"</xsl:text>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.AUTHORS">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding author reference table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] AUTHORS =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:text>"</xsl:text>
    <xsl:call-template name="config.db-table-name.document-authors"/>
    <xsl:text>"</xsl:text>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.MEMBERS">
  <xsl:text>&sp;/**&br;</xsl:text>
  <xsl:text>&sp; * Array mapping Mumie document types as defined in&br;</xsl:text>
  <xsl:text>&sp; * {@link net.mumie.cocoon.notions.DocType}</xsl:text>
  <xsl:text> to the corresponding member reference table names&br;</xsl:text>
  <xsl:text>&sp; * of the database.&br;</xsl:text>
  <xsl:text>&sp; */&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;public static final String[] MEMBERS =&br;</xsl:text>
  <xsl:text>&sp;{&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:variable name="has-members">
      <xsl:call-template name="config.document-type.has-members"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$has-members='yes'">
        <xsl:text>"</xsl:text>
        <xsl:call-template name="config.db-view-name.document-members"/>
        <xsl:text>"</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>null</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>,&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&sp;};&br;</xsl:text>
</xsl:template>

<xsl:template name="DbTable.tables">
  <xsl:for-each select="/*/db-tables/db-table">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>&sp;/**&br;</xsl:text>
    <xsl:text>&sp; * </xsl:text>
    <xsl:call-template name="config.description"/>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp; */&br;</xsl:text>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;final public static String </xsl:text>
    <xsl:call-template name="config.db-table.constant"/>
    <xsl:text> = "</xsl:text>
    <xsl:call-template name="config.db-table.name"/>
    <xsl:text>";&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template name="DbTable">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$DbTable.package"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text> * &lt;p&gt;&br;</xsl:text>
      <xsl:text> * &sp;Defines static constants wrapping db table names.&br;</xsl:text>
      <xsl:text> * &lt;/p&gt;&br;</xsl:text>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class DbTable&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="DbTable.DOC"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.LATEST_DOC"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.PSEUDO_DOC"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.REF"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.ANNS_PAIRS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.ANNS_SINGLE"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.REF_XXX_SOURCE">
    <xsl:with-param name="format" select="'text'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.REF_XXX_SOURCE">
    <xsl:with-param name="format" select="'binary'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.THEME_MAP"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.GDIM"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.VC_THREADS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.DOCUMENT_READ_PERMISSIONS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.PSEUDODOC_READ_PERMISSIONS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.WRITE_PERMISSIONS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.PSEUDODOC_WRITE_PERMISSIONS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.AUTHORS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.MEMBERS"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="DbTable.tables"/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="DbTable"/>
</xsl:template>

</xsl:stylesheet>
