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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public class DialogAction extends AbstractAction {

	public final static int UNKNOWN_ACTION = -1;
	
	public final static int ANSWER_NO_ACTION = 0;
	
	public final static int ANSWER_YES_ACTION = 1;
	
	public final static int APPROVE_ACTION = 2;
	
	public final static int CANCEL_ACTION = 3;
	
	public final static int CLOSE_ACTION = 4;
	
	public final static int IGNORE_ACTION = 5;
	
	public final static int APPLY_ACTION = 6;
	
	public final static int RETRY_ACTION = 7;
	
	public final static int USER_DEFINED_ACTION = 10;
	
	private final int m_action;
	
	private AbstractAction m_userAction;
	
	private boolean m_hideDialog;
	
	private final DefaultDialog m_dialog;
	
	public DialogAction(DefaultDialog dialog, int action, boolean hideDialog, String name, Icon icon) {
		super(name);
		if(icon != null)
			putValue(Action.SMALL_ICON, icon);
		m_dialog = dialog;
		m_action = action;
		m_hideDialog = hideDialog;
	}
	
	public DialogAction(DefaultDialog dialog, AbstractAction action, boolean hideDialog) {
		super((String) action.getValue(Action.NAME), (Icon) action.getValue(Action.SMALL_ICON));
		m_dialog = dialog;
		m_action = USER_DEFINED_ACTION;
		m_userAction = action;
		m_hideDialog = hideDialog;
	}
	
	public void actionPerformed(ActionEvent e) {
		// do nothing if action is unknown (i.e. not set)
		if(getActionType() == UNKNOWN_ACTION)
			return;
		// hide dialog ?
		if(hideDialog())
			getDialog().dispose();
		// choose action
		switch(getActionType()) {
		case IGNORE_ACTION:
			// do nothing
			break;
		case USER_DEFINED_ACTION:
			if(m_userAction != null)// may be null!
				m_userAction.actionPerformed(e);
			break;
		}
	}
	
	public int getActionType() {
		return m_action;
	}

	String getName() {
		return (String) getValue(Action.NAME);
	}
	
	Icon getIcon() {
		return (Icon) getValue(Action.SMALL_ICON);
	}
	
	boolean hideDialog() {
		return m_hideDialog;
	}
	
	private DefaultDialog getDialog() {
		return m_dialog;
	}
}
