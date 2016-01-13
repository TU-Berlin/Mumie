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

package net.mumie.mathletfactory.math.algebra.op.node;

import java.text.DecimalFormat;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;

/** 
 * Represents the object for fac(n) = n! in the Op-Scheme 
 *
 * 	@author Paehler
 * 	@mm.docstatus finished 
 */

public class FacOp extends FunctionOpNode {

  public FacOp(Class entryClass){
    super(entryClass);
  }

  public FacOp(OpNode child){
    super(child);
  }

//hier .....................
  public int getMaxChildrenNr(){
    return 1;
  }
  
  public int getMinChildrenNr(){
    return 1;
  }
  //bis hier...............................
  
  public String toString(int printFlags, DecimalFormat format){
	String result = m_children[0].toString(format);
	if(result.startsWith("(") && result.endsWith(")"))
		return printFactorAndExponent(nodeToString()+result, printFlags, format);
    else 
		return printFactorAndExponent(nodeToString()+"("+result+")", printFlags, format);
  }

  public String nodeToString(){
    return "fac";
  }

  /** Returns {asin(x)+2*pi*Z, -asin(-x)+2*pi*Z} */
  public OpNode[] solveStepFor(String identifier){ 
    throw new TodoException();
  }
  
  public OpNode getInverseOp(OpNode newChild){
    throw new TodoException();  
  }

  /** Simply calls MNumber.sin(). */
  protected void calculate(){
    double result = 1;
    double arg = m_children[0].getResult().getDouble();
    for(int i=1;i<=arg;i++)
      result *= i;
    m_base.setDouble(result);
  }
  
  /** Simply calls Math.sin(child). */
  protected void calculateDouble(){
    double result = 1;
    double arg = m_children[0].getResult().getDouble();
    for(int i=1;i<arg;i++)
      result *= i;
    m_base.setDouble(result);
  }
  
  public RelNode getMonotonicDecreasingRel(OpNode operand){
    throw new TodoException();
  }
  
  public RelNode getMonotonicIncreasingRel(OpNode operand){
    throw new TodoException();
  }
  
  public RelNode getNodeDefinedRel(OpNode operand){
    return new AllRel(getNumberClass());
  }
  
  /** Returns for sin(x) a relation that is satisfied for x = pi*n. */ 
  public RelNode getZeroRel(OpNode operand){
    return new NullRel(getNumberClass());
  }
  
  /**
   *  Implements <i> (sin(f(x)))' = cos(f(x)) * f`(x) </i>.
   */
  public OpNode getDerivative(String variable){
    throw new TodoException();    
  }
}

