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
import net.mumie.cdk.workers.JavaDocumentCreator;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

/**
 * Creates master and content files from Java source files.
 *
 * <h4>Usage:</h4>
 * <pre><!--
-->   mmjava [<var>OPTIONS</var>] <var>FILES</var></pre>
 * <h4>Description:</h4>
 * <p>
 *   Creates the master and content files for <var>SOURCES</var>, a list of Java source files
 *   and/or directories. For each source file, the master and content files are
 *   created. For each directory, the master and content files for each source
 *   file therein are created. If the recursive flag is turned on (see --recursive
 *   and -r) below, subdirectories are processed recursively. Works for Applets
 *   (document type "applet") and Java classes (document type "java_class").
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--force, -f</code></dt>
 *   <dd>
 *     Create master and content files even if they are up-to date
 *   </dd>
 *   <dt><code>--recursive, -r</code></dt>
 *   <dd>
 *     Process subdirectories recursively
 *   </dd>
 *   <dt><code>--keep-tmp, -k</code></dt>
 *   <dd>
 *     Do not delete the temporary directory used for compiling. This directory
 *     is created in the current directory and has the name <var>PURE_NAME</var>.tmp, where
 *     <var>PURE_NAME</var> is the pure name of the created document. It is deleted by
 *     default.
 *   </dd>
 *   <dt><code>--quiet, -q</code></dt>
 *   <dd>
 *     Suppress status messages to stdout.
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Print this message and exit.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Print version information and exit.
 *   </dd>
 * </dl>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmjava.java,v 1.7 2008/06/04 09:45:16 rassy Exp $</code>
 */

public class Mmjava extends AbstractCommand
{
  /**
   * The command name (<code>"mmjava"</code>).
   */

  public static final String COMMAND_NAME = "mmjava";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Creates master and content files from Java sources";

  /**
   * The JavaDocumentCreator of this instance.
   */

  protected JavaDocumentCreator documentCreator = new JavaDocumentCreator();

  /**
   * Returns the command name (<code>"mmjava"</code>).
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
   * Main task, creates the master and content files..
   */

  protected void mmjava (List<CdkFile> fileList,
                         boolean force,
                         boolean deleteTmp,
                         boolean recursive,
                         boolean quiet)
    throws Exception
  {
    CdkFile[] files = fileList.toArray(new CdkFile[fileList.size()]);
    this.documentCreator.setup(this.directory, this.out, this.err, quiet);
    try
      {
        this.documentCreator.createDocuments
          (files, force, deleteTmp, recursive);
      }
    catch (CdkWorkerStoppedException exception)
      {
        throw new CommandStoppedException(exception);
      }
    this.documentCreator.reset();
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
      "  mmjava [OPTIONS] FILES",
      "Description:",
      "  Creates the master and content files for SOURCES, a list of Java source files",
      "  and/or directories. For each source file, the master and content files are",
      "  created. For each directory, the master and content files for each source",
      "  file therein are created. If the recursive flag is turned on (see --recursive",
      "  and -r) below, subdirectories are processed recursively. Works for Applets",
      "  (document type \"applet\") and Java classes (document type \"java_class\").",
      "Options:",
      "  --force, -f",
      "      Create master and content files even if they are up-to date",
      "  --recursive, -r",
      "      Process subdirectories recursively",
      "  --keep-tmp, -k",
      "      Do not delete the temporary directory used for compiling. This directory",
      "      is created in the current directory and has the name PURE_NAME.tmp, where",
      "      PURE_NAME is the pure name of the created document. It is deleted by",
      "      default.",
      "  --quiet, -q",
      "      Suppress status messages to stdout.",
      "  --help, -h",
      "      Print this message and exit.",
      "  --version, -v",
      "      Print version information and exit.",
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
    this.out.println("$Revision: 1.7 $");
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

        final int MMJAVA = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMJAVA;

        List<CdkFile> files = new ArrayList<CdkFile>();
        boolean force = false;
        boolean deleteTmp = true;
        boolean recursive = false;
        boolean quiet = false;

        this.expandShortOptions("fkrqhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--force", "-f") )
              force = true;
            else if ( paramHelper.checkParam("--keep-tmp", "-k") )
              deleteTmp = false;
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
          case MMJAVA:
            if ( files.isEmpty() )
              files.add(new CdkFile(this.makeAbsoluteFile(this.directory)));
            this.mmjava(files, force, deleteTmp, recursive, quiet);
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
   * {@link JavaDocumentCreator#stop stop} method of the 
   * {@link JavaDocumentCreator JavaDocumentCreator} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.documentCreator.stop();
    super.stop();
  }
}
