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

/**
 * Represents a document or pseudo-document of no particular type.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: VariableTypeContentObject.java,v 1.3 2007/07/11 15:38:43 grudzin Exp $</code>
 */

public interface VariableTypeContentObject
  extends ContentObject
{
  /**
   * Role of an implementing class as an Avalon component
   * (<code>VariableTypeContentObject.class.getName()</code>).
   */

  String ROLE = VariableTypeContentObject.class.getName();

  /**
   * Sets the nature (pseuo-document or document) of this content object.
   * @throws IllegalArgumentException if the argument is not a valid nature code.
   */

  public void setNature (int nature)
    throws IllegalArgumentException;

  /**
   * Sets the type of this content object. The nature must be set before.
   * @throws IllegalStateException id the nature has not been set before.
   * @throws IllegalArgumentException if the argument is not a valid type code.
   */

  public void setType (int type)
    throws IllegalArgumentException, IllegalStateException;

  /**
   * Sets the type according to the specified type name. The nature is set, too. This is
   * possible because type names are unique among documents and pseudo-documents.
   */

  public void setType (String typeName)
    throws IllegalArgumentException;
}