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

package net.mumie.mathletfactory.math.util;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;

/**
 * Library for some integration algorithms.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class IntegrationLib {
	
  private final static double[] m_tmpNext = new double[2], m_tmpLast = new double[2];  
  private final static NumberTuple m_tmpIn = new NumberTuple(MDouble.class, 2);
  private final static NumberTuple m_tmpOut = new NumberTuple(MDouble.class, 2);;
  private final static MDouble ONE = new MDouble(1);
  private final static MDouble TWO = new MDouble(2);
  private final static MDouble SIX = new MDouble(6);

  /**
   * Implements the euler integration method for a 2nd order linear homogenuous differential equation.
   */
  public static void euler(Affine2DDouble matrix, double[] lastVector, double[] nextVector, double dt){
    matrix.applyTo(lastVector, nextVector);
    AffineDouble.scale(nextVector, dt);
    AffineDouble.add(nextVector, lastVector);
  }

	public static void euler(
		MMFunctionOverR2 function,
		Affine2DPoint lastPoint,
		Affine2DPoint nextPoint,
		MNumber dt) {
		m_tmpIn.setEntry(1, lastPoint.getXProjAsNumberRef());
		m_tmpIn.setEntry(2, lastPoint.getYProjAsNumberRef());

		euler(function, m_tmpIn, m_tmpOut, dt);

		nextPoint.setAffineCoordinates(m_tmpOut);
	}

	public static void euler(
		MMFunctionOverR2 function,
		double[] lastPoint,
		double[] nextPoint,
		double dt) {

    double funcOut = function.evaluate(lastPoint[0], lastPoint[1]);
    nextPoint[0] = lastPoint[0] + dt;
    nextPoint[1] = lastPoint[1] + dt * funcOut;
	}
  

	public static void euler(
		MMFunctionOverR2 function,
		NumberTuple lastPoint,
		NumberTuple nextPoint,
		MNumber dt) {
      if(dt instanceof MDouble){
        m_tmpLast[0] = lastPoint.getEntry(1).getDouble();
        m_tmpLast[1] = lastPoint.getEntry(2).getDouble();

        euler(function, m_tmpLast, m_tmpNext, dt.getDouble());
        nextPoint.setEntry(1, m_tmpNext[0]);
        nextPoint.setEntry(2, m_tmpNext[1]);
        return;
      }

		MNumber m_funcOut =
			function.evaluate(lastPoint.getEntry(1), lastPoint.getEntry(2));
		nextPoint.setEntry(1, MNumber.add(lastPoint.getEntry(1), dt));
		nextPoint.setEntry(
			2,
			MNumber.add(lastPoint.getEntry(2), MNumber.multiply(dt, m_funcOut)));
	}

	public static void runge_kutta(
		MMFunctionOverR2 function,
		Affine2DPoint lastPoint,
		Affine2DPoint nextPoint,
		MNumber dt) {
		Class m_numberClass = lastPoint.getNumberClass();
		m_tmpIn.setEntry(1, lastPoint.getXProjAsNumberRef());
		m_tmpIn.setEntry(2, lastPoint.getYProjAsNumberRef());

		runge_kutta(function, m_tmpIn, m_tmpOut, dt);

		nextPoint.setAffineCoordinates(m_tmpOut);
	}

	public static void runge_kutta(
		MMFunctionOverR2 function,
		double[] lastPoint,
		double[] nextPoint,
		double dt) {

      double k1 = function.evaluate(lastPoint[0], lastPoint[1]);
      double k2 = function.evaluate(lastPoint[0] + dt/2, lastPoint[1] + k1 * dt/2);
      double k3 = function.evaluate(lastPoint[0] + dt/2, lastPoint[1]+ k2 * dt/2);
      double k4 = function.evaluate(lastPoint[0] + dt, lastPoint[1] + dt * k3);
          
      nextPoint[0] = lastPoint[0] + dt; 
      nextPoint[1] = lastPoint[1] + dt/6 *(k1 + 2*k2 + 2*k3 + k4) ;
	}  

	public static void runge_kutta(
		MMFunctionOverR2 function,
		NumberTuple lastPoint,
		NumberTuple nextPoint,
		MNumber dt) {
      if(dt instanceof MDouble){
        m_tmpLast[0] = lastPoint.getEntry(1).getDouble();
        m_tmpLast[1] = lastPoint.getEntry(2).getDouble();

        runge_kutta(function, m_tmpLast, m_tmpNext, dt.getDouble());
        nextPoint.setEntry(1, m_tmpNext[0]);
        nextPoint.setEntry(2, m_tmpNext[1]);
        return;
      }
		MNumber k1 =
			function.evaluate(lastPoint.getEntry(1), lastPoint.getEntry(2));
		MNumber k2 =
			function.evaluate(
				MNumber.add(lastPoint.getEntry(1), MNumber.divide(dt, TWO)),
				MNumber.add(
					lastPoint.getEntry(2),
					MNumber.multiply(MNumber.divide(dt, TWO), k1)));
		MNumber k3 =
			function.evaluate(
				MNumber.add(lastPoint.getEntry(1), MNumber.divide(dt, TWO)),
				MNumber.add(
					lastPoint.getEntry(2),
					MNumber.multiply(MNumber.divide(dt, TWO), k2)));
		MNumber k4 =
			function.evaluate(
				MNumber.add(lastPoint.getEntry(1), dt),
				MNumber.add(lastPoint.getEntry(2), MNumber.multiply(dt, k3)));
		nextPoint.setEntry(1, MNumber.add(lastPoint.getEntry(1), dt));
		nextPoint.setEntry(
			2,
			MNumber.add(
				lastPoint.getEntry(2),
				MNumber.multiply(
					dt,
					MNumber.multiply(
						MNumber.divide(ONE, SIX),
						MNumber.add(
							MNumber.add(
								MNumber.add(k1, MNumber.multiply(TWO, k2)),
								MNumber.multiply(TWO, k3)),
							k4)))));
	}
}
