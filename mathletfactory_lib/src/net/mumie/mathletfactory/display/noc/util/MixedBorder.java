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

package net.mumie.mathletfactory.display.noc.util;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * This class wraps a {@link CompoundBorder} but allows to set the outside and inside border
 * after creation time.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MixedBorder extends CompoundBorder {
	
	public MixedBorder() {
		super();
	}

  public MixedBorder(Border outsideBorder, Border insideBorder) {
  	super(outsideBorder, insideBorder);
  }
	/**
	 * Sets the outside border.
	 */
	public void setOutsideBorder(Border b) {
		this.outsideBorder = b;
	}
	
	/**
	 * Sets the inside border.
	 */
	public void setInsideBorder(Border b) {
		this.insideBorder = b;
	}
}
