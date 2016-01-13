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

import java.lang.reflect.Method;
import net.mumie.cdk.CdkConfigParam;
import net.mumie.cocoon.util.MD5PasswordEncryptor;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.util.CmdlineParamHelper;

/**
 * Encrypts passwords.
 *
 * <h4>Usage:</h4>
 * <pre><!--
     -->  mmencrypt <var>PASSWORD</var> [ --no-newline | -n ]
<!-- -->  mmencrypt --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Encrypts the specified password and writes the result to stdout. The
 *   encryptor can be set by the system property
 *   <pre>  "net.mumie.cdk.passwordEncryptor"</pre>
 *   Its value must be the fully qualified name of the encryptor class. If the
 *   property is not set, a default is used, which is
 *   <pre>  "net.mumie.cocoon.util.MD5PasswordEncryptor"</pre>
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--no-newline, -n</code></dt>
 *   <dd>
 *     Suppresses the trailing newline in the output.
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
 * @version <code>$Id: Mmencrypt.java,v 1.5 2008/06/04 09:44:06 rassy Exp $</code>
 */

public class Mmencrypt extends AbstractCommand
{
  /**
   * The command name (<code>"mmencrypt"</code>).
   */

  public static final String COMMAND_NAME = "mmencrypt";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Encrypts passwords";

  /**
   * Class name of the default encryptor.
   */

  public static final String DEFAULT_ENCRYPTOR = MD5PasswordEncryptor.class.getName() ;

  /**
   * The password encryptor.
   */

  protected PasswordEncryptor encryptor = null;;

  /**
   * Returns the command name (<code>"mmencrypt"</code>).
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
   * Main task, encrypts the password.
   */

  protected void mmencrypt (String password, boolean newline)

    throws Exception
  {
    if ( this.encryptor == null )
      {
        String encryptorClassName =
          System.getProperty(CdkConfigParam.PASSWORD_ENCRYPTOR, DEFAULT_ENCRYPTOR);
        Class encryptorClass = Class.forName(encryptorClassName);
        Method createInstanceMethod =
          encryptorClass.getMethod("createOfflineInstance", new Class[0]);
        this.encryptor = (PasswordEncryptor)createInstanceMethod.invoke(null, new Object[0]);
      }
    this.out.print(this.encryptor.encrypt(password));
    if ( newline ) this.out.println();
  }

  /**
   * Prints the encryptor class to stdout.
   */

  protected void showEncryptorClass ()
  {
    this.out.println(System.getProperty(CdkConfigParam.PASSWORD_ENCRYPTOR, DEFAULT_ENCRYPTOR));
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
      "  " + COMMAND_NAME + " PASSWORD [ --no-newline | -n ]",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  Encrypts the specified password and writes the result to stdout. The",
      "  encryptor can be set by the system property",
      "    \"" + CdkConfigParam.PASSWORD_ENCRYPTOR + "\".",
      "  Its value must be the fully qualified name of the encryptor class. If the",
      "  property is not set, a default is used, which is",
      "    \"" + DEFAULT_ENCRYPTOR + "\".",
      "Options:",
      "  --no-newline, -n",
      "      Suppresses the trailing newline in the output.",
      "  --encryptor-class, -c",
      "      Prints the encryptor class and exits.",
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
    this.out.println("$Revision: 1.5 $");
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

        final int MMENCRYPT = 0;
        final int SHOW_ENCRYPTOR_CLASS = 1;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMENCRYPT;

        String password = null;
        boolean newline = true;

        this.expandShortOptions("nhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            if ( paramHelper.checkParam("--no-newline", "-n") )
              newline = false;
            else if ( paramHelper.checkParam("--encryptor-class", "-c") )
              task = SHOW_ENCRYPTOR_CLASS;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( password == null )
              password = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMENCRYPT:
            if ( password == null )
              throw new IllegalArgumentException("No password specified");
            this.mmencrypt(password, newline);
            break;
          case SHOW_ENCRYPTOR_CLASS:
            this.showEncryptorClass();
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
