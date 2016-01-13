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
 * This class helps to describe an entry from inside a matrix with its value and position.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public 	class MatrixEntry {
	
	private Object m_entry;
	private int m_row, m_col;
	
	/**
	 * Creates a new matrix entry with given "value" and position.
	 */
	public MatrixEntry(Object value, int row, int col) {
		m_entry = value;
		m_row = row;
		m_col = col;
	}
	
	/**
	 * Returns the value, i.e. the entry itself.
	 */
	public Object getValue() {
		return m_entry;
	}
	
	/**
	 * Sets the value, i.e. the entry itself.
	 */
	public void setValue(Object value) {
		m_entry = value;
	}
	
	/**
	 * Returns the position in row direction.
	 */
	public int getRowPosition() {
		return m_row;
	}
	
	/**
	 * Returns the position in column direction.
	 */
	public int getColumnPosition() {
		return m_col;
	}
	
	public String toString() {
		return getValue() + " at [" + getRowPosition() + "," + getColumnPosition() + "]";
	}
}
