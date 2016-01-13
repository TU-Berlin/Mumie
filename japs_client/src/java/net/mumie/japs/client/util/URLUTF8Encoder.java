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

package net.mumie.japs.client.util;

/**
 * <p>
 *   Provides methods to encode characters and strings into a URL-safe form.  Non-ASCII
 *   characters are first encoded as sequences of two or three bytes, using the UTF-8
 *   algorithm, before being encoded as <code>"%xy"</code> escapes.
 * </p>
 * <p>
 *   This class is based on the code provided by the W3C at
 *   <a href="http://www.w3.org/International/URLUTF8Encoder.java">www.w3.org/International/URLUTF8Encoder.java</a>.
 *   See also 
 *   <a href="http://www.w3.org/International/O-URL-code.html">www.w3.org/International/O-URL-code.html</a>.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Revision: 1.4 $</code>
 */

public class URLUTF8Encoder
{
  /**
   * The characters from <code>0</code> through <code>127</code> as strings.
   */

  static final String[] LITERAL = new String[128];
  static
  {
    for (int i = 0; i < LITERAL.length; i++)
      LITERAL[i] = new String(new char[] { (char)i });
  }

  /**
   * Hexadecimal <code>"%xy"</code> codes for the characters from <code>0</code> through
   * <code>255</code>.
   */

  static final String[] HEX_CODES =
  {
    "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
    "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f",
    "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
    "%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f",
    "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
    "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f",
    "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
    "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
    "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
    "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f",
    "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
    "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
    "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
    "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f",
    "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
    "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
    "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
    "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
    "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
    "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f",
    "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
    "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af",
    "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7",
    "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf",
    "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
    "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf",
    "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7",
    "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df",
    "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
    "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
    "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
    "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"
  };

  /**
   * Whether the space character is encoded as <code>'+'</code> or as the hexadecimal code
   * <code>"%20"</code>. <code>true</code> means plus sign, <code>false</code> means
   * hexadecimal code. Default is <code>true</code>
   */

  protected boolean spaceAsPlus = true;

  /**
   * Returns whether the space character is encoded as <code>'+'</code> or as the
   * hexadecimal code <code>"%20"</code>. <code>true</code> means plus sign,
   * <code>false</code> means hexadecimal code.
   */

  public boolean getSpaceAsPlus ()
  {
    return this.spaceAsPlus;
  }

  /**
   * Sets whether the space character is encoded as <code>'+'</code> or as the hexadecimal
   * code <code>"%20"</code>. <code>true</code> means plus sign, <code>false</code> means
   * hexadecimal code. Default is <code>true</code>
   */

  public void setSpaceAsPlus (boolean spaceAsPlus)
  {
    this.spaceAsPlus = spaceAsPlus;
  }

  /**
   * <p>
   *   Encodes a single character to the "x-www-form-urlencoded" form, enhanced with the
   *   UTF-8-in-URL proposal. This is what happens:
   * </p>
   * <ul>
   *   <li>
   *     The ASCII characters <code>'a'</code> through <code>'z'</code>, <code>'A'</code>
   *     through <code>'Z'</code>, and <code>'0'</code> through <code>'9'</code> remain the
   *     same. 
   *   </li>
   *   <li>
   *     The unreserved characters <code>'-', '_', '.', '!', '~', '*', '\'', '(', ')'</code>
   *     remain the same. 
   *   </li>
   *   <li>
   *     The space character <code>' '</code> is converted into a plus sign
   *     <code>'+'</code> unless {@link #spaceAsPlus} is <code>false</code>.
   *   </li>
   *   <li>
   *     All other ASCII characters, including the space character if {@link #spaceAsPlus}
   *     is <code>false</code>, are converted into the 3-character string
   *     <code>"%xy"</code>, where <code>xy</code> is the two-digit hexadecimal
   *     representation of the character code
   *   </li>
   *   <li>
   *     All non-ASCII characters are encoded in two steps: first to a sequence of 2 or 3
   *     bytes, using the UTF-8 algorithm; secondly each of these bytes is encoded as
   *     <code>"%xy"</code>.
   *   </li>
   * </ul>
   *
   * @param c The character to be encoded, as an integer
   * @return The encoded character, as a string
   */

  public String encode (int c)
  {
      if ( ( 'A' <= c && c <= 'Z' ) ||             // 'A'..'Z'
           ( 'a' <= c && c <= 'z' ) ||             // 'a'..'z'
           ( '0' <= c && c <= '9' ) ||             // '0'..'9'
           c == '-' || c == '_' || c == '.' ||     // unreserved except space
           c == '!' || c == '~' || c == '*' ||
           c == '\'' || c == '(' || c == ')' )
        return LITERAL[c];
      else if ( c == ' ' )                         // space
        return
          (this.spaceAsPlus
           ? "+"
           : HEX_CODES[c]);
      else if ( c <= 0x007f )                      // other ASCII
        return HEX_CODES[c];
      else if ( c <= 0x07FF )                      // non-ASCII <= 0x7FF
        return
          HEX_CODES[0xc0 | (c >> 6)] +
          HEX_CODES[0x80 | (c & 0x3F)];
      else                                         // non-ASCII > 0x7FF
        return
          HEX_CODES[0xe0 | (c >> 12)] +
          HEX_CODES[0x80 | ((c >> 6) & 0x3F)] +
          HEX_CODES[0x80 | (c & 0x3F)];
  }

  /**
   * <p>
   *   Encodes a character array to the "x-www-form-urlencoded" form, enhanced with the
   *   UTF-8-in-URL proposal.
   * </p>
   * <p>
   *   Calls {@link #encode(int) encode(c)} for each character <code>c</code> of the
   *   array. See {@link #encode(int) encode} for more information.
   * </p>
   * @param chars The character array to be encoded
   * @return The encoded characters, as a string
   */

  public String encode (char[] chars)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < chars.length; i++)
      buffer.append(encode(chars[i]));
    return buffer.toString();
  }

  /**
   * <p>
   *   Encodes a string to the "x-www-form-urlencoded" form, enhanced with the
   *   UTF-8-in-URL proposal.
   * </p>
   * <p>
   *   Same as {@link #encode(char[]) encode(string.toCharArray())}. See there for more
   *   information.
   * </p>
   * @param string The string to be encoded
   * @return The encoded string
   */

  public String encode (String string)
  {
    return encode(string.toCharArray());
  }
}
