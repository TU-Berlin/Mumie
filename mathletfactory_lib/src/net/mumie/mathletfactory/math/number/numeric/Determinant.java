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

package net.mumie.mathletfactory.math.number.numeric;

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.DeterminantDouble;

/**
 * Contains different methods to compute the determinant of
 * a given nxn-matrix, which is given by an array of
 * {@link net.mumie.mathletfactory.math.number.MNumber MNumber} entries.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class Determinant {

  private static double EPSILON = 1e-14;

  /**
   * Calculates the determinant of <code>A</code> by using
   * {@link LUDecomposition#ludecomposition ludecomposition}.
   * It works correctly with matrix entries of type
   * {@link net.mumie.mathletfactory.math.number.MDouble MDouble} and
   * {@link net.mumie.mathletfactory.math.number.MRational MRational}. This method
   * expects the length of <code>A</code> to be a square number.
   * A detailed description of the algorithm and the numerical background can be
   * found on the <a href="http://www.nr.com">numerical recipes website</a>.
   *
   * @param    A      a  <code>MNumber[]</code> holding the nxn
   * input matrix rowwise. <code>A</code> remains unchanged.
   *
   * @return   a <code>MNumber</code> the determinant of A.
   *
   */
  public final static MNumber  detByLUDecomposition( MNumber[] A ) {
    int k;
    int m = A.length;
    int n = (int)Math.sqrt(m);
    MNumber LU[] = new MNumber[A.length];
    int indx[] = new int[n];
    MNumber d = A[0].create().setDouble(1.0) ;
    //    try {
    LU = LUDecomposition.ludecomposition(A, indx, d);// Decompose the matrix.
    //    }
    //    catch(ArithmeticException e){      // Matrix is not invertible
    //      return new MDouble();
    //    }
    //    try {
    for ( k=0; k<n; k++) {          // Multiply the diagonal elements and the
      d.mult(LU[k*n + k]);           // appropriate sign.
    }
    //    } catch( Exception e ) {
    //      EchelonForm.debug(A, n, n);
    //      e.printStackTrace();
    //      System.exit(0);
    //    }
    return d;
  }

  /**
   * This method calculates the determinant of a given nxn-matrix by using the
   * recursive (laplace-) calculation. On non-sparse matrices this is done
   * in O(n!). All entries in the traversed array remain unchanged. This method
   * expects the length of the array to be a square number.
   *
   * @param    A    a  <code>MNumber[]</code> holding the nxn
   * input matrix rowwise. <code>A</code> remains unchanged.
   *
   * @return   a <code>MNumber</code> the determinant of A.
   *
   */
  public final static MNumber det(MNumber[] A){
    int n = (int)Math.sqrt(A.length);
    MNumber result = null;

    // do the double case with primitive data types
    if(A[0] instanceof MDouble){
      double[] a = new double[A.length];
      for(int i=0;i<A.length;i++)
        a[i] = A[i].getDouble();
      return NumberFactory.newInstance(A[0].getClass(), DeterminantDouble.det(a));
    }

    if (n==1) {
      result = A[0].copy();
    }
    else if (n==2) {
      //product in main diagonal minus product in other diagonal:
      result = A[0].copy().mult(A[3]).sub(A[1].copy().mult(A[2]));
    }
    else if (n==3) {
      // "Jaegerzaun" Sarrus formula:
      result = A[0].copy().mult(A[4]).mult(A[8]).add(
      A[1].copy().mult(A[5]).mult(A[6])).add(
      A[2].copy().mult(A[3]).mult(A[7])).sub(
      A[2].copy().mult(A[4]).mult(A[6])).sub(
      A[0].copy().mult(A[5]).mult(A[7])).sub(
      A[1].copy().mult(A[3]).mult(A[8]));
    }
    else {
      result = A[0].create();
      // create submatrix for subdeterminant
      MNumber[] a_i = new MNumber[ (n-1)*(n-1) ];

      for(int i=0;i<n;i++){
        //get the element of the first row and the i-th column
        MNumber factor =  A[i].copy();
        if(!(factor.copy().abs().getDouble() < EPSILON)){
          int count=0;
          for(int k=0; k<n*(n-1); k++)
          if(k % n != i && k != i)
             a_i[count++] = A[n+k].copy();
          factor.mult(det(a_i));
        }

        if(i%2 == 1)
          factor.negate();
        result.add(factor);
      }
    }
    return  result;
  }

  /**
   * This method calculates the determinant of a given nxn-matrix by using the
   * recursive (laplace-) calculation. On non-sparse matrices this is done
   * in O(n!). All entries in the traversed array remain unchanged. This method
   * expects the length of the array to be a square number.
   *
   * @param    A    a  <code>MNumber[]</code> holding the nxn
   * input matrix rowwise. <code>A</code> remains unchanged.
   *
   * @return   a <code>MNumber</code> the determinant of A.
   *
   */
  public final static Operation det(Operation[] A){
    int n = (int)Math.sqrt(A.length);
    Operation result = null;

    if (n==1) {
      result = A[0].deepCopy();
    }
    else if (n==2) {
      //product in main diagonal minus product in other diagonal:
      result = A[0].deepCopy().mult(A[3]).subFrom(A[1].deepCopy().mult(A[2]));
    }
    else if (n==3) {
      // "Jaegerzaun" Sarrus formula:
      result = A[0].deepCopy().mult(A[4].deepCopy()).mult(A[8].deepCopy());
      result.addTo(A[1].deepCopy().mult(A[5].deepCopy()).mult(A[6].deepCopy()));
      result.addTo(A[2].deepCopy().mult(A[3].deepCopy()).mult(A[7].deepCopy()));
      result.subFrom(A[2].deepCopy().mult(A[4].deepCopy()).mult(A[6].deepCopy()));
      result.subFrom(A[0].deepCopy().mult(A[5].deepCopy()).mult(A[7].deepCopy()));
      result.subFrom(A[1].deepCopy().mult(A[3].deepCopy()).mult(A[8].deepCopy()));
    }
    else {
      result = new Operation(A[0]);
      result.setDouble(0);
      // create submatrix for subdeterminant
      Operation[] a_i = new Operation[ (n-1)*(n-1) ];

      for(int i=0;i<n;i++){
        //get the element of the first row and the i-th column
        Operation factor =  A[i].deepCopy();
       
          int count=0;
          for(int k=0; k<n*(n-1); k++)
          if(k % n != i && k != i)
             a_i[count++] = A[n+k].deepCopy();
          factor.mult(det(a_i));

        if(i%2 == 1)
          factor.negate();
        result.addTo(factor);
      }
    }
    return  result;
  }
}



