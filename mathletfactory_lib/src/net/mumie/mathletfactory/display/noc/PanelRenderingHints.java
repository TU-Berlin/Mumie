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

package net.mumie.mathletfactory.display.noc;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.ScreenTypeRenderingHints;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

public class PanelRenderingHints extends ScreenTypeRenderingHints {

	/** Base path for all {@link PanelRenderingHints} properties. */
	public final static String DP_BASE_PATH = "display.noc.";
	
	//
	// property names
	//

	/** 
	 * Name of the <i>is label visible</i> property.
	 * @see #isLabelDisplayed()
	 */
	public final static String LABEL_DISPLAYED_PROPERTY = DP_BASE_PATH + "labelDisplayed";
	
	/** 
	 * Name of the <i>append equal sign</i> property.
	 * @see #isAppendEqualSign()
	 */
	public final static String APPEND_EQUAL_SIGN_PROPERTY = DP_BASE_PATH + "appendEqualSign";
	
	public PanelRenderingHints(PropertyMapIF properties) {
		super(properties);
		
	}
	
	public void initialize() {
		
	}
	
	public void setLabelDisplayed(boolean displayed) {
		setProperty(LABEL_DISPLAYED_PROPERTY, new Boolean(displayed));
	}
	
	public boolean isLabelDisplayed() {
		Object value = this.getProperty(LABEL_DISPLAYED_PROPERTY);
		if ( value == null ) // read commom property instead
			value = this.getProperty(DisplayProperties.LABEL_DISPLAYED_PROPERTY);
		if ( value == null ) // read default property instead
			value = DisplayProperties.DEFAULT.getProperty(DisplayProperties.LABEL_DISPLAYED_PROPERTY);
		return (( Boolean ) value).booleanValue();
	}
	
	public void setAppendEqualSign(boolean append) {
		setProperty(APPEND_EQUAL_SIGN_PROPERTY, new Boolean(append));
	}
	
	public boolean isAppendEqualSign() {
		return getBooleanProperty(APPEND_EQUAL_SIGN_PROPERTY, false);
	}
}
