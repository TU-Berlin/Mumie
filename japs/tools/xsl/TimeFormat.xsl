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

   $Id: TimeFormat.xsl,v 1.7 2007/07/11 15:38:58 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">TimeFormat.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: TimeFormat.xsl,v 1.7 2007/07/11 15:38:58 grudzin Exp $</xsl:param>
<xsl:param name="TimeFormat.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<!--
   ==========================================================================================
   Templates for target "TimeFormat"
   ==========================================================================================
-->

<xsl:template name="TimeFormat.class-description">
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Time formats used within the Japs&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;Currenty, the following time formats are defined:&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
  <xsl:text> * &lt;table class="genuine indented"&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;thead&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Name&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Format&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Constant&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&sp;&lt;td&gt;Description&lt;/td&gt;&br;</xsl:text>
  <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;/thead&gt;&br;</xsl:text>
  <xsl:text> * &sp;&lt;tbody&gt;&br;</xsl:text>
  <xsl:for-each select="/*/time-formats/time-format">
    <xsl:text> * &sp;&sp;&lt;tr&gt;&br;</xsl:text>
    <xsl:text> * &sp;&sp;&sp;&lt;td class="string"&gt;</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>&lt;/td&gt;&br;</xsl:text>
    <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;format&gt;</xsl:text>
    <xsl:value-of select="@format"/>
    <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
    <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&lt;code&gt;</xsl:text>
    <xsl:call-template name="config.time-format.constant"/>
    <xsl:text>&lt;/code&gt;&lt;/td&gt;&br;</xsl:text>
    <xsl:text> * &sp;&sp;&sp;&lt;td&gt;&br;</xsl:text>
    <xsl:text> * &sp;&sp;&sp;&sp;</xsl:text>
    <xsl:call-template name="config.description">
      <xsl:with-param name="newline">&br; * &sp;&sp;&sp;&sp;</xsl:with-param>
    </xsl:call-template>
    <xsl:text>&br;</xsl:text>
    <xsl:text> * &sp;&sp;&sp;&lt;/td&gt;&br;</xsl:text>
    <xsl:text> * &sp;&sp;&lt;/tr&gt;&br;</xsl:text>
  </xsl:for-each>
  <xsl:text> * &sp;&lt;/tbody&gt;&br;</xsl:text>
  <xsl:text> * &lt;/table&gt;&br;</xsl:text>
  <xsl:text> * &lt;p&gt;&br;</xsl:text>
  <xsl:text> * &sp;This class defines static constants with self-explanatory names&br;</xsl:text>
  <xsl:text> * &sp;wrapping the time formats.&br;</xsl:text>
  <xsl:text> * &lt;/p&gt;&br;</xsl:text>
</xsl:template>

<xsl:template name="TimeFormat.time-formats">
  <xsl:for-each select="/*/time-formats/time-format">
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
    <xsl:call-template name="config.time-format.constant"/>
    <xsl:text> = "</xsl:text>
    <xsl:value-of select="@format"/>
    <xsl:text>";&br;</xsl:text>
  </xsl:for-each>
</xsl:template>

<xsl:template name="TimeFormat">
  <xsl:text>package </xsl:text>
  <xsl:value-of select="$TimeFormat.package"/>
  <xsl:text>;&br;&br;</xsl:text>
  <xsl:call-template name="config.class-description">
    <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
    <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
    <xsl:with-param name="content">
      <xsl:call-template name="TimeFormat.class-description"/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:text>&br;</xsl:text>
  <xsl:text>public class TimeFormat&br;</xsl:text>
  <xsl:text>{&br;</xsl:text>
  <xsl:call-template name="TimeFormat.time-formats"/>
  <xsl:text>}&br;</xsl:text>
</xsl:template>

<!--
   ==========================================================================================
   Top-level template
   ==========================================================================================
-->

<xsl:template match="/*">
  <xsl:call-template name="TimeFormat"/>
</xsl:template>

</xsl:stylesheet>
