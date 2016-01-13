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

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *   Converter for transforming PreSQL to Java.
 * </p>
 * <p>
 *   Instances of this class convert PreSQL code to Java source code. After compilation, the
 *   latter produces SQL code when executed. PreSQL is useful for autocoding Java classes
 *   which dynamically produce SQL code.
 * </p>
 * <p>
 *   For the PreSQL language, see the corresponding MUMIE specification.
 * </p>
 * <p>
 *   The Java code created by this class consists of method calls of
 *   {@link SQLComposer SQLComposer}. All calls are concatenated with dots. For example,
 * </p>
 * <pre>
 *   SELECT column("name") FROM table(dbTable) where column(idColumn) = value(docId)</pre>
 * <p>
 *   is converted to
 * </p>
 * <pre>
 *   .addSELECT()
 *   .addColumn("name")
 *   .addFROM()
 *   .addTable(dbTable)
 *   .addWHERE()
 *   .addColumn(idColumn)
 *   .addEq()
 *   .addValle(docId)</pre>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SQLComposerAutocoder.java,v 1.7 2007/07/16 11:01:05 grudzin Exp $</code>
 */

public class SQLComposerAutocoder
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The source string.
   */

  protected String source = null;

  /**
   * The current parse position in the source string.
   */

  protected int sourcePos = -1;

  /**
   * The length of the source string
   */

  protected int sourceLength = -1;

  /**
   * The last found token.
   */

  protected String lastToken = null;

  /**
   * The "add" methods of {@link SQLComposer SQLComposer}
   */

  protected List methodNames;

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>SQLComposerAutocoder</code>.
   */

  public SQLComposerAutocoder ()
  {
    Method[] methods = SQLComposer.class.getMethods();
    this.methodNames = new ArrayList();
    for (int i = 0; i < methods.length; i++)
      {
        String name = methods[i].getName();
        if ( name.startsWith("add") )
          this.methodNames.add(name);
      }
  }

  // --------------------------------------------------------------------------------
  // Tester methods
  // --------------------------------------------------------------------------------

  /**
   * Tests for whitespaces at the source position. If finds some, sets
   * {@link #lastToken lastToken} to the found whitespaces and returns <code>true</code>;
   * otherwise, lets {@link #lastToken lastToken} unchanged and returns <code>false</code>.
   */

  protected boolean testForWhitespace ()
  {
    boolean success = false;
    int pos = this.sourcePos;
    while ( pos < this.sourceLength &&
            Character.isWhitespace(this.source.charAt(pos)) )
      pos++;
    if ( pos > this.sourcePos )
      {
        success = true;
        this.lastToken = this.source.substring(this.sourcePos, pos);
        this.sourcePos = pos;
      }
    return success;
  }

  /**
   * Tests for an identifier at the source position. If finds one, sets
   * {@link #lastToken lastToken} to the found identifyer and returns <code>true</code>;
   * otherwise, lets {@link #lastToken lastToken} unchanged and returns <code>false</code>.
   */

  protected boolean testForIdentifier ()
  {
    final String IDENTIFIER_CHARS =
      "abcdefghijklmnopqrstuvwxyz" +
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
      "0123456789" +
      "_";
    boolean success = false;
    int pos = this.sourcePos;
    while ( pos < this.sourceLength &&
            IDENTIFIER_CHARS.indexOf(this.source.charAt(pos)) != -1 )
      pos++;
    if ( pos > this.sourcePos )
      {
        success = true;
        this.lastToken = this.source.substring(this.sourcePos, pos);
        this.sourcePos = pos;
      }
    return success;
  }

  /**
   * Tests for a block enclosed in round parenthesis. If finds one, sets
   * {@link #lastToken lastToken} to the found block and returns <code>true</code>;
   * otherwise, lets {@link #lastToken lastToken} unchanged and returns <code>false</code>.
   */

  protected boolean testForRoundParenBlock ()
    throws SQLComposerAutocoderException
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == '(' )
      {
        int pos = this.sourcePos+1;
        int depth = 1;
        boolean doubleQuoted = false;
        boolean singleQuoted = false;
        boolean backslashed = false;
        boolean backslashNext = false;
        while ( pos < this.sourceLength && depth > 0 )
          {
            backslashed = backslashNext;
            backslashNext = false;
            switch ( this.source.charAt(pos) )
              {
              case '(':
                if ( ! ( doubleQuoted || singleQuoted ) )
                  depth++;
                break;
              case ')':
                if ( ! ( doubleQuoted || singleQuoted ) )
                  depth--;
                break;
              case '"':
                if ( !singleQuoted )
                  {
                    if ( !doubleQuoted )
                      doubleQuoted = true;
                    else if ( !backslashed )
                      doubleQuoted = false;
                  }
                break;
              case '\'':
                if ( !doubleQuoted )
                  {
                    if ( !singleQuoted )
                      singleQuoted = true;
                    else if ( !backslashed )
                      singleQuoted = false;
                  }
                break;
              case '\\':
                if ( doubleQuoted || singleQuoted )
                  {
                    if ( !backslashed )
                      backslashNext = true;
                  }
                break;
              }
            pos++;
          }
        if ( depth > 0 )
          throw new SQLComposerAutocoderException
            (this.source, this.sourcePos, "Missing closing \")\" paren");
        success = true;
        this.lastToken = this.source.substring(this.sourcePos, pos);
        this.sourcePos = pos;
      }
    return success;
  }

  /**
   * Tests for a <code>"="</code>. If finds one, sets {@link #lastToken lastToken}
   * to <code>"="</code> and returns <code>true</code>; otherwise, lets
   * {@link #lastToken lastToken} unchanged and returns <code>false</code>. 
   */

  protected boolean testForEqualitySign ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == '=' )
      {
        success = true;
        this.lastToken = "=";
        this.sourcePos++;
      }
    return success;
  }

  /**
   * Tests for a <code>"!="</code> expression. If finds one, sets
   * {@link #lastToken lastToken} to <code>"!="</code> and returns <code>true</code>;
   * otherwise, lets {@link #lastToken lastToken} unchanged and returns <code>false</code>.
   */

  protected boolean testForNotEqExpression ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == '!' &&
         this.sourcePos < this.sourceLength &&
         this.source.charAt(this.sourcePos+1) == '=' )
      {
        success = true;
        this.lastToken = "!=";
        this.sourcePos+=2;
      }
    return success;
  }

  /**
   * Tests for a <code>"?"</code>. If finds one, sets {@link #lastToken lastToken}
   * to <code>"?"</code> and returns <code>true</code>; otherwise, lets
   * {@link #lastToken lastToken} unchanged and returns <code>false</code>. 
   */

  protected boolean testForQuestionMark ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == '?' )
      {
        success = true;
        this.lastToken = "?";
        this.sourcePos++;
      }
    return success;
  }

  /**
   * Tests for a <code>"*"</code>. If finds one, sets {@link #lastToken lastToken}
   * to <code>"*"</code> and returns <code>true</code>; otherwise, lets
   * {@link #lastToken lastToken} unchanged and returns <code>false</code>. 
   */

  protected boolean testForAsterisk ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == '*' )
      {
        success = true;
        this.lastToken = "*";
        this.sourcePos++;
      }
    return success;
  }

  /**
   * Tests for a <code>","</code>. If finds one, sets {@link #lastToken lastToken}
   * to <code>","</code> and returns <code>true</code>; otherwise, lets
   * {@link #lastToken lastToken} unchanged and returns <code>false</code>. 
   */

  protected boolean testForComma ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == ',' )
      {
        success = true;
        this.lastToken = ",";
        this.sourcePos++;
      }
    return success;
  }

  /**
   * Tests for a <code>";"</code>. If finds one, sets {@link #lastToken lastToken}
   * to <code>";"</code> and returns <code>true</code>; otherwise, lets
   * {@link #lastToken lastToken} unchanged and returns <code>false</code>. 
   */

  protected boolean testForSemicolon ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == ';' )
      {
        success = true;
        this.lastToken = ";";
        this.sourcePos++;
      }
    return success;
  }

  /**
   * Tests for a <code>"{"</code>. If finds one, sets {@link #lastToken lastToken}
   * to <code>"{"</code> and returns <code>true</code>; otherwise, lets
   * {@link #lastToken lastToken} unchanged and returns <code>false</code>. 
   */

  protected boolean testForLeftCurrlyParen ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == '{' )
      {
        success = true;
        this.lastToken = "{";
        this.sourcePos++;
      }
    return success;
  }

  /**
   * Tests for a <code>"}"</code>. If finds one, sets {@link #lastToken lastToken}
   * to <code>"}"</code> and returns <code>true</code>; otherwise, lets
   * {@link #lastToken lastToken} unchanged and returns <code>false</code>. 
   */

  protected boolean testForRightCurrlyParen ()
  {
    boolean success = false;
    if ( this.source.charAt(this.sourcePos) == '}' )
      {
        success = true;
        this.lastToken = "}";
        this.sourcePos++;
      }
    return success;
  }

  // --------------------------------------------------------------------------------
  // Converting
  // --------------------------------------------------------------------------------

  /**
   * Converts the given source string to Java code.
   */

  public String convert (String source)
    throws SQLComposerAutocoderException
  {
    this.source = source;
    this.sourcePos = 0;
    this.sourceLength = source.length();
    StringBuffer buffer = new StringBuffer();

    while ( this.sourcePos < this.sourceLength )
      {
        if ( this.testForWhitespace() )
          buffer.append(this.lastToken);
        else if ( this.testForIdentifier() )
          {
            buffer.append(".").append(this.tokenToMethodName(this.lastToken));
            if ( this.testForRoundParenBlock() )
              buffer.append(this.lastToken);
            else
              buffer.append("()");
          }
        else if ( this.testForEqualitySign() )
          buffer.append(".addEq()");
        else if ( this.testForNotEqExpression() )
          buffer.append(".addNotEq()");
        else if ( this.testForQuestionMark() )
          buffer.append(".addQuestMark()");
        else if ( this.testForAsterisk() )
          buffer.append(".addAsterisk()");
        else if ( this.testForComma() )
          buffer.append(".addComma()");
        else if ( this.testForSemicolon() )
          buffer.append(".addSemicolon()");
        else if ( this.testForLeftCurrlyParen() )
          buffer.append(".addBlockStart()");
        else if ( this.testForRightCurrlyParen() )
          buffer.append(".addBlockEnd()");
        else
          throw new SQLComposerAutocoderException
            (this.source, this.sourcePos, "Can not identify token");
      }
    return buffer.toString();
  }

  /**
   * Converts the given source string to Java code, and formats it according to the
   * specified indent and indent step.
   */

  public String convert (String source, int indent, int indentStep)
    throws SQLComposerAutocoderException
  {
    // Generate code:
    String code = this.convert(source);

    try
      {
        // Remove leading and trailing whitespaces:
        code = code.trim();

        // Get a reader to read from the code:
        BufferedReader reader =
          new BufferedReader(new StringReader(code));

        // Indent code:
        StringBuffer buffer = new StringBuffer();
        String line;
        boolean first = true;
        while ( (line = reader.readLine()) != null )
          {
            line = line.trim();

            // Add separator (newline) if necessary:
            if ( !first)
              buffer.append("\n");
            first = false;

            // Intent line:
            if ( line.equals(".addBlockStart()") )
              {
                indent += indentStep;
                line = fillSpace(indent) + line;
                indent += indentStep;
              }
            else if ( line.equals(".addBlockEnd()") && indent >= indentStep )
              {
                indent -= indentStep;
                line = fillSpace(indent) + line;
                if ( indent >= indentStep )
                  indent -= indentStep;
              }
            else
              {
                line = fillSpace(indent) + line;
              }

            // Add line:
            buffer.append(line);
          }

        reader.close();
        return buffer.toString();
      }
    catch (Exception exception)
      {
        throw new SQLComposerAutocoderException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Converts a token to the corresponding method name.
   */

  protected String tokenToMethodName (String token)
    throws SQLComposerAutocoderException
  {
    String name =
      (token.equals("token")
       ? "add"
       : "add"
         + Character.toUpperCase(token.charAt(0))
         + token.substring(1));
    if ( !this.methodNames.contains(name) )
      throw new SQLComposerAutocoderException
        (this.source, this.sourcePos, "Unknwon method name: " + name);
    return name;
  }

  /**
   * Tests for the specifgied string at the current parse position. Returns true if the
   * string is found, otherwise false.
   */
  // Currently unused

  protected boolean testFor (String string)
  {
    boolean success = true;
    for (int i = this.sourcePos;
         success && i < this.source.length() && i - this.sourcePos < string.length();
         i++)
      success = ( this.source.charAt(i) == string.charAt(i - this.sourcePos) );
    return success;
  }

  /**
   * 
   */

  protected static String fillSpace (int length)
  {
    char[] spaceChars = new char[length];
    Arrays.fill(spaceChars, ' ');
    return new String(spaceChars);
  }
}
