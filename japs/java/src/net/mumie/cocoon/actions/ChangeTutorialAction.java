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


import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UserGroupName;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.PasswordEncryptor;
import net.mumie.cocoon.util.UserUtil;
import net.mumie.util.IntList;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;


public class ChangeTutorialAction extends ServiceableAction
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

    try
      {
        // Init sitemap parameters to return:
        Map sitemapParams = new HashMap();

        int formStage = ParamUtil.getAsInt(parameters, "form-stage", 0);
	sitemapParams.put("form-stage", formStage);
        switch ( formStage )
          {
          case 0:
            // Do nothing, only inform sitemap to generate form and set the user:
            int userId = ParamUtil.getAsInt(parameters, "id", -1);
            sitemapParams.put("mode", "form");
            sitemapParams.put("user", Integer.toString(userId));
            break;
          case 1:
            // Change tutorial:
            this.changeTutorial(parameters, sitemapParams);
            break;
          default:
            throw new IllegalArgumentException("Illegal form-stage value: " + formStage);
          }

	this.getLogger().debug(METHOD_NAME + " 2/2: Done");

        return sitemapParams;
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * 
   */

  protected void changeTutorial (Parameters params, Map sitemapParams)
    throws ProcessingException
  {
    final String METHOD_NAME = "changeTutorial";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    DbHelper dbHelper = null;
    try 
      {
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);

	// Get data from parameters:
	int userId = ParamUtil.getAsInt(params, "user");
	int classId = ParamUtil.getAsId(params, "class");
	int oldTutorialId = ParamUtil.getAsInt(params, "old-tutorial");
	int newTutorialId = ParamUtil.getAsInt(params, "new-tutorial");

	// Both tutorials must belong to the specified class:
	int oldTutorialClassId =
	  dbHelper.getPseudoDocDatumAsInt(PseudoDocType.TUTORIAL, oldTutorialId, DbColumn.CLASS);
	int newTutorialClassId =
	  dbHelper.getPseudoDocDatumAsInt(PseudoDocType.TUTORIAL, newTutorialId, DbColumn.CLASS);
	if ( oldTutorialClassId != classId )
	  throw new IllegalArgumentException
	    ("Old tutorial does not belong to the specified class");
	if ( newTutorialClassId != classId )
	  throw new IllegalArgumentException
	    ("New tutorial does not belong to the specified class");

	dbHelper.beginTransaction(this, true);

        // Remove user from old tutorial:
        dbHelper.removeTutorialMember(oldTutorialId, userId);

        // Add user to new tutorial:
        dbHelper.addTutorialMember(newTutorialId, userId);

	dbHelper.endTransaction(this);

	this.getLogger().debug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( dbHelper != null )
	  try
	    {
	      if ( dbHelper.hasTransactionLocked(this) )
		dbHelper.abortTransaction(this);
	    }
	  catch(Exception exception)
	    {
	      throw new ProcessingException(exception);
	    }
	  finally
	    {
	      this.manager.release(dbHelper);
	    }
      }
  }
}