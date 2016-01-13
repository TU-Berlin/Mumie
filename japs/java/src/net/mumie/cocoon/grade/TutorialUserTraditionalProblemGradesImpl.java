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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
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
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Request;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link TutorialUserProblemGrades}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: TutorialUserTraditionalProblemGradesImpl.java,v 1.5 2009/11/17 16:02:00 linges Exp $</code>
 */

public class TutorialUserTraditionalProblemGradesImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, TutorialUserTraditionalProblemGrades
{
  // --------------------------------------------------------------------------------
  // h1: Inner classes
  // --------------------------------------------------------------------------------

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: UserRefIdPair
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
     * only if, it is a {@link UserRefIdPair TutorialUserTraditionalProblemGradesImpl.UserRefIdPair}
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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Grade
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Represents a grade. Objects of this type are the values of the grade table.
   */

  public static final class Grade
  {
    /**
     * The score.
     */

    protected float score = -1;

    /**
     * The result (number of points achieved).
     */

    protected float result = -1;

    /**
     * The input for this grade. This means the following: If the human corrector
     * entered something in the from field for this grade, the input is contained in this
     * variable. If the corrector did not entered someting yet, the variable is null.
     */

    protected String input = null;

    /**
     * Error message if the last input could not be written to the database. Otherwise
     * null.
     */

    protected String error = null;

    /**
     * Returns the score
     */

    public final float getScore ()
    {
      return this.score;
    }

    /**
     * Returns the result
     */

    public final float getResult ()
    {
      return this.result;
    }

    /**
     * Returns the input for this grade. This means the following: If the human corrector
     * entered something in the from field for this grade, the input is returned by this
     * method. If the corrector did not entered someting yet, the method returns null.
     */

    public final String getInput ()
    {
      return this.input;
    }

    /**
     * Returns the error message if the last input could not be written to the
     * database. Otherwise null.
     */

    public final String getError ()
    {
      return this.error;
    }

    /**
     * Sets the score
     */

    public void setScore (float score)
    {
      this.score = score;
    }

    /**
     * Sets the result
     */

    public void setResult (float result)
    {
      this.result = result;
    }

    /**
     * Sets the last input
     */

    public void setInput (String input)
    {
      this.input = input;
    }

    /**
     * Sets the error message
     */

    public void setError (String error)
    {
      this.error = error;
    }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: User
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  /**
   * Comprises the data of a user (first name and surname).
   */

  public static final class User
  {
    /**
     * The user's first name.
     */

    protected String firstName = null;

    /**
     * The user's surname.
     */

    protected String surname = null;

    /**
     * Returns the user's first name.
     */

    public String getFirstName ()
    {
      return this.firstName;
    }

    /**
     * Returns the user's surname.
     */

    public String getSurname ()
    {
      return this.surname;
    }

    /**
     * Creates a new instance with the specified first name and surname.
     */

    public User (String firstName, String surname)
    {
      this.firstName = firstName;
      this.surname = surname;
    }
  }

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Problem
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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

  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // h2: Worksheet
  // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
  // h1: Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(TutorialUserTraditionalProblemGradesImpl.class);

  /**
   * Common prefix of all request parameters containing grades
   */

  public static final String REQ_PARAM_PREFIX = "grade";

  /**
   * The id of the tutorial.
   */

  protected int tutorialId;

  /**
   * The name of the tutorial.
   */

  protected String tutorialName;

  /**
   * The id of the course.
   */

  protected int courseId;

  /**
   * The name of the course.
   */

  protected String courseName;

  /**
   * The name of the class.
   */

  protected String className;

  /**
   * The if course has a class.
   */

  private boolean courseHasClass;

  /**
   * The name of the course.
   */

  protected int classId;


  /**
   * The db helper of his instance
   */

  protected DbHelper dbHelper = null;

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement
    (XMLNamespace.URI_GRADES, XMLNamespace.PREFIX_GRADES);

  /**
   * Regular expression to parse request parameters
   */

  protected Pattern reqParamPattern = Pattern.compile
    ("^" + REQ_PARAM_PREFIX + "\\.([0-9]+)\\.([0-9]+)$");

  /**
   * The grades table
   */

  protected Map<UserRefIdPair,Grade> grades;

  /**
   * The users table
   */

  protected Map<Integer,User> users;

  /**
   * The problems table
   */

  protected Map<Integer,Problem> problems;

  /**
   * The worksheet table
   */

  protected Map<Integer,Worksheet> worksheets;

  // --------------------------------------------------------------------------------
  // Ensure/release methods for the services
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
   * Resets this instance.
   */

  protected void reset ()
  {
    this.tutorialId = Id.UNDEFINED;
    this.courseId = Id.UNDEFINED;
    this.tutorialName = null;
    this.courseName = null;
    this.xmlElement.reset();
    this.grades = new HashMap<UserRefIdPair,Grade>();
    this.users = new HashMap<Integer,User>();
    this.problems = new HashMap<Integer,Problem>();
    this.worksheets = new HashMap<Integer,Worksheet>();
  }

  /**
   * Creates a new <code>TutorialUserTraditionalProblemGradesImpl</code> instance.
   */

  public TutorialUserTraditionalProblemGradesImpl ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
    this.reset();
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
  // h1: Loading the grades from the database
  // --------------------------------------------------------------------------------

  /**
   * Loads the data for the specified tutorial and course
   */

  public void load (int tutorialId, int courseId)
    throws ProcessingException, ServiceException, SQLException
  {
    final String METHOD_NAME = "load";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. tutorialId = " + tutorialId + ", courseId = " + courseId);
    this.reset();
    this.tutorialId = tutorialId;
    this.courseId = courseId;

    this.ensureDbHelper();

    // Query tutorial name:
    this.tutorialName = this.dbHelper.getPseudoDocDatumAsString
      (PseudoDocType.TUTORIAL, this.tutorialId, DbColumn.NAME);

    // Query course and class name:
    ResultSet courseData = dbHelper.queryData
      (DocType.COURSE, this.courseId, new String[] {DbColumn.NAME, DbColumn.CLASS});
    if ( !courseData.next() )
      throw new SQLException("Could not find course with id " + this.courseId);
    String courseName = courseData.getString(DbColumn.NAME);

    this.classId = courseData.getInt(DbColumn.CLASS);
    this.courseHasClass = !courseData.wasNull();
    this.className =
      (courseHasClass
        ? dbHelper.getPseudoDocDatumAsString(PseudoDocType.CLASS, classId, DbColumn.NAME)
        : null);

    // Retrieve data:
    ResultSet resultSet = this.dbHelper.queryTutorialUserTraditionalProblemGrades
      (this.tutorialId, this.courseId);

    // Process data:
    while ( resultSet.next() )
      {
        // Some values needed:
        int userId = resultSet.getInt(DbColumn.USER_ID);
        int worksheetId = resultSet.getInt(DbColumn.WORKSHEET_ID);
        int refId = resultSet.getInt(DbColumn.PROBLEM_REF_ID);
        int points = resultSet.getInt(DbColumn.POINTS);
        float score = resultSet.getFloat(DbColumn.SCORE);
        boolean scoreExists = !resultSet.wasNull();

        // Add grade to grade table:
        UserRefIdPair gradeKey = new UserRefIdPair(userId, refId);
        Grade grade = new Grade();
        if ( scoreExists )
          {
            grade.setScore(score);
            grade.setResult((float)Math.round(score*points*10)/10);
          }
        this.grades.put(gradeKey, grade);
    
        // Add user to user table if necessary:
        Integer userKey = new Integer(userId);
        if ( !this.users.containsKey(userKey) )
          {
            User user = new User
              (resultSet.getString(DbColumn.USER_FIRST_NAME),
               resultSet.getString(DbColumn.USER_SURNAME));
            this.users.put(userKey, user);
          }

        // Add problem to problem table if necessary:
        Integer problemKey = new Integer(refId);
        if ( !this.problems.containsKey(problemKey) )
          {
            Problem problem = new Problem
              (resultSet.getInt(DbColumn.PROBLEM_ID),
               resultSet.getString(DbColumn.PROBLEM_LABEL),
               resultSet.getInt(DbColumn.PROBLEM_CATEGORY_ID),
               points,
               worksheetId);
            this.problems.put(problemKey, problem);
          }

        // Add worksheet to worksheet table if necessary:
        Integer worksheetKey = new Integer(worksheetId);
        if ( !this.worksheets.containsKey(worksheetKey) )
          {
            Worksheet worksheet = new Worksheet
              (resultSet.getString(DbColumn.WORKSHEET_LABEL),
               resultSet.getInt(DbColumn.WORKSHEET_CATEGORY_ID));
            this.worksheets.put(worksheetKey, worksheet);
          }
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Adding form data
  // --------------------------------------------------------------------------------

  /**
   * Adds the form data found in the specified request to the grades.
   */

  public void addFormData (Request request)
    throws ProcessingException
  {
    final String METHOD_NAME = "addFormData";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    Enumeration paramNames = request.getParameterNames();
    while ( paramNames.hasMoreElements() )
      {
        // Get parameter name and value:
        String paramName = (String)paramNames.nextElement();
        String paramValue = request.getParameter(paramName);

        // Skip this parameter if it does not start with the required prefix:
        if ( !paramName.startsWith(REQ_PARAM_PREFIX + ".") ) continue;

        // Skip this parameter if its value is void:
        if ( paramValue.trim().length() == 0 ) continue;

        // Parse the parameter name:
        Matcher matcher = this.reqParamPattern.matcher(paramName);
        if ( !matcher.matches() )
          throw new ProcessingException("Illegal request parameter name: " + paramName);

        // Get user, ref, worksheet, problem ids from parse result:
        int userId = Integer.parseInt(matcher.group(1));
        int refId = Integer.parseInt(matcher.group(2));

        // Get grade and problem objects:
        Grade grade = this.grades.get(new UserRefIdPair(userId, refId));
        Problem problem = this.problems.get(new Integer(refId));

        // Maximum number of points:
        int maxPoints = problem.getPoints();

        // Get result:
        float result = -1;
        String error = null;
        try
          {
            result = Float.parseFloat(paramValue);
            if ( result < 0 )
              error = "Input is negative";
            else if ( result > maxPoints )
              error = "Input exceeds maximum number of points";
          }
        catch (NumberFormatException exception)
          {
            error = "Input is not a number";
          }

        // Enter result in grade object
        grade.setInput(paramValue);
        if ( error == null )
          {
            grade.setResult(result);
            grade.setScore(result/maxPoints);
          }
        else
          grade.setError(error);
      }
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  // --------------------------------------------------------------------------------
  // h1: Storing the grades in the database
  // --------------------------------------------------------------------------------

  /**
   * Stores the grades in the database.
   */

  public void store ()
    throws ServiceException, SQLException
  {
    final String METHOD_NAME = "store";
    this.logDebug(METHOD_NAME + " 1/2: Started");

    this.ensureDbHelper();

    for (Map.Entry<UserRefIdPair,Grade> entry : this.grades.entrySet())
      {
        UserRefIdPair gradeKey = entry.getKey();
        Grade grade = entry.getValue();

        int userId = gradeKey.getUserId();
        int refId = gradeKey.getRefId();
        String input = grade.getInput();
        float score = grade.getScore();
        String error = grade.getError();

        if ( input != null && error == null && score != -1)
          this.dbHelper.storeProblemGrade(userId, refId, score);
      }

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
        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TUTORIAL_USER_PROBLEM_GRADES);
	      this.xmlElement.addAttribute(XMLAttribute.TUTORIAL_ID, this.tutorialId);
	      this.xmlElement.addAttribute(XMLAttribute.TUTORIAL_NAME, this.tutorialName);
	      this.xmlElement.addAttribute(XMLAttribute.COURSE_NAME, this.courseName);
	      this.xmlElement.addAttribute(XMLAttribute.COURSE_ID, this.courseId);
        this.xmlElement.addAttribute(XMLAttribute.COURSE_ID, this.courseId);
            if ( courseHasClass )
              {
                this.xmlElement.addAttribute(XMLAttribute.CLASS_NAME, className);
                this.xmlElement.addAttribute(XMLAttribute.CLASS_ID, classId);
              }
            this.xmlElement.addAttribute(XMLAttribute.CURRENT_TIME, System.currentTimeMillis());

        this.xmlElement.startToSAX(contentHandler);

        // Grades:
        for (Map.Entry<UserRefIdPair,Grade> entry : this.grades.entrySet())
          {
            UserRefIdPair gradeKey = entry.getKey();
            int userId = gradeKey.getUserId();
            int refId = gradeKey.getRefId();
            Grade grade = entry.getValue();
            String input = grade.getInput();
            float score = grade.getScore();
            float result = grade.getResult();
            String error = grade.getError();
            
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER_PROBLEM_GRADE);
            this.xmlElement.addAttribute(XMLAttribute.USER_ID, userId);
            this.xmlElement.addAttribute(XMLAttribute.REF_ID, refId);

            if ( score != -1 )
              {
                this.xmlElement.addAttribute(XMLAttribute.SCORE, score);
                this.xmlElement.addAttribute(XMLAttribute.RESULT, result);
              }

            if ( input != null )
              this.xmlElement.addAttribute(XMLAttribute.INPUT, input);

            if ( error != null )
              this.xmlElement.addAttribute(XMLAttribute.ERROR, error);

            this.xmlElement.toSAX(contentHandler);
          }
  
        // Users:
        for (Map.Entry<Integer,User> entry : this.users.entrySet())
          {
            int id = entry.getKey().intValue();
            User user = entry.getValue();
            this.xmlElement.reset();
            this.xmlElement.setLocalName(XMLElement.USER);
            this.xmlElement.addAttribute(XMLAttribute.ID, id);
            this.xmlElement.addAttribute(XMLAttribute.FIRST_NAME, user.getFirstName());
            this.xmlElement.addAttribute(XMLAttribute.SURNAME, user.getSurname());
            this.xmlElement.toSAX(contentHandler);
          }
  
        // Problems:
        for (Map.Entry<Integer,Problem> entry : this.problems.entrySet())
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
        for (Map.Entry<Integer,Worksheet> entry : this.worksheets.entrySet())
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
        this.xmlElement.setLocalName(XMLElement.TUTORIAL_USER_PROBLEM_GRADES);
        this.xmlElement.endToSAX(contentHandler);

        // Close XML document if necessary:
        if ( ownDocument ) contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("Failed to output grades for tutorial " + tutorialId + " and course " + courseId +
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
