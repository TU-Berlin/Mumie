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

package net.mumie.mathletfactory.display.noc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.mumie.mathletfactory.action.SelectionListener;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.Drawable;
import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.layout.SimpleInsets;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.CombinedPropertyMap;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

/**
 *  This class is the base for all MM-Panels which are the container drawables
 *  for MM-Objects. It is used by {@link net.mumie.mathletfactory.display.noc.MMEditablePanel}
 *  and {@link net.mumie.mathletfactory.display.noc.MMCompoundPanel}. Refer to these classes for
 *  more information about the different types of MM-Panels.<br>
 *  
 *  A MM-Panel is updated by its master {@link net.mumie.mathletfactory.mmobject.MMObjectIF MMObject}
 *  via an instance of {@link net.mumie.mathletfactory.transformer.ContainerObjectTransformer}.
 *  The master is informed about changes in its container drawable via {@link java.beans.PropertyChangeEvent property change events}.
 *  
 *  Every MM-Panel has the ability to align itself to another component's baseline. 
 *  Subclasses have only to implement the {@link net.mumie.mathletfactory.display.noc.util.Alignable#getDefaultBaseline()}
 *  method for defining a custom default baseline (i.e. when no alignment is performed).
 *  The most comfortable solution for this is to define a custom "viewer aligner" which can be in the best 
 *  case the viewer component itself. The viewer aligner can be set via {@link #setViewerAligner(Alignable)}.
 *  
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */
public abstract class MMPanel extends AlignablePanel implements Drawable, Alignable {

  /** The font used by this component. */
  protected Font m_font;

  /** The master object of this drawable. */
	private MMObjectIF m_mmMaster;
	
  /** The preferred height of this MMPanel. */
  protected int m_preferredHeight = -1;
  
  /** The preferred width of this MMPanel. */
  protected int m_preferredWidth = -1;

  /** Flag for being edited. */
	private boolean m_isEdited = true;

  /** Flag for text visibility. */
	private boolean m_isTextVisible = true;
	
	/** The rendering hints for this MM-Panel. */
	private PanelRenderingHints m_panelRenderingHints;

	private Vector m_selectionListeners = new Vector(0);

	private JComponent m_viewerComp;
	private TextPanel m_labelRenderer;

	private boolean m_isSelectable = false;
	private boolean m_isSelected = false;
	private boolean m_isMouseOver = false;

  private Border m_mouseOverBorder = BorderFactory.createRaisedBevelBorder();
  private Border m_selectionBorder = BorderFactory.createLoweredBevelBorder();
  private Border m_emptyBorder = new EmptyBorder(m_mouseOverBorder.getBorderInsets(null));

  private boolean m_isHighlighted = false;
  protected Color m_oldEditForeground;

	/**	Field describing if this MMPanel is included in another MMPanel (e.g. MMNumberMatrixPanel) or not.  */
	private MMCompoundPanel m_rootPanel;

  /** Map holding all (display) properties for this drawable. */
	protected CombinedPropertyMap m_properties = new CombinedPropertyMap();

  private final static MumieLogger LOGGER = MumieLogger.getLogger(MMPanel.class);
  private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline");
  
  /** Constructs the drawable by setting its master.*/
	public MMPanel(MMObjectIF masterObject, ContainerObjectTransformer transformer) {
		this(masterObject, transformer, new JPanel());
	}
	
  /** Constructs the drawable by setting its master.*/
	public MMPanel(MMObjectIF masterObject, ContainerObjectTransformer transformer, JComponent viewerComp) {
		super(0);
		m_mmMaster = masterObject;
		// ensure that this constructor is only called by a transformer
		if(transformer == null)
      throw new IllegalStateException("This constructor must only be called by a transformer!");
		// create panel rendering hints
		m_panelRenderingHints = new PanelRenderingHints(getDrawableProperties());
		// init viewer
		setViewerComponent(viewerComp);
		setFocusable(false);
		// init label
		m_labelRenderer = new TextPanel();
		// add label as first component
		super.addImpl(m_labelRenderer, null, 0);
		// define debug outline border
		if(LOGGER.isActiveCategory(DRAW_OUTLINE))
			setBorder(new LineBorder(Color.GREEN));
	}
	
	/**
	 * Returns the panel rendering hints for this MM-Panel containing display
	 * properties for this drawable.
	 */
	public PanelRenderingHints getPanelRenderingHints() {
		return m_panelRenderingHints;
	}
	
	/**
	 * Sets the viewer component and allows to change it after startup.
	 */
	protected void setViewerComponent(JComponent c) {
		if(c == null)
			c = new JPanel();
		m_viewerComp = c;
		super.addImpl(m_viewerComp, null, -1);
		repaintAll();
	}
		
	protected void paintBorder(Graphics g) {
		// first paint user border
		super.paintBorder(g);
		// prepare coordinates for state border
		int x = 0, y = 0, width = getWidth(), height = getHeight();
		if(super.getBorder() != null) {
			Insets insets = super.getBorder().getBorderInsets(this);
			x = insets.left;
			y = insets.top;
			width = getWidth()-insets.left-insets.right;
			height = getHeight()-insets.top-insets.bottom;
		}
		// then draw state border
		paintModusBorder(g, x, y, width, height);
	}

	/**
	* Paints the active border according to the current mode.
	* 
	* @see #paintBorder(Graphics)
	* @see Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
	*/
	private void paintModusBorder(Graphics g, int x, int y, int width, int height) {
	if(isEditable())
		return;
	if(isSelectable() && !isSelected() && isMouseOver())
		m_mouseOverBorder.paintBorder(this, g, x, y, width, height);
	else if(isSelectable() && isSelected())
		m_selectionBorder.paintBorder(this, g, x, y, width, height);
	}
	
	/**
	 * Returns the border insets for this panel. Oberserve that an empty border is used
	 * to reserve the required space but the real borders will be drawn on by {@link #paintBorder(Graphics)}
	 * and {@link #paintModusBorder(Graphics, int, int, int, int)}
	 */
	public Insets getInsets() {
		if(isEditable() == false && isSelectable())
			// all borders have the same insets
			return new SimpleInsets(m_emptyBorder.getBorderInsets(this), super.getInsets());
		return super.getInsets();
	}

  public PropertyMapIF getDrawableProperties() {
  	return m_properties;
  }

  /**
	 * Returns the viewer component for this MM-Panel.
	 */
	public JComponent getViewerComponent() {
		return m_viewerComp;
	}
	
	/**
	 * Sets the label for this MM-Panel.
	 */
	public void setLabel(String label) {
		if(getPanelRenderingHints().isAppendEqualSign())
			label += " = ";
		m_labelRenderer.setText(label);
		// use bold font for labels
		m_labelRenderer.setFontStyle(Font.BOLD);
		// update label's visibility
		m_labelRenderer.setVisible(isLabelVisible());
		repaintAll();
	}
	
	/**
	 * Sets whether the label should be visible. Default is <code>true</code>.
	 */
	public void setLabelVisible(boolean visible) {
		getPanelRenderingHints().setLabelDisplayed(visible);
	}
	
	/**
	 * Returns whether the label is visible.
	 */
	public boolean isLabelVisible() {
		return getPanelRenderingHints().isLabelDisplayed();
	}

  public void render(DisplayProperties properties) {
  	render();
  }

  /** Updates this panel's visualisation. */
  public void render() {
		m_labelRenderer.setVisible(isLabelVisible());
  	repaintAll();
  }
  
  /** 
   * Validates and repaints this drawable.
   * @see JComponent#revalidate() 
   */
  public void repaintAll() {
  	revalidate();
  	repaint();
  }

  /** Returns the master object of this drawable. */
	public MMObjectIF getMaster() {
		return m_mmMaster;
	}

  /**
   * Sets the font used by this panel. May be overridden by the font specified in the display properties
   * of this panel's master.
   */
  public void setFont(Font font) {
    m_font = font;
    super.setFont(getFont());
    if(getViewerComponent() != null)
    	getViewerComponent().setFont(font);
    if(m_labelRenderer != null)
    	m_labelRenderer.setFont(font);
  }

  /**
   * Returns the font that is used for rendering this component.
   * It will be the font that is either defined by the user in the display properties of the master
   * or the font set by a previous {@link #setFont} call or the font determined by the current theme
   * (in that order, unless the chosen font is not null).
   */
  public Font getFont() {
    // needed because SWING calls this method before all fields are initialized
    if(getMaster() == null)
      return super.getFont();
    else if(m_font != null)
      return m_font;
    else
      return getMaster().getDisplayProperties().getFont();
  }

	/**
	 * Sets whether the text color will be overidden with the background color,
	 * i.e. whether the text is visible or not.
	 * Should be overidden but called from subclasses.
	 */
  public void setTextVisible(boolean visible) {
  	m_isTextVisible = visible;
  }

  /**
   * Returns if the text of this component should be visible or not.
   */
  public boolean isTextVisible() {
  	return m_isTextVisible;
  }

  /* Forwards color to viewer and label. */
  public void setForeground(Color c) {
    super.setForeground(c);
    if(getViewerComponent() != null)
    	getViewerComponent().setForeground(c);
    if(m_labelRenderer != null)
    	m_labelRenderer.setForeground(c);
  }
  
  /* Forwards color to viewer and label. */
  public void setBackground(Color c) {
  	super.setBackground(c);
    if(getViewerComponent() != null)
    	getViewerComponent().setBackground(c);
    if(m_labelRenderer != null)
    	m_labelRenderer.setBackground(c);
  }

  /**
   * Sets if the content represented in this component should be marked edited or not.
 	 * Should be overidden but called from subclasses.
   */
  public void setEdited(boolean isEdited) {
    m_isEdited = isEdited;
  }

  /**
   * Returns if the content of this component was already edited or not.
   */
  public boolean isEdited() {
  	return m_isEdited;
  }

  /**
   * Adds the selection listener <code>l</code> to the list of registered listeners that will
   * be informed when this MMPanel will be selected or deselected.
   */
  public void addSelectionListener(SelectionListener l) {
  	m_selectionListeners.add(l);
  }

  /**
   * Removes the selection listener <code>l</code> from the list of registered listeners.
   */
  public void removeSelectionListener(SelectionListener l) {
  	m_selectionListeners.remove(l);
  }

  /**
   * Returns an array with all registered {@link SelectionListener}s.
   */
  public SelectionListener[] getSelectionListeners() {
  	SelectionListener[] ls = new SelectionListener[m_selectionListeners.size()];
  	return (SelectionListener[])m_selectionListeners.toArray(ls);
  }

  /**
   * Returns whether this panel can be selected.
   */
  public boolean isSelectable() {
  	return m_isSelectable;
  }

  /**
   * Sets whether this panel can be selected.
   */
  public void setSelectable(boolean selectable) {
  	m_isSelectable = selectable;
  	repaintAll();
  }

  /**
   * Selects or deselects this panel and informs
   * all registered selection listeners.
   */
  public void setSelected(boolean selected) {
    boolean oldSelection = m_isSelected;
  	m_isSelected = selected;
    SelectionListener[] ls = getSelectionListeners();
    for(int i = 0; i < ls.length; i++) {
      boolean cancelSelection = false;
      if(selected)
        cancelSelection = ! ls[i].select(this);
      else
        cancelSelection = ! ls[i].deselect(this);
      if(cancelSelection) {
      	// previous selections in loop may have incorrect state
        for(int j = 0; j < ls.length; j++) {
        	ls[j].deselect(this);
        }
        m_isSelected = oldSelection;
        return;
      }
    }
    repaint();
  }

  /**
   * Returns if this panel is selected or deselected.
   */
  public boolean isSelected() {
  	return m_isSelected;
  }

  /**
   * Returns if there is a root panel for this MMPanel. 
   */
  public boolean isOwnRootPanel() {
  	return m_rootPanel == null;
  }

  /**
   * Sets the root panel of this MMPanel. 
   * The root panel must be an instance of {@link MMCompoundPanel}.
   * @throws IllegalArgumentException if this MMPanel is a compound panel 
   */
  protected void setRootPanel(MMCompoundPanel rootPanel) {
  	if(isCompoundPanel())
  		throw new IllegalArgumentException("Cannot set a root panel on a compound panel!");
  	m_rootPanel = rootPanel;
  }
  
  /**
   * Returns the root panel of this MMPanel if there is one. 
   * The result is an instance of MMCompoundPanel.
   */
  public MMCompoundPanel getRootPanel() {
  	if(isCompoundPanel())
  		return (MMCompoundPanel) this;
  	return m_rootPanel;
  }
  
  /**
   * Returns if this panel is an instance of MMCompoundPanel.
   */
  public boolean isCompoundPanel() {
  	return this instanceof MMCompoundPanel;
  }

  /**
   * Sets this panel to be highlighted or not.
   */
  void setHighLighted(boolean highlighted) {
  	if(m_isHighlighted == highlighted)
  		return;
    m_isHighlighted = highlighted;
    if(getMaster() == null)
    	return;
    if(highlighted)
      setForeground(getMaster().getDisplayProperties().getSelectionColor());
    else
      setForeground(getMaster().getDisplayProperties().getObjectColor());//m_oldHighlightForeground);
  }
  
  public void setMouseOver(boolean mouseOver) {
  	if(m_isMouseOver == mouseOver)
  		return;
		m_isMouseOver = mouseOver;
		repaint();
  }
  
  /**
   * Returns if the mouse pointer is currently above this MMPanel and if it is selectable.
   */
  public boolean isMouseOver() {
  	return m_isMouseOver && isSelectable();
  }

  /**
   * Returns if this panel is highlighted.
   */
  boolean isHighlighted() {
    return m_isHighlighted;
  }
  
  /**
   * Sets the border color for this panel. A value of <code>null</code> means the current background color.
   * Note: this is a convenience method for <code>setBorder(new LineBorder(c, 2))</code>
   * i.e. the created border has a thickness of 2 pixels.
   * 
   * @param c a color; may be <code>null</code>
   */
  public void setBorderColor(Color c) {
  	if(c == null)
  		c = getBackground();
  	setBorder(new LineBorder(c, 2));
  }
  
  /**
   * Returns the border color, or <code>null</code> if no border color was set.
   * @see #setBorderColor(Color)
   */
  public Color getBorderColor() {
  	if(getBorder() != null && getBorder() instanceof LineBorder)
  		return ((LineBorder) getBorder()).getLineColor();
  	return null;
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if(!enabled) {
      m_oldEditForeground = getForeground();
      setForeground(Color.GRAY);
    } else {
      if(m_oldEditForeground != null)
        setForeground(getMaster().getDisplayProperties().getObjectColor());//m_oldEditForeground
    }
  }
  
  protected Dimension getPreferredPanelSize() {
  	return super.getPreferredSize();
  }
  
  public Dimension getPreferredSize() {
  	Dimension result = getPreferredPanelSize();
  	return getPreferredSize(result);
  }
  
	/**
	 * Returns the preferred size according to the flags <code>m_preferredWidth</code> 
	 * and <code>m_preferredHeight</code>.
	 * 
	 *  @see MMPanel#setHeight(int)
	 *  @see MMPanel#setWidth(int)
	 *  @see MMPanel#setPreferredSize(Dimension)
	 */
	protected Dimension getPreferredSize(Dimension preferredSize) {
		Dimension result = new Dimension(preferredSize);
    if(m_preferredWidth != -1)
    	result.width = m_preferredWidth;
    if(m_preferredHeight != -1)
    	result.height = m_preferredHeight;
		return result;
	}
	
  public void setPreferredSize(Dimension d) {
    m_preferredWidth = d.width;
    m_preferredHeight = d.height;
    validate();
  }
  
  /**
   * Ensures that this component has always its preferred size
   * if the layout manager this component is handled by also respects
   * the minimum size.
   * 
   * @see JComponent#getMinimumSize()
   */
  public Dimension getMinimumSize() {
  	return getPreferredSize();
  }
  
  /**
   * Ensures that this component has always its preferred size
   * if the layout manager this component is handled by also respects
   * the maximum size (e.g. {@link javax.swing.BoxLayout}).
   * 
   * @see JComponent#getMaximumSize()
   */
  public Dimension getMaximumSize() {
  	return getPreferredSize();
  }
  
  
  /**
   * Returns the current border insets.
   */
	protected SimpleInsets getBorderInsets() {
//  selection and mouse over border have same sizes
		return new SimpleInsets(null, m_selectionBorder.getBorderInsets(this));
	}
	
	/**
	 * Returns if the width was set to a fixed value.
	 * Only applies if the parent's layout respects the preferred width.
	 */
  public boolean isFixedWidth() {
  	return m_preferredWidth != -1;
  }
  
	/**
	 * Returns if the height was set to a fixed value.
	 * Only applies if the parent's layout respects the preferred height.
	 */
  public boolean isFixedHeight() {
  	return m_preferredHeight != -1;
  }
  
  /**
   * Sets the preferred height of this panel.
   * Setting <code>-1</code> will restore the default (automatic) size.
	 * Only applies if the parent's layout respects the preferred height.
   */
  public void setHeight(int height) {
    m_preferredHeight = height;
    revalidate();
  }

  /**
   * Sets the preferred width of this panel.
   * Setting <code>-1</code> will restore the default (automatic) size.
	 * Only applies if the parent's layout respects the preferred width.
   */
  public void setWidth(int width) {
    m_preferredWidth = width;
    revalidate();
  }
    
  /**
   * Performs a focus request for the given component depending on the active window owning this component.
   * This method is intended to gather the appropriate focus even if the current focus is inside
   * a html element.
   */
  protected static void requestFocusFor(Component c) {
		Window w = SwingUtilities.windowForComponent(c);
		boolean focusInWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == w;
		if(focusInWindow)
			c.requestFocusInWindow();
		else
			c.requestFocus();
  }
  
  private boolean isEditable() {
  	if(this instanceof MMEditablePanel)
  		return ((MMEditablePanel) this).isEditable();
  	if(this instanceof MMCompoundPanel)
  		return ((MMCompoundPanel) this).isEditable();
  	return false;
  }
  
  class BasicMouseListener extends MouseAdapter {
  	
  	protected MMPanel m_panel;
  	
  	BasicMouseListener(MMPanel panel) {
  		m_panel = panel;
  	}
 }
  
  class CompoundListener extends BasicMouseListener {
  	
  	
  	CompoundListener(MMPanel panel) {
  		super(panel);
//  		log("compound listener for " + panel.getClass().getName());
  	}
  	
  	public void mouseEntered(MouseEvent e) {
  		log("compound::entered");
  		if(getRealPanel().isEditable() == false && getRealPanel().isSelectable()) {
  			select(getRealPanel(), true, HAND_CURSOR);
  		} else {
  			select(getRealPanel(), false, DEFAULT_CURSOR);
  		}
  	}
  	
  	public void mouseExited(MouseEvent e) {
  		log("compound::exited");
			select(getRealPanel(), false, DEFAULT_CURSOR);
  	}
  	
  	public void mouseClicked(MouseEvent e) {
  		log("compound::clicked");
  		if(getRealPanel().isEditable() == false && getRealPanel().isSelectable()) {
  			getRealPanel().setSelected(!getRealPanel().isSelected());
  			e.consume();
  		}
  	}
  	
  	private MMPanel getRealPanel() {
  		return (MMPanel) m_panel;
  	}
 }
  
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private final static Cursor CARET_CURSOR = new Cursor(Cursor.TEXT_CURSOR);

  private static void select(MMPanel panel, boolean flag, Cursor cursor) {
		panel.setHighLighted(flag);
		panel.setMouseOver(flag);
  	panel.setCursor(cursor);
  }

  class EditableListener extends BasicMouseListener {
  	
  	EditableListener(MMEditablePanel panel) {
  		super(panel);
//  		log("editable listener for " + panel.getClass().getName());
  	}
  	
  	public void mouseEntered(MouseEvent e) {
  		log("editable::entered");
  		// check if root panel exists
  		if(getRealPanel().isOwnRootPanel() == false) {
  			// check if root is not editable but selectable and should be highlighted
  			if(getRealPanel().getRootPanel().isEditable() == false
  					&& getRealPanel().getRootPanel().isSelectable()) {
        	select(getRealPanel().getRootPanel(), true, HAND_CURSOR);
        	getRealPanel().setCursor(HAND_CURSOR);
        	return;
  			}
  		}
    	if(getRealPanel().isEditable()) {
    		// show caret
    		select(getRealPanel(), false, CARET_CURSOR);
    	} else if(getRealPanel().isSelectable()) {
    		// highlight panel
    		select(getRealPanel(), true, HAND_CURSOR);
    	}
  	}
  	
  	private MMEditablePanel getRealPanel() {
  		return (MMEditablePanel) m_panel;
  	}
  	
  	public void mouseExited(MouseEvent e) {
  		log("editable::exited");  		
  		// check if root panel exists
  		if(getRealPanel().isOwnRootPanel() == false) {
  	    // check if root panel should be selected
  			if(getRealPanel().getRootPanel().isEditable() == false 
  					&& getRealPanel().getRootPanel().isSelectable()) {
        	select(getRealPanel().getRootPanel(), false, DEFAULT_CURSOR);
        	return;
  			}
  		}
  		select(m_panel, false, DEFAULT_CURSOR);
  	}
  	
  	public void mouseClicked(MouseEvent e) {
  		log("editable::clicked");
  		// do nothing if disabled
			if(getRealPanel().isEnabled() == false)
				return;
  		// check if root panel exists
  		if(getRealPanel().isOwnRootPanel() == false) {
  			// check if root is not editable but selectable and should be selected
  			if(getRealPanel().getRootPanel().isEditable() == false
  					&& getRealPanel().getRootPanel().isSelectable()) {
  				getRealPanel().getRootPanel().setSelected(!getRealPanel().getRootPanel().isSelected());
  				e.consume();
        	return;
  			}
  		}
  		// edit panel
  		if(getRealPanel().isEditable()) {
    		if(getRealPanel().getViewerComponent().hasFocus())
    			getRealPanel().startEditing();
    		else
    			// start editing by requesting focus...
    			requestFocusFor(getRealPanel().getViewerComponent());
  		} else {
  			// select panel
	  		if(getRealPanel().isSelectable()) {
	      	getRealPanel().setSelected(!getRealPanel().isSelected());
	    		e.consume();
	      }
  		}
  	}
  }
  
  private void log(String message) {
//  	System.out.println(message);
  }
}
