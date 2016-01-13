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

package net.mumie.cocoon.util;

import java.sql.ResultSet;
import java.util.Arrays;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Lang;
import net.mumie.cocoon.notions.Theme;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import org.apache.avalon.excalibur.pool.Poolable;

/**
 * Default implementation of {@link GenericDocumentResolver GenericDocumentResolver}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultGenericDocumentResolver.java,v 1.6 2008/06/23 13:37:21 rassy Exp $</code>
 */

public class DefaultGenericDocumentResolver extends AbstractJapsServiceable
  implements GenericDocumentResolver, Poolable
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(DefaultGenericDocumentResolver.class);

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultGenericDocumentResolver</code> instance.
   */

  public DefaultGenericDocumentResolver ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  // --------------------------------------------------------------------------------
  // Resolving
  // --------------------------------------------------------------------------------

  /**
   * Returns the id of the real document corresponding to the generic document, theme, and
   * languge specified by the parameters.
   */

  public int resolve (int typeOfGeneric,
                      int idOfGeneric,
                      int languageId,
                      int themeId,
                      ResultSet resultSet)
    throws GenericDocumentResolveException
  {
    final String METHOD_NAME = "resolve";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " typeOfGeneric = " + typeOfGeneric +
       ", idOfGeneric = " + idOfGeneric +
       ", languageId = " + languageId +
       ", themeId = " + themeId);
    try
      {
        // Prepare sequence of real document ids:
        int[] idOfRealSequence = new int[7];
        Arrays.fill(idOfRealSequence, Id.UNDEFINED);

        // Init result set cursor:
        resultSet.beforeFirst();

        // Add real document ids to sequence according to priority:
        while ( resultSet.next() )
          {
            // Skip this row if it does not correspond to the specified generic document:
            if ( resultSet.getInt(DbColumn.GENERIC_DOCUMENT) != idOfGeneric )
              continue;

            int pos = -1;
            if ( resultSet.getInt(DbColumn.LANGUAGE) == languageId )
              {
                if ( resultSet.getInt(DbColumn.THEME) == themeId )
                  pos = 0;
                else if ( resultSet.getInt(DbColumn.THEME) == Theme.DEFAULT )
                  pos = 1;
              }
            else if ( resultSet.getInt(DbColumn.LANGUAGE) == Lang.NEUTRAL )
              {
                if ( resultSet.getInt(DbColumn.THEME) == themeId )
                  pos = 2;
                else if ( resultSet.getInt(DbColumn.THEME) == Theme.DEFAULT )
                  pos = 3;
              }
            else if ( resultSet.getInt(DbColumn.LANGUAGE) == Lang.DEFAULT )
              {
                if ( resultSet.getInt(DbColumn.THEME) == themeId )
                  pos = 4;
                else if ( resultSet.getInt(DbColumn.THEME) == Theme.DEFAULT )
                  pos = 5;
              }
            if ( pos != -1 )
              idOfRealSequence[pos] = resultSet.getInt(DbColumn.DOCUMENT);
          }

        // Find highest-priority real document id:
        int pos = 0;
        while ( pos < idOfRealSequence.length - 1 && idOfRealSequence[pos] == Id.UNDEFINED)
          pos++;
        int idOfReal = idOfRealSequence[pos];
        if ( idOfReal == Id.UNDEFINED )
          throw new GenericDocumentResolveException("No real document found");

        this.logDebug(METHOD_NAME + " 2/2: Done. idOfReal = " + idOfReal);
        return idOfReal;
      }
    catch (Exception exception)
      {
        throw new GenericDocumentResolveException("Wrapped exception", exception);
      }
  }

  /**
   * Returns the id of the real document corresponding to the generic document, theme, and
   * languge specified by the parameters.
   */

  public int resolve (int typeOfGeneric,
                      int idOfGeneric,
                      int languageId,
                      int themeId)
    throws GenericDocumentResolveException
  {
    DbHelper dbHelpder = null;
    try
      {
        // Init db helper:
        dbHelpder = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        // Get data from database:
        ResultSet resultSet =
          dbHelpder.queryGDIM(typeOfGeneric, idOfGeneric, themeId, languageId, true);

        return this.resolve(typeOfGeneric, idOfGeneric, languageId, themeId, resultSet);
      }
    catch (Exception exception)
      {
        throw new GenericDocumentResolveException("Wrapped exception", exception);
      }
    finally
      {
        if ( dbHelpder != null )
          this.serviceManager.release(dbHelpder);
      }
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultGenericDocumentResolver" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id and <code>lifecycleStatus</code> the
   * lifecycle status instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultGenericDocumentResolver" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ')';
  }
}