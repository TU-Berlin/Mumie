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
import net.mumie.cdk.workers.SectionMasterFileChecker;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

public class Mmcksecmaster extends AbstractCommand
{
  /**
   * The command name (<code>"mmcksecmaster"</code>).
   */

  public static final String COMMAND_NAME = "mmcksecmaster";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Checks existence of section master files";

  /**
   * The reference checker of this instance.
   */

  protected SectionMasterFileChecker checker = new SectionMasterFileChecker();

  /**
   * Returns the command name (<code>"mmcksecmaster"</code>).
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

  protected int mmcksecmaster (List<CdkFile> dirList,
															 boolean recursive,
															 boolean quiet)
    throws Exception
  {
    this.checker.setup(this.directory, this.out, this.err, quiet);
    CdkFile[] dirs = dirList.toArray(new CdkFile[dirList.size()]);
    int warnCount = 0;
    try
      {
        warnCount = this.checker.checkSectionMasterFiles(dirs, recursive);
      }
    catch (CdkWorkerStoppedException exception)
      {
        throw new CommandStoppedException(exception);
      }
    finally
      {
        this.checker.reset();
      }
    return (warnCount == 0 ? 0 : 11);
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    final String[] HELP_TEXT =
    {    
			"TODO",
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
    this.out.println("$Revision: 1.1 $");
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

        final int MMCKSECMASTER = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMCKSECMASTER;

        List<CdkFile> dirs = new ArrayList<CdkFile>();
        boolean recursive = false;
        boolean quiet = false;

        this.expandShortOptions("rqhv");
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
              dirs.add(new CdkFile(this.makeAbsoluteFile(paramHelper.getParam())));
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        int exitValue = 0;
        switch ( task )
          {
          case MMCKSECMASTER:
            if ( dirs.size() == 0 )
              dirs.add(new CdkFile(this.makeAbsoluteFile(this.directory)));
            exitValue = this.mmcksecmaster(dirs, recursive, quiet);
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

  /**
   * Stops (or tries to stop) the execution of this command. Calls the
   * {@link SectionMasterFileChecker#stop stop} method of the 
   * {@link SectionMasterFileChecker SectionMasterFileChecker} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.checker.stop();
    super.stop();
  }
}
