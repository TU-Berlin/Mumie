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
import net.mumie.cocoon.grade.UserProblemGradesImpl.Grade;

import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.service.AbstractJapsServiceable;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.xml.GeneralXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.service.ServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Represents the total grades per problem for each user and worksheet in a course.
 * 
 * @version <code>$Id: ClassUserProblemGradesImpl.java,v 1.3 2009/12/17 12:55:49 linges Exp $</code>
 */

public class ClassUserProblemGradesImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, ClassUserProblemGrades
{
  // --------------------------------------------------------------------------------
  // h1: Inner class: UserRefIdPair
  // --------------------------------------------------------------------------------

  /**
   * Represents a pair, consisting of a user id and e ref id. Objects of this type are the
   * keys of the grade table.
   */

  public static final class UserRefIdPair
  {
    /**
     * The user id.
     */

    protected int userId;

    /**
     * The ref id (id of a worksheet/generic-problem id)
     */

    protected int refId;

    /**
     * Returns the user id.
     */

    public int getUserId ()
    {
      return this.userId;
    }

    /**
     * Returns the ref id.
     */

    public int getRefId ()
    {
      return this.refId;
    }

    /**
     * Returns true if the specified object is equal to this one. This is the case if, and
     * only if, it is a {@link UserRefIdPair ClassUserProblemGradesImpl.UserRefIdPair}
     * instance and its user and ref id equal the corresponding values of this instance.
     */

    public boolean equals (Object object)
    {
      if ( object instanceof UserRefIdPair )
        {
          UserRefIdPair pair = (UserRefIdPair)object;
          return ( this.userId == pair.getUserId() && this.refId == pair.getRefId() );
        }
      else
        return false;
    }

    /**
     * Returns a hash code for this instance.
     */
  
    public int hashCode ()
    {
      return
        (this.userId <= 32767 && this.refId <= 32767
         ? (this.userId << 15) + this.refId
         : this.userId);
    }

    /**
     * Creates a new instance with the specified ids.
     */

    public UserRefIdPair (int userId, int refId)
    {
      this.userId = userId;
      this.refId = refId;
    }

    /**
     * Returns a string representation of this instance.
     */

    public String toString ()
    {
      return "UserRefIdPair(" + userId + "," + refId + ")";
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: UserWorksheetPair
  // --------------------------------------------------------------------------------

  /**
   * Represents a user/worksheet pair.
   */

  public static final class UserWorksheetPair
  {
    /**
     * Id of the user.
     */

    protected int userId;

    /**
     * Id of the worksheet.
     */

    protected int worksheetId;

    /**
     * Creates a new instance with the specified user and worksheet ids.
     */

    public UserWorksheetPair (int userId, int worksheetId)
    {
      this.userId = userId;
      this.worksheetId = worksheetId;
    }

    /**
     * Returns the user id.
     */

    public final int getUserId ()
    {
      return this.userId;
    }

    /**
     * Returns the worksheet id.
     */

    public final int getWorksheetId ()
    {
      return this.worksheetId;
    }

    /**
     * Returns true if the specified object is a
     * <code>ClassUserProblemGradesImpl.UserWorksheetPair</code> which coincides with this
     * instance in the user and worksheet id.
     */

    public boolean equals (Object object)
    {
      if ( object instanceof UserWorksheetPair )
        {
          UserWorksheetPair pair = (UserWorksheetPair)object;
          return ( pair.getUserId() == this.userId && pair.getWorksheetId() == this.worksheetId );
        }
      else
        return false;
    }

    /**
     * Returns a hash code for this instance.
     */
  
    public int hashCode ()
    {
      return
        (this.userId <= 32767 && this.worksheetId <= 32767
         ? (this.userId << 15) + this.worksheetId
         : this.userId);
    }

    /**
     * Returns a string representation of this instance.
     */

    public String toString ()
    {
      return "UserWorksheetPair(" + userId + "," + worksheetId + ")";
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: User
  // --------------------------------------------------------------------------------

  /**
   * Comprises first and surename of a user.
   */

  public static final class User
  {
    /**
     * The first name of the user.
     */

    protected String firstName;

    /**
     * The surname of the user.
     */

    protected String surname;

    /**
     * The sync id of the user, or null if the user has no sync id.
     */

    protected String syncId;

    /**
     * Returns the first name.
     */

    public final String getFirstName ()
    {
      return this.firstName;
    }

    /**
     * Returns the surname.
     */

    public final String getSurname ()
    {
      return this.surname;
    }

    /**
     * Returns the sync id of the user, or null if the user has no sync id.
     */

    public final String getSyncId ()
    {
      return this.syncId;
    }

    /**
     * Creates a new instance.
     */

    public User (String firstName, String surname, String syncId)
    {
      this.firstName = firstName;
      this.surname = surname;
      this.syncId = syncId;
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: Problem
  // --------------------------------------------------------------------------------

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

  // --------------------------------------------------------------------------------
  // h1: Inner class: Worksheet
  // --------------------------------------------------------------------------------

  /**
   * Comprises worksheet data.
   */

  public static final class Worksheet
  {
    /**
     * The category
     */

    protected int category;

    /**
     * The id of the course
     */

    protected int courseId;

    /**
     * The label
     */

    protected String label;

    /**
     * Maximum points.
     */

    protected int points;

    /**
     * Number of problems
     */

    protected int numProblems;

    /**
     * Returns the category.
     */

    public int getCategory ()
    {
      return this.category;
    }

    /**
     * Returns the id of the course
     */

    public int getCourseId ()
    {
      return this.courseId;
    }

    /**
     * Returns the label
     */

    public String getLabel ()
    {
      return this.label;
    }

    /**
     * Returns the maximum number of points.
     */

    public int getPoints ()
    {
      return this.points;
    }

    /**
     * Returns the number of problems.
     */

    public int getNumProblems ()
    {
      return this.numProblems;
    }

    /**
     * Creates a new instance with the specified category, course, and label. The number of
     * points and the number of problems are set to 0.
     */

    public Worksheet (int category, int courseId, String label)
    {
      this.category = category;
      this.courseId = courseId;
      this.label = label;
      this.points = 0;
      this.numProblems = 0;
    }

    /**
     * Increases the number of points by the specified value.
     */

    public final void incrPoints (int increment)
    {
      this.points += increment;
    }

    /**
     * Increases the number of problems by the specified value.
     */

    public final void incrNumProblems (int increment)
    {
      this.numProblems += increment;
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: Course
  // --------------------------------------------------------------------------------

  /**
   * Comprises data of a course
   */

  public static final class Course
  {
    /**
     * The name of the course.
     */

    protected String name;

    /**
     * The id of the vc thread of the course.
     */

    protected int vcThreadId;

    /**
     * Returns the name of the course,.
     */

    public String getName ()
    {
      return this.name;
    }

    /**
     * Returns the id of the vc thread of the course.
     */

    public int getVCThreadId ()
    {
      return this.vcThreadId;
    }

    /**
     * Creates a new instance with the specified data.
     */

    public Course (String name, int vcThreadId)
    {
      this.name = name;
      this.vcThreadId = vcThreadId;
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: ProblemGrade
  // --------------------------------------------------------------------------------

  /**
   * Represents a grade of a student in a problem.
   */

  public static final class ProblemGrade
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

  // --------------------------------------------------------------------------------
  // h1: Inner class: WorksheetGrade
  // --------------------------------------------------------------------------------

  /**
   * Represents the grade of a student in a worksheet.
   */

  public static final class WorksheetGrade
  {
    /**
     * Achieved number of points
     */

    protected double result;

    /**
     * Number of edited problems
     */

    protected int numEdited;

    /**
     * Number of corrected problems
     */

    protected int numCorrected;

    /**
     * Returns the achieved number of points.
     */

    public double getResult ()
    {
      return this.result;
    }

    /**
     * Returns the number of edited problems.
     */

    public int getNumEdited ()
    {
      return this.numEdited;
    }

    /**
     * Returns the number of corrected problems.
     */

    public int getNumCorrected ()
    {
      return this.numCorrected;
    }

    /**
     * Creates a new instance with all variables set to 0.
     */

    public WorksheetGrade ()
    {
      this.result = 0;
      this.numEdited = 0;
      this.numCorrected = 0;
    }

    /**
     * Increases the result (achieved number of points) by the specified value.
     */

    public final void incrResult (double increment)
    {
      this.result += increment;
    }

    /**
     * Increases the number of edited problems by the specified value.
     */

    public final void incrNumEdited (int increment)
    {
      this.numEdited += increment;
    }

    /**
     * Increases the number of corrected problems by the specified value.
     */

    public final void incrNumCorrected (int increment)
    {
      this.numCorrected += increment;
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Inner class: Data
  // --------------------------------------------------------------------------------

  /**
   * Comprises all data.
   */

  public static final class Data
  {
    /**
     * The class name.
     */

    protected String className = null;

    /**
     * Maps user/problem pairs to problem grades. The problems are specified by their
     * worksheet/generic-problem reference ids.
     */

    protected Map<UserRefIdPair,ProblemGrade> problemGrades =
      new HashMap<UserRefIdPair,ProblemGrade>();

    /**
     * Maps user/worksheet pairs to worksheet grades.
     */

    protected Map<UserWorksheetPair,WorksheetGrade> worksheetGrades =
      new HashMap<UserWorksheetPair,WorksheetGrade>();

    /**
     * Maps user id's to {@link #User User} objects.
     */

    protected Map<Integer,User> users = new HashMap<Integer,User>();

    /**
     * Maps problem id's to {@link #Problem Problem} objects.
     */

    protected Map<Integer,Problem> problems = new HashMap<Integer,Problem>();

    /**
     * Maps worksheet id's to {@link #Worksheet Worksheet} objects.
     */

    protected Map<Integer,Worksheet> worksheets = new HashMap<Integer,Worksheet>();

    /**
     * Maps course id's to {@link #Course Course} objects.
     */

    protected Map<Integer,Course> courses = new HashMap<Integer,Course>();

    /**
     * Returns the class name.
     */

    public String className ()
    {
      return this.className;
    }

    /**
     * Sets the class name.
     */

    public void setClassName (String className)
    {
      this.className = className;
    }

    /**
     * Returns the map that associates user/problem pairs to problem grades
     * ({@link #problemGrades problemGrades}).
     */

    public final Map<UserRefIdPair,ProblemGrade> problemGrades ()
    {
      return this.problemGrades;
    }

    /**
     * Returns the map that associates user/worksheet pairs to worksheet grades
     * ({@link #worksheetGrades worksheetGrades}).
     */

    public final Map<UserWorksheetPair,WorksheetGrade> worksheetGrades ()
    {
      return this.worksheetGrades;
    }

    /**
     * Returns the map that associates user id's to {@link #User User} objects.
     */

    public final Map<Integer,User> users ()
    {
      return this.users;
    }

    /**
     * Returns the map that associates problem id's to {@link #Problem Problem}
     * objects.
     */

    public final Map<Integer,Problem> problems ()
    {
      return this.problems;
    }

    /**
     * Returns the map that associates worksheet id's to {@link #Worksheet Worksheet}
     * objects.
     */

    public final Map<Integer,Worksheet> worksheets ()
    {
      return this.worksheets;
    }

    /**
     * Returns the map that associates course id's to {@link #Course Course}
     * objects.
     */

    public final Map<Integer,Course> courses ()
    {
      return this.courses;
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The id of the class.
   */

  protected int classId = Id.UNDEFINED;

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(ClassUserProblemGradesImpl.class);

  /**
   * The db helper of this instance.
   */

  protected DbHelper dbHelper = null;

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement
    (XMLNamespace.URI_GRADES, XMLNamespace.PREFIX_GRADES);

  // --------------------------------------------------------------------------------
  // h1: "Ensure" methods for the services
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

  // --------------------------------------------------------------------------------
  // h1: Releasing services
  // --------------------------------------------------------------------------------

  /**
   * Releases all services. Since the db helper is the only service needed by this class,
   * only the db helper is released.
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
    this.logDebug(METHOD_NAME + " 1/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>ClassUserProblemGradesImpl</code> instance.
   */

  public ClassUserProblemGradesImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance to represent the specified class.
   */

  public void setup (int classId)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. classId = " + classId);
    this.classId = classId;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Initializes this instance to represent the class with the specified sync id.
   */

  public void setup (String syncId)
    throws ServiceException, SQLException
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. syncId = " + syncId);
    this.ensureDbHelper();
    this.classId = this.dbHelper.getPseudoDocDatumBySyncIdAsInt
      (PseudoDocType.CLASS, syncId, DbColumn.ID);
    this.logDebug(METHOD_NAME + " 2/2: Done. this.classId = " + this.classId);
  }

  /**
   * Resets the class id ({@link #classId classId}) and the meta XML element writer
   * ({@link #xmlElement xmlElement}).
   */

  protected void reset ()
  {
    this.classId = Id.UNDEFINED;
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
    this.releaseServices();
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
    this.releaseServices();
    this.instanceStatus.notifyDispose();
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Retrieving the data
  // --------------------------------------------------------------------------------

  /**
   * Retrieves the data from the database.
   */

  protected Data getData ()
    throws Exception
  {
    Data data = new Data();

    this.ensureDbHelper();

    // Get class name:
    String className = dbHelper.getPseudoDocDatumAsString
      (PseudoDocType.CLASS, this.classId, DbColumn.NAME);
    data.setClassName(className);

    // Retrieve data:
    ResultSet resultSet = this.dbHelper.queryGradesForClass(this.classId);

    // Process data:
    while ( resultSet.next() )
      {
        // Some variables needed: user id, ref id, worksheet id, course id, points:
        int userId = resultSet.getInt(DbColumn.USER_ID);
        int refId = resultSet.getInt(DbColumn.PROBLEM_REF_ID);
        int worksheetId = resultSet.getInt(DbColumn.WORKSHEET_ID);
        int courseId = resultSet.getInt(DbColumn.COURSE_ID);
        int points = resultSet.getInt(DbColumn.POINTS);

        // Find or create the corresponding problem grade:
        UserRefIdPair problemGradeKey = new UserRefIdPair(userId, refId);
        ProblemGrade problemGrade = data.problemGrades().get(problemGradeKey);
        if ( problemGrade == null )
          data.problemGrades().put(problemGradeKey, (problemGrade = new ProblemGrade()));

        problemGrade.setCategory(resultSet.getInt(DbColumn.PROBLEM_CATEGORY_ID));
        
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
              problemGrade.setEdited(true);
            }
          }
        else
          priority = 3;

        if ( priority > problemGrade.getPriority() )
          {
            // Set priority:
            problemGrade.setPriority(priority);

            // Set status and (if necessary) score and result:
            switch ( priority )
              {
              case 1:
                problemGrade.setStatus(ProblemGrade.VOID);
                break;
              case 2:
                problemGrade.setStatus(ProblemGrade.ANSWERS);
                break;
              case 3:
                problemGrade.setStatus(ProblemGrade.CORRECTION);
                float result = (float)Math.round(score*points*10)/10;
                problemGrade.setScore(score);
                problemGrade.setResult(result);
                break;
              }
          }
            
        // Add user to user table if necessary:
        Integer userKey = new Integer(userId);
        if ( !data.users().containsKey(userKey) )
          {
            User user = new User
              (resultSet.getString(DbColumn.USER_FIRST_NAME),
               resultSet.getString(DbColumn.USER_SURNAME),
               resultSet.getString(DbColumn.USER_SYNC_ID));
            data.users().put(userKey, user);
          }

        // Add problem to problem table if necessary:
        Integer problemKey = new Integer(refId);
        if ( !data.problems().containsKey(problemKey) )
          {
            Problem problem = new Problem
              (resultSet.getInt(DbColumn.PROBLEM_ID),
               resultSet.getString(DbColumn.PROBLEM_LABEL),
               resultSet.getInt(DbColumn.PROBLEM_CATEGORY_ID),
               points,
               worksheetId);
            data.problems().put(problemKey, problem);
          }

        // Add worksheet to worksheet table if necessary:
        Integer worksheetKey = new Integer(worksheetId);
        if ( !data.worksheets().containsKey(worksheetKey) )
          {
            Worksheet worksheet = new Worksheet
              (resultSet.getInt(DbColumn.WORKSHEET_CATEGORY_ID),
               courseId,
               resultSet.getString(DbColumn.WORKSHEET_LABEL));
            data.worksheets().put(worksheetKey, worksheet);
          }

        // Add course to course table if necessary:
        Integer courseKey = new Integer(courseId);
        if ( !data.courses().containsKey(courseKey) )
          {
            Course course = new Course
              (resultSet.getString(DbColumn.COURSE_NAME),
               resultSet.getInt(DbColumn.COURSE_VC_THREAD_ID));
            data.courses().put(courseKey, course);
          }
      }

    // Worksheets: get total points and number of problems:
    for (Problem problem : data.problems().values())
      {
        Worksheet worksheet = data.worksheets().get(new Integer(problem.getWorksheetId()));
        worksheet.incrNumProblems(1);
        worksheet.incrPoints(problem.getPoints());
      }

    // Get grades per worksheet:
    for (Map.Entry<UserRefIdPair,ProblemGrade> problemGradeEntry
           : data.problemGrades().entrySet())
      {
        UserRefIdPair problemGradeKey = problemGradeEntry.getKey();
        ProblemGrade problemGrade = problemGradeEntry.getValue();
        int userId = problemGradeKey.getUserId();
        int refId = problemGradeKey.getRefId();
        int worksheetId = data.problems().get(refId).getWorksheetId();
        UserWorksheetPair worksheetGradeKey = new UserWorksheetPair(userId, worksheetId);

        // Find or create worksheet grade:
        WorksheetGrade worksheetGrade = data.worksheetGrades().get(worksheetGradeKey);
        if ( worksheetGrade == null )
          data.worksheetGrades().put(worksheetGradeKey, (worksheetGrade = new WorksheetGrade()));

        switch ( problemGrade.getStatus() )
          {
          case ProblemGrade.CORRECTION:
            worksheetGrade.incrNumEdited(1);
            worksheetGrade.incrNumCorrected(1);
            worksheetGrade.incrResult(problemGrade.getResult());
            break;
          case ProblemGrade.ANSWERS:
            worksheetGrade.incrNumEdited(1);
            break;
          }
      }

    return data;
  }

  // --------------------------------------------------------------------------------
  // h1: To-SAX methods
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
    try
      {
        // Get data:
        Data data = this.getData();

        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASS_USER_PROBLEM_GRADES);
        this.xmlElement.addAttribute(XMLAttribute.CLASS_ID, this.classId);
        this.xmlElement.addAttribute(XMLAttribute.CLASS_NAME, data.className());
        this.xmlElement.startToSAX(contentHandler);

        // Grades:
        for (Map.Entry<UserRefIdPair,ProblemGrade> entry : data.problemGrades().entrySet())
          {
            UserRefIdPair gradeKey = entry.getKey();
            int userId = gradeKey.getUserId();
            int refId = gradeKey.getRefId();
            ProblemGrade grade = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_GRADE);
            this.xmlElement.addAttribute(XMLAttribute.USER_ID, userId);
            this.xmlElement.addAttribute(XMLAttribute.REF_ID, refId);
            switch ( grade.getStatus() )
              {
              case ProblemGrade.VOID:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "void");
                break;
              case ProblemGrade.ANSWERS:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "answers");
                break;
              case ProblemGrade.CORRECTION:
                this.xmlElement.addAttribute(XMLAttribute.STATUS, "correction");
                this.xmlElement.addAttribute(XMLAttribute.SCORE, grade.getScore());
                this.xmlElement.addAttribute(XMLAttribute.RESULT, grade.getResult());
                break;
              }
            this.xmlElement.toSAX(contentHandler);
          }
  
        
        
        // Users:
        for (Map.Entry<Integer,User> entry : data.users().entrySet())
          {
            int id = entry.getKey().intValue();
            User user = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER);
            this.xmlElement.addAttribute(XMLAttribute.ID, id);
            this.xmlElement.addAttribute(XMLAttribute.FIRST_NAME, user.getFirstName());
            this.xmlElement.addAttribute(XMLAttribute.SURNAME, user.getSurname());
            String syncId = user.getSyncId();
            if ( syncId != null )
              this.xmlElement.addAttribute(XMLAttribute.SYNC_ID, syncId);
            this.xmlElement.toSAX(contentHandler);
          }
        
        // Problems:
        for (Map.Entry<Integer,Problem> entry : data.problems().entrySet())
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
        
        // Worksheets:
        for (Map.Entry<Integer,Worksheet> entry : data.worksheets().entrySet())
          {
            int id = entry.getKey().intValue();
            Worksheet worksheet = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.WORKSHEET);
            this.xmlElement.addAttribute(XMLAttribute.ID, id);
            this.xmlElement.addAttribute(XMLAttribute.LABEL, worksheet.getLabel());
            this.xmlElement.addAttribute
              (XMLAttribute.CATEGORY, Category.nameFor(worksheet.getCategory()));
            this.xmlElement.addAttribute(XMLAttribute.COURSE_ID, worksheet.getCourseId());
            this.xmlElement.addAttribute(XMLAttribute.PROBLEMS, worksheet.getNumProblems());
            this.xmlElement.addAttribute(XMLAttribute.POINTS, worksheet.getPoints());
            this.xmlElement.toSAX(contentHandler);
          }
  
        // Courses:
        for (Map.Entry<Integer,Course> entry : data.courses().entrySet())
          {
            int id = entry.getKey().intValue();
            Course course = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.COURSE);
            this.xmlElement.addAttribute(XMLAttribute.ID, id);
            this.xmlElement.addAttribute(XMLAttribute.NAME, course.getName());
            this.xmlElement.addAttribute(XMLAttribute.VC_THREAD_ID, course.getVCThreadId());
            this.xmlElement.toSAX(contentHandler);
          }

        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.CLASS_USER_PROBLEM_GRADES);
        this.xmlElement.endToSAX(contentHandler);

        // Close XML document if necessary:
        if ( ownDocument ) contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("Failed to output grades for class " + this.classId, exception);
      }
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