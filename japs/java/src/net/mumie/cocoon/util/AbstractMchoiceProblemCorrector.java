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

package net.mumie.cocoon.util;

import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.ProblemCorrectionException;
import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.japs.datasheet.DataSheetException;

/**
 * Abstract base class for multiple choice problem corrector.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AbstractMchoiceProblemCorrector.java,v 1.4 2007/07/11 15:38:52 grudzin Exp $</code>
 */

public abstract class AbstractMchoiceProblemCorrector
  implements ProblemCorrector
{
  /**
   * The datasheet path <code>"common/solution"</code> as a constant.
   */

  static final String COMMON_SOLUTION = "common/solution";

  /**
   * The datasheet path <code>"user/answer"</code> as a constant.
   */

  static final String USER_ANSWER = "user/answer";

  /**
   * The datasheet path <code>"user/meta/score"</code> as a constant.
   */

  static final String USER_META_SCORE = "user/meta/score";

  /**
   * The datasheet path <code>"user/meta/correction-log"</code> as a constant.
   */

  static final String USER_META_CORRECTIONLOG = "user/meta/correction-log";

  /**
   * The datasheet path <code>"common/problem"</code> as a constant.
   */

  static final String COMMON_PROBLEM = "common/problem";

  /**
   * The string <code>"yesno"</code> (multiple choice question type) as a constant.
   */

  static final String YESNO = "yesno";

  /**
   * The string <code>"unique"</code> (multiple choice question type)as a constant.
   */

  static final String UNIQUE = "unique";

  /**
   * The string type <code>"multiple"</code> (multiple choice question) as a constant.
   */

  static final String MULTIPLE = "multiple";

  /**
   * The string <code>"true"</code> as a constant
   */

  static final String TRUE = "true";

  /**
   * The string <code>"false"</code> as a constant
   */

  static final String FALSE = "false";

  /**
   * The string <code>"compute"</code> as a constant
   */

  static final String COMPUTE = "compute";

  /**
   * Index of the user's score in the array returned by
   * {@link #correct(CocoonEnabledDataSheet,String) correct(input, unit}.
   */

  static final int SCORE = 0;

  /**
   * Index of the maximum score in the array returned by
   * {@link #correct(CocoonEnabledDataSheet,String) correct(input, unit}.
   */

  static final int MAX_SCORE = 1;

  /**
   * Returns all choices keys in the specified data sheet.
   */

  protected String[] getChoicesKeys (CocoonEnabledDataSheet input)
  {
    final String PREFIX = COMMON_PROBLEM + "/choices-";
    String[] keys = input.getMatchingDataPaths(PREFIX + "*/type");
    for (int i = 0; i < keys.length; i++)
      keys[i] = keys[i].substring(PREFIX.length(), keys[i].length()-"/type".length());
    return keys;
  }

  /**
   * Returns all choice keys for <code>choicesKey</code> in the specified unit of the
   * specified data sheet.
   */

  protected String[] getChoiceKeys (CocoonEnabledDataSheet input,
                                    String unit,
                                    String choicesKey)
  {
    final String PREFIX = unit + "/choices-" + choicesKey + "/choice-";
    String[] keys = input.getMatchingDataPaths(PREFIX + "*");
    for (int i = 0; i < keys.length; i++)
      keys[i] = keys[i].substring(PREFIX.length());
    return keys;
  }

  /**
   * Computes the correct solution for the question with the specified
   * <code>choicesKey</code> and <code>choiceKey</code>.
   */

  protected abstract String computeSolution(CocoonEnabledDataSheet input, 
                                            CocoonEnabledDataSheet output,
                                            String choicesKey,
                                            String choiceKey)
    throws ProblemCorrectionException, DataSheetException ;

  /**
   * <p>
   *   Returns the solution for the question with the specified <code>choicesKey</code> and
   *   <code>choiceKey</code>. This is done as follows:
   * </p>
   * <ol>
   *   <li>
   *     The datasheet entry with the path
   *     <code>"common/solution/choices-<var>choicesKey</var>/choice-<var>choiceKey</var>"</code>
   *     is read. According to the specification, this entry must be <code>"true"</code>,
   *     <code>"false"</code>, or <code>"compute"</code>.
   *   </li>
   *   <li>
   *     If the entry is <code>"true"</code> or <code>"false"</code>, that value is
   *     returned. If the entry is <code>"compute"</code>,
   *     {@link #computeSolution computeSolution} is called with the same arguments as this
   *     method. {@link #computeSolution computeSolution} should return <code>"true"</code> or
   *     <code>"false"</code>. That value is returned by this method.
   *   </li>
   * </ol>
   */

  protected String getSolution (CocoonEnabledDataSheet input, 
                                CocoonEnabledDataSheet output,
                                String choicesKey,
                                String choiceKey)
    throws ProblemCorrectionException, DataSheetException 
  {
    String solution = input.getAsString
      (COMMON_SOLUTION + "/choices-" + choicesKey + "/choice-" + choiceKey);
    if ( solution.equals(COMPUTE) )
      solution = this.computeSolution(input, output, choicesKey, choiceKey);
    if ( !( solution.equals(TRUE) && solution.equals(FALSE) ) )
      throw new IllegalStateException("Illegal solution keyword: " + solution);
    return solution;
  }

  /**
   * Corrects the questions specified by <code>choicesKey</code> in the input data sheet
   * <code>input</code>. Returns an int array with two elements: element 0 contains the
   * user's score, element 1 the maximum score.
   */

  protected int[] correct (CocoonEnabledDataSheet input,
                           CocoonEnabledDataSheet output,
                           String choicesKey)
    throws ProblemCorrectionException, DataSheetException 
  {
    String[] solutionChoiceKeys = 
      this.getChoiceKeys(input, COMMON_SOLUTION, choicesKey);

    String[] answerChoiceKeys = 
      this.getChoiceKeys(input, USER_ANSWER, choicesKey);

    String type =
      input.getAsString(COMMON_PROBLEM + "/choices-" + choicesKey + "/type");

    int maxScore = 0;
    int score = 0;

    if ( type.equals(YESNO) )
      {
        maxScore = solutionChoiceKeys.length;
        for (int i = 0; i < solutionChoiceKeys.length; i++)
          {
            String choiceKey = solutionChoiceKeys[i];
            String solution = this.getSolution(input, output, choicesKey, choiceKey);
            String answer = input.getAsString
              (USER_ANSWER + "/choices-" + choicesKey + "/choice-" + choiceKey);
            if ( answer != null && answer.equals(solution) )
              score++;
          }
      }
    else if ( type.equals(UNIQUE) )
      {
        maxScore = 1;
        String correctChoiceKey = null;
        for (int i = 0; correctChoiceKey == null && i < solutionChoiceKeys.length; i++)
          {
            String choiceKey = solutionChoiceKeys[i];
            String solution = this.getSolution(input, output, choicesKey, choiceKey);
            if ( solution.equals(TRUE) )
              correctChoiceKey = choiceKey;
          }
        String answer = input.getAsString
          (USER_ANSWER + "/choices-" + choicesKey + "/choice-" + correctChoiceKey);
        if ( answer != null && answer.equals(TRUE) )
          score = 1;
      }
    else if ( type.equals(MULTIPLE) )
      {
        maxScore = solutionChoiceKeys.length;
        for (int i = 0; i < solutionChoiceKeys.length; i++)
          {
            String choiceKey = solutionChoiceKeys[i];
            String solution = this.getSolution(input, output, choicesKey, choiceKey);
            String answer = input.getAsString
              (USER_ANSWER + "/choices-" + choicesKey + "/choice-" + choiceKey,
               FALSE);
            if ( answer.equals(solution) )
              score++;
            else
              score--;
          }
        if ( score < 0 ) score = 0;
      }

    return new int[] {score, maxScore};
  }

  /**
   * Corrects a multiple choice problem.
   */

  public void correct (CocoonEnabledDataSheet input,
                       CocoonEnabledDataSheet output)
    throws ProblemCorrectionException 
    
  {
    try
      {
        int score = 0;
        int maxScore = 0;
        StringBuffer log = new StringBuffer("\n");
        String[] choicesKeys = this.getChoicesKeys(input);
        for (int i = 0; i < choicesKeys.length; i++)
          {
            String choicesKey = choicesKeys[i];
            int[] result = this.correct(input, output, choicesKey);
            score += result[SCORE];
            maxScore += result[MAX_SCORE];
            log.append
              ("choices " + choicesKey + ": " +
               result[SCORE] + "/" + result[MAX_SCORE] + "\n");
          }
        double relScore = (double)score / (double)maxScore;
        output.put(USER_META_SCORE, relScore);
        output.put(USER_META_CORRECTIONLOG, log.toString());
      }
    catch (Exception exception)
      {
        throw new ProblemCorrectionException(exception);
      }
  }
}
