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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.layout.AlignerLayout;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 * This class is a container for GUI components allowing to arrange the
 * components in a "text editor" like manner. So it is possible to insert
 * line breaks, tabstops, blank lines and alignment information for each line.
 * 
 * <p>
 * Text can easily be added via one of the "addText" methods where the
 * text style and color can be set either by using direct font/color information
 * or by using html-formatting code (e.g. '&lt;font color=...&gt;' or '&lt;b&gt;').
 * @see #addText(String)
 * @see #addText(String, Color)
 * @see #addText(String, Font)
 * </p>
 *
 * <p>
 * Editing begins (like in editors) in the first line where editing means adding of
 * components to the current line. Inserting a single line break causes the "cursor"
 * to jump to the next line, i.e. editing of the previous line is done.
 * Inserting more than one line break will cause empty space between the
 * last and the next line (where the "cursor" will jump to).
 * @see #insertLineBreak()
 * @see #insertLineBreaks(int)
 * @see #getCurrentLine()
 * </p>
 * 
 * <p>
 * Every "line" has an own alignment: either centered (the default), left or right.
 * Line alignments can be changed for the current line, where all following lines
 * also take this alignment, if not set explicitly.
 * @see #setCenterAlignment()
 * @see #setLeftAlignment()
 * @see #setRightAlignment()
 * </p>
 *
 * <p>
 * Empty horizontal space can be added either with tabstops or by indicating an explicit width.
 * Empty vertical space can be added by indicating an explicit width.
 * @see #insertTab()
 * @see #insertTabs(int)
 * @see #insertHSpace(int)
 * @see #insertVSpace(int)
 * </p>
 * 
 * <p>
 * Per default control panels are not scrollable and have allways the appropriate size
 * to display correctly their content. Due to layout restrictions scrollable components should
 * have a defined size if they share their place with other components. This can be achieved by
 * defining a constant value either for the height (if the alignment is along the y-axis) 
 * or for the width (if the alignment is along the x-axis) or both.
 * @see #setScrollable(boolean)
 * @see #setHeight(int)
 * @see #setWidth(int)
 * </p>
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public class ControlPanel extends JPanel {

	/** Contstant for a centered line alignment. */
	public final static int CENTER_ALIGNMENT = AlignerLayout.CENTER;

	/** Contstant for a left line alignment. */
	public final static int LEFT_ALIGNMENT = AlignerLayout.LEFT;

	/** Contstant for a right line alignment. */
	public final static int RIGHT_ALIGNMENT = AlignerLayout.RIGHT;

	/** Index pointing to the actual line (an instance of LinePanel). */
	private int m_currentLineIndex = 0;

	/** Field containing the current line alignment. */
	private int m_alignment = CENTER_ALIGNMENT;

	/** Number of pixels taken by a Tabstop. */
	public final static int TAB_SIZE = 30;

	/** Number of pixels taken by an empty line. */
	public final static int LINE_HEIGHT = 15;

	/** Number of pixels between 2 lines. */
	public final static int LINE_DISTANCE = 5;
	
	/** Number of pixels for the margin. */
	public final static int INSETS = 5;
	
	/** Constant for margin border using {@link #INSETS} as insets. */
	private Border m_margin = new EmptyBorder(INSETS, INSETS, INSETS, INSETS);

	/** The preferred height of this ControlPanel. */
	private int m_preferredHeight = -1;
	
	/** The preferred width of this ControlPanel. */
	private int m_preferredWidth = -1;

	/** Field containing the scroller. May be null if not scrollable. */
  private Scroller m_scroller;

  /** Field containg the content pane containing all line panels. */
  private ContentPanel m_contentPane = new ContentPanel();

  /** Flag indicating if scrollbars should be used when necessary. */
  private boolean m_isScrollable = false;
  
  private final static MumieLogger LOGGER = MumieLogger.getLogger(ControlPanel.class);
  private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline");
  
  /**
   * Constructs a new empty control panel instance.
   * Adding components starts at the first line.
   */
	public ControlPanel() {
		super(new BorderLayout());
    // set margin around this panel
    super.setBorder(m_margin);
    // create first empty line
    LinePanel firstLine = new LinePanel();
		// add first line to content pane
		m_contentPane.add(firstLine);
		// add content pane to "real" control panel
    super.addImpl(m_contentPane, BorderLayout.CENTER, -1);
    // draw debug outline border
    if(LOGGER.isActiveCategory(DRAW_OUTLINE))
    	setBorder(new LineBorder(Color.BLACK));
	}
	
	/**
	 * Returns the content pane containing all line panels.
	 */
  protected ContentPanel getContentPane() {
    return m_contentPane;
  }
  
  /**
   * Returns the current line's container.
   */
  public Container getCurrentLine() {
  	return getLine(getCurrentLineIndex());
  }
  
  /** Convenience method for {@link #getCurrentLine()}</code>. */
  protected LinePanel getCurrentLinePanel() {
  	return (LinePanel) getCurrentLine();
  }
  
  /**
   * Returns the the current line's index (starting from zero).
   */
  public int getCurrentLineIndex() {
  	return m_currentLineIndex * 2;
  }
  
  /**
   * Returns the n-th line's container.
   */
  public Container getLine(int n) {
  	return (Container) getContentPane().getComponent(n);
  }
  
  /**
   * Returns the number of lines.
   */
  public int getLineCount() {
  	return getContentPane().getComponentCount();
  }

  /*
   * Overriden to add component to current line.
   */
  protected void addImpl(Component comp, Object constraints, int index) {
  	getCurrentLinePanel().addImpl(comp, constraints, index);
  }
  
  /**
   * Adds and returns a text panel with the given text and a vector border.
   */
	public TextPanel addVector(String vectorName) {
		TextPanel tp = new TextPanel(vectorName);
		tp.setBorder(new VectorBorder());
		add(tp);
		return tp;
	}
  
  /**
   * Adds and returns a text panel with the given text and a vector border with the given y-shift.
   */
	public TextPanel addVector(String vectorName, int yShift) {
		TextPanel tp = new TextPanel(vectorName);
		tp.setBorder(new VectorBorder(yShift));
		add(tp);
		return tp;
	}
  
	/**
	 * Add a text panel to the actual Line in the controlPanel. HTML-code can be
	 * used to change text style information.
	 */
	public void addText(String text) {
	  addText(text, null, null);
	}

	/**
	 * Add a text panel to the actual Line in the controlPanel with the given font.
	 */
	public void addText(String text, Font aFont) {
		addText(text, aFont, null);
	}

	/**
	 * Add a text panel to the actual Line in the controlPanel with the given foreground color.
	 */
	public void addText(String text, Color color) {
		addText(text, null, color);
	}

	/**
	 * Add a text panel to the actual Line in the controlPanel with the given font and foreground color.
	 */
	public void addText(String labelText, Font aFont, Color color) {
		TextPanel tp = new TextPanel(labelText);
		if(color != null)
		  tp.setForeground(color);
		if(aFont != null)
		  tp.setFont(aFont);
		add(tp);
	}

	/** Insert a line break, i.e. jump to the next line. */
	public void insertLineBreak() {
    getContentPane().add(new LinePanel());
		m_currentLineIndex++;
	}

	/**
	 * Insert <code>nrOfLines</code> line breaks, i.e. jump <code>nrOfLines</code>
	 * lines downwards. Empty lines takes a serveral amount of height.
	 * @see #LINE_HEIGHT
	 */
	public void insertLineBreaks(int nrOfLines) {
		if (nrOfLines <= 0)
			throw new IllegalArgumentException("Number of line breaks cannot be negative, nor zero!");
		for(int i = 0; i < nrOfLines; i++)
			insertLineBreak();
	}

	/** Insert a tab, i.e. move a serveral amount of pixels to the right. */
	public void insertTab() {
		this.insertTabs(1);
	}

	/** Insert <code>nrOfTabs</code> tabs, i.e. move a serveral amount of pixels to the right. */
	public void insertTabs(int nrOfTabs) {
		add(new ControlPanel.TabspacePanel(nrOfTabs));
	}

	/** Insert empty space of <code>nrOfPix</code> pixels to the right. */
	public void insertHSpace(int nrOfPix) {
		add(Box.createRigidArea(new Dimension(nrOfPix, 0)));
	}

	/** Insert empty space of <code>nrOfPix</code> pixels downwards. */
	public void insertVSpace(int nrOfPix) {
		if (nrOfPix <= 0)
			throw new IllegalArgumentException("Number of pixels cannot be negative, nor zero!");

		getCurrentLine().add(Box.createRigidArea(new Dimension(0, nrOfPix)));
    getContentPane().add(new LinePanel());
		m_currentLineIndex++;
	}

	/**
	 * Sets the horizontal alignment of the current and all following lines.
	 * @see #CENTER_ALIGNMENT
	 * @see #LEFT_ALIGNMENT
	 * @see #RIGHT_ALIGNMENT
	 */
	public void setHorizontalAlignment(int alignment) {
		m_alignment = alignment;
		getCurrentLinePanel().getAlignerLayout().setHorizontalAlignment(alignment);
	}

	/**
	 * Removes the component, specified by <code>index</code>, from the current line.
	 */
	public void remove(int index) {
    getCurrentLine().remove(index);
	}

	public void removeAll() {
    getContentPane().removeAll();

		LinePanel firstLine = new LinePanel();
    getContentPane().add(firstLine);
		m_currentLineIndex = 0;
	}

	/** Convenience method for <code>setLineAlignment(ControlPanel.LEFT_ALIGNMENT)</code>. */
	public void setLeftAlignment() {
		setHorizontalAlignment(LEFT_ALIGNMENT);
	}

	/** Convenience method for <code>setLineAlignment(ControlPanel.RIGHT_ALIGNMENT)</code>. */
	public void setRightAlignment() {
		setHorizontalAlignment(RIGHT_ALIGNMENT);
	}

	/** Convenience method for <code>setLineAlignment(ControlPanel.CENTER_ALIGNMENT)</code>. */
	public void setCenterAlignment() {
		setHorizontalAlignment(CENTER_ALIGNMENT);
	}
	
	/**
	 * Adds the image located under the specified path as an image icon.
	 */
	public Image addImage(String imageLocation) {
		URL imageURL = getClass().getResource(imageLocation);
		if(imageURL == null)
			throw new IllegalArgumentException("No image can be found under this location: " + imageLocation);
		ImageIcon icon = new ImageIcon(imageURL);
		add(new JLabel(icon));
		return icon.getImage();
	}

	/* Overriden to return preferred size. */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
    if(m_preferredWidth != -1)
    	dim.width = m_preferredWidth;
    if(m_preferredHeight != -1)
    	dim.height = m_preferredHeight;
		return dim;
  }

	/**
	 * Sets the preferred height of this ControlPanel.
	 * Setting <code>-1</code> will restore the default height.
	 */
	public void setHeight(int height) {
		m_preferredHeight = height;
    validate();
	}

	/**
	 * Sets the preferred width of this ControlPanel.
	 * Setting <code>-1</code> will restore the default width.
	 */
	public void setWidth(int width) {
		m_preferredWidth = width;
    validate();
	}

  /**
   * Sets the preferred size of this ControlPanel. Setting <code>-1</code> 
   * for either the width or the height (or both) will restore the default values.
   */
  public void setPreferredSize(Dimension d) {
    m_preferredWidth = d.width;
    m_preferredHeight = d.height;
    validate();
  }
  
  /**
   * Returns whether this ControlPanel is scrollable.
   */
  public boolean isScrollable() {
  	return m_isScrollable;
  }
  
  /**
   * Sets if this ControlPanel should be scrollable (if necessary).
   * A scollable ControlPanel only should be used in conjunction with a fixed width/height
   * either by an appropriate layout manager in its parent container or by setting explicitly
   * the width or height or both.
   * @see #setWidth(int)
   * @see #setHeight(int)
   */
  public void setScrollable(boolean scrollable) {
  	if(m_isScrollable == scrollable)
  		return;
  	m_isScrollable = scrollable;
  	if(isScrollable() && m_scroller == null) {
  		// remove content pane from this
	    super.remove(0);
	    // create scroller with content pane
	    m_scroller = new Scroller(getContentPane());
	    // set background color
	  	if(m_scroller.getViewport() != null)
	  		m_scroller.getViewport().setBackground(getBackground());
	  	// add scroller to this
	    super.addImpl(m_scroller, BorderLayout.CENTER, -1);
  	} else if(!isScrollable() && m_scroller != null) {
  		// remove scroller from this
	    super.remove(0);
			m_scroller = null;
			// add content pane to this
	    super.addImpl(getContentPane(), BorderLayout.CENTER, -1);
  	}
  }
  
  /**
   * Scrolls the view of the scrollpane's viewport to the upper-left corner.
   * A reset of the viewport's view will only be done if this ControlPanel is scrollable.
   */
  public void resetScrollingView() {
  	if(m_scroller != null)
  		getContentPane().scrollRectToVisible(new Rectangle(0, 0, 1, 1));
  }
  
  /* Overridden to add margin. */
  public void setBorder(Border border) {
    if(border != null)
      super.setBorder(new CompoundBorder(border, m_margin));
    else
      super.setBorder(m_margin);
  }
  
  /**
   * Sets the margins for this control panel to the given value.
   * A value of <code>-1</code> will restore the default margin.
   * This is a convenience method for {@link #setMargin(int, int, int, int)}.
   * 
   * @param margin margins for top, left, bottom and right
   */
  public void setMargin(int margin) {
    setMargin(margin, margin, margin, margin);
  }
  
  /**
   * Sets the margins for this control panel to the given values.
   * A value of <code>-1</code> will restore the default margin.
   * 
   * @param top top margin
   * @param left left margin
   * @param bottom bottom margin
   * @param right right margin
   */
  public void setMargin(int top, int left, int bottom, int right) {
    if(top == -1) top = INSETS;
    if(left == -1) left = INSETS;
    if(bottom == -1) bottom = INSETS;
    if(right == -1) right = INSETS;
    m_margin = new EmptyBorder(top, left, bottom, right);
    Border oldBorder = super.getBorder();
    if(oldBorder != null && oldBorder instanceof CompoundBorder)
      setBorder(((CompoundBorder) oldBorder).getOutsideBorder());
    else
      setBorder(null);
  }
  
  public void setBackground(Color bgColor) {
  	super.setBackground(bgColor);
  	if(m_scroller != null && m_scroller.getViewport() != null)
  		m_scroller.getViewport().setBackground(bgColor);
  }
  
  /** Helper class for content panel containing all line panels. */
  private class ContentPanel extends JComponent {
  	ContentPanel() {
  		super();
  		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
      setLayout(boxLayout);
  	}
  	
  	protected void addImpl(Component comp, Object constraints, int index) {
  		if(super.getComponentCount() > 0)
    		super.addImpl(Box.createVerticalStrut(LINE_DISTANCE), null, -1);
  		super.addImpl(comp, constraints, index);
  	}
  }

	/** 
	 * Helper class for a line (an alignable panel).
	 */
	private class LinePanel extends AlignablePanel {

		private LinePanel() {
			super();
			setOpaque(false);
			getAlignerLayout().setHorizontalAlignment(m_alignment);
	    // draw debug outline border
	    if(LOGGER.isActiveCategory(DRAW_OUTLINE))
	    	setBorder(new LineBorder(Color.BLUE));
		}
		
		/* Overridden to be made visible. */
		public void addImpl(Component comp, Object constraints, int index) {
			super.addImpl(comp, constraints, index);
	  }

		/* Overridden to return min line height. */
		public Dimension getPreferredSize() {
			Dimension dim = super.getPreferredSize();
			dim.height = Math.max(dim.height, LINE_HEIGHT);
			return dim;
		}
		
		/**
		 * Returns the aligner layout.
		 */
		public AlignerLayout getAlignerLayout() {
			return (AlignerLayout) getLayout();
		}
	}

	/**
	 * Helper class for a panel with dynamic width.
	 */
	private class TabspacePanel extends JPanel {

		private int m_nrOfTabs;

		protected TabspacePanel(int nrOfTabs) {
			m_nrOfTabs = nrOfTabs;
		}

		/* Overridden to return dynamic tabstop width. */
    public Dimension getPreferredSize() {
      int newWidth = m_nrOfTabs * TAB_SIZE;// - getX() % TAB_SIZE;
      return new Dimension(newWidth, 0);
    }
	}
	
	/**
	 * Helper class for content scroller.
	 * It is only used if this ControlPanel is scollable. 
	 */
	private class Scroller extends JScrollPane {
		Scroller(Component view) {
			super();
			// use aligner container for view for correct alignment if view is smaller than viewport
			setViewportView(new ScrollableView(view));
			setBorder(new EmptyBorder(0,0,0,0));
		}
	}
  
	// NOTE: implementing the Scrollable interface will cause layout problems,
	// own implementation of scroller layout necessary (RT #746)
  /**
   * Helper class for content scroller.
   * It is only used if this ControlPanel is scollable. 
   */
  private class ScrollableView extends JPanel {// implements Scrollable {
    
    private int m_maxUnitIncrement = 10;
    
    ScrollableView(Component view) {
      super(new BorderLayout());
      setOpaque(false);
      add(view, BorderLayout.CENTER);
    }
    
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      if (orientation == SwingConstants.HORIZONTAL)
        return visibleRect.width - m_maxUnitIncrement;
      else
        return visibleRect.height - m_maxUnitIncrement;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return m_maxUnitIncrement;
    }
    
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }

    public boolean getScrollableTracksViewportWidth() {
      return false;
    }
  }
}
