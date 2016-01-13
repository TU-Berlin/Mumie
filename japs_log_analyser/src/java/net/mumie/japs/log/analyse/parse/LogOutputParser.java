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

package net.mumie.japs.log.analyse.parse;

import java.io.BufferedReader;
import net.mumie.japs.log.analyse.LogOutputProcessingException;
import net.mumie.japs.log.analyse.LogOutputProducer;
import net.mumie.japs.log.analyse.source.LogOutputSource;
import net.mumie.japs.log.analyse.LogOutputHandler;

/**
 * <p>
 *   Parses the log output.
 * </p>
 * <p>
 *   A <code>LogOutputParser</code> reads log output from a
 *   {@link LogOutputSource LogOutputSource}, parses it, and sends the results as
 *   so-called "log output events" to a {@link LogOutputHandler LogOutputHandler}. A log
 *   output event is a set of so-called "fields". Each log output event corresponds to a
 *   single line in the log ouput. Usually, such a line has a fixed, well-defined format
 *   specified in the log configuration file. Such a line corresponds to a singe log
 *   message. The fields are in that case: 
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Field</th><th>Description</th><th>Example</th>
 *   </tr>
 *   <tr>
 *     <td>Source name</td>
 *     <td>Name of the log output source, usually the name of a log file</td>
 *     <td><code>logs/core.log</code></td>
 *   </tr>
 *   <tr>
 *     <td>Line number</td>
 *     <td>Line number in the log output source</td>
 *     <td><code>1523</code></td>
 *   </tr>
 *   <tr>
 *     <td>Priority</td>
 *     <td>The priority or "level" of the log message</td>
 *     <td><code>DEBUG</code></td>
 *   </tr>
 *   <tr>
 *     <td>Time</td>
 *     <td>Timestamp of the log message</td>
 *     <td><code></code></td>
 *   </tr>
 *   <tr>
 *     <td>Category</td>
 *     <td></td>
 *     <td><code></code></td>
 *   </tr>
 *   <tr>
 *     <td>URI</td>
 *     <td>The URI that was processed when the log message was created</td>
 *     <td><code></code></td>
 *   </tr>
 *   <tr>
 *     <td>Thread</td>
 *     <td>Name of the thread in which the log message was created</td>
 *     <td><code></code></td>
 *   </tr>
 *   <tr>
 *     <td>Class name</td>
 *     <td>Name of the class which issued the log message</td>
 *     <td><code></code></td>
 *   </tr>
 *   <tr>
 *     <td>Message</td>
 *     <td>The text of the log message</td>
 *     <td><code></code></td>
 *   </tr>
 *   <tr>
 *     <td>Highlight</td>
 *     <td>A keyword to group together certain log events</td>
 *     <td><code></code></td>
 *   </tr>
 * </table>
 * <p>
 *   The text of some log messages may be spread over several lines, which results in
 *   several log events. The first log events is of the type just described. Its message
 *   field contains the first line of the text. The following log events correspond to the
 *   remaining lines of the text. The fields are in that case: 
 * </p>
 * <table class="genuine">
 *   <tr>
 *     <th>Field</th><th>Description</th><th>Example</th>
 *   </tr>
 *   <tr>
 *     <td>Source name</td>
 *     <td>Name of the log output source, usually the name of a log file</td>
 *     <td><code>logs/core.log</code></td>
 *   </tr>
 *   <tr>
 *     <td>Line number</td>
 *     <td>Line number in the log output source</td>
 *     <td><code>1523</code></td>
 *   </tr>
 *   <tr>
 *     <td>Line</td>
 *     <td>Content of the line</td>
 *     <td><code></code></td>
 *   </tr>
 * </table>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LogOutputParser.java,v 1.4 2006/11/09 00:19:12 rassy Exp $</code>
 */

public interface LogOutputParser extends LogOutputProducer
{
  /**
   * Notifies the handler that analysing starts.
   */

  public void notifyStart ()
    throws LogOutputProcessingException;

  /**
   * Parses the specified source and sends the parse results to the handler.
   */

  public void parse (LogOutputSource source)
    throws LogOutputProcessingException;

  /**
   * Notifies the handler that analysing ends.
   */

  public void notifyEnd ()
    throws LogOutputProcessingException;
}
