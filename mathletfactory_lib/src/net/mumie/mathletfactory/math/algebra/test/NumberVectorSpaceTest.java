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

package net.mumie.mathletfactory.math.algebra.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.numeric.SolveHomogen;
import net.mumie.mathletfactory.math.number.numeric.Span;

/**
 *  This class contains tests for the functionality of 
 *  {@link net.mumie.mathletfactory.algebra.linalg.NumberVectorSpace}.
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class NumberVectorSpaceTest extends TestCase
{
  
  NumberTuple[] vecs1 = new NumberTuple[3];
  NumberTuple[] vecs2 = new NumberTuple[3];
  
  
  public NumberVectorSpaceTest(String name) {
    super(name);
  }
 
  public void setUp(){
    // create two arrays of 3 NumberVectors each
    vecs1[0] = new NumberTuple(MDouble.class,
                               new Object[] {new MDouble(1), new MDouble(0), new MDouble(0)});
    vecs1[1] = new NumberTuple(MDouble.class,
                               new Object[] {new MDouble(1), new MDouble(0), new MDouble(1)});
    vecs1[2] = new NumberTuple(MDouble.class,
                               new Object[] {new MDouble(0), new MDouble(0), new MDouble(0)});
    
    vecs2[0] = new NumberTuple(MDouble.class,
                               new Object[] {new MDouble(0), new MDouble(1), new MDouble(1)});
    vecs2[1] = new NumberTuple(MDouble.class,
                               new Object[] {new MDouble(0), new MDouble(0), new MDouble(1)});
    vecs2[2] = new NumberTuple(MDouble.class,
                               new Object[] {new MDouble(1), new MDouble(1), new MDouble(0)});
    
    
    // create NumberVectorSpace with internal dimension 3 and external dimension 4
    NumberVectorSpace space = new NumberVectorSpace(MDouble.class, 3, 4);
    
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.algebra.linalg.NumberVectorSpace#joined}.
   */
  public void testJoin(){
    
    // create subspace
    NumberTuple[] spanBasis1 = Span.getBasisForSpanOf(vecs1);
    NumberTuple[] spanBasis2 = Span.getBasisForSpanOf(vecs2);
    NumberVectorSpace subspace1 = new NumberVectorSpace(spanBasis1);
    NumberTuple[] subbasis1 = subspace1.getBasisAsTuples();
    //System.out.println("subbasis1 is"+subbasis1[0]+subbasis1[1]);
    
    NumberVectorSpace subspace2 = new NumberVectorSpace(spanBasis2);
    NumberTuple[] subbasis2 = subspace2.getBasisAsTuples();
    //System.out.println("subbasis2 is"+subbasis2[0]+subbasis2[1]);
    
    // test the join in two different ways:
    NumberVectorSpace joinSpace1 = (NumberVectorSpace)subspace1.joined(subspace2);
    NumberVectorSpace[] spaces = new NumberVectorSpace[]{subspace1,subspace2};
    NumberVectorSpace joinSpace2 = new NumberVectorSpace(spaces);
    //System.out.println("join of subspace1 and subspace2: "
    //                     +joinSpace.getDefaultBasis()[0].getDefaultCoordinates()
    //                     +joinSpace.getDefaultBasis()[1].getDefaultCoordinates()
    //                     +joinSpace.getDefaultBasis()[2].getDefaultCoordinates());
    Assert.assertEquals(joinSpace1.getDimension(), 3);
    Assert.assertEquals(joinSpace2.getDimension(), 3);
    //System.out.println(joinSpace.getDefaultBasis()[0].getDefaultCoordinates());
    //System.out.println(vecs1[0]);
    Assert.assertTrue(joinSpace1.getBasis()[0].getDefaultCoordinates().equals(vecs1[0]));
    Assert.assertTrue(joinSpace2.getBasis()[0].getDefaultCoordinates().equals(vecs1[0]));
    Assert.assertTrue(joinSpace1.equals(joinSpace2));
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.algebra.linalg.NumberVectorSpace#intersection}.
   */
  public void testIntersect(){
    Class entryClass = new MDouble().getClass();
    // create subspace
    NumberTuple[] spanBasis1 = Span.getBasisForSpanOf(vecs1);
    NumberTuple[] spanBasis2 = Span.getBasisForSpanOf(vecs2);
    NumberVectorSpace subspace1 = new NumberVectorSpace(spanBasis1);
    NumberTuple[] subbasis1 = subspace1.getBasisAsTuples();
    //System.out.println("spanBasis2 is"+spanBasis2[0]+spanBasis2[1]);
    
    NumberVectorSpace subspace2 = new NumberVectorSpace(spanBasis2);
    NumberTuple[] subbasis2 = SolveHomogen.intersection(vecs1, vecs2);
    //subspace2.getDefaultBasisAsTupelArrayRef();
    //System.out.println("subbasis2 is"+subbasis2[0]+subbasis2[1]);
    
    NumberVectorSpace intersectionSpace = (NumberVectorSpace)subspace1.intersected(subspace2);
    NumberTuple[] iBase = NumberVector.vectors2Tuples(intersectionSpace.getBasis());
    NumberMatrix basisMatrix1 = new NumberMatrix(entryClass, false,
                                                 NumberVector.vectors2Tuples(subspace1.getBasis()));
    NumberMatrix basisMatrix2 = new NumberMatrix(entryClass, false,
                                                 NumberVector.vectors2Tuples(subspace2.getBasis()));
    NumberMatrix basisMatrix3 = new NumberMatrix(entryClass, false,
                                                 NumberVector.vectors2Tuples(intersectionSpace.getBasis()));
    
    
    //System.out.println("intersection of :\n"+basisMatrix1+ "\n and \n"+basisMatrix2+
    //                     "\n is \n"+basisMatrix3);
    NumberMatrix solution = new NumberMatrix(entryClass, 2, 3);
    solution.setEntry( 3, 1, new MDouble(1));
    solution.setEntry( 1, 2, new MDouble(1));
    solution.setEntry( 3, 2, new MDouble(-1));
    
    Assert.assertTrue(Span.isContainedInSpanOf(solution.getColumnVectors()[0],
                                               basisMatrix3.getColumnVectors()));
    Assert.assertTrue(Span.isContainedInSpanOf(solution.getColumnVectors()[1],
                                               basisMatrix3.getColumnVectors()));
    
  }
  /*
   public static void main(String[] args){
   new NumberVectorSpaceTest("test").testSpan();
   }
   */
  
}


