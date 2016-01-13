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

package net.mumie.mathletfactory.util.exercise.mc;

import net.mumie.mathletfactory.util.exercise.ExerciseConstants;

/**
 * This interface holds all necessary constants for MC-Problems.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public interface MCProblemConstants extends ExerciseConstants {

  /** Constant for the an unknown answer type */
  public final static int UNKNOWN_ANSWER_TYPE = -1;
  
	/**	Constant for the answer type "unique" */
	public final static int UNIQUE_ANSWER_TYPE = 0;
	
	/**	Constant for the answer type "multiple" */
	public final static int MULTIPLE_ANSWER_TYPE = 1;
	
	/**	Constant for the answer type "yesno" */
	public final static int YESNO_ANSWER_TYPE = 2;

  /** Constant for the answer type "text" */
  public final static int TEXT_ANSWER_TYPE = 3;

	/**	Constant for the answer type name "unique" */
	public final static String UNIQUE_ANSWER_TYPE_NAME = "unique";
	
	/**	Constant for the answer type name "multiple" */
	public final static String MULTIPLE_ANSWER_TYPE_NAME = "multiple";
	
	/**	Constant for the answer type name "yesno" */
	public final static String YESNO_ANSWER_TYPE_NAME = "yesno";

  /** Constant for the answer type name "text" */
  public final static String TEXT_ANSWER_TYPE_NAME = "text";

	/**	Constant for the question's "type" sub-path */
	public final static String QUESTION_TYPE_SUBPATH = "type";

	/**	Constant for the question's "task" sub-path */
	public final static String QUESTION_TASK_SUBPATH = "task";

	/**	Constant for the variable sub-path */
	public final static String VARIABLE_SUBPATH = "vars";
	
	/**	Constant for the variable's "field" sub-path */
	public final static String VARIABLE_FIELD_SUBPATH = "field";
	
	/**	Constant for the variable's "precision" sub-path */
	public final static String VARIABLE_PRECISION_SUBPATH = "precision";
	
	/**	Constant for the answer's "assertion" sub-path */
	public final static String ANSWER_ASSERTION_SUBPATH = "assertion";

	/**	Constant for the answer's "solution" sub-path */
	public final static String ANSWER_SOLUTION_SUBPATH = "solution";

  /** Constant for the answer's "explanation" sub-path */
  public final static String ANSWER_EXPLANATION_SUBPATH = "explanation";

	/**	Constant for the answer's "field" sub-path */
	public final static String ANSWER_FIELD_SUBPATH = VARIABLE_FIELD_SUBPATH;
	
	/**	Constant for the answer's "precision" sub-path */
	public final static String ANSWER_PRECISION_SUBPATH = VARIABLE_PRECISION_SUBPATH;
	
	/**	Constant for the answer's "field" sub-path */
	public final static String EDITED_FLAG_SUBPATH = "generic" + PATH_SEPARATOR + "edited";
	
	/**	Constant for the function's "content" sub-path */
	public final static String FUNCTION_CONTENT_SUBPATH = "content";

	/**	Constant for the function's "action" sub-path */
	public final static String FUNCTION_ACTION_SUBPATH = "action";

	/**	Constant for the function's "normalize" sub-path */
	public final static String FUNCTION_NORMALIZE_SUBPATH = "normalize";
	
  /** Constant for an unknown action */
  public final static int FUNCTION_UNKNOWN_ACTION = -1;
  
	/**	Constant for the function action "replace" */
	public final static int FUNCTION_REPLACE_ACTION = 0;
	
	/**	Constant for the function action "calculate" */
	public final static int FUNCTION_CALCULATE_ACTION = 1;

	
	/**	Constant for the function action name "replace" */
	public final static String FUNCTION_REPLACE_ACTION_NAME = "replace";
	
	/**	Constant for the function action name "calculate" */
	public final static String FUNCTION_CALCULATE_ACTION_NAME = "calculate";
	
	/**	Constant for the question prefix sub-path */
  public final static String QUESTION_PREFIX = "question_";
	
	/**	Constant for the answer prefix sub-path */
  public final static String ANSWER_PREFIX = "choice_";
  
  /** Constant for the answer prefix sub-path */
  public final static String TEXT_ANSWER_PREFIX = "text";
  
  /** Constant for the question selection prefix sub-path */
  public final static String QUESTIONS_SELECTION_PREFIX = "questions";
  
  /** Constant for an unknown solution */
  public final static int SOLUTION_UNKNOWN = -2;
  
  /** Constant for the explicit solution "false" */
  public final static int SOLUTION_FALSE = 0;
  
	/**	Constant for the explicit solution "true" */
  public final static int SOLUTION_TRUE = 1;
  
	/**	Constant for the implicit solution "compute" */
  public final static int SOLUTION_COMPUTE = -1;
  
	/**	Constant for the explicit solution name "true" */
  public final static String SOLUTION_TRUE_TYPE_NAME = "true";
  
	/**	Constant for the explicit solution name "false" */
  public final static String SOLUTION_FALSE_TYPE_NAME = "false";
  
	/**	Constant for the implicit solution name "compute" */
  public final static String SOLUTION_COMPUTE_TYPE_NAME = "compute";
}
