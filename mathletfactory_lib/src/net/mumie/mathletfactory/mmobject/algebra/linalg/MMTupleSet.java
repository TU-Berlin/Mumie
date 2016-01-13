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

import org.w3c.dom.Node;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.TupleSet;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeSupport;
import net.mumie.mathletfactory.mmobject.util.MatrixEntry;
import net.mumie.mathletfactory.mmobject.util.TupleSetEntry;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class is the MM-class for the TupleSet-class.
 * 
 * It represents a number tuple set. A TupleSet-object is defined by the
 * the sum of vectors (NumberTuple of MOpNumber) muliplied by scalars (MOpNumber) 
 * and the definition of the variables (MOpNumber).
 * 
 * e.g.   /   /1\   /b\ |         \
 * 		M = | a*|0| + |2| | a,b in C|
 * 		    \   \1/   \1/ |         / 
 *
 * @author heimann
 * @mm.docstatus finished
 */
public class MMTupleSet extends TupleSet implements MMObjectIF {
	//todo: seperate factor-enabled flags (for each component), analog in MMTupleSetPanel
	
	private DisplayProperties m_displayProperties = new DisplayProperties();
	private boolean m_editable = false, m_buttonsEnabled = false;
	private final VisualizeSupport m_visualizeHelper = new VisualizeSupport(this);
	private final InteractivitySupport m_interactionHelper = new InteractivitySupport(this);
	private HashSet m_specialCaseListenersList = new HashSet();
	
	/**
	 * creates a new MMTupleSet
	 * @param numberClass: number class internally used for the MOpNumbers
	 * @param components: 	number of the left-hand side components (vectors, factors)
	 * @param variables:	number of the right-hand side components (variables) 
	 * @param dimension:	dimension of the vectors used in the tuple set 
	 */
	public MMTupleSet(Class numberClass, int components, int variables, int dimension){
		super(numberClass, components, variables, dimension);
	}
	
	/**
	 * creates a new MMTupleSet 
	 * @param numberClass: number class internally used for the MOpNumbers 
	 * @param dimension:	dimension of the vectors used in the tuple set 
	 */
	public MMTupleSet(Class numberClass, int dimension){
		super(numberClass, dimension);
	}
	
	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param mathMLnode a MathML/XML node
	 * @throws Exception when an error while instantiation occurrs
	 */
  public MMTupleSet(Node content) throws Exception {
  	super(content);
  }
	
	/**
	 * 
	 * @return true, if buttons are visible
	 */
	public boolean isButtonsEnabled(){
		return m_buttonsEnabled;
	}


	 /** 
	 * if true, buttons are visible
	 */
	public void setButtonsEnabled(boolean aFlag){
		m_buttonsEnabled=aFlag;
	}
	
	public void addSpecialCaseListener(SpecialCaseListener listener) {
		m_specialCaseListenersList.add(listener);
	}

	public void fireSpecialCaseEvent(SpecialCaseEvent e) {
		Iterator i = m_specialCaseListenersList.iterator();
		while (i.hasNext())
			 ((SpecialCaseListener) i.next()).displaySpecialCase(e);
	}

	public DisplayProperties getDisplayProperties() {
		return m_displayProperties;
	}
	
	public void setDisplayProperties(DisplayProperties newProperties) {
		m_displayProperties = newProperties;
	}

	public boolean isEditable() {
		return m_editable;
	}

	public void removeSpecialCaseListener(SpecialCaseListener listener) {
	    m_specialCaseListenersList.remove(listener);
	}

	public void setEditable(boolean isEditable) {
	    if(isEditable == m_editable)
	      return;
		m_editable = isEditable;
		if(m_visualizeHelper!=null)render();
	}

	public void setDimension(int dimension){
		super.setDimension(dimension);
		if(m_visualizeHelper!=null)render();
	}
	
	public void draw() {
		m_visualizeHelper.draw();
	}

	public JComponent getAsContainerContent() {
		return m_visualizeHelper.getAsContainerContent();
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.SET_TRANSFORM;
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.SET_TRANSFORM;
	}

	public HashMap getDisplayTransformerMap() {
		return m_visualizeHelper.getDisplayTransformerMap();
	}

	public int getTransformType(JComponent display) {
		return m_visualizeHelper.getTransformType(display);
	}

	public boolean isVisible() {
	    return m_visualizeHelper.isVisible();
	}

	public void render() {
		m_visualizeHelper.render();
	}

	public void setVisible(boolean aFlag) {
	    m_visualizeHelper.setVisible(aFlag);
	}

	public void addHandler(MMHandler aHandler) {
		m_interactionHelper.addHandler(aHandler);
	}

	public void addUpdater(MMUpdater anUpdater) {
		m_interactionHelper.addUpdater(anUpdater);
	}

	public void dependsOn(MMObjectIF obj, DependencyIF dependency) {
		m_interactionHelper.dependsOn(obj, dependency);
	}

	public void dependsOn(MMObjectIF[] objects, DependencyIF dependency) {
		m_interactionHelper.dependsOn(objects, dependency);
	}

	public boolean doAction(MMEvent event) {
		return m_interactionHelper.doAction(event);
	}

	public MMHandler getActiveHandler() {
		return m_interactionHelper.getActiveHandler();
	}

	public int getHandlerCount() {
		return m_interactionHelper.getHandlerCount();
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

	public void resetActiveHandler() {
		m_interactionHelper.resetActiveHandler();
	}

	public List getHandlers() {
		return this.m_interactionHelper.getHandlers();
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(SET_ENTRY)){
			int type = ((TupleSetEntry)evt.getNewValue()).getType();
			if(type==TupleSetEntry.ADDING_OR_REMOVING_COMPONENT){
				switch (((TupleSetEntry)evt.getNewValue()).getIndex()) {
				case TupleSetEntry.REMOVING_LEFT_COMPONENT:
					removeLeftComponent();
					break;
				case TupleSetEntry.ADDING_LEFT_COMPONENT:
					addLeftComponent();
					break;
				case TupleSetEntry.REMOVING_RIGHT_COMPONENT:
					removeRightComponent();
					break;
				case TupleSetEntry.ADDING_RIGHT_COMPONENT:
					addRightComponent();
					break;
				case TupleSetEntry.ENABLING_FACTORS:
					setFactorsEnabled(true);
					break;
				case TupleSetEntry.DISABLING_FACTORS:
					setFactorsEnabled(false);
					break;
				}
				ActionManager.performActionCycleFromObject(this);
				return;
			}
			int index = ((TupleSetEntry)evt.getNewValue()).getIndex();
			if(type==TupleSetEntry.FACTOR){
				getFactors()[index].setOperation((Operation)((TupleSetEntry)evt.getNewValue()).getValue());
				if(!getFactors()[index].isEdited())getFactors()[index].setEdited(true);
			}
			if(type==TupleSetEntry.VARIABLE){
				getRightVariables()[index].setOperation((Operation)((TupleSetEntry)evt.getNewValue()).getValue());
				if(!getRightVariables()[index].isEdited())getRightVariables()[index].setEdited(true);
			}
			if(type==TupleSetEntry.VECTOR_ENTRY){
				int vectorIndex = ((MatrixEntry)((TupleSetEntry)evt.getNewValue()).getValue()).getRowPosition();
				getVectors()[index].setEntry(vectorIndex,(MOpNumber)((MatrixEntry)((TupleSetEntry)evt.getNewValue()).getValue()).getValue());
				if(!getVectors()[index].getEntryRef(vectorIndex).isEdited())getVectors()[index].getEntryRef(vectorIndex).setEdited(true);
			}
			ActionManager.performActionCycleFromObject(this);
		}

	}

}
