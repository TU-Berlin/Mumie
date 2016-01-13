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

package net.mumie.mathletfactory.display.noc.rel.node;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.math.algebra.rel.node.ComplexRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;

/**
 * This class serves as base for views of 
 * {@link net.mumie.mathletfactory.algebra.rel.node.ComplexRel Complex Relations}.
 * 
 * @author Paehler, Gronau
 * @mm.docstatus finished
 */

public abstract class ComplexRelationContainer extends AlignablePanel implements RelationContainer {

  /** The children of this container. */
  protected RelationComponent[] m_children;
  
  /** The box in which the child nodes are - either vertically or horizontally aligned. */
  private JComponent m_container;
  
  /** The model of this view. */
  protected ComplexRel m_master;

  /** Returns {@link #m_master}, the model for this view. */
  public RelNode getMasterRelNode(){
    return m_master;
  }
  
  /** 
   * Called by subclasses. Decides, whether a horizontal or vertical layout is used. 
   * @param either {@link javax.swing.BoxLayout.X_AXIS} or {@link javax.swing.BoxLayout.Y_AXIS}. 
   */
  public ComplexRelationContainer(int layout) {
    super();
    if(layout == BoxLayout.X_AXIS) {
    	m_container = this;
    } else {
    	m_container = new Box(layout);
      add(m_container);
    }
  }

  public void addRelationComponent(RelationComponent relationComponent) {
  	getRelationContainer().add((JPanel) relationComponent);
  }
  
  protected void addChild(RelationComponent child){
    if(m_children == null){
      m_children = new RelationComponent[]{ child};
      return;
    }
    RelationComponent[] tmp;
    tmp = new RelationComponent[m_children.length+1];
    System.arraycopy(m_children, 0, tmp, 0, m_children.length);
    tmp[m_children.length] = child;
    m_children = tmp; 
  }
  
  /** Returns the children of this container in the relation component tree. */
  public RelationComponent[] getChildren(){
    return m_children;
  }
  
  /**
   * Returns the container component containing the relation panels.
   */
  public JComponent getRelationContainer() {
  	return m_container;
  }
  
  /**
   *  Propagates the font to the children. 
   */
  public void setFont(Font font){
    super.setFont(font);
    if(getRelationContainer() == null)
      return;
    Component[] comps = getRelationContainer().getComponents();
    if(comps != null)
      for(int i=0;i<comps.length;i++)
        comps[i].setFont(font);
  }

  /**
   * Sets the color of the font.
   */
  public void setForeground(Color color){
    super.setForeground(color);
    if(m_children != null)
      for(int i=0;i<m_children.length;i++)
        ((JPanel)m_children[i]).setForeground(color);
  }
  
  /** For debugging purposes, outputs the string representation of the master and the dimensions. */
  public String toString(){
    return (m_master != null ? m_master.toString() : "") + ", " + getSize();
  }
  
  public void setDecimalFormat(DecimalFormat format){
    for(int i=0; i<m_children.length;i++)
      m_children[i].setDecimalFormat(format);
  }
  
  /** 
   * Returns the display format for decimal (i.e. MDouble) numbers. 
   */
  public DecimalFormat getDecimalFormat(){
    return m_children[0].getDecimalFormat();
  }
}
