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
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.mmobject.util.MatrixEntry;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents an affine 2D point.
 *
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MMAffine2DPoint
	extends Affine2DPoint
	implements MMCanvasObjectIF {

	private HashSet m_specialCaseListenersList = new HashSet();

	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);

	private final VisualizeInCanvasSupport m_visualizeHelper =
		new VisualizeInCanvasSupport(this);

	private DisplayProperties m_displayProperties = new PointDisplayProperties();

	private String m_label = null;

	private boolean m_isEditable = false;


	private int m_ident = 0;

	public MMAffine2DPoint(Class numberEntryClass) {
		this(numberEntryClass, 0, 0);
	}

	public MMAffine2DPoint(Class numberEntryClass, double x, double y) {
		super(numberEntryClass, x, y);
	}

	// constructor necessary because our deepCopy()-method in AffineSpace expects
	// a constructor of type new <mmobject>(mmobject aMMObject)
	public MMAffine2DPoint(MMAffine2DPoint aPoint) {
		super(aPoint);
	}

  /** Sets an index to this point, useful for handling dynamically created points. */
	public void setIdentitfier(int i) {
		m_ident = i;
	}

  /** Returns the index of this point, useful for handling dynamically created points. */
	public int getIdentifier() {
		return m_ident;
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

	public JComponent getAsContainerContent() {
		return m_visualizeHelper.getAsContainerContent();
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
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

  /** Returns the centre point of the two given points. */
	public static MMAffine2DPoint getCentrePoint(
		MMAffine2DPoint p,
		MMAffine2DPoint q) {
		if (p.getNumberClass().equals(q.getNumberClass())) {
			if (p.isInfinity() || q.isInfinity())
				throw new TodoException("not implemented for points that are at infinity");
			else {
				MNumber two = NumberFactory.newInstance(p.getNumberClass(), 2);
				NumberTuple pc =
					(NumberTuple) p.getAffineCoordinatesOfPoint().deepCopy();
				NumberTuple qc = q.getAffineCoordinatesOfPoint();
				pc.addTo(qc).multWithNumber(two.inverse());
				MMAffine2DPoint centre = new MMAffine2DPoint(p.getNumberClass());
				return (MMAffine2DPoint) centre.setAffineCoordinates(pc);
			}
		}
		else
			throw new IllegalArgumentException("both points must have same entry type");
	}

	/**
	 * This class catches
	 * {@link #MATRIX_ENTRY}
	 * property changes.
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if(e.getPropertyName().equals(MATRIX_ENTRY)) {
			MNumber newValue = (MNumber)((MatrixEntry)e.getNewValue()).getValue();
			int row = ((MatrixEntry)e.getNewValue()).getRowPosition();
			if(row == 1)
				setX(newValue);
			else // row = 2
				setY(newValue);
//			setAffineCoordinates(new NumberTuple((NumberMatrix) e.getNewValue()));
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

	public Rectangle2D getWorldBoundingBox() {
		return new Rectangle2D.Double(
			getAffineCoordinatesOfPoint().getEntry(1).getDouble(),
			getAffineCoordinatesOfPoint().getEntry(2).getDouble(),
			0,
			0);
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

  /** Returns the coordinates of this points, e.g. "[3,-4]". */
	public String toString() {
		return "[" + this.getXAsDouble() + "," + this.getYAsDouble() + "]";
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
