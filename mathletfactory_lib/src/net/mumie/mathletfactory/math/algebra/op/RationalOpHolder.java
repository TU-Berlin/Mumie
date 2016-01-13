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
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;



/**
 *  This class acts as a container for a node rooting a rational expression
 *  in zero, one or multiple variables. It bundles the functionality used for
 *  rational expressions (getting the Domain, etc.).
 * 
 * 
 *  @author Paehler
 *  @mm.todo implement various methods
 *  @mm.docstatus finished
 */
public class RationalOpHolder{
  
  /** The root node of the rational expression. */
  protected OpNode m_rootNode;
  
  /** Constructs the rational operation holder with the given root node. */
  public RationalOpHolder(OpNode rootNode){
    m_rootNode = rootNode;
  }
   
  /**
   *  Returns a <code>BorelSet</code> containing the definition range of this
   *  rational expression.
   */
  public FiniteBorelSet getDomain(){
    return new FiniteBorelSet(m_rootNode.getNumberClass());
  }
  
  /**
   *  Returns a <code>BorelSet</code> containing the range where this
   *  rational expression is positive.
   */
  public FiniteBorelSet isPositiveFor(){
    return new FiniteBorelSet(m_rootNode.getNumberClass());
  }
  
  /**
   *  Returns a <code>BorelSet</code> containing the range where this
   *  rational expression is positive.
   */
  public FiniteBorelSet isNegativeFor(){
    return new FiniteBorelSet(m_rootNode.getNumberClass());
  }
}

