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

import java.io.File;
import java.io.FileReader;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import net.mumie.util.CmdlineParamHelper;

public class XMLValidator
{
  /**
   * Validates the specified XML file with respect to the specified Schema file.
   */

  protected static void validate (String xmlFilename, String schemaFilename)
    throws Exception
  {
    // SAX event handler:
    SAXHandler saxHandler = new SAXHandler();

    // Get a validator as a SAX ContentHandler:
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    schemaFactory.setErrorHandler(saxHandler);
    ValidatorHandler validatorHandler =
      schemaFactory.newSchema(new File(schemaFilename)).newValidatorHandler();
    validatorHandler.setErrorHandler(saxHandler);

    // Create a SAX parser:
    XMLReader parser = XMLReaderFactory.createXMLReader();
    parser.setErrorHandler(saxHandler);
    parser.setDTDHandler(saxHandler);
    parser.setContentHandler(validatorHandler);

    // Get the input source from the filename:
    InputSource source = new InputSource(new FileReader(xmlFilename));
    
    // Parse the source:
    parser.parse(source);
  }

  /**
   * Prints a help text to stdout.
   */

  public static void showHelp ()
  {
    final String[] TEXT =
      {
        "Usage:",
        "  mmvalxml XML_FILE SCHEMA_FILE",
        "  mmvalxml --help | -h | -version | -v",
        "Description:",
        "  Validates the specified xml file with respect to the specified Schema file.",
        "  The latter must be conform to the W3C XML Schema (WXS) specification.",
        "Options:",
        "  --help, -h",
        "      Prints this help text and exits.",
        "  --version, -v",
        "      Prints version information and exits.",
      };
    for (String line : TEXT)
      System.out.println(line);
  }

  /**
   * Prints version information and exits.
   */

  public static void showVersion ()
  {
    System.out.println("$Revision: 1.1 $");
  }

  /**
   * Main entry point.
   */

  public static void main (String[] params)
    throws Exception
  {
    final int VALIDATE = 0;
    final int SHOW_HELP = 1;
    final int SHOW_VERSION = 2;
    int task = VALIDATE;

    String xmlFilename = null;
    String schemaFilename = null;

    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( paramHelper.checkParam("--help", "-h") )
          task = SHOW_HELP;
        else if ( paramHelper.checkParam("--version", "-v") )
          task = SHOW_VERSION;
        else if ( paramHelper.checkArgument() )
          {
            if ( xmlFilename == null )
              xmlFilename = paramHelper.getParam();
            else if ( schemaFilename == null )
              schemaFilename = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Extra argument: " + paramHelper.getParam());
          }
        else
          throw new IllegalArgumentException
            ("Unknown parameter: " + paramHelper.getParam());
      }

    switch ( task )
      {
      case VALIDATE:
        if ( xmlFilename == null )
          throw new IllegalArgumentException("No XML file specified");
        if ( schemaFilename == null )
          throw new IllegalArgumentException("No Schme file specified");
        validate(xmlFilename, schemaFilename);
        break;
      case SHOW_HELP:
        showHelp();
        break;
      case SHOW_VERSION:
        showVersion();
        break;
      }
  }

}



//     // Get a DOM object from the XML file:
//     DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//     documentBuilderFactory.setNamespaceAware(true);
//     documentBuilderFactory.setValidating(false);
//     DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//     documentBuilder.setErrorHandler(errorHandler);
//     Document document = documentBuilder.parse(new File(xmlFilename));
