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
import net.mumie.jvmd.cmd.CommandFactory;
import net.mumie.jvmd.cmd.CommandLoadingException;
import net.mumie.util.CmdlineParamHelper;

/**
 * Loads a command. 
 * 
 *     <h4>Usage:</h4>
     <pre class="code"><!--
     -->  loadcmd [ --pool-max=<var>NUM</var> | -m <var>NUM</var> ] <var>CLASS</var>
<!-- -->  loadcmd --help | -h | --version | -v</pre>
 *     <h4>Description:</h4>
 *     <p>
 *       Loads a command. <var>CLASS</var> is the fully qualified name of the class
 *       implementing the command. <var>NUM</var> is the maximum pool size of the command.
 *       The default is 5.
 *     </p>
 *     <h4>Options:</h4>
 *     <dl>
 *       <dt><code>--pool-max=<var>NUM</var>, -m <var>NUM</var></code></dt>
 *       <dd>
 *         Set the maximum pool size
 *       </dd>
 *       <dt><code>--help, -h</code></dt>
 *       <dd>
 *         Print this message and exit.
 *       </dd>
 *       <dt><code>--version, -v</code></dt>
 *       <dd>
 *         Print version information and exit.
 *       </dd>
 *     </dl>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Loadcmd.java,v 1.2 2007/07/16 10:49:30 grudzin Exp $</code>
 */

public class Loadcmd extends AbstractCommand
{
  /**
   * The command name (<code>"loadcmd"</code>).
   */

  public static final String COMMAND_NAME = "loadcmd";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Loads a command";

  /**
   * Returns the command name (<code>"loadcmd"</code>).
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
   * Main task, loads the command.
   */

  protected void loadcmd (String className, int poolMax)
    throws CommandLoadingException
  {
    CommandFactory.getSharedInstance().loadCommand(className, poolMax);
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    this.out.print
      ("Usage:\n" +
       "  " + COMMAND_NAME + " [ --pool-max=NUM | -m NUM ] CLASS\n" +
       "  " + COMMAND_NAME + " --help | -h | --version | -v\n" +
       "Description:\n" +
       "  Loads a command. CLASS is the fully qualified name of the class\n" +
       "  implementing the command. NUM is the maximum pool size of the command.\n" +
       "  The default is 5.\n" +
       "Options:\n" +
       "  --pool-max=NUM, -m NUM\n" +
       "      Set the maximum pool size\n" +
       "  --help, -h\n" +
       "      Print this message and exit.\n" +
       "  --version, -v\n" +
       "      Print version information and exit.\n");
  }

  /**
   * Prints the version to stdout.
   */

  public void showVersion ()
  {
    this.out.println("$Revision: 1.2 $");
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

        final int LOADCMD = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = LOADCMD;

        String className = null;
        int poolMax = 5;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkOptionWithValue("--pool-max", "-m") )
              poolMax = Integer.parseInt(paramHelper.getValue());
            else if ( className == null && paramHelper.checkArgument() )
              className = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case LOADCMD:
            if ( className == null )
              throw new IllegalArgumentException("No class name specified");
            this.loadcmd(className, poolMax);
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
