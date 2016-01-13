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

package net.mumie.mathletfactory.appletskeleton.util.theme;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;

public class PlainButtonUI extends MetalButtonUI {

	private final static PlainButtonUI m_staticButtonUI = new PlainButtonUI();
	
	private final static Border m_staticBorder = new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(3, 3, 3, 3));
	
	private final static RollOverListener m_rollOverListener = new RollOverListener();
	
  public static ComponentUI createUI(JComponent c) {
    return m_staticButtonUI;
   }
   
  private static void ensureCorrectBorder(JComponent c) {
  	if(c.getBorder() != null)
  		c.setBorder(m_staticBorder);
  }
  
  private static void ensureCorrectListener(JComponent c) {
  	if(RollOverListener.hasListener(c) == false)
  		c.addMouseListener(m_rollOverListener);
  }
  
  protected void paintButton(Graphics g, JComponent c, boolean pressed) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int x = 0;
		int y = 0;
		int width = c.getWidth();
		int height = c.getHeight();
		Color bgColor = new Color(230, 240, 245);//MumieTheme.DEFAULT_THEME.getFocusColor();
		if(m_rollOverListener.isMouseOver(c))
			bgColor = MumieTheme.DEFAULT_THEME.getFocusColor();
		
		g2d.setColor(bgColor);
		g2d.fillRect(x, y, width, height);
		
		g2d.dispose();
  }
  
  public void paint(Graphics g, JComponent c) {
  	
		AbstractButton b = (AbstractButton) c;
		
//		if(b.getIcon() != null)
//		System.out.println("margin="+b.getMargin());
//		System.out.println("insets="+b.getInsets()); == empty border
//		System.out.println("textGap="+b.getIconTextGap()); == 4
		
		ButtonModel model = b.getModel();
		boolean pressed = model.isArmed() && model.isPressed();
  	paintButton(g, c, pressed);
  	if(pressed)
  	g.translate(1, 1);
  	super.paint(g, c);
	}
  
  protected void paintButtonPressed(Graphics g, AbstractButton b) {}

  public void update(Graphics g, JComponent c) {
  	ensureCorrectBorder(c);
  	ensureCorrectListener(c);
  	if (c.isOpaque()) {
  		g.setColor(c.getBackground());
  		g.fillRect(0, 0, c.getWidth(),c.getHeight());
  	}
  	paint(g, c);
	}
}
