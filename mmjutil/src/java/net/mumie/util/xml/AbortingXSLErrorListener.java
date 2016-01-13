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

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;

/**
 * An error listener for XSL transformations which aborts the transformation when an error
 * or warning occurs.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbortingXSLErrorListener.java,v 1.3 2007/04/23 23:54:51 rassy Exp $</code>
 */

public class AbortingXSLErrorListener
  implements ErrorListener
{

  /**
   * <p>
   *   Handles the specified transformer exception with the specified type by throwing a new
   *   {@link RuntimeException RuntimeException}. The description of the runtime exception
   *   is constructed from the type and the transformer exception's source location and
   *   description.
   * </p>
   * <p>
   *   The type can be used to distinguish errors, fatal errors and warnings. The source
   *   location comprises the public id, the system id, the line number, and the column
   *   number. However, it may happen that not all of these data are available. The
   *   description of the transformer exception is obtained by its
   *   {@link TransformerException#getMessage getMessage} method.
   * </p>
   * <p>
   *   The description of the runtime exception has the following form:
   *   <pre>  <var>type</var>: [<var>public_id</var> ]<var>system_id</var>: line <var>line_number</var>, column <var>colum_number</var>: <var>description</var></pre>
   *   The notations should be self-explanatory. The square brackets <code>'['</code> and
   *   <code>']'</code> indicate that the enclosed term is optional.
   * </p>
   */

  protected void handle (String type, TransformerException exception)
  {
    // Start message:
    StringBuffer message = new StringBuffer(type);

    message.append(": ");

    // Add location:
    SourceLocator locator = exception.getLocator();
    if ( locator != null )
      {
        String publicId = locator.getPublicId();
        String systemId = locator.getSystemId();
        int lineNumber = locator.getLineNumber();
        int columnNumber = locator.getColumnNumber();

        // Append public id to message if necessary:
        if ( publicId != null )
          message.append(publicId).append(" ");

        // Append system id to message:
        message
          .append(systemId != null ? systemId : "unknown source");

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
      }
    else
      {
	message.append("unknown location");
      }

    // Add description from exception:
    message.append(": ").append(exception.getMessage());

    // Throw new exception:
    throw new RuntimeException(message.toString());
  }

  /**
   * <p>
   *   Handles an XSLT error by throwing a {@link RuntimeException RuntimeException} (which
   *   aborts the transformation). A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT ERROR", transformerException)</pre>
   * </p>
   * <p>
   *   NOTE: This implementation is not completely conform to the specification. Due to the
   *   specification, the error is "recoverable" and the transformer must try to continue.
   *   However, often it is better to abort immediately if an error of this kind occurrs.
   * </p>
   */

  public void error (TransformerException transformerException)
  {
    this.handle("XSLT ERROR", transformerException);
  }

  /**
   * <p>
   *   Handles an XSLT fatal error by throwing a {@link RuntimeException RuntimeException}
   *   (which aborts the transformation). A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT FATAL ERROR", transformerException)</pre>
   * </p>
   * <p>
   *   NOTE: This implementation is not completely conform to the specification. Due to the
   *   specification, the transformer must try to continue. However, often it is better to
   *   abort immediately if an error of this kind occurrs.
   * </p>
   */

  public void fatalError (TransformerException transformerException)
  {
    this.handle("XSLT FATAL ERROR", transformerException);
  }

  /**
   * <p>
   *   Handles an XSLT warning by throwing a {@link RuntimeException RuntimeException}
   *   (which aborts the transformation). A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT WARNING", transformerException)</pre>
   * </p>
   * <p>
   *   NOTE: This implementation is not conform to the specification. Due to the
   *   specification, the transformer must continue. However, it might be better to abort
   *   immediately if an XSL warning occurrs, because even severe problems (e.g.,
   *   <code>document()</code> function can not find the file to load) are reported as
   *   warnings. 
   * </p>
   */

  public void warning (TransformerException transformerException)
  {
    this.handle("XSLT WARNING", transformerException);
  }
}
