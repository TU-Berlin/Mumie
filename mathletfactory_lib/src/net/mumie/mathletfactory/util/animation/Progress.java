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

package net.mumie.mathletfactory.util.animation;

/**
 * @author gronau
 * @mm.docstatus outstanding
 */
public class Progress {
  
  private double m_progressRate;
  private boolean m_isFirstCall;
  private int m_callCount;
  
  public Progress(double progressRate, int callCount) {
    m_progressRate = progressRate;
    m_callCount = callCount;
  }
  
  public void setProgressRate(double progress) {
    m_progressRate = progress;
  }
  
  public double getProgressRate() {
    return m_progressRate;
  }
  
  public boolean isFirstCall() {
    return m_isFirstCall;
  }
  
  public void setFirstCall(boolean first) {
    m_isFirstCall = first;
  }
  
  public void setCallCount(int callCount) {
    m_callCount = callCount;
  }
  
  public int getCallCount() {
    return m_callCount;
  }
}
