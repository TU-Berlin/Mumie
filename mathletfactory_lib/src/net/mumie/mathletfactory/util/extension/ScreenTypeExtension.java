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

package net.mumie.mathletfactory.util.extension;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.Property;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScreenTypeExtension {
	
	private final static MumieLogger LOGGER = MumieLogger.getLogger(ScreenTypeExtension.class);
	
	private final static LogCategory RENDERING_HINTS_CATEGORY = LOGGER.getCategory("display.rendering-hints");

	class TransformType {
		
		private String m_name;
		
		private Class m_masterClass;
		
		private Class m_transformerClass;

		TransformType(TransformType parent, Node xmlNode) {
			if(parent != null) {
				this.m_name = parent.m_name;
				this.m_masterClass = parent.m_masterClass;
				this.m_transformerClass = parent.m_transformerClass;
			}
			String name = XMLUtils.getAttribute(xmlNode, "name");
			if(name != null)
				m_name = name;
			
			String master = XMLUtils.getAttribute(xmlNode, "master");
			if(master != null)
				m_masterClass = getClassFromString(master);
			
			String transformer = XMLUtils.getAttribute(xmlNode, "transformer");
			if(transformer != null)
				m_transformerClass = getClassFromString(transformer);
		}
		
		String getName() {
			return m_name;
		}
		
		Class getMasterClass() {
			return m_masterClass;
		}
		
		Class getTransformerClass() {
			return m_transformerClass;
		}
		
		boolean isValid() {
			return m_name != null && m_masterClass != null && m_transformerClass != null;
		}
	}
	
	class RenderingHint {
		
		private Property m_property;
		
		private String m_transformer;

		RenderingHint(RenderingHint parent, Node xmlNode) {
			if(parent != null) {
				this.m_transformer = parent.m_transformer;
			}
			String transformer = XMLUtils.getAttribute(xmlNode, "transformer");
			if(transformer != null)
				m_transformer = transformer;
			if(xmlNode.getNodeName().equals("mf:property"))// lists do not have properties
				m_property = new Property(xmlNode);
		}
		
		boolean isValid() {
			return m_transformer != null;
		}
		
		public String toString() {
			return m_transformer + "=" + m_property;
		}
	}
	
	public final static int NO_SCREEN_TYPE = -1;
	
	public final static int SCREEN_DIMENSION_NOC = 0;
	
	public final static int SCREEN_DIMENSION_2D = 2;

	public final static int SCREEN_DIMENSION_3D = 3;
	
	public final static String DIMENSION_NOC_NAME = "NOC";

	public final static String DIMENSION_2D_NAME = "2D";

	public final static String DIMENSION_3D_NAME = "3D";

	private String m_name;
	
	private int m_screenDimension = NO_SCREEN_TYPE;
	
	private Class m_canvasClass;
	
	private TransformType[] m_transformTypes;
	
	private HashMap m_renderingHints = new HashMap();
	
	public ScreenTypeExtension(Node xmlNode) {
		String name = XMLUtils.getAttribute(xmlNode, "name");
		String type = XMLUtils.getAttribute(xmlNode, "type");
		String canvas = XMLUtils.getAttribute(xmlNode, "canvas");
		Node transformTypesNode = XMLUtils.getFirstChildNode(xmlNode, "mf:transform-types");
		Node renderingHintsNode = XMLUtils.getFirstChildNode(xmlNode, "mf:rendering-hints");
		setName(name);
		setDimension(type);
		setCanvasClass(canvas);
		setTransformTypes(transformTypesNode);
		setRenderingHints(renderingHintsNode);
	}
	
	private int getDimensionFromString(String dimString) {
		if(dimString.equalsIgnoreCase(DIMENSION_NOC_NAME))
			return SCREEN_DIMENSION_NOC;
		else if(dimString.equalsIgnoreCase(DIMENSION_2D_NAME))
			return SCREEN_DIMENSION_2D;
		else if(dimString.equalsIgnoreCase(DIMENSION_3D_NAME))
			return SCREEN_DIMENSION_3D;
		else
			throw new XMLParsingException("Type string must be either \"noc\", \"2d\" or \"3d\" but is: \"" + dimString + "\"");
	}
	
	private Class getClassFromString(String className) {
		try {
			return Class.forName(className);
		} catch(Throwable t) {
			throw new UnsupportedExtensionException(t);
		}
	}
	
	private void setName(String name) {
		if(name == null)
			throw new XMLParsingException("Not a valid screen-type node ! Missing attribute: \"name\"");
		m_name = name;
	}

	public String getName() {
		return m_name;
	}
	
	private void setDimension(String type) {
		if(type != null)
//			throw new XMLParsingException("Not a valid screen-type node ! Missing attribute: \"type\"");
		m_screenDimension = getDimensionFromString(type);
	}
	
	public int getScreenDimension() {
		return m_screenDimension;
	}
	
	private void setCanvasClass(String canvasClass) {
		if(canvasClass != null)
//			throw new XMLParsingException("Not a valid screen-type node ! Missing attribute: \"canvas\"");
		m_canvasClass = getClassFromString(canvasClass);
	}
	
	public Class getCanvasClass() {
		return m_canvasClass;
	}
	
	private void setTransformTypes(Node xmlNode) {
		Vector v = new Vector();
		if(xmlNode != null) {
			addTransformTypes(xmlNode, v, null);
		}
		m_transformTypes = new TransformType[v.size()];
		v.toArray(m_transformTypes);
	}
	
	private void addTransformTypes(Node xmlNode, Vector list, TransformType parent) {
		if(xmlNode.getNodeName().equals("mf:transform-types")) { // a list
			TransformType listParent = new TransformType(parent, xmlNode);
			NodeList children = xmlNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				Node n = children.item(i);
				if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
					continue;
				addTransformTypes(n, list, listParent);
			}
		} else if(xmlNode.getNodeName().equals("mf:transform-type")) { // a single type
			TransformType tt = new TransformType(parent, xmlNode);
			if(tt.isValid())
				list.add(tt);
			else
				throw new XMLParsingException("Not a valid transform type !", xmlNode);
		} else
			throw new XMLParsingException("Only transform types are allowed in this section !", xmlNode);
	}
	
	public TransformType[] getTransformTypes() {
		return m_transformTypes;
	}
	
	private void setRenderingHints(Node xmlNode) {
		if(xmlNode != null) {
			addRenderingHints(xmlNode, null);
		}
	}
	
	private void addRenderingHints(Node xmlNode, RenderingHint parent) {
		if(xmlNode.getNodeName().equals("mf:rendering-hints")) { // a list
			RenderingHint listParent = new RenderingHint(parent, xmlNode);
			NodeList children = xmlNode.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				Node n = children.item(i);
				if(n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.COMMENT_NODE)
					continue;
				addRenderingHints(n, listParent);
			}
		} else if(xmlNode.getNodeName().equals("mf:property")) { // a single rendering hint
			addRenderingHint(new RenderingHint(parent, xmlNode));
		} else
			throw new XMLParsingException("Only rendering hints and properties are allowed in this section !", xmlNode);
	}
	
	public HashMap getRenderingHints() {
		return m_renderingHints;
	}
	
	public PropertyMapIF getRenderingHints(String transformer) {
		DefaultPropertyMap propMap = (DefaultPropertyMap) m_renderingHints.get(transformer);
		if(propMap == null) {
			propMap = new DefaultPropertyMap();
			m_renderingHints.put(transformer, propMap);
		}
		return propMap;
	}
	
	private boolean invalidCanvasClasses(Class o1, Class o2) {
		return o1 != null && o2 != null && !o1.equals(o2);
	}
	
	private boolean validCanvasClasses(ScreenTypeExtension extension) {
		if(this.getScreenDimension() == SCREEN_DIMENSION_NOC)
			return true;
		return !invalidCanvasClasses(this.getCanvasClass(), extension.getCanvasClass());
	}
	
	private Object getValid(Object o1, Object o2) {
		if(o1 == null)
			return o2;
		else
			return o1;
	}
	
	private boolean invalid(int i1, int i2) {
		return (i1 == NO_SCREEN_TYPE && i2 == NO_SCREEN_TYPE) || (i1 != NO_SCREEN_TYPE && i2 != NO_SCREEN_TYPE && i1 != i2);
	}
	
	private int getValid(int i1, int i2) {
		if(i1 == NO_SCREEN_TYPE)
			return i2;
		else
			return i1;
	}
	
	public void addProperties(ScreenTypeExtension extension) {
		if((!this.getName().equals(extension.getName())) || invalid(this.getScreenDimension(), extension.getScreenDimension()) || !validCanvasClasses(extension))
			throw new IllegalArgumentException("Both extensions must have the same name and must neither differ nor be empty in screen type and canvas class !");
		m_screenDimension = getValid(this.getScreenDimension(), extension.getScreenDimension());
		m_canvasClass = (Class) getValid(this.getCanvasClass(), extension.getCanvasClass());
		this.copyTransformTypes(extension);
		this.copyRenderingHints(extension);
	}
	
	private void copyRenderingHints(ScreenTypeExtension extension) {
		Set keys = extension.m_renderingHints.keySet();// keys are transformers
		for(Iterator i = keys.iterator(); i.hasNext(); ) {
			String transformer = (String) i.next();
			PropertyMapIF extMap = extension.getRenderingHints(transformer);
			PropertyMapIF map = this.getRenderingHints(transformer);
			if(map == null)
				m_renderingHints.put(transformer, extMap);
			else
				map.copyPropertiesFrom(extMap);
		}
	}
	
	private void copyTransformTypes(ScreenTypeExtension extension) {
		TransformType[] result = new TransformType[this.m_transformTypes.length + extension.m_transformTypes.length];
		System.arraycopy(this.m_transformTypes, 0, result, 0, this.m_transformTypes.length);
		System.arraycopy(extension.m_transformTypes, 0, result, this.m_transformTypes.length, extension.m_transformTypes.length);
		m_transformTypes = result;
	}
	
	private void addRenderingHint(RenderingHint rh) {
		if(rh.isValid()) {
			LOGGER.log(RENDERING_HINTS_CATEGORY, "Creating rendering hint \"" +rh + "\" for screen type \"" + getName() + "\"");
			DefaultPropertyMap propMap = (DefaultPropertyMap) getRenderingHints(rh.m_transformer);
			propMap.setProperty(rh.m_property);
		}
		else
			throw new XMLParsingException("Not a valid rendering hint: " + rh);
	}
}
