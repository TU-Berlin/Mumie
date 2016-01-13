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

package net.mumie.mathletfactory.math.analysis.function.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;


/**
 * JUnit testsuite for {@link net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2}.
 *
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 *  @author Paehler
 *  @mm.docstatus finished
 *
 */
public class MultivariateFunctionTest extends TestCase
{

  MMFunctionOverR2 sin_square_x_plus_cos_square_x;
  MMFunctionOverR2 polynomial1;
  MMFunctionOverR2 polynomial2;

  public MultivariateFunctionTest(String name) {
    super(name);
  }

  public void setUp(){
    sin_square_x_plus_cos_square_x = new MMFunctionOverR2(MDouble.class, "(sin x)^2 +(cos x)^2");
    polynomial1 = new MMFunctionOverR2(MDouble.class, "x^2+y^2");
    polynomial2 = new MMFunctionOverR2(MDouble.class, "x^3 + 3*x^2*y + 3*x*y^2 + y^3");
  }

  /**
   * Tests the evaluation of a
   * {@link net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2}.
   */
  public void testFunctionValues(){
    //System.out.println("sin(Pi/3)^2 +cos(Pi/3)^2 is "+sin_square_x_plus_cos_square_x.getOpRoot());
    Assert.assertTrue(polynomial2.evaluate(new MDouble(2), new MDouble(3)).equals(new MDouble(125)));
    //System.out.println("sin(Pi/3)^2 +cos(Pi/3)^2 evals to "+sin_square_x_plus_cos_square_x.evaluateFor(new MDouble(Math.PI/3), new MDouble(Math.PI/3)));
    Assert.assertTrue(sin_square_x_plus_cos_square_x.evaluate(new MDouble(Math.PI/3),new MDouble()).equals(new MDouble(1)));
  }

  /**
   * Tests
   * {@link net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2#derive}.
   */
  public void testDerivatives(){
    sin_square_x_plus_cos_square_x.derive("x");
    //System.out.println("The derivative of (sin x)^2 +(cos x)^2 is "+sin_square_x_plus_cos_square_x.getOperation());
    //System.out.println("it evals for x=3 to "+sin_square_x_plus_cos_square_x.evaluateFor(new MDouble(3), new MDouble(0)));
    Assert.assertTrue(sin_square_x_plus_cos_square_x.evaluate(new MDouble(3.45678),new MDouble()).equals(new MDouble(0)));
  }

}


