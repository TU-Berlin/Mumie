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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.appletskeleton.util.SplitPanel;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.layout.SimpleGridConstraints;
import net.mumie.mathletfactory.display.layout.SimpleGridLayout;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * Abstract template class for an applet with 2
 * {@link net.mumie.mathletfactory.display.MMCanvas MMCanvas} aligned in a <i>column</i>
 * and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * below. Since it is independant of the number 
 * of dimensions of the graphics canvi, it is the base for 
 * {@link net.mumie.mathletfactory.appletskeleton.g2d.UpperLowerG2DCanvasApplet UpperLowerG2DCanvasApplet}.
 * 
 * @author Gronau 
 * @mm.docstatus finished
 */

public abstract class UpperLowerCanvasApplet extends BaseApplet {

  /** Canvi for drawing */
  protected MMCanvas m_upperCanvas, m_lowerCanvas;

  /** JPanels for the the controls and the canvi. */
  protected JPanel m_upperCanvasPane,
    m_lowerCanvasPane,
    m_upperCanvasTitlePane,
    m_lowerCanvasTitlePane,
    m_upperSubInsetPane,
    m_lowerSubInsetPane;

  /** JLabels for the canvi titles */
  protected JLabel m_upperCanvasLabel, m_lowerCanvasLabel;

  /** Constant for the canvas margin. */
//  private final static int m_canvasMargin = 20;

  public void init() {
    super.init();
    performLayouting();
  }

  private void performLayouting() {
    m_upperCanvasPane = new JPanel();
    m_upperCanvasPane.setLayout(new BorderLayout());
    m_upperCanvasTitlePane = new JPanel();
    m_upperCanvasPane.add(m_upperCanvasTitlePane, BorderLayout.NORTH);
    m_upperCanvasLabel = new JLabel();
    m_upperCanvasTitlePane.add(m_upperCanvasLabel);

    m_lowerCanvasPane = new JPanel();
    m_lowerCanvasPane.setLayout(new BorderLayout());
    m_lowerCanvasTitlePane = new JPanel();
    m_lowerCanvasPane.add(m_lowerCanvasTitlePane, BorderLayout.NORTH);
    m_lowerCanvasLabel = new JLabel();
    m_lowerCanvasTitlePane.add(m_lowerCanvasLabel);

    SimpleGridLayout sgLayout1 = new SimpleGridLayout(1, 4, m_canvasPane);
    sgLayout1.fixRow(2);
    sgLayout1.fixRow(4);
    m_canvasPane.setLayout(sgLayout1);
    m_canvasPane.add(m_upperCanvasPane, new SimpleGridConstraints(1, 1));
    m_canvasPane.add(m_lowerCanvasPane, new SimpleGridConstraints(1, 3));
    m_upperSubInsetPane = new JPanel(new BorderLayout());
    m_lowerSubInsetPane = new JPanel(new BorderLayout());
    m_canvasPane.add(m_upperSubInsetPane, new SimpleGridConstraints(1, 2));
    m_canvasPane.add(m_lowerSubInsetPane, new SimpleGridConstraints(1, 4));

    m_canvasTabbedPanel.add(m_canvasPane);
    
    m_centerSplitPanel = new SplitPanel(m_canvasTabbedPanel, m_controlTabbedPanel);
    
    m_centerPane.setLayout(new BorderLayout());
    m_centerPane.add(m_centerSplitPanel, BorderLayout.CENTER);
  }

  /** Returns the upper canvas to paint on. */
  public MMCanvas getUpperCanvas() {
    return m_upperCanvas;
  }

  /** Returns the lower canvas to paint on. */
  public MMCanvas getLowerCanvas() {
    return m_lowerCanvas;
  }

  /** Sets the title for the upper canvas. The title will be displayed over the canvas. */
  public void setUpperCanvasTitle(String title) {
    m_upperCanvasLabel.setText(title);
  }

  /** Sets the title for the lower canvas. The title will be displayed over the canvas. */
  public void setLowerCanvasTitle(String title) {
    m_lowerCanvasLabel.setText(title);
  }

  /** Places the given component below the upper canvas. */
  public void setUpperCanvasSubInset(Component comp) {
    m_upperSubInsetPane.add(new JScrollPane(comp), BorderLayout.CENTER);
    //m_leftSubInsetPane.add(comp, BorderLayout.CENTER);
  }

  /** Places the given component below the lower canvas. */
  public void setLowerCanvasSubInset(Component comp) {
    m_lowerSubInsetPane.add(new JScrollPane(comp), BorderLayout.CENTER);
    //m_rightSubInsetPane.add(comp, BorderLayout.CENTER);
  }

  protected abstract void initializeObjects();

  public void addScreenShotButton() {
      if (!wasStartedAsApplet())
        addControl(new UpperLowerCanvasApplet.ScreenshotButton(), BOTTOM_RIGHT_PANE);
    }

  public void reset() {
    initializeObjects(); // give all declared objects the starting values
    /* now adjust all dependencies
     * imagine p3 = p1+p2 (always), then perhaps only p1,p2 would be
     * reinitialized and to adjust p3 to its proper value you would have
     * to call p1.update() or p2.update()
    */
    ArrayList objectsUpper = getUpperCanvas().getObjects();
    ArrayList objectsLower = getLowerCanvas().getObjects();
    int upNum = objectsUpper.size();
    int lowNum = objectsLower.size();
    MMObjectIF[] mmobjs = new MMObjectIF[upNum + lowNum];
    for (int i = 0; i < upNum; i++)
      mmobjs[i] = (MMObjectIF) objectsUpper.get(i);
    for (int i = upNum; i < upNum + lowNum; i++)
      mmobjs[i] = (MMObjectIF) objectsLower.get(i - upNum);
    ActionManager.performActionCycleFromObjects(mmobjs);
  }

  public class ScreenshotButton extends BaseApplet.ScreenshotButton {
    
      public ScreenshotButton() {
        super();
        JMenuItem miCanvasShot = new JMenuItem(getMessage("Screenshot_of_both_canvases"));
        miCanvasShot.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            popup_print.setVisible(false);
            new Thread(new Runnable() {
              public void run() {
                BaseApplet.showScreenshotFrom(m_canvasPane);
              }
            }).start();
          }
        });
        popup_print.add(miCanvasShot);
      }

    }

}
