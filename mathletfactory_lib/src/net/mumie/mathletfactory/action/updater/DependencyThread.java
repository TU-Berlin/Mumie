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

package net.mumie.mathletfactory.action.updater;

import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * This class implements the dependency functionality with a thread to prevent
 * the Java VM to hang during calculations.
 * Subclasses of this class must implement a {@link net.mumie.mathletfactory.action.updater.DependencyIF}
 * (e.g. a {@link net.mumie.mathletfactory.action.updater.DependencyAdapter}) and one of the three 
 * <code>hasChanged</code> methods to indicate that the <code>free</code> objects have changed.
 * 
 * @author gronau
 * 
 * @see #hasChanged()
 * @see #hasChanged(MMObjectIF, MMObjectIF)
 * @see #hasChanged(MMObjectIF, MMObjectIF[])
 */
public class DependencyThread extends Thread implements Runnable, DependencyIF {

  private DependencyIF m_depedencyAdapter;

  private MMObjectIF m_dependant;
  private MMObjectIF[] m_free;

  private boolean m_isRunning = false;
  private boolean m_isSleeping = false;
  private boolean m_isStarted = false;
  private boolean m_recalc = false;

  /**
   * Constructs a new <code>DepedencyThread</code>-instance with given dependency adapter and 
   * minimal priority.
   */
  public DependencyThread(DependencyIF dependenyAdapter) {
    this(dependenyAdapter, MIN_PRIORITY);
  }
  
  /**
   * Constructs a new <code>DepedencyThread</code>-instance with given dependency adapter and 
   * priority.
   */
  public DependencyThread(DependencyIF dependenyAdapter, int priority) {
    m_depedencyAdapter = dependenyAdapter;
    setPriority(priority);
  }

  /** Returns if the <code>free</code> objects have changed. */
  public boolean hasChanged(MMObjectIF dependant, MMObjectIF[] free) {
  	return false;
  }
  
  /** Returns if the <code>free</code> objects have changed. */
  public boolean hasChanged(MMObjectIF dependant, MMObjectIF free) {
  	return false;
  }
  
  /** Returns if the <code>free</code> objects have changed. */
  public boolean hasChanged() {
  	return false;
  }
  
  private boolean oneHasChanged() {
  	if(hasChanged(m_dependant, m_free))
  		return true;
  	else if(m_free[0] != null && hasChanged(m_dependant, m_free[0]))
  		return true;
  	else if(hasChanged())
  		return true;
  	return false;
  }
  
  public void run() {
    while(m_isRunning) {
      if(m_recalc) {
        m_recalc = false;
        performUpdate();
      } else if(oneHasChanged()) {
        performUpdate();
      } else {
        sleep();
      }
    }
  }
  
  private void performUpdate() {
    m_depedencyAdapter.doUpdate(m_dependant, m_free);
    if(m_free[0] != null)
    	m_depedencyAdapter.doUpdate(m_dependant, m_free[0]);
    m_depedencyAdapter.doUpdate();
    m_dependant.render();
  }

  private synchronized void sleep() {
    if(isSleeping())
      return;
    try {
      m_isSleeping = true;
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private synchronized void wakeUp() {
    if(!isSleeping())
      return;
    m_isSleeping = false;
    notify();
  }

  private boolean isSleeping() {
    return m_isSleeping;
  }

  public void doUpdate() {}

  public void doUpdate(MMObjectIF dependant, MMObjectIF free) {}

  public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
    if(m_dependant == null)
      m_dependant = dependant;
    if(m_free == null)
      m_free = free;
    if(!m_isStarted) {
      m_isStarted = true;
      m_isRunning = true;
      start();
    } else if(isSleeping()) {
      wakeUp();
    }
  }
}
