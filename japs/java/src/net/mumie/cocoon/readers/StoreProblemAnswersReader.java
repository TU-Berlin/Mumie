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

package net.mumie.cocoon.readers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.readers.ServiceableJapsReader;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.receipt.ReceiptHelper;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.ProblemAnswerFormatException;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.xml.dom.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import net.mumie.cocoon.notions.Category;

/**
 * <p>
 *   Stores the answers for a problem in the database. The answers must be given as a data
 *   sheet. The data sheet code is read from a request parameter named
 *   <code>"content"</code>, or, if no such request parameter exists, from a 
 *   parameter with the same name passed to this reader.
 * </p>
 * <p>
 *   Expects the following paramters, which may be specified either as request parameters
 *   or as parameters in the sitemap:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>ref</code></td>
 *       <td>Id of the generic_problem &ndash; worksheet reference.</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>course</code></td>
 *       <td>Id of the course</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>worksheet</code></td>
 *       <td>Id of the worksheet</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>problem</code></td>
 *       <td>Id of the (non-generic) problem</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>content</code></td>
 *       <td>XML code of the answer data sheet.</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>issue-receipt</code></td>
 *       <td>Boolean. Specifies whether a receipt is created and sent back to the client.</td>
 *       <td>No. Default is <code>"false"</code>.</td>
 *     </tr>
 *     <tr>
 *       <td><code>send-receipt-with-response</code></td>
 *       <td>Boolean. If <code>"true"</code>, the receipt is sent as the body of the response. Otherwise, only the receipt
 *         name is transmitted with the response, and the user gets a link for downloading the receipt. This parameter is
 *         in effect only if <code>issue-receipt</code> is <code>"true"</code>.</td>
 *       <td>No. Default is <code>"yes"</code>.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   If a parameter is specified both in the request and in the sitemap, the latter takes
 *   precedence.
 * </P>
 * <p>
 *   If the <code>issue-receipt</code> flag is turned on, a receipt is created, signed, and
 *   sent back to the client as a zip file.
 * </P>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: StoreProblemAnswersReader.java,v 1.17 2009/10/01 00:42:50 rassy Exp $</code>
 */

public class StoreProblemAnswersReader extends ServiceableJapsReader
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(StoreProblemAnswersReader.class);

  /**
   * Name of the status header.
   */

  protected static String STATUS = "X-Mumie-Status";

  /**
   * Name of the filename header.
   */

  protected static String FILENAME = "X-Mumie-Filename";

  /**
   * Value of the status header if a receipt is created.
   */

  protected static String OK_RECEIPT = "OK. Issued receipt";

  /**
   * Value of the status header no receipt is created.
   */

  protected static String OK = "OK";

  /**
   * The time format used by this reader.
   */

  protected DateFormat timeFormat = null;

  /**
   * The time format used in the receipt filename.
   */

  protected DateFormat filenameTimeFormat = null;

  /**
   * Wether a receipt is send bak to the user.
   */

  protected boolean issueReceipt;

  /**
   * Wether the receipt is sent back with the response. In effect only if {@link #issueReceipt issueReceipt} is true.
   */

  protected boolean sendReceiptWithResponse;

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>StoreProblemAnswersReader</code> instance.
   */

  public StoreProblemAnswersReader ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Calls the superclass setup method, sets the global variables ({@link #refId refId},
   * {@link #validate validate}, {@link #issueReceipt issueReceipt}).
   */

  public void setup (SourceResolver sourceResolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException, SAXException, IOException
  {
    final String METHOD_NAME = "setup";
    this.logDebug(METHOD_NAME + " 1/2: started");

    try
      {
        // Call superclass setup method:
        super.setup(sourceResolver, objectModel, source, parameters);

        // Add request parameters to reader parameters:
        ParamUtil.addRequestParams
          (this.parameters, ObjectModelHelper.getRequest(objectModel), false);

        // Set issueReceipt and sendReceiptWithResponse flags:
        this.issueReceipt = ParamUtil.getAsBoolean(this.parameters, "issue-receipt", false);
        this.sendReceiptWithResponse = ParamUtil.getAsBoolean(this.parameters, "send-receipt-with-response", true);
        this.logDebug
          (METHOD_NAME + " 2/2: Done." +
           " issueReceipt = " + this.issueReceipt +
           ", sendReceiptWithResponse = " + this.sendReceiptWithResponse);
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
  }

  /**
   * Recycles this instance. Calls {@link #resetVariables resetVariables} and the superclass
   * <code>recycle</code> method.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    super.recycle();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.getLogger().debug(METHOD_NAME + " 1/2");
    super.dispose();
    this.instanceStatus.notifyDispose();
    this.getLogger().debug(METHOD_NAME + " 2/2");
  }

  // --------------------------------------------------------------------------------
  // The generate method
  // --------------------------------------------------------------------------------

  /**
   * Stores the answers, issues an receipt if necessary, generates the response.
   */

  public void generate ()
    throws ProcessingException 
  {
    final String METHOD_NAME = "generate";
    this.logDebug(METHOD_NAME + "1/2: Started");

    SessionUser user = null;
    DbHelper dbHelper = null;
    DOMParser domParser = null;
    CocoonEnabledDataSheet dataSheet = null;
    ReceiptHelper receiptHelper = null;

    try
      {
        // Get data from parameters:
        int refId = ParamUtil.getAsId(this.parameters, "ref");
        int courseId = ParamUtil.getAsId(this.parameters, "course");
        int worksheetId = ParamUtil.getAsId(this.parameters, "worksheet");
        int problemId = ParamUtil.getAsId(this.parameters, "problem");
        boolean validate = ParamUtil.getAsBoolean(this.parameters, "validate", false);
        String answers = ParamUtil.getAsString(this.parameters, "content");
        
        // Init user and db helper:
        user = (SessionUser)this.serviceManager.lookup(SessionUser.ROLE);
        dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);

        int userId = user.getId();

        // Store answers:
        dbHelper.storeUserAnnotation
          (userId,
           DocType.WORKSHEET,
           DocType.GENERIC_PROBLEM,
           refId,
           AnnType.PROBLEM_ANSWERS,
           answers);

        // Get response object:
        Response response = ObjectModelHelper.getResponse(this.objectModel);

        if ( this.issueReceipt )
          {
            // Init receipt helper:
            receiptHelper = (ReceiptHelper)this.serviceManager.lookup(ReceiptHelper.ROLE);

            // Create datasheet and init it with answers:
            dataSheet =
              (CocoonEnabledDataSheet)this.serviceManager.lookup(CocoonEnabledDataSheet.ROLE);
            domParser = (DOMParser)this.serviceManager.lookup(DOMParser.ROLE);
            Reader reader = new StringReader(answers);
            Document document = domParser.parseDocument(new InputSource(reader));
            dataSheet.setDocument(document);

            // Check datasheet:
            for (String path : dataSheet.getAllDataPaths())
              {
                if ( !path.startsWith("user/answer/") )
                  throw new ProblemAnswerFormatException
                    ("Unallowed datasheet path in answers: " + path +
                     " (user " + userId + ", ref id " + refId);
              }

            // Add receipt data to datasheet:
            receiptHelper.addReceiptData(userId, refId, courseId, worksheetId, problemId, dataSheet, dbHelper);
            
            // Get recommended filename:
            String filename = receiptHelper.getFilename(dataSheet);

            // Set response headers:
            response.addHeader(STATUS, OK_RECEIPT);
            response.addHeader(FILENAME, filename);

            // Sign receipt, zip it, and write it to the output stream or store it in the receipt folder:
            if ( this.sendReceiptWithResponse )
              receiptHelper.signZipAndWrite(dataSheet, this.out);
            else
              receiptHelper.signZipAndStore(dataSheet);
          }
        else
          {
            response.addHeader(STATUS, OK);
            this.out.write(OK.getBytes());
          }
        
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new ProcessingException(exception);
      }
    finally
      {
        if ( receiptHelper != null ) this.serviceManager.release(receiptHelper);
        if ( dataSheet != null ) this.serviceManager.release(dataSheet);
        if ( domParser != null ) this.serviceManager.release(domParser);
        if ( dbHelper != null ) this.serviceManager.release(dbHelper);
        if ( user != null ) this.serviceManager.release(user);
      }
  }

  // --------------------------------------------------------------------------------
  // Mime type
  // --------------------------------------------------------------------------------

  /**
   * Returns the mime type of the response
   */

  public String getMimeType ()
  {
    return
      MediaType.nameFor(this.issueReceipt && this.sendReceiptWithResponse ? MediaType.APPLICATION_ZIP : MediaType.TEXT_PLAIN);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Replaces all whitespaces by underscores.
   */

  protected static String normalizeFilename (String filename)
  {
    char[] chars = filename.toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        if ( Character.isWhitespace(chars[i]) )
          chars[i] = '_';
      }
    return new String(chars);
  }
}
