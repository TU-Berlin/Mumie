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
import java.sql.ResultSet;

/**
 * Resolves generic documents/
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: GenericDocumentResolver.java,v 1.4 2008/06/20 10:04:18 mumie Exp $</code>
 */

public interface GenericDocumentResolver
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>GenericDocumentResolver.class.getName()</code>).
   */

  static public String ROLE = GenericDocumentResolver.class.getName();

  /**
   * Returns the id of the real document corresponding to the generic document, theme, and
   * languge specified by the parameters. The information needed is retrieved from the
   * specified result set.
   *
   * @param typeOfGeneric type of the generic document, as numerical code
   * @param idOfGeneric id of the generic document
   * @param themeId id of the theme
   * @param languageId id of the language
   *
   * @throws GenericDocumentResolveException id something goes wrong
   */

  public int resolve (int typeOfGeneric,
                      int idOfGeneric,
                      int languageId,
                      int themeId,
		      ResultSet resultSet)
    throws GenericDocumentResolveException;

  /**
   * Returns the id of the real document corresponding to the generic document, theme, and
   * languge specified by the parameters.
   *
   * @param typeOfGeneric type of the generic document, as numerical code
   * @param idOfGeneric id of the generic document
   * @param themeId id of the theme
   * @param languageId id of the language
   *
   * @throws GenericDocumentResolveException id something goes wrong
   */

  public int resolve (int typeOfGeneric,
                      int idOfGeneric,
                      int languageId,
                      int themeId)
    throws GenericDocumentResolveException;
}