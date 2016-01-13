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

package net.mumie.mathletfactory.test;

import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.mumie.mathletfactory.math.algebra.test.AllAlgebraTest;
import net.mumie.mathletfactory.math.geom.test.AllGeomTest;
import net.mumie.mathletfactory.math.number.test.AllNumberTest;
import net.mumie.mathletfactory.math.set.test.AllSetTest;

/**
 * JUnit testsuite for all classes in {@link net.mumie.mathletfactory}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class AllMathletfactoryTest {
  /**
   * Testsuite running
   * {@link net.mumie.mathletfactory.algebra.test.AllAlgebraTest},
   * {@link net.mumie.mathletfactory.geom.test.AllGeomTest},
   * {@link net.mumie.mathletfactory.number.test.AllNumberTest}, and
   * {@link net.mumie.mathletfactory.set.test.AllSetTest}.
   *
   * @return   a <code>Test</code>
   *
   */
  public static Test suite() {
    
    TestSuite suite = new TestSuite();
    
    suite.addTest(AllNumberTest.suite());
    suite.addTest(AllSetTest.suite());
    suite.addTest(AllAlgebraTest.suite());
    suite.addTest(AllGeomTest.suite());
    return suite;
  }
  
  /**
   * Runs the test.
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String args[]) {
    Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
    junit.textui.TestRunner.run(suite());
  }
  
}
