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
 *  This node models the intersection of two sets.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class IntersectSet extends SetNode {


  public IntersectSet(SetNode leftChild, SetNode rightChild) {
    this(new SetNode[]{leftChild, rightChild});
  }
  
  public IntersectSet(SetNode[] children) {
    super(children[0].getNumberClass());
    setChildren(children);
  }

  public boolean contains(double aNumber) {
    return m_children[0].contains(aNumber) && m_children[1].contains(aNumber); 
  }

  public boolean contains(MNumber aNumber) {
    return m_children[0].contains(aNumber) && m_children[1].contains(aNumber);
  }

  public FiniteBorelSet getFiniteBorelSet(Interval boundingInterval, double eps) {
    return m_children[0].getFiniteBorelSet(boundingInterval, eps).
           intersect(m_children[1].getFiniteBorelSet(boundingInterval, eps));
  }
}
