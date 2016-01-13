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
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.util.xml.XMLUtils;


/**
 * This class tests for a set of expressions the MathML in- and -output for
 * Operations.
 * 
 * @author gronau
 */
public class OpXMLTest extends TestCase {

	Class[] numberClasses = { MDouble.class, MInteger.class, MRational.class, MComplex.class, MComplexRational.class };

	// String[] expressions = {"((2x)(5+6)(x)(y)(z)*(2)+(5+6)/(8*9)+(8+9))!"};
	// String[] expressions = {"abxyz(c(d(3)))(5)((8))sed|a|"};
	// String[] expressions = {"+++1+(+++8+9) +
	// sin+++9+(1+1)((8+8))+c(v+v)c+5(7+7)5
	// + (sin(x)sin(y)) + vsin(x)+sinv+v9+9v+vvvv + sin9 + 9sinx"};
	// String[] expressions =
	// {"sin(x)cos(x)sin(x)+tan(x)cot(x)theta(x)+asin(x)"};
	String[] expressions = { "sin(x)", "cos(x)", "tan(x)", "cot(x)", "sinh(x)", "cosh(x)", "tanh(x)", "coth(x)", "asin(x)", "acos(x)", "atan(x)", "acot(x)", "arcsin(x)", "arccos(x)", "arctan(x)",
			"arccot(x)", "asinh(x)", "acosh(x)", "atanh(x)", "acoth(x)", "arsinh(x)", "arcosh(x)", "artanh(x)", "arcoth(x)", "exp(x)", "ln(x)", "sqrt(x)", "abs(x)", "floor(x)", "fac(x)", "re(x)",
			"im(x)", "conj(x)", "cbrt(x)", "sign(x)", "theta(x)" };

	// String[] expressions = { "2*sin(x)cos(x)theta(x)",
	// "2*cos(x)*sin(x)+sin(9)",
	// "1/2", "10/2", "a/2", "r/2", "1/a", "1/r", "m/r", "r/m", "k/m", "k*m",
	// "(r-1)/2", "(a-1)/2", "2*PI", "sin(2*k*PI)",
	// "sin(x)/cos(x)", "e^(i*PI)", "exp(i*x)", "x^(N+1)", "x^y", "1/(x*y)",
	// "i^2",
	// "5 (x+1)^2", "5(x*y)^2", "sqrt(x+1)", "(x+1)^(1/3)", "x^(2/3)", "0.5 *
	// ((sin
	// x)^2 + (sin y)^2)",
	// "(x+y)^2 - x^2 - 2*x*y - y^2", "2*x*y^2/(x^2+y^4)", "-2",
	// "+++2cos(x)sin(x)theta(++++9+8)", "asin(+++x)" };

	public OpXMLTest( String name ) {
		super( name );
	}

	public void testDivision() {
		for ( int c = 0; c < numberClasses.length; c++ ) {
			System.out.println( "\nTest with number class " + numberClasses[c] );
			for ( int s = 0; s < expressions.length; s++ ) {
				Operation op1 = new Operation( numberClasses[c], expressions[s], false );
				Operation op2 = new Operation( numberClasses[c], "0", false );
				op2.setMathMLNode( op1.getMathMLNode() );
				System.out.print( ( s + 1 ) + ". " + expressions[s] + "" );
				assertEqualsOps( op1, op2 );
				System.out.println( "\t" + op1.toString() + "\t" + XMLUtils.nodeToString( op1.getMathMLNode(), true ) );
				System.out.println( "\t" + op2.toString() + "\t" + XMLUtils.nodeToString( op2.getMathMLNode(), true ) );
			}
		}
	}

	private void assertEqualsOps( Operation parsed, Operation read ) {
		try {
			Assert.assertEquals( parsed, read );
			System.out.println( "\tOK" );
		} catch ( AssertionFailedError afe ) {
			System.err.println( "\tFailure" );
		}
	}

	public static void main( String args[] ) {
		TestSuite suite = new TestSuite();
		suite.addTestSuite( OpXMLTest.class );
		junit.textui.TestRunner.run( suite );
	}
}
