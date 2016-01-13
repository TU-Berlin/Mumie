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

package net.mumie.srv.build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.xalan.extensions.ExpressionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Provides static methods suitable as XSL extension functions.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: XSLExtLib.java,v 1.4 2009/05/12 22:49:45 rassy Exp $</code>
 */

public class XSLExtLib
{
  // --------------------------------------------------------------------------------
  // h1: Constants
  // --------------------------------------------------------------------------------

  /**
   * Contains all alphabetic characters.
   */

  public static final String ALPH_CHARS =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  /**
   * Contains all numeric characters.
   */

  public static final String NUM_CHARS =
    "0123456789";

  /**
   * Contains all characters allowed as separators in names.
   */

  public static final String SEP_CHARS = "-_.";

  // --------------------------------------------------------------------------------
  // h1: Strings
  // --------------------------------------------------------------------------------

  /**
   * Returns the plural of the specified word.
   */

  public static String plural (String word)
  {
    if ( word.endsWith("s") )
      return word + "es";
    else if ( word.endsWith("y") )
      return word.substring(0, word.length()-1) + "ies";
    else
      return word + "s";
  }

  /**
   * Returns <code>string</code> with the first character changed to upper case.
   */

  public static String upcaseFirstChar (String string)
  {
    char[] chars = string.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  // --------------------------------------------------------------------------------
  // h1: Item keys
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates a string suitable as a key for a configuration item (a document type, an
   *   XML element name, etc.). Keys are also used as Java names for the constants
   *   representing the configuration entity.
   * </p>
   * <p>
   *   All non-separator characters are upcased, and all separator characters are replaced
   *   by an underscore.
   * </p>
   */

  public static final String itemKey (String name)
  {
    final String METHOD_NAME = "itemKey";
    StringBuilder builder = new StringBuilder();
    char[] chars = name.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        char c = chars[i];
        if ( ALPH_CHARS.indexOf(c) != -1 )
          builder.append(Character.toUpperCase(c));
        else if ( NUM_CHARS.indexOf(c) != -1 )
          {
            if ( i == 0 )
              throw new IllegalArgumentException
                (METHOD_NAME + ": Name must not start with a number: \"" + name + "\"");
            builder.append(c);
          }
        else if ( SEP_CHARS.indexOf(c) != -1 )
          builder.append('_');
        else
          throw new IllegalArgumentException
            (METHOD_NAME + ": Illegal character in \"" + name + "\" at position " + i +
             ": '" + c + "'");
      }
    return builder.toString();
  }

  /**
   * Returns the key for the configuration item represented by the specified node. The node
   * must be an attribute or element; otherwise, an exception is thrown. If the node is an
   * attribute, {@link #itemKey(String) itemKey(String)} is applied to its value. If the
   * node is an element, the following happens: If the element has a <code>key</code>
   * attribute, the value of that attribute is returned. If the element has no
   * <code>key</code> attribute, {@link #itemKey(String) itemKey(String)} is applied to the
   * value of the <code>name</code> attribute, and the result is returned. If neither the
   * <code>key</code> nor the <code>name</code> attribute exits, an exception is thrown.
   */

  public static final String itemKey (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "itemKey";
    switch ( node.getNodeType() )
      {
      case Node.ATTRIBUTE_NODE:
        return itemKey(((Attr)node).getValue());        
      case Node.ELEMENT_NODE:
        Element element = (Element)node;
        String key = element.getAttribute("key");
        if ( key.equals("") )
          {
            if ( element.getLocalName().equals("media-type") )
              {
                String type = element.getAttribute("type");
                if ( type.equals("") )
                  throw new ConfigException(METHOD_NAME + ": Missing \"type\" attribute");
                String subtype = element.getAttribute("subtype");
                if ( subtype.equals("") )
                  throw new ConfigException(METHOD_NAME + ": Missing \"subtype\" attribute");
                key = itemKey(type + "_" + subtype);
              }
            else
              {
                String name = element.getAttribute("name");
                if ( name.equals("") )
                  throw new ConfigException
                    (METHOD_NAME + ": Found neither \"key\" nor \"name\" attribute");
                key = itemKey(name);
              }
          }
        return key;
      default:
        throw new IllegalArgumentException
          (METHOD_NAME + ": Neither an element nor attribute node: " + node);
      }
  }

  /**
   * Returns the key for the configuration item represented by the context node. Same as
   * {@link #itemKey itemKey(context.getContextNode()))}.
   */

  public static final String itemKey (ExpressionContext context)
    throws ConfigException
  {
    return itemKey(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Java names
  // --------------------------------------------------------------------------------

  /**
   * Converts the specified string to a name following the Java conventions. That means the
   * following:
   * <ul>
   *   <li>The string is split into components, where components are the parts of the string
   *     separated by one or more characters in {@link #SEP_CHARS SEP_CHARS}.</li>
   *   <li>Each component is converted to lower case. The first character of each component,
   *     maybe except the first one (see below), is changed to upper case.</li>
   *   <li>If <code>startWithUpperCase</code> is <code>true</code>, the first character of
   *     the first component is changed to upper case, too, otherwise not.</li>
   *   <li>The components are concatenated.</li>
   * </ul>
   */

  public static String javaName (String string,
                                 boolean startWithUpperCase)
  {
    StringTokenizer tokenizer = new StringTokenizer(string, SEP_CHARS);
    StringBuffer buffer = new StringBuffer();
    boolean notFirst = false;
    while ( tokenizer.hasMoreTokens() )
      {
	String token = tokenizer.nextToken().toLowerCase();
	if ( notFirst || startWithUpperCase ) token = upcaseFirstChar(token);
	buffer.append(token);
	notFirst = true;
      }
    return buffer.toString();
  }

  /**
   * <p>
   *   Creates a Java class name from the specified string. The string is expected to be
   *   composed of lower case characters, digits, characters in {@link #SEP_CHARS SEP_CHARS}.
   *   The latter characters are interpreted as separators. The parts separated by them
   *   are joined. The first character of each part is capitalized. The separators are
   *   removed. For example, <code>generic_xsl_stylesheet</code> becomes
   *   <code>GenericXslStylesheet</code>.
   * </p>
   * <p>
   *   This method is equivalent to {@link #javaName javaName((name, "_-", true)}.
   * </p>
   */

  public static String javaClass (String name)
  {
    return javaName(name, true);
  }

  /**
   * Returns the java class name for the configuration item represented by the specified
   * node. The node must be an attribute or element; otherwise, an exception is thrown. If
   * the node is an attribute, {@link #javaClass(String) javaClass(String)} is applied to
   * its value. If the node is an element, the following happens: If the element has a
   * <code>java-class</code> attribute, the value of that attribute is returned.  Otherwise,
   * {@link #javaClass(String) javaClass(String)} is applied to the <code>name</code>
   * attribute and the result is returned. If neither the <code>java-class</code> nor the
   * <code>name</code> attribute exits, an exception is thrown.
   */

  public static String javaClass (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "javaClass";
    switch ( node.getNodeType() )
      {
      case Node.ATTRIBUTE_NODE:
        return javaClass(((Attr)node).getValue());
      case Node.ELEMENT_NODE:
        Element element = (Element)node;
        String javaClass = element.getAttribute("java-class");
        if ( javaClass.equals("") )
          {
            String name = element.getAttribute("name");
            if ( name.equals("") )
              throw new ConfigException
                (METHOD_NAME + ": Found neither \"java-class\" nor \"name\" attribute");
            javaClass = javaClass(name);
          }
        return javaClass;
      default:
        throw new IllegalArgumentException
          (METHOD_NAME + ": Neither an element nor attribute node: " + node);
      }
  }

  /**
   * Returns the java class name for the configuration item represented by the context
   * node. Same as {@link #javaClass javaClass(context.getContextNode()))}. 
   */

  public static String javaClass (ExpressionContext context)
    throws ConfigException
  {
    return javaClass(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Db names
  // --------------------------------------------------------------------------------

  /**
   * Converts the specified string to a name following database conventions. This means all
   * characters are made lower case, and characters in {@link #SEP_CHARS SEP_CHARS} are
   * replaced by underscores ( <code>'_'</code>).
   */

  public static String dbName (String string)
  {
    StringBuilder builder = new StringBuilder();
    for (char c : string.toCharArray())
      {
        if ( SEP_CHARS.indexOf(c) != -1 )
          builder.append('_');
        else
          builder.append(Character.toLowerCase(c));
      }
    return builder.toString();
  }

  /**
   * Creates the db table name for the specified document type. This is done by applying
   * {@link #dbName(String) dbName} and {@link #plural(String) plural} to it.
   */

  public static final String dbTable (String type)
  {
    return plural(dbName(type));
  }

  /**
   * Returns the db table name for the configuration item represented by the specified
   * node. The node must be an attribute or element; otherwise, an exception is thrown. If
   * the node is an attribute, {@link #dbTable(String) dbTable(String)} is applied to its
   * value. If the node is an element, the following happens: If the element has a
   * <code>db-table</code> attribute, the value of that attribute is returned.  Otherwise,
   * {@link #dbTable(String) dbTable(String)} is applied to the <code>name</code> attribute
   * and the result is returned. If neither the <code>db-table</code> nor the
   * <code>name</code> attribute exits, an exception is thrown.
   */

  public static String dbTable (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "dbTable";
    switch ( node.getNodeType() )
      {
      case Node.ATTRIBUTE_NODE:
        return dbTable(((Attr)node).getValue());
      case Node.ELEMENT_NODE:
        Element element = (Element)node;
        String dbTable = element.getAttribute("db-table");
        if ( dbTable.equals("") )
          {
            String name = element.getAttribute("name");
            if ( name.equals("") )
              throw new ConfigException
                (METHOD_NAME + ": Found neither \"db-table\" nor \"name\" attribute");
            dbTable = dbTable(name);
          }
        return dbTable;
      default:
        throw new IllegalArgumentException
          (METHOD_NAME + ": Neither an element nor attribute node: " + node);
      }
  }

  /**
   * Returns the db table name for the configuration item represented by the context
   * node. Same as {@link #dbTable dbTable(context.getContextNode()))}. 
   */

  public static String dbTable (ExpressionContext context)
    throws ConfigException
  {
    return dbTable(context.getContextNode());
  }

  /**
   * Returns the name of the db table storing references from documents of type
   * <code>originType</code> to documents of type <code>targetType</code>.
   */

  public static String dbRefTable (String originType, String targetType)
  {
    return "refs_" + dbName(originType) + "_" + dbName(targetType);
  }

  /**
   * Returns the name of the db table storing references between documents of the types
   * represented by the specified nodes. The nodes must be the <code>document-type</code>
   * elements of the origin and target document type, respectively.
   * 
   */

  public static String dbRefTable (Node originNode, Node targetNode)
    throws ConfigException
  {
    final String METHOD_NAME = "dbRefTable";
    Element originElement = toElement(originNode, METHOD_NAME);
    Element targetElement = toElement(targetNode, METHOD_NAME);
    String originType = originElement.getAttribute("name");
    String targetType = targetElement.getAttribute("name");
    if ( originType.equals("") )
      throw new ConfigException(METHOD_NAME + ": Missing \"name\" attribute for origin");
    if ( targetType.equals("") )
      throw new ConfigException(METHOD_NAME + ": Missing \"name\" attribute for target");
    return dbRefTable(originType, targetType);
  }

  /**
   * Returns the name of the db table for the version control threads of documents of the
   * specified type.
   */

  public static String dbVCThreadTable (String type)
  {
    return "vc_threads_" + dbName(type);
  }

  /**
   * Returns the vc thread db table name for the configuration item represented by the
   * specified node. The node must be an attribute or element; otherwise, an exception is
   * thrown. If the node is an attribute,
   * {@link #dbVCThreadTable(String) dbVCThreadTable(String)} is applied to its value. If
   * the node is an element, the value is obtained by applying
   * {@link #dbVCThreadTable(String) dbVCThreadTable(String)} to the <code>name</code>
   * attribute of the node; and an exception is thrown if the latter attribute does not
   * exist.
   */

  public static String dbVCThreadTable (Node node)
    throws ConfigException
  {
    return dbVCThreadTable(getName(node, "dbVCThreadTable"));
  }

  /**
   * Returns the vc threasd db table name for the configuration item represented by
   * the context. Same as
   * {@link #dbVCThreadTable(Node) dbVCThreadTable(context.getContextNode()))}.
   */

  public static String dbVCThreadTable (ExpressionContext context)
    throws ConfigException
  {
    return dbVCThreadTable(context.getContextNode());
  }

  /**
   * Returns the "read" db table name for the document type represented by the specified
   * node. The "read" table is the table from which data about (pseudo-)documents are
   * read. It is per default identical with the usual db table name for the document type
   * (cf. {@link dbTable(Node) dbTable(node)}), but may also be a view which adds extra
   * data to the table. Works as follows: The node must be an element node. If not, an
   * exception is thrown. If the element has a <code>read-db-table</code> attribute, that is
   * returned. Otherwise, {@link dbTable(Node) dbTable(node)} is called and the result is
   * returned. 
   */

  public static String dbReadTable (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "dbReadTable";
    if ( node.getNodeType() != Node.ELEMENT_NODE )
      throw new IllegalArgumentException
	(METHOD_NAME + ": Not an element node: " + node);
    Element element = (Element)node;
    String readDbTable = element.getAttribute("read-db-table");
    if ( readDbTable.equals("") )
      readDbTable = dbTable(node);
    return readDbTable;
  }

  /**
   * Returns the "read" db table name for the document type represented by the specified
   * context. Same as
   * {@link #dbReadTable(Node) dbReadTable(context.getContextNode()))}.
   */

  public static String dbReadTable (ExpressionContext context)
    throws ConfigException
  {
    return dbReadTable(context.getContextNode());
  }

  /**
   * Returns the name of the GDIM table for the specified (pseudo-)document type.
   */

  public static String dbGDIMTable (String type)
  {
    return "gdim_" + type;
  }

  /**
   * Returns the name of the GDIM table for the configuration item represented by
   * the specified node. The node must be an attribute or element; otherwise, an exception
   * is thrown. If the node is an attribute,
   * {@link #dbGDIMTable(String) dbGDIMTable(String)} is applied to its value. If the
   * node is an element, the value is obtained by applying
   * {@link #dbGDIMTable(String) dbGDIMTable(String)} to the <code>name</code>
   * attribute of the node; and an exception is thrown if the latter attribute does not exist.
   */

  public static String dbGDIMTable (Node node)
    throws ConfigException
  {
    return dbGDIMTable(getName(node, "dbGDIMTable"));
  }

  /**
   * Returns the name of GDIM table for the configuration item represented
   * by the context. Same as
   * {@link #dbGDIMTable(Node) dbGDIMTable(context.getContextNode()))}.
   * 
   */

  public static String dbGDIMTable (ExpressionContext context)
    throws ConfigException
  {
    return dbGDIMTable(context.getContextNode());
  }

  /**
   * Returns the name of the read permission table for the specified (pseudo-)document
   * type.
   */

  public static String dbReadPermTable (String type)
  {
    return "read_" + plural(dbName(type));
  }

  /**
   * Returns the name of the read permission table for the configuration item represented by
   * the specified node. The node must be an attribute or element; otherwise, an exception
   * is thrown. If the node is an attribute,
   * {@link #dbReadPermTable(String) dbReadPermTable(String)} is applied to its value. If the
   * node is an element, the value is obtained by applying
   * {@link #dbReadPermTable(String) dbReadPermTable(String)} to the <code>name</code>
   * attribute of the node; and an exception is thrown if the latter attribute does not exist.
   */

  public static String dbReadPermTable (Node node)
    throws ConfigException
  {
    return dbReadPermTable(getName(node, "dbReadPermTable"));
  }

  /**
   * Returns the name of the read permission table for the configuration item represented
   * by the context. Same as
   * {@link #dbReadPermTable(Node) dbReadPermTable(context.getContextNode()))}.
   * 
   */

  public static String dbReadPermTable (ExpressionContext context)
    throws ConfigException
  {
    return dbReadPermTable(context.getContextNode());
  }

  /**
   * Returns the name of the write permission table for the specified (pseudo-)document
   * type.
   */

  public static String dbWritePermTable (String type)
  {
    return "write_" + plural(dbName(type));
  }

  /**
   * Returns the name of the write permission table for the configuration item represented by
   * the specified node. The node must be an attribute or element; otherwise, an exception
   * is thrown. If the node is an attribute,
   * {@link #dbWritePermTable(String) dbWritePermTable(String)} is applied to its value. If the
   * node is an element, the value is obtained by applying
   * {@link #dbWritePermTable(String) dbWritePermTable(String)} to the <code>name</code>
   * attribute of the node; and an exception is thrown if the latter attribute does not exist.
   */

  public static String dbWritePermTable (Node node)
    throws ConfigException
  {
    return dbWritePermTable(getName(node, "dbWritePermTable"));
  }

  /**
   * Returns the name of the write permission table for the configuration item represented
   * by the context. Same as
   * {@link #dbWritePermTable(Node) dbWritePermTable(context.getContextNode()))}.
   * 
   */

  public static String dbWritePermTable (ExpressionContext context)
    throws ConfigException
  {
    return dbWritePermTable(context.getContextNode());
  }

  /**
   * Returns the name of the author table for the specified document type.
   */

  public static String dbAuthorTable (String type)
  {
    return dbName(type) + "_authors";
  }

  /**
   * Returns the name of the author table for the configuration item represented by
   * the specified node. The node must be an attribute or element; otherwise, an exception
   * is thrown. If the node is an attribute,
   * {@link #dbAuthorTable(String) dbAuthorTable(String)} is applied to its value. If the
   * node is an element, the value is obtained by applying
   * {@link #dbAuthorTable(String) dbAuthorTable(String)} to the <code>name</code>
   * attribute of the node; and an exception is thrown if the latter attribute does not exist.
   */

  public static String dbAuthorTable (Node node)
    throws ConfigException
  {
    return dbAuthorTable(getName(node, "dbAuthorTable"));
  }

  /**
   * Returns the name of the author table for the configuration item represented
   * by the context. Same as
   * {@link #dbAuthorTable(Node) dbAuthorTable(context.getContextNode()))}.
   * 
   */

  public static String dbAuthorTable (ExpressionContext context)
    throws ConfigException
  {
    return dbAuthorTable(context.getContextNode());
  }

  /**
   * Returns the name of the id sequence for the specified table.
   */

  public static String dbIdSec (String table)
  {
    return table + "_id_seq";
  }

  /**
   * Returns the name of the rule that creates a new vc thread for the specified document
   * type.
   */

  public static String dbRuleNewVCThread (String type)
  {
    return "new_" + dbName(type) + "_vc_thread";
  }

  /**
   * Returns the name of the rule that creates a new vc thread for the configuration item
   * represented by the specified node. The node must be an attribute or element; otherwise,
   * an exception is thrown. If the node is an attribute,
   * {@link #dbRuleNewVCThread(String) dbRuleNewVCThread(String)} is applied to its
   * value. If the node is an element, the value is obtained by applying
   * {@link #dbRuleNewVCThread(String) dbRuleNewVCThread(String)} to the
   * <code>name</code> attribute of the node; and an exception is thrown if the attribute
   * does not exist.
   */

  public static String dbRuleNewVCThread (Node node)
    throws ConfigException
  {
    return dbRuleNewVCThread(getName(node, "dbRuleNewVCThread"));
  }

  /**
   * Returns the name of the rule that creates a new vc thread for the configuration item 
   * represented by the context. Same as
   * {@link #dbRuleNewVCThread(Node) dbRuleNewVCThread(context.getContextNode()))}.
   */

  public static String dbRuleNewVCThread (ExpressionContext context)
    throws ConfigException
  {
    return dbRuleNewVCThread(context.getContextNode());
  }

  /**
   * Returns the name of the trigger function which automatically sets the last_modified
   * column of the db table for the specified document type.
   */

  public static String dbTriggerSetLastModified (String type)
  {
    return "update_" + dbName(type) + "_last_modified";
  }

  /**
   * Returns the name of the trigger function that creates a new vc thread for the
   * configuration item represented by the specified node. The node must be an attribute or
   * element; otherwise, an exception is thrown. If the node is an attribute,
   * {@link #dbTriggerSetLastModified(String) dbTriggerSetLastModified(String)} is
   * applied to its value. If the node is an element, the value is obtained by applying
   * {@link #dbTriggerSetLastModified(String) dbTriggerSetLastModified(String)} to the
   * <code>name</code> attribute of the node; and an exception is thrown if the attribute
   * does not exist.
   * 
   */

  public static String dbTriggerSetLastModified (Node node)
    throws ConfigException
  {
    return dbTriggerSetLastModified(getName(node, "dbTriggerSetLastModified"));
  }

  /**
   * Returns the name of the trigger that updates a document when a new version of a link
   * target is checked-in. <code>originType</code> and <code>targetType</code> are the types
   * of the link origin and target, respectively, which occur in the trigger name.
   */

  public static String dbTriggerUpdateLastModifiedByRef (String originType, String targetType)
  {
    return "update_last_modified_by_ref_" + originType + "_" + targetType;
  }

  /**
   * Returns the name of the trigger that updates a document when a new version of a link
   * target is checked-in. <code>origin</code> and <code>target</code> specify the types
   * of the link origin and target, respectively, which occur in the trigger name.
   */

  public static String dbTriggerUpdateLastModifiedByRef (Node origin, Node target)
    throws ConfigException
  {
    final String METHOD_NAME = "dbTriggerUpdateLastModifiedByRef";
    return dbTriggerUpdateLastModifiedByRef
      (getName(origin, METHOD_NAME), getName(target, METHOD_NAME));
  }

  // --------------------------------------------------------------------------------
  // h1: XML names
  // --------------------------------------------------------------------------------

  /**
   * Creates a string suitable as an XML element or attribute name from the specified
   * string. All separator characters are replaced by underscores.
   */

  public static final String xmlName (String string)
  {
    StringBuilder builder = new StringBuilder();
    for (char c : string.toCharArray())
      {
        if ( SEP_CHARS.indexOf(c) != -1 )
          builder.append('_');
        else
          builder.append(Character.toLowerCase(c));
      }
    return builder.toString();
  }

  /**
   * Creates the XML element name for the specified document type. This is done by applying
   * {@link #xmlName(String) xmlName} to it.
   */

  public static final String xmlElement (String type)
  {
    return xmlName(type);
  }

  /**
   * Returns the XML element name for the configuration item represented by the specified
   * node. The node must be an attribute or element; otherwise, an exception is thrown. If
   * the node is an attribute, {@link #xmlElement(String) xmlElement(String)} is applied to its
   * value. If the node is an element, the following happens: If the element has a
   * <code>xml-element</code> attribute, the value of that attribute is returned.  Otherwise,
   * {@link #xmlElement(String) xmlElement(String)} is applied to the <code>name</code> attribute
   * and the result is returned. If neither the <code>xml-element</code> nor the
   * <code>name</code> attribute exits, an exception is thrown.
   */

  public static String xmlElement (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "xmlElement";
    switch ( node.getNodeType() )
      {
      case Node.ATTRIBUTE_NODE:
        return xmlElement(((Attr)node).getValue());
      case Node.ELEMENT_NODE:
        Element element = (Element)node;
        String xmlElement = element.getAttribute("xml-element");
        if ( xmlElement.equals("") )
          {
            String name = element.getAttribute("name");
            if ( name.equals("") )
              throw new ConfigException
                (METHOD_NAME + ": Found neither \"xml-element\" nor \"name\" attribute");
            xmlElement = xmlElement(name);
          }
        return xmlElement;
      default:
        throw new IllegalArgumentException
          (METHOD_NAME + ": Neither an element nor attribute node: " + node);
      }
  }

  /**
   * Returns the XML element name for the configuration item represented by the context
   * node. Same as {@link #xmlElement xmlElement(context.getContextNode()))}. 
   */

  public static String xmlElement (ExpressionContext context)
    throws ConfigException
  {
    return xmlElement(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Content format
  // --------------------------------------------------------------------------------

  /**
   * Returns the content format for the configuration item represented by the specified
   * node. The latter must be an element node. Otherwise, an exception is thrown. If the
   * element has a <code>format</code> attribute, the value of that attribute is returned.
   * Otherwise, if the element's local name is <code>"document-type"</code> and the element
   * has no <code>is-generic-of</code> attribute, <code>"text"</code> is returned. Otherwise,
   * <code>"none"</code> is returned.
   */

  public static final String format (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "format";
    Element element = toElement(node, METHOD_NAME);
    String format = element.getAttribute("format");
    if ( format.equals("") )
      {
        if ( element.getLocalName().equals("document-type") &&
             element.getAttributeNode("is-generic-of") == null )
          format = "text";
        else
          format = "none";
      }
    return format;
  }

  /**
   * Returns the content format for the configuration item represented by the context
   * node. Same as {@link #format format(context.getContextNode()))}.
   */

  public static final String format (ExpressionContext context)
    throws ConfigException
  {
    return format(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Selector hints
  // --------------------------------------------------------------------------------

  /**
   * Returns the selector hint for the configuration item represented by the specified
   * node. The latter must be an element node. Otherwise, an exception is thrown. If the
   * element has a <code>hint</code> attribute, the value of that attribute is returned.
   * Otherwise, the value of the <code>name</code> attribute is returned. If neither the
   * <code>hint</code> nor the <code>name</code> attribute exits, an exception is thrown.
   */

  public static final String hint (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "hint";
    Element element = toElement(node, METHOD_NAME);
    String hint = element.getAttribute("hint");
    if ( hint.equals("") )
      hint = element.getAttribute("name");
    if ( hint.equals("") )
      throw new ConfigException
        (METHOD_NAME + ": Found neither \"hint\" nor \"name\" attribute");
    return hint;
  }

  /**
   * Returns the selector hint for the configuration item represented by the context
   * node. Same as {@link #hint hint(context.getContextNode()))}.
   */

  public static final String hint (ExpressionContext context)
    throws ConfigException
  {
    return hint(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Generic documents
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>is-generic</em> flag for the configuration item represented by the
   * specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has an <code>is-generic-of</code> attribute, the method returns
   * true; otherwise, it reuturns false.
   */

  public static boolean isGeneric (Node node)
  {
    final String METHOD_NAME = "isGeneric";
    Element element = toElement(node, METHOD_NAME);
    return ( element.getAttributeNode("is-generic-of") != null );
  }

  /**
   * Returns the <em>is-generic</em> flag for the configuration item represented by the
   * context node. Same as {@link #isGeneric isGeneric(context.getContextNode()))}.
   */

  public static boolean isGeneric (ExpressionContext context)
  {
    return isGeneric(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Category
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-category</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-category</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasCategory (Node node)
  {
    final String METHOD_NAME = "hasCategory";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-category"), false);
  }

  /**
   * Returns the <em>has-category</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasCategory hasCategory(context.getContextNode()))}.
   */

  public static boolean hasCategory (ExpressionContext context)
  {
    return hasCategory(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Qualified name
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-qualified-name</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-qualified-name</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasQualifiedName (Node node)
  {
    final String METHOD_NAME = "hasQualifiedName";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-qualified-name"), false);
  }

  /**
   * Returns the <em>has-qualified-name</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasQualifiedName hasQualifiedName(context.getContextNode()))}.
   */

  public static boolean hasQualifiedName (ExpressionContext context)
  {
    return hasQualifiedName(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Width and height
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-width-and-height</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-width-and-height</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasWidthAndHeight (Node node)
  {
    final String METHOD_NAME = "hasWidthAndHeight";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-width-and-height"), false);
  }

  /**
   * Returns the <em>has-width-and-height</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasWidthAndHeight hasWidthAndHeight(context.getContextNode()))}.
   */

  public static boolean hasWidthAndHeight (ExpressionContext context)
  {
    return hasWidthAndHeight(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Duration
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-duration</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-duration</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasDuration (Node node)
  {
    final String METHOD_NAME = "hasDuration";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-duration"), false);
  }

  /**
   * Returns the <em>has-duration</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasDuration hasDuration(context.getContextNode()))}.
   */

  public static boolean hasDuration (ExpressionContext context)
  {
    return hasDuration(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Corrector
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-corrector</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-corrector</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasCorrector (Node node)
  {
    final String METHOD_NAME = "hasCorrector";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-corrector"), false);
  }

  /**
   * Returns the <em>has-corrector</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasCorrector hasCorrector(context.getContextNode()))}.
   */

  public static boolean hasCorrector (ExpressionContext context)
  {
    return hasCorrector(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Class
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-class</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-class</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasClass (Node node)
  {
    final String METHOD_NAME = "hasClass";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-class"), false);
  }

  /**
   * Returns the <em>has-class</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasClass hasClass(context.getContextNode()))}.
   */

  public static boolean hasClass (ExpressionContext context)
  {
    return hasClass(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Members
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-members</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-members</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasMembers (Node node)
  {
    final String METHOD_NAME = "hasMembers";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-members"), false);
  }

  /**
   * Returns the <em>has-members</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasMembers hasMembers(context.getContextNode()))}.
   */

  public static boolean hasMembers (ExpressionContext context)
  {
    return hasMembers(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Timeframe
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-timeframe</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-timeframe</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasTimeframe (Node node)
  {
    final String METHOD_NAME = "hasTimeframe";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-timeframe"), false);
  }

  /**
   * Returns the <em>has-timeframe</em> flag for the configuration item represented
   * by the context node. Same as
   * {@link #hasTimeframe(Node) hasTimeframe(context.getContextNode()))}.
   */

  public static boolean hasTimeframe (ExpressionContext context)
  {
    return hasTimeframe(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Summary
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-summary</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-summary</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasSummary (Node node)
  {
    final String METHOD_NAME = "hasSummary";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-summary"), false);
  }

  /**
   * Returns the <em>has-summary</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasSummary hasSummary(context.getContextNode()))}.
   */

  public static boolean hasSummary (ExpressionContext context)
  {
    return hasSummary(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Points
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-points</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-points</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasPoints (Node node)
  {
    final String METHOD_NAME = "hasPoints";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-points"), false);
  }

  /**
   * Returns the <em>has-points</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasPoints hasPoints(context.getContextNode()))}.
   */

  public static boolean hasPoints (ExpressionContext context)
  {
    return hasPoints(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Label
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>has-label</em> flag for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>has-label</code> attribute, its value is
   * converted to a boolean and returned. Otherwise, false is returned.
   */

  public static boolean hasLabel (Node node)
  {
    final String METHOD_NAME = "hasLabel";
    Element element = toElement(node, METHOD_NAME);
    return toBoolean(element.getAttribute("has-label"), false);
  }

  /**
   * Returns the <em>has-label</em> flag for the configuration item represented
   * by the context node.  Same as
   * {@link #hasLabel hasLabel(context.getContextNode()))}.
   */

  public static boolean hasLabel (ExpressionContext context)
  {
    return hasLabel(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Refs-to and no-refs-to
  // --------------------------------------------------------------------------------

  /**
   * Returns the <em>refs-to</em> value for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>refs-to</code> attribute, its value is returned.
   * Otherwise, null is returned.
   */

  public static String refsTo (Node node)
  {
    final String METHOD_NAME = "refsTo";
    Element element = toElement(node, METHOD_NAME);
    Attr attribute = element.getAttributeNode("refs-to");
    String refsTo = (attribute != null ? attribute.getValue() : "*");
    return refsTo;
  }

  /**
   * Returns the <em>refs-to</em> value for the configuration item represented by the
   * context node. Same as {@link #refsTo(Node) refsTo(context.getContextNode())}.
   */

  public static String refsTo (ExpressionContext context)
  {
    return refsTo(context.getContextNode());
  }

  /**
   * Returns the <em>no-refs-to</em> value for the configuration item represented
   * by the specified node. The latter must be an element node. Otherwise, an exception is
   * thrown. If the element has a <code>no-refs-to</code> attribute, its value is returned.
   * Otherwise, the empty string is returned.
   */

  public static String noRefsTo (Node node)
  {
    final String METHOD_NAME = "noRefsTo";
    Element element = toElement(node, METHOD_NAME);
    Attr attribute = element.getAttributeNode("no-refs-to");
    String noRefsTo = (attribute != null ? attribute.getValue() : "");
    return noRefsTo;
  }

  /**
   * Returns the <em>no-refs-to</em> value for the configuration item represented by the
   * context node. Same as {@link #noRefsTo(Node) noRefsTo(context.getContextNode())}.
   */

  public static String noRefsTo (ExpressionContext context)
  {
    return noRefsTo(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Suffixes
  // --------------------------------------------------------------------------------

  /**
   * Returns the suffix for the configuration item represented by the specified
   * node. The latter must be an element node. Otherwise, an exception is thrown. If the
   * element has a <code>suffix</code> attribute, the value of that attribute is returned.
   * Otherwise, the value of the <code>name</code> attribute is returned. If neither the
   * <code>suffix</code> nor the <code>name</code> attribute exits, an exception is thrown.
   */

  public static final String suffix (Node node)
    throws ConfigException
  {
    final String METHOD_NAME = "suffix";
    Element element = toElement(node, METHOD_NAME);
    String suffix = element.getAttribute("suffix");
    if ( suffix.equals("") )
      {
        if ( element.getLocalName().equals("media-type") )
          {
            suffix = element.getAttribute("subtype");
            if ( suffix.equals("") )
              throw new ConfigException
                (METHOD_NAME + ": Found neither \"suffix\" nor \"subtype\" attribute");
          }
        else
          {
            suffix = element.getAttribute("name");
            if ( suffix.equals("") )
              throw new ConfigException
                (METHOD_NAME + ": Found neither \"suffix\" nor \"name\" attribute");
          }
      }
    return suffix;
  }

  /**
   * Returns the selector suffix for the configuration item represented by the context
   * node. Same as {@link #suffix suffix(context.getContextNode()))}.
   */

  public static final String suffix (ExpressionContext context)
    throws ConfigException
  {
    return suffix(context.getContextNode());
  }

  // --------------------------------------------------------------------------------
  // h1: Booleans
  // --------------------------------------------------------------------------------

  /**
   * Gets a boolean from a string. If the string is null or the empty string, returns the
   * specified default value. Otherwise, if the string equals <code>"yes"</code> or
   * <code>"true"</code>, returns true. Otherwise, if the string equals <code>"no"</code> or
   * <code>"false"</code>, returns false. Otherwise, throws an exception.
   */

  public static boolean toBoolean (String string, boolean defaultValue)
  {
    if ( string == null || string.equals("") )
      return defaultValue;
    else if ( string.equals("yes") || string.equals("true") )
      return true;
    else if ( string.equals("no") || string.equals("false") )
      return false;
    else
      throw new IllegalArgumentException
        ("getBoolean: Illegal boolean specifier: " + string);
  }

  /**
   * Same as {@link #toBoolean(String,boolean) toBoolean(string, false)}.
   */

  public static final boolean toBoolean (String string)
  {
    return toBoolean(string, false);
  }

  // --------------------------------------------------------------------------------
  // h1: Strings, text, Javadoc comments
  // --------------------------------------------------------------------------------

  /**
   * Returns the indentation of the specified string. The indentation is the number of
   * space characters before the first non-space character.
   */

  public static int getIndent (String string)
  {
    if ( string.trim().length() == 0 ) return -1;
    int pos = 0;
    while ( string.charAt(pos) == ' ' ) pos++;
    return pos;
  }

  /**
   * Indents the specified text.
   */

  public static String indent (String text, int indent)
  {
    final String NEWLINE = System.getProperty("line.separator");

    String[] lines = text.split(Pattern.quote(NEWLINE));

    // Get current indentation:
    int textIndent = -1;
    for (String line : lines)
      {
        int lineIndent = getIndent(line);
        if ( lineIndent != -1 && ( lineIndent < textIndent || textIndent == -1 ) )
          textIndent = lineIndent;
      }
    if ( textIndent == -1 ) textIndent = 0;

    // Space characters for new indentation:
    char[] spaces = new char[indent];
    Arrays.fill(spaces, ' ');

    // Set new indentation:
    StringBuilder buffer = new StringBuilder();
    for (String line : lines)
      {
        buffer.append(spaces);
        if ( line.length() > textIndent )
          buffer.append(line.substring(textIndent));
        buffer.append(NEWLINE);
      }

    return buffer.toString();
  }

  /**
   * Formats the the specified text as a javadoc comment.
   */

  public static String javadocComment (String text, int indent)
  {
    final String NEWLINE = System.getProperty("line.separator");

    String[] lines = text.split(Pattern.quote(NEWLINE));

    // Get current indentation:
    int textIndent = -1;
    for (String line : lines)
      {
        int lineIndent = getIndent(line);
        if ( lineIndent != -1 && ( lineIndent < textIndent || textIndent == -1 ) )
          textIndent = lineIndent;
      }
    if ( textIndent == -1 ) textIndent = 0;

    // Space characters for new indentation:
    char[] spaces = new char[indent];
    Arrays.fill(spaces, ' ');

    // Format comment:
    StringBuilder buffer = new StringBuilder();
    buffer.append(spaces).append("/**").append(NEWLINE);
    for (int i = 0; i < lines.length; i++)
      {
        String line = lines[i];
        // Ignore first resp.last line if empty:
        if ( ( i == 0 || i == lines.length-1 ) && line.trim().length() == 0 ) continue;
        buffer.append(spaces).append(" * ");
        if ( line.length() > textIndent )
          buffer.append(line.substring(textIndent));
        buffer.append(NEWLINE);
      }
    buffer.append(spaces).append(" */");

    return buffer.toString();
  }

  /**
   * Returns the javadoc comment for the configuration item represented by the specified
   * node. <code>indent</code> is the number of spaces by which the comment is to be
   * indented.
   */

  public static String javadocComment (Node node, int indent)
    throws ConfigException
  {
    final String METHOD_NAME = "javadocComment";
    Element element = toElement(node, METHOD_NAME);
    String text = element.getAttribute("description");
    if ( text.equals("") )
      {
        Element descrElement = (Element)element.getElementsByTagName("description").item(0);
        if ( descrElement != null )
          {
            Node content = descrElement.getFirstChild();
            if ( content.getNodeType() != Node.TEXT_NODE )
              throw new ConfigException
                (METHOD_NAME + ": Illegal content of \"description\" element: " + content +
                 " (expected text content)");
            text = content.getNodeValue();
          }
      }
    return javadocComment(text, indent);
  }

  /**
   * Returns the javadoc comment for the configuration item represented by the specified
   * node.
   */

  public static String javadocComment (Node node)
    throws ConfigException
  {
    return javadocComment(node, 2);
  }    

  /**
   * Returns the javadoc comment for the configuration item represented by the context
   * node.
   */

  public static String javadocComment (ExpressionContext context)
    throws ConfigException
  {
    return javadocComment(context.getContextNode(), 2);
  }    

  // --------------------------------------------------------------------------------
  // h1: Other utililies
  // --------------------------------------------------------------------------------

  /**
   * Checks if the specified string is listed in <code>included</code>, but not in
   * <code>excluded</code>.
   */

  public static boolean enabled (String string, String included, String excluded)
  {
    final String SEPARATORS = ", \t\n\r\f";
    boolean enabled = false;

    if ( included.equals("*") )
      enabled = true;
    else
      {
        StringTokenizer includedTokenizer = new StringTokenizer(included, SEPARATORS);
        while ( includedTokenizer.hasMoreTokens() && !enabled )
          {
            String token = includedTokenizer.nextToken();
            if ( token.equals(string) ) enabled = true;
          }
      }

    if ( excluded.equals("*") )
      enabled = false;
    else if ( enabled )
      {
        StringTokenizer excludedTokenizer = new StringTokenizer(excluded, SEPARATORS);
        while ( excludedTokenizer.hasMoreTokens() && enabled )
          {
            String token = excludedTokenizer.nextToken();
            if ( token.equals(string) ) enabled = false;
          }
      }

    return enabled;
  }

  /**
   * Returns true if references are between the document types represented by the specified
   * nodes.
   */

  public static boolean refsEnabled (Node origin, Node target)
    throws ConfigException
  {
    final String METHOD_NAME = "refsEnabled";
    String targetType = toElement(target, METHOD_NAME).getAttribute("name");
    if ( targetType.equals("") )
      throw new ConfigException(METHOD_NAME + ": Missing \"name\" attribute for target");
    return enabled(targetType, refsTo(origin), noRefsTo(origin));
  }

  // --------------------------------------------------------------------------------
  // h1: Internal auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Casts the specified node to {@link Element Element} and returns the latter. If the
   * specified node is not an element node, an exception is thrown. The string
   * <code>METHOD_NAME</code> is used as a prefix in error messages. It should be set to the
   * name of the calling method.
   */

  protected static Element toElement (Node node, final String METHOD_NAME)
  {
    if ( node.getNodeType() != Node.ELEMENT_NODE )
      throw new IllegalArgumentException(METHOD_NAME + ": Not an element node: " + node);
    return (Element)node;
  }

  /**
   * Returns a name from the specified node. If the node is an attribute, its value is
   * returned. If the node is an element, the value of the <code>name</code> attribute of
   * the element is returned. If the element has no such attribute, an exception is
   * thrown. If the node is neither an attribute nor an element, an exception is thrown,
   * too.
   */

  protected static String getName (Node node, final String METHOD_NAME)
    throws ConfigException
  {
    switch ( node.getNodeType() )
      {
      case Node.ATTRIBUTE_NODE:
        return ((Attr)node).getValue();
      case Node.ELEMENT_NODE:
        Element element = (Element)node;
        String name = element.getAttribute("name");
        if ( name.equals("") )
          throw new ConfigException
            (METHOD_NAME + ": Missing  \"name\" attribute");
        return name;
      default:
        throw new IllegalArgumentException
          (METHOD_NAME + ": Neither an element nor attribute node: " + node);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Other utilities
  // --------------------------------------------------------------------------------

  /**
   * Encloses the specified string with single quotes, and protects single quotes and
   * backslashes within the string. The latter is done by replacing each quote by two
   * quotes, and each backslash by two backslashes.
   * 
   */

  public static String quoteSQL (String string)
  {
    StringBuilder result = new StringBuilder();
    result.append("'");
    char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        switch ( chars[i] )
          {
          case '\\':
            result.append("\\\\"); break;
          case '\'':
            result.append("''"); break;
          default:
            result.append(chars[i]);
          }
      }
    result.append("'");
    return result.toString();
  }

}
