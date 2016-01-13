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

package net.mumie.mathletfactory.util.error;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DefaultDialog;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogAction;

public class ReportErrorDialog extends DefaultDialog {

	public final static int DIALOG_ID = 21;
	
	private JTextComponent m_userCommentArea, m_userMailAddressField;
	
	public ReportErrorDialog(MathletContext mathlet, Dialog owner, final QualityFeedbackReport report) {
		super(mathlet, owner, null);
		
		setDialogID(DIALOG_ID);
		setTitle(mathlet.getMessage("errorhandler.report.title.bar"));
		setContentTitle(mathlet.getMessage("errorhandler.report.title.content"));
		addText(mathlet.getMessage("errorhandler.report.text.top"));
		insertLineBreak();
		JButton detailsButton = new JButton(new AbstractAction(mathlet.getMessage("errorhandler.button.details")) {
			public void actionPerformed(ActionEvent e) {
				new QualityFeedbackDetailsDialog(getContext(), ReportErrorDialog.this, report).showDialog();
			}
		});
		addControl(detailsButton);
		insertLineBreaks(2);
		
		addText(mathlet.getMessage("errorhandler.report.text.middle"));
		insertLineBreak();
		m_userCommentArea = new JTextArea(3, 32);
		m_userCommentArea.setBackground(Color.WHITE);
		JScrollPane areaScroller = new JScrollPane(m_userCommentArea);
		addControl(areaScroller);
		insertLineBreaks(2);
		
		addText(mathlet.getMessage("errorhandler.report.text.bottom"));
		m_userMailAddressField = new JTextField(32);
		m_userMailAddressField.setBackground(Color.WHITE);
		insertLineBreak();
		addControl(m_userMailAddressField);
		insertLineBreaks(2);
		
		
		addAction(DialogAction.APPROVE_ACTION, mathlet.getMessage("errorhandler.button.submit"));
		addAction(DialogAction.CANCEL_ACTION, mathlet.getMessage("dialog.action.cancel"));
		
		pack();
	}
	
	public String getUserComment() {
		return m_userCommentArea.getText();
	}

	public String getUserMailAddress() {
		return m_userMailAddressField.getText();
	}
}
