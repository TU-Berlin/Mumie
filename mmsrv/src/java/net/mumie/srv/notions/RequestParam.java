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

public class RequestParam
{
  // ================================================================================
  // Autocoded start
  // ================================================================================

  
  /**
   * A synchronization id
   */

  public static final String SYNC_ID = "sync-id";

  /**
   * A filename body
   */

  public static final String PURE_NAME = "pure-name";

  /**
   * A section id
   */

  public static final String CONTAINED_IN = "contained-in";

  /**
   * The login name of a user
   */

  public static final String LOGIN_NAME = "login-name";

  /**
   * The encrypted password of a user
   */

  public static final String PASSWORD_ENCRYPTED = "password-encrypted";

  /**
   * The first name of a user
   */

  public static final String FIRST_NAME = "first-name";

  /**
   * The surname of a user
   */

  public static final String SURNAME = "surname";

  /**
   * The matriculation number of a student
   */

  public static final String MATR_NUMBER = "matr-number";

  /**
   * A name; may be used in various circumstances
   */

  public static final String NAME = "name";

  /**
   * A description, mostly of a (pseudo-)document
   */

  public static final String DESCRIPTION = "description";

  /**
   * A semester
   */

  public static final String SEMESTER = "semester";

  /**
   * List of lecturers of a class. Used with synchronisation.
   */

  public static final String LECTURERS = "lecturers";

  /**
   * A tutor. Used with synchronisation.
   */

  public static final String TUTOR = "tutor";

  /**
   * An e-learning class. Used with synchronisation.
   */

  public static final String CLASS = "class";

  /**
   * A user
   */

  public static final String USER = "user";

  /**
   * An e-learning tutorial. Used with synchronisation.
   */

  public static final String TUTORIAL = "tutorial";

  /**
   * The old tutorial when a user changes her or his tutorial.
   */

  public static final String OLD_TUTORIAL = "old-tutorial";

  /**
   * The new tutorial when a user changes her or his tutorial.
   */

  public static final String NEW_TUTORIAL = "new-tutorial";

  /**
   * The pure name of a section
   */

  public static final String SECTION_PURE_NAME = "section-pure-name";

  /**
   * List of courses
   */

  public static final String COURSES = "courses";

  /**
   * Id of the vc thread of a course
   */

  public static final String COURSE_VC_THREAD = "course-vc-thread";


  // ================================================================================
  // Autocoded end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private RequestParam ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("RequestParam must not be instanciated");
  }
}