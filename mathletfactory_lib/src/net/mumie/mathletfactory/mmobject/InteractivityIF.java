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

package net.mumie.mathletfactory.mmobject;

import java.util.List;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;

/**
 * This interface defines the interactive part of all {@link MMObjectIF MMObject}s. It is implemented 
 * by all classes that take part of the mumie interaction system. 
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public interface InteractivityIF {
  
  /**
   * Tries to handle the given event for this object. If the
   * event is handled, <code>true</code> is returned, otherwise
   * <code>false</code>. 
   */
  public boolean doAction(MMEvent event);

  /** Associates the specified handler with this object. */
  public void addHandler(MMHandler aHandler);
  
  /** Removes the specified handler associated with this object. */
  public void removeHandler(MMHandler aHandler);

  /** Removes all handlers associated with this object. */
  public void removeAllHandlers();
  
  /** Returns a list of all previously added handlers. */
  public List getHandlers();
  
  /** Returns the number of handlers associated with this object. */
  public int getHandlerCount();
  
  /**
   * Returns the handler that consumed the last 
   * {@link net.mumie.mathletfactory.action.MMEvent}.
   */
  public MMHandler getActiveHandler();
  
  /**
   * sets the active 
   * {@link net.mumie.mathletfactory.action.handler.MMHandler Handler}
   * to <code>null</code>. 
   */
  public void resetActiveHandler();
  
  /**
   * Associates an updater with this object: Whenever the state of this 
   * object changes {@link #invokeUpdaters} may be called to inform
   * other objects of the change.
   */
  public void addUpdater(MMUpdater anUpdater);
  
  /** Removes the specified updater from the list of updaters associated with this object. */
  public void removeUpdater(MMUpdater anUpdater);
  
  /** Removes all updaters associated with this object. */
  public void removeAllUpdaters();
  
  /**
   * Returns the number of updaters associated with this object.
   */
  public int getUpdaterCount();
  
  /**
   * This method calls 
   * {@link net.mumie.mathletfactory.action.updater.MMUpdater#update}
   * in all {@link net.mumie.mathletfactory.action.updater.MMUpdater}s
   * associated with this object.  
   */
  public void invokeUpdaters();
    
  /**
   * Returns true, if this object is selectable in a 
   * {@link net.mumie.mathletfactory.display.MMCanvas}.
   */
  public boolean isSelectable();
  
  /**
   * Returns true, if this object has been selected in a 
   * {@link net.mumie.mathletfactory.display.MMCanvas}.
   */
  public boolean isSelected();
  
  /**
   * Returns a new 
   * {@link net.mumie.mathletfactory.action.updater.DependencyUpdater}
   * that has <code>obj</code> as free object and this object as dependent 
   * object.
   */
  public void dependsOn(MMObjectIF obj, DependencyIF dependency);
  
  /**
   * Returns a new 
   * {@link net.mumie.mathletfactory.action.updater.DependencyUpdater}
   * that has <code>objects</code> as free objects and this object as dependent 
   * object.
   */
  public void dependsOn(MMObjectIF[] objects, DependencyIF dependency);
  
}

