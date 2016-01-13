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

import java.io.StringReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * <p>
 *   A simple XPath processor based on an XSL stylesheet wrapping an XPath expression.
 * </p>
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: SimpleXPath.java,v 1.2 2003/08/21 09:21:58 rassy Exp $</span>
 */

public class SimpleXPath
{
  protected XSLStylesheet xslStylesheet;

  public SimpleXPath (TransformerFactory transformerFactory, String code)
    throws TransformerConfigurationException
  {
    String stylesheetCode =
      "<?xml version='1.0' encoding='utf-8'?>\n" +
      "<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n" +
      "  <xsl:output method='text'/>\n" +
      "  <xsl:strip-space elements='*'/>\n" +
      "  <xsl:template match='/'>\n" +
      "    <xsl:value-of select='" + code + "'/>\n" +
      "  </xsl:template>\n" +
      "</xsl:stylesheet>\n";

    StringReader reader = new StringReader(stylesheetCode);
      
    this.xslStylesheet = new XSLStylesheet(transformerFactory, new StreamSource(reader));
  }

  public SimpleXPath (String code)
    throws TransformerConfigurationException
  {
    this(TransformerFactory.newInstance(), code);
  }

  public String process (Source source)
    throws TransformerException
  {
    return this.xslStylesheet.process(source);
  }

  public String process (String sourceSystemId)
    throws TransformerException
  {
    return this.process(new StreamSource(sourceSystemId));
  }
}
