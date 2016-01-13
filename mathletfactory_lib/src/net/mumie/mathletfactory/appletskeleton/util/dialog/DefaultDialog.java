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

package net.mumie.mathletfactory.appletskeleton.util.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.util.StyledTextButton;
import net.mumie.mathletfactory.display.util.TextPanel;

public class DefaultDialog extends JDialog {

	public final static int INFO_DIALOG = 0;
	
	public final static int WARNING_DIALOG = 1;
	
	public final static int QUESTION_DIALOG = 2;
	
	public final static int ERROR_DIALOG = 3;
	
	public static int parseDialogType(String typeString) {
		if(typeString.equalsIgnoreCase("info") || typeString.equalsIgnoreCase("information"))
			return INFO_DIALOG;
		if(typeString.equalsIgnoreCase("warn") || typeString.equalsIgnoreCase("warning"))
			return WARNING_DIALOG;
		if(typeString.equalsIgnoreCase("error"))
			return ERROR_DIALOG;
		throw new IllegalArgumentException("Not a valid dialog type string: \"" + typeString + "\"");
	}
	
	private MathletContext m_mathlet;
	
	private Object[] m_params;
	
	private ControlPanel m_controlPanel;
	private int m_contentWidth = 350;
	private boolean m_useLargeIcons = false;
	
	private TextPanel m_titlePanel;
	private JPanel m_buttonsPane;
	private JLabel m_idLabel, m_iconLabel;
	
	private DialogAction m_selectedAction;
	
	public DefaultDialog(MathletContext mathlet, Object[] params) {
		this(mathlet, (Frame) null, params);
	}
	
	public DefaultDialog(MathletContext mathlet, Dialog owner, Object[] params) {
		super(owner, true);
		initialize(mathlet, params);
	}
	
	public DefaultDialog(MathletContext mathlet, Frame owner, Object[] params) {
		super(owner == null? mathlet.getAppletFrame() : owner, true);
		initialize(mathlet, params);
	}
	
	private void initialize(MathletContext mathlet, Object[] params) {
		// internal fields
		m_mathlet = mathlet;
		m_params = params; // may be null !
		m_controlPanel = new ControlPanel();
		// dialog layout
		JPanel contentPane = new JPanel(new BorderLayout());
		JPanel centerPane = new JPanel(new BorderLayout());
		JPanel controlPaneResizerPane = new JPanel(new BorderLayout());
		controlPaneResizerPane.add(m_controlPanel, BorderLayout.CENTER);
		m_titlePanel = new TextPanel();
		m_titlePanel.setFont(MumieTheme.getDialogTitleFont());
		int margin = 10;
		m_titlePanel.setBorder(new EmptyBorder(0, 0, margin, 0));
		controlPaneResizerPane.setBorder(new EmptyBorder(margin, margin/2, margin, margin));
		controlPaneResizerPane.add(m_titlePanel, BorderLayout.NORTH);
		centerPane.add(controlPaneResizerPane, BorderLayout.CENTER);
		int outerMargin = getOuterMargin();
		JPanel iconResizerPane = new JPanel(new BorderLayout());
		iconResizerPane.add(createIconPane(), BorderLayout.CENTER);
		m_idLabel = new JLabel();
		m_idLabel.setToolTipText(mathlet.getMessage("dialog.id.tooltip"));
		m_idLabel.setBorder(new EmptyBorder(0, outerMargin, 0, 0));
		iconResizerPane.add(m_idLabel, BorderLayout.SOUTH);
		centerPane.add(iconResizerPane, BorderLayout.WEST);
		contentPane.add(centerPane, BorderLayout.CENTER);
		JPanel bottomPane = new JPanel(new BorderLayout());
		bottomPane.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		m_buttonsPane = new JPanel();
		bottomPane.add(m_buttonsPane, BorderLayout.CENTER);
		// help button
		JButton helpButton = new JButton(mathlet.getMessage("Help"));
		mathlet.getRuntimeSupport().installHelp(helpButton, "errors");
		JPanel helpResizerPane = new JPanel();
		helpResizerPane.setBorder(new EmptyBorder(0, outerMargin, 0, 0));
		helpResizerPane.add(helpButton);
		bottomPane.add(helpResizerPane, BorderLayout.WEST);
		// apply layout
		setContentPane(contentPane);
		
		// install F1 help
		mathlet.getRuntimeSupport().installHelp(getRootPane(), "errors");
		
		pack();
	}
	
	/**
	 * Allows the dialog to add specific content after the generic content block, which will be added dynamically.
	 * @see DialogManager#showDialog(int, Object[])
	 */
	public void finishGenericLayout() {
		// empty implementation
	}
	
	public MathletContext getContext() {
		return m_mathlet;
	}
	
	public Object[] getParameters() {
		return m_params;
	}
	
	public ControlPanel getControlPanel() {
		return m_controlPanel;
	}
	
	protected int getContentWidth() {
		return m_contentWidth;
	}
	
	public void addText(String message) {
		addText(message, false);
	}
	
	public void addText(String message, boolean wrap) {
		if(message == null)
			return;
		String text = getContext().getMessage(message, getParameters());
		if(text == null)
			text = message;
		TextPanel tp = new TextPanel(text);
		tp.setHorizontalTextAlignment(SwingConstants.LEFT);
		if(wrap)
			tp.setWidth(getContentWidth());
		addControl(tp);
	}
	
	public void insertLineBreak() {
		getControlPanel().insertLineBreak();
	}
	
	public void insertLineBreaks(int count) {
		getControlPanel().insertLineBreaks(count);
	}
	
	public void setCenterAlignment() {
		getControlPanel().setCenterAlignment();
	}
	
	public void setLeftAlignment() {
		getControlPanel().setLeftAlignment();
	}
	
	public void setRightAlignment() {
		getControlPanel().setRightAlignment();
	}
	
	public void addControl(Component control) {
		getControlPanel().add(control);
	}
	
	public void addAction(int action, String name) {
		addAction(action, name, null);
	}
	
	public void addAction(int action, String name, Icon icon) {
		addAction(new DialogAction(this, action, true, name, icon));
	}
	
	public void addAction(final DialogAction action) {
		Action a = new AbstractAction(action.getName(), action.getIcon()) {
			public void actionPerformed(ActionEvent e) {
				// store selected action
				m_selectedAction = action;
				// perform dialog action
				action.actionPerformed(e);
			}
		};
		JButton b = new StyledTextButton(a);
		m_buttonsPane.add(b);
		// put focus on first action button
		if(m_buttonsPane.getComponentCount() == 1)
			b.requestFocusInWindow();
	}
	
	public void setContentTitle(String title) {
		m_titlePanel.setText(title);
	}
	
	public void setDialogID(int id) {
		m_idLabel.setText(getContext().getMessage("dialog.id.label") + id);
	}
	
	public void setIcon(Icon icon) {
		m_iconLabel.setIcon(icon);
	}
	
	public void setType(int type) {
		
	}
	
	protected JComponent createIconPane() {
		int outerMargin = getOuterMargin();
//		String iconURL = useLargeIcons() ? "/resource/icon/mumie_sad.png" : "/resource/icon/error.png";
		m_iconLabel = new JLabel();
		m_iconLabel.setOpaque(true);
		m_iconLabel.setVerticalTextPosition(JLabel.CENTER);
		if(useLargeIcons()) {
			int innerMargin = 10;
			m_iconLabel.setBorder(new CompoundBorder(BorderFactory.createLoweredBevelBorder(), new EmptyBorder(innerMargin, innerMargin, innerMargin, innerMargin)));
			m_iconLabel.setBackground(Color.WHITE);
		}
		JPanel iconPanel = new JPanel(new BorderLayout());
		iconPanel.setBorder(new EmptyBorder(outerMargin, outerMargin, outerMargin, outerMargin/2));
		iconPanel.add(m_iconLabel, BorderLayout.CENTER);
		return iconPanel;
	}
	
	protected int getOuterMargin() {
		return useLargeIcons() ? 10 : 20;
	}
	
	protected boolean useLargeIcons() {
		return m_useLargeIcons;
	}
	
	public DialogAction showDialog() {
    getContentPane().validate();
		pack();
		center();
		show();
		return getSelectedAction();
	}
	
	public DialogAction getSelectedAction() {
		return m_selectedAction;
	}
	
  /** Centers this frame on the screen. */
  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - getWidth())/2;
    int y = (screenSize.height - getHeight())/2;
    setLocation(x,y);
  }
}
