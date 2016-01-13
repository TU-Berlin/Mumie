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
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector;

/**
 * This class offers a collection of static methods to calculate the vector product.
 *
 * @author Schimanowski
 * @mm.docstatus finished
 *
 */
public class Vectorproduct {

	/**
	 * Method getVectorproduct
	 *
	 * @param    v          a  MNumber[]
	 * @param    w          a  MNumber[]
	 *
	 * @return   a MNumber[]
	 *
	 */
	public static MNumber[] getVectorproduct(MNumber[] v, MNumber[] w) {
		MNumber resultVector[] = new MNumber[3];
		resultVector[0] =
			MNumber.subtract(
				MNumber.multiply(v[1], w[2]),
				MNumber.multiply(v[2], w[1]));
		resultVector[1] =
			MNumber.subtract(
				MNumber.multiply(v[2], w[0]),
				MNumber.multiply(v[0], w[2]));
		resultVector[2] =
			MNumber.subtract(
				MNumber.multiply(v[0], w[1]),
				MNumber.multiply(v[1], w[0]));

		return resultVector;
	}

	/**
		 * Method getVectorproductVectors
		 *
		 * @param    vector1          a  MMDefaultR3Vector
		 * @param    vector2          a  MMDefaultR3Vector
		 *
		 * @return   a double[]
		 *
		 */

	public static double[] getVectorproductVectors(
		MMDefaultR3Vector vector1,
		MMDefaultR3Vector vector2) {
		double pseudovector[] = new double[3];
		MDouble arr1[] = new MDouble[3];
		MDouble arr2[] = new MDouble[3];
		MDouble arr3[] = new MDouble[3];
		for (int i = 0; i < 3; i++) {
			arr1[i] = (MDouble) vector1.getCoordinate(i + 1);
			arr2[i] = (MDouble) vector2.getCoordinate(i + 1);
		}

		arr3[0] =
			(MDouble) MNumber.subtract(
				MNumber.multiply(arr1[1], arr2[2]),
				MNumber.multiply(arr1[2], arr2[1]));

		arr3[1] =
			(MDouble) MNumber.subtract(
				MNumber.multiply(arr1[2], arr2[0]),
				MNumber.multiply(arr1[0], arr2[2]));

		arr3[2] =
			(MDouble) MNumber.subtract(
				MNumber.multiply(arr1[0], arr2[1]),
				MNumber.multiply(arr1[1], arr2[0]));

		for (int i = 0; i < 3; i++) {

			pseudovector[i] = arr3[i].getDouble();
		}
		return pseudovector;
	}

	/**
		 * Method getVectorproductVector
		 *
		 * @param    vect          a  MMDefaultR3Vector
		 * @param    vec           a  MMDefaultR3Vector
		 *
		 * @return   a MNumber[]
		 *
		 */
	public static MNumber[] getVectorproductVector(
		MMDefaultR3Vector vect,
		MMDefaultR3Vector vec) {

		MNumber resultVector[] = new MNumber[3];
		MNumber v[] = new MNumber[3];
		MNumber w[] = new MNumber[3];
		for (int i = 0; i < 3; i++) {
			v[i] = (MDouble) vect.getCoordinate(i + 1);
			w[i] = (MDouble) vec.getCoordinate(i + 1);
		}
		resultVector[0] =
			MNumber.subtract(
				MNumber.multiply(v[1], w[2]),
				MNumber.multiply(v[2], w[1]));
		resultVector[1] =
			MNumber.subtract(
				MNumber.multiply(v[2], w[0]),
				MNumber.multiply(v[0], w[2]));
		resultVector[2] =
			MNumber.subtract(
				MNumber.multiply(v[0], w[1]),
				MNumber.multiply(v[1], w[0]));

		return resultVector;
	}

	/**
		 * Method getVectorproductVector
		 *
		 * @param    v          a  MMDefaultR3Vector
		 * @param    r           a  MMDefaultR3Vector
		 *
		 * @return   a MNumber[]
		 *
		 */
	public static NumberTuple VectorproductTuple(MMDefaultR3Vector v,
	MMDefaultR3Vector r) {
	NumberTuple tuple, tuple1, tuple2;
	tuple = new NumberTuple (MDouble.class,3);
	tuple1 = v.getCoordinates();
	tuple2 = r.getCoordinates();

	tuple.setEntry(1,MNumber.subtract(
	MNumber.multiply(tuple1.getEntry(2), tuple2.getEntry(3)),
	MNumber.multiply(tuple1.getEntry(3), tuple2.getEntry(2))));

	tuple.setEntry(2,MNumber.subtract(
		MNumber.multiply(tuple1.getEntry(3), tuple2.getEntry(1)),
		MNumber.multiply(tuple1.getEntry(1), tuple2.getEntry(3))));

	tuple.setEntry(3,MNumber.subtract(
			MNumber.multiply(tuple1.getEntry(1), tuple2.getEntry(2)),
			MNumber.multiply(tuple1.getEntry(2), tuple2.getEntry(1))));

return tuple;
	}
}