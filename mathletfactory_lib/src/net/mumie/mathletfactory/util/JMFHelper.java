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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.MessageFormat;

import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * This class should not be loaded static. Use Graphics2DUtils.isJMFAvailable
 * to check whether this class can be used or not.
 *
 * @author Amsel
 * @mm.docstatus finished
 */
public class JMFHelper {
  
  /** 
   * Creates a video stream of the interactions with the given component at the specified frame rate 
   * and saves it as the given file. 
   */
  public static Processor recordVideo(Component component, int fps, File dest) {
    if (component == null)
      return null;
    Dimension size = component.getSize();
    Point location = component.getLocationOnScreen();
    String loc = MessageFormat.format("screen://{0},{1},{2},{3}/{4}", 
        new Object[] { 
            new Integer(location.x), new Integer(location.y), 
            new Integer(size.width - size.width % 8), new Integer(size.height - size.height % 8), 
            new Integer(fps)});
    MediaLocator mSource = new MediaLocator(loc);
    Processor p = null;
    try {
      DataSource data = Manager.createDataSource(mSource);
      p = Manager.createProcessor(mSource);
      waitForState(p, Processor.Configured);
      p.setContentDescriptor( new FileTypeDescriptor("video.x_msvideo"));
      p.getTrackControls()[0].setFormat(new VideoFormat(VideoFormat.MJPG));
      
      waitForState(p, Processor.Realized);
      
      DataSource data2 = p.getDataOutput();
      String path = dest.getParent();
      String name = dest.getName();
      if (!name.toLowerCase().endsWith(".avi"))
        name += ".avi";
      MediaLocator mediaDest = new MediaLocator(new File(path, name).toURL());
      DataSink dataSink = Manager.createDataSink(data2, mediaDest);
      dataSink.open();
      dataSink.start ();
      p.start ();
    } catch (Exception ex) {
      ex.printStackTrace();
      p = null;
    } 
    return p;    
  }
  
  /**
   * Stops recording for the given processor.
   */
  public static void stopRecord(Processor p) {
    if (p != null) {
      p.stop();
      p.close();
    }
  } 
  
  static Object stateLock = new Object();
  static boolean stateFailed = false;
    
  private static synchronized boolean waitForState(Processor p, int state) {
      p.addControllerListener(new StateListener());
      stateFailed = false;
  
      if (state == Processor.Configured) {
          p.configure();
      }
      else if (state == Processor.Realized) {
          p.realize();
      }

      while (p.getState() < state && !stateFailed) {
    synchronized (stateLock) {
          try {
              stateLock.wait();
          }
          catch (InterruptedException ie) {
              return false;
          }
    }
      }

    return ( !stateFailed );
  }
  
  static class StateListener implements ControllerListener {

      public void controllerUpdate ( ControllerEvent ce ) {
          if (ce instanceof ControllerClosedEvent)
              stateFailed = true;
          if (ce instanceof ControllerEvent)
              synchronized (stateLock) {
                  stateLock.notifyAll();
              }
      }
  }
  
  public static Action createRecordAction(Component component) {
    return new RecordAction(component);
  }
  
  public static Action createStopAction(Action record) {
    return new StopAction((RecordAction)record);
  }
  
  static class RecordAction extends AbstractAction implements PropertyChangeListener {
    private Processor processor = null;
    private JFileChooser fileChooser;
    private Component component;

    public RecordAction(Component comp) {
      super("", new ImageIcon(comp.getClass().getResource("/resource/icon/Record24.gif")));
      component = comp;
      fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
      fileChooser.setAcceptAllFileFilterUsed(true);
      fileChooser.resetChoosableFileFilters();
      fileChooser.setMultiSelectionEnabled(false);
      fileChooser.addChoosableFileFilter(new FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().endsWith(".avi");
        }
        public String getDescription() {
          return "Microsoft Video Format (*.avi)";
        }
      });
    }

    public Processor getProcessor() { return processor; }

    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("record")) {
        boolean isRecording = ((Boolean)event.getNewValue()).booleanValue(); 
        setEnabled(!isRecording);
        if (!isRecording) {
          processor = null;
        }
      }
    } 
    
    public void actionPerformed(ActionEvent e) {
      if (fileChooser.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
        processor = JMFHelper.recordVideo(component, 10, fileChooser.getSelectedFile());
        if (processor != null) {
          setEnabled(false);
          firePropertyChange("record", Boolean.FALSE, Boolean.TRUE);
        }
      }
    }
  }
  
  static class StopAction extends AbstractAction implements PropertyChangeListener {
    private RecordAction record;
    
    public StopAction(RecordAction action) {
      super("", new ImageIcon(action.getClass().getResource("/resource/icon/Stop24.gif")));
      setEnabled(false);
      record = action;
      record.addPropertyChangeListener(this);
      addPropertyChangeListener(record);
    }
    
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("record")) {
        boolean isRecording = ((Boolean)event.getNewValue()).booleanValue(); 
        setEnabled(isRecording);
      }
    } 
    
    public void actionPerformed(ActionEvent event) {
      Processor processor = record.getProcessor();
      if (processor == null) {
        return;
      }
      JMFHelper.stopRecord(processor);
      setEnabled(false);
      firePropertyChange("record", Boolean.TRUE, Boolean.FALSE);
    }
  }
 
}
