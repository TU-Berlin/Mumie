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

package net.mumie.japs.log.analyse.serialize;

import net.mumie.japs.log.analyse.LogOutputProcessingException;

public class TextLogOutputSerializer extends AbstractLogOutputSerializer
{
  /**
   * Number of newlines between log records. Defaults to 1.
   */

  protected int recordSpace = 1;

  /**
   * Auxiliary variable; <code>true</code> if fields except the message fields are included,
   * <code>false</code> otherwise. Needed for formatting.
   */

  protected boolean withNonMessageFields = false;

  /**
   * Sets the number of newlines between log records. Default is 1. Values lower then 1 are
   * ignored.
   */

  public void setRecordSpace (int recordSpace)
  {
    if ( recordSpace >= 1 )
      this.recordSpace = recordSpace;
  }

  /**
   * Sets the number of newlines between log records to the value represented by the
   * specified string. The string must be convertable to an integer. The default value is
   * 1. Values lower then 1 are ignored.
   */

  public void setRecordSpace (String value)
    throws LogOutputProcessingException
  {
    try
      {
        this.setRecordSpace(Integer.parseInt(value));
      }
    catch (Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {
    if ( name.equals("target-file") )
      this.setTarget(value);
    else if ( name.equals("record-space") )
      this.setRecordSpace(value);
    else
      throw new LogOutputProcessingException("Unknown parameter: " + name);
  }

  /**
   *
   */

  public void handleStart (boolean withSourceName,
                           boolean withLineNumber,
                           boolean withPriority,
                           boolean withTime,
                           boolean withCategory,
                           boolean withUri,
                           boolean withThread,
                           boolean withClassName,
                           boolean withMessage)
    throws LogOutputProcessingException 
  {
    super.handleStart
      (withSourceName,
       withLineNumber,
       withPriority,
       withTime,
       withCategory,
       withUri,
       withThread,
       withClassName,
       withMessage);

    this.withNonMessageFields =
      withSourceName ||
      withLineNumber ||
      withPriority ||
      withTime ||
      withCategory ||
      withUri ||
      withThread ||
      withClassName;
  }

  /**
   * 
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
    for (int i = 0; i < this.recordSpace; i++)
      this.out.println();
    if ( this.withSourceName ) this.out.print(sourceName + " ");
    if ( this.withLineNumber ) this.out.print(lineNumber);
    if ( this.withSourceName || this.withLineNumber ) this.out.print(":  ");
    if ( this.withPriority ) this.out.print(priority + "   ");
    if ( this.withTime ) this.out.print(time + "   ");
    if ( this.withCategory ) this.out.print("[" + category + "] ");
    if ( this.withUri ) this.out.print("(" + uri + ") ");
    if ( this.withThread ) this.out.print(thread + (this.withClassName ? "/" : " "));
    if ( this.withClassName ) this.out.print(className);
    if ( this.withNonMessageFields ) this.out.print(": ");
    if ( this.withMessage )  this.out.print(message);
  }

  /**
   * Creates a HTML table row (<code>tr</code> element) and writes the unformatted line into
   * a cell (<code>td</code> element). If messages are not excluded, the line is put into
   * the "message" cell, otherwise, into the concatenation of all cells except the line
   * number cell.
   */

  public void handleUnformattedLine (String sourceName,
                                     int lineNumber,
                                     String line)
    throws LogOutputProcessingException 
  {
    this.out.println();
    if ( this.withSourceName ) this.out.print(sourceName + " ");
    if ( this.withLineNumber ) this.out.print(lineNumber);
    if ( this.withSourceName || this.withLineNumber ) this.out.print(":  ");
    this.out.print(line);
  }

  /**
   * Writes the terminating HTML code (closing tags).
   */

  public void handleEnd ()
  {
    this.out.println();
  }
}