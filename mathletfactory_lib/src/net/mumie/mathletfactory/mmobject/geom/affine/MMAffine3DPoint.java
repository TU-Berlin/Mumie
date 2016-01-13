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

package net.mumie.mathletfactory.mmobject.geom.affine;

import java.awt.Color;
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
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents a point in the affine 3d space.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMAffine3DPoint extends Affine3DPoint implements MMCanvasObjectIF {

	/**
	 * The list containing all
	 * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s.
	 */
	private HashSet m_specialCaseListenersList = new HashSet();

	/**
	 * This is the helper object, that really implements all methods responsible
	 * for interaction (communicating with handler, updater,...).
	 * MMLinearMap must implement the same methods (of course), but these can
	 * be implemented generically by invoking the methods of this helper.
	 */
	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);

	private final VisualizeInCanvasSupport m_visualizeHelper =
		new VisualizeInCanvasSupport(this);

	private DisplayProperties m_displayProperties = new PointDisplayProperties();

	private String m_label = null;

	private boolean m_isEditable = false;


	/**
	 * Creates an affine point in a three-dimensional space.
	 * Coordinates are based on a number class <code>entryClass</code> and are
	 * initialized to zero.
	 */
	public MMAffine3DPoint(Class numberEntryClass) {
		this(numberEntryClass, 0, 0, 0);
	}

	/**
	 * Creates the affine 3d point for the given number class and coordinates.
	 */
	public MMAffine3DPoint(
		Class numberEntryClass,
		double x,
		double y,
		double z) {
		super(numberEntryClass, x, y, z);
	}

	/**
	 * Copy constructor.
	 */
	public MMAffine3DPoint(MMAffine3DPoint aPoint) {
		super(aPoint);
	}

	/**
	 * Creates the affine point with the given projective coordinates.
	 * @see net.mumie.mathletfactory.math.geom.affine.AffinePoint#AffinePoint(NumberTuple)
	 */
	public MMAffine3DPoint(NumberTuple projectiveCoordinates) {
		super(projectiveCoordinates);
	}

	/**
	 * This class catches
	 * {@link #NUMBERMATRIX}
	 * property changes.
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (!e.getPropertyName().equals(NUMBERMATRIX))
			return;
		setAffineCoordinates(new NumberTuple((NumberMatrix) e.getNewValue()));
		ActionManager.performActionCycleFromObject(this);
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
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
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
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
	}

	public boolean isEditable() {
		return m_isEditable;
	}

	public void setEditable(boolean editable) {
		m_isEditable = editable;
	}

	public Rectangle2D getWorldBoundingBox() {
		return null;
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

  /** Sets the object fill color (without border). */
  public void setObjectColor(Color color){
    DisplayProperties props = getDisplayProperties();
    props.setObjectColor(color);
    setDisplayProperties(props);
  }

  /** Sets the object border color. */
  public void setBorderColor(Color color){
    DisplayProperties props = getDisplayProperties();
    props.setBorderColor(color);
    setDisplayProperties(props);
  }

  /** Sets both the object's fill and border color. */
  public void setColor(Color color){
    setObjectColor(color);
    setBorderColor(color);
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