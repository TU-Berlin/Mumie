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

package net.mumie.srv.checkin;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MasterException extends Exception
{
  /**
   * Creates a new <code>MasterException</code> with message
   * <code>description</code>.
   */

  public MasterException (String description)
  {
    super(description);
  }

  /**
   * Creates a new <code>MasterException</code> that wraps
   * <code>throwable</code>.
   */

  public MasterException (Throwable throwable)
  {
    super(throwable);
  }

  /**
   * Creates a new <code>MasterException</code> with message <code>description</code> that
   * wraps <code>throwable</code>.
   */

  public MasterException (String description, Throwable throwable)
  {
    super(description, throwable);
  }

  /**
   * Creates a new <code>MasterException</code>; the  message is <code>description</code>
   * prefixed with a string showing the specified location.
   */

  public MasterException (Element location, String description)
  {
    super(locationToString(location) +  ": " + description);
  }

  /**
   * Creates a new <code>MasterException</code> that wraps the specified throwable; the
   * message is <code>description</code> prefixed with a string showing the specified
   * location.
   */

  public MasterException (Element location, String description, Throwable throwable)
  {
    super(locationToString(location) +  ": " + description, throwable);
  }

  /**
   * Creates an XPath-like string displaying the local names of the specified node and its
   * ancestors. Not more then 9 ancestors are displayed.
   */

  protected static String locationToString (Element location)
  {
    Node node = location;
    Document document = location.getOwnerDocument();
    StringBuffer buffer = new StringBuffer();
    for (int i = 1; node != null && node != document && i <= 10; i++)
      {
        buffer.insert(0, "/" + node.getLocalName());
        node = node.getParentNode();
      }
    if ( node != null && node != document )
      buffer.insert(0, "[...]/");
    return buffer.toString();
  }
}
