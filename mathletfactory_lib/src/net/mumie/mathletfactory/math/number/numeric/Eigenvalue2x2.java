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

import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 * Contains methods for the computation of eigenvalues and eigenvectors
 * of a real 2x2-matrix given as an array of
 * {@link MNumber} entries.
 *
 * @author Liu
 * @author Mrose
 * @mm.docstatus finished
 */
public class Eigenvalue2x2 {

  private static final double EPSILON = 1e-12;
  /**
   * Checks whether <code>matrix</code> has real eigenvalues.
   *
   * @param    matrix              a  <code>MNumber[]</code> holding rowwise a
   * 2x2-matrix.
   *
   * @return   a <code>boolean</code> which is true if <code>matrix</code> has
   * only real eigenvalues.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is |=4.
   */
  public static boolean realEigenvaluesExist(MNumber[] matrix) {
    // If the matrix is given by
    // [a b]
    // [c d]
    // then real eigenvalues exist, if (a-d)� + 4*b*c >= 0.
    MNumber zero = matrix[0].create().setDouble(0.0);
    if(matrix.length == 4) { // tests, if the matrix is a 2x2 matrix
      return
        MNumber.add(MNumber.square(MNumber.subtract(matrix[0], matrix[3])),
                     MNumber.multiply(matrix[0].create().setDouble(4.0),
                                       MNumber.multiply(matrix[1], matrix[2]))
                    ).greaterOrEqualThan(zero);
    }
    else throw new IllegalArgumentException("Matrix must be 2x2!");
  }

  /**
   * Computes the (real or complex) eigenvalues of <code>matrix</code>.
   *
   * @param    matrix               a  <code>MNumber[]</code> holding rowwise a
   * 2x2-matrix.
   *
   * @return   a <code>MNumber[]</code> holding the eigenvalues of <code>matrix</code>.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is |=4.
   */
  public static MNumber[] eigenvalue(MNumber[] matrix) {
    // If the matrix is given by
    // [a b]
    // [c d]
    // then in the real case the eigenvalues are given by
    // 1/2*(a+d +- sqrt[(a-d)� + 4*b*c])
    // and in the complex case they are given by
    // 1/2*(a+d +- i*sqrt[-(a-d)� - 4*b*c])
    MNumber four = matrix[0].create().setDouble(4.0);
    MNumber firsteigenvalue, secondeigenvalue;

    if( realEigenvaluesExist(matrix) ) {
      firsteigenvalue = MNumber.multiply(
        MNumber.add(MNumber.add(matrix[0], matrix[3]), MNumber.squareRoot(
                       MNumber.add(MNumber.square(MNumber.subtract(matrix[0],
                                                                      matrix[3])),
                                    MNumber.multiply(four, MNumber.multiply(
                                                        matrix[1], matrix[2]))))),
        matrix[0].create().setDouble(0.5));
      secondeigenvalue = MNumber.multiply(
        MNumber.subtract(MNumber.add(matrix[0], matrix[3]), MNumber.squareRoot(
                            MNumber.add(MNumber.square(MNumber.subtract(matrix[0],
                                                                           matrix[3])),
                                         MNumber.multiply(four, MNumber.multiply(
                                                             matrix[1], matrix[2]))))),
        matrix[0].create().setDouble(0.5));
    }
    else {
      firsteigenvalue = new MComplex(
        MNumber.multiply(MNumber.add(matrix[0], matrix[3]),
                          matrix[0].create().setDouble(0.5)).getDouble(),
        MNumber.multiply(MNumber.squareRoot(
                            MNumber.add(MNumber.square(
                                           MNumber.subtract(matrix[0], matrix[3])),
                                         MNumber.multiply(four, MNumber.multiply(
                                                             matrix[1],
                                                             matrix[2]))).negated()),
                          matrix[0].create().setDouble(0.5)).getDouble());
      secondeigenvalue = firsteigenvalue.conjugated();
    }
    return sort(matrix, new MNumber[]{firsteigenvalue, secondeigenvalue});
  }

  private static MNumber[] computeRealEigenvector(MNumber[] matrix, MNumber eigenvalue) {
    if (MNumber.subtract(matrix[0], eigenvalue).abs().getDouble() < EPSILON &&
        matrix[1].abs().getDouble() < EPSILON ) { // return (l-d, c), where l is the eigenvalue
      if ( matrix[2].getDouble() < 0.0 ) {
        return normalized(new MNumber[]{MNumber.subtract(eigenvalue, matrix[3]).negated(),
                matrix[2].negated()});
      }
      return normalized(new MNumber[]{MNumber.subtract(eigenvalue, matrix[3]),
              matrix[2]});
    }
    else { // return (b, l-a), where l is the eigenvalue
      if (MNumber.subtract(eigenvalue, matrix[0]).getDouble() < 0.0 ) {
        return normalized(new MNumber[]{matrix[1].negated(),
                MNumber.subtract(eigenvalue, matrix[0]).negated()});
      }
      else return normalized(new MNumber[]{matrix[1],
                MNumber.subtract(eigenvalue, matrix[0])});
    }
  }

  private static MNumber[] normalized(MNumber[] vector) {
    MNumber norm = MNumber.squareRoot(MNumber.add(MNumber.square(vector[0]),
                                                     MNumber.square(vector[1])));
    return new MNumber[]{MNumber.divide(vector[0], norm),
        MNumber.divide(vector[1], norm)};
  }

  /**
   * Computes the normalized eigenvectors which belong to the eigenvalues
   * given by {@link #eigenvalue eigenvalue}.
   * If two different eigenvectors exist, (v_11, v_12, v_21, v_22) is returned,
   * where v_1 is the eigenvector belonging to the first eigenvalue and v_2 is
   * the eigenvector belonging to the second eigenvalue. If only one
   * eigenvector exists, this eigenvector is returned.
   *
   * @param    matrix              a  <code>MNumber[]</code> holding rowwise a
   * 2x2-matrix.
   *
   * @return   a <code>MNumber[]</code> holding the eigenvectors of <code>matrix</code>.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is |=4.
   */
  public static MNumber[] eigenvector(MNumber[] matrix) {
    if ( realEigenvaluesExist(matrix) ){ // only real eigenvalues
      MNumber[] EV = eigenvalue(matrix);
      if ( EV[0].equals(EV[1]) ){
        // the same real eigenvalues
        if (MNumber.subtract(matrix[0], EV[0]).abs().getDouble() < EPSILON &&
            matrix[1].abs().getDouble() < EPSILON && matrix[2].abs().getDouble() < EPSILON &&
            MNumber.subtract(matrix[3], EV[0]).abs().getDouble() < EPSILON ){
          // two eigenvectors which are a basis of IR� (2-dim eigenspace)
          return new MNumber[]{matrix[0].create().setDouble(1.0),
              matrix[0].create().setDouble(0.0),
              matrix[0].create().setDouble(0.0),
              matrix[0].create().setDouble(1.0)};
        }
        else return new MNumber[]{computeRealEigenvector(matrix, EV[0])[0],
              computeRealEigenvector(matrix, EV[0])[1],
              matrix[0].create().setDouble(Double.NaN),
              matrix[0].create().setDouble(Double.NaN)};
        // only one eigenvector
      }
      else {
        // two different real eigenvalues
        MNumber[] firstEigenvector = computeRealEigenvector(matrix, EV[0]);
        MNumber[] secondEigenvector = computeRealEigenvector(matrix, EV[1]);
        if (firstEigenvector[0].getDouble()*secondEigenvector[1].getDouble()
            - firstEigenvector[1].getDouble()*secondEigenvector[0].getDouble() > 0) {
          return new MNumber[]{firstEigenvector[0],
              firstEigenvector[1],
              secondEigenvector[0],
              secondEigenvector[1]};
        }
        else return new MNumber[]{firstEigenvector[0],
              firstEigenvector[1],
              secondEigenvector[0].negate(),
              secondEigenvector[1].negate()
		      };
      }
    }
    else  {// only complex eigenvalues
      MNumber re = new MDouble(((MComplex)eigenvalue(matrix)[0]).getRe());
      MNumber im = new MDouble(((MComplex)eigenvalue(matrix)[0]).getIm());
      MNumber v12 = new MComplex( // ((a-re)� + im�, 0)
        MNumber.add(MNumber.square(MNumber.subtract(matrix[0], re)),
                     MNumber.square(im)).getDouble(),
        matrix[0].create().setDouble(0.0).getDouble());
      MNumber v11 = new MComplex( // (-a*b + b*re, b*im )
        MNumber.multiply(matrix[0].create().setDouble(-1.0),
                          MNumber.multiply(matrix[1], MNumber.add(matrix[0], re))).getDouble(),
        MNumber.multiply(matrix[1], im).getDouble());
      MNumber v21 = v11.conjugated();
      return new MNumber[]{v11, v12, v21, v12};
    }
  }

  private static MNumber[] sort(MNumber[] matrix, MNumber[] x){
    if (realEigenvaluesExist(matrix)) {
      if(x[0].copy().mult(x[1]).abs().getDouble() < EPSILON){
        if(x[0].abs().getDouble() < EPSILON){
          return new MNumber[]{x[1],x[0]};
        }
        else return new MNumber[]{x[0],x[1]};
      }
      else if (x[1].lessThan(x[0]))
        return new MNumber[]{x[1],x[0]};
      else return new MNumber[]{x[0],x[1]};
    }
    return x;
  }

  private static boolean isSymmetric(MNumber[] matrix){
    return ( matrix.length == 4 && matrix[1].equals(matrix[2]) );
  }

  /**
   * Computes the orthonormalized eigenvectors of the symmetric
   * 2x2-<code>matrix</code>.
   *
   * @param    matrix              a  <code>MNumber[]</code> holding rowwise a
   * symmetric 2x2-matrix.
   *
   * @return   a <code>MNumber[]</code> holding the orthonormalized
   * eigenvectors of <code>matrix</code>.
   * @throws IllegalArgumentException if the length of <code>matrix</code> is
   * |=4 or if <code>matrix</code> is not symmetric.
   */
  public static MNumber[] eigenvectorOfSymmetricMatrix(MNumber[] matrix) {
    if (isSymmetric(matrix)) {
      if ( MNumber.subtract( // is det > 0 ?
            MNumber.multiply(eigenvector(matrix)[0],
                              eigenvector(matrix)[3]),
            MNumber.multiply(eigenvector(matrix)[1],
                              eigenvector(matrix)[2])).greaterThan(
            matrix[0].create().setDouble(0.0)) ) {
        return eigenvector(matrix);
      }
      else return new MNumber[]{eigenvector(matrix)[0], eigenvector(matrix)[1],
            eigenvector(matrix)[2].negate(), eigenvector(matrix)[3].negate()};
    }
    else throw new IllegalArgumentException("Matrix must be symmetric.");
  }
}




