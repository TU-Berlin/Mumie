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

package net.mumie.cocoon.sitemap;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the parameter block of sitemap autocoding functions.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ParamBlockParser.java,v 1.4 2007/07/11 15:38:50 grudzin Exp $</code>
 */

public class ParamBlockParser
{
  /**
   * The currently parsed token. Characters except those with a special meaning are appended
   * to this string buffer as the parser steps through the source.
   */

  protected StringBuffer token;

  /**
   * The list of tokens under construction.
   */

  protected List tokens;

  /**
   * Adds the currently parsed token to the token list (see {@link #token} and
   * {@link tokens}, respectively). If {@link #token} is <code>null</code>, which means
   * there is no currently parsed token, does nothing. Otherwise, converts {@link #token} to
   * a string, adds it to {@link tokens}, and sets {@link #token} to <code>null</code>.
   */

  protected void flushToken ()
  {
    if ( this.token != null )
      {
        this.tokens.add(this.token.toString());
        this.token = null;
      }
  }

  /**
   * Appends the specified character to the currently parsed token ({@link #token}). If
   * {@link #token} is <code>null</code>, it is set to a newliy created string buffer
   * before.
   */

  protected void addToToken (char c)
  {
    if ( this.token == null )
      this.token = new StringBuffer();
    this.token.append(c);
  }

  /**
   * Starts a new token. This token becomes the new "currently parsed token". Usually, it is
   * not necessary to start a new token explicitly; this is done automatically by
   * {@link #addToToken addToToken}. This method is needed only to parse void tokens
   * specified as <code>''</code> or <code>""</code>.
   */

  protected void startToken ()
  {
    this.token = new StringBuffer();
  }

  /**
   * Parses the specified string and returns the tokens.
   */

  public String[] parse (String source)
    throws ParamBlockParseException
  {
    this.token = null;
    this.tokens = new ArrayList();
    boolean singleQuoted = false;
    boolean doubleQuoted = false;
    boolean nextMustBeSeparator = false;
    int pos;
    for (pos = 0; pos < source.length(); pos++)
      {
        char c = source.charAt(pos);

        if ( nextMustBeSeparator && ( !Character.isWhitespace(c) || c == ',' ) )
          throw new ParamBlockParseException
            (source, pos, "Parse error: Expected separator");

        if ( c == '\\' )
          {
            pos++;
            if ( pos >= source.length() )
              throw new ParamBlockParseException
                (source, pos-1, "Backslash must be followed by a character");
            this.addToToken(source.charAt(pos));
          }
        else if ( c == '"' )
          {
            if ( singleQuoted )
              this.addToToken(c);
            else if ( doubleQuoted )
              doubleQuoted = false;
            else
              {
                this.startToken();
                doubleQuoted = true;
              }
          }
        else if ( c == '\'' )
          {
            if ( doubleQuoted )
              this.addToToken(c);
            else if ( singleQuoted )
              singleQuoted = false;
            else
              {
                this.startToken();
                singleQuoted = true;
              }
          }
        else if ( Character.isWhitespace(c) )
          {
            if ( doubleQuoted  || singleQuoted )
              this.addToToken(c);
            else if ( this.token != null )
              nextMustBeSeparator = true;
          }
        else if ( c == ',' )
          {
            if ( doubleQuoted  || singleQuoted )
              this.addToToken(c);
            else if ( this.token != null )
              {
                this.flushToken();
                nextMustBeSeparator = false;
              }
            else
              throw new ParamBlockParseException
                (source, pos, "Parse error (extra separator?)");
          }
        else if ( ( c == '(' || c == ')' ) &&
                  !( doubleQuoted || singleQuoted ) )
          throw new ParamBlockParseException
            (source, pos, "Unquoted special character: " + c);
        else
          {
            this.addToToken(c);
          }
      }
    if ( this.token == null )
      throw new ParamBlockParseException
        (source, pos, "Parse error (extra separator?)");
    this.flushToken();
    String[] tokensAsArray = (String[])this.tokens.toArray(new String[this.tokens.size()]);
    this.token = null;
    this.tokens = null;
    return tokensAsArray;
  }
}
