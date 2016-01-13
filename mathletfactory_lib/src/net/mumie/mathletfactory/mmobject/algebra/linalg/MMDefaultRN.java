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
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents the default R<sup>n</sup> as vector space.
 *  @see MMDefaultRNVector
 * 
 *  @author vossbeck, klich
 *  @mm.docstatus finished
 */
public class MMDefaultRN extends NumberVectorSpace implements MMCanvasObjectIF {

	public class RNDisplayProperties extends PointDisplayProperties {

		public final static String DP_BASE_PATH = "display.defaultRn.";
		public final static String UNIT_PROPERTY = DP_BASE_PATH + "unit";
		public final static String EXTENT_PROPERTY = DP_BASE_PATH + "extent";
		
		public RNDisplayProperties() {
			this(null);
		}

		/** Copy constructor */
		public RNDisplayProperties( DisplayProperties p ) {
			super( p );
//		this.getShaderDisplayProperties().setEdgeVisible( true );
			this.setPointRadius( 8 );
			this.setFilled( true );
			setDefaultProperty(UNIT_PROPERTY, 0.2);
			setDefaultProperty(EXTENT_PROPERTY, 10);
		}

		/** Returns the unit to display the vectorspace in a canvas. */
		public double getUnit() {
			Object value = this.getProperty( UNIT_PROPERTY );
			if ( value == null ) {
				value = new Double( 0.2 );
			}
//			return ( Double ) value;
			return (( Double ) value).doubleValue();
		}

		/** Sets the unit to display the vectorspace in a canvas. */
		public void setUnit( double unit ) {
			this.setProperty( UNIT_PROPERTY, new Double( unit ) );
		}

		public double getExtent() {
			Object value = this.getProperty( EXTENT_PROPERTY );
			if ( value == null ) {
				value = new Double( 10 );
			}
//			return ( Double ) value;
			return (( Double ) value).doubleValue();
		}

		/** Sets the unit to display the vectorspace in a canvas. */
		public void setExtent( double extent ) {
			this.setProperty( EXTENT_PROPERTY, new Double( extent ) );
		}

	}

	private HashSet m_specialCaseListenersList = new HashSet();

	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);

	private final VisualizeInCanvasSupport m_visualizeHelper =
		new VisualizeInCanvasSupport(this);

	private RNDisplayProperties m_displayProperties = new RNDisplayProperties();

	private String m_label = null;

	private boolean m_displayGrid = false;
		private boolean m_editable;

  /**
   * An instance of a full <it>K</it><sup>n</sup> will be created, where <it>K</it>
   * may be any of the number types inheriting from
   * {@link net.mumie.mathletfactory.number.MNumber MNumber}. This instance
   * will hold an immutable default basis (see
   * {@link net.mumie.mathletfactory.algebra.NumberVectorSpace#m_default}) that
   * is the mathematical canonical basis of a <it>K</it><sup>n</sup>, i.e. those
   * vectors like (1,0,...,0), (0,1,0,...,0), ..., (0,...,0,1).
   */
	public MMDefaultRN(Class numberClass, int dimension) {
		super(numberClass, dimension, dimension);
		setDisplayProperties(new RNDisplayProperties());
	}

	public DisplayProperties getDisplayProperties() {
		return m_displayProperties;
	}

	public void setDisplayProperties(DisplayProperties newProperties) {
		m_displayProperties = new RNDisplayProperties(newProperties);
	}

	public void setLabel(String aLabel) {
		m_label = aLabel;
	}

	public String getLabel() {
		return m_label;
	}

	public int getDefaultTransformType() {
		return GeneralTransformer
			.NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer
			.NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM;
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

  /** Returns the unit of the grid of coordinates. */
	public double getGridInMath() {
		return m_displayProperties.getUnit();
	}

  /** Sets the unit of the grid of coordinates. */
	public void setGridInMath(double aValue) {
		m_displayProperties.setUnit(aValue);
	}

  /** Enables the drawing of a grid of coordinates, 
   *   if <code >aBoolean</code> is set to <code>true</code>.
   */ 
	public void enableDrawGrid(boolean aBoolean) {
		m_displayGrid = aBoolean;
	}

  /** Returns <code>true</code> if a grid of coordinates is drawn. */
	public boolean displayGrid() {
		return m_displayGrid;
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
		return m_editable;
	}

	public void setEditable(boolean isEditable) {
		m_editable = isEditable;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		throw new TodoException();
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
