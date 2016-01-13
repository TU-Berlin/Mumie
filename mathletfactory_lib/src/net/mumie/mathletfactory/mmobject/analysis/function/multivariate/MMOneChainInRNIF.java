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

package net.mumie.mathletfactory.mmobject.analysis.function.multivariate;

import net.mumie.mathletfactory.math.analysis.function.multivariate.OneChainInRNIF;
import net.mumie.mathletfactory.mmobject.Discretizable1DIF;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * An interface for one-chains (a monovariate piecewise continuous function) in R<sup>n</sup>.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public interface MMOneChainInRNIF extends MMCanvasObjectIF, OneChainInRNIF, Discretizable1DIF{

  /** Specifies whether the boundaries of the one chain's domain should be made visible by the system. */	
  public void setBoundarysVisible(boolean aFlag);

  /** Returns whether the boundaries of the one chain's domain should be made visible by the system. */
	public boolean areBoundarysVisible();
	
	/**
	 *  InCorners holds the corners of a Box B.
	 *	e.g. in 2 Dimensions we have
	 *	InCorners[0][0] = lowerLeftX
	 *	InCorners[0][1] = lowerLeftY
	 *	InCorners[1][0] = upperRightX
	 *	InCorners[1][1] = upperRightY
	 *
	 *	outTminTmax is an interval I such that we have
	 *  (x0(t),x1(t),...,xn-1(t)) in B ==> t in I
	 *	this interval should be choosen as small as possible
	 */
	public void getBoundingBox(final double [][] InCorners, final double[] outTminTmax);

}


