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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class PropertyTreeNode {
	
	private Property m_property;
	
	private String m_name;
	
	private PropertyTreeNode m_parent;
	
	private List m_children = new ArrayList();
	
	public static PropertyTreeNode createTree(Property[] properties) {
		PropertyTreeNode rootNode = new PropertyTreeNode(null, null);
		for(int i = 0; i < properties.length; i++) {
			addNode(rootNode, properties[i]);
		}
		return rootNode;
	}
	
	private static PropertyTreeNode addNode(PropertyTreeNode rootNode, Property p) {
		StringTokenizer tok = new StringTokenizer(p.getName(), ".");
		PropertyTreeNode last = rootNode.getChild(tok.nextToken(), true);
		while(tok.hasMoreTokens()) { // property is a sub property
			last = last.getChild(tok.nextToken(), true);
		}
		last.setProperty(p);
		return last;
	}
	
	PropertyTreeNode(String name) {
		this(null, name);
	}
	
	PropertyTreeNode(PropertyTreeNode parent, String name) {
		m_parent = parent;
		m_name = name;
	}
	
//	public PropertyTreeNode(Property p) {
//		this(null, p);
//	}
//	
	PropertyTreeNode(PropertyTreeNode parent, String name, Property p) {
		m_parent = parent;
		m_name = name;
		m_property = p;		
	}
	
	public PropertyTreeNode getParent() {
		return m_parent;
	}
	
	public Property getProperty() {
		return m_property;
	}
	
	void setProperty(Property p) {
		m_property = p;
	}
	
	public String getName() {
		return m_name;
	}
	
	boolean isRootNode() {
		return getName() == null;
	}
	
	String getTopLevelName() {
		if(isRootNode())
			return "";
		if(getName().indexOf('.') > -1)
			return getName().substring(0, getName().indexOf('.'));
		return getName();
	}
	
	PropertyTreeNode addChild(String name) {
		return addChild(name, null);
	}
	
	PropertyTreeNode addChild(Property p) {
		return addChild(p.getName(), p);
	}
	
	private PropertyTreeNode addChild(String name, Property p) {
		if(!isRootNode() && name.startsWith(this.getName()) == false)
			throw new IllegalArgumentException("Property name does not start with this property's name: " + this.getName() + " and " + p.getName());
		PropertyTreeNode n = new PropertyTreeNode(this, name, p);
		m_children.add(n);
		return n;
	}
	
	PropertyTreeNode getChild(String relName, boolean create) {
		for(Iterator i = m_children.iterator(); i.hasNext(); ) {
			PropertyTreeNode n = (PropertyTreeNode) i.next();
			if(n.getTopLevelName().equals(relName))
				return n;
		}
		if(create) {
			if(isRootNode())
				return addChild(relName);
			else
				return addChild(getName() + "." + relName);
		}
		return null;
	}
	
	public List getChildren() {
		return Collections.unmodifiableList(m_children);
	}
}
