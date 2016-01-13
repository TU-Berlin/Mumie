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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.NrtOp}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class NrtView extends ViewNode
{
   
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    
    ViewNodeMetrics metrics = drawNrt(g2, xPos, yPos, m_children[0]);
    return metrics;
  }
  
  /**
   *  Draws the nth root of a subexpression.
   */
  public ViewNodeMetrics drawNrt(Graphics2D g2, double xPos, double yPos,
                             ViewNode childExpression){
    OFFLINEGRAPHICS.setFont(g2.getFont());
    ViewNodeMetrics nrtMetrics = new ViewNodeMetrics();
    ViewNodeMetrics expressionMetrics =  new ViewNodeMetrics(m_children[0].draw(OFFLINEGRAPHICS, 0,0, OpNode.PRINT_ALL));
    FontRenderContext frc = g2.getFontRenderContext();
    int n = ((NrtOp)m_master).getN();
    
    double height = 0;
    
    // the root's hook (the "v"-shape) should be as wide as a "-" in the current font
    TextLayout minusLayout = new TextLayout("-",g2.getFont(),frc);
    double minusWidth = minusLayout.getBounds().getWidth();
    
    TextLayout dotLayout = new TextLayout(".", g2.getFont(), frc);
    double lineWidth = dotLayout.getBounds().getHeight() / 1.5f;

    double newXPos = xPos + getSpacer(g2)/2;
    double newYPos = yPos - expressionMetrics.getAscentBounds().getHeight()/2;
    
    // 1. draw the "n" if n != 2
    if(n != 2){
      Font origFont = g2.getFont();
      Font nFont = origFont.deriveFont(origFont.getSize() * 0.8f);
      g2.setFont(nFont);
      TextLayout nText = new TextLayout(""+n, nFont, g2.getFontRenderContext());
      nText.draw(g2, (float) (newXPos), (float) (newYPos - 2*lineWidth));
      g2.setFont(origFont);
      height = yPos - (newYPos + lineWidth + nFont.getSize());
      newXPos += getSpacer(g2)/2 + nText.getBounds().getWidth();
    }
    
    // adjust position of subexpression metrics: baseline on the yPos and minusWidth on the xPos
    expressionMetrics.setAscentBounds(
      newXPos + getSpacer(g2) + minusWidth, yPos - expressionMetrics.getAscentBounds().getHeight(),
      expressionMetrics.getWidth(), expressionMetrics.getAscentBounds().getHeight());
    expressionMetrics.setDescentBounds(
      newXPos + getSpacer(g2) + minusWidth, yPos,
      expressionMetrics.getAscentBounds().getWidth(), expressionMetrics.getDescentBounds().getHeight());
    //expressionMetrics.draw(g2);
    
    // set the stroke width to 3/2 the width of a "." in the used font
    g2.setStroke(new BasicStroke((float)lineWidth));
    
    // 2. draw the content inside the root
    childExpression.draw(g2, expressionMetrics.getBounds().getMinX(),
                         expressionMetrics.getBaseline(), OpNode.PRINT_ALL);
    
    // 3. draw the root Shape
    GeneralPath nrtShape = new GeneralPath();
    
    // 3.1 start at ascent/2 above the lower left corner
    nrtShape.moveTo((float)newXPos, (float)newYPos);
    
    // 3.2 small horizontal line/move spacer/2 to the right
    newXPos += getSpacer(g2) / 2;
    nrtShape.lineTo((float)newXPos, (float)newYPos);
    
    // 3.3 line downwards/move half a "-" length to the right and ascent/2 + ascent/10 + descent down.
    newXPos += minusWidth / 2;
    newYPos += expressionMetrics.getAscentBounds().getHeight()*0.6 + expressionMetrics.getDescentBounds().getHeight();
    // now that we are at the deepest point of the root set the depth
    double depth = newYPos - yPos;
    nrtShape.lineTo((float) newXPos, (float) newYPos);
    g2.draw(nrtShape);
    nrtShape.reset();
    nrtShape.moveTo((float) newXPos, (float) newYPos);
    g2.setStroke(new BasicStroke((float)dotLayout.getBounds().getHeight()/2.f));
    
    // 3.4 line upwards/go another half "-" to the right (which is the left edge of
    // expression bounds) and rise 6/5*ascent + descent.
    newXPos += minusWidth / 2;
    newYPos -= expressionMetrics.getAscentBounds().getHeight()*1.2 + getSpacer(g2)
      + expressionMetrics.getDescentBounds().getHeight();
    nrtShape.lineTo((float) newXPos, (float) newYPos);
    
    // 3.5 horizontal line above content/draw a line over the whole expression and half a "-" more
    newXPos += expressionMetrics.getWidth() + minusWidth / 2;
    nrtShape.lineTo( (float) newXPos, (float) newYPos);
    // consider the linewidth, when calculating the bounds
    newYPos -= lineWidth;
    height = Math.max(yPos - newYPos, height);
    g2.draw(nrtShape);
    nrtMetrics.setDescentBounds(xPos, yPos, newXPos - xPos + getSpacer(g2), depth);
    nrtMetrics.setAscentBounds(xPos, yPos - height, newXPos - xPos + getSpacer(g2), height);
    
    //nrtMetrics.draw(g2);
    return nrtMetrics;
  }
  
}


