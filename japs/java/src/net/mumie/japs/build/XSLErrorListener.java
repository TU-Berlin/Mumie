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

package net.mumie.japs.build;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import net.mumie.util.Util;

/**
 * XSL error handler for the Japs build. This handler terminates the virtual machine with a
 * message printed to stderr on XSL errors, fatal errors, and, by default, even on
 * warnings. Though this is not completely conform to the respective specification, it is
 * the appropriate behaviour in the Japs build.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: XSLErrorListener.java,v 1.5 2007/07/11 15:38:55 grudzin Exp $</code>
 */

public class XSLErrorListener implements ErrorListener
{
  /**
   * Whether the Java virtual machine should terminate when an XSLT warning occurs or not.
   */

  protected boolean failOnWarning = true;

  /**
   * <p>
   *   Handles the specified transformer exception with the specified type. Prints the type,
   *   source location, and description of the exception to stdout. If <code>abort</code> is
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
   * <p>
   *   Handles an XSLT error. The Java virtual machine terminates with an error message. The
   *   exit code is 2. A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT ERROR", transformerException, true, 2);</pre>
   * </p>
   * <p>
   *   NOTE: This implementation is not completely conform to the specification. Due to the
   *   specification, the error is "recoverable" and the transformer must try to
   *   continue. However, in the japs build process it is better to abort immediately if an
   *   error of this kind occurrs.
   * </p>
   */

  public void error (TransformerException transformerException)
    throws TransformerException
  {
    this.handle("XSLT ERROR", transformerException, true, 2);
  }

  /**
   * <p>
   *   Handles an XSLT fatal error. The Java virtual machine terminates with an error
   *   message. The exit code is 3. A call to this method is identical to<pre>
   *   this.{@link #handle handle}("XSLT FATAL ERROR", transformerException, true, 3);</pre>
   * </p>
   * <p>
   *   NOTE: This implementation is not completely conform to the specification. Due to the
   *   specification, the transformer must try to continue. However, in the japs build
   *   process it is better to abort immediately if an error of this kind occurrs.
   * </p>
   */

  public void fatalError (TransformerException transformerException)
    throws TransformerException
  {
    this.handle("XSLT FATAL ERROR", transformerException, true, 3);
  }

  /**
   * <p>
   *   Handles an XSLT warning. What happens depends on the flag
   *   {@link #failOnWarning failOnWarning}: if <code>true</code>, the Java virtual machine
   *   terminates with exit code 1, if <code>false</code>, the transformation continues. In
   *   any case, a message including the source location is printed to stdout. A call to
   *   this method is identical to<pre> 
   *   this.{@link #handle handle}("WARNING", transformerException, this.failOnWarning, 1);</pre>
   * </p>
   * <p>
   *   By default, {@link #failOnWarning failOnWarning} is <code>true</code>. This can be
   *   changed by means of the system property
   *   <code>net.mumie.japs.build.failOnWarning</code>. If the latter is
   *   <code>"false"</code>  or <code>"no"</code>, {@link #failOnWarning failOnWarning} will
   *   be set to <code>false</code> (cf. {@link #initFailOnWarning initFailOnWarning}).
   * </p>
   * <p>
   *   NOTE: This implementation is not completely conform to the specification. Due to the
   *   specification, the transformer must continue in any case . However, in the japs build
   *   process it is better to abort immediately if an XSL warning occurrs, because even
   *   severe problems (e.g., document() function can not find the file to load) might be
   *   reported as warnings.
   * </p>
   */

  public void warning (TransformerException transformerException)
    throws TransformerException
  {
    this.handle("XSLT WARNING", transformerException, this.failOnWarning, 1);
  }

  /**
   * <p>
   *   Initializes the {@link #failOnWarning failOnWarning} flag. Reads the system property
   *   <code>net.mumie.japs.build.failOnWarning</code>. If its value is <code>"true"</code>
   *   or <code>"yes"</code>, {@link #failOnWarning failOnWarning} is set to
   *   <code>true</code>, if its value is <code>"false"</code> or <code>"no"</code>,
   *   {@link #failOnWarning failOnWarning} is set to <code>false</code>. If the value is
   *   any other string, an {@link IllegalArgumentException IllegalArgumentException} is
   *   thrown.
   * </p>
   * <p>
   *   This method is called in the constructor.
   * </p>
   */

  protected void initFailOnWarning ()
  {
    final String propertyName = this.getClass().getName() + ".failOnWarning";
    String propertyValue = System.getProperty(propertyName, "true");
    if ( propertyValue.equals("true") || propertyValue.equals("yes") )
      this.failOnWarning = true;
    else if ( propertyValue.equals("false") || propertyValue.equals("no") )
      this.failOnWarning = false;
    else
      throw new IllegalArgumentException
        ("Illegal value for system property " + propertyName + ": " +
         propertyValue +
         " (expected true|yes|false|no)");
  }

  /**
   * Creates a new instance. Calls {@link #initFailOnWarning initFailOnWarning}.
   */

  public XSLErrorListener ()
  {
    this.initFailOnWarning();
  }
}
