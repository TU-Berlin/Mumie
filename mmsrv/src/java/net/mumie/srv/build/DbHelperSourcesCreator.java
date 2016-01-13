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
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.mumie.util.CmdlineParamHelper;

/**
 * Creates the db helper sources. Usage from the command line
 * <pre class="box">
 *   java net.mumie.srv.build.DbHelperCreator \
 *     --xsl-dir=<var>xsldir</var> \
 *     --db-helper-dir=<var>dbhdir</var></pre>
 * <var>xsldir</var> denotes the directory where the XSL stylesheet
 * <code>DbHelper.xsl</code> is expected. 
 * <var>dbhdir</var> denotes the directory where the XML input file
 * <code>DbHelper.xml</code> and the skeleton files <code >*.java.skel</code> are expected.
 * <var>xsldir</var> should be set to <var>basedir</var><code>/tools/xsl</code>
 * and <var>dbhdir</var> to
 * <var>basedir</var><code>/java/src/net/mumie/cocoon/db</code>, where
 * <var>basedir</var> denotes the root directory of the Japs distribution.
 *
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DbHelperSourcesCreator.java,v 1.2 2008/07/29 15:54:28 rassy Exp $</code>
 */

public class DbHelperSourcesCreator
{
  /**
   * The stylesheet that carries out the transforations from XML to Java code.
   */

  protected Templates templates = null;

  /**
   * The directory where the db helper files reside
   */

  protected String dbHelperDir = null;

  /**
   * The XSL directory
   */

  protected String xslDir;

  /**
   * Prints a log message
   */

  protected void log (String message)
  {
    System.out.println("DbHelperSourcesCreator: " + message);
  }

  /**
   * Creates a new instance
   */

  public DbHelperSourcesCreator (String xslDir, String dbHelperDir)
    throws Exception
  {
    File templatesFile = new File(xslDir, "DbHelper.xsl");
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setErrorListener(new XSLErrorListener());
    this.templates = transformerFactory.newTemplates(new StreamSource(templatesFile));
    this.dbHelperDir = dbHelperDir;
  }

  /**
   * Creates the db helper java sources corresponding to <code>target</code>
   */

  protected void create (String target)
    throws Exception
  {
    log("Creating " + target);
    File inputFile = new File(this.dbHelperDir, "DbHelper.xml");
    File outputFile = new File(this.dbHelperDir, target + ".java");
    File skeletonFile = new File(this.dbHelperDir, target + ".java.skel");
    Transformer transformer = this.templates.newTransformer();
    transformer.setErrorListener(new XSLErrorListener());
    transformer.setParameter("target", target);
    transformer.setParameter("skeleton-filename", skeletonFile.getPath());
    transformer.transform(new StreamSource(inputFile), new StreamResult(outputFile));
  }

  /**
   * Creates the db helper java sources 
   */

  public static void main (String[] params)
    throws Exception
  {
    String xslDir = null;
    String dbHelperDir = null;

    // Process command line:
    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( paramHelper.checkOptionWithValue("--xsl-dir", "-x") )
          xslDir = paramHelper.getValue();
        else if ( paramHelper.checkOptionWithValue("--db-helper-dir", "-d") )
          dbHelperDir = paramHelper.getValue();
        else
          throw new IllegalArgumentException
            ("Invalid parameter: " + paramHelper.getParam());
      }

    // Check variables:
    if ( xslDir == null )
      throw new IllegalArgumentException("Missing xsl dir");
    if ( dbHelperDir == null )
      throw new IllegalArgumentException("Missing db helper dir");

    // Create an instance:
    DbHelperSourcesCreator creator = new DbHelperSourcesCreator(xslDir, dbHelperDir);

    // Create sources:
    creator.create("DbHelper");
    creator.create("AbstractDbHelper");
    creator.create("PostgreSQLDbHelper");
  }
}
