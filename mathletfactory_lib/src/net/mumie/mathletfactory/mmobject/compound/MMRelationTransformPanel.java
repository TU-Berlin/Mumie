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

package net.mumie.mathletfactory.mmobject.compound;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.mumie.mathletfactory.display.noc.rel.RelationTransformStepPanel;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 *  This panel is used to transform an arbitrary relation using a list of
 *  {@link net.mumie.mathletfactory.display.noc.rel.RelationTransformStepPanel}s.
 *  
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMRelationTransformPanel extends JPanel implements PropertyChangeListener {
  
  private List m_panels = new ArrayList();
  private Box m_box = Box.createVerticalBox();
  private JScrollPane m_scrollPane = new JScrollPane(m_box);
  
  /**
   * Constructs a new relation transform panel with the given initial relation.
   */
  public MMRelationTransformPanel(MMRelation relation){
    setLayout(new BorderLayout());
    relation.setEditable(true);
    RelationTransformStepPanel initialPanel = new RelationTransformStepPanel(relation);
    initialPanel.addPropertyChangeListener(this);
    m_panels.add(initialPanel);
    m_box.add(initialPanel);
    m_scrollPane.setViewportView(m_box);
    add(m_scrollPane, BorderLayout.CENTER);
//    m_box.setBackground(MumieTheme.DEFAULT_THEME.getWindowBackground());
//    setBackground(MumieTheme.DEFAULT_THEME.getWindowBackground());
  }
  
  /**
   * This method catches 
   * {@link net.mumie.mathletfactory.display.noc.rel.RelationTransformStepPanel#TRANSFORM}
   * property change events and if they receive one, a new
   * {@link net.mumie.mathletfactory.display.noc.rel.RelationTransformStepPanel}  
   * with the new transformed relation is created and appended to the list. 
   * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent e){
  	if(!e.getPropertyName().equals(RelationTransformStepPanel.TRANSFORM))
  	  return;
  	MMRelation newRelation = (MMRelation)e.getNewValue();
  	RelationTransformStepPanel sourcePanel = (RelationTransformStepPanel)e.getSource();
    int i = m_panels.indexOf(sourcePanel);
    if(i < m_panels.size()) // remove all panels after the source panel
      while(i+1 < m_panels.size()){
        m_box.remove((JPanel)m_panels.get(m_panels.size()-1));
        m_panels.remove(m_panels.size()-1);
      }
    m_box.repaint();
    if(newRelation == null) // invalid transformation
      return;
    // add the new panel
    RelationTransformStepPanel newPanel = new RelationTransformStepPanel(newRelation);
    newPanel.addPropertyChangeListener(this);
    m_box.add(newPanel);
    m_panels.add(newPanel);
    
    // scroll to bottom
    Rectangle scrollTo = newPanel.getVisibleRect();
    scrollTo.x = 2*m_box.getWidth();
    scrollTo.y = 2*m_box.getHeight();
    newPanel.scrollRectToVisible(scrollTo);
    newPanel.requestFocus();
  }
  
  /**
   * For testing purposes.
   */
  public static void main(String[] args){
    Logger.getLogger("").setLevel(java.util.logging.Level.FINE);
    BasicApplicationFrame f = new BasicApplicationFrame(
      new MMRelationTransformPanel(new MMRelation(MRational.class, "y=0.5*tan(1/(x^3-1))",
                                                  new Font("Serif",Font.PLAIN, 28), Relation.NORMALIZE_OP)),600);
    f.pack();
    f.setVisible(true);
  }
}

