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

package net.mumie.mathletfactory.action.handler.global;

import net.mumie.mathletfactory.display.MM3DCanvas;

/**
 * The base class for all 3D global handlers - i.e. handlers  that are associated to a
 * {@link net.mumie.mathletfactory.display.MM3DCanvas}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public abstract class Global3DHandler extends GlobalHandler {
  
  /** Constructs the handler to work in the given canvas. */
  public Global3DHandler(MM3DCanvas aCanvas) {
    super(aCanvas);
  }
  
  /** Returns the canvas, this handler is associated to. */
  public final MM3DCanvas getCanvas3D() {
    return (MM3DCanvas)m_canvas;
  }
 
  /**
   * This method will be called by the Controller of the underlying canvas at
   * the end of its controlAction() method:
   * A subclass overriding this method should reset internal variables and call  
   * a final draw of the canvas if drawing in {@link #doAction} disabled.
   */
  public void finish(){
    ((MM3DCanvas)m_canvas).globalHandlerFinished();
    m_canvas.repaint();
  }
}

