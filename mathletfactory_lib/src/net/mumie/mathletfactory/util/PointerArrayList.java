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

package net.mumie.mathletfactory.util;

import java.util.ArrayList;

/**
 * This class overwrites the <tt>indexOf</tt> methods of <tt>ArrayList</tt>, which
 * uses the <tt>equals</tt> methods to check if an object is contained in this list.
 * This provoques an <tt>IllegalArgumentException</tt> in <tt>ActionManager</tt> if 
 * 2 objects have different number classes. 
 * Moreover it fails if both objects are mathematically identical.
 * Last, a pointer comparision is native process and therefore much more faster than 
 * a test for equality (which is a recursive process and may be slow for e.g. matrices).
 * 
 * @author gronau
 * @mm.docstatus finished
 * 
 * @see java.util.ArrayList
 * @see net.mumie.mathletfactory.action.ActionManager
 */
public class PointerArrayList extends ArrayList {
	
  /**
   * Searches for the first occurence of the given argument, testing 
   * for pointer equality (not using the <tt>equals</tt> method).
   * Returns <code>-1</code> if <code>obj</code> is null.
   */
	public int indexOf(Object obj) {
		if(obj == null)
			return -1;
		for(int i = 0; i < size(); i++) {
			if(get(i) == obj)
				return i;
		}
		return -1;
	}
	
  /**
   * Searches for the last occurence of the given argument, testing 
   * for pointer equality (not using the <tt>equals</tt> method).
   * Returns <code>-1</code> if <code>obj</code> is null.
   */
	public int lastIndexOf(Object obj) {
		if(obj == null)
			return -1;
		for(int i = size() - 1; i >= 0; i--) {
			if(get(i) == obj)
				return i;
		}
		return -1;
	}
}
