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

package net.mumie.mathletfactory.math.set.node;

import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This node models a leaf containing a set that is a "periodic interval": 
 *  {x | \exists n \in Z: x - n*p \in I} with a given Interval I and a given 
 *  period p.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class PeriodicIntervalSet extends SetNode {

  private Interval m_interval;

  private MNumber m_period;
  
  /**
   * Constructs the periodic interval from the given interval and period. 
   */
  public PeriodicIntervalSet(Interval interval, MNumber period) {
    super(period.getClass());
  }

  public PeriodicIntervalSet(Class numberClass){
    super(numberClass);
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.set.node.SetNode#contains(double)
   */
  public boolean contains(double aNumber) {
    return false;
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.set.node.SetNode#contains(net.mumie.mathletfactory.number.MNumber)
   */
  public boolean contains(MNumber aNumber) {
    while(aNumber.greaterThan(m_interval.getUpperBoundaryVal()))
      aNumber.sub(m_period);
    while(aNumber.lessThan(m_interval.getLowerBoundaryVal()))
      aNumber.add(m_period);
    return m_interval.contains(aNumber);
  }

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.set.node.SetNode#getFiniteBorelSet(net.mumie.mathletfactory.set.Interval, double)
   */
  public FiniteBorelSet getFiniteBorelSet(Interval boundingInterval, double eps) {
    return null;
  }

  public Interval getInterval() {
    return m_interval;
  }

  public MNumber getPeriod() {
    return m_period;
  }

  public void setInterval(Interval interval) {
    m_interval = interval;
  }
  
  public void setPeriod(MNumber number) {
    m_period = number;
  }
  
  public void setPeriod(double number){
    m_period.setDouble(number);
  }

}
