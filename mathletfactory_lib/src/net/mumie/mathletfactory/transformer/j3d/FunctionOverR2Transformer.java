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

package net.mumie.mathletfactory.transformer.j3d;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.j3d.J3DFunctionGraphOverR2Drawable;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;

/**
 *  This class transforms 
 *  {@link net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2} 
 *  objects into a Java3D 
 *  {@link net.mumie.mathletfactory.display.j3d.J3DFunctionGraphOverR2Drawable}.
 *  
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class FunctionOverR2Transformer extends Affine3DDefaultTransformer {

	/** Every time the function is recalculated, the version number is incremented */
	private int m_version = 0;

	/** 
	 * Constructs the transformer for the given 
	 * {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas} display.
	 */
	public FunctionOverR2Transformer() {
		m_allDrawables =
			new CanvasDrawable[] { new J3DFunctionGraphOverR2Drawable()};
		m_activeDrawable = m_allDrawables[0];
	}

	public void initialize(MMObjectIF masterObject) {
		super.initialize(masterObject);
		((J3DFunctionGraphOverR2Drawable) m_activeDrawable).setValues(
			getRealMaster().getValues());
	}

	public double[] getWorldPickPointFromMaster() {
		return new double[3];
	}

	public void synchronizeMath2Screen() {
		if (getRealMaster().getVersion() > m_version) {
			((J3DFunctionGraphOverR2Drawable) m_activeDrawable).setValues(
				getRealMaster().getValues());
			((J3DFunctionGraphOverR2Drawable) m_activeDrawable).setGridParameters(
				getRealMaster().getGridParameters());
			m_version = getRealMaster().getVersion();
		}
	}

	private MMFunctionOverR2 getRealMaster() {
		return (MMFunctionOverR2) m_masterMMObject;
	}

	public void render() {
		super.render();
		renderLabel(getWorldPickPointFromMaster());
	}

}
