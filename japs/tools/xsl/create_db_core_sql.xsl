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

   $Id: create_db_core_sql.xsl,v 1.72 2010/01/03 21:42:54 rassy Exp $
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-strutil="xalan://net.mumie.util.StringUtil"
                xmlns:x-encrypt="xalan://net.mumie.japs.build.PasswordEncryptorWrapper"
                xmlns:x-sql="xalan://net.mumie.sql.SQLUtil"
                version="1.0">

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:include href="config.xsl"/>
<xsl:include href="db_notions.xsl"/>
<xsl:include href="xml_notions.xsl"/>

<!--
   =====================================================================================
   Parameters and variables
   =====================================================================================
-->

<xsl:param name="xml-source">config.xml</xsl:param>
<xsl:param name="this.filename">create_db_core_sql.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: create_db_core_sql.xsl,v 1.72 2010/01/03 21:42:54 rassy Exp $</xsl:param>

<xsl:param name="db-core.default-xml-content">
  <xsl:choose>
    <xsl:when test="/*/@default-xml-content">
      <xsl:value-of select="/*/@default-xml-content"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'&lt;document/&gt;'"/>
    </xsl:otherwise>
  </xsl:choose>  
</xsl:param>

  <xsl:param name="db-core.annotations.default-xml-content">
    <xsl:choose>
      <xsl:when test="/*/@default-xml-content">
        <xsl:value-of select="/*/@default-xml-content"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&lt;?xml version="1.0" encoding="ASCII"?&gt;
&lt;data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"/&gt;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>  
  </xsl:param>

<xsl:param name="db-core.pure-sql" select="'no'"/>

<xsl:param name="db-core.rootsection.id" select="'0'"/>
<xsl:param name="db-core.rootsection.pure_name" select="''"/>
<xsl:param name="db-core.rootsection.name" select="'ROOT'"/>
<xsl:param name="db-core.default-theme.id" select="'0'"/>
<xsl:param name="db-core.default-theme.pure_name" select="'tme_default'"/>
<xsl:param name="db-core.default-theme.name" select="'Default Theme'"/>
<xsl:param name="db-core.default-language.id" select="'0'"/>
<xsl:param name="db-core.default-language.pure_name" select="'lng_de'"/>
<xsl:param name="db-core.default-language.name" select="'Deutsch'"/>
<xsl:param name="db-core.default-language.code" select="'de'"/>
<xsl:param name="db-core.neutral-language.id" select="'1'"/>
<xsl:param name="db-core.neutral-language.pure_name" select="'lng_zxx'"/>
<xsl:param name="db-core.neutral-language.name" select="'Neutral'"/>
<xsl:param name="db-core.neutral-language.code" select="'zxx'"/>
<xsl:param name="db-core.admin-group.id" select="'0'"/>
<xsl:param name="db-core.admin-group.pure_name" select="'ugr_admins'"/>
<xsl:param name="db-core.admin-group.name" select="'admins'"/>
<xsl:param name="db-core.admin-group.description" select="'Usergroup of Administrators'"/>
<xsl:param name="db-core.admin-user.id" select="'0'"/>
<xsl:param name="db-core.admin-user.pure_name" select="'usr_admin'"/>
<xsl:param name="db-core.admin-user.login_name" select="'admin'"/>
<xsl:param name="db-core.admin-user.surname" select="'Administrator'"/>
<xsl:param name="db-core.admin-user.description" select="'Administrator'"/>
<xsl:param name="db-core.admin-user.theme" select="$db-core.default-theme.id"/>
<xsl:param name="db-core.admin-user.language" select="$db-core.default-language.id"/>
<xsl:param name="db-core.admin-user.password" select="'mumie'"/>

<!--
   =====================================================================================
   Auxiliaries
   =====================================================================================
-->

<xsl:template name="db-core.separator">
  <xsl:text>-- ===========================================================================&br;</xsl:text>
</xsl:template>
  
<xsl:template name="db-core.echo">
  <xsl:param name="message"/>
  <xsl:if test="not ( $db-core.pure-sql = 'yes' )">
    <xsl:text>\qecho </xsl:text>
    <xsl:value-of select="$message"/>
    <xsl:text>&br;</xsl:text>
  </xsl:if>
</xsl:template>

<!--
   =====================================================================================
   Declaring columns
   =====================================================================================
-->

<xsl:template name="db-core.column.id">
  <xsl:param name="type" select="'serial'"/>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$type"/>
  <xsl:text> PRIMARY KEY</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.vc_thread">
  <xsl:param name="doctype" select="@name"/>
  <xsl:value-of select="$db-notions.column-name.vc_thread"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:call-template name="config.db-table-name.vc_threads">
    <xsl:with-param name="doctype" select="$doctype"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) NOT NULL DEFAULT 0</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.version">
  <xsl:value-of select="$db-notions.column-name.version"/>
  <xsl:text> int DEFAULT 1</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.deleted">
  <xsl:value-of select="$db-notions.column-name.deleted"/>
  <xsl:text> boolean NOT NULL DEFAULT false</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.hide">
  <xsl:value-of select="$db-notions.column-name.hide"/>
  <xsl:text> boolean NOT NULL DEFAULT false</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.category">
  <xsl:value-of select="$db-notions.column-name.category"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.categories"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.pure_name">
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text> text NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.name">
  <xsl:value-of select="$db-notions.column-name.name"/>
  <xsl:text> text NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.filename">
  <xsl:value-of select="$db-notions.column-name.filename"/>
  <xsl:text> text UNIQUE NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.login_name">
  <xsl:value-of select="$db-notions.column-name.login_name"/>
  <xsl:text> text UNIQUE DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.password">
  <xsl:value-of select="$db-notions.column-name.password"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.first_name">
  <xsl:value-of select="$db-notions.column-name.first_name"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.surname">
  <xsl:value-of select="$db-notions.column-name.surname"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.email">
  <xsl:value-of select="$db-notions.column-name.email"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.description">
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.qualified-name">
  <xsl:value-of select="$db-notions.column-name.qualified_name"/>
  <xsl:text> text NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.copyright">
  <xsl:value-of select="$db-notions.column-name.copyright"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.first_modified">
  <xsl:value-of select="$db-notions.column-name.first_modified"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.created">
  <xsl:value-of select="$db-notions.column-name.created"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.last_modified">
  <xsl:value-of select="$db-notions.column-name.last_modified"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.timeframe_start">
  <xsl:value-of select="$db-notions.column-name.timeframe_start"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.timeframe_end">
  <xsl:value-of select="$db-notions.column-name.timeframe_end"/>
  <xsl:text> timestamp DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.start_time">
  <xsl:value-of select="$db-notions.column-name.start_time"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.end_time">
  <xsl:value-of select="$db-notions.column-name.end_time"/>
  <xsl:text> timestamp DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.last_login">
  <xsl:value-of select="$db-notions.column-name.last_login"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.contained_in.deferrable">
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.custom_metainfo">
  <xsl:value-of select="$db-notions.column-name.custom_metainfo"/>
  <xsl:text> text DEFAULT '</xsl:text>
  <xsl:value-of select="$db-core.default-xml-content"/>
  <xsl:text>'</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.width">
  <xsl:value-of select="$db-notions.column-name.width"/>
  <xsl:text> int</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.height">
  <xsl:value-of select="$db-notions.column-name.height"/>
  <xsl:text> int</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.duration">
  <xsl:value-of select="$db-notions.column-name.duration"/>
  <xsl:text> int</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.semester">
  <xsl:value-of select="$db-notions.column-name.semester"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.semesters"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.content">
  <xsl:param name="format" select="@format"/>
  <xsl:param name="format">
    <xsl:choose>
      <xsl:when test="@format">
        <xsl:value-of select="@format"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'text'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>
  <xsl:choose>
    <xsl:when test="$format='text'">
      <xsl:value-of select="$db-notions.column-name.content"/>
      <xsl:text> text NOT NULL DEFAULT '</xsl:text>
      <xsl:value-of select="$db-core.default-xml-content"/>
      <xsl:text>'</xsl:text>
    </xsl:when>
    <xsl:when test="$format='binary'">
      <xsl:value-of select="$db-notions.column-name.content"/>
      <xsl:text> oid UNIQUE</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        <xsl:text>Invalid data format: </xsl:text>
        <xsl:value-of select="$format"/>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text></xsl:text>
</xsl:template>

<xsl:template name="db-core.column.content_type">
  <xsl:value-of select="$db-notions.column-name.content_type"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.media_types"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.content_length">
  <xsl:value-of select="$db-notions.column-name.content_length"/>
  <xsl:text> int NOT NULL DEFAULT -1</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.source">
  <xsl:value-of select="$db-notions.column-name.source"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.source_type">
  <xsl:value-of select="$db-notions.column-name.source_type"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.media_types"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.source_length">
  <xsl:value-of select="$db-notions.column-name.source_length"/>
  <xsl:text> int NOT NULL DEFAULT -1</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.info_page">
  <xsl:value-of select="$db-notions.column-name.info_page"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.generic_pages"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.summary">
  <xsl:value-of select="$db-notions.column-name.summary"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.generic_summarys"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.thumbnail">
  <xsl:value-of select="$db-notions.column-name.thumbnail"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.thumbnails"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<!-- TODO Fritz: Ueberpruefung der Kategorie-->

<xsl:template name="db-core.column.corrector">
  <xsl:value-of select="$db-notions.column-name.corrector"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.correctors"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.corrector.deferrable">
  <xsl:value-of select="$db-notions.column-name.corrector"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.correctors"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<xsl:template name="db-core.constraint.corrector-category">
  <xsl:param name="corrector-spec"/>
  <xsl:text>CHECK (</xsl:text>
  <xsl:value-of select="$db-notions.column-name.corrector"/>
  <xsl:text> IS NULL OR </xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/>
  <xsl:choose>
    <xsl:when test="$corrector-spec/@default='yes'">
      <xsl:text> NOT IN (</xsl:text>
      <xsl:for-each select="$corrector-spec/except-category">
        <xsl:if test="position()&gt;1"><xsl:text>,</xsl:text></xsl:if>
        <xsl:call-template name="config.category.code"/>
      </xsl:for-each>    
      <xsl:text>)</xsl:text>
    </xsl:when>
    <xsl:when test="$corrector-spec/@default='no'">
      <xsl:text> IN (</xsl:text>
      <xsl:for-each select="$corrector-spec/category">
        <xsl:if test="position()&gt;1"><xsl:text>,</xsl:text></xsl:if>
        <xsl:call-template name="config.category.code"/>
      </xsl:for-each>    
      <xsl:text>)</xsl:text>
    </xsl:when>
  </xsl:choose>
  <xsl:text>),&br;&sp;&sp;</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.changelog">
  <xsl:value-of select="$db-notions.column-name.changelog"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.from_doc">
  <xsl:param name="from-doctype-name"/>
  <xsl:variable name="from-table-name">
    <xsl:call-template name="config.db-table-name.document">
      <xsl:with-param name="doctype" select="$from-doctype-name"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$from-table-name"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.to_doc">
  <xsl:param name="to-doctype-name" select="@name"/>
  <xsl:variable name="to-table-name">
    <xsl:call-template name="config.db-table-name.document">
      <xsl:with-param name="doctype" select="$to-doctype-name"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text> int DEFAULT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$to-table-name"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.lid">
  <xsl:value-of select="$db-notions.column-name.lid"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.ref_type">
  <xsl:value-of select="$db-notions.column-name.ref_type"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.ref_types"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFAULT 0</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.ann_type">
  <xsl:value-of select="$db-notions.column-name.ann_type"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.ann_types"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.ann-attribute">
  <xsl:param name="db-column-name" select="@db-column-name"/>
  <xsl:param name="sql-datatype" select="@sql-datatype"/>
  <xsl:param name="sql-default" select="''"/>
  <xsl:param name="allowed-ann-types"/>
  <xsl:value-of select="$db-column-name"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$sql-datatype"/>
  <xsl:if test="$sql-default">
    <xsl:text> DEFAULT </xsl:text>
    <xsl:value-of select="$sql-default"/>
  </xsl:if>
  <xsl:if test="$allowed-ann-types/*">
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:text>CHECK (</xsl:text>
    <xsl:value-of select="$db-column-name"/>
    <xsl:text> IS NULL OR </xsl:text>
    <xsl:value-of select="$db-notions.column-name.ann_type"/>
    <xsl:text> IN (</xsl:text>
    <xsl:for-each select="$allowed-ann-types/ann-type">
        <xsl:if test="position()&gt;1">
          <xsl:text>,</xsl:text>
        </xsl:if>
        <xsl:call-template name="config.annotation-type.code">
          <xsl:with-param name="name" select="./@name"/>
        </xsl:call-template>
    </xsl:for-each>
    <xsl:text>))</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template name="db-core.column.document">
   <xsl:param name="doctype" select="@name"/>
  <xsl:variable name="document-table-name">
    <xsl:call-template name="config.db-table-name.document">
      <xsl:with-param name="doctype" select="$doctype"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.document"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$document-table-name"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.pseudo_document">
   <xsl:param name="pseudo-doctype" select="@name"/>
   <xsl:param name="table-name"/>
  <xsl:variable name="pseudo-document-table-name">
    <xsl:choose>
      <xsl:when test="$table-name">
        <xsl:value-of select="$table-name"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="config.db-table-name.document">
          <xsl:with-param name="doctype" select="$pseudo-doctype"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.pseudo_document"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$pseudo-document-table-name"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.generic_document">
  <xsl:param name="generic-counterpart">
    <xsl:call-template name="config.document-type.generic-counterpart"/>
  </xsl:param>
  <xsl:variable name="generic-document-table-name">
    <xsl:call-template name="config.db-table-name.document">
      <xsl:with-param name="doctype" select="$generic-counterpart"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.generic_document"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$generic-document-table-name"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.author">
  <xsl:value-of select="$db-notions.column-name.author"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.member">
  <xsl:value-of select="$db-notions.column-name.member"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.to-source">
  <xsl:param name="format"/>
  <xsl:value-of select="$db-notions.column-name.source"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:choose>
    <xsl:when test="$format='text'">
      <xsl:value-of select="$db-notions.table-name.text_sources"/>
    </xsl:when>
    <xsl:when test="$format='binary'">
      <xsl:value-of select="$db-notions.table-name.binary_sources"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="config.error">
        <xsl:with-param name="message">
          <xsl:text>db-core.column.to-source: Unknown source format: </xsl:text>
          <xsl:value-of select="$format"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.theme">
  <xsl:value-of select="$db-notions.column-name.theme"/>
  <xsl:text> int NOT NULL DEFAULT </xsl:text><xsl:value-of select="$db-core.default-theme.id"/><xsl:text> REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.themes"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.language">
  <xsl:value-of select="$db-notions.column-name.language"/>
  <xsl:text> int NOT NULL DEFAULT </xsl:text><xsl:value-of select="$db-core.default-language.id"/><xsl:text> REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.languages"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.type">
  <xsl:value-of select="$db-notions.column-name.type"/>
  <xsl:text> varchar(80) NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.subtype">
  <xsl:value-of select="$db-notions.column-name.subtype"/>
  <xsl:text> varchar(80) NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.final">
  <xsl:value-of select="$db-notions.column-name.final"/>
  <xsl:text> boolean NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.element">
  <xsl:value-of select="$db-notions.column-name.element"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.elements"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.subelement">
  <xsl:value-of select="$db-notions.column-name.subelement"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.subelements"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.course">
  <xsl:param name="not-null" select='no'/>
  <xsl:param name="is-reference" select='yes'/>
  <xsl:variable name="null-spec">
    <xsl:choose>
      <xsl:when test="$not-null='yes'">
        <xsl:value-of select="'NOT NULL'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'DEFAULT NULL'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="references">
    <xsl:choose>
      <xsl:when test="$is-reference='false'">
        <xsl:value-of select="''"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> REFERENCES </xsl:text>
        <xsl:call-template name="config.db-table-name.document">
          <xsl:with-param name="doctype" select="'course'"/>
        </xsl:call-template>
        <xsl:text>(</xsl:text>
        <xsl:value-of select="$db-notions.column-name.id"/>
        <xsl:text>)</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.course"/>
  <xsl:text> int </xsl:text>
  <xsl:value-of select="$null-spec"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$references"/>
</xsl:template>

<xsl:template name="db-core.column.worksheet">
  <xsl:value-of select="$db-notions.column-name.worksheet"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.worksheets"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.group-admin">
  <xsl:value-of select="$db-notions.column-name.admin"/>
  <xsl:text> boolean NOT NULL DEFAULT FALSE</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.user_group">
  <xsl:value-of select="$db-notions.column-name.user_group"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_groups"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.section">
  <xsl:value-of select="$db-notions.column-name.section"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.may-create">
  <xsl:param name="doctype-name"/>
  <xsl:call-template name="config.db-column-name.may-create-document">
    <xsl:with-param name="doctype-name" select="$doctype-name"/>
  </xsl:call-template>
<!-- 
workaround! this fixes the following behaviour (bug or feature?!:
new usergroups have the permission to create documents of type $doctype-name
if (todo:) not otherly specified in Master Document of the usergroup
-->
  <xsl:text> boolean NOT NULL DEFAULT TRUE</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.originator">
  <xsl:param name="originator"/>
  <xsl:choose>
    <xsl:when test="$originator='user'">
      <xsl:value-of select="$db-notions.column-name.the_user"/>
      <xsl:text> int NOT NULL REFERENCES </xsl:text>
      <xsl:value-of select="$db-notions.table-name.users"/>
    </xsl:when>
    <xsl:when test="$originator='user_group'">
      <xsl:value-of select="$db-notions.column-name.user_group"/>
      <xsl:text> int NOT NULL REFERENCES </xsl:text>
      <xsl:value-of select="$db-notions.table-name.user_groups"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        <xsl:text>db-core.column.originator: Unknown originator: </xsl:text>
        <xsl:value-of select="$originator"/>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFAULT 0</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.annotated-document">
  <xsl:param name="doctype"/>
  <xsl:param name="order"/>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="concat('doc',$order)"/>
  </xsl:call-template>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:call-template name="config.db-table-name.document">
    <xsl:with-param name="doctype" select="$doctype"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.doc">
  <xsl:param name="doctype"/>
  <xsl:value-of select="$db-notions.column-name.doc"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:call-template name="config.db-table-name.document">
    <xsl:with-param name="doctype" select="$doctype"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.ref">
  <xsl:param name="first-doctype"/>
  <xsl:param name="second-doctype"/>
  <xsl:value-of select="$db-notions.column-name.ref"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="$first-doctype"/>
    <xsl:with-param name="to-doctype" select="$second-doctype"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.annotations-content">
  <xsl:value-of select="$db-notions.column-name.content"/>
  <xsl:text> text NOT NULL DEFAULT '</xsl:text>
  <xsl:value-of select="$db-core.annotations.default-xml-content"/>
  <xsl:text>'</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.sync_id">
  <xsl:param name="not-null" select="'no'"/>
  <xsl:param name="unique" select="'yes'"/>
  <xsl:variable name="null-spec">
    <xsl:choose>
      <xsl:when test="$not-null='yes'">
        <xsl:value-of select="'NOT NULL'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'DEFAULT NULL'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="unique-spec">
    <xsl:choose>
      <xsl:when test="$unique='yes'">
        <xsl:value-of select="'UNIQUE'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="''"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.sync_id"/>
  <xsl:text> varchar(</xsl:text>
  <xsl:value-of select="$db-notions.constant-value.sync_id_length"/>
  <xsl:text>) </xsl:text>
  <xsl:value-of select="$unique-spec"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$null-spec"/>
</xsl:template>

<xsl:template name="db-core.column.tutor">
  <xsl:value-of select="$db-notions.column-name.tutor"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.class">
  <xsl:param name="is-reference" select='yes'/>
  <xsl:param name="not-null" select='yes'/>
  <xsl:variable name="references">
    <xsl:choose>
      <xsl:when test="$is-reference='false'">
        <xsl:value-of select="''"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text> REFERENCES </xsl:text>
        <xsl:value-of select="$db-notions.table-name.classes"/>
        <xsl:text>(</xsl:text>
        <xsl:value-of select="$db-notions.column-name.id"/>
        <xsl:text>)</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:value-of select="$db-notions.column-name.class"/>
  <xsl:text> int</xsl:text>
  <xsl:if test="$not-null='yes'">
    <xsl:text> NOT NULL</xsl:text>
  </xsl:if>
  <xsl:value-of select="$references"/>
</xsl:template>

<xsl:template name="db-core.column.capacity">
  <xsl:value-of select="$db-notions.column-name.capacity"/>
  <xsl:text> int CHECK (</xsl:text>
  <xsl:value-of select="$db-notions.column-name.capacity"/>
  <xsl:text> &gt;= 0)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.lecturer">
  <xsl:value-of select="$db-notions.column-name.lecturer"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.tutorial">
  <xsl:value-of select="$db-notions.column-name.tutorial"/>
  <xsl:text> int NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.code">
  <xsl:value-of select="$db-notions.column-name.code"/>
  <xsl:text> varchar(4) UNIQUE NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.sync_home">
  <xsl:value-of select="$db-notions.column-name.sync_home"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.is_wrapper">
  <xsl:value-of select="$db-notions.column-name.is_wrapper"/>
  <xsl:text> boolean NOT NULL DEFAULT false</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating comments on tables
   =====================================================================================
-->

<xsl:template name="db-core.comment-on-table">
  <xsl:param name="name" select="@name"/>
  <xsl:variable name="description">
    <xsl:call-template name="config.description">
      <xsl:with-param name="node" select="/*/db-tables/db-table[$name=@name]"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:if test="$description">
    <xsl:text>&br;</xsl:text>
    <xsl:text>COMMENT ON TABLE </xsl:text>
    <xsl:call-template name="config.db-table.name">
      <xsl:with-param name="name" select="$name"/>
    </xsl:call-template>
    <xsl:text> IS</xsl:text>
    <xsl:text>&br;&sp;</xsl:text>
    <xsl:value-of select="x-sql:toSQL(string($description))"/>
    <xsl:text>;&br;</xsl:text>
  </xsl:if>
</xsl:template>

<!--
   =====================================================================================
   Creating protection RULES for core Pseudo-Documents
   =====================================================================================
-->

<!--
<xsl:template name="db-core.init.protect.sections">
  <xsl:text>CREATE RULE </xsl:text>
  <xsl:value-of select="$db-core.root-section-insert-rule-name"/>
  <xsl:text> AS ON INSERT TO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;&sp;WHERE new.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> = 0&br;</xsl:text>
  <xsl:text>&sp;DO INSTEAD NOTHING;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>COMMENT ON RULE </xsl:text>
  <xsl:value-of select="$db-core.root-section-insert-rule-name"/>
  <xsl:text> IS&br;</xsl:text>
  <xsl:text>&sp;'Ensures the root section is unique';&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>CREATE RULE </xsl:text>
  <xsl:value-of select="$db-core.root-section-update-rule-name"/>
  <xsl:text> AS ON UPDATE TO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;&sp;WHERE old.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> = 0&br;</xsl:text>
  <xsl:text>&sp;DO INSTEAD NOTHING;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>COMMENT ON RULE </xsl:text>
  <xsl:value-of select="$db-core.root-section-update-rule-name"/>
  <xsl:text> IS&br;</xsl:text>
  <xsl:text>&sp;'Ensures the root section is not changed';&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>CREATE RULE </xsl:text>
  <xsl:value-of select="$db-core.root-section-delete-rule-name"/>
  <xsl:text> AS ON DELETE TO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;&sp;WHERE old.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> = 0&br;</xsl:text>
  <xsl:text>&sp;DO INSTEAD NOTHING;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>COMMENT ON RULE </xsl:text>
  <xsl:value-of select="$db-core.root-section-delete-rule-name"/>
  <xsl:text> IS&br;</xsl:text>
  <xsl:text>&sp;'Ensures the root section is not deleteted';&br;</xsl:text>
</xsl:template>

-->
<!--
   =====================================================================================
   Creating and initializing sections table
   =====================================================================================
-->

<xsl:template name="db-core.sections-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.sections)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.first_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.custom_metainfo"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$db-notions.table-name.sections"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="db-core.init-table.set-sequence">
  <xsl:param name="table-name"/>
  <xsl:text>select setval('</xsl:text><xsl:value-of select="$table-name"/><xsl:text>_id_seq',max(id)+1) from </xsl:text><xsl:value-of select="$table-name"/><xsl:text>;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating and initializing users table
   =====================================================================================
-->

<xsl:template name="db-core.users-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.users)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.sync_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.login_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.password"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.theme"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.language"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.first_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.surname"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.email"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_login"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.custom_metainfo"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.sync_home"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$db-notions.table-name.users"/>
  </xsl:call-template>
<!--DEPRECATED-->
  <xsl:text>&br;</xsl:text>

</xsl:template>


<!--
   =====================================================================================
   Creating and initializing user_groups table
   =====================================================================================
-->

<xsl:template name="db-core.user_groups-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.user_groups)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_groups"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name">
    <xsl:with-param name="unique" select="'yes'"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="doctype-name" select="@name"/>
    <xsl:call-template name="db-core.column.may-create">
      <xsl:with-param name="doctype-name" select="$doctype-name"/>
    </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  </xsl:for-each>
  <xsl:call-template name="db-core.column.custom_metainfo"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$db-notions.table-name.user_groups"/>
  </xsl:call-template>
</xsl:template>

<!--
   =====================================================================================
   Creating user_group members table
   =====================================================================================
-->

<xsl:template name="db-core.user_group_members-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.user_group_members)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_group_members"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.member"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.user_group"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>UNIQUE (</xsl:text>
  <xsl:value-of select="$db-notions.column-name.member"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$db-notions.column-name.user_group"/>
  <xsl:text>)</xsl:text>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.group-admin"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'user_group_members'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating and initializing themes table
   =====================================================================================
-->

<xsl:template name="db-core.themes-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.themes)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.themes"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$db-notions.table-name.themes"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating and initializing languages table
   =====================================================================================
-->

<xsl:template name="db-core.languages-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.languages)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.languages"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.code"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$db-notions.table-name.languages"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>

</xsl:template>

<!--
   =====================================================================================
   Creating and initializing categories table
   =====================================================================================
-->

<xsl:template name="db-core.categories-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.categories)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.categories"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id">
    <xsl:with-param name="type" select="'int'"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'categories'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/categories/category">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$db-notions.table-name.categories"/>
    <xsl:text>&br;&sp;VALUES (</xsl:text>
    <xsl:call-template name="config.category.code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating and initializing reference types table
   =====================================================================================
-->

<xsl:template name="db-core.reference-types-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.ref_types)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.ref_types"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'ref_types'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/reference-types/reference-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$db-notions.table-name.ref_types"/>
    <xsl:text>&br;&sp;VALUES (</xsl:text>
    <xsl:call-template name="config.reference-type.code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating and initializing annotation types table
   =====================================================================================
-->

<xsl:template name="db-core.annotation-types-table">
  <xsl:variable name="table-name" select="$db-notions.table-name.ann_types"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$table-name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$table-name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$table-name"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/annotation-types/annotation-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$table-name"/>
    <xsl:text>&br;&sp;VALUES (</xsl:text>
    <xsl:call-template name="config.annotation-type.code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating and initializing media types table
   =====================================================================================
-->

<xsl:template name="db-core.media-types-table">
  <xsl:variable name="this.table-name" select="$db-notions.table-name.media_types"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$this.table-name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$this.table-name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id">
    <xsl:with-param name="type" select="'int'"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.type"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.subtype"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$this.table-name"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/media-types/media-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$this.table-name"/>
    <xsl:text>&br;&sp;VALUES (</xsl:text>
    <xsl:call-template name="config.media-type.code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@type"/>
    <xsl:text>', '</xsl:text>
    <xsl:value-of select="@subtype"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating and initializing semesters table
   =====================================================================================
-->

<xsl:template name="db-core.semesters-table">
  <xsl:variable name="this.table-name" select="$db-notions.table-name.semesters"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$this.table-name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$this.table-name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.sync_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$this.table-name"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating classes table
   =====================================================================================
-->

<xsl:template name="db-core.classes-table">
  <xsl:variable name="name">
    <xsl:value-of select="$db-notions.table-name.classes"/>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.sync_id">
<!-- temporarily (for online-checkin) unset NOT NULL constraint
    <xsl:with-param name="not-null" select="'yes'"/>
-->
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.semester"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.theme"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  
</xsl:template>

<!--
   =====================================================================================
   Creating tutorials table
   =====================================================================================
-->

<xsl:template name="db-core.tutorials-table">
  <xsl:variable name="name">
    <xsl:value-of select="$db-notions.table-name.tutorials"/>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.sync_id">
    <xsl:with-param name="not-null" select="yes"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.tutor"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.class"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.capacity"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.hide"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$db-notions.table-name.tutorials"/>
  </xsl:call-template>
  
</xsl:template>

<!--
   =====================================================================================
   Creating version control thread tables
   =====================================================================================
-->

<xsl:template name="db-core.vc-threads-table">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="doctype" select="@name"/>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="$is-generic='no'">
      <xsl:variable name="table-name">
        <xsl:call-template name="config.db-table-name.vc_threads">
          <xsl:with-param name="doctype" select="$doctype"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating table ',$table-name)"/>
      </xsl:call-template>
      <xsl:text>CREATE TABLE </xsl:text>
      <xsl:value-of select="$table-name"/>
      <xsl:text>&br;&sp;(&br;</xsl:text>
      <xsl:text>&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.id"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.name"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.description"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.created"/>
      <xsl:text>&br;&sp;);&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating document tables
   =====================================================================================
-->

<xsl:template name="db-core.document-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="name">
      <xsl:call-template name="config.db-table-name.document"/>
    </xsl:variable>
    <xsl:variable name="description">
      <xsl:call-template name="config.description"/>
      <xsl:value-of select="'s'"/>
    </xsl:variable>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:call-template name="db-core.echo">
          <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
        </xsl:call-template>
        <xsl:variable name="vc-threads-table-name">
          <xsl:call-template name="config.db-table-name.vc_threads">
            <xsl:with-param name="doctype" select="@name"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:text>CREATE TABLE </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>&br;&sp;(&br;</xsl:text>
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.id"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.vc_thread">
          <xsl:with-param name="doctype" select="@name"/>
        </xsl:call-template>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.version"/>
        <xsl:text>,&br;&sp;&sp;UNIQUE (</xsl:text>
        <xsl:value-of select="$db-notions.column-name.vc_thread"/>
        <xsl:text>,</xsl:text>
        <xsl:value-of select="$db-notions.column-name.version"/>
        <xsl:text>)</xsl:text>
        <xsl:if test="@has-category='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.category"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.pure_name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.description"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.info_page"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.thumbnail"/>
        <xsl:if test="@has-qualified-name='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.qualified-name"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.copyright"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.created"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.deleted"/>
        <xsl:if test="@may-be-wrapper='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.is_wrapper"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.hide"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.last_modified"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.contained_in.deferrable"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.custom_metainfo"/>
        <xsl:if test="@has-width-and-height='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.width"/>
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.height"/>
        </xsl:if>
        <xsl:if test="@has-duration='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.duration"/>
        </xsl:if>
        <xsl:if test="@has-class='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.class">
            <xsl:with-param name="not-null" select="'no'"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="@has-timeframe='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.timeframe_start"/>
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.timeframe_end"/>
        </xsl:if>
        <xsl:variable name="has-corrector">
          <xsl:call-template name="config.document-type.has-corrector"/>
        </xsl:variable>
        <xsl:if test="$has-corrector='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.corrector.deferrable"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:variable name="has-corrector.element" select="has-corrector"/>
        <xsl:if test="$has-corrector.element">
          <xsl:call-template name="db-core.constraint.corrector-category">
            <xsl:with-param name="corrector-spec" select="$has-corrector.element"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:call-template name="db-core.column.content"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.content_type"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.content_length"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.source"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.source_type"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.source_length"/>
        <xsl:if test="@has-summary='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.summary"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.changelog"/>
        <xsl:text>&br;&sp;);&br;</xsl:text>
        <xsl:text>&br;</xsl:text>
        <xsl:text>CREATE RULE </xsl:text>
        <xsl:call-template name="config.db-rule-name.new-vc-thread"/>
        <xsl:text> AS ON INSERT TO </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>&br;&sp;WHERE new.</xsl:text>
        <xsl:value-of select="$db-notions.column-name.vc_thread"/>
        <xsl:text> = 0 DO&br;</xsl:text>
        <xsl:text>&sp;&sp;(&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;INSERT INTO </xsl:text>
        <xsl:value-of select="$vc-threads-table-name"/>
        <xsl:text> (</xsl:text>
        <xsl:value-of select="$db-notions.column-name.name"/>
        <xsl:text>) VALUES (new.</xsl:text>
        <xsl:value-of select="$db-notions.column-name.name"/>
        <xsl:text>);&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;UPDATE </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> SET </xsl:text>
        <xsl:value-of select="$db-notions.column-name.vc_thread"/>
        <xsl:text> = currval('</xsl:text>
        <xsl:call-template name="config.db-sequence-name.for-serial">
          <xsl:with-param name="table-name" select="$vc-threads-table-name"/>
          <xsl:with-param name="column-name" select="$db-notions.column-name.id"/>
        </xsl:call-template>
        <xsl:text>') WHERE </xsl:text>
        <xsl:value-of select="$db-notions.column-name.vc_thread"/>
        <xsl:text> = 0;&br;</xsl:text>
        <xsl:text>&sp;&sp;);&br;</xsl:text>
      </xsl:when>
      <xsl:when test="$is-generic='yes'">
        <xsl:text>CREATE TABLE </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>&br;&sp;(&br;</xsl:text>
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.id"/>
        <xsl:if test="@has-category='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.category"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.pure_name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.description"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.created"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.deleted"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.hide"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.last_modified"/>
        <xsl:if test="@has-qualified-name='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.qualified-name"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.contained_in.deferrable"/>
        <xsl:if test="@has-width-and-height='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.width"/>
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.height"/>
        </xsl:if>
        <xsl:if test="@has-duration='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.duration"/>
        </xsl:if>
        <xsl:if test="@has-thumbnail='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.thumbnail"/>
        </xsl:if>
        <xsl:variable name="has-corrector">
          <xsl:call-template name="config.document-type.has-corrector"/>
        </xsl:variable>
        <xsl:if test="$has-corrector='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.corrector.deferrable"/>
        </xsl:if>
        <xsl:text>&br;&sp;);&br;</xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:text>&br;</xsl:text>
    <xsl:text>COMMENT ON TABLE </xsl:text>
    <xsl:value-of select="$name"/>
    <xsl:text> IS </xsl:text>
    <xsl:value-of select="x-sql:toSQL(string($description))"/>
    <xsl:text>;&br;</xsl:text>

 </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating reference tables
   =====================================================================================
-->

<xsl:template name="db-core.refs-document-document-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="from-doctype" select="@name"/>
    <xsl:variable name="from-doctype-node" select="."/>
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:for-each select="/*/document-types/document-type">
      <xsl:variable name="ref-exists">
        <xsl:call-template name="config.document-type.pair.refs-to">
          <xsl:with-param name="from-doctype-node" select="$from-doctype-node"/>
          <xsl:with-param name="to-doctype" select="@name"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:if test="$ref-exists='yes'">
        
        <xsl:if test="position()&gt;1">
          <xsl:text>&br;</xsl:text>
        </xsl:if>
        <xsl:variable name="name">
          <xsl:call-template name="config.db-table-name.refs-document-document">
            <xsl:with-param name="from-doctype" select="$from-doctype"/>
            <xsl:with-param name="to-doctype" select="@name"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:call-template name="db-core.echo">
          <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
        </xsl:call-template>
        <xsl:text>CREATE TABLE </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>&br;&sp;(&br;</xsl:text>
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.id"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.from_doc">
	  <xsl:with-param name="from-doctype-name" select="$from-doctype"/>
	</xsl:call-template>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.to_doc"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.lid"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.ref_type"/>
        <xsl:text>&br;</xsl:text>
        <xsl:text>&sp;);&br;</xsl:text>
        
        <!-- modifyParents  -->
        <!-- create function mod_refs_x_y_func()  -->
        <xsl:variable name="trigger-name">
          <xsl:value-of select="concat('mod_',$name)"/>
        </xsl:variable>
        <xsl:call-template name="db-core.echo">
          <xsl:with-param name="message" select="concat('Creating trigger ',$trigger-name)"/>
        </xsl:call-template>
        <xsl:text>&sp;&sp;CREATE FUNCTION </xsl:text>
        <xsl:value-of select="concat($trigger-name,'_func()')"/>
        <xsl:text>&br;</xsl:text>
        <xsl:text>&sp;&sp;RETURNS trigger&br;</xsl:text>
        <xsl:text>&sp;&sp;AS '&br;</xsl:text>
        <xsl:text>&sp;&sp;BEGIN&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;UPDATE </xsl:text>
        <xsl:call-template name="config.db-table-name.document">
          <xsl:with-param name="doctype" select="$from-doctype"/>
        </xsl:call-template>
        <xsl:text>&br;&sp;&sp;&sp;SET </xsl:text>
        <xsl:value-of select="$db-notions.column-name.last_modified"/>
        <xsl:text> = (&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;&sp;SELECT </xsl:text>
        <xsl:value-of select="$db-notions.column-name.last_modified"/>
        <xsl:text> FROM </xsl:text>
        <xsl:call-template name="config.db-table-name.document">
          <xsl:with-param name="doctype" select="@name"/>
        </xsl:call-template>
        <xsl:text> WHERE </xsl:text>
        <xsl:value-of select="$db-notions.column-name.id"/>
        <xsl:text> = new.</xsl:text>
        <xsl:value-of select="$db-notions.column-name.to_doc"/>
        <xsl:text>)&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;WHERE </xsl:text>
        <xsl:value-of select="$db-notions.column-name.id"/>
        <xsl:text> = new.</xsl:text>
        <xsl:value-of select="$db-notions.column-name.from_doc"/>
        <xsl:text>;&br;</xsl:text>
        <xsl:text>&sp;&sp;RETURN new;&br; </xsl:text>
        <xsl:text>&sp;&sp;END;'&br; </xsl:text>
        <xsl:text>&sp;&sp;LANGUAGE 'plpgsql';&br;&br;</xsl:text>
        
        <!-- create trigger mod_refs_x_y  -->
        <xsl:text>&sp;&sp;CREATE TRIGGER </xsl:text>
        <xsl:value-of select="$trigger-name"/>
        <xsl:text> AFTER UPDATE ON </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>&br;</xsl:text>
        <xsl:text>&sp;&sp;FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
        <xsl:value-of select="concat($trigger-name,'_func()')"/>
        <xsl:text>;&br;&br;</xsl:text>
        
      </xsl:if>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating triggers modify-parents
   =====================================================================================
-->

<xsl:template name="db-core.modify-parents">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="name">
      <xsl:call-template name="config.db-table-name.document"/>
    </xsl:variable>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:variable name="to-doctype" select="@name"/>
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>

<!-- modifyParents  -->
    <xsl:variable name="is-recursive-component">
      <xsl:call-template name="config.document-type.is-recursive-component"/>
    </xsl:variable>
    <xsl:if test="$is-recursive-component='yes'">
      <xsl:text>&br;</xsl:text>

<!-- create function mod_x_parent_func()  -->
      <xsl:variable name="trigger-name">
        <xsl:value-of select="concat(concat('mod_',$to-doctype),'_parents')"/>
      </xsl:variable>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating trigger ',$trigger-name)"/>
      </xsl:call-template>
      <xsl:text>&sp;&sp;CREATE FUNCTION </xsl:text>
      <xsl:value-of select="concat($trigger-name,'_func()')"/>
      <xsl:text>&br;&sp;&sp;RETURNS trigger&br;</xsl:text>
      <xsl:text>&sp;&sp;AS '&br;</xsl:text>
      <xsl:text>&sp;&sp;BEGIN&br;</xsl:text>
      <xsl:for-each select="/*/document-types/document-type">
        <xsl:variable name="from-doctype-node" select="."/>
        <xsl:variable name="from-doctype" select="@name"/>
        <xsl:variable name="ref-exists">
          <xsl:call-template name="config.document-type.pair.refs-to">
            <xsl:with-param name="from-doctype-node" select="$from-doctype-node"/>
            <xsl:with-param name="to-doctype" select="$to-doctype"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:if test="$ref-exists='yes'">
          <xsl:text>&sp;&sp;&sp;UPDATE </xsl:text>
          <xsl:call-template name="config.db-table-name.document"/>
          <xsl:text> SET </xsl:text>
          <xsl:value-of select="$db-notions.column-name.last_modified"/>
          <xsl:text> = new.</xsl:text>
          <xsl:value-of select="$db-notions.column-name.last_modified"/>
          <xsl:text> WHERE </xsl:text>
          <xsl:value-of select="$db-notions.column-name.id"/>
          <xsl:text> IN (&br;</xsl:text>
          <xsl:text>&sp;&sp;&sp;&sp;SELECT </xsl:text>
          <xsl:value-of select="$db-notions.column-name.from_doc"/>
          <xsl:text> FROM </xsl:text>
          <xsl:call-template name="config.db-table-name.refs-document-document">
            <xsl:with-param name="from-doctype" select="$from-doctype"/>
            <xsl:with-param name="to-doctype" select="$to-doctype"/>
          </xsl:call-template>
          <xsl:text>&br;&sp;&sp;&sp;&sp;WHERE </xsl:text>
          <xsl:value-of select="$db-notions.column-name.to_doc"/>
          <xsl:text> = new.</xsl:text>
          <xsl:value-of select="$db-notions.column-name.id"/>
          <!-- ??
          <xsl:text>&br;&sp;&sp;&sp;&sp;AND </xsl:text>
          <xsl:value-of select="$db-notions.column-name.ref_type"/>
          <xsl:text> IN (0,1)</xsl:text>
          -->
          <xsl:text>);&br;</xsl:text>
        </xsl:if>
      </xsl:for-each>
      <xsl:text>&sp;&sp;RETURN new;&br;</xsl:text>
      <xsl:text>&sp;&sp;END;'&br;</xsl:text>
      <xsl:text>&sp;&sp;LANGUAGE 'plpgsql';&br;&br;</xsl:text>

<!-- create trigger mod_refs_x_y  -->
      <xsl:text>&sp;&sp;CREATE TRIGGER </xsl:text>
      <xsl:value-of select="$trigger-name"/>
      <xsl:text> AFTER UPDATE ON </xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>&br;</xsl:text>
      <xsl:text>&sp;&sp;FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
      <xsl:value-of select="concat($trigger-name,'_func()')"/>
      <xsl:text>;&br;&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating triggers modify-parent-section
   =====================================================================================
-->

<xsl:template name="db-core.modify-parent-section">
  <xsl:param name="on-operation"/>
  <!-- create function mod_parent_section_func()  -->
  <xsl:variable name="func-name">
    <xsl:value-of select="concat(concat('mod_parent_section_on_',$on-operation),'_func()')"/>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating function ',$func-name)"/>
  </xsl:call-template>
  <xsl:text>&sp;&sp;CREATE FUNCTION </xsl:text>
  <xsl:value-of select="$func-name"/>
  <xsl:text>&br;&sp;&sp;RETURNS trigger&br;</xsl:text>
  <xsl:text>&sp;&sp;AS '&br;</xsl:text>
  <xsl:text>&sp;&sp;BEGIN&br;</xsl:text>
  <xsl:choose>
    <xsl:when test="$on-operation='UPDATE'">
      <xsl:text>&sp;&sp;&sp;IF OLD.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.contained_in"/>
      <xsl:text> IS NULL THEN&br;&sp;</xsl:text>
    </xsl:when>
    <xsl:otherwise/>
  </xsl:choose>
  <xsl:text>&sp;&sp;&sp;UPDATE </xsl:text>
  <xsl:call-template name="config.db-table-name.pseudo-document">
    <xsl:with-param name="pseudodoc-type" select="'section'"/>
  </xsl:call-template>
  <xsl:text> SET </xsl:text>
  <xsl:value-of select="$db-notions.column-name.last_modified"/>
  <xsl:text> = CURRENT_TIMESTAMP</xsl:text>
  <xsl:text> WHERE </xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:choose>
    <xsl:when test="$on-operation='DELETE'">
      <xsl:text> = old.</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text> = new.</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:choose>
    <xsl:when test="$on-operation='UPDATE'">
      <xsl:text>&sp;&sp;&sp;ELSIF OLD.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.contained_in"/>
      <xsl:text> != NEW.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.contained_in"/>
      <xsl:text> THEN&br;</xsl:text>
      <xsl:text>&sp;&sp;&sp;&sp;UPDATE </xsl:text>
      <xsl:call-template name="config.db-table-name.pseudo-document">
        <xsl:with-param name="pseudodoc-type" select="'section'"/>
      </xsl:call-template>
      <xsl:text> SET </xsl:text>
      <xsl:value-of select="$db-notions.column-name.last_modified"/>
      <xsl:text> = CURRENT_TIMESTAMP</xsl:text>
      <xsl:text> WHERE </xsl:text>
      <xsl:value-of select="$db-notions.column-name.id"/>
      <xsl:text> IN (new.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.contained_in"/>
      <xsl:text>,old.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.contained_in"/>
      <xsl:text>);&br;</xsl:text>
      <xsl:text>&sp;&sp;&sp;END IF;&br;</xsl:text>
    </xsl:when>
    <xsl:otherwise/>
  </xsl:choose>
  <xsl:text>&sp;&sp;RETURN new;&br;</xsl:text>
  <xsl:text>&sp;&sp;END;'&br;</xsl:text>
  <xsl:text>&sp;&sp;LANGUAGE 'plpgsql';&br;&br;</xsl:text>
  
  <!-- documents -->
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="'Creating triggers for documents'"/>
  </xsl:call-template>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="doctype" select="@name"/>
    <xsl:variable name="table-name">
      <xsl:call-template name="config.db-table-name.document"/>
    </xsl:variable>
    <xsl:variable name="trigger-name">
      <xsl:value-of select="concat(concat(concat('mod_',$doctype),'_parent_section_on_'),$on-operation)"/>
    </xsl:variable>
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <!-- create trigger mod_x_parent_section  -->
    <xsl:text>&sp;&sp;CREATE TRIGGER </xsl:text>
    <xsl:value-of select="$trigger-name"/>
    <xsl:text> AFTER </xsl:text>
    <xsl:value-of select="$on-operation"/>
    <xsl:text> ON </xsl:text>
    <xsl:value-of select="$table-name"/>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;&sp;FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
    <xsl:value-of select="$func-name"/>
    <xsl:text>;&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&br;</xsl:text>

  <!-- pseudo-documents -->
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="'Creating triggers for pseudo-documents'"/>
  </xsl:call-template>
  <xsl:for-each select="/*/pseudodoc-types/pseudodoc-type">
    <xsl:variable name="pseudo-doctype" select="@name"/>
    <xsl:variable name="table-name">
      <xsl:call-template name="config.db-table-name.pseudo-document"/>
    </xsl:variable>
    <xsl:variable name="trigger-name">
      <xsl:value-of select="concat(concat(concat('mod_',$pseudo-doctype),'_parent_section_'),$on-operation)"/>
    </xsl:variable>
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <!-- create trigger mod_x_parent_section  -->
    <xsl:text>&sp;&sp;CREATE TRIGGER </xsl:text>
    <xsl:value-of select="$trigger-name"/>
    <xsl:text> AFTER </xsl:text>
    <xsl:value-of select="$on-operation"/>
    <xsl:text> ON </xsl:text>
    <xsl:value-of select="$table-name"/>
    <xsl:text>&br;</xsl:text>
    <xsl:text>&sp;&sp;FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
    <xsl:value-of select="$func-name"/>
    <xsl:text>;&br;</xsl:text>
  </xsl:for-each>
  <xsl:text>&br;</xsl:text>

</xsl:template>

<!--
   =====================================================================================
   Creating triggers set_last_modified
   =====================================================================================
-->

<xsl:template name="db-code.set-last-modified">
  <xsl:text>CREATE FUNCTION </xsl:text>
  <xsl:value-of select="$config.db-procedure-name.set-last-modified"/>
  <xsl:text>()RETURNS trigger&br;</xsl:text>
  <xsl:text>LANGUAGE plpgsql&br;</xsl:text>
  <xsl:text>AS&br;</xsl:text>
  <xsl:text>'BEGIN&br;</xsl:text>
  <xsl:text>  IF NEW.last_modified = OLD.last_modified THEN&br;</xsl:text>
  <xsl:text>    NEW.last_modified := CURRENT_TIMESTAMP;&br;</xsl:text>
  <xsl:text>  END IF;&br;</xsl:text>
  <xsl:text>  RETURN NEW;&br;</xsl:text>
  <xsl:text>END;';&br;</xsl:text>
  <xsl:for-each select="/*/document-types/document-type|/*/pseudodoc-types/pseudodoc-type">
    <xsl:variable name="table-name">
      <xsl:choose>
        <xsl:when test="local-name() = 'document-type'">
          <xsl:call-template name="config.db-table-name.document"/>
        </xsl:when>
        <xsl:when test="local-name() = 'pseudodoc-type'">
          <xsl:call-template name="config.db-table-name.pseudo-document"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:text>&br;</xsl:text>
    <xsl:text>CREATE TRIGGER </xsl:text>
    <xsl:call-template name="config.db-trigger-name.set-last-modified"/>
    <xsl:text> BEFORE UPDATE ON </xsl:text>
    <xsl:value-of select="$table-name"/>
    <xsl:text>&br;FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
    <xsl:value-of select="$config.db-procedure-name.set-last-modified"/>
    <xsl:text>();&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating reference constraints
   =====================================================================================
-->

<!--   Wrapper template catching attributes "direction", "modulo-vc-thread" and "per-ref-type" -->

<xsl:template name="db-core.reference-constraints">
  <xsl:for-each select="/*/reference-constraints/reference-constraint">

<!--   catching attribute "direction"   -->
    <xsl:variable name="direction" select="@direction"/>
    <xsl:choose>
      <xsl:when test="$direction='from' or $direction='to'">

<!--   catching attribute "modulo-vc-thread"   -->
        <xsl:variable name="mod-vc" select="@modulo-vc-thread"/>
        <xsl:choose>
          <xsl:when test="$mod-vc='yes'">

<!--   catching attribute "per-ref-type"   -->
            <xsl:variable name="per-ref" select="@per-ref-type"/>
            <xsl:choose>
              <xsl:when test="$per-ref='yes'">

                <xsl:call-template name="db-core.reference-constraint.valid">
                  <xsl:with-param name="direction" select="$direction"/>
                  <xsl:with-param name="mod-vc" select="$mod-vc"/>
                  <xsl:with-param name="per-ref" select="$per-ref"/>
                  <xsl:with-param name="from-doctype" select="@from-doctype"/>
                  <xsl:with-param name="to-doctype" select="@to-doctype"/>
                  <xsl:with-param name="ref-type">
                    <xsl:call-template name="config.reference-type.code">
                      <xsl:with-param name="name" select="@reference-type"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>

              </xsl:when>
              <xsl:otherwise>
                <xsl:message terminate="yes">
                  <xsl:text>Invalid or missing attribute "per-ref-type" in reference constraint.&br;Must be "yes". Is: </xsl:text>
                  <xsl:value-of select="$per-ref"/>
                </xsl:message>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="yes">
              <xsl:text>Invalid or missing attribute "modulo-vc-thread" in reference constraint.&br;Must be "yes". Is: </xsl:text>
              <xsl:value-of select="$mod-vc"/>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          <xsl:text>Invalid or missing attribute "direction" in reference constraint.&br;Must be "from" or "to". Is: </xsl:text>
          <xsl:value-of select="$direction"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<!--   wrapped template assuming valid attribute values  -->

<xsl:template name="db-core.reference-constraint.valid">
  <xsl:param name="direction"/>
  <xsl:param name="mod-vc"/>
  <xsl:param name="per-ref"/>
  <xsl:param name="from-doctype"/>
  <xsl:param name="to-doctype"/>
  <xsl:param name="ref-type"/>
  <xsl:variable name="type-unique">
    <xsl:choose>
      <xsl:when test="$direction='from'">
        <xsl:value-of select="$to-doctype"/>
      </xsl:when>
      <xsl:when test="$direction='to'">
        <xsl:value-of select="$from-doctype"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="type-multi">
    <xsl:choose>
      <xsl:when test="$direction='from'">
        <xsl:value-of select="$from-doctype"/>
      </xsl:when>
      <xsl:when test="$direction='to'">
        <xsl:value-of select="$to-doctype"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="table.type-multi">
    <xsl:call-template name="config.db-table-name.document">
      <xsl:with-param name="doctype" select="$type-multi"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="table.refs">
    <xsl:call-template name="config.db-table-name.refs-document-document">
      <xsl:with-param name="from-doctype" select="$from-doctype"/>
      <xsl:with-param name="to-doctype" select="$to-doctype"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="reverse-direction">
    <xsl:choose>
      <xsl:when test="$direction='from'">
        <xsl:value-of select="'to'"/>
      </xsl:when>
      <xsl:when test="$direction='to'">
        <xsl:value-of select="'from'"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="func-name">
    <xsl:text>unique_</xsl:text>
    <xsl:value-of select="$direction"/>
    <xsl:text>_</xsl:text>
    <xsl:value-of select="$from-doctype"/>
    <xsl:text>_</xsl:text>
    <xsl:value-of select="$to-doctype"/>
    <xsl:text>_func</xsl:text>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating trigger function ',$func-name)"/>
  </xsl:call-template>
  <xsl:text></xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>CREATE OR REPLACE FUNCTION </xsl:text>
  <xsl:value-of select="$func-name"/>
  <xsl:text>()&br;</xsl:text>
  <xsl:text>RETURNS trigger&br;</xsl:text>
  <xsl:text>AS '&br;</xsl:text>
  <xsl:text>DECLARE&br;</xsl:text>
  <xsl:text>&sp;ref_type int;&br;</xsl:text>
  <xsl:text>&sp;tmp int;&br;</xsl:text>
  <xsl:text>&sp;vc_old int;&br;</xsl:text>
  <xsl:text>&sp;res int;&br;</xsl:text>
  <xsl:text>BEGIN&br;</xsl:text>
  <xsl:text>ref_type = TG_ARGV[0];&br;</xsl:text>
  <xsl:text>IF NEW.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'ref_type'"/>
  </xsl:call-template>
  <xsl:text> = ref_type THEN&br;</xsl:text>
  <xsl:text>&sp;SELECT INTO tmp </xsl:text>
  <xsl:value-of select="$table.type-multi"/>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'vc_thread'"/>
  </xsl:call-template>
  <xsl:text> FROM </xsl:text>
  <xsl:value-of select="$table.type-multi"/>
  <xsl:text> WHERE </xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'id'"/>
  </xsl:call-template>
  <xsl:text> = new.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="concat($direction,'_doc')"/>
  </xsl:call-template>
  <xsl:text>;</xsl:text>
  <xsl:text>&br;&sp;SELECT INTO res, vc_old</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$table.type-multi"/>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'id'"/>
  </xsl:call-template>
  <xsl:text>, </xsl:text>
  <xsl:value-of select="$table.type-multi"/>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'vc_thread'"/>
  </xsl:call-template>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$table.type-multi"/>
  <xsl:text> INNER JOIN </xsl:text>
  <xsl:value-of select="$table.refs"/>
  <xsl:text> r&br;</xsl:text>
  <xsl:text>&sp;ON </xsl:text>
  <xsl:value-of select="$table.type-multi"/>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'id'"/>
  </xsl:call-template>
  <xsl:text> = r.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="concat($direction,'_doc')"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;WHERE r.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'ref_type'"/>
  </xsl:call-template>
  <xsl:text> = ref_type&br;</xsl:text>
  <xsl:text>&sp;AND r.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="concat($reverse-direction,'_doc')"/>
  </xsl:call-template>
  <xsl:text>&sp;= new.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="concat($reverse-direction,'_doc')"/>
  </xsl:call-template>
  <xsl:text>&br;&sp;AND </xsl:text>
  <xsl:value-of select="$table.type-multi"/>
  <xsl:text>.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="'vc_thread'"/>
  </xsl:call-template>
  <xsl:text> != tmp;&br;</xsl:text>
  <xsl:text>&sp; IF FOUND THEN&br;</xsl:text>
  <xsl:text>&sp;&sp;RAISE EXCEPTION \' INSERT on table </xsl:text>
  <xsl:value-of select="$table.refs"/>
  <xsl:text> was ignored.\n BOESER FEHLER: Sie versuchen, das Uebungsblatt % an den Kurs % , welcher zum VCThread % gehoert, zu knuepfen.\n Es haengt aber (u.a.) schon am Kurs % des VCThreads %.      \', new.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="concat($reverse-direction,'_doc')"/>
  </xsl:call-template>
  <xsl:text>, new.</xsl:text>
  <xsl:call-template name="config.db-column.name">
    <xsl:with-param name="name" select="concat($direction,'_doc')"/>
  </xsl:call-template>
  <xsl:text>, tmp, res, vc_old;&br;</xsl:text>
  <xsl:text>&sp;&sp;RETURN null;&br;</xsl:text>
  <xsl:text>&sp;END IF;&br;</xsl:text>
  <xsl:text>END IF;&br;</xsl:text>
  <xsl:text>RETURN new;&br;</xsl:text>
  <xsl:text>END;'&br;</xsl:text>
  <xsl:text>LANGUAGE 'plpgsql';&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>CREATE TRIGGER unique_</xsl:text>
  <xsl:value-of select="$direction"/>
  <xsl:text>_ref_type</xsl:text>
  <xsl:value-of select="$ref-type"/>
  <xsl:text> BEFORE INSERT ON </xsl:text>
  <xsl:value-of select="$table.refs"/>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
  <xsl:value-of select="$func-name"/>
  <xsl:text>('</xsl:text>
  <xsl:value-of select="$ref-type"/>
  <xsl:text>');&br;</xsl:text>
  <xsl:text>&br;</xsl:text>

</xsl:template>

<!--
   =====================================================================================
   Creating annotations tables
   =====================================================================================
-->

<!--   Wrapper template -->

<xsl:template name="db-core.single-annotations">
  <xsl:for-each select="/*/singledoc-annotations/singledoc-annotation">

<!--   catching attributes  -->
    <xsl:variable name="originator" select="@originator"/>
    <xsl:variable name="with-content" select="@with-content"/>
    <xsl:variable name="doctype" select="./document-type/@name"/>
    <xsl:choose>
      <xsl:when test="$originator='user' or $originator='user_group'">
        <xsl:call-template name="db-core.annotation.single">
          <xsl:with-param name="originator" select="$originator"/>
          <xsl:with-param name="with-content" select="$with-content"/>
          <xsl:with-param name="doctype" select="$doctype"/>
          <xsl:with-param name="ann-attributes" select="./ann-attributes"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          <xsl:text>Invalid or missing attribute "originator" in singledoc-annotation.&br;Must be "user" or "user_group". Is: </xsl:text>
          <xsl:value-of select="$originator"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<xsl:template name="db-core.annotation.single">
  <xsl:param name="originator" select="user"/>
  <xsl:param name="with-content" select="false"/>
  <xsl:param name="doctype"/>
  <xsl:param name="ann-attributes"/>
  <xsl:variable name="name">
    <xsl:call-template name="config.db-table-name.anns-single-document">
      <xsl:with-param name="originator" select="$originator"/>
      <xsl:with-param name="doctype" select="$doctype"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.originator">
    <xsl:with-param name="originator" select="$originator"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.doc">
    <xsl:with-param name="doctype" select="$doctype"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:if test="$with-content='true'">
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.annotations-content"/>
  </xsl:if>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.ann_type"/>
  <!-- maybe the version of origin ?
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.annotated-document">
    <xsl:with-param name="doctype" select="doctype"/>
  </xsl:call-template>
  -->
  <!--  Creating additional annotation attributes -->
  <xsl:for-each select="$ann-attributes/ann-attribute">
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.ann-attribute">
      <xsl:with-param name="db-column-name" select="./@db-column-name"/>
      <xsl:with-param name="sql-datatype" select="./@sql-datatype"/>
      <xsl:with-param name="sql-default" select="./@sql-default"/>
      <xsl:with-param name="allowed-ann-types" select="allowed-ann-types"/>
    </xsl:call-template>
  </xsl:for-each>
  <xsl:text>&br;&sp;);&br;</xsl:text>

</xsl:template>

<!--   Wrapper template catching attribute "originator" and document pairs -->

<xsl:template name="db-core.annotations">
  <xsl:for-each select="/*/multidoc-annotations/multidoc-annotation">

<!--   catching attribute "originator"   -->
    <xsl:variable name="originator" select="@originator"/>
    <xsl:choose>
      <xsl:when test="$originator='user' or $originator='user_group'">
        
        <xsl:variable name="order-max" select="count(document-types/document-type)"/>
        
        <xsl:choose>
          <xsl:when test="$order-max=2">
            <xsl:variable name="first-doctype" select="document-types/document-type[@order='1']/@name"/>
            <xsl:variable name="second-doctype" select="document-types/document-type[@order='2']/@name"/>
            
            <xsl:call-template name="db-core.annotation.pairs">
              <xsl:with-param name="originator" select="$originator"/>
              <xsl:with-param name="first-doctype" select="$first-doctype"/>
              <xsl:with-param name="second-doctype" select="$second-doctype"/>
              <xsl:with-param name="ann-attributes" select="./ann-attributes"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="no">
              <xsl:text>Creation of annotations table skipped.&br;</xsl:text>
              <xsl:text>Annotations to more than two document types are not yet implemented&br;</xsl:text>
              <xsl:text>order-max is: </xsl:text>
              <xsl:value-of select="$order-max"/>
            </xsl:message>
            
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          <xsl:text>Invalid or missing attribute "originator" in multidoc-annotation.&br;Must be "user" or "user_group". Is: </xsl:text>
          <xsl:value-of select="$originator"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

<!--   wrapped template assuming valid attribute values  -->

<xsl:template name="db-core.annotation.pairs">
  <xsl:param name="originator" select="user"/>
  <xsl:param name="first-doctype"/>
  <xsl:param name="second-doctype"/>
  <xsl:param name="ann-attributes"/>
  <xsl:variable name="name">
    <xsl:call-template name="config.db-table-name.anns-document-document">
      <xsl:with-param name="originator" select="$originator"/>
      <xsl:with-param name="from-doctype" select="$first-doctype"/>
      <xsl:with-param name="to-doctype" select="$second-doctype"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.originator">
    <xsl:with-param name="originator" select="$originator"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.ref">
    <xsl:with-param name="first-doctype" select="$first-doctype"/>
    <xsl:with-param name="second-doctype" select="$second-doctype"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.annotations-content"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.ann_type"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.annotated-document">
    <xsl:with-param name="doctype" select="$first-doctype"/>
    <xsl:with-param name="order" select="'1'"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.annotated-document">
    <xsl:with-param name="doctype" select="$second-doctype"/>
    <xsl:with-param name="order" select="'2'"/>
  </xsl:call-template>
  <!--  Creating additional annotation attributes -->
  <xsl:for-each select="$ann-attributes/ann-attribute">
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.ann-attribute">
      <xsl:with-param name="db-column-name" select="./@db-column-name"/>
      <xsl:with-param name="sql-datatype" select="./@sql-datatype"/>
      <xsl:with-param name="sql-default" select="./@sql-default"/>
      <xsl:with-param name="allowed-ann-types" select="allowed-ann-types"/>
    </xsl:call-template>
  </xsl:for-each>
  <xsl:text>&br;&sp;);&br;</xsl:text>

  <xsl:text>CREATE INDEX index_</xsl:text><xsl:value-of select="$name"/><xsl:text> ON </xsl:text><xsl:value-of select="$name"/><xsl:text>(</xsl:text><xsl:value-of select="$db-notions.column-name.ref"/><xsl:text>,</xsl:text><xsl:choose>
    <xsl:when test="$originator='user'">
      <xsl:value-of select="$db-notions.column-name.the_user"/>
    </xsl:when>
    <xsl:when test="$originator='user_group'">
      <xsl:value-of select="$db-notions.column-name.user_group"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        <xsl:text>db-core.column.originator: Unknown originator: </xsl:text>
        <xsl:value-of select="$originator"/>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose><xsl:text>,</xsl:text><xsl:value-of select="$db-notions.column-name.ann_type"/><xsl:text>);&br;</xsl:text>

</xsl:template>

<!--
   =====================================================================================
   Creating additional reference attributes
   =====================================================================================
-->

<xsl:template name="db-core.column.ref-attribute">
  <xsl:param name="name" select="@name"/>
  <xsl:param name="datatype" select="@sql-datatype"/>
  <xsl:param name="not-null" select="''"/>
  <xsl:param name="default" select="''"/>
  <xsl:value-of select="$name"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$datatype"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$not-null"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$default"/>
</xsl:template>

<xsl:template name="db-core.ref-attributes">
  <xsl:for-each select="/*/ref-attributes/ref-attribute">
    
    <!--   catching attributes   -->
    <xsl:variable name="name" select="./@db-column-name"/>
    <xsl:variable name="sql-datatype" select="./@sql-datatype"/>
    <xsl:variable name="allowed-ref-types">

      <xsl:for-each select="./allowed-ref-types/ref-type">
        <xsl:if test="position()&gt;1">
          <xsl:text>,</xsl:text>
        </xsl:if>
        <xsl:call-template name="config.reference-type.code"/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:for-each select="./doctype-pairs/doctype-pair">
      <xsl:variable name="from-doctype" select="./@from-doctype"/>
      <xsl:variable name="to-doctype" select="./@to-doctype"/>
      <xsl:variable name="ref-table-name">
        <xsl:call-template name="config.db-table-name.refs-document-document">
          <xsl:with-param name="from-doctype" select="$from-doctype"/>
          <xsl:with-param name="to-doctype" select="$to-doctype"/>
        </xsl:call-template>
      </xsl:variable>
      <!--  writing the SQL -->
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating points column ',$name,' in table ', $ref-table-name)"/>
      </xsl:call-template>
      <xsl:text>ALTER TABLE </xsl:text>
      <xsl:value-of select="$ref-table-name"/>
      <xsl:text>&br;&sp;ADD </xsl:text>
        <xsl:call-template name="db-core.column.ref-attribute">
          <xsl:with-param name="name" select="$name"/>
          <xsl:with-param name="datatype" select="$sql-datatype"/>
        </xsl:call-template>
      <xsl:text>;&br;</xsl:text>
      <!--  creating "ref-type-only" constraint if configured such. Throwing an sql error like:
      ERROR:  ExecInsert: rejected due to CHECK constraint "refs_fromDocType_toDocType_columnName_constraint" on "refs_fromDocType_toDocType"
      -->
      <xsl:if test="$allowed-ref-types">
        <xsl:variable name="constraint-name" select="concat($name,'_reftypes')"/>
        <xsl:text>ALTER TABLE </xsl:text>
        <xsl:value-of select="$ref-table-name"/>
        <xsl:text> ADD CONSTRAINT </xsl:text>
        <xsl:call-template name="config.db-constraint-name.ref-attribute">
          <xsl:with-param name="table-name" select="$ref-table-name"/>
          <xsl:with-param name="column-name" select="$name"/>
        </xsl:call-template>
        <xsl:text> CHECK (</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> IS NULL OR </xsl:text>
        <xsl:value-of select="$db-notions.column-name.ref_type"/>
        <xsl:text> IN (</xsl:text>
        <xsl:value-of select="$allowed-ref-types"/>
        <xsl:text>));&br;</xsl:text>
      </xsl:if>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating text_sources table
   =====================================================================================
-->

<xsl:template name="db-core.text_sources-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.text_sources)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.text_sources"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.filename"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>

  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.content_type"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.content_length"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>

  <xsl:call-template name="db-core.column.content">
    <xsl:with-param name="format" select="'text'"/>
  </xsl:call-template>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'text_sources'"/>
  </xsl:call-template>
</xsl:template>

<!--
   =====================================================================================
   Creating binary_sources table
   =====================================================================================
-->

<xsl:template name="db-core.binary_sources-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.binary_sources)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.binary_sources"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.filename"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>

  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.content_type"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.content_length"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>

  <xsl:call-template name="db-core.column.content">
    <xsl:with-param name="format" select="'binary'"/>
  </xsl:call-template>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'binary_sources'"/>
  </xsl:call-template>
</xsl:template>

<!--
   =====================================================================================
   Creating juction tables between documents and sources
   =====================================================================================
-->

<xsl:template name="db-core.refs-document-source-tables">
  <xsl:param name="format"/>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="from-doctype" select="@name"/>
    <xsl:variable name="has-source">
      <xsl:call-template name="config.document-type.has-source">
        <xsl:with-param name="format" select="$format"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="$is-generic='no' and $has-source!='no'">
      <xsl:variable name="name">
        <xsl:call-template name="config.db-table-name.refs-document-source">
          <xsl:with-param name="from-doctype" select="$from-doctype"/>
          <xsl:with-param name="format" select="$format"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
      </xsl:call-template>
      <xsl:text>CREATE TABLE </xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>&br;&sp;(&br;</xsl:text>
      <xsl:text>&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.from_doc">
	<xsl:with-param name="from-doctype-name" select="$from-doctype"/>
      </xsl:call-template>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.to-source">
          <xsl:with-param name="format" select="$format"/>
      </xsl:call-template>
      <xsl:text>&br;</xsl:text>
      <xsl:text>&sp;);&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating juction tables between documents and binary_sources
   =====================================================================================
-->

<!--
   =====================================================================================
   Creating write-permissions tables
   =====================================================================================
-->

<xsl:template name="db-core.write-permissions-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="$is-generic='no'">
      <xsl:if test="position()&gt;1">
        <!--  FIXME: Ist das ok ? Was passiert, wenn der erste Dokumenttyp generisch ist ?! -->
        <xsl:text>&br;</xsl:text>
      </xsl:if>
      <xsl:variable name="name">
        <xsl:call-template name="config.db-table-name.write-permissions"/>
      </xsl:variable>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
      </xsl:call-template>
      <xsl:text>CREATE TABLE </xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>&br;&sp;(&br;</xsl:text>
      <xsl:text>&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.user_group"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.vc_thread"/>
      <xsl:text>&br;&sp;);&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template name="db-core.write-permissions-tables.pseudo-document">
  <xsl:for-each select="/*/pseudodoc-types/pseudodoc-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:variable name="name">
      <xsl:call-template name="config.db-table-name.write-permissions"/>
    </xsl:variable>
    <xsl:call-template name="db-core.echo">
      <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
    </xsl:call-template>
    <xsl:text>CREATE TABLE </xsl:text>
    <xsl:value-of select="$name"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.user_group"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.pseudo_document"/>
    <xsl:text>&br;&sp;);&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating read permissions tables
   =====================================================================================
-->

<xsl:template name="db-core.read-permissions-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="$is-generic='no'">
      <xsl:if test="position()&gt;1">
        <!--  FIXME: Ist das ok ? Was passiert, wenn der erste Dokumenttyp generisch ist ?! -->
        <xsl:text>&br;</xsl:text>
      </xsl:if>
      <xsl:variable name="name">
        <xsl:call-template name="config.db-table-name.read-permissions"/>
      </xsl:variable>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
      </xsl:call-template>
      <xsl:text>CREATE TABLE </xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>&br;&sp;(&br;</xsl:text>
      <xsl:text>&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.user_group"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.document"/>
      <xsl:text>&br;&sp;);&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
  <xsl:for-each select="/*/pseudodoc-types/pseudodoc-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:variable name="name">
      <xsl:call-template name="config.db-table-name.read-permissions"/>
    </xsl:variable>
    <xsl:call-template name="db-core.echo">
      <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
    </xsl:call-template>
    <xsl:text>CREATE TABLE </xsl:text>
    <xsl:value-of select="$name"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.user_group"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.pseudo_document"/>
    <xsl:text>&br;&sp;);&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating document authors tables
   =====================================================================================
-->

<xsl:template name="db-core.document-authors-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:variable name="name">
      <xsl:call-template name="config.db-table-name.document-authors"/>
    </xsl:variable>
    <xsl:call-template name="db-core.echo">
      <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
    </xsl:call-template>
    <xsl:text>CREATE TABLE </xsl:text>
    <xsl:value-of select="$name"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.document"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.author"/>
    <xsl:text>&br;&sp;);&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating class lecturers table
   =====================================================================================
-->

<xsl:template name="db-core.class-lecturers-table">
  <xsl:variable name="name">
    <xsl:value-of select="$db-notions.table-name.class_lecturers"/>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.class"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.lecturer"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$name"/>
  </xsl:call-template>
  
</xsl:template>

<!--
   =====================================================================================
   Creating tutorial members tables
   =====================================================================================
-->
 <!-- auxiliar.
       creating (redundant) UNIQUE constraint needed for foreign key with two columns.

       ALTER TABLE $table-name ADD CONSTRAINT unique_$column UNIQUE(id,$column)

       table-name and column are passed as parameters. -->

 <xsl:template name="db-core.redundant-unique-constraint">
   <xsl:param name="table-name"/>
   <xsl:param name="column"/>
   <xsl:variable name="name">
     <xsl:value-of select="concat(concat('unique_',$column),$table-name)"/>
   </xsl:variable>
   <xsl:call-template name="db-core.echo">
     <xsl:with-param name="message" select="concat('Creating constraint ',$name, ' redundant, but needed for foreign key with two columns')"/>
   </xsl:call-template>
   <xsl:text>ALTER TABLE </xsl:text>
   <xsl:value-of select="$table-name"/>
   <xsl:text> ADD CONSTRAINT </xsl:text>
   <xsl:value-of select="$name"/>
   <xsl:text> UNIQUE(</xsl:text>
   <xsl:value-of select="$db-notions.column-name.id"/>
   <xsl:text>,</xsl:text>
   <xsl:value-of select="$column"/>
   <xsl:text>);&br;&br;</xsl:text>
 </xsl:template>

<!-- co-author: Sabine Cikic <cikic@math.tu-berlin.de -->
<xsl:template name="db-core.tutorial-members-table">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name">
    <xsl:value-of select="$db-notions.table-name.tutorial_members"/>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
  </xsl:call-template>
  <xsl:call-template name="db-core.redundant-unique-constraint">
    <xsl:with-param name="table-name" select="$db-notions.table-name.tutorials"/>
    <xsl:with-param name="column" select="$db-notions.column-name.class"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.member"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.tutorial"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.class">
    <xsl:with-param name="is-reference" select="'false'"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>FOREIGN KEY (</xsl:text>
  <xsl:value-of select="$db-notions.column-name.tutorial"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class"/>
  <xsl:text>) REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorials"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class"/>
  <xsl:text>)</xsl:text>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>UNIQUE (</xsl:text>
  <xsl:value-of select="$db-notions.column-name.member"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class"/>
  <xsl:text>)</xsl:text>
  <xsl:text>&br;&sp;);&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating Generic Document IMplementation (GDIM) tables
   =====================================================================================
-->

<xsl:template name="db-core.gdim-document-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-generic-counterpart">
      <xsl:call-template name="config.document-type.has-generic-counterpart"/>
    </xsl:variable>
    <xsl:if test="$has-generic-counterpart='yes'">
      <xsl:variable name="name">
        <xsl:call-template name="config.db-table-name.gdim-document"/>
      </xsl:variable>
      <xsl:if test="position()&gt;1">
        <xsl:text>&br;</xsl:text>
      </xsl:if>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
      </xsl:call-template>
      <xsl:text>CREATE TABLE </xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>&br;&sp;(&br;</xsl:text>
      <xsl:text>&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.theme"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.language"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.generic_document"/>
      <xsl:text>,&br;</xsl:text>
      <xsl:text>&sp;&sp;UNIQUE (</xsl:text>
      <xsl:value-of select="$db-notions.column-name.theme"/>
      <xsl:text>,</xsl:text>
      <xsl:value-of select="$db-notions.column-name.language"/>
      <xsl:text>,</xsl:text>
      <xsl:value-of select="$db-notions.column-name.generic_document"/>
      <xsl:text>),&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="db-core.column.document"/>
      <xsl:text>&br;&sp;);&br;</xsl:text>
      <xsl:text>&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Functions
   =====================================================================================
-->

<!--
  Resolves section paths to ids. Usage:

    section_id_for_path(START_SEC_ID, PATH)

  Returns the id of the section with the path PATH relative to the section with the id
  START_SEC_ID. If the latter is 0 (root section), the absolute path is returned.
-->

<xsl:template name="db-core.function.section_id_for_path">
  <xsl:text>CREATE OR REPLACE FUNCTION section_id_for_path(int,text)&br;</xsl:text>
  <xsl:text>RETURNS int&br;</xsl:text>
  <xsl:text>AS '&br;</xsl:text>
  <xsl:text>DECLARE&br;</xsl:text>
  <xsl:text>&sp;root integer;&br;</xsl:text>
  <xsl:text>&sp;restpath text;&br;</xsl:text>
  <xsl:text>&sp;delimiter text;&br;</xsl:text>
  <xsl:text>&sp;firstlength int;&br;</xsl:text>
  <xsl:text>&sp;lastlength int;&br;</xsl:text>
  <xsl:text>&sp;tmpid integer;&br;</xsl:text>
  <xsl:text>&sp;tmpname text;&br;</xsl:text>
  <xsl:text>BEGIN&br;</xsl:text>
  <xsl:text>&sp;root := $1;&br;</xsl:text>
  <xsl:text>&sp;delimiter := ''/'';&br;</xsl:text>
  <xsl:text>&sp;restpath=$2;&br;</xsl:text>
  <xsl:text>&sp;IF (restpath='''' OR restpath IS NULL ) THEN&br;</xsl:text>
  <xsl:text>&sp;&sp;RETURN root;&br;</xsl:text>
  <xsl:text>&sp;ELSE &br;</xsl:text>
  <xsl:text>&sp;&sp;tmpname = split_part(restpath,delimiter,1);&br;</xsl:text>
  <xsl:text>&sp;&sp;firstlength = char_length(tmpname);&br;</xsl:text>
  <xsl:text>&sp;&sp;lastlength = char_length(restpath) - firstlength;&br;</xsl:text>
  <xsl:text>&sp;&sp;SELECT INTO root id&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;FROM </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;WHERE </xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>=root&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;AND </xsl:text>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>=tmpname;&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;IF NOT FOUND THEN&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;&sp;RETURN -1;&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;END IF;&br;</xsl:text>
  <xsl:text>&sp;&sp;RETURN section_id_for_path(root,substring(restpath from firstlength+2 for lastlength)); &br;</xsl:text>
  <xsl:text>&sp;END IF;&br;</xsl:text>
  <xsl:text>END;'&br;</xsl:text>
  <xsl:text>LANGUAGE 'plpgsql';&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating initial pseudo-documents (sections, admin, etc.)
   =====================================================================================
-->

<!--
  Creates a new section. Expects the following parameters:

    id    Id of the new section. Optional.

    pure_name
          Pure name of the new section

    name  Name of the new section

    description
          Description of the new section

    contained_in
          Id of the section the new section is contained in. Only one of this and
          contained_in.path should be specified.

    contained_in.path
          Path of the section the new section is contained in. Only one of this and
          contained_in should be specified.
-->

<xsl:template name="db-core.insert-into.sections">
  <xsl:param name="id"/>
  <xsl:param name="pure_name"/>
  <xsl:param name="name" select="$pure_name"/>
  <xsl:param name="description"/>
  <xsl:param name="contained_in"/>
  <xsl:param name="contained_in.path"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message">
      <xsl:choose>
        <xsl:when test="$id = $db-core.rootsection.id">
          <xsl:text>Creating root section</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>Creating section </xsl:text>
          <xsl:value-of select="concat($contained_in.path, '/', $pure_name)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;&sp;(&br;&sp;&sp;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:value-of select="$db-notions.column-name.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
  </xsl:if>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
  <xsl:text>VALUES (&br;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="$id"/><xsl:text>,&br;</xsl:text>
  </xsl:if>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$pure_name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$description"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:choose>
    <xsl:when test="$contained_in and $contained_in != ''">
      <xsl:value-of select="$contained_in"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>section_id_for_path(</xsl:text><xsl:value-of select="$db-core.rootsection.id"/><xsl:text>,'</xsl:text><xsl:value-of select="$contained_in.path"/><xsl:text>')</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>&br;&sp;);&br;</xsl:text>
</xsl:template>

<!--
  Gives the admin user_group read_permission.
-->

  <xsl:template name="db-core.admin-permission.pseudo-document">
    <xsl:param name="type"/>
    <xsl:param name="id"/>
    <xsl:param name="contained_in.path"/>
    <xsl:param name="contained_in"/>
    <xsl:param name="pure_name"/>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:call-template name="config.db-table-name.read-permissions">
      <xsl:with-param name="type" select="$type"/>
    </xsl:call-template>
    <xsl:text>&br;&sp;(&br;&sp;&sp;</xsl:text>
    <xsl:value-of select="$db-notions.column-name.user_group"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:value-of select="$db-notions.column-name.pseudo_document"/>
    <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
    <xsl:choose>
      <xsl:when test="$id and $id != ''">
        <xsl:text>VALUES (&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;</xsl:text><xsl:value-of select="$db-core.admin-group.id"/><xsl:text>,&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;</xsl:text><xsl:value-of select="$id"/><xsl:text>&br;&sp;);</xsl:text>
      </xsl:when>
      <xsl:when test="$pure_name and $pure_name != ''">
        <xsl:text>SELECT &br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;</xsl:text><xsl:value-of select="$db-core.admin-group.id"/><xsl:text>,&br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;</xsl:text><xsl:value-of select="$db-notions.column-name.id"/><xsl:text>&br;</xsl:text>
        <xsl:text>&sp;&sp;FROM &br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;</xsl:text>
        <xsl:call-template name="config.db-table-name.pseudo-document">
          <xsl:with-param name="pseudodoc-type" select="$type"/>
        </xsl:call-template>
        <xsl:text>&br;</xsl:text>
        <xsl:text>&sp;&sp;WHERE &br;</xsl:text>
        <xsl:text>&sp;&sp;&sp;</xsl:text><xsl:value-of select="$db-notions.column-name.pure_name"/><xsl:text>='</xsl:text>
        <xsl:value-of select="$pure_name"/><xsl:text>'&br;</xsl:text>
        <xsl:text>&sp;&sp;AND &br;</xsl:text><xsl:value-of select="$db-notions.column-name.contained_in"/><xsl:text>=</xsl:text>
        <xsl:choose>
          <xsl:when test="$contained_in and $contained_in != ''">
            <xsl:value-of select="$contained_in"/>
          </xsl:when>
          <xsl:when test="$contained_in.path and $contained_in.path != ''">
            <xsl:text>section_id_for_path(</xsl:text><xsl:value-of select="$db-core.rootsection.id"/><xsl:text>,'</xsl:text><xsl:value-of select="$contained_in.path"/><xsl:text>')</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <!--error-->
            <xsl:call-template name="config.error">
              <xsl:with-param name="message">
                <xsl:text>db-core.admin-permission.pseudo-document: no id and no contained_in.path found</xsl:text>
                <xsl:text>&br;id = </xsl:text><xsl:value-of select="$id"/>
                <xsl:text>&br;pure_name = </xsl:text><xsl:value-of select="$pure_name"/>
                <xsl:text>&br;contained_in.path = </xsl:text><xsl:value-of select="$contained_in.path"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>;&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <!--error-->
        <xsl:call-template name="config.error">
          <xsl:with-param name="message">
            <xsl:text>db-core.admin-permission.pseudo-document: no id and no pure_name found</xsl:text>
            <xsl:text>&br;id = </xsl:text><xsl:value-of select="$id"/>
            <xsl:text>&br;pure_name = </xsl:text><xsl:value-of select="$pure_name"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!--
  Creates a new user. Expects the following parameters:

    id    Id of the new user. Optional.

    pure_name
          Pure name of the new user

    login_name
          Login name of the new user

    first_name
          First name of the new user

    surname
          Suname of the new user

    description
          Description of the new user

    contained_in.path
          Path of the section the new user is contained in

    theme
          Id of the theme of the new user

    language
          Id of the language of the new user
-->

<xsl:template name="db-core.insert-into.users">
  <xsl:param name="id"/>
  <xsl:param name="pure_name"/>
  <xsl:param name="login_name" select="$pure_name"/>
  <xsl:param name="first_name" select="$pure_name"/>
  <xsl:param name="surname" select="$pure_name"/>
  <xsl:param name="description"/>
  <xsl:param name="contained_in.path"/>
  <xsl:param name="theme"/>
  <xsl:param name="language"/>
  <xsl:param name="password"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating user ', $login_name)"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>(&br;&sp;&sp;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:value-of select="$db-notions.column-name.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
  </xsl:if>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.login_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.first_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.surname"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.theme"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.language"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.password"/>
    <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
  <xsl:text>&sp;&sp;VALUES (&br;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="$id"/><xsl:text>,&br;</xsl:text>
  </xsl:if>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$pure_name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$login_name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$first_name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$surname"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$description"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;section_id_for_path(</xsl:text><xsl:value-of select="$db-core.rootsection.id"/><xsl:text>,'</xsl:text><xsl:value-of select="$contained_in.path"/><xsl:text>'),&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="$theme"/><xsl:text>,&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="$language"/><xsl:text>,&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="x-sql:toSQL(x-encrypt:encrypt($password))"/><xsl:text>&br;</xsl:text>
  <xsl:text>);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
  Creates a new user group. Expects the following parameters:

    id    Id of the new user group. Optional.

    pure_name
          Pure name of the new user group

    name  Name of the new user group

    description
          Description of the new user group

    contained_in.path
          Path of the section the new user group is contained in
-->

<xsl:template name="db-core.insert-into.user_groups">
  <xsl:param name="id"/>
  <xsl:param name="pure_name"/>
  <xsl:param name="name"/>
  <xsl:param name="description"/>
  <xsl:param name="contained_in.path"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating user group ', $name)"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_groups"/>
  <xsl:text>&br;&sp;(&br;&sp;&sp;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:value-of select="$db-notions.column-name.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
  </xsl:if>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
  <xsl:text>VALUES (&br;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="$id"/><xsl:text>,&br;</xsl:text>
  </xsl:if>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$pure_name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$description"/><xsl:text>',&br;</xsl:text>
  <xsl:text>section_id_for_path(</xsl:text><xsl:value-of select="$db-core.rootsection.id"/><xsl:text>,'</xsl:text><xsl:value-of select="$contained_in.path"/><xsl:text>')</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!-- 
  Puts a user into a user group. expects the following parameters:

    member Id of the user

    user_group
           Id of the user group

    admin  Whether the user has administrator rights in the group

-->

<xsl:template name="db-core.insert-into.user_group_members">
  <xsl:param name="user_group"/>
  <xsl:param name="member"/>
  <xsl:param name="admin"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Taking user ', $member, ' to user group', $user_group)"/>
  </xsl:call-template>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_group_members"/>
  <xsl:text>&sp;(&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.member"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$db-notions.column-name.user_group"/>
  <xsl:if test="$admin">
    <xsl:text>,</xsl:text>
    <xsl:value-of select="$db-notions.column-name.admin"/>
  </xsl:if>
  <xsl:text>) VALUES (</xsl:text>
  <xsl:value-of select="$user_group"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$member"/>
  <xsl:if test="$admin">
    <xsl:text>,</xsl:text>
    <xsl:value-of select="$admin"/>
  </xsl:if>
  <xsl:text>);&br;</xsl:text>
</xsl:template>

<!--
  Creates a new theme. Expects the following parameters:

    id    Id of the new theme. Optional.

    pure_name
          Pure name of the new theme

    name  Name of the new theme

    description
          Description of the new theme

    contained_in.path
          Path of the section the new theme is contained in
-->

<xsl:template name="db-core.insert-into.themes">
  <xsl:param name="id"/>
  <xsl:param name="pure_name"/>
  <xsl:param name="name"/>
  <xsl:param name="description"/>
  <xsl:param name="contained_in.path"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating theme ', $name)"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.themes"/>
  <xsl:text>&br;&sp;(&br;&sp;&sp;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:value-of select="$db-notions.column-name.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
  </xsl:if>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
  <xsl:text>VALUES (&br;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="$id"/><xsl:text>,&br;</xsl:text>
  </xsl:if>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$pure_name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$description"/><xsl:text>',&br;</xsl:text>
  <xsl:text>section_id_for_path(</xsl:text><xsl:value-of select="$db-core.rootsection.id"/><xsl:text>,'</xsl:text><xsl:value-of select="$contained_in.path"/><xsl:text>')</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
  Creates a new language. Expects the following parameters:

    id    Id of the new language. Optional.

    pure_name
          Pure name of the new language

    name  Name of the new language

    description
          Description of the new language

    contained_in.path
          Path of the section the new language is contained in

    code  The language code of the new language
-->

<xsl:template name="db-core.insert-into.languages">
  <xsl:param name="id"/>
  <xsl:param name="pure_name"/>
  <xsl:param name="name" select="$pure_name"/>
  <xsl:param name="description"/>
  <xsl:param name="contained_in.path"/>
  <xsl:param name="code"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating language ', $name)"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.languages"/>
  <xsl:text>&br;&sp;(&br;&sp;&sp;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:value-of select="$db-notions.column-name.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
  </xsl:if>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.code"/>
  <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
  <xsl:text>VALUES (&br;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:text>&sp;&sp;</xsl:text><xsl:value-of select="$id"/><xsl:text>,&br;</xsl:text>
  </xsl:if>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$pure_name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$name"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$description"/><xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;section_id_for_path(</xsl:text><xsl:value-of select="$db-core.rootsection.id"/><xsl:text>,'</xsl:text><xsl:value-of select="$contained_in.path"/><xsl:text>')</xsl:text><xsl:text>,&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text><xsl:value-of select="$code"/><xsl:text>'&br;</xsl:text>
  <xsl:text>&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating view with memberships and detailed user-data combined
   =====================================================================================
-->

<xsl:template name="db-views.members_detailed">
  <xsl:variable name="name" select="$db-notions.table-name.user_group_members_detailed"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;&sp;</xsl:text>
  <xsl:text>SELECT </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_group_members"/>
  <xsl:text>.*,</xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>.*&br;&sp;&sp;</xsl:text>
  <xsl:text>FROM </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_group_members"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:text>WHERE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_group_members"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.member"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>;&br;&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating view with elearning classes and attached courses
   =====================================================================================
-->

<xsl:template name="db-views.classes_and_courses">
  <xsl:variable name="name" select="$db-notions.table-name.classes_and_courses"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;&sp;</xsl:text>
  <xsl:text>SELECT&br;</xsl:text>
  <xsl:text>&sp;'class' AS doc_type,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:text>NULL AS </xsl:text><xsl:value-of select="$db-notions.column-name.class"/><xsl:text>&br;</xsl:text>
  <xsl:text>FROM </xsl:text><xsl:value-of select="$db-notions.table-name.classes"/><xsl:text>&br;</xsl:text>
  <xsl:text>UNION&br;</xsl:text>
  <xsl:text>SELECT&br;</xsl:text>
  <xsl:text>&sp;'course' AS doc_type,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class"/><xsl:text>&br;</xsl:text>
  <xsl:text>FROM </xsl:text>
  <xsl:call-template name="config.db-view-name.latest-document">
    <xsl:with-param name="doctype" select="'course'"/>
  </xsl:call-template>
  <xsl:text>&br;;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating views containing only documents, that are latest in their vc_thread
   =====================================================================================
-->

<xsl:template name="db-views.latest-docs">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="$is-generic='no'">
      <xsl:variable name="view-name">
        <xsl:call-template name="config.db-view-name.latest-document"/>
      </xsl:variable>
      <xsl:variable name="doc-table-name">
        <xsl:call-template name="config.db-table-name.document"/>
      </xsl:variable>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating view ',$view-name)"/>
      </xsl:call-template>
      <xsl:text>CREATE VIEW </xsl:text>
      <xsl:value-of select="$view-name"/>
      <xsl:text> AS&br;&sp;</xsl:text>
      <xsl:text>SELECT doc1.*&br;&sp;</xsl:text>
      <xsl:text>FROM </xsl:text>
      <xsl:value-of select="$doc-table-name"/>
      <xsl:text> doc1&br;&sp;</xsl:text>
      <xsl:text>WHERE doc1.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.version"/>
      <xsl:text> IN (&br;&sp;&sp;</xsl:text>
      <xsl:text>SELECT max(</xsl:text>
      <xsl:value-of select="$db-notions.column-name.version"/>
      <xsl:text>)&br;&sp;&sp;FROM </xsl:text>
      <xsl:value-of select="$doc-table-name"/>
      <xsl:text> doc2&br;&sp;&sp;</xsl:text>
      <xsl:text>WHERE doc1.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.vc_thread"/>
      <xsl:text> = doc2.</xsl:text>
      <xsl:value-of select="$db-notions.column-name.vc_thread"/>
      <xsl:text>);&br;&br;</xsl:text>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating document members views.
   =====================================================================================
-->

<xsl:template name="db-views.document-members">
  <xsl:for-each select="/*/document-types/document-type[@has-class='yes']">
    <xsl:text>&br;</xsl:text>
    <xsl:variable name="name">
      <xsl:call-template name="config.db-view-name.document-members"/>
    </xsl:variable>
    <xsl:call-template name="db-core.echo">
      <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
    </xsl:call-template>
    <xsl:text>CREATE VIEW </xsl:text>
    <xsl:value-of select="$name"/>
    <xsl:text>&br;&sp;AS &br;</xsl:text>
    <xsl:text>&sp;&sp;SELECT </xsl:text>
    <xsl:call-template name="config.db-table-name.document"/>
    <xsl:text>.</xsl:text>
    <xsl:value-of select="$db-notions.column-name.id"/>
    <xsl:text> AS "</xsl:text>
    <xsl:value-of select="$db-notions.column-name.document"/>
    <xsl:text>",</xsl:text>
    <xsl:value-of select="$db-notions.table-name.tutorial_members"/>
    <xsl:text>.</xsl:text>
    <xsl:value-of select="$db-notions.column-name.member"/>
    <xsl:text>&br;&sp;FROM </xsl:text>
    <xsl:call-template name="config.db-table-name.document"/>
    <xsl:text>,</xsl:text>
    <xsl:value-of select="$db-notions.table-name.tutorial_members"/>
    <xsl:text>&br;&sp;WHERE </xsl:text>
    <xsl:call-template name="config.db-table-name.document"/>
    <xsl:text>.</xsl:text>
    <xsl:value-of select="$db-notions.column-name.class"/>
    <xsl:text> = </xsl:text>
    <xsl:value-of select="$db-notions.table-name.tutorial_members"/>
    <xsl:text>.</xsl:text>
    <xsl:value-of select="$db-notions.column-name.class"/>
    <xsl:text>&br;&sp;;&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating assigned_problems view.
   =====================================================================================
-->

<xsl:template name="db-views.assigned-problems">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.assigned_problems"/>
  <xsl:variable name="ref-alias" select="'ref'"/>
  <xsl:variable name="prb-alias" select="'prb'"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ref"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.problem_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.label"/>
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.points"/>
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="'worksheet'"/>
    <xsl:with-param name="to-doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$ref-alias"/>
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.document">
    <xsl:with-param name="doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$prb-alias"/>
  <xsl:text>&br;&sp;WHERE&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="$ref-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text>&br;;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating problem_context view.
   =====================================================================================
-->

<xsl:template name="db-views.problem_context">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.problem_context"/>
  <xsl:variable name="ref1-alias" select="'ref1'"/>
  <xsl:variable name="ref2-alias" select="'ref2'"/>
  <xsl:variable name="crs-alias" select="'crs'"/>
  <xsl:variable name="wks-alias" select="'wks'"/>
  <xsl:variable name="prb-alias" select="'prb'"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.course_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.course_name"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.vc_thread"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.course_vc_thread_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref1-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref1-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.label"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_label"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.vc_thread"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_vc_thread_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_category_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.timeframe_start"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.timeframe_start"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.timeframe_end"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.timeframe_end"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref2-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ref_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref2-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.problem_id"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref2-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.label"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.problem_label"/>
  <xsl:text>",</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$ref2-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.points"/>
  <!--
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.problem_points"/>
  <xsl:text>",</xsl:text>
  -->
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp; </xsl:text>
  <xsl:value-of select="$prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/>
  <xsl:text> AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.problem_category_id"/>
  <xsl:text>"</xsl:text>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-view-name.latest-document">
    <xsl:with-param name="doctype" select="'course'"/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="'course'"/>
    <xsl:with-param name="to-doctype" select="'worksheet'"/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$ref1-alias"/>
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.document">
    <xsl:with-param name="doctype" select="'worksheet'"/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="'worksheet'"/>
    <xsl:with-param name="to-doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$ref2-alias"/>
  <xsl:text>,</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.document">
    <xsl:with-param name="doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$prb-alias"/>
  <xsl:text>&br;&sp;WHERE&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref1-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;AND&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref1-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;AND&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref1-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="$ref2-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text>&br;&sp;AND&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref2-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="$prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;ORDER BY &br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.course_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_label"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_category_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.problem_label"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.problem_category_id"/>
  <xsl:text>&br;;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating worksheet_context view.
   =====================================================================================
-->

<xsl:template name="db-views.worksheet_context">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.worksheet_context"/>
  <xsl:variable name="wks-alias" select="'wks'"/>
  <xsl:variable name="crs-alias" select="'crs'"/>
  <xsl:variable name="ref-crs-wks-alias" select="'ref_crs_wks'"/>
  <xsl:variable name="ref-wks-prb-alias" select="'ref_wks_prb'"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp;</xsl:text>
  <!-- id -->
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- vc_thread_id -->
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.vc_thread"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.vc_thread_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- category_id -->
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.category_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- label -->
  <xsl:value-of select="$ref-crs-wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.label"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- course_id -->
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.course_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- course_vc_thread_id -->
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.vc_thread"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.course_vc_thread_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- class -->
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.class_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- num_problems -->
  <xsl:text>count(</xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text>) AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.num_problems"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- points -->
  <xsl:text>sum(</xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.points"/>
  <xsl:text>) AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.points"/>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <!-- latest_worksheets AS wks -->
  <xsl:call-template name="config.db-view-name.latest-document">
    <xsl:with-param name="doctype" select="'worksheet'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- latest_courses AS crs -->
  <xsl:call-template name="config.db-view-name.latest-document">
    <xsl:with-param name="doctype" select="'course'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- refs_course_worksheet AS ref_crs_wks -->
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="'course'"/>
    <xsl:with-param name="to-doctype" select="'worksheet'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$ref-crs-wks-alias"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <!-- refs_worksheet_generic_problem AS ref_wks_prb -->
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="'worksheet'"/>
    <xsl:with-param name="to-doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>&br;&sp;WHERE</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref-crs-wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.to_doc"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text>&br;&sp;AND</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;AND</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref-crs-wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;GROUP BY</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <!-- wks.id -->
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>, </xsl:text>
  <!-- wks.vc_thread -->
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.vc_thread"/>
  <xsl:text>, </xsl:text>
  <!-- wks.category -->
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/>
  <xsl:text>, </xsl:text>
  <!-- ref_crs_wks.label -->
  <xsl:value-of select="$ref-crs-wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.label"/>
  <xsl:text>, </xsl:text>
  <!-- crs.id -->
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>, </xsl:text>
  <!-- crs.class -->
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.class"/>
  <xsl:text>, </xsl:text>
  <!-- crs.vc_thread -->
  <xsl:value-of select="$crs-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.vc_thread"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating user_worksheet_gades view and auxiliary views.
   =====================================================================================
-->

<xsl:template name="db-views.user_worksheet_edited">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.user_worksheet_edited"/>
  <xsl:variable name="wks-alias" select="'wks'"/>
  <xsl:variable name="ann-alias" select="'ann'"/>
  <xsl:variable name="ref-wks-prb-alias" select="'ref_wks_prb'"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.the_user"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.user_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>count(</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ann_type"/>
  <xsl:text>) AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.num_edited"/>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-view-name.latest-document">
    <xsl:with-param name="doctype" select="'worksheet'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="'worksheet'"/>
    <xsl:with-param name="to-doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.anns-document-document">
    <xsl:with-param name="originator" select="'user'"/>
    <xsl:with-param name="from-doctype" select="'worksheet'"/>
    <xsl:with-param name="to-doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>&br;&sp;WHERE</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ref"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;AND</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;AND</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ann_type"/>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.annotation-type.code">
    <xsl:with-param name="name" select="'problem_answers'"/>
  </xsl:call-template>
  <xsl:text>&br;&sp;GROUP BY</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>, </xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.the_user"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template name="db-views.user_worksheet_corrected">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.user_worksheet_corrected"/>
  <xsl:variable name="wks-alias" select="'wks'"/>
  <xsl:variable name="ann-alias" select="'ann'"/>
  <xsl:variable name="ref-wks-prb-alias" select="'ref_wks_prb'"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.the_user"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.user_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>count(</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ann_type"/>
  <xsl:text>) AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.num_corrected"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>sum(</xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.points"/>
  <xsl:text> * </xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.score"/>
  <xsl:text>) AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.grade"/>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-view-name.latest-document">
    <xsl:with-param name="doctype" select="'worksheet'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.refs-document-document">
    <xsl:with-param name="from-doctype" select="'worksheet'"/>
    <xsl:with-param name="to-doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="config.db-table-name.anns-document-document">
    <xsl:with-param name="originator" select="'user'"/>
    <xsl:with-param name="from-doctype" select="'worksheet'"/>
    <xsl:with-param name="to-doctype" select="'generic_problem'"/>
  </xsl:call-template>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>&br;&sp;WHERE</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ref"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;AND</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ref-wks-prb-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.from_doc"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>&br;&sp;AND</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.ann_type"/>
  <xsl:text> = </xsl:text>
  <xsl:call-template name="config.annotation-type.code">
    <xsl:with-param name="name" select="'problem_correction'"/>
  </xsl:call-template>
  <xsl:text>&br;&sp;GROUP BY</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$wks-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>, </xsl:text>
  <xsl:value-of select="$ann-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.the_user"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template name="db-views.user_worksheet_grades">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.user_worksheet_grades"/>
  <xsl:variable name="usr-wks-e-alias" select="'usr_wks_e'"/>
  <xsl:variable name="usr-wks-c-alias" select="'usr_wks_c'"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$usr-wks-e-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$usr-wks-e-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.user_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$usr-wks-e-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.num_edited"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$usr-wks-c-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.num_corrected"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$usr-wks-c-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.grade"/>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_worksheet_edited"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$usr-wks-e-alias"/>
  <xsl:text>&br;&sp;LEFT OUTER JOIN</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_worksheet_corrected"/>
  <xsl:text> AS </xsl:text>
  <xsl:value-of select="$usr-wks-c-alias"/>
  <xsl:text>&br;&sp;ON</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$usr-wks-e-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.user_id"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$usr-wks-c-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.user_id"/>
  <xsl:text>&br;&sp;AND</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$usr-wks-e-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_id"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$usr-wks-c-alias"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.worksheet_id"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating views for tutorial occupancy and extended tutorial data
   =====================================================================================
-->

<xsl:template name="db-views.tutorial_occupancies">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.tutorial_occupancies"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.tutorial"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>count(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.member"/>
  <xsl:text>) AS </xsl:text>
  <xsl:value-of select="$db-notions.column-name.occupancy"/>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorial_members"/>
  <xsl:text>&br;&sp;GROUP BY</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.tutorial"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<xsl:template name="db-views.tutorials_ext">
  <xsl:text>&br;</xsl:text>
  <xsl:variable name="name" select="$db-notions.table-name.tutorials_ext"/>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating view ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE VIEW </xsl:text>
  <xsl:value-of select="$name"/>
  <xsl:text> AS&br;</xsl:text>
  <xsl:text>&sp;SELECT&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorials"/>
  <xsl:text>.*</xsl:text>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorial_occupancies"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.occupancy"/>
  <xsl:text>&br;&sp;FROM</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorials"/>
  <xsl:text>&br;&sp;LEFT OUTER JOIN</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorial_occupancies"/>
  <xsl:text>&br;&sp;ON</xsl:text>
  <xsl:text>&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorials"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> = </xsl:text>
  <xsl:value-of select="$db-notions.table-name.tutorial_occupancies"/>
  <xsl:text>.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.tutorial"/>
  <xsl:text>;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating View of Content Objects.
   =====================================================================================
   This is a union of common metadata of all Documents and Pseudodocuments
-->

<xsl:template name="db-views.content-objects">
  <xsl:param name="only-latest"/>
  <xsl:variable name="name">
    <xsl:if test="$only-latest='true'">
      <xsl:value-of select="'latest_'"/>
    </xsl:if>
    <xsl:call-template name="config.db-table.name">
      <xsl:with-param name="name" select="'content_objects'"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating View ',$name)"/>
  </xsl:call-template>
  <xsl:text>CREATE OR REPLACE VIEW </xsl:text><xsl:value-of select="$name"/><xsl:text>&br;(</xsl:text>
  <xsl:text>&br;</xsl:text>
  <!-- the columns -->
  <xsl:value-of select="$db-notions.column-name.doc_type"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.pure_name"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.thumbnail"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.info_page"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.last_modified"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.created"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.deleted"/><xsl:text>,&br;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.hide"/><xsl:text>&br;</xsl:text>
  <xsl:text>)&br;</xsl:text>
  <xsl:text> AS SELECT </xsl:text>
  <!-- sections first -->
  <!-- the columns -->
  <!-- type -->
  <xsl:text>'</xsl:text><xsl:value-of select="$xml-notions.element-name.section"/><xsl:text>' AS "type", </xsl:text>
  <!-- id -->
  <xsl:value-of select="$db-notions.column-name.id"/><xsl:text>,</xsl:text>
  <!-- pure_name -->
  <xsl:value-of select="$db-notions.column-name.pure_name"/><xsl:text>,</xsl:text>
  <!-- name -->
  <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>,</xsl:text>
  <!-- category -->
  <xsl:text>NULL::int AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.category"/><xsl:text>",</xsl:text>
  <!-- thumbnail -->
  <xsl:text>NULL::int AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.thumbnail"/><xsl:text>",</xsl:text>
  <!-- info_page -->
  <xsl:text>NULL::int AS "</xsl:text>
  <xsl:value-of select="$db-notions.column-name.info_page"/><xsl:text>",</xsl:text>
  <!-- description -->
  <xsl:value-of select="$db-notions.column-name.description"/><xsl:text>,</xsl:text>
  <!-- contained_in -->
  <xsl:value-of select="$db-notions.column-name.contained_in"/><xsl:text>,</xsl:text>
  <!-- last_modified -->
  <xsl:value-of select="$db-notions.column-name.last_modified"/><xsl:text>,</xsl:text>
  <!-- created -->
  <xsl:value-of select="$db-notions.column-name.created"/><xsl:text>,</xsl:text>
  <!-- deleted -->
  <xsl:value-of select="$db-notions.column-name.deleted"/><xsl:text>,&br;</xsl:text>
  <!-- hide -->
  <xsl:value-of select="$db-notions.column-name.hide"/><xsl:text>&br;</xsl:text>
  <xsl:text> FROM </xsl:text><xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;</xsl:text>

  <!-- documents -->
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:variable name="has-category">
      <xsl:call-template name="config.document-type.has-category"/>
    </xsl:variable>

    <xsl:text>UNION&br;</xsl:text>
    <xsl:text>SELECT </xsl:text>
    
    <!-- type -->
    <xsl:text>'</xsl:text>
    <xsl:call-template name="config.document-type.xml-element-name">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>
    <xsl:text>' AS "type", </xsl:text>
    <!-- id -->
    <xsl:value-of select="$db-notions.column-name.id"/><xsl:text>,</xsl:text>
    <!-- pure_name -->
    <xsl:value-of select="$db-notions.column-name.pure_name"/><xsl:text>,</xsl:text>
    <!-- name -->
    <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>,</xsl:text>
    <!-- category -->
    <xsl:choose>
      <xsl:when test="$has-category='yes'">
        <xsl:value-of select="$db-notions.column-name.category"/><xsl:text>,</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>NULL::int AS "</xsl:text>
        <xsl:value-of select="$db-notions.column-name.category"/><xsl:text>",</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- thumbnail -->
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:value-of select="$db-notions.column-name.thumbnail"/><xsl:text>,</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>NULL::int AS "</xsl:text>
        <xsl:value-of select="$db-notions.column-name.thumbnail"/><xsl:text>",</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- info_page -->
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:value-of select="$db-notions.column-name.info_page"/><xsl:text>,</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>NULL::int AS "</xsl:text>
        <xsl:value-of select="$db-notions.column-name.info_page"/><xsl:text>",</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- description -->
    <xsl:value-of select="$db-notions.column-name.description"/><xsl:text>,</xsl:text>
    <!-- contained_in -->
    <xsl:value-of select="$db-notions.column-name.contained_in"/><xsl:text>,</xsl:text>
    <!-- last_modified -->
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:value-of select="$db-notions.column-name.last_modified"/><xsl:text>,</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>NULL::timestamp AS "</xsl:text>
        <xsl:value-of select="$db-notions.column-name.last_modified"/><xsl:text>",</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- created -->
    <xsl:value-of select="$db-notions.column-name.created"/><xsl:text>,</xsl:text>
    <!-- deleted -->
    <xsl:choose>
      <xsl:when test="$is-generic='no'">
        <xsl:value-of select="$db-notions.column-name.deleted"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>NULL::boolean AS "</xsl:text>
        <xsl:value-of select="$db-notions.column-name.deleted"/><xsl:text>"</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>,</xsl:text>
    <!-- hide -->
    <xsl:value-of select="$db-notions.column-name.hide"/>
    <xsl:text> FROM </xsl:text>
    <xsl:choose>
      <xsl:when test="$only-latest = 'true' and $is-generic = 'no'">
        <xsl:call-template name="config.db-view-name.latest-document"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="config.db-table-name.document"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>&br;</xsl:text>
  </xsl:for-each>

  <!-- pseudo-documents -->
  <xsl:for-each select="/*/pseudodoc-types/pseudodoc-type[@name != 'section']">

    <xsl:text>UNION&br;</xsl:text>
    <xsl:text>SELECT </xsl:text>
    
    <!-- type -->
    <xsl:text>'</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>' AS "type", </xsl:text>
    <!-- id -->
    <xsl:value-of select="$db-notions.column-name.id"/><xsl:text>,</xsl:text>
    <!-- pure_name -->
    <xsl:value-of select="$db-notions.column-name.pure_name"/><xsl:text>,</xsl:text>
    <!-- name -->
    <xsl:choose>
      <xsl:when test="@name='user'">
        <xsl:text>(</xsl:text><xsl:value-of select="$db-notions.column-name.first_name"/><xsl:text> || ' ' || </xsl:text><xsl:value-of select="$db-notions.column-name.surname"/>
        <xsl:text>) AS "</xsl:text>
        <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>"</xsl:text>
        <xsl:text>,</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>,</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- category -->
    <xsl:text>NULL::int AS "</xsl:text>
    <xsl:value-of select="$db-notions.column-name.category"/><xsl:text>",</xsl:text>
    <!-- thumbnail -->
    <xsl:text>NULL::int AS "</xsl:text>
    <xsl:value-of select="$db-notions.column-name.thumbnail"/><xsl:text>",</xsl:text>
    <!-- info_page -->
    <xsl:text>NULL::int AS "</xsl:text>
    <xsl:value-of select="$db-notions.column-name.info_page"/><xsl:text>",</xsl:text>
    <!-- description -->
    <xsl:value-of select="$db-notions.column-name.description"/><xsl:text>,</xsl:text>
    <!-- contained_in -->
    <xsl:value-of select="$db-notions.column-name.contained_in"/><xsl:text>,</xsl:text>
    <!-- last_modified -->
    <xsl:value-of select="$db-notions.column-name.last_modified"/><xsl:text>,</xsl:text>
    <!-- created -->
    <xsl:value-of select="$db-notions.column-name.created"/><xsl:text>,</xsl:text>
    <!-- deleted -->
    <xsl:value-of select="$db-notions.column-name.deleted"/><xsl:text>,</xsl:text>
    <!-- hide -->
    <xsl:value-of select="$db-notions.column-name.hide"/>
    <xsl:text> FROM </xsl:text><xsl:call-template name="config.db-table-name.document"/>
    <xsl:text>&br;</xsl:text>

  </xsl:for-each>
  <xsl:text>;&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Main template
   =====================================================================================
-->

<xsl:template match="/*">

  <xsl:call-template name="config.sql-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text>SQL for creation of database tables.</xsl:text>
    </xsl:with-param>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Sections table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.sections-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Themes table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.themes-table"/>



  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Languages table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.languages-table"/>


  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Usergroups table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.user_groups-table"/>

  <xsl:text>&br;</xsl:text>


  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Users table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.users-table"/>


  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Usergroup members table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.user_group_members-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Categories table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.categories-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Reference types table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.reference-types-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Media types table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.media-types-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Semesters table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.semesters-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- classes table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.classes-table"/>

  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- tutorials table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.tutorials-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Version control threads table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.vc-threads-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Document tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.document-tables"/>

  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Reference tables (documents - documents)&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.refs-document-document-tables"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- triggers to modify parents of modified recursive documents&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.modify-parents"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- triggers to modify parent section of added or updated (pseudo-)documents&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.modify-parent-section">
    <xsl:with-param name="on-operation" select="'INSERT'"/>
  </xsl:call-template>
  <xsl:call-template name="db-core.modify-parent-section">
    <xsl:with-param name="on-operation" select="'UPDATE'"/>
  </xsl:call-template>
  <xsl:call-template name="db-core.modify-parent-section">
    <xsl:with-param name="on-operation" select="'DELETE'"/>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>

  <xsl:call-template name="db-code.set-last-modified"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Reference constraint triggers&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.reference-constraints"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Annotation types table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.annotation-types-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- reference attribute columns&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.ref-attributes"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Single Document Annotations tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.single-annotations"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Multi Document Annotations tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.annotations"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Text-sources table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.text_sources-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Binary-sources table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.binary_sources-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Junction tables documents-text-sources&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.refs-document-source-tables">
    <xsl:with-param name="format" select="'text'"/>
  </xsl:call-template>

  <xsl:text>-- Junction tables documents-binary-sources&br;</xsl:text> 
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.refs-document-source-tables">
    <xsl:with-param name="format" select="'binary'"/>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Generic Document IMplementation (GDIM) tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.gdim-document-tables"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Document author tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.document-authors-tables"/>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- class_lecturers table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.class-lecturers-table"/>

  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- tutorial-members table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.tutorial-members-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Write permissions tables for documents&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.write-permissions-tables"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Write permissions tables for pseudo-documents&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.write-permissions-tables.pseudo-document"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Read permissions tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.read-permissions-tables"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Functions&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.function.section_id_for_path"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Initial pseudo-documents&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <!-- Root section: -->
  <xsl:call-template name="db-core.insert-into.sections">
    <xsl:with-param name="id" select="$db-core.rootsection.id"/>
    <xsl:with-param name="pure_name" select="$db-core.rootsection.pure_name"/>
    <xsl:with-param name="name" select="$db-core.rootsection.name"/>
    <xsl:with-param name="description" select="'Root of the checkin tree'"/>
    <xsl:with-param name="contained_in" select="$db-core.rootsection.id"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Adjust sections id sequence: -->
  <xsl:call-template name="db-core.init-table.set-sequence">
    <xsl:with-param name="table-name" select="$db-notions.table-name.sections"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Section org: -->
  <xsl:call-template name="db-core.insert-into.sections">
    <xsl:with-param name="pure_name" select="'org'"/>
    <xsl:with-param name="description">
      (Pseudo-)documents for organizational purporses (user, user groups,etc.)
    </xsl:with-param>
    <xsl:with-param name="contained_in" select="$db-core.rootsection.id"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Section org/users: -->
  <xsl:call-template name="db-core.insert-into.sections">
    <xsl:with-param name="pure_name" select="'users'"/>
    <xsl:with-param name="description" select="'Users'"/>
    <xsl:with-param name="contained_in.path" select="'org'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Section org/user_groups: -->
  <xsl:call-template name="db-core.insert-into.sections">
    <xsl:with-param name="pure_name" select="'user_groups'"/>
    <xsl:with-param name="description" select="'User groups'"/>
    <xsl:with-param name="contained_in.path" select="'org'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Admin user group: -->
  <xsl:call-template name="db-core.insert-into.user_groups">
    <xsl:with-param name="id" select="$db-core.admin-group.id"/>
    <xsl:with-param name="pure_name" select="$db-core.admin-group.pure_name"/>
    <xsl:with-param name="name" select="$db-core.admin-group.name"/>
    <xsl:with-param name="description" select="$db-core.admin-group.description"/>
    <xsl:with-param name="contained_in.path" select="'org/user_groups'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Adjust user group id sequence: -->
  <xsl:call-template name="db-core.init-table.set-sequence">
    <xsl:with-param name="table-name" select="$db-notions.table-name.user_groups"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Section system: -->
  <xsl:call-template name="db-core.insert-into.sections">
    <xsl:with-param name="pure_name" select="'system'"/>
    <xsl:with-param name="description">
      (Pseudo-)documents related to the technology and architacture of Japs, (themes,
      languages, XSL and CSS stylesheets, etc.).
    </xsl:with-param>
    <xsl:with-param name="contained_in" select="$db-core.rootsection.id"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Section system/themes: -->
  <xsl:call-template name="db-core.insert-into.sections">
    <xsl:with-param name="pure_name" select="'themes'"/>
    <xsl:with-param name="description" select="'Themes'"/>
    <xsl:with-param name="contained_in.path" select="'system'"/>
  </xsl:call-template>
  <!-- Section system/languages: -->
  <xsl:call-template name="db-core.insert-into.sections">
    <xsl:with-param name="pure_name" select="'languages'"/>
    <xsl:with-param name="description" select="'Languages'"/>
    <xsl:with-param name="contained_in.path" select="'system'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Default theme -->
  <xsl:call-template name="db-core.insert-into.themes">
    <xsl:with-param name="id" select="$db-core.default-theme.id"/>
    <xsl:with-param name="pure_name" select="$db-core.default-theme.pure_name"/>
    <xsl:with-param name="name" select="$db-core.default-theme.name"/>
    <xsl:with-param name="description" select="'Default Theme'"/>
    <xsl:with-param name="contained_in.path" select="'system/themes'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Adjust themes id sequence: -->
  <xsl:call-template name="db-core.init-table.set-sequence">
    <xsl:with-param name="table-name" select="$db-notions.table-name.themes"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Default language: -->
  <xsl:call-template name="db-core.insert-into.languages">
    <xsl:with-param name="id" select="$db-core.default-language.id"/>
    <xsl:with-param name="pure_name" select="$db-core.default-language.pure_name"/>
    <xsl:with-param name="name" select="$db-core.default-language.name"/>
    <xsl:with-param name="description" select="'Default Language'"/>
    <xsl:with-param name="contained_in.path" select="'system/languages'"/>
    <xsl:with-param name="code" select="$db-core.default-language.code"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Neutral language: -->
  <xsl:call-template name="db-core.insert-into.languages">
    <xsl:with-param name="id" select="$db-core.neutral-language.id"/>
    <xsl:with-param name="pure_name" select="$db-core.neutral-language.pure_name"/>
    <xsl:with-param name="name" select="$db-core.neutral-language.name"/>
    <xsl:with-param name="description" select="'No Language'"/>
    <xsl:with-param name="contained_in.path" select="'system/languages'"/>
    <xsl:with-param name="code" select="$db-core.neutral-language.code"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Adjust languages id sequence: -->
  <xsl:call-template name="db-core.init-table.set-sequence">
    <xsl:with-param name="table-name" select="$db-notions.table-name.languages"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Admin user: -->
  <xsl:call-template name="db-core.insert-into.users">
    <xsl:with-param name="id" select="$db-core.admin-user.id"/>
    <xsl:with-param name="pure_name" select="$db-core.admin-user.pure_name"/>
    <xsl:with-param name="login_name" select="$db-core.admin-user.login_name"/>
    <xsl:with-param name="surname" select="$db-core.admin-user.surname"/>
    <xsl:with-param name="description" select="$db-core.admin-user.description"/>
    <xsl:with-param name="theme" select="$db-core.admin-user.theme"/>
    <xsl:with-param name="language" select="$db-core.admin-user.language"/>
    <xsl:with-param name="password" select="$db-core.admin-user.password"/>
    <xsl:with-param name="contained_in.path" select="'org/users'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Adjust user id sequence: -->
  <xsl:call-template name="db-core.init-table.set-sequence">
    <xsl:with-param name="table-name" select="$db-notions.table-name.users"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- Put admin user in admin user group: -->
  <xsl:call-template name="db-core.insert-into.user_group_members">
    <xsl:with-param name="user_group" select="$db-core.admin-group.id"/>
    <xsl:with-param name="member" select="$db-core.admin-user.id"/>
    <xsl:with-param name="admin" select="'true'"/>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'section'"/>
    <xsl:with-param name="id" select="$db-core.rootsection.id"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'section'"/>
    <xsl:with-param name="pure_name" select="'org'"/>
    <xsl:with-param name="contained_in" select="$db-core.rootsection.id"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'section'"/>
    <xsl:with-param name="pure_name" select="'users'"/>
    <xsl:with-param name="contained_in.path" select="'org'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'section'"/>
    <xsl:with-param name="pure_name" select="'user_groups'"/>
    <xsl:with-param name="contained_in.path" select="'org'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'section'"/>
    <xsl:with-param name="pure_name" select="'system'"/>
    <xsl:with-param name="contained_in" select="$db-core.rootsection.id"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'section'"/>
    <xsl:with-param name="pure_name" select="'languages'"/>
    <xsl:with-param name="contained_in.path" select="'system'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'section'"/>
    <xsl:with-param name="pure_name" select="'themes'"/>
    <xsl:with-param name="contained_in.path" select="'system'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'user_group'"/>
    <xsl:with-param name="id" select="$db-core.admin-group.id"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'theme'"/>
    <xsl:with-param name="pure_name" select="$db-core.default-theme.pure_name"/>
    <xsl:with-param name="contained_in.path" select="'system/themes'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'language'"/>
    <xsl:with-param name="pure_name" select="$db-core.default-language.pure_name"/>
    <xsl:with-param name="contained_in.path" select="'system/languages'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'language'"/>
    <xsl:with-param name="pure_name" select="$db-core.neutral-language.pure_name"/>
    <xsl:with-param name="contained_in.path" select="'system/languages'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.admin-permission.pseudo-document">
    <xsl:with-param name="type" select="'user'"/>
    <xsl:with-param name="id" select="$db-core.admin-user.id"/>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="config.sql-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:text>SQL for creation of database Views.</xsl:text>
    </xsl:with-param>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="'Creating auto-coded Views'"/>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- detailed Usergroup members View&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.members_detailed"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Latest Document views&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.latest-docs"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Document member views&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.document-members"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Content Objects View&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.content-objects"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Classes and Courses View&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.classes_and_courses"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Content Objects View (latest)&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.content-objects">
    <xsl:with-param name="only-latest" select="'true'"/>
  </xsl:call-template>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- assigned problems view&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.assigned-problems"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- problem contex view&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.problem_context"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- worksheet contex view&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.worksheet_context"/>

  <xsl:text>&br;</xsl:text>
  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- tutorial occupancy and extended tutorial data&br;</xsl:text>
  <xsl:call-template name="db-views.tutorial_occupancies"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.tutorials_ext"/>

  <xsl:text>&br;</xsl:text>
  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- user worksheet grades view and auxiliary views&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.user_worksheet_edited"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.user_worksheet_corrected"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-views.user_worksheet_grades"/>

</xsl:template>

</xsl:stylesheet>
