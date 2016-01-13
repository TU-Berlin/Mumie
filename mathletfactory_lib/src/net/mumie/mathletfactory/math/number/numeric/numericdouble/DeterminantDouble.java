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
 * Contains different methods to compute the determinant of
 * a given nxn-matrix, which is given by an array of
 * double entries.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class DeterminantDouble {
  
  private static final double EPSILON = 1e-14;
  
  /**
   * Calculates the determinant of <code>A</code> by using
   * {@link LUDecompositionDouble#ludecomposition ludecomposition}. This method
   * expects the length of <code>A</code> to be a square number.
   * A detailed description of the algorithm and the numerical background can be
   * found on the <a href="http://www.nr.com">numerical recipes website</a>.
   *
   * @param    A      a  <code>double[]</code> holding the nxn
   * input matrix rowwise. <code>A</code> remains unchanged.
   *
   * @return   a <code>double</code> the determinant of A.
   *
   */
  public final static double  detByLUDecomposition( double[] A ) {
    int  k;
    int m = A.length;
    int n = (int)Math.sqrt(m);
    double LU[] = new double[A.length];
    int indx[] = new int[n];
    double[] d = new double[]{1.0};
    //    try {
    LU = LUDecompositionDouble.ludecomposition(A, indx, d); // Decompose the matrix.
    //    }
    //    catch(ArithmeticException e){
    //      // Matrix is not invertible
    //      return 0;
    //    }
    //    try {
    for ( k=0; k<n; k++) {         // Multiply the diagonal elements and the
      d[0] = d[0]*LU[k*n + k];             // appropriate sign.
    }
    //    } catch( Exception e ) {
    //      e.printStackTrace();
    //      System.exit(0);
    //    }
    
    return d[0];
  }
  
  /**
   *
   * This method calculates the determinant of a given nxn-matrix by using the
   * recursive (laplace-) calculation. On non-sparse matrices this is done
   * in O(n!). All entries in the traversed array remain unchanged. This method
   * expects the length of the array to be a square number.
   *
   * @param    A    a  <code>double[]</code> holding the nxn
   * input matrix rowwise. <code>A</code> remains unchanged.
   *
   * @return   a <code>double</code> the determinant of A.
   *
   */
  public final static double det(double[] A){
    int n = (int)Math.sqrt(A.length);
    double result;
    if (n==1) {
      result = A[0];
    }
    else if (n==2) {
      //product in main diagonal minus product in other diagonal:
      result = A[0]*A[3] - A[1]*A[2];
    }
    else if (n==3) {
      // "Jaegerzaun" Sarrus formula:
      result = A[0]*A[4]*A[8] +
        A[1]*A[5]*A[6] +
        A[2]*A[3]*A[7] -
        A[2]*A[4]*A[6] -
        A[0]*A[5]*A[7] -
        A[1]*A[3]*A[8];
    }
    else {
      result = 0.0;
      // create submatrix for subdeterminant
      double[] a_i = new double[ (n-1)*(n-1) ];
      
      for(int i=0;i<n;i++){
        
        //get the element of the first row and the i-th column
        double factor =  A[i];
        
        if(!(Math.abs(factor)<EPSILON)){
          int count=0;
          for(int k=0; k<n*(n-1); k++)
            if(k % n != i && k != i)
              a_i[count++] = A[n+k];
          factor = factor*det(a_i);
        }
        
        if(i%2 == 1)
          factor = -factor;
        result = result + factor;
      }
    }
    
    return  result;
  }
}



