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

package net.mumie.mathletfactory.mmobject.algebra.poly;

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
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.poly.Polynomial;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.Discretizable1DIF;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a polynomial both as algebraic entity and as function over R.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class MMPolynomial extends Polynomial implements MMOneChainInRIF {

  private HashSet m_specialCaseListenersList = new HashSet();

  private final InteractivitySupport m_interactionHelper = new InteractivitySupport(this);
  private final VisualizeInCanvasSupport m_visualizeHelper = new VisualizeInCanvasSupport(this);
  private DisplayProperties m_displayProperties = new DisplayProperties();
  private String m_label = "P";
  private int m_vertices_count_in_drawable = Discretizable1DIF.DEFAULT_VERTICES_COUNT;
  private boolean m_boundarysVisible = true;
  private boolean m_editable = false;

  /** Constructs the polynomial with the given coefficients*/
  public MMPolynomial(NumberTuple coeff) {
    super(coeff);
    PolygonDisplayProperties p = new PolygonDisplayProperties();
    p.setFilled(false);
    setDisplayProperties(p);
  }

  /** Creates the polynomial with the given degree and all coefficients set to zero. */
  public MMPolynomial(Class entryClass, int deg) {
    super(entryClass, deg);
    PolygonDisplayProperties p = new PolygonDisplayProperties();
    p.setFilled(false);
    setDisplayProperties(p);
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM;
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
    return GeneralTransformer.FUNCTION_TRANSFORM;
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

  public int getVerticesCount() {
    return m_vertices_count_in_drawable;
  }

  public void setVerticesCount(int aNumber) {
    m_vertices_count_in_drawable = aNumber;
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getBorelSet(int)
   */
  public FiniteBorelSet getBorelSetInComponent(int index) {
    return getBorelSet();
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getExpressionCount()
   */
  public int getComponentsCount() {
    return 1;
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getFunctionExpression(int)
   */
  public FunctionOverRIF getEvaluateExpressionInComponent(int index) {
    return this;
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getIntervalCount(int)
   */
  public int getIntervalCountInComponent(int expressionIndex) {
    return getBorelSetInComponent(expressionIndex).getIntervalCount();
  }
  /**
   * @see net.mumie.mathletfactory.mmobject.BoundaryIF#areBoundarysVisible()
   */
  public boolean areBoundarysVisible() {
    return m_boundarysVisible;
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.BoundaryIF#setBoundarysVisible(boolean)
   */
  public void setBoundarysVisible(boolean aFlag) {
    m_boundarysVisible = aFlag;
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getAllIntervalCount()
   */
  public int getAllIntervalCount() {
    return getBorelSet().getIntervalCount();
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getBorelSet(int)
   */
  public FiniteBorelSet getBorelSet(int index) {
    return getBorelSet();
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getExpressionCount()
   */
  public int getExpressionCount() {
    return 1;
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getFunctionExpression(int)
   */
  public FunctionOverRIF getFunctionExpression(int index) {
    return this;
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getInterval(int, int)
   */
  public Interval getInterval(int expressionCount, int intervalIndexInBorelSet) {
    return getBorelSet(expressionCount).getInterval(intervalIndexInBorelSet);
  }

  /**
   * @see net.mumie.mathletfactory.mmobject.geom.OneChainIF#getIntervalCount(int)
   */
  public int getIntervalCount(int expressionIndex) {
    return getBorelSet(expressionIndex).getIntervalCount();
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.mmobject.MMObjectIF#isEditable()
   */
  public boolean isEditable() {
    return m_editable;
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.mmobject.MMObjectIF#setEditable(boolean)
   */
  public void setEditable(boolean isEditable) {
    m_editable = isEditable;
  }

  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent e) {
    if (!e.getPropertyName().equals(OPERATION))
      return;
    setOperation(new Operation((Operation) e.getNewValue()));
    setEdited(true);
    ActionManager.performActionCycleFromObject(this);
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
