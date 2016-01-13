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

package net.mumie.mathletfactory.mmobject;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;

/**
 * This is an adapterclass for MMObjects that may be displayed in a container. It is designed
 * as base for specific MMobjects that do not extend pure mathematic classes, but implement
 * their mathematical functionality on their own.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public abstract class MMDefaultObject implements MMObjectIF{

    /**
     * The list containing all
     * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s.
     */
    private HashSet m_specialCaseListenersList = new HashSet();

    private final InteractivitySupport m_interactionHelper =
      new InteractivitySupport(this);
    private final VisualizeSupport m_visualizeHelper =
      new VisualizeSupport(this);
    private DisplayProperties m_displayProperties = new DisplayProperties();
    private String m_label = null;

    private boolean m_editable;

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

    public boolean isVisible() {
      return m_visualizeHelper.isVisible();
    }

    public void setVisible(boolean aFlag) {
      m_visualizeHelper.setVisible(aFlag);
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
      return getDefaultTransformType();
    }

    /**
     * @see net.mumie.mathletfactory.mmobject.MMObjectIF#isEditable()
     */
    public boolean isEditable() {
      return m_editable;
    }

    /**
     * @see net.mumie.mathletfactory.mmobject.MMObjectIF#setEditable(boolean)
     */
    public void setEditable(boolean isEditable) {
      if(m_editable == isEditable)
        return;
      m_editable = isEditable;
      render();
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

    /**
     * Returns the interactivitySupport of this MMObject.
     */
    protected InteractivitySupport getInteractionHelper() {
      return m_interactionHelper;
    }

    /** Sets the object fill color (without border). */
    public void setObjectColor(Color color){
      DisplayProperties props = getDisplayProperties();
      props.setObjectColor(color);
      setDisplayProperties(props);
    }

    /**
     * Sets the border color of this object (only applicable for canvas displaying).
     */
    public void setBorderColor(Color color){
      DisplayProperties props = getDisplayProperties();
      props.setBorderColor(color);
      setDisplayProperties(props);
    }

    /**
     * Sets the color of this object (both border and object color, if applicable).
     */
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
}
