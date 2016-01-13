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

package net.mumie.font;
import java.io.PrintStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

public class FontSerializer
{
  /**
   * Outputs the specified font as XML to the specified print stream.
   */

  public void toXML (Font font, PrintStream out)
    throws IOException
  {
    // XML declaration:
    out.println("<?xml version=\"1.0\" encoding=\"ASCII\"?>");

    // Root element start tag:
    out.println
      ("<" + FontXMLElement.FONT +
       " " + FontXMLAttribute.NAME + "=\"" + font.getName() + "\"" +
       " " + FontXMLAttribute.FAMILY + "=\"" + font.getFamily() + "\"" +
       " " + FontXMLAttribute.STYLE + "=\"" + FontStyle.getName(font.getStyle()) + "\"" +
       " " + FontXMLAttribute.WEIGHT + "=\"" + FontWeight.getName(font.getWeight()) + "\"" +
       " " + FontXMLAttribute.SIZE + "=\"" + font.getSize() + "\"" +
       ">");

    // Glyph tags:
    for (Glyph glyph : font.getGlyphs())
      out.println
        ("<" + FontXMLElement.GLYPH +
         " " + FontXMLAttribute.NAME + "=\"" + glyph.getName() + "\"" +
         " " + FontXMLAttribute.UNICODE + "=\"" + quoteXML(glyph.getUnicode()) + "\"" +
         " " + FontXMLAttribute.X + "=\"" + glyph.getX() + "\"" +
         " " + FontXMLAttribute.Y + "=\"" + glyph.getY() + "\"" +
         " " + FontXMLAttribute.WIDTH + "=\"" + glyph.getWidth() + "\"" +
         " " + FontXMLAttribute.HEIGHT + "=\"" + glyph.getHeight() + "\"" +
         " " + FontXMLAttribute.ADVANCE_WIDTH + "=\"" + glyph.getAdvanceWidth() + "\"" +
         " " + FontXMLAttribute.OUTLINE + "=\"" + glyph.getOutline() + "\"" +
         "/>");

    // Root element end tag:
    out.println
      ("</" + FontXMLElement.FONT + ">");

    out.flush();
  }

  /**
   * Outputs the specified font as XML to the specified file.
   */

  public void toXML (Font font, File file)
    throws IOException
  {
    PrintStream out = new PrintStream(new FileOutputStream(file));
    this.toXML(font, out);
    out.close();
  }

  /**
   * Quotes XML special characters in the specified string.
   */

  public static String quoteXML (String string)
  {
    StringBuilder buffer = new StringBuilder();
    for (char c : string.toCharArray())
      {
        if ( c == '<' )
          buffer.append("&lt;");
        else if ( c == '>' )
          buffer.append("&gt;");
        else if ( c == '&' )
          buffer.append("&amp;");
        else if ( c < 128 )
          buffer.append(c);
        else
          buffer.append("&#" + (int)c + ";");
      }
    return buffer.toString();
  }

}
