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
 * Class for the construction of an hyperbola.
 * The hyperbola is internally represented by a symmetric 3x3 matrix
 * {@link NumberMatrix}. If a quadric in
 * homogeneous coordinates is given by the equation
 * c11 X� + c22 Y� + c33 W� + 2c12 XY + 2c13 XW + 2c23 YW = 0 then
 * the corresponding symmetric quadricMatrix is given by<p>
 * [c_11 c_12 c_13]<br>
 * [c_12 c_22 c_23]<br>
 * [c_13 c_23 c_33].<p>
 * This projective quadric corresponds to the affine quadric given by
 * c11 X� + c22 Y� + 2c12 XY + 2c13 X + 2c23 Y + c33 = 0.
 * The qaudric matrix represents an hyperbola if the two eigenvalues of the
 * minor matrix<p>
 * [c_11 c_12]<br>
 * [c_21 c_22]<p>
 * have a different sign and are |=0 and if det C |= 0.
 *
 * @author Schimanowski, Liu
 * @mm.docstatus finished
 */
public class MMAffine2DHyperbola extends MMAffine2DQuadric {

  private static final double EPSILON = 1e-14;

  /**
   * Class constructor specifying the <code>quadricMatrix</code> of type
   * {@link NumberMatrix}
   * of the hyperpola to create.
   * @throws IllegalArgumentException if <code>quadricMatrix</code>
   * does not describe an ellipse.
   */
  public MMAffine2DHyperbola(NumberMatrix quadricMatrix) {
    super(quadricMatrix);
    if( getQuadricType(quadricMatrix) == ResourceManager.getMessage("hyperbola" ))
      if(quadricMatrix.getEntry(3,3).getDouble() <= 0){
         m_quadricMatrix = quadricMatrix.negate();
      }
//      else if(quadricMatrix.getEntry(3,3).getDouble() == 0 ){
//        m_quadricMatrix = quadricMatrix.negate();
//      }
	  else  m_quadricMatrix = quadricMatrix;
    else throw new IllegalArgumentException("Given matrix does not describe a hyperbola!");
  }

  /**
   * Class constructor creating an hyperbola by the center, by the radian between
   * the semi-axes and the canonic coordinate system and the length of the
   * semi-axes, and by the desired number class of the quadric matrix
   * representing the hyperbola.
   * The radian describes the angle between the x-axis and the
   * FirstSemiAxis. The FirstSemiAxis lies in the first quadrant. Therefore
   * the <code>radian</code> has to be element of [0, pi[.
   * <code>lengthFirstSemiAxis</code> and <code>lengthSecondSemiAxis</code>
   * have to be positive.
   * With <code>matrixNumberClass</code> the desired number class of the
   * quadric matrix representing the hyperbola can be
   * determined.
   * @throws IllegalArgumentException if the length of the semi axes
   * is not positive or <code>radian</code> is no element of [0,Pi[.
   */

  public MMAffine2DHyperbola(double[] center,
                         double radian,
                         double lengthFirstSemiAxis,
                         double lengthSecondSemiAxis,
                         Class matrixNumberClass) {
    super(matrixNumberClass);
    if ( lengthFirstSemiAxis > 0.0 && lengthSecondSemiAxis > 0.0
        && 0.0 <= radian && radian < Math.PI) {
      m_quadricMatrix = getMatrixByPolarCoordinates(center,
                                                  radian,
                                                  lengthFirstSemiAxis,
                                                  lengthSecondSemiAxis,
                                                  matrixNumberClass);
    }
    else throw new
        IllegalArgumentException("length of semi axes must be positive and radian must lie in [0, pi[");
   }

  /**
   * Class constructor creating an hyperbola by the center, by the radian between
   * the semi-axes and the canonic coordinate system and the length of the
   * semi-axes, and by the desired number class of the quadric matrix
   * representing the hyperbola.
   * The radian describes the angle between the x-axis and the
   * FirstSemiAxis. The FirstSemiAxis lies in the first quadrant. Therefore
   * the <code>radian</code> has to be element of [0, pi[.
   * <code>lengthFirstSemiAxis</code> and <code>lengthSecondSemiAxis</code>
   * have to be positive.
   * The number class of the quadric matrix is
   * {@link MDouble}.
   * @throws IllegalArgumentException if the length of the semi axes
   * is not positive or <code>radian</code> is no element of [0,Pi[.
   */

  public MMAffine2DHyperbola(double[] center,
                         double radian,
                         double lengthFirstSemiAxis,
                         double lengthSecondSemiAxis) {
    this(center, radian, lengthFirstSemiAxis, lengthSecondSemiAxis, MDouble.class);
  }

  /**
   * Class constructor creating a hyperbola by the two focal points of
   * the hyperbola, by the difference of the distances between an arbitrary point
   * on the hyperbola and the two focal points, and by the desired number class
   * of the quadric matrix representing the ellipse.
   * @throws IllegalArgumentException if
   * <code>distance</code> is greater
   * than the distance between the two focal points nad not positive.
   */
  public MMAffine2DHyperbola(double[] focalPoints, double distance, Class matrixNumberClass){
	  super(matrixNumberClass);
	  if(distance < Math.sqrt((focalPoints[0]-focalPoints[2])
                               *(focalPoints[0]-focalPoints[2])
                               + (focalPoints[1]-focalPoints[3])
                               *(focalPoints[1]-focalPoints[3]))
		&& distance >0) {
		  double[] center;
		  double radian, lengthSemimajorAxis, lengthSemiminorAxis;
		  center = getCenterByFocalPoints(focalPoints);
		  radian = getRadianByFocalPoints(focalPoints);
		  //System.out.println("RBF=  "+ radian);
		  lengthSemimajorAxis =
			  getLengthSemimajorAxisByDistance(distance);
		  lengthSemiminorAxis =
			  getLengthSemiminorAxisByFocalPointsAndDistance(focalPoints,distance);
			  m_quadricMatrix = getMatrixByPolarCoordinates(center,
															radian,
															lengthSemimajorAxis,
															lengthSemiminorAxis,
															matrixNumberClass);

	  }
	  else throw new
		  IllegalArgumentException("distance must be shorter than the distance between the two focal points and positiv");
  }

  /**
   * Class constructor creating a hyperbola by the two focal points of
   * the hyperbola and by the difference of the distances between an arbitrary point
   * on the hyperbola and the two focal points.
   * The number class of the quadric matrix is
   * {@link MDouble}.
   * @throws IllegalArgumentException if
   * <code>distance</code> is greater
   * than the distance between the two focal points and not positive.
   */
  public MMAffine2DHyperbola(double[] focalPoints, double distance){
	  this(focalPoints, distance,MDouble.class);
  }

  /**
   * @param    focalPoints    a  <code>double[]</code> of length 4 holding the
   * focal points
   * @param    distance       a  <code>double</code> holding the distances between an
   * arbitrary point
   * on this hyperbola and the two focal points of this hyperbola
   *
   * @return   a <code>double</code> holding the length of the
   *
   */
  private double getLengthSemiminorAxisByFocalPointsAndDistance(double[] focalPoints,double distance){
	  return Math.sqrt(1.0/4.0*((focalPoints[2]-focalPoints[0])*
                                   (focalPoints[2]-focalPoints[0])
                                   + (focalPoints[3]-focalPoints[1])*
                                   (focalPoints[3]-focalPoints[1]))
									-getLengthSemimajorAxisByDistance(distance)*
									getLengthSemimajorAxisByDistance(distance));
  }


  /**
   * @param    distance   a <code>double</code> holding the difference of the
   * distances between an arbitrary point
   * on this hyperbola and the two focal points of this hyperbola
   *
   * @return  a <code>double</code> holding the length of the major semi axis
   *
   */
  private double getLengthSemimajorAxisByDistance(double distance){
	  return 0.5*distance;
  }

  /**
   * Returns the center of a hyperbola with the focal points
   * <code>focalPoints</code>. The focal points have to be stored
   * in an array of length 4.
   *
   * @param    focalPoints a <code>double[]</code> of length 4 holding
   * the focal points
   *
   * @return   a <code>double</code> holding the center
   *
   */
  private double[] getCenterByFocalPoints(double[] focalPoints){
	  double[] center = new double[2];
	  center[0] = 0.5*( focalPoints[0] + focalPoints[2] );
	  center[1] = 0.5*( focalPoints[1] + focalPoints[3] );
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
  private double getRadianByFocalPoints(double[] focalPoints){
	  double radian = Math.atan2(focalPoints[3]-focalPoints[1],
								 focalPoints[2]-focalPoints[0]);
	  if (radian < 0){return radian + Math.PI;}
	  else if ( Math.abs(focalPoints[3]-focalPoints[1]) < EPSILON) {
        return 0.0;
      }
	  else return radian;
  }

  /**
   * Computes the quadric matrix representing the ellipse given in
   * polar coordinates, i.e., by center radian and the length of the semi axes.
   *
   * @param    center the center of the desired hyperbola
   * @param    radian the radian of the desired hyperbola
   * @param    lengthFirstSemiAxis the first semi axes of the desired hyperbola
   * @param    lengthSecondSemiAxis the second semi axes of the desired hyperbola
   * @param    matrixNumberClass the desired number class of the returned quadric matrix
   *
   * @return   a <code>NumberMatrix</code> representing the desired hyperbola
   *
   */

  private NumberMatrix getMatrixByPolarCoordinates(double[] center,
                                                   double radian,
                                                   double lengthFirstSemiAxis,
                                                   double lengthSecondSemiAxis,
                                                   Class matrixNumberClass){

  // Let the center be given by (c_1, c_2), the semi-axes by a and b and the
  // radian by r. Let the radian be the angle between the x-axis and the first
  // semi-axis. Define
  // A:= a�sin�r - b�cos�r
  // B:= (-b�-a�) sinr cosr
  // C:= a�cos�r - b�sin�r
  // Then the desired NumberMatrix M is given by
  //      [ A              B             -c_1*A -c_2*B                           ]
  // M := [ B              C             -c_2*C-c_1*B                            ]
  //      [ -c_1*A -c_2*B  -c_2*C-c_1*B  (c_1)�*A + (c_2)�*C + 2c_1*c_2*B + a�b� ]

    double c1 = center[0];
    double c2 = center[1];
    double A, B, C, D, E, F;


    C = lengthFirstSemiAxis*lengthFirstSemiAxis*Math.cos(radian)*Math.cos(radian)
      - lengthSecondSemiAxis*lengthSecondSemiAxis*Math.sin(radian)*Math.sin(radian);

    B = (-lengthSecondSemiAxis*lengthSecondSemiAxis - lengthFirstSemiAxis*lengthFirstSemiAxis)
      * Math.sin(radian) * Math.cos(radian);

    A = lengthFirstSemiAxis*lengthFirstSemiAxis*Math.sin(radian)*Math.sin(radian)
      - lengthSecondSemiAxis*lengthSecondSemiAxis*Math.cos(radian)*Math.cos(radian);

    D = -c1*A - c2*B;

    E = -c2*C - c1*B;

    F = (c1*c1*A + c2*c2*C + 2.0*c1*c2*B
      + lengthFirstSemiAxis*lengthFirstSemiAxis*lengthSecondSemiAxis*lengthSecondSemiAxis);
//    System.out.println("A=  " + A);
//	System.out.println("B=  " + B);
//	  System.out.println("C=  " + C);
//	  System.out.println("D=  " + D);
//	  System.out.println("E=  " + E);
//	  System.out.println("F=  " + F);
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
   * Returns the center of this hyperbola
   *
   * @return a>code>double[]</code> holding the coordinates of the center
   *
   */
  public double[] getCenter() {
    /*Compute center from m_quadricMatrix:
     If the hyperbola is given by the quadric x^T A x + x^T b + c = 0 and Q
     is the matrix of the (normalized and positive oriented) eigenvectors
     of the minor matrix A given by
     [c11 c12]
     [c21 c22],
     then this quadric is equivalent to y^T D y + y^T B + c = 0, where
     y := Q^T x, B := Q^T b, and D is a diagonal matrix with the
     eigenvalues l1, l2 of A on the diagonal.

     The center of the hyperbola is then given by
     Q^T *(  -B1/(2*l1)               , -B2/(2*l2) )
     = Q^T *( (-v11*b1-v12*b2)/(2*l1) , (-v21*b1-v22*b2)/(2*l2) ),
     = Q^T *( (-v11*c13-v12*c23)/l1   , (-v21*c13-v22*c23)/l2   )
     where the matrix of the eigenvectors Q is given by
     [v_11 v_21]
     [v_12 v_22].*/

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
   * Returns the radian of this hyperbola.
   * Since the radian describes the angle between the x-axis and the first semi axis,
   * the return value is element of [0,Pi[.
   *
   * @return a <code>double</code> holding the radian
   *
   */
  public double getRadian() {
    return Math.acos(eigenvectorOfMinorMatrixDouble(m_quadricMatrix)[0]);
  }
  /**
   * Returns the length of the first semi axis of this hyperbola.
   * The first semi axisis the semi axis lying in the first
   * quadrant.
   *
   * @return a <code>double</code> holding the length of the first semi axis
   */
  public double getLengthSemimajorAxis() {
    double l1 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[0];
    double l2 = eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[1];

    if(l1<0){

      return getLengthSemiAxis(l1);}

    else {
      return getLengthSemiAxis(l2);
    }
  }
  /**
   * Returns the length of the second semi axis of this hyperbola.
   * The second semi axis is the semi axis lying in the
   * second quadrant.
   *
   * @return a <code>double</code> holding the length of the second semi axis
   */
  public double getLengthSemiminorAxis() {
    double l1 =  eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[0];
    double l2 =  eigenvalueOfMinorMatrixDouble(m_quadricMatrix)[1];

    if(l2>0){

      return getLengthSemiAxis(l2);}
    else{

      return getLengthSemiAxis(l1);}
  }
  /**
   * Computing helper for
   * {@link #getLengthSemimajorAxis} and
   * {@link #getLengthSemiminorAxis}
   *
   * @param eigenvalue a <code>double</code>
   */
  private double getLengthSemiAxis(double eigenvalue) {

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

    return Math.sqrt(Math.abs(1.0/eigenvalue*(frac1+frac2-M[5])));
  }

  /**
   *  Returns the upper left point of the bounding rectangle of this hyperbola, assumed
   * that the semi-axes of this hyperbola lie on the coordinate axes
   *
   * @return a <code>double[]</code> of length 2holding the coordinates of the
   * upper left point
   *
   */

  public double[] getUpperLeftPointOfBoundingBox() {
    return new double[]{getCenter()[0]-getLengthSemimajorAxis(),
        getCenter()[1]+getLengthSemiminorAxis()};
  }

  /**
   * Returns the two focal points of this hyperbola.
   *
   * @return  a <code>double[]</code> of length 4 holding the coordinates
   * of the two focal points
   *
   */
  public double[] getFocalPoints(){
	  double radian = getRadian();
	  double c = Math.sqrt(getLengthSemimajorAxis()*getLengthSemimajorAxis()+
							   getLengthSemiminorAxis()*getLengthSemiminorAxis());
	  double [] center = getCenter();
	  return new double[]{center[0] - c*Math.cos(radian),
	                      center[1] - c*Math.sin(radian),
	                      center[0] + c*Math.cos(radian),
	                      center[1] + c*Math.sin(radian)};
  }

  /**
   * Returns the difference of the distances between an arbitrary point
   * on this hyperbola and the two focal points of this hyperbola.
   *
   * @return a <code>double</code> holding the distance
   *
   */
  public double getDistance(){
	  return 2.0*getLengthSemimajorAxis();
  }
  /**
   * returns two points,which lie on the asymptotes.
   *
   * @return  a <code>double[]</code> of length 4holding the coordinates of
   * the two points
   */
  public double[] getAsymptote(){
	  double radian = getRadian();
	  double [] c = getCenter();
	  return new double[] {c[0]+getLengthSemimajorAxis()*Math.cos(radian)-getLengthSemiminorAxis()*Math.sin(radian),
	                       c[1]+getLengthSemimajorAxis()*Math.sin(radian)+getLengthSemiminorAxis()*Math.cos(radian),
	                       c[0]-getLengthSemimajorAxis()*Math.cos(radian)-getLengthSemiminorAxis()*Math.sin(radian),
	                       c[1]-getLengthSemimajorAxis()*Math.sin(radian)+getLengthSemiminorAxis()*Math.cos(radian)};
  }

  public NumberMatrix getMatrix(){
	  return m_quadricMatrix;
  }
}

















