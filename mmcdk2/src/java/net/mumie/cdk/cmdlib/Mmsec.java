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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cdk.util.GenericDocument;
import net.mumie.cdk.util.Section;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.util.io.IOUtil;

/**
 * Creates a new section.
 *
 * <h4>Usage:</h4>
 * <pre><!-- 
 -->  mmsec <var>PURE_NAME</var> {--name=<var>NAME</var> | -n <var>NAME</var>} {--description=<var>DESCR</var> | -d <var>DESCR</var>}
<!-- -->  mmsec --help | -h | --version | -v
 * </pre>
 * <h4>Description:</h4>
 * <p>
 *   Creates a new section locally. This is what happens: The directory of the new
 *   section is created, the master file is created, and, if necessary a ".dir"
 *   file is created. <var>PURE_NAME</var>, <var>NAME</var>, and <var>DESCR</var> are the pure name, name, and
 *   description of the new section (thus, <var>PURE_NAME</var> is also the name of the
 *   directory of the section).
 * </p>
 * <p>
 *   Note that the new section is not automaticaly checked-in to the server.
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--name=<var>NAME</var>, -n <var>NAME</var></code></dt>
 *   <dd>
 *     Sets the name of the new section (metainfo)
 *   </dd>
 *   <dt><code>--description=<var>DESCR</var>, -n <var>DESCR</var></code></dt>
 *   <dd>
 *     Sets the description of the new section (metainfo)
 *   </dd>
 *   <dt><code>--quiet, -q</code></dt>
 *   <dd>
 *     Suppresses messages to stdout
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Prints a help text to stdout and exits
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Prints version information to stdout and exits
 *   </dd>
 * </dl>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmsec.java,v 1.2 2007/08/20 15:26:58 rassy Exp $</code>
 */

public class Mmsec extends AbstractCommand
{
  /**
   * The command name (<code>"mmsec"</code>).
   */

  public static final String COMMAND_NAME = "mmsec";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Creates a section";

  /**
   * Returns the command name (<code>"mmsec"</code>).
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
   * Whether messages are suppressed.
   */

  protected boolean quiet = false;

  /**
   * Prints a message to stdout provided {@link #quiet quiet} is false.
   */

  protected void msg (String message)
  {
    if ( !this.quiet )
      this.out.println(message);
  }

  /**
   * Main task, creates the generic document.
   */

  protected void mmsec (String pureName,
                        String name,
                        String description)

    throws Exception
  {
    final String DOT_DIR = ".dir";

    File dir = new File(this.directory, pureName);

    // Create directory:
    IOUtil.createDir(dir, true);
    this.msg("Created directory " + pureName);

    // Create master file:
    File masterFile = new File(dir, "." + CdkHelper.MASTER_SUFFIX);
    Section section = new Section(name, description);
    section.printMaster(masterFile);
    this.msg("Created " + pureName + File.separator + "." + CdkHelper.MASTER_SUFFIX);

    File parentDotDirFile = new File(this.directory, ".dir");
    if ( parentDotDirFile.exists() )
      {
        String path = IOUtil.readFile(parentDotDirFile).trim() + File.separator + pureName;
        File dotDirFile = new File(dir, DOT_DIR);
        IOUtil.writeFile(dotDirFile, path);
        this.msg("Created " + pureName + File.separator + DOT_DIR);
      }
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
      "  " + COMMAND_NAME + " PURE_NAME {--name=NAME | -n NAME} {--description=DESCR | -d DESCR}",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  Creates a new section locally. This is what happens: The directory of the new",
      "  section is created, the master file is created, and, if necessary a \".dir\"",
      "  file is created. PURE_NAME, NAME, and DESCR are the pure name, name, and",
      "  description of the new section (thus, PURE_NAME is also the name of the",
      "  directory of the section).",
      "    Note that the new section is not automaticaly checked-in to the server.",
      "Options:",
      "  --name=NAME, -n NAME",
      "      Sets the name of the new section (metainfo)",
      "  --description=DESCR, -n DESCR",
      "      sets the description of the new section (metainfo)",
      "  --quiet, -q",
      "      Suppresses messages to stdout",
      "  --help, -h",
      "      Prints this help text to stdout and exits",
      "  --version, -v",
      "      Prints version information to stdout and exits",
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

        final int MMSEC = 0;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMSEC;

        String pureName = null;
        String name = null;
        String description = null;

        this.expandShortOptions("ndqhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkOptionWithValue("--name", "-n") )
              name = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--description", "-d") )
              description = paramHelper.getValue();
            else if ( paramHelper.checkParam("--quiet", "-q") )
              this.quiet = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() && pureName == null )
              pureName = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMSEC:
            if ( pureName == null )
              throw new IllegalArgumentException("Missing pure name");
            if ( name == null )
              throw new IllegalArgumentException("Missing name");
            if ( description == null )
              throw new IllegalArgumentException("Missing description");
            this.mmsec(pureName, name, description);
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
   * Resets this instance for later reuse. Sets {@link #quiet quiet} to false
   * and calls the superclass reset method.
   */

  public void reset ()
  {
    this.quiet = false;
    super.reset();
  }

}
