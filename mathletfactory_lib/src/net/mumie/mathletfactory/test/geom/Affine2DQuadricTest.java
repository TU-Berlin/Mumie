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

/**
 * Affine2DQuadrikTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.test.geom;

import java.awt.Color;

import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DQuadric;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class Affine2DQuadricTest extends SingleG2DCanvasApplet{
	
	private MNumber[] matrix=
	{new MDouble(1), new MDouble(), new MDouble(2),
			new MDouble(), new MDouble(-1), new MDouble(),
			new MDouble(2), new MDouble(), new MDouble(0)
	};
	
	private NumberMatrix quadricMatrix = new NumberMatrix(MDouble.class,3,matrix);
	
	private MMAffine2DQuadric quadric = new MMAffine2DQuadric(quadricMatrix);
	

	public void init(){
    super.init();
		quadric.setFromQuadricMatrix(quadricMatrix);
		
		
		System.out.println(quadric.getQuadricType(quadricMatrix));
		System.out.print(quadric.getTransformationMatrix().getEntry(1,1));
		System.out.print("  "+quadric.getTransformationMatrix().getEntry(1,2));
		System.out.println("  "+quadric.getTransformationMatrix().getEntry(1,3));
		System.out.print(quadric.getTransformationMatrix().getEntry(2,1));
		System.out.print("  "+quadric.getTransformationMatrix().getEntry(2,2));
		System.out.println("  "+quadric.getTransformationMatrix().getEntry(2,3));
		System.out.print(quadric.getTransformationMatrix().getEntry(3,1));
		System.out.print("  "+quadric.getTransformationMatrix().getEntry(3,2));
		System.out.println("  "+quadric.getTransformationMatrix().getEntry(3,3));
		
		//System.out.println(quadric.getAsCanvasContent());
		setTitle("2DQuadricTest");
		initializeObjects();
		
	}
	
	protected void initializeObjects() {
		getCanvas2D().getW2STransformationHandler().setUniformWorldDim(10);
		getCanvas().addObject(new MMCoordinateSystem());
        getCanvas2D().setBackground(new Color(248,248,255));
//		Affine2DMouseTranslateHandler amth =
//        new Affine2DMouseTranslateHandler(m_canvas);
//        Affine2DKeyboardTranslateHandler akth =
//        new Affine2DKeyboardTranslateHandler(m_canvas);
//
//		quadric.addHandler(amth);
//		quadric.addHandler(akth);
//		quadric.getAsCanvasContent().getDisplayProperties().setBorderColor(Color.blue);
//		quadric.getAsCanvasContent().getDisplayProperties().setObjectColor(Color.blue);
		getCanvas().addObject(quadric.getAsCanvasContent());
		
	}
	
//	public void reset(){
//	    getCanvas().removeAllObjects();
//        initializeObjects();
//        getCanvas().renderScene();
//        getCanvas().repaint();
//	}
	public static void main(String[] args){
    Affine2DQuadricTest myApplet = new Affine2DQuadricTest();
		myApplet.init();
    myApplet.start();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600);
    f.pack();
    f.setVisible(true);
  }
}

