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

/**
 * AffinePointTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.math.geom.affine.test;



import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.math.number.MDouble;


public class AffinePointTest {
  
  public static void main(String[] args) {
    testCoordinates();
  }
  
  
  public static void testInstantiation() {
    NumberTuple projCoords = new NumberTuple(MDouble.class,new Object[]{new MDouble(1.),
          new MDouble(2.),new MDouble(3.), new MDouble(7.)});
    System.out.println(projCoords);
    AffinePoint ap = new AffinePoint(projCoords);
    
    System.out.println("affine coordinates:" + ap.getAffineCoordinatesOfPoint());
  }

  
  public static void testCoordinates() {
    Affine2DPoint p = new Affine2DPoint(MDouble.class,0.2,0.2);
    Affine2DPoint q = new Affine2DPoint(MDouble.class,0.5,0.5);

    //System.out.println(p.equals(q));
 
    NumberTuple tp = p.getProjectiveCoordinatesOfPoint();
    NumberTuple tq = q.getProjectiveCoordinatesOfPoint();
    NumberTuple tuples = new NumberTuple(MDouble.class,new Object[]{new MDouble(0.2),new MDouble(0.2),new MDouble(1.0)});
    
    System.out.println(tp.equals(tuples));
    
    System.out.println(p.getProjectiveCoordinatesOfPoint());
    System.out.println(q.getProjectiveCoordinatesOfPoint());
    
    System.out.println(tp.equals(tq));
    
  }
  
}

