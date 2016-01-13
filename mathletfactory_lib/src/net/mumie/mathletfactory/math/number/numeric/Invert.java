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
import net.mumie.mathletfactory.math.number.MNumber;

/**
 * Contains different methods to compute the invers of
 * a given nxn-matrix, which is given by an array of
 * {@link MNumber MNumber} entries. The methods
 * expect the length of the array to be a square number.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see net.mumie.mathletfactory.math.number.numeric.numericdouble.InvertDouble
 */

public class Invert {

  /**
   * Inverts a given nxn-matrix A by LU Decomposition. This method uses
   * the methods {@link LUDecomposition#ludecomposition ludecomposition} and
   * {@link LUDecomposition#lubacksubstitution lubacksubstitution} and finds
   * the invers column by column by
   * solving the linear equations A*y_j = e_j, where e_j is an identity vector.
   * The solution vector y_j is the j-th column of the invers of A. The
   * algorithm is based on the numerical recipes.
   * It works correctly with matrix entries of type
   * {@link net.mumie.mathletfactory.math.number.MDouble MDouble} and
   * {@link net.mumie.mathletfactory.math.number.MRational MRational}. This method
   * expects the length of <code>A</code> to be a square number.
   * A detailed description of the algorithm and the numerical background can be
   * found on the <a href="http://www.nr.com">numerical recipes website</a>.
   *
   * @param    A                   a  <code>MNumber[]</code>
   *
   * @return   a <code>MNumber[]</code> holding the invers of <code>A</code>.
   *
   * @throws   ArithmeticException if <code>A</code> is singular.
   *
   * @see LUDecomposition
   */
  public static MNumber[] invertByLUDecomposition(MNumber[] A) {
    int i, j;
    int m = A.length;
    int n = (int)Math.sqrt(m);
    MNumber LU[] = new MNumber[A.length];
    MNumber y[] = new MNumber[m];
    MNumber[] col = new MNumber[n];
    int indx[] = new int[n];
    MNumber d = A[0].create().setDouble(1.0);

    //    try {
    LU = LUDecomposition.ludecomposition( A , indx, d);// Decompose the matrix just once.
    //    }
    //    catch( Exception e ) {
    //      e.printStackTrace();
    //    }

    for (j=0; j<n; j++) {           // Find inverse column by column by using
      // lubacksubstitution.
      for (i=0; i<n; i++) {         // Initialize col with the successive
        // identity vectors.
        col[i] = A[0].create() ;
      }
      col[j] = A[0].create().setDouble(1.0) ;

      col = LUDecomposition.lubacksubstitution( LU, indx, col);
      // Use lubacksubstitution.
      for (i=0; i< n; i++) {
        y[i*n + j] = col[i].copy();// The j-th column of y gets the values of
      }                             // col.
    }
    return y;                       // The inverse matrix y is returned.
  }

  /**
   * Inverts a given nxn-matrix <code>A</code> by Gauss-Jordan-Elimination
   * with full pivoting. The algorithm is based on the numerical recipes.
   * It works correctly with matrix entries of type
   * {@link net.mumie.mathletfactory.math.number.MDouble MDouble} and
   * {@link net.mumie.mathletfactory.math.number.MRational MRational}. This method
   * expects the length of <code>A</code> to be a square number.
   * A detailed description of the algorithm and the numerical background can be
   * found on the <a href="http://www.nr.com">numerical recipes website</a>.
   *
   * @param    A             a  <code>MNumber[]</code>
   *
   * @return   a <code>MNumber[]</code> holding the invers of <code>A</code>.
   *
   * @throws   ArithmeticException if <code>A</code> is singular.
   *
   */

  public static MNumber[] invertByGauss( MNumber[] A ) {
    int m = A.length;
    int n = (int)Math.sqrt(m);
    MNumber[] a = new MNumber[m];
    for (int i=0; i<m; i++){
      a[i] = A[i].copy();
    }
    int indxc[] = new int[n];         // The integer arrays indxc[],indxr[]
    int indxr[] = new int[n];         // and ipiv[] are used for bookkeeping
    int ipiv[]  = new int[n];         // on the pivoting.
    int irow = 0, icol = 0;           // irow and icol are used later to store
    // the position of the present pivot
    // element.

    for( int j=0; j<n; j++ ) {        // Initialize ipiv[].
      ipiv[j] = 0;
    }

    for( int i=0; i<n; i++ ) {        // This is the main loop over columns to
      // be reduced.
      MNumber big = a[0].create() ;
      for( int j=0; j < n; j++ ) {    // This is the outer loop of the search
        // for a pivot element.

        if( ipiv[j] != 1 ) {
          for( int k=0; k<n; k++ ) {
            if( ipiv[k] == 0 ) {
              if( a[j*n+k].absed().greaterOrEqualThan(big ) ) {
                big = a[j*n + k].absed() ;
                irow = j;             // Set irow and icol on the position of
                icol = k;             // the present pivot element.
              }
            }
            else if( ipiv[k] > 1 ) throw
                new ArithmeticException("A singular matrix has no inverse! (1)");
          }
        }
      }
      ++ipiv[icol];

      /* We now have the pivot element, so we interchange rows, if needed,
       to put the pivot element on the diagonal. The columns are not physically
       interchanged, only relabeled: indxc[i], the column of the i-th pivot
       element, is the i-th column that is reduced, while indxr [i] is the row
       in which that pivot element was originally located. If indxr[i]!=indxc[i]
       there is an implied column interchange. With this form of bookkeeping the
       inverse matrix will be scrambled by columns.*/

      if( irow != icol ) {
        for( int l=0; l<n; l++ ) {   // row interchange
          MNumber temp = a[irow*n + l];
          a[irow*n + l] = a[icol*n + l];
          a[icol*n + l] = temp;
        }
      }
      indxr[i] = irow;                // We are now ready to divide the pivot
      indxc[i] = icol;                // row by the pivot element, located at
      // irow and icol.
      if( a[icol*n + icol].isZero() ) throw
          new ArithmeticException( "A singular matrix has no inverse! (2)" );
      MNumber pivinv = a[0].create().setDouble(1.0) ;
      pivinv.div(a[icol*n + icol]);
      // pivinv is the inverse of the pivot element.
      a[icol*n + icol] = a[0].create().setDouble(1.0);
      for( int l=0; l<n; l++ ) {
        a[icol*n + l].mult(pivinv);
      }
      for( int ll=0; ll<n; ll++ ) {   // Next, we reduce the rows...
        if( ll != icol ) {            // ...except for the pivot one, of course.
          MNumber dum = a[ll*n + icol];
          a[ll*n + icol] = a[0].create() ;
          for( int l=0; l<n; l++ ) {
            a[ll*n + l].sub(MNumber.multiply(a[icol*n + l], dum));
          }
        }
      }
    } // This is the end of the main loop over columns of the reduction.

    /* It only remains to unsrcamble the solution in view of the column
     interchanges. We do this by interchanging pairs of columns in the reverse
     order that the permutation was built up. */

    for( int l=n-1; l >=0; l-- ) {
      if( indxr[l] != indxc[l] )
        for( int k=0; k<n; k++ ) {    // column interchange
          MNumber temp = a[k*n + indxr[l]];
          a[k*n + indxr[l]] =  a[k*n + indxc[l]];
          a[k*n + indxc[l]] = temp;
        }
    }
    return a;
  }
}

