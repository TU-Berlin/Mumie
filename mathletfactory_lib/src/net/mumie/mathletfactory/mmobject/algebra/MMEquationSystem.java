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

/*
 * Created on 06.10.2003
 *
 */
package net.mumie.mathletfactory.mmobject.algebra;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

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
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeSupport;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a system of 1 or more equations.
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public class MMEquationSystem implements MMObjectIF {

	private Class m_numberClass;

	Logger logger = Logger.getLogger(MMEquationSystem.class.getName());

	/** Matrix which stores the coefficients for each variable in a row vector.*/
	private NumberMatrix m_coeffMatrix;

	private NumberTuple m_inhomogeneity;

	/** Array containing the variables used in all equations. */
	private List m_variables = new ArrayList();

	/** Field storing the algebraic equations. */
	private SimpleRel[] m_equations;

	public MMEquationSystem(SimpleRel[] equations, Class numberClass) {
		m_numberClass = numberClass;
		m_equations = equations;

		parseRelationsForVariables(equations);
		fillValues(equations);
	}

	/*
	 * Searches the relations for all used variables.
	 * This number is needed for the coeff-matrix's dimension.
	 */
	private void parseRelationsForVariables(SimpleRel[] equations) {
		m_variables.clear();
		for (int i = 0; i < equations.length; i++) {
			String[] variables = getEquation(i + 1).getLeftHandSide().getOpRoot().getContainedVariables();
			for(int j = 0; j < variables.length; j++) {
				if(!m_variables.contains(variables[j]))
					m_variables.add(variables[j]);
			}
		}
	}

	/*
	 * Fill the coeff-matrix and the right side vector with values from the equations.
	 */
	private void fillValues(SimpleRel[] equations) {
		m_coeffMatrix = new NumberMatrix(m_numberClass, equations.length, m_variables.size());
		m_inhomogeneity = new NumberTuple(m_numberClass, equations.length);
		for(int i = 0; i < equations.length; i++) {
			m_inhomogeneity.setEntry(i+1, 1, getEquation(i+1).getRightHandSide().getOpRoot().getResult());
			OpNode[] monoms = getEquation(i + 1).getLeftHandSide().getOpRoot().getChildren();
			for(int j = 0; j < monoms.length; j++) {
				int col = m_variables.indexOf(monoms[j].getContainedVariables()[0]);
				m_coeffMatrix.setEntry(i+1, col+1, monoms[j].getFactor());
			}
		}
	}

	/** Returns the equation at line <code>entry</entry>.
	 * @param entry 1-based index
	 */
	public SimpleRel getEquation(int entry) {
		return m_equations[entry-1];
	}

	/** Return the number of equations of this system. */
	public int getEquationsCount() {
		return m_coeffMatrix.getRowCount();
	}

	/** Returns the number of all used variables. */
	public int getContainedVariablesCount() {
		return m_coeffMatrix.getColumnCount();
	}

	/**
	 * Return the coefficient matrix of this equation system.
	 * The right side vector is not included in this matrix.
	 */
	public NumberMatrix getCoeffMatrix() {
		return m_coeffMatrix;
	}

	/**
	 * Return the right side vector, i.e. the inhomogeneity.
	 */
	public NumberTuple getInhomogeneityVector() {
		return m_inhomogeneity;
	}

	public Class getNumberClass() {
		return m_numberClass;
	}

	/**
	 *  Returns the identifiers of the variables that this equation system contains as an
	 *  alphabetically sorted array.
	 */
	public String[] getContainedVariables(){
		String[] stringArray = new String[m_variables.size()];
		String[] variables = (String[])m_variables.toArray(stringArray);
		Arrays.sort(variables);
		return variables;
	}


	//------------------------------------------------------------


	/**
	 * The list containing all
	 * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s.
	 */
	private HashSet m_specialCaseListenersList = new HashSet();

	/**
	 * This is the helper object, that really implements all methods due to
	 * interaction purposes (handler, updater,...).
	 * MMEquation must implement the same methods (of course), but these can
	 * be implemented generically by invoking the methods of this helper.
	 */
	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);

	private final VisualizeSupport m_visualizeHelper = new VisualizeSupport(this);

	private DisplayProperties m_displayProperties = new DisplayProperties();

	private boolean m_editable = false;

	private String m_label = null;

	/**
	 * This class catches
	 * {@link net.mumie.mathletfactory.mmobject.MMPropertyHandlerIF#RELATION}
	 * property changes.
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
	}

	public DisplayProperties getDisplayProperties() {
		return m_displayProperties;
	}

	public void setDisplayProperties(DisplayProperties newProperties) {
		m_displayProperties = newProperties;
	}

	public void setLabel(String aLabel) {
		m_label = aLabel;
	}

	public String getLabel() {
		return m_label;
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.EQUATION_SYSTEM_TRANSFORM;
	}

	public void setEditable(boolean editable) {
		m_editable = editable;
	}

	public boolean isEditable() {
		return m_editable;
	}

	//following methods should perhaps be moved to the DisplayProperties:
	public HashMap getDisplayTransformerMap() {
		return m_visualizeHelper.getDisplayTransformerMap();
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
		return GeneralTransformer.EQUATION_SYSTEM_TRANSFORM;
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
