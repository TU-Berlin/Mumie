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
import java.util.HashMap;
import java.util.regex.Pattern;
import java.io.File;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.receipt.ReceiptHelper;
import net.mumie.cocoon.util.ParamUtil;

/**
 * <p>
 *   Provides path to Receipt for a given filename and ckecks if user is allowed to acces the receipt
 *   
 * </p>
 * <p>
 *   Sets the following sitemap parameters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>path</code></td>
 *       <td>absolut path of the file</td>
 *     </tr>
 *    </tbody>
 * </table>
 *
 * 
 * 
 */

public class CheckReceiptAction extends ServiceableAction
{
  public Map act(Redirector redirector, SourceResolver resolver,
      Map objectModel, String source, Parameters parameters)
      throws ProcessingException
  {
    // Some constants:
    final String METHOD_NAME = "act";
    final String FILENAME = "receipt-filename";
    
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    

    ReceiptHelper receiptHelper = null;
    User user = null;
    String filename = null;
    String filepath = null;
    try
    {

      user = (User)this.manager.lookup(SessionUser.ROLE);

      // Get user id 
      int userId = user.getId();
      
     //Init receiptHelper
      receiptHelper = (ReceiptHelper)this.manager.lookup(ReceiptHelper.ROLE);
      
      String receipDir = receiptHelper.getReceiptDir().getAbsolutePath();
      
      if ( ParamUtil.checkIfSet(parameters, FILENAME) )
      {
        filename = ParamUtil.getAsString(parameters, FILENAME);
        filepath = receipDir.toString() +File.separator+ filename;
      }
      else
        throw new IllegalArgumentException("Missing filename");
      
      String[] array = filepath.split( Pattern.quote( "__" ) ); 
      int receiptUserId = Integer.parseInt(array[1]);
      
      if(userId != receiptUserId)
        throw new IllegalArgumentException("UserId does not match");
      
      File receipt = new File(filepath);
      if(!receipt.exists())
        return null;

      
      Map sitemapParameters = new HashMap();
      sitemapParameters.put("receipt-path", filepath);
      
      this.getLogger().debug
      (METHOD_NAME + " 2/2: filename = "+ filepath); 
      
      return sitemapParameters;
    }

    catch (Exception exception)
    {
      throw new ProcessingException(exception);
    }
    finally
    {
      if ( receiptHelper != null ) this.manager.release(receiptHelper);
      if ( user != null ) this.manager.release(user);
    }    
       
  }
}
