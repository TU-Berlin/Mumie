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

package net.mumie.mathletfactory.math.algebra.rel.rule.normalize;

import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.ComplexRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NullRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 * Propagates {@link net.mumie.mathletfactory.algebra.rel.node.NullRel}s
 * with the following scheme: 
 * <pre>
    x AND y ... AND NULL -> NULL
    x OR y ... OR NULL -> x OR y ...
   </pre> 
 * @author Paehler
 * @mm.docstatus finished
 */
public class PropagateNullRule extends RelRule {

   /**
    *  Returns true if <code>node</code> is a 
    *  {@link net.mumie.mathletfactory.algebra.rel.node.ComplexRel} and one of
    *  its children is a 
    *  {@link net.mumie.mathletfactory.algebra.rel.node.NullRel}.
    * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo(net.mumie.mathletfactory.algebra.rel.node.RelNode)
    */
   public boolean appliesTo(RelNode node) {
     if(node instanceof ComplexRel && node.getFirstChildOfType(NullRel.class, 0) != null)
       return true;
     return false;

   }
 /**  
  * Realizes
  * <pre>
      x AND NULL -> NULL
      x OR NULL -> x
     </pre> 
  */
  public RelNode transform(RelNode node) {
    if(node instanceof AndRel){
      return new NullRel(node.getNumberClass());
    }
    // must be OrRel
    NullRel nullRel  = (NullRel)node.getFirstChildOfType(NullRel.class, 0);
    RelNode[] newChildren = RelNode.extractNode(node.getChildren(), nullRel);
    if(newChildren.length > 1)
      return new OrRel(newChildren);
    else return newChildren[0];
  }

 

}
