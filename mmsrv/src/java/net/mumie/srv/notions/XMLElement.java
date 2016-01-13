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

package net.mumie.srv.notions;

public class XMLElement
{
  /**
   * Returns the XML element name of the specified data entity type. Same as
   * {@link EntityType#xmlElementOf EntityType.xmlElementOf(type)}.
   */

  public static final String ofEntityType (int type)
  {
    return EntityType.xmlElementOf(type);
  }

  // ================================================================================
  // Autocoded methods start
  // ================================================================================

  
  /**
   * Container for the authors of a document (metainfo XML)
   */

  public static final String AUTHORS = "authors";

  /**
   * Category (metainfo XML)
   */

  public static final String CATEGORY = "category";

  /**
   * History of changes (metainfo XML)
   */

  public static final String CHANGELOG = "changelog";

  /**
   * A Language code (metainfo XML)
   */

  public static final String CODE = "code";

  /**
   * Container for components (metainfo XML)
   */

  public static final String COMPONENTS = "components";

  /**
   * Including section (metainfo XML)
   */

  public static final String CONTAINED_IN = "contained_in";

  /**
   * Container for the (pseudo-)documents a section contains (metainfo XML, sections only)
   */

  public static final String CONTAINS = "contains";

  /**
   * Content of a document (metainfo XML)
   */

  public static final String CONTENT = "content";

  /**
   * Content length (metainfo XML)
   */

  public static final String CONTENT_LENGTH = "content_length";

  /**
   * Content type (metainfo XML)
   */

  public static final String CONTENT_TYPE = "content_type";

  /**
   * Copyright note (metainfo XML)
   */

  public static final String COPYRIGHT = "copyright";

  /**
   * Corrector (metainfo XML)
   */

  public static final String CORRECTOR = "corrector";

  /**
   * Create permissions (metainfo XML)
   */

  public static final String CREATE_PERMISSIONS = "create_permissions";

  /**
   * Creation time (metainfo XML)
   */

  public static final String CREATED = "created";

  /**
   * Whether this (pseudo-)document is deleted (metainfo XML)
   */

  public static final String DELETED = "deleted";

  /**
   * Description (metainfo XML)
   */

  public static final String DESCRIPTION = "description";

  /**
   * Duration (metainfo XML)
   */

  public static final String DURATION = "duration";

  /**
   * E-mail address (metainfo XML, only with users)
   */

  public static final String EMAIL = "email";

  /**
   * First name (metainfo XML, only with users)
   */

  public static final String FIRST_NAME = "first_name";

  /**
   * Container for GDIM entries (metainfo XML)
   */

  public static final String GDIM_ENTRIES = "gdim_entries";

  /**
   * GDIM entry (metainfo XML)
   */

  public static final String GDIM_ENTRY = "gdim_entry";

  /**
   * Height (metainfo XML)
   */

  public static final String HEIGHT = "height";

  /**
   * Whether this (pseudo-)document is hidden (metainfo XML)
   */

  public static final String HIDE = "hide";

  /**
   * Page providing information about a document (metainfo XML)
   */

  public static final String INFO_PAGE = "info_page";

  /**
   * Whether this document is a wrapper (metainfo XML)
   */

  public static final String IS_WRAPPER = "is_wrapper";

  /**
   * Label (metainfo XML)
   */

  public static final String LABEL = "label";

  /**
   * Language of a user (metainfo XML)
   */

  public static final String LANGUAGE = "language";

  /**
   * Last login time of a user (metainfo XML)
   */

  public static final String LAST_LOGIN = "last_login";

  /**
   * Last modification time (metainfo XML)
   */

  public static final String LAST_MODIFIED = "last_modified";

  /**
   * Container for the lecturers of a class (metainfo XML, only with classes)
   */

  public static final String LECTURERS = "lecturers";

  /**
   * Container for links (metainfo XML)
   */

  public static final String LINKS = "links";

  /**
   * Login name (metainfo XML, only with users)
   */

  public static final String LOGIN_NAME = "login_name";

  /**
   * Name (metainfo XML)
   */

  public static final String NAME = "name";

  /**
   * Container for members (metainfo XML)
   */

  public static final String MEMBERS = "members";

  /**
   * Maximum points for a problem (metainfo XML, only with users)
   */

  public static final String PASSWORD = "password";

  /**
   * Maximum points for a problem (metainfo XML)
   */

  public static final String POINTS = "points";

  /**
   * Pure name (metainfo XML)
   */

  public static final String PURE_NAME = "pure_name";

  /**
   * Qualified name (metainfo XML)
   */

  public static final String QUALIFIED_NAME = "qualified_name";

  /**
   * Read permissions (metainfo XML)
   */

  public static final String READ_PERMISSIONS = "read_permissions";

  /**
   * Semester reference (metainfo XML, only with classes)
   */

  public static final String SEMESTER = "semester";

  /**
   * Summary (metainfo XML)
   */

  public static final String SUMMARY = "summary";

  /**
   * Surname (metainfo XML, only with users)
   */

  public static final String SURNAME = "surname";

  /**
   * Synchronisation home section of a user (metainfo XML, only with users)
   */

  public static final String SYNC_HOME = "sync_home";

  /**
   * Synchronization id (metainfo XML)
   */

  public static final String SYNC_ID = "sync_id";

  /**
   * Theme of a user (metainfo XML)
   */

  public static final String THEME = "theme";

  /**
   * Thumbnail (small preview image) (metainfo XML)
   */

  public static final String THUMBNAIL = "thumbnail";

  /**
   * Timeframe end (metainfo XML)
   */

  public static final String TIMEFRAME_END = "timeframe_end";

  /**
   * Timeframe start (metainfo XML)
   */

  public static final String TIMEFRAME_START = "timeframe_start";

  /**
   * Tutor (metainfo XML, only with tutorials)
   */

  public static final String TUTOR = "tutor";

  /**
   * Container for tutorials (metainfo XML, classes only)
   */

  public static final String TUTORIALS = "tutorials";

  /**
   * Container for user groups (metainfo XML, only with users)
   */

  public static final String USER_GROUPS = "user_groups";

  /**
   * Vc thread (metainfo XML)
   */

  public static final String VC_THREAD = "vc_thread";

  /**
   * Version (metainfo XML)
   */

  public static final String VERSION = "version";

  /**
   * Width (metainfo XML)
   */

  public static final String WIDTH = "width";

  /**
   * Write permissions (metainfo XML)
   */

  public static final String WRITE_PERMISSIONS = "write_permissions";

  /**
   * Last modification time of problem answers (metainfo XML)
   */

  public static final String ANSWERS_LAST_MODIFIED = "answers_last_modified";

  /**
   * Root elmement of a list of tutorials of a class (metainfo XML)
   */

  public static final String CLASS_TUTORIALS = "class_tutorials";

  /**
   * Root elmement of a list of all classes and courses (metainfo XML)
   */

  public static final String CLASSES_AND_COURSES = "classes_and_courses";

  /**
   * Root elmement of a checkin report (metainfo XML)
   */

  public static final String CHECKIN_REPORT = "checkin_report";

  /**
   * Last modification time of problem common data (metainfo XML)
   */

  public static final String COMMON_DATA_LAST_MODIFIED = "common_data_last_modified";

  /**
   * Last modification time of problem correction (metainfo XML)
   */

  public static final String CORRECTION_LAST_MODIFIED = "correction_last_modified";

  /**
   * List of correction reports (metainfo XML)
   */

  public static final String CORRECTION_REPORTS = "correction_reports";

  /**
   * A correction report (metainfo XML)
   */

  public static final String CORRECTION_REPORT = "correction_report";

  /**
   * A document type (metainfo XML)
   */

  public static final String DOCUMENT_TYPE = "document_type";

  /**
   * A pseudo-document type (metainfo XML)
   */

  public static final String PSEUDO_DOCUMENT_TYPE = "pseudo_document_type";

  /**
   * Container for a list of documents (metainfo XML)
   */

  public static final String DOCUMENTS = "documents";

  /**
   * Container for additional data (metainfo XML)
   */

  public static final String DYNAMIC_DATA = "dynamic_data";

  /**
   * An error message (metainfo XML)
   */

  public static final String ERROR = "error";

  /**
   * Container for a list of pseudo-documents (metainfo XML)
   */

  public static final String PSEUDO_DOCUMENTS = "pseudo_documents";

  /**
   * Origin of a reference (metainfo XML)
   */

  public static final String ORIGIN = "origin";

  /**
   * Target of a reference (metainfo XML)
   */

  public static final String TARGET = "target";

  /**
   * Data of a certain problem/user pair (metainfo XML)
   */

  public static final String USER_PROBLEM_DATA = "user_problem_data";

  /**
   * Copies a data item (PPD XML)
   */

  public static final String COPY = "copy";

  /**
   * Generates a random integer (PPD XML)
   */

  public static final String RANDOM_INTEGER = "random_integer";

  /**
   * Generates a random rational number (PPD XML)
   */

  public static final String RANDOM_RATIONAL = "random_rational";

  /**
   * Generates a random real number (PPD XML)
   */

  public static final String RANDOM_REAL = "random_real";

  /**
   * Randomly selects from several options (PPD XML)
   */

  public static final String RANDOM_SELECT = "random_select";

  /**
   * Grades per worksheet for all users of a class (grades XML)
   */

  public static final String CLASS_USER_WORKSHEET_GRADES = "class_user_worksheet_grades";

  /**
   * A problem (grades XML; an element with the same name occurs in the metainfo XML)
   */

  public static final String PROBLEM = "problem";

  /**
   * Grades per course for all users of a class (grades XML)
   */

  public static final String TOTAL_CLASS_GRADES = "total_class_grades";

  /**
   * Grades per problem for all users in a given tutorial (grades XML)
   */

  public static final String TUTORIAL_USER_PROBLEM_GRADES = "tutorial_user_problem_grades";

  /**
   * A user (grades XML; an element with the same name occurs in the metainfo XML)
   */

  public static final String USER = "user";

  /**
   * Grade of a user in a course (grades XML)
   */

  public static final String USER_COURSE_GRADE = "user_course_grade";

  /**
   * Grade of a user in a problem (grades XML)
   */

  public static final String USER_PROBLEM_GRADE = "user_problem_grade";

  /**
   * Grades per problem for a given user and course (grades XML)
   */

  public static final String USER_PROBLEM_GRADES = "user_problem_grades";

  /**
   * Grades per problem of a user in a given worksheet (grades XML)
   */

  public static final String USER_WORKSHEET_GRADES = "user_worksheet_grades";

  /**
   * Grade of a user in a worksheet (grades XML)
   */

  public static final String USER_WORKSHEET_GRADE = "user_worksheet_grade";

  /**
   * Data of a worksheet (grades XML)
   */

  public static final String WORKSHEET = "worksheet";

  /**
   * Grades per problem for a given user and worksheet (grades XML)
   */

  public static final String WORKSHEET_USER_PROBLEM_GRADES = "worksheet_user_problem_grades";

  /**
   * Root elmement of a list of all courses and tutorials for a given class
   * (data record XML)
   */

  public static final String COURSES_AND_TUTORIALS = "courses_and_tutorials";

  /**
   * Root elmement of a class members list (data record XML)
   */

  public static final String CLASS_MEMBERS = "class_members";

  /**
   * Choices in a multiple choice problem (problem XML)
   */

  public static final String CHOICES = "choices";

  /**
   * One of several parts from which some are randomly choosen (problem XML)
   */

  public static final String OPTION = "option";

  /**
   * Container for additional data (added-data XML)
   */

  public static final String ADDED_DATA = "added_data";

  /**
   * Generic data container (added-data XML)
   */

  public static final String STORE = "store";

  /**
   * A parameter (added-data XML)
   */

  public static final String PARAM = "param";

  /**
   * The time of the dispose of a service instance (status XML)
   */

  public static final String DISPOSED = "disposed";

  /**
   * Status of a single instance of a class (status namespace)
   */

  public static final String INSTANCE_STATUS = "instance_status";

  /**
   * Container for a list of instance statuses (status namespace)
   */

  public static final String INSTANCE_STATUSES = "instance_statuses";

  /**
   * The time of the first start of a service instance (status XML)
   */

  public static final String FIRST_STARTED = "first_started";

  /**
   * The time of the last lookup of a service instance (status XML)
   */

  public static final String LAST_LOOKUP = "last_lookup";

  /**
   * The time of the last recycle of a service instance (status XML)
   */

  public static final String LAST_RECYCLED = "last_recycled";

  /**
   * The time of the last release of a service instance (status XML)
   */

  public static final String LAST_RELEASE = "last_release";

  /**
   * The time of the last recycle of a service instance (status XML)
   */

  public static final String LAST_STOPPED = "last_stopped";

  /**
   * The lifecycle status of a service instance (status namespace)
   */

  public static final String LIFECYCLE = "lifecycle";

  /**
   * indicates that status information are not available (status XML)
   */

  public static final String NOT_AVAILABLE = "not_available";

  /**
   * Number of active instances of a class (status XML)
   */

  public static final String NUMBER_OF_ACTIVE_INSTANCES = "number_of_active_instances";

  /**
   * Number of created instances of a class (status XML)
   */

  public static final String NUMBER_OF_CREATED_INSTANCES = "number_of_created_instances";

  /**
   * Number of disposed instances of a class (status XML)
   */

  public static final String NUMBER_OF_DISPOSED_INSTANCES = "number_of_disposed_instances";

  /**
   * Number of lookedup instances of a class (status XML)
   */

  public static final String NUMBER_OF_LOOKEDUP_INSTANCES = "number_of_lookedup_instances";

  /**
   * The number of lookups of a service instance (status namespace)
   */

  public static final String NUMBER_OF_LOOKUPS = "number_of_lookups";

  /**
   * Number of recycled instances of a class (status XML)
   */

  public static final String NUMBER_OF_RECYCLED_INSTANCES = "number_of_recycled_instances";

  /**
   * The number of recycles of a service instance (status namespace)
   */

  public static final String NUMBER_OF_RECYCLES = "number_of_recycles";

  /**
   * The number of releases of a service instance (status namespace)
   */

  public static final String NUMBER_OF_RELEASES = "number_of_releases";

  /**
   * The number of stops of a service instance (status namespace)
   */

  public static final String NUMBER_OF_STOPS = "number_of_stops";

  /**
   * Number of running instances of a class (status XML)
   */

  public static final String NUMBER_OF_RUNNING_INSTANCES = "number_of_running_instances";

  /**
   * The number of starts of a service instance (status namespace)
   */

  public static final String NUMBER_OF_STARTS = "number_of_starts";

  /**
   * Number of used instances of a class (status XML)
   */

  public static final String NUMBER_OF_USED_INSTANCES = "number_of_used_instances";

  /**
   * The component using a given service (status XML); user owning a session (session XML)
   */

  public static final String OWNER = "owner";

  /**
   * The class implementing a service (status XML)
   */

  public static final String SERVICE_CLASS = "service_class";

  /**
   * The status of a service (status XML)
   */

  public static final String SERVICE_STATUS = "service_status";

  /**
   * Overview of the statuses of services (status XML)
   */

  public static final String SERVICE_STATUS_OVERVIEW = "service_status_overview";

  /**
   * Time of the last access to a session (session XML)
   */

  public static final String LAST_ACCESSED = "last_accessed";

  /**
   * A list of sessions (session XML)
   */

  public static final String SESSIONS = "sessions";

  /**
   * A session (session XML)
   */

  public static final String SESSION = "session";


  // ================================================================================
  // Autocoded methods end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private XMLElement ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("XMLElement must not be instanciated");
  }
}