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

package net.mumie.mathletfactory.mmobject.algebra.linalg;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.mmobject.util.MatrixEntry;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The base class for 2d and 3d vectors. 
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public abstract class MMDefaultRNVector
	extends NumberVector
	implements MMCanvasObjectIF, ExerciseObjectIF {

	private boolean[] m_isEdited;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;
	
 /**
  *  The origin of this vector. Note that this value is purely used for rendering and
  *  has no mathematical meaning.
  */  
  protected NumberTuple m_rootingPoint;

	private HashSet m_specialCaseListenersList = new HashSet();

	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);
	private final VisualizeInCanvasSupport m_visualizeHelper =
		new VisualizeInCanvasSupport(this);

	private DisplayProperties m_displayProperties = new DisplayProperties();

	private boolean m_isEditable = false;

	/** Returns the dimension of the vector space of this vector. */
	protected int getDimension() {
		return getVectorSpace().getDimension();
	}

  /** Constructs the vector from the given coefficients and vector space. */
	protected MMDefaultRNVector(
		NumberTuple coefficients,
		NumberVectorSpace aSpace) {
		super(coefficients, aSpace);
		setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.VECTOR_DEFAULT));
		m_isEdited = new boolean[getDimension()];
		for(int r = 0; r < m_isEdited.length; r++) {
			m_isEdited[r] = true;
		}
	}

  /** Copy constructor. */
	protected MMDefaultRNVector(NumberVector aNumberVector) {
		super(aNumberVector);
		// check dimension and space of aNumberVector:
		if (aNumberVector.getCoordinatesInSurroundingSpace().getRowCount()
			!= getDimension())
			throw new IllegalArgumentException(
				"NumberVector not of Dimension " + getDimension() + "!");
		setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.VECTOR_DEFAULT));
		m_isEdited = new boolean[getDimension()];
		for(int r = 0; r < m_isEdited.length; r++) {
			m_isEdited[r] = true;
		}
	}
	
	/** Copy constructor. */
	protected MMDefaultRNVector(MMDefaultRNVector vector) {
		super(vector);
		// check dimension and space of aNumberVector:
		if (vector.getCoordinatesInSurroundingSpace().getRowCount()
			!= getDimension())
			throw new IllegalArgumentException(
				"NumberVector not of Dimension " + getDimension() + "!");
		setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.VECTOR_DEFAULT));
		m_isEdited = new boolean[getDimension()];
		for(int r = 0; r < m_isEdited.length; r++) {
			m_isEdited[r] = vector.isEdited(r+1);
		}
	}

	public abstract int getDefaultTransformType();

  /**
   *  Returns the origin of this vector. Note that this value is purely used for rendering and
   *  has no mathematical meaning.
   */  
  public NumberTuple getRootingPoint(){
    return m_rootingPoint;
  }

  /**
   *  Sets the origin of this vector. Note that this value is purely for rendering and
   *  has no mathematical meaning.
   */  
  public void setRootingPoint(NumberTuple rootingPoint){
    m_rootingPoint = rootingPoint;
  }

	public DisplayProperties getDisplayProperties() {
		return m_displayProperties;
	}

	public void setDisplayProperties(DisplayProperties newProperties) {
		m_displayProperties = newProperties;
	}

	public HashMap getDisplayTransformerMap() {
		return m_visualizeHelper.getDisplayTransformerMap();
	}

	public CanvasObjectTransformer getCanvasTransformer() {
		return m_visualizeHelper.getCanvasTransformer();
	}

	public void setCanvasTransformer(int transformType, int visualType) {
		m_visualizeHelper.setCanvasTransformer(transformType, visualType);
	}

	public int getTransformType(JComponent display) {
		return m_visualizeHelper.getTransformType(display);
	}

	public void draw() {
		m_visualizeHelper.draw();
	}

	public void render() {
		m_visualizeHelper.render();
	}

	public MMCanvasObjectIF getAsCanvasContent() {
		return m_visualizeHelper.getAsCanvasContent();
	}

	public boolean isAtScreenLocation(int x, int y) {
		return m_visualizeHelper.isAtScreenLocation(x, y);
	}

	public boolean isVisible() {
		return m_visualizeHelper.isVisible();
	}

	public void setVisible(boolean aFlag) {
		m_visualizeHelper.setVisible(aFlag);
	}

	public MMCanvas getCanvas() {
		return m_visualizeHelper.getCanvas();
	}

	public void setCanvas(MMCanvas aCanvas) {
		m_visualizeHelper.setCanvas(aCanvas);
	}

	public boolean doAction(MMEvent event) {
		return m_interactionHelper.doAction(event);
	}

	public void addHandler(MMHandler aHandler) {
		m_interactionHelper.addHandler(aHandler);
	}

	public MMHandler getActiveHandler() {
		return m_interactionHelper.getActiveHandler();
	}

	public void resetActiveHandler() {
		m_interactionHelper.resetActiveHandler();
	}

	public void addUpdater(MMUpdater anUpdater) {
		m_interactionHelper.addUpdater(anUpdater);
	}

	public void invokeUpdaters() {
		m_interactionHelper.invokeUpdaters();
	}

	public int getHandlerCount() {
		return m_interactionHelper.getHandlerCount();
	}

	public int getUpdaterCount() {
		return m_interactionHelper.getUpdaterCount();
	}

	public boolean isSelectable() {
		return m_interactionHelper.isSelectable();
	}

	public boolean isSelected() {
		return m_interactionHelper.isSelected();
	}

	public void dependsOn(MMObjectIF obj, DependencyIF dependency) {
		m_interactionHelper.dependsOn(obj, dependency);
	}

	public void dependsOn(MMObjectIF[] objects, DependencyIF dependency) {
		m_interactionHelper.dependsOn(objects, dependency);
	}

	public JComponent getAsContainerContent() {
		return m_visualizeHelper.getAsContainerContent();
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer
			.NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM;
	}

	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName().equals(MATRIX_ENTRY)) {
			MNumber newValue = (MNumber)((MatrixEntry)e.getNewValue()).getValue();
			int row = ((MatrixEntry)e.getNewValue()).getRowPosition();
			setCoordinate(row, newValue);
			setEdited(row, true);
			ActionManager.performActionCycleFromObject(this);
		}
	}

	public boolean isEditable() {
		return m_isEditable;
	}

	public void setEditable(boolean editable) {
    if(editable == m_isEditable)
      return;
		m_isEditable = editable;
    render();
	}

	public void addSpecialCaseListener(SpecialCaseListener listener) {
		m_specialCaseListenersList.add(listener);
	}

  public void removeSpecialCaseListener(SpecialCaseListener listener) {
    m_specialCaseListenersList.remove(listener);
  }

	public void fireSpecialCaseEvent(SpecialCaseEvent e) {
		Iterator i = m_specialCaseListenersList.iterator();
		while (i.hasNext())
			 ((SpecialCaseListener) i.next()).displaySpecialCase(e);
	}
  public void setObjectColor(Color color){
    DisplayProperties props = getDisplayProperties(); 
    props.setObjectColor(color);
    setDisplayProperties(props);
  }

  public void setBorderColor(Color color){
    DisplayProperties props = getDisplayProperties(); 
    props.setBorderColor(color);
    setDisplayProperties(props);
  }

  public void setColor(Color color){
    setObjectColor(color);
    setBorderColor(color);  
  }
  
  public String toString(){
    return getCoordinates().toString();
  }

  public void removeAllHandlers() {
    m_interactionHelper.removeAllHandlers();
  }

  public void removeAllUpdaters() {
    m_interactionHelper.removeAllUpdaters();
  }

  public void removeHandler(MMHandler aHandler) {
    m_interactionHelper.removeHandler(aHandler);
  }

  public void removeUpdater(MMUpdater anUpdater) {
    m_interactionHelper.removeUpdater(anUpdater);
  }
    
  /**
   * Returns true if at least one entry was edited by the user.
   */
  public boolean isEdited() {
		for(int row = 1; row <= getDimension(); row++) {
			if(isEdited(row))
				return true;
    }
    return false;
  }
  
  /**
   * Returns if the cell at the specified position was edited.
   */
  public boolean isEdited(int row) {
		return m_isEdited[row-1];
  }
  
  /**
   * Returns true if all entries were edited by the user.
   */
  public boolean isCompletelyEdited() {
		for(int row = 1; row <= getDimension(); row++) {
			if( !isEdited(row))
				return false;
    }
    return true;
  }
  
	/**
	 * Sets the edited-flag for one cell.
	 */
	public void setEdited(int row, boolean edited) {
		m_isEdited[row-1] = edited;
	}

	/**
	 * Sets the edited-flag for all entries.
	 */
	public void setEdited(boolean edited) {
		// No global field "edited" is needed as for setEditable(boolean)
		for(int row = 1; row <= getDimension(); row++) {
			setEdited(row, edited);
		}
	}
  
  public Node getMathMLNode() {
  	return getMathMLNode(XMLUtils.getDefaultDocument());
  }
  
  public Node getMathMLNode(Document doc) {
  	NumberTuple coordinates = getDefaultCoordinates();
		for(int r = 1; r <= coordinates.getDimension(); r++) {
			coordinates.setEdited(r, isEdited(r));
		}
	coordinates.setLabel(getLabel());
  	return coordinates.getMathMLNode(doc);
  }
  
  public void setMathMLNode(Node content) {
  	NumberTuple coordinates = new NumberTuple(getNumberClass(), 1);
  	coordinates.setMathMLNode(content);
  	setDefaultCoordinates(coordinates);
		for(int r = 1; r <= coordinates.getDimension(); r++) {
			setEdited(r, coordinates.isEdited(r));
		}
  }

	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}
	
	public List getHandlers() {
		return this.m_interactionHelper.getHandlers();
	}
}
