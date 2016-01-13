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

package net.mumie.mathletfactory.display.noc.test;

import java.awt.Color;
import java.awt.Font;

public abstract class TestSetting {

	// the setting's value
	protected Object m_value;
	
	/** applies the setting to mmobject or drawable (both inside GUITestPanel) */
	abstract void applyValue(GUITestPanel testPanel);
	
	/** loads the setting from mmobject or drawable (both inside GUITestPanel) */
	abstract void loadValue(GUITestPanel testPanel);
	
	TestSetting(Object value) {
		m_value = value;
	}
	
	boolean isActive(GUITestPanel testPanel) {
		return true;
	}
	
	Object getValue() {
		return m_value;
	}
	
	void setValue(Object value) {
		m_value = value;
	}

	void setValue(boolean b) {
		m_value = new Boolean(b);
	}
	
	void setValue(int i) {
		m_value = new Integer(i);
	}
	
	boolean getBooleanValue() {
//		if(getValue() instanceof Boolean)
		return ((Boolean) getValue()).booleanValue();
	}
	
	String getStringValue() {
		return (String) getValue();
	}
	
	int getIntegerValue() {
		return ((Integer) getValue()).intValue();
	}
	
	Color getColorValue() {
//	if(getValue() instanceof Color)
	return (Color) getValue();
}
	Font getFontValue() {
		return (Font) getValue();
	}
}
