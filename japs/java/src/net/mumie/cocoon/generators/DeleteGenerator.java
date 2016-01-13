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

package net.mumie.cocoon.generators;

import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.xml.MetaXMLElement;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.cocoon.ProcessingException;
import java.util.List;
import net.mumie.cocoon.delete.DeleteItem;
import java.util.ArrayList;
import net.mumie.cocoon.delete.DeleteHelper;
import net.mumie.cocoon.notions.XMLAttribute;
import org.apache.avalon.framework.service.ServiceSelector;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.pseudodocs.PseudoDocument;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.content.VariableTypeContentObject;
import net.mumie.cocoon.util.LogUtil;
import java.sql.ResultSet;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.DocType;

public class DeleteGenerator extends ServiceableJapsGenerator
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DeleteGenerator.class);

  /**
   * Indicates that the generator deletes (not undeletes)
   */

  public static final String DELETE = "delete";

  /**
   * Indicates that the generator undeletes (not deletes)
   */

  public static final String UNDELETE = "undelete";

  /**
   * Indicates that the generator creates a form for confirming (un)deletion
   */

  public static final String CONFIRM = "confirm";

  /**
   * Indicates that the generator executes the (un)deletion.
   */

  public static final String EXECUTE = "execute";

  /**
   * Helper to create XML elements
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement();

  /**
   * Creates a new <code>DeleteGenerator</code> instance.
   */

  public DeleteGenerator ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance. Essentially, this method only calls the superclass recycle
   * method. Besides this, the instance status id notified about the recycle, and log
   * messages are written.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.xmlElement.reset();
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance. Essentially, this method only calls the superclass dispose
   * method. Besides this, the instance status id notified about the dispose, and log
   * messages are written.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.xmlElement.reset();
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Creates the XML for the confirmation dialog for a document to delete.
   */

  protected void confirmDocument (DeleteItem item, String task)
    throws Exception
  {
    final String METHOD_NAME = "confirmDocument";
    this.logDebug(METHOD_NAME + " 1/2: Started. item = " + item.toString());
    ServiceSelector selector = null;
    Document document = null;
    try
      {
        int type = item.getType();
        int id = item.getId();

        // Start XML document:
        this.contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.addAttribute(XMLAttribute.TASK, task);
        this.xmlElement.addAttribute(XMLAttribute.MODE, CONFIRM);
        this.xmlElement.startToSAX(this.contentHandler);

        // Start mumie:item element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.ITEM);
        this.xmlElement.startToSAX(this.contentHandler);

        // Write document info XML:
        selector = (ServiceSelector)this.serviceManager.lookup(Document.ROLE + "Selector");
        document = (Document)selector.select(DocType.hintFor[type]);
        document.setId(id);
        document.setUseMode(UseMode.INFO);
        document.setWithPath(true);
        document.toSAX(this.contentHandler, false);

        // Close mumie:item element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.ITEM);
        this.xmlElement.endToSAX(this.contentHandler);

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.endToSAX(this.contentHandler);

        // End XML:
        this.contentHandler.endDocument();

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    finally
      {
        if ( document != null ) selector.release(document);
        if ( selector != null ) this.serviceManager.release(selector);
      }
  }

  /**
   * Creates the XML for the confirmation dialog for a pseudo-document to delete.
   */

  protected void confirmPseudoDoc (DeleteItem item, String task)
    throws Exception
  {
    final String METHOD_NAME = "confirmPseudoDoc";
    this.logDebug(METHOD_NAME + " 1/2: Started. item = " + item.toString());
    ServiceSelector selector = null;
    PseudoDocument pseudoDoc = null;
    try
      {
        int type = item.getType();
        int id = item.getId();

        // Start XML document:
        this.contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.addAttribute(XMLAttribute.TASK, task);
        this.xmlElement.addAttribute(XMLAttribute.MODE, CONFIRM);
        this.xmlElement.startToSAX(this.contentHandler);

        // Start mumie:item element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.ITEM);
        this.xmlElement.startToSAX(this.contentHandler);

        // Write pseudoDoc info XML:
        selector = (ServiceSelector)this.serviceManager.lookup(PseudoDocument.ROLE + "Selector");
        pseudoDoc = (PseudoDocument)selector.select(PseudoDocType.hintFor[type]);
        pseudoDoc.setId(id);
        pseudoDoc.setUseMode(UseMode.INFO);
        pseudoDoc.setWithPath(true);
        pseudoDoc.toSAX(this.contentHandler, false);

        // Close mumie:item element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.ITEM);
        this.xmlElement.endToSAX(this.contentHandler);

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.endToSAX(this.contentHandler);

        // End XML:
        this.contentHandler.endDocument();

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    finally
      {
        if ( pseudoDoc != null ) selector.release(pseudoDoc);
        if ( selector != null ) this.serviceManager.release(selector);
      }
  }

  /**
   * Creates the XML for the confirmation dialog for a document or pseudo-document to delete.
   */

  protected void confirm (DeleteItem item, String task)
    throws Exception
  {
    switch ( item.getNature() )
      {
      case Nature.DOCUMENT :
        this.confirmDocument(item, task);
        break;
      case Nature.PSEUDO_DOCUMENT :
        this.confirmPseudoDoc(item, task);
        break;
      default :
        throw new IllegalArgumentException("Cannot determine nature of: " + item);
      }
  }

  /**
   * Creates the XML for the confirmation dialog for the specified items to delete.
   */

  protected void confirmMulti (DeleteItem[] items, String task)
    throws Exception
  {
    final String METHOD_NAME = "confirmMulti";
    this.logDebug(METHOD_NAME + " 1/2: Started. items = " + LogUtil.arrayToString(items));
    DbHelper dbHelper = null;
    VariableTypeContentObject contentObject = null;
    try
      {
        // Start XML document:
        this.contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.addAttribute(XMLAttribute.TASK, task);
        this.xmlElement.addAttribute(XMLAttribute.MODE, CONFIRM);
        this.xmlElement.startToSAX(this.contentHandler);

        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        contentObject = (VariableTypeContentObject)this.serviceManager.lookup(VariableTypeContentObject.ROLE);
        ResultSet resultSet = dbHelper.queryContentObjects(items, contentObject.getDbColumns(), true);
        for (DeleteItem item : items)
          {
            // Start mumie:item element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.ITEM);
            this.xmlElement.startToSAX(this.contentHandler);

            // Write content object.
            contentObject.setType(resultSet.getString(DbColumn.DOC_TYPE));
            contentObject.setId(resultSet.getInt(DbColumn.ID));
            contentObject.toSAX(resultSet, contentHandler, false);

            // Close mumie:item element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.ITEM);
            this.xmlElement.endToSAX(this.contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.endToSAX(this.contentHandler);

        // End XML:
        this.contentHandler.endDocument();

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    finally
      {
        if ( contentObject != null ) this.serviceManager.release(contentObject);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }
  }

  /**
   * Creates the XML for the confirmation dialog for the specified items to delete.
   */

  protected void confirm (DeleteItem[] items, String task)
    throws Exception
  {
    if ( items.length == 1 )
      this.confirm(items[0], task);
    else
      this.confirmMulti(items, task);
  }

  /**
   * 
   */

  protected void execute (DeleteItem[] items, String task)
    throws Exception
  {
    final String METHOD_NAME = "execute";
    this.logDebug(METHOD_NAME + " 1/2: Started. items = " + LogUtil.arrayToString(items));

    DbHelper dbHelper = null;
    DeleteHelper deleteHelper = null;
    VariableTypeContentObject contentObject = null;

    // Try to delete all:
    DeleteItem[] processedItems = null;
    try
      {
        deleteHelper = (DeleteHelper)this.serviceManager.lookup(DeleteHelper.ROLE);
        if ( task.equals(DELETE) )
          processedItems = deleteHelper.tryDeleteAll(items, true);
        else if ( task.equals(UNDELETE) )
          processedItems = deleteHelper.tryUndeleteAll(items);
      }
    finally
      {
        if ( deleteHelper != null ) this.serviceManager.release(deleteHelper);
      }

    // Write report:
    try
      {
        // Start XML document:
        this.contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.addAttribute(XMLAttribute.TASK, task);
        this.xmlElement.addAttribute(XMLAttribute.MODE, EXECUTE);
        this.xmlElement.startToSAX(this.contentHandler);

        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        contentObject =
          (VariableTypeContentObject)this.serviceManager.lookup(VariableTypeContentObject.ROLE);
        ResultSet resultSet =
          dbHelper.queryContentObjects(processedItems, contentObject.getDbColumns(), true);
        for (DeleteItem item : items)
          {
            // Start mumie:item element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.ITEM);
            this.xmlElement.addAttribute(XMLAttribute.STATUS, item.getStatusStr());
            String errorStr = item.getErrorStr();
            if ( errorStr != null ) this.xmlElement.addAttribute(XMLAttribute.ERROR, errorStr);
            this.xmlElement.startToSAX(this.contentHandler);

            // Write content object.
            contentObject.setType(resultSet.getString(DbColumn.DOC_TYPE));
            contentObject.setId(resultSet.getInt(DbColumn.ID));
            contentObject.toSAX(resultSet, contentHandler, false);

            // Close mumie:item element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.ITEM);
            this.xmlElement.endToSAX(this.contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.DELETE);
        this.xmlElement.endToSAX(this.contentHandler);

        // End XML:
        this.contentHandler.endDocument();
      }
    finally
      {
        if ( contentObject != null ) this.serviceManager.release(contentObject);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        String[] entities = ParamUtil.getAsStringArray(this.parameters, "entities");
        String mode = ParamUtil.getAsString(this.parameters, "mode");
        String task = ParamUtil.getAsString(this.parameters, "task", DELETE);
        if ( !( task.equals(DELETE) || task.equals(UNDELETE) ) )
          throw new IllegalArgumentException("Unknown task: " + task);

        // Get all (pseudo-)documents to (un)delete:
        List<DeleteItem> itemsList = new ArrayList<DeleteItem>();
        for (String entity : entities)
          itemsList.add(createDeleteItem(entity));
        DeleteItem[] items = itemsList.toArray(new DeleteItem[itemsList.size()]);

        if ( mode.equals(CONFIRM) )
          this.confirm(items, task);
        else if ( mode.equals(EXECUTE) )
          this.execute(items, task);
        else
          throw new IllegalArgumentException("Unknown mode: " + mode);
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * Creates a DeleteItem from the specified string. The string descriibes the
   * the (pseudo-)document in the form <code>"<var>type</var>/<var>id</var>"</code>, e.g.,
   * <code>"element/46"</code> or <code>"section/6"</code>
   */

  protected static DeleteItem createDeleteItem (String descr)
    throws Exception
  {
    int sep = descr.indexOf('/');
    if ( sep == -1 || sep == 0 || sep == descr.length()-1 )
      throw new IllegalArgumentException("Illegal (pseudo-)document descriptor: " + descr);
    String typeName = descr.substring(0, sep);
    String idStr = descr.substring(sep+1);
    int id = Id.UNDEFINED;
    try
      {
        id = Integer.parseInt(idStr);
      }
    catch (NumberFormatException nfe)
      {
        throw new IllegalArgumentException("Cannot convert to int: " + idStr);
      }
    return new DeleteItem(typeName, id);
  }
}