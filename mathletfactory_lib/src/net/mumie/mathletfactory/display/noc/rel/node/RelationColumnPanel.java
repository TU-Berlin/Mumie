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

import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.display.noc.symbol.AttributedStringLabel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;

/**
 *  This class represents the view of an {@link net.mumie.mathletfactory.algebra.rel.node.OrRel}
 *  relation node by arranging the child nodes vertically and placing an OR sign between them.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

public class RelationColumnPanel extends ComplexRelationContainer {
  
  private AttributedStringLabel m_orLabel;
  
  /** Initializes the vertical alignment. */
  public RelationColumnPanel(OrRel master) {
    super(BoxLayout.Y_AXIS);
    m_master = master;
    m_orLabel = new AttributedStringLabel("\u2228");
  }
  
  public double getOffset(){
    return 0;
  }
  
  public double getAscent(){
    return getHeight()/2;
  }
  
  public double getDescent(){
    return getHeight()/2;
  }
  
  /** Empty implementation. */
  public void setAlignedWith(Alignable alignWith){
  }
  
  /**
   * Adds the component and places an OR sign before it, if this is 2nd or 
   * more relation component to be added.
   * @see net.mumie.mathletfactory.display.noc.rel.node.RelationContainer#addRelationComponent(RelationComponent)
   */
  public void addRelationComponent(RelationComponent relationComponent) {
    if(relationComponent == null)
      throw new IllegalArgumentException("relation component is null!");
    addChild(relationComponent);
    if (getRelationContainer().getComponentCount() > 0){
    	getRelationContainer().add(m_orLabel);
    }
    getRelationContainer().add((JPanel) relationComponent);
  }
  
  public void setFont(Font font){
    super.setFont(font);
    if(m_orLabel == null)
      return;
    m_orLabel.setFont(font);
  } 
}
