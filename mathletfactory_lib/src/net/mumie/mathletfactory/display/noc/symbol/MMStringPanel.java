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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;

import net.mumie.mathletfactory.appletskeleton.util.theme.RollOverListener;
import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.display.noc.MMEditablePanel;
import net.mumie.mathletfactory.math.util.MString;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.util.MMString;
import net.mumie.mathletfactory.transformer.noc.StringTransformer;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 * @author gronau
 * @mm.docstatus outstanding
 */
public class MMStringPanel extends MMEditablePanel {

  private ContentViewRenderer m_renderer;

  public MMStringPanel(MMObjectIF master, StringTransformer transformer) {
    super(master, transformer, new ContentViewRenderer());
    m_renderer = (ContentViewRenderer) getValueViewer();

    setEditable(master.isEditable());
    setEdited(((MMString)master).isEdited());
    setValue(((MMString)master).getValue());
    repaintAll();
  }
  
	protected boolean checkContent(String content) {
		return true;
	}
	
	protected String getEditingContent() {
		return ((MMString) getMaster()).getValue();
	}

	protected void applyContent(String content) {
    String newValue = content;
    String oldValue = isEdited() ? ((MString) getMaster()).getValue() : null;
    log("old value: " + oldValue + ", fire new value: " + newValue);
    firePropertyChange(
      PropertyHandlerIF.STRING,
      oldValue,
      newValue);
    setValue(newValue);
	}
	
	public void setValue(String string) {
		m_renderer.setText(string);
		m_renderer.revalidate();
		repaintAll();
  }
	
	public double getBaseline() {
		if(isViewingMode() && isEdited())
			return m_renderer.getBaseline();
		else
			return super.getBaseline();
	}
	
	private void log(String message) {
//		System.out.println("MMStringPanel: " + message);
	}
}
	class ContentViewRenderer extends JPanel implements Alignable {
		
		private final static RollOverListener m_rollOverListener = new RollOverListener();
		
	  private final static MumieLogger LOGGER = MumieLogger.getLogger(ContentViewRenderer.class);
	  private final static LogCategory DRAW_BASELINE = LOGGER.getCategory("ui.draw-baseline");
	  
	  private ContentViewIF m_view;
	  private String m_value;

	  /** Field holding the old baseline value. Used for comparing with the new value. */
	  private double m_baseline;
	  
	  public ContentViewRenderer() {
	  	super();
	  	addMouseListener(m_rollOverListener);
	  }

	  public Dimension getPreferredSize() {
	  	Dimension dim = new Dimension(0, 0);
			if(m_view == null)
				return dim;
			dim.setSize(m_view.getPreferredSize());
			Insets insets = getInsets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			return dim;
		}
		
		public double getBaseline() {
			double baseline = getBaselineImpl();
			baseline = getHeight() / 2 + baseline / 2;
			if(baselineHasChanged(baseline))
				revalidate();
			m_baseline = baseline;
			return baseline;
		}
		
		private double getBaselineImpl() {
			Rectangle rect = new Rectangle(m_view.getPreferredSize());
			return m_view.getBaseline(rect);
		}
		
		private boolean baselineHasChanged(double newBaseline) {
			return m_baseline == 0 || m_baseline != newBaseline;
		}
		
		public void paintComponent(Graphics g) {
			// ensure correct baseline placement by invoking new painting process if necessary
	  	if(baselineHasChanged(getBaselineImpl()))
	  		revalidate();
			super.paintComponent(g);
			if(isOpaque()) {
				if(isEditable()) {
					if(m_rollOverListener.isMouseOver(this))
						g.setColor(new Color(230, 240, 245));
					else
						g.setColor(new Color(250,250,250));
				} else {
					g.setColor(getBackground());
				}
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			g.setColor(getForeground());
			if(m_view == null)
				return;
			Rectangle rect = new Rectangle();
			rect.setSize(m_view.getPreferredSize());
			m_view.paintComponent(g, rect);
      if(LOGGER.isActiveCategory(DRAW_BASELINE)) {
      	// draw debug baseline
				int baseline = (int) getBaseline();
				g.drawLine(0, baseline, getWidth(), baseline);
      }
		}

	  private boolean isEditable() {
	    if(getParent() != null && getParent().getParent() instanceof MMEditablePanel)
	    	return ((MMEditablePanel) getParent().getParent()).isEditable();
	    else
	    	return false;
	  }
	  
		public void setText(String text) {
			if(BasicHTML.isHTMLString(text))
				m_view = new HTMLContentView(text, this);
			else
				m_view = new BasicContentView();
			m_value = text;
		}
		
		public String getText() {
			if(m_value != null)
				return m_value;
			return new String();
		}
	
	
	class HTMLContentView implements ContentViewIF {

		private View m_view;
		
		HTMLContentView(String text, JComponent c) {
			m_view = BasicHTML.createHTMLView(c, text);
		}
		
		public Dimension getPreferredSize() {
			return new Dimension((int) m_view.getPreferredSpan(View.X_AXIS), (int) m_view.getPreferredSpan(View.Y_AXIS));
		}
		
		public void paintComponent(Graphics g, Rectangle rect) {
      ((Graphics2D) g).setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
			m_view.setSize(rect.width, rect.height);
			rect.x = getWidth() / 2 - rect.width / 2;
			rect.y = (int) (ContentViewRenderer.this.getBaseline() - getBaseline(rect));
			m_view.paint(g, rect);
		}
		
		public double getBaseline(Rectangle rect) {
			return getBaseline(m_view.getView(0), rect);
		}
		
	   private int getBaseline(View view, Shape bounds) {
	     if (view.getViewCount() == 0) {
	         return -1;
	     }
	     AttributeSet attributes = view.getElement().getAttributes();
	     Object name = null;
	     if (attributes != null) {
	         name = attributes.getAttribute(StyleConstants.NameAttribute);
	     }
	     int index = 0;
	     if (name == HTML.Tag.HTML && view.getViewCount() > 1) {
	         // For html on widgets the header is not visible, skip it.
	         index++;
	     }
	     bounds = view.getChildAllocation(index, bounds);
	     if (bounds == null) {
	         return -1;
	     }
	     View child = view.getView(index);
	     if (view instanceof javax.swing.text.ParagraphView) {
	         Rectangle rect;
	         if (bounds instanceof Rectangle) {
	             rect = (Rectangle)bounds;
	         }
	         else {
	             rect = bounds.getBounds();
	         }
	         return rect.y + (int)(rect.height *
	                               child.getAlignment(View.Y_AXIS));
	     }
	     return getBaseline(child, bounds);
	   }
	}
	
	class BasicContentView implements ContentViewIF {
		public void paintComponent(Graphics g, Rectangle rect) {
      ((Graphics2D) g).setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setColor(getForeground());
			int x = getWidth() /2 - getPreferredSize().width / 2;
			int y = (int) ContentViewRenderer.this.getBaseline();
			g.drawString(getText(), x, y);
		}
		
		public Dimension getPreferredSize() {
			return getFontMetrics(getFont()).getStringBounds(getText(), getGraphics()).getBounds().getSize();
		}
		
		public double getBaseline(Rectangle rect) {
			if(getGraphics() == null)
				return 0;
			return getFont().getLineMetrics(getText(), ((Graphics2D) getGraphics()).getFontRenderContext()).getAscent();
		}
	}
	
	interface ContentViewIF {
		
		void paintComponent(Graphics g, Rectangle rect);
		Dimension getPreferredSize();
//		void setContent(int contentType, Object value);
		double getBaseline(Rectangle rect);
	}
	}
