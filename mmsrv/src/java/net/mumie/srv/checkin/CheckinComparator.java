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

package net.mumie.srv.checkin;

import java.io.File;
import java.util.Comparator;
import net.mumie.srv.notions.Nature;
import net.mumie.srv.notions.EntityType;

/**
 * Comparator to bring {@link EntityToCheckin EntityToCheckin}s in the correct
 * order for checkin.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckinComparator.java,v 1.2 2009/09/26 00:09:44 rassy Exp $</code>
 */

public class CheckinComparator implements Comparator
{
  /**
   * Compares to (pseuodo-)documents to checkin
   */

  public int compare (Object object1, Object object2)
    throws ClassCastException
  {
    EntityToCheckin toCheckin1 = (EntityToCheckin)object1;
    EntityToCheckin toCheckin2 = (EntityToCheckin)object2;

    boolean isSection1 = toCheckin1.getType() == EntityType.SECTION;
    boolean isSection2 = toCheckin2.getType() == EntityType.SECTION;

    String path1 = toCheckin1.getPath();
    String path2 = toCheckin2.getPath();

    if ( isSection1 && !isSection2 )
      // Sections are always before non-sections:
      return -1;
    else if ( isSection2 && !isSection1 )
      // Sections are always before non-sections:
      return 1;
    else if ( isSection1 && isSection2 )
      {
        // Order by nesting depth:
        int depth1 = getDepth(path1);
        int depth2 = getDepth(path2);

        if ( depth1 < depth2 )
          // Section with lower depth comes first:
          return -1;
        else if ( depth2 < depth1 )
          // Section with lower depth comes first:
          return 1;
        else
          // Order alphabetically:
          return path1.compareTo(path2);
      }
    else
      // Order alphabetically:
      return path1.compareTo(path2);
  }

  /**
   * Auxiliary method; returns the nesting depth of a checkin path.
   */

  protected static int getDepth (String path)
  {
    // Remove leading file separator:
    if ( path.startsWith(File.separator) )
      path = path.substring(File.separator.length());

    // Remove trailing file separator:
    if ( path.endsWith(File.separator) )
      path = path.substring(0, path.length() - File.separator.length());

    // Count file separators:
    int depth = 0;
    int index = 0;
    while ( true )
      {
        index = path.indexOf(File.separator, index);
        if ( index == -1 ) break;
        index += File.separator.length();
        depth++;
      }

    return depth;
  }
}