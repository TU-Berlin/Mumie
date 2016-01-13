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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * Abstract template class for an applet with 1
 * {@link net.mumie.mathletfactory.display.MMCanvas MMCanvas}and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * one under the other. Since it is independant of the number of dimensions of
 * the graphics canvas, it is the base for
 * {@link net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet SingleG2DCanvasApplet}
 * and
 * {@link net.mumie.mathletfactory.appletskeleton.j3d.SingleJ3DCanvasApplet SingleJ3DCanvasApplet}.
 *
 * @author Gronau
 * @mm.docstatus finished
 */

public abstract class SingleCanvasApplet extends BaseApplet {

	/** Canvas for drawing */
	protected MMCanvas m_canvas;

	/** Constant for the canvas margin. */
//	private final static int m_canvasMargin = 20;

	public void init() {
		super.init();
		getCanvasPane().setLayout(new BorderLayout());
//		m_canvasPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    getCanvasTabbedPanel().add(getCanvasPane());

		//m_centerSplitPanel = new SplitPanel(m_canvasTabbedPanel, m_controlTabbedPanel);

    getCanvasPane().add(getControlTabbedPanel(), BorderLayout.SOUTH);
//		getCenterPane().setLayout(new BorderLayout());
    getCenterPane().add(getCanvasTabbedPanel());
//    getCenterPane().add(getControlTabbedPanel(), BorderLayout.SOUTH);

//    final Box hBox = Box.createVerticalBox();
//    hBox.add(getCanvasTabbedPanel());
//    hBox.add(getControlTabbedPanel());
    getContentPane().addComponentListener(m_canvas);
//    getContentPane().addComponentListener(new ComponentAdapter(){
//      public void componentResized(ComponentEvent e){
//       	System.out.println("canvas.height="+m_canvas.getDrawingBoard().getSize()+", box.height="+hBox.getHeight()+", canvasPane"+m_canvasTabbedPanel);
//       	if(hBox.getHeight() < m_canvas.getDrawingBoard().getHeight() || hBox.getHeight() < m_canvasTabbedPanel.getHeight()){
////       		m_canvas.getDrawingBoard().setBounds(0,0,10,10);
////					m_canvas.getDrawingBoard().getParent().setBounds(0,0,10,10);
////					m_canvas.setSize(10,10);
//       		m_canvas.componentResized(new ComponentEvent(SingleCanvasApplet.this, ComponentEvent.COMPONENT_RESIZED));
//       		m_canvas.revalidate();
//       		m_canvas.repaint();
//       	}
//      }
//    });
//		m_centerPane.add(hBox, BorderLayout.CENTER);
	}

	/** Returns the canvas to paint on. */
	public MMCanvas getCanvas() {
		return m_canvas;
	}

	/** Initializes all declared objects with the starting values. */
	protected void initializeObjects() {}

	public void invokeAllCanvasObjectUpdater() {
		for (int i = 0; i < getCanvas().getObjectCount(); i++)
			getCanvas().getObject(i).invokeUpdaters();
	}

	public void addScreenShotButton() {
		if (!wasStartedAsApplet())
			addControl(new SingleCanvasApplet.ScreenshotButton(), BOTTOM_RIGHT_PANE);
	}

	/** Gives all declared objects the starting values. */
	public void reset() {
		initializeObjects();
		/*
		 * now adjust all dependencies imagine p3 = p1+p2 (always), then perhaps
		 * only p1,p2 would be reinitialized and to adjust p3 to its proper value
		 * you would have to call p1.update() or p2.update()
		 */
		ArrayList objects = getCanvas().getObjects();
		int num = objects.size();
		MMObjectIF[] mmobjs = new MMObjectIF[num];
		for (int i = 0; i < num; i++)
			mmobjs[i] = (MMObjectIF)objects.get(i);
		ActionManager.performActionCycleFromObjects(mmobjs);
	}

	public class ScreenshotButton extends BaseApplet.ScreenshotButton {

		public ScreenshotButton() {
			super();
			JMenuItem miCanvasShot =
				new JMenuItem(getMessage("Screenshot_canvas"));
			miCanvasShot.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					popup_print.setVisible(false);
					new Thread(new Runnable() {
						public void run() {
							BaseApplet.showScreenshotFrom(getCanvas().getDrawingBoard());
						}
					}).start();
				}
			});
			popup_print.add(miCanvasShot);
		}

	}

}
