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

import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.ProblemCorrectionException;
import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.util.MString;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.marking.SubtaskNode;
import net.mumie.mathletfactory.util.xml.marking.SubtaskSubNode;

/**
 * This class is the base for all generic and custom MC-Problem-Correctors.
 * Sub-classing correctors must only implement the method {@link #computeSolution(int, int)}.
 * 
 * @see #computeSolution(int, int)
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MCProblemCorrector implements ProblemCorrector, MCProblemConstants {

	/** Field holding the worker instance. */
	private MCCorrectorExercise m_exercise;
	
	public final void correct(CocoonEnabledDataSheet inputDS, CocoonEnabledDataSheet outputDS) throws ProblemCorrectionException {
		try {
			m_exercise = new MCCorrectorExercise(inputDS, outputDS);
			double score = correctAnswers() / getQuestionCount();
			getExercise().setScore(score);
			getExercise().finish();
		} catch(Throwable t) {
			throw new ProblemCorrectionException("Error occurred while invoking MC corrector", t);
		}
	}
  
  /**
   * Corrects the answers for all questions and returns the score.
   */
  protected double correctAnswers() {
    double score = 0;
    for(int q = 1; q <= getQuestionCount(); q++) {
      score += correctAnswers(q);
    }
    return score;
  }
  
  /**
   * Corrects the answers for the given question and returns the score for it.
   */
  protected double correctAnswers(int questionNr) {
    double score = 0;
    int answerType = getExercise().getAnswerType(questionNr);
    switch(answerType) {
    case UNIQUE_ANSWER_TYPE:
      score = adjustScore(correctUniqueAnswer(questionNr));
      break;
    case MULTIPLE_ANSWER_TYPE:
      score = adjustScore(correctMultipleAnswers(questionNr));
      break;
    case YESNO_ANSWER_TYPE:
      score = adjustScore(correctYesNoAnswers(questionNr));
      break;
    case TEXT_ANSWER_TYPE:
      score = adjustScore(correctTextAnswer(questionNr));
      break;
    }
    return score;
  }
	
  /**
   * Returns the total number of questions.
   */
  protected int getQuestionCount() {
    return getExercise().getQuestionCount();
  }
  
	/**
	 * Returns an adjusted value of the given score (i.e. inside [0, 1] ).
	 */
	private double adjustScore(double score) {
		if(score > 1)
			return 1;
		if(score < 0)
			return 0;
		return score;
	}
	
	/**
	 * Computes and returns the solution for the given question and answer by loading the solution from the datasheet and
	 * possibly by computing a relation.
	 * This method is intented to be subclassed by custom MC-Problem-Correctors for all or only some questions.
	 * @param questionNr the number of the specified question
	 * @param answerNr the number of the specified answer
	 * @return
	 */
	protected boolean computeSolution(int questionNr, int answerNr) {
		int solution = getExercise().loadSolution(questionNr, answerNr);
		if(solution == SOLUTION_COMPUTE)
			return computeGenericSolution(questionNr, answerNr);
		if(solution == SOLUTION_TRUE)
			return true;
		if(solution == SOLUTION_FALSE)
			return false;
		throw new MCProblemException("Unknown solution type for answer " + answerNr + " in question " + questionNr);
	}
	
	/**
	 * Computes and returns the generic solution for the given question and answer by evaluating the solution's relation.
	 * @param questionNr the number of the specified question
	 * @param answerNr the number of the specified answer
	 * @return
	 */
	private boolean computeGenericSolution(int questionNr, int answerNr) {
		Relation relation = getExercise().loadRelation(questionNr, answerNr);
		relation.prepareEvaluateFast();
		return relation.evaluateFast(new double[0]);
	}
	
	/**
	 * Evaluates the "unique" answers of the specified question and returns the score for this question.
	 * @param questionNr the number of the specified question
	 * @return a number >=0 and <= 1
	 */
	private double correctUniqueAnswer(int questionNr) {
		double score = -1;
		Boolean[] allAnswers = new Boolean[getExercise().getAnswerCount(questionNr)];
		Boolean[] allSolutions = new Boolean[allAnswers.length];
		for(int a = 0; a < allSolutions.length; a++) {
			allAnswers[a] = new Boolean(false);
			allSolutions[a] = new Boolean(computeSolution(questionNr, a + 1));
		}
		Integer uniqueAnswer = getExercise().getIntegerAnswer(QUESTION_PREFIX + questionNr);
		// only correct edited answer (i.e. != null)
		if(uniqueAnswer != null && uniqueAnswer.intValue() != -1) {
			boolean solution = allSolutions[uniqueAnswer.intValue() - 1].booleanValue();
			score = (solution == true ? 1 : 0);
			allAnswers[uniqueAnswer.intValue() - 1] = new Boolean(true);
		}
		addMarking(questionNr, score, allAnswers, allSolutions);
		return score;
	}
	
	/**
	 * Evaluates the "multiple" answers of the specified question and returns the score for this question.
	 * @param questionNr the number of the specified question
	 * @return a number >=0 and <= 1
	 */
	private double correctMultipleAnswers(int questionNr) {
		int answerCount = getExercise().getAnswerCount(questionNr);
		Boolean[] allAnswers = new Boolean[answerCount];
		Boolean[] allSolutions = new Boolean[answerCount];
		int trueSolutionCount = 0, correctTrueAnswerCount = 0, incorrectTrueAnswerCount = 0;
		for(int a = 1; a <= answerCount; a++) {
			Boolean answer = getExercise().getBooleanAnswer(QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
					+ ANSWER_PREFIX + a);
			boolean solution = computeSolution(questionNr, a);
			allSolutions[a - 1] = new Boolean(solution);
			// only selected answers are saved
			if(answer == null)
				answer = new Boolean(false);
			allAnswers[a - 1] = answer;
			// test if a TRUE solution is contained
			if(solution == true)
				trueSolutionCount++;
			// compare answer with solution
			if(answer.equals(new Boolean(true)))
				if(solution)correctTrueAnswerCount++;
				else incorrectTrueAnswerCount++;
		}
		// convention: at least 1 TRUE solution must be defined
		if(trueSolutionCount == 0)
			throw new MCProblemException("No TRUE solution defined for question " + questionNr);
		// calculate score
		double score;
		//prohibit points for unedited question with accidentally correct answers
		if(getExercise().wasEdited() == false)
			score = -1;
		else {
			// score :=	(#hit - (#false alarm / (#total_false + 1) ) ) / #total_true
			score = correctTrueAnswerCount;
			score -= (double)incorrectTrueAnswerCount / (answerCount - trueSolutionCount + 1);
			score /= (double)trueSolutionCount;
		}
		addMarking(questionNr, score, allAnswers, allSolutions);
		return score;
	}
  
  /**
   * Evaluates the "text" answer of the specified question and returns the score for this question.
   * @param questionNr the number of the specified question
   * @return a number >=0 and <= 1
   */
  protected double correctTextAnswer(int questionNr) {
    throw new MCProblemException("Method must be implemented in problem corrector for question " + questionNr);
  }
	
	/**
	 * Evaluates the "yesno" answers of the specified question and returns the score for this question.
	 * @param questionNr the number of the specified question
	 * @return a number >=0 and <= 1
	 */
	private double correctYesNoAnswers(int questionNr) {
		double score = 0;
		double amount = 1.0 / getExercise().getAnswerCount(questionNr);
		boolean edited = false;
		Boolean[] allAnswers = new Boolean[getExercise().getAnswerCount(questionNr)];
		Boolean[] allSolutions = new Boolean[getExercise().getAnswerCount(questionNr)];
		for(int a = 1; a <= getExercise().getAnswerCount(questionNr); a++) {
			Boolean answer = getExercise().getBooleanAnswer(QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
					+ ANSWER_PREFIX + a);
			boolean solution = computeSolution(questionNr, a);
			allSolutions[a - 1] = new Boolean(solution);
			// only edited answers are saved
			if(answer != null) {
				edited = true;
				allAnswers[a - 1] = answer;
				if(new Boolean(solution).equals(answer))
					score += amount;
			}
		}
		if(edited == false)
			score = -1;
		addMarking(questionNr, score, allAnswers, allSolutions);
		return score;
	}
	
  /**
   * Adds the answer marking for the specified question depending on the given score.
   * @param questionNr the number of the specified question
   * @param score the score
   */
  protected void addAnswerMarking(int questionNr, double score) {
    SubtaskNode n = getSubtaskNode(questionNr);
    n.getAnswerNode().addParagraphNode(" ");
    if(score != -1) {
      if(isFullScore(score)) {
        n.getAnswerNode().addParagraphNode(getExercise().preparseText("Richtig gel\"ost !"));
      } else if(score > 0) {
        n.getAnswerNode().addParagraphNode(getExercise().preparseText("Teilweise richtig gel\"ost !"));
      } else {
        n.getAnswerNode().addParagraphNode(getExercise().preparseText("Falsch gel\"ost !"));
      }
    } else {
      n.getAnswerNode().addParagraphNode(getExercise().preparseText("Nicht bearbeitet."));
    }
  }
  
  /**
   * Adds a given answer text to the marking of the specified question.
   * @param questionNr the number of the specified question
   * @param answerNr the number of the specified answer
   * @param answer a text
   */
  protected void addAnswer(int questionNr, int answerNr, String answer) {
    SubtaskNode n = getSubtaskNode(questionNr);
    addText(n.getAnswerNode(), getExercise().parseText(answer, questionNr, answerNr));
  }
  
  /**
   * Adds a given solution text to the marking of the specified question.
   * @param questionNr the number of the specified question
   * @param answerNr the number of the specified answer
   * @param solution a text
   */
  protected void addSolution(int questionNr, int answerNr, String solution) {
    SubtaskNode n = getSubtaskNode(questionNr);
    addText(n.getSolutionNode(), getExercise().parseText(solution, questionNr, answerNr));
  }
  
  /**
   * Adds a given explanation text to the marking of the specified question.
   * @param questionNr the number of the specified question
   * @param answerNr the number of the specified answer
   * @param explanation a text
   */
  protected void addExplanation(int questionNr, int answerNr, String explanation) {
    SubtaskNode n = getSubtaskNode(questionNr);
    addText(n.getExplanationNode(), getExercise().parseText(explanation, questionNr, answerNr));
  }
  
	/**
	 * Adds the marking for the specified question depending on the given score, including the given answers and solutions.
	 * @param questionNr the number of the specified question
	 * @param score the score
	 * @param answers a list of answers
	 * @param solutions a list of solutions
	 */
	protected void addMarking(int questionNr, double score, Boolean[] answers, Boolean[] solutions) {
		SubtaskNode n = getSubtaskNode(questionNr);
    // add question task to marking
		addText(n.getAnswerNode(), getTask(questionNr));
    // add answers to marking
		if(score != -1) { // only print user answers if edited
//			n.getAnswerNode().addParagraphNode(getExercise().preparseText("Ihre Antworten sind:"));
      n.getAnswerNode().addParagraphNode(" ");
			addAnswers(questionNr, answers, n.getAnswerNode());
		}
    addAnswerMarking(questionNr, score);
    // add solutions to marking
		if(isFullScore(score)) {
			n.getSolutionNode().addParagraphNode(getExercise().preparseText("siehe links !"));
		} else {
//			n.getSolutionNode().addParagraphNode(getExercise().preparseText("Die richtigen L\"osungen sind:"));
			addAnswers(questionNr, solutions, n.getSolutionNode());
			// add common explanation
      addText(n.getExplanationNode(), getExplanation(questionNr));
      // add answer explanations (only for wrong answers)
      for(int i = 0; i < solutions.length; i++) {
        if(answers[i] == null || !answers[i].equals(solutions[i]))
          addText(n.getExplanationNode(), getExplanation(questionNr, i + 1));
      }
		}
	}
  
  /**
   * Returns the marking's subtask node for the given question.
   */
  protected SubtaskNode getSubtaskNode(int questionNr) {
    return getExercise().getMarkingDocBuilder().getSubtaskNode(questionNr);
  }
	
  /**
   * Adds the marking for the specified question depending on the given score, including the given answers and solutions.
   * @param questionNr the number of the specified question
   * @param score the score
   * @param answers a list of answers
   * @param solutions a list of solutions
   */
  protected void addTextMarking(int questionNr, double score, String[] solutions) {
    String answer = getExercise().getTextAnswer(questionNr);
    SubtaskNode n = getSubtaskNode(questionNr);
    // add question task to marking
    addText(n.getAnswerNode(), getTask(questionNr));
    // add answers to marking
    if(score != -1 && answer != null) { // only print user answers if edited
//      n.getAnswerNode().addParagraphNode(getExercise().preparseText("Ihre Antwort war:"));
      n.getAnswerNode().addParagraphNode(" ");
      addAnswer(questionNr, 1, answer);
    }
    addAnswerMarking(questionNr, score);
    // add solutions to marking
    if(isFullScore(score)) {
      n.getSolutionNode().addParagraphNode(getExercise().preparseText("siehe links !"));
    } else {
//      if(solutions.length > 1)
//        n.getSolutionNode().addParagraphNode(getExercise().preparseText("Die richtigen L\"osungen sind:"));
//      else
//        n.getSolutionNode().addParagraphNode(getExercise().preparseText("Die richtige L\"osung ist:"));
      for(int i = 0; i < solutions.length; i++)
        addSolution(questionNr, 1, solutions[i]);
    }
  }
  
  /**
   * Adds the given text to the given subtask node. This method removes any line break definitions
   * by placing the following content in a new line.
   */
  protected void addText(SubtaskSubNode n, String text) {
    addText(n, new MString[] { new MString(text) });
  }
  
	/**
	 * Adds the given text as MathML-objects to the given subtask node. This method removes any line break definitions
	 * by placing the following content in a new line.
	 */
	private void addText(SubtaskSubNode n, MathMLSerializable[] text) {
    if(text == null)
      return;
		int lastLineBreak = 0;
		for(int i = 0; i < text.length; i++) {
			// search for line breaks and add content before it
			if(text[i] instanceof MString && ((MString) text[i]).getValue().equals("\\\\")) {
				ExerciseObjectIF[] lineTask = new ExerciseObjectIF[i - lastLineBreak];
				System.arraycopy(text, lastLineBreak, lineTask, 0, lineTask.length);
				n.addMathObjects(lineTask);
				lastLineBreak = i + 1;
			}
		}
		// add text behind last line break
		if(lastLineBreak < text.length) {
			ExerciseObjectIF[] lineTask = new ExerciseObjectIF[text.length - lastLineBreak];
			System.arraycopy(text, lastLineBreak, lineTask, 0, lineTask.length);
			n.addMathObjects(lineTask);
		// no line breaks at all contained in text
		} else if(lastLineBreak == 0) {
			n.addMathObjects(text);
		}
	}
	
  /**
   * Returns if the given amount of points is the maximum score.
   * This method uses an epsilon comparision.
   * @param score a number between 0 and 1
   */
	protected boolean isFullScore(double score) {
		return Math.abs(score - 1) <= 1e-6; // compare using epsilon
	}
	
	/**
	 * Adds the assertions for the specified question to the given substask sub-node.
	 * The answers are contained in the given list, where unedited entries are <code>null</code>.
	 * @param questionNr the question's number
	 * @param answers a list of all answers
	 * @param n a substask sub-node in the marking (e.g. answer or solution node)
	 * @see net.mumie.mathletfactory.util.xml.marking.SubtaskAnswerNode
	 * @see net.mumie.mathletfactory.util.xml.marking.SubtaskSolutionNode
	 */
	protected void addAnswers(int questionNr, Boolean[] answers, SubtaskSubNode n) {
		for(int i = 0; i < getAssertionCount(questionNr); i++) {
			MathMLSerializable[] tmp = getAssertion(questionNr, i + 1);
			MathMLSerializable[] answer = new MathMLSerializable[tmp.length + 1];
			System.arraycopy(tmp, 0, answer, 0, tmp.length);
			answer[tmp.length] = new MString(": " + getBooleanAsText(answers[i]));
			addText(n, answer);
		}
	}
	
	/**
	 * Returns the question task for the specified question as a list of MathML objects.
	 * This method is intented to be subclassed by custom MC-Problem-Correctors for all or only some questions.
	 * @param questionNr the question's number
	 * @return the question task as a list of MathML-capable objects
	 */
	protected ExerciseObjectIF[] getTask(int questionNr) {
		return getExercise().parseText(getExercise().loadQuestionTask(questionNr), questionNr, -1);
	}
	
	/**
	 * Returns the specified assertion as a list of MathML objects for the specified question.
	 * This method is intented to be subclassed by custom MC-Problem-Correctors for all or only some questions.
	 * @param questionNr the question's number
   * @param answerNr the number of the specified answer
	 * @return the assertion texts as a list of MathML objects
	 */
	protected ExerciseObjectIF[] getAssertion(int questionNr, int answerNr) {
		return getExercise().parseText(getExercise().loadAssertion(questionNr, answerNr), questionNr, answerNr);
	}
	
	/**
	 * Returns the number of assertions for the specified question.
	 * This method is intented to be subclassed by custom MC-Problem-Correctors for all or only some questions.
	 * @param questionNr the question's number
	 */
	protected int getAssertionCount(int questionNr) {
		return getExercise().getAnswerCount(questionNr);
	}
	
  /**
   * Returns the explanation for the specified question as a list of MathML objects.
   * This method is intented to be subclassed by custom MC-Problem-Correctors for all or only some questions.
   * @param questionNr the question's number
   * @return the explanation as a list of MathML-capable objects
   */
  protected ExerciseObjectIF[] getExplanation(int questionNr) {
    String explanation = getExercise().loadExplanation(questionNr);
    if(explanation == null)
      return null;
    return getExercise().parseText(explanation, questionNr, -1);
  }

  /**
   * Returns the explanation for the specified question and answer as a list of MathML objects.
   * This method is intented to be subclassed by custom MC-Problem-Correctors for all or only some questions.
   * @param questionNr the question's number
   * @param answerNr the number of the specified answer
   * @return the explanation as a list of MathML-capable objects
   */
  protected ExerciseObjectIF[] getExplanation(int questionNr, int answerNr) {
    String explanation = getExercise().loadExplanation(questionNr, answerNr);
    if(explanation == null)
      return null;
    return getExercise().parseText(explanation, questionNr, answerNr);
  }
  
	/**
	 * Returns a string representing the given boolean value.
	 * @param b an instance of {@link Boolean} or <code>null</code>
	 * @return a string representing the boolean value
	 */
	protected String getBooleanAsText(Boolean b) {
		if(b == null)
			return "nicht bearbeitet";
		if(b.booleanValue() == true)
			return "ja";
		return "nein";
	}
	
	/**
	 * Returns the worker instance.
	 */
	protected MCCorrectorExercise getExercise() {
		return m_exercise;
	}
}
