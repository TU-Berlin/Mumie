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

import java.util.Vector;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.japs.datasheet.DataSheetException;
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.util.MString;
import net.mumie.mathletfactory.util.text.TeXConverter;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Element;

/**
 * This class is the base for both applet and corrector exercise classes.
 * Sub-classes should use a worker instance (e.g. {@link net.mumie.mathletfactory.util.exercise.MumieExercise} or
 * {@link net.mumie.mathletfactory.util.exercise.CorrectorHelper}) for dealing with datasheets.
 *
 * @see net.mumie.mathletfactory.util.exercise.mc.MCAppletExercise
 * @see net.mumie.mathletfactory.util.exercise.mc.MCCorrectorExercise
 * @see net.mumie.japs.datasheet.DataSheet
 *
 * @author gronau
 * @mm.docstatus finished
 */
public abstract class MCAbstractExercise implements MCExerciseIF {

	private int m_questionCount = -1;
	private int[] m_answerType;
	private int[] m_answerCount;
	private Boolean m_wasEdited = null;
	private DataSheet m_AnswerSheet;

  /**
   * Returns the answer type of the given question.
   */
	public int getAnswerType(int questionNr) {
		if(m_answerType == null) {
			m_answerType = new int[getQuestionCount()];
			for(int i = 0; i < m_answerType.length; i++)
				m_answerType[i] = -1;
		}
		if(m_answerType[questionNr - 1] == -1)
			m_answerType[questionNr - 1] = loadAnswerType(questionNr);
		return m_answerType[questionNr - 1];
	}
  
  /**
   * Returns the answer type name for the given answer type or <code>null</code>
   * if it does not correspond to one of the answer type constants.
   * @param type the answer type
   * @return answer type name
   * @see #UNIQUE_ANSWER_TYPE
   * @see #MULTIPLE_ANSWER_TYPE
   * @see #YESNO_ANSWER_TYPE
   * @see #TEXT_ANSWER_TYPE
   */
  public static String getAnswerTypeName(int type) {
    switch(type) {
    case UNIQUE_ANSWER_TYPE:
      return UNIQUE_ANSWER_TYPE_NAME;
    case MULTIPLE_ANSWER_TYPE:
      return MULTIPLE_ANSWER_TYPE_NAME;
    case YESNO_ANSWER_TYPE:
      return YESNO_ANSWER_TYPE_NAME;
    case TEXT_ANSWER_TYPE:
      return TEXT_ANSWER_TYPE_NAME;
    default:
      return null;
    }
  }
  
  /**
   * Returns the answer type for the given answer type name or the constant
   * {@link #UNKNOWN_ANSWER_TYPE} if it does not correspond to one of the answer 
   * type name constants.
   * @param typeName the answer type name
   * @return answer type name
   * @see #UNIQUE_ANSWER_TYPE_NAME
   * @see #MULTIPLE_ANSWER_TYPE_NAME
   * @see #YESNO_ANSWER_TYPE_NAME
   * @see #TEXT_ANSWER_TYPE_NAME
   * @see #UNKNOWN_ANSWER_TYPE
   */
  public static int getAnswerType(String typeName) {
    if(typeName.equals(UNIQUE_ANSWER_TYPE_NAME))
      return UNIQUE_ANSWER_TYPE;
    if(typeName.equals(MULTIPLE_ANSWER_TYPE_NAME))
      return MULTIPLE_ANSWER_TYPE;
    if(typeName.equals(YESNO_ANSWER_TYPE_NAME))
      return YESNO_ANSWER_TYPE;
    if(typeName.startsWith(TEXT_ANSWER_TYPE_NAME))
      return TEXT_ANSWER_TYPE;
    return UNKNOWN_ANSWER_TYPE;
  }

  /**
   * Returns the function action type name for the given function action type or <code>null</code>
   * if it does not correspond to one of the function action type constants.
   * @param type the function action type
   * @return answer type name
   * @see #FUNCTION_REPLACE_ACTION
   * @see #FUNCTION_CALCULATE_ACTION
   */
  public static String getFunctionActionTypeName(int type) {
    switch(type) {
    case FUNCTION_REPLACE_ACTION:
      return FUNCTION_REPLACE_ACTION_NAME;
    case FUNCTION_CALCULATE_ACTION:
      return FUNCTION_CALCULATE_ACTION_NAME;
    default:
      return null;
    }
  }

  /**
   * Returns the function action type for the given function action type name or 
   * the constant {@link #FUNCTION_UNKNOWN_ACTION} if it does not correspond to 
   * one of the function action type name constants.
   * @param type the function action type name
   * @return answer type name
   * @see #FUNCTION_REPLACE_ACTION_NAME
   * @see #FUNCTION_CALCULATE_ACTION_NAME
   * @see #FUNCTION_UNKNOWN_ACTION
   */
  public static int getFunctionActionType(String actionString) {
    if(actionString.equals(FUNCTION_REPLACE_ACTION_NAME))
      return FUNCTION_REPLACE_ACTION;
    else if(actionString.equals(FUNCTION_CALCULATE_ACTION_NAME))
      return FUNCTION_CALCULATE_ACTION;
    return FUNCTION_UNKNOWN_ACTION;
  }
	
	/**
	 * Loads the answer type for the specified question from a datasheet.
	 * @param questionNr the question's number
	 * @return the answer type (see {@link MCProblemConstants})
	 * @throws MCProblemException if no valid TYPE element is found for the given question
	 */
	private int loadAnswerType(int questionNr) {
		String typeString = getProblemString(QUESTION_PREFIX + questionNr + PATH_SEPARATOR + QUESTION_TYPE_SUBPATH);
		if(typeString == null)
			throw new MCProblemException("No TYPE element found for question " + questionNr + " !");
		typeString = typeString.trim();
    int type = getAnswerType(typeString);
    if(type == UNKNOWN_ANSWER_TYPE)
      throw new MCProblemException("No TYPE element found for question " + questionNr + " !");
    return type;
	}
	
	public String loadQuestionTask(int questionNr) {
		String task = getProblemString(QUESTION_PREFIX + questionNr + PATH_SEPARATOR + QUESTION_TASK_SUBPATH);
		if(task == null)
      throw new MCProblemException("Task must not be null for question " + questionNr);
		task = task.trim();
		return task;
	}
  
  /**
   * Loads the explanation for the given question and returns it. May be <code>null</code>.
   * @param questionNr the question's number
   * @return an explanation or <code>null</code>
   */
  public String loadExplanation(int questionNr) {
    String path = QUESTION_PREFIX + questionNr + PATH_SEPARATOR;
    String text = getProblemString(path + ANSWER_EXPLANATION_SUBPATH);
    if(text == null)
      return null;
    return text.trim();
  }
  
  /**
   * Loads the explanation for the given question and answer and returns it. May be <code>null</code>.
   * @param questionNr the question's number
   * @param answerNr the answer's number
   * @return an explanation or <code>null</code>
   */
  public String loadExplanation(int questionNr, int answerNr) {
    String path = QUESTION_PREFIX + questionNr + PATH_SEPARATOR;
    String text = (answerNr == -1) ? null : getProblemString(path + ANSWER_PREFIX + answerNr + PATH_SEPARATOR + ANSWER_EXPLANATION_SUBPATH);
    if(text == null)
      return null;
    return text.trim();
  }
  
	public int getQuestionCount() {
		if(m_questionCount == -1)
			m_questionCount = loadQuestionCount();
		return m_questionCount;
	}
	
	/**
	 * Loads the total number of questions from a datasheet.
	 */
	private int loadQuestionCount() {
		int result = 0;
		while(problemElementExists(QUESTION_PREFIX + (result + 1) + PATH_SEPARATOR + QUESTION_TYPE_SUBPATH)) {
			result++;
		}
		return result;
	}
	
  /**
   * Loads and returns a list of selected questions used for random problems.
   * @return a list of question indices
   */
  public int[] loadQuestionSelections() {
    Vector questions = new Vector();
    int count = 0;
    while(problemElementExists(QUESTIONS_SELECTION_PREFIX + PATH_SEPARATOR + QUESTION_PREFIX + (count + 1))) {
      String selectionString = getProblemString(QUESTIONS_SELECTION_PREFIX + PATH_SEPARATOR + QUESTION_PREFIX + (count + 1));
      Integer selection = null;
      if(selectionString == null) { // value is not a string but a "mn" node
        MInteger mnumber = new MInteger();
        loadElement(COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTIONS_SELECTION_PREFIX + PATH_SEPARATOR + QUESTION_PREFIX + (count + 1), mnumber);        
        selection = new Integer(mnumber.getIntValue());
      } else { // value is a raw string
        try {
          selectionString = selectionString.trim(); // remove line breaks and white spaces
          selection = new Integer(selectionString);
        } catch(NumberFormatException e) {
          throw new MCProblemException("Wrong question selection for question " + (count + 1) + ": \"" + selectionString + "\"");
        }
      }
      // check bounds for random number
      if(selection.intValue() < 1 || selection.intValue() > getQuestionCount())
        throw new MCProblemException("Question selection for question " + (count + 1) + " is out of bounds!");
      questions.add(selection);
      count++;
    }
    if(count == 0)
      throw new MCProblemException("No question selections found!");
    int[] result = new int[questions.size()];
    for(int i = 0; i < result.length; i++)
      result[i] = ((Integer) questions.get(i)).intValue();
    return result;
  }
  
	public int getAnswerCount(int questionNr) {
		if(m_answerCount == null) {
			m_answerCount = new int[getQuestionCount()];
			for(int i = 0; i < m_answerCount.length; i++)
				m_answerCount[i] = -1;
		}
		if(m_answerCount[questionNr - 1] == -1)
			m_answerCount[questionNr - 1] = loadAnswerCount(questionNr);
		return m_answerCount[questionNr - 1];
	}
	
	/**
	 * Loads the number of answers/assertions for the specified question from a datasheet.
	 * @param questionNr the question's number
	 */
	private int loadAnswerCount(int questionNr) {
		int result = 0;
		while(problemElementExists(
				QUESTION_PREFIX + questionNr + PATH_SEPARATOR + 
				ANSWER_PREFIX + (result + 1) + PATH_SEPARATOR + ANSWER_ASSERTION_SUBPATH)) {
			result++;
		}
		return result;
	}
	
	/**
	 * Returns whether this MC-Problem was already edited (i.e. saved).
	 */
	public boolean wasEdited() {
		if(m_wasEdited == null)
			m_wasEdited = new Boolean(loadEditedFlag());
		return m_wasEdited.booleanValue();
	}
	
	/**
	 * Returns whether the datasheet contains entries below the user answer path.
	 */
	private boolean loadEditedFlag() {
		Boolean editedFlag = getBooleanAnswer(EDITED_FLAG_SUBPATH);
		if(editedFlag == null)
			return false;
		return editedFlag.booleanValue();// should always be "true"
	}
	
	public String loadAssertion(int questionNr, int answerNr) {
		String assertionText = getProblemString(
				QUESTION_PREFIX + questionNr + PATH_SEPARATOR + 
				ANSWER_PREFIX + answerNr + PATH_SEPARATOR + ANSWER_ASSERTION_SUBPATH);
		if(assertionText == null)
      throw new MCProblemException("Assertion must not be null for question " + questionNr);
		assertionText = assertionText.trim();
		return assertionText;
	}
	
	/**
	 * Loads the solution for the specified question and answer from a datasheet.
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @return a solution constant (see {@link MCProblemConstants})
	 */
	public int loadSolution(int questionNr, int answerNr) {
		String solutionString = getProblemString(
				QUESTION_PREFIX + questionNr + PATH_SEPARATOR + 
				ANSWER_PREFIX + answerNr + PATH_SEPARATOR + ANSWER_SOLUTION_SUBPATH);
		if(solutionString == null)
			throw new MCProblemException("No SOLUTION element found for answer " + answerNr + " in question " + questionNr);
		solutionString = solutionString.trim();
    int solution = getSolutionType(solutionString);
    if(solution == SOLUTION_UNKNOWN)
      throw new MCProblemException("Wrong SOLUTION element: " + solutionString);
    return solution;
	}
	
  /**
   * Returns the name of the specified solution type or <code>null</code> if it
   * does not correspond to one of the solution type constants.
   * @param solutionType a solution type
   * @return the solution type name (e.g. "TRUE")
   * @see #SOLUTION_TRUE
   * @see #SOLUTION_FALSE
   * @see #SOLUTION_COMPUTE
   */
  public static String getSolutionTypeName(int solutionType) {
    switch(solutionType) {
    case SOLUTION_TRUE:
      return SOLUTION_TRUE_TYPE_NAME;
    case SOLUTION_FALSE:
      return SOLUTION_FALSE_TYPE_NAME;
    case SOLUTION_COMPUTE:
      return SOLUTION_COMPUTE_TYPE_NAME;
    default:
      return null;
    }
  }
  
  /**
   * Returns the type of the specified solution type name or the constant
   * {@link #SOLUTION_UNKNOWN} if it does not correspond to one of 
   * the solution type name constants.
   * @param solutionString a solution type name
   * @return the solution type
   * @see #SOLUTION_TRUE_TYPE_NAME
   * @see #SOLUTION_FALSE_TYPE_NAME
   * @see #SOLUTION_COMPUTE_TYPE_NAME
   * @see #SOLUTION_UNKNOWN
   */
  public static int getSolutionType(String solutionString) {
    if(solutionString.equals(SOLUTION_TRUE_TYPE_NAME))
      return SOLUTION_TRUE;
    if(solutionString.equals(SOLUTION_FALSE_TYPE_NAME))
      return SOLUTION_FALSE;
    if(solutionString.equals(SOLUTION_COMPUTE_TYPE_NAME))
      return SOLUTION_COMPUTE;
    return SOLUTION_UNKNOWN;
  }
  
	/**
	 * Loads a relation for the specified question and answer from a datasheet.
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @return an instance of {@link Relation}
	 */
	public Relation loadRelation(int questionNr, int answerNr) {
		String path = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ ANSWER_PREFIX + answerNr + PATH_SEPARATOR + "correction/relation/";
		Operation leftSide = loadFunctionVariable(questionNr, answerNr, path, "left_side", true);
		Operation rightSide = loadFunctionVariable(questionNr, answerNr, path, "right_side", true);
		String sign = getString(path + "sign").trim();
		Relation relation = new Relation(leftSide, rightSide, sign.trim(), Relation.NORMALIZE_NONE);
		return relation;
	}
	
	public Class loadNumberClass(int questionNr) {
		String fieldName = getProblemString(QUESTION_PREFIX + questionNr + PATH_SEPARATOR + ANSWER_FIELD_SUBPATH);
		if(fieldName == null)
			return MDouble.class;// Default is "real"
		return parseNumberClass(fieldName);
	}
	
	/**
	 * Parses the given field name (i.e. number class definition) and returns the equivalent number class.
	 * @throws MCProblemException if the field name is invalid
	 */
	public Class parseNumberClass(String fieldName) {
		try {
			return XMLUtils.getNumberClassFromField(fieldName.trim());
		} catch(Exception e) {
			throw new MCProblemException("Invalid number class definition!", e);
		}
	}
	
	/**
	 * Returns the precision for the specified question.
	 * @throws MCProblemException if the precision definition is invalid
	 */
	public int loadPrecision(int questionNr) {
		try {
			String precision = getProblemString(QUESTION_PREFIX + questionNr + PATH_SEPARATOR + ANSWER_PRECISION_SUBPATH);
			if(precision == null)
				return 2;// Default value
			return Integer.parseInt(precision.trim());
		} catch(Exception e) {
			throw new MCProblemException("Invalid precision definition!", e);
		}
	}
	
	/**
	 * Returns whether the variable with the given identifier and for the specified question is a number.
	 * This method mainly checks if a <code>data</code> element is contained in the datasheet under the question's path
	 * for variables.
	 * @param questionNr the question's number
	 * @param identifier the variable's name
	 * @see XMLUtils#isDataElement(DataSheet, String)
	 */
	public boolean isNumberVariable(int questionNr, String identifier) {
		String path = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ VARIABLE_SUBPATH + PATH_SEPARATOR;
		return XMLUtils.isDataElement(getQuestionSheet(), path + identifier);
	}
	
	/**
	 * Returns whether the variable with the given identifier and for the specified question and answer is a number.
	 * This method mainly checks if a <code>data</code> element is either contained under the answer's variables path
	 * or under the question's variables path in the datasheet.
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @param identifier the variable's name
	 * @see #isNumberVariable(int, String)
	 * @see XMLUtils#isDataElement(DataSheet, String)
	 */
	public boolean isNumberVariable(int questionNr, int answerNr, String identifier) {
		String path = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ ANSWER_PREFIX + answerNr + PATH_SEPARATOR + VARIABLE_SUBPATH + PATH_SEPARATOR;
		if(XMLUtils.isDataElement(getQuestionSheet(), path + identifier))
			return true;
		// search for global variable
		return isNumberVariable(questionNr, identifier);
	}
	
	/**
	 * Returns whether the variable with the given identifier and for the specified question is a function.
	 * This method mainly checks if a <code>unit</code> element is contained in the datasheet under the question's path
	 * for variables.
	 * @param questionNr the question's number
	 * @param identifier the variable's name
	 * @see XMLUtils#isUnitElement(DataSheet, String)
	 */
	public boolean isFunctionVariable(int questionNr, String identifier) {
		String path = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ VARIABLE_SUBPATH + PATH_SEPARATOR;
		return XMLUtils.isUnitElement(getQuestionSheet(), path + identifier);
	}
	
	/**
	 * Returns whether the variable with the given identifier and for the specified question and answer is a function.
	 * This method mainly checks if a <code>unit</code> element is either contained under the answer's variables path
	 * or under the question's variables path in the datasheet.
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @param identifier the variable's name
	 * @see #isFunctionVariable(int, String)
	 * @see XMLUtils#isUnitElement(DataSheet, String)
	 */
	public boolean isFunctionVariable(int questionNr, int answerNr, String identifier) {
		String path = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ ANSWER_PREFIX + answerNr + PATH_SEPARATOR + VARIABLE_SUBPATH + PATH_SEPARATOR;
		if(XMLUtils.isUnitElement(getQuestionSheet(), path + identifier))
			return true;
		// search for global variable
		return isFunctionVariable(questionNr, identifier);
	}
	
	/**
	 * Loads a number variable with the given identifier and for the specified question from a datasheet. 
	 * @param questionNr the question's number
	 * @param identifier the variable's name
	 * @return an instance of {@link MNumber}
	 */
	public MNumber loadNumberVariable(int questionNr, String identifier) {
		String varsPath = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ VARIABLE_SUBPATH + PATH_SEPARATOR;
		return loadNumberVariable(questionNr, varsPath, identifier, true);
	}
	
	/**
	 * Loads a number variable with the given identifier and for the specified question and answer from a datasheet. 
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @param identifier the variable's name
	 * @return an instance of {@link MNumber}
	 */
	public MNumber loadNumberVariable(int questionNr, int answerNr, String identifier) {
		String varsPath = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ ANSWER_PREFIX + answerNr + PATH_SEPARATOR + VARIABLE_SUBPATH + PATH_SEPARATOR;
		MNumber result = (answerNr == -1) ? null : loadNumberVariable(questionNr, varsPath, identifier, false);
		if(result == null)
			return loadNumberVariable(questionNr, identifier);
		return result;
	}
	
	/**
	 * Loads a number variable from the specified path from a datasheet. 
	 */
	private MNumber loadNumberVariable(int questionNr, String variablePath, String identifier, boolean throwError) {
		if(!XMLUtils.isDataElement(getQuestionSheet(), variablePath + identifier)) {
			if(throwError)
				throw new MCProblemException("No DATA element found for number variable \"" + identifier + "\"");
			else
				return null;
		}
		String identifierPath = variablePath + identifier;
		Class numberClass = loadNumberClass(questionNr);
		MNumber result = NumberFactory.newInstance(numberClass);
		loadElement(identifierPath, result);
		return result;
	}
	
	/**
	 * Loads a function variable with the given identifier and for the specified question from a datasheet. 
	 * This method automatically applies all required substitutions, normalization rules and operations on the resulting function. 
	 * @param questionNr the question's number
	 * @param identifier the variable's name
	 * @return an instance of {@link Operation}
	 */
	public Operation loadFunctionVariable(int questionNr, String identifier) {
		String varsPath = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ VARIABLE_SUBPATH + PATH_SEPARATOR;
		return loadFunctionVariable(questionNr, -1, varsPath, identifier, true);
	}
	
	/**
	 * Loads a function variable with the given identifier and for the specified question and answer from a datasheet. 
	 * This method automatically applies all required substitutions, normalization rules and operations on the resulting function. 
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @param identifier the variable's name
	 * @return an instance of {@link Operation}
	 */
	public Operation loadFunctionVariable(int questionNr, int answerNr, String identifier) {
		String varsPath = COMMON_PROBLEM_PATH + PATH_SEPARATOR + QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
				+ ANSWER_PREFIX + answerNr + PATH_SEPARATOR + VARIABLE_SUBPATH + PATH_SEPARATOR;
		Operation result = (answerNr == -1) ? null : loadFunctionVariable(questionNr, answerNr, varsPath, identifier, false);
		if(result == null)
			return loadFunctionVariable(questionNr, identifier);
		return result;
	}
	
	/**
	 * Loads a function variable from the specified path from a datasheet.
	 * This method automatically applies all required substitutions, normalization rules and operations on the resulting function. 
	 */
	private Operation loadFunctionVariable(int questionNr, int answerNr, String variablePath, String identifier, boolean throwError) {
		if(!XMLUtils.isUnitElement(getQuestionSheet(), variablePath + identifier)) {
			if(throwError)
				throw new MCProblemException("No UNIT element found for function variable \"" + identifier + "\" for answer " + answerNr + " in question " + questionNr);
			else
				return null;
		}
		String identifierPath = variablePath + identifier + PATH_SEPARATOR;
		// load number class from question
		Class numberClass = loadNumberClass(questionNr);
		// load normalize flag (if existent)
		boolean normalize = false; // Default
		String normalizeFlag = getString(identifierPath + FUNCTION_NORMALIZE_SUBPATH);
		if(normalizeFlag != null) {
			normalizeFlag = normalizeFlag.trim();
			if(normalizeFlag.equalsIgnoreCase("true"))
				normalize = true;
			else if(normalizeFlag.equalsIgnoreCase("false"))
				normalize = false;
			else
				throw new MCProblemException("Values of NORMALIZE can be either TRUE or FALSE but not " + normalizeFlag);
		}
		// load action flag (if existent)
		int action = FUNCTION_REPLACE_ACTION; // Default
		String actionString = getString(identifierPath + FUNCTION_ACTION_SUBPATH);
		if(actionString != null) {
			actionString = actionString.trim();
      action = getFunctionActionType(actionString);
      if(action == FUNCTION_UNKNOWN_ACTION)
				throw new MCProblemException("Values of ACTION can be either REPLACE or CALCULATE but not " + actionString);
		}
		// load field flag (if existent)
		String fieldName = getString(identifierPath + VARIABLE_FIELD_SUBPATH);
		if(fieldName != null) {
			numberClass = parseNumberClass(fieldName);
		}
		// construct operation
		Operation op = new Operation(numberClass, "0", normalize);
		loadFunction(identifierPath + FUNCTION_CONTENT_SUBPATH, op);
		// replace variables in operation (if required)
		if(action == FUNCTION_REPLACE_ACTION || action == FUNCTION_CALCULATE_ACTION) {
			String[] containedVars = op.getUsedVariables();
			for(int i = 0; i < containedVars.length; i++) {
				// is it a number?
				if(isNumberVariable(questionNr, containedVars[i])) {
					MNumber parameter = loadNumberVariable(questionNr, answerNr, containedVars[i]);
					replaceVariable(op, new Operation(parameter), containedVars[i]);
				}
				// or a function?
				else if(isFunctionVariable(questionNr, containedVars[i])) {
					Operation parameterOp = loadFunctionVariable(questionNr, answerNr, containedVars[i]);
					replaceVariable(op, parameterOp, containedVars[i]);
				}
			}
		}
		// calculate operation (if required)
		if(action == FUNCTION_CALCULATE_ACTION) {
			op.set(op.getResult());
		}
		return op;
	}
	
	protected void loadFunction(String path, Operation target) throws MCProblemException {
		try {
			try {
				Element opContent = getQuestionSheet().getAsElement(path);
				if(opContent == null)
					throw new MCProblemException("Cannot find content of function variable!");		
				target.setMathMLNode(opContent);
				return;
			} catch(DataSheetException dse1) {// cannot read function as XML element, try as a raw string instead...
				try {
					String opContent = getQuestionSheet().getAsString(path);
					if(opContent == null)
						throw new MCProblemException("Cannot find content of function variable!");
					target.setOpRoot(OpParser.getOperationTree(target.getNumberClass(), opContent.trim(), target.isNormalForm()));
				} catch(DataSheetException dse2) {// cannot read function as string, forward first error...
					throw new MCProblemException("Cannot read content of function variable", dse1);
				}
			} 
		} catch (Exception e) {
			throw new MCProblemException("Cannot read function from path: " + path, e);
		}
	}
	
	/**
	 * Replaces all occurences of the given identifier in <code>inOp</code> with <code>withOp</code>.
	 * @param inOp the expression to be changed
	 * @param withOp the value of the variable
	 * @param identifier the identifier of the variable
	 */
	private static void replaceVariable(Operation inOp, Operation withOp, String identifier) {
    if(inOp.getOpRoot() instanceof VariableOp && ((VariableOp)inOp.getOpRoot()).getIdentifier().equals(identifier)) {
    	OpNode old = inOp.getOpRoot();
    	inOp.setOpRoot(withOp.getOpRoot());
    	OpNode.transferFactorAndExponent(old, inOp.getOpRoot(), false);
    } else {
    	replaceVariable(inOp.getOpRoot(), withOp.getOpRoot(), identifier);
    }
	}

	/**
	 * Replaces all occurences of the given identifier in <code>inNode</code> with <code>withNode</code>.
	 * Returns <code>true</code> if at least one substitution was made.
	 * @param inNode the operation node to be changed
	 * @param withNode the value of the variable
	 * @param identifier the identifier of the variable
	 */
  private static boolean replaceVariable(OpNode inNode, OpNode withNode, String identifier) {
  	OpNode[] children = inNode.getChildren();
    if(children == null)
      return false;
    boolean didReplace = false;
    for(int i = 0; i < children.length; i++) {
      if(children[i] instanceof VariableOp &&
           ((VariableOp)children[i]).getIdentifier().equals(identifier)){
      	OpNode old = children[i];
      	children[i] = (OpNode)withNode.clone();
      	OpNode.transferFactorAndExponent(old, children[i], false);
        didReplace = true;
      } else
        didReplace = replaceVariable(children[i], withNode, identifier);
    }
		inNode.setChildren(children); // reorder
    return didReplace;
  }
  
	/**
	 * Parses the given text for variables contained in the specified question and answer and returns
	 * an array of {@link ExerciseObjectIF} containing all parsed tokens. This method automatically replaces
	 * all contained special characters into Unicode characters.
	 * 
	 * @param text the text to be parsed
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @throws MCProblemException if no variable is defined for the specified question and answer
	 */
	public ExerciseObjectIF[] parseText(String text, int questionNr, int answerNr) {
		String preParsed = preparseText(text);
		Vector v = new Vector();
		parseText(preParsed, v, questionNr, answerNr);
		ExerciseObjectIF[] result = new ExerciseObjectIF[v.size()];
		v.toArray(result);
		return result;
	}
	
	/**
	 * Performs a custom preparsing of the given text and returns a new processed string.
	 * @param text a text to be preparsed
	 * @return a new string which is the preparsed version of the given text
	 */
	protected String preparseText(String text) {
		// replace TeX-like expressions with Unicode characters (e.g. "s or "a) and remove white spaces/line breaks from XML
		return TeXConverter.toUnicode(TeXConverter.trim(text));
	}

	/**
	 * Parses the given text into the list <code>v</code> of {@link ExerciseObjectIF}s by traversing linearly
	 * through the text. The parser mainly searches for variables and line breaks. 
	 */
	private void parseText(String text, Vector list, int questionNr, int answerNr) {
		if(text.length() == 0)
			return;
		// look for first occurence of a break line or a formula
		int breakLineIndex = text.indexOf("\\\\");
		int formulaIndex = text.indexOf("$");
		int index;
		// first occurence is a break line?
		if(breakLineIndex > -1 && (formulaIndex == -1 || breakLineIndex < formulaIndex)) {
			index = breakLineIndex;
			// search for line breaks
			String endOfLine = text.substring(0, index);
			if(endOfLine.length() > 0)
				list.add(new MString(endOfLine));
			// add line break as single entry
			list.add(new MString("\\\\"));
			parseText(text.substring(index + 2), list, questionNr, answerNr);
			return;
		}
		// or is first occurence a formula?
		if(formulaIndex > -1 && (breakLineIndex == -1 || formulaIndex < breakLineIndex)) {
			index = formulaIndex;
			// search for variables
			String beforeVar = text.substring(0, index);
			if(beforeVar.length() > 0)
				list.add(new MString(beforeVar));
			// search for closing bracket
			int closing = text.indexOf("$", index + 1);
			String varName = text.substring(index + 1, closing);
			if(isNumberVariable(questionNr, answerNr, varName)) {
				MNumber number = loadNumberVariable(questionNr, answerNr, varName);
				list.add(number);
			}
			else if(isFunctionVariable(questionNr, answerNr, varName)) {
				Operation function = loadFunctionVariable(questionNr, answerNr, varName);
				list.add(function);
			} else
				throw new MCProblemException("No variable defined with identifier \"" + varName + "\"!");
			parseText(text.substring(closing + 1), list, questionNr, answerNr);
			return;
		}
		// add it as raw text otherwise
		list.add(new MString(text));
	}
	
	/**
	 * Parses the user answer string in the question sheet under the specified path into a {@link Boolean} and returns it.
	 * Returns <code>null</code> if no such path is found.
	 * @param path a path in the question sheet below the user answer path
	 * @return an instance of {@link Boolean} or <code>null</code>
	 * @throws MCProblemException if a {@link DataSheetException} occurs (e.g. not a valid boolean string as "true" and "false")
	 * @see DataSheet#getAsBoolean(String)
	 */
	public Boolean getBooleanAnswer(String path) {
  	try {
			return getQuestionSheet().getAsBoolean(USER_ANSWER_PATH + PATH_SEPARATOR + path);
		} catch (DataSheetException e) {
			throw new MCProblemException("Error reading datasheet", e);
		}
	}
  
  /**
   * Returns the user answer text for the specified question.
   * Returns <code>null</code> if nothing was saved.
   * @param questionNr the number of the question
   * @return an instance of {@link String} or <code>null</code>
   * @throws MCProblemException if a {@link DataSheetException} occurs (e.g. not a valid string)
   * @see DataSheet#getAsString(String)
   */
  public String getTextAnswer(int questionNr) {
    try {
      String s = getQuestionSheet().getAsString(USER_ANSWER_PATH + PATH_SEPARATOR 
          + QUESTION_PREFIX + questionNr + PATH_SEPARATOR + TEXT_ANSWER_PREFIX);
      if(s == null)
        return null;
      return TeXConverter.trim(s);
    } catch (DataSheetException e) {
      throw new MCProblemException("Error reading datasheet", e);
    }
  }
  
  /**
   * Parses the text size parameters for the specified question.
   * @param questionNr the number of the question
   * @return an int array where the first value is the number of rows and the second value is the number of columns
   */
  public int[] parseTextSizeParameters(int questionNr) {
    String typeString = getProblemString(QUESTION_PREFIX + questionNr + PATH_SEPARATOR + QUESTION_TYPE_SUBPATH);
    int[] result = new int[] { 1, 40};
    int paranthesisOpen = typeString.indexOf('(');
    if(paranthesisOpen == -1)
      return result;
    int paranthesisClose = typeString.indexOf(')');
    int commaIndex = typeString.indexOf(',');
    if(commaIndex == -1) {
      result[0] = Integer.parseInt(typeString.substring(paranthesisOpen + 1, paranthesisClose));
      return result;
    }
    result[0] = Integer.parseInt(typeString.substring(paranthesisOpen + 1, commaIndex));
    result[1] = Integer.parseInt(typeString.substring(commaIndex + 1, paranthesisClose));
    return result;
  }
	
	/**
	 * Parses the user answer string in the question sheet under the specified path into a {@link Integer} and returns it.
	 * Returns <code>null</code> if no such path is found.
	 * @param path a path in the question sheet below the user answer path
	 * @return an instance of {@link Integer} or <code>null</code>
	 * @throws MCProblemException if a {@link DataSheetException} occurs (e.g. not a valid integer definition)
	 * @see DataSheet#getAsInteger(String)
	 */
	public Integer getIntegerAnswer(String path) {
  	try {
			return getQuestionSheet().getAsInteger(USER_ANSWER_PATH + PATH_SEPARATOR + path);
		} catch (DataSheetException e) {
			throw new MCProblemException("Error reading datasheet", e);
		}
	}
	
	/**
	 * Clears the answer sheet in order to initialize a new saving process.
	 * This method must be called before any answers will be saved.
	 */
	public void clearAnswerSheet() {
		m_AnswerSheet = XMLUtils.createDataSheet();
	}
	
	/**
	 * Allows to implement additional steps after saving all answers into the answer sheet.
	 * The default implementation does nothing.
	 */
	public void finishAnswerSheet() {}

	protected DataSheet getAnswerSheet() {
		return m_AnswerSheet;
	}

	/**
	 * Saves the value into the answer sheet under the specified path.
	 * @param path a path in the answer sheet below the user answer path
	 */
  public void setAnswer(String path, boolean value) {
  	getAnswerSheet().put(USER_ANSWER_PATH + PATH_SEPARATOR + path, value);
  }

	/**
	 * Saves the value into the answer sheet under the specified path.
	 * @param path a path in the answer sheet below the user answer path
	 */
  public void setAnswer(String path, int value) {
  	getAnswerSheet().put(USER_ANSWER_PATH + PATH_SEPARATOR + path, value);
  }
  
  /**
   * Saves the value into the answer sheet under the specified path.
   * @param path a path in the answer sheet below the user answer path
   */
  public void setAnswer(String path, String value) {
    getAnswerSheet().put(USER_ANSWER_PATH + PATH_SEPARATOR + path, value);
  }
  
  /**
   * Sets the "edited" flag in the answer sheet to "true" to indicate that the exercise was saved.
   */
  protected void setEditedFlag() {
  	getAnswerSheet().put(USER_ANSWER_PATH + PATH_SEPARATOR + EDITED_FLAG_SUBPATH, MCProblemConstants.SOLUTION_TRUE_TYPE_NAME);
  }
}