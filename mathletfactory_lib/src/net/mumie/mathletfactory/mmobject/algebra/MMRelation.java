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

package net.mumie.mathletfactory.mmobject.algebra;
import java.awt.Font;
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
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeSupport;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents an arbitrary complex algebraic relation based on a
 *  dedicated number class. Basically it consists of a tree of
 *  {@link RelNode}s that determine the logical structure of the relation.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class MMRelation extends Relation implements MMObjectIF {

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
	 *  constructs an <code>MMRelation</code> over a given number class and from
	 *  a given expression.
	 */
	public MMRelation(
		Class numberClass,
		String expression,
		Font font,
		int normalForm) {
		super(numberClass, expression, normalForm);
	}

	/**
	 * Constructs a relation for the given number class, the string expression
	 * and a flag indicating, whether this relation should be in relational normal form.
	 */
	public MMRelation(Class numberClass, String expression, int normalForm) {
		super(numberClass, expression, normalForm);
	}

	/**
	 * Copy constructor.
	 */
	public MMRelation(MMRelation relation) {
		this((Relation) relation);
    setDisplayProperties(new DisplayProperties(m_displayProperties));
	}

	/**
	 * Copy constructor.
	 */
	public MMRelation(Relation relation) {
		super(relation);
	}

	/**
	 * This class catches
	 * {@link net.mumie.mathletfactory.mmobject.MMPropertyHandlerIF#RELATION}
	 * property changes.
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (!e.getPropertyName().equals(RELATION))
			return;
		RelNode oldRoot = getRelRoot();
		RelNode newRoot = ((Relation) e.getNewValue()).getRelRoot();
		if (oldRoot.equals(newRoot))
			return;
		setRelRoot(newRoot);
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
		return GeneralTransformer.RELATION_TRANSFORM;
	}

	public void setEditable(boolean editable) {
    if(editable == m_editable)
      return;
		m_editable = editable;
    render();
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
		return GeneralTransformer.RELATION_TRANSFORM;
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
