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

package net.mumie.mathletfactory.util.xml;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcosOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcoshOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcotOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcothOp;
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.AsinOp;
import net.mumie.mathletfactory.math.algebra.op.node.AsinhOp;
import net.mumie.mathletfactory.math.algebra.op.node.AtanOp;
import net.mumie.mathletfactory.math.algebra.op.node.AtanhOp;
import net.mumie.mathletfactory.math.algebra.op.node.ConjOp;
import net.mumie.mathletfactory.math.algebra.op.node.CosOp;
import net.mumie.mathletfactory.math.algebra.op.node.CoshOp;
import net.mumie.mathletfactory.math.algebra.op.node.CotOp;
import net.mumie.mathletfactory.math.algebra.op.node.CothOp;
import net.mumie.mathletfactory.math.algebra.op.node.ExpOp;
import net.mumie.mathletfactory.math.algebra.op.node.FacOp;
import net.mumie.mathletfactory.math.algebra.op.node.FloorOp;
import net.mumie.mathletfactory.math.algebra.op.node.ImOp;
import net.mumie.mathletfactory.math.algebra.op.node.LnOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PiecewiseConstOp;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;
import net.mumie.mathletfactory.math.algebra.op.node.ReOp;
import net.mumie.mathletfactory.math.algebra.op.node.SinOp;
import net.mumie.mathletfactory.math.algebra.op.node.SinhOp;
import net.mumie.mathletfactory.math.algebra.op.node.TanOp;
import net.mumie.mathletfactory.math.algebra.op.node.TanhOp;
import net.mumie.mathletfactory.math.algebra.op.node.UneditedOp;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.math.number.NumberFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class helps to load and save MathML-Nodes from/to XML-Files.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class XMLUtils {

	public final static String MATHML_NAMESPACE = "http://www.w3.org/1998/Math/MathML";
	public final static String MATHML_EXT_NAMESPACE = "http://www.mumie.net/xml-namespace/mathml-ext";
	public final static String MARKING_NAMESPACE = "http://www.mumie.net/xml-namespace/marking";

	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db;
	private static Document doc;
	private static boolean printXMLNamespaces;
	
	/** Field used to cache the omitting XML namespaces flag. */
	private static Boolean omitsXMLNamespaces;

	static {
		try {
			printXMLNamespaces = isOmittingXMLNamespaces();
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware( true );
			db = dbf.newDocumentBuilder();
			doc = db.getDOMImplementation().createDocument( DataSheet.NAMESPACE, DataSheet.ROOT_ELEMENT, null );// newDocument();
		} catch ( FactoryConfigurationError e ) {
			throw new XMLParsingException("XML configuration error", e);
		} catch ( ParserConfigurationException e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
	}

	/**
	 * Returns the default document to create new nodes.
	 */
	public static Document getDefaultDocument() {
		return doc;
	}

	/**
	 * Creates a new DocumentBuilder instance.
	 */
	public static DocumentBuilder createDocumentBuilder() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware( true );
			return dbf.newDocumentBuilder();
		} catch ( ParserConfigurationException e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
	}

	/**
	 * Creates a new DataSheet instance with the nodes defined in the file
	 * <code>f</code> and returns it.
	 * 
	 * @return null if an exception occurs
	 */
	public static DataSheet createDataSheetFromFile( File f ) {

		try {
			Document doc = db.parse( f );
			return new DataSheet( doc );
		} catch ( SAXException e ) {
			throw new XMLParsingException("XML configuration error", e);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the first MathML-Node which is embedded in a DataSheet read from
	 * a file.
	 */
	public static Node getNodeFromDataSheetFile( String dataElement, File f ) {
		return getFirstNodeFromDataElement( createDataSheetFromFile( f ).getDataElement( dataElement ) );
	}

	/**
	 * Returns the first child node of <code>dataElement</code> whose type is
	 * not Node.TEXT_NODE (which would only contain data for structuring the
	 * XML-Element. <code>dataElement</code> must be a "data"-element. Used
	 * for extracting the MathML-Nodes from a data-Element.
	 */
	public static Node getFirstNodeFromDataElement( Element dataElement ) {
		if ( !dataElement.getNodeName().equalsIgnoreCase( "data" ) ) throw new NumberFormatException( "Data element name must be \"<data>\"!" );

		NodeList children = dataElement.getChildNodes();
		for ( int i = 0; i < children.getLength(); i++ ) {
			if ( children.item( i ).getNodeType() == Node.TEXT_NODE )
				continue;
			else
				return children.item( i );
		}
		return null;
	}
	
	/**
	 * Loads the DataSheet from an input stream.
	 */
	public static DataSheet loadDataSheetFromStream(InputStream inputStream) {
		DataSheet dataSheet = null;
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			dataSheet = new DataSheet( document );
		} catch ( Exception e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
		return dataSheet;
	}

	/**
	 * Loads the DataSheet from the file with the given filename.
	 */
	public static DataSheet loadDataSheetFromFile( String filename ) {
		DataSheet dataSheet = null;
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse( new File( filename ) );

			dataSheet = new DataSheet( document );
		} catch ( Exception e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
		return dataSheet;
	}

	/**
	 * Loads the DataSheet from the file with the given URL.
	 */
	public static DataSheet loadDataSheetFromURL( URL file ) {
		DataSheet dataSheet = null;
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();

			Document document = builder.parse( file.openStream() );

			dataSheet = new DataSheet( document );
		} catch ( Exception e ) {
			System.err.println( "The datasheet could not be loaded from " + file + ": " + e );
		}
		return dataSheet;
	}

	/**
	 * Loads the DataSheet from the file with the given URL string.
	 */
	public static DataSheet loadDataSheetFromURL( String urlString ) {
		try {
			return loadDataSheetFromURL( new URL( urlString ) );
		} catch ( MalformedURLException e ) {
			System.err.println( "The URL \"" + urlString + "\" is invalid!" );
			return null;
		}
	}

	/**
	 * Saves the DataSheet <code>dataSheet</code> to the file f.
	 */
	public static void saveDataSheetToFile( DataSheet dataSheet, File f ) {
		try {
			// Use a Transformer for output
			Transformer transformer = createDefaultTransformer();
			DOMSource source = new DOMSource( dataSheet.getDocument() );
			StreamResult result = new StreamResult( new FileOutputStream( f ) );
			transformer.transform( source, result );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new empty instance of DataSheet.
	 */
	public static DataSheet createDataSheet() {
		DataSheet d = new DataSheet( createDocumentBuilder() );
		return d;
	}

	/**
	 * Creates a new instance of DataSheet with the given MathML-node contained
	 * in a data-element with the given path.
	 */
	public static DataSheet createDataSheet( String path, Node mathmlNode ) {
		DataSheet d = createDataSheet();
		d.getDataElement( path, true ).appendChild( mathmlNode );
		return d;
	}

	/**
	 * Creates a new instance of DataSheet with the given MathML-nodes contained
	 * in data-elements with the given paths.
	 */
	public static DataSheet createDataSheet( String[] paths, Node[] mathmlNodes ) {
		if ( paths.length != mathmlNodes.length ) throw new IllegalArgumentException( "Both arrays must have the same size!" );

		DocumentBuilder builder = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch ( ParserConfigurationException e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
		DataSheet dataSheet = new DataSheet( builder );
		for ( int i = 0; i < paths.length; i++ ) {
			Element compatibleMathmlNode = ( Element ) dataSheet.getDocument().importNode( mathmlNodes[i], true );
			dataSheet.getDataElement( paths[i], true ).appendChild( compatibleMathmlNode );
		}
		return dataSheet;
	}

	/**
	 * Returns the MNumber class that fits to the given field value:
	 * 
	 * real : MDouble.class is returned complex : MComplex.class is returned
	 * rational : MRational.class is returned
	 */
	public static Class getNumberClassFromField( String field ) {
		if ( field == null ) return null;
		if ( field.equals( "real" ) ) return MDouble.class;
		if ( field.equals( "complex" ) ) return MComplex.class;
		if ( field.equals( "rational" ) ) return MRational.class;
		if ( field.equals( "complex-rational" ) ) return MComplexRational.class;
		if ( field.equals( "integer" ) ) return MInteger.class;
		throw new IllegalArgumentException( "unsupported field :" + field );
	}

  /**
   * Returns if a data element exists in the specified data sheet under the specified path.
   */
  public static boolean isDataElement(DataSheet ds,String path) {
    try {
      return ds.getAsElement(path) != null;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns if a unit element exists in the specified data sheet under the specified path.
   */
  public static boolean isUnitElement(DataSheet ds,String path) {
    try {
      return ds.getUnitElement(path) != null;
    } catch (Exception e) {
      return false;
    }
  }

	/**
	 * This utility method returns a XML String representation of the given
	 * node.
	 */
	public static String nodeToString( Node node ) {
		return nodeToString( node, false );
	}

	/**
	 * This utility method returns a XML String representation of the given node
	 * without the xml declaration at the beginning of the String.
	 */
	public static String nodeToString( Node node, boolean omitXMLDeclaration ) {
		StringWriter writer = new StringWriter();
		try {
			Transformer transformer = createDefaultTransformer();
			DOMSource source = new DOMSource( node );
			if ( omitXMLDeclaration ) transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
			transformer.transform( source, new StreamResult( writer ) );
		} catch ( Exception e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
		return writer.toString();
	}
	
	/**
	 * Returns a new transformer instance with the appropriate output properties.
	 */
	public static Transformer createDefaultTransformer() {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			transformer.setOutputProperty( OutputKeys.ENCODING, "US-ASCII" );
			return transformer;
		} catch ( TransformerException e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
	}

	/**
	 * Returns the first XML-node from a file
	 */
	public static Node createNodeFromFile( File f ) {
		Document doc = null;
		try {
			doc = db.parse( f );
		} catch ( SAXException e ) {
			throw new XMLParsingException("XML configuration error", e);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return doc.getChildNodes().item( 0 );
	}

	/**
	 * Saves this XML-Node to a file
	 */
	public static void saveNodeToFile( Node node, File file ) {
		try {
			FileWriter writer = new FileWriter( file );
			writer.write( node.toString() );
			writer.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a XML Node with specified (and printed!) namespace and tag name.
	 */
	public static Element createNamespaceElement( String namespaceURI, String tagName ) {
		Element e = getDefaultDocument().createElementNS( namespaceURI, tagName );
		// e.setAttribute("xmlns", namespaceURI);
		return e;
	}

	/**
	 * Creates a XML Node with specified tag name.
	 */
	public static Element createElement( String tagName ) {
		return getDefaultDocument().createElement( tagName );
	}

	/**
	 * Create a XML node from the given String.
	 */
	public static Element createElementFromString( String xmlContent ) {
		Element result = null;
		try {
			result = createDocumentBuilder().parse( new ByteArrayInputStream( xmlContent.getBytes() ) ).getDocumentElement();
		} catch ( Exception e ) {
			throw new XMLParsingException("XML configuration error", e);
		}
		return result;
	}

	/**
	 * Creates a MathML node consisting of a single operator node with an
	 * <code>unedited</code>-flag place holder sign.
	 */
	public static Node createUneditedNode( Document doc ) {
		Element mo = doc.createElementNS( XMLUtils.MATHML_NAMESPACE, ExerciseObjectIF.UNEDITED_NODE_NAME );
		return mo;
	}

	public static Node createEmptyMarkingNode( Document doc ) {
//		return doc.createTextNode( "?" );
		Element mi = doc.createElementNS( XMLUtils.MATHML_NAMESPACE, ExerciseObjectIF.UNEDITED_NODE_NAME );
		mi.setAttribute("class", "void");
		mi.appendChild(doc.createTextNode("?"));
		return mi;
	}

	// public static String parseMathMLExpression(Node expr) {
	// try {
	// String functionString = new String();
	// if(expr.getNodeName().equalsIgnoreCase("mn")
	// || expr.getNodeName().equalsIgnoreCase("mfrac")
	// || expr.getNodeName().equalsIgnoreCase("cnum")) {
	// return NumberFactory.parseMathMLNumber(expr).toString();
	// } else if(expr.getNodeName().equalsIgnoreCase("mi")
	// || expr.getNodeName().equalsIgnoreCase("mo")) {
	// return nodeToString(expr.getChildNodes().item(0), true);
	// } else if(expr.getNodeName().equalsIgnoreCase("mrow")) {
	// for (int i = 0; i < expr.getChildNodes().getLength(); i++) {
	// functionString += parseMathMLExpression(expr.getChildNodes().item(i));
	// }
	// } else if(expr.getNodeName().equalsIgnoreCase("msup")) {
	// int index = 0;
	// index = getNextNonTextNodeIndex(expr, index);
	// String s1 = parseMathMLExpression(expr.getChildNodes().item(index));
	// index = getNextNonTextNodeIndex(expr, index+1);
	// String s2 = parseMathMLExpression(expr.getChildNodes().item(index));
	// functionString += s1 + "^" + s2;
	// } else if(expr instanceof TextNode) {
	// functionString += expr.getNodeValue().trim();
	// }
	// return functionString;
	// } catch(Exception e) {
	// MessageConsole.addException(e);
	// return null;
	// }
	// }

	private static void debug( String message ) {
	// System.out.println(message);
	}

	/**
	 * Returns a symbolic representation from a MathML node, readable from
	 * operations.
	 */
	public static OpNode parseMathMLNode( Class numberClass, Node node ) {
		try {
			if ( node.getNodeName().equalsIgnoreCase( "mn" ) || node.getNodeName().equalsIgnoreCase( "cnum" ) ) {// all
				// number
				// nodes
				MNumber number = NumberFactory.parseMathMLNumber( numberClass, node );
				if ( !( ( number instanceof MComplex ) || ( number instanceof MComplexRational ) ) && number.getDouble() < 0 ) {
					number.abs();
					OpNode numberNode = new NumberOp( number );
					numberNode.setFactor( -1.0 );
					return numberNode;
				}
				return new NumberOp( number );
			} else if ( node.getNodeName().equalsIgnoreCase( "mfrac" ) ) {
				int index = getNextNonTextNodeIndex( node, 0 );
				Node node1 = node.getChildNodes().item( index );
				index = getNextNonTextNodeIndex( node, index + 1 );
				Node node2 = node.getChildNodes().item( index );
				if ( ( numberClass.isAssignableFrom( MRational.class ) || numberClass.isAssignableFrom( MRational.class ) ) && node1.getNodeName().equalsIgnoreCase( "mn" )
						&& node2.getNodeName().equalsIgnoreCase( "mn" ) ) // all
					// number
					// nodes
					return new NumberOp( NumberFactory.parseMathMLNumber( numberClass, node ) );
				OpNode n1 = parseMathMLNode( numberClass, node1 );
				OpNode n2 = parseMathMLNode( numberClass, node2 );
				n2.setExponent( -1 * n2.getExponent() );
				return new MultOp( new OpNode[] { n1, n2 } );
			} else if ( node.getNodeName().equalsIgnoreCase( "mi" ) ) {// identifiers/variables
				// and
				// unedited
				// elements
				if ( node.getChildNodes().getLength() == 0 )// empty <mi/> means
					// empty
					// number/expression
					// -> set
					// edited-flag to
					// false!
					return new UneditedOp( numberClass );
				else {
					String nodeValue = node.getChildNodes().item( 0 ).getNodeValue().trim();
					if ( nodeValue.length() > 1 ) {
						OpNode resultNode = OpParser.getOperationTree( numberClass, nodeValue, false );
						if ( resultNode != null ) {
							return resultNode;
						}
					}
					return new VariableOp( numberClass, nodeValue );
				}
			} else if ( node.getNodeName().equalsIgnoreCase( "msup" ) ) {// power
				// node
				int index = 0;
				index = getNextNonTextNodeIndex( node, index );
				OpNode n1 = parseMathMLNode( numberClass, node.getChildNodes().item( index ) );
				index = getNextNonTextNodeIndex( node, index + 1 );
				OpNode n2 = parseMathMLNode( numberClass, node.getChildNodes().item( index ) );
				return new PowerOp( n1, n2 );
			} else if ( node.getNodeName().equalsIgnoreCase( "msqrt" ) ) {// 2-Nrt
				// node
				int index = 0;
				index = getNextNonTextNodeIndex( node, index );
				OpNode n1 = parseMathMLNode( numberClass, node.getChildNodes().item( index ) );
				return new NrtOp( n1, 2 );
			} else if ( node.getNodeName().equalsIgnoreCase( "mroot" ) ) {// Nrt
				// node
				int index = 0;
				index = getNextNonTextNodeIndex( node, index );
				OpNode n1 = parseMathMLNode( numberClass, node.getChildNodes().item( index ) );
				index = getNextNonTextNodeIndex( node, index + 1 );
				return new NrtOp( n1, ( int ) NumberFactory.parseMathMLNumber( numberClass, node.getChildNodes().item( index ) ).getDouble() );
			} else if ( node.getNodeName().equalsIgnoreCase( "mrow" ) ) {// subexpression
				// node
				/*
				 * 
				 * The only 2 implemented operations are addition and
				 * multiplication. The fields factor and exponent are used for
				 * substraction and division (value in these cases: -1). The
				 * precessing is done linearly through the expression. First
				 * paranthesises are checked and a vector of multiplication
				 * expressions is built. Every one of theses contains a vector
				 * of addition expressions.
				 * 
				 * Reading the expression AFTER an operation is done by caching
				 * the expression BEFORE the operation.
				 * 
				 * Debugging can be done by uncommenting the debug(String)
				 * method.
				 * 
				 */
				debug( XMLUtils.nodeToString( node, true ) );
				Node[] addOps = extractAddOps( node );
				OpNode[] toAddOps = new OpNode[addOps.length];
				for ( int i = 0; i < addOps.length; i++ ) {
					MNumber factor = NumberFactory.newInstance( numberClass, 1 );
					MNumber exponent = NumberFactory.newInstance( numberClass, 1 );
					Vector multOps = new Vector();
					String opStack = "*";
					String funcStack = null;
					for ( int j = 0; j < addOps[i].getChildNodes().getLength(); j++ ) {
						if ( addOps[i].getChildNodes().item( j ).getNodeName().equalsIgnoreCase( "mo" ) ) {// math.
							// operation
							// node
							String op = addOps[i].getChildNodes().item( j ).getChildNodes().item( 0 ).getNodeValue().trim();
							if ( op.equals( "(" ) ) {
								int nextIndex = findNextParanthesis( addOps[i], j + 1 );
								OpNode paranthesisNode = parseMathMLNode( numberClass, getParanthesisOp( addOps[i], j + 1, nextIndex ) );
								if ( funcStack != null ) {
									OpNode funcNode = getFuncNode( funcStack, paranthesisNode );
									multOps.add( funcNode );
									funcStack = null;
								} else
									multOps.add( paranthesisNode );
								j = nextIndex;
								continue;
							} else if ( op.equals( "*" ) || op.equals( "/" ) ) {
								opStack = op;
								continue;
							} else if ( op.equals( "+" ) || op.equals( "-" ) ) {
								if ( j == 0 ) {
									if ( op.equals( "-" ) ) factor.negate();
									continue;
								} else {
									System.err.println( "Invalid place for operation: " + op );
								}
							} else if ( isFunction( op ) ) {
								if ( funcStack == null )
									funcStack = op;
								else
									System.err.println( "Function stack not empty, cannot parse: " + op );
							} else
								System.err.println( "Invalid operation: " + op + "\nPosition: " + j + "\nNode: " + nodeToString( addOps[i], true ) );
						} else { // other than <mo>-Node
							if ( addOps[i].getChildNodes().item( j ).getNodeType() == Node.TEXT_NODE ) continue;
							OpNode n = parseMathMLNode( numberClass, addOps[i].getChildNodes().item( j ) );
							debug( "Parse node: " + n );
							if ( opStack.equals( "/" ) ) {
								n.setExponent( exponent.negated().getSign() );
							}
							multOps.add( n );
						}
					}
					OpNode[] toMultOps = new OpNode[multOps.size()];
					for ( int k = 0; k < toMultOps.length; k++ ) {
						toMultOps[k] = ( OpNode ) multOps.get( k );
					}
					if ( toMultOps.length == 1 )
						toAddOps[i] = toMultOps[0];
					else
						toAddOps[i] = new MultOp( toMultOps );
					if ( factor.getSign() < 0 ) toAddOps[i].negateFactor();
				}
				OpNode result = null;
				if ( toAddOps.length == 1 )
					result = toAddOps[0];
				else
					result = new AddOp( toAddOps );
				return result;
			}
			debug( "return null from parse node" );
			return null;
		} catch ( RuntimeException re ) {
			throw new XMLParsingException( node, re );
		}
	}

	private static Node[] extractAddOps( Node node ) {
		Vector addOps = new Vector();
		// TODO: first element could be <mo>-</mo>, in this case don't start at
		// 0???
		Node firstNode = getNextNonTextNode( node, 0 );
		if ( firstNode != null && firstNode.getNodeName().equalsIgnoreCase( "mo" ) && firstNode.getChildNodes().item( 0 ).getNodeValue().trim().equals( "-" ) ) {
			DocumentFragment frag = node.getOwnerDocument().createDocumentFragment();
			frag.appendChild( new MInteger( -1 ).getMathMLNode( node.getOwnerDocument() ) );
			Element minus = node.getOwnerDocument().createElementNS( MATHML_NAMESPACE, "mo" );
			minus.appendChild( node.getOwnerDocument().createTextNode( "*" ) );
			frag.appendChild( minus );
			node.replaceChild( frag, firstNode );
			debug( "Replace minus sign by -1" );
		}
		int secondAddOpIndex = findNextAddOp( node, 0 );
		if ( secondAddOpIndex == -1 ) {// only one add op
			debug( "only 1 add op" );
			return new Node[] { node };
		}
		Node firstAddOp = copyChildren( node, 0, secondAddOpIndex - 1 );
		if ( firstAddOp.getChildNodes().item( 0 ).getNodeName().equalsIgnoreCase( "mo" ) ) {
			String op = firstAddOp.getChildNodes().item( 0 ).getChildNodes().item( 0 ).getNodeValue().trim();
			if ( !( op.equals( "+" ) || op.equals( "-" ) ) ) { // if first add
				// op doesn't
				// start with
				// <mo>+</mo> or
				// <mo>-</mo>
				Element mo = node.getOwnerDocument().createElementNS( MATHML_NAMESPACE, "mo" );
				mo.appendChild( node.getOwnerDocument().createTextNode( "+" ) );
				firstAddOp.insertBefore( mo, firstAddOp.getFirstChild() );
			}
		} else {
			Element mo = node.getOwnerDocument().createElementNS( MATHML_NAMESPACE, "mo" );
			mo.appendChild( node.getOwnerDocument().createTextNode( "+" ) );
			firstAddOp.insertBefore( mo, firstAddOp.getFirstChild() );
		}
		addOps.add( firstAddOp );// also add first element in list
		for ( int i = secondAddOpIndex; i < node.getChildNodes().getLength(); i++ ) {
			if ( node.getChildNodes().item( i ).getNodeName().equalsIgnoreCase( "mo" ) ) {
				String op = node.getChildNodes().item( i ).getChildNodes().item( 0 ).getNodeValue().trim();

				if ( op.equals( "+" ) || op.equals( "-" ) ) {
					int next = findNextAddOp( node, i + 1 );
					if ( next == -1 ) {// last add op
						debug( "last add op" );
						Node children = copyChildren( node, i, node.getChildNodes().getLength() - 1 );
						addOps.add( children );
						break;
					} else {
						Node children = copyChildren( node, i, next - 1 );
						i = next - 1;
						addOps.add( children );
					}
				}
			}
		}
		Node[] result = new Node[addOps.size()];
		for ( int j = 0; j < result.length; j++ ) {
			result[j] = ( Node ) addOps.get( j );
		}
		return result;
	}

	/**
	 * Searches among the children of <code>node</code> for <mo>+</mo> or
	 * <mo>-</mo> at the index <code>index</code> and returns the index of
	 * the closing paranthesis. It jumps over paranthesises.
	 */
	private static int findNextAddOp( Node node, int index ) {
		int innerParanthesises = 0;
		int i;
		for ( i = index; i < node.getChildNodes().getLength(); i++ ) {
			Node n = node.getChildNodes().item( i );
			// jump over paranthesises
			if ( n.getNodeName().equalsIgnoreCase( "mo" ) && n.getChildNodes().item( 0 ).getNodeValue().trim().equals( "(" ) ) {
				innerParanthesises++;
				continue;
			}
			if ( n.getNodeName().equalsIgnoreCase( "mo" ) && n.getChildNodes().item( 0 ).getNodeValue().trim().equals( ")" ) ) {
				innerParanthesises--;
				continue;
			}
			if ( innerParanthesises == 0 && n.getNodeName().equalsIgnoreCase( "mo" )
					&& ( n.getChildNodes().item( 0 ).getNodeValue().trim().equals( "+" ) || n.getChildNodes().item( 0 ).getNodeValue().trim().equals( "-" ) ) ) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param parent
	 *            the parent node
	 * @param start
	 *            start index (inclusive)
	 * @param end
	 *            end index (inclusive)
	 * @return a <mrow> instance of {@link Node}
	 */
	private static Node copyChildren( Node parent, int start, int end ) {
		Element mrow = parent.getOwnerDocument().createElementNS( MATHML_NAMESPACE, "mrow" );
		for ( int j = start; j <= end; j++ ) {
			mrow.appendChild( parent.getChildNodes().item( j ).cloneNode( true ) );
		}
		return mrow;
	}

	private static boolean isFunction( String func ) {
		if ( func.equalsIgnoreCase( "sin" ) || func.equalsIgnoreCase( "cos" ) || func.equalsIgnoreCase( "tan" ) || func.equalsIgnoreCase( "cot" ) || func.equalsIgnoreCase( "sinh" )
				|| func.equalsIgnoreCase( "cosh" ) || func.equalsIgnoreCase( "tanh" ) || func.equalsIgnoreCase( "coth" ) || func.equalsIgnoreCase( "asin" ) || func.equalsIgnoreCase( "acos" )
				|| func.equalsIgnoreCase( "atan" ) || func.equalsIgnoreCase( "acot" ) || func.equalsIgnoreCase( "asinh" ) || func.equalsIgnoreCase( "acosh" ) || func.equalsIgnoreCase( "atanh" )
				|| func.equalsIgnoreCase( "acoth" ) || func.equalsIgnoreCase( "exp" ) || func.equalsIgnoreCase( "ln" ) || func.equalsIgnoreCase( "sqrt" ) || func.equalsIgnoreCase( "abs" )
				|| func.equalsIgnoreCase( "floor" ) || func.equalsIgnoreCase( "fac" ) || func.equalsIgnoreCase( "im" ) || func.equalsIgnoreCase( "re" ) || func.equalsIgnoreCase( "conj" )
				|| func.equalsIgnoreCase( "sign" ) || func.equalsIgnoreCase( "theta" ) ) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the appropriate operation node for the 4 basic operations +, -, *
	 * and / with given children. Note that the array will allways have the size
	 * 2.
	 */
	// private static OpNode getOpNode(String operation, OpNode[] children) {
	// debug("Putting op: " + operation);
	// OpNode result = null;
	// if(operation.equals("*")) { // mult operation
	// result = new MultOp(children);
	// } else if(operation.equals("+")) { // add operation
	// result = new AddOp(children);
	// } else if(operation.equals("-")) { // sub operation
	// OpNode[] modifiedChildren = {children[0],
	// ((OpNode)children[1].clone()).negateFactor()};
	// result = new AddOp(modifiedChildren);
	// } else if(operation.equals("/")) { // div operation
	// OpNode[] modifiedChildren = {children[0],
	// ((OpNode)children[1].clone()).negateExponent()};
	// result = new MultOp(modifiedChildren);
	// } else
	// System.err.println("No operation node could be found for operation " +
	// operation);
	// return result;
	// }
	/**
	 * Returns the appropriate operation node for the 4 basic operations +, -, *
	 * and / with given children. Note that the array will allways have the size
	 * 2.
	 */
	private static OpNode getFuncNode( String function, OpNode child ) {
		OpNode result = null;
		if ( function.equalsIgnoreCase( "sin" ) ) { // sin operation
			result = new SinOp( child );
		} else if ( function.equalsIgnoreCase( "cos" ) ) { // cos operation
			result = new CosOp( child );
		} else if ( function.equalsIgnoreCase( "tan" ) ) { // tan operation
			result = new TanOp( child );
		} else if ( function.equalsIgnoreCase( "cot" ) ) { // cot operation
			result = new CotOp( child );
		} else if ( function.equalsIgnoreCase( "sinh" ) ) { // sin operation
			result = new SinhOp( child );
		} else if ( function.equalsIgnoreCase( "cosh" ) ) { // cosh operation
			result = new CoshOp( child );
		} else if ( function.equalsIgnoreCase( "tanh" ) ) { // tan operation
			result = new TanhOp( child );
		} else if ( function.equalsIgnoreCase( "coth" ) ) { // coth operation
			result = new CothOp( child );
		} else if ( function.equalsIgnoreCase( "asin" ) ) { // asin operation
			result = new AsinOp( child );
		} else if ( function.equalsIgnoreCase( "acos" ) ) { // acos operation
			result = new AcosOp( child );
		} else if ( function.equalsIgnoreCase( "atan" ) ) { // atan operation
			result = new AtanOp( child );
		} else if ( function.equalsIgnoreCase( "acot" ) ) { // acot operation
			result = new AcotOp( child );
		} else if ( function.equalsIgnoreCase( "asinh" ) ) { // asinh
			// operation
			result = new AsinhOp( child );
		} else if ( function.equalsIgnoreCase( "acosh" ) ) { // acosh
			// operation
			result = new AcoshOp( child );
		} else if ( function.equalsIgnoreCase( "atanh" ) ) { // atanh
			// operation
			result = new AtanhOp( child );
		} else if ( function.equalsIgnoreCase( "acoth" ) ) { // acoth
			// operation
			result = new AcothOp( child );
		} else if ( function.equalsIgnoreCase( "exp" ) ) { // exp operation
			result = new ExpOp( child );
		} else if ( function.equalsIgnoreCase( "ln" ) ) { // ln operation
			result = new LnOp( child );
		} else if ( function.equalsIgnoreCase( "sqrt" ) ) { // sqrt operation
			result = new NrtOp( child, 2 );
		} else if ( function.equalsIgnoreCase( "abs" ) ) { // abs operation
			result = new AbsOp( child );
		} else if ( function.equalsIgnoreCase( "floor" ) ) { // floor
			// operation
			result = new FloorOp( child );
		} else if ( function.equalsIgnoreCase( "fac" ) ) { // fac operation
			result = new FacOp( child );
		} else if ( function.equalsIgnoreCase( "im" ) ) { // im operation
			result = new ImOp( child );
		} else if ( function.equalsIgnoreCase( "re" ) ) { // re operation
			result = new ReOp( child );
		} else if ( function.equalsIgnoreCase( "conj" ) ) { // conj //
			// operation
			result = new ConjOp( child );
		} else if ( function.equalsIgnoreCase( "sign" ) ) {
			result = new PiecewiseConstOp( child, new double[] { 0 }, new double[] { -1, 1 }, "sign" );
		} else if ( function.equalsIgnoreCase( "theta" ) ) {
			result = new PiecewiseConstOp( child, new double[] { 0 }, new double[] { 0, 1 }, "theta" );;
		} else
			System.err.println( "No function node could be found for function " + function );
		return result;
	}

	/**
	 * Searches among the children of <code>node</code> for <mo>)</mo> at the
	 * index <code>index</code> and returns the index of the closing
	 * paranthesis.
	 */
	private static int findNextParanthesis( Node node, int index ) {
		int innerParanthesises = 0;
		int i;
		for ( i = index; i < node.getChildNodes().getLength(); i++ ) {
			Node n = node.getChildNodes().item( i );
			if ( n.getNodeName().equalsIgnoreCase( "mo" ) && n.getChildNodes().item( 0 ).getNodeValue().trim().equals( "(" ) ) {
				innerParanthesises++;
				continue;
			}
			if ( n.getNodeName().equalsIgnoreCase( "mo" ) && n.getChildNodes().item( 0 ).getNodeValue().trim().equals( ")" ) ) {
				if ( innerParanthesises > 0 )
					innerParanthesises--;
				else
					break;
			}
		}
		return i;
	}

	/**
	 * Returns the children of <code>node</code> from the index
	 * <code>startIndex</code> to the index <code>endIndex-1</code>
	 * encapsulated in a <mrow> element.
	 */
	private static Node getParanthesisOp( Node node, int startIndex, int endIndex ) {
		Element mrow = node.getOwnerDocument().createElementNS( XMLUtils.MATHML_NAMESPACE, "mrow" );
		for ( int j = startIndex; j < endIndex; j++ ) {
			// System.out.println(XMLUtils.nodeToString(node.getChildNodes().item(j),
			// true));
			mrow.appendChild( node.getChildNodes().item( j ).cloneNode( true ) );
		}
		return mrow;
	}

	/**
	 * Returns the index of the next child of this node that is not a Text node,
	 * else returns <code>-1</code>.
	 */
	public static int getNextNonTextNodeIndex( Node n, int startIndex ) {
		int i;
		NodeList list = n.getChildNodes();
		for ( i = startIndex; i < list.getLength(); i++ ) {
			if ( list.item( i ).getNodeType() != Node.TEXT_NODE ) return i;
		}
		return -1;
	}

	/** Returns the next child of this node that is not a Text node. */
	public static Node getNextNonTextNode( Node n, int startIndex ) {
		if ( startIndex >= 0 && startIndex < n.getChildNodes().getLength() )
			return n.getChildNodes().item( getNextNonTextNodeIndex( n, startIndex ) );
		else
			return null;
	}

	/**
	 * Replaces all occurrences of <code>oldNode</code> in
	 * <code>masterNode</code> with <code>newNode</code>. Only nodes
	 * without children are replaced!
	 */
	public static void replaceNode( Node masterNode, Node oldNode, Node newNode ) {
		for ( int i = 0; i < masterNode.getChildNodes().getLength(); i++ ) {
			Node n = masterNode.getChildNodes().item( i );
			replaceNode( n, oldNode, newNode );
			if ( n.getNodeType() == Node.TEXT_NODE && oldNode.getNodeType() == Node.TEXT_NODE && n.getNodeValue().equals( oldNode.getNodeValue() ) ) {
				masterNode.insertBefore( newNode.cloneNode( true ), n );
				masterNode.removeChild( n );
			} else if ( n.getNodeType() != Node.TEXT_NODE && n.getNodeName().equals( oldNode.getNodeName() ) && !n.hasChildNodes() ) {
				masterNode.insertBefore( newNode.cloneNode( true ), n );
				masterNode.removeChild( n );
			}
		}
	}
	
	/**
	 * Returns the first child node of <code>parentNode</code> with the given node name or
	 * <code>null</code> if no such child node exists.
	 * @param parentNode a node
	 * @param childNodeName node name of child node
	 * @return a node or <code>null</code>
	 */
	public static Node getFirstChildNode(Node parentNode, String childNodeName) {
  	NodeList nodes = parentNode.getChildNodes();
  	for(int i = 0; i < nodes.getLength(); i++) {
  		Node n = nodes.item(i);
  		if(n.getNodeType() == Node.TEXT_NODE)
  			continue;
  		if(n.getNodeName().equals(childNodeName))
  			return n;
  	}
  	return null;
	}
	
	/**
	 * Returns an array of all child nodes of <code>parentNode</code> with the given node name or
	 * an empty list if no such child nodes exist. If the node name is <code>null</code> every
	 * non-text and non-comment node will be returned.
	 * 
	 * @param parentNode a node
	 * @param childNodeName node name of child nodes, may be <code>null</code>
	 * @return a list of nodes
	 */
	public static Node[] getChildNodes(Node parentNode, String childNodeName) {
		Vector children = new Vector();
  	NodeList nodes = parentNode.getChildNodes();
  	for(int i = 0; i < nodes.getLength(); i++) {
  		Node n = nodes.item(i);
  		if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
  			continue;
  		if(childNodeName == null || n.getNodeName().equals(childNodeName))
  			children.add(n);
  	}
  	Node[] result = new Node[children.size()];
  	children.toArray(result);
  	return result;
	}
	
	/**
	 * Returns the value of the named attribute from the given XML node or <code>null</code>
	 * if no such attribute exists.
	 * @param aNode a XML node
	 * @param attributeName the attribute's name
	 * @return the value of the named attribute or <code>null</code>
	 */
	public static String getAttribute(Node aNode, String attributeName) {
		NamedNodeMap attributes = aNode.getAttributes();
		if(attributes == null)
			return null;
		Node n = attributes.getNamedItem(attributeName);
		if(n == null)
			return null;
		return n.getNodeValue();
	}
	
	/**
	 * Creates and returns a new XML element node with the given name and namespace URI.
	 * The full element's name is constructed by the prefix followed by a colon and finally the real node name.
	 * If the prefix is null, the full name equals the node name.
	 * This method contains a workaround for older Java versions.
	 * 
	 * @param doc needed for creating the XML node
	 * @param namespaceURI the URI of the underlying namespace
	 * @param prefix the namespace prefix for this attribute (w/o ':'); may be null
	 * @param nodeName the node name w/o prefix and ':'
	 * @return a new XML node
	 * @see Document#createElementNS(java.lang.String, java.lang.String)
	 */
	public static Element createElementNS(Document doc, String namespaceURI, String prefix, String nodeName) {
		String fullName = (prefix == null) ? nodeName : prefix + ":" + nodeName;
		Element e = doc.createElementNS(namespaceURI, fullName);
		if(printXMLNamespaces) {
			String attrName = (prefix == null) ? "xmlns" : "xmlns:" + prefix;
			e.setAttribute(attrName, namespaceURI);
		}
		return e;
	}
	
	/**
	 * Adds the named attribute with the given namespace URI and value to the given element node.
	 * The full attribute name is constructed by the prefix followed by a colon and finally the real node name.
	 * If the prefix is null, the full name equals the node name.
	 * This method contains a workaround for older Java versions.
	 * 
	 * @param node the node the attribute will be added to
	 * @param namespaceURI the URI of the underlying namespace
	 * @param prefix the namespace prefix for this attribute (w/o ':'); may be null
	 * @param nodeName the node name w/o prefix and ':'
	 * @param value the attribute's value
	 * @see Element#setAttributeNS(java.lang.String, java.lang.String, java.lang.String)
	 */
	public static void setAttributeNS(Element node, String namespaceURI, String prefix, String nodeName, String value) {
		String fullName = (prefix == null) ? nodeName : prefix + ":" + nodeName;
		node.setAttributeNS(namespaceURI, fullName, value);
		if(printXMLNamespaces) {
			String attrName = (prefix == null) ? "xmlns" : "xmlns:" + prefix;
			node.setAttribute(attrName, namespaceURI);
		}
	}
	
	/**
   * Returns whether XML namespaces are omitted in string represenations by the XML classes.
   * This behaviour may occur in Java version 1.4.2.
   */
  public static boolean isOmittingXMLNamespaces() {
  	if(omitsXMLNamespaces == null) {
			try {
				Document doc = XMLUtils.createDocumentBuilder().newDocument();
				// create arbitrary XML node with concrete namespace URI
				DOMSource source = new DOMSource(doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mn"));
				// identity transform
				Transformer transformer = createDefaultTransformer();
				StringWriter writer = new StringWriter();
				// "copy" XML node to string representation
				transformer.transform( source, new StreamResult( writer ) );
				String xmlString = writer.toString();
				// test if substring "xmlns" is contained in string representation of XML node
				omitsXMLNamespaces =  new Boolean(xmlString.indexOf("xmlns") == -1);
			} catch ( Exception e ) {
				throw new XMLParsingException("XML configuration error", e);
			}
  	}
		return omitsXMLNamespaces.booleanValue();
  }
}
