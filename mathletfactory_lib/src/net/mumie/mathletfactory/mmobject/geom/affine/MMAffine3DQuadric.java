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

package net.mumie.mathletfactory.mmobject.geom.affine;

import java.math.BigInteger;

import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.numeric.Determinant;
import net.mumie.mathletfactory.math.number.numeric.Eigenvalue;
import net.mumie.mathletfactory.math.number.numeric.Eigenvalue3x3Sym;
import net.mumie.mathletfactory.math.number.numeric.MatrixMultiplikation;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * Class representing the MMObject of a quadric in 3-dimensional space.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMAffine3DQuadric extends MMDefaultCanvasObject implements GeometryIF {

  /**
   * Affine3DQuadric is internally represented by the symmetric 4x4
   * {@link #m_quadricMatrix}. The quadric in
   * homogeneous coordinates is given by the equation<p>
   * c_11 X� + c_22 Y� + c_33 Z� + c_44 W�
   *  + 2c_12 XY + 2c_13 XZ + 2c_13 XW + 2c_23 YZ + 2c_24 YW + 2c_34 ZW = 0.<p>
   * The corresponding symmetric {@link #m_quadricMatrix} is then given by <p>
   * [c_11 c_12 c_13 c_14]<br>
   * [c_12 c_22 c_23 c_24]   = C<br>
   * [c_13 c_23 c_33 c_34]<br>
   * [c_14 c_24 c_34 c_44].<p>
   * This projective quadric corresponds to the affine quadric given by<p>
   * c_11 X� + c_22 Y� + +c_33 Z�
   *  + 2c_12 XY + 2c_13 XZ + 2c_23 YZ
   *  + 2c_14 X + 2c_24 Y + 2c_34 Z + c_44 = 0.
   */
  public NumberMatrix m_quadricMatrix;

  /**
   * {@link #m_quadricType} holds the type of the quadric. Possible values are
   * "imaginary ellipsoid", "point", "ellipsoid", "hyperboloid of one sheet",
   * "elliptic cone", "hyperboloid of two sheets", "elliptic paraboloid",
   * "imaginary elliptic cylinder", "elliptic cylinder", "straight line",
   * "hyperbolic paraboloid", "hyperbolic cylinder",
   * "pair of intersecting planes", "parabolic cylinder",
   * "pair of imaginary parallel planes", "pair of distinct parallel planes",
   * "pair of coincident parallel planes", or "error".
   */
  private String m_quadricType;

  /**
   * {@link #m_eigenvaluesOfMinorMatrix} holds the eigenvalues of the minor
   * matrix. If <p>
   * [c_11 c_12 c_13 c_14]<br>
   * [c_12 c_22 c_23 c_24]   = C<br>
   * [c_13 c_23 c_33 c_34]<br>
   * [c_14 c_24 c_34 c_44].<p>
   * is the {@link #m_quadricMatrix} then the minor matrix is given by<p>
   * [c11 c12 c13]<br>
   * [c12 c22 c23]<br>
   * [c13 c23 c33].<p>
   * To avoid expensive computing the eigenvalues are computed only once
   * (in the constructor or when the quadric is changed).
   */
  private MNumber[] m_eigenvaluesOfMinorMatrix;

  /**
   * {@link #m_transformationMatrix} holds the transformation matrix T of this quadric. T has the form<p>
   * [r_11 r_12 r_13 t_1]<br>
   * [r_12 r_22 r_23 t_2]   = T<br>
   * [r_13 r_23 r_33 t_3]<br>
   * [     0      0      0     1],<p>
   * where <p>
   * [r_11 r_12 r_13]<br>
   * [r_12 r_22 r_23]<br>
   * [r_13 r_23 r_33]<p>
   * describes the rotation and <p>
   * [t_1]<br>
   * [t_2]<br>
   * [t_3]<p>
   * the translation with respect to the standard coordinate system.
   */
  private NumberMatrix m_transformationMatrix;

  /**
   * {@link #m_version} is incremented every time the quadric is changed.
   */
  private int m_version;

  /**
   * {@link #m_recalculationNeeded} is set true every time the quadric is changed.
   */
//  private boolean m_recalculationNeeded = true;

  /**
   * STEPS determines the distance of the grid vertices.
   */
  private final int STEPS = 20;

  /**
   * {@link #m_values} holds the z-coordinate of the surface at the grid vertices.
   */
  private NumberTuple[][] m_values = new NumberTuple[STEPS + 1][STEPS + 1];

  private static double EPSILON = 1e-13;

  /**
   * Class constructor initializing {@link #m_quadricMatrix} with the identity
   * matrix.
   */
  public MMAffine3DQuadric(Class entryClass) {
    this(new NumberMatrix(entryClass, 4));
  }

  /**
   * Class constructor specifying the <code>quadricMatrix</code> of type
   * {@link NumberMatrix}
   * of the quadric to create.
   * @throws IllegalArgumentException if <code>quadricMatrix</code>
   * is not symmetric.
   */
  public MMAffine3DQuadric(NumberMatrix quadricMatrix) {
    // is symmetric?
    if (quadricMatrix.equals(quadricMatrix.transposed())) {
      m_quadricMatrix = standardize(quadricMatrix);
      m_eigenvaluesOfMinorMatrix =
        eigenvalueOfMinorMatrix(m_quadricMatrix);
      m_quadricType = getQuadricType(m_quadricMatrix);
      m_transformationMatrix = getTransformationMatrix();
    }
    else
      throw new IllegalArgumentException("The given matrix must be symmetric!");
  }

  /**
   * If there are more negative than positive eigenvalues of the minor matrix
   * of <code>quadricMatrix</code> then <code>quadricMatrix</code> is negated.
   * @param quadricMatrix
   * @return the either or not negated quadricMatrix
   */
  private NumberMatrix standardize(NumberMatrix quadricMatrix) {
    int pos = 0;
    int neg = 0;
    int posEV = 0;
    int negEV = 0;
    MNumber[] eigenvaluesOfMinorMatrix =
      eigenvalueOfMinorMatrix(quadricMatrix);
    MNumber[] eigenvalues =
      Eigenvalue.eigenvalue(quadricMatrix.getEntriesAsNumberRef());
    for (int j = 0; j < eigenvaluesOfMinorMatrix.length; j++) {
      if (eigenvaluesOfMinorMatrix[j].getDouble() > EPSILON) {
        posEV = posEV + 1;
      }
      else if (-eigenvaluesOfMinorMatrix[j].getDouble() > EPSILON) {
        negEV = negEV + 1;
      }
    }
    for (int i = 0; i < eigenvalues.length; i++) {
      if (eigenvalues[i].getDouble() > EPSILON) {
        pos = pos + 1;
      }
      else if (-eigenvalues[i].getDouble() > EPSILON) {
        neg = neg + 1;
      }
    }
    if (negEV > posEV || neg > pos) {
      for (int l = 0;
        l
          < quadricMatrix.getRowCount()
            * quadricMatrix.getColumnCount();
        l++) {
        int m = l / quadricMatrix.getColumnCount() + 1;
        int n =
          (new BigInteger(l + "")
            .mod(
              new BigInteger(
                quadricMatrix.getColumnCount() + "")))
            .intValue()
            + 1;
        quadricMatrix.getEntryAsNumberRef(m, n).negate();
      }
    }
    return quadricMatrix;
  }

  /**
   * Class constructor for a quadric given by<p>
   * <code>c_11</code>*X� + <code>c_22</code>*Y�+ <code>c_33</code>*Z�
   * + 2<code>c_12</code>*XY + 2<code>c_23</code>*YZ + 2<code>c_13</code>*XZ
   * + 2<code>c_14</code>*X + 2<code>c_24</code>*Y + 2<code>c_34</code>*Y
   * + <code>c_44</code> = 0.
   */
  public MMAffine3DQuadric(
    MNumber c_11,
    MNumber c_12,
    MNumber c_13,
    MNumber c_14,
    MNumber c_22,
    MNumber c_23,
    MNumber c_24,
    MNumber c_33,
    MNumber c_34,
    MNumber c_44) {
    this(
      new NumberMatrix(
        MNumber.class,
        4,
        new Object[] {
          c_11,
          c_12,
          c_13,
          c_14,
          c_12,
          c_22,
          c_23,
          c_24,
          c_13,
          c_23,
          c_33,
          c_34,
          c_14,
          c_24,
          c_34,
          c_44 }));
  }

  /**
   * Returns the type of the quadric. Possible types are an imaginary ellipsoid
   * (an empty set in IR�), a point, an ellipsoid, a hyperboloid of one sheet,
   * an elliptic cone, a hyperboloid of two sheets, an elliptic paraboloid,
   * an imaginary elliptic cylinder (an empty set in IR�), and elliptic cylinder,
   * a straight line, a hyperbolic paraboloid, a hyperbolic cylinder, a pair of
   * intersecting planes, a parabolic cylinder, a pair of imaginary parallel
   * planes (an empty set in IR�), a pair of distinct parallel planes and a pair
   * of coincident parallel planes.
   *
   * @param    quadricMatrix       a  <code>NumberMatrix</code> representing
   * the quadric.
   *
   * @return   a <code>String</code> holding "imaginary ellipsoid", "point",
   * "ellipsoid", "hyperboloid of one sheet", "elliptic cone", "hyperboloid of
   * two sheets", "elliptic paraboloid", "imaginary elliptic cylinder",
   * "elliptic cylinder", "straight line", "hyperbolic paraboloid", "hyperbolic
   * cylinder", "pair of intersecting planes", "parabolic cylinder", "pair of
   * imaginary parallel planes", "pair of distinct parallel planes", "pair
   * of coincident parallel planes", or "error".
   */
  public String getQuadricType(NumberMatrix quadricMatrix) {
    if (isImaginaryEllipsoid(quadricMatrix))
      return ResourceManager.getMessage("imaginary_ellipsoid");
    else if (isPoint(quadricMatrix))
      return ResourceManager.getMessage("point");
    else if (isEllipsoid(quadricMatrix))
      return ResourceManager.getMessage("ellipsoid");
    else if (isHyperboloidOfOneSheet(quadricMatrix))
      return ResourceManager.getMessage("hyperboloid_of_one_sheet");
    else if (isEllipticCone(quadricMatrix))
      return ResourceManager.getMessage("elliptic_cone");
    else if (isHyperboloidOfTwoSheets(quadricMatrix))
      return ResourceManager.getMessage("hyperboloid_of_two_sheets");
    else if (isEllipticParaboloid(quadricMatrix))
      return ResourceManager.getMessage("elliptic_paraboloid");
    else if (isImaginaryEllipticCylinder(quadricMatrix))
      return ResourceManager.getMessage("imaginary_elliptic_cylinder");
    else if (isEllipticCylinder(quadricMatrix))
      return ResourceManager.getMessage("elliptic_cylinder");
    else if (isStraightLine(quadricMatrix))
      return ResourceManager.getMessage("straight_line");
    else if (isHyperbolicParaboloid(quadricMatrix))
      return ResourceManager.getMessage("hyperbolic_paraboloid");
    else if (isHyperbolicCylinder(quadricMatrix))
      return ResourceManager.getMessage("hyperbolic_cylinder");
    else if (isPairOfIntersectingPlanes(quadricMatrix))
      return ResourceManager.getMessage("pair_of_intersecting_planes");
    else if (isParabolicCylinder(quadricMatrix))
      return ResourceManager.getMessage("parabolic_cylinder");
    else if (isPairOfImaginaryParallelPlanes(quadricMatrix))
      return ResourceManager.getMessage(
        "pair_of_imaginary_parallel_planes");
    else if (isPairOfDistinctParallelPlanes(quadricMatrix))
      return ResourceManager.getMessage(
        "pair_of_distinct_parallel_planes");
    else if (isPairOfCoincidentParallelPlanes(quadricMatrix))
      return ResourceManager.getMessage(
        "pair_of_coincident_parallel_planes");
    else
      return ResourceManager.getMessage("error");
  }

  /**
   * Returns the minor matrix. If <p>
   * [c_11 c_12 c_13 c_14]<br>
   * [c_12 c_22 c_23 c_24]   = C<br>
   * [c_13 c_23 c_33 c_34]<br>
   * [c_14 c_24 c_34 c_44].<p>
   * is the <code>quadricMatrix</code> then the minor matrix is given by<p>
   * [c11 c12 c13]<br>
   * [c12 c22 c23]<br>
   * [c13 c23 c33].<p>
   *
   * @param    quadricMatrix       a  <code>NumberMatrix</code>.
   *
   * @return   a <code>NumberMatrix</code> holding the minor matrix.
   *
   */
  public NumberMatrix getMinorMatrix(NumberMatrix quadricMatrix) {
    MNumber[] entry =
      {
        quadricMatrix.getEntry(1, 1),
        quadricMatrix.getEntry(1, 2),
        quadricMatrix.getEntry(1, 3),
        quadricMatrix.getEntry(2, 1),
        quadricMatrix.getEntry(2, 2),
        quadricMatrix.getEntry(2, 3),
        quadricMatrix.getEntry(3, 1),
        quadricMatrix.getEntry(3, 2),
        quadricMatrix.getEntry(3, 3)};
    return new NumberMatrix(
      quadricMatrix.getEntry(1, 1).getClass(),
      3,
      entry);
  }

  /**
   * Returns the minor vector. If <p>
   * [c_11 c_12 c_13 c_14]<br>
   * [c_12 c_22 c_23 c_24]   = C<br>
   * [c_13 c_23 c_33 c_34]<br>
   * [c_14 c_24 c_34 c_44].<p>
   * is the <code>quadricMatrix</code> then the minor vector is given by<p>
   * [c14]<br>
   * [c24]<br>
   * [c34].<p>
   *
   * @param    quadricMatrix       <code>NumberMatrix</code>.
   *
   * @return   a <code>NumberMatrix</code> holding the minor vector.
   *
   */
  public NumberMatrix getMinorVector(NumberMatrix quadricMatrix) {
    MNumber two = quadricMatrix.getEntry(1, 1).create().setDouble(2.0);
    MNumber[] entry =
      {
        quadricMatrix.getEntry(1, 4).copy().mult(two),
        quadricMatrix.getEntry(2, 4).copy().mult(two),
        quadricMatrix.getEntry(3, 4).copy().mult(two)};
    return new NumberMatrix(
      quadricMatrix.getEntry(1, 1).getClass(),
      1,
      entry);
  }

  /**
   * Returns the eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_31 c_32 c_34].
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>MNumber[]</code> holding the eigenvalues of the minor
   * matrix.
   *
   */
  private MNumber[] eigenvalueOfMinorMatrix(NumberMatrix quadricMatrix) {
    return Eigenvalue3x3Sym.eigenvalue(getMinorMatrix(quadricMatrix));
  }

  /**
   * Returns the orthonormalized
   * eigenvectors ((v11, v12, v13), (v21, v22, v23), (v31, v32, v33)) of the
   * minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * If v1 is the first, v2 the second and v3 the third eigenvector, then
   * (v1, v2, v3) is positive oriented.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>MNumber[]</code> holding rowwise the orthonormalized
   * positive oriented eigenvectors of <code>quadricMatrix</code>.
   *
   */
  private MNumber[] eigenvectorOfMinorMatrix(NumberMatrix quadricMatrix) {
    return Eigenvalue3x3Sym.eigenvector(getMinorMatrix(quadricMatrix));
  }

  /**
   * Checkes whether a given quadric is an imaginary
   * ellipsoid (an empty set in IR^3). This is the case if the three eigenvalues
   * of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * have the same sign and are |=0 and if det C > 0.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents an imaginary ellipsoid.
   *
   */
  private boolean isImaginaryEllipsoid(NumberMatrix quadricMatrix) {
    return (
      MNumber.multiply(
        m_eigenvaluesOfMinorMatrix[0],
        m_eigenvaluesOfMinorMatrix[1]))
      .getDouble()
      > EPSILON
      && (MNumber
        .multiply(
          m_eigenvaluesOfMinorMatrix[1],
          m_eigenvaluesOfMinorMatrix[2]))
        .getDouble()
        > EPSILON
      && quadricMatrix.determinant().getDouble() > EPSILON;
  }

  /**
   * Checkes whether a given quadric is a point. This is the
   * case if the three eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * have the same sign and are |=0 and if det C = 0.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a point.
   *
   */
  private boolean isPoint(NumberMatrix quadricMatrix) {
    return (
      MNumber.multiply(
        m_eigenvaluesOfMinorMatrix[0],
        m_eigenvaluesOfMinorMatrix[1]))
      .getDouble()
      > EPSILON
      && (MNumber
        .multiply(
          m_eigenvaluesOfMinorMatrix[1],
          m_eigenvaluesOfMinorMatrix[2]))
        .getDouble()
        > EPSILON
      && quadricMatrix.determinant().absed().getDouble() < EPSILON;
  }

  /**
   * Checkes whether a given quadric is an ellipsoid. This
   * is the case if the three eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * have the same sign and are |=0 and if det C < 0.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents an ellipsoid.
   *
   */
  private boolean isEllipsoid(NumberMatrix quadricMatrix) {
    return (
      MNumber.multiply(
        m_eigenvaluesOfMinorMatrix[0],
        m_eigenvaluesOfMinorMatrix[1]))
      .getDouble()
      > EPSILON
      && (MNumber
        .multiply(
          m_eigenvaluesOfMinorMatrix[1],
          m_eigenvaluesOfMinorMatrix[2]))
        .getDouble()
        > EPSILON
      && quadricMatrix.determinant().getDouble() < 0.0;
  }

  /**
   * Checkes whether a given quadric is a
   * hyperboloid of one sheet. This is the case if the three eigenvalues of the
   * minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * have a different sign and are |=0 and if det C > 0.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a hyperboloid of one sheet.
   *
   */
  private boolean isHyperboloidOfOneSheet(NumberMatrix quadricMatrix) {
    return (
      (MNumber
        .multiply(
          m_eigenvaluesOfMinorMatrix[0],
          m_eigenvaluesOfMinorMatrix[1])
        .getDouble()
        > EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          < 0.0)
        || (MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[0],
            m_eigenvaluesOfMinorMatrix[1])
          .getDouble()
          < 0.0
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[1],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            > EPSILON)
        || (MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[0],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          > EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[1],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            < 0.0))
      && quadricMatrix.determinant().getDouble() > EPSILON;
  }

  /**
   * Checkes whether a given quadric is an elliptic cone.
   * This is the case if the three eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * have a different sign and are |=0 and if det C = 0.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents an elliptic cone.
   *
   */
  private boolean isEllipticCone(NumberMatrix quadricMatrix) {
    return (
      (MNumber
        .multiply(
          m_eigenvaluesOfMinorMatrix[0],
          m_eigenvaluesOfMinorMatrix[1])
        .getDouble()
        > EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          < 0.0)
        || (MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[0],
            m_eigenvaluesOfMinorMatrix[1])
          .getDouble()
          < 0.0
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[1],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            > EPSILON)
        || (MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[0],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          > EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[1],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            < 0.0))
      && quadricMatrix.determinant().absed().getDouble() < EPSILON;
  }

  /**
   * Checkes whether a given quadric is a
   * hyperboloid of two sheets. This is the case if the three eigenvalues of
   * the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * have a different sign and are |=0 and if det C < 0.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a hyperboloid of two sheets.
   *
   */
  private boolean isHyperboloidOfTwoSheets(NumberMatrix quadricMatrix) {
    return (
      (MNumber
        .multiply(
          m_eigenvaluesOfMinorMatrix[0],
          m_eigenvaluesOfMinorMatrix[1])
        .getDouble()
        > EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          < 0.0)
        || (MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[0],
            m_eigenvaluesOfMinorMatrix[1])
          .getDouble()
          < 0.0
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[1],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            > EPSILON)
        || (MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[0],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          > EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[1],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            < 0.0))
      && quadricMatrix.determinant().getDouble() < 0.0;
  }

  /**
   * Checkes whether a given quadric is an elliptic
   * paraboloid. This is the case if one of the eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * is zero, the two others have the same sign, and rank C = 4.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents an elliptic paraoloid.
   *
   */
  private boolean isEllipticParaboloid(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && (MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2]))
          .getDouble()
          > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && (MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[2]))
            .getDouble()
            > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && (MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[1]))
            .getDouble()
            > EPSILON))
      && quadricMatrix.rank() == 4;
  }
  /**
   * Is used for the determination of an elliptic cylinder and an imaginary
   * elliptic cylinder. Returns true if sigma2>0 and sigma1*sigma3>0.
   */
  private static boolean firstComputationHelper(NumberMatrix quadricMatrix) {
    MNumber c11, c12, c13, c14, c22, c23, c24, c33, c34, c44;
    c11 = quadricMatrix.getEntry(1, 1);
    c12 = quadricMatrix.getEntry(1, 2);
    c13 = quadricMatrix.getEntry(1, 3);
    c14 = quadricMatrix.getEntry(1, 4);
    c22 = quadricMatrix.getEntry(2, 2);
    c23 = quadricMatrix.getEntry(2, 3);
    c24 = quadricMatrix.getEntry(2, 4);
    c33 = quadricMatrix.getEntry(3, 3);
    c34 = quadricMatrix.getEntry(3, 4);
    c44 = quadricMatrix.getEntry(4, 4);

    MNumber sigma1, sigma2, sigma3;
    sigma1 = c11.copy().add(c22).add(c33).add(c44);
    sigma2 =
      Determinant
        .det(new MNumber[] { c33, c34, c34, c44 })
        .add(Determinant.det(new MNumber[] { c22, c24, c24, c44 }))
        .add(Determinant.det(new MNumber[] { c22, c23, c23, c33 }))
        .add(Determinant.det(new MNumber[] { c11, c14, c14, c44 }))
        .add(Determinant.det(new MNumber[] { c11, c13, c13, c33 }))
        .add(Determinant.det(new MNumber[] { c11, c12, c12, c22 }));
    sigma3 =
      Determinant
        .det(
          new MNumber[] {
            c22,
            c23,
            c24,
            c23,
            c33,
            c34,
            c24,
            c34,
            c44 })
        .add(
          Determinant.det(
            new MNumber[] {
              c11,
              c13,
              c14,
              c13,
              c33,
              c34,
              c14,
              c34,
              c44 }))
        .add(
          Determinant.det(
            new MNumber[] {
              c11,
              c12,
              c14,
              c12,
              c22,
              c24,
              c14,
              c24,
              c44 }))
        .add(
          Determinant.det(
            new MNumber[] {
              c11,
              c12,
              c13,
              c12,
              c22,
              c23,
              c13,
              c23,
              c33 }));
    return sigma2.getDouble() > EPSILON
      && sigma1.mult(sigma3).getDouble() > EPSILON;
  }

  /**
   * Checkes whether a given quadric is an
   * imaginary elliptic cylinder (an empty set in IR^3). This is the
   * case if one of the eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * is zero, the two others have the same sign, rank C = 3, sigma_2(C)>0, and
   * sigma_1(C)*sigma_3(C) > 0. Hereby sigma_i(C) is the sum of the
   * determinants of the ixi-matrices which you get by eliminating 4-i rows and
   * columns. This last condition is checked by the firstComputationHelper.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents an imaginary elliptic cylinder.
   *
   */
  private boolean isImaginaryEllipticCylinder(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[1])
            .getDouble()
            > EPSILON))
      && quadricMatrix.rank() == 3
      && firstComputationHelper(quadricMatrix);
  }

  /**
   * Checkes whether a given quadric is an elliptic
   * cylinder. This is the case if one of the eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * is zero, the two others have the same sign, rank C = 3, sigma_2(C)<=0, and
   * sigma_1(C)*sigma_3(C) <= 0. Hereby sigma_i(C) is the sum of the
   * determinants of the ixi-matrices which you get by eliminating 4-i rows and
   * columns. This last condition is checked by the firstComputationHelper.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents an elliptic cylinder.
   *
   */
  private boolean isEllipticCylinder(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[1])
            .getDouble()
            > EPSILON))
      && quadricMatrix.rank() == 3
      && !firstComputationHelper(quadricMatrix);
  }

  /**
   * Checkes whether a given quadric is a straight line.
   * This is the case if one of the eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * is zero, the two others have the same sign, and rank C = 2.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a straight line.
   *
   */
  private boolean isStraightLine(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            > EPSILON)
        || (m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[1])
            .getDouble()
            > EPSILON))
      && quadricMatrix.rank() == 2;
  }

  /**
   * Checkes whether a given quadric is a
   * hyperbolic paraboloid. This is the case if one of the eigenvalues of the
   * minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * is zero, the two others have different sign, and rank C = 4.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a hyperbolic paraboloid.
   *
   */
  private boolean isHyperbolicParaboloid(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          < 0.0)
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            < 0.0)
        || (m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[1])
            .getDouble()
            < 0.0))
      && quadricMatrix.rank() == 4;
  }

  /**
   * Checkes whether a given quadric is a
   * hyperbolic cylinder. This is the case if one of the eigenvalues of the
   * minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * is zero, the two others have different sign, and rank C = 3.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a hyperbolic cylinder.
   *
   */
  private boolean isHyperbolicCylinder(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          < 0.0)
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            < 0.0)
        || (m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[1])
            .getDouble()
            < 0.0))
      && quadricMatrix.rank() == 3;
  }

  /**
   * Checkes whether a given quadric is a pair
   * of intersecting planes. This is the case if one of the eigenvalues of the
   * minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * is zero, the two others have different sign, and rank C = 2.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a pair of intersecting planes.
   *
   */
  private boolean isPairOfIntersectingPlanes(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && MNumber
          .multiply(
            m_eigenvaluesOfMinorMatrix[1],
            m_eigenvaluesOfMinorMatrix[2])
          .getDouble()
          < 0.0)
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[2])
            .getDouble()
            < 0.0)
        || (m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && MNumber
            .multiply(
              m_eigenvaluesOfMinorMatrix[0],
              m_eigenvaluesOfMinorMatrix[1])
            .getDouble()
            < 0.0))
      && quadricMatrix.rank() == 2;
  }

  /**
   * Checkes whether a given quadric is a parabolic
   * cylinder. This is the case if two eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * are zero, the third eigenvector is |=0 and rank C = 3.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a parabolic cylinder.
   *
   */
  private boolean isParabolicCylinder(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
        && !(m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[1].absed().getDouble()
            < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[0].absed().getDouble()
            < EPSILON)))
      && quadricMatrix.rank() == 3;
  }

  /**
   * Is used for the determination of a pair of distinct parallel planes and a
   * pair of imaginary parallel planes. Returns true if sigma>0.
   */
  private static boolean secondComputationHelper(NumberMatrix quadricMatrix) {
    MNumber c11, c14, c22, c24, c33, c34, c44;
    c11 = quadricMatrix.getEntry(1, 1);
    c14 = quadricMatrix.getEntry(1, 4);
    c22 = quadricMatrix.getEntry(2, 2);
    c24 = quadricMatrix.getEntry(2, 4);
    c33 = quadricMatrix.getEntry(3, 3);
    c34 = quadricMatrix.getEntry(3, 4);
    c44 = quadricMatrix.getEntry(4, 4);
    MNumber sigma =
      Determinant
        .det(new MNumber[] { c11, c14, c14, c44 })
        .add(Determinant.det(new MNumber[] { c22, c24, c24, c44 }))
        .add(Determinant.det(new MNumber[] { c33, c34, c34, c44 }));
    return sigma.getDouble() > EPSILON;
  }

  /**
   * Checkes whether a given quadric is a
   * pair of imaginary parallel planes (an empty set in IR^2). This is the case
   * if two eigenvalues of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * are zero, the third eigenvector is |=0, rank C = 2, and sigma > 0, where
   * sigma is computed by the secondComputationHelper.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a pair of imaginary parallel planes.
   *
   */
  private boolean isPairOfImaginaryParallelPlanes(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
        && !(m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[1].absed().getDouble()
            < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[0].absed().getDouble()
            < EPSILON)))
      && quadricMatrix.rank() == 2
      && secondComputationHelper(quadricMatrix);
  }

  /**
   * Checkes whether a given quadric is a
   * pair of distinct parallel planes. This is the case if two eigenvalues of
   * the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * are zero, the third eigenvector is |=0, rank C = 2, and sigma <= 0, where
   * sigma is computed by the secondComputationHelper.
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a pair of distinct parallel planes.
   *
   */
  private boolean isPairOfDistinctParallelPlanes(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
        && !(m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[1].absed().getDouble()
            < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[0].absed().getDouble()
            < EPSILON)))
      && quadricMatrix.rank() == 2
      && !secondComputationHelper(quadricMatrix);
  }

  /**
   * Checkes whether a given quadric is
   * a pair of coincident parallel planes. This is the case if two eigenvalues
   * of the minor matrix<p>
   * [c_11 c_12 c_13]<br>
   * [c_21 c_22 c_23]<br>
   * [c_13 c_23 c_33].<p>
   * are zero, the third eigenvector is |=0 and rank C = 1
   *
   * @param    quadricMatrix         a  <code>NumberMatrix</code>
   *
   * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
   * represents a pair of coincident parallel planes.
   *
   */
  private boolean isPairOfCoincidentParallelPlanes(NumberMatrix quadricMatrix) {
    return (
      (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
        && m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
        && !(m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[0].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[1].absed().getDouble()
            < EPSILON))
        || (m_eigenvaluesOfMinorMatrix[1].absed().getDouble() < EPSILON
          && m_eigenvaluesOfMinorMatrix[2].absed().getDouble() < EPSILON
          && !(m_eigenvaluesOfMinorMatrix[0].absed().getDouble()
            < EPSILON)))
      && quadricMatrix.rank() == 1;
  }

  /**
   * Calls the method {@link #calculate} which iterates over the grid,
   * calculates the parameterization of the quadric and stores it in
   * {@link #m_values}. Since some quadrics have two components, we need to
   * distinguish them. This is done by the variable <code>flag</code>.
   */
  private NumberTuple[][] getValues(boolean flag) {
    //    if(m_recalculationNeeded)
    calculate(flag);
    NumberTuple[][] helper = new NumberTuple[STEPS + 1][STEPS + 1];
    for (int i = 0; i < STEPS + 1; i++) {
      for (int j = 0; j < STEPS + 1; j++) {
        helper[i][j] = m_values[i][j];
      }
    }
    return helper;
  }

  /**
   * Gets the grid of values for the first (connected) component of this quadric.
   *
   * @return a <code>NumberTuple[][]</code> holding the grid of values.
   */
  public NumberTuple[][] getValuesFirstComponent() {
    return getValues(true);
  }

  /**
   * Gets the grid of values for the second (connected) component of
   * this quadric (if it exists).
   *
   * @return a <code>NumberTuple[][]</code> holding the grid of values.
   */
  public NumberTuple[][] getValuesSecondComponent() {
    return getValues(false);
  }

  /**
   * Returns the appropriate u-range for the parameterization of the quadric.
   *
   * @return double[]
   */
  private double[] getURange() {
    if (m_quadricType == ResourceManager.getMessage("ellipsoid"))
      return new double[] { -Math.PI, Math.PI };
    else
      return new double[] { -2.0, 2.0 };
  }

  /**
   * Returns the appropriate v-range for the parameterization of the quadric.
   *
   * @return double[]
   */
  private double[] getVRange() {
    if (m_quadricType == ResourceManager.getMessage("ellipsoid"))
      return new double[] { 0.0, Math.PI };
    else if (
      m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_one_sheet")
        || m_quadricType == ResourceManager.getMessage("elliptic_cone")
        || m_quadricType
          == ResourceManager.getMessage("hyperboloid_of_two_sheets")
        || m_quadricType
          == ResourceManager.getMessage("elliptic_paraboloid")
        || m_quadricType
          == ResourceManager.getMessage("elliptic_cylinder"))
      return new double[] { 0.0, 2.0 * Math.PI };
    else
      return new double[] { -2.0, 2.0 };
  }

  /**
   * Returns the parameterization of this quadric.
   * The parametrization is given by S*T*par(u,v) + S*(a1 + a2).
   * Hereby is
   * ---> S = matrix of eigenvectors of the minor matrix<p>
   *           [c_11 c_12 c_13]<br>
   *           [c_21 c_22 c_23]<br>
   *           [c_31 c_32 c_34]<p>
   * ---> a0 = (-B_1/(2*l_1), ... , -B_m/(2*l_m), 0, ... , 0)<br>
   * ---> l_i = i_th eigenvalue<br>
   * ---> B = S^(-1)*(2*c_14, 2*c_24, 2*c_34)<p>
   *
   * If p:=(B_m+1, ... , B_n) |= 0 :<br>
   * ---> T = [ E  0 ]<br>
   *          [ 0  P ]<br>
   * ---> P = an ONB starting with the scaled vector p<br>
   * ---> E = Id_m<br>
   * ---> a1 = -(1/|p|�)(c_44 - B_1�/(4*l_1) -...- B_m�/(4*l_m)) * (0, ... , 0, B_m+1, ... , B_n)<p>
   *
   * Otherwise T=Id_n and a1 = (0, ... , 0).<p>
   *
   * par(u,v) is the parameterization of the quadric in its standard position.<br>
   *
   * For a more detailed description see
   * "Repetitorium der Linearen Algebra 2", S. 275 - 281.
  */
  private MNumber[] getCoordinates(MNumber[] par) {
    MNumber[] ST =
      new MNumber[] {
        m_transformationMatrix.getEntry(1, 1),
        m_transformationMatrix.getEntry(1, 2),
        m_transformationMatrix.getEntry(1, 3),
        m_transformationMatrix.getEntry(2, 1),
        m_transformationMatrix.getEntry(2, 2),
        m_transformationMatrix.getEntry(2, 3),
        m_transformationMatrix.getEntry(3, 1),
        m_transformationMatrix.getEntry(3, 2),
        m_transformationMatrix.getEntry(3, 3)};
    MNumber[] sMultTmultPar =
      MatrixMultiplikation.multinxm(ST, par, 3, 3, 1);
    // S*T*par
    MNumber[] result =
      new MNumber[] {
        (sMultTmultPar[0].copy()).add(
          m_transformationMatrix.getEntry(1, 4)),
        (sMultTmultPar[1].copy()).add(
          m_transformationMatrix.getEntry(2, 4)),
        (sMultTmultPar[2].copy()).add(
          m_transformationMatrix.getEntry(3, 4))};
    //S*T*par + S*(a1+a0)
    return result;
  }

  /**
   * Returns the matrix T. Hereby is <br>
   * ---> B = S^(-1)*(2*c_14, 2*c_24, 2*c_34)<p>
   *
   * If p:=(B_m+1, ... , B_n) |= 0 :<br>
   * ---> T = [ E  0 ]<br>
   *          [ 0  P ]<br>
   * ---> P = an ONB starting with the scaled vector p<br>
   * ---> E = Id_m<br>
   *
   * Otherwise T=Id_n.<p>
   * @param B
   * @return MNumber[]
   */
  private MNumber[] getT(MNumber[] B) {
    MNumber[] T =
      new MNumber[] {
        B[0].create().setDouble(1.0),
        B[0].create().setDouble(0.0),
        B[0].create().setDouble(0.0),
        B[0].create().setDouble(0.0),
        B[0].create().setDouble(1.0),
        B[0].create().setDouble(0.0),
        B[0].create().setDouble(0.0),
        B[0].create().setDouble(0.0),
        B[0].create().setDouble(1.0)};
    // if one ore more eigenvalues are zero a second rotation is necessary
    // to get the standard form of a quadric. This rotation will be stored
    // in T.
    if (m_quadricType
      == ResourceManager.getMessage("elliptic_paraboloid")
      || m_quadricType == ResourceManager.getMessage("elliptic_cylinder")
      || m_quadricType
        == ResourceManager.getMessage("hyperbolic_paraboloid")
      || m_quadricType
        == ResourceManager.getMessage("hyperbolic_cylinder")
      || m_quadricType == ResourceManager.getMessage("straight_line")
      || m_quadricType
        == ResourceManager.getMessage("pair_of_intersecting_planes")) {
      if (B[2].absed().getDouble() > EPSILON) {
        T[8] = B[2].copy().div(B[2].absed());
      }
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("parabolic_cylinder")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_distinct_parallel_planes")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_coincident_parallel_planes")) {
        MNumber P = //sqrt(B1� + B2�)
  MNumber.squareRoot(B[1].copy().mult(B[1]).add(B[2].copy().mult(B[2])));
      if (P.getDouble() > EPSILON) {
        T[4] = B[1].copy().div(P);
        T[7] = B[2].copy().div(P);
        T[5] = T[7].copy().negate();
        T[8] = T[4].copy();
      }
    }
    return T;
  }

  /**
   * Returns the vector a0. Hereby is <br>
   * ---> a0 = (-B_1/(2*l_1), ... , -B_m/(2*l_m), 0, ... , 0)<br>
   * ---> l_i = i_th eigenvalue<br>
   * ---> B = S^(-1)*(2*c_14, 2*c_24, 2*c_34)<p>
   *
   * @param B
   * @param D
   * @return MNumber[]
   */
  private MNumber[] geta0(MNumber[] B, MNumber[] D) {
    MNumber[] a0;
    // a0 = translation vector which is obtained after the completion of square
    if (m_quadricType == ResourceManager.getMessage("ellipsoid")
      || m_quadricType == ResourceManager.getMessage("point")
      || m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_one_sheet")
      || m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_two_sheets")
      || m_quadricType == ResourceManager.getMessage("elliptic_cone")) {
      a0 =
        new MNumber[] {
          (MNumber
            .divide(
              B[0],
              MNumber.multiply(
                B[0].create().setDouble(2.0),
                D[0])))
            .negate(),
          (MNumber
            .divide(
              B[1],
              MNumber.multiply(
                B[0].create().setDouble(2.0),
                D[1])))
            .negate(),
          (MNumber
            .divide(
              B[2],
              MNumber.multiply(
                B[0].create().setDouble(2.0),
                D[2])))
            .negate()};
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("elliptic_paraboloid")
        || m_quadricType
          == ResourceManager.getMessage("elliptic_cylinder")
        || m_quadricType
          == ResourceManager.getMessage("hyperbolic_paraboloid")
        || m_quadricType
          == ResourceManager.getMessage("hyperbolic_cylinder")
        || m_quadricType == ResourceManager.getMessage("straight_line")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_intersecting_planes")) {
      a0 =
        new MNumber[] {
          (MNumber
            .divide(
              B[0],
              MNumber.multiply(
                B[0].create().setDouble(2.0),
                D[0])))
            .negate(),
          (MNumber
            .divide(
              B[1],
              MNumber.multiply(
                B[0].create().setDouble(2.0),
                D[1])))
            .negate(),
          B[0].create().setDouble(0.0)};
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("parabolic_cylinder")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_distinct_parallel_planes")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_coincident_parallel_planes")) {
      a0 =
        new MNumber[] {
          (MNumber
            .divide(
              B[0],
              MNumber.multiply(
                B[0].create().setDouble(2.0),
                D[0])))
            .negate(),
          B[0].create().setDouble(0.0),
          B[0].create().setDouble(0.0)};
    }
    else { //imaginary ellipsoid, imaginary elliptic cylinder, pair of imaginary parallel planes
      a0 =
        new MNumber[] {
          B[0].create().setDouble(0.0),
          B[0].create().setDouble(0.0),
          B[0].create().setDouble(0.0)};
    }
    return a0;
  }

  /**
   * Returns the vector a1.
   * If p:=(B_m+1, ... , B_n) |= 0 :<br>
   * a1 = -(1/|p|�)(c_44 - B_1�/(4*l_1) -...- B_m�/(4*l_m)) * (0, ... , 0, B_m+1, ... , B_n)<p>
   * Otherwise a1 = (0, ... , 0).<p>
   * @param B
   * @param b
   * @return MNumber[]
   */
  private MNumber[] geta1(MNumber[] B, MNumber b) {
    MNumber[] a1 =
      new MNumber[] {
        b.create().setDouble(0.0),
        b.create().setDouble(0.0),
        b.create().setDouble(0.0)};
    // if one ore more eigenvalues are zero a second translation is necessary
    // to get the standard form of a quadric. This translation will be stored
    // in a1, which is initialized here.
    if (m_quadricType
      == ResourceManager.getMessage("elliptic_paraboloid")
      || m_quadricType == ResourceManager.getMessage("elliptic_cylinder")
      || m_quadricType
        == ResourceManager.getMessage("hyperbolic_paraboloid")
      || m_quadricType
        == ResourceManager.getMessage("hyperbolic_cylinder")
      || m_quadricType == ResourceManager.getMessage("straight_line")
      || m_quadricType
        == ResourceManager.getMessage("pair_of_intersecting_planes")) {
      if (B[2].absed().getDouble() > EPSILON) {
        a1[2] =
          (B[2].copy().mult(b))
            .div(B[2].copy().mult(B[2]))
            .negate();
      }
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("parabolic_cylinder")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_distinct_parallel_planes")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_coincident_parallel_planes")) {
      MNumber P = B[1].copy().mult(B[1]).add(B[2].copy().mult(B[2]));
      if (P.getDouble() > EPSILON) {
        a1[1] = (B[1].copy().mult(b)).div(P).negate();
        a1[2] = (B[2].copy().mult(b)).div(P).negate();
      }
    }
    return a1;
  }

  /**
   * Computes b, the constant part of the quadric equation.
   * @param B
   * @param D
   * @return MNumber
   */
  private MNumber getb(MNumber[] B, MNumber[] D) {
    MNumber b; // b = the constant part of the quadric equation
    if (m_quadricType == ResourceManager.getMessage("ellipsoid")
      || m_quadricType == ResourceManager.getMessage("point")
      || m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_one_sheet")
      || m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_two_sheets")
      || m_quadricType == ResourceManager.getMessage("elliptic_cone")) {
      b =
        m_quadricMatrix.getEntry(4, 4).copy().sub(
          MNumber.divide(
            MNumber.add(
              MNumber.divide(MNumber.multiply(B[0], B[0]), D[0]),
              MNumber.add(
                MNumber.divide(
                  MNumber.multiply(B[1], B[1]),
                  D[1]),
                MNumber.divide(
                  MNumber.multiply(B[2], B[2]),
                  D[2]))),
            B[0].create().setDouble(4.0)));
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("elliptic_paraboloid")
        || m_quadricType
          == ResourceManager.getMessage("elliptic_cylinder")
        || m_quadricType
          == ResourceManager.getMessage("hyperbolic_paraboloid")
        || m_quadricType
          == ResourceManager.getMessage("hyperbolic_cylinder")
        || m_quadricType == ResourceManager.getMessage("straight_line")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_intersecting_planes")) {
      b =
        m_quadricMatrix.getEntry(4, 4).copy().sub(
          MNumber.divide(
            MNumber.add(
              MNumber.divide(MNumber.multiply(B[0], B[0]), D[0]),
              MNumber.divide(MNumber.multiply(B[1], B[1]), D[1])),
            B[0].create().setDouble(4.0)));
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("parabolic_cylinder")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_distinct_parallel_planes")
        || m_quadricType
          == ResourceManager.getMessage(
            "pair_of_coincident_parallel_planes")) {
      b =
        m_quadricMatrix.getEntry(4, 4).copy().sub(
          MNumber.divide(
            MNumber.divide(MNumber.multiply(B[0], B[0]), D[0]),
            B[0].create().setDouble(4.0)));
    }
    else { //imaginary ellipsoid, imaginary elliptic cylinder, pair of imaginary parallel planes
      b = B[0].create().setDouble(0.0);
    }
    return b;
  }

  /**
   * Computes the parameterization of the quadric at
   * (u, v) = (defU, defV) in its standard position.
   *
   * @param flag determines which component to compute
   * (since some quadrics have two components).
   * @param defU
   * @param defV
   * @param alpha
   * @param beta
   * @param gamma
   * @return MNumber[]
   */
  private MNumber[] getParameterization(
    boolean flag,
    MNumber defU,
    MNumber defV,
    MNumber alpha,
    MNumber beta,
    MNumber gamma) {
    if (m_quadricType == ResourceManager.getMessage("ellipsoid")) {
      //[-a*cos(u)*sin(v), b*sin(u)*sin(v), c*cos(v)]
      return new MNumber[] {
        alpha
          .copy()
          .mult(defU.copy().cos().mult(defV.copy().sin()))
          .negate(),
        beta.copy().mult(defU.copy().sin().mult(defV.copy().sin())),
        gamma.copy().mult(defV.copy().cos())};
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_one_sheet")) {
      //[-a*sqrt(1+u�)*cos(v), b*sqrt(1+u�)*sin(v), c*u]
      return new MNumber[] {
        alpha
          .copy()
          .mult(defV.copy().cos())
          .mult(
            MNumber.squareRoot(
              (defU.copy().mult(defU)).add(
                defU.create().setDouble(1.0))))
          .negate(),
        beta.copy().mult(defV.copy().sin()).mult(
          MNumber.squareRoot(
            (defU.copy().mult(defU)).add(
              defU.create().setDouble(1.0)))),
        gamma.copy().mult(defU)};
    }
    else if (
      m_quadricType == ResourceManager.getMessage("elliptic_cone")) {
      //[-(a/c)*u*cos(v), (b/c)*u*sin(v), u]
      return new MNumber[] {
        (alpha.copy().div(gamma.copy()))
          .mult(defU.copy())
          .mult(defV.copy().cos())
          .negate(),
        (beta.copy().div(gamma.copy())).mult(defU.copy()).mult(
          defV.copy().sin()),
        defU };
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_two_sheets")) {
      //[-a*sinh(u)*cos(v), b*sinh(u)*sin(v), +-c*cosh(u)]
      if (flag == true) {
        return new MNumber[] {
          alpha
            .copy()
            .mult(defU.copy().sinh())
            .mult(defV.copy().cos())
            .negate(),
          beta.copy().mult(defU.copy().sinh()).mult(
            defV.copy().sin()),
          gamma.copy().mult(defU.copy().cosh())};
      }
      else {
        return new MNumber[] {
          alpha.copy().mult(defU.copy().sinh()).mult(
            defV.copy().cos()),
          beta.copy().mult(defU.copy().sinh()).mult(
            defV.copy().sin()),
          (gamma.copy().mult(defU.copy().cosh())).negate()};
      }
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("elliptic_paraboloid")) {
      //[a*u*cos(v), b*u*sin(v), -u�/2]
      return new MNumber[] {
        alpha.copy().mult(defU).mult(defV.copy().cos()),
        beta.copy().mult(defU).mult(defV.copy().sin()),
        defU.copy().mult(defU).div(defU.create().setDouble(-2.0))};
      //[u, v, -v�/(2*b�)-u�/(2*a�)]
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("elliptic_cylinder")) {
      //[-a*cos(v), b*sin(v), u]
      return new MNumber[] {
        alpha.copy().mult(defV.copy().cos()).negate(),
        beta.copy().mult(defV.copy().sin()),
        defU };
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("hyperbolic_paraboloid")) {
      //[-u, v, v�/(2*b�)-u�/(2*a�)]
      return new MNumber[] {
        defU.copy().negate(),
        defV,
        defV
          .copy()
          .mult(defV)
          .div(
            beta.copy().mult(beta).mult(
              defU.create().setDouble(2.0)))
          .sub(
            defU.copy().mult(defU).div(
              alpha.copy().mult(alpha).mult(
                defU.create().setDouble(2.0))))};
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("hyperbolic_cylinder")) {
      //[+-a*cosh(u), b*sinh(u), v]
      if (flag == true) {
        return new MNumber[] {
          alpha.copy().mult(defU.copy().cosh()),
          beta.copy().mult(defU.copy().sinh()),
          defV };
      }
      else
        return new MNumber[] {
          alpha.copy().mult(defU.copy().cosh()).negate(),
          beta.copy().mult(defU.copy().sinh()),
          defV };
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("parabolic_cylinder")) {
      //[u, -u�/(2*a�), v]
      return new MNumber[] {
        defU,
        defU
          .copy()
          .mult(defU)
          .div(
            alpha.copy().mult(alpha).mult(
              defU.create().setDouble(2.0)))
          .negate(),
        defV };
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage(
          "pair_of_coincident_parallel_planes")) { //[0, u, v] ?
      return new MNumber[] { defU.create().setDouble(0.0), defU, defV };
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage(
          "pair_of_distinct_parallel_planes")) { //[+-a, u, v] ?
      if (flag == true) {
        return new MNumber[] { alpha, defU, defV };
      }
      else
        return new MNumber[] { alpha.copy().negate(), defU, defV };
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("pair_of_intersecting_planes")) {
      //[u, +-sqrt((b�/a�)*u�), v] ?
      if (flag == true) {
        return new MNumber[] {
          defU,
          beta
            .copy()
            .mult(beta)
            .div(alpha.copy().mult(alpha))
            .mult(defU.copy().mult(defU))
            .squareRoot(),
          defV };
      }
      else
        return new MNumber[] {
          defU,
          beta
            .copy()
            .mult(beta)
            .div(alpha.copy().mult(alpha))
            .mult(defU.copy().mult(defU))
            .squareRoot()
            .negate(),
          defV };
    }
    else
      return new MNumber[] {
        defU.create().setDouble(0.0),
        defU.create().setDouble(0.0),
        defU.create().setDouble(0.0)};
  }

  /**
   * Computes the appropriate a, b and c of the quadric transformed in it's normal form
   * x�/a� + y�/b� + z�/c� = 0 (1)
   *
   * @param b
   * @param D
   * @param B
   * @return MNumber[]
   */
  private MNumber[] getAlphaBetaGamma(MNumber b, MNumber[] D, MNumber[] B) {
    MNumber alpha, beta, gamma, P;
    if (m_quadricType == ResourceManager.getMessage("ellipsoid")
      || m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_one_sheet")
      || m_quadricType
        == ResourceManager.getMessage("hyperboloid_of_two_sheets")) {
      alpha =
        MNumber.squareRoot(
          MNumber.multiply(b, D[0].inverted()).absed());
      beta =
        MNumber.squareRoot(
          MNumber.multiply(b, D[1].inverted()).absed());
      gamma =
        MNumber.squareRoot(
          MNumber.multiply(b, D[2].inverted()).absed());
    }
    else if (
      m_quadricType == ResourceManager.getMessage("point")
        || m_quadricType
          == ResourceManager.getMessage("elliptic_cone")) {
      alpha = MNumber.squareRoot(D[0].inverted().absed());
      beta = MNumber.squareRoot(D[1].inverted().absed());
      gamma = MNumber.squareRoot(D[2].inverted().absed());
    }
    else if (
      m_quadricType == ResourceManager.getMessage("elliptic_cylinder")
        || m_quadricType
          == ResourceManager.getMessage("hyperbolic_cylinder")) {
      alpha =
        MNumber.squareRoot(
          MNumber.multiply(b, D[0].inverted()).absed());
      beta =
        MNumber.squareRoot(
          MNumber.multiply(b, D[1].inverted()).absed());
      gamma = b.create().setDouble(0.0);
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("elliptic_paraboloid")
        || m_quadricType
          == ResourceManager.getMessage("hyperbolic_paraboloid")) {
      P = B[2].absed();
      alpha =
        MNumber.squareRoot(
          P
            .copy()
            .mult((b.create().setDouble(2.0).mult(D[0])).inverted())
            .absed());
      beta =
        MNumber.squareRoot(
          P
            .copy()
            .mult((b.create().setDouble(2.0).mult(D[1])).inverted())
            .absed());
      gamma = b.create().setDouble(0.0);
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("pair_of_intersecting_planes")
        || m_quadricType
          == ResourceManager.getMessage("straight_line")) {
      alpha = MNumber.squareRoot(D[0].inverted().absed());
      beta = MNumber.squareRoot(D[1].inverted().absed());
      gamma = b.create().setDouble(0.0);
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage("parabolic_cylinder")) {
      P =
        MNumber.squareRoot(
          B[1].copy().mult(B[1]).add(B[2].copy().mult(B[2])));
      alpha =
        MNumber.squareRoot(
          P
            .copy()
            .mult((b.create().setDouble(2.0).mult(D[0])).inverted())
            .absed());
      beta = b.create().setDouble(0.0);
      gamma = b.create().setDouble(0.0);
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage(
          "pair_of_coincident_parallel_planes")) {
      alpha = MNumber.squareRoot(D[0].inverted().absed());
      beta = b.create().setDouble(0.0);
      gamma = b.create().setDouble(0.0);
    }
    else if (
      m_quadricType
        == ResourceManager.getMessage(
          "pair_of_distinct_parallel_planes")) {
      alpha =
        MNumber.squareRoot(
          MNumber.multiply(b, D[0].inverted()).absed());
      beta = b.create().setDouble(0.0);
      gamma = b.create().setDouble(0.0);
    }
    else {
      alpha = b.create().setDouble(0.0);
      beta = b.create().setDouble(0.0);
      gamma = b.create().setDouble(0.0);
    }
    return new MNumber[] { alpha, beta, gamma };
  }

  /**
   * @param matrix
   * @return MNumber[]
   */
  private static MNumber[] transpose(MNumber[] matrix) {
    return new MNumber[] {
      matrix[0].copy(),
      matrix[3].copy(),
      matrix[6].copy(),
      matrix[1].copy(),
      matrix[4].copy(),
      matrix[7].copy(),
      matrix[2].copy(),
      matrix[5].copy(),
      matrix[8].copy()};
  }

  /**
   * Iterates over the grid and calculates the parameterization of the quadric for
   * retrieval with <code>getValues()</code>
   *
   * @param flag
   */
  private void calculate(boolean flag) {
    if (!isDegenerated()) {
      m_eigenvaluesOfMinorMatrix =
        eigenvalueOfMinorMatrix(m_quadricMatrix);
      m_quadricType = getQuadricType(m_quadricMatrix);
      m_transformationMatrix = getTransformationMatrix();
      double width = getURange()[1] - getURange()[0];
      double height = getVRange()[1] - getVRange()[0];
      // calculate an equidistant grid within the range and
      // evaluate the parameterization for each value of (x,y) in the grid
      MNumber defU =
        NumberFactory.newInstance(
          m_quadricMatrix.getEntry(1, 1).getClass());
      MNumber defV =
        NumberFactory.newInstance(
          m_quadricMatrix.getEntry(1, 1).getClass());
      MNumber[] sInv = eigenvectorOfMinorMatrix(m_quadricMatrix);
      // sInv = S^(-1) = S^t = matrix of eigenvectors (columnwise)
      MNumber[] D =
        new MNumber[] {
          m_eigenvaluesOfMinorMatrix[0],
          m_eigenvaluesOfMinorMatrix[1],
          m_eigenvaluesOfMinorMatrix[2] };
      // D = array of eigenvalues
      MNumber[] A = new MNumber[] {
        // A = the translationvector corresponding to the standard coordinate system
        MNumber.multiply(
          defU.create().setDouble(2.0),
          m_quadricMatrix.getEntry(1, 4)),
          MNumber.multiply(
            defU.create().setDouble(2.0),
            m_quadricMatrix.getEntry(2, 4)),
          MNumber.multiply(
            defU.create().setDouble(2.0),
            m_quadricMatrix.getEntry(3, 4))};
      MNumber[] B = MatrixMultiplikation.multinxm(sInv, A, 3, 3, 1);
      // B = the translationvector corresponding to the coordinate system given by S
      MNumber b = getb(B, D);
      // b = the constant part of the quadric equation

      MNumber[] alphaBetaGamma = getAlphaBetaGamma(b, D, B);
      MNumber alpha = alphaBetaGamma[0];
      MNumber beta = alphaBetaGamma[1];
      MNumber gamma = alphaBetaGamma[2];

      for (int i = 0; i <= STEPS; i++) {
        for (int j = 0; j <= STEPS; j++) {
          m_values[i][j] = new NumberTuple(MDouble.class, 3);
          defU.setDouble(getURange()[0] + (i * width) / STEPS);
          defV.setDouble(getVRange()[0] + (j * height) / STEPS);

          // calculate the parameterization and store
          // it in the grid
          MNumber[] par =
            getParameterization(
              flag,
              defU,
              defV,
              alpha,
              beta,
              gamma);
          // the parameterization of the quadric
          MNumber[] helper = getCoordinates(par);
          m_values[i][j].setEntryRef(1, helper[0]);
          m_values[i][j].setEntryRef(2, helper[1]);
          m_values[i][j].setEntryRef(3, helper[2]);
        }
      }
    }
//    m_recalculationNeeded = false;
  }

  /**
   * Returns true if the given quadric is a point or a straight line.
   *
   * @return boolean
   */
  private boolean isDegenerated() {
    return (
      getQuadricType(m_quadricMatrix)
        == ResourceManager.getMessage("point")
        || getQuadricType(m_quadricMatrix)
          == ResourceManager.getMessage("straight_line"));
  }

  /**
   * If the type of this quadric is a point this method returns the point,
   * otherwise the origin of the transformed coordinate system is returned.
   *
   * @return an <code>Affine3DPoint</code>.
   */
  public Affine3DPoint getPoint() {
    return new Affine3DPoint(MDouble.class, // return S*(a1 + a0)
    m_transformationMatrix.getEntry(1, 4).getDouble(),
      m_transformationMatrix.getEntry(2, 4).getDouble(),
      m_transformationMatrix.getEntry(3, 4).getDouble());
  }

  /**
   * If this quadric type is a straight line this method returns two different points on this line,
   * otherwise the origin is returned.
   *
   * @return a <code>double[][]</code> holding the coordinates of two different
   * points on this line or two times the origin.
   */
  public double[][] getPoints() {
    if (getQuadricType(m_quadricMatrix)
      == ResourceManager.getMessage("straight_line")) {
      MNumber[] secondPoint =
        new MNumber[] {
          m_transformationMatrix.getEntry(1, 3).add(
            m_transformationMatrix.getEntry(1, 4)),
          m_transformationMatrix.getEntry(2, 3).add(
            m_transformationMatrix.getEntry(2, 4)),
          m_transformationMatrix.getEntry(3, 3).add(
            m_transformationMatrix.getEntry(3, 4))};
      return new double[][] {
        {
          m_transformationMatrix.getEntry(1, 4).getDouble(),
          m_transformationMatrix.getEntry(2, 4).getDouble(),
          m_transformationMatrix.getEntry(3, 4).getDouble()},
        // first point = S*(a1 + a0)
        {
          secondPoint[0].getDouble(),
          // second point = last column of S*T + S*(a1 + a0)
          secondPoint[1].getDouble(), secondPoint[2].getDouble()
          }
      };
    }
    else
      return new double[][] { { 0.0, 0.0, 0.0 }, {
        0.0, 0.0, 0.0 }
    };
  }

  /**
   * Returns the transformation matrix T of this quadric. T has the form<p>
   * [r_11 r_12 r_13 t_1]<br>
   * [r_12 r_22 r_23 t_2]   = T<br>
   * [r_13 r_23 r_33 t_3]<br>
   * [0      0      0      1    ],<p>
   * where <p>
   * [r_11 r_12 r_13]<br>
   * [r_12 r_22 r_23]<br>
   * [r_13 r_23 r_33]<p>
   * describes the rotation and <p>
   * [t_1]<br>
   * [t_2]<br>
   * [t_3]<p>
   * the translation with respect to the standard coordinate system.
   *
   * @return   a <code>NumberMatrix</code> holding the transformation matrix T.
   */
  public NumberMatrix getTransformationMatrix() {
    // the transformation matrix is given by:
    // [S*T_11  S*T_12  S*T_13  S*(a1+a0)_1 ]
    // [S*T_21  S*T_22  S*T_23  S*(a1+a0)_2 ]
    // [S*T_31  S*T_32  S*T_33  S*(a1+a0)_3 ]
    // [     0       0       0            1]

    MNumber[] A =
      new MNumber[] {
        MNumber.multiply(
          m_quadricMatrix.getEntry(1, 1).create().setDouble(2.0),
          m_quadricMatrix.getEntry(1, 4)),
        MNumber.multiply(
          m_quadricMatrix.getEntry(1, 1).create().setDouble(2.0),
          m_quadricMatrix.getEntry(2, 4)),
        MNumber.multiply(
          m_quadricMatrix.getEntry(1, 1).create().setDouble(2.0),
          m_quadricMatrix.getEntry(3, 4))};
    // A is needed for the computation of B.
    MNumber[] sInv = eigenvectorOfMinorMatrix(m_quadricMatrix);
    MNumber[] B = MatrixMultiplikation.multinxm(sInv, A, 3, 3, 1);
    // B is needed for the
    // computation of T, b, a0 and a1.
    MNumber[] S = transpose(sInv);

    MNumber[] T = getT(B);
    MNumber[] ST = MatrixMultiplikation.multinxm(S, T, 3, 3, 3); // S*T
    MNumber[] D =
      new MNumber[] {
        m_eigenvaluesOfMinorMatrix[0],
        m_eigenvaluesOfMinorMatrix[1],
        m_eigenvaluesOfMinorMatrix[2] };
    // D is needed for
    // the computation of b and a0
    MNumber b = getb(B, D); // b is needed for the computation of a1
    MNumber[] a0 = geta0(B, D);
    MNumber[] a1 = geta1(B, b);
    MNumber[] a1plusa0 =
      new MNumber[] {
        a1[0].copy().add(a0[0]),
        a1[1].copy().add(a0[1]),
        a1[2].copy().add(a0[2])};
    // a1+a0
    MNumber[] sMulta1plusa0 =
      MatrixMultiplikation.multinxm(S, a1plusa0, 3, 3, 1);
    // S*(a1+a0)
    // now compute the return matrix:
    MNumber[] matrix =
      new MNumber[] {
        ST[0],
        ST[1],
        ST[2],
        sMulta1plusa0[0],
        ST[3],
        ST[4],
        ST[5],
        sMulta1plusa0[1],
        ST[6],
        ST[7],
        ST[8],
        sMulta1plusa0[2],
        m_quadricMatrix.getEntry(1, 1).create().setDouble(0.0),
        m_quadricMatrix.getEntry(1, 1).create().setDouble(0.0),
        m_quadricMatrix.getEntry(1, 1).create().setDouble(0.0),
        m_quadricMatrix.getEntry(1, 1).create().setDouble(1.0)};
    return new NumberMatrix(MNumber.class, 4, matrix);
  }

  /**
   * Returns {@link #m_version}, the version number of this quadric.
   *
   * @return an <code>int</code>.
   *
   */
  public int getVersion() {
    return m_version;
  }

  public int getGeomType() {
    return GeometryIF.AFFINE3D_GEOMETRY;
  }

  public Class getNumberClass() {
    return m_quadricMatrix.getNumberClass();
  }

  public GeometryIF groupAction(GroupElementIF actingGroupElement) {
    // returns T^{-t}CT^{-1}
    NumberMatrix tInverse =
      new NumberMatrix(m_quadricMatrix.getNumberClass(), 4, 4);
    NumberMatrix tInverseTrans =
      new NumberMatrix(m_quadricMatrix.getNumberClass(), 4, 4);
    tInverse =
      ((AffineGroupElement) actingGroupElement.inverse())
        .getLinearMatrixRepresentation();
    tInverseTrans = tInverse.transposed();
    m_quadricMatrix =
      standardize((tInverseTrans.mult(m_quadricMatrix)).mult(tInverse));
    m_version++;
    return this;
  }

  /**
   * Returns the {@link #m_quadricMatrix} representing this quadric.
   *
   * @return a <code>NumberMatrix</code> holding the quadric matrix.
   */
  public NumberMatrix getMatrix() {
    return m_quadricMatrix;
  }

  /**
   * Changes this quadric into the quadric given by <code>quadricMatrix</code>
   * and returns it also.
   *
   * @param quadricMatrix a <code>NumberMatrix</code>.
   *
   * @return the <code>Affine3DQuadric</code> determined by <code>quadricMatrix</code>.
   * @mm.sideeffects changes {@link #m_quadricMatrix}, increments the version number {@link #m_version}.
   */
  public MMAffine3DQuadric setFromQuadricMatrix(NumberMatrix quadricMatrix) {
    if (quadricMatrix.getDimension() == 4) {
      quadricMatrix = standardize(quadricMatrix);
      m_quadricMatrix.setEntry(1, 1, quadricMatrix.getEntry(1, 1));
      m_quadricMatrix.setEntry(1, 2, quadricMatrix.getEntry(1, 2));
      m_quadricMatrix.setEntry(1, 3, quadricMatrix.getEntry(1, 3));
      m_quadricMatrix.setEntry(1, 4, quadricMatrix.getEntry(1, 4));
      m_quadricMatrix.setEntry(2, 1, quadricMatrix.getEntry(2, 1));
      m_quadricMatrix.setEntry(2, 2, quadricMatrix.getEntry(2, 2));
      m_quadricMatrix.setEntry(2, 3, quadricMatrix.getEntry(2, 3));
      m_quadricMatrix.setEntry(2, 4, quadricMatrix.getEntry(2, 4));
      m_quadricMatrix.setEntry(3, 1, quadricMatrix.getEntry(3, 1));
      m_quadricMatrix.setEntry(3, 2, quadricMatrix.getEntry(3, 2));
      m_quadricMatrix.setEntry(3, 3, quadricMatrix.getEntry(3, 3));
      m_quadricMatrix.setEntry(3, 4, quadricMatrix.getEntry(3, 4));
      m_quadricMatrix.setEntry(4, 1, quadricMatrix.getEntry(4, 1));
      m_quadricMatrix.setEntry(4, 2, quadricMatrix.getEntry(4, 2));
      m_quadricMatrix.setEntry(4, 3, quadricMatrix.getEntry(4, 3));
      m_quadricMatrix.setEntry(4, 4, quadricMatrix.getEntry(4, 4));
      m_version++;
//      m_recalculationNeeded = true;
      m_eigenvaluesOfMinorMatrix =
        eigenvalueOfMinorMatrix(m_quadricMatrix);
      m_transformationMatrix = getTransformationMatrix();
    }
    else
      throw new IllegalArgumentException("Wrong dimension of quadric matrix!");
    return this;
  }

  /**
   * Changes this quadric into the quadric corresponding to<p>
   * a[0]*x� + a[1]*xy + a[2]*xz + a[3]*y� + a[4]*yz + a[5]*z� + a[6]*x + a[7]*y + a[8]*z + a[9] = 0,<p>
   * where <code>a[]</code> is the <code>quadricMatrixArray</code> and returns it.
   *
   * @param quadricMatrixArray a <code>MNumber[]</code> holding the coefficients for the quadric equation.
   * @return the <code>Affine3DQuadric</code> determined by <code>quadricMatrix</code>.
   * @mm.sideeffects changes {@link #m_quadricMatrix}, increments the version number {@link #m_version}.
   */
  public MMAffine3DQuadric setFromQuadricMatrix(MNumber[] quadricMatrixArray) {
    if (quadricMatrixArray.length == 10) {
      m_quadricMatrix.setEntry(1, 1, quadricMatrixArray[0]); //x�
      m_quadricMatrix.setEntry(
        1,
        2,
        quadricMatrixArray[1].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //xy
      m_quadricMatrix.setEntry(
        1,
        3,
        quadricMatrixArray[2].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //xz
      m_quadricMatrix.setEntry(
        1,
        4,
        quadricMatrixArray[6].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //x
      m_quadricMatrix.setEntry(
        2,
        1,
        quadricMatrixArray[1].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //xy
      m_quadricMatrix.setEntry(2, 2, quadricMatrixArray[3]); //y�
      m_quadricMatrix.setEntry(
        2,
        3,
        quadricMatrixArray[4].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //yz
      m_quadricMatrix.setEntry(
        2,
        4,
        quadricMatrixArray[7].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //y
      m_quadricMatrix.setEntry(
        3,
        1,
        quadricMatrixArray[2].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //xz
      m_quadricMatrix.setEntry(
        3,
        2,
        quadricMatrixArray[4].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //xy
      m_quadricMatrix.setEntry(3, 3, quadricMatrixArray[5]); //z�
      m_quadricMatrix.setEntry(
        3,
        4,
        quadricMatrixArray[8].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //z
      m_quadricMatrix.setEntry(
        4,
        1,
        quadricMatrixArray[6].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //x
      m_quadricMatrix.setEntry(
        4,
        2,
        quadricMatrixArray[7].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //y
      m_quadricMatrix.setEntry(
        4,
        3,
        quadricMatrixArray[8].copy().div(
          quadricMatrixArray[0].create().setDouble(2.0)));
      //z
      m_quadricMatrix.setEntry(4, 4, quadricMatrixArray[9]); //const
      m_quadricMatrix = standardize(m_quadricMatrix);
      m_eigenvaluesOfMinorMatrix =
        eigenvalueOfMinorMatrix(m_quadricMatrix);
      m_quadricType = getQuadricType(m_quadricMatrix);
      m_version++;
//      m_recalculationNeeded = true;
      m_transformationMatrix = getTransformationMatrix();
    }
    else
      throw new IllegalArgumentException(" wrong dimension of quadric matrix array");
    return this;
  }
  public String toString() {
    return m_quadricMatrix.toString();
  }

	public int getDefaultTransformType() {
		return GeneralTransformer.FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM;
	}

}
