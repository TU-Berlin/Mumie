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

package net.mumie.cocoon.receipt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.mumie.cocoon.clienttime.ClientTime;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.sign.SignHelper;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

public class ReceiptHelperImpl extends AbstractJapsServiceable
  implements Configurable, Poolable, ReceiptHelper
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(ReceiptHelperImpl.class);

  /**
   * The datasheet path of the unit where the receipt data are kept.
   */

  public static final String RECEIPT_PATH = "user/receipt";

  /**
   * Directory where the receipts are kept.
   */

  protected File receiptDir = null;

  /**
   * The time format used by this reader.
   */

  protected DateFormat timeFormat = null;

  /**
   * The time format used in the receipt filename.
   */

  protected DateFormat filenameTimeFormat = null;

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>ReceiptHelperImpl</code>.
   */

  public ReceiptHelperImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this rceipt helper. See class decription for details.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.receiptDir = new File(configuration.getChild("receipt-dir").getValue().trim());
        if ( !this.receiptDir.exists() )
          throw new IllegalArgumentException ("Directory does not exist: " + this.receiptDir);
        if ( !this.receiptDir.isDirectory() )
          throw new IllegalArgumentException("Not a directory: " + this.receiptDir);
        this.timeFormat = new SimpleDateFormat
          (getConfValue(configuration, "time-format", TimeFormat.PRECISE));
        this.filenameTimeFormat = new SimpleDateFormat
          (getConfValue(configuration, "filename-time-format", TimeFormat.FILENAME));
        this.logDebug
          (METHOD_NAME + " 2/2: Done." +
           " receiptDir = " + this.receiptDir +
           ", timeFormat = " + this.timeFormat +
           ", filenameTimeFormat = " + this.filenameTimeFormat);
      }
    catch (Exception exception)
      {
        throw new ConfigurationException("Failed to configure ReceiptHelperImpl", exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Receipt methods
  // --------------------------------------------------------------------------------

  /**
   * Adds the receipt data specified by the first four parameters to the specified
   * datasheet. Uses the specified db helper for retrieving the data.
   */

  public void addReceiptData (int userId,
                              int refId,
                              int courseId,
                              int worksheetId,
                              int problemId,
                              CocoonEnabledDataSheet datasheet,
                              DbHelper dbHelper)
    throws ReceiptException
  {
    final String METHOD_NAME = "addReceiptData";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " userId = " + userId +
       ", refId = " + refId +
       ", courseId = " + courseId +
       ", worksheetId = " + worksheetId +
       ", problemId = " + problemId);
    ClientTime clientTime = null;
    try
      {
        // Get receipt data:
        ResultSet receiptData = dbHelper.queryReceiptData
          (userId, refId, courseId, worksheetId, problemId);
        if ( !receiptData.next() )
          throw new SQLException
            ("Failed to get receipt data for user " + userId + " and ref id " + refId +
             ": Result set empty");
        clientTime = (ClientTime)this.serviceManager.lookup(ClientTime.ROLE);
        long currentTime = clientTime.getTime();
        Date currentDate = new Date(currentTime);
        String worksheetLabel = receiptData.getString(DbColumn.WORKSHEET_LABEL);
        int worksheetCategoryId = receiptData.getInt(DbColumn.WORKSHEET_CATEGORY_ID);
        String problemLabel = receiptData.getString(DbColumn.PROBLEM_LABEL);
        String worksheetCategoryShortcut;
        switch ( worksheetCategoryId )
          {
          case Category.HOMEWORK : worksheetCategoryShortcut = "hwk"; break;
          case Category.PRELEARN : worksheetCategoryShortcut = "prl"; break;
          default : worksheetCategoryShortcut = Category.nameFor(worksheetCategoryId);
          }
        String filename = normalizeFilename
          ("rcpt__" +
           userId + "__" +
           worksheetCategoryShortcut + "__" +
           worksheetLabel + "__" + problemLabel + "__" +
           this.filenameTimeFormat.format(currentDate) +
           ".zip");

        // Add receipt data to datasheet:
        datasheet.put(RECEIPT_PATH + "/time",
                      this.timeFormat.format(currentDate));
        datasheet.put(RECEIPT_PATH + "/time-raw", currentTime);
        datasheet.put(RECEIPT_PATH + "/user-id", userId);
        datasheet.put(RECEIPT_PATH + "/user-first-name",
                      receiptData.getString(DbColumn.USER_FIRST_NAME));
        datasheet.put(RECEIPT_PATH + "/user-surname",
                      receiptData.getString(DbColumn.USER_SURNAME));
        datasheet.put(RECEIPT_PATH + "/course-id",courseId);
        datasheet.put(RECEIPT_PATH + "/course-name",
                      receiptData.getString(DbColumn.COURSE_NAME));
        datasheet.put(RECEIPT_PATH + "/worksheet-id",worksheetId);
        datasheet.put(RECEIPT_PATH + "/worksheet-name",
                      receiptData.getString(DbColumn.WORKSHEET_NAME));
        datasheet.put(RECEIPT_PATH + "/worksheet-label", worksheetLabel);
        datasheet.put(RECEIPT_PATH + "/problem-id",problemId);
        datasheet.put(RECEIPT_PATH + "/problem-name",
                      receiptData.getString(DbColumn.PROBLEM_NAME));
        datasheet.put(RECEIPT_PATH + "/problem-name", problemLabel);
        datasheet.put(RECEIPT_PATH + "/worksheet-problem-ref-id", refId);
        datasheet.put(RECEIPT_PATH + "/filename", filename);

        this.logDebug(METHOD_NAME + "2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ReceiptException(exception);
      }
    finally
      {
        if ( clientTime != null )
          this.serviceManager.release(clientTime);
      }
  }

  /**
   * Returns the (recommended) filename for the receipt represented by the
   * specified datasheet.
   */

  public String getFilename (CocoonEnabledDataSheet dataSheet)
    throws ReceiptException
  {
    try
      {
        String filename = dataSheet.getAsString(RECEIPT_PATH + "/filename");
        if ( filename == null )
          throw new IllegalStateException("Datasheet contains no receipt filename");
        return filename;
      }
    catch (Exception exception)
      {
        throw new ReceiptException(exception);
      }
  }

  /**
   * Signs and zips the receipt represented by the specified dataSheet and writes
   * the result to the specified output stream. The datasheet should be a user answer
   * datasheet to which the receipt data were added by the
   * {@link #addReceiptData addReceiptData} method.
   */

  public void signZipAndWrite (CocoonEnabledDataSheet dataSheet, OutputStream out)
    throws ReceiptException
  {
    final String METHOD_NAME = "signZipAndWrite";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    SignHelper signHelper = null;
    try
      {
        // Init sign helper:
        signHelper = (SignHelper)this.serviceManager.lookup(SignHelper.ROLE);

        // Sign receipt, zip it, and write it to the output stream:
        signHelper.signAndZip
          (new ByteArrayInputStream(dataSheet.toXMLCode().getBytes()), out);

        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ReceiptException(exception);
      }
    finally
      {
        if ( signHelper != null )
          this.serviceManager.release(signHelper);
      }
  }

  /**
   * Signs and zips the receipt represented by the specified dataSheet and writes
   * the result to a file in the receipt directory. The filename is obtained by
   * {@link #getFilename getFilename}. The datasheet should be a user answer
   * datasheet to which the receipt data were added by the
   * {@link #addReceiptData addReceiptData} method.
   */

  public void signZipAndStore (CocoonEnabledDataSheet dataSheet)
    throws ReceiptException
  {
    try
      {
        File file = new File(this.receiptDir, this.getFilename(dataSheet));
        this.signZipAndWrite(dataSheet, new FileOutputStream(file));
      }
    catch (Exception exception)
      {
        throw new ReceiptException(exception);
      }
  }

  /**
   * If the receipt file with the specified filename exists in the receipt
   * directory, reads the file, writes it to the specified output stream, and
   * returns true. If the file does not exist, does nothing and returns false.
   */

  public boolean getAndWriteStoredReceipt (String filename, OutputStream out)
    throws ReceiptException
  {
    final String METHOD_NAME = "getAndWriteStoredReceipt";
    this.logDebug(METHOD_NAME + " 1/2: Started. filename = " + filename);
    try
      {
        File file = new File(this.receiptDir, filename);
        FileInputStream in = null;
        try
          {
            in = new FileInputStream(file);
          }
        catch (FileNotFoundException notFoundException)
          {
            this.logDebug(METHOD_NAME + " 2/2: Done. File not found: " + file);
            return false;
          }
        byte[] buffer = new byte[1024];
        int len;
        while ( (len = in.read(buffer)) != -1 )
          out.write(buffer, 0, len);
        out.flush();
        in.close();
        this.logDebug(METHOD_NAME + " 2/2: Done");
        return true;
      }
    catch (Exception exception)
      {
        throw new ReceiptException(exception);
      }
  }

  /**
   * Returns the ReceiptDir
   */
  public File getReceiptDir() throws ReceiptException
  {
    if (receiptDir == null) throw new IllegalArgumentException ("NIL!");
    return receiptDir;
  }
  
  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the value of the configuration child with the specified name, or the
   * specified default value if the child does not exist.
   */

  protected static String getConfValue (Configuration configuration,
                                        String name, String defaultValue)
    throws Exception
  {
    Configuration child = configuration.getChild(name, false);
    return
      (child == null
       ? defaultValue
       : child.getValue().trim());
  }

  /**
   * Replaces all whitespaces by underscores.
   */

  protected static String normalizeFilename (String filename)
  {
    char[] chars = filename.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        if ( Character.isWhitespace(chars[i]) )
          chars[i] = '_';
      }
    return new String(chars);
  }
  


}

