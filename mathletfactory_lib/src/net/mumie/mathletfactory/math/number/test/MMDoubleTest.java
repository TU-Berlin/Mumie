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
 * DoubleTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.math.number.test;





import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MRational;


public class MMDoubleTest extends TestCase {
  
  public static void main(String[] args){
    MInteger i1 = new MInteger(2);
    MInteger i2 = new MInteger(0);
    System.out.println(i1.copy().div(i2).getDouble());
    MDouble pInf = new MDouble(Double.POSITIVE_INFINITY);
    MDouble nInf = new MDouble(Double.NEGATIVE_INFINITY);
    System.out.println(pInf);
    System.out.println(nInf);
  }
  
  public MMDoubleTest(String name) {
    super(name);
  }
  
  
  public void testMantisseExponent(){
    
    MDouble bigNumber = new MDouble(0.1415*Math.pow(10,40));
    MDouble negativeBigNumber = new MDouble(-0.1415*Math.pow(10,40));
    MDouble smallNumber = new MDouble(0.1415*Math.pow(10,-40));
    MDouble negativeSmallNumber = new MDouble(-0.1415*Math.pow(10,-40));
    
    MDouble d = new MDouble(1.0);
    MRational r = new MRational(1.0);
    Object o = new Object();
    
    
    // zero tolerance for the basis! if we use one more in digit
    // in MDouble.formatter this test fails
    Assert.assertEquals(bigNumber.mantisse(), 1.415, 0);
    Assert.assertEquals(bigNumber.exponent(), 39);
    Assert.assertEquals(negativeBigNumber.mantisse(), -1.415, 0);
    Assert.assertEquals(negativeBigNumber.exponent(), 39);
    Assert.assertEquals(smallNumber.mantisse(), 1.415, 0);
    Assert.assertEquals(smallNumber.exponent(), -41);
    Assert.assertEquals(negativeSmallNumber.mantisse(), -1.415, 0);
    Assert.assertEquals(negativeSmallNumber.exponent(), -41);
    //Assert.assertEquals(d.equals(r),false);
    Assert.assertEquals(d.equals(o),false);
  }
  
}

