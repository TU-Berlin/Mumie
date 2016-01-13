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

package net.mumie.mathletfactory.mmobject.analysis.function.multivariate;


import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.Set;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOpArrayIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;


/**
 * This class takes three strings representing arithmetic expressions of one or two parameters, an array of ranges and performs the calculations for a one- or twodimensional set of elements in this
 * range by feeding the operations with a grid inside the range.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class MMParametricFunctionInR3 extends MMDefaultCanvasObject implements NumberTypeDependentIF, UsesOpArrayIF {

	/** The number class used in the calculations */
	private Class m_numberClass;

	private String[] m_parameters = new String[] { "u", "v" };

	private double[][] m_parameterRanges = new double[][] { new double[] { -1, 1 }, new double[] { -1, 1 } };

	/**
	 * The root node of the f_x(u,v) operation tree as returned by {@link net.mumie.mathletfactory.algebra.op.OpParser}.
	 */
	private Operation m_FxOperation = null;

	/**
	 * The root node of the f_y(u,v) operation tree as returned by {@link net.mumie.mathletfactory.algebra.op.OpParser}.
	 */
	private Operation m_FyOperation = null;

	/**
	 * The root node of the f_z(u,v) operation tree as returned by {@link net.mumie.mathletfactory.algebra.op.OpParser}.
	 */
	private Operation m_FzOperation = null;

	/** The number of vertices in each direction of the grid. */
	private int m_steps;

	/** The twodimensional ((steps+1)^3) array of x values */
	private NumberTuple[][] m_values;

	private double[][] m_doubleValues;

	/**
	 * A flag indicating that {@link #calculate} needs to be called prior to {@link #getValues}.
	 */
	private boolean m_recalculationNeeded = true;

	/**
	 * Each time a new expression is set, the version is increased. Used for synchronisation with views.
	 */
	private int m_version;

	/** A one chain adapter for 2d rendering. is <code>null</code> for N=3. */
	// private MMOneChainInR2 m_chain = null;
	/**
	 * Upon construction, this class preparses the string, hands it over to <code>ExprParser</code>, stores the operation tree and keeps record of the nodes representing the variables (x,y,z) for
	 * later insertion (i.e filling the {@link net.mumie.mathletfactory.algebra.op.node.VariableOp}s with elements of Type <code>MNumber</code>).
	 */
	public MMParametricFunctionInR3( Class numberClass, String xExpr, String yExpr, String zExpr ) {
		m_numberClass = numberClass;
		setOperations( xExpr, yExpr, zExpr );
		setVerticesCount( 50 );
		setDisplayProperties( new SurfaceDisplayProperties(SurfaceDisplayProperties.DEFAULT) );
		setLabel( "f" );
	}

	/**
	 * Constructs a parametric function with the given values.
	 * 
	 * @param numberClass
	 *            the number class used.
	 * @param xExpr
	 *            the (u,v)-expression for the <i>x</i> value
	 * @param yExpr
	 *            the (u,v)-expression for the <i>y</i> value
	 * @param zExpr
	 *            the (u,v)-expression for the <i>z</i> value
	 * @param uRange
	 *            the range in u direction
	 * @param vRange
	 *            the range in v direction
	 */
	public MMParametricFunctionInR3( Class numberClass, String xExpr, String yExpr, String zExpr, double[] uRange, double[] vRange ) {
		this( numberClass, xExpr, yExpr, zExpr );
		setRange( new double[][] { uRange, vRange } );
	}

	/**
	 * Constructs a parametric function with the given values.
	 * 
	 * @param numberClass
	 *            the number class used.
	 * @param xExpr
	 *            the (u,v)-expression for the <i>x</i> value
	 * @param yExpr
	 *            the (u,v)-expression for the <i>y</i> value
	 * @param zExpr
	 *            the (u,v)-expression for the <i>z</i> value
	 * @param uRange
	 *            the range in u direction
	 * @param vRange
	 *            the range in v direction
	 */
	public MMParametricFunctionInR3( Class numberClass, String xExpr, String yExpr, String zExpr, double[] uRange ) {
		this( numberClass, xExpr, yExpr, zExpr );
		setVerticesCount( 200 );
		m_parameters = new String[] { "t" };
		setRange( new double[][] { uRange } );
	}

	/**
	 * Constructs a parametric function with the given values.
	 * 
	 * @param numberClass
	 *            the number class used.
	 * @param xExpr
	 *            the (u,v)-expression for the <i>x</i> value
	 * @param yExpr
	 *            the (u,v)-expression for the <i>y</i> value
	 * @param zExpr
	 *            the (u,v)-expression for the <i>z</i> value
	 * @param uRange
	 *            the range in u direction
	 * @param vRange
	 *            the range in v direction
	 * @param steps
	 *            the number of steps in each direction, the function will be evaluated in a <code>steps x steps</code> mesh.
	 */
	public MMParametricFunctionInR3( Class numberClass, String xExpr, String yExpr, String zExpr, double[] uRange, double[] vRange, int steps ) {
		this( numberClass, xExpr, yExpr, zExpr );
		setRange( new double[][] { uRange, vRange } );
		setVerticesCount( steps );
	}

	/**
	 * Constructs a parametric function with the given values.
	 * 
	 * @param numberClass
	 *            the number class used.
	 * @param xExpr
	 *            the (u,v)-expression for the <i>x</i> value
	 * @param yExpr
	 *            the (u,v)-expression for the <i>y</i> value
	 * @param zExpr
	 *            the (u,v)-expression for the <i>z</i> value
	 * @param uRange
	 *            the range in u direction
	 * @param vRange
	 *            the range in v direction
	 * @param steps
	 *            the number of steps in each direction, the function will be evaluated in a <code>steps x steps</code> mesh.
	 */
	public MMParametricFunctionInR3( Class numberClass, String xExpr, String yExpr, String zExpr, double[] uRange, int steps ) {
		this( numberClass, xExpr, yExpr, zExpr );
		m_parameters = new String[] { "t" };
		setRange( new double[][] { uRange } );
		setVerticesCount( steps );
	}

	/**
	 * Sets the expressions for the parametric function.
	 * 
	 * @param xExpr
	 *            The value in x direction.
	 * @param yExpr
	 *            The value in y direction.
	 * @param zExpr
	 *            The value in z direction.
	 */
	public void setOperations( String xExpr, String yExpr, String zExpr ) {
		setOperations( new Operation[] { new Operation( getNumberClass(), xExpr, true ), new Operation( getNumberClass(), yExpr, true ), new Operation( getNumberClass(), zExpr, true ) } );
	}

	/**
	 * Sets the three operations for the parametric function.
	 * 
	 * @param operations
	 *            an operation array with length 3.
	 */
	public void setOperations( Operation[] operations ) {
		m_FxOperation = operations[0];
		m_FyOperation = operations[1];
		m_FzOperation = operations[2];
		// update used Parameters
		Set parameterSet = new HashSet( 2 );
		for ( int i = 0; i < operations.length; i++ ) {
			String[] params = operations[i].getUsedVariables();
			for ( int j = 0; j < params.length; j++ )
				parameterSet.add( params[j] );
		}
		String[] newParam = new String[parameterSet.size()];
		m_parameters = ( String[] ) parameterSet.toArray( newParam );
		// System.out.println("new function "+toString());
		m_version++;
		m_recalculationNeeded = true;
	}

	/**
	 * Returns the three operations for the parametric function.
	 * 
	 * @return an operation array with length 3.
	 */
	public Operation[] getOperations() {
		return new Operation[] { m_FxOperation, m_FyOperation, m_FzOperation };
	}

	/**
	 * Returns the number class used.
	 * 
	 * @see net.mumie.mathletfactory.number.NumberTypeDependentIF#getNumberClass()
	 */
	public Class getNumberClass() {
		return m_numberClass;
	}

	/**
	 * Sets the range of the grid to be calculated (default is (-1,1) in each direction)
	 */
	public void setRange( double[][] parameterRanges ) {
		boolean hasChanged = false;
		for ( int i = 0; i < parameterRanges.length; i++ )
			for ( int j = 0; j < parameterRanges[0].length; j++ )
				if ( parameterRanges[i][j] != m_parameterRanges[i][j] ) hasChanged = true;
		// System.out.println("hasChanged: "+hasChanged);
		if ( !hasChanged ) return;
		m_parameterRanges = parameterRanges;
		m_version++;
		m_recalculationNeeded = true;
		render();
	}

	/**
	 * Returns the range of the grid to be calculated (default is (-1,1) in each direction)
	 */
	public double[][] getRange() {
		return m_parameterRanges;
	}

	/**
	 * Get the grid of values for the current expressions. Will recalculate if not up to date.
	 */
	public NumberTuple[][] getValues() {
		if ( m_recalculationNeeded ) calculate();
		return m_values;
	}

	public double[][] getDoubleValues() {
		if ( m_recalculationNeeded ) {
			calculateDouble();
		}
		return m_doubleValues;
	}

	public void calculateDouble() {
		if ( m_parameters.length < 1 ) {
			m_doubleValues = new double[][] { { m_FxOperation.getResultDouble(), m_FyOperation.getResultDouble(), m_FzOperation.getResultDouble() } };
		} else if ( m_parameters.length == 1 ) {
			m_doubleValues = new double[m_steps + 1][3];

			double parameterValue = m_parameterRanges[0][0];
			double stepWidth = ( m_parameterRanges[0][1] - m_parameterRanges[0][0] ) / m_steps;

			for ( int i = 0; i <= m_steps; i++ ) {
				if(i!=0)parameterValue = parameterValue + stepWidth;

				// assign the parameter values
				for ( int k = 0; k < m_parameters.length; k++ ) {
					m_FxOperation.assignValue( m_parameters[k], parameterValue );
					m_FyOperation.assignValue( m_parameters[k], parameterValue );
					m_FzOperation.assignValue( m_parameters[k], parameterValue );
				}

				m_doubleValues[i] = new double[] { m_FxOperation.getResultDouble(), m_FyOperation.getResultDouble(), m_FzOperation.getResultDouble() };
			}

		} else {
			m_doubleValues = new double[( m_steps + 1 ) * ( m_steps + 1 )][3];

			double[] stepWidth = new double[2];
			double[] parameterValues = new double[2];

			for ( int i = 0; i < 2; i++ ) {
				stepWidth[i] = ( m_parameterRanges[i][1] - m_parameterRanges[i][0] ) / m_steps;
			}

			// calculate an equidistant grid within the range and
			// evaluate the operation tree for each value of (x,y,z) in the grid
			int index = 0;
			for ( int i = 0; i <= m_steps; i++ ) {
				for ( int j = 0; j <= m_steps; j++ ) {
					parameterValues[0] = m_parameterRanges[0][0] + i * stepWidth[0];
					parameterValues[1] = m_parameterRanges[1][0] + j * stepWidth[1];

					// assign the parameter values
					for ( int k = 0; k < 2; k++ ) {
						m_FxOperation.assignValue( m_parameters[k], parameterValues[k] );
						m_FyOperation.assignValue( m_parameters[k], parameterValues[k] );
						m_FzOperation.assignValue( m_parameters[k], parameterValues[k] );
					}

					m_doubleValues[index++] = new double[] { m_FxOperation.getResultDouble(), m_FyOperation.getResultDouble(), m_FzOperation.getResultDouble() };
				}
			}
		}
		m_recalculationNeeded = false;
	}

	/** For debugging purposes. */
	public String toString() {
		return getLabel() + "(" + m_parameters[0] + "," + ( m_parameters.length > 1 ? m_parameters[1] : "" ) + ") = (" + m_FxOperation + ", " + m_FyOperation + ", " + m_FzOperation + ")";
	}

	/**
	 * Calculates a result for a certain parameter tuple.
	 */
	public NumberTuple evaluateFor( MNumber[] paramValues ) {
		NumberTuple retVal = new NumberTuple( m_numberClass, 3 );
		// insert the argument def into the variable-holders
		for ( int k = 0; k < m_parameters.length; k++ ) {
			m_FxOperation.assignValue( m_parameters[k], paramValues[k] );
			m_FyOperation.assignValue( m_parameters[k], paramValues[k] );
			m_FzOperation.assignValue( m_parameters[k], paramValues[k] );
		}
		retVal.setEntryRef( 1, m_FxOperation.getResult() );
		retVal.setEntryRef( 2, m_FyOperation.getResult() );
		retVal.setEntryRef( 3, m_FzOperation.getResult() );
		return retVal;
	}

	/**
	 * Iterates over the grid and produces the results, storing them for retrieval with {@link #getValues}.
	 */
	public void calculate() {
		if ( m_parameters.length < 1 ) {
			m_values[0][0] = new NumberTuple( m_numberClass, 3 );

			m_values[0][0].setEntryRef( 1, m_FxOperation.getResult() );
			m_values[0][0].setEntryRef( 2, m_FyOperation.getResult() );
			m_values[0][0].setEntryRef( 3, m_FzOperation.getResult() );
		} else {
			double[] stepWidth = new double[m_parameters.length];
			MNumber[] parameterValues = new MNumber[m_parameters.length];

			for ( int i = 0; i < m_parameters.length; i++ ) {
				stepWidth[i] = ( m_parameterRanges[i][1] - m_parameterRanges[i][0] ) / m_steps;
				parameterValues[i] = NumberFactory.newInstance( m_numberClass );
			}

			// calculate an equidistant grid within the range and
			// evaluate the operation tree for each value of (x,y,z) in the grid
			for ( int i = 0; i <= m_steps; i++ ) {
				for ( int j = 0; j <= m_steps; j++ ) {
					m_values[i][j] = new NumberTuple( m_numberClass, 3 );

					parameterValues[0].setDouble( m_parameterRanges[0][0] + ( j * stepWidth[0] ) );
					if ( m_parameters.length > 1 ) parameterValues[1].setDouble( m_parameterRanges[1][0] + ( i * stepWidth[1] ) );

					// assign the parameter values
					for ( int k = 0; k < m_parameters.length; k++ ) {
						m_FxOperation.assignValue( m_parameters[k], parameterValues[k] );
						m_FyOperation.assignValue( m_parameters[k], parameterValues[k] );
						m_FzOperation.assignValue( m_parameters[k], parameterValues[k] );
					}

					// calculate the result of the whole operation and store
					// it in the grid
					m_values[i][j].setEntryRef( 1, m_FxOperation.getResult() );
					m_values[i][j].setEntryRef( 2, m_FyOperation.getResult() );
					m_values[i][j].setEntryRef( 3, m_FzOperation.getResult() );
				}
				if ( m_parameters.length == 1 ) break;
			}
		}
		m_recalculationNeeded = false;
	}

	/** Returns the current version number. Needed for update checks. */
	public int getVersion() {
		return m_version;
	}

	/**
	 * This class catches {@link net.mumie.mathletfactory.mmobject.MMPropertyHandlerIF#OPERATION_ARRAY} property changes.
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange( PropertyChangeEvent e ) {
		if ( !e.getPropertyName().equals( OPERATION_ARRAY ) ) return;
		setOperations( ( Operation[] ) e.getNewValue() );
		ActionManager.performActionCycleFromObject( this );
		// System.out.println("expression is now " + getOperation());
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM;
	}

	/**
	 * Sets the number of steps for each direction in the grid. So steps^2 cells are are created for a two dimensional parametric function.
	 */
	public void setVerticesCount( int steps ) {
		m_steps = steps;
		m_values = new NumberTuple[steps + 1][steps + 1];
		m_doubleValues = new double[( steps + 1 ) * ( steps + 1 )][3];
		m_version++;
		m_recalculationNeeded = true;
		render();
	}

	/** Returns the number of vertices in each direction of the grid. */
	public int getVerticesCount() {
		return m_steps;
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.FUNCTION_TRANSFORM;
	}

	/** Returns the parameters used. */
	public String[] getParameters() {
		return m_parameters;
	}

	/** Sets the parameters used. */
	public void setParameters( String[] strings ) {
		m_parameters = strings;
	}

}
