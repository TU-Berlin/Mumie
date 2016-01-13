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
 * This class stores an object along with an index for describing an entry inside an indexed set of entries.
 * Instances of this class can be used to define values for {@link java.beans.PropertyChangeEvent PropertyChangeEvents}
 * inside drawables and mm-objects.
 *  
 *  It is up to the implementor/list to define where to start with the first index (0 or 1).
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public 	class IndexedEntry {
	
	private Object m_entry;
	private int m_index;
	private String m_name;
	
	/**
	 * Creates a new entry with given "value", index and empty name.
	 */
	public IndexedEntry(Object value, int index) {
		this(value, index, null);
	}
	
	/**
	 * Creates a new entry with given "value", index and name.
	 */
	public IndexedEntry(Object value, int index, String name) {
		m_entry = value;
		m_index = index;
	}
	
	/**
	 * Returns the name of the entry. May be <code>null</code>.
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Sets the name of the entry. May be <code>null</code>.
	 */
	public void setName(String name) {
		m_name = name;
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
	 * Returns the index of the entry.
	 */
	public int getIndex() {
		return m_index;
	}
		
	public String toString() {
		return "[" + getValue() + " at position " + getIndex() + "]";
	}
}
