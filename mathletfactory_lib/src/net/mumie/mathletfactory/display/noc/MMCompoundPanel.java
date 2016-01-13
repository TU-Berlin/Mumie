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

package net.mumie.mathletfactory.display.noc;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JComponent;

import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * This class is the base for all compound MM-Panels, i.e. container drawables
 * which use other MM-Objects and their MM-Panels internally to diplay itself.
 * These MM-Panels must be registered in this class in order to work correctly
 * inside this MM-Panel. 
 * 
 * @see #addMMPanel(MMPanel)
 * @see #getMMPanel(int)
 * @see #removeMMPanel(MMPanel)
 * 
 * @author Gronau
 * @mm.docstatus finished
 */
public abstract class MMCompoundPanel extends MMPanel {

	private Vector m_mmpanels = new Vector();
	private boolean m_isEditable = false;
	
	/**
	 * Creates a new compound panel for a given master MM-Object and transformer.
	 */
	public MMCompoundPanel(MMObjectIF masterObject, ContainerObjectTransformer transformer) {
		super(masterObject, transformer);
    addGenericMouseListener();
	}

	/**
	 * Creates a new compound panel for a given master MM-Object and transformer.
	 */
	public MMCompoundPanel(MMObjectIF masterObject, ContainerObjectTransformer transformer, JComponent viewerComp) {
		super(masterObject, transformer, viewerComp);
    addGenericMouseListener();
	}
	
	private void addGenericMouseListener() {
		addMouseListener(new CompoundListener(this));
	}
	
	/**
	 * Returns whether the values in th panel are editable.
	 */
	public boolean isEditable() {
		return m_isEditable;
	}

  /**
   * Sets whether the values in this panel should be editable.
   * This method should not be called by an application. Instead
   * the <code>setEditable()</code> method of the master
   * should be called, after which this method is called internally
   * indirectly by the transformer (and by this any former value will be
   * overwritten).
   */
	public void setEditable(boolean isEditable) {
		m_isEditable = isEditable;
		repaintAll();
	}
	
	void setHighLighted(boolean highlighted) {
		super.setHighLighted(highlighted);
		for(int i = 0; i < getMMPanelCount(); i++)
			getMMPanel(i).setHighLighted(highlighted);
	}
	
	/**
	 * Registers a MM-Panel i.e. adds it to the list of registered drawables. 
	 */
	protected void addMMPanel(MMPanel p) {
		m_mmpanels.add(p);
		p.setRootPanel(this);
	}
	
	/**
	 * Removes a MM-Panel from the list of registered drawables. 
	 */
	protected void removeMMPanel(MMPanel p) {
		m_mmpanels.remove(p);
		p.setRootPanel(null);
	}

	/**
	 * Returns the number of registered MM-Panels.
	 */
	protected int getMMPanelCount() {
		return m_mmpanels.size();
	}
	
	/**
	 * Returns the registered MM-Panels with the given index.
	 */
	protected MMPanel getMMPanel(int index) {
		return (MMPanel) m_mmpanels.get(index);
	}

	/**
	 * Removes the MM-Panel with the given index from the list of registered drawables. 
	 */
	protected void removeMMPanel(int index) {
		((MMPanel)m_mmpanels.remove(index)).setRootPanel(null);
	}
	
	protected void removeAllMMPanels() {
		for(int i = 0; i < m_mmpanels.size(); i++)
			removeMMPanel(i);
	}
	
//	public Insets getInsets() {
//		return new SimpleInsets(new SimpleInsets(3), getBorderInsets());
//	}
	
	/**
	 * Convenience method for writing <code>getViewerComponent().add(...)</code>.
	 */
	protected void addImpl(Component comp, Object constraints, int index) {
		getViewerComponent().add(comp, constraints, index);
	}

	/**
	 * Convenience method for writing <code>getViewerComponent().remove(int)</code>.
	 */
	public void remove(int index) {
		getViewerComponent().remove(index);
	}
	
	/**
	 * Convenience method for writing <code>getViewerComponent().removeAll()</code>.
	 */
	public void removeAll() {
		getViewerComponent().removeAll();
	}
}
