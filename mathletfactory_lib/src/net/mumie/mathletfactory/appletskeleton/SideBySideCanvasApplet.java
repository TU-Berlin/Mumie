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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * Abstract template class for an applet with 2
 * {@link net.mumie.mathletfactory.display.MMCanvas MMCanvas} aligned in a <i>row</i>
 * and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * below. Since it is independant of the number 
 * of dimensions of the graphics canvi, it is the base for 
 * {@link net.mumie.mathletfactory.appletskeleton.g2d.SideBySideG2DCanvasApplet SideBySideG2DCanvasApplet} 
 * and {@link net.mumie.mathletfactory.appletskeleton.j3d.SideBySideJ3DCanvasApplet SideBySideJ3DCanvasApplet}.

 * 
 * @author Gronau 
 * @mm.docstatus finished
 */

public abstract class SideBySideCanvasApplet extends BaseApplet {

	/** Canvi for drawing */
	protected MMCanvas m_leftCanvas, m_rightCanvas;

	/** JPanels for the the controls and the canvi. */
	protected JPanel m_leftCanvasPane,
		m_rightCanvasPane,
		m_leftCanvasTitlePane,
		m_rightCanvasTitlePane,
		m_leftSubInsetPane,
		m_rightSubInsetPane;
		
	/** The SplitPanes for both canvi and the controlPanel. */
	protected JSplitPane m_canvasSplitPane;
	
	/** Field holding the location of the split bar. */
	protected int m_centerSplitPaneDividerLocation = 0;
  
  /** Field holding the orientation of the split pane, allowed values are constants used in JSplitPane. */
  protected int m_splitPaneOrientation = JSplitPane.VERTICAL_SPLIT;

	/** JLabels for the canvi titles */
  protected JLabel m_leftCanvasLabel, m_rightCanvasLabel;

	/** Constant for the canvas margin. */
//	private final static int m_canvasMargin = 20;

	public void init() {
    super.init();
		performLayouting();
	}

	private void performLayouting() {
		m_leftCanvasPane = new JPanel();
		m_leftCanvasPane.setLayout(new BorderLayout());
		m_leftCanvasTitlePane = new JPanel();
//		m_leftCanvasPane.add(m_leftCanvasTitlePane, BorderLayout.NORTH);
		m_leftCanvasLabel = new JLabel();
		m_leftCanvasTitlePane.add(m_leftCanvasLabel);
//		m_leftCanvasPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));

		m_rightCanvasPane = new JPanel();
		m_rightCanvasPane.setLayout(new BorderLayout());
		m_rightCanvasTitlePane = new JPanel();
		m_rightCanvasLabel = new JLabel();
		m_rightCanvasTitlePane.add(m_rightCanvasLabel);
//		m_rightCanvasPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
		
		m_canvasSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, m_leftCanvasPane, m_rightCanvasPane);
		m_canvasSplitPane.setResizeWeight(0.5);
		m_canvasSplitPane.setOneTouchExpandable(true);
		m_canvasSplitPane.setBorder(null);
		m_canvasPane.add(m_canvasSplitPane, BorderLayout.CENTER);

		m_leftSubInsetPane = new JPanel(new BorderLayout());
		m_leftCanvasPane.add(m_leftSubInsetPane, BorderLayout.SOUTH);
		m_rightSubInsetPane = new JPanel(new BorderLayout());
		m_rightCanvasPane.add(m_rightSubInsetPane, BorderLayout.SOUTH);

		//m_centerSplitPanel = new SplitPanel(m_canvasPane, m_controlsPane);
		final Box hBox = Box.createVerticalBox();
		hBox.add(m_canvasPane);
		hBox.add(m_controlTabbedPanel);
		getContentPane().addComponentListener(m_leftCanvas);
		getContentPane().addComponentListener(m_rightCanvas);
		m_canvasSplitPane.revalidate();
		getContentPane().addComponentListener(new ComponentAdapter(){
		  public void componentResized(ComponentEvent e){
			//System.out.println("canvas.height="+m_leftCanvas.getDrawingBoard().getHeight()+", box.height="+hBox.getHeight()+", canvasPane.height="+m_leftCanvasPane.getHeight());
				if(hBox.getHeight() < m_leftCanvas.getDrawingBoard().getHeight() || hBox.getHeight() < m_leftCanvasPane.getHeight()){       	
					m_leftCanvas.getDrawingBoard().setBounds(0,0,10,10);
					m_leftCanvas.getDrawingBoard().getParent().setBounds(0,0,10,10);
					m_leftCanvas.setSize(10,10);
					m_rightCanvas.getDrawingBoard().getParent().setBounds(0,0,10,10);
					m_rightCanvas.setSize(10,10);
					m_leftCanvasPane.setSize(hBox.getWidth()/2, hBox.getHeight());
					m_rightCanvasPane.setSize(hBox.getWidth()/2, hBox.getHeight());
					m_canvasSplitPane.setSize(hBox.getWidth(),hBox.getHeight());
					m_leftCanvas.componentResized(new ComponentEvent(hBox, ComponentEvent.COMPONENT_RESIZED));
					m_rightCanvas.componentResized(new ComponentEvent(hBox, ComponentEvent.COMPONENT_RESIZED));
					m_leftCanvas.getDrawingBoard().validate();
					m_rightCanvas.getDrawingBoard().validate();
					//System.out.println("Done: canvas.height="+m_leftCanvas.getDrawingBoard().getHeight()+", box.height="+hBox.getHeight()+", canvasPane.height="+m_leftCanvasPane.getHeight());				
				}
		  }
		});

		
		m_centerPane.setLayout(new BorderLayout());
		m_centerPane.add(hBox, BorderLayout.CENTER);
	}

	/** Returns the left canvas to paint on. */
	public MMCanvas getLeftCanvas() {
		return m_leftCanvas;
	}

	/** Returns the right canvas to paint on. */
	public MMCanvas getRightCanvas() {
		return m_rightCanvas;
	}

	/** Sets the title for the left canvas. The title will be displayed over the canvas. */
	public void setLeftCanvasTitle(String title) {
		if(title == null || title.equals("")) {
			m_leftCanvasTitlePane.remove(m_leftCanvasTitlePane);
		}
		else {
			m_leftCanvasLabel.setText(title);
			m_leftCanvasPane.add(m_leftCanvasTitlePane, BorderLayout.NORTH);
		}
	}
	
	
	/** Sets the title for the left canvas with the given font and foreground color. The title will be displayed over the canvas. */
	public void setLeftCanvasTitle(String title, Font aFont, Color color) {
		if(title == null || title.equals("")) {
			m_leftCanvasTitlePane.remove(m_leftCanvasTitlePane);
		}
		else {
			m_leftCanvasLabel.setText(title);
			m_leftCanvasLabel.setForeground(color);
			m_leftCanvasLabel.setFont(aFont);
			m_leftCanvasPane.add(m_leftCanvasTitlePane, BorderLayout.NORTH);
		}
	}	

	/** Sets the title for the right canvas. The title will be displayed over the canvas. */
	public void setRightCanvasTitle(String title) {
		if(title == null || title.equals("")) {
			m_rightCanvasTitlePane.remove(m_rightCanvasTitlePane);
		}
		else {
			m_rightCanvasLabel.setText(title);
			m_rightCanvasPane.add(m_rightCanvasTitlePane, BorderLayout.NORTH);
		}
	}
	
	/** Sets the title for the right canvas with the given font and foreground color. The title will be displayed over the canvas. */
	public void setRightCanvasTitle(String title, Font aFont, Color color) {
		if(title == null || title.equals("")) {
			m_rightCanvasTitlePane.remove(m_rightCanvasTitlePane);
		}
		else {
			m_rightCanvasLabel.setText(title);
			m_rightCanvasLabel.setForeground(color);
			m_rightCanvasLabel.setFont(aFont);
			m_rightCanvasPane.add(m_rightCanvasTitlePane, BorderLayout.NORTH);
		}
	}	

	/** Places the given component below the left canvas. */
	public void setLeftCanvasSubInset(Component comp) {
		m_leftSubInsetPane.add(new JScrollPane(comp), BorderLayout.CENTER);
		//m_leftSubInsetPane.add(comp, BorderLayout.CENTER);
	}

	/** Places the given component below the right canvas. */
	public void setRightCanvasSubInset(Component comp) {
		m_rightSubInsetPane.add(new JScrollPane(comp), BorderLayout.CENTER);
		//m_rightSubInsetPane.add(comp, BorderLayout.CENTER);
	}
		
	/**
	 * Passes the argument to JSplitPane.setDividerLocation(double). 
	 */
	public void setSplitPaneDividerLocation(double relativeLocation) {
		m_centerSplitPanel.getJSplitPane().setDividerLocation(relativeLocation);
	}
	
	/**
	 * Sets the location of the split bar (divider). If the specified integer-value is negative, the location is counted from the bottom.
	 */
	public void setSplitPaneDividerLocation(int loc) {
		m_centerSplitPaneDividerLocation = loc;
	}

  /** Initializes all declared objects with the starting values. */
	protected abstract void initializeObjects();

	public void addScreenShotButton() {
			if (!wasStartedAsApplet())
				addControl(new SideBySideCanvasApplet.ScreenshotButton(), BOTTOM_RIGHT_PANE);
		}

  /** Gives all declared objects the starting values. */
	public void reset() {
		initializeObjects(); // 
		/* now adjust all dependencies
		 * imagine p3 = p1+p2 (always), then perhaps only p1,p2 would be 
		 * reinitialized and to adjust p3 to its proper value you would have
		 * to call p1.update() or p2.update()
		*/
		ArrayList objectsLeft = getLeftCanvas().getObjects();
		ArrayList objectsRight = getRightCanvas().getObjects();
		int leftNum = objectsLeft.size();
		int rightNum = objectsRight.size();
		MMObjectIF[] mmobjs = new MMObjectIF[leftNum + rightNum];
		for (int i = 0; i < leftNum; i++)
			mmobjs[i] = (MMObjectIF) objectsLeft.get(i);
		for (int i = leftNum; i < leftNum + rightNum; i++)
			mmobjs[i] = (MMObjectIF) objectsRight.get(i - leftNum);
		ActionManager.performActionCycleFromObjects(mmobjs);
	}
	
//  protected void applyAppletSizeTheme() {
//    if(getLeftCanvas() == null || getRightCanvas() == null)
//      return;
//    super.applyAppletSizeTheme();
//    getLeftCanvas().reloadProperties();
//    getRightCanvas().reloadProperties();
//  }

	public class ScreenshotButton extends BaseApplet.ScreenshotButton {
		
			public ScreenshotButton() {
				super();
				JMenuItem miCanvasShot = new JMenuItem(getMessage("Screenshot_of_both_canvases"));
				miCanvasShot.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						popup_print.setVisible(false);
						new Thread(new Runnable() {
							public void run() {
								BaseApplet.showScreenshotFrom(m_canvasSplitPane);
							}
						}).start();
					}
				});
				popup_print.add(miCanvasShot);
			}

		}

}
