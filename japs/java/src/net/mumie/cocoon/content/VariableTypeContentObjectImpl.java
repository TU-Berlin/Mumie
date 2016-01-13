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

package net.mumie.cocoon.content;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import org.xml.sax.ContentHandler;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;

/**
 * Default implementation of {@link VariableTypeContentObject VariableTypeContentObject}
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: VariableTypeContentObjectImpl.java,v 1.9 2008/09/08 08:48:25 rassy Exp $</code>
 */

public class VariableTypeContentObjectImpl extends AbstractContentObject
  implements VariableTypeContentObject
{
  /**
   * Sets the nature of this content object (pseuo-document or document).
   * @throws IllegalArgumentException if the argument is not a valid nature code.
   */

  public void setNature (int nature)
    throws IllegalArgumentException
  {
    if ( !Nature.exists(nature) )
      throw new IllegalArgumentException("Unknown nature code: " + nature);
    this.nature = nature;
  }

  /**
   * Sets the type of this content object. The nature must be set before.
   * @throws IllegalStateException id the nature has not been set before.
   * @throws IllegalArgumentException if the argument is not a valid type code.
   */

  public void setType (int type)
    throws IllegalArgumentException, IllegalStateException
  {
    if ( this.nature == Nature.UNDEFINED )
      throw new IllegalStateException("Nature must be set before type");

    if ( this.nature == Nature.DOCUMENT )
      {
        if ( !DocType.exists(type) )
          throw new IllegalArgumentException("Unknown document type code: " + type);
        this.type = type;
        this.rootElementName = DocType.nameFor(type);
      }
    else if ( this.nature == Nature.PSEUDO_DOCUMENT )
      {
        if (  !PseudoDocType.exists(type) )
          throw new IllegalArgumentException("Unknown pseudo-document type code: " + type);
        this.type = type;
        this.rootElementName = PseudoDocType.nameFor(type);
      }
  }

  /**
   * Sets the type according to the specified type name. The nature is set, too. This is
   * possible because type names are unique among documents and pseudo-documents.
   */

  public void setType (String typeName)
    throws IllegalArgumentException
  {
    if ( DocType.exists(typeName) )
      {
        this.nature = Nature.DOCUMENT;
        this.type = DocType.codeFor(typeName);
        this.rootElementName = typeName;
      }
    else if ( PseudoDocType.exists(typeName) )
      {
        this.nature = Nature.PSEUDO_DOCUMENT;
        this.type = PseudoDocType.codeFor(typeName);
        this.rootElementName = typeName;
      }
    else
      throw new IllegalArgumentException
        ("Unknon (pseudo-)document type: " + typeName);
  }

  /**
   * 
   */

  public void reset ()
  {
    super.reset();
    this.nature = Nature.UNDEFINED;
    this.type = DocType.UNDEFINED;
    this.rootElementName = null;
  }

  /**
   * Returns the database columns this document needs if one of the
   * <code>toSAX</code> methods would be called.
   */

  public String[] getDbColumns ()
  {
    switch ( this.useMode )
      {
      case UseMode.LINK:
        return new String []
          {
            DbColumn.DOC_TYPE,
            DbColumn.ID,
            DbColumn.PURE_NAME,
            DbColumn.NAME,
            DbColumn.DESCRIPTION,
            DbColumn.THUMBNAIL,
            DbColumn.INFO_PAGE,
          };
      case UseMode.COMPONENT:
        return new String []
          {
            DbColumn.DOC_TYPE,
            DbColumn.ID,
            DbColumn.SECTION_PATH,
            DbColumn.PURE_NAME,
            DbColumn.NAME,
            DbColumn.CATEGORY,
            DbColumn.THUMBNAIL,
            DbColumn.INFO_PAGE,
            DbColumn.DESCRIPTION,
            DbColumn.CONTAINED_IN,
            DbColumn.CREATED,
	    DbColumn.HIDE,
            DbColumn.DELETED,
            DbColumn.LAST_MODIFIED
          };
      default:
        return null;
      }
  }

  /**
   * Returns the result set needed in the toSAX methods.
   */

  protected ResultSet getResultSet ()
    throws SQLException
  {
    return
      (this.nature == Nature.DOCUMENT
       ? this.dbHelper.queryData(this.type, this.id, this.getDbColumns())
       : this.dbHelper.queryPseudoDocData(this.type, this.id, this.getDbColumns()));
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#LINK LINK}.
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_LINK";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    this.infoPageToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#COMPONENT COMPONENT}.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_COMPONENT";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    if ( this.withPath )
      this.pathToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.categoryToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.thumbnailToSAX(resultSet, contentHandler);
    this.infoPageToSAX(resultSet, contentHandler);
    this.containedInToSAX(resultSet, contentHandler);
    this.createdToSAX(resultSet, contentHandler);
    this.hideToSAX(resultSet, contentHandler);
    this.deletedToSAX(resultSet, contentHandler);
    this.lastModifiedToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(VariableTypeContentObjectImpl.class);

  /**
   * Creates a new <code>VariableTypeContentObjectImpl</code> instance.
   */

  public VariableTypeContentObjectImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }
}