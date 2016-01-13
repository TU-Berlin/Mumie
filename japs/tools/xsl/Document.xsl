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

   $Id: Document.xsl,v 1.89 2010/01/03 21:39:09 rassy Exp $
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:x-util="xalan://net.mumie.util.Util"
                xmlns:x-redirect="org.apache.xalan.xslt.extensions.Redirect"
                extension-element-prefixes="x-redirect">

<xsl:import href="config.xsl"/>

<xsl:output method="text"/>

<xsl:preserve-space elements="*"/>

<!--
   ==========================================================================================
   Global parameters and variables
   ==========================================================================================
-->

<!--
   Filename of the source
-->

<xsl:param name="source.filename" select="/*/@filename"/>

<!--
   CVS id of the source
-->

<xsl:param name="source.cvs-id" select="/*/@cvs-id"/>

<!--
   CVS id of the source with the $'s removed
-->

<xsl:variable name="source.cvs-id.protected">
  <xsl:call-template name="config.cvs-keyword">
    <xsl:with-param name="value" select="$source.cvs-id"/>
  </xsl:call-template>
</xsl:variable>

<!--
   Filename of this XSL script
-->

<xsl:param name="this.filename">Document.xsl</xsl:param>

<!--
   Directory of this (and the imported) XSL scripts; as an absolute filename.
-->

<xsl:param name="xsl-dir"/>

<!--
   CVS id of this XSL script
-->

<xsl:param name="this.cvs-id">$Id: Document.xsl,v 1.89 2010/01/03 21:39:09 rassy Exp $</xsl:param>

<!--
   CVS id of this XSL stylesheet with the $'s removed
-->

<xsl:variable name="this.cvs-id.protected">
  <xsl:call-template name="config.cvs-keyword">
    <xsl:with-param name="value" select="$this.cvs-id"/>
  </xsl:call-template>
</xsl:variable>

<!--
   The mode of this stylesheet. Possible values are "create" (create the sources) and "delete"
   (delete the sources). Default is "create".
-->
<xsl:param name="this.mode">create</xsl:param>

<!--
   Whether sources should be created even if they are up-to-date.
-->

<xsl:param name="force">no</xsl:param>

<!--
   Package name of the documents classes
-->

<xsl:param name="package.documents">net.mumie.cocoon.documents</xsl:param>

<!--
   Package name of the notions classes
-->

<xsl:param name="package.notions">net.mumie.cocoon.notions</xsl:param>

<!--
   The root directory of the Java source tree; as an absolute filename
-->

<xsl:param name="base-dir"/>

<!--
   Package path of the document classes, relative to $base-dir
-->

<xsl:param name="package-path.documents"
           select="translate($package.documents,'.','/')"/>

<!--
   Package path of the notions classes, relative to $base-dir
-->

<xsl:param name="package-path.notions"
           select="translate($package.notions,'.','/')"/>

<!--
   =====================================================================================
   Templates for the column arrays
   =====================================================================================
-->

<xsl:template name="db-columns-serve"><!-- 
   -->DbColumn.ID,
      DbColumn.PURE_NAME,
      <xsl:if test="@has-category='yes'">DbColumn.CATEGORY,</xsl:if>
      <xsl:if test="@has-class='yes'">DbColumn.CLASS,</xsl:if>
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.INFO_PAGE,
      DbColumn.THUMBNAIL,
      DbColumn.COPYRIGHT,
      <xsl:if test="@has-summary='yes'">DbColumn.SUMMARY,</xsl:if>
      <xsl:if test="@may-be-wrapper='yes'">DbColumn.IS_WRAPPER,</xsl:if>
      DbColumn.CONTENT,
</xsl:template>

<xsl:template name="db-columns-info">
  <xsl:param name="is-generic"/>
  <xsl:param name="format"/><!-- 
   -->
      DbColumn.ID,
      DbColumn.PURE_NAME,
      <xsl:if test="@has-category='yes'">DbColumn.CATEGORY,</xsl:if>
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.CONTAINED_IN,
      <xsl:if test="@may-be-wrapper='yes'">DbColumn.IS_WRAPPER,</xsl:if>
      <xsl:choose>
      <xsl:when test="$is-generic='no'">
      DbColumn.VC_THREAD,
      DbColumn.VERSION,
      <xsl:if test="@has-qualified-name='yes'">DbColumn.QUALIFIED_NAME,</xsl:if>
      DbColumn.COPYRIGHT,
      DbColumn.CREATED,
      DbColumn.LAST_MODIFIED,
      DbColumn.DELETED,
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
      DbColumn.CUSTOM_METAINFO,
      <xsl:if test="@has-summary='yes'">DbColumn.SUMMARY,</xsl:if>
      <xsl:if test="$format='text'">DbColumn.CONTENT,</xsl:if>
      </xsl:when>
      <xsl:otherwise>
      </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="db-columns-component">
  <xsl:param name="is-generic"/><!-- 
   -->
      DbColumn.ID,
      DbColumn.PURE_NAME,
      <xsl:if test="@has-category='yes'">DbColumn.CATEGORY,</xsl:if>
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.DELETED,
      DbColumn.HIDE,
      <xsl:if test="@may-be-wrapper='yes'">DbColumn.IS_WRAPPER,</xsl:if>
      <xsl:if test="$is-generic='no'">
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
</xsl:template>

<xsl:template name="db-columns-link">
  <xsl:param name="is-generic"/><!-- 
   -->DbColumn.ID,
      DbColumn.PURE_NAME,
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      <xsl:if test="@may-be-wrapper='yes'">DbColumn.IS_WRAPPER,</xsl:if>
      <xsl:if test="$is-generic='no'">
      DbColumn.INFO_PAGE,
      DbColumn.THUMBNAIL,
      </xsl:if>
</xsl:template>

<!--
   =====================================================================================
   Writing the class
   =====================================================================================
-->

<xsl:template name="document">
  <xsl:param name="class-name">
    <xsl:call-template name="config.java-class-name.document"/>
  </xsl:param>
  <xsl:param name="document-type.constant">
    <xsl:call-template name="config.document-type.constant"/>
  </xsl:param>
  <xsl:variable name="format">
    <xsl:call-template name="config.document-type.format"/>
  </xsl:variable>
  <xsl:variable name="is-generic">
    <xsl:call-template name="config.document-type.is-generic"/>
  </xsl:variable>
  <xsl:variable name="is-recursive-component">
    <xsl:call-template name="config.document-type.is-recursive-component"/>
  </xsl:variable>
<!--
-->package net.mumie.cocoon.documents;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.service.LookupNotifyable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXException;

/**
 * &lt;p&gt;
 *   Represents a document of type <!--
  -->{@link DocType#<xsl:value-of select="concat($document-type.constant,' ',$document-type.constant)"/> }
 * &lt;/p&gt;
 * &lt;p&gt;
 *   Java source automatically created by XSLT transformation.
 * &lt;/p&gt;
 * &lt;dl class="genuine"&gt;
 *   &lt;dt&gt;XML Source:&lt;/dt&gt;
 *   &lt;dd class="file"&gt;
 *     <xsl:value-of select="$source.filename"/>&lt;br;&gt;
 *     (<xsl:value-of select="$source.cvs-id.protected"/>)
 *   &lt;/dd&gt;
 *   &lt;dt&gt;XSL Stylesheet:&lt;/dt&gt;
 *   &lt;dd class="file"&gt;
 *     <xsl:value-of select="$this.filename"/>&lt;br;&gt;
 *     (<xsl:value-of select="$this.cvs-id.protected"/>)
 *   &lt;/dd&gt;
 * &lt;/dl&gt;
 */

public class <xsl:value-of select="$class-name"/> extends AbstractDocument
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * Role of this class as an Avalon component.
   * (&lt;code&gt;"net.mumie.cocoon.documents.Document"&lt;/code&gt;)
   */

  public static final String ROLE = "net.mumie.cocoon.documents.Document";

  /**
   * Hint for this class as an Avalon component.
   * (&lt;code&gt;"<xsl:value-of select="@name"/>"&lt;/code&gt;)
   */

  public static final String HINT = "<xsl:value-of select="@name"/>";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(<xsl:value-of select="$class-name"/>.class);

 <xsl:if test="$format='text' and $is-generic='no'">
  /**
   * Database columns needed in use mode {@link UseMode#SERVE SERVE}.
   */

  public static final String[] DB_COLUMNS_SERVE =
    {
      <xsl:call-template name="db-columns-serve"/>
    };

  /**
   * Database columns needed in use mode {@link UseMode#SERVE SERVE} when
   * {@link #withPath withPath} is true.
   */

  public static final String[] DB_COLUMNS_SERVE_WITH_PATH =
    {
      <xsl:call-template name="db-columns-serve"/>
      DbColumn.SECTION_PATH,
    };
  </xsl:if>  

  /**
   * Database columns needed in use mode {@link UseMode#INFO INFO}.
   */

  public static final String[] DB_COLUMNS_INFO =
    {
      <xsl:call-template name="db-columns-info">
        <xsl:with-param name="is-generic" select="$is-generic"/>
        <xsl:with-param name="format" select="$format"/>
      </xsl:call-template>
    };

  /**
   * Database columns needed in use mode {@link UseMode#INFO INFO} when
   * {@link #withPath withPath} is true..
   */

  public static final String[] DB_COLUMNS_INFO_WITH_PATH =
    {
      <xsl:call-template name="db-columns-info">
        <xsl:with-param name="is-generic" select="$is-generic"/>
        <xsl:with-param name="format" select="$format"/>
      </xsl:call-template>
      DbColumn.SECTION_PATH,
    };

  /**
   * Database columns needed in use mode {@link UseMode#COMPONENT COMPONENT}.
   */

  public static final String[] DB_COLUMNS_COMPONENT =
    {
      <xsl:call-template name="db-columns-component">
        <xsl:with-param name="is-generic" select="$is-generic"/>
      </xsl:call-template>
    };

  /**
   * Database columns needed in use mode {@link UseMode#COMPONENT COMPONENT} when
   * {@link #withPath withPath} is true.
   */

  public static final String[] DB_COLUMNS_COMPONENT_WITH_PATH =
    {
      <xsl:call-template name="db-columns-component">
        <xsl:with-param name="is-generic" select="$is-generic"/>
      </xsl:call-template>
      DbColumn.SECTION_PATH,
    };

  /**
   * Database columns needed in use mode {@link UseMode#LINK LINK}.
   */

  public static final String[] DB_COLUMNS_LINK =
    {
      <xsl:call-template name="db-columns-link">
        <xsl:with-param name="is-generic" select="$is-generic"/>
      </xsl:call-template>
    };

  /**
   * Database columns needed in use mode {@link UseMode#LINK LINK} when
   * {@link #withPath withPath} is true.
   */

  public static final String[] DB_COLUMNS_LINK_WITH_PATH =
    {
      <xsl:call-template name="db-columns-link">
        <xsl:with-param name="is-generic" select="$is-generic"/>
      </xsl:call-template>
      DbColumn.SECTION_PATH,
    };

  /**
   * Database columns needed in use mode {@link UseMode#CHECKOUT CHECKOUT}.
   */

  public static final String[] DB_COLUMNS_CHECKOUT =
    {
      <xsl:call-template name="db-columns-info">
        <xsl:with-param name="is-generic" select="$is-generic"/>
        <xsl:with-param name="format" select="$format"/>
      </xsl:call-template>
      DbColumn.SECTION_PATH,
    };

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new &lt;code&gt;<xsl:value-of select="$class-name"/>&lt;/code&gt; instance.
   */

  public <xsl:value-of select="$class-name"/> ()
  {
    this.type = DocType.<xsl:value-of select="$document-type.constant"/>;
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

  <xsl:if test="$format='text' and $is-generic='no'">
  /**
   * &lt;p&gt;
   *  Core part of the toSAX method if the use mode is {@link UseMode#SERVE SERVE}.
   * &lt;/p&gt;
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException, SQLException
  {
    final String METHOD_NAME = "toSAX_SERVE";
    this.getLogger().debug
      (METHOD_NAME + " 1/2: Started. resultSet = " + resultSet);

    // Start root element:
    this.rootElementStartToSAX(contentHandler);

    // Write data:
    <xsl:if test="@has-category='yes'">this.categoryToSAX(resultSet, contentHandler);</xsl:if>
    this.nameToSAX(resultSet, contentHandler);
    <xsl:if test="@has-class='yes'">this.classToSAX(resultSet, contentHandler);</xsl:if>
    this.descriptionToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    this.copyrightToSAX(resultSet, contentHandler);
    this.componentsToSAX(contentHandler);
    this.linksToSAX(contentHandler);
    <xsl:if test="@has-summary='yes'">this.summaryToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@may-be-wrapper='yes'">this.isWrapperToSAX(resultSet, contentHandler);</xsl:if>
    this.contentToSAX(resultSet, contentHandler);

    // Write reference attributes (if necessary):
    // if ( this.refId != Id.UNDEFINED )
    //  this.refAttribsToSAX(resultSet, contentHandler, METHOD_NAME);

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
    throws SAXException, SQLException
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
    <xsl:if test="@may-be-wrapper='yes'">this.isWrapperToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:choose>
    <xsl:when test="$is-generic='no'">
    this.vcThreadToSAX(resultSet, contentHandler);
    this.versionToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    <xsl:if test="@has-qualified-name='yes'">
    this.qualifiedNameToSAX(resultSet, contentHandler);
    </xsl:if>
    this.copyrightToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.deletedToSAX(resultSet, contentHandler);
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
    <xsl:if test="@has-summary='yes'">this.summaryToSAX(resultSet, contentHandler)</xsl:if>;
    this.customMetainfoToSAX(resultSet, contentHandler);
    this.readPermissionsToSAX(contentHandler);
    this.componentsToSAX(contentHandler);
    this.linksToSAX(contentHandler);
    this.attachableToSAX(contentHandler);
    <xsl:if test="$format='text'">this.contentCodeToSAX(resultSet, contentHandler);</xsl:if> 
    this.componentOfToSAX(contentHandler);
    this.linkOfToSAX(contentHandler);
    </xsl:when>
    <xsl:otherwise>
    this.componentOfToSAX(contentHandler);
    this.linkOfToSAX(contentHandler);
    </xsl:otherwise>
    </xsl:choose>

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
    throws SAXException, SQLException
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
    this.deletedToSAX(resultSet, contentHandler);
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
    <xsl:if test="$is-recursive-component='yes'">this.componentsToSAX(contentHandler);</xsl:if>
    <xsl:if test="@has-category='yes'">this.categoryToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="@may-be-wrapper='yes'">this.isWrapperToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:choose>
    <xsl:when test="$is-generic='no'">
    this.vcThreadToSAX(resultSet, contentHandler);
    this.versionToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
    </xsl:choose>

    // Write reference attributes (if necessary):
    if ( this.refId != Id.UNDEFINED )
      this.refAttribsToSAX(resultSet, contentHandler, METHOD_NAME);

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
    throws SAXException, SQLException
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
    <xsl:if test="@may-be-wrapper='yes'">this.isWrapperToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:if test="$is-generic='no'">
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
    throws SAXException, SQLException
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
    <xsl:if test="@may-be-wrapper='yes'">this.isWrapperToSAX(resultSet, contentHandler);</xsl:if>
    <xsl:choose>
    <xsl:when test="$is-generic='no'">
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
    this.customMetainfoToSAX(resultSet, contentHandler);
    this.readPermissionsToSAX(contentHandler);
    this.componentsToSAX(contentHandler);
    this.linksToSAX(contentHandler);
    this.attachableToSAX(contentHandler);
    <xsl:if test="$format='text'">this.contentToSAX(resultSet, contentHandler);</xsl:if>
    </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
    </xsl:choose>

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries for the toSAX methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the database columns this document needs if one of the
   * &lt;code&gt;toSAX&lt;/code&gt; methods would be called.
   */

  public String[] getDbColumns ()
    throws ServiceException
  {
    switch ( this.useMode )
      {
      <xsl:if test="$format='text' and $is-generic='no'">
      case UseMode.SERVE:
        return copy(this.withPath ? DB_COLUMNS_SERVE_WITH_PATH : DB_COLUMNS_SERVE);
      </xsl:if>
      case UseMode.INFO:
        return copy(this.withPath ? DB_COLUMNS_INFO_WITH_PATH : DB_COLUMNS_INFO);
      case UseMode.COMPONENT:
        return copy(this.withPath ? DB_COLUMNS_COMPONENT_WITH_PATH : DB_COLUMNS_COMPONENT);
      case UseMode.LINK:
        return copy(this.withPath ? DB_COLUMNS_LINK_WITH_PATH : DB_COLUMNS_LINK);
      case UseMode.CHECKOUT:
        return copy(DB_COLUMNS_CHECKOUT);
      default: return null;
      }
  }

  <xsl:if test="$format='binary' and $is-generic='no'">
  // --------------------------------------------------------------------------------
  // toStream methods
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary method. Reads all data from <code>inputStream</code> and writes them to
   * &lt;code&gt;outputStream&lt;/code&gt;.
   */

  protected void toStream_SERVE (OutputStream outputStream)
    throws IOException, SQLException
  {
    final String METHOD_NAME = "toStream_SERVE (OutputStream outputStream)";
    this.getLogger().debug(METHOD_NAME + "1/2: Started");

    // Retrieving the content and writing it to the stream
    this.dbHelper.queryAndWriteBlobDatum(this, DbColumn.CONTENT, outputStream);

    this.getLogger().debug(METHOD_NAME + "2/2: Done");
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

<!--
   =====================================================================================
   Main template
   =====================================================================================
-->

<xsl:template match="/">
  <xsl:variable name="this.filename.abs">
    <xsl:value-of select="x-util:concatenatePaths($xsl-dir,$this.filename)"/>
  </xsl:variable>
  <xsl:variable name="config.xsl.filename.abs">
    <xsl:value-of select="x-util:concatenatePaths($xsl-dir,'config.xsl')"/>
  </xsl:variable>
  <xsl:for-each select="/*/document-types/document-type">
    <xsl:variable name="class-name">
      <xsl:call-template name="config.java-class-name.document"/>
    </xsl:variable>
    <xsl:variable name="output.filename.rel">
      <xsl:value-of select="x-util:concatenatePaths($package-path.documents,concat($class-name,'.java'))"/>
    </xsl:variable>
    <xsl:variable name="output.filename.abs">
      <xsl:value-of select="x-util:concatenatePaths($base-dir,$output.filename.rel)"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$this.mode='create'">
        <xsl:if test="$force='yes' or
                       not(x-util:fileExists($output.filename.abs)) or
                       not(x-util:fileIsNewer($output.filename.abs,$this.filename.abs)) or
                       not(x-util:fileIsNewer($output.filename.abs,$config.xsl.filename.abs))">
           <xsl:value-of select="concat('Document.xsl: Creating ',$class-name, '.java')"/>
           <xsl:text>&br;</xsl:text>
           <x-redirect:write select="$output.filename.abs">
             <xsl:call-template name="document">
               <xsl:with-param name="class-name" select="$class-name"/>
             </xsl:call-template>
           </x-redirect:write>
         </xsl:if>
      </xsl:when>
      <xsl:when test="$this.mode='delete'">
        <xsl:if test="x-util:fileExists($output.filename.abs)">
          <xsl:value-of select="concat('Deleting ',$output.filename.abs)"/>
          <xsl:text>&br;</xsl:text>
          <xsl:value-of select="x-util:deleteFile($output.filename.abs)"/>
        </xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
