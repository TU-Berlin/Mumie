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

package net.mumie.mathletfactory.math.number.test;
import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;

/**
 * JUnit tests for methods of the class
 * {@link net.mumie.mathletfactory.number.MRational}.
 *
 * @author Mrose
 * @mm.docstatus finished
 * @see  <a href="http://junit.sourceforge.net">JUnit</a>
 */
public class MMRationalTest extends TestCase {
  
  long a=18;
  long b=-12;
  long c=0;
  long d=15129;
  MRational rational_a  = new MRational(2, -3);
  MRational rational_b = new MRational(-1, 4);
  MRational rational_c = new MRational(10, -15);
  MRational rational_d = new MRational(12, 16);
  MRational rational_zero = new MRational(0, 1);
  MRational aNegativeZero = new MRational(-0, (long)1e9);
  
  /**
   * Class constructor.
   */
  public MMRationalTest(String name) {
    super(name);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#getNumerator getNumerator}.
   *
   */
  public void testGetNumerator() {
    Assert.assertEquals(2, rational_a.getNumerator());
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#getDenominator getDenominator}.
   *
   */
  public void testGetDenominator() {
    Assert.assertEquals(-3, rational_a.getDenominator());
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#leastCommonMultiple leastCommonMultiple}.
   *
   */
  public void testLeastCommonMultiple() {
    long expected_ab = 36;
    long expected_bc = 0;
    Assert.assertEquals(expected_ab, MRational.leastCommonMultiple(a,b));
    Assert.assertEquals(expected_bc, MRational.leastCommonMultiple(b,c));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#greatestCommonDivisor greatestCommonDivisor}.
   *
   */
  public void testGreatestCommonDivisor() {
    long long1=6000000;
    long long2=1;
    long exp = 1;
    long expected_ab = 6;
    long expected_bc = 12;
    Assert.assertEquals(exp, MRational.greatestCommonDivisor(long1,long2));
    Assert.assertEquals(expected_ab, MRational.greatestCommonDivisor(a,b));
    Assert.assertEquals(expected_bc, MRational.greatestCommonDivisor(b,c));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#cancelDown cancelDown}.
   *
   */
  public void testCancelDown() {
    MRational expected_c= new MRational(-2, 3);
    Assert.assertTrue(expected_c.equalsExactly(rational_c.cancelDown()));
    MRational expected_aNegativeZero= new MRational(0, 1);
    Assert.assertTrue(expected_aNegativeZero.equalsExactly
                        (aNegativeZero.cancelDown()));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#addTo addTo}.
   *
   */
  public void testAddTo() {
    MRational expected_ab= new MRational(-11, 12);
    MNumber result_ab = rational_a.add(rational_b);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MRational expected_cZero= new MRational(-2, 3);
    MNumber result_cZero= rational_c.add(rational_zero);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#subFrom subFrom}.
   *
   */
  public void testSubFrom() {
    MRational expected_ab= new MRational(-5, 12);
    MNumber result_ab = rational_a.sub(rational_b);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MRational expected_cZero= new MRational(-2, 3);
    MNumber result_cZero = rational_c.sub(rational_zero);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#mult mult}.
   *
   */
  public void testMult() {
    MRational expected_ab= new MRational(1, 6);
    MNumber result_ab = rational_a.mult(rational_b);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MRational expected_cZero= new MRational(0, 1);
    MNumber result_cZero = rational_c.mult(rational_zero);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#divBy divBy}.
   *
   */
  public void testDivBy() {
    MRational expected_ab= new MRational(8, 3);
    MNumber result_ab = rational_a.div(rational_b);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MRational expected_cZero= new MRational(0, 1);
    MNumber result_cZero = rational_zero.div(rational_c);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#inverse inverse}.
   *
   */
  public void testInverse() {
    MRational expected_a= new MRational(-3, 2);
    MNumber result_a= rational_a.inverse();
    Assert.assertTrue(expected_a.equalsExactly(result_a));
    Assert.assertTrue(rational_a.equalsExactly(expected_a));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#inverted inverted}.
   *
   */
  public void testInverted() {
    MRational original = new MRational(2, -3);
    MRational expected_a= new MRational(-3, 2);
    MNumber result_a = rational_a.inverted();
    Assert.assertTrue(expected_a.equalsExactly(result_a));
    Assert.assertTrue(rational_a.equalsExactly(original));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#lessThan lessThan}.
   *
   */
  public void testLessThan() {
    boolean expected_ab = true;
    boolean expected_ac = false;
    boolean expected_azero = true;
    boolean result_ab= rational_a.lessThan(rational_b);
    boolean result_ac= rational_a.lessThan(rational_c);
    boolean result_azero= rational_a.lessThan(rational_zero);
    Assert.assertEquals(expected_ab, result_ab);
    Assert.assertEquals(expected_ac, result_ac);
    Assert.assertEquals(expected_azero, result_azero);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#lessOrEqualThan lessOrEqualThan}.
   *
   */
  public void testLessOrEqualThan() {
    boolean expected_ab = true;
    boolean expected_ac = true;
    boolean expected_azero = true;
    boolean result_ab = rational_a.lessOrEqualThan(rational_b);
    boolean result_ac = rational_a.lessOrEqualThan(rational_c);
    boolean result_azero= rational_a.lessThan(rational_zero);
    Assert.assertEquals(expected_ab, result_ab);
    Assert.assertEquals(expected_ac, result_ac);
    Assert.assertEquals(expected_azero, result_azero);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#greaterThan greaterThan}.
   *
   */
  public void testGreaterThan() {
    boolean expected_ab = false;
    boolean expected_ac = false;
    boolean expected_azero = false;
    boolean result_ab= rational_a.greaterThan(rational_b);
    boolean result_ac= rational_a.greaterThan(rational_c);
    boolean result_azero= rational_a.greaterThan(rational_zero);
    Assert.assertEquals(expected_ab, result_ab);
    Assert.assertEquals(expected_ac, result_ac);
    Assert.assertEquals(expected_azero, result_azero);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#greaterOrEqualThan greaterOrEqualThan}.
   *
   */
  public void testGreaterOrEqualThan() {
    boolean expected_ab = false;
    boolean expected_ac = true;
    boolean expected_azero = false;
    boolean result_ab= rational_a.greaterOrEqualThan(rational_b);
    boolean result_ac= rational_a.greaterOrEqualThan(rational_c);
    boolean result_azero= rational_a.greaterOrEqualThan(rational_zero);
    Assert.assertEquals(expected_ab, result_ab);
    Assert.assertEquals(expected_ac, result_ac);
    Assert.assertEquals(expected_azero, result_azero);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#isZero isZero}.
   *
   */
  public void testIsZero() {
    boolean expected_zero = true;
    boolean expected_NegZero = true;
    boolean result_zero= rational_zero.isZero();
    boolean result_NegZero= aNegativeZero.isZero();
    Assert.assertEquals(expected_zero, result_zero);
    Assert.assertEquals(expected_NegZero, result_NegZero);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#getDouble getDouble}.
   *
   */
  public void testGetDouble() {
    Assert.assertEquals(-0.25,  rational_b.getDouble(),   0.0);
    Assert.assertEquals(0.75,  rational_d.getDouble(),    0.0);
    Assert.assertEquals(0.0,   rational_zero.getDouble(), 0.0);
    Assert.assertEquals(0.0,   aNegativeZero.getDouble(), 0.0);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#setDouble setDouble}.
   *
   */
  public void testSetDouble() {
    MRational result_b = new MRational();
    MRational result_d = new MRational();
    MRational result_zero = new MRational();
    Assert.assertTrue(rational_b.equals(result_b.setDouble(-0.25)));
    Assert.assertTrue(rational_d.equals(result_d.setDouble(0.75)));
    Assert.assertTrue(rational_zero.equals(result_zero.setDouble(0.0)));
    MRational kleineZahl = new MRational();
    MRational fastEins = new MRational();
    kleineZahl.setDouble(-0.0000000000000001);
    fastEins.setDouble(-0.9999999999999999);
    Assert.assertTrue(MRational.add(kleineZahl, fastEins).equals(new MRational(-1,1)));
    MRational zahl1 = new MRational();
    MRational zahl2 = new MRational();
    
    zahl1.setDouble(1.23456789012);
    zahl2.setDouble(-2.34567890123);
    
    // the following is true for MRational.NUMBER_OF_DIGITS = 6
    Assert.assertEquals(MRational.multiply(zahl1, zahl2).getDouble(),-2.895888,0);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#power power}.
   *
   */
  public void testPower() {
    MInteger exponent_pos = new MInteger(3);
    MInteger exponent_neg = new MInteger(-2);
    
    MRational expected_apos = (MRational)rational_a.power(exponent_pos);
    MRational expected_aneg = (MRational)rational_a.power(exponent_neg);
    MRational result_apos = new MRational(-8, 27);
    MRational result_aneg = new MRational(9, 4);
    
    MRational expected_dpos = (MRational)rational_d.power(exponent_pos);
    MRational expected_dneg = (MRational)rational_d.power(exponent_neg);
    MRational result_dpos = new MRational(27, 64);
    MRational result_dneg = new MRational(16, 9);
    
    MRational expected_zeropos = (MRational)rational_zero.power(exponent_pos);
    MRational result_zeropos = new MRational(0, 1);
    
    Assert.assertTrue(expected_apos.equalsExactly(result_apos));
    Assert.assertTrue(expected_aneg.equalsExactly(result_aneg));
    
    Assert.assertTrue(expected_dpos.equalsExactly(result_dpos));
    Assert.assertTrue(expected_dneg.equalsExactly(result_dneg));
    
    Assert.assertTrue(expected_zeropos.equalsExactly(result_zeropos));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#power power} for
   * base zero and a negative exponent.
   *
   */
  public void testPowerArithmeticException() {
    MInteger exponent_neg = new MInteger(-2);
    try {
      rational_zero.power(exponent_neg);
      fail("Should raise an ArithmeticException");
    } catch (ArithmeticException success) {}
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.number.MRational#isSquareNumber isSquareNumber}.
   *
   */
  public void testIsSquareNumber() {
    Assert.assertEquals(false, MRational.isSquareNumber(a));
    Assert.assertEquals(false, MRational.isSquareNumber(b));
    Assert.assertEquals(true, MRational.isSquareNumber(c));
    Assert.assertEquals(true, MRational.isSquareNumber(d));
  }
  
  /**
   * Test of
   * {@link net.mumie.mathletfactory.number.MRational#squareRoot squareRoot}.
   *
   */
  public void testSquareRoot() {
    MNumber rational_a  = new MRational(25, 15129);
    MNumber expected_a = new MRational(5, 123);
    MNumber result_a = rational_a.squareRoot();
    Assert.assertEquals(expected_a, result_a);
    Assert.assertEquals(rational_a, result_a);

    MNumber rational_b  = new MRational(25, 4);
    MNumber expected_b = new MRational(5, 2);
    MNumber result_b = rational_b.squareRoot();
    Assert.assertEquals(expected_b, result_b);
    Assert.assertEquals(rational_b, result_b);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MRational#negate negate}.
   *
   */
  public void testNegate() {
    MRational expected_a = new MRational(2, 3);
    MNumber result_a = rational_a.negate();
    Assert.assertTrue(expected_a.equals(result_a));
    Assert.assertTrue(rational_a.equals(expected_a));
  }
}



