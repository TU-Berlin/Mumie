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
   <!ENTITY dash10  "----------">
   <!ENTITY dash20  "&dash10;&dash10;">
   <!ENTITY dash80  "&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;&dash10;">
  ]
>

<!--

   Author:  Tilman Rassy

   $Id: db_main.xsl,v 1.16 2008/01/24 16:29:24 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.db classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dc="http://www.mumie.net/xml-namespace/declarations"
                xmlns:x-lib="xalan://net.mumie.japs.build.XSLExtLib"
                version="1.0">

<xsl:import href="db_table_names.xsl"/>
<xsl:import href="db_column_names.xsl"/>

<xsl:output method="text"/>

<xsl:strip-space elements="*"/>

<!-- ====================================================================== -->
<!-- h1: Global parameters set by the build script                          -->
<!-- ====================================================================== -->

<xsl:param name="root-section.id" select="'0'"/>
<xsl:param name="root-section.pure_name" select="''"/>
<xsl:param name="root-section.name" select="'ROOT'"/>
<xsl:param name="default-theme.id" select="'0'"/>
<xsl:param name="default-theme.pure_name" select="'tme_default'"/>
<xsl:param name="default-theme.name" select="'Default Theme'"/>
<xsl:param name="default-language.id" select="'0'"/>
<xsl:param name="default-language.pure_name" select="'lng_de'"/>
<xsl:param name="default-language.name" select="'Deutsch'"/>
<xsl:param name="default-language.code" select="'de'"/>
<xsl:param name="neutral-language.id" select="'1'"/>
<xsl:param name="neutral-language.pure_name" select="'lng_zxx'"/>
<xsl:param name="neutral-language.name" select="'Neutral'"/>
<xsl:param name="neutral-language.code" select="'zxx'"/>
<xsl:param name="admin-group.id" select="'0'"/>
<xsl:param name="admin-group.pure_name" select="'ugr_admins'"/>
<xsl:param name="admin-group.name" select="'admins'"/>
<xsl:param name="admin-group.description" select="'Usergroup of Administrators'"/>
<xsl:param name="admin-user.id" select="'0'"/>
<xsl:param name="admin-user.pure_name" select="'usr_admin'"/>
<xsl:param name="admin-user.login_name" select="'admin'"/>
<xsl:param name="admin-user.surname" select="'Administrator'"/>
<xsl:param name="admin-user.description" select="'Administrator'"/>
<xsl:param name="admin-user.theme" select="$default-theme.id"/>
<xsl:param name="admin-user.language" select="$default-language.id"/>
<xsl:param name="admin-user.password" select="'mumie'"/>

<!-- ====================================================================== -->
<!-- h1: Other gloabl parameters and variables                              -->
<!-- ====================================================================== -->

<!-- Whether the output shall be pure SQL (no psql "backslash commands");
  allowed values are "yes", "true", "no", "false":  -->
<xsl:param name="pure-sql">no</xsl:param>

<!-- All data entity type declarations: -->
<xsl:variable name="detypes" select="/*/*/dc:document-type|/*/*/dc:pseudo-document-type"/>

<!-- All document type declarations: -->
<xsl:variable name="doctypes" select="/*/*/dc:document-type"/>

<!-- All pseudo-document type declarations: -->
<xsl:variable name="psdoctypes" select="/*/*/dc:pseudo-document-type"/>

<!-- Name of the trigger function to automatically set the last_modified column: -->
<xsl:variable name="set-last-modified-function">update_last_modified</xsl:variable>

<!-- ====================================================================== -->
<!-- h1: Auxiliaries                                                        -->
<!-- ====================================================================== -->

<xsl:template name="echo">
  <xsl:param name="message"/>
  <xsl:if test="not(x-lib:toBoolean($pure-sql))">
    <xsl:text>\qecho '</xsl:text>
    <xsl:value-of select="$message"/>
    <xsl:text>'&br;</xsl:text>
  </xsl:if>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Declaring columns                                                  -->
<!-- ====================================================================== -->

<xsl:template name="column-def.id">
  <xsl:param name="type" select="'serial'"/>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text> </xsl:text>
  <xsl:value-of select="$type"/>
  <xsl:text> PRIMARY KEY</xsl:text>
</xsl:template>

<xsl:template name="column-def.vc_thread">
  <xsl:param name="doctype" select="@name"/>
  <xsl:param name="default"/>
  <xsl:value-of select="$db-column.vc_thread"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="x-lib:dbVCThreadTable($doctypes[@name=$doctype])"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>) NOT NULL</xsl:text>
  <xsl:if test="$default and $default!=''">
    <xsl:text> DEFAULT</xsl:text>
    <xsl:value-of select="$default"/>
  </xsl:if>
</xsl:template>

<xsl:template name="column-def.version">
  <xsl:value-of select="$db-column.version"/>
  <xsl:text> int DEFAULT 1</xsl:text>
</xsl:template>

<xsl:template name="column-def.deleted">
  <xsl:value-of select="$db-column.deleted"/>
  <xsl:text> boolean NOT NULL DEFAULT false</xsl:text>
</xsl:template>

<xsl:template name="column-def.category">
  <xsl:value-of select="$db-column.category"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.categories"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.pure_name">
  <xsl:value-of select="$db-column.pure_name"/>
  <xsl:text> text NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.name">
  <xsl:param name="unique">no</xsl:param>
  <xsl:value-of select="$db-column.name"/>
  <xsl:text> text</xsl:text>
  <xsl:if test="$unique='yes'"> UNIQUE</xsl:if>
  <xsl:text> NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.label">
  <xsl:value-of select="$db-column.label"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.points">
  <xsl:value-of select="$db-column.points"/>
  <xsl:text> real DEFAULT 0</xsl:text>
</xsl:template>

<xsl:template name="column-def.login_name">
  <xsl:value-of select="$db-column.login_name"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.password">
  <xsl:value-of select="$db-column.password"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.first_name">
  <xsl:value-of select="$db-column.first_name"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.surname">
  <xsl:value-of select="$db-column.surname"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.email">
  <xsl:value-of select="$db-column.email"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.description">
  <xsl:value-of select="$db-column.description"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.qualified-name">
  <xsl:value-of select="$db-column.qualified_name"/>
  <xsl:text> text NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.copyright">
  <xsl:value-of select="$db-column.copyright"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.created">
  <xsl:value-of select="$db-column.created"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="column-def.last_modified">
  <xsl:value-of select="$db-column.last_modified"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="column-def.timeframe_start">
  <xsl:value-of select="$db-column.timeframe_start"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="column-def.timeframe_end">
  <xsl:value-of select="$db-column.timeframe_end"/>
  <xsl:text> timestamp DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.last_login">
  <xsl:value-of select="$db-column.last_login"/>
  <xsl:text> timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP</xsl:text>
</xsl:template>

<xsl:template name="column-def.contained_in">
  <xsl:value-of select="$db-column.contained_in"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.contained_in.deferrable">
  <xsl:value-of select="$db-column.contained_in"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<xsl:template name="column-def.width">
  <xsl:value-of select="$db-column.width"/>
  <xsl:text> int</xsl:text>
</xsl:template>

<xsl:template name="column-def.height">
  <xsl:value-of select="$db-column.height"/>
  <xsl:text> int</xsl:text>
</xsl:template>

<xsl:template name="column-def.duration">
  <xsl:value-of select="$db-column.duration"/>
  <xsl:text> int</xsl:text>
</xsl:template>

<xsl:template name="column-def.semester">
  <xsl:value-of select="$db-column.semester"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.semesters"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.content">
  <xsl:param name="format" select="x-lib:format()"/>
  <xsl:value-of select="$db-column.content"/>
  <xsl:choose>
    <xsl:when test="$format='text'"> text NOT NULL</xsl:when>
    <xsl:when test="$format='binary'"> oid UNIQUE</xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">
        <xsl:text>Invalid content format: </xsl:text>
        <xsl:value-of select="$format"/>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text></xsl:text>
</xsl:template>

<xsl:template name="column-def.content_type">
  <xsl:value-of select="$db-column.content_type"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.media_types"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.content_length">
  <xsl:value-of select="$db-column.content_length"/>
  <xsl:text> int NOT NULL DEFAULT -1</xsl:text>
</xsl:template>

<xsl:template name="column-def.summary">
  <xsl:value-of select="$db-column.summary"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.generic_summaries"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.corrector">
  <xsl:value-of select="$db-column.corrector"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.java_classes"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.corrector.deferrable">
  <xsl:value-of select="$db-column.corrector"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.java_classes"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<xsl:template name="column-def.changelog">
  <xsl:value-of select="$db-column.changelog"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.from_doc">
  <xsl:param name="origin"/>
  <xsl:value-of select="$db-column.from_doc"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="x-lib:dbTable($origin)"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.to_doc">
  <xsl:param name="to-doctype" select="@name"/>
  <xsl:value-of select="$db-column.to_doc"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="x-lib:dbTable($doctypes[@name=$to-doctype])"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.lid">
  <xsl:value-of select="$db-column.lid"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.ref_type">
  <xsl:value-of select="$db-column.ref_type"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.ref_types"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.problem_data_type">
  <xsl:value-of select="$db-column.type"/>
  <!-- Note that in the template name, it is "problem_data_type", whereas the column
    itself is called "type" -->
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.problem_data_types"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.score">
  <xsl:value-of select="$db-column.score"/>
  <xsl:text> real DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.document">
  <xsl:param name="type" select="@name"/>
  <xsl:value-of select="$db-column.document"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="x-lib:dbTable($doctypes[@name=$type])"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.entity">
  <xsl:param name="type" select="@name"/>
  <xsl:value-of select="$db-column.entity"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="x-lib:dbTable($detypes[@name=$type])"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.generic_document">
  <xsl:param name="generic" select="$doctypes[@is-generic-of=current()/@name]"/>
  <xsl:value-of select="$db-column.generic_document"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="x-lib:dbTable($generic)"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.author">
  <xsl:value-of select="$db-column.author"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.member">
  <xsl:value-of select="$db-column.member"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.theme">
  <xsl:value-of select="$db-column.theme"/>
  <xsl:text> int NOT NULL DEFAULT </xsl:text>
  <xsl:value-of select="$default-theme.id"/>
  <xsl:text> REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.themes"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.language">
  <xsl:value-of select="$db-column.language"/>
  <xsl:text> int NOT NULL DEFAULT </xsl:text>
  <xsl:value-of select="default-language.id"/>
  <xsl:text> REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.languages"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.type">
  <xsl:value-of select="$db-column.type"/>
  <xsl:text> text NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.subtype">
  <xsl:value-of select="$db-column.subtype"/>
  <xsl:text> text NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.user_group">
  <xsl:value-of select="$db-column.user_group"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.user_groups"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.ref">
  <xsl:param name="origin"/>
  <xsl:param name="target"/>
  <xsl:value-of select="$db-column.ref"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="x-lib:dbRefTable($origin, $target)"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.problem-data.content">
  <xsl:value-of select="$db-column.content"/>
  <xsl:text> text DEFAULT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.sync_id">
  <xsl:param name="not-null">no</xsl:param>
  <xsl:param name="unique">yes</xsl:param>
  <xsl:value-of select="$db-column.sync_id"/>
  <xsl:text> text</xsl:text>
  <xsl:if test="$unique='yes'"> UNIQUE</xsl:if>
  <xsl:choose>
    <xsl:when test="$not-null='yes'"> NOT NULL</xsl:when>
    <xsl:otherwise> DEFAULT NULL</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="column-def.tutor">
  <xsl:value-of select="$db-column.tutor"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>) DEFERRABLE INITIALLY DEFERRED</xsl:text>
</xsl:template>

<xsl:template name="column-def.class">
  <xsl:param name="is-reference">yes</xsl:param>
  <xsl:param name="not-null">yes</xsl:param>
  <xsl:value-of select="$db-column.class"/>
  <xsl:text> int</xsl:text>
  <xsl:if test="$not-null='yes'"> NOT NULL</xsl:if>
  <xsl:if test="$is-reference=yes">
    <xsl:text> REFERENCES </xsl:text>
    <xsl:value-of select="$db-table.classes"/>
    <xsl:text>(</xsl:text>
    <xsl:value-of select="$db-column.id"/>
    <xsl:text>)</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template name="column-def.lecturer">
  <xsl:value-of select="$db-column.lecturer"/>
  <xsl:text> int NOT NULL REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.users"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template name="column-def.tutorial">
  <xsl:value-of select="$db-column.tutorial"/>
  <xsl:text> int NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.code">
  <xsl:value-of select="$db-column.code"/>
  <xsl:text> varchar(4) UNIQUE NOT NULL</xsl:text>
</xsl:template>

<xsl:template name="column-def.sync_home">
  <xsl:value-of select="$db-column.sync_home"/>
  <xsl:text> int REFERENCES </xsl:text>
  <xsl:value-of select="$db-table.sections"/>
  <xsl:text>(</xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text>) DEFAULT NULL</xsl:text>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Initializing id sequence                                           -->
<!-- ====================================================================== -->

<xsl:template name="init-id-sequence">
  <xsl:param name="table-name"/>
  <xsl:text>SELECT setval('</xsl:text>
  <xsl:value-of select="$table-name"/>
  <xsl:text>_id_seq',max(id)+1) FROM </xsl:text>
  <xsl:value-of select="$table-name"/><xsl:text>;</xsl:text>
  <xsl:text>&br;</xsl:text>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Rules, functions, triggers                                         -->
<!-- ====================================================================== -->

<!-- Rule to create a new vc thread if a new document is checked in which is not
  a newer version of an existing document. The special vc thread id 0, which is
  the default for the vc_thread column of the document table, acts as a marker:
  if a document with vc thread 0 is checked in, the rule creates a new vc thread
  and replaces the value 0 by the id of the new cv thread. -->

<xsl:template name="create-rule.new-vc-thread">
  <xsl:param name="type" select="@name"/>
  <xsl:variable name="table" select="x-lib:dbTable($type)"/>
  <xsl:text>CREATE RULE </xsl:text>
  <xsl:value-of select="x-lib:dbRuleNewVCThread($type)"/>
  <xsl:text> AS ON INSERT TO </xsl:text>
  <xsl:value-of select="$table"/>
  <xsl:text>&br;&sp;WHERE new.</xsl:text>
  <xsl:value-of select="$db-column.vc_thread"/>
  <xsl:text> = 0 DO&br;</xsl:text>
  <xsl:text>&sp;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;INSERT INTO </xsl:text>
  <xsl:value-of select="x-lib:dbVCThreadTable($type)"/>
  <xsl:text> (</xsl:text>
  <xsl:value-of select="$db-column.name"/>
  <xsl:text>) VALUES (new.</xsl:text>
  <xsl:value-of select="$db-column.name"/>
  <xsl:text>);&br;</xsl:text>
  <xsl:text>&sp;&sp;&sp;UPDATE </xsl:text>
  <xsl:value-of select="$table"/>
  <xsl:text> SET </xsl:text>
  <xsl:value-of select="$db-column.vc_thread"/>
  <xsl:text> = currval('</xsl:text>
  <xsl:value-of select="x-lib:dbIdSec(x-lib:dbVCThreadTable($type))"/>
  <xsl:text>') WHERE </xsl:text>
  <xsl:value-of select="$db-column.vc_thread"/>
  <xsl:text> = 0;&br;</xsl:text>
  <xsl:text>&sp;&sp;);&br;</xsl:text>
</xsl:template>

<!-- Trigger function to set the 'last_modified' column if a table is updated.
  Compares the old and new 'last_modified' value. If they are equal, it is assumed
  that the 'last_modified' value was not set explicitely, and the new value is set
  to CURRENT_TIMESTAMP. -->

<xsl:template name="create-function.set-last-modified">
  <xsl:param name="type" select="@name"/>
  <xsl:text>CREATE FUNCTION </xsl:text>
  <xsl:value-of select="$set-last-modified-function"/>
  <xsl:text>()&br;</xsl:text>
  <xsl:text>RETURNS trigger&br;</xsl:text>
  <xsl:text>LANGUAGE plpgsql&br;</xsl:text>
  <xsl:text>AS&br;</xsl:text>
  <xsl:text>'BEGIN&br;</xsl:text>
  <xsl:text> IF NEW.</xsl:text><xsl:value-of select="$db-column.last_modified"/>
  <xsl:text> = </xsl:text>
  <xsl:text>OLD.</xsl:text><xsl:value-of select="$db-column.last_modified"/>
  <xsl:text> THEN&br;</xsl:text>
  <xsl:text>   NEW.</xsl:text><xsl:value-of select="$db-column.last_modified"/>
  <xsl:text> := CURRENT_TIMESTAMP;&br;</xsl:text>
  <xsl:text> END IF;&br;</xsl:text>
  <xsl:text> RETURN NEW;&br;</xsl:text>
  <xsl:text> END;';&br;</xsl:text>
</xsl:template>

<!-- Trigger to set the 'last_modified' column if a table is updated. The trigger
  executes the function defined by the template
  'create-function.set-last-modified'. -->

<xsl:template name="create-trigger.set-last-modified">
  <xsl:param name="type" select="@name"/>
  <xsl:text>CREATE TRIGGER </xsl:text>
  <xsl:value-of select="x-lib:dbTriggerSetLastModified($type)"/>
  <xsl:text> BEFORE UPDATE ON </xsl:text>
  <xsl:value-of select="x-lib:dbTable($type)"/>
  <xsl:text>&br;</xsl:text>
  <xsl:text>FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
  <xsl:value-of select="$set-last-modified-function"/>
  <xsl:text>();&br;</xsl:text>  
</xsl:template>

<!-- Trigger function to update the 'last_modified' column of a document table if a new
  version of the target of a reference is created. -->

<xsl:template name="create-function.update-last-modified-by-ref">
  <xsl:param name="origin"/>
  <xsl:param name="target"/>
  <xsl:text>CREATE FUNCTION </xsl:text>
  <xsl:value-of select="x-lib:dbTriggerUpdateLastModifiedByRef($origin, $target)"/>
  <xsl:text>()&br;</xsl:text>
  <xsl:text>RETURNS trigger&br;</xsl:text>
  <xsl:text>LANGUAGE plpgsql&br;</xsl:text>
  <xsl:text>AS&br;</xsl:text>
  <xsl:text>'BEGIN&br;</xsl:text>
  <xsl:text>   UPDATE </xsl:text>
  <xsl:value-of select="x-lib:dbTable($origin)"/>
  <xsl:text>&br;</xsl:text>
  <xsl:text>   SET </xsl:text>
  <xsl:value-of select="$db-column.last_modified"/>
  <xsl:text> = (SELECT </xsl:text>
  <xsl:value-of select="$db-column.last_modified"/>
  <xsl:text> FROM </xsl:text>
  <xsl:value-of select="x-lib:dbTable($target)"/>
  <xsl:text> WHERE </xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text> = NEW.</xsl:text>
  <xsl:value-of select="$db-column.to_doc"/>
  <xsl:text>)&br;</xsl:text>
  <xsl:text>   WHERE </xsl:text>
  <xsl:value-of select="$db-column.id"/>
  <xsl:text> = NEW.</xsl:text>
  <xsl:value-of select="$db-column.from_doc"/>
  <xsl:text>;&br;</xsl:text>
  <xsl:text>   RETURN NEW;&br;</xsl:text>
  <xsl:text> END;';&br;</xsl:text>
</xsl:template>

<xsl:template name="create-trigger.update-last-modified-by-ref">
  <xsl:param name="origin"/>
  <xsl:param name="target"/>
  <xsl:text>CREATE TRIGGER </xsl:text>
  <xsl:value-of select="x-lib:dbTriggerUpdateLastModifiedByRef($origin, $target)"/>
  <xsl:text>&br;AFTER UPDATE ON </xsl:text>
  <xsl:value-of select="x-lib:dbRefTable($origin, $target)"/>
  <xsl:text>&br;</xsl:text>
  <xsl:text>FOR EACH ROW EXECUTE PROCEDURE </xsl:text>
  <xsl:value-of select="x-lib:dbTriggerUpdateLastModifiedByRef($origin, $target)"/>
  <xsl:text>();&br;</xsl:text>  
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Notion tables                                                      -->
<!-- ====================================================================== -->

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Categories                                                         -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.categories">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.categories)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.categories"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:for-each select="/*/dc:categories/dc:category">
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$db-table.categories"/>
    <xsl:text> VALUES (</xsl:text>
    <xsl:value-of select="@code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Media types                                                       -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.media_types">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ', $db-table.media_types)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.media_types"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id">
    <xsl:with-param name="type" select="'int'"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.type"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.subtype"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:for-each select="/*/dc:media-types/dc:media-type">
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$db-table.media_types"/>
    <xsl:text> VALUES (</xsl:text>
    <xsl:value-of select="@code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@type"/>
    <xsl:text>', '</xsl:text>
    <xsl:value-of select="@subtype"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Reference types                                                   -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.ref_types">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.ref_types)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.ref_types"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:for-each select="/*/dc:reference-types/dc:reference-type">
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$db-table.ref_types"/>
    <xsl:text> VALUES (</xsl:text>
    <xsl:value-of select="@code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Problem data types                                                -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.problem_data_types">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message"
                    select="concat('Creating table ',$db-table.problem_data_types)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.problem_data_types"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:for-each select="/*/dc:problem-data-types/dc:problem-data-type">
    <xsl:text>INSERT INTO </xsl:text>
    <xsl:value-of select="$db-table.problem_data_types"/>
    <xsl:text> VALUES (</xsl:text>
    <xsl:value-of select="@code"/>
    <xsl:text>, '</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>');&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Pseudo-document tables                                             -->
<!-- ====================================================================== -->

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Sections                                                           -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.sections">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.sections)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.sections"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">section</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Users                                                             -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.users">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.users)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.users"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.sync_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.login_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.password"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.theme"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.language"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.first_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.surname"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.email"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_login"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.sync_home"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">user</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: User groups                                                       -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.user_groups">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.user_groups)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.user_groups"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name">
    <xsl:with-param name="unique">yes</xsl:with-param>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">user_group</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Themes                                                            -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.themes">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.themes)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.themes"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">theme</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Languages                                                         -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.languages">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.languages)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.languages"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.code"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">theme</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Semesters                                                         -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.semesters">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.semesters)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.semesters"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.sync_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">theme</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Classes                                                           -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.classes">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.classes)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.classes"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.sync_id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.semester"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">theme</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
<!-- h2: Tutorials                                                         -->
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

<xsl:template name="create-table.tutorials">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message" select="concat('Creating table ',$db-table.tutorials)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.tutorials"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.id"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.sync_id">
    <xsl:with-param name="not-null" select="yes"/>
  </xsl:call-template>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.contained_in.deferrable"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.tutor"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.class"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.created"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.deleted"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.last_modified"/>
  <xsl:text>&br;&sp;);&br;</xsl:text>
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="create-trigger.set-last-modified">
    <xsl:with-param name="type">theme</xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: User group members table                                           -->
<!-- ====================================================================== -->

<xsl:template name="create-table.user_group_members">
  <xsl:text>&br;</xsl:text>
  <xsl:call-template name="echo">
    <xsl:with-param name="message"
                    select="concat('Creating table ',$db-table.user_group_members)"/>
  </xsl:call-template>
  <xsl:text>CREATE TABLE </xsl:text>
  <xsl:value-of select="$db-table.user_group_members"/>
  <xsl:text>&br;&sp;(&br;</xsl:text>
  <xsl:text>&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.member"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:call-template name="column-def.user_group"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:text>UNIQUE (</xsl:text>
  <xsl:value-of select="$db-column.member"/>
  <xsl:text>,</xsl:text>
  <xsl:value-of select="$db-column.user_group"/>
  <xsl:text>)</xsl:text>
  <xsl:text>&br;&sp;);&br;</xsl:text>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: VC thread tables                                                   -->
<!-- ====================================================================== -->

<xsl:template name="create-vc-thread-tables">
  <xsl:for-each select="$doctypes[not(x-lib:isGeneric())]">
    <xsl:text>&br;</xsl:text>
    <xsl:variable name="table" select="x-lib:dbVCThreadTable()"/>
    <xsl:call-template name="echo">
      <xsl:with-param name="message" select="concat('Creating table ',$table)"/>
    </xsl:call-template>
    <xsl:text>CREATE TABLE </xsl:text>
    <xsl:value-of select="$table"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.name"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.description"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.created"/>
    <xsl:text>&br;&sp;);&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Document tables                                                    -->
<!-- ====================================================================== -->

<xsl:template name="create-document-tables">
  <xsl:for-each select="$doctypes">
    <xsl:text>&br;</xsl:text>
    <xsl:variable name="table" select="x-lib:dbTable()"/>
    <xsl:call-template name="echo">
      <xsl:with-param name="message" select="concat('Creating table ',$table)"/>
    </xsl:call-template>
    <xsl:choose>
      <xsl:when test="x-lib:isGeneric()">

        <xsl:text>CREATE TABLE </xsl:text>
        <xsl:value-of select="$table"/>
        <xsl:text>&br;&sp;(&br;</xsl:text>
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.id"/>
        <xsl:if test="x-lib:hasCategory()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.category"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.pure_name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.description"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.created"/>
        <xsl:if test="x-lib:hasQualifiedName()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.qualified-name"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.contained_in.deferrable"/>
        <xsl:if test="x-lib:hasWidthAndHeight()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.width"/>
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.height"/>
        </xsl:if>
        <xsl:if test="x-lib:hasDuration()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.duration"/>
        </xsl:if>
        <xsl:if test="x-lib:hasCorrector()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.corrector.deferrable"/>
        </xsl:if>
        <xsl:text>&br;&sp;);&br;</xsl:text>

        <xsl:text>&br;</xsl:text>
        <xsl:call-template name="create-trigger.set-last-modified"/>
        
      </xsl:when>
      <xsl:otherwise>

        <!-- Table: -->
        <xsl:text>CREATE TABLE </xsl:text>
        <xsl:value-of select="$table"/>
        <xsl:text>&br;&sp;(&br;</xsl:text>
        <xsl:text>&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.id"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.vc_thread">
          <xsl:with-param name="default">0</xsl:with-param>
        </xsl:call-template>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.version"/>
        <xsl:text>,&br;&sp;&sp;UNIQUE (</xsl:text>
        <xsl:value-of select="$db-column.vc_thread"/>
        <xsl:text>,</xsl:text>
        <xsl:value-of select="$db-column.version"/>
        <xsl:text>)</xsl:text>
        <xsl:if test="x-lib:hasCategory()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.category"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.pure_name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.name"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.description"/>
        <xsl:if test="x-lib:hasQualifiedName()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.qualified-name"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.copyright"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.created"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.deleted"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.last_modified"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.contained_in.deferrable"/>
        <xsl:if test="x-lib:hasWidthAndHeight()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.width"/>
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.height"/>
        </xsl:if>
        <xsl:if test="x-lib:hasDuration()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.duration"/>
        </xsl:if>
        <xsl:if test="x-lib:hasClass()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.class">
            <xsl:with-param name="not-null" select="'no'"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="x-lib:hasTimeframe()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.timeframe_start"/>
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.timeframe_end"/>
        </xsl:if>
        <xsl:if test="x-lib:hasCorrector()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.corrector.deferrable"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.content"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.content_type"/>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.content_length"/>
        <xsl:if test="x-lib:hasSummary()">
          <xsl:text>,&br;&sp;&sp;</xsl:text>
          <xsl:call-template name="column-def.summary"/>
        </xsl:if>
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.changelog"/>
        <xsl:text>&br;&sp;);&br;</xsl:text>
        <xsl:text>&br;</xsl:text>

        <!-- Rule to create a new vc thread: -->
        <xsl:call-template name="create-rule.new-vc-thread"/>

        <!-- Trigger to update 'last_mpdified': -->
        <xsl:text>&br;</xsl:text>
        <xsl:call-template name="create-trigger.set-last-modified"/>
        
      </xsl:otherwise>
    </xsl:choose>

 </xsl:for-each>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Reference tables                                                   -->
<!-- ====================================================================== -->

<xsl:template name="create-reference-tables">
  <xsl:for-each select="$doctypes[not(x-lib:isGeneric())]">
    <xsl:variable name="origin" select="."/>
    <xsl:for-each select="$doctypes[x-lib:refsEnabled($origin, .)]">
      <xsl:variable name="target" select="."/>
      <xsl:variable name="config"
        select="/*/dc:refs-config/dc:ref-config[@origin=$origin/@name and @target=$target/@name]"/>
      <xsl:text>&br;</xsl:text>
      <xsl:call-template name="echo">
        <xsl:with-param name="message"
          select="concat('Creating table ',x-lib:dbRefTable($origin, $target))"/>
      </xsl:call-template>
      <xsl:text>CREATE TABLE </xsl:text>
      <xsl:value-of select="x-lib:dbRefTable($origin, $target)"/>
      <xsl:text>&br;&sp;(&br;</xsl:text>
      <xsl:text>&sp;&sp;</xsl:text>
      <xsl:call-template name="column-def.id"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="column-def.from_doc">
	<xsl:with-param name="origin" select="$origin"/>
      </xsl:call-template>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="column-def.to_doc"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="column-def.lid"/>
      <xsl:text>,&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="column-def.ref_type"/>
      <xsl:if test="$config and x-lib:hasPoints($config)">
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.points"/>
      </xsl:if>
      <xsl:if test="$config and x-lib:hasLabel($config)">
        <xsl:text>,&br;&sp;&sp;</xsl:text>
        <xsl:call-template name="column-def.label"/>
      </xsl:if>
      <xsl:text>&br;</xsl:text>
      <xsl:text>&sp;);&br;</xsl:text>
      <xsl:text>&br;</xsl:text>
      <xsl:call-template name="create-function.update-last-modified-by-ref">
        <xsl:with-param name="origin" select="$origin"/>
        <xsl:with-param name="target" select="$target"/>
      </xsl:call-template>
      <xsl:text>&br;</xsl:text>
      <xsl:call-template name="create-trigger.update-last-modified-by-ref">
        <xsl:with-param name="origin" select="$origin"/>
        <xsl:with-param name="target" select="$target"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:for-each>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Author tables                                                      -->
<!-- ====================================================================== -->

<xsl:template name="create-author-tables">
  <xsl:for-each select="$doctypes[not(x-lib:isGeneric())]">
    <xsl:text>&br;</xsl:text>
    <xsl:call-template name="echo">
      <xsl:with-param name="message" select="concat('Creating table ', x-lib:dbAuthorTable())"/>
    </xsl:call-template>
    <xsl:text>CREATE TABLE </xsl:text>
    <xsl:value-of select="x-lib:dbAuthorTable()"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.document"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.author"/>
    <xsl:text>&br;&sp;);&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!-- ====================================================================== -->
<!-- h1: Permission tables                                                  -->
<!-- ====================================================================== -->

<xsl:template name="create-read-permission-tables">
  <xsl:for-each select="$detypes">
    <xsl:text>&br;</xsl:text>
    <xsl:call-template name="echo">
      <xsl:with-param name="message" select="concat('Creating table ', x-lib:dbReadPermTable())"/>
    </xsl:call-template>
    <xsl:text>CREATE TABLE </xsl:text>
    <xsl:value-of select="x-lib:dbReadPermTable()"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.user_group"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.entity"/>
    <xsl:text>&br;&sp;);&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template name="create-write-permission-tables">
  <xsl:for-each select="$detypes">
    <xsl:text>&br;</xsl:text>
    <xsl:call-template name="echo">
      <xsl:with-param name="message" select="concat('Creating table ', x-lib:dbWritePermTable())"/>
    </xsl:call-template>
    <xsl:text>CREATE TABLE </xsl:text>
    <xsl:value-of select="x-lib:dbWritePermTable()"/>
    <xsl:text>&br;&sp;(&br;</xsl:text>
    <xsl:text>&sp;&sp;</xsl:text>
    <xsl:call-template name="column-def.user_group"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
    <xsl:choose>
      <xsl:when test="local-name()='document-type' and not(x-lib:isGeneric())">
        <xsl:call-template name="column-def.vc_thread"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="column-def.entity"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>&br;&sp;);&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<!-- ====================================================================== -->
<!-- Creating sections                                                      -->
<!-- ====================================================================== -->

<!-- Creates a new section. Expects the following parameters:

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
          contained_in should be specified.  -->

<xsl:template name="create-section">
  <xsl:param name="id"/>
  <xsl:param name="pure_name"/>
  <xsl:param name="name" select="$pure_name"/>
  <xsl:param name="description"/>
  <xsl:param name="contained_in"/>
  <xsl:param name="contained_in.path"/>
  <xsl:call-template name="echo">
    <xsl:with-param name="message">
      <xsl:choose>
        <xsl:when test="$id = $root-section.id">
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
  <xsl:value-of select="$db-table.sections"/>
  <xsl:text>&br;&sp;(&br;&sp;&sp;</xsl:text>
  <xsl:if test="$id and $id != ''">
    <xsl:value-of select="$column-def.id"/>
    <xsl:text>,&br;&sp;&sp;</xsl:text>
  </xsl:if>
  <xsl:value-of select="$column-def.pure_name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$column-def.name"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$column-def.description"/>
  <xsl:text>,&br;&sp;&sp;</xsl:text>
  <xsl:value-of select="$column-def.contained_in"/>
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
      <xsl:text>section_id_for_path(</xsl:text>
      <xsl:value-of select="$root-section.id"/>
      <xsl:text>,'</xsl:text>
      <xsl:value-of select="$contained_in.path"/>
      <xsl:text>')</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>&br;&sp;);&br;</xsl:text>
</xsl:template>

<!-- ====================================================================== -->
<!--                                                                        -->
<!-- ====================================================================== -->

<!-- ====================================================================== -->
<!-- h1: Main template                                                      -->
<!-- ====================================================================== -->

<xsl:template match="/">
  <xsl:call-template name="create-table.categories"/>
  <xsl:call-template name="create-table.ref_types"/>
  <xsl:call-template name="create-table.media_types"/>
  <xsl:call-template name="create-table.problem_data_types"/>
  <xsl:call-template name="create-function.set-last-modified"/>
  <xsl:call-template name="create-table.sections"/>
  <xsl:call-template name="create-table.users"/>
  <xsl:call-template name="create-table.user_groups"/>
  <xsl:call-template name="create-table.themes"/>
  <xsl:call-template name="create-table.languages"/>
  <xsl:call-template name="create-table.semesters"/>
  <xsl:call-template name="create-table.classes"/>
  <xsl:call-template name="create-table.tutorials"/>
  <xsl:call-template name="create-table.user_group_members"/>
  <xsl:call-template name="create-vc-thread-tables"/>
  <xsl:call-template name="create-document-tables"/>
  <xsl:call-template name="create-reference-tables"/>
  <xsl:call-template name="create-author-tables"/>
  <xsl:call-template name="create-read-permission-tables"/>
  <xsl:call-template name="create-write-permission-tables"/>
</xsl:template>



</xsl:stylesheet>
