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

import java.io.FileOutputStream;
import net.mumie.cdk.io.CdkFile;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.cdk.workers.PreviewStatusChecker;
import java.util.List;
import java.util.ArrayList;

/**
 * Displays the preview statuses
 *
 * <h4>Usage:</h4>
 * <pre><!--
     -->  mmlsprev [ <var>FILES</var> ]
<!-- -->  mmlsprev --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Displays the statuses of the previews of documents. See below for the
 *   possible statuses. If <var>FILES</var> is not specified, the statuses for
 *   all documents in the current directory are displayed. If specified, <var>FILES</var>
 *   should be a list of master files and/or directories. For each master file, the status
 *   of the corresponding preview is displayed. For each directory, the statuses of the
 *   previews of the documents in the directory are displayed. It is allowed to
 *   include other files in <var>FILES</var>; these are silently ignored (with the exception
 *   of a preview file for which no master file exists; this leds to a status
 *   entry for the corresponding document).
 * </p>
 * <p>
 *   The output is a table with two columns: The pure name of the document, and
 *   the status. Per default, the statuses are highlighted by different colors.
 *   This can be turned off by the --no-color or -C option.
 * </p>
 * <h4>Statuses:</h4>
 * <dl>
 *   <dt><code>up-to-date</code></dt>
 *   <dd>
 *     The preview exists and is up-to-date
 *   </dd>
 *   <dt><code>outdated</code></dt>
 *   <dd>
 *     The preview exists but is outdated
 *   </dd>
 *   <dt><code>inexistent</code></dt>
 *   <dd>
 *     The preview does not exist
 *   </dd>
 *   <dt><code>unclarified  </code></dt>
 *   <dd>
 *     The preview exists, but the content or master file or both do not exist
 *   </dd>
 * </dl>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--no-color, -C</code></dt>
 *   <dd>
 *     Disables colored output
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Prints this help message and exit.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Prints version information and exit.
 *   </dd>
 * </dl>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmlsprev.java,v 1.3 2007/07/16 11:07:05 grudzin Exp $</code>
 */

public class Mmlsprev extends AbstractCommand
{
  /**
   * The command name (<code>"mmlsprev"</code>).
   */

  public static final String COMMAND_NAME = "mmlsprev";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Lists the preview files and their statuses";

  /**
   * The preview status checker of this instance.
   */

  protected PreviewStatusChecker statusChecker = new PreviewStatusChecker();

  /**
   * Returns the command name (<code>"mmlsprev"</code>).
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
   * Main task, lists the document abd their preview statuses.
   */

  protected void mmlsprev (CdkFile[] files,
                           boolean colored)

    throws Exception
  {
    this.statusChecker.setup(this.directory, this.out, this.err, false);
    this.statusChecker.printPeviewStatusTable(files, colored);
    this.statusChecker.reset();
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
      "  " + COMMAND_NAME + " [ FILES ]",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  Displays the statuses of the previews of documents. See below for the",
      "  possible statuses. If FILES is not specified, the statuses for all documents",
      "  in the current directory are displayed. If specified, FILES should be a list",
      "  of master files and/or directories. For each master file, the status of the",
      "  corresponding preview is displayed. For each directory, the statuses of the",
      "  previews of the documents in the directory are displayed. It is allowed to",
      "  include other files in FILES; these are silently ignored (with the exception",
      "  of a preview file for which no master file exists; this leds to a status",
      "  entry for the corresponding document).",
      "    The output is a table with two columns: The pure name of the document, and",
      "  the status. Per default, the statuses are highlighted by different colors.",
      "  This can be turned off by the --no-color or -C option.",
      "Statuses:",
      "  up-to-date",
      "      The preview exists and is up-to-date",
      "  outdated",
      "      The preview exists but is outdated",
      "  inexistent",
      "      The preview does not exist",
      "  unclarified  ",
      "      The preview exists, but the content or master file or both do not exist",
      "Options:",
      "  --no-color, -C",
      "      Disables colored output",
      "  --help, -h",
      "      Prints this help message and exit.",
      "  --version, -v",
      "      Prints version information and exit.",
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

        final int MMLSPREV = 0;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMLSPREV;

        List<CdkFile> fileList = new ArrayList<CdkFile>();
        boolean colored = true;

        this.expandShortOptions("");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--no-color", "-C") )
              colored = false;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() )
              fileList.add(new CdkFile(this.makeAbsoluteFile(paramHelper.getParam())));
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMLSPREV:
            CdkFile[] files =
              (fileList.size() > 0
               ? fileList.toArray(new CdkFile[fileList.size()])
               : (new CdkFile(this.directory)).listCdkFiles());
            this.mmlsprev(files, colored);
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
   * {@link PreviewStatusChecker#stop stop} method of the 
   * {@link PreviewStatusChecker JavaDocumentCreator} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.statusChecker.stop();
    super.stop();
  }
}
