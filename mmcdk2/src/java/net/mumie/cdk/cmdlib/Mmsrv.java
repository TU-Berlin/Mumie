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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsResponseHeader;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import java.util.Set;

/**
 * Sends a Http request to a Mumie server
 *
 * <h4>Usage:</h4>
 * <pre class="code"><!--
     -->  mmsrv [--alias= <var>ALIAS</var> | -a <var>ALIAS</var>] [<var>OPTIONS</var>] <var>PATH</var>
<!-- -->  mmsrv --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Sends a Http request to a Mumie server. The server and account are specified
 *   by <var>ALIAS</var>, the server account alias (see mmalias). If not specified,
 *   <var>ALIAS</var> is set to <code>"default"</code>. <var>PATH</var> is the
 *   requested URL relative the server's URL prefix.
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--alias=<var>ALIAS</var>, -a <var>ALIAS</var></code></dt>
 *   <dd>
 *     Specifies the server account alias. Defaults to <code>"default"</code>.
 *   </dd>
 *   <dt><code>--method=<var>METHOD</var>, -m <var>METHOD</var></code></dt>
 *   <dd>
 *     Sets the Http method to use. Must be "get" or "post"; case does not
 *     matter. Default is "get".
 *   </dd>
 *   <dt> <code> --param <var>NAME</var>=<var>VALUE</var>,
 *   -p <var>NAME</var>=<var>VALUE</var></code></dt>
 *   <dd>
 *     Sets a request parameter (use this repeatedly to specify several
 *     request parameters)
 *   </dd>
 *   <dt><code>--output=FILE, -o FILE</code></dt>
 *   <dd>
 *     Sets a file the response is written to. Per default, the response is
 *     printed to stdout.
 *   </dd>
 *   <dt><code>--discard-response, -d</code></dt>
 *   <dd>
 *     Discard the response, instead of printing it to stdout or a file
 *   </dd>
 *   <dt><code>--dump-response-headers, -u</code></dt>
 *   <dd>
 *     Print all response headers to stdout
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
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmsrv.java,v 1.7 2009/08/27 23:15:41 rassy Exp $</code>
 */

public class Mmsrv extends AbstractCommand
{
  /**
   * The command name (<code>"mmsrv"</code>).
   */

  public static final String COMMAND_NAME = "mmsrv";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Sends a Http request to a Mumie server";

  /**
   * Indicates that the Http "Get" method should be used.
   */

  protected static final int GET = 0;

  /**
   * Indicates that the Http "Post" method should be used.
   */

  protected static final int POST = 1;

  /**
   * Indicates that the Http "Head" method should be used.
   */

  protected static final int HEAD = 2;

  /**
   * Size of the i/o buffer, in bytes.
   */

  static final int IO_BUFFER_SIZE = 1024;

  /**
   * Returns the command name (<code>"mmsrv"</code>).
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
   * Main task, sends a Http request to a Mumie server.
   */

  protected int mmsrv (String alias,
                       int method,
                       String path,
                       Map<String, String> requestParams,
                       File outputFile,
                       boolean discardResponse,
                       boolean dumpResponseHeaders)
    throws Exception
  {
    // Get Japs client:
    JapsClient japsClient = CdkHelper.getSharedInstance().getJapsClient(alias);

    // Establish connection:
    HttpURLConnection connection;
    if ( method == GET )
      connection = japsClient.get(path, requestParams);
    else if ( method == POST )
      connection = japsClient.post(path, requestParams);
    else if ( method == HEAD )
      connection = japsClient.head(path, requestParams);
    else
      throw new IllegalArgumentException("Unknown method code: " + method);

    // Check success:
    if ( connection == null )
      throw new IllegalStateException("Connection is null");
    String status = connection.getHeaderField(JapsResponseHeader.STATUS);
    boolean error = ( status != null && status.trim().equals("ERROR") );

    // Print response headers if necessary:
    if ( dumpResponseHeaders ) printResponseHeaders(connection);

    // Handle response:
    InputStream in = connection.getInputStream();
    byte[] buffer = new byte[IO_BUFFER_SIZE];
    if ( discardResponse )
      while ( in.read(buffer) != -1 );
    else
      {
        OutputStream out =
          (error
           ? this.err
           : (outputFile == null
              ? this.out
              : new FileOutputStream(outputFile)));
        int length;
        while ( (length = in.read(buffer)) != -1 )
          out.write(buffer, 0, length);
        out.flush();
        out.close();
      }
    in.close();

    return (error ? 10 : 0);
  }

  /**
   * 
   */

  protected void printResponseHeaders (HttpURLConnection connection)
    throws Exception
  {
    Map<String,List<String>> headers = connection.getHeaderFields();

    // Find longest key (for pretty-printing):
    int maxLen = 0;
    for (String key : headers.keySet())
      {
        if ( key != null && key.length() > maxLen ) maxLen = key.length();
      }

    out.println("=========== Response headers ==========");
    for (Map.Entry<String,List<String>> header : headers.entrySet())
      {
        String key = header.getKey();
        if ( key == null ) key = "";
        List<String> values = header.getValue();
        out.print(key);
        for (int i = key.length(); i < maxLen; i++) out.print(" ");
        out.print(" : ");
        boolean first = true;
        for (String value : values)
          {
            if ( !first) out.print(", ");
            out.print(value);
            first = false;
          }
        out.println();
      }
    out.println("========= End response headers ========");
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
      "  " + COMMAND_NAME + " [--alias=ALIAS | -a ALIAS] [OPTIONS] PATH ",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  Sends a Http request to a Mumie server. The server and account are specified",
      "  by ALIAS, the server account alias (see mmalias). If not specified, ALIAS is",
      "  set to \"default\". PATH is the requested URL relative the server's URL prefix.",
      "Options:",
      "  --alias=ALIAS, -a ALIAS",
      "      Specifies the server account alias. Defaults to \"default\".",
      "  --method=METHOD, -m METHOD",
      "      Sets the Http method to use. Must be \"get\" or \"post\"; case does not",
      "      matter. Default is \"get\".",
      "  --param NAME=VALUE, -p NAME=VALUE",
      "      Sets a request parameter (use this repeatedly to specify several",
      "      request parameters)",
      "  --output=FILE, -o FILE",
      "      Sets a file the response is written to. Per default, the response is",
      "      printed to stdout.",
      "  --discard-response, -d",
      "      Discard the response, instead of printing it to stdout or a file",
      "  --dump-response-headers, -u",
      "      Print all response headers to stdout",
      "  --help, -h",
      "      Print this help text to stdout and exit.",
      "  --version, -v",
      "      Print version information to stdout and exit.",
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

        final int MMSRV = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMSRV;

        int method = GET;
        String alias = "default";
        String path = null;
        Map<String, String> requestParams = new HashMap<String, String>();
        File outputFile = null;
        boolean discardResponse = false;
        boolean dumpResponseHeaders = false;

        this.expandShortOptions("amopduhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkOptionWithValue("--alias", "-a") )
              alias = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--method", "-m") )
              method = toMethod(paramHelper.getValue());
            else if ( paramHelper.checkOptionWithValue("--output", "-o") )
              outputFile = this.makeAbsoluteFile(paramHelper.getValue());
            else if ( paramHelper.checkOptionWithKeyValuePair("--param", "-p") )
              paramHelper.copyKeyValuePair(requestParams);
            else if ( paramHelper.checkParam("--discard-response", "-d") )
              discardResponse = true;
            else if ( paramHelper.checkParam("--dump-response-headers", "-u") )
              dumpResponseHeaders = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() && path == null )
              path = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        int exitValue = 0;
        switch ( task )
          {
          case MMSRV:
            if ( path == null )
              throw new IllegalArgumentException("No path specified");
            exitValue = this.mmsrv
              (alias, method, path, requestParams, outputFile, discardResponse, dumpResponseHeaders);
            break;
          case SHOW_HELP:
            this.showHelp();
            break;
          case SHOW_VERSION:
            this.showVersion();
            break;
          }

        return exitValue;
      }
    catch (Exception exception)
      {
        throw new CommandExecutionException(exception);
      }
  }

  /**
   * Returns the numerical code ({@link #GET GET} or {@link #POST POST}) for the specified
   * string. The string must be either <code>"get"</code> or  <code>"post"</code>. Case does
   * not matter.
   */

  protected static int toMethod (String string)
  {
    if ( string.equalsIgnoreCase("get") )
      return GET;
    else if ( string.equalsIgnoreCase("post") )
      return POST;
    else if ( string.equalsIgnoreCase("head") )
      return HEAD;
    else
      throw new IllegalArgumentException("Unknown Http method: " + string);
  }
}
