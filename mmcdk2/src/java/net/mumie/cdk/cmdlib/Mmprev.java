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
import net.mumie.cdk.workers.PreviewCreator;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

public class Mmprev extends AbstractCommand
{
  /**
   * The command name (<code>"mmprev"</code>).
   */

  public static final String COMMAND_NAME = "mmprev";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Creates previews";

  /**
   * The preview creator of this instance.
   */

  protected PreviewCreator previewCreator = new PreviewCreator();

  /**
   * Returns the command name (<code>"mmprev"</code>).
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
   * Main task, creates the previews.
   */

  protected void mmprev (List<CdkFile> fileList,
                         boolean force,
                         boolean deleteTmp,
                         boolean onlyTmp,
                         boolean recursive,
                         boolean quiet)
    throws Exception
  {
    CdkFile[] files = fileList.toArray(new CdkFile[fileList.size()]);
    this.previewCreator.setup(this.directory, this.out, this.err, quiet);
    try
      {
        this.previewCreator.createPreviews
          (files, force, deleteTmp, onlyTmp, recursive);
      }
    catch (CdkWorkerStoppedException exception)
      {
        throw new CommandStoppedException(exception);
      }
    this.previewCreator.reset();
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
       "  Creates the previews of FILES, a list of master files and/or directories.",
       "  For each master file, the preview is created. For each directory, the",
       "  preview of each master file therein is created. If the recursive flag (see",
       "  --recursive and -r below) is set, subdirectories are processed recursively.",
       "Options:",
       "  --force, -f",
       "      Create previews even if they are up-to date",
       "  --recursive, -r",
       "      Process subdirectories recursively",
       "  --clear-tmp, -c",
       "      Delete temporary XML files after preview creation",
       "  --only-tmp, -t",
       "      Create only the temporary XML files, not the previews.",
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
    this.out.println("$Revision: 1.8 $");
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

        final int MMPREV = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMPREV;

        List<CdkFile> files = new ArrayList<CdkFile>();
        boolean force = false;
        boolean deleteTmp = false;
        boolean onlyTmp = false;
        boolean recursive = false;
        boolean quiet = false;

        this.expandShortOptions("fctrqhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--force", "-f") )
              force = true;
            else if ( paramHelper.checkParam("--clear-tmp", "-c") )
              deleteTmp = true;
            else if ( paramHelper.checkParam("--only-tmp", "-t") )
              onlyTmp = true;
            else if ( paramHelper.checkParam("--recursive", "-r") )
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
          case MMPREV:
            if ( files.size() == 0 )
              files.add(new CdkFile(this.makeAbsoluteFile(this.directory)));
            this.mmprev(files, force, deleteTmp, onlyTmp, recursive, quiet);
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
   * {@link PreviewCreator#stop stop} method of the 
   * {@link PreviewCreator PreviewCreator} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.previewCreator.stop();
    super.stop();
  }
}
