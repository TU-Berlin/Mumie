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

package net.mumie.mathletfactory.math.util;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;

/**
 * Offers a collection of static methods for the addition and scalar multiplication of double 
 * arrays as vectors. 
 *  
 * @author Paehler
 * @mm.docstatus finished
 */
public class AffineDouble {

 
  public static double getMaxAbsCoordinate(double[] vector){
    double max = 0;
    for(int i=0; i<vector.length; i++)
      max = Math.max(Math.abs(vector[i]), max);
    return max;
  }
  
  public static void scale(double[] vector, double factor){
    for(int i=0;i< vector.length;i++)
      vector[i] *= factor;
  }
  
  public static double standardNorm(double[] vector){
    double norm = 0;
    for(int i=0;i<vector.length;i++)
      norm += vector[i]*vector[i];
    return Math.sqrt(norm);
  }
  
  public static void normalize(double[] vector){
    scale(vector, 1./standardNorm(vector));
  }
  
  public static void setFrom(double[] vector1, double[] vector2){
    for(int i=0; i<vector1.length; i++)
      vector1[i] = vector2[i];
  }

  
  /**
   *  vector1 = vector1 - vector2
   */
  public static void sub(double[] vector1, double[] vector2){
    for(int i=0; i<vector1.length; i++)
      vector1[i] -= vector2[i];
  }
  
  /**
   *  vector1 = vector1 + vector2
   */
  public static void add(double[] vector1, double[] vector2){
    for(int i=0; i<vector1.length; i++)
      vector1[i] += vector2[i];
  }
  
  public static double dotProduct(double[] vector1, double[] vector2){
    double result = 0;
    for(int i=0; i<vector1.length; i++)
      result += vector1[i]*vector2[i];
    return result;
  }
  
  public static double angleBetween(double[] vector1, double[] vector2){
    return Math.acos(dotProduct(vector1, vector2)/(standardNorm(vector1) * standardNorm(vector2)));
  }

  /**
   * Parses a double array out of a string and returns it:
   * "a,b,c,d" -> new double[]{a,b,c,d} <br>
   * "x,y" -> new double[]{x,y}
   */
  public static double[] parseArray(String str){
    if(str.startsWith("(") || str.startsWith("[") || str.startsWith("{"))
      str = str.substring(1, str.length()-1);
    StringTokenizer tokenizer = new StringTokenizer(str,",;");
    double[] vector = new double[tokenizer.countTokens()];
    for(int i=0;i<vector.length;i++)
      vector[i] = Double.parseDouble(tokenizer.nextToken());
    return vector;
  }
  
  /**
   * Parses a MDouble NumberTuple out of a string and returns it:
   */
  public static NumberTuple parseTuple(String str){
    if(str.startsWith("(") || str.startsWith("[") || str.startsWith("{"))
      str = str.substring(1, str.length()-1);
    StringTokenizer tokenizer = new StringTokenizer(str,",;");
    NumberTuple vector = new NumberTuple(MDouble.class, tokenizer.countTokens());
    for(int i=1;i<=vector.getDimension();i++)
      vector.setEntry(i, Double.parseDouble(tokenizer.nextToken()));
    return vector;
  }


  /** 
   * Returns a String representation of a double array of the form '(x|y|z,...)', where x,y,z 
   * are the double values.
   */ 
  public static String printAsPoint(double[] array){
    return printAsPoint(array, null);
  }

  /** 
   * Returns a String representation of a double array of the form '(x|y|z,...)', where x,y,z 
   * are the double values formated by the given format.
   */ 
  public static String printAsPoint(double[] array, DecimalFormat format){
    String retVal = "(";
    for(int i=0;i<array.length;i++)
      if(format != null)    
        retVal += format.format(array[i])+"|";
      else
        retVal += array[i]+"|";
    return retVal.substring(0, retVal.length()-1)+")";
  }

}
