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


/**
 * This class is the base for all generic and custom MC-Random-Problem-Applets.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MCRandomProblemApplet extends MCProblemApplet {

  private int[] m_randomQuestions;
  
  /* Returns the size of the list. */
  protected int getQuestionCount() {
    if(m_randomQuestions == null)
      m_randomQuestions = getExercise().loadQuestionSelections();
    return m_randomQuestions.length;
  }
  
  protected void createQuestions(MCQuestionPanel[] panels) {
    for(int i = 0; i < m_randomQuestions.length; i++) {
      panels[i] = createQuestion(m_randomQuestions[i]);
      // add vertical space between 2 questions
      if(i + 1 < getQuestionCount())
        getMCContentPane().insertLineBreaks(2);
      // initialize question components
      initQuestion(m_randomQuestions[i]);
    }
  }
  
  protected MCQuestionPanel getQuestionPanel(int questionNr) {
    for(int i = 0; i < m_randomQuestions.length; i++) {
      if(m_randomQuestions[i] == questionNr)
        return super.getQuestionPanel(i + 1);
    }
    // should never be called
    throw new MCProblemException("Question number \"" + questionNr + "\" not contained in number list!");
  }
  
  protected void loadAnswers() {
    for(int i = 0; i < m_randomQuestions.length; i++) {
      loadAnswer(m_randomQuestions[i]);
    }
  }
  
  public boolean collectAnswers() {
    // clear and create "own" MC-Problem answer sheet
    getExercise().clearAnswerSheet();
    // save answers per question
    for(int i = 0; i < m_randomQuestions.length; i++) {
      collectAnswers(m_randomQuestions[i]);
    }
    // save answers to "real" answer sheet in MumieExercise
    getExercise().finishAnswerSheet();
    // no errors occured
    return true;
  }
  
  public void reset() {
    for(int i = 0; i < m_randomQuestions.length; i++) {
      getQuestionPanel(m_randomQuestions[i]).clearAnswers();
    }
  }
}
