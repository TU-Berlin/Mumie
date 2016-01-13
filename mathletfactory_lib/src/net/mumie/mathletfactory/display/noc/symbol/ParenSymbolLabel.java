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

package net.mumie.mathletfactory.display.noc.symbol;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 *  This class represents a label for a parenthesis or any other symbol that 
 *  needs to be rendered as high as the alignable components height.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

public class ParenSymbolLabel extends JComponent {

  /** the string representation of the symbol to be rendered. */
  private String m_parenString;
  
  /** Needed for determining the size of this component. */
  private int m_width;

  private double m_scaleY = 1;
  
  /** 
   *  Constructs a ParenSymbolLabel that renders <code>parenString</code>
   *  using the given font.
   */
  public ParenSymbolLabel(String parenString, Font font) {
    m_parenString = parenString;
    setFont(font);
  }
  
  /** 
   *  Constructs a ParenSymbolLabel that renders <code>parenString</code>
   *  using the given font.
   */
  public ParenSymbolLabel(String parenString, Font font, double scaleY) {
    this(parenString,font);
  	m_scaleY = scaleY;
  }

  /**
   * Renders the symbol with maximal (visible) height on the component.
   * @see javax.swing.JComponent#paintComponent(Graphics)
   */
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setBackground(getBackground());
    g2.clearRect(0, 0, getWidth(), getHeight());
    // debug
//    g2.drawRect(0, 0, getWidth(), getHeight());

    // create the glyph as a Shape
    Shape glyphShape = getFont().createGlyphVector(((Graphics2D) getGraphics())
    .getFontRenderContext(), m_parenString).getOutline();
    Rectangle2D parenBounds = glyphShape.getBounds2D();

    // scale the glyph up to the component's height
    double scaleFactor = getHeight() / parenBounds.getHeight();
    AffineTransform scale = AffineTransform.getScaleInstance(m_scaleY, scaleFactor);
    glyphShape = scale.createTransformedShape(glyphShape);  
    
    // move the glyph with the baseline to the bottom of the component
	  glyphShape = AffineTransform.getTranslateInstance(2, 
	  scaleFactor * -parenBounds.getY()).createTransformedShape(glyphShape); 
    m_width = (int)(1.5*(glyphShape.getBounds2D().getMinX()+glyphShape.getBounds2D().getWidth())+1);
    //System.out.println("width is "+m_width);

    // draw the transformed glyph shape
    g2.fill(glyphShape);
  }
  
  /** The preferred size is the minimum size. */
  public Dimension getPreferredSize(){
    return getMinimumSize();
  }

  /** 
   * The minimum size is the height of the font and three times the width of 
   * the paren symbol in unscaled mode. 
   */ 
  public Dimension getMinimumSize() {
    if(getGraphics() == null)
      return super.getMinimumSize();
    if(m_width == 0)
      m_width = (int) (new TextLayout(m_parenString, getFont(), ((Graphics2D)getGraphics()).getFontRenderContext())
            .getBounds().getWidth()*2+5);
    return new Dimension((int)m_width, getFont().getSize());
  }
}
