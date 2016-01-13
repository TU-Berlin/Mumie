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

package net.mumie.mathletfactory.mmobject;

import java.beans.PropertyChangeListener;

/**
 *  This interface is implemented by all
 *  {@link net.mumie.mathletfactory.mmobject.MMObjectIF MMObject}s, that
 *  have an interactive container representation (it is therefore extended
 *  by {@link MMObjectIF}). Besides extending the listener for
 *  the java-beans property update mechanism it standardizes the names of
 *  properties that are used by <code>MMObject</code>s and their container
 *  representations.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public interface PropertyHandlerIF extends PropertyChangeListener {

  /** Property for mmobjects using operations */
  public final static String OPERATION = "Operation";

  /** Property for mmobjects using operation arrays */
  public final static String OPERATION_ARRAY = "OperationArray";

  /** Property for mmobjects using operations */
  public final static String RELATION = "Relation";

  /** Property for mmobjects using  {@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix NumberMatrices} */
  public final static String NUMBERMATRIX = "NumberMatrix";

  /** Property for mmobjects using matrices. */
  public final static String MATRIX_ENTRY = "MatrixEntry";
  
  /** Property for mmobjects using a list of entries. */
  public final static String INDEXED_ENTRY = "IndexedEntry";
  
  /** Property for mmobjects distinguishing between direction and origin vector */
  public final static String ORIGIN_VECTOR = "originVector";

  /** Property for mmobjects distinguishing between direction and origin vector */
  public final static String DIRECTION_VECTOR = "directionVector";

  /** Property for mmobjects using the editable flag */
  public final static String EDITABLE = "Editable";

	/** Property for mmobjects using intervals */
	public final static String INTERVAL_UP_BOUND_TYPE = "IntervalUpBoundType";

	/** Property for mmobjects using intervals */
	public final static String INTERVAL_UP_BOUND_VALUE = "IntervalUpBoundValue";

	/** Property for mmobjects using intervals */
	public final static String INTERVAL_LOW_BOUND_TYPE = "IntervalLowBoundType";

	/** Property for mmobjects using intervals */
	public final static String INTERVAL_LOW_BOUND_VALUE = "IntervalLowBoundValue";

  /** Property for mmobjects using intervals */
  public final static String INTERVAL = "Interval";

	/**
   * Property for mmobjects using instances of
   * {@link net.mumie.mathletfactory.math.number.MNumber}.
   */
	public final static String NUMBER = "Number";

  /** Property for mmobjects using strings */
  public final static String STRING = "String";

  /** Property for mmobjects using string matrices */
  public final static String STRING_MATRIX = "StringMatrix";

  /** Property for exercise objects using the edited flag */
  public final static String EDITED = "Edited";
  
  /** Property for mmobjects using operation tuple sets */
  public final static String SET_ENTRY = "SetEntry";
}

