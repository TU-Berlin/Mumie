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

package net.mumie.cocoon.util;

import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.Writer;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import org.apache.excalibur.xml.dom.DOMParser;
import net.mumie.japs.datasheet.DataSheet;
import net.mumie.japs.datasheet.DataSheetException;
import org.apache.excalibur.xml.sax.XMLizable;

/**
 * A {@link DataSheet DataSheet} as an Avalon service.
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CocoonEnabledDataSheet.java,v 1.13 2008/05/15 12:11:52 rassy Exp $</code>
 */

public interface CocoonEnabledDataSheet extends XMLizable, Identifyable
{
  // --------------------------------------------------------------------------------
  // Static constants
  // --------------------------------------------------------------------------------

  /**
   * Role as an Avalon service (<code>CocoonEnabledDataSheet.class.getName()</code>).
   */

  public static final String ROLE = CocoonEnabledDataSheet.class.getName();

  // --------------------------------------------------------------------------------
  // Document, root element
  // --------------------------------------------------------------------------------

  /**
   * Returns the root element of this data sheet.
   */

  public Element getRootElement ();

  /**
   * <p>
   *   Sets the root element of this data sheet.
   * </p>
   * <p>
   *   By means of this method, the object can be reused for another data sheet source.
   * </p>
   */

  public void setRootElement (Element rootElement);

  /**
   * Returns the document node associated with this data sheet.
   */

  public Document getDocument ();

  /**
   * <p>
   *   Sets the document node of this data sheet.
   * </p>
   * <p>
   *   By means of this method the <code>DataSheet</code> object can be reused for another
   *   source. 
   * </p>
   * @throws NullPointerException if <code>document</code> is <code>null</code>.
   */

  public void setDocument (Document document);
  /**
   * <p>
   *   Sets the document node to a newly created, empty data sheet document node. The node
   *   contains only the root element (no unit or data elements). It is created by
   *   <code>parser</code>.
   * </p>
   * <p>
   *   By means of this method the data sheet object can be reused for another
   *   source. 
   * </p>
   */

  public void setEmptyDocument (DOMParser parser)
    throws SAXException;

  // --------------------------------------------------------------------------------
  // Extraction
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   "Extracts" data from another document.
   * </p>
   * <p>
   *   <code>sourceDocument</code> represents the other document. The value of
   *   <code>mode</code> controls what happens if the data sheet already contains entries
   *   with the same path:
   * </p>
   * <ul>
   *   <li>{@link DataSheet#REPLACE DataSheet.REPLACE}:&nbsp; the old data is removed
   *   before,</li>  
   *   <li>{@link DataSheet#PROTECT DataSheet.PROTECT}:&nbsp; nothing is done (so the old
   *   data is preserved),</li> 
   *   <li>{@link DataSheet#APPEND DataSheet.APPEND}:&nbsp; the new data is appended to the
   *   old one.</li> 
   * </ul>
   */

  public void extract (Document sourceDocument, int mode);

  /**
   * <p>
   *   Merges this data sheet with another data sheet.
   * </p>
   * <p>
   *   All data of the other data sheet is imported into this one. The value of
   *   <code>mode</code> controls what happens if both data sheets contain entries with the
   *   same path:
   * </p>
   * <ul>
   *   <li>{@link DataSheet#REPLACE DataSheet.REPLACE}:&nbsp; the data of this data sheet is
   *   removed before,</li> 
   *   <li>{@link DataSheet#PROTECT DataSheet.PROTECT}:&nbsp; nothing is done (so the data
   *   of this data sheet is preserved),</li> 
   *   <li>{@link DataSheet#APPEND DataSheet.APPEND}:&nbsp; the data of
   *   <code>dataSheet</code>is appended to the data of this data sheet.</li>
   * </ul>
   */

  public void merge (CocoonEnabledDataSheet dataSheet, int mode);

  // --------------------------------------------------------------------------------
  // Retrieving data
  // --------------------------------------------------------------------------------

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>NodeList</code>. 
   */

  public NodeList getAsNodeList (String path);

  /**
   * Returns the data stored in the data element with path <code>path</code> as an
   * <code>Element</code>. 
   */

  public Element getAsElement (String path)
    throws DataSheetException;

  /**
   * <p>
   *   Returns the data stored in the data element with path <code>path</code> as a
   *   <code>String</code>. If the data element does not exist, returns <code>null</code>.
   * </p>
   * <p>
   *   If <code>trim</code> is <code>true</code> the returned string will be trimmed if not
   *   <code>null</code>.
   * </p>
   */

  public String getAsString (String path, boolean trim)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>String</code>. If the data element does not exist, returns <code>null</code>.
   */

  public String getAsString (String path)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>String</code>. If the data element does not exist, returns
   * <code>defaultValue</code>. 
   */

  public String getAsString (String path, String defaultValue)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Boolean</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to a boolean. If the data element does not exist,
   * returns <code>null</code>.
   */

  public Boolean getAsBoolean (String path)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Boolean</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to a boolean. If the data element does not exist,
   * returns <code>defaultValue</code>.
   */

  public boolean getAsBoolean (String path, boolean defaultValue)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as an
   * <code>Integer</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to an <code>Integer</code>. If the data element does
   * not exist, returns <code>null</code>. 
   */

  public Integer getAsInteger (String path)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as
   * an integer. Concatenates the values of the text childs of the data
   * element and converts the result to an integer. If the data element does not exist,
   * returns <code>defaultValue</code>.
   */

  public int getAsInteger (String path, int defaultValue)
    throws DataSheetException;


  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Float</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to an <code>Float</code>. If the data element does
   * not exist, returns <code>null</code>. 
   */

  public Float getAsFloat (String path)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as
   * a float. Concatenates the values of the text childs of the data
   * element and converts the result to an float. If the data element does not exist,
   * returns <code>defaultValue</code>.
   */

  public float getAsFloat (String path, float defaultValue)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Double</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to an <code>Double</code>. If the data element does
   * not exist, returns <code>null</code>. 
   */

  public Double getAsDouble (String path)
    throws DataSheetException;

  /**
   * Returns the data stored in the data element with path <code>path</code> as
   * a double. Concatenates the values of the text childs of the data
   * element and converts the result to an double. If the data element does not exist,
   * returns <code>defaultValue</code>.
   */

  public double getAsDouble (String path, double defaultValue)
    throws DataSheetException;

  // --------------------------------------------------------------------------------
  // Storing data
  // --------------------------------------------------------------------------------

  /**
   * Stores <code>node</code> in the data element with path <code>path</code>. The data
   * element is created if it does not exist. The behavior in the case of already existing
   * children of the data element is controlled by <code>mode</code>: if
   * {@link DataSheet#REPLACE DataSheet.REPLACE}, the old children are removed before, if
   * {@link DataSheet#PROTECT DataSheet.PROTECT}, nothing is done (so the old data are
   * preserved), if {@link DataSheet#APPEND DataSheet.APPEND}, the new data are appended to
   * the old data.
   */

  public void put (String path, Node node, int mode);

  /**
   * <p>
   *   Stores <code>node</code> in the data element with path <code>path</code>. The data
   *   element is created if it does not exist. Already existing children of the data
   *   element are removed before.
   * </p>
   * <p>
   *   Same as {@link #put(String,Node,int) put(path, node, REPLACE)}.
   * </p>
   */

  public void put (String path, Node node);

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The data element is created if it does not exist. The behavior in
   * the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link DataSheet#REPLACE DataSheet.REPLACE}, the old children are
   * removed before, if {@link DataSheet#PROTECT DataSheet.PROTECT}, nothing is done (so the
   * old data are preserved).
   */

  public void put (String path, String value, int mode);

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The data element is created if it does not exist. Already existing
   *   children of the data element are removed before.
   * </p>
   * <p>
   *   Same as {@link #put(String,String,int) put(path, value, REPLACE)}.
   * </p>
   */

  public void put (String path, String value);

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is <code>"true"</code> or
   * <code>"false"</code> depending on wether <code>value</code> is <code>true</code> or
   * <code>false</code>, respectively. The data element is created if it does not exist. The
   * behavior in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link DataSheet#REPLACE DataSheet.REPLACE}, the old children are
   * removed before, if {@link DataSheet#PROTECT DataSheet.PROTECT}, nothing is done (so the
   * old data are preserved).
   */

  public void put (String path, boolean value, int mode);

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The value of the text node is <code>"true"</code> or
   *   <code>"false"</code> depending on wether <code>value</code> is <code>true</code> or
   *   <code>false</code>, respectively. The data element is created if it does not
   *   exist. Already existing children of the data element are removed before.
   * </p>
   * <p>
   *   Same as {@link #put(String,boolean,int) put(path, value, REPLACE)}.
   * </p>
   */

  public void put (String path, boolean value);

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is the <code>String</code> representation
   * of <code>value</code>. The data element is created if it does not exist. The behavior
   * in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link DataSheet#REPLACE DataSheet.REPLACE}, the old children are
   * removed before, if {@link DataSheet#PROTECT DataSheet.PROTECT}, nothing is done (so the
   * old data are preserved).
   */

  public void put (String path, int value, int mode);

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The value of the text node is the <code>String</code>
   *   representation of <code>value</code>.The data element is created if it does not
   *   exist. Already existing children of the data element are removed before.
   * </p>
   * <p>
   *   Same as {@link #put(String,boolean,int) put(path, value, REPLACE)}.
   * </p>
   */

  public void put (String path, int value);

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is the <code>String</code> representation
   * of <code>value</code>. The data element is created if it does not exist. The behavior
   * in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link DataSheet#REPLACE DataSheet.REPLACE}, the old children are
   * removed before, if {@link DataSheet#PROTECT DataSheet.PROTECT}, nothing is done (so the
   * old data are preserved). 
   */

  public void put (String path, float value, int mode);

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The value of the text node is the <code>String</code>
   *   representation of <code>value</code>.The data element is created if it does not
   *   exist. Already existing children of the data element are removed before.
   * </p>
   */

  public void put (String path, float value);

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is the <code>String</code> representation
   * of <code>value</code>. The data element is created if it does not exist. The behavior
   * in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link DataSheet#REPLACE DataSheet.REPLACE}, the old children are
   * removed before, if {@link DataSheet#PROTECT DataSheet.PROTECT}, nothing is done (so the
   * old data are preserved).
   */

  public void put (String path, double value, int mode);

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The value of the text node is the <code>String</code>
   *   representation of <code>value</code>.The data element is created if it does not
   *   exist. Already existing children of the data element are removed before.
   * </p>
   */

  public void put (String path, double value);

  // --------------------------------------------------------------------------------
  // Removing data
  // --------------------------------------------------------------------------------

  /**
   * Removes the unit with the specified path. If the path points to a data element,
   * throws an exception. If the path points to nothing (neither data nor unit), does
   * nothing.
   */

  public void removeUnit (String path)
    throws DataSheetException;

  /**
   * Removes the data with the specified path. If the path points to a unit, throws an
   * exception. If the path points to nothing (neither data nor unit), does nothing.
   */

  public void remove (String path)
    throws DataSheetException;

  // --------------------------------------------------------------------------------
  // Data sheet elements
  // --------------------------------------------------------------------------------

  /**
   * Returns the unit element with path <code>path</code>. If <code>create</code> is
   * <code>true</code>, the element will be created if it does not exist. Otherwise, the
   * method returns <code>null</code> if the element does not exist.
   */

  public Element getUnitElement (String path, boolean create);

  /**
   * Returns the unit element with path <code>path</code>. If it does not exist, returns
   * <code>null</code>.
   */

  public Element getUnitElement (String path);

  /**
   * Returns the data element with path <code>path</code>. If <code>create</code> is
   * <code>true</code>, the element will be created if it does not exist. Otherwise, the
   * method returns <code>null</code> if the element does not exist.
   */

  public Element getDataElement (String path, boolean create);

  /**
   * Returns the data element with path <code>path</code>. If it does not exist, returns
   * <code>null</code>.
   */

  public Element getDataElement (String path);

  /**
   * <p>
   *   Returns the path of <code>element</code>.
   * </p>
   * <p>
   *   NOTE: This method does not check if <code>element</code> is indeed a unit or data
   *   element of this data sheet. If necessary, you shoud check this yourself by calling
   *   {@link #isUnitOrDataElement isUnitOrDataElement}.
   * </p>
   */

  public String getPathOf (Element element);

  /**
   * Returns the paths of all data elements.
   */

  public String[] getAllDataPaths ();

  /**
   * Returns the paths of all unit elements.
   */

  public String[] getAllUnitPaths ();

  /**
   * <p>
   *   Returns the paths of all data elements matching <code>pathTemplate</code>.
   * </p>
   * <p>
   *   <code>pathTemplate</code> is a path that may contain wildcards. Currently, the
   *   following wildcards are supported:
   * </p>
   * <table class="genuine">
   *   <thead>
   *     <tr>
   *       <td>Wildcard</td>
   *       <td>Meaning</td>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td><code>*</code></td>
   *       <td>Matches any substring (including the empty string) of characters other than
   *       <code>'/'</code>.</td>
   *     </tr>
   *     <tr>
   *       <td><code>?</code></td>
   *       <td>Matches any character other than <code>'/'</code>.</td>
   *     </tr>
   *   </tbody>
   * </table>
   */

  public String[] getMatchingDataPaths (String pathTemplate);

  /**
   * <p>
   *   Returns the paths of all data elements matching <code>pathTemplate</code>.
   * </p>
   * <p>
   *   <code>pathTemplate</code> is a path that may contain wildcards. See
   *   {@link #getMatchingDataPaths getMatchingDataPaths} for the supported wildcards.
   * </p>
   */

  public String[] getMatchingUnitPaths (String pathTemplate);

  // --------------------------------------------------------------------------------
  // Output
  // --------------------------------------------------------------------------------

  /**
   * Outputs the XML code of this data sheet to <code>result</code>.
   */

  public void toXMLCode (Result result)
    throws DataSheetException;

  /**
   * Outputs the XML code of this data sheet to <code>out</code>.
   */

  public void toXMLCode (OutputStream out)
    throws DataSheetException;

  /**
   * Outputs the XML code of this data sheet to <code>writer</code>.
   */

  public void toXMLCode (Writer writer)
    throws DataSheetException;

  /**
   * Returns the XML code of this data sheet as a string.
   */

  public String toXMLCode ()
    throws DataSheetException;

  // --------------------------------------------------------------------------------
  // Indentation
  // --------------------------------------------------------------------------------

  /**
   * Indents the data sheet XML code.
   */

  public void indent ();

  // --------------------------------------------------------------------------------
  // Checking and Validating
  // --------------------------------------------------------------------------------

  /**
   * Returns <code>true</code> if <code>element</code> is a data sheet element (i.e., in the
   * namespace {@link DataSheet#NAMESPACE}) of this data sheet, otherwise <code>false</code>.
   */

  public boolean isDataSheetElement (Element element);

  /**
   * Returns <code>true</code> if <code>element</code> is a unit or data element of this
   * data sheet, otherwise <code>false</code>.
   */

  public boolean isUnitOrDataElement (Element element);

  /**
   * Validates this data sheet. I.e., checks if the data sheet meets the specification.
   */

  public void validate ()
    throws DataSheetException;
}
