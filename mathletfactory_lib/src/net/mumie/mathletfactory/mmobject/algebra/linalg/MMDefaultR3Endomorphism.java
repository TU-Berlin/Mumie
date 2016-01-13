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

package net.mumie.mathletfactory.mmobject.algebra.linalg;

import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This is the default MMobject for endomorphisms from R<sup>3</sup> to
 *  R<sup>3</sup>.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class MMDefaultR3Endomorphism extends MMDefaultRNEndomorphism {

	/**
	 * Constructs an <code>MMDefaultR3Endomorphism</code> that is a
	 * {@link net.mumie.mathletfactory.algebra.LinearMap} from <code>aR3Space</code>
	 * into itself. It is initialized as identity. Use method
	 * {@link #setDefaultMatrixRepresentation} to change this <code>
	 * MMDefaultR3Endomorphism</code>.
	 */
	public MMDefaultR3Endomorphism(MMDefaultR3 aR3Space) {
		super(aR3Space);
	}

	/**
	 * Constructs an <code>MMDefaultR3Endomorphism</code> that is a
	 * {@link net.mumie.mathletfactory.algebra.LinearMap} from an instance of
	 * {@link net.mumie.mathletfactory.mmobject.MMDefaultR3} to itself and that
	 * maps domain[i] to range[i] (i=1,2,3)
	 */
	public MMDefaultR3Endomorphism(
		MMDefaultR3Vector[] domain,
		MMDefaultR3Vector[] range) {
		super(domain, range);
	}

  /**
   * Returns the default transform type. 
   * @see net.mumie.mathletfactory.mmobject.VisualizeIF#getDefaultTransformType()
   */
	public int getDefaultTransformType() {
		return GeneralTransformer.LINEAR_MAP_TO_MATRIX_TRANSFORM;
	}

}
