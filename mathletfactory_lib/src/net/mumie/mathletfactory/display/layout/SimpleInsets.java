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

package net.mumie.mathletfactory.display.layout;

import java.awt.Dimension;
import java.awt.Insets;

/**
 * This class allows to easily create and mix the insets of different borders and
 * to calculate the inner and outer dimension of the component for which these insets apply.
 * The size of the resulting insets can dynamically be updated via the {@link #update()} 
 * method.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class SimpleInsets extends Insets {
	
	/* Fields holding 2 children insets. */
	private Insets m_i1, m_i2;
	
	/**
	 * Creates a new instance with empty insets.
	 */
	public SimpleInsets() {
		this(new Insets(0, 0, 0, 0));
	}
	/**
	 * Creates a new instance with the given insets.
	 */
	public SimpleInsets(Insets i1) {
		super(0,0,0,0);//i1.top, i1.left, i1.bottom, i1.right);
		m_i1 = i1;
		update();
	}
	
	/**
	 * Creates a new instance with the sum of the 2 given insets as own insets.
	 * Note that this constructor can be used to construct a hierarchie using more
	 * <code>SimpleInsets</code>.
	 */
	public SimpleInsets(Insets i1, Insets i2) {
		super(0,0,0,0);//i1.top+i2.top, i1.left+i2.left, i1.bottom+i2.bottom, i1.right+i2.right);
		m_i1 = i1;
		m_i2 = i2;
		update();
	}

	/**
	 * Creates a new instance with the sum of the 3 given insets as own insets.
	 * Note that this constructor can be used to construct a hierarchie using more
	 * <code>SimpleInsets</code>.
	 */
	public SimpleInsets(Insets i1, Insets i2, Insets i3) {
		this(new SimpleInsets(i1, i2), i3);
	}

	/**
	 * Creates a new instance with the given equal size for top, bottom, left and right insets.
	 */
	public SimpleInsets(int size) {
		super(size, size, size, size);
	}
	
	/**
	 * Updates recursivly the size of these insets by adding the size of the
	 * children's insets and calling <code>update()</code> on instances of this class.
	 */
	public void update() {
		if(m_i1 == null && m_i2 == null)
			return;
		setInsets(0);
		if(m_i1 != null) {
			if(m_i1 instanceof SimpleInsets)
				((SimpleInsets) m_i1).update();
			addToInsets(m_i1);
		}
		if(m_i2 != null) {
			if(m_i2 instanceof SimpleInsets)
				((SimpleInsets) m_i2).update();
			addToInsets(m_i2);
		}
	}
	
	/*
	 * Adds the given values to top, bottom, left and right insets.
	 */
	private void addToInsets(int top, int left, int bottom, int right) {
		this.top += top;
		this.left += left;
		this.bottom += bottom;
		this.right += right;
	}
	
	/*
	 * Adds the given insets to this insets.
	 */
	private void addToInsets(Insets i) {
		addToInsets(i.top, i.left, i.bottom, i.right);
	}
	
	/**
	 * Sets top, bottom, left and right insets to the given values.
	 */
	public void setInsets(int top, int left, int bottom, int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	/**
	 * Sets top, bottom, left and right insets equally to <code>size</code>.
	 */
	public void setInsets(int size) {
		setInsets(size, size, size, size);
	}

	/**
	 * Returns the inner dimension of the component for which these insets apply.
	 * @param d size of the component
	 */
	public Dimension getInsideDim(Dimension d) {
		Dimension result = new Dimension(d);
		result.height -= (top + bottom);
		result.width -= (left + right);
		return result;
	}
	
	/**
	 * Returns the outer dimension of the component for which these insets apply.
	 * @param d size of the component
	 */
	public Dimension getOutsideDim(Dimension d) {
		Dimension result = new Dimension(d);
		result.height += (top + bottom);
		result.width += (left + right);
		return result;
	}
}
