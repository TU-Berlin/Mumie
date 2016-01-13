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
import net.mumie.mathletfactory.display.jr3d.JR3DFaceSetDrawable;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPolygon;


/**
 * @author jweber
 * 
 */
public class JR3DPolygonTransformer extends JR3DCanvasTransformer {

	public void initialize( MMObjectIF masterObject ) {
		super.initialize( masterObject );
		this.m_allDrawables = new JR3DDrawable[] { new JR3DFaceSetDrawable( masterObject.getLabel() ) };
		this.m_activeDrawable = this.m_allDrawables[0];
	}

	public void synchronizeMath2Screen() {
		NumberTuple[] vertices = ( ( MMAffine3DPolygon ) this.m_masterMMObject ).getCoords();

		int[][] faceIndices = new int[1][vertices.length];
		for ( int i = 0; i < vertices.length; i++ ) {
			faceIndices[0][i] = i;
		}

		( ( JR3DFaceSetDrawable ) this.m_activeDrawable ).setFaces( vertices, faceIndices );
		( ( JR3DFaceSetDrawable ) this.m_activeDrawable ).setSelected( this.getMaster().isSelected() );
		renderLabel();
	}
}
