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
 * Contains the methods {@link #ludecomposition ludecomposition} and
 * {@link #lubacksubstitution lubacksubstitution}
 * which can be used for various numerical applications like solving a linear
 * equation or inverting a matrix. The algorithms are based on the numerical
 * recipes. They work correctly with matrix entries of type
 * {@link net.mumie.mathletfactory.math.number.MDouble MDouble} and
 * {@link net.mumie.mathletfactory.math.number.MRational MRational}.
 * A detailed description of the algorithms and the numerical background can be
 * found on the <a href="http://www.nr.com">numerical recipes website</a>.
 *
 * @author Mrose
 * @mm.docstatus finished
 *
 */
public class LUDecomposition {

  private static double EPSILON = 1e-14;
  /**
   * Returns a LU-decomposition of a given nxn-matrix <code>A</code>, such that
   * <code>A</code> = L*U,
   * where L is lower triangular and U is upper triangular. This method
   * expects the length of <code>A</code> to be a square number. The algorithm
   * uses "Crout's method with partial pivoting". That means that not actually
   * <code>A</code> is decomposed into LU form, but rather a rowwise permutation
   * of <code>A</code>.
   * <code>indx</code> and <code>d</code> keep track of what the permutation
   * is, so this decomposition is
   * just as useful as the original one would have been.
   * Since decomposition is not unique it is possible to choose L with diagonal
   * elements = 1. This makes it possible to store L and U in one matrix
   * which is returned. <code>A</code> remains unchanged.
   * The algorithm is based on the numerical recipes.
   * It works correctly with matrix entries of type
   * {@link net.mumie.mathletfactory.math.number.MDouble MDouble} and
   * {@link net.mumie.mathletfactory.math.number.MRational MRational}.
   * A detailed description of the algorithm and the numerical background can be
   * found on the <a href="http://www.nr.com">numerical recipes website</a>.
   *
   * @param    A  a  <code>MNumber[]</code> holding rowwise the nxn
   * input matrix. <code>A</code> remains unchanged. The length
   * of the array <code>A</code> has to be a square number.
   * @param    indx      an arbitrary <code>int[]</code> which will
   * afterwards hold a vector which records the row permutation effected by the
   * partial pivoting.
   * @param    d an arbitrary <code>MNumber</code> which is
   * set +1 or -1 depending on whether the number of row interchanges was even
   * or odd, respectively.
   * @return   a <code>MNumber[]</code> holding the LUDecomposition of <code>A</code>.
   * @throws ArithmeticException if <code>A</code> is singular.
   * @mm.sideeffects <code>indx</code> and <code>d</code> are changed.
   * @see net.mumie.mathletfactory.math.number.numeric.Determinant#detByLUDecomposition detByLUDecomposition
   * @see net.mumie.mathletfactory.math.number.numeric.Invert#invertByLUDecomposition invertByLUDecomposition
   */
  public static MNumber[] ludecomposition( MNumber[] A, int[] indx, MNumber d ) {
    int m = A.length;
    int n = (int)Math.sqrt(m);
    MNumber[] a = new MNumber[m];
    for (int i=0; i<m; i++){
      a[i] = A[i].copy();
    }
    MNumber big, sum, dum;
    MNumber vv[] = new MNumber[n]; // vv[] stores the implicit scaling of each
    // row.
    int imax = -1;                   // No row interchanges yet.

    for (int h=0; h<n; h++) {      // Initialize indx[] which records the
      indx[h] = h;                   // row permutations effected by partial
    }                                // pivoting.

    for (int i=0; i<n; i++) {       // Loop over rows to get the implicit
      // scaling information.
      big = a[0].create();          // Initialize big == 0.0
      for (int j=0; j<n; j++) {
        MNumber temp = a[i*n + j].copy().abs();
        if (temp.greaterThan(big)) {
          big = temp.copy();
        }
      }
      if (big.copy().abs().getDouble() < EPSILON) {
        throw new ArithmeticException( "Singular matrix in method ludecomposition!" );
      }                              // No nonzero largest element.
      vv[i] = MNumber.divide(a[0].create().setDouble(1.0), big);
    }                                // Save the scaling.

    for ( int j=0; j<n; j++ ) {      // this is the loop over columns of
      // Crout's method.
      for (int i=0; i<j; i++) {      // This is equation (2.3.12) --> www.nr.com
        sum = a[i*n + j].copy(); // except for i = j.
        for (int k=0; k<i; k++) {
          sum = sum.copy().sub(MNumber.multiply(a[i*n + k], a[k*n + j]) );
        }
        a[i*n + j] = sum.copy();
      }
      big =a[0].create() ;          // Initialize big for the search for largest
      // pivot element.MNumber
      for (int i=j; i<n; i++) {     // This is equation (2.3.12) --> www.nr.com
        sum = a[i*n + j].copy();// for i = j and equation (2.3.13) for
        for (int k=0; k<j; k++) {   // i = (j+1)...n
          sum = sum.copy().sub(MNumber.multiply(a[i*n + k], a[k*n + j]));
        }
        a[i*n + j] = sum.copy();
        dum = MNumber.multiply(vv[i], sum.copy().absed());
        if (dum.greaterOrEqualThan(big)) {
          big = dum.copy();                // Is the figure of merit for the pivot
          imax = i;                 // better than the best so far?
        }
      }
      if ( j != imax ) {            // Do we need to interchange rows?
        for (int k=0; k<n; k++) {   // yes, do so...
          dum = a[imax*n + k].copy();
          a[imax*n + k] = a[j*n + k].copy();
          a[j*n + k] = dum.copy();
        }
        d.negate();// ... and change the parity of d.
        vv[imax] = vv[j].copy();// Also interchange the scale factor.
      }
      indx[j] = imax;               // indx[i] = j means: i-th and j-th row changed.
      if (a[j*n + j].isZero()) {
        a[j*n + j].setDouble(EPSILON);
        System.out.println("WARNING: Singular matrix in method ludecomposition!");
      }
      if (j != n-1 ) {              // Now, finally, divide by the pivot element.
        dum = MNumber.divide(a[0].create().setDouble(1.0), (a[j*n + j]));
        for ( int i=j+1; i<n; i++) {
          a[i*n + j] = a[i*n + j].copy().mult(dum);
        }
      }                             // Go back for the next column in the reduction.
    }
    return a;
  }

  /**
   * Method for solving a linear equation A*x=b
   *
   * <code>A</code>, <code>b</code> and <code>indx</code> are not modified by
   * this method and can be left in
   * place for successive calls with different right-hand sides b. This method
   * takes into account the possibility that <code>b</code> will begin with many
   * zero elements, so it is efficient for use in matrix inversion.
   *
   * @param    A             a  <code>MNumber[]</code> holding the nxn-matrix
   * A as its LU decomposition, determined by the method
   * {@link #ludecomposition ludecomposition}
   * @param    indx          an <code>int[]</code> holding the permutation
   * vector given by {@link #ludecomposition ludecomposition}
   * @param    b             a  <code>MNumber[]</code> holding the right-hand
   * side vector b
   *
   * @return   a <code>MNumber[]</code> the solution vector x
   *
   */
  public static MNumber[] lubacksubstitution(MNumber[] A,
                                              int[] indx,
                                              MNumber[] b ) {
    int m = A.length;
    int n = (int)Math.sqrt(m);

    MNumber sum;
    int ii = -1;
    MNumber[] x = new MNumber[b.length];
    /* When ii is set to a positive value, it will become the index of the
     first nonvanishing element of b. We now do the forward substitution.
     The only new wrinkle is to unscramble the permutation as we go. */
    for (int i=0; i<b.length; i++){
      x[i] = b[i].copy();
    }
    for (int i = 0; i<n; i++ ) {    // Forward substitution.
      int ip = indx[i];
      sum = x[ip].copy();
      x[ip] = x[i].copy();
      if (ii!=-1)
        for (int j=ii; j<=i-1; j++) {
          sum = sum.sub(MNumber.multiply(A[i*n + j], x[j]));
        }
      else if ( ! (sum.abs().getDouble() < EPSILON) )// A nonzero element was encountered, so
        ii=i;                       // from now on we will have to do the
      x[i] = sum.copy();        // sums in the loop above.
    }
    for (int i=n-1; i>=0; i--) {    // Backsubstitution.
      sum = x[i].copy();
      for (int j=i+1; j<=n-1; j++) {
        sum = sum.sub(MNumber.multiply(A[i*n + j], x[j]));
      }
      x[i] = MNumber.divide(sum, A[i*n + i]);// Was ist wenn A[i*n +i] = +-0???
    }
    return x;
  }
}
