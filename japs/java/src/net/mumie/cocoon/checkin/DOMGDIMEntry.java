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

package net.mumie.cocoon.checkin;

import org.w3c.dom.Element;

public class DOMGDIMEntry
  implements GDIMEntry
{
  // --------------------------------------------------------------------------------
  // Gloabl variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The GDIM entry element.
   */

  protected Element gdimEntryElement;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance from the DOM tree starting with the specified element.
   */

  public DOMGDIMEntry (Element gdimEntryElement)
  {
    this.gdimEntryElement = gdimEntryElement;
  }

  // --------------------------------------------------------------------------------
  // Getting the data
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the generic document, or {@link Id#UNDEFINED UNDEFINED} if no such id
   * is set.
   */

  public int getGenericDocId ()
    throws MasterException
  {
    return MasterUtil.getGenericDocId(this.gdimEntryElement);
  }

  /**
   * Returns the path of the generic document, or <code>null</code> if no such path is set.
   */

  public String getGenericDocPath ()
    throws MasterException
  {
    return MasterUtil.getGenericDocPath(this.gdimEntryElement);
  }

  /**
   * Returns the theme path, or <code>null</code> if no theme path is set.
   */

  public String getThemePath ()
    throws MasterException
  {
    return MasterUtil.getThemePath(this.gdimEntryElement);
  }

  /**
   * Returns the language path, or <code>null</code> if no language is set.
   */

  public String getLang ()
    throws MasterException
  {
    return MasterUtil.getLang(this.gdimEntryElement);
  }
  
  /**
   * Returns the languages code, or <code>null</code> if no language is set.
   */

  public String getLangCode ()
    throws MasterException
  {
    return MasterUtil.getLangCode(this.gdimEntryElement);
  }
  
}
