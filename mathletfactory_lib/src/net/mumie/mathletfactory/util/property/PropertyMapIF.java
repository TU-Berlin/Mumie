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


/**
 * This interface describes the common capabilities of property maps in the meaning of
 * the MathletFactory Property Framework.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public interface PropertyMapIF {
	
//	/**
//	 * Returns the named property from the internal map. When no property is found, the map will
//	 * be quested for the default property. When no default property is found, <code>null</code>
//	 * will be returned.
//	 * 
//	 * @param name name of a property
//	 * @return the (default) property or <code>null</code>
//	 */
//	Property getPropertyImpl(String name);
	
//	/**
//	 * Stores the given property in this map and returns the old property.
//	 * If the value is <code>null</code> the possible old property will be removed from the map 
//	 * and returned by this method, or <code>null</code> if none existed.
//	 * An already existing property will be overwritten.
//	 * 
//	 * @param p a property; may be <code>null</code>
//	 * @return the old property or <code>null</code>
//	 */
//	Property setPropertyImpl(Property p);
	
	/**
	 * Returns if a property with the given name is contained in this map.
	 * 
	 * @param name name of a property
	 */
	boolean containsProperty(String name);
	
	/**
	 * Returns if a default property (i.e. a property with a default value) with the given name 
	 * is contained in the internal map.
	 * 
	 * @param name name of a property
	 */
	boolean containsDefaultProperty(String name);
	
	/**
	 * Returns the named property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>null</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @return the property's (default) value or <code>null</code>
	 */
	Object getProperty(String name);
	
	/**
	 * Returns the named property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>defaultValue</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @param defaultValue default value if neither an explicit nor a default value exist; may be <code>null</code>
	 * @return the property's (default) value or <code>defaultValue</code>
	 */
	Object getProperty(String name, Object defaultValue);
	
	/**
	 * Stores the named property in the internal map and returns its previous value.
	 * If the value is <code>null</code> the possible old property will be removed from the map 
	 * and returned by this method, or <code>null</code> if none existed.
	 * If an old property exists which is read-only, an exception will be thrown
	 * 
	 * @param name name of a property
	 * @param value property value; may be <code>null</code>
	 * @return the old property or <code>null</code>
	 * @throws PropertyChangeException
	 */
	Object setProperty(String name, Object value);
	
	/**
	 * Stores the default value of the named property in the internal map and returns its previous value.
	 * If the value is <code>null</code> the possible old property will be removed from the map 
	 * and returned by this method, or <code>null</code> if none existed.
	 * Already existing values will be overwritten.
	 * 
	 * @param name name of a property
	 * @param value property value; may be <code>null</code>
	 * @return the property's previous value or <code>null</code>
	 */
	Object setDefaultProperty(String name, Object value);

	/**
	 * Deletes all properties from this map.
	 */
	void clear();
	
	/**
	 * Returns a new property map instance with the same properties as this map.
	 */
	PropertyMapIF copy();
	
	/**
	 * Copies all properties contained in this map into <code>map</code>.
	 */
	void copyPropertiesInto(PropertyMapIF map);
	
	/**
	 * Copies all properties contained in <code>map</code> into this map.
	 */
	void copyPropertiesFrom(PropertyMapIF map);
	
	/**
	 * Returns an array containing all properties in this map. 
	 */
	Property[] getProperties();
	
	/**
	 * Returns the number of properties in this map.
	 */
	int getPropertiesCount();
	
	/**
	 * Returns whether this map is empty, i.e. contains no properties.
	 */
	boolean isEmpty();
	
	/**
	 * Returns a comma separated list of all stored properties.
	 */
	String toString();
	
	/**
	 * Returns <code>true</code> if the argument is a {@link PropertyMapIF} and if both have the same size and 
	 * same keys with the same content.
	 * 
	 * @author weber
	 */
	boolean equals(Object o);
}
