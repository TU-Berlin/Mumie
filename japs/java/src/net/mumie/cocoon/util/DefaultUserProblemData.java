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

import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.mumie.cocoon.classloading.DbClassLoader;
import net.mumie.cocoon.classloading.DbClassLoaderWrapper;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.RefAttrib;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.xml.FragmentSAXFilter;
import net.mumie.cocoon.xml.MetaXMLElement;
import net.mumie.japs.datasheet.DataSheet;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.xml.dom.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link UserProblemData}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultUserProblemData.java,v 1.29 2008/08/28 13:35:42 rassy Exp $</code>
 */

public class DefaultUserProblemData extends AbstractJapsServiceable 
  implements Configurable, Recyclable, Disposable, UserProblemData
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus = new ServiceStatus(DefaultUserProblemData.class);

  /**
   * Value of {@link #correctionStatus correctionStatus} if the correction status is
   * unknown.
   */

  static final int UNKNOWN = -1;

  /**
   * Data sheet path of the score (<code>"user/meta/score"</code>).
   */

  static final String SCORE_PATH = "user/meta/score";

  /**
   * Data sheet path of the correction logh (<code>"user/meta/correction-log"</code>).
   */

  static final String CORRECTIONLOG_PATH = "user/meta/correction-log";

  /**
   * Data sheet path of the solutions (<code>"common/solution"</code>).
   */

  static final String SOLUTION_PATH = "common/solution";

  /**
   * Data sheet path of the temporary directory for correctors.
   */

  static final String CORRECTION_TMP_DIR_PATH = "common/problem/tempdir";

  /**
   * The db helper of this object.
   */

  protected DbHelper dbHelper = null;

  /**
   * The DOM parser of this object.
   */

  protected DOMParser domParser = null;

  /**
   * The db classloader wrapper of this object.
   */

  protected DbClassLoaderWrapper dbClassLoaderWrapper = null;

  /**
   * The PPD helper of this object.
   */

  protected PPDHelper ppdHelper = null;

  /**
   * Stores the status of the correction.
   */

  protected int correctionStatus = UNKNOWN;

  /**
   * Whether the common data are included in the output.
   */

  protected boolean includesCommonData = true;

  /**
   * Whether the personalized data are included in the output.
   */

  protected boolean includesPersonalizedData = true;

  /**
   * Whether the answers are included in the output.
   */

  protected boolean includesAnswers = true;

  /**
   * Whether the correction is included.
   */

  protected boolean includesCorrection = false;

  /**
   * Whether references to personalized problem data in the common data should be resolved
   * or not in the output.
   */

  protected boolean resolvePPD = true;

  /**
   * Whether the <code>common/solution</code> unit is to be removed.
   */

  protected boolean removeSolutions = false;

  /**
   * Whether the <code>toSAX</code> methods should wrap the datasheet by an element of the
   * metainfo namespace, and add metainfos (see class description).
   */

  protected boolean wrapByMeta = true;

  /**
   * The problem content as a DOM tree.
   */

  protected Document problemContent = null;

  /**
   * Holds the common data.
   */

  protected CocoonEnabledDataSheet commonData = null;

  /**
   * Holds the personalized data.
   */

  protected CocoonEnabledDataSheet personalizedData = null;

  /**
   * Holds the answers.
   */

  protected CocoonEnabledDataSheet answers = null;

  /**
   * Holds the correction.
   */

  protected CocoonEnabledDataSheet correction = null;

  /**
   * The id of the problem. Note that this is the id of the <em>real</em> problem.
   */

  protected int problemId = Id.UNDEFINED;

  /**
   * The reference id. This is the id of the reference from the worksheet to the
   * <em>generic</em> problem.
   */

  protected int refId = Id.UNDEFINED;

  /**
   * The id of the user.
   */

  protected int userId = Id.UNDEFINED;

  /**
   * Whether data sheets should be indented.
   */

  protected boolean indent = true;

  /**
   * Helper to write Meta XML elements to SAX.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement ();

  /**
   * Time format for log entries in the correction datasheet. (Used in
   * {@link #setScore setScore} only.)
   */

  protected DateFormat timeFormat = new SimpleDateFormat(TimeFormat.PRECISE);

  /**
   * The temporary directory for correctors.
   */

  protected String correctionTmpDir = null;

  // --------------------------------------------------------------------------------
  // Getting last modification times
  // --------------------------------------------------------------------------------

  /**
   * Returns the last modification time of the problem.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public long getProblemLastModified ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getProblemLastModified";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensureDbHelper();
        ResultSet resultSet = this.dbHelper.queryDatum(DocType.PROBLEM, this.problemId, DbColumn.LAST_MODIFIED);
        if ( !resultSet.next() )
          throw new UserProblemDataException
            ("Failed to retrieve last modification time of problem");
        long lastModified = resultSet.getTimestamp(DbColumn.LAST_MODIFIED).getTime();
        this.logDebug(METHOD_NAME + " 2/2: lastModified = " + lastModified);
        return lastModified;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Returns the last modification time of the common data. This is the last modification
   * time of the problem.
   *
   * @throws UserProblemDataException if something goes wrong.
   */

  public long getCommonDataLastModified ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getCommonDataLastModified";
    this.logDebug(METHOD_NAME + "1/2: Started");
    long lastModified = this.getProblemLastModified();
    this.logDebug
      (METHOD_NAME + "2/2: Done. lastModified = " + LogUtil.timeToString(lastModified));
    return lastModified;
  }

  /**
   * Returns the last modification time of the personalized data, or -1 if the latter have
   * not been created yet.
   *
   * @throws UserProblemDataException if something goes wrong.
   */

  public long getPersonalizedDataLastModified ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getPersonalizedDataLastModified";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    long lastModified = this.getAnnotationLastModified(AnnType.PERSONALIZED_PROBLEM_DATA);
    this.logDebug
      (METHOD_NAME + " 2/2: Done. lastModified = " + LogUtil.timeToString(lastModified));
    return lastModified;
  }

  /**
   * Returns the last modification time of the answers, or -1 if the latter have not been
   * created yet.
   *
   * @throws UserProblemDataException if something goes wrong.
   */

  public long getAnswersLastModified ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getAnswersLastModified";
    this.logDebug(METHOD_NAME + "1/2: Started");
    long lastModified = this.getAnnotationLastModified(AnnType.PROBLEM_ANSWERS);
    this.logDebug
      (METHOD_NAME + "2/2: Done. lastModified = " + LogUtil.timeToString(lastModified));
    return lastModified;
  }

  /**
   * Returns the last modification time of the correction, or -1 if the latter has not been
   * created yet.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public long getCorrectionLastModified ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getCorrectionLastModified";
    this.logDebug(METHOD_NAME + "1/2: Started");
    long lastModified = this.getAnnotationLastModified(AnnType.PROBLEM_CORRECTION);
    this.logDebug
      (METHOD_NAME + "2/2: Done. lastModified = " + LogUtil.timeToString(lastModified));
    return lastModified;
  }

  /**
   * Returns the last modification time of the corrector.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public long getCorrectorLastModified ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getCorrectorLastModified";
    this.logDebug(METHOD_NAME + "1/2: Started");
    try
      {
        this.ensureDbHelper();
        long lastModified = this.dbHelper.getCorrectorLastModifiedByProblemId
          (this.problemId);
        this.logDebug(METHOD_NAME + " 2/2: lastModified = " + lastModified);
        return lastModified;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Correction status
  // --------------------------------------------------------------------------------

  /**
   * Returns the correction status.
   * @throws UserProblemDataException if something goes wrong
   */

  public int getCorrectionStatus ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getCorrectionStatus";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. this.correctionStatus = " + this.correctionStatus);
    try
      {
        if ( this.correctionStatus == UNKNOWN )
          {
            // Get last modification time of correction:
            long correctionLastModified = this.getCorrectionLastModified();

            if ( correctionLastModified != -1 ) // Correction exists
              {
                this.logDebug(METHOD_NAME + " 1.1/2: Correction exists");

                // Get last modififation times of problem, answers, corrector:
                long problemLastModified = this.getProblemLastModified();
                long answersLastModified = this.getAnswersLastModified();
                long correctorLastModified = this.getCorrectorLastModified();
                if ( correctorLastModified == -1 )
                  throw new UserProblemDataException
                    ("No corrector found for reference: " + this.refId);
                this.logDebug
                  (METHOD_NAME + " 1.2/2:" +
                   " correctionLastModified = " + LogUtil.timeToString(correctionLastModified) +
                   ", problemLastModified = " + LogUtil.timeToString(problemLastModified) +
                   ", answersLastModified = " + LogUtil.timeToString(answersLastModified) +
                   ", correctorLastModified = " + LogUtil.timeToString(correctorLastModified));
                
                if ( correctionLastModified >= problemLastModified &&
                     correctionLastModified >= answersLastModified &&
                     correctionLastModified >= correctorLastModified )
                  {
                    this.logDebug(METHOD_NAME + " 1.3/2: Correction is up-to-date");
                    this.correctionStatus = UP_TO_DATE;
                  }
                else
                  {
                    this.logDebug(METHOD_NAME + " 1.3/2: Correction may be outdated");
                    this.correctionStatus = OUTDATED;
                  }
              }
            else // Correction does not exist
              {
                this.logDebug(METHOD_NAME + " 1.1/2: Correction does not exist");
                this.correctionStatus = INEXISTENT;
              }
          }

        this.logDebug
          (METHOD_NAME + " 2/2: Done. this.correctionStatus = " + this.correctionStatus);
        return this.correctionStatus;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Checks if the correction exists.
   * @throws UserProblemDataException if something goes wrong
   */

  public boolean correctionExists ()
    throws UserProblemDataException
  {
    return ( this.getCorrectionStatus() != INEXISTENT );
  }

  /**
   * Checks if the correction exists and is up-to-date.
   * @throws UserProblemDataException if something goes wrong
   */

  public boolean correctionUpToDate ()
    throws UserProblemDataException
  {
    return ( this.getCorrectionStatus() == UP_TO_DATE );
  }

  // --------------------------------------------------------------------------------
  // Creating personalized data and correction
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates the personalized data. If <code>force</code> is <code>true</code>, the
   *   personalized data are created entirely new even if they already exist (be careful
   *   with this!). If <code>force</code> is <code>false</code>, existing entries in the
   *   personalized data are preserved and only the lacking ones (if any) are created.
   * </p>
   * <p>
   *   In either case, a refernce to the personalized data (as a datasheet) is saved in the
   *   global variable {@link #personalizedData personalizedData}, so the personalized data
   *   need not be retrieved from the database again if required later.
   * </p>
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public void createPersonalizedData (boolean force)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "createPersonalizedData";
    this.logDebug(METHOD_NAME + " 1/2: Started. force = " + force);
    try
      {
        this.ensurePPDHelper();
        this.requireProblemContent();
        this.personalizedData = this.getDataSheet();
        if ( force ||
             !this.initWithStoredData(this.personalizedData, AnnType.PERSONALIZED_PROBLEM_DATA) )
          this.initEmpty(this.personalizedData);
        this.ppdHelper.create(this.problemContent, this.personalizedData, null);
        this.storeData(this.personalizedData, AnnType.PERSONALIZED_PROBLEM_DATA);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * <p>
   *   Creates the personalized data. Already existing entries in the personalized data are
   *   preserved, only the lacking entries (if any) are newly created.
   * </p>
   * <p>
   *   This is the same as
   *   {@link #createPersonalizedData(boolean) createPersonalizedData(false)}.
   * </p>
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public void createPersonalizedData ()
    throws UserProblemDataException
  {
    this.createPersonalizedData(false);
  }

  /**
   * <p>
   *   Creates the correction. If <code>force</code> is <code>true</code>, the correction is
   *   created even if it already exists (usually this is harmless). If <code>force</code>
   *   is <code>false</code>, the correction is created only if it does not exist already.
   * </p>
   * <p>
   *   In either case, a reference to the the correction (as a datasheet) is saved in the
   *   global variable {@link #correction correction}, so the correction needs not be
   *   retrieved from the database again if required later.
   * </p>
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public void createCorrection (boolean force)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "createCorrection";
    this.logDebug(METHOD_NAME + " 1/2: Started. force = " + force);
    try
      {
        // Decide if the correction must be (re)created:
        boolean create = ( force || !this.correctionUpToDate() );

        // Setup correction datasheet:
        this.correction = this.getDataSheet();

        if ( !create )
          {
            // Load correction from database:
            this.initWithStoredData(this.correction, AnnType.PROBLEM_CORRECTION);
            this.logDebug(METHOD_NAME + " 2/3: Loaded correction from database");
          }
        else
          {
            // (Re)create correction:
            this.logDebug(METHOD_NAME + " 2/3: (Re)creating correction");

	    this.initEmpty(this.correction);
            CocoonEnabledDataSheet input = null;
            try
              {
                this.ensureDbHelper();
                this.ensureDbClassLoaderWrapper();

                // Setup input for corrector:
                input = this.getDataSheet();
                if ( this.getCorrectionStatus() == INEXISTENT )
                  this.initEmpty(input);
                else
                  this.initWithStoredData(input, AnnType.PROBLEM_CORRECTION);
                this.addCommonDataTo(input);
                this.addPersonalizedDataTo(input);
                this.resolvePPD(input);
                this.addAnswersTo(input);
                input.put(CORRECTION_TMP_DIR_PATH, this.correctionTmpDir);

                // Setup corrector:
                String correctorClassName = this.dbHelper.getCorrectorQNameByProblemId
                  (this.problemId);
                if ( correctorClassName == null )
                  throw new UserProblemDataException
                    ("No corrector found for reference: " + this.refId);
                this.logDebug
                  (METHOD_NAME + " 2.1/3: correctorClassName = " + correctorClassName);
                DbClassLoader dbClassLoader = this.dbClassLoaderWrapper.getDbClassLoader();
                Class correctorClass = dbClassLoader.loadClass(correctorClassName);
                this.logDebug
                  (METHOD_NAME + " 2.2/3: correctorClass = " + correctorClass);
                ProblemCorrector corrector = (ProblemCorrector)correctorClass.newInstance();
		this.logDebug
                  (METHOD_NAME + " 2.3/3:" +
                   ", corrector = " + corrector +
                   ", input = " + LogUtil.identify(input) +
                   ", this.correction = " + LogUtil.identify(this.correction));

                // Do correction:
		corrector.correct(input, this.correction);
		this.correctionStatus = UP_TO_DATE;

		this.logDebug(METHOD_NAME + " 2.4/3: Successfully corrected");

                // Store correction:
                this.storeData(this.correction, AnnType.PROBLEM_CORRECTION);
              }
            finally
              {
                if ( input != null )
                  this.serviceManager.release(input);
              }
          }

        this.logDebug(METHOD_NAME + " 4/4: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * <p>
   *   Creates the correction provided it does not exist already.
   * </p>
   * <p>
   *   A reference to the the correction (as a datasheet) is saved in the global variable
   *   {@link #correction correction}, so the correction needs not be retrieved from the
   *   database again if required later. This is done regardless wether the correction
   *   already exists or is created.
   * </p>
   * <p>
   *   Same as
   *   {@link #createCorrection(boolean) createCorrection(false)}.
   * </p>
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public void createCorrection ()
    throws UserProblemDataException
  {
    this.createCorrection(false);
  }

  // --------------------------------------------------------------------------------
  // Deleting personalized data, answers, and correction
  // --------------------------------------------------------------------------------

  /**
   * Deletes the annotation with the specified annotation type.
   */

  protected void deleteAnnotation (int annType)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "deleteAnnotation";
    this.logDebug(METHOD_NAME + " 1/2: Started. annType = " + annType);
    try
      {
        this.ensureDbHelper();
        this.dbHelper.deleteUserAnnotation
          (this.userId,
           DocType.WORKSHEET,
           DocType.GENERIC_PROBLEM,
           this.refId,
           annType);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException(exception);
      }
  }

  /**
   * Deletes the personalized data.
   */

  public void deletePersonalizedData ()
    throws UserProblemDataException
  {
    this.deleteAnnotation(AnnType.PERSONALIZED_PROBLEM_DATA);
  }

  /**
   * Deletes the answers.
   */

  public void deleteAnswers ()
    throws UserProblemDataException
  {
    this.deleteAnnotation(AnnType.PROBLEM_ANSWERS);
  }

  /**
   * Deletes the correction.
   */

  public void deleteCorrection ()
    throws UserProblemDataException
  {
    this.deleteAnnotation(AnnType.PROBLEM_CORRECTION);
    this.correctionStatus = INEXISTENT;
  }

  /**
   * Deletes the personalized data, answers, and correction.
   */

  public void clear ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "clear";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensureDbHelper();
        this.dbHelper.deleteUserAnnotation
          (this.userId,
           DocType.WORKSHEET,
           DocType.GENERIC_PROBLEM,
           this.refId,
           new int[]
           {
             AnnType.PERSONALIZED_PROBLEM_DATA,
             AnnType.PROBLEM_ANSWERS,
             AnnType.PROBLEM_CORRECTION
           });
        this.logDebug(METHOD_NAME + " 2/2: Done");
        this.correctionStatus = INEXISTENT;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Get / set score
  // --------------------------------------------------------------------------------

  /**
   * Returns the score of the user for the problem in question, or -1 if the problem has not
   * been corrected yet.
   *
   * @throws UserProblemDataException if something goes wrong.
   */

  public float getScore ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getScore";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        if ( this.getCorrectionStatus() == INEXISTENT )
          return -1;
        this.requireCorrection();
        Float value = this.correction.getAsFloat(SCORE_PATH);
        if ( value == null )
          throw new UserProblemDataException("Can not find score in datasheet");
        float score = value.floatValue();
        this.logDebug(METHOD_NAME + " 2/2: Done. score = " + score);
        return score;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME + ": " + exception, exception);
      }
  }

  /**
   * Sets the score to the specified value.
   */

  public void setScore (float score)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "setScore";
    this.logDebug(METHOD_NAME + " 1/2: Started. score = " + score);
    SessionUser sessionUser = null;
    try
      {
        if ( score < 0 || score > 1 )
          throw new IllegalArgumentException
            ("Score value out of range: " + score + " (must be between 0 and 1)");

        this.requireCorrection();

        // Get old score, if any:
        float oldScore = this.correction.getAsFloat(SCORE_PATH, -1);

        // Set new score:
        this.correction.put(SCORE_PATH, score);

        // Init session user:
	sessionUser = (SessionUser)this.serviceManager.lookup(SessionUser.ROLE);

        // Compose log message (appears in both server and datasheet log):
        String logMessage =
          (oldScore < 0
           ? "Score set to " + score + " by user " + sessionUser.getId()
           : "Score changed from " + oldScore + " to " + score +
             " by user " + sessionUser.getId());

        // Set log entry in datasheet:
        this.correction.put
          (CORRECTIONLOG_PATH,
           this.correction.getAsString(CORRECTIONLOG_PATH, "") + // Old log text
           this.timeFormat.format(new Date(System.currentTimeMillis())) + // Timestamp
           " DefaultUserProblemData: " + logMessage + "\n");

        // Store correction:
        this.storeData(this.correction, AnnType.PROBLEM_CORRECTION);

        this.logDebug(METHOD_NAME + " 2/2: Done. " + logMessage);
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME + ": " + exception, exception);
      }
    finally
      {
        if ( sessionUser != null )
          this.serviceManager.release(sessionUser);
      }
  }

  // --------------------------------------------------------------------------------
  // Get maximum point number
  // --------------------------------------------------------------------------------

  /**
   * Returns the maximum number of points for this problem.
   */

  public int getMaxPoints ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getMaxPoints";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensureDbHelper();
        ResultSet resultSet = this.dbHelper.queryReference
          (DocType.WORKSHEET, DocType.GENERIC_PROBLEM, this.refId);
        if ( resultSet == null || !resultSet.next() )
          throw new UserProblemDataException("Result set empty");
        int maxPoints = resultSet.getInt(RefAttrib.dbColumnOf[RefAttrib.POINTS]);
        this.logDebug(METHOD_NAME + " 2/2: Done. maxPoints = " + maxPoints);
        return maxPoints;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME, exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Output
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Configures the output.
   * </p>
   * <p>
   *   By default, all parts except the correction are included in the output, and
   *   references to personalized data in the common data are resolved. This default
   *   behaviour can be changed by this method.
   * </p>
   */

  public void configureOutput (boolean includesCommonData,
                               boolean includesPersonalizedData,
                               boolean includesAnswers,
                               boolean includesCorrection,
                               boolean resolvePPD,
                               boolean removeSolutions,
                               boolean wrapByMeta,
                               String dateFormat)
  {
    final String METHOD_NAME = "configureOutput";
    this.logDebug
      (METHOD_NAME + " 1/2:" +
       ", includesCommonData = " + includesCommonData +
       ", includesPersonalizedData = " + includesPersonalizedData +
       ", includesAnswers = " + includesAnswers +
       ", includesCorrection = " + includesCorrection +
       ", resolvePPD = " + resolvePPD +
       ", removeSolutions = " + removeSolutions +
       ", wrapByMeta = " + wrapByMeta +
       ", dateFormat = " + dateFormat);
    this.includesCommonData = includesCommonData;
    this.includesPersonalizedData = includesPersonalizedData;
    this.includesAnswers = includesAnswers;
    this.includesCorrection = includesCorrection;
    this.resolvePPD = resolvePPD;
    this.removeSolutions = removeSolutions;
    this.wrapByMeta = wrapByMeta;
    this.xmlElement.setDateFormat(dateFormat);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Loads the required parts.
   */

  public void loadParts ()
    throws UserProblemDataException
  {
    final String METHOD_NAME = "loadParts";
    this.logDebug(METHOD_NAME + " 1/2");

    if ( this.includesCommonData )
      this.requireCommonData();

    if ( this.includesPersonalizedData )
      this.requirePersonalizedData();

    if ( this.includesAnswers )
      this.requireAnswers();

    if ( this.includesCorrection )
      this.requireCorrection();

    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Writes this data to <code>dataSheet</code>. The output can be configured before by
   * {@link #configureOutput configureOutput}. If the personalized data are included in the
   * output but do not exist yet, they are created. If the correction is included in the
   * output but does not exist yet, the personalized data and the correction are created
   * (the former are needed to create the latter).
   */

  public void toDataSheet (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "toDataSheet";
    this.logDebug(METHOD_NAME + " 1/2: dataSheet = " + LogUtil.identify(dataSheet));

    if ( this.includesCommonData )
      this.addCommonDataTo(dataSheet);
    if ( this.includesCommonData && this.removeSolutions )
      this.removeSolutions(dataSheet);
    if ( this.includesPersonalizedData )
      this.addPersonalizedDataTo(dataSheet);
    if ( this.resolvePPD && this.includesCommonData && this.includesPersonalizedData )
      this.resolvePPD(dataSheet);
    if ( this.includesAnswers )
      this.addAnswersTo(dataSheet);
    if ( this.includesCorrection )
      this.addCorrectionTo(dataSheet);

    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * <p>
   *   Writes this data as SAX events to <code>contentHandler</code>. The output can be
   *   configured before by {@link #configureOutput configureOutput}. If the personalized data
   *   are included in the output but do not exist yet, they are created. If the correction is
   *   included in the output but does not exist yet, the personalized data and the correction
   *   are created (the former are needed to create the latter).
   * </p>
   * <p>
   *   If <code>ownDocument</code> is <code>true</code>, the <code>startDocument</code> and
   *   <code>endDocument</code> methods are called before resp. after the XML is created. If
   *   <code>ownDocument</code> is false, this is suppressed.
   * </p>
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "toSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    CocoonEnabledDataSheet dataSheet = null;
    FragmentSAXFilter fragmentSAXFilter = null;
    try
      {
        // Start document if necessary:
        if ( ownDocument )
          contentHandler.startDocument();

        if ( this.wrapByMeta )
          {
            // Start wrapper element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_DATA);
            this.xmlElement.addAttribute(XMLAttribute.REF_ID, this.refId);
            this.xmlElement.addAttribute(XMLAttribute.USER_ID, this.userId);
            this.xmlElement.startToSAX(contentHandler);

            // Common data last modification time:
            if ( this.includesCommonData )
              this.timeToSAX
                (this.getCommonDataLastModified(),
                 contentHandler,
                 XMLElement.COMMON_DATA_LAST_MODIFIED);

            // Answers last modification time:
            if ( this.includesAnswers )
              this.timeToSAX
                (this.getAnswersLastModified(),
                 contentHandler,
                 XMLElement.ANSWERS_LAST_MODIFIED);

            // Correction last modification time:
            if ( this.includesCorrection )
              this.timeToSAX
                (this.getCorrectionLastModified(),
                 contentHandler,
                 XMLElement.CORRECTION_LAST_MODIFIED);

            // Start content element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.CONTENT);
            this.xmlElement.startToSAX(contentHandler);
          }

        // Datasheet to SAX:
        dataSheet = this.getDataSheet();
        this.initEmpty(dataSheet);
        this.toDataSheet(dataSheet);
        // Use a FragmentSAXFilter to suppress startDocument() and endDocument():
        fragmentSAXFilter =
          (FragmentSAXFilter)this.serviceManager.lookup(FragmentSAXFilter.ROLE);
        fragmentSAXFilter.setContentHandler(contentHandler);
        dataSheet.toSAX(fragmentSAXFilter);
        this.logDebug(METHOD_NAME + " 2/2: Done");

        if ( this.wrapByMeta )
          {
            // Close content element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.CONTENT);
            this.xmlElement.endToSAX(contentHandler);

            // Close wrapper element:
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_DATA);
            this.xmlElement.endToSAX(contentHandler);
          }

        // Close document if necessary:
        if ( ownDocument )
          contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    finally
      {
        if ( dataSheet != null )
          this.serviceManager.release(dataSheet);
        if ( fragmentSAXFilter != null )
          this.serviceManager.release(fragmentSAXFilter);
      }
  }

  /**
   * Auxiliary method to write a time value to SAX.
   * @param time the time value (as milliseconds since Jan 01 1970 00:00)
   * @param contentHandler recieves the SAX events
   * @param element local name of the XML element.
   */

  public void timeToSAX (long time,
                         ContentHandler contentHandler,
                         String element)
    throws SAXException
  {
    this.xmlElement.reset();
    this.xmlElement.setLocalName(element);
    this.xmlElement.addAttribute(XMLAttribute.RAW, time);
    if ( time > -1 )
      this.xmlElement.addAttribute(XMLAttribute.VALUE, new Date(time) );
    this.xmlElement.toSAX(contentHandler);
  }

  // --------------------------------------------------------------------------------
  // Validity object and caching key
  // --------------------------------------------------------------------------------

  /**
   * Creates a {@link UserProblemDataValidity} object for the data contained in the output
   * produced by {@link #toDataSheet toDataSheet} or {@link #toSAX toSAX}. This method can
   * be used by sitemap generators which generate an XML document from the output. It can
   * also be used by sitemap transformers which add an XML fragment build from the output to
   * the response.
   */

  public UserProblemDataValidity getValidity ()
  {
    final String METHOD_NAME = "getValidity";
    this.logDebug (METHOD_NAME + " 1/2: Started");
    try
      {
        long problemLastModified = -1;
        long personalizedDataLastModified = -1;
        long answersLastModified = -1;
        long correctionLastModified = -1;

        if ( this.includesCommonData || this.includesPersonalizedData )
          problemLastModified = this.getProblemLastModified();

        if ( this.includesPersonalizedData )
          {
            personalizedDataLastModified = this.getPersonalizedDataLastModified();
            // If PPD do not exist, we have to create them:
            if ( personalizedDataLastModified == -1 )
              {
                this.createPersonalizedData();
                personalizedDataLastModified = this.getPersonalizedDataLastModified();
              }
          }

        if ( this.includesAnswers )
          answersLastModified = this.getAnswersLastModified();

        if ( this.includesCorrection )
          {
            correctionLastModified = this.getCorrectionLastModified();
            if ( correctionLastModified == -1 )
              {
                // If correction does not exist, we have to create it:
                this.createCorrection();
                correctionLastModified = this.getCorrectionLastModified();
              }
          }

        UserProblemDataValidity validity = new UserProblemDataValidity
          (this.includesCommonData,
           this.includesPersonalizedData,
           this.includesAnswers,
           this.includesCorrection,
           this.resolvePPD,
           this.removeSolutions,
           problemLastModified,
           personalizedDataLastModified,
           answersLastModified,
           correctionLastModified);

        this.logDebug(METHOD_NAME + " 2/2: validity = " + validity);

        return validity;
      }
    catch (Exception exception)
      {
        this.getLogger().warn(METHOD_NAME + ": Caught exception: " + exception);
      }
    return null;
  }

  /**
   * Creates the caching key for this data. The key is a string consisting of the following
   * parts, separated by dashes:
   * <ul>
   *   <li>the problem id ({@link #problemId this.problemId}),</li>
   *   <li>the reference id ({@link #refId this.refId}),</li>
   *   <li>the user id ({@link #userId this.userId}),</li>
   *   <li><code>"1"</code> or <code>"0"</code> depending on whether the common data are
   *     included in the output or not (see {@link #includesCommonData includesCommonData}),
   *   <li><code>"1"</code> or <code>"0"</code> depending on whether the personalized data
   *     are included in the output or not (see
   *     {@link #includesPersonalizedData includesCommonData}),</li>
   *   <li><code>"1"</code> or <code>"0"</code> depending on whether the answers are
   *     included in the output or not (see {@link #includesAnswers includesAnswers}),
   *   <li><code>"1"</code> or <code>"0"</code> depending on whether the correction is
   *     included in the output or not (see {@link #includesCorrection includesCorrection}).
   *   <li><code>"1"</code> or <code>"0"</code> depending on whether PPD references are
   *      resolved  in the output or not (see {@link #resolvePPD resolvePPD}).
   *   <li><code>"1"</code> or <code>"0"</code> depending on whether solutions are removed
   *      in the output or not (see {@link #removeSolutions removeSolutions}).
   * </ul>
   */

  public Serializable getKey()
  {
    final String METHOD_NAME = "getKey";
    String key =
      this.problemId + "-" +
      this.refId + "-" +
      this.userId + "-" +
      (this.includesCommonData ? "1" : "0") + "-" +
      (this.includesPersonalizedData ? "1" : "0") + "-" +
      (this.includesAnswers ? "1" : "0") + "-" +
      (this.includesCorrection ? "1" : "0") + "-" +
      (this.resolvePPD ? "1" : "0") + "-" +
      (this.removeSolutions ? "1" : "0");
    this.logDebug(METHOD_NAME + " 1/1: key = " + key);
    return key;
  }

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>DefaultUserProblemData</code> instance.
   */

  public DefaultUserProblemData ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Configures this instance. This method sets
   * {@link #correctionTmpDir correctionTmpDir}.
   */

  public void configure (Configuration configuration)
    throws ConfigurationException
  {
    final String METHOD_NAME = "configure";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.correctionTmpDir = configuration.getChild("correction-tmp-dir").getValue().trim();
    this.logDebug
      (METHOD_NAME + " 2/2: Done." +
       " correctionTmpDir = " + this.correctionTmpDir);
  }

  /**
   * Releases all data. This means the following:
   * <ul>
   *   <li>{@link #problemContent problemContent} is set to <code>null</code>,</li>
   *   <li>if {@link #commonData commonData} is not <code>null</code>, the service manager's
   *   <code>release</code> method is called for {@link #commonData commonData} and 
   *   {@link #commonData commonData} is set to <code>null</code> afterwards,</li>
   *   <li>the same is done for {@link #personalizedData personalizedData},
   *   {@link #answers answers}, and {@link #correction correction}.
   * </ul
   */

  protected void releaseData ()
  {
    final String METHOD_NAME = "releaseData";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.problemContent = null;
    if ( this.commonData != null )
      {
        this.serviceManager.release(this.commonData);
        this.commonData = null;
      }
    if ( this.personalizedData != null )
      {
        this.serviceManager.release(this.personalizedData);
        this.personalizedData = null;
      }
    if ( this.answers != null )
      {
        this.serviceManager.release(this.answers);
        this.answers = null;
      }
    if ( this.correction != null )
      {
        this.serviceManager.release(this.correction);
        this.correction = null;
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Resets the global variables ro their initial values. The variables are
   * {@link #problemId}, {@link #refId}, {@link #userId}, {@link #includesCommonData},
   * {@link #includesPersonalizedData}, {@link #includesAnswers},
   * {@link #includesCorrection}, and {@link #resolvePPD}.
   */

  protected void resetVariables ()
  {
    this.problemId = Id.UNDEFINED;
    this.refId = Id.UNDEFINED;
    this.userId = Id.UNDEFINED;
    this.includesCommonData = true;
    this.includesPersonalizedData = true;
    this.includesAnswers = true;
    this.includesCorrection = false;
    this.resolvePPD = true;
    this.removeSolutions = false;
    this.wrapByMeta = true;
    this.correctionStatus = UNKNOWN;
    this.xmlElement.setDateFormat(null); // Reset the date fromat to the default
  }

  /**
   * Releases the db helper, DOM parser, db class loader wrapper, and PPD helper provided
   * they are not <code>null</code>.
   */

  protected void releaseServices ()
  {
    final String METHOD_NAME = "releaseServices";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    if ( this.dbHelper != null )
      {
        this.serviceManager.release(this.dbHelper);
        this.dbHelper = null;
      }
    if ( this.domParser != null )
      {
        this.serviceManager.release(this.domParser);
        this.domParser = null;
      }
    if ( this.dbClassLoaderWrapper != null )
      {
        this.serviceManager.release(this.dbClassLoaderWrapper);
        this.dbClassLoaderWrapper = null;
      }
    if ( this.ppdHelper != null )
      {
        this.serviceManager.release(this.ppdHelper);
        this.ppdHelper = null;
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Initializes this instance to represent the specified problem/user pair.
   */

  public void setup (int problemId, int refId, int userId)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2:" +
       " problemId = " + problemId +
       " refId = " + refId +
       ", userId = " + userId);

    // Reset variables:
    this.resetVariables();

    // Release the data:
    this.releaseData();

    // Set problem, ref and user id:
    this.problemId = problemId;
    this.refId = refId;
    this.userId = userId;

    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Recycles this object.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseData();
    this.releaseServices();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this object.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.resetVariables();
    this.releaseData();
    this.releaseServices();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // "Ensure" methods for the services
  // --------------------------------------------------------------------------------

  /**
   * Ensures that the db helper is ready to use.
   */

  protected void ensureDbHelper ()
    throws ServiceException 
  {
    if ( this.dbHelper == null )
      this.dbHelper = (DbHelper)this.serviceManager.lookup(DbHelper.ROLE);
  }

  /**
   * Ensures that the DOM parser is ready to use.
   */

  protected void ensureDOMParser ()
    throws ServiceException 
  {
    if ( this.domParser == null )
      this.domParser = (DOMParser)this.serviceManager.lookup(DOMParser.ROLE);
  }

  /**
   * Ensures that the db class loader wrapper is ready to use.
   */

  protected void ensureDbClassLoaderWrapper ()
    throws ServiceException 
  {
    if ( this.dbClassLoaderWrapper == null )
      this.dbClassLoaderWrapper =
        (DbClassLoaderWrapper)this.serviceManager.lookup(DbClassLoaderWrapper.ROLE);
  }

  /**
   * Ensures that the PPD helper is ready to use.
   */

  protected void ensurePPDHelper ()
    throws ServiceException 
  {
    if ( this.ppdHelper == null )
      this.ppdHelper = (PPDHelper)this.serviceManager.lookup(PPDHelper.ROLE);
  }

  // --------------------------------------------------------------------------------
  // "Require" methods for the data
  // --------------------------------------------------------------------------------

  /**
   * If {@link #problemContent problemContent} is <code>null</code>,
   * loads the problem content and saves it (as a DOM tree) in the global variable
   * {@link #problemContent problemContent}.
   */

  protected void requireProblemContent ()
    throws UserProblemDataException
  {
    if ( this.problemContent != null ) return;
    final String METHOD_NAME = "requireProblemContent";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensureDbHelper();
        this.ensureDOMParser();
        ResultSet resultSet = this.dbHelper.queryDatum
          (DocType.PROBLEM, this.problemId, DbColumn.CONTENT);
        if ( !resultSet.next() )
          throw new UserProblemDataException
            ("Failed to retrieve content for problem: " + this.problemId);
        Reader reader = resultSet.getCharacterStream(DbColumn.CONTENT);
        this.problemContent = this.domParser.parseDocument(new InputSource(reader));
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);        
      }
  }

  /**
   * If {@link #commonData commonData} is <code>null</code>, loads the common data and saves
   * them (as a datasheet) in the global variable {@link #commonData commonData}.
   */

  protected void requireCommonData ()
    throws UserProblemDataException
  {
    if ( this.commonData != null )  return;
    final String METHOD_NAME = "requireCommonData";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensureDOMParser();
        this.requireProblemContent();
        this.commonData = this.getDataSheet();
        this.commonData.setEmptyDocument(this.domParser);
        this.commonData.extract(this.problemContent, DataSheet.APPEND);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * If {@link #answers answers} is <code>null</code>, loads the answers from the database
   * and stores them in the global variable {@link #answers answers}.
   */

  protected void requireAnswers ()
    throws UserProblemDataException
  {
    if ( this.answers != null ) return;
    final String METHOD_NAME = "requireAnswers";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensurePPDHelper();
        this.requireProblemContent();
        this.answers = this.getDataSheet();
        if ( !this.initWithStoredData(this.answers, AnnType.PROBLEM_ANSWERS) )
          this.initEmpty(this.answers);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Calls {@link #createPersonalizedData(boolean) createPersonalizedData(false)} if
   * {@link #personalizedData this.personalizedData} is <code>null</code>.
   */

  protected final void requirePersonalizedData ()
    throws UserProblemDataException
  {
    if ( this.personalizedData == null )
      this.createPersonalizedData(false);
  }

  /**
   * Calls {@link #createCorrection(boolean) createCorrection(false)} if
   * {@link #correction this.correction} is <code>null</code>.
   */

  protected final void requireCorrection ()
    throws UserProblemDataException
  {
    if ( this.correction == null )
      this.createCorrection(false);
  }

  // --------------------------------------------------------------------------------
  // "Add-to" methods for the data
  // --------------------------------------------------------------------------------

  /**
   * Adds the common data to <code>dataSheet</code>.
   */

  protected void addCommonDataTo (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "addCommonDataTo";
    this.logDebug(METHOD_NAME + " 1/2: dataSheet = " + LogUtil.identify(dataSheet));
    this.requireCommonData();
    dataSheet.merge(this.commonData, DataSheet.REPLACE);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Adds the personalized data to <code>dataSheet</code>.
   */

  protected void addPersonalizedDataTo (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "addPersonalizedDataTo";
    this.logDebug(METHOD_NAME + " 1/2: dataSheet = " + dataSheet);
    this.requirePersonalizedData();
    dataSheet.merge(this.personalizedData, DataSheet.REPLACE);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Adds the answers to <code>dataSheet</code>.
   */

  protected void addAnswersTo (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "addAnswersTo";
    this.logDebug(METHOD_NAME + " 1/2: dataSheet = " + dataSheet);
    this.requireAnswers();
    dataSheet.merge(this.answers, DataSheet.REPLACE);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Adds the correction to <code>dataSheet</code>.
   */

  protected void addCorrectionTo (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "addCorrectionTo";
    this.logDebug(METHOD_NAME + " 1/2: dataSheet = " + LogUtil.identify(dataSheet));
    this.requireCorrection();
    dataSheet.merge(this.correction, DataSheet.REPLACE);
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // Identification method
  // --------------------------------------------------------------------------------
  
  /**
   * Returns a string that identifies this instance. It has the
   * following form:<pre>
   *   "DefaultUserProblemData" +
   *   '#' + instanceId
   *   '(' + lifecycleStatus
   *   ',' + numberOfRecycles
   *   ',' + {@link #refId refId}
   *   ',' + {@link #problemId problemId}
   *   ',' + {@link #userId userId}
   *   ',' + {@link #includesCommonData includesCommonData}
   *   ',' + {@link #includesPersonalizedData includesPersonalizedData}
   *   ',' + {@link #includesAnswers includesAnswers}
   *   ',' + {@link #includesCorrection includesCorrection}
   *   ',' + {@link #resolvePPD resolvePPD}
   *   ',' + {@link #removeSolutions removeSolutions}
   *   ')'</pre>
   * where <code>instanceId</code> is the instance id, <code>lifecycleStatus</code> the
   * lifecycle status, and <code>numberOfRecycles</code> the number of recycles of this
   * instance.
   */

  public String getIdentification ()
  {
    return
      "DefaultUserProblemData" +
      '#' + this.instanceStatus.getInstanceId() +
      '(' + this.instanceStatus.getLifecycleStatus() +
      ',' + this.instanceStatus.getNumberOfRecycles() +
      ',' + this.refId +
      ',' + this.problemId +
      ',' + this.userId +
      ',' + this.includesCommonData +
      ',' + this.includesPersonalizedData +
      ',' + this.includesAnswers +
      ',' + this.includesCorrection +
      ',' + this.resolvePPD +
      ',' + this.removeSolutions +
      ')';
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns a data sheet from the pool.
   */

  protected CocoonEnabledDataSheet getDataSheet ()
    throws ServiceException 
  {
    return
      (CocoonEnabledDataSheet)this.serviceManager.lookup(CocoonEnabledDataSheet.ROLE);
  }

  /**
   * Initializes <code>dataSheet</code> with an empty data sheet.
   * 
   */

  protected void initEmpty (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "initEmpty";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    try
      {
        this.ensureDOMParser();
        dataSheet.setEmptyDocument(this.domParser);
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Initializes <code>dataSheet</code> with the data found in the user annotations table
   * for the specified annotation type.
   */

  protected boolean initWithStoredData (CocoonEnabledDataSheet dataSheet,
                                        int annotationType)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "initWithStoredData";
    this.logDebug(METHOD_NAME + " 1/2: annotationType = " + annotationType);
    try
      {
        this.ensureDbHelper();
        String[] columns =
          (annotationType == AnnType.PROBLEM_CORRECTION
           ? new String[] {DbColumn.CONTENT, DbColumn.SCORE}
           : new String[] {DbColumn.CONTENT});
        ResultSet resultSet = this.dbHelper.queryUserAnnotation 
          (this.userId,
           DocType.WORKSHEET,
           DocType.GENERIC_PROBLEM,
           this.refId,
           annotationType,
           columns);
        boolean storedDataFound = resultSet.next();
        if ( storedDataFound )
          {
            this.ensureDOMParser();
            Reader reader = resultSet.getCharacterStream(DbColumn.CONTENT);
            Document dataAsDOM = this.domParser.parseDocument(new InputSource(reader));
            dataSheet.setDocument(dataAsDOM);
            if ( annotationType == AnnType.PROBLEM_CORRECTION )
              {
                float score = resultSet.getFloat(DbColumn.SCORE);
                if ( !resultSet.wasNull() ) dataSheet.put(SCORE_PATH, score);
              }
          }
        this.logDebug(METHOD_NAME + " 2/2: Done. storedDataFound = " + storedDataFound);
        return storedDataFound;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Stores <code>dataSheet</code> in the user annotations table, with the specified
   * annotation type.
   */

  protected void storeData (CocoonEnabledDataSheet dataSheet,
                            int annotationType)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "storeData";
    this.logDebug(METHOD_NAME + " 1/2: annotationType = " + annotationType);
    try
      {
        this.ensureDbHelper();
        if ( this.indent ) dataSheet.indent();
        String[] attribColumns = null;
        Object[] attribValues = null;
        if ( annotationType == AnnType.PROBLEM_CORRECTION )
          {
            Float score = dataSheet.getAsFloat(SCORE_PATH);
            // -- Disabled because of traditional problems: --
            // if ( score == null )
            //   throw new UserProblemDataException("Missing score");
            this.dbHelper.storeUserAnnotation
              (this.userId,
               DocType.WORKSHEET,
               DocType.GENERIC_PROBLEM,
               this.refId,
               annotationType,
               dataSheet.toXMLCode(),
               new String[] {DbColumn.SCORE},
               new Float[] {score});
          }
        else
          {
            this.dbHelper.storeUserAnnotation
              (this.userId,
               DocType.WORKSHEET,
               DocType.GENERIC_PROBLEM,
               this.refId,
               annotationType,
               dataSheet.toXMLCode());
          }
        this.logDebug(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Returns the last modification time of the annotation with the specified annotation
   * type.
   */

  protected long getAnnotationLastModified (int annotationType)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "getAnnotationLastModified";
    this.logDebug(METHOD_NAME + " 1/2: annotationType = " + annotationType);
    try
      {
        this.ensureDbHelper();
        long lastModified = this.dbHelper.getUserAnnotationLastModified
          (this.userId,
           DocType.WORKSHEET,
           DocType.GENERIC_PROBLEM,
           this.refId,
           annotationType);
        this.logDebug(METHOD_NAME + " 2/2: lastModified = " + lastModified);
        return lastModified;
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Resolves PPD elements in <code>dataSheet</code>.
   */

  protected void resolvePPD (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "resolvePPD";
    try
      {
        this.ensurePPDHelper();
        this.ppdHelper.resolve(dataSheet.getDocument(), dataSheet);
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }

  /**
   * Removes the <code>common/solution</code> unit from the specified datasheet.
   */

  protected void removeSolutions (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException
  {
    final String METHOD_NAME = "removeSolutions";
    try
      {
        dataSheet.removeUnit(SOLUTION_PATH);
      }
    catch (Exception exception)
      {
        throw new UserProblemDataException
          (this.getIdentification() + ": " + METHOD_NAME +
           ": Caught exception: " + exception);
      }
  }
}
