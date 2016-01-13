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



public class CombinedPropertyMap extends DefaultPropertyMap {
	
	private DefaultPropertyMap m_defaultsMap;
	
	public CombinedPropertyMap() {
		this(null);
	}
	
	public CombinedPropertyMap(DefaultPropertyMap defaultsMap) {
		m_defaultsMap = defaultsMap;
	}
	
	protected Property getDefaultPropertyImpl(String name) {
		if(m_defaultsMap == null)
			return super.getDefaultPropertyImpl(name);
		Property p = m_defaultsMap.getDefaultPropertyImpl(name);
		if( p == null )
			return super.getDefaultPropertyImpl(name);
		return p;
	}
	
	protected Property getPropertyImpl(String name) {
		if(m_defaultsMap == null)
			return super.getPropertyImpl(name);
		Property p = m_defaultsMap.getPropertyImpl(name);
		if( p == null )
			return super.getPropertyImpl(name);
		return p;
	}
	
	public void setDefaultsMap(DefaultPropertyMap defaultsMap) {
		m_defaultsMap = defaultsMap;
	}
	
	public int getPropertiesCount() {
		int n = m_defaultsMap == null ? 0 : m_defaultsMap.getPropertiesCount();
		return n + super.getPropertiesCount();
	}
	
	public Property[] getProperties() {
		Property[] p1 = super.getProperties();
		if(m_defaultsMap == null)
			return p1;
		Property[] p2 = m_defaultsMap.getProperties();
		Property[] result = new Property[p1.length + p2.length];
		System.arraycopy(p2, 0, result, 0, p2.length);
		System.arraycopy(p1, 0, result, p2.length, p1.length);
		return result;
	}
}
