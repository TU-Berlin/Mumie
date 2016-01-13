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
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;

/**
 *  This class is the graphical representation of the subclasses of
 *  {@link net.mumie.mathletfactory.algebra.op.node.FunctionOpNode}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class FunctionView extends ViewNode {
  
  
  
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    String string = m_master.nodeToString();
    if(string.equals("re") || string.equals("im"))
      string = string.substring(0,1).toUpperCase()+string.substring(1);
    TextLayout layout = new TextLayout(string, g2.getFont(), g2.getFontRenderContext());
    Rectangle2D bounds = layout.getBounds();
    layout.draw(g2, (float) xPos, (float) yPos);

    double height = Math.max(bounds.getHeight(), g2.getFont().getSize()*0.6);
    double depth = 0;
    double newXPos = xPos + layout.getBounds().getWidth() + getSpacer(g2);
    
    // draw parentheses and child expression
    
    // make the parentheses as high as the bounding box of the expression
    OFFLINEGRAPHICS.setFont(g2.getFont());
    ViewNodeMetrics childMetrics = m_children[0].draw(OFFLINEGRAPHICS, 0, 0, OpNode.PRINT_ALL);
    
    // I. draw the opening parenthesis behind the factor
    ViewNodeMetrics parenMetrics = drawParenShape(g2, newXPos, yPos, "(", childMetrics);
    newXPos += parenMetrics.getWidth();
    height = Math.max(height, parenMetrics.getAscentBounds().getHeight());
    depth = Math.max(depth, parenMetrics.getDescentBounds().getHeight());
    
    // II. draw the child expression
    childMetrics = m_children[0].draw(g2, newXPos, yPos, OpNode.PRINT_ALL);
    newXPos += childMetrics.getWidth();
    
    // III. draw the closing parenthesis
    parenMetrics = drawParenShape(g2, newXPos, yPos, ")", childMetrics);
    newXPos += parenMetrics.getWidth();
    
    ViewNodeMetrics metrics = new ViewNodeMetrics();
    metrics.setAscentBounds(xPos, yPos - height, newXPos - xPos, height);
    metrics.setDescentBounds(xPos, yPos, newXPos - xPos, depth);
    
    //metrics.draw(g2);
    return metrics;
  }
}


