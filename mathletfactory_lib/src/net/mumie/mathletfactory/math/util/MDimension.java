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

package net.mumie.mathletfactory.math.util;

import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.util.xml.ExerciseCompoundObjectIF;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents a dimension value (containing values for the width and the height) 
 * inside the Exercise Object Framework and acts as base for its MM-Class.
 * The number class is always {@link net.mumie.mathletfactory.math.number.MInteger} because
 * dimension values are always whole numbers.
 * 
 * @see net.mumie.mathletfactory.mmobject.util.MMDimension
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MDimension implements ExerciseCompoundObjectIF {

	public final static int WIDTH_POSITION = 1;
	public final static int HEIGHT_POSITION = 0;
	
	protected final static Class NUMBER_CLASS = MInteger.class;
	
	private MInteger m_width;
	
	private MInteger m_height;
	
	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;
	
	/**
	 * Creates a new instance with zero values for the width and height.
	 */
	public MDimension() {
		this(0, 0);
	}

	/**
	 * Copy Constructor: creates a new instance with the given value.
	 */
	public MDimension(MDimension dim) {
		this(dim.getHeight(), dim.getWidth());
	}
	
	/**
	 * Creates a new instance with the given values for the width and height.
	 */
	public MDimension(int height,int width) {
		m_width = new MInteger(width);
		m_height = new MInteger(height);
	}
	
	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
	public MDimension(Node content) {
		this(0, 0);
		setMathMLNode(content);
	}
	
	public int getWidth() {
		return m_width.getIntValue();
	}
	
	public void setWidth(MInteger width) {
		m_width.set(width);
	}

	public void setWidth(int width) {
		m_width.setDouble(width);
	}
	
	public int getHeight() {
		return m_height.getIntValue();
	}
	
	public void setHeight(MInteger height) {
		m_height.set(height);
	}

	public void setHeight(int height) {
		m_height.setDouble(height);
	}

	public boolean isCompletelyEdited() {
		return m_width.isEdited() && m_height.isEdited();
	}

	public boolean isEdited() {
		return m_width.isEdited() || m_height.isEdited();
	}

	/**
	 * Returns whether the width or height entry (see index contants in {@link MDimension}) was user-edited,
	 * i.e. returns the internal stored edited flag for the entry.
	 * 
	 * @param index whether {@link MDimension#WIDTH_POSITION} or {@link MDimension#HEIGHT_POSITION}
	 */
	public boolean isEdited(int index) {
		switch(index) {
		case WIDTH_POSITION:
			return m_width.isEdited();
		case HEIGHT_POSITION:
			return m_height.isEdited();
		}
		throw new IllegalArgumentException("Index does not match one of the index constants !");
	}

	public void setEdited(boolean edited) {
		m_width.setEdited(edited);
		m_height.setEdited(edited);
	}
	
	/**
	 * Marks the width or height entry (see index contants in {@link MDimension}) user-edited or not,
	 * i.e. sets the internal edited flag for the entry to <code>edited</code>.
	 * 
	 * @param edited a flag
	 * @param index whether {@link MDimension#WIDTH_POSITION} or {@link MDimension#HEIGHT_POSITION}
	 */
	public void setEdited(boolean edited, int index) {
		switch(index) {
		case WIDTH_POSITION:
			m_width.setEdited(edited);
			break;
		case HEIGHT_POSITION:
			m_height.setEdited(edited);
			break;
		default:
			throw new IllegalArgumentException("Index does not match one of the index constants !");
		}
	}

	public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
	}

	public Node getMathMLNode(Document doc) {
		if(isEdited()) {
			Element mrow = ExerciseObjectFactory.createNode(this, "mrow", doc);
			mrow.appendChild(m_height.getMathMLNode(doc));
			mrow.appendChild(doc.createTextNode("x"));
			mrow.appendChild(m_width.getMathMLNode(doc));
			return mrow;
		} else return ExerciseObjectFactory.createUneditedNode(this, doc);
	}

	public void setMathMLNode(Node content) {
		if(content.getNodeName().equals("mi")) {
			m_height.setEdited(false);
			m_width.setEdited(false);
			ExerciseObjectFactory.importExerciseAttributes(content, this);
		}else if(content.getNodeName().equals("mrow")) {
			int index = XMLUtils.getNextNonTextNodeIndex(content, 0);
			m_height.setMathMLNode(content.getChildNodes().item(index));
			m_width.setMathMLNode(XMLUtils.getNextNonTextNode(content, index+1));
			ExerciseObjectFactory.importExerciseAttributes(content, this);
		} else 
			throw new IllegalArgumentException("Node name must be mrow but is " + XMLUtils.nodeToString(content, true));
	}

	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	public Class getNumberClass() {
		return NUMBER_CLASS;
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}
	
	public String toString() {
		return "( " + getWidth() + " x " + getHeight() + " )";
	}
}
