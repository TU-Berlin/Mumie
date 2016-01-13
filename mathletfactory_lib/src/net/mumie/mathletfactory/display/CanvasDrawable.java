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

import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.property.CombinedPropertyMap;

/**
 * This class is responsible for rendering and drawing the mathematical object
 * to the screen. This is done by the methods {@link #render} and {@link #draw(MMCanvasObjectIF)},
 * which are called via the corresponding methods of
 * {@link net.mumie.mathletfactory.transformer.CanvasObjectTransformer} by
 * its {net.mumie.mathletfactory.mmobject.MMObjectIF master}-object.<br>
 *
 * The CanvasDrawable and its subclasses use screen-coordinates exclusively
 * and therefore uses java data types like <code>double</code> and
 * <code>java.awt.Point2D</code> (which are calculated in the
 * <code>Transformer</code>.
 *
 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public abstract class CanvasDrawable implements Drawable {

  private boolean m_isVisible=true;
  
  /** Map holding all (display) properties for this drawable. */
	protected CombinedPropertyMap m_properties = new CombinedPropertyMap();

  /**
   *  In this method a subclass specifies, how the master has
   *  to be laid out on the screen.
   */
	public abstract void render(DisplayProperties properties);

  /** Implementation of the actual drawing of the object. */
  protected abstract void drawObject(MMCanvas canvas, DisplayProperties properties);

    /** Implementation of the actual drawing of the selection for the object, if any. */
  protected abstract void drawSelection(Object destination, DisplayProperties properties);

  /**
   * In this method the actual drawing on the canvas is performed given the
   * canvas and the properties for display. The Drawable should render a
   * visible unselected object.
   */
  public void draw(MMCanvas canvas, DisplayProperties properties) {
    drawObject(canvas, properties);
  }

  /**
   * just like draw(Object, DisplayProperties). The object should be asked for
   * it selection state and visibility and should be draw accordingly.
   */
  public void draw(MMCanvasObjectIF object) {
    if (object.isVisible()) {
      draw(object.getCanvas(), object.getDisplayProperties());
    }
    if (object.isSelected()) {
      drawSelection(object.getCanvas(), object.getDisplayProperties());
    }
  }

  /** Returns true, if this drawable has been "picked" by the given screen coordinates. */
  public abstract boolean isAtScreenLocation(int xOnScreen, int yOnScreen);

  /**
   * Returns true if this drawable should be drawn, false otherwise.
   */
  public boolean isVisible(){
    return m_isVisible;
  }

  /**
   * Set to true if this drawable should be drawn, false otherwise.
   */
  public void setVisible(boolean b){
    m_isVisible = b;
  }
  
  public PropertyMapIF getDrawableProperties() {
  	return m_properties;
  }
}

