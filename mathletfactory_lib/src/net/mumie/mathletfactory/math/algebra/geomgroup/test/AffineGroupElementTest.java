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
 * AffineGroupElementTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.math.algebra.geomgroup.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;

public class AffineGroupElementTest extends TestCase {
  
  static AffineGroupElement g = new AffineGroupElement(MDouble.class,2);
  
   public AffineGroupElementTest(String name) {
    super(name);
  }
  
  public void testRotate3D() {
    
    AffineGroupElement rotate = new AffineGroupElement(MDouble.class, 3);
    NumberTuple axis = new NumberTuple(MDouble.class, 3);
    axis.setEntry(1, new MDouble(1));
    rotate.rotate(axis, new MDouble(Math.PI/2));
    NumberMatrix assumedResult = new NumberMatrix(MDouble.class, 3, 3);
    assumedResult.setEntryRef(1, 1, new MDouble(1));
    assumedResult.setEntryRef(2, 3, new MDouble(-1));
    assumedResult.setEntryRef(3, 2, new MDouble(1));
    NumberMatrix result = rotate.getDeformation().getMatrix();
    //System.out.println(result);
    //System.out.println(assumedResult);
    Assert.assertTrue(result.equals(assumedResult));
    
    rotate = new AffineGroupElement(MDouble.class, 3);
    axis.setEntry(1,new MDouble(0));
    axis.setEntry(3,new MDouble(1));
    rotate.rotate(axis, new MDouble(Math.PI/2));
    assumedResult = new NumberMatrix(MDouble.class, 3, 3);
    assumedResult.setEntryRef(3, 3, new MDouble(1));
    assumedResult.setEntryRef(1, 2, new MDouble(-1));
    assumedResult.setEntryRef(2, 1, new MDouble(1));
    result = rotate.getDeformation().getMatrix();
    //System.out.println(result);
    //Assert.assertEquals(assumedResult,result);
    Assert.assertTrue(result.equals(assumedResult));
    
    rotate = new AffineGroupElement(MDouble.class, 3);
    axis.setEntry(3,new MDouble(0));
    axis.setEntry(2,new MDouble(1));
    rotate.rotate(axis, new MDouble(Math.PI/2));
    assumedResult = new NumberMatrix(MDouble.class, 3, 3);
    assumedResult.setEntryRef(1, 3, new MDouble(1));
    assumedResult.setEntryRef(2, 2, new MDouble(1));
    assumedResult.setEntryRef(3, 1, new MDouble(-1));
    result = rotate.getDeformation().getMatrix();
    //Assert.assertEquals(assumedResult,result);
    Assert.assertTrue(result.equals(assumedResult));
  }
  
  /*
  public static void testRotate() {
    System.out.println("values after initializing:");
    System.out.println("linear part: "+g.getDeformation().getMatrixRef());
    System.out.println("translation: "+g.getTranslation());
    
    g.rotate(new MDouble(Math.PI/2));
    System.out.println("values after rotate with pi/2:");
    System.out.println("linear part: "+g.getDeformation().getMatrixRef());
    System.out.println("translation: "+g.getTranslation());
    
    g.translate(new NumberTuple(MDouble.class,new Object[]{new MDouble(2),new MDouble(3)}));
    System.out.println("values after additional translation:");
    System.out.println("linear part: "+g.getDeformation().getMatrixRef());
    System.out.println("translation: "+g.getTranslation());
    
    g.rotate(new MDouble(Math.PI));
    System.out.println("values after second rotate with pi:");
    System.out.println("linear part: "+g.getDeformation().getMatrixRef());
    System.out.println("translation: "+g.getTranslation());
  }
 
  public void testScale() {
    System.out.println("values after initializing:");
    System.out.println("linear part: "+g.getDeformation().getMatrixRef());
    System.out.println("translation: "+g.getTranslation());
    
    g.uniformScale(new MDouble(3));
    System.out.println("values after scale with 3:");
    System.out.println("linear part: "+g.getDeformation().getMatrixRef());
    System.out.println("translation: "+g.getTranslation());
  }
  
  public static void main(String[] args) {
    //testRotate();
    new AffineGroupElementTest("test").testScale();
  }
  */
}

