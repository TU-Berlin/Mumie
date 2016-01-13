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
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.xml.MetaXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link UserWorksheetGrades}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Fritz Lehmann-Grube
 *   <a href="mailto:lehmannf@math.tu-berlin.de">lehmannf@math.tu-berlin.de</a>
 * @version <code>$Id: UserWorksheetGradesImpl.java,v 1.7 2008/03/04 11:43:11 rassy Exp $</code>
 * 
 * @deprecated See interface {@link UserWorksheetGrades}.
 */

public class UserWorksheetGradesImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, UserWorksheetGrades
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The id of the user this grades belong to.
   */

  protected int userId = Id.UNDEFINED;

  /**
   * The id of the worksheet this grades belong to.
   */

  protected int worksheetId = Id.UNDEFINED;

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(UserWorksheetGradesImpl.class);

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected MetaXMLElement xmlElement = new MetaXMLElement();

  // --------------------------------------------------------------------------------
  // Inner classes
  // --------------------------------------------------------------------------------

  /**
   * Comprises the data for a given problem.
   */

  public static final class Record
  {
    /**
     * Indicates that no answers and no correction exist for the problem.
     */

    public static final int VOID = 0;

    /**
     * Indicates that answers, but no corrections exist for the problem.
     */

    public static final int ANSWERS = 1;

    /**
     * Indicates that a correction exists for the problem.
     */

    public static final int CORRECTION = 2;

    /**
     * The priority of the database result set row from which this record has been
     * constructed. See {@link #setPriority setPriority} for more information on
     * priorities.
     */

    protected int priority = -1;

    /**
     * The status of the problem ({@link #NOT_EDITED NOT_EDITED},
     * {@link #NOT_CORRECTED NOT_CORRECTED}, or {@link #CORRECTED CORRECTED}).
     */

    protected int status = -1;

    /**
     * The score.
     */

    protected float score = -1;

    /**
     * The problem's id.
     */

    protected int problemId = -1;

    /**
     * The category.
     */

    protected int category = -1;

    /**
     * The label.
     */

    protected String label = null;

    /**
     * The points.
     */

    protected int points = -1;

    /**
     * Sets the priority of the database result set row from which this record has been
     * constructed. There are three types of rows:
     * <ul>
     *   <li>
     *     Rows where the {@link DbColumn#SCORE SCORE} column is not SQL NULL. They have the
     *     highest priority, i.e., 3. Rows of this type occur if the problem has been
     *     corrected, either automatically or by a human corrector.
     *   <li>
     *     Rows where the {@link DbColumn#SCORE SCORE} column is SQL NULL and the
     *     {@link DbColumn#ANN_TYPE ANN_TYPE} column has the value 
     *     {@link AnnType#PROBLEM_ANSWERS PROBLEM_ANSWERS}. They have the medium
     *     priority 2. Rows of this type occur if the user has edited the problem. If a row
     *     of this type occurs, a row with priority 3 (see above) may or may not occur for
     *     the same problem.
     *   </li>
     *   <li>
     *     Rows where both the {@link DbColumn#SCORE SCORE} column and the
     *     {@link DbColumn#ANN_TYPE ANN_TYPE} column are SQL NULL. They have the lowest
     *     priority 1. Rows of this type occur if the user has not edited the problem, or
     *     if the problem is edited and corrected non-electronically and the human corrector
     *     has not corrected the problem yet.
     *   </li>
     * </ul>
     */

    public final void setPriority (int priority)
    {
      this.priority = priority;
    }

    /**
     * Returns priority of the database result set row from which this record has been
     * constructed. See {@link #setPriority setPriority} for more information on
     * priorities.
     */

    public final int getPriority ()
    {
      return this.priority;
    }

    /**
     * Sets the status of the problem ({@link #NOT_EDITED NOT_EDITED},
     * {@link #NOT_CORRECTED NOT_CORRECTED}, or {@link #CORRECTED CORRECTED}).
     */

    public final void setStatus (int status)
    {
      this.status = status;
    }

    /**
     * Returns the status of the problem ({@link #NOT_EDITED NOT_EDITED},
     * {@link #NOT_CORRECTED NOT_CORRECTED}, or {@link #CORRECTED CORRECTED}).
     */

    public final int getStatus ()
    {
      return this.status;
    }

    /**
     * Sets the score.
     */

    public final void setScore (float score)
    {
      this.score = score;
    }

    /**
     * Returns the score
     */

    public final float getScore ()
    {
      return this.score;
    }

    /**
     * Sets the category.
     */

    public final void setCategory (int category)
    {
      this.category = category;
    }

    /**
     * Returns the category
     */

    public final int getCategory ()
    {
      return this.category;
    }

    /**
     * Sets the problemId.
     */

    public final void setProblemId (int problemId)
    {
      this.problemId = problemId;
    }

    /**
     * Returns the problemId
     */

    public final int getProblemId ()
    {
      return this.problemId;
    }

    /**
     * Sets the label.
     */

    public final void setLabel (String label)
    {
      this.label = label;
    }

    /**
     * Returns the label
     */

    public final String getLabel ()
    {
      return this.label;
    }

    /**
     * Sets the points.
     */

    public final void setPoints (int points)
    {
      this.points = points;
    }

    /**
     * Returns the points
     */

    public final int getPoints ()
    {
      return this.points;
    }
  }

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>UserWorksheetGradesImpl</code> instance.
   */

  public UserWorksheetGradesImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance to represent the specified user/worksheet pair.
   */

  public void setup (int userId, int worksheetId)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. userId = " + userId + ", worksheetId = " + worksheetId);
    this.userId = userId;
    this.worksheetId = worksheetId;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Resets the user id ({@link #userId userId}), worksheet id
   * ({@link #worksheetId worksheetId}), and the meta XML element writer
   * ({@link #xmlElement xmlElement}).
   */

  protected void reset ()
  {
    this.userId = Id.UNDEFINED;
    this.worksheetId = Id.UNDEFINED;
    this.xmlElement.reset();
  }

  /**
   * Recycles this instance.
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
    this.instanceStatus.notifyRecycle();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this instance.
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose";
    this.logDebug(METHOD_NAME + " 1/2: Started");
    this.reset();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // To-SAX methods
  // --------------------------------------------------------------------------------

  /**
   * Writes the grades to the specified content handler. If <code>ownDocument</code> is
   * <code>true</code>, the <code>startDocument</code> and <code>endDocument</code> methods
   * are called before resp. after the XML is created. If <code>ownDocument</code> is false,
   * they are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    final String METHOD_NAME = "toSAX";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    DbHelper dbHelper = null;
    try
      {
        // Provide db helper:
        dbHelper = (DbHelper) this.serviceManager.lookup(DbHelper.ROLE);

        // Retreive data:
        ResultSet resultSet =
          dbHelper.queryUserWorksheetGrades(this.userId, this.worksheetId);

        // Prepare record hash:
        Map<Integer,Record> records = new HashMap<Integer,Record>();

        // Create data records:
        while ( resultSet.next() )
          {
            // Get the ref id:
            int refId = resultSet.getInt(DbColumn.REF);

            // Find or create the corresponding record:
            Integer key = new Integer(refId);
            Record record = records.get(key);
            if ( record == null )
              records.put(key, (record = new Record()));

            // Find the priority of this result set row:
            // [Remark: If annotation type is AnnType.PROBLEM_CORRECTION, but no score
            //  exists, we ignore this result set row be setting its priotity to -1.
            //  This should only happen with traditional problems]
            int priority = -1;
            float score = resultSet.getFloat(DbColumn.SCORE);
            if ( resultSet.wasNull() )
              {
                int annType = resultSet.getInt(DbColumn.ANN_TYPE);
                if ( resultSet.wasNull() )
		  priority = 1;
                else if ( annType == AnnType.PROBLEM_ANSWERS )
                  priority = 2;
              }
            else
              priority = 3;

            if ( priority > record.getPriority() )
              {
                // Set priority:
                record.setPriority(priority);

                // Set status and (if necessary) score:
                switch ( priority )
                  {
                  case 1:
                    record.setStatus(Record.VOID);
                    break;
                  case 2:
                    record.setStatus(Record.ANSWERS);
                    break;
                  case 3:
                    record.setStatus(Record.CORRECTION);
                    record.setScore(score);
                    break;
                  }

                // Set remaining data:
                record.setCategory(resultSet.getInt(DbColumn.CATEGORY));
                record.setProblemId(resultSet.getInt(DbColumn.PROBLEM_ID));
                record.setLabel(resultSet.getString(DbColumn.LABEL));
                record.setPoints(resultSet.getInt(DbColumn.POINTS));
              }
          }

        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_WORKSHEET_GRADES);
	this.xmlElement.addAttribute(XMLAttribute.USER_ID, this.userId);
	this.xmlElement.addAttribute(XMLAttribute.WORKSHEET_ID, this.worksheetId);
        this.xmlElement.startToSAX(contentHandler);

        int totalPoints = 0;
        float totalResult = 0;

        // Grades in every problem:
        for (Map.Entry<Integer,Record> entry : records.entrySet())
          {
            int refId = entry.getKey().intValue();
            Record record = entry.getValue();
            int points = record.getPoints();
            totalPoints += points;
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.PROBLEM_GRADE);
            this.xmlElement.addAttribute(XMLAttribute.PROBLEM_ID, record.getProblemId());
            this.xmlElement.addAttribute(XMLAttribute.REF_ID, refId);
            this.xmlElement.addAttribute
              (XMLAttribute.CATEGORY, Category.nameFor(record.getCategory()));
            this.xmlElement.addAttribute(XMLAttribute.LABEL, record.getLabel());
            this.xmlElement.addAttribute(XMLAttribute.POINTS, points);
            switch ( record.getStatus() )
              {
              case Record.VOID:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "void");
		break;
              case Record.ANSWERS:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "answers");
		break;
              case Record.CORRECTION:
                float score = record.getScore();
		float result = (float)Math.round(score*points*10)/10;
                totalResult += result;
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "correction");
                this.xmlElement.addAttribute(XMLAttribute.SCORE, score);
                this.xmlElement.addAttribute(XMLAttribute.RESULT, result);
		break;
              }
            this.xmlElement.toSAX(contentHandler);
          }

        // Total grade:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TOTAL_GRADE);
        this.xmlElement.addAttribute(XMLElement.POINTS, totalPoints);
        this.xmlElement.addAttribute(XMLElement.RESULT, totalResult);
        this.xmlElement.toSAX(contentHandler);

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_WORKSHEET_GRADES);
        this.xmlElement.endToSAX(contentHandler);

        // Close XML document if necessary:
        if ( ownDocument ) contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("Failed to output grades for user " + userId + " and worksheet " + worksheetId +
           " as XML", exception);
      }
    finally
      {
        if ( dbHelper != null )
          this.serviceManager.release(dbHelper);
      }

    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Same as {@link #toSAX(ContentHandler,boolean) toSAX(contentHandler, true)}.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.toSAX(contentHandler, true);
  }
}
