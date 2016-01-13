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

package net.mumie.cocoon.generators;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.cocoon.ProcessingException;
import net.mumie.cocoon.session.SessionXMLizer;

public class SessionListGenerator extends ServiceableGenerator
{
  /**
   * 
   */

  public void generate ()
    throws ProcessingException
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    SessionXMLizer sessionXMLizer = null;
    try
      {
        sessionXMLizer = (SessionXMLizer)this.manager.lookup(SessionXMLizer.ROLE);
        sessionXMLizer.sessionsToSAX(this.contentHandler, true);
      }
    catch (Exception exception)
      {
        throw new ProcessingException("Caught exception: " + exception);
      }
    finally
      {
        if ( sessionXMLizer != null )
          this.manager.release(sessionXMLizer);
      }
  }
}
