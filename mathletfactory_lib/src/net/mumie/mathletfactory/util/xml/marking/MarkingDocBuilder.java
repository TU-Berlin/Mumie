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

import java.util.Vector;

import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * This class is used to generate content for the marking part of a datasheet.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MarkingDocBuilder {

  public final static String USER_MARKING_PATH = "user/marking";

  private Document m_doc;
  private CommonExplanationNode m_commonExplanationNode;
  private Node m_markingNode;
  private Node m_subtasksNode;
  private Vector m_subtaskNodes = new Vector();

  /**
   *
   *@deprecated replaced by {@link #MarkingDocBuilder(Document)}
   */
  public MarkingDocBuilder() {
    this(XMLUtils.getDefaultDocument());
  }

  /**
   * Creates a new instance of a marking builder.
   * @param doc an instance of the document the marking-nodes will be created from
   */
  public MarkingDocBuilder(Document doc) {
    m_doc = doc;
    resetAllSubtasks();
  }

  /**
   * Adds a new <code>subtask</code>-node for a new subtask's marking content
   * @return an instance of a subtask node appended to the list of subtask nodes
   */
  public SubtaskNode addSubtaskNode() {
    SubtaskNode n = new SubtaskNode(m_subtasksNode);
    m_subtaskNodes.add(n);
    return n;
  }

  /**
   * Adds a <code>commonexpl</code>-node if it does not exists yet
   * @return an instance of a common explanation node
   */
  public CommonExplanationNode addCommonExplanationNode() {
  	if(m_commonExplanationNode != null)
  		return m_commonExplanationNode;
    m_commonExplanationNode = new CommonExplanationNode(m_markingNode);
    return m_commonExplanationNode;
  }
  
  /**
   * Returns the <code>subtask</code>-node with the given index for the subtask's marking content.
   * If this node does not exist yet, it will be created.
   * @param index the index of the <code>subtask</code>-node, starting with 1
   * @return an instance of a subtask node appended to the list of subtask nodes
   */
  public SubtaskNode getSubtaskNode(int index) {
    if(index < 1)
      throw new IllegalArgumentException("Subtask index must be greater or equal to 1 !");
    while(index - 1 >= m_subtaskNodes.size()) { // create new subtask node
      addSubtaskNode();
    }
    return (SubtaskNode) m_subtaskNodes.get(index - 1);
  }

  /**
   * Returns the root node (the <code>marking</code>-node itself).
   */
  public Node getMarkingNode() {
    return m_markingNode;
  }

  /**
   * Resets the marking content for all subtasks and deletes the list
   * of subtask nodes.
   */
  public void resetAllSubtasks() {
    m_markingNode = m_doc.createElementNS(XMLUtils.MARKING_NAMESPACE, "marking");
    m_subtasksNode = m_doc.createElement("subtasks");
    m_markingNode.appendChild(m_subtasksNode);
    m_subtaskNodes.clear();
  }
}
