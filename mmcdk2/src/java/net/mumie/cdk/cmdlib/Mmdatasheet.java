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

package net.mumie.cdk.cmdlib;

import java.util.ArrayList;
import java.util.List;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cdk.workers.CdkWorkerStoppedException;
import net.mumie.cdk.workers.DataSheetCreator;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

public class Mmdatasheet extends AbstractCommand
{
  /**
   * The command name (<code>"mmdatasheet"</code>).
   */

  public static final String COMMAND_NAME = "mmdatasheet";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Creates datasheets";

  /**
   * The datasheet creator of this instance.
   */

  protected DataSheetCreator datasheetCreator = null;

  /**
   * Returns the command name (<code>"mmdatasheet"</code>).
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
   * Main task, creates the datasheets.
   */

  protected void mmdatasheet (List<CdkFile> fileList,
                              boolean recursive,
                              boolean quiet)
    throws Exception
  {
    CdkFile[] files = fileList.toArray(new CdkFile[fileList.size()]);
    if ( this.datasheetCreator == null )
      this.datasheetCreator = new DataSheetCreator();
    this.datasheetCreator.setup(this.directory, this.out, this.err, quiet);
    try
      {
        this.datasheetCreator.createDataSheets
          (files, recursive);
      }
    catch (CdkWorkerStoppedException exception)
      {
        throw new CommandStoppedException(exception);
      }
    this.datasheetCreator.reset();
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    final String[] HELP_TEXT =
    {
      "Usage:",
       "  " + COMMAND_NAME + " [OPTIONS] FILES",
       "Description:",
       "  Creates the datasheets of FILES, a list of master files and/or directories.",
       "  For each master file, the datasheet is created. For each directory, the",
       "  datasheet of each master file therein is created. If the recursive flag (see",
       "  --recursive and -r below) is set, subdirectories are processed recursively.",
       "Options:",
       "  --recursive, -r",
       "      Process subdirectories recursively",
       "  --quiet, -q",
       "      Suppress status messages to stdout.",
       "  --help, -h",
       "      Print this message and exit.",
       "  --version, -v",
       "      Print version information and exit."
    };
    for (String line : HELP_TEXT)
      this.out.println(line);
  }

  /**
   * Prints version information to the shell output.
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

        final int MMDATASHEET = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMDATASHEET;

        List<CdkFile> files = new ArrayList<CdkFile>();
        boolean recursive = false;
        boolean quiet = false;

        this.expandShortOptions("fctrqhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--recursive", "-r") )
              recursive = true;
            else if ( paramHelper.checkParam("--quiet", "-q") )
              quiet = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() )
              files.add(new CdkFile(this.makeAbsoluteFile(paramHelper.getParam())));
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMDATASHEET:
            if ( files.size() == 0 )
              files.add(new CdkFile(this.makeAbsoluteFile(this.directory)));
            this.mmdatasheet(files, recursive, quiet);
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

  /**
   * Stops (or tries to stop) the execution of this command. Calls the
   * {@link DataSheetCreator#stop stop} method of the 
   * {@link DataSheetCreator DataSheetCreator} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.datasheetCreator.stop();
    super.stop();
  }
}
