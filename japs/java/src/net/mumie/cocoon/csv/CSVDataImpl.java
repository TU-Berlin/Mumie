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

package net.mumie.cocoon.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;

public class CSVDataImpl extends AbstractJapsService
  implements Recyclable, CSVData
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(CSVDataImpl.class);

  /**
   * The parsed data.
   */

  protected String[][] data;

  /**
   * The coumn names.
   */

  protected String[] columnNames;

  /**
   * The current row.
   */

  protected int row;

  /**
   * Whether the last <code>getAsXxx</code> method returned a void value.
   */

  protected boolean wasVoid;

  /**
   * If this flag is true, requesting a non-existing column by one of the <code>getAsXxx</code>
   * methods results in an error. Otherwise, the methods behave as if the value in the column was
   * void.
   */

  protected boolean strict;

  /**
   * The time format pattern
   */

  protected String dateFormatPattern;

  /**
   * The default time format pattern.
   */

  public static final String DEFAULT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss S";

  /**
   * The date format to handle fields representing a date.
   */

  protected DateFormat dateFormat;

  // --------------------------------------------------------------------------------
  // h1: Strict flag, time format
  // --------------------------------------------------------------------------------

  /**
   * Returns the "strict" flag. If this flag is true, requesting a non-existing column by one
   * of the <code>getAsXxx</code> methods results in an error. Otherwise, the methods behave
   * as if the value in the column was void. Default is strict = false.
   */

  public boolean getStrict ()
  {
    return this.strict;
  }

  /**
   * Sets the "strict" flag. If this flag is true, requesting a non-existing column by one
   * of the <code>getAsXxx</code> methods results in an error. Otherwise, the methods behave
   * as if the value in the column was void. Default is strict = false.
   */

  public void setStrict (boolean strict)
  {
    this.strict = strict;
  }

  /**
   * Sets the date format pattern to handle fields representing a date.
   */

  public void setDateFormat (String pattern)
  {
    this.dateFormatPattern = pattern;
  }

  /**
   * Resets the time format to its default value. 
   */

  public void resetDateFormat ()
  {
    this.dateFormat = null;
    this.dateFormatPattern = DEFAULT_TIME_PATTERN;
  }

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Resets this instance, but leaves the time format unchanged. The following
   * happens:
   * <ul>
   * <li>The data and column names are released (set to null)</li>
   * <li>The cursor and the <em>was-void</em> flag are reset</li>
   * </ul>
   */

  public void reset ()
  {
    this.data = null;
    this.columnNames = null;
    this.row = -1;
    this.strict = false;
    this.wasVoid = false;
  }

  /**
   * Creates a new instance.
   */

  public CSVDataImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
    this.reset();
    this.resetDateFormat();
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
    this.resetDateFormat();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
    this.resetDateFormat();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Parsing
  // --------------------------------------------------------------------------------

  /**
   * Parses the input from the specified reader and stores it internally.
   *
   * @param reader the reader from which the CSV data is read
   * @param separator the value separator (e.g., <code>','</code> or <code>';'</code>)
   */

  public void parse (Reader reader, final char separator)
    throws IOException 
  {
    this.reset();

    List<String> record = new ArrayList<String>();
    Deque<String[]> result = new LinkedList<String[]>();
    StringBuilder buffer = new StringBuilder();

    final int NORMAL = 0;
    final int SINGLE_QUOTED = 1;
    final int DOUBLE_QUOTED = 2;
    int state = NORMAL;

    boolean protectChar = false;
    boolean protectNextChar = false;

    String lineSeparator = System.getProperty("line.separator");
    String line;

    BufferedReader bufferedReader =
      (reader instanceof BufferedReader
       ? (BufferedReader)reader
       : new BufferedReader(reader));

    while ( (line = bufferedReader.readLine()) != null )
      {
        if ( line.trim().length() == 0 ) continue;
            
        for (char c : line.toCharArray())
          {
            protectChar = protectNextChar;
            protectNextChar = false;
                
            if ( c == separator )
              {
                if (  protectChar || state == SINGLE_QUOTED || state == DOUBLE_QUOTED )
                  buffer.append(c);
                else
                  {
                    record.add(buffer.toString());
                    buffer = new StringBuilder();
                  }
              }
            else if ( c == '"' )
              {
                if ( protectChar || state == SINGLE_QUOTED )
                  buffer.append(c);
                else if ( state == DOUBLE_QUOTED )
                  state = NORMAL;
                else
                  state = DOUBLE_QUOTED;
              }
            else if ( c == '\'' )
              {
                if ( protectChar || state == DOUBLE_QUOTED )
                  buffer.append(c);
                else if ( state == SINGLE_QUOTED )
                  state = NORMAL;
                else
                  state = SINGLE_QUOTED;
              }
            else if ( c == '\\' )
              {
                protectNextChar = true;
              }
            else
              {
                buffer.append(c);
              }
          }
            
        if ( protectNextChar || state == SINGLE_QUOTED || state == DOUBLE_QUOTED )
          {
            buffer.append(lineSeparator);
            protectNextChar = false;
          }
        else
          {
            record.add(buffer.toString());
            result.add(record.toArray(new String[record.size()]));
            record.clear();
            buffer = new StringBuilder();
          }
      }

    this.columnNames = result.removeFirst();
    this.data = result.toArray(new String[result.size()][]);
  }

  /**
   * Parses the input from the specified file and stores it internally.
   *
   * @param file the file from which the CSV data is read
   * @param separator the value separator (e.g., <code>','</code> or <code>';'</code>)
   */
  
  public void parse (File file, final char separator)
    throws IOException
  {
    this.parse(new FileReader(file), separator);
  }

  /**
   * Parses the input from the specified input stream and stores it internally.
   *
   * @param in the input stream from which the CSV data is read
   * @param separator the value separator (e.g., <code>','</code> or <code>';'</code>)
   */
  
  public void parse (InputStream in, final char separator)
    throws IOException
  {
    this.parse(new InputStreamReader(in), separator);
  }

  // --------------------------------------------------------------------------------
  // h1: Moving the cursor
  // --------------------------------------------------------------------------------

  /**
   * Moves the cursor to the first row. Returns true whenever this is possible, i.e.,
   * when there is at least one row in this data set. If there are no rows in this
   * data set, returns false.
   */

  public boolean first ()
  {
    if ( this.data.length > 0 )
      {
        this.row = 0;
        return true;
      }
    else
      {
        this.row = -1;
        return false;
      }
  }

  /**
   * Moves the cursor to the last row. Returns true whenever this is possible, i.e.,
   * when there is at least one row in this data set. If there are no rows in this
   * data set, returns false.
   */

  public boolean last ()
  {
    if ( this.data.length > 0 )
      {
        this.row = this.data.length - 1;
        return true;
      }
    else
      {
        this.row = -1;
        return false;
      }
  }

  /**
   * Changes the cursor position by the specified number. If the number is positive, the
   * cursor moves forward, otherwise backward. If the new cursor position is lower than
   * the first or greater than the last position, the curser is put before the first resp.
   * after the last row. Returns true if the new curser position is a valid row, and false
   * of the cursor is before the first or after the last row.
   */

  public boolean relative (int num)
  {
    this.row += num;
    if ( this.row < 0 )
      {
        this.row = -1;
        return false;
      }
    else if ( this.row >= this.data.length )
      {
        this.row = this.data.length;
        return false;
      }
    else
      return true;
  }

  /**
   * Moves the cursor one row forward. Returns true whenever this is possible, i.e.,
   * when there is a next row. If there is none, returns false. In the latter case,
   * the cursor is after the last row afterwards.
   */

  public boolean next ()
  {
    return this.relative(1);
  }

  /**
   * Moves the cursor one row backward. Returns true whenever this is possible, i.e.,
   * when there is a previous row. If there is none, returns false. In the latter case,
   * the cursor is before the first row afterwards.
   */

  public boolean previous ()
  {
    return this.relative(-1);
  }

  /**
   * Moves the cursor before the first row.
   */

  public void beforeFirst ()
  {
    this.row = -1;
  }

  /**
   * Moves the cursor after the last row.
   */

  public void afterLast ()
  {
    this.row = this.data.length;
  }

  // --------------------------------------------------------------------------------
  // h1: Retrievng data
  // --------------------------------------------------------------------------------

  /**
   * Returns the value of the specified column in the current row as a string.
   *
   * @param column the column index
   *
   * @return the value of the column, or the empty string if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public String getAsString (int column)
    throws CSVException
  {
    String value = "";
    if ( column >= 0 && column < this.data[this.row].length )
      value = this.data[this.row][column];
    else if ( this.strict )
      throw new CSVException("No such column number: " + column);
    this.wasVoid = value.equals("");
    return value;
  }

  /**
   * Returns the value of the specified column in the current row as a string.
   *
   * @param columnName the column name
   *
   * @return the value of the column, or the empty string if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public String getAsString (String columnName)
    throws CSVException
  {
    return this.getAsString(this.getIndexOfColumn(columnName));
  }

  /**
   * Returns the value of the specified column in the current row as an integer.
   *
   * @param column the column index
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public int getAsInt (int column)
    throws CSVException
  {
    String stringValue = this.getAsString(column);
    return (this.wasVoid ? 0 : Integer.parseInt(stringValue));
  }

  /**
   * Returns the value of the specified column in the current row as an integer.
   *
   * @param columnName the column name
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public int getAsInt (String columnName)
    throws CSVException
  {
    return this.getAsInt(this.getIndexOfColumn(columnName));
  }

  /**
   * Returns the value of the specified column in the current row as a
   * <code>long</code> number.
   *
   * @param column the column index
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public long getAsLong (int column)
    throws CSVException
  {
    String stringValue = this.getAsString(column);
    return (this.wasVoid ? 0 : Long.parseLong(stringValue));
  }

  /**
   * Returns the value of the specified column in the current row as a
   * <code>long</code> number.
   *
   * @param columnName the column name
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public long getAsLong (String columnName)
    throws CSVException
  {
    return this.getAsLong(this.getIndexOfColumn(columnName));
  }

  /**
   * Returns the value of the specified column in the current row as a
   * <code>float</code> number.
   *
   * @param column the column index
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public float getAsFloat (int column)
    throws CSVException
  {
    String stringValue = this.getAsString(column);
    return (this.wasVoid ? 0 : Float.parseFloat(stringValue));
  }

  /**
   * Returns the value of the specified column in the current row as a
   * <code>float</code> number.
   *
   * @param columnName the column name
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public float getAsFloat (String columnName)
    throws CSVException
  {
    return this.getAsFloat(this.getIndexOfColumn(columnName));
  }

  /**
   * Returns the value of the specified column in the current row as a
   * <code>double</code> number.
   *
   * @param column the column index
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public double getAsDouble (int column)
    throws CSVException
  {
    String stringValue = this.getAsString(column);
    return (this.wasVoid ? 0 : Double.parseDouble(stringValue));
  }

  /**
   * Returns the value of the specified column in the current row as a
   * <code>double</code> number.
   *
   * @param columnName the column name
   *
   * @return the value of the column, or 0 if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public double getAsDouble (String columnName)
    throws CSVException
  {
    return this.getAsDouble(this.getIndexOfColumn(columnName));
  }

  /**
   * Returns the value of the specified column in the current row as a
   * {@link Date Date} object.
   *
   * @param column the column index
   *
   * @return the value of the column, or null if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public Date getAsDate (int column)
    throws CSVException
  {
    String stringValue = this.getAsString(column);
    if ( this.wasVoid )
      return null;
    else
      {
        if ( this.dateFormat == null )
          this.dateFormat = new SimpleDateFormat(this.dateFormatPattern);
        try
          {
            return this.dateFormat.parse(stringValue);
          }
        catch (Exception exception)
          {
            throw new CSVException("Failed to parse date value", exception);
          }
      }
  }

  /**
   * Returns the value of the specified column in the current row as a
   * {@link Date Date} object.
   *
   * @param columnName the column name
   *
   * @return the value of the column, or null if the column is empty.
   *
   * @throws CSVException if the column does not exist
   */

  public Date getAsDate (String columnName)
    throws CSVException
  {
    return this.getAsDate(this.getIndexOfColumn(columnName));
  }

  /**
   * Returns true if the last <code>getAsXxx</code> method call returned a void
   * value.
   */

  public boolean wasVoid ()
  {
    return this.wasVoid;
  }

  // --------------------------------------------------------------------------------
  // h1: Identification
  // --------------------------------------------------------------------------------

  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "CSVDataImpl" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status.
   */

  public String getIdentification ()
  {
    return
      "CSVDataImpl" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }

  // --------------------------------------------------------------------------------
  // h1: Utilities
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the column with the specified name exists, otherwise false.
   */

  public boolean hasColumn (String columnName)
  {
    return ( getIndexOfColumn(columnName) != -1 );
  }

  /**
   * Returns the index of the column with the specified name, or -1 if no such column exists.
   */

  protected int getIndexOfColumn (String columnName)
  {
    for (int i = 0; i < this.columnNames.length; i++)
      {
        if ( columnNames[i].equals(columnName) )
          return i;
      }
    return -1;
  }
}

