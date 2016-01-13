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


import net.mumie.mathletfactory.display.jr3d.JR3DCompositeDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DFaceSetDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DLineSetDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DPointSetDrawable;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DSubspace;


/**
 * @author jweber
 * 
 */
public class JR3DSubspaceTransformer extends JR3DCanvasTransformer {

	public void initialize( MMObjectIF masterObject ) {
		super.initialize( masterObject );
		this.m_allDrawables = new JR3DDrawable[] { new JR3DPointSetDrawable( "point" ), new JR3DLineSetDrawable( "line" ), new JR3DFaceSetDrawable( "plane" ) };
		this.m_activeDrawable = new JR3DCompositeDrawable( this.m_masterMMObject.getLabel(), (net.mumie.mathletfactory.display.jr3d.JR3DDrawable[] ) this.m_allDrawables );
	}

	public void synchronizeMath2Screen() {
		MMAffine3DSubspace master = ( MMAffine3DSubspace ) this.getMaster();

		double[][] values = null;

		switch ( master.getDimension() ) {
		case 0:
			NumberTuple[] coords = master.getAffineCoordinates();

			double[] vertice = new double[3];
			for ( int i = 0; i < 3; i++ ) {
				vertice[i] = coords[0].getEntry( 1, i ).getDouble();
			}

			( ( JR3DPointSetDrawable ) this.m_allDrawables[0] ).setPoints( new double[][] { vertice } );
			break;
		case 1:

			( ( JR3DLineSetDrawable ) this.m_allDrawables[1] ).setLines( values, null );
			break;

		case 2:
			JR3DFaceSetDrawable.createSurface( ( JR3DFaceSetDrawable ) this.m_allDrawables[2], null, 0, 0 );
			break;

		default:
		}
		renderLabel();
	}
}
