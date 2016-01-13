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

package net.mumie.cocoon.pseudodocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link Language}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultLanguage.java,v 1.8 2007/12/05 12:25:12 grudzin Exp $</code>
 */

public class DefaultLanguage extends ExternallyControlledPseudoDocument
  implements Language
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultLanguage.class);

  /**
   * Returns the database columns this pseudo-document needs if one of the
   * <code>toSAX</code> methods would be called. The colums are:
   * <ul>
   *   <li>
   *     In use mode {@link UseMode#COMPONENT COMPONENT}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#LINK LINK}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#SERVE SERVE}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *     </ul>
   *   </li>
   *   <li>
   *     In use mode {@link UseMode#INFO INFO}:
   *     <ul>
   *       <li>{@link DbColumn#ID ID}</li>
   *       <li>{@link DbColumn#NAME NAME}</li>
   *       <li>{@link DbColumn#DESCRIPTION DESCRIPTION}</li>
   *     </ul>
   *   </li>
   * </ul>
   */

  public String[] getDbColumns ()
  {
    if ( this.withPath )
      switch ( this.useMode )
	{
	case UseMode.COMPONENT :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.CODE
	    };
	case UseMode.LINK :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.SERVE :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION
	    };
	case UseMode.INFO :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.CONTAINED_IN,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.CODE
	    };
	case UseMode.CHECKOUT :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.SECTION_PATH,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.CODE
	    };
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode]);
	}
    else // not! this.withPath
      switch ( this.useMode )
	{
	case UseMode.COMPONENT :
	  return new String[]
	    {
	      DbColumn.ID,
	      DbColumn.NAME,
        DbColumn.PURE_NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.LINK :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	    };
	case UseMode.SERVE :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION
	    };
	case UseMode.INFO :
	  return new String []
	    {
	      DbColumn.ID,
	      DbColumn.CONTAINED_IN,
	      DbColumn.PURE_NAME,
	      DbColumn.NAME,
	      DbColumn.DESCRIPTION,
	      DbColumn.CODE
	    };
	default:
	  throw new IllegalStateException
	    (this + ": Method \"getDbColumns\" not implemented for use mode: " + UseMode.nameFor[this.useMode] + ", this.withPath: " + this.withPath);
	}
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#COMPONENT COMPONENT}.
   */

  protected void toSAX_COMPONENT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_COMPONENT(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#LINK LINK}.
   */

  protected void toSAX_LINK (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_LINK(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#SERVE SERVE}.
   */

  protected void toSAX_SERVE (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_SERVE(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode#INFO INFO}.
   */

  protected void toSAX_INFO (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_INFO(ContentHandler contentHandler)";
    this.logDebug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.containedInToSAX(resultSet, contentHandler);
    this.pureNameToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.codeToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Called from {@link #toSAX} if {@link #useMode} is {@link UseMode.INFO CHECKOUT}.
   */

  protected void toSAX_CHECKOUT (ResultSet resultSet, ContentHandler contentHandler)
    throws Exception
  {
    final String METHOD_NAME = "toSAX_CHECKOUT(ContentHandler contentHandler)";
    this.getLogger().debug(METHOD_NAME + "1/2: Started. resultSet = " + resultSet);
    this.startRootElement(contentHandler);
    this.containedInToSAX(resultSet, contentHandler);
    this.nameToSAX(resultSet, contentHandler);
    this.descriptionToSAX(resultSet, contentHandler);
    this.codeToSAX(resultSet, contentHandler);
    this.closeRootElement(contentHandler);
    this.getLogger().debug(METHOD_NAME + "2/2: Done");
  }

  /**
   * Writes the {@link XMLElement#CODE CODE} element.
   */

  protected void codeToSAX (ResultSet resultSet, ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CODE);
        this.xmlElement.setText(resultSet.getString(DbColumn.CODE));
        this.xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("codeToSAX: " + this.xmlElement.statusToString(), exception);
      }
  }

  /**
   * Returns a string that identificates this instance. It has the
   * following form:<pre>
   *   "DefaultLanguage" +
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
      "DefaultLanguage" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ')';
  }

  /**
   * Creates a <code>DefaultLanguage</code> instance and sets {@link #type type} and
   * {@link #rootElementName rootElementName} to the appropriate values.
   */

  public DefaultLanguage ()
  {
    this.type = PseudoDocType.LANGUAGE;
    this.rootElementName = XMLElement.LANGUAGE;
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Recycles this instance.
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
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.logDebug(METHOD_NAME + "1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + "2/2: Done");
  }
}
