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

import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.noc.function.MMFunctionPanel;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * Test of the symbolic representation of an 
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class Affine2DLineNocTest extends SingleG2DCanvasApplet {
  private MMFunctionPanel m_straightLinePane;
	private MMObjectIF m_firstPoint, m_secondPoint;
  private MMObjectIF m_line;
	private LineDisplayProperties properties;
	private final double m_initLowerBound = -4;
	private final double m_initUpperBound = 4;
	private final double m_initialCanvasDim =
		(m_initUpperBound - m_initLowerBound);

	public void init() {
    super.init();
		setTitle("Geradentest");
		getCanvas2D().getW2STransformationHandler().setUniformWorldDim(
			m_initialCanvasDim);
		getCanvas2D().addObject(new MMCoordinateSystem());
		createObjects();
		initializeObjects();
		setProperties();
    getCanvas().addObject((MMCanvasObjectIF)m_line);
    addControl(m_straightLinePane);
		addResetButton();
		addScreenShotButton();
	}

	public void initializeObjects() {
		((Affine2DPoint)m_firstPoint).setXY(1, 1);
		((Affine2DPoint)m_secondPoint).setXY(-1, 1);
    m_line.setEditable(true);
    ((MMAffine2DLine)m_line).setFromPoints(new AffinePoint[]{(AffinePoint)m_firstPoint, (AffinePoint)m_secondPoint});
    m_straightLinePane = (MMFunctionPanel)(m_line.getAsContainerContent());        
	}

	private void createObjects() {
		m_firstPoint = new MMAffine2DPoint(MDouble.class, 1.0, 1.0);
		m_secondPoint = new MMAffine2DPoint(MDouble.class, -1.0, 1.0);
    m_line = new MMAffine2DLine((MMAffine2DPoint)m_firstPoint, (MMAffine2DPoint)m_secondPoint);
		properties = new LineDisplayProperties();
 	}

	private void setProperties() {
		properties.setObjectColor(Color.blue);
    ((MMAffine2DLine)m_line).setDisplayProperties(properties);
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
		Affine2DLineNocTest myApplet = new Affine2DLineNocTest();
		myApplet.init();
    myApplet.start();
		BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 500);
		f.pack();
		f.setVisible(true);
	}
}
