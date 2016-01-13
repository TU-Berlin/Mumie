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
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.OpMatrix;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeSupport;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * Class implementing the MMObject for a {@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix}.
 *
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMOpMatrix extends OpMatrix implements MMObjectIF {

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
		private String m_label = null;
	private boolean m_isRealType = false;

	/**
	 * Class constructor which constructs a <code>MMOpMatrix</code> that is a deep copy of
	 * <code>matrix</code>.
	 */
	public MMOpMatrix(MMOpMatrix matrix) {
		super(matrix);
	}

  /** For convenience: Produces a mxn double matrix with the given (row-wise) entries as values. */
  public MMOpMatrix(int numOfCols, int numOfRows, double[] values){
    super(numOfCols, numOfRows, values);
  }

	/**
	 * Class constructor which constructs a <code>MMOpMatrix</code> with
	 * <code>numOfCols</code> number
	 * of columns and with the number entries of type <code>numberClass</code>. The
	 * numbers are the elements in <code>entries</code> and the user has to care
	 * for consistency of their number type. <code>numOfCols</code> will (of course)
	 * equal the length of any row in the <code>NumberMatrix</code> and by that the
	 * number of rows will equal entries.length / <code>numOfCols</code>. Again the
	 * user has to care for the fact that entries.length must be a multiple of
	 * <code>numOfCols</code>. The object array itsself is treated &quot;rowwise&quot;.
	 */
	public MMOpMatrix(Class numberClass, int numOfCols, Object[] entries) {
		super(numberClass, numOfCols, entries);
	}

	/**
	 * Class constructor which constructs a zero <code>MMNumberMatrix</code>
	 * with entries of number type
	 * <code>numberClass</code> and with <code>numOfCols</code> columns and
	 * <code>numOfRows</code> rows.
	 */
	public MMOpMatrix(Class numberClass, int numOfCols, int numOfRows) {
		super(numberClass, numOfCols, numOfRows, true);
	}

	/**
	 * Class constructor which constructs a zero square <code>MMNumberMatrix</code>
	 * with entries of number type
	 * <code>numberClass</code> and of desired dimension.
	 */
	public MMOpMatrix(Class numberClass, int dimension) {
		super(numberClass, dimension, true);
	}

	/**
	 * Class constructor which constructs a <code>MMNumberMatrix</code> of number type
	 * <code>numberClass</code> and with
	 * the {@link NumberTuple} array treated as rows or columns for this
	 * <code>NumberMatrix</code> in dependence of the <code>type</code> that may be
	 * set to either {@link #NUMBERTUPEL_ROWWISE} or {@link #NUMBERTUPEL_COLUMNWISE}.
	 */
	public MMOpMatrix(Class numberClass, int type, NumberTuple[] rowcol) {
		super(numberClass, type, rowcol);
	}

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

	public String getLabel() {
		return m_label;
	}

	public void setLabel(String label) {
		m_label = label;
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
		return isSelected();
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
		if (!evt.getPropertyName().equals(NUMBERMATRIX))
			return;
		super.copyFrom(((OpMatrix) evt.getNewValue()).deepCopy());
		invokeUpdaters();
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
