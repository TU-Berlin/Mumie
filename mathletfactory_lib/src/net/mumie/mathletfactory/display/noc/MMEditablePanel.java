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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.mumie.mathletfactory.appletskeleton.util.theme.RollOverListener;
import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.display.layout.SimpleInsets;
import net.mumie.mathletfactory.display.noc.util.EventUtils;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
import net.mumie.mathletfactory.util.ResourceManager;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 * This class is the base for all single MM-Panels, i.e. container drawables
 * which do not use other MM-Panels to display itself and which allow user interaction.<br>
 * A MMEditablePanel has 2 basic views (for viewing and editing values) and
 * visualizes the <code>edited</code> flag of MM-Objects with a question mark.<br>
 * 
 * Subclasses must define a <i>viewing component</i> (must be an instance of {@link JComponent})
 * and the handling of the MM-Object's data via the following methods: {@link #getEditingContent()},
 * {@link #checkContent(String)} and {@link #applyContent(String)}.
 *  
 * @author Gronau
 * @mm.docstatus finished
 */
public abstract class MMEditablePanel extends MMPanel {

	public final static int VIEWING_MODE = 0;
	public final static int EDITING_MODE = 1;
	
	final static int NO_ACTION = 0;
	final static int SWITCHING_TO_DISPLAY_MODE_ACTION = 1;
	final static int SWITCHING_TO_EDITOR_MODE_ACTION = 2;
	
	final static String ENTER_ACTION_KEY = "enter-key";
	final static String ESCAPE_ACTION_KEY = "escape-key";
	final static String START_EDITING_ACTION_KEY = "start-editing";
	final static String ESCAPE_TOOLTIP_ACTION_KEY = "cancel-tooltip";
	private final static String TAB_FORWARD = "tab-forward";
	private final static String TAB_BACKWARD = "tab-backward";
	
	private JComponent m_valueViewer;
	private ContentViewerPanel m_contentPane;
	private int m_mode = VIEWING_MODE;
	private int m_action = NO_ACTION;
	private JTextField m_textField;
	private ToolTip m_toolTip;
	
	protected final static Font TEXTFIELD_FONT = new Font(null, Font.PLAIN, 12);
	protected final static Cursor TEXT_CURSOR = new Cursor(Cursor.TEXT_CURSOR);
	protected final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	protected final static Color TEXTFIELD_BACKGROUND = new Color(255, 255, 128);
	private final static int MIN_TEXTFIELD_LENGTH = 3;

	private boolean m_isEditable = false;
	
	/**
	 * Returns the text that will be editable in the textfield.
	 */
	protected abstract String getEditingContent();

  /**
   * Returns true if the entered content is valid.
   * This method must be overwritten for parsing and other content checking.
   */
	protected abstract boolean checkContent(String content);

	/**
	 * Forwards the content to the master object
	 * via {@link java.beans.PropertyChangeEvent}s.
	 */
	protected abstract void applyContent(String content);
	
	/**
	 * Creates a new editable panel for the given master MM-Object and transformer with an
	 * empty {@link JPanel} as viewing component.
	 */
	public MMEditablePanel(MMObjectIF master, ContainerObjectTransformer transformer) {
		this(master, transformer, new JPanel());
	}
	
	/**
	 * Creates a new editable panel for the given master MM-Object, transformer and viewing component.
	 */
	public MMEditablePanel(MMObjectIF master, ContainerObjectTransformer transformer, JComponent valueViewer) {
		super(master, transformer, new ContentViewerPanel());
		((ContentViewerPanel) getViewerComponent()).setMasterPanel(this);
		
		addPropertyChangeListener((PropertyHandlerIF) master);

		m_valueViewer = valueViewer;
		m_contentPane = (ContentViewerPanel) getViewerComponent();
		m_contentPane.add(m_valueViewer);
		m_contentPane.setFocusable(m_isEditable);
		m_contentPane.addFocusListener(new ContainerFocusHandler());
		addMouseListener(new EditableListener((MMEditablePanel) this));
		m_contentPane.addMouseListener(new EditableListener((MMEditablePanel) this));
  	
    // enter on viewer
    m_contentPane.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), START_EDITING_ACTION_KEY);
    // space on viewer
    m_contentPane.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), START_EDITING_ACTION_KEY);
    
    // action bindings
    m_contentPane.getActionMap().put(START_EDITING_ACTION_KEY, new StartEditingAction());
    
    m_contentPane.revalidate();
	}
	
  /**
   * Returns the viewing component that is shown in viewing mode.
   */
	protected JComponent getValueViewer() {
		return m_valueViewer;
	}
	
	/**
	 * Initializes the textfield after startup. This method will be called before
	 * first switch to editing mode in order to reduce memory usage of non-editable and
	 * not edited container drawables.
	 */
	private void initializeTextfield() {
		log("initializeTextfield");
    m_textField = new JTextField("", 5);
    m_textField.setFocusTraversalKeysEnabled(false);
    m_textField.setFont(TEXTFIELD_FONT);
    m_textField.setBackground(TEXTFIELD_BACKGROUND);
		m_textField.addFocusListener(new TextfieldFocusHandler());
    
    // enter on editor
    m_textField.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_ACTION_KEY);
    // escape on editor
    m_textField.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE_ACTION_KEY);
    // tab on editor
    m_textField.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), TAB_FORWARD);
    // shift-tab on editor
    m_textField.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK), TAB_BACKWARD);
		
    // action bindings
    m_textField.getActionMap().put(ENTER_ACTION_KEY, new TextfieldEnterAction());
    m_textField.getActionMap().put(ESCAPE_ACTION_KEY, new TextfieldEscapeAction());
    m_textField.getActionMap().put(TAB_FORWARD, new TextfieldTabForwardAction());
    m_textField.getActionMap().put(TAB_BACKWARD, new TextfieldTabBackwardAction());
	}
	
	/**
	 * Switches to editing mode.
	 */
	void startEditing() {
		if(!isEditable())
			return;
		if(m_textField == null)
			initializeTextfield();
		if (isEdited())
			m_textField.setText(getEditingContent());
		else
			m_textField.setText(null);
		if(m_textField.getText().length() > MIN_TEXTFIELD_LENGTH)
			m_textField.setColumns(m_textField.getText().length());
		else
			m_textField.setColumns(MIN_TEXTFIELD_LENGTH);
		switchToEditorMode();
	}
	
	/**
	 * Prepares the switching to viewing mode.
	 */
	private void processStopping(boolean applyContent, boolean cancelOnError, boolean keepFocus) {
		if(m_mode != EDITING_MODE)
			return;
		String content = m_textField.getText();
		if(applyContent) {
			if(content.trim().length() > 0 && checkContent(content)) {
				applyContent(content);
				setEdited(true);
			} else if(cancelOnError) {
				showToolTip(ResourceManager.getMessage("no_operation"));
				return;
			}
		}
		stopEditing(keepFocus);
	}
	
	/**
	 * Stops the editing mode.
	 */
	private void stopEditing(boolean keepFocus) {
		if(m_mode != EDITING_MODE)
			return;
		log("stopEditing");
		if(keepFocus) {
			m_action = SWITCHING_TO_DISPLAY_MODE_ACTION;
			requestFocusFor(m_contentPane);
			return;
		} else {
			switchToDisplayMode();
		}
		repaint();
	}
	
	/**
	 * Switches to editing mode.
	 */
	private void switchToEditorMode() {
		if(m_mode == VIEWING_MODE) {
			m_mode = EDITING_MODE;
			m_contentPane.remove(getValueViewer());
			m_contentPane.add(m_textField);
			m_textField.selectAll();
			requestFocusFor(m_textField);
			repaintAll();
		}
	}
	
	/**
	 * Switches to viewing mode.
	 */
	private void switchToDisplayMode() {
		m_mode = VIEWING_MODE;
		m_action = NO_ACTION;
		m_contentPane.remove(m_textField);
		if(isEdited())
			m_contentPane.add(m_valueViewer);
		repaintAll();
	}
	
	/**
	* Also adds the mouse listener to the value viewer.
	* @see java.awt.Component#addMouseListener(MouseListener)
	*/
	public void addMouseListener(MouseListener l){
	  super.addMouseListener(l);
	  if(m_valueViewer != null)
	  	m_valueViewer.addMouseListener(l);
	}

	
	/**
	 * Allows to set a custom additional margin (i.e. empty space between content and border).
	 */
	public void setViewerMargin(int size) {
		m_contentPane.setViewerMargin(size);
	}
	
  public void setMinimumViewerSize(Dimension d) {
  	m_contentPane.setMinimumViewerSize(d);
  }
	
  /**
   * Returns the currently visible component according to the current mode.
   * Note that the question mark is also visualized in viewing mode.
   */
	JComponent getActiveComponent() {
		if(getCurrentMode() == VIEWING_MODE)
			return getValueViewer();
		else
			return getTextField();
	}

	/**
	 * Shows a tooltip with the given text to mark an error during value validation.
	 */
	private void showToolTip(String text) {
		if(m_toolTip == null)
			m_toolTip = new ToolTip(this);
		m_toolTip.setToolTipText(text);
		m_toolTip.showToolTip();
	}

	/**
   * Returns the text field component that is shown in editor mode.
   */
  protected JTextField getTextField() {
		return m_textField;
	}
  
  protected int getCurrentMode() {
  	return m_mode;
  }
  
  /**
   * Returns whether this panel is currently in edting mode.
   */
  protected boolean isEditingMode() {
  	return getCurrentMode() == EDITING_MODE;
  }
  
  /**
   * Returns whether this panel is currently in viewing mode.
   */
  protected boolean isViewingMode() {
  	return getCurrentMode() == VIEWING_MODE;
  }

	/**
	 * Returns whether the values in th panel are editable.
	 */
	public boolean isEditable() {
		return m_isEditable;
	}
	
  /**
   * Sets whether the values in this panel should be editable.
   * This method should not be called by an application. Instead
   * the <code>setEditable()</code> method of the master
   * should be called, after which this method is called internally
   * indirectly by the transformer (and by this any former value will be
   * overwritten).
   */
	public void setEditable(boolean isEditable) {
    if(isEditable == isEditable())
      return;
		m_isEditable = isEditable;
		m_contentPane.setFocusable(isEditable);
		repaintAll();
	}
	
  public void setEdited(boolean isEdited) {
    if(isEdited() == isEdited)
      return;
    super.setEdited(isEdited);
    if(isEdited)
    	m_contentPane.add(m_valueViewer);
    else
    	m_contentPane.remove(m_valueViewer);
    revalidate();
  	repaint();
  }
  
  public void setTextVisible(boolean visible) {
  	if(isTextVisible() == visible) 
  		return;
  	super.setTextVisible(visible);
  	repaint();
  }
  
  public void setFont(Font font) {
    super.setFont(font);
    if (getValueViewer() != null)
    	getValueViewer().setFont(font);
  }

  public void setForeground(Color c) {
    super.setForeground(c);
    if(getValueViewer() != null)
    	getValueViewer().setForeground(c);
  }
    
//  public void setBackground(Color c) {
//  	super.setBackground(c);
//  	if(getValueViewer() != null)
//  		getValueViewer().setBackground(c);
//  }

	/**
	 * Returns whether the content of the textfield is empty or only white spaces.
	 */
	protected boolean isEmpty() {
	    String text = m_textField.getText();
		if(text.length() == 0 || text.matches("^\\s+$"))
			return true;
		else
			return false;
	}
	
	private void log(String message) {
//		System.out.println("MMEditablePanel@" + Integer.toHexString(hashCode()) + ": " + message);
	}
	

	
	class TextfieldFocusHandler extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			log("Textfield: focusGained");
			repaint();
		}
		
		public void focusLost(FocusEvent e) {
			log("Textfield: focusLost");
			if(m_action == SWITCHING_TO_DISPLAY_MODE_ACTION)
				return;
			if(EventUtils.isEventTemporary(e)) {
				repaint();
				return;
			}
			if(m_mode == EDITING_MODE)
				processStopping(true, false, false);
		}
	}
	
	class ContainerFocusHandler extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			log("Container: focusGained");
			log("Mode = " + m_mode);
			log("Action = " + m_action);
			log("Temporary = " + EventUtils.isEventTemporary(e));
			if(m_action == SWITCHING_TO_DISPLAY_MODE_ACTION) {
				stopEditing(false);
				return;
			}
			if(isEditable() == false || isEnabled() == false)
				return;
			if(m_mode == EDITING_MODE || EventUtils.isEventTemporary(e)) {
				repaint();
				return;
			}
			if(m_mode == VIEWING_MODE)
				startEditing();
			repaint();
		}

		public void focusLost(FocusEvent e) {
			log("Container: focusLost");
			repaint();
		}
	}
	
	class TextfieldEnterAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			processStopping(true, true, true);
		}
	}

	class TextfieldEscapeAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			processStopping(false, false, true);
		}
	}
	
	class TextfieldTabForwardAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			log("Tab forward");
			KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
		}
	}

	class TextfieldTabBackwardAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			log("Tab backward");
			KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(MMEditablePanel.this.m_contentPane);
		}
	}
	
	class StartEditingAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			if(isEditable() == false || isEnabled() == false)
				return;
  		if(m_contentPane.hasFocus())
  			startEditing();
  		else
  			requestFocusFor(MMEditablePanel.this);
		}
	}

	class ToolTipEscapeAction extends AbstractAction {
		public void actionPerformed(ActionEvent event) {
			log("Cancel tooltip");
			m_toolTip.hideToolTip();
		}
	}
	
	private class ToolTip extends JFrame implements Runnable {
		private final static int PAUSE = 15;
		private Color t_background = new Color(255, 50, 0);
		private Color t_foreground = Color.BLACK;
		private int t_time = 1500;
		private boolean m_stop = false;
		
		private JComponent t_parent; 
		private Point t_location;
		private JLabel t_toolTip;
		
		public ToolTip(JComponent parent) {
			this(parent, "");
		}
		
		public ToolTip(JComponent parent, String text) {
			super();
			t_parent = parent;
			
			getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE_TOOLTIP_ACTION_KEY);
			getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ESCAPE_TOOLTIP_ACTION_KEY);

			getRootPane().getActionMap().put(ESCAPE_TOOLTIP_ACTION_KEY, new ToolTipEscapeAction());
			
			t_toolTip = new JLabel(text);
			t_toolTip.setBorder(new TitledBorder(""));
			t_toolTip.setOpaque(true);
			t_toolTip.setBackground(t_background);
			t_toolTip.setForeground(t_foreground);
			
			this.setUndecorated(true);
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(t_toolTip, BorderLayout.CENTER);
		}
		
		public ToolTip(JComponent parent, String text, int millsec) {
			this(parent, text);
			if (millsec > 0) t_time = millsec;
		}
		
		public void run() {
			int loop = t_time/ PAUSE;
			try {
				while (loop >= 0 && m_stop == false) {
					if (t_parent != null && !t_parent.isShowing()) {
						break;
					}
					Thread.sleep(PAUSE);
					loop--;
				}
			} catch (InterruptedException e) {}
			finally {
				this.setVisible(false);
				m_stop = false;
			}
		}
		
		public void showToolTip() {
			showToolTipFor(t_time);
		}	
		
		public void showToolTipFor(int millsec) {
			this.pack();
			if(!t_parent.isShowing())
				return;
			t_location = t_parent.getLocationOnScreen();
			t_location.setLocation(t_location.x + 20, t_location.y - this.getHeight() - 5);
			this.setLocation(t_location);
			if (millsec > 0)
				t_time = millsec;
			this.setVisible(true);
			new Thread(this).start();
		}
		
		public void hideToolTip() {
			m_stop = true;
		}
		
		public void setToolTipText(String text) {
			t_toolTip.setText(text);
		}
		
		public String getToolTipText() {
			return t_toolTip.getText();
		}
		
		public void setToolTipBackground(Color bg) {
			t_background = bg;
			t_toolTip.setBackground(bg);
		}
		
		public void setToolTipForeground(Color fg) {
			t_foreground = fg;
			t_toolTip.setBackground(fg);
		}
	}
}
class ContentViewerPanel extends JPanel implements Alignable {
	
	MMEditablePanel m_masterPanel;
	
	protected final static Font QUESTION_MARK_FONT = new Font(null, Font.BOLD, 16);

	private Border m_focusedBorder = BorderFactory.createLineBorder(MetalLookAndFeel.getFocusColor());
	private Border m_editableBorder = new BevelBorder(BevelBorder.LOWERED);
	private SimpleInsets m_outsideInsets = new SimpleInsets(0);
	private SimpleInsets m_editableBorderInsets = new SimpleInsets(m_outsideInsets, m_editableBorder.getBorderInsets(null));
	private SimpleInsets m_insideInsets = new SimpleInsets(0);
	private SimpleInsets m_allBorderInsets = new SimpleInsets(m_editableBorderInsets, m_focusedBorder.getBorderInsets(null), m_insideInsets);

	private Dimension m_minSize = new Dimension(20, 26);

	private final static RollOverListener m_rollOverListener = new RollOverListener();
	
  private final static MumieLogger LOGGER = MumieLogger.getLogger(ContentViewerPanel.class);
  private final static LogCategory DRAW_BASELINE = LOGGER.getCategory("ui.draw-baseline");
  private final static LogCategory PRINT_BASELINE = LOGGER.getCategory("ui.print-baseline");
  
	ContentViewerPanel() {
		super(null); // disable any layout manager
		addMouseListener(m_rollOverListener);
		setBorder(null);
	}
	
	void setMasterPanel(MMEditablePanel masterPanel) {
		m_masterPanel = masterPanel;
	}
	
	private MMEditablePanel getMasterPanel() {
		return m_masterPanel;
	}
	
	public double getBaseline() {
		double baseline = 0;
		if(getMasterPanel().isEditingMode())
			baseline = getPreferredQuestionMarkSize().getHeight();
		else if(getMasterPanel().isEdited() == false)//isViewingMode() && 
			baseline = getPreferredQuestionMarkSize().getHeight();
		else 	if(getMasterPanel().getValueViewer() instanceof Alignable)
			baseline = ((Alignable) getMasterPanel().getValueViewer()).getBaseline() + new SimpleInsets(getBorderInsets(), getMasterPanel().getValueViewer().getInsets()).top;
		else
			baseline = getMasterPanel().getValueViewer().getHeight() / 2;
		return baseline;
	}
	
	/**
	 * Performs the layouting (i.e. resizing and positioning) of the active component.
	 * Will be called by SWING during validation process.
	 */
	public void doLayout() {
		JComponent c = getMasterPanel().getValueViewer();
		if(getMasterPanel().isEditingMode())
			c = getMasterPanel().getTextField();
		SimpleInsets allBorder = getBorderInsets();
		Dimension dim = allBorder.getInsideDim(this.getSize());
		c.setSize(dim);
		c.setLocation(allBorder.left, allBorder.top);
	}
	
	Dimension getPreferredQuestionMarkSize() {
		if(getGraphics() != null)
			return ((Graphics2D) getGraphics()).getFontMetrics(QUESTION_MARK_FONT).getStringBounds("?", getGraphics()).getBounds().getSize();
		return new Dimension(0, 0);
	}
	
	public Dimension getPreferredSize() {
		if(getMasterPanel().getCurrentMode() == MMEditablePanel.VIEWING_MODE) {
			// edited == false --> use question mark size + borders
			if(getMasterPanel().isEdited() == false)
				return getPreferredSize(getBorderInsets().getOutsideDim(getPreferredQuestionMarkSize()));
			// nothing visible --> use min size + borders
			if(getMasterPanel().isTextVisible() == false)
				return getMasterPanel().getPreferredSize(getBorderInsets().getOutsideDim(m_minSize));
		}
		
		// request size of visible component (viewer or textfield)
		Dimension prefSize = getMasterPanel().getActiveComponent().getPreferredSize();
		
		// textfield should have same height than the value viewer
		if(getMasterPanel().getCurrentMode() == MMEditablePanel.EDITING_MODE) {
			// active comp is textfield
			int vvHeight = getMasterPanel().getValueViewer().getPreferredSize().height;
			if(vvHeight > prefSize.height)
				prefSize.height = vvHeight;
		}
		
		// return size of active/visible component + borders
		return getPreferredSize(getBorderInsets().getOutsideDim(prefSize));
	}
	
	/**
	 * Returns the preferred size according to the minumum size and the flags <code>m_preferredWidth</code> 
	 * and <code>m_preferredHeight</code>.
	 * 
	 *  @see MMPanel#getPreferredSize(Dimension)
	 */
	private Dimension getPreferredSize(Dimension preferredSize) {
		// calc min size + borders
		Dimension minBorderSize = getBorderInsets().getOutsideDim(m_minSize);
		if(preferredSize.width < minBorderSize.width)
			preferredSize.width = minBorderSize.width;
		if(preferredSize.height < minBorderSize.height)
			preferredSize.height = minBorderSize.height;
		return getMasterPanel().getPreferredSize(preferredSize);
	}
	
	private SimpleInsets getBorderInsets() {
		if(getMasterPanel().isEditable() || getMasterPanel().isSelectable())
			return new SimpleInsets(null, m_allBorderInsets);
		else
			// no borders if not editable and not selectable
			return m_insideInsets;
	}
	
	/**
	 * Allows to set a custom additional margin (i.e. empty space between content and border).
	 */
	void setViewerMargin(int size) {
		m_insideInsets.setInsets(size);
		m_allBorderInsets.update();// recursive update
		revalidate();
	}

	void setMinimumViewerSize(Dimension d) {
  	if(d == null)
  		throw new NullPointerException();
  	m_minSize = d;
  }
	
  /*
   * Utility method for returning if the focus is inside this MM-Panel.
   */
  private boolean focusInside() {
  	return hasFocus() || (getMasterPanel().getTextField() != null && getMasterPanel().getTextField().hasFocus());
  }
	
	protected void paintBorder(Graphics g) {
		Insets insets = getInsets();
		int x = 0, y = 0, width = getWidth(), height = getHeight();
		if(getMasterPanel().isEditable() && isEnabled()) {
			m_editableBorder.paintBorder(this, g, x, y, width, height);
			if(focusInside()) {
				Insets i = m_editableBorder.getBorderInsets(this);
				m_focusedBorder.paintBorder(this, g, x+i.left, y+i.top, width-i.left-i.right, height-i.top-i.bottom);
			}
		}
	}
	
  protected void paintComponent(Graphics g) {
   	Graphics2D g2d = (Graphics2D) g.create();
		Insets insets = getInsets();
  	if(isOpaque()) {
			Color oldColor = g.getColor();
			if(getMasterPanel().isEditable()) {
				if(m_rollOverListener.isMouseOver(this))
					g2d.setColor(new Color(230, 240, 245));
				else
					g2d.setColor(new Color(250,250,250));
			} else {
				g2d.setColor(getBackground());
			}
			g2d.fillRect(insets.left, insets.top, getWidth(), getHeight());
			g2d.setColor(oldColor);
  	}
		if(getMasterPanel().isViewingMode() && getMasterPanel().isTextVisible() == false) {
			// do nothing
		} else if(getMasterPanel().isViewingMode() && getMasterPanel().isEdited() == false) {
			paintQuestionMark(g2d);
		} else {
			// only translate graphics origin to display the active component at the correct position 
			g2d.translate(insets.left, insets.top);
			// active component will be drawn by paintChildren(Graphics) from super.paint(Graphics) !
		}
		g2d.dispose();
	}
  
  /*
   * Paints a question mark for visualizing the flag <code>edited == false</code>.
   */
	private void paintQuestionMark(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(QUESTION_MARK_FONT);
		Dimension size = getPreferredQuestionMarkSize();
		float x = ((float) getWidth() - size.width) / 2.0f; 
		float y = (float) getMasterPanel().getBaseline() - getLocation().y;//((float) getHeight() - size.height) / 2.0f + size.height;
		// paint "?" under the baseline (ignore real y value)
		y = y + 3;
		if(getMasterPanel().isHighlighted()) // use highlight color if possible
			g2.setColor(getMasterPanel().getMaster().getDisplayProperties().getSelectionColor());
		else
			g2.setColor(Color.RED);
		g2.drawString("?", x, y);
		LOGGER.log(PRINT_BASELINE, "paint ?, baseline=" + y);
    if(LOGGER.isActiveCategory(DRAW_BASELINE)) {
    	// drawing debug baseline
    	g2.drawLine(0, (int) y, getWidth(), (int) y);
    }
	}
}
