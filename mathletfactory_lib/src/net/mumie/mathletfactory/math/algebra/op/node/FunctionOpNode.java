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

package net.mumie.mathletfactory.math.algebra.op.node;

import java.text.DecimalFormat;

import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 *  Represents the abstract object for functions in the Op-Scheme
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class FunctionOpNode extends OpNode
{
  public FunctionOpNode(Class entryClass){
    super(entryClass);
  }

  public FunctionOpNode(OpNode child){
    super(child);
  }

  /** Returns the inverse function of this node. */
  public abstract OpNode getInverseOp(OpNode newChild);

  /**
   * Simply returns the inverse operation with {@link #REPLACEMENT_IDENTIFIER}
   * as child.
   *
   * @see net.mumie.mathletfactory.math.algebra.op.node.OpNode#solveStepFor
   */
  public OpNode[] solveStepFor(String identifier){
    //if(m_children[0].getChildrenWithVariableCount(identifier) > 0)
    //  return null;
    return new OpNode[]{getInverseOp(new VariableOp(m_numberClass,REPLACEMENT_IDENTIFIER))};
  }

  /**
   * Simply returns the inverse operation with {@link #REPLACEMENT_IDENTIFIER}
   * as child.
   */
  public OpNode simpleSolveStepFor(String identifier){
    return getInverseOp(new VariableOp(m_numberClass,REPLACEMENT_IDENTIFIER));
  }

  public String toString(int printFlags, DecimalFormat format){
    return printFactorAndExponent(nodeToString()+"("+m_children[0].toString(format)+")", printFlags, format);
  }

  public String nodeToContentMathML(){
    return "<apply> <"+nodeToString()+"/> "+m_children[0].toContentMathML()+" </apply>";
  }

  public int getMinChildrenNr(){
    return 1;
  }

  public int getMaxChildrenNr(){
    return 1;
  }

  public Node getMathMLNode(Document doc) {
    DocumentFragment fragment = doc.createDocumentFragment();
    
    Node node, function;
	
	function = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
	
	node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
	node.appendChild(doc.createTextNode(nodeToString()));
	function.appendChild(node);
	node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
	node.appendChild(doc.createTextNode("("));
	function.appendChild(node);
	function.appendChild(m_children[0].getMathMLNode(doc));
	node = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
	node.appendChild(doc.createTextNode(")"));
	function.appendChild(node);
	
	fragment.appendChild(function);
	return writeFactorAndExponent(this, fragment);
  }
}

