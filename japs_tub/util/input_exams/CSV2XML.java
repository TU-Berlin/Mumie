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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

public class CSV2XML
{
  static final int SYNC_ID = 0;
  static final int FIRST_NAME = 1;
  static final int SURNAME = 2;

  protected static String encode (String string)
  {
    StringBuilder buffer = new StringBuilder();
    for (char c : string.toCharArray())
      {
        if ( c > 127 )
          buffer.append("&#" + (int)c + ";");
        else
          buffer.append(c);
      }
    return buffer.toString();
  }

  protected static void csv2xml (String filename)
    throws Exception
  {
    System.out.println(filename);
    if ( ! filename.endsWith(".csv") )
      throw new IllegalArgumentException("Invalid input filename: " + filename);
    String name = filename.substring(0, filename.length() - 4);
    String outputFilename = name + ".xml";
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    PrintStream out = new PrintStream(new FileOutputStream(outputFilename));
    out.println("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
    out.println("<tutorial name=\"" + name.replace('_', ' ') + "\">");
    String line;
    while ( (line = reader.readLine()) != null )
      {
        line = line.trim();
        if ( line.length() == 0 ) continue;
        String[] data = line.split(";");
        out.println
          ("  <member" +
           " sync-id=\"" + data[SYNC_ID] + "\"" +
           " first-name=\"" + encode(data[FIRST_NAME]) + "\"" +
           " surname=\"" + encode(data[SURNAME]) + "\"/>");
      }
    out.println("</tutorial>");
  }    

  public static void main (String[] params)
    throws Exception
  {
    for (String filename : params)
      csv2xml(filename);
  }
}