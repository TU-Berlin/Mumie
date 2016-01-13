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

package net.mumie.mathletfactory.install;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class Installer extends JFrame{

	private final static String MUMIE_THINK_IMAGE = "/resource/images/mumie_think.gif";
	private final static String MUMIE_HAPPY_IMAGE = "/resource/images/mumie_happy.gif";
	private final static String MUMIE_SAD_IMAGE = "/resource/images/mumie_sad.gif";
	
  private static String PROPERTY_FILE = "/resource/install.properties";
	
  private final static String INTRO_PAGE = "INTRO";
  private final static String TABLE_PAGE = "TABLE";
  private final static String INSTALL_PAGE = "INSTALL";
  private final static String END_PAGE = "END";
  private final static String ERROR_PAGE = "CHECK";
  private final static String DEINSTALL_PAGE = "DEINSTALL";

  private final static String WELCOME_TEXT = "<html><br>Mumie Client Installation";
  private final static String INTRO_TEXT = "<html><br>Dieses Programm bereitet Ihren Computer <br>f&uuml;r die Benutzung der Mumie-Platform vor. <br><br>";//Im Folgenden wird Ihr System &uuml;berpr&uuml;ft und <br>fehlende Packete werden installiert bzw. aktualisiert <br>oder installierte Packet werden deinstalliert.
  private final static String TABLE_TEXT = "<html><center><br>Folgende Packete sollen installiert bzw. aktualisiert werden:<br>";
  private final static String INSTALL_TEXT = "<html><br>Installation l&auml;uft. Bitte warten...";
  private final static String SUCCESS_END_TEXT = "<html><br><br>Installation beendet.<br><br>Damit die &Auml;nderungen wirksam werden k&ouml;nnen, <br>m&uuml;ssen Sie Ihren Browser neu starten.";
  private final static String NO_CHANGES_END_TEXT = "<html><br><br>Installation beendet.<br><br>Es waren keine &Auml;nderungen an Ihrem System n&ouml;tig.<br>";
  private final static String ERROR_END_TEXT = "<html><br><br>Installation beendet.<br>Es sind Fehler aufgetreten.<br><br>";
  private final static String CANCEL_TEXT = "Wollen Sie die Installation wirklich abbrechen und beenden?";
  private final static String ERROR_NO_RIGHTS_TEXT1 = "<html><br><br>Sie besitzen nicht die notwendigen Schreibrechte <br>f&uuml;r das Java JRE Home Verzeichnis, um die <br>Installation durchzuf&uuml;hren!";
  private final static String ERROR_NO_RIGHTS_TEXT2 = "<html><br><br><b>Java JRE Home Verzeichnis:</b><br>";
  private final static String ERROR_NO_RIGHTS_TEXT3 = "<br><br>Wechseln Sie in den Root- bzw. Administrator-Modus <br>und wiederholen Sie die Installation.";
  private final static String ERROR_OLD_JAVA_VERSION_TEXT1 = "<html><br><br>Sie besitzen eine zu alte Java Version, <br>um mit der Mumie arbeiten zu k&ouml;nnen!";
  private final static String ERROR_OLD_JAVA_VERSION_TEXT2 = "<html><br><br>Sie ben&ouml;tigen mindestens die Version ";
  	private final static String ERROR_OLD_JAVA_VERSION_TEXT3 = ".<br>Installierte Version: ";
  	private final static String ERROR_OLD_JAVA_VERSION_TEXT4 = "<br><br>Installieren Sie eine aktuelle Java Version <br>und wiederholen Sie die Installation.";
  private final static String DEINSTALL_TEXT = "Sind Sie sicher, dass Sie alle installierten Packete entfernen wollen?";
  private final static String DEINSTALL_ERROR_OCCURRED_TEXT = "<html>Es sind Fehler aufgetreten.<br><br>";
  private final static String COPY_LOG_TEXT = "Nachrichten kopieren";
  private final static String CLOSE_WINDOW_TEXT = "Fenster schliessen";
  private final static String LOG_TITLE_TEXT = "Protokoll";
  private final static String AUTOMATIC_INSTALL_BUTTON_TEXT ="Automatische Installation"; 
  private final static String MANUAL_INSTALL_BUTTON_TEXT ="Benutzerdefinierte Installation"; 
  private final static String DEINSTALL_BUTTON_TEXT = "Deinstallation"; 
  private final static String CONTINUE_DIALOG_TEXT = "Fortsetzen?";
  private final static String CANCEL_DIALOG_TEXT = "Abbrechen?";
  private final static String DEINSTALL_TITLE_TEXT = "<html><br><br>Deinstallation l&auml;uft...<br><br>";
  private final static String DEINSTALL_SUCCESS_TITLE_TEXT = "<html><br><br>Deinstallation erfolgreich abgeschlossen!<br><br>";
  private final static String DEINSTALL_END_TITLE_TEXT = "<html><br><br>Deinstallation abgeschlossen!<br><br>";
  private final static String DEINSTALL_REMOVE_FILE_TEXT = "<html>Entferne ";
  private final static String DEINSTALL_REMOVE_SUCCESS_TEXT = " ..... <font color=\"green\">OK";
  private final static String DEINSTALL_REMOVE_ERROR_TEXT = " ..... <font color=\"red\">Fehler";
  private final static String DEINSTALL_REMOVE_NOT_FOUND_TEXT = " ..... <font color=\"blue\">nicht gefunden";
  //<br>Zum Fortfahren &lt;Weiter&gt; dr&uuml;cken.

  private final static Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);
  private final static Font SMALL_ITALIC_FONT = new Font("SansSerif", Font.ITALIC, 12);
  private final static Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 12);
  private final static Font BIG_FONT = new Font("SansSerif", Font.BOLD, 14);

  private final static Color BG1_COLOR = new Color(224, 223, 227);
  private final static Color BG2_COLOR = new Color(204, 204, 204);

  private static String ENDORSED_PATTERN = "endorsed.";
  private static String EXT_PATTERN = "ext.";
  private static String SOURCE_PATTERN = ".src";
  private static String NAME_PATTERN = ".name";
  private static String VERSION_PATTERN = ".version";

  private static String NONE_ACTION = "keine";
  private static String INSTALL_ACTION = "Installation";
//  private static String UPDATE_ACTION = "Update";

  private static String VERSION_KEY = "version";

  private Properties m_props = new Properties();

  private Vector m_packages = new Vector();
  private Vector m_errors = new Vector();
  
  private boolean m_systemExtWritable, m_systemEndorsedWritable, m_systemEndorsedExists;

//  private int m_amountOfData = 0;

//  private String m_actualPage = INTRO_PAGE;
  private CardLayout m_cards;

  private JPanel m_mainPane, m_pagePane, m_introPane, m_tablePane, m_installPane, m_endPane, m_errorPane, m_deinstallPane;
//  private JLabel m_statusLabel = new JLabel();
  private JButton[] m_backButtons = { new JButton("< Zurück"), new JButton("< Zurück"), new JButton("< Zurück") };
  private JButton[] m_nextButtons = { new JButton("Weiter >"), new JButton("Weiter >"), new JButton("Abbrechen"), new JButton("Beenden"), new JButton("Weiter >") };
  private JRadioButton m_autoSetupButton, m_manualSetupButton, m_deinstallButton;
  private PackageTable m_packageTable;
  private PackageLoaderThread m_loader;
  private JLabel m_iconLabel = new JLabel();

  private boolean m_isVerboseMode = false;
  private JButton m_showLogButton = new JButton("Protokoll anzeigen");

  public Installer() {
  		this(false);
  }
  
  public Installer(boolean verboseMode) {
    super("Mumie Client Installer");
    
    if(System.getProperty("java.ext.dirs") != null)
    		m_systemExtWritable = new File(System.getProperty("java.ext.dirs")).canWrite();
    if(System.getProperty("java.endorsed.dirs") != null) {
    		File endorsedDir =  new File(System.getProperty("java.endorsed.dirs"));
    		m_systemEndorsedWritable = endorsedDir.canWrite();
    		m_systemEndorsedExists = endorsedDir.exists();
    }
    
    if(verboseMode) {
    		m_isVerboseMode = verboseMode;
    		vlog("Verbose mode is turned on.");
        vlog("System-Properties:\n--------------------");
        vlog("java.version       = " + System.getProperty("java.version"));
        vlog("java.endorsed.dirs = " + System.getProperty("java.endorsed.dirs") + " (exists: " + m_systemEndorsedExists + ", writable: " + m_systemEndorsedWritable + ")");
        vlog("java.ext.dirs      = " + System.getProperty("java.ext.dirs") + "(Writable: " + m_systemExtWritable + ")");
        vlog("");
    }
    
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        askExit();
      }
    });
    setResizable(false);
    m_mainPane = new JPanel(new BorderLayout());

    JPanel bottomPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPane.add(m_showLogButton);
    bottomPane.add(m_backButtons[0]);
    bottomPane.add(m_nextButtons[0]);
    bottomPane.add(m_nextButtons[1]);
    bottomPane.add(m_nextButtons[2]);
    bottomPane.add(m_nextButtons[3]);
    bottomPane.add(m_nextButtons[4]);
    m_showLogButton.setVisible(false);
    m_backButtons[0].setVisible(false);
    m_nextButtons[1].setVisible(false);
    m_nextButtons[2].setVisible(false);
    m_nextButtons[3].setVisible(false);
    m_nextButtons[4].setVisible(false);
    m_mainPane.add(bottomPane, BorderLayout.SOUTH);
    // -> check
    m_nextButtons[0].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        if(m_manualSetupButton.isSelected()) { // manual setup
        		manualInstall();
        } else if(m_autoSetupButton.isSelected()) { // auto setup
        		autoInstall();
        } else if(m_deinstallButton.isSelected()) { // deinstallation
        		deinstall();
        }
      }
    });
    // -> install
    m_nextButtons[1].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        m_nextButtons[1].setVisible(false);
        m_nextButtons[2].setVisible(true);
        m_backButtons[0].setVisible(false);
        m_cards.next(m_pagePane);
        m_loader.start();
      }
    });
    // intro <-
    m_backButtons[0].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        m_nextButtons[0].setVisible(true);
        m_nextButtons[1].setVisible(false);
        m_backButtons[0].setVisible(false);
        m_cards.previous(m_pagePane);
      }
    });
    // -> end
    m_nextButtons[2].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        askExit();
      }
    });
    // -> exit
    m_nextButtons[3].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        System.exit(0);
      }
    });
    m_nextButtons[4].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        System.exit(0);
      }
    });
    m_showLogButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      		new LogDialog().show();
      }
    });

    m_introPane = new JPanel();
    JLabel l = new JLabel(WELCOME_TEXT);
    l.setFont(BIG_FONT);
    m_introPane.add(l);
    l = new JLabel(INTRO_TEXT);
    l.setFont(BOLD_FONT);
    m_introPane.add(l);
    m_autoSetupButton = new JRadioButton(AUTOMATIC_INSTALL_BUTTON_TEXT, true);
    m_manualSetupButton = new JRadioButton(MANUAL_INSTALL_BUTTON_TEXT);
    m_deinstallButton = new JRadioButton(DEINSTALL_BUTTON_TEXT);
    ButtonGroup bg = new ButtonGroup();
    bg.add(m_autoSetupButton);
    bg.add(m_manualSetupButton);
    bg.add(m_deinstallButton);
    JPanel p1 = new JPanel(new GridLayout(3, 1));
    p1.add(m_autoSetupButton);
    p1.add(m_manualSetupButton);
    p1.add(m_deinstallButton);
    m_introPane.add(p1);

    m_tablePane = new JPanel(new BorderLayout());
    l = new JLabel(TABLE_TEXT);
    l.setFont(BOLD_FONT);
    m_tablePane.add(l, BorderLayout.NORTH);

    m_packageTable = new PackageTable(m_packages);
    m_tablePane.add(m_packageTable, BorderLayout.CENTER);

    m_loader = new PackageLoaderThread(m_packages);
    m_installPane = new JPanel();
    m_installPane.setLayout(new GridLayout(2, 1));
    l = new JLabel(INSTALL_TEXT);
    l.setFont(BOLD_FONT);
    l.setHorizontalAlignment(JLabel.CENTER);
    m_installPane.add(l);
    m_installPane.add(m_loader);

    m_endPane = new JPanel();
    BoxLayout b = new BoxLayout(m_endPane, BoxLayout.Y_AXIS);
    m_endPane.setLayout(b);
    
    m_errorPane = new JPanel();
    m_deinstallPane = new JPanel(new BorderLayout());

    m_cards = new CardLayout();
    m_pagePane = new JPanel(m_cards);
    m_pagePane.add(m_introPane, INTRO_PAGE);
    m_pagePane.add(m_tablePane, TABLE_PAGE);
    m_pagePane.add(m_installPane, INSTALL_PAGE);
    m_pagePane.add(m_endPane, END_PAGE);
    m_pagePane.add(m_errorPane, ERROR_PAGE);
    m_pagePane.add(m_deinstallPane, DEINSTALL_PAGE);

    ImageIcon icon = new ImageIcon(getClass().getResource(MUMIE_THINK_IMAGE));
    m_iconLabel.setIcon(icon);
    JPanel p = new JPanel(new BorderLayout());
    p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10,10,10,10), BorderFactory.createLoweredBevelBorder()));
    p.add(m_iconLabel, BorderLayout.CENTER);
    m_mainPane.add(p, BorderLayout.WEST);
    m_mainPane.add(m_pagePane, BorderLayout.CENTER);
    getContentPane().add(m_mainPane);


    getContentPane().setBackground(BG1_COLOR);
    m_mainPane.setBackground(BG1_COLOR);
    m_pagePane.setBackground(BG1_COLOR);
    m_introPane.setBackground(BG1_COLOR);
    m_tablePane.setBackground(BG1_COLOR);
    m_installPane.setBackground(BG1_COLOR);
    m_endPane.setBackground(BG1_COLOR);
    m_errorPane.setBackground(BG1_COLOR);
    m_deinstallPane.setBackground(BG1_COLOR);
    m_packageTable.setBackground(BG1_COLOR);
    m_loader.setBackground(BG1_COLOR);
    p.setBackground(BG1_COLOR);
    p1.setBackground(BG1_COLOR);
    m_autoSetupButton.setBackground(BG1_COLOR);
    m_manualSetupButton.setBackground(BG1_COLOR);
    m_deinstallButton.setBackground(BG1_COLOR);
    bottomPane.setBackground(BG2_COLOR);
    bottomPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
    
    pack();
    setSize(550, 300);
    center();
    setVisible(true);

    loadProps();
    
// check java version
    Version clientVersion = new Version(System.getProperty("java.version"));
    Version targetVersion = new Version(m_props.getProperty("jre.minversion"));
    vlog(" ");
		vlog("Überprüfe Java Version");
    vlog("----------------------");
		vlog("Client java version: " + clientVersion.toString());
		vlog("Target java version: " + targetVersion.toString());
		vlog("Client version >= target version: " + clientVersion.isBiggerOrEqualThan(targetVersion));
		vlog("");
    if(!clientVersion.isBiggerOrEqualThan(targetVersion)) {
  			showErrorPane(ERROR_OLD_JAVA_VERSION_TEXT1, ERROR_OLD_JAVA_VERSION_TEXT2 + targetVersion + ERROR_OLD_JAVA_VERSION_TEXT3 + clientVersion + ERROR_OLD_JAVA_VERSION_TEXT4);
  			return;
    }
    
// check write permissions
    // extra condition if endorsed dir doesn't exist
    if(!m_systemExtWritable || (m_systemEndorsedExists && !m_systemEndorsedWritable)) {
    		showErrorPane(ERROR_NO_RIGHTS_TEXT1, ERROR_NO_RIGHTS_TEXT2 + System.getProperty("java.home") + ERROR_NO_RIGHTS_TEXT3);
  			return;
    }
  }
  
  void autoInstall() {
    m_nextButtons[0].setVisible(false);
    m_packages.clear();
    checkFiles();
    m_nextButtons[2].setVisible(true);
    m_cards.show(m_pagePane, INSTALL_PAGE);
    m_loader.start();
  }
  
  void manualInstall() {
    m_nextButtons[0].setVisible(false);
    m_nextButtons[1].setVisible(true);
    m_backButtons[0].setVisible(true);
    m_cards.next(m_pagePane);
    m_packages.clear();
    checkFiles();
  }
  
  void deinstall() {
    int choice = JOptionPane.showConfirmDialog(Installer.this, DEINSTALL_TEXT, CONTINUE_DIALOG_TEXT, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if(choice != JOptionPane.YES_OPTION) {
    		return;
    }
    m_packages.clear();
  		checkFiles();
  		JPanel p = new JPanel();
  		BoxLayout bl = new BoxLayout(p, BoxLayout.Y_AXIS);
  		p.setLayout(bl);
  		p.setBackground(BG1_COLOR);;
    JLabel l2 = new JLabel(DEINSTALL_TITLE_TEXT);
    l2.setFont(BOLD_FONT);
    l2.setHorizontalAlignment(JLabel.CENTER);
    m_deinstallPane.add(l2, BorderLayout.NORTH);
    JScrollPane deinstallScroller = new JScrollPane(p);
    deinstallScroller.setBorder(null);
  		m_deinstallPane.add(deinstallScroller, BorderLayout.CENTER);
  		m_nextButtons[0].setVisible(false);
  		m_nextButtons[3].setVisible(true);
  		m_nextButtons[3].setEnabled(false);
  		m_cards.show(m_pagePane, DEINSTALL_PAGE);
    boolean successful = true;
  		for(int i = 0; i < m_packages.size(); i++) {
      PackageListEntry entry = (PackageListEntry) m_packages.get(i);
  	    JLabel l = new JLabel();
  	    File f = new File(entry.getTarget(), entry.getFileName());
  	    if(f.exists()) {
  	    		if(f.delete()) {
    	    		vlog("Entferne Datei: " + entry.getTarget() + File.separator + entry.getFileName());
    	    		l.setText(DEINSTALL_REMOVE_FILE_TEXT + entry.getFileName() + DEINSTALL_REMOVE_SUCCESS_TEXT);  	    			
  	    		} else {
    	    		vlog("Kann Datei nicht entfernen: " + entry.getTarget() + File.separator + entry.getFileName());
    	    		l.setText(DEINSTALL_REMOVE_FILE_TEXT + entry.getFileName() + DEINSTALL_REMOVE_ERROR_TEXT);
    	    		successful = false;
  	    		}
  	    } else {
  	    		vlog("Datei nicht gefunden: " + entry.getTarget() + File.separator + entry.getFileName());
  	    		l.setText(DEINSTALL_REMOVE_FILE_TEXT + entry.getFileName() + DEINSTALL_REMOVE_NOT_FOUND_TEXT);
  	    }
  	    l.setFont(SMALL_FONT);
  	    p.add(l);
  		}
  		if(successful) {
    		l2.setText(DEINSTALL_SUCCESS_TITLE_TEXT);
  			m_iconLabel.setIcon(new ImageIcon(getClass().getResource(MUMIE_HAPPY_IMAGE)));
  		} else {
    		l2.setText(DEINSTALL_END_TITLE_TEXT);
  			m_iconLabel.setIcon(new ImageIcon(getClass().getResource(MUMIE_SAD_IMAGE)));
  	    JLabel l3 = new JLabel(DEINSTALL_ERROR_OCCURRED_TEXT);
  	    l3.setFont(BOLD_FONT);
  	    l3.setHorizontalAlignment(JLabel.CENTER);
  	    m_deinstallPane.add(l3, BorderLayout.SOUTH);
  	    m_showLogButton.setVisible(true);
  		}
  		m_nextButtons[3].setEnabled(true);
  }
  
  void showErrorPane(String message, String secondMessage) {
		m_nextButtons[0].setVisible(false);
		m_nextButtons[3].setVisible(true);
    JLabel l = new JLabel(message);
    l.setFont(BOLD_FONT);
    m_errorPane.add(l);
    if(secondMessage != null) {
      l = new JLabel(secondMessage);
      l.setFont(SMALL_FONT);
      m_errorPane.add(l);
    }
		m_iconLabel.setIcon(new ImageIcon(getClass().getResource(MUMIE_SAD_IMAGE)));
		m_cards.show(m_pagePane, ERROR_PAGE);
  }

  void askExit() {
    int choice = JOptionPane.showConfirmDialog(Installer.this, CANCEL_TEXT, CANCEL_DIALOG_TEXT, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if(choice == JOptionPane.YES_OPTION)
      System.exit(0);
  }
  
  void vlog(String s) {
		if(m_isVerboseMode) {
			log(s);
		}	
  }
  
  void vlog(Exception ex) {
  		if(m_isVerboseMode) {
  			if(ex.getCause() != null)
  				vlog("  Ursache: " + ex.getCause().toString());
  		}
  }

  void log(String s) {
    System.out.println(s);
    m_errors.add(s);
  }

  void setEndPage(boolean successful, boolean noChanges) {
    m_nextButtons[2].setVisible(false);
    m_nextButtons[3].setVisible(true);
    JLabel l = null;
    if(successful) {
      m_iconLabel.setIcon(new ImageIcon(getClass().getResource(MUMIE_HAPPY_IMAGE)));
      if(noChanges)
        l = new JLabel(NO_CHANGES_END_TEXT);
      else
        l = new JLabel(SUCCESS_END_TEXT);
    } else {
      m_iconLabel.setIcon(new ImageIcon(getClass().getResource(MUMIE_SAD_IMAGE)));
      l = new JLabel(ERROR_END_TEXT);
    }
    l.setFont(BOLD_FONT);
    l.setHorizontalAlignment(JLabel.CENTER);
    l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    m_endPane.add(l);
    m_showLogButton.setVisible(true);
    m_cards.next(m_pagePane);
  }
  
  class LogDialog extends JFrame {
  	
  		private JPanel m_logPanel;
  	  private JButton m_copyClipboardButton = new JButton(COPY_LOG_TEXT);
  	  private JButton m_closeButton = new JButton(CLOSE_WINDOW_TEXT);
  		
  		LogDialog() {
  			super(LOG_TITLE_TEXT);
  			
        JList logList = new JList(m_errors);
        logList.setFont(SMALL_FONT);
        JScrollPane scroller = new JScrollPane(logList);
        m_copyClipboardButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String allErrors = new String();
            for(int i = 0; i < m_errors.size(); i++) {
              allErrors += (String)m_errors.get(i) + "\n";
            }
            StringSelection content = new StringSelection(allErrors);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, content);
          }
        });
        m_closeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
          		LogDialog.this.setVisible(false);
          }
        });
        JPanel p = new JPanel();
        p.add(m_copyClipboardButton);
        p.add(m_closeButton);
        m_logPanel = new JPanel(new BorderLayout());
        m_logPanel.add(scroller, BorderLayout.CENTER);
        m_logPanel.add(p, BorderLayout.SOUTH);
        getContentPane().add(m_logPanel);
        pack();
        setSize(new Dimension(500, 400));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth())/2;
        int y = (screenSize.height - getHeight())/2;
        setLocation(x,y);
        m_copyClipboardButton.setVisible(true);
//        m_logPanel.setVisible(true);
//        m_endPane.revalidate();
  		}
  }

  class PackageLoaderThread extends JPanel implements Runnable {

    private JProgressBar m_progress = new JProgressBar(0, 100);
    private JLabel m_targetLabel, m_fileNameLabel, m_packageLabel;

    private Vector m_entries;
    private boolean m_run = false;
    private boolean m_successful = true;
    private boolean m_noChanges = true;
    private Thread m_thread;

    PackageLoaderThread(Vector entries) {
      m_entries = entries;

      setLayout(new BorderLayout());
      m_progress.setStringPainted(true);
      m_targetLabel = new JLabel(" ");
      m_targetLabel.setFont(SMALL_FONT);
      m_fileNameLabel = new JLabel(" ");
      m_fileNameLabel.setFont(SMALL_FONT);
      m_packageLabel = new JLabel(" ");
      m_packageLabel.setFont(SMALL_FONT);

      Box descPane = new Box(BoxLayout.X_AXIS);
//      descPane.setBorder(BorderFactory.createTitledBorder("Beschreibung"));
      Box leftCol = new Box(BoxLayout.Y_AXIS);
      Box rightCol = new Box(BoxLayout.Y_AXIS);
      descPane.add(leftCol);
      descPane.add(Box.createHorizontalStrut(10));
      descPane.add(rightCol);
      JLabel l = new JLabel("Packet: ");
      l.setFont(BOLD_FONT);
      leftCol.add(l);
      rightCol.add(m_packageLabel);
      l = new JLabel("Name: ");
      l.setFont(BOLD_FONT);
      leftCol.add(l);
      rightCol.add(m_fileNameLabel);
      l = new JLabel("Ziel: ");
      l.setFont(BOLD_FONT);
      leftCol.add(l);
      rightCol.add(m_targetLabel);
      l = new JLabel("Fortschritt: ");
      l.setFont(BOLD_FONT);
      leftCol.add(l);
      rightCol.add(m_progress);

      add(descPane, BorderLayout.CENTER);
    }

    void start() {
    	log(" ");
    	log("Starte Downloads");
    	log("----------------");
      m_run = true;
      m_thread = new Thread(this);
      m_thread.start();
    }

    void stop() {
      m_run = false;
    }

    public void run() {
      for(int i = 0; i < m_entries.size(); i++) {
        PackageListEntry entry = (PackageListEntry) m_entries.get(i);
        if(!entry.isRequested())
          continue;
        m_noChanges = false;
        BufferedInputStream in = null;
        FileOutputStream out = null;
        HttpURLConnection connection = null;
        int fileSize = 0;
        int byteCounter = 0;
        boolean proceed = true;
        try {
          m_packageLabel.setText("<html><b>" + (i+1) + " von " + m_entries.size());
          m_fileNameLabel.setText(entry.getFileName());
          m_targetLabel.setText(entry.getTarget());
          URL url = new URL(entry.getSource());
          connection = (HttpURLConnection) url.openConnection();
          fileSize = connection.getContentLength();
          m_progress.setValue(0);
          m_progress.setMaximum(fileSize);
//          Thread.sleep(1000);
          in = new BufferedInputStream(connection.getInputStream());
        } catch(UnknownHostException uhex) {
          log("Host nicht erreichbar: " + uhex.getMessage());
          vlog(uhex);
          m_successful = false;
          proceed = false;
        } catch(MalformedURLException muex) {
          log("Malformed URL: " + entry.getSource());
          vlog(muex);
          m_successful = false;
          proceed = false;
        } catch(IOException ioex) {
          log("Lese-Fehler: " + entry.getSource());
	      		vlog(ioex);
          m_successful = false;
          proceed = false;
        }
        // cancel writing on error
        if(!proceed)
          continue;
        try {
          File targetDir = new File(entry.getTarget());
//          if(!targetDir.exists()) {
          boolean created = targetDir.mkdirs();
          if(created)
            log("Erstelle Verz.: " + targetDir.getAbsolutePath());
          // targetDir.exists() returns true even if endorsed dir doesn't exist
          else if(!targetDir.exists())
            log("Konnte Verz. nicht erstellen: " + targetDir);
//          }
          out = new FileOutputStream(new File(entry.getTarget(), entry.getFileName()));
          byte[] data = new byte[512];
          int nrBytes = 0;
          while (m_run && ((nrBytes = in.read(data)) > -1)) {
            byteCounter += nrBytes;
            m_progress.setString(((int)((double)byteCounter/(double)fileSize*100.0)) + " %");
            m_progress.setValue(byteCounter);
            out.write(data, 0, nrBytes);
          }
          in.close();
          out.close();
          connection.disconnect();
          log("Kopieren der Datei \"" + entry.getFileName() + "\" erfolgreich");
        } catch(FileNotFoundException fnfex) {
          log("Konnte Datei nicht erstellen: " + new File(entry.getTarget(), entry.getFileName()));
          vlog(fnfex);
          m_successful = false;
        } catch(IOException ioex) {
          log("Schreib-Fehler: " + new File(entry.getTarget(), entry.getFileName()));
          vlog(ioex);
          m_successful = false;
        }
      }
      log(" ");
      log("Installation " + (m_successful ? "":"nicht ") + "erfolgreich " + (m_noChanges ? "ohne ":"mit ") + "Änderungen abgeschlossen.");
      setEndPage(m_successful, m_noChanges);
   }

    boolean wasSuccessful() {
      return m_successful;
    }
  }

  class PackageTable extends JPanel implements ListSelectionListener{

    private Vector m_packages;
    private JTable m_table;
    private JLabel m_targetLabel, m_fileNameLabel;

    private PackageTableModel m_model = new PackageTableModel();

    PackageTable(Vector packages) {
      m_packages = packages;

      setLayout(new BorderLayout());

      PackageListBooleanRenderer booleanRenderer = new PackageListBooleanRenderer();
      booleanRenderer.setHorizontalAlignment(PackageListBooleanRenderer.CENTER);

      PackageListStringRenderer stringRenderer = new PackageListStringRenderer();
      stringRenderer.setHorizontalAlignment(PackageListBooleanRenderer.CENTER);

      DefaultCellEditor booleanEditor = new DefaultCellEditor(new JCheckBox());
      ((JCheckBox) booleanEditor.getComponent()).setHorizontalAlignment(PackageListBooleanRenderer.CENTER);

      m_table = new JTable(m_model);
      m_table.getSelectionModel().addListSelectionListener(this);
      m_table.getColumnModel().getColumn(0).setCellRenderer(booleanRenderer);
      m_table.getColumnModel().getColumn(0).setPreferredWidth(40);
      m_table.getColumnModel().getColumn(0).setCellEditor(booleanEditor);
//      m_table.getColumnModel().getColumn(1).setPreferredWidth(300);

      m_table.getColumnModel().getColumn(1).setCellRenderer(stringRenderer);
      m_table.getColumnModel().getColumn(2).setCellRenderer(stringRenderer);
      m_table.setFont(SMALL_FONT);

      TableCellRenderer headerRenderer = m_table.getTableHeader().getDefaultRenderer();//.getColumnModel().getColumn(1).getHeaderRenderer();
      if (headerRenderer instanceof DefaultTableCellRenderer) {
        ((DefaultTableCellRenderer)headerRenderer).setHorizontalAlignment(SwingConstants.CENTER);
      }
      m_targetLabel = new JLabel();
      m_targetLabel.setFont(SMALL_FONT);
      m_fileNameLabel = new JLabel();
      m_fileNameLabel.setFont(SMALL_FONT);

      Box descPane = new Box(BoxLayout.X_AXIS);
      descPane.setBorder(BorderFactory.createTitledBorder("Beschreibung"));
      Box leftCol = new Box(BoxLayout.Y_AXIS);
      Box rightCol = new Box(BoxLayout.Y_AXIS);
      descPane.add(leftCol);
      descPane.add(Box.createHorizontalStrut(10));
      descPane.add(rightCol);
      JLabel l = new JLabel("Packet:");
      l.setFont(BOLD_FONT);
      leftCol.add(l);
      rightCol.add(m_fileNameLabel);
      l = new JLabel("Ziel:");
      l.setFont(BOLD_FONT);
      leftCol.add(l);
      rightCol.add(m_targetLabel);

      add(new JScrollPane(m_table), BorderLayout.CENTER);
      add(descPane, BorderLayout.SOUTH);
    }

    void doUpdate() {
        m_model.fireTableDataChanged();
    }

    public void valueChanged(ListSelectionEvent e) {
      if (e.getValueIsAdjusting()) return;
      ListSelectionModel lsm =
        (ListSelectionModel)e.getSource();
      if (!lsm.isSelectionEmpty()) {
        PackageListEntry entry = (PackageListEntry) m_packages.get(lsm.getMinSelectionIndex());
        m_targetLabel.setText(entry.getTarget());
        m_fileNameLabel.setText(entry.getFileName());
      }
    }
  }

  class PackageTableModel extends AbstractTableModel {

    public boolean isCellEditable(int row, int col) {
      return col == 0;// && ((PackageListEntry) m_packages.get(row)).isRequested();
    }

    public Object getValueAt(int row, int col) {
      PackageListEntry entry = (PackageListEntry) m_packages.get(row);
      switch(col) {
      case 0:
        return new Boolean(entry.isRequested());
      case 1:
        return entry.getFileName();
      case 2:
        return entry.getAction();
      }
      System.err.println("Bad column, returning null!");
      return null;
    }

    public int getRowCount() {
      if(m_packages != null)
        return m_packages.size();
      else
        return 0;
    }

    public Class getColumnClass(int columnIndex) {
      switch(columnIndex) {
        case 0: return Boolean.class;
        case 1: return String.class;
        case 2: return String.class;
      }
      return null;
    }

    public int getColumnCount() {
      return 3;
    }

    public String getColumnName(int columnIndex) {
      switch(columnIndex) {
      case 0: return "Installieren";
      case 1: return "Packet";
      case 2: return "Aktion";
    }
      return null;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      PackageListEntry entry = (PackageListEntry) m_packages.get(rowIndex);
      switch(columnIndex) {
      case 0:
        entry.setRequested(((Boolean)aValue).booleanValue());
        fireTableRowsUpdated(rowIndex, rowIndex);
      case 1:
//        return entry.getFileName();
      }
    }
  }

  class PackageListBooleanRenderer extends JCheckBox implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int row, int column) {
      setBackground(Color.WHITE);
      Boolean B = (Boolean) value;
      setSelected(B.booleanValue());
      setEnabled(B.booleanValue());
      if(isSelected) {
        setBackground(table.getSelectionBackground());
      } else {
        setBackground(table.getBackground());
      }
      return this;
    }
  }

  class PackageListStringRenderer extends JLabel implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int row, int column) {
      PackageListEntry entry = (PackageListEntry) m_packages.get(row);
      setEnabled(entry.isRequested());
      setText((String)value);
      if(entry.isRequested())
        setFont(SMALL_FONT);
      else
        setFont(SMALL_ITALIC_FONT);
      setOpaque(true);
      if(isSelected) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      } else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }
      return this;
    }
  }

  class PackageListEntry {

    private String m_source, m_target, m_fileName;
    private boolean m_isRequested = false;
    private boolean m_exists;
    private boolean m_userRequest = true;
    private Version m_version;
    private String m_actionString = NONE_ACTION;

    PackageListEntry(String source, String fileName, String target, boolean exists) {
      this(source, fileName, target, exists, null);
    }

    PackageListEntry(String source, String fileName, String target, boolean exists, String version) {
      m_source = source;
      m_fileName = fileName;
      m_target = target;
      m_exists = exists;
      if(version != null)
        m_version = new Version(version);
    }

    String getSource() {
      return m_source;
    }

    String getTarget() {
      return m_target;
    }

    String getFileName() {
      return m_fileName;
    }

    boolean isRequested() {
      return m_isRequested;
    }

    boolean exists() {
      return m_exists;
    }

    void setRequested(boolean b) {
      m_isRequested = b;
    }

    void setUserRequest(boolean b) {
      m_userRequest = b;
    }

    boolean getUserRequest() {
      return m_userRequest;
    }

    Version getVersion() {
      return m_version;
    }

    void setAction(String a) {
      m_actionString = a;
    }

    String getAction() {
      return m_actionString;
    }
  }

  private void loadProps() {
//    m_statusLabel.setText("Lade Einstellungen...");
    try {
      m_props.load(Installer.class.getResourceAsStream(PROPERTY_FILE));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "File \"install.properties\" not found!", "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }

  private String getEndorsedSource(int i) {
    return m_props.getProperty(ENDORSED_PATTERN + i + SOURCE_PATTERN);
  }

  private String getEndorsedName(int i) {
    return m_props.getProperty(ENDORSED_PATTERN + i + NAME_PATTERN);
  }

  private String getExtSource(int i) {
    return m_props.getProperty(EXT_PATTERN + i + SOURCE_PATTERN);
  }

  private String getExtName(int i) {
    return m_props.getProperty(EXT_PATTERN + i + NAME_PATTERN);
  }

  private String getExtVersion(int i) {
    return m_props.getProperty(EXT_PATTERN + i + VERSION_PATTERN);
  }

  private void checkFiles() {
    vlog(" ");
    log("Überprüfe aktuelle Konfiguration");
    log("--------------------------------");
//    int nrOfEntries = m_props.size();
    // checking endorsed files
    File endorsedDir = new File(System.getProperty("java.endorsed.dirs"));
    int i = 1;
    while(getEndorsedSource(i) != null) {
    	String fileURL = getEndorsedSource(i).trim();
    	String fileName = getEndorsedName(i).trim();//fileURL.substring(fileURL.lastIndexOf("/")+1);
      File f = new File(endorsedDir.getAbsolutePath(), fileName);
      PackageListEntry entry = new PackageListEntry(
          fileURL,
          fileName,
          endorsedDir.getAbsolutePath(),
          f.exists());
      m_packages.addElement(entry);
      checkEntry(entry);
      i++;
    }

    // checking ext files
    File extDir = new File(System.getProperty("java.ext.dirs"));
    int j = 1;
    while(getExtSource(j) != null) {
      String fileURL = getExtSource(j).trim();
      String fileName = getExtName(j).trim();
      String version = getExtVersion(j).trim();
      File f = new File(extDir.getAbsolutePath(), fileName);
      PackageListEntry entry = new PackageListEntry(
          fileURL,
          fileName,
          extDir.getAbsolutePath(),
          f.exists(),
          version);
      m_packages.addElement(entry);
      checkEntry(entry);
      j++;
    }
    m_packageTable.doUpdate();
  }

  class Version {

    private final static int NR_OF_DIGITS = 3;
    private int[] m_digits;
    private int m_subVersion = -1;

    Version(String spec) {
    	spec = spec.trim();
    	int _index = spec.indexOf("_");
    	if(_index > 1) {
    		String subVersionString = spec.substring(_index+1);
    		m_subVersion = Integer.parseInt(subVersionString);
    		spec = spec.substring(0, _index);
    	}
      // parse version digits
      // "\\D" instead of "." -> "\D" wg. String; non-decimal for regex Pattern
      String[] specs = spec.split("\\D");
      m_digits = new int[NR_OF_DIGITS];
      for(int i = 0; i < specs.length; i++) {
        m_digits[i] = Integer.parseInt(specs[i]);
      }
      for(int j = specs.length; j < NR_OF_DIGITS; j++) {
        m_digits[j] = 0;
      }
    }

    boolean isBiggerThan(Version v) {
//      return m_digits[0] > v.m_digits[0]
//        && m_digits[1] > v.m_digits[1]
//        && m_digits[2] > v.m_digits[2];
      return toString().compareTo(v.toString()) > 0;
    }
    
    boolean isBiggerOrEqualThan(Version v) {
    		return toString().compareTo(v.toString()) >= 0;
    }
    
    public boolean equals(Version v) {
  			return toString().compareTo(v.toString()) == 0;
    }

    public String toString() {
      String result = new String();
      for(int j = 0; j < NR_OF_DIGITS; j++) {
        result += m_digits[j];
        if(j < NR_OF_DIGITS -1)
          result += ".";
      }
      if(m_subVersion != -1) {
      	result += "_";
      	if(m_subVersion < 10)
      		result += "0" + m_subVersion;
      	else
      		result += m_subVersion;
      }
      return result;
    }
  }

  private void checkEntry(PackageListEntry entry) {
    if(!entry.exists()) {
      log("Packet fehlt: \"" + entry.getFileName() + "\"");
      entry.setAction(INSTALL_ACTION);
      entry.setRequested(true);
      return;
    }
    // for files without version control; files exist
    if(entry.getVersion() == null) {
      log("Packet gefunden: \"" + entry.getFileName() + "\"");
      entry.setAction(NONE_ACTION);
      entry.setRequested(false);
      return;
    }
    JarFile jar = null;
    try {
      jar = new JarFile(new File(entry.getTarget(), entry.getFileName()));
    } catch(IOException ioex) {
      // obsolete because of if-statement at the beginning of checkEntry-method
      log("Library not found, requiring install: " + entry.getFileName());
      entry.setAction(INSTALL_ACTION);
      entry.setRequested(true);
      return;
    }
    try {
//      jar.getManifest().
      JarEntry versionFile = jar.getJarEntry("version.properties");
      if(versionFile == null)
        throw new FileNotFoundException();
      Properties props = new Properties();
      props.load(jar.getInputStream(versionFile));
      Version localVersion = new Version(props.getProperty(VERSION_KEY));
      Version netVersion = entry.getVersion();
      if(netVersion.isBiggerThan(localVersion)) {
        log("Packet \"" + entry.getFileName() + "\" ist veraltet");
        entry.setAction(localVersion + " -> " + netVersion);
        entry.setRequested(true);
      } else {
        log("Packet \"" + entry.getFileName() + "\" ist up-to-date");
        entry.setAction(NONE_ACTION);
        entry.setRequested(false);
      }
    } catch (IOException e) {
      log("Cannot read version information from jar, requiring install: " + entry.getFileName());
      entry.setAction(INSTALL_ACTION);
      entry.setRequested(true);
      return;
    }
  }

  /** Centers this frame on the screen. */
  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - getWidth())/2;
    int y = (screenSize.height - getHeight())/2;
    setLocation(x,y);
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//getCrossPlatformLookAndFeelClassName()
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
    boolean verboseMode = false;
    for(int i = 0; i < args.length; i++) {
    		if(args[i].equals("-v")) {
    			verboseMode = true;
    		}
    }
    new Installer(verboseMode);
  }

}
