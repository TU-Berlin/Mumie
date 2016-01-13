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

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.AddOp}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class AddView extends ViewNode
{
   
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    
    // the "cursor"
    double newXPos = xPos;
    double height = 0;
    double depth = 0;
    
    MNumber aNumber = NumberFactory.newInstance(getMaster().getNumberClass());
    aNumber.setDouble(0);
    for(int i=0;i<m_children.length;i++){
      
      FontRenderContext frc = g2.getFontRenderContext();
      
      ViewNodeMetrics childMetrics = null;
      if (i > 0 && m_children[i].m_master.getFactor().lessThan(aNumber)) {
    	  ViewNode node = (ViewNode)m_children[i].clone();
    	  node.getMaster().negateFactor();
    	  childMetrics = node.draw(g2, newXPos, yPos, printFlags);
      }	else {
    	  childMetrics = m_children[i].draw(g2, newXPos, yPos, printFlags);
      }
      
      //childMetrics.draw(g2);
      newXPos += childMetrics.getWidth();
      height = Math.max(childMetrics.getAscentBounds().getHeight(), height);
      depth = Math.max(childMetrics.getDescentBounds().getHeight(), depth);
      
      
      if(i<m_children.length-1){ // print the "+" between the child nodes
        double step = new TextLayout("+",g2.getFont(),frc).getBounds().getWidth() * 1.6;
        newXPos += step/4;
        g2.drawString(m_children[i+1].m_master.getFactor().getDouble() > 0 ? "+" 
                       : "-", (float) newXPos, (float) yPos);
        newXPos += step;
      }
    }
    
    ViewNodeMetrics metrics = new ViewNodeMetrics();
    metrics.setAscentBounds(xPos, yPos - height, newXPos - xPos, height);
    metrics.setDescentBounds(xPos, yPos, newXPos - xPos, depth);
    //metrics.draw(g2);
    return metrics;
  }
}


