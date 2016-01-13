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

package net.mumie.mathletfactory.action.updater;

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * This updater ensures the updating of a parameter in a function.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class ParameterInFunctionUpdater extends MMUpdater {

	/** Field holding the identifier. */
	private final String m_identifier;
	
	/**
	 * Constructs a new parameter updater for the given function, parameter name and value.
	 * Whenever the given value instance changes, the function's operation will be adjusted.
	 * Note: both the function and the value must be MM-objects.
	 * 
	 * @param function a MM-function
	 * @param identifier the identifier
	 * @param value a MM-number
	 * @throws ClassCastException if the function or the value or not MM-objects
	 */
	public ParameterInFunctionUpdater(UsesOperationIF function, String identifier, MNumber value) {
		super((MMObjectIF) function, new MMObjectIF[] { (MMObjectIF) value });
		m_identifier = identifier;
		function.getOperation().setParameter(m_identifier);
		userDefinedUpdate();
	}
	
	public void userDefinedUpdate() {
		UsesOperationIF function = (UsesOperationIF) getSlave();
		Object value = getParent(0);
		Operation operation = function.getOperation();
		assignValue(operation, value);
		function.setOperation(operation);
	}
	
	/**
	 * Assigns the given value for the registered parameter.
	 */
	private void assignValue(Operation operation, Object value) {
		if(value instanceof MNumber)
			operation.assignValue(m_identifier, (MNumber) value);
	}
}
