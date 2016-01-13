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

package net.mumie.jvmd.cmd;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.InputStream;

/**
 * <p>
 *   Abstract base class for implementing Jvmd commands. Provides member variables for the
 *   i/o streams, the command line parameters, and the working directory (see <em>Field
 *   Summery</em> below). The respective getXxx, setXxx, and addXxx methods required by the
 *   {@link Command Command} interface are implemented accordingly. Another member variable
 *   is the so-called <em>stop flag</em> (see {@link #stop stop}). This boolean flag is
 *   false by default and set to true if the {@link #stop() stop()} method is
 *   called. Extending classes should check the flag at appropriate times and throw a
 *   {@link CommandStoppedException CommandStoppedException} if it is true. For convenience,
 *   a method {@link #checkStop checkStop} is provided which performs the checking and
 *   exception throwing.
 * </p>
 * <p>
 *   This class contains some utility methods:
 *   {@link #makeAbsoluteFile(File) makeAbsoluteFile(<var>File</var>)} and
 *   {@link #makeAbsoluteFile(String) makeAbsoluteFile(<var>String</var>)},
 *   which make relative file paths absolute with respect to the command's working
 *   directory; and {@link #expandShortOptions expandShortOptions}, which expands bundled
 *   short options (e.g., converts <code>-rfq</code> to <code>-r -f -q</code>).
 * </p>
 * <p>
 *  <strong>Please note:</strong> Do not use <code>myFile.getAbsoluteFile()</code> to make a
 *  {@link File File} object absolute in a Jvmd command &ndash; that would interpret
 *  <code>myFile</code> relative to the directory where you started the Jvmd server, not
 *  relative to the working directory of the command. Use
 *  <code>makeAbsoluteFile(myFile)</code> instead. (If <code>myFile</code> is already
 *  absolute, this is no issue, of course). The same applies to strings denoting relative
 *  paths.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractCommand.java,v 1.7 2007/07/16 10:49:29 grudzin Exp $</code>
 */

abstract public class AbstractCommand
  implements Command
{
  /**
   * The working directory.
   */

  protected File directory = null;

  /**
   * The parameters.
   */

  protected List params = new ArrayList();

  /**
   * The input stream.
   */

  protected InputStream in = null;

  /**
   * The output stream.
   */

  protected PrintStream out = null;

  /**
   * The error stream.
   */

  protected PrintStream err = null;

  /**
   * Flag indicating that this command should terminate execution..
   */

  protected boolean stop = false;

  /**
   * Sets the working directory.
   */

  public void setDirectory (File directory)
  {
    if ( !directory.exists() )
      throw new IllegalArgumentException
        ("Working directory does not exist: " + directory);
    if ( !directory.isDirectory() )
      throw new IllegalArgumentException
        ("File specified as working directory is not a directory: " + directory);
    this.directory = directory;
  }

  /**
   * Returns the working directory.
   */

  public File getDirectory ()
  {
    return this.directory;
  }

  /**
   * Adds a command line parameter.
   */

  public void addParameter (String parameter)
  {
    this.params.add(parameter);
  }

  /**
   * Sets the input stream.
   */

  public void setInputStream (InputStream in)
  {
    this.in = in;
  }

  /**
   * Sets the output stream.
   */

  public void setOutputStream (PrintStream out)
  {
    this.out = out;
  }

  /**
   * Sets the error stream.
   */

  public void setErrorStream (PrintStream err)
  {
    this.err = err;
  }

  /**
   * Stops (or tries to stop) the execution of this command. This implementation sets the
   * {@link #stop stop} flag to <code>true</code>. This implementation is synchronized.
   */

  public synchronized void stop ()
  {
    this.stop = true;
  }

  /**
   * Throws a {@link CommandStoppedException CommandStoppedException} if the
   * {@link #stop stop} flag is set.
   */

  protected void checkStop ()
    throws CommandStoppedException
  {
    if ( this.stop )
      throw new CommandStoppedException ();
  }

  /**
   * Resets this instance for later reuse. This implemetation does the following:
   * <ul>
   *   <li>The variables
   *     {@link #directory},
   *     {@link #in},
   *     {@link #out},
   *     {@link #err}, and
   *     {@link #stop}
   *     are set to null,</li>
   *   <li>The variable {@link #params} is set to a newly created, empty list.</li>
   * </ul>
   */

  public void reset ()
  {
    this.directory = null;
    this.params = new ArrayList();
    this.in = null;
    this.out = null;
    this.err = null;
    this.stop = false;
  }

  /**
   * Makes the specified file object absolute and returns it. If the file object is already
   * absolute, it is simply returned. Otherwise, it is made absolute with
   * {@link #directory directory} as parent.
   */

  protected File makeAbsoluteFile (File file)
  {
    return (file.isAbsolute() ? file : new File(this.directory, file.getPath()));
  }

  /**
   * <p>
   *   Makes the specified fileanme absolute and returns it a {@link File File} object. If
   *   the filename is already absolute, simply returns it a {@link File File} object.
   *   Otherwise, it is made absolute with {@link #directory directory} as parent.
   * </p>
   * <p>
   *   Same as
   *   {@link #makeAbsoluteFile(File) makeAbsoluteFile(new File(filename))}.
   * </p>
   */

  protected File makeAbsoluteFile (String filename)
  {
    return this.makeAbsoluteFile(new File(filename));
  }

  /**
   * Expands boundled short options in the parameters. For example, replaces
   * <code>-rbf</code> by <code>-r -b -f</code>. The string <code>shortOptions</code>
   * specifies the short options that should be expanded. Each parameter that starts with a
   * hyphen and contains only characters in <code>shortOptions</code> is replaced.
   */

  protected void expandShortOptions (String shortOptions)
  {
    for (int i = 0; i < this.params.size(); i++)
      {
	char[] paramChars = ((String)this.params.get(i)).toCharArray();
	if ( paramChars[0] == '-' )
	  {
	    // Checking if parameter contains only short option characters after the dash ...
	    int k = 1;
	    while ( k < paramChars.length && shortOptions.indexOf(paramChars[k]) != -1 ) k++;
	    if ( k == paramChars.length )
	      { // ... yes.
		this.params.remove(i);
		i--;
		for (int m = 1; m < paramChars.length; m++)
		  {
		    i++;
		    char[] expandedParamChars = {'-', paramChars[m]};
		    this.params.add(i, new String(expandedParamChars));
		  }
	      }
	  }
      }
  }
}
