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

package net.mumie.mathletfactory.mmobject.set;

import java.awt.geom.Rectangle2D;
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
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a simple set with a starting and an ending value.
 * 
 * @author Markus Gronau
 * @mm.docstatus finished
 */
public class MMInterval extends Interval implements MMCanvasObjectIF {

	/**
	 * The list containing all 
	 * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s. 
	 */
	private HashSet m_specialCaseListenersList = new HashSet();

	private boolean m_isEditable = false;
	private boolean m_isLowerBoundaryEditable = true;
	private boolean m_isUpperBoundaryEditable = true;

	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);

	//NICKI:
	//	private final VisualizeSupport m_visualizeHelper =
	//		new VisualizeSupport(this);

	private final VisualizeInCanvasSupport m_visualizeHelper =
		new VisualizeInCanvasSupport(this);

	private DisplayProperties m_displayProperties = new DisplayProperties();

	private String m_label = null;

	public MMInterval(
		Class numberClass,
		double lowBoundVal,
		boolean lowerBoundType,
		double upBoundVal,
		boolean upperBoundType,
    boolean form) {
		super(
			numberClass,
			lowBoundVal,
			lowerBoundType,
			upBoundVal,
			upperBoundType,
      form);
	}

	public MMInterval(
		MRealNumber lowBoundVal,
		boolean lowerBoundType,
		MRealNumber upBoundVal,
		boolean upperBoundType,
    boolean form) {
		super(lowBoundVal, lowerBoundType, upBoundVal, upperBoundType, form);
	}

	public MMInterval(Class numberClass, String intervalString) {
		super(numberClass, intervalString);
	}

	public MMInterval(Interval interval) {
		this(
			(MRealNumber) interval.getLowerBoundaryVal(),
			interval.getLowerBoundaryType(),
			(MRealNumber) interval.getUpperBoundaryVal(),
			interval.getUpperBoundaryType(),
      interval.getForm());
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
		return GeneralTransformer.INTERVAL_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.INTERVAL_TRANSFORM;
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

	public JComponent getAsContainerContent() {
		return m_visualizeHelper.getAsContainerContent();
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.INTERVAL_TRANSFORM;
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

	public boolean isEditable() {
		return m_isEditable;
	}

	public void setEditable(boolean isEditable) {
		if (isEditable == m_isEditable)
			return;
		m_isEditable = isEditable;
		render();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(INTERVAL_LOW_BOUND_VALUE)) {
			setLowerBoundaryVal((MNumber) evt.getNewValue());
			ActionManager.performActionCycleFromObject(this);
		}
		else if (
			evt.getPropertyName().equals(INTERVAL_LOW_BOUND_TYPE)) {
			setLowerBoundaryType(((Boolean) evt.getNewValue()).booleanValue());
			ActionManager.performActionCycleFromObject(this);
		}
		else if (evt.getPropertyName().equals(INTERVAL_UP_BOUND_VALUE)) {
			setUpperBoundaryVal((MNumber) evt.getNewValue());
			ActionManager.performActionCycleFromObject(this);
		}
		else if (
			evt.getPropertyName().equals(INTERVAL_UP_BOUND_TYPE)) {
			setUpperBoundaryType(((Boolean) evt.getNewValue()).booleanValue());
			ActionManager.performActionCycleFromObject(this);
		}
	}

	public void setLowerBoundaryEditable(boolean editable) {
		m_isLowerBoundaryEditable = editable;
	}

	public boolean isLowerBoundaryEditable() {
		return m_isLowerBoundaryEditable;
	}

	public void setUpperBoundaryEditable(boolean editable) {
		m_isUpperBoundaryEditable = editable;
	}

	public boolean isUpperBoundaryEditable() {
		return m_isUpperBoundaryEditable;
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

	//NICKI:
	public MMCanvas getCanvas() {
		return m_visualizeHelper.getCanvas();
	}

	public void setCanvas(MMCanvas aCanvas) {
		m_visualizeHelper.setCanvas(aCanvas);
	}

	public boolean isAtScreenLocation(int x, int y) {
		return m_visualizeHelper.isAtScreenLocation(x, y);
	}

	public MMCanvasObjectIF getAsCanvasContent() {
		return m_visualizeHelper.getAsCanvasContent();
	}

	public Rectangle2D getWorldBoundingBox() {
		return null;
	} // TODO: to be implemented

	public void setVisible(boolean aFlag) {
		m_visualizeHelper.setVisible(aFlag);
	}

	public void setCanvasTransformer(int transformType, int visualType) {
		m_visualizeHelper.setCanvasTransformer(transformType, visualType);
	}

	public CanvasObjectTransformer getCanvasTransformer() {
		return m_visualizeHelper.getCanvasTransformer();
	}

	public boolean isVisible() {
		return m_visualizeHelper.isVisible();
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
  
  public List getHandlers() {
	return this.m_interactionHelper.getHandlers(); 
  }
}
