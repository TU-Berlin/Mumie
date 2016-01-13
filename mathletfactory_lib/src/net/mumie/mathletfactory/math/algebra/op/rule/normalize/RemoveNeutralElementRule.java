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

import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;

/**
 * This Rule replaces redundant operations like x * 1 or x + 0 with x.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class RemoveNeutralElementRule extends OpRule {

	/**
	 * Checks, whether the node has children of type
	 * {@link net.mumie.mathletfactory.algebra.op.node.MultOp}<code>(x,1)</code>,
	 * {@link net.mumie.mathletfactory.algebra.op.node.AddOp}<code>(x,0)</code>
	 * or{@link net.mumie.mathletfactory.algebra.op.node.MultOp}<code>(x,-1)</code>.
	 */
	public boolean appliesTo(OpNode node) {
		if (node.getChildren() == null)
			return false;
		
		for (int i = 0; i < node.getChildren().length; i++)
			if (node.getChildren()[i].isNeutralElementFor(node))
				return true;

		return false;
	}

	/**
	 * If node has two children, replace the node by the child, which is not the
	 * neutralElement, else remove the neutral child node.
	 */
	public OpNode transform(OpNode node) {
		if (node.getChildren().length == 2) {
			if (node.getChildren()[0].isNeutralElementFor(node)) {
				if (node instanceof MultOp)
					node.getChildren()[1].multiplyFactor(node.getChildren()[0].getResult());
				OpNode.replaceWith(node, node.getChildren()[1]);
				node = node.getChildren()[1];
			} else {
				if (node instanceof MultOp)
					node.getChildren()[0].multiplyFactor(node.getChildren()[1].getResult());
				OpNode.replaceWith(node, node.getChildren()[0]);
				node = node.getChildren()[0];
			}
			return node;
		} else { // remove neutral child
			OpNode[] newChildren = new OpNode[node.getChildren().length - 1];
			int counter = 0;
			boolean replace = false;
			for (int i = 0; i < node.getChildren().length; i++) {
				if (!node.getChildren()[i].isNeutralElementFor(node) || replace)
					newChildren[counter++] = node.getChildren()[i];
				else {
					if (node instanceof MultOp)
						node.multiplyFactor(node.getChildren()[i].getResult());
					replace = true;	
				}
			}
			// else
			// System.out.println("removing
			// "+node.getChildren()[i].toDebugString());
			node.setChildren(newChildren);
		}
		return node;
	}
}
