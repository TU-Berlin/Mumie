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
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.geom.affine.Affine2DLine;
import net.mumie.mathletfactory.math.geom.affine.AffineSpace;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents an affine 2D line of infinite length. 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class MMAffine2DLine extends Affine2DLine implements MMCanvasObjectIF, MMAffineSubspaceIF {

	private HashSet m_specialCaseListenersList = new HashSet();

	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);

	private final VisualizeInCanvasSupport m_visualizeHelper =
		new VisualizeInCanvasSupport(this);

	private DisplayProperties m_displayProperties = new LineDisplayProperties();

	private String m_label = "G";

	private boolean m_isEditable = false;

  /** Constructs a line running through the given points. */
	public MMAffine2DLine(
		MMAffine2DPoint initialPoint,
		MMAffine2DPoint endPoint) {
		super(initialPoint, endPoint);
    m_displayProperties.setLabelDisplayed(false);
	}

  /** Constructs a line running through the given points. */
	public MMAffine2DLine(
		Class entryClass,
		double x1,
		double y1,
		double x2,
		double y2) {
		super(entryClass, x1, y1, x2, y2);
    m_displayProperties.setLabelDisplayed(false);
	}

  /** Copy constructor. */
  public MMAffine2DLine(MMAffine2DLine line) {
    this(
      line.getNumberClass(),
      line.getAffineCoordinatesOfInitialPoint().getEntry(1).getDouble(),
      line.getAffineCoordinatesOfInitialPoint().getEntry(2).getDouble(),
      line.getAffineCoordinatesOfEndPoint().getEntry(1).getDouble(),
      line.getAffineCoordinatesOfEndPoint().getEntry(2).getDouble());
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
		return m_visualizeHelper.getAsContainerContent(getDefaultTransformTypeAsContainer());
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.FUNCTION_TRANSFORM;
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

  /** Listens exclusively for OPERATION or ORIGIN_VECTOR or DIRECTION_VECTOR events and adjusts itself to the new value. */
	public void propertyChange(PropertyChangeEvent e) {
    //System.out.println(e.getPropertyName()+" changed:"+e.getOldValue()+" to "+e.getNewValue());
    if (e.getPropertyName().equals(OPERATION)){    
      setOperation(new Operation((Operation) e.getNewValue()));
      //System.out.println("changed:"+e.getOldValue()+" to "+e.getNewValue());
    } else if(e.getPropertyName().equals(ORIGIN_VECTOR) || e.getPropertyName().equals(DIRECTION_VECTOR)){    
      //System.out.println("changed:"+e.getOldValue()+" to "+e.getNewValue()+" this is "+this);
      // check if the "rooting point" has changed:
      NumberTuple[] spanningPoints = getAffineCoordinates();
      if(e.getPropertyName().equals(ORIGIN_VECTOR)){      
        spanningPoints[0] = new NumberTuple((NumberMatrix)e.getNewValue());
        spanningPoints[1].addTo(spanningPoints[0].deepCopy().subFrom((NumberMatrix)e.getOldValue()));        
      } else{      
        // update second point by adding  direction vector to first point
        //System.out.println(spanningPoints[1]+" + "+e.getNewValue());
        spanningPoints[1] = (NumberTuple)spanningPoints[0].deepCopy().addTo((NumberMatrix)e.getNewValue());
        //System.out.println(" = "+spanningPoints[1]);
      }
      setAffineCoordinates(spanningPoints);
    } else
      return;
    ActionManager.performActionCycleFromObject(this);    
	}

	public Rectangle2D getWorldBoundingBox() {
		NumberTuple[] nt = this.getAffineCoordinates();
		Affine2DLine xAxis = new Affine2DLine(this.getNumberClass(), 0, 0, 1, 0);
		Affine2DLine yAxis = new Affine2DLine(this.getNumberClass(), 0, 0, 0, 1);
		AffineSpace xintersect = intersected(xAxis);
		AffineSpace yintersect = intersected(yAxis);

		Rectangle2D result = null;
		if (xintersect.getDimension() == 1) {
			result = new Rectangle2D.Double(-1, -1, 2, 2);
		}
		else {
			if (xintersect.hasDefiningPointAtInfinity()) {
				double distance =
					yintersect.getAffineCoordinates()[0].getEntry(2).getDouble();
				result = new Rectangle2D.Double(-distance / 2, 0, distance, distance);
			}
		}
		if (result == null) {
			double x1 = xintersect.getAffineCoordinates()[0].getEntry(1).getDouble();
			double x2 = xintersect.getAffineCoordinates()[0].getEntry(2).getDouble();
			double y1 = yintersect.getAffineCoordinates()[0].getEntry(1).getDouble();
			double y2 = yintersect.getAffineCoordinates()[0].getEntry(2).getDouble();
			result =
				new Rectangle2D.Double(
					Math.min(x1, x2),
					Math.min(y1, y2),
					Math.abs(x1 - x2),
					Math.abs(y1 - y2));
		}
		result.setFrame(
			result.getX() - result.getWidth() * .3,
			result.getY() - result.getHeight() * .3,
			result.getWidth() * 1.6,
			result.getHeight() * 1.6);
		return result;
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

/* (non-Javadoc)
 * @see net.mumie.mathletfactory.mmobject.InteractivityIF#getHandlers()
 */
public List getHandlers() {
	return this.m_interactionHelper.getHandlers();
}
}
