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
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import net.mumie.cocoon.pseudodocs.GeneralUser;
import net.mumie.cocoon.util.ParamUtil;

/**
 * <p>
 *   Generates the SAX events for a user. Recognizes the following parameters:
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
 *       <td><code>id</code></td>
 *       <td>The id of the user</td>
 *       <td>No. Default is the id of the current user</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode</code></td>
 *       <td>The use mode (numerical code).</td>
 *       <td rowspan="2" style="vertical-align:middle">None or one of the both. Default is
 *           {@link UseMode#SERVE UseMode.SERVE}.</td>
 *     </tr>
 *     <tr>
 *       <td><code>use-mode-name</code></td>
 *       <td>The use mode (string name).</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   NOTE: Only one of the parameters <code>use-mode</code> and <code>use-mode-name</code> should be
 *   set. If both are set, <code>use-mode</code> takes precedence.
 * </p>
 * <p>
 *   If the id is not specified or {@link Id#UNDEFINED UNDEFINED}, the user defaults to the
 *   current user, i.e., the user owning the session.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UserGenerator.java,v 1.8 2007/07/11 15:38:45 grudzin Exp $</code>
 */

public class UserGenerator extends ServiceableGenerator
{
  /**
   * The user to send to SAX.
   */

  protected User user = null;

  /**
   * Calls the superclass <code>setup</code> method and processes the parameters and
   * initializes the {@link #user} object.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    try
      {
        final String METHOD_NAME = "setup";
        this.getLogger().debug(METHOD_NAME + " 1/3: Started");
        super.setup(resolver, objectModel, source, parameters);

        // Parameter names:
        final String ID = "id";
        final String USE_MODE = "use-mode";
        final String USE_MODE_NAME = "use-mode-name";

        // Id:
        int id = parameters.getParameterAsInteger(ID, Id.UNDEFINED);

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
	  useMode = UseMode.SERVE;

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" + 
           " id = " + id +
           ", useMode = " + useMode);

        // Init the user object:
        if ( id == Id.UNDEFINED )
          user = (User)this.manager.lookup(SessionUser.ROLE);
        else
          {
            user = (User)this.manager.lookup(GeneralUser.ROLE);
            user.setId(id);
          }
        user.setUseMode(useMode);

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Generates the XML. See class documentation for details.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.getLogger().debug(METHOD_NAME + " 1/2: started");
    try
      {
	this.user.toSAX(this.contentHandler, true);
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    this.getLogger().debug(METHOD_NAME + " 2/2: finished");
  }

  /**
   * Releases {@link #user} if not <code>null</code>.
   */

  protected void releaseUser ()
  {
    if ( this.user != null )
      {
        this.manager.release(this.user);
        this.user = null;
      }
  }

  /**
   * Recycles this <code>UserGenerator</code>.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    try
      {
        this.getLogger().debug(METHOD_NAME + " 1/2");
        this.releaseUser();
        super.recycle();
        this.getLogger().debug(METHOD_NAME + " 2/2");
      }
    catch (Exception exception)
      {
	this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
  }

  /**
   * Disposes this <code>UserGenerator</code>.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    try
      {
        this.getLogger().debug(METHOD_NAME + " 1/2");
        this.releaseUser();
        super.dispose();
        this.getLogger().debug(METHOD_NAME + " 2/2");
      }
    catch (Exception exception)
      {
	this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
  }
}
