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

package net.mumie.mathletfactory.action.handler;

import javax.swing.JComponent;

import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;

/**
 * This class offers the basic functionality for 2d translation with the mouse.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public abstract class Abstract2DTranslateHandler extends MMHandler {

  /** A flag indicating that translation should occur exclusively in x dimension. */ 
  private boolean inXOnly = false;

  /** A flag indicating that translation should occur exclusively in y dimension. */
  private boolean inYOnly = false;

  /** A flag indicating that translation should occur exclusively on a graph defined by a function. */
  private boolean yDefinedByFunction = false, xDefinedByFunction = false;

  /** The functions defining the graph on which the object should be moved. */
  protected FunctionOverRIF m_xFunction, m_yFunction;

  public Abstract2DTranslateHandler(JComponent display) {
    super(display);
  }

  /** Sets whether translation should occur exclusively in x dimension. */
  public void setinXOnly(boolean aValue) {
    inXOnly = aValue;
  }

  /** Returns whether translation should occur exclusively in x dimension. */
  public boolean isInXOnly() {
    return inXOnly;
  }

  /** Sets whether translation should occur exclusively in y dimension. */
  public void setinYOnly(boolean aValue) {
    inYOnly = aValue;
  }
  
  /** Returns whether translation should occur exclusively in y dimension. */
  public boolean isInYOnly() {
    return inYOnly;
  }

  /** Sets whether translation should occur exclusively on a graph defined by a function. */
  public void setYDefinedByFunction(boolean aValue, FunctionOverRIF function) {
    yDefinedByFunction = aValue;
    m_yFunction = function;
  }

  /** Returns whether translation should occur exclusively on a graph defined by a function. */
  public boolean isYDefinedByFunction() {
    return yDefinedByFunction;
  }

  /** Sets whether translation should occur exclusively on a graph defined by a function. */
  public void setXDefinedByFunction(boolean aValue, FunctionOverRIF function) {
    xDefinedByFunction = aValue;
    m_xFunction = function;
  }

  /** Returns whether translation should occur exclusively on a graph defined by a function. */
  public boolean isXDefinedByFunction() {
    return xDefinedByFunction;
  }

  /**
  * To avoid casting in the code: This handler can only work for instances
  * of MMCanvasObjectIF.
  */
  protected MMCanvasObjectIF getRealMasterType() {
    return (MMCanvasObjectIF) m_master;
  }

  /** A shortcut to the transformer used. */
  protected CanvasObjectTransformer getTransformer() {
    return (CanvasObjectTransformer) ((MMCanvasObjectIF) m_master)
      .getCanvasTransformer();
  }

  /** A shortcut to the transformer used. */
  protected Canvas2DObjectTransformer getTransformer2D() {
    return (Canvas2DObjectTransformer) ((MMCanvasObjectIF) m_master)
      .getCanvasTransformer();
  }

}
