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

package net.mumie.cocoon.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil
{
  /**
   * Returns the content of the specified file as a string.
   */
  // [Originally copied from net/mumie/util/ioIOUtil in repository mmjutil]

  public static String readFile (File file)
    throws IOException
  {
    FileReader fileReader = new FileReader(file);
    char[] ioBuffer = new char[1024];
    StringBuilder content = new StringBuilder();
    int length = -1;
    while ( (length = fileReader.read(ioBuffer)) != -1 )
      content.append(ioBuffer, 0, length);
    return content.toString();
  }

  /**
   * Returns the content of the file with the specified name as a string.
   */
  // [Originally copied from net/mumie/util/ioIOUtil in repository mmjutil]

  public static String readFile (String filename)
    throws IOException
  {
    return readFile(new File(filename));
  }

  /**
   * Writes the specified string to the specified file.
   */
  // [Originally copied from net/mumie/util/ioIOUtil in repository mmjutil]

  public static void writeFile (File file, String string)
    throws IOException
  {
    FileWriter writer = new FileWriter(file);
    writer.write(string);
    writer.flush();
    writer.close();
  }

  /**
   * Writes the specified string to the file with the specified name.
   *
   * @param filename the name of the file
   * @param string the string to write
   */
  // [Originally copied from net/mumie/util/ioIOUtil in repository mmjutil]

  public static void writeFile (String filename, String string)
    throws IOException
  {
    writeFile(new File(filename), string);
  }
}