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

import java.io.PrintStream;
import java.util.Arrays;

/**
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: StatusPrinter.java,v 1.1 2007/04/15 00:03:53 rassy Exp $</code>
 */

public class StatusPrinter
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Indicates that the statuses should be aligned to the left.
   */

  public static final int LEFT = 0;

  /**
   * Indicates that the statuses should be aligned to the right.
   */

  public static final int RIGHT = 1;

  /**
   * Indicates that the statuses should be centered.
   */

  public static final int CENTER = 2;

  /**
   * The stream the output goes to.
   */

  protected PrintStream out;

  /**
   * The total width of the output
   */

  protected int width;

  /**
   * The width of the status field.
   */

  protected int statusWidth;

  /**
   * The alignment of the statuses.
   */

  protected int statusAlign;

  /**
   * Whether this status printer creates colored output.
   */

  protected boolean colorEnabled;

  /**
   * Current column of the output.
   */

  protected int column = 0;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specifed print stream, width, status field width,
   * status alignment, and color flag.
   */

  public StatusPrinter (PrintStream out, int width, int statusWidth, int statusAlign,
                        boolean colorEnabled)
  {
    if ( out == null )
      throw new NullPointerException ("Print stream is null");
    this.out = out;

    if ( width < 0 )
      throw new IllegalArgumentException
        ("Illegal width value: " + width + " (must be positive)");
    this.width = width;

    if ( statusWidth < 0 )
      throw new IllegalArgumentException
        ("Illegal status width value: " + statusWidth + " (must be positive)");
    this.statusWidth = statusWidth;

    if ( statusAlign != LEFT && statusAlign != RIGHT && statusAlign != CENTER )
      throw new IllegalArgumentException
        ("Illegal status align value: " + statusAlign +
         " (must be " + LEFT + ", " + RIGHT + ", or " + CENTER + ")");
    this.statusAlign = statusAlign;

    this.colorEnabled = colorEnabled;
  }

  // --------------------------------------------------------------------------------
  // Printing
  // --------------------------------------------------------------------------------

  /**
   * Prints the specified key.
   */

  public void printKey (String key)
  {
    if ( this.column != 0 )
      {
        this.out.println();
        this.column = 0;
      }
    this.out.print(key);
    this.column = key.length();
  }

  /**
   * Prints the specified status. The parameter <code>vt100esc</code> should be either null
   * or a VT 100 escape sequence as defined in {@link VT100Esc VT100Esc}. If null or the
   * global color flag ({@link #colorEnabled colorEnabled}) is false, it is
   * ignored. Otherwise, it is printed before the status, and 
   * {@link VT100Esc#RESET VT100Esc.RESET} is printed after the status.
   */

  public void printStatus (String status, String vt100esc)
  {
    // Calculate how many spaces must be printed:
    int space = 0;
    switch ( this.statusAlign )
      {
      case LEFT:
        space = this.width - this.column - this.statusWidth;
        break;
      case RIGHT:
        space = this.width - this.column - status.length();
        break;
      case CENTER:
        int margin = this.statusWidth - status.length();
        if ( margin < 0 ) margin = 0;
        space = this.width - this.column - this.statusWidth + margin/2;
      }
    if ( space < 1 ) space = 1;

    // Print the spaces:
    char[] spaceChars = new char[space];
    Arrays.fill(spaceChars, ' ');
    this.out.print(spaceChars);

    // Print VT 100 escape sequence if necessay:
    if ( this.colorEnabled && vt100esc != null ) this.out.print(vt100esc);

    // Print status:
    this.out.print(status);

    // Resett VT 100 escape sequence if necessay:
    if ( this.colorEnabled && vt100esc != null ) this.out.print(VT100Esc.RESET);

    // Newline:
    this.out.println();

    // Update column:
    this.column = 0;
  }

  /**
   * Prints the specified key and status. The parameter <code>vt100esc</code> should be
   * either null or a VT 100 escape sequence as defined in {@link VT100Esc VT100Esc}. If
   * null or the global color flag ({@link #colorEnabled colorEnabled}) is false, it is
   * ignored. Otherwise, it is printed before the status, and 
   * {@link VT100Esc#RESET VT100Esc.RESET} is printed after the status.
   * 
   */

  public void print (String key, String status, String vt100esc)
  {
    this.printKey(key);
    this.printStatus(status, vt100esc);
  }
}
