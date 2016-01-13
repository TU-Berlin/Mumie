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
 * This class represents a <code>subtask</code> node inside the <code>marking</code>
 * part of a corrector written datasheet.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class SubtaskNode {
  private Node m_subtaskNode;
  private SubtaskAnswerNode m_answerNode;
  private SubtaskSolutionNode m_solutionNode;
  private SubtaskExplanationNode m_explanationNode;

  /** Constructs a new "subtask" node for this subtask containing an answer and a solution node. */
  public SubtaskNode(Node parentNode) {
    m_subtaskNode = parentNode.getOwnerDocument().createElement("subtask");
    m_answerNode = new SubtaskAnswerNode(m_subtaskNode);
    m_solutionNode = new SubtaskSolutionNode(m_subtaskNode);
    m_explanationNode = new SubtaskExplanationNode(m_subtaskNode);
    parentNode.appendChild(m_subtaskNode);
  }

  /** Returns the answer node for this subtask. */
  public SubtaskAnswerNode getAnswerNode() {
    return m_answerNode;
  }

  /** Returns the solution node for this subtask. */
  public SubtaskSolutionNode getSolutionNode() {
    return m_solutionNode;
  }
  
  /** Returns the explanation node for this subtask. */
  public SubtaskExplanationNode getExplanationNode() {
    return m_explanationNode;
  }
}
