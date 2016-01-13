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

package net.mumie.mathletfactory.appletskeleton.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.display.util.StyledTextButton;
import net.mumie.mathletfactory.util.BasicApplicationFrame;
import net.mumie.mathletfactory.util.Version;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
//import net.mumie.mathletfactory.util.plugin.PluginManager;

/**
 * This class acts as a support (helper) class for mathlet contexts.
 * It handles the EXECUTION and EMBEDDING mode, the language settings, quality feedback, 
 * the help system and the plugin and version managment.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MathletRuntimeSupport implements MathletConstants {
	
	/** Constant for the APPLET embedding mode. */
	public final static int APPLET_EMBEDDING_MODE = 0;

	/** Constant for the BUTTON embedding mode. */
	public final static int BUTTON_EMBEDDING_MODE = 1;	

	/** Constant for the LABEL embedding mode. */
	public final static int LABEL_EMBEDDING_MODE = 2;	
	
	private int m_embeddingMode = -1;
	
	private boolean m_isInitialized = false;
	
	private MathletContext m_mathlet;
	
	private MathletRuntime m_runtime;
	
	private JLabel m_statusLabel;
	
//	private MumieHelp m_javaHelp;
	
  // Logging
  private final static MumieLogger m_logger = MumieLogger.getLogger(MathletRuntimeSupport.class);
  private final static LogCategory RUNTIME_CATEGORY = m_logger.getCategory("mathlet.runtime");

  /** Field for displaying the applet in a different frame. */
  protected BasicApplicationFrame m_contentFrame = null;

  /**
   * Creates a new instance of this support class but does NOT initialize it.
   * @see #initialize()
   */
  public MathletRuntimeSupport(MathletContext mathlet) {
		m_mathlet = mathlet;
	}
  
  /**
   * Initializes this support class.
   * A call to this method is needed in order to properly initialize the support.
   */
  public void initialize() {
  	try {
			initializeRuntime();
			initializeEmbeddingMode();
  	} catch(Throwable t) {
  		reportError(t);
  	}
  	setLabelText();
  	if(hasErrors())
  		handleErrors();
  }
	
	protected void initializeRuntime() {
		m_runtime = MathletRuntime.createStaticRuntime(getContext());
	}
	
	public MathletContext getContext() {
		return m_mathlet;
	}
	
	public MathletRuntime getRuntime() {
		return m_runtime;
	}
	
	/**
	 * Returns whether the EMBEDDING mode was set to APPLET.
	 */
	public boolean isAppletEmbeddingMode() {
		return m_embeddingMode == APPLET_EMBEDDING_MODE;
	}
	
	/**
	 * Returns whether the EMBEDDING mode was set to BUTTON.
	 */
	public boolean isButtonEmbeddingMode() {
		return m_embeddingMode == BUTTON_EMBEDDING_MODE;
	}
	
	/**
	 * Returns whether the EMBEDDING mode was set to LABEL.
	 */
	public boolean isLabelEmbeddingMode() {
		return m_embeddingMode == LABEL_EMBEDDING_MODE;
	}
	
	/**
	 * Returns the EMBEDDING mode as an integer.
	 */
	public int getEmbeddingMode() {
		return m_embeddingMode;
	}
	
  /**
   * Initializes the EMBEDDING mode.
   */
	protected void initializeEmbeddingMode() {
		String embeddingModeParam = getRuntime().getParameter("embeddingMode", "applet");
		if(embeddingModeParam.equals("applet")) {
			m_embeddingMode = APPLET_EMBEDDING_MODE;
    	m_logger.log(RUNTIME_CATEGORY, "initializing applet embedding mode");
		}
		else if(embeddingModeParam.equals("button")) {
			m_embeddingMode = BUTTON_EMBEDDING_MODE;
    	m_logger.log(RUNTIME_CATEGORY, "initializing button embedding mode");
			initializeContentFrame();
			String buttonText = getRuntime().getParameter("buttonText", getContext().getMessage("exercise.edit"));
			JButton startButton = new StyledTextButton(buttonText);
			initializeEmbeddingView(startButton);
			startButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          showContentFrame();
        }
      });
		}
		else if(embeddingModeParam.equals("label")) {
			m_embeddingMode = LABEL_EMBEDDING_MODE;
    	m_logger.log(RUNTIME_CATEGORY, "initializing label embedding mode");
			initializeContentFrame();
			m_statusLabel = new JLabel("...", JLabel.CENTER);
			m_statusLabel.setBackground(Color.WHITE);
			m_statusLabel.setOpaque(true);
			initializeEmbeddingView(m_statusLabel);
		}
	}
	
  /**
   * Initializes the view component for the EMBEDDING mode.
   */
	protected void initializeEmbeddingView(Component comp) {
		m_mathlet.getJApplet().getContentPane().setLayout(new BorderLayout());
		m_mathlet.getJApplet().getContentPane().add(comp, BorderLayout.CENTER);
	}
	
  /**
   * Initializes the mathlet frame for the EMBEDDING mode.
   */
	private void initializeContentFrame() {
		m_contentFrame = new BasicApplicationFrame("Mumie Mathlet");
    m_contentFrame.removeWindowMonitor();
	}
	
	/**
	 * Returns the content frame of this mathlet (if available). May be <code>null</code>.
	 */
  public JFrame getContentFrame() {
  	return m_contentFrame;
  }
  
  /**
   * Returns the content pane of this mathlet. This can be the content pane of {@link javax.swing.JApplet} or
   * {@link JFrame}.
   */
  public Container getContentPane() {
  	if(isButtonEmbeddingMode() || isLabelEmbeddingMode())
  		return m_contentFrame.getContentPane();
  	else
  		return m_mathlet.getJApplet().getContentPane();
  }
  
  /**
   * Disposes of any resources hold by this support.
   */
  public void dispose() {
    if(m_contentFrame != null) {
    	m_contentFrame.setVisible(false);
    	m_contentFrame.dispose();
    	m_contentFrame = null;
    }
    getRuntime().dispose();
    m_runtime = null;
  }
  
  /**
   * Displays the mathlet frame (if available).
   */
  public void showContentFrame() {
    if (m_contentFrame != null && !m_contentFrame.isVisible()) {
	  	String title = getContext().getParameter("title");
	  	if (title != null)
	  		getContext().setTitle(title);
      int width = 600, height = 600;
  	  try {
  			String widthStr = m_mathlet.getParameter("appletWidth");
  			if (widthStr != null)
  			  width = Integer.parseInt(widthStr);
  			String heightStr = m_mathlet.getParameter("appletHeight");
  			if (heightStr != null)
  			  height = Integer.parseInt(heightStr);
  		} catch (Exception e) {
  			System.err.println("Cannot read applet size from parameters: " + e);
  		}
  		m_contentFrame.pack();
  		m_contentFrame.setSize(new Dimension(width, height));
      m_contentFrame.setVisible(true);
    } else if( m_contentFrame != null && m_contentFrame.isShowing())
      m_contentFrame.toFront();
  }
  
  /**
   * Returns whether this support was successfully initialized.
   */
  public boolean isInitialized() {
  	return m_isInitialized;
  }
  
  /**
   * Sets that this support was successfully initialized.
   */
  public void setInitialized() {
  	m_isInitialized = true;
  	setLabelText();
  	if(hasErrors())
  		handleErrors();
  }
  
  protected void handleErrors() {
  	getRuntime().getErrorHandler().handleFirstError();
  }
  
  /**
   * Sets the mathlet label's text if EMBEDDING mode is set to LABEL.
   */
  private void setLabelText() {
  	if(isLabelEmbeddingMode()) {
  		if(isInitialized()) {
	  		if(hasErrors())
	  			m_statusLabel.setText("<html><font color=\"red\">" + getContext().getMessage("Error") + "!");
	  		else
	  			m_statusLabel.setText("<html><font color=\"green\">" + getContext().getMessage("applet_loaded"));
  		} else
  			m_statusLabel.setText("<html><font color=\"orange\">" + getContext().getMessage("Loading..."));
  	}
  }
  
  /**
   * Reports an error to the quality feedback system.
   */
  public void reportError(Throwable error) {
  	if(getRuntime() == null) {
  		error.printStackTrace();
  		String[] text = {
  			"An unknown error has ocurred during applet start: ",
  			"  " + error.toString(),
  			" ",
  			"Refer to the Java console for more information!"
  		};
  		JOptionPane.showMessageDialog(getContext().getJApplet().getContentPane(), text, "Error", JOptionPane.ERROR_MESSAGE);
  		if(m_statusLabel != null) // only set if embedding mode is LABEL
  			m_statusLabel.setText("<html><font color=\"red\">Error");
  	} else
  		getRuntime().addError(error);
  	setLabelText();
  	if(isInitialized())
  		handleErrors();
  }
  
  /**
   * Returns whether errors have been reported to this support instance.
   */
  public boolean hasErrors() {
  	// a "null" runtime indicates an error during runtime initialization!
  	return getRuntime() == null || getRuntime().hasErrors();
  }
    
  /**
   * Returns whether full screen windows are supported by this mathlet's runtime.
   */
  public boolean isFullScreenSupported() {
  	return m_contentFrame != null && m_contentFrame.isFullScreenSupported();
  }
  
  /**
   * Sets whether full screen visualization should be used.
   */
  public void setFullScreenEnabled(boolean flag) {
  	if(m_contentFrame != null)
  		m_contentFrame.setFullScreenEnabled(flag);
  }
  
  /**
   * Sets this mathlet's version as a system property.
   */
  public void setMathletVersion(Version v) {
		getRuntime().getSystemProperties().setProperty(SystemPropertyIF.MATHLET_VERSION_PROPERTY, v.toString());
  }
  
  /**
   * Sets this mathlet's date as a system property.
   */
  public void setMathletDate(Date date) {
  	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, m_mathlet.getLocale());
		getRuntime().getSystemProperties().setProperty(SystemPropertyIF.MATHLET_DATE_PROPERTY, df.format(date));
  }
  
  /**
   * Reloads this mathlet (if possible).
   */
  public void reloadApplet() {
  	try {
	  	if(getRuntime().isAppletExecutionMode())
	  		m_mathlet.getJApplet().getAppletContext().showDocument(m_mathlet.getJApplet().getDocumentBase(), "_self");
	  	else
	  		JOptionPane.showMessageDialog(m_mathlet.getAppletContentPane(), "Cannot restart application!");
  	} catch(Throwable t) {
  		JOptionPane.showMessageDialog(m_mathlet.getAppletContentPane(), "Cannot restart applet!");
  	}
  }
  
  /**
   * Returns whether the JavaHelp library is available.
   */
  public boolean isJavaHelpAvailable() {
  	try {
  		Class.forName("javax.help.HelpSet");
  		return true;
  	} catch(ClassNotFoundException e) {
  		return false;
  	}
  }
  
  /**
   * Opens the help window.
   */
  public void showHelp() {
  	getRuntime().getHelpSystem().showHelp();
  }
  
//  public void setLibVersion(Version v) {
//  	m_libVersion = v;
//  }
  
//	/**
//	 * Initializes the help system.
//	 */
//	private void initializeHelp() {
//		if (isJavaHelpAvailable()) {
//    	m_logger.log(RUNTIME_CATEGORY, "initializing JavaHelp system");
//			initializeJavaHelp();
//		} else {
//    	m_logger.log(RUNTIME_CATEGORY, "initializing simple help system");
//			initializeSimpleHelp();
//		}
//	}
//  
//	/**
//	 * Initializes the JavaHelp system.
//	 */
//  private void initializeJavaHelp() {
//  	try {
//  		m_javaHelp = new MumieHelp(m_mathlet);
//  		m_mathlet.addControl(m_javaHelp.getHelpButton(), MathletContext.BOTTOM_LEFT_PANE);
//  		m_mathlet.addControl(m_javaHelp.getContextHelpButton(), MathletContext.BOTTOM_LEFT_PANE);
//  	} catch(Throwable t) {
//  		System.err.println("Error occurred while initializing Java Help");
//  		t.printStackTrace();
//  		initializeSimpleHelp();
//  	}
//  }
  
  /**
   * Installs help for a given help ID for a root pane.
   */
  public void installHelp(JRootPane rootPane, String paneHelpID) {
  	getRuntime().getHelpSystem().installHelp(rootPane, paneHelpID);
  }
  
  /**
   * Installs help for a given help ID for a button.
   */
  public void installHelp(AbstractButton button, String buttonHelpID) {
  	getRuntime().getHelpSystem().installHelp(button, buttonHelpID);
  }

//	/**
//	 * Initializes the simple help system.
//	 */
//  private void initializeSimpleHelp() {
//  	try {
//  		JButton helpButton = new JButton(m_mathlet.getMessage("Help"));
//  		helpButton.setToolTipText(m_mathlet.getMessage("help_tooltip"));
//  		helpButton.addActionListener(new ActionListener() {
//        public void actionPerformed(ActionEvent e) {
//        	openSimpleHelpDialog();
//        }
//      });
//  		m_mathlet.addControl(helpButton, MathletContext.BOTTOM_LEFT_PANE);
//  	} catch(Throwable t) {
//  		System.err.println("Error occurred while initializing simple help.");
//  		t.printStackTrace();
//  	}
//  }
  
//  /**
//   * Opens the help window for the simple help system.
//   */
//  private void openSimpleHelpDialog() {
//  	try {
//	    // check if url has been set as applet-param for help file
//	  	if(m_mathlet.getParameter("helpURL") != null) {
//	  		if(getRuntime().isAppletExecutionMode())
//	  			m_mathlet.getJApplet().getAppletContext().showDocument(
//	  					new URL(m_mathlet.getJApplet().getDocumentBase(), m_mathlet.getParameter("helpURL")), "_blank");
//	  		else
//	  			throw new RuntimeException("Applications cannot view help files in a browser!");
//	  	} else { // param-tag has not been set
//	  		SimpleHelp help = new SimpleHelp(m_mathlet);
//	  		help.showHelp();
//	  	}
//  	} catch(Throwable t) {
//  		System.err.println("Error occurred while opening simple help: " + t);
//  	}
//  }
  
  /**
   * Loads and initializes a class from a given file and returns the instance.
   * The class will either be loaded from the class path or from an {@link URL}.
   */
	public Object loadClass(File containingJarFile, String className) throws Throwable {
		if(containingJarFile == null) // load from class path
			return Class.forName(className).newInstance();
		else { // load class from file
    	URLClassLoader classLoader = new URLClassLoader(new URL[] {containingJarFile.toURI().toURL()} );
    	return Class.forName(className, true, classLoader).newInstance();
		}
	}
	
	/**
	 * Loads and initalizes a mathlet from a given file with the given fully qualified class name and returns
	 * the instance.
	 * @see #loadClass(File, String)
	 */
	public MathletContext loadMathlet(File mathletFile, String mathletClass) throws Throwable {
		return (MathletContext) loadClass(mathletFile, mathletClass);
	}
}