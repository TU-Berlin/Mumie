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

import net.mumie.mathletfactory.display.Drawable;
import net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * This abstract class serves as an "in-between-class" - every concrete instance of
 * a <code>ContainerObjectTransformer</code> that uses a
 * {@link net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel} as drawable for  the
 * mathematical entity should inherit from this class.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public abstract class TransformerUsingMatrixPanel
	extends ContainerObjectTransformer {

	public final void initialize(MMObjectIF obj) {
		initializeForMatrixPanel(obj);
		// it is necessary to render a first time to put the actual entries into the drawable:
		render();
	}

	/**
	 * This abstract method must be implemented in the non-abstract ("real") <code>
	 * ContainerObjectTransformer</code>s that use a
	 * {@link net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel} as drawable for
	 * the mathematical entity.
	 * 
	 * @param obj is the mathematical entity that shall be visualised.
	 */
	protected abstract void initializeForMatrixPanel(MMObjectIF obj);


	/**
	 * Returns the unique <code>ContainerDrawable</code> used
	 * for displaying the mathematical object, which is always in instance of
	 * {@link net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel}.
	 * 
	 * @see net.mumie.mathletfactory.transformer.GeneralTransformer#getActiveDrawable()
	 */
	public Drawable getActiveDrawable() {
		return (MMNumberMatrixPanel) m_mmPanel;
	}

  /** Returns the matrix panel used by this transformer. */
	protected MMNumberMatrixPanel getMatrixPanel() {
		return (MMNumberMatrixPanel) m_mmPanel;
	}

}
