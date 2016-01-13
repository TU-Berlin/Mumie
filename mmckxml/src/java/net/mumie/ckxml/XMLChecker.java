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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLChecker
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
  // Static methods
  // --------------------------------------------------------------------------------

  /**
   * Checks the XML document with the specified filename. The specified features are set for
   * the parser
   */

  protected static void check (List<String> filenames, Map features)
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

    // Get an instance of SAXHandler, and set it as ContentHandler, DTDHandler,
    // and ErrorHandler:
    SAXHandler handler = new SAXHandler();
    parser.setContentHandler(handler);
    parser.setDTDHandler(handler);
    parser.setErrorHandler(handler);

    for (String filename : filenames)
      {
        // Get the input source from the filename:
        InputSource source = new InputSource(new FileReader(filename));
        source.setSystemId(filename);

        // parse:
        parser.parse(source);
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
       "  " + name + " [OPTIONS] FILENAMES\n" +
       "  " + name + " [--help|-h|--version]\n" +
       "Description:\n" +
       "  Checks the XML files FILENAMES for well-formedness and, optionally, validates\n" +
       "  them with respect to a DTD or Schema. This is done by sending the files through\n" +
       "  a SAX parser.\n" +
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
    System.out.println("$Revision: 1.2 $");
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

    List<String> filenames = new ArrayList<String>(); 
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
          features.put(STANDARD_FEATURE_PREFIX + "namespaces", new Boolean(true));

        // --schema, -s:
        else if ( param.equals("--schema") || param.equals("-s") )
          features.put(APACHE_FEATURE_PREFIX + "validation/schema", new Boolean(true));

        // FILENAME
        else if ( !param.startsWith("-") )
          filenames.add(param);

        else
          throw new IllegalArgumentException("Unknown parameter: " + param);
      }

    switch ( task )
      {
      case CHECK:
        if ( filenames.isEmpty() )
          throw new IllegalArgumentException("No input filename specified");
        check(filenames, features); break;
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
