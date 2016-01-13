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

package net.mumie.cocoon.content;

import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;

public class ContentObjectItem
{
  /**
   * The nature of the content object
   */

  protected final int nature;

  /**
   * The type of the content object
   */

  protected final  int type;

  /**
   * The id of the content object
   */

  protected final int id;

  /**
   * Creates a new instance.
   */

  public ContentObjectItem (int nature, int type, int id)
  {
    this.nature = nature;
    this.type = type;
    this.id = id;
  }

  /**
   * Creates a new instance.
   */

  public ContentObjectItem (String typeName, int id)
  {
    int type = DocType.codeFor(typeName);
    if ( type != DocType.UNDEFINED )
      this.nature = Nature.DOCUMENT;
    else if ( (type = PseudoDocType.codeFor(typeName)) != PseudoDocType.UNDEFINED )
      this.nature = Nature.PSEUDO_DOCUMENT;
    else
      throw new IllegalArgumentException("Unknown (pseudo-)document type: " + typeName);
    this.type = type;
    this.id = id;
  }

  /**
   * Returns the nature of the content object.
   */

  public int getNature ()
  {
    return this.nature;
  }

  /**
   * Returns the type of the content object.
   */

  public int getType ()
  {
    return this.type;
  }

  /**
   * Returns the id of the content object.
   */

  public int getId ()
  {
    return this.id;
  }

  /**
   * Returns the name of the type of the (pseudo-)document.
   */

  public String getTypeName ()
  {
    if ( this.nature == Nature.DOCUMENT )
      return DocType.nameFor(this.type);
    else if ( this.nature == Nature.PSEUDO_DOCUMENT )
      return PseudoDocType.nameFor(this.type);
    else
      return null;
  }

  /**
   * Returns a string representation of this instance.
   */

  public String toString ()
  {
    return
      "ContentObjectItem(" +
      this.nature + "," +
      this.type + "," +
      this.id + ")";
  }
}