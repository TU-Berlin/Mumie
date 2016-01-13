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
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Declares the public methods a Jvmd command must have. For a Java class being suitable as
 * a Jvmd command, it must implement this interface <em>and</em> define a static public
 * String variable <code>COMMAND_NAME</code> containing the command name. It is recommended
 * that, in addition, the class defines a static public String variable
 * <code>COMMAND_DESCRIPTION</code> containing a short description (about 50 characters, one
 * line) of the command.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Command.java,v 1.4 2007/07/16 10:49:29 grudzin Exp $</code>
 */

public interface Command
{
  /**
   * Returns the command name.
   */

  public String getName ();

  /**
   * Returns a short description of the command
   */

  public String getDescription ();

  /**
   * Sets the working directory.
   */

  public void setDirectory (File directory);

  /**
   * Adds a command line parameter.
   */

  public void addParameter (String parameter);

  /**
   * Sets the input stream.
   */

  public void setInputStream (InputStream in);

  /**
   * Sets the output stream.
   */

  public void setOutputStream (PrintStream out);

  /**
   * Sets the error stream.
   */

  public void setErrorStream (PrintStream err);

  /**
   * Executes the command
   */

  public int execute ()
    throws CommandExecutionException;

  /**
   * Stops (or tries to stop) the execution of the command
   */

  public void stop ();

  /**
   * Resets this instance for later reuse.
   */

  public void reset ();
}
