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

import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.PseudoDocType;
import java.sql.SQLException;
import java.sql.ResultSet;

public class TutorialUtil
{
  /**
   * Returns the default section path. It is obtained as follows: First, the section
   * path of the specified class is determined. This path should have
   * the form <code> <var>some_path</var>/classes</code>. The default section path of
   * the tutorial is then <var>some_path</var>/tutorials</code>, which is
   * returned. If the path of the section has not the above form, an
   * {@link IllegalArgumentException IllegalArgumentException} is thrown.
   */

  public static String getDefaultSectionPath (String classSyncId,
					      DbHelper dbHelper, PathTokenizer pathTokenizer)
    throws SQLException
  {
    ResultSet resultSet = dbHelper.queryPseudoDocDatumBySyncId
      (PseudoDocType.CLASS, classSyncId, DbColumn.SECTION_PATH);
    if ( !resultSet.next() )
      throw new SQLException("Cannot find class with sync id \"" + classSyncId + "\"");
    String classSectionPath = resultSet.getString(DbColumn.SECTION_PATH);
    pathTokenizer.tokenize(classSectionPath);
    String pureName = pathTokenizer.getPureName();
    if ( !pureName.equals("classes") )
      throw new IllegalArgumentException
        ("Class section pure name does not meet the standard: \"" + pureName + "\"" +
         " (should be \"classes\")");
    String sectionPath = pathTokenizer.getSectionPath();
    return sectionPath + "/tutorials";
  }

  /**
   * Returns the default section path. It is obtained as follows: First, the section
   * path of the specified class is determined. This path should have
   * the form <code> <var>some_path</var>/classes</code>. The default section path of
   * the tutorial is then <var>some_path</var>/tutorials</code>, which is
   * returned. If the path of the section has not the above form, an
   * {@link IllegalArgumentException IllegalArgumentException} is thrown.
   */

  public static String getDefaultSectionPath (int classId, DbHelper dbHelper, PathTokenizer pathTokenizer)
    throws SQLException
  {
    String classSectionPath = dbHelper.getPseudoDocDatumAsString
      (PseudoDocType.CLASS, classId, DbColumn.SECTION_PATH);
    pathTokenizer.tokenize(classSectionPath);
    String pureName = pathTokenizer.getPureName();
    if ( !pureName.equals("classes") )
      throw new IllegalArgumentException
        ("Class section pure name does not meet the standard: \"" + pureName + "\"" +
         " (should be \"classes\")");
    String sectionPath = pathTokenizer.getSectionPath();
    return sectionPath + "/tutorials";
  }
}