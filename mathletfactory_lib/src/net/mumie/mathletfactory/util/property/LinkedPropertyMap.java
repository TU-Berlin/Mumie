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

/**
 * This class can be used to store arbitrary properties (i.e. values which can be adressed via name keys)
 * in a map. Every property can have its own default value whereas all data is stored in the same map as
 * a <code>java.lang.Object</code>.
 * The internal name of default properties is constructed by the real property name and the suffixe <i>.default</i>.
 * In the case where a property was not set (i.e. no value was stored under the property name), the
 * default value will be retrieved (unless it was set).
 * There are several getter-methods for retrieving values in the correct format from the internal map, where
 * it is almost always necessary to define a default return value if neither an explicit value nor its 
 * default value were stored:
 * <ul>
 * <li>{@link #getProperty(String)}</li>
 * <li>{@link #getProperty(String, Object)}</li>
 * <li>{@link #getBooleanProperty(String, boolean)}</li>
 * <li>{@link #getDoubleProperty(String, double)}</li>
 * <li>{@link #getColorProperty(String, Color)}</li>
 * <li>{@link #getFontProperty(String, Font)}</li>
 * </ul>
 * <br>
 * Default values can only be retrieved explicitly from the map by appending the <i>.default</i> suffixe
 * to the real property name in one of the prior getter-methods.
 * <br>
 * <br>
 * Values can be stored by one of the following getter-methods, whereas each one returns the old
 * property value which will be overwritten (unless the second method is not used):
 * <ul>
 * <li>{@link #setProperty(String, Object)}</li>
 * <li>{@link #setProperty(String, Object, boolean)}</li>
 * <li>{@link #setProperty(String, boolean)}</li>
 * <li>{@link #setProperty(String, double)}</li>
 * </ul>
 * <br>
 * Note that the last 2 methods don't return a primitive type but their corresponding wrapper classes.
 * <br>
 * Default values can be stored by appending the <i>.default</i> suffixe to the real property name
 * or by using one of the appropriate setter-methods:
 * <ul>
 * <li>{@link #setDefaultProperty(String, Object)}</li>
 * <li>{@link #setDefaultProperty(String, boolean)}</li>
 * <li>{@link #setDefaultProperty(String, double)}</li>
 * </ul>
 * <br>
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class LinkedPropertyMap implements PropertyMapIF {
	
	/** Reference to master map. */
	private PropertyMapIF m_masterMap;
	
	/**
	 * Creates a new {@link LinkedPropertyMap} instance by referencing the internal master map
	 * to the argument. Note that no values are copied.
	 * 
	 * @param map an instance of {@link LinkedPropertyMap} or <code>null</code>
	 */
	public LinkedPropertyMap(PropertyMapIF map) {
		if(map == null)
			throw new NullPointerException("Referencing map must not be null!");
		m_masterMap = map;
	}
	
	/**
	 * Returns the named property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>null</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @return the property's (default) value or <code>null</code>
	 */
	public Object getProperty(String name) {
		return getProperty(name, null);
	}
	
//	public Property getPropertyImpl(String name) {
//		return getMasterMap().getPropertyImpl(name);
//	}
	
	/**
	 * Returns the named property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>defaultValue</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @param defaultValue default value if neither an explicit nor a default value exist; may be <code>null</code>
	 * @return the property's (default) value or <code>defaultValue</code>
	 */
	public Object getProperty(String name, Object defaultValue) {
		return getMasterMap().getProperty(name, defaultValue);
	}
	
	public boolean containsProperty(String name) {
		return getMasterMap().containsProperty(name);
	}
	
	public boolean containsDefaultProperty(String name) {
		return getMasterMap().containsDefaultProperty(name);
	}
		
	public Object setDefaultProperty(String name, Object value) {
		return getMasterMap().setDefaultProperty(name, value);
	}
	
	public Object setProperty(String name, Object value) {
		return getMasterMap().setProperty(name, value);
	}
	
	/**
	 * Returns the named boolean property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>defaultValue</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @param defaultValue default value if neither an explicit nor a default value exist
	 * @return the property's (default) value or <code>defaultValue</code>
	 */
	public boolean getBooleanProperty(String name, boolean defaultValue) {
		return ((Boolean) getProperty(name, new Boolean(defaultValue))).booleanValue();
	}

	/**
	 * Returns the named double property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>defaultValue</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @param defaultValue default value if neither an explicit nor a default value exist
	 * @return the property's (default) value or <code>defaultValue</code>
	 */
	public double getDoubleProperty(String name, double defaultValue) {
		return ((Double) getProperty(name, new Double(defaultValue))).doubleValue();
	}
	
	/**
	 * Stores the named boolean property in the internal map and returns its previous value.
	 * Already existing values will be overwritten.
	 * 
	 * @param name name of a property
	 * @param value property value
	 * @return the property's previous value as a {@link Boolean} or <code>null</code>
	 */
	public Boolean setProperty(String name, boolean value) {
		return (Boolean) setProperty(name, new Boolean(value));
	}
	
	/**
	 * Stores the named double property in the internal map and returns its previous value.
	 * Already existing values will be overwritten.
	 * 
	 * @param name name of a property
	 * @param value property value
	 * @return the property's previous value as a {@link Double} or <code>null</code>
	 */
	public Double setProperty(String name, double value) {
		return (Double) setProperty(name, new Double(value));
	}
	
	public boolean equals( Object obj ) {
		return getMasterMap().equals(obj);
	}

	public void clear() {
		getMasterMap().clear();
	}
	
	/* Returns null */
	public PropertyMapIF copy() {
		return null;
	}
	
	public void copyPropertiesInto(PropertyMapIF map) {
		map.copyPropertiesFrom(this);
	}
	
	public void copyPropertiesFrom(PropertyMapIF map) {
		getMasterMap().copyPropertiesFrom(map);
	}
	
	public String toString() {
		return getMasterMap().toString();
	}
	
	protected PropertyMapIF getMasterMap() {
		return m_masterMap;
	}
	
	public int getPropertiesCount() {
		return getMasterMap().getPropertiesCount();
	}
	
	public boolean isEmpty() {
		return getMasterMap().isEmpty();
	}
	
	public Property[] getProperties() {
		return getMasterMap().getProperties();
	}
}
