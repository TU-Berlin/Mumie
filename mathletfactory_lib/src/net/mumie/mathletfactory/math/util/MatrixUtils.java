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

import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;

/** 
 * This class helps manipulating number matrices, including echelon form and submatrix views.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MatrixUtils {

	/**
	 * Sets a submatrix of <code>bigMatrix</code> to <code>outputSubMatrix</code>, beginning with the given indices.
	 */
	public static void getSubMatrix(NumberMatrix bigMatrix, int startingRow,
			int startingCol, NumberMatrix outputSubMatrix) {
		for (int r = 1; r <= outputSubMatrix.getRowCount(); r++) {
			for (int c = 1; c <= outputSubMatrix.getColumnCount(); c++) {
				outputSubMatrix.setEntryRef(r, c, bigMatrix.getEntryRef(r + startingRow
						- 1, c + startingCol - 1));
			}
		}
	}

	/**
	 * Returns a submatrix of <code>bigMatrix</code>, beginning with the given indices.
	 */
	public static NumberMatrix getSubMatrix(NumberMatrix bigMatrix,
			int startingRow, int startingCol) {
		NumberMatrix smallMatrix = new NumberMatrix(bigMatrix.getNumberClass(),
				bigMatrix.getColumnCount() - startingCol + 1, bigMatrix.getRowCount()
						- startingRow + 1);
		getSubMatrix(bigMatrix, startingRow, startingCol, smallMatrix);
		return smallMatrix;
	}
	
	/**
	 * Copies the submatrix to <code>bigMatrix</code> beginning with the given indices.
	 */
	public static void setSubMatrix(NumberMatrix bigMatrix, int startingRow,
			int startingCol, NumberMatrix subMatrix) {
		for (int r = 1; r <= subMatrix.getRowCount(); r++) {
			for (int c = 1; c <= subMatrix.getColumnCount(); c++) {
				bigMatrix.setEntryRef(r + startingRow - 1, c + startingCol - 1,
						subMatrix.getEntryRef(r, c));
			}
		}
	}

	/**
	 * Returns the head indices for all rows of the given matrix.
	 */
	public static int[] getHeadIndices(NumberMatrix matrix) {
		int indices[] = new int[matrix.getRowCount()];
		for (int r = 1; r <= matrix.getRowCount(); r++) {
			for (int c = 1; c <= matrix.getColumnCount(); c++) {
				indices[r - 1] = matrix.getColumnCount()+1; // zero rows with too big head index (bigger than column count)
				if (!matrix.getEntry(r, c).isZero()) {
					indices[r - 1] = c;
					break;
				}
			}
		}
		return indices;
	}
	
	/**
	 * Returns the given matrix in echelon form.
	 */
	public static NumberMatrix getEchelonForm(NumberMatrix matrix) {
		NumberMatrix inputMatrix = matrix.deepCopy();
		if(inputMatrix.getNumberClass().isAssignableFrom(MInteger.class)) {
			throw new IllegalArgumentException("Cannot return the echelon form of an integer matrix!");
		}
		NumberMatrix outputMatrix = new NumberMatrix(inputMatrix.getNumberClass(),
				inputMatrix.getColumnCount(), inputMatrix.getRowCount());

		// find head indices and put rows in increasing minIndex order
		int[] headIndices = getHeadIndices(inputMatrix);
		sortRows(inputMatrix, headIndices);

		// search for smallest head index
		int minIndex = inputMatrix.getColumnCount();// init with index of last
																								// column
		for (int h = 0; h < headIndices.length; h++) {
			if (headIndices[h] < minIndex && headIndices[h] >= 0)
				minIndex = headIndices[h];
		}
				
		log("Input matrix=" + inputMatrix);
		// manipulate rows
		int counter = 0;
		for (int h = 0; h < headIndices.length; h++) {
			log("h=" + h + ", " + headIndices[h]);
			log("minindex=" + minIndex);
			// manipulate all rows with head index = min head index, than copy to
			// output matrix
			if (headIndices[h] == minIndex) {
				// first row with min head index
				if (counter == 0) {
					NumberTuple row = (NumberTuple) inputMatrix.getRowVector(1)
							.deepCopy();
					outputMatrix.setRowVector(1, row);
					counter++;
				} else {
					NumberTuple row = (NumberTuple) inputMatrix.getRowVector(h + 1)
							.deepCopy();
					log("Row 1:" + row);
					if( !row.isZero()) {
						divByFirstNonZeroEntry(row);
						log("Row 2:" + row);
						MNumber scalar = getFirstNonZeroEntry(outputMatrix.getRowVector(1)); 
						log("Skalar:" + scalar);
						log("h="+h);
						if(scalar != null)
							row.subFrom((((NumberTuple)outputMatrix.getRowVector(1).deepCopy()).multiplyWithScalar(scalar.inverted())));
					}
					log("Row 3:" + row);
					outputMatrix.setRowVector(h + 1, row);
				}
				// copy all rows with bigger min head index to output matrix
			} else {
				NumberTuple row = (NumberTuple) inputMatrix.getRowVector(h + 1)
						.deepCopy();
				outputMatrix.setRowVector(h + 1, row);
			}
		}
			if (outputMatrix.getRowCount() >= 2 && outputMatrix.getColumnCount() >= 2) {
			setSubMatrix(outputMatrix, 2, 2, getEchelonForm(getSubMatrix(
					outputMatrix, 2, 2)));
		}
			log("out" + outputMatrix);
		return outputMatrix;
	}

	/*
	 * Sorts the rows in a matrix according to its head indices.
	 */
	private static void sortRows(NumberMatrix m, int[] headIndices) {
		boolean changed = true;
		int lastIndex = -1;
		while (changed) {
			changed = false;
			lastIndex = -1;
			for (int h = 0; h < headIndices.length; h++) {
				if (lastIndex > -1) {// not first entry; same as h > 0
					if (headIndices[h] < lastIndex) { // && headIndices[h] > 0,  || lastIndex == 0
						NumberTuple lastRow = m.getRowVector(h); // last = h - 1;
																																// row indexing
																																// = h + 1 --> h
						NumberTuple currRow = m.getRowVector(h + 1);
						m.setRowVector(h, currRow);
						m.setRowVector(h + 1, lastRow);
						int tmp = headIndices[h - 1];
						headIndices[h - 1] = headIndices[h];
						headIndices[h] = tmp;
						changed = true;
					}
				}
				lastIndex = headIndices[h];
			}
		}
	}

	/*
	 * Divides this row by the first non-zero number.
	 */
	private static void divByFirstNonZeroEntry(NumberTuple tuple) {
		MNumber scalar = getFirstNonZeroEntry(tuple);
		if(scalar != null)
			tuple.multiplyWithScalar(scalar.inverted());
	}
	
	private static MNumber getFirstNonZeroEntry(NumberTuple tuple) {
		for(int i = 1; i <= tuple.getDimension(); i++) {
			if( !tuple.getEntry(i).isZero()) {
				return tuple.getEntry(i);
			}
		}
		return null;
	}
	
	private static void log(String message) {
//		System.out.println(message);
	}
	
	/**
	 * This method transforms a NumberMatrix into a Gauss normalized
	 * NumberMatrix
	 * 
	 * @param aMatrix to be transformed to Gauss normalized form
	 * @return a Gauss normalized form matrix
	 */
	public static NumberMatrix getNormalizedEchelonForm(NumberMatrix aMatrix){
		NumberMatrix tmp = getEchelonForm(aMatrix);
		int cols = tmp.getColumnCount();
		int rows = tmp.getRowCount();
		
		for (int i = 1; i <= tmp.getRowCount(); i++) {
			NumberTuple r = tmp.getRowVector(i);
			divByFirstNonZeroEntry(r);
			tmp.setRowVector(i,r);
		}
		
		int[] indices = getHeadIndices(tmp);
		for (int row = rows; row > 1; row--) {
			int headIndex = indices[row-1];
			NumberTuple currentRow = (NumberTuple) tmp.getRowVector(row).deepCopy();
			for (int j = row-1; j >= 1; j--) {
				if(headIndex > cols)
					break;
				MNumber scalar = tmp.getRowVector(j).getEntry(headIndex);
				if(scalar.isZero())
					continue;
				NumberTuple newRow = tmp.getRowVector(j).subFrom(((NumberTuple)currentRow.deepCopy()).multiplyWithScalar(scalar));
				tmp.setRowVector(j,newRow);
			}
		}
		return tmp;
	}
}
