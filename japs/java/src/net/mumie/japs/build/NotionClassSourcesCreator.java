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

package net.mumie.japs.build;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.util.StringUtil;

public class NotionClassSourcesCreator
{
  /**
   * The factory that creates the transformers corresponding to the various stylesheets.
   */

  protected TransformerFactory transformerFactory;

  /**
   * The configuration file.
   */

  protected File configFile;

  /**
   * The directory where the notion class sources are created
   */

  protected String resultDir;

  /**
   * The XSL directory
   */

  protected String xslDir;

  /**
   * Prints a log message
   */

  protected void log (String message)
  {
    System.out.println("NotionClassSourcesCreator: " + message);
  }

  /**
   * Creates the specified Java source.
   */

  protected void create (String resultName)
    throws Exception
  {
    this.log("Creating " + resultName);

    // Get the stylesheet source object:
    String stylesheetName = StringUtil.replaceSuffix(resultName, "java", "xsl");
    Source stylesheet = new StreamSource(new File(this.xslDir, stylesheetName));

    // Get the input source object:
    Source input = new StreamSource(this.configFile);

    // Get the output result object:
    Result result = new StreamResult(new File(this.resultDir, resultName));

    // Get the transformer:
    Transformer transformer = this.transformerFactory.newTransformer(stylesheet);
    transformer.setErrorListener(new XSLErrorListener());
 
    // Transform:
    transformer.transform(input, result);
 }

  /**
   * Creates a new instance.
   */

  public NotionClassSourcesCreator (String xslDir, String resultDir, String configDir)
    throws Exception
  {
    this.xslDir = xslDir;
    this.resultDir = resultDir;

    this.transformerFactory = TransformerFactory.newInstance();
    this.transformerFactory.setErrorListener(new XSLErrorListener());

    this.configFile = new File(configDir, "config.xml");
  }

  /**
   * Creates the Java sources.
   */

  public static void main (String[] params)
    throws Exception
  {
    String xslDir = null;
    String resultDir = null;
    String configDir = null;
    List resultNames = new ArrayList();

    // Process command line:
    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( paramHelper.checkOptionWithValue("--xsl-dir", "-x") )
          xslDir = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--result-dir", "-r") )
          resultDir = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--config-dir", "-c") )
          configDir = paramHelper.getValue();
        else if ( paramHelper.checkArgument() )
          resultNames.add(paramHelper.getParam());
        else
          throw new IllegalArgumentException
            ("Invalid parameter: " + paramHelper.getParam());
      }

    // Check variables:
    if ( xslDir == null )
      throw new IllegalArgumentException("Missing xsl dir");
    if ( resultDir == null )
      throw new IllegalArgumentException("Missing result dir");
    if ( configDir == null )
      throw new IllegalArgumentException("Missing config dir");

    // Create an instance:
    NotionClassSourcesCreator creator =
      new NotionClassSourcesCreator(xslDir, resultDir, configDir);

    // Create Java sources:
    Iterator iterator = resultNames.iterator();
    while ( iterator.hasNext() )
      {
        String resultName = (String)iterator.next();
        creator.create(resultName);
      }
  }
}
