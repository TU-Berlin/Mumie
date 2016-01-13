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

package net.mumie.mathletfactory.util.xml.marking;

import net.mumie.mathletfactory.math.util.MString;
import net.mumie.mathletfactory.util.text.MathMLConverter;
import net.mumie.mathletfactory.util.text.TeXConverter;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents a subtask sub-node inside the <code>marking</code>
 * part of a corrector written datasheet.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class SubtaskSubNode {

  protected Element m_xmlNode;
  protected Document m_doc;

  /**
   * Creates a new subtask sub-node
   * @param parentNode the parent node
   */
  public SubtaskSubNode(String nodeName, Node parentNode) {
    m_doc = parentNode.getOwnerDocument();
    m_xmlNode = m_doc.createElement(nodeName);
    m_xmlNode.appendChild(m_doc.createTextNode("\n  "));
    parentNode.appendChild(m_doc.createTextNode("\n"));
    parentNode.appendChild(m_xmlNode);
    parentNode.appendChild(m_doc.createTextNode("\n"));
  }
  
  /**
   * Sets the score for this subtask answer.
   * @param score a number between 0 and 1 (both inclusive)
   */
  public void setScore(double score) {
    m_xmlNode.setAttribute("score", Double.toString(score));
  }
  
  /**
   * Adds a <code>math</code> node containing the object's content
   * as MathML-Node. 
   */
  public void addMathObject(MathMLSerializable obj) {
  	addMathObject(null, obj, null);
  }
  
  /**
   * Adds the content of multiple objects to a single line.
   */
  public void addMathObjects(MathMLSerializable[] objects) {
    Element parElement = m_doc.createElement("par");
    for(int o = 0; o < objects.length; o++) {
    	if(objects[o] == null) {
    		continue;
    	} else if(objects[o] instanceof MString) {
    		parElement.appendChild(m_doc.createTextNode(TeXConverter.toUnicode(((MString)objects[o]).getValue())));
    	} else {
	      Element mathElement = m_doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "math");
	      mathElement.setAttribute("mode", "inline");
	    	mathElement.appendChild(MathMLConverter.toPresentationMarkup(m_doc, objects[o]));
	      parElement.appendChild(mathElement);
    	}
    }
    m_xmlNode.appendChild(parElement);
  }
  
  /**
   * Adds a <code>math</code> node containing the label and the object's content
   * as MathML-Node. 
   */
  public void addMathObject(String label, MathMLSerializable obj) {
  	addMathObject(label, obj, null);
  }
  
  /**
   * Adds a <code>math</code> node containing the first label, the object's content and 
   * the second label as MathML-Node. 
   */
  public void addMathObject(String preLabel, MathMLSerializable obj, String postLabel) {
  	MathMLSerializable s1 = (preLabel == null ? null : new MString(preLabel));
  	MathMLSerializable s2 = (postLabel == null ? null : new MString(postLabel));
  	addMathObjects(new MathMLSerializable[] { s1, obj, s2});
  }

  /**
   * Adds a <code>par</code> node which can contain a text paragraph.
   * @param text the text to be displayed
   * @return a <code>par</code> node for text
   */
  public Node addParagraphNode(String text) {
    Element parElement = m_doc.createElement("par");
    parElement.appendChild(m_doc.createTextNode(TeXConverter.toUnicode(text)));
    m_xmlNode.appendChild(parElement);
    return parElement;
  }

  /**
   * Adds a <code>mark</code> node which contains a highlighted text.
   * @param text the text to be displayed
   * @return a <code>mark</code> node
   */
  public Node addMarkNode(String text) {
    Element markElement = m_doc.createElement("mark");
    markElement.appendChild(m_doc.createTextNode(TeXConverter.toUnicode(text)));
    m_xmlNode.appendChild(markElement);
    return markElement;
  }
}
