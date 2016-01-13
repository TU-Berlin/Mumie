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

package net.mumie.mathletfactory.display.noc.geom;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.Box;
import javax.swing.JLabel;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.math.geom.affine.AffineSpace;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 *  This class renders the symbolical representation of an
 *  {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DSubspace} using
 *  {@link net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel}s for the
 *  rendering of the position vector and the directions.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMAffineSubspacePanel extends MMPanel implements PropertyHandlerIF {

  private AffineSpace m_master; 

  private ContainerObjectTransformer m_transformer;

  private MMNumberMatrixPanel m_pointPanel,
    m_firstDirectionPanel,
    m_secondDirectionPanel;

  private int m_dimension;
  /**
    * Class constructor.
    */
  public MMAffineSubspacePanel(
    MMObjectIF masterObject,
    ContainerObjectTransformer transformer) {
    super(masterObject, transformer);
    m_transformer = transformer;
//    setLayout(new BorderLayout());
    m_master = (AffineSpace)masterObject;
    addPropertyChangeListener(masterObject);
    createPanel();
  }

  public void createPanel() {
    m_dimension = m_master.getDimension();
    removeAll();
    Box box = Box.createHorizontalBox();
    m_pointPanel = new MMNumberMatrixPanel(getMaster(), m_master.getEnvDimension(), 1, m_transformer);
    m_firstDirectionPanel =
      new MMNumberMatrixPanel(getMaster(), m_master.getEnvDimension(), 1, m_transformer);
    m_secondDirectionPanel =
      new MMNumberMatrixPanel(getMaster(), m_master.getEnvDimension(), 1, m_transformer);
    m_pointPanel.addPropertyChangeListener(this);
    m_firstDirectionPanel.addPropertyChangeListener(this);
    m_secondDirectionPanel.addPropertyChangeListener(this);
    if(m_dimension == -1){
      box.add(new ParenSymbolLabel("{", DisplayProperties.STD_FONT));
      box.add(new ParenSymbolLabel("}", DisplayProperties.STD_FONT));
    } else if(m_dimension == 0){
      box.add(m_pointPanel);
    } else if (m_dimension == 1) {
      box.add(new ParenSymbolLabel("{", DisplayProperties.STD_FONT));
      box.add(m_pointPanel);
      box.add(new JLabel(" + t  "));
      box.add(m_firstDirectionPanel);
      box.add(new ParenSymbolLabel("}", DisplayProperties.STD_FONT));
    }
    else if (m_dimension == 2) {
      box.add(new ParenSymbolLabel("{", DisplayProperties.STD_FONT));
      box.add(m_pointPanel);
      box.add(new JLabel(" + s  "));
      box.add(m_firstDirectionPanel);
      box.add(new JLabel(" + t  "));
      box.add(m_secondDirectionPanel);
      box.add(new ParenSymbolLabel("}", DisplayProperties.STD_FONT));
    }
    add(box, BorderLayout.CENTER);
  }

  public int getDimension(){
    return m_dimension;
  }

  public void propertyChange(PropertyChangeEvent e){
    if(!e.getPropertyName().equals(NUMBERMATRIX))
      return;
    if(e.getSource().equals(m_pointPanel))
      firePropertyChange(ORIGIN_VECTOR, e.getOldValue(), e.getNewValue());
    if(e.getSource().equals(m_firstDirectionPanel) || e.getSource().equals(m_secondDirectionPanel))
      firePropertyChange(DIRECTION_VECTOR, e.getOldValue(), e.getNewValue());
  }

  /**
    *  Returns the 
    *  {@link net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel} of the
    *  position vector.
    */
  public MMNumberMatrixPanel getPointPanel() {
    return m_pointPanel;
  }

  /**
    *  Returns the 
    *  {@link net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel} of the 
    *  first direction vector.
    */
  public MMNumberMatrixPanel getFirstDirectionPanel() {
    return m_firstDirectionPanel;
  }

  /**
    *  Returns the 
    *  {@link net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel} of the 
    *  second direction vector.
    */
  public MMNumberMatrixPanel getSecondDirectionPanel() {
    return m_secondDirectionPanel;
  }
}
