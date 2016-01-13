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

import java.util.Random;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.numeric.SolveHomogen;

/**
 * Contains methods for the computation of the eigenvalues and eigenvectors
 * of a symmetric 3x3-{@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix}.
 * The eigenvalues are computed exactly with a
 * <a href="http://www.cg.inf.ethz.ch/~bauer/Algebra/kubisch.html">formula for
 * the solution of cubic equations</a>.
 * The eigenvectors are computed exactly by solving a system
 * of linear equations and if this fails because of
 * numerical inaccuracy the eigenvectors are approximately computed by
 * inverse iteration.
 * The inverse iteration algorithm is based on the numerical recipes.
 * A detailed description of this algorithm and the numerical background can be
 * found on the <a href="http://www.nr.com">numerical recipes website</a>.
 * The number class of the returned eigenvalues and eigenvectors is
 * <code>double</code>.
 * @author Liu
 * @author Mrose
 * @mm.docstatus finished
 */
public class Eigenvalue3x3SymDouble {

  private static double EPSILON = 1e-10;

  /**
   * Checkes whether the given matrix is symmetric and 3x3.
   */
  private static boolean if3x3Sym (NumberMatrix matrix){
    Matrix m = matrix.deepCopy().transpose();
    return (matrix.isSquare() && matrix.getColumnCount()==3 && m.equals(matrix));
  }

  /**
   * Algorithm to simplify the characteristic equation ax³ + bx² + cx + d = 0 of
   * a 3x3-matrix. This equation has either one real and two complex or three real
   * roots. By the linear transformation
   *
   * x = (y - b)/(3a)
   *
   * we get the reduced cubic equation
   *
   * y³ + 3py + q = 0.
   *
   * Hereby is
   *
   * p = 3ac - b²
   * q = 2b³ - 9abc + 27a²d.
   *
   * Method reduceToCubicEquation gets a NumberMatrix and returns {p, q}.
   */
  private static double[] reduceToCubicEquation(NumberMatrix matrix){

    double a = -1.0;
    double b , c , d , p , q ;

    b = matrix.getEntry(1,1).add(matrix.getEntry(2,2)).add(matrix.getEntry(3,3)).getDouble();

    c = MNumber.subtract(
      matrix.getEntry(1,2).square().add(
                           matrix.getEntry(1,3).square()).add(
                           matrix.getEntry(2,3).square()),
      matrix.getEntry(1,1).mult(matrix.getEntry(2,2)).add(
                           matrix.getEntry(1,1).mult(matrix.getEntry(3,3))).add(
                           matrix.getEntry(2,2).mult(matrix.getEntry(3,3)))).getDouble();

    d = MNumber.subtract(
      matrix.getEntry(1,1).mult(
                           matrix.getEntry(2,2)).mult(
                           matrix.getEntry(3,3)).add(
                           matrix.getEntry(1,3).mult(matrix.getEntry(2,1)).
                             mult(matrix.getEntry(3,2))).
        add(matrix.getEntry(1,2).mult(matrix.getEntry(2,3)).mult(matrix.getEntry(3,1))),
      matrix.getEntry(1,1).mult(matrix.getEntry(3,2)).mult(
                           matrix.getEntry(2,3)).add(
                           matrix.getEntry(1,2).mult(matrix.getEntry(2,1)).mult(
                                                        matrix.getEntry(3,3))).add(
                           matrix.getEntry(1,3).mult(matrix.getEntry(2,2)).
                             mult(matrix.getEntry(3,1)))).getDouble();

    p = 3.0*a*c - b*b;

    q = 2*b*b*b - 9*a*b*c + 27*a*a*d;

    return  new double[]{p,q};
  }

  /**
   * Computes the eigenvalues of the symmetric
   * 3x3-{@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix}
   * <code>matrix</code>. The eigenvalues are sorted in increasing order,
   * first the negative, then the positive, and then the zero eigenvalues.
   *
   * @param    matrix              a  symmetric 3x3-<code>NumberMatrix</code>.
   *
   * @return   a <code>double[]</code> holding the eigenvalues of
   * <code>matrix</code>  in increasing order, first the negative, then
   * the positive, and then the zero eigenvalues.
   * @throws IllegalUsageException if <code>matrix</code> is no symmetric 3x3-matrix.
   *
   */
  public static double[] eigenvalue(NumberMatrix matrix){
    // Computes the eigenvalues of a 3x3-matrix by calculating the roots of the
    // reduced cubic equation of the characteristic equation. They are given by
    // x1 = (y1 - b) / (3a)
    // x2 = (y2 - b) / (3a)
    // x3 = (y3 - b) / (3a)
    if (if3x3Sym(matrix)){

      double a = -3.0;
      double b = matrix.getEntry(1,1).getDouble()
        + matrix.getEntry(2,2).getDouble()
        + matrix.getEntry(3,3).getDouble();
      double y1, y2, y3 ;
      double[] reducedCubicEquation = reduceToCubicEquation(matrix);
      double p = reducedCubicEquation[0];
      double q = reducedCubicEquation[1];
      double discriminant = reducedCubicEquation[1]*reducedCubicEquation[1]
        + 4*reducedCubicEquation[0]*reducedCubicEquation[0]
        *reducedCubicEquation[0];//q²+4p³
      double[] x;
      // For D < 0 there are three different real roots:
      // cos(phi) = -q / (2sqrt[-p³])
      // y1 = 2 sqrt[-p] cos(phi/3 )
      // y2 = 2 sqrt[-p] cos(phi/3 + 120°)
      // y3 = 2 sqrt[-p] cos(phi/3 + 240°)
      if (discriminant < 0.0) {
        double cosOfPhi = -q/(2*Math.sqrt(-p*p*p));
        double phi = Math.acos(cosOfPhi);
        y1 = 2.0*Math.sqrt(-p)*Math.cos(phi/3);
        y2 = 2.0*Math.sqrt(-p)*Math.cos(phi/3 + 2.0*Math.PI/3);
        y3 = 2.0*Math.sqrt(-p)*Math.cos(phi/3 + 4.0*Math.PI/3);
      }

        // For D > 0 there is one real and there are two complexe roots:
        // u = 1/2 cubrt[ -4q + 4sqrt[D] ]
        // v = 1/2 cubrt[ -4q - 4sqrt[D] ]
        //
        // y1 = u+v
        // y2 = -(u+v)/2 + (u-v)/2 sqrt[3] i
        // y3 = -(u+v)/2 - (u-v)/2 sqrt[3] i
        //
        // For D = 0 there are three real roots amongst them at least two are
        // equal. Computation is the same as in the case D > 0 but the
        // imaginary part of y2 and y3 is zero.
      else {
        double u, v;
        double d = Math.sqrt(discriminant);
        //1/2 cubrt[ -4q + 4sqrt[D] ]
        if (-4.0*q + 4.0*d < 0.0) {
          u = -0.5*Math.pow(4.0*q - 4.0*d, 1.0/3.0);
        }
        else u = 0.5*Math.pow(-4.0*q + 4.0*d, 1.0/3.0);
        if (-4.0*q - 4.0*d < 0.0) {
          v = -0.5*Math.pow(4.0*q + 4.0*d, 1.0/3.0);
        }
        else v = 0.5*Math.pow(-4.0*q - 4.0*d, 1.0/3.0);
        y1 = u+v;
        y2 = -(u+v)/2.0;
        y3 = -(u+v)/2.0;
      }
      x = new double[]{(y1-b)/a, (y2-b)/a, (y3-b)/a};
      for (int i=0; i<=2; i++) {
        if (Math.abs(x[i]) < EPSILON) {
          x[i] = 0.0;
        }
      }
      return sort(x);
    }
    else throw new IllegalUsageException("The matrix must be a symmetric 3x3-matrix.");
  }

  private static double[] sort(double[] x) {
    if ( x[0]*x[1]*x[2]==0.0 ) {
      //at least one eigenvalue is zero
      if ( x[0]==0.0 && x[1]==0.0 ) {
        return new double[]{x[2], x[0], x[1]};
      }
      else if ( x[0]==0.0 && x[2]==0.0 ) {
        return new double[]{x[1], x[0], x[2]};
      }
      else if ( x[1]==0.0 && x[2]==0.0 ) {
        return new double[]{x[0], x[1], x[2]};
      }
      else if ( x[0]==0.0 ) {
        return new double[]{Math.min(x[1], x[2]), Math.max(x[1], x[2]), x[0]};
      }
      else if ( x[1]==0.0 ) {
        return new double[]{Math.min(x[0], x[2]), Math.max(x[0], x[2]), x[1]};
      }
      else {
        return new double[]{Math.min(x[0], x[1]), Math.max(x[0], x[1]), x[2]};
      }
    }
    else {
      //no eigenvalue is zero
      double helper;
      if ( Math.min(Math.max(x[0], x[1]), x[2])
          == Math.min(Math.min(x[0], x[1]), x[2]) ) {
        helper = Math.max(Math.min(x[0], x[1]), x[2]);
      }
      else helper = Math.min(Math.max(x[0], x[1]), x[2]);
      return new double[]{Math.min(Math.min(x[0], x[1]), x[2]), helper,
          Math.max(Math.max(x[0], x[1]), x[2])};
    }
  }

  /**
   * Computes the norm of a vector.
   */
  private static double getNorm(double[] vec){
    double length = Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
    return length;
  }

  /**
   * Scales a vector.
   */
  private static double[] scaling(double[] v){
    if (getNorm(v)==0.0) throw new IllegalUsageException("zero-vector cannot be scaled!");
    else return new double[]{v[0]/getNorm(v), v[1]/getNorm(v), v[2]/getNorm(v)};
  }

  /**
   * Gets a 3x3-matrix and an eigenvalue l and returns the matrix A-lId
   */
  private static NumberMatrix subEv (NumberMatrix matrix, double evalue){
    MNumber[] entry = matrix.getEntriesAsNumberRef();
    MNumber eigenvalue = matrix.getEntry(1,1).create().setDouble(evalue);
    entry[0] = entry[0].copy().sub(eigenvalue);
    entry[4] = entry[4].copy().sub(eigenvalue);
    entry[8] = entry[8].copy().sub(eigenvalue);
    return new NumberMatrix (MDouble.class, 3, entry);
  }

  private static double[] computeEigenvectors (NumberMatrix matrix){
    if(if3x3Sym(matrix)){
      double[] entry = new double[9];
      double[] first = new double[3], second = new double[3], third = new double[3];
      double[] evalue = eigenvalue(matrix);

      if(evalue[0]==evalue[1] && evalue[1]==evalue[2]){
        return new double[]{ 1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0};
      }
      else {
        if (evalue[0] == evalue[1] ) {
          NumberTuple[] helper0 = SolveHomogen.nullSpace(subEv(matrix,evalue[0]));
          NumberTuple[] helper2 = SolveHomogen.nullSpace(subEv(matrix,evalue[2]));
          first = new double[]{helper0[0].getEntry(1).getDouble(),
              helper0[0].getEntry(2).getDouble(),
              helper0[0].getEntry(3).getDouble()};
          third = new double[]{helper2[0].getEntry(1).getDouble(),
              helper2[0].getEntry(2).getDouble(),
              helper2[0].getEntry(3).getDouble()};
          second = VectorproductDouble.getVectorproduct(first, third);
        }
        else {
          NumberTuple[] helper0 = SolveHomogen.nullSpace(subEv(matrix,evalue[0]));
          NumberTuple[] helper1 = SolveHomogen.nullSpace(subEv(matrix,evalue[1]));
          first = new double[]{helper0[0].getEntry(1).getDouble(),
              helper0[0].getEntry(2).getDouble(),
              helper0[0].getEntry(3).getDouble()};
          second  = new double[]{helper1[0].getEntry(1).getDouble(),
              helper1[0].getEntry(2).getDouble(),
              helper1[0].getEntry(3).getDouble()};
          third = VectorproductDouble.getVectorproduct(first, second);
        }
        if ((first[0]==0.0 && first[1]==0.0 && first[2]==0.0)
            && (second[0]==0.0 && second[1]==0.0 && second[2]==0.0)
            &&  (third[0]==0.0 && third[1]==0.0  && third[2]==0.0)) {
          if (evalue[0]==evalue[2]) {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            second = eigenvectorByInverseIteration(evalue[1], matrix);
            third = VectorproductDouble.getVectorproduct(first, second);
          }
          else {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            third = eigenvectorByInverseIteration(evalue[2], matrix);
            second = VectorproductDouble.getVectorproduct(first, third) ;
          }
        }
        else if ((first[0]==0.0 && first[1]==0.0 && first[2]==0.0)
                 && (second[0]==0.0 && second[1]==0.0 && second[2]==0.0)) {
          if (evalue[0]==evalue[2]) {
            second = eigenvectorByInverseIteration(evalue[1], matrix);
            first = VectorproductDouble.getVectorproduct(second, third) ;
          }
          else {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            second = VectorproductDouble.getVectorproduct(first, third) ;
          }
        }
        else if ((first[0]==0.0 && first[1]==0.0 && first[2]==0.0)
                 && (third[0]==0.0 && third[1]==0.0 && third[2]==0.0)) {
          if (evalue[0]==evalue[1]) {
            third = eigenvectorByInverseIteration(evalue[2], matrix);
            first =VectorproductDouble.getVectorproduct(second, third);
          }
          else {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            third =VectorproductDouble.getVectorproduct(first, second);
          }
        }
        else if ((second[0]==0.0 && second[1]==0.0 && second[2]==0.0)
                 && (third[0]==0.0 && third[1]==0.0 && third[2]==0.0)) {
          if (evalue[0]==evalue[1]) {
            third = eigenvectorByInverseIteration(evalue[2], matrix);
            second =VectorproductDouble.getVectorproduct(first, third);
          }
          else {
            second = eigenvectorByInverseIteration(evalue[1], matrix);
            third =VectorproductDouble.getVectorproduct(first, second);
          }
        }
        else if (first[0]==0.0 && first[1]==0.0 && first[2]==0.0) {
          first = VectorproductDouble.getVectorproduct(second, third) ;
        }
        else if (second[0]==0.0 && second[1]==0.0 && second[2]==0.0) {
          second = VectorproductDouble.getVectorproduct(first, third);
        }
        else if (third[0]==0.0 && third[1]==0.0 && third[2]==0.0) {
          third =VectorproductDouble.getVectorproduct(first, second);
        }
        first = scaling(first);
        second = scaling(second);
        third = scaling(third);
        entry = new double[]{first[0], first[1], first[2],
            second[0], second[1], second[2],
            third[0], third[1], third[2]};
        return entry;
      }
    }
    else throw new IllegalUsageException("The matrix is not a 3x3 symmetric matrix");
  }

  /**
   * Returns an orthonormal system of eigenvectors of the symmetric
   * 3x3-{@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix}
   * <code>matrix</code>. If the
   * direct method (solving a system of linear equations) fails because of
   * numerical inaccuracy the eigenvectors are approximately computed by
   * inverse iteration.
   *
   * @param    matrix              a  symmetric 3x3-<code>NumberMatrix</code>.
   *
   * @return   a <code>double[]</code> holding one after another the
   * eigenvectors of <code>matrix</code>.
   * @throws IllegalUsageException if <code>matrix</code> is no symmetric 3x3-matrix.
   *
   */
  public static double[] eigenvector (NumberMatrix matrix){
    double[] eigenvectors = computeEigenvectors(matrix);
    if ( DeterminantDouble.det(eigenvectors) > 0.0 )
      return eigenvectors;
    else {
      return new double[]{eigenvectors[0], eigenvectors[1], eigenvectors[2],
          eigenvectors[3], eigenvectors[4], eigenvectors[5],
          -eigenvectors[6], -eigenvectors[7], -eigenvectors[8]};
    }
  }

  /**
   * Starts with a scaled randomvector y_0 and solves iterative the system
   * of linear equations A*y_(k+1) = y_k until the changes are less than
   * a certain EPSILON.
   * If l is an eigenvalue of B and A is the matrix (B-l*Id) then the solution
   * vector is (approximately) an eigenvector of the eigenvalue l.
   */
  private static double[] eigenvectorByInverseIteration(double eigenvalue,
                                                        NumberMatrix quadricmatrix){
    NumberMatrix aSubLambda = subEv(quadricmatrix, eigenvalue);
    double[] A = new double[]{aSubLambda.getEntry(1,1).getDouble(),
        aSubLambda.getEntry(1,2).getDouble(), aSubLambda.getEntry(1,3).getDouble(),
        aSubLambda.getEntry(2,1).getDouble(), aSubLambda.getEntry(2,2).getDouble(),
        aSubLambda.getEntry(2,3).getDouble(), aSubLambda.getEntry(3,1).getDouble(),
        aSubLambda.getEntry(3,2).getDouble(), aSubLambda.getEntry(3,3).getDouble()};
    int indx[] = new int[3];
    double[] d = new double[]{1.0};
    double LU[] = new double[9];
    LU = LUDecompositionDouble.ludecomposition( A, indx, d );
    double[] eigenvector = new double[3];
    Random r = new Random();
    double[] y =
      scaling(new double[]{r.nextDouble(), r.nextDouble(), r.nextDouble()});
    eigenvector = (LUDecompositionDouble.lubacksubstitution(LU, indx, y));
    int i = 0;
    while (Math.abs(getNorm(new double[]{y[0]-eigenvector[0],
                            y[1]-eigenvector[1],
                            y[2]-eigenvector[2]})) > EPSILON) {
      for ( int j=0; j<3; j++ ) {
        y[j] = eigenvector[j];
      }
      eigenvector = scaling(LUDecompositionDouble.lubacksubstitution(LU, indx, y));
      i = i+1;
    }
    return scaling(eigenvector);
  }
}



