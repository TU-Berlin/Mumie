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

import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This node models a set defined by a {@link net.mumie.mathletfactory.math.algebra.rel.Relation}. 
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class RelSet extends SetNode {

	/** 
	 * The underlying relation that has to be true for a certain value of "x", 
	 * iff x is in the BorelSet. 
	 */ 
	private Relation m_relation;

  public RelSet(Relation relation){
    super(relation.getNumberClass());
    m_relation = relation;
  }

	public boolean contains(double aNumber) {
		return m_relation.evaluate(new MNumber[]{NumberFactory.newInstance(getNumberClass(), aNumber)});
	}

	public boolean contains(MNumber aNumber) {
		return m_relation.evaluate(new MNumber[]{});
	}
	
	public FiniteBorelSet getFiniteBorelSet(Interval boundingInterval, double eps) {
		FiniteBorelSet tmpLeft  = null;
		return tmpLeft;
	}
  
	/** 
	 * Sets the underlying relation that has to be true for a certain value of "x", 
	 * iff x is in the BorelSet. 
	 */ 
	public Relation getRelation() {
		return m_relation;
	}

	/** 
	 * Returns the underlying relation that has to be true for a certain value of "x", 
	 * iff x is in the BorelSet. 
	 */ 
	public void setRelation(Relation relation) {
		m_relation = relation;
	}
}
