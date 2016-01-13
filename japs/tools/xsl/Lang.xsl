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

   $Id: Lang.xsl,v 1.5 2007/07/11 15:38:57 grudzin Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<xsl:param name="this.filename">Lang.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: Lang.xsl,v 1.5 2007/07/11 15:38:57 grudzin Exp $</xsl:param>

<xsl:template match="/*">
package net.mumie.cocoon.notions;

<xsl:call-template name="config.class-description">
  <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
  <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
  <xsl:with-param name="content"><!--
--> * Defines static constants wrapping special language id's.
</xsl:with-param>
</xsl:call-template>

<xsl:variable name="default-lang-id" select="/*/languages/default-language/@db-id"/>
<xsl:variable name="neutral-lang-id" select="/*/languages/neutral-language/@db-id"/>

public class Lang
{
  /**
   * Indicates the language is undefined.
   */

  public static final int UNDEFINED = -1;

  /**
   * The default language. Value: <xsl:value-of select="$default-lang-id"/>.
   */

  public static final int DEFAULT = <xsl:value-of select="$default-lang-id"/>;

  /**
   * The neutral language. Value: <xsl:value-of select="$neutral-lang-id"/>.
   */

  public static final int NEUTRAL = <xsl:value-of select="$neutral-lang-id"/>;
}
</xsl:template>

</xsl:stylesheet>
