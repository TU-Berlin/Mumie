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

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MMCanvas;

/**
 * The base class for all global handlers - i.e. handlers that are associated to a
 * {@link net.mumie.mathletfactory.display.MMCanvas}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public abstract class GlobalHandler {
  
  
  /** The canvas this global handler is associated with. */
  protected final MMCanvas m_canvas;
  
  /** Whether this handler processes events or not. */
  protected boolean m_active = true;
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalHandler(MMCanvas aCanvas) {
    m_canvas = aCanvas;
    m_canvas.addGlobalHandler(this);
  }
  

  /**
   * Returns whether this instance
   * is able to work with the given {@link net.mumie.mathletfactory.action.MMEvent}.
   * <br><br>
   * This method will typically be called from within the <code>controlAction(MMEvent)
   * </code> method of a <code>CanvasControllerIF</code>
   * (e.g. {@link net.mumie.mathletfactory.action.DefaultCanvasController}), to check
   * if this instance can be invoked by calling this <code>GlobalHandler</code>'s
   * {@link #doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
   * method with the given <code>MMEvent</code>.
   */  
  public boolean dealsWith(MMEvent event) {
    if(m_active)
      return userDefinedDealsWith(event);
    else
      return false;
  }
  
  /**
   * Returns whether this instance
   * is able to work with the given {@link net.mumie.mathletfactory.action.MMEvent}.
   * <br><br>
   * This method will typically be called from within the <code>controlAction(MMEvent)
   * </code> method of a <code>CanvasControllerIF</code>
   * (e.g. {@link net.mumie.mathletfactory.action.DefaultCanvasController}), to check
   * if this instance can be invoked by calling this <code>GlobalHandler</code>'s
   * {@link #doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
   * method with the given <code>MMEvent</code>.
   * <br>
   * <b>Attention:</b> This method will always return <code>false</code>
   * if {@link #isActive()} returns <code>false</code>, i.e. if this
   * <code>GlobalHandler</code> was deactivated.
   */
  protected abstract boolean userDefinedDealsWith(MMEvent event);
  
  /**
   * Processes the incoming <code>MMEvent</code> by executing the user defined actions,
   * if this <code>GlobalHandler</code> can work with it (see
   * {@link #dealsWith(net.mumie.mathletfactory.action.MMEvent) dealsWith} ).
   * <br>
   * 
   * @see #dealsWith(net.mumie.mathletfactory.action.MMEvent)
   * @see net.mumie.mathletfactory.action.DefaultCanvasController#controlAction() 
   */
  public abstract boolean doAction(MMEvent event);

  /**
   * This method is called, when an action finishes (a mouse button is released
   * after dragging or klicking, a previously pressed key is released, etc.).
   * This method must be called when an <b>action cycle</b> shall be properly terminated,
   * typically it will be called within the
   * {@link net.mumie.mathletfactory.action.CanvasControllerIF#finishAction() finishAction}
   * method in <code>CanvasControllerIF</code>.
   * <br><br>
   * This method is dedicated to clean up internal settings in this <code>GlobalHandler</code>,
   * so that it will be prepared for properly processing a new action cycle.
   */
  public abstract void finish();
  
  
  /**
   * Returns the canvas this global handler is associated with.
   */
  public final MMCanvas getCanvas() {
    return m_canvas;
  }

  /**
   * Calling this method with value <code>false</code> deactivates this <code>MMHandler
   * </code> which means that this <code>GlobalHandler</code> will no longer process it's
   * {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method.
   */  
  public void setActive(boolean aFlag) {
    m_active = aFlag;
  }
  
  /**
   * Returns whether this <code>GlobalHandler</code> would currently process it's
   * {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method or not.
   * A deactivated <code>GlobalHandler</code> acts if it were not existent.
   * 
   * @return
   */
  public boolean isActive() {
    return m_active;
  }
  
}

