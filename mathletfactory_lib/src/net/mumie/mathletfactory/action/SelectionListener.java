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

package net.mumie.mathletfactory.action;

import net.mumie.mathletfactory.display.noc.MMPanel;


/**
 * This interface describes a listener for selectable {@link net.mumie.mathletfactory.display.noc.MMPanel MMPanels}.
 *
 * @author gronau
 * @mm.docstatus finished
 * @see net.mumie.mathletfactory.display.noc.MMPanel#addSelectionListener(SelectionListener)
 * @see net.mumie.mathletfactory.display.noc.MMPanel#setSelectable(boolean)
 */
public interface SelectionListener {

	/**
	 * Called when the panel will be selected.
   * @return true if the panel could be correctly selected
	 */
	boolean select(MMPanel panel);

	/**
	 * Called when the panel will be deselected.
   * @return true if the panel could be correctly deselected
	 */
  boolean deselect(MMPanel panel);
}
