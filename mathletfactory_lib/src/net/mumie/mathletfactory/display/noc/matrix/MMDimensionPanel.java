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

package net.mumie.mathletfactory.display.noc.matrix;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.math.util.MDimension;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.number.MMInteger;
import net.mumie.mathletfactory.mmobject.util.IndexedEntry;
import net.mumie.mathletfactory.mmobject.util.MMDimension;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * This class is the container drawable for {@link MMDimension}.
 * 
 * @author gronau
 * @mm.docstatus finished
 *
 */
public class MMDimensionPanel extends MMCompoundPanel implements PropertyChangeListener {
	
	private MMInteger m_width, m_height;
	private MMNumberPanel m_widthPanel, m_heightPanel;

	public MMDimensionPanel(MMObjectIF master, ContainerObjectTransformer transformer) {
		super(master, transformer);
		
		addPropertyChangeListener(getRealMaster());
		
		m_height = new MMInteger(getRealMaster().getHeight());
		m_heightPanel = (MMNumberPanel) m_height.getAsContainerContent();		
		m_heightPanel.addPropertyChangeListener(this);
		addMMPanel(m_heightPanel);
		
		m_width = new MMInteger(getRealMaster().getWidth());
		m_widthPanel = (MMNumberPanel) m_width.getAsContainerContent();
		m_widthPanel.addPropertyChangeListener(this);
		addMMPanel(m_widthPanel);
		
    getViewerComponent().setLayout(new FlowLayout());
    add(m_heightPanel);
    add(new JLabel(" x "));
    add(m_widthPanel);
	}
	
  /**
   * Sets whether the two values are editable.
   */
  public void setEditable(boolean editable){
    super.setEditable(editable);
    m_width.setEditable(editable);
    m_height.setEditable(editable);
    render();
  }
  
  public void render() {
	m_width.setDouble(getRealMaster().getWidth());
	m_height.setDouble(getRealMaster().getHeight());
  	m_width.render();
  	m_height.render();
  }
  
	/**
	 * This panel listens to single changes in the number drawables it holds and
	 * forwards a new PropertyChange-Event (type:
	 * PropertyHandlerIF.INDEXED_ENTRY) with the new dimension entry to the
	 * master.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(!evt.getPropertyName().equals(PropertyHandlerIF.NUMBER))
			return;
		if(m_widthPanel == evt.getSource()) {
			firePropertyChange(PropertyHandlerIF.INDEXED_ENTRY, 
					new IndexedEntry(evt.getOldValue(), MDimension.WIDTH_POSITION), 
					new IndexedEntry(evt.getNewValue(), MDimension.WIDTH_POSITION));
		}
		else if(m_heightPanel == evt.getSource()) {
			firePropertyChange(PropertyHandlerIF.INDEXED_ENTRY, 
					new IndexedEntry(evt.getOldValue(), MDimension.HEIGHT_POSITION), 
					new IndexedEntry(evt.getNewValue(), MDimension.HEIGHT_POSITION));
		}
	}
	
	/**
	 * Marks the width or height entry (see index contants in {@link MDimension}) user-edited or not,
	 * i.e. sets the internal edited flag for the entry to <code>edited</code>.
	 * 
	 * @param edited a flag
	 * @param index whether {@link MDimension#WIDTH_POSITION} or {@link MDimension#HEIGHT_POSITION}
	 */
	public void setEdited(boolean edited, int index) {
		switch(index) {
		case MDimension.WIDTH_POSITION:
			m_width.setEdited(edited);
			m_width.render();
			break;
		case MDimension.HEIGHT_POSITION:
			m_height.setEdited(edited);
			m_height.render();
			break;
		}
	}
	
	private MMDimension getRealMaster() {
		return (MMDimension) getMaster();
	}
}
