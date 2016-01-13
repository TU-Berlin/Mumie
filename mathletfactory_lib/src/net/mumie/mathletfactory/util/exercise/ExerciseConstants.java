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

package net.mumie.mathletfactory.util.exercise;

/**
 * This interface holds all necessary constants for Mumie exercises.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public interface ExerciseConstants {

  public final static String PATH_SEPARATOR = "/";
  
  public final static String COMMON_PROBLEM_PATH = "common/problem";
  
  public final static String USER_PROBLEM_PATH = "user/problem";

  /** Path containing flags about correctness for each subtask. Used by corrector helpers. */
  public final static String USER_META_ANSWER_PATH = "user/meta/answer";
  
  public final static String FIELD_PATH = "common/problem/field";

  public final static String DIMENSION_PATH = "common/problem/dimension";

  public final static String USER_ANSWER_PATH = "user/answer";

  public final static String GENERIC_USER_ANSWER_PATH = "user/answer/generic";
  
  public final static String CURRENT_SUBTASK_PATH = GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + "currentSubtask";

  public final static String USER_SCORE_PATH = "user/meta/score";

  public final static String SUBTASK_PREFIX = "subtask_";
  
  public final static String SELECTION_PREFIX = "selected_";
  
  public final static String SUBTASK_PATH = USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX;

  public final static String USER_MARKING_PATH = "user/marking";
  
  /** Path for the exercise type name. */
  public final static String EXERCISE_TYPE_PATH = GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + "problemType";
  
  /** Exercise type constant for traditional applet exercises. */
  public final static int EXERCISE_DEFAULT_TYPE = 0;
  
  /** Exercise type constant for multiple choice applet exercises. */
  public final static int EXERCISE_MC_TYPE = 1;
  
  /** Exercise type constant for text input applet exercises. */
  public final static int EXERCISE_TEXT_TYPE = 2;
  
  /** Exercise type name constant for traditional applet exercises. */
  public final static String EXERCISE_DEFAULT_TYPE_NAME = "default";
  
  /** Exercise type name constant for multiple choice applet exercises. */
  public final static String EXERCISE_MC_TYPE_NAME = "mc";
  
  /** Exercise type name constant for text input applet exercises. */
  public final static String EXERCISE_TEXT_TYPE_NAME = "text";
  
  //
  // Applet parameters for homework mode
  //
  public final static String HOMEWORK_MODE_PARAM = "homeworkMode";
  public final static String HOMEWORK_EDITABLE_PARAM = "homeworkEditable";
  public final static String INPUT_DATASHEET_PARAM = "inputDatasheet";
  public final static String OUTPUT_DATASHEET_PARAM = "outputDatasheet";
  public final static String CORRECTOR_CLASS_PARAM = "correctorClass";
  public final static String PROBLEM_REF_PARAM = "problemRef";
  public final static String URL_PREFIX_PARAM = "urlPrefix";
  public final static String COURSE_ID_PARAM = "courseId";
  public final static String WORKSHEET_ID_PARAM = "worksheetId";
  public final static String PROBLEM_ID_PARAM = "problemId";
  public final static String SAVE_ANSWERS_PATH_PARAM = "saveAnswersPath";
  
  //
  // Values for homework mode
  //
  public final static String REMOTE_JAPS_MODE = "remote:japs";
  public final static String LOCAL_DEBUG_MODE = "local:debug";
  public final static String LOCAL_PREVIEW_MODE = "local:preview";
  
}
