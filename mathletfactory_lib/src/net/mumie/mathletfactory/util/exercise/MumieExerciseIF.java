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

import net.mumie.mathletfactory.appletskeleton.MathletContext;

/**
 * This interface describes a basic exercise mathlet allowing to check
 * and collect the entered data and to reset the current (or only) subtask.
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public interface MumieExerciseIF extends MathletContext {

  /**
   * Notifies the applet to check the entered data and to return <code>false</code>
   * if the send process of the results should be canceled or to return <code>true</code>
   * if all necessary data has been entered.
   * The answers should be saved into the answer sheet.
   */
  boolean collectAnswers();

  /**
   * Notifies the applet to clear the results of the current (or only) sub task.
   */
  void clearSubtask();
  
  /**
   * Notifies the exercise to save the answers.
   */
  void save();
}
