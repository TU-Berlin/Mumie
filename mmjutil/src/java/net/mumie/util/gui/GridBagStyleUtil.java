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

package net.mumie.util.gui;

import java.awt.GridBagConstraints;
import net.mumie.util.KeyValuePair;
import java.util.regex.Pattern;
import java.io.PrintStream;

public class GridBagStyleUtil
{
  /**
   * All possible <em>anchor</em> values.
   */

  protected static final int[] anchorValues = new int[]
    {
      GridBagConstraints.ABOVE_BASELINE,
      GridBagConstraints.ABOVE_BASELINE_LEADING,
      GridBagConstraints.ABOVE_BASELINE_TRAILING,
      GridBagConstraints.BASELINE,
      GridBagConstraints.BASELINE_LEADING,
      GridBagConstraints.BASELINE_TRAILING,
      GridBagConstraints.BELOW_BASELINE,
      GridBagConstraints.BELOW_BASELINE_LEADING,
      GridBagConstraints.BELOW_BASELINE_TRAILING,
      GridBagConstraints.CENTER,
      GridBagConstraints.EAST,
      GridBagConstraints.FIRST_LINE_END,
      GridBagConstraints.FIRST_LINE_START,
      GridBagConstraints.LAST_LINE_END,
      GridBagConstraints.LAST_LINE_START,
      GridBagConstraints.LINE_END,
      GridBagConstraints.LINE_START,
      GridBagConstraints.NORTH,
      GridBagConstraints.NORTHEAST,
      GridBagConstraints.NORTHWEST,
      GridBagConstraints.PAGE_END,
      GridBagConstraints.PAGE_START,
      GridBagConstraints.SOUTH,
      GridBagConstraints.SOUTHEAST,
      GridBagConstraints.SOUTHWEST,
      GridBagConstraints.WEST,
    };

  /**
   * All possible <em>anchor</em> names.
   */

  protected static final String[] anchorNames = new String[]
    {
      "ABOVE_BASELINE",
      "ABOVE_BASELINE_LEADING",
      "ABOVE_BASELINE_TRAILING",
      "BASELINE",
      "BASELINE_LEADING",
      "BASELINE_TRAILING",
      "BELOW_BASELINE",
      "BELOW_BASELINE_LEADING",
      "BELOW_BASELINE_TRAILING",
      "CENTER",
      "EAST",
      "FIRST_LINE_END",
      "FIRST_LINE_START",
      "LAST_LINE_END",
      "LAST_LINE_START",
      "LINE_END",
      "LINE_START",
      "NORTH",
      "NORTHEAST",
      "NORTHWEST",
      "PAGE_END",
      "PAGE_START",
      "SOUTH",
      "SOUTHEAST",
      "SOUTHWEST",
      "WEST",
    };

  /**
   * All possible <em>fill</em> values
   */

  protected static int[] fillValues = new int[]
    {
      GridBagConstraints.BOTH,
      GridBagConstraints.HORIZONTAL,
      GridBagConstraints.NONE,
      GridBagConstraints.VERTICAL,
    };

  /**
   * All possible <em>fill</em> names
   */

  protected static String[] fillNames = new String[]
    {
      "BOTH",
      "HORIZONTAL",
      "NONE",
      "VERTICAL",
    };

  /**
   * Pattern to split multi-valued properties.
   */

  protected static final Pattern splitPattern = Pattern.compile(",");

  /**
   * Adds the settings in the specified expressions to the specified constraints object.
   */

  public static final void add (GridBagConstraints style, String... expressions)
  {
    KeyValuePair datum = new KeyValuePair();
    for (String expression : expressions)
      {
        datum.parse(expression);
        String key = datum.getKey();
        String value = datum.getValue();
        if ( key.equals("anchor") )
          style.anchor = getAnchorValue(value);
        else if ( key.equals("gridx") )
          style.gridx = getGridValue(value);
        else if ( key.equals("gridy") )
          style.gridy = getGridValue(value);
        else if ( key.equals("gridxy") )
          {
            String[] items = splitValue(key, value, 2);
            style.gridx = getGridValue(items[0]);
            style.gridy = getGridValue(items[1]);
          }
        else if ( key.equals("gridwidth") )
          style.gridwidth = Integer.parseInt(value);
        else if ( key.equals("gridheight") )
          style.gridheight = Integer.parseInt(value);
        else if ( key.equals("gridspan") )
          {
            String[] items = splitValue(key, value, 2);
            style.gridwidth = Integer.parseInt(items[0]);
            style.gridheight = Integer.parseInt(items[1]);
          }
        else if ( key.equals("weightx") )
          style.weightx = Double.parseDouble(value);
        else if ( key.equals("weighty") )
          style.weighty = Double.parseDouble(value);
        else if ( key.equals("weight") )
          {
            String[] items = splitValue(key, value, 2);
            style.weightx = Double.parseDouble(items[0]);
            style.weighty = Double.parseDouble(items[1]);
          }
        else if ( key.equals("fill") )
          style.fill = getFillValue(value);
        else if ( key.equals("insets") )
          {
            String[] items = splitValue(key, value, 4);
            style.insets.top = Integer.parseInt(items[0]);
            style.insets.right = Integer.parseInt(items[1]);
            style.insets.bottom = Integer.parseInt(items[2]);
            style.insets.left = Integer.parseInt(items[3]);
          }
        else if ( key.equals("ipadx") )
          style.ipadx = Integer.parseInt(value);
        else if ( key.equals("ipady") )
          style.ipady = Integer.parseInt(value);
        else if ( key.equals("ipad") )
          {
            String[] items = splitValue(key, value, 2);
            style.ipadx = Integer.parseInt(items[0]);
            style.ipady = Integer.parseInt(items[1]);
          }
        else
          throw new IllegalArgumentException("Unknown style key: " + key);
      }
  }

  /**
   * Resets the specified constraints object.
   */

  public static final void reset (GridBagConstraints style)
  {
    style.gridx = GridBagConstraints.RELATIVE;
    style.gridy = GridBagConstraints.RELATIVE;
    style.gridwidth = 1;
    style.gridheight = 1;
    style.weightx = 0;
    style.weighty = 0;
    style.anchor = GridBagConstraints.CENTER;
    style.fill = GridBagConstraints.NONE;
    style.insets.top = 0;
    style.insets.right = 0;
    style.insets.bottom = 0;
    style.insets.left = 0;
    style.ipadx = 0;
    style.ipady = 0;
  }

  /**
   * 
   */

  public static final void print (GridBagConstraints style, PrintStream out)
  {
    out.println("gridx         = " + getGridAsString(style.gridx));
    out.println("gridy         = " + getGridAsString(style.gridy));
    out.println("gridwidth     = " + style.gridwidth);
    out.println("gridheight    = " + style.gridheight);
    out.println("weightx       = " + style.weightx);
    out.println("weighty       = " + style.weighty);
    out.println("anchor        = " + getAnchorName(style.anchor));
    out.println("fill          = " + getFillName(style.fill));
    out.println("insets.top    = " + style.insets.top);
    out.println("insets.right  = " + style.insets.right);
    out.println("insets.bottom = " + style.insets.bottom);
    out.println("insets.left   = " + style.insets.left);
    out.println("ipadx         = " + style.ipadx);
    out.println("ipady         = " + style.ipady);
  }

  /**
   * Returns the <em>anchor</em> value for the specified name.
   */

  protected static final int getAnchorValue (String name)
  {
    int i = 0;
    while ( !anchorNames[i].equals(name) && i < anchorNames.length ) i++;
    if ( i < anchorNames.length )
      return anchorValues[i];
    else
      throw new IllegalArgumentException("Unknown anchor name: " + name);
  }

  /**
   * Returns the <em>anchor</em> name for the specified value.
   */

  protected static final String getAnchorName (int value)
  {
    int i = 0;
    while ( anchorValues[i] != value && i < anchorValues.length ) i++;
    if ( i < anchorValues.length )
      return anchorNames[i];
    else
      return "unknown (" + value + ")";      
  }

  /**
   * Returns the <em>fill</em> value for the specified name.
   */

  protected static final int getFillValue (String name)
  {
    int i = 0;
    while ( !fillNames[i].equals(name) && i < fillNames.length ) i++;
    if ( i < fillNames.length )
      return fillValues[i];
    else
      throw new IllegalArgumentException("Unknown fill name: " + name);
  }

  /**
   * Returns the <em>fill</em> name for the specified value.
   */

  protected static final String getFillName (int value)
  {
    int i = 0;
    while ( fillValues[i] != value && i < fillValues.length ) i++;
    if ( i < fillValues.length )
      return fillNames[i];
    else
      return "unknown (" + value + ")";      
  }

  /**
   * Returns the <em>gridx</em> or <em>gridy</em> value for the specified string.
   */

  public static final int getGridValue (String string)
  {
    return
      (string.equals("RELATIVE")
       ? GridBagConstraints.RELATIVE
       : Integer.parseInt(string));
  }

  /**
   * Returns a string describing the specified <em>gridx</em> or <em>gridy</em> value. If
   * the value is {@link GridBagConstraints#RELATIVE GridBagConstraints.RELATIVE}, the
   * returned string is <code>"RELATIVE"</code>, otherwise, the string contains the value
   * as a numerical expression.
   */

  public static final String getGridAsString (int value)
  {
    return (value == GridBagConstraints.RELATIVE ? "RELATIVE" : Integer.toString(value));
  }

  /**
   * 
   */

  protected static final String[] splitValue (String key, String value, int reqLength)
  {
    String[] items = splitPattern.split(value);
    if ( items.length != reqLength )
      throw new IllegalArgumentException
        ("Invalid " + key + " value:" + value + " (must have exactly " + reqLength + " items)");
    return items;
  }

}
