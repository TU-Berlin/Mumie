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

package net.mumie.mathletfactory.math.algebra.poly;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.PolynomialOpHolder;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveNeutralElementRule;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

/**
 * This class represents a polynomial both as algebraic entity and as function
 * over R.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Polynomial implements FunctionOverBorelSetIF, UsesOperationIF, ExerciseObjectIF {

	// observe, that these coefficients essentially should define this
	// polynomial:
	protected NumberTuple m_standardCoefficients;

	protected Operation m_operation;
	
	protected boolean m_normalize = true;
	// coefficients for standard form

	MNumber[] m_Newton; // newton finite differences

	MNumber[] m_NewtonZ; // corresponding denominators

	MNumber[] m_xVals; // corresponding zeros of newton polynomial

	MNumber m_tmp; // to avoid "new" in calculations

	/**	This field describes the current edited status. */
	private boolean m_isEdited = true;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;

	private boolean m_standardForm = true;

	private NumberMatrix m_vdm;

	private int m_degree; // formal degree

	private FiniteBorelSet m_domain;

	public Polynomial(Class entryClass, int deg) {
		if (entryClass.isAssignableFrom(MOpNumber.class)) throw new IllegalArgumentException("MOpNumbers may not be used");
		initDataFields(entryClass, deg);
		m_domain = new FiniteBorelSet(entryClass);
		setOperation();
		// this should be the whole of the real axis
	}

	/** Copy constructor. */
	public Polynomial(Polynomial poly) {
		initDataFields(poly.getNumberClass(), poly.getStandardCoefficientsRef().getDimension());
		m_domain = new FiniteBorelSet(poly.getNumberClass());
		m_normalize = poly.isNormalForm();
		setOperation(poly.getOperation());
	}

	public Polynomial(NumberTuple coeff) {
		if (coeff.getNumberClass().isAssignableFrom(MOpNumber.class)) throw new IllegalArgumentException("MOpNumbers may not be used"); 
		initDataFields(coeff.getNumberClass(), coeff.getDimension());
		m_standardCoefficients.copyFrom(coeff);
		m_domain = new FiniteBorelSet(coeff.getNumberClass());
		setOperation();
	}
	
	public Polynomial(NumberTuple coeff, Interval interval) {
		this(coeff);
		m_domain = new FiniteBorelSet(interval);
	}

	public Polynomial(NumberTuple coeff, FiniteBorelSet borelSet) {
		this(coeff);
		m_domain = borelSet.deepCopy();
	}

	private void initDataFields(Class entryClass, int degree) {
		m_standardCoefficients = new NumberTuple(entryClass, degree + 1);
		m_vdm = new NumberMatrix(entryClass, degree + 1, degree + 1);
		m_Newton = new MNumber[degree + 1];
		m_NewtonZ = new MNumber[degree + 1];
		m_xVals = new MNumber[degree + 1];
		m_degree = degree;
		for (int i = 0; i < degree + 1; i++) {
			m_Newton[i] = NumberFactory.newInstance(entryClass);
			m_NewtonZ[i] = NumberFactory.newInstance(entryClass);
			m_xVals[i] = NumberFactory.newInstance(entryClass);
			m_tmp = NumberFactory.newInstance(entryClass);
		}
	}

	public Class getNumberClass() {
		return m_standardCoefficients.getNumberClass();
	}

	public NumberTypeDependentIF groupAction(GroupElementIF aGroupElement) {
		throw new TodoException();
	}

	public NumberSetIF getDomain() {
		return m_domain;
	}

	public FiniteBorelSet getBorelSet() {
		return m_domain;
	}

	public double evaluate(double x) {
		if (m_standardForm) {
			int m = m_standardCoefficients.getDimension();
			double tmp = m_standardCoefficients.getEntryRef(m).getDouble();
			for (int i = m - 1; i >= 1; i--) {
				tmp = tmp * x
						+ m_standardCoefficients.getEntryRef(i).getDouble();
			}
			return tmp;
		} else {
			int m = m_degree;
			double tmp = m_Newton[m].getDouble();
			for (int i = m - 1; i >= 0; i--) {
				tmp = tmp * (x - m_xVals[i].getDouble())
						+ m_Newton[i].getDouble();
			}
			return tmp;
		}
	}

	public void evaluate(double[] xin, double[] yout) {
		for (int i = 0; i < xin.length; ++i) {
			yout[i] = evaluate(xin[i]);
		}
	}

	public int getTrueDegree() {
		if (m_standardForm) {
			int i = m_standardCoefficients.getDimension();
			while (m_standardCoefficients.getEntryRef(i).isZero() && (i >= 1)) {
				--i;
			}
			return i - 1;
		} else {
			throw new TodoException("not yet implemented");
		}
	}

	private void generateVandermontSystem(MNumber[] xVals) {
		for (int i = 1; i <= xVals.length; i++) {
			m_vdm.getEntryAsNumberRef(i, 1).setDouble(1.);
		}
		for (int i = xVals.length; i >= 1; i--) {
			for (int j = 2; j <= xVals.length; j++) {
				m_vdm.setEntry(i, j, m_vdm.getEntry(i, j - 1));
				((MNumber) m_vdm.getEntryRef(i, j)).mult(xVals[i - 1]);
			}
		}
	}

	public void setInterpolationPolynomial(MMAffine2DPoint[] m_points) {
		MNumber[] xVals = new MNumber[m_points.length];
		MNumber[] yVals = new MNumber[m_points.length];
		for (int i = 0; i < m_points.length; i++) { // setting links to
													// coordinates
			xVals[i] = m_points[i].getXProjAsNumberRef();
			yVals[i] = m_points[i].getYProjAsNumberRef();
		}
		setInterpolationPolynomial(xVals, yVals);
	}

	public void setInterpolationPolynomial(MNumber[] xVals, MNumber[] yVals) {
		// generate vandermont system
		m_standardForm = true;
		m_degree = xVals.length - 1;
		generateVandermontSystem(xVals);
		for (int i = 1; i <= xVals.length; i++) {
			m_standardCoefficients.getEntryRef(i).set(yVals[i - 1]);
		}
		try {
			m_vdm.inverse().applyTo(m_standardCoefficients);
		} catch (Exception e) {
			System.out.println("matrix not invertible!");
		}
		setOperation();
	}

	public void setLagrangePolynomial(MNumber[] xVals, int n, MNumber y) {
		m_standardForm = true;
		m_degree = xVals.length - 1;
		generateVandermontSystem(xVals);
		// System.out.println(m_vdm);
		for (int i = 1; i < n; ++i) {
			m_standardCoefficients.getEntryRef(i).setDouble(0.);
		}
		m_standardCoefficients.getEntryRef(n).set(y);

		for (int i = n + 1; i <= xVals.length; ++i) {
			m_standardCoefficients.getEntryRef(i).setDouble(0.);
		}
		if (m_vdm.isInvertible()) {
			m_vdm.inverse().applyTo(m_standardCoefficients);
			// observe that m_vdm now holds the inverse
		}
		setOperation();
	}

	// is OK
	public void setNewtonCoefficients(MNumber[] xVals, MNumber[] yVals) {
		m_standardForm = false;
		m_degree = xVals.length - 1;
		for (int i = 0; i < xVals.length; i++) {
			m_Newton[i].set(yVals[i]);
			m_xVals[i].set(xVals[i]);
			m_NewtonZ[i].setDouble(1.);
		}
		for (int k = 1; k < xVals.length; k++) {
			for (int i = xVals.length - 1; i >= k; i--) { // for the moment
															// not projectively
				(m_Newton[i].sub(m_Newton[i - 1])).div((m_tmp.set(m_xVals[i]))
						.sub(m_xVals[i - k]));
			}
		}
	}

	/**
	 * Returns the standard coefficients of this polynomial in ascending order
	 * and including leading zeros.
	 * 
	 * @mm.sideeffects a reference to an internal field is returned.
	 */
	public NumberTuple getStandardCoefficientsRef() {
		return m_standardCoefficients;
	}

	public void setNewtonPolynomial(MNumber[] xVals, MNumber[] yVals, int i) {
		setNewtonCoefficients(xVals, yVals);
		for (int k = 0; k < i; k++) {
			m_Newton[k].setDouble(0.);
		}
		for (int k = i + 1; k < m_degree + 1; k++) {
			m_Newton[k].setDouble(0);
		}
	}

	public void setFromStdCoefficients(NumberTuple standardCoefficients) {
		if (m_standardCoefficients.getDimension() == standardCoefficients.getDimension()) {
			m_standardCoefficients.copyFrom(standardCoefficients);
		} else
			m_standardCoefficients = new NumberTuple(standardCoefficients);
		setOperation();
	}

	/**
	 * @see net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF#evaluate(net.mumie.mathletfactory.math.number.MNumber,
	 *      net.mumie.mathletfactory.math.number.MNumber)
	 */
	public void evaluate(MNumber xin, MNumber yout) {
		throw new TodoException();
	}

	public String toString() {
		return m_operation.toString();
	}

	public void setOperation(Operation operation) {
		Operation temp = operation.deepCopy();
		temp.expand();
		temp.numerize();
		temp.normalize();
		if (!temp.isPolynomial())
			throw new IllegalArgumentException("No polynomial expression: " + operation);
		
		NumberTuple standardCoefficients = temp.getPolynomialHolder().getAsPolynomial("x").getStandardCoefficientsRef();
		if (m_standardCoefficients.getDimension() == standardCoefficients.getDimension()) {
			m_standardCoefficients.copyFrom(standardCoefficients);
		} else
			m_standardCoefficients = new NumberTuple(standardCoefficients);
		
		m_operation = new Operation(operation);
		setNormalForm(m_normalize);
	}

	public Operation getOperation() {
		return m_operation;
	}

	public void setBorelSet(FiniteBorelSet set) {
		m_domain = set;
	}

	public boolean isEdited() {
		return m_isEdited;
	}

	public void setEdited(boolean edited) {
		m_isEdited = edited;
		if (!edited) setOperation(new Operation(getNumberClass(), "0", false));
		m_operation.setEdited(edited);
	}

	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}

	public Node getMathMLNode() {
		return getMathMLNode(XMLUtils.getDefaultDocument());
	}

	public Node getMathMLNode(Document doc) {
		if (isEdited()) {
			return m_operation.getMathMLNode(doc);
		} else {
			return ExerciseObjectFactory.createUneditedNode(this, doc);
		}
	}

	public void setMathMLNode(Node n) {
		Operation op = new Operation(XMLUtils.parseMathMLNode(getNumberClass(), n));
		setOperation(op);
		setEdited(op.isEdited());
		ExerciseObjectFactory.importExerciseAttributes(n, this);
	}
	
	public void setNormalForm(boolean normalize) {
		m_normalize = normalize;
		m_operation.setNormalForm(normalize);
		if (!m_normalize)
			m_operation.setOpRoot(OpTransform.applyRule(m_operation.getOpRoot(), new RemoveNeutralElementRule()));
	}
	
	public boolean isNormalForm() {
		return m_normalize;
	}
	
	private void setOperation() {
		m_operation = new PolynomialOpHolder(this).getAsOperation();
		m_operation.setNormalForm(m_normalize);
	}
}
