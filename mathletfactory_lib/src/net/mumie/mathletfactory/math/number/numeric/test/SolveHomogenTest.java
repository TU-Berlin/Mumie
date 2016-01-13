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

package net.mumie.mathletfactory.math.number.numeric.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.numeric.SolveHomogen;


/**
 *  This class tests the mathematical correctness of
 *  {@link net.mumie.mathletfactory.number.numeric.SolveHomogen}.
 *  
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SolveHomogenTest extends TestCase
{
  public SolveHomogenTest(String name) {
    super(name);
  }
  
  Class doubleClass = new MDouble().getClass();
  
  public void setUp(){
    
  }
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.SolveHomogen#nullSpace}.
   */
  public void testNullSpace(){
    
    // a matrix, that has 3 linear independent row vectors:
    
    double[] coeffs1 = {
      1, -2,  0, -1,  3,
      1, -2,  2,  3, -1,
      2, -4,  5,  8, -4};
    
    double[] coeffs2 = {
      1,  2,  0,  1,  2,
      0,  0,  1,  2,  4,
      0,  0,  0,  0,  0};
    
        
    MDouble[] inputMatrix1 = new MDouble[coeffs1.length];
    MDouble[] inputMatrix2 = new MDouble[coeffs2.length];
    NumberMatrix identity = new NumberMatrix(doubleClass,5,5);
    identity.setToIdentity();
      
      
    // set coeffs
    for(int j=0; j<5; j++){
      for(int i=0; i<3; i++){
        inputMatrix1[i*5+j] = new MDouble(coeffs1[i*5+j]);
        inputMatrix2[i*5+j] = new MDouble(coeffs2[i*5+j]);
      }
    }
    //System.out.println("\n"+EchelonForm.getREFMatrix(new NumberMatrix(doubleClass, 5, identity)));
    NumberTuple[] vectors1 = SolveHomogen.nullSpace(new NumberMatrix(doubleClass, 5,
                                                      inputMatrix1));
    NumberTuple[] vectors2 = SolveHomogen.nullSpace(new NumberMatrix(doubleClass, 5,
                                                      inputMatrix2));
    NumberTuple[] vectors3 = SolveHomogen.nullSpace(new NumberMatrix(doubleClass, 5,5));
    NumberTuple[] vectors4 = SolveHomogen.nullSpace(identity);
    //for(int i=0;i<vectors3.length;i++)
    //  System.out.println(vectors3[i]);
    
    // solution for coeffs1:
    NumberTuple solution0 = new NumberTuple(doubleClass,5);
    solution0.setEntryRef(1,new MDouble(2));
    solution0.setEntryRef(2,new MDouble(1));
    
    
    NumberTuple solution1 = new NumberTuple(doubleClass,5);
    solution1.setEntryRef(1,new MDouble(1));
    solution1.setEntryRef(3,new MDouble(-2));
    solution1.setEntryRef(4,new MDouble(1));
    
    NumberTuple solution2 = new NumberTuple(doubleClass,5);
    solution2.setEntryRef(1,new MDouble(-3));
    solution2.setEntryRef(3,new MDouble(2));
    solution2.setEntryRef(5,new MDouble(1));
    
    
    // solution for coeffs2:
    NumberTuple solution3 = new NumberTuple(doubleClass,5);
    solution3.setEntryRef(1,new MDouble(-2));
    solution3.setEntryRef(2,new MDouble(1));
    
    
    NumberTuple solution4 = new NumberTuple(doubleClass,5);
    solution4.setEntryRef(1,new MDouble(-1));
    solution4.setEntryRef(3,new MDouble(-2));
    solution4.setEntryRef(4,new MDouble(1));
    
    NumberTuple solution5 = new NumberTuple(doubleClass,5);
    solution5.setEntryRef(1,new MDouble(-2));
    solution5.setEntryRef(3,new MDouble(-4));
    solution5.setEntryRef(5,new MDouble(1));
    
    
    Assert.assertTrue(vectors1[0].equals(solution0));
    Assert.assertTrue(vectors1[1].equals(solution1));
    Assert.assertTrue(vectors1[2].equals(solution2));
    
    Assert.assertTrue(vectors2[0].equals(solution3));
    Assert.assertTrue(vectors2[1].equals(solution4));
    Assert.assertTrue(vectors2[2].equals(solution5));
    
  
    Assert.assertTrue(new NumberMatrix(doubleClass, false, vectors3).equals(identity));
    Assert.assertTrue(vectors4.length == 1 && vectors4[0].isZero());
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.SolveHomogen#intersection}.
   */
  public void testIntersection(){
    NumberTuple vector1 = new NumberTuple(doubleClass, 3);
    NumberTuple vector2 = new NumberTuple(doubleClass, 3);
    NumberTuple vector3 = new NumberTuple(doubleClass, 3);
    NumberTuple vector4 = new NumberTuple(doubleClass, 3);
    vector1.setEntry(1, new MDouble(1));
    vector1.setEntry(2, new MDouble(0));
    
    vector2.setEntry(1, new MDouble(0));
    vector2.setEntry(2, new MDouble(1));
    
    vector3.setEntry(2, new MDouble(0));
    vector3.setEntry(3, new MDouble(1));
    
    vector4.setEntry(2, new MDouble(1));
    vector4.setEntry(3, new MDouble(0));
    
    
    NumberTuple resultVector = SolveHomogen.intersection(new NumberTuple[] {vector1,
                           vector2}, new NumberTuple[] { vector3, vector4})[0];
    NumberTuple expectedResult = new NumberTuple(MDouble.class, 3);
    expectedResult.setEntry(2, 1);
    Assert.assertEquals(expectedResult.toString(), resultVector.toString());                           
  }
}

