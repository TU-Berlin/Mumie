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

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNEndomorphism;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;

/**
 * Transformer for mmobjects extending 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNEndomorphism}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class DefaultRNEndomorphismTransformer extends TransformerUsingMatrixPanel {
  
  private static Logger logger = Logger.getLogger(CanvasObjectTransformer.class.getName());
  
  
  /**
   * Display a <code>LinearMap</code> by its matrix representation with
   * respect to the default basis in domain and range.
   */
  public static final int DEFAULT_MATRIX_REPRESENTATION = 1;
  
  /**
   * Display a {@link net.mumie.mathletfactory.algebra.linalg.LinearMap} by its matrix
   * representation with respect to the actual basis in domain and range.
   */
  public static final int ACTUAL_MATRIX_REPRESENTATION = 2;
  
  
  private int m_presentationType = DEFAULT_MATRIX_REPRESENTATION;

 
  /**
   * This method mainly has to initialize the drawable for the <code>
   * MMDefaultRNEndomorphism</code>, that is to initialize a new
   * {@link net.mumie.mathletfactory.display.noc.matrix.MatrixPanel} with appropriate
   * number of rows and columns.
   *
   * @param    masterObject is the <code>MMDefaultRNEndomorphism</code> to visualise.
   *
   * @version  9/24/2002
   */
  public void initializeForMatrixPanel(MMObjectIF masterObject) {
  	if( masterObject instanceof MMDefaultRNEndomorphism) {
    if( !m_isInitialized ) {
      m_masterMMObject = masterObject;
      int nrows = getRealMaster().getDefaultMatrixRepresentationRef().getRowCount();
      int ncols = getRealMaster().getDefaultMatrixRepresentationRef().getColumnCount();
      m_mmPanel = new MMNumberMatrixPanel(masterObject,nrows,ncols,this);//,MMMatrixPanel.MATRIX_BRACKETS);
      getMatrixPanel().setEditable(getRealMaster().isEditable());
      m_isInitialized = true;
    }
    else
      logger.warning("initialize:: call happens at least a second time");
  	}
  	else{
  		throw new IllegalArgumentException("can only work for instances of MMDefaultRNEndomorphism," +  			"but I got "+masterObject.getClass().getName());
  	}
  }
  
  
  /**
   * This method mainly actualises the drawable by putting in the current entries of the
   * matrix representation of the
   * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNEndomorphism}.
   * But also other properties (like being editable or not) will be synchronized between
   * math object and the drawable.
   */
  public void render() {
		super.render();
  	MMDefaultRNEndomorphism linMap = getRealMaster();
    if( linMap.isMap() ) {
      NumberMatrix m;
      if ( m_presentationType == DEFAULT_MATRIX_REPRESENTATION)
      // ok to use reference here, because method setValuesFromNumberMatrix
      // above will do a copy.
        m = linMap.getDefaultMatrixRepresentationRef();
      else if ( m_presentationType == ACTUAL_MATRIX_REPRESENTATION)
        m = linMap.getActualMatrixRepresentation();
      else
        throw new IllegalUsageException("should never occur");
      getMatrixPanel().setValuesFromNumberMatrix(m);
			getMatrixPanel().setEditable(getRealMaster().isEditable());
    }
    else{
      Matrix m = linMap.getExceptionMatrix();
      // put actual values from math-entity to drawable:
      getMatrixPanel().setValuesFromMatrix(m);
			getMatrixPanel().setEditable(getRealMaster().isEditable());
    }
  }
  
  private MMDefaultRNEndomorphism getRealMaster() {
    return (MMDefaultRNEndomorphism)m_masterMMObject;
  }
    
}

