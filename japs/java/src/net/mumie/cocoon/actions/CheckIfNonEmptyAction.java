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
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Checks if a string is non-empty. Recognizes the following parameters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>test</code></td>
 *       <td>The string to check</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>trim</code></td>
 *       <td>Whether the string is trimmed before the check. Boolean</td>
 *       <td>No. Default is <code>"no".</code></td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   If the string is non-empty, the {@link #act act} method returns an empty {@link Map Map}
 *   object; otherwise, it returns null.
 * </p>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CheckIfNonEmptyAction.java,v 1.1 2007/11/13 12:49:27 rassy Exp $</code>
 */

public class CheckIfNonEmptyAction extends AbstractAction
{
  /**
   * See class description.
   */

  public Map act (Redirector redirector, SourceResolver resolver, Map objectModel, String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    this.getLogger().debug("act 1/2: started");
    try
      {
        String test = ParamUtil.getAsString(parameters, "test", "");
        boolean trim = ParamUtil.getAsBoolean(parameters, "trim", false);
        this.getLogger().debug
          ("act 2/2: done." +
           " test = " + test +
           ", trim = " + trim);
        if ( trim ) test = test.trim();
        return (test.length() > 0 ? EMPTY_MAP : null);
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }
}