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

package net.mumie.cocoon.transformers;



import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DbTable;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.util.GenericDocumentResolver;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.xml.sax.SAXException;

public abstract class AbstractResolveGenericRefsTransformer
  extends AbstractAddDynamicDataTransformer
  implements CacheableProcessingComponent
{
  // --------------------------------------------------------------------------------
  // Inner class: RefTable
  // --------------------------------------------------------------------------------

  /**
   * A table mapping generic document ids to real document ids and vice versa.
   */

  protected static final class RefTable
  {
    /**
     * The gemeric document ids.
     */

    protected int[] genericDocIds;

    /**
     * The real document ids.
     */

    protected int[] realDocIds;

    /**
     * <p>
     *   Returns the generic document ids.
     * </p>
     * <p>
     *   CAUTION: The array is not copied; so changes in the returned array affect this
     *   object.
     * </p>
     */

    public final int[] getGenericDocIds ()
    {
      return this.genericDocIds;
    }

    /**
     * <p>
     *   Returns the real document ids.
     * </p>
     * <p>
     *   CAUTION: The array is not copied; so changes in the returned array affect this
     *   object.
     * </p>
     */

    public final int[] getRealDocIds ()
    {
      return this.realDocIds;
    }

    /**
     * Returns the generic document id for the specified real document id.
     */

    public int getGeneric (int realDocId)
    {
      return this.genericDocIds[index(realDocId, this.realDocIds)];
    }

    /**
     * Returns the real document id for the specified generic document id.
     */

    public int getReal (int genericDocId)
    {
      return this.realDocIds[index(genericDocId, this.genericDocIds)];
    }

    /**
     * Creates a new instance with the specified generic and real documentids. The two
     * arrays must contain the ids an order so that, for all indeces <em>i</em>,
     * <code>realDocIds[<em>i</em>]</code> is the id of the real document which implements
     * the generic document with the id <code>genericDocIds[<em>i</em>]</code>.
     */

    public RefTable (int[] genericDocIds, int[] realDocIds)
    {
      this.genericDocIds = genericDocIds;
      this.realDocIds = realDocIds;
    }

    /**
     * Auxiliary method; returns the index of the specified item in the specified array.
     */

    protected static final int index (int item, int[] array)
    {
      for (int i = 0; i < array.length; i++)
        {
          if ( array[i] == item )
            return i;
        }
      return -1;
    }
  }

  // --------------------------------------------------------------------------------
  // Global variabls and constants
  // --------------------------------------------------------------------------------

  /**
   * Contains all generic document types.
   */

  protected static final int[] genericDocTypes = getGenericDocTypes();

  /**
   * The db helper of this instance.
   */

  protected DbHelper dbHelper = null;

  /**
   * Map holding a {@link RefTable RefTable} for each generic document type.
   */

  protected Map<Integer,RefTable> refTables;

  /**
   * Name of the {@link XMLElement#STORE STORE} element that wrappes the data.
   */

  protected String storeName = null;

  /**
   * The use mode for the document output.
   */

  protected int useMode = UseMode.UNDEFINED;

  /**
   * The caching key
   */

  protected String cachingKey = null;

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Resets the global variables to their initial states.
   */

  protected void resetVariables ()
  {
    super.resetVariables();
    this.refTables = null;
    this.storeName = null;
    this.useMode = UseMode.UNDEFINED;
    this.cachingKey = null;
  }

  /**
   * Releases the global services. 
   */

  protected void releaseServices ()
  {
    super.releaseServices();
    if ( this.dbHelper != null )
      {
	this.manager.release(this.dbHelper);
	this.dbHelper = null;
      }
  }

  // --------------------------------------------------------------------------------
  // Output as SAX events
  // --------------------------------------------------------------------------------

  /**
   * Sends the content of the dynamic data element to SAX.
   */

  protected  void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started ");

    ServiceSelector documentSelector = null;
    try
      {
        // Init services:
        documentSelector = (ServiceSelector)this.manager.lookup(Document.ROLE + "Selector");

        // Start STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.addAttribute(XMLAttribute.NAME, this.storeName);
        this.metaXMLElement.startToSAX(this.contentHandler);

	for (Map.Entry<Integer,RefTable> entry : this.refTables.entrySet())
          {
	    int genericDocType = entry.getKey().intValue();
	    int realDocType = DocType.realOf(genericDocType);
            RefTable refTable = entry.getValue();
            
            Document realDoc = null;
            try
              {
                realDoc = (Document)documentSelector.select(DocType.hintFor[realDocType]);
                realDoc.setUseMode(this.useMode);
                String[] columns = realDoc.getDbColumns();
                ResultSet realDocsData =
                  dbHelper.queryData(realDocType, refTable.getRealDocIds(), columns);
                while ( realDocsData.next() )
                  {
                    int realDocId = realDocsData.getInt(DbColumn.ID);
                    int genericDocId = refTable.getGeneric(realDocId);

                    // Start GENERIC_DOCUMENT_IMPL element:
                    this.metaXMLElement.reset();
                    this.metaXMLElement.setLocalName(XMLElement.GENERIC_DOCUMENT_IMPL);
                    this.metaXMLElement.addAttribute(XMLAttribute.ID_OF_GENERIC, genericDocId);
                    this.metaXMLElement.startToSAX(this.contentHandler);

                    // Create element for real document:
                    realDoc.setId(realDocId);
                    realDoc.toSAX(realDocsData, this.contentHandler, false);

                    // Close GENERIC_DOCUMENT_IMPL element:
                    this.metaXMLElement.reset();
                    this.metaXMLElement.setLocalName(XMLElement.GENERIC_DOCUMENT_IMPL);
                    this.metaXMLElement.endToSAX(this.contentHandler);
                  }
              }
            finally
              {
                if ( realDoc != null )
                  documentSelector.release(realDoc);
              }
          }

        // Close STORE element:
        this.metaXMLElement.reset();
        this.metaXMLElement.setLocalName(XMLElement.STORE);
        this.metaXMLElement.endToSAX(this.contentHandler);

        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( documentSelector != null )
          this.manager.release(documentSelector);
      }
  }

  // --------------------------------------------------------------------------------
  // Methods related to caching
  // --------------------------------------------------------------------------------

  /**
   * Returns the caching key
   */
  
  public Serializable getKey()
  {
    final String METHOD_NAME = "getKey";
    this.getLogger().debug(METHOD_NAME + " 1/1: this.cachingKey = " + this.cachingKey);
    return this.cachingKey;
  }


  /**
   * Creates the validity object.
   * Returns a {@link TimeStampValidity TimeStampValidity}
   * with the latest last modification time amoung all resolved refererences. 
   */

  public SourceValidity getValidity()
  {
    final String METHOD_NAME = "getValidity";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    try
      {
	long latestLastModified = -1;
	for (Map.Entry<Integer,RefTable> entry : this.refTables.entrySet())
	  {
	    int genericDocType = entry.getKey().intValue();
	    int realDocType = DocType.realOf(genericDocType);
            int[] realDocIds = entry.getValue().getRealDocIds();
	    long lastModified =
	      this.getLatestLastModified(realDocType, realDocIds);
	    if ( lastModified > latestLastModified )
	      latestLastModified = lastModified;
	  }
        this.getLogger().debug(METHOD_NAME + " 2/2: latestLastModified = " + latestLastModified);
        return new TimeStampValidity(latestLastModified);
      }
    catch (Exception exception)
      {
	this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
    return null;
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Converts the specified list of <code>Integer</code> objects to an array of
   * <code>int</code> values.
   */

  protected static int[] toIntArray (List<Integer> list)
  {
    int[] array = new int[list.size()];
    int i = 0;
    for (Integer item : list)
      array[i++] = item.intValue();
    return array;
  }

  /**
   * Returns all generic document types.
   */

  protected static final int[] getGenericDocTypes ()
  {
    List<Integer> genericTypes = new ArrayList<Integer>();
    for (int type = DocType.first;  type <= DocType.last; type++)
      {
        if ( DocType.isGeneric(type) )
          genericTypes.add(new Integer(type));
      }
    return toIntArray(genericTypes);
  }

  /**
   * 
   */

  protected long getLatestLastModified (int realDocType, int[] realDocIds)
    throws Exception
  {
    ResultSet resultSet = this.dbHelper.queryData
      (realDocType, realDocIds, new String[] { DbColumn.LAST_MODIFIED });
    long latestLastModified = -1;
    while ( resultSet.next() )
      {
	long lastModified = resultSet.getTimestamp(DbColumn.LAST_MODIFIED).getTime();
	if ( lastModified > latestLastModified )
	  latestLastModified = lastModified;
      }
    return latestLastModified;
  }

}