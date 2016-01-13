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
   <!ENTITY br                "&#xA;">
   <!ENTITY sp                "  ">
  ]
>

<!--
   Author:  Tilman Rassy

   $Id: Document.xsl,v 1.3 2009/03/05 23:47:47 rassy Exp $
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-lib="xalan://net.mumie.srv.build.XSLExtLib"
                xmlns:dc="http://www.mumie.net/xml-namespace/declarations">

<xsl:output method="text"/>

<xsl:preserve-space elements="*"/>

<!-- ================================================================================ -->
<!-- h1: Global parameters and variables                                              -->
<!-- ================================================================================ -->

<!-- Document type for which the class is created -->
<xsl:param name="type"/>

<!-- Name of the class (not qualified) -->
<xsl:param name="class-name"/>

<!-- ================================================================================ -->
<!-- h1: Writing the class                                                            -->
<!-- ================================================================================ -->

<xsl:template match="/dc:declarations/dc:entity-types/dc:document-type[@name=$type]">
package net.mumie.srv.entities.documents;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import net.mumie.srv.entities.Document;
import net.mumie.srv.entities.AbstractDocument;
import net.mumie.srv.notions.Category;
import net.mumie.srv.notions.DbColumn;
import net.mumie.srv.notions.EntityType;
import net.mumie.srv.notions.Id;
import net.mumie.srv.notions.MediaType;
import net.mumie.srv.notions.RefType;
import net.mumie.srv.notions.TimeFormat;
import net.mumie.srv.notions.UseMode;
import net.mumie.srv.notions.XMLAttribute;
import net.mumie.srv.notions.XMLElement;
import net.mumie.srv.notions.XMLNamespace;
import net.mumie.srv.service.LookupNotifyable;
import net.mumie.srv.service.ServiceInstanceStatus;
import net.mumie.srv.service.ServiceStatus;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Represents a document of type <xsl:value-of select="$type"/>.
 */

public class <xsl:value-of select="$class-name"/> extends AbstractDocument
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * Role of this class as an Avalon component.
   * (&lt;code&gt;"net.mumie.srv.entities.Document"&lt;/code&gt;)
   */

  public static final String ROLE = "net.mumie.srv.entities.Document";

  /**
   * Hint for this class as an Avalon component.
   * (&lt;code&gt;"<xsl:value-of select="$type"/>"&lt;/code&gt;)
   */

  public static final String HINT = "<xsl:value-of select="$type"/>";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(<xsl:value-of select="$class-name"/>.class);

 <xsl:if test="x-lib:format()='text' and not(x-lib:isGeneric())">
  /**
   * Database columns needed in use mode {@link UseMode#SERVE SERVE}.
   */

  protected static final String[] DB_COLUMNS_SERVE =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      <xsl:if test="@has-category='yes'">DbColumn.CATEGORY,</xsl:if>
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.INFO_PAGE,
      DbColumn.THUMBNAIL,
      DbColumn.COPYRIGHT,
      <xsl:if test="@has-summary='yes'">DbColumn.SUMMARY,</xsl:if>
      DbColumn.CONTENT,
    };
  </xsl:if>  

  /**
   * Database columns needed in use mode {@link UseMode#INFO INFO}.
   */

  protected static final String[] DB_COLUMNS_INFO =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      <xsl:if test="@has-category='yes'">DbColumn.CATEGORY,</xsl:if>
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.CONTAINED_IN,
      <xsl:if test="not(x-lib:isGeneric())"> <!-- Start: if not generic -->
      DbColumn.VC_THREAD,
      DbColumn.VERSION,
      <xsl:if test="@has-qualified-name='yes'">DbColumn.QUALIFIED_NAME,</xsl:if>
      DbColumn.COPYRIGHT,
      DbColumn.CREATED,
      DbColumn.LAST_MODIFIED,
      DbColumn.HIDE,
      <xsl:if test="@has-class='yes'">DbColumn.CLASS,</xsl:if>
      <xsl:if test="@has-timeframe='yes'">
      DbColumn.TIMEFRAME_START,
      DbColumn.TIMEFRAME_END,
      </xsl:if>
      <xsl:if test="@has-width-and-height='yes'">
      DbColumn.WIDTH,
      DbColumn.HEIGHT,
      </xsl:if>
      <xsl:if test="@has-duration='yes'">DbColumn.DURATION,</xsl:if>
      <xsl:if test="@has-corrector='yes'">DbColumn.CORRECTOR,</xsl:if>
      DbColumn.CONTENT_TYPE,
      DbColumn.CONTENT_LENGTH,
      DbColumn.INFO_PAGE,
      DbColumn.THUMBNAIL,
      <xsl:if test="@has-summary='yes'">DbColumn.SUMMARY,</xsl:if>
      <xsl:if test="x-lib:format()='text'">DbColumn.CONTENT,</xsl:if>
      </xsl:if> <!-- End: if not generic -->
    };

  /**
   * Database columns needed in use mode {@link UseMode#COMPONENT COMPONENT}.
   */

  protected static final String[] DB_COLUMNS_COMPONENT =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      <xsl:if test="@has-category='yes'">DbColumn.CATEGORY,</xsl:if>
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.HIDE,
      <xsl:if test="not(x-lib:isGeneric())">
      DbColumn.VC_THREAD,
      DbColumn.VERSION,
      DbColumn.INFO_PAGE,
      DbColumn.THUMBNAIL,
      </xsl:if>
      <xsl:if test="@has-qualified-name='yes'">DbColumn.QUALIFIED_NAME,</xsl:if>
      <xsl:if test="@has-class='yes'">DbColumn.CLASS,</xsl:if>
      <xsl:if test="@has-timeframe='yes'">
      DbColumn.TIMEFRAME_START,
      DbColumn.TIMEFRAME_END,
      </xsl:if>
      DbColumn.CONTAINED_IN,
      <xsl:if test="@has-width-and-height='yes'">
      DbColumn.WIDTH,
      DbColumn.HEIGHT,
      </xsl:if>
      <xsl:if test="@has-duration='yes'">DbColumn.DURATION,</xsl:if>
    };

  /**
   * Database columns needed in use mode {@link UseMode#LINK LINK}.
   */

  protected static final String[] DB_COLUMNS_LINK =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      <xsl:if test="not(x-lib:isGeneric())">
      DbColumn.INFO_PAGE,
      DbColumn.THUMBNAIL,
      </xsl:if>
    };

  /**
   * Database columns needed in use mode {@link UseMode#CHECKOUT CHECKOUT}.
   */

  protected static final String[] DB_COLUMNS_CHECKOUT =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      <xsl:if test="@has-category='yes'">DbColumn.CATEGORY,</xsl:if>
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.CONTAINED_IN,
      <xsl:if test="not(x-lib:isGeneric())"> <!-- Start: if not generic -->
      DbColumn.VC_THREAD,
      DbColumn.VERSION,
      <xsl:if test="@has-qualified-name='yes'">DbColumn.QUALIFIED_NAME,</xsl:if>
      DbColumn.COPYRIGHT,
      DbColumn.CREATED,
      DbColumn.LAST_MODIFIED,
      DbColumn.HIDE,
      <xsl:if test="@has-class='yes'">DbColumn.CLASS,</xsl:if>
      <xsl:if test="@has-timeframe='yes'">
      DbColumn.TIMEFRAME_START,
      DbColumn.TIMEFRAME_END,
      </xsl:if>
      <xsl:if test="@has-width-and-height='yes'">
      DbColumn.WIDTH,
      DbColumn.HEIGHT,
      </xsl:if>
      <xsl:if test="@has-duration='yes'">DbColumn.DURATION,</xsl:if>
      <xsl:if test="@has-corrector='yes'">DbColumn.CORRECTOR,</xsl:if>
      DbColumn.CONTENT_TYPE,
      DbColumn.CONTENT_LENGTH,
      DbColumn.INFO_PAGE,
      DbColumn.THUMBNAIL,
      <xsl:if test="@has-summary='yes'">DbColumn.SUMMARY,</xsl:if>
      <xsl:if test="x-lib:format()='text'">DbColumn.CONTENT,</xsl:if>
      </xsl:if> <!-- End: if not generic -->
    };

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new &lt;code&gt;<xsl:value-of select="$class-name"/>&lt;/code&gt; instance.
   */

  public <xsl:value-of select="$class-name"/> ()
  {
    this.type = EntityType.<xsl:value-of select="x-lib:itemKey()"/>;
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Calls the superclass recycle method and increases the recycle counters.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Calls the superclass dispose method.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // toSAX methods
  // --------------------------------------------------------------------------------

  <xsl:if test="x-lib:format()='text' and not(x-lib:isGeneric())">
  /**
   * &lt;p&gt;
   *  Core part of the toSAX method if the use mode is {@link UseMode#SERVE SERVE}.
   * &lt;/p&gt;
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_SERVE";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. resultSet = " + resultSet);

    // Start root element:
    this.rootElementStartToSAX(contentHandler);

    // Write data:
    <xsl:if test="@has-category='yes'">this.categoryToSAX(resultSet, contentHandler);</xsl:if>
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    this.copyrightToSAX(resultSet, contentHandler);
    this.componentsToSAX(contentHandler);
    this.linksToSAX(contentHandler);
    <xsl:if test="@has-summary='yes'">this.summaryToSAX(resultSet, contentHandler);</xsl:if>
    this.contentToSAX(resultSet, contentHandler);

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }
  </xsl:if>

  /**
   * &lt;p&gt;
   *  Core part of the toSAX method if the use mode is {@link UseMode#INFO INFO}.
   * &lt;/p&gt;
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME =
      "toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. resultSet = " + resultSet);

    // Start root element:
    this.rootElementStartToSAX(contentHandler);

    // Write data:
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    <xsl:if test="@has-category='yes'">this.categoryToSAX(resultSet, contentHandler);</xsl:if>
    this.pureNameToSAX(resultSet, contentHandler);
    this.containedInToSAX(resultSet, contentHandler);
    <xsl:if test="not(x-lib:isGeneric())"> <!-- start: if not generic -->
    this.vcThreadToSAX(resultSet, contentHandler);
    this.versionToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    <xsl:if test="@has-qualified-name='yes'">
    this.qualifiedNameToSAX(resultSet, contentHandler);
    </xsl:if>
    this.copyrightToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.hideToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    <xsl:if test="@has-class='yes'">this.classToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@has-timeframe='yes'">
    this.timeframeStartToSAX(resultSet, contentHandler);
    this.timeframeEndToSAX(resultSet, contentHandler);
    </xsl:if>
    <xsl:if test="@has-width-and-height='yes'">
    this.widthToSAX(resultSet, contentHandler);
    this.heightToSAX(resultSet, contentHandler);
    </xsl:if>
    <xsl:if test="@has-duration='yes'">this.durationToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@has-corrector='yes'">this.correctorToSAX(resultSet, contentHandler);</xsl:if>
    this.contentTypeToSAX(resultSet, contentHandler);
    this.contentLengthToSAX(resultSet, contentHandler);
    this.infoPageToSAX(resultSet, contentHandler);
    <xsl:if test="@has-summary='yes'">this.summaryToSAX(resultSet, contentHandler);</xsl:if>
    this.readPermissionsToSAX(contentHandler);
    this.componentsToSAX(contentHandler);
    this.linksToSAX(contentHandler);
    <xsl:if test="x-lib:format()='text'">this.contentToSAX(resultSet, contentHandler);</xsl:if>
    </xsl:if> <!-- end: if not generic -->

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * &lt;p&gt;
   *  Core part of the toSAX method if the use mode is {@link UseMode#COMPONENT COMPONENT}.
   * &lt;/p&gt;
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_COMPONENT";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. resultSet = " + resultSet);

    // Start root element:
    this.rootElementStartToSAX(contentHandler);

    // Write data:
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.hideToSAX(resultSet, contentHandler);
    <xsl:if test="@has-qualified-name='yes'">
    this.qualifiedNameToSAX(resultSet, contentHandler);
    </xsl:if>
    <xsl:if test="@has-class='yes'">this.classToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@has-timeframe='yes'">
    this.timeframeStartToSAX(resultSet, contentHandler);
    this.timeframeEndToSAX(resultSet, contentHandler);
    </xsl:if>
    this.containedInToSAX(resultSet, contentHandler);
    <xsl:if test="@has-width-and-height='yes'">
    this.widthToSAX(resultSet, contentHandler);
    this.heightToSAX(resultSet, contentHandler);
    </xsl:if>
    <xsl:if test="@has-duration='yes'">this.durationToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@is-recursive-component='yes'">this.componentsToSAX(contentHandler);</xsl:if>
    <xsl:if test="@has-category='yes'">this.categoryToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="not(x-lib:isGeneric())">
    this.vcThreadToSAX(resultSet, contentHandler);
    this.versionToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    </xsl:if>

    // Write reference attributes (if necessary):
    if ( this.refId != Id.UNDEFINED )
      this.refAttribsToSAX(resultSet, contentHandler);

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * &lt;p&gt;
   *  Core part of the toSAX method if the use mode is {@link UseMode#LINK LINK}.
   * &lt;/p&gt;
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME =
      "toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. resultSet = " + resultSet);

    // Start root element:
    this.rootElementStartToSAX(contentHandler);

    // Write data:
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    <xsl:if test="not(x-lib:isGeneric())">
    this.thumbnailToSAX(resultSet, contentHandler);
    </xsl:if>

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * &lt;p&gt;
   *  Core part of the toSAX method if the use mode is {@link UseMode#CHECKOUT CHECKOUT}.
   * &lt;/p&gt;
   */

  protected void toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME =
      "toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. resultSet = " + resultSet);

    // Start root element:
    this.rootElementStartToSAX(contentHandler);

    // Write data:
    <xsl:if test="@has-category='yes'">this.categoryToSAX(resultSet, contentHandler);</xsl:if>
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    <xsl:if test="not(x-lib:isGeneric())"> <!-- start: if not generic -->
    this.vcThreadToSAX(resultSet, contentHandler);
    this.versionToSAX(resultSet, contentHandler);
    <xsl:if test="@has-qualified-name='yes'">
    this.qualifiedNameToSAX(resultSet, contentHandler);
    </xsl:if>
    this.copyrightToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    <xsl:if test="@has-class='yes'">this.classToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@has-timeframe='yes'">
    this.timeframeStartToSAX(resultSet, contentHandler);
    this.timeframeEndToSAX(resultSet, contentHandler);
    </xsl:if>
    this.containedInToSAX(resultSet, contentHandler);
    <xsl:if test="@has-width-and-height='yes'">
    this.widthToSAX(resultSet, contentHandler);
    this.heightToSAX(resultSet, contentHandler);
    </xsl:if>
    <xsl:if test="@has-duration='yes'">this.durationToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@has-corrector='yes'">this.correctorToSAX(resultSet, contentHandler);</xsl:if>
    this.contentTypeToSAX(resultSet, contentHandler);
    this.contentLengthToSAX(resultSet, contentHandler);
    this.infoPageToSAX(resultSet, contentHandler);
    <xsl:if test="@has-summary='yes'">this.summaryToSAX(resultSet, contentHandler);</xsl:if>
    this.readPermissionsToSAX(contentHandler);
    this.componentsToSAX(contentHandler);
    this.linksToSAX(contentHandler);
    <xsl:if test="x-lib:format()='text'">this.contentToSAX(resultSet, contentHandler);</xsl:if>
    </xsl:if>  <!-- end: if not generic -->

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // getDbColumns method
  // --------------------------------------------------------------------------------

  /**
   * Returns the database columns this document needs if one of the
   * &lt;code&gt;toSAX&lt;/code&gt; methods would be called.
   */

  public String[] getDbColumns ()
  {
    switch ( this.useMode )
      {
      <xsl:if test="x-lib:format()='text' and not(x-lib:isGeneric())">
      case UseMode.SERVE:
        return copyDbColums(DB_COLUMNS_SERVE, this.withPath);
      </xsl:if>
      case UseMode.INFO:
        return copyDbColums(DB_COLUMNS_INFO, this.withPath);
      case UseMode.COMPONENT:
        return copyDbColums(DB_COLUMNS_COMPONENT, this.withPath);
      case UseMode.LINK:
        return copyDbColums(DB_COLUMNS_LINK, this.withPath);
      case UseMode.CHECKOUT:
        return copyDbColums(DB_COLUMNS_CHECKOUT, this.withPath);
      default: return null;
      }
  }

  <xsl:if test="x-lib:format()='binary' and not(x-lib:isGeneric())">
  // --------------------------------------------------------------------------------
  // toStream methods
  // --------------------------------------------------------------------------------

  /**
   * Writes the content of this document to the specified output stream.
   */

  protected void toStream_SERVE (OutputStream out)
    throws IOException, SQLException
  {
    this.contentToStream(out);
  }
  </xsl:if>

  // --------------------------------------------------------------------------------
  // Identification method 
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this &lt;code&gt;<xsl:value-of select="$class-name"/>&lt;/code&gt; Instance. It has the
   * following form:&lt;pre&gt;
   *   "<xsl:value-of select="$class-name"/>" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ',' + {@link #id id}
   *   ',' + {@link #useMode useMode}
   *   ')'&lt;/pre&gt;
   * where &lt;code&gt;instanceId&lt;/code&gt; is the instance id and
   * &lt;code&gt;numberOfRecycles&lt;/code&gt; the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "<xsl:value-of select="$class-name"/>" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ')';
  }
}
</xsl:template>

<!-- ================================================================================ -->
<!-- h1: Top-level template                                                           -->
<!-- ================================================================================ -->

<xsl:template match="/">
  <xsl:apply-templates select="/dc:declarations/dc:entity-types/dc:document-type[@name=$type]"/>
</xsl:template>

</xsl:stylesheet>
