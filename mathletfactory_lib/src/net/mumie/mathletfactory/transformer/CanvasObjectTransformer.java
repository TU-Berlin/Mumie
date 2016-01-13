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

package net.mumie.mathletfactory.transformer;

import java.util.HashSet;
import java.util.Set;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.Drawable;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * This is the base class for all transformers that render and draw objects on a canvas.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public abstract class CanvasObjectTransformer extends GeneralTransformer {

  /**
   * this array holds all possible {@link net.mumie.mathletfactory.display.CanvasDrawable}s
   * necessary to visualize the mathematics this
   */
  protected CanvasDrawable[] m_allDrawables;

  /**
   * this array holds additional <code>CanvasDrawables</code> that might be
   * required by the &quot;real&quot; mathematics. Here we think of extra
   * presentation of boundary values for functions defined on borel sets
   * (these might be displayed as point objects, whereas the actual function
   * graph is displayed as a polygon) etc.
   */
  protected CanvasDrawable[] m_additionalDrawables;

  /** If a drawable is contained in this set, it neither rendered, nor drawn. */
  protected Set m_invisibleDrawables = new HashSet();

  /**
   * this will be the instance of the current active (valid)
   * <code>CanvasDrawable</code> and always points to one of the drawables
   * stored in {@link #m_allDrawables}.
   */
  protected CanvasDrawable m_activeDrawable;

  protected DisplayProperties[] m_additionalProperties;

  /**
   * This method overrides the implementation of
   * {@link net.mumie.mathletfactory. transformer.GeneralTransformer#initialize()} due to the
   * fact, that an instance of <code>CanvasObjectTransformer</code> can only
   * work for instances of {@link net.mumie.mathletfactory.transformer.MMCanvasObjectIF}.
   *
   * @see net.mumie.mathletfactory.transformer.GeneralTransformer#initialize(MMObjectIF)
   */
  public void initialize(MMObjectIF masterObject) {
  	initializeDrawables(m_allDrawables);
  	initializeDrawables(m_additionalDrawables);
    if (masterObject instanceof MMCanvasObjectIF)
      super.initialize(masterObject);
    else
      throw new IllegalArgumentException(
        "CanvasObjectTransformer::init(): can only work for instances of MMCanvasObjectIF, but I got "
          + masterObject.getClass().getName());
  }

  /**
   * This method forces all related drawables, i.e the active drawable and all
   * additional drawables, for the mathematical object to be drawn on the
   * desired canvas. Observe, that these drawables are based on their data
   * derived from the last {@link #render()} call.
   * @see net.mumie.mathletfactory.transformer.GeneralTransformer#draw()
   */
  public void draw() {
    if(!isCanvasVisible()){
      return;
    }
    int i;
    if (m_activeDrawable != null && !m_invisibleDrawables.contains(m_activeDrawable)) {
      m_activeDrawable.draw((MMCanvasObjectIF) m_masterMMObject);
    }
    if (m_additionalDrawables != null)
      for (i = 0; i < m_additionalDrawables.length; i++)
        if (m_additionalDrawables[i] != null && !m_invisibleDrawables.contains(m_additionalDrawables[i]))
        m_additionalDrawables[i].draw(
          ((MMCanvasObjectIF) m_masterMMObject)
            .getAsCanvasContent()
            .getCanvas(),
          m_additionalProperties[i]);
  }

  /**
   * Implements method
   * {@link net.mumie.mathletfactory.transformer.GeneralTransformer#render()}.
   * Here, first the method
   * {@link #synchronizeMath2Screen()} is called in order to synchronize the
   * mathematical object with its visualisation. Then the active drawable for
   * this object (see {@link #getActiveDrawable}) and all additional drawables
   * due to this representation of the object are rendered, that is, they are
   * filled with the appropriate screen coordinates due to the current
   * mathematics.
   *
   * @see net.mumie.mathletfactory.transformer.GeneralTransformer#render()
   */
  public void render() {
    if(!isCanvasVisible()){
      return;
    }
    int i;
    synchronizeMath2Screen();
    if (m_activeDrawable != null && !m_invisibleDrawables.contains(m_activeDrawable)) {
    	m_activeDrawable.getDrawableProperties().copyPropertiesFrom(m_masterMMObject.getDisplayProperties());
      m_activeDrawable.render(m_masterMMObject.getDisplayProperties());
    }
    if (m_additionalDrawables != null)
      for (i = 0; i < m_additionalDrawables.length; i++)
        if (m_additionalDrawables[i] != null && !m_invisibleDrawables.contains(m_additionalDrawables[i])) {
        	m_additionalDrawables[i].getDrawableProperties().copyPropertiesFrom(m_additionalProperties[i]);
          m_additionalDrawables[i].render(m_additionalProperties[i]);
        }

  }

  private boolean isCanvasVisible() {
    return getCanvas().getDrawingBoard().isVisible() && getCanvas().getDrawingBoard().getSize().getWidth() > 0 && getCanvas().getDrawingBoard().getSize().getHeight() > 0;
  }

  /**
   * Here, first the method {@link #synchronizeWorld2Screen()} is called in
   * order to synchronize the mathematical object with its visualisation,
   * expecting that the real mathematics has not changed but only its
   * representation in the canvas. Then the active drawable for this object
   * (see {@link #getActiveDrawable}) and all additional drawables due to this
   * representation of the object are rendered.
   *
   */
  public void renderFromWorld() {
    int i;
    synchronizeWorld2Screen();
    if (m_activeDrawable != null && !m_invisibleDrawables.contains(m_activeDrawable)) {
    	m_activeDrawable.getDrawableProperties().copyPropertiesFrom(m_masterMMObject.getDisplayProperties());
      m_activeDrawable.render(m_masterMMObject.getDisplayProperties());
    }
    if (m_additionalDrawables != null){
      for (i = 0; i < m_additionalDrawables.length; i++)
        if (m_additionalDrawables[i] != null && !m_invisibleDrawables.contains(m_additionalDrawables[i])) {
        	m_additionalDrawables[i].getDrawableProperties().copyPropertiesFrom(m_additionalProperties[i]);
          m_additionalDrawables[i].render(m_additionalProperties[i]);
        }
    }
  }

  /**
   * Simply calls {@link #getActiveCanvasDrawable()}.
   *
   * @see net.mumie.mathletfactory.transformer.GeneralTransformer#getActiveDrawable()
   */
  public Drawable getActiveDrawable() {
    return getActiveCanvasDrawable();
  }

  /**
   * Each {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF} may be
   * visualised by different {@link net.mumie.mathletfactory.display.
   * CanvasDrawable}s. For example think of a line segment, that will be
   * displayed by a &quot;line like&quot; object in general, but may be
   * displayed by a &quot;point like&quot; object, when it is degenerated. This
   * method returns the current (valid) <code>CanvasDrawable</code> due to the
   * actual mathematical data.
   *
   * @return CanvasDrawable
   */
  public CanvasDrawable getActiveCanvasDrawable() {
    return m_activeDrawable;
  }

  /**
   * This method returns whether the drawable of a mathematical object in a
   * {@link net.mumie.mathletfactory.display.MMCanvas} (which is the
   * {@link net.mumie.mathletfactory.display.CanvasDrawable} returned by the method
   * {@link #getActiveDrawable}) is located at the position <code>xOnScreen</code>,
   * <code>yOnScreen</code>, where these coordinates are interpreted as screen
   * coordinates.
   *
   * @param xOnScreen
   * @param yOnScreen
   * @return boolean
   */
  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    if (m_activeDrawable != null && m_activeDrawable.isVisible()) {
      return m_activeDrawable.isAtScreenLocation(xOnScreen, yOnScreen);
    }
    return false;
  }

  /**
   *
   */
  public abstract void synchronizeMath2Screen();

  public abstract void synchronizeWorld2Screen();

  /**
   *  Returns the mathematical object from the given screen coordinates. For
   *  some actions it is necessary change the mathematical coordinates of
   *  an object by transforming screen coordinates back into math space. If
   *  for example an affine point should be translated, the mouse movement is
   *  recorded in screen coordinates, then the corresponding mathematical
   *  coordinates for the point are calculated by using this method.
   */
  public abstract void getMathObjectFromScreen(
    double[] javaScreenCoordinates,
    NumberTypeDependentIF mathObject);

  /**
   *  Returns the sceen coordinates from a given mathematical entity by using
   *  the transformations.
   */
  public abstract void getScreenPointFromMath(
    NumberTypeDependentIF entity,
    double[] javaScreenCoordinates);

  /**
   * This method will return the transformers master object (see
   * {@link net.mumie.mathletfactory.transformer.GeneralTransformer#m_masterMMObject}
   * and {@link net.mumie.mathletfactory.transformer.GeneralTransformer#getMaster()}
   * casted to {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF}.
   *
   * @return   a MMCanvasObjectIF
   *
   */
  public MMCanvasObjectIF getMasterAsCanvasObject() {
    return (MMCanvasObjectIF) getMaster();
  }

  /**
   * returns the instance of {@link net.mumie.mathletfactory.display.MMCanvas}
   * in which the master object will be visualized.
   *
   * @return   a MMCanvas
   *
   */
  public MMCanvas getCanvas() {
    return getMasterAsCanvasObject().getCanvas();
  }
}
