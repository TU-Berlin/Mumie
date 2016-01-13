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

package net.mumie.mathletfactory.math.number.numeric.numericdouble;
/**
 * This class can be used to calculate the vector product with java native doubles.
 *
 * @author Schimanowski
 * @mm.docstatus finished
 *
 */
public class VectorproductDouble {
  
  
  /**
   * Method getVectorproduct
   *
   * @param    v         a  double[]
   * @param    w         a  double[]
   *
   * @return   a double[]
   *
   */
  public static double[] getVectorproduct( double[] v, double[] w) {
    double resultVector[] =  new double[3];
    resultVector[0] = v[1]*w[2]-v[2]*w[1];
    resultVector[1] = v[2]*w[0]-v[0]*w[2];
    resultVector[2] = v[0]*w[1]-v[1]*w[0];
    
    return resultVector;
  }
  
}
