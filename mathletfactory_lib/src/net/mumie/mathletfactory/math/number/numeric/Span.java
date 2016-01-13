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
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.math.number.MDouble;


/**
 * This class determines the span of an arbitrary number of vectors given as
 * {@link NumberTuple}s. This can be done
 * by returning a new set of basis vectors (the pivot columns of corresponding
 * the echelon form) or simply the indices (with respect to argument array),
 * which of the given vectors would serve as a basis.
 *
 *  @author Paehler
 * 	@mm.docstatus finished
 */
public class Span
{
  private static MDouble EPSILON = new MDouble(1e-14);

  /**
   * This method returns a basis for the span of the given NumberTupels
   * (vectors). This is done by constructing the matrix, of which the
   * row-equivalent echelon form is calculated. The columns of the echelon
   * form matrix containing the pivot elements are assigned as the new basis
   * vectors, which are returned as NumberTupels.
   *
   * @param   vectors              a  NumberTuple[], the vectors of which the
   * span is constructed.
   *
   * @return   a NumberTuple[], the basis of the span.
   *
   */
  public static NumberTuple[] span(NumberTuple[] vectors){

    NumberTuple[] basisTupels = new NumberTuple[vectors.length];
    for(int i=0; i < vectors.length; i++)
      basisTupels[i] = (NumberTuple)vectors[i].deepCopy();

    int count = 0;
    NumberMatrix eFMatrix = EchelonForm.getEFMatrix(
      new NumberMatrix(vectors[0].getNumberClass(), false, vectors));

    // traverse the echelon form matrix rowwise and search for pivot columns
    for(int i=1; i<=eFMatrix.getRowCount(); i++){
      for(int j=1; j<=eFMatrix.getColumnCount(); j++)

        // locate the column with the pivot-element
        if(!eFMatrix.getEntry(i,j).isZero()){

          // get this column as new basis vector
          basisTupels[count++] = (NumberTuple) eFMatrix.getColumnVector(j).deepCopy();

          // we are done here, head for the next row
          break;
        }
    }

    // now the array needs to be resized to the actual number of basisTupels
    NumberTuple[] retVal = new NumberTuple[count];
    System.arraycopy(basisTupels, 0, retVal, 0, count);
    return retVal;

  }

  /**
   *  Determine the span of an array of affine points by regarding them as
   *  projective vectors.
   *  @deprecated may be removed in future versions!
   */
  public static AffinePoint[] span(AffinePoint[] points){
    NumberTuple[] projectiveCoords = new NumberTuple[points.length];
    for(int i=0; i < points.length; i++)
      projectiveCoords[i] = (NumberTuple)points[i].getProjectiveCoordinatesOfPoint();

    int count = 0;
    NumberMatrix eFMatrix = EchelonForm.getEFMatrix(
      new NumberMatrix(points[0].getNumberClass(), false, projectiveCoords));

    // traverse the echelon form matrix rowwise and search for pivot columns
    for(int i=1; i<=eFMatrix.getRowCount(); i++){
      for(int j=1; j<=eFMatrix.getColumnCount(); j++)

        // locate the column with the pivot-element
        if(Math.abs(eFMatrix.getEntry(i,j).getDouble()) >= EPSILON.getDouble()){

          // get this column as new basis vector
          points[count++] = new AffinePoint((NumberTuple) eFMatrix.getColumnVector(j));

          // we are done here, head for the next row
          break;
        }
    }

    // now the array needs to be resized to the actual number of basisTupels
    AffinePoint[] basisPoints = new AffinePoint[count];
    System.arraycopy(points, 0, basisPoints, 0, count);
    return basisPoints;
  }
  /**
   * This method returns the indices of the argument vectors, that would serve
   * as a basis for the span (e.g. if the argument was arg={{1,0, 0}, {2, 0, 0},
   * {0, 0, 1}}, the int[] {0,2} would be returned, because the first and the
   * third vector serve as a basis for the span(arg).
   *
   */
  public static int[] spanIndices(NumberTuple[] vectors){

    int[] indices = new int[vectors.length];
    int count = 0;

    NumberMatrix eFMatrix = EchelonForm.getEFMatrix(
      new NumberMatrix(vectors[0].getNumberClass(), false, vectors));

    for(int i=1; i<=eFMatrix.getRowCount(); i++)
      for(int j=1; j<=eFMatrix.getColumnCount(); j++)
        if(Math.abs(eFMatrix.getEntry(i,j).getDouble()) >= EPSILON.getDouble()){
          indices[count++] = j-1;
          break;
        }
    int[] retVal = new int[count];
    System.arraycopy(indices, 0, retVal, 0, count);
    return retVal;
  }

  /**
   * This method returns for an arbitrary array of NumberTupels a
   * (deep-copied) subset that serves as a basis for the Span of aListOfVectors
   */
  public static NumberTuple[] getBasisForSpanOf(NumberTuple[] aListOfVectors) {
    int[] indices = Span.spanIndices(aListOfVectors);
    NumberTuple[] basis = new NumberTuple[indices.length];
    for(int i=0; i<indices.length; i++)
      basis[i] = (NumberTuple) aListOfVectors[indices[i]].deepCopy();
    return basis;
  }

  /**
   *  This method tests, if a vector is contained in the Span of other vectors.
   */
  public static boolean isContainedInSpanOf(NumberTuple testVector, NumberTuple[] testSpan){
    NumberTuple[] allSpan = new NumberTuple[testSpan.length+1];
    System.arraycopy(testSpan, 0, allSpan, 0, testSpan.length);
    allSpan[testSpan.length] = testVector;
    // if the last index does not serve as a basis for the span, it is
    // linear dependent on the other vectors
    int[] indices = spanIndices(allSpan);
    return indices[indices.length-1] != testSpan.length;
  }
}

