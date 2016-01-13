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
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  This class is the graphical representation of an
 *  {@link net.mumie.mathletfactory.algebra.op.node.VariableOp}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class VariableView extends ViewNode {
  
  
  
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    String string = m_master.toString(0, null);
    //System.out.println("checking "+m_master.toString());
    
    Font origFont = g2.getFont();
    Font variableFont;
    if(!(m_master instanceof VariableOp) || OpParser.GREEK_ALPHABET.indexOf(((VariableOp)m_master).getIdentifier())>=0)
      variableFont = origFont;
      else
      variableFont = origFont.deriveFont(Font.ITALIC);
    g2.setFont(variableFont);
    String varName = "";
    String subName = null;
    int subIndex = string.indexOf('_');
    if(subIndex > 0) {
    	subName = string.substring(subIndex + 1);
    	varName = string.substring(0, subIndex);
    	// remove brackets from index string
    	subName = subName.replaceAll("[\\{\\}]", "");
    } else {
    	for(int i = 0; i < string.length(); i++) {
    		char c = string.charAt(i);
    		if(Character.isLetter(c)) {
    			varName += c;
    		} else {
    			subName = string.substring(i);
    			break;
    		}
    	}
    }
    Rectangle2D bounds = drawText(g2, (float) xPos, (float) yPos, varName);
    // avoid different exponent's heights in polynoms (e.g. "x^3+2x^2")
    if(getMaster().getFactor().equals(NumberFactory.newInstance(getMaster().getNumberClass(), 1))) {
    	double diff = g2.getFontMetrics().getHeight() - bounds.getHeight() - 1;// one pixel difference elsewhere!
    	bounds.setFrame(bounds.getX(), bounds.getY()-diff, bounds.getWidth(), bounds.getHeight()+diff);
    }
    double ascent = Math.max(-bounds.getMinY(), origFont.getSize()*0.6);
    double descent = bounds.getHeight() - ascent;
    ViewNodeMetrics metrics = new ViewNodeMetrics();
    metrics.setAscentBounds(xPos, yPos - ascent,
                            bounds.getWidth()+getSpacer(g2), ascent);
    metrics.setDescentBounds(xPos, yPos, bounds.getWidth()+getSpacer(g2), descent);

    if(subName != null) {
    	double oldWidth = metrics.getWidth();
      PowerView.drawSubscript(g2, metrics, printFlags, subName, PowerView.SUBSCRIPT, OFFLINEGRAPHICS);
    	// exponent and index must have equal x coordinates
    	if(getMaster().getExponent() >= 0 && getMaster().getExponent() != 1) {
        Rectangle2D ascentBounds = metrics.getAscentBounds();
        ascentBounds.setFrame(ascentBounds.getX(), ascentBounds.getY(), oldWidth, ascentBounds.getHeight());
        Rectangle2D descentBounds = metrics.getDescentBounds();
        descentBounds.setFrame(descentBounds.getX(), descentBounds.getY(), oldWidth, descentBounds.getHeight());
    	}
    }
    //metrics.draw(g2);
    g2.setFont(origFont);
    return metrics;
  }
}


