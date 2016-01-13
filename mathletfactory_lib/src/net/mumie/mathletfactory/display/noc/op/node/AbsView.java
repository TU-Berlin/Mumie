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

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.AbsOp}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class AbsView extends ViewNode
{
   
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
	// the "cursor"
	double newXPos = xPos + getSpacer(g2);
	double height = 0;
	double depth = 0;
 
	// draw expression with parentheses or without
   ViewNodeMetrics expressionMetrics;

	// I. draw the left parenthesis
      
	//newXPos -= getSpacer(g2);
	// make the font as high as the bounding box of the expression
 	Font origFont = g2.getFont();
  OFFLINEGRAPHICS.setFont(origFont);
	expressionMetrics = m_children[0].draw(OFFLINEGRAPHICS, 0, 0, printFlags);
	//FontRenderContext frc = g2.getFontRenderContext();
      
	// draw the opening parenthesis behind the factor
	String parenString = "|";
	g2.setFont(origFont.deriveFont(Font.BOLD));
	ViewNodeMetrics parenMetrics = drawParenShape(g2, newXPos, yPos, parenString, expressionMetrics);
	
	newXPos += parenMetrics.getWidth();
	height = Math.max(height, parenMetrics.getAscentBounds().getHeight());
	depth = Math.max(depth, parenMetrics.getDescentBounds().getHeight());
      
	// II. draw the expression plain or as fraction
	g2.setFont(origFont);
	expressionMetrics = m_children[0].draw(g2, newXPos, yPos, printFlags);
	newXPos += expressionMetrics.getWidth()+getSpacer(g2);
	height = Math.max(height, expressionMetrics.getAscentBounds().getHeight());
	depth = Math.max(depth, expressionMetrics.getDescentBounds().getHeight());
      
	//expressionMetrics.draw(g2);
      
	// III. draw the right parenthesis    
	g2.setFont(origFont.deriveFont(Font.BOLD));
	parenMetrics = drawParenShape(g2, newXPos, yPos, parenString, expressionMetrics);
	newXPos += parenMetrics.getWidth();
	height = Math.max(height, parenMetrics.getAscentBounds().getHeight());
	depth = Math.max(depth, parenMetrics.getDescentBounds().getHeight());
	
	g2.setFont(origFont);
	
	// return the rectangle that contains everything from the factor to the right parenthesis
	ViewNodeMetrics metrics = new ViewNodeMetrics();
	metrics.setAscentBounds(xPos, yPos - height, newXPos - xPos, height);
	metrics.setDescentBounds(xPos, yPos, newXPos - xPos, depth);
	return metrics; 
  }
}


