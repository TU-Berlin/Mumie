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

package net.mumie.mathletfactory.mmobject.util;

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
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.util.MDimension;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.VisualizeSupport;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a dimension value (containing values for the width and the height) 
 * inside the MM-Object Framework.
 * The number class is always {@link net.mumie.mathletfactory.math.number.MInteger} because
 * dimension values are always whole numbers.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MMDimension extends MDimension implements MMObjectIF {

	private DisplayProperties m_displayProperties = new DisplayProperties();

	private boolean m_isEditable = false;

	private HashSet m_specialCaseListenersList = new HashSet();

	private final InteractivitySupport m_interactionHelper =
		new InteractivitySupport(this);

	private final VisualizeSupport m_visualizeHelper =
		new VisualizeSupport(this);

	/**
	 * Creates a new instance with zero values for the width and height.
	 */
	public MMDimension() {
		this(0, 0);
	}

	/**
	 * Creates a new instance with the given value.
	 */
	public MMDimension(MDimension dim) {
		this(dim.getHeight(), dim.getWidth());
	}
	
	/**
	 * Copy Constructor: creates a new instance with the given value.
	 */
	public MMDimension(MMDimension dim) {
		this(dim.getHeight(), dim.getWidth());
	}
	
	/**
	 * Creates a new instance with the given values for the width and height.
	 */
	public MMDimension(int height, int width) {
		super(height, width);
	}

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
	public MMDimension(Node content) {
    super(content);
	}

	public void setDisplayProperties(DisplayProperties newProperties) {
		m_displayProperties = newProperties;
	}

	public DisplayProperties getDisplayProperties() {
		return m_displayProperties;
	}
	
	 /**
   * @see net.mumie.mathletfactory.mmobject.MMDefaultObject#getDefaultTransformType()
   */
  public int getDefaultTransformType() {
    return GeneralTransformer.DIMENSION_TRANSFORM;
  }
  
	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.DIMENSION_TRANSFORM;
	}

  /**
   * Listens to changes from its drawables.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if(!evt.getPropertyName().equals(PropertyHandlerIF.INDEXED_ENTRY))
      return;
    IndexedEntry indexedEntry = (IndexedEntry) evt.getNewValue();
    if(indexedEntry.getIndex() == WIDTH_POSITION)
    	setWidth((MInteger) indexedEntry.getValue());
    if(indexedEntry.getIndex() == HEIGHT_POSITION)
    	setHeight((MInteger) indexedEntry.getValue());
  	setEdited(true, indexedEntry.getIndex());
    ActionManager.performActionCycleFromObject(this);
  }

	public boolean isEditable() {
		return m_isEditable;
	}

	public void setEditable(boolean isEditable) {
		m_isEditable = isEditable;
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

	public HashMap getDisplayTransformerMap() {
		return m_visualizeHelper.getDisplayTransformerMap();
	}

	public int getTransformType(JComponent display) {
		return m_visualizeHelper.getTransformType(display);
	}
	
	public JComponent getAsContainerContent() {
		return m_visualizeHelper.getAsContainerContent();
	}

	public JComponent getAsContainerContent(int transformType) {
		return m_visualizeHelper.getAsContainerContent(transformType);
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
    if(m_interactionHelper != null)
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
  
	public void draw() {
		m_visualizeHelper.draw();
	}

	public void render() {
    if(m_visualizeHelper != null)
		  m_visualizeHelper.render();
	}
	
  public List getHandlers() {
  	return this.m_interactionHelper.getHandlers(); 
  }
}
