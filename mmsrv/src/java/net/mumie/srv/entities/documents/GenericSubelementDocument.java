/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
 * Represents a document of type generic_subelement.
 */

public class GenericSubelementDocument extends AbstractDocument
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * Role of this class as an Avalon component.
   * (<code>"net.mumie.srv.entities.Document"</code>)
   */

  public static final String ROLE = "net.mumie.srv.entities.Document";

  /**
   * Hint for this class as an Avalon component.
   * (<code>"generic_subelement"</code>)
   */

  public static final String HINT = "generic_subelement";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(GenericSubelementDocument.class);

   

  /**
   * Database columns needed in use mode {@link UseMode#INFO INFO}.
   */

  protected static final String[] DB_COLUMNS_INFO =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      DbColumn.CATEGORY,
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.CONTAINED_IN,
       
    };

  /**
   * Database columns needed in use mode {@link UseMode#COMPONENT COMPONENT}.
   */

  protected static final String[] DB_COLUMNS_COMPONENT =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      DbColumn.CATEGORY,
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.HIDE,
      
      DbColumn.CONTAINED_IN,
      
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
      
    };

  /**
   * Database columns needed in use mode {@link UseMode#CHECKOUT CHECKOUT}.
   */

  protected static final String[] DB_COLUMNS_CHECKOUT =
    {
      DbColumn.ID,
      DbColumn.PURE_NAME,
      DbColumn.CATEGORY,
      DbColumn.NAME,
      DbColumn.DESCRIPTION,
      DbColumn.CONTAINED_IN,
       
    };

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>GenericSubelementDocument</code> instance.
   */

  public GenericSubelementDocument ()
  {
    this.type = EntityType.GENERIC_SUBELEMENT;
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

  

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#INFO INFO}.
   * </p>
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
    this.categoryToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.containedInToSAX(resultSet, contentHandler);
     

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#COMPONENT COMPONENT}.
   * </p>
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
    
    this.containedInToSAX(resultSet, contentHandler);
    this.categoryToSAX(resultSet, contentHandler);

    // Write reference attributes (if necessary):
    if ( this.refId != Id.UNDEFINED )
      this.refAttribsToSAX(resultSet, contentHandler);

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#LINK LINK}.
   * </p>
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
    

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * <p>
   *  Core part of the toSAX method if the use mode is {@link UseMode#CHECKOUT CHECKOUT}.
   * </p>
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
    this.categoryToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
      

    // Close root element:
    this.rootElementEndToSAX(contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // getDbColumns method
  // --------------------------------------------------------------------------------

  /**
   * Returns the database columns this document needs if one of the
   * <code>toSAX</code> methods would be called.
   */

  public String[] getDbColumns ()
  {
    switch ( this.useMode )
      {
      
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

  

  // --------------------------------------------------------------------------------
  // Identification method 
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identificates this <code>GenericSubelementDocument</code> Instance. It has the
   * following form:<pre>
   *   "GenericSubelementDocument" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ',' + {@link #id id}
   *   ',' + {@link #useMode useMode}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "GenericSubelementDocument" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ')';
  }
}
