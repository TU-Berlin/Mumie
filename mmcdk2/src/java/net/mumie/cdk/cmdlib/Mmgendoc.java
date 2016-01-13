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
import java.io.PrintStream;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cdk.util.GenericDocument;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

/**
 * Creates generic documents.
 *
 * <h4>Usage:</h4>
 * <pre><!--
     -->  mmgendoc <var>OPTIONS</var>
<!-- -->  mmgendoc --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Creates the XML code of the master of a generic document, and outputs it to a
 *   file or to stdout. See "Options" below for details.
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--output=<var>FILE</var>, -o <var>FILE</var></code></dt>
 *   <dd>
 *     File where the output is written to. If omitted, the output is written to
 *     stdout. If specified, <var>FILE</var> must be a master filename. If the file already
 *     exists, an error is signaled unless the <code>--force</code> or <code>-f</code> option
 *     is set (see below).
 *   </dd>
 *   <dt><code>--type=<var>TYPE</var>, -t <var>TYPE</var></code></dt>
 *   <dd>
 *     The type of the document to create. Must be the name of a generic
 *     document type. If omitted, mmgendoc tries to guess the type from the the
 *     output filename. This succeeds only if the filename starts with one of
 *     the standard type indicators, e.g., <code>"g_xsl"</code> for a generic XSL stylesheet.
 *   </dd>
 *   <dt><code>--category=<var>CATEGORY</var>, -c <var>CATEGORY</var></code></dt>
 *   <dd>
 *     The category of the document to create. Must be the name of a category.
 *     If omitted, mmgendoc tries to guess the category from the the
 *     output filename. This succeeds only if the filename starts with one of
 *     the common category indicators, e.g., <code>"thm"</code> for <code>theorem</code>.
 *   </dd>
 *   <dt><code>--name=<var>NAME</var>, -n <var>NAME</var></code></dt>
 *   <dd>
 *     The name of the document.
 *   </dd>
 *   <dt><code>--description=<var>DESCRIPTION</var>, -n <var>DESCRIPTION</var></code></dt>
 *   <dd>
 *     The description of the document.
 *   </dd>
 *   <dt><code>--width=<var>WIDTH</var>, -W <var>WIDTH</var></code></dt>
 *   <dd>
 *     The width of the document, in pixels. This option is only allowed with
 *     document types witch have width and height (e.g., generic images).
 *   </dd>
 *   <dt><code>--height=<var>HEIGHT</var>, -H <var>HEIGHT</var></code></dt>
 *   <dd>
 *     The height of the document, in pixels. This option is only allowed with
 *     document types witch have width and height (e.g., generic images).
 *   </dd>
 *   <dt><code>--force, -f</code></dt>
 *   <dd>
 *     Overwrites the output file if it exists already.
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
 * @version <code>$Id: Mmgendoc.java,v 1.6 2007/07/16 11:07:05 grudzin Exp $</code>
 */

public class Mmgendoc extends AbstractCommand
{
  /**
   * The command name (<code>"mmgendoc"</code>).
   */

  public static final String COMMAND_NAME = "mmgendoc";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Creates a generic document";

  /**
   * Returns the command name (<code>"mmgendoc"</code>).
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
   * Main task, creates the generic document.
   */

  protected void mmgendoc (CdkFile masterFile,
                           String typeName,
                           String categoryName,
                           String name,
                           String description,
                           int width,
                           int height,
                           boolean force)

    throws Exception
  {
    if ( masterFile != null && !masterFile.isMasterFile() )
      throw new IllegalArgumentException("Not a master file: " + masterFile);

    if ( masterFile != null && masterFile.exists() && !force )
      throw new IllegalStateException("File exists: " + masterFile);

    // Get type:
    int type = DocType.UNDEFINED;
    if ( typeName != null )
      {
        type = DocType.codeFor(typeName);
        if ( type == DocType.UNDEFINED )
          throw new IllegalArgumentException
            ("Unknown document type: " + typeName);
      }
    else if ( masterFile != null )
      {
        type = GenericDocument.guessType(masterFile.getPureName());
        if ( type == DocType.UNDEFINED )
          throw new IllegalArgumentException
            ("Type not specified and not deducible from filename");
      }
    else
      throw new IllegalArgumentException("Type not specified");

    // If necessary, get category:
    int category = Category.UNDEFINED;
    if ( DocType.hasCategory[type] )
      {
        if ( categoryName != null )
          {
            category = Category.codeFor(categoryName);
            if ( category == Category.UNDEFINED )
              throw new IllegalArgumentException
                ("Unknown category: " + categoryName);
          }
        else if ( masterFile != null )
          {
            category = GenericDocument.guessCategory(masterFile.getPureName());
            if ( category == Category.UNDEFINED )
              throw new IllegalArgumentException
                ("Category not specified and not deducible from filename");
          }
      }

    // Create a GenericDocument instance:
    GenericDocument document =
      new GenericDocument(type, category, name, description, width, height);

    // Get output stream:
    PrintStream out =
      (masterFile != null
       ? new PrintStream(new FileOutputStream(masterFile))
       : this.out);

    // Write master file:
    document.printMaster(out);
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
      "  " + COMMAND_NAME + " OPTIONS",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  Creates the XML code of the master of a generic document, and outputs it to a",
      "  file or to stdout. See \"Options\" below for details.",
      "Options:",
      "  --output=FILE, -o FILE",
      "      File where the output is written to. If omitted, the output is written to",
      "      stdout. If specified, FILE must be a master filename. If the file already",
      "      exists, an error is signaled unless the --force or -f option is set (see",
      "      below).",
      "  --type=TYPE, -t TYPE",
      "      The type of the document to create. Must be the name of a generic",
      "      document type. If omitted, " + COMMAND_NAME + " tries to guess the type from the the",
      "      output filename. This succeeds only if the filename starts with one of",
      "      the standard type indicators, e.g., \"g_xsl\" for a generic XSL stylesheet.",
      "  --category=CATEGORY, -c CATEGORY",
      "      The category of the document to create. Must be the name of a category.",
      "      If omitted, " + COMMAND_NAME + " tries to guess the category from the the",
      "      output filename. This succeeds only if the filename starts with one of",
      "      the common category indicators, e.g., \"thm\" for \"theorem\".",
      "  --name=NAME, -n NAME",
      "      The name of the document.",
      "  --description=DESCRIPTION, -n DESCRIPTION",
      "      The description of the document.",
      "  --width=WIDTH, -W WIDTH",
      "      The width of the document, in pixels. This option is only allowed with",
      "      document types witch have width and height (e.g., generic images).",
      "  --height=HEIGHT, -H HEIGHT",
      "      The height of the document, in pixels. This option is only allowed with",
      "      document types witch have width and height (e.g., generic images).",
      "  --force, -f",
      "      Overwrites the output file if it exists already.",
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

        final int MMGENDOC = 0;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMGENDOC;

        CdkFile masterFile = null;
        String typeName = null;
        String categoryName = null;
        String name = null;
        String description = null;
        int width = -1;
        int height = -1;
        boolean force = false;

        this.expandShortOptions("otcndWHfhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkOptionWithValue("--output", "-o") )
              masterFile = new CdkFile(this.makeAbsoluteFile(paramHelper.getValue()));
            else if ( paramHelper.checkOptionWithValue("--doctype", "-t") )
              typeName = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--category", "-c") )
              categoryName = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--name", "-n") )
              name = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--description", "-d") )
              description = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--width", "-W") )
              width = Integer.parseInt(paramHelper.getValue());
            else if ( paramHelper.checkOptionWithValue("--height", "-H") )
              height = Integer.parseInt(paramHelper.getValue());
            else if ( paramHelper.checkParam("--force", "-f") )
              force = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMGENDOC:
            this.mmgendoc
              (masterFile, typeName, categoryName, name, description, width, height, force);
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
