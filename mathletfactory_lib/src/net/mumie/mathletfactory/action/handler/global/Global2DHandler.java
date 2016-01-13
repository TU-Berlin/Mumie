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

import net.mumie.mathletfactory.display.MM2DCanvas;

/**
 * The base class for all 2D global handlers  - i.e. handlers that are associated to a
 * {@link net.mumie.mathletfactory.display.MM2DCanvas}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public abstract class Global2DHandler extends GlobalHandler {
  
  /** Constructs the handler to work in the given canvas. */
  public Global2DHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
  }
  
  /** Returns the canvas, this handler is working in. */
  public final MM2DCanvas getCanvas2D() {
    return (MM2DCanvas)m_canvas;
  }
  
}

