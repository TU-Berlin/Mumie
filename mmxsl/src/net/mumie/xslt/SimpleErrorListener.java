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

package net.mumie.xslt;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;
import net.mumie.util.Util;

/**
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: SimpleErrorListener.java,v 1.4 2003/08/15 22:33:59 rassy Exp $</span>
 */

public class SimpleErrorListener implements ErrorListener
{
  public static String composeMessage (String prefix, TransformerException transformerException)
  {
    StringBuffer message = new StringBuffer(prefix);
    SourceLocator locator = transformerException.getLocator();
    message.append("\n");
    if ( locator != null )
      {
	message.append(  "System Id : ").append(locator.getSystemId()).append("\n");
	if ( locator.getPublicId() != null )
	  message.append("Public Id : ").append(locator.getPublicId()).append("\n");
	message.append(  "Line      : ")
	  .append(Util.getNotation(locator.getLineNumber(), "-unknown-")).append("\n");
	message.append(  "Column    : ")
	  .append(Util.getNotation(locator.getColumnNumber(), "-unknown-")).append("\n");
      }
    else
      {
	message.append("-Location unknown-\n");
      }
    message.append(transformerException.getMessage()).append("\n");
    return message.toString();
  }

  public static String composeMessage (TransformerException transformerException)
  {
    return composeMessage("ERROR in XSL transformation", transformerException);
  }

  public void error (TransformerException transformerException)
    throws TransformerException
  {
    throw transformerException;
  }

  public void fatalError (TransformerException transformerException)
    throws TransformerException
  {
    throw transformerException;
  }

  public void warning (TransformerException transformerException)
    throws TransformerException
  {
    // throw transformerException;
    System.err.print(composeMessage("WARNING", transformerException));
  }
}

