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

   $Id: DbHelper.xsl,v 1.14 2008/06/23 14:22:09 rassy Exp $

   XSL Stylesheet to generate the Java source code for the net.mumie.cocoon.db classes
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dbh="http://www.mumie.net/xml-namespace/db-helper"
                xmlns:h="http://www.mumie.net/xml-namespace/html-in-javadoc"
                xmlns:x-strutil="xalan://net.mumie.util.StringUtil"
                xmlns:x-ioutil="xalan://net.mumie.util.io.IOUtil"
                xmlns:x-autocoder="xalan://net.mumie.sql.SQLComposerAutocoder"
                version="1.0">

<xsl:output method="text"/>

<xsl:strip-space elements="*"/>
<xsl:preserve-space elements="dbh:body h:pre"/>

<!--
  ================================================================================
  Global parameters and variables
  ================================================================================
-->

<!--
   The target. Should be the non-qualified name of the Java class to create, without
   the ".java" suffix. E.g., "DbHelper", "AbstractDbHelper", "PostgreSQLDbHelper".
-->

<xsl:param name="target"/>

<!--
   Whether the target is the db helper interface. Must be "yes" or "no". Default is
   "yes" if $target is "DbHelper", otherwise "no".
-->

<xsl:param name="targetIsInterface">
  <xsl:choose>
    <xsl:when test="$target='DbHelper'">yes</xsl:when>
    <xsl:otherwise>no</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<!--
   The skeleton filename. Default is $target + ".skeleton".
-->

<xsl:param name="skeleton-filename" select="concat($target,'.skeleton')"/>

<!--
  The SQLComposerAutocoder instance
-->

<xsl:variable name="autocoder" select="x-autocoder:new()"/>

<!--
  ================================================================================
  Method creation
  ================================================================================
-->

<!--
   Creates a method.
-->

<xsl:template match="dbh:method">

  <xsl:param name="access">
    <xsl:choose>
      <xsl:when test="@access"><xsl:value-of select="@access"/></xsl:when>
      <xsl:otherwise>public</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:param name="static">
    <xsl:choose>
      <xsl:when test="@static"><xsl:value-of select="@static"/></xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:param name="final">
    <xsl:choose>
      <xsl:when test="@final"><xsl:value-of select="@final"/></xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:param name="returnType">
    <xsl:choose>
      <xsl:when test="@returnType"><xsl:value-of select="@returnType"/></xsl:when>
      <xsl:otherwise>ResultSet</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:param name="methodTarget">
    <xsl:choose>
      <xsl:when test="@target"><xsl:value-of select="@target"/></xsl:when>
      <xsl:otherwise>AbstractDbHelper</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:param name="toInterface">
    <xsl:choose>
      <xsl:when test="@toInterface"><xsl:value-of select="@toInterface"/></xsl:when>
      <xsl:when test="$access='public'">yes</xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:param name="logging">
    <xsl:call-template name="logging-enabled"/>
  </xsl:param>

  <xsl:if test="$methodTarget=$target or ($toInterface='yes' and $targetIsInterface='yes')">

    <!-- Javadoc comment: -->
    <xsl:if test="dbh:doc">
      <xsl:text>&br;</xsl:text>
      <xsl:text>&br;&sp;/**</xsl:text>
      <xsl:variable name="doc">
        <xsl:apply-templates select="dbh:doc"/>
      </xsl:variable>
      <xsl:value-of select="x-strutil:shiftTextLeft($doc, 3)"/>
      <xsl:text>  */&br;</xsl:text>
    </xsl:if>

    <!-- Method declaration: -->
    <xsl:text>&br;&sp;</xsl:text>
    <xsl:value-of select="$access"/>
    <xsl:if test="$static='yes'"> static</xsl:if>
    <xsl:if test="$final='yes'"> final</xsl:if>
    <xsl:text> </xsl:text>
    <xsl:value-of select="$returnType"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text> (</xsl:text>
    <xsl:for-each select="dbh:params/dbh:param">
      <xsl:if test="position()!=1">, </xsl:if>
      <xsl:value-of select="@type"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="@name"/>
    </xsl:for-each>
    <xsl:text>)</xsl:text>

    <xsl:if test="dbh:throws/dbh:throwable">
      <xsl:text>&br;&sp;&sp;throws</xsl:text>
      <xsl:for-each select="dbh:throws/dbh:throwable">
        <xsl:if test="position()!=1">,</xsl:if>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/>
      </xsl:for-each>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="$targetIsInterface='no'">
        <!-- Method body start: -->
        <xsl:text>&br;&sp;{</xsl:text>
        <xsl:if test="$logging='yes'">
          <!-- Define METHOD_NAME constant: -->
          <xsl:text>&br;&sp;&sp;final String METHOD_NAME = "</xsl:text>
          <xsl:value-of select="@name"/>
          <xsl:text>";</xsl:text>
          <!-- Log message if necessary: -->
          <xsl:if test="not(dbh:body//dbh:log)">
            <xsl:text>&br;&sp;&sp;</xsl:text>
            <xsl:call-template name="log">
              <xsl:with-param name="number">
                <xsl:text>1/</xsl:text>
                <xsl:choose>
                  <xsl:when test="dbh:body/dbh:returnResultSet">3</xsl:when>
                  <xsl:when test="dbh:body/dbh:executeInsertAndReturnId">3</xsl:when>
                  <xsl:when test="dbh:body/dbh:pgExecuteInsertAndReturnId">3</xsl:when>
                  <xsl:when test="dbh:body/dbh:executeUpdate">3</xsl:when>
                  <xsl:otherwise>1</xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="message">"Started"</xsl:with-param>
              <xsl:with-param name="printParams">yes</xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </xsl:if>
        <!-- Method body contents: -->
        <xsl:apply-templates select="dbh:body"/>
        <!-- Method body end: -->
        <xsl:text>}&br;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <!-- Interface, semicolon after method declaration -->
        <xsl:text>;&br;</xsl:text>
      </xsl:otherwise>      
    </xsl:choose>
    
  </xsl:if>

</xsl:template>

<xsl:template match="dbh:body">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="dbh:doc">
  <xsl:apply-templates/>
</xsl:template>

<!--
   ================================================================================
   Java code
   ================================================================================
-->

<xsl:template match="dbh:body/text()">
  <xsl:value-of select="x-strutil:shiftTextLeft(string(.),2)"/>
</xsl:template>

<!--
   ================================================================================
   Javadoc
   ================================================================================
-->

<!--
  Converts h:xxx elements into a xxx HTML elements.
-->

<xsl:template match="*[namespace-uri()='http://www.mumie.net/xml-namespace/html-in-javadoc']">
  <xsl:choose>
    <xsl:when test=".">
      <!-- Start tag: -->
      <xsl:text>&lt;</xsl:text>
      <xsl:value-of select="local-name()"/>
      <xsl:if test="@*">
        <xsl:for-each select="@*">
          <xsl:text> </xsl:text>
          <xsl:value-of select="name()"/>
          <xsl:text>="</xsl:text>
          <xsl:value-of select="."/>
          <xsl:text>"</xsl:text>
        </xsl:for-each>
      </xsl:if>
      <xsl:text>&gt;</xsl:text>
      <!-- Content: -->
      <xsl:apply-templates/>
      <!-- End tag: -->
      <xsl:text>&lt;/</xsl:text>
      <xsl:value-of select="local-name()"/>
      <xsl:text>&gt;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
      <!-- Empty element tag: -->
      <xsl:text>&lt;/</xsl:text>
      <xsl:value-of select="local-name()"/>
      <xsl:if test="@*">
        <xsl:for-each select="@*">
          <xsl:text> </xsl:text>
          <xsl:value-of select="name()"/>
          <xsl:text>="</xsl:text>
          <xsl:value-of select="."/>
          <xsl:text>"</xsl:text>
        </xsl:for-each>
      </xsl:if>
      <xsl:text>&gt;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>

</xsl:template>

<!--
  ================================================================================
  Logging
  ================================================================================
-->

<!--
  Returns "yes" if logging is enabled, otherwise "no". Logging can be enabled or disabled
  for each method. This can be controlled by the 'logging' attribute of the 'method'
  element: a value of 'yes' means logging is enabled, a value of 'no' means logging is
  disabled. Default is 'yes'.

  This template must be called from within a 'method' element.
-->

<xsl:template name="logging-enabled">
  <xsl:variable name="logging" select="ancestor-or-self::dbh:method/@logging"/>
  <xsl:choose>
    <xsl:when test="$logging"><xsl:value-of select="$logging"/></xsl:when>
    <xsl:otherwise>yes</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--
   Creates a log message. Recognizes the following parameters:

     number    The number of the message. Defaults to the 'number attribute of the
               current node.

     message   The text of the message. Defaults to the string value of the current
               node.

     printParams
               Must be "yes" or "no". If "yes", the parameters of the method are
               added to the message. 

   This template must be called from within a 'method' element.
-->

<xsl:template name="log">

  <xsl:param name="number">
    <xsl:choose>
      <xsl:when test="@number"><xsl:value-of select="@number"/></xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="count(preceding-sibling::dbh:log) + 1"/>
        <xsl:text>/</xsl:text>
        <xsl:value-of select="count(../dbh:log)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>
  <xsl:param name="message" select="."/>
  <xsl:param name="printParams">
    <xsl:choose>
      <xsl:when test="@printParams"><xsl:value-of select="@printParams"/></xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </xsl:param>
  <xsl:param name="params" select="ancestor-or-self::dbh:method/dbh:params/dbh:param[not(@log='no')]"/>
  <xsl:param name="linebreak">
    <xsl:choose>
      <xsl:when test="@linebreak"><xsl:value-of select="@linebreak"/></xsl:when>
      <xsl:when test="$printParams='yes'">yes</xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:text>this.logDebug</xsl:text>
  <xsl:if test="$linebreak='yes'">
    <xsl:text>&br;&sp;&sp;&sp;</xsl:text>
  </xsl:if>
  <xsl:text>(METHOD_NAME + " </xsl:text>
  <xsl:value-of select="$number"/>
  <xsl:text>: " + </xsl:text>
  <xsl:value-of select="x-strutil:shiftTextLeft(x-strutil:stripTrailingWhitespaces($message),1)"/>
  <xsl:if test="$printParams='yes' and $params">
    <xsl:text> + ". " +&br;</xsl:text>
    <xsl:for-each select="$params">
      <xsl:text>&sp;&sp;&sp; "</xsl:text>
      <xsl:if test="position()!=1">,</xsl:if>
      <xsl:text> </xsl:text>
      <xsl:value-of select="@name"/>
      <xsl:text> = " + </xsl:text>
      <xsl:choose>
        <xsl:when test="@hint='Identifyable'">
          <xsl:text>LogUtil.identify(</xsl:text>
          <xsl:value-of select="@name"/>
          <xsl:text>)</xsl:text>
        </xsl:when>
        <xsl:when test="@hint='time'">
          <xsl:text>LogUtil.timeToString(</xsl:text>
          <xsl:value-of select="@name"/>
          <xsl:text>)</xsl:text>
        </xsl:when>
        <xsl:when test="@hint='array' or contains(@type,'[]')">
          <xsl:text>LogUtil.arrayToString(</xsl:text>
          <xsl:value-of select="@name"/>
          <xsl:text>)</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@name"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="position()!=last()">
        <xsl:text> +&br;</xsl:text>
      </xsl:if>
    </xsl:for-each>
  </xsl:if>
  <xsl:text>);</xsl:text>
  
</xsl:template>

<!--
  Processes a 'dbh:log' element. Simply calls the named template "log".
-->

<xsl:template match="dbh:log">
  <xsl:call-template name="log"/>
</xsl:template>

<!--
   ================================================================================
   SQL composing
   ================================================================================
-->

<!--
  Processes a 'dbh:sqlComposer' element.
-->

<xsl:template match="dbh:sqlComposer">

  <!-- Check whether the sqlComposer must be cleared: -->
  <xsl:param name="clear">
    <xsl:choose>
      <xsl:when test="@clear"><xsl:value-of select="@clear"/></xsl:when>
      <xsl:when test=". = ../descendant::dbh:sqlComposer[1]">yes</xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <!-- Create code: -->
  <xsl:text>this.sqlComposer</xsl:text>
  <xsl:if test="$clear='yes'">
    <xsl:text>&br;&sp;&sp;&sp;.clear()</xsl:text>
  </xsl:if>
  <xsl:variable name="sqlCode" select="x-autocoder:convert($autocoder, string(.))"/>
  <xsl:value-of select="x-strutil:shiftTextLeft(x-strutil:stripTrailingWhitespaces($sqlCode), 2)"/>
  <xsl:text>;</xsl:text>

</xsl:template>

<!--
   ================================================================================
   SQL executing
   ================================================================================
-->

<!--
  Processes a 'dbh:returnResultSet' element.
-->

<xsl:template match="dbh:returnResultSet">
  <xsl:text>String query = this.sqlComposer.getCode();</xsl:text>

  <xsl:variable name="logging">
    <xsl:call-template name="logging-enabled"/>
  </xsl:variable>

  <xsl:variable name="statement-opts">
    <xsl:choose>
      <xsl:when test="@scrollable and @scrollable = 'yes'">
        <xsl:text>ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text></xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:choose>

    <xsl:when test="$logging='yes' and not(..//dbh:log)">

      <xsl:variable name="logNumber">
        <xsl:choose>
          <xsl:when test="@withLogNumber"><xsl:value-of select="@withLogNumber"/></xsl:when>
          <xsl:otherwise>2/3</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="nextLogNumber">
        <xsl:value-of select="string(number(substring-before($logNumber,'/')) + 1)"/>
        <xsl:text>/</xsl:text>
        <xsl:value-of select="substring-after($logNumber,'/')"/>
      </xsl:variable>

      <xsl:text>&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="log">
        <xsl:with-param name="number" select="$logNumber"/>
        <xsl:with-param name="message">"query = " + query</xsl:with-param>
      </xsl:call-template>

      <xsl:text>&br;&sp;&sp;ResultSet resultSet = </xsl:text>
      <xsl:text>this.connection.createStatement(</xsl:text>
      <xsl:value-of select="$statement-opts"/>
      <xsl:text>).executeQuery(query);</xsl:text>

      <xsl:text>&br;&sp;&sp;</xsl:text>
      <xsl:call-template name="log">
        <xsl:with-param name="number" select="$nextLogNumber"/>
        <xsl:with-param name="message">"Done"</xsl:with-param>
      </xsl:call-template>

      <xsl:text>&br;&sp;&sp;return resultSet;</xsl:text>
      
    </xsl:when>

    <xsl:otherwise>
      <xsl:text>&br;&sp;&sp;return this.connection.createStatement(</xsl:text>
      <xsl:value-of select="$statement-opts"/>
      <xsl:text>).executeQuery(query);</xsl:text>
    </xsl:otherwise>

  </xsl:choose>

</xsl:template>

<!--
  Processes a 'dbh:executeInsertAndReturnId' element.
-->

<xsl:template match="dbh:executeInsertAndReturnId">

  <xsl:variable name="logging">
    <xsl:call-template name="logging-enabled"/>
  </xsl:variable>

  <xsl:variable name="logNumber">
    <xsl:choose>
      <xsl:when test="@withLogNumber"><xsl:value-of select="@withLogNumber"/></xsl:when>
      <xsl:otherwise>2/3</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="nextLogNumber">
    <xsl:value-of select="string(number(substring-before($logNumber,'/')) + 1)"/>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="substring-after($logNumber,'/')"/>
  </xsl:variable>

  <xsl:text>String query = this.sqlComposer.getCode();</xsl:text>

  <xsl:if test="$logging='yes' and not(..//dbh:log)">
    <xsl:text>&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="log">
      <xsl:with-param name="number" select="$logNumber"/>
      <xsl:with-param name="message">"query = " + query</xsl:with-param>
    </xsl:call-template>
  </xsl:if>

  <xsl:text>&br;&sp;&sp;Statement statement = this.connection.createStatement();</xsl:text>
  <xsl:text>&br;&sp;&sp;statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);</xsl:text>
  <xsl:text>&br;&sp;&sp;ResultSet resultSet = statement.getGeneratedKeys();</xsl:text>
  <xsl:text>&br;&sp;&sp;if ( !resultSet.next() )</xsl:text>
  <xsl:text>&br;&sp;&sp;&sp;throw new SQLException</xsl:text>
  <xsl:text>("Can not get generated id (result set empty)");</xsl:text>
  <xsl:text>&br;&sp;&sp;int id = resultSet.getInt(DbColumn.ID);</xsl:text>
  <xsl:text>&br;&sp;&sp;if ( resultSet.wasNull() )</xsl:text>
  <xsl:text>&br;&sp;&sp;&sp;throw new SQLException</xsl:text>
  <xsl:text>("Can not get generated id (column SQL NULL)");</xsl:text>
  <xsl:text>&br;&sp;&sp;statement.close();</xsl:text>
  
  <xsl:if test="$logging='yes' and not(..//dbh:log)">
    <xsl:text>&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="log">
      <xsl:with-param name="number" select="$nextLogNumber"/>
      <xsl:with-param name="message">"Done. id = " + id</xsl:with-param>
    </xsl:call-template>
  </xsl:if>
  
  <xsl:text>&br;&sp;&sp;return id;</xsl:text>

</xsl:template>

<!--
  Processes a 'dbh:pgExecuteInsertAndReturnId' element.
-->

<xsl:template match="dbh:pgExecuteInsertAndReturnId">

  <xsl:variable name="logging">
    <xsl:call-template name="logging-enabled"/>
  </xsl:variable>

  <xsl:variable name="logNumber">
    <xsl:choose>
      <xsl:when test="@withLogNumber"><xsl:value-of select="@withLogNumber"/></xsl:when>
      <xsl:otherwise>2/3</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="nextLogNumber">
    <xsl:value-of select="string(number(substring-before($logNumber,'/')) + 1)"/>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="substring-after($logNumber,'/')"/>
  </xsl:variable>

  <xsl:text>String query = this.sqlComposer.getCode();</xsl:text>

  <xsl:if test="$logging='yes' and not(..//dbh:log)">
    <xsl:text>&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="log">
      <xsl:with-param name="number" select="$logNumber"/>
      <xsl:with-param name="message">"query = " + query</xsl:with-param>
    </xsl:call-template>
  </xsl:if>

  <xsl:text>&br;&sp;&sp;Statement statement = this.connection.createStatement();</xsl:text>
  <xsl:text>&br;&sp;&sp;statement.executeUpdate(query);</xsl:text>
  <xsl:text>&br;&sp;&sp;int id = this.getIdForOid(((PGStatement)statement).getLastOID(), </xsl:text>
  <xsl:value-of select="@table"/>
  <xsl:text>);</xsl:text>
  
  <xsl:if test="$logging='yes' and not(..//dbh:log)">
    <xsl:text>&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="log">
      <xsl:with-param name="number" select="$nextLogNumber"/>
      <xsl:with-param name="message">"Done. id = " + id</xsl:with-param>
    </xsl:call-template>
  </xsl:if>
  
  <xsl:text>&br;&sp;&sp;return id;</xsl:text>

</xsl:template>

<!--
  Processes a 'dbh:executeUpdate' element.
-->

<xsl:template match="dbh:executeUpdate">

  <xsl:variable name="logging">
    <xsl:call-template name="logging-enabled"/>
  </xsl:variable>

  <xsl:variable name="logNumber">
    <xsl:choose>
      <xsl:when test="@withLogNumber"><xsl:value-of select="@withLogNumber"/></xsl:when>
      <xsl:otherwise>2/3</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="nextLogNumber">
    <xsl:value-of select="string(number(substring-before($logNumber,'/')) + 1)"/>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="substring-after($logNumber,'/')"/>
  </xsl:variable>

  <xsl:text>String query = sqlComposer.getCode();</xsl:text>

  <xsl:if test="$logging='yes'">
    <xsl:text>&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="log">
      <xsl:with-param name="number" select="$logNumber"/>
      <xsl:with-param name="message">"query = " + query</xsl:with-param>
    </xsl:call-template>
  </xsl:if>

  <xsl:text>&br;&sp;&sp;Statement statement = this.connection.createStatement();</xsl:text>
  <xsl:text>&br;&sp;&sp;int rowCount = statement.executeUpdate(query);</xsl:text>
  <xsl:text>&br;&sp;&sp;statement.close();</xsl:text>

  <xsl:if test="@expectedRowCount and @expectedRowCount!=''">
    <xsl:text>&br;&sp;&sp;if ( rowCount != </xsl:text>
    <xsl:value-of select="@expectedRowCount"/>
    <xsl:text> )</xsl:text>
    <xsl:text>&br;&sp;&sp;&sp;throw new SQLException</xsl:text>
    <xsl:text>&br;&sp;&sp;&sp;&sp;("Unexpected row count: " + rowCount + " (expected </xsl:text>
    <xsl:value-of select="@expectedRowCount"/>
    <xsl:text>)");</xsl:text>
  </xsl:if>

  <xsl:if test="$logging='yes'">
    <xsl:text>&br;&sp;&sp;</xsl:text>
    <xsl:call-template name="log">
      <xsl:with-param name="number" select="$nextLogNumber"/>
      <xsl:with-param name="message">"Done. rowCount = " + rowCount</xsl:with-param>
    </xsl:call-template>
  </xsl:if>

  <xsl:text>&br;&sp;&sp;return rowCount;</xsl:text>

</xsl:template>

<!--
   ================================================================================
   Method signature
   ================================================================================
-->

<!--
  Returns the signature od a method.
-->

<xsl:template name="method-signature">
  <xsl:param name="method" select="ancestor-or-self::dbh:method"/>
  <xsl:value-of select="$method/@name"/>
  <xsl:text>(</xsl:text>
  <xsl:for-each select="$method/dbh:params/dbh:param">
    <xsl:if test="position() != 1">,</xsl:if>
    <xsl:value-of select="@type"/>
  </xsl:for-each>
  <xsl:text>)</xsl:text>
</xsl:template>

<!--
   ================================================================================
   Writing the class
   ================================================================================
-->

<xsl:template match="/">
  <xsl:variable name="skeleton" select="x-ioutil:readFile($skeleton-filename)"/>
  <xsl:variable name="autocoded">//#AUTOCODED</xsl:variable>
  <xsl:value-of select="substring-before($skeleton,$autocoded)"/>
  <xsl:apply-templates select="/*/*"/>
  <xsl:value-of select="substring-after($skeleton,$autocoded)"/>
</xsl:template>

</xsl:stylesheet>
