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

package net.mumie.mathletfactory.display.noc.set;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.plaf.PanelUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.transformer.noc.IntervalVariableTransformer;

/**
 * A panel that displays intervals as a variable within bounds. For example the interval
 * [0,3) is displayed as "0<=x<3".
 *   
 * @author Paehler, Gronau
 * @mm.docstatus finished
 */
public class MMIntervalVariablePanel extends MMCompoundPanel implements PropertyChangeListener {
	
	private MMObjectIF m_lowBound, m_upBound;
	private MMNumberPanel m_lowBoundPanel;
	private MMNumberPanel m_upBoundPanel;
  private TextPanel m_lowBoundSign, m_upBoundSign;  
  private TextPanel m_variableLabel;
  
  /** The name of the variable to be displayed. */
  private String m_variable = "x";
  
	private boolean m_lowBoundType = MMInterval.OPEN,
		m_upBoundType = MMInterval.OPEN;
		
	/**
	 * Sets up the panel for the given master.
	 */
	public MMIntervalVariablePanel(MMObjectIF master, IntervalVariableTransformer transformer) {
		super(master, transformer, new AlignablePanel());

		MMInterval masterInterval = (MMInterval) master;

		m_lowBound = NumberFactory.getNewMMInstanceFor(masterInterval.getLowerBoundaryVal());
		m_lowBoundPanel = (MMNumberPanel) m_lowBound.getAsContainerContent();
		addMMPanel(m_lowBoundPanel);
    m_lowBoundPanel.addPropertyChangeListener(getMaster());   
		m_lowBoundPanel.setRememberSize(true);
		
    m_lowBoundSign = new TextPanel("<");

    m_variableLabel = new TextPanel("\\textbf{x}");

    m_upBoundSign = new TextPanel("<");

    m_upBound = NumberFactory.getNewMMInstanceFor(masterInterval.getUpperBoundaryVal());
		m_upBoundPanel = (MMNumberPanel) m_upBound.getAsContainerContent();
		addMMPanel(m_upBoundPanel);
    m_upBoundPanel.addPropertyChangeListener(getMaster());
		m_upBoundPanel.setRememberSize(true);
	
    addPropertyChangeListener((PropertyHandlerIF) master);
    m_lowBoundPanel.addPropertyChangeListener(this);   
    m_upBoundPanel.addPropertyChangeListener(this);
    getViewerComponent().add(m_lowBoundPanel);
    getViewerComponent().add(m_lowBoundSign);
    getViewerComponent().add(m_variableLabel);
    getViewerComponent().add(m_upBoundSign);
    getViewerComponent().add(m_upBoundPanel);
	
		m_lowBoundSign.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!isEditable())
					return;
				boolean oldVal = m_lowBoundType;
				boolean newVal = false;
        if(m_lowBoundType == MMInterval.OPEN){
          m_lowBoundType = MMInterval.CLOSED;
          newVal = MMInterval.CLOSED;
        } else {
          m_lowBoundType = MMInterval.OPEN;
          newVal = MMInterval.OPEN;
        }

				firePropertyChange(PropertyHandlerIF.INTERVAL_LOW_BOUND_TYPE,	oldVal,	newVal);
			}
		});

		m_upBoundSign.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!isEditable())
					return;
			boolean oldVal = m_upBoundType;
			boolean newVal = false;
      if(m_upBoundType == MMInterval.OPEN){
        m_upBoundType = MMInterval.CLOSED;
        newVal = MMInterval.CLOSED;
       } else {
        m_upBoundType = MMInterval.OPEN;
        newVal = MMInterval.OPEN;
       }
			firePropertyChange(PropertyHandlerIF.INTERVAL_UP_BOUND_TYPE,	oldVal,	newVal);
			}
		});
    m_lowBoundSign.setFocusable(false);
    m_upBoundSign.setFocusable(false);
//    getViewerComponent().setBorder(new LineBorder(Color.BLACK));
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyHandlerIF.NUMBER)) {
			MNumber oldValue = (MNumber) evt.getOldValue();
			MNumber newValue = (MNumber) evt.getNewValue();
			if (m_lowBoundPanel == evt.getSource()) {
				firePropertyChange(
						PropertyHandlerIF.INTERVAL_LOW_BOUND_VALUE,
						oldValue,
						newValue);
			}
			else if (m_upBoundPanel == evt.getSource()) {
				firePropertyChange(
						PropertyHandlerIF.INTERVAL_UP_BOUND_VALUE,
						oldValue,
						newValue);
			}
		}
	}

  /** Sets the boundaries of this panel. Called by appropriate transformer. */
	public void setBoundary(MNumber lowBoundVal, boolean openOrClosed,	MNumber upBoundVal,	boolean closedOrOpen) {
		((MNumber) m_lowBound).set(lowBoundVal);
		m_lowBoundType = openOrClosed;
		if (m_lowBoundType == MMInterval.OPEN){
			m_lowBoundSign.setText("<");
    }
		else {
      m_lowBoundSign.setText("\u2264");
    }
			
		((MNumber) m_upBound).set(upBoundVal);
		m_upBoundType = closedOrOpen;
		if (m_upBoundType == MMInterval.OPEN) {
      m_upBoundSign.setText("<");
    } else {
      m_upBoundSign.setText("\u2264");
    }
	}
	
	public void render() {
		m_lowBound.render();
		m_upBound.render();
		super.render();
	}

	public void setEditable(boolean isEditable) {
		super.setEditable(isEditable);
    m_lowBound.setEditable(isEditable);
    m_upBound.setEditable(isEditable);
		m_lowBoundSign.setFocusable(isEditable);
		m_upBoundSign.setFocusable(isEditable);
	}

  public void setForeground(Color color){
    super.setForeground(color);   
    if(m_lowBoundPanel != null)
      m_lowBoundPanel.setForeground(color);
    if(m_upBoundPanel != null)
      m_upBoundPanel.setForeground(color);
    if(m_lowBoundSign != null)
      m_lowBoundSign.setForeground(color);
    if(m_upBoundPanel != null)
      m_upBoundSign.setForeground(color);
    if(m_variableLabel != null)
      m_variableLabel.setForeground(color);
  }

  /** Returns the name of the variable to be displayed. */
  public String getVariable() {
    return m_variable;
  }

  /** Sets the name of the variable to be displayed. */
  public void setVariable(String string) {
    m_variable = string;
  }

  /**
   * Additionally sets the theme property "MMFunctionPanel.font".
   */
  public void setUI(PanelUI ui) {
    super.setUI(ui);
    setFont((Font)MumieTheme.getThemeProperty("MMFunctionPanel.font"));
  }

  
}
