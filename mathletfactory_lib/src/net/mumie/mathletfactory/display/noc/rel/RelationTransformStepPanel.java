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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.noc.rel.node.RelationComponent;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.rel.RelTransform;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;

/**
 *  This class implements the functionality needed for transforming a complex
 *  {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation}.
 *  
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class RelationTransformStepPanel extends JPanel {

  public final static String TRANSFORM = "transform";

  /** 
   * The interactive display of the 
   * {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation}.
   */ 
  private MMRelationPanel m_relationPanel;

  /** The box that contains the transform panel. */
  private Box m_transformPanelBox = Box.createVerticalBox();

  /**
   *  This inner class contains the mathematical transformation typed in by the
   *  user. It refers to a subrelation (a relation tree that contains no 
   *  OR-Conjunctions). For each subrelation in a relation there is a transform
   *  panel, so that branching into subrelations is possible. 
   */
  protected class TransformPanel extends JPanel {

//    private RelationComponent m_relationRow;
    
    /**
     *  The textfield in which the user types the transformation which should
     *  be applied to the relation.
     */
    JTextField m_textField = new JTextField(10) {
      public Dimension getMaximumSize() {
        Dimension dim = super.getMaximumSize();
        dim.height = (int) (getFont().getSize() * 2);
        dim.width = getColumnWidth() * 10;
        return dim;
      }
    };

    /** Constructs the transform panel for the given relation row. */
    protected TransformPanel(RelationComponent relationRow) {
//      m_relationRow = relationRow;
      setLayout(new BorderLayout());
      Box box = Box.createVerticalBox();
      m_textField.setAlignmentX(Component.RIGHT_ALIGNMENT);
      //box.setBorder(new LineBorder(Color.BLACK));
      box.add(Box.createGlue());
      box.add(m_textField);
      box.add(Box.createGlue());
      this.add(box, BorderLayout.CENTER);
      m_textField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          transformRelation(m_textField.getText(), TransformPanel.this);
        }
      });
    }
    
    /** Passes the focus to the text field. */
    public void requestFocus() {
      m_textField.requestFocus();
    }
  }

  private TransformPanel[] m_transformPanels;

  /** 
   *  Constructs the relation transform step panel for the given 
   *  {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation}.
   *  @see net.mumie.mathletfactory.display.noc.JPanelDrawable#JPanelDrawable(MMObjectIF)
   */
  public RelationTransformStepPanel(MMObjectIF master) {
    setLayout(new GridLayout(1, 1));
    m_relationPanel = (MMRelationPanel)((MMRelation)master).getAsContainerContent();
    RelationComponent[] relationRows = m_relationPanel.getRelationRows();
    Box box = new Box(BoxLayout.X_AXIS);
    box.add(Box.createGlue());
    box.add(m_relationPanel);
    box.add(Box.createGlue());
    
    
    // created all transform panels and add them to the trafo-box
    m_transformPanels = new TransformPanel[relationRows.length];
    for(int i=0; i<relationRows.length;i++){
      m_transformPanels[i] = new TransformPanel(relationRows[i]);
//      m_transformPanels[i].setBackground(MumieTheme.DEFAULT_THEME.getWindowBackground());
      //m_transformPanels[i].setBorder(new LineBorder(Color.BLACK));
      m_transformPanelBox.add(m_transformPanels[i]);
    }
    
    box.add(m_transformPanelBox);
    add(box);
    //setBorder(new LineBorder(Color.BLACK));
    
    setBackground(MumieTheme.DEFAULT_THEME.getWindowBackground());
    box.setBackground(MumieTheme.DEFAULT_THEME.getWindowBackground());
  }

  /**
   *  Transforms the relation by using the given string (e.g. "*3" results in
   *  a multiplication of both sides of the relation with 3.
   */
  private void transformRelation(String transformString, TransformPanel source) {
    // check if input is non-null
    if (transformString.equals(OpNode.REPLACEMENT_IDENTIFIER)) {
      // user typed no valid transformation
      firePropertyChange(TRANSFORM, null, null);
      return;
    }

    MMRelation newRelation = new MMRelation(RelTransform.transformRelation(transformString, 
    new Relation((RelNode)((MMRelation) m_relationPanel.getMaster()).getRelRoot().clone(),((MMRelation) m_relationPanel.getMaster()).getNormalForm()),
    ((MMRelation)m_relationPanel.getMaster()).getSimpleRelationsWith("y")[0]));
    newRelation.getDisplayProperties().setFont(m_relationPanel.getFont());
   
    // inform master of new relation
    firePropertyChange(TRANSFORM, null, newRelation);
  }

  /** 
   * Ensures that the minimum height is not less than the height of 
   * {@link #m_relationPanel}.
   * 
   * @see java.awt.Component#getPreferredSize()
   */
  public Dimension getPreferredSize() {
    Dimension dim = super.getPreferredSize();
    dim.height = m_relationPanel.getMinimumSize().height*5/4;
    return dim;
  }

  /** 
   * Ensures that the maximum height is not more than the height of 
   * {@link #m_relationPanel}.
   * 
   * @see java.awt.Component#getMaximumSize()
   */
  public Dimension getMaximumSize() {
    Dimension dim = super.getMaximumSize();
    dim.height = getPreferredSize().height;
    return dim;
  }

  /** Passes the focus to the first of the {@link #m_transformPanels}. */
  public void requestFocus() {
    m_transformPanels[0].requestFocus();
  }

}
