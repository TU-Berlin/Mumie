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

import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;

/**
 * Evaluates powers of <code>i</code>, i.e. replaces i^2 with -1, etc.
 * 
 * @author markov, gronau
 * @mm.docstatus finished
 */
public class CollapsePowerOfIRule extends OpRule {

	/**
	 * Evaluates to true, if the node is a VariableOp, its exponent is 
	 * different to <code>1</code> or <code>-1</code> and its identifier
	 * is <code>i</code>.
	 */
	public boolean appliesTo(OpNode node) {
		if (!(node instanceof VariableOp))
			return false;
		if (node.getNumberClass() != MComplex.class && node.getNumberClass() != MComplexRational.class)
			return false;
		if (Math.abs(node.getExponent()) > 1
				&& ((VariableOp) node).getIdentifier().equals("i"))
			return true;
		return false;
	}

	/**
	 * Replaces the VariableOp node with NumberOp nodes if the result is real
	 * or by an appropriate VariableOp if the result is imaginary.
	 */
	public OpNode transform(OpNode node) {
		// even exponent (multiple of 2) -> result is a real number
		if (node.getExponent() % 2 == 0) {
			// positive value when exp is multiple of 4
			NumberOp result = new NumberOp(node.getNumberClass(), 
					(node.getExponent() % 4 == 0) ? 1 : -1);
			// multiply value with factor directly
			result.setResult(result.getResult().mult(node.getFactor()));
			result.setParent(node.getParent());
			return result;
		} else {
			// odd exponent -> result is a multiple of i
			// -> reuse VariableOp
			if (Math.abs(node.getExponent() % 4) != 1)
				node.setFactor(node.getFactor().negate());
			if(node.getExponent() < 0)
				node.setFactor(node.getFactor().negate());
			node.setExponent(1);
			return node;
		}
	}
}
