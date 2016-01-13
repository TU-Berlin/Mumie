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

package net.mumie.cocoon.sync;

import org.apache.cocoon.environment.Request;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.RequestParam;

/**
 * Represents the synchronization command <code>change-user-data</code>.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ChangeUserDataSyncCommand.java,v 1.5 2007/07/11 15:38:50 grudzin Exp $</code>
 */

public class ChangeUserDataSyncCommand extends AbstractSyncCommand
{
  /**
   * Hint for this sync command.
   */

  public static final String HINT = "change-user-data";

  /**
   * Executes this sync command.
   */

  public void execute (Request request)
    throws SyncException
  {
    final String METHOD_NAME = "execute";
    this.getLogger().debug(METHOD_NAME + "1/3: Started");
    DbHelper dbHelper = null;
    try
      {
        // Initialize db helper:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get data from request:
        String syncId = request.getParameter(RequestParam.SYNC_ID);
        String loginName = request.getParameter(RequestParam.LOGIN_NAME);
        String passwordEncrypted = request.getParameter(RequestParam.PASSWORD_ENCRYPTED);
        String firstName = request.getParameter(RequestParam.FIRST_NAME);
        String surname = request.getParameter(RequestParam.SURNAME);
        String matrNumber = request.getParameter(RequestParam.MATR_NUMBER);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " syncId = " + syncId +
           ", loginName = " + loginName +
           ", passwordEncrypted : " + (passwordEncrypted == null ? "-null-" : "-not-null-") +
           ", firstName = " + firstName +
           ", surname = " + surname +
           ", matrNumber = " + matrNumber);

        // Update user data in db:
        dbHelper.updateUserData
          (syncId,
           loginName,
           passwordEncrypted,
           firstName,
           surname,
           matrNumber);

        this.getLogger().debug(METHOD_NAME + "3/3: Done");
      }
    catch (Exception exception)
      {
        throw new SyncException(exception);
      }
    finally
      {
        if ( dbHelper != null )
          this.serviceManager.release(dbHelper);
      }
  }

}
