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

package net.mumie.cocoon.checkin;

/**
 * <p>
 * Represents an editable entry of the generic document section of a document or
 * pseudo-document.
 * </p>
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public interface EditableGDIMEntry extends GDIMEntry {

  /**
   * Sets the id of the generic document, or {@link Id#UNDEFINED UNDEFINED} if
   * no such id is set.
   */

  public void setGenericDocId(int genDocId) throws MasterException;

  /**
   * Sets the path of the generic document, or <code>null</code> if no such
   * path is set.
   */

  public void setGenericDocPath(String genDocPath) throws MasterException;

  /**
   * Sets the theme path, or <code>null</code> if no theme path is set.
   */

  public void setThemePath(String themePath) throws MasterException;

  /**
   * Sets the language path, or <code>null</code> if no language is set.
   */

  public void setLang(String lang) throws MasterException;

  /**
   * Sets the languages code, or <code>null</code> if no language is set.
   */

  public void setLangCode(String langCode) throws MasterException;
}
