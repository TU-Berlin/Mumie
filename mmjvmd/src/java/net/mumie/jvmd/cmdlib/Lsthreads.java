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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.mumie.jvmd.Jvmd;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandFactory;
import net.mumie.jvmd.cmd.CommandThreadInfo;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.util.SimpleStringTable;

/**
 * Lists all currently running command threads.
 *
 *   <h4>Usage:</h4>
 *   <pre class="code"><!-- 
     -->  lsthreads
<!-- -->  lsthreads --help | -h | --version | -v</pre>
 *   <h4>Description:</h4>
 *   <p>
 *     Prints informations abount all all currently running command threads to stdout.
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
 * @version <code>$Id: Lsthreads.java,v 1.2 2009/02/22 00:53:24 rassy Exp $</code>
 */

public class Lsthreads extends AbstractCommand
{
  /**
   * The command name (<code>"lsthreads"</code>).
   */

  public static final String COMMAND_NAME = "lsthreads";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Lists all command threads";

  /**
   * Returns the command name (<code>"lsthreads"</code>).
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
   * Main task, prints the thread list.
   */

  protected void lsthreads (boolean verbose)
    throws Exception
  {
    List threadInfos = Jvmd.getSharedInstance().getCommandThreadInfos();
    if ( verbose )
      {
        SimpleStringTable table = new SimpleStringTable(1+threadInfos.size(), 4);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        table.set(0, 0, "ID");
        table.set(0, 1, "PID");
        table.set(0, 2, "Created");
        table.set(0, 3, "Command line");
        Iterator iterator = threadInfos.iterator();
        int row = 0;
        while ( iterator.hasNext() )
          {
            this.checkStop();
            CommandThreadInfo threadInfo = (CommandThreadInfo)iterator.next();
            row++;
            table.set(row, 0, threadInfo.getInstanceId());
            table.set(row, 1, threadInfo.getClientPID());
            table.set(row, 2, dateFormat.format(new Date(threadInfo.getCreationTime())));
            table.set(row, 3, threadInfo.getCommandLine());
          }
        table.setColumnBorders(true);
        table.setRowBorder(0, true);
        this.out.println(table);
      }
    else
      {
        SimpleStringTable table = new SimpleStringTable(1+threadInfos.size(), 2);
        table.set(0, 0, "ID");
        table.set(0, 1, "Command");
        Iterator iterator = threadInfos.iterator();
        int row = 0;
        while ( iterator.hasNext() )
          {
            this.checkStop();
            CommandThreadInfo threadInfo = (CommandThreadInfo)iterator.next();
            row++;
            table.set(row, 0, threadInfo.getInstanceId());
            table.set(row, 1, threadInfo.getCommandName());
          }
        table.setColumnBorders(true);
        table.setRowBorder(0, true);
        this.out.println(table);
      }
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
  {
    final String[] HELP_TEXT =
      {    
        "Usage:",
        "  " + COMMAND_NAME + " [--verbose | -e]",
        "  " + COMMAND_NAME + " --help | -h | --version | -v",
        "Description:",
        "  Lists all currently running command threads. In normal mode (see below), the",
        "  following informations are displayed:",
        "    ID (thread id)",
        "    Command (command name)",
        "  in verbose mode (see below), the following informations are displayed:",
        "    ID (thread id)",
        "    PID (client process pid)",
        "    Created (time when then thread started)",
        "    Command line",
        "Options:",
        "  --verbose, -e",
        "      Turns vebose mode on. If not set, normal mode is enabled.",
        "  --help, -h",
        "      Prints this help text and exits",
        "  --version , -v",
        "      Prints version information and exits"
      };
    for (String line : HELP_TEXT)
      this.out.println(line);
  }

  /**
   * Displayes version information.
   */

  public void showVersion ()
    throws Exception
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

        final int LSTHREADS = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = LSTHREADS;
        boolean verbose = false;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--verbose", "-e") )
              verbose = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case LSTHREADS:
            this.lsthreads(verbose);
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
