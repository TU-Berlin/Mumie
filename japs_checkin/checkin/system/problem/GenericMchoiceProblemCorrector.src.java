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

package net.mumie.corrector;

import java.util.ArrayList;
import java.util.List;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.ProblemCorrectionException;
import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.japs.datasheet.DataSheetException;

/**
 * Generic corrector for multiple choice problems.
 *
 * @author Tilman Rassy
 *   <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 *   (org/users/usr_tilman.meta.xml)
 * @version
 *   <code>$Id: GenericMchoiceProblemCorrector.src.java,v 1.3 2008/03/18 10:55:37 rassy Exp $</code>
 *
 * @mm.type java_class
 * @mm.category corrector
 * @mm.section system/problem
 * @mm.copyright Copyright (c) 2005 -2007, Technische Universitaet Berlin
 * @mm.buildClasspath mumie-japs-for-mmcdk.jar
 * @mm.buildClasspath mumie-japs-datasheet.jar
 * @mm.buildClasspath excalibur-xmlutil-2.1.jar
 * @mm.description Generischer Korrektor fuer Multiple-Choice-Aufgaben
 * @mm.changelog Erste Version
 */

public class GenericMchoiceProblemCorrector
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
   * The datasheet path <code>"user/problem"</code> as a constant.
   */

  static final String USER_PROBLEM = "user/problem";

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
    throws DataSheetException 
  {
    List<String> keys = new ArrayList<String>();
    final String PREFIX = COMMON_PROBLEM + "/choices-";
    for (String path : input.getMatchingDataPaths(PREFIX + "*/type"))
      {
        String key = path.substring(PREFIX.length(), path.length() - "/type".length());
        if ( input.getAsBoolean(USER_PROBLEM + "/choices-" + key  + "/selected", true) )
          keys.add(key);
      }
    return keys.toArray(new String[keys.size()]);
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
   * Corrects the questions specified by <code>choicesKey</code> in the input data sheet
   * <code>input</code>. Returns an int array with two elements: element 0 contains the
   * user's score, element 1 the maximum score.
   */

  protected int[] correct (CocoonEnabledDataSheet input,
                           String choicesKey)
    throws DataSheetException 
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
            String solution = input.getAsString
              (COMMON_SOLUTION + "/choices-" + choicesKey + "/choice-" + choiceKey);
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
            String solution = input.getAsString
              (COMMON_SOLUTION + "/choices-" + choicesKey + "/choice-" + choiceKey);
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
            String solution = input.getAsString
              (COMMON_SOLUTION + "/choices-" + choicesKey + "/choice-" + choiceKey);
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
            int[] result = this.correct(input, choicesKey);
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
