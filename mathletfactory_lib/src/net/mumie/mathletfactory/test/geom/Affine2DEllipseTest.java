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
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DEllipse;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * Test of {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DEllipse}.
 *
 * @author Mrose
 * @mm.docstatus finished
 */

public class Affine2DEllipseTest extends JPanel {
  
//  private MNumber[] matrix =
//  {new MDouble(0.1), new MDouble(0.0), new MDouble(0.0),
//      new MDouble(0.0), new MDouble(4.1), new MDouble(0.0),
//      new MDouble(0.0), new MDouble(0.0), new MDouble(-0.4)};
//  
//  private NumberMatrix quadricMatrix =
//    new NumberMatrix(MDouble.class, 3, matrix);
  
  private Affine2DEllipseTest() {
    
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
    
    // add ellipse:
    //    MMAffine2DEllipse ellipse = new MMAffine2DEllipse(new double[]{2.0, 1.0},
    //                                                      Math.PI/3.0,
    //                                                      3.0,
    //                                                      1.0);
    //    MMAffine2DEllipse ellipse = new MMAffine2DEllipse(quadricMatrix);
    MMAffine2DEllipse ellipse =
      new MMAffine2DEllipse(
      new double[] { -2.0, 3.0, 2.0, -1.0 },
      6.5,
      MDouble.class);
    
    //    ellipse.getDisplayProperties().setObjectColor(Color.yellow);
    ellipse.addHandler(amth);
    ellipse.addHandler(akth);
    m_canvas.addObject(ellipse.getAsCanvasContent());
    
    MMAffine2DEllipse ellipse2 =
      new MMAffine2DEllipse(new double[] { -1, -1 }, 0, 1, 1, MDouble.class);
    
    ellipse2.addHandler(amth);
    m_canvas.addObject(ellipse2.getAsCanvasContent());
    
    MMAffine2DPoint center, upperleft, focalpoint1, focalpoint2;
    PointDisplayProperties pp;
    center = new MMAffine2DPoint(MDouble.class, ellipse.getCenter()[0],
                                 ellipse.getCenter()[1]);
    upperleft = new MMAffine2DPoint(MDouble.class,
                                    ellipse.getUpperLeftPointOfBoundingBox()[0],
                                    ellipse.getUpperLeftPointOfBoundingBox()[1]);
    upperleft.getDisplayProperties().setFilled(false);
    center.getDisplayProperties().setObjectColor(Color.green);
    focalpoint1 = new MMAffine2DPoint(MDouble.class, ellipse.getFocalPoints()[0],
                                      ellipse.getFocalPoints()[1]);
    focalpoint2 = new MMAffine2DPoint(MDouble.class, ellipse.getFocalPoints()[2],
                                      ellipse.getFocalPoints()[3]);
    pp = new PointDisplayProperties();
    pp.setObjectColor(Color.blue);
    focalpoint1.setDisplayProperties(pp);
    focalpoint2.setDisplayProperties(pp);
    m_canvas.addObject(focalpoint1);
    m_canvas.addObject(focalpoint2);
    m_canvas.addObject(center);
    //    m_canvas.addObject(upperleft);
    m_canvas.addObject(r2);
    m_canvas.addObject(v1);
    m_canvas.addObject(v2);
  }
  
  /**
   * Runs the test.
   *
   * @param    args                a  <code>String[]</code>
   *
   */
  public static void main(String[] args) {
    java.util.logging.Logger.getLogger("").setLevel(
      java.util.logging.Level.SEVERE);
    Affine2DEllipseTest myPanel = new Affine2DEllipseTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
    f.pack();
    f.setVisible(true);
  }
  
}

