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

package net.mumie.jvmd.cmdlib;

import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.util.CmdlineParamHelper;

/**
 * Prints a system property.
 *
 *   <h4>Usage:</h4>
 *   <pre class="code"><!-- 
     -->  getprop <var>NAME</var>
<!-- -->  getprop --help | -h | --version | -v</pre>
 *   <h4>Description:</h4>
 *   <p>
 *     Prints the value of the a system property. <var>NAME</var> is the name of the
 *     property. If the property does not exist, prints the empty string and returns 10
 *     as exit value.
 *   </p>
 *   <h4>Options:</h4>
 *   <dl>
 *     <dt><code>--help, -h</code></dt>
 *     <dd>
 *       Prints this help text and exists
 *     </dd>
 *     <dt><code>--version , -v</code></dt>
 *     <dd>
 *       Prints version information and exists
 *     </dd>
 *   </dl>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Getprop.java,v 1.3 2007/07/16 10:49:30 grudzin Exp $</code>
 */

public class Getprop extends AbstractCommand
{
  /**
   * The command name (<code>"getprop"</code>).
   */

  public static final String COMMAND_NAME = "getprop";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Prints the value of a system property";

  /**
   * Returns the command name (<code>"getprop"</code>).
   */

  public String getName ()
  {
    return COMMAND_NAME;
  }

  /**
   * Returns a short description of the command
   */

  public String getDescription ()
  {
    return COMMAND_DESCRIPTION;
  }

  /**
   * Sets a system property
   */

  protected void setProperty (String name, String value)
    throws Exception
  {
    System.setProperty(name, value);
  }

  /**
   * Prints the value of a system property to stdout
   */

  protected int getprop (String name)
    throws Exception
  {
    String value = System.getProperty(name);
    this.out.println(value != null ? value : "");
    return (value != null ? 0 : 10);
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    this.out.print
      ("Usage:\n" +
       "  " + COMMAND_NAME + " NAME\n" +
       "  " + COMMAND_NAME + " --help | -h | --version | -v\n" +
       "Description:\n" +
       "  Prints the value of the a system property. NAME is the name of the property.\n" +
       "  If the property does not exist, prints the empty string and returns 10 as\n" +
       "  exit value.\n" +
       "Options:\n" +
       "  --help, -h\n" +
       "      Print this message and exit.\n" +
       "  --version, -v\n" +
       "      Print version information and exit.\n");
  }

  /**
   * Prints version information to the shell output.
   */

  public void showVersion ()
    throws Exception
  {
    this.out.println("$Revision: 1.3 $");
  }

  /**
   * Executes the command
   */

  public int execute ()
    throws CommandExecutionException
  {
    try
      {
        this.checkStop();

        final int GETPROP = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = GETPROP;
        int exitValue = 0;

        String name = null;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( name == null && paramHelper.checkArgument() )
              name = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case GETPROP:
            if ( name == null )
              throw new IllegalArgumentException("No property name specified");
            exitValue = this.getprop(name);
            break;
          case SHOW_HELP:
            this.showHelp();
            break;
          case SHOW_VERSION:
            this.showVersion();
            break;
          }

        return exitValue;
      }
    catch (Exception exception)
      {
        throw new CommandExecutionException(exception);
      }
  }
}
