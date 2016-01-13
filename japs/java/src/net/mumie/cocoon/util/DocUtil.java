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

package net.mumie.cocoon.util;

public class DocUtil
{
  /**
   * Creates a suitable pure name from the specified name. The string is converted to
   * lower case, and all characters which are no letters and no digits are converted to
   * underscores (<code>'_'</code>). Multiple underscores are combined to one
   * underscore. German umlauts are replaced by their two-letter ASCII replacements.
   * If the specified prefix is non-null, it is prepended to the name, separated by an
   * underscore.
   */

  public static final String nameToPureName (String prefix, String name)
  {
    StringBuilder pureName = new StringBuilder(name.length());
    boolean lastWasWhitesapce = false;
    for (char c : name.toLowerCase().toCharArray())
      {
        if ( Character.isLetterOrDigit(c) )
          {
            switch ( c )
              {
              case 228: // a umlaut
                pureName.append('a').append('e');
                break;
              case 246: // o umlaut
                pureName.append('o').append('e');
                break;
              case 252: // u umlaut
                pureName.append('u').append('e');
                break;
              case 223: // sz
                pureName.append('s').append('s');
                break;
              default:
                pureName.append(c);
              }
            lastWasWhitesapce = false;
          }
        else if ( !lastWasWhitesapce )
          {
            pureName.append('_');
            lastWasWhitesapce = true;
          }
      }
    if ( prefix != null )
      pureName.insert(0, "_").insert(0, prefix);
    return pureName.toString();
  }
  
}