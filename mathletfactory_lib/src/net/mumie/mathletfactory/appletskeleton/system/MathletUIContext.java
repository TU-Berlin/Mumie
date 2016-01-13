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

package net.mumie.mathletfactory.appletskeleton.system;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;

import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogAction;

/**
 * This interface describes the GUI capabilities of mathlets.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public interface MathletUIContext {

	/** Constant for adding components to the control pane. */
	public final static int CONTROL_PANE = 0;
	
	/** Constant for adding components to the left bottom pane. */
	public final static int BOTTOM_LEFT_PANE = 1;
	
	/** Constant for adding components to the right bottom pane. */
	public final static int BOTTOM_RIGHT_PANE = 2;
	
	/** Constant for adding components to the center bottom pane. */
	public final static int BOTTOM_CENTER_PANE = 3;
	
	/** Constant for adding components to the top/title pane. */
	public final static int TOP_PANE = 4;
	
	/** Constant for adding components to the bottom pane. */
	public final static int BOTTOM_PANE = BOTTOM_CENTER_PANE;
	
	/**
	 * Returns the content (root) pane of this mathlet.
	 */
	Container getAppletContentPane();
	
	/**
	 * Returns the frame containing this mathlet (if available). May be <code>null</code>.
	 */
	JFrame getAppletFrame();
	
	/**
	 * Adds a control (i.e. a GUI component) to this mathlet at the specified position.
	 * 
	 * @see #CONTROL_PANE
	 * @see #BOTTOM_PANE
	 * @see #BOTTOM_LEFT_PANE
	 * @see #BOTTOM_RIGHT_PANE
	 * @see #BOTTOM_CENTER_PANE
	 * @see #TOP_PANE
	 */
	void addControl(Component c, int location);
	
	/**
	 * Removes a control (i.e. a GUI component) from this mathlet from the specified position.
	 * 
	 * @see #CONTROL_PANE
	 * @see #BOTTOM_PANE
	 * @see #BOTTOM_LEFT_PANE
	 * @see #BOTTOM_RIGHT_PANE
	 * @see #BOTTOM_CENTER_PANE
	 * @see #TOP_PANE
	 */
	void removeControl(Component c, int location);
	
  /**
   * Sets the title of this applet that will be displayed on top.
   */
	void setTitle(String title);
	
  /**
   * Sets the title component of this applet that will be displayed on top.
   */
	void setTitle(Component c);
	
  /**
   * Sets whether the title should be visible or not.
   */
	void setTitleVisible(boolean visible);
	
	/**
	 * Brings up a message dialog containing the given message.
	 * @param message message (mostly a {@link String})
	 */
	void showMessageDialog(Object message);
	
	/**
	 * Brings up a message dialog containing the given message and a help button for the given
	 * help message ID.
	 * @param message message (mostly a {@link String})
	 */
	void showMessageDialog(Object message, String messageID);
	
	/**
	 * Brings up an error dialog containing the given message.
	 * @param message message (mostly a {@link String})
	 */
	void showErrorDialog(Object message);
	
	/**
	 * Brings up a confirm dialog containing the given message and returns the answer
	 * as a boolean (only YES and NO are available in the dialog).
	 * 
	 * @param message message (mostly a {@link String})
	 * @param title the title of the confirm dialog
	 */
	boolean showConfirmDialog(Object message, String title);
	
	/**
	 * Brings up the dialog with the given ID, executes and returns the selected action.
	 * The parameters' types are dependant of the choosen dialog ID.
	 *  
	 * @param dialogID an ID referencing a dialog
	 * @param params a list of parameters; dependant of the dialog ID; may be <code>null</code>
	 * @return an instance of <code>DialogAction</code> describing the action
	 */
	DialogAction showDialog(int dialogID, Object[] params);
}
