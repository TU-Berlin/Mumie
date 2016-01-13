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

package net.mumie.mathletfactory.math.set;

import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.node.IntersectSet;
import net.mumie.mathletfactory.math.set.node.JoinSet;
import net.mumie.mathletfactory.math.set.node.SetNode;

/**
 * This set models a borel set (a join or intersection of countable intervals). This set is internally
 * organized as a tree of {@link net.mumie.mathletfactory.math.set.node.SetNode SetNodes} to allow
 * algebraic operations.
 *   
 * @author Paehler
 * @mm.docstatus finished
 */
public abstract class BorelSet implements NumberSetIF, Cloneable {

  private SetNode m_root;
  
	public boolean contains(double aNumber) {
	  return m_root.contains(aNumber);
	}

	public boolean contains(MNumber aNumber) {
	  return m_root.contains(aNumber);
	}
	
	/**
	 * Returns the finite approximation of this <code>BorelSet</code> with respect to the
	 * given <code>boundingInterval</code> and the small-cut-off <code>epsilon</code>.
	 */
	public FiniteBorelSet getFiniteBorelSet(Interval boundingInterval, double eps) {
		return m_root.getFiniteBorelSet(boundingInterval, eps);
	}
	
	/** Sets this set to the intersection of this set and the given set. */
	public void intersect(BorelSet B) {
	  m_root = new IntersectSet(m_root, B.getRootNode());
	}

	/** Joins this set to the intersection of this set and the given set. */
	public void join(BorelSet B){
		m_root = new JoinSet(m_root, B.getRootNode());
	}

  /** Returns a copy of the set. */
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
  
  /** Returns the root node of the set tree. */
  public SetNode getRootNode(){
    return m_root;
  }
}
