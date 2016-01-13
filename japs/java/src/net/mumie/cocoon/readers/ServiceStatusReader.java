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

package net.mumie.cocoon.readers;

import java.io.IOException;
import java.io.PrintStream;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.reading.AbstractReader;

/**
 * <p>
 *   TODO
 * </p>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <span class="file">$Id: ServiceStatusReader.java,v 1.8 2007/07/11 15:38:48 grudzin Exp $</span>
 */

public class ServiceStatusReader extends AbstractReader
{
  /**
   * <p>
   *   Returns the mime type of the document (<code>"text/plain"</code>). 
   * </p>
   * @return the mime type of the document. 
   */

  public String getMimeType ()
  {
    this.getLogger().debug("getMimeType 1/1");
    return "text/plain";
  }

  /**
   * Generates the response.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    try
      {
        String className = ParamUtil.getAsString(this.parameters, "class-name");
        PrintStream out = new PrintStream(this.out);
        ServiceStatus.statusToStream(className, out);
        out.flush();
        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }
}
