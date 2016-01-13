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

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * An updater using {@link DependencyIF Dependencies} for updating. 
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class DependencyUpdater extends MMUpdater {
  
  
  private DependencyIF m_realizer;
  
  public DependencyUpdater(MMObjectIF slave, MMObjectIF free, DependencyIF dependency) {
    this(slave,new MMObjectIF[]{free},dependency);
  }
  
  public DependencyUpdater(MMObjectIF slave, MMObjectIF[] free, DependencyIF dependency) {
    super(slave,free);
    m_realizer = dependency;
    update();
    ActionManager.reset();
  }
  
  public void userDefinedUpdate() {
    m_realizer.doUpdate(getSlave(),m_parents);
    if(getParent(0) != null)
      m_realizer.doUpdate(getSlave(), getParent(0));
    m_realizer.doUpdate();
  }
}

