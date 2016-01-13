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

package net.mumie.mathletfactory.math.algebra.op.rule.normalize;


import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;


/**
 * If the node and children are both
 * {@link net.mumie.mathletfactory.algebra.op.node.MultOp}s or
 * {@link net.mumie.mathletfactory.algebra.op.node.AddOp}s, they may be
 * replaced by a single Operation with the union of both nodes' children as new
 * children.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class CollapseEqualOpsRule extends OpRule {

	/**
	 * Evaluates to true, if both the node and a child are
	 * {@link net.mumie.mathletfactory.algebra.op.node.MultOp}s or
	 * {@link net.mumie.mathletfactory.algebra.op.node.AddOp}s.
	 */
	public boolean appliesTo( OpNode node ) {
		// System.out.println("checking "+node.toDebugString());
		if ( node.getChildren() == null || !( node instanceof MultOp ) && !( node instanceof AddOp ) ) return false;
		for ( int i = 0; i < node.getChildren().length; i++ )
			if ( node.getChildren()[i].getClass() == node.getClass() ) {
				// multiplication works for every power
				if ( node instanceof MultOp )
					return true;
				else
				// additions can be contracted only for exponent 1 terms
				if ( node instanceof AddOp && node.getChildren()[i].getExponent() == 1 ) return true;
			}
		return false;
	}

	/**
	 * Add the children of the child node as extra children of <code>node</code>.
	 */
	public OpNode transform( OpNode node ) {

		// System.out.println("ACTION: node is"+node.toDebugString());
		OpNode sameOpChild = node.getFirstChildOfType( node.getClass(), 0 );
		int i = 0;
		// make sure that we get the child with exponent = 1
		while ( node instanceof AddOp && sameOpChild.getExponent() != 1 ) {
			sameOpChild = node.getFirstChildOfType( node.getClass(), ++i );
			if ( sameOpChild == null ) return node;
		}
		// put both children of node and children of child into one array
		OpNode[] newChildren = new OpNode[node.getChildren().length - 1 + sameOpChild.getChildren().length];
		int counter = 0;
		for ( int j = 0; j < node.getChildren().length; j++ )
			if ( node.getChildren()[j] != sameOpChild ) newChildren[counter++] = node.getChildren()[j];
		// add child's children as node's new children
		for ( int j = 0; j < sameOpChild.getChildren().length; j++ ) {
			// multiply the children's exponent and the exponent from the
			// operation node
			newChildren[counter] = sameOpChild.getChildren()[j];

			if ( sameOpChild instanceof MultOp && j == 0 ) {
				OpNode.transferFactorAndExponent( sameOpChild, newChildren[counter], false );
				sameOpChild.setFactor( sameOpChild.getFactor().div( sameOpChild.getFactor() ) );
			} else {
				OpNode.transferFactorAndExponent( sameOpChild, newChildren[counter], false );
			}
			counter++;
		}
		node.setChildren( newChildren );
		// System.out.println("ACTION: now node is"+node.toDebugString());
		return node;
	}
}
