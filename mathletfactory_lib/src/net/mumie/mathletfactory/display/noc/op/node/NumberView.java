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
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.math.number.MRealNumber;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.NumberOp}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class NumberView extends ViewNode {

  public final static String DEFAULT_PATTERN = new String("#.##");

  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags) {
    return drawNumber(g2, xPos, yPos, m_master.getResult(0), printFlags, OFFLINEGRAPHICS, m_format);
  }

  /**
   *  Draws the given number. If the flag <code>PRINT_FACTOR</code> has not been
   *  set, this means that only the sign, if any, should be drawn.
   */
  public static ViewNodeMetrics drawNumber(Graphics2D g2, double xPos,  double yPos,  MNumber number,
    int printFlags, Graphics2D offlineGraphics, DecimalFormat format) {
    Font fontUsed = g2.getFont();

    // if the node is a rational with denominator != 1 it is rendered as fraction
    if (number.getClass().equals(MRational.class) && ((MRational) number).getDenominator() != 1)
        return MultView.drawFraction(g2, xPos, yPos, "" + ((MRational) number).getNumerator(),
          "" + ((MRational) number).getDenominator(),
          printFlags,
          printFlags, offlineGraphics, format);
    
    ViewNodeMetrics metrics = new ViewNodeMetrics();
    // if the node has infinite value, render the appropriate character
    String numberString;
    if (number.isInfinity()) {
      numberString = "\u221e";
      if (number instanceof MRealNumber && ((MRealNumber) number).isNegInfinity())
        numberString = "-\u221e";
    } else if (number instanceof MDouble || number instanceof MComplex) {
      if(number instanceof MComplex && ((MComplex)number).getIm() != 0)
        numberString = "("+number.toString(format)+")";
      else
        numberString = number.toString(format);
    } else if (number instanceof MComplexRational && !number.isZero()){
        MComplexRational z = (MComplexRational)number;
        ViewNodeMetrics reMetrics = null, imMetrics = null;
        Rectangle2D plusRect = new Rectangle2D.Double();
        if(!z.getRe().isZero()){
          reMetrics = drawNumber(g2, xPos, yPos, z.getRe(), printFlags, offlineGraphics, format);
          if(!z.getIm().isZero())
            plusRect = drawText(g2, xPos + reMetrics.getWidth(), yPos, "+");
        }
        if(!z.getIm().isZero()){
          imMetrics = drawNumber(g2, xPos + (reMetrics != null ? reMetrics.getWidth() : 0) + plusRect.getWidth(), yPos, z.getIm(), printFlags, offlineGraphics, format);
        }
        if(imMetrics != null){
          if(reMetrics != null){
            metrics.setAscentBounds(reMetrics.getAscentBounds().createUnion(imMetrics.getAscentBounds()));
            metrics.setDescentBounds(reMetrics.getAscentBounds().createUnion(imMetrics.getAscentBounds()));
          } else
            metrics = imMetrics;
        } else
            metrics = reMetrics;
        return metrics;
    }
    // otherwise the string of the number is drawn
    else
      numberString = number.toString();

    
    if ((printFlags & OpNode.PRINT_SIGN) == 0)
      numberString = numberString.replaceAll("^-", "");
    if ((printFlags & OpNode.PRINT_FACTOR) == 0)
      numberString = numberString.replaceAll("[^-]*", "");
    if (numberString.equals("")) {
      metrics.setAscentBounds(xPos, yPos, 0, 0);
      metrics.setDescentBounds(xPos, yPos, 0, 0);
      return metrics;
    }
		TextLayout textLayout = new TextLayout(numberString, g2.getFont(), g2.getFontRenderContext());
		textLayout.draw(g2, (float)xPos, (float)yPos);
		Rectangle2D bounds = textLayout.getBounds();

    // numbers with a comma have a descent
    float ascent = (float) Math.max(textLayout.getAscent(), fontUsed.getSize() * 0.6);
		float descent = textLayout.getDescent();
		//System.out.println("ascent "+ascent+", descent "+descent);
    metrics.setAscentBounds(xPos, yPos - ascent, bounds.getWidth() + getSpacer(g2), ascent);
    metrics.setDescentBounds(xPos, yPos, 0, descent);
    
    //g2.draw(m_bounds);
    return metrics;
  }
}
