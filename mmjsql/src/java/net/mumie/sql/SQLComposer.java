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

package net.mumie.sql;

import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.sql.SQLException;
import java.util.Map;
import java.util.Iterator;

/**
 * <p>
 *   Helper class to compose SQL code.
 * </p>
 * <p>
 *   A <code>SQLComposer</code> represents a buffer for SQL code. Methods exist to add SQL
 *   keywords (e.g., <code>"SELECT"</code>, <code>"INSERT"</code>), column names, column
 *   values, etc. The necessary delimiters (spaces or commas) are inserted automatically in
 *   most cases. The {@link #getCode getCode} method returns the buffer content as a
 *   string. The {@link #clear clear} method empties the buffer, so a new SQL statement can
 *   be created using the same <code>SQLComposer</code> instance.
 * </p>
 * <h4>Examples:</h4>
 * <ol>
 *   <li>
 *     Java code:
 *     <pre>
 *     sqlComposer
 *       .clear()
 *       .addSELECT()
 *       .addColumn("id")
 *       .addColumn("name")
 *       .addColumn("description")
 *       .addFROM()
 *       .addTable("pages")
 *       .addWHERE()
 *       .addColumn("id").add("=").addValue(12)
 *       .addAND()
 *       .addColumn("name").add("=").addValue("Test Page");</pre>
 *     SQL code produced by <code>sqlComposer.getCode()</code>:
 *     <pre>
 *     SELECT id,name,description FROM pages WHERE id = 12 AND name = 'Test Page'</pre>
 *   </li>
 *   <li>
 *     Java code:
 *     <pre>
 *     String[] columns = {"id", "name", "description"};
 *     sqlComposer.clear();
 *     sqlComposer
 *       .addSELECT()
 *       .addColumns(columns)
 *       .addColumn("vc_thread")
 *       .addFROM()
 *       .addTable("pages")
 *       .addWHERE()
 *       .addColumn("id").add("=").addValue(12)
 *       .addAND()
 *       .addColumn("name").add("=").addValue("Test Page");</pre>
 *     SQL code produced by <code>sqlComposer.getCode()</code>:
 *     <pre>
 *     SELECT id,name,description,vc_thread FROM pages WHERE id = 12 AND name = 'Test Page'</pre>
 *   </li>
 *   <li>
 *     Java code:
 *     <pre>
 *     Map data = new HashMap();
 *     data.put("id", new Integer(5));
 *     data.put("login_name", "foo");
 *     data.put("password", "bar");
 *     data.put("time", new Date(System.currentTimeMillis()));
 *     sqlComposer.clear();
 *     sqlComposer
 *       .addINSERT()
 *       .addINTO()
 *       .addTable("users")
 *       .addInsertData(data);</pre>
 *     SQL code produced by <code>sqlComposer.getCode()</code>:
 *     <pre>
 *     INSERT INTO users (password,login_name,time,id) VALUES ('bar','foo','2006-04-28 11:54:18.152',5)</pre>
 *   </li>
 * </ol>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SQLComposer.java,v 1.17 2009/12/27 18:03:28 rassy Exp $</code>
 */

public class SQLComposer
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Indicates that no token has been added so far.
   */

  protected static final int NONE = 0;

  /**
   * Indicates that the last added token was a column name.
   */

  protected static final int COLUMN = 1;

  /**
   * Indicates that the last added token was a table name.
   */

  protected static final int TABLE = 2;

  /**
   * Indicates that the last added token was a column value.
   */

  protected static final int VALUE = 3;

  /**
   * Indicates that the last added token was an opening round parenthesis.
   */

  protected static final int PAREN_START = 4;

  /**
   * Indicates that the last added token was a closing round parenthesis.
   */

  protected static final int PAREN_END = 5;

  /**
   * Indicates that the last added token was not of a special type as {@link #COLUMN},
   * {@link #VALUE}, etc.
   */

  protected static final int OTHER = 6;

  /**
   * The initial capacity of the internal string buffer.
   */

  protected static final int INITIAL_CAPACITY = 64;

  /**
   * The <code>StringBuffer</code> wrapped by this <code>SQLComposer</code> object.
   */

  protected StringBuffer buffer = new StringBuffer(INITIAL_CAPACITY);

  /**
   * The type of the last added token.
   */

  protected int lastToken = NONE;

  // --------------------------------------------------------------------------------
  // Clear method
  // --------------------------------------------------------------------------------

  /**
   * Removes all code and resets this object in its initial state.
   * @return this <code>SQLComposer</code> instance.
   */

  public SQLComposer clear ()
  {
    this.buffer = new StringBuffer(INITIAL_CAPACITY);
    lastToken = NONE;
    return this;
  }

  // --------------------------------------------------------------------------------
  // Getting the SQL code
  // --------------------------------------------------------------------------------

  /**
   * Returns the SQL code as a string.
   */

  public String getCode ()
  {
    return this.buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Basic token-adding methods
  // --------------------------------------------------------------------------------

  /**
   * Appends <code>token</code> to the internal buffer and sets {@link #lastToken} to
   * <code>type</code>. If <code>separate</code> is <code>true</code>, a blank character is
   * inserted before the token, otherwise not.
   */

  protected SQLComposer add (String token, int type, boolean separate)
  {
    if ( separate ) this.buffer.append(" ");
    this.buffer.append(token);
    this.lastToken = type;
    return this;
  }

  /**
   * Appends <code>token</code> to the internal buffer and sets {@link #lastToken} to
   * {@link #OTHER}. If <code>separate</code> is <code>true</code>, a blank character is
   * inserted before the token, otherwise not.
   */

  public SQLComposer add (String token, boolean separate)
  {
    return this.add(token, OTHER, separate);
  }

  /**
   * Appends <code>token</code> to the internal buffer and sets {@link #lastToken} to
   * {@link #OTHER}. A blank character is inserted before the token automatically.
   */

  public SQLComposer add (String token)
  {
    return this.add(token, OTHER, true);
  }

  /**
   * Adds a <code>"SELECT"</code> token.
   */

  public SQLComposer addSELECT ()
  {
    return this.add("SELECT", this.lastToken != NONE);
  }

  /**
   * Adds a <code>"DISTINCT"</code> token.
   */

  public SQLComposer addDISTINCT ()
  {
    return this.add("DISTINCT");
  }

  /**
   * Adds an <code>"INSERT"</code> token.
   */

  public SQLComposer addINSERT ()
  {
    return this.add("INSERT", false);
  }

  /**
   * Adds an <code>"INTO"</code> token.
   */

  public SQLComposer addINTO ()
  {
    return this.add("INTO");
  }

  /**
   * Adds a <code>"UPDATE"</code> token.
   */

  public SQLComposer addUPDATE ()
  {
    return this.add("UPDATE", false);
  }

  /**
   * Adds a <code>"DELETE"</code> token.
   */

  public SQLComposer addDELETE ()
  {
    return this.add("DELETE", false);
  }

  /**
   * Adds a <code>"NULL"</code> token.
   */

  public SQLComposer addNULL ()
  {
    return this.add("NULL");
  }

  /**
   * Adds a <code>"CAST"</code> token.
   */

  public SQLComposer addCAST ()
  {
    return this.add("CAST");
  }

  /**
   * Adds a <code>"UNION"</code> token.
   */

  public SQLComposer addUNION ()
  {
    return this.add("UNION");
  }

  /**
   * Adds a <code>"JOIN"</code> token.
   */

  public SQLComposer addJOIN ()
  {
    return this.add("JOIN");
  }

  /**
   * Adds a <code>"OUTER"</code> token.
   */

  public SQLComposer addOUTER ()
  {
    return this.add("OUTER");
  }

  /**
   * Adds a <code>"LEFT"</code> token.
   */

  public SQLComposer addLEFT ()
  {
    return this.add("LEFT");
  }

  /**
   * Adds a <code>"RIGHT"</code> token.
   */

  public SQLComposer addRIGHT ()
  {
    return this.add("RIGHT");
  }

  /**
   * Adds a <code>"FULL"</code> token.
   */

  public SQLComposer addFULL ()
  {
    return this.add("FULL");
  }

  /**
   * Adds an <code>"ON"</code> token.
   */

  public SQLComposer addON ()
  {
    return this.add("ON");
  }

  /**
   * Adds a <code>"FROM"</code> token.
   */

  public SQLComposer addFROM ()
  {
    return this.add("FROM");
  }

  /**
   * Adds a <code>"WHERE"</code> token.
   */

  public SQLComposer addWHERE ()
  {
    return this.add("WHERE");
  }

  /**
   * Adds an <code>"AND"</code> token.
   */

  public SQLComposer addAND ()
  {
    return this.add("AND");
  }

  /**
   * Adds an <code>"OR"</code> token.
   */

  public SQLComposer addOR ()
  {
    return this.add("OR");
  }

  /**
   * Adds am <code>"IN"</code> token.
   */

  public SQLComposer addIN ()
  {
    return this.add("IN");
  }

  /**
   * Adds a <code>"VALUES"</code> token.
   */

  public SQLComposer addVALUES ()
  {
    return this.add("VALUES");
  }

  /**
   * Adds a <code>"SET"</code> token.
   */

  public SQLComposer addSET ()
  {
    return this.add("SET");
  }

  /**
   * Adds an <code>"AS"</code> token.
   */

  public SQLComposer addAS ()
  {
    return this.add("AS");
  }

  /**
   * Adds a <code>"NOT"</code> token.
   */

  public SQLComposer addNOT ()
  {
    return this.add("NOT");
  }

  /**
   * Adds a <code>"IS"</code> token.
   */

  public SQLComposer addIS ()
  {
    return this.add("IS");
  }

  /**
   * Adds an <code>"GROUP"</code> token.
   */

  public SQLComposer addGROUP ()
  {
    return this.add("GROUP");
  }

  /**
   * Adds an <code>"BY"</code> token.
   */

  public SQLComposer addBY ()
  {
    return this.add("BY");
  }

  /**
   * Adds a <code>"TRUE"</code> token.
   */

  public SQLComposer addTRUE ()
  {
    return this.add("TRUE");
  }

  /**
   * Adds a <code>"max"</code> token.
   */

  public SQLComposer addMax ()
  {
    return this.add("max");
  }

  /**
   * Adds an equality sign.
   */

  public SQLComposer addEq ()
  {
    return this.add("=");
  }

  /**
   * Adds a "!=" token.
   */

  public SQLComposer addNotEq ()
  {
    return this.add("!=");
  }

  /**
   * Adds a question mark.
   */

  public SQLComposer addQuestMark ()
  {
    return this.add("?");
  }

  /**
   * Adds an asterisk.
   */

  public SQLComposer addAsterisk ()
  {
    return this.add("*");
  }

  /**
   * Adds a comma.
   */

  public SQLComposer addComma ()
  {
    return this.add(",");
  }

  /**
   * Adds a semicolon.
   */

  public SQLComposer addSemicolon ()
  {
    return this.add(";");
  }

  /**
   * Adds an opening round parenthesis.
   */

  public SQLComposer addBlockStart ()
  {
    return this.add("(", PAREN_START, true);
  }

  /**
   * Adds a closing round parenthesis.
   */

  public SQLComposer addBlockEnd ()
  {
    return this.add(")", PAREN_END, false);
  }

  // --------------------------------------------------------------------------------
  // Adding column names
  // --------------------------------------------------------------------------------

  /**
   * Adds a column name. If the previous token was a column name or value, a comma is added
   * before. If <code>correlation</code> is not <code>null</code>,
   * <code>correlation + "."</code> is prepended to the column name.
   */

  public SQLComposer addColumn (String column, String correlation)
  {
    switch ( this.lastToken )
      {
      case COLUMN:
        this.buffer.append(",");
        break;
      case VALUE:
        this.buffer.append(",");
        break;
      case PAREN_START:
        break;
      default:
        this.buffer.append(" ");
      }
    if ( correlation != null )
      this.buffer.append(correlation).append(".");
    this.buffer.append(column);
    this.lastToken = COLUMN;
    return this;
  }

  /**
   * Adds a column name. If the previous token was a column name or value, a comma is added
   * before.
   */

  public SQLComposer addColumn (String column)
  {
    return this.addColumn(column, null);
  }

  /**
   * Adds all column names in the array <code>columns</code>. Before each column, a comma is
   * inserted if the previous token was a column name or value. If <code>correlation</code> is
   * not <code>null</code>, <code>correlation + "."</code> is prepended to each column name.
   */

  public SQLComposer addColumns (String[] columns, String correlation)
  {
    for (int i = 0; i < columns.length; i++)
      this.addColumn(columns[i], correlation);
    return this;
  }

  /**
   * Adds all column names in the array <code>columns</code>. Before each column, a comma is
   * inserted if the previous token was a column name or value.
   */

  public SQLComposer addColumns (String[] columns)
  {
    return this.addColumns(columns, null);
  }

  /**
   * Adds the keywork <code>NULL</code> as a column name. If the previous token was a column
   * name or value, a comma is added before.
   */

  public SQLComposer addColumnNull ()
  {
    return this.addColumn("NULL", null);
  }

  // --------------------------------------------------------------------------------
  // Adding table names
  // --------------------------------------------------------------------------------

  /**
   * Adds a table name. If the previous token was a table name, too, a comma is added
   * before. If <code>alias</code> is not <code>null</code>,
   * <code>alias + "."</code> is prepended to the table name.
   */

  public SQLComposer addTable (String table, String alias)
  {
    switch ( this.lastToken )
      {
      case TABLE:
        this.buffer.append(",");
        break;
      default:
        this.buffer.append(" ");
      }
    this.buffer.append(table);
    if ( alias != null )
      this.buffer.append(" AS ").append(alias);
    this.lastToken = TABLE;
    return this;
  }

  /**
   * Adds a table name. If the previous token was a table name, too, a comma is added
   * before.
   */

  public SQLComposer addTable (String table)
  {
    return this.addTable(table, null);
  }

  // --------------------------------------------------------------------------------
  // Adding column values
  // --------------------------------------------------------------------------------

  /**
   * Base method for all <code>addValue</code> methods. Adds a string unchanged (no quoting)
   * as a column value. If the previous token was a column name or value, a comma
   * is added before.
   */

  protected SQLComposer addValueRaw (String value)
  {
    switch ( this.lastToken )
      {
      case COLUMN:
        this.buffer.append(",");
        break;
      case VALUE:
        this.buffer.append(",");
        break;
      case PAREN_START:
        break;
      default:
        this.buffer.append(" ");
      }
    this.buffer.append(value);
    this.lastToken = VALUE;
    return this;
  }

  /**
   * Adds a string as a column value. If the previous token was a column name or value, a comma
   * is added before. The string is quoted (see method {@link SQLUtil#quote quote}) and
   * enclosed in single quotes.
   */

  public SQLComposer addValue (String value)
  {
    return this.addValueRaw(SQLUtil.toSQL(value));
  }

  /**
   * Adds an integer as a column value. If the previous token was a column name or value, a
   * comma is added before.
   */

  public SQLComposer addValue (int value)
  {
    return this.addValueRaw(Integer.toString(value));
  }

  /**
   * Adds a long integer as a column value. If the previous token was a column name or value, a
   * comma is added before.
   */

  public SQLComposer addValue (long value)
  {
    return this.addValueRaw(Long.toString(value));
  }

  /**
   * Adds a boolean as a column value. If the previous token was a column name or value, a
   * comma is added before.
   */

  public SQLComposer addValue (boolean value)
  {
    return this.addValueRaw(SQLUtil.toSQL(value));
  }

  /**
   * Adds a timestamp as a column value. If the previous token was a column name or value, a
   * comma is added before.
   */

  public SQLComposer addValue (Timestamp value)
  {
    return this.addValueRaw(SQLUtil.toSQL(value));
  }

  /**
   * Adds a date as a column value. If the previous token was a column name or value, a
   * comma is added before.
   */

  public SQLComposer addValue (Date value)
  {
    return this.addValueRaw(SQLUtil.toSQL(value));
  }

  /**
   * Adds a calendar as a column value. If the previous token was a column name or value, a
   * comma is added before.
   */

  public SQLComposer addValue (Calendar value)
  {
    return this.addValueRaw(SQLUtil.toSQL(value));
  }

  /**
   * Adds the specified object as a column value. If the previous token was a column value,
   * too, a comma is added before.
   */

  public SQLComposer addValue (Object object)
  {
    return this.addValueRaw(SQLUtil.toSQL(object));
  }

  /**
   * Adds all objects in the array <code>objects</code> as column values. Before each value,
   * a comma is inserted if the previous token was a value, too.
   */

  public SQLComposer addValues (Object[] objects)
  {
    for (int i = 0; i < objects.length; i++)
      this.addValue(objects[i]);
    return this;
  }

  /**
   * Adds all integers in the array <code>values</code> as column values. Before each value,
   * a comma is inserted if the previous token was a value, too.
   */

  public SQLComposer addValues (int[] values)
  {
    for (int i = 0; i < values.length; i++)
      this.addValue(values[i]);
    return this;
  }

  // --------------------------------------------------------------------------------
  // Adding types
  // --------------------------------------------------------------------------------

  /**
   * Adds an SQL type.
   */

  public SQLComposer addType (String type)
  {
    return this.add(type);
  }

  // --------------------------------------------------------------------------------
  // Adding INSERT data
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Adds a code fragment for an <code>INSERT</code> statement. The fragment comrises the
   *   column names, the <code>VALUES</code> keyword, and the corresponding column
   *   values. Thus, it has the form
   *   <pre>
   *   <var>column<sub>1</sub></var>, ... , <var>column<sub>n</sub></var> VALUES <var>value<sub>1</sub></var>, ... , <var>value<sub>n</sub></var></pre>
   *   where <var>column<sub>1</sub></var>, ... , <var>column<sub>n</sub></var> are the column
   *   names and <var>value<sub>1</sub></var>, ... , <var>value<sub>n</sub></var> the
   *   corresponding column values.
   *  </p>
   *  <p>
   *    The column names and values are specified by the parameters <code>columns</code> and
   *   <code>values</code>, respectively. The elements of the latter array are converted
   *   automatically into the correct SQL representation. The length of both arrays must be
   *   equal.
   *  </p>
   *
   * @param columns the column names
   * @param values the column values
   *
   * @throws SQLException if the length of <code>columns</code> and <code>values</code> do
   * not coincide.
   * @throws IllegalArgumentException if one of the values can not be converted into its SQL
   * representation.
   */

  public SQLComposer addInsertData (String[] columns, Object[] values)
    throws SQLException 
  {
    if ( columns.length != values.length )
      throw new SQLException
        ("Can not add INSERT data: Numbers of colums and values differ");
    this
      .addBlockStart().addColumns(columns).addBlockEnd()
      .addVALUES()
      .addBlockStart().addValues(values).addBlockEnd();
    return this;
  }

  /**
   * Adds a code fragment for an <code>INSERT</code> statement. The fragment is the same as
   * with {@link #addInsertData(String[], Object[]) addInsertData(String[], Object[])}. The
   * column names and values are given by the specified map, the keys of which are the
   * column names and the values the corresponding column values.
   */

  public SQLComposer addInsertData (Map data)
    throws SQLException
  {
    int length = data.size();
    String[] columns = new String[length];
    Object[] values = new Object[length];
    Iterator iterator = data.entrySet().iterator();
    int i = -1;
    while ( iterator.hasNext() )
      {
        i++;
        Map.Entry entry = (Map.Entry)iterator.next();
        columns[i] = (String)entry.getKey();
        values[i] = entry.getValue();
      }
    return this.addInsertData(columns, values);
  }

  // --------------------------------------------------------------------------------
  // Adding UPDATE data
  // --------------------------------------------------------------------------------

  /**
   * Adds the specified data as a <code>SET ...</code> block to an <code>UPDATE</code>
   * request.
   */

  public SQLComposer addUpdateData (Map data)
    throws SQLException
  {
    this.addSET();
    Iterator iterator = data.entrySet().iterator();
    while ( iterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)iterator.next();
        String column = (String)entry.getKey();
        Object value = entry.getValue();
        this.addColumn(column).addEq().addValue(value);
      }
    return this;
  }

  // --------------------------------------------------------------------------------
  // Adding aggregate function
  // --------------------------------------------------------------------------------

  /**
   * Adds a "sum" keyword.
   */

  public SQLComposer addSum ()
  {
    this.addColumn("sum");
    this.lastToken = OTHER;
    return this;
  }

  // --------------------------------------------------------------------------------
  // Adding function calls
  // --------------------------------------------------------------------------------

  /**
   * Adds a function call for the function with the specified name and argument.
   */

  public SQLComposer addFuncCall (String name, Object arg)
  {
    return this.add(name).add("(", false).add(SQLUtil.toSQL(arg), false).add(")", false);
  }

  /**
   * Adds a function call for the function with the specified name and argument.
   */

  public SQLComposer addFuncCall (String name, int arg)
  {
    return this.add(name).add("(", false).add(SQLUtil.toSQL(arg), false).add(")", false);
  }

  /**
   * Adds a function call for the function with the specified name and arguments.
   */

  public SQLComposer addFuncCall (String name, Object arg1, Object arg2)
  {
    return this
      .add(name)
      .add("(", false)
      .add(SQLUtil.toSQL(arg1), false)
      .add(",", false)
      .add(SQLUtil.toSQL(arg2), false)
      .add(")", false);
  }

  /**
   * Adds a function call for the function with the specified name and arguments.
   */

  public SQLComposer addFuncCall (String name, int arg1, Object arg2)
  {
    return this
      .add(name)
      .add("(", false)
      .add(SQLUtil.toSQL(arg1), false)
      .add(",", false)
      .add(SQLUtil.toSQL(arg2), false)
      .add(")", false);
  }

  /**
   * Adds a function call for the function with the specified name and arguments.
   */

  public SQLComposer addFuncCall (String name, Object arg1, int arg2)
  {
    return this
      .add(name)
      .add("(", false)
      .add(SQLUtil.toSQL(arg1), false)
      .add(",", false)
      .add(SQLUtil.toSQL(arg2), false)
      .add(")", false);
  }

  /**
   * Adds a function call for the function with the specified name and arguments.
   */

  public SQLComposer addFuncCall (String name, int arg1, int arg2)
  {
    return this
      .add(name)
      .add("(", false)
      .add(SQLUtil.toSQL(arg1), false)
      .add(",", false)
      .add(SQLUtil.toSQL(arg2), false)
      .add(")", false);
  }
}
