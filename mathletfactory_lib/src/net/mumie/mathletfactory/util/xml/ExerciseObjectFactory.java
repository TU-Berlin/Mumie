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

package net.mumie.mathletfactory.util.xml;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JComponent;

import net.mumie.mathletfactory.mmobject.MMObjectIF;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class manages the XML attributes of exercise objects i.e. the object types and fields.
 * Attributes can be imported from and exported to existing XML nodes. 
 * New nodes can be created easily via factory methods.
 * All mappings are stored in external settings.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class ExerciseObjectFactory {
	
	/** The namespace URI for all exercise object attributes. */
	public final static String NAMESPACE_URI = "http://www.mumie.net/xml-namespace/exercise-object-attributes";

	/** The prefix for all exercise object attributes. */
	public final static String PREFIX = "mf";

	/* Location of settings file. */
	private final static String SETTINGS_FILE_LOCATION = "/resource/settings/exerciseObjects.xml";
	
	// Constants for MathML-attributes
	
	public final static String TYPE_ATTRIBUTE = "type";
	
	public final static String FIELD_ATTRIBUTE = "field";
	
	public final static String LABEL_ATTRIBUTE = "label";
	
	public final static String HIDDEN_ATTRIBUTE = "hidden";
	
	// Constants for reading XML settings
	
	private final static String NAME_ATTRIBUTE = "name";
	
	private final static String CLASS_ATTRIBUTE = "class";
	
	private final static String M_CLASS_ATTRIBUTE = "mClass";
	
	private final static String MM_CLASS_ATTRIBUTE = "mmClass";
	
	private final static String RULE_FIELD_ATTRIBUTE = "field";
	
	private final static String RULE_APPLIES_ATTRIBUTE = "applies";
	
	/* Field holding reference to current instance for static methods. */ 
	private static ExerciseObjectFactory m_instance = new ExerciseObjectFactory();
	
	/* Field containing the object types. */
	private Vector m_types = new Vector();
	
	/* Field containing the field types. */
	private Vector m_fields = new Vector();
	
	/*
	 * Creates a new instance (the "only" instance) and parses the settings file.
	 */
	private ExerciseObjectFactory() {
		try {
			parseSettingsFile();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* ... */
	private void parseSettingsFile() throws SAXException, IOException {
		Document doc = XMLUtils.createDocumentBuilder().parse(
				getClass().getResourceAsStream(SETTINGS_FILE_LOCATION) );
		
		// find root nodes
		Node rootNode = XMLUtils.getNextNonTextNode(doc, 0);
		NodeList rootNodes = rootNode.getChildNodes();
		int index = XMLUtils.getNextNonTextNodeIndex(rootNode, 0);
    Node typesNode = rootNodes.item(index);
    index = XMLUtils.getNextNonTextNodeIndex(rootNode, index+1);
    Node fieldsNode = rootNodes.item(index);
    
    // read in object types
    index = 0;
		while((index = XMLUtils.getNextNonTextNodeIndex(typesNode, index)) > -1) {
			addType(typesNode.getChildNodes().item(index));
			index++;
		}
    
    // read in object fields
    index = 0;
		while((index = XMLUtils.getNextNonTextNodeIndex(fieldsNode, index)) > -1) {
			addField(fieldsNode.getChildNodes().item(index));
			index++;
		}
	}
	
	/* Adds a new type to the list. */
	private void addType(Node typeNode) {
		// read in attributes
		NamedNodeMap attributes = typeNode.getAttributes();
		String typeName = attributes.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
		String mClass = attributes.getNamedItem(M_CLASS_ATTRIBUTE).getNodeValue();
		String mmClass = attributes.getNamedItem(MM_CLASS_ATTRIBUTE).getNodeValue();
		// read in rule
		Node ruleNode = XMLUtils.getNextNonTextNode(typeNode, 0);
		ExerciseObjectTypeRule rule = new ExerciseObjectTypeRule();
		attributes = ruleNode.getAttributes();
		rule.m_field = attributes.getNamedItem(RULE_FIELD_ATTRIBUTE).getNodeValue();
		rule.m_applies = Boolean.valueOf(
				attributes.getNamedItem(RULE_APPLIES_ATTRIBUTE).getNodeValue() ).booleanValue();
		ExerciseObjectType type = new ExerciseObjectType();
		type.m_name = typeName;
		type.m_mClass = mClass;
		type.m_mmClass = mmClass;
		type.m_rule = rule;
		m_types.add(type);
	}
	
	/* Adds a new field to the list. */
	private void addField(Node fieldNode) {
		// read in attributes
		NamedNodeMap attributes = fieldNode.getAttributes();
		String fieldName = attributes.getNamedItem(NAME_ATTRIBUTE).getNodeValue();
		String fieldClass = attributes.getNamedItem(CLASS_ATTRIBUTE).getNodeValue();
		ExerciseObjectField field = new ExerciseObjectField();
		field.m_name = fieldName;
		field.m_class = fieldClass;
		m_fields.add(field);
	}
	
	/**
	 * Parses the <code>field</code> attribute of a XML node and returns the appropriate
	 * number class.
	 *  
	 * @param content a XML node
	 * @return a referemce to the number class
	 * @throws ClassNotFoundException if the <code>field</code> attribute is not contained in the XML node
	 */
	public static Class getNumberClass(Node content) throws ClassNotFoundException {
		NamedNodeMap attributes = content.getAttributes();
		if(attributes != null) {
			String fieldName = attributes.getNamedItemNS(NAMESPACE_URI, FIELD_ATTRIBUTE).getNodeValue();
			String className = m_instance.getFieldClass(fieldName);
			return Class.forName(className);
		} else
			throw new IllegalArgumentException("XML content node must contain \"" + FIELD_ATTRIBUTE + "\" attribute !");
	}
	
	/**
	 * Creates and returns a new MathML node for the given exercise object and node name.
	 * All available non-default exercise object attributes are written to the newly created node,
	 * i.e. attributes with the default value are omitted.
	 * The default MathML namespace is used.
	 */
	public static Element createNode(ExerciseObjectIF exObject, String nodeName, Document doc) {
		return createNode(exObject, nodeName, doc, XMLUtils.MATHML_NAMESPACE);
	}

	/**
	 * Creates and returns a new unedited MathML node for the given exercise object,
	 * i.e. a node reflecting a non-edited state.
	 * All available non-default exercise object attributes are written to the newly created node,
	 * i.e. attributes with the default value are omitted.
	 * The default MathML namespace is used.
	 */
	public static Element createUneditedNode(ExerciseObjectIF exObject, Document doc) {
		return createNode(exObject, ExerciseObjectIF.UNEDITED_NODE_NAME, doc, XMLUtils.MATHML_NAMESPACE);
	}

	/**
	 * Creates and returns a new MathML node for the given exercise object and node name.
	 * All available non-default exercise object attributes are written to the newly created node,
	 * i.e. attributes with the default value are omitted.
	 * The given MathML namespace is used instead of the default namespace.
	 */
	public static Element createNode(ExerciseObjectIF exObject, String nodeName, Document doc, String namespace) {
		Element node = XMLUtils.createElementNS(doc, namespace, null, nodeName);
		exportExerciseAttributes(node, exObject);
		return node;
	}
	
	public static String getTypeName(Class anObjectClass, String fieldName) {
		return m_instance.getTypeNameImpl(anObjectClass, fieldName);
	}
	
	/* Returns the type name for an exercise object class and field name. */
	private String getTypeNameImpl(Class anObjectClass, String fieldName) {
		String className = anObjectClass.getName();
		for(int i = 0; i < m_types.size(); i++) {
			ExerciseObjectType type = (ExerciseObjectType) m_types.get(i);
			if( (type.m_mClass.equals(className) || type.m_mmClass.equals(className) )
					&& type.m_rule.applies(fieldName) )
				return type.m_name;
		}
		return null;
	}
	
	public static String getFieldName(Class aNumberClass) {
		 return m_instance.getFieldNameImpl(aNumberClass);
	}
	
	public static String getAttribute(String name, Node mathMLnode) {
		NamedNodeMap attributes = mathMLnode.getAttributes();
		if(attributes == null) // XML node has no attributes
			return null;
		Node attrNode = attributes.getNamedItemNS(NAMESPACE_URI, name);
		if(attrNode == null) // XML node has no such attribute
			return null;
		return attrNode.getNodeValue();
	}
	
	/* Returns the field name for an exercise object class. */
	private String getFieldNameImpl(Class aNumberClass) {
		for(int i = 0; i < m_fields.size(); i++) {
			ExerciseObjectField field = (ExerciseObjectField) m_fields.get(i);
			if(isInstanceOf(aNumberClass, field.m_class))
				return field.m_name;
		}
		return null;
	}
	
	/* Returns the field (i.e. number) class for a field name. */
	private String getFieldClass(String fieldName) {
		for(int i = 0; i < m_fields.size(); i++) {
			ExerciseObjectField field = (ExerciseObjectField) m_fields.get(i);
			if(field.m_name.equals(fieldName))
				return field.m_class;
		}
		return null;
	}
	
	/* Returns the M class for an exercise object type and field name. */
	private String getMClass(String typeName, String fieldName) {
		for(int i = 0; i < m_types.size(); i++) {
			ExerciseObjectType type = (ExerciseObjectType) m_types.get(i);
			if( type.m_name.equals(typeName)
					&& type.m_rule.applies(fieldName) )
				return type.m_mClass;
		}
		return null;
	}
	
	/* Returns the MM class for an exercise object type and field name. */
	private String getMMClass(String typeName, String fieldName) {
		for(int i = 0; i < m_types.size(); i++) {
			ExerciseObjectType type = (ExerciseObjectType) m_types.get(i);
			if( type.m_name.equals(typeName)
					&& type.m_rule.applies(fieldName) )
				return type.m_mmClass;
		}
		return null;
	}
	
	/* Returns whether both class references describe the same class or a super class. */
	private static boolean isInstanceOf(Class classInQuestion, String baseClass) {
		if(baseClass.equals(classInQuestion.getName()))
			return true;
		Class c = classInQuestion.getSuperclass();
		if(c != null)
			return isInstanceOf(c, baseClass);
		return false;
	}
	
	/**
	 * Imports all available exercise attributes of the given XML node 
	 * into the given exercise object. Existing attributes will be overwritten.
	 */
	public static void importExerciseAttributes(Node mathMLnode, ExerciseObjectIF exerciseObject) {
		NamedNodeMap attributes = mathMLnode.getAttributes();
		if(attributes == null) // XML node has no attributes
			return;
		Node labelNode = attributes.getNamedItemNS(NAMESPACE_URI, LABEL_ATTRIBUTE);
		if(labelNode != null) {
			exerciseObject.setLabel(labelNode.getNodeValue());
		}
		Node hiddenNode = attributes.getNamedItemNS(NAMESPACE_URI, HIDDEN_ATTRIBUTE);
		if(hiddenNode != null) {
			exerciseObject.setHidden(Boolean.valueOf(hiddenNode.getNodeValue()).booleanValue());
		}
	}
	
	/**
	 * Exports all available non-default exercise attributes of the given exercise object 
	 * into the given XML node, i.e. attributes with the default value are omitted.
	 * Existing attributes will be overwritten.
	 */
	public static Node exportExerciseAttributes(Node mathMLnode, ExerciseObjectIF exObject) {
		if(mathMLnode instanceof Element)
			return exportExerciseAttributes((Element) mathMLnode, exObject);
		// create new Element instance --> attributes not defined in Node
		Element node = mathMLnode.getOwnerDocument().createElementNS(mathMLnode.getNamespaceURI(), mathMLnode.getNodeName());
		copyChildren(mathMLnode, node);
		return exportExerciseAttributes(node, exObject);
	}
	
	/**
	 * Copies the children below <code>sourceNode</code> to <code>targetNode</code>.
	 */
	public static void copyChildren(Node sourceNode, Node targetNode) {
		NodeList children = sourceNode.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			targetNode.appendChild(targetNode.getOwnerDocument().importNode(children.item(i), true));
		}
	}

	/**
	 * Exports all available non-default exercise attributes of the given exercise object 
	 * into the given XML node, i.e. attributes with the default value are omitted.
	 * Existing attributes will be overwritten.
	 */
	public static Node exportExerciseAttributes(Element node, ExerciseObjectIF exObject) {
		if(exObject.getNumberClass() == null)
			throw new NullPointerException("Number class of exercise object must not be null: " + exObject.getClass().getName());
		String fieldName = m_instance.getFieldNameImpl(exObject.getNumberClass());
		if(fieldName != null) {
			XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, FIELD_ATTRIBUTE, fieldName);
			String typeName = m_instance.getTypeNameImpl(exObject.getClass(), fieldName);
			if(typeName != null) {
				XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, TYPE_ATTRIBUTE, typeName);
			} else
				System.err.println("Exercise object has no registered type name: " + exObject.getClass().getName() + "\nOmitting \"" + TYPE_ATTRIBUTE + "\" attribute.");
		} else
			System.err.println("Exercise object has no registered number class: " + exObject.getNumberClass().getName() + "\nOmitting \"" + TYPE_ATTRIBUTE + "\" and \"" + FIELD_ATTRIBUTE + "\" attributes.");
		if(exObject.getLabel() != null && !exObject.getLabel().trim().equals(""))
			XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, LABEL_ATTRIBUTE, exObject.getLabel());
		if(exObject.isHidden())
			XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, HIDDEN_ATTRIBUTE, "true");
		return node;
	}
	
	/**
	 * Converts the given class types into exercise attributes and exports them into the given XML node.
	 * Existing attributes will be overwritten.
	 */
	public static Node exportExerciseAttributes(Element node, Class objectClass, Class numberClass) {
		String fieldName = m_instance.getFieldNameImpl(numberClass);
		if(fieldName != null) {
			XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, FIELD_ATTRIBUTE, fieldName);
			String typeName = m_instance.getTypeNameImpl(objectClass, fieldName);
			if(typeName != null) {
				XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, TYPE_ATTRIBUTE, typeName);
			} else
				System.err.println("Exercise object has no registered type name: " + objectClass.getName() + "\nOmitting \"" + TYPE_ATTRIBUTE + "\" attribute.");
		} else
			System.err.println("Exercise object has no registered number class: " + numberClass.getName() + "\nOmitting \"" + TYPE_ATTRIBUTE + "\" and \"" + FIELD_ATTRIBUTE + "\" attributes.");
		return node;
	}
	
	/**
	 * Converts the given class types into exercise attributes and exports them into the given XML node.
	 * Existing attributes will be overwritten.
	 */
	public static Node exportExerciseAttributes(Element node, String typeName, String fieldName) {
		if(typeName != null)
			XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, TYPE_ATTRIBUTE, typeName);
		if(fieldName != null)
			XMLUtils.setAttributeNS(node, NAMESPACE_URI, PREFIX, FIELD_ATTRIBUTE, fieldName);
		return node;
	}
	
	/**
	 * Returns an mm-object instance of the given MathML content node which must contain exercise
	 * object attributes in order to create successfully the appropriate object.
	 * 
	 * @param mathMLnode a MathML/XML node
	 * @return an instance of {@link MMObjectIF}
	 * @throws ClassNotFoundException thrown if the registered MM-class was not found
	 * @throws NoSuchMethodException thrown if the MM-class does not have an appropriate constructor
	 * @throws IllegalArgumentException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InstantiationException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws IllegalAccessException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InvocationTargetException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws SecurityException see {@link Class#getConstructor(java.lang.Class[])}
	 */
	public static MMObjectIF createMMObject(Node mathMLnode) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
				IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if(mathMLnode == null)
		throw new NullPointerException("MathML node must not be null !");
		if(mathMLnode.getAttributes() == null 
			|| mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, TYPE_ATTRIBUTE) == null 
			|| mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, FIELD_ATTRIBUTE) == null)
		throw new NullPointerException("MathML node must contain exercise attributes !");
		String typeName = mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, TYPE_ATTRIBUTE).getNodeValue();
		String fieldName = mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, FIELD_ATTRIBUTE).getNodeValue();
		String mmClassName = m_instance.getMMClass(typeName, fieldName);
		Class mmClass = Class.forName(mmClassName);
		Constructor constructor = mmClass.getConstructor(new Class[] { Node.class });
		return (MMObjectIF) constructor.newInstance(new Node[] { mathMLnode });
	}

	
	/**
	 * Returns a viewer instance instance of the given MathML content node which must contain exercise
	 * object attributes in order to create successfully the appropriate MM-Object's container drawable.
	 * 
	 * @param mathMLnode a MathML/XML node
	 * @return an instance of an appropiate viewer
	 * @throws ClassNotFoundException thrown if the registered MM-class was not found
	 * @throws NoSuchMethodException thrown if the MM-class does not have an appropriate constructor
	 * @throws IllegalArgumentException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InstantiationException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws IllegalAccessException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InvocationTargetException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws SecurityException see {@link Class#getConstructor(java.lang.Class[])}
	 */
	public static JComponent createViewer(Node mathMLnode) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
				IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return createMMObject(mathMLnode).getAsContainerContent();
	}

	/**
	 * Returns an exercise object instance of the given MathML content node which must contain exercise
	 * object attributes in order to create successfully the appropriate object.
	 * 
	 * @param mathMLnode a MathML/XML node
	 * @return an instance of {@link ExerciseObjectIF}
	 * @throws ClassNotFoundException thrown if the registered M-class was not found
	 * @throws NoSuchMethodException thrown if the M-class does not have an appropriate constructor
	 * @throws IllegalArgumentException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InstantiationException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws IllegalAccessException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InvocationTargetException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws SecurityException see {@link Class#getConstructor(java.lang.Class[])}
	 */
	public static ExerciseObjectIF createExerciseObject(Node mathMLnode) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
				IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if(mathMLnode == null)
			throw new NullPointerException("MathML node must not be null !");
		if(mathMLnode.getAttributes() == null 
				|| mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, TYPE_ATTRIBUTE) == null 
				|| mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, FIELD_ATTRIBUTE) == null)
			throw new NullPointerException("MathML node must contain exercise attributes !");
		String typeName = mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, TYPE_ATTRIBUTE).getNodeValue();
		String fieldName = mathMLnode.getAttributes().getNamedItemNS(NAMESPACE_URI, FIELD_ATTRIBUTE).getNodeValue();
		return createExerciseObject(typeName, fieldName, mathMLnode);
	}
	
	/**
	 * Returns an exercise object instance for the given type, field and MathML content node.
	 * 
	 * @param mathMLnode a MathML/XML node
	 * @return an instance of {@link ExerciseObjectIF}
	 * @throws ClassNotFoundException thrown if the registered M-class was not found
	 * @throws NoSuchMethodException thrown if the M-class does not have an appropriate constructor
	 * @throws IllegalArgumentException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InstantiationException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws IllegalAccessException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws InvocationTargetException see {@link Constructor#newInstance(java.lang.Object[])}
	 * @throws SecurityException see {@link Class#getConstructor(java.lang.Class[])}
	 */
	public static ExerciseObjectIF createExerciseObject(String type, String field, Node mathMLnode) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
	IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		String mClassName = m_instance.getMClass(type, field);
		Class mClass = Class.forName(mClassName);
		Constructor constructor = mClass.getConstructor(new Class[] { Node.class });
		return (ExerciseObjectIF) constructor.newInstance(new Node[] { mathMLnode });
	}
	
	class ExerciseObjectType {
		String m_name, m_mClass, m_mmClass;
		ExerciseObjectTypeRule m_rule;
	}
	
	class ExerciseObjectField {
		String m_name, m_class;
	}
	
	class ExerciseObjectTypeRule {
		String m_field;
		boolean m_applies;
		
		boolean applies(String fieldName) {
			if(m_field.equals("*"))
				return m_applies;
			return m_field.equals(fieldName) == m_applies;
		}
	}
}
