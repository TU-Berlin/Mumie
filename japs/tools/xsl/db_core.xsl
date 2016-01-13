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

   $Id: db_core.xsl,v 1.110 2007/07/11 15:38:59 grudzin Exp $
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                xmlns:x-encrypt="xalan://net.mumie.japs.tools.ant.util.PasswordEncryptorWrapper"
                xmlns:x-sql="xalan://net.mumie.xslt.ext.SQLUtil"
                version="1.0">

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:include href="config.xsl"/>
<xsl:include href="db_notions.xsl"/>

<!--
   =====================================================================================
   Parameters and variables
   =====================================================================================
-->

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
        <xsl:value-of select="'&lt;empty/&gt;'"/>
      </xsl:otherwise>
    </xsl:choose>  
  </xsl:param>

<xsl:param name="db-core.rootpassword">
  <xsl:choose>
    <xsl:when test="/*/default-admin-password">
      <xsl:value-of select="/*/default-admin-password/text()"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="'mumie'"/>
    </xsl:otherwise>
  </xsl:choose>  
</xsl:param>

<xsl:param name="db-core.pure-sql" select="'no'"/>

<!-- DEPRECATED
<xsl:param name="db-core.root-section-pure_name">ROOTSECTION</xsl:param>

<xsl:param name="db-core.root-section-name">ROOTSECTION</xsl:param>

<xsl:param name="db-core.root-section-insert-rule-name">root_section_ins</xsl:param>

<xsl:param name="db-core.root-section-update-rule-name">root_section_upd</xsl:param>

<xsl:param name="db-core.root-section-delete-rule-name">root_section_del</xsl:param>
-->

<xsl:param name="db-core.default-theme-update-rule-name">default_theme_upd</xsl:param>

<xsl:param name="db-core.default-theme-delete-rule-name">default_theme_del</xsl:param>

<!--
   =====================================================================================
   Auxiliaries
   =====================================================================================
-->

<xsl:template name="db-core.echo">
  <xsl:param name="message"/>
  <xsl:if test="not(x-util:getBoolean($db-core.pure-sql))">
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
  <xsl:text> varchar(</xsl:text>
  <xsl:value-of select="$db-notions.constant-value.name_length"/>
  <xsl:text>) NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.filename">
  <xsl:value-of select="$db-notions.column-name.filename"/>
  <xsl:text> varchar(</xsl:text>
  <xsl:value-of select="$db-notions.constant-value.filename_length"/>
  <xsl:text>) UNIQUE NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.login_name">
  <xsl:value-of select="$db-notions.column-name.login_name"/>
  <xsl:text> text UNIQUE NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.password">
  <xsl:value-of select="$db-notions.column-name.password"/>
  <xsl:text> text NOT NULL</xsl:text>
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

<xsl:template name="db-core.column.contained_in">
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

  <!-- provisional hack ! Fix online-checkin to become a clean proper database.
  -->
<xsl:template name="db-core.column.contained_in.null-allowed">
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text> int DEFAULT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.contained_in.deferrable">
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<!-- new ref style (not in use yet)
<xsl:template name="db-core.column.contained_in_document">
  <xsl:param name="container.doctype.name" select="@document-type"/>
  <xsl:param name="required" select="@required"/>
  <xsl:call-template name="config.db-column.contained-in-document.name">
    <xsl:with-param name="container.doctype.name" select="$container.doctype.name"/>
  </xsl:call-template>
  <xsl:text> int</xsl:text>
  <xsl:if test="$required='yes'">
    <xsl:text> NOT NULL</xsl:text>
  </xsl:if>
  <xsl:text> REFERENCES </xsl:text>
  <xsl:call-template name="config.db-table-name.document">
    <xsl:with-param name="doctype" select="$container.doctype.name"/>
  </xsl:call-template>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template> -->

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
  <xsl:text>) DEFAULT 0</xsl:text>
<!-- FIXME: diese hard-coded "0" ist vielleicht gefaehrlich.
            Siehe auch config.xml <semesters>, <statuses> etc.  -->
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

<xsl:template name="db-core.column.manual">
  <xsl:value-of select="$db-notions.column-name.manual"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.pages"/>
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
  <xsl:text>)</xsl:text>
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

<xsl:template name="db-core.column.status">
  <xsl:value-of select="$db-notions.column-name.status"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.statuses"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFAULT 0</xsl:text>
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

<xsl:template name="db-core.column.role">
  <xsl:value-of select="$db-notions.column-name.role"/>
  <xsl:text> int DEFAULT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.user_roles"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="db-core.column.theme">
  <xsl:value-of select="$db-notions.column-name.theme"/>
  <xsl:text> int NOT NULL DEFAULT 0 REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.themes"/>
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

<!--DEPRECATED-->
<xsl:template name="db-core.column.admin">
  <xsl:value-of select="$db-notions.column-name.admin"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>) DEFAULT 0</xsl:text>
</xsl:template>

<!--DEPRECATED
<xsl:template name="db-core.column.read-default">
  <xsl:value-of select="$db-notions.column-name.read_default"/>
  <xsl:text> BOOLEAN DEFAULT FALSE</xsl:text>
</xsl:template>
-->

<!--DEPRECATED
<xsl:template name="db-core.column.write-default">
  <xsl:value-of select="$db-notions.column-name.write_default"/>
  <xsl:text> BOOLEAN DEFAULT FALSE</xsl:text>
</xsl:template>
-->

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

<xsl:template name="db-core.column.course_subsection">
  <xsl:value-of select="$db-notions.column-name.course_subsection"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-notions.table-name.course_subsections"/>
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
  <xsl:text> boolean NOT NULL DEFAULT FALSE</xsl:text>
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
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
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
    <xsl:value-of select="x-sql:stringLiteral(string($description))"/>
    <xsl:text>;&br;</xsl:text>
  </xsl:if>
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
  <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.theme"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.first_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.surname"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.email"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_login"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.custom_metainfo"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'users'"/>
  </xsl:call-template>
<!--DEPRECATED-->
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.users"/>
  <xsl:text>(&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.login_name"/>
<!--
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.first_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.surname"/>
-->
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.password"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.theme"/>
    <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
  <xsl:text>&sp;&sp;VALUES (0,'ROOTADMIN','Admin',</xsl:text>
  <xsl:value-of select="x-sql:stringLiteral(x-encrypt:encrypt($db-core.rootpassword))"/>
  <xsl:text>,'Administrator of the ROOTSECTION',0);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
</xsl:template>

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
  <xsl:call-template name="db-core.column.first_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
<!-- DEPRECATED
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.admin"/>
-->
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.custom_metainfo"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.contained_in.deferrable"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'sections'"/>
  </xsl:call-template>
<!--DEPRECATED
TODO: dieses codestueck muss vielleicht trotzdem ausgefuehrt werden,
 aber erst nach dem build-checkin und mit dem Parameter
 "id des checkinverzeichnisses"
 statt hard-coded "0".
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.sections"/>
  <xsl:text>&br;&sp;(&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.contained_in"/>
  <xsl:text>&br;&sp;)&br;&sp;</xsl:text>
  <xsl:text>VALUES (&br;</xsl:text>
  <xsl:text>&sp;&sp;0,&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text>
  <xsl:value-of select="$db-core.root-section-pure_name"/>
  <xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'</xsl:text>
  <xsl:value-of select="$db-core.root-section-name"/>
  <xsl:text>',&br;</xsl:text>
  <xsl:text>&sp;&sp;'Virtual top-level section',&br;</xsl:text>
  <xsl:text>&sp;&sp;0&br;</xsl:text>
  <xsl:text>&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
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
-->
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
   Creating and initializing statuses table
   =====================================================================================
-->

<xsl:template name="db-core.statuses-table">
  <xsl:call-template name="db-core.echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-notions.table-name.statuses)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-notions.table-name.statuses"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.id">
    <xsl:with-param name="type" select="'int'"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'statuses'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:for-each select="/*/statuses/status">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$db-notions.table-name.statuses"/>
    <xsl:text>&br;&sp;VALUES (</xsl:text>
    <xsl:call-template name="config.status.code"/>
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
   Creating and initializing user roles table
   =====================================================================================
-->

<xsl:template name="db-core.user-roles-table">
  <xsl:variable name="table-name" select="$db-notions.table-name.user_roles"/>
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
  <xsl:for-each select="/*/user-roles/user-role">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$table-name"/>
    <xsl:text>&br;&sp;VALUES (</xsl:text>
    <xsl:call-template name="config.user-role.code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@name"/>
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
  <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <!-- DEPRECATED:
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.start_time"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.end_time"/>
  -->
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="$this.table-name"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <!-- DEPRECATED:
  <xsl:for-each select="/*/semesters/semester">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$this.table-name"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:value-of select="$db-notions.column-name.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:value-of select="$db-notions.column-name.pure_name"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:value-of select="$db-notions.column-name.name"/>
    <xsl:text>&br;&sp;)&br;</xsl:text>
    <xsl:text>&br;&sp;VALUES (</xsl:text>
    <xsl:call-template name="config.semester.code"/>
    <xsl:text>,&br;&sp;'</xsl:text>
    <xsl:value-of select="$db-core.root-section-pure_name"/>
    <xsl:text>',&br;&sp;</xsl:text>
    <xsl:text>'</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
  -->
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
  <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.semester"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
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
  <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.tutor"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.class"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'tutorials'"/>
  </xsl:call-template>
  
</xsl:template>

<!--
   =====================================================================================
   Creating version control thread table
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
   Creating theme table
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
  <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'themes'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.themes"/>
  <xsl:text>(</xsl:text>
    <!-- id -->
    <xsl:value-of select="$db-notions.column-name.id"/>
    <!-- raw filename -->
    <xsl:text>,</xsl:text>
    <xsl:value-of select="$db-notions.column-name.pure_name"/>
    <!-- name -->
    <xsl:text>,</xsl:text>
    <xsl:value-of select="$db-notions.column-name.name"/>
    <!-- description -->
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:value-of select="$db-notions.column-name.description"/>
  <xsl:text>)</xsl:text>
   <xsl:text>&br;&sp;VALUES (0, 'theme', 'Default theme', 'Default and fallback theme');&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>CREATE RULE </xsl:text>
  <xsl:value-of select="$db-core.default-theme-update-rule-name"/>
  <xsl:text> AS ON UPDATE TO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.themes"/>
  <xsl:text>&br;&sp;WHERE old.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> = 0&br;</xsl:text>
  <xsl:text>&sp;DO INSTEAD NOTHING;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>COMMENT ON RULE </xsl:text>
  <xsl:value-of select="$db-core.default-theme-update-rule-name"/>
  <xsl:text> IS&br;</xsl:text>
  <xsl:text>&sp;'Ensures the default theme is not changed';&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>CREATE RULE </xsl:text>
  <xsl:value-of select="$db-core.default-theme-delete-rule-name"/>
  <xsl:text> AS ON DELETE TO </xsl:text>
  <xsl:value-of select="$db-notions.table-name.themes"/>
  <xsl:text>&br;&sp;WHERE old.</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/>
  <xsl:text> = 0&br;</xsl:text>
  <xsl:text>&sp;DO INSTEAD NOTHING;&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:text>COMMENT ON RULE </xsl:text>
  <xsl:value-of select="$db-core.default-theme-delete-rule-name"/>
  <xsl:text> IS&br;</xsl:text>
  <xsl:text>&sp;'Ensures the root theme is not deleted';&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
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
        <xsl:if test="@has-qualified-name='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.qualified-name"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.copyright"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.created"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.last_modified"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
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
        <xsl:call-template name="db-core.column.manual"/>
        <!-- new ref style (not in use yet)
        <xsl:for-each select="./containable_in[@unique='yes']">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.contained_in_document"/>
        </xsl:for-each>
        -->
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.status"/>
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
<!--    <xsl:value-of select="$name"/>
        <xsl:text>.</xsl:text> -->
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
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.pure_name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.description"/>
        <xsl:if test="@has-qualified-name='yes'">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="db-core.column.qualified-name"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
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
    <xsl:value-of select="x-sql:stringLiteral($description)"/>
    <xsl:text>;&br;</xsl:text>

 </xsl:for-each>
</xsl:template>


<!--
   =====================================================================================
   Creating views containing only documents, that are latest in their vc_thread
   =====================================================================================
-->

<xsl:template name="db-core.create-views">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <xsl:if test="$is-generic='no'">
      <xsl:variable name="view-name">
        <xsl:call-template name="config.db-view-name.document"/>
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
   Inserting the root_course_section (deprecated)
   =====================================================================================
-->
<!-- FIXME: should be a course instead  -->

<xsl:template name="db-core.insert-root-course-section">
  <xsl:text>INSERT INTO </xsl:text>
  <xsl:call-template name="config.db-table-name.document">
    <xsl:with-param name="doctype" select="'course_section'"/>
  </xsl:call-template>
  <xsl:text>(&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.id"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.name"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.description"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.copyright"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.content"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.content_type"/><xsl:text>,&br;&sp;</xsl:text>
  <xsl:value-of select="$db-notions.column-name.content_length"/>
  <xsl:text>&br;)&br;</xsl:text>
  <xsl:text>VALUES&br;(&br;&sp;</xsl:text>
    <xsl:value-of select="0"/><xsl:text>,&br;&sp;</xsl:text>
    <xsl:text>'root_course_section',&br;&sp;</xsl:text>
    <xsl:text>'Container for all &quot;Top Level Coursesection''s&quot; (TLK). Note, that it has hard-coded the id &quot;0&quot;.&br; A TLK is one that is NOT contained in any other Coursesection. It is viewed as the top of the tree for a single course. Technically it is specified as &br;A: being contained in this root_course_section as a component and NOT being contained in any other &br;or &br;B: containing this root_course_section as a component.',&br;&sp;</xsl:text>
    <xsl:text>'Copyright (C) 2003, ??Berlin University of Technology / TU Muenchen??',&br;&sp;</xsl:text>
    <xsl:text>'&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;mumie:course_section&gt;&lt;/mumie:course_section&gt;',&br;&sp;</xsl:text>
    <xsl:call-template name="config.media-type.code">
      <xsl:with-param name="type" select="'text'"/>
      <xsl:with-param name="subtype" select="'plain'"/>
    </xsl:call-template>
    <xsl:text>,&br;&sp;</xsl:text>
    <xsl:value-of select="82"/><xsl:text>&br;</xsl:text>
  <xsl:text>);&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating reference tables
   =====================================================================================
-->

<xsl:template name="db-core.refs-document-document-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="from-doctype" select="@name"/>
    <xsl:variable name="refs-to">
      <xsl:call-template name="config.document-type.refs-to"/>
    </xsl:variable>
    <xsl:variable name="no-refs-to">
      <xsl:call-template name="config.document-type.no-refs-to"/>
    </xsl:variable>
    <xsl:variable name="is-generic">
      <xsl:call-template name="config.document-type.is-generic"/>
    </xsl:variable>
    <!-- new container style (not in use yet)
    <xsl:variable name="containable-in" select="./containable_in"/>
    -->
    <xsl:if test="$is-generic='no' and $no-refs-to!='*'">
      <xsl:if test="position()&gt;1">
        <xsl:text>&br;</xsl:text>
      </xsl:if>
      <xsl:for-each select="/*/document-types/document-type[
                    ($refs-to='*' or x-util:listContains(string($refs-to), string(@name)))
                    and not(x-util:listContains(string($no-refs-to),string(@name)))]">
        <!-- new container style (not in use yet)
        <xsl:variable name="to-doctype" select="@name"/>
         -->
        <xsl:if test="position()&gt;1">
          <xsl:text>&br;</xsl:text>
        </xsl:if>
        <xsl:variable name="name">
          <xsl:call-template name="config.db-table-name.refs-document-document">
            <xsl:with-param name="from-doctype" select="$from-doctype"/>
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
        <!-- new container style (not in use yet)
        <xsl:if test="$containable-in[@document-type=$to-doctype and @unique='no']">
          <xsl:text>, </xsl:text>
          <xsl:call-template name="config.reference-type.code">
            <xsl:with-param name="name" select="'container'"/>
          </xsl:call-template>
        </xsl:if>
        -->
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



      </xsl:for-each>
    </xsl:if>
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
    <xsl:if test="position()&gt;1">
      <xsl:text>&br;</xsl:text>
    </xsl:if>

<!-- modifyParents  -->
    <xsl:variable name="doctype" select="@name"/>
    <xsl:variable name="is-recursive-component">
      <xsl:call-template name="config.document-type.is-recursive-component"/>
    </xsl:variable>
    <xsl:if test="$is-recursive-component='yes'">
      <xsl:text>&br;</xsl:text>

<!-- create function mod_x_parent_func()  -->
      <xsl:variable name="trigger-name">
        <xsl:value-of select="concat(concat('mod_',$doctype),'_parents')"/>
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
        <xsl:variable name="refs-to">
          <xsl:call-template name="config.document-type.refs-to"/>
        </xsl:variable>
        <xsl:variable name="no-refs-to">
          <xsl:call-template name="config.document-type.no-refs-to"/>
        </xsl:variable>
        <xsl:variable name="is-generic">
          <xsl:call-template name="config.document-type.is-generic"/>
        </xsl:variable>
        <xsl:if test="$is-generic='no' and $no-refs-to!='*'">
          <xsl:if test="($refs-to='*' or x-util:listContains(string($refs-to), string($doctype))
                  and not(x-util:listContains(string($no-refs-to),string($doctype))))">
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
              <xsl:with-param name="from-doctype" select="@name"/>
              <xsl:with-param name="to-doctype" select="$doctype"/>
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
        </xsl:if>
      </xsl:for-each>
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
   Creating user_groups table
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
  <xsl:call-template name="db-core.column.contained_in.null-allowed"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.description"/>
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
    <xsl:with-param name="name" select="'user_groups'"/>
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
<!-- DEPRECATED
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.read-default"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="db-core.column.write-default"/>
-->
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:call-template name="db-core.comment-on-table">
    <xsl:with-param name="name" select="'user_group_members'"/>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!--
   =====================================================================================
   Creating write-permissions table
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
</xsl:template>

<!--
   =====================================================================================
   Creating view with memberships and detailed user-data combined
   =====================================================================================
-->

<xsl:template name="db-core.create-views.members_detailed">
  <xsl:variable name="name" select="$db-notions.view-name.user_group_members_detailed"/>
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
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="db-core.column.role"/>
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
   Creating document members views -  FIXME
   =====================================================================================
-->

<xsl:template name="db-core.document-members-views">
  <xsl:for-each select="/*/document-types/document-type[@has-class='yes']">
<!--
    <xsl:variable name="has-members">
      <xsl:call-template name="config.document-type.has-members"/>
    </xsl:variable>
    <xsl:if test="$has-members='yes'">
-->
      <xsl:text>&br;</xsl:text>
      <xsl:variable name="name">
        <xsl:call-template name="config.db-view-name.document-members"/>
      </xsl:variable>
      <xsl:call-template name="db-core.echo">
        <xsl:with-param name="message" select="concat('Creating table ',$name)"/>
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
<!--
    </xsl:if>
-->
  </xsl:for-each>
</xsl:template>

<!--
   =====================================================================================
   Creating document theme map tables
   =====================================================================================
-->

<xsl:template name="db-core.theme-map-document-tables">
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="has-generic-counterpart">
      <xsl:call-template name="config.document-type.has-generic-counterpart"/>
    </xsl:variable>
    <xsl:if test="$has-generic-counterpart='yes'">
      <xsl:variable name="name">
        <xsl:call-template name="config.db-table-name.theme-map-document"/>
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
      <xsl:call-template name="db-core.column.generic_document"/>
      <xsl:text>,&br;</xsl:text>
      <xsl:text>&sp;&sp;UNIQUE (</xsl:text>
      <xsl:value-of select="$db-notions.column-name.theme"/>
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
   Main template
   =====================================================================================
-->

<xsl:template name="db-core.separator">
  <xsl:text>-- ==============================================================</xsl:text>
  <xsl:text>=============&br;</xsl:text>
</xsl:template>

<xsl:template match="/">

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

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Users table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.users-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Usergroups table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.user_groups-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Usergroup members table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.user_group_members-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- detailed Usergroup members View&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.create-views.members_detailed"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Categories table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.categories-table"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Statuses table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.statuses-table"/>

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
  <xsl:text>-- Latest Document views&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.create-views"/>

  <xsl:text>&br;</xsl:text>
<!-- scratch migration
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>- Insert root_course_section&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.insert-root-course-section"/>

  <xsl:text>&br;</xsl:text>
-->
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
  <xsl:text>-- User roles table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.user-roles-table"/>

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
  <xsl:text>-- Theme map document tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.theme-map-document-tables"/>

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
  <xsl:text>-- Document member views&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.document-members-views"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Write permissions table&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.write-permissions-tables"/>

  <xsl:text>&br;</xsl:text>

  <xsl:call-template name="db-core.separator"/>
  <xsl:text>-- Read permissions tables&br;</xsl:text>
  <xsl:call-template name="db-core.separator"/>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="db-core.read-permissions-tables"/>

  <xsl:text>&br;</xsl:text>

</xsl:template>

</xsl:stylesheet>
