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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.mumie.util.CmdlineParamHelper;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.ErrorListener;
import java.util.List;
import java.util.ArrayList;
import net.mumie.util.StringUtil;

/**
 * Simple command line tool to perform XSL transformations.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmxtr.java,v 1.4 2006/10/31 00:55:08 rassy Exp $</code>
 */

public class Mmxtr
{
  /**
   * The name of this class.
   */

  protected static String CLASS_NAME = Mmxtr.class.getName();

  /**
   * The default source suffix. Value is <code>"xml"</code>.
   */

  protected static String DEFAULT_SOURCE_SUFFIX = "xml";

  /**
   * The default result suffix. Value is <code>"xhtml"</code>.
   */

  protected static String DEFAULT_RESULT_SUFFIX = "xhtml";

  /**
   * Wether messages are printed to stdout. Default is <code>false</code>.
   */

  protected static boolean verbose = false;

  /**
   * Prints a message to stdout provided {@link #verbose} is <code>true</code>.
   */

  protected static void message (String message)
  {
    if ( verbose )
      System.out.println("mmxtr: " + message);
  }

  /**
   * Auxiliary method. Returns a string representation of the specified result object. If
   * the latter is a {@link StreamResult} and the corresponding output stream equals
   * <code>System.out</code>, the returned string is <code>"stdout"</code>. Otherwise, the
   * returned string is obtained by <code>result.getSystemId()</code>.
   */

  protected static String toString (Result result)
  {
    if ( result instanceof StreamResult &&
         ((StreamResult)result).getOutputStream() == System.out )
      return "stdout";
    else
      return result.getSystemId();
  }

  /**
   * Auxiliary method. Returns a string representation of the specified source object. If
   * the latter is a {@link StreamSource} and the corresponding input stream equals
   * <code>System.in</code>, the returned string is <code>"stdin"</code>. Otherwise, the
   * returned string is obtained by <code>source.getSystemId()</code>.
   */

  protected static String toString (Source source)
  {
    if ( source instanceof StreamSource &&
         ((StreamSource)source).getInputStream() == System.in )
      return "stdin";
    else
      return source.getSystemId();
  }

  /**
   * Auxiliary method. Passes the specified parameters to the specified transformer.
   */

  protected static void setTransfomerParams (Transformer transformer, Map transformerParams)
    throws Exception
  {
    Iterator paramsIterator = transformerParams.entrySet().iterator();
    while ( paramsIterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)paramsIterator.next();
        transformer.setParameter((String)entry.getKey(), entry.getValue());
      }
  }

  /**
   * Auxiliary method. Creates a {@link Result Result} object by replacing the specifed
   * source suffix by the specified result suffix in the specified source filename.
   */

  protected static Result composeResult (String sourceFilename,
                                         String sourceSuffix,
                                         String resultSuffix)
    throws Exception
  {
    String resultFilename =
      StringUtil.replaceSuffix(sourceFilename, sourceSuffix, resultSuffix);
    return new StreamResult(new File(resultFilename));
  }

  /**
   * Carries out the actual transformation.
   */

  protected static void transform (File transformerFile,
                                   List sourceFilenames,
                                   String resultFilename,
                                   String sourceSuffix,
                                   String resultSuffix,
                                   Map transformerParams)
    throws Exception
  {
    if ( transformerFile == null && sourceFilenames.size() == 0 )
      throw new IllegalArgumentException("Neither source nor stylesheet specified");

    if ( resultFilename != null && !resultFilename.equals("-") && sourceFilenames.size() > 1 )
      throw new IllegalArgumentException
	("Specifying an output file is not allowed with multiple input files");

    // Transformer source as specified by the user:
    Source specifiedTransfomerSource =
      (transformerFile != null
       ? new StreamSource(transformerFile)
       : null);

    // Result as specified by the user:
    Result specifiedResult =
      (resultFilename != null
       ? (resultFilename.equals("-")
          ? new StreamResult(System.out)
          : new StreamResult(new File(resultFilename)))
       : null);

    // Get error listener:
    ErrorListener errorListener = new XSLErrorListener();

    // Get the transformerFactory:
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setErrorListener(errorListener);

    if ( sourceFilenames.size() == 0 ) // No source specified
      {
        // Get source from stdin:
        Source source = new StreamSource(System.in);

        // Get transformer:
        Transformer transformer =
          transformerFactory.newTransformer(specifiedTransfomerSource);
        transformer.setErrorListener(errorListener);

        // Set parameters:
        setTransfomerParams(transformer, transformerParams);

        // Transform:
        transformer.transform(source, specifiedResult);
      }
    else // one or more source(s) specified
      {
        Iterator sourceIterator = sourceFilenames.iterator();
        while ( sourceIterator.hasNext() )
          {
            // Get input source:
            String sourceFilename = (String)sourceIterator.next();
            Source source = new StreamSource(new File(sourceFilename));

            // Get result:
            Result result =
              (specifiedResult != null
               ? specifiedResult
               : composeResult(sourceFilename, sourceSuffix, resultSuffix));

            // Get transformer source:
            Source transformerSource =
              (specifiedTransfomerSource != null
               ? specifiedTransfomerSource
               : transformerFactory.getAssociatedStylesheet(source, null, null, null));

            // Get transformer:
            message("Loading transformer: " + toString(transformerSource));
            Transformer transformer =
              transformerFactory.newTransformer(transformerSource);
            transformer.setErrorListener(errorListener);

            // Set parameters:
            setTransfomerParams(transformer, transformerParams);

            // Transform:
            message("Transforming: " + toString(source) + " -> " + toString(result));
            transformer.transform(source, result);
          }
      }
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
       "  mmxtr [OPTIONS] [SOURCES]\n" +
       "Description:\n" +
       "  Applies XSL stylesheets to XML sources.\n" +
       "Options:\n" +
       "  --stylesheet=XSLFILE, -s XSLFILE\n" +
       "      Specifies the XSL stylesheet to use. If not specifed, mmxtr tries to\n" +
       "      obtain the stylesheet from the <?xml-stylesheet ...?> processing\n" +
       "      instruction in the source.\n" +
       "  --input=SOURCE, -i SOURCE\n" +
       "      Specifies the source file. May occur several times to specify more than\n" +
       "      one source file. If not specified, the source is read from stdin.\n" +
       "  --output=OUTPUT, -o OUTPUT\n" +
       "      Specifies the file the output is written to. If omitted, the filename is\n" +
       "      automatically created by replacing the suffix INSUFF of the source file\n" +
       "      by OUTSUFF (s.b.). If \"-\", output goes to stdout. If multiple sources are\n" +
       "      specifed, it is not allowed to specify a file by this option; i.e., the\n" +
       "      option must either be omitted or \"-\" in that case.\n" +
       "  --input-suffix=INSUFF, -I INSUFF\n" +
       "      Specifies the input suffix. This is the suffix which is replaced by\n" +
       "      OUTSUFF in automatic output filename creation. Default is \"xml\".\n" +
       "  --output-suffix=OUTSUFF, -I OUTSUFF\n" +
       "      Specifies the output suffix. This is the suffix which replaces INSUFF in\n" +
       "      automatic output filename creation.  Default is \"xhtml\".\n" +
       "  --param NAME=VALUE, -p NAME=VALUE\n" +
       "      Specifies a stylesheet parameter\n" +
       "  --version , -v\n" +
       "      Prints version information and exists\n" +
       "  --verbose , -b\n" +
       "      Enables printing of status messages to stdout\n" +
       "  --help, -h\n" +
       "      Prints this help text and exists\n" +
       "Examples:\n" +
       "  $ mmxtr -s transf.xsl -o output.xml input.xml\n" +
       "      Transforms input.xml to output.xml by means of transf.xsl\n" +
       "  $ mmxtr foo.xml\n" +
       "      Transforms foo.xml to foo.xhtml using the XSL stylesheet specified in the\n" +
       "      <?xml-stylesheet ...?> processing instruction in foo.xml.\n" +
       "  $ mmxtr foo.xdat -I xdat -O txt\n" +
       "      Transforms foo.xdat to foo.txt using the XSL stylesheet specified in the\n" +
       "      <?xml-stylesheet ...?> processing instruction in foo.xdat.\n" +
       "  $ mmxtr -s transf.xsl foo1.xml foo2.xml foo3.xml\n" +
       "      Transforms foo1.xml, foo2.xml, and foo3.xml to foo1.xhtml, foo2.xhtml,\n" +
       "      and foo3.xhtml, respectivly, using the stylesheet transf.xsl in each\n" +
       "      transformation.\n" +
       "  $ mmxtr foo1.xml foo2.xml foo3.xml\n" +
       "      Transforms foo1.xml to foo1.xhtml using the stylesheet specified in the\n" +
       "      <?xml-stylesheet ...?> processing instruction in foo1.xml, foo2.xml to\n" +
       "      foo2.xhtml using the stylesheet specified in the <?xml-stylesheet ...?>\n" +
       "      processing instruction in foo2.xml, and so on.\n" +
       "  $ mmxtr -s transf.xsl -o - input.xml\n" +
       "      Transforms input.xml by means of transf.xsl and writes the output to\n" +
       "      stdout.\n" +
       "  $ mmxtr -s transf.xsl -o output.xml\n" +
       "      Reads input from stdin and transforms it to output.xml by means of\n" +
       "      transf.xsl\n"
       );
  }

  /**
   * Displayes version information.
   */

  public static void showVersion ()
    throws Exception
  {
    System.out.println("$Revision: 1.4 $");
  }

  /**
   * Main method. Processes the parameters, then executes one of the following tasks: doinf
   * the transformation, printing a help text, printing version information.
   */

  public static void main (String[] params)
    throws Exception
  {
    final int TRANSFORM = 0;
    final int SHOW_HELP = 1;
    final int SHOW_VERSION = 2;
    int task = TRANSFORM;

    File transformerFile = null;
    List sourceFilenames = new ArrayList();
    String resultFilename = null;
    String sourceSuffix = DEFAULT_SOURCE_SUFFIX;
    String resultSuffix = DEFAULT_RESULT_SUFFIX;
    Map transformerParams = new HashMap();

    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( paramHelper.checkOptionWithValue("--output", "-o", false) )
          resultFilename = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--stylesheet", "-s") )
          transformerFile = new File(paramHelper.getValue());
        else if ( paramHelper.checkOptionWithKeyValuePair("--param", "-p") )
          paramHelper.copyKeyValuePair(transformerParams);
        else if ( paramHelper.checkParam("--help", "-h") )
          task = SHOW_HELP;
        else if ( paramHelper.checkParam("--version", "-v") )
          task = SHOW_VERSION;
        else if ( paramHelper.checkOptionWithValue("--input-suffix", "-I") )
          sourceSuffix = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--output-suffix", "-O") )
          resultSuffix = paramHelper.getValue();
        else if ( paramHelper.checkParam("--verbose", "-b") )
          verbose = true;
        else if ( paramHelper.checkOptionWithValue("--input", "-i", false) )
          sourceFilenames.add(paramHelper.getValue());
        else if ( paramHelper.checkArgument() )
          sourceFilenames.add(paramHelper.getParam());
        else
          throw new IllegalArgumentException
            ("Unknown parameter: " + paramHelper.getParam());
      }

    switch ( task )
      {
      case TRANSFORM:
        transform
          (transformerFile, sourceFilenames, resultFilename, sourceSuffix,
           resultSuffix, transformerParams);
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
