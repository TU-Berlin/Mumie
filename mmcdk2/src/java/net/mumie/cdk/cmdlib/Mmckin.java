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
import net.mumie.cdk.workers.CheckinWorker;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;

/**
 * Checks-in documents and/or pseudo-documents.
 *
 * <h4>Usage:</h4>
 * <pre><!--
 -->  mmckin [<var>OPTIONS</var>] [<var>MASTER_FILES</var>]
<!-- -->  mmckin --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Checks-in (pseudo-)documents to a Mumie server. The (pseudo-)documents are
 *   specified by their master files. The latter can be passed to the command in
 *   three ways: (1) on the command line ( <var>MASTER_FILES</var> above), (2) as a
 *   file containing a list of master files (see options <code>--input-file</code>
 *   and <code>-f</code> below), and (3) by printing them to stdin (see options
 *   <code>--input-from-stdin</code> and <code>-i</code> below).
 * </p>
 * <p>
 *   The Mumie server is specified by a server account alias. The default is
 *   "default". This can be changed by means of the option <code>--alias</code> or
 *   <code>-a</code>.
 * </p>
 * <p>
 *   The master and (if any) content files of the (pseudo-)documents are first
 *   boundled in a zip file, which is then uploaded to the server. The zip file
 *   is named "checkin.zip" and is located in
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--alias=<var>ALIAS</var>, -a <var>ALIAS</var></code></dt>
 *   <dd>
 *     Specifies the server account alias. Defaults to <code>"default"</code>.
 *   </dd>
 *   <dt><code>--keep-zip, -k</code></dt>
 *   <dd>
 *     Do not delete the zip file after the upload
 *   </dd>
 *   <dt><code>--only-zip, -z</code></dt>
 *   <dd>
 *     Create only the zip file, suppress the upload. This option implies
 *     <code>--keep-zip</code>.
 *   </dd>
 *   <dt><code>--input-file=<code>FILE</code>, -f <code>FILE</code></code></dt>
 *   <dd>
 *     Reads the (pseudo-)documents to check-in from the specified file. See
 *     below for the format of the file. This option may occur several times,
 *     in which case all files are read.
 *   </dd>
 *   <dt><code>--input-from-stdin, -i</code></dt>
 *   <dd>
 *     Reads the (pseudo-)documents to check-in from stdin. The format must be
 *     the same as for the files read with the <code>--input-file</code> or
 *     <code>-f</code> option.
 *   </dd>
 *   <dt><code>--quiet, -q</code></dt>
 *   <dd>
 *     Suppress status messages to stdout. The server response is printed
 *     anyway.
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Print this help text to stdout and exit.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Print version information to stdout and exit.
 *   </dd>
 * </dl>
 * <h4>Input file format:</h4>
 * <p>
 *   The files specified by the <code>--input-file</code> or <code>-f</code> option
 *   must contain a list of master filenames separated by whitespaces. Blank lines
 *   or lines starting with <code>'#'</code> (possible preceeded by whitespaces) are
 *   ignored.
 * </p>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmckin.java,v 1.6 2007/12/26 00:05:41 rassy Exp $</code>
 */

public class Mmckin extends AbstractCommand
{
  /**
   * The command name (<code>"mmckin"</code>).
   */

  public static final String COMMAND_NAME = "mmckin";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Checkin of contents";

  /**
   * The checkin worker of this instance.
   */

  protected CheckinWorker checkinWorker = new CheckinWorker();

  /**
   * Regular expression pattern to split filenames. Used when the (pseudo-)documents to
   * chechk-in are read from stdin.
   */

  protected Pattern sepPattern = null;

  /**
   * Returns the command name (<code>"mmckin"</code>).
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
   * Main task, does the checkin.
   */

  protected void mmckin (List<CdkFile> masterFiles,
                         String alias,
                         boolean deleteZip,
                         boolean onlyZip,
                         boolean quiet)
    throws Exception
  {
    this.checkinWorker.setup(this.directory, this.out, this.err, quiet);
    this.checkinWorker.checkin(masterFiles, alias, deleteZip, onlyZip);
    this.checkinWorker.reset();
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
        "  mmckin [OPTIONS] [MASTER_FILES]",
        "  mmckin --help | -h | --version | -v",
        "Description:",
        "  Checks-in (pseudo-)documents to a Mumie server. The (pseudo-)documents are",
        "  specified by their master files. The latter can be passed to the command in",
        "  three ways: (1) on the command line (MASTER_FILES above), (2) as a file",
        "  containing a list of master files (see options --input-file and -f below), and",
        "  (3) by printing them to stdin (see options --input-from-stdin and -i below).",
        "    The Mumie server is specified by a server account alias. The default is",
        "  \"default\". This can be changed by means of the option --alias or -a.",
        "    The master and (if any) content files of the (pseudo-)documents are first",
        "  boundled in a zip file, which is then uploaded to the server. The zip file is",
        "  named \"checkin.zip\" and is located in the current working directory. It is",
        "  deleted after the upload by default.",
        "Options:",
        "  --alias=ALIAS, -a ALIAS",
        "      Specifies the server account alias. Defaults to \"default\".",
        "  --keep-zip, -k",
        "      Do not delete the zip file after the upload",
        "  --only-zip, -z",
        "      Create only the zip file, suppress the upload. This option implies",
        "      --keep-zip.",
        "  --input-file=FILE, -f FILE",
        "      Reads the (pseudo-)documents to check-in from the specified file. See",
        "      below for the format of the file. This option may occur several times,",
        "      in which case all files are read.",
        "  --input-from-stdin, -i",
        "      Reads the (pseudo-)documents to check-in from stdin. The format must be",
        "      the same as for the files read with the --input-file or -f option.",
        "  --quiet, -q",
        "      Suppress status messages to stdout. The server response is printed",
        "      anyway.",
        "  --help, -h",
        "      Print this help text to stdout and exit.",
        "  --version, -v",
        "      Print version information to stdout and exit.",
        "Input file format:",
        "  The files specified by the --input-file or -f option must contain a list of",
        "  master filenames separated by whitespaces. Blank lines or lines starting with",
        "  '#' (possible preceeded by whitespaces) are ignored."
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
    this.out.println("$Revision: 1.6 $");
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

        final int MMCKIN = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMCKIN;

        List<CdkFile> masterFiles = new ArrayList<CdkFile>();
        List<File> inputFiles = new ArrayList<File>();
        boolean inputFromStdin = false;
        String alias = "default";
        boolean deleteZip = true;
        boolean onlyZip = false;
        boolean quiet = false;

        this.expandShortOptions("akzfiqhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkOptionWithValue("--alias", "-a") )
              alias = paramHelper.getValue();
            else if ( paramHelper.checkParam("--keep-zip", "-k") )
              deleteZip = false;
            else if ( paramHelper.checkParam("--only-zip", "-z") )
              onlyZip = true;
            else if ( paramHelper.checkOptionWithValue("--input-file", "-f") )
              inputFiles.add(this.makeAbsoluteFile(paramHelper.getParam()));
            else if ( paramHelper.checkParam("--input-from-stdin", "-i") )
              inputFromStdin = true;
            else if ( paramHelper.checkParam("--quiet", "-q") )
              quiet = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() )
              masterFiles.add(new CdkFile(this.makeAbsoluteFile(paramHelper.getParam())));
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        if ( onlyZip ) deleteZip = false;

        switch ( task )
          {
          case MMCKIN:
            for (File inputFile : inputFiles)
              this.readMasterFiles(new FileInputStream(inputFile), masterFiles);
            if ( inputFromStdin )
              this.readMasterFiles(this.in, masterFiles);
            if ( masterFiles.size() == 0 )
              throw new IllegalArgumentException("No (pseudo-)documents to check-in specified");
            this.mmckin(masterFiles, alias, deleteZip, onlyZip, quiet);
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
   * Reads a list of master files from the specified input stream.
   */

  protected void readMasterFiles (InputStream in, List<CdkFile> masterFiles)
    throws Exception
  {
    if ( this.sepPattern == null )
      this.sepPattern = Pattern.compile("\\s+");
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String line;
    while ( (line = reader.readLine()) != null )
      {
        this.checkStop();
        line = line.trim();
        if ( line.length() == 0 || line.charAt(0) == '#' ) continue;
        for (String token : this.sepPattern.split(line))
          masterFiles.add(new CdkFile(this.makeAbsoluteFile(token)));
      }
  }

  /**
   * Stops (or tries to stop) the execution of this command. Calls the
   * {@link CheckinWorker#stop stop} method of the 
   * {@link CheckinWorker CheckinWorker} and then the
   * {@link AbstractCommand#stop stop} method of the superclass.
   */

  public synchronized void stop ()
  {
    this.checkinWorker.stop();
    super.stop();
  }
}
