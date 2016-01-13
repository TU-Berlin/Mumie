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

package net.mumie.mathletfactory.display.noc.op.node;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.ExpOp}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class ExpView extends ViewNode {

  protected ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags) {
    Font origFont = g2.getFont();
    Font variableFont = origFont.deriveFont(Font.ITALIC);
    g2.setFont(variableFont);
    Rectangle2D bounds = drawText(g2, xPos, yPos, "e");
    g2.setFont(origFont);
    ViewNodeMetrics expressionMetrics = new ViewNodeMetrics();
    expressionMetrics.setAscentBounds(xPos, yPos - bounds.getHeight(), bounds.getWidth() + getSpacer(g2)*1.5, bounds.getHeight());
    expressionMetrics.setDescentBounds(xPos, yPos, bounds.getWidth() + getSpacer(g2)*1.5, 0);
    //expressionMetrics.draw(g2);
    return PowerView.drawSubscript(g2, expressionMetrics, printFlags, m_children[0], PowerView.SUPERSCRIPT, OFFLINEGRAPHICS);    
  }

}
