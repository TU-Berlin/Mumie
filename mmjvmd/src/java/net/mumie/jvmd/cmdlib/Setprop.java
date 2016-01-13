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
 * Sets a system property.
 *
 *   <h4>Usage:</h4>
 *   <pre class="code"><!-- 
 *      -->  setprop <var>NAME</var>=<var>VALUE</var>
<!-- -->  setprop <var>NAME</var> <var>VALUE</var>
<!-- -->  setprop --help | -h | --version | -v</pre>
 *   <h4>Description:</h4>
 *   <p>
 *     Sets a system property. <var>NAME</var> and <var>VALUE</var> are the name and the value
 *     of the property, respectively.
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
 * @version <code>$Id: Setprop.java,v 1.3 2007/07/16 10:49:30 grudzin Exp $</code>
 */

public class Setprop extends AbstractCommand
{
  /**
   * The command name (<code>"setprop"</code>).
   */

  public static final String COMMAND_NAME = "setprop";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Sets a system properties";

  /**
   * Returns the command name (<code>"setprop"</code>).
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
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    this.out.print
      ("Usage:\n" +
       "  " + COMMAND_NAME + " NAME VALUE\n" +
       "  " + COMMAND_NAME + " NAME=VALUE\n" +
       "  " + COMMAND_NAME + " --help | -h | --version | -v\n" +
       "Description:\n" +
       "  Sets a system property. NAME and VALUE are the name and the value of the\n" +
       "  property, respectively.\n" +
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

        final int SETPROP = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = SETPROP;

        String name = null;
        String value = null;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( name == null && paramHelper.checkArgument() )
              {
                String param = paramHelper.getParam();
                if ( name == null )
                  {
                    int posEq = param.indexOf('=');
                    if ( posEq == -1 )
                      {
                        name = param;
                        if ( ! ( paramHelper.next() && paramHelper.checkArgument() ) )
                          throw new IllegalArgumentException
                            ("Missing value after \"" + name + "\"");
                        name = paramHelper.getParam();
                      }
                    else if ( posEq > 0 )
                      {
                        name = param.substring(0, posEq);
                        value = param.substring(posEq+1);
                      }
                    else
                      throw new IllegalArgumentException
                        ("Missing name before \"=\" in \"" + param + "\"");
                  }
              }
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case SETPROP:
            if ( name == null || value == null )
              throw new IllegalArgumentException("Missing property to set");
            System.setProperty(name, value);
            break;
          case SHOW_HELP:
            this.showHelp();
            break;
          case SHOW_VERSION:
            this.showVersion();
            break;
          }

        return 0;
      }
    catch (Exception exception)
      {
        throw new CommandExecutionException(exception);
      }
  }
}
