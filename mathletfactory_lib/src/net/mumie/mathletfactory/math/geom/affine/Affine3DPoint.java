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

package net.mumie.mathletfactory.math.geom.affine;


import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  This class represents a point in the affine 3d space.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class Affine3DPoint extends AffinePoint {


  /**
   * Creates an affine point in a three-dimensional space.
   * Coordinates are based on a number class <code>entryClass</code> and are
   * initialized to zero.
   */
  public Affine3DPoint (Class numberClass){
    super (numberClass,3);
  }

  /**
   * Creates the affine 3d point from the number class and coordinates of
   * the given number tuple.
   */
  public Affine3DPoint (NumberTuple coords){
    super(coords.getNumberClass(), 3);
    setAffineCoordinates(coords);
  }

  /**
   * Creates the affine 3d point from the number class and coordinates of
   * the given point.
   */
  public Affine3DPoint (Affine3DPoint anAffinePoint) {
    this(anAffinePoint.getNumberClass());
    setProjectiveCoordinates(anAffinePoint.getProjectiveCoordinatesOfPoint());
  }

  /**
   * Creates the affine 3d point for the given number class and coordinates.
   */
  public Affine3DPoint(Class numberClass, double x, double y, double z) {
    this(numberClass);
    NumberTuple coords = new NumberTuple(numberClass,
                               new Object[]{NumberFactory.newInstance(numberClass, x),
                                            NumberFactory.newInstance(numberClass, y),
                                            NumberFactory.newInstance(numberClass, z),
                                            NumberFactory.newInstance(numberClass, 1.0)});
    setProjectiveCoordinates(coords);
  }

  /**
   * Sets the affine coordinates for this point and returns it.
   */
  public Affine3DPoint setXYZ(double[] xyz) {
    return setXYZ(xyz[0],xyz[1], xyz[2]);
  }

  /**
   * Sets the affine coordinates for this point and returns it
   */
  public Affine3DPoint setXYZ(double x, double y, double z){
    getProjectiveBasisMatrixRef().setEntry(1, 1, x);
    getProjectiveBasisMatrixRef().setEntry(2, 1, y);
    getProjectiveBasisMatrixRef().setEntry(3, 1, z);
    getProjectiveBasisMatrixRef().setEntry(4, 1, 1);
   return this;
  }

  /**
   * Returns the affine coordinates of this point as double array.
   */
  public double[] getXYZ(){
    return new double[]{ getXAsDouble(), getYAsDouble(), getZAsDouble()};
  }

  /**
   * Returns the affine coordinate <i>x/i> as <code>double</code>
   */
  public double getXAsDouble(){
    return   getProjectiveBasisMatrixRef().getEntry(1, 1).getDouble()/
    getProjectiveBasisMatrixRef().getEntry(4, 1).getDouble();
  }

  /**
   * Returns the affine coordinate <i>y/i> as <code>double</code>
   */
  public double getYAsDouble(){
    return   getProjectiveBasisMatrixRef().getEntry(2, 1).getDouble()/
    getProjectiveBasisMatrixRef().getEntry(4, 1).getDouble();
  }

  /**
   * Returns the affine coordinate <i>z</i> as <code>double</code>
   */
  public double getZAsDouble(){
    return   getProjectiveBasisMatrixRef().getEntry(3, 1).getDouble()/
    getProjectiveBasisMatrixRef().getEntry(4, 1).getDouble();
  }

  /**
   * Returns the projective coordinate <i>x</i> as reference to a
   * {@link net.mumie.mathletfactory.math.number.MNumber}.
   */
  public MNumber getXProjAsNumberRef(){
    return getProjectiveBasisMatrixRef().getEntry(1, 1);
  }

  /**
   * Returns the projective coordinate <i>y</i> as reference to a
   * {@link net.mumie.mathletfactory.math.number.MNumber}.
   */
  public MNumber getYProjAsNumberRef(){
    return getProjectiveBasisMatrixRef().getEntry(2, 1);
  }

  /**
   * Returns the projective coordinate <i>z</i> as reference to a
   * {@link net.mumie.mathletfactory.math.number.MNumber}.
   */
  public MNumber getZProjAsNumberRef(){
    return getProjectiveBasisMatrixRef().getEntry(3, 1);
  }

  /**
   * Returns the projective coordinate <i>w</i> as reference to a
   * {@link net.mumie.mathletfactory.math.number.MNumber}.
   */
  public MNumber getWProjAsNumberRef(){
    return getProjectiveBasisMatrixRef().getEntry(4, 1);
  }

  /**
   * Returns {@link net.mumie.mathletfactory.math.geom.GeometryIF#AFFINE3D_GEOMETRY}.
   */
  public int getGeomType() {
    return GeometryIF.AFFINE3D_GEOMETRY;
  }
}


