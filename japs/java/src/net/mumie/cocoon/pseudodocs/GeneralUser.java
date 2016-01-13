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
import org.apache.cocoon.ProcessingException;

/**
 * Represents a user
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Uwe Sinha <a href="mailto:sinha@math.tu-berlin.de">sinha@math.tu-berlin.de</a>
 * @version <code>$Id: GeneralUser.java,v 1.6 2007/07/11 15:38:48 grudzin Exp $</code>
 */

public interface GeneralUser extends User
{
  /**
   * Role of an implementing class as an Avalon service. Value is
   * <span class="string">"net.mumie.cocoon.pseudodocs.GeneralUser"</span>.
   */

  public static final String ROLE = "net.mumie.cocoon.pseudodocs.GeneralUser";

  /**
   * Sets a piece of data in the data buffer.
   */

  public void setDatum (String key, String value)
    throws ServiceException;

  /**
   * Returns a piece of data from the buffer.
   */

  public Object getDatum (String key)
    throws ServiceException;

  /**
   * <p>
   *   Updates this user in the database.
   * </p>
   */

  public void update ()
    throws ProcessingException;

  /**
   * <p>
   *   Creates this user in the database.
   * </p>
   */

  public void create ()
    throws ProcessingException;
}
