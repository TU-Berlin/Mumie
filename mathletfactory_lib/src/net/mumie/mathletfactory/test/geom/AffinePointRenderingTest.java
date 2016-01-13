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

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalAffine2DPointCreateHandler;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class AffinePointRenderingTest extends JPanel {

	MMG2DCanvas m_canvas;

	ArrayList m_pointList;
	
	public AffinePointRenderingTest() {

		setLayout(new BorderLayout());

		m_canvas = new MMG2DCanvas();
		//m_canvas.setFocusable(true);

		//m_canvas.setControllerEnabled(false);
		printCanvasProperties();
		new GlobalAffine2DPointCreateHandler(m_canvas){
				{
					m_event = new MMEvent(MouseEvent.MOUSE_CLICKED,
										  MouseEvent.BUTTON1_MASK,
										  1,
										  MMEvent.NO_KEY,
										  MMEvent.NO_MODIFIER_SET);
				}
			  protected void pointCreated(MMAffine2DPoint anObject){
				m_pointList = getCanvas().getObjectsOfType(MMAffine2DPoint.class);
				// an already existing polynomial must be removed
			  }
			};
		add(m_canvas, BorderLayout.CENTER);

		MMAffine2DPoint p;
		MMAffine2DLineSegment l;
		PointDisplayProperties pp;
		Affine2DMouseTranslateHandler amth =
			new Affine2DMouseTranslateHandler(m_canvas);
		//    Affine2DMouseGridTranslateHandler amgth = new Affine2DMouseGridTranslateHandler(m_canvas,1);
		//    Affine2DKeyboardTranslateHandler akth = new Affine2DKeyboardTranslateHandler(m_canvas);
		//    Affine2DKeyboardGridTranslateHandler akgth = new Affine2DKeyboardGridTranslateHandler(m_canvas,0.1);

		MMAffine2DLine line =
			new MMAffine2DLine(MDouble.class, -0.1, 0.1, -0.1, 0.2);
		//line.addHandler(amth);
		p = new MMAffine2DPoint(MDouble.class, 0, 0);
		//p.addTransformer(GeneralTransformer.AFFINE_DEFAULT_TRANSFORM, m_canvas);
		//System.out.println(p.getTransformer().getTransformType(p.getTransformer()));
		pp = new PointDisplayProperties();
		pp.setShadowOffset(new Point(-5, -5));
		p.setDisplayProperties(pp);
		p.addHandler(amth);
		
		//    p.addHandler(amgth);
		//    p.addHandler(akth);
		//    p.addHandler(akgth);
		m_canvas.addObject(p.getAsCanvasContent());
		m_canvas.addObject(line.getAsCanvasContent());
		//m_canvas.addObject(new MMAffine2DPoint(MDouble.class,0.1,0.1));
	}

	private void printCanvasProperties() {
		System.out.println(m_canvas.getLocation());
		System.out.println(m_canvas.getLayout().getClass().getName());
		System.out.println(m_canvas.isFocusable());
		System.out.println(m_canvas.isDoubleBuffered());
	}

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("").setLevel(
			java.util.logging.Level.WARNING);
		AffinePointRenderingTest myPanel = new AffinePointRenderingTest();
		BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
		f.pack();
		f.setVisible(true);
	}

}
