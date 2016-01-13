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

package net.mumie.mathletfactory.display.noc.rel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.noc.rel.node.RelationColumnPanel;
import net.mumie.mathletfactory.display.noc.rel.node.RelationComponent;
import net.mumie.mathletfactory.display.noc.rel.node.RelationContainer;
import net.mumie.mathletfactory.display.noc.rel.node.RelationRowPanel;
import net.mumie.mathletfactory.display.noc.rel.node.SimpleRelationPanel;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.noc.RelationTransformer;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 *  This class implements the interactive presentation of a complex relation 
 *  of arbitrary number  based content. It acts as a view for a single 
 *  {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation} object.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

class RelationDisplay extends AlignablePanel implements RelationContainer {

  private final static MumieLogger LOGGER = MumieLogger.getLogger(RelationDisplay.class);
  private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline"); 

  private RelationComponent m_root;
  
  private MMObjectIF m_master;

  /**
   *  Constructs a RelationPanel for a given 
   *  {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation}.
   */
  public RelationDisplay(MMObjectIF master, RelationTransformer transformer) {
  	super();
  	m_master = master;
//  	setFillHeight(true);
  	
    if(LOGGER.isActiveCategory(DRAW_OUTLINE))
    	setBorder(new LineBorder(Color.ORANGE));
    Relation relation = (Relation) master;
    setRelation(relation);
  }
  
  public void setRelation(Relation relation){
  	for(int i = 0; i < getMouseListeners().length; i++) {
  		recursivelyRemoveMouseListeners(m_root, getMouseListeners()[i]);
  	}
    removeAll();
    if(!relation.isAll() && !relation.isNull()) {
      m_root = addPanel(relation.getRelRoot(), this);
      m_root.setFont(getFont());
//      ((Component)m_root).setLocation(0,0);
    }
  	for(int i = 0; i < getMouseListeners().length; i++) {
  		recursivelyAddMouseListeners(m_root, getMouseListeners()[i]);
  	}
    revalidate();
    repaint();
  }
  
  /**
   * Recursively adds a view for the given <code>relNode</code> and inserts it
   * into the container <code>parent</code>.
   */
  private RelationComponent addPanel(RelNode relNode, RelationContainer parent) {
    RelationComponent newPanel = null;
    if (relNode instanceof OrRel) {
      OrRel orRel = (OrRel) relNode;
      newPanel = new RelationColumnPanel(orRel);
      for(int i=0; i < orRel.getChildren().length;i++)
        addPanel(orRel.getChildren()[i], (RelationContainer) newPanel);
    }
    if (relNode instanceof AndRel) {
      AndRel andRel = (AndRel) relNode;
      newPanel = new RelationRowPanel(andRel);
      for(int i=0; i < andRel.getChildren().length;i++)
        addPanel(andRel.getChildren()[i], (RelationContainer) newPanel);
    }
    if (relNode instanceof SimpleRel)
      newPanel = new SimpleRelationPanel((SimpleRel)relNode);
    //((JPanel) newPanel).setBorder(new LineBorder(Color.BLACK));
    ((RelationContainer) parent).addRelationComponent(newPanel);
    return newPanel;
  }
  
  public void addRelationComponent(RelationComponent relationComponent) {
    add((JPanel) relationComponent);
  }
  
  /** Returns the root node of the master relation object. */
  public RelNode getMasterRelNode(){
    return ((Relation)m_master).getRelRoot();
  }
  
  public RelationComponent[] getRelationRows(){
    
    // The relation is in normal form that means we have either an OR relation 
    // as root node (which results in a RelationColumnPanl as root container) or
    // there is only one row, which means that this object can be returned as
    // single element of the relation rows
    
    if(m_root instanceof RelationColumnPanel){
    
      // return the children of m_root, which is the RelationColumn container
      return (RelationComponent[])((RelationContainer)m_root).getChildren();  
    }
    
    return new RelationComponent[]{this};
  }
  
  public RelationComponent[] getChildren(){
    return new RelationComponent[] {m_root};
  }
  
  public void addPropertyChangeListener(PropertyChangeListener l){
    if(m_root != null)
      m_root.addPropertyChangeListener(l);
  }
  
  /**
   * Propagates the given <code>MouseListener</code> to all descendents in the
   * {@link net.mumie.mathletfactory.display.noc.rel.node.RelationComponent}
   * tree.
   *
   * @see java.awt.Component#addMouseListener(MouseListener)
   */ 
  public void addMouseListener(MouseListener l){
    super.addMouseListener(l);
    if(m_root != null)
      recursivelyAddMouseListeners(m_root, l);
  }
    
  /** Adds <code>l</code> to <code>comp</code> and its children if any. */
  private void recursivelyAddMouseListeners(RelationComponent comp, MouseListener l){
    ((JComponent)comp).addMouseListener(l);
    if(comp instanceof RelationContainer){
      RelationComponent[] children = ((RelationContainer)comp).getChildren();
      for(int i=0;i<children.length;i++)
        recursivelyAddMouseListeners(children[i], l);
    }
  }
  
  /** Adds <code>l</code> to <code>comp</code> and its children if any. */
  private void recursivelyRemoveMouseListeners(RelationComponent comp, MouseListener l){
    ((JComponent)comp).removeMouseListener(l);
    if(comp instanceof RelationContainer){
      RelationComponent[] children = ((RelationContainer)comp).getChildren();
      for(int i=0;i<children.length;i++)
      	recursivelyRemoveMouseListeners(children[i], l);
    }
  }
  
  public void setFont(Font font){
    super.setFont(font);
    if(m_root != null)
      m_root.setFont(font);
  }

  /**
   * Sets the color of the font.
   */
  public void setForeground(Color color){
    super.setForeground(color);
    if(m_root != null)
      ((JPanel)m_root).setForeground(color);
  }
  
  /** 
   * Sets the given display format for decimal (i.e. MDouble) numbers. 
   * Otherwise the format using {@link net.mumie.mathletfactory.display.noc.op.node.NumberView#DEFAULT_PATTERN} 
   * is used.
   */
  public void setDecimalFormat(DecimalFormat format){
    m_root.setDecimalFormat(format);
  }

  /** 
   * Returns the display format for decimal (i.e. MDouble) numbers. 
   */
  public DecimalFormat getDecimalFormat(){
    return m_root.getDecimalFormat();
  }
}
