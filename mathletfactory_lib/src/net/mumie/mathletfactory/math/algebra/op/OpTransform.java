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
import net.mumie.mathletfactory.math.algebra.op.rule.NumerizeConstantsAndParametersRule;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandInternalDataRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandPowerOfSumRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandPowerRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandProductRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseEqualOpsRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseInverseFunctionRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseNrtRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapsePowerOfIRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapsePowerRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseRationalPowerRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.HandleFunctionSymmetryRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.NormalizeAddRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.NormalizeConstantRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.NormalizeMultRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.PropagateConstantsRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveNeutralElementRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveZeroExponentRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveZeroMultRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.SummarizeEqualAddChildrenRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.SummarizeEqualMultChildrenRule;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 *  This class bundles all actions, which transform an Operation Tree (which
 *  practically reduces its complexity while leaving it mathematically the
 *  same). It uses different sets of
 *  {@link net.mumie.mathletfactory.analysis.function.op.rules.OpRule Rule}s
 *  that determine the steps of transformation.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class OpTransform {
	
  private static MumieLogger LOGGER = MumieLogger.getLogger(OpTransform.class);
  private static LogCategory RULES_CATEGORY = LOGGER.getCategory("math.op.rules");
  
  /** The rules for numerizing constants and parameters. */
  static OpRule[] numerizingRules = new OpRule[]{ new NumerizeConstantsAndParametersRule() };
  
  /** The rules used for normalization.*/
  static OpRule[] normalizationRules = new OpRule[]{
    new NormalizeMultRule(), new CollapseEqualOpsRule(),
      new CollapsePowerOfIRule(), new PropagateConstantsRule(),
      new RemoveZeroMultRule(), new RemoveZeroExponentRule(),
      new SummarizeEqualAddChildrenRule(), new SummarizeEqualMultChildrenRule(),
      new RemoveNeutralElementRule(), new CollapsePowerRule(), 
      new CollapseRationalPowerRule(),
      new NormalizeConstantRule(), new CollapseInverseFunctionRule(),
      new CollapseNrtRule(), new HandleFunctionSymmetryRule(),
      new NormalizeAddRule()
  //   buggy ,new NormalizeExponentsRule()
  };
  
  /** The rules used for term expansion.*/
  static OpRule[] expansionRules = new OpRule[]{
    new ExpandProductRule(), new ExpandInternalDataRule(),
      new ExpandPowerRule(), new ExpandPowerOfSumRule()
      };

  /** The rules used for term expansion.*/
  static OpRule[] internalExpansionRules = new OpRule[]{
      new ExpandInternalDataRule(), new CollapseEqualOpsRule(), 
      new SummarizeEqualAddChildrenRule(), new SummarizeEqualMultChildrenRule()
      };

  /** Normalizes the expression represented by <code>opRoot</code>. */
  public static OpNode numerize(OpNode opRoot){
    opRoot = transform(opRoot, numerizingRules); 
    if(LOGGER.isActiveCategory(RULES_CATEGORY))
	   LOGGER.log(RULES_CATEGORY, "numerized: "+opRoot.toString());
    return opRoot;
  }
  
  
  /** Normalizes the expression represented by <code>opRoot</code>. */
  public static OpNode normalize(OpNode opRoot){
    opRoot = transform(opRoot, normalizationRules);
    if(LOGGER.isActiveCategory(RULES_CATEGORY))
 	   LOGGER.log(RULES_CATEGORY, "normalized: "+opRoot.toString());
    return opRoot;
  }
  
  /** Expands the expression represented by <code>opRoot</code>
   *  and normalizes the result.
   */
  public static OpNode expand(OpNode opRoot){
	  return expand(opRoot, true);
  }
  
  /** Expands the expression represented by <code>opRoot</code>
   * and normalizes the result if the <code>normalize</code> flag 
   * is set to <code>true</code>.
   */
  public static OpNode expand(OpNode opRoot, boolean normalize){
    opRoot = transform(opRoot, expansionRules);
    if(LOGGER.isActiveCategory(RULES_CATEGORY))
 	   LOGGER.log(RULES_CATEGORY, "expanded: "+opRoot.toDebugString());
    if(normalize)
    	opRoot = normalize(opRoot);
    return opRoot;
  }
  
  public static OpNode expandInternal(OpNode opRoot){
    opRoot = transform(opRoot, internalExpansionRules);
    if(LOGGER.isActiveCategory(RULES_CATEGORY))
 	   LOGGER.log(RULES_CATEGORY, "expanded internal data: "+opRoot.toDebugString());
    return opRoot;
  }
  
  /**
    *  Transforms an operation tree by descending node by node and checking for
    *  each the application of the existing rules. Because there is no sensible
    *  way of determining the interaction of changes made by rules, the
    *  transformation process is rerun from the root node in depth order if any
    *  changes to any node occur. The process is finished, when the whole tree
    *  has been traversed without any changes being made.
    */
   private static OpNode transform(OpNode opRoot, OpRule[] rules) {
     OpNode newOpRoot = opRoot;
     do {
       newOpRoot = transformNode(newOpRoot, rules);
       if(newOpRoot != null)
         opRoot = newOpRoot;
       else
         return opRoot;
     } while(true);
   }
  
   /**
    *  Applies the set of rules to the given node an initiates a restart, if
    *  changes have been made. Otherwise the children of the node are checked
    *  for transformations. If the node is not changed, <code>null</code> will
    *  be returned.
    */
   private static OpNode transformNode(OpNode node, OpRule[] rules) {
     boolean restart = false;
     //System.out.println("checking "+node.toDebugString());
     // check for each rule, if it applies
     for(int i = 0;i < rules.length; i++) {
       if(rules[i].appliesTo(node)) {
    	   if(LOGGER.isActiveCategory(RULES_CATEGORY))
    		   LOGGER.log(RULES_CATEGORY, node.toDebugString()+": applying "+rules[i]);
         node =  rules[i].transform(node);
  	   	if(LOGGER.isActiveCategory(RULES_CATEGORY))
		   LOGGER.log(RULES_CATEGORY, "done :"+node.toDebugString());
         // every time a node has been altered, the analysis restarts from root
         restart = true;
         break;
       }
     }
     if(restart)
       return node;
     // node has not been changed, so check the rules for children, if any
     OpNode[] children = node.getChildren();
     if(children == null)
       return null;// neither node nor children were changed
    
     for(int i = 0; i < children.length; i++) {
       OpNode newChild = transformNode(children[i], rules);
       if(newChild != null) {
         children[i] = newChild;
         node.setChildren(children);
         return node;
       }
     }
     // node was not changed
     return null;
   }
   
   /**
    * Applies the given operation rules to <code>opRoot</code>
    * and returns the resulting node.
    */
   public static OpNode applyRules(OpNode opRoot, OpRule[] rules) {
	   opRoot = transform(opRoot, rules);
	   if(LOGGER.isActiveCategory(RULES_CATEGORY)) {
	    	for (int i = 0; i < rules.length; i++) 
	    		LOGGER.log(RULES_CATEGORY, "applyRules: "+rules[i].getClass().getName()+", "+opRoot.toString());
	    }
	    return opRoot;
   }
   
   /**
    * Applies the given operation rule to <code>opRoot</code>
    * and returns the resulting node.
    */
   public static OpNode applyRule(OpNode opRoot, OpRule rule) {
	   return applyRules(opRoot, new OpRule[] {rule});
   }
}


