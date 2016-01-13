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
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.grade.TutorialUserProblemGradesImpl.Grade;
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.util.LogUtil;
import net.mumie.cocoon.xml.GeneralXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link UserProblemGrades}.
 *
 * @author Marek Grudzinski <a href="mailto:grudzin@math.tu-berlin.de">grudzin@math.tu-berlin.de</a>
 * @version <code>$Id: UserProblemGradesImpl.java,v 1.10 2009/12/17 12:55:49 linges Exp $</code>
 */

public class UserProblemGradesImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, UserProblemGrades
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The id of the user.
   */

  protected int userId = Id.UNDEFINED;
  
  /**
   * The id of the course.
   */

  protected int courseId = Id.UNDEFINED;

  /**
   * The id's of problem categories.
   */
  
  protected int[] problemCategories = null;

  /**
   * The id's of worksheet categories.
   */
  
  protected int[] worksheetCategories = null;
  
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(UserProblemGradesImpl.class);

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement
    (XMLNamespace.URI_GRADES, XMLNamespace.PREFIX_GRADES);

  // --------------------------------------------------------------------------------
  // Inner classes
  // --------------------------------------------------------------------------------

  /**
   * Represents a grade. Objects of this type are the values of the grade table.
   */

  public static final class Grade
  {
    /**
     * Category of the problem
     */
   
    int category = -1;
    
    
    /**
     * Indicates that the user gave an anwer.
     */
   
    boolean edited = false;
    
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
     * The result (number of points achieved).
     */

    protected float result = -1;

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
      if(this.edited || Category.nameFor(this.category) == "traditional")
        return this.status;
      else
        return Grade.VOID;
    }

    /**
     * Sets the edited flag.
     */

    public final void setEdited (boolean edited)
    {
      this.edited = edited;
    }
    
    /**
     * Returns the edited flag.
     */

    public final boolean getEdited ()
    {
      return this.edited;
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
     * Sets the result.
     */

    public final void setResult (float result)
    {
      this.result = result;
    }

    /**
     * Returns the result
     */

    public final float getResult ()
    {
      return this.result;
    }

    /**
     * Returns the category
     */
    
    public int getCategory()
    {
      return category;
    }

    /**
     * Returns the category
     */
    
    public void setCategory(int category)
    {
      this.category = category;
    }
  }  

  /**
   * Comprises the data of a problem (id, label, category, points, and worksheet containing
   * the problem).
   */

  public static final class Problem
  {
    /**
     * The problem's id.
     */

    protected int id = -1;

    /**
     * The problem's label.
     */

    protected String label = null;

    /**
     * The problem's category.
     */

    protected int category = -1;

    /**
     * The maximum number of points for this problem.
     */

    protected int points = -1;

    /**
     * The id of the worksheet this problem is in.
     */

    protected int worksheetId = -1;

    /**
     * Returns the problem's id
     */

    public final int getId ()
    {
      return this.id;
    }

    /**
     * Returns the problem's label
     */

    public final String getLabel ()
    {
      return this.label;
    }

    /**
     * Returns the problem's category
     */

    public final int getCategory ()
    {
      return this.category;
    }

    /**
     * Returns the points
     */

    public final int getPoints ()
    {
      return this.points;
    }

    /**
     * Returns the id of the worksheet this problem is in.
     */

    public final int getWorksheetId ()
    {
      return this.worksheetId;
    }

    /**
     * Creates a new instance with the specified data.
     */

    public Problem (int id, String label, int category, int points, int worksheetId)
    {
      this.id = id;
      this.label = label;
      this.category =  category;
      this.points = points;
      this.worksheetId = worksheetId;
    }
  }

  /**
   * Comprises the data of a worksheet (label, category).
   */

  public static final class Worksheet
  {
    /**
     * The worksheet's label.
     */

    protected String label = null;

    /**
     * The worksheet's category.
     */

    protected int category = -1;

    /**
     * Returns the worksheet's label
     */

    public final String getLabel ()
    {
      return this.label;
    }

    /**
     * Returns the worksheet's category
     */

    public final int getCategory ()
    {
      return this.category;
    }

    /**
     * Creates a new instance with the specified data.
     */

    public Worksheet (String label, int category)
    {
      this.label = label;
      this.category = category;
    }
  }

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>UserProblemGradesImpl</code> instance.
   */

  public UserProblemGradesImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance.
   * @param userId id of the user
   * @param courseId id of the course
   * @param problemCategories problem categories. If null, all categories are taken into
   *   account.
   * @param worksheetCategories worksheet categories. If null, all categories are taken into
   *   account.
   */

  public void setup (int userId,
                     int courseId,
                     int[] problemCategories,
                     int[] worksheetCategories)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started." +
       " userId = " + userId +
       ", courseId = " + courseId +
       ", problemCategories = " + LogUtil.arrayToString(problemCategories) +
       ", worksheetCategories = " + LogUtil.arrayToString(worksheetCategories));
    this.userId = userId;
    this.courseId = courseId;
    this.problemCategories = problemCategories;
    this.worksheetCategories = worksheetCategories;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }


  /**
   * Resets the user id ({@link #userId userId}) 
   * and the meta XML element writer ({@link #xmlElement xmlElement}).
   */

  protected void reset ()
  {
    this.userId = Id.UNDEFINED;    
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

        // Query user name:
        ResultSet userData = dbHelper.queryUserData
          (this.userId, new String[]{DbColumn.LOGIN_NAME, DbColumn.FIRST_NAME, DbColumn.SURNAME});
        if ( !userData.next() )
          throw new IllegalArgumentException("Can not find user with id: " + this.userId);
        String userLoginname = userData.getString(DbColumn.LOGIN_NAME);
        String userFirstname = userData.getString(DbColumn.FIRST_NAME);
        String userSurname = userData.getString(DbColumn.SURNAME);
        
        // Query course name:
        ResultSet courseData = dbHelper.queryDatum
          (DocType.COURSE, this.courseId, DbColumn.NAME);
        if ( !courseData.next() )
          throw new IllegalArgumentException("Can not find course with id: " + this.courseId);
        String courseName = courseData.getString(DbColumn.NAME);

        // Init tables  for grades, problems, and worksheets:
        Map<Integer,Grade> grades = new HashMap<Integer,Grade>();        
        Map<Integer,Problem> problems = new HashMap<Integer,Problem>();
        Map<Integer,Worksheet> worksheets = new HashMap<Integer,Worksheet>();

        // Retreive data:
        ResultSet resultSet =
          dbHelper.queryUserProblemGrades(this.userId, courseId);
      
        // Process data:
        while ( resultSet.next() )
          {
            // Some variables needed: ref id, worksheet id, points:
            int refId = resultSet.getInt(DbColumn.PROBLEM_REF_ID);
            int worksheetId = resultSet.getInt(DbColumn.WORKSHEET_ID);
            int points = resultSet.getInt(DbColumn.POINTS);
            
            // Find or create the corresponding grade:
            Integer gradeKey = new Integer(refId);
            Grade grade = grades.get(gradeKey);
            if ( grade == null ) grades.put(gradeKey, (grade = new Grade()));
            
            grade.setCategory(resultSet.getInt(DbColumn.PROBLEM_CATEGORY_ID));
            
            // Find the priority of this result set row:
            // [Remark: If annotation type is AnnType.PROBLEM_CORRECTION, but no score
            //  exists, we ignore this result set row be setting its priotity to -1.
            //  This should only happen with traditional problems]
            float score = resultSet.getFloat(DbColumn.SCORE);
            int priority = -1;
            if ( resultSet.wasNull() )
              {
                int annType = resultSet.getInt(DbColumn.ANN_TYPE);
                if ( resultSet.wasNull() )
                    priority = 1;
                else if ( annType == AnnType.PROBLEM_ANSWERS )
                {  
                  priority = 2;
                  grade.setEdited(true);
                }
              }
            else
              priority = 3;

            if ( priority > grade.getPriority() )
              {
                // Set priority:
                grade.setPriority(priority);

                // Set status and (if necessary) score and result:
                switch ( priority )
                  {
                  case 1:
                    grade.setStatus(Grade.VOID);
                    break;
                  case 2:
                    grade.setStatus(Grade.ANSWERS);
                    break;
                  case 3:
                    grade.setStatus(Grade.CORRECTION);
                    
                    float result = (float)Math.round(score*points*10)/10;
                    grade.setScore(score);
                    grade.setResult(result);
                    break;
                  }
              }
            
            // Add problem to problem table if necessary:
            Integer problemKey = new Integer(refId);
            if ( !problems.containsKey(problemKey) )
              {
                Problem problem = new Problem
                  (resultSet.getInt(DbColumn.PROBLEM_ID),
                   resultSet.getString(DbColumn.PROBLEM_LABEL),
                   resultSet.getInt(DbColumn.PROBLEM_CATEGORY_ID),
                   points,
                   worksheetId);
                problems.put(problemKey, problem);
              }

            // Add worksheet to worksheet table if necessary:
            Integer worksheetKey = new Integer(worksheetId);
            if ( !worksheets.containsKey(worksheetKey) )
              {
                Worksheet worksheet = new Worksheet
                  (resultSet.getString(DbColumn.WORKSHEET_LABEL),
                   resultSet.getInt(DbColumn.WORKSHEET_CATEGORY_ID));
                worksheets.put(worksheetKey, worksheet);
              }
          }

        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_GRADES);
        this.xmlElement.addAttribute(XMLAttribute.USER_ID, this.userId);        
        this.xmlElement.addAttribute(XMLAttribute.COURSE_NAME, courseName);
        this.xmlElement.addAttribute(XMLAttribute.COURSE_ID, this.courseId);
        this.xmlElement.startToSAX(contentHandler);

        // Grades:
        for (Map.Entry<Integer,Grade> entry : grades.entrySet())
          {
            int refId = entry.getKey().intValue();
            Grade grade = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_GRADE);
            this.xmlElement.addAttribute(XMLAttribute.USER_ID, this.userId);
            this.xmlElement.addAttribute(XMLAttribute.REF_ID, refId);
            switch ( grade.getStatus() )
              {
              case Grade.VOID:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "void");
		break;
              case Grade.ANSWERS:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "answers");
		break;
              case Grade.CORRECTION:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "correction");
                this.xmlElement.addAttribute(XMLAttribute.SCORE, grade.getScore());
                this.xmlElement.addAttribute(XMLAttribute.RESULT, grade.getResult());
		break;
              }
            this.xmlElement.toSAX(contentHandler);
          }
  
        // User:        	
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER);
        this.xmlElement.addAttribute(XMLAttribute.ID, this.userId);          
        this.xmlElement.addAttribute(XMLAttribute.FIRST_NAME, userFirstname);
        this.xmlElement.addAttribute(XMLAttribute.SURNAME, userSurname);
        this.xmlElement.toSAX(contentHandler);
        
  
        // Problems:
        for (Map.Entry<Integer,Problem> entry : problems.entrySet())
          {
            int refId = entry.getKey().intValue();
            Problem problem = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.PROBLEM);
            this.xmlElement.addAttribute(XMLAttribute.ID, problem.getId());
            this.xmlElement.addAttribute(XMLAttribute.REF_ID, refId);
            this.xmlElement.addAttribute(XMLAttribute.LABEL, problem.getLabel());
            this.xmlElement.addAttribute
              (XMLAttribute.CATEGORY, Category.nameFor(problem.getCategory()));
            this.xmlElement.addAttribute(XMLAttribute.POINTS, problem.getPoints());
            this.xmlElement.addAttribute(XMLAttribute.WORKSHEET_ID, problem.getWorksheetId());
            this.xmlElement.toSAX(contentHandler);
          }
  
        // Worksheet:
        for (Map.Entry<Integer,Worksheet> entry : worksheets.entrySet())
          {
            int id = entry.getKey().intValue();
            Worksheet worksheet = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.WORKSHEET);
            this.xmlElement.addAttribute(XMLAttribute.ID, id);
            this.xmlElement.addAttribute(XMLAttribute.LABEL, worksheet.getLabel());
            this.xmlElement.addAttribute
              (XMLAttribute.CATEGORY, Category.nameFor(worksheet.getCategory()));
            this.xmlElement.toSAX(contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_GRADES);
        this.xmlElement.endToSAX(contentHandler);

        // Close XML document if necessary:
        if ( ownDocument ) contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("Failed to output grades for user " + this.userId + " and course " + courseId +
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
  
  //--------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns a string containing the names for the specified categories, seperated by
   * <code>", "</code>.
   */

  protected static final String getCategoryNames (int[] categories)
  {
    if ( categories == null )
      return "*";
    StringBuilder buffer = new StringBuilder();
    String sep = "";
    for (int category : categories)
      {
        buffer.append(sep).append(Category.nameFor(category));
        sep = ", ";
      }
    return buffer.toString();
  }
  
}
