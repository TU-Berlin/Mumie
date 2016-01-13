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
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector;

/**
 * Transformer for mmobjects extending 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class DefaultRNVectorTransformer
	extends TransformerUsingMatrixPanel {

	private static Logger logger =
		Logger.getLogger(DefaultRNVectorTransformer.class.getName());

	private boolean m_isDisplayAsColumnVector = true;

	/**
		 * This method mainly has to initialize the drawable for the <code>
		 * MMDefaultRNVector</code>, that is to initialize a new
		 * {@link net.mumie.mathletfactory.display.noc.matrix.MatrixPanel}
		 * with <code>ncol=1</code> or <code>nrows=1</code> and with appropriate
		 * number of coordinate entries.
		 *
		 * @param    masterObject is the <code>MMDefaultRNVector</code>
		 */
	public void initializeForMatrixPanel(MMObjectIF masterObject) {
		m_masterMMObject = masterObject;
		if (masterObject instanceof MMDefaultRNVector) {
			if (!m_isInitialized) {
				int nrows, ncols;
				if (m_isDisplayAsColumnVector) {
					nrows = getRealMaster().getVectorSpace().getEnvDimension();
					ncols = 1;
				}
				else {
					nrows = 1;
					ncols = getRealMaster().getVectorSpace().getEnvDimension();
				}
				m_mmPanel =
					new MMNumberMatrixPanel(
						masterObject,
						nrows,
						ncols,this);
						//MMMatrixPanel.MATRIX_BRACKETS);
				getMatrixPanel().setEditable(getMaster().isEditable());
				m_isInitialized = true;
			}
			else
				logger.warning("init():: call happens at least a second time");
		}
		else
			throw new IllegalArgumentException("can only work for instances of MMDefaultRNVector");

	}
	/**
		 * This method mainly actualises the drawable by putting in the current entries of the
		 * coordinate representation of the
		 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector}.
		 * But also other properties (like being editable or not) will be synchronized between
		 * math object and the drawable.
		 */
	public void render() {
		super.render();
		NumberTuple coordinates = getRealMaster().getDefaultCoordinates();
		for(int r = 1; r <= coordinates.getDimension(); r++) {
			coordinates.setEdited(r, getRealMaster().isEdited(r));
		}
		getMatrixPanel().setValuesFromNumberMatrix(coordinates);
		getMatrixPanel().setEditable(getMaster().isEditable());
	}

	private MMDefaultRNVector getRealMaster() {
		return (MMDefaultRNVector) m_masterMMObject;
	}

	public boolean isDisplayAsColumnVector() {
		return m_isDisplayAsColumnVector;
	}

	public void setDisplayAsColumnVector(boolean aValue) {
		m_isDisplayAsColumnVector = aValue;
	}

}
