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
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.ConjOp}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class ConjView extends ViewNode {

  /* (non-Javadoc)
   * @see net.mumie.mathletfactory.display.noc.op.node.ViewNode#drawExpression(java.awt.Graphics2D, double, double, int)
   */
  protected ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags) {
    double newXPos = xPos + getSpacer(g2);
    ViewNodeMetrics metrics = m_children[0].draw(g2, newXPos, yPos, printFlags);
    // set the stroke width to 3/2 the width of a "." in the used font
    TextLayout dotLayout = new TextLayout(".", g2.getFont(), g2.getFontRenderContext());
    double lineWidth = dotLayout.getBounds().getHeight() / 1.5f;
    g2.setStroke(new BasicStroke((float)lineWidth));
    GeneralPath barShape = new GeneralPath();
    barShape.moveTo((float)newXPos,(float)(yPos-metrics.getAscentBounds().getHeight()-2*lineWidth));
    barShape.lineTo((float) (newXPos+metrics.getWidth()), (float)(yPos-metrics.getAscentBounds().getHeight()-2*lineWidth));
    g2.draw(barShape);
    metrics.setAscentBounds(xPos, yPos - metrics.getAscentBounds().getHeight()-4*lineWidth, 
                            metrics.getWidth()+getSpacer(g2), metrics.getAscentBounds().getHeight()+4*lineWidth);
    //metrics.draw(g2);                           
    return metrics;    
  }

}
