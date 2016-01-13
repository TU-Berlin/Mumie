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
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.PowerOp}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class PowerView extends ViewNode
{
  /**
   * The constant values for positioning subscripts. Used for <code>posAt</code> in 
   * {@link #drawSubscript}.
   * */
  public final static double SUBSCRIPT = -0.3, SUPERSCRIPT = 0.6; 
  
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    // draw fraction if exponent == -1
    if(m_children[1].getMaster() instanceof NumberOp &&
       m_children[1].getMaster().getResult().isInteger() &&
       m_children[1].getMaster().getResult().getDouble() == -1){
      String factorString = m_children[0].getMaster().getFactorString(OpNode.PRINT_ALL, null);
      if(factorString.endsWith("-") || factorString.length() == 0)
        factorString += "1";
      return MultView.drawFraction(g2, xPos, yPos, factorString, m_children[0],
                                     OpNode.PRINT_ALL, OpNode.PRINT_ALL, OFFLINEGRAPHICS, m_format);
    }
    double newXPos = xPos;
    double height = 0;
    double depth = 0;
    boolean parenthesesNeeded = false;
    if(getMaster().getChildren()[0].parenthesesNeeded()
    		|| !m_children[0].getMaster().getFactorString(OpNode.PRINT_ALL, m_format).equals("")
    		|| getMaster().getChildren()[0] instanceof PowerOp )
      parenthesesNeeded = true;
    OFFLINEGRAPHICS.setFont(g2.getFont());
    ViewNodeMetrics metrics = m_children[0].draw(OFFLINEGRAPHICS, xPos, yPos, OpNode.PRINT_ALL);
    ViewNodeMetrics parenMetrics;
    if(parenthesesNeeded){
      parenMetrics = drawParenShape(g2, xPos, yPos, "(", metrics);
      newXPos += parenMetrics.getWidth() + getSpacer(g2);
      height = parenMetrics.getAscentBounds().getHeight();
      depth = parenMetrics.getDescentBounds().getHeight();
    }
    metrics = m_children[0].draw(g2, newXPos, yPos, OpNode.PRINT_ALL);
    newXPos += metrics.getWidth() + getSpacer(g2);
    height = Math.max(height, metrics.getAscentBounds().getHeight());
    depth = Math.max(depth, metrics.getDescentBounds().getHeight());
    if(parenthesesNeeded){
      parenMetrics = drawParenShape(g2, newXPos, yPos, ")", metrics);
      newXPos += parenMetrics.getWidth();
      height = Math.max(height, parenMetrics.getAscentBounds().getHeight());
      depth = Math.max(depth, parenMetrics.getDescentBounds().getHeight());
    }
    
    metrics.setAscentBounds(xPos, yPos - height, newXPos - xPos, height);
    metrics.setDescentBounds(xPos, yPos, newXPos - xPos, depth);
    
    //metrics.setAscentBounds(ascent.getMinX(), ascent.getMinY(),
    //                    ascent.getWidth(), ascent.getHeight());
    return drawSubscript(g2, metrics, OpNode.PRINT_ALL & ~OpNode.PRINT_PARENTHESES, m_children[1], SUPERSCRIPT, OFFLINEGRAPHICS);
  }
  
   /**
   *  Draws a subscript or superscript of the expression represented by <code>subscript</code> 
   *  depending on <code>posAt</code> (a relative value between -1 and 1) and modifies the 
   *  given <code>expressionMetrics</code> accordingly. This method is called from {@link #draw} 
   *  after the actual expression has been drawn.
   */
  protected static ViewNodeMetrics drawSubscript(Graphics2D g2, ViewNodeMetrics expressionMetrics,
                                           int printFlags, Object subscript, double posAt, Graphics2D offlineGraphics){
    if(subscript instanceof String && subscript.toString().equals(""))
      return expressionMetrics;
    ViewNodeMetrics subscriptMetrics = new ViewNodeMetrics();
    double xPos = expressionMetrics.getBounds().getMaxX() - getSpacer(g2)/2;
    double yPos = expressionMetrics.getBaseline() - expressionMetrics.getAscentBounds().getHeight() * posAt;
    
    if(subscript instanceof ViewNode)
      subscriptMetrics.setFrom(((ViewNode)subscript).draw(offlineGraphics, 0, 0, printFlags));
    Font origFont = g2.getFont();
    Font newFont = origFont.deriveFont(origFont.getSize()/1.15f);
    g2.setFont(newFont);
                               
    Rectangle2D exponentBounds = null;
    
    if(subscript instanceof ViewNode){
      subscriptMetrics.setFrom(((ViewNode)subscript).draw(
        g2, xPos, yPos - subscriptMetrics.getDescentBounds().getHeight() * posAt, printFlags));
      exponentBounds = subscriptMetrics.getBounds();
    } else
      exponentBounds = drawText(g2, (float) xPos, (float) yPos, subscript.toString());

    //exponentBounds.setRect(xPos, yPos - exponentBounds.getHeight(),exponentBounds.getWidth(),exponentBounds.getHeight());    
    //g2.draw(exponentBounds);
    
    if(posAt > 0){
      double expAscent = exponentBounds.getHeight();
      expressionMetrics.setAscentBounds(expressionMetrics.getBounds().getMinX(), yPos - expAscent,
                            expressionMetrics.getWidth() + exponentBounds.getWidth() + 3*getSpacer(g2)/2,
                            expressionMetrics.getBaseline() - (yPos - expAscent));
    } else {
      double expDescent = yPos - expressionMetrics.getBaseline();
      //System.out.println("expDescent="+expDescent);
      expressionMetrics.setDescentBounds(expressionMetrics.getBounds().getMinX(), expressionMetrics.getBaseline(), 
                              expressionMetrics.getWidth() + exponentBounds.getWidth() + 3*getSpacer(g2)/2, 
                              expDescent);
    }
       
    g2.setFont(origFont);    
    //expressionMetrics.draw(g2);
    return expressionMetrics;
  }
}


