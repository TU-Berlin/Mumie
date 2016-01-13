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

import java.io.InputStreamReader;
import java.io.Reader;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.util.CdkJapsClient;
import net.mumie.util.PasswordBuffer;
import net.mumie.japs.client.JapsClient;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;

/**
 * Reads a password for an account and stores it.
 *
 * <h4>Usage:</h4>
 * <pre><!-- 
     -->  mmstorepass  [--alias= <var>ALIAS</var> | -a <var>ALIAS</var>]
<!-- -->  mmstorepass --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Reads the password for the server account alias <var>ALIAS</var> from stdin
 *   and stores it. <var>ALIAS</var> defaults to <code>"default"</code>.
 * </p>
 * <p>
 *   This command is not intended to be invoked directly by the user. Rather, it
 *   should be used in shell scripts which want to send passwords to the Jvmd
 *   server. The shell scripts may prompt the user for the passwords and read them
 *   in a secure way.
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--alias=<var>ALIAS</var>, -a <var>ALIAS</var></code></dt>
 *   <dd>
 *     Specifies the server account alias. Defaults to <code>"default"</code>.
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Prints this help message and exits.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Prints version information and exits.
 *   </dd>
 * </dl>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmstorepass.java,v 1.1 2007/11/01 00:26:55 rassy Exp $</code>
 */

public class Mmstorepass extends AbstractCommand
{
  /**
   * The command name (<code>"mmstorepass"</code>).
   */

  public static final String COMMAND_NAME = "mmstorepass";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Stores the password for a Mumie server account";

  /**
   * Returns the command name (<code>"mmstorepass"</code>).
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
   * Main task, sets the password.
   */

  protected void mmstorepass (String alias)
    throws Exception
  {
    // Read password:
    Reader reader = new InputStreamReader(this.in);
    PasswordBuffer passwordBuffer = new PasswordBuffer();
    int c;
    while ( true )
      {
        c = reader.read();
        if ( c == 0 || c == -1 ) break;
        passwordBuffer.append((char)c);
      }
    char[] password = passwordBuffer.toCharArray();

    // Set password:
    CdkHelper.getSharedInstance().getJapsClient(alias).setPassword(password);

    // Cleanup:
    passwordBuffer.erase();
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
        "  " + COMMAND_NAME + " [--alias=ALIAS | -a ALIAS]",
        "  " + COMMAND_NAME + " --help | -h | --version | -v",
        "Description:",
        "  Reads the password for the server account alias ALIAS from stdin and stores",
        "  it. ALIAS defaults to \"default\".",
        "    This command is not intended to be invoked directly by the user. Rather, it",
        "  should be used in shell scripts which want to send passwords to the Jvmd",
        "  server. The shell scripts may prompt the user for the passwords and read them",
        "  in a secure way.",
        "Options:",
        "  --alias=ALIAS, -a ALIAS",
        "      Specifies the server account alias. Defaults to \"default\".",
        "  --help, -h",
        "      Prints this help message and exits.",
        "  --version, -v",
        "      Prints version information and exits.",
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

        final int MMSTOREPASS = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMSTOREPASS;

        String alias = "default";

        this.expandShortOptions("hv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkOptionWithValue("--alias", "-a") )
              alias = paramHelper.getValue();
            else if ( paramHelper.checkParam("--help", "-v") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMSTOREPASS:
            this.mmstorepass(alias);
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
