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
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 *  This class is the graphical representation of a
 *  {@link net.mumie.mathletfactory.algebra.op.node.MultOp}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MultView extends ViewNode {
  
  public ViewNodeMetrics drawExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    ViewNode[] pseudoViewNodes = createPseudoViewNodes();
    ViewNode numerator = pseudoViewNodes[0];
    ViewNode denominator = pseudoViewNodes[1];  
    if(denominator != null)
      return drawFraction(g2, xPos, yPos, numerator, denominator, printFlags, printFlags, OFFLINEGRAPHICS, m_format);
    else{    
      return numerator.draw(g2,xPos,yPos, printFlags);
    }
  }
  
  private ViewNode[] createPseudoViewNodes(){
    // distribute the children to numerator and denominator based on their exponents
    List numeratorChildren = new ArrayList();
    List numeratorChildrenMasters = new ArrayList();
    List denominatorChildren = new ArrayList();
    List denominatorChildrenMasters = new ArrayList();
    for(int i = 0; i < m_children.length; i++){
      if(m_children[i].m_master.getExponent() < 0){
    	  ViewNode node = (ViewNode)m_children[i].clone();
    	  node.getMaster().negateExponent();
    	  denominatorChildren.add(node);
    	  denominatorChildrenMasters.add(node.getMaster());
      }
      else {
    	  ViewNode node = (ViewNode)m_children[i].clone();
    	  numeratorChildren.add(m_children[i].clone());
    	  numeratorChildrenMasters.add(node.getMaster());
      }
    }
    ViewNode numerator = new PureMultView();
    ViewNode denominator = new PureMultView();
    
    if(numeratorChildren.size() == 0){
      numerator = new NumberView();
      numerator.setMaster(new NumberOp(m_master.getNumberClass(),1));
    }
    // if one of the nodes has only one children, replace the node by its child
    else if(numeratorChildren.size() == 1)
      numerator =(ViewNode)((ViewNode)numeratorChildren.get(0)).clone();
    else { // more than one children -> new MultOp(numeratorChildren) as pseudo master
      ViewNode[] numeratorChildrenArray = new ViewNode[numeratorChildren.size()];
      OpNode[] numeratorChildrenMastersArray = new OpNode[numeratorChildrenMasters.size()];      
      // insert the children for the pseudo view nodes
      numerator.setChildren((ViewNode[])numeratorChildren.toArray(numeratorChildrenArray));
      
      // set for each numeratorchild the master as a MultOp with
      // MultOp.getChildren(i) == m_children[i].getMaster() for each i.
      MultOp numeratorPseudoMaster = new MultOp(m_master.getNumberClass());
      numeratorPseudoMaster.setChildren((OpNode[])numeratorChildrenMasters.toArray(numeratorChildrenMastersArray));
      numerator.setMaster(numeratorPseudoMaster);
    }
    //numerator.getMaster().multiplyFactor(m_master.getFactor());
    //numerator.getMaster().multiplyExponent(m_master.getExponent());
    
    if(denominatorChildren.size() == 0){
      return new ViewNode[] {numerator, null};
    }
    else {
      if(denominatorChildren.size() == 1)
        denominator = (ViewNode)((ViewNode)denominatorChildren.get(0)).clone();
      else{
        ViewNode[] denominatorChildrenArray = new ViewNode[denominatorChildren.size()];
        OpNode[] denominatorChildrenMastersArray = new OpNode[denominatorChildrenMasters.size()];
        MultOp denominatorPseudoMaster = new MultOp(m_master.getNumberClass());
        denominator.setChildren((ViewNode[])denominatorChildren.toArray(denominatorChildrenArray));
        denominatorPseudoMaster.setChildren((OpNode[])denominatorChildrenMasters.toArray(denominatorChildrenMastersArray));
        denominator.setMaster(denominatorPseudoMaster);
      }
    }
    //denominator.getMaster().multiplyExponent(m_master.getExponent());
    return new ViewNode[] {numerator, denominator};
  }
  
  /**
   * Draws a Fraction onto <code>g2</code> with (xPos, yPos) as left baseline with the String or {@link ViewNode} 
   * <code>numerator</code> and <code>denominator</code> and corresponding print flags.
   * 
   * @return ViewNodeMetrics the metrics of the drawn fraction.
   */
  static ViewNodeMetrics drawFraction(Graphics2D g2, double xPos, double yPos, Object numerator,
                                 Object denominator, int numeratorPrintFlags,
                                 int denominatorPrintFlags, Graphics2D offlineGraphics, DecimalFormat format){
    Font origFont = g2.getFont();
    Font fractionFont = origFont.deriveFont(origFont.getSize()*0.9f);
    g2.setFont(fractionFont);
    offlineGraphics.setFont(fractionFont);
    FontRenderContext frc = g2.getFontRenderContext();
    double numeratorDescent = 0;
    double denominatorDescent = 0;
    double minusWidth = 0;
    
    // set the stroke width of the fraction rule to the width of a "." and 
    // align it horizontally with the "-" in the used font
    TextLayout dotLayout = new TextLayout(".", origFont, frc);
    TextLayout minusLayout = new TextLayout("-", origFont, frc);
    double ruleOffset = minusLayout.getBounds().getHeight();
       
//    denominatorPrintFlags |= OpNode.PRINT_FACTOR; 	
//    
//    // check for minus
//    if(numerator instanceof ViewNode && denominator instanceof ViewNode) {
//      ViewNode numeratorNode = ((ViewNode)numerator);
//      boolean numeratorNegative = numeratorNode.getMaster().getSign() < 0;
//      boolean numeratorPrintSign = numeratorNegative && (numeratorPrintFlags & OpNode.PRINT_FACTOR) != 0;
//      ViewNode denominatorNode = ((ViewNode)denominator);
//      boolean denominatorNegative = denominatorNode.getMaster().getSign() < 0;
//      boolean denominatorPrintSign = denominatorNegative && (denominatorPrintFlags & OpNode.PRINT_FACTOR) != 0;
//      if(numeratorPrintSign && !denominatorPrintSign) {
//        Rectangle2D bounds = drawText(g2, xPos, yPos - ruleOffset/4, "-");
//        minusWidth = bounds.getWidth() + getSpacer(g2)*1.5; 
//        xPos += minusWidth;
//        numeratorPrintFlags &= ~OpNode.PRINT_SIGN;
//      } else if(!numeratorPrintSign && denominatorPrintSign) {
//        Rectangle2D bounds = drawText(g2, xPos, yPos - ruleOffset/4, "-");
//        minusWidth = bounds.getWidth() + getSpacer(g2)*1.5; 
//        xPos += minusWidth;
//        denominatorPrintFlags &= ~OpNode.PRINT_SIGN;
//      } else if(numeratorPrintSign && denominatorPrintSign) { // both negative
//        numeratorPrintFlags |= OpNode.PRINT_SIGN;
//        denominatorPrintFlags |= OpNode.PRINT_SIGN;
//      } else { /* both positive -> do nothing*/ }
//    }
    
    // I. Render numerator
    
    Rectangle2D numeratorBounds = null;
    TextLayout numeratorLayout = null;
    
    if(numerator instanceof ViewNode){
//      if(Math.abs(((ViewNode)numerator).getMaster().getFactor().getDouble()) == 1
//         && ((ViewNode)numerator).getMaster().getExponent() == 1)
//        numeratorPrintFlags &= ~OpNode.PRINT_PARENTHESES;
//      else if(numerator instanceof AddView)
//	      numeratorPrintFlags |= OpNode.PRINT_PARENTHESES;
      ViewNodeMetrics numeratorMetrics = ((ViewNode)numerator).draw(
        offlineGraphics, 0, 0, numeratorPrintFlags);
      numeratorDescent = numeratorMetrics.getDescentBounds().getHeight();
      numeratorBounds = numeratorMetrics.getBounds();
    }
    else {
      if(numerator instanceof MNumber)
         numeratorBounds = NumberView.drawNumber(offlineGraphics, 0, 0, 
         (MNumber)numerator, numeratorPrintFlags, offlineGraphics, format).getBounds();
      else{
        numeratorLayout = new TextLayout(numerator.toString(), fractionFont, frc);
        numeratorBounds = numeratorLayout.getBounds();
      }
    }
    
    // II. Render denominator
    
    TextLayout denominatorLayout = null;
    Rectangle2D denominatorBounds = null;
    //denominatorPrintFlags &= ~(OpNode.PRINT_EXPONENT_SIGN); // | OpNode.PRINT_SIGN
    if(denominator instanceof ViewNode){
      ViewNode denom = (ViewNode)denominator;
      // check if the master has an exponentString == "", if so draw no parentheses
//      if(denom.getMaster().getExponentString(denominatorPrintFlags).equals("")
//        && !(denom instanceof PowerView))
//          denominatorPrintFlags &= ~OpNode.PRINT_PARENTHESES;
      ViewNodeMetrics denominatorMetrics = denom.draw(offlineGraphics, 0, 0, denominatorPrintFlags);
      denominatorDescent = denominatorMetrics.getDescentBounds().getHeight();
      denominatorBounds = denominatorMetrics.getBounds();
    } else {
      if(denominator instanceof MNumber)
         denominatorBounds = NumberView.drawNumber(offlineGraphics, 0, 0,
         (MNumber)denominator, denominatorPrintFlags, offlineGraphics, format).getBounds();
      else{
        denominatorLayout = new TextLayout(denominator.toString(), fractionFont, frc);
        denominatorBounds = denominatorLayout.getBounds();
      }
    }
    
    
    // draw the fraction rule
    double ruleWidth = dotLayout.getBounds().getHeight()/2;
    g2.setStroke(new BasicStroke((float)ruleWidth));
    double xRight = xPos + getSpacer(g2) + Math.max(numeratorBounds.getWidth(), denominatorBounds.getWidth())*1.1;
    g2.draw(new Line2D.Double(xPos + getSpacer(g2), yPos - ruleOffset,
                              xRight + getSpacer(g2), yPos - ruleOffset));
    
    // III. Draw numerator
    
    // the height of the numerator is the height of its bounding box + the offsets
    // above the baseline
    double numeratorHeight = numeratorBounds.getHeight() + getSpacer(g2) + ruleOffset + ruleWidth;
    
    // center the numerator over the rule
    numeratorBounds = centerHorizontally(numeratorBounds, xPos + getSpacer(g2)*2, xRight,
                                         yPos - numeratorHeight - numeratorDescent);
    //g2.draw(numeratorBounds);
    
    if(numerator instanceof ViewNode)
        ((ViewNode)numerator).draw(
        g2, numeratorBounds.getMinX(), numeratorBounds.getMaxY(),
        numeratorPrintFlags);
    else{
      if(numerator instanceof MNumber)
        numeratorBounds = NumberView.drawNumber(g2, numeratorBounds.getMinX(),
                  numeratorBounds.getMaxY(), (MNumber)numerator, numeratorPrintFlags, offlineGraphics, format).getBounds();
      else
        numeratorLayout.draw(g2, (float)numeratorBounds.getMinX(),
                               (float)numeratorBounds.getMaxY());
    }
    
    // IV. Draw denominator
    
    double denominatorHeight = denominatorBounds.getHeight() - ruleOffset + 1.5 * getSpacer(g2);
    
    // center the denominator under the rule
    denominatorBounds = centerHorizontally(
      denominatorBounds, xPos + getSpacer(g2)*2, xRight, yPos - ruleOffset - denominatorDescent + 1.5 * getSpacer(g2));
    //g2.draw(denominatorBounds);
    
    if(denominator instanceof ViewNode)
        ((ViewNode)denominator).draw(
      g2, denominatorBounds.getMinX(), denominatorBounds.getMaxY(), denominatorPrintFlags);
    else {
      if(denominator instanceof MNumber)
        denominatorBounds = NumberView.drawNumber(g2,denominatorBounds.getMinX(),
                  denominatorBounds.getMaxY(), (MNumber)denominator, denominatorPrintFlags, offlineGraphics, format).getBounds();
      else
        denominatorLayout.draw(g2, (float)denominatorBounds.getMinX(),
                             (float)denominatorBounds.getMaxY());
    }
    // V. Recover state and set metrics
    
    g2.setFont(origFont);
    // set height and depth
    double width = xRight - xPos + 2*getSpacer(g2) + minusWidth;
    ViewNodeMetrics metrics = new ViewNodeMetrics();
    metrics.setAscentBounds(xPos, yPos - numeratorHeight,
                            width, numeratorHeight);
    metrics.setDescentBounds(xPos, yPos, width, denominatorHeight);
    //metrics.draw(g2);
    return metrics;
  }
  
}
