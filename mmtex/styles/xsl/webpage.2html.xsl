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

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mumie="http://www.mumie.net/xml-namespace/mmtex-xsl"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mumie">

<xsl:import href="config.xsl"/>
<xsl:import href="math.2xhtml.xsl"/>

<xsl:output method="xml" 
            doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN"
            doctype-system="http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd"/>

<xsl:strip-space elements="a h1 h2 h3 h4 h5 h6"/>


<!--
   ==========================================================================================
   Top level parameters and variables
   ==========================================================================================
-->

<!--
   The language the source is written in.
-->

<xsl:param name="language" select="'english'"/>

<!--
   The default CSS stylesheet
-->

<xsl:param name="default-css-stylesheet"><xsl:value-of select="$css_dir"/>/webpage.css</xsl:param>

<!--
   The MathML namespace
-->

<xsl:param name="mathml-namespace">http://www.w3.org/1998/Math/MathML</xsl:param>

<!--
   The XHTML namespace
-->

<xsl:param name="xhtml-namespace">http://www.w3.org/1999/xhtml</xsl:param>

<!--
   Prefix to prepend to URI's in references. - Currently unused.
-->

<xsl:param name="uri-prefix"/>

<!--
   Suffix to append to referenced filenames.
-->

<xsl:param name="output-suffix" select="'xhtml'"/>

<!--
   ==========================================================================================
   Data
   ==========================================================================================
-->

<!--
   The data block itself.
-->

<mumie:data>
  <mumie:notions language="english">
    <mumie:notion name="toc_title" value="Contents"/>
    <mumie:notion name="quote_left" value='"'/>
    <mumie:notion name="quote_right" value='"'/>
  </mumie:notions>
  <mumie:notions language="german">
    <mumie:notion name="toc_title" value="Inhalt"/>
    <mumie:notion name="quote_left" value="&#8222;"/>
    <mumie:notion name="quote_right" value="&#8220;"/>
  </mumie:notions>
  <mumie:structures>
    <mumie:structure name="part" level="1"/>
    <mumie:structure name="chapter" level="2"/>
    <mumie:structure name="section" level="3"/>
    <mumie:structure name="subsection" level="4"/>
    <mumie:structure name="subsubsection" level="5"/>
  </mumie:structures>
  <mumie:footnote-symbols>
    <mumie:footnote-symbol>&#8727;</mumie:footnote-symbol>        <!-- asterisk -->
    <mumie:footnote-symbol>&#8224;</mumie:footnote-symbol>        <!-- dagger -->
    <mumie:footnote-symbol>&#8225;</mumie:footnote-symbol>        <!-- double dagger -->
    <mumie:footnote-symbol>&#167;</mumie:footnote-symbol>         <!-- section -->
    <mumie:footnote-symbol>&#182;</mumie:footnote-symbol>         <!-- paragraph -->
    <mumie:footnote-symbol>&#x02225;</mumie:footnote-symbol>      <!-- double vertical bar -->
    <mumie:footnote-symbol>&#8727;&#8727;</mumie:footnote-symbol> <!-- two  asterisks -->
    <mumie:footnote-symbol>&#8224;&#8224;</mumie:footnote-symbol> <!-- two daggers -->
    <mumie:footnote-symbol>&#8225;&#8225;</mumie:footnote-symbol> <!-- two double daggers -->
  </mumie:footnote-symbols>
</mumie:data>

<!--
   Root node of the data block
-->

<xsl:variable name="data" select="document('')/xsl:stylesheet/mumie:data"/>

<!--
   ==========================================================================================
   Auxiliary templates
   ==========================================================================================
-->

<!--
   Outputs an error message and terminates transformation. The message is passed as the
   parameter $message.
-->

<xsl:template name="error">
  <xsl:param name="message"/>
  <xsl:message terminate="yes">
    <xsl:text>Error:&br;&br;&sp;</xsl:text>
    <xsl:value-of select="$message"/>
    <xsl:text>&br;</xsl:text>
  </xsl:message>
</xsl:template>

<!--
   Sets a "class" attribute if necessary. Expects up to two parameters:

     class       The class. Defaults to the "class" attribute of the current node.

     base-class  An additional class (see below). No default.

  If both $class and $base-class are present, the value of the attribute is
  "$base-class $class". If only $class or $base-class are present, the value of the
  attribute is "$class" or "$base-class", repsectively. If neither $class nor $base-class is
  present, the attribute is not set.
-->

<xsl:template name="class">
  <xsl:param name="base-class"/>
  <xsl:param name="class" select="@class"/>
  <xsl:choose>
    <xsl:when test="$base-class and $class">
      <xsl:attribute name="class">
        <xsl:value-of select="concat($base-class,' ',$class)"/>
      </xsl:attribute>
    </xsl:when>
    <xsl:when test="$class">
      <xsl:attribute name="class">
        <xsl:value-of select="$class"/>
      </xsl:attribute>
    </xsl:when>
    <xsl:when test="$base-class">
      <xsl:attribute name="class">
        <xsl:value-of select="$base-class"/>
      </xsl:attribute>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!--
   Composes an URI. Expects one parameter:

     spec  A description of the URI in a certain format; see below. Defaults to the "href"
           attribute of the current node.

   The format of the description is:

     $mode:[$uri][#$anchor]

   $mode is keyword that specifies how $spec must be processed, $uri the name of the file
   to where the URI points, and $anchor an anchor within this file. "$uri" and  "#$anchor"
   are both optional, as indicated by the square brackets. $mode may take the following
   values:

     normal 
           The part of $spec after "$mode:" is taken as the URI.

     mmtex
           If $uri is not lacking, the URI is composed of $uri, a dot, the value of the
           global parameter $output-suffix, and, optinally, a "#" and $anchor; i.e.,  

             $uri.$output-suffix[#$anchor]

           If $uri is lacking but $anchor is present, the URI is

             #$anchor

           If neither $uri nor $anchor is present, nothing is returned for the URI.

     mailto
           Indicates that the part of $spec after "mailto:" is an email address. Entire
           $spec is returned for the URI.
-->

<xsl:template name="uri">
  <xsl:param name="spec" select="@href"/>
  <xsl:variable name="mode" select="substring-before($spec,':')"/>
  <xsl:variable name="uri-plus-anchor" select="substring-after($spec,':')"/>
  <xsl:variable name="uri">
    <xsl:choose>
      <xsl:when test="contains($uri-plus-anchor,'#')">
        <xsl:value-of select="substring-before($uri-plus-anchor,'#')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$uri-plus-anchor"/>
      </xsl:otherwise>    
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="anchor" select="substring-after($uri-plus-anchor,'#')"/>
  <xsl:choose>
    <xsl:when test="$mode='normal'">
      <xsl:value-of select="$uri-plus-anchor"/>
    </xsl:when>
    <xsl:when test="$mode='mmtex'">
      <xsl:choose>
        <xsl:when test="string-length($uri)&gt;0 and string-length($anchor)&gt;0">
          <xsl:value-of select="concat($uri,'.',$output-suffix,'#',$anchor)"/>
        </xsl:when>
        <xsl:when test="string-length($uri)&gt;0">
          <xsl:value-of select="concat($uri,'.',$output-suffix)"/>
        </xsl:when>
        <xsl:when test="string-length($anchor)&gt;0">
          <xsl:value-of select="concat('#',$anchor)"/>
        </xsl:when>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="$mode='mailto'">
      <xsl:value-of select="$spec"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="error">
        <xsl:with-param name="message"
          select="concat('Unknown uri processing mode: ',$mode)"/>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Sets an "href" attribute of necessary. Expects up to one parameter:

     href  The description of the URI this href attribute should point to. Defaults to the
           "href" attribute of the current node.

   $href is precessed by means of the "uri" template. If $href is not set, no attribute is
   written to the result tree.
-->

<xsl:template name="href">
  <xsl:param name="href" select="@href"/> 
  <xsl:if test="$href">
    <xsl:attribute name="href">
      <xsl:call-template name="uri">
        <xsl:with-param name="spec" select="$href"/>
      </xsl:call-template>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!--
-->

<xsl:template name="id">
  <xsl:param name="id" select="@id"/> 
  <xsl:if test="$id">
    <xsl:attribute name="id">
      <xsl:value-of select="$id"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!--
-->

<xsl:template name="onevent">
  <xsl:param name="onclick" select="@onclick"/>
  <xsl:param name="ondblclick" select="@ondblclick"/>
  <xsl:param name="onmousedown" select="@onmousedown"/>
  <xsl:param name="onmouseup" select="@onmouseup"/>
  <xsl:param name="onmouseover" select="@onmouseover"/>
  <xsl:param name="onmousemove" select="@onmousemove"/>
  <xsl:param name="onmouseout" select="@onmouseout"/>
  <xsl:param name="onkeypress" select="@onkeypress"/>
  <xsl:param name="onkeydown" select="@onkeydown"/>
  <xsl:param name="onkeyup" select="@onkeyup"/>
  <xsl:if test="$onclick">
    <xsl:attribute name="onclick">
      <xsl:value-of select="$onclick"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$ondblclick">
    <xsl:attribute name="ondblclick">
      <xsl:value-of select="$ondblclick"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onmousedown">
    <xsl:attribute name="onmousedown">
      <xsl:value-of select="$onmousedown"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onmouseup">
    <xsl:attribute name="onmouseup">
      <xsl:value-of select="$onmouseup"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onmouseover">
    <xsl:attribute name="onmouseover">
      <xsl:value-of select="$onmouseover"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onmousemove">
    <xsl:attribute name="onmousemove">
      <xsl:value-of select="$onmousemove"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onmouseout">
    <xsl:attribute name="onmouseout">
      <xsl:value-of select="$onmouseout"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onkeypress">
    <xsl:attribute name="onkeypress">
      <xsl:value-of select="$onkeypress"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onkeydown">
    <xsl:attribute name="onkeydown">
      <xsl:value-of select="$onkeydown"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="$onkeyup">
    <xsl:attribute name="onkeyup">
      <xsl:value-of select="$onkeyup"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!--
  Inserts vertical space.
-->

<xsl:template name="vertical-space">
  <xsl:param name="space" select="@space"/>
  <div>
    <xsl:attribute name="style">
      <xsl:value-of select="concat('height:',$space)"/>
    </xsl:attribute>
  </div>
</xsl:template>

<!--
   ==========================================================================================
   Styles
   ==========================================================================================
-->

<xsl:template match="/webpage/styles/css-stylesheet">
  <link rel="stylesheet" type="text/css">
    <xsl:call-template name="href"/>
  </link>
</xsl:template>

<xsl:template name="styles">
  <xsl:choose>
    <xsl:when test="/webpage/styles">
      <xsl:apply-templates select="/webpage/styles"/>
    </xsl:when>
    <xsl:otherwise>
      <link rel="stylesheet" type="text/css">
        <xsl:copy-of select="$default-css-stylesheet"/>
      </link>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   ==========================================================================================
   General structure
   ==========================================================================================
-->

<xsl:template match="/">
  <html>
    <head>
      <xsl:copy-of select="/webpage/title"/>
      <xsl:call-template name="styles"/>
    </head>
    <body>
      <xsl:apply-templates select="/webpage/document"/>
    </body>
  </html>
</xsl:template>

<!--
   ==========================================================================================
   Structuring (incl. headlines)
   ==========================================================================================
-->

<xsl:template match="/webpage/document">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="par">
  <p>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="/webpage/document/title">
  <div class="title">
    <h1>
      <xsl:call-template name="id"/>
      <xsl:call-template name="balloon"/>
      <xsl:call-template name="onevent"/>
      <xsl:apply-templates/>
    </h1>
    <xsl:apply-templates select="/webpage/document/subtitle" mode="title"/>
  </div>
</xsl:template>

<xsl:template match="/webpage/document/subtitle" mode="title">
  <h1 class="sub">
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </h1>
</xsl:template>

<xsl:template match="/webpage/document/subtitle"></xsl:template>

<xsl:template match="/webpage/document/section">
  <xsl:if test="@id">
    <a name="{@id}"></a>
  </xsl:if>
  <div class="section">
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="/webpage/document/section/headline">
  <h2>
    <xsl:if test="../@no">
      <span class="section-no"><xsl:number value="../@no" format="1 "/></span>
    </xsl:if>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </h2>
</xsl:template>

<xsl:template match="/webpage/document/section/subsection">
  <xsl:if test="@id">
    <a name="{@id}"></a>
  </xsl:if>
  <div class="subsection">
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="/webpage/document/section/subsection/headline">
  <h3>
    <xsl:if test="../../@no and ../@no">
      <span class="subsection-no">
        <xsl:number value="../../@no" format="1."/>
        <xsl:number value="../@no" format="1 "/>
      </span>
    </xsl:if>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </h3>
</xsl:template>

<xsl:template match="/webpage/document/section/subsection/subsubsection">
  <xsl:if test="@id">
    <a name="{@id}"></a>
  </xsl:if>
  <div class="subsubsection">
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="/webpage/document/section/subsection/subsubsection/headline">
  <h4>
    <xsl:if test="../../../@no and ../../@no and ../@no">
      <span class="subsubsection-no">
        <xsl:number value="../../../@no" format="1."/>
        <xsl:number value="../../@no" format="1."/>
        <xsl:number value="../@no" format="1 "/>
      </span>
    </xsl:if>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </h4>
</xsl:template>



<!--
   ==========================================================================================
   Simple markup                              
   ==========================================================================================
-->

<xsl:template match="mark">
  <mark>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </mark>
</xsl:template>

<xsl:template match="emph">
  <em>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </em>
</xsl:template>

<xsl:template match="code">
  <code>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </code>
</xsl:template>

<xsl:template match="keyb">
  <kbd>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </kbd>
</xsl:template>

<xsl:template match="var">
  <var>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </var>
</xsl:template>

<xsl:template match="file">
  <span>

    <xsl:call-template name="class">
      <xsl:with-param name="base-class">file</xsl:with-param>
    </xsl:call-template> 
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="plhld">
  <span>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">plhld</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template name="quote_left">
  <xsl:variable name="quote" select="$data/mumie:notions[@language=$language]/mumie:notion[@name='quote_left']/@value"/>
  <xsl:choose>
    <xsl:when test="$quote">
      <xsl:value-of select="$quote"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="error">
        <xsl:with-param name="message">Can't find left quote specification</xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="quote_right">
  <xsl:variable name="quote" select="$data/mumie:notions[@language=$language]/mumie:notion[@name='quote_right']/@value"/>
  <xsl:choose>
    <xsl:when test="$quote">
      <xsl:value-of select="$quote"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="error">
        <xsl:with-param name="message">Can't find right quote specification</xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="quoted">
  <xsl:call-template name="quote_left"/>
  <xsl:apply-templates/>
  <xsl:call-template name="quote_right"/>
</xsl:template>

<xsl:template match="meta">
  <span class="meta">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="optional">
  <span class="meta">[</span>
  <xsl:apply-templates/>
  <span class="meta">]</span>
</xsl:template>

<xsl:template match="alts">
  <xsl:variable name="suppress-parenthesis"
                select="local-name(..)='optional' and count(../*)=1 and count(../text())=0"/>
  <xsl:if test="not($suppress-parenthesis)">
    <span class="meta">(</span>
  </xsl:if>
  <xsl:for-each select="alt">
    <xsl:if test="position()!=1">
      <span class="meta">|</span>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:for-each>
  <xsl:if test="not($suppress-parenthesis)">
    <span class="meta">)</span>
  </xsl:if>
</xsl:template>

<xsl:template match="break">
  <br/>
  <xsl:if test="@space">
    <xsl:call-template name="vertical-space"/>
  </xsl:if>
</xsl:template>

<!--
   ==========================================================================================
   Links and anchors                          
   ==========================================================================================
-->

<xsl:template match="anchor">
  <a name="{@name}"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="link">
  <a>
    <xsl:copy-of select="@target"/>
    <xsl:call-template name="href"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </a>
</xsl:template>



<!--
   ==========================================================================================
   Preformatted and verbatim Text                    
   ==========================================================================================
-->

<xsl:template match="preformatted|verbatim">
  <pre>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">block</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </pre>
</xsl:template>

<xsl:template match="pref">
  <span>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">pref</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="verb">
  <span>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">verb</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>



<!--
   ==========================================================================================
   Lists
   ==========================================================================================
-->

<xsl:template match="list">
  <xsl:choose>
    <xsl:when test="@numbering='no'">
      <ul>
        <xsl:call-template name="class"/>
        <xsl:call-template name="id"/>
        <xsl:call-template name="balloon"/>
        <xsl:call-template name="onevent"/>
        <xsl:apply-templates/>
      </ul>
    </xsl:when>
    <xsl:when test="@numbering='yes'">
      <ol>
        <xsl:call-template name="class"/>
        <xsl:call-template name="id"/>
        <xsl:call-template name="balloon"/>
        <xsl:call-template name="onevent"/>
        <xsl:apply-templates/>
      </ol>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="list/item">
  <li>
    <xsl:if test="label">
      <xsl:attribute name="style">
        <xsl:value-of select="'list-style-type:none'"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </li>
</xsl:template>

<xsl:template match="list/item[label]/content/par[position()=1]">
  <p>
    <xsl:apply-templates select="../../label" mode="item-label"/>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="list/item/label">
  <!--
    Only processed in mode "item-label"
  -->
</xsl:template>

<xsl:template match="list/item/label" mode="item-label">
  <span class="label">
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="list/item/content">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="description-list">
  <dl>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </dl>
</xsl:template>

<xsl:template match="description-list/item">
  <dt>
    <xsl:if test="label">
      <xsl:apply-templates select="label"/>    
    </xsl:if>
  </dt>
  <dd>
    <xsl:call-template name="balloon">
      <xsl:with-param name="balloon" select="content/@balloon"/>
    </xsl:call-template>
    <xsl:apply-templates select="content"/>    
  </dd>
</xsl:template>



<!--
   ==========================================================================================
   Boxes
   ==========================================================================================
-->

<xsl:template name="setup-box-element">
  <xsl:call-template name="class">
    <xsl:with-param name="base-class">box</xsl:with-param>
  </xsl:call-template>
  <xsl:variable name="style">
    <xsl:if test="@width">
      <xsl:value-of select="concat('width:',@width,';')"/>
    </xsl:if>
    <xsl:if test="@height">
      <xsl:value-of select="concat('height:',@height,';')"/>
    </xsl:if>
    <xsl:if test="@content-align">
      <xsl:value-of select="concat('text-align:',@content-align)"/>
    </xsl:if>
    <xsl:if test="@content-valign">
      <xsl:value-of select="concat('vertical-align:',@content-valign)"/>
    </xsl:if>
  </xsl:variable>
  <xsl:if test="$style!=''">
    <xsl:attribute name="style">
      <xsl:value-of select="$style"/>
    </xsl:attribute>
  </xsl:if>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="box">
  <span>
    <xsl:call-template name="setup-box-element"/>
  </span>
</xsl:template>

<xsl:template match="block">
  <div>
    <xsl:call-template name="setup-box-element"/>
  </div>
</xsl:template>



<!--
   ==========================================================================================
   Horizontal floating                          
   ==========================================================================================
-->

<xsl:template match="float-left">
  <span style="float:left">
    <xsl:apply-templates/>
  </span>
  <xsl:if test="not(../text()) or string-length(normalize-space(../text()))=0">
    <xsl:text>&#160;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="float-right">
  <span style="float:right">
    <xsl:apply-templates/>
  </span>
  <xsl:if test="not(../text()) or string-length(normalize-space(../text()))=0">
    <xsl:text>&#160;</xsl:text>
  </xsl:if>
</xsl:template>


<!--
   ==========================================================================================
   Tables
   ==========================================================================================
-->

<xsl:template match="table">
  <table>
    <xsl:call-template name="class">
      <xsl:with-param name="base-class">genuine</xsl:with-param>
    </xsl:call-template>
    <xsl:copy-of select="@align|@valign"/>
    <xsl:apply-templates select="./thead"/>
    <xsl:apply-templates select="./tfoot"/>
    <xsl:apply-templates select="./tbody"/>
  </table>
</xsl:template>

<xsl:template match="table/thead">
  <thead>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </thead>
</xsl:template>

<xsl:template match="table/tbody">
  <tbody>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </tbody>
</xsl:template>
  
<xsl:template match="table/tfoot">
  <tfoot>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </tfoot>
</xsl:template>

<xsl:template match="trow">
  <tr>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="tcell">
  <td>
    <xsl:copy-of select="@rowspan|@colspan|@align|@valign"/>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
    <xsl:apply-templates/>
  </td>
</xsl:template>



<!--
   ==========================================================================================
   Multimedia
   ==========================================================================================
-->

<xsl:template name="image">
  <img>
    <xsl:copy-of select="@src|@alt"/>
    <xsl:call-template name="class"/>
    <xsl:call-template name="id"/>
    <xsl:call-template name="balloon"/>
    <xsl:call-template name="onevent"/>
  </img>
</xsl:template>

<xsl:template match="image">
  <xsl:choose>
    <xsl:when test="@align='left' or @align='right' or @align='center'">
      <div>
        <xsl:copy-of select="@align"/>
        <xsl:call-template name="class">
          <xsl:with-param name="base-class">image</xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="image"/>
      </div>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="image"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="balloon">
  <xsl:param name="balloon" select="@balloon"/>
  <xsl:if test="$balloon">
    <xsl:attribute name="title">
      <xsl:value-of select="$balloon"/>
    </xsl:attribute>
  </xsl:if>
</xsl:template>



<!--
   ==========================================================================================
   Numbers
   ==========================================================================================
-->

<xsl:template match="number">
  <xsl:choose>
    <xsl:when test="@format='arabic'">
      <xsl:number value="@value" format="1"/>
    </xsl:when>
    <xsl:when test="@format='roman'">
      <xsl:number value="@value" format="i"/>
    </xsl:when>
    <xsl:when test="@format='Roman'">
      <xsl:number value="@value" format="I"/>
    </xsl:when>
    <xsl:when test="@format='alph'">
      <xsl:number value="@value" format="a"/>
    </xsl:when>
    <xsl:when test="@format='Alph'">
      <xsl:number value="@value" format="A"/>
    </xsl:when>
    <xsl:when test="@format='footnote-symbol'">
      <xsl:value-of select="$data/mumie:footnote-symbols/mumie:footnote-symbol[position()=current()/@value]/text()"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:number value="@value" format="@format"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<!--
   ==========================================================================================
   Table of contents                          
   ==========================================================================================
-->


<xsl:template name="toc-title">
  <xsl:variable name="title"
    select="$data/mumie:notions[@language=$language]/mumie:notion[@name='toc_title']/@value"/>
  <xsl:choose>
    <xsl:when test="$title">
      <xsl:value-of select="$title"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="error">
        <xsl:with-param name="message">Can't find toc title</xsl:with-param>
      </xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="toc">
  <div class="toc">
    <h2>
      <xsl:call-template name="toc-title"/>
    </h2>
    <ul>
      <xsl:apply-templates/>
    </ul>
  </div>
</xsl:template>

<xsl:template match="toc-item[@class='section']">
  <xsl:choose>
    <xsl:when test="./toc-item">
      <li class="section">
        <xsl:apply-templates select="./toc-entry"/>
        <ul>
          <xsl:apply-templates select="./toc-item"/>
        </ul>
      </li>
    </xsl:when>
    <xsl:otherwise>
      <li class="section">
        <xsl:apply-templates select="./toc-entry"/>
      </li>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="toc-item/toc-entry[../@class='section']">
  <xsl:if test="../@no">
    <span class="number"><xsl:number value="../@no" format="1 "/></span>
  </xsl:if>
  <a>
    <xsl:call-template name="href">
      <xsl:with-param name="href" select="../@href"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="toc-item[@class='subsection']">
  <xsl:choose>
    <xsl:when test="./toc-item">
      <li class="subsection">
        <xsl:apply-templates select="./toc-entry"/>
          <ul>
            <xsl:apply-templates select="./toc-item"/>
          </ul>
      </li>
    </xsl:when>
    <xsl:otherwise>
      <li class="subsection">
        <xsl:apply-templates select="./toc-entry"/>
      </li>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="toc-item/toc-entry[../@class='subsection']">
  <xsl:if test="../../@no and ../@no">
    <span class="number">
      <xsl:number value="../../@no" format="1."/>
      <xsl:number value="../@no" format="1 "/>
    </span>
  </xsl:if>
  <a>
    <xsl:call-template name="href">
      <xsl:with-param name="href" select="../@href"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="toc-item[@class='subsubsection']">
  <xsl:choose>
    <xsl:when test="./toc-item">
      <li class="subsubsection">
        <xsl:apply-templates select="./toc-entry"/>
          <ul>
            <xsl:apply-templates select="./toc-item"/>
          </ul>
      </li>
    </xsl:when>
    <xsl:otherwise>
      <li class="subsubsection">
        <xsl:apply-templates select="./toc-entry"/>
      </li>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="toc-item/toc-entry[../@class='subsubsection']">
  <xsl:if test="../../../@no and ../../@no and ../@no">
    <span class="number">
      <xsl:number value="../../../@no" format="1."/>
      <xsl:number value="../../@no" format="1."/>
      <xsl:number value="../@no" format="1 "/>
    </span>
  </xsl:if>
  <a>
    <xsl:call-template name="href">
      <xsl:with-param name="href" select="../@href"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </a>
</xsl:template>


<!--
   ==========================================================================================
   Scratchpad
   ==========================================================================================
-->

<xsl:template name="get-struc-notation">
  <xsl:param name="struc-name"/>
  <xsl:param name="sep.number" select="'.'"/>
  <xsl:param name="sep.headline" select="' &gt; '"/>
  <xsl:param name="add-sep">
    <xsl:choose>
      <xsl:when test="$struc-name='subsubsection'">
        <xsl:value-of select="'no'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'yes'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>
      <xsl:message>
DEBUG: get-struc-notation:
  struc-name = <xsl:value-of select="$struc-name"/>
      </xsl:message>
  <xsl:variable name="struc" select="ancestor::*[local-name()=$struc-name]"/>
  <xsl:if test="$struc">
    <xsl:choose>
      <xsl:when test="$struc/@no">
        <xsl:value-of select="$struc/@no"/>
        <xsl:if test="$add-sep='yes'">
          <xsl:value-of select="$sep.number"/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="$struc/headline"/>
        <xsl:if test="$add-sep='yes'">
          <xsl:value-of select="$sep.headline"/>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template name="get-ref-notation">
  <xsl:choose>
    <xsl:when test="ancestor::subsubsection">
      <xsl:message>
DEBUG: get-ref-notation: ancestor subsubsection
      </xsl:message>
      <xsl:call-template name="get-struc-notation">
        <xsl:with-param name="struc-name" select="'section'"/>
      </xsl:call-template>
      <xsl:call-template name="get-struc-notation">
        <xsl:with-param name="struc-name" select="'subsection'"/>
      </xsl:call-template>
      <xsl:call-template name="get-struc-notation">
        <xsl:with-param name="struc-name" select="'subsubsection'"/>
        <xsl:with-param name="add-sep" select="'no'"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="ancestor::subsection">
      <xsl:message>
DEBUG: get-ref-notation: ancestor subsection
      </xsl:message>
      <xsl:call-template name="get-struc-notation">
        <xsl:with-param name="struc-name" select="'section'"/>
      </xsl:call-template>
      <xsl:call-template name="get-struc-notation">
        <xsl:with-param name="struc-name" select="'subsection'"/>
        <xsl:with-param name="add-sep" select="'no'"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test="ancestor::section">
      <xsl:message>
DEBUG: get-ref-notation: ancestor section
      </xsl:message>
      <xsl:call-template name="get-struc-notation">
        <xsl:with-param name="struc-name" select="'section'"/>
        <xsl:with-param name="add-sep" select="'no'"/>
      </xsl:call-template>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="test-ref-notation">
  <xsl:call-template name="get-ref-notation"/>
</xsl:template>

</xsl:stylesheet>

