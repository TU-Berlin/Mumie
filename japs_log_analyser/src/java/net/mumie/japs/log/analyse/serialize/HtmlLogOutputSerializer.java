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

public class HtmlLogOutputSerializer extends AbstractLogOutputSerializer
{
  /**
   * Number of columns of the HTML table that displays the log output.
   */

  protected int numberOfColums = 0;

  /**
   * The CSS styles.
   */

  protected String cssStyles =
    "body\n" +
    "{\n" +
    "  margin: 2ex 1em 2ex 1em;\n" +
    "  font-family: monospace;\n" +
    "  font-size: 12pt;\n" +
    "}\n" +
    "table\n" +
    "{\n" +
    "  border-collapse: collapse;\n" +
    "}\n" +
    "td, th\n" +
    "{\n" +
    "  border-style: solid;\n" +
    "  border-width: 1px;\n" +
    "  border-color: #808080;\n" +
    "  padding: 0.1ex 0.5em 0.1ex 0.5em;\n" +
    "}\n" +
    "th\n" +
    "{\n" +
    "  font-weight: bold;\n" +
    "  background-color: #DCDCDC;\n" +
    "}\n" +
    "td.sourceName\n" +
    "{\n" +
    "  color: #473C8B;\n" +
    "}\n" +
    "td.lineNumber\n" +
    "{\n" +
    "  color: #D2691E;\n" +
    "}\n" +
    "td.priority\n" +
    "{\n" +
    "  color: #8A2BE2;\n" +
    "}\n" +
    "td.time\n" +
    "{\n" +
    "  color: #4169E1;\n" +
    "}\n" +
    "td.className\n" +
    "{\n" +
    "  color: #CD0000;\n" +
    "}\n" +
    "td.category\n" +
    "{\n" +
    "  color: #EE7600;\n" +
    "}\n" +
    "td.uri\n" +
    "{\n" +
    "  color: #228B22;\n" +
    "}\n" +
    "td.thread\n" +
    "{\n" +
    "  color: #008B8B;\n" +
    "}";

  /**
   * Sets a parameter.
   */

  public void setParameter (String name, String value)
    throws LogOutputProcessingException
  {
    if ( name.equals("target-file") )
      this.setTarget(value);
    else if ( name.equals("css-styles") )
      this.cssStyles = value;
    else
      throw new LogOutputProcessingException("Unknown parameter: " + name);
  }

  /**
   * Calls the corresponding super-class method, initializes the serializer, and writes the
   * beginning of the HTML page.
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

    this.out.println
      ("<html>\n" +
       "<head>\n" +
       "<title>Log Output</title>\n" +
       "<style type=\"text/css\">\n" +
       this.cssStyles + "\n" +
       "</style>\n" +
       "<body>\n" +
       "<table>\n" +
       "<tr>");

    this.numberOfColums = 0;

    if ( withSourceName )
      {
        numberOfColums++;
        this.out.println("<th>Source</th>");
      }
    if ( withLineNumber )
      {
        numberOfColums++;
        this.out.println("<th>Line</th>");
      }
    if ( withPriority )
      {
        numberOfColums++;
        this.out.println("<th>Priority</th>");
      }
    if ( withTime )
      {
        numberOfColums++;
        this.out.println("<th>Time</th>");
      }
    if ( withCategory )
      {
        numberOfColums++;
        this.out.println("<th>Category</th>");
      } 
    if ( withUri )
      {
        numberOfColums++; 
        this.out.println("<th>URI</th>");
      }
    if ( withThread )
      {
        numberOfColums++;
        this.out.println("<th>Thread</th>");
      }
    if ( withClassName )
      {
        numberOfColums++;
        this.out.println("<th>Class</th>");
      }
    if ( withMessage )
      {
        numberOfColums++;
        this.out.println("<th>Message</th>");
      }

    this.out.println("</tr>");
  }

  /**
   * Creates a HTML table row (<code>tr</code> element) and writes the specified data into
   * the cells (<code>td</code> elements). Excluded data are ignored.
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
    if ( highlight != null )
      this.out.println("<tr class=\"" + highlight + "\">");
    else
      this.out.println("<tr>");
    if ( this.withSourceName ) this.htmlElement("td", "sourceName", sourceName);
    if ( this.withLineNumber ) this.htmlElement("td", "lineNumber", lineNumber);
    if ( this.withPriority ) this.htmlElement("td", "priority", priority);
    if ( this.withTime ) this.htmlElement("td", "time", time);
    if ( this.withCategory ) this.htmlElement("td", "category", category);
    if ( this.withUri ) this.htmlElement("td", "uri", uri);
    if ( this.withThread ) this.htmlElement("td", "thread", thread);
    if ( this.withClassName ) this.htmlElement("td", "className", className);
    if ( this.withMessage ) this.htmlElement("td", "message", message);
    this.out.println("</tr>");
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
    this.out.println("<tr>");
    if ( this.withSourceName ) this.htmlElement("td", "sourceName", sourceName);
    if ( this.withLineNumber ) this.htmlElement("td", "lineNumber", lineNumber);
    if ( this.withMessage )
      {
        int colSpan = this.numberOfColums - 1;
        if ( this.withSourceName ) colSpan--;
        if ( this.withLineNumber ) colSpan--;
        // Fill space before message:
        if ( colSpan == 0 )
          this.out.println("<td></td>");
        else if ( colSpan > 0 )
          this.out.println("<td colspan=\"" + colSpan + "\"></td>");
        // Write line in massage column:
        this.htmlElement("td", "message", line);
      }
    else
      {
        // Write line in concatenated columns after line number:
        int colSpan = this.numberOfColums;
        if ( this.withSourceName ) colSpan--;
        if ( this.withLineNumber ) colSpan--;
        this.out.println
          ("<td colspan=\"" + colSpan + "\">" + line + "</td>");
      }
    this.out.println("</tr>");
  }

  /**
   * Writes the terminating HTML code (closing tags).
   */

  public void handleEnd ()
  {
    this.out.println
      ("</table>\n" +
       "</bodey>\n" +
       "</html>");
  }

  /**
   * Auxiliary method; writes a HTML element with the specified name, CSS class and
   * content. The content is a simple text child.
   */

  protected void htmlElement (String name, String cssClass, String content)
  {
    this.out.println
      ("<" + name + " class=\"" + cssClass + "\">" + content + "</" + name + ">");
  }

  /**
   * Auxiliary method; writes a HTML element with the specified name, CSS class and
   * the spcified number as content. The content is a simple text child.
   */

  protected void htmlElement (String name, String cssClass, int number)
  {
    this.htmlElement(name, cssClass, Integer.toString(number));
  }
}