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
 * Contains methods for the computation of eigenvalues and eigenvectors
 * of a real 2x2-matrix given as array of <code>double</code> entries
 *
 * @author Liu
 * @author Mrose
 * @mm.docstatus finished
 */
public class Eigenvalue2x2Double {
  
  
  private static final double EPSILON = 1e-12;
  /**
   * Checks whether <code>matrix</code> has real eigenvalues.
   *
   * @param    matrix              a  <code>double[]</code> holding rowwise a
   * 2x2-matrix.
   *
   * @return   a <code>boolean</code> which is true if <code>matrix</code> has
   * only real eigenvalues.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is |=4.
   */
  public static boolean realEigenvaluesExist(double[] matrix) {
    // If the matrix is given by
    // [a b]
    // [c d]
    // then real eigenvalues exist, if (a-d)� + 4*b*c >= 0.
    if(matrix.length == 4) { // tests, if the matrix is a 2x2 matrix
      return (matrix[0]-matrix[3])*(matrix[0]-matrix[3])+4*matrix[1]*matrix[2]>=0;
    }
    else throw new IllegalArgumentException("Matrix must be 2x2!");
  }
  
  /**
   * Computes the real eigenvalues of <code>matrix</code>.
   *
   * @param    matrix               a  <code>double[]</code> holding rowwise a
   * 2x2-matrix.
   *
   * @return   a <code>double[]</code> holding the eigenvalues of <code>matrix</code>.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is |=4.
   */
  public static double[] eigenvalue(double[] matrix) {
    // If the matrix is given by
    // [a b]
    // [c d]
    // then in the real case the eigenvalues are given by
    // 1/2*(a+d +- sqrt[(a-d)� + 4*b*c])
    // and in the complex case they are given by
    // 1/2*(a+d +- i*sqrt[-(a-d)� - 4*b*c])
    double firsteigenvalue, secondeigenvalue;
    if( realEigenvaluesExist(matrix) ) {
      firsteigenvalue = 1.0/2.0*(matrix[0]+matrix[3] +
                                   Math.sqrt((matrix[0]-matrix[3])*(matrix[0]-matrix[3]) +
                                               4.0*matrix[1]*matrix[2]));
      secondeigenvalue = 1.0/2.0*(matrix[0]+matrix[3] -
                                    Math.sqrt((matrix[0]-matrix[3])*(matrix[0]-matrix[3]) +
                                                4.0*matrix[1]*matrix[2]));
      return sort(new double[]{firsteigenvalue, secondeigenvalue});
    }
    else throw new IllegalArgumentException("Matrix has no real eigenvalues.");
  }
  
  private static double[] computeRealEigenvector(double[] matrix, double eigenvalue) {
    if ( Math.abs(matrix[0]-eigenvalue) < EPSILON && Math.abs(matrix[1]) < EPSILON ) {
      // return (l-d, c), where l is the eigenvalue
      if ( matrix[2] < 0.0 ) {
        return normalized(new double[]{-eigenvalue+matrix[3], -matrix[2]});
      }
      else return normalized(new double[]{eigenvalue-matrix[3], matrix[2]});
    }
    else {
      // return (b, l-a), where l is the eigenvalue
      if ( eigenvalue-matrix[0] < 0.0 ) {
        return normalized(new double[]{-matrix[1], -eigenvalue+matrix[0]});
      }
      else return normalized(new double[]{matrix[1], eigenvalue-matrix[0]});
    }
  }
  
  private static double[] normalized(double[] vector) {
    double norm = Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1]);
    return new double[]{vector[0]/norm, vector[1]/norm};
  }
  
  /**
   * Computes the normalized eigenvectors which belong to the eigenvalues
   * given by {@link #eigenvalue eigenvalue}.
   * If two different eigenvectors exist, (v_11, v_12, v_21, v_22) is returned,
   * where v_1 is the eigenvector belonging to the first eigenvalue and v_2 is
   * the eigenvector belonging to the second eigenvalue. If only one
   * eigenvector exists, this eigenvector is returned.
   *
   * @param    matrix              a  <code>double[]</code> holding rowwise a
   * 2x2-matrix.
   *
   * @return   a <code>double[]</code> holding the eigenvectors of <code>matrix</code>.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is |=4 or
   * if <code>matrix</code> has only complex eigenvalues.
   */
  public static double[] eigenvector(double[] matrix) {
    if ( realEigenvaluesExist(matrix) ){ // only real eigenvalues
      double[] EV = eigenvalue(matrix);
      if ( Math.abs(EV[0] - EV[1]) < EPSILON ){
        // the same real eigenvalues
        if (Math.abs(matrix[0]-EV[0]) < EPSILON &&
            Math.abs(matrix[1]) < EPSILON && Math.abs(matrix[2]) < EPSILON &&
            Math.abs(matrix[3]-EV[0]) < EPSILON ){
          // two eigenvectors which are a basis of IR� (2-dim eigenspace)
          return new double[]{1.0, 0.0, 0.0, 1.0};
        }
        else return new double[]{computeRealEigenvector(matrix, EV[0])[0],
              computeRealEigenvector(matrix, EV[0])[1], Double.NaN, Double.NaN};
        // only one eigenvector
      }
      else {
        // two different real eigenvalues
        double[] firstEigenvector = computeRealEigenvector(matrix, EV[0]);
        double[] secondEigenvector = computeRealEigenvector(matrix, EV[1]);
        if (firstEigenvector[0]*secondEigenvector[1]
            - firstEigenvector[1]*secondEigenvector[0] > 0) {
          return new double[]{firstEigenvector[0], firstEigenvector[1],
              secondEigenvector[0], secondEigenvector[1]};
        }
        else return new double[]{firstEigenvector[0], firstEigenvector[1],
              -secondEigenvector[0], -secondEigenvector[1]};
      }
    }
    else throw new IllegalArgumentException("Matrix has no real eigenvectors.");
  }
  
  
  private static double[] sort(double[] x){
    if(x[0]*x[1] == 0){
      if(x[0] == 0){
        return new double[]{x[1], x[0]};
      }
      else return new double[]{x[0], x[1]};
    }
    else return new double[]{Math.min(x[1],x[0]),Math.max(x[0],x[1])};
  }
  
  private static boolean isSymmetric(double[] matrix){
    return ( matrix.length == 4 && Math.abs(matrix[1] - matrix[2]) < EPSILON );
  }
  
  /**
   * Computes the orthonormalized eigenvectors of the symmetric
   * 2x2-<code>matrix</code>.
   *
   * @param    matrix              a  <code>double[]</code> holding rowwise a
   * symmetric 2x2-matrix.
   *
   * @return   a <code>double[]</code> holding the orthonormalized
   * eigenvectors of <code>matrix</code>.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is
   * |=4 or if <code>matrix</code> is not symmetric.
   */
  public static double[] eigenvectorOfSymmetricMatrix(double[] matrix) {
    if (isSymmetric(matrix)) {
      if (eigenvector(matrix)[0]*eigenvector(matrix)[3] -
          eigenvector(matrix)[1]*eigenvector(matrix)[2] >= 0 ) {// is det > 0  ?) {
        return new double[]{eigenvector(matrix)[0], eigenvector(matrix)[1],
            eigenvector(matrix)[2], eigenvector(matrix)[3]};
      }
      else return new double[]{eigenvector(matrix)[0], eigenvector(matrix)[1],
            -eigenvector(matrix)[2], -eigenvector(matrix)[3]};
    }
    else throw new IllegalArgumentException("Matrix must be symmetric");
  }
}



