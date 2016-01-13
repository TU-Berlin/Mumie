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

package net.mumie.xtr;

import java.io.File;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import net.mumie.util.CmdlineParamHelper;

/**
 * A simple XSL checker.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmckxsl.java,v 1.2 2006/10/23 11:42:59 rassy Exp $</code>
 */

public class Mmckxsl
{
  /**
   * Checks the specified file for correct XSL.
   */

  protected static void check (File file)
    throws Exception
  {
    // Get error listener:
    ErrorListener errorListener = new XSLErrorListener();

    // Get the transformerFactory:
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setErrorListener(errorListener);

    // Get transformer source:
    Source transformerSource = new StreamSource(file);

    // Instanciate transformer:
    transformerFactory.newTransformer(transformerSource);
  }

  /**
   * Displayes a help text.
   */

  public static void showHelp ()
    throws Exception
  {
    System.out.print
      (
       "Usage:\n" +
       "  mmckxsl XSLFILE | --stylesheet=XSLFILE | -s XSLFILE\n" +
       "  mmckxsl --version | -v | --help | -h\n" +
       "Description:\n" +
       "  Checks an XSL stylesheet file.\n" +
       "Options:\n" +
       "  --stylesheet=XSLFILE, -s XSLFILE\n" +
       "      Specifies the XSL stylesheet to check\n" +
       "  --version , -v\n" +
       "      Prints version information and exists\n" +
       "  --help, -h\n" +
       "      Prints this help text and exists\n"
       );
  }

  /**
   * Displayes version information.
   */

  public static void showVersion ()
    throws Exception
  {
    System.out.println("$Revision: 1.2 $");
  }

  /**
   * Main method. Processes the parameters, then executes one of the following tasks:
   * checking, printing a help text, printing version information.
   */

  public static void main (String[] params)
    throws Exception
  {
    final int CHECK = 0;
    final int SHOW_HELP = 1;
    final int SHOW_VERSION = 2;
    int task = CHECK;

    File file = null;

    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( file == null && paramHelper.checkArgument() )
          file = new File(paramHelper.getParam());
        else if ( file == null &&
                  paramHelper.checkOptionWithValue("--stylesheet", "-s", false) )
          file = new File(paramHelper.getValue());
        else if ( paramHelper.checkParam("--help", "-h") )
          task = SHOW_HELP;
        else if ( paramHelper.checkParam("--version", "-v") )
          task = SHOW_VERSION;
        else
          throw new IllegalArgumentException
            ("Unknown parameter: " + paramHelper.getParam());
      }

    switch ( task )
      {
      case CHECK:
        if ( file == null )
          throw new IllegalArgumentException("No XSL file specified");
        check(file);
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
