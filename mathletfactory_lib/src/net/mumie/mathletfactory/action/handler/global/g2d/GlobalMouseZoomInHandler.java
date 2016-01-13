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

package net.mumie.mathletfactory.action.handler.global.g2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.global.Global2DHandler;
import net.mumie.mathletfactory.display.MM2DCanvas;

/**
 * This handler allows the user to zoom in and out of the canvas by pressing shift and 'dragging'
 * a rectangle open with the mouse.
 *  
 * @author Paehler
 * @mm.docstatus finished
 *
 */
public class GlobalMouseZoomInHandler extends Global2DHandler {
  private Point clickPos = null, currentPos = null;
  private Rectangle scaleRect = null;

  /** Constructs the handler to work in the given canvas. */
  public GlobalMouseZoomInHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
  }

  protected boolean userDefinedDealsWith(MMEvent event) {
    boolean keyTest = (event.getKeyCode() == KeyEvent.VK_Z &&
                      event.getModifier() == MMEvent.NO_MODIFIER_SET)
                      ||
                      (event.getKeyCode() == MMEvent.NO_KEY &&
                       event.getModifier() == MouseEvent.SHIFT_MASK);
    if(  event.getEventType() == MouseEvent.MOUSE_DRAGGED &&
         event.getMouseButton() == MouseEvent.BUTTON1_MASK &&
         event.getClickCount() == 0 &&
         keyTest)
      return true;
    return false;
  }

	/**
	 * Calculates the rectangle to be taken as new world viewport.
	 */
  protected Rectangle calcRectFromPositions() {
    scaleRect = new Rectangle(clickPos.x, clickPos.y, 0, 0);
    
    int distX = currentPos.x - clickPos.x; 
    int distY = currentPos.y - clickPos.y;
    int canvasWidth = getCanvas().getDrawingBoard().getWidth();
    int canvasHeight = getCanvas().getDrawingBoard().getHeight();
    double scaleX = (double)distX / (double)canvasWidth;
    double scaleY = (double)distY / (double)canvasHeight;

    double scale = Math.abs(Math.abs(scaleX) > Math.abs(scaleY) ? scaleX : scaleY);
    
    
    scaleRect.width = (int)(scale * canvasWidth);
    scaleRect.height = (int)(scale * canvasHeight);
    if (scaleX < 0) {
      scaleRect.x = clickPos.x - scaleRect.width;
    }
    if (scaleY < 0) {
      scaleRect.y = clickPos.y - scaleRect.height;
    }
    return scaleRect; 
  }
  
  /** Draws the rectangle chosen by the user. */
  protected void updateRect() {
    Rectangle oldRect = scaleRect;
  
    calcRectFromPositions();
  
    Graphics gr = getCanvas2D().getDrawingBoard().getGraphics();
    
    gr.setXORMode(Color.gray);
    if (oldRect != null) {
      gr.drawRect(oldRect.x, oldRect.y, oldRect.width, oldRect.height);
    }
    gr.drawRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);
  }
  
  public boolean doAction(MMEvent event) {
    currentPos = new Point(event.getX(), event.getY());
    if (clickPos == null) {
      clickPos = currentPos;
    }
    updateRect();
    return true;
  }

  public void finish() {
    Graphics gr = getCanvas2D().getDrawingBoard().getGraphics();
    gr.setXORMode(Color.gray);
    gr.drawRect(scaleRect.x, scaleRect.y, scaleRect.width, scaleRect.height);
    
    if (scaleRect.getWidth() != 0) {
      double scale = getCanvas().getDrawingBoard().getWidth() / scaleRect.getWidth();
        
      getCanvas2D().getWorld2Screen().leftTranslateX(-scaleRect.x);
      getCanvas2D().getWorld2Screen().leftTranslateY(-scaleRect.y);
      getCanvas2D().getWorld2Screen().leftScale(scale);
      getCanvas2D().adjustTransformations();
      m_canvas.renderFromWorldDraw();
      m_canvas.repaint();
      getCanvas2D().getWorld2Screen().addSnapshotToHistory();
    }

    scaleRect = null;
    clickPos = null;
  }

}
