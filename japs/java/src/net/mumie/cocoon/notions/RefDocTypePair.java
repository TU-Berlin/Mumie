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

package net.mumie.cocoon.notions;

/**
 * A pair of document types of a reference.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: RefDocTypePair.java,v 1.5 2007/07/11 15:38:46 grudzin Exp $</code>
 */

public class RefDocTypePair
{
  /**
   * The "from" document type. This is the type of the reference origin documents.
   */

  private int fromDocType;

  /**
   * The "to" document type. This is the type of the reference target documents.
   */

  private int toDocType;

  /**
   * Returns the "from" document type. This is the type of the reference origin
   * documents.
   */

  public int getFromDocType ()
  {
    return this.fromDocType;
  }

  /**
   * Returns the "to" document type. This is the type of the reference target documents.
   */

  public int getToDocType ()
  {
    return this.toDocType;
  }

  /**
   * Returns <code>true</code> if the specified object is a <code>RefDocTypePair</code> with
   * the same "from" resp. "to" document types as this one.
   */

  public boolean equals (Object object)
  {
    return
      ( object instanceof RefDocTypePair &&
        ((RefDocTypePair)object).getFromDocType() == this.fromDocType &&
        ((RefDocTypePair)object).getToDocType() == this.toDocType );
  }

  /**
   * Returns a string representation of this <code>RefDocTypePair</code>.
   */

  public String toString ()
  {
    return "RefDocTypePair(" + this.fromDocType + "," + this.toDocType + ")";
  }

  /**
   * Creates a new <code>RefDocTypePair</code> with the specified "from" and "to" document
   * types.
   */

  public RefDocTypePair (int fromDocType, int toDocType)
  {
    this.fromDocType = fromDocType;
    this.toDocType = toDocType;
  }
}
