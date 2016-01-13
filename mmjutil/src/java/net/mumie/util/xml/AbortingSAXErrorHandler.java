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

package net.mumie.util.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

/**
 * An SAX error handler which aborts the parsing process when an error or warning occurs.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbortingSAXErrorHandler.java,v 1.1 2007/04/23 23:22:03 rassy Exp $</code>
 */

public class AbortingSAXErrorHandler
  implements ErrorHandler
{
  /**
   * <p>
   *   Handles the specified SAX parse exception with the specified type by throwing a
   *   new {@link SAXException SAXException}. The description of the latter exception is
   *   constructed from the type and the SAX parse exception's source location and 
   *   description.
   * </p>
   * <p>
   *   The type can be used to distinguish errors, fatal errors and warnings. The source
   *   location comprises the public id, the system id, the line number, and the column
   *   number. However, it may happen that not all of these data are available. The 
   *   description of the SAX parse exception is obtained by its
   *   {@link SAXParseException#getMessage getMessage} method.
   * </p>
   * <p>
   *   The description of the new exception has the following form:
   *   <pre>  <var>type</var>: [<var>public_id</var> ]<var>system_id</var>: line <var>line_number</var>, column <var>colum_number</var>: <var>description</var></pre>
   *   The notations should be self-explanatory. The square brackets <code>'['</code> and
   *   <code>']'</code> indicate that the enclosed term is optional.
   * </p>
   */

  protected void handle (String type, SAXParseException exception)
    throws SAXException 
  {
    int lineNumber = exception.getLineNumber();
    int columnNumber = exception.getColumnNumber();
    String publicId = exception.getPublicId();
    String systemId = exception.getSystemId();

    // Start message:
    StringBuilder message = new StringBuilder(type);
    message.append(": ");

    // Append public id to message if necessary:
    if ( publicId != null )
      message.append(publicId).append(" ");

    // Append system id to message:
    message.append(systemId != null ? systemId : "unknown source");

    // Append line number to message:
    message.append(", line ");
    if ( lineNumber != -1 )
      message.append(lineNumber);
    else
      message.append("unknown");

    // Append column number to message:
    message.append(", column ");
    if ( columnNumber != -1 )
      message.append(columnNumber);
    else
      message.append("unknown");

    // Add description from exception:
    message.append(": ").append(exception.getMessage());

    // Throw new exception:
    throw new SAXException(message.toString());
  }

  /**
   * <p>
   *   Handles an SAX error by throwing a {@link SAXException SAXException} (which
   *   aborts the processing). A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT ERROR", transformerException)</pre>
   * </p>
   * <p>
   *   NOTE: This implementation is not completely conform to the specification. Due to the
   *   specification, the error is "recoverable" and the parser must try to continue.
   *   However, often it is better to abort immediately if an error of this kind occurrs.
   * </p>
   */

  public void error (SAXParseException exception)
    throws SAXException
  {
    this.handle("SAX ERROR", exception);
  }

  /**
   *   Handles an SAX fatal error by throwing a {@link SAXException SAXException} (which
   *   aborts the processing). A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT ERROR", transformerException)</pre>
   */

  public void fatalError (SAXParseException exception)
    throws SAXException
  {
    this.handle("SAX FATAL ERROR", exception);
  }

  /**
   * <p>
   *   Handles an SAX warning by throwing a {@link SAXException SAXException} (which
   *   aborts the processing). A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT ERROR", transformerException)</pre>
   * </p>
   * <p>
   *   NOTE: This implementation is not conform to the specification. Due to the
   *   specification, the parser must continue.
   * </p>
   */

  public void warning (SAXParseException exception)
    throws SAXException
  {
    this.handle("SAX WARNING", exception);
  }
}
