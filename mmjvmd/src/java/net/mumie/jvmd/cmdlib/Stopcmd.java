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
import net.mumie.jvmd.Jvmd;

/**
 * Stops a command.
 *
 *   <h4>Usage:</h4>
 *   <pre class="code"><!-- 
     -->  stopcmd <var>CLIENT_PID</var>
<!-- -->  stopcmd --help | -h | --version | -v</pre>
 *   <h4>Description:</h4>
 *   <p>
 *     Stops (or tries to stop) a command. <var>CLIENT_PID</var> is the client pid of the
 *     command to stop. Note that only the stop flag is set for the command, which
 *     may be ignored, so that the command is not stopped.
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
 * @version <code>$Id: Stopcmd.java,v 1.2 2007/07/16 10:49:30 grudzin Exp $</code>
 */

public class Stopcmd extends AbstractCommand
{
  /**
   * The command name (<code>"stopcmd"</code>).
   */

  public static final String COMMAND_NAME = "stopcmd";

  /**
   * A short description of the command
   */

  public static final String COMMAND_DESCRIPTION = "Stops a command";

  /**
   * Returns the command name (<code>"stopcmd"</code>).
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
   * Main task, stops the command.
   */

  protected void stopcmd (long clientPID)
  {
    Jvmd.getSharedInstance().stopCommandThread(clientPID);
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    this.out.print
      ("Usage:\n" +
       "  " + COMMAND_NAME + " CLIENT_PID\n" +
       "  " + COMMAND_NAME + " --help | -h | --version | -v\n" +
       "Description:\n" +
       "  Stops (or tries to stop) a command. CLIENT_PID is the client pid of the\n" + 
       "  command to stop. Note that only the stop flag is set for the command, which\n" + 
       "  may be ignored, so that the command is not stopped.\n" + 
       "Options:\n" +
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

        final int STOPCMD = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = STOPCMD;

        long clientPID = -1;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( clientPID == -1 && paramHelper.checkArgument() )
              {
                clientPID = Long.parseLong(paramHelper.getParam());
                if ( clientPID < 0 )
                  throw new IllegalArgumentException("Illegal client pid: " + clientPID);
              }
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case STOPCMD:
            if ( clientPID == -1 )
              throw new IllegalArgumentException("No client pid specified");
            this.stopcmd(clientPID);
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
