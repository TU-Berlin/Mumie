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

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import net.mumie.util.ResourceDependencyHelper;
import net.mumie.util.ResourceDependencyUpdateException;

/**
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: XSLStylesheetDependencyHelper.java,v 1.4 2005/02/17 19:37:17 rassy Exp $</span>
 */

public class XSLStylesheetDependencyHelper extends ResourceDependencyHelper
{
  /**
   * The {@link javax.xml.parsers.SAXParser SAXParser} that is used to parse stylesheet
   * sources.
   */

  protected SAXParser saxParser;

  /**
   * Parses the stylesheet source specified by <code>uri</code> and returns all references
   * declared in <code>"xsl:include"</code> or <code>xsl:import</code> elements.
   */

  protected List updateDependencies (final URI uri)
    throws ResourceDependencyUpdateException
  {
    try
      {
	this.log("updateDependencies", "1/2: uri = " + uri);
	final List dependencies = new ArrayList();
	DefaultHandler handler = new DefaultHandler()
	  {
	    public void startElement (String namespaceURI, String localName, String qualifiedName,
				      Attributes attributes)
	      throws SAXException
	    {
              try
                {
                  if ( ( qualifiedName.equals("xsl:include") ) ||
                       ( qualifiedName.equals("xsl:import") ) )
                    {
                      String href = attributes.getValue("href");
                      URI requiredURI =  uri.resolve(new URI(href));
                      dependencies.add(requiredURI);
                    }
                }
              catch (Exception exception)
                {
                  throw new SAXException(exception);
                }
            }
	  };
	this.saxParser.parse(new File(uri), handler);
	this.log("updateDependencies", "2/2: " + (dependencies.isEmpty() ? "No dependencies found." :
						 "Dependencies: " + listToString(dependencies) + "."));
	return dependencies;
      }
    catch (Exception exception)
      {
	throw new ResourceDependencyUpdateException(exception);
      }
  }

  /**
   * Creates and returns a {@link javax.xml.parsers.SAXParser SAXParser}. This method is
   * called to set {@link #saxParser} when no parser is passed to the contructor.
   */

  protected static SAXParser createSAXParser ()
    throws SAXException, ParserConfigurationException
  {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    return factory.newSAXParser();
  }

  /**
   * Creates a new <code>XSLStylesheetDependencyHelper</code> object.
   */

  public XSLStylesheetDependencyHelper (SAXParser saxParser)
  {
    this.logger = Logger.getLogger("net.mumie.xslt.XSLStylesheetDependencyHelper");
    this.classNameForLogging = "net.mumie.xslt.XSLStylesheetDependencyHelper";
    this.saxParser = saxParser;
  }

  /**
   * Creates a new <code>XSLStylesheetDependencyHelper</code> object.
   */

  public XSLStylesheetDependencyHelper ()
    throws SAXException, ParserConfigurationException
  {
    this(createSAXParser());
  }
}
