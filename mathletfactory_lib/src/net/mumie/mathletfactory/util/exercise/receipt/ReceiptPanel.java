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

package net.mumie.mathletfactory.util.exercise.receipt;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.util.xml.DatasheetRenderer;

/**
 * This class is used to display a receipt in a panel, used for applet and application mode.
 * It supports multiple views, where a second view with a {@link DatasheetRenderer} is already
 * implemented.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class ReceiptPanel extends JPanel {

	private final static Font m_boldFont = new Font("Dialog", Font.BOLD, 14);
	private final static Font m_plainFont = new Font("Dialog", Font.PLAIN, 14);
//	private final static Font m_bigFont = new Font("Dialog", Font.PLAIN, 16);
	private final static int SCROLL_INCREMENT = 10;
	private final static String DEFAULT_VIEW = "default-view";
	private final static String EXTENDED_VIEW = "extended-view";
	
	private final Receipt m_receipt;
	private final JComponent m_answerPanel;
	private final JScrollPane m_answerScroller;
	private final JComponent m_headerPanel;
	private final CardLayout m_layout = new CardLayout();
	private final DatasheetRenderer m_dsRenderer;
	
	private boolean m_isDefaultView = true;
	
	/**
	 * Creates a new panel for the given receipt.
	 * 
	 * @param receipt a receipt; must not be null
	 */
	public ReceiptPanel(Receipt receipt) {
		super();
		setLayout(m_layout);
		setBorder(BorderFactory.createLoweredBevelBorder());
		m_receipt = receipt;
		
		JPanel leftHeaderPanel = new JPanel(new MatrixLayout(3, 2));
		leftHeaderPanel.add(createLabel("Veranstaltung:", true));
		leftHeaderPanel.add(createLabel(getReceipt().getCourseName(), false));
		leftHeaderPanel.add(createLabel("Aufgabenblatt:", true));
		leftHeaderPanel.add(createLabel(getReceipt().getWorksheetName(), false));
		leftHeaderPanel.add(createLabel("Aufgabe:", true));
		leftHeaderPanel.add(createLabel(getReceipt().getProblemName(), false));
		
		JPanel rightHeaderPanel = new JPanel(new MatrixLayout(3, 2));
		rightHeaderPanel.add(createLabel("Datum:", true));
		rightHeaderPanel.add(createLabel(getReceipt().getTime(), false));
		rightHeaderPanel.add(createLabel("Name:", true));
		rightHeaderPanel.add(createLabel(getReceipt().getUserSurname(), false));
		rightHeaderPanel.add(createLabel("Vorname:", true));
		rightHeaderPanel.add(createLabel(getReceipt().getUserFirstName(), false));

		m_headerPanel = new JPanel(new MatrixLayout(1, 2));
		int margin = 10;
		m_headerPanel.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
		m_headerPanel.add(leftHeaderPanel);
		m_headerPanel.add(rightHeaderPanel);
		
		m_answerPanel = receipt.getAnswerPanel();
		m_answerScroller = new JScrollPane(m_answerPanel);
		// used for scrolling with scroll bar knobs
		m_answerScroller.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		m_answerScroller.getHorizontalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		// used for scrolling with mouse wheel
		m_answerScroller.getVerticalScrollBar().setBlockIncrement(SCROLL_INCREMENT);
		MouseWheelListener mouseWheelListener = new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				JScrollBar bar = m_answerScroller.getVerticalScrollBar();
				bar.setValue(bar.getValue() + e.getWheelRotation());
			}
		};
		m_headerPanel.addMouseWheelListener(mouseWheelListener);
		m_answerPanel.addMouseWheelListener(mouseWheelListener);
		
		m_dsRenderer = new DatasheetRenderer(receipt.getDatasheet());
		
		JPanel defaultViewPane = new JPanel(new BorderLayout());		
		defaultViewPane.add(m_headerPanel, BorderLayout.NORTH);
		defaultViewPane.add(m_answerScroller, BorderLayout.CENTER);
		add(defaultViewPane, DEFAULT_VIEW);
		add(m_dsRenderer, EXTENDED_VIEW);
	}
	
	/**
	 * Sets whether the header panel containing the student's data should be visible.
	 * @param visible a flag
	 */
	public void setHeaderVisible(boolean visible) {
		m_headerPanel.setVisible(visible);
	}
	
	/**
	 * Switches to the default view.
	 */
	public void setDefaultView() {
		m_layout.show(this, DEFAULT_VIEW);
		m_isDefaultView = true;
	}
	
	/**
	 * Returns whether the current view is the default view.
	 */
	public boolean isDefaultView() {
		return m_isDefaultView;
	}
	
	/**
	 * Returns whether the current view is the extended view.
	 */
	public boolean isExtendedView() {
		return !m_isDefaultView;
	}
	
	/**
	 * Switches to the extended view.
	 */
	public void setExtendedView() {
		m_layout.show(this, EXTENDED_VIEW);
		m_isDefaultView = false;
	}
	
	/**
	 * Creates and returns a new label used to display the student's data.
	 */
	private JLabel createLabel(String text, boolean isTitle) {
		String emptySpace = "   ";
		JLabel result = new JLabel(text + emptySpace);
		result.setFont(isTitle ? m_boldFont : m_plainFont);
		return result;
	}
	
	/**
	 * Returns the receipt which is displayed in this panel.
	 */
	public Receipt getReceipt() {
		return m_receipt;
	}
}
