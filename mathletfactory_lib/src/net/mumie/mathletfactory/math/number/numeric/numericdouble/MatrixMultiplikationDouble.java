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

package net.mumie.mathletfactory.math.number.numeric.numericdouble;

/**
 * Contains methods for the matrix multiplication of matrices given rowwise
 * as an array of double entries.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class MatrixMultiplikationDouble {
  
  
  /**
   * Multiplies an nxm-matrix <code>A</code> and an mxp-matrix <code>B</code>.
   * Result is an nxp-matrix.
   * <code>A</code> and <code>B</code> have to be given rowwise. They remain
   * unchanged.
   *
   * @param    A                   a  <code>double[]</code> of length n*m
   * @param    B                   a  <code>double[]</code> of length m*p
   * @param    n                   an <code>int</code>
   * @param    m                   an <code>int</code>
   * @param    p                   an <code>int</code>
   *
   * @return   a <code>double[]</code> of length n*p
   * @throws   IllegalArgumentException if the matrices have the wrong dimensions.
   *
   */
  public static double[] multinxm( double[] A, double[] B, int n, int m, int p ) {
    if (A.length != n*m || B.length != m*p) {
      throw new IllegalArgumentException("Matrices have wrong dimensions.");
    }
    double M[] =  new double[n*p];
    for (int i=0; i<n*p; i++) {
      M[i] = 0.0;
    }
    for (int i=0; i < n; i++) {
      for (int j=0; j < p; j++) {
        for (int k=0; k < m ; k++) {
          M[i*p + j]+=A[i*m + k]*B[k*p + j];
        }
      }
    }
    return M;
  }
  
  /**
   * Multiplies two nxn-matrices <code>A</code> and <code>B</code>.
   * Result is an nxn-matrix.
   * <code>A</code> and <code>B</code> have to be given rowwise. They remain
   * unchanged.
   *
   * @param    A             a  <code>double[]</code> of length n*n
   * @param    B             a  <code>double[]</code> of length n*n
   *
   * @return   a <code>double[]</code> of length n*n
   * @throws   IllegalArgumentException if the matrices have the wrong dimensions.
   *
   */
  public static double[] multinxn( double[] A, double[] B ) {
    int m = A.length;
    int n = (int)Math.sqrt(m);
    return multinxm(A, B, n, n, n);
  }
}
