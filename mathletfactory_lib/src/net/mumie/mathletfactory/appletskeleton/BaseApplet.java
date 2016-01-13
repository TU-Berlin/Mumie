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

package net.mumie.mathletfactory.appletskeleton;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntimeSupport;
import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.appletskeleton.util.SplitPanel;
import net.mumie.mathletfactory.appletskeleton.util.TabbedPanel;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogAction;
import net.mumie.mathletfactory.appletskeleton.util.dialog.MessageDialog;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.util.Graphics2DUtils;
import net.mumie.mathletfactory.util.ResourceManager;
import net.mumie.mathletfactory.util.Version;
import net.mumie.mathletfactory.util.animation.Animation;
import net.mumie.mathletfactory.util.exercise.MathletExerciseSupport;
import net.mumie.mathletfactory.util.exercise.MumieExerciseIF;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 * Basic abstract skeleton class for applets providing place for title, graphics
 * canvi, special buttons and dialogs. Since it is independant of the number and
 * dimension of the graphics canvi, it is the base for all other applet
 * templates.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public abstract class BaseApplet extends AbstractMathlet {

  /* ***********************************************************************
   *                               Versioning                              *
   ** **********************************************************************/
  public final static String CVS_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
  
  protected String CVS_REVISION, CVS_DATE;
  
  /* ***********************************************************************
   *               applet theme settings and constants                     *
   ** **********************************************************************/
  public static final String BIG_APPLET_THEME = "/resource/themes/bigApplet.theme";
  public static final String SMALL_APPLET_THEME = "/resource/themes/smallApplet.theme";
  public static final String TINY_APPLET_THEME = "/resource/themes/tinyApplet.theme";
  public static final int BIG_THEME = 0;
  public static final int SMALL_THEME = 1;
  public static final int TINY_THEME = 2;
//  private int m_currentAppletSize;
  public static final String DEFAULT_APPLET_SIZE_THEME = SMALL_APPLET_THEME;

  /* ***********************************************************************
   *                                 GUI stuff                             *
   ** **********************************************************************/
  
  /** Panels for the title, the infobar and the "real" content between them. */
  private JPanel m_titlePane, m_bottomPane, m_bottomCenterPane, m_bottomRightPane, m_bottomLeftPane;
  
  /** Label for the title. */
  private TextPanel m_titleLabel;

  /** Stores the background color of the title label during special case displaying */
  private Color m_oldTitleLabelBackground;

  // Buttons for the infoBar on the bottom
  private JButton m_resetButton, m_dynamicResetButton;
  private boolean m_isResetButtonAdded = false;
  private boolean m_isDynamicResetButtonAdded = false;

  /** ControlPanel for additional GUI components. */
  protected ControlPanel m_controlsPane = new ControlPanel();

  /** JPanel for the canvas/canvases. */
  protected JPanel m_canvasPane;

  protected SplitPanel m_centerSplitPanel;

  // tabbed panels
  protected TabbedPanel m_centerPane;
  protected TabbedPanel m_controlTabbedPanel;
  protected TabbedPanel m_canvasTabbedPanel;

  protected JPopupMenu popup_print;

  /* ***********************************************************************
   *                                 diverses                              *
   ** **********************************************************************/
  private MathletRuntimeSupport m_runtimeSupport;

  private String m_shortMathletName, m_fullMathletName, m_packageName;
  
  protected Animation m_animation;

  /** If this variable is set (e.g. by applet parameter) it overrides any other
   * title in {@link #setTitle}. */
  private String m_title;

  /** Field contaning the mathlet status. */
	private boolean m_isInitialized = false;

  // Logging
  private static ConsoleHandler consoleHandler = null;// old logger
  private final static MumieLogger m_logger = MumieLogger.getLogger(BaseApplet.class);
  private final static LogCategory RUNTIME_CATEGORY = m_logger.getCategory("mathlet.runtime");
  private final static LogCategory METHOD_ENTERING_CATEGORY = m_logger.getCategory("method-entering");

  /* ***********************************************************************
   *                           exercise stuff                              *
   ** **********************************************************************/
  /** Field holding the support for the homework mode. */
  protected MathletExerciseSupport m_exerciseSupport;
  
  /* ***********************************************************************
   *                       END OF FIELD DECLARATIONS                       *
   ** **********************************************************************/
  
  
  // initialize error handler
  static {
    try {
      consoleHandler = new ConsoleHandler();
      Logger.getLogger("").addHandler(consoleHandler);
      setLoggerLevel(Level.OFF);
    } catch (SecurityException e) {}
  }
  
  {
		// parse mathlet name
    try {
      m_fullMathletName = getClass().getName();
      m_shortMathletName = m_fullMathletName.substring(m_fullMathletName.lastIndexOf('.') + 1);    	
    } catch(IndexOutOfBoundsException e) {// applet is in default package
    	m_shortMathletName = m_fullMathletName;
    }

    // parse package name
  	try {
  		m_packageName = m_fullMathletName.substring(0, m_fullMathletName.lastIndexOf('.'));
  	} catch(IndexOutOfBoundsException e) {// applet is in default package
  		m_packageName = "";
  	}
  }

  public BaseApplet() {}

  /**
   * This method initializes this extension of a BaseApplet and must be called
   * first in each overwriting <code>init()</code> -method (i.e. by invoking
   * <code>super.init()</code> from inside the method with the same name).
   */
  public void init() {
    m_logger.log(METHOD_ENTERING_CATEGORY, "entering init()...");
  	// check if an old static runtime instance is still available
  	if(MathletRuntime.isInitialized()) {
    	m_logger.log(RUNTIME_CATEGORY, "old static runtime instance found! Unloading old instance...");
    	MathletRuntime.getRuntime().dispose(); // unload old runtime instance
  	} // proceed with initializing the new runtime instance
  	m_logger.log(RUNTIME_CATEGORY, "switching to execution phase I: initializing applet...");
  	// initialize GUI elements necessary for runtime
    m_titlePane = new JPanel();
    m_bottomPane = new JPanel();
    m_bottomCenterPane = new JPanel();
    m_bottomLeftPane = new JPanel();
    m_bottomRightPane = new JPanel();

  	// initializing the applet runtime support for execution and embedding mode
  	m_runtimeSupport = createRuntimeSupport(this);
  	m_runtimeSupport.initialize();
  	
  	// initializing the applet support for exercises
  	m_exerciseSupport = createExerciseSupport(this);

    // store CVS Version and Date
    if(CVS_REVISION != null) {
    	try {
    		m_runtimeSupport.setMathletVersion(new Version(CVS_REVISION));
    	} catch(Exception e) {
    		warn("Cannot parse CVS version: " + e);
    	}
    }
    if(CVS_DATE != null) {
	    	try {
	    		String dateString = CVS_DATE.substring(new String("!Date: ").length(), CVS_DATE.length()-2);
	    		SimpleDateFormat sdf = new SimpleDateFormat(CVS_DATE_FORMAT);
	    		m_runtimeSupport.setMathletDate(sdf.parse(dateString));
	    	} catch(Exception e) {
	    		warn("Cannot parse CVS date: " + e);
	    	}
    }
        
    /*
     * creating gui components
     */
    ((JComponent) getMyContentPane()).setBorder(new LineBorder(Color.GRAY));
    m_centerPane = new TabbedPanel();
    m_controlTabbedPanel = new TabbedPanel();
    m_canvasTabbedPanel = new TabbedPanel();

    m_titlePane = new JPanel(new BorderLayout());
    m_canvasPane = new JPanel(new BorderLayout());
    m_centerPane.setBorder(BorderFactory
        .createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5),
            BorderFactory.createRaisedBevelBorder()));
    m_bottomPane = new JPanel();
    m_resetButton = new ResetButton();
    m_dynamicResetButton = new DynamicResetButton();
    m_bottomRightPane = new JPanel();
    m_titleLabel = new TextPanel();
    m_titleLabel.setFont(MumieTheme.getTitleFont());

    m_controlsPane = new ControlPanel();
    m_controlTabbedPanel.add(m_controlsPane);

    // info pane
    m_bottomPane.setLayout(new BorderLayout());
    m_bottomPane.add(m_bottomLeftPane, BorderLayout.WEST);
    m_bottomPane.add(m_bottomRightPane, BorderLayout.EAST);
    m_bottomPane.add(m_bottomCenterPane, BorderLayout.CENTER);

    // title pane
    m_titlePane.add(m_titleLabel, BorderLayout.CENTER);

    m_resetButton.setToolTipText(getMessage("reset_tooltip"));

    // setting up the center pane
    getAppletContentPane().setLayout(new BorderLayout());

    JPanel titleThemePanel = new JPanel(new BorderLayout());
    JLabel iconLabel = new JLabel(ResourceManager.getIcon("/resource/icon/mumie_icon_small.png"));
    iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    titleThemePanel.add(iconLabel, BorderLayout.WEST);
    titleThemePanel.add(m_titlePane, BorderLayout.CENTER);
    
    // add the 3 parts to the applet/frame
    getAppletContentPane().add(titleThemePanel, BorderLayout.NORTH);
    getAppletContentPane().add(m_centerPane, BorderLayout.CENTER);
    getAppletContentPane().add(m_bottomPane, BorderLayout.SOUTH);

    // ensure all components that had been created before "init()" have the right theme settings
    updateMathletUI();
    
    // set the flag
  	m_runtimeSupport.setInitialized();
  }
  
  protected MathletRuntimeSupport createRuntimeSupport(MathletContext mathlet) {
  	return new MathletRuntimeSupport(mathlet);
  }
  
  protected MathletExerciseSupport createExerciseSupport(MathletContext mathlet) {
  	return new MathletExerciseSupport(mathlet);
  }
  
  public String getMessage(String key) {
  	return getMathletRuntime().getMessage(key);
  }

  public String getMessage(String key, Object[] params) {
  	return getMathletRuntime().getMessage(key, params);
  }

  /**
   * Gets the the message text for the given key corresponding to the language.
   * @deprecated use the method {@link #getMessage(String)} instead
   */
  public String getString(String key) {
  	return getMessage(key);
  }
  
  public String getParameter(String name) {
  	return getMathletRuntime().getParameter(name);
  }
  
  public String readParameter(String name) {
  	return super.getParameter(name);
  }

  /**
   * Returns the parameter with the given name if it exists or <code>defaultValue</code> otherwise.
   * The <code>name</code> argument is case insensitive.
   */
  public String getParameter(String name, String defaultValue) {
  	if(getParameter(name) != null)
  		return getParameter(name);
  	else
  		return defaultValue;
  }
  
  public String[][] getParameterInfo() {
  	return getMathletRuntime().getParameterInfo();
  }
  
  public Locale getLocale() {
  	if(getRuntimeSupport() == null)
  		return super.getLocale();
  	else
  		return getMathletRuntime().getLocale();
  }

//  /**
//   * Sets the theme with given path containing size theme properties.  
//   */
//  public void setAppletSizeTheme(int theme) {
//    switch(theme) {
//    case BIG_THEME:
//      MumieTheme.loadThemeFile(BIG_APPLET_THEME);
//      break;
//    case SMALL_THEME:
//      MumieTheme.loadThemeFile(SMALL_APPLET_THEME);
//      break;
//    case TINY_THEME:
//      MumieTheme.loadThemeFile(TINY_APPLET_THEME);
//      break;
//    }
//    updateTheme();
//    applyAppletSizeTheme();
//  }

//  /**
//   * Sets a bigger applet size theme (if the current is not the biggest size theme).
//   */
//  public void setBiggerAppletSize() {
//    if(m_currentAppletSize > BIG_THEME) {
//      m_currentAppletSize--;
//      setAppletSizeTheme(m_currentAppletSize);
//    }
//  }
//
//  /**
//   * Sets a smaller applet size theme (if the current is not the smallest size theme).
//   */
//  public void setSmallerAppletSize() {
//    if(m_currentAppletSize < TINY_THEME) {
//      m_currentAppletSize++;
//      setAppletSizeTheme(m_currentAppletSize);
//    }
//  }

//  /**
//   * Applies current applet size settings.
//   */
//  protected void applyAppletSizeTheme() {
//    if(m_titlePane == null || m_bottomPane == null)
//      return;
//    boolean showTitle = MumieTheme.getThemeProperty("BaseApplet.title.visible", "true").equals(
//    "true");
//    m_titleLabel.setVisible(showTitle);
//    boolean showButtons = MumieTheme.getThemeProperty("BaseApplet.bottomButtons.visible", "true")
//        .equals("true");
//    m_bottomPane.setVisible(showButtons);
//    m_titleLabel.setTextFont((Font)MumieTheme.getThemeProperty("BaseApplet.title.font"));
//    getMyContentPane().validate();
//    // provoques runtime error in eclipse on mac with java 1.5
////    SwingUtilities.updateComponentTreeUI(getMyContentPane());
//  }

  public void setTitleVisible(boolean v) {
    m_titlePane.setVisible(v);
  }

  /**
   * Sets whether the bottom button bar should be visible or not.
   */
  public void setBottomButtonsVisible(boolean v) {
    m_bottomPane.setVisible(v);
  }
  
  public void showAppletFrame() {
  	if(getExerciseSupport() != null)
  		getExerciseSupport().showAppletFrame();
  }
  
  public void save() {
  	if(getExerciseSupport() != null)
  		getExerciseSupport().save();
  }
  
  protected void warn(String s) {
  	System.out.println("Warning: " + s);
  }

  public void setTitle(String title) {
    if (title == null) {
      setTitle("");
      return;
    }
  	String message = getMessage(title);
  	if(message == null)
  		message = title;
    m_title = message;
    m_titleLabel.setText(message);
  }
  
  public void setTitle(Component c) {
  	m_titlePane.removeAll();
  	if(c != null)
  		m_titlePane.add(c, BorderLayout.CENTER);
  	else
  		m_titlePane.add(m_titleLabel, BorderLayout.CENTER);
  }

  /**
   * Convenience method for <code>getBottomPane().add(animation.getButtonPanel())</code>.
   */
  public void setAnimationPanel(Animation animation) {
    m_animation = animation;
    m_bottomCenterPane.add(animation.getButtonPanel());
  }

  /**
   * Removes the animation's button panel from the bottom pane.
   */
  public void removeAnimationPanel() {
    // no error is thrown if no such a panel has been added.
  	m_bottomCenterPane.remove(m_animation.getButtonPanel());
  }
  
  /**
   * Updates the Look&Feel settings of all components contained in this mathlet.
   * This method is intended for reinitializing the UI settings of components which 
   * were initialized before the current Look&Feel was set.
   */
  public void updateMathletUI() {
  	SwingUtilities.updateComponentTreeUI(getMyContentPane());
  }
  
  public JFrame getAppletFrame() {
  	return m_runtimeSupport.getContentFrame();
  }
  
  /**
   * Returns if this applet is shown in a window and if this window is visible.
   * @deprecated should not be used
   */
  public boolean isFrameVisible() {
  	return m_runtimeSupport.getContentFrame() != null && m_runtimeSupport.getContentFrame().isVisible();
  }
  
  public MathletExerciseSupport getExerciseSupport() {
  	return m_exerciseSupport;
  }
  
  public MathletRuntimeSupport getRuntimeSupport() {
  	return m_runtimeSupport;
  }
  
  public MathletRuntime getMathletRuntime() {
  	return m_runtimeSupport.getRuntime();
  }
  
  /**
   * Returns if the parameter <code>homeworkMode</code> is set to true,
   * the necessary libraries for the homework mode are available and
   * the exercise initialisation has reported no errors.
   */
  public boolean isHomeworkMode() {
  	return m_exerciseSupport != null && m_exerciseSupport.isHomeworkMode();
  }
  
  public void reportError(Throwable error) {
    m_logger.log(METHOD_ENTERING_CATEGORY, "entering reportError(Throwable)...");
  	if(m_runtimeSupport == null || isDebugMode()) {
  		throw new RuntimeException(error);
  	}
  	m_runtimeSupport.reportError(error);
  }
  
  public boolean hasErrors() {
  	return m_runtimeSupport != null && m_runtimeSupport.hasErrors();
  }
  
  public boolean isDebugMode() {
  	return m_exerciseSupport != null && m_exerciseSupport.isDebugMode();
  }
  
  public boolean isOnlineMode() {
  	return m_exerciseSupport != null && m_exerciseSupport.isRemoteMode();
  }
  
  public boolean isOfflineMode() {
  	return ! isOnlineMode();
  }
  
  public boolean isPreviewMode() {
  	return m_exerciseSupport != null && m_exerciseSupport.isPreviewMode();
  }

  public static void setLoggerLevel(java.util.logging.Level level) {
    try {
      Logger.getLogger("").setLevel(level);
      if (consoleHandler != null)
        consoleHandler.setLevel(level);
    } catch (Exception e) {
      System.out.println("not able to set log level.");
    }
  }

  /**
   * Returns whether this Applet was started as an applet or an application.
   * Should only be invoked after super.init().
   */
  public boolean wasStartedAsApplet() {
  	return getMathletRuntime().isAppletExecutionMode();
  }
  
  /**
   * Adds a reset button at the bottom right corner of the applet that calls the appropriate reset methods
   * of the applet.
   * 
   * @see #resetDemo()
   * @see #resetTraining()
   * @see MumieExerciseIF#clearSubtask()
   */
  public void addDynamicResetButton() {
    if (!m_isDynamicResetButtonAdded) {
      m_bottomRightPane.add(m_dynamicResetButton);
      m_isDynamicResetButtonAdded = true;
    }
  }

  public void addResetButton() {
    if (!m_isResetButtonAdded) {
      m_bottomRightPane.add(m_resetButton);
      m_isResetButtonAdded = true;
    }
  }

  public TabbedPanel getControlTabbedPanel() {
    return m_controlTabbedPanel;
  }

  /**
   * @return Returns the canvasTabbedPanel.
   */
  public TabbedPanel getCanvasTabbedPanel() {
    return m_canvasTabbedPanel;
  }

  /**
   * @return Returns the centerTabbedPanel.
   */
  public TabbedPanel getCenterTabbedPanel() {
    return m_centerPane;
  }

  /**
   * Returns the (singleton) instance of a <code>ResetButton</code> which was
   * added to the bottom of the applet.
   */
  public final JButton getResetButton() {
    return m_resetButton;
  }

  /** Adds a button that allows screen shots. */
  protected void addScreenShotButton() {
    if (!wasStartedAsApplet())
      m_bottomRightPane.add(new ScreenshotButton());
  }

  /**
   * This method should be implemented in "real life applets" inheriting from
   * <code>
   * BaseApplet</code>. The generic <code>ResetButton</code> (see
   * {@link #addResetButton()}) will call this method when it is performing
   * it's action.
   */
  public abstract void reset();
  
  /**
   * This method should be implemented in "real life applets" inheriting from
   * <code>
   * BaseApplet</code>. The generic <code>ResetButton</code> (see
   * {@link #addResetButton()}) will call this method when it is performing
   * it's action.
   */
  public void resetDemo() {}
  
  public void resetTraining() {}

  /**
   * @see net.mumie.mathletfactory.util.exercise.SelectableDataIF#selectData(String)
   */
  public synchronized void selectData(String path) {
  	m_exerciseSupport.selectData(path);
  }

  public synchronized void selectSubtask(int subtaskNr) {
  	m_exerciseSupport.selectSubtask(subtaskNr);
  }
  
  protected class ResetButton extends JButton implements ActionListener {
    public ResetButton() {
      super(getMessage("Reset"));
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
      reset();
      getControlPanel().resetScrollingView();
      m_centerPane.updateUI();
    }
  }
  
  protected class DynamicResetButton extends JButton implements ActionListener {
    public DynamicResetButton() {
      super(getMessage("Reset"));
      addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
    	TabbedPanel tp = null;
    	if(BaseApplet.this instanceof NoCanvasApplet && getControlTabbedPanel().hasMultipleTabs()) {
    		tp = getControlTabbedPanel();
    	} else if(getCenterTabbedPanel().getTabbedPane() != null && getCenterTabbedPanel().hasMultipleTabs()) {
    		tp = getCenterTabbedPanel();
    	} else
    		return;
    	String tabName = tp.getTabbedPane().getTitleAt(tp.getSelectedIndex());
    	if(tabName.indexOf("Demo") > -1) {
    		resetDemo();
    	} else if(tabName.indexOf("Training") > -1) {
    		resetTraining();
    	} else if(tabName.indexOf("Übung") > -1 || tabName.indexOf("&Uuml;bung") > -1 || tabName.indexOf("Uebung") > -1
    			|| tabName.indexOf("Aufgabe") > -1 || tabName.indexOf("Problem") > -1) {
    		MumieExerciseIF exApplet = (MumieExerciseIF) BaseApplet.this;
    		if(showConfirmDialog(getMessage("exercise.ASK_CLEAR_ANSWERS"), ""))
    			exApplet.clearSubtask();
    	}
      m_centerPane.updateUI();
    }
  }

  /**
   * Returns the current content pane depending on BaseApplet parent (Applet or
   * Frame).
   */
  public Container getMyContentPane() {
  	if(m_runtimeSupport != null)
  		return m_runtimeSupport.getContentPane();
  	else
  		return null;
  }
  
  public Container getAppletContentPane() {
  	return getMyContentPane();
  }
  
  public Class getAppletClass() {
  	return getClass();
  }
  
  public String getShortName() {
  	return m_shortMathletName;
  }
  
  public String getPackageName() {
  	return m_packageName;
  }
  
  public JApplet getJApplet() {
  	return this;
  }
  
  /**
   * Returns the center pane containing the canvas panel and the controlpanel.
   */
  public JPanel getCenterPane() {
    return m_centerPane;
  }

  /**
   * Returns the canvas-pane containing all canvases.
   */
  public JPanel getCanvasPane() {
    return m_canvasPane;
  }

  /**
   * Returns the already present ControlPanel which can be used to manage
   * graphical control elements in this applet.
   */
  public ControlPanel getControlPanel() {
    return m_controlsPane;
  }

  /** Sets the height of the ControlPanel.
   * Setting -1 will restore the default (automatic) size.
   */
  public void setControlPanelHeight(int height) {
  	m_controlsPane.setHeight(height);
  }

  /** Sets the width of the ControlPanel.
   * Setting -1 will restore the default (automatic) size.
   */
  public void setControlPanelWidth(int width) {
  	m_controlsPane.setWidth(width);
  }

  //
  // Methods declared in ControlPanel
  //

  /**
   * Append a MMObjectIF as container content to the actual line in the
   * controlPanel
   */
  public void addMMObjectAsContainerContent(MMObjectIF obj, int transformType) {
    obj.addSpecialCaseListener(this);
    m_controlsPane.add(obj.getAsContainerContent(transformType));
  }

  /**
   * Append a MMObjectIF as container content to the actual line in the
   * controlPanel
   */
  public void addMMObjectAsContainerContent(MMObjectIF obj) {
    addMMObjectAsContainerContent(obj, obj.getDefaultTransformTypeAsContainer());
  }

  private boolean removeMMObjectInContainer(Container container, MMObjectIF obj) {
    for (int i = container.getComponentCount() - 1; i > -1; i--) {
      Component comp = container.getComponent(i);

      if (comp instanceof MMPanel) {
        MMPanel panel = (MMPanel) comp;
        if (panel.getMaster() == obj) {
          if (panel.getParent() != null) {
            panel.getParent().remove(panel);
            return true;
          }
        }
      }

      if (comp instanceof Container) {
        if (removeMMObjectInContainer((Container) comp, obj))
          return true;
      }
    }
    return false;
  }

  /**
   * removes <code>obj</code> from the applet
   *
   * @param obj
   */
  public void removeMMObject(MMObjectIF obj) {
    removeMMObjectInContainer(getContentPane(), obj);
    obj.removeSpecialCaseListener(this);
  }

  /** Append a component to the actual line in the controlPanel. */
  public void addControl(JComponent c) {
    m_controlsPane.add(c);
  }

  /** Append a component to the actual line in the controlPanel. */
  public void addControl(Component c) {
    m_controlsPane.add(c);
  }

  public void addControl(MMObjectIF obj) {
    addControl(obj.getAsContainerContent());
  }

  public void addControl(MMObjectIF obj, int transformType) {
    addControl(obj.getAsContainerContent(transformType));
  }
  
  public void addControl(Component c, int location) {
  	switch(location) {
  	case CONTROL_PANE:
  		addControl(c);
  		break;
  	case BOTTOM_LEFT_PANE:
  		m_bottomLeftPane.add(c);
  		break;
  	case BOTTOM_CENTER_PANE:
  		m_bottomCenterPane.add(c);
  		break;
  	case BOTTOM_RIGHT_PANE:
  		m_bottomRightPane.add(c);
  		break;
  	case TOP_PANE:
  		setTitle(c);
  		break;
		default:
  		throw new IllegalArgumentException("Unknown location for adding control: " + location);	
  	}
  }
  
  public void removeControl(Component c, int location) {
  	switch(location) {
  	case CONTROL_PANE:
  		m_controlsPane.remove(c);
  		break;
  	case BOTTOM_LEFT_PANE:
  		m_bottomLeftPane.remove(c);
  		break;
  	case BOTTOM_CENTER_PANE:
  		m_bottomCenterPane.remove(c);
  		break;
  	case BOTTOM_RIGHT_PANE:
  		m_bottomRightPane.remove(c);
  		break;
  	case TOP_PANE:
  		setTitle((Component) null);
  		break;
		default:
  		throw new IllegalArgumentException("Unknown location for removing control: " + location);	
  	}
  }

  /** Append a label to the actual line in the controlPanel. */
  public void addText(String labelText) {
  	String message = getMessage(labelText);
  	if(message == null)
  		message = labelText;
    m_controlsPane.addText(message);
  }

  /**
   * Append a label with the specified text color to the actual line in the
   * controlPanel.
   */
  public void addText(String labelText, Color color) {
  	String message = getMessage(labelText);
  	if(message == null)
  		message = labelText;
    m_controlsPane.addText(message, color);
  }

  /**
   * Append a label with the specified font to the actual line in the
   * controlPanel.
   */
  public void addText(String text, Font aFont) {
  	String message = getMessage(text);
  	if(message == null)
  		message = text;
    m_controlsPane.addText(message, aFont);
  }

  /**
   * Append a label with the specified font and text color to the actual line in
   * the controlPanel.
   */
  public void addText(String text, Font aFont, Color color) {
  	String message = getMessage(text);
  	if(message == null)
  		message = text;
    m_controlsPane.addText(message, aFont, color);
  }

  public TextPanel addVector(String vectorName) {
    return m_controlsPane.addVector(vectorName);
  }
  
  public TextPanel addVector(String vectorName, int yShift) {
	    return m_controlsPane.addVector(vectorName, yShift);
  }

  /**
   * Do <code>nrOfLines</code> line breaks, i.e. jump <code>nrOfLines</code>
   * lines downwards. Empty lines takes a serveral amount of height (
   * <code>LINE_HEIGHT</code> pixels).
   */
  public void insertLineBreaks(int nrOfLines) {
    m_controlsPane.insertLineBreaks(nrOfLines);
  }

  /** Do a line break, i.e. jump to the next line. */
  public void insertLineBreak() {
    m_controlsPane.insertLineBreak();
  }

  /** Insert a tab, i.e. move to the right. */
  public void insertTab() {
    m_controlsPane.insertTab();
  }

  /** Insert <code>nrOfTabs</code> tabs, i.e. move to the right. */
  public void insertTabs(int nrOfTabs) {
    m_controlsPane.insertTabs(nrOfTabs);
  }

  /** Insert free space of <code>nrOfPix</code> pixels to the right. */
  public void insertHSpace(int nrOfPix) {
    m_controlsPane.insertHSpace(nrOfPix);
  }

  /** Insert free space of <code>nrOfPix</code> pixels downwards. */
  public void insertVSpace(int nrOfPix) {
    m_controlsPane.insertVSpace(nrOfPix);
  }

  /**
   * Convenience method for
   * <code>setLineAlignment(MMControlPanel.LEFT_ALIGNMENT)</code>.
   */
  public void setLeftAlignment() {
    m_controlsPane.setLeftAlignment();
  }

  /**
   * Convenience method for
   * <code>setLineAlignment(MMControlPanel.RIGHT_ALIGNMENT)</code>.
   */
  public void setRightAlignment() {
    m_controlsPane.setRightAlignment();
  }

  /**
   * Convenience method for
   * <code>setLineAlignment(MMControlPanel.CENTER_ALIGNMENT)</code>.
   */
  public void setCenterAlignment() {
    m_controlsPane.setCenterAlignment();
  }

  /** By default all special cases are displayed in a popup window. */
  public void displaySpecialCase(SpecialCaseEvent e) {
    m_titleLabel.setText(e.toString());
    m_oldTitleLabelBackground = m_titleLabel.getBackground();
    m_titleLabel.setBackground(new Color(255, 255, 200));
    new Timer().schedule(new TimerTask() {
      public void run() {
        m_titleLabel.setText(m_title);
        m_titleLabel.setBackground(m_oldTitleLabelBackground);
        m_oldTitleLabelBackground = null;
      }
    }, 2000);
  }
  
  /**
   * Brings up a dialog for an error message.
   */
  public void showErrorDialog(Object message) {
  	showMessageDialog(message, getMessage("error"), JOptionPane.ERROR_MESSAGE);
  }
  
  /**
   * Brings up a dialog for an information message with default title.
   */
  public void showMessageDialog(Object message) {
  	showMessageDialog(message, null);
  }
  
	public void showMessageDialog(Object message, String messageID) {
  	new MessageDialog(this, message, messageID);
	}

  /**
   * Brings up a dialog for a message with given title and message type.
   */
  public void showMessageDialog(Object message, String title, int messageType) {
  	JOptionPane.showMessageDialog(getMyContentPane(), message, title, messageType); 
  }
  
  /**
   * Brings up a confirmation dialog for querying a YES/NO question and returns the answer as a boolean.
   */
  public boolean showConfirmDialog(Object message, String title) {
  	int result = JOptionPane.showConfirmDialog(getMyContentPane(), message, title, JOptionPane.YES_NO_OPTION);
  	return result == JOptionPane.YES_OPTION ? true : false;
  }
  
	public DialogAction showDialog(int dialogID, Object[] params) {
		return getMathletRuntime().showDialog(dialogID, params);
	}
	
  /**
   * Returns if this mathlet has been fully initialized.
   */
  public boolean isInitialized() {
  	return m_isInitialized;
  }
  
  /**
   * Called by the browser or applet viewer after the execution of the {@link #init()} method.
   * Implementors must call this method (via <code>super.start()</code>) at the end of their 
   * {@link #start()} method implementation in order to set up the <i>initialized</i> flag correctly.
   */
  public void start() {
    m_logger.log(METHOD_ENTERING_CATEGORY, "entering start()...");
    m_isInitialized = true;
  	m_logger.log(RUNTIME_CATEGORY, "switching to execution phase II: applet is running...");
  }
  
  public void stop() {
    m_logger.log(METHOD_ENTERING_CATEGORY, "entering stop()...");
  	m_logger.log(RUNTIME_CATEGORY, "switching to execution phase III: destroying applet...");
  }

  /**
   * Removes all components from this applet and sets all global fields to <code>null</code>.
   * This method is intended to release all memory allocated by this applet.
   */
  public void destroy() {
    m_logger.log(METHOD_ENTERING_CATEGORY, "entering destroy()...");
    
    // removing all components
    unload(m_controlsPane);
    unload(m_centerPane);
    unload(m_controlTabbedPanel);
    unload(m_canvasTabbedPanel);
    unload(m_canvasPane);
    unload(m_centerSplitPanel);
    getMyContentPane().removeAll();
    m_runtimeSupport.dispose();
    if(m_animation != null) {
    	m_animation.stop();
    }
    unload(popup_print);
        
    // clear all internal fields in all applet classes
    clearInternalFields();
    System.gc();
  }
  
  public void clearInternalFields() {
    try { // silent failing
      Field[] fields = getClass().getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        try {
          if(!fields[i].getType().isPrimitive() && !Modifier.isFinal(fields[i].getModifiers()))
            fields[i].set(this, null);
        } catch (Exception e) {} // called upon private fields in super- or sub-classes
      }
    } catch(Exception e) {}
  }
  
  /**
   * Releases the references from <code>list</code> to all of its entries by setting <code>null</code> to every postion inside the array.
   * If one object in the array/list is an instance of <code>java.awt.Container</code>, all of its subcomponents
   * will be removed from it. 
   */
  protected void unload(Object[] list) {
  	if(list == null)
  		return;
  	for(int i = 0; i < list.length; i++) {
  		Object o = list[i];
    	if(o != null) {
    		if(o instanceof Container)
    			((Container) o).removeAll();
    		list[i] = null;
    	}
  	}
  }
  
  /**
   * Removes all subcomponents of <code>c</code> if it is not <code>null</code>.
   */
  protected void unload(Container c) {
  	if(c != null)
  		c.removeAll();
  }

  /**
   * This class represents a button which highlights and underlines its content
   * when moving over with the mouse pointer.
   *
   * @author Gronau
   */
  class LinkButton extends JButton {

    private Color m_oldColor;
    private final Color HIGHLIGHT_COLOR = Color.BLUE;

    private String m_text;

    LinkButton(String text) {
      super("<html> " + text + " </html>");
      m_text = text;
      setContentAreaFilled(false);
      addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          m_oldColor = getForeground();
          setForeground(HIGHLIGHT_COLOR);
          setText("<html><u>" + m_text + "</u></html>");
        }

        public void mouseExited(MouseEvent e) {
          setForeground(m_oldColor);
          setText("<html>" + m_text + "</html>");
        }
      });
      setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
  }


  

  public static class ScreenShotFrame extends JFrame {
    private BufferedImage image;

//    private JButton btnPrint;
//
//    private JButton btnSave;
//
//    private JButton btnClose;

    public ScreenShotFrame(String title, BufferedImage screenShot) {
      super(title);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      JToolBar toolBar = new JToolBar();
      toolBar.setFloatable(false);
      toolBar.add(new AbstractAction(MathletRuntime.getRuntime().getMessage("Print")) {
        public void actionPerformed(ActionEvent event) {
          PrinterJob printJob = PrinterJob.getPrinterJob();
          printJob.setPrintable(new Printable() {
            public int print(Graphics g, PageFormat pf, int pi)
                throws PrinterException {
              if (pi >= 1) {
                return Printable.NO_SUCH_PAGE;
              }

              double x = pf.getImageableX();
              double y = pf.getImageableY();
              double scaleX = pf.getImageableWidth() / image.getWidth();
              double scaleY = pf.getImageableHeight() / image.getHeight();
              double scale = Math.min(scaleX, scaleY);
              Graphics2D g2d = (Graphics2D) g;
              if (scale < 1) {
                x *= scale;
                y *= scale;
                g2d.scale(scale, scale);
              }
              g2d.drawImage(image, (int) x, (int) y, ScreenShotFrame.this);
              return Printable.PAGE_EXISTS;
            }
          });
          if (printJob.printDialog()) {
            try {
              printJob.print();
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      toolBar.add(new AbstractAction(MathletRuntime.getRuntime().getMessage("Save")) {
        public void actionPerformed(ActionEvent event) {
          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
              return (f.isDirectory())
                  || (f.isFile() && f.getName().toLowerCase().endsWith(".png"));
            }

            public String getDescription() {
              return "Portable Network Graphics - PNG (*.png)";
            }
          });
          if (fileChooser.showSaveDialog(ScreenShotFrame.this) == JFileChooser.APPROVE_OPTION) {
            try {
              ImageIO.write(image, "PNG", fileChooser.getSelectedFile());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      });
      toolBar.add(new AbstractAction(MathletRuntime.getRuntime().getMessage("Close")) {
        public void actionPerformed(ActionEvent event) {
          dispose();
        }
      });

      image = screenShot;
      JLabel lblImage = new JLabel(new ImageIcon(image));
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(toolBar, BorderLayout.NORTH);
      getContentPane().add(new JScrollPane(lblImage), BorderLayout.CENTER);
      pack();
      setVisible(true);
    }
  }

  protected static void showScreenshotFrom(final Component component) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          new ScreenShotFrame(MathletRuntime.getRuntime().getMessage("Screenshot"),
              Graphics2DUtils.acquireScreenshot(component));
        } catch (AWTException e) {
          JOptionPane.showMessageDialog(component, e.getMessage(),
          		MathletRuntime.getRuntime().getMessage("Error"), JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }

  protected class ScreenshotButton extends JButton implements ActionListener {
    public ScreenshotButton() {
      super(MathletRuntime.getRuntime().getMessage("Screenshot"));
      addActionListener(this);
      popup_print = new JPopupMenu();
      JMenuItem miAppletShot = new JMenuItem(MathletRuntime.getRuntime().getMessage("Screenshot_applet"));
      miAppletShot.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          popup_print.setVisible(false);
          Window wnd = SwingUtilities.windowForComponent(ScreenshotButton.this);
          showScreenshotFrom(wnd);
        }
      });
      popup_print.add(miAppletShot);
    }

    public void actionPerformed(ActionEvent event) {
      popup_print.show(getParent(), getX(), getY() + getHeight());
    }
  }
}
