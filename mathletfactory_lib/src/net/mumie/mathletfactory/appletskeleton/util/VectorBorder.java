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

package net.mumie.mathletfactory.appletskeleton.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * This border draws a vector arrow above the first character of a text component.
 *
 * @author Gronau
 */
public class VectorBorder extends AbstractBorder {
  private int xOffset = 0;
  private int yOffset = 16;
	
  public VectorBorder(){
  }
  
  public VectorBorder(int yShift){
	  yOffset=yOffset-yShift;	  
  }
	
  public Insets getBorderInsets(Component comp) {
    return new Insets(10, 5, 10, 5);
  }

  public void paintBorder(
      Component c,
      Graphics g,
      int x,
      int y,
      int width,
      int height) {
    g.setFont(g.getFont().deriveFont((float) g.getFont().getSize() + 4));
//    FontMetrics metrics = g.getFontMetrics();
//    Rectangle2D rec = metrics.getStringBounds("\u2192", g.create());
    g.drawString("\u2192", xOffset, yOffset);
  }
}