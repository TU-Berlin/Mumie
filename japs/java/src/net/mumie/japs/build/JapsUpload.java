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

package net.mumie.japs.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import net.mumie.japs.client.JapsResponseHeader;
import net.mumie.japs.client.NonInteractiveJapsClient;
import net.mumie.util.CmdlineParamHelper;

/**
 * <p>
 *   Performs a file upload to Japs. Usage from the command line:
 *   <pre>
 *     java net.mumie.japs.build.JapsUpload \
 *       --url-prefix=<var>url-prefix</var> \
 *       --path=<var>path</var> \
 *       --account=<var>account</var> \
 *       --password=<var>password</var> \
 *       --file=<var>filename</var> \
 *       [--content-type=<var>content-type</var>]
 *   </pre>
 *   where the variables have the following meaning:
 * </p>
 * <table class="genuine">
 *   <thead>
 *     <tr>
 *       <td>Variable</td>
 *       <td>Meaning</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><var>url-prefix</var></td>
 *       <td>The URL prefix of the Japs, e.g., <code>net.mumie.japs/cocoon</code></td>
 *     </tr>
 *     <tr>
 *       <td><var>path</var></td>
 *       <td>The path of the URL, i.e., the part after the URL prefix</td>
 *     </tr>
 *     <tr>
 *       <td><var>account</var></td>
 *       <td>The account (or login name) to use</td>
 *     </tr>
 *     <tr>
 *       <td><var>password</var></td>
 *       <td>The password for the account</td>
 *     </tr>
 *     <tr>
 *       <td><var>filename</var></td>
 *       <td>Name of the file to upload</td>
 *     </tr>
 *     <tr>
 *       <td><var>content-type</var></td>
 *       <td>The MIME type of the file. Set automatically if omitted</td>
 *     </tr>
 *     <tr>
 *       <td><var>output</var></td>
 *       <td>Name of the file the response should be written to. If not specified, the
 *           response goes to stdout.</td>
 *     </tr>
 *   </tbody>
 * <table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JapsUpload.java,v 1.11 2007/07/11 15:38:55 grudzin Exp $</code>
 */

public class JapsUpload
{
  public static void main (String[] params)
    throws Exception
  {
    char[] password = null;
    String account = null;
    String urlPrefix = null;
    String path = null;
    File file = null;
    String contentType = null;
    String outputFilename = null;

    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( paramHelper.checkOptionWithValue("--content-type") )
          contentType = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--password") )
          password = paramHelper.getValue().toCharArray();
        else if ( paramHelper.checkOptionWithValue("--account") )
          account = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--url-prefix") )
          urlPrefix = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--path") )
          path = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--file") )
	  file = new File(paramHelper.getValue());
        else if ( paramHelper.checkOptionWithValue("--output") )
	  outputFilename = paramHelper.getValue();
        else
          throw new IllegalArgumentException
            ("Invalid parameter: " + paramHelper.getParam());
      }

    if ( password == null )
      throw new IllegalArgumentException ("Missing password");
    if ( account == null )
      throw new IllegalArgumentException ("Missing account");
    if ( urlPrefix == null )
      throw new IllegalArgumentException ("Missing url prefix");
    if ( path == null )
      throw new IllegalArgumentException ("Missing path");
    if ( file == null )
      throw new IllegalArgumentException("Missing file to upload");

    NonInteractiveJapsClient japsClient =
      new NonInteractiveJapsClient(urlPrefix, System.out);

    japsClient.setAccount(account);
    japsClient.setPassword(password);

    HttpURLConnection connection =
      (contentType == null
       ? japsClient.post(path, file)
       : japsClient.post(path, file, contentType));

    String status = connection.getHeaderField(JapsResponseHeader.STATUS);
    boolean error = ( status != null && status.trim().equals("ERROR") );

    BufferedReader reader =
      new BufferedReader(new InputStreamReader(connection.getInputStream()));

    PrintStream out =
      (error
       ? System.err
       : (outputFilename == null
          ? System.out
          : new PrintStream(new FileOutputStream(outputFilename))));

    String line;
    while ( (line = reader.readLine()) != null )
      out.println(line);
    reader.close();

    japsClient.logout();

    if ( error )
      System.exit(1);
  }
}
