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

   $Id: DocumentToCheckin.xsl,v 1.29 2009/11/15 23:23:57 rassy Exp $
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

<xsl:param name="this.filename">DocumentToCheckin.xsl</xsl:param>

<!--
   Directory of this (and the imported) XSL scripts; as an absolute filename.
-->

<xsl:param name="xsl-dir"/>

<!--
   CVS id of this XSL script
-->

<xsl:param name="this.cvs-id">$Id: DocumentToCheckin.xsl,v 1.29 2009/11/15 23:23:57 rassy Exp $</xsl:param>

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

<xsl:param name="package.documents">net.mumie.cocoon.checkin.documents</xsl:param>

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
   Writing the class
   =====================================================================================
-->

<xsl:template name="document">
  <xsl:param name="document-type.name" select="@name"/>
  <xsl:param name="class-name">
    <xsl:call-template name="config.java-class-name.document-to-checkin"/>
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
  <xsl:variable name="has-generic-counterpart">
    <xsl:call-template name="config.document-type.has-generic-counterpart"/>
  </xsl:variable>
  <xsl:variable name="is-corrector">
    <xsl:call-template name="config.document-type.is-corrector"/>
  </xsl:variable><!--
-->package net.mumie.cocoon.checkin.documents;

import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.checkin.CheckinException;
import net.mumie.cocoon.checkin.Content;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.Source;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.DocPath;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.PasswordEncryptor;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * &lt;p&gt;
 *   Represents a document of type <!--
  -->{@link DocType#<xsl:value-of select="concat($document-type.constant,' ',$document-type.constant)"/> }
     that is about to be checked in.
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

public class <xsl:value-of select="$class-name"/> extends AbstractDocumentToCheckin
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * Role of this class as an Avalon service.
   * (&lt;code&gt;"net.mumie.cocoon.checkin.DocumentToCheckin"&lt;/code&gt;)
   */

  public static final String ROLE = "net.mumie.cocoon.checkin.DocumentToCheckin";

  /**
   * Hint for this class as an Avalon component.
   * (&lt;code&gt;"<xsl:value-of select="@name"/>"&lt;/code&gt;)
   */

  public static final String HINT = "<xsl:value-of select="@name"/>";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(<xsl:value-of select="$class-name"/>.class);

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new &lt;code&gt;<xsl:value-of select="$class-name"/>&lt;/code&gt; instance.
   */

  public <xsl:value-of select="$class-name"/> ()
  {
    this.nature = Nature.DOCUMENT;
    this.type = DocType.<xsl:value-of select="$document-type.constant"/>;
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.resetVariables();
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this document with the specified master, content, source, and path.
   <xsl:if test="$is-generic='yes'">
   * Since this is a generic document, content and source must be null. Otherwise,
   * an exception is thrown.
   </xsl:if>
   */

  public void setup (Master master, Content content, Source source, String path)
    throws CheckinException
  {
    final String METHOD_NAME = "setup";
    this.logDebug(METHOD_NAME + " 1/2: Started. path = " + path);
    this.path = path;
    this.master = master;
    <xsl:choose>
    <xsl:when test="$is-generic='no'">
    this.content = content;
    this.source = source;
    </xsl:when>
    <xsl:otherwise>
    if ( content != null )
      throw new CheckinException
        (this.path + ": Generic documents must not have content");
    if ( source != null )
      throw new CheckinException
        (this.path + ": Generic documents must not have a source");
    this.content = null;
    this.source = null;
    </xsl:otherwise>
    </xsl:choose>
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Recycles this instance. Resets the global variables and increases the recycle counters.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Resets the global variables.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  <xsl:if test="$document-type.name='generic_problem'">
  // --------------------------------------------------------------------------------
  // Auxiliary methods
  // --------------------------------------------------------------------------------

  /**
   * Adds this generic problem to the default worksheet if necessary.
   */
  
  protected void addToDefaultWorksheet (DbHelper dbHelper)
    throws Exception
  {
    final String METHOD_NAME = "addToDefaultWorksheet";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    int defaultWorksheetId = dbHelper.getIdForPath
      (DocType.WORKSHEET, DocPath.DEFAULT_WORKSHEET);
    if ( !dbHelper.checkIfReferenced
            (DocType.WORKSHEET, defaultWorksheetId, this.type, this.id, RefType.COMPONENT) )
      dbHelper.storeReference
        (DocType.WORKSHEET, defaultWorksheetId,
         this.type, this.id,
         null, RefType.COMPONENT, null);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }
  </xsl:if>

  // --------------------------------------------------------------------------------
  // Checkin
  // --------------------------------------------------------------------------------

  /**
   * Checkin stage 1. Sets vc thread, version, etc. if necessary, checks-in all
   * non-referential data, sets the id.
   */

  protected void checkin_1 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_1";
        this.logDebug(METHOD_NAME + " 1/2: Started");

        <xsl:choose>
        <xsl:when test="$is-generic='no'">
        this.setupVersion(dbHelper);
        this.checkPermissions(user);
        </xsl:when>
        <xsl:otherwise>
        this.checkCreatePermission(user);  
        </xsl:otherwise>
        </xsl:choose>

        Map data = new HashMap();
        this.pureNameToData(data);
        this.nameToData(data);
        this.descriptionToData(data);
        <xsl:if test="@has-timeframe='yes'">
        this.timeframeStartToData(data);
        this.timeframeEndToData(data);
        </xsl:if>
        <xsl:if test="@has-width-and-height='yes'">
        this.widthToData(data);
        this.heightToData(data);
        </xsl:if>
        <xsl:if test="@has-duration='yes'">this.durationToData(data);</xsl:if>
        <xsl:if test="@has-category='yes'">this.categoryToData(data);</xsl:if>
        <xsl:choose>
        <xsl:when test="$is-generic='no'">
        this.vcThreadToData(data);
        this.versionToData(data);
        this.copyrightToData(data);
        this.contentTypeToData(data);
        this.contentLengthToData(data);
        <xsl:if test="$format='text'">
        this.contentAsStringToData(data);
        </xsl:if>
        if ( this.sourceToData(data) )
	  {
	    this.sourceTypeToData(data);
	    this.sourceLengthToData(data);
	  }
        </xsl:when>
        <xsl:otherwise>
        </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="@has-qualified-name='yes'">this.qualifiedNameToData(data);</xsl:if>
        <xsl:if test="@may-be-wrapper='yes'">this.isWrapperToData(data);</xsl:if>

        <xsl:choose>
        <xsl:when test="$is-generic='no'">
        this.id = dbHelper.storeData(this.type, data);
        </xsl:when>
        <xsl:otherwise>
        this.storeOrUpdateData(dbHelper, data);          
        </xsl:otherwise>
        </xsl:choose>

        <xsl:if test="$is-generic='no' and $format='binary'">
        dbHelper.updateBinaryDatum
          (this.type, this.id, DbColumn.CONTENT, this.content.getInputStream());
        </xsl:if>
        
        this.logDebug(METHOD_NAME + " 2/2: Done. this.id = " + this.id);
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  /**
   * Checkin stage 3. Checks-in all referential data.
   */

  protected void checkin_3 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_3";
        this.logDebug(METHOD_NAME + " 1/2: Started");

        <xsl:choose>
        <xsl:when test="$is-generic='no'">
        <xsl:if test="@has-class='yes'">this.checkinELClass(dbHelper);</xsl:if>
        <xsl:if test="@has-corrector='yes'">this.checkinCorrector(dbHelper);</xsl:if>

        this.checkinReferences(dbHelper);
        this.checkinAuthorships(dbHelper, defaults);
        <xsl:if test="$has-generic-counterpart='yes'">
        if ( this.isNewVersion )
          this.deleteGDIMEntries(dbHelper);
        this.checkinThemeMapEntries(dbHelper);
        </xsl:if>
        this.checkinReadPermissions(dbHelper, defaults);
        if ( !this.isNewVersion )
          this.checkinWritePermissions(dbHelper, defaults);
        this.checkinThumbnail(dbHelper);
        this.checkinInfoPage(dbHelper);
        <xsl:if test="@has-summary='yes'">this.checkinSummary(dbHelper);</xsl:if>
        </xsl:when>
        <xsl:otherwise>
        </xsl:otherwise>
        </xsl:choose>

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }

  <xsl:if test="$is-generic='no'">
  /**
   * Checkin stage 4.
   */

  protected void checkin_4 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_4";
        this.logDebug(METHOD_NAME + " 1/2: Started");

        if ( this.isNewVersion )
          {
            this.updateRefTargets(dbHelper);
            <xsl:if test="$is-corrector='yes'">this.updateCorrector(dbHelper);</xsl:if>
            <xsl:call-template name="updateUserAnnTables"/>
          }

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }
  </xsl:if>

  <xsl:if test="$document-type.name='generic_problem'">
  /**
   * Checkin stage 4.
   */

  protected void checkin_4 (DbHelper dbHelper,
                            User user,
                            PasswordEncryptor encryptor,
                            Master defaults)
    throws CheckinException
  {
    try
      {
        final String METHOD_NAME = "checkin_4";
        this.logDebug(METHOD_NAME + " 1/2: Started");
        this.addToDefaultWorksheet(dbHelper);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new CheckinException(this.getIdentification(), exception);
      }
  }
  </xsl:if>

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Writes the XML representation of this document as SAX events to the specified
   * content handler. If &lt;code&gt;ownDocument&lt;/code&gt; is true, the
   * &lt;code&gt;startDocument&lt;/code&gt; and &lt;code&gt;endDocument&lt;/code&gt;
   * methods of the content handler are called before resp. after. Otherwise, these
   * calls are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    if ( ownDocument )
      contentHandler.startDocument();

    // Start tag:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(DocType.nameFor[this.type]);
    if ( this.id != Id.UNDEFINED )
      this.xmlElement.addAttribute(XMLAttribute.ID, this.id);
    if ( this.path != null )
      this.xmlElement.addAttribute(XMLAttribute.PATH, this.path);
    <xsl:if test="$is-generic='no'">
    if ( this.oldPath != null )
      this.xmlElement.addAttribute(XMLAttribute.OLD_PATH, this.oldPath);
    </xsl:if>
    this.xmlElement.startToSAX(contentHandler);

    <xsl:if test="$is-generic='no'">
    // The vc_thread element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.VC_THREAD);
    this.xmlElement.addAttribute(XMLAttribute.ID, this.vcThreadId);
    this.xmlElement.toSAX(contentHandler);

    // The version element:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(XMLElement.VERSION);
    this.xmlElement.addAttribute(XMLAttribute.VALUE, this.version);
    this.xmlElement.toSAX(contentHandler);
    </xsl:if>

    // End tag:
    this.xmlElement.reset();
    this.xmlElement.setLocalName(DocType.nameFor[this.type]);
    this.xmlElement.endToSAX(contentHandler);

    if ( ownDocument )
      contentHandler.endDocument();
  }

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
   *   ',' + {@link #path path}
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
      ',' + this.path +
      ')';
  }
}
</xsl:template>

<!--
   =====================================================================================
   Auxiliaries
   =====================================================================================
-->

<xsl:template name="updateUserAnnTables">
  <xsl:param name="doctype-name" select="@name"/>
  <xsl:for-each select="/*/multidoc-annotations/multidoc-annotation[@originator='user']">
    <xsl:variable name="doctype-1-name" select="document-types/document-type[@order='1']/@name"/>
    <xsl:variable name="doctype-2-name" select="document-types/document-type[@order='2']/@name"/>
    <xsl:if test="$doctype-1-name=$doctype-name">
      <xsl:text>dbHelper.updateUserAnnotation(this.type, DocType.</xsl:text>
      <!-- Java constant for the numerical code of document type 2:  -->
      <xsl:call-template name="config.document-type.constant">
        <xsl:with-param name="name" select="$doctype-2-name"/>
      </xsl:call-template>
      <xsl:text>, this.id, this.oldId);</xsl:text>
    </xsl:if>
  </xsl:for-each>
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
      <xsl:call-template name="config.java-class-name.document-to-checkin"/>
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
           <xsl:value-of select="concat('DocumentToCheckin.xsl: Creating ',$class-name,'.java')"/>
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
