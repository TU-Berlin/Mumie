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

package net.mumie.mathletfactory.math.algebra.rel.node;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.Interval;

/**
 *  This class models the "Null"-Relation: A relation that is 
 *  never satisfied for any variable-value connection.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class NullRel extends RelNode {
	
	public NullRel(Class numberClass) {
		super(numberClass);
	}
	  
  /** 
   * Always returns false.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluate(MNumber[])
   */
  public boolean evaluate(MNumber[] assignedValues) {
	  return false;
	}
  
  /** 
   * Always returns false.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluate(HashMap)
   */
	public boolean evaluate(HashMap variableValues) {
	  return false;
	}

  /** 
   * Always returns false.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluateFast()
   */ 
   public boolean evaluateFast() {
     return false;
   }

	public void separateFor(String identifier) {
	}

	public void setNormalForm(int normalForm) {
	}

  /** 
   * Always returns the maximum normalization level.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#isNormalForm()
   */
  public int getNormalForm() {
    return Relation.NORMALIZE_OP_SEPARATED;
  }
	
	public HashSet getUsedVariables(HashSet usedVariables) {
	  return usedVariables;
	}

  public RelNode negate(boolean deepCopy){
    return new AllRel(m_numberClass);
  }
  
  public boolean isLeaf(){
    return true;
  }

  /** Returns "NULL" */
  public String toString(DecimalFormat format){
      return "NULL";
  }
	
  /** Returns "NULL" */
  public String toString(){
    return "NULL";
  }
  
  /** Returns complete real axis. */
  public Interval[] getIntervalsFalse(double epsilon,  Interval interval){
    return new Interval[]{Interval.getRealAxis(m_numberClass)};
  }
    
  /** Returns <code>null</code>. */
  public Interval[] getIntervalsTrue(double epsilon,  Interval interval){
    return new Interval[]{};
  }
}
