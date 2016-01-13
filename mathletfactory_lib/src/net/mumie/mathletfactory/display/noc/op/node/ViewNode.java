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
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import net.mumie.mathletfactory.display.noc.op.ViewNodeMetrics;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 *  This class serves as a base class for all operation view nodes. Operation
 *  view nodes represent the graphical view of an algebraic
 *  {@link net.mumie.mathletfactory.algebra.op.node.OpNode Operation Node}
 *  and are also organized as nodes forming a tree.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public abstract class ViewNode implements Cloneable {
  
  /** The master node, the model of this view */
  protected OpNode m_master;
  
  /** The child nodes of this view node. */
  protected ViewNode[] m_children = null;
  
  /** The metrics of the rendered text (bounds, height, depth etc.) */
  private ViewNodeMetrics m_metrics = new ViewNodeMetrics();
  
  protected DecimalFormat m_format = null;
  
  /** Used for temporary offline-rendering (e.g. determining the size of the panel) */    
  protected final Graphics2D OFFLINEGRAPHICS =
    (Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY).getGraphics();
  
  /** Returns the bounds covering the rendered text of this node */
  public ViewNodeMetrics getMetrics() {
    return m_metrics;
  }
  
  /** Sets the <code>OpNode</code> that this node is the view for. */
  public void setMaster(OpNode node) {
    m_master = node;
  }
  
  /** Inserts the argument as children for this node.  */
  public void setChildren(ViewNode[] children) {
    m_children = children;
  }
  
  /** Draws the actual expression without parentheses, factor and exponent */
  protected abstract ViewNodeMetrics drawExpression(Graphics2D g2, double xPos,
                                            double yPos, int printFlags);
  
  /**
   *  Draws the factor of the expression represented by this node. This method
   *  is called from {@link #draw} before the actual expression is drawn.
   */
  protected ViewNodeMetrics drawFactor(Graphics2D g2, double xPos, double yPos, int printFlags) {
	  ViewNodeMetrics metrics = new ViewNodeMetrics();
	  metrics.setAscentBounds(xPos, yPos, 0, 0);
	  metrics.setDescentBounds(xPos, yPos, 0, 0);
	  
	  MNumber factor = getMaster().getFactor();
	  
	  MNumber aNumber = NumberFactory.newInstance(factor.getClass());

	  if ((printFlags & OpNode.PRINT_FACTOR) != 0 && !factor.equals(aNumber.setDouble(1.0))) {
		  if ((printFlags & OpNode.PRINT_SIGN) == 0) {
			  factor.abs();
		  }
		  
		  if (factor.equals(aNumber.setDouble(-1.0))) {
			  metrics.setAscentBounds(drawText(g2, xPos, yPos, "-"));
		  } else {
			  FontRenderContext frc = g2.getFontRenderContext();
			  metrics = NumberView.drawNumber(g2, xPos, yPos, factor, printFlags, OFFLINEGRAPHICS, m_format);
			  double newXPos = xPos + metrics.getWidth(); 
			  double step = new TextLayout("*",g2.getFont(), frc).getBounds().getWidth() * 1.6;
		      newXPos += step/4;
		      //System.out.println("font can display u22c5: "+g2.getFont().canDisplay('\u22c5'));
		      g2.drawString("\u22c5", (float) newXPos, (float) yPos);
		      newXPos += step;
		      metrics.setAscentBounds(metrics.getAscentBounds().getX(), metrics.getAscentBounds().getY(), newXPos - xPos , metrics.getHeight());
		  }
	  }
	  return metrics;
  }
  
  /**
   *  Draws the expression represented by this node onto <code>g2</code> using 
   *  position (xPos, yPos) as lower left corner.  
   *  @return ViewNodeMetrics the metrics of the drawn expression.
   */
  public ViewNodeMetrics draw(Graphics2D g2, double xPos, double yPos, int printFlags) {
    
    //System.out.println("drawing "+this);
    // the "cursor"
    double newXPos = xPos;
    double ascent = 0;
    double descent = 0;
    
    //System.out.println("decimal format is: "+m_format.getMaximumFractionDigits());
    
    // draw factor if any. MultOpViews must draw the factor in drawExpression
    // because they might be fractions, where the factor is drawn in the numerator
    ViewNodeMetrics factorMetrics = new ViewNodeMetrics();
    boolean printParentheses = false;
    if (getMaster().getExponent() >= 0) {
      factorMetrics = drawFactor(g2, xPos, yPos, printFlags);
      // only add factor's metrics if a factor is drawn
      if(factorMetrics.getWidth() > 0) {
	      newXPos += factorMetrics.getWidth() + getSpacer(g2);
	      ascent = factorMetrics.getAscentBounds().getHeight();
	      descent = Math.max(descent, factorMetrics.getDescentBounds().getHeight());
	      printParentheses = newXPos != xPos + getSpacer(g2);
      }
      // write paranthesises if exponent is positive
      printParentheses = printParentheses || (printFlags & OpNode.PRINT_EXPONENT ) != 0 && getMaster().getExponent() >= 0 && getMaster().getExponent() != 1;
    }
    
    // draw expression with parentheses or without
    ViewNodeMetrics expressionMetrics;
    
    // draw parentheses if needed
    if (printParentheses && (m_master.parenthesesNeeded() && (printFlags & OpNode.PRINT_PARENTHESES) != 0)) {
      // I. draw the left parenthesis
      // make the font as high as the bounding box of the expression
      OFFLINEGRAPHICS.setFont(g2.getFont());
      expressionMetrics = drawFractionOrExpression(OFFLINEGRAPHICS, 0, 0, printFlags);
   
      // draw the opening parenthesis behind the factor
      newXPos += getSpacer(g2);
      ViewNodeMetrics parenMetrics = drawParenShape(g2, newXPos, yPos, "(", expressionMetrics);
      newXPos += parenMetrics.getWidth();
      ascent = Math.max(ascent, parenMetrics.getAscentBounds().getHeight());
      descent = Math.max(descent, parenMetrics.getDescentBounds().getHeight());
    }
      // II. draw the expression plain or as fraction
    
    expressionMetrics = drawFractionOrExpression(g2, newXPos, yPos, printFlags);
    newXPos += expressionMetrics.getWidth();
    ascent = Math.max(ascent, expressionMetrics.getAscentBounds().getHeight());
    descent = Math.max(descent, expressionMetrics.getDescentBounds().getHeight());
      
    //expressionMetrics.draw(g2);
      
    // III. draw the right parenthesis
    if (printParentheses && (m_master.parenthesesNeeded() && (printFlags & OpNode.PRINT_PARENTHESES) != 0)) {
        // I. draw the left parenthesis
        // make the font as high as the bounding box of the expression
        // draw the opening parenthesis behind the factor
        newXPos += getSpacer(g2);
        ViewNodeMetrics parenMetrics = drawParenShape(g2, newXPos, yPos, ")", expressionMetrics);
        newXPos += parenMetrics.getWidth();
        ascent = Math.max(ascent, parenMetrics.getAscentBounds().getHeight());
        descent = Math.max(descent, parenMetrics.getDescentBounds().getHeight());
    } 
    
    // return the rectangle that contains everything from the factor to the right parenthesis
    m_metrics.setAscentBounds(xPos, yPos - ascent, newXPos - xPos, ascent);
    m_metrics.setDescentBounds(xPos, yPos, newXPos - xPos, descent);
    //m_metrics.draw(g2);
    if ((printFlags & OpNode.PRINT_EXPONENT ) != 0 && getMaster().getExponent() >= 0 && getMaster().getExponent() != 1)
      m_metrics = PowerView.drawSubscript(g2, m_metrics, printFlags, getMaster().getExponent() + "", PowerView.SUPERSCRIPT, OFFLINEGRAPHICS);
    //m_metrics.draw(g2);
    return m_metrics;
  }
  
  /**
   * This method decides, whether a fraction or simply an expression should be drawn onto
   * <code>g2</code>, using (xPos,yPos) as the lower left corner.
   * 
   * @return ViewNodeMetrics The metrics of the drawn fraction or expression
   * @see #drawExpression
   * @see MultView#drawFraction
   */
  private ViewNodeMetrics drawFractionOrExpression(Graphics2D g2, double xPos, double yPos, int printFlags){
    ViewNodeMetrics expressionMetrics = new ViewNodeMetrics();
    double newXPos = xPos;
    // for negative exponents, draw a fraction
    if (this.getMaster().getExponent() < 0) {
    	ViewNode node = (ViewNode)this.clone();
    	node.setMaster(getMaster().deepCopy(true, true));
    	node.getMaster().setExponent(-1 * getMaster().getExponent());
    	expressionMetrics = MultView.drawFraction(g2, newXPos, yPos, getMaster().getFactor(), node, printFlags, printFlags, OFFLINEGRAPHICS, m_format);
    } else {
    	expressionMetrics = drawExpression(g2, newXPos, yPos, printFlags);
    }
    //expressionMetrics.draw(g2);
    return expressionMetrics;
  }
    
  /**
   * Draws the parenthesis specified by <code>glyph</code> onto <code>g2</code>, using
   * (xPos,yPos) as lower left corner. <code>expressionMetrics</code> is used for the
   * height of the parenthesis.
   * 
   * @return ViewNodeMetrics the metrics of the drawn parenthesis
   */
  protected static ViewNodeMetrics drawParenShape(Graphics2D g2, double xPos, double yPos,
                                    String glyph, ViewNodeMetrics expressionMetrics){
    ViewNodeMetrics parenMetrics = new ViewNodeMetrics();
    FontRenderContext frc = g2.getFontRenderContext();
    // if we have to draw an "|", simply draw a Line2D
    if(glyph.equals("|")){
      double height = expressionMetrics.getAscentBounds().getHeight();
      double depth = expressionMetrics.getDescentBounds().getHeight();
      
      double lineWidth = new TextLayout(".", g2.getFont(),frc).getBounds().getHeight()/3;
      g2.setStroke(new BasicStroke((float)lineWidth));
      
      g2.draw(new Line2D.Double(xPos + lineWidth, yPos - height,
                                xPos + lineWidth, yPos + depth));
      parenMetrics.setAscentBounds(xPos, yPos - height, 2*lineWidth + getSpacer(g2), height);
      parenMetrics.setDescentBounds(xPos, yPos, 2*lineWidth + getSpacer(g2), depth);
      //parenMetrics.draw(g2);
      return parenMetrics;
    }
    GlyphVector gv = g2.getFont().createGlyphVector(frc, glyph);
    Shape glyphShape = gv.getGlyphOutline(0);
    Rectangle2D parenBounds = glyphShape.getBounds2D();
    double glyphDescent = parenBounds.getY() + parenBounds.getHeight();
    
    // scale the parenthesis up to the height of the surrounded expression
    double scaleFactor = Math.max(expressionMetrics.getBounds().getHeight() / parenBounds.getHeight(), 1);
    AffineTransform scale = AffineTransform.getScaleInstance(1, scaleFactor);
    glyphDescent *= scaleFactor;
    double parenYPos = Math.max(yPos + expressionMetrics.getDescentBounds().getHeight(), yPos + glyphDescent);
    
    Shape parenShape = AffineTransform.getTranslateInstance(xPos, parenYPos - glyphDescent)
      .createTransformedShape(scale.createTransformedShape(glyphShape));
    g2.fill(parenShape);
    
    // take measures for metrics
    double width = parenShape.getBounds().getWidth() + getSpacer(g2)/2;
    double height = parenShape.getBounds().getHeight() - (parenYPos - yPos);
    double depth = (parenYPos - yPos);
    
    parenMetrics.setAscentBounds(xPos, yPos - height,
                                 width, height);
    parenMetrics.setDescentBounds(xPos, yPos, width, depth);
    //parenMetrics.draw(g2);
    return parenMetrics;
  }
  
  /**
   *  Return the {@link net.mumie.mathletfactory.algebra.op.node.OpNode}, that
   *  is the model for this view node.
   */
  protected OpNode getMaster() {
    return m_master;
  }
  
  /**
   *  Returns the baseline for the expression represented by this node.
   */
  public double getBaseline() {
    return m_metrics.getBaseline();
  }
  
  /** 
   * Returns the overall bounds of the expression represented by this node.
   */
  public Rectangle2D getBounds() {
    return m_metrics.getBounds();
  }
  
  /**
   * Draws <code>text</code> onto <code>g2</code> beginning using position (xPos, yPos)
   * as lower left corner.  
   * @return Rectangle2D the bounds of the drawn text.
   */
  protected static Rectangle2D drawText(Graphics2D g2, double xPos, double yPos, String text){
    TextLayout textLayout = new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
    textLayout.draw(g2, (float)xPos, (float)yPos);
    return textLayout.getBounds();
  }
  
  /**
   * A utility method that returns a rectangle that is horizontally centered 
   * betwwen <code>left</code> and <code>right</code> and has <code>y</code>
   * as upper boundary.
   */
  protected static Rectangle2D centerHorizontally(Rectangle2D frame, double left,
                                               double right, double y) {
    frame.setRect(left + (right - left) / 2 - frame.getWidth() / 2,  y,
                  frame.getWidth(), frame.getHeight());
    return frame;
  }
  
  /** 
   * Create a "deep copy" of the subtree rooted by this node.
   * @see java.lang.Object#clone()
   */
  public Object clone() {
    ViewNode clone = null;
    try {
      clone = (ViewNode) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    clone.m_master = (OpNode) m_master.clone();
    clone.m_metrics = new ViewNodeMetrics(m_metrics);
    if (m_children != null) {
      ViewNode[] clonedChildren = new ViewNode[m_children.length];
      for (int i = 0; i < clonedChildren.length; i++)
        clonedChildren[i] = (ViewNode) clone.m_children[i].clone();
      clone.setChildren(clonedChildren);
    }
    return clone;
  }
  
  /** this method is used for creating a small "dotwide" space depending on 
   * the current font.
   */
  protected static double getSpacer(Graphics2D g2){
    return 1.0 + g2.getFont().getSize() / 8.0;
  }
  
  /** Returns {@link {@link net.mumie.mathletfactory.algebra.op.node.OpNode#toString master.toString()}. */ 
  public String toString(){
    return m_master.toString();
  }
  
  /** 
   * Sets the given display format for decimal (i.e. MDouble) numbers. 
   * Otherwise the format using {@link NumberView#DEFAULT_PATTERN} is used.
   */
  public void setDecimalFormat(DecimalFormat format){
    m_format = format;
    if(m_children != null)
      for(int i=0;i<m_children.length;i++)
        m_children[i].setDecimalFormat(format);
  }
  /** 
   * Returns the display format for decimal (i.e. MDouble) numbers. 
   */
  public DecimalFormat getDecimalFormat(){
    if(m_format != null)
      return m_format;
    return new DecimalFormat(NumberView.DEFAULT_PATTERN);    
  }
  
}


