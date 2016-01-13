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

package net.mumie.mathletfactory.display.noc.number;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JLabel;

import net.mumie.mathletfactory.display.noc.MMEditablePanel;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.transformer.noc.NumberSimpleTransformer;

/**
 * This class acts as a container drawable for all simple mm-numbers.
 * Its number field can only handle numerical values.
 * 
 * @author Markus Gronau
 * @mm.docstatus finished
 */
public class MMNumberSimplePanel extends MMEditablePanel {

	private boolean m_displayAbsValue = false;
	
	private DecimalFormat m_decimalFormat;
	private String m_pattern = new String("#.###");
	private JLabel m_valueLabel = new JLabel();

	public MMNumberSimplePanel(MMObjectIF master, NumberSimpleTransformer transformer) {
		super(master, transformer, new JLabel());

		m_decimalFormat = new DecimalFormat(m_pattern);

		m_valueLabel = (JLabel) getValueViewer();
	}

	protected boolean checkContent(String content) {
		if (parse(getTextField().getText()) != Double.NaN)
			return true;
		else
			return false;
	}
	
	protected String getEditingContent() {
		return ((MNumber) getMaster()).toString();
	}

	protected void applyContent(String content) {
		MNumber newValue =
			NumberFactory.newInstance(
				getMaster().getNumberClass(),
				parse(getTextField().getText()));
		firePropertyChange(
			PropertyHandlerIF.NUMBER,
			getMaster(),
			newValue);
		setValue(newValue); // show the display component
	}
	
	public void setValue(MNumber aNumber) {
		if(isVisible())
			m_valueLabel.setText(format(aNumber));
		else
			m_valueLabel.setText("");
	}

	private String format(MNumber value) {
		if(value instanceof MMDouble) {
//		Double d = new Double(value.getDouble());
			if(m_displayAbsValue && value.getDouble() < 0)
				return m_decimalFormat.format(value.abs().getDouble());
			else
				return m_decimalFormat.format(value.getDouble());
		}
		else {
			if(m_displayAbsValue && value.getDouble() < 0)
				return value.abs().toString();
			else
				return value.toString();
		}
	}

	private double parse(String s) {
		Double d;
		try {
			d = new Double(s);
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
		return d.doubleValue();
	}
	
	public void setForeground(Color c) {
		super.setForeground(c);
		if(m_valueLabel != null)
			m_valueLabel.setForeground(c);
	}
	
	public void setDisplayAbsValue(boolean displayAbsValue) {
		m_displayAbsValue = displayAbsValue;
	}
	
	public boolean isDisplayingAbsValue() {
		return m_displayAbsValue;
	}
}