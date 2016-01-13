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

package net.mumie.srv.build;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.mumie.util.CmdlineParamHelper;

/**
 * Applies an XSL stylesheet to a single source file. Usage from the command line:
 * <pre class="box">
 *   java net.mumie.srv.build.XSLTransformer \
 *     --stylesheet=<var>stylefile</var> \
 *     --input=<var>infile</var> \
 *     [--output=<var>outfile</var>] \
 *     [--param <var>name1</var>=<var>value1</var> \
 *      --param <var>name2</var>=<var>value2</var>
 *      ...]</pre>
 * where <var>stylefile</var>, <var>infile</var>, and <var>outfile</var> denote the
 * stylesheet file, input file, and output file, respectively. The <code>--param</code>
 * option sets stylesheet parameters. All options may be replaced by short forms as
 * follows:
 * <code>--stylesheet</code> by <code>-s</code>,
 * <code>--input</code> by <code>-i</code>,
 * <code>--output</code> by <code>-o</code>,
 * <code>--param</code> by <code>-p</code>.
 * In addition, the <code>'='</code> signs after <code>--stylesheet</code>,
 * <code>--input</code>, <code>--output</code>, and after the respective short forms may be
 * omitted (but the <code>'='</code> sign between a parameter name and value can not be
 * omitted). The specification of an output file is optional. If no one is specified, the
 * output is sent to stdout.
 */

public class XSLTransformer
{
  /**
   * Transforms the specifed input file to the specifed output file using the specified
   * stylehseet. The entries of the map <code>params</code> are passed as parameters to the
   * stylesheet. The keys of the map must be strings, the values may be arbitrary
   * objects. <code>outputFilename</code> may also be <code>null</code>, in which case the
   * output is sent stdout.
   *
   * @param stylesheetFilename filename of the XSL stylesheet
   * @param inputFilename filename of the input
   * @param outputFilename filename of the output, or <code>null</code> if the output shout
   * go to stdout
   * @param params parameters to pass to the stylesheet
   *
   * @throws Exception if something goes wrong
   */

  public static void transform (String stylesheetFilename,
                                String inputFilename,
                                String outputFilename,
                                Map params)
    throws Exception
  {
    // Get the stylesheet source object:
    Source stylesheet = new StreamSource(new File(stylesheetFilename));

    // Get the input source object:
    Source input = new StreamSource(new File(inputFilename));

    // Get the output result object:
    Result output = 
      (outputFilename != null
       ? new StreamResult(new File(outputFilename))
       : new StreamResult(System.out));

    // Get the transformer:
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setErrorListener(new XSLErrorListener());
    Transformer transformer = transformerFactory.newTransformer(stylesheet);
    transformer.setErrorListener(new XSLErrorListener());

    // Pass parameters to the transformer:
    if ( params != null )
      {
        Iterator iterator = params.entrySet().iterator();
        while ( iterator.hasNext() )
          {
            Map.Entry entry = (Map.Entry)iterator.next();
            transformer.setParameter((String)entry.getKey(), entry.getValue());
          }
      }

    // Transform:
    transformer.transform(input, output);
  }

  /**
   * Processes the command line parameters and calls {@link #transform transform}. See the
   * class documentation for additional information.
   *
   * @throws Exception if something goes wrong
   */

  public static void main (String[] params)
    throws Exception
  {
    String stylesheetFilename = null;
    String inputFilename = null;
    String outputFilename = null;
    Map stylesheetParams = new HashMap();

    // Process command line:
    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( paramHelper.checkOptionWithValue("--stylesheet", "-s") )
          stylesheetFilename = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--input", "-i") )
          inputFilename = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--output", "-o") )
          outputFilename = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithKeyValuePair("--param", "-p") )
          paramHelper.copyKeyValuePair(stylesheetParams);
        else
          throw new IllegalArgumentException
            ("Invalid parameter: " + paramHelper.getParam());
      }

    // Check variables:
    if ( stylesheetFilename == null )
      throw new IllegalArgumentException("Missing stylesheet");
    if ( inputFilename == null )
      throw new IllegalArgumentException("Missing input file");

    // Print log message provided outputFilename is not null:
    if ( outputFilename != null )
      System.out.println
        ("XSLTransformer: Transforming " + inputFilename + " -> " + outputFilename);

    // Perform transformation:
    transform(stylesheetFilename, inputFilename, outputFilename, stylesheetParams);
  }
}
