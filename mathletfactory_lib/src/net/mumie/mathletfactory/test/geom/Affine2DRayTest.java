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
import java.awt.Color;

import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DRay;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * Test of the visualization of an 
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DRay}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class Affine2DRayTest extends SingleG2DCanvasApplet {
	private MMObjectIF m_firstPoint, m_secondPoint;
	private MMObjectIF m_ray;
	private DisplayProperties properties;
	private final double m_initLowerBound = -4;
	private final double m_initUpperBound = 4;
	private final double m_initialCanvasDim =
		(m_initUpperBound - m_initLowerBound);
	private Affine2DMouseTranslateHandler amth;

	public void init() {
		super.init();
		setTitle("Strahltest");
		getCanvas2D().getW2STransformationHandler().setUniformWorldDim(
			m_initialCanvasDim);
		getCanvas2D().addObject(new MMCoordinateSystem());
		createObjects();
    m_ray.setLabel("r");
		initializeObjects();
		setProperties();
		getCanvas().addObject((MMCanvasObjectIF) m_ray);
		getCanvas().addObject((MMCanvasObjectIF) m_firstPoint);
		getCanvas().addObject((MMCanvasObjectIF) m_secondPoint);
		addResetButton();
		addScreenShotButton();
	}

	private void createObjects() {
		m_firstPoint = new MMAffine2DPoint(MDouble.class, 0, 0);
		m_secondPoint = new MMAffine2DPoint(MDouble.class, 0, 1);
		m_ray =
			new MMAffine2DRay(
				(MMAffine2DPoint) m_firstPoint,
				(MMAffine2DPoint) m_secondPoint);
		properties = new DisplayProperties();
	}

	public void initializeObjects() {
		amth = new Affine2DMouseTranslateHandler(getCanvas2D());
		((Affine2DPoint) m_firstPoint).setXY(0.0, 1.0);
		((Affine2DPoint) m_secondPoint).setXY(1.0, 1.0);
		m_ray.setEditable(true);
		((MMAffine2DRay) m_ray).setInitialPoint((MMAffine2DPoint) m_firstPoint);
		((MMAffine2DRay) m_ray).setRayRunsThrough(
			(MMAffine2DPoint) m_secondPoint);
		m_firstPoint.addHandler(amth);
		m_secondPoint.addHandler(amth);
		setDependencies();
	}

	private void setDependencies() {
		DependencyAdapter DPA = new DependencyAdapter() {
			public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
				MMAffine2DRay ray = (MMAffine2DRay) dependant;
				ray.setInitialPoint((MMAffine2DPoint) free[0]);
				ray.setRayRunsThrough((MMAffine2DPoint) free[1]);
			}
		};
		m_ray.dependsOn(
			new MMObjectIF[] {
				(MMObjectIF) m_firstPoint,
				(MMObjectIF) m_secondPoint },
			DPA);
	}

	private void setProperties() {
		properties.setObjectColor(Color.blue);
		((MMAffine2DRay) m_ray).setDisplayProperties(properties);
	}

	public void reset() {
		initializeObjects();
		getCanvas().renderScene();
		getCanvas().repaint();
	}

  /**
   * Starting point if applet runs as an application.
   */
	public static void main(String[] args) {
		Affine2DRayTest myApplet = new Affine2DRayTest();
		myApplet.init();
		myApplet.start();
		BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 500);
		f.pack();
		f.setVisible(true);
	}
}
