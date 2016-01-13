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

package net.mumie.cocoon.msg;

import net.mumie.cocoon.util.Identifyable;

/**
 * A table that contains all {@link MessageDestination MessageDestination}s. The
 * destinations can be accessed by names.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MessageDestinationTable.java,v 1.2 2008/09/24 11:10:07 rassy Exp $</code>
 */

public interface MessageDestinationTable
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>MessageDestinationTable.class.getName()</code>).
   */

  public static final String ROLE = MessageDestinationTable.class.getName();

  /**
   * Returns the destination for the specified name.
   *
   * @throws Exception if something goes wrong.
   */

  public MessageDestination getDestination (String name)
    throws Exception;
}
