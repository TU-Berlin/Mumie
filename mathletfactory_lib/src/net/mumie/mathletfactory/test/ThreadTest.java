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

package net.mumie.mathletfactory.test;

import java.awt.Color;
import java.util.ArrayList;

import net.mumie.mathletfactory.action.handler.global.GlobalAffine2DPointCreateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;
import net.mumie.mathletfactory.mmobject.analysis.vectorfield.MMVectorField2DOverR2DefByComponents;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class ThreadTest extends SingleG2DCanvasApplet implements Runnable {
	private Thread t;
	private MMVectorField2DOverR2DefByComponents vectorField;
	private Class numberClass = MDouble.class;
	private MMFunctionOverR2 xFunction;
	private MMFunctionOverR2 yFunction;
	private String startUpString1 = "5";
	private String startUpString2 = "5";
	private double dt_double = 0.1;

	public void init() {
    super.init();
		setTitle("ThreadTest");
		getCanvas2D().addObject(new MMCoordinateSystem());
		new GlobalAffine2DPointCreateHandler(getCanvas2D()) {
			protected void pointCreated(MMAffine2DPoint anObject) {
			}
		};
		t = new Thread(this);

		vectorField = new MMVectorField2DOverR2DefByComponents(numberClass);

		xFunction = new MMFunctionOverR2(MDouble.class, startUpString1);
		yFunction = new MMFunctionOverR2(MDouble.class, startUpString2);
		xFunction.setEditable(true);
		yFunction.setEditable(true);

		vectorField
			.dependsOn(
				new MMObjectIF[] { xFunction, yFunction },
				new DependencyAdapter() {
			public void doUpdate(MMObjectIF dependent, MMObjectIF[] free) {
				MMFunctionOverR2 func1 = (MMFunctionOverR2) free[0];
				MMFunctionOverR2 func2 = (MMFunctionOverR2) free[1];
				vectorField.setEvaluateStrings(
					func1.getOperation().toString(),
					func2.getOperation().toString());
			}
		});

		addControl(xFunction.getAsContainerContent());
		addControl(yFunction.getAsContainerContent());
		addResetButton();

		initializeObjects();

		getCanvas().addObject(vectorField.getAsCanvasContent());
		t.start();
	}

	public void initializeObjects() {
		setObjectProperties();
		setTrafoInCanvas();
	}

	private void setObjectProperties() {
		vectorField.getDisplayProperties().setObjectColor(Color.LIGHT_GRAY);
		vectorField.setArrowLength(0.2);
		vectorField.setXCount(10);
		vectorField.setYCount(10);
		vectorField.setVisualizeByLength(true);
	}

	private void setTrafoInCanvas() {
		//		getCanvas2D().getW2STransformationHandler().setUniformWorldDim(
		//			worldDimAtStart);
	}

	public static void main(String[] args) {
		ThreadTest myApplet = new ThreadTest();
		BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600);
    myApplet.init();
    myApplet.start();
		f.pack();
		f.setVisible(true);
	}

	public void run() {
		double[] tmpIn = new double[2];
		double[] tmpOut = new double[2];
		while (true) {
			ArrayList points = getCanvas().getObjectsOfType(MMAffine2DPoint.class);
			for (int i = 0; i < points.size(); i++) {
				tmpIn[0] = ((MMAffine2DPoint) points.get(i)).getXAsDouble();
				tmpIn[1] = ((MMAffine2DPoint) points.get(i)).getYAsDouble();
				vectorField.evaluate(tmpIn, tmpOut);
				((MMAffine2DPoint) points.get(i)).setXY(
					tmpIn[0] + dt_double * tmpOut[0],
					tmpIn[1] + dt_double * tmpOut[1]);
				getCanvas().renderScene();
				getCanvas().repaint();
				try {
					Thread.sleep(10);
				}
				catch (InterruptedException e) {
				}
			}
		}
	}

	public void reset() {
		getCanvas().removeAllObjectsOfType(MMAffine2DPoint.class);
	}
}
