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

package net.mumie.mathletfactory.math.algebra.op.rule;

import net.mumie.mathletfactory.math.algebra.op.node.OpNode;

/**
 *  An <code>OpRule</code> operates on the Operation Tree by transforming
 *  a subtree represented by its root node. It consists of a set of activities,
 *  bundled in {@link #transform}, that are performed onto the node (e.q.
 *  replacing the node with one of its children etc.).
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public abstract class OpRule {

  /**
   *  Performs the transformation specified by this rule.
   */
  public abstract OpNode transform(OpNode node);

  /**
   *  Checks whether this rules applies or not.
   */
  public abstract boolean appliesTo(OpNode node);

}