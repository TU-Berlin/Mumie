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

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeSupport;
import net.mumie.mathletfactory.mmobject.util.MatrixEntry;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

import org.w3c.dom.Node;

/**
 * Class implementing the MMObject for a {@link net.mumie.mathletfactory.math.algebra.linalg.NumberTuple}.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMNumberTuple extends NumberTuple implements MMObjectIF {

	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);
	private final VisualizeSupport m_visualizeHelper = new VisualizeSupport(this);

	/**
	 * The list containing all
	 * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s.
	 */
	private HashSet m_specialCaseListenersList = new HashSet();

	private DisplayProperties m_displayProperties = new DisplayProperties();
	private boolean m_editable = false;
	private boolean m_isRealType = false;

	/**
	 * Class constructor which creates a vector with the number entries of type
	 * <code>numberClass</code> and the elements in <code>entries</code>.
	 * The user has to care for consistency of the number type of the elements
	 * in <code>entries</code>.
	 */
	public MMNumberTuple(Class numberClass, Object[] entries) {
		super(numberClass, entries);
	}

	/**
	 * Class constructor which creates a zero vector with entries of number type
	 * <code>numberClass</code> and with dimension <code>dimension</code>.
	 */
	public MMNumberTuple(Class numberClass, int dimension) {
		super(numberClass, dimension);
	}

	/**
	 * Class constructor which creates a 2-dim vector with entries of number type
	 * <code>numberClass</code> and coordinates <code>x</code> and <code>y</code>.
	 */
	public MMNumberTuple(Class numberClass, double x, double y) {
		super(numberClass, x, y);
	}

	/**
	 * Class constructor which creates a 3-dim vector with entries of number type
	 * <code>numberClass</code> and coordinates <code>x</code>, <code>y</code>,
	 * and <code>z</code>.
	 */
	public MMNumberTuple(Class numberClass, double x, double y, double z) {
		super(numberClass, x, y, z);
	}

	/**
	 * Class constructor which creates a vector that is a deep copy of
	 * <code>aVector</code>.
	 */
	public MMNumberTuple(NumberTuple aVector) {
		super(aVector);
	}

	/**
	 * Class constructor which creates a vector that is a deep copy of
	 * <code>aVector</code>.
	 */
	public MMNumberTuple(MMNumberTuple aVector) {
		super(aVector);
		setDisplayProperties(aVector.getDisplayProperties());
	}

	/**
	 * Class constructor which creates a vector that is a deep copy of
	 * <code>aOneColumnMatrix</code>.
	 * @throws IllegalArgumentException if <code>aOneColumnMatrix</code>
	 * has more than one column.
	 */
	public MMNumberTuple(NumberMatrix aOneColumnMatrix) {
		super(aOneColumnMatrix);
	}

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param mathMLnode a MathML/XML node
   * @throws Exception when an error while instantiation occurrs
	 */
  public MMNumberTuple(Node content) throws Exception {
    super(content);
  }

	/*
	 * @see net.mumie.mathletfactory.mmobject.MMObjectIF#getAsContainerContent()
	 */
	public JComponent getAsContainerContent() {
		return m_visualizeHelper.getAsContainerContent();
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
	}

	public boolean isEditable() {
		return m_editable;
	}

	public void setEditable(boolean isEditable) {
    if(isEditable == m_editable)
      return;
		m_editable = isEditable;
    render();
	}

	public void setDisplayProperties(DisplayProperties newProperties) {
		m_displayProperties = newProperties;
	}

	public DisplayProperties getDisplayProperties() {
		return m_displayProperties;
	}

	public HashMap getDisplayTransformerMap() {
		return m_visualizeHelper.getDisplayTransformerMap();
	}

	public int getTransformType(JComponent display) {
		return m_visualizeHelper.getTransformType(display);
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.NUMBER_MATRIX_TRANSFORM;
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.NUMBER_MATRIX_TRANSFORM;
	}

	public void render() {
		m_visualizeHelper.render();
	}

	public void draw() {
		m_visualizeHelper.draw();
	}

	public boolean doAction(MMEvent event) {
		return m_interactionHelper.doAction(event);
	}

	public void addHandler(MMHandler aHandler) {
		m_interactionHelper.addHandler(aHandler);
	}

	public int getHandlerCount() {
		return m_interactionHelper.getHandlerCount();
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

	public int getUpdaterCount() {
		return m_interactionHelper.getUpdaterCount();
	}

	public void invokeUpdaters() {
		m_interactionHelper.invokeUpdaters();
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

	public Class getNumberClass() {
		Class result = super.getNumberClass();
		if ((m_isRealType) && (result == MNumber.class))
			result = MRealNumber.class;
		return result;
	}

  /**
   * This method only listens to NUMBERMATRIX-change, i.e. only the whole matrix can be updated from the drawable
   * not only a single number.
   */
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(MATRIX_ENTRY)) {
			MNumber newValue = (MNumber)((MatrixEntry)evt.getNewValue()).getValue();
			int row = ((MatrixEntry)evt.getNewValue()).getRowPosition();
			int col = ((MatrixEntry)evt.getNewValue()).getColumnPosition();
			setEntry(row, col, newValue);
			((MNumber)getEntryRef(row,col)).setEdited(true);
		}
		invokeUpdaters();
	}

	/** The <code>listener</code> will be informed if this handler produces a special case.*/
	public void addSpecialCaseListener(SpecialCaseListener listener) {
		m_specialCaseListenersList.add(listener);
	}

  public void removeSpecialCaseListener(SpecialCaseListener listener) {
    m_specialCaseListenersList.remove(listener);
  }

	/** Informs all registered special case listeners, that the given event has occurred. */
	public void fireSpecialCaseEvent(SpecialCaseEvent e) {
		Iterator i = m_specialCaseListenersList.iterator();
		while (i.hasNext())
			 ((SpecialCaseListener) i.next()).displaySpecialCase(e);
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

  public boolean isVisible() {
    return m_visualizeHelper.isVisible();
  }

  public void setVisible(boolean aFlag) {
    m_visualizeHelper.setVisible(aFlag);
  }
  
  public List getHandlers() {
		return this.m_interactionHelper.getHandlers();
  }
}
