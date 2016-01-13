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

package net.mumie.cocoon.matchers;

import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.ObjectModelHelper;
// import org.apache.cocoon.sitemap.PatternException;
import org.apache.cocoon.matching.Matcher;

public class ListURIMatcher extends AbstractLogEnabled
  implements Matcher
{
  /**
   * Checks if the requested URI equals an element <code>pattern</code>, which must be a
   * list of Strings.
   */

  public Map match (String pattern, Map objectModel, Parameters parameters)
  {
    final String METHOD_NAME = "match";
    this.getLogger().debug(METHOD_NAME + "1/4: started");
    String uri = ObjectModelHelper.getRequest(objectModel).getSitemapURI();
    this.getLogger().debug(METHOD_NAME + "2/4: pattern = " + pattern + ", uri = " + uri);
    StringTokenizer tokenizer = new StringTokenizer(pattern, ", \t\n\r\f");
    boolean matches = false;
    while ( !matches && tokenizer.hasMoreTokens() )
      matches = tokenizer.nextToken().equals(uri);
    this.getLogger().debug(METHOD_NAME + "3/4: matches = " + matches);
    if ( matches )
      {
        Map sitemapParameters = new HashMap();
        sitemapParameters.put("matches", "yes");
        return sitemapParameters;
      }
    else
      return null;
  }
}