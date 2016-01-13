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

   $Id: UseMode.xsl,v 1.3 2007/07/11 15:38:58 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>
<xsl:import href="java_utils.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">UseMode.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: UseMode.xsl,v 1.3 2007/07/11 15:38:58 grudzin Exp $</xsl:param>
<xsl:param name="UseMode.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<xsl:template match="/*">
package net.mumie.cocoon.notions;

<xsl:call-template name="config.class-description">
  <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
  <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
  <xsl:with-param name="content"><!--
-->* &lt;p&gt;
 *  Defines static variables and methods to access information concerning pseudo-document
 *  types.
 * &lt;/p&gt;
 * &lt;p&gt;
 *   Use modes are specified by numerical codes, or, alternatively, by string
 *   names. The computer almost always uses the numerical codes. The string names exist to
 *   have a more human-readable notation for use modes as well. In addition,
 *   there exist self-explanatory named static constants wrapping the numerical codes. They
 *   are defined in this class. 
 * &lt;/p&gt;
 * &lt;p&gt;
 *   Currently, the following following use modes are defined:
 * &lt;/p&gt;
 * &lt;table class="genuine indented"&gt;
 *   &lt;thead&gt;
 *     &lt;tr&gt;
 *       &lt;td&gt;Name&lt;/td&gt;
 *       &lt;td&gt;Code&lt;/td&gt;
 *       &lt;td&gt;Constant&lt;/td&gt;
 *       &lt;td&gt;Description&lt;/td&gt;
 *     &lt;/tr&gt;
 *   &lt;/thead&gt;
 *   &lt;tbody&gt;<!--
--><xsl:for-each select="/*/use-modes/use-mode">
 *     &lt;tr&gt;
 *       &lt;td class="string"&gt;<xsl:value-of select="@name"/>&lt;/td&gt;
 *       &lt;td&gt;&lt;code&gt;<xsl:value-of select="position() - 1"/>&lt;/code&gt;&lt;/td&gt;
 *       &lt;td&gt;&lt;code&gt;<xsl:call-template name="config.constant"/>&lt;/code&gt;&lt;/td&gt;
 *       &lt;td&gt;<xsl:call-template name="config.description"/>&lt;/td&gt;
 *     &lt;/tr&gt;<!--
--></xsl:for-each>
 *   &lt;/tbody&gt;
 * &lt;/table&gt;&br;<!--
--></xsl:with-param>
</xsl:call-template>

public class UseMode
{
  /**
   * Numerical code indicating hat the use mode is undefined.
   */

  public static int UNDEFINED = -1;

  <xsl:for-each select="/*/use-modes/use-mode">
  /**
   * <xsl:call-template name="config.description"/>
   */

  public static final int <!-- 
    --><xsl:call-template name="config.constant"/> = <!--
    --><xsl:value-of select="position() - 1"/>;

</xsl:for-each>
  /**
   * Smallest number used as numerical code of a use mode.
   */

  public static final int first = <xsl:call-template name="config.use-mode.code-min"/>;

  /**
   * Largest number used as numerical code of a use mode.
   */

  public static final int last = <xsl:call-template name="config.use-mode.code-max"/>;

  /**
   * Maps use modes codes to the corresponding names.
   */

  public static final String[] nameFor =
  {<xsl:for-each select="/*/use-modes/use-mode">
    "<xsl:value-of select="@name"/>",<!--
--></xsl:for-each>
  };

<xsl:call-template name="java-utils.codeFor"/>

<xsl:text>&br;</xsl:text>

<xsl:call-template name="java-utils.exists">
  <xsl:with-param name="description">a use mode</xsl:with-param>
</xsl:call-template>

<xsl:text>&br;</xsl:text>

<xsl:call-template name="java-utils.array-to-method">
  <xsl:with-param name="array" select="'nameFor'"/>
  <xsl:with-param name="type" select="'String'"/>
</xsl:call-template>

<xsl:text>&br;</xsl:text>

<xsl:call-template name="java-utils.disabled-constructor">
  <xsl:with-param name="class" select="'UseMode'"/>
</xsl:call-template><!--
-->}
</xsl:template>

</xsl:stylesheet>
