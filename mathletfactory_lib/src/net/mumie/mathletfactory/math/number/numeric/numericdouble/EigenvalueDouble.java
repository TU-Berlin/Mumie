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
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;

/**
 * Contains methods for the computation of eigenvalues and eigenvectors of a
 * real symmetric nxn-matrix by using the Jacobi method.
 * The algorithms are based on the numerical recipes. A detailed decription of
 * the algorithms and the numerical background can be found on
 * the <a href="http://www.nr.com">numerical recipes website</a>.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class EigenvalueDouble {
  
  private static double EPSILON = 1e-14;
  
  /**
   * Computes all eigenvalues and a basis of eigenvectors of a real symmetric
   * nxn-matrix <code>A</code> by approximation. The algorithm uses the Jacobi
   * method which consists of a sequence of orthogonal similarity
   * transformations. Each transformation, a so-called Jacobi rotation, is a
   * plane rotation designed to annihilate one of the off-diagonal matrix
   * elements. Successive transformations undo previously set zeros, but the
   * off-diagonal elements nevertheless get smaller and smaller, until the
   * matrix is diagonal to machine precision. Accumulating the product of the
   * transformations as you go gives a matrix of eigenvectors, while the
   * elements of the final diagonal matrix are the eigenvalues.
   *
   * @param    A             a  <code>double[]</code> holding rowwise the nxn
   * input matrix. Elements of <code>A</code> above the diagonal are destroyed.
   * @param    V             a  <code>double[]</code> which is arbitrary on
   * input and holding columnwise the normalized eigenvectors of <code>A</code>
   * on output.
   * @param    diag          a  <code>double[]</code> which is arbitrary on
   * input and holding the eigenvalues of <code>A</code> on output.
   * @throws IllegalArgumentException if <code>A</code> is not a symmetric
   * square matrix.
   * @mm.sideeffects <code>A</code>, <code>V</code>, and <code>diag</code> are
   * changed.
   *
   */
  private static void jacobi( double[] A , double[] V, double[] diag ) {
    if (isSymmetricSquareMatrix(A)) {
      int m = A.length;                   // m=n*n
      int n = (int)Math.sqrt(m);          // matrixA is (n x n) - matrix
      int nrot = 0;
      double[] b = new double[n];
      double[] z = new double[n];
      double tresh, theta, tau, tangens, sm, sinus, hh, gg, g, h, cosinus;
      
      for (int ip=0; ip<n; ip++) {        // Initialize matrixV to the identity
        for (int iq=0; iq<n; iq++) {      // matrix.
          V[ip*n + iq] = 0.0;
        }
        V[ip*n + ip] = 1.0;
      }
      
      for (int ip=0; ip<n; ip++) {        // Initialize b and diag to the diagonal
        // of matrixA, z to zero.
        b[ip] = A[ip*n + ip];
        diag[ip] = A[ip*n + ip];
        z[ip] = 0.0;
      }                                   // Vector z will accumulate terms of the
      // form tangens*a_pq as in equation
      // (11.1.14) --> www.nr.com
      
      for (int i=1; i<=50; i++) {         // loop of the sweeps
        sm = 0.0;
        for (int ip=0; ip<n-1; ip++) {    // Sum off-diagonal elements.
          for (int iq=ip+1; iq<n; iq++) {
            sm = sm + Math.abs(A[ip*n + iq]);
          }
        }
        if ( !(Math.abs(sm) < EPSILON) ) {// If matrix is diagonal --> STOP
          if (i<4)                          // Set the treshold
            // ... on the first three sweeps.
            tresh = (0.2*sm)/(double)m;
          else                              // ... thereafter.
            tresh = 0.0;
          for (int ip=0; ip<n-1; ip++) {    // loop of all off-diagonal elements
            for (int iq=ip+1; iq<n; iq++) {
              gg = 100.0*Math.abs(A[ip*n + iq]);
              // After four sweeps, skip the rotation
              // if the off-diagonal element is small.
              if ( i>4 && Math.abs(diag[ip])+gg == Math.abs(diag[ip])
                  && Math.abs(diag[iq])+gg == Math.abs(diag[iq]) ) {
                A[ip*n + iq] = 0.0;
              }
              else if ( Math.abs(A[ip*n + iq]) > tresh ) {
                hh = diag[iq] - diag[ip];
                if ( Math.abs(hh) + gg == Math.abs(hh) ) {
                  tangens = A[ip*n + iq]/hh;
                  // tangens = 1/(2*theta)
                }
                else {
                  theta = (0.5*hh)/A[ip*n + iq];
                  tangens = 1.0/(Math.abs(theta) + Math.sqrt(1.0 + theta*theta));
                  // Equation (11.1.10) --> www.nr.com
                  if (theta < 0.0) {
                    tangens = -tangens;
                  }
                }
                cosinus = 1.0/(Math.sqrt(1.0 + tangens*tangens));
                // cos = 1/sqrt(1 + tan^2)
                sinus = tangens*cosinus;
                tau = sinus/(1.0 + cosinus);
                // tau = sin/(1 + cos)
                hh = tangens*A[ip*n + iq];
                z[ip] = z[ip] - hh;
                z[iq] = z[iq] + hh;
                diag[ip] = diag[ip] - hh;
                diag[iq] = diag[iq] + hh;
                A[ip*n + iq] = 0.0;
                // Case of rotations 1<= j< p.
                for (int j=0; j<=ip-1; j++) {
                  g = A[j*n + ip];
                  h = A[j*n + iq];
                  A[j*n + ip] = g - (sinus*(h + g*tau));
                  A[j*n + iq] = h + (sinus*(g - h*tau));
                }
                // Case of rotations p < j < q.
                for (int j=ip+1; j<=iq-1; j++) {
                  g = A[ip*n + j];
                  h = A[j*n + iq];
                  A[ip*n + j] = g - (sinus*(h + g*tau));
                  A[j*n + iq] = h + (sinus*(g - h*tau));
                }
                // Case of rotations q < j <= n.
                for (int j=iq+1; j<n; j++) {
                  g = A[ip*n + j];
                  h = A[iq*n + j];
                  A[ip*n + j] = g - (sinus*(h + g*tau));
                  A[iq*n + j] = h + (sinus*(g - h*tau));
                }
                for (int j=0; j<n; j++) {
                  g = V[j*n + ip];
                  h = V[j*n + iq];
                  V[j*n + ip] = g - (sinus*(h + g*tau));
                  V[j*n + iq] = h + (sinus*(g - h*tau));
                }
                ++nrot;                     // increase number of jacobi rotations
              }
            }
          }
          for  (int ip=0; ip<n; ip++) {     // update diag
            b[ip] = b[ip] + z[ip];
            diag[ip] = b[ip];
            z[ip] = 0.0 ;
          }
        }
      }
    }
    else throw new IllegalArgumentException("The given matrix is not a symmetric square matrix.");
  }
  
  /**
   * Checkes whether the given matrix is a symmetric square matrix.
   */
  private static boolean isSymmetricSquareMatrix (double[] matrix){
    int m = matrix.length;
    int n = (int)Math.sqrt(m);
    MNumber[] matrixNumber = new MDouble[m];
    for (int i=0; i<m; i++){
      matrixNumber[i] = new MDouble(matrix[i]);
    }
    NumberMatrix matrix1 = new NumberMatrix(MDouble.class, n, matrixNumber);
    NumberMatrix matrix2 = (NumberMatrix)matrix1.deepCopy().transpose();
    return (MRational.isSquareNumber(m) && matrix1.equals(matrix2));
  }
  
  /**
   * Computes all eigenvalues of a real symmetric
   * nxn-matrix <code>A</code> by approximation. The algorithm uses the Jacobi
   * method.
   *
   * @param    A             a  <code>double[]</code> holding rowwise the
   * symmetric nxn input matrix.
   *
   * @return   a    <code>double[]</code> holding the eigenvalues of
   * <code>A</code>.
   * @throws IllegalArgumentException if <code>A</code> is not a symmetric
   * square matrix.
   *
   */
  public static double[] eigenvalue(double[] A){
    int m = A.length;                   // m=n*n
    int n = (int)Math.sqrt(m);          // matrixA is (n x n) - matrix
    double[] a = new double[m];
    for (int i=0; i<m; i++){
      a[i] = A[i];
    }
    double[] diag = new double[n];
    double[] V = new double[m];
    jacobi(a, V, diag);
    return diag;
  }
  
  /**
   * Computes a basis of eigenvectors of a real symmetric
   * nxn-matrix <code>A</code> by approximation. The algorithm uses the Jacobi
   * method.
   *
   * @param    A             a  <code>double[]</code> holding rowwise the
   * symmetric nxn input matrix.
   *
   * @return   a <code>double[]</code> holding columnwise the normalized
   * eigenvectors of <code>A</code>.
   * @throws IllegalArgumentException if <code>A</code> is not a symmetric
   * square matrix.
   *
   */
  public static double[] eigenvector(double[] A){
    int m = A.length;                   // m=n*n
    int n = (int)Math.sqrt(m);          // matrixA is (n x n) - matrix
    double[] a = new double[m];
    for (int i=0; i<m; i++){
      a[i] = A[i];
    }
    double[] diag = new double[n];
    double[] V = new double[m];
    jacobi(a, V, diag);
    return V;
  }
}
