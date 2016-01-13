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

import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents a single String value inside the Exercise Object Framework and
 * acts as base for its MM-Class.
 * The number class is always {@link MString} (i.e. this class).
 * 
 * @see net.mumie.mathletfactory.mmobject.util.MMString
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MString implements ExerciseObjectIF {

  protected String m_value;
  
	/**	This field describes the current edited status. */
	private boolean m_isEdited = true;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;
	
	/**
	 * Creates a new MString instance with an empty value.
	 */
	public MString() {
		this(new String());
	}
	
	/**
	 * Creates a new MString instance with the given value.
	 */
	public MString(String value) {
    if(value != null)
      m_value = new String(value);
    else
      m_value = new String();
	}

	/**
	 * Creates a new MString instance with the value.
	 */
	public MString(MString value) {
		this(value.getValue());
	}
	
	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
	public MString(Node content) {
		setMathMLNode(content);
	}
	
	/**
	 * Returns the value of this MString (i.e. a String).
	 */
  public String getValue() {
    return m_value;
  }

  /**
   * Sets the value of this MString (i.e. a String).
   */
  public void setValue(String newValue) {
    if(newValue != null) {
      m_value = new String(newValue);
    } else {
      m_value = new String();
    }
  }

  public void setMathMLNode(Node n) {
  	if(n.getNodeName().equals("mi")) {
  		setValue(null);
  		setEdited(false);
  		ExerciseObjectFactory.importExerciseAttributes(n, this);
  	} else if(n.getNodeName().equals("mtext")) {
      setValue(n.getFirstChild().getNodeValue());
  		setEdited(true);
  		ExerciseObjectFactory.importExerciseAttributes(n, this);
  	} else if(n.getNodeType() == Node.TEXT_NODE) {
      setValue(n.getNodeValue());
  		setEdited(true);
    } else {
      throw new IllegalArgumentException("Node name must be mtext or node type must be TEXT_NODE but is " + XMLUtils.nodeToString(n, true));
    }
  }

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
  	if(isEdited()) {
	    Element mtext = ExerciseObjectFactory.createNode(this, "mtext", doc);
	    mtext.appendChild(doc.createTextNode(this.toString()));
  		return mtext;
  	} else {
  		return ExerciseObjectFactory.createUneditedNode(this, doc);
  	}
  }

  public String toString() {
    return m_value;
  }

	public boolean isEdited() {
		return m_isEdited;
	}

	public void setEdited(boolean edited) {
		m_isEdited = edited;
		if(edited == false)
			setValue(null);
	}
	
	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	public Class getNumberClass() {
		return getClass();
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}	
}
