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
import java.util.Vector;

import javax.swing.SwingConstants;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.layout.SimpleInsets;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

public abstract class AbstractView implements ViewIF {

	private final static MumieLogger LOGGER = MumieLogger.getLogger(AbstractView.class);
	private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline");

	protected SimpleInsets m_margin = new SimpleInsets(0);
	
	private Vector m_children = new Vector();

	private PropertyMapIF m_attributes = new DefaultPropertyMap();
	
	private Dimension m_maximumSize = new Dimension(-1, -1);

	private Dimension m_size = new Dimension(-1, -1);

  /**
   * Adds a view to the internal list of child views.
   */
	public ViewIF addChild(ViewIF child) {
		m_children.add(child);
		return child;
	}
	
  /**
   * Returns the font property of this view.
   */
	public Font getFont() {
		// this field is only used for caching the current font
		Font f = (Font) getAttribute(FONT_ATTRIBUTE);
		if(f == null) {
			// construct current font from internal fields
			Font defaultFont = MumieTheme.getTextFont();
			String fontName = (String) getAttribute(FONT_NAME_ATTRIBUTE, defaultFont.getName());
			int fontStyle = ((Integer) getAttribute(FONT_STYLE_ATTRIBUTE, new Integer(defaultFont.getStyle()))).intValue();
			int fontSize = ((Integer) getAttribute(FONT_SIZE_ATTRIBUTE, new Integer(defaultFont.getSize()))).intValue();
			int fontSizeChange = ((Integer) getAttribute(FONT_SIZE_CHANGE_ATTRIBUTE, new Integer(0))).intValue();
			f = new Font(fontName, fontStyle, (fontSize + fontSizeChange));
			// cache the newly constructed font
			setFont(f);
		}
		return f;
	}
	
	/**
	 * Caches the given font.
	 */
	private void setFont(Font f) {
		m_attributes.setProperty(FONT_ATTRIBUTE, f);
	}
	
  /**
   * Returns the foreground property of this view.
   */
	public Color getForeground() {
		return (Color) getAttribute(FOREGROUND_ATTRIBUTE, MumieTheme.getTextColor());
	}
	
	/**
	 * Returns the horizontal alignment of this view as a SWING constant.
	 * 
	 * @see SwingConstants#LEFT
	 * @see SwingConstants#CENTER
	 * @see SwingConstants#RIGHT
	 */
	public int getHorizontalAlignment() {
		return ((Integer) getAttribute(HORIZONTAL_ALIGNMENT_ATTRIBUTE, new Integer(SwingConstants.CENTER))).intValue();
	}
	
  /**
   * Updates this view.
   * This method is intended to perform custom re-rendering after change of attributes in subclassing views.
   * The default implementation does nothing.
   */
	public void updateView() {
		// empty implementation
	}
	
  /**
   * Sets the named attribute to the given value.
   * Existing attributes of the same name will be overwritten.
   * 
   * @param name an attribute name as defined in {@link ViewIF}
   * @param value the specific attribute value (e.g. {@link Integer}, {@link Color}, {@link Font}, ...)
   * @see ViewIF
   */
	public void setAttribute(String name, Object value) {
		// handle global font attribute differently
		if(value != null && name.equals(FONT_ATTRIBUTE)) {
			setFieldsFromFont((Font) value);
		} else {
			Object oldValue = m_attributes.setProperty(name, value);
//			System.out.println("setting attr " + name + "=" + value + ", overwrites " + oldValue);
		}
		// propagate attributes to children
		for(int i = 0; i < getChildCount(); i++)
			getChild(i).setAttribute(name, value);
		// view may need an update after change of attributes
		updateView();
	}
	
  /**
   * Sets all attributes of the given map to this view and all of its descendants.
   * Existing attributes of the same name will be overwritten.
   * @see #setAttribute(String, Object)
   */
	public void setAttributes(PropertyMapIF map) {
		if(map != null) {
			m_attributes.copyPropertiesFrom(map);
			// handle global font attribute differently
			if(map.containsProperty(FONT_ATTRIBUTE))
				setFieldsFromFont((Font) map.getProperty(FONT_ATTRIBUTE));
			// propagate attributes to children
			for(int i = 0; i < getChildCount(); i++)
				getChild(i).setAttributes(map);
			// view may need an update after change of attributes
			updateView();
		}
	}
	
  /**
   * Returns a reference to the internal property map holding all attributes.
   */
	public PropertyMapIF getAttributes() {
		return m_attributes;
	}
	
	/**
	 * Sets the internal fields from the given font and resets the old cached font.
	 */
	private void setFieldsFromFont(Font f) {
		if(f != null) {
			setAttribute(FONT_NAME_ATTRIBUTE, f.getName());
			setAttribute(FONT_STYLE_ATTRIBUTE, new Integer(f.getStyle()));
			setAttribute(FONT_SIZE_ATTRIBUTE, new Integer(f.getSize()));
		}
		setFont(null);
	}
	
  /**
   * Returns the named attribute of this view.
   * May return <code>null</code>.
   */
	public Object getAttribute(String name) {
		return getAttribute(name, null);
	}
		
  /**
   * Returns the named attribute of this view or <code>defaultValue</code> if none exists.
   */
	public Object getAttribute(String name, Object defaultValue) {
		return m_attributes.getProperty(name, defaultValue);
	}
	
  /**
   * Returns the baseline of this view.
   */
	public abstract double getBaseline(Graphics context);
	
  /**
   * Returns the n-th child view of this view.
   */
	public ViewIF getChild(int index) {
		return (ViewIF) m_children.get(index);
	}
	
  /**
   * Returns the number of child views of this view.
   */
	public int getChildCount() {
		return m_children.size();
	}

  /**
   * Paints this view onto the given graphics context with the defined coordinates and size.
   */
	public void paint(Graphics g, int x, int y, int width, int height) {
		if(LOGGER.isActiveCategory(DRAW_OUTLINE))
			g.drawRect(x, y, width-1, height-1);
	}

  /**
   * Returns the insets (i.e. margin) of this view.
   */
	public Insets getInsets() {
		return m_margin;
	}
	
	public void setMaximumSize(Dimension size) {
		m_maximumSize = size;
	}
	
	public Dimension getMaximumSize() {
		return m_maximumSize;
	}
	
	public void setSize(Dimension size) {
		m_size.setSize(size);
	}
	
	public Dimension getSize() {
		return m_size;
	}
}
