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

package net.mumie.mathletfactory.util.text;

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.ConjOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class offers methods for transforming operational MathML into presentational MathML used for rendering
 * e.g. in web browsers.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MathMLConverter {

	/**
	 * Returns the presentation markup of the given MathML capable object.
	 *  
	 * @param doc the DOM document to use for creating XML nodes
	 * @param mathmlObject a MathML capable object
	 */
	public static Node toPresentationMarkup(Document doc, MathMLSerializable mathmlObject) {
		Node mathMLNode = mathmlObject.getMathMLNode(doc);
		if(mathmlObject instanceof Operation) {
			OpNode rootNode = ((Operation) mathmlObject).getOpRoot();
			Node node = null;
			// change "abs" nodes
			if(rootNode instanceof AbsOp || rootNode.getFirstDescendantOfType(AbsOp.class) != null) {
				// replace <mrow><mo>abs</mo><mo>(</mo> ... <mo>)</mo></mrow>
				// with    <mrow><mo>|</mo> ... <mo>|</mo></mrow>
				while((node = findFirstNode(mathMLNode, "mo", "abs")) != null) {
					Node mrowNode = node.getParentNode();
					mrowNode.removeChild(node);
					Node newNode = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
					newNode.appendChild(doc.createTextNode("|"));
					mrowNode.replaceChild(newNode, findFirstNode(mrowNode, "mo", "("));
					mrowNode.replaceChild(newNode.cloneNode(true), findLastNode(mrowNode, "mo", ")"));
				}
			}
			// change "conj" nodes
			if(rootNode instanceof ConjOp || rootNode.getFirstDescendantOfType(ConjOp.class) != null) {
				// replace <mrow><mo>conj</mo><mo>(</mo> ... <mo>)</mo></mrow>
				// with    <mrow><mover><mrow> ... </mrow><mo>-</mo></mover></mrow>
				while((node = findFirstNode(mathMLNode, "mo", "conj")) != null) {
					Node mrowNode = node.getParentNode();
					mrowNode.removeChild(node);
					mrowNode.removeChild(findFirstNode(mrowNode, "mo", "("));
					mrowNode.removeChild(findLastNode(mrowNode, "mo", ")"));
					Node fragment = mrowNode.getParentNode();
					fragment.removeChild(mrowNode);
					Node moverNode = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mover");
					moverNode.appendChild(mrowNode);
					Node moNode = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
					moNode.appendChild(doc.createTextNode("_")); // only underscore character will be "stretched"
					moverNode.appendChild(moNode);
					fragment.appendChild(moverNode);
				}
			}
		}
		return mathMLNode;
	}
	
	/**
	 * Searches recursively for the first XML node with the specified name and node value, starting at the given parent node
	 * and returns it or <code>null</code> if no such node is found.
	 * 
	 * @return a node with the specified name or <code>null</code>
	 */
	public static Node findFirstNode(Node parent, String name, String value) {
		return findNode(parent, name, value, FORWARD_DIRECTION);
	}
	
	/**
	 * Searches recursively for the last XML node with the specified name and node value, starting at the given parent node
	 * and returns it or <code>null</code> if no such node is found.
	 * 
	 * @return a node with the specified name or <code>null</code>
	 */
	public static Node findLastNode(Node parent, String name, String value) {
		return findNode(parent, name, value, BACKWARD_DIRECTION);
	}
	
	/**
	 * Searches recursively for a XML node with the specified name and node value, starting at the given parent node
	 * and returns it or <code>null</code> if no such node is found.
	 * 
	 * @return a node with the specified name or <code>null</code>
	 */
	private static Node findNode(Node parent, String name, String value, int direction) {
		if(parent.getNodeType() == Node.TEXT_NODE)
			return null;
		NodeList children = parent.getChildNodes();
		if(name.equals(parent.getNodeName())) {
			if(children.getLength() == 1) {
				Node firstChild = children.item(0);
				if(firstChild.getNodeType() == Node.TEXT_NODE && value.equals(firstChild.getNodeValue()))
					return parent;
			}
		}
		for(int i = startLoop(children, direction); continueLoop(children, i, direction); i = incrementLoop(i, direction)) {
			// search recursively
			Node child = findNode(children.item(i), name, value, direction);
			if(child != null)
				return child;
		}
		return null;
	}
	
	private final static int FORWARD_DIRECTION = 1;
	private final static int BACKWARD_DIRECTION = -1;
	
	private static int startLoop(NodeList children, int direction) {
		if(direction == FORWARD_DIRECTION)
			return 0;
		else // BACKWARD_DIRECTION
			return children.getLength() - 1;
	}
	
	private static boolean continueLoop(NodeList children, int i, int direction) {
		if(direction == FORWARD_DIRECTION)
			return i < children.getLength();
		else // BACKWARD_DIRECTION
			return i >= 0;
	}
	
	private static int incrementLoop(int i, int direction) {
		if(direction == FORWARD_DIRECTION)
			return i + 1;
		else // BACKWARD_DIRECTION
			return i - 1;
	}
}
