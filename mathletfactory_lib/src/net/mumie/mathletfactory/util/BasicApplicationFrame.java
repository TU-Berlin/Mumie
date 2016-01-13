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

package net.mumie.mathletfactory.util;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A utility class for displaying Applets as applications in a frame.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class BasicApplicationFrame extends JFrame {

  /** Default quadratic size of the applet displayed. */
  public static final int DEFAULT_QUADRATIC_SIZE = 600;

  private boolean m_isFullScreenEnabled = false;
  private Dimension m_oldFrameSize;

  private int m_width, m_height;
  private final Dimension dim = new Dimension();
  private WindowListener m_windowListener = new WindowAdapter() {

  public void windowClosing(WindowEvent e) {
    Window w = e.getWindow();
    w.setVisible(false);
    w.dispose();
    System.exit(0);
  }};

  /** Constructs this frame. */
  public BasicApplicationFrame() {
    this("");
  }

  /** Constructs this frame with the given title. */
  public BasicApplicationFrame(String str) {
    this(str,DEFAULT_QUADRATIC_SIZE);
  }

  /** Constructs this frame with the given title and size. */
  public BasicApplicationFrame(String str, int quadraticSize) {
    this(str,quadraticSize,quadraticSize);
  }

  /** Constructs this frame with the given applet and size. */
  public BasicApplicationFrame(JApplet applet, int quadraticSize) {
    this(applet,quadraticSize,quadraticSize);
  }

  /** Constructs this frame with the given applet and size. */
  public BasicApplicationFrame(JApplet applet, int width, int height) {
    this(applet.getClass().getName(),width,height);
    setContentPane(applet.getContentPane());
  }

  /** Constructs this frame with the given panel. */
  public BasicApplicationFrame(JPanel aPanel) {
    this(aPanel,DEFAULT_QUADRATIC_SIZE);
  }

  /** Constructs this frame with the given panel and size. */
  public BasicApplicationFrame(JPanel aPanel, int quadraticSize) {
    this(aPanel.getClass().getName(),quadraticSize);
    setContentPane(aPanel);
  }

  /** Constructs this frame with the given panel and size. */
  public BasicApplicationFrame(JPanel aPanel, int width, int height) {
    this(aPanel.getClass().getName(), width, height);
    setContentPane(aPanel);
  }

  /** Constructs this frame with the given name and size. */
  public BasicApplicationFrame(String str, int width, int height) {
    super(str);
    m_width = width;
    m_height = height;
    addWindowListener(m_windowListener);
    center();
  }

  /** Returns the preferred size of this frame. */
  public Dimension getPreferredSize() {
    dim.setSize(m_width, m_height);
    return dim;
  }

  /** Removes the window listener. */
  public void removeWindowMonitor(){
    removeWindowListener(m_windowListener);
  }

  /** Centers this frame on the screen. */
  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - m_width)/2;
    int y = (screenSize.height - m_height)/2;
    setLocation(x,y);
  }

  /**
   * Initializes this frame and makes it visible at the screen center.
   */
  public void show() {
    pack();
    setSize(m_width, m_height);
    //System.out.println("setting to "+m_width+" x "+m_height);
    center();
    super.show();
  }

  public void setSize(int width, int height){
    m_width = width;
    m_height = height;
    super.setSize(width, height);
  }

  public void setSize(Dimension dim){
    setSize(dim.width, dim.height);
  }

  public boolean isFullScreenSupported() {
    return GraphicsEnvironment
    .getLocalGraphicsEnvironment()
    .getDefaultScreenDevice().isFullScreenSupported();
  }

  public boolean isFullScreenEnabled() {
    return m_isFullScreenEnabled;
  }

  /** Sets the view to full screen mode, if supported. */
  public void setFullScreenEnabled(boolean enable) {
    if(m_isFullScreenEnabled == enable)
      return;
    m_isFullScreenEnabled = enable;
    GraphicsDevice device = GraphicsEnvironment
    .getLocalGraphicsEnvironment()
    .getDefaultScreenDevice();
    if(enable) {
  		if( ! device.isFullScreenSupported()) {
  				System.err.println("Full Screen Mode not supported!");
  				return;
  		}
      m_oldFrameSize = getSize();
      setVisible(false);
    	device.setFullScreenWindow(this);
      setSize(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
      setVisible(true);
    } else {
      setVisible(false);
      device.setFullScreenWindow(null);
      setSize(m_oldFrameSize);
      setVisible(true);
    }
  }
}

