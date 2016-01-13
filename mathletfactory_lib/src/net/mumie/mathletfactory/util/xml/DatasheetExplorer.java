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

package net.mumie.mathletfactory.util.xml;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;

public class DatasheetExplorer extends JFrame{

  public final static String INITIAL_URL = "http://www.math.tu-berlin.de/~gronau/ds/prb_koordinatenvektoren1.datasheet.xml";
  public final static String INITIAL_FILE = "";

  private JDesktopPane m_desktop;

  private JToolBar m_topPane;

  private JButton m_fileChooserButton, m_urlChooserButton;

  private int m_iframeCounter = 0;
  
  private String m_oldDir, m_oldURL;

  public DatasheetExplorer() {
    super("Mumie Datasheet Explorer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    getContentPane().setLayout(new BorderLayout());
    m_desktop = new JDesktopPane();
    getContentPane().add(m_desktop, BorderLayout.CENTER);

    m_topPane = new JToolBar();
    getContentPane().add(m_topPane, BorderLayout.NORTH);

    m_fileChooserButton = new JButton("Load file...");
    m_topPane.add(m_fileChooserButton);
    m_urlChooserButton = new JButton("Load URL...");
    m_topPane.add(m_urlChooserButton);

    m_fileChooserButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
      	String dirToOpen = m_oldDir == null ? INITIAL_FILE : m_oldDir;
        JFileChooser fileChooser = new JFileChooser(dirToOpen);
        int result = fileChooser.showOpenDialog(DatasheetExplorer.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          m_oldDir = file.getAbsolutePath();
          String path = file.getAbsolutePath();
          loadDatasheet(XMLUtils.loadDataSheetFromFile(path), path);
        }//else is user canceling
      }
    });
    m_urlChooserButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
      	String urlToShow = m_oldURL == null ? INITIAL_URL : m_oldURL;
        String url = JOptionPane.showInputDialog(DatasheetExplorer.this, "Type in the URL of the DataSheet to be loaded:", urlToShow);
        if(url == null)// user canceled
          return;
        m_oldURL = url;
        loadDatasheet(XMLUtils.loadDataSheetFromURL(url), url);
      }
    });
    pack();
    setSize(800, 600);
    center();
  }

  private void loadDatasheet(DataSheet ds, String path) {
    if(ds == null) {
      JOptionPane.showMessageDialog(this, "The data sheet could not be loaded from:\n" + path);
      return;
    }
    DatasheetRenderer m_dsRenderer = new DatasheetRenderer(ds);
    JInternalFrame iframe = new JInternalFrame(path, true, true, false, false);
    iframe.getContentPane().setLayout(new BorderLayout());
    iframe.getContentPane().add(m_dsRenderer, BorderLayout.CENTER);
    iframe.setSize(400, 500);
    iframe.setVisible(true);
    m_desktop.add(iframe);
    iframe.toFront();
    iframe.setLocation(m_iframeCounter * 20, m_iframeCounter * 20);
    try {
      iframe.setSelected(true);
    } catch (java.beans.PropertyVetoException e) {}
    m_iframeCounter++;
  }

  /** Centers this frame on the screen. */
  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - getWidth())/2;
    int y = (screenSize.height - getHeight())/2;
    setLocation(x,y);
  }

  public static void main(String[] args) {
    Logger.getLogger("").setLevel(Level.OFF);
    MumieTheme mumieTheme = MumieTheme.DEFAULT_THEME;
    MetalLookAndFeel.setCurrentTheme(mumieTheme);
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

    } catch (Exception ex) {
      System.err.println(ex);
    }
    DatasheetExplorer app = new DatasheetExplorer();
    app.setVisible(true);
  }
}
