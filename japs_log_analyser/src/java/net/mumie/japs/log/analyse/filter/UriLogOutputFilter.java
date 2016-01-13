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

package net.mumie.japs.log.analyse.filter;

import net.mumie.japs.log.analyse.LogOutputProcessingException;

/**
 * <p>
 *   Filters by an URI. Only those log records whose "uri" field equals a given URI are
 *   passed. Recognizes the following parameters:
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Name</th><th>Description</th><th>Required</th>
 *   </tr>
 *   <tr>
 *     <td>
 *       <code>uri</code>
 *     </td>
 *     <td>
 *       The URI to filter.
 *     </td>
 *     <td>
 *       Yes
 *     </td>
 *   </tr>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UriLogOutputFilter.java,v 1.1 2006/01/15 20:22:43 rassy Exp $</code>
 */

public class UriLogOutputFilter extends DefaultLogOutputFilter
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The URI.
   */

  protected String uri = null;

  /**
   * Internal auxiliary variable, <code>true</code> if the last URI test was successful,
   * <code>false</code> otherwise.
   */

  protected boolean matched = false;

  // --------------------------------------------------------------------------------
  // Setting the URI.
  // --------------------------------------------------------------------------------

  /**
   * Sets the URI. Throws an exception if the specified URI is <code>null</code>.
   */

  public void setUri (String uri)
    throws LogOutputProcessingException 
  {
    if ( uri == null )
      throw new LogOutputProcessingException("URI is null");
    this.uri = uri;
  }

  // --------------------------------------------------------------------------------
  // Parameterization
  // --------------------------------------------------------------------------------

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {
    if ( name.equals("uri") )
      this.setUri(value);
    else
      throw new LogOutputProcessingException("Unknown parameter: " + name);
  }

  // --------------------------------------------------------------------------------
  // Implementation of the handler methods
  // --------------------------------------------------------------------------------

  /**
   * Handles a formatted log line.
   */

  public void handleFormattedLine (String sourceName,
                                   int lineNumber,
                                   String priority,
                                   String time,
                                   String category,
                                   String uri,
                                   String thread,
                                   String className,
                                   String message,
                                   String highlight)
    throws LogOutputProcessingException
  {
    if ( this.withUri && this.checkUri(uri) )
      this.handler.handleFormattedLine
        (sourceName,
         lineNumber,
         priority,
         time,
         category,
         uri,
         thread,
         className,
         message,
         highlight);
  }

  /**
   * Handles a unformatted log line.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException
  {
    if ( this.withUri && this.matched )
      this.handler.handleUnformattedLine(sourceName, lineNumber, line);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Checks if the specified URI equals {@link #uri uri}, sets {@link #matched matched}
   * according to the result, and returns {@link #matched matched}.
   */

  public boolean checkUri (String uri)
  {
    this.matched = uri.equals(this.uri);
    return this.matched;
  }
}