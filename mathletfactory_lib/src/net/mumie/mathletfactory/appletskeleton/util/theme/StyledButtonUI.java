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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.metal.MetalComboBoxButton;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.layout.SimpleInsets;

public class StyledButtonUI extends MetalButtonUI {
	
	public final static SimpleInsets BORDER_INSETS = new SimpleInsets(new Insets(3, 7, 3, 7));

	private final static StyledButtonUI m_staticButtonUI = new StyledButtonUI();
	
	private final static RollOverListener m_rollOverListener = new RollOverListener();
	
	private final static Rectangle m_viewRect = new Rectangle(BORDER_INSETS.left, BORDER_INSETS.top, 0, 0);
	
	private final static int m_arcWidth = 10;
	
	private final static int m_arcHeight = 10;
	
	private final static RoundRectangle2D.Double m_rect1 = new RoundRectangle2D.Double(0, 0, 0, 0, m_arcWidth, m_arcHeight);
	
	private final static RoundRectangle2D.Double m_rect2 = new RoundRectangle2D.Double(1, 1, 0, 0, m_arcWidth, m_arcHeight);
	
  public static ComponentUI createUI(JComponent c) {
  	return m_staticButtonUI;
  }
  
  protected void paintButton(Graphics g, JComponent c, boolean pressed) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = c.getWidth();
		int height = c.getHeight();
		Color c1 = c.getBackground().brighter();
		Color c2 = c.getBackground().darker().darker();
		m_rect1.setFrame(0, 0, width-2, height-2);
		m_rect2.setFrame(1, 1, width-2, height-2);
		
		Color p1 = Color.WHITE;
		int gray = 230;
		Color p2 = MumieTheme.DEFAULT_THEME.getControlShadow();//new Color(gray, gray, gray);//
		if(m_rollOverListener.isMouseOver(c))
			p2 = MumieTheme.DEFAULT_THEME.getFocusColor();
		Rectangle r = m_rect1.getBounds();
		Paint oldPaint = g2d.getPaint();
		GradientPaint gradientPaint = new GradientPaint(r.width / 2, 0, p1, r.width / 2, r.height, p2);
		g2d.setPaint(gradientPaint);
		g2d.fill(m_rect2);
		g2d.setPaint(oldPaint);
		
		if(pressed) {
			Color tmp = c1;
			c1 = c2;
			c2 = tmp;
		}
		
		g2d.setColor(c1);
		g2d.draw(m_rect2);
		
		g2d.setColor(c2);
		if(!pressed)
		g2d.draw(m_rect1);
		
		g2d.dispose();
  }
  
  private static void ensureNoBorder(JComponent c) {
  	if((c.getBorder() instanceof StyledButtonBorder) == false)
  		c.setBorder(new StyledButtonBorder((AbstractButton) c));
  }
  
  private static void ensureCorrectListener(JComponent c) {
  	if(RollOverListener.hasListener(c) == false)
  		c.addMouseListener(m_rollOverListener);
  }
  
  public void paint(Graphics g, JComponent c) {
  	// components are allways buttons
		AbstractButton b = (AbstractButton) c;
		// determine button state
		ButtonModel model = b.getModel();
		boolean pressed = model.isArmed() && model.isPressed();
		// draw styled border and background
  	paintButton(g, c, pressed);
  	// set opaque property of list cell renderer accordingly to current state
  	if(c instanceof MetalComboBoxButton) {
  		MetalComboBoxButton comboButton = (MetalComboBoxButton) c;
  		JComboBox comboBox = comboButton.getComboBox();
  		ListCellRenderer renderer = comboBox.getRenderer();
      boolean renderPressed = b.getModel().isPressed();
      JComponent cellRenderer = (JComponent) renderer.getListCellRendererComponent(new JList(comboBox.getModel()),
                                                comboBox.getSelectedItem(), -1, renderPressed, false);
      if(comboBox.isPopupVisible())
      	cellRenderer.setOpaque(!cellRenderer.isFocusOwner());// use highlighting of focused list entry
      else
      	cellRenderer.setOpaque(false);// use styled background
  	}
		m_viewRect.setSize(BORDER_INSETS.getInsideDim(b.getSize()));
  	if(pressed)
  		paintButtonPressed(g, b);
  	// draw contents of button
  	super.paint(g, c);
	}
  
  protected void paintButtonPressed(Graphics g, AbstractButton b) { 	
  	// move origine down-right to simulate pull-down effect
		g.translate(1, 1);
  }

  protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
		g.setColor(MumieTheme.DEFAULT_THEME.getFocusColor());
		g.drawRect(viewRect.x - 3, viewRect.y - 2, viewRect.width + 6, viewRect.height + 2);
  }
  
  public void update(Graphics g, JComponent c) {
  	ensureNoBorder(c);
  	ensureCorrectListener(c);
  	if (c.isOpaque()) {
  		g.setColor(c.getBackground());
  		g.fillRect(0, 0, c.getWidth(), c.getHeight());
  	}
  	paint(g, c);
	}
}

class StyledButtonBorder extends EmptyBorder {
	
	StyledButtonBorder(AbstractButton b) {
		super(new SimpleInsets(StyledButtonUI.BORDER_INSETS, b.getMargin()));
	}
}

