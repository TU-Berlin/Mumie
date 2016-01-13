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

package net.mumie.mathletfactory.math.number.numeric;


import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 *  This class determines the section of vector spaces by solving the
 *  corresponding linear equation system.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SolveHomogen {
  
  private static MDouble EPSILON = new MDouble(1e-14);
  
  /**
   *  This method calculates the intersection of two (number-)vectorspaces. The
   *  argument are vector arrays taken as bases for the vectorspaces and the
   *  resulting vectorspace is also returned by its basis.
   */
  public static NumberTuple[] intersection(NumberTuple[] span1, NumberTuple[] span2){
    Class entryClass = span1[0].getNumberClass();
    NumberMatrix linEq = new NumberMatrix(entryClass,
                                          span1.length + span2.length,
                                          span1[0].getRowCount());
    
    for(int i=0; i<span1.length; i++)
      linEq.setColumnVector(i+1, span1[i]);
    for(int i=0; i<span2.length;i++)
      linEq.setColumnVector(span1.length + i + 1, (NumberTuple)span2[i].negate());
    
    // determine the Null Space of the matrix of the vectors, i.e. the basis
    // for the non-trivial solutions
    //System.out.println(linEq);
    NumberTuple[] parameterVectors = nullSpace(linEq);
    //System.out.println("parameters are "+parameterVectors[0]+parameterVectors[1]);
    NumberTuple result[] = new NumberTuple[parameterVectors.length];
    // now return the result, which is calculated fastest for the
    // shorter set of basis vectors
    //if(span1.length < span2.length){
      for(int i=1; i<=parameterVectors.length; i++){
        result[i-1] = new NumberTuple(entryClass, span1[0].getRowCount());
        for(int j=1; j<=span1.length; j++){
        // result[i-1] += span[j-1]*parameterVector[i-1,j-1]:
          result[i-1].addTo(((NumberTuple)span1[j-1].deepCopy()).multiplyWithScalar(parameterVectors[i-1].getEntryRef(j)));
        }
      }
      return result;
      /* }
    else {
      for(int i=1; i<=parameterVectors.length; i++){
        result[i-1] = new NumberTuple(entryClass, span1[0].getRowCount());
        for(int j=1; j<=span1.length; j++){
          result[i-1].addTo(span1[j-1].multiplyWithScalar(parameterVectors[i-1].getEntryRef(j)));
        }
      }
       return result;
       } */
    
  }
  /**
   *  This method returns the basis of the null space of the linear
   *  transformation, the given Matrix stands for. If the null space is empty,
   *  it returns the zero vector with a number of rows that equals the number
   *  of columns of the given matrix.
   */
  public static NumberTuple[] nullSpace(NumberMatrix matrix){
    Class entryClass = matrix.getNumberClass();
    NumberMatrix refMatrix = EchelonForm.getREFMatrix(matrix);
    
    // pick a vector of ker(refMatrix) by multiplying a vector of
    // variables with refMatrix and use the free variables as weights of the
    // linear combination. E.g.
    //
    //                  /x_1\          /x_1\   / -2x_2 - x_4 -2x_5 \   / 0 -2  0 -1 -2 \  /x_1\
    // / 1 2 0 1 2 \    |x_2|          |x_2|   |         x_2       |   | 0  1  0  0  0 |  |x_2|
    // | 0 0 1 2 4 |  * |x_3| = 0  =>  |x_3| = | -2x_4 - 4x_5      | = | 0  0  0 -2 -4 |  |x_3|
    // \ 0 0 0 0 0 /    |x_4|          |x_4|   |         x_4       |   | 0  0  0  1  0 |  |x_4|
    //                  \x_5/          \x_5/   \         x_5       /   \ 0  0  0  0  1 /  \x_5/
    //
    //   refMatrix                                                       variableMatrix
    //
    // so what we have to do is the following:
    // copy the pivot rows into variableMatrix, so that the "1"s are on the
    // diagonal, then subtract it from the identity
    // then all non-zero columns of variableMatrix form a basis of ker(refMatrix)
    
    int n = refMatrix.getColumnCount();
    NumberMatrix variableMatrix = new NumberMatrix(entryClass, n, n);
    
    
    // the counter for the rows of refMatrix. It is increased, when a pivot
    // element is found.
    int rowcount = 1;
    
    // now do variableMatrix = Id - (refMatrix with zero rows inserted)
    for(int i=1; i<=n; i++){
      
      NumberTuple row = null;
      // get the current row with the pivot element
      // if there are no more rows, return a zero row
      if(rowcount <= refMatrix.getRowCount())
        row = refMatrix.getRowVector(rowcount);
      else
        row = new NumberTuple(entryClass, refMatrix.getColumnCount());
      int pivot = row.getIndexOfFirstNonZeroEntry();
      
      // if rowcount points to a row of refMatrix for which a pivot column
      // exists (in the above example rows 1 and 3), subtract the corresponding
      // row
      if(pivot == i){
        
        // subtract row from Identity; because the entry on the pivot position
        // is a one we can simply omit that position
        for(int j=pivot+1; j<=row.getRowCount(); j++)
            ((MNumber)variableMatrix.getEntryRef(i,j)).add((MNumber)row.getEntryRef(j)).negate();
        rowcount++;
      }
      else
        // set this row to the one of identity
          ((MNumber)variableMatrix.getEntryRef(i,i)).setDouble(1.0d);
    }
    
    int count=0;
    NumberTuple[] ker = new NumberTuple[n];
    // the non-zero columns are the base vectors for ker(matrix)
    for(int i=1; i<=n; i++){
      NumberTuple column = variableMatrix.getColumnVector(i);
      if(column.getMaxAbsEntryRef().absed().getDouble() >= EPSILON.getDouble())
        ker[count++] = column;
    }
    NumberTuple[] retVal = new NumberTuple[count];
    System.arraycopy(ker, 0, retVal, 0, count);
    // if there are no base vectors return the zero vector as base
    if(retVal.length == 0)
      retVal = new NumberTuple[] { new NumberTuple(entryClass, n)};
    return retVal;
  }
}
