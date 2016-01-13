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
 * Contains different methods to compute the invers of
 * a given nxn-matrix, which is given by an array of
 * double entries. The methods expect the length of the array to be a
 * square number.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @mm.todo another author implemented the methods gaussOrg,make2DFortranArray,
 * getFrom2DFortranArray, print2DFortran, arrayEquals, and main and has to
 * comment them.
 * @see net.mumie.mathletfactory.math.number.numeric.Invert
 *
 */
public class InvertDouble {

  /**
   * Inverts a given nxn-matrix A by LU Decomposition. This method uses
   * the methods {@link LUDecompositionDouble#ludecomposition ludecomposition}
   * and {@link LUDecompositionDouble#lubacksubstitution lubacksubstitution}
   * and finds the invers column by column by
   * solving the linear equations A*y_j = e_j, where e_j is an identity vector.
   * The solution vector y_j is the j-th column of the invers of A. The
   * algorithm is based on the numerical recipes. This method
   * expects the length of <code>A</code> to be a square number.
   * A detailed description of the algorithm and the numerical background can be
   * found on the <a href="http://www.nr.com">numerical recipes website</a>.
   *
   * @param    A                   a  <code>double[]</code>
   *
   * @return   a <code>double[]</code> holding the invers of <code>A</code>.
   *
   * @throws   ArithmeticException if <code>A</code> is singular.
   *
   * @see net.mumie.mathletfactory.math.number.numeric.numericdouble.LUDecompositionDouble
   */

  public static double[] invertByLUDecomposition(double[] A) {
    int i, j;
    int m = A.length;
    int n = (int) Math.sqrt(m);
    double LU[] = new double[A.length];
    double y[] = new double[m];
    double[] col = new double[n];
    int indx[] = new int[n];
    double[] d = new double[]{1.0};

    //    try {
    LU =LUDecompositionDouble.ludecomposition(A, indx, d); // Decompose the matrix just once.
    //    } catch (Exception e) {
    //      e.printStackTrace();
    //    }

    for (j = 0; j < n; j++) {   // Find inverse column by column by using
                                // lubacksubstitution.
      for (i = 0; i < n; i++) { // Initialize col with the successive
        col[i] = 0.0;           // identity vectors.
      }
      col[j] = 1.0;
      col = LUDecompositionDouble.lubacksubstitution(LU, indx, col);
                                // Use lubacksubstitution.
      for (i = 0; i < n; i++) {
        y[i * n + j] = col[i];  // The j-th column of y gets the values of
      }                         // col.
    }
    return y;                   // The inverse matrix y is returned.
  }


  /**
   * Inverts a given nxn-matrix <code>A</code> by Gauss-Jordan-Elimination
   * with full pivoting. The algorithm is based on the numerical recipes.
   * It expects the length of <code>A</code> to be a square number.
   * A detailed description of the algorithm and the numerical background can be
   * found on the <a href="http://www.nr.com">numerical recipes website</a>.
   *
   * @param    A             a  <code>double[]</code>
   *
   * @return   a <code>double[]</code> holding the invers of <code>A</code>.
   *
   * @throws   ArithmeticException if <code>A</code> is singular.
   *
   */
  public static double[] invertByGauss( double[] A ) {
    int m = A.length;
    int n = (int)Math.sqrt(m);
    double[] a = new double[m];
    for (int i=0; i<m; i++){
      a[i]=A[i];
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
      double big = 0;
      for( int j=0; j < n; j++ ) {    // This is the outer loop of the search
        // for a pivot element.

        if( ipiv[j] != 1 ) {
          for( int k=0; k<n; k++ ) {
            if( ipiv[k] == 0 ) {
              if( Math.abs(a[j*n+k])>=big) {
                big = Math.abs(a[j*n + k]) ;
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
          double temp = a[irow*n + l];
          a[irow*n + l] = a[icol*n + l];
          a[icol*n + l] = temp;
        }
      }
      indxr[i] = irow;                // We are now ready to divide the pivot
      indxc[i] = icol;                // row by the pivot element, located at
      // irow and icol.
      if( a[icol*n + icol]==0 ) throw
          new ArithmeticException( "A singular matrix has no inverse! (2)" );
      double pivinv = 1.0 ;
      pivinv/=(a[icol*n + icol]);
      // pivinv is the inverse of the pivot element.
      a[icol*n + icol] = 1.0;
      for( int l=0; l<n; l++ ) {
        a[icol*n + l]*=(pivinv);
      }
      for( int ll=0; ll<n; ll++ ) {   // Next, we reduce the rows...
        if( ll != icol ) {            // ...except for the pivot one, of course.
          double dum = a[ll*n + icol];
          a[ll*n + icol] = 0 ;
          for( int l=0; l<n; l++ ) {
            a[ll*n + l]-=(a[icol*n + l]* dum);
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
          double temp = a[k*n + indxr[l]];
          a[k*n + indxr[l]] =  a[k*n + indxc[l]];
          a[k*n + indxc[l]] = temp;
        }
    }
    return a;
  }

  public static double[] gaussOrg(double[] matrixA) {
    // conversion done to use original nr-algorithm that uses fortran indexing:
    double[][] matrix = make2DFortranArray(matrixA);

    //print2DFortran(matrix);

    int m = matrixA.length;
    int n = (int)Math.sqrt(m);
    int indxc[] = new int[n+1];         // The integer arrays indxc[],indxr[]
    int indxr[] = new int[n+1];         // and ipiv[] are used for bookkeeping
    int ipiv[]  = new int[n+1];         // on the pivoting.
    int irow = 0, icol = 0;           // irow and icol are used later to store
    // the position of the present pivot
    // element.


    /*Initialize ipiv[] */
    for( int j=1; j<=n; j++ )
      ipiv[j] = 0;

    for( int i=1; i<=n; i++ ) {        // This is the main loop over columns to
      // be reduced.
      double big = 0;
      for( int j=1; j <= n; j++ ) {    // This is the outer loop of the search
        // for a pivot element.

        if( ipiv[j] != 1 ) {
          for( int k=1; k<=n; k++ ) {
            if( ipiv[k] == 0 ) {
              if( Math.abs(matrix[j][k])>=big) {
                big = Math.abs(matrix[j][k]) ;
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

      if( irow != icol ) {
        for( int l=1; l<=n; l++ ) {   // row interchange
          double temp = matrix[irow][l];
          matrix[irow][l] = matrix[icol][l];
          matrix[icol][l] = temp;
        }
      }
      indxr[i] = irow;                // We are now ready to divide the pivot
      indxc[i] = icol;                // row by the pivot element, located at
      // irow and icol.
      if( matrix[icol][icol]==0 ) throw
          new ArithmeticException( "A singular matrix has no inverse! (2)" );
      double pivinv = 1.0/matrix[icol][icol];
      System.out.println(pivinv);
      matrix[icol][icol] = 1.0;
      for( int l=1; l<=n; l++ )
        matrix[icol][l]*=pivinv;
      for( int ll=1; ll<=n; ll++ ) {   // Next, we reduce the rows...
        if( ll != icol ) {            // ...except for the pivot one, of course.
          double dum = matrix[ll][icol];
          matrix[ll][icol] = 0 ;
          for( int l=1; l<=n; l++ ) {
            matrix[ll][l]-=matrix[icol][l]* dum;
          }
        }
      }
    }// end of main loop

    for( int l=n; l >=1; l-- ) {
      if( indxr[l] != indxc[l] )
        for( int k=1; k<=n; k++ ) {    // column interchange
          double temp = matrix[k][indxr[l]];
          matrix[k][indxr[l]] = matrix[k][indxc[l]];
          matrix[k][indxc[l]] = temp;
        }
    }

    //print2DFortran(matrix);

    // reconversion from the fortran indexed two dimensional array:
    getFrom2DFortranArray(matrix,matrixA);
    return matrixA;
  }


  /**
   * oneD is expected to represent a square matrix with entries stored rowwise
   */
  private static double[][] make2DFortranArray(double[] oneD) {
    int m = oneD.length;
    int n = (int)Math.sqrt(m);
    double[][] twoD = new double[n+1][n+1];
    // set all entries in row zero to zero:
    for(int irow=1; irow<=n; irow++)
      twoD[0][irow-1] = 0;
    for(int icol=1; icol<=n; icol++)
      twoD[icol-1][0] = 0;
    for(int irow=1; irow<=n; irow++)
      for(int icol=1; icol<=n; icol++)
        twoD[irow][icol] = oneD[(irow-1)*n + icol-1];
    return twoD;
  }

  public static void getFrom2DFortranArray(double[][] twoDF,double[] oneD) {
    int n = twoDF.length-1;
    for(int irow=1; irow<=n; irow++)
      for(int icol=1; icol<=n; icol++)
        oneD[(irow-1)*n + icol-1] = twoDF[irow][icol];
  }

  private static void print2DFortran(double[][] twoDF) {
    int dimension = twoDF.length-1;
    for(int irow=1; irow<=dimension; irow++){
      System.out.print("(");
      for(int icol=1; icol<=dimension; icol++)
        System.out.print(twoDF[irow][icol]+" ");
      System.out.println(")");
    }
  }

  public static boolean arrayEquals(double[] a, double[] b) {
    for(int i=0; i<a.length; i++)
      if(a[i] != b[i])
        return false;
    return true;
  }

  public static void main(String[] args) {
    double[] matrix = { 1, 1 ,5, 0, 1,1,1,0,4};
    //double[] orgMatrix = {1,4,3,1};


    double[] matrixInvOrg = new double[matrix.length];
    for(int i=0; i<matrix.length; i++)
      matrixInvOrg[i] = matrix[i];
    double[] matrixInv = new double[matrix.length];
    for(int i=0; i<matrix.length; i++)
      matrixInv[i] = matrix[i];

    invertByGauss(matrixInv);
    //gaussOrg(matrixInvOrg);
    //twoDF = make2DFortranArray(matrixA);
    //print2DFortran(twoDF);
    double[] mult = MatrixMultiplikationDouble.multinxn(matrix,matrixInvOrg);

    print2DFortran(make2DFortranArray(matrix));
    print2DFortran(make2DFortranArray(matrixInv));
    print2DFortran(make2DFortranArray(matrixInvOrg));
    //print2DFortran(make2DFortranArray(mult));
  }

}

