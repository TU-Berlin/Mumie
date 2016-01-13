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

import net.mumie.cdk.CdkHelper;
import net.mumie.japs.client.JapsClient;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import java.util.List;
import net.mumie.util.SimpleStringTable;
import net.mumie.cdk.util.CdkJapsClient;
import java.util.Collections;

/**
 * Sets an alias for a Mumie server account
 *
 * <h4>Usage:</h4>
 * <pre><!--
     -->  mmalias <var>ALIAS</var> <var>URL_PREFIX</var> <var>ACCOUNT</var>
<!-- -->  mmalias --list | -l
<!-- -->  mmalias --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Sets an alias for a Mumie server account. <var>ALIAS</var> is the alias,
 *   <var>URL_PREFIX</var> the URL prefix of the server, and <var>ACCOUNT</var> the account
 *   (login name) on the server. With the <code>--list</code> or <code>-l</code> option, lists
 *   all currently defined aliases. 
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--list, -l</code></dt>
 *   <dd>
 *     Lists all currently defined aliases. The output is a table comprising the
 *     aliases, URL prefixes and accounts.
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Prints this help text to stdout and exit.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Prints version information to stdout and exit.
 *   </dd>
 * </dl>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmalias.java,v 1.6 2007/12/14 17:00:24 rassy Exp $</code>
 */

public class Mmalias extends AbstractCommand
{
  /**
   * The command name (<code>"mmalias"</code>).
   */

  public static final String COMMAND_NAME = "mmalias";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Defines a Mumie server account alias";

  /**
   * Returns the command name (<code>"mmalias"</code>).
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
   * Main task, defines the alias.
   */

  protected void mmalias (String alias, String urlPrefix, String account)
    throws Exception
  {
    CdkHelper.getSharedInstance().createJapsClient(alias, urlPrefix, account);
  }

  /**
   * Lists all currently defined aliases.
   */

  protected void list ()
    throws Exception
  {
    // Column numbers:
    final int ALIAS = 0;
    final int URL_PREFIX = 1;
    final int ACCOUNT = 2;
    final int PASSWORD_SET = 3;

    // Get CDK Helper:
    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    // Get and sort aliases:
    List<String> aliases = cdkHelper.getAccountAliases();
    Collections.sort(aliases);

    // Initialize table:
    SimpleStringTable table = new SimpleStringTable(aliases.size()+1, 4);
    int row = 0;

    // Table headings:
    table.set(row, ALIAS, "Alias");
    table.set(row, URL_PREFIX, "URL prefix");
    table.set(row, ACCOUNT, "Account");
    table.set(row, PASSWORD_SET, "Passw. stored");

    // Table body:
    for (String alias : aliases)
      {
        row++;
        CdkJapsClient japsClient = cdkHelper.getJapsClient(alias);
        table.set(row, ALIAS, alias);
        table.set(row, URL_PREFIX, japsClient.getUrlPrefix());
        table.set(row, ACCOUNT, japsClient.getAccount());
        table.set(row, PASSWORD_SET, japsClient.passwordSet());
      }

    // Table borders:
    table.setColumnBorders(true);
    table.setRowBorder(0, true);

    // Print table:
    this.out.println(table);
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
      "  mmalias ALIAS URL_PREFIX ACCOUNT",
      "  mmalias --list | -l",
      "  mmalias --help | -h | --version | -v",
      "Description:",
      "  Sets an alias for a Mumie server account. ALIAS is the alias, URL_PREFIX the",
      "  URL prefix of the server, and ACCOUNT the account (login name) on the server.",
      "  With the --list or -l option, lists all currently defined aliases.",
      "Options:",
      "  --list, -l",
      "      Lists all currently defined aliases. The output is a table comprising the",
      "      aliases, URL prefixes and accounts.",
      "  --help, -h",
      "      Prints this help text to stdout and exit.",
      "  --version, -v",
      "      Prints version information to stdout and exit.",
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

        final int MMALIAS = 0;
        final int LIST = 1;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMALIAS;

        String alias = null;
        String urlPrefix = null;
        String account = null;

        this.expandShortOptions("lhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--list", "-l") )
              task = LIST;
            else if ( paramHelper.checkParam("--help", "-h") )
             task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
             task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() && alias == null )
              alias = paramHelper.getParam();
            else if ( paramHelper.checkArgument() && urlPrefix == null )
              urlPrefix = paramHelper.getParam();
            else if ( paramHelper.checkArgument() && account == null )
              account = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMALIAS:
            if ( alias == null )
              throw new IllegalArgumentException("No alias specified");
            if ( urlPrefix == null )
              throw new IllegalArgumentException("No url prefix specified");
            if ( account == null )
              throw new IllegalArgumentException("No account specified");
            this.mmalias(alias, urlPrefix, account);
            break;
          case LIST:
            this.list();
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
