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
 * TestNumberMatrix.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.math.util.test;


import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;

public class TestNumberMatrix {
  
  public static void main (String[] args) {
    //testIsCollinear();
    //testDeepCopy();
    //testDeterminant();
    //testSetAsLinearMapping();
    testApply();
  }
  
  public static void testApply(){
    NumberTuple vector = new NumberTuple(MDouble.class, 3);
    NumberMatrix matrix = new NumberMatrix(MDouble.class, 3,4);
    for(int i=1; i<=3; i++){
      for(int j=1; j<=4; j++)
        matrix.setEntry(i,j,new MDouble(i+j));
      vector.setEntry(i, new MDouble(i));
    }
    //System.out.print(matrix+"applied to "+vector +" = " );
    NumberTuple result = new NumberTuple(MDouble.class, 3);
    double time = System.currentTimeMillis();
    for(int i=0; i<10000; i++)
      matrix.applyTo(vector, result);
    time = System.currentTimeMillis() - time;
    System.out.println(result+"in "+time/1000+" seconds");
    System.out.flush();
    
    time = System.currentTimeMillis();
    for(int i=0; i<10000; i++)
    result = matrix.applyTo(vector);
    time = System.currentTimeMillis() - time;
    System.out.println(result+"in "+time/1000+" seconds"+vector);
    
    
  }
  
  
  public static void testIsCollinear() {
    NumberMatrix M1 = new NumberMatrix(MDouble.class,2,
        new Object[]{new MDouble(1), new MDouble(1), new MDouble(2), new MDouble(2)});
    
    NumberMatrix M2 = new NumberMatrix(MDouble.class,2,
        new Object[]{new MDouble(2), new MDouble(2), new MDouble(4), new MDouble(4)});
    
    NumberMatrix M3 = new NumberMatrix(MDouble.class,2,
        new Object[]{new MDouble(2), new MDouble(2), new MDouble(3), new MDouble(4)});
    
    System.out.println(M1.isCollinear(M2));
    System.out.println(M1.isCollinear(M3));
  }
  
  public static void testShallowCopy() {
    NumberMatrix M1 = new NumberMatrix(MDouble.class,2,
        new Object[]{new MDouble(1), new MDouble(1), new MDouble(2), new MDouble(2)});
    NumberTuple v = new NumberTuple(MDouble.class,new Object[]{new MDouble(1), new MDouble(2)});
    
    
  }
  
  public static void testDeepCopy() {
    NumberMatrix M1 = new NumberMatrix(MDouble.class,2,
        new Object[]{new MDouble(1), new MDouble(1), new MDouble(2), new MDouble(2)});
    System.out.println(M1);
    NumberMatrix M2 = M1.deepCopy();
    System.out.println(M2);
  }
  
  public static void testDeterminant() {
//    NumberMatrix M1 = new NumberMatrix(MDouble.class,2,
//        new Object[]{new MDouble(1), new MDouble(2), new MDouble(2), new MDouble(2)});
    NumberMatrix M1 = new NumberMatrix(MDouble.class,3,
        new Object[]{new MDouble(1), new MDouble(2), new MDouble(3),
          new MDouble(4), new MDouble(5), new MDouble(6),
          new MDouble(7), new MDouble(8), new MDouble(9)});
    System.out.println(M1);
    System.out.println(M1.determinant());
    System.out.println(M1);
  }
  
  public static void testSetAsLinearMapping() {
    NumberMatrix M = new NumberMatrix(MDouble.class,2,
        new Object[]{new MDouble(),new MDouble(),new MDouble(),new MDouble()});
    System.out.println(M);
//    Vector v1 = new Vector(MDouble.class,new Object[]{new MDouble(1.0),new MDouble(0.0)});
//    Vector v2 = new Vector(MDouble.class,new Object[]{new MDouble(0.0),new MDouble(0.1)});
//    Vector w1 = new Vector(MDouble.class,new Object[]{new MDouble(1.0),new MDouble(0.0)});
//    Vector w2 = new Vector(MDouble.class,new Object[]{new MDouble(0.0),new MDouble(0.1)});
//    M.setAsLinearMapping(new Vector[] {v1,v2},new Vector[]{w1,w2});
    
    NumberTuple v1 = new NumberTuple(MDouble.class,new Object[]{new MDouble(1.0),new MDouble(0.0)});
    NumberTuple v2 = new NumberTuple(MDouble.class,new Object[]{new MDouble(0.0),new MDouble(1.0)});
    NumberTuple w1 = new NumberTuple(MDouble.class,new Object[]{new MDouble(0.0),new MDouble(1.0)});
    NumberTuple w2 = new NumberTuple(MDouble.class,new Object[]{new MDouble(1.0),new MDouble(0.0)});
//    Vector w1 = new Vector(MDouble.class,new Object[]{new MDouble(0.0),new MDouble(1.0),new MDouble(2.0)});
//    Vector w2 = new Vector(MDouble.class,new Object[]{new MDouble(1.0),new MDouble(0.0),new MDouble(2.0)});
    M.setAsLinearMapping(new NumberTuple[] {v1,v2},new NumberTuple[]{w1,w2});
    
    System.out.println(M);
  }
  
}

