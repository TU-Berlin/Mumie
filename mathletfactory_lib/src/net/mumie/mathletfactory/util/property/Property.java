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

package net.mumie.mathletfactory.util.property;

import java.awt.Color;
import java.awt.Font;
import java.util.StringTokenizer;

import javax.swing.border.Border;

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.util.Version;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;

public class Property {
	
	public final static int UNKNOWN_PROPERTY = -1;

	public final static int STRING_PROPERTY = 0;
	public final static int BOOLEAN_PROPERTY = 1;
	public final static int DOUBLE_PROPERTY = 2;
	public final static int INTEGER_PROPERTY = 3;
	public final static int VERSION_PROPERTY = 4;
	public final static int COLOR_PROPERTY = 5;
	public final static int FONT_PROPERTY = 6;
	public final static int BORDER_PROPERTY = 7;
	
	private String m_name;
	private Object m_value;
	private boolean m_readOnly = false;
	private int m_type = UNKNOWN_PROPERTY;

	/**
	 * Constructs a new Property with the given name and value.
	 * 
	 * @param name the property's name; must not be <code>null</code>
	 * @param value the property's value; must not be <code>null</code>
	 */
	public Property(String name, Object value) {
		if(name == null)
			throw new NullPointerException("Property names must not be null!");
		m_name = name;
		setValue(value);
	}
	
	/**
	 * Copy constructor.
	 */
	public Property(Property p) {
		this(p.getName(), p.getValue());
		m_readOnly = p.isReadOnly();
	}
	
	
	/**
	 * Constructs a new property by parsing the given XML node.
	 * Note that the XML node must contain valid attributes (i.e. non-null) for
	 * the name, the type and the value. 
	 * @throws XMLParsingException if the XML node is not a valid property node 
	 */
	public Property(Node xmlNode) {
		String name = XMLUtils.getAttribute(xmlNode, "name");
		String type = XMLUtils.getAttribute(xmlNode, "type");
		String value = XMLUtils.getAttribute(xmlNode, "value");
		String readOnly = XMLUtils.getAttribute(xmlNode, "readOnly");
		setName(name);
		setType(type);
		setValueFromType(value);
		setReadOnlyFlag(readOnly);
	}
	
	/**
	 * Sets the value of this property.
	 * 
	 * @param newValue the new value; must not be <code>null</code>
	 * @throws PropertyChangeException if this property is read-only
	 */
	public void setValue(Object newValue) {
		if(newValue == null)
			throw new NullPointerException("Property values must not be null! Exception in property: " + getName());
		if(isReadOnly())
			throw new PropertyChangeException(this, PropertyChangeException.VALUE_CHANGE_ATTEMPT);
		setTypeFromValue();
		m_value = newValue;
	}
	
	protected void setTypeFromValue() {
		if(m_value instanceof String) {
			m_type = STRING_PROPERTY;
		} else if(m_value instanceof Boolean) {
			m_type = BOOLEAN_PROPERTY;
		} else if(m_value instanceof Double) {
			m_type = DOUBLE_PROPERTY;
		} else if(m_value instanceof Integer) {
			m_type = INTEGER_PROPERTY;
		} else if(m_value instanceof Version) {
			m_type = VERSION_PROPERTY;
		} else if(isColorInstance(m_value)) {
			m_type = COLOR_PROPERTY;
		} else if(isFontInstance(m_value)) {
			m_type = FONT_PROPERTY;
		} else if(isBorderInstance(m_value)) {
			m_type = BORDER_PROPERTY;
		} else {
			m_type = UNKNOWN_PROPERTY;
		}
	}
	
	protected void setValueFromType(String value) {
		if(value == null)
			throw new XMLParsingException("Not a valid property node ! Missing attribute: \"value\"");
		try {
			switch(getType()) {
			case STRING_PROPERTY:
				m_value = value;
				break;
			case BOOLEAN_PROPERTY:
				m_value = Boolean.valueOf(value);
				break;
			case DOUBLE_PROPERTY:
				m_value = Double.valueOf(value);
				break;
			case INTEGER_PROPERTY:
				m_value = Integer.valueOf(value);
				break;
			case VERSION_PROPERTY:
				m_value = new Version(value);
				break;
			case COLOR_PROPERTY:
				setColorValue(value);
				break;
			case FONT_PROPERTY:
				setFontValue(value);
				break;
			case BORDER_PROPERTY:
				setBorderValue(value);
				break;
			case UNKNOWN_PROPERTY:
				m_value = value;
				break;
			}
		} catch(Exception e) {
			throw new XMLParsingException("Not a valid property value ! Wrong value: \"" + value + "\"", e);
		}
	}
	
	private void setReadOnlyFlag(String value) {
		
	}
	
	private static boolean isColorClassAvailable() {
		return isClassAvailable("java.awt.Color");
	}
	
	private static boolean isFontClassAvailable() {
		return isClassAvailable("java.awt.Font");
	}
	
	private static boolean isBorderClassAvailable() {
		return isClassAvailable("javax.swing.border.Border");
	}
	
	private static boolean isClassAvailable(String className) {
		try {
			// check if AWT class is available
			Class.forName(className);
			return true;
		} catch(Throwable t) {
			// no it is not
			return false;
		}
	}
	
	private static boolean isColorInstance(Object o) {
		if(isColorClassAvailable())
			return o instanceof Color;
		// we do not know, if it is a color
		return false;
	}
	
	private static boolean isFontInstance(Object o) {
		if(isFontClassAvailable())
			return o instanceof Font;
		// we do not know, if it is a font
		return false;
	}
	
	private static boolean isBorderInstance(Object o) {
		if(isBorderClassAvailable())
			return o instanceof Border;
		// we do not know, if it is a border
		return false;
	}
	
	private void setColorValue(String value) {
		if(isColorClassAvailable()) {
			try {
				// try 1st format: "<red>,<green>,<blue>"
				StringTokenizer st = new StringTokenizer(value, ",; ");
				int red = Integer.parseInt(st.nextToken());
				int green = Integer.parseInt(st.nextToken());
				int blue = Integer.parseInt(st.nextToken());
				m_value = new Color(red, green, blue);
			} catch(Exception e1) {
				try {
					// try 2nd format: "<hex>"
					m_value = Color.decode(value);
				} catch(Exception e2) {
					throw new XMLParsingException("Not a valid color definition ! Wrong value: \"" + value + "\"", e1);
				}
			}
		} else {
			// write raw value instead
			m_value = "#color[" + value + "]";
		}
	}
	
	/*
	 * e.g.: "Arial-BOLD-18"
	 * 
	 * The default font has the family name "Dialog", a size of 12 and a 
   * PLAIN style.
	 */
	private void setFontValue(String value) {
		if(isFontClassAvailable()) {
			try {
				m_value = Font.decode(value);
			} catch(Exception e) {
				throw new XMLParsingException("Not a valid font definition ! Wrong value: \"" + value + "\"", e);
			}
		} else {
			// write raw value instead
			m_value = "#font[" + value + "]";
		}
	}
	
	private void setBorderValue(String value) {
		if(isBorderClassAvailable()) {
			try {
				m_value = (Border) Class.forName(value).newInstance();
			} catch(Exception e) {
				throw new XMLParsingException("Not a valid border definition ! Wrong value: \"" + value + "\"", e);
			}
		} else {
			// no they aren't, write raw value instead
			m_value = "#border[" + value + "]";
		}
	}
	
	/**
	 * Returns the value of this property.
	 */
	public Object getValue() {
		return m_value;
	}
	
	/**
	 * Returns the value of this property as a primitive boolean value.
	 * 
	 * @throws ClassCastException if this property's value is not an instance of {@link Boolean}.
	 */
	public boolean getBooleanValue() {
		return ((Boolean) getValue()).booleanValue();
	}
	
	/**
	 * Returns the value of this property as an instance of {@link Version}.
	 * 
	 * @throws ClassCastException if this property's value is not an instance of {@link Boolean}.
	 */
	public Version getVersionValue() {
		return (Version) getValue();
	}
	
	private void setName(String name) {
		if(name == null)
			throw new XMLParsingException("Not a valid property node ! Missing attribute: \"name\"");
		m_name = name;
	}

	/**
	 * Returns the name of this property.
	 */
	public final String getName() {
		return m_name;
	}
	
	protected void setType(String type) {
		if(type == null)
			throw new XMLParsingException("Not a valid property node ! Missing attribute: \"type\"");
		if(type.equalsIgnoreCase("string")) {
			m_type = STRING_PROPERTY;
		} else if(type.equalsIgnoreCase("string")) {
			m_type = STRING_PROPERTY;
		} else if(type.equalsIgnoreCase("boolean")) {
			m_type = BOOLEAN_PROPERTY;
		} else if(type.equalsIgnoreCase("double")) {
			m_type = DOUBLE_PROPERTY;
		} else if(type.equalsIgnoreCase("integer")) {
			m_type = INTEGER_PROPERTY;
		} else if(type.equalsIgnoreCase("version")) {
			m_type = VERSION_PROPERTY;
		} else if(type.equalsIgnoreCase("color")) {
			m_type = COLOR_PROPERTY;
		} else if(type.equalsIgnoreCase("font")) {
			m_type = FONT_PROPERTY;
		} else if(type.equalsIgnoreCase("border")) {
			m_type = BORDER_PROPERTY;
		} else {
//			m_type = UNKNOWN_PROPERTY;
			throw new XMLParsingException("Not a valid property type ! Wrong value: \"" + type + "\"");
		}
	}
	
	/**
	 * Returns the type of this property.
	 */
	public final int getType() {
		return m_type;
	}
	
	/**
	 * Returns whether this property is read-only.
	 */
	public final boolean isReadOnly() {
		return m_readOnly;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Property) {
			Property p = (Property) o;
			return this.getName().equals(p.getName()) && this.getValue().equals(p.getValue());
		} else
			return false;
	}
	
	public Object clone() {
		return new Property(this);
	}
	
	public String toString() {
		return "[" + getName() + "=" + getValue().toString() + "]" + (isReadOnly() ? "*" : "");
	}
}
