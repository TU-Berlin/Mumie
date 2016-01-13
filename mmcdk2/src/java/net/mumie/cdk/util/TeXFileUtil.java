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

package net.mumie.cdk.util;

import java.util.regex.Pattern;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class TeXFileUtil
{
  /**
   * 
   */

  protected static Pattern documentClassPattern = null;

  /**
   * 
   */

  public static String getDocumentClass (File file)
    throws FileNotFoundException, IOException 
  {
    if ( documentClassPattern == null )
      documentClassPattern = 
        Pattern.compile("^\\s*\\\\documentclass\\s*(?:\\[.*?\\])?\\s*\\{(.*?)\\}");
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String documentClass = null;
    String line;
    while ( documentClass == null && (line = reader.readLine()) != null )
      {
        Matcher matcher = documentClassPattern.matcher(line);
        if ( matcher.find() )
          documentClass = matcher.group(1);
      }
    return documentClass;
  }

}
