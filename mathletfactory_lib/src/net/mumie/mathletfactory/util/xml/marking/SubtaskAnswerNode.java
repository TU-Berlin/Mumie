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

import org.w3c.dom.Node;

/**
 * This class represents an <code>answer</code> node inside the <code>marking</code>
 * part of a corrector written datasheet.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class SubtaskAnswerNode extends SubtaskSubNode {

  /**
   * Creates a new <code>answer</code> node
   * @param parentNode the parent node
   */
  public SubtaskAnswerNode(Node parentNode) {
  	super("answer", parentNode);
  }

  /**
   * Sets the score for this subtask answer.
   * @param score a number between 0 and 1 (both inclusive)
   */
  public void setScore(double score) {
    m_xmlNode.setAttribute("score", Double.toString(score));
  }
}
