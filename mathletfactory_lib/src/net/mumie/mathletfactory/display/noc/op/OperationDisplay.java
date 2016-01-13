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

package net.mumie.mathletfactory.display.noc.op;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.LineBorder;
import javax.swing.plaf.PanelUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.appletskeleton.util.theme.RollOverListener;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.display.noc.MMEditablePanel;
import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.display.noc.op.node.ViewNode;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 *  This class renders the symbolic representation of an expression (e.g. in
 *  an equation or inequality, for a set definition etc.) It acts as a container
 *  for a tree of {@link net.mumie.mathletfactory.display.noc.op.OpViewNode}
 *  nodes, which recursively paint themselves, when
 *  {@link #paintComponent} is called.
 *
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

public class OperationDisplay extends JComponent implements Alignable {

  public static Object repaintLock = new Object();

  /** The value for left aligned expressions.*/
  public final static int ALIGN_LEFT = 0;
  /** The value for right aligned expressions.*/
  public final static int ALIGN_RIGHT = 1;
  /** The value for centered expressions.*/
  public final static int ALIGN_CENTER = 2;

  /** The alignment of the expression.*/
  private int m_align = ALIGN_CENTER;

  /** The node rooting the expression to be rendered.*/
  private ViewNode m_opViewRoot;

  /** Field determining if the tooltip of this component shows the string representation of its master.*/
  private boolean m_showToolTip = true;
  
  /** Used for temporary offline-rendering (e.g. determining the size of the panel) */
  private static final Image OFFLINEBUFFER =
    new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_GRAY);
  private static final Graphics2D OFFLINEGRAPHICS = ((Graphics2D) OFFLINEBUFFER.getGraphics());

	private final static RollOverListener m_rollOverListener = new RollOverListener();
	
  private final static MumieLogger LOGGER = MumieLogger.getLogger(OperationDisplay.class);
  private final static LogCategory DRAW_BASELINE = LOGGER.getCategory("ui.draw-baseline");
  private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline");
  private final static LogCategory PRINT_BASELINE = LOGGER.getCategory("ui.print-baseline");
  
  /**
   * Default constructor - uses {@link net.mumie.mathletfactory.mmobject.MMObjectIF#STD_FONT}
   * for rendering. Should only be used by {@link OperationPanel}.
   */
  public OperationDisplay(){
    this(DisplayProperties.STD_FONT);
  }

  /**
   * @param font The font used for rendering the operation. Should only be used by
   * {@link OperationPanel}.
   */
  public OperationDisplay(Font font) {
  	super();
  	setLayout(null);
    setFont(font);
    if(LOGGER.isActiveCategory(DRAW_OUTLINE))
    	setBorder(new LineBorder(Color.RED));
    addMouseListener(m_rollOverListener);
  }

  /**
   *  This constructor is for standalone use of operation display.
   *  Creates a operation display for the given expression string. <code>expr</code> must be
   *  parseable by {@link net.mumie.mathletfactory.algebra.op.OpParser} in order to create
   *  the operation that is displayed by this component.
   */
  public OperationDisplay(String expr){
    this(DisplayProperties.STD_FONT);
    setOpViewRoot(OpViewMapper.getViewFor(new Operation(MMDouble.class, expr, false).getOpRoot()));
  }

  /** Sets the view node rooting the expression to be rendered.*/
  public void setOpViewRoot(ViewNode root) {
    m_opViewRoot = root;
  }

  /** Returns the view node rooting the expression to be rendered.*/
  public ViewNode getOpViewRoot(){
    return m_opViewRoot;
  }

  /**
   *  Determines the position and size of this panel by rendering the string on
   *  an offline image buffer.
   */
  public void render() {
    OFFLINEGRAPHICS.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_OFF);
    OFFLINEGRAPHICS.setFont(getFont());
    paintComponent(OFFLINEGRAPHICS);
    if(m_showToolTip)
      setToolTipText(m_opViewRoot.toString());
    else
      setToolTipText(null);
    //System.out.println("baseline "+getBaseline()+", bounds "+m_opViewRoot.getMetrics().getAscentBounds());
  }

  /**
   *  Renders the expression on the component using the specified
   *  {@link #getFont font} and {@link #getAlign alignment}. It also
   *  informs the registered observers of changes to {@link #getBaseline baseline}.
   */
  protected void paintComponent(Graphics g) {
    Insets insets = getInsets();
    double baseline = getBaseline() + insets.top;
  	Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    int innerWidth = getWidth()-insets.left-insets.right;
  	if(isOpaque()) {
			Color oldColor = g.getColor();
			if(isEditable()) {
				if(m_rollOverListener.isMouseOver(this))
					g2.setColor(new Color(230, 240, 245));
				else
					g2.setColor(new Color(250,250,250));
			} else {
				g2.setColor(getBackground());
			}
			g2.fillRect(insets.left, insets.top, innerWidth, getHeight()-insets.top-insets.bottom);
			g2.setColor(oldColor);
  	}
    g2.setColor(getForeground());
    int xPos = 0;
    Rectangle2D opViewRootBounds = m_opViewRoot.getBounds();
    if (opViewRootBounds != null)
      switch (m_align) {
        case ALIGN_LEFT :
          xPos = insets.left;
          break;
        case ALIGN_RIGHT :
          xPos = (int) (innerWidth - opViewRootBounds.getWidth()) + insets.right;
          break;
        case ALIGN_CENTER :
          xPos = (int) (innerWidth - opViewRootBounds.getWidth()) / 2 + insets.left;
          break;
      }

    LOGGER.log(PRINT_BASELINE, hashCode() + "-baseline is +"+baseline);
    synchronized(repaintLock){
//      System.out.println(">>starting repaint: "+(++repaint));
//    	if(isShowing()) {
//      	System.out.println("display-baseline: " + getBaseline());
//      	System.out.println("display-offset: " + getOffset());
//      	System.out.println("Draw display at: " + (getOffset() + getBaseline()));
//    	}
      m_opViewRoot.draw(g2, xPos, baseline, OpNode.PRINT_ALL);
//      System.out.println("<< done:            "+repaint--);
    }
    if(LOGGER.isActiveCategory(DRAW_BASELINE)) {
    	// drawing debug baseline
    	g2.drawLine(xPos,(int) baseline, innerWidth, (int) baseline);
    }
		g2.dispose();
//    m_opViewRoot.getMetrics().draw(g2);
//    System.out.println("drawn "+toString());
//    System.out.println("metrics are: "+m_opViewRoot.getMetrics().toString());
  }
//private int repaint = 0;
  
  private boolean isEditable() {
    if(getParent() != null && getParent().getParent() instanceof MMEditablePanel)
    	return ((MMEditablePanel) getParent().getParent()).isEditable();
    else
    	return false;
  }
  
  /**
   * Returns the baseline of the rendered expression.
   * @see net.mumie.mathletfactory.display.noc.util.Alignable#getBaseline()
   */
  public double getAscent() {
    return m_opViewRoot.getMetrics().getAscentBounds().getHeight();
  }

  public double getDescent() {
    return m_opViewRoot.getMetrics().getDescentBounds().getHeight();
  }
  
  public double getBaseline() {
  	Insets insets = getInsets();
  	int innerHeight = getHeight() - insets.top - insets.bottom;
  	double margin = (innerHeight - m_opViewRoot.getMetrics().getHeight()) / 2;
  	return margin + getAscent();
  }
  	
  /**
   *  The preferred width is the overall width of the rendered expression
   */
  public Dimension getPreferredSize() {
    if (getToolTipText() == null || m_opViewRoot == null || m_opViewRoot.getBounds() == null)
      render();
    Rectangle2D opViewRootBounds = m_opViewRoot.getBounds();
    int width = (int) opViewRootBounds.getWidth();
    int height =  (int) opViewRootBounds.getHeight();
//	  System.out.println("height of "+m_opViewRoot+" is "+height+" (ascent="+(int)m_opViewRoot.getMetrics().getAscentBounds().getHeight()+", descent="+(int)m_opViewRoot.getMetrics().getDescentBounds().getHeight()+", baseline="+(int)m_opViewRoot.getMetrics().getBaseline());
    Insets insets = getInsets();
    width += insets.left + insets.right;
    height += insets.top + insets.bottom;
		Dimension dim = new Dimension(width, height);
    return dim;
  }

  /** Returns the alignment of the rendered expression.*/
  public int getAlignment() {
    return m_align;
  }

  /**
   *  Sets the alignment of the rendered expression. It may be either
   *  {@link #ALIGN_LEFT}, {@link #ALIGN_RIGHT} or {@link #ALIGN_CENTER}.
   */
  public void setAlignment(int align) {
    m_align = align;
    render();
    repaint();
  }

  /**
   *  Sets the font used for the rendering of the expression.
   */
  public void setFont(Font font) {
    super.setFont(font);
    //System.out.println("setting font to "+font+(m_opViewRoot != null ? " for display of "+m_opViewRoot : ""));
    if (m_opViewRoot != null)
      render();
    repaint();
  }

  /** Returns the size of the font used within this component. */
  public int getFontSize() {
    return getFont().getSize();
  }

  /** Sets the size of the font used within this component. */
  public void setFontSize(int size) {
    setFont(getFont().deriveFont(size));
  }

  /**
   * For debugging purposes, outputs the string representation of the master
   * operation and the dimensions.
   */
  public String toString(){
    return (m_opViewRoot != null ? m_opViewRoot.toString() : "") + ", " + getSize() + " baseline: "+getBaseline();
  }

  /**
   * Sets the given display format for decimal (i.e. MDouble) numbers.
   * Otherwise the format using {@link net.mumie.mathletfactory.display.noc.op.node.NumberView#DEFAULT_PATTERN}
   * is used.
   */
  public void setDecimalFormat(DecimalFormat format){
    if(m_opViewRoot != null)
      m_opViewRoot.setDecimalFormat(format);
  }

  /**
   * Returns the display format for decimal (i.e. MDouble) numbers.
   */
  public DecimalFormat getDecimalFormat(){
    if(m_opViewRoot != null)
      return m_opViewRoot.getDecimalFormat();
    return null;
  }

  /** Usage example for standalone operation displays. */
  public static void main(String[] args){
    OperationDisplay od = new OperationDisplay("x^-1");
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(od.getPreferredSize());
    f.getContentPane().add(od);
    f.setVisible(true);
  }

  /**
   * Additionally sets the theme property "OperationPanel.font" or "MMNumberPanel.font" (if instance of MMNumberPanel).
   */
  public void setUI(PanelUI ui) {
    super.setUI(ui);
    if(getParent() != null)
      if(getParent().getParent() instanceof MMNumberPanel)
        setFont((Font)MumieTheme.getThemeProperty("MMNumberPanel.font"));
      else
        setFont((Font)MumieTheme.getThemeProperty("OperationPanel.font"));
  }

  /**
   * Sets if a tool tip is visible containing the current string representation
   * of its master.
   */
  public void setToolTipVisible(boolean visible) {
    m_showToolTip = visible;
  }

  /**
   * Returns if a tool tip containing the current string representation
   * of its master is visible.
   */
  public boolean isToolTipVisible() {
    return m_showToolTip;
  }
}
