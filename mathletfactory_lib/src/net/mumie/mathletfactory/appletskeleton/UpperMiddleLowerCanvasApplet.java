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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.appletskeleton.util.SplitPanel;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * Abstract template class for a 2D-Applet with 3
 * {@link net.mumie.mathletfactory.display.MM2DCanvas MM2DCanvas}and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * aligned in a <i>column</i>. Since it is independant of the number of
 * dimensions of the graphics canvi, it is the base for
 * {@link net.mumie.mathletfactory.appletskeleton.g2d.UpperMiddleLowerG2DCanvasApplet UpperMiddleLowerG2DCanvasApplet}.
 * 
 * @author Gronau
 * @mm.docstatus finished
 *  
 */
public abstract class UpperMiddleLowerCanvasApplet extends BaseApplet {

	/** Canvi for drawing */
	protected MMCanvas m_upperCanvas, m_middleCanvas, m_lowerCanvas;

	/** JPanels for the the controls and the canvi. */
	protected JPanel m_upperCanvasPane,
		m_middleCanvasPane,
		m_lowerCanvasPane;

	public void init() {
		super.init();
		performLayouting();
	}

	private void performLayouting() {
		m_canvasPane.setLayout(new GridLayout(3, 1));
		
		m_canvasTabbedPanel.add(m_canvasPane);
		
		m_centerSplitPanel = new SplitPanel(m_canvasTabbedPanel, m_controlTabbedPanel);
		
		m_centerPane.setLayout(new BorderLayout());
		m_centerPane.add(m_centerSplitPanel, BorderLayout.CENTER);
	}

	/** Returns the upper canvas to paint on. */
	public MMCanvas getUpperCanvas() {
		return m_upperCanvas;
	}

	/** Returns the middle canvas to paint on. */
	public MMCanvas getMiddleCanvas() {
		return m_middleCanvas;
	}

	/** Returns the lower canvas to paint on. */
	public MMCanvas getLowerCanvas() {
		return m_lowerCanvas;
	}

	protected abstract void initializeObjects();

	public void addScreenShotButton() {
		if (!wasStartedAsApplet())
			addControl(new UpperMiddleLowerCanvasApplet.ScreenshotButton(), BOTTOM_RIGHT_PANE);
	}

	public void reset() {
		initializeObjects(); // give all declared objects the starting values
		/*
		 * now adjust all dependencies imagine p3 = p1+p2 (always), then perhaps
		 * only p1,p2 would be reinitialized and to adjust p3 to its proper value
		 * you would have to call p1.update() or p2.update()
		 */
		ArrayList objectsUpper = getUpperCanvas().getObjects();
		ArrayList objectsMiddle = getUpperCanvas().getObjects();
		ArrayList objectsLower = getLowerCanvas().getObjects();
		int upNum = objectsUpper.size();
		int midNum = objectsMiddle.size();
		int lowNum = objectsLower.size();
		MMObjectIF[] mmobjs = new MMObjectIF[upNum + lowNum];
		for (int i = 0; i < upNum; i++)
			mmobjs[i] = (MMObjectIF)objectsUpper.get(i);
		for (int i = upNum; i < upNum + midNum; i++)
			mmobjs[i] = (MMObjectIF)objectsLower.get(i - upNum);
		for (int i = upNum + midNum; i < upNum + midNum + lowNum; i++)
			mmobjs[i] = (MMObjectIF)objectsLower.get(i - upNum - midNum);
		ActionManager.performActionCycleFromObjects(mmobjs);
	}

	public class ScreenshotButton extends BaseApplet.ScreenshotButton {

		public ScreenshotButton() {
			super();
			JMenuItem miCanvasShot =
				new JMenuItem(getMessage("Screenshot_of_both_canvases"));
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
