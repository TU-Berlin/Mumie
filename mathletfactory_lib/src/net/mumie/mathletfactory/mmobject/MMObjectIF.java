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

import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 * This interface is implemted by all MMObjects. It declares their visual, interactivity
 * and update methods defined in its specific superinterfaces and adds some object
 * properties.
 *
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public interface MMObjectIF
	extends VisualizeIF, InteractivityIF, NumberTypeDependentIF, PropertyHandlerIF {

	/**
	 * Sets the display properties for this class. All
	 * {@link net.mumie.mathletfactory.display.Drawable}s will
	 * use this properties for rendering.
	 * @see #getDisplayProperties
	 */
	public void setDisplayProperties(DisplayProperties newProperties);

	/**
	 * Returns the display properties for this class. All
	 * {@link net.mumie.mathletfactory.display.Drawable}s will
	 * use this properties for rendering.
	 * @see #setDisplayProperties
	 */
	public DisplayProperties getDisplayProperties();

	/**
	 * Returns true if all container displays of this object should be editable.
	 * @see #setEditable
	 */
	public boolean isEditable();

	/**
	 * Sets whether all container displays of this object should be editable.
	 * @see #isEditable
	 */
	public void setEditable(boolean isEditable);

	/**
	 * Returns the "Name" of the object. Labels are used by the
	 * {@link net.mumie.mathletfactory.display.Drawable} if
	 * {@link net.mumie.mathletfactory.display.DisplayProperties#setLabelDisplayed}
	 * has been set to true.
	 * @see #setLabel
	 */
	public String getLabel();

	/**
	 * Sets the "Name" of the object. Labels are used by the
	 * {@link net.mumie.mathletfactory.display.Drawable} if
	 * {@link net.mumie.mathletfactory.display.DisplayProperties#setLabelDisplayed}
	 * has been set to true.
	 * @see #getLabel
	 */
	public void setLabel(String label);

	/** The <code>listener</code> will be informed if this handler produces a special case.*/
	public void addSpecialCaseListener(SpecialCaseListener listener);

  /** Removes <code>listener</code> that was added by {@link #addSpecialCaseListener(SpecialCaseListener)}.*/
  public void removeSpecialCaseListener(SpecialCaseListener listener);

	/** Informs all registered special case listeners, that the given event has occurred. */
	public void fireSpecialCaseEvent(SpecialCaseEvent e);

}
