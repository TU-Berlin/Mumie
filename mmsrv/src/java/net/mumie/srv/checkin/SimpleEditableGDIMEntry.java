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
import net.mumie.srv.notions.Id;

/**
 * This class is the base implementation of an editable generic document entry.
 * It can be used to store and retrieve all available meta information, although
 * the mapping of specific meta information to single document types must be
 * done in sub classes.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id: SimpleEditableGDIMEntry.java,v 1.2 2009/10/07 00:36:15 rassy Exp $</code>
 */
public class SimpleEditableGDIMEntry implements EditableGDIMEntry
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The path of the generic document, or <code>null</code> if no such path is set.
   */

  protected String genericDocPath = null;

  /**
   * The theme path, or <code>null</code> if no theme path is set.
   */

  protected String themePath = null;

  /**
   * The language path, or <code>null</code> if no language is set.
   */

  protected String langPath = null;

  /**
   * The languages code, or <code>null</code> if no language is set.
   */

  protected String lang = null;

  // --------------------------------------------------------------------------------
  // h1: Constructors
  // --------------------------------------------------------------------------------

  /**
   * Default constructor, creates a void instance.
   */

  public SimpleEditableGDIMEntry () throws MasterException
  {
    // Nothing
  }

  /**
   * Creates an instance which represents the same GDIM entry as the specified one. The
   * values of the specified GDIM entry are copied.
   */

  public SimpleEditableGDIMEntry(GDIMEntry entry) throws MasterException
  {
    if (entry != null)
      {
        this.setGenericDocPath(entry.getGenericDocPath());
        this.setLang(entry.getLang());
        this.setLangCode(entry.getLangCode());
        this.setThemePath(entry.getThemePath());
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Get / set methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the path of the generic document, or <code>null</code> if no such path is set.
   */

  public String getGenericDocPath()
  {
    return this.genericDocPath;
  }

  /**
   * Sets the path of the generic document.
   */

  public void setGenericDocPath (String genericDocPath)
  {
    this.genericDocPath = genericDocPath;
  }

  /**
   * Returns the language path, or <code>null</code> if no language is set.
   */

  public String getLang()
  {
    return this.lang;
  }

  /**
   * Sets the language path, or <code>null</code> if no language is set.
   */

  public void setLang(String lang) {
    this.lang = lang;
  }

  /**
   * Returns the languages code, or <code>null</code> if no language is set.
   */

  public String getLangCode()
  {
    return this.lang;
  }

  /**
   * sets the languages code.
   */

  public void setLangCode (String lang)
  {
    this.lang = lang;
  }

  /**
   * Returns the theme path, or <code>null</code> if no theme path is set.
   */

  public String getThemePath()
  {
    return this.themePath;
  }

  /**
   * Ses the theme path.
   */

  public void setThemePath (String themePath)
  {
    this.themePath = themePath;
  }
}
