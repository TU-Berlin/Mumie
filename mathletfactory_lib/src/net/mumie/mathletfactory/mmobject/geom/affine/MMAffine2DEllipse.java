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

import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * Class for the construction of an ellipse.
 * The ellipse is internally represented by a symmetric 3x3
 * {@link NumberMatrix}. If a quadric in
 * homogeneous coordinates is given by the equation
 * c11 X� + c22 Y� + c33 W� + 2c12 XY + 2c13 XW + 2c23 YW = 0 then
 * the corresponding symmetric quadricMatrix is given by<p>
 * [c11 c12 c13] <br>
 * [c12 c22 c23]<br>
 * [c13 c23 c33].<p>
 * This projective quadric corresponds to the affine quadric given by
 * c11 X� + c22 Y� + 2c12 XY + 2c13 X + 2c23 Y + c33 = 0.
 * This quadric matrix represents an ellipse if the two eigenvalues of the
 * minor matrix<p>
 * [c11 c12]<br>
 * [c21 c22]<p>
 * have the same sign and are |=0 and if c11*det C < 0.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMAffine2DEllipse extends MMAffine2DQuadric {

  private static final double EPSILON = 1e-14;

//  private boolean m_isFilled = false;

  /**
   * Class constructor specifying the <code>quadricMatrix</code> of type
   * {@link NumberMatrix}
   * of the ellipse to create.
   * @throws IllegalArgumentException if <code>quadricMatrix</code>
   * does not describe an ellipse.
   */
  public MMAffine2DEllipse(NumberMatrix quadricMatrix) {
    super(quadricMatrix);
    if( getQuadricType(quadricMatrix) == ResourceManager.getMessage("ellipse"))
      m_quadricMatrix = quadricMatrix;
    else throw new IllegalArgumentException("Given matrix does not describe an ellipse!");
  }

  /**
   * Class constructor creating an ellipse by the center, by the radian between
   * the semi axes and the canonic coordinate system, by the length of the
   * semi axes, and by the desired number class of the quadric matrix
   * representing the ellipse.
   * The radian describes the angle between the x-axis and
   * the first semi axis assumed that the center of the ellipse is the origin.
   * The first semi axis then lies in the
   * first quadrant. Therefore <code>radian</code> has to be element of [0, PI/2[.
   * <code>lengthFirstSemiAxis</code> and <code>lengthSecondSemiAxis</code>
   * have to be positive.
   * With <code>matrixNumberClass</code> the desired number class of the
   * quadric matrix representing the ellipse can be
   * determined.
   * @throws IllegalArgumentException if the length of the
   * semi axes is not positive or <code>radian</code> is no element of [0, PI/2[.
   */
  public MMAffine2DEllipse(double[] center, double radian,
                         double lengthFirstSemiAxis, double lengthSecondSemiAxis,
                         Class matrixNumberClass) {
    super(matrixNumberClass);
    setFromPolarCoordinates(center, radian, lengthFirstSemiAxis, lengthSecondSemiAxis, matrixNumberClass);
  }

  /**
   * Class constructor creating an ellipse by the center, by the radian between
   * the semi axes and the canonic coordinate system, and by the length of the
   * semi axes. The radian describes the angle between the x-axis and the
   * first semi axis assumed that the center of the ellipse is the origin.
   * The first semi axis then lies in the
   * first quadrant. Therefore <code>radian</code> has to be element of [0, PI/2[.
   * <code>lengthFirstSemiAxis</code> and <code>lengthSecondSemiAxis</code>
   * have to be positive.
   * The number class of the quadric matrix is
   * {@link net.mumie.mathletfactory.math.number.MDouble}.
   * @throws IllegalArgumentException if the length of the
   * semi axes is not positive or <code>radian</code> is no element of [0, PI/2[.
   */
  public MMAffine2DEllipse(double[] center, double radian,
                         double lengthFirstSemiAxis, double lengthSecondSemiAxis) {
    this(center, radian, lengthFirstSemiAxis, lengthSecondSemiAxis, MDouble.class);
  }

  /**
   * Class constructor creating an ellipse by the two focal points of
   * the ellipse, by the sum of the distances between an arbitrary point
   * on the ellipse and the two focal points, and by the desired number class
   * of the quadric matrix representing the ellipse.
   * @throws IllegalArgumentException if
   * <code>distance</code> is less
   * than the distance between the two focal points.
   */
  public MMAffine2DEllipse(double[] focalPoints, double distance, Class matrixNumberClass) {
    super(matrixNumberClass);
    if (distance > Math.sqrt((focalPoints[0]-focalPoints[2])
                               *(focalPoints[0]-focalPoints[2])
                               + (focalPoints[1]-focalPoints[3])
                               *(focalPoints[1]-focalPoints[3]))) {
      double[] center;
      double radian, lengthMinorSemiAxis, lengthMajorSemiAxis;
      center = getCenterByFocalPoints(focalPoints);
      radian = getRadianByFocalPoints(focalPoints);
      lengthMinorSemiAxis =
        getLengthMinorSemiAxisByFocalPointsAndDistance(focalPoints,
                                                       distance);
      lengthMajorSemiAxis =
        getLengthMajorSemiAxisByDistance(distance);
      if (radian < Math.PI/2.0) {
        m_quadricMatrix = getMatrixByPolarCoordinates(center,
                                                      radian,
                                                      lengthMajorSemiAxis,
                                                      lengthMinorSemiAxis,
                                                      matrixNumberClass);
      }
      else {
        m_quadricMatrix = getMatrixByPolarCoordinates(center,
                                                      radian-Math.PI/2.0,
                                                      lengthMinorSemiAxis,
                                                      lengthMajorSemiAxis,
                                                      matrixNumberClass);

      }
    }
    else throw new
        IllegalArgumentException("distance must be longer than the distance between the two focal points.");
  }

  /**
   * Class constructor creating an ellipse by the two focal points of
   * the ellipse and by the sum of the distances between an arbitrary point
   * on the ellipse and the two focal points.
   * The number class of the quadric matrix is
   * {@link net.mumie.mathletfactory.math.number.MDouble}.
   * @throws IllegalArgumentException if
   * <code>distance</code> is less
   * than the distance between the two focal points.
   */
  public MMAffine2DEllipse(double[] focalPoints, double distance) {
    this(focalPoints, distance, MDouble.class);
  }
  
  /**
   * Sets the ellipse by the center, by the radian between
   * the semi axes and the canonic coordinate system, by the length of the
   * semi axes, and by the desired number class of the quadric matrix
   * representing the ellipse.
   * The radian describes the angle between the x-axis and
   * the first semi axis assumed that the center of the ellipse is the origin.
   * The first semi axis then lies in the
   * first quadrant. Therefore <code>radian</code> has to be element of [0, PI/2[.
   * <code>lengthFirstSemiAxis</code> and <code>lengthSecondSemiAxis</code>
   * have to be positive.
   * With <code>matrixNumberClass</code> the desired number class of the
   * quadric matrix representing the ellipse can be
   * determined.
   * @throws IllegalArgumentException if the length of the
   * semi axes is not positive or <code>radian</code> is no element of [0, PI/2[.
   */
  public void setFromPolarCoordinates(double[] center,
                                                   double radian,
                                                   double lengthFirstSemiAxis,
                                                   double lengthSecondSemiAxis,
                                                   Class matrixNumberClass) {
    if ( lengthFirstSemiAxis > 0.0 && lengthSecondSemiAxis > 0.0
        && 0.0 <= radian && radian < Math.PI/2.0) {
      m_quadricMatrix = getMatrixByPolarCoordinates(center,
                                                    radian,
                                                    lengthFirstSemiAxis,
                                                    lengthSecondSemiAxis,
                                                    matrixNumberClass);
    }
    else throw new
        IllegalArgumentException("length of semi axes must be positive and radian must lie in [0, PI/2[");
  }

  /**
   * Computes the quadric matrix representing the ellipse given in
   * polar coordinates, i.e., by center, radian and the lengths of the
   * semi axes.
   *
   * @param    center the center of the desired ellipse
   * @param    radian the radian of the desired ellipse
   * @param    lengthFirstSemiAxis the first semi axis of the
   * desired ellipse
   * @param    lengthSecondSemiAxis the second semi axis of the
   * desired ellipse
   * @param    matrixNumberClass   the desired number class of
   * the returned quadric matrix
   *
   * @return   a <code>NumberMatrix</code> representing the desired ellipse
   *
   */
  public NumberMatrix getMatrixByPolarCoordinates(double[] center,
                                                   double radian,
                                                   double lengthFirstSemiAxis,
                                                   double lengthSecondSemiAxis,
                                                   Class matrixNumberClass) {
    // Let the center be given by (c1, c2), the semi-axes by a and b and the
    // radian by r. Let the radian be the angle between the x-axis and the first
    // semi-axis. Define
    // A:= a�sin�r + b�cos�r
    // B:= (b�-a�) sinr cosr
    // C:= a�cos�r + b�sin�r
    // Then the returned NumberMatrix
    // is given by
    // [ A              B             -c1*A -c2*B                     ]
    // [ B              C             -c2*C-c1*B                      ]
    // [ -c1*A -c2*B  -c2*C-c1*B  (c1)�*A + (c2)�*C + 2c1*c2*B - a�b� ]

    double c1 = center[0];
    double c2 = center[1];
    double A, B, C, D, E, F;

    A = lengthFirstSemiAxis*lengthFirstSemiAxis*Math.sin(radian)*Math.sin(radian)
      + lengthSecondSemiAxis*lengthSecondSemiAxis*Math.cos(radian)*Math.cos(radian);

    B = (lengthSecondSemiAxis*lengthSecondSemiAxis
           - lengthFirstSemiAxis*lengthFirstSemiAxis)
      * Math.sin(radian) * Math.cos(radian);

    C = lengthFirstSemiAxis*lengthFirstSemiAxis*Math.cos(radian)*Math.cos(radian)
      + lengthSecondSemiAxis*lengthSecondSemiAxis*Math.sin(radian)*Math.sin(radian);

    D = -c1*A - c2*B;

    E = -c2*C - c1*B;

    F = c1*c1*A + c2*c2*C + 2.0*c1*c2*B
      - lengthFirstSemiAxis*lengthFirstSemiAxis*lengthSecondSemiAxis*lengthSecondSemiAxis;

    MNumber[] matrix = { NumberFactory.newInstance(matrixNumberClass).setDouble(A),
        NumberFactory.newInstance(matrixNumberClass, B),
        NumberFactory.newInstance(matrixNumberClass, D),
        NumberFactory.newInstance(matrixNumberClass, B),
        NumberFactory.newInstance(matrixNumberClass, C),
        NumberFactory.newInstance(matrixNumberClass, E),
        NumberFactory.newInstance(matrixNumberClass, D),
        NumberFactory.newInstance(matrixNumberClass, E),
        NumberFactory.newInstance(matrixNumberClass, F) };

    NumberMatrix M = new NumberMatrix(matrixNumberClass, 3, matrix);

    return M;
  }

  /**
   * Returns the center of this ellipse.
   *
   * @return   a <code>double[]</code> holding the coordinates of the center
   *
   */
  public double[] getCenter() {
    /*Compute center from m_quadricMatrix:
     If the ellipse is given by the quadric x^T A x + x^T b + c = 0 and Q
     is the matrix of the (normalized and positive oriented) eigenvectors
     of the minor matrix A given by
     [c11 c12]
     [c21 c22],
     then this quadric is equivalent to y^T D y + y^T B + c = 0, where
     y := Q^T x, B := Q^T b, and D is a diagonal matrix with the
     eigenvalues l1, l2 of A on the diagonal.

     The center of the ellipse is then given by
     Q^T *(  -B1/(2*l1)                , -B2/(2*l2) )
     = Q^T *( (-v11*b_1-v12*b2)/(2*l1) , (-v21*b1-v22*b2)/(2*l2) ),
     = Q^T *( (-v11*c_13-v12*c23)/l1   , (-v21*c13-v22*c23)/l2   )
     where the matrix of the eigenvectors Q is given by
     [v11 v21]
     [v12 v22].*/

    double[] M = new double[]{m_quadricMatrix.getEntry(1, 1).getDouble(),
        m_quadricMatrix.getEntry(1, 2).getDouble(),
        m_quadricMatrix.getEntry(1, 3).getDouble(),
        m_quadricMatrix.getEntry(2, 2).getDouble(),
        m_quadricMatrix.getEntry(2, 3).getDouble(),
        m_quadricMatrix.getEntry(3, 3).getDouble()};

    double l1 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[0];
    double l2 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[1];
    double[] v1 = {eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[0],
        eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[1]};
    double[] v2 = {eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[2],
        eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[3]};
    double B1 = v1[0]*M[2] + v1[1]*M[4];
    double B2 = v2[0]*M[2] + v2[1]*M[4];
    return new double[]{v1[0]*(-B1/l1) + v2[0]*(-B2/l2),
        v1[1]*(-B1/l1) + v2[1]*(-B2/l2)};
  }

  /**
   * Returns the radian of this ellipse.
   * Since the radian describes the angle between the x-axis and
   * the first semi axis and the first semi axis lies in the
   * first quadrant, the return value is element of [0, PI/2[.
   *
   * @return   a <code>double</code> holding the radian
   *
   */
  public double getRadian() {
    // radian = min{arccos(<e1, v1>), arccos(<e1, v2>)}
    return Math.min(Math.acos(eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[0]),
                    Math.acos(eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[2]));
  }
  /**
   * Returns the length of the first semi axis of this ellipse.
   * The first semi axis is the semi axis lying in the
   * first quadrant assumed that the center of this ellipse
   * is the origin.
   *
   * @return  a <code>double</code> holding the length of the first semi axis
   *
   */
  public double getLengthFirstSemiAxis() {
    double l1 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[0];
    double l2 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[1];
    if( Math.acos(eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[0]) <
       Math.acos(eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[2]) )
      return getLengthSemiAxis(l1);
    else return getLengthSemiAxis(l2);
  }

  /**
   * Returns the length of the second semi axis of this ellipse.
   * The second semi axis is the semi axis lying in the
   * second quadrant assumed that the center of this ellipse
   * is the origin.
   *
   * @return  a <code>double</code> holding the length of the second semi axis
   *
   */
  public double getLengthSecondSemiAxis() {
    double l1 =  eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[0];
    double l2 =  eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[1];
    if( Math.acos(eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[0]) <
       Math.acos(eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[2]) )
      return getLengthSemiAxis(l2);
    else return getLengthSemiAxis(l1);
  }

  /**
   * Computing helper for
   * {@link #getLengthFirstSemiAxis} and
   * {@link #getLengthSecondSemiAxis}.
   *
   * @param    eigenvalue          a  <code>double</code>
   *
   * @return   a <code>double</code>
   *
   */
  private double getLengthSemiAxis(double eigenvalue) {
    /* returns sqrt( 1/eigenvalue * ( B1�/(4*l1) + B2�/(4*l2) - c ) )
     = sqrt (  1/eigenvalue * frac1 + frac2 -c )
     */
    double[] M = new double[]{m_quadricMatrix.getEntry(1, 1).getDouble(),
        m_quadricMatrix.getEntry(1, 2).getDouble(),
        m_quadricMatrix.getEntry(1, 3).getDouble(),
        m_quadricMatrix.getEntry(2, 2).getDouble(),
        m_quadricMatrix.getEntry(2, 3).getDouble(),
        m_quadricMatrix.getEntry(3, 3).getDouble()};

    double l1 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[0];
    double l2 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[1];
    double[] v1 = {eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[0],
        eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[1]};
    double[] v2 = {eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[2],
        eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[3]};
    double B1 = v1[0]*2.0*M[2] + v1[1]*2.0*M[4];
    double B2 = v2[0]*2.0*M[2] + v2[1]*2.0*M[4];
    double frac1 = B1*B1/(4.0*l1);
    double frac2 = B2*B2/(4.0*l2);
    return Math.sqrt(1.0/eigenvalue*(frac1+frac2-M[5]));
  }

  /**
   * Returns the sum of the distances between an arbitrary point
   * on this ellipse and the two focal points of this ellipse.
   *
   * @return a <code>double</code> holding the distance
   *
   */
  public double getDistance() {
    // distance = 2*a, where a is the lengthMajorSemiAxis
    return 2.0*Math.max(getLengthFirstSemiAxis(), getLengthSecondSemiAxis());
  }

  /**
   * Returns the two focal points of this ellipse.
   *
   * @return  a <code>double[]</code> of length 4 holding the coordinates
   * of the two focal points
   *
   */
  public double[] getFocalPoints() {
    // The focal points have the distance c:=sqrt(a�-b�) from the center m,
    // where a is the lengthMajorSemiAxis and b is the lengthMinorSemiAxis.
    // Let v be the vector v = (cos(radian), sin(radian)) which is the unit
    // vector in the direction of the major semi axis.
    // The focal points p and q can then be computed by p = m - c*v and q = m + c*v.
    double radian;
    double lengthMajorSemiAxis = Math.max(getLengthFirstSemiAxis(),
                                          getLengthSecondSemiAxis());
    double lengthMinorSemiAxis = Math.min(getLengthFirstSemiAxis(),
                                          getLengthSecondSemiAxis());
    double c = Math.sqrt(lengthMajorSemiAxis*lengthMajorSemiAxis-
                           lengthMinorSemiAxis*lengthMinorSemiAxis);
    if (Math.abs(lengthMajorSemiAxis - getLengthFirstSemiAxis()) < EPSILON) {
      radian = getRadian();
    }
    else radian = -Math.PI/2.0 + getRadian();
    double[] center = getCenter();
    double[] v;
    v = new double[]{Math.cos(radian), Math.sin(radian)};
    return new double[]{center[0] - c*v[0],
        center[1] - c*v[1],
        center[0] + c*v[0],
        center[1] + c*v[1]};
  }

  /**
   * Returns the center of an ellipse with the focal points
   * <code>focalPoints</code>. The focal points have to be stored
   * in an array of length 4.
   *
   * @param    focalPoints a <code>double[]</code> of length 4 holding
   * the focal points
   *
   * @return   a <code>double</code> holding the center
   *
   */
  private double[] getCenterByFocalPoints(double[] focalPoints) {
    double[] center = new double[2];
    // center = 1/2*(p+q) where p = first focal point and q = second focal point
    center[0] = 1.0/2.0*( focalPoints[0] + focalPoints[2] );
    center[1] = 1.0/2.0*( focalPoints[1] + focalPoints[3] );
    return center;
  }

  /**
   * Returns the radian (in [0, PI[) between the major semi axis
   * and the x-axis.
   *
   * @param    focalPoints  a <code>double[]</code> of length 4 holding the
   * focal points
   *
   * @return   a <code>double</code> holding the radian
   *
   */
  private double getRadianByFocalPoints(double[] focalPoints) {
    // radian = arctan(q-p), where p=first focal point, q=second focal point
    if (focalPoints[3]-focalPoints[1] < 0.0) {
      // atan2 returns values between -pi and pi. We want to return values
      // between [0, PI[. Therefore we distinguish different cases and add PI if
      // necessary.
      return Math.atan2(focalPoints[3]-focalPoints[1],  //q2-p2
                        focalPoints[2]-focalPoints[0])  //q1-p1
        + Math.PI;
    }
    else if (Math.abs(focalPoints[3]-focalPoints[1]) < EPSILON) {
      return 0.0;
    }
    else return Math.atan2(focalPoints[3]-focalPoints[1],     //q2-p2
                           focalPoints[2]-focalPoints[0] );   //q1-p1
  }

  /**
   * @param    distance a <code>double</code> holding the distances between an
   * arbitrary point
   * on this ellipse and the two focal points of this ellipse
   *
   * @return   a <code>double</code> holding the length of the major semi axis
   *
   */
  private double getLengthMajorSemiAxisByDistance(double distance) {
    // a = 1/2 * distance, where a = lengthMajorSemiAxis
    return 1.0/2.0*distance;
  }

  /**
   * @param    focalPoints a <code>double[]</code> of length 4 holding the
   * focal points
   * @param    distance a <code>double</code> holding the difference of the distances between an
   * arbitrary point
   * on this ellipse and the two focal points of this ellipse
   *
   * @return   a <code>double</code> holding the length of the minor semi axis
   *
   */
  private double getLengthMinorSemiAxisByFocalPointsAndDistance(double[] focalPoints,
                                                                double distance) {
    // b = sqrt(a^2 - 1/4*|q-p|^2) where p=first focal point,
    // q=second focal point and a = lengthMajorSemiAxis, b = lengthMinorSemiAxis

    return Math.sqrt(getLengthMajorSemiAxisByDistance(distance)*
                       getLengthMajorSemiAxisByDistance(distance)
                       -1.0/4.0*((focalPoints[2]-focalPoints[0])*
                                   (focalPoints[2]-focalPoints[0])
                                   + (focalPoints[3]-focalPoints[1])*
                                   (focalPoints[3]-focalPoints[1])));
  }

  /**
   * Returns the upper left point of the bounding rectangle of this ellipse,
   * assumed that the semi-axes of this ellipse lie on the
   * coordinate axes.
   *
   * @return   a <code>double[]</code> of length 2 holding the coordinates of
   * the upper left point
   *
   */
  public double[] getUpperLeftPointOfBoundingBox() {
    return new double[]{getCenter()[0]-getLengthFirstSemiAxis(),
        getCenter()[1]+getLengthSecondSemiAxis()};
  }

  /**
   * If P and Q are points in the 2-dimensional space then the method returns a
   * point C such that the vector 0C is orthogonal to the vector Q-P.
   *
   * @param    p  a <code>double[]</code> of length 4 composed of
   * the coordinates of P and Q
   *
   * @return   a <code>double[]</code> of length 2 holding the point C
   *
   */
//  private double[] calcOrthDirection(double[] p) {
//    // c1 = -(q2-p2),
//    // c2 = q1-p1,
//    return new double[]{-(p[3]-p[1]), p[2]-p[0]};
//  }
}














