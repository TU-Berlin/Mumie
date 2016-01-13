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
import net.mumie.mathletfactory.math.number.MNumber;


/** 
 * This class determines the rank of an arbitrary matrix by using the class
 * {@link EchelonForm}.
 *
 * @author Paehler
 * @mm.docstatus finished
 */

public class MatrixRank
{
	
	/**
	 * Returns the rank of an arbitrary (m x n) matrix
	 *
	 * @param    matrix              a  MNumber[]
	 * @param    m                   determining the height of the matrix
	 * @param    n                   determining the width of the matrix
	 *
	 * @return   the rank of the given matrix
	 *
	 */
	public static int rank(MNumber[] matrix, int m, int n){
		
		MNumber[] eFMatrix = new MNumber[matrix.length];
		try{
			EchelonForm.toRowEchelonForm(matrix,m,n, new MNumber[m*m], eFMatrix);
		} catch( Exception e){ e.printStackTrace();}
		int i;
		
		// traverse the echelon form matrix rowwise and search for non-null rows
		for(i=0; i<Math.min(m,n); i++){
			boolean isNull = true;
			for(int j=0; j<n; j++)
				if(!eFMatrix[i*n+j].isZero()){
					isNull = false;
					break;
				}
			if(isNull)
				return i;
	  }
		return i;
	}
	
  /**
   * Returns the rank of the given number matrix.
   */
	public static int rank(NumberMatrix m){
		return rank(m.getEntriesAsNumberRef(),m.getRowCount(), m.getColumnCount());
	}
}

