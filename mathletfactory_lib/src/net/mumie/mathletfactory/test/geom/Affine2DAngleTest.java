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

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.util.BasicApplicationFrame;
/**
 * Test of {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle}.
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class Affine2DAngleTest extends SingleG2DCanvasApplet {
	private MMAffine2DAngle alpha, beta;
	private Affine2DPoint vertex, firstPoint, secondPoint;
	private MMDouble degrees, radians;
	private PointDisplayProperties properties1, properties2;
	private DisplayProperties properties3;
	private Affine2DKeyboardTranslateHandler akth;
	private Affine2DMouseTranslateHandler amth;
	private final double m_initLowerBound = -4;
	private final double m_initUpperBound = 4;
	private final double m_initialCanvasDim =
		(m_initUpperBound - m_initLowerBound);

	public void init() {
    super.init();
		setTitle("Winkeltest");
		getCanvas2D().getW2STransformationHandler().setUniformWorldDim(
			m_initialCanvasDim);
		getCanvas2D().addObject(new MMCoordinateSystem());
		createObjects();
		initializeObjects();
		setProperties();
		getCanvas().addObject(alpha);
    getCanvas().addObject(beta);
		getCanvas().addObject((MMCanvasObjectIF)vertex);
		getCanvas().addObject((MMCanvasObjectIF)firstPoint);
		getCanvas().addObject((MMCanvasObjectIF)secondPoint);
		insertLineBreaks(1);
		addText("Winkel im Gradmaﬂ:");
		addMMObjectAsContainerContent(degrees);
		addText("∞");
		insertLineBreaks(1);
		addText("Winkel im Bogenmaﬂ:");
		addMMObjectAsContainerContent(radians);
		insertLineBreaks(1);
    addMMObjectAsContainerContent(beta);

		addResetButton();
		addScreenShotButton();
	}

  private void createObjects() {
    vertex = new MMAffine2DPoint(MDouble.class);
    firstPoint = new MMAffine2DPoint(MDouble.class);
    secondPoint = new MMAffine2DPoint(MDouble.class);
    alpha =
      new MMAffine2DAngle(
        (Affine2DPoint)vertex,
        (Affine2DPoint)firstPoint,
        (Affine2DPoint)secondPoint,
        1);
    
    degrees = new MMDouble();
    radians = new MMDouble();
    properties1 = new PointDisplayProperties();
    properties2 = new PointDisplayProperties();
    properties3 = new DisplayProperties();
    akth = new Affine2DKeyboardTranslateHandler(getCanvas());
    amth = new Affine2DMouseTranslateHandler(getCanvas());
  }

	protected void initializeObjects() {
		vertex.setXY(0, 0);
		firstPoint.setXY(1, 1);
		secondPoint.setXY(-1, 1);
		alpha.setAngle(vertex, firstPoint, secondPoint, 2);
		((MMAffine2DPoint)firstPoint).addHandler(amth);
		((MMAffine2DPoint)firstPoint).addHandler(akth);
		((MMAffine2DPoint)secondPoint).addHandler(amth);
		((MMAffine2DPoint)secondPoint).addHandler(akth);
		((MMAffine2DPoint)vertex).addHandler(amth);
		((MMAffine2DPoint)vertex).addHandler(akth);
//    beta = new MMAffine2DAngle(MDouble.class, 1.0, 7.8, 2);
		setDependencies();
	}

	private void setDependencies() {
		DependencyAdapter DPA1 = new DependencyAdapter() {
			public void doUpdate(MMObjectIF dependant, MMObjectIF[] free) {
				MMAffine2DAngle angle = (MMAffine2DAngle)dependant;
				angle.setAngle(
					(Affine2DPoint)free[0],
					(Affine2DPoint)free[1],
					(Affine2DPoint)free[2],
					angle.getClosure());
			}
		};
		alpha.dependsOn(
			new MMObjectIF[] {
				(MMObjectIF)vertex,
				(MMObjectIF)firstPoint,
				(MMObjectIF)secondPoint },
			DPA1);
		DependencyAdapter DPA2 = new DependencyAdapter() {
			public void doUpdate(MMObjectIF dependant, MMObjectIF free) {
				MMDouble degree = (MMDouble)dependant;
				degree.setDouble(((MMAffine2DAngle)free).getDegrees());
			}
		};
		degrees.dependsOn((MMObjectIF)alpha, DPA2);
		DependencyAdapter DPA3 = new DependencyAdapter() {
			public void doUpdate(MMObjectIF dependant, MMObjectIF free) {
				MMDouble degree = (MMDouble)dependant;
				degree.setDouble(((MMAffine2DAngle)free).getRadians());
			}
		};
		radians.dependsOn((MMObjectIF)alpha, DPA3);
	}

	private void setProperties() {
		properties1.setObjectColor(Color.red);
		properties2.setObjectColor(Color.blue);
		properties3.setObjectColor(Color.yellow);
		((MMAffine2DPoint)firstPoint).setDisplayProperties(properties1);
		((MMAffine2DPoint)secondPoint).setDisplayProperties(properties1);
		((MMAffine2DPoint)vertex).setDisplayProperties(properties2);
		alpha.setDisplayProperties(properties3);
//		alpha.getDisplayProperties().setFilled(false);

	}

	public void reset() {
		initializeObjects();
		getCanvas().renderScene();
		getCanvas().repaint();
	}

	public static void main(String[] args) {
		Affine2DAngleTest myApplet = new Affine2DAngleTest();
		myApplet.init();
    myApplet.start();
		BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 700);
		f.pack();
		f.setVisible(true);
	}
}
