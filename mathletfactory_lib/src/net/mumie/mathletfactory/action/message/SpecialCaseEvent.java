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

import java.util.EventObject;

import net.mumie.mathletfactory.util.ResourceManager;

/**
 * This class is used for signaling mathematical special cases to
 * {@link SpecialCaseListener}s (which are usually part of the client applet or
 * application).<br>
 * A special case occurs for example, when a division by zero occurs, a set of
 * vectors becomes linear dependent, etc. Because it is not always clear that
 * a special case will lead to an error or exception, the client is able to
 * handle the special case himself by opening a popup, displaying a warning
 * sign, etc.
 *
 * @see SpecialCaseListener
 *
 * @author Paehler
 * @mm.docstatus finished
 */
public class SpecialCaseEvent extends EventObject {

  private String m_messageIdentifier;

  /**
   *  Constructs the event with the given source and the
   *  <code>messageIdentifier</code> that is mapped onto the value specified
   *  in <code>Messages.properties[_LOCALE]</code> in this package.
   */
  public SpecialCaseEvent(Object source, String messageIdentifier) {
    super(source);
    m_messageIdentifier = messageIdentifier;
  }

  /**
   *  Sets the <code>messageIdentifier</code> which is mapped onto the value
   *  specified in <code>Messages.properties[_LOCALE]</code> in this package.
   */
  public void setMessageIdentifier(String messageIdentifier){
    m_messageIdentifier = messageIdentifier;
  }


  /**
   *  Returns the <code>messageIdentifier</code> which is mapped onto the value
   *  specified in <code>Messages.properties[_LOCALE]</code> in this package.
   */
  public String getMessageIdentifier(){
    return m_messageIdentifier;
  }

  /**
   *  Returns the message based on the messageIdentifier and the message in
   *  the language determined by {@link java.util.Locale#getDefault()}.
   *  If the message identifier is not found, the identifier itselt will be returned.
   */
  public String toString(){
		String message = ResourceManager.getMessage(m_messageIdentifier);
		if(message != null)
			return message;
		else
			return m_messageIdentifier;
  }
}
