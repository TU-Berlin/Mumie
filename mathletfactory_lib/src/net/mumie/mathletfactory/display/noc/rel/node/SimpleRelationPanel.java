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
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.border.LineBorder;

import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.noc.op.OpViewMapper;
import net.mumie.mathletfactory.display.noc.op.OperationDisplay;
import net.mumie.mathletfactory.display.noc.symbol.AttributedStringLabel;
import net.mumie.mathletfactory.display.noc.util.ExpressionPopupMenu;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 *  This class implements the interactive presentation of a simple relation of
 *  arbitrary (at this moment number  based) content. It acts as a view (and
 *  partially as controller @see OperationPopUpMenu) for a single
 *  {@link net.mumie.mathletfactory.mmobject.algebra.MMSimpleRelation} object.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */
public class SimpleRelationPanel extends AlignablePanel implements RelationComponent {

  private final static MumieLogger LOGGER = MumieLogger.getLogger(SimpleRelationPanel.class);
  private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline"); 

  /**
   *  The graphical view component of the left and right hand side of the
   *  simple relation.
   */
  protected OperationDisplay m_leftHandSide, m_rightHandSide;

  /** the graphical representation of the relation sign. */
  private AttributedStringLabel m_symLabel;

  /** the master {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel} node. */
  private SimpleRel m_master;
    
  /** Constructs a panel from a {@link net.mumie.mathletfactory.mmobject.algebra.MMSimpleRelation}. */
  public SimpleRelationPanel(SimpleRel master) {
  	super(2);
    setRelation(master);
    
    if(LOGGER.isActiveCategory(DRAW_OUTLINE))
    	setBorder(new LineBorder(Color.BLACK));
    add(Box.createGlue());
    add(m_leftHandSide);
    add(m_symLabel);
    add(m_rightHandSide);
    add(Box.createGlue());
    new ExpressionPopupMenu(this);
  }
  
  /**
   *  Sets the "master", the model of this view.
   */
  public void setRelation(SimpleRel master) {
    m_master = master;
    ComponentListener compL = new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        SimpleRelationPanel.this.revalidate();
        SimpleRelationPanel.this.repaint();
      }
    };
    ContainerObjectTransformer dummy = new ContainerObjectTransformer(){ public void render(){}};
    m_leftHandSide = new OperationDisplay(getFont());
    m_leftHandSide.setOpViewRoot(OpViewMapper.getViewFor(master.getLeftHandSide().getOpRoot()));
    m_leftHandSide.setAlignment(OperationDisplay.ALIGN_RIGHT);
    m_leftHandSide.addComponentListener(compL);

    m_rightHandSide = new OperationDisplay(getFont());
    m_rightHandSide.setOpViewRoot(OpViewMapper.getViewFor(master.getRightHandSide().getOpRoot()));
    m_rightHandSide.setAlignment(OperationDisplay.ALIGN_LEFT);
    m_rightHandSide.addComponentListener(compL);
    updateRelationSign();
  }

  /** Call needed, when the master has changed. */
  private void updateRelationSign() {
    String sign = getRelationSign();
    if (sign.equals("!="))
      m_symLabel = new AttributedStringLabel("\u2260");
    if (sign.equals("<="))
      m_symLabel = new AttributedStringLabel( "\u2264");
    if (sign.equals(">="))
      m_symLabel = new AttributedStringLabel( "\u2265");
    if (sign.equals("=") || sign.equals(">") || sign.equals("<"))
      m_symLabel = new AttributedStringLabel(getRelationSign());
    m_symLabel.setFont(getFont());
  }

  /**
   *  Returns the relation sign of the simple relation displayed. Value is
   *  one of the constants defined in 
   *  {@link net.mumie.mathletfactory.algebra.rel.Relation}.
   */
  public String getRelationSign() {
    return m_master.getRelationSign();
  }
  
  /**
   *  Propagates the given mouse listener to children.
   */
  public void addMouseListener(MouseListener l) {
    super.addMouseListener(l);
    m_leftHandSide.addMouseListener(l);
    m_rightHandSide.addMouseListener(l);
  }
  
  /** Sets the font for the rendered expressions. */
  public void setFont(Font font) {
    super.setFont(font);
    if (m_leftHandSide != null){
      m_leftHandSide.setFont(font);
      m_leftHandSide.revalidate();
    }
    if (m_rightHandSide != null){
      m_rightHandSide.setFont(font);
      m_rightHandSide.revalidate();
    }
    if (m_symLabel != null) {
      m_symLabel.revalidate();
    }
    repaint();
  }

  /**
   * Sets the color of the font.
   */
  public void setForeground(Color color){
    super.setForeground(color);
    if(m_leftHandSide != null)
      m_leftHandSide.setForeground(color);
    if(m_rightHandSide != null)
      m_rightHandSide.setForeground(color);
    if(m_symLabel != null)
    	m_symLabel.setForeground(getForeground());
  }
      
  /**
   * Returns the simple relation that is displayed by this panel. 
   * @see net.mumie.mathletfactory.display.noc.rel.node.RelationComponent#getMasterRelNode()
   */
  public RelNode getMasterRelNode(){
    return m_master;
  }
  
  /** For debugging purposes, outputs the string representation of the master and the dimensions. */
  public String toString(){
    return (m_master != null ? m_master.toString() : "") + ", " + getSize() + " baseline: "+getBaseline();
  }
  
  public void setDecimalFormat(DecimalFormat format){
    m_leftHandSide.setDecimalFormat(format);
    m_rightHandSide.setDecimalFormat(format);
  }
  
  /** 
   * Returns the display format for decimal (i.e. MDouble) numbers. 
   */
  public DecimalFormat getDecimalFormat(){
    return m_leftHandSide.getDecimalFormat();
  }
}
