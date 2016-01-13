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

import java.lang.reflect.Method;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.generation.AbstractGenerator;
import org.xml.sax.ContentHandler;

/**
 * Generates a status report for a service class.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ServiceStatusGenerator.java,v 1.5 2007/07/11 15:38:45 grudzin Exp $</code>
 */

public class ServiceStatusGenerator extends AbstractGenerator 
{
  /**
   * Generates the status report.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
        String className = ParamUtil.getAsString(this.parameters, "class-name");
        ServiceStatus.statusToSAX(className, this.contentHandler, true);
        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }
}
