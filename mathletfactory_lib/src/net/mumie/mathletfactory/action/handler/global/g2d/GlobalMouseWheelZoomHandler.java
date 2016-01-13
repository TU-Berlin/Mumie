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

import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.global.Global2DHandler;
import net.mumie.mathletfactory.display.MM2DCanvas;

/**
 * This handler allows the user to zoom in and out of the canvas by scrolling
 * with the mouse wheel.
 *  
 * @author Gronau
 * @mm.docstatus finished
 *
 */
public class GlobalMouseWheelZoomHandler extends Global2DHandler {
  
  private final static double SCLALE_FACTOR = 0.1; 

  /** Constructs the handler to work in the given canvas. */
  public GlobalMouseWheelZoomHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
  }

  protected boolean userDefinedDealsWith(MMEvent event) {
    if(event.getEventType() == MouseEvent.MOUSE_WHEEL)
      return true;
    return false;
  }

  public boolean doAction(MMEvent event) {
      if(event.getMouseWheelRotation() < 0)
        getCanvas2D().getWorld2Screen().rightScale(1 + SCLALE_FACTOR);
      else
        getCanvas2D().getWorld2Screen().rightScale(1 - SCLALE_FACTOR);
      getCanvas2D().adjustTransformations();
      m_canvas.renderFromWorldDraw();
      m_canvas.repaint();
      getCanvas2D().getWorld2Screen().addSnapshotToHistory();
      return false;
  }

  public void finish() {}
}
