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

package net.mumie.cocoon.actions;

import java.util.Map;
import java.util.HashMap;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Response;

/**
 * <p>
 *   Adds headers to the Http response.
 * </P>
 * <p>
 *   All key-value pairs passed to this action as parameters are set as response headers.
 * </P>
 * <p>
 *   The {@link #act act} method always returns a non-null <code>Map</code> with a single
 *   entry. The name of the entry is the full qualified name of this class, the value the string
 *   <code>"true"</code>. Normally, you will not need this returned data but only the
 *   functionality of setting response headers.
 * </P>
 * <p>
 *   NOTE: This class differs from {@link org.apache.cocoon.acting.HttpHeaderAction} in two
 *   aspects: 1. The latter sets all response headers also as sitemap parameters; 2. the
 *   latter allows for default headers that can not be overwritten.
 * </p>
 * <p>
 *   Example (sitemap excerpt):
 * </p>
 * <pre>
 *   &lt;map:actions&gt;
 *     &lt;!-- ... --&gt;
 *     &lt;map:action name="add-response-headers"
 *                 src="net.mumie.cocoon.actions.AddResponseHeadersAction"/&gt;
 *   &lt;/map:actions&gt;
 * 
 *     &lt;!-- ... --&gt;
 * 
 *   &lt;map:pipelines&gt;
 * 
 *     &lt;map:pipeline&gt;
 *       &lt;map:match pattern="foo/bar"&gt;
 *         &lt;map:act type="add-response-headers"&gt;
 *           &lt;map:parameter name="X-Mumie-Version" value="1.0.2"/&gt;
 *           &lt;map:parameter name="X-Mumie-Login-Required" value="yes"/&gt;
 *           &lt;!-- code --&gt;
 *         &lt;/map:act&gt;
 *       &lt;/map:match&gt;
 *     &lt;/map:pipeline&gt;
 * 
 *   &lt;/map:pipelines&gt;</pre>
 * <p>
 *   This will set the response headers <code>"X-Mumie-Version"</code> and
 *   <code>"X-Mumie-Login-Required"</code> with the specified values.
 * </p>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Revision: 1.5 $</code>
 */

public class AddResponseHeadersAction extends AbstractAction
{
  /*
   * See class description.
   */

  public Map act (Redirector redirector, SourceResolver resolver, Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    this.getLogger().debug("act 1/2: started");
    try
      {
        Response response = ObjectModelHelper.getResponse(objectModel);
        String [] parameterNames = parameters.getNames();
        int count = -1;
        for (int i = 0; i < parameterNames.length; i++)
          {
            String name = parameterNames[i];
            String value = parameters.getParameter(parameterNames[i]);
            this.getLogger().debug("act 1/2." + ++count + ": name = " + name + ", value = " + value);
            response.addHeader(name, value);
          }
        Map sitemapParameters = new HashMap();
        sitemapParameters.put(AddResponseHeadersAction.class.getName(), "true");
        this.getLogger().debug("act 2/2: done");
        return sitemapParameters;
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }
}
