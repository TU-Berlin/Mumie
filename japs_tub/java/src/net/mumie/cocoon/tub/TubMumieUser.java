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

package net.mumie.cocoon.tub;

import net.mumie.cocoon.notions.DbColumn;
import java.sql.ResultSet;

/**
 * Comprises the data of a user retrieved from the database.
 */

public class TubMumieUser
{
  /**
   * The id
   */

  protected final int id;

  /**
   * The id of the theme
   */

  protected final int themeId;

  /**
   * The id of the language
   */

  protected final int languageId;

  /**
   * Returns the id
   */

  public final int getId ()
  {
    return this.id;
  }

  /**
   * Returns the theme id
   */

  public final int getThemeId ()
  {
    return this.themeId;
  }

  /**
   * Returns the language id
   */

  public final int getLanguageId ()
  {
    return this.languageId;
  }

  /**
   * Returns the db columns corresoding to the informations comprised by a
   * <code>TubMumieUser</code> instance.
   */

  public static final String[] getDbColumns ()
  {
    return new String[]
      {
	DbColumn.ID,
	DbColumn.THEME,
	DbColumn.LANGUAGE,
	DbColumn.SYNC_ID,
      };
  }

  /**
   * Creates a new instance with the specified values
   */

  public TubMumieUser (int id, int themeId, int languageId)
  {
    this.id = id;
    this.themeId = themeId;
    this.languageId = languageId;
  }

  /**
   * Creates a new instance with values from the specified SQL result set.
   */

  public TubMumieUser (ResultSet resultSet)
    throws Exception
  {
    this
      (resultSet.getInt(DbColumn.ID),
       resultSet.getInt(DbColumn.THEME),
       resultSet.getInt(DbColumn.LANGUAGE));
  }

  /**
   * Returns a string representation of this object
   */

  public String toString ()
  {
    return "id: " + this.id + " themeId: " + this.themeId + " languageId: " + languageId;
  }
}
