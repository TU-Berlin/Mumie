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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;

import net.mumie.mathletfactory.appletskeleton.util.theme.PlainButton;
import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.transformer.noc.IntervalTransformer;

/**
 * This class acts as a container drawable for {@link net.mumie.mathletfactory.mmobject.set.MMInterval}.
 * 
 * @author Gronau
 * @mm.docstatus finished
 */
public class MMIntervalPanel extends MMCompoundPanel implements PropertyChangeListener {

	private TextPanel m_commaLabel;
	private JButton m_lowBoundBracketButton, m_upBoundBracketButton;

	private MMObjectIF m_lowBound, m_upBound;
	
	private MMNumberPanel m_lowBoundRenderer;
	private MMNumberPanel m_upBoundRenderer;

	private boolean m_lowBoundType = MMInterval.OPEN,
		m_upBoundType = MMInterval.OPEN;

	private boolean m_isLowerBoundaryEditable = true;
	private boolean m_isUpperBoundaryEditable = true;

	/**
	 * 
	 */
	public MMIntervalPanel(
		MMObjectIF master,
		IntervalTransformer transformer) {
		super(master, transformer, new AlignablePanel());

		m_lowBoundBracketButton = new PlainButton(" ( ");
		m_upBoundBracketButton = new PlainButton(" ) ");
		m_commaLabel = new TextPanel(",");

		addPropertyChangeListener((PropertyHandlerIF) master);
		MMInterval masterInterval = (MMInterval) master;

		m_lowBound = NumberFactory.getNewMMInstanceFor(masterInterval.getLowerBoundaryVal());
		m_lowBoundRenderer = (MMNumberPanel) m_lowBound.getAsContainerContent();
		addMMPanel(m_lowBoundRenderer);
		m_lowBoundRenderer.addPropertyChangeListener(this);
		m_lowBoundRenderer.setRememberSize(true);

		m_upBound = NumberFactory.getNewMMInstanceFor(masterInterval.getUpperBoundaryVal());
		m_upBoundRenderer = (MMNumberPanel) m_upBound.getAsContainerContent();
		addMMPanel(m_upBoundRenderer);
		m_upBoundRenderer.addPropertyChangeListener(this);
		m_upBoundRenderer.setRememberSize(true);

		getViewerComponent().add(m_lowBoundBracketButton);
		getViewerComponent().add(m_lowBoundRenderer);
		getViewerComponent().add(m_commaLabel);
		getViewerComponent().add(m_upBoundRenderer);
		getViewerComponent().add(m_upBoundBracketButton);

		m_lowBoundBracketButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isEditable() || !m_isLowerBoundaryEditable)
					return;
				boolean oldVal = m_lowBoundType;
				boolean newVal = false;
				if (m_lowBoundType == MMInterval.OPEN) {
					m_lowBoundType = MMInterval.CLOSED;
					newVal = MMInterval.CLOSED;
				}
				else {
					m_lowBoundType = MMInterval.OPEN;
					newVal = MMInterval.OPEN;
				}
				firePropertyChange(
					PropertyHandlerIF.INTERVAL_LOW_BOUND_TYPE,
					oldVal,
					newVal);
			}
		});

		m_upBoundBracketButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isEditable() || !m_isUpperBoundaryEditable)
					return;
				boolean oldVal = m_upBoundType;
				boolean newVal = false;
				if (m_upBoundType == MMInterval.OPEN) {
					m_upBoundType = MMInterval.CLOSED;
					newVal = MMInterval.CLOSED;
				}
				else {
					m_upBoundType = MMInterval.OPEN;
					newVal = MMInterval.OPEN;
				}
				firePropertyChange(
					PropertyHandlerIF.INTERVAL_UP_BOUND_TYPE,
					oldVal,
					newVal);
			}
		});

	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyHandlerIF.NUMBER)) {
			MNumber oldValue = (MNumber) evt.getOldValue();
			MNumber newValue = (MNumber) evt.getNewValue();
			if (m_lowBoundRenderer == evt.getSource()) {
				firePropertyChange(
						PropertyHandlerIF.INTERVAL_LOW_BOUND_VALUE,
						oldValue,
						newValue);
			}
			else if (m_upBoundRenderer == evt.getSource()) {
				firePropertyChange(
						PropertyHandlerIF.INTERVAL_UP_BOUND_VALUE,
						oldValue,
						newValue);
			}
		}
	}
	
	public void setForeground(Color c) {
		super.setForeground(c);
		if (m_lowBoundBracketButton != null) {
			m_lowBoundBracketButton.setForeground(c);
			m_upBoundBracketButton.setForeground(c);
			m_commaLabel.setForeground(c);
			m_lowBoundRenderer.setForeground(c);
			m_upBoundRenderer.setForeground(c);
		}
	}

	public void setFont(Font font) {
		super.setFont(font);
		if (m_lowBoundBracketButton != null) {
			m_lowBoundBracketButton.setFont(font);
			m_upBoundBracketButton.setFont(font);
			m_commaLabel.setFont(font);
			m_lowBoundRenderer.setFont(font);
			m_upBoundRenderer.setFont(font);
		}
	}

	public void setBoundary(
		MNumber lowBoundVal,
		boolean openOrClosed,
		MNumber upBoundVal,
		boolean closedOrOpen,
		boolean roundOrSquare) {
		((MNumber) m_lowBound).set(lowBoundVal);
		m_lowBoundType = openOrClosed;
		if (m_lowBoundType == MMInterval.OPEN) {
			if (roundOrSquare == MMInterval.SQUARE)
				m_lowBoundBracketButton.setText(" ] ");
			else
				m_lowBoundBracketButton.setText(" ( ");
		}
		else
			m_lowBoundBracketButton.setText(" [ ");

		((MNumber) m_upBound).set(upBoundVal);
		m_upBoundType = closedOrOpen;
		if (m_upBoundType == MMInterval.OPEN) {
			if (roundOrSquare == MMInterval.SQUARE)
				m_upBoundBracketButton.setText(" [ ");
			else
				m_upBoundBracketButton.setText(" ) ");
		}
		else
			m_upBoundBracketButton.setText(" ] ");
	}
	
	public void render() {
		m_lowBound.render();
		m_upBound.render();
		revalidate();
		repaint();
	}

	public void setEditable(boolean isEditable) {
		super.setEditable(isEditable);
		m_lowBound.setEditable(isEditable);
		m_upBound.setEditable(isEditable);
		m_lowBoundBracketButton.setFocusable(isEditable);
		m_lowBoundBracketButton.setEnabled(isEditable);
		m_upBoundBracketButton.setFocusable(isEditable);
		m_upBoundBracketButton.setEnabled(isEditable);
	}

	public void setLowerBoundaryEditable(boolean editable) {
		m_lowBoundBracketButton.setFocusable(editable);
		m_lowBoundBracketButton.setEnabled(editable);
		m_isLowerBoundaryEditable = editable;
	}

	public void setUpperBoundaryEditable(boolean editable) {
		m_upBoundBracketButton.setFocusable(editable);
		m_upBoundBracketButton.setEnabled(editable);
		m_isUpperBoundaryEditable = editable;
	}
}
