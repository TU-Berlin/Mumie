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

import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.set.NumberSetIF;

/**
 *  This class models a Relation of the type: x \in S, where x is an identifier 
 *  for a variable and S is an arbtrary
 * {@link net.mumie.mathletfactory.set.NumberSetIF NumberSet}. 
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class InSetRel extends RelNode {
	
	/** The identifier for the variable that is tested. */
	private VariableOp m_variable;

	/** The set, for which containment of the variable is tested. */
	private NumberSetIF m_set;

	/** Constructs the set relation with uninitialized variable and set values. */
	public InSetRel(Class numberClass) {
		super(numberClass);
	}
	  
	/** Constructs the set relation with the given variable and set values. */ 
	public InSetRel(NumberSetIF set, VariableOp variable){
		this( set.getNumberClass()); 
		m_variable = variable;
		m_set = set;
	}
	  
  /** 
   * Always returns true.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluate(MNumber[])
   */
	public boolean evaluate(MNumber[] assignedValues) {
	  return m_set.contains(m_variable.getResult());
	}
  
  /** 
   * Always returns true.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluateFast()
   */ 
  public boolean evaluateFast() {
    return true;
  }
  
  /** 
   * Always returns true.
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#evaluate(HashMap)
   */
	public boolean evaluate(HashMap variableValues) {
	  return true;
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
    
  /**
   * Simply returns the given HashSet, since no variables are used. 
   * 
   * @see net.mumie.mathletfactory.algebra.rel.node.RelNode#getUsedVariables(HashSet)
   */
	public HashSet getUsedVariables(HashSet usedVariables) {
	  return usedVariables;
	}

  public RelNode negate(boolean deepCopy){
    return new NullRel(m_numberClass);
  }
  
  public boolean isLeaf(){
    return true;
  }
  
	public Class getNumberClass() {
	  return m_numberClass;
	}

    /** Returns "x \in S" with x as the variable identifier and S as the set's string value. */
    public String toString(DecimalFormat format){
      return m_variable.getIdentifier()+" \\in "+m_set;
    }
  
	/** Returns "x \in S" with x as the variable identifier and S as the set's string value. */
	public String toString(){
 	  return m_variable.getIdentifier()+" \\in "+m_set;
	}

  /** Returns complete real axis. */
  public Interval[] getIntervalsTrue(double epsilon,  Interval interval){
    return new Interval[]{Interval.getRealAxis(m_numberClass)};
  }
  
  /** Returns <code>null</code>. */
  public Interval[] getIntervalsFalse(double epsilon,  Interval interval){
    return new Interval[]{};
  }

  /** Returns the set, for which containment of the variable is tested. */
	public NumberSetIF getSet() {
		return m_set;
	}
	
	/** Sets the set, for which containment of the variable is tested. */
	public void setSet(NumberSetIF setIF) {
		m_set = setIF;
	}
	
	/** The identifier for the variable that is tested. */
	public VariableOp getVariable() {
		return m_variable;
	}

	/** The identifier for the variable that is tested. */
	public void setVariable(VariableOp op) {
		m_variable = op;
	}
}
