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

public class XMLAttribute
{
  // ================================================================================
  // Autocoded methods start
  // ================================================================================

  
  /**
   * An id (metainfo XML)
   */

  public static final String ID = "id";

  /**
   * A Language path (metainfo XML)
   */

  public static final String LANG = "lang";

  /**
   * A Language code (metainfo XML)
   */

  public static final String LANG_CODE = "lang_code";

  /**
   * The local id (metainfo XML)
   */

  public static final String LID = "lid";

  /**
   * A time format (metainfo XML)
   */

  public static final String FORMAT = "format";

  /**
   * Path of a generic document (metainfo XML)
   */

  public static final String GENERIC_DOC_PATH = "generic_doc_path";

  /**
   * The name of something (metainfo XML)
   */

  public static final String NAME = "name";

  /**
   * A (pseudo-)document path (metainfo XML); a path in a datasheet (PPD XML)
   */

  public static final String PATH = "path";

  /**
   * The raw value of something (metainfo XML)
   */

  public static final String RAW = "raw";

  /**
   * The id of a reference (metainfo XML)
   */

  public static final String REF_ID = "ref_id";

  /**
   * Minor part of a media type (metainfo XML)
   */

  public static final String SUBTYPE = "subtype";

  /**
   * Path of a theme (metainfo XML)
   */

  public static final String THEME_PATH = "theme_path";

  /**
   * The type of something (metainfo XML). For example, the major part of the media
   * type is specified like this.
   */

  public static final String TYPE = "type";

  /**
   * The value of something (metainfo XML)
   */

  public static final String VALUE = "value";

  /**
   * The id of an user (metainfo XML; grades XML)
   */

  public static final String USER_ID = "user_id";

  /**
   * Maximum value of the denominator (PPD XML)
   */

  public static final String DENOMINATOR_MAX = "denominator_max";

  /**
   * Minimum value of the denominator (PPD XML)
   */

  public static final String DENOMINATOR_MIN = "denominator_min";

  /**
   * Maximum value (PPD XML)
   */

  public static final String MAX = "max";

  /**
   * Minimum value (PPD XML)
   */

  public static final String MIN = "min";

  /**
   * Maximum value of the numerator (PPD XML)
   */

  public static final String NUMERATOR_MAX = "numerator_max";

  /**
   * Minimum value of the numerator (PPD XML)
   */

  public static final String NUMERATOR_MIN = "numerator_min";

  /**
   * Whether a number should be non-zero (PPD XML)
   */

  public static final String NON_ZERO = "non_zero";

  /**
   * Whether a fraction should be reduced (PPD XML)
   */

  public static final String REDUCE = "reduce";

  /**
   * A category name (grades XML)
   */

  public static final String CATEGORY = "category";

  /**
   * Number of corrected problems (grades XML)
   */

  public static final String CORRECTED = "corrected";

  /**
   * Id of a course (grades XML)
   */

  public static final String COURSE_ID = "course_id";

  /**
   * Name of a course (grades XML)
   */

  public static final String COURSE_NAME = "course_name";

  /**
   * Number of edited problems (grades XML)
   */

  public static final String EDITED = "edited";

  /**
   * First name of a user (grades XML; data record XML)
   */

  public static final String FIRST_NAME = "first_name";

  /**
   * A label (grades XML)
   */

  public static final String LABEL = "label";

  /**
   * A problem id (grades XML)
   */

  public static final String PROBLEM_ID = "problem_id";

  /**
   * Number of problems in a worksheet (grades XML)
   */

  public static final String PROBLEMS = "problems";

  /**
   * Maximum number of points (grades XML)
   */

  public static final String POINTS = "points";

  /**
   * Achieved number of points (grades XML)
   */

  public static final String RESULT = "result";

  /**
   * The score of an user in a problem (grades XML)
   */

  public static final String SCORE = "score";

  /**
   * The status of something (grades XML)
   */

  public static final String STATUS = "status";

  /**
   * Surname of a user (grades XML; data record XML)
   */

  public static final String SURNAME = "surname";

  /**
   * The syncId of a pseudo-document (grades XML)
   */

  public static final String SYNC_ID = "sync_id";

  /**
   * Total number of something (grades XML; data session XML)
   */

  public static final String TOTAL = "total";

  /**
   * Id of a tutorial (grades XML; data record XML)
   */

  public static final String TUTORIAL_ID = "tutorial_id";

  /**
   * Name of a tutorial (grades XML; data record XML)
   */

  public static final String TUTORIAL_NAME = "tutorial_name";

  /**
   * Id of a VC thread (grades XML)
   */

  public static final String VC_THREAD_ID = "vc_thread_id";

  /**
   * A worksheets id (grades XML)
   */

  public static final String WORKSHEET_ID = "worksheet_id";

  /**
   * Id of a teaching class (data record XML)
   */

  public static final String CLASS_ID = "class_id";

  /**
   * Name of a teaching class (data record XML)
   */

  public static final String CLASS_NAME = "class_name";

  /**
   * Id of the tutor (data record XML)
   */

  public static final String TUTOR_ID = "tutor_id";

  /**
   * First name of a tutor (data record XML)
   */

  public static final String TUTOR_FIRST_NAME = "tutor_first_name";

  /**
   * Surname of a tutor (data record XML)
   */

  public static final String TUTOR_SURNAME = "tutor_surname";

  /**
   * Originator of a "added_data" element (added-data XML).
   */

  public static final String ORIGINATOR = "originator";

  /**
   * Whether a java class is disposable (status XML)
   */

  public static final String DISPOSABLE = "disposable";

  /**
   * Whether a java class is lookup notifyable (status XML)
   */

  public static final String LOOKUP_NOTIFYABLE = "lookup_notifyable";

  /**
   * The reason of something (status XML)
   */

  public static final String REASON = "reason";

  /**
   * Whether a java class is recyclable (status XML)
   */

  public static final String RECYCLABLE = "recyclable";

  /**
   * Whether a java class is startable (status XML)
   */

  public static final String STARTABLE = "startable";


  // ================================================================================
  // Autocoded methods end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private XMLAttribute ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("XMLAttribute must not be instanciated");
  }
}