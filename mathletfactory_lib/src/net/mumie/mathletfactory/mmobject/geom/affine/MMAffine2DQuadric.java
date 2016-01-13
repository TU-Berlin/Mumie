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

package net.mumie.mathletfactory.mmobject.geom.affine;

import java.math.BigInteger;

import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffineGeometryIF;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.numeric.Eigenvalue;
import net.mumie.mathletfactory.math.number.numeric.Eigenvalue2x2;
import net.mumie.mathletfactory.math.number.numeric.MatrixMultiplikation;
import net.mumie.mathletfactory.math.number.numeric.numericdouble.Eigenvalue2x2Double;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * Class for the construction of a quadric in 2-dimensional space.
 * The quadric is internally represented by a symmetric 3x3
 * {@link NumberMatrix}. The quadric in
 * homogeneous coordinates is given by the equation
 * c11 X� + c22 Y� + c33 W� + 2*c12 XY + 2*c13 XW + 2*c23 YW = 0.
 * The corresponding symmetric quadric matrix is then given by<p>
 * [c11 c12 c13]<br>
 * [c12 c22 c23]<br>
 * [c13 c23 c33].<p>
 * This projective quadric corresponds to the affine quadric given by
 * c11 X� + c22 Y� + 2*c12 XY + 2*c13 X + 2*c23 Y + c33 = 0.
 *
 * @author Mrose, liu
 * @mm.docstatus finished
 */
public class MMAffine2DQuadric extends MMDefaultCanvasObject implements AffineGeometryIF {

	/**
	 * Stores the matrix representing the quadric. If a quadric in
	 * homogeneous coordinates is given by the equation<br>
	 * c11 X� + c22 Y� + c33 W� + 2*c12 XY + 2*c13 XW + 2*c23 YW = 0,<br> or in
	 * affine coordinates by<br>
	 * c11 X� + c22 Y� + 2*c12 XY + 2*c13 X + 2*c23 Y + c33 = 0,<br>
	 * the corresponding symmetric quadric matrix is then given by<p>
	 * [c11 c12 c13]<br>
	 * [c12 c22 c23]<br>
	 * [c13 c23 c33].<p>
	 */
	protected NumberMatrix m_quadricMatrix;

	private String m_quadricType;

	private MNumber[] m_eigenvaluesOfMinorMatrix;

	private NumberMatrix m_transformationMatrix;

	private int m_version;

//	private boolean m_recalculationNeeded = true;

	private int steps = 40;

	private NumberTuple[] m_values = new NumberTuple[steps + 1];

	private static double EPSILON = 1e-13;

	/**
	 * Class constructor initializing {@link #m_quadricMatrix} with the identity
	 * matrix.
	 */
	public MMAffine2DQuadric(Class entryClass) {
		this(new NumberMatrix(entryClass, 3));
	}

	/**
	 * Class constructor specifying the <code>quadricMatrix</code> of type
	 * {@link NumberMatrix}
	 * of the quadric to create.
	 * @throws IllegalArgumentException if <code>quadricMatrix</code>
	 * is not symmetric.
	 */
	public MMAffine2DQuadric(NumberMatrix quadricMatrix) {
		// is symmetric?
		if (quadricMatrix.equals(quadricMatrix.transposed()))
			m_quadricMatrix = standardize(quadricMatrix);
		else
			throw new IllegalArgumentException("The given matrix must be symmetric!");
	}

	/**
	 * If there are more negative than positive eigenvalues of the minor matrix
	 * of <code>quadricMatrix</code> then <code>quadricMatrix</code> is negated.
	 * @param quadricMatrix
	 * @return the either or not negated quadricMatrix
	 */
	private NumberMatrix standardize(NumberMatrix quadricMatrix) {
		int pos = 0;
		int neg = 0;
		int posEV = 0;
		int negEV = 0;
		double[] eigenvaluesOfMinorMatrix =
			eigenvalueOfMinorMatrixDouble(quadricMatrix);
		MNumber[] eigenvalues =
			Eigenvalue.eigenvalue(quadricMatrix.getEntriesAsNumberRef());
		for (int j = 0; j < eigenvaluesOfMinorMatrix.length; j++) {
			if (eigenvaluesOfMinorMatrix[j] > EPSILON) {
				posEV = posEV + 1;
			}
			else if (-eigenvaluesOfMinorMatrix[j] > EPSILON) {
				negEV = negEV + 1;
			}
		}
		for (int i = 0; i < eigenvalues.length; i++) {
			if (eigenvalues[i].getDouble() > EPSILON) {
				pos = pos + 1;
			}
			else if (-eigenvalues[i].getDouble() > EPSILON) {
				neg = neg + 1;
			}
		}
		if (negEV > posEV || neg > pos) {
			for (int l = 0;
				l
					< quadricMatrix.getRowCount()
						* quadricMatrix.getColumnCount();
				l++) {
				int m = l / quadricMatrix.getColumnCount() + 1;
				int n =
					(new BigInteger(l + "")
						.mod(
							new BigInteger(
								quadricMatrix.getColumnCount() + "")))
						.intValue()
						+ 1;
				quadricMatrix.getEntryAsNumberRef(m, n).negate();
			}
		}
		return quadricMatrix;
	}

	/**
	 * Class constructor for a quadric given by
	 * <code>c11</code>*X� + 2<code>c12</code>*XY + <code>c22</code>*Y�
	 * + 2<code>c13</code>*X + 2<code>c23</code>*Y + <code>c33</code> = 0.
	 */
	public MMAffine2DQuadric(
		MNumber c11,
		MNumber c12,
		MNumber c13,
		MNumber c22,
		MNumber c23,
		MNumber c33) {
		this(
			new NumberMatrix(
				MNumber.class,
				3,
				new Object[] { c11, c12, c13, c12, c22, c23, c13, c23, c33 }));
	}

	/**
	 * Returns the type of the quadric. Possible types are an imaginary ellipse
	 * (an empty set in IR�), a point, an ellipse, a hyperbola, a pair of
	 * intersecting lines, a parabola, a pair of imaginary parallel lines (an
	 * empty set in IR�), a pair of distinct parallel lines and a pair of
	 * coincident parallel lines.
	 *
	 * @param    quadricMatrix       a  <code>NumberMatrix</code> representing
	 * the quadric.
	 *
	 * @return   a <code>String</code> holding "imaginary ellipse", "point",
	 * "ellipse", "hyperbola", "pair of intersecting lines", "parabola", "pair of
	 * imaginary parallel lines", "pair of distinct parallel lines", "pair of
	 * coincident parallel lines", or "error".
	 *
	 */
	public String getQuadricType(NumberMatrix quadricMatrix) {
		if (isImaginaryEllipse(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("imaginary_ellipse");
		else if (isPoint(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("point");
		else if (isEllipse(quadricMatrix))
    m_quadricType = ResourceManager.getMessage("ellipse");
		else if (isHyperbola(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("hyperbola");
		else if (isPairOfIntersectingLines(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("pair_of_intersecting_lines");
		else if (isParabola(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("parabola");
		else if (isPairOfImaginaryParallelLines(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("pair_of_imaginary_parallel_lines");
		else if (isPairOfDistinctParallelLines(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("pair_of_distinct_parallel_lines");
		else if (isPairOfCoincidentParallelLines(quadricMatrix))
			m_quadricType = ResourceManager.getMessage("pair_of_coincident_parallel_lines");
		else
			m_quadricType =ResourceManager.getMessage("error");
		return m_quadricType;
	}

	/**
	 * Returns the eigenvalues of the minor matrix. If <p>
	 * [c11 c12 c13]<br>
	 * [c12 c22 c23]<br>
	 * [c13 c23 c33].<p>
	 * is the <code>quadricMatrix</code> then the minor matrix is given by<p>
	 * [c11 c12]<br>
	 * [c21 c22].
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>double[]</code> holding the eigenvalues of
	 * <code>quadricMatrix</code>.
	 *
	 */
	public double[] eigenvalueOfMinorMatrixDouble(NumberMatrix quadricMatrix) {
		double[] minorMatrix =
			{
				quadricMatrix.getEntry(1, 1).getDouble(),
				quadricMatrix.getEntry(1, 2).getDouble(),
				quadricMatrix.getEntry(2, 1).getDouble(),
				quadricMatrix.getEntry(2, 2).getDouble()};
		return Eigenvalue2x2Double.eigenvalue(minorMatrix);
	}

	/**
	 * Returns columnwise the orthonormalized
	 * eigenvectors (v11, v12, v21, v22) of the minor matrix. If <p>
	 * [c11 c12 c13]<br>
	 * [c12 c22 c23]<br>
	 * [c13 c23 c33].<p>
	 * is the <code>quadricMatrix</code> then the minor matrix is given by<p>
	 * [c11 c12]<br>
	 * [c21 c22].<p>
	 * If v1 is the first and v2 the second eigenvector, then (v1, v2) is
	 * positive oriented.
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>double[]</code> holding rowwise the orthonormalized
	 * positive oriented eigenvectors of <code>quadricMatrix</code>.
	 *
	 */
	public double[] eigenvectorOfMinorMatrixDouble(NumberMatrix quadricMatrix) {
		double[] minorMatrix =
			{
				quadricMatrix.getEntry(1, 1).getDouble(),
				quadricMatrix.getEntry(1, 2).getDouble(),
				quadricMatrix.getEntry(2, 1).getDouble(),
				quadricMatrix.getEntry(2, 2).getDouble()};
		return Eigenvalue2x2Double.eigenvectorOfSymmetricMatrix(minorMatrix);
	}

	private MNumber[] eigenvectorOfMinorNumberMatrix(NumberMatrix quadricMatrix) {
		MNumber[] minorMatrix =
			{
				quadricMatrix.getEntry(1, 1),
				quadricMatrix.getEntry(1, 2),
				quadricMatrix.getEntry(2, 1),
				quadricMatrix.getEntry(2, 2)};
		return Eigenvalue2x2.eigenvectorOfSymmetricMatrix(minorMatrix);

	}

	/**
	 * Returns the eigenvalues of the minor matrix. If <p>
	 * [c11 c12 c13]<br>
	 * [c12 c22 c23]<br>
	 * [c13 c23 c33].<p>
	 * is the <code>quadricMatrix</code> then the minor matrix is given by<p>
	 * [c11 c12]<br>
	 * [c21 c22].
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>MNumber[]</code> holding the eigenvalues of <code>quadricMatrix</code>.
	 *
	 */
	private static MNumber[] eigenvalueOfMinorNumberMatrix(NumberMatrix quadricMatrix) {
		MNumber[] minorMatrix =
			{
				quadricMatrix.getEntry(1, 1),
				quadricMatrix.getEntry(1, 2),
				quadricMatrix.getEntry(2, 1),
				quadricMatrix.getEntry(2, 2)};
		return Eigenvalue2x2.eigenvalue(minorMatrix);
	}

	/**
	 * Returns columnwise the orthonormalized
	 * eigenvectors (v11, v12, v21, v22) of the minor matrix. If <p>
	 * [c11 c12 c13]<br>
	 * [c12 c22 c23]<br>
	 * [c13 c23 c33].<p>
	 * is the <code>quadricMatrix</code> then the minor matrix is given by<p>
	 * [c11 c12]<br>
	 * [c21 c22].<p>
	 * If v1 is the first and v2 the second eigenvector, then (v1, v2) is
	 * positive oriented.
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>MNumber[]</code>
	 *
	 */
	//  private MNumber[] eigenvectorOfMinorNumberMatrix(NumberMatrix quadricMatrix) {
	//    MNumber[] minorMatrix =
	//    {quadricMatrix.getEntry(1, 1), quadricMatrix.getEntry(1, 2),
	//        quadricMatrix.getEntry(2, 1), quadricMatrix.getEntry(2, 2)};
	//    return Eigenvalue2x2.eigenvectorOfSymmetricMatrix(minorMatrix);
	//  }

	/**
	 * Checkes whether a given quadric is an ellipse.
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents an ellipse.
	 *
	 */
	private static boolean isEllipse(NumberMatrix quadricMatrix) {
		/* True if the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * have the same sign and are |=0 and if c11*det C < 0, where C is the
		 * quadricMatrix.
		 */
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.getDouble()
				> EPSILON
				&& MNumber
					.multiply(
						quadricMatrix.getEntry(1, 1),
						quadricMatrix.determinant())
					.getDouble()
					< 0.0);
	}

	/**
	 * Checkes whether a given quadric is a point.
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents a point.
	 *
	 */
	private static boolean isPoint(NumberMatrix quadricMatrix) {
		/* True if the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * have the same sign and are |=0 and if c11*det C = 0, where C is the
		 * quadricMatrix.
		 */
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.getDouble()
				> EPSILON
				&& MNumber
					.multiply(
						quadricMatrix.getEntry(1, 1),
						quadricMatrix.determinant())
					.absed()
					.getDouble()
					< EPSILON);
	}

	/**
	 * Checkes whether a given quadric is an imaginary
	 * ellipse (an empty set in IR^2).
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents an imaginary ellipse.
	 *
	 */
	private static boolean isImaginaryEllipse(NumberMatrix quadricMatrix) {
		/* True if the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * have the same sign and are |=0 and if c11*det C > 0, where C is the
		 * quadricMatrix.
		 */
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.getDouble()
				> EPSILON
				&& MNumber
					.multiply(
						quadricMatrix.getEntry(1, 1),
						quadricMatrix.determinant())
					.getDouble()
					> EPSILON);
	}

	/**
	 * Checkes whether a given quadric is a hyperbola.
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents a hyperbola.
	 *
	 */
	private static boolean isHyperbola(NumberMatrix quadricMatrix) {
		/* True if the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * have a different sign and are |=0 and if det C |= 0, where C is the
		 * quadricMatrix.
		 */
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.getDouble()
				< 0.0
				&& !(quadricMatrix.determinant().absed().getDouble() < EPSILON));
	}

	/**
	 * Checkes whether a given quadric is a pair
	 * of intersecting lines.
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents a pair of intersecting lines.
	 *
	 */
	private static boolean isPairOfIntersectingLines(NumberMatrix quadricMatrix) {
		/* True if the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * have a different sign and are |=0 and if det C = 0, where C is the
		 * quadricMatrix.
		 */
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.getDouble()
				< 0.0
				&& quadricMatrix.determinant().absed().getDouble() < EPSILON);
	}

	/**
	 * Checkes whether a given quadric is a parabola.
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents a parabola.
	 *
	 */
	private static boolean isParabola(NumberMatrix quadricMatrix) {
		/* True if one of the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * is zero and rank C = 3, where C is the quadricMatrix.
		 */
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.absed()
				.getDouble()
				< EPSILON
				&& quadricMatrix.rank() == 3);
	}

	/**
	 * Checkes whether a given quadric is a pair of distinct parallel lines.
	 *
	 * @param    quadricMatrix         a <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents a pair of distinct parallel lines.
	 *
	 */
	private static boolean isPairOfDistinctParallelLines(NumberMatrix quadricMatrix) {
		/* True if one of the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * is zero and rank C = 2 and
		 *    [c11 c13]      [c22 c23]
		 * det[       ] + det[       ] <= 0,
		 *    [c13 c33]      [c23 c33]
		 * where C is the quadricMatrix.
		 */
		MNumber det1 =
			MNumber.subtract(
				MNumber.multiply(
					quadricMatrix.getEntry(1, 1),
					quadricMatrix.getEntry(3, 3)),
				MNumber.multiply(
					quadricMatrix.getEntry(1, 3),
					quadricMatrix.getEntry(1, 3)));
		MNumber det2 =
			MNumber.subtract(
				MNumber.multiply(
					quadricMatrix.getEntry(2, 2),
					quadricMatrix.getEntry(3, 3)),
				MNumber.multiply(
					quadricMatrix.getEntry(2, 3),
					quadricMatrix.getEntry(2, 3)));
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.absed()
				.getDouble()
				< EPSILON
				&& quadricMatrix.rank() == 2
				&& MNumber.add(det1, det2).getDouble() <= EPSILON);
	}

	/**
	 * Checkes whether a given quadric is a
	 * pair of imaginary parallel lines (an empty set in IR^2).
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents a pair of imaginary parallel lines.
	 *
	 */
	private static boolean isPairOfImaginaryParallelLines(NumberMatrix quadricMatrix) {
		/* True if one of the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * is zero and rank C = 2 and
		 *    [c11 c13]      [c22 c23]
		 * det[       ] + det[       ] > 0,
		 *    [c13 c33]      [c23 c33]
		 * where C is the quadricMatrix.
		 */
		MNumber det1 =
			MNumber.subtract(
				MNumber.multiply(
					quadricMatrix.getEntry(1, 1),
					quadricMatrix.getEntry(3, 3)),
				MNumber.multiply(
					quadricMatrix.getEntry(1, 3),
					quadricMatrix.getEntry(1, 3)));
		MNumber det2 =
			MNumber.subtract(
				MNumber.multiply(
					quadricMatrix.getEntry(2, 2),
					quadricMatrix.getEntry(3, 3)),
				MNumber.multiply(
					quadricMatrix.getEntry(2, 3),
					quadricMatrix.getEntry(2, 3)));
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.absed()
				.getDouble()
				< EPSILON
				&& quadricMatrix.rank() == 2
				&& MNumber.add(det1, det2).getDouble() > EPSILON);
	}

	/**
	 * Checkes whether a given quadric is a
	 * pair of coincident parallel lines (an empty set in IR^2).
	 *
	 * @param    quadricMatrix         a  <code>NumberMatrix</code>
	 *
	 * @return   a <code>boolean</code> which is true if <code>quadricMatrix</code>
	 * represents a pair of coincident parallel lines.
	 *
	 */
	private static boolean isPairOfCoincidentParallelLines(NumberMatrix quadricMatrix) {
		/* True if one of the two eigenvalues of the minor matrix
		 * [c11 c12]
		 * [c21 c22]
		 * is zero and rank C = 1, where C is the
		 * quadricMatrix.
		 */
		return (
			MNumber
				.multiply(
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[0],
					eigenvalueOfMinorNumberMatrix(quadricMatrix)[1])
				.absed()
				.getDouble()
				< EPSILON
				&& quadricMatrix.rank() == 1);
	}

	private void calculate(boolean flag) {
		if (!isDegenerated()) {
			this.m_eigenvaluesOfMinorMatrix =
				eigenvalueOfMinorNumberMatrix(m_quadricMatrix);
			this.m_quadricType = getQuadricType(m_quadricMatrix);
			this.m_transformationMatrix = getTransformationMatrix();
			MNumber defT =
				NumberFactory.newInstance(
					m_quadricMatrix.getEntry(1, 1).getClass());
			MNumber[] sInv = eigenvectorOfMinorNumberMatrix(m_quadricMatrix);
			MNumber[] D =
				new MNumber[] {
					m_eigenvaluesOfMinorMatrix[0],
					m_eigenvaluesOfMinorMatrix[1] };
			MNumber[] A =
				new MNumber[] {
					MNumber.multiply(
						defT.create().setDouble(2.0),
						m_quadricMatrix.getEntry(1, 3)),
					MNumber.multiply(
						defT.create().setDouble(2.0),
						m_quadricMatrix.getEntry(2, 3))};
			MNumber[] B = MatrixMultiplikation.multinxm(sInv, A, 2, 2, 1);
			MNumber b = getb(B, D);
			MNumber[] alphaBeta = getAlphaBeta(b, D, B);
			MNumber alpha = alphaBeta[0];
			MNumber beta = alphaBeta[1];

			for (int i = 0; i <= steps; i++) {
				m_values[i] = new NumberTuple(MDouble.class, 2);
				defT.setDouble(
					getTRange()[0]
						+ (i * (getTRange()[1] - getTRange()[0])) / steps);

				MNumber[] par = getParametrization(flag, defT, alpha, beta);
				MNumber[] helper = getCoordinates(par);
				m_values[i].setEntryRef(1, helper[0]);
				m_values[i].setEntryRef(2, helper[1]);
			}
		}
//		m_recalculationNeeded = false;
	}

	private MNumber[] getCoordinates(MNumber[] par) {
		MNumber[] ST =
			new MNumber[] {
				m_transformationMatrix.getEntry(1, 1),
				m_transformationMatrix.getEntry(1, 2),
				m_transformationMatrix.getEntry(2, 1),
				m_transformationMatrix.getEntry(2, 2)};
		MNumber[] sMultTmultPar =
			MatrixMultiplikation.multinxm(ST, par, 2, 2, 1);
		MNumber[] result =
			new MNumber[] {
				(sMultTmultPar[0].copy()).add(
					m_transformationMatrix.getEntry(1, 3)),
				(sMultTmultPar[1].copy()).add(
					m_transformationMatrix.getEntry(2, 3))};
		return result;
	}
	private MNumber[] getParametrization(
		boolean flag,
		MNumber defT,
		MNumber alpha,
		MNumber beta) {
		if (m_quadricType.equals(ResourceManager.getMessage("ellipse"))) {
			//[a*cos(t), b*sin(t)]
			return new MNumber[] {
				alpha.copy().mult(defT.copy().cos()),
				beta.copy().mult(defT.copy().sin())};
		}
		else if (m_quadricType.equals(ResourceManager.getMessage("hyperbola"))) {
			//[+-a*cosh(t),b*sinh(t)]
			if (flag) {
				return new MNumber[] {
					alpha.copy().mult(defT.copy().cosh()),
					beta.copy().mult(defT.copy().sinh())};
			}
			else
				return new MNumber[] {
					alpha.copy().mult(defT.copy().cosh()).negate(),
					beta.copy().mult(defT.copy().sinh())};
		}
		else if (m_quadricType.equals(ResourceManager.getMessage("parabola"))) {
			//[t,t�/(2*a�)]
			return new MNumber[] {
				defT,
				defT.copy().mult(defT).div(
					alpha.copy().mult(alpha).mult(
						defT.create().setDouble(2.0))).negate()};
		}
		else if (m_quadricType.equals(ResourceManager.getMessage("pair_of_intersecting_lines"))) {
			//[t,+-t*b/a]
			if (flag)
				return new MNumber[] {
					beta.copy().div(alpha).mult(defT),
					defT };

			else
				return new MNumber[] {
					beta.copy().div(alpha).mult(defT).negate(),
					defT };
		}
		else if (m_quadricType.equals(ResourceManager.getMessage("pair_of_distinct_parallel_lines"))) {
			//[+-a,t]
			if (flag) {
				return new MNumber[] { alpha, defT };
			}
			else
				return new MNumber[] { alpha.copy().negate(), defT };
		}
		else if (m_quadricType.equals(ResourceManager.getMessage("pair_of_coincident_parallel_lines"))) {
			//[0,t]
			return new MNumber[] { defT.create().setDouble(0.0), defT };
		}
		else
			return new MNumber[] {
				defT.create().setDouble(0.0),
				defT.create().setDouble(0.0)};
	}

	private MNumber[] getAlphaBeta(MNumber b, MNumber[] D, MNumber[] B) {
		MNumber alpha, beta, P;
		if (m_quadricType.equals(ResourceManager.getMessage("ellipse"))
			|| m_quadricType.equals(ResourceManager.getMessage("hyperbola"))) {
			alpha =
				MNumber.squareRoot(
					MNumber.multiply(b, D[0].inverted()).absed());
			beta =
				MNumber.squareRoot(
					MNumber.multiply(b, D[1].inverted()).absed());
		}
		else if (
			m_quadricType.equals(ResourceManager.getMessage("point"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_intersecting_lines"))) {
			alpha = MNumber.squareRoot(D[0].inverted().absed());
			beta = MNumber.squareRoot(D[1].inverted().absed());
		}
		else if (
			m_quadricType.equals(ResourceManager.getMessage("parabola"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_coincident_parallel_lines"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_distinct_parallel_lines"))) {
			P = B[1].absed();
			if (P.getDouble() > EPSILON) {
				alpha =
					MNumber.squareRoot(
						P
							.copy()
							.mult(
								(b.create().setDouble(2.0).mult(D[0]))
									.inverted())
							.absed());
			}
			else {
				alpha = MNumber.squareRoot(D[0].inverted().absed());
			}
			beta = b.create().setDouble(0.0);
		}
		else {
			alpha = b.create().setDouble(0.0);
			beta = b.create().setDouble(0.0);
		}
//			  System.out.println("a"+ alpha);
//			  System.out.println("�"+ beta);
		return new MNumber[] { alpha, beta };
	}

	private double[] getTRange() {
		if (m_quadricType.equals(ResourceManager.getMessage("ellipse"))) {
			return new double[] { -Math.PI, Math.PI };
		}
		else
			return new double[] { -6, 6 };
	}

	//  public NumberMatrix getTransformationMatrix(){
	//	  MNumber[] m = new MNumber[]{m_quadricMatrix.getEntry(1,3),
	//			  m_quadricMatrix.getEntry(2,3)};
	//  }
	public NumberMatrix getTransformationMatrix() {
//			  System.out.print(m_quadricMatrix.getEntry(1,1));
//				System.out.print("  "+m_quadricMatrix.getEntry(1,2));
//				System.out.println("  "+m_quadricMatrix.getEntry(1,3));
//				System.out.print(m_quadricMatrix.getEntry(2,1));
//				System.out.print("  "+m_quadricMatrix.getEntry(2,2));
//				System.out.println("  "+m_quadricMatrix.getEntry(2,3));
//				System.out.print(m_quadricMatrix.getEntry(3,1));
//		    	System.out.print("  "+m_quadricMatrix.getEntry(3,2));
//				System.out.println("  "+m_quadricMatrix.getEntry(3,3));

		MNumber TWO = m_quadricMatrix.getEntry(1, 1).create().setDouble(2.0);

		MNumber[] A =
			new MNumber[] {
				MNumber.multiply(TWO, m_quadricMatrix.getEntry(1, 3)),
				MNumber.multiply(TWO, m_quadricMatrix.getEntry(2, 3))};

		//System.out.println("A:   "+ A[0]+"  " +A[1]);

		MNumber[] sInv = eigenvectorOfMinorNumberMatrix(m_quadricMatrix);

		 //System.out.println("sInv:   "+sInv[0]+ "  "+ sInv[1]+ "  " +sInv[2]+ "  "+ sInv[3]);

		MNumber[] B = MatrixMultiplikation.multinxm(sInv, A, 2, 2, 1);

		MNumber[] S = transpose(sInv);
		MNumber[] T = getT(B);
		//System.out.println("T:   "+T[0]+ "  "+ T[1]+ "  " +T[2]+ "  "+ T[3]);
		MNumber[] ST = MatrixMultiplikation.multinxm(S, T, 2, 2, 2);
		MNumber[] D =
			new MNumber[] {
				eigenvalueOfMinorNumberMatrix(m_quadricMatrix)[0],
				eigenvalueOfMinorNumberMatrix(m_quadricMatrix)[1] };
		//System.out.println("D:   "+ D[0]+"  " +D[1]);

		MNumber b = getb(B, D);
		//System.out.println("b:  "+b);

		MNumber[] a0 = geta0(B, D);
		//System.out.println("a0:  "+a0[0]+" "+a0[1]);

		MNumber[] a1 = geta1(B, b);
		//System.out.println("a1:  "+a1[0]+" "+a1[1]);
		MNumber[] a1plusa0 =
			new MNumber[] {
				a1[0].copy().add(a0[0]),
				a1[1].copy().add(a0[1])};
		MNumber[] sMulta1plusa0 =
			MatrixMultiplikation.multinxm(S, a1plusa0, 2, 2, 1);
		MNumber[] matrix =
			new MNumber[] {
				ST[0],
				ST[1],
				sMulta1plusa0[0],
				ST[2],
				ST[3],
				sMulta1plusa0[1],
				m_quadricMatrix.getEntry(1, 1).create().setDouble(0.0),
				m_quadricMatrix.getEntry(1, 1).create().setDouble(0.0),
				m_quadricMatrix.getEntry(1, 1).create().setDouble(1.0)};
		m_transformationMatrix = new NumberMatrix(MNumber.class, 3, matrix);
//		System.out.print(m_transformationMatrix.getEntry(1,3));
//		System.out.println("  "+m_transformationMatrix.getEntry(2,3));
		return m_transformationMatrix;
	}

	private MNumber getb(MNumber[] B, MNumber[] D) {
		MNumber b;
		if (m_quadricType.equals(ResourceManager.getMessage("ellipse"))
			|| m_quadricType.equals(ResourceManager.getMessage("point"))
			|| m_quadricType.equals(ResourceManager.getMessage("hyperbola"))
			|| m_quadricType.equals(ResourceManager.getMessage("pair_of_intersecting_lines"))) {
			b =
				m_quadricMatrix.getEntry(3, 3).copy().sub(
					MNumber.divide(
						MNumber.add(
							MNumber.divide(MNumber.multiply(B[0], B[0]), D[0]),
							MNumber.divide(MNumber.multiply(B[1], B[1]), D[1])),
						B[0].create().setDouble(4.0)));
		}
		else if (
			m_quadricType.equals(ResourceManager.getMessage("parabola"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_distinct_parallel_lines"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_coincident_parallel_lines"))) {
			b =
				m_quadricMatrix.getEntry(3, 3).copy().sub(
					MNumber.divide(
						MNumber.divide(MNumber.multiply(B[0], B[0]), D[0]),
						B[0].create().setDouble(4.0)));
		}
		else {
			b = B[0].create().setDouble(0.0);
		}
		return b;
	}

	private MNumber[] geta0(MNumber[] B, MNumber[] D) {
		MNumber[] a0;
		if (m_quadricType.equals(ResourceManager.getMessage("ellipse"))
			|| m_quadricType.equals(ResourceManager.getMessage("point"))
			|| m_quadricType.equals(ResourceManager.getMessage("hyperbola"))
			|| m_quadricType.equals(ResourceManager.getMessage("pair_of_intersecting_lines"))) {
			a0 =
				new MNumber[] {
					(MNumber
						.divide(
							B[0],
							MNumber.multiply(
								B[0].create().setDouble(2.0),
								D[0])))
						.negate(),
					(MNumber
						.divide(
							B[1],
							MNumber.multiply(
								B[0].create().setDouble(2.0),
								D[1])))
						.negate()};
		}
		else if (
			m_quadricType.equals(ResourceManager.getMessage("parabola"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_distinct_parallel_lines"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_coincident_parallel_lines"))) {
			a0 =
				new MNumber[] {
					(MNumber
						.divide(
							B[0],
							MNumber.multiply(
								B[0].create().setDouble(2.0),
								D[0])))
						.negate(),
					B[0].create().setDouble(0.0)};
		}
		else {
			a0 =
				new MNumber[] {
					B[0].create().setDouble(0.0),
					B[0].create().setDouble(0.0)};
		}
		return a0;
	}

	private MNumber[] geta1(MNumber[] B, MNumber b) {
		MNumber[] a1 =
			new MNumber[] {
				b.create().setDouble(0.0),
				b.create().setDouble(0.0)};
//		if (m_quadricType.equals("ellipse")
//			|| m_quadricType.equals("point")
//			|| m_quadricType.equals("hyperbola")
//			|| m_quadricType.equals("pair of intersecting lines")) {
//			if (B[1].absed().getDouble() > EPSILON) {
//				a1[1] =
//					(B[1].copy().mult(b))
//						.div(B[1].copy().mult(B[1]))
//						.negate();
//			}
//		}
		 if (
			m_quadricType.equals(ResourceManager.getMessage("parabola"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_distinct_parallel_lines"))
				|| m_quadricType.equals(ResourceManager.getMessage("pair_of_coincident_parallel_lines"))) {
			if (B[1].copy().mult(B[1]).getDouble() > EPSILON) {
				a1[1] =
					(B[1].copy().mult(b))
						.div(B[1].copy().mult(B[1]))
						.negate();
			}
		}
		return a1;
	}

	private static MNumber[] transpose(MNumber[] matrix) {
		return new MNumber[] {
			matrix[0].copy(),
			matrix[2].copy(),
			matrix[1].copy(),
			matrix[3].copy()};
	}

	private MNumber[] getT(MNumber[] B) {
		MNumber[] T =
			new MNumber[] {
				B[0].create().setDouble(1.0),
				B[0].create().setDouble(0.0),
				B[0].create().setDouble(0.0),
				B[0].create().setDouble(1.0)};

		//		if(getQuadricType(getMatrix()) .equals("point")
		//		   ||getQuadricType(getMatrix()).equals("elipse")
		//		   ||getQuadricType(getMatrix()).equals("hyperbola")
		//		   ||getQuadricType(getMatrix()).equals("pair of intersecting lines")){
		if (getQuadricType(getMatrix()).equals(ResourceManager.getMessage("parabola"))
			|| getQuadricType(getMatrix()).equals(
				ResourceManager.getMessage("pair_of_distinct_parallel_lines"))
			|| getQuadricType(getMatrix()).equals(
				ResourceManager.getMessage("pair_of_coincident_parallel_lines"))) {
			if (B[1].absed().getDouble() > EPSILON) {
				T[3] = B[1].copy().div(B[1].absed());
			}
		}
		return T;
	}
	public boolean isDegenerated() {
		return (getQuadricType(m_quadricMatrix).equals(ResourceManager.getMessage("point")));
	}

	/**
	 * Returns the quadric matrix representing this quadric.
	 *
	 * @return   a <code>NumberMatrix</code> holding the quadric matrix
	 *
	 */
	public NumberMatrix getMatrix() {
		//if (m_quadricMatrix.getEntry(3, 3).getDouble() <= 0.0)
			return m_quadricMatrix;
		//else
			//return m_quadricMatrix.negate();
	}

	/**
	 * Returns the minor matrix of the quadric matrix representing this quadric.
	 * If this quadric is represented by the quadric matrix<p>
	 * [c11 c12 c13] <br>
	 * [c12 c22 c23]<br>
	 * [c13 c23 c33]<p>
	 * then the method returns<p>
	 * [c11 c12]<br>
	 * [c21 c22].
	 *
	 * @return   a <code>NumberMatrix</code> holding the minor matrix
	 *
	 */
	//  private NumberMatrix getMinorMatrix( ) {
	//    MNumber[] minorMatrix = {m_quadricMatrix.getEntry(1,1),
	//        m_quadricMatrix.getEntry(1,2), m_quadricMatrix.getEntry(2,1), m_quadricMatrix.getEntry(2,2)};
	//    NumberMatrix M = new NumberMatrix(MDouble.class, 2 , minorMatrix);
	//
	//    return M;
	//  }

	public int getGeomType() {
		return GeometryIF.AFFINE2D_GEOMETRY;
	}

	public Affine2DPoint getPoint() {
		return new Affine2DPoint(
			MDouble.class,
			m_transformationMatrix.getEntry(1, 3).getDouble(),
			m_transformationMatrix.getEntry(2, 3).getDouble());
	}

	//  public  double[][] getPoints(){
	//	  if(m_quadricType.equals("pair of coincident parallel lines")){
	//
	//		  MNumber[] secondPoint =
	//			  new MNumber[] {m_transformationMatrix.getEntry(1,2).addTo(
	//				  m_transformationMatrix.getEntry(1,3)),
	//				  m_transformationMatrix.getEntry(2,2).addTo(
	//				  m_transformationMatrix.getEntry(2,3))};
	//
	//				  return new double[][]
	//				  {{m_transformationMatrix.getEntry(1,3).getDouble(),
	//							 m_transformationMatrix.getEntry(2,3).getDouble()},
	//							 {secondPoint[0].getDouble(),secondPoint[1].getDouble()}};
	//	  }
	////	  else {
	////		  if(m_quadricType.equals("pair of distinct parallel lines")){
	////
	////	      }
	////	      else if(m_quadricType.equals("pair of intersecting lines")){
	////			  MNumber helper1 = m_quadricMatrix.getEntry(1,1).create();
	////			  MNumber helper2 = m_quadricMatrix.getEntry(1,1).create();
	////			  return new double[][]{{m_transformationMatrix.getEntry(1,3).getDouble(),
	////						  m_transformationMatrix.getEntry(2,2).getDouble()},
	////						  {getCoordinates(new MNumber{helper1,helper2})},
	////						  {}};
	////	      }
	//	      else return new double[][]{{0.0,0.0},{0.0,0.0}};
	//  }

	public Class getNumberClass() {
		return m_quadricMatrix.getNumberClass();
	}

	public AffineGeometryIF groupAction(AffineGroupElement actingGroupElement) {
		// returns T^{-t}CT^{-1}
		NumberMatrix tInverse =
			new NumberMatrix(m_quadricMatrix.getNumberClass(), 3, 3);
		NumberMatrix tInverseTrans =
			new NumberMatrix(m_quadricMatrix.getNumberClass(), 3, 3);
		tInverse =
			((AffineGroupElement) actingGroupElement.inverse())
				.getLinearMatrixRepresentation();
		tInverseTrans = tInverse.transposed();
		m_quadricMatrix =//(tInverseTrans.mult(m_quadricMatrix)).mult(tInverse);
			standardize((tInverseTrans.mult(m_quadricMatrix)).mult(tInverse));
		m_version++;
		return this;
	}

	public GeometryIF groupAction(GroupElementIF actingGroupElement) {
		// returns T^{-t}CT^{-1}
		NumberMatrix tInverse =
			new NumberMatrix(m_quadricMatrix.getNumberClass(), 3, 3);
		NumberMatrix tInverseTrans =
			new NumberMatrix(m_quadricMatrix.getNumberClass(), 3, 3);
		tInverse =
			((AffineGroupElement) actingGroupElement.inverse())
				.getLinearMatrixRepresentation();
		tInverseTrans = tInverse.transposed();
		m_quadricMatrix = //(tInverseTrans.mult(m_quadricMatrix)).mult(tInverse);
			standardize((tInverseTrans.mult(m_quadricMatrix)).mult(tInverse));
		m_version++;
		return this;
	}

	/**
	 * Changes this quadric into the quadric given by <code>entries</code>
	 * and returns it also.
	 *
	 * @return the <code>Affine2DQuadric</code> which was determined by
	 * <code>entries</code>
	 */
	public MMAffine2DQuadric setFromQuadricMatrix(MNumber[] entries) {
		if (entries.length == 6) {
			getMatrix().setEntry(1, 1, entries[0]);
			getMatrix().setEntry(
				1,
				2,
				entries[1].copy().mult(entries[1].create().setDouble(0.5)));
			getMatrix().setEntry(
				2,
				1,
				entries[1].copy().mult(entries[1].create().setDouble(0.5)));
			getMatrix().setEntry(2, 2, entries[2]);
			getMatrix().setEntry(1, 3, entries[3].copy().mult(entries[1].create().setDouble(0.5)));
			getMatrix().setEntry(3, 1, entries[3].copy().mult(entries[1].create().setDouble(0.5)));
			getMatrix().setEntry(2, 3, entries[4].copy().mult(entries[1].create().setDouble(0.5)));
			getMatrix().setEntry(3, 2, entries[4].copy().mult(entries[1].create().setDouble(0.5)));
			getMatrix().setEntry(3, 3, entries[5]);
			m_quadricMatrix = standardize(m_quadricMatrix);
			m_quadricType = getQuadricType(m_quadricMatrix);
			m_version++;
			m_eigenvaluesOfMinorMatrix =
				eigenvalueOfMinorNumberMatrix(m_quadricMatrix);
			m_transformationMatrix = getTransformationMatrix();
		}
		else
			throw new IllegalArgumentException(" wrong dimension of entries ");
		return this;
	}

	public MMAffine2DQuadric setFromQuadricMatrix(NumberMatrix quadricMatrix) {
		if (quadricMatrix.getDimension() == 3) {
			quadricMatrix = standardize(quadricMatrix);
			getMatrix().setEntry(1, 1, quadricMatrix.getEntry(1, 1));
			getMatrix().setEntry(1, 2, quadricMatrix.getEntry(1, 2));
			getMatrix().setEntry(1, 3, quadricMatrix.getEntry(1, 3));
			getMatrix().setEntry(2, 1, quadricMatrix.getEntry(2, 1));
			getMatrix().setEntry(2, 2, quadricMatrix.getEntry(2, 2));
			getMatrix().setEntry(2, 3, quadricMatrix.getEntry(2, 3));
			getMatrix().setEntry(3, 1, quadricMatrix.getEntry(3, 1));
			getMatrix().setEntry(3, 2, quadricMatrix.getEntry(3, 2));
			getMatrix().setEntry(3, 3, quadricMatrix.getEntry(3, 3));
			m_version++;
			m_eigenvaluesOfMinorMatrix =
				eigenvalueOfMinorNumberMatrix(m_quadricMatrix);
			m_transformationMatrix = getTransformationMatrix();
		}
		else
			throw new IllegalArgumentException(" wrong dimension of quadric matrix ");
		return this;
	}

	public NumberTuple[] getValues(boolean flag) {
		calculate(flag);
		NumberTuple[] helper = new NumberTuple[steps + 1];
		for (int i = 0; i < steps + 1; i++) {
			helper[i] = m_values[i];
			//System.out.println(m_values[i]);
		}
		return helper;
	}
	public NumberTuple[] getValuesFirstComponent() {
		return getValues(true);
	}

	public NumberTuple[] getValuesSecondComponent() {
		return getValues(false);
	}

	public int getVersion() {
		return m_version;
	}

  public int getDefaultTransformType() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }
}
