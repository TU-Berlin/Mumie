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

package net.mumie.servercheck;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import net.mumie.japs.client.NonInteractiveJapsClient;
import net.mumie.util.ErrorUtil;
import net.mumie.util.io.IOUtil;
import net.mumie.util.logging.SimpleLogger;

public class ConnectionMonitor
{
  /**
   * 
   */

  public static void main (String[] params)
    throws Exception
  {
    if ( params.length != 9 )
      throw new IllegalArgumentException
        ("Invalid number of parameters: expected 9, found " + params.length);

    String urlPrefix = params[0];
    String path = params[1];
    String account = params[2];
    String password = params[3];
    int interval = Integer.parseInt(params[4]) * 1000;
    String logFilename = params[5];
    int maxLogFiles = Integer.parseInt(params[6]);
    int maxLogRecords = Integer.parseInt(params[7]);
    String datePattern = params[8];

    NonInteractiveJapsClient japsClient = new NonInteractiveJapsClient(urlPrefix, System.out);

    SimpleLogger logger = new SimpleLogger(logFilename, maxLogFiles, maxLogRecords, datePattern);

    japsClient.setAccount(account);
    japsClient.setPassword(password.toCharArray());

    while ( true )
      {
        try
          {
            long time = System.currentTimeMillis();
            HttpURLConnection connection = japsClient.get(path);
            long length = IOUtil.discardAndGetLength(connection.getInputStream());
            time = System.currentTimeMillis() - time;
            japsClient.logout();
            logger.log("OK: " + length + " bytes, " + time + " ms");
          }
        catch (Throwable throwable)
          {
            logger.log("ERROR: " + ErrorUtil.unwrapThrowable(throwable));
          }
        Thread.currentThread().sleep(interval);
      }
  }
}
