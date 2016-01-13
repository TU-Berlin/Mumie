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
import java.awt.Color;

import javax.swing.JPanel;

import net.mumie.mathletfactory.action.handler.Affine2DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DHyperbola;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLine;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;
/**
 * Affine2DHyperbolaTest.java
 *
 * @author Schimanowski, Liu
 * @mm.docstatus outstanding
 */
public class Affine2DHyperbolaTest extends JPanel {

  MNumber[] matrix =
    {
      new MDouble(9),new MDouble(),new MDouble(0),
      new MDouble(),new MDouble(-16),new MDouble(0),
      new MDouble(0),new MDouble(0),new MDouble(-144)};

  NumberMatrix quadricMatrix = new NumberMatrix(MDouble.class, 3, matrix);

  public Affine2DHyperbolaTest() {

    setLayout(new BorderLayout());

    MMG2DCanvas m_canvas = new MMG2DCanvas();
    m_canvas.getW2STransformationHandler().setUniformWorldDim(10);
    add(m_canvas, BorderLayout.CENTER);

    Affine2DMouseTranslateHandler amth =
      new Affine2DMouseTranslateHandler(m_canvas);
    Affine2DKeyboardTranslateHandler akth =
      new Affine2DKeyboardTranslateHandler(m_canvas);

    //add IR²-coordinate system:
    MMDefaultR2 r2 = new MMDefaultR2(MDouble.class);
    LineDisplayProperties lineProp1 = new LineDisplayProperties();
    lineProp1.setObjectColor(Color.blue);
    lineProp1.setArrowAtEnd(LineDisplayProperties.COMPLEX_ARROW_END);
    LineDisplayProperties lineProp2 = new LineDisplayProperties();
    lineProp2.setObjectColor(Color.red);
    lineProp2.setArrowAtEnd(LineDisplayProperties.COMPLEX_ARROW_END);
    r2.setGridInMath(1);
    NumberTuple coords =
      new NumberTuple(
        MDouble.class,
        new Object[] { new MDouble(1.0), new MDouble(0.0)});
    MMDefaultR2Vector v1 =
      (MMDefaultR2Vector) r2.getNewFromCoordinates(coords);
    v1.setDisplayProperties(lineProp1);
    coords =
      new NumberTuple(
        r2.getNumberClass(),
        new Object[] { new MDouble(0.0), new MDouble(1.0)});
    MMDefaultR2Vector v2 =
      (MMDefaultR2Vector) r2.getNewFromCoordinates(coords);
    v2.setDisplayProperties(lineProp2);

    MMAffine2DHyperbola hyperbola =
			  new MMAffine2DHyperbola(new double[]{-5,0,5,0}, 8);
      //new MMAffine2DHyperbola(new double[] { 2, -1 }, 1, 1, 2);
    //new MMAffine2DHyperbola(quadricMatrix);
	//System.out.println(hyperbola.getMatrix().getEntry(1,1).getDouble());
    hyperbola.addHandler(amth);
    hyperbola.addHandler(akth);
    m_canvas.addObject(hyperbola.getAsCanvasContent());
		  
		  MMAffine2DPoint focalPoint1,focalPoint2, center,p1,p2;
		  focalPoint1= new MMAffine2DPoint(MDouble.class,
			                               hyperbola.getFocalPoints()[0],
										   hyperbola.getFocalPoints()[1]);
		  focalPoint2= new MMAffine2DPoint(MDouble.class,
			                               hyperbola.getFocalPoints()[2],
										   hyperbola.getFocalPoints()[3]);
		  center = new MMAffine2DPoint(MDouble.class,
									   hyperbola.getCenter()[0],
									   hyperbola.getCenter()[1]);
		  p1= new MMAffine2DPoint(MDouble.class,
								  hyperbola.getAsymptote()[0],
								  hyperbola.getAsymptote()[1]);
		  p2=  new MMAffine2DPoint(MDouble.class,
								  hyperbola.getAsymptote()[2],
								  hyperbola.getAsymptote()[3]);
		  focalPoint1.getDisplayProperties().setObjectColor(Color.blue);
          focalPoint2.getDisplayProperties().setObjectColor(Color.blue);
		  center.getDisplayProperties().setObjectColor(Color.cyan);
		  
		  MMAffine2DLine asymptote1,asymptote2;
		  asymptote1 = new MMAffine2DLine(center,p1);
		  asymptote2 = new MMAffine2DLine(center,p2);
		  asymptote1.getDisplayProperties().setObjectColor(Color.orange);
		  asymptote2.getDisplayProperties().setObjectColor(Color.orange);
		  
//		  System.out.println("1Axis= " + hyperbola.getLengthSemimajorAxis());
//		  System.out.println("2Axis= " + hyperbola.getLengthSemiminorAxis());
//		  System.out.println("R=  " + hyperbola.getRadian());
    m_canvas.addObject(r2);
    m_canvas.addObject(v1);
    m_canvas.addObject(v2);
		  m_canvas.addObject(focalPoint1);
		  m_canvas.addObject(focalPoint2);
		  m_canvas.addObject(center);
		  m_canvas.addObject(asymptote1);
		  m_canvas.addObject(asymptote2);
		  
  }

  public static void main(String[] args) {
    java.util.logging.Logger.getLogger("").setLevel(
      java.util.logging.Level.WARNING);
    Affine2DHyperbolaTest myPanel = new Affine2DHyperbolaTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
    f.pack();
    f.setVisible(true);
  }

}

