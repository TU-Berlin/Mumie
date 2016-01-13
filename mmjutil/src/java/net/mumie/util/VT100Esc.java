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

/**
 * Provides VT 100 escape sequences for choosing colors and font attributes.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author (Based on the class ColorUtil written by Fiete Meyer
 *   <a href="mailto:fmeyer@math.tu-berlin.de">fmeyer@math.tu-berlin.de</a>)
 */

public class VT100Esc
{
  // --------------------------------------------------------------------------------
  // Start, end, and reset of escape sequences
  // --------------------------------------------------------------------------------

  /**
   * Introduces an escape sequence.
   */

  public static final String BEGIN = "\u001B[";

  /**
   * Closes an escape sequence.
   */

  public static final String END = "m";

  /**
   * Resets an escape sequence.
   */

  public static final String RESET = "\u001B[0m";

  // --------------------------------------------------------------------------------
  // Foreground colors
  // --------------------------------------------------------------------------------

  /**
   * Sets the foreground color to black.
   */

  public static final String FG_BLACK = BEGIN + "30" + END;

  /**
   * Sets the foreground color to red.
   */

  public static final String FG_RED = BEGIN + "31" + END;

  /**
   * Sets the foreground color to green.
   */

  public static final String FG_GREEN = BEGIN + "32" + END;

  /**
   * Sets the foreground color to yellow.
   */

  public static final String FG_YELLOW = BEGIN + "33" + END;

  /**
   * Sets the foreground color to blue.
   */

  public static final String FG_BLUE = BEGIN + "34" + END;

  /**
   * Sets the foreground color to magenta.
   */

  public static final String FG_MAGENTA = BEGIN + "35" + END;

  /**
   * Sets the foreground color to cyan.
   */

  public static final String FG_CYAN = BEGIN + "36" + END;

  /**
   * Sets the foreground color to white.
   */

  public static final String FG_WHITE = BEGIN + "37" + END;

  // --------------------------------------------------------------------------------
  // Background colors
  // --------------------------------------------------------------------------------

  /**
   * Sets the background color to black.
   */

  public static final String BG_BLACK = BEGIN + "40" + END;

  /**
   * Sets the background color to red.
   */

  public static final String BG_RED = BEGIN + "41" + END;

  /**
   * Sets the background color to green.
   */

  public static final String BG_GREEN = BEGIN + "42" + END;

  /**
   * Sets the background color to yellow.
   */

  public static final String BG_YELLOW = BEGIN + "43" + END;

  /**
   * Sets the background color to blue.
   */

  public static final String BG_BLUE = BEGIN + "44" + END;

  /**
   * Sets the background color to magenta.
   */

  public static final String BG_MAGENTA = BEGIN + "45" + END;

  /**
   * Sets the background color to cyan.
   */

  public static final String BG_CYAN = BEGIN + "46" + END;

  /**
   * Sets the background color to white.
   */

  public static final String BG_WHITE = BEGIN + "47" + END;

  // --------------------------------------------------------------------------------
  // Font attributes
  // --------------------------------------------------------------------------------

  /**
   * Brightens the text.
   */

  public static final String BRIGHT = BEGIN + "1" + END;

  /**
   * Dimms the text.
   */

  public static final String DIM = BEGIN + "2" + END;

  /**
   * Underscores the text.
   */

  public static final String UNDERSCORE = BEGIN + "4" + END;

  /**
   * Lets the text blink.
   */

  public static final String BLINK = BEGIN + "5" + END;

  /**
   * Reverses the colors.
   */

  public static final String REVERSE = BEGIN + "7" + END;

  /**
   * Makes the text invisible
   */

  public static final String HIDDEN = BEGIN + "8" + END;
}
