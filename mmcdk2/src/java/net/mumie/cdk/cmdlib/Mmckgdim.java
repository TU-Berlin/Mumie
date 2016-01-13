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
import net.mumie.cdk.workers.GenDocImplChecker;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

public class Mmckgdim extends AbstractCommand
{
  /**
   * The command name (<code>"mmckgdim"</code>).
   */

  public static final String COMMAND_NAME = "mmckgdim";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Checks generic document implementation";

  /**
   * The generic document implementation checker of this instance.
   */

  protected GenDocImplChecker genDocImplChecker = new GenDocImplChecker();

  /**
   * Returns the command name (<code>"mmckgdim"</code>).
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
   * Main task, checks the generic document implementations.
   */

  protected int mmckgdim (List<CdkFile> fileList,
                         boolean recursive,
                         boolean quiet)
    throws Exception
  {
    this.genDocImplChecker.setup(this.directory, this.out, this.err, quiet);
    CdkFile[] files = fileList.toArray(new CdkFile[fileList.size()]);
    int warnCount = 0;
    try
      {
        warnCount = this.genDocImplChecker.checkGenDocImpl(files, recursive);
      }
    catch (CdkWorkerStoppedException exception)
      {
        throw new CommandStoppedException(exception);
      }
    finally
      {
        this.genDocImplChecker.reset();
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
      "  Checks the implementation of the generic documents specified by FILES, a list",
      "  of generic document master files and/or directories. For each master file, the",
      "  implementation of the generic document it belongs to is checked. For each",
      "  directory, the implementation of each generic document therein is checked. If",
      "  the recursive flag (see --recursive and -r below) is set, subdirectories are",
      "  processed recursively.",
      "    If FILES is not specified on the command line, the current directory is",
      "  used as default.",
      "    For each generic document checked, a warning is printed in any of the",
      "  following cases:",
      "      o  No implementation in the default theme and either the default or neutral",
      "         language was found.",
      "      o  Multiple implementations in the default theme and either the default or",
      "         neutral language were found.",
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

        final int MMCKGDIM = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMCKGDIM;

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
          case MMCKGDIM:
            if ( files.size() == 0 )
              files.add(new CdkFile(this.makeAbsoluteFile(this.directory)));
            exitValue = this.mmckgdim(files, recursive, quiet);
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
   * {@link GenDocImplChecker#stop stop} method of the 
   * {@link GenDocImplChecker GenDocImplChecker} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.genDocImplChecker.stop();
    super.stop();
  }
}
