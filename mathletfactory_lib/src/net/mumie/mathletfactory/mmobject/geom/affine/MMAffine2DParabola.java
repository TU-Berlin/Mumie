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
 * Class for the construction of an parabola.
 * The parabola is internally represented by a symmetric 3x3
 * {@link NumberMatrix}.If a quadric in
 * homogeneous coordinates is given by the equation
 * c_11 X� + c_22 Y� + c_33 W� + 2c_12 XY + 2c_13 XW + 2c_23 YW = 0 then
 * the corresponding symmetric quadricMatrix is given by<p>
 * [c_11 c_12 c_13] <br>
 * [c_12 c_22 c_23]<br>
 * [c_13 c_23 c_33].<p>
 * This projective quadric corresponds to the affine quadric given by
 * c_11 X^2 + c_22 Y^2 + 2c_12 XY + 2c_13 X + 2c_23 Y + c_33 = 0.
 * This quadric matrix represents an parabola if one eigenvalue of the
 * minor matrix<p>
 * [c_11 c_12]<br>
 * [c_21 c_22]<p>
 * is = 0 and the other ist /= 0.
 *
 * @author Liu
 * @mm.docstatus finished
 */
public class MMAffine2DParabola extends MMAffine2DQuadric{

 /**
   * Class constructor specifying the <code>quadricMatrix</code> of type
   * {@link NumberMatrix}
   * of the parabola to create.
   * @throws IllegalArgumentException if <code>quadricMatrix</code>
   * does not describe a parabola.
   */
  public MMAffine2DParabola(NumberMatrix quadricMatrix){
    super(quadricMatrix);
    if(getQuadricType(quadricMatrix) == ResourceManager.getMessage("parabola"))
      m_quadricMatrix = quadricMatrix;
    else throw new IllegalArgumentException("Given matrix does not describe a parabola!");
  }

  /**
   * Class constructor creating a parabola ba the apex, by the radian between
   * the semi axes and the canonic coordinate system, by the distance between
   * the focalpoint and the guideline, and by the desired number class of the
   * quadric matrix representing the parabola.
   * The radian describes the angle between the y-axis and the semi axis.
   * Therefore <code>radian</code> hase to be element of [-PI/2,3*PI/2[,
   * <code>distanceBetweenGuidelineAndFocalPoint</code> has to be positive.
   * With <code>matrixNumberClass</code> the desired number class of the quadric
   * matrix representing the parabola can be determined.
   * @throws IllegalArgumentException if the distance between
   * the focalpoint and the guideline is not positiv or
   * <code>radian</code> is no element of [-PI/2, 3*PI/2[.
   */
  public MMAffine2DParabola (double[] apex, double radian, double distanceBetweenGuidelineAndFocalPoint, Class matrixNumberClass){
    super(matrixNumberClass);
    if( distanceBetweenGuidelineAndFocalPoint > 0.0 && -Math.PI/2 <= radian && radian < 3*Math.PI/2) {
      m_quadricMatrix = getMatrixByPolarCoordinates(apex,radian,distanceBetweenGuidelineAndFocalPoint,matrixNumberClass);
    }
    else throw new
      IllegalArgumentException("Halbparameter must be positive and radian must lie in [-pi/2, 3*pi/2]");
  }

  /**
   * Class constructor creating a parabola by the apex, by the radian between
   * the semi axes and the canonic coordinate system, by the distance between
   * the focal point and the guideline, and by the desired number class of the quadric matrix.
   * The radian describes the angle between the y-axis and the semi axis.
   * Therefore <code>radian</code> hase to be element of [-PI/2,3*PI/2[,
   * <code>distanceBetweenGuidelineAndFocalPoint</code> has to be positive.
   * The number class of the quadric matrix is
   * {@link MDouble}.
   * @throws IllegalArgumentException if the distance between
   * the focalpoint and the guideline is not positiv or
   * <code>radian</code> is no element of [-PI/2, 3*PI/2[.
   */
  public MMAffine2DParabola(double[] apex,double radian,double distanceBetweenGuidelineAndFocalPoint){
    this(apex,radian,distanceBetweenGuidelineAndFocalPoint,MDouble.class);
  }

  /**
   * Class constructor specifying a parabola by apex, by the focal point,
   * and by the desired number class of the quadric matrix representing the parabola.
   * @throws IllegalArgumentException if
   * <code>apex</code> and <code>focalpoint</code> have the same coordinate.
   */
  public MMAffine2DParabola(double[] apex, double[] focalpoint,Class matrixNumberClass){
    super(matrixNumberClass);
    if(!(apex[0]==focalpoint[0] && apex[1] ==focalpoint[1])){
    double radian = 0;
    if (apex[0]<focalpoint[0] && apex[1] >focalpoint[1]){
      radian = -Math.atan((apex[0]-focalpoint[0])/(apex[1]-focalpoint[1]))+Math.PI;
    }
    else if (apex[0]>focalpoint[0] && apex[1]>focalpoint[1]){
      radian = -Math.atan((apex[0]-focalpoint[0])/(apex[1]-focalpoint[1]))+Math.PI;
    }
    else if(apex[1]==focalpoint[1]){
      if(apex[0]>focalpoint[0])
      radian = Math.PI/2;
      else radian = -Math.PI/2;
    }
    else radian = -Math.atan((apex[0]-focalpoint[0])/(apex[1]-focalpoint[1]));

    double P = 2*Math.sqrt((apex[0]-focalpoint[0])*(apex[0]-focalpoint[0])+
              (apex[1]-focalpoint[1])*(apex[1]-focalpoint[1]));
    m_quadricMatrix = getMatrixByPolarCoordinates(apex,radian,P,matrixNumberClass);
  }
  else throw new
      IllegalArgumentException("focal point must be different to apex");
  }

  /**
   * Class constructor specifying a parabola by apex, by the focal point.
   * The number class of the quadric matrix is
   * {@link MDouble}.
   * @throws IllegalArgumentException if
   * <code>apex</code> and <code>focalpoint</code> have the same coordinate.
   */
  public MMAffine2DParabola(double[] apex, double[] focalpoint){
    this (apex,focalpoint,MDouble.class);
  }

  /**
   * Computes the quadric matrix representing the parabola give in
   * polar coordinates.
   *
   * @param    apex the center of the desired parabola
     * @param    radian the radian of the desired parabola
     * @param    distanceBetweenGuidelineAndFocalPoint
   *             the distance between guideline and focal point of the desired parabola
     * @param    matrixNumberClass    the desired number class of the returned quadric matrix
     *
     * @return   a <code>NumberMatrix</code> representing the desired parabola
     *
   */

  private NumberMatrix getMatrixByPolarCoordinates(double[] apex,double radian,
                           double distanceBetweenGuidelineAndFocalPoint,
                           Class matrixNumberClass){
//   Let the apex be given by (c_1, c_2), the  distance between guideline and
//   focal point by P and the  radian by r. Let the radian be the angle between
//   the y-axis and the
//   Then the desired NumberMatrix M is given by
//
//         [ cos�r                                 cosr sinr                        P*sinr - c_1*cos�r-cosr*sinr*c_2  ]
//    M := [ cosr sinr                             sin�r                            -P*cosr-c_1*sinr*cosr-sin�r*c_2   ]
//         [ P*sinr - c_1*cos�r-cosr*sinr*c_2    -P*cosr-c_1*sinr*cosr-sin�r*c_2    cos�r*c_1�+sin�r*c_2�+2*cosr*sinr*c_1*c_2  +2P*cosr*c_2 -2P*sinr*c_1   ]
//
//   If the desired quadric is a point, this method cannot be applied!

    double c1 = apex[0];
    double c2 = apex[1];
    double A,B,C,D,E,F;
    A = Math.cos(radian)*Math.cos(radian);

    B = Math.sin(radian)*Math.cos(radian);
    //System.out.println("B=  "+B);
    C = Math.sin(radian)*Math.sin(radian);
    //System.out.println("C=  "+C);
    D = distanceBetweenGuidelineAndFocalPoint*Math.sin(radian) - c1*Math.cos(radian)*Math.cos(radian)-c2*Math.sin(radian)*Math.cos(radian);
    //System.out.println("D=  "+D);
    E = -distanceBetweenGuidelineAndFocalPoint*Math.cos(radian) - c1*Math.sin(radian)*Math.cos(radian)-c2*Math.sin(radian)*Math.sin(radian);
    //System.out.println("E=  "+E);
    F = c1*c1*Math.cos(radian)*Math.cos(radian)+c2*c2*Math.sin(radian)*Math.sin(radian)
      +2*c1*c2*Math.cos(radian)*Math.sin(radian)+2*distanceBetweenGuidelineAndFocalPoint*Math.cos(radian)*c2
      -2*distanceBetweenGuidelineAndFocalPoint*Math.sin(radian)*c1;
    //System.out.println("F=  "+F);
    MNumber[] matrix = {NumberFactory.newInstance(matrixNumberClass,A),
                         NumberFactory.newInstance(matrixNumberClass,B),
                         NumberFactory.newInstance(matrixNumberClass,D),
                         NumberFactory.newInstance(matrixNumberClass,B),
                         NumberFactory.newInstance(matrixNumberClass,C),
                         NumberFactory.newInstance(matrixNumberClass,E),
                         NumberFactory.newInstance(matrixNumberClass,D),
                         NumberFactory.newInstance(matrixNumberClass,E),
                         NumberFactory.newInstance(matrixNumberClass,F)};
    NumberMatrix M  = new NumberMatrix(matrixNumberClass,3,matrix);
    return M;
  }

  /**
   * Returns the apex of this parabola
   *
   * @return   a <code>double[]</code> holding the apex
   *
   */
  public double[] getApex(){
//   (1)    cosr*c_1 + sinr*c_2 = -(D*cosr + E*sinr)
//   (2)    cosr*c_2 - sinr*c_1 = (F - (1)�)/2*P
//
//   c_1 =  (1)*cosr - (2)*sinr
//   c_2 =  (2)*cosr + (1)*sinr
    double g1 = -(m_quadricMatrix.getEntry(1,3).getDouble()*Math.cos(getRadian())+
              m_quadricMatrix.getEntry(2,3).getDouble()*Math.sin(getRadian()))/
                 (m_quadricMatrix.getEntry(1,1).getDouble()+ m_quadricMatrix.getEntry(2,2).getDouble());
    double g2 = (m_quadricMatrix.getEntry(3,3).getDouble()/
             (m_quadricMatrix.getEntry(1,1).getDouble()+ m_quadricMatrix.getEntry(2,2).getDouble())
             -g1*g1)/(2*getDistanceBetweenGuidelineAndFocalPoint());
    //System.out.println("g1= "+g1);
    //System.out.println("g2= "+g2);

    double a1 = g1*Math.cos(getRadian())-g2*Math.sin(getRadian());
    double a2 = g2*Math.cos(getRadian())+g1*Math.sin(getRadian());
    double[] apex = new double[]{a1,a2};
    //System.out.println("A1= "+a1);
    //System.out.println("A2= "+a2);
    return apex;
   }

  /**
   * Returns the radian of this parabola.
   * The return value lies in [-pi/2,3pi/2[.
   *
   * @return   a <code>double</code> holding the radian
   */

  public double getRadian(){
//  if M11=0, r = PI/2;
//  else  r = atan(M12/M11);
    double r;
    if(m_quadricMatrix.getEntry(1,1).getDouble()!=0){
         r = Math.atan(m_quadricMatrix.getEntry(1,2).getDouble()/m_quadricMatrix.getEntry(1,1).getDouble());
    }
    else r = Math.PI/2;
    //check, whether the focalpoint lies upperleft(upperright ...) of the apex.
    double p = (m_quadricMatrix.getEntry(1,3).getDouble()*Math.sin(r)
                -m_quadricMatrix.getEntry(2,3).getDouble()*Math.cos(r))/
      (m_quadricMatrix.getEntry(1,1).getDouble()+m_quadricMatrix.getEntry(2,2).getDouble());
    if (p >= 0) return r;
    else return  r+Math.PI;

  }

  /**
   * Returns the distance between guideline and focal point of this parabola.
   *
   * @return  a <code>double</code> holding the distance between guideline and focal point
   */
  public double getDistanceBetweenGuidelineAndFocalPoint(){

    return (m_quadricMatrix.getEntry(1,3).getDouble()*Math.sin(getRadian())
            -m_quadricMatrix.getEntry(2,3).getDouble()*Math.cos(getRadian()))/
         (m_quadricMatrix.getEntry(1,1).getDouble()+m_quadricMatrix.getEntry(2,2).getDouble());
  }

  /**
   * Returns the focal point of this parabola.
   *
   * @return  a <code>double[]</code> of length 2 holding the focal point
   *
   */
  public double[] getFocalPoint(){
    //the focal point has the distance P/2 from the Apex
    double radian = getRadian();
    double P = getDistanceBetweenGuidelineAndFocalPoint();
    double c1 = getApex()[0];
    double c2 = getApex()[1];

    double[] F = new double[]{c1-0.5*P*Math.sin(radian),c2+0.5*P*Math.cos(radian)};
//    System.out.println("P=" + P);
//    System.out.println("F1=" + F[0]);
//    System.out.println("F2=" + F[1]);
    return F;
  }

  /*
  public static void main(String[] arg){
    MNumber[] matrix =
     { new MDouble(-1), new MDouble(-1), new MDouble(0.5*Math.sqrt(2)),
      new MDouble(-1), new MDouble(-1), new MDouble(-0.5*Math.sqrt(2)),
      new MDouble(0.5*Math.sqrt(2)), new MDouble(-0.5*Math.sqrt(2)), new MDouble(0) };

   NumberMatrix quadricMatrix = new NumberMatrix(MDouble.class, 3, matrix);
    Affine2DParabola p = new Affine2DParabola(quadricMatrix);
    System.out.println(p.getP());
    System.out.println(p.getRadian());
    System.out.println(p.getApex()[0]);
    System.out.println(p.getApex()[1]);
    System.out.println(p.getFocalPoint()[0]);
    System.out.println(p.getFocalPoint()[1]);

   }*/
}


