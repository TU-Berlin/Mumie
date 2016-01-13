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

   $Id: RefAttrib.xsl,v 1.5 2007/07/11 15:38:57 grudzin Exp $

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

<xsl:param name="this.filename">RefAttrib.xsl</xsl:param>
<xsl:param name="this.cvs-id">$Id: RefAttrib.xsl,v 1.5 2007/07/11 15:38:57 grudzin Exp $</xsl:param>
<xsl:param name="RefAttrib.package">net.mumie.cocoon.notions</xsl:param>
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
--> * Defines static variables and methods to access information concerning reference attributes.
</xsl:with-param>
</xsl:call-template>

public class RefAttrib
{
  /**
   * Indicates an undefined reference attribute.
   */

   public static final int UNDEFINED = -1;

<xsl:for-each select="/*/ref-attributes/ref-attribute">
  /**
   * <xsl:call-template name="config.description"/>
   */

  public static final int <!-- 
    --><xsl:call-template name="config.ref-attribute.constant"/> = <!--
    --><xsl:value-of select="position() - 1"/>;

</xsl:for-each>
  /**
   * Smallest number used as numerical code of a reference attribute.
   */

  public static final int first = <xsl:call-template name="config.ref-attribute.code-min"/>;

  /**
   * Largest number used as numerical code of a reference attribute.
   */

  public static final int last = <xsl:call-template name="config.ref-attribute.code-max"/>;

  /**
   * Maps ref attributes codes to the corresponding names.
   */

  public static final String[] nameFor =
  {<xsl:for-each select="/*/ref-attributes/ref-attribute">
    "<xsl:value-of select="@name"/>",
  </xsl:for-each>};

  /**
   * Maps ref attributes (given as numerical codes) to the corresponding db column names.
   */

  public static final String[] dbColumnOf =
  {<xsl:for-each select="/*/ref-attributes/ref-attribute">
    "<xsl:call-template name="config.ref-attribute.db-column-name"/>",
  </xsl:for-each>};

  /**
   * Maps ref attributes (given as numerical codes) to the corresponding SQL data types.
   */

  public static final String[] sqlDataTypeOf =
  {<xsl:for-each select="/*/ref-attributes/ref-attribute">
    "<xsl:value-of select="@sql-datatype"/>",
  </xsl:for-each>};

  /**
   * Maps ref attributes (given as numerical codes) to the corresponding Java types.
   */

  public static final String[] javaTypeOf =
  {<xsl:for-each select="/*/ref-attributes/ref-attribute">
    "<xsl:call-template name="config.ref-attribute.java-type"/>",
  </xsl:for-each>};

  /**
   * Maps ref attributes (given as numerical codes) to the corresponding document type
   * pairs.
   */

  public static final RefDocTypePair[][] docTypePairsOf =
  {<xsl:for-each select="/*/ref-attributes/ref-attribute">
     {<xsl:for-each select="doctype-pairs/doctype-pair">
       new RefDocTypePair(<!--
    --><xsl:call-template name="config.document-type.code">
         <xsl:with-param name="name" select="@from-doctype"/>
       </xsl:call-template>, <!--
    --><xsl:call-template name="config.document-type.code">
         <xsl:with-param name="name" select="@to-doctype"/>
       </xsl:call-template>),
     </xsl:for-each>},
  </xsl:for-each>};

  /**
   * Maps ref attributes (given as numerical codes) to the corresponding reference types.
   */

  public static final int[][] refTypesOf =
  {<xsl:for-each select="/*/ref-attributes/ref-attribute">
     {
      <xsl:for-each select="allowed-ref-types/ref-type">
       <xsl:call-template name="config.reference-type.code"/>,
     </xsl:for-each>},
  </xsl:for-each>};

  /**
   * Returns the numerical code for the string name &lt;code&gt;name&lt;/code&gt;. Can also
   * return &lt;code&gt;UNDEFINED&lt;/code&gt; if &lt;code&gt;name&lt;/code&gt; is not a valid
   * name.
   */

  public static int codeFor ( String name )
  {
    int code = first;
    while ( ( code &lt;= last ) &amp;&amp; ( !nameFor[code].equals(name) ) ) code++;
    if ( code &gt; last ) code = UNDEFINED;
    return code;
  }

  /**
   * Checks if the ref attribute &lt;code&gt;refAttrib&lt;/code&gt; (numerical code) exists for the
   * specified document type pair.
   */

  public static boolean exists (int refAttrib,
                                RefDocTypePair docTypePair)
  {
    return checkIfContained(docTypePairsOf[refAttrib], docTypePair);
  }

  /**
   * Checks if the ref attribute &lt;code&gt;refAttrib&lt;/code&gt; (numerical code) exists for the
   * specified "from" and "to" document types.
   */

  public static boolean exists (int refAttrib,
                                int fromDocType,
                                int toDocType)
  {
    return exists(refAttrib, new RefDocTypePair(fromDocType, toDocType));
  }

  /**
   * Checks if the ref attribute &lt;code&gt;refAttrib&lt;/code&gt; (numerical code) exists for the
   * specified document type pair and reference type.
   */

  public static boolean exists (int refAttrib,
                                RefDocTypePair docTypePair,
                                int refType)
  {
    return
      ( checkIfContained(docTypePairsOf[refAttrib], docTypePair) &amp;&amp;
        checkIfContained(refTypesOf[refAttrib], refType) );
  }

  /**
   * Checks if the ref attribute &lt;code&gt;refAttrib&lt;/code&gt; (numerical code) exists for the
   * specified document types and reference type.
   */

  public static boolean exists (int refAttrib,
                                int fromDocType,
                                int toDocType,
                                int refType)
  {
    return exists(refAttrib, new RefDocTypePair(fromDocType, toDocType), refType);
  }

  /**
   * Returns &lt;code&gt;true&lt;/code&gt; if &lt;code&gt;docTypePair&lt;/code&gt; is contained in
   * &lt;code&gt;docTypePairs&lt;/code&gt;, otherwise &lt;code&gt;false&lt;/code&gt; (auxiliary method).
   */

  protected static boolean checkIfContained (RefDocTypePair[] docTypePairs,
                                             RefDocTypePair docTypePair)
  {
    boolean contains = false;
    for (int i = 0; !contains &amp;&amp; i &lt; docTypePairs.length; i++)
      contains = ( docTypePairs[i].equals(docTypePair) );
    return contains;
  }

  /**
   * Returns &lt;code&gt;true&lt;/code&gt; if &lt;code&gt;value&lt;/code&gt; is contained in
   * &lt;code&gt;values&lt;/code&gt;, otherwise &lt;code&gt;false&lt;/code&gt;  (auxiliary method).
   */

  protected static boolean checkIfContained (int[] values, int value)
  {
    boolean contains = false;
    for (int i = 0; !contains &amp;&amp; i &lt; values.length; i++)
      contains = ( values[i] == value );
    return contains;
  }
}
</xsl:template>

</xsl:stylesheet>
