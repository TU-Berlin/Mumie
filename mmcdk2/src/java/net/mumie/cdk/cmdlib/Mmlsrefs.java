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

import java.util.List;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.cdk.io.CdkFile;
import java.util.ArrayList;

/**
 * Lists references of a (pseudo-)document.
 *
 * <h4>Usage:</h4>
 * <pre><!--
     -->  mmlsrefs [<var>OPTIONS</var>] <var>MASTER_FILE</var>
<!-- -->  mmlsrefs  --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Lists all (pseudo-)documents referenced directly or indirectly by
 *   <var>MASTER_FILE</var>. Indirect means the (pseudo-)document is not refernced by
 *   <var>MASTER_FILE</var> itself, but by a (pseudo-)document referenced by
 *   <var>MASTER_FILE</var>, or a (pseudo-)document referenced by a (pseudo-)document
 *   referenced by <var>MASTER_FILE</var>, etc.
 * </p>
 * <p>
 *   By default, the output is a list of master file paths relativ to the
 *   checkin root. If the <code>--abs-filenames</code> or <code>-a</code> option is set,
 *   absolute filenames are printed instead.
 * </p>
 * <p>
 *   If the 'resolve-generic' flag is turned on (see options below), real
 *   documents implementing referenced generic documents are listed, too,
 *   including their references.
 * </p>
 * <p>
 *   If the 'suppress-generic' flag is turned on (see options below), generic
 *   documents are suppressed in the output (but the corresponding real documents
 *   and their references are listet anyway if the 'resolve-generic' flag is
 *   turned on). 
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--resolve-generic, -e</code></dt>
 *   <dd>
 *     Turn the 'resolve-generic' flag on (see above).
 *   </dd>
 *   <dt><code>--suppress-generic, -G</code></dt>
 *   <dd>
 *     Turn the 'suppress-generic' flag on (see above).
 *   </dd>
 *   <dt><code>--abs-filenames, -a</code></dt>
 *   <dd>
 *     Output absolute filenames instead of paths relativ to the checkin root.
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Print this help message and exit.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Print version information and exit.
 *   </dd>
 * </dl>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmlsrefs.java,v 1.3 2007/07/16 11:07:05 grudzin Exp $</code>
 */

public class Mmlsrefs extends AbstractCommand
{
  /**
   * The command name (<code>"mmlsrefs"</code>).
   */

  public static final String COMMAND_NAME = "mmlsrefs";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Lists all (pseudo-)documents referenced by a (pseudo-)document";

  /**
   * Returns the command name (<code>"mmlsrefs"</code>).
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
   * Finds all references in the specified master file and adds them to the specified
   * list. If <code>resolveGeneric</code> is true, real documents implementing generic ones
   * are added, too, including their references. This method works recursively, i.e., it
   * applies itself to the references found.
   */

  protected void scanRefs (CdkFile masterFile,
                           boolean resolveGeneric,
                           boolean suppressGeneric,
                           List<CdkFile> refs)
    throws Exception
  {
    for (String path : masterFile.getMaster().getAllRefPaths())
      {
        this.checkStop();
        CdkFile file = new CdkFile(path);
        if ( !refs.contains(file) )
          {
            if ( file.isMasterOfGeneric() )
              {
                if ( !suppressGeneric )
                  refs.add(file);
                if ( resolveGeneric )
                  {
                    CdkFile realFile = file.getMasterFileOfReal();
                    if ( ! refs.contains(realFile) )
                      {
                        refs.add(realFile);
                        this.scanRefs(realFile, resolveGeneric, suppressGeneric, refs);
                      }
                  }
              }
            else
              {
                refs.add(file);
                this.scanRefs(file, resolveGeneric, suppressGeneric, refs);
              }
          }
      }
  }

  /**
   * Main task, lsits the references.
   */

  protected void mmlsrefs (CdkFile masterFile,
                           boolean resolveGeneric,
                           boolean suppressGeneric,
                           boolean abs)
    throws Exception
  {
    List<CdkFile> refs = new ArrayList<CdkFile>();
    this.scanRefs(masterFile, resolveGeneric, suppressGeneric, refs);
    for (CdkFile file : refs)
      {
        this.checkStop();
        this.out.println(abs ? file.getAbsolutePath() : file.getCdkPath());
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
      "  " + COMMAND_NAME + " [OPTIONS] MASTER_FILE",
      "  " + COMMAND_NAME + "  --help | -h | --version | -v",
      "Description:",
      "  Lists all (pseudo-)documents referenced directly or indirectly by",
      "  MASTER_FILE. Indirect means the (pseudo-)document is not refernced by",
      "  MASTER_FILE itself, but by a (pseudo-)document referenced by MASTER_FILE, or",
      "  a (pseudo-)document referenced by a (pseudo-)document referenced by",
      "  MASTER_FILE, etc.",
      "    By default, the output is a list of master file paths relativ to the",
      "  checkin root. If the --abs-filenames or -a option is set, absolute filenames",
      "  are printed instead.",
      "    If the 'resolve-generic' flag is turned on (see options below), real",
      "  documents implementing referenced generic documents are listed, too,",
      "  including their references.",
      "    If the 'suppress-generic' flag is turned on (see options below), generic",
      "  documents are suppressed in the output (but the corresponding real documents",
      "  and their references are listet anyway if the 'resolve-generic' flag is",
      "  turned on). ",
      "Options:",
      "  --resolve-generic, -e",
      "      Turn the 'resolve-generic' flag on (see above).",
      "  --suppress-generic, -G",
      "      Turn the 'suppress-generic' flag on (see above).",
      "  --abs-filenames, -a",
      "      Output absolute filenames instead of paths relativ to the checkin root.",
      "  --help, -h",
      "      Print this help message and exit.",
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

        final int MMLSREFS = 0;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMLSREFS;

        CdkFile masterFile = null;
        boolean resolveGeneric = false;
        boolean suppressGeneric = false;
        boolean abs = false;

        this.expandShortOptions("eGahv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--resolve-generic", "-e") )
              resolveGeneric = true;
            else if ( paramHelper.checkParam("--suppress-generic", "-G") )
              suppressGeneric = true;
            else if ( paramHelper.checkParam("--abs-filenames", "-a") )
              abs = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() && masterFile == null )
              masterFile = new CdkFile(this.makeAbsoluteFile(paramHelper.getParam()));
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMLSREFS:
            if ( masterFile == null )
              throw new IllegalArgumentException("No master file specified");
            this.mmlsrefs(masterFile, resolveGeneric, suppressGeneric, abs);
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
