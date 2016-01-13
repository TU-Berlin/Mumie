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
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment}.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Affine2DLineSegmentTransformer
	extends TransformerUsingMatrixPanel {

	private static Logger logger =
		Logger.getLogger(Affine2DLineSegmentTransformer.class.getName());

	public void initializeForMatrixPanel(MMObjectIF masterObject) {
		if (!m_isInitialized) {
			masterObject = (MMAffine2DLineSegment) masterObject;
			int nrows = 2;
			int ncols = 2;
			m_mmPanel =
				new MMNumberMatrixPanel(
					masterObject,
					nrows,
					ncols, this);
					//MMMatrixPanel.MATRIX_BRACKETS);
			m_isInitialized = true;
		}
		else if (logger.isLoggable(java.util.logging.Level.WARNING));
		logger.warning("initialize()::happened at least a second time");
	}

	public void render() {
		super.render();
		getMatrixPanel().setColumnValues(
			1,
			getRealMaster().getInitialPoint().getAffineCoordinatesOfPoint());
		getMatrixPanel().setColumnValues(
			2,
			getRealMaster().getEndPoint().getAffineCoordinatesOfPoint());
	}

	private MMAffine2DLineSegment getRealMaster() {
		return (MMAffine2DLineSegment) m_masterMMObject;
	}

}
