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
import net.mumie.cdk.workers.ReferenceChecker;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

public class Mmckrefs extends AbstractCommand
{
  /**
   * The command name (<code>"mmckrefs"</code>).
   */

  public static final String COMMAND_NAME = "mmckrefs";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Checks references";

  /**
   * The reference checker of this instance.
   */

  protected ReferenceChecker referenceChecker = new ReferenceChecker();

  /**
   * Returns the command name (<code>"mmckrefs"</code>).
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

  protected int mmckrefs (List<CdkFile> fileList,
                          boolean recursive,
                          boolean quiet)
    throws Exception
  {
    this.referenceChecker.setup(this.directory, this.out, this.err, quiet);
    CdkFile[] files = fileList.toArray(new CdkFile[fileList.size()]);
    int warnCount = 0;
    try
      {
        warnCount = this.referenceChecker.checkReferences(files, recursive);
      }
    catch (CdkWorkerStoppedException exception)
      {
        throw new CommandStoppedException(exception);
      }
    finally
      {
        this.referenceChecker.reset();
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
      "Usage:",
      "  " + COMMAND_NAME + " [OPTIONS] [FILES]",
      "Description:",
      "  Checks the references of FILES, a list of master files and/or directories.",
      "  For each master file, the references are checked. For each directory, the",
      "  references of each master file therein are checked. If the recursive flag",
      "  (see --recursive and -r below) is set, subdirectories are processed",
      "  recursively.",
      "    If FILES is not specified on the command line, the current directory is",
      "  used as default.",
      "    For each file checked, a warning is printed in any of the following cases:",
      "      o  A file is referenced but does not exist.",
      "      o  A file is referenced as a component or link and the document type",
      "         declared for the component or link differs from the document type",
      "         specified in the referenced file.",
      "      o  A file is referenced as a generic document in the GDIM section but the",
      "         document type specified in the referenced file is not the generic",
      "         counterpart of the type of the referencing file.",
      "      o  A file is referenced as a generic document in the GDIM section but the",
      "         type of the referencing file has no generic counterpart.",
      "      o  A file is referenced as a corrector and the document type specified",
      "         in the referenced file is not 'java_class'.",
      "      o  A file is referenced as a summary and the document type specified",
      "         in the referenced file is not 'generic_summary'.",
      "      o  A file is referenced as a thumbail and the document type specified",
      "         in the referenced file is not 'generic_image'.",
      "      o  A file is referenced as an info page and the document type specified",
      "         in the referenced file is not 'generic_page'.",
      "      o  A component, link or GDIM reference with no path specified.",
      "Options:",
      "  --recursive, -r",
      "      Process subdirectories recursively",
      "  --quiet, -q",
      "      Suppress status messages to stdout (warnings are not suppressed).",
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

        final int MMCKREFS = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMCKREFS;

        List<CdkFile> files = new ArrayList<CdkFile>();
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
              files.add(new CdkFile(this.makeAbsoluteFile(paramHelper.getParam())));
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        int exitValue = 0;
        switch ( task )
          {
          case MMCKREFS:
            if ( files.size() == 0 )
              files.add(new CdkFile(this.makeAbsoluteFile(this.directory)));
            exitValue = this.mmckrefs(files, recursive, quiet);
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
   * {@link ReferenceChecker#stop stop} method of the 
   * {@link ReferenceChecker ReferenceChecker} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.referenceChecker.stop();
    super.stop();
  }
}
