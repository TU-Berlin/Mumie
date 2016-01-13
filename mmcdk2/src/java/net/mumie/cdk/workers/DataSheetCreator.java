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

package net.mumie.cdk.workers;

import java.io.File;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import net.mumie.cdk.io.CdkFile;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.DefaultCocoonEnabledDataSheet;
import net.mumie.cocoon.util.DefaultPPDHelper;
import net.mumie.cocoon.util.PPDHelper;
import net.mumie.japs.datasheet.DataSheet;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.excalibur.xml.impl.JaxpParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DataSheetCreator extends CdkWorker
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The DOM parser of this instance.
   */

  protected DOMParser domParser;

  /**
   * The personalized problem data helper of this instance.
   */

  protected PPDHelper ppdHelper;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public DataSheetCreator ()
    throws DataSheetCreatorInitException
  {
    try
      {
        JaxpParser parser = new JaxpParser();
        parser.enableLogging(new NullLogger());
        parser.parameterize(Parameters.EMPTY_PARAMETERS);
        this.domParser = parser;
        this.ppdHelper = new DefaultPPDHelper();
      }
    catch (Exception exception)
      {
        throw new DataSheetCreatorInitException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Creating datasheets
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public void createDataSheet (CdkFile masterFile)
    throws Exception
  {
    this.checkStop();

    if ( !masterFile.isMasterFile() )
      throw new IllegalArgumentException("Not a master file: " + masterFile);

    try
      {
        this.msg(masterFile + ": ");

        // File objects needed:
        CdkFile contentFile = masterFile.getContentFile();
        CdkFile answersFile = masterFile.replaceSuffix(CdkFile.ANSWERS_SUFFIX);
        CdkFile correctionFile = masterFile.replaceSuffix(CdkFile.CORRECTION_SUFFIX);
        CdkFile datasheetFile = masterFile.replaceSuffix(CdkFile.DATASHEET_SUFFIX);

        // Init datasheet:
        CocoonEnabledDataSheet dataSheet = new DefaultCocoonEnabledDataSheet ();
        dataSheet.setEmptyDocument(this.domParser);

        // Get common data by extracting from content:
        Document content =
          this.domParser.parseDocument(new InputSource(contentFile.toURI().toString()));
        dataSheet.extract(content, DataSheet.REPLACE);

        // Add personalized data:
        this.ppdHelper.create(content, dataSheet, null);
        this.ppdHelper.resolve(dataSheet.getDocument(), dataSheet);

        // Add answers if existing:
        if ( answersFile.exists() )
          this.merge(dataSheet, answersFile);

        // Add correction if existing:
        if ( correctionFile.exists() )
          this.merge(dataSheet, correctionFile);

        // Write datasheet file:
        dataSheet.indent();
        dataSheet.toXMLCode(new FileOutputStream(datasheetFile));

        this.msgln("created datasheet");
      }
    catch (Exception exception)
      {
        this.msgln();
        throw exception;
      }
  }

  /**
   * 
   */

  protected void createDataSheets (CdkFile[] files,
                                   boolean recursive,
                                   boolean firstLevel)
    throws Exception
  {
    for (CdkFile file : files)
      {
        if ( file.isMasterFile() )
          this.createDataSheet(file);
        else if ( file.isDirectory() && ( recursive || firstLevel ) )
          this.createDataSheets
            (file.listCdkFiles(), recursive, false);
        else if ( firstLevel )
          throw new IllegalArgumentException("Not a master file: " + file);
      }
  }

  /**
   * 
   */

  public void createDataSheets (CdkFile[] files,
                                boolean recursive)
    throws Exception
  {
    this.createDataSheets(files, recursive, true);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected void merge (CocoonEnabledDataSheet dataSheet, File file)
    throws Exception
  {
    Document document =
      this.domParser.parseDocument(new InputSource(file.toURI().toString()));
    CocoonEnabledDataSheet otherDataSheet = new DefaultCocoonEnabledDataSheet();
    otherDataSheet.setDocument(document);
    dataSheet.merge(otherDataSheet, DataSheet.REPLACE);
  }
}