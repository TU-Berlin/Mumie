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

package net.mumie.mathletfactory.math.algebra.rel;

import java.util.logging.Logger;

import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.algebra.rel.rule.RelRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.CollapseComplexRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.MoveOrUpwardsRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.NormalizeSimpleRelRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.PropagateAllRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.PropagateNullRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.RemoveConstantSimpleRelRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.RemoveNotRelRule;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.ReplaceTrivialAllRel;
import net.mumie.mathletfactory.math.algebra.rel.rule.normalize.SummarizeEqualChildrenRule;

/**
 *  This static class offers methods to transform a {@link Relation} into another.
 * 
 * 	@author Paehler
 * 	@mm.docstatus finished
 */
public class RelTransform {

  private static Logger logger = Logger.getLogger(RelTransform.class.getName());

  /** Every time a node has been changed, the transformation process is restarted */
  static boolean restart = false;

  /** The ruleset for normalization. */
  static RelRule[] normalizeRules = new RelRule[] { new CollapseComplexRule(),
  new MoveOrUpwardsRule(), new PropagateNullRule(), new PropagateAllRule(),
  new RemoveConstantSimpleRelRule(), new SummarizeEqualChildrenRule(),
  new RemoveNotRelRule(), new NormalizeSimpleRelRule(), new ReplaceTrivialAllRel()};

  /** 
   * Normalizes the relation tree by applying the normalize rules (located in
   * {@link net.mumie.mathletfactory.algebra.rel.rule.normalize}).
   * 
   * @param relRoot the root node of the tree to be normalized
   * @return the root node of the normalized tree 
   */
  public static RelNode normalizeRelation(RelNode relRoot) {
    relRoot = transform(relRoot, normalizeRules);
    return relRoot;
  }

  /**
   *  Transforms an relation tree by descending node by node and checking for
   *  each the application of the existing rules. Because there is no sensible
   *  way of determining the interaction of changes made by rules, the
   *  transformation process is rerun from the root node in depth order if any
   *  changes to any node occur. The process is finished, when the whole tree
   *  has been traversed without any changes being made.
   */
  private static RelNode transform(RelNode relRoot, RelRule[] rules) {
    //logger.fine("checking "+opRoot.toDebugString());
    restartFromRoot : while (true) {
      // start from root
      restart = false;
      relRoot = transformNode(relRoot, rules);
      if (restart)
        continue restartFromRoot;
      return relRoot;
    }
  }

  /**
   *  Applies the set of rules to the given node an initiates a restart, if
   *  changes have been made. Otherwise the children of the node are checked
   *  for transformations.
   */
  private static RelNode transformNode(RelNode node, RelRule[] rules) {
    //logger.fine("checking "+node.toString());
    // check for each rule, if it applies
    for (int i = 0; i < rules.length; i++)
      if (rules[i].appliesTo(node)) {
        if (logger.isLoggable(java.util.logging.Level.FINE))
          logger.fine(node.toString() + ": applying " + rules[i]);
        node = rules[i].transform(node);
        if (logger.isLoggable(java.util.logging.Level.FINE))
          logger.fine("done :" + node.toString());
        // every time a node has been altered, the analysis restarts from root
        restart = true;
        break;
      }
    if (restart)
      return node;
    // node has not been changed, so check the rules for children, if any
    RelNode[] children = node.getChildren();
    if (children == null)
      return node;

    for (int i = 0; i < children.length; i++) {
      children[i] = transformNode(children[i], rules);
      if (restart) {
        node.setChildren(children);
        return node;
      }
    }
    node.setChildren(children);
    return node;
  }

  /**
   *  Applies <code>operation</code> on both sides of the simple relation, checking
   *  the definition range of the operation and handle the inequality signs with
   *  respect to <code>operation</code>s monotonocity.
   */
  public static RelNode performOpOnSimpleRel(OpNode operation, SimpleRel simpleRel) {
    RelNode retVal = null;
    logger.fine("performing "+operation+" onto "+simpleRel);
    OpNode oldLeft = simpleRel.getLeftHandSide().getOpRoot();
    OpNode oldRight = simpleRel.getRightHandSide().getOpRoot();
    
      
    SimpleRel newRel = performOpOnSimpleRelWithoutDefCheck(operation, simpleRel);
    logger.fine("newRel is "+newRel);
    OpNode newLeft =  newRel.getLeftHandSide().getOpRoot();
    OpNode newRight = newRel.getRightHandSide().getOpRoot();    
    
    if (newRel.getRelationSign().equals(Relation.EQUAL)
      || newRel.getRelationSign().equals(Relation.NOT_EQUAL)) {
      retVal = newRel;
    } else  {
      // inequality with "<", ">", "<=" or ">="
      if(operation.getContainedVariables().length == 0 || operation instanceof AddOp)
        return new AndRel(new RelNode[]{newRel, 
        operation.getMonotonicIncreasingRel(oldLeft), 
        operation.getMonotonicIncreasingRel(oldRight)});
      
      retVal =
        new OrRel(
          new AndRel(new RelNode[]{newRel, 
            operation.getMonotonicIncreasingRel(oldLeft), 
            operation.getMonotonicIncreasingRel(oldRight)}),
          new AndRel(new RelNode[]{newRel.negated(), 
            operation.getMonotonicDecreasingRel(oldLeft), 
            operation.getMonotonicDecreasingRel(oldRight)}));
    }
    logger.fine("returning "+retVal.toString());
    return retVal;
  }

  /**
   * Performs <code>operation</code> on both parts of the simple relation 
   * <code>relation</code> by calling {@link #performOperationOn} for both
   * sides <code>relation</code>.
   */
  private static SimpleRel performOpOnSimpleRelWithoutDefCheck(OpNode operation,SimpleRel simpleRel) {
    simpleRel.setLeftHandSide(
      performOpOnOp((OpNode) operation.clone(), simpleRel.getLeftHandSide().getOpRoot()));
    simpleRel.setRightHandSide(
      performOpOnOp((OpNode) operation.clone(), simpleRel.getRightHandSide().getOpRoot()));
    return simpleRel;
  }

  /**
   * Sets <code>relationPart</code> as child of <code>operation</code>. If 
   * <code>operation</code> is an n-ary operation, this is done by calling 
   * {@link net.mumie.mathletfactory.algebra.op.node.OpNode#replace}.
   */
  private static OpNode performOpOnOp(OpNode operation, OpNode relationPart) {
    if (operation.getChildren() == null)
      // operation is unary (sin, sqrt, exp etc.)
      operation.setChildren(new OpNode[] { relationPart });
    else
      // operation is non-unary (+,*,^), so we have to add the equationPart to
      // the other children: replace the REPLACEMENT_IDENTIFIER node with the
      // equation part
      operation.replace(relationPart);
    return operation;
  }

  /**
   * Creates an operation by parsing <code>transformString</code> that contains
   * a {@link net.mumie.mathletfactory.algebra.op.node.OpNode#REPLACEMENT_IDENTIFIER}
   * which will be replaced by the parts of the relation to be transformed by
   * the returned {@link net.mumie.mathletfactory.algebra.op.node.OpNode}.
   */
  public static OpNode getOperationForRelTransform(Class numberClass, String transformString) {
    transformString.toLowerCase();
    if (transformString.equals("1/"))
      transformString = "^-1";

    // add an replacement identifier to the string, so that the parser will not comply
    // performOperation() will then replace it with the equation part
    if (transformString.matches(".*a*(abs|sin|cos|tan|exp|ln|sqrt).*"))
      // prefix operation
      transformString += "(" + OpNode.REPLACEMENT_IDENTIFIER + ")";

    else
      // postfix operation
      transformString = OpNode.REPLACEMENT_IDENTIFIER + transformString;
    return OpParser.getOperationTree(numberClass, transformString, false);

  }
 
  /**
   *  Recursively walks through the subtree represented by <code>relNode</code>
   *  and calls {@link #separateFor(SimpleRel, String)} for any 
   *  {@link net.mumie.mathletfactory.algebra.rel.node.SimpleRel}. 
   */
  public static RelNode separateFor(RelNode relNode, String identifier){
    if(relNode instanceof SimpleRel)
      return separateFor((SimpleRel)relNode, identifier);
    RelNode[] children = relNode.getChildren(); 
    if(children != null){
      for(int i=0;i<children.length;i++)
        children[i] = separateFor(children[i], identifier);
      relNode.setChildren(children);
    }
    return relNode;
  }
 
  /**
   *  Perform a simple solving strategy: Leave everything, that contains the
   *  given identifier on the left hand side and put the rest onto the right
   *  hand side. Since the transformations are equivalence transformations for
   *  a maximal domain in |R, the result may be a relation tree.
   */
  public static RelNode separateFor(SimpleRel simpleRel, String identifier) {
    Relation result = new Relation(simpleRel, Relation.NORMALIZE_OP);
    RelNode currentResult = simpleRel;

    Operation leftHandSide = simpleRel.getLeftHandSide();
    leftHandSide.expandInternal();
    while (!leftHandSide.getOpRoot().isIrreducibleFor(identifier)) {
      Operation rightHandSide = simpleRel.getRightHandSide();
      logger.fine("left hand side is " + leftHandSide.getOpRoot().toDebugString());
      logger.fine("right hand side is " + rightHandSide.getOpRoot().toDebugString());
      OpNode[] operations = leftHandSide.getOpRoot().solveStepFor(identifier);
      
      RelNode[] results = new RelNode[operations.length];
      SimpleRel toBePerformedOn = null;
      for(int i=0;i<operations.length;i++){
        toBePerformedOn = new SimpleRel(simpleRel);
        toBePerformedOn.setLeftHandSide(new Operation(leftHandSide)); // this is the expanded version
        results[i] = performOpOnSimpleRel(operations[i], toBePerformedOn);
      }
      if(results.length > 1)
        currentResult = new OrRel(results);
      else
        currentResult = results[0];
      currentResult = RelTransform.normalizeRelation(currentResult); 
      currentResult.setRelation(simpleRel.getRelation());  
      result.replace(simpleRel, currentResult);
 
      simpleRel = toBePerformedOn;
      leftHandSide = simpleRel.getLeftHandSide();
      logger.fine("result is " + currentResult.toString());
      leftHandSide.expand();
    }
    return result.getRelRoot();
  }
  
  /**
   *  Does the same as {@link #separateFor}, but only within the domain of 
   *  simple relations (i.e. no equivalence transformations).
   */
  public static SimpleRel simpleSeparateFor(SimpleRel simpleRel, String identifier) {
    Operation leftHandSide = simpleRel.getLeftHandSide();
    leftHandSide.expandInternal();
    while (!leftHandSide.getOpRoot().isIrreducibleFor(identifier)) {
      Operation rightHandSide = simpleRel.getRightHandSide();
      logger.fine("left hand side is " + leftHandSide.getOpRoot().toDebugString());
      logger.fine("right hand side is " + rightHandSide.getOpRoot().toDebugString());
      OpNode operation = leftHandSide.getOpRoot().simpleSolveStepFor(identifier);
   
      SimpleRel toBePerformedOn = new SimpleRel(simpleRel);
      toBePerformedOn.setLeftHandSide(new Operation(leftHandSide)); // this is the expanded version
      simpleRel = performOpOnSimpleRelWithoutDefCheck(operation, toBePerformedOn);
      simpleRel.normalizeOp();
      leftHandSide = simpleRel.getLeftHandSide();
      logger.fine("result is " + simpleRel.toString());
      leftHandSide.expand();
      
     }
    return simpleRel;
  }
  /**
   *  Transforms the relation by using the given string (e.g. "*3" results in
   *  a multiplication of both sides of the relation with 3).
   */
  public static Relation transformRelation(String transformString, Relation relation, SimpleRel simpleRelation) {

    // create newRelation as a clone of the master MMRelation object
    Relation newRelation = new Relation((RelNode)relation.getRelRoot().clone(), relation.getNormalForm());
    
    // extract SimpleRel node on which the operation should be applied
    RelNode oldSubRelation = simpleRelation;
    
    // apply the operation onto a clone of the specified subrelation  
    SimpleRel newSubRelation = (SimpleRel)oldSubRelation.clone();
    OpNode operation = getOperationForRelTransform(newRelation.getNumberClass(), transformString);
    RelNode result = performOpOnSimpleRel(operation, newSubRelation);
    result.setNormalForm(newRelation.getNormalForm());
    newRelation.replace(oldSubRelation, result);
    return newRelation;    
  }
}
