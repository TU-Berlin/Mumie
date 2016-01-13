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

package net.mumie.mathletfactory.mmobject.util;

/**
 * This class helps to describe an TupleSet-entry.
 * 
 * @author heimann
 * @mm.docstatus finished
 */
public 	class TupleSetEntry {
	
	private Object m_entry;
	private int m_type, m_index;
	public final static int VECTOR_ENTRY   = 0;
	public final static int FACTOR   = 1;
	public final static int VARIABLE = 2;
	public final static int ADDING_OR_REMOVING_COMPONENT = -1;
	public final static int REMOVING_LEFT_COMPONENT = 1;
	public final static int ADDING_LEFT_COMPONENT = 2;
	public final static int REMOVING_RIGHT_COMPONENT = 3;
	public final static int ADDING_RIGHT_COMPONENT = 4;
	public final static int ENABLING_FACTORS = 5;
	public final static int DISABLING_FACTORS = 6;
	
	/**
	 * Creates a new set panel entry with type ADDING_OR_REMOVING.
	 */
	public TupleSetEntry() {
		this(null, ADDING_OR_REMOVING_COMPONENT, -1);
	}
	
	/**
	 * Creates a new set panel entry with given "value", type and position.
	 */
	public TupleSetEntry(Object value, int type, int index) {
		m_entry = value;
		m_type = type;
		m_index = index;
	}
	
	/**
	 * Returns the value, i.e. the entry itself.
	 */
	public Object getValue() {
		return m_entry;
	}
	
	/**
	 * Returns the type.
	 */
	public int getType() {
		return m_type;
	}
	
	/**
	 * Returns the index.
	 */
	public int getIndex() {
		return m_index;
	}
	
	public String toString() {
		return getValue() + " type = " + getType() + ", index = " + getIndex();
	}
}
