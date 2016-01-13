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

package net.mumie.mathletfactory.util;

import java.awt.Color;
import java.awt.Font;

/**
 * This class can be used to create fonts.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class FontFactory {

	public static Font createFont(String name, int style, int size) {
		return new Font(name, style, size);
	}
	
	/**
	 * Creates and returns a new default bold font with given size. 
	 */
	public static Font createBoldFont(int size) {
		return createFont(" ", Font.BOLD, size);
	}

	/**
	 * Creates and returns a new default plain font with given size. 
	 */
	public static Font createFont(int size) {
		return createFont(" ", Font.PLAIN, size);
	}
	
  /**
   * Returns the equivalent HTML 3.2 font size to the given Java font size.
   */
  public static int getHMTLFontSize(int javaFontSize) {
    if(javaFontSize < 9)
      return 1;
    else if(javaFontSize >= 9 && javaFontSize < 11)
      return 2;
    else if(javaFontSize >= 11 && javaFontSize < 13)
      return 3;
    else if(javaFontSize >= 13 && javaFontSize < 16)
      return 4;
    else if(javaFontSize >= 16 && javaFontSize < 22)
      return 5;
    else if(javaFontSize >= 22 && javaFontSize < 31)
      return 6;
    else if(javaFontSize >= 31)
      return 7;
    return 0;
  }

  /**
   * Enodes a java.awt.Color to a HTML valid color definition in form of "#rrggbb" where
   * rr, gg and bb are the hexadecimal values for red, green and blue.
   */
  public static String encodeHTMLColor(Color c) {
    return "#" + intToHex(c.getRed()) + intToHex(c.getGreen())
        + intToHex(c.getBlue());
  }

  /*
   * Helper method to calculate decimal numbers to hex format
   */
  private static String intToHex(int integer) {
    String s = Integer.toHexString(integer);
    if (s.length() == 1)
      s = "0" + s;
    return s;
  }
}
