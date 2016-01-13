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

package net.mumie.mathletfactory.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.mumie.mathletfactory.action.CanvasControllerIF;
import net.mumie.mathletfactory.action.DefaultCanvasController;
import net.mumie.mathletfactory.action.handler.global.GlobalHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalKeyboardSelectionHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalMouseSelectionHandler;
import net.mumie.mathletfactory.action.handler.global.MouseShapeHandler;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.appletskeleton.BaseApplet;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.appletskeleton.util.theme.PlainButton;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.CanvasImage;
import net.mumie.mathletfactory.util.CanvasMessage;

/**
 * This Class acts as a base class for all drawing areas in the MM-System. It
 * holds the functionality for adding, retrieving and removing
 * {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF CanvasObject}s,
 * which do the rendering for themselves (i.e. via
 * {@link net.mumie.mathletfactory.display.CanvasDrawable}s). The
 * event-handling is delegated to a dedicated {@link #m_controller Controller}.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public abstract class MMCanvas
	extends JPanel
	implements ComponentListener, FocusListener, SpecialCaseListener {

	public final static int DIMENSION_2D = 2;
	
	public final static int DIMENSION_3D = 3;
	
	/** Vector containing all CanvasMessage's. */
	private Vector m_canvasMessages = new Vector(0);

	private static Logger logger = Logger.getLogger(MMCanvas.class.getName());
	
	private static Map m_canvasClasses = new HashMap();

	/**
	 * stores the registered <code>MMObjectIF</code>s.
	 */
	protected final ArrayList m_objects = new ArrayList();

	/**
	 * stores the registered <code>CanvasImage</code>s.
	 */
	protected final ArrayList m_images = new ArrayList();

	/**
	 * stores the registered global handlers.
	 */
	protected ArrayList m_globalHandlers = new ArrayList();

	/**
	 * the (unique) <code>CanvasControllerIF</code> for this <code>MMCanvas</code>
	 */
	protected final CanvasControllerIF m_controller;

	/**
	 * Flag for communication with subclasses.
	 */
	protected boolean m_isSceneRendered = false;

	/**
	 * A Flag indicating that the FocusBorder is shown when the canvas receives
	 * focus.
	 */
	protected boolean m_showFocusBorder = true;

	/** A flag indicating that the coordinates under the mouse cursor shoud be displayed.*/
	protected boolean m_showMouseCoords = false;

	/** Flag indicating that the render cycle should restart if it is currently executing. */
	protected boolean m_restartRenderCycle = false;

	public final static int NO_TOOLBAR = 0x0;
	public final static int VERTICAL_TOOLBAR = 0x1;
	public final static int HORIZONTAL_TOOLBAR = 0x2;
	public final static int ROTATE_BUTTONS = 0x4;
	public final static int SCROLL_BUTTONS = 0x8;

	protected JButton m_btnScrollUp;
	protected JButton m_btnScrollDown;
	protected JButton m_btnScrollLeft;
	protected JButton m_btnScrollRight;

	protected JToolBar m_toolBar = new JToolBar();
	protected int m_toolFlags;

	// for measuring performance
	private long m_before = 0;
	private double m_FrameRate; // frames per second
	protected boolean m_calculateFPS = false;
	protected long m_fpsTimeCounter;
	protected int m_fpsImageCounter = 0;

	protected CanvasMessage m_specialCaseCanvasMessage;

	public static final Border FOCUSED_BORDER =
		BorderFactory.createLineBorder(MetalLookAndFeel.getFocusColor(), 3);
	public static final Border UNFOCUSED_BORDER =
		BorderFactory.createLineBorder(MetalLookAndFeel.getWindowBackground(), 3);

	/**
	 * Creates a new <code>MMCanvas</code> that uses a
	 * {@link net.mumie.mathletfactory.action.DefaultCanvasController}as
	 * listener for java events in it's drawing board. <br>This <code>MMCanvas</code>
	 * already offers some basic interaction facilities.
	 * <ol>
	 * <li>mouse shape changes under selectable <code>MMObjectIF</code> (see
	 * {@link net.mumie.mathletfactory.action.handler.global.MouseShapeHandler})
	 * </li>
	 * <li>walking through selectable <code>MMObjectIF</code> s by hitting the
	 * <b>TAB</b> or <b>Shift+TAB</b> key. (see
	 * {@link net.mumie.mathletfactory.action.handler.global.GlobalMouseSelectionHandler})
	 * </li>
	 * <li>selecting objects by clicking on them with the mouse pointer (see
	 * {@link net.mumie.mathletfactory.action.handler.global.GlobalKeyboardSelectionHandler})
	 * </li>
	 * </ol>
	 */
	public MMCanvas() {
		this(new DefaultCanvasController());
	}

	/**
	 * Creates a new <code>MMCanvas</code> that uses <code>aCanvasController</code>
	 * as listener for java events in it's drawing board.
	 */
	public MMCanvas(final CanvasControllerIF aCanvasController) {
		m_controller = aCanvasController;
		m_controller.setCanvas(this); //controller hears for this canvas
		drawFocus(false);
		new GlobalMouseSelectionHandler(this);
		new GlobalKeyboardSelectionHandler(this);
		new MouseShapeHandler(this);
		// necessary, because java3D canvas is not a JComponent and it is null at this point:
		if (getDrawingBoard() instanceof JComponent) {
			getDrawingBoard().addFocusListener(this);
			getDrawingBoard().addComponentListener(this);
		}
		m_specialCaseCanvasMessage =
			new CanvasMessage(
				this,
				null,
				3000,
				CanvasMessage.CENTERED,
				CanvasMessage.INFO_MESSAGE);
	}

	public void setControllerEnabled(boolean aFlag) {
		m_controller.setEnabled(aFlag);
	}

	public boolean isControllerEnabled() {
		return m_controller.isEnabled();
	}

	/**
	 * Indicates whether the render cycle should restart if it is currently executing.
	 */
	public void setRestartRenderCycle(boolean restart) {
		m_restartRenderCycle = restart;
	}

  public void reloadProperties() {
    boolean showScrollButtons = MumieTheme.getThemeProperty("Canvas.scrollbuttons.visible",
        "true").equals("true");
    setScrollButtonsVisible(showScrollButtons);
    boolean showToolbar = MumieTheme.getThemeProperty("Canvas.toolbar.visible",
    "true").equals("true");
    setToolbarVisible(showToolbar);
    validate();
  }
	/**
	 * Adds an <code>MMObjectIF</code> to this <code>MMCanvas</code> at the
	 * last position of all registered (added) <code>MMObjectIF</code>s.<br>
	 * The traversed {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF}
	 * will be added to the "list of registered objects" within this <code>MMCanvas</code>
	 * and later displayed in it. <br>Observe that <code>MMObjectIF</code> s
	 * that are <b>compound objects</b> (see
	 * {@link net.mumie.mathletfactory.mmobject.compound.MMCompoundCanvasObjectIF})
	 * are added to a <code>MMCanvas</code> by adding their defining
	 * components.
	 */
	public void addObject(MMCanvasObjectIF object) {
		if (getTopLevelAncestor() instanceof BaseApplet)
			object.addSpecialCaseListener((BaseApplet)getTopLevelAncestor());
		addSingleObject(object, -1);
	}

	/**
	 * Adds every object contained in the given list to this canvas.
	 * This is a convenience method for {@link #addObject(MMCanvasObjectIF)} calls on every list's item.
	 * @param objects a list of <code>MMObjectIF</code>
	 */
	public void addAllObjects(MMCanvasObjectIF[] objects) {
		for(int i = 0; i < objects.length; i++)
			addObject(objects[i]);
	}
	/**
	 * Adds a <code>MMCanvasObjectIF</code> to this <code>MMCanvas</code> at
	 * the desired position in the object list of this <code>MMCanvas</code>.
	 *
	 * @see #addObject(MMCanvasObjectIF)
	 * @param object
	 *          is the <code>MMCanvasObjectIF</code> to add to this <code>MMCanvas
	 * </code>.
	 * @param position
	 *          is the index in the list of <code>MMCanvasObjectIF</code> s for
	 *          this <code>MMCanvas</code>.
	 */
	public void addObject(MMCanvasObjectIF object, int position) {
		  if (getTopLevelAncestor() instanceof BaseApplet)
					object.addSpecialCaseListener((BaseApplet)getTopLevelAncestor());
//		object.addSpecialCaseListener(this);
			addSingleObject(object, position);

	}

  /**
   * Adds a single object with the given position index to this canvas.
   * 
   * @param object a MM object able to be drawn onto this canvas type
   * @param position the desired index for this object; <code>-1</code> adds it to the end
   */
	private void addSingleObject(MMCanvasObjectIF object, int position) {
		if (contains(object)) {
			if (logger.isLoggable(java.util.logging.Level.WARNING))
				logger.warning(
					"you tried to add +"
						+ object.getClass().getName()
						+ " a second time");
		} else {
      if(position > -1)
        m_objects.add(position, object);
      else
        m_objects.add(object);
			object.setCanvas(this);
			if (object.getCanvasTransformer() == null) {
        int transformType = object.getDefaultTransformTypeInCanvas();
        if(transformType == GeneralTransformer.NO_TRANSFORM_TYPE)
          throw new RuntimeException("No transform type specified for object " + object.getClass().getName());
				object.setCanvasTransformer(
          transformType,
					getScreenType());
      }
		}
		if (getDrawingBoard().isVisible()
        && getDrawingBoard().getSize().getWidth() > 0
        && getDrawingBoard().getSize().getHeight() > 0) {
          object.render();
          repaint();
		}
	}

	/**
	 * Removes the specified object from the canvas's object list.
	 * @param object the object to be removed
	 */
	public void removeObject(MMCanvasObjectIF object) {
		for (int i = 0; i < m_objects.size(); i++)
			if (object == m_objects.get(i))
				m_objects.remove(i);
		if (getTopLevelAncestor() instanceof BaseApplet) {
			object.removeSpecialCaseListener((BaseApplet)getTopLevelAncestor());
		}
	}

	/**
	 * Removes all objects of the type {@link Class} from the canvas's list of objects.
	 */
	public void removeAllObjectsOfType(Class type) {
		for (int i = 0; i < m_objects.size(); i++) {
			if (getObject(i).getClass() == type) {
				removeObject(getObject(i));
				i--;
			}
		}
	}

	/**
	 * Removes all objects from the canvas's list of objects.
	 */
	public void removeAllObjects() {
		while (getObjectCount() > 0)
			removeObject(getObject(getObjectCount() - 1));
	}

	/**
	 * Returns an {@link ArrayList} with all objects.
	 */
	public ArrayList getObjects() {
		return m_objects;
	}

	/**
	 * Returns an {@link ArrayList} with all objects of the type {@link Class}.
	 */
	public ArrayList getObjectsOfType(Class type) {
		ArrayList l = new ArrayList();
		for (int i = 0; i < m_objects.size(); i++) {
			if (getObject(i).getClass() == type) {
				l.add(getObject(i));
			}
		}
		return l;
	}

	/**
	 * Returns the number of objects contained in the canvas's list of objects.
	 */
	public int getObjectCount() {
		return m_objects.size();
	}

	/**
	 * Returns the index of the specified object in the canvas's list of objects or
	 * <code>-1</code> if no such object is found.
	 */
	public int getObjectIndex(MMCanvasObjectIF object) {
		for (int i = 0; i < m_objects.size(); i++)
			if (m_objects.get(i) == object)
				return i;
		return -1;
	}

	/**
	 * Returns the object with the specified index, <code>null</code> if no such object is found.
	 */
	public MMCanvasObjectIF getObject(int index) {
		if (index < 0)
			return null;
		return (MMCanvasObjectIF)m_objects.get(index);
	}

	/**
	 * Returns the object with the specified name, <code>null</code> if no such object is found.
	 */
	public MMCanvasObjectIF getObject(String nameOfObject) {
		MMCanvasObjectIF obj = null;
		for (int i = 0; i < getObjectCount(); i++) {
			obj = getObject(i);
			if (obj.getLabel().equals(nameOfObject))
				return obj;
		}
		return null;
	}

	/**
	 * Checks whether the object is contained in the canvas's list of objects.
	 */
	public boolean contains(MMCanvasObjectIF object) {
		if (getObjectIndex(object) > -1)
			return true;
		else
			return false;
	}

	//
	// Image-Support in canvas
	//

	/**
	 * Adds a new image at the end of the list of registered images.
	 */
	public void addImage(CanvasImage img) {
		if (getImageIndex(img) > -1) {
			logger.warning(
				"you tried to add the image \""
					+ img.getImageFileLocation()
					+ "\" a second time!");
			return;
		}
		m_images.add(img);
		img.setCanvas((MMG2DCanvas)this);
	}

	/**
	 * Removes the image from the list of registered images.
	 *
	 * @param img
	 */
	public void removeImage(CanvasImage img) {
		for (int i = 0; i < m_images.size(); i++)
			if (img == m_images.get(i))
				m_images.remove(i);
	}

	/**
	 * Returns the list of all registered images.
	 */
	public ArrayList getImages() {
		return m_images;
	}

	/**
	 * Returns the i-th element of the list of registered images.
	 */
	public CanvasImage getImage(int i) {
		return (CanvasImage)m_images.get(i);
	}

	/**
	 * Returns the number of images in the list of registered images.
	 */
	public int getImagesCount() {
		return m_images.size();
	}

	/**
	 * Returns the index of the specified CanvasImage in the list of registered
	 * images.
	 */
	public int getImageIndex(CanvasImage img) {
		for (int i = 0; i < m_images.size(); i++)
			if (m_images.get(i) == img)
				return i;
		return -1;
	}

	/**
	 * Needed to determine the suitable
	 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer
	 * Transformer} for this type of Canvas (e.g.
	 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer#ST_GRAPHICS2D}).
	 */
	public abstract int getScreenType();

	/**
	 * This method renders the content of the canvas by iterating through the
	 * {@link #m_objects CanvasObjects} and calling
	 * {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF#render()}for
	 * each. The rendering is mostly used for the generation of shapes, which are
	 * then drawn by {@link #drawScene}.
	 */
	public void renderScene() {
		for (int i = 0; i < getObjectCount(); i++) {
			getObject(i).render();
			if(m_restartRenderCycle) {
				i = 0;
				m_restartRenderCycle = false;
			}
		}
		if (getDrawingBoard().isVisible()
			&& getDrawingBoard().getSize().getWidth() > 0
			&& getDrawingBoard().getSize().getHeight() > 0)
			m_isSceneRendered = true;
	}

	public void renderFromWorldDraw() {
		for (int i = 0; i < getObjectCount(); i++)
			((CanvasObjectTransformer) ((MMCanvasObjectIF)getObject(i))
				.getCanvasTransformer())
				.renderFromWorld();
	}

	/**
	 * For performance reasons.
	 */
	public boolean isSceneRendered() {
		return m_isSceneRendered;
	}

	/**
	 * Draws the content of the canvas. This is done by iterating through the
	 * {@link #m_objects CanvasObject}s and calling
	 * {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF#draw()}for each.
	 */
	protected void drawScene() {
		for (int i = 0; i < m_images.size(); i++)
			 ((CanvasImage)m_images.get(i)).drawImage();
		for (int i = 0; i < getObjectCount(); i++) {
			//System.out.println("drawing "+getObject(i));
			getObject(i).draw();
		}
		int k = 1;
		for (int i = 0; i < m_canvasMessages.size(); i++) {
			CanvasMessage message = ((CanvasMessage)m_canvasMessages.get(i));
			if (message.getMessage() != null) {
				message.drawMessage(k);
				k++;
			}
		}
	}

	public void addGlobalHandler(GlobalHandler aGlobalHandler) {
		if (aGlobalHandler.getCanvas() != this)
			throw new IllegalArgumentException("canvas in global handler differs from this canvas");
		boolean addHandler = true;
		for (int i = 0; i < getGlobalHandlerCount(); i++) {
			if (getGlobalHandler(i).getClass().equals(aGlobalHandler.getClass()))
				addHandler = false;
			break;
		}
		if (addHandler)
			m_globalHandlers.add(aGlobalHandler);
	}

  /**
   * Removes the global handler of the type {@link Class} from the canvas's list of the used global handlers.
   */
  public void removeGlobalHandler(Class type) {
    for (int i = 0; i < m_globalHandlers.size(); i++) {
      if (getGlobalHandler(i).getClass() == type) {
        m_globalHandlers.remove(i);
      }
    }
  }

	public GlobalHandler[] getGlobalHandlers() {
		GlobalHandler[] globalHandlers = new GlobalHandler[getGlobalHandlerCount()];
		for (int i = 0; i < getGlobalHandlerCount(); i++)
			globalHandlers[i] = (GlobalHandler)m_globalHandlers.get(i);
		return globalHandlers;
	}

	public int getGlobalHandlerCount() {
		return m_globalHandlers.size();
	}

	public GlobalHandler getGlobalHandler(int index) {
		return (GlobalHandler)m_globalHandlers.get(index);
	}

//	public Dimension getPreferredSize() {
//		Dimension dim = super.getPreferredSize();
//		dim.setSize(dim.getWidth() * 10, dim.getHeight() * 10);
//		return dim;
//	}

	public void requestForFocus() {
		getDrawingBoard().requestFocus();
	}

	/**
	 * Per default the border is shown, when canvas is focused.
	 */
	public boolean showFocusBorder() {
		return m_showFocusBorder;
	}

	/**
	 * Determine, whether the border is shown when canvas is focused.
	 */
	public void setShowFocusBorder(boolean showFocusBorder) {
		m_showFocusBorder = showFocusBorder;
	}

	public void setShowMouseCoordinates(boolean show) {
	  m_showMouseCoords = show;
	}

	public boolean getShowMouseCoordinates() {
	  return m_showMouseCoords;
	}

	/**
	 * Returns the <code>CanvasControllerIF</code> which is the listener to
	 * various events occuring in the drawing board of this <code>MMCanvas</code>
	 * (see {@link #getDrawingBoard()}.
	 *
	 * @see net.mumie.mathletfactory.action.CanvasControllerIF @mm.sideeffects
	 *      the method returns a reference to an internal field of this <code>
	 * MMCanvas</code>.
	 */
	public final CanvasControllerIF getController() {
		return m_controller;
	}

	/**
	 * Resets <code>MMCanvas</code> and its unique
	 * {@link net.mumie.mathletfactory.action.CanvasControllerIF} to their initial
	 * state. <br>Observe especially that all registered
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s will be deselected.
	 *
	 */
	public void reset() {
		getController().finishAction();
	}

	/**
	 * Returns the <code>Component</code> on which the java engine really will
	 * paint the registered (see
	 * {@link #addObject(net.mumie.mathletfactory.mmobject.MMCanvasObjectIF)}
	 * <code>MMObjectIF</code>s.
	 */
	public abstract Component getDrawingBoard();

	public final int CANVAS_UPPER_RIGHT_QUARTER = 1;
	public final int CANVAS_UPPER_LEFT_QUARTER = 2;
	public final int CANVAS_LOWER_LEFT_QUARTER = 3;
	public final int CANVAS_LOWER_RIGHT_QUARTER = 4;

	/**
	 * Returns the constant for a canvas quarter for the specified point.
	 */
	public int isInCanvasQuarter(double xInPixel, double yInPixel) {
		double xHalf = ((double)getWidth()) / 2.;
		double yHalf = ((double)getHeight()) / 2.;
		if (xInPixel >= xHalf && yInPixel <= yHalf)
			return CANVAS_UPPER_RIGHT_QUARTER;
		else if (xInPixel < xHalf && yInPixel <= yHalf)
			return CANVAS_UPPER_LEFT_QUARTER;
		else if (xInPixel < xHalf && yInPixel > yHalf)
			return CANVAS_LOWER_LEFT_QUARTER;
		else
			return CANVAS_LOWER_RIGHT_QUARTER;
	}

	/**
	 * Starts the frame rate counter.
	 *
	 */
	protected void startFrameRateCounter() {
		m_before = System.currentTimeMillis();
	}

	/**
	 * Stops the frame rate counter and calculates its value.
	 *
	 */
	protected void stopFrameRateCounter() {
		// Measure the frame rate
		long now = System.currentTimeMillis();
		double newRate = 1000.0 / (double) (now - m_before);

		m_fpsTimeCounter += (double) (now - m_before);
		m_fpsImageCounter++;
		if (m_fpsTimeCounter >= 100 && m_fpsTimeCounter <= 150) {
			m_FrameRate = (double)1000 / m_fpsTimeCounter * m_fpsImageCounter;
			m_fpsImageCounter = 0;
			m_fpsTimeCounter = 0;
		}
		if (m_fpsTimeCounter > 150) {
			m_fpsImageCounter = 0;
			m_fpsTimeCounter = 0;
		}

	}

	public double getFrameRate() {
		return m_FrameRate;
	}

	public void enableCalculateFPS(boolean aBoolean) {
		m_calculateFPS = aBoolean;
	}

	public boolean isCalculateFPSEnabled() {
		return m_calculateFPS;
	}

	/**
	 * Returns the drawing board's bounds rectangle minus insets in pixel coordinates.
	 */
	public Rectangle getClientRect() {
		return getClientRect(new Rectangle());
	}

	public Rectangle getClientRect(Rectangle rect) {
		Component drawingBoard = getDrawingBoard();
		if (!(drawingBoard instanceof JComponent)) {
      rect.setBounds(drawingBoard.getBounds());
      return rect;
		}
    rect.setBounds(((JComponent)drawingBoard).getVisibleRect());
		Insets insets = ((JComponent)getDrawingBoard()).getInsets();
		rect.x += insets.left;
		rect.y += insets.top;
		rect.width -= insets.left + insets.right;
		rect.height -= insets.top + insets.bottom;
		return rect;
	}

	public void componentHidden(ComponentEvent e) {
		if (logger.isLoggable(java.util.logging.Level.FINE))
			logger.fine(getClass().getName() + "component hidden");
	}

	public void componentMoved(ComponentEvent e) {
		if (logger.isLoggable(java.util.logging.Level.FINE))
			logger.fine(getClass().getName() + "component moved");
	}

	public void componentShown(ComponentEvent e) {
		if (logger.isLoggable(java.util.logging.Level.FINE))
			logger.fine(getClass().getName() + "component shown");
	}

	public void focusGained(FocusEvent e) {
		if (!e.isTemporary() && showFocusBorder()) {
			drawFocus(true);
			repaint();
		}
	}

	public void focusLost(FocusEvent e) {
		boolean checkOpposite1 = true, checkOpposite2 = true;
		if (e.getOppositeComponent() != null)
			checkOpposite1 = !e.getOppositeComponent().equals(getDrawingBoard());

		if (!e.isTemporary() && checkOpposite1 && showFocusBorder())
			drawFocus(false);

		if (e.getOppositeComponent() != null)
			checkOpposite2 =
				!(e.getOppositeComponent().equals(this)
					|| e.getOppositeComponent().equals(getDrawingBoard()));

		if (!e.isTemporary() && checkOpposite2) {
			getController().setSelectedObject(null);
		}

		if (!e.isTemporary()) {
			getController().finishAction();
		}
	}

	protected void drawFocus(boolean focus) {
		if (getDrawingBoard() instanceof DrawingBoardIF)
			 ((DrawingBoardIF)getDrawingBoard()).drawFocus(focus);
	}

	/**
	 * Overrides the method inherited from {@link java.awt.Component}due to the
	 * fact, that the drawing board of this <code>MMCanvas</code> (see
	 * {@link #getDrawingBoard()}) should have the background color.
	 *
	 * @see java.awt.Component#setBackground(java.awt.Color)
	 */
	public void setBackground(Color c) {
		super.setBackground(c);
		if (getDrawingBoard() != null)
			getDrawingBoard().setBackground(c);
	}

	/** By default all special cases are displayed in a popup window. */
	public void displaySpecialCase(SpecialCaseEvent e) {
		m_specialCaseCanvasMessage.setMessage(e.toString());
	}

	/**
	 * Adds a snapshot of the world2screen view to the history
	 */
	public abstract void addSnapShotToHistory();

	protected class ExtendedButtonModel
		extends DefaultButtonModel
		implements ActionListener {
		private Timer pressedTimer = new Timer(0, this);
		private int counter, currentIncrement;
		public void setPressed(boolean pressed) {
			super.setPressed(pressed);
			if (pressed) {
				counter = 0;
				currentIncrement = 1;
				pressedTimer.start();
			} else {
				pressedTimer.stop();
				addSnapShotToHistory();
			}
		}

		/** Does not work yet. */
		public int getIncrement() {
			//System.out.println("increment is "+(int) Math.pow(2,
			// currentIncrement));
			return (int)Math.pow(2, currentIncrement);
		}

		public void actionPerformed(ActionEvent e) {
			if (isArmed()) {
				counter++;
				if ((counter == getIncrement()) && (currentIncrement < 5)) {
					counter = 0;
					currentIncrement++;
				}
				fireActionPerformed(
					new ActionEvent(
						this,
						ActionEvent.ACTION_PERFORMED,
						getActionCommand(),
						EventQueue.getMostRecentEventTime(),
						0));
			}
		}
	}

	/** Checks and updates the inverse for each transformation. */
	public abstract void adjustTransformations();

	protected void updateCanvas() {
		adjustTransformations();
		renderFromWorldDraw();
		repaint();
	}

	protected JButton createToolbarButton(Action action, boolean enabled) {
		JButton button = new PlainButton(action);
		m_toolBar.add(button);
		button.setFocusable(false);
		button.setEnabled(enabled);
		button.setMargin(new Insets(0, 0, 0, 0));
		return button;
	}

	protected JButton createScrollButton(
		Action action,
		Insets insets,
		boolean scrollButtonsVisible) {
		JButton button = new PlainButton(action);
		button.setModel(new ExtendedButtonModel());
		button.setFocusable(false);
		button.setMargin(insets);
		button.setVisible(scrollButtonsVisible);
		return button;
	}
	/**
	 * Returns whether the scoll buttons are currently visible.
	 */
	public boolean isScrollButtonsVisible() {
		return m_btnScrollDown.isVisible();
	}

	/**
	 * Returns whether the toolbar is currently visible.
	 */
	public boolean isToolbarVisible() {
		return m_toolBar.isVisible();
	}

	/**
	 * Sets the visibility of the scroll buttons to b.
	 */
	public void setScrollButtonsVisible(boolean b) {
		if (b)
			m_toolFlags |= SCROLL_BUTTONS;
		else
			m_toolFlags &= ~SCROLL_BUTTONS;
		if (m_btnScrollDown == null) // has not been initialized
			return;
		m_btnScrollDown.setVisible(b);
		m_btnScrollUp.setVisible(b);
		m_btnScrollLeft.setVisible(b);
		m_btnScrollRight.setVisible(b);
	}

	/**
	 * Sets the visibility of the tollbar to b.
	 */
	public void setToolbarVisible(boolean b) {
		m_toolBar.setVisible(b);
    revalidate();
	}

  /**
   * Adds a canvas message that will be drawn onto the canvas.
   */
	public void addMessage(CanvasMessage message) {
		m_canvasMessages.add(message);
	}

  /**
   * Removes a former added canvas message.
   */
	public void removeMessage(CanvasMessage message) {
		m_canvasMessages.remove(message);
	}
	
	/**
	 * Registers a canvas class for a named screen type.
	 * 
	 * @see #createCanvas(String)
	 */
	public static void registerCanvas(String screenTypeName, int dimension, Class canvasClass) {
		Class oldClass = getCanvasClass(screenTypeName);
		if(oldClass != null && !oldClass.equals(canvasClass))
			throw new IllegalArgumentException("Canvas class was already registered for screen type \"" + screenTypeName + "\": " + canvasClass.getName());
		if(oldClass == null)
			m_canvasClasses.put(screenTypeName, canvasClass);
		if(dimension == DIMENSION_2D && MM2DCanvas.getDefault2DScreenType() == null)
			MM2DCanvas.setDefault2DScreenType(screenTypeName);
		if(dimension == DIMENSION_3D && MM3DCanvas.getDefault3DScreenType() == null)
			MM3DCanvas.setDefault3DScreenType(screenTypeName);
	}
	
	/**
	 * Returns the internally stored canvas class for the named screen type or <code>null</code>.
	 */
	private static Class getCanvasClass(String screenTypeName) {
		return (Class) m_canvasClasses.get(screenTypeName);
	}
	
	/**
	 * Creates and returns the appropriate {@link MMCanvas canvas} for the named screen type.
	 * 
	 * @param screenTypeName the name of the screen type
	 * @return an instance of {@link MMCanvas}
	 */
	public static MMCanvas createCanvas(String screenTypeName) {
		try {
			Class canvasClass = getCanvasClass(screenTypeName);
			if(canvasClass == null)
				throw new NullPointerException("No canvas class was registered for screen type \"" + screenTypeName + "\" !");
			return (MMCanvas) canvasClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("An exception occurred while creating canvas class: " + e.toString(), e);
		}
	}
}
