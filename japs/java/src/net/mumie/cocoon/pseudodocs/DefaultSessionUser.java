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

package net.mumie.cocoon.pseudodocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.SessionAttrib;
import net.mumie.cocoon.notions.UserRole;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.webapps.session.SessionManager;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Uwe Sinha <a href="mailto:sinha@math.tu-berlin.de">sinha@math.tu-berlin.de</a>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultSessionUser.java,v 1.22 2007/07/11 15:38:47 grudzin Exp $</code>
 */

public class DefaultSessionUser extends AbstractUser
  implements SessionUser
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultSessionUser.class);

  /**
   * The session context from authentication. Value is
   * <span class="string">"authentication"</span>. 
   */

  public static final String SESSION_CONTEXT = "authentication";

  /**
   * The path in the session context pointing to the element or attribut that stores the
   * id. Value is <span class="string">"authentication/ID"</span>.
   */

  public static final String SESSION_ID_PATH = "authentication/ID";

  /**
   * The path in the session context pointing to the element or attribut that stores the
   * theme. Value is <span class="string">"authentication/data/theme"</span>.
   */

  public static final String SESSION_THEME_PATH = "authentication/data/theme";

  /**
   * The fallback theme, as an id.
   */

  public static final String FALLBACK_THEME = "0";

  /**
   * The session manager of this user.
   */

  protected SessionManager sessionManager = null;

  /**
   * <p>
   *   Makes this instance ready to represent another user.
   * </p>
   */

  public void reset()
  {
    this.id = Id.DEFINED_ELSEWHERE;
    super.reset();
  }

  /**
   * <p>
   *   Releases all resources hold by this object.
   * </p>
   */

  protected void releaseResources ()
  {
    if ( this.sessionManager != null )
      {
        this.serviceManager.release(this.sessionManager);
        this.sessionManager = null;
      }
    super.releaseResources();
  }

  /**
   * <p>
   *   Recycles this instance.
   * </p>
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.logDebug(METHOD_NAME + "1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * <p>
   *   Disposes this instance.
   * </p>
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.logDebug(METHOD_NAME + "1/2: Started");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + "2/2: Done");
  }

  /**
   * <p>
   *   Ensures that {@link #sessionManager} is initialized. 
   * </p>
   * <p>
   *   If {@link #sessionManager} is <code>null</code>, gets a session manager from the
   *   service manager ({@link #serviceManager}) and sets {@link #sessionManager} to it. 
   * </p>
   * <p>
   *   If {@link #sessionManager} is not <code>null</code>, does nothing.
   * </p>
   */

  public void ensureSessionManager ()
    throws ServiceException
  {
    if ( this.sessionManager != null )
      return;
    final String METHOD_NAME = "ensureSessionManager()";
    this.logDebug(METHOD_NAME + " 1/2: Need a SessionManager");
    this.sessionManager = (SessionManager)this.serviceManager.lookup(SessionManager.ROLE);
    this.logDebug(METHOD_NAME + " 2/2: this.sessionManager = " + this.sessionManager);
  }

  /**
   * Returns the session attribute with the specified name, or <code>null</code> if the
   * attribute is not set.
   */

  public Object getSessionAttribute (String name)
    throws ServiceException
  {
    this.ensureSessionManager(); 
    Session session = this.sessionManager.getSession(false);
    return session.getAttribute(name);
  }

  /**
   * Sets the session attribute with the specified name to the specified value.
   */

  public void setSessionAttribute (String name, Object value)
    throws ServiceException, IllegalStateException 
  {
    this.ensureSessionManager();
    Session session = this.sessionManager.getSession(false);
    if ( session == null )
      throw new IllegalStateException("Session null");
    session.setAttribute(name, value);
  }

  /**
   * Returns the session datum specified by <code>context</code> and <code>path</code>. If
   * the corresponding context fragment can not be found, returns <code>fallback</code>;
   */

  protected String getSessionDatum (String context, String path, String fallback)
    throws ServiceException
  {
    final String METHOD_NAME = "getSessionDatum";
    this.logDebug
      (METHOD_NAME + " 1/2:" +
       " context = " + context +
       ", path = " + path +
       ", fallback = " + fallback);
    try
      {
	this.ensureSessionManager(); 
	DocumentFragment fragment = this.sessionManager.getContextFragment(context, path);
	String datum;
	if ( fragment != null )
	  {
	    this.logDebug(METHOD_NAME + " 2/3: Retrieving datum from session");
	    NodeList childs = fragment.getChildNodes();
	    if ( childs.getLength() != 1 )
	      throw new ServiceException
		(LogUtil.identify(this) + ": Invalid number of childs in context fragment: "
                 + context + ", " + path);
	    Node child = childs.item(0);
	    if ( child.getNodeType() != Node.TEXT_NODE )
	      throw new ServiceException
                (LogUtil.identify(this) + ": Child is not TEXT in context fragment: "
                 + context + ", " + path);
	    datum = ((Text)child).getData();
	  }
	else
	  {
	    this.logDebug
              (METHOD_NAME + "2/3: Datum not found in session. Returning fallback");
	    datum = fallback;
	  }
	this.logDebug(METHOD_NAME + " 3/3: datum = " + datum);
	return datum;
      }
    catch (Exception exception)
      {
	throw new ServiceException
          (LogUtil.identify(this) + ": Wrapped exception", exception);
      }
  }

  /**
   * Returns the id of the user.
   */

  public int getId ()
  {
    final String METHOD_NAME = "getId";
    int id = Id.UNDEFINED;
    try
      {
        id = ((Integer)this.getSessionAttribute(SessionAttrib.USER)).intValue();
        // String value = this.getSessionDatum(SESSION_CONTEXT, SESSION_ID_PATH, null);
        // if ( value == null )
        // throw new ServiceException("Failed to retrieve id from session context");
        // id = Integer.parseInt(value);
      }
    catch (Exception exception)
      {
        this.getLogger().warn
          (METHOD_NAME + ": Caught exception: " + exception.toString());
      }
    return id;
  }

  /**
   * Should not be called, since the id is set by the session.
   * @throws ServiceException if called.
   */

  public void setId (int id)
    throws ServiceException
  {
    throw new ServiceException
      (LogUtil.identify(this) + ": Can not set id for session user:" +
       " Id is determined by session");
  }

  /**
   * Returns the language of the user.
   */

  public int getLanguage ()
    throws ServiceException
  {
    try
      {
        return ((Integer)this.getSessionAttribute(SessionAttrib.LANGUAGE)).intValue();
        // return Integer.parseInt
        //   (this.getSessionDatum(SESSION_CONTEXT, SESSION_LANGUAGE_PATH, FALLBACK_LANGUAGE));
      }
    catch (Exception exception)
      {
	throw new ServiceException
          (LogUtil.identify(this) + ": Wrapped exception", exception);
      }
  }

  /**
   * Returns the theme of the user.
   */

  public int getTheme ()
    throws ServiceException
  {
    try
      {
        return ((Integer)this.getSessionAttribute(SessionAttrib.THEME)).intValue();
        // return Integer.parseInt
        //   (this.getSessionDatum(SESSION_CONTEXT, SESSION_THEME_PATH, FALLBACK_THEME));
      }
    catch (Exception exception)
      {
	throw new ServiceException
          (LogUtil.identify(this) + ": Wrapped exception", exception);
      }
  }

  /**
   * Returns a string that identificates this instance. It has the
   * following form:<pre>
   *   "DefaultSessionUser" +
   *   '#' + instanceId
   *   '(' + numberOfRecycles
   *   ',' + {@link #id id}
   *   ',' + {@link #useMode useMode}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and
   * <code>numberOfRecycles</code> the number of recycles of this instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultSessionUser" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.id +
      ',' + this.useMode +
      ')';
  }

  /**
   * Default constructor. Creates an <code>DefaultSessionUser</code> instance, calls the
   * superclass constructor, sets {@link #id id} to
   * {@link Id#DEFINED_ELSEWHERE DEFINED_ELSEWHERE}, increases the instance counter and sets
   * the instance id.
   */

  public DefaultSessionUser ()
  {
    super();
    this.id = Id.DEFINED_ELSEWHERE;
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }
}
