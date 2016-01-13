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

package net.mumie.mathletfactory.transformer.noc;

import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.math.algebra.linalg.OpMatrix;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;

/**
 * Transformer for 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMOpMatrix}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class OpMatrixTransformer extends TransformerUsingMatrixPanel implements ChangeListener {
  
  private static Logger logger = Logger.getLogger(CanvasObjectTransformer.class.getName());
  
  /**
   * Should be called one time from within the MMObject or the GeneralTransformer.
   *
   * @param    masterObject        a <code>MMObjectIF</code>
   */
  public void initializeForMatrixPanel(MMObjectIF masterObject) {
    if (!m_isInitialized) {
      m_masterMMObject = masterObject;
      int nrows = ((OpMatrix)getRealMaster()).getRowCount();
      int ncols = ((OpMatrix)getRealMaster()).getColumnCount();
      m_mmPanel = new MMNumberMatrixPanel(masterObject, nrows, ncols,this); //,
                                  //MMMatrixPanel.MATRIX_BRACKETS);
      getMatrixPanel().setEditable(getRealMaster().isEditable());
      m_isInitialized = true;
      ((OpMatrix)masterObject).addDimensionChangeListener(this);
    }
    else logger.warning("initialize:: call happens at least a second time");
  }
  
  public void render() {
		super.render();
    OpMatrix m = (OpMatrix)getRealMaster();
    getMatrixPanel().setValuesFromNumberMatrix(m);
    getMatrixPanel().setEditable(getRealMaster().isEditable());
  }
  
  private MMObjectIF getRealMaster() {
    return m_masterMMObject;
  }
  
  public void stateChanged(ChangeEvent event) {
    MMNumberMatrixPanel matrixPanel = (MMNumberMatrixPanel)m_mmPanel;
    Matrix matrix = (Matrix)event.getSource();
    matrixPanel.setColumnsAndRows(matrix.getColumnCount(), matrix.getRowCount());
  }
  
}
