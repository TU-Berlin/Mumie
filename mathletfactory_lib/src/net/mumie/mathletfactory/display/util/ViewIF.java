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

package net.mumie.mathletfactory.display.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import net.mumie.mathletfactory.util.property.PropertyMapIF;

public interface ViewIF {
	
	public final static String FONT_ATTRIBUTE = "font";

	public final static String FONT_NAME_ATTRIBUTE = "font-name";

	public final static String FONT_STYLE_ATTRIBUTE = "font-style";

	public final static String FONT_SIZE_ATTRIBUTE = "font-size";

	public final static String FONT_SIZE_CHANGE_ATTRIBUTE = "font-size-change";

	public final static String FOREGROUND_ATTRIBUTE = "foreground";

	public final static String BACKGROUND_ATTRIBUTE = "background";

	public final static String HORIZONTAL_ALIGNMENT_ATTRIBUTE = "horizontal-alignment";

	public final static String VERTICAL_ALIGNMENT_ATTRIBUTE = "vertical-alignment";

	ViewIF addChild(ViewIF child);
	
	ViewIF getChild(int index);
	
	int getChildCount();
	
	void paint(Graphics g, int x, int y, int width, int height);
	
	double getBaseline(Graphics context);
	
	void setAttributes(PropertyMapIF map);
	
	PropertyMapIF getAttributes();
	
	void setAttribute(String name, Object value);
	
	Object getAttribute(String name);
	
	Object getAttribute(String name, Object defaultValue);
	
	Insets getInsets();
	
	ViewIF copy();
	
	Font getFont();
	
	Color getForeground();
	
	Dimension getPreferredSize(Graphics context);
	
	void setSize(Dimension size);
	
	Dimension getSize();
	
	void setMaximumSize(Dimension size);
	
	Dimension getMaximumSize();
}
