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

import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 *  This class is the base for all affine (i.e. math coordinates = world 
 *  coordinates) Java3D transformers.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class Affine3DDefaultTransformer
	extends Canvas3DObjectJ3DTransformer {

	private final double[] m_tmpForCalc = new double[3];

	/**
	 *  The coordinates need not be transformed, since math coordinates are
	 *  identical to world coordinates (euclidean vector space) and
	 *  world2screen conversion is done implicitly in
	 *  {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas#renderScene} by
	 *  transforming the view platform.
	 */
	public void math2World(Affine3DPoint mathPoint, double[] worldPoint) {
		worldPoint[0] = ((Affine3DPoint) m_masterMMObject).getXAsDouble();
		worldPoint[1] = ((Affine3DPoint) m_masterMMObject).getYAsDouble();
		worldPoint[2] = ((Affine3DPoint) m_masterMMObject).getZAsDouble();
	}

  public void math2World(double[] mathPoint, double[] worldPoint){
    worldPoint[0] = mathPoint[0];
    worldPoint[1] = mathPoint[1];
    worldPoint[2] = mathPoint[2];    
  }

	public void getMathObjectFromScreen(
		double[] javaScreenCoords,
		NumberTypeDependentIF affine3DPoint) {
		if (affine3DPoint instanceof Affine3DPoint) {
			screen2World(javaScreenCoords, m_tmpForCalc, getDistanceInZProj());
			((Affine3DPoint) affine3DPoint).setXYZ(m_tmpForCalc);
		}
		else
			throw new IllegalArgumentException("can only be applied to instance of Affine3DPoint here");
	}

	public void getScreenPointFromMath(
		NumberTypeDependentIF affine3DPoint,
		double[] javaScreenCoords) {
		if (affine3DPoint instanceof Affine3DPoint) {
			m_tmpForCalc[0] = ((Affine3DPoint) affine3DPoint).getXAsDouble();
			m_tmpForCalc[1] = ((Affine3DPoint) affine3DPoint).getYAsDouble();
			m_tmpForCalc[2] = ((Affine3DPoint) affine3DPoint).getZAsDouble();
			world2Screen(m_tmpForCalc, javaScreenCoords);
		}
		else
			throw new IllegalArgumentException("can only be applied to instance of Affine3DPoint here");
	}
}
