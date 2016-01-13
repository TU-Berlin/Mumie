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
import java.util.Date;
import java.util.Deque;
import java.util.List;
import net.mumie.cocoon.util.Identifyable;

public interface CSVData
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>CSVData.class.getName()</code>).
   */

  public static String ROLE = CSVData.class.getName();

  // --------------------------------------------------------------------------------
  // h1: Time format
  // --------------------------------------------------------------------------------

  /**
   * Sets the date format pattern to handle fields representing a date.
   */

  public void setDateFormat (String pattern);

  /**
   * Resets the time format to its default value. 
   */

  public void resetDateFormat ();

  // --------------------------------------------------------------------------------
  // h1: Strict flag, time format
  // --------------------------------------------------------------------------------

  /**
   * Returns the "strict" flag. If this flag is true, requesting a non-existing column by one
   * of the <code>getAsXxx</code> methods results in an error. Otherwise, the methods behave
   * as if the value in the column was void. Default is strict = false.
   */

  public boolean getStrict ();

  /**
   * Sets the "strict" flag. If this flag is true, requesting a non-existing column by one
   * of the <code>getAsXxx</code> methods results in an error. Otherwise, the methods behave
   * as if the value in the column was void. Default is strict = false.
   */

  public void setStrict (boolean strict);

  /**
   * Resets this instance, but leaves the time format unchanged. The following
   * happens:
   * <ul>
   * <li>The data and column names are released (set to null)</li>
   * <li>The cursor and the <em>was-void</em> flag are reset</li>
   * </ul>
   */

  public void reset ();

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
    throws IOException;

  /**
   * Parses the input from the specified file and stores it internally.
   *
   * @param file the file from which the CSV data is read
   * @param separator the value separator (e.g., <code>','</code> or <code>';'</code>)
   */
  
  public void parse (File file, final char separator)
    throws IOException;

  /**
   * Parses the input from the specified input stream and stores it internally.
   *
   * @param in the input stream from which the CSV data is read
   * @param separator the value separator (e.g., <code>','</code> or <code>';'</code>)
   */
  
  public void parse (InputStream in, final char separator)
    throws IOException;

  // --------------------------------------------------------------------------------
  // h1: Moving the cursor
  // --------------------------------------------------------------------------------

  /**
   * Moves the cursor to the first row. Returns true whenever this is possible, i.e.,
   * when there is at least one row in this data set. If there are no rows in this
   * data set, returns false.
   */

  public boolean first ();

  /**
   * Moves the cursor to the last row. Returns true whenever this is possible, i.e.,
   * when there is at least one row in this data set. If there are no rows in this
   * data set, returns false.
   */

  public boolean last ();

  /**
   * Changes the cursor position by the specified number. If the number is positive, the
   * cursor moves forward, otherwise backward. If the new cursor position is lower than
   * the first or greater than the last position, the curser is put before the first resp.
   * after the last row. Returns true if the new curser position is a valid row, and false
   * of the cursor is before the first or after the last row.
   */

  public boolean relative (int num);

  /**
   * Moves the cursor one row forward. Returns true whenever this is possible, i.e.,
   * when there is a next row. If there is none, returns false. In the latter case,
   * the cursor is after the last row afterwards.
   */

  public boolean next ();

  /**
   * Moves the cursor one row backward. Returns true whenever this is possible, i.e.,
   * when there is a previous row. If there is none, returns false. In the latter case,
   * the cursor is before the first row afterwards.
   */

  public boolean previous ();

  /**
   * Moves the cursor before the first row.
   */

  public void beforeFirst ();

  /**
   * Moves the cursor after the last row.
   */

  public void afterLast ();

  // --------------------------------------------------------------------------------
  // h1: Retrieving data
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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

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
    throws CSVException;

  /**
   * Returns true if the last <code>getAsXxx</code> method call returned a void
   * value.
   */

  public boolean wasVoid ();
}

