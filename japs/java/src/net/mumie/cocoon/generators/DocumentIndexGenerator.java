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

import java.util.Map;
import net.mumie.cocoon.documents.DocumentIndex;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;

/**
 * <p>
 *   Generates an index of documents. Recognizes the following paramaters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td><td>Description</td><td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>type</code></td>
 *       <td>The type of the documents, as numerical code</td>
 *       <td rowspan="2" style="vertical-align:middle">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>type-name</code></td>
 *       <td>The type of the documents, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode</code></td>
 *       <td>The use mode of the documents, as numerical code</td>
 *       <td rowspan="2" style="vertical-align:middle">No. Default is
 *       {@link UseMode#COMPONENT COMPONENT}.</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode-name</code></td>
 *       <td>The use mode of the documents, as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>only-latest</code></td>
 *       <td>Whether only the latest versions of the documents should be included</td>
 *       <td>No. Default is <code>true</code></td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE 1: Only one of the parameters <code>type</code> and <code>type-name</code>
 *   should be set. If both are set, <code>type</code> takes precedence. 
 * </p>
 * <p>
 *   NOTE 2: Only one of the parameters <code>use-mode</code> and
 *   <code>use-mode-name</code> should be set. If both are set, <code>use-mode</code>
 *   takes precedence.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DocumentIndexGenerator.java,v 1.13 2007/07/11 15:38:44 grudzin Exp $</code>
 */

public class DocumentIndexGenerator extends ServiceableGenerator
{
  /**
   * The document index.
   */

  protected DocumentIndex index = null;

  /**
   * Releases {@link #index} if not <code>null</code>.
   */

  protected void releaseIndex ()
  {
    if ( this.index == null )
      {
        this.manager.release(this.index);
        this.index = null;
      }
  }

  /**
   * Recycles this generator for re-use. Calls the superclass recycle method and, if
   * {@link #index} is not <code>null</code>, releases {@link #index} and sets it to
   * <code>null</code>. 
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.releaseIndex();
    super.recycle();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this generator. Calls the superclass dispose method and, if
   * {@link #index} is not <code>null</code>, releases {@link #index} and sets it to
   * <code>null</code>. 
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.releaseIndex();
    super.dispose();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Calls the superclass <code>setup</code> method and processes the parameters.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "setup";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    try
      {
        super.setup(resolver, objectModel, source, parameters);

        // Parameter names:
        final String TYPE = "type";
        final String TYPE_NAME = "type-name";
        final String USE_MODE = "use-mode";
        final String USE_MODE_NAME = "use-mode-name";
        final String ONLY_LATEST = "only-latest";

	// document type:
	int type = DocType.UNDEFINED;
	if ( ParamUtil.checkIfSet(this.parameters, TYPE) )
	  {
	    type = this.parameters.getParameterAsInteger(TYPE);
	    if ( !DocType.exists(type) )
	      throw new ProcessingException("Unknown document type code: " + type);
	    if ( ParamUtil.checkIfSet(this.parameters, TYPE_NAME) )
	      this.getLogger().warn(METHOD_NAME + ": Superfluous parameter \"" +
                                    TYPE_NAME + "\". Ignored");
	  }
	else if ( ParamUtil.checkIfSet(this.parameters, TYPE_NAME) )
	  {
	    String typeName = this.parameters.getParameter(TYPE_NAME);
	    type = DocType.codeFor(typeName);
	    if ( type == DocType.UNDEFINED )
	      throw new ProcessingException("Unknown document type: " + typeName);
	  }

	// Use mode:
 	int useMode;
	if ( ParamUtil.checkIfSet(this.parameters, USE_MODE) )
	  {
	    useMode = this.parameters.getParameterAsInteger(USE_MODE);
	    if ( !UseMode.exists(useMode) )
	      throw new ProcessingException("Unknown use mode code: " + useMode);
	  }
	else if ( ParamUtil.checkIfSet(this.parameters, USE_MODE_NAME) )
	  {
	    String useModeName = this.parameters.getParameter(USE_MODE_NAME);
	    useMode = UseMode.codeFor(useModeName);
	    if ( useMode == UseMode.UNDEFINED )
	      throw new ProcessingException("Unknown use mode: " + useModeName);
	  }
	else
	  useMode = UseMode.COMPONENT;

        // Only latest:
        boolean onlyLatest =
          (ParamUtil.checkIfSet(this.parameters, ONLY_LATEST)
           ? this.parameters.getParameterAsBoolean(ONLY_LATEST)
           : true);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " type = " + type +
           ", useMode = " + useMode +
           ", onlyLatest = " + onlyLatest);

        // Preparing index:
        this.index =
          (DocumentIndex)this.manager.lookup(DocumentIndex.ROLE);
        this.index.setType(type);
        this.index.setUseMode(useMode);
        this.index.setOnlyLatest(onlyLatest);

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Sends the index as SAX envents to the content handler.
   */

  public void generate ()
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "generate";
        this.getLogger().debug(METHOD_NAME + " 1/2: Started");
        this.index.toSAX(this.contentHandler);
        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }
}
