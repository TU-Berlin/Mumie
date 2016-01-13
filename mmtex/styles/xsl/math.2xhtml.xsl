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
                xmlns:mtx="http://www.mumie.net/xml-namespace/mmtex"
                xmlns:mathml="http://www.w3.org/1998/Math/MathML"
                xmlns:mathml-ext="http://www.mumie.net/xml-namespace/mathml-ext"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="mtx mathml">


<!-- ================================================================================ -->
<!-- h1: Parameters                                                                   -->
<!-- ================================================================================ -->

<!--
   The MathML namespace
-->

<xsl:param name="mathml-namespace">http://www.w3.org/1998/Math/MathML</xsl:param>

<!--
   How vector symbols are to be displayed.
-->

<xsl:param name="vector-mode" select="'arrow'"/>

<!--
   Left parenthesis of row vectors.
-->

<xsl:param name="vector-paren.row.left" select="'('"/>

<!--
   Right parenthesis of row vectors.
-->

<xsl:param name="vector-paren.row.right" select="')'"/>

<!--
   Left parenthesis of column vectors.
-->

<xsl:param name="vector-paren.column.left" select="'('"/>

<!--
   Right parenthesis of column vectors.
-->

<xsl:param name="vector-paren.column.right" select="')'"/>

<!--
   Left parenthesis of inner products
-->

<xsl:param name="inner-product-paren.left" select="'&#x02329;'"/>

<!--
   Right parenthesis of inner products
-->

<xsl:param name="inner-product-paren.right" select="'&#x0232A;'"/>

<!--
   Mid symbol of inner products
-->

<xsl:param name="inner-product-mid-symbol" select="','"/>

<!--
   How number set symbols are to be displayed.
-->

<xsl:param name="number-set-mode" select="'double-struck'"/>

<!--
   Symbol for identical map
-->

<xsl:param name="id-map-symbol" select="'id'"/>

<!--
   Left and right parenthesis for norm
-->

<xsl:param name="norm-paren.left"  select="'||'"/>
<xsl:param name="norm-paren.right" select="'||'"/>
<xsl:param name="norm-R-paren.left"  select="'|'"/>
<xsl:param name="norm-R-paren.right" select="'|'"/>

<!--
   Left and right parenthesis for several matrix types
-->

<xsl:param name="vmatrix-paren.left"  select="'|'"/>
<xsl:param name="vmatrix-paren.right" select="'|'"/>

<xsl:param name="Vmatrix-paren.left"  select="'||'"/>
<xsl:param name="Vmatrix-paren.right" select="'||'"/>

<xsl:param name="pmatrix-paren.left"  select="'['"/>
<xsl:param name="pmatrix-paren.right" select="']'"/>

<xsl:param name="bmatrix-paren.left"  select="'['"/>
<xsl:param name="bmatrix-paren.right" select="']'"/>

<xsl:param name="ematrix-paren.left"  select="''"/>
<xsl:param name="ematrix-paren.right" select="''"/>

<xsl:param name="cases-matrix-paren.left"  select="'{'"/>
<xsl:param name="cases-matrix-paren.right" select="''"/>

<xsl:param name="small-matrix-paren.left"  select="''"/>
<xsl:param name="small-matrix-paren.right" select="''"/>

<!--
   The arrow symbol above vector symbols (effect only when $vector-mode is "arrow").
-->

<xsl:param name="vector-arrow" select="'&#x02192;'"/>

<!-- ================================================================================ -->
<!-- h1: Double-struck symbols                                                        -->
<!-- ================================================================================ -->

<!--
  Template mapping letters (A - Z) to the corresponding double struck symbols. Usefull
  for number set symbols.

  NOTE [Mar 2004]: Currently not all double struck letters can be displayed in
                   Mozilla.
-->


<xsl:template name="double-struck">
  <xsl:param name="char"/>
  <xsl:choose>
    <xsl:when test="$char='A'">&#x1D538;</xsl:when>
    <xsl:when test="$char='B'">&#x1D539;</xsl:when>
    <xsl:when test="$char='C'">&#x02102;</xsl:when>
    <xsl:when test="$char='D'">&#x1D53B;</xsl:when>
    <xsl:when test="$char='E'">&#x1D53C;</xsl:when>
    <xsl:when test="$char='F'">&#x1D53D;</xsl:when>
    <xsl:when test="$char='G'">&#x1D53E;</xsl:when>
    <xsl:when test="$char='H'">&#x0210D;</xsl:when>
    <xsl:when test="$char='I'">&#x1D540;</xsl:when>
    <xsl:when test="$char='J'">&#x1D541;</xsl:when>
    <xsl:when test="$char='K'">&#x1D542;</xsl:when>
    <xsl:when test="$char='L'">&#x1D543;</xsl:when>
    <xsl:when test="$char='M'">&#x1D544;</xsl:when>
    <xsl:when test="$char='N'">&#x02115;</xsl:when>
    <xsl:when test="$char='O'">&#x1D546;</xsl:when>
    <xsl:when test="$char='P'">&#x02119;</xsl:when>
    <xsl:when test="$char='Q'">&#x0211A;</xsl:when>
    <xsl:when test="$char='R'">&#x0211D;</xsl:when>
    <xsl:when test="$char='S'">&#x1D54A;</xsl:when>
    <xsl:when test="$char='T'">&#x1D54B;</xsl:when>
    <xsl:when test="$char='U'">&#x1D54C;</xsl:when>
    <xsl:when test="$char='V'">&#x1D54D;</xsl:when>
    <xsl:when test="$char='W'">&#x1D54E;</xsl:when>
    <xsl:when test="$char='X'">&#x1D54F;</xsl:when>
    <xsl:when test="$char='Y'">&#x1D550;</xsl:when>
    <xsl:when test="$char='Z'">&#x02124;</xsl:when>
    <xsl:otherwise><xsl:value-of select="$char"/></xsl:otherwise>
  </xsl:choose>
  <xsl:value-of select="$char"/>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Auxiliaries                                                                  -->
<!-- ================================================================================ -->

<!--
   Creates an equation number. The value is taken from the "no" attribute of the
   current node.
-->

<xsl:template name="equation-number">
  <xsl:text>(</xsl:text>
  <xsl:value-of select="@equation-no"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Top level structures                                                         -->
<!-- ================================================================================ -->

<!--
   Handels the "displaymath" environment, which is represented by the "displaymath"
   XML element.
-->

<xsl:template match="mtx:displaymath">
  <xsl:apply-templates/>
</xsl:template>


<!--
   Handels the "equation" environment, which is represented by the "equation" XML
   element.
-->

<xsl:template match="mathml:math" mode="equation-table">
  <table class="equation">
    <tr>
      <td class="left-margin">
      </td>
      <td class="all">
        <xsl:element name="math" namespace="{$mathml-namespace}"> 
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates mode="math"/>
        </xsl:element> 
      </td>
      <td class="right-margin">
        <xsl:call-template name="equation-number"/>
      </td>
    </tr>
  </table>
</xsl:template>

<!--
   Handels the MathML top-level element "math".
-->

<xsl:template match="mathml:math">
  <xsl:choose>

    <!-- Create equation number -->
    <xsl:when test="@equation-no">
     <xsl:apply-templates select="." mode="equation-table"/>
    </xsl:when>

    <!-- No equation numbering -->
    <xsl:otherwise> 
      <xsl:element name="math" namespace="{$mathml-namespace}"> 
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates mode="math"/>
      </xsl:element> 
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: General MathML elements                                                      -->
<!-- ================================================================================ -->

<!--
   Copies an MathML element to the result tree.
-->

<xsl:template match="*" mode="math">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="math"/>
  </xsl:copy>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Number sets                                                                  -->
<!-- ================================================================================ -->

<!--
   Handles symbols that denote number sets.
-->

<xsl:template match="mathml:mi[@class='number-set']" mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <xsl:variable name="key" select="normalize-space(.)"/>
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:choose>
      <xsl:when test="$number-set-mode='double-struck'">
        <xsl:call-template name="double-struck">
          <xsl:with-param name="char" select="$key"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="mathvariant">
          <xsl:value-of select="$number-set-mode"/>
        </xsl:attribute>
        <xsl:value-of select="$key"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:copy>
</xsl:template>


<!-- ================================================================================ -->
<!-- h1: Matrices                                                                     -->
<!-- ================================================================================ -->

<!--
  Sets the appropriate parenthesis for the mtable.
-->

<xsl:template match="mathml:mtable[contains(@class,'vmatrix') or
                                   contains(@class,'Vmatrix') or  
                                   contains(@class,'pmatrix') or  
                                   contains(@class,'bmatrix') or  
                                   contains(@class,'ematrix') or  
                                   contains(@class,'cases-matrix') or  
                                   contains(@class,'small-matrix')]"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <mfenced>

    <xsl:attribute name="open">
      <xsl:choose>
        <xsl:when test="contains(@class,'vmatrix')"><xsl:value-of select="$vmatrix-paren.left"/></xsl:when>
        <xsl:when test="contains(@class,'Vmatrix')"><xsl:value-of select="$Vmatrix-paren.left"/></xsl:when>
        <xsl:when test="contains(@class,'bmatrix')"><xsl:value-of select="$bmatrix-paren.left"/></xsl:when>
        <xsl:when test="contains(@class,'pmatrix')"><xsl:value-of select="$pmatrix-paren.left"/></xsl:when>
        <xsl:when test="contains(@class,'ematrix')"><xsl:value-of select="$ematrix-paren.left"/></xsl:when>
        <xsl:when test="contains(@class,'cases-matrix')"><xsl:value-of select="$cases-matrix-paren.left"/></xsl:when>
        <xsl:when test="contains(@class,'small-matrix')"><xsl:value-of select="$small-matrix-paren.left"/></xsl:when>
      </xsl:choose>
    </xsl:attribute>

    <xsl:attribute name="close">
      <xsl:choose>
        <xsl:when test="contains(@class,'vmatrix')"><xsl:value-of select="$vmatrix-paren.right"/></xsl:when>
        <xsl:when test="contains(@class,'Vmatrix')"><xsl:value-of select="$Vmatrix-paren.right"/></xsl:when>
        <xsl:when test="contains(@class,'bmatrix')"><xsl:value-of select="$bmatrix-paren.right"/></xsl:when>
        <xsl:when test="contains(@class,'ematrix')"><xsl:value-of select="$ematrix-paren.right"/></xsl:when>
        <xsl:when test="contains(@class,'pmatrix')"><xsl:value-of select="$pmatrix-paren.right"/></xsl:when>
        <xsl:when test="contains(@class,'cases-matrix')"><xsl:value-of select="$cases-matrix-paren.right"/></xsl:when>
        <xsl:when test="contains(@class,'small-matrix')"><xsl:value-of select="$small-matrix-paren.right"/></xsl:when>
      </xsl:choose>
    </xsl:attribute>

    <mtable>
      <xsl:apply-templates mode="math"/>
    </mtable>

  </mfenced>

</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Vectors                                                                      -->
<!-- ================================================================================ -->

<!--
   Handles symbols denoting vectors, which are represented in MathML by elements of
   class "vector".
-->

<xsl:template match="*[@class='vector' or
                       contains(@class,'vector ') or
                       contains(@class,' vector')]"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">

  <xsl:param name="local-vector-mode">
    <xsl:choose>
      <xsl:when test="contains(@class,'monospace')">monospace</xsl:when>
      <xsl:when test="contains(@class,'sans-serif')">plain</xsl:when>
      <xsl:when test="contains(@class,'script')">script</xsl:when>
      <xsl:when test="contains(@class,'double-struck')">double-struck</xsl:when>
      <xsl:when test="contains(@class,'bold-fract')">bold-fract</xsl:when>
      <xsl:when test="contains(@class,'bold-italic')">bold-italic</xsl:when>
      <xsl:when test="contains(@class,'underline')">underline</xsl:when>
      <xsl:when test="contains(@class,'arrow')">arrow</xsl:when>
      <xsl:when test="contains(@class,'bold')">bold</xsl:when>
      <xsl:when test="contains(@class,'frakt')">fraktur</xsl:when>
      <xsl:when test="contains(@class,'italic')">fraktur</xsl:when>
      <xsl:when test="contains(@class,'plain')">none</xsl:when>
      <xsl:otherwise><xsl:value-of select="$vector-mode"/></xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:choose>
    <!-- No special style: -->
    <xsl:when test="$local-vector-mode='none'">
      <xsl:apply-templates mode="math"/>
    </xsl:when>
    <!-- Arrow style: -->
    <xsl:when test="$local-vector-mode='arrow'">
      <mover>
        <xsl:copy-of select="@*"/>
        <xsl:copy-of select="."/>
        <mo class="vector-arrow"><xsl:value-of select="$vector-arrow"/></mo>
      </mover>
    </xsl:when>
    <!-- Underline style: -->
    <xsl:when test="$local-vector-mode='underline'">
      <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:attribute name="style">text-decoration:underline</xsl:attribute>
        <xsl:apply-templates mode="math"/>
      </xsl:copy>
    </xsl:when>
    <!-- Other styles: -->
    <xsl:otherwise>
      <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:attribute name="mathvariant">
          <xsl:value-of select="$local-vector-mode"/>
        </xsl:attribute>
        <xsl:apply-templates mode="math"/>
      </xsl:copy>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Handles row vectors, which are represented in MathML by "mrow" elements of
   class "row-vector".
 -->

<xsl:template match="mathml:mrow[@class='row-vector' or
                                 contains(@class,' row-vector') or
                                 contains(@class,'row-vector ')]"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <mfenced close=')'>
    <xsl:attribute name="open"><xsl:value-of select="$vector-paren.row.left"/></xsl:attribute>
    <xsl:attribute name="close"><xsl:value-of select="$vector-paren.row.right"/></xsl:attribute>
    <mtable>
      <xsl:attribute name="class"><xsl:value-of select="'row-vector'"/></xsl:attribute>
      <mtr>
        <xsl:for-each select="./*">
          <mtd>
            <xsl:apply-templates select="." mode="math"/>
            <xsl:if test="position() != last()">
              <mo>,</mo>
            </xsl:if>
          </mtd>
        </xsl:for-each>
      </mtr>
    </mtable>
  </mfenced>
</xsl:template>

<!--
   Handels column vectors, which are represented in MathML by "mrow" elements of
   class "column-vector".
 -->

<xsl:template match="mathml:mrow[@class='column-vector' or
                                 contains(@class,' column-vector') or
                                 contains(@class,'column-vector ')]"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <mfenced open='(' close=')'>
    <xsl:attribute name="open">
      <xsl:value-of select="$vector-paren.column.left"/>
    </xsl:attribute>
    <xsl:attribute name="close">
      <xsl:value-of select="$vector-paren.column.right"/>
    </xsl:attribute>
    <mtable>
      <xsl:attribute name="class">  
      <xsl:value-of select="'column-vector'"/>
      </xsl:attribute>
      <xsl:for-each select="./*">
        <mtr>
          <mtd>
            <xsl:apply-templates select="." mode="math"/>
          </mtd>
        </mtr>
      </xsl:for-each>
    </mtable>
  </mfenced>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Vectorspace and affine space                                                 -->
<!-- ================================================================================ -->

<!--
  Handles symbols denoting vector spaces
-->

<xsl:template match="*[@class='vectorspace-name']"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="math"/>
  </xsl:copy>
</xsl:template>

<!--
  Handles symbols denoting affine spaces
-->

<xsl:template match="*[@class='affinespace-name']"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates mode="math"/>
  </xsl:copy>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Norm                                                                         -->
<!-- ================================================================================ -->

<!--
  Handles symbols denoting norms (very similar to inner products)
-->

<xsl:template match="mathml:norm" mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">

  <xsl:choose>
    <xsl:when test="@class='norm-R'">
      <mfenced open="{$norm-R-paren.left}" close="{$norm-R-paren.right}">
        <xsl:apply-templates mode='math'/>
      </mfenced>
    </xsl:when>
    <xsl:otherwise>
      <mfenced open="{$norm-paren.left}" close="{$norm-paren.right}">
        <xsl:apply-templates mode='math'/>
      </mfenced>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Inner products                                                               -->
<!-- ================================================================================ -->

<xsl:template match="mathml-ext:innerproduct" mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <mfenced>
    <xsl:if test="$inner-product-paren.left and $inner-product-paren.left!=''">
      <xsl:attribute name="open">
        <xsl:value-of select="$inner-product-paren.left"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="$inner-product-paren.right and $inner-product-paren.right!=''">
      <xsl:attribute name="close">
        <xsl:value-of select="$inner-product-paren.right"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="$inner-product-mid-symbol and $inner-product-mid-symbol!=''">
      <xsl:attribute name="separators">
        <xsl:value-of select="$inner-product-mid-symbol"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates mode="math"/>
  </mfenced>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Identical map                                                                -->
<!-- ================================================================================ -->

<xsl:template match="mathml:idmap" mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">

  <xsl:if test="not(*)">
      <mi>
        <xsl:value-of select="$id-map-symbol"/>
      </mi>
  </xsl:if>

  <xsl:if test="*">
    <msub>
      <mi>
        <xsl:value-of select="$id-map-symbol"/>
      </mi>
      <mrow>
        <xsl:apply-templates mode='math'/>
      </mrow>
    </msub>
  </xsl:if>

</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Normal text in formulas                                                      -->
<!-- ================================================================================ -->

<xsl:template match="mtx:non-math" mode="math">
  <span xmlns="http://www.w3.org/1999/xhtml" class="embedded-html">
    <xsl:apply-templates/>
  </span>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Style corrections for indices                                                -->
<!-- ================================================================================ -->

<xsl:template match="mathml:msub/*[position()=2 and local-name()='mtable']|mathml:msup/*[position()=2 and local-name()='mtable']|mathml:msubsup/*[position()!=1 and local-name()='mtable']"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <xsl:copy>
    <xsl:attribute name="class">
      <xsl:choose>
        <xsl:when test="@class">
          <xsl:value-of select="concat(@class,' script')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'script'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
    <xsl:copy-of select="@*[local-name()!='class']"/>
    <xsl:apply-templates mode="math"/>
  </xsl:copy>
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Style corrections for under/overline                                         -->
<!-- ================================================================================ -->

<!-- Matches each 'mover' which fullfills the following conditions:

    o  The second child is a 'mo'

    o  The content of the second child is a bar character

    o  The content of the first child has child elements

  'mover' elements of that type represent overlined expressions which consist not only of
  one symbol. In that cases, the overline bar is too small. This templates applies a
  workaround: the overlined expression is wrapped by a mrow of class 'overline', which
  causes the CSS stylesheet to draw a top border line. The surrounding 'mover' and the 'mo'
  are removed by this template. -->

<xsl:template match="mathml:mover[local-name(*[2])='mo' and normalize-space(*[2])='&#175;'
                                  and *[1]/*]"
              mode="math"
              xmlns="http://www.w3.org/1998/Math/MathML">
  <mrow class="overline">
    <xsl:apply-templates select="*[1]" mode="math"/>
  </mrow>
</xsl:template>

</xsl:stylesheet>