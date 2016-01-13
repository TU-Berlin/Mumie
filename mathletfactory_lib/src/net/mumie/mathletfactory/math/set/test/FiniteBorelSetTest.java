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

package net.mumie.mathletfactory.math.set.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This class contains tests for the functionality of
 *  {@link net.mumie.mathletfactory.math.set.FiniteBorelSet}.
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class FiniteBorelSetTest extends TestCase
{


  public FiniteBorelSetTest(String name) {
    super(name);
  }


  /**
   * Tests {@link net.mumie.mathletfactory.math.set.FiniteBorelSet#getComplement()}.
   */
  public void testComplement(){
    Class cl = MDouble.class;
    FiniteBorelSet testSet = new FiniteBorelSet(new Interval[]{ new Interval(cl, "(-3;2]"), new Interval(cl, "(3;4)")});
    Assert.assertEquals(testSet.getComplement().getInterval(1).getLowerBoundaryVal().getDouble(), 2,0);
    Assert.assertEquals(testSet.getComplement().getInterval(1).getLowerBoundaryType(), Interval.OPEN);
    Assert.assertEquals(testSet.getComplement().getInterval(2).getLowerBoundaryVal().getDouble(), 4,0);
    Assert.assertEquals(testSet.getComplement().getInterval(2).isUpperInfinite(), true);
    //System.out.println("complement of "+testSet+" is "+testSet.getComplement());
    testSet = new FiniteBorelSet(new Interval[]{ new Interval(cl, "(-infinity;2]"), new Interval(cl, "(3;infinity)")});
    Assert.assertEquals(testSet.getComplement().getInterval(1).getLowerBoundaryVal().getDouble(), 2,0);
    Assert.assertEquals(testSet.getComplement().getInterval(1).getLowerBoundaryType(), Interval.OPEN);
    Assert.assertEquals(testSet.getComplement().getInterval(2).isLowerInfinite(), false);
    Assert.assertEquals(testSet.getComplement().getInterval(2).getLowerBoundaryType(), Interval.CLOSED);
    Assert.assertEquals(testSet.getComplement().getInterval(2).isUpperInfinite(), true);
    Assert.assertEquals(testSet.getComplement().getInterval(2).getUpperBoundaryType(), Interval.CLOSED);
    //System.out.println("complement of "+testSet+" is "+testSet.getComplement());
  }


  public static void main(String[] args){
    new FiniteBorelSetTest("test").testComplement();
  }


}


