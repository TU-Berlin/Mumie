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

package net.mumie.japs.datasheet;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 *   Represents a data sheet XML document. See the corresponding 
 *   <a href="doc-files/data_sheet_spec.txt">specification</a> for more
 *   information on data sheets. Provides methods to access and create unit and data
 *   elements and to validate a data sheet.
 * </p>
 * <p>
 *   <em>IMPORTANT NOTE:</em> <code>DataSheet</code> objects must be created by a namespace
 *   aware docment builder. Otherwise, they will not work properly.
 * </p>
 * <h2>Examples</h2>
 * <p>
 *   Assume the following data sheet is contained in a file <code>"data_sheet.xml"</code>:
 * </p>
 * <pre>
 *   &lt;?xml version="1.0"?&gt;
 *   &lt;data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"&gt;
 *     &lt;unit name="meta"&gt;
 *       &lt;data name="mode"&gt;homework&lt;/data&gt;
 *       &lt;data name="points"&gt;12&lt;/data&gt;
 *     &lt;/unit&gt;
 *     &lt;unit name="matrix_pair"&gt;
 *       &lt;data name="left_matrix"&gt;
 *         &lt;mtable xmlns="http://www.w3.org/1998/Math/MathML"&gt;
 *           &lt;!-- MathML-Code  --&gt;
 *         &lt;/mtable&gt;
 *       &lt;/data&gt;
 *       &lt;data name="right_matrix"&gt;
 *         &lt;mtable xmlns="http://www.w3.org/1998/Math/MathML"&gt;
 *           &lt;!-- MathML-Code --&gt;
 *         &lt;/mtable&gt;
 *       &lt;/data&gt;
 *     &lt;/unit&gt;
 *     &lt;data name="vectors"&gt;
 *       &lt;mrow xmlns="http://www.w3.org/1998/Math/MathML"&gt;
 *         &lt;!-- MathML-Code --&gt;
 *       &lt;/mrow&gt;
 *       &lt;mrow xmlns="http://www.w3.org/1998/Math/MathML"&gt;
 *         &lt;!-- MathML-Code --&gt;
 *       &lt;/mrow&gt;
 *     &lt;/data&gt;
 *   &lt;/data_sheet&gt;</pre>
 * <p>
 *   To obtain a <code>DataSheet</code> object from it, do the following:
 * </p>
 * <pre>
 *   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 *   factory.setNamespaceAware(true); // <em>&lt;-- IMPORTANT!</em>
 *   DocumentBuilder builder = factory.newDocumentBuilder();
 *   Document document = builder.parse(new File("data_sheet.xml"));
 *   DataSheet dataSheet = new DataSheet(document);</pre>
 * <p>
 *   To retrieve data, use the <code>getAsXXX</code> methods. You specify the data by their
 *   paths, e.g.,
 * </p>
 * <pre>
 *   Element rightMatrix = dataSheet.getAsElement("matrix_pair/right_matrix");
 *   String mode = dataSheet.getAsString("meta/mode");
 *   int points = dataSheet.getAsInteger("meta/points", 10);</pre>
 * <p>
 *   To directly access unit and data elements, use the <code>getUnitElement</code> and
 *   <code>getDataElement</code> methods, e.g.,
 * </p>
 * <pre>
 *   Element dataElement = dataSheet.getDataElement("matrix_pair/right_matrix");</pre>
 * <p>
 *   For another example, assume you want to create a new data sheet from existing data. A
 *   new, empty <code>DataSheet</code> object is created by
 * </p>
 * <pre>
 *   DataSheet dataSheet = new DataSheet(builder);</pre>
 * <p>
 *   where <code>builder</code> is a namespace aware document builder. To fill the data
 *   sheet with data, use the <code>put</code> methods. E.g., consider an XML fragment
 * </p>
 * <pre>
 *   &lt;mrow xmlns="http://www.w3.org/1998/Math/MathML"&gt;
 *     &lt;!-- MathML-Code --&gt;
 *   &lt;/mrow&gt;</pre>
 * <p>
 *   which is represented by an <code>Element</code> object <code>element</code>. If
 *   <code>element</code> has been created with a different document then the one associated
 *   with <code>dataSheet</code>, you first have to import it:
 * </p>
 * <pre>
 *   element = (Element)dataSheet.getDocument().importNode(element, true);</pre>
 * <p>
 *   You insert it into the data sheet by
 * </p>
 * <pre>
     dataSheet.put("foo/bar", element);</pre>
 * <p>
 *   This should result in a data sheet:
 * </p>
 * <pre>
 *   &lt;?xml version="1.0"?&gt;
 *   &lt;data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"&gt;
 *     &lt;unit name="foo"&gt;
 *       &lt;data name="bar"&gt;
 *         &lt;mrow xmlns="http://www.w3.org/1998/Math/MathML"&gt;
 *           &lt;!-- MathML-Code --&gt;
 *         &lt;/mrow&gt;
 *       &lt;/data&gt;
 *     &lt;/unit&gt;
 *   &lt;/data_sheet&gt;</pre>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DataSheet.java,v 1.19 2008/05/15 11:28:51 rassy Exp $</code>
 */

public class DataSheet
{
  // --------------------------------------------------------------------------------
  // Static constants
  // --------------------------------------------------------------------------------

  /**
   * The data sheet namespace
   * (<code>"http://www.mumie.net/xml-namespace/data-sheet"</code>). 
   */

  public final static String NAMESPACE = "http://www.mumie.net/xml-namespace/data-sheet";

  /**
   * <p>
   *   The data sheet extraction namespace
   *   (<code>"http://www.mumie.net/xml-namespace/data-sheet/extract"</code>).
   * </p>
   * <p>
   *   This namespace is used to distinguish the elements that "mark" data in other
   *   namespaces. This data can then be "extracted" and made into a data sheet.
   * </p>
   */

  public final static String EXTRACT_NAMESPACE =
    "http://www.mumie.net/xml-namespace/data-sheet/extract";

  /**
   * Local name of the data sheet root element (<code>"data_sheet"</code>).
   */

  public final static String ROOT_ELEMENT = "data_sheet";

  /**
   * Local name of the unit elements (<code>"unit"</code>).
   */

  public final static String UNIT_ELEMENT = "unit";

  /**
   * Local name of the data elements (<code>"data"</code>).
   */

  public final static String DATA_ELEMENT = "data";

  /**
   * Local name of the datalabel element (<code>datalabel</code>). &mdash; This element
   * occurs in the {@link #EXTRACT_NAMESPACE data sheet extraction namespace} only
   */

  public final static String DATALABEL_ELEMENT = "data";

  /**
   * Name of the name attribute (<code>"name"</code>).
   */

  public final static String NAME_ATTRIBUTE = "name";

  /**
   * Name of the path attribute (<code>"path"</code>). &mdash; This attribut
   * occurs in the {@link #EXTRACT_NAMESPACE data sheet extraction namespace} only
   */

  public final static String PATH_ATTRIBUTE = "path";

  /**
   * Flag in <code>put</code> method calls indicating that old data should be renmoved.
   */

  public final static int REPLACE = 0;

  /**
   * Flag in <code>put</code> method calls indicating that old data should be preserved in
   * favour of new data.
   */

  public final static int PROTECT = 1;

  /**
   * Flag in <code>put</code> method calls indicating that old data should be appended to
   * already existing data.
   */

  public final static int APPEND = 2;

  // --------------------------------------------------------------------------------
  // Global variables
  // --------------------------------------------------------------------------------

  /**
   * The root element of this data sheet.
   */

  protected Element rootElement = null;

  /**
   * Regular expression for name attribute values.
   */

  protected Pattern namePattern = null;

  /**
   * The transformer to get printed representations of the data sheet.
   */

  protected Transformer transformer = null;

  // --------------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new data sheet object with root element <code>rootElement</code>. This
   * element must be namespace aware, i.e., it must have been created by a document that
   * was created by a namespace aware document builder.
   */

  public DataSheet (Element rootElement)
  {
    this.setRootElement(rootElement);
  }

  /**
   * Creates a new data sheet object with the root element of <code>document</code> as root
   * element. <code>document</code> must be namespace aware, i.e., it must have been created
   * by a namespace aware document builder.
   */

  public DataSheet (Document document)
  {
    this.setDocument(document);
  }

  /**
   * Creates a new data sheet object containing no data by means of
   * <code>documentBuilder</code>. The latter must be namespace aware.
   */

  public DataSheet (DocumentBuilder documentBuilder)
  {
    this.setEmptyDocument(documentBuilder);
  }

  /**
   * <p>
   *   Creates a new, void data sheet object.
   * </p>
   * <p>
   *   NOTE: You <em>must</em> call {@link #setDocument setDocument},
   *   {@link #setEmptyDocument setEmptyDocument}, or {@link #setRootElement setRootElement}
   *   before you can work with a <code>DataSheet</code> created by this constructor.
   * </p>
   * <p>
   *   Use this constructor if you want to reuse the <code>DataSheet</code> object for
   *   several data sheet sources. &mdash; Code pattern:
   * </p>
   * <pre>
   *   DataSheet dataSheet = new DataSheet();
   *
   *   dataSheet.setDocument(document1);
   *   // Process document1
   *
   *   dataSheet.setDocument(document2);
   *   // Process document2
   *
   *   // ... </pre>
   */

  public DataSheet ()
  {
    // Nothing
  }

  // --------------------------------------------------------------------------------
  // Document, root element
  // --------------------------------------------------------------------------------

  /**
   * Returns the root element of this data sheet.
   */

  public Element getRootElement ()
  {
    return this.rootElement;
  }

  /**
   * <p>
   *   Sets the root element of this data sheet.
   * </p>
   * <p>
   *   By means of this method, the object can be reused for another data sheet source.
   * </p>
   * @throws IllegalArgumentException if <code>rootElement</code> is not a data sheet root
   * element.
   * @throws NullPointerException if <code>rootElement</code> is <code>null</code>.
   */

  public void setRootElement (Element rootElement)
  {
    if ( rootElement == null )
      throw new NullPointerException("Root element is null");
    if ( !rootElement.getLocalName().equals(ROOT_ELEMENT) )
      throw new IllegalArgumentException("Not a data sheet root element: " + rootElement);
    this.rootElement = rootElement;
  }

  /**
   * Returns the document node associated with this data sheet.
   */

  public Document getDocument ()
  {
    return this.rootElement.getOwnerDocument();
  }

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

  public void setDocument (Document document)
  {
    if ( document == null )
      throw new NullPointerException("Document is null");
    this.setRootElement(document.getDocumentElement());
  }

  /**
   * <p>
   *   Sets the document node to a newly created, empty data sheet document node. The node
   *   contains only the root element (no unit or data elements). It is created by
   *   <code>documentBuilder</code>.
   * </p>
   * <p>
   *   IMPORTANT NODE: <code>documentBuilder</code> <em>must</em> be namespace aware.
   * </p>
   * <p>
   *   By means of this method the <code>DataSheet</code> object can be reused for another
   *   source. 
   * </p>
   * @throws NullPointerException if <code>documentBuilder</code> is <code>null</code>.
   * @throws IllegalArgumentException if <code>documentBuilder</code> is not namespace
   * aware.
   */

  public void setEmptyDocument (DocumentBuilder documentBuilder)
  {
    if ( documentBuilder == null )
      throw new NullPointerException("Document builder null");
    if ( !documentBuilder.isNamespaceAware() )
      throw new IllegalArgumentException("Document builder is not namespace aware");
    Document document =
      documentBuilder.getDOMImplementation().createDocument(NAMESPACE, ROOT_ELEMENT, null);
    Element rootElement = document.getDocumentElement();
    rootElement.setAttribute("xmlns", NAMESPACE);
    this.setRootElement(rootElement);
  }

  // --------------------------------------------------------------------------------
  // Merging and extraction
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Auxiliary method: Imports all nodes in <code>nodeList</code> and returns them as a
   *   single node.
   * </p>
   * <p>
   *   If <code>setXmlnsAttribute</code> is <code>true</code>, an <code>xmlns</code>
   *   attribute is set for each imported node provided its namespace URI is not
   *   <code>null</code>.
   * </p>
   * <p>
   *   Currently, the returned node is always a {@link DocumentFragment DocumentFragment}.
   *   In future versions, this may be a simple {@link Node Node} if <code>nodeList</code> has
   *   length 1.
   * </p>
   */

  protected Node importNodes (NodeList nodeList, boolean setXmlnsAttribute)
  {
    Document document = this.getDocument();
    DocumentFragment documentFragment = document.createDocumentFragment();
    for (int i = 0; i < nodeList.getLength(); i++)
      {
        Node child = document.importNode(nodeList.item(i), true);
        if ( setXmlnsAttribute && child.getNodeType() == Node.ELEMENT_NODE )
          {
            Element element = (Element)child;
            String namespace = element.getNamespaceURI();
            if ( namespace != null ) element.setAttribute("xmlns", namespace);
          }
        documentFragment.appendChild(child);
      }
    return documentFragment;
  }

  /**
   * <p>
   *   Auxiliary method: Imports all nodes in <code>nodeList</code> and returns them as a
   *   single node.
   * </p>
   * <p>
   *   Same as {@link #importNodes(NodeList,boolean) importNodes(nodeList, false)}.
   * </p>
   */

  protected Node importNodes (NodeList nodeList)
  {
    return this.importNodes(nodeList, false);
  }

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
   *   <li>{@link #REPLACE}:&nbsp; the old data is removed before,</li>
   *   <li>{@link #PROTECT}:&nbsp; nothing is done (so the old data is preserved),</li>
   *   <li>{@link #APPEND}:&nbsp; the new data is appended to the old one.</li>
   * </ul>
   */

  public void extract (Document sourceDocument, int mode)
  {
    NodeList sourceDataElementNodes =
      sourceDocument.getElementsByTagNameNS(EXTRACT_NAMESPACE, DATA_ELEMENT);
    for (int i = 0; i < sourceDataElementNodes.getLength(); i++)
      {
        Element sourceDataElement = (Element)sourceDataElementNodes.item(i);
        String path = sourceDataElement.getAttribute(PATH_ATTRIBUTE);
        this.put(path, this.importNodes(sourceDataElement.getChildNodes(), true), mode);
      }
  }

  /**
   * <p>
   *   Merges this data sheet with another data sheet.
   * </p>
   * <p>
   *   All data of <code>dataSheet</code> is imported into this data sheet. The value of
   *   <code>mode</code> controls what happens if both data sheets contain entries with the
   *   same path:
   * </p>
   * <ul>
   *   <li>{@link #REPLACE}:&nbsp; the data of this data sheet is removed before,</li>
   *   <li>{@link #PROTECT}:&nbsp; nothing is done (so the data of this data sheet is
   *   preserved),</li> 
   *   <li>{@link #APPEND}:&nbsp; the data of <code>dataSheet</code>is appended to the data
   *   of this data sheet.</li> 
   * </ul>
   */

  public void merge (DataSheet dataSheet, int mode)
  {
    String[] paths = dataSheet.getAllDataPaths();
    for (int i = 0; i < paths.length; i++)
      this.put(paths[i], this.importNodes(dataSheet.getAsNodeList(paths[i])), mode);
  }

  // --------------------------------------------------------------------------------
  // Retrieving data
  // --------------------------------------------------------------------------------

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>NodeList</code>. 
   */

  public NodeList getAsNodeList (String path)
  {
    Element dataElement = this.getDataElement(path);
    return (dataElement != null ? dataElement.getChildNodes() : null);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as an
   * <code>Element</code>. 
   */

  public Element getAsElement (String path)
    throws DataSheetException
  {
    Element element = null;
    Element dataElement = this.getDataElement(path);
    if ( dataElement != null )
      {
        NodeList children = dataElement.getChildNodes();
        int length = children.getLength();
        for (int i = 0;  i < length; i++)
          {
            Node child = children.item(i);
            switch ( child.getNodeType() )
              {
              case Node.ELEMENT_NODE:
                if ( element != null )
                  throw new DataSheetException
                    ("Multiple child elements in data element: " + dataElement);
                element = (Element)child;
                break;
              case Node.TEXT_NODE:
                if ( child.getNodeValue().trim().length() > 0 )
                  throw new DataSheetException
                    ("Non-whitespace text node in data element: " + dataElement);
                break;
              }
          }
      }
    return element;
  }

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
    throws DataSheetException
  {
    Element dataElement = this.getDataElement(path);
    if ( dataElement != null )
      {
        StringBuffer buffer = new StringBuffer();
        NodeList children = dataElement.getChildNodes();
        int length = children.getLength();
        for (int i = 0;  i < length; i++)
          {
            Node child = children.item(i);
            switch ( child.getNodeType() )
              {
              case Node.TEXT_NODE:
                buffer.append(child.getNodeValue());
                break;
              case Node.ELEMENT_NODE:
                throw new DataSheetException
                  ("Child element in data element: " + dataElement);
              }
          }
        String string = buffer.toString();
        return (trim ? string.trim() : string);
      }
    else
      return null;
  }

  /**
   * <p>
   *   Returns the data stored in the data element with path <code>path</code> as a
   *   <code>String</code>. If the data element does not exist, returns <code>null</code>.
   * </p>
   * <p>
   *   Same as {@link #getAsString(String,noolean) getAsString(path, false)}.
   * </p>
   */

  public String getAsString (String path)
    throws DataSheetException
  {
    return this.getAsString(path, false);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>String</code>. If the data element does not exist, returns
   * <code>defaultValue</code>. 
   */

  public String getAsString (String path, String defaultValue)
    throws DataSheetException
  {
    String value = this.getAsString(path);
    return (value != null ? value : defaultValue);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Boolean</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to a boolean by means of
   * {@link #stringToBoolean stringToBoolean}. If the data element does not exist, returns
   * <code>null</code>. 
   */

  public Boolean getAsBoolean (String path)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? new Boolean(this.stringToBoolean(valueAsString)) : null);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Boolean</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to a boolean by means of
   * {@link #stringToBoolean stringToBoolean}. If the data element does not exist, returns
   * <code>defaultValue</code>.
   */

  public boolean getAsBoolean (String path, boolean defaultValue)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? this.stringToBoolean(valueAsString) : defaultValue);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as an
   * <code>Integer</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to an <code>Integer</code>. If the data element does
   * not exist, returns <code>null</code>. 
   */

  public Integer getAsInteger (String path)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? new Integer(valueAsString) : null);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as
   * an integer. Concatenates the values of the text childs of the data
   * element and converts the result to an integer. If the data element does not exist,
   * returns <code>defaultValue</code>.
   */

  public int getAsInteger (String path, int defaultValue)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? Integer.parseInt(valueAsString) : defaultValue);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Float</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to an <code>Float</code>. If the data element does
   * not exist, returns <code>null</code>. 
   */

  public Float getAsFloat (String path)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? new Float(valueAsString) : null);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as
   * a float. Concatenates the values of the text childs of the data
   * element and converts the result to an float. If the data element does not exist,
   * returns <code>defaultValue</code>.
   */

  public float getAsFloat (String path, float defaultValue)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? Float.parseFloat(valueAsString) : defaultValue);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as a
   * <code>Double</code> object. Concatenates the values of the text childs of the data
   * element and converts the result to an <code>Double</code>. If the data element does
   * not exist, returns <code>null</code>. 
   */

  public Double getAsDouble (String path)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? new Double(valueAsString) : null);
  }

  /**
   * Returns the data stored in the data element with path <code>path</code> as
   * a double. Concatenates the values of the text childs of the data
   * element and converts the result to an double. If the data element does not exist,
   * returns <code>defaultValue</code>.
   */

  public double getAsDouble (String path, double defaultValue)
    throws DataSheetException
  {
    String valueAsString = this.getAsString(path, true);
    return (valueAsString != null ? Double.parseDouble(valueAsString) : defaultValue);
  }

  // --------------------------------------------------------------------------------
  // Storing data
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Starting point for all <code>put</code> methods.
   * </p>
   * <p>
   *   Stores <code>node</code> in the data element with path <code>path</code>. The
   *   element is created if it does not exist yet. The behavior if the element already
   *   contains children controled by <code>mode</code>, which can take three values with
   *   the following meaning:
   * </p>
   * <ul>
   *   <li>{@link #REPLACE}:&nbsp; the old children are removed before,</li>
   *   <li>{@link #PROTECT}:&nbsp; nothing is done (so the old children are preserved),</li>
   *   <li>{@link #APPEND}:&nbsp; the new children are appended to the old ones.</li>
   * </ul>
   * <p>
   *   The last possibility can be switched off be setting <code>allowAppend</code> to
   *   <code>true</code>. In that case, setting <code>mode</code> to {@link #APPEND} will
   *   cause an {@link IllegalArgumentException IllegalArgumentException}.
   * </p>
   */

  protected void put (String path, Node node, int mode, boolean allowAppend)
  {
    Element dataElement = this.getDataElement(path, true);
    Node[] children = this.nodeListToArray(dataElement.getChildNodes());
    switch ( mode )
      {
      case REPLACE:
        for (int i = 0; i < children.length; i++)
          dataElement.removeChild(children[i]);
        dataElement.appendChild(node);
        break;
      case PROTECT:
        if ( children.length == 0 )
          dataElement.appendChild(node);
        break;
      case APPEND:
        if ( !allowAppend )
          throw new IllegalArgumentException("Mode APPEND not allowed");
        dataElement.appendChild(node);
        break;
      default:
        throw new IllegalArgumentException("Unknown mode: " + mode);
      }
  }

  /**
   * Stores <code>node</code> in the data element with path <code>path</code>. The data
   * element is created if it does not exist. The behavior in the case of already existing
   * children of the data element is controlled by <code>mode</code>: if {@link #REPLACE},
   * the old children are removed before, if {@link #PROTECT}, nothing is done (so the old
   * data are preserved), if {@link #APPEND}, the new data are appended to the old data. 
   */

  public void put (String path, Node node, int mode)
  {
    this.put(path, node, mode, true);
  }

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

  public void put (String path, Node node)
  {
    this.put(path, node, REPLACE);
  }

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The data element is created if it does not exist. The behavior in
   * the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link #REPLACE}, the old children are removed before,
   * if {@link #PROTECT}, nothing is done (so the old data are preserved).
   */

  public void put (String path, String value, int mode)
  {
    this.put(path, this.getDocument().createTextNode(value), mode, false);
  }

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

  public void put (String path, String value)
  {
    this.put(path, value, REPLACE);
  }

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is <code>"true"</code> or
   * <code>"false"</code> depending on wether <code>value</code> is <code>true</code> or
   * <code>false</code>, respectively. The data element is created if it does not exist. The
   * behavior in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link #REPLACE}, the old children are removed before, if
   * {@link #PROTECT}, nothing is done (so the old data are preserved).
   */

  public void put (String path, boolean value, int mode)
  {
    this.put(path, (value ? "true" : "false"), mode);
  }

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

  public void put (String path, boolean value)
  {
    this.put(path, value, REPLACE);
  }

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is the <code>String</code> representation
   * of <code>value</code>. The data element is created if it does not exist. The behavior
   * in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link #REPLACE}, the old children are removed before, if
   * {@link #PROTECT}, nothing is done (so the old data are preserved).
   */

  public void put (String path, int value, int mode)
  {
    this.put(path, Integer.toString(value), mode);
  }

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The value of the text node is the <code>String</code>
   *   representation of <code>value</code>.The data element is created if it does not
   *   exist. Already existing children of the data element are removed before.
   * </p>
   * <p>
   *   Same as {@link #put(String,int,int) put(path, value, REPLACE)}.
   * </p>
   */

  public void put (String path, int value)
  {
    this.put(path, value, REPLACE);
  }

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is the <code>String</code> representation
   * of <code>value</code>. The data element is created if it does not exist. The behavior
   * in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link #REPLACE}, the old children are removed before, if
   * {@link #PROTECT}, nothing is done (so the old data are preserved).
   */

  public void put (String path, float value, int mode)
  {
    this.put(path, Float.toString(value), mode);
  }

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The value of the text node is the <code>String</code>
   *   representation of <code>value</code>.The data element is created if it does not
   *   exist. Already existing children of the data element are removed before.
   * </p>
   * <p>
   *   Same as {@link #put(String,float,int) put(path, value, REPLACE)}.
   * </p>
   */

  public void put (String path, float value)
  {
    this.put(path, value, REPLACE);
  }

  /**
   * Stores <code>value</code> as a text node in the data element with path
   * <code>path</code>. The value of the text node is the <code>String</code> representation
   * of <code>value</code>. The data element is created if it does not exist. The behavior
   * in the case of already existing children of the data element is controlled by
   * <code>mode</code>: if {@link #REPLACE}, the old children are removed before, if
   * {@link #PROTECT}, nothing is done (so the old data are preserved).
   */

  public void put (String path, double value, int mode)
  {
    this.put(path, Double.toString(value), mode);
  }

  /**
   * <p>
   *   Stores <code>value</code> as a text node in the data element with path
   *   <code>path</code>. The value of the text node is the <code>String</code>
   *   representation of <code>value</code>.The data element is created if it does not
   *   exist. Already existing children of the data element are removed before.
   * </p>
   * <p>
   *   Same as {@link #put(String,double,int) put(path, value, REPLACE)}.
   * </p>
   */

  public void put (String path, double value)
  {
    this.put(path, value, REPLACE);
  }

  // --------------------------------------------------------------------------------
  // Removing data
  // --------------------------------------------------------------------------------

  /**
   * Removes the unit with the specified path. If the path points to a data element,
   * throws an exception. If the path points to nothing (neither data nor unit), does
   * nothing.
   */

  public void removeUnit (String path)
    throws DataSheetException
  {
    Element removedElement = this.removeDataSheetElement(UNIT_ELEMENT, path);
    if ( removedElement == null && this.getDataElement(path) != null )
      throw new DataSheetException("Cannot remove; is not a unit: " + path);
  }

  /**
   * Removes the data with the specified path. If the path points to a unit, throws an
   * exception. If the path points to nothing (neither data nor unit), does nothing.
   */

  public void remove (String path)
    throws DataSheetException
  {
    Element removedElement = this.removeDataSheetElement(DATA_ELEMENT, path);
    if ( removedElement == null && this.getUnitElement(path) != null )
      throw new DataSheetException("Cannot remove; is a unit: " + path);
  }

  // --------------------------------------------------------------------------------
  // Data sheet elements
  // --------------------------------------------------------------------------------

  /**
   * Returns the <code>elementName</code>-element with path <code>path</code>. If
   * <code>create</code> is <code>true</code>, the element will be created if it does not
   * exist. Otherwise, the method returns <code>null</code> if the element does not exist.
   */

  protected Element getDataSheetElement (String elementName, String path, boolean create)
  {
    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    Element parent = this.rootElement;
    int numOfTokens = tokenizer.countTokens();
    for (int i = 1; i < numOfTokens && parent != null; i++)
      parent = this.getChildElementOf(parent, UNIT_ELEMENT, tokenizer.nextToken(), create);
    return
      (parent != null
       ? this.getChildElementOf(parent, elementName, tokenizer.nextToken(), create)
       : null);
  }

  /**
   * Returns the unit element with path <code>path</code>. If <code>create</code> is
   * <code>true</code>, the element will be created if it does not exist. Otherwise, the
   * method returns <code>null</code> if the element does not exist.
   */

  public Element getUnitElement (String path, boolean create)
  {
    return this.getDataSheetElement(UNIT_ELEMENT, path, create);
  }

  /**
   * Returns the unit element with path <code>path</code>. If it does not exist, returns
   * <code>null</code>.
   */

  public Element getUnitElement (String path)
  {
    return this.getDataSheetElement(UNIT_ELEMENT, path, false);
  }

  /**
   * Returns the data element with path <code>path</code>. If <code>create</code> is
   * <code>true</code>, the element will be created if it does not exist. Otherwise, the
   * method returns <code>null</code> if the element does not exist.
   */

  public Element getDataElement (String path, boolean create)
  {
    return this.getDataSheetElement(DATA_ELEMENT, path, create);
  }

  /**
   * Returns the data element with path <code>path</code>. If it does not exist, returns
   * <code>null</code>.
   */

  public Element getDataElement (String path)
  {
    return this.getDataSheetElement(DATA_ELEMENT, path, false);
  }

  /**
   * Removes the data sheet element with the specified name and path, and returns the
   * removed element. If the element does not exist, returns <code>null</code>.
   */

  protected Element removeDataSheetElement (String elementName, String path)
  {
    Element element = this.getDataSheetElement(elementName, path, false);
    if ( element != null )
      element.getParentNode().removeChild(element);
    return element;
  }

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

  public String getPathOf (Element element)
  {
    LinkedList nameValues = new LinkedList();
    while ( element != this.rootElement )
      {
        String nameValue = element.getAttribute(NAME_ATTRIBUTE);
        if ( nameValue == null )
          throw new IllegalArgumentException
            ("Element has no name attribute: " + element);
        nameValues.addFirst(nameValue);
        element = (Element)element.getParentNode();
      }
    StringBuffer path = new StringBuffer();
    Iterator iterator = nameValues.iterator();
    boolean separate = false;
    while ( iterator.hasNext() )
      {
        if ( separate ) path.append('/');
        path.append((String)iterator.next());
        separate = true;
      }
    return path.toString();
  }

  /**
   * Auxiliary method to implement {@link #getAllDataPaths getAllDataPaths} and
   * {@link #getAllUnitPaths getAllUnitPaths}. Returns the paths of all elements with name
   * <code>elementName</code>. 
   */

  protected String[] getAllPaths (String elementName)
  {
    NodeList dataElements =
      this.getDocument().getElementsByTagNameNS(NAMESPACE, elementName);
    String[] paths = new String[dataElements.getLength()];
    for (int i = 0; i < paths.length; i++)
      paths[i] = this.getPathOf((Element)dataElements.item(i));
    return paths;
  }

  /**
   * Returns the paths of all data elements.
   */

  public String[] getAllDataPaths ()
  {
    return this.getAllPaths(DATA_ELEMENT);
  }

  /**
   * Returns the paths of all unit elements.
   */

  public String[] getAllUnitPaths ()
  {
    return this.getAllPaths(UNIT_ELEMENT);
  }

  /**
   * Auxiliary method to implement {@link #getMatchingDataPaths getMatchingDataPaths} and
   * {@link #getMatchingUnitPaths getMatchingUnitPaths}. Returns all paths that match
   * <code>pathTemplate</code> and correspond to an element with name
   * <code>elementName</code>. <code>pathTemplate</code> is a wildecard pattern. See
   * {@link #getMatchingDataPaths getMatchingDataPaths} for the supported wildcards.
   */

  protected String[] getMatchingPaths (String elementName, String pathTemplate)
  {
    Pattern pattern = Pattern.compile(this.wildcardsToRegex(pathTemplate));
    String[] allPaths = this.getAllPaths(elementName);
    List matchingPaths = new ArrayList();
    for (int i = 0; i < allPaths.length; i++)
      {
        if ( pattern.matcher(allPaths[i]).matches() )
          matchingPaths.add(allPaths[i]);
      }
    return (String[])matchingPaths.toArray(new String[matchingPaths.size()]);
  }

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

  public String[] getMatchingDataPaths (String pathTemplate)
  {
    return this.getMatchingPaths(DATA_ELEMENT, pathTemplate);
  }

  /**
   * <p>
   *   Returns the paths of all data elements matching <code>pathTemplate</code>.
   * </p>
   * <p>
   *   <code>pathTemplate</code> is a path that may contain wildcards. See
   *   {@link #getMatchingDataPaths getMatchingDataPaths} for the supported wildcards.
   * </p>
   */

  public String[] getMatchingUnitPaths (String pathTemplate)
  {
    return this.getMatchingPaths(UNIT_ELEMENT, pathTemplate);
  }

  // --------------------------------------------------------------------------------
  // Output
  // --------------------------------------------------------------------------------

  /**
   * Outputs the XML code of this data sheet to <code>result</code>.
   */

  public void toXMLCode (Result result)
    throws DataSheetException
  {
    try
      {
        if ( this.transformer == null )
          {
            TransformerFactory factory = TransformerFactory.newInstance();
            this.transformer = factory.newTransformer();
            this.transformer.setOutputProperty(OutputKeys.ENCODING, "ASCII");
          }
        Source source = new DOMSource(this.getDocument());
        this.transformer.transform(source, result);
      }
    catch (Exception exception)
      {
        throw new DataSheetException(exception);
      }
  }

  /**
   * Outputs the XML code of this data sheet to <code>out</code>.
   */

  public void toXMLCode (OutputStream out)
    throws DataSheetException
  {
    this.toXMLCode(new StreamResult(out));
  }

  /**
   * Outputs the XML code of this data sheet to <code>writer</code>.
   */

  public void toXMLCode (Writer writer)
    throws DataSheetException
  {
    this.toXMLCode(new StreamResult(writer));
  }

  /**
   * Returns the XML code of this data sheet as a string.
   */

  public String toXMLCode ()
    throws DataSheetException
  {
    StringWriter writer = new StringWriter();
    this.toXMLCode(writer);
    return writer.toString();
  }

  // --------------------------------------------------------------------------------
  // Indentation
  // --------------------------------------------------------------------------------

  /**
   * Recursively indents the descendants of <code>node</code>. <code>indent</code> and
   * <code>childIndent</code> are the indentations of <code>node</code> and its childs,
   * respectively. 
   */

  protected void indent (Node node, String indent, String childIndent)
  {
    Node child = node.getFirstChild();
    short lastType = Node.ELEMENT_NODE;
    while ( child != null )
      {
        short type = child.getNodeType();
        if ( type == Node.ELEMENT_NODE  )
          {
            if ( lastType == Node.ELEMENT_NODE )
              node.insertBefore(this.getDocument().createTextNode("\n" + childIndent), child);
            if ( ((Element)child).getNamespaceURI().equals(NAMESPACE) )
              this.indent(child, childIndent, childIndent + "  ");
          }
        lastType = type;
        child = child.getNextSibling();
      }
    if ( lastType == Node.ELEMENT_NODE )
      node.appendChild(this.getDocument().createTextNode("\n" + indent));
  }

  /**
   * Indents the data sheet XML code.
   */

  public void indent ()
  {
    this.indent(this.rootElement, "", "  ");
  }

  // --------------------------------------------------------------------------------
  // Checking and Validating
  // --------------------------------------------------------------------------------

  /**
   * Returns <code>true</code> if <code>element</code> is a data sheet element (i.e., in the
   * namespace {@link #NAMESPACE}) of this data sheet, otherwise <code>false</code>.
   */

  public boolean isDataSheetElement (Element element)
  {
    Document document = element.getOwnerDocument();
    String namespace = element.getNamespaceURI();
    return ( document != null &&
             document == this.getDocument() &&
             namespace.equals(NAMESPACE) );
  }

  /**
   * Returns <code>true</code> if <code>element</code> is a unit or data element of this
   * data sheet, otherwise <code>false</code>.
   */

  public boolean isUnitOrDataElement (Element element)
  {
    return ( this.isDataSheetElement(element) &&
             ( element.getLocalName().equals(UNIT_ELEMENT) ||
               element.getLocalName().equals(DATA_ELEMENT) ) );
  }

  /**
   * Auxiliary method to validate the data sheet. Checks <code>element</code>. The
   * parameters <code>parentElementName</code> and <code>parentNamespace</code> are the name
   * and the namespace of the parent (or <code>null</code> if <code>element</code> does not
   * have a parent resp. a namespace with a namespace). They are needed to perform the
   * check.
   */

  protected void checkForValidation (Element element, String parentElementName,
                                     String parentNamespace)
    throws DataSheetException
  {
    String elementName = element.getLocalName();
    String namespace = element.getNamespaceURI();
    if ( namespace == null ) namespace = parentNamespace;
    if ( namespace != null && namespace.equals(NAMESPACE ) )
      {
        if ( parentNamespace != null && !parentNamespace.equals(NAMESPACE) )
          throw new DataSheetException("DataSheet element with non-DataSheet parent: " + element);
        if ( parentElementName != null &&  parentElementName.equals(DATA_ELEMENT) )
          throw new DataSheetException("DataSheet child of data element: " + element);
        if ( elementName.equals(UNIT_ELEMENT) || elementName.equals(DATA_ELEMENT) )
          {
            Attr nameAttr = element.getAttributeNode(NAME_ATTRIBUTE);
            if ( nameAttr == null )
              throw new DataSheetException("Missing name attribute: " + element);
            String nameValue = nameAttr.getValue();
            if ( this.namePattern == null )
              this.namePattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9-_.]*");
            if ( !this.namePattern.matcher(nameValue).matches() )
              throw new DataSheetException("Invalid name value: \"" + nameValue + "\"");
          }
        else if ( !elementName.equals(ROOT_ELEMENT) )
          throw new DataSheetException("Unknown DataSheet element: " + element);
      }
    NodeList children = element.getChildNodes();
    int length = children.getLength();
    for (int i = 0; i < length; i++)
      {
        Node child = children.item(i);
        switch ( child.getNodeType() )
          {
          case Node.ELEMENT_NODE:
            this.checkForValidation((Element)child, elementName, namespace);
            break;
          case Node.TEXT_NODE:
            this.checkForValidation((CharacterData)child, elementName, namespace);
            break;
          }
      }
  }

  /**
   * Auxiliary method to validate the data sheet. Checks <code>text</code>. The
   * parameters <code>parentElementName</code> and <code>parentNamespace</code> are the name
   * and the namespace of the parent (or <code>null</code> if <code>text</code> does not
   * have a parent resp. a parent with a namespace). They are needed to perform the check.
   * 
   */

  protected void checkForValidation (CharacterData text, String parentElementName,
                                     String parentNamespace)
    throws DataSheetException
  {
    if ( parentNamespace != null && parentNamespace.equals(NAMESPACE) &&
         parentElementName != null && parentElementName.equals(UNIT_ELEMENT) &&
         text.getData().trim().length() > 0 )
      throw new DataSheetException("Non-whitespace text node: " + text);
  }


  /**
   * Validates this data sheet. I.e., checks if the data sheet meets the specification.
   */

  public void validate ()
    throws DataSheetException
  {
    String namespace = this.rootElement.getNamespaceURI();
    if ( namespace == null )
      throw new DataSheetException("Missing namespace");
    if ( !namespace.equals(NAMESPACE) )
      throw new DataSheetException("Wrong namespace: " + namespace);
    if ( !this.rootElement.getLocalName().equals(ROOT_ELEMENT) )
      throw new IllegalArgumentException("Wrong root element: " + this.rootElement);
    this.checkForValidation(this.rootElement, null, null);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Returns the first <code>childName</code>-child element of <code>element</code>
   *   with a {@link #NAME_ATTRIBUTE} equal to <code>nameValue</code>. If
   *   <code>create</code> is <code>true</code>, the element will be created if it does not
   *   exist. Otherwise, this method returns <code>null</code> if the element does not
   *   exist.
   * </p>
   */

  protected Element getChildElementOf (Element element, String childName, String nameValue,
                                       boolean create)
  {
    NodeList children = element.getChildNodes();
    int length = children.getLength();
    Element childElement = null;
    for (int i = 0;  i < length && childElement == null; i++)
      {
        Node currentChild = children.item(i);
        if ( currentChild.getNodeType() == Node.ELEMENT_NODE &&
             currentChild.getLocalName().equals(childName) )
          {
            Element currentChildElement = (Element)currentChild;
            if ( currentChildElement.getAttribute(NAME_ATTRIBUTE).equals(nameValue) )
              childElement = currentChildElement;
          }
      }
    if ( childElement == null && create )
      {
        childElement = element.getOwnerDocument().createElementNS(NAMESPACE, childName);
        childElement.setAttribute(NAME_ATTRIBUTE, nameValue);
        element.appendChild(childElement);
      }
    return childElement;
  }

  /**
   * Converts a <code>NodeList</code> to an array of nodes.
   */

  protected Node[] nodeListToArray (NodeList nodeList)
  {
    Node[] nodeArray = new Node[nodeList.getLength()];
    for (int i = 0; i < nodeArray.length; i++)
      nodeArray[i] = nodeList.item(i);
    return nodeArray;
  }

  /**
   * Auxiliary method to convert a string to a <code>boolean</code>. Returns
   * <code>true</code> if <code>string</code> is <code>"true"</code> or <code>"yes"</code>,
   * <code>false</code> if <code>string</code> is <code>"false"</code> <code>"no"</code>,
   * and throws a <code>DataSheetException</code> if <code>string</code> has any other
   * value. Case does not matter.
   */

  protected boolean stringToBoolean (String string)
    throws DataSheetException
  {
    if ( string.equalsIgnoreCase("true") || string.equalsIgnoreCase("yes") )
      return true;
    else if ( string.equalsIgnoreCase("false") || string.equalsIgnoreCase("no") )
      return false;
    else
      throw new DataSheetException("Can not convert to boolean: " + string);
  }

  /**
   * Converts <code>string</code>, which may contain wildcards, to a regular expression.
   */

  protected String wildcardsToRegex (String string)
  {
    char[] chars = string.toCharArray();
    StringBuffer regex = new StringBuffer();
    for (int i = 0; i < chars.length; i++)
      {
        char c = chars[i];
        if ( ( '0' <= c && c <= '9' ) ||    //
             ( 'a' <= c && c <= 'z' ) ||    // Allowed name characters except '.'
             ( 'A' <= c && c <= 'Z' ) ||    // 
             c == '-' || c == '_' )         //
          regex.append(c);
        else if ( c == '.' )                // '.' - special meaning in regex
          regex.append("\\.");
        else if ( c == '/' )                // Name delimiter
          regex.append('/');
        else if ( c == '*' )                // Wildcard '*'
          regex.append("[^/]*");
        else if ( c == '?' )                // Wildcard '?'
          regex.append("[^/]");
        else
          throw new IllegalArgumentException
            ("Invalid name pattern: " + string + " (position " + i + ")");
      }
    return regex.toString();
  }
}
