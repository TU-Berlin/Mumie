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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DefaultDialog;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogAction;

public class ErrorCaughtDialog extends DefaultDialog {

	public final static int DIALOG_ID = 20;
	public final static String MESSAGE_ID = "errors.feedback";
	
	
	private JButton m_detailsButton, m_reportButton;
	
	public ErrorCaughtDialog(MathletContext mathlet, final QualityFeedbackReport report) {
		super(mathlet, mathlet.getAppletFrame(), null);
		
		setDialogID(DIALOG_ID);
		setTitle(mathlet.getMessage("errorhandler.title.bar"));
		setContentTitle(mathlet.getMessage("errorhandler.title.content"));
		addText(mathlet.getMessage("errorhandler.text.intro"));
		insertLineBreak();
		m_detailsButton = new JButton(new AbstractAction(mathlet.getMessage("errorhandler.button.details")) {
			public void actionPerformed(ActionEvent e) {
				new QualityFeedbackDetailsDialog(getContext(), ErrorCaughtDialog.this, report).showDialog();
			}
		});
		addControl(m_detailsButton);
		m_detailsButton.setVisible(mathlet.isOfflineMode()); // button is only visible in offline mode
		m_reportButton = new JButton(new AbstractAction(mathlet.getMessage("errorhandler.button.report")) {
			public void actionPerformed(ActionEvent e) {
				ReportErrorDialog dialog = new ReportErrorDialog(getContext(), ErrorCaughtDialog.this, report);
				DialogAction option = dialog.showDialog();
				if(option.getActionType() == DialogAction.APPROVE_ACTION) {
					report.setUserDescription(dialog.getUserComment());
					report.setUserMailAddress(dialog.getUserMailAddress());
					getContext().getMathletRuntime().getErrorHandler().sendReport(report);
					// disable further report sending
					m_reportButton.setVisible(false);
					m_detailsButton.setVisible(true);
				}
			}
		});
		addControl(m_reportButton);
		m_reportButton.setVisible(mathlet.isOnlineMode()); // button is only visible in online mode
		insertLineBreaks(2);

		addText(mathlet.getMessage("errorhandler.text.outro"));
		insertLineBreak();
		
		if(mathlet.isDebugMode() == false)
			addAction(new DialogAction(this, new AbstractAction(getContext().getMessage("Restart_applet")) {
				public void actionPerformed(ActionEvent e) {
					getContext().getRuntimeSupport().reloadApplet();
				}
			}, true));
		addAction(DialogAction.IGNORE_ACTION, getContext().getMessage("dialog.action.ignore"));
		
		pack();
	}
}
