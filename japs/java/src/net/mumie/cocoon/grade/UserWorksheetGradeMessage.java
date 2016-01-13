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

package net.mumie.cocoon.grade;
import net.mumie.cocoon.msg.Message;

/**
 * A message which reports the total grade of a user in a worksheet.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UserWorksheetGradeMessage.java,v 1.1 2008/09/24 11:10:07 rassy Exp $</code>
 */

public interface UserWorksheetGradeMessage
  extends Message
{
  /**
   * Role as an Avalon service (<code>UserWorksheetGradeMessage.class.getName()</code>).
   */

  public static final String ROLE = UserWorksheetGradeMessage.class.getName();

  /**
   * Initializes this instance
   * @param userId id of the user
   * @param worksheetId id of the worksheet
   * @param destinationName the destination name
   */

  public void setup (int userId, int worksheetId, String destinationName);
}

