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
import java.util.List;
import net.mumie.cdk.util.JavaMetatagParser;
import net.mumie.cdk.util.JavaMetatags;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

/**
 * Prints metatags from Java source files.
 *
 * <h4>Usage:</h4>
 * <pre><!--
     -->    mmjmtag <var>SRC_FILE</var> [<var>NAME</var>]
<!-- -->    mmjmtag --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   If <var>NAME</var> is specified: Prints the value(s) of the metatag <var>NAME</var> in the
 *   Java source file <var>SRC_FILE</var>. If <var>NAME</var> is not specified: Prints the
 *   values of all metatags in <var>SRC_FILE</var>.
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt>--help, -h</dt>
 *   <dd>
 *     Print this help message and exit.
 *   </dd>
 *   <dt>--version, -v</dt>
 *   <dd>
 *     Print version information and exit.
 *   </dd>
 * </dl>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmjmtag.java,v 1.4 2007/07/16 11:07:05 grudzin Exp $</code>
 */

public class Mmjmtag extends AbstractCommand
{
  /**
   * The command name (<code>"mmjmtag"</code>).
   */

  public static final String COMMAND_NAME = "mmjmtag";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Prints metatags from Java source files";

  /**
   * The metatag parser of this instance.
   */

  protected JavaMetatagParser metatagParser = new JavaMetatagParser();

  /**
   * Returns the command name (<code>"mmjmtag"</code>).
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
   * Auxiliary method; prints the metatag with the specified name from the specified
   * metatags object. If the metatag has multiple values, each value is printed in a
   * separate line. If the <code>withName</code> flag is true, the name of the metatag is
   * printed in a separate line before the value(s), and each value is indented by two
   * spaces. Otherwise, only the values are printed, without indentation.
   */

  protected void printMetatag (JavaMetatags metatags, String name, boolean withName)
    throws Exception
  {
    List<String> values = metatags.getAsList(name);
    if ( withName )  this.out.println(name + ":");
    for (String value : values)
      this.out.println(withName ? "  " + value : value);
  }

  /**
   * Main task, prints the metatag(s).
   */

  protected void mmjmtag (File sourceFile, String name)
    throws Exception
  {
    JavaMetatags metatags = this.metatagParser.parse(sourceFile);
    if ( name != null )
      this.printMetatag(metatags, name, false);
    else
      {
        String[] names = metatags.getNames();
        for (int i = 0; i < names.length; i++)
          this.printMetatag(metatags, names[i], true);
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
      "  " + COMMAND_NAME + " SRC_FILE [NAME]",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  If NAME is specified: Prints the value(s) of the metatag NAME in the Java",
      "  source file SRC_FILE. If NAME is not specified: Prints the values of all",
      "  metatags in SRC_FILE.",
      "Options:",
      "  --help, -h",
      "      Print this help message and exit.",
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
    this.out.println("$Revision: 1.4 $");
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

        final int MMJMTAG = 0;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMJMTAG;

        File sourceFile = null;
        String name = null;

        this.expandShortOptions("hv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--help", "-h") )
             task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
             task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() && sourceFile == null )
              sourceFile = this.makeAbsoluteFile(paramHelper.getParam());
            else if ( paramHelper.checkArgument() && name == null )
              name = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMJMTAG:
            if ( sourceFile == null )
              throw new IllegalArgumentException("No source file specified");
            this.mmjmtag(sourceFile, name);
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
