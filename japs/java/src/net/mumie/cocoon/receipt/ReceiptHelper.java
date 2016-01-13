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

import java.io.File;
import java.io.OutputStream;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.Identifyable;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

public interface ReceiptHelper extends Identifyable
{
  /**
   * Role as an Avalon service (<code>ReceiptHelper.class.getName()</code>).
   */

  public static final String ROLE = ReceiptHelper.class.getName();

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
    throws ReceiptException;

  /**
   * Returns the (recommended) filename for the receipt represented by the
   * specified datasheet.
   */

  public String getFilename (CocoonEnabledDataSheet dataSheet)
    throws ReceiptException;

  /**
   * Signs and zips the receipt represented by the specified dataSheet and writes
   * the result to the specified output stream. The datasheet should be a user answer
   * datasheet to which the receipt data were added by the
   * {@link #addReceiptData addReceiptData} method.
   */

  public void signZipAndWrite (CocoonEnabledDataSheet dataSheet, OutputStream out)
    throws ReceiptException;

  /**
   * Signs and zips the receipt represented by the specified dataSheet and writes
   * the result to a file in the receipt directory. The filename is obtained by
   * {@link #getFilename getFilename}. The datasheet should be a user answer
   * datasheet to which the receipt data were added by the
   * {@link #addReceiptData addReceiptData} method.
   */

  public void signZipAndStore (CocoonEnabledDataSheet dataSheet)
    throws ReceiptException;

  /**
   * If the receipt file with the specified filename exists in the receipt
   * directory, reads the file, writes it to the specified output stream, and
   * returns true. If the file does not exist, does nothing and returns false.
   */

  public boolean getAndWriteStoredReceipt (String filename, OutputStream out)
    throws ReceiptException;


/**
 *  Returns the ReceiptDir
 */

  public File getReceiptDir ()
    throws ReceiptException;
}