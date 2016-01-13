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
import java.io.File;
import java.io.FileReader;
import net.mumie.japs.log.analyse.LogOutputProcessingException;

public class FileLogOutputSource extends DefaultLogOutputSource
{
  /**
   * The file from which to read the source.
   */

  protected File file = null;

  /**
   * Creates a new <code>FileLogOutputSource</code> from the specified file and gives it the
   * specified name. 
   */

  public FileLogOutputSource (File file, String name)
  {
    this.file = file;
    this.name = name;
  }

  /**
   * Creates a new <code>FileLogOutputSource</code> from the specified file. The name of the
   * log output source is set to the local name of the file (filename without path).
   * 
   */

  public FileLogOutputSource (File file)
  {
    this(file, file.getName()); 
  }

  /**
   * Creates a new <code>FileLogOutputSource</code> from the file with the specified
   * filename and gives it the specified name.
   */

  public FileLogOutputSource (String filename, String name)
  {
    this(new File(filename), name); 
  }

  /**
   * Creates a new <code>FileLogOutputSource</code> from the file with the specified
   * filename. The name of the log output source is set to the local name of the file
   * (filename without path).
   */

  public FileLogOutputSource (String filename)
  {
    this(new File(filename)); 
  }

  /**
   * Returns a {@link BufferedReader BufferedReader} from which the contents of the file can
   * be read. 
   */

  public BufferedReader getReader ()
    throws LogOutputProcessingException
  {
     try
      {
        return new BufferedReader(new FileReader(this.file));
      }
    catch (Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }
}