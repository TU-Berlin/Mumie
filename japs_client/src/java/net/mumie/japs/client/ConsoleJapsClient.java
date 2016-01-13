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

package net.mumie.japs.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ConsoleJapsClient extends LoggingCookieEnabledJapsClient
{

  /**
   * Returns account and password as a {@link LoginDialogResult LoginDialogResult}.
   */

  public LoginDialogResult performLoginDialog (boolean afterFailure)
    throws JapsClientException
  {
    try
      {
        return new ConsoleLoginDialog(afterFailure).getResult();
      }
    catch (Exception exception)
      {
        throw new JapsClientException(exception);
      }
  }

  /**
   * Creates a new <code>ConsoleJapsClient</code> that writes its log messages
   * to the stream <code>logStream</code>.
   */

  public ConsoleJapsClient (String urlPrefix, PrintStream logStream)
  {
    super(urlPrefix, logStream);
  }

  /**
   * Creates a new <code>ConsoleJapsClient</code> that writes its log messages
   * to the file <code>logFile</code>.
   */

  public ConsoleJapsClient (String urlPrefix, File logFile)
    throws FileNotFoundException 
  {
    super(urlPrefix, logFile);
  }

  /**
   * Creates a new <code>ConsoleJapsClient</code> that writes its log messages
   * to the file with the name <code>logFilename</code>.
   */

  public ConsoleJapsClient (String urlPrefix, String logFilename)
    throws FileNotFoundException 
  {
    super(urlPrefix, logFilename);
  }
}
