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

package net.mumie.japs.log.analyse.serialize;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import net.mumie.japs.log.analyse.LogOutputHandler;
import net.mumie.japs.log.analyse.LogOutputProcessingException;

/**
 * A {@link LogOutputHandler LogOutputHandler} that writes the output events to a stream.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LogOutputSerializer.java,v 1.3 2006/01/13 22:00:14 rassy Exp $</code>
 */

public interface LogOutputSerializer extends LogOutputHandler
{
  /**
   * Sets the target to the specified output stream.
   */

  public void setTarget (OutputStream out)
    throws LogOutputProcessingException;

  /**
   * Sets the target to the specified print stream.
   */

  public void setTarget (PrintStream  out)
    throws LogOutputProcessingException;

  /**
   * Sets the target to the specified file.
   */

  public void setTarget (File file)
    throws LogOutputProcessingException;

  /**
   * Sets the target to the file with the specified name.
   */

  public void setTarget (String filename)
    throws LogOutputProcessingException;
}