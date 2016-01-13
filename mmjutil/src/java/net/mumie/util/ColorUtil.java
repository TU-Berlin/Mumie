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
 * Provides various methods for generation an handling of escape sequences compliant with
 * the VT 100 standard. Allows choosing colors and attributes for output strings.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 *         Fiete Meyer <a href="mailto:fmeyer@math.tu-berlin.de">fmeyer@math.tu-berlin.de</a>
 *
 * @version <span class="file">$Id: ColorUtil.java,v 1.2 2008/04/28 23:58:46 rassy Exp $</span>
 *
 * @deprecated Use {@link VT100Esc} now.
 */

public class ColorUtil
{
  /**
   * Flag, specifying whether escape sequence precedes <code>String</code> to be formatted.
   */

  protected static final boolean PRECEDING = true;

  /**
   * Flag, specifying whether escape sequence precedes <code>String</code> to be formatted.
   */

  protected static final boolean FOLLOWING = false;

  /**
   * Escape sequence (begin) for color choosing in VT100-compatible Terminals.
   */

  protected static final String ESC_VT_BGN = "\u001B[";

  /**
   * Escape sequence (end) for color choosing in VT100-compatible Terminals.
   */

  protected static final String ESC_VT_END = "m";

  /**
   * Escape sequence (reset) for color choosing in VT100-compatible Terminals.
   */

  protected static final String ESC_VT_RST = "\u001B[0m";

  /**
   * Array containing terms describing colors.
   */

  protected static final String[] MM_COLORS = {"black", "red", "green", "yellow", "blue", "magenta", "cyan", "white"};

  /**
   * Array containing terms describing attributes.
   */

  protected static final String[] MM_ATTRIBUTES = {"bright", "dim", "underscore", "blink", "reverse", "hidden"};

  /**
   * Array containing background color codes for VT100-compatible Terminals.
   */

  protected static final byte[] VT_FORE_COLORS = {30, 31, 32, 33, 34, 35, 36, 37};

  /**
   * Array containing foreground color codes for VT100-compatible Terminals.
   */

  protected static final byte[] VT_BACK_COLORS = {40, 41, 42, 43, 44, 45, 46, 47};

  /**
   * Array containing various attribute codes for VT100-compatible Terminals.
   */

  protected static final byte[] VT_ATTRIBUTES = {1, 2, 4, 5, 7, 8};

  /**
   * Gets foreground color for specified color string.
   */

  public int getForeColor(String foreColor)
  {
    int color = 0;

    for(int number = 0; number < this.MM_COLORS.length; number++)
      {
        if ( this.MM_COLORS[number].equalsIgnoreCase(foreColor) )
          {
            color = this.VT_FORE_COLORS[number];
          }
      }

    return color;
  }

  /**
   * Gets background color for specified color string.
   */

  public int getBackColor(String backColor)
  {
    int color = 0;

    for(int number = 0; number < this.MM_COLORS.length; number++)
      {
        if ( this.MM_COLORS[number].equalsIgnoreCase(backColor) )
          {
            color = this.VT_BACK_COLORS[number];
          }
      }

    return color;
  }

  /**
   * Gets attribute for specified attribute string.
   */

  public int getAttribute(String attribute)
  {
    int attributeCode = 0;

    for(int number = 0; number < this.MM_ATTRIBUTES.length; number++)
      {
        if ( this.MM_ATTRIBUTES[number].equalsIgnoreCase(attribute) )
          {
            attributeCode = this.VT_ATTRIBUTES[number];
          }
      }

    return attributeCode;
  }

  /**
   * Returns VT sequence terminating string to be formatted.
   */

  public String buildVTSequence()
  {
    return ESC_VT_RST;
  }

  /**
   * Builds VT sequence including foreground color only.
   */

  public String buildVTSequence(int foreColor)
  {
    StringBuffer sequence = new StringBuffer("");

    sequence
    .append(ESC_VT_BGN)
    .append(foreColor)
    .append(ESC_VT_END);

    return sequence.toString();
  }

  /**
   * Builds VT sequence including foreground and background color.
   */

  public String buildVTSequence(int foreColor, int backColor)
  {
    StringBuffer sequence = new StringBuffer("");

    sequence
    .append(ESC_VT_BGN)
    .append(foreColor)
    .append(";")
    .append(backColor)
    .append(ESC_VT_END);

    return sequence.toString();
  }

  /**
   * Builds VT sequence including foreground color, background color and attribute.
   */

  public String buildVTSequence(int foreColor, int backColor, int attribute)
  {
    StringBuffer sequence = new StringBuffer("");

    sequence
    .append(ESC_VT_BGN)
    .append(foreColor)
    .append(";")
    .append(backColor)
    .append(";")
    .append(attribute)
    .append(ESC_VT_END);

    return sequence.toString();
  }

  /**
   * Builds VT sequence including foreground color only.
   */

  public String buildVTSequence(String foreColorName)
  {
    int foreColor = this.getForeColor(foreColorName);

    StringBuffer sequence = new StringBuffer("");

    sequence
    .append(ESC_VT_BGN)
    .append(foreColor)
    .append(ESC_VT_END);

    return sequence.toString();
  }

  /**
   * Builds VT sequence including foreground and background color.
   */

  public String buildVTSequence(String foreColorName, String backColorName)
  {
    int foreColor = this.getForeColor(foreColorName);
    int backColor = this.getBackColor(backColorName);

    StringBuffer sequence = new StringBuffer("");

    sequence
    .append(ESC_VT_BGN)
    .append(foreColor)
    .append(";")
    .append(backColor)
    .append(ESC_VT_END);

    return sequence.toString();
  }

  /**
   * Builds VT sequence including foreground color, background color and attribute.
   */

  public String buildVTSequence(String foreColorName, String backColorName, String attributeName)
  {
    int foreColor = this.getForeColor(foreColorName);
    int backColor = this.getBackColor(backColorName);
    int attribute = this.getAttribute(attributeName);

    StringBuffer sequence = new StringBuffer("");

    sequence
    .append(ESC_VT_BGN)
    .append(foreColor)
    .append(";")
    .append(backColor)
    .append(";")
    .append(attribute)
    .append(ESC_VT_END);

    return sequence.toString();
  }
}
