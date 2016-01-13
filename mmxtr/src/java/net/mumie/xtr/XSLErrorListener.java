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

package net.mumie.xtr;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: XSLErrorListener.java,v 1.2 2006/10/23 11:42:59 rassy Exp $</code>
 */

public class XSLErrorListener implements ErrorListener
{
  /**
   * <p>
   *   Handles the specified transformer exception with the specified type. Prints the type,
   *   source location, and description of the exception to stderr. If <code>abort</code> is
   *   <code>true</code>, the Java virtual machine terminates with the specified exit
   *   code. Otherwise, the process continues and <code>exitCode</code> is ignored.
   * </p>
   * <p>
   *   The type can be used to distinguish XSLT errors, fatal errors and warnings. The
   *   source location comprises the public id if existing, the system id, the line number,
   *   and the column number; but it may happen that not all of these data are
   *   available. The description of the exception is obtained by the latter's
   *   {@link TransformerException#getMessage getMessage} method.
   * </p>
   * <p>
   *   The text printed to stdout has the following form:
   *   <pre>  <var>type</var>: [<var>public_id</var> ]<var>system_id</var>: line <var>line_number</var>, column <var>colum_number</var>: <var>description</var></pre>
   *   The notations should be self-explanatory. The square brackets <code>'['</code> and
   *   <code>']'</code> indicate that the enclosed term is optional.
   * </p>
   */

  protected void handle (String type,
                         TransformerException exception,
                         boolean abort,
                         int exitCode)
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

    // Print message to stderr:
    System.err.println(message.toString());

    // Exit if necessary:
    if ( abort )
      System.exit(exitCode);
  }

  /**
   * Handles an XSLT error. Prints an error message to stderr, but continues the
   * transformation. A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT ERROR", transformerException, false, 0);</pre>
   */

  public void error (TransformerException transformerException)
    throws TransformerException
  {
    this.handle("XSLT ERROR", transformerException, false, 0);
  }

  /**
   * Handles an XSLT fatal error. Prints an error message to stderr, but continues the
   * transformation. A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT FATAL ERROR", transformerException, false, 0);</pre>
   */

  public void fatalError (TransformerException transformerException)
    throws TransformerException
  {
    this.handle("XSLT FATAL ERROR", transformerException, false, 0);
  }

  /**
   *   Handles an XSLT warning. Prints a message to stderr and continues the
   *   transformation.
   */

  public void warning (TransformerException transformerException)
    throws TransformerException
  {
    this.handle("XSLT WARNING", transformerException, false, 0);
  }
}

