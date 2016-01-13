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

package net.mumie.mathletfactory.action.message;

import java.util.EventListener;

/**
 * This interface is implemented by classes that want to catch mathematical special 
 * cases (signaled by {@link SpecialCaseEvent}s 
 * (which are usually fired by 
 * {@link net.mumie.mathletfactory.action.handler.MMHandler MMHandler}s and
 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF MMObject}s).<br>
 * A special case occurs for example, when a division by zero occurs, a set of 
 * vectors becomes linear dependent, etc. Because it is not always clear that 
 * a special case will lead to an error or exception, the client is able to
 * handle the special case himself by opening a popup, displaying a warning 
 * sign, etc.
 *
 * @see SpecialCaseEvent
 *    
 * @author Paehler
 * @mm.docstatus finished
 */
 
public interface SpecialCaseListener extends EventListener {

  /**
   *  Displays the message of the given Event by popping up a window or 
   *  displaying it on a pane, etc.
   */  
  public void displaySpecialCase(SpecialCaseEvent eventToBeDisplayed);
}
