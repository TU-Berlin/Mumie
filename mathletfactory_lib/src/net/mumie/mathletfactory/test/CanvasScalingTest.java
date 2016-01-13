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

/*
 * Created on May 28, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package net.mumie.mathletfactory.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;

import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.MM2DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * @author vossbeck
 */
public class CanvasScalingTest
	extends SingleG2DCanvasApplet
	implements ActionListener {

	MM2DCanvas c;

	public void init() {
    super.init();
		c = getCanvas2D();
		c.addObject(new MMCoordinateSystem());
		MMAffine2DPoint p0 = new MMAffine2DPoint(MDouble.class, 0, 0);
		MMAffine2DPoint ll = new MMAffine2DPoint(MDouble.class, -5, -5);
		MMAffine2DPoint ur = new MMAffine2DPoint(MDouble.class, 5, 5);
		c.addObject(p0);
		c.addObject(ll);
		c.addObject(ur);
		c.getW2STransformationHandler().setUniformWorldDim(15);

		JButton scaleButton1 = new JButton("scale1");
		JButton scaleButton2 = new JButton("scale2");
		scaleButton1.addActionListener(this);
		scaleButton2.addActionListener(this);

		addControl(scaleButton1);
		addControl(scaleButton2);
	}

	public void actionPerformed(ActionEvent e) {
		double w = c.getDrawingBoard().getWidth();
		double h = c.getDrawingBoard().getHeight();
		if (e.getActionCommand().equals("scale1")) {
			c.getWorld2Screen().setRectToScreen(
				new double[] { -5, -5 },
				new double[] { 5, 5 },
				w,
				h);
			c.adjustTransformations();
			System.out.println(c.getWorld2Screen());
		}
		else if (e.getActionCommand().equals("scale2")) {
			Rectangle2D.Double rect = new Rectangle2D.Double(-10, -10, 20, 20);
			c.getWorld2Screen().setRectToScreen(rect, w, h);
			c.adjustTransformations();
			System.out.println(c.getWorld2Screen());
		}
		c.renderScene();
		c.repaint();
	}

	/* (non-Javadoc)
	 * @see net.mumie.mathletfactory.appletskeleton.MMSingleCanvasApplet#initializeObjects()
	 */
	protected void initializeObjects() {
	}

	public static void main(String[] args) {

		CanvasScalingTest myApplet = new CanvasScalingTest();
		myApplet.init();
    myApplet.start();
		BasicApplicationFrame f =
			new BasicApplicationFrame("Iteration of Map", 600);
		f.getContentPane().add(myApplet);
		f.pack();
		f.setVisible(true);
	}
}
