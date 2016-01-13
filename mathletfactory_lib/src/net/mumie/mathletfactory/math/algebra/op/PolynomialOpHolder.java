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

package net.mumie.mathletfactory.math.algebra.op;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.poly.Polynomial;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 *  This class acts as a container for a node rooting a polynomial expression
 *  in zero, one or multiple variables. It bundles the functionality used for
 *  polynomials (expanding, factorizing, getting the nulls, etc.).
 *
 *  @author Paehler
 *  @mm.todo implement various methods
 *  @mm.docstatus finished
 */
public class PolynomialOpHolder {
  
  /** The root node of the polynomial expression. */
  protected OpNode m_rootNode;
  
  /** Constructs the polynomial operation holder with the given root node. */
  public PolynomialOpHolder(OpNode rootNode) {
    m_rootNode = rootNode;
  }
  
  /** Constructs the polynomial operation holder from the given polynomial. */
  public PolynomialOpHolder(Polynomial polynomial){
    NumberTuple coefficients = polynomial.getStandardCoefficientsRef();
    OpNode[] children = new OpNode[coefficients.getDimension()];
    children[0] = new NumberOp(coefficients.getEntry(1).copy());
    if(children.length == 1){
      m_rootNode = children[0];
      return;
    }
    
    for(int i=1;i<coefficients.getDimension();i++){
      children[i] = new VariableOp(coefficients.getNumberClass(),"x");
      children[i].setExponent(i);
      children[i].setFactor(coefficients.getEntry(i+1));
    }
    m_rootNode = OpTransform.normalize(new AddOp(children));
  }
  
  /** Factorizes the polynomial. */
  public void factorize() {
  }
  
  /** Expands the polynomial. */
  public void expand() {
  }
  
  /** Returns true if this polynomial contains only one variable. */
  public boolean isMonovariate() {
    throw new TodoException();
  }
  
  /** Returns true if this polynomial contains no variable. */
  public boolean isConstant() {
    throw new TodoException();
  }
  
  /** Returns true if this polynomial contains up to one variable. */
  public boolean isConstantOrMonovariate() {
    return isConstant() || isMonovariate();
  }
  
  /**
   *  Returns an Array of values for which this polynomial evaluates to zero
   *  WARNING: Only the identifier is assumed to be variable. All other
   *  variables are taken as constants with their currently assigned values.
   */
  public MNumber[] getNull(String identifier) {
    return new MNumber[0];
  }
  
  /**
   * Returns a {@link net.mumie.mathletfactory.algebra.poly.Polynomial} representation
   * of this polynomial.
   * @param identifier the identifier for which the polynomial should be monovariate,
   *  if <code>null</code> the identifier <code>"x"</code> will be used.
   */
  public Polynomial getAsPolynomial(String identifier) {
    m_rootNode = OpTransform.numerize(m_rootNode);
    m_rootNode = OpTransform.normalize(m_rootNode);
    if (identifier == null || identifier.equals(""))
      identifier = "x";
    int powerOfX = m_rootNode.getMaxAbsPowerOfVariable(identifier);
    if(powerOfX == Integer.MIN_VALUE )
      powerOfX = 0;
    Polynomial retVal = new Polynomial(m_rootNode.getNumberClass(), powerOfX);
    NumberTuple coefficients = retVal.getStandardCoefficientsRef();
    // operation is in normal form, this means, that the root node is either a VariableOp, an AddOp or a NumberOp
    if(m_rootNode instanceof NumberOp)
      coefficients.setEntry(1, m_rootNode.getResult());
    else if(m_rootNode instanceof VariableOp)
      coefficients.setEntry(m_rootNode.getExponent()+1, m_rootNode.getFactor());
    else // instance of AddOp
    {
      OpNode[] children = m_rootNode.getChildren();
      for(int i=0;i< children.length;i++){
        if(children[i] instanceof NumberOp)
          coefficients.setEntry(1, children[i].getResult());
        else
          coefficients.setEntry(children[i].getExponent()+1, children[i].getFactor());
      }
    }
    retVal.setFromStdCoefficients(coefficients);
    return retVal;
  }
  
  /** Returns an operation that is the mathematic equivalent of this polynomial. */
  public Operation getAsOperation(){
    return new Operation(m_rootNode);
  }
  
}
