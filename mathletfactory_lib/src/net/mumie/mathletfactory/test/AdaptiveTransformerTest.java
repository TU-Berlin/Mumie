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

import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMOneChainInR2;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class AdaptiveTransformerTest extends SingleG2DCanvasApplet {
	private Class numberClass = MDouble.class;
	private double worldDimAtStart = 15;
	private Interval domain = new Interval(numberClass, -2*Math.PI, Interval.CLOSED, 2*Math.PI, Interval.CLOSED, Interval.SQUARE);
	private MMFunctionDefByOp xFunc = new MMFunctionDefByOp(
		numberClass,
		"sin(1/x)",
		domain
	);
	private MMFunctionDefByOp yFunc = new MMFunctionDefByOp(
		numberClass,
		"cos(1/x)",
		domain
	);
	//liste von interessanten funktionen
	/*fx=x^2;fy=x^2 => hier liegen alle punkte auf einer geraden ... der teil um 0 wird nie gefunden.
	 *fx=x;fy=sin(1/x) => bei neaherung gegen 0 wird die funktion extrem schnell und die bogenlaenge unendlich -> ungenaues zeichnen
	 *fx=x;fy=abs(x)/x => sprungstelle
	 *fx=x;fy=x; => gerade - theoretisch mit 2 punkten zeichenbar
	 *
	 *
	 */
	private MMOneChainInR2 chain = new MMOneChainInR2(xFunc, yFunc, domain);
	
	public void init() {
    super.init();
		setTitle("AdaptiveTransformerTest");
		getCanvas2D().addObject(new MMCoordinateSystem());
		
		initializeObjects();
    	getCanvas().addObject(chain);
		//getCanvas().addObject(yFunc);
		
		//getCanvas().addObject(new MMFunctionExpression());
	}

	public static void main(String[] args) {
		AdaptiveTransformerTest myApplet = new AdaptiveTransformerTest();
		BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600);
    myApplet.init();
    myApplet.start();
		f.pack();
		f.setVisible(true);
	}

	public void initializeObjects() {
		setObjectProperties();
		setTrafoInCanvas();
	}

	private void setObjectProperties() {
	}

	private void setTrafoInCanvas() {
		getCanvas2D().getW2STransformationHandler().setUniformWorldDim(
			worldDimAtStart);
	}
}

