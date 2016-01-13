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
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;

/**
 *  This class contains tests for the functionality of
 *  {@link net.mumie.mathletfactory.mmobject.set.MMSetDefByRel}.
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SetDefinedByRelationTest extends TestCase
{

  public SetDefinedByRelationTest(String name) {
    super(name);
  }


  /**
   * Tests {@link MMSetDefByRel#contains(NumberTuple)}.
   */
  public void testContains(){
    MMSetDefByRel set = new MMSetDefByRel(MDouble.class,"x/y+x^2>0");
    NumberTuple containedXY = new NumberTuple(MDouble.class, new MDouble[]{new MDouble(0.5), new MDouble(0.5)});
    Assert.assertTrue(set.contains(containedXY));
    containedXY.setEntry(2, -1e-300);
    Assert.assertTrue(!set.contains(containedXY));

  }

  /*
   public static void main(String[] args){
   new SetDefinedByRelationTest("test").testSpan();
   }
   */

}


