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
 * A simple XML checker.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: XMLChecker.java,v 1.4 2006/10/23 11:44:41 rassy Exp $</code>
 */

public class XMLChecker extends DefaultHandler 
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Prefix of standard SAX2 features (<code>"http://xml.org/sax/features/"</code>).
   */

  static final String STANDARD_FEATURE_PREFIX = "http://xml.org/sax/features/";

  /**
   * Prefix of Apache SAX2 features (<code>"http://apache.org/xml/features/"</code>).
   */

  static final String APACHE_FEATURE_PREFIX = "http://apache.org/xml/features/";

  // --------------------------------------------------------------------------------
  // Inner classes
  // --------------------------------------------------------------------------------

  /**
   * A throwable to signal that a fatal error occurred.
   */

  protected static class FatalError extends RuntimeException {};

  // --------------------------------------------------------------------------------
  // Error handler methods
  // --------------------------------------------------------------------------------

  /**
   * Prints a {@link SAXParseException SAXParseException} to stderr.
   */

  protected void reportSAXParseException (String prefix, SAXParseException exception)
  {
    System.err.println
      (prefix + ": " +
       "Line " + exception.getLineNumber() +
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
    throw new FatalError();
  }

  // --------------------------------------------------------------------------------
  // Static methods
  // --------------------------------------------------------------------------------

  /**
   * Checks the XML document with the specified filename. The specified features are set for
   * the parser
   */

  protected static void check (String filename, Map features)
    throws Exception
  {
    // Create the parser:
    XMLReader parser = XMLReaderFactory.createXMLReader();

    // Set the features:
    Iterator iterator = features.entrySet().iterator();
    while ( iterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)iterator.next();
        String name = (String)entry.getKey();
        boolean value = ((Boolean)entry.getValue()).booleanValue();
        parser.setFeature(name, value);
      }

    // Get an instance of this class, and set it as ContentHandler, DTDHandler,
    // and ErrorHandler:
    XMLChecker xmlChecker = new XMLChecker();
    parser.setContentHandler(xmlChecker);
    parser.setDTDHandler(xmlChecker);
    parser.setErrorHandler(xmlChecker);

    // Get the input source from the filename:
    InputSource source = new InputSource(new FileReader(filename));

    // parse:
    try
      {
        parser.parse(source);
      }
    catch (FatalError fatalError)
      {
        System.exit(1);
      }
  }

  /**
   * Prints a help text to stdout.
   */

  protected static void showHelp ()
  {
    final String name =
      System.getProperty(XMLChecker.class.getName() + ".execName", "mmckxml");
    System.out.print
      (
       "Usage:\n" +
       "  " + name + " [OPTIONS] FILENAME\n" +
       "  " + name + " [--help|-h|--version]\n" +
       "Description:\n" +
       "  Checks the XML file FILENAME for well-formedness and, optionally, validates\n" +
       "  it with respect to a DTD or Schema. This is done by sending the file through\n" +
       "  a SAX parser.\n" +
       "    If no error is found, " + name + " simply returns with exit code 0. Otherwise,\n" +
       "  the error is reported to stderr and the exit code is different from 0.\n" +
       "Options:\n" +
       "  --feature NAME=VALUE, -f NAME=VALUE\n" +
       "      Sets a feature of the SAX parser\n" +
       "  --validate, -v\n" +
       "      Turns validation on\n" +
       "  --namespaces, -n\n" +
       "      Turns namespace processing on\n" +
       "  --schema, -s\n" +
       "      Turns Schema validation on\n" +
       "  --help, -h\n" +
       "      Prnts this help text and exits\n" +
       "  --version\n" +
       "      Prints version information and exits\n"
      );
  }

  /**
   * Prints version information to stdout.
   */

  protected static void showVersion ()
  {
    System.out.println("$Revision: 1.4 $");
  }

  /**
   * The main method
   */

  public static void main (String[] params)
    throws Exception
  {
    final int CHECK = 0;
    final int SHOW_HELP = 1;
    final int SHOW_VERSION = 2;
    int task = CHECK;

    String filename = null;
    Map features = new HashMap();

    // Process command line:
    for (int i = 0; i < params.length; i++)
      {
        String param = params[i];

        // --help, -h:
        if ( param.equals("--help") || param.equals("-h") )
          task = SHOW_HELP;

        // --version:
        else if ( param.equals("--version") )
          task = SHOW_VERSION;

	// --feature NAME=VALUE, -f NAME=VALUE:
	else if ( ( param.equals("--feature") ) || ( param.equals("-p") ) )
	  {
            if ( !( i < params.length ) )
	      throw new IllegalArgumentException("Missing value after " + param);
	    i++;
            processFeatureParam(params[i], features);
	  }

        // --validate, -v:
        else if ( param.equals("--validate") || param.equals("-v") )
          features.put(STANDARD_FEATURE_PREFIX + "validation", new Boolean(true));

        // --namespaces, -n:
        else if ( param.equals("--namespaces") || param.equals("-n") )
          features.put(APACHE_FEATURE_PREFIX + "namespaces", new Boolean(true));

        // --schema, -s:
        else if ( param.equals("--schema") || param.equals("-s") )
          features.put(APACHE_FEATURE_PREFIX + "validation/schema", new Boolean(true));

        // FILENAME
        else if ( filename == null && !param.startsWith("-") )
          filename = param;

        else
          throw new IllegalArgumentException("Unknown parameter: " + param);
      }

    switch ( task )
      {
      case CHECK:
        check(filename, features); break;
      case SHOW_HELP:
        showHelp(); break;
      case SHOW_VERSION:
        showVersion(); break;
      }
  }

  /**
   * 
   */

  protected static void processFeatureParam (String param, Map features)
  {
    int posEq = param.lastIndexOf('=');
    if ( posEq == -1 )
      throw new IllegalArgumentException
        ("Invalid feature specifier: " + param + ": Must end with \"=true\" or \"=false\"");
    if ( posEq == 0 )
      throw new IllegalArgumentException
        ("Invalid feature specifier: " + param + ": Missing feature name");
    if ( posEq == (param.length()-1) )
      throw new IllegalArgumentException
        ("Invalid feature specifier: " + param + ": Missing feature value");
    String name = param.substring(0, posEq);
    String value = param.substring(posEq+1);
    if ( value.equals("true") )
      features.put(name, new Boolean(true));
    else if ( value.equals("false") )
      features.put(name, new Boolean(false));
    else
      throw new IllegalArgumentException
        ("Invalid feature specifier: " + param + ": value must be \"true\" or \"false\"");
  }

}
