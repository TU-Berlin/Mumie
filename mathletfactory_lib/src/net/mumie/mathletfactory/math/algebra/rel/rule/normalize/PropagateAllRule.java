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

import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.ComplexRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 * Propagates {@link net.mumie.mathletfactory.algebra.rel.node.NullRel}s
 * with the following scheme: 
 * <pre>
    x AND y ... AND ALL -> x OR y ...
    x OR y ... OR ALL -> ALL
   </pre> 
 * @author Paehler
 * @mm.docstatus finished
 */
public class PropagateAllRule extends RelRule {

   /**
    *  Returns true if <code>node</code> is a 
    *  {@link net.mumie.mathletfactory.algebra.rel.node.ComplexRel} and one of
    *  its children is a 
    *  {@link net.mumie.mathletfactory.algebra.rel.node.AllRel}.
    * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo(net.mumie.mathletfactory.algebra.rel.node.RelNode)
    */
   public boolean appliesTo(RelNode node) {
     if(node instanceof ComplexRel && node.getFirstChildOfType(AllRel.class, 0) != null)
       return true;
     return false;
   }
 /**  
  * Realizes
  * <pre>
      x AND ALL -> x
      x OR ALL -> ALL
     </pre> 
  */
  public RelNode transform(RelNode node) {
    if(node instanceof OrRel){
      return new AllRel(node.getNumberClass());
    }
    // node must be AndRel
    AllRel allRel  = (AllRel)node.getFirstChildOfType(AllRel.class, 0);
    RelNode[] newChildren = RelNode.extractNode(node.getChildren(), allRel);
    if(newChildren.length > 1)
      return new AndRel(newChildren);
    else return newChildren[0];
  }

 

}
