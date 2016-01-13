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
import net.mumie.util.CmdlineParamHelper;
import net.mumie.util.SimpleStringTable;
import java.util.Map;
import java.util.Iterator;

/**
 * Lists all commands.
 *
 *   <h4>Usage:</h4>
 *   <pre class="code"><!-- 
     -->  lscmds
<!-- -->  lscmds --help | -h | --version | -v</pre>
 *   <h4>Description:</h4>
 *   <p>
 *     Prints the names and descriptions of all currently loaded commands to stdout.
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
 * @version <code>$Id: Lscmds.java,v 1.3 2007/07/16 10:49:30 grudzin Exp $</code>
 */

public class Lscmds extends AbstractCommand
{
  /**
   * The command name (<code>"lscmds"</code>).
   */

  public static final String COMMAND_NAME = "lscmds";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Lists all commands";

  /**
   * Returns the command name (<code>"lscmds"</code>).
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
   * Main task, prints the names and descriptions of all commands to stdout.
   */

  protected void lscmds ()
    throws Exception
  {
    Map descriptions = CommandFactory.getSharedInstance().getCommandDescriptions();
    SimpleStringTable table = new SimpleStringTable(1+descriptions.size(), 2);
    table.set(0, 0, "Command");
    table.set(0, 1, "Description");
    Iterator iterator = descriptions.entrySet().iterator();
    int row = 0;
    while ( iterator.hasNext() )
      {
        this.checkStop();
        Map.Entry entry = (Map.Entry)iterator.next();
        String name = (String)entry.getKey();
        String description = (String)entry.getValue();
        row++;
	table.set(row, 0, name);
	table.set(row, 1, description);
      }
    table.setColumnBorders(true);
    table.setRowBorder(0, true);
    this.out.println(table);
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
  {
    this.out.print
      ("Usage:\n" +
       "  lscmds\n" +
       "  lscmds --help | -h | --version | -v\n" +
       "Description:\n" +
       "  Prints the names and descriptions of all currently loaded commands to\n" +
       "  stdout.\n" +
       "Options:\n" +
       "  --help, -h\n" +
       "      Prints this help text and exists\n" +
       "  --version , -v\n" +
       "      Prints version information and exists\n");
  }

  /**
   * Displayes version information.
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

        final int LSCMDS = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = LSCMDS;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case LSCMDS:
            this.lscmds();
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
