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

package net.mumie.cocoon.transformers;

import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.sitemap.ParamBlockParseException;
import net.mumie.cocoon.sitemap.ParamBlockParser;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.transformation.ServiceableTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Resolves sitemap autocoding attributes
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SitemapTransformer.java,v 1.11 2009/05/29 13:35:55 rassy Exp $</code>
 */

public class SitemapTransformer extends ServiceableTransformer
  implements Configurable
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The URL prefix of the Japs URLs.
   */

  protected String urlPrefix;

  /**
   * The db helper of this instance.
   */

  protected DbHelper dbHelper = null;

  /**
   * The parameter block parser of this instance
   */

  protected ParamBlockParser paramBlockParser = new ParamBlockParser();

  /**
   * Pattern to find the <code>"%{prefix}"</code> placeholder.
   */

  protected Pattern urlPrefixPattern = Pattern.compile("%\\{prefix\\}");

  /**
   * Pattern to find the <code>"%{id}"</code> placeholder.
   */

  protected Pattern idPattern = Pattern.compile("%\\{id\\}");

  // --------------------------------------------------------------------------------
  // Lifecycle methods
  // --------------------------------------------------------------------------------

  /**
   * Configures this instance. Sets the URL prefix ({@link #urlPrefix urlPrefix}).
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.getLogger().debug(METHOD_NAME + ": 1/2: Started");

    String urlPrefix = configuration.getChild("url-prefix").getAttribute("value").trim();

    // Remove trailing slash if any:
    int len = urlPrefix.length();
    if ( urlPrefix.charAt(len-1) == '/' )
      urlPrefix = urlPrefix.substring(0, len-1);

    this.urlPrefix = urlPrefix;

    this.getLogger().debug
      (METHOD_NAME + " 2/2: Done. urlPrefix = " + this.urlPrefix);
  }

  /**
   * Releases the global services. Currently, there is only one service: the db helper.
   */

  protected void releaseServices ()
  {
    if ( this.dbHelper == null )
      this.manager.release(this.dbHelper);
  }

  /**
   * Recycles this transformer.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.releaseServices();
    super.recycle();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this transformer.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.releaseServices();
    super.dispose();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // "Ensure" methods for the services
  // --------------------------------------------------------------------------------

  /**
   * Ensures that the db helper is ready to use.
   */

  protected void ensureDbHelper ()
    throws ServiceException 
  {
    if ( this.dbHelper == null )
      this.dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
  }

  // --------------------------------------------------------------------------------
  // Evaluation of autocoding functions
  // --------------------------------------------------------------------------------

  /**
   * Evaluates the specified function expression.
   */

  protected String evaluateFunction (String expression)
    throws SitemapTransformerException, ParamBlockParseException
  {
    final String METHOD_NAME = "evaluateFunction";
    this.getLogger().debug
      (METHOD_NAME + ": 1/3: Started. expression = " + expression);

    // Get function name:
    int paramsStart = expression.indexOf('(');
    if ( paramsStart == -1 )
      throw new SitemapTransformerException
        ("Illegal expression: " + expression + ": missing parameter block");
    String name = expression.substring(0, paramsStart).trim();
    if ( name.equals("") )
      throw new SitemapTransformerException
        ("Illegal expression: " + expression + ": missing function name");

    // Get paramter block:
    int paramsEnd = expression.lastIndexOf(')');
    if ( paramsEnd == -1 )
      throw new SitemapTransformerException
        ("Illegal expression: " + expression + ": unclosed parameter block");
    String paramBlock = expression.substring(paramsStart+1, paramsEnd);

    // Get parameters:
    String[] params = this.paramBlockParser.parse(paramBlock);
    this.getLogger().debug
      (METHOD_NAME + ": 2/3: params = " + LogUtil.arrayToString(params));

    // Call function:
    String result = this.evaluateFunction(name, params);
    this.getLogger().debug
      (METHOD_NAME + ": 3/3: Done. result = " + result);

    return result;
  }

  /**
   * Evaluates the function with the spacified name and parameters.
   */

  protected String evaluateFunction (String name, String[] params)
    throws SitemapTransformerException
  {
    if ( name.equals("resolve-path") )
      return this.resolvePath(params);
    else if ( name.equals("ugr-names-to-ids") )
      return this.ugrNamesToIds(params);
    else if ( name.equals("add-url-prefix") )
      return this.addURLPrefix(params);
    else if ( name.equals("use-mode-name-to-code") )
      return this.useModeNameToCode(params);
    else if ( name.equals("doctype-name-to-code") )
      return this.docTypeNameToCode(params);
    else if ( name.equals("doc-paths-to-ids") )
      return this.docPathsToIds(params);
    else if ( name.equals("psdoc-paths-to-ids") )
      return this.psdocPathsToIds(params);
    else
      throw new SitemapTransformerException("Unknown function: " + name);
  }
                                     

  // --------------------------------------------------------------------------------
  // Implementation of the autocoding functions
  // --------------------------------------------------------------------------------

  /**
   * Autocoding function. Resolves a checkin path.
   */

  protected String resolvePath (String[] params)
    throws SitemapTransformerException
  {
    try
      {
        final String METHOD_NAME = "resolvePath";
        this.getLogger().debug
          (METHOD_NAME + ": 1/2: Started. params = " + LogUtil.arrayToString(params));

        // Process parameters:
        this.checkNumberOfParams(METHOD_NAME, 3, 4, params);
        String url = params[0];
        String path = params[1];
        int docType = DocType.codeFor(params[2]);
	String requestParams = (params.length > 3 ? params[3] : null);

        // Get id:
        this.ensureDbHelper();
        int id = this.dbHelper.getIdForPath(docType, path);

        // Resolve the %{prefix}" placeholder:
        Matcher urlPrefixMatcher = this.urlPrefixPattern.matcher(url);
        if ( urlPrefixMatcher.find() )
	  url = urlPrefixMatcher.replaceAll(this.urlPrefix);

        // Resolve the %{id} placeholder:
        Matcher idMatcher = this.idPattern.matcher(url);
        if ( idMatcher.find() )
	  url = idMatcher.replaceAll(Integer.toString(id));

	// Add request parameters if necessary:
	if ( requestParams != null )
	  url = url + requestParams;

        this.getLogger().debug
          (METHOD_NAME + ": 2/2: Done. url = " + url);

        return url;
      }
    catch (Exception exception)
      {
        throw new SitemapTransformerException(exception);
      }
  }

  /**
   * Autocoding function. Resolves a list of user group names.
   */

  protected String ugrNamesToIds (String[] params)
    throws SitemapTransformerException
  {
    try
      {
        final String METHOD_NAME = "ugrNamesToIds";
        this.getLogger().debug
          (METHOD_NAME + ": 1/2: Started. params = " + LogUtil.arrayToString(params));

        StringBuffer buffer = null;
        for (int i = 0; i < params.length; i++)
          {
            String name = params[i];
            if ( buffer == null )
              buffer = new StringBuffer();
            else
              buffer.append(",");
            this.ensureDbHelper();
            ResultSet resultSet =
              this.dbHelper.queryUserGroupData(name, new String[] { DbColumn.ID });
            if ( !resultSet.next() )
              throw new SitemapTransformerException
                ("Can not resolve user group name: " + name);
            String id = resultSet.getString(DbColumn.ID);
            buffer.append(id);
          }

        String ids = buffer.toString();

        this.getLogger().debug
          (METHOD_NAME + ": 2/2: Done. ids = " + ids);

        return ids;
      }
    catch (Exception exception)
      {
        throw new SitemapTransformerException(exception);
      }
  }

  /**
   * Autocoding function. Resolves a list of document paths
   */

  protected String docPathsToIds (String[] params)
    throws SitemapTransformerException
  {
    try
      {
        final String METHOD_NAME = "docPathsToIds";
        this.getLogger().debug
          (METHOD_NAME + ": 1/2: Started. params = " + LogUtil.arrayToString(params));

        int type = PseudoDocType.codeFor(params[0]);

        StringBuffer buffer = null;
        for (int i = 1; i < params.length; i++)
          {
            String path = params[i];
            if ( buffer == null )
              buffer = new StringBuffer();
            else
              buffer.append(",");
            this.ensureDbHelper();
            int id = dbHelper.getIdForPath(type, path);
            buffer.append(id);
          }

        String ids = buffer.toString();

        this.getLogger().debug
          (METHOD_NAME + ": 2/2: Done. ids = " + ids);

        return ids;
      }
    catch (Exception exception)
      {
        throw new SitemapTransformerException(exception);
      }
  }

  /**
   * Autocoding function. Resolves a list of pseudo document paths
   */

  protected String psdocPathsToIds (String[] params)
    throws SitemapTransformerException
  {
    try
      {
        final String METHOD_NAME = "psdocPathsToIds";
        this.getLogger().debug
          (METHOD_NAME + ": 1/2: Started. params = " + LogUtil.arrayToString(params));

        int type = PseudoDocType.codeFor(params[0]);

        StringBuffer buffer = null;
        for (int i = 1; i < params.length; i++)
          {
            String path = params[i];
            if ( buffer == null )
              buffer = new StringBuffer();
            else
              buffer.append(",");
            this.ensureDbHelper();
            int id = dbHelper.getPseudoDocIdForPath(type, path);
            buffer.append(id);
          }

        String ids = buffer.toString();

        this.getLogger().debug
          (METHOD_NAME + ": 2/2: Done. ids = " + ids);

        return ids;
      }
    catch (Exception exception)
      {
        throw new SitemapTransformerException(exception);
      }
  }

  /**
   * Autocoding function. Adds the URL prefix at the beginning of a relative URL.
   */

  protected String addURLPrefix (String[] params)
    throws SitemapTransformerException
  {
    try
      {
        final String METHOD_NAME = "addURLPrefix";
        this.getLogger().debug
          (METHOD_NAME + ": 1/2: Started. params = " + LogUtil.arrayToString(params));
        this.checkNumberOfParams(METHOD_NAME, 1, params);

        String relURL = params[0].trim();
        return
          this.urlPrefix + "/" +
          (relURL.startsWith("/") ? relURL.substring(1) : relURL);
      }
    catch (Exception exception)
      {
        throw new SitemapTransformerException(exception);
      }
  }

  /**
   * Autocoding function
   */

  protected String useModeNameToCode (String[] params)
    throws SitemapTransformerException
  {
    try
      {
        final String METHOD_NAME = "useModeNameToCode";
        this.getLogger().debug
          (METHOD_NAME + ": 1/2: Started. params = " + LogUtil.arrayToString(params));
        this.checkNumberOfParams(METHOD_NAME, 1, params);
        String name = params[0].trim();
        int code = UseMode.codeFor(name);
        if ( code == UseMode.UNDEFINED )
          throw new IllegalArgumentException("Unknown use mode: " + name);
        return Integer.toString(code);
      }
    catch (Exception exception)
      {
        throw new SitemapTransformerException(exception);
      }
  }

  /**
   * Autocoding function
   */

  protected String docTypeNameToCode (String[] params)
    throws SitemapTransformerException
  {
    try
      {
        final String METHOD_NAME = "docTypeNameToCode";
        this.getLogger().debug
          (METHOD_NAME + ": 1/2: Started. params = " + LogUtil.arrayToString(params));
        this.checkNumberOfParams(METHOD_NAME, 1, params);
        String name = params[0].trim();
        int code = DocType.codeFor(name);
        if ( code == DocType.UNDEFINED )
          throw new IllegalArgumentException("Unknown document type: " + name);
        return Integer.toString(code);
      }
    catch (Exception exception)
      {
        throw new SitemapTransformerException(exception);
      }
  }

  /**
   * Auxiliary method to check if an autocoding function is called with the correct number
   * of parameters.
   */

  protected void checkNumberOfParams (String methodName,
                                      int expcetedNumber,
                                      String[] params)
    throws SitemapTransformerException
  {
    if ( params.length != expcetedNumber )
      throw new SitemapTransformerException
        (methodName + ": Illegal number of parameters: " +
         "Expected " + expcetedNumber + ", found " + params.length + ": " +
         LogUtil.arrayToString(params));
  }

  /**
   * Auxiliary method to check if an autocoding function is called with the correct number
   * of parameters.
   */

  protected void checkNumberOfParams (String methodName,
                                      int minExpcetedNumber,
                                      int maxExpcetedNumber,
                                      String[] params)
    throws SitemapTransformerException
  {
    if ( params.length < minExpcetedNumber || params.length > maxExpcetedNumber)
      throw new SitemapTransformerException
        (methodName + ": Illegal number of parameters: " +
         "Expected " + minExpcetedNumber + ".." + maxExpcetedNumber +
	 ", found " + params.length + ": " +
         LogUtil.arrayToString(params));
  }

  // --------------------------------------------------------------------------------
  // SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Handles an element start.
   */

  public void startElement (String namespaceURI,
                            String localName,
                            String qualifiedName,
                            Attributes attributes)
    throws SAXException
  {
    try
      {
        AttributesImpl newAttributes = new AttributesImpl();
        for (int i = 0; i < attributes.getLength(); i++)
          {
            String attribNamespaceURI = attributes.getURI(i);
            String attribLocalName = attributes.getLocalName(i);
            String attribQualifiedName = attributes.getQName(i);
            String attribType = attributes.getType(i);
            String attribValue = attributes.getValue(i);

            if ( attribNamespaceURI.equals(XMLNamespace.URI_SITEMAP_AUTOCODING) )
              {
                attribNamespaceURI = XMLNamespace.URI_SITEMAP;
                attribQualifiedName = attribLocalName;
                attribValue = this.evaluateFunction(attribValue);
              }

            newAttributes.addAttribute
              (attribNamespaceURI,
               attribLocalName,
               attribQualifiedName,
               attribType,
               attribValue);
          }

        this.contentHandler.startElement
          (namespaceURI, localName, qualifiedName, newAttributes);
      }
    catch (Exception exception)
      {
        throw new SAXException("Wrapped exception", exception);
      }
  }  
}