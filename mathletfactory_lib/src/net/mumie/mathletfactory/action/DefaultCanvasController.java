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

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.mumie.mathletfactory.action.handler.global.GlobalHandler;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 *  This class implements the functionality for translating Java AWT-events
 *  to mumie {@link MMEvent}s. It does so by implementing the Java
 *  <code>Mouse[Motion]Listener</code>, <code>KeyListener</code> and
 *  <code>ComponentListener</code> (bundled together in {@link CanvasControllerIF})
 *  and making them call {@link DefaultCanvasController#controlAction controlAction()},
 *  which walks through the potential targets
 *  ({@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF CanvasObject}s
 *  or {@link net.mumie.mathletfactory.action.handler.MMHandler})
 *  and calls for each {@link net.mumie.mathletfactory.mmobject.MMObjectIF#doAction
 *  MMObject.doAction()} until the event reaches its target.<br>
 *  This class is always associated to exactly one
 *  {@link net.mumie.mathletfactory.display.MMCanvas}, for which it catches and
 *  delegates the events.
 *
 *  @author vossbeck
 *  @mm.docstatus finished
 */
public class DefaultCanvasController implements CanvasControllerIF {

	private static Logger logger =
		Logger.getLogger(DefaultCanvasController.class.getName());

	private MMCanvas m_canvas;

	private boolean m_isEnabled = true;

	private MMCanvasObjectIF m_selectedObject = null;

	private MMCanvasObjectIF m_activeObject = null;

	private ArrayList m_listOfSelectedObjects = new ArrayList();

	private MMEvent m_event = new MMEvent();
	private int m_keyCode = MMEvent.NO_KEY;
	private int m_keyCodeForMouseClick = MMEvent.NO_KEY;
	private int m_modifier = MMEvent.NO_MODIFIER_SET;

	private GlobalHandler m_activeGlobalHandler = null;

	private int m_lastPressedX;
	private int m_lastPressedY;
	private int m_lastClickedX;
	private int m_lastClickedY;

	/**
	 * Creates a new instance of a <code>DefaultCanvasController</code> without specifying
	 * the {@link net.mumie.mathletfactory.display.MMCanvas} for which it shall work. This
	 * new instance can be used in the
	 * {@link net.mumie.mathletfactory.display.MMCanvas#setController(net.mumie.mathletfactory.action.CanvasControllerIF) setController}
	 * method of a <code>MMCanvas</code>.
	 *
	 */
	public DefaultCanvasController() {
	}

	/**
	 * Creates a new instance of a <code>DefaultCanvasController</code> that explicitly
	 * shall work for the traversed <code>aCanvas</code>.
	 * <br>
	 * Obsever however, that you still have to call the method
	 * {@link net.mumie.mathletfactory.display.MMCanvas#setController(net.mumie.mathletfactory.action.CanvasControllerIF) setController}
	 * of <code>MMCanvas</code> to register this <code>DefaultCanvasController</code> as
	 * the controller used by <code>aCanvas</code>.
	 *
	 * @see net.mumie.mathletfactory.display.MMCanvas
	 * @mm.sideeffects After the method call the internal <code>MMCanvas</code>-field and
	 * <code>aCanvas</code> will point to the same object.
	 */
	public DefaultCanvasController(MMCanvas aCanvas) {
		setCanvas(aCanvas);
	}

	public void setEnabled(boolean aFlag) {
		m_isEnabled = aFlag;
	}

	public boolean isEnabled() {
		return m_isEnabled;
	}

	/**
	 * @mm.sideeffects the internal field for the <code>MMCanvas</code> will point to
	 * <code>aCanvas</code> after the method call.
	 * @see net.mumie.mathletfactory.action.CanvasControllerIF#setCanvas(net.mumie.mathletfactory.display.MMCanvas)
	 */
	public void setCanvas(MMCanvas aCanvas) {
		m_canvas = aCanvas;
		if (m_canvas.getDrawingBoard() == null) {
			if (!(m_canvas instanceof MM3DCanvas))
				// drawing board for MM3DCanvas is created after MMCanvas.<init>
				if (logger.isLoggable(Level.FINEST))
					logger.fine(
						"drawingBoard of canvas was null, no controller could be added");
			return;
		}
		m_canvas.getDrawingBoard().addMouseListener(this);
		m_canvas.getDrawingBoard().addMouseMotionListener(this);
		m_canvas.getDrawingBoard().addMouseWheelListener(this);
		m_canvas.getDrawingBoard().addKeyListener(this);
	}

	/**
	 * Returns the <code>MMCanvas</code> for which this <code>DefaultCanvasController
	 * </code> is dedicted to work.
	 *
	 * @see net.mumie.mathletfactory.action.CanvasControllerIF#getCanvas()
	 */
	public MMCanvas getCanvas() {
		return m_canvas;
	}

	/**
	 * Which Objects have been selected by the user?
	 */
	public MMCanvasObjectIF getPreselectedObject(int index) {
		return (MMCanvasObjectIF) m_listOfSelectedObjects.get(index);
	}

	public int getPreselectedObjectCount() {
    //System.out.println("preselected: "+m_listOfSelectedObjects);
		return m_listOfSelectedObjects.size();
	}

	public void addPreSelectedObject(MMCanvasObjectIF object) {
		m_listOfSelectedObjects.add(object);
		if(getSelectedObject() == null){
			setSelectedObject(object);
		}
	}

	/**
	 *  Sets the "focus" on the given CanvasObject, which results in a visual
	 *  mark on the Canvas and a higher priority in the Queue of event-targets.
	 */
	public void setSelectedObject(MMCanvasObjectIF object) {
		if (object == m_selectedObject)
			return;
		m_selectedObject = object;
		if (m_selectedObject != null && !(m_canvas instanceof MM3DCanvas))
			new Thread(new SelectionRenderer(m_selectedObject)).start();
	}

	public MMCanvasObjectIF getSelectedObject() {
		return m_selectedObject;
	}

	public MMCanvasObjectIF[] getSelectedObjects() {
		throw new TodoException();
	}

	public void flashObject(MMCanvasObjectIF object) {
		flashObjects(new MMCanvasObjectIF[] { object });
	}

	public void flashObjects(MMCanvasObjectIF[] objects) {
		new Thread(new FlashRenderer(objects, 6000)).start();
	}

	public void flashObjects(MMCanvasObjectIF[] objects, long duration) {
		new Thread(new FlashRenderer(objects, duration)).start();
	}

	public void flashObjects(
		MMCanvasObjectIF[] objects,
		long duration,
		int flashNum) {
		new Thread(new FlashRenderer(objects, duration, flashNum)).start();
	}

	private class FlashRenderer implements Runnable {
		private final Object waitObject = new Object();
		private long duration;
		private int flashNum;
		private MMCanvasObjectIF[] flashObjects;

		public FlashRenderer(MMCanvasObjectIF[] flashObjects, long duration) {
			this(flashObjects, duration, 12);
		}

		public FlashRenderer(
			MMCanvasObjectIF[] flashObjects,
			long duration,
			int flashNum) {
			this.flashNum = flashNum;
			this.duration = duration;
			this.flashObjects = flashObjects;
		}

		public void run() {
			Color[] objectColors = new Color[flashObjects.length];
			Color[] backColors = new Color[flashObjects.length];
			ArrayList canvi = new ArrayList();

			for (int i = 0; i < flashObjects.length; i++) {
				DisplayProperties prop = flashObjects[i].getDisplayProperties();
				MMCanvas canvas = flashObjects[i].getCanvas();
				objectColors[i] = prop.getObjectColor();
				backColors[i] = canvas.getBackground();
				if (canvi.indexOf(canvas) < 0) {
					canvi.add(canvas);
				}
			}
			int flashDone = flashNum;
			while (flashDone > 0) {
				for (int i = 0; i < flashObjects.length; i++) {
					DisplayProperties prop = flashObjects[i].getDisplayProperties();
					prop.setObjectColor(
						flashDone % 2 == 1 ? objectColors[i] : backColors[i]);
				}
				flashDone--;
				for (Iterator iter = canvi.iterator(); iter.hasNext();) {
					MMCanvas canvas = (MMCanvas) iter.next();
					canvas.repaint();
				}
				try {
					synchronized (waitObject) {
						waitObject.wait(duration / flashNum);
					}
				}
				catch (Exception e) {
					//egal, malen wir halt eher, aber Nachricht ins Log
					if (logger.isLoggable(java.util.logging.Level.FINE))
						logger.fine(e.getMessage());
				}
			}
		}
	}

	private class SelectionRenderer implements Runnable {
		private Object selectedObject;
		public SelectionRenderer(Object selected) {
			selectedObject = selected;
		}

		public void run() {
			while (getSelectedObject() == selectedObject) {
				getSelectedObject().getCanvas().repaint();
				try {
					Thread.sleep(CanvasControllerIF.SELECTION_RENDER_UPDATE_PAUSE);
				}
				catch (InterruptedException e) {
					//egal, malen wir halt eher, aber Nachricht ins Log
					if (logger.isLoggable(java.util.logging.Level.FINE))
						logger.fine(e.getMessage());
				}
			}
		}
	}

	/**
	 *  called by {@link #controlAction}, walks through the list of
	 *  preselected Objects and invokes
	 * [@link net.mumie.mathletfactory.mmobject.MMObjectIF#doAction doAction()}
	 * in them.
	 */
	protected boolean distributeEvent(MMEvent event) {
		for (int j = 0; j < getPreselectedObjectCount(); j++) {
			MMCanvasObjectIF obj = getPreselectedObject(j);
			if (obj.doAction(event)) {
				m_activeObject = obj;
				m_listOfSelectedObjects.clear();
				return true;
			}
		}
		return false;
	}

	/**
	 * This is really the central method of any controller:
	 * It checks which handler (global or object related) must receive the event
	 * and calls the handler's doAction()-method
	 *
	 * The sequence of delivering the event is the following:
	 *<ol>
	 * <li> deliver event to active object(with fitting handler), if there is any</li>
	 * <li> check for objects that are selected by a mousePressedEvent and store
	 *      them.</li>
	 * <li> deliver event to active global handler (if there is any)</li>
	 * <li> check if there is any global handler that fits to the event</li>
	 * <li> if the are stored selected objects, check for the first with a handler
	 *      that can work with the event and mark (only) this as active object.</li>
	 *</ol>
	 */
	public void controlAction(MMEvent event) {
		if (m_activeObject != null) {
			if (m_activeObject.doAction(event)){
        m_selectedObject = m_activeObject;
				return;
      }
		}

		if (m_selectedObject != null && (m_listOfSelectedObjects.contains(m_selectedObject) || event.getEventType()< KeyEvent.KEY_LAST)) {
			if (m_selectedObject.doAction(event)) {
				m_activeObject = m_selectedObject;
				return;
			}
		}

		// test the event for all preselected objects in the canvas
		boolean actionPerformed = distributeEvent(event);

		// set the mmobject that consumed the event (the 'front' of the preselected mmobjects)
		// as selected object
		if (getPreselectedObjectCount() > 0) {
			setSelectedObject(getPreselectedObject(0));
			m_activeObject = getSelectedObject();
			m_listOfSelectedObjects.clear();
			m_canvas.repaint();
		}

		if(actionPerformed)
			return;

		// after no mmobject consumed the event, test for global handlers
		if (m_activeGlobalHandler != null) {
			if (m_activeGlobalHandler.doAction(event)) {
				return;
			}
		}

		for (int i = 0; i < m_canvas.getGlobalHandlerCount(); i++) {
			GlobalHandler handler = m_canvas.getGlobalHandler(i);
			if (handler.dealsWith(event)) {
				if (handler.doAction(event)) {
					m_activeGlobalHandler = handler;
					return;
				}
			}
		}
	}

	public void finishAction() {
		resetHandlerAndInternalData();
	}

	/**
	 *  Restores state variables (current key-code and modifiers) and calls
	 *  {@link net.mumie.mathletfactory.action.handler.MMHandler#finish}.
	 */
	protected void resetHandlerAndInternalData() {
		if (logger.isLoggable(java.util.logging.Level.FINE))
			logger.fine(getClass() + "::mouseReleased()");
		// resets the keyText:
		m_keyCode = MMEvent.NO_KEY;
		// resets the modifier (<shift>,<ctrl>,...)
		m_modifier = MMEvent.NO_MODIFIER_SET;

		if (m_activeGlobalHandler != null) {
			// clean up in the active global handler:
			m_activeGlobalHandler.finish();
			// reset the active global handler:
			m_activeGlobalHandler = null;
		}

		if (m_activeObject != null) {
			// reset active object:
			m_activeObject.getActiveHandler().finish();
			m_activeObject.resetActiveHandler();
			m_activeObject = null;
		}
	}

	/**
	 *@return the x position measured in pixel of the last mouseClicked event.
	 */
	public int getLastClickedX() {
		return m_lastClickedX;
	}

	/**
	 *@return the y position measured in pixel of the last mouseClicked event.
	 */
	public int getLastClickedY() {
		return m_lastClickedY;
	}

	/**
	 *@return the x position measured in pixel of the last mousePressed event.
	 */
	public int getLastPressedX() {
		return m_lastPressedX;
	}

	/**
	 *@return the y position measured in pixel of the last mousePressed event.
	 */
	public int getLastPressedY() {
		return m_lastPressedY;
	}

	// the sequence of the mouseEvents may unfortunately depend on the platform,
	// so programming must be independent of
	// pressed() <-> released() <-> clicked() <-> ...
	/**
	 * This method only will store the x,y position of the MouseEvent.
	 */
	public void mouseClicked(MouseEvent me) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass() + "::mouseClicked()");
			m_lastClickedX = me.getX();
			m_lastClickedY = me.getY();
			m_event.setEventType(me.getID());
			if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON1_MASK);
			else if ((me.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON2_MASK);
			else if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON3_MASK);
			m_event.setClickCount(me.getClickCount());
			m_event.setKeyCode(m_keyCodeForMouseClick);
			if (m_modifier == MMEvent.NO_MODIFIER_SET) {
				if (me.isShiftDown()) {
					m_event.setModifier(InputEvent.SHIFT_MASK);
					m_modifier = InputEvent.SHIFT_MASK;
				}
				else if (me.isControlDown()) {
					m_event.setModifier(InputEvent.CTRL_MASK);
					m_modifier = InputEvent.CTRL_MASK;
				}
				else if (me.isAltDown()) {
					m_event.setModifier(InputEvent.ALT_MASK);
					m_modifier = InputEvent.ALT_MASK;
				}
				else {
					m_event.setModifier(MMEvent.NO_MODIFIER_SET);
					m_modifier = MMEvent.NO_MODIFIER_SET;
				}
			}
			m_event.setX(me.getX());
			m_event.setY(me.getY());
			controlAction(m_event);
			m_keyCodeForMouseClick = MMEvent.NO_KEY;
			resetHandlerAndInternalData();
			// reset list of selected objects
			if (getPreselectedObjectCount() > 0) {
				m_listOfSelectedObjects.clear();
			}
			// is already dun in mousePressed, which will occur before the mouseClicked:
			//m_canvas.requestForFocus();
			m_canvas.repaint();
		}
	}

	/**
	 * When the mouse enters the canvas, the component of the canvas that is used
	 * to draw onto will request for KeyEvents also. For example this is necessary
	 * to use {@link #mouseDragged} for scaling of a scene.
	 */
	public void mouseEntered(MouseEvent me) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass().getName() + "::mouseEntered()");
		}
	}

	/**
	 * This method is an empty implementation in the moment.
	 */
	public void mouseExited(MouseEvent me) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass() + "::mouseExited()");
		}
	}

	/**
	 * When a mousePressend event occurs, this method is invoked and will initialize
	 * the DefaultCanvasController's internal {@link MMEvent} and finally call the
	 * controller's {@link #controlAction} method.
	 */
	public void mousePressed(MouseEvent me) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass() + "::mousePressed()");
			m_lastPressedX = me.getX();
			m_lastPressedY = me.getY();
			m_event.setEventType(me.getID());
			if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON1_MASK);
			else if ((me.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON2_MASK);
			else if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON3_MASK);
			m_event.setClickCount(me.getClickCount());
			m_event.setKeyCode(m_keyCode);
			if (m_modifier == MMEvent.NO_MODIFIER_SET) {
				//if (true) {
				if (me.isShiftDown()) {
					m_event.setModifier(InputEvent.SHIFT_MASK);
					m_modifier = InputEvent.SHIFT_MASK;
				}
				else if (me.isControlDown()) {
					m_event.setModifier(InputEvent.CTRL_MASK);
					m_modifier = InputEvent.CTRL_MASK;
				}
				else if (me.isAltDown()) {
					m_event.setModifier(InputEvent.ALT_MASK);
					m_modifier = InputEvent.ALT_MASK;
				}
				else {
					m_event.setModifier(MMEvent.NO_MODIFIER_SET);
					m_modifier = MMEvent.NO_MODIFIER_SET;
				}
			}
			m_event.setX(me.getX());
			m_event.setY(me.getY());
			m_canvas.requestForFocus();

			controlAction(m_event);
		}
	}

	/**
	 * When a mouseReleased event occurs the action process due to the current
	 * MMEvent will be finished. This method cleans up and resets all internally
	 * changed fields (i.e. keycode, mouse event type, selected MMObject).
	 */
	public void mouseReleased(MouseEvent me) {
		if (m_isEnabled) {
			finishAction();
			// reset list of selected objects
			if (getPreselectedObjectCount() > 0) {
				m_listOfSelectedObjects.clear();
			}
		}
	}

	/**
	 * When a mouseMotionEvent occurs this method is invoked and will initialize
	 * the @see DefautlController's internal @see MMEvent and finally call the
	 * controller's @see controlAction(MMEvent event) method.
	 */
	public void mouseDragged(MouseEvent me) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass() + "::mouseDragged()");
			m_event.setEventType(me.getID());
			if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON1_MASK);
			else if ((me.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON2_MASK);
			if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON3_MASK);
			// Apple Java compatibility issue:
			// getClickCount() returns 1 whereas under Windows/Linux it returns 0
			m_event.setClickCount(0); // me.getClickCount()
			m_event.setKeyCode(m_keyCode);
			if (m_modifier == MMEvent.NO_MODIFIER_SET) {
				if (me.isShiftDown()) {
					m_event.setModifier(InputEvent.SHIFT_MASK);
					m_modifier = InputEvent.SHIFT_MASK;
				}
				else if (me.isControlDown()) {
					m_event.setModifier(InputEvent.CTRL_MASK);
					m_modifier = InputEvent.CTRL_MASK;
				}
				else if (me.isAltDown()) {
					m_event.setModifier(InputEvent.ALT_MASK);
					m_modifier = InputEvent.ALT_MASK;
				}
				else {
					m_event.setModifier(MMEvent.NO_MODIFIER_SET);
					m_modifier = MMEvent.NO_MODIFIER_SET;
				}

			}
			m_event.setX(me.getX());
			m_event.setY(me.getY());
			controlAction(m_event);
		}
	}

	/**
	 * This method has an empty implementation in the moment.
	 */
	public void mouseMoved(MouseEvent me) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass().getName() + "::mouseMoved()");
			m_event.setEventType(me.getID());
			if ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON1_MASK);
			else if ((me.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON2_MASK);
			if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
				m_event.setMouseButton(InputEvent.BUTTON3_MASK);
			m_event.setClickCount(me.getClickCount());
			m_event.setKeyCode(m_keyCode);
			if (m_modifier == MMEvent.NO_MODIFIER_SET) {
				if (me.isShiftDown()) {
					m_event.setModifier(InputEvent.SHIFT_MASK);
					m_modifier = InputEvent.SHIFT_MASK;
				}
				else if (me.isControlDown()) {
					m_event.setModifier(InputEvent.CTRL_MASK);
					m_modifier = InputEvent.CTRL_MASK;
				}
				else if (me.isAltDown()) {
					m_event.setModifier(InputEvent.ALT_MASK);
					m_modifier = InputEvent.ALT_MASK;
				}
				else {
					m_event.setModifier(MMEvent.NO_MODIFIER_SET);
					m_modifier = MMEvent.NO_MODIFIER_SET;
				}
			}
			m_event.setX(me.getX());
			m_event.setY(me.getY());
			controlAction(m_event);
		}
	}

  public void mouseWheelMoved(MouseWheelEvent mwe) {
    if(!m_isEnabled)
      return;
		if (logger.isLoggable(java.util.logging.Level.FINE))
			logger.fine(getClass().getName() + "::mouseWheelMoved()");
		m_event.setEventType(mwe.getID());
		m_event.setMouseWheelRotation(mwe.getWheelRotation());
		m_event.setX(mwe.getX());
		m_event.setY(mwe.getY());
		controlAction(m_event);
  }
	// in general the keyPressed() method implemented from KeyListener
	// "hears" to primitive KeyEvents. We need to use this method for processing
	// KeyEvents because we also use not printable characters like <Shift>, <Ctrl>

	/**
	 * This method extracts the keycode and the modifier of the KeyEvent by
	 * using the methods getKeyCode() and getModifiers() from KeyEvent. The
	 * derived values are internally stored in the controller's {@link MMEvent}
	 * and by that way may be used for further processing.<br>
	 * In the moment the controller's {@link #controlAction controlAction()}
	 * method will not be called automatically: That means, that a single
	 * KeyEvent will not start any action in our applets.
	 */
	public void keyPressed(KeyEvent ke) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass() + "::keyPressed()");
			if (isKeyEventForMouse(ke)) {
				m_keyCode = ke.getKeyCode();
				m_keyCodeForMouseClick = ke.getKeyCode();
			}
			//dirty solution, for the moment only:
			else if (isKeyEventWithAction(ke)) {
				m_event =
					new MMEvent(ke.getID(), 0, 0, ke.getKeyCode(), ke.getModifiers());
				controlAction(m_event);
			}
			else
				return;
		}
	}

	/*
	 * When a key is released, the handler needs to be reset.
	 */
	public void keyReleased(KeyEvent ke) {
		if (m_isEnabled) {
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(getClass() + "::keyReleased()");
			m_keyCode = MMEvent.NO_KEY;
			m_keyCodeForMouseClick = MMEvent.NO_KEY;
			m_modifier = MMEvent.NO_MODIFIER_SET;
			if (isKeyEventWithAction(ke))
				finishAction();
		}
	}

	/**
	 * This method has an empty implementation in the moment.
	 */
	public void keyTyped(KeyEvent ke) {
		if (m_isEnabled) {
			//perhaps do anything
		}
	}

	void resetKeyCode() {
		m_keyCode = MMEvent.NO_KEY;
	}

	private boolean isKeyEventWithAction(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_TAB
			|| e.getKeyCode() == KeyEvent.VK_ESCAPE
			|| e.getKeyCode() == KeyEvent.VK_LEFT
			|| e.getKeyCode() == KeyEvent.VK_RIGHT
			|| e.getKeyCode() == KeyEvent.VK_UP
			|| e.getKeyCode() == KeyEvent.VK_DOWN
			|| e.getKeyCode() == KeyEvent.VK_PAGE_DOWN
			|| e.getKeyCode() == KeyEvent.VK_PAGE_UP
			|| e.getKeyCode() == KeyEvent.VK_V;

	}

	private boolean isKeyEventForMouse(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_C
			|| e.getKeyCode() == KeyEvent.VK_R
			|| e.getKeyCode() == KeyEvent.VK_X
			|| e.getKeyCode() == KeyEvent.VK_Y
			|| e.getKeyCode() == KeyEvent.VK_S
			|| e.getKeyCode() == KeyEvent.VK_T;
	}

	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
