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

package net.mumie.ckxml;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Map;
import java.util.HashMap;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import java.util.Iterator;
import java.io.FileReader;
import org.xml.sax.InputSource;

/**
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SAXHandler.java,v 1.2 2008/09/23 12:02:26 rassy Exp $</code>
 */

public class SAXHandler extends DefaultHandler 
{
  // --------------------------------------------------------------------------------
  // Error handler methods
  // --------------------------------------------------------------------------------

  /**
   * Prints a {@link SAXParseException SAXParseException} to stderr.
   */

  protected void reportSAXParseException (String prefix, SAXParseException exception)
  {
    String systemId = exception.getSystemId();
    String publicId = exception.getPublicId();
    System.err.println
      (prefix + ": " +
       (systemId != null
        ? systemId
        : (publicId != null
           ? publicId
           : "Unknown source")) +
       ", line " + exception.getLineNumber() +
       ", column " + exception.getColumnNumber() +
       ": " +
       exception.getMessage());
  }

  /**
   * Reports a parser warning.
   */

  public void warning (SAXParseException exception)
    throws SAXException
  {
    this.reportSAXParseException("WARNING", exception);
  }

  /**
   * Reports a parser error.
   */

  public void error (SAXParseException exception)
    throws SAXException
  {
    this.reportSAXParseException("ERROR", exception);
  }

  /**
   * Reports a fatal parser error.
   */

  public void fatalError (SAXParseException exception)
    throws SAXException
  {
    this.reportSAXParseException("FATAL ERROR", exception);
    System.exit(1);
  }
}
