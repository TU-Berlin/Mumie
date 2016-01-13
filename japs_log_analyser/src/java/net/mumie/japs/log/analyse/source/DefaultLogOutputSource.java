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

package net.mumie.japs.log.analyse.source;

import java.io.BufferedReader;
import net.mumie.japs.log.analyse.LogOutputProcessingException;

/**
 * Base class for implementing {@link LogOutputSource LogOutputSource}s.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultLogOutputSource.java,v 1.3 2007/06/10 00:43:40 rassy Exp $</code>
 */

public class DefaultLogOutputSource
  implements LogOutputSource
{
  /**
   * The name of this log output source.
   */

  protected String name = null;

  /**
   * The {@link BufferedReader BufferedReader} from which the log output is read.
   */

  protected BufferedReader reader = null;

  /**
   * Returns the name of this log output source. Usually, this is the name of the file that
   * contains the log output.
   */

  public String getName ()
  {
    return this.name;
  }

  /**
   * Returns a {@link BufferedReader BufferedReader} from which the log output can be read
   * line by line.
   */

  public BufferedReader getReader ()
    throws LogOutputProcessingException
  {
    return this.reader;
  }
}
