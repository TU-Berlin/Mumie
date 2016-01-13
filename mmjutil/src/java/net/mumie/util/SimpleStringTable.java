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

package net.mumie.util;

import java.io.IOException;


/**
 * <p>
 *   Allows the construction of simple text tables. These would look like the following:
 *     <pre>
 *
 *         Name       | Width | Heigth
 *        ------------+-------+--------
 *         foo.png    |   345 |    566
 *         bar_01.png |    45 |   1005
 *         bar_02.png |    59 |    992
 *
 *     </pre>
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Support for VT 100 escape sequences is based on the class ColorStringTable
 *   written by Fiete Meyer
 *   <a href="mailto:fmeyer@math.tu-berlin.de">fmeyer@math.tu-berlin.de</a>
 * @version <code>$Id: SimpleStringTable.java,v 1.4 2007/04/15 00:03:15 rassy Exp $</code>
 */

public class SimpleStringTable
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Alignment specifier meaning "left-justified".
   */

  public static final int LEFT = 0;

  /**
   * Alignment specifier meaning "right-justified".
   */

  public static final int RIGHT = 1;

  /**
   * Alignment specifier meaning "centered".
   */

  public static final int CENTER = 2;

  /**
   * Number of columns.
   */

  public final int COLUMNS;

  /**
   * Number of rows.
   */

  public final int ROWS;

  /**
   * Index of the last column, i.e., {@link #COLUMNS COLUMNS} - 1.
   */

  public final int LAST_COLUMN;

  /**
   * Index of the last row, i.e., {@link #ROWS ROWS} - 1.
   */

  public final int LAST_ROW;

  /**
   * Two-dimensional array representing the table.
   */

  protected String[][] table;

  /**
   * Holds an optional VT100 escape sequence for each table cell.
   */

  protected String[][] vt100escs;

  /**
   * Whether the table has a left border.
   */

  protected boolean leftBorder;

  /**
   * Specifies which columns are separated by borders. If
   * <code>columnBorders[<var>i</var>]</code> is <code>true</code>, then a border is placed
   * between column <var>i</var> and <code><var>i</var>+1</code>, otherwise not.
   */

  protected boolean[] columnBorders;

  /**
   * Whether the table has a right border.
   */

  protected boolean rightBorder;

  /**
   * Whether the table has a top border.
   */

  protected boolean topBorder;

  /**
   * Specifies which rows are separated by borders. If <code>rowBorders[<var>i</var>]</code>
   * is <code>true</code>, then a border is placed between row <var>i</var> and
   * <code><var>i</var>+1</code>, otherwise not.
   */

  protected boolean[] rowBorders;

  /**
   * Whether the table has a bottom border.
   */

  protected boolean bottomBorder;

  /**
   * The left paddings of each column. <code>leftPaddings[<var>i</var>]</code> is the number
   * of space characters added at the left side of each entry of the <var>i</var>th column.
   */

  protected int[] leftPaddings;

  /**
   * The right paddings of each column. <code>rightPaddings[<var>i</var>]</code> is the
   * number of space characters added at the right side of each entry of the <var>i</var>th
   * column.
   */

  protected int[] rightPaddings;

  /**
   * Character used to draw horizontal border lines. Default is <code>'-'</code>.
   */

  protected char horizontalBorderSymbol;

  /**
   * Character used to draw vertical border lines. Default is <code>'|'</code>.
   */

  protected char verticalBorderSymbol;

  /**
   * Character used to draw an intersection of a horizontal and a vertical border
   * line. Default is <code>'+'</code>. 
   */

  protected char intersectionBorderSymbol;

  /**
   * Auxiliary variable. Stores which columns have already been
   * formatted. <code>columnsFormatted[<var>i</var>] == true</code> means the <var>i</var>th
   * column has already been formatted, <code>columnsFormatted[<var>i</var>] == false</code>
   * means it has not.
   */

  protected boolean[] columnsFormatted;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>SimpleStringTable</code> with <code>rows</code> rows and
   * <code>columns</code> columns.
   */

  public SimpleStringTable (int rows, int columns)
  {
    this.COLUMNS = columns;
    this.ROWS = rows;
    this.LAST_COLUMN = columns - 1;
    this.LAST_ROW = rows - 1;
    this.table = new String[this.ROWS][this.COLUMNS];
    this.vt100escs = null;
    this.columnBorders = new boolean[this.COLUMNS];
    this.rowBorders = new boolean[this.ROWS];
    this.leftPaddings = new int[this.COLUMNS];
    this.rightPaddings = new int[this.COLUMNS];
    this.setLeftPaddings(1);
    this.setRightPaddings(1);
    this.setOuterBorders(false, false, false, false);
    this.setRowBorders(false);
    this.setColumnBorders(false);
    this.setBorderSymbols('-', '|', '+');
    this.columnsFormatted =  new boolean[this.COLUMNS];
    for (int column = 0; column < this.COLUMNS; column++)
      this.columnsFormatted[column] = false;
  }

  // --------------------------------------------------------------------------------
  // Borders
  // --------------------------------------------------------------------------------

  /**
   * Sets whether the table has a left border.
   */

  public void setLeftBorder (boolean leftBorder)
  {
    this.leftBorder = leftBorder;
  }

  /**
   * Sets wether borders are displayed between all columns.
   */

  public void setColumnBorders (boolean columnBorders)
  {
    for (int column = 0; column < this.COLUMNS; column++)
      this.columnBorders[column] = columnBorders;
  }

  /**
   * Sets wether a border is printed  between column <code>column</code> and
   * <code>column+1</code> (see {@link #columnBorders columnBorders}).
   */

  public void setColumnBorder (int column, boolean columnBorder)
  {
    this.columnBorders[column] = columnBorder;
  }

  /**
   * Sets whether the table has a right border.
   */

  public void setRightBorder (boolean rightBorder)
  {
    this.rightBorder = rightBorder;
  }

  /**
   * Sets whether the table has a top border.
   */

  public void setTopBorder (boolean topBorder)
  {
    this.topBorder = topBorder;
  }

  /**
   * Sets wether borders are displayed between all rows.
   */

  public void setRowBorders (boolean rowBorders)
  {
    for (int row = 0; row < this.ROWS; row++)
      this.rowBorders[row] = rowBorders;
  }

  /**
   * Sets wether a border is printed  between row <code>row</code> and
   * <code>row+1</code> (see {@link #rowBorders rowBorders}).
   */

  public void setRowBorder (int row, boolean rowBorder)
  {
    this.rowBorders[row] = rowBorder;
  }

  /**
   * Sets whether the table has a bottom border.
   */

  public void setBottomBorder (boolean bottomBorder)
  {
    this.bottomBorder = bottomBorder;
  }

  /**
   * Sets wether the tables has top, right, bottom, and left boders.
   */

  public void setOuterBorders (boolean top, boolean right, boolean bottom, boolean left)
  {
    this.topBorder = top;
    this.rightBorder = right;
    this.bottomBorder = bottom;
    this.leftBorder = left;
  }

  /**
   * Sets the characters used to draw the borders.
   */

  public void setBorderSymbols (char horizontal, char vertical, char intersection)
  {
    this.horizontalBorderSymbol = horizontal;
    this.verticalBorderSymbol = vertical;
    this.intersectionBorderSymbol = intersection;
  }

  // --------------------------------------------------------------------------------
  // Padding
  // --------------------------------------------------------------------------------

  /**
   * Sets the left padding of the <code>column</code>-th column to <code>padding</code>
   * spaces. 
   */

  public void setLeftPadding (int column, int padding)
  {
    this.leftPaddings[column] = padding;
  }

  /**
   * Sets the left padding of all column to <code>padding</code> spaces.
   */

  public void setLeftPaddings (int padding)
  {
    for (int column = 0; column < this.COLUMNS; column++)
      this.leftPaddings[column] = padding;
  }

  /**
   * Sets the right padding of the <code>column</code>-th column to <code>padding</code>
   * spaces. 
   */

  public void setRightPadding (int column, int padding)
  {
    this.rightPaddings[column] = padding;
  }

  /**
   * Sets the right padding of all column to <code>padding</code> spaces.
   */

  public void setRightPaddings (int padding)
  {
    for (int column = 0; column < this.COLUMNS; column++)
      this.rightPaddings[column] = padding;
  }

  // --------------------------------------------------------------------------------
  // Alignment
  // --------------------------------------------------------------------------------

  /**
   * Returns the width of the <code>column</code>-th column. This is the length of the
   * longest entry in this column. The length of an entry is it length as a string with
   * leading and trailing whitespaces ignored.
   */

  public int getColumnWidth (int column)
  {
    int maxWidth = 0;
    for (int row = 0; row < this.ROWS; row++)
      {
	int width = this.table[row][column].trim().length();
	if ( width > maxWidth ) maxWidth = width;
      }
    return maxWidth;
  }

  /**
   * Aligns the specified column left-justified.
   */

  public void formatColumnLeft (int column)
  {
    int maxWidth = this.getColumnWidth(column);
    for (int row = 0; row < this.ROWS; row++)
      {
	String cell = this.table[row][column].trim();
	this.table[row][column] = cell + this.makeFill(maxWidth - cell.length(), ' ');
      }
    this.columnsFormatted[column] = true;
  }

  /**
   * Aligns the specified column right-justified.
   */

  public void formatColumnRight (int column)
  {
    int maxWidth = this.getColumnWidth(column);
    for (int row = 0; row < this.ROWS; row++)
      {
	String cell = this.table[row][column].trim();
	this.table[row][column] = this.makeFill(maxWidth - cell.length(), ' ') + cell;
      }
    this.columnsFormatted[column] = true;
  }

  /**
   * Centers the specified column.
   */

  public void formatColumnCenter (int column)
  {
    int maxWidth = this.getColumnWidth(column);
    for (int row = 0; row < this.ROWS; row++)
      {
	String cell = this.table[row][column].trim();
	int space = maxWidth - cell.length();
	int spaceLeft = space / 2;
	int spaceRight = space - spaceLeft;
	this.table[row][column] =
	  this.makeFill(spaceLeft, ' ') + cell + this.makeFill(spaceRight, ' ');
      }
    this.columnsFormatted[column] = true;
  }

  /**
   * Formats the specified column according to the specified alignment. The latter must be
   * one of the constants {@link #LEFT LEFT}, {@link #RIGHT RIGHT}, and
   * {@link #CENTER CENTER}.
   */

  public void formatColumn (int column, int alignment)
  {
    switch (alignment)
      {
        case LEFT   : this.formatColumnLeft(column); break;
        case RIGHT  : this.formatColumnRight(column); break;
        case CENTER : this.formatColumnCenter(column); break;
        default : throw new IllegalArgumentException
		    ("Invalid alignment specifier: " + alignment);
      }
  }

  // --------------------------------------------------------------------------------
  // VT 100 escape sequences
  // --------------------------------------------------------------------------------

  /**
   * Sets the VT 100 escape sequences of the cell with the specified row and column to the
   * specified value.
   */

  public void setVT100esc (int row, int column, String vt100esc)
  {
    if ( vt100esc == null )
      return;
    if ( this.vt100escs == null )
      this.vt100escs = new String[this.ROWS][this.COLUMNS];
    this.vt100escs[row][column] = vt100esc;
  }

  // --------------------------------------------------------------------------------
  // Setting cell entries
  // --------------------------------------------------------------------------------

  /**
   * Sets the cell with the specified row and column to the specified object. The actual
   * cell entry is obtained by object's the <code>toString</code> method.
   */

  public void set (int row, int column, Object object)
  {
    this.table[row][column] = object.toString();
  }

  /**
   * Sets the cell with the specified row and column to the specified object. The actual
   * cell entry is obtained by object's the <code>toString</code> method. If
   * <code>vt100esc</code> is not null, the VT 100 sequence of the cell is set to that
   * value.
   */

  public void set (int row, int column, Object object, String vt100esc)
  {
    this.set(row, column, object);
    this.setVT100esc(row, column, vt100esc);
  }

  /**
   * Sets the cell with the specified row and column to the specified <code>int</code>
   * variable. 
   */

  public void set (int row, int column, int number)
  {
    this.set(row, column, new Integer(number));
  }

  /**
   * Sets the cell with the specified row and column to the specified <code>int</code>
   * variable. If <code>vt100esc</code> is not null, the VT 100 sequence of the cell is set
   * to that value.
   */

  public void set (int row, int column, int number, String vt100esc)
  {
    this.set(row, column, new Integer(number), vt100esc);
  }

  /**
   * Sets the cell with the specified row and column to the specified <code>long</code>
   * variable. 
   */

  public void set (int row, int column, long number)
  {
    this.set(row, column, new Long(number));
  }

  /**
   * Sets the cell with the specified row and column to the specified <code>long</code>
   * variable. If <code>vt100esc</code> is not null, the VT 100 sequence of the cell is set
   * to that value.
   */

  public void set (int row, int column, long number, String vt100esc)
  {
    this.set(row, column, new Long(number), vt100esc);
  }

  /**
   * Sets the cell with the specified row and column to the specified <code>boolean</code>
   * variable. 
   */

  public void set (int row, int column, boolean value)
  {
    this.set(row, column, new Boolean(value));
  }

  /**
   * Sets the cell with the specified row and column to the specified <code>boolean</code>
   * variable. If <code>vt100esc</code> is not null, the VT 100 sequence of the cell is set
   * to that value.
   */

  public void set (int row, int column, boolean value, String vt100esc)
  {
    this.set(row, column, new Boolean(value), vt100esc);
  }

  // --------------------------------------------------------------------------------
  // Output
  // --------------------------------------------------------------------------------

  /**
   * Outputs the table to the specfied {@link Appendable Appendable} object.
   */

  public void printTo (Appendable out)
    throws IOException 
  {
    final String lineSeperator = System.getProperty("line.separator");
    for (int column = 0; column < this.COLUMNS; column++)
      {
	if ( ! this.columnsFormatted[column] ) this.formatColumn(column, LEFT);
      }
    if ( this.topBorder ) this.printHorizontalBorder(out, 0);
    for (int row = 0; row < this.ROWS; row++)
      {
	if ( this.leftBorder ) out.append(this.verticalBorderSymbol);
	for (int column = 0; column < this.COLUMNS; column++)
	  {
            String content = this.table[row][column];
            String leftSpace = this.makeFill(this.leftPaddings[column], ' ');
            String rightSpace = this.makeFill(this.rightPaddings[column], ' ');
            String vt100esc = (this.vt100escs != null ? this.vt100escs[row][column] : null);
            out.append(leftSpace);
            if ( vt100esc != null ) out.append(vt100esc);
            out.append(content);
            if ( vt100esc != null ) out.append(VT100Esc.RESET);
            out.append(rightSpace);
	    if ( ( column < this.LAST_COLUMN ) && ( this.columnBorders[column] ) )
	      out.append(this.verticalBorderSymbol);
	  }
	if ( this.rightBorder ) out.append(this.verticalBorderSymbol);
	out.append(lineSeperator);
	if ( ( row < this.LAST_ROW ) && ( this.rowBorders[row] ) )
	  this.printHorizontalBorder(out, row);
      }
    if ( this.bottomBorder ) this.printHorizontalBorder(out, 0);
  }

  /**
   * Returns the table, as a string.
   */

  public String toString ()
  {
    try
      {
        StringBuffer buffer = new StringBuffer();
        this.printTo(buffer);
        return buffer.toString();
      }
    catch (Exception exception)
      {
        throw new RuntimeException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliary methods
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary method. Returns a string made of <code>size</code> characters
   * <code>symbol</code>.
   */

  protected static String makeFill (int size, char symbol)
  {
    char[] space = new char[size];
    for (int i = 0; i < size; i++)
      space[i] = symbol;
    return new String(space);
  }

  /**
   * Prints a horizontal border above the specified row to the specified
   * {@link Appendable Appendable} object. This is used in the {@link #printTo printTo}
   * method.
   */

  protected void printHorizontalBorder (Appendable out, int row)
    throws IOException 
  {
    if ( this.leftBorder ) out.append(this.intersectionBorderSymbol);
    for (int column = 0; column < this.COLUMNS; column++)
      {
	int length =
          this.leftPaddings[column]
          + this.table[row][column].length()
          + this.rightPaddings[column];
	out.append(this.makeFill(length, this.horizontalBorderSymbol));
	if ( ( column < this.LAST_COLUMN ) && ( this.columnBorders[column] ) )
	  out.append(this.intersectionBorderSymbol);
      }
    if ( this.rightBorder ) out.append(this.intersectionBorderSymbol);
    out.append("\n");
  }
}
