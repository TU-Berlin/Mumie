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

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.mumie.mathletfactory.display.noc.symbol.AttributedStringLabel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 *  This class represents the view of an {@link net.mumie.mathletfactory.algebra.rel.node.AndRel}
 *  relation node by arranging the child nodes horizontally and placing an AND sign between them.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */
 
public class RelationRowPanel extends ComplexRelationContainer {
  
  private final static MumieLogger LOGGER = MumieLogger.getLogger(SimpleRelationPanel.class);
  private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline"); 

  /** Initializes the horizontal alignment. */
  public RelationRowPanel(AndRel master) {
    super(BoxLayout.X_AXIS);
    if(LOGGER.isActiveCategory(DRAW_OUTLINE))
    	setBorder(new LineBorder(Color.BLACK));
    m_master = master;
  }

  /**
   * Adds the component and places an AND sign before it, if this is 2nd or 
   * more relation component to be added.
   * @see net.mumie.mathletfactory.display.noc.rel.node.RelationContainer#addRelationComponent(RelationComponent)
   */
  public void addRelationComponent(RelationComponent relationComponent) {
    if(relationComponent == null)
      throw new IllegalArgumentException("relation component is null!");
    addChild(relationComponent);
    if (getRelationContainer().getComponentCount() != 0)
    	getRelationContainer().add(new AttributedStringLabel("\u2227"));
    if(relationComponent.getMasterRelNode().parenthesesNeeded())
    	getRelationContainer().add(new ParenSymbolLabel("(", getFont()));
    getRelationContainer().add((JPanel) relationComponent);
    if(relationComponent.getMasterRelNode().parenthesesNeeded())
    	getRelationContainer().add(new ParenSymbolLabel(")", getFont()));
  }
}
