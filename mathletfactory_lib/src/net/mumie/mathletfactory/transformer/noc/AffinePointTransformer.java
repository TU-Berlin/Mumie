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

import net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * Transformer for mmobjects extending 
 * {@link net.mumie.mathletfactory.math.geom.affine.AffinePoint}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class AffinePointTransformer extends TransformerUsingMatrixPanel {
  
  private static Logger logger = Logger.getLogger(AffinePointTransformer.class.getName());
  
  
  private boolean m_displayAsColumnVector = true;
  
  
  public void initializeForMatrixPanel(MMObjectIF masterObject) {
    if( ! m_isInitialized ) {
      m_masterMMObject = masterObject;
      int nrows,ncols;
      if( m_displayAsColumnVector ){
        nrows = getRealMaster().getEnvDimension();
        ncols = 1;
      }
      else {
        nrows = 1;
        ncols = getRealMaster().getEnvDimension();
      }
      m_mmPanel = new MMNumberMatrixPanel(masterObject,nrows,ncols,this);//,MMMatrixPanel.MATRIX_BRACKETS);
      m_isInitialized = true;
    }
    else
      logger.warning("initialize:: call happened at least a second time");
  }
   
  public void render() {
		super.render();
    if(getRealMaster().getAffineCoordinates().length != 1)
      return;
		NumberTuple coordinates = getRealMaster().getAffineCoordinates()[0];
		for(int r = 1; r <= coordinates.getDimension(); r++) {
			coordinates.setEdited(r, getRealMaster().isEdited(r));
		}
    getMatrixPanel().setValuesFromNumberMatrix(coordinates);
		getMatrixPanel().setEditable(getMaster().isEditable());
  }
  
  public boolean isDisplayAsColumnVector() {
    return m_displayAsColumnVector;
  }
  
  public void setDisplayAsColumnVector(boolean aValue) {
    m_displayAsColumnVector = aValue;
  }
  
  private AffinePoint getRealMaster() {
    return (AffinePoint)m_masterMMObject;
  }
}

