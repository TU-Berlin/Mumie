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
import java.util.Date;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Provides static auxiliary methods to write XML elements from SQL result sets.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ToSAXUtil.java,v 1.10 2008/09/08 08:48:25 rassy Exp $</code>
 */

public class ToSAXUtil
{

  /**
   * Writes the {@link XMLElement#NAME NAME} element.
   */

  public static void nameToSAX (ResultSet resultSet,
                                MetaXMLElement xmlElement,
                                ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.NAME);
        xmlElement.setText(resultSet.getString(DbColumn.NAME));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("nameToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#PATH PATH} element.
   */

  public static void pathToSAX (ResultSet resultSet,
				MetaXMLElement xmlElement,
				ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.PATH);
        xmlElement.setText(resultSet.getString(DbColumn.SECTION_PATH));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("pathToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#PURE_NAME PURE_NAME} element.
   */

  public static void pureNameToSAX (ResultSet resultSet,
                                    MetaXMLElement xmlElement,
                                    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.PURE_NAME);
        xmlElement.setText(resultSet.getString(DbColumn.PURE_NAME));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("pureNameToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#QUALIFIED_NAME QUALIFIED_NAME} element.
   */

  public static void qualifiedNameToSAX (ResultSet resultSet,
                                         MetaXMLElement xmlElement,
                                         ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.QUALIFIED_NAME);
        xmlElement.setText(resultSet.getString(DbColumn.QUALIFIED_NAME));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("qualifiedNameToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#DESCRIPTION DESCRIPTION} element.
   */

  public static void descriptionToSAX (ResultSet resultSet,
                                       MetaXMLElement xmlElement,
                                       ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.DESCRIPTION);
        xmlElement.setText(resultSet.getString(DbColumn.DESCRIPTION));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("descriptionToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#COPYRIGHT COPYRIGHT} element.
   */

  public static void copyrightToSAX (ResultSet resultSet,
                                     MetaXMLElement xmlElement,
                                     ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.COPYRIGHT);
        xmlElement.setText(resultSet.getString(DbColumn.COPYRIGHT));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("copyrightToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#VC_THREAD VC_THREAD} element.
   */

  public static void vcThreadToSAX (ResultSet resultSet,
                                    MetaXMLElement xmlElement,
                                    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.VC_THREAD);
        xmlElement.addAttribute(XMLAttribute.ID, resultSet.getString(DbColumn.VC_THREAD));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("vcThreadToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#VERSION VERSION} element.
   */

  public static void versionToSAX (ResultSet resultSet,
                                   MetaXMLElement xmlElement,
                                   ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.VERSION);
        xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getString(DbColumn.VERSION));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("versionToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes a time value to SAX.
   * @param resultSet result of the database query
   * @param contentHandler recieves the SAX events.
   * @param xmlElement auxiliary to compose the XML element.
   * @param columnName the column to look up in <code>resultSet</code>
   * @param elementName local name of the XML element.
   */

  public static void timeToSAX (ResultSet resultSet,
                                MetaXMLElement xmlElement,
                                ContentHandler contentHandler,
                                String columnName,
                                String elementName)
    throws SAXException
  {
    try
      {
        Date date = resultSet.getTimestamp(columnName);
	if ( date != null )
	  {
	    xmlElement.reset();
	    xmlElement.setLocalName(elementName);
	    xmlElement.addAttribute(XMLAttribute.VALUE, date);
	    xmlElement.addAttribute(XMLAttribute.RAW, date.getTime());
	    xmlElement.toSAX(contentHandler);
	  }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("timeToSAX for column" + columnName + ": " +
           xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CREATED CREATED} element.
   */

  public static void createdToSAX (ResultSet resultSet,
                                   MetaXMLElement xmlElement,
                                   ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX
      (resultSet, xmlElement, contentHandler,
       DbColumn.CREATED, XMLElement.CREATED);
  }

  /**
   * Writes the {@link XMLElement#HIDE HIDE} element.
   */

  public static void hideToSAX (ResultSet resultSet,
			    MetaXMLElement xmlElement,
			    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.HIDE);
        xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getBoolean(DbColumn.HIDE));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("hideToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#DELETED DELETED} element.
   */

  public static void deletedToSAX (ResultSet resultSet,
                                   MetaXMLElement xmlElement,
                                   ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
	String deleted = ( resultSet.getBoolean(DbColumn.DELETED) ? "true" : "false");
	xmlElement.reset();
	xmlElement.setLocalName(XMLElement.DELETED);
	xmlElement.addAttribute(XMLAttribute.VALUE, deleted);
	xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("deletedToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#LAST_MODIFIED LAST_MODIFIED} element.
   */

  public static void lastModifiedToSAX (ResultSet resultSet,
                                        MetaXMLElement xmlElement,
                                        ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX
      (resultSet, xmlElement, contentHandler,
       DbColumn.LAST_MODIFIED, XMLElement.LAST_MODIFIED);
  }

  /**
   * Writes the {@link XMLElement#TIMEFRAME_START TIMEFRAME_START} element.
   */

  public static  void timeframeStartToSAX (ResultSet resultSet,
                                           MetaXMLElement xmlElement,
                                           ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX
      (resultSet, xmlElement, contentHandler,
       DbColumn.TIMEFRAME_START, XMLElement.TIMEFRAME_START);
  } 

  /**
   * Writes the {@link XMLElement#TIMEFRAME_END TIMEFRAME_END} element.
   */

  public static  void timeframeEndToSAX (ResultSet resultSet,
                                           MetaXMLElement xmlElement,
                                           ContentHandler contentHandler)
    throws SAXException
  {
    timeToSAX
      (resultSet, xmlElement, contentHandler,
       DbColumn.TIMEFRAME_END, XMLElement.TIMEFRAME_END);
  }

  /**
   * Writes the {@link XMLElement#CATEGORY CATEGORY} element. If the category column is SQL
   * NULL, the element is suppressed.
   */

  public static void categoryToSAX (ResultSet resultSet,
                                    MetaXMLElement xmlElement,
                                    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        int categoryId = resultSet.getInt(DbColumn.CATEGORY);
        if ( !resultSet.wasNull() )
          {
            xmlElement.reset();
            xmlElement.setLocalName(XMLElement.CATEGORY);
            xmlElement.addAttribute(XMLAttribute.ID, categoryId);
            xmlElement.addAttribute(XMLAttribute.NAME, Category.nameFor[categoryId]);
            xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("categoryToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#THUMBNAIL THUMBNAIL} element. If the thumbnail column is SQL
   * NULL, the element is suppressed.
   */

  public static void thumbnailToSAX (ResultSet resultSet,
                                    MetaXMLElement xmlElement,
                                    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        String value = resultSet.getString(DbColumn.THUMBNAIL);
        if ( value != null )
          {
            xmlElement.reset();
            xmlElement.setLocalName(XMLElement.THUMBNAIL);
            xmlElement.addAttribute(XMLAttribute.ID, value);
            xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("thumbnailToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#INFO_PAGE INFO_PAGE} element. If the info_page column is SQL
   * NULL, the element is suppressed.
   */

  public static void infoPageToSAX (ResultSet resultSet,
                                    MetaXMLElement xmlElement,
                                    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        String value = resultSet.getString(DbColumn.INFO_PAGE);
        if ( value != null )
          {
            xmlElement.reset();
            xmlElement.setLocalName(XMLElement.INFO_PAGE);
            xmlElement.addAttribute(XMLAttribute.ID, value);
            xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("infoPageToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#WIDTH WIDTH} element.
   */

  public static void widthToSAX (ResultSet resultSet,
                                 MetaXMLElement xmlElement,
                                 ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.WIDTH);
        xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getString(DbColumn.WIDTH));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("widthToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#HEIGHT HEIGHT} element.
   */

  public static void heightToSAX (ResultSet resultSet,
                                  MetaXMLElement xmlElement,
                                  ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.HEIGHT);
        xmlElement.addAttribute(XMLAttribute.VALUE, resultSet.getString(DbColumn.HEIGHT));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("heightToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#DURATION DURATION} element.
   */

  public static void durationToSAX (ResultSet resultSet,
                                    MetaXMLElement xmlElement,
                                    ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.DURATION);
        xmlElement.addAttribute
          (XMLAttribute.VALUE, resultSet.getString(DbColumn.DURATION));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("durationToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTAINED_IN CONTAINED_IN} element.
   */

  public static void containedInToSAX (ResultSet resultSet,
                                       MetaXMLElement xmlElement,
                                       ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.CONTAINED_IN);
        xmlElement.addAttribute
          (XMLAttribute.ID, resultSet.getString(DbColumn.CONTAINED_IN));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("containedInToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTENT_TYPE CONTENT_TYPE} element.
   */

  public static void contentTypeToSAX (ResultSet resultSet,
                                       MetaXMLElement xmlElement,
                                       ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.CONTENT_TYPE);
        int contentTypeId = resultSet.getInt(DbColumn.CONTENT_TYPE);
        xmlElement.addAttribute(XMLAttribute.ID, contentTypeId);
        xmlElement.addAttribute(XMLAttribute.VALUE, MediaType.nameFor[contentTypeId]);
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("contentTypeToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CONTENT_LENGTH CONTENT_LENGTH} element.
   */

  public static void contentLengthToSAX (ResultSet resultSet,
                                         MetaXMLElement xmlElement,
                                         ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.CONTENT_LENGTH);
        xmlElement.addAttribute
          (XMLAttribute.VALUE, resultSet.getString(DbColumn.CONTENT_LENGTH));
        xmlElement.toSAX(contentHandler);
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("contentLengthToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Writes the {@link XMLElement#CORRECTOR CORRECTOR} element.
   */

  public static  void correctorToSAX (ResultSet resultSet,
                                      MetaXMLElement xmlElement,
                                      ContentHandler contentHandler)
    throws SAXException
  {
    try
      {
        String value = resultSet.getString(DbColumn.CORRECTOR);
        if ( value != null )
          {
            xmlElement.reset();
            xmlElement.setLocalName(XMLElement.CORRECTOR);
            xmlElement.addAttribute(XMLAttribute.ID, value);
            xmlElement.toSAX(contentHandler);
          }
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("correctorToSAX: " + xmlElement.statusToString(), exception);
      }
  }

  /**
   * Disabled constructor
   */

  private ToSAXUtil ()
  {
    throw new IllegalStateException("ToSAXUtil should not be instanciated");
  }
}

