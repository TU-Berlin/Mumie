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

package net.mumie.mathletfactory.display.g2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.action.CanvasControllerIF;
import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;



/**
 *
 * This class serves as base for all subclasses of
 * {@link net.mumie.mathletfactory.display.CanvasDrawable} that
 * use the Java 2D-API.
 *
 * It handles all the drawing stuff (elements, shadow and selection). Subclasses
 * have to supply a {@link java.awt.Shape} to use this features.
 *
 * @author amsel
 * @mm.docstatus finished
 */

public abstract class G2DDrawable extends CanvasDrawable {
  private static final Stroke[] SELECTED_STROKES = new Stroke[] {
      new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[] {3, 3}, 4),
      new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[] {3, 3}, 2),
      new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[] {3, 3}, 0)
  };

  private Object m_rendererLock = new Object();
  private int m_currentSelection = 0;

  private long m_lastSelectionTime = System.currentTimeMillis();

  /**
   *  Determines, by how many pixels you may miss the object when picking it
   *  with a mouse.
   */
  public static final int PICKING_TOLERANCE = 2;

  /**
   *  Constant for signaling, that pixels are written with 50% transparency.
   */
  protected static final AlphaComposite ALPHA_COMPOSITE_50 =
    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

  /**
   *  Constant for signaling, that pixels are written with 25% transparency.
   */
  protected static final AlphaComposite ALPHA_COMPOSITE_25 =
    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f);


  /**
   *  The Java 2D-{@link java.awt.Shape}, that acts as graphical representation
   *  of the corresponding mathematical {@link net.mumie.mathletfactory.mmobject.MMObjectIF
   *  MMObject}.
   */
  protected abstract Shape getShape();

  /**
   * Determines wheater the object should be displayed as filled or not. By
   * default this method return the filled flag from the properties given as
   * param. I.e. subclasses may override this methode, when the fill state
   * depends on the shapes properties.
   * @param properties - DisplayProperties associated with this drawable.
   * @return boolean
   */
  protected boolean isFilled(DisplayProperties properties) {
    return properties.isFilled();
  }

  /**
   *  Needed to paint color-gradient fills
   */
  protected Rectangle2D getGradientBounds() {
    return getShape().getBounds2D();
  }

  private void upcountCurrentSelectionStroke() {
    synchronized(m_rendererLock) {
      m_currentSelection = (m_currentSelection + 1) % 3;
    }
  }

  /**
   * This method may be used to prepare the canvas for rendering, i.e. setting
   * fonts or colors.
   * @param gr - the {@link Graphics2D} object on which the drawable will be
   * rendered.
   * @param properties - the properties associated with the drawable
   */
  protected void prepareCanvas(Graphics2D gr, DisplayProperties properties) {
  }

  /**
   * Draws the shape of the Drawable onto the canvas. As in this class
   * destination is assumed to be a {@link MMG2DCanvas}.
   * @param gr - the {@link Graphics2D} object on which the drawable will be
   * rendered.
   * @param properties - the properties associated with the drawable
   */
  protected void drawObject(MMCanvas destination, DisplayProperties properties) {
    MMG2DCanvas canvas = (MMG2DCanvas)destination;
    Graphics2D gr = canvas.getGraphics2D();
    if(gr == null || !isVisible()) // component not visible?
      return;
    Paint oldPaint = gr.getPaint();
    Stroke oldStroke = gr.getStroke();

    if (properties.hasShadow()) {
      Point2D offset = properties.getShadowOffset();
      AffineTransform trafo = AffineTransform.getTranslateInstance(offset.getX(), offset.getY());
      gr.transform(trafo);
      gr.setPaint(properties.getShadowColor());
      Composite oldComposite = gr.getComposite();
      gr.setComposite(ALPHA_COMPOSITE_50);
      if (isFilled(properties))
        gr.fill(getShape());
      else
        gr.draw(getShape());
      try {
        gr.transform(trafo.createInverse());
      } catch (NoninvertibleTransformException e) {
        throw new Error("translation is not invertible!" + e.getMessage());
      }
      gr.setComposite(oldComposite);
      gr.setPaint(properties.createGradientPaint(getGradientBounds(), true));
    } else {
      gr.setPaint(properties.getObjectColor());
    }

    Composite oldComposite = gr.getComposite();
    if (properties.getTransparency() > 0) {
      gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)properties.getTransparency()));
    }
    if (isFilled(properties)) {
      gr.fill(getShape());
    }
    if(properties.getBorderWidth() != 0){
      gr.setPaint(properties.getBorderColor());
      float[] dashPattern = properties.getDashPattern();
      gr.setStroke(new BasicStroke(properties.getBorderWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dashPattern, 0));
      Shape shape = getShape();
      if(shape != null)
        gr.draw(getShape());
      else
        System.out.println("null shape: "+this);
    }
    gr.setComposite(oldComposite);
    gr.setPaint(oldPaint);
    gr.setStroke(oldStroke);
  }

  /**
   * Draw the selection of the drawable. By default a dotted line is drawn
   * arround the shape given by {@link getShape()}. The dotted line is animated
   * as one knows from computer graphic programs.
   * @param gr - the {@link Graphics2D} object on which the drawable will be
   * rendered.
   * @param properties - the properties associated with the drawable
   */
  protected void drawSelection(Object destination, DisplayProperties properties) {
    MMG2DCanvas canvas = (MMG2DCanvas)destination;
    Graphics2D gr = canvas.getGraphics2D();

    if(System.currentTimeMillis() - m_lastSelectionTime >= CanvasControllerIF.SELECTION_RENDER_UPDATE_PAUSE) {
      upcountCurrentSelectionStroke();
      m_lastSelectionTime = System.currentTimeMillis();
    }
    Stroke stroke = SELECTED_STROKES[m_currentSelection];
    if (stroke != null) {
      gr.setPaint(canvas.getBackground());
/*      if (properties.isVisible())
        gr.setPaint(canvas.getBackground());
      else
        gr.setPaint(canvas.getForeground());*/
      Stroke oldStroke = gr.getStroke();
      gr.setStroke(stroke);
      gr.draw(getShape());
      gr.setStroke(oldStroke);
    }
  }
  /**
   *  In this method the actual drawing on the canvas is performed. It
   *  considers not only the shape of the corresponding
   *  {@link m_master master} MMObject, but also evaluates its
   *  {@link DisplayProperties} and creates shadows, etc. accordingly.
   */
  public void draw(MMCanvas canvas, DisplayProperties properties) {
    // here we expect only MMG2DCanvas:
    MMG2DCanvas canvas2D = (MMG2DCanvas)canvas;
    Graphics2D gr = canvas2D.getGraphics2D();
    prepareCanvas(gr, properties);
    super.draw(canvas, properties);
  }
}
