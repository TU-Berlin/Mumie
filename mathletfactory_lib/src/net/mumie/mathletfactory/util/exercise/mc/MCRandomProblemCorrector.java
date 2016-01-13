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

import net.mumie.mathletfactory.util.xml.marking.SubtaskNode;

/**
 * This class is the base for all generic and custom MC-Random-Problem-Correctors.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MCRandomProblemCorrector extends MCProblemCorrector {

  private int[] m_randomQuestions;
  
  /* Returns the size of the list. */
  protected int getQuestionCount() {
    return m_randomQuestions.length;
  }
  
  protected double correctAnswers() {
    if(m_randomQuestions == null)
      m_randomQuestions = getExercise().loadQuestionSelections();
    double score = 0;
    for(int i = 0; i < m_randomQuestions.length; i++) {
      score += correctAnswers(m_randomQuestions[i]);
    }
    return score;
  }
  
  protected SubtaskNode getSubtaskNode(int questionNr) {
    for(int i = 0; i < m_randomQuestions.length; i++) {
      if(m_randomQuestions[i] == questionNr)
        return super.getSubtaskNode(i + 1);
    }
    // should never be called
    throw new MCProblemException("Question number \"" + questionNr + "\" not contained in number list!");
  }
  
  protected void addAnswerMarking(int questionNr, double score) {
    SubtaskNode n = getSubtaskNode(questionNr);
    int realQuestionNr = -1;
    for(int i = 0; i < m_randomQuestions.length; i++) {
      if(m_randomQuestions[i] == questionNr) {
        realQuestionNr = i + 1;
        break;
      }
    }
    if(realQuestionNr == -1)
      throw new MCProblemException("Question number \"" + questionNr + "\" not contained in number list!");
    if(score != -1) {
      if(isFullScore(score)) {
        n.getAnswerNode().addParagraphNode(getExercise().preparseText("Frage " + realQuestionNr + " richtig gel\"ost !"));
      } else if(score > 0) {
        n.getAnswerNode().addParagraphNode(getExercise().preparseText("Frage " + realQuestionNr + " teilweise richtig gel\"ost !"));
      } else {
        n.getAnswerNode().addParagraphNode(getExercise().preparseText("Frage " + realQuestionNr + " falsch gel\"ost !"));
      }
    } else {
      n.getAnswerNode().addParagraphNode(getExercise().preparseText("Frage " + realQuestionNr + " nicht bearbeitet."));
    }
  }
}
