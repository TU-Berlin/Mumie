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

package net.mumie.mathletfactory.mmobject.analysis.sequence;

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 * This class models a recursive sequence, i.e. a sequence that is defined
 * by an expression of the form <code>a_{n+1} = f(a_n,n)</code>. The initial
 * value a_1 (or a_0, a_2, etc. depending on {@link #getStartingIndex}) has the 
 * default value of 1 and can be set by {@link #setInitialValue}. 
 * The lower bound can be set as well. 
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMRecursiveSequenceDefByOp extends MMSequenceDefByOp {
  
  /**
   * The value for the initial sequence member (e.g. <code>a_1</code>), default is 1.
   */
  private MNumber m_initialValue;

  /**
   * The starting index of this sequence from which calculation starts. Default is 1.
   */  
  private int m_startingIndex = 1; 
    
  /**
   * Creates a recursive sequence with starting index and initial value 1 and 
   * the given expression of the sequence variable.
   */
  public MMRecursiveSequenceDefByOp(Class numberClass, String stringFunction) {
    this(new Operation(numberClass, stringFunction, true));
  }

  /**
   * Creates a recursive sequence with starting index and initial value 1 and 
   * the given operation containing an expression of the sequence variable.
   */
  public MMRecursiveSequenceDefByOp(Operation operation){
    super(operation);
    m_initialValue = NumberFactory.newInstance(operation.getNumberClass(),1);
  }

  public MNumber getSequenceValue(MNatural natural) {
    double n = natural.getDouble();
    MNumber k = NumberFactory.newInstance(getNumberClass()); 
    MNumber a_n = m_initialValue.copy();
    for(int _k=m_startingIndex;_k<=n;_k++){
      k.setDouble(_k);
      m_operation.assignValue("n", _k);
      m_operation.assignValue("a_n", a_n);
      m_operation.evaluate(k, a_n);
    }
    return a_n;
  }
  
  /**
   * Returns the starting index of this sequence from which calculation starts.
   */
  public int getStartingIndex() {
    return m_startingIndex;
  }

  /**
   * Sets the starting index of this sequence from which calculation starts.
   */
  public void setStartingIndex(int i) {
    m_startingIndex = i;
  }

  /**
   * Returns the value for <code>a_0</code>.
   */
  public MNumber getInitialValue() {
    return m_initialValue;
  }

  /**
   * Sets the value for <code>a_0</code>.
   */
  public void setInitialValue(MNumber number) {
    m_initialValue = number;
  }
}
