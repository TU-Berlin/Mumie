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

import java.awt.geom.Rectangle2D;
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
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a finite 3D line segment.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMAffine3DLineSegment
	extends MMAffine3DPolygon
	implements MMCanvasObjectIF {

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

	private final VisualizeInCanvasSupport m_visualizeHelper =
		new VisualizeInCanvasSupport(this);

	private String m_label = null;

	private boolean m_isEditable = false;

	public MMAffine3DLineSegment(Class entryClass) {
		this(
			new MMAffine3DPoint(entryClass, 0, 0, 0),
			new MMAffine3DPoint(entryClass, 0, 0, 1));
	}

	public MMAffine3DLineSegment(
		MMAffine3DPoint initialPoint,
		MMAffine3DPoint endPoint) {      
		super(new MMAffine3DPoint[]{initialPoint, endPoint});
    setDisplayProperties(new LineDisplayProperties());
	}

	public MMAffine3DLineSegment(
		Class entryClass,
		double x1,
		double y1,
    double z1,
		double x2,
		double y2,
    double z2) {
      this(new MMAffine3DPoint(entryClass, x1, y1, z1),
          new MMAffine3DPoint(entryClass, x2, y2, z2));
    }

	public MMAffine3DLineSegment(
		Class entryClass,
		double x1,
		double y1,
    double z1,
		double x2,
		double y2,
    double z2,
		LineDisplayProperties displayProperties) {
		this(entryClass, x1, y1, z1, x2, y2, z2);
    setDisplayProperties(displayProperties);
	}

  /** Returns the first endpoint of this line segment. */
  public MMAffine3DPoint getInitialPoint() {
    return (MMAffine3DPoint) getVertexRef(0);
  }

  /** Sets the first endpoint of this line segment. */
  public MMAffine3DLineSegment setInitialPoint(MMAffine3DPoint anAffine3DPoint) {
    if (anAffine3DPoint.getNumberClass().equals(getNumberClass())) {
      setVertexRef(0, anAffine3DPoint);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "entry class of anAffine3DPoint and the "
          + "Affine3DLineSegment must coincide");
  }

  /** Sets the first endpoint of this line segment. */
  public MMAffine3DLineSegment setInitialPoint(double x, double y, double z) {
    getVertexRef(0).setXYZ(x, y, z);
    return this;
  }

  /** Returns the second endpoint of this line segment. */
  public MMAffine3DPoint getEndPoint() {
    return (MMAffine3DPoint) getVertexRef(1);
  }

  /** Sets the second endpoint of this line segment. */
  public MMAffine3DLineSegment setEndPoint(MMAffine3DPoint
   anAffine3DPoint) {
    if (anAffine3DPoint.getNumberClass().equals(getNumberClass())) {
      setVertexRef(1, anAffine3DPoint);
      return this;
    }
    else
      throw new IllegalArgumentException(
        "entry class of anAffine3DPoint and the "
          + "Affine3DLineSegement must coincide");
  }

  /** Sets the second endpoint of this line segment. */
  public MMAffine3DLineSegment setEndPoint(double x, double y, double z) {
    getVertexRef(1).setXYZ(x, y, z);
    return this;
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
		return GeneralTransformer.NO_TRANSFORM_TYPE;
	}

	public boolean isEditable() {
		return m_isEditable;
	}

	public void setEditable(boolean editable) {
		if (editable == m_isEditable)
			return;
		m_isEditable = editable;
		render();
	}

  /** Empty implementation (yet). */
	public void propertyChange(PropertyChangeEvent e) {
	}

	public Rectangle2D getWorldBoundingBox() {
		return null;
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
	
	public List getHandlers() {
		return this.m_interactionHelper.getHandlers();
	}
}
