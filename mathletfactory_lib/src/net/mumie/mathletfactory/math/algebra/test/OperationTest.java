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

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;


/**  
 *  This class contains tests for the functionality of 
 *  {@link net.mumie.mathletfactory.algebra.op.Operation}.
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class OperationTest extends TestCase
{
  String expr1 = " 0.5 * ((sin x)^2 + (sin y)^2) ";
  String expr2 = " (x+y)^2 - x^2 - 2*x*y - y^2 ";
  String expr3 = " 2*x*y^2/(x^2+y^4) ";

  public OperationTest(String name) {
    super(name);
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.algebra.op.Operation#getDerivative}.
   */
  public void testDerive(){
    Assert.assertEquals(getXDerivative("(x-1)^-1"), "-(x-1)^-2");
    
    MMFunctionOverR2 function = new MMFunctionOverR2(MDouble.class, expr1);
    MMFunctionOverR2 gradientAbs = new MMFunctionOverR2(function.getDerivative("x"));
    //System.out.println("result is "+gradientAbs.getOperation().toDebugString());
    
    
    //Assert.assertEquals("sqrt((cos(x))^2*(sin(x))^2+(cos(y))^2*(sin(y))^2)", gradientAbs.getOperation().toString());
    
    // test the derivative for some functions
    
    Assert.assertEquals(getXDerivative("0.5*(sin(x)^2)"),"cos(x)*sin(x)");
    
    Assert.assertEquals(getXDerivative(expr2),"0");
    
    Assert.assertEquals(getXDerivative("cos(sin(x))"), "-sin(sin(x))*cos(x)");
    
    Assert.assertEquals(getXDerivative("exp(sin(x))"), "exp(sin(x))*cos(x)");
    
    Assert.assertEquals(getXDerivative("exp(cos(1/x))"),"x^-2*exp(cos(x^-1))*sin(x^-1)");
    
    Assert.assertEquals(getXDerivative("cos(sin(exp(x)))"),
                        "-sin(sin(exp(x)))*cos(exp(x))*exp(x)");
    
    Assert.assertEquals(getXDerivative("1/cos(2*y*x)^2"), "4y*sin(2x*y)/(cos(2x*y))^3");
    
    Assert.assertEquals(getXDerivative("(x^3+2x^2+3x)/(x^4-x^3+4*x+1)"),
        "-(x^4-x^3+4x+1)^-2*(x^3+2x^2+3x)*(4x^3-3x^2+4)+(x^4-x^3+4x+1)^-1*(3x^2+4x+3)");
    
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.algebra.op.Operation#normalize}.
   */
  public void testNormalize(){
    Operation expr = new Operation(MDouble.class,"5*(3*x^3)^2",true);
    Assert.assertEquals("45x^6",expr.toString());
    
    expr = new Operation(MDouble.class,"3*sqrt(4*x^4)",true);
    Assert.assertEquals("|6x^2|",expr.toString());
    expr = new Operation(MDouble.class,"5*asin(sin x)^2",true);
    //System.out.println(expr.toContentMathML());
    Assert.assertEquals("5x^2",expr.toString());

    expr = new Operation(MDouble.class,"3*sqrt(2*x^4)^2",true);
    Assert.assertEquals("|6x^4|",expr.toString());
    
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.algebra.op.Operation#expand}.
   */
  public void testExpand(){
    Operation expr = new Operation(MDouble.class, "(x+y)^5", true);
    expr.expand();
    //System.out.println(expr);
  }
  
  /**
   * Tests multiple operation and normalization by calculcating |grad f|
   */
  public void testCompound(){
  MMFunctionOverR2 function = new MMFunctionOverR2(MDouble.class, "(x*y^2)/(x^2+y^4)", -1, 1, -1, 1, 80);
  MMFunctionOverR2 derivative = new MMFunctionOverR2(function.getDerivative("x")); // should be | grad f|
  //Assert.assertEquals("sqrt((-2x^2*y^2/(x^2+y^4)^2+y^2/(x^2+y^4))^2+(-4x*y^5/(x^2+y^4)^2+2x*y/(x^2+y^4))^2)",
  //  derivative.toString());
          
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.algebra.op.Operation#evaluate}.
   */
  public void testEvaluate(){
    Operation op = new Operation(MDouble.class, "sin(pi*x/2)",true);
    for(int i=1;i<100;i++)
      if(i%2 == 0)
        Assert.assertEquals(0.0,op.evaluate(i),1e-13);
      else if(i%4 == 1)
        Assert.assertEquals(1.0,op.evaluate(i),1e-13);
      else if(i%4 == 3)
        Assert.assertEquals(-1.0,op.evaluate(i),1e-13);
  }
  
  public void testInvert(){
    MMFunctionDefByOp function = new MMFunctionDefByOp(MDouble.class, "-x^2");
    Assert.assertEquals(function.getInverse().getOperation().toString(), "sqrt(-x)");
    //System.out.println(function.getInverse());
  }
  
  public void testCompose(){
    MMFunctionDefByOp function = new MMFunctionDefByOp(MDouble.class, "exp(x)-1");
    Assert.assertEquals(function.getComposed(new MMFunctionDefByOp(MDouble.class, "sin(x)")).getOperation().toString(), "exp(sin(x))-1");
    //System.out.println(function.getComposed(new MMFunctionDefByOp(MDouble.class, "sin(x)"))); 
  }
  
  public void testMatrixCharPoly(){
    NumberMatrix id = new NumberMatrix(MRational.class, 3, 3);
    id.setToIdentity();
    System.out.println(id.getCharPoly());
  }
  
  /**
   * Utiltiy method: Returns the derivative d/dx of the given string as operation.
   */
  private String getXDerivative(String function){
    Operation op = new Operation(MDouble.class, function, true);
    //System.out.println(op.toDebugString());
    //System.out.println(op.toOpML());
    return op.getDerivative("x").toString();
  }
  
  /** Running the test case. */
  public static void main(String[] args){
    ConsoleHandler consoleHandler = new ConsoleHandler();
    Logger.getLogger("").addHandler(consoleHandler);
    Logger.getLogger("").setLevel(java.util.logging.Level.FINE);
    consoleHandler.setLevel(Level.FINE);
    OperationTest test = new OperationTest("test");
    test.testMatrixCharPoly();
    //test.testInvert();
    //test.testDerive();
//    Expression testExpr = new Expression(MDouble.class, "6y^3*x^2 + x^2 + 2*x^2*y^3", true);
    
    /*
    test.setUp();
    test.testSolve();
    test.testNormalize();
    test.testDerive();
     */
  }
  
  
}


