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

package net.mumie.mathletfactory.util.xml;


/**
 * This interface describes the exercise-proper capabilities of MM-Objects and their mathematical super classes, 
 * such as an <code>edited</code> flag or MathML serialisation.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public interface ExerciseObjectIF extends MathMLSerializable {
	
	public final static String UNEDITED_NODE_NAME = "mi";
	
	/**
	 * Marks this object to be user-edited or not, i.e.
	 * sets the internal <code>edited</code> flag to <code>edited</code>.
	 */
	public void setEdited(boolean edited);
	
	/**
	 * Returns if this object was user edited, i.e.
	 * returns the internal stored <code>edited</code> flag.
	 * If this object contains other components that might be edited,
	 * this method should return <code>true</code> if at least on of these
	 * components was edited. 
	 */
	public boolean isEdited();
	
	/**
	 * Returns the label for this exercise object.
	 */
	public String getLabel();
	
	/**
	 * Sets the label for this exercise object.
	 */
	public void setLabel(String label);

	/**
	 * Returns whether this exercise object should be hidden or not. Default is <code>false</code>.
	 */
	public boolean isHidden();
	
	/**
	 * Sets whether this exercise object should be hidden or not. Default is <code>false</code>.
	 */
	public void setHidden(boolean hidden);

	/**
	 * Returns the underlying number class of this exercise object.
	 */
	public Class getNumberClass();
}
