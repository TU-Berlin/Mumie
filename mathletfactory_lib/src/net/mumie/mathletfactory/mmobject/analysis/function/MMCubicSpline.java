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

package net.mumie.mathletfactory.mmobject.analysis.function;

import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;

/**
 * This class represents a cubic spline, i.e. a piecewise defined function 
 * that consists of a polynomial expression on each interval with a maximum degree of 3 and that 
 * are 2 times differentiable at the joints of each two neighbouring intervals.
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MMCubicSpline extends MMAbstractSpline {

	private double[] z_f;
	private double[] u_f, h, m_y, a, b, c, m_x; 
	private double m_rightDerivative, m_leftDerivative;
  
  private NumberTuple v, b_i;
  private NumberMatrix m;
	private double[] m_xVals_f, m_yVals_f;

  /** Constructs a cubic spline that has its joints at the given points. */
	public MMCubicSpline(Class numberClass, double[] xVals, double[] yVals) {
		super(numberClass, xVals, yVals);
	}

  /** Constructs a cubic spline that has its joints at the given points. */	
  public MMCubicSpline(MMAffine2DPoint[] points) {
		super(points);
	}

	protected void adjustInternalData() {
		super.adjustInternalData();
		adjustFortranFields();
    adjustFields();
	}

  private void adjustFields(){
    if (a == null || a.length != getSamplesCount() - 1)
      a = new double[getSamplesCount() - 1];
    if (b == null || b.length != getSamplesCount())
      b = new double[getSamplesCount()];
    if (c == null || c.length != getSamplesCount() - 1)
      c = new double[getSamplesCount() - 1];
    if (m_y == null || m_y.length != getSamplesCount())
      m_y = new double[getSamplesCount()];
    if (h == null || h.length != getSamplesCount())
      h = new double[getSamplesCount()];
    if (m_x == null || m_x.length != getSamplesCount())
      m_x = new double[getSamplesCount()];
    if(m == null || m.getDimension() != getSamplesCount() - 2)
      m = new NumberMatrix(MDouble.class, getSamplesCount() - 2);
    if(b_i == null || b_i.getDimension() != getSamplesCount() - 2)
      b_i = new NumberTuple(MDouble.class, getSamplesCount() - 2);
    if(v == null || v.getDimension() != getSamplesCount() - 2)
      v = new NumberTuple(MDouble.class, getSamplesCount() - 2);
    newSpline();
  }

	private void adjustFortranFields() {
		if (z_f == null || z_f.length != getSamplesCount() + 1)
			z_f = new double[getSamplesCount() + 1];
		if (u_f == null || u_f.length != getSamplesCount())
			u_f = new double[getSamplesCount()];
		if (m_xVals_f == null || m_xVals_f.length != getSamplesCount() + 1) {
			m_xVals_f = new double[getSamplesCount() + 1];
			m_yVals_f = new double[getSamplesCount() + 1];
		}
		System.arraycopy(m_xVals, 0, m_xVals_f, 1, m_xVals.length);
		System.arraycopy(m_yVals, 0, m_yVals_f, 1, m_xVals.length);
		spline(
			m_xVals_f,
			m_yVals_f,
			getSamplesCount(),
			m_leftDerivative,
			m_rightDerivative,
			z_f);
	}

  /** Sets the spline's derivatives at the left and the right border of the domain. */
	public void setDerivatives(double leftDerivative, double rightDerivative) {
		m_leftDerivative = leftDerivative;
		m_rightDerivative = rightDerivative;
		adjustInternalData();
	}

  /** Returns the spline's derivatives at the left border of the domain. */
	public double getLeftDerivative() {
		return m_leftDerivative;
	}

  /** Returns the spline's derivatives at the right border of the domain. */
	public double getRightDerivative() {
		return m_rightDerivative;
	}

	protected double specificEvaluate(double x, int i) {
		return newSplint(x, i-1 ); 
    //return splint(m_xVals_f, m_yVals_f, z_f, getSamplesCount(), x);
	}

	protected void specificEvaluate(MNumber xin, MNumber yout, int i) {
		yout.setDouble(specificEvaluate(xin.getDouble(), i));
	}

  private void newSpline(){
    Affine2DPoint i_pt = new Affine2DPoint(MDouble.class);
    int n =  getSamplesCount()-1;
    for(int i=0;i<=n;i++){
      getSamplePoint(i, i_pt);
      m_x[i] = i_pt.getXAsDouble();
      m_y[i] = i_pt.getYAsDouble();
      if(i>0 && m_x[i] == m_x[i-1])
        return;
    }
    for(int i=0;i<=n-1;i++)
      h[i] = m_x[i+1] - m_x[i];
    
    for(int i=1;i<n;i++){
      m.setEntry(i, i, 2*(h[i-1]+h[i]));
      if(i<n-1){      
        m.setEntry(i, i+1, h[i]);
        m.setEntry(i+1, i, h[i]);
      }
      v.setEntry(i, (m_y[i+1] - m_y[i])/h[i] - (m_y[i] - m_y[i-1])/h[i-1]);
    }
    //System.out.println(m);
    b_i = m.inverse().applyTo(v);    
    //System.out.println(m);
    b[0] = 0; // natural spline
    b[n] = 0; // natural spline
    for(int i=1;i<=n-1;i++)    
      b[i] = 3 * b_i.getEntryRef(i).getDouble();
    for(int i=0;i<n;i++){
      a[i] = (b[i+1] - b[i])/(3*h[i]);
      c[i] = (m_y[i+1] - m_y[i])/h[i] - (b[i+1] + 2*b[i])*h[i]/3;
    }
    //System.out.println(this);
  }

  public String toString(){
    String retVal = "";
    for(int i=0;i<getSamplesCount()-1;i++){
      retVal += a[i]+"(x - "+m_x[i]+")� + "+b[i]+"(x - "+m_x[i]+")� + "+c[i]+"(x - "+m_x[i]+") + "+m_y[i] +         " for "+m_x[i]+" =< x < "+m_x[i+1]+"\n";
    }
    return retVal;
  }

	private void spline(
		double x[],
		double y[],
		int n,
		double yp1,
		double ypn,
		double y2[]) {
		int i, k;
		double p, qn, sig, un;
		if (yp1 > 0.99e30) {
			y2[1] = 0.;
			u_f[1] = 0.0;
		} else {
			y2[1] = -0.5;
			u_f[1] = (3.0 / (x[2] - x[1])) * ((y[2] - y[1]) / (x[2] - x[1]) - yp1);
		}

		for (i = 2; i <= n - 1; i++) {
			sig = (x[i] - x[i - 1]) / (x[i + 1] - x[i - 1]);
			p = sig * y2[i - 1] + 2.0;
			y2[i] = (sig - 1.0) / p;
			u_f[i] =
				(y[i + 1] - y[i]) / (x[i + 1] - x[i])
					- (y[i] - y[i - 1]) / (x[i] - x[i - 1]);
			u_f[i] = (6.0 * u_f[i] / (x[i + 1] - x[i - 1]) - sig * u_f[i - 1]) / p;
		}

		if (ypn > 0.99e30)
			qn = un = 0.0;
		else {
			qn = 0.5;
			un =
				(3.0 / (x[n] - x[n - 1]))
					* (ypn - (y[n] - y[n - 1]) / (x[n] - x[n - 1]));
		}
		y2[n] = (un - qn * u_f[n - 1]) / (qn * y2[n - 1] + 1.0);
		for (k = n - 1; k >= 1; k--)
			y2[k] = y2[k] * y2[k + 1] + u_f[k];
	}

  private double newSplint(double x, int i){
    if(i>getSamplesCount()-2)
      i = getSamplesCount()-2;
    double xdiff = x - m_x[i];
    double result = a[i]*xdiff*xdiff*xdiff + b[i] * xdiff*xdiff + c[i] * xdiff + m_y[i];
    return result;
  }

//	private double splint(
//		double xa[],
//		double ya[],
//		double y2a[],
//		int n,
//		double x) {
//
//		int klo, khi, k;
//		double h, b, a;
//
//		klo = 1;
//		khi = n;
//		while (khi - klo > 1) {
//			k = (khi + klo) >> 1;
//			if (xa[k] > x)
//				khi = k;
//			else
//				klo = k;
//		}
//		h = xa[khi] - xa[klo];
//		if (h == 0.0)
//			System.out.println("Bad xa input to routine splint");
//		a = (xa[khi] - x) / h;
//		b = (x - xa[klo]) / h;
//		return a * ya[klo]
//			+ b * ya[khi]
//			+ ((a * a * a - a) * y2a[klo] + (b * b * b - b) * y2a[khi]) * (h * h) / 6.0;
//
//	}

  public static void main(String[] str){
    MMCubicSpline spline = new MMCubicSpline(MDouble.class, new double[]{-2,0,2}, new double[]{0,2,0});
    spline.evaluate(1);
    System.out.println(spline);
  }

}
