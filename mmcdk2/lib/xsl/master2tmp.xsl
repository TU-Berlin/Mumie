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
  ]
>

<!--
   Author: Tilman Rassy <rassy@math.tu-berlin.de>

   $Id: master2tmp.xsl,v 1.8 2008/06/04 09:46:24 rassy Exp $
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mumie="http://www.mumie.net/xml-namespace/document/metainfo"
                xmlns:x-io-util="xalan://net.mumie.util.io.IOUtil"
                xmlns:x-helper="xalan://net.mumie.cdk.CdkHelper"
                exclude-result-prefixes="mumie x-io-util x-helper">

<xsl:output method="xml" encoding="ASCII"/>

<!--
  The net.mumie.cdk.CdkHelper object.
-->

<xsl:param name="helper" select="x-helper:getSharedInstance()"/>

<!--
   The checkin root directory.
-->

<xsl:param name="checkin-root" select="x-helper:getCheckinRoot($helper)"/>

<!--
   Path of the master file.
-->

<xsl:param name="master-path"/>

<!--
   Absolute content filename.
-->

<xsl:param name="content-abs-filename"/>

<!--
   Absolute filename of datasheet.
-->

<xsl:param name="datasheet-abs-filename"/>

<!--
   Absolute filename of dynamic data.
-->

<xsl:param name="dyndata-abs-filename"/>

<!--
   Creates the XML element for a document in use mode "component".
-->

<xsl:template name="component">
  <xsl:param name="referencer-path" select="$master-path"/>
  <xsl:copy>
    <xsl:copy-of select="@path|@url"/>
    <xsl:copy-of select="@lid"/>
    <xsl:copy-of select="@param"/>
    <xsl:if test="@path">
      <xsl:variable name="resolved-path"
                    select="x-helper:resolvePathPrefix($helper,@path,$referencer-path)"/>
      <xsl:variable name="abs-filename"
                    select="x-io-util:concatenatePaths($checkin-root, $resolved-path)"/>
      <xsl:variable name="master"
                    select="document($abs-filename)"/>
      <xsl:if test="not($master)">
        <xsl:message terminate="yes">
  
  **********************************************************************
   ERROR:  Can not load master of component

   Referenced    : <xsl:value-of select="$referencer-path"/>
   Lid           : <xsl:value-of select="@lid"/>
   Path          : <xsl:value-of select="@path"/>
   Path resolved : <xsl:value-of select="$resolved-path"/>
  **********************************************************************
        </xsl:message>
      </xsl:if>
      <xsl:copy-of select="$master/*/mumie:name"/>
      <xsl:copy-of select="$master/*/mumie:description"/>
      <xsl:copy-of select="$master/*/mumie:category"/>
      <xsl:copy-of select="$master/*/mumie:qualified_name"/>
      <xsl:copy-of select="$master/*/mumie:width"/>
      <xsl:copy-of select="$master/*/mumie:height"/>
      <xsl:copy-of select="$master/*/mumie:timeframe_start"/>
      <xsl:copy-of select="$master/*/mumie:timeframe_end"/>
      <mumie:components>
        <xsl:for-each select="$master/*/mumie:components/*">
          <xsl:call-template name="component">
            <xsl:with-param name="referencer-path" select="$resolved-path"/>
          </xsl:call-template>
        </xsl:for-each>
      </mumie:components>
    </xsl:if>
  </xsl:copy>
</xsl:template>

<!--
   Top-level template.
-->

<xsl:template match="/">
  <xsl:variable name="element-name" select="name(/*)"/>
  <xsl:element name="{$element-name}">
    <xsl:attribute name="use-mode">serve</xsl:attribute>
    <xsl:copy-of select="/*/mumie:name"/>
    <xsl:copy-of select="/*/mumie:description"/>
    <xsl:copy-of select="/*/mumie:copyright"/>
    <xsl:copy-of select="/*/mumie:category"/>
    <xsl:copy-of select="/*/mumie:links"/>
    <mumie:components>
      <xsl:for-each select="/*/mumie:components/*">
        <xsl:call-template name="component"/>
      </xsl:for-each>
    </mumie:components>
    <mumie:content>
      <xsl:copy-of select="document($content-abs-filename)"/>
    </mumie:content>
    <xsl:if test="$dyndata-abs-filename">
      <xsl:copy-of select="document($dyndata-abs-filename)"/>
    </xsl:if>
    <xsl:if test="$datasheet-abs-filename">
      <mumie:dynamic_data>
        <mumie:store name="data">
          <mumie:user_problem_data user_id="" ref_id="">
            <mumie:common_data_last_modified value="" raw=""/>
            <mumie:answers_last_modified value="" raw=""/>
            <mumie:correction_last_modified value="" raw=""/>
            <mumie:content>
              <xsl:copy-of select="document($datasheet-abs-filename)"/>
            </mumie:content>
          </mumie:user_problem_data>
        </mumie:store>
      </mumie:dynamic_data>
    </xsl:if>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>

