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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.FacOp}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class FacView extends ViewNode {

  protected ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags) {
	// the "cursor"
	double newXPos = xPos;
	double height = 0;
	double depth = 0;
	
	ViewNodeMetrics metrics = m_children[0].draw(OFFLINEGRAPHICS, xPos, yPos, OpNode.PRINT_ALL);
	if(m_children[0].getMaster().getFactor().getDouble() != 1 || m_children[0].getMaster().parenthesesNeeded()){
		ViewNodeMetrics parenMetrics = drawParenShape(g2, newXPos, yPos, "(", metrics);
		newXPos += parenMetrics.getWidth();
		height = parenMetrics.getAscentBounds().getHeight();
		depth = parenMetrics.getDescentBounds().getHeight();
	}
		  
    metrics = m_children[0].draw(g2, newXPos, yPos, OpNode.PRINT_ALL);
    newXPos += metrics.getWidth();
    height = Math.max(height, metrics.getAscentBounds().getHeight());
	depth = Math.max(height, metrics.getDescentBounds().getHeight());
	
	if(m_children[0].getMaster().getFactor().getDouble() != 1 || m_children[0].getMaster().parenthesesNeeded()){
		ViewNodeMetrics parenMetrics = drawParenShape(g2, newXPos, yPos, ")", metrics);
		newXPos += parenMetrics.getWidth();
		height = Math.max(height, parenMetrics.getHeight());
	}

	Rectangle2D bounds = drawText(g2, newXPos, yPos, "!");
	newXPos += bounds.getWidth()+getSpacer(g2);

    metrics.setAscentBounds(xPos, yPos, 
                            newXPos - xPos, 
                            height);
    return metrics;    
  }

}
