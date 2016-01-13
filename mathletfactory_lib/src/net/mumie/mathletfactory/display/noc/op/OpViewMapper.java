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

package net.mumie.mathletfactory.display.noc.op;

import java.util.Hashtable;

import net.mumie.mathletfactory.display.noc.op.node.AbsView;
import net.mumie.mathletfactory.display.noc.op.node.AddView;
import net.mumie.mathletfactory.display.noc.op.node.ConjView;
import net.mumie.mathletfactory.display.noc.op.node.ExpView;
import net.mumie.mathletfactory.display.noc.op.node.FacView;
import net.mumie.mathletfactory.display.noc.op.node.FunctionView;
import net.mumie.mathletfactory.display.noc.op.node.MultView;
import net.mumie.mathletfactory.display.noc.op.node.NrtView;
import net.mumie.mathletfactory.display.noc.op.node.NumberView;
import net.mumie.mathletfactory.display.noc.op.node.PowerView;
import net.mumie.mathletfactory.display.noc.op.node.VariableView;
import net.mumie.mathletfactory.display.noc.op.node.ViewNode;
import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.ConjOp;
import net.mumie.mathletfactory.math.algebra.op.node.ExpOp;
import net.mumie.mathletfactory.math.algebra.op.node.FacOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultipleOfZNode;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;
import net.mumie.mathletfactory.math.algebra.op.node.UneditedOp;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;

/**
 *  This class is responsible for creating the view of a given operation tree
 *  represented by its root node.
 *  @see net.mumie.mathletfactory.algebra.op.node.OpNode
 *  @see net.mumie.mathletfactory.display.noc.op.node.ViewNode
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class OpViewMapper
{
  private static Hashtable map = new Hashtable(20);
  
  static {
    map.put(AbsOp.class, AbsView.class);
    map.put(AddOp.class, AddView.class);
    map.put(ExpOp.class, ExpView.class);
    map.put(FacOp.class, FacView.class);
    map.put(MultOp.class, MultView.class);
    map.put(MultipleOfZNode.class, VariableView.class);
    map.put(NrtOp.class, NrtView.class);
    map.put(NumberOp.class, NumberView.class);
    map.put(UneditedOp.class, NumberView.class);
    map.put(PowerOp.class, PowerView.class);
    map.put(ConjOp.class, ConjView.class);
    map.put(VariableOp.class, VariableView.class);
  }
  
  /**
   * Returns a subtree of {@link net.mumie.mathletfactory.display.noc.op.node.ViewNode}
   * for the given {@link net.mumie.mathletfactory.algebra.op.node.OpNode}.
   */
  public static ViewNode getViewFor(OpNode node){
    ViewNode viewNode = null;
    Class vClass = null;
    try {
      vClass =  (Class)map.get(node.getClass());
      if(vClass == null)
        vClass = FunctionView.class;
      viewNode =  (ViewNode)vClass.newInstance();
    } catch(Exception e){e.printStackTrace();}
    if(node.getChildren() != null){
      ViewNode[] children = new ViewNode[node.getChildren().length];
      for(int i=0; i<children.length;i++)
        children[i] = getViewFor(node.getChildren()[i]);
      viewNode.setChildren(children);
    }
    viewNode.setMaster(node);
    return viewNode;
  }
}

