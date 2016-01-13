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

package net.mumie.mathletfactory.util.exercise.receipt;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.util.BasicApplicationFrame;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * This class can be used to display a receipt's content either in a panel or in a dialog/frame.
 * It supports 3 modes: application mode with internal frames (each for a single receipt), applet
 * mode, where the only receipt is shown directly in a dialog, and embedding mode where 
 * a receipt view is directly embedded in a container. The 2 first cases can be obtained by calling
 * one of the static "create" methods, in the last case one of this class's constructors
 * must be used.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class ReceiptViewer extends JPanel {

	public final String DEFAULT_RECEIPT_DIR = System.getProperty("user.home") + File.separator + "TUMULT";

	public final static int APPLICATION_MODE = 0;
	public final static int APPLET_MODE = 1;
	
	public static int frameWidth = 640;
	public static int frameHeight = 480;
	private static final int xOffset = 30, yOffset = 30;
	public static String TITLE = "Mumie Quittungsansicht";

	private int openFrameCount = 0;
	private String m_oldLoadPath = DEFAULT_RECEIPT_DIR;

	private final int m_mode;
	private JDesktopPane m_desktop;
	private JToolBar m_toolbar;
	private JMenuBar m_menubar;
	private InternalFrameAdapter m_frameListener;
	private Action m_loadReceiptAction, m_closeReceiptAction, m_defaultViewAction, m_extendedViewAction;
	private JRadioButtonMenuItem m_defaultViewMenuItem, m_extendedViewMenuItem;
	private JMenu m_fileMenu;
	private ReceiptViewerDialog m_receiptDialog;
	
	/**
	 * Creates a new receipt viewer panel in the given mode.
	 */
	public ReceiptViewer(int mode) {
		this(mode, null);
	}

	/**
	 * Creates a new receipt viewer panel in the given mode and with the given receipt.
	 */
	public ReceiptViewer(int mode, Receipt receipt) {		
		m_mode = mode;
		if(mode == APPLET_MODE && receipt == null)
			throw new NullPointerException("Receipt must not be null in applet mode !");
		
  	// check if system property "user.home" is readable
  	// true if applet is signed and certificate was accepted by the user
  	try {
  		if(System.getSecurityManager() != null)
  			System.getSecurityManager().checkPropertyAccess("user.home");
  	} catch(SecurityException ace) { // not readable
  		System.err.println("cannot read user home!");
  	}
		
		m_menubar = new JMenuBar();
		m_fileMenu = new JMenu("Quittung");
		m_menubar.add(m_fileMenu);

		if(isApplicationMode()) {
			initApplicationMode();
		}
		
		JMenu viewMenu = new JMenu("Ansicht");
		ButtonGroup viewGroup = new ButtonGroup();
		m_defaultViewAction = new DefaultViewAction();
		m_defaultViewMenuItem = new JRadioButtonMenuItem(m_defaultViewAction);
		m_defaultViewMenuItem.setSelected(true);
		viewGroup.add(m_defaultViewMenuItem);
		m_extendedViewAction = new ExtendedViewAction();
		m_extendedViewMenuItem = new JRadioButtonMenuItem(m_extendedViewAction);
		viewGroup.add(m_extendedViewMenuItem);
		viewMenu.add(m_defaultViewMenuItem);
		viewMenu.add(m_extendedViewMenuItem);
		m_menubar.add(viewMenu);
		m_menubar.add(Box.createHorizontalGlue());
		m_menubar.add(new JMenu("Hilfe"));
	}
	
	/**
	 * Initializes the proper fields for the application mode.
	 */
	private void initApplicationMode() {
		m_desktop = new JDesktopPane();
		m_frameListener = new InternalFrameAdapter() {
			public void internalFrameActivated(InternalFrameEvent e) {
				m_closeReceiptAction.setEnabled(true);
				m_defaultViewAction.setEnabled(true);
				m_extendedViewAction.setEnabled(true);
				ReceiptFrame iframe = (ReceiptFrame) m_desktop.getSelectedFrame();
				m_defaultViewMenuItem.setSelected(iframe.getReceiptPanel().isDefaultView());
				m_extendedViewMenuItem.setSelected(iframe.getReceiptPanel().isExtendedView());
			}
			public void internalFrameDeactivated(InternalFrameEvent e) {
				boolean b = m_desktop.getSelectedFrame() != null;
				m_closeReceiptAction.setEnabled(b);
				m_defaultViewAction.setEnabled(b);
				m_extendedViewAction.setEnabled(b);
			}
		};
		m_loadReceiptAction = new LoadReceiptAction();
		m_closeReceiptAction = new CloseReceiptAction();
		m_toolbar = new JToolBar();
		m_toolbar.addSeparator(new Dimension(10, 1));
		m_toolbar.setBorder(BorderFactory.createEtchedBorder());
		m_toolbar.setFloatable(false);
		m_toolbar.add(createToolbarButton(m_loadReceiptAction));
		m_toolbar.add(createToolbarButton(m_closeReceiptAction));
		m_toolbar.addSeparator();
		setLayout(new BorderLayout());
		add(m_toolbar, BorderLayout.NORTH);
		add(m_desktop);
		m_fileMenu.add(new JMenuItem(m_loadReceiptAction));
		m_fileMenu.add(new JMenuItem(m_closeReceiptAction));
		m_fileMenu.addSeparator();
		m_fileMenu.add(new JMenuItem(new CloseAppAction()));
	}
	
	/**
	 * Creates and returns a button for the toolbar.
	 */
	private JButton createToolbarButton(Action a) {
		JButton result = new JButton(a);
		return result;
	}
	
	/**
	 * Returns the mode this viewer is running in.
	 */
	public int getMode() {
		return m_mode;
	}
	
	/**
	 * Returns whether this viewer is running in application mode.
	 */
	public boolean isApplicationMode() {
		return getMode() == APPLICATION_MODE;
	}
	
	/**
	 * Returns whether this viewer is running in applet mode.
	 */
	public boolean isAppletMode() {
		return getMode() == APPLET_MODE;
	}
	
	public void addReceipt(Receipt receipt) {
		if(isAppletMode())
			throw new RuntimeException("method must not be called in applet mode !");
		ReceiptFrame frame = new ReceiptFrame(receipt);
		m_desktop.add(frame);
		frame.addInternalFrameListener(m_frameListener);
		frame.show();
	}
	
	/**
	 * Create and returns a new dialog containg a receipt viewer in applet mode with the given content.
	 * @param component the component inside the parent frame for this dialog
	 */
	public static JDialog createAppletDialog(JComponent component, Receipt receipt) {
		Window parent = SwingUtilities.windowForComponent(component);
		if(parent instanceof Frame)
			return createAppletDialog((Frame) parent, receipt);
		else if(parent instanceof Dialog)
			return createAppletDialog((Dialog) parent, receipt);
		else
			return createAppletDialog((Dialog) null, receipt);
	}
	
	/**
	 * Create and returns a new dialog containg a receipt viewer in applet mode with the given content.
	 * @param parent the parent frame of this dialog
	 */
	public static JDialog createAppletDialog(Frame parent, Receipt receipt) {
		return new ReceiptViewer(APPLET_MODE, receipt).createDialog(parent, receipt);
	}
	
	/**
	 * Create and returns a new dialog containg a receipt viewer in applet mode with the given content.
	 * @param parent the parent frame of this dialog
	 */
	public JDialog createDialog(Frame parent, Receipt receipt) {
		m_receiptDialog = new ReceiptViewerDialog(parent, receipt);
		return m_receiptDialog;
	}
	
	/**
	 * Create and returns a new dialog containg a receipt viewer in applet mode with the given content.
	 * @param parent the parent dialog of this dialog
	 */
	public static JDialog createAppletDialog(Dialog parent, Receipt receipt) {
		return new ReceiptViewer(APPLET_MODE, receipt).createDialog(parent, receipt);
	}
	
	/**
	 * Create and returns a new dialog containg a receipt viewer in applet mode with the given content.
	 * @param parent the parent dialog of this dialog
	 */
	public JDialog createDialog(Dialog parent, Receipt receipt) {
		m_receiptDialog = new ReceiptViewerDialog(parent, receipt);
		return m_receiptDialog;
	}
	
	/**
	 * Create and returns a new frame containg a receipt viewer in application mode.
	 */
	public static JFrame createApplicationFrame() {
    MumieTheme mumieTheme = MumieTheme.DEFAULT_THEME;
    MetalLookAndFeel.setCurrentTheme(mumieTheme);
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      MathletRuntime.createStaticRuntime(null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
		ReceiptViewer viewer = new ReceiptViewer(APPLICATION_MODE);
		BasicApplicationFrame frame = new BasicApplicationFrame(TITLE, frameWidth, frameHeight);
		frame.setContentPane(viewer);
		frame.setJMenuBar(viewer.m_menubar);
		frame.show();
		return frame;
	}
	
	public static void main(String[] args) {
		createApplicationFrame();
	}
	
	/**
	 * This class is used to display a dialog for the applet mode.
	 */
	class ReceiptViewerDialog extends JDialog {
		
		private ReceiptPanel m_receiptPanel; 
		
		ReceiptViewerDialog(Frame parent, Receipt receipt) {
			super(parent, TITLE, true);
			initDialog(receipt);
		}
		
		ReceiptViewerDialog(Dialog parent, Receipt receipt) {
			super(parent, TITLE, true);
			initDialog(receipt);
		}
		
		private void initDialog(Receipt receipt) {
			setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			setJMenuBar(m_menubar);
			if(receipt != null) {
				m_receiptPanel = new ReceiptPanel(receipt);
				getContentPane().add(m_receiptPanel);
			}
			m_fileMenu.add(new JMenuItem(new AbstractAction("Schliessen") {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}));
			pack();
			setSize(frameWidth, frameHeight);
			center();
		}
		
		public ReceiptPanel getReceiptPanel() {
			return m_receiptPanel;
		}
		
	  /** Centers this frame on the screen. */
	  public void center() {
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (screenSize.width - getWidth())/2;
	    int y = (screenSize.height - getHeight())/2;
	    setLocation(x,y);
	  }
	}
	
	/**
	 * This class is used to display an internal frame holding a receipt in application mode.
	 */
	class ReceiptFrame extends JInternalFrame {
		
		private ReceiptPanel m_receiptPanel;
		
		ReceiptFrame(Receipt receipt) {
			super(receipt.getFileName(), true, true, true, true);
			m_receiptPanel = new ReceiptPanel(receipt);
			getContentPane().add(m_receiptPanel, BorderLayout.CENTER);
			setSize(m_desktop.getWidth() - xOffset*openFrameCount, m_desktop.getHeight() - yOffset*openFrameCount);
			setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
			openFrameCount++;
		}
		
		public ReceiptPanel getReceiptPanel() {
			return m_receiptPanel;
		}
		
		public void show() {
			try {
				setSelected(true);
				setMaximum(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			super.show();
		}
	}
	
	/**
	 * This action is used to open new receipts.
	 */
	class LoadReceiptAction extends AbstractAction {

		LoadReceiptAction() {
			super("Öffnen...", ResourceManager.getIcon("/resource/icon/open16.gif"));
		}
		
		public void actionPerformed(ActionEvent ae) {
			JFileChooser chooser = new JFileChooser(m_oldLoadPath);
			chooser.setFileFilter(new FileFilter() {
				public boolean accept(File f) {
					if(f.isDirectory())
						return true;
					if(f.isFile() && f.getName().endsWith(".zip"))
						return true;
					return false;
				}

				public String getDescription() {
					return "Quittungsdateien (*.zip)";
				}
			});
			int option = chooser.showOpenDialog(m_desktop);
			if(option == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				m_oldLoadPath = file.getParent();
				addReceipt(new Receipt(file.getAbsolutePath()));
			}
		}
	}
	
	/**
	 * This action is used to close receipts.
	 */
	class CloseReceiptAction extends AbstractAction {

		CloseReceiptAction() {
			super("Schliessen", ResourceManager.getIcon("/resource/icon/animation/Stop16.GIF"));
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent ae) {
			m_desktop.getSelectedFrame().doDefaultCloseAction();
		}
	}
	
	/**
	 * This action is used to close the application.
	 */
	class CloseAppAction extends AbstractAction {

		CloseAppAction() {
			super("Beenden", ResourceManager.getIcon("/resource/icon/exit16.gif"));
		}
		
		public void actionPerformed(ActionEvent ae) {
			if(isApplicationMode())
				System.exit(0);
		}
	}
	
	/**
	 * This action is used to switch to the default view.
	 */
	class DefaultViewAction extends AbstractAction {
		DefaultViewAction() {
			super("Standard");
		}
		
		public void actionPerformed(ActionEvent ae) {
			if(isApplicationMode()) {
				ReceiptFrame iframe = (ReceiptFrame) m_desktop.getSelectedFrame();
				iframe.getReceiptPanel().setDefaultView();
			} else if(isAppletMode()) {
				m_receiptDialog.getReceiptPanel().setDefaultView();
			}
		}
	}

	/**
	 * This action is used to switch to the extended view.
	 */
	class ExtendedViewAction extends AbstractAction {
		ExtendedViewAction() {
			super("Erweitert");
		}
		
		public void actionPerformed(ActionEvent ae) {
			if(isApplicationMode()) {
				ReceiptFrame iframe = (ReceiptFrame) m_desktop.getSelectedFrame();
				iframe.getReceiptPanel().setExtendedView();
			} else if(isAppletMode()) {
				m_receiptDialog.getReceiptPanel().setExtendedView();
			}
		}
	}
}
