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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Represents the data of a certain problem/user pair.
 * </p>
 * <p>
 *   User problem data can be diveded into the following parts:
 * </p>
 * <ul>
 *   <li><em>Common data:</em> Data that is unique for all users</li>
 *   <li><em>Personalized data:</em> Data that is particular for the user</li>
 *   <li><em>Answers:</em> The users answers</li> 
 *   <li><em>Correction:</em> The correction/marking</li> 
 * </ul>
 * <p>
 *   Common data are extracted from the problem content on demand. The other three parts are
 *   created on certain occasions and are then stored in the database.
 * </p>
 * <p>
 *   This class provides methods for the following purposes:
 * </p>
 * <ul>
 *   <li>Output the user problem data as a datasheet or SAX events (see
 *   {@link #toDataSheet toDataSheet}, {@link #toSAX toSAX}),</li>
 *   <li>Creating personalized data and/or correction
 *   (see {@link #createPersonalizedData(boolean) createPersonalizedData},
 *   {@link #createCorrection(boolean) createCorrection}),</li>
 *   <li>Deleting personalized data, answers, and/or correction (see
 *   {@link #deletePersonalizedData deletePersonalizedData},
 *   {@link #deleteAnswers deleteAnswers}, {@link #deleteCorrection deleteCorrection},
 *   {@link #cleaer clear}).</li>
 *   <li>Getting information about the user problem data (see {@link #getScore getScore},
 *   {@link #getMaxPoints getMaxPoints}, {@link #getCorrectionStatus getCorrectionStatus}),
 *   </li> 
 *   <li>Creating keys and validity objects for caching (see {@link #getKey getKey},
 *   {@link #getValidity getValidity}).</li>
 * </ul>
 * <p>
 *   An instance of this class must initialized by the {@link #setup setup} method, which
 *   ties the instance to a particular problem/user pair. A typical usage of this class
 *   might look like the following:
 * </p>
 * <pre>
 *   UserProblemData userProblemData = null;
 *   try
 *     {
 *       userProblemData = (UserProblemData)this.serviceManager.lookup(UserProblemData.ROLE);
 *       userProblemDate.setup(refId, userId);
 *
 *       // Use userProblemData here
 *     }
 *   catch (Exception exception)
 *     {
 *       // Handle exceptions here
 *     }
 *   finally
 *     {
 *       if ( userProblemData != null )
 *         this.serviceManager.release(userProblemData);
 *     }</pre>
 * <p>
 *   The output of the user problem data can be controlled by the
 *   {@link #configureOutput configureOutput} method. It takes the following arguments:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Type</td>
 *       <td>Description</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>includesCommonData</code></td>
 *       <td><code>boolean</code></td>
 *       <td>Whether the common data is included</td>
 *     </tr>
 *     <tr>
 *       <td><code>includesPersonalizedData</code></td>
 *       <td><code>boolean</code></td>
 *       <td>Whether the personalized data is included</td>
 *     </tr>
 *     <tr>
 *       <td><code>includesAnswers</code></td>
 *       <td><code>boolean</code></td>
 *       <td>Whether the answers are included</td>
 *     </tr>
 *     <tr>
 *       <td><code>includesCorrection</code></td>
 *       <td><code>boolean</code></td>
 *       <td>Whether the correction/marking is included</td>
 *     </tr>
 *     <tr>
 *       <td><code>resolvePPD</code></td>
 *       <td><code>boolean</code></td>
 *       <td>Whether the PPD references in the common data are resolved (i.e., replaced by
 *       their actual values)</td>
 *     </tr>
 *     <tr>
 *       <td><code>wrapByMeta</code></td>
 *       <td><code>boolean</code></td>
 *       <td>Whether the <code>toSAX</code> methods should wrap the datasheet by XML
 *       from the metainfo namespace, and add metainfos (s.b.)</td> 
 *     </tr>
 *     <tr>
 *       <td><code>dateFormat</code></td>
 *       <td><code>String</code></td>
 *       <td>THe date fromat. Must be a pattern string as described with
 *       {@link SimpleDateFormat SimpleDateFormat}.</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   The <code>wrapByMeta</code> flag only effects the <code>toSAX</code> methods, not the
 *   <code>toDataSheet</code> method. If <code>wrapByMeta</code> is <code>true</code>, the
 *   XML produced by this methods looks like the following:
 * </p>
 * <pre>
 *   &lt;mumie:user_problem_data user_id="24" ref_id="243"&gt;
 *     &lt;mumie:common_data_last_modified value="2006-04-13 12:45:12 567" raw="162561526521"/&gt;
 *     &lt;mumie:answers_last_modified value="2006-04-21 12:45:12 567" raw="3746736473"/&gt;
 *     &lt;mumie:correction_last_modified value="2006-04-25 12:45:12 567" raw="394839495"/&gt;
 *     &lt;mumie:content&gt;
 *       &lt;data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"&gt;
 *         &lt;!-- datasheet contents --&gt;
 *       &lt;/data_sheet&gt;
 *     &lt;/mumie:content&gt;
 *   &lt;/mumie:user_problem_data&gt;</pre>
 * <p>
 *   I.e., the datasheet is wrapped by XML elements from the metainfo namespace, and some
 *   metainfos are added. If <code>wrapByMeta</code> is <code>false</code>, the output of
 *   the <code>toSAX</code> methods is just:
 * </p>
 * <pre>
 *   &lt;data_sheet xmlns="http://www.mumie.net/xml-namespace/data-sheet"&gt;
 *     &lt;!-- datasheet contents --&gt;
 *   &lt;/data_sheet&gt;</pre>
 * <p>
 *   I.e., it contains the datasheet only.
 * </p>
 *
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UserProblemData.java,v 1.12 2008/05/15 12:11:53 rassy Exp $</code>
 */

public interface UserProblemData extends Identifyable
{
  // --------------------------------------------------------------------------------
  // Global static constants
  // --------------------------------------------------------------------------------

  /**
   * Role as an Avalon service (<code>UserProblemData.class.getName()</code>).
   */

  public static final String ROLE = UserProblemData.class.getName();

  /**
   * Correction status code indicating that the correction
   * is up-to-date.
   */

  public static final int UP_TO_DATE = 0;

  /**
   * Correction status code indicating that the correction
   * is outdated.
   */

  public static final int OUTDATED = 1;

  /**
   * Correction status code indicating that the correction
   * does not exist.
   */

  public static final int INEXISTENT = 2;

  // --------------------------------------------------------------------------------
  // Initializing
  // --------------------------------------------------------------------------------

  /**
   * Initializes this instance to represent the specified problem/user pair.
   */

  public void setup (int problemId, int refId, int userId);

  // --------------------------------------------------------------------------------
  // Requesting last modification times
  // --------------------------------------------------------------------------------

  /**
   * Returns the last modification time of the problem.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public long getProblemLastModified ()
    throws UserProblemDataException;

  /**
   * Returns the last modification time of the common data. This is the last modification
   * time of the problem.
   *
   * @throws UserProblemDataException if something goes wrong.
   */

  public long getCommonDataLastModified ()
    throws UserProblemDataException;

  /**
   * Returns the last modification time of the personalized data, or -1 if the latter have
   * not been created yet.
   *
   * @throws UserProblemDataException if something goes wrong.
   */

  public long getPersonalizedDataLastModified ()
    throws UserProblemDataException;

  /**
   * Returns the last modification time of the answers, or -1 if the latter have not been
   * created yet.
   *
   * @throws UserProblemDataException if something goes wrong.
   */

  public long getAnswersLastModified ()
    throws UserProblemDataException;

  /**
   * Returns the last modification time of the correction, or -1 if the latter has not been
   * created yet.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public long getCorrectionLastModified ()
    throws UserProblemDataException;

  /**
   * Returns the last modification time of the corrector.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public long getCorrectorLastModified ()
    throws UserProblemDataException;

  // --------------------------------------------------------------------------------
  // Correction status
  // --------------------------------------------------------------------------------

  /**
   * Returns the correction status.
   * @throws UserProblemDataException if something goes wrong
   */

  public int getCorrectionStatus ()
    throws UserProblemDataException;

  /**
   * Checks if the correction exists.
   * @throws UserProblemDataException if something goes wrong
   */

  public boolean correctionExists ()
    throws UserProblemDataException;

  /**
   * Checks if the correction exists and is up to date.
   * @throws UserProblemDataException if something goes wrong
   */

  public boolean correctionUpToDate ()
    throws UserProblemDataException;

  // --------------------------------------------------------------------------------
  // Creating personalized data and correction
  // --------------------------------------------------------------------------------

  /**
   * Creates the personalized data. If <code>force</code> is <code>true</code>, the
   * personalized data are created entirely new even if they already exist (be careful with
   * this!). If <code>force</code> is <code>false</code>, existing entries in the
   * personalized data are preserved and only the lacking ones (if any) are created.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public void createPersonalizedData (boolean force)
    throws UserProblemDataException;

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
    throws UserProblemDataException;

  /**
   * Creates the correction. If <code>force</code> is <code>true</code>, the correction is
   * created even if it already exists (usually this is harmless). If <code>force</code> is
   * <code>false</code>, the correction is created only if it does not exist already.
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public void createCorrection (boolean force)
    throws UserProblemDataException;

  /**
   * <p>
   *   Creates the correction provided it does not exist already
   * </p>
   * <p>
   *   This is the same as
   *   {@link #createCorrection(boolean) createCorrection(false)}.
   * </p>
   *
   * @throws UserProblemDataException if something goes wrong
   */

  public void createCorrection ()
    throws UserProblemDataException;

  // --------------------------------------------------------------------------------
  // Deleting personalized data, answers, and correction
  // --------------------------------------------------------------------------------

  /**
   * Deletes the personalized data.
   */

  public void deletePersonalizedData ()
    throws UserProblemDataException;

  /**
   * Deletes the answers.
   */

  public void deleteAnswers ()
    throws UserProblemDataException;

  /**
   * Deletes the correction.
   */

  public void deleteCorrection ()
    throws UserProblemDataException;

  /**
   * Deletes the personalized data, answers, and correction.
   */

  public void clear ()
    throws UserProblemDataException;

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
    throws UserProblemDataException;

  /**
   * Sets the score to the specified value.
   */

  public void setScore (float score)
    throws UserProblemDataException;

  // --------------------------------------------------------------------------------
  // Get maximum point number
  // --------------------------------------------------------------------------------

  /**
   * Returns the maximum number of points for this problem.
   */

  public int getMaxPoints ()
    throws UserProblemDataException;

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
                               String dateFormat);

  /**
   * Loads the required parts.
   */

  public void loadParts ()
    throws UserProblemDataException;

  /**
   * Writes this data to <code>dataSheet</code>. The output can be configured before by
   * {@link #configureOutput configureOutput}. If the personalized data are included in the
   * output but do not exist yet, they are created. If the correction is included in the
   * output but does not exist yet, the personalized data and the correction are created
   * (the former are needed to create the latter).
   */

  public void toDataSheet (CocoonEnabledDataSheet dataSheet)
    throws UserProblemDataException;

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
    throws SAXException;

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

  public UserProblemDataValidity getValidity ();

  /**
   * Creates a <code>Serializable</code> suitable as a caching key for this data. See
   * {@link #getValidity getValidity} for the purpose odf this method.
   */

  public Serializable getKey();
}
