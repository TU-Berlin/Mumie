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

import java.math.BigInteger;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.number.MBigRational;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 *  This class is a test suite for the methods of
 *  {@link net.mumie.mathletfactory.number.MBigRational}
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMBigRationalTest extends TestCase {
  BigInteger a = new BigInteger("18");
  BigInteger b = new BigInteger("-12");
  BigInteger c = new BigInteger("0");
  MBigRational rational_a  = new MBigRational(2, -3);
  MBigRational rational_b = new MBigRational(-1, 4);
  MBigRational rational_c = new MBigRational(10, -15);
  MBigRational rational_d = new MBigRational(12, 16);
  MBigRational rational_zero = new MBigRational(0, 1);
  MBigRational aNegativeZero = new MBigRational(-0, (int)1e9);
  
  /**
   * Class constructor.
   */
  public MMBigRationalTest(String name) {
    super(name);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#getNumerator getNumerator}.
   */
  public void testGetNumerator() {
    Assert.assertEquals(2, rational_a.getNumerator().longValue());
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#getDenominator getDenominator}.
   */
  public void testGetDenominator() {
    Assert.assertEquals(-3, rational_a.getDenominator().longValue());
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#leastCommonMultiple leastCommonMultiple}.
   */
  public void testLeastCommonMultiple() {
    BigInteger expected_ab = new BigInteger("36");
    BigInteger expected_bc = new BigInteger("0");
    Assert.assertEquals(expected_ab, MBigRational.leastCommonMultiple(a,b));
    Assert.assertEquals(expected_bc, MBigRational.leastCommonMultiple(b,c));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#greatestCommonDivisor greatestCommonDivisor}.
   */  
  public void testGreatestCommonDivisor() {
    BigInteger int1 = new BigInteger("6000000");
    BigInteger int2 = new BigInteger("1");
    BigInteger exp = new BigInteger("1");
    BigInteger expected_ab = new BigInteger("6");
    BigInteger expected_bc = new BigInteger("12");
    Assert.assertEquals(exp, MBigRational.greatestCommonDivisor(int1,int2));
    Assert.assertEquals(expected_ab, MBigRational.greatestCommonDivisor(a,b));
    Assert.assertEquals(expected_bc, MBigRational.greatestCommonDivisor(b,c));
  }

  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#cancelDown cancelDown}.
   */  
  public void testCancelDown() {
    MBigRational expected_c= new MBigRational(-2, 3);
    Assert.assertTrue(expected_c.equalsExactly(rational_c.cancelDown()));
    MBigRational expected_aNegativeZero= new MBigRational(0, 1);
    Assert.assertTrue(expected_aNegativeZero.equalsExactly
                        (aNegativeZero.cancelDown()));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#addTo addTo}.
   */
  public void testAddTo() {
    MBigRational expected_ab= new MBigRational(-11, 12);
    MNumber result_ab = rational_a.add(rational_b);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MBigRational expected_cZero= new MBigRational(-2, 3);
    MNumber result_cZero= rational_c.add(rational_zero);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#subFrom subFrom}.
   */
  public void testSubFrom() {
    MBigRational expected_ab= new MBigRational(-5, 12);
    MNumber result_ab = rational_a.sub(rational_b);
    //System.out.println(result_ab);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MBigRational expected_cZero= new MBigRational(-2, 3);
    MNumber result_cZero = rational_c.sub(rational_zero);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#mult mult}.
   */
  public void testMultWith() {
    MBigRational expected_ab= new MBigRational(1, 6);
    MNumber result_ab = rational_a.mult(rational_b);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MBigRational expected_cZero= new MBigRational(0, 1);
    MNumber result_cZero = rational_c.mult(rational_zero);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  public void testDivBy() {
    MBigRational expected_ab= new MBigRational(8, 3);
    MNumber result_ab = rational_a.div(rational_b);
    Assert.assertTrue(expected_ab.equals(result_ab));
    
    MBigRational expected_cZero= new MBigRational(0, 1);
    MNumber result_cZero = rational_zero.div(rational_c);
    Assert.assertTrue(expected_cZero.equals(result_cZero));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#divBy divBy}.
   */  
  public void testDivByArithmeticException() {
    
    try {
      rational_a.div(rational_zero);
      fail("Should raise an ArithmeticException");
      
    } catch (ArithmeticException success) {}
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#inverse inverse}.
   */
  public void testInverse() {
    MBigRational expected_a= new MBigRational(-3, 2);
    MNumber result_a= rational_a.inverse();
    Assert.assertTrue(expected_a.equalsExactly(result_a));
    Assert.assertTrue(rational_a.equalsExactly(expected_a));
  }
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#inverted inverted}.
   */
  public void testInverted() {
    MBigRational original = new MBigRational(2, -3);
    MBigRational expected_a= new MBigRational(-3, 2);
    MNumber result_a = rational_a.inverted();
    Assert.assertTrue(expected_a.equalsExactly(result_a));
    Assert.assertTrue(rational_a.equalsExactly(original));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#lessThan lessThan}.
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
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#lessOrEqualThan lessOrEqualThan}.
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
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#greaterThan greaterThan}.
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
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#greaterOrEqualThan greaterOrEqualThan}.
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
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#isZero isZero}.
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
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#getDouble getDouble}.
   */
  public void testGetDouble() {
    Assert.assertEquals(-0.25,  rational_b.getDouble(),   0.0);
    Assert.assertEquals(0.75,  rational_d.getDouble(),    0.0);
    Assert.assertEquals(0.0,   rational_zero.getDouble(), 0.0);
    Assert.assertEquals(0.0,   aNegativeZero.getDouble(), 0.0);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#setDouble setDouble}.
   */
  public void testSetDouble() {
    MBigRational result_b = new MBigRational();
    MBigRational result_d = new MBigRational();
    MBigRational result_zero = new MBigRational();
    Assert.assertTrue(rational_b.equals(result_b.setDouble(-0.25)));
    Assert.assertTrue(rational_d.equals(result_d.setDouble(0.75)));
    Assert.assertTrue(rational_zero.equals(result_zero.setDouble(0.0)));
    MBigRational kleineZahl = new MBigRational();
    MBigRational fastEins = new MBigRational();
    kleineZahl.setDouble(-0.000000000000001);
    //System.out.println(kleineZahl);
    fastEins.setDouble(-0.999999999999999);
    //System.out.println(MBigRational.add(kleineZahl, fastEins));
    Assert.assertTrue(MBigRational.add(kleineZahl, fastEins).equals(new MBigRational(-1,1)));
    MBigRational zahl1 = new MBigRational();
    MBigRational zahl2 = new MBigRational();
    
    zahl1.setDouble(1.23456789012);
    zahl2.setDouble(-2.34567890123);
    
    // the following is true for MBigRational.NUMBER_OF_DIGITS = 6
    Assert.assertEquals(MBigRational.multiply(zahl1, zahl2).getDouble(),-2.8958998519905212,0);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#power power}.
   */
  public void testPower() {
    MInteger exponent_pos = new MInteger(3);
    MInteger exponent_neg = new MInteger(-2);
    
    MBigRational expected_apos = (MBigRational)rational_a.power(exponent_pos);
    MBigRational expected_aneg = (MBigRational)rational_a.power(exponent_neg);
    MBigRational result_apos = new MBigRational(-8, 27);
    MBigRational result_aneg = new MBigRational(9, 4);
    
    MBigRational expected_dpos = (MBigRational)rational_d.power(exponent_pos);
    MBigRational expected_dneg = (MBigRational)rational_d.power(exponent_neg);
    MBigRational result_dpos = new MBigRational(27, 64);
    MBigRational result_dneg = new MBigRational(16, 9);
    
    MBigRational expected_zeropos = (MBigRational)rational_zero.power(exponent_pos);
    MBigRational result_zeropos = new MBigRational(0, 1);
    
    Assert.assertTrue(expected_apos.equalsExactly(result_apos));
    Assert.assertTrue(expected_aneg.equalsExactly(result_aneg));
    
    Assert.assertTrue(expected_dpos.equalsExactly(result_dpos));
    Assert.assertTrue(expected_dneg.equalsExactly(result_dneg));
    
    Assert.assertTrue(expected_zeropos.equalsExactly(result_zeropos));
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#power power} for
   * base zero and a negative exponent.
   */
  public void testPowerArithmeticException() {
    
    MInteger exponent_neg = new MInteger(-2);
    
    try {
      rational_zero.power(exponent_neg);
      fail("Should raise an ArithmeticException");
      
    } catch (ArithmeticException success) {}
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#negate negate}.
   */
  public void testNegate() {
    MBigRational expected_a = new MBigRational(2, 3);
    MNumber result_a = rational_a.negate();
    Assert.assertTrue(expected_a.equals(result_a));
    Assert.assertTrue(rational_a.equals(expected_a));
    
  }
  
  /**
   * Test of handling big numbers.
   */
  public void testBigNumbers(){
    MBigRational kleineZahl = new MBigRational();
    MBigRational mittlereZahl = new MBigRational();
    MBigRational grosseZahl = new MBigRational();
    //  mittlereZahl.setDouble(5000);
    kleineZahl.setDouble(1.23456789012 * Math.pow(10,-24));
    grosseZahl.setDouble(Math.pow(10,24));
    Assert.assertEquals(MBigRational.multiply(kleineZahl, grosseZahl).getDouble(),1.23456789012,0);
  }
  
  /**
   * Test of {@link net.mumie.mathletfactory.number.MBigRational#isPosInfinity}
   * and {@link net.mumie.mathletfactory.number.MBigRational#isNegInfinity}.
   */
  public void testPosNegInfinity(){
    MBigRational posInf = new MBigRational(1,0);
    Assert.assertTrue(posInf.isPosInfinity());
    MBigRational negInf = new MBigRational(-1,0);
    Assert.assertTrue(negInf.isNegInfinity());
  }
}



