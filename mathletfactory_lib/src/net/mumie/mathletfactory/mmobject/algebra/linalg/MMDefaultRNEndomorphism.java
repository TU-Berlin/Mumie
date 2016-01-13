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

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.LinearMap;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeSupport;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents an endomorphismus in R<sup>n</sup>, i.e. a
 * {@link net.mumie.mathletfactory.math.algebra.linalg.LinearMap} from R<sup>n</sup>
 * into itsself.
 *
 *  @author vossbeck, klich
 *  @mm.docstatus finished
 */
public abstract class MMDefaultRNEndomorphism
	extends LinearMap
	implements MMObjectIF {

	/**
	 * The list containing all
	 * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s.
	 */
	private HashSet m_specialCaseListenersList = new HashSet();

	/**
	 * This is the helper object, that really implements all methods due to
	 * interaction purposes (handler, updater,...).
	 * MMLinearMap must implement the same methods (of course), but these can
	 * be implemented generically by invoking the methods of this helper.
	 */
	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);
	private final VisualizeSupport m_visualizeHelper = new VisualizeSupport(this);

	//TODO: define appropriate properties for linear maps!
	private DisplayProperties m_displayProperties = new DisplayProperties();

	private boolean m_editable = false;


	private String m_label = null;

	/**
	 * constructs an <code>MMDefaultREndomorphism</code> that is a
	 * {@link net.mumie.mathletfactory.algebra.LinearMap} from <code>aRNSpace</code>
	 * into itsself. It is initialized as identity. Use method
	 * {@link #setDefaultMatrixRepresentation} to change this <code>
	 * MMDefaultREndomorphism</code>.
	 */
	public MMDefaultRNEndomorphism(MMDefaultRN aSpace) {
		super(aSpace);
	}

	/**
	 * constructs an <code>MMDefaultRNEndomorphism</code> that is a
	 * {@link net.mumie.mathletfactory.algebra.LinearMap} from an instance of
	 * {@link net.mumie.mathletfactory.mmobject.algebra.MMDefaultRN} to itself and that
	 * maps domain[i] to range[i] (i=1,...,n).
	 */
	public MMDefaultRNEndomorphism(
		MMDefaultRNVector[] domain,
		MMDefaultRNVector[] range) {
		super(domain, range);
		//TODO: set appropriate display properties for linear map:
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

	public abstract int getDefaultTransformType();

	public void setEditable(boolean editable) {
    if(editable == m_editable)
      return;
		m_editable = editable;
    render();
	}

	public boolean isEditable() {
		return m_editable;
	}

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
		return isSelected();
	}

	public void dependsOn(MMObjectIF obj, DependencyIF dependency) {
		m_interactionHelper.dependsOn(obj, dependency);
	}

	public void dependsOn(MMObjectIF[] objects, DependencyIF dependency) {
		m_interactionHelper.dependsOn(objects, dependency);
	}

	public void propertyChange(PropertyChangeEvent e) {
		if (!e.getPropertyName().equals(NUMBERMATRIX))
			return;
		super.setDefaultMatrixRepresentation((NumberMatrix) e.getNewValue());
		ActionManager.performActionCycleFromObject(this);
	}

	public JComponent getAsContainerContent() {
		return m_visualizeHelper.getAsContainerContent();
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.LINEAR_MAP_TO_MATRIX_TRANSFORM;
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
