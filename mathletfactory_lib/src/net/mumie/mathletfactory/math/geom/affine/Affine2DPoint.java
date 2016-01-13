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
 * This class represents a point living in a 2d affine space.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Affine2DPoint extends AffinePoint {
 
  /**
   * Creates an affine point in a two-dimensional space.
   * Coordinates are based on a number class <code>entryClass</code>.
   */
  public Affine2DPoint (Class entryClass){
    super (entryClass,2);
  }

  public Affine2DPoint (Affine2DPoint anAffinePoint) {
    this(anAffinePoint.getNumberClass());
    setProjectiveCoordinates(anAffinePoint.getProjectiveCoordinatesOfPoint());
  }

  public Affine2DPoint(Class entryClass, double x, double y) {
    this(entryClass);
    NumberTuple coords = new NumberTuple(entryClass,
                               new Object[]{NumberFactory.newInstance(entryClass, x),
                                            NumberFactory.newInstance(entryClass, y),
                                            NumberFactory.newInstance(entryClass, 1.0)});
    setProjectiveCoordinates(coords);
  }
  
  public Affine2DPoint setFrom(double[] xy) {
    return setXY(xy[0],xy[1]);
  }
  
  public Affine2DPoint setY(MNumber yVal) {
    getProjectiveBasisMatrixRef().setEntry(2, 1, yVal);
    return this;
  }
  
  public Affine2DPoint setX(MNumber xVal) {
    getProjectiveBasisMatrixRef().setEntry(1, 1, xVal);
    return this;
  }
  
  public Affine2DPoint setY(double y) {
    getProjectiveBasisMatrixRef().setEntry(2, 1, y);
    return this;
  }
  
  public Affine2DPoint setX(double x) {
    getProjectiveBasisMatrixRef().setEntry(1, 1, x);
    return this;
  }

  public Affine2DPoint setXY(double[] xy) {
    getProjectiveBasisMatrixRef().setEntry(1, 1, xy[0]);
    getProjectiveBasisMatrixRef().setEntry(2, 1, xy[1]);
    return this;
  }
  
  public Affine2DPoint setXY(double x, double y){
    //System.out.println("setting to "+x+","+y);
    setX(x);
    setY(y);
   return this;
  }
  
  public double getXAsDouble(){
    return   getProjectiveBasisMatrixRef().getEntry(1, 1).getDouble()/
    getProjectiveBasisMatrixRef().getEntry(3, 1).getDouble();
  }
  
  public double getYAsDouble(){
    return   getProjectiveBasisMatrixRef().getEntry(2, 1).getDouble()/
    getProjectiveBasisMatrixRef().getEntry(3, 1).getDouble();
  }
  
  public double[] getXYAsDouble(){
    return new double[] {getXAsDouble(), getYAsDouble()};
  }
  
  public MNumber getXProjAsNumberRef(){
    return  getProjectiveBasisMatrixRef().getEntry(1, 1);
  }
  
  public MNumber getYProjAsNumberRef(){
    return  getProjectiveBasisMatrixRef().getEntry(2, 1);
  }
  
  public MNumber getZProjAsNumberRef(){
    return  getProjectiveBasisMatrixRef().getEntry(1, 1);
  }
  /**
   * Method getGeomType
   *
   * returns
   * @see GeometryIF#AFFINE2D_GEOMETRY
   *
   * @return   an int
   *
   * @version  8/12/2002
   */
  public int getGeomType() {
    return GeometryIF.AFFINE2D_GEOMETRY;
  }

  
}

