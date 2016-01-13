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

package net.mumie.cocoon.grade;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.util.GenericDocumentResolver;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.UserProblemData;
import net.mumie.cocoon.xml.GeneralXMLElement;
import org.apache.avalon.excalibur.pool.Poolable;
import org.xml.sax.ContentHandler;

/**
 * Abstract base class for bulk correctors.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractBulkCorrector.java,v 1.9 2008/03/05 11:06:33 grudzin Exp $</code>
 */

public abstract class AbstractBulkCorrector extends AbstractJapsServiceable
  implements Poolable
{
  /**
   * Gets the needed data from the database. This method controls which corrections are
   * made. It should return a table with (at least) the following columns:
   * <ol>
   *   <li>{@link DbColumn.PROBLEM_ID PROBLEM_ID} &ndash; the id of the generic problem </li>
   *   <li>{@link DbColumn.PROBLEM_REF_ID PROBLEM_REF_ID} &ndash; the id of the reference
   *     from the worksheet to the generic problem</li>
   *   <li>{@link DbColumn.USER_ID USER_ID} &ndash; the id of the user</li>
   * </ol>
   * Each row defines a problem user pair. For each of these pairs, the correction is
   * created.
   */

  protected abstract ResultSet queryInputData (DbHelper dbHelper)
    throws SQLException;

  /**
   * <p>
   *   Adds attributes to the specified XML element. This method is called in
   *   {@link #doCorrection doCorrection} with the root element of the correction protocol
   *   as argument. The method gives extending classes an opportunity to set attributes of
   *   the root element.
   * </p>
   * <p>
   *   The implementation in this class does nothing.
   * </p>
   */

  protected void addRootElementAttribs (GeneralXMLElement xmlElement)
  {
    // Does nothing.
  }

  /**
   * <p>
   *   Carries out the bulk correction for the problem user pairs as returned by
   *   {@link #queryInputData queryInputData}. If <code>force</code> is true, problems are
   *   corrected even if the correction exists and is up-to-date. A protocol of the correction
   *   is written in XML form to the specified content handler. If <code>ownDocument</code> is
   *   false, the content handlers <code>startDocument</code> and <code>endDocument</code>
   *   methods are not called before resp. after the XML is written.
   * </p>
   */

  protected void doCorrection (boolean force,
                               ContentHandler contentHandler,
                               boolean ownDocument)
    throws BulkCorrectionException
  {
    final String METHOD_NAME = "bulkCorrect";
    final String[] USER_COLUMNS = {DbColumn.LANGUAGE, DbColumn.THEME};

    DbHelper dbHelper = null;
    GenericDocumentResolver gendocResolver = null;
    UserProblemData userProblemData = null;
    GeneralXMLElement xmlElement = new GeneralXMLElement(XMLNamespace.URI_CORRECTION_REPORT,
                                                         XMLNamespace.PREFIX_CORRECTION_REPORT);

    try
      {
        // Init services:
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
        gendocResolver =
          (GenericDocumentResolver)this.serviceManager.lookup(GenericDocumentResolver.ROLE);
        userProblemData = (UserProblemData)this.serviceManager.lookup(UserProblemData.ROLE);

        // Get the result set containing the input data:
        ResultSet resultSet = this.queryInputData(dbHelper);

        // Start document if necessary:
        if ( ownDocument )
          contentHandler.startDocument();

        // Start root element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.CORRECTION_REPORTS);
        this.addRootElementAttribs(xmlElement);
        xmlElement.startToSAX(contentHandler);

        while ( resultSet.next() )
          {
            // Get problem, ref and user ids:
            int idOfGenericProblem = resultSet.getInt(DbColumn.PROBLEM_ID);
            int refId = resultSet.getInt(DbColumn.PROBLEM_REF_ID);
            int userId = resultSet.getInt(DbColumn.USER_ID);

            // Resolve generic problem:
            ResultSet userData =
              dbHelper.queryPseudoDocData(PseudoDocType.USER, userId, USER_COLUMNS);
            if ( !userData.next() )
	      throw new BulkCorrectionException("No such user: " + userId);
	    int idOfRealProblem =  gendocResolver.resolve
              (DocType.GENERIC_PROBLEM,
               idOfGenericProblem,
               userData.getInt(DbColumn.LANGUAGE),
               userData.getInt(DbColumn.THEME));

            // Do correction:
            userProblemData.setup(idOfRealProblem, refId, userId);
            String error = null;
            try
              {
                userProblemData.createCorrection(force);
              }
            catch (Throwable throwable)
              {
                error = LogUtil.unwrapThrowable(throwable).toString();
                this.logError
                  (METHOD_NAME +
                   " Correction failed for userId = " + userId + ", refId = " + refId,
                   throwable);
              }

            // Start CORRECTION_REPORT element:
            xmlElement.reset();
            xmlElement.setLocalName(XMLElement.CORRECTION_REPORT);
            xmlElement.addAttribute(XMLAttribute.PROBLEM_ID, idOfGenericProblem);
            xmlElement.addAttribute(XMLAttribute.USER_ID, userId);
            xmlElement.addAttribute(XMLAttribute.REF_ID, refId);
            xmlElement.addAttribute(XMLAttribute.STATUS, (error == null ? "ok" : "failed"));
            xmlElement.startToSAX(contentHandler);

            // Write ERROR element if necessary:
            if ( error != null )
              {
                xmlElement.reset();
                xmlElement.setLocalName(XMLElement.ERROR);
                xmlElement.setText(error);
                xmlElement.toSAX(contentHandler);
              }

            // Close CORRECTION element:
            xmlElement.reset();
            xmlElement.setLocalName(XMLElement.CORRECTION_REPORT);
            xmlElement.endToSAX(contentHandler);
          }

        // Close root element:
        xmlElement.reset();
        xmlElement.setLocalName(XMLElement.CORRECTION_REPORTS);
        xmlElement.endToSAX(contentHandler);

        // Close document if necessary:
        if ( ownDocument )
          contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new BulkCorrectionException(exception);
      }
    finally
      {
        if ( userProblemData != null )
          this.serviceManager.release(userProblemData);
        if ( gendocResolver != null )
          this.serviceManager.release(gendocResolver);
        if ( dbHelper != null )
          this.serviceManager.release(dbHelper);
      }
  }
}