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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


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
public class DefaultPropertyMap implements PropertyMapIF {
	
	/** Name suffix for default properties. */
	public final static String DEFAULT_PROPERTY_SUFFIX = ".default";
	
	/** Map holding all properties. */
	private final Map m_map;
	
	/**
	 * Constructs a new empty property map which does not preserve the property order.
	 */
	public DefaultPropertyMap() {
		this(null, false);
	}
	
	/**
	 * Constructs a new empty property map which preserves the property order on activated flag ("FIFO map").
	 * 
	 * @param preserveOrdering enables a FIFO ordering of the contained properties
	 */
	public DefaultPropertyMap(boolean preserveOrdering) {
		this(null, preserveOrdering);
	}
	
	/**
	 * Creates a new {@link DefaultPropertyMap} instance with the same property bindings as the argument
	 * and which does not preserve the property order.
	 * 
	 * @param map an instance of {@link PropertyMapIF} or <code>null</code>
	 */
	public DefaultPropertyMap(PropertyMapIF map) {
		this(map, false);
	}
	
	/**
	 * Creates a new {@link DefaultPropertyMap} instance with the same property bindings as the argument
	 * and which preserves the property order on activated flag ("FIFO map").
	 * 
	 * @param map an instance of {@link PropertyMapIF} or <code>null</code>
	 * @param preserveOrdering enables a FIFO ordering of the contained properties
	 */
	public DefaultPropertyMap(PropertyMapIF map, boolean preserveOrdering) {
		m_map = (preserveOrdering ? new LinkedHashMap() : new HashMap());
		if(map != null)
			copyPropertiesFrom(map);
	}
	
	public Object getProperty(String name) {
		return getProperty(name, null);
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
	 * Returns the named property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>defaultValue</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @param defaultValue default value if neither an explicit nor a default value exist; may be <code>null</code>
	 * @return the property's (default) value or <code>defaultValue</code>
	 */
	public Object getProperty(String name, Object defaultValue) {
		if(name == null)
			throw new NullPointerException("Property name must not be null !");
		// handle default properties differently
		if( isDefaultPropertyName(name) )
			return getDefaultPropertyImpl(getExplicitPropertyName(name));
		// search for explicit prop
		Property p = getPropertyImpl(name);
		if( p == null )
			// search for default prop
			p = getDefaultPropertyImpl(name);
		if( p == null )
			return defaultValue;
		// return property's value
		return p.getValue();
	}
	
	protected Property getPropertyImpl(String name) {
		return (Property) getRealMap().get(name);
	}
		
	protected Property getDefaultPropertyImpl(String name) {
		return (Property) getRealMap().get(getDefaultPropertyName(name));
	}
	
	private String getExplicitPropertyName(String name) {
		return isDefaultPropertyName(name) ? name.substring(0, name.indexOf(DEFAULT_PROPERTY_SUFFIX)) : name;
	}
	
	private String getDefaultPropertyName(String name) {
		return isDefaultPropertyName(name) ? name : name + DEFAULT_PROPERTY_SUFFIX;
	}
	
	private boolean isDefaultPropertyName(String name) {
		return name.endsWith(DEFAULT_PROPERTY_SUFFIX);
	}
	
	private Property createProperty(String name, Property value) {
		return new Property(name, value.getValue());
	}
	
	public boolean containsProperty(String name) {
		return getRealMap().containsKey(name);
	}
	
	public boolean containsDefaultProperty(String name) {
		return getRealMap().containsKey(getDefaultPropertyName(name));
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
	
	/**
	 * Stores the default boolean value of the named property in the internal map and returns its previous value.
	 * Already existing values will be overwritten.
	 * 
	 * @param name name of a property
	 * @param value property value
	 * @return the property's previous value as a {@link Boolean} or <code>null</code>
	 */
	public Object setDefaultProperty(String name, boolean value) {
		return setDefaultProperty(name, new Boolean(value));
	}
	
	/**
	 * Stores the default double value of the named property in the internal map and returns its previous value.
	 * Already existing values will be overwritten.
	 * 
	 * @param name name of a property
	 * @param value property value
	 * @return the property's previous value as a {@link Double} or <code>null</code>
	 */
	public Object setDefaultProperty(String name, double value) {
		return setDefaultProperty(name, new Double(value));
	}
	
	public Object setProperty(String name, Object value) {
		// remove property if value is null
		if(value == null)
			return removeProperty(name);
		// value != null; name may be null -> check in property constructor
		return setProperty(new Property(name, value));
	}
	
	public Object setDefaultProperty(String name, Object value) {
		String defName = getDefaultPropertyName(name);
		// remove property if value is null
		if(value == null)
			return removeProperty(defName);
		// value != null; name may be null -> check in property constructor
		return setDefaultProperty(new Property(name, value));
	}
	
	public Object setProperty(Property property) {
		if(property == null)
			throw new NullPointerException("Property must not be null !");
		// handle default properties differently
		if( isDefaultPropertyName(property.getName()) )
			return setDefaultProperty(property);
		Property p = setPropertyImpl(property);
		if(p == null)
			return null;
		return p.getValue();
	}
	
	public Object setDefaultProperty(Property property) {
		if(property == null)
			throw new NullPointerException("Property must not be null !");
		Property p = createProperty(getExplicitPropertyName(property.getName()), property);
		p = setDefaultPropertyImpl(p);
		if(p == null)
			return null;
		return p.getValue();
	}
	
	protected Property setPropertyImpl(Property p) {
		Property oldP = (Property) getRealMap().put(p.getName(), p);
		// throw exception if old property was read-only and both properties have different values
		if(oldP != null && oldP.isReadOnly() && oldP.getValue().equals(p.getValue()) == false)
			throw new PropertyChangeException(p, PropertyChangeException.VALUE_CHANGE_ATTEMPT);
		return oldP;
	}

	protected Property setDefaultPropertyImpl(Property p) {
		String defName = getDefaultPropertyName(p.getName());
		Property defProp = new Property(defName, p.getValue());
		Property oldP = (Property) getRealMap().put(defName, defProp);
		// throw exception if old property was read-only and both properties have different values
		if(oldP != null && oldP.isReadOnly() && oldP.getValue().equals(p.getValue()) == false)
			throw new PropertyChangeException(p, PropertyChangeException.VALUE_CHANGE_ATTEMPT);
		return oldP;
	}

	public Object removeProperty(String name) {
		if(name == null)
			throw new NullPointerException("Property name must not be null !");
		Property p = (Property) getRealMap().remove(name);
		if(p != null && p.isReadOnly())
			throw new PropertyChangeException(p, PropertyChangeException.PROPERTY_REMOVE_ATTEMPT);
		if(p == null)
			return null;
		return p.getValue();
	}
	
	public boolean equals( Object obj ) {
		boolean equals = false;
		if ( obj instanceof PropertyMapIF ) {
			PropertyMapIF attrMap = ( PropertyMapIF ) obj;
			equals = attrMap.getPropertiesCount() == getRealMap().size();
			for ( Iterator it = this.getRealMap().keySet().iterator(); equals && it.hasNext(); ) {
				String key = (String) it.next();
				equals &= attrMap.containsProperty( key );
				equals &= attrMap.getProperty( key ).equals( this.getProperty( key ) );
			}
		}
		return equals;
	}

	public void clear() {
		getRealMap().clear();
	}
	
	public PropertyMapIF copy() {
		DefaultPropertyMap map = new DefaultPropertyMap();
		copyPropertiesInto(map);
		return map;
	}
	
	public Object clone() {
		return copy();
	}
	
	public void copyPropertiesInto(PropertyMapIF map) {
		map.copyPropertiesFrom(this);
	}
	
	public void copyPropertiesFrom(PropertyMapIF map) {
		Property[] props = map.getProperties();
		for (int i = 0; i < props.length; i++) {
			setProperty(props[i]);
		}
	}
	
	public void copyPropertiesFrom(Map map) {
		Set keys = map.keySet();
		for (Iterator i= keys.iterator(); i.hasNext(); ) {
			String key = i.next().toString(); // avoid ClassCastExc.'s
			Object o = map.get(key);
			if(o != null) // Maps can have null-values for bindings
				setProperty(key, o);
		}
	}
	
	public int getPropertiesCount() {
		return getRealMap().size();
	}
	
	public boolean isEmpty() {
		return getRealMap().isEmpty();
	}
	
	public Property[] getProperties() {
		Property[] result = new Property[getRealMap().size()];
		int i = 0;
		for ( Iterator it = getRealMap().values().iterator(); it.hasNext(); ) {
			result[i++] = (Property) it.next();
		}
		return result;
	}

	public String toString() {
		String result = " [";
		for ( Iterator it = this.getRealMap().keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			result += this.getRealMap().get( key );
			if ( it.hasNext() ) {
				result += ", ";
			}
		}
		return result + "]";
	}
	
	private Map getRealMap() {
		return m_map;
	}
}
