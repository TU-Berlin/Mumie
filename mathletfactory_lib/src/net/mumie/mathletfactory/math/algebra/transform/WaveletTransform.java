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

package net.mumie.mathletfactory.math.algebra.transform;

import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;

/**
 * This class is used to apply wavelet transformations and inverse
 * transformations on a matrix
 * 
 * @author markov
 * @mm.docstatus finished
 */
public class WaveletTransform {

	/**
	 * Returns the wavelet tranformed matix of the argument matrix
	 * 
	 * @param aMatrix
	 *            schould have even number of columns and rows
	 * @return the matix that is transformed with wavelet
	 */
	public static NumberMatrix transform(NumberMatrix aMatrix) {

		int hight = aMatrix.getRowCount();
		int width = aMatrix.getColumnCount();

		if (hight - Math.floor((double) hight / 2.0) * 2 != 0)
			throw new IllegalArgumentException("Hight should be even number!");
		if (width - Math.floor((double) width / 2.0) * 2 != 0)
			throw new IllegalArgumentException("Width should be even number!");

//		NumberMatrix wl1 = new NumberMatrix(aMatrix.getNumberClass(), hight,
//				hight / 2);
//		NumberMatrix wh1 = new NumberMatrix(aMatrix.getNumberClass(), hight,
//				hight / 2);
//		for (int i = 1; i <= hight / 2; i++) { // buid Wl
//			wl1.setEntry(i, 2 * i, 0.5); 		// 0.5 0.5 0   0   0 0 0 0 ...
//			wl1.setEntry(i, 2 * i - 1, 0.5);	// 0   0   0.5 0.5 0 0 0 0 ...
//		}
//		for (int i = 1; i <= hight / 2; i++) { // build Wh
//			wh1.setEntry(i, 2 * i, -1);			// 1 -1 0  0 0 0 0 0 ...
//			wh1.setEntry(i, 2 * i - 1, 1); 		// 0  0 1 -1 0 0 0 0 ...
//		}
//		NumberMatrix w1 = new NumberMatrix(aMatrix.getNumberClass(), hight, hight);
//		for (int i = 1; i <= hight / 2; i++)			// combines both matrices
//			w1.setRowVector(i, wl1.getRowVector(i));
//		for (int i = 1; i <= hight / 2; i++)
//			w1.setRowVector(hight / 2 + i, wh1.getRowVector(i));

		NumberMatrix w1 = getTransformingMatrix(aMatrix.getNumberClass(),hight);
		
//		NumberMatrix wl2 = new NumberMatrix(aMatrix.getNumberClass(), width,
//				width / 2);
//		NumberMatrix wh2 = new NumberMatrix(aMatrix.getNumberClass(), width,
//				width / 2);
//		for (int i = 1; i <= width / 2; i++) { // buid Wl
//			wl2.setEntry(i, 2 * i, 0.5);
//			wl2.setEntry(i, 2 * i - 1, 0.5);
//		}
//		for (int i = 1; i <= width / 2; i++) { // build Wh
//			wh2.setEntry(i, 2 * i, -1);
//			wh2.setEntry(i, 2 * i - 1, 1);
//		}
//		NumberMatrix w2 = new NumberMatrix(aMatrix.getNumberClass(), width,
//				width);
//		for (int i = 1; i <= width / 2; i++)
//			w2.setRowVector(i, wl2.getRowVector(i));
//		for (int i = 1; i <= width / 2; i++)
//			w2.setRowVector(width / 2 + i, wh2.getRowVector(i));

		NumberMatrix w2 = getTransformingMatrix(aMatrix.getNumberClass(), width);
		w2.transpose();
		return w1.mult(aMatrix).mult(w2);
	}

	/**
	 * Returns the wavelet inverse tranformed matix of the argument matrix
	 * 
	 * @param aMatrix
	 *            is a wavelet transformed matrix
	 * @return the original matrix
	 */
	public static NumberMatrix inverseTransform(NumberMatrix aMatrix) {

		int hight = aMatrix.getRowCount();
		int width = aMatrix.getColumnCount();

		if (hight - Math.floor((double) hight / 2.0) * 2 != 0)
			throw new IllegalArgumentException("Hight should be even number!");
		if (width - Math.floor((double) width / 2.0) * 2 != 0)
			throw new IllegalArgumentException("Width should be even number!");

		NumberMatrix w1 = getTransformingMatrix(aMatrix.getNumberClass(),hight);
//		NumberMatrix wl1 = new NumberMatrix(aMatrix.getNumberClass(), hight,
//				hight / 2);
//		NumberMatrix wh1 = new NumberMatrix(aMatrix.getNumberClass(), hight,
//				hight / 2);
//		for (int i = 1; i <= hight / 2; i++) { // buid Wl
//			wl1.setEntry(i, 2 * i, 0.5);
//			wl1.setEntry(i, 2 * i - 1, 0.5);
//		}
//		for (int i = 1; i <= hight / 2; i++) { // build Wh
//			wh1.setEntry(i, 2 * i, -1);
//			wh1.setEntry(i, 2 * i - 1, 1);
//		}
//
//		NumberMatrix w1 = new NumberMatrix(aMatrix.getNumberClass(), hight,
//				hight);
//		for (int i = 1; i <= hight / 2; i++)
//			w1.setRowVector(i, wl1.getRowVector(i));
//		for (int i = 1; i <= hight / 2; i++)
//			w1.setRowVector(hight / 2 + i, wh1.getRowVector(i));

		NumberMatrix w2 = getTransformingMatrix(aMatrix.getNumberClass(), width);
//		NumberMatrix wl2 = new NumberMatrix(aMatrix.getNumberClass(), width,
//				width / 2);
//		NumberMatrix wh2 = new NumberMatrix(aMatrix.getNumberClass(), width,
//				width / 2);
//		for (int i = 1; i <= width / 2; i++) { // buid Wl
//			wl2.setEntry(i, 2 * i, 0.5);
//			wl2.setEntry(i, 2 * i - 1, 0.5);
//		}
//		for (int i = 1; i <= width / 2; i++) { // build Wh
//			wh2.setEntry(i, 2 * i, -1);
//			wh2.setEntry(i, 2 * i - 1, 1);
//		}
//		NumberMatrix w2 = new NumberMatrix(aMatrix.getNumberClass(), width,
//				width);
//		for (int i = 1; i <= width / 2; i++)
//			w2.setRowVector(i, wl2.getRowVector(i));
//		for (int i = 1; i <= width / 2; i++)
//			w2.setRowVector(width / 2 + i, wh2.getRowVector(i));

		w1.inverse();
		w2.transpose();
		w2.inverse();

		return w1.mult(aMatrix).mult(w2);
	}

	/**
	* Returns a quadrat NumberMatrix that stays left of the to transformed
	* matrix in in a wavelet transformation. To get the right matrix 
	* you have to transpose the returned one (see <code> transpose()</codee>).
	* In case of inverse transformation the left matrix is the
	* inversed of the returned one see <code> inverse()</codee>) and 
	* the right one is the transposed and inversed.
	* 
	* @param numberClass tha class of the returned matrix
	* @param n the dimension of the quadrat matrix
	*            
	* @return a transforming matrix that looks like:
	* 0.5 0.5 0   0   0 0 0 ...
	* 0   0   0.5 0.5 0 0 0 ...
	* .
	* .
	* 1  -1   0   0   0 0 0 ...
	* 0   0   1  -1   0 0 0 ...
	* .
	* .
	*/
	public static NumberMatrix getTransformingMatrix(Class numberClass, int n) {
		int dim = n;
		
		if (dim - Math.floor((double) dim / 2.0) * 2 != 0)
			throw new IllegalArgumentException("Hight should be even number!");

		NumberMatrix wl1 = new NumberMatrix(numberClass, dim,	dim / 2);
		NumberMatrix wh1 = new NumberMatrix(numberClass, dim, dim / 2);
		for (int i = 1; i <= dim / 2; i++) { // buid Wl
			wl1.setEntry(i, 2 * i, 0.5); 		// 0.5 0.5 0   0   0 0 0 0 ...
			wl1.setEntry(i, 2 * i - 1, 0.5);	// 0   0   0.5 0.5 0 0 0 0 ...
		}
		for (int i = 1; i <= dim / 2; i++) { // build Wh
			wh1.setEntry(i, 2 * i, -1);			// 1 -1 0  0 0 0 0 0 ...
			wh1.setEntry(i, 2 * i - 1, 1); 		// 0  0 1 -1 0 0 0 0 ...
		}
		NumberMatrix W = new NumberMatrix(numberClass, dim, dim);
		for (int i = 1; i <= dim / 2; i++)			// combines both matrices
			W.setRowVector(i, wl1.getRowVector(i));
		for (int i = 1; i <= dim / 2; i++)
			W.setRowVector(dim / 2 + i, wh1.getRowVector(i));
		return W;
	}
}
