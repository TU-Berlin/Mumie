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
import net.mumie.cocoon.notions.AnnType;
import net.mumie.cocoon.notions.DbColumn;
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
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Default implementation of {@link TotalClassGrades}.
 *
 * @author Marek Grudzinski <a href="mailto:grudzin@math.tu-berlin.de">grudzin@math.tu-berlin.de</a>
 * @version <code>$Id: TotalClassGradesImpl.java,v 1.6 2008/01/24 16:29:22 rassy Exp $</code>
 */

public class TotalClassGradesImpl extends AbstractJapsServiceable
  implements Recyclable, Disposable, TotalClassGrades
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The syncId of the class.
   */

  protected String syncId = "";

  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(TotalClassGradesImpl.class);

  /**
   * Helper tho write Meta XML elements to SAX.
   */

  protected GeneralXMLElement xmlElement = new GeneralXMLElement
    (XMLNamespace.URI_GRADES, XMLNamespace.PREFIX_GRADES);

  // --------------------------------------------------------------------------------
  // Inner classes
  // --------------------------------------------------------------------------------
  
  /**
   * Comprises the data of a user.
   */

  public static final class User
  {
    /**
     * The user's syncId.
     */

    protected String syncId = null;
        
    /**
     * Sum of edited problems
     */
    
    protected int editedProblems = 0;
    
    /**
     * Sum of corrected problems
     */
    
    protected int correctedProblems = 0;

    /**
     * Sum of points for all corrected problems 
     */
    protected double totalPoints = 0;
    
    /**
     * Returns the user's first name.
     */

    public String getSyncId ()
    {
      return this.syncId;
    }

    /**
     * Get sum of edited problems.
     */
    
    public int getEditedProblems()
    {
    	return this.editedProblems;
    }
    
    /**
     *  Increase sum of edited problem with 1.
     */
    
    public void increaseEditedProblems()
    {
    	this.editedProblems += 1;
    }
    
    /**
     *  Get sum of corrected problems.
     */
    
    public int getCorrectedProblems()
    {
    	return this.correctedProblems;
    }
    
    /**
     * Increase sum of corrected problems with 1. 
     */
    
    public void increaseCorrectedProblems()
    {
    	this.correctedProblems += 1;
    }
    
    /**
     * Get sum of all points the user has. 
     */
    
    public double getTotalPoints()
    {
    	return this.totalPoints;
    }
    
    /**
     * Increase sum of all points with the given value.
     */
    
    public void addTotalPoints(double value)
    {
    	this.totalPoints += value;
    }
    
    /**
     * Creates a new instance with the specified first name and surname.
     */

    public User (String syncId)
    {
      this.syncId = syncId;      
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
   * Comprises the data of a course (id, name, totalProblems)
   */

  public static final class Course
  {
  	/**
  	 * The course's id
  	 */
  	
  	protected int id = -1;
  	
  	/**
  	 * The course's name
  	 */
  	
  	protected String name = null;
  	
  	/**
  	 * Sum of all problems in this course
  	 */
  	
  	protected int totalProblems = 0;
  	
  	/**
  	 * Sum of all points for all problems in this course
  	 */
  	
  	protected int totalPoints = 0;
  	
  	/**
  	 * Get the course id.
  	 */
  	
  	public int getId() {
			return id;
		}
		
  	/**
  	 * Get the course name.
  	 */

  	public String getName() {
			return name;
		}

  	/**
  	 *  Get sum of problems. 
  	 */

		public int getTotalProblems() {
			return totalProblems;
		}

  	/**
  	 * Get sum of points. 
  	 */

		public int getTotalPoints() {
			return totalPoints;
		}
		
  	/**
  	 * Increase sum of problems with 1. 
  	 */

		public void increaseTotalProblems()
		{
			this.totalProblems += 1;
		}
		
  	/**
  	 * Increase sum of points with the given points. 
  	 */

		public void increaseTotalPoints(int points)
		{
			this.totalPoints += points;
		}
  	
  	/**
     * Creates a new instance with the specified data.
     */
  	
  	public Course (int id, String name)
  	{
  		this.id = id;
  		this.name = name;
  	}		
  }

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>TotalClassGradesImpl</code> instance.
   */

  public TotalClassGradesImpl()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Initializes this instance to represent the specified class.
   */

  public void setup (String syncId)
  {
    final String METHOD_NAME = "setup";
    this.logDebug
      (METHOD_NAME + " 1/2: Started. syncId = " + syncId);    
    this.syncId = syncId;
    this.logDebug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Resets the sync id ({@link #syncId syncId})
   * and the meta XML element writer
   * ({@link #xmlElement xmlElement}).
   */

  protected void reset ()
  {    
    this.syncId = null;
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
        
        //Query classId by given syncId
        int classId = dbHelper.getIdForSyncId(PseudoDocType.CLASS, syncId);
        String className = dbHelper.getPseudoDocDatumAsString(PseudoDocType.CLASS, classId, DbColumn.NAME);
                        
        // Query the data of all courses for this class:
        Map<Integer,Course> courses = new HashMap<Integer,Course>();
        ResultSet resultSet = dbHelper.queryClassesAndCourses();
        while ( resultSet.next() )
        	{
        		//Some variables needed: docType, id, classId
        		String docType = resultSet.getString(DbColumn.DOC_TYPE);
        		int localClassId = resultSet.getInt(DbColumn.CLASS);
        		int courseId = resultSet.getInt(DbColumn.ID);
        		String courseName = resultSet.getString(DbColumn.NAME);        		
        		// Add course to table if necessary:
        		if ( docType.equals("course") && localClassId == classId && !courses.containsKey(courseId))
        			{	
        				Course course = new Course(courseId, courseName);
        				courses.put(courseId, course);        				
        			}
        	}
        resultSet = null;
        
        // Start XML document if necessary:
        if ( ownDocument ) contentHandler.startDocument();

        // Start root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TOTAL_CLASS_GRADES);        
        this.xmlElement.addAttribute(XMLAttribute.CLASS_NAME, className);
        this.xmlElement.addAttribute(XMLAttribute.CLASS_ID, classId);        
        this.xmlElement.startToSAX(contentHandler);       
        
        // Process all courses in this class
        for (Map.Entry<Integer, Course> entry : courses.entrySet())
        	{
        		Course course = entry.getValue();        		
        		
		        // Init tables  for grades, users, problems, and worksheets:        
		        Map<Integer,User> users = new HashMap<Integer,User>();
		        Map<Integer,Problem> problems = new HashMap<Integer,Problem>();        
		
		        // Retreive data:
		        resultSet = dbHelper.queryTotalCourseGrades(course.getId());
		
		        // Process data:
		        while ( resultSet.next() )
		          {        	
		        		// Some variables needed: user id, ref id, worksheet id, points:
		          	int userId = resultSet.getInt(DbColumn.USER_ID);
		          	int refId = resultSet.getInt(DbColumn.PROBLEM_REF_ID);
		          	int worksheetId = resultSet.getInt(DbColumn.WORKSHEET_ID);
		          	int points = resultSet.getInt(DbColumn.POINTS);
		          
		        		// Add user to user table if necessary:
		          	User user;
		          	Integer userKey = new Integer(userId);          	
		          	if ( !users.containsKey(userKey) )
			          	{
			          		user = new User(resultSet.getString(DbColumn.SYNC_ID));
			              users.put(userKey, user);
			            } 
		          	else 
			          	{
			          		user = users.get(userKey);
			          	}
		
		            // Find the priority of this result set row:
		            float score = resultSet.getFloat(DbColumn.SCORE);
		            int priority = -1;
		            if ( resultSet.wasNull() )
		              {
		                int annType = resultSet.getInt(DbColumn.ANN_TYPE);
		                if ( resultSet.wasNull() )
		                	priority = 1;
		                else if ( annType == AnnType.PROBLEM_ANSWERS )
		                  priority = 2;
		                else
		                  throw new IllegalStateException
		                    ("Problem refId " + refId + ": No score and ann type is " + annType);
		              }
		            else
		              priority = 3;
		
		            if ( priority > -1 )
		              {
		            	 // Increase edited and/or corrected problems and total points of a user if necessary:
		                switch ( priority )
		                  {
		                  case 1:
		                    //do nothing;
		                    break;
		                  case 2:
		                    user.increaseEditedProblems();
		                    break;
		                  case 3:                    
		                    user.increaseCorrectedProblems();
		                    double result = (double)Math.round(score*points*10)/10;
		                    user.addTotalPoints(result);
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
		                //update course data
		                course.increaseTotalProblems();
		                course.increaseTotalPoints(points);
		              }            
		          }		        
		
		        // Users:
		        for (Map.Entry<Integer, User> userEntry : users.entrySet())
		          {  
		        		Integer userId = userEntry.getKey();
		        		User user = userEntry.getValue();
		        	
		            this.xmlElement.reset();
		            this.xmlElement.setLocalName(XMLElement.USER_COURSE_GRADE);
		            this.xmlElement.addAttribute(XMLAttribute.USER_ID, userId);
		            this.xmlElement.addAttribute(XMLAttribute.SYNC_ID, user.getSyncId());		            
		            this.xmlElement.addAttribute(XMLAttribute.POINTS, Double.toString(user.getTotalPoints()));
		            this.xmlElement.addAttribute(XMLAttribute.EDITED_PROBLEMS, user.getEditedProblems());
		            this.xmlElement.addAttribute(XMLAttribute.CORRECTED_PROBLEMS, user.getCorrectedProblems());
		            this.xmlElement.addAttribute(XMLAttribute.COURSE_ID, course.getId());
		            this.xmlElement.toSAX(contentHandler);
		          }
        		}
		        
        	//Courses
		      for (Map.Entry<Integer, Course> courseEntry : courses.entrySet())  
		      	{
		      		Course course = courseEntry.getValue();
		      		// Course element:
		        	this.xmlElement.reset();
		        	this.xmlElement.setLocalName(XMLElement.COURSE);        
		        	this.xmlElement.addAttribute(XMLAttribute.COURSE_NAME, course.getName());
		        	this.xmlElement.addAttribute(XMLAttribute.COURSE_ID, course.getId());
		        	this.xmlElement.addAttribute(XMLAttribute.TOTAL_PROBLEMS, course.getTotalProblems());
		        	this.xmlElement.addAttribute(XMLAttribute.TOTAL_POINTS, course.getTotalPoints());
		        	this.xmlElement.toSAX(contentHandler);
		      	}
        
        // Close root element:
        this.xmlElement.reset();
        this.xmlElement.setLocalName(XMLElement.TOTAL_CLASS_GRADES);
        this.xmlElement.endToSAX(contentHandler);

        // Close XML document if necessary:
        if ( ownDocument ) contentHandler.endDocument();
      }
    catch (Exception exception)
      {
        throw new SAXException
          ("Failed to output grades for class with syncId: " + this.syncId +
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
