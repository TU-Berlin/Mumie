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

   $Id: WorksheetState.xsl,v 1.4 2007/08/05 16:11:43 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.notion classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                version="1.0">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>
<xsl:strip-space elements="*"/>

<!--
  ==========================================================================================
  Global parameters and variables
  ==========================================================================================
-->

<xsl:param name="this.filename">WorksheetState.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: WorksheetState.xsl,v 1.4 2007/08/05 16:11:43 rassy Exp $</xsl:param>
<xsl:param name="WorksheetState.package">net.mumie.cocoon.notions</xsl:param>
<xsl:param name="default-newline">&br;&sp; * </xsl:param>

<!--
  ==========================================================================================
  Main template
  ==========================================================================================
-->

<xsl:template match="/*">
package net.mumie.cocoon.notions;

<xsl:call-template name="config.class-description">
  <xsl:with-param name="xsl-stylesheet" select="$this.filename"/>
  <xsl:with-param name="xsl-stylesheet-cvs-id" select="$this.cvs-id"/>
  <xsl:with-param name="content"><!--
--> * Defines static variables and methods to access information concerning worksheet states.
</xsl:with-param>
</xsl:call-template>

public class WorksheetState
{
  /**
   * Indicates an undefined worksheet state.
   */

   public static final int UNDEFINED = -1;

<xsl:for-each select="/*/worksheet-states/worksheet-state">
  /**
   * <xsl:call-template name="config.description"/>
   */

  public static final int <!-- 
    --><xsl:call-template name="config.constant"/> = <!--
    --><xsl:value-of select="position() - 1"/>;

</xsl:for-each>
  /**
   * Smallest number used as numerical code of a worksheet state.
   */

  public static final int first = <xsl:call-template name="config.worksheet-state.code-min"/>;

  /**
   * Largest number used as numerical code of a worksheet state.
   */

  public static final int last = <xsl:call-template name="config.worksheet-state.code-max"/>;

  /**
   * Maps worksheet states codes to the corresponding names.
   */

  public static final String[] nameFor =
  {<xsl:for-each select="/*/worksheet-states/worksheet-state">
    "<xsl:value-of select="@name"/>",
  </xsl:for-each>};

  /**
   * Returns the numerical code for the string name &lt;code&gt;name&lt;/code&gt;. Can also
   * return &lt;code&gt;UNDEFINED&lt;/code&gt; if &lt;code&gt;name&lt;/code&gt; is not a valid
   * name.
   */

  public static int codeFor (String name)
  {
    int code = first;
    while ( ( code &lt;= last ) &amp;&amp; ( !nameFor[code].equals(name) ) ) code++;
    if ( code &gt; last ) code = UNDEFINED;
    return code;
  }

  /**
   * Returns true if the specified integer exists as a worksheet state, otherwise false.
   */

  public static boolean exists (int state)
  {
    return ( state &gt;= first &amp;&amp; state &lt;= last );
  }
}
</xsl:template>

</xsl:stylesheet>
