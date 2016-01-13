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
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;

/**
 * This rule summarizes equal children of a 
 * {@link net.mumie.mathletfactory.algebra.rel.node.ComplexRel}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class SummarizeEqualChildrenRule extends RelRule {

  /**
   * Returns true if any two children of <code>node</code> are equal.
   * 
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#appliesTo(RelNode)
   */
  public boolean appliesTo(RelNode node) {
   //System.out.println("checking "+node.toDebugString());
   if(!(node instanceof ComplexRel))
     return false;
   int childCount = node.getChildren().length;
   for(int i=0;i<childCount;i++){   
     // check for equality
     if(i+1<childCount && node.getChildren()[i].equals(node.getChildren()[i+1]))
       return true;
     // check if two children are both simple relations with the same operations but with a "!=" and a ">" or a "<"
     if(i+1<childCount && node.getChildren()[i] instanceof SimpleRel && node.getChildren()[i+1] instanceof SimpleRel){
       SimpleRel rel1 = (SimpleRel) node.getChildren()[i], rel2 = (SimpleRel) node.getChildren()[i+1];
       if(rel1.includes(rel2) || rel2.includes(rel1))
         return true;
     }
   }
   return false;

  }
  
  /**
   * Replaces the two equal children with one.
   * 
   * @see net.mumie.mathletfactory.algebra.rel.rule.RelRule#transform(RelNode)
   */
   
  public RelNode transform(RelNode node) {
    for (int i = 0; i < node.getChildren().length; i++) {
      RelNode[] newChildren = null;
      // check for equality
      if (i + 1 < node.getChildren().length
        && node.getChildren()[i].equals(node.getChildren()[i + 1])) {
        newChildren = RelNode.extractNode(node.getChildren(), node.getChildren()[i]);
      }
      // check two children for inclusion
      if (i + 1 < node.getChildren().length
        && node.getChildren()[i] instanceof SimpleRel
        && node.getChildren()[i + 1] instanceof SimpleRel) {
        SimpleRel rel1 = (SimpleRel) node.getChildren()[i],
          rel2 = (SimpleRel) node.getChildren()[i + 1];
        if(rel1.includes(rel2) && node instanceof AndRel || rel2.includes(rel1) && node instanceof OrRel)
          newChildren = RelNode.extractNode(node.getChildren(), rel1);
        if(rel2.includes(rel1) && node instanceof AndRel || rel1.includes(rel2) && node instanceof OrRel)
          newChildren = RelNode.extractNode(node.getChildren(), rel2);
      }
      if (newChildren != null)
        if(newChildren.length == 1){
           RelNode.replaceWith(node, newChildren[0]);
           return newChildren[0];
        }
        else
          node.setChildren(newChildren);
      } 
    return node;
  }

}
