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

import java.util.Random;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.math.number.MRealNumber;

/**
 * Contains methods for the computation of the eigenvalues and eigenvectors
 * of a symmetric 3x3-{@link NumberMatrix}.
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
 * {@link MNumber}.
 * @author Liu
 * @author Mrose
 * @mm.docstatus finished
 */
public class Eigenvalue3x3Sym {

  private static double EPSILON = 1e-7;
  private static MNumber zero = new MDouble(0.0);

  /**
   * Checkes whether the given matrix is symmetric and 3x3.
   */
  private static boolean if3x3Sym(NumberMatrix matrix) {
    Matrix m = matrix.deepCopy().transpose();
    return (
      matrix.isSquare() && matrix.getColumnCount() == 3 && m.equals(matrix));
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
  private static MNumber[] reduceToCubicEquation(NumberMatrix matrix) {

    MNumber a = matrix.getEntry(1, 1).create().setDouble(-1.0);
    MNumber b, c, d, p, q;

    b =
      matrix.getEntry(1, 1).add(matrix.getEntry(2, 2)).add(
        matrix.getEntry(3, 3));

    c =
      MNumber.subtract(
        matrix.getEntry(1, 2).square().add(
          matrix.getEntry(1, 3).square()).add(
          matrix.getEntry(2, 3).square()),
        matrix
          .getEntry(1, 1)
          .mult(matrix.getEntry(2, 2))
          .add(matrix.getEntry(1, 1).mult(matrix.getEntry(3, 3)))
          .add(matrix.getEntry(2, 2).mult(matrix.getEntry(3, 3))));

    d =
      MNumber.subtract(
        matrix
          .getEntry(1, 1)
          .mult(matrix.getEntry(2, 2))
          .mult(matrix.getEntry(3, 3))
          .add(
            matrix.getEntry(1, 3).mult(matrix.getEntry(2, 1)).mult(
              matrix.getEntry(3, 2)))
          .add(
            matrix.getEntry(1, 2).mult(matrix.getEntry(2, 3)).mult(
              matrix.getEntry(3, 1))),
        matrix
          .getEntry(1, 1)
          .mult(matrix.getEntry(3, 2))
          .mult(matrix.getEntry(2, 3))
          .add(
            matrix.getEntry(1, 2).mult(matrix.getEntry(2, 1)).mult(
              matrix.getEntry(3, 3)))
          .add(
            matrix.getEntry(1, 3).mult(matrix.getEntry(2, 2)).mult(
              matrix.getEntry(3, 1))));

    p =
      (
        ((matrix.getEntry(1, 1).create().setDouble(3.0)).mult(a)).mult(
          c)).sub(
        b.copy().square());

    q =
      (matrix
        .getEntry(1, 1)
        .create()
        .setDouble(2.0)
        .mult(b.copy().power(matrix.getEntry(1, 1).create().setDouble(3.0))))
        .sub(
          matrix.getEntry(1, 1).create().setDouble(9.0).mult(a).mult(b).mult(
            c))
        .add(
          matrix.getEntry(1, 1).create().setDouble(27.0).mult(a.square()).mult(
            d));
    return new MNumber[] { p, q };
  }

  /**
   * Computes the eigenvalues of the symmetric
   * 3x3-{@link NumberMatrix}
   * <code>matrix</code>. The eigenvalues are sorted in
   * increasing order, first the negative, then
   * the positive, and then the zero eigenvalues.
   *
   * @param    matrix              a  symmetric 3x3-<code>NumberMatrix</code>.
   *
   * @return   a <code>MNumber[]</code> holding the eigenvalues of
   * <code>matrix</code>  in increasing order, first the negative, then
   * the positive, and then the zero eigenvalues.
   * @throws IllegalUsageException if <code>matrix</code> is no symmetric 3x3-matrix.
   *
   */
  public static MNumber[] eigenvalue(NumberMatrix matrix) {
    // Computes the eigenvalues of a 3x3-matrix by calculating the roots of the
    // reduced cubic equation of the characteristic equation. They are given by
    // x1 = (y1 - b) / (3a)
    // x2 = (y2 - b) / (3a)
    // x3 = (y3 - b) / (3a)
    if (if3x3Sym(matrix)) {

      MNumber a = matrix.getEntry(1, 1).create().setDouble(-3.0);
      MNumber b =
        matrix.getEntry(1, 1).add(matrix.getEntry(2, 2)).add(
          matrix.getEntry(3, 3));
      MNumber y1, y2, y3;
      MNumber[] reducedCubicEquation = reduceToCubicEquation(matrix);
      MNumber p = reducedCubicEquation[0];
      MNumber q = reducedCubicEquation[1];
      MNumber discriminant =
        reducedCubicEquation[1].copy().square().add(
          matrix.getEntry(1, 1).create().setDouble(4.0).mult(
            reducedCubicEquation[0].copy().power(
              matrix.getEntry(1, 1).create().setDouble(3.0))));
      //q²+4p³
      MNumber[] x;
      // For D < 0 there are three different real roots:
      // cos(phi) = -q / (2*sqrt[-p³])
      // y1 = 2 sqrt[-p] cos(phi/3 )
      // y2 = 2 sqrt[-p] cos(phi/3 + 120°)
      // y3 = 2 sqrt[-p] cos(phi/3 + 240°)
      if (discriminant.getDouble() < 0.0) {
        MNumber cosOfPhi =
          (q.copy().negate()).div(
            matrix.getEntry(1, 1).create().setDouble(2.0).mult(
              p
                .copy()
                .power(matrix.getEntry(1, 1).create().setDouble(3.0))
                .negate()
                .squareRoot()));
        MNumber phi = cosOfPhi.copy().arccos();
        MNumber phiDiv3 =
          phi.copy().div(matrix.getEntry(1, 1).create().setDouble(3.0));
        y1 =
          (matrix.getEntry(1, 1).create().setDouble(2.0))
            .mult(((p.copy()).negate()).squareRoot())
            .mult((phiDiv3.copy()).cos());
        y2 =
          (matrix.getEntry(1, 1).create().setDouble(2.0))
            .mult(((p.copy()).negate()).squareRoot())
            .mult(
              (phiDiv3.copy())
                .add(
                  matrix.getEntry(1, 1).create().setDouble(
                    2.0 * Math.PI / 3.0))
                .cos());
        y3 =
          (matrix.getEntry(1, 1).create().setDouble(2.0))
            .mult(((p.copy()).negate()).squareRoot())
            .mult(
              (phiDiv3.copy())
                .add(
                  matrix.getEntry(1, 1).create().setDouble(
                    4.0 * Math.PI / 3.0))
                .cos());
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
        MNumber u, v;
        MNumber d = MNumber.squareRoot(discriminant);
        //1/2 cubrt[ -4q + 4sqrt[D] ]
        if (matrix
          .getEntry(1, 1)
          .create()
          .setDouble(-4.0)
          .mult(q)
          .add(matrix.getEntry(1, 1).create().setDouble(4.0).mult(d))
          .lessThan(zero)) {
          u =
            matrix.getEntry(1, 1).create().setDouble(-0.5).mult(
              MNumber.power(
                matrix.getEntry(1, 1).create().setDouble(4.0).mult(q).sub(
                  matrix.getEntry(1, 1).create().setDouble(4.0).mult(d)),
                new MRational(1, 3)));

        }
        else {
          u =
            matrix.getEntry(1, 1).create().setDouble(0.5).mult(
              MNumber.power(
                matrix.getEntry(1, 1).create().setDouble(-4.0).mult(q).add(
                  matrix.getEntry(1, 1).create().setDouble(4.0).mult(d)),
                new MRational(1, 3)));
        }
        if (matrix
          .getEntry(1, 1)
          .create()
          .setDouble(-4.0)
          .mult(q)
          .sub(matrix.getEntry(1, 1).create().setDouble(4.0).mult(d))
          .lessThan(zero)) {
          v =
            matrix.getEntry(1, 1).create().setDouble(-0.5).mult(
              matrix
                .getEntry(1, 1)
                .create()
                .setDouble(4.0)
                .mult(q)
                .add(matrix.getEntry(1, 1).create().setDouble(4.0).mult(d))
                .power(new MRational(1, 3)));
        }
        else {
          v =
            matrix.getEntry(1, 1).create().setDouble(0.5).mult(
              matrix
                .getEntry(1, 1)
                .create()
                .setDouble(-4.0)
                .mult(q)
                .sub(matrix.getEntry(1, 1).create().setDouble(4.0).mult(d))
                .power(new MRational(1, 3)));
        }
        y1 = u.copy().add(v);
        y2 =
          u.copy().add(v).negate().div(
            matrix.getEntry(1, 1).create().setDouble(2.0));
        y3 =
          u.copy().add(v).negate().div(
            matrix.getEntry(1, 1).create().setDouble(2.0));
      }

      x =
        new MNumber[] {
          (y1.copy().sub(b)).div(a),
          (y2.copy().sub(b)).div(a),
          (y3.copy().sub(b)).div(a)};
      for (int i = 0; i <= 2; i++) {
        if (x[i].absed().getDouble() < EPSILON) {
          x[i] = matrix.getEntry(1, 1).create().setDouble(0.0);
        }
      }
      return sort(x);
      //      return x;
    }
    else
      throw new IllegalUsageException("The matrix must be a symmetric 3x3-matrix.");
  }

  //  public static MNumber[] eigenvalue(NumberMatrix matrix){
  //      return sort(unsortedEigenvalue(matrix));
  //  }

  private static MNumber[] sort(MNumber[] x) {
    //Sorts the eigenvalues in the following way:
    //First the positive, then the negative and last the zero eigenvalues.
    if (MNumber.multiply(x[0], MNumber.multiply(x[1], x[2])).isZero()) {
      //at least one eigenvalue is zero
      if (x[0].isZero() && x[1].isZero()) {
        return new MNumber[] { x[2], x[0], x[1] };
      }
      else if (x[0].isZero() && x[2].isZero()) {
        return new MNumber[] { x[1], x[0], x[2] };
      }
      else if (x[1].isZero() && x[2].isZero()) {
        return new MNumber[] { x[0], x[1], x[2] };
      }
      else if (x[0].isZero()) {
        return new MNumber[] { max(x[1], x[2]), min(x[1], x[2]), x[0] };
      }
      else if (x[1].isZero()) {
        return new MNumber[] { max(x[0], x[2]), min(x[0], x[2]), x[1] };
      }
      else {
        return new MNumber[] { max(x[0], x[1]), min(x[0], x[1]), x[2] };
      }
    }
    else {
      // no eigenvalue is zero
      MNumber helper;
      if (min(max(x[0], x[1]), x[2]).equals(min(min(x[0], x[1]), x[2]))) {
        // x[2] is the minimum
        helper = max(min(x[0], x[1]), x[2]); // helper is the middle
      }
      else
        helper = min(max(x[0], x[1]), x[2]);
      //x[0] or x[1] is the minimum, helper is the middle
      return new MNumber[] {
        max(max(x[0], x[1]), x[2]),
        helper,
        min(min(x[0], x[1]), x[2])};
    }
  }

  private static MNumber min(MNumber aNumber, MNumber aSecondNumber) {
    return (MNumber) MRealNumber.min(
      (MRealNumber) aNumber,
      (MRealNumber) aSecondNumber);
  }

  private static MNumber max(MNumber aNumber, MNumber aSecondNumber) {
    return (MNumber) MRealNumber.max(
      (MRealNumber) aNumber,
      (MRealNumber) aSecondNumber);
  }

  /**
   * Computes the norm of a vector.
   */
  private static MNumber getNorm(MNumber[] vec) {
    MNumber length =
      vec[0]
        .copy()
        .square()
        .add(vec[1].copy().square())
        .add(vec[2].copy().square())
        .squareRoot();
    return length;
  }

  /**
   * Scales a vector.
   */
  private static MNumber[] scaling(MNumber[] v) {
    if (getNorm(v).isZero())
      throw new IllegalUsageException("zero-vector cannot be scaled!");
    else
      return new MNumber[] {
        MNumber.divide(v[0], getNorm(v)),
        MNumber.divide(v[1], getNorm(v)),
        MNumber.divide(v[2], getNorm(v))};
  }

  /**
   * Gets a 3x3-matrix and an eigenvalue l and returns the matrix A-lId
   */
  private static NumberMatrix subEv(NumberMatrix matrix, MNumber evalue) {
    MNumber[] entry = matrix.getEntriesAsNumberRef();
    entry[0] = entry[0].copy().sub(evalue);
    entry[4] = entry[4].copy().sub(evalue);
    entry[8] = entry[8].copy().sub(evalue);
    return new NumberMatrix(entry[0].getClass(), 3, entry);
  }

  private static MNumber[] computeEigenvectors(NumberMatrix matrix) {
    if (if3x3Sym(matrix)) {
      MNumber[] entry = new MNumber[9];
      MNumber[] first = new MNumber[3],
        second = new MNumber[3],
        third = new MNumber[3];
      MNumber[] evalue = eigenvalue(matrix);

      if (Math.abs(evalue[0].getDouble() - evalue[1].getDouble()) < EPSILON
        && Math.abs(evalue[1].getDouble() - evalue[2].getDouble()) < EPSILON) {
        double[] doubleentry =
          new double[] { 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0 };
        for (int i = 0; i < 9; i++) {
          entry[i] = matrix.getEntry(1, 1).create().setDouble(doubleentry[i]);
        }
        return entry;
      }
      else {
        if (Math.abs(evalue[0].getDouble() - evalue[1].getDouble())
          < EPSILON) {
          NumberTuple[] helper0 =
            SolveHomogen.nullSpace(subEv(matrix, evalue[0]));
          NumberTuple[] helper2 =
            SolveHomogen.nullSpace(subEv(matrix, evalue[2]));
          first = helper0[0].getEntriesAsNumberRef();
          third = helper2[0].getEntriesAsNumberRef();
          second = Vectorproduct.getVectorproduct(first, third);
        }
        else {
          NumberTuple[] helper0 =
            SolveHomogen.nullSpace(subEv(matrix, evalue[0]));
          NumberTuple[] helper1 =
            SolveHomogen.nullSpace(subEv(matrix, evalue[1]));
          first = helper0[0].getEntriesAsNumberRef();
          second = helper1[0].getEntriesAsNumberRef();
          third = Vectorproduct.getVectorproduct(first, second);
        }
        if ((Math.abs(first[0].getDouble()) < EPSILON
          && Math.abs(first[1].getDouble()) < EPSILON
          && Math.abs(first[2].getDouble()) < EPSILON)
          && (Math.abs(second[0].getDouble()) < EPSILON
            && Math.abs(second[1].getDouble()) < EPSILON
            && Math.abs(second[2].getDouble()) < EPSILON)
          && (Math.abs(third[0].getDouble()) < EPSILON
            && Math.abs(third[1].getDouble()) < EPSILON
            && Math.abs(third[2].getDouble()) < EPSILON)) {
          if (Math.abs(evalue[0].getDouble() - evalue[2].getDouble())
            < EPSILON) {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            second = eigenvectorByInverseIteration(evalue[1], matrix);
            third = Vectorproduct.getVectorproduct(first, second);
          }
          else {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            third = eigenvectorByInverseIteration(evalue[2], matrix);
            second = Vectorproduct.getVectorproduct(first, third);
          }
        }
        else if (
          (Math.abs(first[0].getDouble()) < EPSILON
            && Math.abs(first[1].getDouble()) < EPSILON
            && Math.abs(first[2].getDouble()) < EPSILON)
            && (Math.abs(second[0].getDouble()) < EPSILON
              && Math.abs(second[1].getDouble()) < EPSILON
              && Math.abs(second[2].getDouble()) < EPSILON)) {
          if (Math.abs(evalue[0].getDouble() - evalue[2].getDouble())
            < EPSILON) {
            second = eigenvectorByInverseIteration(evalue[1], matrix);
            first = Vectorproduct.getVectorproduct(second, third);
          }
          else {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            second = Vectorproduct.getVectorproduct(first, third);
          }
        }
        else if (
          (Math.abs(first[0].getDouble()) < EPSILON
            && Math.abs(first[1].getDouble()) < EPSILON
            && Math.abs(first[2].getDouble()) < EPSILON)
            && (Math.abs(third[0].getDouble()) < EPSILON
              && Math.abs(third[1].getDouble()) < EPSILON
              && Math.abs(third[2].getDouble()) < EPSILON)) {
          if (Math.abs(evalue[0].getDouble() - evalue[1].getDouble())
            < EPSILON) {
            third = eigenvectorByInverseIteration(evalue[2], matrix);
            first = Vectorproduct.getVectorproduct(second, third);
          }
          else {
            first = eigenvectorByInverseIteration(evalue[0], matrix);
            third = Vectorproduct.getVectorproduct(first, second);
          }
        }
        else if (
          (Math.abs(second[0].getDouble()) < EPSILON
            && Math.abs(second[1].getDouble()) < EPSILON
            && Math.abs(second[2].getDouble()) < EPSILON)
            && (Math.abs(third[0].getDouble()) < EPSILON
              && Math.abs(third[1].getDouble()) < EPSILON
              && Math.abs(third[2].getDouble()) < EPSILON)) {
          if (Math.abs(evalue[0].getDouble() - evalue[1].getDouble())
            < EPSILON) {
            third = eigenvectorByInverseIteration(evalue[2], matrix);
            second = Vectorproduct.getVectorproduct(first, third);
          }
          else {
            second = eigenvectorByInverseIteration(evalue[1], matrix);
            third = Vectorproduct.getVectorproduct(first, second);
          }
        }
        else if (
          Math.abs(first[0].getDouble()) < EPSILON
            && Math.abs(first[1].getDouble()) < EPSILON
            && Math.abs(first[2].getDouble()) < EPSILON) {
          first = Vectorproduct.getVectorproduct(second, third);
        }
        else if (
          Math.abs(second[0].getDouble()) < EPSILON
            && Math.abs(second[1].getDouble()) < EPSILON
            && Math.abs(second[2].getDouble()) < EPSILON) {
          second = Vectorproduct.getVectorproduct(first, third);
        }
        else if (
          Math.abs(third[0].getDouble()) < EPSILON
            && Math.abs(third[1].getDouble()) < EPSILON
            && Math.abs(third[2].getDouble()) < EPSILON) {
          third = Vectorproduct.getVectorproduct(first, second);
        }
        first = scaling(first);
        second = scaling(second);
        third = scaling(third);

        entry =
          new MNumber[] {
            first[0],
            first[1],
            first[2],
            second[0],
            second[1],
            second[2],
            third[0],
            third[1],
            third[2] };
        return entry;
      }
    }
    else
      throw new IllegalUsageException("The matrix must be a symmetric 3x3-matrix.");
  }

  /**
   * Returns an orthonormal system of eigenvectors of the symmetric
   * 3x3-{@link NumberMatrix}
   * <code>matrix</code>. If the
   * direct method (solving a system of linear equations) fails because of
   * numerical inaccuracy the eigenvectors are approximately computed by
   * inverse iteration.
   *
   * @param    matrix              a  symmetric 3x3-<code>NumberMatrix</code>.
   *
   * @return   a <code>MNumber[]</code> holding one after another the
   * eigenvectors of <code>matrix</code>.
   * @throws IllegalUsageException if <code>matrix</code> is no symmetric 3x3-matrix.
   *
   */
  public static MNumber[] eigenvector(NumberMatrix matrix) {
    MNumber[] eigenvectors = computeEigenvectors(matrix);
    if (Determinant.det(eigenvectors).greaterThan(zero))
      return eigenvectors;
    else {
      return new MNumber[] {
        eigenvectors[0],
        eigenvectors[1],
        eigenvectors[2],
        eigenvectors[3],
        eigenvectors[4],
        eigenvectors[5],
        eigenvectors[6].negated(),
        eigenvectors[7].negated(),
        eigenvectors[8].negated()};
    }
  }

  /**
   * Starts with a scaled randomvector y_0 and solves iterative the system
   * of linear equations A*y_(k+1) = y_k until the changes are less than
   * a certain EPSILON.
   * If l is an eigenvalue of B and A is the matrix (B-l*Id) then the solution
   * vector is (approximately) an eigenvector of the eigenvalue l.
   */
  private static MNumber[] eigenvectorByInverseIteration(
    MNumber eigenvalue,
    NumberMatrix quadricmatrix) {
    double helper;
    NumberMatrix aSubLambda = subEv(quadricmatrix, eigenvalue);
    MNumber[] A =
      new MNumber[] {
        aSubLambda.getEntry(1, 1),
        aSubLambda.getEntry(1, 2),
        aSubLambda.getEntry(1, 3),
        aSubLambda.getEntry(2, 1),
        aSubLambda.getEntry(2, 2),
        aSubLambda.getEntry(2, 3),
        aSubLambda.getEntry(3, 1),
        aSubLambda.getEntry(3, 2),
        aSubLambda.getEntry(3, 3)};
    int indx[] = new int[3];
    MNumber d = eigenvalue.create().setDouble(1.0);
    MNumber LU[] = new MNumber[9];
    LU = LUDecomposition.ludecomposition(A, indx, d);
    MNumber[] eigenvector = new MNumber[3];
    Random r = new Random();
    MNumber[] y =
      new MNumber[] {
        eigenvalue.create().setDouble(r.nextDouble()),
        eigenvalue.create().setDouble(r.nextDouble()),
        eigenvalue.create().setDouble(r.nextDouble())};
    y = scaling(y);
    eigenvector = (LUDecomposition.lubacksubstitution(LU, indx, y));
    int i = 0;
    do {
      for (int j = 0; j < 3; j++) {
        y[j] = eigenvector[j].copy();
      }
      eigenvector = scaling(LUDecomposition.lubacksubstitution(LU, indx, y));
      i = i + 1;
      helper =
        Math.abs(
          getNorm(
            new MNumber[] {
              MNumber.subtract(y[0], eigenvector[0]),
              MNumber.subtract(y[1], eigenvector[1]),
              MNumber.subtract(y[2], eigenvector[2])})
            .getDouble());
    }
    while (helper > EPSILON && i < 100);
    return scaling(eigenvector);
  }
}
