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

import java.util.List;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.environment.Session;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.db.DbHelper;

/**
 * Represents a user
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Uwe Sinha <a href="mailto:sinha@math.tu-berlin.de">sinha@math.tu-berlin.de</a>
 * @version <code>$Id: SessionUser.java,v 1.6 2007/07/11 15:38:48 grudzin Exp $</code>
 */

public interface SessionUser extends User
{
  /**
   * Role of an implementing class as an Avalon service. Value is
   * <span class="string">"net.mumie.cocoon.pseudodocs.SessionUser"</span>.
   */

  public static final String ROLE = "net.mumie.cocoon.pseudodocs.SessionUser";

  /**
   * Returns the language of the user, as its id.
   */

  public int getLanguage ()
    throws ServiceException;

  /**
   * Returns the theme of the user, as its id.
   */

  public int getTheme ()
    throws ServiceException;

  /**
   * Returns the session attribute with the specified name, or <code>null</code> if the
   * attribute is not set.
   */

  public Object getSessionAttribute (String name)
    throws ServiceException;

  /**
   * Sets the session attribute with the specified name to the specified value.
   */

  public void setSessionAttribute (String name, Object value)
    throws ServiceException, IllegalStateException;
}
