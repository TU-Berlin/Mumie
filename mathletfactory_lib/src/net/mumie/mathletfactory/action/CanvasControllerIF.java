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

package net.mumie.mathletfactory.action;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * This interface mainly determines how an <b>action cycle</b> from
 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s that are registered within a
 * {@link net.mumie.mathletfactory.display.MMCanvas} will be started, controlled and
 * properly terminated (to get some more information about an "action cycle", please
 * refer to the class
 * {@link net.mumie.mathletfactory.action.ActionManager}).
 * <br><br>
 * To understand the role of a <code>CanvasControllerIF</code> it is necessary to know
 * how the interaction with <code>MMObjectIF</code>s should work: The user will in general
 * be able to "talk" with <code>MMObjectIF</code>s by doing something with the <b>mouse
 * </b> or the <b>keyboard</b>. A <code>MMObjectIF</code> rendered within a <code>
 * MMCanvas</code> however, will never be itsself an instance of
 * {@link java.awt.event.MouseListener} or {@link java.awt.event.KeyListener} - our
 * approach is that the {@link net.mumie.mathletfactory.display.MMCanvas} works as
 * listener for various types of events. In order not to overload the <code>MMCanvas</code>
 * with all the required methods (needed to be a specific listener) we decided to delegate
 * these purposes to the
 * {@link net.mumie.mathletfactory.action.CanvasControllerIF CanvasControllerIF}. Here
 * our philosophy is that each <code>MMCanvas</code> has a <b>unique</b> <code>
 * CanvasControllerIf</code> and that each instance of a <code>CanvasControllerIF</code>
 * is working for a <b>unique</b> <code>MMCanvas</code>.
 * <br><br>
 * The following are the <b>main tasks</b> of a <code>CanvasControllerIF</code>:
 * <ol>
 * <li>
 * Listen to all {@link java.awt.event.MouseEvent}s and {@link java.awt.event.KeyEvent}s
 * occuring in the drawing board of the <code>MMCanvas</code> (see
 * {@link net.mumie.mathletfactory.display.MMCanvas#getDrawingBoard()} for more information
 * about this)
 * </li>
 * <li>
 * Generate a {@link net.mumie.mathletfactory.action.MMEvent} from these native
 * java events.
 * </li>
 * <li>
 * Recording information about former happended mouse clicks and mouse presses.
 * </li>
 * <li>
 * Traverse the generated <code>MMEvent</code> to the <code>MMObjectIF</code>s registered
 * in the <code>MMCanvas</code>. This will be done in the central
 * {@link #controlAction(net.mumie.mathletfactory.action.MMEvent) controlAction} method.
 * In general, if some of the registered <code>MMObjectIF</code>s may then get control
 * about the action cycle.
 * </li>
 * <li>
 * Recording which <code>MMObjectIF</code>s in the <code>MMCanvas</code> are currently
 * selected to receive the next <code>MMEvent</code>s.
 * </li>
 * </ol>
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public interface CanvasControllerIF
	extends
		MouseListener,
		MouseMotionListener,
		MouseWheelListener,
		KeyListener,
		Cloneable {

  /** The pause (in milliseconds) where the selection render thread waits to
   * repaint the selection border of the selected object. */
  public final static long SELECTION_RENDER_UPDATE_PAUSE = 500;

	public void setEnabled(boolean aFlag);

	public boolean isEnabled();

	public void controlAction(MMEvent event);

	public void finishAction();

	public void addPreSelectedObject(MMCanvasObjectIF object);

	public int getPreselectedObjectCount();

	public void setSelectedObject(MMCanvasObjectIF object);

	public MMCanvasObjectIF getSelectedObject();

	/**
	 * @return the x-coordinate of the last occured <code>MouseEvent</code> that was
	 * a mouse click.
	 */
	public int getLastClickedX();

	/**
	* @return the y-coordinate of the last occured <code>MouseEvent</code> that was
	* a mouse click.
	*/
	public int getLastClickedY();

	/**
	 * @return the x-coordinate of the last occured <code>MouseEvent</code> that was
	 * a mouse press.
	*/
	public int getLastPressedX();

	/**
	 * @return the y-coordinate of the last occured <code>MouseEvent</code> that was a
	 * mouse press.
	 */
	public int getLastPressedY();

	/**
	 * Makes this <code>MMCanvasControllerIF</code> be the
	 * {@link java.awt.event.MouseListener}, {@link java.awt.event.MouseMotionListener}
	 * and {@link java.awt.event.KeyListener} of the drawing board of <code>aCanvas</code>.
	 * <br>
	 * <b>Observe</b>: Although this method has modifier <code>public</code> it is not
	 * really intended for public use. The call of this method should only occur from
	 * within the
	 * {@link net.mumie.mathletfactory.display.MMCanvas#setController(net.mumie.mathletfactory.action.CanvasControllerIF) setController}
	 * method from the <code>MMCanvas</code>.
	 *
	 * @param <code>aCanvas</code> is the <code>MMCanvas</code> this <code>
	 * CanvasControllerIF</code> will be working for.
	 *
	 * @mm.sideeffects the internal field for the <code>MMCanvas</code> will point to
	 * <code>aCanvas</code> after the method call.

	 */
	public void setCanvas(MMCanvas aCanvas);

	/**
	 * Returns the <code>MMCanvas</code> for which this instance of
	 * {@link net.mumie.mathletfactory.action.CanvasControllerIF}
	 * is working as listener.
	 *
	 * @return
	 */
	public MMCanvas getCanvas();

	public Object clone();

  /**
   * The flash methods should be used to attract interest by the user. It is the
   * Controllers job to find a appropriate method for it. Blinking could be a
   * possibility.<p>
   * An empty implementation could be used as a first step.
   * @param object
   */
  public void flashObject(MMCanvasObjectIF object);
  public void flashObjects(MMCanvasObjectIF[] objects);
  public void flashObjects(MMCanvasObjectIF[] objects, long duration);
  public void flashObjects(MMCanvasObjectIF[] objects, long duration, int flashNum);


}
