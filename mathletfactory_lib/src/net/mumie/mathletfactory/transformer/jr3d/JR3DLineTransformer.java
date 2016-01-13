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

package net.mumie.mathletfactory.transformer.jr3d;


import net.mumie.mathletfactory.display.jr3d.JR3DDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DLineSetDrawable;
import net.mumie.mathletfactory.display.jr3d.MMJR3DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLine;


/**
 * @author jweber
 * 
 */
public class JR3DLineTransformer extends JR3DCanvasTransformer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#initialize(net.mumie.mathletfactory.mmobject.MMObjectIF)
	 * @override
	 */
//	@Override
	public void initialize( MMObjectIF masterObject ) {
		super.initialize( masterObject );

		this.m_allDrawables = new JR3DDrawable[] { new JR3DLineSetDrawable( masterObject.getLabel() ) };
		this.m_activeDrawable = this.m_allDrawables[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#synchronizeMath2Screen()
	 * @override
	 */
//	@Override
	public void synchronizeMath2Screen() {
		MMJR3DCanvas canvas = ( MMJR3DCanvas ) this.getCanvas();
		NumberTuple[] coords = ( ( MMAffine3DLine ) this.m_masterMMObject ).getAffineCoordinates();

		double[][] points = new double[2][];
		points[0] = new double[] { coords[0].getEntry( 1, 1 ).getDouble(), coords[0].getEntry( 1, 2 ).getDouble(), coords[0].getEntry( 1, 3 ).getDouble() };
		points[1] = new double[] { coords[1].getEntry( 1, 1 ).getDouble(), coords[1].getEntry( 1, 2 ).getDouble(), coords[1].getEntry( 1, 3 ).getDouble() };

		double[] direction = new double[] { points[1][0] - points[0][0], points[1][1] - points[0][1], points[1][2] - points[0][2] };

		// ( ( JR3DLineSetDrawable ) this.m_activeDrawable ).setLines( canvas.getPointOfView( points[0], direction ), new int[][] { { 0, 1 } } );
		( ( JR3DLineSetDrawable ) this.m_activeDrawable ).setLines( new double[][] { { 1.0, 1.0, -100.0 }, { 1.0, 1.0, 100.0 } }, new int[][] { { 0, 1 } } );
		renderLabel();
	}
}
