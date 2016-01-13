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

import java.lang.reflect.Constructor;

import javax.swing.ImageIcon;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.util.error.ErrorCaughtDialog;
import net.mumie.mathletfactory.util.error.QualityFeedbackReport;
import net.mumie.mathletfactory.util.extension.DialogActionDefinition;
import net.mumie.mathletfactory.util.extension.DialogDefinition;
import net.mumie.mathletfactory.util.extension.DialogExtensionMap;

public class DialogManager {

	private final MathletContext m_context;
	
	private DialogExtensionMap m_dialogs = new DialogExtensionMap();
	
	public DialogManager(MathletContext context) {
		m_context = context;
	}
	
	public MathletContext getContext() {
		return m_context;
	}
	
	public void addDialogs(DialogExtensionMap dialogs) {
		m_dialogs.addAll(dialogs);
	}
	
	/**
	 * Brings up the dialog with the given ID, executes and returns the selected action.
	 * The parameters' types are dependant of the choosen dialog ID.
	 *  
	 * @param dialogID an ID referencing a dialog
	 * @param params a list of parameters; dependant of the dialog ID; may be <code>null</code>
	 * @return an instance of <code>DialogAction</code> describing the action
	 */
	public DialogAction showDialog(int dialogID, Object[] params) {
		DialogDefinition definition = m_dialogs.getDialogDefinition(dialogID);
		if(definition == null)
			throw new IllegalArgumentException("No dialog registered with ID \"" + dialogID + "\"!");
		try {
			Constructor c = definition.getDialogClass().getConstructor(new Class[] {MathletContext.class, Object[].class});
			DefaultDialog dialog = (DefaultDialog) c.newInstance(new Object[] {getContext(), params});
			dialog.setDialogID(definition.getID());
			dialog.setType(definition.getType());
			dialog.setModal(definition.isModal());
			String dialogTitle = getDefaultDialogTitle(definition.getType());
			dialog.setTitle(dialogTitle);
			String contentTitle = definition.getTitle() == null ? dialogTitle : getContext().getMessage(definition.getTitle());
			dialog.setContentTitle(contentTitle);
			if(definition.getText() != null)
				dialog.addText(definition.getText(), true);
			dialog.setIcon(new ImageIcon(getClass().getResource(getDefaultIconPath(definition.getType()))));
			dialog.finishGenericLayout();
			DialogActionDefinition[] actions = definition.getActions();
			for(int i = 0; i < actions.length; i++) {
				String actionName = actions[i].getActionName();
				if(actionName == null)
					actionName = getDefaultActionName(actions[i].getActionType());
				dialog.addAction(new DialogAction(dialog, actions[i].getActionType(), actions[i].hideDialog(), actionName, null));
			}
			return dialog.showDialog();
		} catch (Exception e) {
			e.printStackTrace();
			return new ErrorCaughtDialog(getContext(), new QualityFeedbackReport(getContext(), 
					new RuntimeException("Cannot instantiate dialog with ID " + dialogID, e))).showDialog();
		}
	}
	
	private String getDefaultActionName(int actionType) {
		switch(actionType) {
		case DialogAction.ANSWER_NO_ACTION:
			return getContext().getMessage("dialog.action.no");
		case DialogAction.ANSWER_YES_ACTION:
			return getContext().getMessage("dialog.action.yes");
		case DialogAction.APPROVE_ACTION:
			return getContext().getMessage("dialog.action.approve");
		case DialogAction.CANCEL_ACTION:
			return getContext().getMessage("dialog.action.cancel");
		case DialogAction.CLOSE_ACTION:
			return getContext().getMessage("dialog.action.close");
		case DialogAction.IGNORE_ACTION:
			return getContext().getMessage("dialog.action.ignore");
		case DialogAction.APPLY_ACTION:
			return getContext().getMessage("dialog.action.apply");
		case DialogAction.RETRY_ACTION:
			return getContext().getMessage("dialog.action.retry");
		}
		throw new IllegalArgumentException("Unknown action type used: " + actionType);
	}
	
	private String getDefaultDialogTitle(int dialogType) {
		switch(dialogType) {
		case DefaultDialog.INFO_DIALOG:
			return getContext().getMessage("dialog.title.info");
		case DefaultDialog.WARNING_DIALOG:
			return getContext().getMessage("dialog.title.warning");
		case DefaultDialog.QUESTION_DIALOG:
			return getContext().getMessage("dialog.title.question");
		case DefaultDialog.ERROR_DIALOG:
			return getContext().getMessage("dialog.title.error");
		}
		throw new IllegalArgumentException("Unknown dialog type used: " + dialogType);
	}
	
	private String getDefaultIconPath(int dialogType) {
		switch(dialogType) {
		case DefaultDialog.INFO_DIALOG:
			return "/resource/icon/info24.gif";
		case DefaultDialog.WARNING_DIALOG:
			return "/resource/icon/info24.gif";
		case DefaultDialog.QUESTION_DIALOG:
			return "/resource/icon/info24.gif";
		case DefaultDialog.ERROR_DIALOG:
			return "/resource/icon/error.png";
		}
		throw new IllegalArgumentException("Unknown dialog type used: " + dialogType);
	}
}
