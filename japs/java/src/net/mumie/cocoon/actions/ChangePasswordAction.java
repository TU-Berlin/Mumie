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
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.ProcessingException;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.DbColumn;
import java.util.HashMap;

/**
 * @author Marek Grudzinski <a href="mailto:grudzin@math.tu-berlin.de">grudzin@math.tu-berlin.de</a>
 * @version <code>$Id: ChangePasswordAction.java,v 1.6 2008/03/06 12:44:08 rassy Exp $</code>
 */

public class ChangePasswordAction extends ServiceableAction
{
  /**
   * See class description.
   */

  public Map act (Redirector redirector,
                  SourceResolver resolver,
                  Map objectModel,
                  String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    DbHelper dbHelper = null;
    User user = null;
    PasswordEncryptor encryptor = null;

    try
      {  
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        user = (User)this.manager.lookup(SessionUser.ROLE);
        encryptor = (PasswordEncryptor)this.manager.lookup(PasswordEncryptor.ROLE);

        // User id:
        int userId = user.getId();

        // Get input data from parameters:
        String oldPassword = ParamUtil.getAsString(parameters, "old-password");
        String newPassword = ParamUtil.getAsString(parameters, "new-password");
        String newPasswordRepeated = ParamUtil.getAsString(parameters, "new-password-repeated");

        String status = null;

        // Get current (encrypted) password from db:
        String passwordEncr = dbHelper.getPseudoDocDatumAsString
          (PseudoDocType.USER, userId, DbColumn.PASSWORD);

        // Check if current password is null:
        if ( passwordEncr == null )
          status = "user-has-no-password";
        // Check if specified password is not correct:
        else if ( !encryptor.encrypt(oldPassword).equals(passwordEncr) )
          status = "wrong-password";
        // Check if new password and retyped new password are different:
        else if ( !newPassword.equals(newPasswordRepeated) )
          status = "new-passwords-differ";
        // Check id new password is too short:
        else if ( newPassword.length() < 6 )
          status = "new-password-too-short";
        // Set new password:
        else
          {
            dbHelper.updatePseudoDocDatum
              (PseudoDocType.USER, userId, DbColumn.PASSWORD, encryptor.encrypt(newPassword));
            status = "password-changed";
          }

        Map result = new HashMap();
        result.put("status", status);

        this.getLogger().debug
          (METHOD_NAME + " 2/2: Done." +
           " userId = " + userId + ", status = " + status);

        return result;
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( encryptor != null )
          this.manager.release(encryptor);

        if ( user != null )
          this.manager.release(user);

        if ( dbHelper != null )
          this.manager.release(dbHelper);
      }
  }
}
