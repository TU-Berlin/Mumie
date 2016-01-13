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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.MultOp}, where all
 *  exponents are positive (no fraction is drawn).
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
class PureMultView extends PseudoViewNode
{
  
  public static final boolean PRINT_DOT = true;
  
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    
    // the "cursor"
    double newXPos = xPos;
    double height = 0;
    double depth = 0;
    
    MNumber number = NumberFactory.newInstance(getMaster().getNumberClass());
    number.setDouble(0);
    for(int i=0;i < m_children.length; i++){
      FontRenderContext frc = g2.getFontRenderContext();
      // print left paranthesis
      if(m_children[i].getMaster().parenthesesNeeded() || m_children[i].getMaster().getFactor().lessThan(number) && i > 0) {
	      double parenWidth = 0;
	      Rectangle2D bounds = drawText(g2, newXPos, yPos, "(");
	      parenWidth = bounds.getWidth() + getSpacer(g2)*1.5;
	      newXPos += parenWidth;
      }
      
//      // print minus sign
//    	if(m_children[i].getMaster().getSign() < 0) {
//	      TextLayout minusLayout = new TextLayout("-", g2.getFont(), frc);
//	      double ruleOffset = minusLayout.getBounds().getHeight();
//	      double minusWidth = 0;
//	        Rectangle2D bounds = drawText(g2, newXPos, yPos - ruleOffset/4, "-");
//	        minusWidth = bounds.getWidth() + getSpacer(g2) * 1.5; 
//	        newXPos += minusWidth;
//    	}
      
      ViewNodeMetrics childMetrics = m_children[i].draw(g2, newXPos, yPos, printFlags);
      newXPos += childMetrics.getWidth();
      height = Math.max(childMetrics.getAscentBounds().getHeight(), height);
      depth = Math.max(childMetrics.getDescentBounds().getHeight(), depth);
      //childMetrics.draw(g2);
      
      // print right paranthesis
      if(m_children[i].getMaster().parenthesesNeeded()|| m_children[i].getMaster().getFactor().lessThan(number) && i > 0) {
        newXPos += getSpacer(g2);
	    double parenWidth = 0;
	    Rectangle2D bounds = drawText(g2, newXPos, yPos, ")");
	    parenWidth = bounds.getWidth() + getSpacer(g2); 
	    newXPos += parenWidth;
      }
      
      if(i < m_children.length-1){
        if(PRINT_DOT){ // print the "*" between the child nodes
          double step = new TextLayout("*",g2.getFont(),frc).getBounds().getWidth() * 1.6;
          newXPos += step/4;
          //System.out.println("font can display u22c5: "+g2.getFont().canDisplay('\u22c5'));
          g2.drawString("\u22c5", (float) newXPos, (float) yPos);
          newXPos += step;
        } else
          newXPos += getSpacer(g2);
      }
    }
    ViewNodeMetrics metrics = new ViewNodeMetrics();
    metrics.setAscentBounds(xPos,yPos - height, newXPos - xPos, height);
    metrics.setDescentBounds(xPos, yPos, newXPos - xPos, depth);
    //g2.draw(metrics.getBounds());
    return metrics;
  }
  
}



