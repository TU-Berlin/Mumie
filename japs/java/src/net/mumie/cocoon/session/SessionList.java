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

package net.mumie.cocoon.session;

import javax.servlet.http.HttpSessionListener;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

/**
 * <p>
 *   List of active sessions. Meant to be registered as a listener in web.xml.
 * </p>
 * <p>
 *   This class holds an internal static list of sessions, and provides public static
 *   methods to add and remove sessions from it (see {@link #add add} and
 *   {@link #remove remove}). There is also a public static method to retrieve all sessions
 *   in the list as an array ({@link #getSessions getSessions}).
 * </p>
 * <p>
 *   In addition, this class implements {@link HttpSessionListener HttpSessionListener}. The
 *   {@link #sessionCreated sessionCreated} method adds the newly created session to the
 *   internal list; the {@link #sessionDestroyed sessionDestroyed} method removes the
 *   destroyed session from the internal list. Thus, if this class is registered as a
 *   listener in web.xml, the internal list will be automatically updated each time a
 *   session is created or destroyed, so that the list always contains the currently active
 *   sessions.
 * </p>
 * <p>
 *   Note that each instance of this class manipulates the same internal list. For this
 *   reason, {@link #add add}, {@link #remove remove}, and {@link #getSessions getSessions}
 *   are synchronized. However, there should be only one instance if this class is used
 *   through the listener statement in web.xml, and in no other way.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SessionList.java,v 1.6 2007/07/11 15:38:49 grudzin Exp $</code>
 */

public class SessionList
  implements HttpSessionListener
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The sessions of the list.
   */

  protected static List sessions = new ArrayList();

  // --------------------------------------------------------------------------------
  // Adding / removing sessions
  // --------------------------------------------------------------------------------

  /**
   * Adds the specified session to the list.
   */

  public static synchronized void add (HttpSession session)
  {
    if ( !sessions.contains(session) )
      sessions.add(session);
  }

  /**
   * Removes the specified session from the list.
   */

  public static synchronized void remove (HttpSession session)
  {
    sessions.remove(session);
  }

  // --------------------------------------------------------------------------------
  // Implementing HttpSessionListener
  // --------------------------------------------------------------------------------

  /**
   * Adds the session of the specified event to the list.
   */

  public void sessionCreated (HttpSessionEvent event)
  {
    add(event.getSession());
  }

  /**
   * Removes the session of the specified event from the list.
   */

  public void sessionDestroyed (HttpSessionEvent event)
  {
    remove(event.getSession());
  }

  // --------------------------------------------------------------------------------
  // Access to the sessions
  // --------------------------------------------------------------------------------

  /**
   * Returns an array of all sessions in the list.
   */

  public static synchronized HttpSession[] getSessions ()
  {
    return (HttpSession[])(sessions.toArray(new HttpSession[sessions.size()]));
  }

  /**
   * Returns the number of active sessions.
   */

  public static synchronized int getSize ()
  {
    return sessions.size();
  }
}
