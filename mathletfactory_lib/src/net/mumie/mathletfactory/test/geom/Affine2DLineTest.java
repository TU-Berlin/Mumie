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

package net.mumie.mathletfactory.test.geom;

//import com.incors.plaf.kunststoff.KunststoffLookAndFeel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.handler.global.g2d.GlobalMouseZoomInHandler;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.g2d.GridLineDescriptor;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * @author lars
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Affine2DLineTest extends SingleG2DCanvasApplet {
  private JPopupMenu popup;

  public static class ScreenShotFrame extends JFrame {
    private BufferedImage image;
    private JButton btnPrint;
    private JButton btnSave;
    private JButton btnClose;

    public ScreenShotFrame(String title, BufferedImage screenShot) {
      super(title);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      JToolBar toolBar = new JToolBar();
      toolBar.setFloatable(false);
      toolBar.add(new AbstractAction("Drucken") {
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
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      toolBar.add(new AbstractAction("Speichern") {
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
          if (fileChooser.showSaveDialog(ScreenShotFrame.this)
            == JFileChooser.APPROVE_OPTION) {
            try {
              ImageIO.write(image, "PNG", fileChooser.getSelectedFile());
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      });
      toolBar.add(new AbstractAction("Schliessen") {
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
      show();
    }
  }

  public class ScreenshotButton extends JButton implements ActionListener {
    public ScreenshotButton() {
      super("Screenshot");
      addActionListener(this);
      popup = new JPopupMenu();
      JMenuItem miCanvasShot = new JMenuItem("Bild der Grafik erzeugen");
      miCanvasShot.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          popup.setVisible(false);
          new Thread(new Runnable() {
            public void run() {
              showScreenshotFrom(getCanvas());
            }
          }).start();
        }
      });
      JMenuItem miAppletShot = new JMenuItem("Bild des Fenster erzeugen");
      miAppletShot.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          popup.setVisible(false);
          showScreenshotFrom(Affine2DLineTest.this);
        }
      });

      popup.add(miCanvasShot);
      popup.add(miAppletShot);
    }

    public void actionPerformed(ActionEvent event) {
      popup.show(getParent(), getX(), getY() + getHeight());
    }
  }

  public void init() {
    super.init();
    getCanvas().addGlobalHandler(new GlobalMouseZoomInHandler(getCanvas2D()));

    MMCoordinateSystem cs = new MMCoordinateSystem();
    getCanvas().addObject(cs);
    cs.addXGridLineDescriptor(
      new GridLineDescriptor(
        new MDouble(Math.PI),
        GridLineDescriptor.PI,
        Color.green));
    cs.addYGridLineDescriptor(
      new GridLineDescriptor(
        new MDouble(Math.sqrt(2)),
        GridLineDescriptor.SQRT + "2",
        Color.magenta));

    MMAffine2DLine line;
    Affine2DMouseTranslateHandler amth =
      new Affine2DMouseTranslateHandler(m_canvas);
    Affine2DKeyboardTranslateHandler akth =
      new Affine2DKeyboardTranslateHandler(m_canvas);

    line =
      new MMAffine2DLine(
        new MMAffine2DPoint(MDouble.class, -1, 0),
        new MMAffine2DPoint(MDouble.class, 1, 0));
    line.addHandler(amth);
    line.addHandler(akth);
    //getCanvas().addObject(line);
    line =
      new MMAffine2DLine(
        new MMAffine2DPoint(MDouble.class, 0, -1),
        new MMAffine2DPoint(MDouble.class, 0, 1));
    //getCanvas().addObject(line);
    MMAffine2DPoint centre = new MMAffine2DPoint(MDouble.class, 1, 1);
    centre.getDisplayProperties().setObjectColor(Color.RED);
    MMAffine2DPoint null1 = new MMAffine2DPoint(MDouble.class, 0, 1);
    null1.getDisplayProperties().setObjectColor(Color.blue);
    getCanvas().addObject(centre.getAsCanvasContent());

    addResetButton();
    addControl(new ScreenshotButton());

  }

  public void initializeObjects() {
    getCanvas2D().getW2STransformationHandler().setUniformWorldDim(10);
  }

  public static void main(String[] args) throws Exception {
    Affine2DLineTest myApplet = new Affine2DLineTest();
    BasicApplicationFrame f = new BasicApplicationFrame("Affine Linie", 600);
    myApplet.init();
    myApplet.start();
    f.getContentPane().add(myApplet);
    f.pack();
    f.setVisible(true);
  }
}
