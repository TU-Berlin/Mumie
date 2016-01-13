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

import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.japs.datasheet.DataSheetException;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;
import net.mumie.mathletfactory.util.xml.marking.MarkingDocBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is used to generate datasheet content for corrector-classes
 * in the server-environment JAPS.
 * It encapsulates the score and marking functionalities.
 *
 * The score is a double value between 0 and 1 (both inclusive).
 *
 * The marking part consists of subtask-nodes which contain answer and solution nodes.
 *
 * <b>Note:</b>
 * At the end of {@link net.mumie.cocoon.util.ProblemCorrector#correct(net.mumie.cocoon.util.CocoonEnabledDataSheet, net.mumie.cocoon.util.CocoonEnabledDataSheet)}-method
 * implementations must be called {@link #finish()} to write the score and marking-content
 * into the datasheet.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class CorrectorHelper implements ExerciseConstants {

  private Document m_doc;
  private CocoonEnabledDataSheet m_inputDS;
  private CocoonEnabledDataSheet m_outputDS;
  private double m_score = 0;
  private MarkingDocBuilder m_builder;

  private Class m_numberClass;
  private int m_dim = -1;

  /**
   * Initializes a new helper instance for a given datasheet.
   * @param inputDS an instance of a particular datasheet-class for corrector-classes
   */
  public CorrectorHelper(CocoonEnabledDataSheet inputDS, CocoonEnabledDataSheet outputDS) {
    m_inputDS = inputDS;
    m_outputDS = outputDS;
    m_doc = outputDS.getDocument();
    m_builder = new MarkingDocBuilder(m_doc);
    try {
      m_numberClass = XMLUtils.getNumberClassFromField(inputDS
          .getAsString(FIELD_PATH, true));
      Integer dim = inputDS.getAsInteger(DIMENSION_PATH);
      if (dim != null)
        m_dim = dim.intValue();
    } catch (DataSheetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Notifies the helper to finish and to write the marking-content
   * into the outgoing corrector data sheet. Must be called at the end of
   * {@link net.mumie.cocoon.util.ProblemCorrector#correct(net.mumie.cocoon.util.CocoonEnabledDataSheet, net.mumie.cocoon.util.CocoonEnabledDataSheet)}-method implementations.
   */
  public void finish() {
    try {
      // insert unrounded score
      m_outputDS.put(MumieExercise.USER_SCORE_PATH, getScore());

      // insert marking node
      Node markingNode =
        m_doc.importNode(m_builder.getMarkingNode(), true).cloneNode(true);
      XMLUtils.replaceNode(markingNode, XMLUtils.createUneditedNode(m_doc), XMLUtils.createEmptyMarkingNode(m_doc));
      m_outputDS.put(MarkingDocBuilder.USER_MARKING_PATH, markingNode);
      m_outputDS.validate();
      m_outputDS.indent();
    } catch(DataSheetException dse) {
      System.err.println(dse);
    }
  }

  /**
   * Returns the current instance of a builder creating the marking-part
   * of an exercise datasheet.
   */
  public MarkingDocBuilder getMarkingDocBuilder() {
    return m_builder;
  }

  /**
   * Sets the score to <code>score</code>.
   *
   * @param score
   *          a double value between 0 and 1
   */
  public void setScore(double score) {
    m_score = score;
  }

  /**
   * Adds an amount of points to the actual score. The resulting score must be
   * >=0 and <= 1!
   *
   * @param scoreToAdd
   *          a double value between 0 and 1
   */
  public void addToScore(double scoreToAdd) {
    m_score += scoreToAdd;
  }

  /**
   * Returns the current score.
   * Values outside the range are rounded to min (0) or max (1).
   */
  public double getScore() {
    if(m_score < 0)
      return 0;
    if(m_score > 1)
      return 1;
    return m_score;
  }

  /**
   * Returns the element with the specified path from the answer sheet.
   */
  public Element getElement(String path) {
    try {
      return m_inputDS.getAsElement(path);
    } catch (DataSheetException e) {
      return null;
    }
  }


  /**
   * Returns the element with the specified user path from the answer sheet.
   * @param path a path under "user/answer/subtask_[subtaskIndex]"
   */
  public Element getUserElement(int subtaskIndex, String path) {
    return getElement(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path);
  }

  /**
   * Returns the element with the specified problem path from the answer sheet.
   * @param path a path under "common/problem"
   */
  public Element getProblemElement(String path) {
    return getElement(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path);
  }

  /**
   * Loads the element with the specified path from the data sheet and saves
   * the node into the given MMObject.
   *
   * @param path
   *          a path in the answer datasheet
   */
  public void loadElement(String path, MathMLSerializable mmobject) {
    if(mmobject == null)
      throw new NullPointerException("MMObject must not be null!");
    try {
      Element e = m_inputDS.getAsElement(path);
      if (e != null)
        mmobject.setMathMLNode(e);
      else
        throw new NullPointerException();
    } catch (DataSheetException e) {
      System.err.println("Loading datasheet-element \"" + path
          + "\" failed: " + e);
    } catch(NullPointerException npex) {
      System.err.println("Loading datasheet-element \"" + path
          + "\" failed, invalid path.");
    }
  }

  /**
   * Loads the user answer element with the specified path from the answer sheet and saves the node into
   * the given MMObject.
   * @param path a path under "user/answer/subtask_[subtaskIndex]"
   */
  public void loadUserElement(int subtaskIndex, String path, MathMLSerializable mmobject) {
    loadElement(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path, mmobject);
  }

  /**
   * Loads the element with the specified problem path from the answer sheet and saves the node into
   * the given MMObject.
   * @param path a path under "common/problem"
   */
  public void loadProblemElement(String path, MathMLSerializable mmobject) {
    loadElement(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path, mmobject);
  }

  /**
   * Marks a subtask to be correct. Sets the data
   * <code>/user/meta/answer/subtask_[subtaskIndex]/correct</code> to
   * <code>yes</code> or <code>no</code>.
   */
  public void setAnswerCorrect(int subtaskIndex, boolean correct) {
    m_outputDS.put(MumieExercise.USER_META_ANSWER_PATH + MumieExercise.PATH_SEPARATOR + MumieExercise.SUBTASK_PREFIX
        + subtaskIndex + "/correct", (correct) ? "yes" : "no");
  }

  public String getString(String path) {
    try {
      return m_inputDS.getAsString(path);
    } catch (DataSheetException e) {
      return null;
    }
  }

  public String getUserString(int subtaskIndex, String path) {
    return getString(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path);
  }

  public String getProblemString(String path) {
    return getString(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path);
  }

  /**
   * Checks if an element with the specified path exists in the answer sheet.
   */
  public boolean elementExists(String path) {
    Element e = m_inputDS.getDataElement(path);
    return e != null ? true : false;
  }

  /**
   * Checks if the user element with the specified subpath exists for the given subtask in the answer sheet.
   */
  public boolean userElementExists(int subtaskIndex, String path) {
    return elementExists(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path);
  }

  /**
   * Checks if the problem element with the specified subpath exists for the given subtask in the answer sheet.
   */
  public boolean problemElementExists(String path) {
    return elementExists(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path);
  }

  /**
   * Returns the parsed <code>common/problem/field</code> as a class. May
   * return null if this data is not found.
   */
  public Class getNumberClass() {
    if (m_numberClass == null)
      System.err.println("No value set in datasheet for " + FIELD_PATH);
    return m_numberClass;
  }

  /**
   * Returns the parsed <code>common/problem/dimension</code> as an integer.
   * May return -1 if this data is not found.
   */
  public int getDimension() {
    if (m_dim == -1)
      System.err.println("No value set in datasheet for "
          + DIMENSION_PATH);
    return m_dim;
  }
}
